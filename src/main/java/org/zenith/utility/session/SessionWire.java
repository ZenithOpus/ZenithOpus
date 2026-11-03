package org.zenith.utility.session;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.state.NetworkState;

/** Minecraft framing, including the vanilla compression negotiated during login. */
public final class SessionWire {
    public static final int MAX_FRAME = 8 * 1024 * 1024;
    public static final int COMPRESSION_THRESHOLD = 256;

    private SessionWire() {}

    public static int readVarInt(DataInputStream input) throws IOException {
        int value = 0;
        for (int i = 0; i < 5; i++) {
            int next = input.readUnsignedByte();
            value |= (next & 127) << (7 * i);
            if ((next & 128) == 0) {
                if (i == 4 && (next & 0xf0) != 0) throw new IOException("Invalid VarInt");
                return value;
            }
        }
        throw new IOException("VarInt too long");
    }

    public static void writeVarInt(DataOutputStream output, int value) throws IOException {
        do {
            int next = value & 127;
            value >>>= 7;
            output.writeByte(value == 0 ? next : next | 128);
        } while (value != 0);
    }

    public static byte[] readFrame(DataInputStream input) throws IOException {
        int length = readVarInt(input);
        if (length <= 0 || length > MAX_FRAME) throw new IOException("Invalid packet length");
        byte[] data = input.readNBytes(length);
        if (data.length != length) throw new EOFException();
        return data;
    }

    public static void writeFrame(DataOutputStream output, byte[] data) throws IOException {
        if (data.length == 0 || data.length > MAX_FRAME) throw new IOException("Packet too large");
        writeVarInt(output, data.length);
        output.write(data);
    }

    /** Reader-thread owned; never inflate packets on the render thread. */
    static final class CompressedInput implements AutoCloseable {
        private final DataInputStream input;
        private final Inflater inflater = new Inflater();

        CompressedInput(DataInputStream input) { this.input = input; }

        byte[] readFrame() throws IOException {
            var frame = new DataInputStream(new ByteArrayInputStream(SessionWire.readFrame(input)));
            int length = readVarInt(frame);
            byte[] payload = frame.readAllBytes();
            if (length == 0) {
                if (payload.length == 0) throw new IOException("Empty packet");
                return payload;
            }
            if (length < COMPRESSION_THRESHOLD || length > MAX_FRAME) throw new IOException("Invalid inflated packet length");
            try {
                inflater.setInput(payload);
                byte[] result = new byte[length];
                int offset = 0;
                while (offset < length) {
                    int count = inflater.inflate(result, offset, length - offset);
                    if (count == 0) throw new IOException("Incomplete compressed packet");
                    offset += count;
                }
                // Consume a possible zlib trailer, but reject output exceeding the declared size.
                if (inflater.inflate(new byte[1]) != 0 || !inflater.finished() || inflater.getRemaining() != 0)
                    throw new IOException("Invalid compressed packet size");
                return result;
            } catch (DataFormatException e) {
                throw new IOException("Invalid compressed packet", e);
            } finally { inflater.reset(); }
        }

        @Override public void close() { inflater.end(); }
    }

    /** Writer-thread owned. BEST_SPEED keeps chunk traffic small without delaying small updates. */
    static final class CompressedOutput implements AutoCloseable {
        private final DataOutputStream output;
        private final Deflater deflater = new Deflater(Deflater.BEST_SPEED);
        private final byte[] buffer = new byte[8192];

        CompressedOutput(DataOutputStream output) { this.output = output; }

        void writeFrame(byte[] data) throws IOException {
            if (data.length == 0 || data.length > MAX_FRAME) throw new IOException("Packet too large");
            if (data.length < COMPRESSION_THRESHOLD) {
                writeVarInt(output, data.length + 1);
                writeVarInt(output, 0);
                output.write(data);
                return;
            }
            var bytes = new ByteArrayOutputStream();
            var frame = new DataOutputStream(bytes);
            writeVarInt(frame, data.length);
            try {
                deflater.setInput(data);
                deflater.finish();
                while (!deflater.finished()) bytes.write(buffer, 0, deflater.deflate(buffer));
                SessionWire.writeFrame(output, bytes.toByteArray());
            } finally { deflater.reset(); }
        }

        @Override public void close() { deflater.end(); }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static byte[] encode(NetworkState state, Packet<?> packet) {
        ByteBuf buffer = Unpooled.buffer();
        try {
            state.codec().encode(buffer, packet);
            byte[] data = new byte[buffer.readableBytes()];
            buffer.readBytes(data);
            return data;
        } finally {
            buffer.release();
        }
    }

    static Packet<?> decode(NetworkState<?> state, byte[] data) throws IOException {
        ByteBuf buffer = Unpooled.wrappedBuffer(data);
        try {
            Packet<?> packet = state.codec().decode(buffer);
            if (buffer.isReadable()) throw new IOException("Trailing packet data");
            return packet;
        } catch (RuntimeException e) {
            throw new IOException("Invalid Minecraft packet", e);
        } finally {
            buffer.release();
        }
    }
}

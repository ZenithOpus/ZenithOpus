package org.zenith.utility.session;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.network.handler.PacketDeflater;
import net.minecraft.network.handler.PacketInflater;

/** Cross-check the session transport against Minecraft's own compression handlers. */
final class SessionTransportTest {
    static void run() throws Exception {
        byte[] chunk = new byte[600_000];
        byte[] random = new byte[140_000];
        new Random(92).nextBytes(random);
        for (int i = 0; i < chunk.length; i++) chunk[i] = (byte)(i % 32);
        byte[][] packets = {new byte[]{0, 1, 2}, chunk, random, new byte[]{7}, chunk};
        var encoded = new ByteArrayOutputStream();
        try (var compressed = new SessionWire.CompressedOutput(new DataOutputStream(encoded))) {
            for (byte[] packet : packets) compressed.writeFrame(packet);
        }
        require(encoded.size() < 160_000, "Chunk traffic must be compressed");
        try (var compressed = new SessionWire.CompressedInput(input(encoded.toByteArray()))) {
            for (byte[] packet : packets) require(Arrays.equals(packet, compressed.readFrame()), "Reusable compression round trip");
        }
        var vanillaInflater = new EmbeddedChannel(new PacketInflater(SessionWire.COMPRESSION_THRESHOLD, true));
        var frames = input(encoded.toByteArray());
        try {
            for (byte[] packet : packets) {
                vanillaInflater.writeInbound(Unpooled.wrappedBuffer(SessionWire.readFrame(frames)));
                ByteBuf decoded = vanillaInflater.readInbound();
                try {
                    byte[] actual = new byte[decoded.readableBytes()];
                    decoded.readBytes(actual);
                    require(Arrays.equals(actual, packet), "Vanilla peer decodes session compression");
                } finally { decoded.release(); }
            }
        } finally { vanillaInflater.finishAndReleaseAll(); }
        var vanillaDeflater = new EmbeddedChannel(new PacketDeflater(SessionWire.COMPRESSION_THRESHOLD));
        var vanillaBytes = new ByteArrayOutputStream();
        try {
            for (byte[] packet : packets) {
                vanillaDeflater.writeOutbound(Unpooled.wrappedBuffer(packet));
                ByteBuf frame = vanillaDeflater.readOutbound();
                try {
                    byte[] data = new byte[frame.readableBytes()];
                    frame.readBytes(data);
                    SessionWire.writeFrame(new DataOutputStream(vanillaBytes), data);
                } finally { frame.release(); }
            }
        } finally { vanillaDeflater.finishAndReleaseAll(); }
        try (var compressed = new SessionWire.CompressedInput(input(vanillaBytes.toByteArray()))) {
            for (byte[] packet : packets) require(Arrays.equals(packet, compressed.readFrame()), "Session decodes vanilla compression");
        }
        expectInvalid(new byte[]{0});
        expectInvalid(new byte[]{(byte)0x80, (byte)0x80, (byte)0x80, 8});
        expectInvalid(new byte[]{(byte)0x80, 2, 1, 2, 3});
        byte[] compressedChunk;
        var onlyChunk = new ByteArrayOutputStream();
        try (var compressed = new SessionWire.CompressedOutput(new DataOutputStream(onlyChunk))) { compressed.writeFrame(chunk); }
        compressedChunk = SessionWire.readFrame(input(onlyChunk.toByteArray()));
        expectInvalid(Arrays.copyOf(compressedChunk, compressedChunk.length - 1));
        expectInvalid(Arrays.copyOf(compressedChunk, compressedChunk.length + 1));

        var guest = new SessionGuest(null, new Socket());
        try {
            guest.awaitTeleport(-73);
            require(!guest.canControl(), "Old movement is blocked during handoff");
            guest.confirmTeleport(-72);
            require(!guest.canControl(), "Stale teleport acknowledgement cannot unblock movement");
            guest.confirmTeleport(-73);
            require(guest.canControl(), "Movement resumes after the matching position acknowledgement");
        } finally { guest.close(); }
        System.out.println("Session transport: vanilla compression compatibility, malformed input and control handoff passed");
    }

    private static DataInputStream input(byte[] data) { return new DataInputStream(new ByteArrayInputStream(data)); }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
    private static void expectInvalid(byte[] payload) throws Exception {
        var bytes = new ByteArrayOutputStream();
        SessionWire.writeFrame(new DataOutputStream(bytes), payload);
        try (var compressed = new SessionWire.CompressedInput(input(bytes.toByteArray()))) {
            try { compressed.readFrame(); } catch (IOException expected) { return; }
            throw new AssertionError("Expected malformed compressed frame to be rejected");
        }
    }
}

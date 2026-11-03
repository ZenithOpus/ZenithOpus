package org.zenith.utility.session;

import com.google.gson.JsonObject;
import io.netty.buffer.Unpooled;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.SharedConstants;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;
import net.minecraft.network.packet.c2s.login.EnterConfigurationC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.network.state.*;
import net.minecraft.text.Text;

/** One vanilla client. Only its reader/writer threads touch the socket. */
final class SessionGuest implements AutoCloseable {
    private static final int MAX_QUEUED_BYTES = 64 * 1024 * 1024;
    private final SharedSession session;
    private final Socket socket;
    private final ArrayBlockingQueue<byte[]> output = new ArrayBlockingQueue<>(16384);
    private final AtomicInteger queuedBytes = new AtomicInteger();
    private NetworkState<?> inbound;
    private NetworkState<?> outbound;
    volatile boolean open = true;
    volatile boolean playing;
    volatile long lastRead = System.nanoTime();
    private long lastKeepAlive;
    private long keepAliveId;
    private int failedCommands;
    private int rateCount;
    private long rateWindow = System.nanoTime();
    private final AtomicInteger pending = new AtomicInteger();
    private String name = "Minecraft";
    final SessionView view = new SessionView();
    private Integer pendingTeleport;

    SessionGuest(SharedSession session, Socket socket) {
        this.session = session;
        this.socket = socket;
    }

    void run() {
        try (socket) {
            socket.setSoTimeout(15000);
            socket.setTcpNoDelay(true);
            DataInputStream input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream direct = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            PacketByteBuf handshake = new PacketByteBuf(Unpooled.wrappedBuffer(SessionWire.readFrame(input)));
            int version;
            int intention;
            try {
                if (handshake.readVarInt() != 0) throw new IOException("Expected handshake");
                version = handshake.readVarInt();
                handshake.readString(255);
                handshake.readUnsignedShort();
                intention = handshake.readVarInt();
                if (handshake.isReadable()) throw new IOException("Invalid handshake");
            } finally {
                handshake.release();
            }
            if (intention == 1) {
                status(input, direct);
                return;
            }
            if (intention != 2) return;
            outbound = LoginStates.S2C;
            if (version != SharedConstants.getProtocolVersion() || !session.isActive()) {
                SessionWire.writeFrame(direct, SessionWire.encode(outbound,
                    new LoginDisconnectS2CPacket(Text.literal("ZenithOpus: нужен Minecraft 1.21.11 и активная сессия"))));
                direct.flush();
                return;
            }
            Packet<?> hello = SessionWire.decode(LoginStates.C2S, SessionWire.readFrame(input));
            if (!(hello instanceof LoginHelloC2SPacket login)) return;
            name = login.name();
            SessionWire.writeFrame(direct, SessionWire.encode(outbound, new LoginCompressionS2CPacket(SessionWire.COMPRESSION_THRESHOLD)));
            direct.flush();
            try (var compressedInput = new SessionWire.CompressedInput(input)) {
                try (var compressedOutput = new SessionWire.CompressedOutput(direct)) {
                    compressedOutput.writeFrame(SessionWire.encode(outbound, new LoginSuccessS2CPacket(session.profile())));
                    direct.flush();
                    if (!(SessionWire.decode(LoginStates.C2S, compressedInput.readFrame()) instanceof EnterConfigurationC2SPacket)) return;
                    inbound = ConfigurationStates.C2S;
                    outbound = ConfigurationStates.S2C;
                    List<byte[]> configuration = session.configuration();
                    if (configuration.isEmpty()) return;
                    for (byte[] packet : configuration) compressedOutput.writeFrame(packet);
                    direct.flush();
                    // The recorded configuration ends with Finish Configuration (ReadyS2CPacket).
                    while (!(SessionWire.decode(inbound, compressedInput.readFrame()) instanceof ReadyC2SPacket)) {
                        lastRead = System.nanoTime();
                    }
                }
                inbound = session.playInbound();
                outbound = session.playOutbound();
                socket.setSoTimeout(45000);
                Thread writer = new Thread(() -> writeLoop(direct), "ZenithOpus-session-writer");
                writer.setDaemon(true);
                writer.start();
                session.client().execute(() -> session.join(this));
                while (open) {
                    Packet<?> packet = SessionWire.decode(inbound, compressedInput.readFrame());
                    lastRead = System.nanoTime();
                    if (lastRead - rateWindow > 1_000_000_000L) { rateWindow = lastRead; rateCount = 0; }
                    if (++rateCount > 500) throw new IOException("Too many session packets");
                    session.receive(this, packet);
                }
            }
        } catch (IOException | RuntimeException e) {
            if (open) SharedSession.LOGGER.debug("Session guest {} disconnected: {}", name, e.toString());
        } finally {
            close();
            session.client().execute(() -> session.left(this));
        }
    }

    private void status(DataInputStream input, DataOutputStream out) throws IOException {
        byte[] request = SessionWire.readFrame(input);
        if (request.length != 1 || request[0] != 0) return;
        JsonObject root = new JsonObject();
        JsonObject version = new JsonObject();
        version.addProperty("name", "1.21.11");
        version.addProperty("protocol", SharedConstants.getProtocolVersion());
        root.add("version", version);
        JsonObject players = new JsonObject();
        players.addProperty("max", 1);
        players.addProperty("online", session.guestCount());
        root.add("players", players);
        JsonObject description = new JsonObject();
        description.addProperty("text", "ZenithOpus · Общая сессия");
        root.add("description", description);
        root.addProperty("enforcesSecureChat", false);
        PacketByteBuf response = new PacketByteBuf(Unpooled.buffer());
        try {
            response.writeVarInt(0);
            response.writeString(root.toString());
            byte[] bytes = new byte[response.readableBytes()];
            response.readBytes(bytes);
            SessionWire.writeFrame(out, bytes);
            out.flush();
            byte[] ping = SessionWire.readFrame(input);
            if (ping.length == 9 && ping[0] == 1) {
                SessionWire.writeFrame(out, ping);
                out.flush();
            }
        } finally {
            response.release();
        }
    }

    void send(Packet<?> packet) {
        if (open) sendRaw(SessionWire.encode(outbound, packet));
    }

    void sendRaw(byte[] data) {
        if (!open) return;
        if (queuedBytes.addAndGet(data.length) > MAX_QUEUED_BYTES || !output.offer(data)) close();
    }

    private void writeLoop(DataOutputStream out) {
        try (var compressed = new SessionWire.CompressedOutput(out)) {
            long lastFlush = System.nanoTime();
            int batchBytes = 0;
            while (open) {
                byte[] data = output.poll(1, java.util.concurrent.TimeUnit.SECONDS);
                if (data != null) {
                    queuedBytes.addAndGet(-data.length);
                    compressed.writeFrame(data);
                    batchBytes += data.length;
                    long now = System.nanoTime();
                    if (output.isEmpty() || batchBytes >= 65536 || now - lastFlush >= 2_000_000L) {
                        out.flush();
                        batchBytes = 0;
                        lastFlush = now;
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            close();
        }
    }

    void tick() {
        long now = System.nanoTime();
        if (now - lastRead > 45_000_000_000L) close();
        if (playing && now - lastKeepAlive > 10_000_000_000L) {
            lastKeepAlive = now;
            keepAliveId = now;
            send(new KeepAliveS2CPacket(keepAliveId));
        }
    }

    boolean wrongCommand() { return ++failedCommands >= 5; }
    boolean reserveTask() { if (pending.incrementAndGet() <= 1000) return true; close(); return false; }
    void completeTask() { pending.decrementAndGet(); }
    String name() { return name; }
    void awaitTeleport(int id) { pendingTeleport = id; }
    void confirmTeleport(int id) { if (pendingTeleport != null && pendingTeleport == id) pendingTeleport = null; }
    boolean canControl() { return pendingTeleport == null; }

    @Override
    public void close() {
        open = false;
        output.clear();
        try { socket.close(); } catch (IOException ignored) {}
    }
}

package org.zenith.utility.session;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.*;
import net.minecraft.SharedConstants;

/** Real relay test using a disposable local Minecraft status endpoint, without touching the game UI. */
public final class PublicTunnelSmoke {
    public static void main(String[] args) throws Exception {
        SharedConstants.createGameVersion();
        String marker = "ZenithOpus-tunnel-test-" + UUID.randomUUID();
        try (ServerSocket server = new ServerSocket(0, 4, InetAddress.getByName("127.0.0.1"));
             PublicSessionTunnel tunnel = new PublicSessionTunnel(server.getLocalPort())) {
            Thread responder = Thread.ofVirtual().start(() -> {
                while (!server.isClosed()) {
                    try (Socket socket = server.accept()) { respond(socket, marker); }
                    catch (IOException e) { if (!server.isClosed()) e.printStackTrace(); }
                }
            });
            String address = tunnel.start().get(35, TimeUnit.SECONDS);
            System.out.println("Assigned public relay address: " + address);
            probe(address, marker);
            System.out.println("Public Minecraft status request and ping returned through the relay");
            // Covers the connection timeout firing after a successful assignment.
            Thread.sleep(31000);
            if (tunnel.state() != PublicSessionTunnel.State.READY) throw new AssertionError("Ready tunnel expired: " + tunnel.failure());
            probe(address, marker);
            System.out.println("Public tunnel remained usable beyond the startup timeout");
            tunnel.close();
            if (tunnel.state() != PublicSessionTunnel.State.CLOSED) throw new AssertionError("Tunnel did not close");
            System.out.println("Public tunnel smoke test passed; relay and test endpoint closed");
        }
    }

    private static void respond(Socket socket, String marker) throws IOException {
        socket.setSoTimeout(10000);
        var input = new DataInputStream(socket.getInputStream());
        var output = new DataOutputStream(socket.getOutputStream());
        SessionWire.readFrame(input); // Handshake, relayed unchanged.
        if (SessionWire.readFrame(input)[0] != 0) throw new IOException("Expected status request");
        JsonObject root = new JsonObject();
        JsonObject version = new JsonObject();
        version.addProperty("name", "1.21.11");
        version.addProperty("protocol", SharedConstants.getProtocolVersion());
        root.add("version", version);
        JsonObject description = new JsonObject();
        description.addProperty("text", marker);
        root.add("description", description);
        JsonObject players = new JsonObject();
        players.addProperty("max", 1); players.addProperty("online", 0); root.add("players", players);
        ByteArrayOutputStream packet = new ByteArrayOutputStream();
        var data = new DataOutputStream(packet);
        SessionWire.writeVarInt(data, 0);
        writeString(data, root.toString());
        SessionWire.writeFrame(output, packet.toByteArray());
        output.flush();
        SessionWire.writeFrame(output, SessionWire.readFrame(input));
        output.flush();
    }

    private static void probe(String address, String marker) throws Exception {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(address, 25565), 10000);
            socket.setSoTimeout(12000);
            var input = new DataInputStream(socket.getInputStream());
            var output = new DataOutputStream(socket.getOutputStream());
            ByteArrayOutputStream handshake = new ByteArrayOutputStream();
            var data = new DataOutputStream(handshake);
            SessionWire.writeVarInt(data, 0);
            SessionWire.writeVarInt(data, SharedConstants.getProtocolVersion());
            writeString(data, address);
            data.writeShort(25565);
            SessionWire.writeVarInt(data, 1);
            SessionWire.writeFrame(output, handshake.toByteArray());
            SessionWire.writeFrame(output, new byte[]{0});
            output.flush();
            var response = new DataInputStream(new ByteArrayInputStream(SessionWire.readFrame(input)));
            if (SessionWire.readVarInt(response) != 0) throw new AssertionError("Invalid public status reply");
            String json = new String(response.readNBytes(SessionWire.readVarInt(response)), StandardCharsets.UTF_8);
            if (!JsonParser.parseString(json).getAsJsonObject().getAsJsonObject("description").get("text").getAsString().equals(marker)) {
                throw new AssertionError("Public address did not route to the local test endpoint: " + json);
            }
            byte[] ping = new byte[]{1, 12, 23, 34, 45, 56, 67, 78, 89};
            SessionWire.writeFrame(output, ping); output.flush();
            if (!java.util.Arrays.equals(SessionWire.readFrame(input), ping)) throw new AssertionError("Public ping payload changed");
        }
    }

    private static void writeString(DataOutputStream output, String text) throws IOException {
        byte[] encoded = text.getBytes(StandardCharsets.UTF_8);
        SessionWire.writeVarInt(output, encoded.length); output.write(encoded);
    }
}

package org.zenith.utility.session;

import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import java.nio.charset.StandardCharsets;

final class TunnelControlTest {
    static void run() {
        JsonObject assignment = new JsonObject();
        assignment.addProperty("kind", "domain_assignment_complete");
        assignment.addProperty("domain", "test-example.eu.e4mc.link");
        byte[] encoded = assignment.toString().getBytes(StandardCharsets.UTF_8);
        EmbeddedChannel channel = new EmbeddedChannel(new PublicSessionTunnel.ControlCodec());
        try {
            channel.writeInbound(Unpooled.wrappedBuffer(new byte[]{(byte) encoded.length, encoded[0]}));
            require(channel.readInbound() == null, "Fragmented control header must wait");
            channel.writeInbound(Unpooled.wrappedBuffer(encoded, 1, encoded.length - 1));
            require(assignment.equals(channel.readInbound()), "Fragmented assignment must decode");
            JsonObject longMessage = new JsonObject();
            longMessage.addProperty("kind", "request_message_broadcast");
            longMessage.addProperty("message", "x".repeat(150));
            byte[] longData = longMessage.toString().getBytes(StandardCharsets.UTF_8);
            require(longData.length > 127 && longData.length < 256, "Unsigned control length fixture");
            channel.writeInbound(Unpooled.buffer().writeByte(longData.length).writeBytes(longData));
            require(longMessage.equals(channel.readInbound()), "Lengths above 127 must not become negative");
            channel.writeOutbound(longMessage);
            ByteBuf outbound = channel.readOutbound();
            try {
                int first = outbound.readUnsignedByte();
                int second = outbound.readUnsignedByte();
                require(((first & 127) | (second << 7)) == longData.length, "Server-bound lengths use VarInt");
            } finally { outbound.release(); }
        } finally { channel.finishAndReleaseAll(); }
        require(PublicSessionTunnel.validateDomain("TEST.eu.e4mc.link").equals("test.eu.e4mc.link"), "Relay name normalization");
        require(RelayTrustManager.matches("eu.e4mc.link", "*.e4mc.link"), "One-label certificate wildcard");
        require(!RelayTrustManager.matches("deep.eu.e4mc.link", "*.e4mc.link"), "Certificate wildcard cannot span labels");
        require(!RelayTrustManager.matches("evil-e4mc.link", "*.e4mc.link"), "Certificate suffix must be a full DNS label");
        require(RelayTrustManager.matches("eu.e4mc.link", "EU.E4MC.LINK"), "Certificate DNS names are case-insensitive");
        for (String invalid : new String[]{"localhost", "127.0.0.1", "evil.example", "fake.e4mc.link.attacker.example", "test.e4mc.link:1234"}) {
            try { PublicSessionTunnel.validateDomain(invalid); throw new AssertionError("Accepted invalid relay: " + invalid); }
            catch (IllegalArgumentException expected) { }
        }
        try (PublicSessionTunnel cancelled = new PublicSessionTunnel(25565)) {
            cancelled.close();
            require(cancelled.start().isCancelled(), "Closing before startup must not open a relay");
            require(cancelled.state() == PublicSessionTunnel.State.CLOSED, "Cancelled tunnel state");
        }
        System.out.println("Tunnel control: fragmentation, frame lengths, endpoint validation and cancellation passed");
    }

    private static void require(boolean value, String description) { if (!value) throw new AssertionError(description); }
}

package org.zenith.utility.session;

import java.io.*;
import java.util.*;
import net.minecraft.SharedConstants;
import net.minecraft.entity.EntityPosition;
import net.minecraft.network.packet.c2s.login.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.state.*;
import net.minecraft.util.math.Vec3d;
import com.mojang.authlib.GameProfile;

/** Dependency-free protocol checks; run with gradlew sessionProtocolTest. */
public final class SessionProtocolTest {
    public static void main(String[] args) throws Exception {
        SharedConstants.createGameVersion();
        for (int value : new int[]{0, 1, 127, 128, 255, 2097151, Integer.MAX_VALUE, -1}) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            SessionWire.writeVarInt(new DataOutputStream(bytes), value);
            require(SessionWire.readVarInt(input(bytes.toByteArray())) == value, "VarInt " + value);
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        var output = new DataOutputStream(stream);
        byte[] large = new byte[140000];
        new Random(42).nextBytes(large);
        SessionWire.writeFrame(output, new byte[]{1, 2, 3});
        SessionWire.writeFrame(output, large);
        var input = input(stream.toByteArray());
        require(Arrays.equals(SessionWire.readFrame(input), new byte[]{1, 2, 3}), "First frame");
        require(Arrays.equals(SessionWire.readFrame(input), large), "Chunk-sized frame");
        expectIOException(() -> SessionWire.readVarInt(input(new byte[]{-128, -128, -128, -128, -128, 0})));
        expectIOException(() -> SessionWire.readFrame(input(new byte[]{0})));
        expectIOException(() -> SessionWire.readFrame(input(new byte[]{3, 1})));
        expectIOException(() -> SessionWire.readFrame(input(new byte[]{-1, -1, -1, -1, 7})));
        UUID id = UUID.randomUUID();
        var hello = new LoginHelloC2SPacket("VanillaGuest", id);
        require(SessionWire.decode(LoginStates.C2S, SessionWire.encode(LoginStates.C2S, hello)).equals(hello), "Vanilla login hello");
        var profile = new GameProfile(id, "OriginalPlayer");
        var success = new LoginSuccessS2CPacket(profile);
        require(SessionWire.decode(LoginStates.S2C, SessionWire.encode(LoginStates.S2C, success)).equals(success), "Host identity");
        expectIOException(() -> SessionWire.decode(LoginStates.C2S, new byte[]{0, 0}));
        var position = PlayerPositionLookS2CPacket.of(-123,
            new EntityPosition(new Vec3d(10, 80, -50), Vec3d.ZERO, 90, 12), Set.of());
        var buffer = new net.minecraft.network.PacketByteBuf(io.netty.buffer.Unpooled.buffer());
        try {
            PlayerPositionLookS2CPacket.CODEC.encode(buffer, position);
            require(PlayerPositionLookS2CPacket.CODEC.decode(buffer).equals(position), "Spectator position sync");
        } finally { buffer.release(); }
        require(new PlayerMoveC2SPacket.Full(0, 64, 0, 0, 0, true, false).getPacketType().id().getPath().equals("move_player_pos_rot"), "Movement forwarding ID");
        require(new UpdateSelectedSlotC2SPacket(1).getPacketType().id().getPath().equals("set_carried_item"), "Inventory forwarding ID");
        System.out.println("Session protocol: framing, login, movement and inventory checks passed");
        TunnelControlTest.run();
        SessionTransportTest.run();
    }

    private static DataInputStream input(byte[] bytes) { return new DataInputStream(new ByteArrayInputStream(bytes)); }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
    private static void expectIOException(Checked action) throws Exception {
        try { action.run(); } catch (IOException expected) { return; }
        throw new AssertionError("Expected invalid packet to be rejected");
    }
    private interface Checked { void run() throws Exception; }
}

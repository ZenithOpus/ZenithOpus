package org.zenith.utility.session;

import com.mojang.authlib.GameProfile;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.EntityPosition;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.world.GameMode;
import org.zenith.utility.mixin.accessors.SessionPlayerListAccessor;

/** A vanilla spectator camera: remote entities interpolate instead of teleporting the local player. */
final class SessionView {
    private static final int CAMERA_ID = Integer.MIN_VALUE + 128;
    private OtherClientPlayerEntity camera;
    private net.minecraft.util.math.Vec3d bodyPosition;

    boolean isViewing() { return camera != null; }

    void start(SessionGuest guest, MinecraftClient mc) {
        if (camera != null) remove(guest);
        var profile = new GameProfile(UUID.randomUUID(), "ZenithView");
        camera = new OtherClientPlayerEntity(mc.world, profile);
        camera.setId(CAMERA_ID);
        camera.refreshPositionAndAngles(mc.player.getEntityPos(), mc.player.getYaw(), mc.player.getPitch());
        camera.setPose(mc.player.getPose());
        bodyPosition = mc.player.getEntityPos();

        var players = new PlayerListS2CPacket(EnumSet.of(PlayerListS2CPacket.Action.ADD_PLAYER,
            PlayerListS2CPacket.Action.UPDATE_LISTED, PlayerListS2CPacket.Action.UPDATE_GAME_MODE), List.of());
        ((SessionPlayerListAccessor)(Object)players).zenith$setEntries(List.of(new PlayerListS2CPacket.Entry(
            profile.id(), profile, false, 0, GameMode.SPECTATOR, null, false, 0, null)));
        guest.send(players);
        guest.send(new EntitySpawnS2CPacket(camera.getId(), profile.id(), camera.getX(), camera.getY(), camera.getZ(),
            camera.getPitch(), camera.getYaw(), camera.getType(), 0, mc.player.getVelocity(), camera.getYaw()));
        sendPose(guest);
        sendPlayerMode(guest, mc, GameMode.SPECTATOR);
        guest.send(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.GAME_MODE_CHANGED, GameMode.SPECTATOR.getIndex()));
        var abilities = new PlayerAbilities();
        abilities.invulnerable = abilities.allowFlying = abilities.flying = true;
        guest.send(new PlayerAbilitiesS2CPacket(abilities));
        guest.send(new SetCameraEntityS2CPacket(camera));
    }

    void tick(SessionGuest guest, MinecraftClient mc) {
        if (camera == null || mc.player == null) return;
        if (camera.getEntityWorld() != mc.world) {
            start(guest, mc);
            return;
        }
        if (camera.getPose() != mc.player.getPose()) {
            camera.setPose(mc.player.getPose());
            sendPose(guest);
        }
        guest.send(new EntityPositionSyncS2CPacket(camera.getId(), EntityPosition.fromEntity(mc.player), mc.player.isOnGround()));
    }

    private void sendPose(SessionGuest guest) {
        var entries = camera.getDataTracker().getDirtyEntries();
        if (entries != null) guest.send(new EntityTrackerUpdateS2CPacket(camera.getId(), entries));
    }

    boolean needsBodySync(MinecraftClient mc) {
        if (camera == null || bodyPosition.squaredDistanceTo(mc.player.getEntityPos()) < 256) return false;
        bodyPosition = mc.player.getEntityPos();
        return true;
    }

    void stop(SessionGuest guest, MinecraftClient mc) {
        if (camera == null) return;
        guest.send(new SetCameraEntityS2CPacket(mc.player));
        remove(guest);
        sendPlayerMode(guest, mc, mc.interactionManager.getCurrentGameMode());
        guest.send(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.GAME_MODE_CHANGED,
            mc.interactionManager.getCurrentGameMode().getIndex()));
        guest.send(new PlayerAbilitiesS2CPacket(mc.player.getAbilities()));
    }

    private void sendPlayerMode(SessionGuest guest, MinecraftClient mc, GameMode mode) {
        var profile = mc.player.getGameProfile();
        var update = new PlayerListS2CPacket(EnumSet.of(PlayerListS2CPacket.Action.UPDATE_GAME_MODE), List.of());
        ((SessionPlayerListAccessor)(Object)update).zenith$setEntries(List.of(new PlayerListS2CPacket.Entry(
            profile.id(), profile, true, 0, mode, null, true, 0, null)));
        guest.send(update);
    }

    private void remove(SessionGuest guest) {
        guest.send(new EntitiesDestroyS2CPacket(camera.getId()));
        guest.send(new PlayerRemoveS2CPacket(List.of(camera.getUuid())));
        camera = null;
    }

    Packet<?> adapt(Packet<?> packet, MinecraftClient mc) {
        if (!isViewing()) return packet;
        if (packet instanceof PlayerPositionLookS2CPacket || packet instanceof SetCameraEntityS2CPacket
            || packet instanceof PlayerAbilitiesS2CPacket) return null;
        if (packet instanceof GameStateChangeS2CPacket state
            && state.getReason() == GameStateChangeS2CPacket.GAME_MODE_CHANGED) return null;
        if (packet instanceof PlayerListS2CPacket players && players.getActions().contains(PlayerListS2CPacket.Action.UPDATE_GAME_MODE)) {
            var id = mc.player.getUuid();
            if (players.getEntries().stream().anyMatch(entry -> entry.profileId().equals(id))) {
                var update = new PlayerListS2CPacket(EnumSet.copyOf(players.getActions()), List.of());
                ((SessionPlayerListAccessor)(Object)update).zenith$setEntries(players.getEntries().stream().map(entry ->
                    !entry.profileId().equals(id) ? entry : new PlayerListS2CPacket.Entry(entry.profileId(), entry.profile(),
                        entry.listed(), entry.latency(), GameMode.SPECTATOR, entry.displayName(), entry.showHat(), entry.listOrder(), entry.chatSession())).toList());
                return update;
            }
        }
        return packet;
    }
}

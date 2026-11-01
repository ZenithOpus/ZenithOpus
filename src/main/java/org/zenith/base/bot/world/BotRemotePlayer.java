package org.zenith.base.bot.world;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.zenith.base.bot.net.BotPlayHandler;

public class BotRemotePlayer extends PlayerEntity {
   private final BotPlayHandler networkHandler;
   private Vec3d clientVelocity = Vec3d.ZERO;
   private int velocityLerpDivisor;

   public BotRemotePlayer(BotWorld var1, BotPlayHandler var2, GameProfile var3) {
      super(var1, var3);
      this.networkHandler = var2;
      this.noClip = true;
   }

   @Nullable
   private GameMode getListedGameMode() {
      PlayerListEntry var1 = this.networkHandler.getPlayerListEntry(this.getUuid());
      return var1 == null ? null : var1.getGameMode();
   }

   @Override
   public boolean isSpectator() {
      return this.getListedGameMode() == GameMode.SPECTATOR;
   }

   @Override
   public boolean isCreative() {
      return this.getListedGameMode() == GameMode.CREATIVE;
   }

   @Override
   public boolean clientDamage(DamageSource source) {
      return true;
   }

   @Override
   public void tick() {
      super.tick();
      this.updateLimbs(false);
   }

   @Override
   public void tickMovement() {
      if (this.isInterpolating()) {
         this.getInterpolator().tick();
      }

      if (this.headTrackingIncrements > 0) {
         this.lerpHeadYaw(this.headTrackingIncrements, this.serverHeadYaw);
         this.headTrackingIncrements--;
      }

      if (this.velocityLerpDivisor > 0) {
         this.addVelocityInternal(
            new Vec3d(
               (this.clientVelocity.x - this.getVelocity().x) / this.velocityLerpDivisor,
               (this.clientVelocity.y - this.getVelocity().y) / this.velocityLerpDivisor,
               (this.clientVelocity.z - this.getVelocity().z) / this.velocityLerpDivisor
            )
         );
         this.velocityLerpDivisor--;
      }

      this.tickHandSwing();
      this.tickCramming();
   }

   @Override
   public void setVelocityClient(Vec3d clientVelocity) {
      this.clientVelocity = clientVelocity;
      this.velocityLerpDivisor = this.getType().getTrackTickInterval() + 1;
   }

   @Override
   public void updatePose() {
   }

   @Override
   public void onSpawnPacket(EntitySpawnS2CPacket packet) {
      super.onSpawnPacket(packet);
      this.resetPosition();
   }

   @Override
   public GameMode getGameMode() {
      GameMode var1 = this.getListedGameMode();
      return var1 != null ? var1 : GameMode.SURVIVAL;
   }
}

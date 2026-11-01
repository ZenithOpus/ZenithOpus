package org.zenith.utility.game;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

public class Npc extends OtherClientPlayerEntity {
   public Npc(ClientWorld var1, GameProfile var2) {
      super(var1, var2);
   }

   @Override
   public boolean handleAttack(Entity attacker) {
      this.animateDamage(this.method7023(attacker));
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2.player != null) {
         this.getEntityWorld()
            .playSound(
               var2.player,
               this.getX(),
               this.getY(),
               this.getZ(),
               SoundEvents.ENTITY_PLAYER_HURT,
               this.getSoundCategory(),
               1.0F,
               1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
            );
      }

      return false;
   }

   private float method7023(Entity var1) {
      if (var1 == null) {
         return 0.0F;
      } else {
         double var2 = var1.getX() - this.getX();
         double var4 = var1.getZ() - this.getZ();
         return (float)(MathHelper.atan2(var4, var2) * 180.0 / Math.PI - this.getYaw());
      }
   }

   @Override
   public void takeKnockback(double strength, double x, double z) {
   }

   @Override
   public void setVelocityClient(Vec3d clientVelocity) {
   }

   @Override
   public boolean isPushable() {
      return false;
   }

   @Override
   public boolean isImmuneToExplosion(Explosion explosion) {
      return true;
   }
}

package org.zenith.client.screens.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public class ImplOtherClientPlayerEntity extends OtherClientPlayerEntity {
   public ImplOtherClientPlayerEntity(ClientWorld var1, GameProfile var2) {
      super(var1, var2);
   }

   @Override
   public boolean isAttackable() {
      return false;
   }

   @Override
   public boolean canHit() {
      return false;
   }

   @Override
   public boolean isPushable() {
      return false;
   }

   @Override
   public void pushAwayFrom(Entity entity) {
   }

   @Override
   public void pushAway(Entity entity) {
   }

   @Override
   public boolean canBeHitByProjectile() {
      return false;
   }
}

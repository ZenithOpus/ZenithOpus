package org.zenith.utility.mixin.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.zenith.utility.misc.Handler6;
import org.zenith.utility.misc.Handler8;

@Mixin({OtherClientPlayerEntity.class})
public class MixinOtherClientPlayerEntity extends AbstractClientPlayerEntity implements Handler8 {
   @Unique
   private double backUpX;
   @Unique
   private double backUpY;
   @Unique
   private double backUpZ;

   public MixinOtherClientPlayerEntity(ClientWorld var1, GameProfile var2) {
      super(var1, var2);
   }

   @Unique
   @Override
   public void zenithDLC$resolve() {
      this.backUpX = this.getX();
      this.backUpY = this.getY();
      this.backUpZ = this.getZ();
      Vec3d var1 = new Vec3d(
         ((Handler6)(Object)this).zenithDLC$getPrevServerX(), ((Handler6)(Object)this).zenithDLC$getPrevServerY(), ((Handler6)(Object)this).zenithDLC$getPrevServerZ()
      );
      Vec3d var2 = this.getEntityPos();
      if (MinecraftClient.getInstance().player.squaredDistanceTo(var1) > MinecraftClient.getInstance().player.squaredDistanceTo(var2)) {
         this.setPosition(var2.x, var2.y, var2.z);
      } else {
         this.setPosition(var1.x, var1.y, var1.z);
      }
   }

   @Unique
   @Override
   public void zenithDLC$releaseResolver() {
      if (this.backUpY != -999.0) {
         this.setPosition(this.backUpX, this.backUpY, this.backUpZ);
         this.backUpY = -999.0;
      }
   }
}

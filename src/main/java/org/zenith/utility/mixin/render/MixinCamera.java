package org.zenith.utility.mixin.render;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.Rotation;
import org.zenith.client.events.CameraPosEvent;
import org.zenith.client.events.CameraUpdateEvent;
import org.zenith.client.modules.impl.movement.SpeedModule;

@Mixin({Camera.class})
public abstract class MixinCamera {
   @Shadow
   private Vec3d pos;
   @Shadow
   private Entity focusedEntity;
   @Shadow
   @Final
   private Mutable blockPos;
   @Shadow
   private float yaw;
   @Shadow
   private float pitch;
   @Shadow
   private float cameraY;
   @Shadow
   private float lastCameraY;

   @Shadow
   public abstract void setRotation(float var1, float var2);

   @Shadow
   public abstract void moveBy(float var1, float var2, float var3);

   @Shadow
   public abstract float clipToSpace(float var1);

   @Inject(
      method = {"updateEyeHeight()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$keepStandingCameraHeight(CallbackInfo var1) {
      if (this.focusedEntity instanceof ClientPlayerEntity var2
         && var2 == MinecraftClient.getInstance().player
         && SpeedModule.INSTANCE.isEnabled()
         && SpeedModule.INSTANCE.method6606()) {
         float var4 = var2.getEyeHeight(EntityPose.STANDING);
         this.cameraY = var4;
         this.lastCameraY = var4;
         var1.cancel();
      }
   }

   @Inject(
      method = {"update(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;ZZF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V",
         shift = Shift.AFTER
      )},
      cancellable = true
   )
   private void updateHook(World var1, Entity var2, boolean var3, boolean var4, float var5, CallbackInfo var6) {
      CameraUpdateEvent var7 = new CameraUpdateEvent(false, 4.0F, new Rotation(this.yaw, this.pitch));
      EventManager.call(var7);
      Rotation var8 = var7.getRotation();
      if (var7.isCancelled() && var2 instanceof ClientPlayerEntity var9 && !var9.isSleeping() && var3) {
         float var10 = var4 ? -var8.getFloatValue2() : var8.getFloatValue2();
         float var11 = var8.getFloatValue() - (var4 ? 180 : 0);
         float var12 = var7.getFloatValue();
         this.setRotation(var11, var10);
         this.moveBy(var7.isFlag() ? -var12 : -this.clipToSpace(var12), 0.0F, 0.0F);
         var6.cancel();
      }
   }

   @Inject(
      method = {"setPos(Lnet/minecraft/util/math/Vec3d;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void posHook(Vec3d var1, CallbackInfo var2) {
      CameraPosEvent var3 = new CameraPosEvent(var1);
      EventManager.call(var3);
      this.pos = var1 = var3.getVec3d();
      this.blockPos.set(var1.x, var1.y, var1.z);
      var2.cancel();
   }
}

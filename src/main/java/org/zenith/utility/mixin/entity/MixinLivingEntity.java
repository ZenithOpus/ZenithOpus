package org.zenith.utility.mixin.entity;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.MinecraftAccess;
import org.zenith.Rotation;
import org.zenith.RotationManager;
import org.zenith.Zenith;
import org.zenith.client.events.EntityDeathEvent;
import org.zenith.client.events.EntityPushEvent;
import org.zenith.client.events.JumpEvent;
import org.zenith.client.modules.impl.movement.ElytraBoosterModule;
import org.zenith.client.modules.impl.movement.ElytraTargetModule;
import org.zenith.client.modules.impl.movement.GrimGlideModule;
import org.zenith.client.modules.impl.render.SwingAnimationModule;
import org.zenith.utility.misc.Handler6;

@Mixin({LivingEntity.class})
public class MixinLivingEntity implements MinecraftAccess, Handler6 {
   @Unique
   public float safeYaw = 0.0F;
   @Unique
   public float safePitch = 0.0F;
   @Unique
   private double serverX;
   @Unique
   private double serverY;
   @Unique
   private double serverZ;
   @Unique
   public double prevServerX;
   @Unique
   public double prevServerY;
   @Unique
   public double prevServerZ;
   @Unique
   private static final long SERVER_DELTA_MAX_AGE = 4L;
   @Unique
   private static final double SERVER_DELTA_MAX_SQ = 100.0;
   @Unique
   private boolean serverPosTracked;
   @Unique
   private long serverPosTime;

   @Inject(
      method = {"jump()V"},
      at = {@At("RETURN")}
   )
   public void replaceMovePacketPitche3(CallbackInfo var1) {
      if ((Object)this == mc.player) {
         EventManager.call(new JumpEvent());
      }
   }

   @ModifyVariable(
      method = {"jump()V"},
      at = @At("STORE"),
      ordinal = 1
   )
   private float hookJumpSprintRadians(float var1) {
      if ((Object)this != mc.player) {
         return var1;
      } else {
         Rotation var2 = Zenith.getInstance().getRotationManager().getCurrentRotation();
         return var2 == null ? var1 : var2.getFloatValue() * (float) (Math.PI / 180.0);
      }
   }

   @Inject(
      method = {"tickMovement()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/LivingEntity;travel(Lnet/minecraft/util/math/Vec3d;)V",
         shift = Shift.AFTER
      )}
   )
   public void gownso(CallbackInfo var1) {
      if ((Object)this == mc.player && Zenith.getInstance().getRotationManager().getCurrentRotation() != null) {
         mc.player.setYaw(this.safeYaw);
         mc.player.setPitch(this.safePitch);
      }
   }

   @Inject(
      method = {"tickMovement()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/LivingEntity;travel(Lnet/minecraft/util/math/Vec3d;)V"
      )}
   )
   public void replaceMo(CallbackInfo var1) {
      if ((Object)this == mc.player) {
         mc.player.setVelocity(ElytraBoosterModule.INSTANCE.method1769(mc.player.getVelocity()));
         if (Zenith.getInstance().getRotationManager().getCurrentRotation() != null) {
            Rotation var2 = Zenith.getInstance().getRotationManager().getCurrentRotation();
            this.safeYaw = mc.player.getYaw();
            this.safePitch = mc.player.getPitch();
            mc.player.setYaw(var2.getFloatValue());
            mc.player.setPitch(var2.getFloatValue2());
         }
      }
   }

   @Inject(
      method = {"calcGlidingVelocity(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void hookLightningGrimPlayerTick(Vec3d var1, CallbackInfoReturnable<Vec3d> var2) {
      if ((Object)this == MinecraftClient.getInstance().player) {
         RotationManager var3 = Zenith.getInstance().getRotationManager();
         Rotation var4 = var3.getCurrentRotation();
         Vec3d var5 = var4 == null ? MinecraftClient.getInstance().player.getRotationVector() : var4.method3135();
         Rotation var6 = var3.getRotation2();
         Vec3d var7 = var6 == null ? var5 : var6.method3135();
         Vec3d var8 = ElytraBoosterModule.INSTANCE.method1770(var1, (Vec3d)var2.getReturnValue(), var5, var7);
         var8 = GrimGlideModule.INSTANCE.method5976(var1, var8, var5);
         var8 = ElytraTargetModule.INSTANCE.method5249(var1, var8, var5);
         var2.setReturnValue(var8);
      }
   }

   @ModifyExpressionValue(
      method = {"calcGlidingVelocity(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/LivingEntity;getPitch()F"
      )}
   )
   private float hookModifyFallFlyingPitch(float var1) {
      if ((Object)this != MinecraftClient.getInstance().player) {
         return var1;
      } else {
         Rotation var2 = Zenith.getInstance().getRotationManager().getCurrentRotation();
         return var2 == null ? var1 : var2.getFloatValue2();
      }
   }

   @ModifyExpressionValue(
      method = {"calcGlidingVelocity(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;"
      )}
   )
   private Vec3d hookModifyFallFlyingRotationVector(Vec3d var1) {
      if ((Object)this != MinecraftClient.getInstance().player) {
         return var1;
      } else {
         Rotation var2 = Zenith.getInstance().getRotationManager().getCurrentRotation();
         return var2 == null ? var1 : var2.method3135();
      }
   }

   @ModifyReturnValue(
      method = {"getHandSwingDuration()I"},
      at = {@At("RETURN")}
   )
   private int modifySwingDuration(int var1) {
      SwingAnimationModule var2 = SwingAnimationModule.INSTANCE;
      return (LivingEntity)(Object)this == MinecraftClient.getInstance().player && var2.active() ? var2.method3269(var1) : var1;
   }

   @Inject(
      method = {"onDeath(Lnet/minecraft/entity/damage/DamageSource;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onDead(DamageSource var1, CallbackInfo var2) {
      EventManager.call(new EntityDeathEvent((LivingEntity)(Object)this));
   }

   @Inject(
      method = {"isPushable()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void isPushable(CallbackInfoReturnable<Boolean> var1) {
      EntityPushEvent var2 = new EntityPushEvent(EntityPushEvent.Type.COLLISION);
      EventManager.call(var2);
      if (var2.isCancelled()) {
         var1.setReturnValue(false);
      }
   }

   @Override
   public double zenithDLC$getPrevServerX() {
      return this.prevServerX;
   }

   @Override
   public double zenithDLC$getPrevServerY() {
      return this.prevServerY;
   }

   @Override
   public double zenithDLC$getPrevServerZ() {
      return this.prevServerZ;
   }

   @Override
   public void zenithDLC$trackServerPos(double var1, double var3, double var5) {
      if (this.serverPosTracked) {
         this.prevServerX = this.serverX;
         this.prevServerY = this.serverY;
         this.prevServerZ = this.serverZ;
      } else {
         this.prevServerX = var1;
         this.prevServerY = var3;
         this.prevServerZ = var5;
         this.serverPosTracked = true;
      }

      this.serverX = var1;
      this.serverY = var3;
      this.serverZ = var5;
      this.serverPosTime = ((LivingEntity)(Object)this).getEntityWorld().getTime();
   }

   @Override
   public Vec3d zenithDLC$getServerDelta() {
      if (this.serverPosTracked && ((LivingEntity)(Object)this).getEntityWorld().getTime() - this.serverPosTime <= 4L) {
         Vec3d var1 = new Vec3d(this.serverX - this.prevServerX, this.serverY - this.prevServerY, this.serverZ - this.prevServerZ);
         return var1.lengthSquared() > 100.0 ? Vec3d.ZERO : var1;
      } else {
         return Vec3d.ZERO;
      }
   }
}

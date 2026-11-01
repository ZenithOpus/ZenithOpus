package org.zenith.utility.mixin.entity;

import com.darkmagician6.eventapi.EventManager;
import java.util.Optional;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.client.events.MovementCorrectionEvent;
import org.zenith.client.modules.impl.render.AntiInvisibleModule;
import org.zenith.client.modules.impl.render.PredictionsModule;
import org.zenith.client.modules.impl.render.ShaderESPModule;
import org.zenith.utility.game.Util4;
import org.zenith.utility.misc.Handler6;

@Mixin({Entity.class})
public abstract class MixinEntity implements MinecraftAccess {
   @Shadow
   private float pitch;
   @Shadow
   public float lastPitch;
   @Shadow
   public float lastYaw;

   @Shadow
   public abstract void setPitch(float var1);

   @Shadow
   public abstract void setYaw(float var1);

   @Shadow
   public abstract float getYaw();

   @Shadow
   public abstract float getPitch();

   @Shadow
   public abstract String getNameForScoreboard();

   @Inject(
      method = {"updateTrackedPositionAndAngles(Ljava/util/Optional;Ljava/util/Optional;Ljava/util/Optional;)V"},
      at = {@At("HEAD")}
   )
   private void zenith$trackServerPos(Optional<Vec3d> var1, Optional<Float> var2, Optional<Float> var3, CallbackInfo var4) {
      if (var1.isPresent() && (Object)this instanceof Handler6 var5) {
         Vec3d var6 = (Vec3d)var1.get();
         var5.zenithDLC$trackServerPos(var6.x, var6.y, var6.z);
      }
   }

   @Inject(
      method = {"isInvisible()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void bypassSpeed(CallbackInfoReturnable<Boolean> var1) {
      if (AntiInvisibleModule.INSTANCE.isEnabled() && ((Object)this == mc.player || Zenith.getInstance().getFriends().method7627(this.getNameForScoreboard()))) {
         var1.setReturnValue(false);
      }
   }

   @Redirect(
      method = {"updateVelocity(FLnet/minecraft/util/math/Vec3d;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/Entity;getYaw()F"
      )
   )
   public float movementCorrection(Entity var1) {
      return var1 instanceof ClientPlayerEntity && Zenith.getInstance().getRotationManager().getCurrentRotation() != null
         ? Zenith.getInstance().getRotationManager().getCurrentRotation().getFloatValue()
         : var1.getYaw();
   }

   @Inject(
      method = {"updateVelocity(FLnet/minecraft/util/math/Vec3d;)V"},
      at = {@At("TAIL")}
   )
   private void zenith$movePost(float var1, Vec3d var2, CallbackInfo var3) {
      if (mc.player != null && (Object)this == mc.player) {
         EventManager.call(new MovementCorrectionEvent(var1, var2));
      }
   }

   @Inject(
      method = {"isGlowing()Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$shaderEspGlowing(CallbackInfoReturnable<Boolean> var1) {
      Entity var2 = (Entity)(Object)this;
      if (Util4.method1278(var2.getId())) {
         var1.setReturnValue(true);
      } else {
         ShaderESPModule var3 = ShaderESPModule.INSTANCE;
         if (var3 != null && var3.isEnabled() && var3.method4584(var2)) {
            var1.setReturnValue(true);
         } else {
            if (PredictionsModule.INSTANCE.method2911(var2)) {
               var1.setReturnValue(true);
            }
         }
      }
   }
}

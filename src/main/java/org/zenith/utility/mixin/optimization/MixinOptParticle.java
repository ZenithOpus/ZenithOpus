package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.particle.AscendingParticle;
import net.minecraft.client.particle.BubbleColumnUpParticle;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.render.FluidColorCache;

@Mixin({Particle.class})
public abstract class MixinOptParticle {
   @Shadow
   @Final
   public ClientWorld world;
   @Shadow
   public double x;
   @Shadow
   public double y;
   @Shadow
   public double z;
   @Unique
   private long zenith$lightTime = Long.MIN_VALUE;
   @Unique
   private double zenith$lightX;
   @Unique
   private double zenith$lightY;
   @Unique
   private double zenith$lightZ;
   @Unique
   private int zenith$light;

   @Inject(
      method = {"getBrightness(F)I"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$cachedLight(float var1, CallbackInfoReturnable<Integer> var2) {
      if (OptimizationFlags.flag
         && this.zenith$lightTime == this.world.getTime()
         && this.zenith$lightX == this.x
         && this.zenith$lightY == this.y
         && this.zenith$lightZ == this.z) {
         var2.setReturnValue(this.zenith$light);
      }
   }

   @Inject(
      method = {"getBrightness(F)I"},
      at = {@At("RETURN")}
   )
   private void zenith$storeLight(float var1, CallbackInfoReturnable<Integer> var2) {
      if (OptimizationFlags.flag) {
         this.zenith$light = var2.getReturnValueI();
         this.zenith$lightTime = this.world.getTime();
         this.zenith$lightX = this.x;
         this.zenith$lightY = this.y;
         this.zenith$lightZ = this.z;
      }
   }

   @ModifyExpressionValue(
      method = {"move(DDD)V"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/particle/Particle;collidesWithWorld:Z",
         opcode = 180
      )}
   )
   private boolean zenith$cheapPhysics(boolean var1) {
      if (!var1 || !OptimizationFlags.flag2) {
         return var1;
      } else {
         return !((Object)this instanceof AscendingParticle) && !((Object)this instanceof CampfireSmokeParticle) && !((Object)this instanceof BubbleColumnUpParticle)
            ? FluidColorCache.method7101(this.x, this.y, this.z) <= OptimizationFlags.doubleValue
            : false;
      }
   }
}

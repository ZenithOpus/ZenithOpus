package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.BillboardParticleRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.render.FluidColorCache;

@Mixin({BillboardParticleRenderer.class})
public abstract class MixinOptBillboardParticleRenderer {
   @WrapOperation(
      method = {"render(Lnet/minecraft/client/render/Frustum;Lnet/minecraft/client/render/Camera;F)Lnet/minecraft/client/render/Submittable;"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/Frustum;intersectPoint(DDD)Z"
      )}
   )
   private boolean zenith$lod(
      Frustum var1, double var2, double var4, double var6, Operation<Boolean> var8, Frustum var9, Camera var10, float var11, @Local BillboardParticle var12
   ) {
      if (!(Boolean)var8.call(new Object[]{var1, var2, var4, var6})) {
         return false;
      } else if (!OptimizationFlags.flag3) {
         return true;
      } else {
         Vec3d var13 = var10.getCameraPos();
         double var14 = var2 - var13.x;
         double var16 = var4 - var13.y;
         double var18 = var6 - var13.z;
         return FluidColorCache.method7102(var12, var14 * var14 + var16 * var16 + var18 * var18);
      }
   }
}

package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.particle.BubbleColumnUpParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.render.FluidColorCache;

@Mixin({BubbleColumnUpParticle.class})
public abstract class MixinOptBubbleColumnUpParticle {
   @WrapOperation(
      method = {"tick()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/world/ClientWorld;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"
      )}
   )
   private FluidState zenith$cachedFluid(ClientWorld var1, BlockPos var2, Operation<FluidState> var3) {
      if (!OptimizationFlags.flag4) {
         return (FluidState)var3.call(new Object[]{var1, var2});
      } else {
         FluidState var4 = FluidColorCache.method7103(var1, var2);
         if (var4 == null) {
            var4 = (FluidState)var3.call(new Object[]{var1, var2});
            FluidColorCache.method7104(var2, var4);
         }

         return var4;
      }
   }
}

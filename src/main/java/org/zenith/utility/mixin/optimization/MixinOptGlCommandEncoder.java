package org.zenith.utility.mixin.optimization;

import com.mojang.blaze3d.buffers.GpuFence;
import net.minecraft.client.gl.GlCommandEncoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.render.CompletionFence;

@Mixin({GlCommandEncoder.class})
public class MixinOptGlCommandEncoder {
   @Inject(
      method = {"createFence()Lcom/mojang/blaze3d/buffers/GpuFence;"},
      at = {@At(
         value = "NEW",
         target = "Lnet/minecraft/client/gl/GlGpuFence;"
      )},
      cancellable = true
   )
   private void zenith$sharedFrameFence(CallbackInfoReturnable<GpuFence> var1) {
      if (OptimizationFlags.flag20) {
         var1.setReturnValue(CompletionFence.method5163());
      }
   }
}

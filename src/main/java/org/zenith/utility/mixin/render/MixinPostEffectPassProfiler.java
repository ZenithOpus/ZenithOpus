package org.zenith.utility.mixin.render;

import net.minecraft.client.gl.PostEffectPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.zenith.utility.render.WorldProfiler;

@Mixin({PostEffectPass.class})
public abstract class MixinPostEffectPassProfiler {
   @ModifyArg(
      method = {"render(Lnet/minecraft/client/render/FrameGraphBuilder;Ljava/util/Map;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/FramePass;setRenderer(Ljava/lang/Runnable;)V"
      )
   )
   private Runnable zenith$profileWeatherPass(Runnable var1) {
      return WorldProfiler.method789(var1);
   }
}

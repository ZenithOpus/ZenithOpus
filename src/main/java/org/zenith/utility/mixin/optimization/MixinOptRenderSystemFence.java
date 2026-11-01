package org.zenith.utility.mixin.optimization;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.tracy.TracyFrameCapturer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.render.CompletionFence;

@Mixin({RenderSystem.class})
public class MixinOptRenderSystemFence {
   @Inject(
      method = {"flipFrame(Lnet/minecraft/client/util/Window;Lnet/minecraft/client/util/tracy/TracyFrameCapturer;)V"},
      at = {@At("HEAD")}
   )
   private static void zenith$endFrameFence(Window var0, TracyFrameCapturer var1, CallbackInfo var2) {
      CompletionFence.endFrame();
   }
}

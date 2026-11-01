package org.zenith.utility.mixin.render;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.misc.ScissorStack;
import org.zenith.utility.render.ImmediateRenderer;

@Mixin({GameRenderer.class})
public class MixinGuiExtraction {
   @Inject(
      method = {"render(Lnet/minecraft/client/render/RenderTickCounter;Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/render/state/GuiRenderState;clear()V",
         shift = Shift.AFTER
      )}
   )
   private void zenith$beginGui(RenderTickCounter var1, boolean var2, CallbackInfo var3) {
      ImmediateRenderer.method8003();
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/render/RenderTickCounter;Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/render/GuiRenderer;render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"
      )}
   )
   private void zenith$endGui(RenderTickCounter var1, boolean var2, CallbackInfo var3) {
      MsdfRenderer.flushBatch();
      ImmediateRenderer.method8004();
      ScissorStack.clear();
   }
}

package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.render.GuiVertexBatch;
import org.zenith.utility.render.ScissorFlusher;

@Mixin({GuiRenderer.class})
public class MixinGuiRendererTint {
   @Inject(
      method = {"render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"},
      at = {@At("HEAD")}
   )
   private void zenith$enter(CallbackInfo var1) {
      GuiVertexBatch.method103();
   }

   @Inject(
      method = {"render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"},
      at = {@At("RETURN")}
   )
   private void zenith$exit(CallbackInfo var1) {
      GuiVertexBatch.method104();
   }

   @Inject(
      method = {"clearOversizedItems()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$keepOversizedMidFrame(CallbackInfo var1) {
      if (ScissorFlusher.method6646()) {
         var1.cancel();
      }
   }

   @ModifyArg(
      method = {"prepareItem(Lnet/minecraft/client/gui/render/state/ItemGuiElementRenderState;FFII)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/render/state/GuiRenderState;addSimpleElementToCurrentLayer(Lnet/minecraft/client/gui/render/state/TexturedQuadGuiElementRenderState;)V"
      )
   )
   private TexturedQuadGuiElementRenderState zenith$itemColor(TexturedQuadGuiElementRenderState var1, @Local(argsOnly = true) ItemGuiElementRenderState var2) {
      return GuiVertexBatch.method99(var1, var2);
   }
}

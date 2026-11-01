package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.OversizedItemGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.zenith.utility.render.GuiVertexBatch;

@Mixin({SpecialGuiElementRenderer.class})
public class MixinSpecialGuiElementTint {
   @ModifyArg(
      method = {"renderElement(Lnet/minecraft/client/gui/render/state/special/SpecialGuiElementRenderState;Lnet/minecraft/client/gui/render/state/GuiRenderState;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/render/state/GuiRenderState;addSimpleElementToCurrentLayer(Lnet/minecraft/client/gui/render/state/TexturedQuadGuiElementRenderState;)V"
      )
   )
   private TexturedQuadGuiElementRenderState zenith$elementColor(
      TexturedQuadGuiElementRenderState var1, @Local(argsOnly = true) SpecialGuiElementRenderState var2
   ) {
      Object var3 = var2 instanceof OversizedItemGuiElementRenderState var4 ? var4.guiItemRenderState() : var2;
      return GuiVertexBatch.method99(var1, var3);
   }
}

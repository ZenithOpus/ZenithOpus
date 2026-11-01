package org.zenith.utility.mixin.render;

import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.render.GuiVertexBatch;
import org.zenith.utility.render.ScissorFlusher;

@Mixin({GuiRenderState.class})
public class MixinGuiRenderState implements ScissorFlusher.Handler {
   @Unique
   private boolean zenith$pending;

   @Inject(
      method = {"addItem(Lnet/minecraft/client/gui/render/state/ItemGuiElementRenderState;)V", "addText(Lnet/minecraft/client/gui/render/state/TextGuiElementRenderState;)V", "addSpecialElement(Lnet/minecraft/client/gui/render/state/special/SpecialGuiElementRenderState;)V", "addSimpleElement(Lnet/minecraft/client/gui/render/state/SimpleGuiElementRenderState;)V", "addSimpleElementToCurrentLayer(Lnet/minecraft/client/gui/render/state/TexturedQuadGuiElementRenderState;)V", "addPreparedTextElement(Lnet/minecraft/client/gui/render/state/SimpleGuiElementRenderState;)V"},
      at = {@At("HEAD")}
   )
   private void zenith$markPending(CallbackInfo var1) {
      this.zenith$pending = true;
   }

   @Inject(
      method = {"clear()V"},
      at = {@At("HEAD")}
   )
   private void zenith$clearPending(CallbackInfo var1) {
      this.zenith$pending = false;
      GuiVertexBatch.clear();
   }

   @ModifyVariable(
      method = {"addSimpleElement(Lnet/minecraft/client/gui/render/state/SimpleGuiElementRenderState;)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private SimpleGuiElementRenderState zenith$tintSimple(SimpleGuiElementRenderState var1) {
      return GuiVertexBatch.method102(var1);
   }

   @Inject(
      method = {"addItem(Lnet/minecraft/client/gui/render/state/ItemGuiElementRenderState;)V"},
      at = {@At("HEAD")}
   )
   private void zenith$tintItem(ItemGuiElementRenderState var1, CallbackInfo var2) {
      GuiVertexBatch.method98(var1);
   }

   @Inject(
      method = {"addSpecialElement(Lnet/minecraft/client/gui/render/state/special/SpecialGuiElementRenderState;)V"},
      at = {@At("HEAD")}
   )
   private void zenith$tintSpecial(SpecialGuiElementRenderState var1, CallbackInfo var2) {
      GuiVertexBatch.method98(var1);
   }

   @Override
   public boolean zenith$pending() {
      return this.zenith$pending;
   }
}

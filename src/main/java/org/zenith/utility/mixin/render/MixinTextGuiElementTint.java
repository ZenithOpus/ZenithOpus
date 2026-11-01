package org.zenith.utility.mixin.render;

import net.minecraft.client.gui.render.state.TextGuiElementRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.render.GuiVertexBatch;

@Mixin({TextGuiElementRenderState.class})
public class MixinTextGuiElementTint {
   @Shadow
   @Final
   @Mutable
   public int color;
   @Shadow
   @Final
   @Mutable
   public int backgroundColor;

   @Inject(
      method = {"<init>(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;Lorg/joml/Matrix3x2fc;IIIIZZLnet/minecraft/client/gui/ScreenRect;)V"},
      at = {@At("RETURN")}
   )
   private void zenith$tint(CallbackInfo var1) {
      float[] var2 = GuiVertexBatch.method97();
      if (var2 != null) {
         this.color = GuiVertexBatch.method100(this.color, var2, false);
         this.backgroundColor = GuiVertexBatch.method100(this.backgroundColor, var2, false);
      }
   }
}

package org.zenith.utility.mixin.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.screens.override.main.component.VanillaOptionStyle;

@Mixin({SliderWidget.class})
public abstract class MixinSliderWidget {
   @Shadow
   public double value;

   @Inject(
      method = {"renderWidget(Lnet/minecraft/client/gui/DrawContext;IIF)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$style(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (VanillaOptionStyle.isActive()) {
         VanillaOptionStyle.drawSlider((SliderWidget)(Object)this, this.value, var1, var2, var3, var4);
         var5.cancel();
      }
   }
}

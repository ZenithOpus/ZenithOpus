package org.zenith.utility.mixin.render;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.modules.impl.misc.ShulkerPreviewModule;

@Mixin({TooltipComponent.class})
public interface MixinShulkerLookTooltipComponent {
   @Inject(
      method = {"of(Lnet/minecraft/item/tooltip/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void replaceTooltipFactory(TooltipData var0, CallbackInfoReturnable<TooltipComponent> var1) {
      TooltipComponent var2 = ShulkerPreviewModule.method1388(var0);
      if (var2 != null) {
         var1.setReturnValue(var2);
      }
   }
}

package org.zenith.utility.mixin.world;

import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.modules.impl.misc.ShulkerPreviewModule;

@Mixin({ItemStack.class})
public abstract class MixinShulkerLookItemStack {
   @Inject(
      method = {"getTooltipData()Ljava/util/Optional;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void injectShulkerTooltipData(CallbackInfoReturnable<Optional<TooltipData>> var1) {
      var var2 = ShulkerPreviewModule.method1387((ItemStack)(Object)this);
      if (var2.isPresent()) {
         var1.setReturnValue(var2);
      }
   }
}

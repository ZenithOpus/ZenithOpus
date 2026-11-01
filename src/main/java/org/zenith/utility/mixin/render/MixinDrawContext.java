package org.zenith.utility.mixin.render;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.modules.impl.misc.ShulkerPreviewModule;
import org.zenith.client.modules.impl.player.AHHelperModule;
import org.zenith.utility.game.ItemNbtUtils;

@Mixin({DrawContext.class})
public abstract class MixinDrawContext {
   @Inject(
      method = {"createNewRootLayer()V"},
      at = {@At("HEAD")}
   )
   private void zenith$flushMsdfTextBatch(CallbackInfo var1) {
      MsdfRenderer.flushBatch();
   }

   @Invoker("drawTooltip")
   public abstract void invokeDrawTooltip(
      TextRenderer var1, List<TooltipComponent> var2, int var3, int var4, TooltipPositioner var5, @Nullable Identifier var6, boolean var7
   );

   @ModifyVariable(
      method = {"drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   private String zenith$showRealLotCount(@Nullable String var1, TextRenderer var2, ItemStack var3, int var4, int var5) {
      if (var1 != null) {
         return var1;
      } else if (!AHHelperModule.INSTANCE.isEnabled() || !AHHelperModule.INSTANCE.method4054()) {
         return null;
      } else if (var3 != null && !var3.isEmpty() && this.zenith$isContainerScreenOpen()) {
         int var6 = ItemNbtUtils.method91(var3);
         return var6 > 1 && var6 != var3.getCount() ? String.valueOf(var6) : null;
      } else {
         return null;
      }
   }

   @Unique
   private boolean zenith$isContainerScreenOpen() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      return var1.currentScreen instanceof HandledScreen;
   }

   @Inject(
      method = {"drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/util/Identifier;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void filterShulkerTooltip(
      TextRenderer var1, List<Text> var2, Optional<TooltipData> var3, int var4, int var5, @Nullable Identifier var6, CallbackInfo var7
   ) {
      if (var3 != null) {
         TooltipData var8 = (TooltipData)var3.orElse(null);
         if (var8 instanceof ShulkerPreviewModule.Entry) {
            ArrayList var9 = new ArrayList(2);
            if (var2 != null && !var2.isEmpty()) {
               var9.add(TooltipComponent.of(((Text)var2.get(0)).asOrderedText()));
            }

            TooltipComponent var10 = ShulkerPreviewModule.method1388(var8);
            if (var10 != null) {
               var9.add(var10);
            }

            if (var2 != null && var2.size() > 1) {
               Integer var11 = Formatting.DARK_GRAY.getColorValue();

               for (int var12 = 1; var12 < var2.size(); var12++) {
                  Text var13 = (Text)var2.get(var12);
                  if (var11 != null && var13.getStyle().getColor() != null && var13.getStyle().getColor().getRgb() == var11) {
                     var9.add(TooltipComponent.of(var13.asOrderedText()));
                  }
               }
            }

            if (!var9.isEmpty()) {
               this.invokeDrawTooltip(var1, var9, var4, var5, HoveredTooltipPositioner.INSTANCE, var6, false);
               var7.cancel();
            }
         }
      }
   }
}

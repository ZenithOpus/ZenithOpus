package org.zenith.utility.mixin.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.Handler3;

@Mixin({InventoryScreen.class})
public abstract class MixinInventoryScreen extends RecipeBookScreen<PlayerScreenHandler> {
   public MixinInventoryScreen(PlayerScreenHandler var1, RecipeBookWidget<?> var2, PlayerInventory var3, Text var4) {
      super(var1, var2, var3, var4);
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/gui/DrawContext;IIF)V"},
      at = {@At("RETURN")}
   )
   private void zenith$popInventoryScaleAnimation(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      Handler3 var6 = (Handler3)(Object)this;
      var6.zenith$betterMinecraft$popScaleIfNeeded(var1);
      var6.zenith$betterMinecraft$finishClosingAnimation();
   }
}

package org.zenith.utility.mixin.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.hud.WatermarkIslandHud;

@Mixin({BossBarHud.class})
public class MixinBossBarHud {
   @Inject(
      method = {"render(Lnet/minecraft/client/gui/DrawContext;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$islandTakesOver(DrawContext var1, CallbackInfo var2) {
      if (WatermarkIslandHud.method5669()) {
         var2.cancel();
      }
   }
}

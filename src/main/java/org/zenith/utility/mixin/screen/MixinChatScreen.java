package org.zenith.utility.mixin.screen;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.MinecraftAccess;
import org.zenith.client.hud.MusicHud;

@Mixin({ChatScreen.class})
public class MixinChatScreen extends Screen implements MinecraftAccess {
   public MixinChatScreen(Text var1) {
      super(var1);
   }

   @Inject(
      method = {"sendMessage(Ljava/lang/String;Z)V"},
      at = {@At("HEAD")},
      cancellable = false
   )
   private void onSendMessage(String var1, boolean var2, CallbackInfo var3) {
   }

   @Inject(
      method = {"mouseScrolled(DDDD)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$musicVolume(double var1, double var3, double var5, double var7, CallbackInfoReturnable<Boolean> var9) {
      if (var7 != 0.0 && MusicHud.method6122(var7)) {
         var9.setReturnValue(true);
      }
   }
}

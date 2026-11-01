package org.zenith.utility.mixin.input;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.KeyEvent;

@Mixin({Keyboard.class})
public class MixinKeyboard {
   @Inject(
      method = {"onKey(JILnet/minecraft/client/input/KeyInput;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void triggerKeyEvent(long var1, int var3, KeyInput var4, CallbackInfo var5) {
      if (var4.key() != -1) {
         EventManager.call(new KeyEvent(var3, var4.key(), var4.modifiers()));
      }
   }
}

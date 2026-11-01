package org.zenith.utility.mixin.client_core;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.events.WindowResizeEvent;

@Mixin({Window.class})
public class MixinWindow {
   @Inject(
      method = {"onWindowSizeChanged(JII)V"},
      at = {@At("TAIL")}
   )
   private void onWindowSizeChanged(long var1, int var3, int var4, CallbackInfo var5) {
      EventManager.call(new WindowResizeEvent());
   }
}

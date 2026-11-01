package org.zenith.utility.mixin.input;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.KeyEvent;
import org.zenith.MinecraftAccess;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.events.MouseRotationEvent;
import org.zenith.client.events.MouseScrollEvent;
import org.zenith.utility.misc.Handler3;

@Mixin({Mouse.class})
public class MixinMouse {
   @Shadow
   private MouseInput activeButton;
   @Shadow
   private double cursorDeltaX;
   @Shadow
   private double cursorDeltaY;

   @Inject(
      method = {"onMouseButton(JLnet/minecraft/client/input/MouseInput;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onMouseButton(long var1, MouseInput var3, int var4, CallbackInfo var5) {
      int var6 = var3.button();
      int var7 = var3.modifiers();
      if (var6 != -1 && var1 == MinecraftAccess.mc.getWindow().getHandle()) {
         EventManager.call(new KeyEvent(var4, var6, var7));
         MouseButtonEvent var8 = new MouseButtonEvent(var6, var4);
         EventManager.call(var8);
         if (var8.isCancelled()) {
            var5.cancel();
         }
      }
   }

   @Inject(
      method = {"onMouseScroll(JDD)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerEntity;getInventory()Lnet/minecraft/entity/player/PlayerInventory;"
      )},
      cancellable = true
   )
   public void onMouseScrollHook(long var1, double var3, double var5, CallbackInfo var7) {
      MouseScrollEvent var8 = new MouseScrollEvent(var3, var5);
      EventManager.call(var8);
      if (var8.isCancelled()) {
         var7.cancel();
      }
   }

   @Redirect(
      method = {"tick()V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/Mouse;isCursorLocked()Z"
      )
   )
   public boolean onIsCursorLocked(Mouse var1) {
      return var1.isCursorLocked() || this.isAnim();
   }

   @WrapWithCondition(
      method = {"updateMouse(D)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"
      )},
      require = 1,
      allow = 1
   )
   private boolean modifyMouseRotationInput(ClientPlayerEntity var1, double var2, double var4) {
      MouseRotationEvent var6 = new MouseRotationEvent(var2, var4, this.cursorDeltaX, this.cursorDeltaY);
      EventManager.call(var6);
      if (var6.isCancelled()) {
         return false;
      } else {
         var1.changeLookDirection(var6.getDoubleValue(), var6.getDoubleValue2());
         return false;
      }
   }

   @Unique
   private boolean isAnim() {
      return MinecraftClient.getInstance().currentScreen instanceof Handler3 var2 && var2.zenith$betterMinecraft$isClosingAnimation();
   }
}

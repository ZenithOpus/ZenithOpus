package org.zenith.utility.mixin.input;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.modules.impl.movement.WaterRiseModule;
import org.zenith.utility.misc.ViaCompat;

@Mixin({KeyboardInput.class})
public abstract class MixinKeyboardInput extends Input {
   @Inject(
      method = {"tick()V"},
      at = {@At("TAIL")}
   )
   private void tickHook(CallbackInfo var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2.player != null && var2.player.input == this) {
         MoveInputEvent var3 = new MoveInputEvent(this.playerInput);
         EventManager.call(var3);
         PlayerInput var4 = var3.getPlayerInput();
         if (var4 != null) {
            this.playerInput = new PlayerInput(var4.forward(), var4.backward(), var4.left(), var4.right(), var4.jump(), var4.sneak(), var4.sprint());
            this.movementVector = ViaCompat.method4851(
               new Vec2f(zenith$multiplier(var4.left(), var4.right()), zenith$multiplier(var4.forward(), var4.backward()))
            );
            WaterRiseModule var5 = WaterRiseModule.INSTANCE;
            if (var5.method1743()) {
               if (var4.forward()) {
                  var5.method1742();
               } else if (!var4.backward()) {
                  this.movementVector = new Vec2f(this.movementVector.x, var5.method1744());
               }
            }
         }
      }
   }

   @Unique
   private static float zenith$multiplier(boolean var0, boolean var1) {
      if (var0 == var1) {
         return 0.0F;
      } else {
         return var0 ? 1.0F : -1.0F;
      }
   }
}

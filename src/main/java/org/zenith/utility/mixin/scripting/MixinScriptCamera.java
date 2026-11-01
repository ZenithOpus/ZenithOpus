package org.zenith.utility.mixin.scripting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.game.Util4;
import org.zenith.utility.misc.ModuleHookDefinitions;

@Mixin({GameRenderer.class})
public abstract class MixinScriptCamera {
   @Inject(
      method = {"tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$hurtCam(MatrixStack var1, float var2, CallbackInfo var3) {
      if (ModuleHookDefinitions.method6119(ModuleHookDefinitions.class129)) {
         var3.cancel();
      }
   }

   @Inject(
      method = {"bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$viewBob(MatrixStack var1, float var2, CallbackInfo var3) {
      if (ModuleHookDefinitions.method6119(ModuleHookDefinitions.class1210)) {
         var3.cancel();
      }
   }

   @ModifyReturnValue(
      method = {"getFov(Lnet/minecraft/client/render/Camera;FZ)F"},
      at = {@At("RETURN")}
   )
   private float zenith$scriptZoom(float var1) {
      float var2 = Util4.method1281();
      return var2 > 1.001F ? Math.max(1.0F, var1 / var2) : var1;
   }
}

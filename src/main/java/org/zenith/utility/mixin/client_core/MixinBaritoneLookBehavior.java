package org.zenith.utility.mixin.client_core;

import baritone.api.utils.Rotation;
import baritone.behavior.LookBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.base.rotation.RotationLock;

@Mixin(
   value = {LookBehavior.class},
   remap = false
)
public abstract class MixinBaritoneLookBehavior {
   @Inject(
      method = {"updateTarget(Lbaritone/api/utils/Rotation;Z)V"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private void zenith$interceptLook(Rotation var1, boolean var2, CallbackInfo var3) {
      if (var1 != null && RotationLock.method2706()) {
         RotationLock.method2707(var1.getYaw(), var1.getPitch());
         var3.cancel();
      }
   }
}

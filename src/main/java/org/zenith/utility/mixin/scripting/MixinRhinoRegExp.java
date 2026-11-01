package org.zenith.utility.mixin.scripting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.scripting.ScriptContextFactory;

@Mixin(
   targets = {"org/mozilla/javascript/regexp/NativeRegExp"},
   remap = false
)
public abstract class MixinRhinoRegExp {
   @Inject(
      method = {"pushBackTrackState(Lorg/mozilla/javascript/regexp/REGlobalData;BI)V"},
      at = {@At("HEAD")},
      remap = false
   )
   private static void zenith$backtrack(CallbackInfo var0) {
      ScriptContextFactory.method7407();
   }

   @Inject(
      method = {"simpleMatch(Lorg/mozilla/javascript/regexp/REGlobalData;Ljava/lang/String;I[BIIZ)I"},
      at = {@At("HEAD")},
      remap = false
   )
   private static void zenith$simpleMatch(CallbackInfoReturnable<Integer> var0) {
      ScriptContextFactory.method7407();
   }
}

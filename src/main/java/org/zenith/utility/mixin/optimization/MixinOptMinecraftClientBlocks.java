package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.misc.Util20;

@Mixin({MinecraftClient.class})
public class MixinOptMinecraftClientBlocks {
   @Inject(
      method = {"tick()V"},
      at = {@At("HEAD")}
   )
   private void zenith$optTick(CallbackInfo var1) {
      Util20.method5134();
   }

   @ModifyReturnValue(
      method = {"isAmbientOcclusionEnabled()Z"},
      at = {@At("RETURN")}
   )
   private static boolean zenith$flatMovingBlock(boolean var0) {
      return var0 && (!OptimizationFlags.flag10 || !Util20.method5138());
   }
}

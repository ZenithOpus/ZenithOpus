package org.zenith.utility.mixin.optimization;

import net.minecraft.client.render.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.utility.misc.OptimizationFlags;

@Pseudo
@Mixin(
   targets = {"net/caffeinemc/mods/sodium/client/render/vertex/VertexConsumerUtils"},
   remap = false
)
public class MixinOptSodiumVertexConsumerUtils {
   @Unique
   private static volatile VertexConsumer zenith$lastWriter;

   @Inject(
      method = {"convertOrLog"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false,
      require = 0
   )
   private static void zenith$cachedWriter(VertexConsumer var0, CallbackInfoReturnable<Object> var1) {
      if (OptimizationFlags.flag14 && var0 != null && var0 == zenith$lastWriter) {
         var1.setReturnValue(var0);
      }
   }

   @Inject(
      method = {"convertOrLog"},
      at = {@At("RETURN")},
      remap = false,
      require = 0
   )
   private static void zenith$rememberWriter(VertexConsumer var0, CallbackInfoReturnable<Object> var1) {
      if (var1.getReturnValue() == var0) {
         zenith$lastWriter = var0;
      }
   }
}

package org.zenith.utility.mixin.client_core;

import baritone.api.BaritoneAPI;
import baritone.api.Settings;
import baritone.cache.CachedWorld;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {CachedWorld.class},
   remap = false
)
public abstract class MixinBaritoneCachedWorld {
   @Inject(
      method = {"queueForPacking(Lnet/minecraft/world/chunk/WorldChunk;)V"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private void zenith$skipPackingWhenCacheDisabled(WorldChunk var1, CallbackInfo var2) {
      Settings var3 = BaritoneAPI.getSettings();
      if (var3 != null && Boolean.FALSE.equals(var3.chunkCaching.value)) {
         var2.cancel();
      }
   }
}

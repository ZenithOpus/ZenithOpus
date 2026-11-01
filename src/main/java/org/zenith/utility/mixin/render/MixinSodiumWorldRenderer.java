package org.zenith.utility.mixin.render;

import java.lang.reflect.Field;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
   targets = {"net/caffeinemc/mods/sodium/client/render/SodiumWorldRenderer"},
   remap = false
)
public class MixinSodiumWorldRenderer {
   @Unique
   private static Field zenith$renderSectionManagerField;
   @Unique
   private static boolean zenith$renderSectionManagerLookupFailed;

   @Inject(
      method = {"scheduleRebuildForChunk"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private void zenith$skipUninitializedRebuild(int var1, int var2, int var3, boolean var4, CallbackInfo var5) {
      if (this.zenith$isRenderSectionManagerMissing()) {
         var5.cancel();
      }
   }

   @Unique
   private boolean zenith$isRenderSectionManagerMissing() {
      if (zenith$renderSectionManagerLookupFailed) {
         return false;
      } else {
         Field var1 = zenith$renderSectionManagerField;
         if (var1 == null) {
            try {
               var1 = this.getClass().getDeclaredField("renderSectionManager");
               var1.setAccessible(true);
               zenith$renderSectionManagerField = var1;
            } catch (Exception var4) {
               zenith$renderSectionManagerLookupFailed = true;
               return false;
            }
         }

         try {
            return var1.get(this) == null;
         } catch (Exception var3) {
            return false;
         }
      }
   }
}

package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;
import org.zenith.client.modules.impl.render.WorldModule;
import org.zenith.utility.misc.ModuleHookDefinitions;

@Mixin({LightmapTextureManager.class})
public class MixinLightmapTextureManager {
   @ModifyReturnValue(
      method = {"getDarkness(Lnet/minecraft/entity/LivingEntity;FF)F"},
      at = {@At("RETURN")}
   )
   private float zenith$darknessFactor(float var1) {
      return (float)ModuleHookDefinitions.method6118(ModuleHookDefinitions.class127, var1);
   }

   @ModifyExpressionValue(
      method = {"update(F)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;"
      )},
      slice = {@Slice(
         from = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/option/GameOptions;getGamma()Lnet/minecraft/client/option/SimpleOption;"
         )
      )}
   )
   private Object injectXRayFullBright(Object var1) {
      if (var1 instanceof Double var2) {
         float var3 = WorldModule.INSTANCE.method5644();
         Object var4 = var1;
         if (var3 >= 0.0F) {
            var4 = Math.max(var2, (double)(var3 * 10.0F));
         }

         if (ModuleHookDefinitions.class1211.active() && var4 instanceof Double var5) {
            var4 = Math.max(0.0, Math.min(15.0, ModuleHookDefinitions.method6118(ModuleHookDefinitions.class1211, var5)));
         }

         return var4;
      } else {
         return var1;
      }
   }
}

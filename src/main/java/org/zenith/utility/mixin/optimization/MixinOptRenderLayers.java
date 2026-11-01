package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.utility.misc.OptimizationFlags;

@Mixin({RenderLayers.class})
public class MixinOptRenderLayers {
   @Unique
   private static final ConcurrentHashMap<Identifier, RenderLayer> zenith$cutoutNoCull = new ConcurrentHashMap<>();
   @Unique
   private static final ConcurrentHashMap<Identifier, RenderLayer> zenith$translucent = new ConcurrentHashMap<>();

   @WrapOperation(
      method = {"entityCutoutNoCull(Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/RenderLayer;"},
      at = {@At(
         value = "INVOKE",
         target = "Ljava/util/function/BiFunction;apply(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
      )}
   )
   private static Object zenith$cachedCutoutNoCull(BiFunction<Object, Object, Object> var0, Object var1, Object var2, Operation<Object> var3) {
      return zenith$lookup(zenith$cutoutNoCull, var0, var1, var2, var3);
   }

   @WrapOperation(
      method = {"entityTranslucent(Lnet/minecraft/util/Identifier;Z)Lnet/minecraft/client/render/RenderLayer;"},
      at = {@At(
         value = "INVOKE",
         target = "Ljava/util/function/BiFunction;apply(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
      )}
   )
   private static Object zenith$cachedTranslucent(BiFunction<Object, Object, Object> var0, Object var1, Object var2, Operation<Object> var3) {
      return zenith$lookup(zenith$translucent, var0, var1, var2, var3);
   }

   @Unique
   private static Object zenith$lookup(
      ConcurrentHashMap<Identifier, RenderLayer> var0, BiFunction<Object, Object, Object> var1, Object var2, Object var3, Operation<Object> var4
   ) {
      if (OptimizationFlags.flag15 && var3 == Boolean.TRUE && var2 != null) {
         RenderLayer var5 = (RenderLayer)var0.get(var2);
         if (var5 == null) {
            var5 = (RenderLayer)var4.call(new Object[]{var1, var2, var3});
            var0.putIfAbsent((Identifier)var2, var5);
         }

         return var5;
      } else {
         return var4.call(new Object[]{var1, var2, var3});
      }
   }
}

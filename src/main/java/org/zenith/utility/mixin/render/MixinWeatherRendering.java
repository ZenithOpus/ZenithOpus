package org.zenith.utility.mixin.render;

import net.minecraft.client.render.WeatherRendering;
import net.minecraft.client.render.state.WeatherRenderState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.Precipitation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.modules.impl.render.WeatherModule;

@Mixin({WeatherRendering.class})
public class MixinWeatherRendering {
   @Inject(
      method = {"getPrecipitationAt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/world/biome/Biome$Precipitation;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$forcePrecipitation(World var1, BlockPos var2, CallbackInfoReturnable<Precipitation> var3) {
      int var4 = WeatherModule.INSTANCE.method3112();
      if (var4 >= 0) {
         var3.setReturnValue(switch (var4) {
            case 1 -> Precipitation.RAIN;
            case 2 -> Precipitation.SNOW;
            default -> Precipitation.NONE;
         });
      }
   }

   @Inject(
      method = {"buildPrecipitationPieces(Lnet/minecraft/world/World;IFLnet/minecraft/util/math/Vec3d;Lnet/minecraft/client/render/state/WeatherRenderState;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$skipHiddenPieces(World var1, int var2, float var3, Vec3d var4, WeatherRenderState var5, CallbackInfo var6) {
      if (WeatherModule.INSTANCE.method3113()) {
         var5.clear();
         var6.cancel();
      }
   }
}

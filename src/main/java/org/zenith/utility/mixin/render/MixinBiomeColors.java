package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.client.modules.impl.render.WeatherModule;

@Mixin({BiomeColors.class})
public class MixinBiomeColors {
   private static final int FROZEN_GRASS = 14215388;
   private static final int FROZEN_FOLIAGE = 13821144;

   @ModifyReturnValue(
      method = {"getGrassColor(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;)I"},
      at = {@At("RETURN")}
   )
   private static int zenith$frostGrass(int var0, BlockRenderView var1, BlockPos var2) {
      float var3 = WeatherModule.INSTANCE.method3101();
      return var3 > 0.001F ? blend(var0, 14215388, var3) : var0;
   }

   @ModifyReturnValue(
      method = {"getFoliageColor(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;)I"},
      at = {@At("RETURN")}
   )
   private static int zenith$frostFoliage(int var0, BlockRenderView var1, BlockPos var2) {
      float var3 = WeatherModule.INSTANCE.method3101();
      return var3 > 0.001F ? blend(var0, 13821144, var3) : var0;
   }

   private static int blend(int var0, int var1, float var2) {
      int var3 = channel(var0 >> 16, var1 >> 16, var2);
      int var4 = channel(var0 >> 8, var1 >> 8, var2);
      int var5 = channel(var0, var1, var2);
      return var3 << 16 | var4 << 8 | var5;
   }

   private static int channel(int var0, int var1, float var2) {
      int var3 = var0 & 0xFF;
      int var4 = var1 & 0xFF;
      return Math.clamp((long)((int)(var3 + (var4 - var3) * var2 + 0.5F)), 0, 255);
   }
}

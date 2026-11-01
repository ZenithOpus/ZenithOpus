package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.client.modules.impl.render.WeatherModule;

@Mixin({BlockColors.class})
public class MixinBlockColors {
   private static final int FROZEN_GRASS = frosted(8434839);
   private static final int FROZEN_FOLIAGE = frosted(6332795);

   private static int frosted(int var0) {
      int var1 = var0 >> 16 & 0xFF;
      int var2 = var0 >> 8 & 0xFF;
      int var3 = var0 & 0xFF;
      var1 += (255 - var1) * 88 / 100;
      var2 += (255 - var2) * 88 / 100;
      var3 += (255 - var3) * 92 / 100;
      return var1 << 16 | var2 << 8 | var3;
   }

   private static int blend(int var0, int var1, float var2) {
      return Math.clamp((long)((int)(var0 + (var1 - var0) * var2)), 0, 255);
   }

   @ModifyReturnValue(
      method = {"getColor(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;I)I"},
      at = {@At("RETURN")}
   )
   private int zenith$frostTint(int var1, BlockState var2, @Nullable BlockRenderView var3, @Nullable BlockPos var4, int var5) {
      float var6 = WeatherModule.INSTANCE.method3101();
      if (var6 <= 0.001F) {
         return var1;
      } else {
         int var7 = var2.isIn(BlockTags.LEAVES) ? FROZEN_FOLIAGE : FROZEN_GRASS;
         int var8 = blend(var1 >> 16 & 0xFF, var7 >> 16 & 0xFF, var6);
         int var9 = blend(var1 >> 8 & 0xFF, var7 >> 8 & 0xFF, var6);
         int var10 = blend(var1 & 0xFF, var7 & 0xFF, var6);
         return var8 << 16 | var9 << 8 | var10;
      }
   }
}

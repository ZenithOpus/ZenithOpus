package org.zenith.utility.game;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public final class NoRenderBlocks {
   public static volatile boolean enabled = false;
   private static final String[] texts = new String[]{
      "short_grass", "tall_grass", "fern", "large_fern", "dead_bush", "seagrass", "tall_seagrass", "short_dry_grass", "tall_dry_grass", "bush"
   };

   private NoRenderBlocks() {
   }

   public static boolean method5237(BlockState var0, BlockPos var1) {
      if (!enabled || var0 == null || var1 == null) {
         return false;
      } else if (!method5238(var0)) {
         return false;
      } else {
         int var2 = var1.getY();
         if (var0.contains(Properties.DOUBLE_BLOCK_HALF) && var0.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
            var2--;
         }

         return method5239(var1.getX(), var2, var1.getZ()) < 0.5F;
      }
   }

   private static boolean method5238(BlockState var0) {
      return NoRenderBlocks.Entry.items.contains(var0.getBlock());
   }

   private static float method5239(int var0, int var1, int var2) {
      long var3 = var0 * -7046029254386353131L ^ var1 * -4417276706812531889L ^ var2 * 1609587929392839161L;
      var3 ^= var3 >>> 31;
      var3 *= -4658895280553007687L;
      var3 ^= var3 >>> 29;
      return (float)(var3 >>> 40) / 1.6777216E7F;
   }

   private static final class Entry {
      private static final Set<Block> items = build();

      private static Set<Block> build() {
         ReferenceOpenHashSet var0 = new ReferenceOpenHashSet();

         for (String var4 : NoRenderBlocks.texts) {
            Registries.BLOCK.getOptionalValue(Identifier.ofVanilla(var4)).ifPresent(var0::add);
         }

         return var0;
      }
   }
}

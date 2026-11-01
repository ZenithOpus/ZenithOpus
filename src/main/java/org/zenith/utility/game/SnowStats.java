package org.zenith.utility.game;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import org.zenith.utility.misc.Class32;

public final class SnowStats {
   public static final int CONST_intValue = 24;
   public static final int CONST_intValue2 = 48;

   private SnowStats() {
   }

   public static SnowStats.Entry method803(World var0, Class32 var1, int var2, int var3, int var4, int var5, float var6) {
      SnowStats.Entry var7 = new SnowStats.Entry();
      if (var0 != null && var1 != null) {
         int var8 = Math.max(1, Math.round(var6 * 4.0F));
         Mutable var9 = new Mutable();

         for (int var10 = -var5; var10 <= var5; var10++) {
            for (int var11 = -var5; var11 <= var5; var11++) {
               if (var10 * var10 + var11 * var11 <= var5 * var5) {
                  int var12 = var2 + var10;
                  int var13 = var4 + var11;
                  var7.intValue++;
                  int var14 = method804(var0, var9, var12, var3, var13);
                  if (var14 == Integer.MIN_VALUE) {
                     var7.intValue3++;
                  } else {
                     method805(var1, var12, var14, var13, var8);
                     var7.intValue2++;
                  }
               }
            }
         }

         var7.intValue4 = var1.method4910();
         return var7;
      } else {
         return var7;
      }
   }

   private static int method804(World var0, Mutable var1, int var2, int var3, int var4) {
      int var5 = var3 + 24;
      int var6 = Math.max(var0.getBottomY(), var3 - 48);
      boolean var7 = false;

      for (int var8 = var5; var8 >= var6; var8--) {
         var1.set(var2, var8, var4);
         if (!var0.isChunkLoaded(var2 >> 4, var4 >> 4)) {
            return Integer.MIN_VALUE;
         }

         BlockState var9 = var0.getBlockState(var1);
         boolean var10 = !var9.isAir() && var9.getFluidState().isEmpty() && var9.isSideSolidFullSquare(var0, var1, Direction.UP);
         if (var10 && var7) {
            return var8;
         }

         var7 = var9.isAir();
      }

      return Integer.MIN_VALUE;
   }

   private static void method805(Class32 var0, int var1, int var2, int var3, int var4) {
      int var5 = Class32.method4911(var1);
      int var6 = Class32.method4911(var3);
      int var7 = Class32.method4911(var2 + 1);

      for (int var8 = 0; var8 < 4; var8++) {
         for (int var9 = 0; var9 < 4; var9++) {
            for (int var10 = 0; var10 < var4; var10++) {
               int var11 = var10 == var4 - 1 ? 0 : 127;
               var0.method4904(var5 + var8, var7 + var10, var6 + var9, (byte)var11);
            }
         }
      }
   }

   public static final class Entry {
      public int intValue;
      public int intValue2;
      public int intValue3;
      public int intValue4;

      @Override
      public String toString() {
         return "колонок " + this.intValue + ", засыпано " + this.intValue2 + ", пропущено " + this.intValue3 + ", схлопнуто чанков " + this.intValue4;
      }
   }
}

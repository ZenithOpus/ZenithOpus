package org.zenith.utility.game;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.zenith.Rotation;

public final class MineTargetFinder {
   private static final MineTargetFinder.Data data = new MineTargetFinder.Data(65363, 65382, 52, 61, 65348, 65369);
   private static final MineTargetFinder.Data data2 = new MineTargetFinder.Data(65389, -127, 51, 62, 65375, 65395);
   private static final Set<Block> items = Set.of(
      Blocks.COAL_ORE,
      Blocks.DEEPSLATE_COAL_ORE,
      Blocks.IRON_ORE,
      Blocks.DEEPSLATE_IRON_ORE,
      Blocks.COPPER_ORE,
      Blocks.DEEPSLATE_COPPER_ORE,
      Blocks.GOLD_ORE,
      Blocks.DEEPSLATE_GOLD_ORE,
      Blocks.REDSTONE_ORE,
      Blocks.DEEPSLATE_REDSTONE_ORE,
      Blocks.EMERALD_ORE,
      Blocks.DEEPSLATE_EMERALD_ORE,
      Blocks.LAPIS_ORE,
      Blocks.DEEPSLATE_LAPIS_ORE,
      Blocks.DIAMOND_ORE,
      Blocks.DEEPSLATE_DIAMOND_ORE,
      Blocks.NETHER_GOLD_ORE,
      Blocks.NETHER_QUARTZ_ORE,
      Blocks.ANCIENT_DEBRIS,
      Blocks.CLAY
   );

   public static MineTargetFinder.Data method1933(boolean var0) {
      return var0 ? data2 : data;
   }

   private MineTargetFinder() {
   }

   public static boolean method1934(Block var0) {
      return items.contains(var0);
   }

   public static boolean method1935(MineTargetFinder.Data var0, BlockPos var1) {
      return var1.getX() >= var0.getIntValue()
         && var1.getX() <= var0.getIntValue2()
         && var1.getY() >= var0.getIntValue3()
         && var1.getY() <= var0.getIntValue4()
         && var1.getZ() >= var0.getIntValue5()
         && var1.getZ() <= var0.getIntValue6();
   }

   public static boolean method1936(MineTargetFinder.Data var0, Vec3d var1) {
      return var1.x >= var0.getIntValue() && var1.x <= var0.getIntValue2() + 1.0 && var1.z >= var0.getIntValue5() && var1.z <= var0.getIntValue6() + 1.0;
   }

   public static boolean method1937(MineTargetFinder.Data var0, BlockPos var1) {
      return var1.getX() >= var0.getIntValue()
         && var1.getX() <= var0.getIntValue2()
         && var1.getY() >= var0.getIntValue3() - 3
         && var1.getY() <= var0.getIntValue4() + 3
         && var1.getZ() >= var0.getIntValue5()
         && var1.getZ() <= var0.getIntValue6();
   }

   public static boolean method1938(MineTargetFinder.Data var0, World var1) {
      int var2 = var0.getIntValue() - 4;
      int var3 = var0.getIntValue2() + 4;
      int var4 = var0.getIntValue5() - 4;
      int var5 = var0.getIntValue6() + 4;
      int var6 = (var0.getIntValue() + var0.getIntValue2()) / 2;
      int var7 = (var0.getIntValue5() + var0.getIntValue6()) / 2;
      return var1.isChunkLoaded(new BlockPos(var2, var0.getIntValue3(), var4))
         && var1.isChunkLoaded(new BlockPos(var2, var0.getIntValue3(), var5))
         && var1.isChunkLoaded(new BlockPos(var3, var0.getIntValue3(), var4))
         && var1.isChunkLoaded(new BlockPos(var3, var0.getIntValue3(), var5))
         && var1.isChunkLoaded(new BlockPos(var6, var0.getIntValue3(), var7));
   }

   public static double method1939(Vec3d var0, BlockPos var1) {
      double var2 = Math.max((double)var1.getX(), Math.min(var1.getX() + 1.0, var0.x));
      double var4 = Math.max((double)var1.getY(), Math.min(var1.getY() + 1.0, var0.y));
      double var6 = Math.max((double)var1.getZ(), Math.min(var1.getZ() + 1.0, var0.z));
      return var0.squaredDistanceTo(var2, var4, var6);
   }

   public static BlockHitResult method1940(BlockPos var0, double var1, Vec3d var3) {
      Vec3d var4 = var0.toCenterPos();
      double var5 = 0.49;
      Vec3d[] var7 = new Vec3d[]{
         var4,
         var4.add(var5, 0.0, 0.0),
         var4.add(-var5, 0.0, 0.0),
         var4.add(0.0, var5, 0.0),
         var4.add(0.0, -var5, 0.0),
         var4.add(0.0, 0.0, var5),
         var4.add(0.0, 0.0, -var5)
      };

      for (Vec3d var11 : var7) {
         BlockHitResult var12 = TargetUtils.method3205(var3, var1, Rotation.method3128(var11, var3), false);
         if (var12 != null && var12.getType() != Type.MISS && var12.getBlockPos().equals(var0)) {
            return var12;
         }
      }

      return null;
   }

   public static boolean method1941(BlockPos var0, Vec3d var1, Vec3d var2, double var3) {
      Vec3d var5 = var0.toCenterPos().subtract(var1);
      double var6 = var5.x * var2.x + var5.z * var2.z;
      return var6 > 0.0 && var6 <= var3 + 0.75 && method1945(var0, var1, var2) <= 1.2;
   }

   public static boolean method1942(BlockPos var0, Vec3d var1, Vec3d var2, double var3) {
      Vec3d var5 = var0.toCenterPos().subtract(var1);
      double var6 = var5.x * var2.x + var5.z * var2.z;
      return var6 > -0.35 && var6 <= Math.min(var3 + 0.75, 4.25) && method1945(var0, var1, var2) <= 2.25;
   }

   public static BlockPos method1943(BlockPos var0, List<BlockPos> var1, Vec3d var2, Vec3d var3) {
      Vec3d var4 = var0.toCenterPos();
      Vec3d var5 = new Vec3d(var4.x - var3.x, 0.0, var4.z - var3.z);
      double var6 = var5.lengthSquared();
      if (var6 < 0.01) {
         return null;
      } else {
         double var8 = Math.sqrt(var6);
         Vec3d var10 = var5.multiply(1.0 / var8);
         return var1.stream()
            .filter(var1x -> !var1x.equals(var0))
            .filter(var4x -> method1942(var4x, var3, var10, var8))
            .min(Comparator.<BlockPos>comparingDouble(var1x -> method1939(var2, var1x)).thenComparingDouble(var2x -> method1945(var2x, var3, var10)))
            .orElse(null);
      }
   }

   public static BlockPos method1944(BlockPos var0, List<BlockPos> var1, Vec3d var2, Vec3d var3, int var4) {
      if (var0.getY() != var4) {
         return null;
      } else {
         Vec3d var5 = var0.toCenterPos();
         Vec3d var6 = new Vec3d(var5.x - var3.x, 0.0, var5.z - var3.z);
         double var7 = var6.lengthSquared();
         if (var7 < 0.01) {
            return null;
         } else {
            double var9 = Math.sqrt(var7);
            Vec3d var11 = var6.multiply(1.0 / var9);
            return var1.stream()
               .filter(var1x -> var1x.getY() == var0.getY())
               .filter(var4x -> method1941(var4x, var3, var11, var9))
               .min(Comparator.<BlockPos>comparingDouble(var2x -> method1945(var2x, var3, var11)).thenComparingDouble(var1x -> method1939(var2, var1x)))
               .orElse(null);
         }
      }
   }

   public static double method1945(BlockPos var0, Vec3d var1, Vec3d var2) {
      Vec3d var3 = var0.toCenterPos().subtract(var1);
      double var4 = var3.x * var2.x + var3.z * var2.z;
      double var6 = var1.x + var2.x * var4;
      double var8 = var1.z + var2.z * var4;
      double var10 = var0.getX() + 0.5 - var6;
      double var12 = var0.getZ() + 0.5 - var8;
      return var10 * var10 + var12 * var12;
   }

   public static final class Data {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;
      private final int intValue5;
      private final int intValue6;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public int intValue4() {
         return this.intValue4;
      }

      public int intValue5() {
         return this.intValue5;
      }

      public int intValue6() {
         return this.intValue6;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.intValue4, other.intValue4) && java.util.Objects.equals(this.intValue5, other.intValue5) && java.util.Objects.equals(this.intValue6, other.intValue6);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3, intValue4, intValue5, intValue6);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "intValue4=" + this.intValue4 + ", " + "intValue5=" + this.intValue5 + ", " + "intValue6=" + this.intValue6 + "]";
      }

      public Data(int var1, int var2, int var3, int var4, int var5, int var6) {
         this.intValue = var1;
         this.intValue2 = var2;
         this.intValue3 = var3;
         this.intValue4 = var4;
         this.intValue5 = var5;
         this.intValue6 = var6;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public int getIntValue3() {
         return this.intValue3;
      }

      public int getIntValue4() {
         return this.intValue4;
      }

      public int getIntValue5() {
         return this.intValue5;
      }

      public int getIntValue6() {
         return this.intValue6;
      }
   }
}

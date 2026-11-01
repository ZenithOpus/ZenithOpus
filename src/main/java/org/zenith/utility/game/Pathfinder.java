package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.zenith.MinecraftAccess;

public final class Pathfinder implements MinecraftAccess {
   private static final double CONST_doubleValue = Math.sqrt(2.0);
   private static final int[][] CONST_intValues = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
   private static final int[][] CONST_intValues2 = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

   private Pathfinder() {
   }

   public static Optional<Pathfinder.Entry2> method757(BlockPos var0, BlockPos var1) {
      return mc.world == null ? Optional.empty() : method759(mc.world, var0, var1, Pathfinder.Data.INSTANCE);
   }

   public static Optional<Pathfinder.Entry2> method758(BlockPos var0, BlockPos var1, Pathfinder.Data var2) {
      return mc.world == null ? Optional.empty() : method759(mc.world, var0, var1, var2);
   }

   public static Optional<Pathfinder.Entry2> method759(World var0, BlockPos var1, BlockPos var2, Pathfinder.Data var3) {
      if (var0 != null && var1 != null && var2 != null && var3 != null) {
         BlockPos var4 = var1.toImmutable();
         BlockPos var5 = var2.toImmutable();
         boolean var6 = var0.isChunkLoaded(var5.getX() >> 4, var5.getZ() >> 4);
         if (!method771(var0, var4)
            && !method771(var0, var5)
            && var0.isChunkLoaded(var4.getX() >> 4, var4.getZ() >> 4)
            && (var3.getType() != Pathfinder.Type.EXACT || var6)) {
            BlockPos var7 = method768(var0, var4, var3.isFlag());
            BlockPos var8 = var3.getIntValue4() == 0 && var6 ? method768(var0, var5, var3.isFlag()) : var5;
            if (var8 == null && var3.getType() == Pathfinder.Type.CLOSEST_REACHABLE) {
               var8 = var5;
            }

            if (var7 == null || var8 == null) {
               return Optional.empty();
            } else if (method772(var7, var8, var3.getIntValue4())) {
               return Optional.of(new Pathfinder.Entry2(List.of(var7)));
            } else {
               HashMap var9 = new HashMap();
               PriorityQueue var10 = new PriorityQueue<>(
                  Comparator.comparingDouble(Pathfinder.Data3::getDoubleValue).thenComparingLong(Pathfinder.Data3::getLongValue)
               );
               Pathfinder.Entry var11 = new Pathfinder.Entry(var7, null, 0.0, method773(var7, var8, var3.getIntValue4()));
               Pathfinder.Entry var12 = var11;
               var9.put(var7, var11);
               long var13 = 0L;
               var10.add(new Pathfinder.Data3(var11, var11.doubleValue3, var13++));
               int var15 = 0;

               while (!var10.isEmpty() && var15 < var3.getIntValue()) {
                  Pathfinder.Data3 var16 = (Pathfinder.Data3)var10.poll();
                  Pathfinder.Entry var17 = var16.getEntry();
                  if (!var17.closed && var16.getDoubleValue() == var17.doubleValue3) {
                     var17.closed = true;
                     var15++;
                     if (method774(var17, var12)) {
                        var12 = var17;
                     }

                     if (method772(var17.blockPos, var8, var3.getIntValue4())) {
                        return Optional.of(new Pathfinder.Entry2(method775(var17)));
                     }

                     for (Pathfinder.Data2 var19 : method763(var0, var17.blockPos, var7, var3)) {
                        Pathfinder.Entry var20 = (Pathfinder.Entry)var9.get(var19.getBlockPos());
                        double var21 = var17.doubleValue2 + var19.getDoubleValue();
                        if (var20 == null || !(var21 >= var20.doubleValue2)) {
                           if (var20 == null) {
                              var20 = new Pathfinder.Entry(var19.getBlockPos(), var17, var21, method773(var19.getBlockPos(), var8, var3.getIntValue4()));
                              var9.put(var19.getBlockPos(), var20);
                           } else {
                              var20.entry = var17;
                              var20.doubleValue2 = var21;
                              var20.doubleValue3 = var21 + var20.doubleValue;
                              var20.closed = false;
                           }

                           if (method774(var20, var12)) {
                              var12 = var20;
                           }

                           var10.add(new Pathfinder.Data3(var20, var20.doubleValue3, var13++));
                        }
                     }
                  }
               }

               return var3.getType() == Pathfinder.Type.CLOSEST_REACHABLE ? Optional.of(new Pathfinder.Entry2(method775(var12))) : Optional.empty();
            }
         } else {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   @Nullable
   public static BlockPos method760(BlockPos var0, BlockPos var1) {
      return method757(var0, var1).map(var1x -> var1x.method778(var0)).orElse(null);
   }

   public static Vec3d method761(BlockPos var0) {
      return new Vec3d(var0.getX() + 0.5, var0.getY() + 0.15, var0.getZ() + 0.5);
   }

   public static boolean method762(World var0, BlockPos var1, boolean var2) {
      return var0 != null && var1 != null && !method771(var0, var1) && var0.isChunkLoaded(var1.getX() >> 4, var1.getZ() >> 4) && method767(var0, var1, var2);
   }

   private static List<Pathfinder.Data2> method763(World var0, BlockPos var1, BlockPos var2, Pathfinder.Data var3) {
      List<Data2> var4 = new ArrayList<>(var3.isFlag2() ? 8 : 4);
      method764(var0, var1, var2, var3, CONST_intValues, var4);
      if (var3.isFlag2()) {
         method764(var0, var1, var2, var3, CONST_intValues2, var4);
      }

      return var4;
   }

   private static void method764(World var0, BlockPos var1, BlockPos var2, Pathfinder.Data var3, int[][] var4, List<Pathfinder.Data2> var5) {
      for (int[] var9 : var4) {
         int var10 = var9[0];
         int var11 = var9[1];
         boolean var12 = var10 != 0 && var11 != 0;
         double var13 = var12 ? CONST_doubleValue : 1.0;
         if (!var12 || method766(var0, var1, var10, var11, var3.isFlag())) {
            BlockPos var15 = var1.add(var10, 0, var11);
            if (method765(var0, var15, var2, var3) && method767(var0, var15, var3.isFlag())) {
               var5.add(new Pathfinder.Data2(var15.toImmutable(), var13));
            } else {
               BlockPos var16 = var1.add(var10, 1, var11);
               if (method765(var0, var16, var2, var3) && method769(var0, var1.up(2), var3.isFlag()) && method767(var0, var16, var3.isFlag())) {
                  var5.add(new Pathfinder.Data2(var16.toImmutable(), var13 + 0.65));
               } else if (method769(var0, var15, var3.isFlag()) && method769(var0, var15.up(), var3.isFlag())) {
                  for (int var17 = 1; var17 <= var3.getIntValue3(); var17++) {
                     BlockPos var18 = var1.add(var10, -var17, var11);
                     if (!method765(var0, var18, var2, var3)) {
                        break;
                     }

                     if (method767(var0, var18, var3.isFlag())) {
                        var5.add(new Pathfinder.Data2(var18.toImmutable(), var13 + 0.2 + var17 * 0.15));
                        break;
                     }

                     if (!method769(var0, var18, var3.isFlag())) {
                        break;
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean method765(World var0, BlockPos var1, BlockPos var2, Pathfinder.Data var3) {
      if (!method771(var0, var1) && var0.isChunkLoaded(var1.getX() >> 4, var1.getZ() >> 4)) {
         int var4 = var1.getX() - var2.getX();
         int var5 = var1.getZ() - var2.getZ();
         return Math.max(Math.abs(var4), Math.abs(var5)) <= var3.getIntValue2();
      } else {
         return false;
      }
   }

   private static boolean method766(World var0, BlockPos var1, int var2, int var3, boolean var4) {
      BlockPos var5 = var1.add(var2, 0, 0);
      BlockPos var6 = var1.add(0, 0, var3);
      return method769(var0, var5, var4) && method769(var0, var5.up(), var4) && method769(var0, var6, var4) && method769(var0, var6.up(), var4);
   }

   private static boolean method767(World var0, BlockPos var1, boolean var2) {
      if (!method769(var0, var1, var2) || !method769(var0, var1.up(), var2)) {
         return false;
      } else if (var2 && !var0.getFluidState(var1).isEmpty()) {
         return true;
      } else {
         BlockPos var3 = var1.down();
         BlockState var4 = var0.getBlockState(var3);
         if (method770(var4)) {
            return false;
         } else {
            VoxelShape var5 = var4.getCollisionShape(var0, var3);
            if (var5.isEmpty()) {
               return false;
            } else {
               double var6 = var5.getMax(Axis.Y);
               return var6 >= 0.499 && var6 <= 1.001;
            }
         }
      }
   }

   @Nullable
   private static BlockPos method768(World var0, BlockPos var1, boolean var2) {
      if (method767(var0, var1, var2)) {
         return var1.toImmutable();
      } else {
         BlockPos var3 = var1.up();
         return !method771(var0, var3) && method767(var0, var3, var2) ? var3.toImmutable() : null;
      }
   }

   private static boolean method769(World var0, BlockPos var1, boolean var2) {
      BlockState var3 = var0.getBlockState(var1);
      if (!method770(var3) && var3.getCollisionShape(var0, var1).isEmpty()) {
         FluidState var4 = var3.getFluidState();
         return var4.isEmpty() || var2;
      } else {
         return false;
      }
   }

   private static boolean method770(BlockState var0) {
      return var0.isOf(Blocks.LAVA)
         || var0.isOf(Blocks.FIRE)
         || var0.isOf(Blocks.SOUL_FIRE)
         || var0.isOf(Blocks.CACTUS)
         || var0.isOf(Blocks.MAGMA_BLOCK)
         || var0.isOf(Blocks.CAMPFIRE)
         || var0.isOf(Blocks.SOUL_CAMPFIRE)
         || var0.isOf(Blocks.SWEET_BERRY_BUSH)
         || var0.isOf(Blocks.COBWEB)
         || var0.isOf(Blocks.POWDER_SNOW);
   }

   private static boolean method771(World var0, BlockPos var1) {
      return var1.getY() < var0.getBottomY() || var1.getY() > var0.getTopYInclusive() - 1;
   }

   private static boolean method772(BlockPos var0, BlockPos var1, int var2) {
      return var0.getX() == var1.getX() && var0.getZ() == var1.getZ() && Math.abs(var0.getY() - var1.getY()) <= var2;
   }

   private static double method773(BlockPos var0, BlockPos var1, int var2) {
      int var3 = Math.abs(var0.getX() - var1.getX());
      int var4 = Math.abs(var0.getZ() - var1.getZ());
      int var5 = Math.min(var3, var4);
      int var6 = Math.max(var3, var4) - var5;
      int var7 = var1.getY() - var0.getY();
      int var8 = Math.max(0, Math.abs(var7) - var2);
      double var9 = var7 > 0 ? var8 * 0.65 : var8 * 0.15;
      return var5 * CONST_doubleValue + var6 + var9;
   }

   private static boolean method774(Pathfinder.Entry var0, Pathfinder.Entry var1) {
      int var2 = Double.compare(var0.doubleValue, var1.doubleValue);
      return var2 < 0 || var2 == 0 && var0.doubleValue2 < var1.doubleValue2;
   }

   private static List<BlockPos> method775(Pathfinder.Entry var0) {
      ArrayList<BlockPos> var1 = new ArrayList<>();

      for (Pathfinder.Entry var2 = var0; var2 != null; var2 = var2.entry) {
         var1.add(var2.blockPos);
      }

      Collections.reverse(var1);
      return var1;
   }

   public static final class Data {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;
      private final boolean flag;
      private final boolean flag2;
      private final Pathfinder.Type type;

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

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public Pathfinder.Type type() {
         return this.type;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.intValue4, other.intValue4) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.type, other.type);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3, intValue4, flag, flag2, type);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "intValue4=" + this.intValue4 + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "type=" + this.type + "]";
      }
      public static final Pathfinder.Data INSTANCE = new Pathfinder.Data(50000, 192, 3, 0, false, true, Pathfinder.Type.EXACT);

      public Data(int var1, int var2, int var3, int var4, boolean var5, boolean var6) {
         this(var1, var2, var3, var4, var5, var6, Pathfinder.Type.EXACT);
      }

      public Data(int var1, int var2, int var3, boolean var4, boolean var5) {
         this(var1, var2, var3, 0, var4, var5, Pathfinder.Type.EXACT);
      }

      public Data(int var1, int var2, int var3, int var4, boolean var5, boolean var6, Pathfinder.Type var7) {
         if (var1 <= 0) {
            throw new IllegalArgumentException("maxVisitedNodes must be positive");
         } else if (var2 <= 0) {
            throw new IllegalArgumentException("maxRange must be positive");
         } else if (var3 < 0) {
            throw new IllegalArgumentException("maxFallDistance cannot be negative");
         } else if (var4 < 0) {
            throw new IllegalArgumentException("goalYTolerance cannot be negative");
         } else if (var7 == null) {
            throw new IllegalArgumentException("pathMode cannot be null");
         } else {
            this.intValue = var1;
            this.intValue2 = var2;
            this.intValue3 = var3;
            this.intValue4 = var4;
            this.flag = var5;
            this.flag2 = var6;
            this.type = var7;
         }
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

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public Pathfinder.Type getType() {
         return this.type;
      }
   }

   private static final class Data2 {
      private final BlockPos blockPos;
      private final double doubleValue;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.doubleValue, other.doubleValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, doubleValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "blockPos=" + this.blockPos + ", " + "doubleValue=" + this.doubleValue + "]";
      }

      private Data2(BlockPos var1, double var2) {
         this.blockPos = var1;
         this.doubleValue = var2;
      }

      public BlockPos getBlockPos() {
         return this.blockPos;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }
   }

   private static final class Data3 {
      private final Pathfinder.Entry entry;
      private final double doubleValue;
      private final long longValue;

      public Pathfinder.Entry entry() {
         return this.entry;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.entry, other.entry) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(entry, doubleValue, longValue);
      }

      @Override
      public String toString() {
         return "Data3[" + "entry=" + this.entry + ", " + "doubleValue=" + this.doubleValue + ", " + "longValue=" + this.longValue + "]";
      }

      private Data3(Pathfinder.Entry var1, double var2, long var4) {
         this.entry = var1;
         this.doubleValue = var2;
         this.longValue = var4;
      }

      public Pathfinder.Entry getEntry() {
         return this.entry;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Entry {
      private final BlockPos blockPos;
      private final double doubleValue;
      private Pathfinder.Entry entry;
      private double doubleValue2;
      private double doubleValue3;
      private boolean closed;

      private Entry(BlockPos var1, Pathfinder.Entry var2, double var3, double var5) {
         this.blockPos = var1;
         this.entry = var2;
         this.doubleValue2 = var3;
         this.doubleValue = var5;
         this.doubleValue3 = var3 + var5;
      }
   }

   public static final class Entry2 {
      private final List<BlockPos> items;
      private int intValue;

      private Entry2(List<BlockPos> var1) {
         this.items = List.copyOf(var1);
         this.intValue = Math.min(1, var1.size());
      }

      public List<BlockPos> getItems() {
         return this.items;
      }

      public BlockPos method776() {
         return this.items.getLast();
      }

      public boolean method777() {
         return this.intValue >= this.items.size();
      }

      @Nullable
      public BlockPos method778(BlockPos var1) {
         for (int var2 = this.intValue; var2 < this.items.size(); var2++) {
            if (this.items.get(var2).equals(var1)) {
               this.intValue = var2 + 1;
               break;
            }
         }

         return this.method777() ? null : this.items.get(this.intValue);
      }

      @Nullable
      public BlockPos method779(Vec3d var1) {
         while (!this.method777()) {
            BlockPos var2 = this.items.get(this.intValue);
            double var3 = var1.x - (var2.getX() + 0.5);
            double var5 = var1.z - (var2.getZ() + 0.5);
            if (!(var3 * var3 + var5 * var5 > 0.2025) && !(Math.abs(var1.y - var2.getY()) > 0.75)) {
               this.intValue++;
               continue;
            }
            break;
         }

         return this.method777() ? null : this.items.get(this.intValue);
      }

      @Nullable
      public Vec3d method780(BlockPos var1) {
         BlockPos var2 = this.method778(var1);
         return var2 == null ? null : Pathfinder.method761(var2);
      }

      @Nullable
      public Vec3d method781(Vec3d var1) {
         BlockPos var2 = this.method779(var1);
         return var2 == null ? null : Pathfinder.method761(var2);
      }

      public void reset() {
         this.intValue = Math.min(1, this.items.size());
      }
   }

   public static enum Type {
      EXACT,
      CLOSEST_REACHABLE;
   }
}

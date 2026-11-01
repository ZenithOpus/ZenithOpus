package org.zenith.utility.game;

import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.chunk.WorldChunk;

public final class FlakeState {
   public static final double CONST_doubleValue = Double.NEGATIVE_INFINITY;
   private static final int CONST_intValue = 12;
   private static final int CONST_intValue2 = 30000000;
   private int intValue;
   private int intValue2;
   private final int intValue3;
   private final int intValue4;
   private final double[] doubleValues;
   private final double[] doubleValues2;
   private final boolean[] flags;
   private final boolean[] flags2;
   private static long longValue;
   private long longValue2;
   private WorldChunk worldChunk;
   private int intValue5;
   private int intValue6;

   private FlakeState(int var1, int var2, int var3) {
      this.longValue2 = ++longValue;
      this.intValue = var1;
      this.intValue2 = var2;
      this.intValue3 = var3;
      this.intValue4 = var3 * 2 + 1;
      this.doubleValues = new double[this.intValue4 * this.intValue4];
      this.doubleValues2 = new double[this.intValue4 * this.intValue4];
      this.flags = new boolean[this.intValue4 * this.intValue4];
      this.flags2 = new boolean[this.intValue4 * this.intValue4];
   }

   public static FlakeState method4535(World var0, int var1, int var2, int var3) {
      FlakeState var4 = new FlakeState(var1, var2, var3);
      var4.method4536(var0, var1, var2);
      return var4;
   }

   public void method4536(World var1, int var2, int var3) {
      boolean var4 = this.intValue != var2 || this.intValue2 != var3;
      this.intValue = var2;
      this.intValue2 = var3;
      this.worldChunk = null;
      Mutable var5 = new Mutable();

      for (int var6 = -this.intValue3; var6 <= this.intValue3; var6++) {
         for (int var7 = -this.intValue3; var7 <= this.intValue3; var7++) {
            var4 |= this.method4540(var1, var5, var2 + var6, var3 + var7, this.method4550(var6, var7));
         }
      }

      this.worldChunk = null;
      if (var4) {
         this.longValue2 = ++longValue;
      }
   }

   public void method4537(World var1, int var2, int var3) {
      int var4 = var2 - this.intValue;
      int var5 = var3 - this.intValue2;
      if (var4 != 0 || var5 != 0) {
         if (Math.abs(var4) < this.intValue4 && Math.abs(var5) < this.intValue4) {
            this.intValue = var2;
            this.intValue2 = var3;
            int var6 = Math.max(0, -var4);
            int var7 = Math.min(this.intValue4 - 1, this.intValue4 - 1 - var4);
            int var8 = Math.max(0, -var5);
            int var9 = Math.min(this.intValue4 - 1, this.intValue4 - 1 - var5);
            int var10 = var9 - var8 + 1;
            int var11 = var8 + var5;
            if (var4 >= 0) {
               for (int var12 = var6; var12 <= var7; var12++) {
                  this.method4539(var12 + var4, var11, var12, var8, var10);
               }
            } else {
               for (int var16 = var7; var16 >= var6; var16--) {
                  this.method4539(var16 + var4, var11, var16, var8, var10);
               }
            }

            this.worldChunk = null;
            Mutable var17 = new Mutable();

            for (int var13 = 0; var13 < this.intValue4; var13++) {
               boolean var14 = var13 >= var6 && var13 <= var7;

               for (int var15 = 0; var15 < this.intValue4; var15++) {
                  if (!var14 || var15 < var8 || var15 > var9) {
                     this.method4540(var1, var17, var2 + var13 - this.intValue3, var3 + var15 - this.intValue3, var13 * this.intValue4 + var15);
                  }
               }
            }

            this.worldChunk = null;
         } else {
            this.method4536(var1, var2, var3);
         }
      }
   }

   public void method4538(World var1, int var2, int var3) {
      int var4 = var2 - this.intValue;
      int var5 = var3 - this.intValue2;
      if (var4 >= -this.intValue3 && var4 <= this.intValue3 && var5 >= -this.intValue3 && var5 <= this.intValue3) {
         this.worldChunk = null;
         boolean var6 = this.method4540(var1, new Mutable(), var2, var3, this.method4550(var4, var5));
         this.worldChunk = null;
         if (var6) {
            this.longValue2 = ++longValue;
         }
      }
   }

   private void method4539(int var1, int var2, int var3, int var4, int var5) {
      int var6 = var1 * this.intValue4 + var2;
      int var7 = var3 * this.intValue4 + var4;
      System.arraycopy(this.doubleValues, var6, this.doubleValues, var7, var5);
      System.arraycopy(this.doubleValues2, var6, this.doubleValues2, var7, var5);
      System.arraycopy(this.flags, var6, this.flags, var7, var5);
      System.arraycopy(this.flags2, var6, this.flags2, var7, var5);
   }

   private boolean method4540(World var1, Mutable var2, int var3, int var4, int var5) {
      double var6 = this.doubleValues[var5];
      double var8 = this.doubleValues2[var5];
      boolean var10 = this.flags[var5];
      boolean var11 = this.flags2[var5];
      this.method4541(var1, var2, var3, var4, var5);
      return this.doubleValues[var5] != var6 || this.doubleValues2[var5] != var8 || this.flags[var5] != var10 || this.flags2[var5] != var11;
   }

   private void method4541(World var1, Mutable var2, int var3, int var4, int var5) {
      this.flags[var5] = false;
      this.flags2[var5] = false;
      int var6 = var3 >> 4;
      int var7 = var4 >> 4;
      if (!var1.isChunkLoaded(var6, var7)) {
         this.doubleValues[var5] = Double.NEGATIVE_INFINITY;
         this.doubleValues2[var5] = Double.NEGATIVE_INFINITY;
      } else {
         WorldChunk var8 = null;
         int var9;
         if (var3 >= -30000000 && var4 >= -30000000 && var3 < 30000000 && var4 < 30000000) {
            if (this.worldChunk == null || this.intValue5 != var6 || this.intValue6 != var7) {
               this.worldChunk = var1.getChunk(var6, var7);
               this.intValue5 = var6;
               this.intValue6 = var7;
            }

            var8 = this.worldChunk;
            var9 = var8.sampleHeightmap(Type.MOTION_BLOCKING, var3 & 15, var4 & 15) + 1;
         } else {
            var9 = var1.getTopY(Type.MOTION_BLOCKING, var3, var4);
         }

         int var10 = var1.getBottomY();
         if (var9 <= var10) {
            this.doubleValues[var5] = Double.NEGATIVE_INFINITY;
            this.doubleValues2[var5] = Double.NEGATIVE_INFINITY;
         } else {
            this.doubleValues2[var5] = var9;
            this.doubleValues[var5] = Double.NEGATIVE_INFINITY;
            int var11 = Math.max(var10, var9 - 12);

            for (int var12 = var9 - 1; var12 >= var11; var12--) {
               var2.set(var3, var12, var4);
               BlockState var13 = var8 != null ? var8.getBlockState(var2) : var1.getBlockState(var2);
               if (!var13.getFluidState().isEmpty() || var13.isIn(BlockTags.ICE)) {
                  this.doubleValues[var5] = var12 + 1;
                  return;
               }

               if (var13.isIn(BlockTags.LEAVES)) {
                  this.doubleValues[var5] = var12 + 1;
                  this.flags[var5] = true;
                  this.flags2[var5] = true;
                  return;
               }

               if (var13.isSolidBlock(var1, var2)) {
                  this.doubleValues[var5] = var12 + 1;
                  this.flags[var5] = true;
                  return;
               }
            }
         }
      }
   }

   public static FlakeState method4542(int var0) {
      FlakeState var1 = new FlakeState(0, 0, var0);

      for (int var2 = -var0; var2 <= var0; var2++) {
         for (int var3 = -var0; var3 <= var0; var3++) {
            int var4 = var1.method4550(var2, var3);
            var1.doubleValues[var4] = method4543(var2, var3);
            var1.doubleValues2[var4] = var1.doubleValues[var4];
            var1.flags[var4] = true;
         }
      }

      return var1;
   }

   private static double method4543(int var0, int var1) {
      double var2 = 0.0;
      if (var1 <= -2) {
         var2++;
      }

      if (var1 <= -4 && Math.abs(var0) <= 3) {
         var2++;
      }

      if (var0 >= 3 && var1 >= 1) {
         var2++;
      }

      return var2;
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

   public long getLongValue2() {
      return this.longValue2;
   }

   public double method4544(int var1, int var2) {
      return var1 >= -this.intValue3 && var1 <= this.intValue3 && var2 >= -this.intValue3 && var2 <= this.intValue3
         ? this.doubleValues[this.method4550(var1, var2)]
         : Double.NEGATIVE_INFINITY;
   }

   public boolean method4545(int var1, int var2) {
      return var1 >= -this.intValue3 && var1 <= this.intValue3 && var2 >= -this.intValue3 && var2 <= this.intValue3
         ? this.flags2[this.method4550(var1, var2)]
         : false;
   }

   public boolean method4546(int var1, int var2) {
      return var1 >= -this.intValue3 && var1 <= this.intValue3 && var2 >= -this.intValue3 && var2 <= this.intValue3
         ? this.flags[this.method4550(var1, var2)]
         : false;
   }

   public double method4547(double var1, double var3) {
      return this.method4544((int)Math.floor(var1) - this.intValue, (int)Math.floor(var3) - this.intValue2);
   }

   public double method4548(double var1, double var3) {
      return this.method4549((int)Math.floor(var1), (int)Math.floor(var3));
   }

   public double method4549(int var1, int var2) {
      int var3 = var1 - this.intValue;
      int var4 = var2 - this.intValue2;
      return var3 >= -this.intValue3 && var3 <= this.intValue3 && var4 >= -this.intValue3 && var4 <= this.intValue3
         ? this.doubleValues2[this.method4550(var3, var4)]
         : Double.NEGATIVE_INFINITY;
   }

   private int method4550(int var1, int var2) {
      return (var1 + this.intValue3) * this.intValue4 + var2 + this.intValue3;
   }
}

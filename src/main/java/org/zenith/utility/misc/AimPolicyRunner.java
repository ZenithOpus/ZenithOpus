package org.zenith.utility.misc;

import java.util.Arrays;
import java.util.Random;
import org.zenith.utility.api.AimPolicy;

public final class AimPolicyRunner {
   private static final float CONST_floatValue = -8.0F;
   private static final float CONST_floatValue2 = 4.0F;
   private final AimPolicy aimPolicy;
   private final Random random = new Random();
   private final float[] floatValues;
   private final float[] floatValues2;
   private final float[] floatValues3;
   private final float[] floatValues4;
   private final float[] floatValues5;
   private final float[] floatValues6;
   private final float[] floatValues7;
   private final float[] floatValues8;
   private final float[] floatValues9;
   private final float[] floatValues10;
   private final float[] floatValues11;
   private final float[] floatValues12;
   private final float[] floatValues13;
   private final double[] doubleValues;
   private final int[] intValues;
   private final float[] floatValues14;
   private final float[] floatValues15;
   private final float[] floatValues16;
   private final float[] floatValues17;
   private final float[] floatValues18;
   private boolean flag;
   private int intValue;
   private int intValue2;

   public AimPolicyRunner(AimPolicy var1) {
      this.aimPolicy = var1;
      int var2 = Math.max(this.aimPolicy.intValue5, this.aimPolicy.intValue6);
      this.floatValues = new float[this.aimPolicy.intValue3];
      this.floatValues2 = new float[this.aimPolicy.intValue3];
      this.floatValues3 = new float[this.aimPolicy.intValue4];
      this.floatValues4 = new float[this.aimPolicy.intValue2];
      this.floatValues5 = new float[this.aimPolicy.intValue4];
      this.floatValues6 = new float[4 * this.aimPolicy.intValue3];
      this.floatValues7 = new float[4 * this.aimPolicy.intValue3];
      this.floatValues8 = new float[this.aimPolicy.intValue4 + 2 * this.aimPolicy.intValue9 + this.aimPolicy.intValue10];
      this.floatValues9 = new float[3 * this.aimPolicy.intValue4];
      this.floatValues10 = new float[3 * this.aimPolicy.intValue4];
      this.floatValues11 = new float[this.aimPolicy.intValue4 + this.aimPolicy.intValue9];
      this.floatValues12 = new float[this.aimPolicy.intValue4];
      this.floatValues13 = new float[var2];
      this.doubleValues = new double[var2];
      this.intValues = new int[2 * this.aimPolicy.intValue];
      this.floatValues14 = new float[this.intValues.length];
      this.floatValues15 = new float[this.aimPolicy.intValue10];
      this.floatValues16 = new float[this.aimPolicy.intValue4 + 2 * this.aimPolicy.intValue9];
      this.floatValues17 = new float[this.aimPolicy.intValue11];
      this.floatValues18 = new float[2 * this.aimPolicy.intValue10];
      this.reset();
   }

   public void reset() {
      Arrays.fill(this.floatValues, 0.0F);
      Arrays.fill(this.floatValues2, 0.0F);
      Arrays.fill(this.floatValues3, 0.0F);
      Arrays.fill(this.floatValues15, 0.0F);
      this.flag = false;
      this.intValue = this.aimPolicy.intValue7;
      this.intValue2 = this.aimPolicy.intValue8;
   }

   public int[][] method6749(float[] var1) {
      return this.method6751(var1, false);
   }

   public int[][] method6750(float[] var1) {
      return this.method6751(var1, true);
   }

   private int[][] method6751(float[] var1, boolean var2) {
      this.method6753(var1);
      this.method6754();
      method6762(this.aimPolicy.floatValues5, this.floatValues, this.aimPolicy.floatValues6, this.floatValues5);
      method6763(this.floatValues5);
      int[][] var3 = new int[this.aimPolicy.intValue][2];

      for (int var4 = 0; var4 < this.aimPolicy.intValue; var4++) {
         int var5 = 0;
         System.arraycopy(this.floatValues5, 0, this.floatValues8, var5, this.floatValues5.length);
         var5 += this.floatValues5.length;
         System.arraycopy(this.aimPolicy.floatValues7[this.intValue], 0, this.floatValues8, var5, this.aimPolicy.intValue9);
         var5 += this.aimPolicy.intValue9;
         System.arraycopy(this.aimPolicy.floatValues8[this.intValue2], 0, this.floatValues8, var5, this.aimPolicy.intValue9);
         var5 += this.aimPolicy.intValue9;
         System.arraycopy(this.floatValues15, 0, this.floatValues8, var5, this.aimPolicy.intValue10);
         this.method6755();
         method6762(this.aimPolicy.floatValues13, this.floatValues3, this.aimPolicy.floatValues14, this.floatValues13);
         int var6 = var2 ? method6765(this.floatValues13, this.aimPolicy.intValue5) : this.method6766(this.floatValues13, this.aimPolicy.intValue5);
         int var7 = 2 * var4;
         this.intValues[var7] = var6;
         this.floatValues14[var7] = this.floatValues13[var6];
         System.arraycopy(this.floatValues3, 0, this.floatValues11, 0, this.aimPolicy.intValue4);
         System.arraycopy(this.aimPolicy.floatValues7[var6], 0, this.floatValues11, this.aimPolicy.intValue4, this.aimPolicy.intValue9);
         method6762(this.aimPolicy.floatValues15, this.floatValues11, this.aimPolicy.floatValues16, this.floatValues12);
         method6763(this.floatValues12);
         method6762(this.aimPolicy.floatValues17, this.floatValues12, this.aimPolicy.floatValues18, this.floatValues13);
         int var8 = var2 ? method6765(this.floatValues13, this.aimPolicy.intValue6) : this.method6766(this.floatValues13, this.aimPolicy.intValue6);
         this.intValues[var7 + 1] = var8;
         this.floatValues14[var7 + 1] = this.floatValues13[var8];
         var3[var4][0] = var2 ? this.method6769(0, var6) : this.method6768(0, var6);
         var3[var4][1] = var2 ? this.method6769(1, var8) : this.method6768(1, var8);
         this.intValue = var6;
         this.intValue2 = var8;
         boolean var9 = var6 == this.aimPolicy.intValue7 && var8 == this.aimPolicy.intValue8;
         if (this.flag && var9) {
            this.flag = false;
            Arrays.fill(this.floatValues15, 0.0F);
         } else if (!this.flag && !var9) {
            this.flag = true;
            this.method6752(var6, var8, var2);
         }
      }

      return var3;
   }

   private void method6752(int var1, int var2, boolean var3) {
      int var4 = 0;
      System.arraycopy(this.floatValues5, 0, this.floatValues16, var4, this.floatValues5.length);
      var4 += this.floatValues5.length;
      System.arraycopy(this.aimPolicy.floatValues7[var1], 0, this.floatValues16, var4, this.aimPolicy.intValue9);
      var4 += this.aimPolicy.intValue9;
      System.arraycopy(this.aimPolicy.floatValues8[var2], 0, this.floatValues16, var4, this.aimPolicy.intValue9);
      method6762(this.aimPolicy.floatValues19, this.floatValues16, this.aimPolicy.floatValues20, this.floatValues17);
      method6763(this.floatValues17);
      method6762(this.aimPolicy.floatValues21, this.floatValues17, this.aimPolicy.floatValues22, this.floatValues18);

      for (int var5 = 0; var5 < this.aimPolicy.intValue10; var5++) {
         if (var3) {
            this.floatValues15[var5] = this.floatValues18[var5];
         } else {
            float var6 = Math.max(-8.0F, Math.min(4.0F, this.floatValues18[this.aimPolicy.intValue10 + var5]));
            this.floatValues15[var5] = this.floatValues18[var5] + (float)(Math.exp(0.5 * var6) * this.random.nextGaussian());
         }
      }
   }

   private void method6753(float[] var1) {
      for (int var2 = 0; var2 < this.aimPolicy.entrys.length; var2++) {
         AimPolicy.Entry var3 = this.aimPolicy.entrys[var2];
         double var4 = var1[var3.intValue];
         if (var3.flag) {
            var4 = Math.signum(var4) * Math.log1p(Math.abs(var4) / var3.floatValue);
         }

         this.floatValues4[var2] = (float)((var4 - var3.floatValue2) / var3.floatValue3);
      }
   }

   private void method6754() {
      method6762(this.aimPolicy.floatValues, this.floatValues4, this.aimPolicy.floatValues3, this.floatValues6);
      method6762(this.aimPolicy.floatValues2, this.floatValues, this.aimPolicy.floatValues4, this.floatValues7);
      int var1 = this.aimPolicy.intValue3;

      for (int var2 = 0; var2 < var1; var2++) {
         double var3 = method6764(this.floatValues6[var2] + this.floatValues7[var2]);
         double var5 = method6764(this.floatValues6[var1 + var2] + this.floatValues7[var1 + var2]);
         double var7 = Math.tanh(this.floatValues6[2 * var1 + var2] + this.floatValues7[2 * var1 + var2]);
         double var9 = method6764(this.floatValues6[3 * var1 + var2] + this.floatValues7[3 * var1 + var2]);
         double var11 = var5 * this.floatValues2[var2] + var3 * var7;
         this.floatValues2[var2] = (float)var11;
         this.floatValues[var2] = (float)(var9 * Math.tanh(var11));
      }
   }

   private void method6755() {
      method6762(this.aimPolicy.floatValues9, this.floatValues8, this.aimPolicy.floatValues11, this.floatValues9);
      method6762(this.aimPolicy.floatValues10, this.floatValues3, this.aimPolicy.floatValues12, this.floatValues10);
      int var1 = this.aimPolicy.intValue4;

      for (int var2 = 0; var2 < var1; var2++) {
         double var3 = method6764(this.floatValues9[var2] + this.floatValues10[var2]);
         double var5 = method6764(this.floatValues9[var1 + var2] + this.floatValues10[var1 + var2]);
         double var7 = Math.tanh(this.floatValues9[2 * var1 + var2] + var3 * this.floatValues10[2 * var1 + var2]);
         this.floatValues3[var2] = (float)((1.0 - var5) * var7 + var5 * this.floatValues3[var2]);
      }
   }

   public int[] method6756() {
      return (int[])this.intValues.clone();
   }

   public float method6757() {
      return this.floatValues14[0];
   }

   public double method6758() {
      return method6761(this.floatValues);
   }

   public double method6759() {
      return method6761(this.floatValues3);
   }

   public double method6760() {
      return method6761(this.floatValues15);
   }

   public boolean isFlag() {
      return this.flag;
   }

   private static double method6761(float[] var0) {
      double var1 = 0.0;

      for (float var6 : var0) {
         var1 += (double)var6 * var6;
      }

      return Math.sqrt(var1);
   }

   private static void method6762(float[][] var0, float[] var1, float[] var2, float[] var3) {
      for (int var4 = 0; var4 < var0.length; var4++) {
         double var5 = var2[var4];

         for (int var7 = 0; var7 < var0[var4].length; var7++) {
            var5 += var0[var4][var7] * var1[var7];
         }

         var3[var4] = (float)var5;
      }
   }

   private static void method6763(float[] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         var0[var1] = (float)Math.tanh(var0[var1]);
      }
   }

   private static double method6764(double var0) {
      return 1.0 / (1.0 + Math.exp(-var0));
   }

   private static int method6765(float[] var0, int var1) {
      int var2 = 0;

      for (int var3 = 1; var3 < var1; var3++) {
         if (var0[var3] > var0[var2]) {
            var2 = var3;
         }
      }

      return var2;
   }

   private int method6766(float[] var1, int var2) {
      double var3 = Math.max((double)this.aimPolicy.floatValue, 1.0E-6);
      double var5 = Double.NEGATIVE_INFINITY;

      for (int var7 = 0; var7 < var2; var7++) {
         var5 = Math.max(var5, var1[var7] / var3);
      }

      double var14 = 0.0;

      for (int var9 = 0; var9 < var2; var9++) {
         this.doubleValues[var9] = Math.exp(var1[var9] / var3 - var5);
         var14 += this.doubleValues[var9];
      }

      for (int var15 = 0; var15 < var2; var15++) {
         this.doubleValues[var15] = this.doubleValues[var15] / var14;
      }

      if (this.aimPolicy.floatValue2 < 1.0F) {
         this.method6767(var2);
      }

      double var16 = this.random.nextDouble();
      double var11 = 0.0;

      for (int var13 = 0; var13 < var2; var13++) {
         var11 += this.doubleValues[var13];
         if (var16 <= var11) {
            return var13;
         }
      }

      return var2 - 1;
   }

   private void method6767(int var1) {
      Integer[] var2 = new Integer[var1];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = var3;
      }

      Arrays.sort(var2, (var1x, var2x) -> Double.compare(this.doubleValues[var2x], this.doubleValues[var1x]));
      double var10 = 0.0;
      boolean var5 = false;
      Integer[] var6 = var2;
      int var7 = var2.length;

      for (int var8 = 0; var8 < var7; var8++) {
         int var9 = var6[var8];
         if (var5) {
            this.doubleValues[var9] = 0.0;
         } else {
            var10 += this.doubleValues[var9];
            if (var10 >= this.aimPolicy.floatValue2) {
               var5 = true;
            }
         }
      }

      for (int var11 = 0; var11 < var1; var11++) {
         this.doubleValues[var11] = this.doubleValues[var11] / var10;
      }
   }

   private int method6768(int var1, int var2) {
      int var3 = this.aimPolicy.intValues[var1][var2];
      int var4 = this.aimPolicy.intValues2[var1][var2];
      if (var3 == var4) {
         return var3;
      } else {
         int var5 = Math.min(Math.abs(var3), Math.abs(var4));
         int var6 = Math.max(Math.abs(var3), Math.abs(var4));
         double var7 = Math.log(var5) + this.random.nextDouble() * (Math.log(var6 + 1.0) - Math.log(var5));
         int var9 = Math.max(var5, Math.min(var6, (int)Math.exp(var7)));
         return var3 > 0 ? var9 : -var9;
      }
   }

   private int method6769(int var1, int var2) {
      int var3 = this.aimPolicy.intValues[var1][var2];
      int var4 = this.aimPolicy.intValues2[var1][var2];
      if (var3 == var4) {
         return var3;
      } else {
         int var5 = Math.min(Math.abs(var3), Math.abs(var4));
         int var6 = Math.max(Math.abs(var3), Math.abs(var4));
         int var7 = Math.max(var5, Math.min(var6, (int)Math.round(Math.sqrt((double)var5 * var6))));
         return var3 > 0 ? var7 : -var7;
      }
   }
}

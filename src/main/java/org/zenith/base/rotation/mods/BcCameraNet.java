package org.zenith.base.rotation.mods;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Random;

public final class BcCameraNet {
   public static final String text = "neurotest-bc-camera-v1";
   public static final double CONST_doubleValue = 1.0E-4;
   public static final double CONST_doubleValue2 = 0.002;
   public int intValue;
   public int intValue2;
   public int layers;
   public int intValue3;
   public int intValue4;
   public int intValue5;
   public int clip;
   public int intValue6;
   public boolean flag;
   public boolean flag2;
   public int[] intValues = new int[0];
   public double doubleValue;
   public volatile double doubleValue2 = -1.0;
   public double[] doubleValues;
   public double[] doubleValues2;
   public double doubleValue3;
   public double doubleValue4 = 45.0;
   public boolean flag3;
   public double[][] doubleValues3;
   public BcCameraNet.Entry3 entry3;
   public double doubleValue5 = 1.0;
   public double[] doubleValues4;
   public static final double CONST_doubleValue3 = 1.62;
   public static final double CONST_doubleValue4 = 1.27;
   public static final double CONST_doubleValue5 = 0.6;
   public static final double CONST_doubleValue6 = 1.8;
   public static final double CONST_doubleValue7 = 1.5;
   public double[][] doubleValues5;
   public double[] doubleValues6;
   public double[] doubleValues7;
   public double[][][] doubleValues8;
   public double[][][] doubleValues9;
   public double[][] doubleValues10;
   public double[][] doubleValues11;
   public double[][] doubleValues12;
   public double[][] doubleValues13;
   public double[][] doubleValues14;
   public double[][] doubleValues15;
   public double[][] doubleValues16;
   public double[] doubleValues17;
   public double[] doubleValues18;
   public double[] doubleValues19;
   public double[] doubleValues20;
   public String text2 = "";
   public String source = "?";
   public BcCameraNet.Entry[] entrys = new BcCameraNet.Entry[0];
   public int intValue7;
   public double doubleValue6;

   public double[] method3344(double[] var1) {
      double var2 = 1.0E9;
      if (this.doubleValue3 > 0.0 && !(var1[21] <= 0.5) && Math.abs(method3351(var1[23])) < this.doubleValue4) {
         double var4 = method3351(var1[23]);
         double var6 = method3351(var1[26]);
         double var8 = method3351(var1[27]);
         double var10 = Math.max(Math.abs(var4), Math.exp(var1[28] * 3.0) + this.doubleValue3);
         return new double[]{var4 - var10, var4 + var10, Math.min(var6 - this.doubleValue3, 0.0), Math.max(var8 + this.doubleValue3, 0.0)};
      } else {
         return new double[]{-var2, var2, -var2, var2};
      }
   }

   public double method3345(int var1) {
      return var1 == 0 ? 0.0 : (var1 <= this.intValue4 ? this.doubleValues4[var1 - 1] : -this.doubleValues4[var1 - this.intValue4]);
   }

   public double method3346(int var1) {
      return var1 == 0 ? 0.0 : (var1 <= this.intValue4 ? this.doubleValues4[var1] : -this.doubleValues4[var1 - 1 - this.intValue4]);
   }

   public double[] method3347(double[] var1, double var2, double var4) {
      double[] var6 = (double[])var1.clone();

      for (int var7 = 0; var7 < var6.length; var7++) {
         if (this.method3346(var7) < var2 || this.method3345(var7) > var4) {
            var6[var7] = -1.0E9;
         }
      }

      return var6;
   }

   public double[] method3348(BcCameraNet.Entry2 var1, double[] var2, double[] var3, double var4, Random var6) {
      double[] var7 = this.method3344(var2);
      double[] var8 = this.method3373(var1, var3);
      int var9 = this.method3377(this.method3347(this.method3375(var8), var7[0], var7[1]), var6.nextDouble());
      int var10 = this.method3377(this.method3347(this.method3376(var8, var9), var7[2], var7[3]), var6.nextDouble());
      double var11 = Math.max(var7[0], Math.min(var7[1], this.method3378(var9, var4, var6)));
      double var13 = Math.max(var7[2], Math.min(var7[3], this.method3378(var10, var4, var6)));
      return new double[]{var11, var13};
   }

   public double[] method3349(double[] var1, double var2, double var4) {
      double[] var6 = this.method3347(var1, var2, var4);

      for (double var10 : var6) {
         if (var10 > -1.0E8) {
            return var6;
         }
      }

      return var1;
   }

   public double[] method3350(BcCameraNet.Entry2 var1, double[] var2, double[] var3, double var4, Random var6) {
      BcCameraNet.Entry3 var7 = this.entry3;
      if (var1.doubleValues3 == null) {
         var1.doubleValues3 = var7.doubleValues[var6.nextInt(var7.doubleValues.length)];
         var1.doubleValues4 = new double[var7.intValue];
         var1.doubleValues5 = new double[var7.intValue];
         var1.flags = new boolean[var7.intValue];
      }

      if (var1.intValue == var7.intValue) {
         System.arraycopy(var1.doubleValues4, 1, var1.doubleValues4, 0, var7.intValue - 1);
         System.arraycopy(var1.doubleValues5, 1, var1.doubleValues5, 0, var7.intValue - 1);
         System.arraycopy(var1.flags, 1, var1.flags, 0, var7.intValue - 1);
         var1.intValue--;
      }

      var1.doubleValues4[var1.intValue] = Math.sinh(var2[1] * 4.0) * 0.5;
      var1.doubleValues5[var1.intValue] = Math.sinh(var2[3] * 4.0) * 0.5;
      var1.flags[var1.intValue] = var2[4] > 0.5;
      var1.intValue++;
      double[] var8 = var7.method3397(
         var1.doubleValues4, var1.doubleValues5, var1.flags, var1.intValue, var1.doubleValues3, var6.nextDouble(), var6.nextDouble()
      );
      double[] var9 = this.method3373(var1, var3);
      int var10 = this.method3377(this.method3349(this.method3375(var9), var8[0], var8[1]), var6.nextDouble());
      int var11 = this.method3377(this.method3349(this.method3376(var9, var10), var8[2], var8[3]), var6.nextDouble());
      double var12 = this.method3378(var10, var4, var6);
      double var14 = this.method3378(var11, var4, var6);
      double var16 = Math.max(var8[0], Math.min(var8[1], var12));
      double var18 = Math.max(var8[2], Math.min(var8[3], var14));
      var7.longValue++;
      if (Math.abs(var16 - var12) > 1.0E-9 || Math.abs(var18 - var14) > 1.0E-9) {
         var7.longValue2++;
      }

      return new double[]{var16, var18};
   }

   public static double method3351(double var0) {
      return Math.sinh(var0 * 3.0) * 2.0;
   }

   public double[] method3352(double[] var1, double var2, double var4) {
      if (this.doubleValue3 > 0.0 && !(var1[21] <= 0.5)) {
         double var6 = method3351(var1[23]);
         double var8 = method3351(var1[26]);
         double var10 = method3351(var1[27]);
         double var12 = Math.exp(var1[28] * 3.0);
         if (!(Math.abs(var6) < this.doubleValue4)) {
            return new double[]{var2, var4};
         } else {
            double var14 = Math.max(Math.abs(var6), var12 + this.doubleValue3);
            double var16 = var6 - var2;
            if (Math.abs(var16) > var14) {
               var2 = var6 - Math.signum(var16) * var14;
            }

            double var18 = Math.min(var8 - this.doubleValue3, 0.0);
            double var20 = Math.max(var10 + this.doubleValue3, 0.0);
            var4 = Math.max(var18, Math.min(var20, var4));
            return new double[]{var2, var4};
         }
      } else {
         return new double[]{var2, var4};
      }
   }

   public double method3353(double[] var1) {
      if (this.doubleValue2 >= 0.0) {
         return var1[21] > 0.5 ? this.doubleValue2 : 0.0;
      } else if (this.doubleValues2 == null) {
         return 1.0;
      } else if (var1[21] <= 0.5) {
         return 0.0;
      } else {
         double var2 = Math.abs(Math.sinh(var1[23] * 3.0) * 2.0);
         int var4 = 0;

         while (var4 + 1 < this.doubleValues.length && this.doubleValues[var4 + 1] <= var2) {
            var4++;
         }

         var4 = Math.min(var4, this.doubleValues2.length - 1);
         return Math.min(1.0, this.doubleValue * this.doubleValues2[var4]);
      }
   }

   public int method3354() {
      return (this.flag ? 7 : 0) + (this.flag2 ? 1 : 0);
   }

   public BcCameraNet.Entry2 method3355() {
      return new BcCameraNet.Entry2();
   }

   public static BcCameraNet method3356(int var0, int var1, int var2) {
      BcCameraNet var3 = new BcCameraNet();
      var3.intValue = var0;
      var3.clip = var1;
      var3.intValue6 = var2;
      return var3;
   }

   public static double method3357(double var0) {
      double var2 = (var0 + 180.0) % 360.0;
      if (var2 < 0.0) {
         var2 += 360.0;
      }

      return var2 - 180.0;
   }

   public static double asinh(double var0) {
      double var2 = Math.abs(var0);
      return Math.copySign(Math.log(var2 + Math.sqrt(var2 * var2 + 1.0)), var0);
   }

   public static double method3358(double var0, double var2, double var4) {
      return Math.max(var2, Math.min(var4, var0));
   }

   public double method3359(double var1) {
      return Math.log1p(var1) / Math.log1p(this.clip);
   }

   public double method3360(boolean[] var1) {
      int var2 = var1.length - 1;

      for (int var3 = var2; var3 >= 0; var3--) {
         if (var1[var3]) {
            return Math.min(var2 - var3, this.clip);
         }
      }

      return this.clip;
   }

   public static boolean method3361(double var0, double var2, double var4) {
      return !Double.isNaN(var0) && !Double.isNaN(var2) && !Double.isNaN(var4);
   }

   public static double[] method3362(double var0, double var2, double var4) {
      return new double[]{Math.toDegrees(Math.atan2(-var0, var4)), -Math.toDegrees(Math.atan2(var2, Math.hypot(var0, var4)))};
   }

   public static double[] method3363(double var0, double var2) {
      double var4 = Math.toRadians(var0);
      double var6 = Math.toRadians(var2);
      return new double[]{-Math.sin(var4) * Math.cos(var6), -Math.sin(var6), Math.cos(var4) * Math.cos(var6)};
   }

   public static boolean method3364(double[] var0, double[] var1, double[] var2, double var3) {
      double[] var5 = new double[]{var2[0] - 0.3, var2[1], var2[2] - 0.3};
      double[] var6 = new double[]{var2[0] + 0.3, var2[1] + var3, var2[2] + 0.3};
      double var7 = Double.NEGATIVE_INFINITY;
      double var9 = Double.POSITIVE_INFINITY;
      boolean var11 = false;
      boolean var12 = false;

      for (int var13 = 0; var13 < 3; var13++) {
         double var14 = 1.0 / var1[var13];
         double var16 = (var5[var13] - var0[var13]) * var14;
         double var18 = (var6[var13] - var0[var13]) * var14;
         double var20 = !Double.isNaN(var16) && !Double.isNaN(var18) ? Math.min(var16, var18) : Double.NaN;
         double var22 = !Double.isNaN(var16) && !Double.isNaN(var18) ? Math.max(var16, var18) : Double.NaN;
         if (!Double.isNaN(var20)) {
            var7 = var11 ? Math.max(var7, var20) : var20;
            var11 = true;
         }

         if (!Double.isNaN(var22)) {
            var9 = var12 ? Math.min(var9, var22) : var22;
            var12 = true;
         }
      }

      return var11 && var12 ? var9 >= Math.max(var7, 0.0) && var7 < 6.0 : false;
   }

   public double[] method3365(BcCameraNet.Entry2 var1, double var2) {
      int var4 = var1.size();
      BcCameraNet.Entry4[] var5 = new BcCameraNet.Entry4[var4];

      for (int var6 = 0; var6 < var4; var6++) {
         var5[var6] = var1.method3395(var4 - 1 - var6);
      }

      double[] var53 = new double[this.intValue];
      BcCameraNet.Entry4 var7 = var5[var4 - 1];
      double var8 = var7.flag5 ? var7.doubleValue9 : 0.0;
      double var10 = var7.flag5 ? var7.doubleValue10 : 0.0;
      var53[0] = method3358(var8 / 10.0, -6.0, 6.0);
      var53[1] = asinh(var8 / 0.5) / 4.0;
      var53[2] = method3358(var10 / 10.0, -6.0, 6.0);
      var53[3] = asinh(var10 / 0.5) / 4.0;
      var53[4] = var7.flag5 ? 1.0 : 0.0;
      var53[5] = var7.doubleValue8 / 90.0;
      var53[6] = Math.log(var2 / 0.15);
      boolean[] var12 = new boolean[var4];
      boolean[] var13 = new boolean[var4];

      for (int var14 = 0; var14 < var4; var14++) {
         double var15 = var5[var14].flag5 ? var5[var14].doubleValue9 : 0.0;
         double var17 = var5[var14].flag5 ? var5[var14].doubleValue10 : 0.0;
         var13[var14] = var15 != 0.0 || var17 != 0.0;
         var12[var14] = !var13[var14];
      }

      var53[7] = this.method3359(this.method3360(var12));
      var53[8] = this.method3359(this.method3360(var13));
      var53[9] = var7.flag ? 1.0 : 0.0;
      var53[10] = var7.flag2 ? 1.0 : 0.0;

      for (int var54 = 0; var54 < 7; var54++) {
         var53[11 + var54] = var7.intValue >> var54 & 1;
      }

      double var55 = Math.toRadians(var7.doubleValue7);
      double[] var16 = new double[]{-Math.sin(var55), 0.0, Math.cos(var55)};
      double[] var56 = new double[]{-Math.cos(var55), 0.0, -Math.sin(var55)};
      BcCameraNet.Entry4 var18 = var4 > 1 ? var5[var4 - 2] : null;
      double var19 = var18 == null ? 0.0 : method3366(var7.doubleValue - var18.doubleValue);
      double var21 = var18 == null ? 0.0 : method3366(var7.doubleValue2 - var18.doubleValue2);
      double var23 = var18 == null ? 0.0 : method3366(var7.doubleValue3 - var18.doubleValue3);
      var53[18] = method3358((var19 * var16[0] + var23 * var16[2]) / 0.3, -3.0, 3.0);
      var53[19] = method3358((var19 * var56[0] + var23 * var56[2]) / 0.3, -3.0, 3.0);
      var53[20] = method3358(var21 / 0.3, -3.0, 3.0);
      boolean[] var25 = new boolean[var4];
      double[] var26 = new double[var4];
      double[] var27 = new double[var4];

      for (int var28 = 0; var28 < var4; var28++) {
         BcCameraNet.Entry4 var29 = var5[var28];
         var25[var28] = var29.intValue2 >= 0
            && method3361(var29.doubleValue4, var29.doubleValue5, var29.doubleValue6)
            && method3361(var29.doubleValue, var29.doubleValue2, var29.doubleValue3);
         double[] var30 = method3368(var29, var25[var28]);
         double[] var31 = method3362(var30[0], var30[1], var30[2]);
         var26[var28] = var31[0];
         var27[var28] = var31[1];
      }

      boolean var57 = var25[var4 - 1];
      double[] var58 = method3368(var7, var57);
      double var59 = var7.flag4 ? 1.5 : 1.8;
      double var32 = var57 ? method3357(var26[var4 - 1] - var7.doubleValue7) : 0.0;
      double var34 = var57 ? var27[var4 - 1] - var7.doubleValue8 : 0.0;
      double var36 = method3362(var58[0], var58[1] + var59 / 2.0, var58[2])[1];
      double var38 = method3362(var58[0], var58[1] - var59 / 2.0, var58[2])[1];
      double var40 = Math.max(Math.hypot(var58[0], var58[2]), 0.05);
      double var42 = Math.max(Math.sqrt(var58[0] * var58[0] + var58[1] * var58[1] + var58[2] * var58[2]), 0.05);
      var53[21] = var57 ? 1.0 : 0.0;
      var53[22] = method3358(var32 / 30.0, -6.0, 6.0);
      var53[23] = asinh(var32 / 2.0) / 3.0;
      var53[24] = method3358(var34 / 30.0, -6.0, 6.0);
      var53[25] = asinh(var34 / 2.0) / 3.0;
      var53[26] = var57 ? asinh((var36 - var7.doubleValue8) / 2.0) / 3.0 : 0.0;
      var53[27] = var57 ? asinh((var38 - var7.doubleValue8) / 2.0) / 3.0 : 0.0;
      var53[28] = var57 ? Math.log(Math.toDegrees(Math.atan(0.3 / var40))) / 3.0 : 0.0;
      var53[29] = var57 ? Math.log(var42) / 2.0 : 0.0;
      if (var57) {
         double[] var44 = method3367(var7);
         var53[30] = method3364(
               var44, method3363(var7.doubleValue7, var7.doubleValue8), new double[]{var7.doubleValue4, var7.doubleValue5, var7.doubleValue6}, var59
            )
            ? 1.0
            : 0.0;
      }

      boolean[] var60 = new boolean[var4];
      boolean[] var45 = new boolean[var4];

      for (int var46 = 0; var46 < var4; var46++) {
         var60[var46] = var25[var46] && var46 > 0 && var25[var46 - 1] && var5[var46].intValue2 == var5[var46 - 1].intValue2;
         var45[var46] = var25[var46] && !var60[var46] || !var25[var46];
      }

      boolean var61 = var60[var4 - 1];
      if (var61) {
         var53[31] = asinh(method3357(var26[var4 - 1] - var26[var4 - 2]) / 0.5) / 4.0;
         var53[32] = asinh((var27[var4 - 1] - var27[var4 - 2]) / 0.5) / 4.0;
         double var47 = method3366(var7.doubleValue4 - var18.doubleValue4);
         double var49 = method3366(var7.doubleValue5 - var18.doubleValue5);
         double var51 = method3366(var7.doubleValue6 - var18.doubleValue6);
         var53[33] = method3358((var47 * var16[0] + var51 * var16[2]) / 0.3, -3.0, 3.0);
         var53[34] = method3358((var47 * var56[0] + var51 * var56[2]) / 0.3, -3.0, 3.0);
         var53[35] = method3358(var49 / 0.3, -3.0, 3.0);
      }

      var53[36] = var57 && var7.flag4 ? 1.0 : 0.0;
      var53[37] = var57 ? Math.min(var7.intValue3, 3) / 3.0 : 0.0;
      var53[38] = var57 && !var61 ? 1.0 : 0.0;
      var53[39] = var57 ? this.method3359(this.method3360(var45)) : 0.0;
      boolean[] var62 = new boolean[var4];

      for (int var48 = 0; var48 < var4; var48++) {
         var62[var48] = var5[var48].flag3;
      }

      var53[40] = var7.flag3 ? 1.0 : 0.0;
      var53[41] = this.method3359(this.method3360(var62));
      return var53;
   }

   public static double method3366(double var0) {
      return Double.isNaN(var0) ? 0.0 : var0;
   }

   public static double[] method3367(BcCameraNet.Entry4 var0) {
      boolean var1 = (var0.intValue >> 5 & 1) != 0;
      return new double[]{var0.doubleValue, var0.doubleValue2 + (var1 ? 1.27 : 1.62), var0.doubleValue3};
   }

   public static double[] method3368(BcCameraNet.Entry4 var0, boolean var1) {
      if (!var1) {
         return new double[]{0.0, 0.0, 1.0};
      } else {
         double var2 = var0.flag4 ? 1.5 : 1.8;
         double[] var4 = method3367(var0);
         return new double[]{var0.doubleValue4 - var4[0], var0.doubleValue5 + var2 / 2.0 - var4[1], var0.doubleValue6 - var4[2]};
      }
   }

   public static double method3369(double var0) {
      return 0.5 * var0 * (1.0 + method3370(var0 / Math.sqrt(2.0)));
   }

   public static double method3370(double var0) {
      double var2 = Math.abs(var0);
      double var4 = 1.0 / (1.0 + 0.5 * var2);
      double var6 = var4
         * Math.exp(
            -var2 * var2
               - 1.26551223
               + var4
                  * (
                     1.00002368
                        + var4
                           * (
                              0.37409196
                                 + var4
                                    * (
                                       0.09678418
                                          + var4
                                             * (
                                                -0.18628806
                                                   + var4
                                                      * (0.27886807 + var4 * (-1.13520398 + var4 * (1.48851587 + var4 * (-0.82215223 + var4 * 0.17087277))))
                                             )
                                    )
                           )
                  )
         );
      return var0 >= 0.0 ? 1.0 - var6 : var6 - 1.0;
   }

   public static double method3371(double var0) {
      return 1.0 / (1.0 + Math.exp(-var0));
   }

   public static double[] method3372(double[][] var0, double[] var1, double[] var2) {
      double[] var3 = new double[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         double var5 = var1[var4];
         double[] var7 = var0[var4];

         for (int var8 = 0; var8 < var2.length; var8++) {
            var5 += var7[var8] * var2[var8];
         }

         var3[var4] = var5;
      }

      return var3;
   }

   public double[] method3373(BcCameraNet.Entry2 var1, double[] var2) {
      return this.method3374(var1, var2, 0);
   }

   public double[] method3374(BcCameraNet.Entry2 var1, double[] var2, int var3) {
      int var4 = this.intValue + this.method3354();
      double[] var5 = new double[var4 + this.intValue3];
      System.arraycopy(var2, 0, var5, 0, var4);
      System.arraycopy(this.doubleValues7, 0, var5, var4, this.intValue3);
      double[] var6 = method3372(this.doubleValues5, this.doubleValues6, var5);

      for (int var7 = 0; var7 < var6.length; var7++) {
         var6[var7] = method3369(var6[var7]);
      }

      for (int var19 = 0; var19 < this.layers; var19++) {
         double[] var8 = var1.doubleValues[var19];
         double[] var9 = method3372(this.doubleValues8[var19], this.doubleValues10[var19], var6);
         double[] var10 = method3372(this.doubleValues9[var19], this.doubleValues11[var19], var8);
         double[] var11 = new double[this.intValue2];

         for (int var12 = 0; var12 < this.intValue2; var12++) {
            double var13 = method3371(var9[var12] + var10[var12]);
            double var15 = method3371(var9[this.intValue2 + var12] + var10[this.intValue2 + var12]);
            double var17 = Math.tanh(var9[2 * this.intValue2 + var12] + var13 * var10[2 * this.intValue2 + var12]);
            var11[var12] = (1.0 - var15) * var17 + var15 * var8[var12];
         }

         var1.doubleValues[var19] = var11;
         var6 = var11;
      }

      return var6;
   }

   public double[] method3375(double[] var1) {
      double[] var2 = method3372(this.doubleValues12, this.doubleValues17, var1);

      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = method3369(var2[var3]);
      }

      return method3372(this.doubleValues13, this.doubleValues18, var2);
   }

   public double[] method3376(double[] var1, int var2) {
      double[] var3 = new double[var1.length + this.doubleValues16[var2].length];
      System.arraycopy(var1, 0, var3, 0, var1.length);
      System.arraycopy(this.doubleValues16[var2], 0, var3, var1.length, this.doubleValues16[var2].length);
      double[] var4 = method3372(this.doubleValues14, this.doubleValues19, var3);

      for (int var5 = 0; var5 < var4.length; var5++) {
         var4[var5] = method3369(var4[var5]);
      }

      return method3372(this.doubleValues15, this.doubleValues20, var4);
   }

   public int method3377(double[] var1, double var2) {
      double var4 = Double.NEGATIVE_INFINITY;

      for (double var9 : var1) {
         var4 = Math.max(var4, var9);
      }

      double var16 = 0.0;
      double[] var17 = new double[var1.length];

      for (int var18 = 0; var18 < var17.length; var18++) {
         var17[var18] = Math.exp(var1[var18] - var4);
         var16 += var17[var18];
      }

      if (this.doubleValue5 != 1.0) {
         double var19 = var17[0] / var16;
         double var11 = Double.NEGATIVE_INFINITY;

         for (int var13 = 1; var13 < var17.length; var13++) {
            var11 = Math.max(var11, var1[var13] / this.doubleValue5);
         }

         double var22 = 0.0;

         for (int var15 = 1; var15 < var17.length; var15++) {
            var17[var15] = Math.exp(var1[var15] / this.doubleValue5 - var11);
            var22 += var17[var15];
         }

         for (int var24 = 1; var24 < var17.length; var24++) {
            var17[var24] = var17[var24] / var22 * (1.0 - var19);
         }

         var17[0] = var19;
         var16 = 1.0;
      }

      double var20 = 0.0;
      double var21 = var2 * var16;

      for (int var23 = 0; var23 < var17.length; var23++) {
         var20 += var17[var23];
         if (var21 < var20) {
            return var23;
         }
      }

      return var17.length - 1;
   }

   public double method3378(int var1, double var2, Random var4) {
      if (var1 == 0) {
         return 0.0;
      } else {
         boolean var5 = var1 <= this.intValue4;
         int var6 = Math.max(0, Math.min(this.intValue4 - 1, var5 ? var1 - 1 : var1 - 1 - this.intValue4));
         double var7 = Math.log(this.doubleValues4[var6]);
         double var9 = Math.log(this.doubleValues4[var6 + 1]);
         if (this.doubleValues3 != null) {
            double[] var11 = this.doubleValues3[var1];
            double var12 = var4.nextDouble();
            double var14 = 0.0;
            int var16 = var11.length - 1;

            for (int var17 = 0; var17 < var11.length; var17++) {
               var14 += var11[var17];
               if (var12 < var14) {
                  var16 = var17;
                  break;
               }
            }

            double var22 = (var9 - var7) / var11.length;
            var7 += var22 * var16;
            var9 = var7 + var22;
         }

         double var21 = Math.exp(var7 + (var9 - var7) * var4.nextDouble());
         double var13 = Math.ceil(Math.exp(var7) / var2);
         double var15 = Math.ceil(Math.exp(var9) / var2) - 1.0;
         double var23 = var4.nextDouble();
         double var19 = var15 >= var13 ? (var13 + Math.floor(var23 * (var15 - var13 + 1.0))) * var2 : var21;
         return var5 ? var19 : -var19;
      }
   }

   public double[] method3379(double[] var1, int var2) {
      double[] var3 = new double[this.intValue + this.method3354()];
      System.arraycopy(var1, 0, var3, 0, this.intValue);

      for (int var7 : this.intValues) {
         var3[var7] = 0.0;
      }

      int var8 = this.intValue;
      if (this.flag) {
         for (int var9 = 0; var9 < 7; var9++) {
            var3[var8++] = var2 >> var9 & 1;
         }
      }

      if (this.flag2) {
         var3[var8] = this.method3353(var1);
      }

      return var3;
   }

   public double[] method3380(BcCameraNet.Entry2 var1, double[] var2, double var3, Random var5) {
      double[] var6 = this.method3373(var1, var2);
      int var7 = this.method3377(this.method3375(var6), var5.nextDouble());
      int var8 = this.method3377(this.method3376(var6, var7), var5.nextDouble());
      return new double[]{this.method3378(var7, var3, var5), this.method3378(var8, var3, var5)};
   }

   public boolean method3381() {
      return this.entrys.length > 0 && this.intValue7 > 1;
   }

   public static double[] method3382(double[] var0, double var1, double var3) {
      double[] var5 = (double[])var0.clone();
      var5[0] = method3358(var1 / 10.0, -6.0, 6.0);
      var5[1] = asinh(var1 / 0.5) / 4.0;
      var5[2] = method3358(var3 / 10.0, -6.0, 6.0);
      var5[3] = asinh(var3 / 0.5) / 4.0;
      var5[4] = 1.0;
      double var6 = method3358(var0[5] * 90.0 + var3, -90.0, 90.0);
      var5[5] = var6 / 90.0;
      boolean var8 = var0[21] > 0.5;
      double var9 = Math.sinh(var0[23] * 3.0) * 2.0 - var1;
      double var11 = Math.sinh(var0[25] * 3.0) * 2.0 - var3;
      var5[22] = var8 ? method3358(var9 / 30.0, -6.0, 6.0) : 0.0;
      var5[23] = var8 ? asinh(var9 / 2.0) / 3.0 : 0.0;
      var5[24] = var8 ? method3358(var11 / 30.0, -6.0, 6.0) : 0.0;
      var5[25] = var8 ? asinh(var11 / 2.0) / 3.0 : 0.0;

      for (int var13 = 26; var13 <= 27; var13++) {
         double var14 = Math.sinh(var0[var13] * 3.0) * 2.0 - var3;
         var5[var13] = var8 ? asinh(var14 / 2.0) / 3.0 : 0.0;
      }

      double var19 = Math.exp(var0[28] * 3.0);
      double var15 = Math.sinh(var5[26] * 3.0) * 2.0;
      double var17 = Math.sinh(var5[27] * 3.0) * 2.0;
      var5[30] = var8 && Math.abs(var9) < var19 && var15 <= 0.0 && var17 >= 0.0 ? 1.0 : 0.0;
      return var5;
   }

   public double[] method3383(BcCameraNet.Entry2 var1, double[] var2, double[] var3, double var4, Random var6) {
      for (int var7 = 0; var7 < this.entrys.length; var7++) {
         var1.doubleValues2[var7] = this.entrys[var7].method3392(var1.doubleValues2[var7], this.entrys[var7].method3391(var2));
      }

      double[] var25 = this.method3373(var1, var3);
      double[] var8 = this.method3375(var25);
      int var9 = this.intValue7;
      double[][] var10 = new double[var9][];
      double[] var11 = new double[var9];
      double var12 = Double.NEGATIVE_INFINITY;

      for (int var14 = 0; var14 < var9; var14++) {
         int var15 = this.method3377(var8, var6.nextDouble());
         int var16 = this.method3377(this.method3376(var25, var15), var6.nextDouble());
         double var17 = this.method3378(var15, var4, var6);
         double var19 = this.method3378(var16, var4, var6);
         var10[var14] = new double[]{var17, var19};
         double[] var21 = method3382(var2, var17, var19);
         double var22 = 0.0;

         for (int var24 = 0; var24 < this.entrys.length; var24++) {
            var22 += this.entrys[var24].method3393(this.entrys[var24].method3392(var1.doubleValues2[var24], this.entrys[var24].method3391(var21)));
         }

         var11[var14] = this.doubleValue6 * var22;
         var12 = Math.max(var12, var11[var14]);
      }

      double var26 = 0.0;

      for (int var27 = 0; var27 < var9; var27++) {
         var11[var27] = Math.exp(var11[var27] - var12);
         var26 += var11[var27];
      }

      double var28 = var6.nextDouble() * var26;
      double var18 = 0.0;

      for (int var20 = 0; var20 < var9; var20++) {
         var18 += var11[var20];
         if (var28 < var18) {
            return var10[var20];
         }
      }

      return var10[var9 - 1];
   }

   public static double[] method3384(BcCameraNet.Entry2 var0, double var1, double var3, double var5, double var7) {
      var0.doubleValue += var1;
      var0.doubleValue2 += var3;
      double var9 = Math.rint(var0.doubleValue / var5);
      double var11 = Math.rint(var0.doubleValue2 / var5);
      double var13 = var9 * var5;
      double var15 = var11 * var5;
      var0.doubleValue -= var13;
      var0.doubleValue2 -= var15;
      double var17 = Math.max(-90.0, Math.min(90.0, var7 + var15));
      return new double[]{var13, var17 - var7};
   }

   public static double[] method3385(JsonObject var0, String var1) {
      JsonObject var2 = var0.getAsJsonObject(var1);
      byte[] var3 = Base64.getDecoder().decode(var2.get("b64").getAsString());
      ByteBuffer var4 = ByteBuffer.wrap(var3).order(ByteOrder.LITTLE_ENDIAN);
      double[] var5 = new double[var3.length / 4];

      for (int var6 = 0; var6 < var5.length; var6++) {
         var5[var6] = var4.getFloat();
      }

      return var5;
   }

   public static double[][] method3386(JsonObject var0, String var1) {
      JsonArray var2 = var0.getAsJsonObject(var1).getAsJsonArray("shape");
      int var3 = var2.get(0).getAsInt();
      int var4 = var2.get(1).getAsInt();
      double[] var5 = method3385(var0, var1);
      double[][] var6 = new double[var3][var4];

      for (int var7 = 0; var7 < var3; var7++) {
         System.arraycopy(var5, var7 * var4, var6[var7], 0, var4);
      }

      return var6;
   }

   public static BcCameraNet method3387(InputStream var0) throws IOException {
      InputStreamReader var2 = new InputStreamReader(var0, StandardCharsets.UTF_8);

      JsonObject var1;
      try {
         var1 = JsonParser.parseReader(var2).getAsJsonObject();
      } catch (Throwable var31) {
         try {
            var2.close();
         } catch (Throwable var30) {
            var31.addSuppressed(var30);
         }

         throw var31;
      }

      var2.close();
      if (!"neurotest-bc-camera-v1".equals(var1.get("format").getAsString())) {
         throw new IOException("формат " + var1.get("format").getAsString() + ", ожидался neurotest-bc-camera-v1");
      } else {
         BcCameraNet var32 = new BcCameraNet();
         var32.intValue = var1.get("nf").getAsInt();
         var32.intValue2 = var1.get("h").getAsInt();
         var32.layers = var1.get("layers").getAsInt();
         var32.intValue3 = var1.get("emb").getAsInt();
         var32.intValue4 = var1.get("nb").getAsInt();
         var32.intValue5 = var1.get("ncls").getAsInt();
         var32.clip = var1.get("clip").getAsInt();
         var32.intValue6 = var1.get("window").getAsInt();
         var32.flag = var1.has("keys_now") && var1.get("keys_now").getAsBoolean();
         var32.flag2 = var1.has("intent") && var1.get("intent").getAsBoolean();
         if (var1.has("intent_hold") && var1.get("intent_hold").isJsonObject()) {
            var32.doubleValue2 = var1.getAsJsonObject("intent_hold").get("ref").getAsDouble();
         }

         if (var1.has("intent_err") && var1.get("intent_err").isJsonObject()) {
            JsonObject var3 = var1.getAsJsonObject("intent_err");
            var32.doubleValue = var3.get("k").getAsDouble();
            var32.doubleValues = method3388(var3.getAsJsonArray("edges"));
            var32.doubleValues2 = method3388(var3.getAsJsonArray("p"));
         }

         String var33 = "";
         if (var1.has("guard") && var1.get("guard").isJsonObject()) {
            JsonObject var4 = var1.getAsJsonObject("guard");
            var32.doubleValue3 = var4.get("m").getAsDouble();
            var32.doubleValue4 = var4.get("front").getAsDouble();
            var32.flag3 = var4.has("mode") && "mask".equals(var4.get("mode").getAsString());
            JsonObject var5 = var4.getAsJsonObject("ref");
            JsonArray var6 = var5.getAsJsonArray("x");
            JsonArray var7 = var5.getAsJsonArray("d");
            JsonArray var8 = var5.getAsJsonArray("out");
            double var9 = 0.0;

            for (int var11 = 0; var11 < var6.size(); var11++) {
               double[] var12 = method3388(var7.get(var11).getAsJsonArray());
               double[] var13 = method3388(var8.get(var11).getAsJsonArray());
               double[] var14 = var32.method3352(method3388(var6.get(var11).getAsJsonArray()), var12[0], var12[1]);
               var9 = Math.max(var9, Math.max(Math.abs(var14[0] - var13[0]), Math.abs(var14[1] - var13[1])));
            }

            if (!(var9 < 1.0E-6)) {
               throw new IOException("эталон предохранителя не сошёлся: max |Δ| " + var9);
            }

            var33 = String.format(
               "; предохранитель M %.1f° %s (эталон %d, max |Δ| %.1e)", var32.doubleValue3, var32.flag3 ? "выбором" : "обрезкой", var6.size(), var9
            );
         }

         if (var1.has("sub_table") && var1.get("sub_table").isJsonObject()) {
            JsonArray var34 = var1.getAsJsonObject("sub_table").getAsJsonArray("p");
            var32.doubleValues3 = new double[var34.size()][];

            for (int var37 = 0; var37 < var34.size(); var37++) {
               var32.doubleValues3[var37] = method3388(var34.get(var37).getAsJsonArray());
            }

            var33 = var33 + String.format("; подбин по людям (%d×%d)", var32.doubleValues3.length, var32.doubleValues3[0].length);
         }

         if (var1.has("drop")) {
            JsonArray var35 = var1.getAsJsonArray("drop");
            var32.intValues = new int[var35.size()];

            for (int var38 = 0; var38 < var35.size(); var38++) {
               var32.intValues[var38] = var35.get(var38).getAsInt();
            }
         }

         JsonArray var36 = var1.getAsJsonArray("edges");
         var32.doubleValues4 = new double[var36.size()];

         for (int var39 = 0; var39 < var36.size(); var39++) {
            var32.doubleValues4[var39] = var36.get(var39).getAsDouble();
         }

         if (var1.has("limits") && var1.get("limits").isJsonObject()) {
            JsonObject var40 = var1.getAsJsonObject("limits");
            BcCameraNet.Entry3 var42 = new BcCameraNet.Entry3();
            JsonArray var45 = var40.getAsJsonArray("players");
            var42.doubleValues = new double[var45.size()][];

            for (int var47 = 0; var47 < var45.size(); var47++) {
               var42.doubleValues[var47] = method3388(var45.get(var47).getAsJsonArray());
            }

            JsonObject var48 = var40.getAsJsonObject("window");
            double[] var50 = method3388(var48.getAsJsonArray("huge"));
            double[] var10 = method3388(var48.getAsJsonArray("flip"));
            var42.doubleValue = var50[0];
            var42.doubleValue2 = var50[1];
            var42.doubleValue3 = var10[0];
            var42.doubleValue4 = var10[1];
            var42.doubleValue5 = var40.get("big").getAsDouble();
            var42.doubleValue6 = var40.get("flip").getAsDouble();
            var42.intValue = var40.get("hist").getAsInt();
            JsonArray var53 = var40.getAsJsonArray("ref");
            double var54 = 0.0;
            int var57 = 0;

            for (int var15 = 0; var15 < var53.size(); var15++) {
               JsonObject var16 = var53.get(var15).getAsJsonObject();
               JsonArray var17 = var16.getAsJsonArray("h");
               int var18 = var17.size();
               double[] var19 = new double[var18];
               double[] var20 = new double[var18];
               boolean[] var21 = new boolean[var18];

               for (int var22 = 0; var22 < var18; var22++) {
                  double[] var23 = method3388(var17.get(var22).getAsJsonArray());
                  var19[var22] = var23[0];
                  var20[var22] = var23[1];
                  var21[var22] = var23[2] > 0.5;
               }

               double[] var65 = var42.method3397(
                  var19,
                  var20,
                  var21,
                  var18,
                  var42.doubleValues[var16.get("player").getAsInt()],
                  var16.get("u_big").getAsDouble(),
                  var16.get("u_flip").getAsDouble()
               );
               double[] var66 = method3388(var16.getAsJsonArray("out"));

               for (int var24 = 0; var24 < 4; var24++) {
                  var54 = Math.max(var54, Math.abs(var65[var24] - var66[var24]));
               }

               double[] var67 = new double[var32.intValue5];
               double[] var25 = var32.method3349(var67, var65[0], var65[1]);
               double[] var26 = var32.method3349(var67, var65[2], var65[3]);
               double[] var27 = method3388(var16.getAsJsonArray("allow_yaw"));
               double[] var28 = method3388(var16.getAsJsonArray("allow_pitch"));

               for (int var29 = 0; var29 < var32.intValue5; var29++) {
                  if (var25[var29] > -1.0E8 != var27[var29] > 0.5 || var26[var29] > -1.0E8 != var28[var29] > 0.5) {
                     var57++;
                  }
               }
            }

            if (!(var54 < 1.0E-6) || var57 > 0) {
               throw new IOException("эталон пределов не сошёлся: max |Δ| " + var54 + ", классов не так " + var57);
            }

            var32.entry3 = var42;
            var33 = var33 + String.format("; пределы людей: %d игроков (эталон %d, max |Δ| %.1e)", var42.doubleValues.length, var53.size(), var54);
         }

         JsonObject var41 = var1.getAsJsonObject("weights");
         var32.doubleValues5 = method3386(var41, "inp.0.weight");
         var32.doubleValues6 = method3385(var41, "inp.0.bias");
         var32.doubleValues7 = method3385(var41, "pemb0");
         var32.doubleValues8 = new double[var32.layers][][];
         var32.doubleValues9 = new double[var32.layers][][];
         var32.doubleValues10 = new double[var32.layers][];
         var32.doubleValues11 = new double[var32.layers][];

         for (int var43 = 0; var43 < var32.layers; var43++) {
            var32.doubleValues8[var43] = method3386(var41, "rnn.weight_ih_l" + var43);
            var32.doubleValues9[var43] = method3386(var41, "rnn.weight_hh_l" + var43);
            var32.doubleValues10[var43] = method3385(var41, "rnn.bias_ih_l" + var43);
            var32.doubleValues11[var43] = method3385(var41, "rnn.bias_hh_l" + var43);
         }

         var32.doubleValues12 = method3386(var41, "yaw.0.weight");
         var32.doubleValues17 = method3385(var41, "yaw.0.bias");
         var32.doubleValues13 = method3386(var41, "yaw.2.weight");
         var32.doubleValues18 = method3385(var41, "yaw.2.bias");
         var32.doubleValues14 = method3386(var41, "pitch.0.weight");
         var32.doubleValues19 = method3385(var41, "pitch.0.bias");
         var32.doubleValues15 = method3386(var41, "pitch.2.weight");
         var32.doubleValues20 = method3385(var41, "pitch.2.bias");
         var32.doubleValues16 = method3386(var41, "yemb.weight");
         String var44 = "";
         if (var1.has("guide") && !var1.get("guide").isJsonNull()) {
            JsonObject var46 = var1.getAsJsonObject("guide");
            var32.intValue7 = var46.get("n").getAsInt();
            var32.doubleValue6 = var46.get("lam").getAsDouble();
            JsonArray var49 = var46.getAsJsonArray("discs");
            var32.entrys = new BcCameraNet.Entry[var49.size()];
            JsonArray var51 = var46.getAsJsonArray("ref_rows");
            double var52 = 0.0;

            for (int var55 = 0; var55 < var49.size(); var55++) {
               JsonObject var56 = var49.get(var55).getAsJsonObject();
               JsonObject var58 = var56.getAsJsonObject("weights");
               BcCameraNet.Entry var59 = new BcCameraNet.Entry();
               double[] var60 = method3388(var56.getAsJsonArray("cols"));
               var59.intValues = new int[var60.length];

               for (int var61 = 0; var61 < var60.length; var61++) {
                  var59.intValues[var61] = (int)var60[var61];
               }

               var59.intValue = var56.get("h").getAsInt();
               var59.doubleValues = method3386(var58, "inp.0.weight");
               var59.doubleValues4 = method3385(var58, "inp.0.bias");
               var59.doubleValues2 = method3386(var58, "rnn.weight_ih_l0");
               var59.doubleValues3 = method3386(var58, "rnn.weight_hh_l0");
               var59.doubleValues5 = method3385(var58, "rnn.bias_ih_l0");
               var59.doubleValues6 = method3385(var58, "rnn.bias_hh_l0");
               var59.doubleValues7 = method3386(var58, "out.weight")[0];
               var59.doubleValue = method3385(var58, "out.bias")[0];
               var32.entrys[var55] = var59;
               double[] var62 = method3388(var56.getAsJsonArray("ref_scores"));
               double[] var63 = new double[var59.intValue];

               for (int var64 = 0; var64 < var51.size(); var64++) {
                  var63 = var59.method3392(var63, var59.method3391(method3388(var51.get(var64).getAsJsonArray())));
                  var52 = Math.max(var52, Math.abs(var59.method3393(var63) - var62[var64]));
               }
            }

            var44 = String.format(
               "; выбор хода: %d дискр., N %d, lam %.1f, эталон оценок %.2e", var32.entrys.length, var32.intValue7, var32.doubleValue6, var52
            );
            if (!(var52 <= 0.002)) {
               throw new IOException("ЭТАЛОН ДИСКРИМИНАТОРА НЕ СОШЁЛСЯ" + var44);
            }
         }

         var32.source = var1.get("source").getAsString().replace(',', ';') + (var32.method3381() ? "+guide" + var32.entrys.length : "");
         var32.text2 = var32.method3390(var1.getAsJsonArray("ref")) + var44 + var33 + "; модель " + var1.get("source").getAsString();
         return var32;
      }
   }

   public static double[] method3388(JsonArray var0) {
      double[] var1 = new double[var0.size()];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = var0.get(var2).getAsDouble();
      }

      return var1;
   }

   public static double method3389(double var0) {
      return var0 > 1.0E29 ? Double.NaN : var0;
   }

   public String method3390(JsonArray var1) throws IOException {
      double var2 = 0.0;
      double var4 = 0.0;
      int var6 = 0;

      for (JsonElement var8 : var1) {
         JsonObject var9 = var8.getAsJsonObject();
         double var10 = var9.get("grid").getAsDouble();
         double[] var12 = method3388(var9.getAsJsonArray("yaw"));
         double[] var13 = method3388(var9.getAsJsonArray("pitch"));
         double[] var14 = method3388(var9.getAsJsonArray("my"));
         double[] var15 = method3388(var9.getAsJsonArray("mp"));
         double[] var16 = method3388(var9.getAsJsonArray("ok"));
         JsonObject var17 = var9.getAsJsonObject("rec");
         JsonArray var18 = var17.getAsJsonArray("pos");
         JsonArray var19 = var17.getAsJsonArray("tgt");
         double[] var20 = method3388(var17.getAsJsonArray("keys"));
         double[] var21 = method3388(var17.getAsJsonArray("attack"));
         double[] var22 = method3388(var17.getAsJsonArray("tgt_id"));
         double[] var23 = method3388(var17.getAsJsonArray("n_cand"));
         double[] var24 = method3388(var17.getAsJsonArray("tgt_sneak"));
         double[] var25 = method3388(var17.getAsJsonArray("on_ground"));
         double[] var26 = method3388(var17.getAsJsonArray("hcoll"));
         JsonArray var27 = var9.getAsJsonArray("G");
         int var28 = var9.get("start").getAsInt();
         JsonArray var29 = var9.getAsJsonArray("ly");
         JsonArray var30 = var9.getAsJsonArray("lp");
         double[] var31 = method3388(var9.getAsJsonArray("yc"));
         BcCameraNet.Entry2 var32 = this.method3355();

         for (int var33 = 0; var33 < var12.length; var33++) {
            BcCameraNet.Entry4 var34 = new BcCameraNet.Entry4();
            JsonArray var35 = var18.get(var33).getAsJsonArray();
            JsonArray var36 = var19.get(var33).getAsJsonArray();
            var34.doubleValue = method3389(var35.get(0).getAsDouble());
            var34.doubleValue2 = method3389(var35.get(1).getAsDouble());
            var34.doubleValue3 = method3389(var35.get(2).getAsDouble());
            var34.doubleValue4 = method3389(var36.get(0).getAsDouble());
            var34.doubleValue5 = method3389(var36.get(1).getAsDouble());
            var34.doubleValue6 = method3389(var36.get(2).getAsDouble());
            var34.intValue = (int)var20[var33];
            var34.flag3 = var21[var33] > 0.0;
            var34.intValue2 = (int)var22[var33];
            var34.intValue3 = (int)var23[var33];
            var34.flag4 = var24[var33] > 0.0;
            var34.flag = var25[var33] > 0.0;
            var34.flag2 = var26[var33] > 0.0;
            var34.doubleValue7 = var12[var33];
            var34.doubleValue8 = var13[var33];
            var34.doubleValue9 = var14[var33];
            var34.doubleValue10 = var15[var33];
            var34.flag5 = var16[var33] > 0.0;
            var32.method3394(var34);
            double[] var37 = this.method3365(var32, var10);
            double[] var38 = method3388(var27.get(var33).getAsJsonArray());

            for (int var39 = 0; var39 < this.intValue; var39++) {
               var2 = Math.max(var2, Math.abs(var37[var39] - var38[var39]));
            }

            if (var33 >= var28) {
               double[] var47 = this.method3379(var33 == 0 ? new double[this.intValue] : method3388(var27.get(var33 - 1).getAsJsonArray()), (int)var20[var33]);
               double[] var40 = this.method3373(var32, var47);
               double[] var41 = this.method3375(var40);
               double[] var42 = this.method3376(var40, (int)var31[var33 - var28]);
               double[] var43 = method3388(var29.get(var33 - var28).getAsJsonArray());
               double[] var44 = method3388(var30.get(var33 - var28).getAsJsonArray());

               for (int var45 = 0; var45 < this.intValue5; var45++) {
                  var4 = Math.max(var4, Math.max(Math.abs(var41[var45] - var43[var45]), Math.abs(var42[var45] - var44[var45])));
               }
            }

            var6++;
         }
      }

      String var46 = String.format("эталон: %d тиков, признаки макс. расхождение %.2e, логиты %.2e", var6, var2, var4);
      if (var2 <= 1.0E-4 && var4 <= 0.002) {
         return var46;
      } else {
         throw new IOException("ЭТАЛОН НЕ СОШЁЛСЯ — " + var46);
      }
   }

   public static void main(String[] var0) throws Exception {
      try (InputStream var1 = Files.newInputStream(Path.of(var0[0]))) {
         BcCameraNet var2 = method3387(var1);
         System.out.println("OK: " + var2.text2);
         BcCameraNet.Entry2 var3 = var2.method3355();
         Random var4 = new Random(0L);
         double[] var5 = new double[var2.intValue + var2.method3354()];
         long var6 = System.nanoTime();
         double[] var8 = new double[var2.intValue];

         for (int var9 = 0; var9 < 2000; var9++) {
            if (var2.method3381()) {
               var2.method3383(var3, var8, var5, 0.15, var4);
            } else if (var2.entry3 != null) {
               var2.method3350(var3, var8, var5, 0.15, var4);
            } else {
               var2.method3380(var3, var5, 0.15, var4);
            }
         }

         System.out.printf("шаг сети: %.3f мс%n", (System.nanoTime() - var6) / 2000L / 1000000.0);
      }
   }

   public static final class Entry {
      public int[] intValues;
      public int intValue;
      public double[][] doubleValues;
      public double[][] doubleValues2;
      public double[][] doubleValues3;
      public double[] doubleValues4;
      public double[] doubleValues5;
      public double[] doubleValues6;
      public double[] doubleValues7;
      public double doubleValue;

      public double[] method3391(double[] var1) {
         double[] var2 = new double[this.intValues.length];

         for (int var3 = 0; var3 < this.intValues.length; var3++) {
            var2[var3] = var1[this.intValues[var3]];
         }

         double[] var5 = BcCameraNet.method3372(this.doubleValues, this.doubleValues4, var2);

         for (int var4 = 0; var4 < var5.length; var4++) {
            var5[var4] = BcCameraNet.method3369(var5[var4]);
         }

         return var5;
      }

      public double[] method3392(double[] var1, double[] var2) {
         double[] var3 = BcCameraNet.method3372(this.doubleValues2, this.doubleValues5, var2);
         double[] var4 = BcCameraNet.method3372(this.doubleValues3, this.doubleValues6, var1);
         double[] var5 = new double[this.intValue];

         for (int var6 = 0; var6 < this.intValue; var6++) {
            double var7 = BcCameraNet.method3371(var3[var6] + var4[var6]);
            double var9 = BcCameraNet.method3371(var3[this.intValue + var6] + var4[this.intValue + var6]);
            double var11 = Math.tanh(var3[2 * this.intValue + var6] + var7 * var4[2 * this.intValue + var6]);
            var5[var6] = (1.0 - var9) * var11 + var9 * var1[var6];
         }

         return var5;
      }

      public double method3393(double[] var1) {
         double var2 = this.doubleValue;

         for (int var4 = 0; var4 < this.intValue; var4++) {
            var2 += this.doubleValues7[var4] * var1[var4];
         }

         return var2;
      }
   }

   public final class Entry2 {
      public final BcCameraNet.Entry4[] entry4s = new BcCameraNet.Entry4[BcCameraNet.this.intValue6];
      public int n;
      public double[][] doubleValues = new double[BcCameraNet.this.layers][BcCameraNet.this.intValue2];
      public double doubleValue;
      public double doubleValue2;
      public final double[][] doubleValues2 = new double[BcCameraNet.this.entrys.length][];
      public double[] doubleValues3;
      public double[] doubleValues4;
      public double[] doubleValues5;
      public boolean[] flags;
      public int intValue;

      public Entry2() {
         for (int var2 = 0; var2 < BcCameraNet.this.entrys.length; var2++) {
            this.doubleValues2[var2] = new double[BcCameraNet.this.entrys[var2].intValue];
         }
      }

      public void method3394(BcCameraNet.Entry4 var1) {
         this.entry4s[this.n % BcCameraNet.this.intValue6] = var1;
         this.n++;
      }

      public BcCameraNet.Entry4 method3395(int var1) {
         return this.entry4s[(this.n - 1 - var1) % BcCameraNet.this.intValue6];
      }

      public int size() {
         return Math.min(this.n, BcCameraNet.this.intValue6);
      }

      public void method3396() {
         this.doubleValue = 0.0;
         this.doubleValue2 = 0.0;
      }
   }

   public static final class Entry3 {
      public double[][] doubleValues;
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5 = 40.0;
      public double doubleValue6 = 15.0;
      public int intValue = 200;
      public long longValue;
      public long longValue2;

      public double[] method3397(double[] var1, double[] var2, boolean[] var3, int var4, double[] var5, double var6, double var8) {
         double[] var10 = new double[var4];
         double[] var11 = new double[var4];

         for (int var12 = 0; var12 < var4; var12++) {
            var10[var12] = var3[var12] ? var1[var12] : 0.0;
            var11[var12] = var3[var12] ? var2[var12] : 0.0;
         }

         double var41 = var4 > 0 ? var10[var4 - 1] : 0.0;
         double var14 = var4 > 0 ? var11[var4 - 1] : 0.0;
         double var16 = Math.max(-var5[0], var41 - var5[1]);
         double var18 = Math.min(var5[0], var41 + var5[1]);
         double var20 = 0.0;
         double var22 = 0.0;

         for (int var24 = Math.max(0, var4 - 3); var24 < var4; var24++) {
            var20 += var10[var24];
         }

         for (int var46 = Math.max(0, var4 - 19); var46 < var4; var46++) {
            var22 += Math.abs(var10[var46]);
         }

         var16 = Math.max(var16, -var5[2] - var20);
         var18 = Math.min(var18, var5[2] - var20);
         double var47 = Math.max(20.0 * var5[3] - var22, 0.0);
         var16 = Math.max(var16, -var47);
         var18 = Math.min(var18, var47);
         int var26 = Math.max(0, var4 - 199);
         int var27 = 0;
         int var28 = 0;

         for (int var29 = var26; var29 < var4; var29++) {
            if (var3[var29] && Math.abs(var10[var29]) > this.doubleValue5) {
               var27++;
            }

            boolean var30 = false;

            for (int var31 = 1; var31 <= 3 && var29 - var31 >= var26; var31++) {
               int var32 = var29 - var31;
               if (Math.abs(var10[var29]) > this.doubleValue6
                  && Math.abs(var10[var32]) > this.doubleValue6
                  && Math.signum(var10[var29]) != Math.signum(var10[var32])
                  && var3[var29]
                  && var3[var32]) {
                  var30 = true;
               }
            }

            if (var30) {
               var28++;
            }
         }

         double var48 = Math.max(0.0, Math.min(1.0, (this.doubleValue2 - var27) / Math.max(this.doubleValue2 - this.doubleValue, 1.0E-9)));
         boolean var49 = var6 > (var27 >= this.doubleValue ? var48 : 1.0);
         if (var49) {
            var16 = Math.max(var16, -this.doubleValue5);
            var18 = Math.min(var18, this.doubleValue5);
         }

         double var50 = Math.max(0.0, Math.min(1.0, (this.doubleValue4 - var28) / Math.max(this.doubleValue4 - this.doubleValue3, 1.0E-9)));
         boolean var34 = var8 > (var28 >= this.doubleValue3 ? var50 : 1.0);
         double var35 = 0.0;

         for (int var37 = 0; var37 < Math.min(3, var4); var37++) {
            double var38 = var10[var4 - 1 - var37];
            if (var35 == 0.0 && Math.abs(var38) > this.doubleValue6) {
               var35 = Math.signum(var38);
            }
         }

         if (var34 && var35 > 0.0) {
            var16 = Math.max(var16, -this.doubleValue6);
         }

         if (var34 && var35 < 0.0) {
            var18 = Math.min(var18, this.doubleValue6);
         }

         double var51 = Math.max(-var5[4], var14 - var5[5]);
         double var39 = Math.min(var5[4], var14 + var5[5]);
         if (var16 > var18) {
            var16 = var18 = (var16 + var18) / 2.0;
         }

         if (var51 > var39) {
            var51 = var39 = (var51 + var39) / 2.0;
         }

         return new double[]{var16, var18, var51, var39};
      }
   }

   public static final class Entry4 {
      public double doubleValue = Double.NaN;
      public double doubleValue2 = Double.NaN;
      public double doubleValue3 = Double.NaN;
      public int intValue;
      public boolean flag;
      public boolean flag2;
      public boolean flag3;
      public double doubleValue4 = Double.NaN;
      public double doubleValue5 = Double.NaN;
      public double doubleValue6 = Double.NaN;
      public int intValue2 = -1;
      public int intValue3;
      public boolean flag4;
      public double doubleValue7;
      public double doubleValue8;
      public double doubleValue9;
      public double doubleValue10;
      public boolean flag5 = true;
   }
}

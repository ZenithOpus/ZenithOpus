package org.zenith.utility.misc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.zenith.utility.api.PointClickDqn;

public final class BumpController {
   public static final double CONST_doubleValue = 0.05;
   public static final double CONST_doubleValue2 = 0.4608;
   public static final double CONST_doubleValue3 = 0.2592;
   public static final double CONST_doubleValue4 = 0.257;
   private static final int CONST_intValue = 2;
   private static final double CONST_doubleValue5 = 0.2;
   private static final double CONST_doubleValue6 = 0.02;
   private final PointClickDqn pointClickDqn;
   private final Random random;
   private final BumpController.Entry2 entry2;

   private BumpController(PointClickDqn var1, long var2, BumpController.Entry2 var4) {
      this.pointClickDqn = var1;
      this.random = new Random(var2);
      this.entry2 = var4;
   }

   public static BumpController method953(PointClickDqn var0, long var1) {
      return var0 == null ? null : method955(var0, var1);
   }

   public static BumpController method954(long var0) {
      return method955(null, var0);
   }

   private static BumpController method955(PointClickDqn var0, long var1) {
      try {
         return new BumpController(var0, var1, BumpController.Entry2.method976());
      } catch (Throwable var4) {
         System.err.println("[PointClick] failed to load mouse calibration: " + var4);
         return null;
      }
   }

   public BumpController.Entry4 method956(BumpController.Entry3 var1) {
      float[] var2 = new float[]{
         (float)var1.doubleValue10,
         (float)var1.doubleValue11,
         (float)var1.doubleValue12,
         (float)var1.doubleValue13,
         (float)var1.doubleValue14,
         (float)var1.doubleValue15,
         (float)var1.doubleValue16,
         (float)var1.doubleValue17,
         (float)var1.doubleValue7,
         (float)var1.doubleValue18,
         (float)var1.doubleValue19
      };
      int var3 = this.pointClickDqn.method3226(var2);
      boolean var4 = var3 / 25 != 0;
      int var5 = 2 + var3 % 25 * 2;
      int var6 = var4 ? var5 : 2;
      BumpController.Entry var7 = method963(2, var1.doubleValue3, -var1.doubleValue5, 0.4608, var1.doubleValue7);
      BumpController.Entry var8 = method963(2, var1.doubleValue4, -var1.doubleValue6, 0.2592, var1.doubleValue7);
      var7.doubleValue2 *= -1.0;
      var8.doubleValue2 *= -1.0;
      double[] var9 = this.method959(var7.doubleValue2, var8.doubleValue2);
      var7.doubleValue2 = var9[0];
      var8.doubleValue2 = var9[1];
      var7 = method963(2, var7.doubleValue, var7.doubleValue2, 0.4608, var1.doubleValue7);
      var8 = method963(2, var8.doubleValue, var8.doubleValue2, 0.2592, var1.doubleValue7);
      double var10 = var7.doubleValue;
      double var12 = var8.doubleValue;
      double var14 = var7.doubleValue2;
      double var16 = var8.doubleValue2;
      BumpController.Entry var18 = method963(var5, var7.doubleValue, var7.doubleValue2, 0.4608, var1.doubleValue7);
      BumpController.Entry var19 = method963(var5, var8.doubleValue, var8.doubleValue2, 0.2592, var1.doubleValue7);
      double[][] var20 = method961(var1.doubleValue10, var1.doubleValue12, var18.doubleValue, var18.doubleValue2, var5, var6);
      double[][] var21 = method961(var1.doubleValue11, var1.doubleValue13, var19.doubleValue, var19.doubleValue2, var5, var6);
      method964(var20, 0.4608);
      method964(var21, 0.2592);
      double[] var22 = (double[])var20[1].clone();
      double[] var23 = (double[])var21[1].clone();
      double[] var24 = new double[var6 + 1];

      for (int var25 = 0; var25 <= var6; var25++) {
         var24[var25] = this.entry2.method977(Math.hypot(var22[var25], var23[var25]));
         if (var24[var25] == 0.0) {
            var24[var25] = Double.MIN_NORMAL;
         }
      }

      double var47 = 0.0;

      for (int var27 = 0; var27 < var6; var27++) {
         double var28 = (var22[var27 + 1] / var24[var27 + 1] - var22[var27] / var24[var27]) / 0.05;
         double var30 = (var23[var27 + 1] / var24[var27 + 1] - var23[var27] / var24[var27]) / 0.05;
         var47 += Math.hypot(var28, var30);
      }

      double[] var48 = method966(var22, var24);
      double[] var49 = method966(var23, var24);
      BumpController.Entry5 var29 = this.entry2.method978(var48, var49, var1.doubleValue8, var1.doubleValue9, var24);
      double var50 = var29.doubleValue - var1.doubleValue8;
      double var32 = var29.doubleValue2 - var1.doubleValue9;
      BumpController.Entry6 var34 = this.method960(var48, var49, var6, 0.0, 0.0, 0.2, 0.02);
      BumpController.Entry5 var35 = this.entry2
         .method978((double[])var34.doubleValues3.clone(), (double[])var34.doubleValues4.clone(), var1.doubleValue8, var1.doubleValue9, var24);
      double[] var36 = method968(method967(var34.doubleValues3, var24));
      double[] var37 = method968(method967(var34.doubleValues4, var24));
      double var38 = (sum(var35.doubleValues) - sum(var36)) / (var6 * 0.05);
      double var40 = (sum(var35.doubleValues2) - sum(var37)) / (var6 * 0.05);
      BumpController.Entry6 var42 = this.method960(method967(var34.doubleValues3, var24), method967(var34.doubleValues4, var24), var6, var38, var40, 0.0, 0.0);
      method965(var42.doubleValues, var42.doubleValues3, var1.doubleValue, 0.4608);
      method965(var42.doubleValues2, var42.doubleValues4, var1.doubleValue2, 0.2592);
      BumpController.Entry var43 = method963(var6, var10, var14, 0.4608, var1.doubleValue7);
      BumpController.Entry var44 = method963(var6, var12, var16, 0.2592, var1.doubleValue7);
      return new BumpController.Entry4(
         var3,
         var6,
         var42.doubleValues,
         var42.doubleValues2,
         var1.doubleValue + var20[0][var6] - var20[0][0],
         var1.doubleValue2 + var21[0][var6] - var21[0][0],
         var20[1][var6],
         var21[1][var6],
         var43.doubleValue,
         var44.doubleValue,
         var43.doubleValue2,
         var44.doubleValue2,
         var35.doubleValue,
         var35.doubleValue2,
         var1.doubleValue8 + var50,
         var1.doubleValue9 + var32,
         var47
      );
   }

   public BumpController.Entry4 method957(BumpController.Entry3 var1, int var2, double var3, double var5, double var7, double var9) {
      if (var2 >= 2 && var2 <= 50) {
         double[][] var11 = method961(var1.doubleValue10, var1.doubleValue12, var3, var7, var2, var2);
         double[][] var12 = method961(var1.doubleValue11, var1.doubleValue13, var5, var9, var2, var2);
         return this.method958(var1, var2, var11, var12);
      } else {
         throw new IllegalArgumentException("BUMP horizon must be in [2, 50]");
      }
   }

   private BumpController.Entry4 method958(BumpController.Entry3 var1, int var2, double[][] var3, double[][] var4) {
      double[] var5 = (double[])var3[1].clone();
      double[] var6 = (double[])var4[1].clone();
      double[] var7 = new double[var2 + 1];

      for (int var8 = 0; var8 <= var2; var8++) {
         var7[var8] = this.entry2.method977(Math.hypot(var5[var8], var6[var8]));
         if (var7[var8] == 0.0) {
            var7[var8] = Double.MIN_NORMAL;
         }
      }

      double var26 = 0.0;

      for (int var10 = 0; var10 < var2; var10++) {
         double var11 = (var5[var10 + 1] / var7[var10 + 1] - var5[var10] / var7[var10]) / 0.05;
         double var13 = (var6[var10 + 1] / var7[var10 + 1] - var6[var10] / var7[var10]) / 0.05;
         var26 += Math.hypot(var11, var13);
      }

      double[] var27 = method966(var5, var7);
      double[] var28 = method966(var6, var7);
      BumpController.Entry5 var12 = this.entry2.method978(var27, var28, var1.doubleValue8, var1.doubleValue9, var7);
      double var29 = var12.doubleValue - var1.doubleValue8;
      double var15 = var12.doubleValue2 - var1.doubleValue9;
      BumpController.Entry6 var17 = this.method960(var27, var28, var2, 0.0, 0.0, 0.2, 0.02);
      BumpController.Entry5 var18 = this.entry2
         .method978((double[])var17.doubleValues3.clone(), (double[])var17.doubleValues4.clone(), var1.doubleValue8, var1.doubleValue9, var7);
      double[] var19 = method968(method967(var17.doubleValues3, var7));
      double[] var20 = method968(method967(var17.doubleValues4, var7));
      double var21 = (sum(var18.doubleValues) - sum(var19)) / (var2 * 0.05);
      double var23 = (sum(var18.doubleValues2) - sum(var20)) / (var2 * 0.05);
      BumpController.Entry6 var25 = this.method960(method967(var17.doubleValues3, var7), method967(var17.doubleValues4, var7), var2, var21, var23, 0.0, 0.0);
      return new BumpController.Entry4(
         -1,
         var2,
         var25.doubleValues,
         var25.doubleValues2,
         var1.doubleValue + var3[0][var2] - var3[0][0],
         var1.doubleValue2 + var4[0][var2] - var4[0][0],
         var3[1][var2],
         var4[1][var2],
         var1.doubleValue14,
         var1.doubleValue15,
         var1.doubleValue16,
         var1.doubleValue17,
         var18.doubleValue,
         var18.doubleValue2,
         var1.doubleValue8 + var29,
         var1.doubleValue9 + var15,
         var26
      );
   }

   private double[] method959(double var1, double var3) {
      double var5 = Math.hypot(var1, var3);
      if (var5 <= 0.0) {
         var5 = Double.MIN_NORMAL;
      }

      double var7 = 2.0 * Math.toDegrees(Math.atan(var5 / 1.26));
      double var9 = Math.log(1.0 + var7 / 0.3);
      double var11 = Math.exp(var9 + 0.15 * this.random.nextGaussian());
      double var13 = Math.max((var11 - 1.0) * 0.3, 0.0);
      double var15 = 1.26 * Math.tan(Math.toRadians(var13) / 2.0);
      return new double[]{var15 * var1 / var5, var15 * var3 / var5};
   }

   private BumpController.Entry6 method960(double[] var1, double[] var2, int var3, double var4, double var6, double var8, double var10) {
      double[] var12 = (double[])var1.clone();
      double[] var13 = (double[])var2.clone();

      for (int var14 = 1; var14 <= var3; var14++) {
         double var15 = var12[var14];
         double var17 = var13[var14];
         double var19 = Math.hypot(var15, var17);
         double var21 = var19 == 0.0 ? 0.0 : var15 / var19;
         double var23 = var19 == 0.0 ? 0.0 : var17 / var19;
         double var25 = -var23;
         double var29 = var8 * var19 * this.random.nextGaussian();
         double var31 = var10 * var19 * this.random.nextGaussian();
         var12[var14] = var15 + var29 * var21 + var31 * var25 + var4;
         var13[var14] = var17 + var29 * var23 + var31 * var21 + var6;
      }

      return new BumpController.Entry6(method968(var12), method968(var13), var12, var13);
   }

   private static double[][] method961(double var0, double var2, double var4, double var6, int var8, int var9) {
      double[][] var10 = new double[][]{{1.0, 0.05}, {0.0, 1.0}};
      double[] var11 = new double[]{0.0012500000000000002, 0.05};
      double[][] var12 = method969(var10, var8);
      double[][] var13 = method970(method962(var8, var8, var10, var11));
      double[] var14 = new double[]{var0, var2};
      double[] var15 = method975(new double[]{var4, var6}, method974(var12, var14));
      double[] var16 = method974(var13, var15);
      double[][] var17 = new double[2][var9 + 1];

      for (int var18 = 0; var18 <= var9; var18++) {
         double[] var19 = method974(method969(var10, var18), var14);
         double[] var20 = method974(method962(var18, var8, var10, var11), var16);
         var17[0][var18] = var19[0] + var20[0];
         var17[1][var18] = var19[1] + var20[1];
      }

      return var17;
   }

   private static double[][] method962(int var0, int var1, double[][] var2, double[] var3) {
      double[][] var4 = new double[2][2];
      double[][] var5 = new double[][]{{var3[0] * var3[0], var3[0] * var3[1]}, {var3[1] * var3[0], var3[1] * var3[1]}};
      double[][] var6 = method971(var2);

      for (int var7 = 0; var7 < var0; var7++) {
         var4 = method972(var4, method973(method973(method969(var2, var7), var5), method969(var6, var1 - var0 + var7)));
      }

      return var4;
   }

   private static BumpController.Entry method963(int var0, double var1, double var3, double var5, double var7) {
      double var9 = var5 - var7;
      double var11 = var7;

      for (int var13 = 0; var13 < var0; var13++) {
         double var14 = var1 + 0.05 * var3;
         if (var14 >= var11 && var14 <= var9) {
            var1 = var14;
         } else if (var14 > var9) {
            var1 = 2.0 * var9 - var14;
            var3 *= -1.0;
         } else {
            var1 = 2.0 * var11 - var14;
            var3 *= -1.0;
         }
      }

      return new BumpController.Entry(var1, var3);
   }

   private static void method964(double[][] var0, double var1) {
      for (int var3 = 0; var3 < var0[0].length; var3++) {
         if (var0[0][var3] <= 0.0) {
            var0[0][var3] = Double.MIN_NORMAL;
            var0[1][var3] = 0.0;
         } else if (var0[0][var3] >= var1) {
            var0[0][var3] = var1;
            var0[1][var3] = 0.0;
         }
      }
   }

   private static void method965(double[] var0, double[] var1, double var2, double var4) {
      double var6 = 0.0;

      for (int var8 = 0; var8 < var0.length; var8++) {
         double var9 = var2 + var6 + var0[var8];
         if (var9 <= 0.0) {
            var0[var8] = -(var2 + var6);
            var1[var8 + 1] = 0.0;
         } else if (var9 >= var4) {
            var0[var8] = var4 - (var2 + var6);
            var1[var8 + 1] = 0.0;
         }

         var6 += var0[var8];
      }
   }

   private static double[] method966(double[] var0, double[] var1) {
      double[] var2 = new double[var0.length];

      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = var0[var3] / var1[var3];
      }

      return var2;
   }

   private static double[] method967(double[] var0, double[] var1) {
      double[] var2 = new double[var0.length];

      for (int var3 = 0; var3 < var2.length; var3++) {
         var2[var3] = var0[var3] * var1[var3];
      }

      return var2;
   }

   private static double[] method968(double[] var0) {
      double[] var1 = new double[var0.length - 1];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = (var0[var2 + 1] + var0[var2]) * 0.5 * 0.05;
      }

      return var1;
   }

   private static double sum(double[] var0) {
      double var1 = 0.0;

      for (double var6 : var0) {
         var1 += var6;
      }

      return var1;
   }

   private static double[][] method969(double[][] var0, int var1) {
      double[][] var2 = new double[][]{{1.0, 0.0}, {0.0, 1.0}};
      double[][] var3 = var0;

      for (int var4 = var1; var4 > 0; var4 >>= 1) {
         if ((var4 & 1) != 0) {
            var2 = method973(var2, var3);
         }

         var3 = method973(var3, var3);
      }

      return var2;
   }

   private static double[][] method970(double[][] var0) {
      double var1 = var0[0][0] * var0[1][1] - var0[0][1] * var0[1][0];
      if (Math.abs(var1) < 1.0E-18) {
         throw new IllegalStateException("singular BUMP grammian");
      } else {
         return new double[][]{{var0[1][1] / var1, -var0[0][1] / var1}, {-var0[1][0] / var1, var0[0][0] / var1}};
      }
   }

   private static double[][] method971(double[][] var0) {
      return new double[][]{{var0[0][0], var0[1][0]}, {var0[0][1], var0[1][1]}};
   }

   private static double[][] method972(double[][] var0, double[][] var1) {
      return new double[][]{{var0[0][0] + var1[0][0], var0[0][1] + var1[0][1]}, {var0[1][0] + var1[1][0], var0[1][1] + var1[1][1]}};
   }

   private static double[][] method973(double[][] var0, double[][] var1) {
      return new double[][]{
         {var0[0][0] * var1[0][0] + var0[0][1] * var1[1][0], var0[0][0] * var1[0][1] + var0[0][1] * var1[1][1]},
         {var0[1][0] * var1[0][0] + var0[1][1] * var1[1][0], var0[1][0] * var1[0][1] + var0[1][1] * var1[1][1]}
      };
   }

   private static double[] method974(double[][] var0, double[] var1) {
      return new double[]{var0[0][0] * var1[0] + var0[0][1] * var1[1], var0[1][0] * var1[0] + var0[1][1] * var1[1]};
   }

   private static double[] method975(double[] var0, double[] var1) {
      return new double[]{var0[0] - var1[0], var0[1] - var1[1]};
   }

   private static final class Entry {
      public double doubleValue;
      public double doubleValue2;

      public Entry(double var1, double var3) {
         this.doubleValue = var1;
         this.doubleValue2 = var3;
      }
   }

   private static final class Entry2 {
      private static final String text = "/assets/zenith/pointclick/mouse_gain.txt";
      private static final String text2 = "/assets/zenith/pointclick/rotation_map_rad.csv";
      private final double[] doubleValues;
      private final double[] doubleValues2;
      private final double[][] doubleValues3;

      private Entry2(double[] var1, double[] var2, double[][] var3) {
         this.doubleValues = var1;
         this.doubleValues2 = var2;
         this.doubleValues3 = var3;
      }

      public static BumpController.Entry2 method976() throws Exception {
         ArrayList var0 = new ArrayList();
         ArrayList var1 = new ArrayList();

         String var3;
         try (BufferedReader var2 = method984("/assets/zenith/pointclick/mouse_gain.txt")) {
            while ((var3 = var2.readLine()) != null) {
               String[] var4 = var3.trim().split(":\\s*");
               if (var4.length == 2) {
                  double var5 = Double.parseDouble(var4[0]);
                  double var7 = Double.parseDouble(var4[1]);
                  double var9 = 0.0254 * (var5 / 400.0) * 125.0;
                  double var11 = 0.0254 * (var7 / 110.0) * 125.0;
                  var0.add(var11);
                  var1.add(var9 == 0.0 ? 0.0 : var11 / var9);
               }
            }
         }

         double[][] var17 = new double[31][31];

         try (BufferedReader var18 = method984("/assets/zenith/pointclick/rotation_map_rad.csv")) {
            for (int var19 = 0; var19 < var17.length; var19++) {
               String var20 = var18.readLine();
               if (var20 == null) {
                  throw new IllegalStateException("rotation map has fewer than 31 rows");
               }

               String[] var6 = var20.split(",");
               if (var6.length != 31) {
                  throw new IllegalStateException("rotation map row has " + var6.length + " columns");
               }

               for (int var21 = 0; var21 < var6.length; var21++) {
                  var17[var19][var21] = Double.parseDouble(var6[var21]);
               }
            }
         }

         return new BumpController.Entry2(method985(var0), method985(var1), var17);
      }

      public double method977(double var1) {
         return method983(this.doubleValues, this.doubleValues2, var1);
      }

      public BumpController.Entry5 method978(double[] var1, double[] var2, double var3, double var5, double[] var7) {
         double[] var8 = BumpController.method968(var1);
         double[] var9 = BumpController.method968(var2);
         double var10 = this.method980(var3, var5);

         for (int var12 = 0; var12 < var8.length; var12++) {
            if (var8[var12] != 0.0 || var9[var12] != 0.0) {
               double var13 = var3;
               double var15 = var5;
               double var17 = Math.cos(-var10);
               double var19 = Math.sin(-var10);
               double var21 = var17 * var8[var12] - var19 * var9[var12];
               double var23 = var19 * var8[var12] + var17 * var9[var12];
               var3 += var21;
               var5 += var23;
               double[] var25 = this.method979(var13, var15, var3, var5, var7[var12]);
               var8[var12] = var25[0];
               var9[var12] = var25[1];
            }
         }

         return new BumpController.Entry5(var8, var9, var3, var5);
      }

      private double[] method979(double var1, double var3, double var5, double var7, double var9) {
         double var11 = this.method980(var5, var7);
         double var13 = this.method980(var1, var3);
         double var15 = Math.hypot(var5 - var1, var7 - var3);
         double var17 = var11 - var13;
         if (var17 == 0.0) {
            var17 = Double.MIN_NORMAL;
         }

         double var19 = var9 * var15 * 2.0 * Math.sin(var17 / 2.0) / var17;
         double var21 = var15 == 0.0 ? Double.MIN_NORMAL : var15;
         double var23 = (var5 - var1) / var21;
         double var25 = (var7 - var3) / var21;
         double var27 = var17 / 2.0 + var13;
         return new double[]{(var23 * Math.cos(var27) - var25 * Math.sin(var27)) * var19, (var23 * Math.sin(var27) + var25 * Math.cos(var27)) * var19};
      }

      private double method980(double var1, double var3) {
         double var5 = var1 / 0.257;
         double var7 = var3 / 0.257;
         double var9 = (var5 + 1.5) / 0.1;
         double var11 = (1.5 - var7) / 0.1;
         return method981(this.doubleValues3, var11, var9);
      }

      private static double method981(double[][] var0, double var1, double var3) {
         int var5 = (int)Math.floor(var1);
         int var6 = (int)Math.floor(var3);
         double var7 = var1 - var5;
         double var9 = var3 - var6;
         double[] var11 = new double[4];

         for (int var12 = -1; var12 <= 2; var12++) {
            int var13 = method986(var5 + var12, 0, var0.length - 1);
            var11[var12 + 1] = method982(
               var0[var13][method986(var6 - 1, 0, var0[var13].length - 1)],
               var0[var13][method986(var6, 0, var0[var13].length - 1)],
               var0[var13][method986(var6 + 1, 0, var0[var13].length - 1)],
               var0[var13][method986(var6 + 2, 0, var0[var13].length - 1)],
               var9
            );
         }

         return method982(var11[0], var11[1], var11[2], var11[3], var7);
      }

      private static double method982(double var0, double var2, double var4, double var6, double var8) {
         return 0.5
            * (
               2.0 * var2
                  + (-var0 + var4) * var8
                  + (2.0 * var0 - 5.0 * var2 + 4.0 * var4 - var6) * var8 * var8
                  + (-var0 + 3.0 * var2 - 3.0 * var4 + var6) * var8 * var8 * var8
            );
      }

      private static double method983(double[] var0, double[] var1, double var2) {
         int var4 = 1;

         while (var4 < var0.length && var0[var4] < var2) {
            var4++;
         }

         if (var4 >= var0.length) {
            var4 = var0.length - 1;
         }

         int var5 = Math.max(var4 - 1, 0);
         if (var0[var4] == var0[var5]) {
            return var1[var5];
         } else {
            double var6 = (var2 - var0[var5]) / (var0[var4] - var0[var5]);
            return var1[var5] + var6 * (var1[var4] - var1[var5]);
         }
      }

      private static BufferedReader method984(String var0) {
         InputStream var1 = BumpController.class.getResourceAsStream(var0);
         if (var1 == null) {
            throw new IllegalStateException("missing resource " + var0);
         } else {
            return new BufferedReader(new InputStreamReader(var1, StandardCharsets.UTF_8));
         }
      }

      private static double[] method985(List<Double> var0) {
         double[] var1 = new double[var0.size()];

         for (int var2 = 0; var2 < var1.length; var2++) {
            var1[var2] = (Double)var0.get(var2);
         }

         return var1;
      }

      private static int method986(int var0, int var1, int var2) {
         return Math.max(var1, Math.min(var0, var2));
      }
   }

   public static final class Entry3 {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public double doubleValue7;
      public double doubleValue8;
      public double doubleValue9;
      public double doubleValue10;
      public double doubleValue11;
      public double doubleValue12;
      public double doubleValue13;
      public double doubleValue14;
      public double doubleValue15;
      public double doubleValue16;
      public double doubleValue17;
      public double doubleValue18;
      public double doubleValue19;
   }

   public static final class Entry4 {
      public final int intValue;
      public final int intValue2;
      public final double[] doubleValues;
      public final double[] doubleValues2;
      public final double doubleValue;
      public final double doubleValue2;
      public final double doubleValue3;
      public final double doubleValue4;
      public final double doubleValue5;
      public final double doubleValue6;
      public final double doubleValue7;
      public final double doubleValue8;
      public final double doubleValue9;
      public final double doubleValue10;
      public final double doubleValue11;
      public final double doubleValue12;
      public final double doubleValue13;

      private Entry4(
         int var1,
         int var2,
         double[] var3,
         double[] var4,
         double var5,
         double var7,
         double var9,
         double var11,
         double var13,
         double var15,
         double var17,
         double var19,
         double var21,
         double var23,
         double var25,
         double var27,
         double var29
      ) {
         this.intValue = var1;
         this.intValue2 = var2;
         this.doubleValues = var3;
         this.doubleValues2 = var4;
         this.doubleValue = var5;
         this.doubleValue2 = var7;
         this.doubleValue3 = var9;
         this.doubleValue4 = var11;
         this.doubleValue5 = var13;
         this.doubleValue6 = var15;
         this.doubleValue7 = var17;
         this.doubleValue8 = var19;
         this.doubleValue9 = var21;
         this.doubleValue10 = var23;
         this.doubleValue11 = var25;
         this.doubleValue12 = var27;
         this.doubleValue13 = var29;
      }
   }

   private static final class Entry5 {
      public final double[] doubleValues;
      public final double[] doubleValues2;
      public final double doubleValue;
      public final double doubleValue2;

      public Entry5(double[] var1, double[] var2, double var3, double var5) {
         this.doubleValues = var1;
         this.doubleValues2 = var2;
         this.doubleValue = var3;
         this.doubleValue2 = var5;
      }
   }

   private static final class Entry6 {
      public final double[] doubleValues;
      public final double[] doubleValues2;
      public final double[] doubleValues3;
      public final double[] doubleValues4;

      public Entry6(double[] var1, double[] var2, double[] var3, double[] var4) {
         this.doubleValues = var1;
         this.doubleValues2 = var2;
         this.doubleValues3 = var3;
         this.doubleValues4 = var4;
      }
   }
}

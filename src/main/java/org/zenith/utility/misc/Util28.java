package org.zenith.utility.misc;

import net.minecraft.util.math.MathHelper;
import org.zenith.utility.game.Class28;
import org.zenith.utility.render.Glowboost;

public final class Util28 {
   public static final int CONST_intValue = 0;
   public static final int CONST_intValue2 = 1;
   public static final int CONST_intValue3 = 2;
   private static final float CONST_floatValue = (float) (Math.PI * 2);

   private Util28() {
   }

   public static void method7552(Glowboost var0, Class28 var1, float var2, int var3, float var4, float var5) {
      float var6 = (float)(var1.doubleValue13 * 0.8 + 0.45) * var4;
      int var7 = 26 + (int)(var5 * 0.42F);
      double var8 = var0.getDoubleValue4();
      double var10 = var0.getDoubleValue5();
      double var12 = var0.getDoubleValue6();
      double var14 = var0.getDoubleValue7();
      double var16 = var0.getDoubleValue8();
      double var18 = var0.getDoubleValue9();

      for (int var20 = 0; var20 < 3; var20++) {
         float var21 = var20 * 0.12F;
         if (!(var2 <= var21)) {
            float var22 = (var2 - var21) / (1.0F - var21);
            if (!(var22 >= 1.0F)) {
               float var23 = (1.0F - var22) * (1.0F - var22) * (var22 < 0.1F ? var22 / 0.1F : 1.0F) * (var20 == 0 ? 1.0F : 0.6F);
               if (!(var23 < 0.02F)) {
                  int var24 = ColorUtils.method6439(var3, var23);
                  float var25 = var6 * (0.12F + Glowboost.easeOut(var22) * (1.0F + var20 * 0.42F));
                  double var26 = 0.0;
                  double var28 = 0.0;
                  double var30 = 0.0;

                  for (int var32 = 0; var32 <= var7; var32++) {
                     float var33 = (float)var32 / var7 * (float) (Math.PI * 2) + var20 * 0.4F;
                     double var34 = var25 * (1.0F + 0.05F * MathHelper.sin(var33 * 5.0F + var1.floatValue + var20));
                     float var36 = MathHelper.cos(var33);
                     float var37 = MathHelper.sin(var33);
                     double var38 = var1.doubleValue + (var8 * var36 + var14 * var37) * var34;
                     double var40 = var1.doubleValue2 + (var10 * var36 + var16 * var37) * var34;
                     double var42 = var1.doubleValue3 + (var12 * var36 + var18 * var37) * var34;
                     if (var32 > 0) {
                        var0.method3236(var26, var28, var30, var38, var40, var42, var24);
                     }

                     if (var20 == 0 && var32 % 5 == 0) {
                        var0.method3250(var38, var40, var42, 0.1F * var4, var3, var23 * 0.5F);
                     }

                     var26 = var38;
                     var28 = var40;
                     var30 = var42;
                  }
               }
            }
         }
      }

      float var44 = 1.0F - Math.min(1.0F, var2 * 3.2F);
      if (var44 > 0.01F) {
         var0.method3250(var1.doubleValue, var1.doubleValue2, var1.doubleValue3, 0.5F * var4 * (0.4F + var44), var3, var44 * 0.85F);
         var0.method3250(var1.doubleValue, var1.doubleValue2, var1.doubleValue3, 1.2F * var4 * (0.5F + var44), var3, var44 * 0.3F);
      }

      float var45 = Math.min(1.0F, var2 * 1.5F);
      float var46 = (1.0F - var45) * (1.0F - var45);
      if (var46 > 0.02F) {
         int var47 = ColorUtils.method6439(var3, var46);
         float var48 = var6 * (0.5F + Glowboost.easeOut(var45) * 1.6F);

         for (int var49 = 0; var49 < 4; var49++) {
            float var50 = var49 / 4.0F * (float) (Math.PI * 2) + 0.39F + var1.floatValue;
            float var27 = MathHelper.cos(var50);
            float var51 = MathHelper.sin(var50);
            double var29 = var8 * var27 + var14 * var51;
            double var31 = var10 * var27 + var16 * var51;
            double var52 = var12 * var27 + var18 * var51;
            var0.method3236(
               var1.doubleValue + var29 * var48 * 0.3,
               var1.doubleValue2 + var31 * var48 * 0.3,
               var1.doubleValue3 + var52 * var48 * 0.3,
               var1.doubleValue + var29 * var48,
               var1.doubleValue2 + var31 * var48,
               var1.doubleValue3 + var52 * var48,
               var47
            );
         }
      }
   }

   public static void method7553(Glowboost var0, Class28 var1, float var2, int var3, float var4, float var5, float var6) {
      int var7 = Math.max(5, (int)(var6 * 0.55F));
      float var8 = Glowboost.method3266(var2);
      if (!(var8 < 0.01F)) {
         float var9 = var2 * var1.intValue2 / 1000.0F;
         Class42 var10 = var1.method4654(1, var7, var4);
         var10.method7382(var9);

         for (int var11 = 0; var11 < var7; var11++) {
            double var12 = var10.method7392(var11);
            double var14 = var10.method7393(var11);
            double var16 = var10.method7394(var11);
            double var18 = var10.method7385(var11);
            double var20 = var10.method7386(var11);
            double var22 = var10.method7387(var11);
            float var24 = (0.16F + Glowboost.method3268(var1.longValue2, var11, 3) * 0.24F) * var5;
            float var25 = var24 * 0.3F;
            double var26 = -var16;
            double var30 = Math.sqrt(var26 * var26 + var12 * var12);
            double var28;
            if (var30 < 1.0E-6) {
               var26 = 1.0;
               var28 = 0.0;
            } else {
               var26 /= var30;
               var28 = var12 / var30;
            }

            double var32 = var14 * var28;
            double var34 = var16 * var26 - var12 * var28;
            double var36 = -var14 * var26;
            float var38 = Glowboost.method3268(var1.longValue2, var11, 5) * (float) (Math.PI * 2)
               + var9 * (3.0F + Glowboost.method3268(var1.longValue2, var11, 8) * 7.0F);
            var38 = method7554(var0, var18, var20, var22, var12, var14, var16, var26, var32, var34, var36, var28, var38);
            float var39 = MathHelper.cos(var38) * var25;
            float var40 = MathHelper.sin(var38) * var25;
            double var41 = var26 * var39 + var32 * var40;
            double var43 = var34 * var40;
            double var45 = var28 * var39 + var36 * var40;
            double var47 = var32 * var39 - var26 * var40;
            double var49 = var34 * var39;
            double var51 = var36 * var39 - var28 * var40;
            float var53 = var8 * (0.55F + 0.45F * Glowboost.method3268(var1.longValue2, var11, 6));
            int var54 = ColorUtils.method6439(var3, var53);
            int var55 = ColorUtils.method6439(var3, var53 * 0.7F);
            double var56 = var18 + var12 * var24 * 0.62;
            double var58 = var20 + var14 * var24 * 0.62;
            double var60 = var22 + var16 * var24 * 0.62;
            double var62 = var18 - var12 * var24 * 0.38;
            double var64 = var20 - var14 * var24 * 0.38;
            double var66 = var22 - var16 * var24 * 0.38;
            method7555(var0, var56, var58, var60, var62, var64, var66, var18, var20, var22, var41, var43, var45, var54);
            method7555(var0, var56, var58, var60, var62, var64, var66, var18, var20, var22, var47, var49, var51, var55);
            var0.method3250(var18, var20, var22, var24 * 2.1F, var3, var53 * (0.5F + 0.35F * var10.method7398(var11)));
         }
      }
   }

   private static float method7554(
      Glowboost var0,
      double var1,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      double var19,
      double var21,
      float var23
   ) {
      double var24 = var0.getDoubleValue() - var1;
      double var26 = var0.getDoubleValue2() - var3;
      double var28 = var0.getDoubleValue3() - var5;
      double var30 = var9 * var28 - var11 * var26;
      double var32 = var11 * var24 - var7 * var28;
      double var34 = var7 * var26 - var9 * var24;
      double var36 = Math.sqrt(var30 * var30 + var32 * var32 + var34 * var34);
      if (var36 < 1.0E-6) {
         return var23;
      } else {
         var30 /= var36;
         var32 /= var36;
         var34 /= var36;
         float var38 = (float)Math.atan2(var30 * var15 + var32 * var17 + var34 * var19, var30 * var13 + var34 * var21);
         float var39 = var23 - var38;
         var39 -= (float)(Math.PI * Math.floor(var39 / Math.PI + 0.5));
         return var38 + var39 * 0.45F;
      }
   }

   private static void method7555(
      Glowboost var0,
      double var1,
      double var3,
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
      int var25
   ) {
      var0.method3236(var1, var3, var5, var13 + var19, var15 + var21, var17 + var23, var25);
      var0.method3236(var13 + var19, var15 + var21, var17 + var23, var7, var9, var11, var25);
      var0.method3236(var7, var9, var11, var13 - var19, var15 - var21, var17 - var23, var25);
      var0.method3236(var13 - var19, var15 - var21, var17 - var23, var1, var3, var5, var25);
   }

   public static void method7556(Glowboost var0, Class28 var1, float var2, int var3, float var4, float var5, float var6) {
      int var7 = Math.max(6, (int)var6);
      float var8 = var2 * var1.intValue2 / 1000.0F;
      float var9 = Glowboost.method3266(var2);
      if (!(var9 < 0.01F)) {
         Class42 var10 = var1.method4654(2, var7, var4);
         var10.method7382(var8);

         for (int var11 = 0; var11 < var7; var11++) {
            double var12 = var10.method7385(var11);
            double var14 = var10.method7386(var11);
            double var16 = var10.method7387(var11);
            double var18 = var10.method7391(var11);
            double var20 = var10.method7395(var11);
            double var22 = var10.method7396(var11);
            double var24 = var10.method7397(var11);
            float var26 = 0.65F + 0.35F * MathHelper.sin(var11 * 12.9F + var8 * 55.0F);
            float var27 = var9 * var26 * (0.6F + 0.4F * Glowboost.method3268(var1.longValue2, var11, 3));
            var27 = Math.min(1.0F, var27 * (1.0F + var10.method7398(var11) * 0.8F));
            if (!(var27 < 0.02F)) {
               double var28 = Math.min(0.55, var18 * 0.05) * var5 * 1.6;
               double var30 = Math.abs(var20 * var0.getDoubleValue10() + var22 * var0.getDoubleValue11() + var24 * var0.getDoubleValue12());
               double var32 = Math.sqrt(Math.max(0.0, 1.0 - var30 * var30));
               double var34 = var28 * (0.35 + 0.65 * var32);
               int var36 = ColorUtils.method6439(var3, var27);
               if (var34 > 1.0E-4) {
                  var0.method3236(var12, var14, var16, var12 - var20 * var34, var14 - var22 * var34, var16 - var24 * var34, var36);
               }

               var0.method3250(var12, var14, var16, 0.09F * var5 * (0.7F + 0.5F * Glowboost.method3268(var1.longValue2, var11, 4)), var3, var27 * 0.65F);
            }
         }
      }
   }
}

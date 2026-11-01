package org.zenith.utility.misc;

import org.zenith.utility.game.FlakeState;
import org.zenith.utility.render.Glowboost;

public final class Util21 {
   private static final long CONST_longValue = 501470595897L;
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 15;

   private Util21() {
   }

   public static void method5356(Glowboost var0, FlakeState var1, WeatherFlakeChannel var2, float var3, float var4, double var5, double var7, double var9) {
      float var11 = var2.floatValue27 * var3;
      if (!(var11 <= 0.02F)) {
         var0.method3235(1.0F);
         byte var12 = 7;
         int var13 = (int)Math.floor(var5 / 2.0);
         int var14 = (int)Math.floor(var9 / 2.0);
         int var15 = var11 > 0.6F ? 4 : 3;

         for (int var16 = -var12; var16 <= var12; var16++) {
            for (int var17 = -var12; var17 <= var12; var17++) {
               int var18 = var13 + var16;
               int var19 = var14 + var17;
               long var20 = method5358(var18, var19);

               for (int var22 = 0; var22 < var15; var22++) {
                  double var23 = Glowboost.method3268(var20, var22, 0) * 2.0F;
                  double var25 = Glowboost.method3268(var20, var22, 1) * 2.0F;
                  double var27 = var4 * (0.16 + 0.22 * Glowboost.method3268(var20, var22, 2));
                  double var29 = var18 * 2.0 + mod(var23 + var2.floatValue20 * var27, 2.0);
                  double var31 = var19 * 2.0 + mod(var25 + var2.floatValue21 * var27, 2.0);
                  double var33 = var29 - var5;
                  double var35 = var31 - var9;
                  double var37 = Math.sqrt(var33 * var33 + var35 * var35);
                  if (!(var37 > 15.0) && (!(var37 > 3.0) || !(var33 * var0.getDoubleValue10() + var35 * var0.getDoubleValue12() < -0.25 * var37))) {
                     double var39 = var1.method4547(var29, var31);
                     if (var39 != Double.NEGATIVE_INFINITY) {
                        double var41 = var39 + 0.25 + 1.15 * Glowboost.method3268(var20, var22, 3);
                        double var43 = Math.abs(var41 - var7);
                        if (!(var43 > 11.0)) {
                           float var45 = (float)Math.min(1.0, (11.0 - var43) / 3.0);
                           float var46 = (float)Math.min(1.0, var37 / 2.6);
                           float var47 = 1.0F - (float)Math.min(1.0, Math.max(0.0, var37 - 15.0 + 4.0) / 4.0);
                           float var48 = 0.55F
                              + 0.45F
                                 * (float)Math.sin(
                                    var4 * (0.25 + 0.35 * Glowboost.method3268(var20, var22, 4)) + Glowboost.method3268(var20, var22, 5) * 6.283F
                                 );
                           float var49 = 0.35F + 0.65F * method5357(var29, var31, var4, var2.floatValue20, var2.floatValue21);
                           float var50 = var11 * var46 * var47 * var45 * var48 * var49 * 0.13F;
                           if (!(var50 <= 0.002F)) {
                              float var51 = Glowboost.method3268(var20, var22, 6);
                              float var52 = 1.2F + 4.6F * var51 * var51;
                              double var53 = (Glowboost.method3268(var20, var22, 7) - 0.5F) * var52 * 0.7;
                              double var55 = (Glowboost.method3268(var20, var22, 8) - 0.5F) * var52 * 0.7;
                              double var57 = (Glowboost.method3268(var20, var22, 9) - 0.5F) * var52 * 0.35;
                              var0.method3251(var29 - var53, var41 - var57, var31 - var55, var52 * 0.78F, var2.intValue8, var50);
                              var0.method3251(var29 + var53, var41 + var57, var31 + var55, var52 * 1.25F, var2.intValue8, var50 * 0.72F);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static float method5357(double var0, double var2, float var4, float var5, float var6) {
      double var7 = var0 - var5 * var4 * 0.35;
      double var9 = var2 - var6 * var4 * 0.35;
      double var11 = Math.sin(var7 * 0.21 + var9 * 0.13) + Math.sin(var7 * 0.07 - var9 * 0.31 + 2.1) + Math.sin((var7 + var9) * 0.045 + var4 * 0.12);
      return (float)Math.clamp((var11 + 3.0) / 6.0, 0.0, 1.0);
   }

   private static double mod(double var0, double var2) {
      double var4 = var0 % var2;
      return var4 < 0.0 ? var4 + var2 : var4;
   }

   private static long method5358(int var0, int var1) {
      return 501470595897L ^ var0 * -6752110988234923001L ^ var1 * -6939452855193903323L;
   }
}

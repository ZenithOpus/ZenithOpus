package org.zenith.utility.misc;

import org.zenith.utility.game.FlakeState;
import org.zenith.utility.render.Glowboost;

public final class Util32 {
   private static final int CONST_intValue = 14;
   private static final double CONST_doubleValue = 0.008;
   private static final float CONST_floatValue = 14.0F;
   private static final float CONST_floatValue2 = 0.2F;
   private static final float CONST_floatValue3 = 0.28F;
   private static final float CONST_floatValue4 = 0.34F;

   private Util32() {
   }

   public static void method7994(Glowboost var0, FlakeState var1, WeatherFlakeChannel var2, float var3, float var4, double var5, double var7, double var9) {
      if (!var2.flag) {
         float var11 = var2.floatValue23 * var3;
         if (!(var11 <= 0.02F)) {
            var0.method3235(0.0F);
            int var12 = (int)Math.floor(var5);
            int var13 = (int)Math.floor(var9);
            int var14 = Math.min(14, var1.getIntValue3());
            float var15 = method8001(var2.floatValues4);
            float var16 = var2.floatValues4[0] / var15;
            float var17 = var2.floatValues4[1] / var15;
            float var18 = var2.floatValues4[2] / var15;

            for (int var19 = -var14; var19 <= var14; var19++) {
               for (int var20 = -var14; var20 <= var14; var20++) {
                  int var21 = var12 + var19;
                  int var22 = var13 + var20;
                  int var23 = var21 - var1.getIntValue();
                  int var24 = var22 - var1.getIntValue2();
                  double var25 = var1.method4544(var23, var24);
                  if (var25 != Double.NEGATIVE_INFINITY && var1.method4546(var23, var24) && !(Math.abs(var25 - var7) > 16.0)) {
                     double var27 = Math.sqrt(var19 * var19 + var20 * var20);
                     if (!(var27 > var14)) {
                        float var29 = 1.0F - method8002(var14 - 3.5F, var14, (float)var27);
                        if (!(var29 <= 0.02F)) {
                           double var30 = var25 + 0.008;
                           method7995(var0, var2, var21, var22, var30, var11, var29, var21 + 0.5 - var5, var30 - var7, var22 + 0.5 - var9, var16, var17, var18);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void method7995(
      Glowboost var0,
      WeatherFlakeChannel var1,
      int var2,
      int var3,
      double var4,
      float var6,
      float var7,
      double var8,
      double var10,
      double var12,
      float var14,
      float var15,
      float var16
   ) {
      method7997(var0, var2, var4, var3, method8000(var1.intValue4, var6 * var7), false);
      float var17 = method7996(var1, var8, var10, var12, var14, var15, var16);
      int var18 = method7999(var1.intValue5, (int)(255.0F * Math.min(0.34F, var17 * var6 * var7)));
      method7997(var0, var2, var4 + 0.008, var3, var18, true);
   }

   private static float method7996(WeatherFlakeChannel var0, double var1, double var3, double var5, float var7, float var8, float var9) {
      double var10 = Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
      if (var10 < 1.0E-4) {
         return 0.0F;
      } else {
         double var12 = var1 / var10;
         double var14 = var3 / var10;
         double var16 = var5 / var10;
         float var18 = method7998((float)Math.abs(var14));
         float var19 = (float)Math.max(0.0, var12 * var7 - var14 * var8 + var16 * var9);
         var19 = (float)Math.pow(var19, 14.0);
         float var20 = var0.floatValue24 * var18 * 0.2F + var19 * var0.floatValue25 * 0.28F;
         float var21 = var0.floatValue24 * 0.34F * 0.2F + 0.06F;
         return var21 + (var20 - var21) * Math.clamp(var0.floatValue26, 0.0F, 1.0F);
      }
   }

   private static void method7997(Glowboost var0, double var1, double var3, double var5, int var7, boolean var8) {
      if (var7 >>> 24 != 0) {
         if (var8) {
            var0.method3238(var1, var3, var5, var1 + 1.0, var3, var5, var1 + 1.0, var3, var5 + 1.0, var1, var3, var5 + 1.0, var7);
         } else {
            var0.method3237(var1, var3, var5, var1 + 1.0, var3, var5, var1 + 1.0, var3, var5 + 1.0, var1, var3, var5 + 1.0, var7);
         }
      }
   }

   private static float method7998(float var0) {
      float var1 = 1.0F - Math.clamp(var0, 0.0F, 1.0F);
      float var2 = var1 * var1;
      return 0.02F + 0.98F * var2 * var2 * var1;
   }

   private static int method7999(int var0, int var1) {
      return Math.clamp((long)var1, 0, 255) << 24 | var0 & 16777215;
   }

   private static int method8000(int var0, float var1) {
      return method7999(var0, (int)(ColorUtils.method6423(var0) * Math.clamp(var1, 0.0F, 1.0F)));
   }

   private static float method8001(float[] var0) {
      float var1 = (float)Math.sqrt(var0[0] * var0[0] + var0[1] * var0[1] + var0[2] * var0[2]);
      return var1 < 1.0E-5F ? 1.0F : var1;
   }

   private static float method8002(float var0, float var1, float var2) {
      float var3 = Math.clamp((var2 - var0) / Math.max(var1 - var0, 1.0E-4F), 0.0F, 1.0F);
      return var3 * var3 * (3.0F - 2.0F * var3);
   }
}

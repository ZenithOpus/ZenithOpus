package org.zenith.utility.misc;

import net.minecraft.util.math.MathHelper;
import org.zenith.utility.game.Class28;
import org.zenith.utility.render.Glowboost;

public final class Util24 {
   public static final int CONST_intValue = 0;
   public static final int CONST_intValue2 = 1;
   public static final int CONST_intValue3 = 2;
   private static final float CONST_floatValue = (float) (Math.PI * 2);
   private static final int CONST_intValue4 = 3;

   private Util24() {
   }

   public static void method6028(Glowboost var0, Class28 var1, float var2, int var3, float var4, float var5, float var6) {
      int var7 = Math.max(8, (int)(var5 * 0.6F));
      float var8 = var2 * var1.intValue2 / 1000.0F;
      float var9 = Glowboost.method3266(var2);
      if (!(var9 < 0.01F)) {
         double var10 = var1.doubleValue15 + var1.doubleValue14 * 0.5;

         for (int var12 = 0; var12 < var7; var12++) {
            float var13 = Glowboost.method3268(var1.longValue2, var12, 0) * (float) (Math.PI * 2);
            float var14 = -0.25F + Glowboost.method3268(var1.longValue2, var12, 1) * 1.45F;
            float var15 = MathHelper.cos(var14);
            double var16 = MathHelper.cos(var13) * var15;
            double var18 = MathHelper.sin(var14);
            double var20 = MathHelper.sin(var13) * var15;
            float var22 = (1.6F + Glowboost.method3268(var1.longValue2, var12, 2) * 3.2F) * var6;
            float var23 = var22 * (1.0F - (float)Math.exp(-2.8F * var8)) / 2.8F;
            double var24 = var1.doubleValue + var16 * var23;
            double var26 = var10 + var18 * var23 - 1.4 * var8 * var8;
            double var28 = var1.doubleValue3 + var20 * var23;
            float var30 = (0.14F + Glowboost.method3268(var1.longValue2, var12, 3) * 0.2F) * var4;
            float var31 = var30 * 0.3F;
            double var32 = var24 - var0.getDoubleValue();
            double var34 = var26 - var0.getDoubleValue2();
            double var36 = var28 - var0.getDoubleValue3();
            double var38 = var18 * var36 - var20 * var34;
            double var40 = var20 * var32 - var16 * var36;
            double var42 = var16 * var34 - var18 * var32;
            double var44 = Math.sqrt(var38 * var38 + var40 * var40 + var42 * var42);
            if (var44 < 1.0E-6) {
               var38 = var0.getDoubleValue4();
               var40 = var0.getDoubleValue5();
               var42 = var0.getDoubleValue6();
               var44 = 1.0;
            }

            var38 = var38 / var44 * var31;
            var40 = var40 / var44 * var31;
            var42 = var42 / var44 * var31;
            float var46 = var9 * (0.55F + 0.45F * Glowboost.method3268(var1.longValue2, var12, 4));
            int var47 = ColorUtils.method6439(var3, var46);
            double var48 = var24 + var16 * var30 * 0.6;
            double var50 = var26 + var18 * var30 * 0.6;
            double var52 = var28 + var20 * var30 * 0.6;
            double var54 = var24 - var16 * var30 * 0.4;
            double var56 = var26 - var18 * var30 * 0.4;
            double var58 = var28 - var20 * var30 * 0.4;
            var0.method3236(var48, var50, var52, var24 + var38, var26 + var40, var28 + var42, var47);
            var0.method3236(var24 + var38, var26 + var40, var28 + var42, var54, var56, var58, var47);
            var0.method3236(var54, var56, var58, var24 - var38, var26 - var40, var28 - var42, var47);
            var0.method3236(var24 - var38, var26 - var40, var28 - var42, var48, var50, var52, var47);
            var0.method3250(var24, var26, var28, var30 * 2.0F, var3, var46 * 0.5F);
         }

         float var60 = 1.0F - Math.min(1.0F, var2 * 2.6F);
         if (var60 > 0.01F) {
            var0.method3250(var1.doubleValue, var10, var1.doubleValue3, 0.6F * var4 * (0.4F + var60), var3, var60 * 0.8F);
            var0.method3250(var1.doubleValue, var10, var1.doubleValue3, 1.5F * var4 * (0.5F + var60), var3, var60 * 0.28F);
         }
      }
   }

   public static void method6029(Glowboost var0, Class28 var1, float var2, int var3, float var4, float var5) {
      float var6 = Glowboost.method3267(var2, 0.08F, 0.5F);
      if (!(var6 < 0.01F)) {
         int var7 = 28 + (int)(var5 * 0.3F);
         double var8 = (var1.doubleValue13 * 0.9 + 0.25) * var4;
         double var10 = var1.doubleValue14 + 0.6;

         for (int var12 = 0; var12 < 3; var12++) {
            float var13 = var12 * 0.14F;
            if (!(var2 <= var13)) {
               float var14 = (var2 - var13) / (1.0F - var13);
               if (!(var14 >= 1.0F)) {
                  float var15 = var6 * (1.0F - var14) * (var12 == 0 ? 1.0F : 0.7F);
                  if (!(var15 < 0.02F)) {
                     int var16 = ColorUtils.method6439(var3, var15);
                     double var17 = var1.doubleValue15 + Glowboost.easeOut(var14) * var10;
                     double var19 = var8 * (0.4 + 0.9 * Glowboost.easeOut(var14));
                     float var21 = var2 * 2.2F + var12 * 1.3F + var1.floatValue;
                     double var22 = 0.0;
                     double var24 = 0.0;

                     for (int var26 = 0; var26 <= var7; var26++) {
                        float var27 = (float)var26 / var7 * (float) (Math.PI * 2) + var21;
                        double var28 = var19 * (1.0F + 0.06F * MathHelper.sin(var27 * 4.0F + var12 + var1.floatValue));
                        double var30 = var1.doubleValue + MathHelper.cos(var27) * var28;
                        double var32 = var1.doubleValue3 + MathHelper.sin(var27) * var28;
                        if (var26 > 0) {
                           var0.method3236(var22, var17, var24, var30, var17, var32, var16);
                        }

                        if (var26 % 6 == 0) {
                           var0.method3250(var30, var17, var32, 0.1F * var4, var3, var15 * 0.5F);
                        }

                        var22 = var30;
                        var24 = var32;
                     }
                  }
               }
            }
         }

         int var34 = Math.max(6, (int)(var5 * 0.3F));

         for (int var35 = 0; var35 < var34; var35++) {
            float var36 = Glowboost.method3268(var1.longValue2, var35, 0);
            float var37 = MathHelper.clamp(var2 * 1.5F + var36 * 0.4F, 0.0F, 1.0F);
            float var38 = var36 * (float) (Math.PI * 2) + var2 * 5.5F * (Glowboost.method3268(var1.longValue2, var35, 1) > 0.5F ? 1.0F : -1.0F);
            double var39 = var8 * (0.55 + 0.5 * Glowboost.method3268(var1.longValue2, var35, 2)) * (0.5 + 0.5 * var37);
            double var40 = var1.doubleValue + MathHelper.cos(var38) * var39;
            double var41 = var1.doubleValue3 + MathHelper.sin(var38) * var39;
            double var23 = var1.doubleValue15 + var37 * var10 * (0.6 + 0.5 * Glowboost.method3268(var1.longValue2, var35, 3));
            var0.method3250(var40, var23, var41, 0.09F * var4, var3, var6 * 0.7F);
         }
      }
   }

   public static void method6030(Glowboost var0, Class28 var1, float var2, int var3, float var4, float var5, float var6) {
      int var7 = Math.max(8, (int)(var5 * 0.6F));
      float var8 = var2 * var1.intValue2 / 1000.0F;
      float var9 = Glowboost.method3267(var2, 0.08F, 0.5F);
      if (!(var9 < 0.01F)) {
         float var10 = Glowboost.method3268(var1.longValue2, 0, 77) > 0.5F ? 1.0F : -1.0F;
         double var11 = var1.doubleValue14 + 0.9;
         double var13 = (var1.doubleValue13 * 0.55 + 0.18) * var4;

         for (int var15 = 0; var15 < var7; var15++) {
            float var16 = Glowboost.method3268(var1.longValue2, var15, 0) * (float) (Math.PI * 2);
            float var17 = Glowboost.method3268(var1.longValue2, var15, 1);
            float var18 = Glowboost.method3268(var1.longValue2, var15, 2) * 0.35F;
            float var19 = MathHelper.clamp((var2 - var18) / Math.max(0.15F, 0.75F - var18 * 0.5F), 0.0F, 1.0F);
            if (!(var19 <= 0.0F)) {
               double var20 = var13 * (0.35 + Glowboost.easeOut(var19) * (0.9 + var17 * 2.4));
               double var22 = var10 * var6 * 5.4 / (0.6 + var20);
               double var24 = var16 + var22 * var8;
               double var26 = var1.doubleValue15 + var11 * (0.08 + Glowboost.easeOut(var19) * (0.35 + var17 * 0.85));
               double var28 = Math.cos(var24);
               double var30 = Math.sin(var24);
               double var32 = var1.doubleValue + var28 * var20;
               double var34 = var1.doubleValue3 + var30 * var20;
               float var36 = var9 * var19 * (0.55F + 0.45F * Glowboost.method3268(var1.longValue2, var15, 3));
               if (!(var36 < 0.02F)) {
                  int var37 = ColorUtils.method6439(var3, var36);
                  double var38 = -var30 * var10 + var28 * 0.45;
                  double var40 = var28 * var10 + var30 * 0.45;
                  double var42 = 0.5 + var17;
                  double var44 = Math.sqrt(var38 * var38 + var42 * var42 + var40 * var40);
                  var38 /= var44;
                  var42 /= var44;
                  var40 /= var44;
                  float var46 = (0.14F + Glowboost.method3268(var1.longValue2, var15, 4) * 0.2F) * var4;
                  float var47 = var46 * 0.3F;
                  double var48 = var32 - var0.getDoubleValue();
                  double var50 = var26 - var0.getDoubleValue2();
                  double var52 = var34 - var0.getDoubleValue3();
                  double var54 = var42 * var52 - var40 * var50;
                  double var56 = var40 * var48 - var38 * var52;
                  double var58 = var38 * var50 - var42 * var48;
                  double var60 = Math.sqrt(var54 * var54 + var56 * var56 + var58 * var58);
                  if (var60 < 1.0E-6) {
                     var54 = var0.getDoubleValue4();
                     var56 = var0.getDoubleValue5();
                     var58 = var0.getDoubleValue6();
                     var60 = 1.0;
                  }

                  var54 = var54 / var60 * var47;
                  var56 = var56 / var60 * var47;
                  var58 = var58 / var60 * var47;
                  double var62 = var32 + var38 * var46 * 0.6;
                  double var64 = var26 + var42 * var46 * 0.6;
                  double var66 = var34 + var40 * var46 * 0.6;
                  double var68 = var32 - var38 * var46 * 0.4;
                  double var70 = var26 - var42 * var46 * 0.4;
                  double var72 = var34 - var40 * var46 * 0.4;
                  var0.method3236(var62, var64, var66, var32 + var54, var26 + var56, var34 + var58, var37);
                  var0.method3236(var32 + var54, var26 + var56, var34 + var58, var68, var70, var72, var37);
                  var0.method3236(var68, var70, var72, var32 - var54, var26 - var56, var34 - var58, var37);
                  var0.method3236(var32 - var54, var26 - var56, var34 - var58, var62, var64, var66, var37);
                  var0.method3250(var32, var26, var34, var46 * 2.0F, var3, var36 * 0.5F);
                  double var74 = var32;
                  double var76 = var26;
                  double var78 = var34;

                  for (int var80 = 1; var80 <= 3; var80++) {
                     double var81 = var24 - var22 * 0.055 * var80;
                     double var83 = var1.doubleValue + Math.cos(var81) * var20;
                     double var85 = var1.doubleValue3 + Math.sin(var81) * var20;
                     double var87 = var26 - var11 * 0.012 * var80;
                     float var89 = var36 * (1.0F - var80 / 4.0F) * 0.6F;
                     var0.method3236(var74, var76, var78, var83, var87, var85, ColorUtils.method6439(var3, var89));
                     var74 = var83;
                     var76 = var87;
                     var78 = var85;
                  }
               }
            }
         }
      }
   }
}

package org.zenith.utility.misc;

import net.minecraft.util.math.MathHelper;
import org.zenith.utility.render.Glowboost;

public final class Util12 {
   private static final float CONST_floatValue = (float) (Math.PI * 2);
   private static final long CONST_longValue = 25214903917L;
   private static final double CONST_doubleValue = 4096.0;
   private static final int CONST_intValue = 14;
   private static final float CONST_floatValue2 = 0.42F;
   private static final double CONST_doubleValue2 = 3.4;
   private static final double CONST_doubleValue3 = 1.9;

   private Util12() {
   }

   public static void method2340(
      Glowboost var0,
      double var1,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      float var13,
      int var14,
      float var15,
      float var16,
      ColorProvider var17
   ) {
      for (int var18 = 0; var18 < var14; var18++) {
         method2341(var0, var18, var1, var3, var5, var7, var9, var11, var13, var15, var16, var17.color(var18 * 29));
      }
   }

   private static void method2341(
      Glowboost var0,
      int var1,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      float var14,
      float var15,
      float var16,
      int var17
   ) {
      double var18 = method2342(var1, 0);
      double var20 = method2342(var1, 1);
      double var22 = method2342(var1, 2);
      double var24 = method2343(var18, var2, var15);
      double var26 = method2343(var20, var4, var15);
      double var28 = method2343(var22, var6, var15);
      double var30 = var24 - var18;
      double var32 = var26 - var20;
      double var34 = var28 - var22;
      float var36 = method2344(var24 - var2, var26 - var4, var28 - var6, var15);
      if (!(var36 < 0.01F)) {
         float var37 = Glowboost.method3268(25214903917L, var1, 11) * 0.4F;
         float var38 = MathHelper.clamp((var16 - var37) / (1.0F - var37), 0.0F, 1.0F);
         if (!(var38 <= 0.0F)) {
            float var39 = var36 * Glowboost.easeOut(var38);
            float var40 = 1.5F + Glowboost.method3268(25214903917L, var1, 3) * 3.4F;
            float var41 = 0.16F + Glowboost.method3268(25214903917L, var1, 4) * 0.3F;
            float var42 = 0.14F + Glowboost.method3268(25214903917L, var1, 5) * 0.26F;
            float var43 = 0.17F + Glowboost.method3268(25214903917L, var1, 6) * 0.32F;
            float var44 = Glowboost.method3268(25214903917L, var1, 7) * (float) (Math.PI * 2);
            float var45 = Glowboost.method3268(25214903917L, var1, 8) * (float) (Math.PI * 2);
            float var46 = Glowboost.method3268(25214903917L, var1, 9) * (float) (Math.PI * 2);
            float var47 = 0.42F * (0.6F + Glowboost.method3268(25214903917L, var1, 10) * 0.9F);
            float var48 = 0.72F + 0.28F * MathHelper.sin(var14 * 0.7F + var1 * 1.7F);
            double var49 = 0.0;
            double var51 = 0.0;
            double var53 = 0.0;
            float var55 = 0.0F;
            boolean var56 = false;

            for (int var57 = 0; var57 < 14; var57++) {
               float var58 = var14 - var57 * var47;
               double var59 = var18 + var30 + var40 * MathHelper.sin(var41 * var58 + var44);
               double var61 = var20 + var32 + var40 * 0.5 * MathHelper.sin(var42 * var58 + var45);
               double var63 = var22 + var34 + var40 * MathHelper.cos(var43 * var58 + var46);
               double var65 = var59 - var8;
               double var67 = var61 - var10;
               double var69 = var63 - var12;
               double var71 = Math.sqrt(var65 * var65 + var67 * var67 + var69 * var69);
               if (var71 < 3.4 && var71 > 0.001) {
                  double var73 = (3.4 - var71) / 3.4;
                  double var75 = var73 * var73 * 1.9 / var71;
                  var59 += var65 * var75;
                  var61 += var67 * var75;
                  var63 += var69 * var75;
               }

               float var77 = 1.0F - var57 / 14.0F;
               float var74 = var39 * var48 * var77 * var77;
               if (var56 && var74 > 0.01F && var55 > 0.01F) {
                  var0.method3236(var49, var51, var53, var59, var61, var63, ColorUtils.method6439(var17, Math.min(var74, var55)));
               }

               if (var57 % 2 == 0 && var74 > 0.02F) {
                  var0.method3250(var59, var61, var63, 0.055F + 0.075F * var77, var17, var74 * 0.8F);
               }

               var49 = var59;
               var51 = var61;
               var53 = var63;
               var55 = var74;
               var56 = true;
            }
         }
      }
   }

   private static double method2342(int var0, int var1) {
      return Glowboost.method3268(25214903917L, var0, var1) * 4096.0;
   }

   private static double method2343(double var0, double var2, double var4) {
      double var6 = var4 * 2.0;
      double var8 = var0 - var2;
      var8 -= Math.floor((var8 + var4) / var6) * var6;
      return var2 + var8;
   }

   private static float method2344(double var0, double var2, double var4, double var6) {
      return Math.min(method2345(var0, var6), Math.min(method2345(var2, var6), method2345(var4, var6)));
   }

   private static float method2345(double var0, double var2) {
      double var4 = Math.abs(var0);
      double var6 = var2 * 0.72;
      return var4 <= var6 ? 1.0F : (float)Math.max(0.0, 1.0 - (var4 - var6) / (var2 - var6));
   }
}

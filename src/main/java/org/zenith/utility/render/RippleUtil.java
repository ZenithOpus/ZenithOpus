package org.zenith.utility.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import org.joml.Vector4f;
import org.zenith.Zenith;
import org.zenith.utility.game.FlakeState;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.FrustumHolder;
import org.zenith.utility.misc.Util15;
import org.zenith.utility.misc.WeatherFlakeChannel;

public final class RippleUtil {
   private static final Identifier identifier = Zenith.method1501("visuals/weather/drop.png");
   private static final Identifier identifier2 = Zenith.method1501("visuals/weather/flake.png");
   private static final long CONST_longValue = 374227287185L;
   private static final int CONST_intValue = 22;
   private static final float CONST_floatValue = 7.0F;
   private static final float CONST_floatValue2 = 11.0F;
   private static final float CONST_floatValue3 = 5.0F;
   private static final float CONST_floatValue4 = 6.0F;
   private static final Identifier identifier3 = Zenith.method1501("visuals/weather/ripple.png");
   private static final float CONST_floatValue5 = 0.075F;
   private static final float CONST_floatValue6 = 1.4F;
   private static final float CONST_floatValue7 = 0.42F;
   private static final float CONST_floatValue8 = 9.5F;
   private static final float CONST_floatValue9 = 1.5F;
   private static final float CONST_floatValue10 = 3.2F;
   private static final float CONST_floatValue11 = 3.2F;
   private static final float CONST_floatValue12 = 1.6F;
   private static final float CONST_floatValue13 = 1.1F;
   private static final float CONST_floatValue14 = 0.5F;
   private static final int CONST_intValue2 = 3;
   private static final int CONST_intValue3 = 3;
   private static final float CONST_floatValue15 = 0.085F;
   private static final float CONST_floatValue16 = 0.17F;
   private static final float CONST_floatValue17 = 0.032F;
   private static final float CONST_floatValue18 = 0.046F;
   private static final float CONST_floatValue19 = 2.5F;
   private static final double CONST_doubleValue = Math.toRadians(18.0);
   private static final double CONST_doubleValue2 = 0.25;
   private static final double CONST_doubleValue3 = Math.cos(Math.toRadians(1.0));
   private static boolean flag;
   private static double doubleValue;
   private static double doubleValue2;
   private static double doubleValue3;
   private static double doubleValue4;
   private static double doubleValue5;
   private static double doubleValue6;
   private static double doubleValue7;

   private RippleUtil() {
   }

   public static void method7826(Glowboost var0, FlakeState var1, WeatherFlakeChannel var2, float var3, float var4, double var5, double var7, double var9) {
      method7828(var0);
      method7830(var0, var1, var2, var3, var4, var5, var7, var9, 11.0F, 5.0F, 6.0F, 22, 7.0F);
   }

   public static void method7827(Glowboost var0, FlakeState var1, WeatherFlakeChannel var2, float var3, float var4, double var5, double var7, double var9) {
      flag = false;
      method7830(var0, var1, var2, var3, var4, var5, var7, var9, 5.5F, 3.4F, 2.0F, 5, 1.0E9F);
   }

   private static void method7828(Glowboost var0) {
      flag = false;
      MinecraftClient var1 = MinecraftClient.getInstance();
      Frustum var2 = FrustumHolder.getFrustum();
      ClientPlayerEntity var3 = var1.player;
      if (var2 != null && var3 != null) {
         if (!(var3.nauseaIntensity > 0.0F) && !(var3.lastNauseaIntensity > 0.0F) && !var3.hasStatusEffect(StatusEffects.NAUSEA)) {
            double var4 = var0.getDoubleValue10();
            double var6 = var0.getDoubleValue11();
            double var8 = var0.getDoubleValue12();
            double var10 = 1.0;
            double var12 = 0.0;
            double var14 = 0.0;
            double var16 = 0.0;

            for (Vector4f var21 : var2.getBoundaryPoints()) {
               double var22 = Math.sqrt(var21.x * var21.x + var21.y * var21.y + var21.z * var21.z);
               if (!(var22 > 1.0E-6) || !Double.isFinite(var22)) {
                  return;
               }

               double var24 = var21.x / var22;
               double var26 = var21.y / var22;
               double var28 = var21.z / var22;
               var10 = Math.min(var10, var24 * var4 + var26 * var6 + var28 * var8);
               var12 += var24;
               var14 += var26;
               var16 += var28;
            }

            double var30 = Math.sqrt(var12 * var12 + var14 * var14 + var16 * var16);
            if (!(var30 < 1.0E-6) && !((var12 * var4 + var14 * var6 + var16 * var8) / var30 < CONST_doubleValue3)) {
               double var31 = Math.acos(Math.clamp(var10, -1.0, 1.0));
               double var34 = Util15.method2704();
               double var35 = (double)var1.getWindow().getFramebufferWidth() / Math.max(1, var1.getWindow().getFramebufferHeight());
               var31 = Math.max(var31, Math.atan(var34 * Math.sqrt(1.0 + var35 * var35)));
               var31 += CONST_doubleValue;
               if (var31 < 1.5607963267948965) {
                  doubleValue = Math.cos(var31);
                  doubleValue2 = Math.sin(var31);
                  flag = true;
               }
            }
         }
      }
   }

   private static boolean method7829(Glowboost var0, double var1, double var3, double var5, double var7) {
      if (!flag) {
         return false;
      } else {
         double var9 = var1 - var0.getDoubleValue();
         double var11 = var3 - var0.getDoubleValue2();
         double var13 = var5 - var0.getDoubleValue3();
         double var15 = var9 * var0.getDoubleValue10() + var11 * var0.getDoubleValue11() + var13 * var0.getDoubleValue12();
         double var17 = var7 + 0.25 + var15 * doubleValue2;
         if (var17 < 0.0) {
            return true;
         } else {
            double var19 = var9 * var9 + var11 * var11 + var13 * var13 - var15 * var15;
            return var19 * doubleValue * doubleValue > var17 * var17;
         }
      }
   }

   private static void method7830(
      Glowboost var0,
      FlakeState var1,
      WeatherFlakeChannel var2,
      float var3,
      float var4,
      double var5,
      double var7,
      double var9,
      float var11,
      float var12,
      float var13,
      int var14,
      float var15
   ) {
      if (var2.intValue2 != 0) {
         float var16 = var2.floatValue18 * var3;
         if (!(var16 <= 0.01F)) {
            boolean var17 = var2.intValue2 == 2;
            var0.method3235(0.0F);
            float var18 = 2.0F * Util15.method2704() / Math.max(1, MinecraftClient.getInstance().getWindow().getFramebufferHeight());
            int var19 = (int)Math.floor(var5);
            int var20 = (int)Math.floor(var9);
            float var21 = (var17 ? 3.2F : 3.2F) * var16;
            float var22 = (var17 ? 1.5F : 9.5F) * Math.max(0.05F, var2.floatValue19);
            float var23 = 0.72F + 0.28F * (float)Math.sin(var4 * 0.53) + 0.18F * (float)Math.sin(var4 * 1.31 + 2.1);
            float var24 = var2.floatValue20 * var23;
            float var25 = var2.floatValue21 * var23;
            float var26 = Math.abs(var24) + Math.abs(var25);
            var0.method3244(var17 ? identifier2 : identifier);
            if (var17) {
               method7834(var0);
            }

            float var27 = var17 ? var12 * 1.6F : var12 * 1.8F;
            float var28 = var17 ? var13 : var13 * 1.35F;
            float var29 = var27 + var28;
            double var30 = var7 - var28;
            double var32 = var7 + var27;
            float var34 = var17 ? 1.1F : 1.6F;
            float var35 = var29 / var11;
            int var36 = Math.min(var14, var1.getIntValue3());
            int var37 = var36 * var36;

            for (int var38 = -var36; var38 <= var36; var38++) {
               for (int var39 = -var36; var39 <= var36; var39++) {
                  int var40 = var38 * var38 + var39 * var39;
                  if (var40 <= var37) {
                     int var41 = var19 + var38;
                     int var42 = var20 + var39;
                     double var43 = var41 + 0.5 - var5;
                     double var45 = var42 + 0.5 - var9;
                     float var47 = (float)Math.sqrt(var43 * var43 + var45 * var45);
                     if (!method7837(var0, var43, var45, var47)) {
                        double var48 = var1.method4549(var41, var42);
                        if (var48 != Double.NEGATIVE_INFINITY && !(var48 > var32)) {
                           float var50 = Math.min(1.0F, var47 / var34);
                           float var51 = 1.0F - method7836(var36 - 4.0F, var36, var47);
                           float var52 = var50 * var51;
                           if (!(var52 <= 0.02F)) {
                              long var53 = method7838(var41, var42);
                              float var55 = var21 * var35 / (1.0F + var47 / var15);
                              int var56 = (int)var55;
                              if (Glowboost.method3268(var53, 0, 7) < var55 - var56) {
                                 var56++;
                              }

                              for (int var57 = 0; var57 < var56; var57++) {
                                 if (var17) {
                                    method7833(
                                       var0, var2, var53, var57, var48, var4, var22, var3 * var52, var41, var42, var24, var25, var26, var29, var30, var32
                                    );
                                 } else {
                                    method7831(
                                       var0,
                                       var2,
                                       var53,
                                       var57,
                                       var48,
                                       var4,
                                       var22,
                                       var3 * var52,
                                       var41,
                                       var42,
                                       var24,
                                       var25,
                                       var29,
                                       var30,
                                       var32,
                                       var18,
                                       var47
                                    );
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (!var17) {
               method7832(var0, var1, var2, var3, var4, var5, var7, var9, var14);
            }
         }
      }
   }

   private static void method7831(
      Glowboost var0,
      WeatherFlakeChannel var1,
      long var2,
      int var4,
      double var5,
      float var7,
      float var8,
      float var9,
      int var10,
      int var11,
      float var12,
      float var13,
      float var14,
      double var15,
      double var17,
      float var19,
      float var20
   ) {
      float var21 = Glowboost.method3268(var2, var4, 0);
      float var22 = Glowboost.method3268(var2, var4, 1);
      float var23 = Glowboost.method3268(var2, var4, 2);
      float var24 = Glowboost.method3268(var2, var4, 4);
      float var25 = var8 * (0.8F + 0.45F * var24);
      double var26 = mod(var7 * var25 + var23 * var14, var14);
      double var28 = method7835(-var26, var14, var15);
      if (!(var28 > var17)) {
         double var30 = var28 - var5;
         if (!(var30 < 0.0)) {
            float var32 = var25 * 0.075F * (0.62F + 0.76F * Glowboost.method3268(var2, var4, 11));
            float var33 = var32 * 0.5F;
            float var34 = Math.max(0.014F + 0.016F * var24, var19 * var20 * 1.4F);
            float var35 = 0.05F + 0.04F * Glowboost.method3268(var2, var4, 6);
            double var36 = var10 + var21 - var12 * 0.55 * var26;
            double var38 = var11 + var22 - var13 * 0.55 * var26;
            if (!method7829(var0, var36, var28, var38, var35 * 1.17F + var32 + var34)) {
               double var40 = Math.sin(var28 * 1.7 + var23 * 6.283) * var35;
               double var42 = var36 + var40;
               double var44 = var38 + var40 * 0.6;
               float var46 = Math.min(1.0F, (float)var30 / 0.45F);
               float var47 = Math.min(1.0F, (float)(var17 - var28) / 2.5F);
               float var48 = Math.min(1.0F, var20 / 3.2F);
               float var49 = var9 * var46 * var47 * var48 * (0.36F + 0.48F * var24 * var24);
               int var50 = ColorUtils.method6439(var1.intValue3, var49);
               float var51 = (Glowboost.method3268(var2, var4, 12) - 0.5F) * 0.11F;
               double var52 = var12 * 0.55 + var51;
               double var54 = var13 * 0.55 + var51 * 0.6;
               double var56 = Math.sqrt(var52 * var52 + 1.0 + var54 * var54);
               var0.method3249(
                  var42 + var52 / var56 * var33,
                  var28 + var33 / var56,
                  var44 + var54 / var56 * var33,
                  var52,
                  1.0,
                  var54,
                  var33,
                  var34,
                  0.0F,
                  0.0F,
                  1.0F,
                  1.0F,
                  var50
               );
            }
         }
      }
   }

   private static void method7832(
      Glowboost var0, FlakeState var1, WeatherFlakeChannel var2, float var3, float var4, double var5, double var7, double var9, int var11
   ) {
      float var12 = var2.floatValue22 * var2.floatValue18 * var3;
      if (!(var12 <= 0.02F)) {
         var0.method3244(identifier3);
         var0.method3235(0.0F);
         int var13 = (int)Math.floor(var5);
         int var14 = (int)Math.floor(var9);
         int var15 = Math.min(9, Math.min(var11, var1.getIntValue3()));

         for (int var16 = -var15; var16 <= var15; var16++) {
            for (int var17 = -var15; var17 <= var15; var17++) {
               int var18 = var13 + var16;
               int var19 = var14 + var17;
               double var20 = var1.method4549(var18, var19);
               if (var20 != Double.NEGATIVE_INFINITY && !(Math.abs(var20 - var7) > 8.0)) {
                  float var22 = (float)Math.sqrt(var16 * var16 + var17 * var17);
                  if (!(var22 > var15) && !method7837(var0, var16, var17, var22)) {
                     float var23 = (1.0F - method7836(var15 - 3.0F, var15, var22)) * Math.min(1.0F, var22 / 1.5F);
                     if (!(var23 <= 0.02F)) {
                        long var24 = method7838(var18, var19) ^ 93L;
                        int var26 = var12 > 0.6F ? 2 : 1;

                        for (int var27 = 0; var27 < var26; var27++) {
                           float var28 = 0.9F + 0.7F * Glowboost.method3268(var24, var27, 1);
                           float var29 = (float)mod(var4 + Glowboost.method3268(var24, var27, 2) * var28, var28);
                           if (!(var29 > 0.42F)) {
                              float var30 = var29 / 0.42F;
                              float var31 = 0.06F + 0.2F * (float)Math.sqrt(var30);
                              int var32 = ColorUtils.method6439(var2.intValue3, var12 * var23 * (1.0F - var30) * 0.55F);
                              if (var32 >>> 24 != 0) {
                                 double var33 = var18 + Glowboost.method3268(var24, var27, 3);
                                 double var35 = var19 + Glowboost.method3268(var24, var27, 4);
                                 if (!method7829(var0, var33, var20 + 0.02, var35, var31 * 1.42F)) {
                                    var0.method3246(var33, var20 + 0.02, var35, 1.0, 0.0, var31, var31, 0.0F, 0.0F, 1.0F, 1.0F, var32);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         var0.method3244(identifier);
      }
   }

   private static void method7833(
      Glowboost var0,
      WeatherFlakeChannel var1,
      long var2,
      int var4,
      double var5,
      float var7,
      float var8,
      float var9,
      int var10,
      int var11,
      float var12,
      float var13,
      float var14,
      float var15,
      double var16,
      double var18
   ) {
      float var20 = Glowboost.method3268(var2, var4, 0);
      float var21 = Glowboost.method3268(var2, var4, 1);
      float var22 = Glowboost.method3268(var2, var4, 2);
      boolean var23 = Glowboost.method3268(var2, var4, 9) < 0.34F;
      float var24 = Glowboost.method3268(var2, var4, 5);
      float var25 = var24 * var24;
      float var26 = var23 ? var8 * (2.1F + 0.7F * var25) : var8 * (1.25F - 0.5F * var25);
      double var27 = mod(var7 * var26 + var22 * var15, var15);
      double var29 = method7835(-var27, var15, var16);
      if (!(var29 > var18)) {
         float var31 = var23 ? 0.0F : (0.55F + 0.45F * var25) * 0.22F;
         var29 += Math.sin(var7 * 0.83 + var22 * 6.283F) * var31;
         double var32 = var29 - var5;
         if (!(var32 < 0.0)) {
            float var34 = var23 ? 0.032F + 0.046F * var25 : 0.085F + 0.17F * var25;
            float var35 = (0.2F + 0.5F * var14) * (0.55F + 0.75F * var25) * (var23 ? 0.35F : 1.0F);
            double var36 = var10 + var20 - var12 * 0.55 * var27;
            double var38 = var11 + var21 - var13 * 0.55 * var27;
            float var40 = var35 * 1.3F + var34 * 1.42F;
            if (var14 >= 0.7F) {
               var40 += 0.75F * (Math.abs(var12) + Math.abs(var13) + 0.2F);
            }

            if (!method7829(var0, var36, var29, var38, var40)) {
               float var41 = var22 * 6.283F + var7 * (0.55F + 0.45F * Glowboost.method3268(var2, var4, 4));
               double var42 = var36 + Math.sin(var41) * var35;
               double var44 = var38 + Math.cos(var41 * 0.73F) * var35 * 0.8;
               float var46 = var22 * 6.283F + var7 * (1.1F + 2.3F * Glowboost.method3268(var2, var4, 10));
               float var47 = var23 ? 1.0F : (float)Math.abs(Math.sin(var46));
               float var48 = var23 ? 1.0F : 0.58F + 0.42F * var47;
               float var49 = Math.min(1.0F, (float)var32 / 0.35F);
               float var50 = Math.min(1.0F, (float)(var18 - var29) / 2.5F);
               float var51 = var9 * var49 * var50 * var48 * (var23 ? 0.62F + 0.28F * var25 : 0.74F + 0.26F * Glowboost.method3268(var2, var4, 6));
               int var52 = ColorUtils.method6439(var1.intValue3, var51);
               int var53 = var23 ? 3 : (int)(Glowboost.method3268(var2, var4, 13) * 3.0F) % 3;
               float var54 = var53 % 2 * 0.5F;
               float var55 = var53 / 2 * 0.5F;
               double var56;
               double var58;
               double var60;
               if (var23) {
                  double var62 = Math.sin(var41 * 0.6) * 0.45;
                  var56 = var12 * 0.3 + var62;
                  var58 = 1.0;
                  var60 = var13 * 0.3 + var62 * 0.6;
               } else {
                  double var69 = var46 * 0.53;
                  double var64 = Math.cos(var69);
                  double var66 = Math.sin(var69);
                  var56 = doubleValue5 * var64 + doubleValue3 * var66;
                  var58 = doubleValue6 * var64;
                  var60 = doubleValue7 * var64 + doubleValue4 * var66;
               }

               var0.method3249(var42, var29, var44, var56, var58, var60, var34, var34 * var48, var54, var55, var54 + 0.5F, var55 + 0.5F, var52);
               if (!(var14 < 0.7F) && !(Glowboost.method3268(var2, var4, 8) > 0.3F)) {
                  float var70 = 0.3F + 0.45F * Glowboost.method3268(var2, var4, 7);
                  var0.method3236(
                     var42,
                     var29,
                     var44,
                     var42 + var12 * var70,
                     var29 + var70 * 0.2,
                     var44 + var13 * var70,
                     ColorUtils.method6439(var1.intValue3, var51 * 0.35F)
                  );
               }
            }
         }
      }
   }

   private static void method7834(Glowboost var0) {
      double var1 = var0.getDoubleValue10();
      double var3 = var0.getDoubleValue11();
      double var5 = var0.getDoubleValue12();
      double var7 = Math.sqrt(var1 * var1 + var5 * var5);
      double var9 = var7 < 1.0E-4 ? 1.0 : var5 / var7;
      double var11 = var7 < 1.0E-4 ? 0.0 : -var1 / var7;
      doubleValue3 = var9;
      doubleValue4 = var11;
      doubleValue5 = -var11 * var3;
      doubleValue6 = var11 * var1 - var9 * var5;
      doubleValue7 = var9 * var3;
   }

   private static double method7835(double var0, float var2, double var3) {
      return var3 + mod(var0 - var3, var2);
   }

   private static float method7836(float var0, float var1, float var2) {
      float var3 = Math.clamp((var2 - var0) / Math.max(var1 - var0, 1.0E-4F), 0.0F, 1.0F);
      return var3 * var3 * (3.0F - 2.0F * var3);
   }

   private static double mod(double var0, double var2) {
      double var4 = var0 - Math.floor(var0 / var2) * var2;
      if (var4 >= var2) {
         var4 -= var2;
      } else if (var4 < 0.0) {
         var4 += var2;
      }

      return var4;
   }

   private static boolean method7837(Glowboost var0, double var1, double var3, float var5) {
      return var5 < 3.0F ? false : var1 * var0.getDoubleValue10() + var3 * var0.getDoubleValue12() < -0.25 * var5;
   }

   private static long method7838(int var0, int var1) {
      return 374227287185L ^ var0 * -7046029254386353131L ^ var1 * -4417276706812531889L;
   }
}

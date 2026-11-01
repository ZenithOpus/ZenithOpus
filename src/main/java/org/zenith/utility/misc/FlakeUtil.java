package org.zenith.utility.misc;

import java.util.Arrays;
import java.util.List;
import net.minecraft.util.Identifier;
import org.zenith.Zenith;
import org.zenith.utility.game.FlakeState;
import org.zenith.utility.game.Print;
import org.zenith.utility.game.Util11;
import org.zenith.utility.render.Glowboost;
import org.zenith.utility.render.SnowShader;
import org.zenith.utility.render.WorldProfiler;

public final class FlakeUtil {
   private static final long CONST_longValue = 488613995701L;
   private static final int CONST_intValue = 48;
   private static final float CONST_floatValue = 0.55F;
   private static final double CONST_doubleValue = 0.035;
   private static final double CONST_doubleValue2 = 0.004;
   private static final double CONST_doubleValue3 = 0.006;
   private static final float CONST_floatValue2 = 0.167F;
   private static final float CONST_floatValue3 = 0.147F;
   private static final float CONST_floatValue4 = 0.054F;
   private static final float CONST_floatValue5 = 0.585F;
   private static final float CONST_floatValue6 = 5.5F;
   private static final float CONST_floatValue7 = 0.24F;
   private static final float CONST_floatValue8 = 0.58F;
   private static final float CONST_floatValue9 = 0.38F;
   private static final float CONST_floatValue10 = 2.0F;
   private static final float CONST_floatValue11 = 0.42F;
   private static final float CONST_floatValue12 = 14.0F;
   private static final float CONST_floatValue13 = 0.16F;
   private static final float[] CONST_floatValues = new float[]{0.78F, 0.84F, 0.96F};
   private static final float CONST_floatValue14 = 1.6F;
   private static final float CONST_floatValue15 = 0.1F;
   private static final float CONST_floatValue16 = 90.0F;
   private static final float CONST_floatValue17 = 0.95F;
   private static final float CONST_floatValue18 = 0.35F;
   private static final long CONST_longValue2 = 400L;
   private static final float[] CONST_floatValues2 = new float[]{0.95F, 0.97F, 1.0F};
   private static final float[] CONST_floatValues3 = new float[]{0.74F, 0.81F, 0.96F};
   private static final FlakeUtil.Entry entry = new FlakeUtil.Entry();
   private static final FlakeUtil.Entry entry2 = new FlakeUtil.Entry();
   private static final Identifier identifier = Zenith.method1501("visuals/weather/flake.png");
   private static final float CONST_floatValue19 = 15.0F;
   private static final int CONST_intValue2 = 18;

   private FlakeUtil() {
   }

   public static float method7506() {
      return 48.0F;
   }

   public static void method7507(
      Glowboost var0, FlakeState var1, WeatherFlakeChannel var2, float var3, Print var4, long var5, double var7, double var9, double var11, float var13
   ) {
      method7510(entry, var0, var1, var2, var3, var4, var5, var7, var9, var11, 48, var13);
   }

   public static void method7508(Glowboost var0, FlakeState var1, WeatherFlakeChannel var2, float var3, double var4, double var6, double var8) {
      method7510(entry2, var0, var1, var2, var3, null, 0L, var4, var6, var8, 6, 1.0F);
   }

   public static float method7509(double var0, double var2) {
      FlakeUtil.Entry var4 = entry;
      float[] var5 = var4.floatValues2;
      if (var4.intValue != 0 && var5.length != 0) {
         int var6 = (int)Math.floor(var0);
         int var7 = (int)Math.floor(var2);
         int var8 = var4.method7540(var6, var7);
         int var9 = var4.method7540(var6 + 1, var7);
         int var10 = var4.method7540(var6, var7 + 1);
         int var11 = var4.method7540(var6 + 1, var7 + 1);
         if (var8 >= 0 && var9 >= 0 && var10 >= 0 && var11 >= 0) {
            float var12 = (float)(var0 - var6);
            float var13 = (float)(var2 - var7);
            float var14 = var5[var8] + (var5[var9] - var5[var8]) * var12;
            float var15 = var5[var10] + (var5[var11] - var5[var10]) * var12;
            return 0.006F + var14 + (var15 - var14) * var13;
         } else {
            return 0.0F;
         }
      } else {
         return 0.0F;
      }
   }

   private static void method7510(
      FlakeUtil.Entry var0,
      Glowboost var1,
      FlakeState var2,
      WeatherFlakeChannel var3,
      float var4,
      Print var5,
      long var6,
      double var8,
      double var10,
      double var12,
      int var14,
      float var15
   ) {
      boolean var16 = var0 == entry;
      if (!var3.flag) {
         var0.flag = false;
         if (var16) {
            Util11.method2336();
            method7516();
         }
      } else {
         float var17 = var3.floatValue23 * Math.clamp(var4, 0.0F, 1.0F);
         if (var17 <= 0.02F) {
            var0.flag = false;
            if (var16) {
               method7516();
            }
         } else {
            var1.method3235(0.0F);
            int var18 = (int)Math.floor(var8);
            int var19 = (int)Math.floor(var12);
            int var20 = Math.min(var14, var2.getIntValue3());
            if (var16) {
               Util11.method2331(var6);
            }

            method7512(var1, var3, var4, var6, var8, var10, var12);
            boolean var21 = var16 && SnowShader.method390();
            method7517(var0, var2, var3, var18, var19, var20, var17 * var15, var8, var10, var12, var6, var21);
            float[] var22 = var0.floatValues2;
            int[] var23 = var0.intValues;
            if (var21) {
               long var24 = method7514(var0, var18, var19, var20, var2) * 31L + Util11.method2333(var6);
               if (var24 == var0.longValue && SnowShader.isFlag3()) {
                  return;
               }

               var0.longValue = var24;
               SnowShader.method393();
               WorldProfiler.method784(1);
            }

            long var52 = var16 ? WorldProfiler.method782() : 0L;
            int var26 = var20 * var20;

            for (int var27 = -var20; var27 <= var20; var27++) {
               for (int var28 = -var20; var28 <= var20; var28++) {
                  if (var27 * var27 + var28 * var28 <= var26) {
                     int var29 = var18 + var27;
                     int var30 = var19 + var28;
                     int var31 = var29 - var2.getIntValue();
                     int var32 = var30 - var2.getIntValue2();
                     double var33 = var2.method4544(var31, var32);
                     if (var33 != Double.NEGATIVE_INFINITY && var2.method4546(var31, var32) && !(Math.abs(var33 - var10) > 64.0)) {
                        int var35 = var0.method7540(var29, var30);
                        int var36 = var0.method7540(var29 + 1, var30);
                        int var37 = var0.method7540(var29 + 1, var30 + 1);
                        int var38 = var0.method7540(var29, var30 + 1);
                        if (var35 >= 0 && var36 >= 0 && var37 >= 0 && var38 >= 0) {
                           float var39 = Util11.method2334(var29, var30, var33, var6);
                           if (!(var39 <= 0.001F)) {
                              float var40 = var2.method4545(var31, var32) ? 0.55F : 1.0F;
                              float var41 = var22[var35] * method7511(var39, var29, var30) * var40;
                              float var42 = var22[var36] * method7511(var39, var29 + 1, var30) * var40;
                              float var43 = var22[var37] * method7511(var39, var29 + 1, var30 + 1) * var40;
                              float var44 = var22[var38] * method7511(var39, var29, var30 + 1) * var40;
                              if (!(var41 + var42 + var43 + var44 <= 0.008F)) {
                                 double var45 = var33 + 0.006;
                                 int var47 = var23[var35];
                                 int var48 = var23[var36];
                                 int var49 = var23[var37];
                                 int var50 = var23[var38];
                                 method7533(
                                    var1,
                                    var21,
                                    var29,
                                    var45 + var41,
                                    var30,
                                    var29,
                                    var45 + var44,
                                    var30 + 1,
                                    var29 + 1,
                                    var45 + var43,
                                    var30 + 1,
                                    var29 + 1,
                                    var45 + var42,
                                    var30,
                                    var47,
                                    var50,
                                    var49,
                                    var48
                                 );
                                 float var51 = var39 * var40;
                                 method7530(var1, var21, var2, var29, var30, var33, var45, 0, -1, var47, var48, var41, var42, var51, var6);
                                 method7530(var1, var21, var2, var29, var30, var33, var45, 1, 0, var48, var49, var42, var43, var51, var6);
                                 method7530(var1, var21, var2, var29, var30, var33, var45, 0, 1, var49, var50, var43, var44, var51, var6);
                                 method7530(var1, var21, var2, var29, var30, var33, var45, -1, 0, var50, var47, var44, var41, var51, var6);
                              }
                           }
                        }
                     }
                  }
               }
            }

            WorldProfiler.method783(22, var52);
         }
      }
   }

   private static float method7511(float var0, int var1, int var2) {
      if (var0 >= 1.0F) {
         return 1.0F;
      } else {
         float var3 = method7537(var1 + 811, var2 - 277) * 0.18F;
         float var4 = Math.clamp((var0 - var3) / (1.0F - var3), 0.0F, 1.0F);
         return Math.max(var4, var0 * 0.55F);
      }
   }

   private static void method7512(Glowboost var0, WeatherFlakeChannel var1, float var2, long var3, double var5, double var7, double var9) {
      List<Util11.Data> var11 = Util11.getItems();
      if (!var11.isEmpty()) {
         var0.method3244(identifier);
         var0.method3235(0.0F);

         for (Object var13_o : (Iterable<?>)(Object)(var11)) {
            Util11.Data var13 = (Util11.Data)var13_o;
            float var14 = (float)(var3 - var13.getLongValue()) / 900.0F;
            if (!(var14 < 0.0F) && !(var14 >= 1.0F)) {
               double var15 = var13.getDoubleValue() + 0.5;
               double var17 = var13.getDoubleValue2() + 0.5;
               double var19 = var15 - var5;
               double var21 = var17 - var9;
               if (!(var19 * var19 + var21 * var21 > 4096.0)) {
                  float var23 = var14 * 0.9F;
                  double var24 = Math.max(0.02, var13.getDoubleValue3() - var13.getDoubleValue4());
                  float var26 = (float)Math.sqrt(2.0 * var24 / 15.0);
                  long var27 = 488613995701L ^ Double.doubleToLongBits(var13.getDoubleValue()) ^ Long.rotateLeft(var13.getLongValue(), 19);

                  for (int var29 = 0; var29 < 18; var29++) {
                     float var30 = var23 - Glowboost.method3268(var27, var29, 0) * 0.14F;
                     if (!(var30 <= 0.0F)) {
                        double var31 = 7.5 * var30 * var30;
                        double var33 = var13.getDoubleValue3() - var31;
                        float var35;
                        if (var33 <= var13.getDoubleValue4()) {
                           var33 = var13.getDoubleValue4() + 0.015;
                           var35 = Math.max(0.0F, 1.0F - (var30 - var26) / 0.3F);
                        } else {
                           var35 = 1.0F;
                        }

                        if (!(var35 <= 0.02F)) {
                           double var36 = Glowboost.method3268(var27, var29, 1) * Math.PI * 2.0;
                           float var38 = 0.12F + 0.26F * Glowboost.method3268(var27, var29, 2);
                           double var39 = 0.35 + var30 * 1.5;
                           double var41 = var15 + Math.cos(var36) * var38 * var39;
                           double var43 = var17 + Math.sin(var36) * var38 * var39;
                           float var45 = 0.028F + 0.04F * Glowboost.method3268(var27, var29, 3);
                           int var46 = ColorUtils.method6439(
                              var1.intValue3, var35 * (0.55F + 0.35F * Glowboost.method3268(var27, var29, 4)) * Math.clamp(var2, 0.0F, 1.0F)
                           );
                           var0.method3249(var41, var33, var43, 0.0, 1.0, 0.0, var45, var45, 0.5F, 0.5F, 1.0F, 1.0F, var46);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean method7513(FlakeState var0, int var1, int var2, double var3, float var5, long var6) {
      int var8 = var1 - var0.getIntValue();
      int var9 = var2 - var0.getIntValue2();
      double var10 = var0.method4544(var8, var9);
      if (var10 != Double.NEGATIVE_INFINITY && !(Math.abs(var10 - var3) > 0.5)) {
         float var12 = Util11.method2334(var1, var2, var10, var6);
         if (var0.method4545(var8, var9)) {
            var12 *= 0.55F;
         }

         if (!var0.method4546(var8, var9)) {
            var12 = 0.0F;
         }

         return var12 < var5 - 0.12F;
      } else {
         return false;
      }
   }

   private static long method7514(FlakeUtil.Entry var0, int var1, int var2, int var3, FlakeState var4) {
      long var5 = 1125899906842597L;
      var5 = var5 * 31L + var1;
      var5 = var5 * 31L + var2;
      var5 = var5 * 31L + var3;
      var5 = var5 * 31L + var4.getIntValue();
      var5 = var5 * 31L + var4.getIntValue2();
      return var5 * 31L + var0.longValue2;
   }

   private static long method7515(FlakeUtil.Entry var0, int var1) {
      long var2 = 1L;

      for (int var4 = 0; var4 < var1; var4++) {
         var2 = var2 * 31L + Float.floatToRawIntBits(var0.floatValues2[var4]);
         var2 = var2 * 31L + var0.intValues[var4];
      }

      return var2;
   }

   private static void method7516() {
      entry.longValue = 0L;
      SnowShader.method401();
   }

   private static void method7517(
      FlakeUtil.Entry var0,
      FlakeState var1,
      WeatherFlakeChannel var2,
      int var3,
      int var4,
      int var5,
      float var6,
      double var7,
      double var9,
      double var11,
      long var13,
      boolean var15
   ) {
      long var16 = method7518(var2, var6) * 31L + (var15 ? 1 : 0);
      boolean var18 = var0.flag
         && var0.intValue5 == var5
         && var0.intValue3 == var3
         && var0.intValue4 == var4
         && var0.intValue6 == var1.getIntValue()
         && var0.intValue7 == var1.getIntValue2()
         && var0.longValue3 == var16
         && var0.flakeState == var1
         && var0.longValue4 == var1.getLongValue2()
         && (var15 || var13 - var0.longValue5 < 400L);
      if (!var18) {
         long var19 = 0L;
         if (var0 == entry) {
            WorldProfiler.method784(0);
            var19 = WorldProfiler.method782();
         }

         method7519(var0, var1, var2, var3, var4, var5, var6);
         method7524(var0, var1, var2, var5, var6, var7, var9, var11, var15);
         int var21 = var0.intValue * var0.intValue;
         System.arraycopy(var0.floatValues, 0, var0.floatValues2, 0, var21);
         var0.longValue2 = method7515(var0, var21);
         var0.flag = true;
         var0.intValue5 = var5;
         var0.intValue6 = var1.getIntValue();
         var0.intValue7 = var1.getIntValue2();
         var0.longValue3 = var16;
         var0.flakeState = var1;
         var0.longValue4 = var1.getLongValue2();
         var0.longValue5 = var13;
         WorldProfiler.method783(21, var19);
      }
   }

   private static long method7518(WeatherFlakeChannel var0, float var1) {
      long var2 = Float.floatToIntBits(var1);
      var2 = var2 * 31L + Float.floatToIntBits(var0.floatValue20);
      var2 = var2 * 31L + Float.floatToIntBits(var0.floatValue21);

      for (int var4 = 0; var4 < 3; var4++) {
         var2 = var2 * 31L + Float.floatToIntBits(var0.floatValues4[var4]);
         var2 = var2 * 31L + Float.floatToIntBits(var0.floatValues5[var4]);
         var2 = var2 * 31L + Float.floatToIntBits(var0.floatValues[var4]);
         var2 = var2 * 31L + Float.floatToIntBits(var0.floatValues2[var4]);
      }

      return var2;
   }

   private static void method7519(FlakeUtil.Entry var0, FlakeState var1, WeatherFlakeChannel var2, int var3, int var4, int var5, float var6) {
      var0.intValue2 = var5 + 2;
      var0.intValue3 = var3;
      var0.intValue4 = var4;
      var0.intValue = var0.intValue2 * 2 + 1;
      int var7 = var0.intValue * var0.intValue;
      if (var0.floatValues.length < var7) {
         var0.floatValues = new float[var7];
         var0.floatValues2 = new float[var7];
         var0.intValues = new int[var7];
         var0.intValue11 = -1;
      }

      int var8 = var0.intValue2;
      float[] var9 = var0.floatValues;
      method7522(var0, var5);
      float[] var10 = var0.floatValues9;
      float var11 = var2.floatValue20;
      float var12 = var2.floatValue21;
      float var13 = (float)Math.sqrt(var11 * var11 + var12 * var12);
      if (var13 < 1.0E-4F) {
         var11 = 0.7071F;
         var12 = 0.7071F;
      } else {
         var11 /= var13;
         var12 /= var13;
      }

      var0.method7542(var0.intValue, var11, var12);
      method7520(var0, var1, var7, var11, var12);
      float[] var14 = var0.floatValues3;
      float[] var15 = var0.floatValues4;

      for (int var16 = -var8; var16 <= var8; var16++) {
         for (int var17 = -var8; var17 <= var8; var17++) {
            int var18 = var0.method7541(var16, var17);
            var9[var18] = var14[var18] * var6 * var10[var18] * var15[var18];
         }
      }

      method7523(var0);
   }

   private static void method7520(FlakeUtil.Entry var0, FlakeState var1, int var2, float var3, float var4) {
      int var5 = var0.intValue2;
      int var6 = var0.intValue;
      long var7 = (long)Float.floatToIntBits(var3) << 32 ^ Float.floatToIntBits(var4) & 4294967295L;
      int var9 = var0.intValue3 - var0.intValue9;
      int var10 = var0.intValue4 - var0.intValue10;
      boolean var11 = var0.flakeState2 == var1
         && var0.longValue6 == var1.getLongValue2()
         && var0.longValue7 == var7
         && var0.intValue8 == var5
         && var0.floatValues3.length >= var2
         && Math.abs(var9) < var6
         && Math.abs(var10) < var6;
      if (!var11 || var9 != 0 || var10 != 0) {
         if (var0.floatValues6.length < var2) {
            var0.floatValues6 = new float[var2];
            var0.floatValues7 = new float[var2];
            var0.floatValues8 = new float[var2];
         }

         int var12 = Math.round(var3 * 1.0F);
         int var13 = Math.round(var4 * 1.0F);
         int var14 = Math.round(var3 * 2.0F);
         int var15 = Math.round(var4 * 2.0F);

         for (int var16 = -var5; var16 <= var5; var16++) {
            for (int var17 = -var5; var17 <= var5; var17++) {
               int var18 = var0.method7541(var16, var17);
               int var19 = var16 + var9;
               int var20 = var17 + var10;
               if (var11 && var19 >= -var5 && var19 <= var5 && var20 >= -var5 && var20 <= var5) {
                  int var21 = var0.method7541(var19, var20);
                  var0.floatValues6[var18] = var0.floatValues3[var21];
                  var0.floatValues7[var18] = var0.floatValues4[var21];
                  var0.floatValues8[var18] = var0.floatValues5[var21];
               } else {
                  method7521(var0, var1, var0.intValue3 + var16, var0.intValue4 + var17, var3, var4, var12, var13, var14, var15, var18);
               }
            }
         }

         float[] var22 = var0.floatValues3;
         var0.floatValues3 = var0.floatValues6;
         var0.floatValues6 = var22;
         var22 = var0.floatValues4;
         var0.floatValues4 = var0.floatValues7;
         var0.floatValues7 = var22;
         var22 = var0.floatValues5;
         var0.floatValues5 = var0.floatValues8;
         var0.floatValues8 = var22;
         var0.flakeState2 = var1;
         var0.longValue6 = var1.getLongValue2();
         var0.longValue7 = var7;
         var0.intValue8 = var5;
         var0.intValue9 = var0.intValue3;
         var0.intValue10 = var0.intValue4;
      }
   }

   private static void method7521(
      FlakeUtil.Entry var0, FlakeState var1, int var2, int var3, float var4, float var5, int var6, int var7, int var8, int var9, int var10
   ) {
      int var11 = var0.method7543(var2, var3);
      float var12;
      if (var0.intValues2[var11] == var2 && var0.intValues3[var11] == var3) {
         var12 = var0.floatValues11[var11];
      } else {
         float var13 = var2 * var4 + var3 * var5;
         float var14 = -var2 * var5 + var3 * var4;
         float var15 = method7536(var13 * 0.24F, var14 * 0.58F) * 0.68F + method7536(var13 * 0.24F * 3.1F + 11.3F, var14 * 0.58F * 2.4F + 4.1F) * 0.32F;
         float var16 = method7536(var13 * 0.9F + 31.1F, var14 * 2.2F + 7.7F);
         var12 = 0.147F * var15 + 0.054F * var16;
         var0.intValues2[var11] = var2;
         var0.intValues3[var11] = var3;
         var0.floatValues11[var11] = var12;
      }

      float var17 = method7529(var1, var2, var3);
      float var18 = 0.35F + 0.65F * var17;
      var0.floatValues6[var10] = 0.167F + var12 * var18 + 0.585F * method7527(var1, var2, var3, var6, var7, var8, var9);
      var0.floatValues7[var10] = method7526(var1, var2, var3);
      var0.floatValues8[var10] = var17;
   }

   private static void method7522(FlakeUtil.Entry var0, int var1) {
      int var2 = var0.intValue2;
      int var3 = var0.intValue * var0.intValue;
      if (var0.intValue11 != var1 || var0.floatValues9.length < var3) {
         if (var0.floatValues9.length < var3) {
            var0.floatValues9 = new float[var3];
            var0.floatValues10 = new float[var3];
         }

         for (int var4 = -var2; var4 <= var2; var4++) {
            for (int var5 = -var2; var5 <= var2; var5++) {
               float var6 = (float)Math.sqrt(var4 * var4 + var5 * var5);
               int var7 = var0.method7541(var4, var5);
               var0.floatValues9[var7] = 1.0F - method7538(var2 - 9.0F, var2 - 2.0F, var6);
               var0.floatValues10[var7] = 1.0F - method7538(var1 - 8.0F, var1, var6);
            }
         }

         var0.intValue11 = var1;
      }
   }

   private static void method7523(FlakeUtil.Entry var0) {
      int var1 = var0.intValue2 - 1;
      float[] var2 = var0.floatValues;
      float[] var3 = var0.floatValues2;

      for (int var4 = 0; var4 < 2.0F; var4++) {
         System.arraycopy(var2, 0, var3, 0, var0.intValue * var0.intValue);

         for (int var5 = -var1; var5 <= var1; var5++) {
            for (int var6 = -var1; var6 <= var1; var6++) {
               float var7 = var3[var0.method7541(var5 - 1, var6)]
                  + var3[var0.method7541(var5 + 1, var6)]
                  + var3[var0.method7541(var5, var6 - 1)]
                  + var3[var0.method7541(var5, var6 + 1)];
               var2[var0.method7541(var5, var6)] = var3[var0.method7541(var5, var6)] * 0.42F + var7 * 0.25F * 0.58000004F;
            }
         }
      }
   }

   private static void method7524(
      FlakeUtil.Entry var0, FlakeState var1, WeatherFlakeChannel var2, int var3, float var4, double var5, double var7, double var9, boolean var11
   ) {
      float[] var12 = var0.floatValues;
      int var13 = var0.intValue3;
      int var14 = var0.intValue4;
      float var15 = method7539(var2.floatValues4);
      float var16 = var2.floatValues4[0] / var15;
      float var17 = var2.floatValues4[1] / var15;
      float var18 = var2.floatValues4[2] / var15;
      float var19 = (var2.floatValues[0] + var2.floatValues2[0]) * 0.5F + 0.16F;
      float var20 = (var2.floatValues[1] + var2.floatValues2[1]) * 0.5F + 0.16F;
      float var21 = (var2.floatValues[2] + var2.floatValues2[2]) * 0.5F + 0.17F;
      float[] var22 = var0.floatValues5;
      float[] var23 = var0.floatValues10;
      int var24 = var0.intValue2 - 1;

      for (int var25 = -var24; var25 <= var24; var25++) {
         for (int var26 = -var24; var26 <= var24; var26++) {
            int var27 = var0.method7541(var25, var26);
            float var28 = var12[var0.method7541(var25 - 1, var26)];
            float var29 = var12[var0.method7541(var25 + 1, var26)];
            float var30 = var12[var0.method7541(var25, var26 - 1)];
            float var31 = var12[var0.method7541(var25, var26 + 1)];
            float var32 = var12[var27];
            float var33 = var22[var27];
            float var34 = (var28 + var29 + var31 + var30) * 0.25F - var32;
            var33 *= Math.clamp(1.0F - var34 * 1.6F, 0.8F, 1.12F);
            float var35 = method7537(var13 + var25 - 401, var14 + var26 + 257);
            if (var11) {
               var0.intValues[var27] = method7532(var32, var33, var35);
            } else {
               float var36 = var23[var27];
               float var37 = (var28 - var29) * 0.5F * 5.5F;
               float var38 = (var30 - var31) * 0.5F * 5.5F;
               float var39 = (float)Math.sqrt(var37 * var37 + 1.0F + var38 * var38);
               var37 /= var39;
               float var40 = 1.0F / var39;
               var38 /= var39;
               float var41 = method7525(var1, var13 + var25, var14 + var26, var37, var40, var38, var16, var17, var18, var5, var7, var9);
               var0.intValues[var27] = method7531(
                  var37, var40, var38, var32, var33, var4 * var36, var41, var35, var19, var20, var21, var2, var16, var17, var18, false
               );
            }
         }
      }
   }

   private static float method7525(
      FlakeState var0, int var1, int var2, float var3, float var4, float var5, float var6, float var7, float var8, double var9, double var11, double var13
   ) {
      float var15 = method7537(var1 + 977, var2 - 613);
      if (var15 > 0.1F) {
         return 0.0F;
      } else {
         double var16 = method7528(var0, var1, var2);
         if (var16 == Double.NEGATIVE_INFINITY) {
            return 0.0F;
         } else {
            float var18 = (float)(var9 - var1);
            float var19 = (float)(var11 - var16);
            float var20 = (float)(var13 - var2);
            float var21 = (float)Math.sqrt(var18 * var18 + var19 * var19 + var20 * var20);
            if (var21 < 0.001F) {
               return 0.0F;
            } else {
               var18 /= var21;
               var19 /= var21;
               var20 /= var21;
               float var22 = var18 + var6;
               float var23 = var19 + var7;
               float var24 = var20 + var8;
               float var25 = (float)Math.sqrt(var22 * var22 + var23 * var23 + var24 * var24);
               if (var25 < 0.001F) {
                  return 0.0F;
               } else {
                  float var26 = (var3 * var22 + var4 * var23 + var5 * var24) / var25;
                  if (var26 <= 0.0F) {
                     return 0.0F;
                  } else {
                     float var27 = Math.clamp(var7 * 3.0F, 0.0F, 1.0F);
                     return (float)Math.pow(var26, 90.0) * var27 * 0.95F;
                  }
               }
            }
         }
      }
   }

   private static float method7526(FlakeState var0, int var1, int var2) {
      int var3 = 0;
      double var4 = Double.NEGATIVE_INFINITY;
      int var6 = 0;
      int var7 = 0;

      for (int var8 = -1; var8 <= 0; var8++) {
         for (int var9 = -1; var9 <= 0; var9++) {
            int var10 = var1 + var8 - var0.getIntValue();
            int var11 = var2 + var9 - var0.getIntValue2();
            double var12 = var0.method4544(var10, var11);
            if (var12 != Double.NEGATIVE_INFINITY && var0.method4546(var10, var11)) {
               var3++;
               if (var4 == Double.NEGATIVE_INFINITY) {
                  var4 = var12;
                  var6 = 1;
               } else if (Math.abs(var12 - var4) < 0.5) {
                  var6++;
               }

               var7 = Math.max(var7, var6);
            }
         }
      }

      return var3 == 0 ? 0.0F : 0.8F + 0.2F * (var7 / 4.0F);
   }

   private static float method7527(FlakeState var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      double var7 = method7528(var0, var1, var2);
      if (var7 == Double.NEGATIVE_INFINITY) {
         return 0.0F;
      } else {
         float var9 = 0.0F;

         for (int var10 = 1; var10 <= 2; var10++) {
            double var11 = var10 == 1 ? method7528(var0, var1 - var3, var2 - var4) : method7528(var0, var1 - var5, var2 - var6);
            if (var11 != Double.NEGATIVE_INFINITY) {
               float var13 = (float)(var11 - var7);
               if (!(var13 < 0.5F)) {
                  var9 = Math.max(var9, Math.min(1.0F, var13 / 2.0F) / var10);
               }
            }
         }

         return var9;
      }
   }

   private static double method7528(FlakeState var0, int var1, int var2) {
      return var0.method4544(var1 - var0.getIntValue(), var2 - var0.getIntValue2());
   }

   private static float method7529(FlakeState var0, int var1, int var2) {
      double var3 = method7528(var0, var1, var2);
      if (var3 == Double.NEGATIVE_INFINITY) {
         return 1.0F;
      } else {
         float var5 = 0.0F;

         for (int var6 = -1; var6 <= 1; var6++) {
            for (int var7 = -1; var7 <= 1; var7++) {
               if (var6 != 0 || var7 != 0) {
                  double var8 = method7528(var0, var1 + var6, var2 + var7);
                  if (var8 != Double.NEGATIVE_INFINITY) {
                     float var10 = (float)(var8 - var3);
                     if (var10 >= 0.5F) {
                        var5 += Math.min(1.0F, var10 / 3.0F);
                     }
                  }
               }
            }
         }

         return 1.0F - Math.min(0.42F, var5 * 0.14F);
      }
   }

   private static void method7530(
      Glowboost var0,
      boolean var1,
      FlakeState var2,
      int var3,
      int var4,
      double var5,
      double var7,
      int var9,
      int var10,
      int var11,
      int var12,
      float var13,
      float var14,
      float var15,
      long var16
   ) {
      if (!(var13 + var14 <= 0.008F)) {
         int var18 = var3 + var9 - var2.getIntValue();
         int var19 = var4 + var10 - var2.getIntValue2();
         double var20 = var2.method4544(var18, var19);
         boolean var22 = var20 == Double.NEGATIVE_INFINITY
            || !var2.method4546(var18, var19)
            || var20 < var5 - 0.5
            || method7513(var2, var3 + var9, var4 + var10, var5, var15, var16);
         if (var22) {
            double var23;
            double var25;
            double var27;
            double var29;
            if (var10 == -1) {
               var23 = var3;
               var25 = var4;
               var27 = var3 + 1;
               var29 = var4;
            } else if (var9 == 1) {
               var23 = var3 + 1;
               var25 = var4;
               var27 = var3 + 1;
               var29 = var4 + 1;
            } else if (var10 == 1) {
               var23 = var3 + 1;
               var25 = var4 + 1;
               var27 = var3;
               var29 = var4 + 1;
            } else {
               var23 = var3;
               var25 = var4 + 1;
               var27 = var3;
               var29 = var4;
            }

            float var31 = var1 ? 1.0F : 0.88F;
            int var32 = method7534(method7535(var11, var31));
            int var33 = method7534(method7535(var12, var31));
            double var34 = var7 - 0.035;
            double var36 = var9 * 0.004;
            double var38 = var10 * 0.004;
            method7533(
               var0,
               var1,
               var23 + var36,
               var7 + var13,
               var25 + var38,
               var27 + var36,
               var7 + var14,
               var29 + var38,
               var27 + var36,
               var34,
               var29 + var38,
               var23 + var36,
               var34,
               var25 + var38,
               var32,
               var33,
               var33,
               var32
            );
         }
      }
   }

   private static int method7531(
      float var0,
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      WeatherFlakeChannel var11,
      float var12,
      float var13,
      float var14,
      boolean var15
   ) {
      float var16 = (0.66F + 0.34F * Math.max(0.0F, var1)) * var4;
      float var17 = Math.max(0.0F, (var0 * var12 + var1 * var13 + var2 * var14 + 0.38F) / 1.38F);
      float var18 = 0.965F + 0.07F * var7;
      float var19 = Math.clamp(var17 * var4, 0.0F, 1.0F);
      float var20 = CONST_floatValues[0] + (1.0F - CONST_floatValues[0]) * var19;
      float var21 = CONST_floatValues[1] + (1.0F - CONST_floatValues[1]) * var19;
      float var22 = CONST_floatValues[2] + (1.0F - CONST_floatValues[2]) * var19;
      float var23;
      float var24;
      float var25;
      if (var15) {
         var23 = CONST_floatValues2[0] * var18 * var4;
         var24 = CONST_floatValues2[1] * var18 * var4;
         var25 = CONST_floatValues2[2] * var18 * var4;
      } else {
         var23 = CONST_floatValues2[0] * var18 * var20 * (var8 * var16 + var11.floatValues5[0] * var17 * 0.46F);
         var24 = CONST_floatValues2[1] * var18 * var21 * (var9 * var16 + var11.floatValues5[1] * var17 * 0.46F);
         var25 = CONST_floatValues2[2] * var18 * var22 * (var10 * var16 + var11.floatValues5[2] * var17 * 0.46F);
      }

      if (!var15) {
         float var26 = (float)Math.pow(Math.max(0.0F, var1 * 0.5F + var17 * 0.5F), 14.0) * 0.16F;
         var23 += var11.floatValues5[0] * var26;
         var24 += var11.floatValues5[1] * var26;
         var25 += var11.floatValues5[2] * var26;
      }

      float var30 = Math.clamp(var3 / 0.16F, 0.0F, 1.0F);
      var23 *= CONST_floatValues3[0] + (1.0F - CONST_floatValues3[0]) * var30;
      var24 *= CONST_floatValues3[1] + (1.0F - CONST_floatValues3[1]) * var30;
      var25 *= CONST_floatValues3[2] + (1.0F - CONST_floatValues3[2]) * var30;
      if (var6 > 0.0F && !var15) {
         var23 += var11.floatValues5[0] * var6;
         var24 += var11.floatValues5[1] * var6;
         var25 += var11.floatValues5[2] * var6;
      }

      return 0xFF000000 | channel(var23 * 255.0F) << 16 | channel(var24 * 255.0F) << 8 | channel(var25 * 255.0F);
   }

   private static int method7532(float var0, float var1, float var2) {
      float var3 = 0.965F + 0.07F * var2;
      float var4 = CONST_floatValues2[0] * var3 * var1;
      float var5 = CONST_floatValues2[1] * var3 * var1;
      float var6 = CONST_floatValues2[2] * var3 * var1;
      float var7 = Math.clamp(var0 / 0.16F, 0.0F, 1.0F);
      var4 *= CONST_floatValues3[0] + (1.0F - CONST_floatValues3[0]) * var7;
      var5 *= CONST_floatValues3[1] + (1.0F - CONST_floatValues3[1]) * var7;
      var6 *= CONST_floatValues3[2] + (1.0F - CONST_floatValues3[2]) * var7;
      return 0xFF000000 | channel(var4 * 255.0F) << 16 | channel(var5 * 255.0F) << 8 | channel(var6 * 255.0F);
   }

   private static void method7533(
      Glowboost var0,
      boolean var1,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      double var14,
      double var16,
      double var18,
      double var20,
      double var22,
      double var24,
      int var26,
      int var27,
      int var28,
      int var29
   ) {
      if (var1) {
         SnowShader.method395(var2, var4, var6, var8, var10, var12, var14, var16, var18, var20, var22, var24, var26, var27, var28, var29);
      } else {
         var0.method3239(var2, var4, var6, var8, var10, var12, var14, var16, var18, var20, var22, var24, var26, var27, var28, var29);
      }
   }

   private static int method7534(int var0) {
      return var0 & 16777215 | -33554432;
   }

   private static int method7535(int var0, float var1) {
      int var2 = var0 >>> 24 & 0xFF;
      int var3 = (int)((var0 >> 16 & 0xFF) * var1);
      int var4 = (int)((var0 >> 8 & 0xFF) * var1);
      int var5 = (int)((var0 & 0xFF) * var1);
      return var2 << 24 | var3 << 16 | var4 << 8 | var5;
   }

   private static float method7536(float var0, float var1) {
      int var2 = (int)Math.floor(var0);
      int var3 = (int)Math.floor(var1);
      float var4 = var0 - var2;
      float var5 = var1 - var3;
      var4 = var4 * var4 * (3.0F - 2.0F * var4);
      var5 = var5 * var5 * (3.0F - 2.0F * var5);
      float var6 = method7537(var2, var3);
      float var7 = method7537(var2 + 1, var3);
      float var8 = method7537(var2, var3 + 1);
      float var9 = method7537(var2 + 1, var3 + 1);
      return (var6 * (1.0F - var4) + var7 * var4) * (1.0F - var5) + (var8 * (1.0F - var4) + var9 * var4) * var5;
   }

   private static float method7537(int var0, int var1) {
      return Glowboost.method3268(488613995701L ^ var0 * -7046029254386353131L, var1, 3);
   }

   private static int channel(float var0) {
      return Math.clamp((long)((int)(var0 + 0.5F)), 0, 255);
   }

   private static float method7538(float var0, float var1, float var2) {
      float var3 = Math.clamp((var2 - var0) / Math.max(var1 - var0, 1.0E-4F), 0.0F, 1.0F);
      return var3 * var3 * (3.0F - 2.0F * var3);
   }

   private static float method7539(float[] var0) {
      float var1 = (float)Math.sqrt(var0[0] * var0[0] + var0[1] * var0[1] + var0[2] * var0[2]);
      return var1 < 1.0E-5F ? 1.0F : var1;
   }

   private static final class Entry {
      private long longValue;
      private float[] floatValues = new float[0];
      private float[] floatValues2 = new float[0];
      private int[] intValues = new int[0];
      private int intValue;
      private int intValue2;
      private int intValue3;
      private int intValue4;
      private long longValue2;
      private boolean flag;
      private int intValue5 = -1;
      private int intValue6 = Integer.MIN_VALUE;
      private int intValue7 = Integer.MIN_VALUE;
      private long longValue3;
      private FlakeState flakeState;
      private long longValue4 = Long.MIN_VALUE;
      private long longValue5;
      private float[] floatValues3 = new float[0];
      private float[] floatValues4 = new float[0];
      private float[] floatValues5 = new float[0];
      private float[] floatValues6 = new float[0];
      private float[] floatValues7 = new float[0];
      private float[] floatValues8 = new float[0];
      private FlakeState flakeState2;
      private long longValue6 = Long.MIN_VALUE;
      private long longValue7;
      private int intValue8 = -1;
      private int intValue9;
      private int intValue10;
      private float[] floatValues9 = new float[0];
      private float[] floatValues10 = new float[0];
      private int intValue11 = -1;
      private int intValue12;
      private int intValue13 = -1;
      private int[] intValues2 = new int[0];
      private int[] intValues3 = new int[0];
      private float[] floatValues11 = new float[0];
      private int intValue14;
      private int intValue15;

      private int method7540(int var1, int var2) {
         int var3 = var1 - this.intValue3;
         int var4 = var2 - this.intValue4;
         return var3 >= -this.intValue2 && var3 <= this.intValue2 && var4 >= -this.intValue2 && var4 <= this.intValue2 ? this.method7541(var3, var4) : -1;
      }

      private int method7541(int var1, int var2) {
         return (var1 + this.intValue2) * this.intValue + var2 + this.intValue2;
      }

      private void method7542(int var1, float var2, float var3) {
         int var4 = Float.floatToRawIntBits(var2);
         int var5 = Float.floatToRawIntBits(var3);
         if (this.intValue13 + 1 < var1) {
            int var6 = 1;

            while (1 << var6 < var1) {
               var6++;
            }

            this.intValue12 = var6;
            this.intValue13 = (1 << var6) - 1;
            int var7 = 1 << var6 * 2;
            this.intValues2 = new int[var7];
            this.intValues3 = new int[var7];
            this.floatValues11 = new float[var7];
            Arrays.fill(this.intValues2, Integer.MIN_VALUE);
         } else if (var4 != this.intValue14 || var5 != this.intValue15) {
            Arrays.fill(this.intValues2, Integer.MIN_VALUE);
         }

         this.intValue14 = var4;
         this.intValue15 = var5;
      }

      private int method7543(int var1, int var2) {
         return (var1 & this.intValue13) << this.intValue12 | var2 & this.intValue13;
      }
   }
}

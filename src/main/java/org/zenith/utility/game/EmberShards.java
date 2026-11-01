package org.zenith.utility.game;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.render.Glowboost;

public final class EmberShards {
   private static final int CONST_intValue = 2000;
   private static final float CONST_floatValue = (float) (Math.PI * 2);
   private static final int CONST_intValue2 = 0;
   private static final int CONST_intValue3 = 1;
   private final List<EmberShards.Entry2> items = new ArrayList<>();
   private final List<EmberShards.Entry4> items2 = new ArrayList<>();
   private final List<EmberShards.Entry> items3 = new ArrayList<>();
   private final List<EmberShards.Entry3> items4 = new ArrayList<>();
   private final Random random = new Random();
   private long longValue;
   private float floatValue;
   private EmberShards.Handler handler;

   public void setHandler(EmberShards.Handler var1) {
      this.handler = var1;
   }

   public void clear() {
      this.items.clear();
      this.items2.clear();
      this.items3.clear();
      this.items4.clear();
      this.floatValue = 0.0F;
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   private void method7577(double var1, double var3, double var5, double var7, double var9, double var11) {
      this.method7581(var1, var3, var5, 0.6F, 0.24F);
      this.method7582(var1, var3, var5, var7, var9, var11, 0.8F, 0.42F, 0.17F);
      this.method7582(var1, var3, var5, var7, var9, var11, 1.4F, 0.55F, 0.06F);

      for (int var13 = 0; var13 < 10; var13++) {
         double var14 = var7 + this.random.nextGaussian() * 0.6;
         double var16 = var9 + this.random.nextGaussian() * 0.6;
         double var18 = var11 + this.random.nextGaussian() * 0.6;
         double var20 = Math.sqrt(var14 * var14 + var16 * var16 + var18 * var18);
         if (!(var20 < 1.0E-4)) {
            double var22 = 13.0 + this.random.nextDouble() * 12.0;
            this.method7583(
               1, var1, var3, var5, var14 / var20 * var22, var16 / var20 * var22, var18 / var20 * var22, 0.09F + this.random.nextFloat() * 0.12F, 0.03F
            );
         }
      }

      for (int var24 = 0; var24 < 8; var24++) {
         this.method7578(
            var1,
            var3,
            var5,
            var7 * 1.5 + this.random.nextGaussian() * 1.2,
            0.6 + this.random.nextDouble(),
            var11 * 1.5 + this.random.nextGaussian() * 1.2,
            0.018F + this.random.nextFloat() * 0.025F
         );
      }

      this.floatValue = Math.min(1.2F, Math.max(this.floatValue, 0.4F));
   }

   public void method7578(double var1, double var3, double var5, double var7, double var9, double var11, float var13) {
      this.method7583(0, var1, var3, var5, var7, var9, var11, 0.7F + this.random.nextFloat() * 0.9F, var13);
   }

   public void method7579(double var1, double var3, double var5, LivingEntity var7, float var8, float var9) {
      if (var7 != null) {
         EmberShards.Entry3 var10 = new EmberShards.Entry3();
         var10.doubleValue = var1;
         var10.doubleValue2 = var3;
         var10.doubleValue3 = var5;
         var10.weakReference = new WeakReference<>(var7);
         var10.floatValue = var8;
         var10.longValue = ++this.longValue;
         Vec3d var11 = method7580(var7, var9, var10.longValue);
         var10.doubleValue7 = var11.x;
         var10.doubleValue8 = var11.y;
         var10.doubleValue9 = var11.z;
         double var12 = Math.sqrt(method7592(var11.x - var1) + method7592(var11.y - var3) + method7592(var11.z - var5));
         var10.floatValue3 = (float)(0.12 + var12 * 0.022);
         var10.doubleValue4 = this.random.nextGaussian() * 0.5;
         var10.doubleValue5 = 0.5 + this.random.nextDouble() * 0.7;
         var10.doubleValue6 = this.random.nextGaussian() * 0.5;
         this.items4.add(var10);
      }
   }

   private static Vec3d method7580(LivingEntity var0, float var1, long var2) {
      Vec3d var4 = var0.getLerpedPos(var1);
      double var5 = var0.getHeight() * (0.45 + 0.3 * Glowboost.method3268(var2, 0, 1));
      double var7 = var0.getWidth() * 0.3;
      return var4.add((Glowboost.method3268(var2, 0, 2) - 0.5) * var7, var5, (Glowboost.method3268(var2, 0, 3) - 0.5) * var7);
   }

   private void method7581(double var1, double var3, double var5, float var7, float var8) {
      EmberShards.Entry var9 = new EmberShards.Entry();
      var9.doubleValue = var1;
      var9.doubleValue2 = var3;
      var9.doubleValue3 = var5;
      var9.size = var7;
      var9.floatValue2 = var8;
      this.items3.add(var9);
   }

   private void method7582(double var1, double var3, double var5, double var7, double var9, double var11, float var13, float var14, float var15) {
      EmberShards.Entry4 var16 = new EmberShards.Entry4();
      var16.doubleValue = var1;
      var16.doubleValue2 = var3;
      var16.doubleValue3 = var5;
      var16.doubleValue4 = var7;
      var16.doubleValue5 = var9;
      var16.doubleValue6 = var11;
      var16.floatValue3 = var13;
      var16.floatValue2 = var14;
      var16.width = var15;
      this.items2.add(var16);
   }

   private void method7583(int var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14, float var15) {
      if (this.items.size() < 2000) {
         EmberShards.Entry2 var16 = new EmberShards.Entry2();
         var16.kind = var1;
         var16.doubleValue = var2;
         var16.doubleValue2 = var4;
         var16.doubleValue3 = var6;
         var16.doubleValue4 = var8;
         var16.doubleValue5 = var10;
         var16.doubleValue6 = var12;
         var16.floatValue2 = var14;
         var16.size = var15;
         var16.longValue = ++this.longValue;
         var16.floatValue3 = this.random.nextFloat() * (float) (Math.PI * 2);
         var16.floatValue4 = (this.random.nextFloat() - 0.5F) * 9.0F;
         this.items.add(var16);
      }
   }

   public void update(float var1, float var2) {
      this.floatValue = Math.max(0.0F, this.floatValue - var1 * 4.2F);

      for (int var3 = this.items.size() - 1; var3 >= 0; var3--) {
         EmberShards.Entry2 var4 = this.items.get(var3);
         var4.floatValue += var1;
         if (var4.floatValue >= var4.floatValue2) {
            method7591(this.items, var3);
         } else {
            if (var4.kind == 0) {
               double var5 = Math.pow(0.45, var1);
               var4.doubleValue4 = var4.doubleValue4 * var5 + MathHelper.sin(var4.floatValue * 6.3F + (float)var4.longValue) * 0.9 * var1;
               var4.doubleValue6 = var4.doubleValue6 * var5 + MathHelper.cos(var4.floatValue * 5.1F + (float)var4.longValue * 0.7F) * 0.9 * var1;
               var4.doubleValue5 = var4.doubleValue5 * var5 + 0.9 * var1;
               var4.floatValue3 = var4.floatValue3 + var4.floatValue4 * var1;
            } else {
               double var11 = Math.pow(0.03, var1);
               var4.doubleValue4 *= var11;
               var4.doubleValue5 *= var11;
               var4.doubleValue6 *= var11;
            }

            var4.doubleValue = var4.doubleValue + var4.doubleValue4 * var1;
            var4.doubleValue2 = var4.doubleValue2 + var4.doubleValue5 * var1;
            var4.doubleValue3 = var4.doubleValue3 + var4.doubleValue6 * var1;
         }
      }

      for (int var7 = this.items2.size() - 1; var7 >= 0; var7--) {
         EmberShards.Entry4 var9 = this.items2.get(var7);
         var9.floatValue += var1;
         if (var9.floatValue >= var9.floatValue2) {
            method7591(this.items2, var7);
         }
      }

      for (int var8 = this.items3.size() - 1; var8 >= 0; var8--) {
         EmberShards.Entry var10 = this.items3.get(var8);
         var10.floatValue += var1;
         if (var10.floatValue >= var10.floatValue2) {
            method7591(this.items3, var8);
         }
      }

      this.method7584(var1, var2);
   }

   private void method7584(float var1, float var2) {
      for (int var3 = this.items4.size() - 1; var3 >= 0; var3--) {
         EmberShards.Entry3 var4 = this.items4.get(var3);
         var4.floatValue2 += var1;
         LivingEntity var5 = var4.weakReference.get();
         if (!var4.flag) {
            if (var5 != null && var5.isAlive()) {
               Vec3d var6 = method7580(var5, var2, var4.longValue);
               var4.doubleValue7 = var6.x;
               var4.doubleValue8 = var6.y;
               var4.doubleValue9 = var6.z;
            }

            float var9 = (var4.floatValue2 - var4.floatValue) / var4.floatValue3;
            if (!(var9 < 1.0F)) {
               var4.flag = true;
               double[] var7 = method7586(var4, 1.0F);
               var4.doubleValue10 = var7[0];
               var4.doubleValue11 = var7[1];
               var4.doubleValue12 = var7[2];
               if (var5 != null) {
                  Vec3d var8 = var5.getLerpedPos(var2);
                  var4.doubleValue13 = var4.doubleValue7 - var8.x;
                  var4.doubleValue14 = var4.doubleValue8 - var8.y;
                  var4.doubleValue15 = var4.doubleValue9 - var8.z;
               }

               this.method7577(var4.doubleValue7, var4.doubleValue8, var4.doubleValue9, var4.doubleValue10, var4.doubleValue11, var4.doubleValue12);
               if (this.handler != null) {
                  this.handler.onShardHit(var4.doubleValue7, var4.doubleValue8, var4.doubleValue9);
               }
            }
         } else {
            var4.floatValue4 += var1;
            if (var5 != null && var5.isAlive()) {
               Vec3d var10 = var5.getLerpedPos(var2);
               var4.doubleValue7 = var10.x + var4.doubleValue13;
               var4.doubleValue8 = var10.y + var4.doubleValue14;
               var4.doubleValue9 = var10.z + var4.doubleValue15;
            }

            if (var4.floatValue4 >= 0.9F) {
               method7591(this.items4, var3);
            }
         }
      }
   }

   private static double[] method7585(EmberShards.Entry3 var0, float var1) {
      double var2 = (var0.doubleValue + var0.doubleValue7) * 0.5 + var0.doubleValue4;
      double var4 = (var0.doubleValue2 + var0.doubleValue8) * 0.5 + var0.doubleValue5;
      double var6 = (var0.doubleValue3 + var0.doubleValue9) * 0.5 + var0.doubleValue6;
      double var8 = 1.0F - var1;
      return new double[]{
         var8 * var8 * var0.doubleValue + 2.0 * var8 * var1 * var2 + var1 * var1 * var0.doubleValue7,
         var8 * var8 * var0.doubleValue2 + 2.0 * var8 * var1 * var4 + var1 * var1 * var0.doubleValue8,
         var8 * var8 * var0.doubleValue3 + 2.0 * var8 * var1 * var6 + var1 * var1 * var0.doubleValue9
      };
   }

   private static double[] method7586(EmberShards.Entry3 var0, float var1) {
      double var2 = (var0.doubleValue + var0.doubleValue7) * 0.5 + var0.doubleValue4;
      double var4 = (var0.doubleValue2 + var0.doubleValue8) * 0.5 + var0.doubleValue5;
      double var6 = (var0.doubleValue3 + var0.doubleValue9) * 0.5 + var0.doubleValue6;
      double var8 = 2.0F * (1.0F - var1) * (var2 - var0.doubleValue) + 2.0F * var1 * (var0.doubleValue7 - var2);
      double var10 = 2.0F * (1.0F - var1) * (var4 - var0.doubleValue2) + 2.0F * var1 * (var0.doubleValue8 - var4);
      double var12 = 2.0F * (1.0F - var1) * (var6 - var0.doubleValue3) + 2.0F * var1 * (var0.doubleValue9 - var6);
      double var14 = Math.sqrt(var8 * var8 + var10 * var10 + var12 * var12);
      return var14 < 1.0E-6 ? new double[]{0.0, 0.0, 1.0} : new double[]{var8 / var14, var10 / var14, var12 / var14};
   }

   public void method7587(Glowboost var1, int var2) {
      int var3 = method7590(var2, -1, 0.55F);

      for (EmberShards.Entry2 var5 : this.items) {
         float var6 = var5.floatValue / var5.floatValue2;
         if (var5.kind == 0) {
            float var7 = 0.7F + 0.3F * MathHelper.sin(var5.floatValue * 23.0F + (float)var5.longValue);
            float var8 = Glowboost.method3267(var6, 0.12F, 0.4F) * var7;
            float var9 = var5.size * (1.0F - var6 * 0.5F);
            var1.method3243(var5.doubleValue, var5.doubleValue2, var5.doubleValue3, var9, var5.floatValue3, ColorUtils.method6439(var3, var8));
            var1.method3250(var5.doubleValue, var5.doubleValue2, var5.doubleValue3, var9 * 3.6F, var2, var8 * 0.55F);
         } else {
            float var34 = 1.0F - var6;
            var1.method3236(
               var5.doubleValue,
               var5.doubleValue2,
               var5.doubleValue3,
               var5.doubleValue - var5.doubleValue4 * 0.032,
               var5.doubleValue2 - var5.doubleValue5 * 0.032,
               var5.doubleValue3 - var5.doubleValue6 * 0.032,
               ColorUtils.method6439(var3, var34)
            );
            var1.method3250(var5.doubleValue, var5.doubleValue2, var5.doubleValue3, 0.07F, var2, var34 * 0.5F);
         }
      }

      for (EmberShards.Entry4 var28 : this.items2) {
         float var31 = var28.floatValue / var28.floatValue2;
         float var35 = Glowboost.easeOut(var31);
         float var37 = var28.floatValue3 * (0.12F + 0.88F * var35);
         float var39 = var28.width * (1.0F - var31) + 0.008F;
         float var10 = (1.0F - var31) * (1.0F - var31);
         method7589(
            var1,
            var28.doubleValue,
            var28.doubleValue2,
            var28.doubleValue3,
            var28.doubleValue4,
            var28.doubleValue5,
            var28.doubleValue6,
            var37,
            var39,
            ColorUtils.method6439(var2, 0.95F * var10)
         );
         method7589(
            var1,
            var28.doubleValue,
            var28.doubleValue2,
            var28.doubleValue3,
            var28.doubleValue4,
            var28.doubleValue5,
            var28.doubleValue6,
            var37 * 0.8F,
            var39 * 0.35F,
            ColorUtils.method6439(var3, 0.7F * var10)
         );
      }

      for (EmberShards.Entry var29 : this.items3) {
         float var32 = var29.floatValue / var29.floatValue2;
         float var36 = (1.0F - var32) * (1.0F - var32);
         var1.method3250(var29.doubleValue, var29.doubleValue2, var29.doubleValue3, var29.size * (0.55F + var32 * 0.9F), var2, var36 * 1.3F);
         var1.method3250(var29.doubleValue, var29.doubleValue2, var29.doubleValue3, var29.size * 0.35F, var3, var36 * var36 * 1.4F);
      }

      var var27 = this.items4.iterator();

      while (true) {
         double var12;
         double var14;
         double var16;
         float var18;
         double var33;
         double var38;
         double var40;
         label44:
         while (true) {
            if (!var27.hasNext()) {
               return;
            }

            EmberShards.Entry3 var30 = (EmberShards.Entry3)var27.next();
            if (!var30.flag) {
               float var41 = (var30.floatValue2 - var30.floatValue) / var30.floatValue3;
               if (var41 < 0.0F) {
                  continue;
               }

               double[] var20 = method7585(var30, var41);
               double[] var21 = method7586(var30, var41);
               var33 = var20[0];
               var38 = var20[1];
               var40 = var20[2];
               var12 = var21[0];
               var14 = var21[1];
               var16 = var21[2];
               var18 = Math.min(1.0F, var41 * 6.0F + 0.2F);
               int var22 = 1;

               while (true) {
                  if (var22 > 5) {
                     break label44;
                  }

                  float var23 = var41 - var22 * 0.06F;
                  if (var23 < 0.0F) {
                     break label44;
                  }

                  double[] var24 = method7585(var30, var23);
                  var1.method3250(var24[0], var24[1], var24[2], 0.16F - var22 * 0.02F, var2, var18 * (0.5F - var22 * 0.08F));
                  var22++;
               }
            }

            var33 = var30.doubleValue7 - var30.doubleValue10 * 0.12;
            var38 = var30.doubleValue8 - var30.doubleValue11 * 0.12;
            var40 = var30.doubleValue9 - var30.doubleValue12 * 0.12;
            var12 = var30.doubleValue10;
            var14 = var30.doubleValue11;
            var16 = var30.doubleValue12;
            float var19 = 1.0F - var30.floatValue4 / 0.9F;
            var18 = var19 * var19;
            break;
         }

         method7588(var1, var33, var38, var40, var12, var14, var16, 0.5F, 0.075F, var2, var3, var18);
      }
   }

   private static void method7588(
      Glowboost var0,
      double var1,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      float var13,
      float var14,
      int var15,
      int var16,
      float var17
   ) {
      if (!(var17 < 0.02F)) {
         double var18 = -var11;
         double var20 = 0.0;
         double var22 = var7;
         double var24 = Math.sqrt(var18 * var18 + var7 * var7);
         if (var24 < 1.0E-4) {
            var18 = 1.0;
            var22 = 0.0;
            var24 = 1.0;
         }

         var18 /= var24;
         var22 /= var24;
         double var26 = var9 * var22 - var11 * var20;
         double var28 = var11 * var18 - var7 * var22;
         double var30 = var7 * var20 - var9 * var18;
         double var32 = var1 + var7 * var13 * 0.6;
         double var34 = var3 + var9 * var13 * 0.6;
         double var36 = var5 + var11 * var13 * 0.6;
         double var38 = var1 - var7 * var13 * 0.4;
         double var40 = var3 - var9 * var13 * 0.4;
         double var42 = var5 - var11 * var13 * 0.4;
         int var44 = ColorUtils.method6439(var15, 0.75F * var17);
         var0.method3238(
            var32,
            var34,
            var36,
            var1 + var18 * var14,
            var3 + var20 * var14,
            var5 + var22 * var14,
            var38,
            var40,
            var42,
            var1 - var18 * var14,
            var3 - var20 * var14,
            var5 - var22 * var14,
            var44
         );
         var0.method3238(
            var32,
            var34,
            var36,
            var1 + var26 * var14,
            var3 + var28 * var14,
            var5 + var30 * var14,
            var38,
            var40,
            var42,
            var1 - var26 * var14,
            var3 - var28 * var14,
            var5 - var30 * var14,
            var44
         );
         var0.method3236(var32, var34, var36, var38, var40, var42, ColorUtils.method6439(var16, var17));
         var0.method3250(var1, var3, var5, var13 * 0.55F, var15, var17 * 0.7F);
      }
   }

   private static void method7589(
      Glowboost var0, double var1, double var3, double var5, double var7, double var9, double var11, float var13, float var14, int var15
   ) {
      if (var15 >>> 24 != 0 && !(var13 <= 0.001F)) {
         double var16 = Math.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
         if (!(var16 < 1.0E-6)) {
            var7 /= var16;
            var9 /= var16;
            var11 /= var16;
            double var18 = -var11;
            double var20 = 0.0;
            double var22 = var7;
            double var24 = Math.sqrt(var18 * var18 + var7 * var7);
            if (var24 < 1.0E-4) {
               var18 = 1.0;
               var22 = 0.0;
               var24 = 1.0;
            }

            var18 /= var24;
            var22 /= var24;
            double var26 = var9 * var22 - var11 * var20;
            double var28 = var11 * var18 - var7 * var22;
            double var30 = var7 * var20 - var9 * var18;
            double var32 = Math.max(0.0, var13 - var14 * 0.5);
            double var34 = var13 + var14 * 0.5;
            byte var36 = 32;
            double var37 = 1.0;
            double var39 = 0.0;

            for (int var41 = 1; var41 <= var36; var41++) {
               double var42 = var41 * (Math.PI * 2) / var36;
               double var44 = Math.cos(var42);
               double var46 = Math.sin(var42);
               double var48 = var18 * var37 + var26 * var39;
               double var50 = var20 * var37 + var28 * var39;
               double var52 = var22 * var37 + var30 * var39;
               double var54 = var18 * var44 + var26 * var46;
               double var56 = var20 * var44 + var28 * var46;
               double var58 = var22 * var44 + var30 * var46;
               var0.method3238(
                  var1 + var48 * var32,
                  var3 + var50 * var32,
                  var5 + var52 * var32,
                  var1 + var54 * var32,
                  var3 + var56 * var32,
                  var5 + var58 * var32,
                  var1 + var54 * var34,
                  var3 + var56 * var34,
                  var5 + var58 * var34,
                  var1 + var48 * var34,
                  var3 + var50 * var34,
                  var5 + var52 * var34,
                  var15
               );
               var37 = var44;
               var39 = var46;
            }
         }
      }
   }

   public static int method7590(int var0, int var1, float var2) {
      float var3 = MathHelper.clamp(var2, 0.0F, 1.0F);
      int var4 = var0 >>> 24 & 0xFF;
      int var5 = var0 >> 16 & 0xFF;
      int var6 = var0 >> 8 & 0xFF;
      int var7 = var0 & 0xFF;
      int var8 = var1 >>> 24 & 0xFF;
      int var9 = var1 >> 16 & 0xFF;
      int var10 = var1 >> 8 & 0xFF;
      int var11 = var1 & 0xFF;
      return Math.round(var4 + (var8 - var4) * var3) << 24
         | Math.round(var5 + (var9 - var5) * var3) << 16
         | Math.round(var6 + (var10 - var6) * var3) << 8
         | Math.round(var7 + (var11 - var7) * var3);
   }

   private static <T> void method7591(List<T> var0, int var1) {
      int var2 = var0.size() - 1;
      if (var1 != var2) {
         var0.set(var1, var0.get(var2));
      }

      var0.remove(var2);
   }

   private static double method7592(double var0) {
      return var0 * var0;
   }

   private static final class Entry {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public float floatValue;
      public float floatValue2;
      public float size;
   }

   private static final class Entry2 {
      public int kind;
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public float floatValue;
      public float floatValue2;
      public float size;
      public float floatValue3;
      public float floatValue4;
      public long longValue;
   }

   private static final class Entry3 {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public WeakReference<LivingEntity> weakReference;
      public double doubleValue7;
      public double doubleValue8;
      public double doubleValue9;
      public float floatValue;
      public float floatValue2;
      public float floatValue3;
      public boolean flag;
      public float floatValue4;
      public double doubleValue10;
      public double doubleValue11;
      public double doubleValue12;
      public double doubleValue13;
      public double doubleValue14;
      public double doubleValue15;
      public long longValue;
   }

   private static final class Entry4 {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public float floatValue;
      public float floatValue2;
      public float floatValue3;
      public float width;
   }

   public interface Handler {
      void onShardHit(double var1, double var3, double var5);
   }
}

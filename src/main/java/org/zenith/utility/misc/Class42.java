package org.zenith.utility.misc;

import net.minecraft.util.math.MathHelper;
import org.zenith.utility.game.Class28;
import org.zenith.utility.render.Glowboost;

public final class Class42 {
   public static final Class42.Handler handler = (var0, var2, var4) -> false;
   private static Class42.Handler handler2 = handler;
   private static final float CONST_floatValue = 0.008333334F;
   private static final int CONST_intValue = 900;
   private static final float CONST_floatValue2 = (float) (Math.PI * 2);
   public final int intValue;
   public final int intValue2;
   public final float floatValue;
   private final double doubleValue;
   private final double doubleValue2;
   private final double doubleValue3;
   private final float floatValue2;
   private final float floatValue3;
   private final float floatValue4;
   private final float floatValue5;
   private final float floatValue6;
   private final float floatValue7;
   private final float[] floatValues;
   private final float[] floatValues2;
   private final float[] floatValues3;
   private final float[] floatValues4;
   private final float[] floatValues5;
   private final float[] floatValues6;
   private final float[] floatValues7;
   private final float[] floatValues8;
   private final float[] floatValues9;
   private final float[] floatValues10;
   private final float[] floatValues11;
   private final float[] floatValues12;
   private final float[] floatValues13;
   private final float[] floatValues14;
   private final boolean[] flags;
   private float time;

   public static void method7379(Class42.Handler var0) {
      handler2 = var0 == null ? handler : var0;
   }

   public static Class42 method7380(int var0, Class28 var1, int var2, float var3) {
      return new Class42(var0, var1, var2, var3);
   }

   public boolean method7381(int var1, int var2, float var3) {
      return this.intValue == var1 && this.intValue2 == var2 && Math.abs(this.floatValue - var3) < 1.0E-4F;
   }

   private Class42(int var1, Class28 var2, int var3, float var4) {
      this.intValue = var1;
      this.intValue2 = Math.max(1, var3);
      this.floatValue = var4;
      this.doubleValue = var2.doubleValue;
      this.doubleValue2 = var2.doubleValue2;
      this.doubleValue3 = var2.doubleValue3;
      boolean var5 = var1 == 2;
      this.floatValue2 = var5 ? 4.2F : 3.4F;
      this.floatValue3 = var5 ? 4.4F : 3.2F;
      this.floatValue4 = var5 ? 0.42F : 0.28F;
      this.floatValue5 = var5 ? 0.74F : 0.55F;
      this.floatValue6 = var5 ? 0.35F : 0.45F;
      this.floatValue7 = var5 ? 14.0F : 10.0F;
      int var6 = this.intValue2;
      this.floatValues = new float[var6];
      this.floatValues2 = new float[var6];
      this.floatValues3 = new float[var6];
      this.floatValues4 = new float[var6];
      this.floatValues5 = new float[var6];
      this.floatValues6 = new float[var6];
      this.floatValues8 = new float[var6];
      this.floatValues9 = new float[var6];
      this.floatValues10 = new float[var6];
      this.floatValues11 = new float[var6];
      this.floatValues12 = new float[var6];
      this.floatValues13 = new float[var6];
      this.floatValues7 = new float[var6];
      this.floatValues14 = new float[var6];
      this.flags = new boolean[var6];
      long var7 = var2.longValue2;

      for (int var9 = 0; var9 < var6; var9++) {
         float var10 = (var9 + Glowboost.method3268(var7, var9, 3) * 0.8F) / var6 * (float) (Math.PI * 2);
         double var11 = MathHelper.cos(var10);
         double var13 = MathHelper.sin(var10);
         double var15 = -var13;
         float var19 = Glowboost.method3268(var7, var9, 0) * (float) (Math.PI * 2);
         float var20 = var5 ? 0.15F + Glowboost.method3268(var7, var9, 1) * 1.55F : 0.22F + Glowboost.method3268(var7, var9, 1) * 1.15F;
         float var21 = MathHelper.sin(var20);
         float var22 = MathHelper.cos(var20);
         float var23 = MathHelper.cos(var19);
         float var24 = MathHelper.sin(var19);
         double var25 = var11 * var22 + var15 * var23 * var21;
         double var27 = var24 * var21 + (var5 ? 0.35 : 0.0);
         double var29 = var13 * var22 + var11 * var23 * var21;
         float var31 = var5 ? (2.0F + Glowboost.method3268(var7, var9, 2) * 4.5F) * var4 : (1.4F + Glowboost.method3268(var7, var9, 2) * 2.6F) * var4;
         this.floatValues4[var9] = (float)(var25 * var31);
         this.floatValues5[var9] = (float)(var27 * var31);
         this.floatValues6[var9] = (float)(var29 * var31);
         double var32 = Math.sqrt(var25 * var25 + var27 * var27 + var29 * var29);
         if (var32 < 1.0E-6) {
            var25 = 0.0;
            var27 = 1.0;
            var29 = 0.0;
            var32 = 1.0;
         }

         this.floatValues8[var9] = (float)(var25 / var32);
         this.floatValues9[var9] = (float)(var27 / var32);
         this.floatValues10[var9] = (float)(var29 / var32);
         this.floatValues11[var9] = this.floatValues8[var9];
         this.floatValues12[var9] = this.floatValues9[var9];
         this.floatValues13[var9] = this.floatValues10[var9];
      }
   }

   public void method7382(float var1) {
      if (!(var1 <= this.time)) {
         int var2;
         for (var2 = 0; this.time < var1 && var2 < 900; var2++) {
            this.method7383();
            this.time += 0.008333334F;
         }

         if (var2 >= 900) {
            this.time = var1;
         }
      }
   }

   private void method7383() {
      float var1 = Math.max(0.0F, 1.0F - this.floatValue2 * 0.008333334F);

      for (int var2 = 0; var2 < this.intValue2; var2++) {
         if (this.floatValues14[var2] > 0.0F) {
            this.floatValues14[var2] = Math.max(0.0F, this.floatValues14[var2] - 0.045833334F);
         }

         if (!this.flags[var2]) {
            this.floatValues4[var2] = this.floatValues4[var2] * var1;
            this.floatValues5[var2] = this.floatValues5[var2] * var1;
            this.floatValues6[var2] = this.floatValues6[var2] * var1;
            this.floatValues7[var2] = Math.max(-this.floatValue7, this.floatValues7[var2] - this.floatValue3 * 0.008333334F);
            float var3 = this.floatValues4[var2];
            float var4 = this.floatValues5[var2] + this.floatValues7[var2];
            float var5 = this.floatValues6[var2];
            float var6 = this.floatValues[var2];
            float var7 = this.floatValues2[var2];
            float var8 = this.floatValues3[var2];
            float var9 = var6 + var3 * 0.008333334F;
            float var10 = var7 + var4 * 0.008333334F;
            float var11 = var8 + var5 * 0.008333334F;
            boolean var12 = false;
            boolean var13 = false;
            if (this.method7384(var9, var7, var8)) {
               var3 = -var3 * this.floatValue4;
               var9 = var6;
               var12 = true;
            }

            if (this.method7384(var9, var10, var8)) {
               if (var4 < 0.0F) {
                  var13 = true;
               }

               var4 = -var4 * this.floatValue4;
               var10 = var7;
               var3 *= this.floatValue5;
               var5 *= this.floatValue5;
               var12 = true;
            }

            if (this.method7384(var9, var10, var11)) {
               var5 = -var5 * this.floatValue4;
               var11 = var8;
               var12 = true;
            }

            this.floatValues[var2] = var9;
            this.floatValues2[var2] = var10;
            this.floatValues3[var2] = var11;
            float var14 = var3 * var3 + var4 * var4 + var5 * var5;
            if (var14 > 1.0E-8F) {
               float var15 = 1.0F / MathHelper.sqrt(var14);
               this.floatValues8[var2] = var3 * var15;
               this.floatValues9[var2] = var4 * var15;
               this.floatValues10[var2] = var5 * var15;
            }

            if (var12) {
               this.floatValues4[var2] = var3;
               this.floatValues5[var2] = var4;
               this.floatValues6[var2] = var5;
               this.floatValues7[var2] = 0.0F;
               this.floatValues14[var2] = 1.0F;
               if (var13 && var14 < this.floatValue6 * this.floatValue6) {
                  this.flags[var2] = true;
                  this.floatValues4[var2] = 0.0F;
                  this.floatValues5[var2] = 0.0F;
                  this.floatValues6[var2] = 0.0F;
                  this.floatValues7[var2] = 0.0F;
               }
            }
         }
      }
   }

   private boolean method7384(float var1, float var2, float var3) {
      return handler2.solid(this.doubleValue + var1, this.doubleValue2 + var2, this.doubleValue3 + var3);
   }

   public double method7385(int var1) {
      return this.doubleValue + this.floatValues[var1];
   }

   public double method7386(int var1) {
      return this.doubleValue2 + this.floatValues2[var1];
   }

   public double method7387(int var1) {
      return this.doubleValue3 + this.floatValues3[var1];
   }

   public float method7388(int var1) {
      return this.flags[var1] ? 0.0F : this.floatValues4[var1];
   }

   public float method7389(int var1) {
      return this.flags[var1] ? 0.0F : this.floatValues5[var1] + this.floatValues7[var1];
   }

   public float method7390(int var1) {
      return this.flags[var1] ? 0.0F : this.floatValues6[var1];
   }

   public float method7391(int var1) {
      float var2 = this.method7388(var1);
      float var3 = this.method7389(var1);
      float var4 = this.method7390(var1);
      return MathHelper.sqrt(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public float method7392(int var1) {
      return this.floatValues11[var1];
   }

   public float method7393(int var1) {
      return this.floatValues12[var1];
   }

   public float method7394(int var1) {
      return this.floatValues13[var1];
   }

   public float method7395(int var1) {
      return this.floatValues8[var1];
   }

   public float method7396(int var1) {
      return this.floatValues9[var1];
   }

   public float method7397(int var1) {
      return this.floatValues10[var1];
   }

   public float method7398(int var1) {
      return this.floatValues14[var1];
   }

   public boolean method7399(int var1) {
      return this.flags[var1];
   }

   public interface Handler {
      boolean solid(double var1, double var3, double var5);
   }
}

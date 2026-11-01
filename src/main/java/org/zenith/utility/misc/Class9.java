package org.zenith.utility.misc;

public final class Class9 {
   private static final float CONST_floatValue = -100.0F;
   private final float floatValue;
   private final float floatValue2;
   private final float floatValue3;
   private final float floatValue4;
   private final float floatValue5;
   private float floatValue6;
   private float floatValue7;
   private float floatValue8;
   private float floatValue9;
   private float floatValue10 = -60.0F;
   private float floatValue11 = 1.0F;
   private final float[] floatValues = new float[960];

   public Class9() {
      double var1 = 0.010471975511965976;
      double var3 = Math.sin(var1) / 1.4142;
      double var5 = Math.cos(var1);
      double var7 = 1.0 + var3;
      this.floatValue = (float)((1.0 + var5) / 2.0 / var7);
      this.floatValue2 = (float)(-(1.0 + var5) / var7);
      this.floatValue3 = (float)((1.0 + var5) / 2.0 / var7);
      this.floatValue4 = (float)(-2.0 * var5 / var7);
      this.floatValue5 = (float)((1.0 - var3) / var7);
   }

   public void reset() {
      this.floatValue6 = this.floatValue7 = this.floatValue8 = this.floatValue9 = 0.0F;
      this.floatValue10 = -60.0F;
      this.floatValue11 = 1.0F;
   }

   public float method1530(short[] var1, float var2, boolean var3) {
      double var4 = 0.0;

      for (int var6 = 0; var6 < var1.length; var6++) {
         float var7 = var1[var6] / 32768.0F;
         float var8 = this.floatValue * var7
            + this.floatValue2 * this.floatValue6
            + this.floatValue3 * this.floatValue7
            - this.floatValue4 * this.floatValue8
            - this.floatValue5 * this.floatValue9;
         this.floatValue7 = this.floatValue6;
         this.floatValue6 = var7;
         this.floatValue9 = this.floatValue8;
         this.floatValue8 = Math.abs(var8) < 1.0E-20F ? 0.0F : var8;
         float var9 = method1533(var8 * var2);
         this.floatValues[var6] = var9;
         var4 += var9 * var9;
      }

      float var11 = method1532(Math.sqrt(var4 / var1.length));
      float var12 = 1.0F;
      if (var3) {
         this.floatValue10 = var11 < this.floatValue10 ? var11 : Math.min(-20.0F, this.floatValue10 + 0.05F);
         float var13 = var11 - this.floatValue10;
         float var15 = Class9.Entry.clamp((var13 - 6.0F) / 6.0F, 0.0F, 1.0F);
         var12 = 0.18F + 0.82F * var15;
      }

      float var14 = var12 > this.floatValue11 ? var12 : this.floatValue11 + (var12 - this.floatValue11) * 0.13F;
      float var16 = (var14 - this.floatValue11) / var1.length;

      for (int var10 = 0; var10 < var1.length; var10++) {
         this.floatValue11 += var16;
         var1[var10] = (short)Math.round(this.floatValues[var10] * this.floatValue11 * 32767.0F);
      }

      this.floatValue11 = var14;
      return var11;
   }

   public static float method1531(short[] var0, int var1) {
      double var2 = 0.0;

      for (int var4 = 0; var4 < var1; var4++) {
         float var5 = var0[var4] / 32768.0F;
         var2 += var5 * var5;
      }

      return method1532(Math.sqrt(var2 / Math.max(1, var1)));
   }

   public static float method1532(double var0) {
      return var0 <= 1.0E-5 ? -100.0F : (float)Math.max(-100.0, 20.0 * Math.log10(var0));
   }

   private static float method1533(float var0) {
      float var1 = Math.abs(var0);
      if (var1 <= 0.6F) {
         return var0;
      } else {
         float var2 = (var1 - 0.6F) / 0.4F;
         float var3 = 0.6F + 0.4F * (float)Math.tanh(var2);
         return Math.copySign(Math.min(var3, 1.0F), var0);
      }
   }

   private static final class Entry {
      public static float clamp(float var0, float var1, float var2) {
         return var0 < var1 ? var1 : Math.min(var0, var2);
      }
   }
}

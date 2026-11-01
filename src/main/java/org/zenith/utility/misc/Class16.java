package org.zenith.utility.misc;

import java.time.Instant;
import java.time.ZoneId;

public final class Class16 {
   private static final float CONST_floatValue = (float) (Math.PI / 180.0);
   private static final long CONST_longValue = System.nanoTime();
   private static int intValue;
   private static long longValue;
   private static boolean flag;
   private final float[] floatValues = new float[9];
   private float[] floatValues2 = new float[0];
   private float[] floatValues3 = new float[0];
   public float[] values = new float[0];

   public float[] method2139(Class15 var1, AnimationState var2, float var3, Class2 var4) {
      int var5 = var1.method2030();
      if (this.values.length != var5 * 9) {
         this.values = new float[var5 * 9];
         this.floatValues2 = new float[var5 * 2];
         this.floatValues3 = new float[var5 * 2];
      }

      if (var2 != null) {
         var2.method6542(var1, var3);
      }

      for (int var6 = 0; var6 < var5; var6++) {
         if (var2 != null) {
            var2.method6546(var1, var6, var3, this.floatValues);
         } else {
            method2146(this.floatValues);
         }

         int var7 = var6 * 9;
         this.values[var7] = var1.floatValues2[var6 * 3] + this.floatValues[0] * (float) (Math.PI / 180.0);
         this.values[var7 + 1] = var1.floatValues2[var6 * 3 + 1] + this.floatValues[1] * (float) (Math.PI / 180.0);
         this.values[var7 + 2] = var1.floatValues2[var6 * 3 + 2] + this.floatValues[2] * (float) (Math.PI / 180.0);
         this.values[var7 + 3] = this.floatValues[3];
         this.values[var7 + 4] = this.floatValues[4];
         this.values[var7 + 5] = this.floatValues[5];
         this.values[var7 + 6] = this.floatValues[6];
         this.values[var7 + 7] = this.floatValues[7];
         this.values[var7 + 8] = this.floatValues[8];
         if (var1.type2s[var6] == Class15.Type2.CLOCK) {
            this.method2143(var1, var6);
         } else if (var4 != null) {
            this.method2142(var1, var6, var4, var3);
         }
      }

      return this.values;
   }

   public void method2140(Class15 var1) {
      int var2 = Math.min(var1.method2030(), this.values.length / 9);

      for (int var3 = 0; var3 < var2; var3++) {
         if (var1.intValues[var3] < 0 && var1.intValues2[var3] >= 0) {
            int var4 = var3 * 9;
            this.values[var4] = var1.floatValues2[var3 * 3];
            this.values[var4 + 1] = var1.floatValues2[var3 * 3 + 1];
            this.values[var4 + 2] = var1.floatValues2[var3 * 3 + 2];
            this.values[var4 + 3] = 0.0F;
            this.values[var4 + 4] = 0.0F;
            this.values[var4 + 5] = 0.0F;
            this.values[var4 + 6] = 1.0F;
            this.values[var4 + 7] = 1.0F;
            this.values[var4 + 8] = 1.0F;
         }
      }
   }

   public void method2141(Class15 var1, AnimationState var2, String var3, float var4, float var5) {
      if (var1.method2032(var3) != null && !(var5 <= 0.0F)) {
         for (int var6 = 0; var6 < var1.method2030(); var6++) {
            if (var1.method2032(var3).method6241(var6)) {
               var2.method6548(var1, var3, var6, var4, this.floatValues);
               int var7 = var6 * 9;
               this.values[var7] = this.values[var7] + this.floatValues[0] * (float) (Math.PI / 180.0) * var5;
               this.values[var7 + 1] = this.values[var7 + 1] + this.floatValues[1] * (float) (Math.PI / 180.0) * var5;
               this.values[var7 + 2] = this.values[var7 + 2] + this.floatValues[2] * (float) (Math.PI / 180.0) * var5;
               this.values[var7 + 3] = this.values[var7 + 3] + this.floatValues[3] * var5;
               this.values[var7 + 4] = this.values[var7 + 4] + this.floatValues[4] * var5;
               this.values[var7 + 5] = this.values[var7 + 5] + this.floatValues[5] * var5;
               this.values[var7 + 6] = this.values[var7 + 6] * (1.0F + (this.floatValues[6] - 1.0F) * var5);
               this.values[var7 + 7] = this.values[var7 + 7] * (1.0F + (this.floatValues[7] - 1.0F) * var5);
               this.values[var7 + 8] = this.values[var7 + 8] * (1.0F + (this.floatValues[8] - 1.0F) * var5);
            }
         }
      }
   }

   private void method2142(Class15 var1, int var2, Class2 var3, float var4) {
      Class15.Type2 var5 = var1.type2s[var2];
      if (var5 != Class15.Type2.NONE) {
         float var6 = var1.floatValues3[var2 * 4];
         float var7 = var1.floatValues3[var2 * 4 + 1];
         float var8 = var1.floatValues3[var2 * 4 + 2];
         float var9 = var1.floatValues3[var2 * 4 + 3];
         int var10 = var2 * 9;
         switch (var5) {
            case CAPE:
               float var18 = var6 == 0.0F ? 1.0F : var6;
               this.values[var10] = this.values[var10] + (6.0F + var3.floatValue2 / 2.0F + var3.floatValue) * (float) (Math.PI / 180.0) * var18;
               this.values[var10 + 2] = this.values[var10 + 2] + var3.floatValue3 / 2.0F * (float) (Math.PI / 180.0) * var18;
               this.values[var10 + 1] = this.values[var10 + 1] - var3.floatValue3 / 2.0F * (float) (Math.PI / 180.0) * var18 * 0.5F;
               break;
            case LEGS_FRONT:
               this.values[var10] = this.values[var10] + Math.min(0.0F, Math.min(var3.floatValue4, var3.floatValue5)) * (var6 == 0.0F ? 1.0F : var6);
               break;
            case LEGS_BACK:
               this.values[var10] = this.values[var10] + Math.max(0.0F, Math.max(var3.floatValue4, var3.floatValue5)) * (var6 == 0.0F ? 1.0F : var6);
               break;
            case SPRING:
               this.method2144(var2, var3, var6 == 0.0F ? 70.0F : var6, var7 == 0.0F ? 9.0F : var7, var8 == 0.0F ? 1.0F : var8, var9 == 0.0F ? 35.0F : var9);
               break;
            case SWAY:
               float var17 = var6 == 0.0F ? 6.0F : var6;
               float var19 = var7 == 0.0F ? 0.5F : var7;
               int var20 = Math.max(0, Math.min(2, (int)var8));
               this.values[var10 + var20] = this.values[var10 + var20]
                  + (float)Math.sin((var4 * var19 + var9) * Math.PI * 2.0) * var17 * (float) (Math.PI / 180.0);
               break;
            case CLOTH:
               float var11 = var6 == 0.0F ? 0.35F : var6;
               float var12 = var7 == 0.0F ? 5.0F : var7;
               float var13 = var8 == 0.0F ? 0.9F : var8;
               float var14 = Math.min(1.5F, var3.floatValue6 + (var3.flag2 ? 0.6F : 0.0F));
               float var15 = (float)Math.sin((var4 * var13 * (1.0F + var14 * 1.6F) - var9) * Math.PI * 2.0);
               float var16 = (float)Math.sin((var4 * var13 * 2.3F * (1.0F + var14) - var9 * 1.7F) * Math.PI * 2.0);
               this.values[var10] = this.values[var10]
                  + ((6.0F + var3.floatValue2 / 2.0F + var3.floatValue) * var11 + var15 * var12 * (0.35F + var14)) * (float) (Math.PI / 180.0);
               this.values[var10 + 2] = this.values[var10 + 2]
                  + (var3.floatValue3 / 2.0F * var11 + var16 * var12 * 0.25F * (0.2F + var14)) * (float) (Math.PI / 180.0);
         }
      }
   }

   private void method2143(Class15 var1, int var2) {
      int var3 = (int)var1.floatValues3[var2 * 4];
      if (var3 >= 3) {
         double var15 = (System.nanoTime() - CONST_longValue) / 1.0E9;
         float var6 = var1.floatValues3[var2 * 4 + 1];
         float var16 = var1.floatValues3[var2 * 4 + 2];
         double var8 = var3 == 3 ? var15 * var6 + var16 : Math.sin((var15 * var1.floatValues3[var2 * 4 + 3] + var16) * Math.PI * 2.0) * var6;
         this.values[var2 * 9] = (float)Math.toRadians(var8 % 360.0);
      } else {
         Instant var4 = Instant.now();
         long var5 = System.nanoTime();
         if (!flag || var5 - longValue > 1000000000L) {
            intValue = ZoneId.systemDefault().getRules().getOffset(var4).getTotalSeconds();
            longValue = var5;
            flag = true;
         }

         long var7 = Math.floorMod(var4.getEpochSecond() + intValue, 86400L);
         int var9 = (int)(var7 / 3600L);
         int var10 = (int)(var7 / 60L % 60L);
         double var11 = var7 % 60L + var4.getNano() / 1.0E9;

         double var13 = switch (var3) {
            case 0 -> var11 * 6.0;
            case 1 -> (var10 + var11 / 60.0) * 6.0;
            default -> (var9 % 12 + var10 / 60.0 + var11 / 3600.0) * 30.0;
         };
         this.values[var2 * 9] = (float)Math.toRadians(var13);
      }
   }

   private void method2144(int var1, Class2 var2, float var3, float var4, float var5, float var6) {
      float var7 = Math.min(Math.max(var2.floatValue8, 0.0F), 0.05F);
      float var8 = method2145(var2.floatValue2 * 0.35F * var5 + (var2.flag2 ? -8.0F * var5 : 0.0F), var6);
      float var9 = method2145(-var2.floatValue7 * 0.04F * var5, var6);
      int var10 = var1 * 2;
      if (var7 > 0.0F) {
         for (int var11 = 0; var11 < 2; var11++) {
            float var12 = var11 == 0 ? var8 : var9;
            float var13 = var3 * (var12 - this.floatValues2[var10 + var11]) - var4 * this.floatValues3[var10 + var11];
            this.floatValues3[var10 + var11] = this.floatValues3[var10 + var11] + var13 * var7;
            this.floatValues2[var10 + var11] = this.floatValues2[var10 + var11] + this.floatValues3[var10 + var11] * var7;
            this.floatValues2[var10 + var11] = method2145(this.floatValues2[var10 + var11], var6 * 1.4F);
         }
      }

      int var14 = var1 * 9;
      this.values[var14] = this.values[var14] + this.floatValues2[var10] * (float) (Math.PI / 180.0);
      this.values[var14 + 2] = this.values[var14 + 2] + this.floatValues2[var10 + 1] * (float) (Math.PI / 180.0);
   }

   private static float method2145(float var0, float var1) {
      return Math.max(-var1, Math.min(var1, var0));
   }

   private static void method2146(float[] var0) {
      var0[0] = var0[1] = var0[2] = 0.0F;
      var0[3] = var0[4] = var0[5] = 0.0F;
      var0[6] = var0[7] = var0[8] = 1.0F;
   }
}

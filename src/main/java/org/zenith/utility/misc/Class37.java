package org.zenith.utility.misc;

import java.util.Map;

public final class Class37 {
   public static final int CONST_intValue = 0;
   public static final int CONST_intValue2 = 1;
   public static final int CONST_intValue3 = 2;
   private final String text;
   private final float floatValue;
   private final boolean flag;
   private final Class37.Entry[][] entrys;

   public Class37(String var1, float var2, boolean var3, Map<Integer, Class37.Entry[]> var4, int var5) {
      this.text = var1;
      this.floatValue = var2;
      this.flag = var3;
      this.entrys = new Class37.Entry[var5][];

      for (Map.Entry var7 : var4.entrySet()) {
         if ((Integer)var7.getKey() >= 0 && (Integer)var7.getKey() < var5) {
            this.entrys[(Integer)var7.getKey()] = (Class37.Entry[])var7.getValue();
         }
      }
   }

   public String name() {
      return this.text;
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public float method6240(float var1) {
      if (this.floatValue <= 0.0F) {
         return 0.0F;
      } else if (!this.flag) {
         return Math.min(Math.max(var1, 0.0F), this.floatValue);
      } else {
         float var2 = var1 % this.floatValue;
         return var2 < 0.0F ? var2 + this.floatValue : var2;
      }
   }

   public boolean method6241(int var1) {
      return var1 >= 0 && var1 < this.entrys.length && this.entrys[var1] != null;
   }

   public void method6242(int var1, float var2, float[] var3) {
      var3[0] = var3[1] = var3[2] = 0.0F;
      var3[3] = var3[4] = var3[5] = 0.0F;
      var3[6] = var3[7] = var3[8] = 1.0F;
      if (this.method6241(var1)) {
         Class37.Entry[] var4 = this.entrys[var1];
         if (var4[0] != null) {
            var4[0].method6243(var2, var3, 0, this.flag);
         }

         if (var4[1] != null) {
            var4[1].method6243(var2, var3, 3, this.flag);
         }

         if (var4[2] != null) {
            var4[2].method6243(var2, var3, 6, this.flag);
         }
      }
   }

   public static final class Entry {
      public final float[] floatValues;
      public final float[] floatValues2;
      public final boolean flag;

      public Entry(float[] var1, float[] var2, boolean var3) {
         this.floatValues = var1;
         this.floatValues2 = var2;
         this.flag = var3;
      }

      public void method6243(float var1, float[] var2, int var3, boolean var4) {
         int var5 = this.floatValues.length - 1;
         if (var5 >= 0) {
            if (var1 <= this.floatValues[0] || var5 == 0) {
               this.method6244(0, var2, var3);
            } else if (var1 >= this.floatValues[var5]) {
               this.method6244(var5, var2, var3);
            } else {
               int var6 = 0;

               while (var6 < var5 && this.floatValues[var6 + 1] < var1) {
                  var6++;
               }

               float var7 = this.floatValues[var6];
               float var8 = this.floatValues[var6 + 1];
               float var9 = var8 - var7 <= 1.0E-6F ? 0.0F : (var1 - var7) / (var8 - var7);
               if (this.flag) {
                  int var13 = var6 - 1 >= 0 ? var6 - 1 : (var4 && var5 >= 2 ? var5 - 1 : 0);
                  int var14 = var6 + 2 <= var5 ? var6 + 2 : (var4 && var5 >= 2 ? Math.min(var6 + 2 - var5, var5) : var5);

                  for (int var12 = 0; var12 < 3; var12++) {
                     var2[var3 + var12] = method6245(
                        this.floatValues2[var13 * 3 + var12],
                        this.floatValues2[var6 * 3 + var12],
                        this.floatValues2[(var6 + 1) * 3 + var12],
                        this.floatValues2[var14 * 3 + var12],
                        var9
                     );
                  }
               } else {
                  for (int var10 = 0; var10 < 3; var10++) {
                     float var11 = this.floatValues2[var6 * 3 + var10];
                     var2[var3 + var10] = var11 + (this.floatValues2[(var6 + 1) * 3 + var10] - var11) * var9;
                  }
               }
            }
         }
      }

      private void method6244(int var1, float[] var2, int var3) {
         var2[var3] = this.floatValues2[var1 * 3];
         var2[var3 + 1] = this.floatValues2[var1 * 3 + 1];
         var2[var3 + 2] = this.floatValues2[var1 * 3 + 2];
      }

      private static float method6245(float var0, float var1, float var2, float var3, float var4) {
         float var5 = var4 * var4;
         float var6 = var5 * var4;
         return 0.5F
            * (2.0F * var1 + (var2 - var0) * var4 + (2.0F * var0 - 5.0F * var1 + 4.0F * var2 - var3) * var5 + (3.0F * var1 - var0 - 3.0F * var2 + var3) * var6);
      }
   }
}

package org.zenith.utility.misc;

import java.awt.Color;

public final class Util2 {
   public static final int CONST_intValue = -1;
   public static final int CONST_intValue2 = -16777216;
   public static final int CONST_intValue3 = -16087809;
   public static final int CONST_intValue4 = -266593250;
   public static final int CONST_intValue5 = -1;
   public static final int CONST_intValue6 = -1712591883;

   private Util2() {
   }

   public static int method809(int var0, int var1, int var2, int var3) {
      return (var3 & 0xFF) << 24 | (var0 & 0xFF) << 16 | (var1 & 0xFF) << 8 | var2 & 0xFF;
   }

   public static int method810(int var0) {
      return var0 >> 24 & 0xFF;
   }

   public static int method811(int var0, int var1) {
      return Math.clamp((long)var1, 0, 255) << 24 | var0 & 16777215;
   }

   public static int method812(int var0, float var1) {
      return method811(var0, Math.round(method810(var0) * Math.clamp(var1, 0.0F, 1.0F)));
   }

   public static int method813(int var0, int var1, float var2) {
      float var3 = Math.clamp(var2, 0.0F, 1.0F);
      int var4 = Math.round(method810(var0) + (method810(var1) - method810(var0)) * var3);
      int var5 = Math.round((var0 >> 16 & 0xFF) + ((var1 >> 16 & 0xFF) - (var0 >> 16 & 0xFF)) * var3);
      int var6 = Math.round((var0 >> 8 & 0xFF) + ((var1 >> 8 & 0xFF) - (var0 >> 8 & 0xFF)) * var3);
      int var7 = Math.round((var0 & 0xFF) + ((var1 & 0xFF) - (var0 & 0xFF)) * var3);
      return method809(var5, var6, var7, var4);
   }

   public static int method814(int var0, float var1, float var2) {
      return method815(System.currentTimeMillis(), var0, var1, var2);
   }

   public static int method815(long var0, int var2, float var3, float var4) {
      float var5 = (float)((var0 + var2) % 4000L) / 4000.0F;
      return 0xFF000000 | Color.HSBtoRGB(var5, var3, var4) & 16777215;
   }
}

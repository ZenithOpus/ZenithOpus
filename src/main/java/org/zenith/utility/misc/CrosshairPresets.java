package org.zenith.utility.misc;

public final class CrosshairPresets {
   public static final String[] texts = new String[]{
      "crosshair.preset.cross",
      "crosshair.preset.dot",
      "crosshair.preset.crossDot",
      "crosshair.preset.tshape",
      "crosshair.preset.circle",
      "crosshair.preset.diamond",
      "crosshair.preset.brackets",
      "crosshair.preset.chevron"
   };
   private static final int CONST_intValue = -1;
   private static final int CONST_intValue2 = 15;

   private CrosshairPresets() {
   }

   public static int[] method5712(int var0) {
      int[] var1 = new int[961];
      switch (var0) {
         case 1:
            method5715(var1);
            break;
         case 2:
            method5714(var1, 3, 5);
            method5715(var1);
            break;
         case 3:
            method5716(var1);
            break;
         case 4:
            method5717(var1, 6);
            break;
         case 5:
            method5718(var1, 6);
            break;
         case 6:
            method5719(var1, 7, 3);
            break;
         case 7:
            method5720(var1);
            break;
         default:
            method5714(var1, 3, 5);
      }

      return var1;
   }

   private static void method5713(int[] var0, int var1, int var2) {
      if (var1 >= 0 && var2 >= 0 && var1 < 31 && var2 < 31) {
         var0[var2 * 31 + var1] = -1;
      }
   }

   private static void method5714(int[] var0, int var1, int var2) {
      for (int var3 = var1; var3 < var1 + var2; var3++) {
         method5713(var0, 15, 15 - var3);
         method5713(var0, 15, 15 + var3);
         method5713(var0, 15 - var3, 15);
         method5713(var0, 15 + var3, 15);
      }
   }

   private static void method5715(int[] var0) {
      method5713(var0, 15, 15);
   }

   private static void method5716(int[] var0) {
      for (int var1 = 3; var1 < 8; var1++) {
         method5713(var0, 15 - var1, 15);
         method5713(var0, 15 + var1, 15);
         method5713(var0, 15, 15 + var1);
      }
   }

   private static void method5717(int[] var0, int var1) {
      int var2 = var1;
      int var3 = 0;
      int var4 = 1 - var1;

      while (var2 >= var3) {
         method5713(var0, 15 + var2, 15 + var3);
         method5713(var0, 15 + var3, 15 + var2);
         method5713(var0, 15 - var3, 15 + var2);
         method5713(var0, 15 - var2, 15 + var3);
         method5713(var0, 15 - var2, 15 - var3);
         method5713(var0, 15 - var3, 15 - var2);
         method5713(var0, 15 + var3, 15 - var2);
         method5713(var0, 15 + var2, 15 - var3);
         var3++;
         if (var4 < 0) {
            var4 += 2 * var3 + 1;
         } else {
            var2--;
            var4 += 2 * (var3 - var2) + 1;
         }
      }
   }

   private static void method5718(int[] var0, int var1) {
      for (int var2 = 0; var2 < var1; var2++) {
         int var3 = var1 - var2;
         method5713(var0, 15 + var2, 15 + var3);
         method5713(var0, 15 + var2, 15 - var3);
         method5713(var0, 15 - var2, 15 + var3);
         method5713(var0, 15 - var2, 15 - var3);
      }
   }

   private static void method5719(int[] var0, int var1, int var2) {
      for (int var3 = 0; var3 < var2; var3++) {
         method5713(var0, 15 - var1 + var3, 15 - var1);
         method5713(var0, 15 - var1, 15 - var1 + var3);
         method5713(var0, 15 + var1 - var3, 15 - var1);
         method5713(var0, 15 + var1, 15 - var1 + var3);
         method5713(var0, 15 - var1 + var3, 15 + var1);
         method5713(var0, 15 - var1, 15 + var1 - var3);
         method5713(var0, 15 + var1 - var3, 15 + var1);
         method5713(var0, 15 + var1, 15 + var1 - var3);
      }
   }

   private static void method5720(int[] var0) {
      for (int var1 = 0; var1 <= 6; var1++) {
         method5713(var0, 15 - var1, 15 + var1 - 3);
         method5713(var0, 15 + var1, 15 + var1 - 3);
      }
   }
}

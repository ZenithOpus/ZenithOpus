package org.zenith.utility.misc;

import org.zenith.utility.render.Glowboost;

public final class SnowVisibility {
   private static final float[] CONST_floatValues = new float[]{0.42F, 0.84F, 0.34F};
   private static boolean visible;
   private static final FloatArrayBuffer floatArrayBuffer = new FloatArrayBuffer();
   private static int intValue;
   private static int intValue2;
   private static int intValue3;

   private SnowVisibility() {
   }

   public static boolean isVisible() {
      return visible && !floatArrayBuffer.isEmpty();
   }

   public static void setVisible(boolean var0) {
      visible = var0;
   }

   public static FloatArrayBuffer getFloatArrayBuffer() {
      return floatArrayBuffer;
   }

   public static void method2401(int var0, int var1, int var2) {
      intValue = var0;
      intValue2 = var1;
      intValue3 = var2;
   }

   public static void method2402(Glowboost var0) {
      if (isVisible()) {
         var0.method3235(0.0F);
         float var1 = 0.25F;

         for (int var2 = 0; var2 < floatArrayBuffer.getIntValue(); var2++) {
            int var3 = method2403(floatArrayBuffer.method383(var2, 0), floatArrayBuffer.method384(var2, 0), floatArrayBuffer.method385(var2, 0));
            int var4 = method2403(floatArrayBuffer.method383(var2, 1), floatArrayBuffer.method384(var2, 1), floatArrayBuffer.method385(var2, 1));
            int var5 = method2403(floatArrayBuffer.method383(var2, 2), floatArrayBuffer.method384(var2, 2), floatArrayBuffer.method385(var2, 2));
            int var6 = method2403(floatArrayBuffer.method383(var2, 3), floatArrayBuffer.method384(var2, 3), floatArrayBuffer.method385(var2, 3));
            var0.method3239(
               floatArrayBuffer.method380(var2, 0) * var1,
               floatArrayBuffer.method381(var2, 0) * var1,
               floatArrayBuffer.method382(var2, 0) * var1,
               floatArrayBuffer.method380(var2, 1) * var1,
               floatArrayBuffer.method381(var2, 1) * var1,
               floatArrayBuffer.method382(var2, 1) * var1,
               floatArrayBuffer.method380(var2, 2) * var1,
               floatArrayBuffer.method381(var2, 2) * var1,
               floatArrayBuffer.method382(var2, 2) * var1,
               floatArrayBuffer.method380(var2, 3) * var1,
               floatArrayBuffer.method381(var2, 3) * var1,
               floatArrayBuffer.method382(var2, 3) * var1,
               var3,
               var4,
               var5,
               var6
            );
         }
      }
   }

   private static int method2403(float var0, float var1, float var2) {
      float var3 = Math.max(0.0F, var0 * CONST_floatValues[0] + var1 * CONST_floatValues[1] + var2 * CONST_floatValues[2]);
      float var4 = 0.42F + 0.58F * var3;
      int var5 = Math.clamp((long)((int)(var4 * 255.0F + 0.5F)), 0, 255);
      return 0xFF000000 | var5 << 16 | var5 << 8 | Math.min(255, var5 + 8);
   }
}

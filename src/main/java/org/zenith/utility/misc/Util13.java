package org.zenith.utility.misc;

public final class Util13 {
   private static volatile float floatValue = 1.0F;

   private Util13() {
   }

   public static float getFloatValue() {
      return floatValue;
   }

   public static void method2404(float var0) {
      if (Float.isFinite(var0) && var0 > 0.0F) {
         floatValue = var0;
      }
   }
}

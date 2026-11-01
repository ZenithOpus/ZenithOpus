package org.zenith.utility.render;

import net.minecraft.client.render.fog.FogData;
import org.joml.Vector4f;

public final class Util26 {
   private static final Vector4f vector4f = new Vector4f();
   private static float floatValue = Float.MAX_VALUE;
   private static float floatValue2 = Float.MAX_VALUE;
   private static float floatValue3 = Float.MAX_VALUE;
   private static float floatValue4 = Float.MAX_VALUE;

   private Util26() {
   }

   public static void method6982(Vector4f var0, FogData var1) {
      vector4f.set(var0);
      floatValue = var1.environmentalStart;
      floatValue2 = var1.environmentalEnd;
      floatValue3 = var1.renderDistanceStart;
      floatValue4 = var1.renderDistanceEnd;
   }

   public static Vector4f getVector4f() {
      return vector4f;
   }

   public static float getFloatValue() {
      return floatValue;
   }

   public static float getFloatValue2() {
      return floatValue2;
   }

   public static float getFloatValue3() {
      return floatValue3;
   }

   public static float getFloatValue4() {
      return floatValue4;
   }
}

package org.zenith.utility.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.zenith.MatrixUtils;
import org.zenith.utility.game.FlakeState;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.WeatherFlakeChannel;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class Util25 {
   public static final float CONST_floatValue = 26.0F;
   public static final int CONST_intValue = 5;
   public static final double CONST_doubleValue = 2.0;
   private static final int CONST_intValue2 = -8749180;
   private static final int CONST_intValue3 = -11644329;

   private Util25() {
   }

   public static MatrixStack method6477(MenuDrawContext var0, float var1, float var2, float var3) {
      MatrixStack var4 = MatrixUtils.method666(var0.getMatrices());
      var4.translate(var1, var2, 50.0F);
      var4.scale(var3, -var3, var3 * 0.4F);
      var4.multiply(RotationAxis.POSITIVE_X.rotationDegrees(26.0F));
      return var4;
   }

   public static float method6478() {
      return -26.0F;
   }

   public static float method6479() {
      return 0.0F;
   }

   public static void method6480(Glowboost var0, FlakeState var1, Util25.Data var2, float var3) {
      int var4 = var1.getIntValue3();

      for (int var5 = -var4; var5 <= var4; var5++) {
         for (int var6 = -var4; var6 <= var4; var6++) {
            double var7 = var1.method4544(var5, var6);
            if (var7 != Double.NEGATIVE_INFINITY) {
               double var9 = Math.sqrt(var5 * var5 + var6 * var6);
               float var11 = (float)Math.min(1.0, var9 / (var4 + 1.0)) * 0.6F;
               float var12 = 0.9F + 0.2F * Glowboost.method3268(method6482(var5, var6), 0, 0);
               var0.method3237(
                  var5, var7, var6, var5 + 1, var7, var6, var5 + 1, var7, var6 + 1, var5, var7, var6 + 1, method6481(-8749180, var2, var12, var11, var3)
               );
               double var13 = var1.method4544(var5, var6 + 1);
               boolean var15 = var13 == Double.NEGATIVE_INFINITY || var6 == var4;
               double var16 = var15 ? var7 - 1.0 : var13;
               if (var15 || var13 < var7) {
                  var0.method3237(
                     var5,
                     var7,
                     var6 + 1,
                     var5 + 1,
                     var7,
                     var6 + 1,
                     var5 + 1,
                     var16,
                     var6 + 1,
                     var5,
                     var16,
                     var6 + 1,
                     method6481(-11644329, var2, var12, var11, var3)
                  );
               }
            }
         }
      }
   }

   private static int method6481(int var0, Util25.Data var1, float var2, float var3, float var4) {
      float var5 = ColorUtils.method6424(var0) * var2;
      float var6 = ColorUtils.method6425(var0) * var2;
      float var7 = ColorUtils.method6426(var0) * var2;
      float var8 = var5 * 0.299F + var6 * 0.587F + var7 * 0.114F;
      var5 = lerp(var8, var5, var1.getFloatValue()) * var1.getFloatValue3() * var1.getFloatValue2();
      var6 = lerp(var8, var6, var1.getFloatValue()) * var1.getFloatValue4() * var1.getFloatValue2();
      var7 = lerp(var8, var7, var1.getFloatValue()) * var1.getFloatValue5() * var1.getFloatValue2();
      float var9 = var3 * var1.getFloatValue6();
      var5 = lerp(var5, ColorUtils.method6424(var1.getIntValue()), var9);
      var6 = lerp(var6, ColorUtils.method6425(var1.getIntValue()), var9);
      var7 = lerp(var7, ColorUtils.method6426(var1.getIntValue()), var9);
      int var10 = (int)(255.0F * Math.clamp(var4, 0.0F, 1.0F));
      return var10 << 24 | channel(var5) << 16 | channel(var6) << 8 | channel(var7);
   }

   private static int channel(float var0) {
      return Math.clamp((long)((int)(var0 * 255.0F + 0.5F)), 0, 255);
   }

   private static float lerp(float var0, float var1, float var2) {
      return var0 + (var1 - var0) * var2;
   }

   private static long method6482(int var0, int var1) {
      return var0 * -7046029254386353131L ^ var1 * -4417276706812531889L;
   }

   public static final class Data {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final int intValue;
      private final float floatValue6;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public int intValue() {
         return this.intValue;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.floatValue6, other.floatValue6);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, intValue, floatValue6);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "intValue=" + this.intValue + ", " + "floatValue6=" + this.floatValue6 + "]";
      }

      public Data(float var1, float var2, float var3, float var4, float var5, int var6, float var7) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
         this.intValue = var6;
         this.floatValue6 = var7;
      }

      public static Util25.Data method6483(WeatherFlakeChannel var0, float var1) {
         float var2 = Math.clamp(var1, 0.0F, 1.0F);
         return new Util25.Data(
            Util25.lerp(1.0F, var0.floatValue16, var2),
            Util25.lerp(1.0F, var0.floatValue15, var2),
            Util25.lerp(1.0F, var0.floatValues11[0], var2),
            Util25.lerp(1.0F, var0.floatValues11[1], var2),
            Util25.lerp(1.0F, var0.floatValues11[2], var2),
            var0.intValue,
            var2
         );
      }

      public static Util25.Data method6484(float var0, float var1, int var2, float var3) {
         return new Util25.Data(var0, var1, 1.0F, 1.0F, 1.0F, var2, var3);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }
   }
}

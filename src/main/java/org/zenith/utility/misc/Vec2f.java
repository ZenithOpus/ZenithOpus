package org.zenith.utility.misc;

public class Vec2f {
   private final float floatValue;
   private final float floatValue2;

   public Vec2f(float var1, float var2) {
      this.floatValue = var1;
      this.floatValue2 = var2;
   }

   public float method5327() {
      return (float)Math.sqrt(this.floatValue * this.floatValue + this.floatValue2 * this.floatValue2);
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public net.minecraft.util.math.Vec2f method5328() {
      return new net.minecraft.util.math.Vec2f(this.floatValue, this.floatValue2);
   }

   public boolean method5329(float var1) {
      return this.method5330(var1, var1);
   }

   public boolean method5330(float var1, float var2) {
      return Math.abs(this.floatValue) <= var1 && Math.abs(this.floatValue2) <= var2;
   }
}

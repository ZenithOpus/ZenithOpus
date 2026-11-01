package org.zenith.utility.render.display.base;

import org.zenith.utility.render.MathUtils;

public final class BoundsData {
   private final float floatValue;
   private final float floatValue2;
   private final float floatValue3;
   private final float floatValue4;

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

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof BoundsData other)) return false;
      return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4);
   }

   @Override
   public String toString() {
      return "BoundsData[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + "]";
   }

   public BoundsData(float var1, float var2, float var3, float var4) {
      this.floatValue = var1;
      this.floatValue2 = var2;
      this.floatValue3 = var3;
      this.floatValue4 = var4;
   }

   public boolean contains(double var1, double var3) {
      return MathUtils.method2647(var1, var3, this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4);
   }

   public boolean method8092(double var1, double var3, float var5) {
      return MathUtils.method2647(var1, var3, this.floatValue - var5, this.floatValue2 - var5, this.floatValue3 + var5 * 2.0F, this.floatValue4 + var5 * 2.0F);
   }

   public boolean method8093(BoundsData var1) {
      return this.floatValue < var1.getFloatValue() + var1.width()
         && this.floatValue + this.floatValue3 > var1.getFloatValue()
         && this.floatValue2 < var1.getFloatValue2() + var1.height()
         && this.floatValue2 + this.floatValue4 > var1.getFloatValue2();
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public float width() {
      return this.floatValue3;
   }

   public float height() {
      return this.floatValue4;
   }
}

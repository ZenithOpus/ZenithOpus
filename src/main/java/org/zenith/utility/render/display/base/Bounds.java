package org.zenith.utility.render.display.base;

import lombok.Generated;
import org.zenith.utility.render.MathUtils;

public class Bounds {
   public float floatValue;
   public float floatValue2;
   public float width;
   public float height;

   public boolean contains(double var1, double var3) {
      return MathUtils.method2647(var1, var3, this.floatValue, this.floatValue2, this.width, this.height);
   }

   @Generated
   public Bounds(float var1, float var2, float var3, float var4) {
      this.floatValue = var1;
      this.floatValue2 = var2;
      this.width = var3;
      this.height = var4;
   }

   @Generated
   public float getX() {
      return this.floatValue;
   }

   @Generated
   public float getY() {
      return this.floatValue2;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public void setX(float var1) {
      this.floatValue = var1;
   }

   @Generated
   public void setY(float var1) {
      this.floatValue2 = var1;
   }

   @Generated
   public void setWidth(float var1) {
      this.width = var1;
   }

   @Generated
   public void setHeight(float var1) {
      this.height = var1;
   }

   @Generated
   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Bounds var2)) {
         return false;
      } else if (!var2.method8094(this)) {
         return false;
      } else if (Float.compare(this.getX(), var2.getX()) != 0) {
         return false;
      } else if (Float.compare(this.getY(), var2.getY()) != 0) {
         return false;
      } else {
         return Float.compare(this.getWidth(), var2.getWidth()) != 0 ? false : Float.compare(this.getHeight(), var2.getHeight()) == 0;
      }
   }

   @Generated
   public boolean method8094(Object var1) {
      return var1 instanceof Bounds;
   }

   @Generated
   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + Float.floatToIntBits(this.getX());
      var2 = var2 * 59 + Float.floatToIntBits(this.getY());
      var2 = var2 * 59 + Float.floatToIntBits(this.getWidth());
      return var2 * 59 + Float.floatToIntBits(this.getHeight());
   }

   @Generated
   @Override
   public String toString() {
      return "ChangeRect(x=" + this.getX() + ", y=" + this.getY() + ", width=" + this.getWidth() + ", height=" + this.getHeight() + ")";
   }
}

package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;

public class DirectionEvent implements Event {
   private float floatValue;
   private float floatValue2;

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   @Generated
   public float getFloatValue2() {
      return this.floatValue2;
   }

   @Generated
   public void setFloatValue(float var1) {
      this.floatValue = var1;
   }

   @Generated
   public void setFloatValue2(float var1) {
      this.floatValue2 = var1;
   }

   @Generated
   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof DirectionEvent var2)) {
         return false;
      } else if (!var2.method490(this)) {
         return false;
      } else {
         return Float.compare(this.getFloatValue(), var2.getFloatValue()) != 0 ? false : Float.compare(this.getFloatValue2(), var2.getFloatValue2()) == 0;
      }
   }

   @Generated
   public boolean method490(Object var1) {
      return var1 instanceof DirectionEvent;
   }

   @Generated
   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + Float.floatToIntBits(this.getFloatValue());
      return var2 * 59 + Float.floatToIntBits(this.getFloatValue2());
   }

   @Generated
   @Override
   public String toString() {
      return "EventDirection(yaw=" + this.getFloatValue() + ", pitch=" + this.getFloatValue2() + ")";
   }

   @Generated
   public DirectionEvent(float var1, float var2) {
      this.floatValue = var1;
      this.floatValue2 = var2;
   }
}

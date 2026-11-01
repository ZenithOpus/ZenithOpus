package org.zenith.client.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import lombok.Generated;

public class MouseScrollEvent extends EventCancellable {
   private double doubleValue;
   private double doubleValue2;

   @Generated
   public double getDoubleValue() {
      return this.doubleValue;
   }

   @Generated
   public double getDoubleValue2() {
      return this.doubleValue2;
   }

   @Generated
   public void setDoubleValue(double var1) {
      this.doubleValue = var1;
   }

   @Generated
   public void setDoubleValue2(double var1) {
      this.doubleValue2 = var1;
   }

   @Generated
   public MouseScrollEvent(double var1, double var3) {
      this.doubleValue = var1;
      this.doubleValue2 = var3;
   }
}

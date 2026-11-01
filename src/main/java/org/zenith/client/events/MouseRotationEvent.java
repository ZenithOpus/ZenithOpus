package org.zenith.client.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import lombok.Generated;

public class MouseRotationEvent extends EventCancellable {
   public double doubleValue;
   public double doubleValue2;
   public double doubleValue3;
   public double doubleValue4;

   @Generated
   public double getDoubleValue() {
      return this.doubleValue;
   }

   @Generated
   public double getDoubleValue2() {
      return this.doubleValue2;
   }

   @Generated
   public double getDoubleValue3() {
      return this.doubleValue3;
   }

   @Generated
   public double getDoubleValue4() {
      return this.doubleValue4;
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
   public void setDoubleValue3(double var1) {
      this.doubleValue3 = var1;
   }

   @Generated
   public void setDoubleValue4(double var1) {
      this.doubleValue4 = var1;
   }

   @Generated
   public MouseRotationEvent(double var1, double var3, double var5, double var7) {
      this.doubleValue = var1;
      this.doubleValue2 = var3;
      this.doubleValue3 = var5;
      this.doubleValue4 = var7;
   }
}

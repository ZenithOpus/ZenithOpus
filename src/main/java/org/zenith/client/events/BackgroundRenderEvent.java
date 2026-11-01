package org.zenith.client.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import lombok.Generated;

public class BackgroundRenderEvent extends EventCancellable {
   private float floatValue;
   private int color;
   private int intValue;
   private float floatValue2;

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   @Generated
   public int getColor() {
      return this.color;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
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
   public void setColor(int var1) {
      this.color = var1;
   }

   @Generated
   public void setIntValue(int var1) {
      this.intValue = var1;
   }

   @Generated
   public void setFloatValue2(float var1) {
      this.floatValue2 = var1;
   }
}

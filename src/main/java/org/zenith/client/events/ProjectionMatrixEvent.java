package org.zenith.client.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import lombok.Generated;

public class ProjectionMatrixEvent extends EventCancellable {
   private float floatValue;

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   @Generated
   public void setFloatValue(float var1) {
      this.floatValue = var1;
   }
}

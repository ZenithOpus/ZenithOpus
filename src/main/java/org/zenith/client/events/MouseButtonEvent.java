package org.zenith.client.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import lombok.Generated;

public class MouseButtonEvent extends EventCancellable {
   private final int intValue;
   private final int intValue2;

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public int getIntValue2() {
      return this.intValue2;
   }

   @Generated
   public MouseButtonEvent(int var1, int var2) {
      this.intValue = var1;
      this.intValue2 = var2;
   }
}

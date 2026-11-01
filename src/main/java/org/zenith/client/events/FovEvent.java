package org.zenith.client.events;

import lombok.Generated;
import org.zenith.CancellableEvent;

public class FovEvent extends CancellableEvent {
   private int intValue;

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public void setIntValue(int var1) {
      this.intValue = var1;
   }
}

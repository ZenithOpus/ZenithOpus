package org.zenith.client.events;

import lombok.Generated;
import org.zenith.CancellableEvent;

public class SprintUpdateEvent extends CancellableEvent {
   private boolean flag;

   @Generated
   public SprintUpdateEvent(boolean var1) {
      this.flag = var1;
   }

   @Generated
   public boolean isFlag2() {
      return this.flag;
   }

   @Generated
   public void setFlag2(boolean var1) {
      this.flag = var1;
   }
}

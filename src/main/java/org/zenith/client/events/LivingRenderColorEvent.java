package org.zenith.client.events;

import lombok.Generated;
import org.zenith.CancellableEvent;

public class LivingRenderColorEvent extends CancellableEvent {
   private int color;

   @Generated
   public int getColor() {
      return this.color;
   }

   @Generated
   public void setColor(int var1) {
      this.color = var1;
   }

   @Generated
   public LivingRenderColorEvent(int var1) {
      this.color = var1;
   }
}

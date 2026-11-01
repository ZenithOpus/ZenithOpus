package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class DrawEvent implements Event {
   private final CustomDrawContext customDrawContext;
   private final float floatValue;

   @Generated
   public CustomDrawContext getCustomDrawContext() {
      return this.customDrawContext;
   }

   @Generated
   public float getTickDelta() {
      return this.floatValue;
   }

   @Generated
   public DrawEvent(CustomDrawContext var1, float var2) {
      this.customDrawContext = var1;
      this.floatValue = var2;
   }
}

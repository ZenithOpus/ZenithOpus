package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import org.zenith.utility.render.display.base.MenuDrawContext;

public class ScreenRenderEvent implements Event {
   private final MenuDrawContext menuDrawContext;

   @Generated
   public MenuDrawContext getMenuDrawContext() {
      return this.menuDrawContext;
   }

   @Generated
   public ScreenRenderEvent(MenuDrawContext var1) {
      this.menuDrawContext = var1;
   }
}

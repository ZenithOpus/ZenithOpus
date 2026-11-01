package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.client.gui.screen.Screen;
import org.zenith.CancellableEvent;

public class CloseHandledScreenEvent extends CancellableEvent {
   private final Screen screen;

   @Generated
   public CloseHandledScreenEvent(Screen var1) {
      this.screen = var1;
   }
}

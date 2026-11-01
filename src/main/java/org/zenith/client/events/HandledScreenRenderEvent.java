package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.slot.Slot;

public class HandledScreenRenderEvent implements Event {
   private final DrawContext drawContext;
   private final Slot slot;
   private final int intValue;
   private final int intValue2;

   @Generated
   public DrawContext getDrawContext() {
      return this.drawContext;
   }

   @Generated
   public Slot getSlot() {
      return this.slot;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public int getIntValue2() {
      return this.intValue2;
   }

   @Generated
   public HandledScreenRenderEvent(DrawContext var1, Slot var2, int var3, int var4) {
      this.drawContext = var1;
      this.slot = var2;
      this.intValue = var3;
      this.intValue2 = var4;
   }
}

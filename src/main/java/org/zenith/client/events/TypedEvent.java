package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import com.darkmagician6.eventapi.events.Typed;

public abstract class TypedEvent implements Event, Typed {
   private final byte byteValue;

   public TypedEvent(byte var1) {
      this.byteValue = var1;
   }

   @Override
   public byte getType() {
      return this.byteValue;
   }
}

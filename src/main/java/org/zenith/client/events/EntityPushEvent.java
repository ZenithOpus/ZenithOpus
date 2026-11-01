package org.zenith.client.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import lombok.Generated;

public class EntityPushEvent extends EventCancellable {
   private EntityPushEvent.Type type;

   @Generated
   public EntityPushEvent.Type getType() {
      return this.type;
   }

   @Generated
   public EntityPushEvent(EntityPushEvent.Type var1) {
      this.type = var1;
   }

   public static enum Type {
      COLLISION,
      BLOCK,
      WATER;
   }
}

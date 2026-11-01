package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.entity.Entity;

public class EntityAddEvent implements Event {
   private Entity entity;

   @Generated
   public Entity getEntity() {
      return this.entity;
   }

   @Generated
   public EntityAddEvent(Entity var1) {
      this.entity = var1;
   }
}

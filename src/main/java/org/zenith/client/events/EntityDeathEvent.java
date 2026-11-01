package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.entity.LivingEntity;

public class EntityDeathEvent implements Event {
   private final LivingEntity livingEntity;

   @Generated
   public EntityDeathEvent(LivingEntity var1) {
      this.livingEntity = var1;
   }

   @Generated
   public LivingEntity getLivingEntity() {
      return this.livingEntity;
   }
}

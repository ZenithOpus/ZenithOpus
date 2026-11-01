package org.zenith.base.bot.client;

import com.darkmagician6.eventapi.EventBus;
import com.darkmagician6.eventapi.events.Event;

public final class BotEventBus {
   private final EventBus bus = new EventBus();

   public void register(Object var1) {
      this.bus.register(var1);
   }

   public void register(Object var1, Class<? extends Event> var2) {
      this.bus.register(var1, var2);
   }

   public void unregister(Object var1) {
      this.bus.unregister(var1);
   }

   public Event call(Event var1) {
      return this.bus.call(var1);
   }
}

package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import org.zenith.base.Module;

public class ModuleToggleEvent implements Event {
   private final Module module;
   private final boolean flag;

   @Generated
   public ModuleToggleEvent(Module var1, boolean var2) {
      this.module = var1;
      this.flag = var2;
   }

   @Generated
   public Module getModule() {
      return this.module;
   }

   @Generated
   public boolean isEnabled() {
      return this.flag;
   }
}

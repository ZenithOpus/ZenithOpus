package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.client.gui.screen.Screen;

public class SetScreenEvent implements Event {
   private Screen screen;

   @Generated
   public SetScreenEvent(Screen var1) {
      this.screen = var1;
   }

   @Generated
   public Screen getScreen() {
      return this.screen;
   }

   @Generated
   public void setScreen(Screen var1) {
      this.screen = var1;
   }

   @Generated
   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SetScreenEvent var2)) {
         return false;
      } else if (!var2.method6720(this)) {
         return false;
      } else {
         Screen var3 = this.getScreen();
         Screen var4 = var2.getScreen();
         return var3 == null ? var4 == null : var3.equals(var4);
      }
   }

   @Generated
   public boolean method6720(Object var1) {
      return var1 instanceof SetScreenEvent;
   }

   @Generated
   @Override
   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      Screen var3 = this.getScreen();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "EventSetScreen(screen=" + this.getScreen() + ")";
   }
}

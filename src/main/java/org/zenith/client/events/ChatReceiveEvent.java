package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.text.Text;
import org.zenith.CancellableEvent;

public class ChatReceiveEvent extends CancellableEvent {
   private Text text;
   private boolean flag;
   private boolean flag2;

   public ChatReceiveEvent(Text var1) {
      this.text = var1;
   }

   public void method7858(Text var1) {
      this.flag = true;
      this.text = var1;
      super.cancel();
   }

   public boolean method7859() {
      return this.flag && !this.flag2;
   }

   @Override
   public void setCancelled(boolean var1) {
      super.setCancelled(var1);
      this.flag2 = var1;
   }

   @Override
   public void cancel() {
      super.cancel();
      this.flag2 = true;
   }

   @Generated
   public Text getText() {
      return this.text;
   }

   @Generated
   public boolean isHidden() {
      return this.flag2;
   }
}

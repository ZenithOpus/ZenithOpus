package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import org.zenith.utility.network.CloudPackets;

public class Event11 implements Event {
   private final CloudPackets.Data13 data13;
   private final boolean flag;

   public Event11(CloudPackets.Data13 var1, boolean var2) {
      this.data13 = var1;
      this.flag = var2;
   }

   public CloudPackets.Data13 getData13() {
      return this.data13;
   }

   public boolean isFlag() {
      return this.flag;
   }
}

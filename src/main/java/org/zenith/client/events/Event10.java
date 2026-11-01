package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import org.zenith.utility.network.CloudPackets;

public class Event10 implements Event {
   private final CloudPackets.Data18 data18;

   public Event10(CloudPackets.Data18 var1) {
      this.data18 = var1;
   }

   public CloudPackets.Data18 getData18() {
      return this.data18;
   }

   public int method7105() {
      return this.data18.kind();
   }
}

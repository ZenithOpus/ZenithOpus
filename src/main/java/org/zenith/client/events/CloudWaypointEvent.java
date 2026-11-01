package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import org.zenith.utility.network.CloudPackets;

public class CloudWaypointEvent implements Event {
   private final CloudPackets.Data10 data10;
   private final boolean flag;

   public CloudWaypointEvent(CloudPackets.Data10 var1, boolean var2) {
      this.data10 = var1;
      this.flag = var2;
   }

   public CloudPackets.Data10 getData10() {
      return this.data10;
   }

   public boolean isFlag() {
      return this.flag;
   }
}

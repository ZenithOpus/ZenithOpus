package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import org.zenith.utility.misc.PartyPlayerInfo;

public class PartyUpdateEvent implements Event {
   private final PartyPlayerInfo partyPlayerInfo;
   private final boolean flag;

   public PartyUpdateEvent(PartyPlayerInfo var1, boolean var2) {
      this.partyPlayerInfo = var1;
      this.flag = var2;
   }

   public PartyPlayerInfo getPartyPlayerInfo() {
      return this.partyPlayerInfo;
   }

   public boolean isFlag() {
      return this.flag;
   }
}

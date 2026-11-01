package org.zenith.client.screens.nlgui;

import org.zenith.utility.misc.Enum3;

public interface MouseBindTarget {
   boolean bindMouse(Enum3 var1);

   static boolean bindable(Enum3 var0) {
      return var0 != null && var0.getIntValue() >= Enum3.MIDDLE.getIntValue();
   }
}

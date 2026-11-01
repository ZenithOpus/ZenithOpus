package org.zenith.utility.misc;

import java.util.Locale;

public enum WardrobeCategory {
   PET,
   CLOTHING,
   CAPE,
   ACCESSORY,
   CHARACTER,
   WEAPON,
   ITEM;

   public String key() {
      return "wardrobe.category." + this.name().toLowerCase(Locale.ROOT);
   }
}

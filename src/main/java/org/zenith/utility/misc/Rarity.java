package org.zenith.utility.misc;

import java.util.Locale;

public enum Rarity {
   COMMON(-6642771, false, 0),
   RARE(-11557889, false, 1),
   EPIC(-5018625, true, 2),
   LEGENDARY(45885, true, 3),
   MYTHIC(-45189, true, 4);

   private final int intValue;
   private final boolean flag;
   private final int intValue2;

   private Rarity(int var3, boolean var4, int var5) {
      this.intValue = var3;
      this.flag = var4;
      this.intValue2 = var5;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public int getIntValue2() {
      return this.intValue2;
   }

   public String key() {
      return "wardrobe.rarity." + this.name().toLowerCase(Locale.ROOT);
   }

   public static Rarity method4709(String var0) {
      if (var0 == null) {
         return COMMON;
      } else {
         try {
            return valueOf(var0.strip().toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var2) {
            return COMMON;
         }
      }
   }
}

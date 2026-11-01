package org.zenith.utility.misc;

import lombok.Generated;

public enum Enum3 {
   LEFT(0),
   RIGHT(1),
   MIDDLE(2),
   BUTTON_4(3),
   BUTTON_5(4),
   BUTTON_6(5),
   BUTTON_7(6);

   private final int intValue;

   public static Enum3 method6094(int var0) {
      for (Enum3 var4 : values()) {
         if (var4.getIntValue() == var0) {
            return var4;
         }
      }

      return LEFT;
   }

   @Generated
   private Enum3(int var3) {
      this.intValue = var3;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }
}

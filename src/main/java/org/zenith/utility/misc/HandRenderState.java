package org.zenith.utility.misc;

public final class HandRenderState {
   private static boolean active;

   private HandRenderState() {
   }

   public static boolean isActive() {
      return active;
   }

   public static void begin() {
      active = true;
   }

   public static void end() {
      active = false;
   }
}

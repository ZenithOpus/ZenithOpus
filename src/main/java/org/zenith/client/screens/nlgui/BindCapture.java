package org.zenith.client.screens.nlgui;


public final class BindCapture {
   private static Object owner;

   private BindCapture() {
   }

   public static void begin(Object var0) {
      owner = var0;
   }

   public static void end(Object var0) {
      if (owner == var0) {
         owner = null;
      }
   }

   public static void reset() {
      owner = null;
   }

   public static boolean isActive() {
      return owner != null;
   }

   public static Object owner() {
      return owner;
   }
}

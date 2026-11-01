package org.zenith.utility.misc;

import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import org.zenith.utility.render.ScissorFlusher;

public final class HudBatchState {
   public static boolean enabled = true;
   private static Immediate immediate;
   private static int depth;
   private static boolean flag;
   private static boolean flag2;
   private static boolean flag3;
   private static boolean flag4;

   private HudBatchState() {
   }

   public static boolean isActive() {
      return enabled && depth > 0 && !flag4;
   }

   public static void begin() {
      if (enabled) {
         depth++;
      }
   }

   public static void end() {
      if (depth != 0) {
         depth--;
         if (depth <= 0) {
            method7759();
            method7760();
            immediate = null;
            ScissorFlusher.flush();
         }
      }
   }

   public static void method7757(Immediate var0, boolean var1) {
      if (var0 != immediate) {
         method7759();
         immediate = var0;
      }

      if (flag && var1 != flag2) {
         method7759();
      }

      if (var1 != flag3) {
         if (var1) {
         }

         flag3 = var1;
      }

      flag2 = var1;
   }

   public static void method7758() {
      flag = true;
   }

   public static void method7759() {
      if (flag && !flag4 && immediate != null) {
         flag4 = true;

         try {
            immediate.draw();
         } finally {
            flag = false;
            flag4 = false;
         }

         method7760();
      }
   }

   private static void method7760() {
      if (flag3) {
         flag3 = false;
      }
   }
}

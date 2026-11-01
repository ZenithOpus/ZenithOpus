package org.zenith.base.rotation;

import org.zenith.MinecraftAccess;
import org.zenith.Rotation;
import org.zenith.client.modules.impl.player.AutoWardenModule;

public final class RotationLock implements MinecraftAccess {
   private static final int CONST_intValue = 2;
   private static Rotation rotation;
   private static int intValue = Integer.MIN_VALUE;

   private RotationLock() {
   }

   public static boolean method2706() {
      return AutoWardenModule.INSTANCE.isEnabled() && AutoWardenModule.INSTANCE.method3949();
   }

   public static void method2707(float var0, float var1) {
      if (mc.player != null) {
         rotation = new Rotation(var0, var1);
         intValue = mc.player.age;
      }
   }

   public static Rotation method2708() {
      if (mc.player != null && rotation != null) {
         int var0 = mc.player.age - intValue;
         return var0 >= 0 && var0 <= 2 ? rotation : null;
      } else {
         return null;
      }
   }

   public static void reset() {
      rotation = null;
      intValue = Integer.MIN_VALUE;
   }
}

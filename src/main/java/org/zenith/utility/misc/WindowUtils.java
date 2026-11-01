package org.zenith.utility.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OperatingSystem;

public final class WindowUtils {
   private WindowUtils() {
   }

   private static boolean method5474(int var0, int var1) {
      Window var2 = MinecraftClient.getInstance().getWindow();
      return InputUtil.isKeyPressed(var2, var0) || InputUtil.isKeyPressed(var2, var1);
   }

   public static boolean method5475() {
      return Util.getOperatingSystem() == OperatingSystem.OSX ? method5474(343, 347) : method5474(341, 345);
   }

   public static boolean method5476() {
      return method5474(340, 344);
   }

   public static boolean method5477() {
      return method5474(342, 346);
   }
}

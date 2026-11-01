package org.zenith.utility.network;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;

public final class BlinkUtils {
   private BlinkUtils() {
   }

   public static boolean method1444(Packet<?> var0) {
      return var0 instanceof PlayerMoveC2SPacket || var0 instanceof VehicleMoveC2SPacket;
   }
}

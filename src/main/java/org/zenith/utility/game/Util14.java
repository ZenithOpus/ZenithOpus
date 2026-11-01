package org.zenith.utility.game;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.zenith.MinecraftAccess;

public final class Util14 implements MinecraftAccess {
   private static World world;
   private static boolean flag;
   private static boolean flag2;

   private Util14() {
   }

   public static void method2637(PlayerEntity var0, ItemStack var1) {
      if (var0 != null && var1 != null && !var1.isEmpty()) {
         if (mc.player != null && var0 != mc.player) {
            method2641();
            if (var1.getCount() > var1.getItem().getMaxCount()) {
               flag = true;
               flag2 = true;
            }

            if (var1.contains(DataComponentTypes.DAMAGE) && !var1.isDamageable()) {
               flag2 = true;
            }
         }
      }
   }

   public static boolean method2638(PlayerEntity var0) {
      method2641();
      return var0 == mc.player || !flag;
   }

   public static boolean method2639(PlayerEntity var0) {
      method2641();
      return var0 == mc.player || !flag2;
   }

   public static boolean method2640() {
      method2641();
      return flag2;
   }

   private static void method2641() {
      if (mc.world != world) {
         world = mc.world;
         flag = false;
         flag2 = false;
      }
   }
}

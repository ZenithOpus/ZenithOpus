package org.zenith.utility.game;

import java.util.List;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public final class ArmorSlots {
   public static final EquipmentSlot[] equipmentSlots = new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

   private ArmorSlots() {
   }

   public static List<ItemStack> method5384(LivingEntity var0) {
      return List.of(
         var0.getEquippedStack(EquipmentSlot.FEET),
         var0.getEquippedStack(EquipmentSlot.LEGS),
         var0.getEquippedStack(EquipmentSlot.CHEST),
         var0.getEquippedStack(EquipmentSlot.HEAD)
      );
   }

   public static List<ItemStack> method5385(LivingEntity var0) {
      return List.of(var0.getEquippedStack(EquipmentSlot.MAINHAND), var0.getEquippedStack(EquipmentSlot.OFFHAND));
   }
}

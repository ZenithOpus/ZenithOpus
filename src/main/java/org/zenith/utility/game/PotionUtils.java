package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.SlotActionType;
import org.zenith.MinecraftAccess;
import org.zenith.utility.network.InventoryUtils;

public final class PotionUtils implements MinecraftAccess {
   private PotionUtils() {
   }

   public static List<StatusEffectInstance> method2321(ItemStack var0) {
      ArrayList<StatusEffectInstance> var1 = new ArrayList<>();
      if (var0 != null && !var0.isEmpty()) {
         PotionContentsComponent var2 = var0.get(DataComponentTypes.POTION_CONTENTS);
         if (var2 == null) {
            return var1;
         } else {
            for (StatusEffectInstance var4 : var2.getEffects()) {
               var1.add(var4);
            }

            return var1;
         }
      } else {
         return var1;
      }
   }

   public static boolean method2322(ItemStack var0, RegistryEntry<StatusEffect> var1) {
      for (StatusEffectInstance var3 : method2321(var0)) {
         if (var3.getEffectType().equals(var1)) {
            return true;
         }
      }

      return false;
   }

   public static boolean method2323(ItemStack var0) {
      return var0 != null && !var0.isEmpty() && var0.isOf(Items.SPLASH_POTION);
   }

   public static boolean method2324(ItemStack var0) {
      return var0 != null && !var0.isEmpty() && var0.isOf(Items.POTION);
   }

   public static int method2325(Predicate<ItemStack> var0) {
      int var1 = method2326(var0);
      if (var1 != -1) {
         return var1;
      } else {
         for (int var2 = 9; var2 < 36; var2++) {
            if (var0.test(mc.player.getInventory().getStack(var2))) {
               return var2;
            }
         }

         return -1;
      }
   }

   public static int method2326(Predicate<ItemStack> var0) {
      for (int var1 = 0; var1 < 9; var1++) {
         if (var0.test(mc.player.getInventory().getStack(var1))) {
            return var1;
         }
      }

      return -1;
   }

   public static int method2327(int var0) {
      return var0 < 9 ? 36 + var0 : var0;
   }

   public static void method2328(int var0, int var1) {
      mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, method2327(var0), var1, SlotActionType.SWAP, mc.player);
      InventoryUtils.closeScreen();
   }

   public static void method2329(int var0) {
      if (mc.player.getInventory().getSelectedSlot() != var0) {
         mc.player.getInventory().setSelectedSlot(var0);
      }

      mc.interactionManager.syncSelectedSlot();
   }
}

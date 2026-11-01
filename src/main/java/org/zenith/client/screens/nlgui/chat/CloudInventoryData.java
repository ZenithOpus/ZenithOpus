package org.zenith.client.screens.nlgui.chat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;

public final class CloudInventoryData {
   public static final int MAIN_SIZE = 36;
   public static final int ARMOR_SIZE = 4;
   private final List<ItemStack> main;
   private final List<ItemStack> armor;
   private final ItemStack offhand;

   public CloudInventoryData(List<ItemStack> var1, List<ItemStack> var2, ItemStack var3) {
      this.main = List.copyOf(padCopy(var1, 36));
      this.armor = List.copyOf(padCopy(var2, 4));
      this.offhand = var3 == null ? ItemStack.EMPTY : var3.copy();
   }

   public List<ItemStack> main() {
      return this.main;
   }

   public List<ItemStack> armor() {
      return this.armor;
   }

   public ItemStack offhand() {
      return this.offhand;
   }

   public ItemStack hotbar(int var1) {
      return var1 >= 0 && var1 < 9 ? this.main.get(var1) : ItemStack.EMPTY;
   }

   public ItemStack inventory(int var1) {
      int var2 = 9 + var1;
      return var1 >= 0 && var2 < 36 ? this.main.get(var2) : ItemStack.EMPTY;
   }

   public int nonEmptyInventoryCount() {
      int var1 = 0;

      for (int var2 = 9; var2 < 36; var2++) {
         if (!this.main.get(var2).isEmpty()) {
            var1++;
         }
      }

      return var1;
   }

   public int nonEmptyHotbarCount() {
      int var1 = 0;

      for (int var2 = 0; var2 < 9; var2++) {
         if (!this.main.get(var2).isEmpty()) {
            var1++;
         }
      }

      return var1;
   }

   private static List<ItemStack> padCopy(List<ItemStack> var0, int var1) {
      List<ItemStack> var2 = new ArrayList<>(var1);
      if (var0 != null) {
         for (ItemStack var4 : var0) {
            var2.add(var4 == null ? ItemStack.EMPTY : var4.copy());
         }
      }

      while (var2.size() < var1) {
         var2.add(ItemStack.EMPTY);
      }

      return (List<ItemStack>)(var2.size() > var1 ? var2.subList(0, var1) : var2);
   }
}

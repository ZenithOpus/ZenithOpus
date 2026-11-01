package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;

public class InventorySnapshot {
   private static final int CONST_intValue = 100;
   private final List<ItemEntry> items = new ArrayList<>();

   public void method1862(ItemStack var1, String var2, int var3, long var4) {
      if (var1 != null && !var1.isEmpty()) {
         if (var3 > 0) {
            ItemStack var6 = var1.copy();
            var6.setCount(var3);
            this.items.add(0, new ItemEntry(var6, var2, var3, Math.max(0L, var4)));

            while (this.items.size() > 100) {
               this.items.removeLast();
            }
         }
      }
   }

   public List<ItemEntry> method1863() {
      return List.copyOf(this.items);
   }
}

package org.zenith.utility.game;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import org.zenith.utility.misc.EnchantMatcher;

public class EnchantedItemMatcher extends ItemMatcher {
   public final ArrayList<EnchantMatcher> arrayList = new ArrayList<>();

   public EnchantedItemMatcher(ItemStack var1, String var2, String var3, ItemMatcher.Type var4) {
      super(var1, var2, var3, var4);
   }

   public EnchantedItemMatcher(ItemStack var1, String var2, ItemMatcher.Type var3) {
      super(var1, var2, var3);
   }

   @Override
   public boolean method2672(ItemStack var1) {
      if (!super.method2672(var1)) {
         return false;
      } else {
         for (EnchantMatcher var3 : this.arrayList) {
            if (!var3.method1746(var1)) {
               return false;
            }
         }

         return true;
      }
   }

   public void method4844(EnchantMatcher var1) {
      this.arrayList.add(var1);
   }

   public ArrayList<EnchantMatcher> getArrayList() {
      return this.arrayList;
   }
}

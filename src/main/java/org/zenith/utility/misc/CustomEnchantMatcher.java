package org.zenith.utility.misc;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.zenith.Zenith;

public class CustomEnchantMatcher extends EnchantMatcher {
   public CustomEnchantMatcher(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   @Override
   public boolean method1746(ItemStack var1) {
      NbtComponent var2 = var1.get(DataComponentTypes.CUSTOM_DATA);
      if (var2 == null) {
         return false;
      } else {
         boolean var3 = Zenith.getInstance().getServerDetector().method1639();
         String var4 = var3 ? "custom-enchantments" : "Enchantments";
         NbtCompound var5 = var2.copyNbt();
         if (var5.contains(var4)) {
            NbtList var6 = var5.getListOrEmpty(var4);

            for (int var7 = 0; var7 < var6.size(); var7++) {
               NbtCompound var8 = var6.getCompoundOrEmpty(var7);
               String var9 = var8.getString(var3 ? "type" : "id", "");
               int var10 = var8.getInt(var3 ? "level" : "lvl", 0);
               if (var9.equals(this.text2)) {
                  return var10 >= this.intValue;
               }
            }
         }

         return false;
      }
   }
}

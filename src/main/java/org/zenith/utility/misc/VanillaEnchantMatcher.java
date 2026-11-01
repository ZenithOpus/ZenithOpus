package org.zenith.utility.misc;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

public class VanillaEnchantMatcher extends EnchantMatcher {
   private static final Map<RegistryKey<?>, String> items = new ConcurrentHashMap<>();

   public VanillaEnchantMatcher(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   @Override
   public boolean method1746(ItemStack var1) {
      if (this.intValue <= 0) {
         return true;
      } else {
         ItemEnchantmentsComponent var2 = var1.getEnchantments();

         for (Entry var4 : var2.getEnchantmentEntries()) {
            Optional var5 = ((RegistryEntry)var4.getKey()).getKey();
            String var6 = var5.isPresent() ? items.computeIfAbsent((RegistryKey<?>)var5.get(), var0 -> Optional.of(var0).toString()) : "Optional.empty";
            if (var6.contains(this.text2)) {
               return var4.getIntValue() >= this.intValue;
            }
         }

         return false;
      }
   }
}

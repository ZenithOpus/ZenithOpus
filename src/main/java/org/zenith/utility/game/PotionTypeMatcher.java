package org.zenith.utility.game;

import java.util.List;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class PotionTypeMatcher extends ItemMatcher {
   private final RegistryEntry<Potion> registryEntry;

   public PotionTypeMatcher(Item var1, RegistryEntry<Potion> var2, String var3, ItemMatcher.Type var4) {
      super(var1.getDefaultStack(), var3, var4);
      this.registryEntry = var2;
      this.itemStack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.of(var2), Optional.empty(), List.of(), Optional.empty()));
   }

   @Override
   public boolean method2672(ItemStack var1) {
      if (!super.method2672(var1)) {
         return false;
      } else {
         PotionContentsComponent var2 = var1.get(DataComponentTypes.POTION_CONTENTS);
         if (var2 == null) {
            return false;
         } else {
            var var3 = var2.potion();
            return var3.isEmpty() ? false : this.method3301((RegistryEntry<Potion>)var3.get());
         }
      }
   }

   private boolean method3301(RegistryEntry<Potion> var1) {
      if (var1.equals(this.registryEntry)) {
         return true;
      } else {
         Identifier var2 = Registries.POTION.getId(this.registryEntry.value());
         Identifier var3 = Registries.POTION.getId((Potion)var1.value());
         return var2 != null && var3 != null
            ? var2.getNamespace().equals(var3.getNamespace()) && method3302(var2.getPath()).equals(method3302(var3.getPath()))
            : false;
      }
   }

   private static String method3302(String var0) {
      if (var0.startsWith("long_")) {
         return var0.substring("long_".length());
      } else {
         return var0.startsWith("strong_") ? var0.substring("strong_".length()) : var0;
      }
   }
}

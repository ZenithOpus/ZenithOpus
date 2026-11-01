package org.zenith.utility.misc;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;

public final class Util10 {
   private Util10() {
   }

   public static boolean method2177(ItemStack var0) {
      return var0.isIn(ItemTags.PICKAXES);
   }

   public static boolean method2178(ItemStack var0) {
      return var0.isIn(ItemTags.SWORDS);
   }

   public static boolean method2179(ItemStack var0) {
      return var0.isIn(ItemTags.AXES);
   }

   public static boolean method2180(ItemStack var0) {
      return var0.isIn(ItemTags.SHOVELS);
   }

   public static boolean method2181(ItemStack var0) {
      return method2183(var0) || method2182(var0) || method2184(var0) || method2185(var0);
   }

   public static boolean method2182(ItemStack var0) {
      return var0.isIn(ItemTags.CHEST_ARMOR);
   }

   public static boolean method2183(ItemStack var0) {
      return var0.isIn(ItemTags.HEAD_ARMOR);
   }

   public static boolean method2184(ItemStack var0) {
      return var0.isIn(ItemTags.LEG_ARMOR);
   }

   public static boolean method2185(ItemStack var0) {
      return var0.isIn(ItemTags.FOOT_ARMOR);
   }
}

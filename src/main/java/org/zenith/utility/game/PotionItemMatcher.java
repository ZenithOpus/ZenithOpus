package org.zenith.utility.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class PotionItemMatcher extends ItemMatcher {
   private static final Pattern pattern = Pattern.compile(",?UUID:\\[I;[-0-9]+,[-0-9]+,[-0-9]+,[-0-9]+]");
   private static final Pattern pattern2 = Pattern.compile(".*\".*\".*");
   private final Map<String, String> items;

   public PotionItemMatcher(ItemStack var1, String var2, String var3, ItemMatcher.Type var4) {
      super(var1, var2, var3, var4);
      this.items = new HashMap<>();
   }

   public PotionItemMatcher(ItemStack var1, String var2, ItemMatcher.Type var3, Map<String, String> var4) {
      super(var1, var2, var3);
      this.items = var4;
   }

   public PotionItemMatcher(ItemStack var1, String var2, ItemMatcher.Type var3, String var4, String var5) {
      super(var1, var2, var3);
      this.items = new HashMap<>();
      this.items.put(var4, var5);
   }

   public PotionItemMatcher(ItemStack var1, String var2, String var3, ItemMatcher.Type var4, String var5, String var6) {
      super(var1, var2, var3, var4);
      this.items = new HashMap<>();
      this.items.put(var5, var6);
   }

   @Override
   public boolean method2672(ItemStack var1) {
      if (!super.method2672(var1)) {
         return false;
      } else {
         NbtComponent var2 = var1.get(DataComponentTypes.CUSTOM_DATA);
         if (var2 == null) {
            return false;
         } else {
            NbtCompound var3 = var2.copyNbt();

            for (Entry var5 : this.items.entrySet()) {
               String var6 = (String)var5.getKey();
               String var7 = (String)var5.getValue();
               if (!var3.contains(var6)) {
                  return false;
               }

               if (method2673(var6, var7)) {
                  if (!method2674(var3, var6, var7)) {
                     return false;
                  }
               } else {
                  String var8 = pattern.matcher(var3.get(var6).toString()).replaceAll("");
                  String var9 = pattern.matcher(var7).replaceAll("");
                  if (!var8.contains(var9)) {
                     return false;
                  }

                  boolean var10 = var9.startsWith("{") || var9.startsWith("[");
                  if (!var10 && pattern2.matcher(var8).matches()) {
                     String var11 = "\"" + var9 + "\"";
                     if (!var8.contains(var11)) {
                        return false;
                     }
                  }
               }
            }

            return true;
         }
      }
   }

   private static boolean method2673(String var0, String var1) {
      return "custom_potion_effects".equals(var0) && var1.startsWith("minecraft:") && !var1.contains("{") && !var1.contains("[");
   }

   private static boolean method2674(NbtCompound var0, String var1, String var2) {
      String var3 = var2;
      int var4 = -1;
      int var5 = var2.indexOf(64);
      if (var5 > 0) {
         var3 = var2.substring(0, var5);

         try {
            var4 = Integer.parseInt(var2.substring(var5 + 1));
         } catch (NumberFormatException var11) {
            var4 = -1;
         }
      }

      if (!(var0.get(var1) instanceof NbtList)) {
         return var0.get(var1).toString().contains(var3);
      } else {
         String var6 = method2675(var3);
         NbtList var7 = var0.getListOrEmpty(var1);
         boolean var8 = false;

         for (int var9 = 0; var9 < var7.size(); var9++) {
            NbtCompound var10 = var7.getCompoundOrEmpty(var9);
            if (!var6.equals(method2675(var10.getString("id", "")))) {
               return false;
            }

            if (var4 < 0 || method2676(var10) >= var4) {
               var8 = true;
            }
         }

         return var8;
      }
   }

   private static String method2675(String var0) {
      return var0 != null && var0.startsWith("minecraft:") ? var0.substring("minecraft:".length()) : String.valueOf(var0);
   }

   private static int method2676(NbtCompound var0) {
      return var0.get("amplifier") instanceof AbstractNbtNumber var1 ? var1.intValue() : 0;
   }
}

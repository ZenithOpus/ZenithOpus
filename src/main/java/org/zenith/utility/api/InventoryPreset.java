package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import org.zenith.Zenith;
import org.zenith.utility.game.HolyWorldItems;
import org.zenith.utility.game.ItemMatcher;

public final class InventoryPreset {
   public static final int CONST_intValue = 36;
   public static final float CONST_floatValue = 0.8F;
   public static final String text = "Funtime 1.21";
   public static final String text2 = "HolyWorld";
   public static final String text3 = "SpookyTime";
   private final String text4;
   private final ItemMatcher[] itemMatchers = new ItemMatcher[36];
   private final int[] intValues = new int[36];
   private final long[] longValues = new long[36];
   private final float[] floatValues = new float[36];

   public InventoryPreset(String var1) {
      this.text4 = var1;
   }

   public String getText4() {
      return this.text4;
   }

   public ItemMatcher method870(int var1) {
      return method888(var1) ? this.itemMatchers[var1] : null;
   }

   public boolean method871(int var1) {
      ItemMatcher var2 = this.method870(var1);
      return var2 == null || var2.getItemStack().isEmpty();
   }

   public boolean isEmpty() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (!this.method871(var1)) {
            return false;
         }
      }

      return true;
   }

   public int method872() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         if (!this.method871(var2)) {
            var1++;
         }
      }

      return var1;
   }

   public int method873() {
      for (int var1 = 0; var1 < 36; var1++) {
         if (this.method871(var1)) {
            return var1;
         }
      }

      return -1;
   }

   public void method874(int var1, ItemMatcher var2) {
      if (method888(var1)) {
         this.itemMatchers[var1] = var2;
         this.intValues[var1] = method883(var2);
         this.longValues[var1] = 0L;
         this.floatValues[var1] = method886(var2) ? 0.8F : 0.0F;
      }
   }

   public void method875(int var1) {
      if (method888(var1)) {
         this.itemMatchers[var1] = null;
         this.intValues[var1] = 0;
         this.longValues[var1] = 0L;
         this.floatValues[var1] = 0.0F;
      }
   }

   public void method876() {
      for (int var1 = 0; var1 < 36; var1++) {
         this.method875(var1);
      }
   }

   public int method877(int var1) {
      ItemMatcher var2 = this.method870(var1);
      if (var2 == null) {
         return 0;
      } else if (!method885(var2)) {
         return method883(var2);
      } else {
         int var3 = method888(var1) ? this.intValues[var1] : 0;
         return var3 > 0 ? Math.min(method884(var2), var3) : method883(var2);
      }
   }

   public void method878(int var1, int var2) {
      if (method888(var1)) {
         ItemMatcher var3 = this.method870(var1);
         this.intValues[var1] = var3 == null ? 0 : Math.max(1, Math.min(method884(var3), var2));
      }
   }

   public long method879(int var1) {
      return method888(var1) ? Math.max(0L, this.longValues[var1]) : 0L;
   }

   public void method880(int var1, long var2) {
      if (method888(var1)) {
         this.longValues[var1] = Math.max(0L, var2);
      }
   }

   public float method881(int var1) {
      return method888(var1) && method886(this.method870(var1)) ? this.floatValues[var1] : 0.0F;
   }

   public void method882(int var1, float var2) {
      if (method888(var1)) {
         this.floatValues[var1] = Math.max(0.0F, Math.min(1.0F, var2));
      }
   }

   public static int method883(ItemMatcher var0) {
      return var0 != null && !var0.getItemStack().isEmpty() ? Math.max(1, var0.getItemStack().getCount()) : 1;
   }

   public static int method884(ItemMatcher var0) {
      if (var0 == null || var0.getItemStack().isEmpty()) {
         return 64;
      } else {
         return method887(var0) ? 64 : Math.min(64, Math.max(1, var0.getItemStack().getMaxCount()));
      }
   }

   public static boolean method885(ItemMatcher var0) {
      return var0 != null && !var0.getItemStack().isEmpty() ? var0.getItemStack().getMaxCount() > 1 || method887(var0) : false;
   }

   public static boolean method886(ItemMatcher var0) {
      if (var0 == null || var0.getItemStack().isEmpty()) {
         return false;
      } else {
         return "Шлем Солнца".equals(var0.getText22()) ? false : var0.getItemStack().getMaxDamage() > 0;
      }
   }

   private static boolean method887(ItemMatcher var0) {
      Item var1 = var0.getItemStack().getItem();
      return var1 == Items.POTION || var1 == Items.SPLASH_POTION || var1 == Items.LINGERING_POTION;
   }

   private static boolean method888(int var0) {
      return var0 >= 0 && var0 < 36;
   }

   public JsonObject save() {
      JsonArray var1 = new JsonArray();

      for (int var2 = 0; var2 < 36; var2++) {
         ItemMatcher var3 = this.method870(var2);
         if (var3 != null && !var3.getItemStack().isEmpty()) {
            JsonObject var4 = new JsonObject();
            var4.addProperty("slot", var2);
            var4.addProperty("search", var3.getText22());
            var4.addProperty("item", Registries.ITEM.getId(var3.getItemStack().getItem()).toString());
            var4.addProperty("count", this.method877(var2));
            var4.addProperty("price", this.method879(var2));
            var4.addProperty("durability", this.method881(var2));
            var1.add(var4);
         }
      }

      JsonObject var5 = new JsonObject();
      var5.add("slots", var1);
      return var5;
   }

   public void load(JsonObject var1) {
      this.method876();
      if (var1 != null && var1.has("slots") && var1.get("slots").isJsonArray()) {
         for (JsonElement var3 : var1.getAsJsonArray("slots")) {
            if (var3.isJsonObject()) {
               JsonObject var4 = var3.getAsJsonObject();
               int var5 = var4.has("slot") ? var4.get("slot").getAsInt() : -1;
               if (method888(var5)) {
                  ItemMatcher var6 = this.method889(
                     var4.has("search") ? var4.get("search").getAsString() : null, var4.has("item") ? var4.get("item").getAsString() : null
                  );
                  if (var6 != null) {
                     this.method874(var5, var6);
                     if (var4.has("count")) {
                        this.method878(var5, var4.get("count").getAsInt());
                     }

                     if (var4.has("price")) {
                        this.method880(var5, var4.get("price").getAsLong());
                     }

                     if (var4.has("durability")) {
                        this.method882(var5, var4.get("durability").getAsFloat());
                     }
                  }
               }
            }
         }
      }
   }

   public ItemMatcher method889(String var1, String var2) {
      if (var1 != null && !var1.isBlank()) {
         ItemMatcher var3 = null;

         for (ItemMatcher var5 : method891(this.text4)) {
            if (var1.equals(var5.getText22())) {
               if (var2 == null || var2.isBlank()) {
                  return var5;
               }

               if (Registries.ITEM.getId(var5.getItemStack().getItem()).toString().equals(var2)) {
                  return var5;
               }

               if (var3 == null) {
                  var3 = var5;
               }
            }
         }

         return var3;
      } else {
         return null;
      }
   }

   public ItemMatcher method890(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         ItemMatcher var2 = null;

         for (ItemMatcher var4 : method891(this.text4)) {
            if (var4.method2672(var1)) {
               if (var4.getClass() != ItemMatcher.class) {
                  return var4;
               }

               if (var2 == null) {
                  var2 = var4;
               }
            }
         }

         return var2;
      } else {
         return null;
      }
   }

   public static List<ItemMatcher> method891(String var0) {
      ArrayList<ItemMatcher> var1 = new ArrayList<>();
      HolyWorldItems var2 = Zenith.getInstance().getHolyWorldItems();
      if (method892(var0)) {
         var1.addAll(var2.getArrayList2());
      } else {
         var1.addAll(var2.getArrayList3());
      }

      var1.addAll(var2.getArrayList());
      return var1;
   }

   public static boolean method892(String var0) {
      return "Funtime 1.21".equals(var0);
   }
}

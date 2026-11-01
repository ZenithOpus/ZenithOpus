package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.registry.Registries;
import org.zenith.utility.game.ItemMatcher;

public final class SborList {
   public static final float CONST_floatValue = -1.0F;
   private final String text;
   private final String text2;
   private final List<SborList.Data> items;

   public SborList(String var1, String var2, List<SborList.Data> var3) {
      this.text = var1;
      this.text2 = var2;
      this.items = List.copyOf(var3);
   }

   public String getName() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public List<SborList.Data> getItems() {
      return this.items;
   }

   public int size() {
      return this.items.size();
   }

   public boolean method1751(String var1) {
      return this.text2 != null && this.text2.equals(var1);
   }

   public static SborList method1752(String var0, InventoryPreset var1) {
      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 < 36; var3++) {
         ItemMatcher var4 = var1.method870(var3);
         if (var4 != null && !var4.getItemStack().isEmpty()) {
            var2.add(
               new SborList.Data(
                  var3,
                  var4.getText22(),
                  Registries.ITEM.getId(var4.getItemStack().getItem()).toString(),
                  var1.method877(var3),
                  var1.method879(var3),
                  var1.method881(var3)
               )
            );
         }
      }

      return new SborList(var0, var1.getText4(), var2);
   }

   public int method1753(InventoryPreset var1) {
      var1.method876();
      int var2 = 0;

      for (SborList.Data var4 : this.items) {
         ItemMatcher var5 = var1.method889(var4.getText(), var4.getText2());
         if (var5 != null && !var5.getItemStack().isEmpty()) {
            var1.method874(var4.getIntValue(), var5);
            if (var4.count() > 0) {
               var1.method878(var4.getIntValue(), var4.count());
            }

            var1.method880(var4.getIntValue(), var4.getLongValue());
            if (var4.getFloatValue() >= 0.0F) {
               var1.method882(var4.getIntValue(), var4.getFloatValue());
            }
         } else {
            var2++;
         }
      }

      return var2;
   }

   public JsonObject save() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("name", this.text);
      var1.addProperty("server", this.text2);
      JsonArray var2 = new JsonArray();

      for (SborList.Data var4 : this.items) {
         JsonObject var5 = new JsonObject();
         var5.addProperty("slot", var4.getIntValue());
         var5.addProperty("search", var4.getText());
         var5.addProperty("item", var4.getText2());
         var5.addProperty("count", var4.count());
         var5.addProperty("price", var4.getLongValue());
         var5.addProperty("durability", var4.getFloatValue());
         var2.add(var5);
      }

      var1.add("slots", var2);
      return var1;
   }

   public static SborList method1754(JsonObject var0) {
      if (var0 != null && var0.has("name")) {
         String var1 = var0.get("name").getAsString();
         if (var1 != null && !var1.isBlank()) {
            String var2 = var0.has("server") ? var0.get("server").getAsString() : null;
            ArrayList var3 = new ArrayList();
            if (var0.has("slots") && var0.get("slots").isJsonArray()) {
               for (JsonElement var5 : var0.getAsJsonArray("slots")) {
                  if (var5.isJsonObject()) {
                     JsonObject var6 = var5.getAsJsonObject();
                     if (var6.has("slot") && var6.has("search")) {
                        var3.add(
                           new SborList.Data(
                              var6.get("slot").getAsInt(),
                              var6.get("search").getAsString(),
                              var6.has("item") ? var6.get("item").getAsString() : null,
                              var6.has("count") ? var6.get("count").getAsInt() : 1,
                              var6.has("price") ? var6.get("price").getAsLong() : 0L,
                              var6.has("durability") ? var6.get("durability").getAsFloat() : -1.0F
                           )
                        );
                     }
                  }
               }
            }

            return new SborList(var1, var2, var3);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static final class Data {
      private final int intValue;
      private final String text;
      private final String text2;
      private final int intValue2;
      private final long longValue;
      private final float floatValue;

      public int intValue() {
         return this.intValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public long longValue() {
         return this.longValue;
      }

      public float floatValue() {
         return this.floatValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.floatValue, other.floatValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, text, text2, intValue2, longValue, floatValue);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "intValue2=" + this.intValue2 + ", " + "longValue=" + this.longValue + ", " + "floatValue=" + this.floatValue + "]";
      }

      public Data(int var1, String var2, String var3, int var4, long var5, float var7) {
         this.intValue = var1;
         this.text = var2;
         this.text2 = var3;
         this.intValue2 = var4;
         this.longValue = var5;
         this.floatValue = var7;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public int count() {
         return this.intValue2;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public float getFloatValue() {
         return this.floatValue;
      }
   }
}

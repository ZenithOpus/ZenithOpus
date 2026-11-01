package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

public final class Presets {
   private static final String text = "presets.json";
   private static final int CONST_intValue = 64;
   private static final int CONST_intValue2 = 32;
   private final Path path;
   private final List<Presets.Data> items = new ArrayList<>();

   public Presets(Path var1) {
      this.path = var1.resolve("presets.json");
      this.load();
   }

   public List<Presets.Data> all() {
      return List.copyOf(this.items);
   }

   public int size() {
      return this.items.size();
   }

   public boolean isFull() {
      return this.items.size() >= 64;
   }

   public boolean method7737(String var1, Collection<String> var2) {
      String var3 = clean(var1);
      if (!var3.isEmpty() && var2 != null && !var2.isEmpty()) {
         var var4 = List.copyOf(new LinkedHashSet(var2));
         int var5 = this.indexOf(var3);
         if (var5 >= 0) {
            this.items.set(var5, new Presets.Data(var3, var4));
            this.method7741();
            return true;
         } else if (this.isFull()) {
            return false;
         } else {
            this.items.add(new Presets.Data(var3, var4));
            this.method7741();
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean method7738(String var1) {
      int var2 = this.indexOf(var1);
      if (var2 < 0) {
         return false;
      } else {
         this.items.remove(var2);
         this.method7741();
         return true;
      }
   }

   public Presets.Data method7739(String var1) {
      int var2 = this.indexOf(var1);
      return var2 < 0 ? null : this.items.get(var2);
   }

   public String method7740() {
      for (int var1 = 1; var1 <= 65; var1++) {
         String var2 = "Набор " + var1;
         if (this.indexOf(var2) < 0) {
            return var2;
         }
      }

      return "Набор";
   }

   private int indexOf(String var1) {
      String var2 = clean(var1).toLowerCase(Locale.ROOT);
      if (var2.isEmpty()) {
         return -1;
      } else {
         for (int var3 = 0; var3 < this.items.size(); var3++) {
            if (this.items.get(var3).name().toLowerCase(Locale.ROOT).equals(var2)) {
               return var3;
            }
         }

         return -1;
      }
   }

   private static String clean(String var0) {
      if (var0 == null) {
         return "";
      } else {
         String var1 = var0.strip();
         return var1.length() > 32 ? var1.substring(0, 32).strip() : var1;
      }
   }

   private void load() {
      JsonObject var1 = JsonIo.method1009(this.path);
      if (var1 != null && var1.has("presets") && var1.get("presets").isJsonArray()) {
         for (JsonElement var3 : var1.getAsJsonArray("presets")) {
            if (var3.isJsonObject()) {
               JsonObject var4 = var3.getAsJsonObject();
               String var5 = var4.has("name") && var4.get("name").isJsonPrimitive() ? clean(var4.get("name").getAsString()) : "";
               if (!var5.isEmpty() && var4.has("items") && var4.get("items").isJsonArray()) {
                  ArrayList var6 = new ArrayList();

                  for (JsonElement var8 : var4.getAsJsonArray("items")) {
                     if (var8.isJsonPrimitive()) {
                        var6.add(var8.getAsString());
                     }
                  }

                  if (!var6.isEmpty() && this.items.size() < 64) {
                     this.items.add(new Presets.Data(var5, var6));
                  }
               }
            }
         }
      }
   }

   private void method7741() {
      JsonObject var1 = new JsonObject();
      JsonArray var2 = new JsonArray();

      for (Presets.Data var4 : this.items) {
         JsonObject var5 = new JsonObject();
         var5.addProperty("name", var4.name());
         JsonArray var6 = new JsonArray();

         for (String var8 : var4.getItems()) {
            var6.add(var8);
         }

         var5.add("items", var6);
         var2.add(var5);
      }

      var1.add("presets", var2);
      JsonIo.method1010(this.path, var1);
   }

   public static final class Data {
      private final String text;
      private final List<String> items;

      public String text() {
         return this.text;
      }

      public List<String> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, items);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "items=" + this.items + "]";
      }

      public Data(String var1, List<String> var2) {
         var2 = List.copyOf(var2);
         this.text = var1;
         this.items = var2;
      }

      public String name() {
         return this.text;
      }

      public List<String> getItems() {
         return this.items;
      }
   }
}

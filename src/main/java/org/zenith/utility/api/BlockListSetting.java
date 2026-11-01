package org.zenith.utility.api;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.zenith.client.settings.Setting;

public class BlockListSetting extends Setting {
   private List<String> items;
   private volatile int version;
   private static final Map<Block, String> items2 = new ConcurrentHashMap<>();
   private static final Map<Item, String> items3 = new ConcurrentHashMap<>();
   private volatile BlockListSetting.Data data;
   private static final Gson gson = new Gson();

   public void method1612(List<String> var1) {
      this.items = var1;
      this.version++;
   }

   public int getVersion() {
      return this.version;
   }

   public BlockListSetting(String var1, List<String> var2) {
      this(var1, "", var2, () -> true);
   }

   public BlockListSetting(String var1, List<String> var2, Supplier<Boolean> var3) {
      this(var1, "", var2, var3);
   }

   public BlockListSetting(String var1, String var2, List<String> var3, Supplier<Boolean> var4) {
      super(var1, var2);
      this.items = var3;
   }

   public List<String> getItems() {
      return this.items;
   }

   public void method1613(String var1) {
      this.items.add(var1);
      this.version++;
   }

   public void remove(String var1) {
      this.items.remove(var1);
      this.version++;
   }

   public boolean contains(String var1) {
      return this.method1614().contains(var1);
   }

   private Set<String> method1614() {
      var var1 = this.items;
      int var2 = this.version;
      BlockListSetting.Data var3 = this.data;
      if (var3 == null || var3.version() != var2 || var3.getItems() != var1) {
         Object[] var4 = var1.toArray();
         Set<String> var5 = new HashSet<>(Math.max(16, var4.length * 2));

         for (Object var9 : var4) {
            var5.add((String)var9);
         }

         var3 = new BlockListSetting.Data(var2, var1, var5);
         this.data = var3;
      }

      return var3.getItems2();
   }

   private static String method1615(Block var0) {
      return items2.computeIfAbsent(var0, var0x -> var0x.getTranslationKey().replace("block.minecraft.", ""));
   }

   private static String method1616(Item var0) {
      return items3.computeIfAbsent(var0, var0x -> var0x.getTranslationKey().replace("item.minecraft.", ""));
   }

   public void method1617(Block var1) {
      this.method1613(var1.getTranslationKey().replace("block.minecraft.", ""));
   }

   public void method1618(Item var1) {
      this.method1613(var1.getTranslationKey().replace("item.minecraft.", ""));
   }

   public void method1619(Block var1) {
      this.remove(var1.getTranslationKey().replace("block.minecraft.", ""));
   }

   public void method1620(Item var1) {
      this.remove(var1.getTranslationKey().replace("item.minecraft.", ""));
   }

   public boolean method1621(Block var1) {
      return this.contains(method1615(var1));
   }

   public boolean method1622(Item var1) {
      return this.contains(method1616(var1));
   }

   public void clear() {
      this.items.clear();
      this.version++;
   }

   @Override
   public void safe(JsonObject var1) {
      var1.add(String.valueOf(this.key), gson.toJsonTree(this.getItems()));
   }

   @Override
   public void load(JsonObject var1) {
      Type var2 = (new TypeToken<List<String>>() {}).getType();
      JsonElement var3 = var1.get(String.valueOf(this.key));
      if (var3 != null && var3.isJsonArray()) {
         List var4 = (List)gson.fromJson(var3, var2);
         this.method1612(var4);
      }
   }

   private static final class Data {
      private final int intValue;
      private final List<String> items;
      private final Set<String> items2;

      public int intValue() {
         return this.intValue;
      }

      public List<String> items() {
         return this.items;
      }

      public Set<String> items2() {
         return this.items2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.items2, other.items2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, items, items2);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "items=" + this.items + ", " + "items2=" + this.items2 + "]";
      }

      private Data(int var1, List<String> var2, Set<String> var3) {
         this.intValue = var1;
         this.items = var2;
         this.items2 = var3;
      }

      public int version() {
         return this.intValue;
      }

      public List<String> getItems() {
         return this.items;
      }

      public Set<String> getItems2() {
         return this.items2;
      }
   }
}

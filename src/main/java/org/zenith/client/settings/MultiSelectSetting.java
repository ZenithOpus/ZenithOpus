package org.zenith.client.settings;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import lombok.Generated;
import org.zenith.Zenith;

public class MultiSelectSetting extends Setting {
   private final List<MultiSelectSetting.Entry> items;
   private final List<List<String>> items2 = new ArrayList<>();
   private static final String text = ".known";

   public MultiSelectSetting(String var1) {
      this(var1, "");
   }

   public MultiSelectSetting(String var1, String var2) {
      super(var1, var2);
      this.items = new ArrayList<>();
   }

   public MultiSelectSetting(String var1, MultiSelectSetting.Entry... var2) {
      this(var1, "", var2);
   }

   public MultiSelectSetting(String var1, String var2, MultiSelectSetting.Entry... var3) {
      super(var1, var2);
      this.items = new ArrayList<>(Arrays.asList(var3));
      this.items.forEach(var1x -> var1x.multiSelectSetting = this);
   }

   public MultiSelectSetting method5220(String... var1) {
      this.items2.add(List.of(var1));
      MultiSelectSetting.Entry var2 = null;

      for (String var6 : var1) {
         MultiSelectSetting.Entry var7 = this.method5222(var6);
         if (var7 != null && var7.isEnabled()) {
            if (var2 == null) {
               var2 = var7;
            } else {
               var7.enabled = false;
            }
         }
      }

      return this;
   }

   private void method5221(MultiSelectSetting.Entry var1) {
      for (List var3 : this.items2) {
         if (var3.contains(var1.getKey())) {
            for (Object var5_o : (Iterable<?>)(Object)(var3)) {
               String var5 = (String)var5_o;
               if (!var5.equals(var1.getKey())) {
                  MultiSelectSetting.Entry var6 = this.method5222(var5);
                  if (var6 != null) {
                     var6.enabled = false;
                  }
               }
            }
         }
      }
   }

   public MultiSelectSetting.Entry method5222(String var1) {
      int var2 = 0;

      for (int var3 = this.items.size(); var2 < var3; var2++) {
         MultiSelectSetting.Entry var4 = this.items.get(var2);
         if (var4.getKey().equalsIgnoreCase(var1)) {
            return var4;
         }
      }

      return null;
   }

   public static MultiSelectSetting method5223(String var0, List<String> var1) {
      return method5224(var0, "", var1);
   }

   public static MultiSelectSetting method5224(String var0, String var1, List<String> var2) {
      MultiSelectSetting.Entry[] var3 = var2.stream().map(var0x -> new MultiSelectSetting.Entry(var0x, true)).toArray(MultiSelectSetting.Entry[]::new);
      return new MultiSelectSetting(var0, var1, var3);
   }

   public MultiSelectSetting.Entry method5225(int var1) {
      return this.items.get(var1);
   }

   public boolean method5226(String var1) {
      MultiSelectSetting.Entry var2 = this.method5222(var1);
      return var2 != null && var2.isEnabled();
   }

   public boolean method5227(int var1) {
      if (var1 >= this.getItems2().size()) {
         return false;
      } else {
         MultiSelectSetting.Entry var2 = this.method5225(var1);
         return var2 != null && var2.isEnabled();
      }
   }

   public List<MultiSelectSetting.Entry> method5228() {
      ArrayList<MultiSelectSetting.Entry> var1 = new ArrayList<>();
      int var2 = 0;

      for (int var3 = this.items.size(); var2 < var3; var2++) {
         MultiSelectSetting.Entry var4 = this.items.get(var2);
         if (var4.isEnabled()) {
            var1.add(var4);
         }
      }

      return var1;
   }

   @Override
   public void safe(JsonObject var1) {
      StringBuilder var2 = new StringBuilder();
      int var3 = 0;

      for (MultiSelectSetting.Entry var5 : this.getItems2()) {
         if (this.method5222(var5.getKey()).isEnabled()) {
            var2.append(var5.getKey()).append("\n");
         }

         var3++;
      }

      var1.addProperty(this.key, var2.toString());
      StringBuilder var7 = new StringBuilder();

      for (MultiSelectSetting.Entry var6 : this.getItems2()) {
         var7.append(var6.getKey()).append("\n");
      }

      var1.addProperty(this.key + ".known", var7.toString());
   }

   @Override
   public void load(JsonObject var1) {
      HashSet var2 = null;
      if (var1.has(this.key + ".known")) {
         var2 = new HashSet<>(List.of(var1.get(this.key + ".known").getAsString().split("\n")));
      }

      for (MultiSelectSetting.Entry var4 : this.getItems2()) {
         boolean var5 = var2 != null ? !var2.contains(var4.getKey()) : var4.flag2;
         var4.setEnabled(var5 && var4.flag);
      }

      String[] var9 = var1.get(String.valueOf(this.key)).getAsString().split("\n");

      for (String var7 : var9) {
         MultiSelectSetting.Entry var8 = this.method5222(var7);
         if (var8 != null) {
            this.method5222(var7).setEnabled(true);
         }
      }
   }

   public List<String> method5229() {
      ArrayList var1 = new ArrayList();
      int var2 = 0;

      for (int var3 = this.items.size(); var2 < var3; var2++) {
         MultiSelectSetting.Entry var4 = this.items.get(var2);
         if (var4.isEnabled()) {
            var1.add(var4.getKey());
         }
      }

      return Collections.unmodifiableList(var1);
   }

   @Generated
   public List<MultiSelectSetting.Entry> getItems2() {
      return this.items;
   }

   @Generated
   public List<List<String>> getItems22() {
      return this.items2;
   }

   public static class Entry {
      private boolean enabled;
      private final boolean flag;
      private boolean flag2;
      private MultiSelectSetting multiSelectSetting;
      private final String text;

      public String getName() {
         return Zenith.getInstance().getLocalization().translate(this.text);
      }

      public String getKey() {
         return this.text;
      }

      private Entry(String var1, boolean var2) {
         this.enabled = var2;
         this.flag = var2;
         this.text = var1;
      }

      public Entry(MultiSelectSetting var1, String var2, boolean var3) {
         this.enabled = var3;
         this.flag = var3;
         this.text = var2;
         this.multiSelectSetting = var1;
         var1.items.add(this);
      }

      public void setEnabled(boolean var1) {
         this.enabled = var1;
         if (var1 && this.multiSelectSetting != null) {
            this.multiSelectSetting.method5221(this);
         }
      }

      public MultiSelectSetting.Entry method5230() {
         this.flag2 = true;
         return this;
      }

      public static MultiSelectSetting.Entry method5231(String var0, boolean var1) {
         return new MultiSelectSetting.Entry(var0, var1);
      }

      public static MultiSelectSetting.Entry method5232(String var0) {
         return new MultiSelectSetting.Entry(var0, true);
      }

      public void toggle() {
         this.setEnabled(!this.enabled);
      }

      @Generated
      public boolean isEnabled() {
         return this.enabled;
      }

      @Generated
      public boolean isFlag() {
         return this.flag;
      }

      @Generated
      public boolean isFlag2() {
         return this.flag2;
      }

      @Generated
      public MultiSelectSetting getMultiSelectSetting() {
         return this.multiSelectSetting;
      }
   }
}

package org.zenith.client.settings;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import lombok.Generated;
import org.zenith.Zenith;

public class ModeSetting extends Setting {
   private final List<ModeSetting.Entry> items = new ArrayList<>();
   private ModeSetting.Entry value;

   public ModeSetting(String var1, String var2) {
      super(var1, var2);
   }

   public ModeSetting(String var1, String var2, String... var3) {
      super(var1, var2);

      for (String var7 : var3) {
         if (!var7.isEmpty()) {
            new ModeSetting.Entry(this, var7);
         }
      }

      if (!this.items.isEmpty()) {
         this.value = this.items.getFirst();
      }
   }

   public ModeSetting(String var1, Supplier<Boolean> var2, String... var3) {
      this(var1, "", var2, var3);
   }

   public ModeSetting(String var1, String var2, Supplier<Boolean> var3, String... var4) {
      super(var1, var2);

      for (String var8 : var4) {
         if (!var8.isEmpty()) {
            new ModeSetting.Entry(this, var8);
         }
      }

      if (!this.items.isEmpty()) {
         this.value = this.items.getFirst();
      }

      this.setVisible(var3);
   }

   public void set(String var1) {
      this.items.stream().filter(var1x -> var1x.getKey().equals(var1)).findFirst().ifPresent(var1x -> this.value = var1x);
   }

   public void method2100(ModeSetting.Entry var1) {
      this.items.remove(var1);
      if (this.value == var1 && !this.items.isEmpty()) {
         this.value = this.items.getFirst();
      }
   }

   public String get() {
      return this.value != null ? this.value.getKey() : "";
   }

   public boolean isSelected(int var1) {
      return this.items.get(var1).isSelected();
   }

   public boolean method2101(String var1) {
      return this.value.getKey().equals(var1);
   }

   public ModeSetting.Entry method2102() {
      var var1 = this.items.stream().filter(ModeSetting.Entry::isSelected).toList();
      return !var1.isEmpty() ? (ModeSetting.Entry)var1.get(new Random().nextInt(var1.size())) : null;
   }

   @Override
   public void safe(JsonObject var1) {
      var1.addProperty(String.valueOf(this.key), this.get());
   }

   @Override
   public void load(JsonObject var1) {
      this.set(var1.get(String.valueOf(this.key)).getAsString());
   }

   public int getIndex() {
      return this.items.indexOf(this.value);
   }

   @Generated
   public List<ModeSetting.Entry> getValues() {
      return this.items;
   }

   @Generated
   public ModeSetting.Entry getValue2() {
      return this.value;
   }

   @Generated
   public void setValue(ModeSetting.Entry var1) {
      this.value = var1;
   }

   public static class Entry {
      private final ModeSetting modeSetting;
      private final String text;
      private final String text2;

      public Entry(ModeSetting var1, String var2) {
         this.modeSetting = var1;
         this.text = var2;
         this.text2 = "";
         if (var1.items.isEmpty()) {
            this.method2103();
         }

         var1.items.add(this);
      }

      public Entry(ModeSetting var1, String var2, String var3) {
         this.modeSetting = var1;
         this.text = var2;
         this.text2 = var3;
         if (var1.items.isEmpty()) {
            this.method2103();
         }

         var1.items.add(this);
      }

      public String getName() {
         return Zenith.getInstance().getLocalization().translate(this.text);
      }

      public ModeSetting.Entry method2103() {
         this.modeSetting.setValue(this);
         return this;
      }

      public boolean isSelected() {
         return this.modeSetting.getValue2() == this;
      }

      @Override
      public String toString() {
         return this.text;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (var1 != null && var1.getClass() == this.getClass()) {
            ModeSetting.Entry var2 = (ModeSetting.Entry)var1;
            return Objects.equals(this.modeSetting, var2.modeSetting) && Objects.equals(this.text, var2.text) && Objects.equals(this.text2, var2.text2);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.modeSetting, this.text, this.text2);
      }

      @Generated
      public ModeSetting getModeSetting() {
         return this.modeSetting;
      }

      @Generated
      public String getKey() {
         return this.text;
      }

      @Generated
      public String getDescription() {
         return this.text2;
      }
   }
}

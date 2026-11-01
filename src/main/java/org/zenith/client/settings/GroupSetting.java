package org.zenith.client.settings;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.Generated;

public class GroupSetting extends Setting {
   private final List<Setting> items;
   private boolean active;

   public GroupSetting(String var1, Setting... var2) {
      this(var1, "", () -> true, var2);
   }

   public GroupSetting(String var1, String var2, Setting... var3) {
      this(var1, var2, () -> true, var3);
   }

   public GroupSetting(String var1, Supplier<Boolean> var2, Setting... var3) {
      this(var1, "", var2, var3);
   }

   public GroupSetting(String var1, String var2, Supplier<Boolean> var3, Setting... var4) {
      super(var1, var2);
      this.setVisible(var3);
      this.items = Lists.newArrayList(Arrays.asList(var4));
      this.active = false;
   }

   public <T extends Setting> T method2786(int var1) {
      return (T)this.items.get(var1);
   }

   public void toggle() {
      this.active = !this.active;
   }

   private void method2787(Consumer<Setting> var1) {
      this.items.forEach(var1);
   }

   @Override
   public void safe(JsonObject var1) {
      this.method2787(var1x -> var1x.safe(var1));
   }

   @Override
   public void load(JsonObject var1) {
      this.method2787(var1x -> {
         if (var1.has(var1x.getKey())) {
            var1x.load(var1);
         }
      });
   }

   @Generated
   public List<Setting> getSettings() {
      return this.items;
   }

   @Generated
   public boolean isActive() {
      return this.active;
   }
}

package org.zenith.client.settings;

import com.google.gson.JsonObject;
import java.util.function.Supplier;
import lombok.Generated;
import org.zenith.utility.misc.KeyName;

public class BindSetting extends Setting {
   private String nameKey;
   private int keyCode;
   private int modifiers;

   public void setKeyCode(int var1) {
      this.setBind(var1, 0);
   }

   public void setBind(int var1, int var2) {
      this.keyCode = var1;
      this.modifiers = KeyName.method6698(var2);
      this.nameKey = KeyName.method6702(this.keyCode, this.modifiers);
   }

   public BindSetting(String var1, Supplier<Boolean> var2) {
      this(var1, "", var2);
   }

   public BindSetting(String var1, String var2, Supplier<Boolean> var3) {
      super(var1, var2);
      this.setVisible(var3);
      this.keyCode = -1;
      this.nameKey = KeyName.method6701(this.keyCode);
   }

   public BindSetting(String var1, int var2, Supplier<Boolean> var3) {
      this(var1, "", var2, var3);
   }

   public BindSetting(String var1, String var2, int var3, Supplier<Boolean> var4) {
      super(var1, var2);
      this.setVisible(var4);
      this.keyCode = var3;
      this.nameKey = KeyName.method6701(var3);
   }

   public BindSetting(String var1, int var2) {
      this(var1, "", var2);
   }

   public BindSetting(String var1, String var2, int var3) {
      super(var1, var2);
      this.keyCode = var3;
      this.nameKey = KeyName.method6701(var3);
   }

   public BindSetting(String var1) {
      this(var1, "");
   }

   public BindSetting(String var1, String var2) {
      super(var1, var2);
      this.keyCode = -1;
      this.nameKey = "";
   }

   @Override
   public void safe(JsonObject var1) {
      var1.addProperty(String.valueOf(this.key), this.getKeyCode());
      var1.addProperty(this.key + "_mods", this.modifiers);
   }

   @Override
   public void load(JsonObject var1) {
      int var2 = var1.has(this.key + "_mods") ? var1.get(this.key + "_mods").getAsInt() : 0;
      this.setBind(var1.get(String.valueOf(this.key)).getAsInt(), var2);
   }

   @Generated
   public String getNameKey() {
      return this.nameKey;
   }

   @Generated
   public int getKeyCode() {
      return this.keyCode;
   }

   @Generated
   public int getModifiers() {
      return this.modifiers;
   }
}

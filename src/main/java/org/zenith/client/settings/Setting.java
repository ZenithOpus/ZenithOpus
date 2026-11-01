package org.zenith.client.settings;

import com.google.gson.JsonObject;
import java.util.function.Supplier;
import lombok.Generated;
import org.zenith.Zenith;

public abstract class Setting {
   public final String key;
   public final String description;
   public Supplier<Boolean> visible;
   public Supplier<String> titleKey;
   public Supplier<String> tipKey;

   public Setting(String var1) {
      this(var1, "");
   }

   public Setting(String var1, String var2) {
      this.key = var1;
      this.description = var2 != null ? var2 : "";
      this.setVisible(() -> true);
   }

   public String getName() {
      return Zenith.getInstance().getLocalization().translate(this.titleKey == null ? this.key : this.titleKey.get());
   }

   public String getDescription() {
      return Zenith.getInstance().getLocalization().translate(this.tipKey == null ? this.description : this.tipKey.get());
   }

   public abstract void safe(JsonObject var1);

   public abstract void load(JsonObject var1);

   public boolean isVisible() {
      return this.visible.get();
   }

   @Generated
   public String getKey() {
      return this.key;
   }

   @Generated
   public Supplier<Boolean> getVisible() {
      return this.visible;
   }

   @Generated
   public Supplier<String> getTitleKey() {
      return this.titleKey;
   }

   @Generated
   public Supplier<String> getTipKey() {
      return this.tipKey;
   }

   @Generated
   public void setVisible(Supplier<Boolean> var1) {
      this.visible = var1;
   }

   @Generated
   public void setTitleKey(Supplier<String> var1) {
      this.titleKey = var1;
   }

   @Generated
   public void setTipKey(Supplier<String> var1) {
      this.tipKey = var1;
   }
}

package org.zenith.client.settings;

import com.google.gson.JsonObject;
import lombok.Generated;

public class ButtonSetting extends Setting {
   private String text;
   private Runnable runnable;

   public ButtonSetting(String var1, Runnable var2) {
      this(var1, "t", var2);
   }

   public ButtonSetting(String var1, String var2, Runnable var3) {
      this(var1, var2, "", var3);
   }

   public ButtonSetting(String var1, String var2, String var3, Runnable var4) {
      super(var1, var3);
      this.runnable = var4;
      this.text = var2;
   }

   public void toggle() {
      this.runnable.run();
   }

   @Override
   public void safe(JsonObject var1) {
   }

   @Override
   public void load(JsonObject var1) {
   }

   @Generated
   public String getIcon() {
      return this.text;
   }

   @Generated
   public Runnable getRunnable() {
      return this.runnable;
   }

   @Generated
   public void setText(String var1) {
      this.text = var1;
   }

   @Generated
   public void setRunnable(Runnable var1) {
      this.runnable = var1;
   }
}

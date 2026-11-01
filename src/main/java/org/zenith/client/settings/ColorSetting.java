package org.zenith.client.settings;

import com.google.gson.JsonObject;
import java.util.function.Supplier;
import lombok.Generated;
import org.zenith.ZenithColor;

public class ColorSetting extends Setting {
   private ZenithColor zenithColor;
   private final ColorSetting.Handler handler;

   public ColorSetting(String var1, ZenithColor var2, Supplier<Boolean> var3, ColorSetting.Handler var4) {
      this(var1, "", var2, var4);
      this.setVisible(var3);
   }

   public ColorSetting(String var1, ZenithColor var2, ColorSetting.Handler var3) {
      this(var1, "", var2, var3);
   }

   public ColorSetting(String var1, String var2, ZenithColor var3, ColorSetting.Handler var4) {
      super(var1, var2);
      if (var3 == null) {
         throw new RuntimeException(var1 + " color is null");
      } else {
         this.zenithColor = var3;
         this.setZenithColor(var3);
         this.handler = var4;
      }
   }

   public ColorSetting(String var1, ZenithColor var2) {
      this(var1, "", var2, () -> var2);
   }

   public ColorSetting(String var1, String var2, ZenithColor var3) {
      this(var1, var2, var3, () -> var3);
   }

   public ColorSetting(String var1, ColorSetting.Handler var2) {
      this(var1, "", var2.getDefaultColor(), var2);
   }

   public ColorSetting(String var1, ZenithColor var2, Supplier<Boolean> var3) {
      this(var1, "", var2, () -> var2);
      this.setVisible(var3);
   }

   public ColorSetting(String var1, String var2, ZenithColor var3, Supplier<Boolean> var4) {
      this(var1, var2, var3, () -> var3);
      this.setVisible(var4);
   }

   public int method1445() {
      return this.zenithColor.method7287();
   }

   public void method1446(int var1) {
      this.zenithColor = new ZenithColor(var1);
   }

   public void setZenithColor(ZenithColor var1) {
      this.zenithColor = var1;
   }

   public void update() {
   }

   public void reset() {
      this.zenithColor = this.handler.getDefaultColor();
   }

   public ZenithColor method1447(float var1) {
      return this.zenithColor.method7294(var1);
   }

   @Override
   public void safe(JsonObject var1) {
      var1.addProperty(String.valueOf(this.key), this.method1445());
   }

   @Override
   public void load(JsonObject var1) {
      this.method1446(var1.get(String.valueOf(this.key)).getAsInt());
   }

   @Generated
   public ZenithColor getZenithColor() {
      return this.zenithColor;
   }

   public interface Handler {
      ZenithColor getDefaultColor();
   }
}

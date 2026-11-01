package org.zenith.client.settings;

import com.google.gson.JsonObject;
import java.util.function.Supplier;
import lombok.Generated;
import org.zenith.Zenith;

public class SliderSetting extends Setting {
   private final String text;
   private final String text2;
   private float floatValue;
   private final float min;
   private final float max;
   private final float floatValue2;
   private SliderSetting.Handler handler;
   private SliderSetting.Handler2 handler2;

   @Override
   public String getDescription() {
      return Zenith.getInstance().getLocalization().translate(this.text2);
   }

   public SliderSetting(
      String var1, float var2, float var3, float var4, float var5, String var6, String var7, Supplier<Boolean> var8, SliderSetting.Handler2 var9
   ) {
      super(var1, var6);
      this.min = var3;
      this.max = var4;
      this.floatValue = var2;
      this.floatValue2 = var5;
      this.text2 = var6 != null ? var6 : "";
      this.text = var7 != null ? var7 : "";
      this.handler2 = var9;
      if (var8 != null) {
         this.setVisible(var8);
      }
   }

   public SliderSetting(String var1, float var2, float var3, float var4, float var5) {
      this(var1, var2, var3, var4, var5, "", "%", null, null);
   }

   public SliderSetting(String var1, float var2, float var3, float var4, float var5, String var6) {
      this(var1, var2, var3, var4, var5, var6, "%", null, null);
   }

   public SliderSetting(String var1, float var2, float var3, float var4, float var5, String var6, String var7) {
      this(var1, var2, var3, var4, var5, var6, var7, null, null);
   }

   public SliderSetting(String var1, float var2, float var3, float var4, float var5, Supplier<Boolean> var6) {
      this(var1, var2, var3, var4, var5, "", "%", var6, null);
   }

   public SliderSetting(String var1, float var2, float var3, float var4, float var5, SliderSetting.Handler2 var6) {
      this(var1, var2, var3, var4, var5, "", "%", null, var6);
   }

   public SliderSetting(String var1, float var2, float var3, float var4, float var5, Supplier<Boolean> var6, SliderSetting.Handler2 var7) {
      this(var1, var2, var3, var4, var5, "", "%", var6, var7);
   }

   public SliderSetting(String var1, float var2, float var3, float var4, float var5, String var6, String var7, Supplier<Boolean> var8) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, null);
   }

   public SliderSetting(String var1, float var2, float var3, float var4, float var5, String var6, Supplier<Boolean> var7, SliderSetting.Handler2 var8) {
      this(var1, var2, var3, var4, var5, "", var6, var7, var8);
   }

   public SliderSetting method2399(SliderSetting.Handler var1) {
      this.handler = var1;
      return this;
   }

   public void method2400(float var1) {
      float var2 = this.floatValue;
      this.floatValue = var1;
      if (this.handler2 != null) {
         this.handler2.apply(var2, var1);
      }
   }

   @Override
   public void safe(JsonObject var1) {
      var1.addProperty(String.valueOf(this.key), this.getValue3());
   }

   @Override
   public void load(JsonObject var1) {
      this.method2400(var1.get(String.valueOf(this.key)).getAsFloat());
   }

   @Generated
   public String getSuffix() {
      return this.text;
   }

   @Generated
   public float getValue3() {
      return this.floatValue;
   }

   @Generated
   public float getMin() {
      return this.min;
   }

   @Generated
   public float getMax() {
      return this.max;
   }

   @Generated
   public float getFloatValue2() {
      return this.floatValue2;
   }

   @Generated
   public SliderSetting.Handler getHandler() {
      return this.handler;
   }

   @Generated
   public SliderSetting.Handler2 getHandler2() {
      return this.handler2;
   }

   public interface Handler {
      String text(float var1);
   }

   public interface Handler2 {
      void apply(float var1, float var2);
   }
}

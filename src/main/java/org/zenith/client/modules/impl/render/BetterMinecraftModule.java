package org.zenith.client.modules.impl.render;

import com.google.gson.JsonObject;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.settings.MultiSelectSetting;

@ModuleInfo(
   name = "BetterMinecraft",
   description = "module.betterminecraft.description",
   category = ModuleCategory.RENDER
)
public final class BetterMinecraftModule extends Module {
   public static final BetterMinecraftModule INSTANCE = new BetterMinecraftModule();
   public static final long CONST_longValue = 130L;
   public static final long CONST_longValue2 = 110L;
   public static final float CONST_floatValue = 0.86F;
   private final MultiSelectSetting animate = new MultiSelectSetting("module.betterMinecraft.animate");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.animate, "module.betterMinecraft.animate.storages", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.animate, "module.betterMinecraft.animate.inventory", true);

   private BetterMinecraftModule() {
      this.setEnabled(true);
   }

   @Override
   public void load(JsonObject var1) {
      super.load(var1);
      this.setToggled(true);
   }

   @Override
   public JsonObject save() {
      JsonObject var1 = super.save();
      var1.addProperty("enabled", true);
      return var1;
   }

   public boolean method2010() {
      return this.isEnabled() && this.entry.isEnabled();
   }

   public boolean method2011() {
      return this.isEnabled() && this.entry2.isEnabled();
   }

   public float method2012(long var1) {
      float var3 = this.method2015((float)(System.currentTimeMillis() - var1) / 130.0F);
      return 0.86F + 0.13999999F * this.method2016(var3);
   }

   public float method2013(long var1, float var3) {
      float var4 = this.method2015((float)(System.currentTimeMillis() - var1) / 110.0F);
      return var3 * (1.0F - this.method2016(var4));
   }

   public boolean method2014(long var1) {
      return System.currentTimeMillis() - var1 >= 110L;
   }

   private float method2015(float var1) {
      return Math.clamp(var1, 0.0F, 1.0F);
   }

   private float method2016(float var1) {
      return var1 < 0.5F ? 2.0F * var1 * var1 : 1.0F - (float)Math.pow(-2.0F * var1 + 2.0F, 2.0) / 2.0F;
   }
}

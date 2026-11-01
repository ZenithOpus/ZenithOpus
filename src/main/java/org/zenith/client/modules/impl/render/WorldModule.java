package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.BackgroundRenderEvent;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.render.display.base.MenuDrawContext;

@ModuleInfo(
   name = "World",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.worldvisuals.description"
)
public final class WorldModule extends Module {
   public static final WorldModule INSTANCE = new WorldModule();
   private static final int CONST_intValue = 0;
   private static final int CONST_intValue2 = 1;
   private static final int CONST_intValue3 = 2;
   private static final int CONST_intValue4 = 3;
   private final MultiSelectSetting parts = new MultiSelectSetting(
      "module.worldVisuals.parts",
      "module.worldVisuals.parts.desc",
      MultiSelectSetting.Entry.method5231("module.worldVisuals.part.light", true),
      MultiSelectSetting.Entry.method5231("module.worldVisuals.part.saturation", true),
      MultiSelectSetting.Entry.method5231("module.worldVisuals.part.time", false),
      MultiSelectSetting.Entry.method5231("module.worldVisuals.part.fog", false)
   );
   private final SliderSetting brightness = new SliderSetting(
      "module.worldVisuals.brightness", 1.0F, 0.0F, 1.0F, 0.05F, "module.worldVisuals.brightness.desc", "%", () -> this.parts.method5227(0), null
   );
   private final SliderSetting saturation = new SliderSetting(
      "module.worldVisuals.saturation", 1.0F, 0.0F, 3.0F, 0.05F, "module.worldVisuals.saturation.desc", "x", () -> this.parts.method5227(1), null
   );
   private final SliderSetting time = new SliderSetting(
      "module.worldVisuals.time", 12.0F, 0.0F, 24.0F, 1.0F, "module.worldVisuals.time.desc", "h", () -> this.parts.method5227(2), null
   );
   private final ColorSetting fogColor = new ColorSetting(
      "module.worldVisuals.fogColor",
      "module.worldVisuals.fogColor.desc",
      Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor(),
      () -> this.parts.method5227(3)
   );
   private final SliderSetting fogDistance = new SliderSetting(
      "module.worldVisuals.fogDistance", 80.0F, 10.0F, 255.0F, 5.0F, "module.worldVisuals.fogDistance.desc", "b", () -> this.parts.method5227(3), null
   );

   private WorldModule() {
   }

   @Override
   public ModulePreviewStage.Kind previewKind() {
      return ModulePreviewStage.Kind.WORLD_VISUALS;
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(this.parts, this.brightness, this.saturation, this.time, this.fogColor, this.fogDistance);
   }

   public float method5644() {
      return this.isEnabled() && this.parts.method5227(0) ? this.brightness.getValue3() : -1.0F;
   }

   public float method5645() {
      return this.isEnabled() && this.parts.method5227(1) ? this.saturation.getValue3() : 1.0F;
   }

   public float method5646() {
      return 1.0F;
   }

   public float method5647() {
      return this.isEnabled() && this.parts.method5227(2) ? this.time.getValue3() : -1.0F;
   }

   public boolean method5648() {
      return this.isEnabled() && this.parts.method5227(3);
   }

   @EventTarget
   public void method5649(BackgroundRenderEvent var1) {
      if (this.method5648()) {
         var1.setFloatValue(this.fogDistance.getValue3());
         var1.setColor(this.fogColor.method1445());
         var1.setCancelled(true);
      }
   }

   public void method5650(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      if (this.method5648()) {
         float var7 = 1.0F - Math.clamp((this.fogDistance.getValue3() - 10.0F) / 245.0F, 0.0F, 1.0F);
         ZenithColor var8 = this.fogColor.getZenithColor().method7294(var6 * (0.25F + 0.6F * var7));
         var1.drawRect(var2, var3, var4, var5, var8);
      }
   }

   public float method5651() {
      return this.method5645();
   }

   public float method5652() {
      float var1 = this.method5644();
      return var1 < 0.0F ? 1.0F : 0.7F + var1 * 0.55F;
   }
}

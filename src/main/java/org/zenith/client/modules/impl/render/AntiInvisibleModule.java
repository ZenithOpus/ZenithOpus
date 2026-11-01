package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import lombok.Generated;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.LivingRenderColorEvent;
import org.zenith.client.settings.ColorSetting;

@ModuleInfo(
   name = "AntiInvisible",
   category = ModuleCategory.RENDER,
   description = "module.antiinvisible.description"
)
public final class AntiInvisibleModule extends Module {
   public static final AntiInvisibleModule INSTANCE = new AntiInvisibleModule();
   private final ColorSetting colorSetting = new ColorSetting(
      "module.antiInvisible.colorSetting", "module.antiInvisible.colorSetting.desc", ZenithColor.INSTANCE.method7294(0.5F)
   );

   private AntiInvisibleModule() {
   }

   @EventTarget
   public void method5168(LivingRenderColorEvent var1) {
      var1.setColor(this.colorSetting.getZenithColor().method7287());
      var1.cancel();
   }

   @Generated
   public ColorSetting getColorSetting3() {
      return this.colorSetting;
   }
}

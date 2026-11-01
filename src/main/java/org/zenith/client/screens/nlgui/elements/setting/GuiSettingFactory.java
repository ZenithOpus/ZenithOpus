package org.zenith.client.screens.nlgui.elements.setting;

import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.BlockListSetting;

public final class GuiSettingFactory {
   private GuiSettingFactory() {
   }

   public static GuiSetting<?> create(Setting var0, float var1) {
      if (var0 instanceof SliderSetting var11) {
         return new GuiNumberSetting(var11, var1);
      } else if (var0 instanceof ModeSetting var10) {
         return new GuiModeSetting(var10, var1);
      } else if (var0 instanceof MultiSelectSetting var9) {
         return new GuiMultiBooleanSetting(var9, var1);
      } else if (var0 instanceof BooleanSetting var8) {
         return new GuiBooleanSetting(var8, var1);
      } else if (var0 instanceof ColorSetting var7) {
         return new GuiColorSetting(var7, var1);
      } else if (var0 instanceof ButtonSetting var6) {
         return new GuiButtonSetting(var6, var1);
      } else if (var0 instanceof BlockListSetting var5) {
         return new GuiItemSelectSetting(var5, var1);
      } else if (var0 instanceof BindSetting var4) {
         return new GuiKeySetting(var4, var1);
      } else if (var0 instanceof TextSetting var3) {
         return new GuiStringSetting(var3, var1);
      } else {
         return var0 instanceof GroupSetting var2 ? new GuiWindowSetting(var2, var1) : null;
      }
   }
}

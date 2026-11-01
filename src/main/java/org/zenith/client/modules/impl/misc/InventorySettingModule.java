package org.zenith.client.modules.impl.misc;

import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;

@ModuleInfo(
   name = "InventorySetting",
   description = "module.inventorysetting.description",
   category = ModuleCategory.MISC
)
public final class InventorySettingModule extends Module {
   private static final String text = "ReallyWorld";
   private static final String text2 = "FunTime";
   private static final String text3 = "HolyWorld";
   private static final String text4 = "module.inventorySetting.mode.custom";
   private final ModeSetting mode = new ModeSetting(
      "module.inventorySetting.mode", "module.inventorySetting.mode.desc", "ReallyWorld", "FunTime", "HolyWorld", "module.inventorySetting.mode.custom"
   );
   private final BooleanSetting stoping = new BooleanSetting("module.inventorySetting.stoping", "module.inventorySetting.stoping.desc", true);
   private final BooleanSetting delayMoveItem = new BooleanSetting("module.inventorySetting.delayMoveItem", "module.inventorySetting.delayMoveItem.desc", true);
   private final BooleanSetting updateSlot = new BooleanSetting("module.inventorySetting.updateSlot", "module.inventorySetting.updateSlot.desc", false);
   public static final InventorySettingModule INSTANCE = new InventorySettingModule();

   private InventorySettingModule() {
      this.stoping.setVisible(() -> this.mode.method2101("module.inventorySetting.mode.custom"));
      this.delayMoveItem.setVisible(() -> this.mode.method2101("module.inventorySetting.mode.custom"));
      this.updateSlot.setVisible(() -> this.mode.method2101("module.inventorySetting.mode.custom"));
   }

   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   public boolean method2396() {
      if (this.mode.method2101("ReallyWorld")) {
         return true;
      } else {
         return !this.mode.method2101("FunTime") && !this.mode.method2101("HolyWorld") ? !this.delayMoveItem.isEnabled() : false;
      }
   }

   public boolean method2397() {
      if (this.mode.method2101("ReallyWorld") || this.mode.method2101("FunTime")) {
         return true;
      } else {
         return this.mode.method2101("HolyWorld") ? false : this.stoping.isEnabled();
      }
   }

   public boolean method2398() {
      return this.mode.method2101("module.inventorySetting.mode.custom") && this.updateSlot.isEnabled();
   }
}

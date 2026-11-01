package org.zenith.client.modules.impl.render;

import net.minecraft.item.ItemStack;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.utility.game.Util14;
import org.zenith.utility.misc.Util10;

@ModuleInfo(
   name = "View Armor Durability",
   category = ModuleCategory.RENDER,
   description = "module.viewarmordurability.description"
)
public class ViewArmorDurabilityModule extends Module {
   public static final ViewArmorDurabilityModule INSTANCE = new ViewArmorDurabilityModule();
   private final BooleanSetting naSebe = new BooleanSetting("module.viewArmorDurability.naSebe", true);
   private final BooleanSetting naEnemies = new BooleanSetting("module.viewArmorDurability.naEnemies", true);

   private ViewArmorDurabilityModule() {
   }

   public boolean method7550(ItemStack var1, boolean var2) {
      if (var1 == null || var1.isEmpty()) {
         return false;
      } else if (!Util10.method2181(var1)) {
         return false;
      } else if (var2 && !this.naSebe.isEnabled()) {
         return false;
      } else {
         return !var2 && Util14.method2640() ? false : var2 || this.naEnemies.isEnabled();
      }
   }

   public int method7551(ItemStack var1) {
      return var1.getItemBarColor();
   }
}

package org.zenith.base.bot.modules;

import java.util.List;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.client.settings.Setting;

@ModuleInfo(
   name = "BotModuleUiAdapter",
   category = ModuleCategory.PLAYER,
   description = "",
   method894 = true
)
public final class BotModuleUiAdapter extends Module {
   private final String botName;
   private final BotModule module;

   public BotModuleUiAdapter(String var1, BotModule var2) {
      this.botName = var1;
      this.module = var2;
      this.setName(var2.getName());
   }

   @Override
   public boolean isEnabled() {
      return this.module.isEnabled();
   }

   @Override
   public boolean isEnabledRaw() {
      return this.module.isEnabled();
   }

   @Override
   public void toggle() {
      HeadlessBots.setModuleEnabled(this.botName, this.module.getName(), !this.module.isEnabled());
   }

   @Override
   public void setToggled(boolean var1) {
      if (var1 != this.module.isEnabled()) {
         this.toggle();
      }
   }

   @Override
   public List<Setting> getSettings() {
      return this.module.getUiSettings();
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof BotModuleUiAdapter var2 && var2.module == this.module;
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this.module);
   }
}

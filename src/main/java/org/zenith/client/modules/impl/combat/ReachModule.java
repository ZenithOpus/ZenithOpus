package org.zenith.client.modules.impl.combat;

import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.ModuleHookDefinitions;

@ModuleInfo(
   name = "Reach",
   category = ModuleCategory.COMBAT,
   description = "module.reach.description"
)
public final class ReachModule extends Module {
   public static final ReachModule INSTANCE = new ReachModule();
   private Runnable runnable;
   private Runnable runnable2;
   public final MultiSelectSetting mods = new MultiSelectSetting("module.reach.mods", "module.reach.mods.desc");
   public final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.mods, "module.reach.defoult", true);
   public final SliderSetting reach = new SliderSetting(
      "module.reach.reach", 3.0F, 3.0F, 6.0F, 0.05F, "module.reach.reach.desc", "b", this.entry::isEnabled, null
   );
   public final SliderSetting reachBlock = new SliderSetting(
      "module.reach.reachBlock", 3.0F, 3.0F, 20.0F, 0.05F, "module.reach.reachBlock.desc", "b", this.entry::isEnabled, null
   );

   private ReachModule() {
   }

   @Override
   public void onEnable() {
      if (this.runnable == null) {
         this.runnable = ModuleHookDefinitions.class12.method1646(0, var1 -> {
            if (this.isEnabled() && this.entry.isEnabled() && this.reach.getValue3() > var1.value) {
               var1.value = this.reach.getValue3();
            }
         });
      }

      if (this.runnable2 == null) {
         this.runnable2 = ModuleHookDefinitions.class122.method1646(0, var1 -> {
            if (this.isEnabled() && this.entry.isEnabled() && this.reachBlock.getValue3() > var1.value) {
               var1.value = this.reachBlock.getValue3();
            }
         });
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (this.runnable != null) {
         this.runnable.run();
         this.runnable = null;
      }

      if (this.runnable2 != null) {
         this.runnable2.run();
         this.runnable2 = null;
      }

      super.onDisable();
   }
}

package org.zenith.client.modules.impl.combat;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.screens.esp.SectionSettings;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.ModuleHeaderSetting;

@ModuleInfo(
   name = "AimBot",
   category = ModuleCategory.COMBAT,
   description = "module.aimbot.description"
)
public final class AimBotModule extends Module {
   public static final AimBotModule INSTANCE = new AimBotModule();
   private final ButtonSetting bow = method1003("module.aimBot.bow", BowAimBotModule.INSTANCE);
   private final ButtonSetting trident = method1003("module.aimBot.trident", TridentAimbotModule.INSTANCE);
   private final List<Module> items = new ArrayList<>();

   private AimBotModule() {
      this.setEnabled(true);
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(this.bow, this.trident);
   }

   @Override
   public void onEnable() {
      super.onEnable();

      for (Module var2 : this.items) {
         if (!var2.isEnabled()) {
            var2.toggle();
         }
      }

      this.items.clear();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.items.clear();

      for (Module var2 : method1002()) {
         if (var2.isEnabled()) {
            this.items.add(var2);
            var2.toggle();
         }
      }
   }

   private static List<Module> method1002() {
      return List.of(BowAimBotModule.INSTANCE, TridentAimbotModule.INSTANCE);
   }

   @Override
   public List<Module> getSurfacedModules() {
      return method1002();
   }

   @Override
   public JsonObject save() {
      JsonObject var1 = super.save();
      var1.addProperty("enabled", true);
      return var1;
   }

   private static ButtonSetting method1003(String var0, Module var1) {
      var var2 = SectionSettings.of(var1);
      return new ModuleHeaderSetting(var0, var1, () -> Zenith.getInstance().getNLMenuScreen().getInspectorPanel().openSection(var1.getName(), var2, null, var1));
   }
}

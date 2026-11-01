package org.zenith.client.modules.impl.render;

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
   name = "Hands",
   category = ModuleCategory.RENDER,
   description = "module.hands.description"
)
public final class HandsModule extends Module {
   public static final HandsModule INSTANCE = new HandsModule();
   private final ButtonSetting model = method2256("module.hands.model", ViewModelModule.INSTANCE);
   private final ButtonSetting swing = method2256("module.hands.swing", SwingAnimationModule.INSTANCE);
   private final ButtonSetting shader = method2256("module.hands.shader", ShaderHandModule.INSTANCE);
   private final List<Module> items = new ArrayList<>();

   private HandsModule() {
      this.setEnabled(true);
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(this.model, this.swing, this.shader);
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

      for (Module var2 : method2255()) {
         if (var2.isEnabled()) {
            this.items.add(var2);
            var2.toggle();
         }
      }
   }

   private static List<Module> method2255() {
      return List.of(ViewModelModule.INSTANCE, SwingAnimationModule.INSTANCE, ShaderHandModule.INSTANCE);
   }

   @Override
   public List<Module> getSurfacedModules() {
      return method2255();
   }

   @Override
   public JsonObject save() {
      JsonObject var1 = super.save();
      var1.addProperty("enabled", true);
      return var1;
   }

   private static ButtonSetting method2256(String var0, Module var1) {
      var var2 = SectionSettings.of(var1);
      return new ModuleHeaderSetting(var0, var1, () -> Zenith.getInstance().getNLMenuScreen().getInspectorPanel().openSection(var1.getName(), var2, null, var1));
   }
}

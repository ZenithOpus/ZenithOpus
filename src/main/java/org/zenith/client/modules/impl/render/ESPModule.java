package org.zenith.client.modules.impl.render;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.screens.esp.SectionSettings;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.ModuleHeaderSetting;

@ModuleInfo(
   name = "ESP",
   category = ModuleCategory.RENDER,
   description = "module.esp.description"
)
public final class ESPModule extends Module {
   public static final ESPModule INSTANCE = new ESPModule();
   private final ButtonSetting entities = method7012("module.esp.entities", EntityESPModule.INSTANCE, ModulePreviewStage.Kind.ENTITY);
   private final ButtonSetting target = method7012("module.esp.target", TargetESPModule.INSTANCE, ModulePreviewStage.Kind.TARGET);
   private final ButtonSetting chams = method7012("module.esp.chams", ChamsModule.INSTANCE, ModulePreviewStage.Kind.CHAMS);
   private final ButtonSetting shader = method7012("module.esp.shader", ShaderESPModule.INSTANCE, ModulePreviewStage.Kind.SHADER);
   private final ButtonSetting blocks = method7012("module.esp.blocks", BlockESPModule.INSTANCE, null);
   private final ButtonSetting fireworks = method7012("module.esp.fireworks", FireWorkESPModule.INSTANCE, ModulePreviewStage.Kind.FIREWORK);
   private final List<Module> items = new ArrayList<>();

   private ESPModule() {
      this.setEnabled(true);
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(this.entities, this.target, this.chams, this.shader, this.blocks, this.fireworks);
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

      for (Module var2 : method7011()) {
         if (var2.isEnabled()) {
            this.items.add(var2);
            var2.toggle();
         }
      }
   }

   private static List<Module> method7011() {
      return List.of(
         EntityESPModule.INSTANCE,
         TargetESPModule.INSTANCE,
         ChamsModule.INSTANCE,
         ShaderESPModule.INSTANCE,
         BlockESPModule.INSTANCE,
         FireWorkESPModule.INSTANCE
      );
   }

   @Override
   public List<Module> getSurfacedModules() {
      return method7011();
   }

   @Override
   public JsonObject save() {
      JsonObject var1 = super.save();
      var1.addProperty("enabled", true);
      return var1;
   }

   private static ButtonSetting method7012(String var0, Module var1, ModulePreviewStage.Kind var2) {
      var var3 = SectionSettings.of(var1);
      return new ModuleHeaderSetting(var0, var1, () -> Zenith.getInstance().getNLMenuScreen().getInspectorPanel().openSection(var1.getName(), var3, var2, var1));
   }
}

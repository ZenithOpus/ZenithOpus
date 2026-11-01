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
   name = "Particles",
   category = ModuleCategory.RENDER,
   description = "module.particles.description"
)
public final class ParticlesModule extends Module {
   public static final ParticlesModule INSTANCE = new ParticlesModule();
   private final ButtonSetting ambient = method7401("module.particles.ambient", AmbientParticlesModule.INSTANCE, ModulePreviewStage.Kind.PARTICLES_AMBIENT);
   private final ButtonSetting totem = method7401("module.particles.totem", TotemParticlesModule.INSTANCE, ModulePreviewStage.Kind.PARTICLES_TOTEM);
   private final ButtonSetting hit = method7401("module.particles.hit", HitParticlesModule.INSTANCE, ModulePreviewStage.Kind.PARTICLES_HIT);
   private final List<Module> items = new ArrayList<>();

   private ParticlesModule() {
      this.setEnabled(true);
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(this.ambient, this.totem, this.hit);
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

      for (Module var2 : method7400()) {
         if (var2.isEnabled()) {
            this.items.add(var2);
            var2.toggle();
         }
      }
   }

   private static List<Module> method7400() {
      return List.of(AmbientParticlesModule.INSTANCE, TotemParticlesModule.INSTANCE, HitParticlesModule.INSTANCE);
   }

   @Override
   public List<Module> getSurfacedModules() {
      return method7400();
   }

   @Override
   public JsonObject save() {
      JsonObject var1 = super.save();
      var1.addProperty("enabled", true);
      return var1;
   }

   private static ButtonSetting method7401(String var0, Module var1, ModulePreviewStage.Kind var2) {
      var var3 = SectionSettings.of(var1);
      return new ModuleHeaderSetting(var0, var1, () -> Zenith.getInstance().getNLMenuScreen().getInspectorPanel().openSection(var1.getName(), var3, var2, var1));
   }
}

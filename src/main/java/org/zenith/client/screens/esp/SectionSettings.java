package org.zenith.client.screens.esp;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.zenith.base.Module;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.Setting;

public final class SectionSettings {
   private SectionSettings() {
   }

   public static List<Setting> of(Module var0) {
      ArrayList<Setting> var1 = new ArrayList<>();
      var1.add(new SectionSettings.ModuleToggle("module.esp.section.enabled", var0));
      var1.addAll(var0.getSettings());
      return var1;
   }

   private static final class ModuleToggle extends BooleanSetting {
      private final Module module;

      private ModuleToggle(String var1, Module var2) {
         super(var1, var2.isEnabled());
         this.module = var2;
      }

      @Override
      public boolean isEnabled() {
         return this.module.isEnabled();
      }

      @Override
      public void toggle() {
         this.module.toggle();
      }

      @Override
      public void safe(JsonObject var1) {
      }

      @Override
      public void load(JsonObject var1) {
      }
   }
}

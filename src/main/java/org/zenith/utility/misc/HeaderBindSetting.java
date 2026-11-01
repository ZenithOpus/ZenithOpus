package org.zenith.utility.misc;

import com.google.gson.JsonObject;
import org.zenith.base.Module;
import org.zenith.client.settings.BindSetting;

public final class HeaderBindSetting extends BindSetting {
   private final Module module;

   public HeaderBindSetting(Module var1) {
      super("gui.inspector.bind", "gui.inspector.bind.desc");
      this.module = var1;
   }

   @Override
   public int getKeyCode() {
      return this.module.getKeyCode();
   }

   @Override
   public int getModifiers() {
      return this.module.getKeyModifiers();
   }

   @Override
   public String getNameKey() {
      return KeyName.method6702(this.module.getKeyCode(), this.module.getKeyModifiers());
   }

   @Override
   public void setBind(int var1, int var2) {
      this.module.setKeyCode(var1);
      this.module.setKeyModifiers(var1 == -1 ? 0 : KeyName.method6698(var2));
   }

   @Override
   public void safe(JsonObject var1) {
   }

   @Override
   public void load(JsonObject var1) {
   }
}

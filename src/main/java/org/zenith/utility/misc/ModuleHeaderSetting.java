package org.zenith.utility.misc;

import org.zenith.base.Module;
import org.zenith.client.settings.ButtonSetting;

public final class ModuleHeaderSetting extends ButtonSetting {
   private final Module module;
   private final HeaderBindSetting headerBindSetting;

   public ModuleHeaderSetting(String var1, Module var2, Runnable var3) {
      super(var1, "w", var3);
      this.module = var2;
      this.headerBindSetting = new HeaderBindSetting(var2);
   }

   @Override
   public String getName() {
      return this.module.getName();
   }

   public HeaderBindSetting getHeaderBindSetting() {
      return this.headerBindSetting;
   }
}

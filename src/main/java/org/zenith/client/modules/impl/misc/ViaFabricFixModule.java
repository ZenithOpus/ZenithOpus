package org.zenith.client.modules.impl.misc;

import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;

@ModuleInfo(
   name = "ViaFabricFix",
   category = ModuleCategory.MISC,
   description = "module.viafabricfix.description"
)
public final class ViaFabricFixModule extends Module {
   public static final ViaFabricFixModule INSTANCE = new ViaFabricFixModule();

   private ViaFabricFixModule() {
   }
}

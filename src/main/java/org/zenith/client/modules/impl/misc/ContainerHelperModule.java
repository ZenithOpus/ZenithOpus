package org.zenith.client.modules.impl.misc;

import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;

@ModuleInfo(
   name = "ContainerHelper",
   description = "module.containerhelper.description",
   category = ModuleCategory.MISC
)
public final class ContainerHelperModule extends Module {
   public static final ContainerHelperModule INSTANCE = new ContainerHelperModule();

   private ContainerHelperModule() {
   }
}

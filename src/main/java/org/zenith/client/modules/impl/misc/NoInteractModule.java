package org.zenith.client.modules.impl.misc;

import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;

@ModuleInfo(
   name = "NoInteract",
   category = ModuleCategory.MISC,
   description = "module.nointeract.description"
)
public final class NoInteractModule extends Module {
   public static final NoInteractModule INSTANCE = new NoInteractModule();

   private NoInteractModule() {
   }
}

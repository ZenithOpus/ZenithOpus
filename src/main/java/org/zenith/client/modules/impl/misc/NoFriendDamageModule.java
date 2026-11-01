package org.zenith.client.modules.impl.misc;

import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;

@ModuleInfo(
   name = "NoFriendDamage",
   category = ModuleCategory.MISC,
   description = "module.nofrienddamage.description"
)
public final class NoFriendDamageModule extends Module {
   public static final NoFriendDamageModule INSTANCE = new NoFriendDamageModule();

   private NoFriendDamageModule() {
   }
}

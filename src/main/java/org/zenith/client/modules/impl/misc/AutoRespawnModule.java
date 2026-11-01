package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.gui.screen.DeathScreen;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;

@ModuleInfo(
   name = "AutoRespawn",
   category = ModuleCategory.MISC,
   description = "module.autorespawn.description"
)
public final class AutoRespawnModule extends Module {
   public static final AutoRespawnModule INSTANCE = new AutoRespawnModule();

   private AutoRespawnModule() {
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null) {
         if (mc.currentScreen instanceof DeathScreen && mc.player.deathTime > 5) {
            mc.player.requestRespawn();
            mc.setScreen(null);
         }
      }
   }
}

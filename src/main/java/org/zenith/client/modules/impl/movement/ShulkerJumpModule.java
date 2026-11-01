package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;

@ModuleInfo(
   name = "ShulkerJump",
   category = ModuleCategory.MOVEMENT,
   description = "module.shulkerjump.description"
)
public final class ShulkerJumpModule extends Module {
   public static final ShulkerJumpModule INSTANCE = new ShulkerJumpModule();
   public int intValue = 0;
   public boolean flag = false;

   @EventTarget
   public void method5129(UpdateEvent var1) {
      if (mc.player != null && mc.world != null) {
         if (mc.player.currentScreenHandler instanceof ShulkerBoxScreenHandler && !this.flag) {
            mc.options.jumpKey.setPressed(true);
            mc.player.closeHandledScreen();
            mc.player.addVelocity(0.0, 2.4, 0.0);
            this.flag = true;
            this.intValue = 0;
         }

         if (this.flag) {
            if (this.intValue >= 5) {
               mc.player.closeHandledScreen();
               mc.options.jumpKey.setPressed(false);
               this.flag = false;
               this.intValue = 0;
            } else {
               this.intValue++;
            }
         }
      }
   }
}

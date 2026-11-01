package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Box;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;

@ModuleInfo(
   name = "BoatHighJump",
   category = ModuleCategory.MOVEMENT,
   description = "module.boathighjump.description"
)
public final class BoatHighJumpModule extends Module {
   public static final BoatHighJumpModule INSTANCE = new BoatHighJumpModule();
   private boolean flag = false;

   private BoatHighJumpModule() {
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null) {
         if (mc.player.getVehicle() == null) {
            if (mc.player.isOnGround()) {
               if (mc.options.jumpKey.isPressed() && !this.flag && this.method2736() != null) {
                  mc.player.setVelocity(mc.player.getVelocity().x, 0.82, mc.player.getVelocity().z);
                  this.flag = true;
               }
            } else {
               this.flag = false;
            }
         }
      }
   }

   private Entity method2736() {
      Box var1 = mc.player.getBoundingBox().expand(1.0);

      for (Entity var3 : mc.world.getEntities()) {
         if (var3 instanceof BoatEntity var4 && !var4.hasPassenger(mc.player) && var4.getBoundingBox().intersects(var1)) {
            return var4;
         }
      }

      return null;
   }

   @Override
   public void onDisable() {
      this.flag = false;
      super.onDisable();
   }
}

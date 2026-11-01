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
   name = "BoatLongJump",
   category = ModuleCategory.MOVEMENT,
   description = "module.boatlongjump.description"
)
public final class BoatLongJumpModule extends Module {
   public static final BoatLongJumpModule INSTANCE = new BoatLongJumpModule();
   private boolean flag = false;

   private BoatLongJumpModule() {
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null) {
         if (mc.player.getVehicle() == null) {
            if (mc.player.isOnGround()) {
               if (mc.options.jumpKey.isPressed() && !this.flag && mc.player.isSprinting() && this.method1825() != null) {
                  float var2 = mc.player.getYaw();
                  double var3 = mc.player.getVelocity().x + -Math.sin(Math.toRadians(var2)) * 0.9F;
                  double var5 = mc.player.getVelocity().z + Math.cos(Math.toRadians(var2)) * 0.9F;
                  mc.player.setVelocity(var3, mc.player.getVelocity().y, var5);
                  this.flag = true;
               }
            } else {
               this.flag = false;
            }
         }
      }
   }

   private Entity method1825() {
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

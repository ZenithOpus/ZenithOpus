package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.PlayerMoveEvent;
import org.zenith.client.events.PlayerTickEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "AirStuck",
   description = "module.airsuck.description",
   category = ModuleCategory.MOVEMENT
)
public final class AirStuckModule extends Module {
   public static final AirStuckModule INSTANCE = new AirStuckModule();
   private final BooleanSetting onlyFalling = new BooleanSetting("module.airStuck.onlyFalling", "module.airStuck.onlyFalling.desc", false);
   private final ModeSetting mode = new ModeSetting("module.airSuck.mode", "module.airSuck.mode.desc", "1.21.4", "1.17.1");
   private Vec3d vec3d = Vec3d.ZERO;
   private boolean frozen;
   private boolean flag;

   private AirStuckModule() {
   }

   @EventTarget
   public void method1577(PlayerMoveEvent var1) {
      if (this.method1581()) {
         var1.setVec3d(Vec3d.ZERO);
      }
   }

   @EventTarget
   public void onTick(TickEvent var1) {
      if (mc.player != null) {
         if (!this.method1581()) {
            this.frozen = false;
            mc.player.setNoGravity(false);
         } else {
            if (!this.frozen) {
               this.frozen = true;
               this.flag = mc.player.isOnGround();
            }

            mc.player.setVelocity(Vec3d.ZERO);
            mc.player.setNoGravity(true);
         }
      }
   }

   @EventTarget
   public void method1578(PlayerTickEvent var1) {
      if (mc.player != null && this.frozen && this.flag && this.method1581()) {
         mc.player.setOnGround(true);
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (mc.player != null && this.method1581()) {
         if (this.method1580()) {
            if (var1.getPacket3() instanceof PlayerMoveC2SPacket) {
               var1.setCancelled(true);
            }
         } else if (var1.getPacket3() instanceof PositionAndOnGround || var1.getPacket3() instanceof Full || var1.getPacket3() instanceof OnGroundOnly) {
            var1.setCancelled(true);
         }
      }
   }

   @EventTarget(0)
   public void method1579(UpdateEvent var1) {
      if (mc.player != null && !this.method1580() && this.method1581()) {
         if (Math.abs(zenith.getRotationManager().method7844().getFloatValue() - mc.player.lastYawClient) != 0.0F
            || Math.abs(zenith.getRotationManager().method7844().getFloatValue2() - mc.player.lastPitchClient) != 0.0F) {
            UseItemUtils.method6533(0.0, zenith.getRotationManager().method7844());
         }
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (mc.player != null) {
         this.vec3d = mc.player.getVelocity();
         this.frozen = false;
         if (this.method1581()) {
            mc.player.setNoGravity(true);
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (mc.player != null) {
         if (!mc.player.isOnGround() && this.method1581() && this.vec3d != null) {
            mc.player.setVelocity(this.vec3d);
         }

         mc.player.setNoGravity(false);
      }
   }

   private boolean method1580() {
      return this.mode.method2101("1.21.4");
   }

   private boolean method1581() {
      return mc.player != null && (!this.onlyFalling.isEnabled() || !mc.player.isOnGround() && mc.player.fallDistance > 0.0);
   }

   public void method1582() {
      this.onEnable();
   }
}

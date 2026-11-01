package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec3d;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "BoatFly",
   category = ModuleCategory.MOVEMENT,
   description = "module.boatfly.description"
)
public final class BoatFlyModule extends Module {
   public static final BoatFlyModule INSTANCE = new BoatFlyModule();
   private static final int CONST_intValue = 40;
   private static final double CONST_doubleValue = 0.04;
   private static final int CONST_intValue2 = 20;
   private static final double CONST_doubleValue2 = 20.25;
   private final ModeSetting mode = new ModeSetting(
      "module.boatfly.mode", "module.boatfly.mode.desc", "module.boatfly.mode.grim", "module.boatfly.mode.vanilla"
   );
   private final SliderSetting speed = new SliderSetting("module.boatfly.speed", 22.0F, 1.0F, 60.0F, 0.5F, "module.boatfly.speed.desc", "bps");
   private final BooleanSetting vertical = new BooleanSetting("module.boatfly.vertical", "module.boatfly.vertical.desc", false);
   private final SliderSetting verticalSpeed = new SliderSetting(
      "module.boatfly.verticalSpeed", 8.0F, 1.0F, 30.0F, 0.5F, "module.boatfly.verticalSpeed.desc", "bps", this.vertical::isEnabled
   );
   private final BooleanSetting remount = new BooleanSetting("module.boatfly.remount", "module.boatfly.remount.desc", true, () -> this.mode.isSelected(0));
   private final BooleanSetting antiKick = new BooleanSetting("module.boatfly.antiKick", "module.boatfly.antiKick.desc", true);
   private final BooleanSetting debug = new BooleanSetting("module.boatfly.debug", "module.boatfly.debug.desc", false);
   private volatile boolean flag;
   private final AtomicInteger atomicInteger = new AtomicInteger();
   private AbstractBoatEntity abstractBoatEntity;
   private int intValue;
   private int intValue2;
   private boolean flag2;
   private int intValue3;

   private BoatFlyModule() {
   }

   @Override
   public void onEnable() {
      this.resetState();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.resetState();
      super.onDisable();
   }

   private void resetState() {
      this.flag = false;
      this.atomicInteger.set(0);
      this.abstractBoatEntity = null;
      this.intValue = 0;
      this.intValue2 = 0;
      this.flag2 = false;
      this.intValue3 = 0;
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null) {
         if (mc.player.getVehicle() instanceof AbstractBoatEntity var2 && var2.getControllingPassenger() == mc.player) {
            this.abstractBoatEntity = var2;
            this.intValue = 0;
         } else {
            this.flag = false;
            if (this.abstractBoatEntity != null) {
               if (mc.options.sneakKey.isPressed() || !this.abstractBoatEntity.isAlive()) {
                  this.abstractBoatEntity = null;
               } else if (this.intValue == 0) {
                  this.intValue = 20;
                  if (this.debug.isEnabled()) {
                     ChatLogger.method2021("BoatFly: сервер ссадил с лодки");
                  }
               }
            }

            this.method1931();
         }

         if (this.debug.isEnabled() && ++this.intValue3 >= 20) {
            this.intValue3 = 0;
            int var4;
            if ((var4 = this.atomicInteger.getAndSet(0)) > 0) {
               ChatLogger.method2019("BoatFly: проигнорировано откатов за секунду: " + var4);
            }
         }
      } else {
         this.resetState();
      }
   }

   private void method1931() {
      if (this.intValue > 0 && this.abstractBoatEntity != null) {
         this.intValue--;
         if (this.mode.isSelected(0)
            && this.remount.isEnabled()
            && this.abstractBoatEntity.isAlive()
            && !this.abstractBoatEntity.hasPassengers()
            && !(mc.player.squaredDistanceTo(this.abstractBoatEntity) > 20.25)) {
            if (this.intValue % 2 == 0) {
               mc.interactionManager.interactEntity(mc.player, this.abstractBoatEntity, Hand.MAIN_HAND);
               mc.player.swingHand(Hand.MAIN_HAND);
            }

            if (this.intValue == 0) {
               this.abstractBoatEntity = null;
            }
         } else {
            if (this.intValue == 0) {
               this.abstractBoatEntity = null;
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100() && this.flag && this.mode.isSelected(0) && var1.getPacket3() instanceof VehicleMoveS2CPacket) {
         var1.cancel();
         this.atomicInteger.incrementAndGet();
      }
   }

   public Vec3d method1932(AbstractBoatEntity var1, Vec3d var2) {
      if (this.isEnabled() && mc.player != null && var1.getControllingPassenger() == mc.player) {
         this.flag = true;
         float var3 = mc.player.getYaw();
         var1.setYaw(var3);
         PlayerInput var4 = mc.player.input.playerInput;
         double var5 = (var4.forward() ? 1.0 : 0.0) - (var4.backward() ? 1.0 : 0.0);
         double var7 = (var4.left() ? 1.0 : 0.0) - (var4.right() ? 1.0 : 0.0);
         double var9 = 0.0;
         double var11 = 0.0;
         double var13;
         if ((var13 = Math.sqrt(var5 * var5 + var7 * var7)) > 0.0) {
            double var15 = this.speed.getValue3() / 20.0 / var13;
            double var17;
            double var19 = Math.sin(var17 = Math.toRadians(var3));
            double var21 = Math.cos(var17);
            var9 = (var7 * var21 - var5 * var19) * var15;
            var11 = (var5 * var21 + var7 * var19) * var15;
         }

         double var23 = 0.0;
         if (this.vertical.isEnabled()) {
            var23 = ((var4.jump() ? 1.0 : 0.0) - (var4.sprint() ? 1.0 : 0.0)) * this.verticalSpeed.getValue3() / 20.0;
         }

         this.intValue2++;
         if (this.flag2) {
            var23 += 0.04;
            this.flag2 = false;
         } else if (this.antiKick.isEnabled() && var23 >= 0.0 && this.intValue2 % 40 == 0 && !var1.isOnGround()) {
            var23 -= 0.04;
            this.flag2 = true;
         }

         Vec3d var24 = new Vec3d(var9, var23, var11);
         var1.setVelocity(var24);
         var1.fallDistance = 0.0;
         return var24;
      } else {
         return var2;
      }
   }
}

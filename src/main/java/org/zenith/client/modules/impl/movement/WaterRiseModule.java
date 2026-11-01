package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.entity.effect.StatusEffects;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.utility.misc.RotationRequest;

@ModuleInfo(
   name = "WaterRise",
   category = ModuleCategory.MOVEMENT,
   description = "module.waterrise.description"
)
public final class WaterRiseModule extends Module {
   public static final WaterRiseModule INSTANCE = new WaterRiseModule();
   public static final float CONST_floatValue = 1.0E-4F;
   private int intValue = -1073741824;

   private WaterRiseModule() {
   }

   private boolean active() {
      if (mc.player == null || mc.world == null || mc.currentScreen != null) {
         return false;
      } else {
         return mc.player.isTouchingWater() && !mc.player.hasVehicle() && !mc.player.isGliding() && !mc.player.getAbilities().flying
            ? mc.options.jumpKey.isPressed() || this.method1741()
            : false;
      }
   }

   private boolean method1741() {
      return mc.options.sneakKey.isPressed() && !mc.options.jumpKey.isPressed();
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent var1) {
      if (this.active()) {
         if (var1.getPlayerInput().forward()) {
            var1.method3039(true);
         }

         if (var1.getPlayerInput().sneak()) {
            var1.method3040(false);
         }
      }
   }

   public void method1742() {
      if (mc.player != null && !mc.player.isSprinting() && this.canStartSprinting()) {
         mc.player.setSprinting(true);
      }
   }

   public boolean method1743() {
      return this.isEnabled() && this.active();
   }

   public float method1744() {
      if (mc.player == null || mc.player.isSprinting() || !this.canStartSprinting() || mc.player.age - this.intValue < 10 && mc.player.age >= this.intValue) {
         return 1.0E-4F;
      } else {
         this.intValue = mc.player.age;
         mc.player.setSprinting(true);
         return 1.0F;
      }
   }

   private boolean canStartSprinting() {
      return mc.player.isSubmergedInWater()
         && (mc.player.getHungerManager().getFoodLevel() > 6 || mc.player.getAbilities().allowFlying)
         && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
         && !mc.player.isUsingItem();
   }

   @EventTarget
   public void method1745(Event5 var1) {
      if (this.active()) {
         float var2 = this.method1741() ? -90.0F : 90.0F;
         Rotation var3 = new Rotation(mc.player.getYaw(), var2);
         rotationManager.method7849(new RotationRequest(var3, () -> var3, bcCameraTestnerv.getRotationConfig()), 0, this);
      }
   }
}

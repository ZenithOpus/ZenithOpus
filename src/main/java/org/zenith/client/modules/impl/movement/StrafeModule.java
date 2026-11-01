package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.input.KeyboardInput;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.utility.misc.MovementInputUtils;
import org.zenith.utility.misc.RotationRequest;

@ModuleInfo(
   name = "Strafe",
   description = "module.strafe.description",
   category = ModuleCategory.MOVEMENT
)
public final class StrafeModule extends Module {
   public static final StrafeModule INSTANCE = new StrafeModule();

   private StrafeModule() {
   }

   @EventTarget
   public void method1527(Event5 var1) {
      float var2 = KeyboardInput.getMovementMultiplier(mc.options.forwardKey.isPressed(), mc.options.backKey.isPressed());
      float var3 = KeyboardInput.getMovementMultiplier(mc.options.leftKey.isPressed(), mc.options.rightKey.isPressed());
      if (var2 != 0.0F || var3 != 0.0F) {
         float var4 = mc.player.getYaw();
         float var5 = (float)Math.toDegrees(Math.atan2(-var3, var2));
         float var6 = var4 + var5;
         Rotation var7 = rotationManager.method7844().method3141(rotationManager.method7844().method3131(new Rotation(var6, mc.player.getPitch())));
         rotationManager.method7849(
            new RotationRequest(
               var7, () -> rotationManager.getBcCameraTestnerv().method1851(bcCameraTestnerv.getRotationConfig(), var7), bcCameraTestnerv.getRotationConfig()
            ),
            -1,
            this
         );
      }
   }

   @EventTarget
   public void method1528(MoveInputEvent var1) {
      if (MovementInputUtils.method7418() && rotationManager.getModule() == this) {
         var1.method3047(false);
         var1.method3045(false);
         var1.method3046(false);
         var1.method3038(
            mc.options.forwardKey.isPressed() || mc.options.backKey.isPressed() || mc.options.rightKey.isPressed() || mc.options.leftKey.isPressed()
         );
      }
   }
}

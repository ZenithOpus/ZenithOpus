package org.zenith.utility.mixin.entity;

import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.zenith.client.modules.impl.movement.BoatFlyModule;

@Mixin({AbstractBoatEntity.class})
public abstract class MixinAbstractBoatEntity {
   @ModifyArg(
      method = {"tick()V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/vehicle/AbstractBoatEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"
      ),
      index = 1
   )
   private Vec3d zenith$boatFlyVelocity(Vec3d var1) {
      return BoatFlyModule.INSTANCE.method1932((AbstractBoatEntity)(Object)this, var1);
   }
}

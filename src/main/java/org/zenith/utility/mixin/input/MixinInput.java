package org.zenith.utility.mixin.input;

import net.minecraft.client.input.Input;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.zenith.utility.misc.ViaCompat;

@Mixin({Input.class})
public abstract class MixinInput implements ViaCompat.Handler {
   @Shadow
   public Vec2f movementVector;

   @Override
   public void zenith$setMovementVector(Vec2f var1) {
      this.movementVector = var1;
   }
}

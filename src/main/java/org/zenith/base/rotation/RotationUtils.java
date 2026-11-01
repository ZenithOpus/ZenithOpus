package org.zenith.base.rotation;

import lombok.Generated;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.MinecraftAccess;
import org.zenith.Rotation;

public final class RotationUtils implements MinecraftAccess {
   public static Rotation method6911() {
      return new Rotation(mc.player.getYaw(), mc.player.getPitch());
   }

   public static Rotation method6912(Vec3d var0) {
      return new Rotation(
         (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var0.z, var0.x)) - 90.0),
         (float)MathHelper.wrapDegrees(Math.toDegrees(-Math.atan2(var0.y, Math.hypot(var0.x, var0.z))))
      );
   }

   public static Rotation method6913(Vec3d var0) {
      return method6912(var0.subtract(mc.player.getEyePos()));
   }

   @Generated
   private RotationUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.util.math.Vec3d;
import org.zenith.CancellableEvent;

public class PlayerMoveEvent extends CancellableEvent {
   private Vec3d vec3d;

   @Generated
   public PlayerMoveEvent(Vec3d var1) {
      this.vec3d = var1;
   }

   @Generated
   public Vec3d getVec3d() {
      return this.vec3d;
   }

   @Generated
   public void setVec3d(Vec3d var1) {
      this.vec3d = var1;
   }
}

package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.util.math.Vec3d;

public class MovementCorrectionEvent implements Event {
   private final float speed;
   private final Vec3d vec3d;

   @Generated
   public float getSpeed() {
      return this.speed;
   }

   @Generated
   public Vec3d getVec3d() {
      return this.vec3d;
   }

   @Generated
   public MovementCorrectionEvent(float var1, Vec3d var2) {
      this.speed = var1;
      this.vec3d = var2;
   }
}

package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.util.math.Vec3d;

public class CameraPosEvent implements Event {
   private Vec3d vec3d;

   @Generated
   public Vec3d getVec3d() {
      return this.vec3d;
   }

   @Generated
   public void setVec3d(Vec3d var1) {
      this.vec3d = var1;
   }

   @Generated
   public CameraPosEvent(Vec3d var1) {
      this.vec3d = var1;
   }
}

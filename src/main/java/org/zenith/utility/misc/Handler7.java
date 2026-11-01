package org.zenith.utility.misc;

import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithColor;

public interface Handler7 {
   Vec3d method1556();

   Vec3d method1557();

   float getSize();

   float getAlpha();

   default float method1449(float var1) {
      return this.getAlpha();
   }

   ZenithColor method1558();

   String method1559();

   float method1560();

   boolean method1555();
}

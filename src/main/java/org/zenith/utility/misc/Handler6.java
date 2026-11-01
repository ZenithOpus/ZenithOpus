package org.zenith.utility.misc;

import net.minecraft.util.math.Vec3d;

public interface Handler6 {
   double zenithDLC$getPrevServerX();

   double zenithDLC$getPrevServerY();

   double zenithDLC$getPrevServerZ();

   void zenithDLC$trackServerPos(double var1, double var3, double var5);

   Vec3d zenithDLC$getServerDelta();
}

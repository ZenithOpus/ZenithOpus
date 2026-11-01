package org.zenith.utility.misc;

import net.minecraft.client.render.Frustum;
import org.jetbrains.annotations.Nullable;

public final class FrustumHolder {
   @Nullable
   private static volatile Frustum frustum;

   private FrustumHolder() {
   }

   public static void method3035(@Nullable Frustum var0) {
      frustum = var0;
   }

   @Nullable
   public static Frustum getFrustum() {
      return frustum;
   }
}

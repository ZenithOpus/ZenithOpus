package org.zenith.utility.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.zenith.MatrixUtils;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class MatrixStackUtils {
   public static final float CONST_floatValue = 18.0F;

   private MatrixStackUtils() {
   }

   public static MatrixStack method5467(MenuDrawContext var0, float var1, float var2, float var3) {
      MatrixStack var4 = MatrixUtils.method666(var0.getMatrices());
      var4.translate(var1, var2, 50.0F);
      var4.scale(var3, -var3, var3 * 0.4F);
      var4.multiply(RotationAxis.POSITIVE_X.rotationDegrees(18.0F));
      return var4;
   }

   public static float method5468() {
      return -18.0F;
   }

   public static float method5469() {
      return 0.0F;
   }
}

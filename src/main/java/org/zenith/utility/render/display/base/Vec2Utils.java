package org.zenith.utility.render.display.base;

import lombok.Generated;
import net.minecraft.client.util.math.Vector2f;
import org.zenith.MinecraftAccess;

public final class Vec2Utils implements MinecraftAccess {
   public static boolean method8065(double var0, double var2, double var4, double var6, int var8, int var9) {
      return var8 >= var0 && var8 < var0 + var4 && var9 >= var2 && var9 < var2 + var6;
   }

   public static boolean method8066(double var0, double var2, double var4, double var6, MenuDrawContext var8) {
      return method8065(var0, var2, var4, var6, var8.getMouseX(), var8.getMouseY());
   }

   public static boolean method8067(double var0, double var2, double var4, double var6, double var8, double var10) {
      return var8 >= var0 && var8 < var0 + var4 && var10 >= var2 && var10 < var2 + var6;
   }

   public static Vector2f method8068(double var0) {
      return new Vector2f((float)(mc.mouse.getX() / var0), (float)(mc.mouse.getY() / var0));
   }

   @Generated
   private Vec2Utils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

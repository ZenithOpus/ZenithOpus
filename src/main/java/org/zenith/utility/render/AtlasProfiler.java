package org.zenith.utility.render;

import net.minecraft.client.texture.AbstractTexture;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AtlasProfiler {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Render");
   private static final int CONST_intValue = 34046;
   private static final int CONST_intValue2 = 34047;
   public static int intValue = 4;
   public static boolean flag = true;
   private static Boolean booleanValue;
   private static float floatValue;
   private static boolean flag2;

   private AtlasProfiler() {
   }

   public static void method7952(AbstractTexture var0) {
      if (flag && var0 != null) {
         if (!flag2) {
            flag2 = true;
            logger.info("Сглаживание дальних текстур: в 1.21.11 задаётся сэмплером, перенос ещё не сделан");
         }
      }
   }

   private static boolean method7953() {
      if (booleanValue == null) {
         booleanValue = GL.getCapabilities().GL_EXT_texture_filter_anisotropic;
         floatValue = booleanValue ? GL11.glGetFloat(34047) : 1.0F;
         logger.info(
            booleanValue
               ? "Сглаживание дальних текстур: линейное уменьшение, анизотропия до " + (int)floatValue + "x"
               : "Сглаживание дальних текстур: линейное уменьшение, анизотропии у драйвера нет"
         );
      }

      return booleanValue;
   }
}

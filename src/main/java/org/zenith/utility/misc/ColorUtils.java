package org.zenith.utility.misc;

import java.awt.Color;
import java.util.regex.Pattern;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;
import org.zenith.Zenith;
import org.zenith.ZenithColor;

public final class ColorUtils {
   public static final int CONST_intValue = method6440(255, 85, 85, 255);
   private static final Pattern pattern = Pattern.compile("(?i)§[0-9a-f-or]");

   public static int method6420(int var0) {
      return var0 >> 16 & 0xFF;
   }

   public static int method6421(int var0) {
      return var0 >> 8 & 0xFF;
   }

   public static int method6422(int var0) {
      return var0 & 0xFF;
   }

   public static int method6423(int var0) {
      return var0 >> 24 & 0xFF;
   }

   public static float method6424(int var0) {
      return method6420(var0) / 255.0F;
   }

   public static float method6425(int var0) {
      return method6421(var0) / 255.0F;
   }

   public static float method6426(int var0) {
      return method6422(var0) / 255.0F;
   }

   public static float method6427(int var0) {
      return method6423(var0) / 255.0F;
   }

   public static int[] method6428(int var0) {
      return new int[]{method6420(var0), method6421(var0), method6422(var0), method6423(var0)};
   }

   public static int[] method6429(int var0) {
      return new int[]{method6420(var0), method6421(var0), method6422(var0)};
   }

   public static float[] method6430(int var0) {
      return new float[]{method6424(var0), method6425(var0), method6426(var0), method6427(var0)};
   }

   public static float[] method6431(int var0) {
      return new float[]{method6424(var0), method6425(var0), method6426(var0)};
   }

   public static boolean method6432(String var0) {
      return var0 != null && var0.matches("(?i)^[a-f0-9]{6}$");
   }

   public static ZenithColor method6433(String var0, ZenithColor var1) {
      if (!method6432(var0)) {
         return var1;
      } else {
         int var2 = Integer.parseInt(var0, 16);
         int var3 = var2 >> 16 & 0xFF;
         int var4 = var2 >> 8 & 0xFF;
         int var5 = var2 & 0xFF;
         return new ZenithColor(new Color(var3, var4, var5));
      }
   }

   public static String method6434(ZenithColor var0) {
      int var1 = var0.method7287();
      return String.format("%06X", var1 & 16777215);
   }

   public static ZenithColor method6435(int var0, int var1, ZenithColor var2, ZenithColor var3) {
      int var4 = (int)((System.currentTimeMillis() / var0 + var1) % 360L);
      var4 = (var4 >= 180 ? 360 - var4 : var4) * 2;
      return method6437(var2, var3, var4 / 360.0F);
   }

   public static ZenithColor method6436(int var0, int var1, ZenithColor... var2) {
      int var3 = (int)((System.currentTimeMillis() / var0 + var1) % 360L);
      var3 = (var3 > 180 ? 360 - var3 : var3) + 180;
      int var4 = (int)(var3 / 360.0F * var2.length);
      if (var4 == var2.length) {
         var4--;
      }

      ZenithColor var5 = var2[var4];
      ZenithColor var6 = var2[var4 == var2.length - 1 ? 0 : var4 + 1];
      return method6437(var5, var6, var3 / 360.0F * var2.length - var4);
   }

   public static ZenithColor method6437(ZenithColor var0, ZenithColor var1, float var2) {
      return var0.method7295(var1, var2);
   }

   public static String method6438(String var0) {
      return var0 != null && !var0.isEmpty() ? pattern.matcher(var0).replaceAll("") : null;
   }

   public static int method6439(int var0, float var1) {
      return method6440(method6420(var0), method6421(var0), method6422(var0), Math.round(method6423(var0) * var1));
   }

   private static int method6440(int var0, int var1, int var2, int var3) {
      return MathHelper.clamp(var3, 0, 255) << 24 | MathHelper.clamp(var0, 0, 255) << 16 | MathHelper.clamp(var1, 0, 255) << 8 | MathHelper.clamp(var2, 0, 255);
   }

   public static int method6441(int var0) {
      return Zenith.getInstance().getStyleManager().getClientColor(var0).method7287();
   }

   @Generated
   private ColorUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

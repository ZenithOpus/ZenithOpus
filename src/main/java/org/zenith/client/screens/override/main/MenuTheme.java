package org.zenith.client.screens.override.main;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;

public final class MenuTheme {
   public static final float SCALE = 0.5F;

   private MenuTheme() {
   }

   public static float px(float var0) {
      return var0 * 0.5F;
   }

   public static String t(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   public static ZenithStyle style() {
      return Zenith.getInstance().getStyleManager().getCurrentStyle();
   }

   public static ZenithColor accent() {
      return style().getPrimaryColor().getZenithColor();
   }

   public static ZenithColor accentSecondary() {
      return style().getSecondaryPrimaryColor().getZenithColor();
   }

   public static ZenithColor panel() {
      return style().getHudBackground().getZenithColor();
   }

   public static ZenithColor plate() {
      return style().getHeaderHudBackground().getZenithColor();
   }

   public static ZenithColor surface() {
      return style().getHudBackground().getZenithColor();
   }

   public static ZenithColor border() {
      return style().getFieldBorder().getZenithColor();
   }

   public static ZenithColor borderSubtle() {
      return border().method7294(0.5F);
   }

   public static ZenithColor track() {
      return style().getDisableActiveBg().getZenithColor();
   }

   public static ZenithColor text() {
      return style().getTextEnable().getZenithColor();
   }

   public static ZenithColor textSecondary() {
      return style().getTextSecondary().getZenithColor();
   }

   public static ZenithColor textTertiary() {
      return style().getTextTertiary().getZenithColor();
   }

   public static ZenithColor danger() {
      return style().getHeartIcon().getZenithColor();
   }

   public static ZenithColor onAccent() {
      return ZenithColor.INSTANCE;
   }

   public static Font regular(float var0) {
      return Fonts.NEW_REGULAR.getFont(px(var0));
   }

   public static Font medium(float var0) {
      return Fonts.NEW_MEDIUM.getFont(px(var0));
   }

   public static Font semibold(float var0) {
      return Fonts.NEW_SEMIBOLD.getFont(px(var0));
   }

   public static Font icon(float var0) {
      return Fonts.NEW_ICONS.getFont(px(var0));
   }

   public static Font legacyIcon(float var0) {
      return Fonts.ICONS.getFont(px(var0));
   }

   public static float snap(float var0) {
      Window var1 = MinecraftClient.getInstance().getWindow();
      if (var1 == null) {
         return var0;
      } else {
         double var2 = var1.getScaleFactor();
         return var2 <= 0.0 ? var0 : (float)(Math.round(var0 * var2) / var2);
      }
   }

   public static List<String> wrap(Font var0, String var1, float var2) {
      ArrayList<String> var3 = new ArrayList<>();
      if (var1 != null && !var1.isEmpty()) {
         if (var0.width(var1) <= var2) {
            var3.add(var1);
            return var3;
         } else {
            StringBuilder var4 = new StringBuilder();

            for (String var8 : var1.split(" ")) {
               String var9 = var4.isEmpty() ? var8 : var4 + " " + var8;
               if (var0.width(var9) > var2 && !var4.isEmpty()) {
                  var3.add(var4.toString());
                  var4 = new StringBuilder(var8);
               } else {
                  var4 = new StringBuilder(var9);
               }
            }

            if (!var4.isEmpty()) {
               var3.add(var4.toString());
            }

            return var3;
         }
      } else {
         return var3;
      }
   }
}

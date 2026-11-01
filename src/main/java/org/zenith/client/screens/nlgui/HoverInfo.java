package org.zenith.client.screens.nlgui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class HoverInfo {
   private static String requested;
   private static String requestedTitle;
   private static final Animation SHOWN = new Animation(240L, 1.0F, Easing.INSTANCE12);
   private static String shownText;
   private static String shownTitle;
   private static String captionFor;
   private static String captionText;
   private static String wrapText;
   private static Font wrapFont;
   private static float wrapWidth = Float.NaN;
   private static List<String> wrapLines;

   private HoverInfo() {
   }

   public static boolean enabled() {
      Zenith var0 = Zenith.getInstance();
      NLMenuScreen var1 = var0 == null ? null : var0.getNLMenuScreen();
      return var1 == null || !var1.isShortMode();
   }

   public static float boxHeight() {
      return 34.0F * SHOWN.method2808(enabled() ? 1.0F : 0.0F);
   }

   public static void request(String var0, String var1) {
      if (var1 != null && !var1.isBlank()) {
         requestedTitle = var0;
         requested = var1;
      }
   }

   public static void beginFrame() {
      requested = null;
      requestedTitle = null;
   }

   public static boolean hasText() {
      return requested != null;
   }

   public static void render(MenuDrawContext var0, float var1, float var2, float var3, float var4, float var5) {
      float var6;
      if (!((var6 = SHOWN.getValue()) <= 0.01F)) {
         var5 *= var6;
         ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var7 != null) {
            Font var8 = Fonts.NEW_MEDIUM.getFont(4.0F);
            Font var9 = Fonts.NEW_REGULAR.getFont(4.8F);
            if (requested != null) {
               shownText = requested;
               shownTitle = requestedTitle;
            }

            String var10 = shownTitle != null && !shownTitle.isBlank() ? shownTitle : null;
            if (captionText == null || !Objects.equals(var10, captionFor)) {
               captionFor = var10;
               captionText = spaced(var10 == null ? "HOVER" : var10.toUpperCase());
            }

            var0.drawText(var8, captionText, var1, var2, var7.getTextTertiary().getZenithColor().method7294(var5 * 0.9F));
            if (shownText != null) {
               float var11 = var2 + var8.height() + 3.0F;
               float var12 = var9.height() + 1.5F;
               int var13 = Math.max(1, (int)((var4 - (var11 - var2)) / var12));
               if (wrapLines == null || var9 != wrapFont || Float.compare(var3, wrapWidth) != 0 || !shownText.equals(wrapText)) {
                  wrapLines = wrap(shownText, var9, var3);
                  wrapText = shownText;
                  wrapFont = var9;
                  wrapWidth = var3;
               }

               var var14 = wrapLines;

               for (int var15 = 0; var15 < Math.min(var14.size(), var13); var15++) {
                  var0.drawText(var9, (String)var14.get(var15), var1, var11 + var15 * var12, var7.getTextSecondary().getZenithColor().method7294(var5));
               }
            }
         }
      }
   }

   public static String spaced(String var0) {
      StringBuilder var1 = new StringBuilder(var0.length() * 2);

      for (int var2 = 0; var2 < var0.length(); var2++) {
         if (var2 > 0) {
            var1.append(' ');
         }

         var1.append(var0.charAt(var2));
      }

      return var1.toString();
   }

   private static List<String> wrap(String var0, Font var1, float var2) {
      ArrayList<String> var3 = new ArrayList<>();
      StringBuilder var4 = new StringBuilder();

      for (String var8 : var0.split(" ")) {
         String var9 = var4.isEmpty() ? var8 : var4 + " " + var8;
         if (var1.width(var9) > var2 && !var4.isEmpty()) {
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
}

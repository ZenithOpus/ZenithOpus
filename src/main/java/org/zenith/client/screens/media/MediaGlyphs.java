package org.zenith.client.screens.media;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class MediaGlyphs {
   private static volatile MediaGlyphs.Stamp stampA;
   private static volatile MediaGlyphs.Stamp stampB;
   private static boolean stampTurn;
   private static final Map<MediaGlyphs.EllipsisKey, String> ELLIPSIZED = new LinkedHashMap<MediaGlyphs.EllipsisKey, String>(64, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<MediaGlyphs.EllipsisKey, String> var1) {
         return this.size() > 256;
      }
   };

   private MediaGlyphs() {
   }

   public static void playPause(CustomDrawContext var0, float var1, float var2, float var3, boolean var4, ZenithColor var5) {
      if (var4) {
         float var6 = var3 * 0.24F;
         float var7 = var3 * 0.78F;
         float var8 = var3 * 0.2F;
         ProxyDotRadiusData var9 = ProxyDotRadiusData.method8069(Math.min(var6 / 2.0F, var3 * 0.07F));
         var0.drawRoundedRect(var1 - var8 / 2.0F - var6, var2 - var7 / 2.0F, var6, var7, var9, var5);
         var0.drawRoundedRect(var1 + var8 / 2.0F, var2 - var7 / 2.0F, var6, var7, var9, var5);
      } else {
         glyph(var0, "s", var1 + var3 * 0.06F, var2, var3 * 0.95F, var5);
      }
   }

   public static void skip(CustomDrawContext var0, float var1, float var2, float var3, boolean var4, ZenithColor var5) {
      String var6 = var4 ? "z" : "y";
      float var7 = var3 * 0.62F;
      float var8 = var3 * 0.23F;
      glyph(var0, var6, var1 - var8, var2, var7, var5);
      glyph(var0, var6, var1 + var8, var2, var7, var5);
   }

   public static void shuffle(CustomDrawContext var0, float var1, float var2, float var3, ZenithColor var4) {
      float var5 = var1 - var3 * 0.5F;
      float var6 = var1 + var3 * 0.28F;
      float var7 = var2 - var3 * 0.3F;
      float var8 = var2 + var3 * 0.3F;
      float var9 = var5 + var3 * 0.22F;
      float var10 = Math.max(0.8F, var3 * 0.11F);
      line(var0, var5, var7, var9, var7, var10, var4);
      line(var0, var9, var7, var6, var8, var10, var4);
      line(var0, var5, var8, var9, var8, var10, var4);
      line(var0, var9, var8, var6, var7, var10, var4);
      glyph(var0, "z", var6 + var3 * 0.1F, var7, var3 * 0.42F, var4);
      glyph(var0, "z", var6 + var3 * 0.1F, var8, var3 * 0.42F, var4);
   }

   public static void repeat(CustomDrawContext var0, float var1, float var2, float var3, boolean var4, ZenithColor var5) {
      float var6 = var3 * 0.86F;
      float var7 = var3 * 0.56F;
      float var8 = Math.max(0.8F, var3 * 0.11F);
      float var9 = var1 - var6 / 2.0F;
      float var10 = var1 + var6 / 2.0F;
      float var11 = var2 - var7 / 2.0F;
      float var12 = var2 + var7 / 2.0F;
      float var13 = var7 * 0.5F;
      line(var0, var9 + var13, var11, var10 - var13, var11, var8, var5);
      line(var0, var9 + var13, var12, var10 - var13, var12, var8, var5);
      arc(var0, var9 + var13, var2, var7 / 2.0F, var8, 180.0F, 180.0F, var5);
      arc(var0, var10 - var13, var2, var7 / 2.0F, var8, 0.0F, 180.0F, var5);
      glyph(var0, "z", var1 + var6 * 0.14F, var11, var3 * 0.36F, var5);
      glyph(var0, "y", var1 - var6 * 0.14F, var12, var3 * 0.36F, var5);
      if (var4) {
         Font var14 = Fonts.NEW_SEMIBOLD.getFont(var3 * 0.42F);
         HudListLayout.method7637(var0, var14, "1", var1 - var3 * 0.2F, var2 - var3 * 0.2F, var3 * 0.4F, var5);
      }
   }

   public static void speaker(CustomDrawContext var0, float var1, float var2, float var3, int var4, ZenithColor var5) {
      float var6 = var3 * 0.2F;
      float var7 = var3 * 0.34F;
      float var8 = var1 - var3 * 0.42F;
      var0.drawRoundedRect(var8, var2 - var7 / 2.0F, var6, var7, ProxyDotRadiusData.method8069(var6 * 0.3F), var5);
      glyph(var0, "y", var8 + var6 + var3 * 0.08F, var2, var3 * 0.66F, var5);
      float var9 = Math.max(0.7F, var3 * 0.09F);
      float var10 = var8 + var6 + var3 * 0.05F;

      for (int var11 = 0; var11 < var4; var11++) {
         float var12 = var3 * (0.34F + var11 * 0.2F);
         arc(var0, var10, var2, var12, var9, 50.0F, 80.0F, var5);
      }
   }

   public static void arc(CustomDrawContext var0, float var1, float var2, float var3, float var4, float var5, float var6, ZenithColor var7) {
      float var8 = (var3 + var4 / 2.0F + 1.6F) * 2.0F;
      var0.pushMatrix();
      var0.getMatrices().translate(var1, var2);
      var0.getMatrices().rotate((float)Math.toRadians(var5));
      var0.drawArcBorder(-var8 / 2.0F, -var8 / 2.0F, var8, var8, var4, var6, 1.0F, var7);
      var0.popMatrix();
   }

   public static void close(CustomDrawContext var0, float var1, float var2, float var3, ZenithColor var4) {
      float var5 = Math.max(0.8F, var3 * 0.13F);
      float var6 = var3 * 0.36F;
      line(var0, var1 - var6, var2 - var6, var1 + var6, var2 + var6, var5, var4);
      line(var0, var1 - var6, var2 + var6, var1 + var6, var2 - var6, var5, var4);
   }

   public static void line(CustomDrawContext var0, float var1, float var2, float var3, float var4, float var5, ZenithColor var6) {
      float var7 = var3 - var1;
      float var8 = var4 - var2;
      float var9 = (float)Math.sqrt(var7 * var7 + var8 * var8);
      if (!(var9 < 0.01F)) {
         var0.pushMatrix();
         var0.getMatrices().translate(var1, var2);
         var0.getMatrices().rotate((float)Math.atan2(var8, var7));
         var0.drawRoundedRect(-var5 / 2.0F, -var5 / 2.0F, var9 + var5, var5, ProxyDotRadiusData.method8069(var5 / 2.0F), var6);
         var0.popMatrix();
      }
   }

   private static void arrow(CustomDrawContext var0, float var1, float var2, float var3, float var4, ZenithColor var5) {
      float var6 = var3 * 0.7071F;
      line(var0, var1 - var6, var2 - var6, var1, var2, var4, var5);
      line(var0, var1 - var6, var2 + var6, var1, var2, var4, var5);
   }

   private static void glyph(CustomDrawContext var0, String var1, float var2, float var3, float var4, ZenithColor var5) {
      Font var6 = Fonts.NEW_ICONS.getFont(var4);
      HudListLayout.method7637(var0, var6, var1, var2 - var4 / 2.0F, var3 - var4 / 2.0F, var4, var5);
   }

   public static String time(double var0) {
      long var2 = Math.max(0L, Math.round(Math.floor(var0)));
      MediaGlyphs.Stamp var4 = stampA;
      if (var4 != null && var4.total() == var2) {
         return var4.text();
      } else {
         MediaGlyphs.Stamp var5 = stampB;
         if (var5 != null && var5.total() == var2) {
            return var5.text();
         } else {
            MediaGlyphs.Stamp var6 = new MediaGlyphs.Stamp(var2, String.format("%d:%02d", var2 / 60L, var2 % 60L));
            if (stampTurn) {
               stampB = var6;
            } else {
               stampA = var6;
            }

            stampTurn = !stampTurn;
            return var6.text();
         }
      }
   }

   public static String ellipsize(Font var0, String var1, float var2) {
      MediaGlyphs.EllipsisKey var3 = new MediaGlyphs.EllipsisKey(var0.getFont(), var0.getSize(), var2, var1);
      String var4 = ELLIPSIZED.get(var3);
      if (var4 != null) {
         return var4;
      } else {
         String var5 = computeEllipsis(var0, var1, var2);
         ELLIPSIZED.put(var3, var5);
         return var5;
      }
   }

   private static String computeEllipsis(Font var0, String var1, float var2) {
      if (var0.width(var1) <= var2) {
         return var1;
      } else {
         String var3 = "...";
         float var4 = var0.width(var3);
         int var5 = var1.length();

         while (var5 > 0 && var0.width(var1.substring(0, var5)) + var4 > var2) {
            var5--;
         }

         return var1.substring(0, var5).stripTrailing() + var3;
      }
   }

   private static final class EllipsisKey {
      private final Object font;
      private final float size;
      private final float maxWidth;
      private final String text;

      public Object font() {
         return this.font;
      }

      public float size() {
         return this.size;
      }

      public float maxWidth() {
         return this.maxWidth;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof EllipsisKey other)) return false;
         return java.util.Objects.equals(this.font, other.font) && java.util.Objects.equals(this.size, other.size) && java.util.Objects.equals(this.maxWidth, other.maxWidth) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(font, size, maxWidth, text);
      }

      @Override
      public String toString() {
         return "EllipsisKey[" + "font=" + this.font + ", " + "size=" + this.size + ", " + "maxWidth=" + this.maxWidth + ", " + "text=" + this.text + "]";
      }

      private EllipsisKey(Object var1, float var2, float var3, String var4) {
         this.font = var1;
         this.size = var2;
         this.maxWidth = var3;
         this.text = var4;
      }
   }

   private static final class Stamp {
      private final long total;
      private final String text;

      public long total() {
         return this.total;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Stamp other)) return false;
         return java.util.Objects.equals(this.total, other.total) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(total, text);
      }

      @Override
      public String toString() {
         return "Stamp[" + "total=" + this.total + ", " + "text=" + this.text + "]";
      }

      private Stamp(long var1, String var3) {
         this.total = var1;
         this.text = var3;
      }
   }
}

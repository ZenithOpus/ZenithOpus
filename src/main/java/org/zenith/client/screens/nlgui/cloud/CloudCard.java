package org.zenith.client.screens.nlgui.cloud;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.Identifier;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class CloudCard {
   private static final float PAD = 4.0F;
   private static final float TITLE_SIZE = 5.0F;
   private static final float META_SIZE = 4.2F;
   private static final float BADGE_HEIGHT = 8.0F;
   private static final float PILL_HEIGHT = 10.0F;
   private static final String DOWNLOAD_LABEL = "gui.configs.downloadShort";
   private static final ZenithColor[] TINTS = new ZenithColor[]{
      new ZenithColor("#8E9BFF"),
      new ZenithColor("#63C9FF"),
      new ZenithColor("#7FE0B4"),
      new ZenithColor("#FFC46B"),
      new ZenithColor("#FF97A0"),
      new ZenithColor("#C89BFF")
   };
   private final Animation hoverAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private final Animation selectAnimation = new Animation(160L, 0.0F, Easing.INSTANCE12);
   private final Animation previewFade = new Animation(260L, 0.0F, Easing.INSTANCE12);
   private final Animation likeAnimation = new Animation(260L, 0.0F, Easing.INSTANCE12);
   private final Animation downloadAnimation = new Animation(120L, 0.0F, Easing.INSTANCE12);
   private boolean downloadHot;
   private boolean likeReady;
   private int likesFor = Integer.MIN_VALUE;
   private String likesText;
   private static final Map<CloudCard.SizeKey, String> SIZES = Collections.synchronizedMap(new LinkedHashMap<CloudCard.SizeKey, String>(64, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<CloudCard.SizeKey, String> var1) {
         return this.size() > 256;
      }
   });
   private static final Map<CloudCard.FitKey, String> FITS = Collections.synchronizedMap(new LinkedHashMap<CloudCard.FitKey, String>(64, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<CloudCard.FitKey, String> var1) {
         return this.size() > 512;
      }
   });

   public static float previewHeight(float var0) {
      return (var0 - 8.0F) * 9.0F / 16.0F;
   }

   public static float height(float var0) {
      Font var1 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var2 = Fonts.NEW_REGULAR.getFont(4.2F);
      return 4.0F + previewHeight(var0) + 5.0F + var1.height() + 3.0F + var2.height() + 4.0F;
   }

   public boolean downloadHovered() {
      return this.downloadHot;
   }

   public void render(
      MenuDrawContext var1,
      int var2,
      int var3,
      StoreApi.Data2 var4,
      float var5,
      float var6,
      float var7,
      boolean var8,
      boolean var9,
      float var10,
      ZenithStyle var11
   ) {
      float var12 = height(var7);
      float var13 = previewHeight(var7);
      float var14 = this.hoverAnimation.method2808(var9 ? 1.0F : 0.0F);
      float var15 = this.selectAnimation.method2808(var8 ? 1.0F : 0.0F);
      ZenithColor var16 = var11.getPrimaryColor().getZenithColor();
      ProxyDotRadiusData var17 = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.75F);
      var1.drawRoundedRect(
         var5,
         var6,
         var7,
         var12,
         var17,
         var11.getFieldSurfaceBackground()
            .getZenithColor()
            .method7295(var11.getDisableActiveBg().getZenithColor(), Math.max(var14 * 0.7F, var15))
            .method7294(var10)
      );
      var1.drawRoundedBorder(
         var5,
         var6,
         var7,
         var12,
         0.15F,
         var17,
         var11.getFieldBorder().getZenithColor().method7294(2.0F).method7295(var16.method7292(0.4F), Math.max(var15, var14 * 0.45F)).method7294(var10)
      );
      this.renderPreview(var1, var4, var5 + 4.0F, var6 + 4.0F, var7 - 8.0F, var13, var10, var11);
      this.renderBadge(var1, var4, var5 + 4.0F + 3.0F, var6 + 4.0F + 3.0F, var10, var11);
      this.renderDownload(var1, var2, var3, var5 + 4.0F, var6 + 4.0F, var7 - 8.0F, var13, var14, var10, var11);
      Font var18 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var19 = Fonts.NEW_REGULAR.getFont(4.2F);
      float var20 = var6 + 4.0F + var13 + 5.0F;
      ZenithColor var21 = var11.getTextSecondary()
         .getZenithColor()
         .method7295(var11.getTextEnable().getZenithColor(), Math.max(var15, var14 * 0.6F))
         .method7294(var10);
      var1.drawText(var18, fit(var18, var4.name(), var7 - 8.0F - 2.0F), var5 + 4.0F + 1.0F, var20, var21);
      float var22 = var20 + var18.height() + 3.0F;
      if (this.likesText == null || this.likesFor != var4.getIntValue()) {
         this.likesFor = var4.getIntValue();
         this.likesText = String.valueOf(this.likesFor);
      }

      String var23 = this.likesText;
      float var24 = 8.0F + var19.width(var23);
      if (!this.likeReady) {
         this.likeAnimation.method2809(var4.isFlag3() ? 1.0F : 0.0F);
         this.likeReady = true;
      }

      float var25 = this.likeAnimation.method2808(var4.isFlag3() ? 1.0F : 0.0F);
      ZenithColor var26 = var11.getTextTertiary().getZenithColor().method7295(var11.getHeartIcon().getZenithColor(), var25);
      float var27 = var5 + var7 - 4.0F - 1.0F - var24;
      float var28 = var22 + (var19.height() - 6.0F) / 2.0F;
      float var29 = 1.0F + 0.35F * (float)Math.sin(Math.PI * var25) * (var4.isFlag3() ? 1.0F : 0.6F);
      var1.pushMatrix();
      var1.getMatrices().translate(var27 + 3.0F, var28 + 3.0F);
      var1.getMatrices().scale(var29, var29);
      MenuGlyph.HEART.draw(var1, -3.0F, -3.0F, var26.method7294(var10));
      var1.popMatrix();
      var1.drawText(var19, var23, var5 + var7 - 4.0F - 1.0F - var19.width(var23), var22, var26.method7294(var10));
      String var30 = var4.getText8() != null && !var4.getText8().isBlank() ? var4.getText8() : size(var4.getLongValue());
      var1.drawText(var19, fit(var19, var30, var7 - 8.0F - var24 - 6.0F), var5 + 4.0F + 1.0F, var22, var11.getTextTertiary().getZenithColor().method7294(var10));
   }

   private void renderPreview(MenuDrawContext var1, StoreApi.Data2 var2, float var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      ProxyDotRadiusData var9 = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.5F);
      Identifier var10 = var2.hasPreview() ? CloudPreviewTextureCache.get(var2.getUUID(), var2.getText6()) : null;
      ZenithColor var11 = TINTS[Math.floorMod(nameSeed(var2.name()), TINTS.length)];
      if (var10 != null) {
         float var15 = this.previewFade.method2808(1.0F);
         if (var15 < 1.0F) {
            var1.drawRoundedRect(
               var3,
               var4,
               var5,
               var6,
               var9,
               var8.getSurfaceEnableBackground().getZenithColor().method7295(var11, 0.09F).method7294(var7 * 2.0F * (1.0F - var15))
            );
         }

         var1.drawRoundedTexture(var10, var3, var4, var5, var6, var9, new ZenithColor(255, 255, 255).method7294(var7 * var15));
      } else {
         var1.drawRoundedRect(var3, var4, var5, var6, var9, var8.getSurfaceEnableBackground().getZenithColor().method7295(var11, 0.09F).method7294(var7 * 2.0F));
         if (var2.hasPreview()) {
            Font var14 = Fonts.NEW_REGULAR.getFont(4.2F);
            float var16 = 0.55F + 0.45F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 260.0));
            var1.drawText(
               var14,
               "…",
               var3 + (var5 - var14.width("…")) / 2.0F,
               var4 + (var6 - var14.height()) / 2.0F,
               var8.getTextTertiary().getZenithColor().method7294(var7 * var16)
            );
         } else {
            Font var12 = Fonts.NEW_SEMIBOLD.getFont(Math.max(9.0F, var6 * 0.36F));
            String var13 = initial(var2.name());
            var1.drawText(
               var12,
               var13,
               var3 + (var5 - var12.width(var13)) / 2.0F,
               var4 + (var6 - var12.height()) / 2.0F,
               var8.getTextTertiary().getZenithColor().method7294(var7 * 0.55F)
            );
         }
      }
   }

   private void renderBadge(MenuDrawContext var1, StoreApi.Data2 var2, float var3, float var4, float var5, ZenithStyle var6) {
      if ("script".equals(var2.kind())) {
         Font var7 = Fonts.NEW_MEDIUM.getFont(4.0F);
         String var8 = t("gui.configs.chip.script");
         float var9 = var7.width(var8) + 8.0F;
         var1.drawRoundedRect(var3, var4, var9, 8.0F, ProxyDotRadiusData.method8069(4.0F), var6.getRightBackground().getZenithColor().method7294(var5));
         var1.drawText(var7, var8, var3 + 4.0F, var4 + (8.0F - var7.height()) / 2.0F, var6.getTextSecondary().getZenithColor().method7294(var5));
      }
   }

   private void renderDownload(
      MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, float var9, ZenithStyle var10
   ) {
      Font var11 = Fonts.NEW_MEDIUM.getFont(4.2F);
      float var12 = var11.width(t("gui.configs.downloadShort")) + 10.0F;
      float var13 = var4 + var6 - 3.0F - var12;
      float var14 = var5 + var7 - 3.0F - 10.0F;
      this.downloadHot = var8 > 0.35F && new BoundsData(var13, var14, var12, 10.0F).contains(var2, var3);
      float var15 = this.downloadAnimation.method2808(this.downloadHot ? 1.0F : 0.0F);
      if (!(var8 <= 0.01F)) {
         ZenithColor var16 = var10.getPrimaryColor().getZenithColor();
         ProxyDotRadiusData var17 = ProxyDotRadiusData.method8069(5.0F);
         float var18 = (1.0F - var8) * 4.0F;
         var1.drawRoundedRect(var13, var14 + var18, var12, 10.0F, var17, var10.getRightBackground().getZenithColor().method7294(var9 * var8));
         var1.drawRoundedBorder(var13, var14 + var18, var12, 10.0F, 0.15F, var17, var16.method7292(0.22F + var15 * 0.28F).method7294(var9 * var8));
         var1.drawText(
            var11,
            t("gui.configs.downloadShort"),
            var13 + 5.0F,
            var14 + var18 + (10.0F - var11.height()) / 2.0F,
            var16.method7294(var9 * var8 * (0.8F + var15 * 0.2F))
         );
      }
   }

   private static int nameSeed(String var0) {
      return var0 == null ? 0 : var0.hashCode();
   }

   private static String initial(String var0) {
      return var0 != null && !var0.isBlank() ? var0.trim().substring(0, 1).toUpperCase(Locale.ROOT) : "?";
   }

   private static String t(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   public static String size(long var0) {
      CloudCard.SizeKey var2 = new CloudCard.SizeKey(var0, Zenith.getInstance().getLocalization().getLanguageName());
      String var3 = SIZES.get(var2);
      if (var3 != null) {
         return var3;
      } else {
         String var4 = var0 < 1024L ? var0 + " " + t("gui.unit.byte") : String.format(Locale.US, "%.1f", (float)var0 / 1024.0F) + " " + t("gui.unit.kb");
         SIZES.put(var2, var4);
         return var4;
      }
   }

   public static String fit(Font var0, String var1, float var2) {
      if (var1 != null && !(var2 <= 0.0F)) {
         if (var0.width(var1) <= var2) {
            return var1;
         } else {
            CloudCard.FitKey var3 = new CloudCard.FitKey(var1, var0, var2);
            String var4 = FITS.get(var3);
            if (var4 != null) {
               return var4;
            } else {
               String var5 = var0.clip(var1, var2);
               FITS.put(var3, var5);
               return var5;
            }
         }
      } else {
         return "";
      }
   }

   private static final class FitKey {
      private final String text;
      private final Font font;
      private final float room;

      public String text() {
         return this.text;
      }

      public Font font() {
         return this.font;
      }

      public float room() {
         return this.room;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof FitKey other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.font, other.font) && java.util.Objects.equals(this.room, other.room);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, font, room);
      }

      @Override
      public String toString() {
         return "FitKey[" + "text=" + this.text + ", " + "font=" + this.font + ", " + "room=" + this.room + "]";
      }

      private FitKey(String var1, Font var2, float var3) {
         this.text = var1;
         this.font = var2;
         this.room = var3;
      }
   }

   private static final class SizeKey {
      private final long bytes;
      private final String language;

      public long bytes() {
         return this.bytes;
      }

      public String language() {
         return this.language;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof SizeKey other)) return false;
         return java.util.Objects.equals(this.bytes, other.bytes) && java.util.Objects.equals(this.language, other.language);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(bytes, language);
      }

      @Override
      public String toString() {
         return "SizeKey[" + "bytes=" + this.bytes + ", " + "language=" + this.language + "]";
      }

      private SizeKey(long var1, String var3) {
         this.bytes = var1;
         this.language = var3;
      }
   }
}

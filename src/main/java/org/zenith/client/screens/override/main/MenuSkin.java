package org.zenith.client.screens.override.main;

import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class MenuSkin {
   public static final float HEADER_HEIGHT = MenuTheme.px(42.0F);
   public static final float ROW_HEIGHT = MenuTheme.px(31.0F);
   public static final float TILE_INSET = MenuTheme.px(7.0F);
   public static final float HEADER_TILE = HEADER_HEIGHT - TILE_INSET * 2.0F;
   public static final float ROW_TILE = MenuTheme.px(21.0F);
   public static final float EDGE = MenuTheme.px(12.0F);
   public static final float GAP = MenuTheme.px(6.0F);
   public static final float LIST_PAD = MenuTheme.px(10.0F);

   private MenuSkin() {
   }

   public static float radius(float var0) {
      float var1 = InterfaceModule.method6801() * 2.0F;
      return Math.max(MenuTheme.px(4.0F), Math.min(var1, var0 / 2.0F));
   }

   public static float tileRadius(float var0) {
      return var0 * 0.3F;
   }

   public static float dividerWidth() {
      return GAP * 2.0F + 1.0F;
   }

   public static void panel(MenuDrawContext var0, float var1, float var2, float var3, float var4) {
      panel(var0, var1, var2, var3, var4, radius(var4));
   }

   public static void panel(MenuDrawContext var0, float var1, float var2, float var3, float var4, float var5) {
      ProxyDotRadiusData var6 = ProxyDotRadiusData.method8069(var5);
      MenuSurface.blur(var0, var1, var2, var3, var4, var6, 1.0F);
      var0.drawRoundedRect(var1, var2, var3, var4, var6, MenuTheme.panel());
   }

   public static void plate(MenuDrawContext var0, float var1, float var2, float var3, float var4, ZenithColor var5) {
      var0.drawRoundedRect(var1, var2, var3, var4, ProxyDotRadiusData.method8069(radius(var4)), var5);
   }

   public static float divider(MenuDrawContext var0, float var1, float var2, float var3) {
      float var4 = var3 * 0.45F;
      var0.drawRect(var1 + GAP, var2 + (var3 - var4) / 2.0F, 1.0F, var4, MenuTheme.textTertiary().method7294(0.45F));
      return var1 + dividerWidth();
   }

   public static void accentTile(MenuDrawContext var0, float var1, float var2, float var3, MenuIcon var4) {
      var0.drawRoundedRect(var1, var2, var3, var3, ProxyDotRadiusData.method8069(tileRadius(var3)), MenuTheme.accent());
      iconCentered(var0, var4, var4.vector() ? var3 * 1.25F : var3 * 0.5F, var1, var2, var3, MenuTheme.onAccent());
   }

   public static void mutedTile(MenuDrawContext var0, float var1, float var2, float var3, MenuIcon var4, ZenithColor var5) {
      var0.drawRoundedRect(var1, var2, var3, var3, ProxyDotRadiusData.method8069(tileRadius(var3)), MenuTheme.plate());
      if (var4 != null) {
         iconCentered(var0, var4, var4.vector() ? var3 * 1.25F : var3 * 0.5F, var1, var2, var3, var5);
      }
   }

   public static void iconCentered(MenuDrawContext var0, MenuIcon var1, float var2, float var3, float var4, float var5, ZenithColor var6) {
      Font var7 = var1.font(var2);
      String var8 = var1.glyph();
      float[] var9 = var7.inkBounds(var8);
      if (var9 == null) {
         var0.drawText(var7, var8, var3 + (var5 - var7.width(var8)) / 2.0F, var4 + (var5 - var7.height()) / 2.0F, var6);
      } else {
         var0.drawText(var7, var8, var3 + (var5 - var9[0]) / 2.0F, var4 + (var5 - var9[1]) / 2.0F - var7.baseline() + var9[2], var6);
      }
   }

   public static float header(MenuDrawContext var0, float var1, float var2, MenuIcon var3, String var4) {
      float var5 = var2 + TILE_INSET;
      accentTile(var0, var1 + TILE_INSET, var5, HEADER_TILE, var3);
      float var6 = divider(var0, var1 + TILE_INSET + HEADER_TILE, var2, HEADER_HEIGHT);
      Font var7 = MenuTheme.medium(12.0F);
      var0.drawText(var7, var4, var6, var2 + (HEADER_HEIGHT - var7.height()) / 2.0F, MenuTheme.text());
      return var6 + var7.width(var4);
   }

   public static float headerWidth(String var0) {
      return TILE_INSET + HEADER_TILE + dividerWidth() + MenuTheme.medium(12.0F).width(var0) + EDGE;
   }

   public static BoundsData listArea(MenuDrawContext var0, BoundsData var1, float var2) {
      panel(var0, var1.getFloatValue(), var1.getFloatValue2(), var1.width(), var2);
      return new BoundsData(var1.getFloatValue() + LIST_PAD, var1.getFloatValue2() + LIST_PAD, var1.width() - LIST_PAD * 2.0F, var2 - LIST_PAD * 2.0F);
   }

   public static float stagger(float var0, int var1) {
      float var2 = Math.min(0.55F, var1 * 0.07F);
      return Math.clamp((var0 - var2) / Math.max(0.05F, 1.0F - var2), 0.0F, 1.0F);
   }

   public static float enterOffset(float var0, int var1) {
      float var2 = stagger(var0, var1);
      return (1.0F - var2) * MenuTheme.px(14.0F);
   }

   public static float valuePillWidth(String var0) {
      return Math.max(ROW_TILE, MenuTheme.semibold(10.0F).width(var0) + MenuTheme.px(10.0F));
   }

   public static float valuePill(MenuDrawContext var0, float var1, float var2, float var3, String var4) {
      Font var5 = MenuTheme.semibold(10.0F);
      float var6 = valuePillWidth(var4);
      float var7 = ROW_TILE;
      float var8 = var1 - var6;
      float var9 = var2 + (var3 - var7) / 2.0F;
      var0.drawRoundedRect(var8, var9, var6, var7, ProxyDotRadiusData.method8069(tileRadius(var7)), MenuTheme.plate());
      var0.drawText(var5, var4, var8 + (var6 - var5.width(var4)) / 2.0F, var9 + (var7 - var5.height()) / 2.0F, MenuTheme.text());
      return var8;
   }
}

package org.zenith.client.screens.override.main.component;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuSurface;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class VanillaOptionStyle {
   private static final float PADDING = MenuTheme.px(14.0F);
   private static final float TRACK_HEIGHT = MenuTheme.px(4.0F);
   private static final float KNOB_SIZE = MenuTheme.px(6.0F);
   private static final float LABEL_SIZE = 12.0F;
   private static boolean active;

   private VanillaOptionStyle() {
   }

   public static boolean isActive() {
      return active;
   }

   public static void begin() {
      active = true;
   }

   public static void end() {
      active = false;
   }

   public static void drawButton(ClickableWidget var0, DrawContext var1, int var2, int var3, float var4) {
      MenuDrawContext var5 = MenuDrawContext.method8080(var1, var2, var3, var4);
      float var6 = var0.getX();
      float var7 = var0.getY();
      float var8 = var0.getWidth();
      float var9 = var0.getHeight();
      boolean var10 = var0.isHovered() || var0.isFocused();
      float var11 = MenuSkin.radius(var9);
      ProxyDotRadiusData var12 = ProxyDotRadiusData.method8069(var11);
      ZenithColor var13 = MenuTheme.accent();
      ZenithColor var14 = var10 ? MenuTheme.plate().method7295(var13, 0.75F) : MenuTheme.plate();
      if (var10) {
         MenuSurface.glow(var5, var6, var7, var8, var9, var11, var13, 0.3F);
      }

      var5.drawRoundedRect(var6, var7, var8, var9, var12, var14);
      Font var15 = MenuTheme.medium(12.0F);
      String var16 = var0.getMessage().getString();
      ZenithColor var17 = var0.active ? (var10 ? MenuTheme.onAccent() : MenuTheme.textSecondary()) : MenuTheme.textTertiary();
      drawClipped(var5, var15, var16, var6, var7, var8, var9, var17);
   }

   public static void drawSlider(ClickableWidget var0, double var1, DrawContext var3, int var4, int var5, float var6) {
      MenuDrawContext var7 = MenuDrawContext.method8080(var3, var4, var5, var6);
      float var8 = var0.getX();
      float var9 = var0.getY();
      float var10 = var0.getWidth();
      float var11 = var0.getHeight();
      boolean var12 = var0.isHovered() || var0.isFocused();
      ProxyDotRadiusData var13 = ProxyDotRadiusData.method8069(MenuSkin.radius(var11));
      var7.drawRoundedRect(var8, var9, var10, var11, var13, MenuTheme.plate().method7295(MenuTheme.track(), var12 ? 0.6F : 0.0F));
      float var14 = var8 + PADDING;
      float var15 = Math.max(0.0F, var10 - PADDING * 2.0F);
      float var16 = var9 + var11 - TRACK_HEIGHT - MenuTheme.px(6.0F);
      ProxyDotRadiusData var17 = ProxyDotRadiusData.method8069(TRACK_HEIGHT / 2.0F);
      var7.drawRoundedRect(var14, var16, var15, TRACK_HEIGHT, var17, MenuTheme.track());
      float var18 = (float)(var15 * Math.min(1.0, Math.max(0.0, var1)));
      if (var18 > 0.0F) {
         var7.drawRoundedRect(var14, var16, var18, TRACK_HEIGHT, var17, MenuTheme.accent());
      }

      var7.drawRoundedRect(
         var14 + var18 - KNOB_SIZE / 2.0F,
         var16 + (TRACK_HEIGHT - KNOB_SIZE) / 2.0F,
         KNOB_SIZE,
         KNOB_SIZE,
         ProxyDotRadiusData.method8069(KNOB_SIZE / 2.0F),
         ZenithColor.INSTANCE
      );
      Font var19 = MenuTheme.medium(12.0F);
      String var20 = var0.getMessage().getString();
      var7.enableScissor(var8 + PADDING, var9, var8 + var10 - PADDING, var9 + var11);
      var7.drawText(var19, var20, var8 + PADDING, var9 + MenuTheme.px(6.0F), MenuTheme.text());
      var7.disableScissor();
   }

   private static void drawClipped(MenuDrawContext var0, Font var1, String var2, float var3, float var4, float var5, float var6, ZenithColor var7) {
      float var8 = var1.width(var2);
      float var9 = var8 > var5 - PADDING * 2.0F ? var3 + PADDING : var3 + (var5 - var8) / 2.0F;
      var0.enableScissor(var3 + MenuTheme.px(4.0F), var4, var3 + var5 - MenuTheme.px(4.0F), var4 + var6);
      var0.drawText(var1, var2, var9, var4 + (var6 - var1.height()) / 2.0F, var7);
      var0.disableScissor();
   }
}

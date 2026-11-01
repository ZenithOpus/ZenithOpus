package org.zenith.client.screens.override.main.widget;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import lombok.Generated;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuSurface;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class MenuSlider extends MenuWidget {
   public static final float HEIGHT = MenuSkin.HEADER_HEIGHT;
   private static final float RADIUS = MenuSkin.radius(HEIGHT);
   private static final float PADDING = MenuTheme.px(16.0F);
   private static final float GAP = MenuTheme.px(16.0F);
   private static final float TRACK_HEIGHT = MenuTheme.px(4.0F);
   private static final float KNOB_SIZE = MenuTheme.px(6.0F);
   private static final float TEXT_SIZE = 12.0F;
   private final String label;
   private final DoubleSupplier min;
   private final DoubleSupplier max;
   private final double step;
   private final DoubleSupplier getter;
   private final DoubleConsumer setter;
   private final Function<Double, String> formatter;
   private boolean dragging;

   public MenuSlider(String var1, double var2, double var4, double var6, DoubleSupplier var8, DoubleConsumer var9, Function<Double, String> var10) {
      this(var1, () -> var2, () -> var4, var6, var8, var9, var10);
   }

   public MenuSlider(
      String var1, DoubleSupplier var2, DoubleSupplier var3, double var4, DoubleSupplier var6, DoubleConsumer var7, Function<Double, String> var8
   ) {
      this.label = var1;
      this.min = var2;
      this.max = var3;
      this.step = var4;
      this.getter = var6;
      this.setter = var7;
      this.formatter = var8;
   }

   private double value() {
      return this.getter.getAsDouble();
   }

   private float progress() {
      double var1 = this.min.getAsDouble();
      double var3 = this.max.getAsDouble() - var1;
      return var3 <= 0.0 ? 0.0F : (float)Math.min(1.0, Math.max(0.0, (this.value() - var1) / var3));
   }

   @Override
   public void draw(MenuDrawContext var1, float var2, float var3) {
      MenuSurface.plate(var1, this.x, this.y, this.width, this.height, RADIUS, MenuTheme.plate());
      Font var4 = MenuTheme.medium(12.0F);
      float var5 = this.y + (this.height - var4.height()) / 2.0F;
      String var6 = this.formatter.apply(this.value());
      float var7 = var4.width(var6);
      var1.drawText(var4, this.label, this.x + PADDING, var5, MenuTheme.text());
      var1.drawText(var4, var6, this.x + this.width - PADDING - var7, var5, MenuTheme.text());
      float var8 = this.trackStart();
      float var9 = this.trackEnd();
      float var10 = Math.max(0.0F, var9 - var8);
      float var11 = this.y + (this.height - TRACK_HEIGHT) / 2.0F;
      ProxyDotRadiusData var12 = ProxyDotRadiusData.method8069(TRACK_HEIGHT / 2.0F);
      var1.drawRoundedRect(var8, var11, var10, TRACK_HEIGHT, var12, MenuTheme.track());
      float var13 = var10 * this.progress();
      if (var13 > 0.0F) {
         var1.drawRoundedRect(var8, var11, var13, TRACK_HEIGHT, var12, MenuTheme.accent());
      }

      float var14 = var8 + var13 - KNOB_SIZE / 2.0F;
      float var15 = this.y + (this.height - KNOB_SIZE) / 2.0F;
      var1.drawRoundedRect(var14, var15, KNOB_SIZE, KNOB_SIZE, ProxyDotRadiusData.method8069(KNOB_SIZE / 2.0F), ZenithColor.INSTANCE);
      if (this.dragging) {
         Font var16 = MenuTheme.semibold(9.0F);
         String var17 = trimNumber(this.value());
         float var18 = var16.width(var17) + MenuTheme.px(8.0F);
         float var19 = MenuTheme.px(14.0F);
         float var20 = Math.min(Math.max(var8, var14 + KNOB_SIZE / 2.0F - var18 / 2.0F), var9 - var18);
         float var21 = this.y + MenuTheme.px(2.0F);
         var1.drawRoundedRect(var20, var21, var18, var19, ProxyDotRadiusData.method8069(MenuTheme.px(4.0F)), MenuTheme.accent());
         var1.drawText(var16, var17, var20 + (var18 - var16.width(var17)) / 2.0F, var21 + (var19 - var16.height()) / 2.0F, MenuTheme.onAccent());
      }
   }

   private static String trimNumber(double var0) {
      return var0 == Math.rint(var0) ? String.valueOf((long)var0) : String.format("%.1f", var0);
   }

   private float trackStart() {
      return this.x + PADDING + MenuTheme.medium(12.0F).width(this.label) + GAP;
   }

   private float trackEnd() {
      Font var1 = MenuTheme.medium(12.0F);
      return this.x + this.width - PADDING - var1.width(this.formatter.apply(this.value())) - GAP;
   }

   private void applyFromMouse(double var1) {
      float var3 = this.trackStart();
      float var4 = Math.max(1.0F, this.trackEnd() - var3);
      double var5 = this.min.getAsDouble();
      double var7 = this.max.getAsDouble();
      double var9 = Math.min(1.0, Math.max(0.0, (var1 - var3) / var4));
      double var11 = var5 + var9 * (var7 - var5);
      if (this.step > 0.0) {
         var11 = Math.round(var11 / this.step) * this.step;
      }

      this.setter.accept(Math.min(var7, Math.max(var5, var11)));
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.visible && this.enabled && var5 == 0 && this.contains(var1, var3)) {
         this.dragging = true;
         this.applyFromMouse(var1);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseDragged(double var1, double var3, int var5) {
      if (!this.dragging) {
         return false;
      } else {
         this.applyFromMouse(var1);
         return true;
      }
   }

   @Override
   public boolean mouseReleased(double var1, double var3, int var5) {
      if (!this.dragging) {
         return false;
      } else {
         this.dragging = false;
         return true;
      }
   }

   @Generated
   public void setDragging(boolean var1) {
      this.dragging = var1;
   }
}

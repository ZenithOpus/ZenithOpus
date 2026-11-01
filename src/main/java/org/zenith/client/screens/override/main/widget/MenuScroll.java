package org.zenith.client.screens.override.main.widget;

import lombok.Generated;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class MenuScroll {
   private static final float RAIL_THICKNESS = MenuTheme.px(2.0F);
   private final Animation smooth = new Animation(220L, 0.0F, Easing.INSTANCE12);
   private float target;
   private float viewport;
   private float content;

   public void update(float var1, float var2) {
      this.viewport = var1;
      this.content = var2;
      this.target = Math.min(this.target, Math.max(0.0F, var2 - var1));
      this.target = Math.max(0.0F, this.target);
      this.smooth.method2808(this.target);
   }

   public float offset() {
      return this.smooth.getValue();
   }

   public boolean scrollable() {
      return this.content > this.viewport + 0.5F;
   }

   public boolean scroll(double var1, float var3) {
      if (!this.scrollable()) {
         return false;
      } else {
         this.target = Math.min(Math.max(0.0F, this.content - this.viewport), Math.max(0.0F, this.target - (float)var1 * var3));
         return true;
      }
   }

   public void reset() {
      this.target = 0.0F;
      this.smooth.method2809(0.0F);
   }

   public void renderRail(MenuDrawContext var1, float var2, float var3, float var4) {
      if (this.scrollable()) {
         ProxyDotRadiusData var5 = ProxyDotRadiusData.method8069(RAIL_THICKNESS / 2.0F);
         var1.drawRoundedRect(var2, var3, RAIL_THICKNESS, var4, var5, MenuTheme.surface());
         float var6 = Math.min(1.0F, this.viewport / this.content);
         float var7 = Math.max(MenuTheme.px(16.0F), var4 * var6);
         float var8 = var4 - var7;
         float var9 = this.content - this.viewport <= 0.0F ? 0.0F : this.offset() / (this.content - this.viewport);
         var1.drawRoundedRect(var2, var3 + var8 * var9, RAIL_THICKNESS, var7, var5, MenuTheme.border());
      }
   }

   public void renderRailHorizontal(MenuDrawContext var1, float var2, float var3, float var4) {
      if (this.scrollable()) {
         ProxyDotRadiusData var5 = ProxyDotRadiusData.method8069(RAIL_THICKNESS / 2.0F);
         var1.drawRoundedRect(var2, var3, var4, RAIL_THICKNESS, var5, MenuTheme.surface());
         float var6 = Math.min(1.0F, this.viewport / this.content);
         float var7 = Math.max(MenuTheme.px(16.0F), var4 * var6);
         float var8 = var4 - var7;
         float var9 = this.content - this.viewport <= 0.0F ? 0.0F : this.offset() / (this.content - this.viewport);
         var1.drawRoundedRect(var2 + var8 * var9, var3, var7, RAIL_THICKNESS, var5, MenuTheme.border());
      }
   }

   @Generated
   public float getViewport() {
      return this.viewport;
   }

   @Generated
   public float getContent() {
      return this.content;
   }
}

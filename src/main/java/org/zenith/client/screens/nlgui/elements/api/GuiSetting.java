package org.zenith.client.screens.nlgui.elements.api;

import lombok.Generated;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.display.base.MenuDrawContext;

public abstract class GuiSetting<T extends Setting> extends Element {
   private static final float MIN_DESC_SCROLL_SPEED = 0.028F;
   private static final float MAX_DESC_SCROLL_SPEED = 0.065F;
   private static final float TARGET_DESC_CHARS_PER_SECOND = 12.0F;
   public final float width;
   public final T setting;
   public final Animation animationVisible;
   private float scrollOffset = 0.0F;
   private int scrollDirection = 1;
   private long hoverStartTime = -1L;
   private long lastUpdateTime = -1L;
   private long pauseUntilTime = -1L;
   private boolean continueScroll = false;

   public GuiSetting(float var1, T var2) {
      this.width = var1;
      this.setting = (T)var2;
      this.animationVisible = new Animation(200L, var2.isVisible() ? 1.0F : 0.0F, Easing.INSTANCE10);
   }

   @Override
   public boolean isVisible() {
      return this.animationVisible.getValue() != 0.0F || this.setting.isVisible();
   }

   @Override
   public float getHeight() {
      return this.rowHeight();
   }

   public float rowHeight() {
      return this.isShort() ? 7.0F : 14.0F;
   }

   public float getVisibleProgress() {
      return this.animationVisible.getValue();
   }

   public float getAnimHeight() {
      return this.getHeight() * this.animationVisible.getValue();
   }

   public boolean isShort() {
      return this.setting.getDescription().isEmpty() || Zenith.getInstance().getNLMenuScreen().isShortMode();
   }

   public boolean onMousePriorityScroll(double var1, double var3, double var5, double var7) {
      return false;
   }

   public boolean onMousePriorityClicked(double var1, double var3, Enum3 var5) {
      return false;
   }

   public void renderPriority(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7) {
   }

   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
   }

   public void drawDefault(
      MenuDrawContext var1,
      float var2,
      float var3,
      String var4,
      String var5,
      String var6,
      Font var7,
      Font var8,
      float var9,
      float var10,
      float var11,
      ZenithColor var12,
      ZenithColor var13,
      ZenithColor var14
   ) {
      float var15 = var7.getSize() - 0.5F;
      if (var15 == 5.0F) {
         var15 = 5.01F;
      }

      Font var16 = Fonts.NEW_ICONS.getFont(var15);
      boolean var17 = Zenith.getInstance().getNLMenuScreen().isRenderIcon();
      float var18 = this.rowHeight();
      float var19 = var17 ? var16.width(var4) + GuiStyle.PADDING.intValue() / 2.0F : 0.0F;
      String var20 = var7.clip(var5, var11 - var19);
      if (this.isShort()) {
         if (var17) {
            var1.drawText(var16, var4, var9, var10 + (var18 - var16.height()) / 2.0F, var14);
         }

         var1.drawText(var7, var20, var9 + var19, var10 + (var18 - var7.height()) / 2.0F, var12);
      } else {
         if (var17) {
            var1.drawText(var16, var4, var9, var10 + (var16.height() / 2.0F - (var4.equals("u") ? 0.0F : 0.2F)), var14);
         }

         var1.drawText(var7, var20, var9 + var19, var10 + 1.1F, var12);
         float var21 = var10 + var18 - var8.height() - 1.0F;
         float var22 = var8.width(var6);
         float var23 = var22 - var11;
         boolean var24 = MathUtils.method2647(var2, var3, var9, var21 - 3.0F, var11 + 1.0F, var8.height() + 10.0F);
         if (var23 <= 1.0F) {
            var1.drawText(var8, var6, var9, var21, var13);
            this.resetDescScroll();
         } else {
            var1.enableScissor(var9 - 0.5F, var21, var9 + var11 + 2.0F, var21 + var8.height() + 4.0F);
            long var25 = System.currentTimeMillis();
            if (this.lastUpdateTime == -1L) {
               this.lastUpdateTime = var25;
            }

            float var27 = (float)(var25 - this.lastUpdateTime);
            this.lastUpdateTime = var25;
            if (var24) {
               if (this.hoverStartTime == -1L) {
                  this.hoverStartTime = var25;
                  this.pauseUntilTime = var25 + 300L;
               }

               this.continueScroll = true;
            } else if (this.continueScroll && this.scrollOffset <= 0.0F && this.scrollDirection == 1) {
               this.continueScroll = false;
            }

            if (!var24 && !this.continueScroll) {
               this.hoverStartTime = -1L;
               this.pauseUntilTime = -1L;
            } else if (this.pauseUntilTime == -1L || var25 >= this.pauseUntilTime) {
               this.pauseUntilTime = -1L;
               float var28 = var23 + 10.0F;
               float var29 = this.getDescScrollSpeed(var22, var6);
               this.scrollOffset = this.scrollOffset + this.scrollDirection * var29 * var27;
               if (this.scrollOffset >= var28) {
                  this.scrollOffset = var28;
                  this.scrollDirection = -1;
                  this.pauseUntilTime = var25 + 900L;
               } else if (this.scrollOffset <= 0.0F) {
                  this.scrollOffset = 0.0F;
                  this.scrollDirection = 1;
                  this.pauseUntilTime = var25 + 300L;
               }
            }

            float var31 = 10.0F;
            float var32 = var11 + this.scrollOffset;
            float var30 = var32 > 0.0F ? (var11 - var31 + this.scrollOffset) / var32 : 1.0F;
            MsdfRenderer.renderText(
               var8.getFont(),
               var6,
               var8.getSize(),
               var13.method7287(),
               MatrixUtils.method664(var1.getMatrices()),
               var9 - this.scrollOffset,
               var21,
               0.0F,
               true,
               var30,
               1.0F,
               var32
            );
            var1.disableScissor();
         }
      }
   }

   private float getDescScrollSpeed(float var1, String var2) {
      float var3 = var1 / Math.max(var2.length(), 1);
      float var4 = var3 * 12.0F / 1000.0F;
      return Math.max(0.028F, Math.min(var4, 0.065F));
   }

   private void resetDescScroll() {
      this.scrollOffset = 0.0F;
      this.scrollDirection = 1;
      this.hoverStartTime = -1L;
      this.lastUpdateTime = -1L;
      this.pauseUntilTime = -1L;
      this.continueScroll = false;
   }

   @Generated
   @Override
   public float getWidth() {
      return this.width;
   }

   @Generated
   public T getSetting() {
      return this.setting;
   }
}

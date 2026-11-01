package org.zenith.client.screens.nlgui.elements;

import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class HoverScrollText {
   private static final float FADE_WIDTH = 10.0F;
   private static final long START_PAUSE = 300L;
   private static final long END_PAUSE = 900L;
   private String text;
   private float availableWidth;
   private float textWidth;
   private float offset;
   private int direction = 1;
   private long lastUpdate;
   private long pauseUntil;
   private boolean scrolling;

   public float render(MenuDrawContext var1, Font var2, String var3, float var4, float var5, float var6, ZenithColor var7, boolean var8) {
      float var9 = var2.width(var3);
      float var10 = this.update(var3, var9, var6, var8, System.currentTimeMillis());
      if (var6 <= 0.0F) {
         return 0.0F;
      } else if (var9 <= var6) {
         var1.drawText(var2, var3, var4, var5, var7);
         return var9;
      } else {
         float var11 = Math.min(10.0F, var6 / 2.0F);
         float var12 = var6 + var10;
         float var13 = (var6 - var11 + var10) / var12;
         if (var10 > 0.0F) {
            var1.enableScissor(var4, var5 - 1.0F, var4 + var6, var5 + var2.height() + 4.0F);
         }

         try {
            var1.drawText(var2, var3, var4 - var10, var5, var7, true, var13, 1.0F, var12);
         } finally {
            if (var10 > 0.0F) {
               var1.disableScissor();
            }
         }

         return var6;
      }
   }

   public float update(String var1, float var2, float var3, boolean var4, long var5) {
      if (!var1.equals(this.text) || var2 != this.textWidth || var3 != this.availableWidth || var5 < this.lastUpdate || var5 - this.lastUpdate > 250L) {
         this.text = var1;
         this.textWidth = var2;
         this.availableWidth = var3;
         this.offset = 0.0F;
         this.direction = 1;
         this.scrolling = false;
         this.pauseUntil = 0L;
         this.lastUpdate = var5;
      }

      long var7 = this.lastUpdate;
      this.lastUpdate = var5;
      if (!(var3 <= 0.0F) && !(var2 <= var3)) {
         if (!this.scrolling) {
            if (!var4) {
               return 0.0F;
            }

            this.scrolling = true;
            this.pauseUntil = var5 + 300L;
         }

         if (var5 <= this.pauseUntil) {
            return this.offset;
         } else {
            float var9 = (float)Math.max(0L, var5 - Math.max(var7, this.pauseUntil)) / 1000.0F;
            float var10 = Math.max(28.0F, Math.min(65.0F, var2 / Math.max(1, var1.length()) * 12.0F));
            float var11 = var2 - var3 + Math.min(10.0F, var3 / 2.0F);
            this.offset = this.offset + this.direction * var10 * var9;
            if (this.offset >= var11) {
               this.offset = var11;
               this.direction = -1;
               this.pauseUntil = var5 + 900L;
            } else if (this.offset <= 0.0F) {
               this.offset = 0.0F;
               this.direction = 1;
               this.pauseUntil = var5 + 300L;
               this.scrolling = var4;
            }

            return this.offset;
         }
      } else {
         this.offset = 0.0F;
         this.direction = 1;
         this.scrolling = false;
         this.pauseUntil = 0L;
         return 0.0F;
      }
   }
}

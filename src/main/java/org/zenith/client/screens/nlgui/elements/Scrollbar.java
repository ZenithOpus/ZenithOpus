package org.zenith.client.screens.nlgui.elements;

import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithColor;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class Scrollbar {
   public static final float MIN_THUMB = 12.0F;
   private static final float GRAB = 3.0F;
   private BoundsData track;
   private BoundsData thumb;
   private float span;
   private float travel;
   private boolean dragging;
   private float grab;

   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, ZenithColor var7) {
      this.render(var1, var2, var3, var4, var5, var5, var6, var7);
   }

   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7, ZenithColor var8) {
      if (!(var5 <= var4) && !(var4 <= 0.0F) && var8.getIntValue4() != 0) {
         float var9 = MathHelper.clamp(var4 * (var4 / var5), Math.min(12.0F, var4), var4);
         this.span = Math.max(0.0F, var6 - var4);
         this.travel = Math.max(0.0F, var4 - var9);
         float var10 = this.span > 0.0F ? MathHelper.clamp(-var7 / this.span, 0.0F, 1.0F) : 0.0F;
         float var11 = var3 + this.travel * var10;
         if (this.span > 0.0F) {
            this.track = new BoundsData(var2 - 3.0F, var3, 7.0F, var4);
            this.thumb = new BoundsData(var2, var11, 1.0F, var9);
         } else {
            this.clear();
         }

         var1.drawRect(var2, var11, 1.0F, var9, var8);
      } else {
         this.clear();
      }
   }

   public void clear() {
      this.track = null;
      this.thumb = null;
      this.span = 0.0F;
      this.travel = 0.0F;
      this.dragging = false;
   }

   public float onMouseClicked(double var1, double var3) {
      if (this.track != null && this.thumb != null && this.track.contains(var1, var3)) {
         this.dragging = true;
         if (var3 >= this.thumb.getFloatValue2() && var3 <= this.thumb.getFloatValue2() + this.thumb.height()) {
            this.grab = (float)(var3 - this.thumb.getFloatValue2());
            return Float.NaN;
         } else {
            this.grab = this.thumb.height() / 2.0F;
            return this.scrollAt(var3);
         }
      } else {
         return Float.NaN;
      }
   }

   public float onMouseDragged(double var1) {
      return this.dragging ? this.scrollAt(var1) : Float.NaN;
   }

   private float scrollAt(double var1) {
      return this.travel <= 0.0F ? 0.0F : -MathHelper.clamp((float)(var1 - this.grab - this.track.getFloatValue2()) / this.travel, 0.0F, 1.0F) * this.span;
   }

   public boolean isDragging() {
      return this.dragging;
   }

   public void release() {
      this.dragging = false;
   }
}

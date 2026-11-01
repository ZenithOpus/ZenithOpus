package org.zenith.client.screens.nlgui.elements;

import java.util.HashMap;
import java.util.Map;
import org.zenith.ZenithColor;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class PressFx {
   private static final long DIP_MS = 460L;
   private static final long INK_MS = 620L;
   private static final float SHRINK = 2.4F;
   private final Map<Object, PressFx.Mark> marks = new HashMap<>();

   public void press(Object var1, double var2, double var4) {
      if (this.marks.size() > 64) {
         long var6 = System.currentTimeMillis();
         this.marks.values().removeIf(var2x -> var6 - var2x.at() > 620L);
      }

      this.marks.put(var1, new PressFx.Mark(System.currentTimeMillis(), (float)var2, (float)var4));
   }

   public void press(Object var1) {
      this.press(var1, Double.NaN, Double.NaN);
   }

   public float dip(Object var1) {
      PressFx.Mark var2 = this.marks.get(var1);
      if (var2 == null) {
         return 0.0F;
      } else {
         float var3 = (float)(System.currentTimeMillis() - var2.at()) / 460.0F;
         if (var3 >= 1.0F) {
            return 0.0F;
         } else if (var3 < 0.3F) {
            float var6 = var3 / 0.3F;
            return var6 * var6 * (3.0F - 2.0F * var6);
         } else {
            float var4 = (var3 - 0.3F) / 0.7F;
            float var5 = 1.0F - (1.0F - var4) * (1.0F - var4) * (1.0F - var4);
            return 1.0F - var5;
         }
      }
   }

   public boolean begin(MenuDrawContext var1, Object var2, BoundsData var3) {
      float var4 = this.dip(var2);
      if (!(Math.abs(var4) < 0.003F) && var3 != null) {
         float var5 = Math.max(1.0F, Math.max(var3.width(), var3.height()));
         float var6 = 1.0F - var4 * Math.min(0.08F, 2.4F / var5);
         float var7 = var3.getFloatValue() + var3.width() / 2.0F;
         float var8 = var3.getFloatValue2() + var3.height() / 2.0F;
         var1.pushMatrix();
         var1.getMatrices().translate(var7, var8);
         var1.getMatrices().scale(var6, var6);
         var1.getMatrices().translate(-var7, -var8);
         return true;
      } else {
         return false;
      }
   }

   public void end(MenuDrawContext var1, boolean var2) {
      if (var2) {
         var1.popMatrix();
      }
   }

   public void ink(MenuDrawContext var1, Object var2, BoundsData var3, float var4, ZenithColor var5, float var6) {
      PressFx.Mark var7 = this.marks.get(var2);
      if (var7 != null && var3 != null) {
         float var8 = (float)(System.currentTimeMillis() - var7.at()) / 620.0F;
         if (!(var8 >= 1.0F) && !(var8 < 0.0F)) {
            float var9 = Math.min(1.0F, var8 * 1.4F);
            float var10 = 1.0F - (1.0F - var9) * (1.0F - var9) * (1.0F - var9) * (1.0F - var9);
            float var11 = Float.isNaN(var7.px())
               ? var3.getFloatValue() + var3.width() / 2.0F
               : Math.max(var3.getFloatValue(), Math.min(var3.getFloatValue() + var3.width(), var7.px()));
            float var12 = Float.isNaN(var7.py())
               ? var3.getFloatValue2() + var3.height() / 2.0F
               : Math.max(var3.getFloatValue2(), Math.min(var3.getFloatValue2() + var3.height(), var7.py()));
            float var13 = var11 + (var3.getFloatValue() - var11) * var10;
            float var14 = var12 + (var3.getFloatValue2() - var12) * var10;
            float var15 = var11 + (var3.getFloatValue() + var3.width() - var11) * var10;
            float var16 = var12 + (var3.getFloatValue2() + var3.height() - var12) * var10;
            float var17 = var15 - var13;
            float var18 = var16 - var14;
            if (!(var17 < 0.5F) && !(var18 < 0.5F)) {
               float var19 = var8 < 0.35F ? 1.0F : 1.0F - (var8 - 0.35F) / 0.65F * ((var8 - 0.35F) / 0.65F) * (3.0F - 2.0F * ((var8 - 0.35F) / 0.65F));
               float var20 = Math.min(var4, Math.min(var17, var18) / 2.0F);
               var1.drawRoundedRect(var13, var14, var17, var18, ProxyDotRadiusData.method8069(var20), var5.method7294(var6 * var19));
            }
         }
      }
   }

   private static final class Mark {
      private final long at;
      private final float px;
      private final float py;

      public long at() {
         return this.at;
      }

      public float px() {
         return this.px;
      }

      public float py() {
         return this.py;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Mark other)) return false;
         return java.util.Objects.equals(this.at, other.at) && java.util.Objects.equals(this.px, other.px) && java.util.Objects.equals(this.py, other.py);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(at, px, py);
      }

      @Override
      public String toString() {
         return "Mark[" + "at=" + this.at + ", " + "px=" + this.px + ", " + "py=" + this.py + "]";
      }

      private Mark(long var1, float var3, float var4) {
         this.at = var1;
         this.px = var3;
         this.py = var4;
      }
   }
}

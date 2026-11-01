package org.zenith.client.screens.nlgui.elements;

import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ChipStrip {
   public static final float CHIP_HEIGHT = 11.0F;
   private static final float GAP = 3.0F;
   private static final float PAD = 5.0F;
   private static final float FONT_SIZE = 4.6F;
   private final String[] labels;
   private final BoundsData[] bounds;
   private final Animation[] hot;
   private int hovered = -1;
   private final Animation plateX = new Animation(160L, 0.0F, Easing.INSTANCE12);
   private final Animation plateY = new Animation(160L, 0.0F, Easing.INSTANCE12);
   private final Animation plateWidth = new Animation(160L, 0.0F, Easing.INSTANCE12);
   private boolean plateReady;
   private int lastSelected = Integer.MIN_VALUE;
   private long pulseAt;

   public ChipStrip(String... var1) {
      this.labels = var1;
      this.bounds = new BoundsData[var1.length];
      this.hot = new Animation[var1.length];

      for (int var2 = 0; var2 < var1.length; var2++) {
         this.hot[var2] = new Animation(130L, 0.0F, Easing.INSTANCE12);
      }
   }

   private static String t(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   private String text(int var1) {
      return t(this.labels[var1]);
   }

   private static Font font() {
      return Fonts.NEW_MEDIUM.getFont(4.6F);
   }

   public float width() {
      Font var1 = font();
      float var2 = 0.0F;

      for (int var3 = 0; var3 < this.labels.length; var3++) {
         var2 += var1.width(this.text(var3)) + 10.0F + (var3 == 0 ? 0.0F : 3.0F);
      }

      return var2;
   }

   public float height(float var1) {
      return this.layout(0.0F, 0.0F, var1);
   }

   private float layout(float var1, float var2, float var3) {
      Font var4 = font();
      float var5 = var1;
      float var6 = var2;

      for (int var7 = 0; var7 < this.labels.length; var7++) {
         float var8 = var4.width(this.text(var7)) + 10.0F;
         if (var5 > var1 && var5 + var8 > var1 + var3 + 0.01F) {
            var5 = var1;
            var6 += 14.0F;
         }

         this.bounds[var7] = new BoundsData(var5, var6, var8, 11.0F);
         var5 += var8 + 3.0F;
      }

      return var6 - var2 + 11.0F;
   }

   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, int var7, float var8, ZenithStyle var9) {
      this.layout(var4, var5, var6);
      this.hovered = -1;

      for (int var10 = 0; var10 < this.labels.length; var10++) {
         if (this.bounds[var10].contains(var2, var3)) {
            this.hovered = var10;
         }
      }

      if (var7 >= 0 && var7 < this.labels.length) {
         BoundsData var18 = this.bounds[var7];
         float var11 = var18.getFloatValue() - var4;
         float var12 = var18.getFloatValue2() - var5;
         if (!this.plateReady) {
            this.plateX.method2809(var11);
            this.plateY.method2809(var12);
            this.plateWidth.method2809(var18.width());
            this.plateReady = true;
         }

         if (this.lastSelected != var7) {
            if (this.lastSelected != Integer.MIN_VALUE) {
               this.pulseAt = System.currentTimeMillis();
            }

            this.lastSelected = var7;
         }

         float var13 = var4 + this.plateX.method2808(var11);
         float var14 = var5 + this.plateY.method2808(var12);
         float var15 = this.plateWidth.method2808(var18.width());
         float var16 = this.pulseAt == 0L ? 0.0F : Math.max(0.0F, 1.0F - (float)(System.currentTimeMillis() - this.pulseAt) / 380.0F);
         var16 *= var16;
         float var17 = (float)Math.sin(var16 * Math.PI) * 1.2F;
         var1.drawRoundedRect(
            var13 - var17,
            var14 - var17 * 0.5F,
            var15 + var17 * 2.0F,
            11.0F + var17,
            ProxyDotRadiusData.method8069(5.5F + var17 * 0.5F),
            var9.getDisableActiveBg().getZenithColor().method7294(var8)
         );
         if (var16 > 0.01F) {
            var1.drawRoundedRect(
               var13,
               var14,
               var15,
               11.0F,
               ProxyDotRadiusData.method8069(5.5F),
               var9.getPrimaryColor().getZenithColor().method7292(0.22F * var16).method7294(var8)
            );
         }
      }

      Font var19 = font();

      for (int var20 = 0; var20 < this.labels.length; var20++) {
         BoundsData var21 = this.bounds[var20];
         boolean var22 = var20 == var7;
         float var23 = this.hot[var20].method2808(var20 == this.hovered && !var22 ? 1.0F : 0.0F);
         if (var23 > 0.01F) {
            var1.drawRoundedRect(
               var21.getFloatValue(),
               var21.getFloatValue2(),
               var21.width(),
               11.0F,
               ProxyDotRadiusData.method8069(5.5F),
               var9.getDisableActiveBg().getZenithColor().method7294(var8 * 0.45F * var23)
            );
         }

         ZenithColor var24 = var22
            ? var9.getTextEnable().getZenithColor()
            : var9.getTextTertiary().getZenithColor().method7295(var9.getTextSecondary().getZenithColor(), var23);
         var1.drawText(var19, this.text(var20), var21.getFloatValue() + 5.0F, var21.getFloatValue2() + (11.0F - var19.height()) / 2.0F, var24.method7294(var8));
      }
   }

   public int hovered() {
      return this.hovered;
   }

   public String label(int var1) {
      return var1 >= 0 && var1 < this.labels.length ? this.text(var1) : "";
   }
}

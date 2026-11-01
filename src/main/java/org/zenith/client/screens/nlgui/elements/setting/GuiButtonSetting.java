package org.zenith.client.screens.nlgui.elements.setting;

import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiButtonSetting extends GuiSetting<ButtonSetting> {
   private final Animation animationEnable = new Animation(200L, Easing.INSTANCE10);
   private final Animation animationClick = new Animation(350L, Easing.INSTANCE15);
   private boolean clickActive;
   private BoundsData bounds;

   public GuiButtonSetting(ButtonSetting var1) {
      super(166.0F, var1);
   }

   public GuiButtonSetting(ButtonSetting var1, float var2) {
      super(var2, var1);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.bounds != null && this.bounds.contains(var1, var3)) {
         this.setting.toggle();
         this.clickActive = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var7 != null) {
         this.animationVisible.method2807(this.setting.isVisible());
         var6 *= this.animationVisible.getValue();
         this.animationClick.method2807(this.clickActive);
         if (this.clickActive && this.animationClick.getValue() > 0.9F) {
            this.clickActive = false;
         }

         Font var8 = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font var9 = Fonts.NEW_REGULAR.getFont(5.4F);
         float var10 = this.width / 1.4F - GuiStyle.PADDING.intValue();
         ZenithColor var11 = var7.getTextEnable().getZenithColor().method7294(var6);
         ZenithColor var12 = var7.getTextSecondary().getZenithColor().method7294(var6);
         ZenithColor var13 = var7.getPrimaryColor().getZenithColor().method7294(var6);
         this.drawDefault(
            var1, var2, var3, this.setting.getIcon(), this.setting.getName(), this.setting.getDescription(), var8, var9, var4, var5, var10, var11, var12, var13
         );
         if (!this.isShort()) {
            float var14 = 6.0F;
            float var15 = 6.0F;
            this.bounds = new BoundsData(var4 + this.width - var14, var5 + (this.getHeight() - var15) / 2.0F, var14, var15);
            var1.drawRoundedRect(
               this.bounds.getFloatValue(),
               this.bounds.getFloatValue2(),
               var14,
               var15,
               ProxyDotRadiusData.method8069(1.0F),
               var7.getFieldSurfaceBackground()
                  .getZenithColor()
                  .method7294(2.0F)
                  .method7295(var7.getPrimaryColor().getZenithColor(), this.animationEnable.getValue())
                  .method7294(var6)
            );
            var1.drawRoundedBorder(
               this.bounds.getFloatValue(),
               this.bounds.getFloatValue2(),
               var14,
               var15,
               -0.5F,
               ProxyDotRadiusData.method8069(1.0F),
               var7.getFieldBorder().getZenithColor().method7295(ZenithColor.INSTANCE8, this.animationEnable.getValue()).method7294(var6)
            );
            Font var16 = Fonts.NEW_ICONS.getFont(4.0F);
            var1.drawText(
               var16,
               "<",
               this.bounds.getFloatValue() + 1.5F - 0.8F,
               this.bounds.getFloatValue2() + (var15 - var16.height()) / 2.0F,
               ZenithColor.INSTANCE8.method7295(var7.getPrimaryColor().getZenithColor(), this.animationClick.getValue()).method7294(var6)
            );
         } else {
            Font var19 = Fonts.NEW_ICONS.getFont(5.5F);
            float var20 = var19.width("H");
            float var21 = var19.height();
            float var17 = var4 + this.width - var20;
            this.bounds = new BoundsData(var17, var5 + (this.getHeight() - var21) / 2.0F, var20, var21);
            var1.drawText(
               var19,
               "H",
               this.bounds.getFloatValue(),
               this.bounds.getFloatValue2(),
               var7.getTextSecondary().getZenithColor().method7295(var7.getPrimaryColor().getZenithColor(), this.animationClick.getValue()).method7294(var6)
            );
         }
      }
   }
}

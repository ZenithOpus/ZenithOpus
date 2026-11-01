package org.zenith.client.screens.override.button;

import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.override.main.MainMenuBlurRenderer;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.ShapeRenderer;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class DefaultButton extends ButtonScreen {
   private final Animation hoverAnimation = new Animation(300L, Easing.INSTANCE10);
   private final String name;
   private final String icon;
   private final Runnable onClick;
   private final MainMenuBlurRenderer blurRenderer;

   public DefaultButton(String var1, String var2, float var3, float var4, Runnable var5, MainMenuBlurRenderer var6) {
      super(var3, var4);
      this.name = var1;
      this.icon = var2;
      this.onClick = var5;
      this.blurRenderer = var6;
   }

   @Override
   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5) {
      super.render(var1, var2, var3, var4, var5);
      ZenithStyle var6 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      this.hoverAnimation.method2807(this.bounds.contains(var2, var3));
      ShapeRenderer.method7167(
         MatrixUtils.method663(var1),
         var4,
         var5,
         this.getWidth(),
         this.getHeight(),
         10,
         ProxyDotRadiusData.method8069(4.0F),
         GlowColor.method8097(ZenithColor.INSTANCE8.method7295(ZenithColor.INSTANCE.method7294(0.2F), this.hoverAnimation.getValue()))
      );
      this.blurRenderer
         .render(
            MatrixUtils.method664(var1.getMatrices()), var4, var5, this.getWidth(), this.getHeight(), ProxyDotRadiusData.method8069(4.0F), ZenithColor.INSTANCE
         );
      var1.drawRoundedRect(
         var4, var5, this.getWidth(), this.getHeight(), ProxyDotRadiusData.method8069(4.0F), var6.getFieldSurfaceBackground().getZenithColor()
      );
      var1.drawRoundedBorder(var4, var5, this.getWidth(), this.getHeight(), -0.1F, ProxyDotRadiusData.method8069(4.0F), var6.getFieldBorder().getZenithColor());
      Font var7 = Fonts.NEW_MEDIUM.getFont(6.0F);
      Font var8 = Fonts.ICONS.getFont(6.0F);
      float var9 = 4.0F;
      float var10 = var8.width(this.icon);
      float var11 = var7.width(this.name);
      float var13 = var4 + (this.getWidth() - var11) / 2.0F;
      float var14 = var5 + (this.getHeight() - var7.height()) / 2.0F;
      float var15 = var5 + (this.getHeight() - var8.height()) / 2.0F;
      if (!this.name.equals("Exit")) {
         var1.drawText(
            var8,
            this.icon,
            var4 + 25.0F,
            var15,
            var6.getTextTertiary().getZenithColor().method7295(var6.getPrimaryColor().getZenithColor(), this.hoverAnimation.getValue())
         );
      }

      var1.drawText(
         var7,
         this.name,
         var13,
         var14,
         var6.getTextSecondary().getZenithColor().method7295(var6.getTextEnable().getZenithColor(), this.hoverAnimation.getValue())
      );
   }

   @Override
   public void onClick(double var1, double var3, Enum3 var5) {
      this.onClick.run();
   }
}

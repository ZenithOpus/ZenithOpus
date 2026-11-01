package org.zenith.client.hud;

import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.movement.TimerModule;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class TimerHud extends Interface {
   private static final float CONST_floatValue = 26.0F;
   private static final float CONST_floatValue2 = 26.0F;
   private static final float CONST_floatValue3 = 2.0F;
   private static final float CONST_floatValue4 = 76.0F;
   private final Animation animation = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(180L, 0.0F, Easing.INSTANCE10);

   public TimerHud(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      this.width = 26.0F + GuiStyle.PADDING.intValue() * 2.0F + 76.0F;
      this.height = 26.0F;
   }

   @Override
   public void method8(CustomDrawContext var1) {
      TimerModule var2 = TimerModule.INSTANCE;
      boolean var3 = var2.isEnabled();
      float var4 = var2.method131();
      String var5 = "Timer";
      String var6 = Math.round(var4 * 100.0F) + "%";
      this.animation.method2807(var3);
      if (!(this.animation.getValue() <= 0.01F)) {
         ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         Font var8 = Fonts.NEW_ICONS.getFont(6.2F);
         Font var9 = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font var10 = Fonts.NEW_SEMIBOLD.getFont(5.3F);
         float var11 = MathHelper.clamp(this.animation2.method2808(var4), 0.0F, 1.0F);
         float var12 = var10.width(var6);
         float var13 = Math.max(18.0F, var12 + GuiStyle.PADDING.intValue() * 1.5F);
         float var14 = Math.max(76.0F, var9.width(var5) + GuiStyle.PADDING.intValue() + var13);
         this.width = 26.0F + GuiStyle.PADDING.intValue() * 2.0F + var14;
         this.height = 26.0F;
         float var15 = InterfaceModule.method6801();
         float var16 = this.floatValue + 26.0F + GuiStyle.PADDING.intValue();
         float var17 = this.floatValue + this.width - GuiStyle.PADDING.intValue();
         float var18 = var17 - var13;
         float var19 = this.floatValue2 + 8.0F;
         float var20 = 8.0F;
         float var21 = this.floatValue2 + 7.5F;
         float var22 = var17 - var16;
         float var23 = this.floatValue2 + this.height - 5.0F;
         var1.pushMatrix();
         var1.getMatrices().translate(this.floatValue + this.width / 2.0F, this.floatValue2 + this.height / 2.0F);
         var1.getMatrices().scale(this.animation.getValue(), this.animation.getValue());
         var1.getMatrices().translate(-(this.floatValue + this.width / 2.0F), -(this.floatValue2 + this.height / 2.0F));
         var1.drawBlurHud(this.floatValue, this.floatValue2, this.width, this.height, 21.0F, ProxyDotRadiusData.method8069(var15), ZenithColor.INSTANCE);
         var1.drawRoundedRect(
            this.floatValue,
            this.floatValue2,
            this.width,
            this.height,
            ProxyDotRadiusData.method8069(var15),
            var7.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
         );
         var1.drawRoundedRect(
            this.floatValue, this.floatValue2, 26.0F, this.height, ProxyDotRadiusData.method8078(var15, var15), var7.getHeaderHudBackground().getZenithColor()
         );
         var1.drawRoundedRect(var18, var21, var13, var20, ProxyDotRadiusData.method8069(1.4F), var7.getHeaderHudBackground().getZenithColor());
         String var24 = var2.getCategory().getIcon();
         var1.drawText(
            var8,
            var24,
            this.floatValue + (26.0F - var8.width(var24)) / 2.0F,
            this.floatValue2 + (this.height - var8.height()) / 2.0F,
            var7.getPrimaryColor().getZenithColor()
         );
         var1.drawText(var9, var5, var16, var19, var7.getTextEnable().getZenithColor());
         var1.drawText(var10, var6, var18 + (var13 - var12) / 2.0F, var21 + (var20 - var10.height()) / 2.0F, var7.getPrimaryColor().getZenithColor());
         var1.drawRoundedRect(var16, var23, var22, 2.0F, ProxyDotRadiusData.method8069(0.6F), var7.getFieldBorder().getZenithColor());
         if (var11 > 0.0F) {
            var1.drawRoundedRect(var16, var23, var22 * var11, 2.0F, ProxyDotRadiusData.method8069(0.6F), var7.getPrimaryColor().getZenithColor());
         }

         var1.popMatrix();
      }
   }

   @Override
   public float method254() {
      return this.animation.getValue();
   }
}

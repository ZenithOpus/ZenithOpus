package org.zenith.client.hud;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class Hud4 extends Interface {
   private final Animation animation = new Animation(300L, Easing.INSTANCE25);
   private final Animation animation2 = new Animation(150L, Easing.INSTANCE25);
   private final Item[] items = new Item[27];
   private final int[] intValues = new int[27];
   private float lastWidth = 0.0F;
   private float lastHeight = 0.0F;

   public Hud4(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void method8(CustomDrawContext var1) {
      if (mc.player == null) {
         this.animation.method2808(0.0F);
         if (this.animation.getValue() > 0.01F) {
            this.method6476(var1, this.animation.getValue());
         }
      } else {
         this.animation.method2808(1.0F);
         if (!(this.animation.getValue() <= 0.01F)) {
            boolean var2 = false;

            for (int var3 = 9; var3 < 36; var3++) {
               ItemStack var4 = mc.player.getInventory().getStack(var3);
               Item var5 = var4.getItem();
               int var6 = var4.getCount();
               int var7 = var3 - 9;
               if (var5 != this.items[var7] || var6 != this.intValues[var7]) {
                  this.items[var7] = var5;
                  this.intValues[var7] = var6;
                  var2 = true;
               }
            }

            if (var2) {
               this.animation2.method2808(0.0F);
            }

            this.animation2.method2808(1.0F);
            this.method6476(var1, this.animation.getValue() * this.animation2.getValue());
         }
      }
   }

   private void method6476(CustomDrawContext var1, float var2) {
      if (mc.player != null) {
         Font var3 = Fonts.NEW_MEDIUM.getFont(5.0F);
         Font var4 = Fonts.ICONS.getFont(4.5F);
         float var5 = 14.0F;
         float var6 = 1.0F;
         float var7 = 4.0F;
         ZenithStyle var8 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         byte var9 = 9;
         byte var10 = 3;
         float var11 = var9 * var5 + (var9 - 1) * var6;
         float var12 = var10 * var5 + (var10 - 1) * 0.5F;
         this.width = var11;
         this.height = var12;
         float var13 = InterfaceModule.method6801();
         this.lastWidth = this.width;
         this.lastHeight = this.height;
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(this.floatValue + this.width / 2.0F, this.floatValue2 + this.height / 2.0F);
         var1.getMatrices().scale(var2, var2);
         var1.getMatrices().translate(-(this.floatValue + this.width / 2.0F), -(this.floatValue2 + this.height / 2.0F));
         var1.drawBlurHud(this.floatValue, this.floatValue2, this.width, this.height, 21.0F, ProxyDotRadiusData.method8069(var13), ZenithColor.INSTANCE);
         var1.drawRoundedRect(
            this.floatValue,
            this.floatValue2,
            this.width,
            this.height,
            ProxyDotRadiusData.method8069(var13),
            var8.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
         );
         float var14 = (var5 - var4.width("X")) / 2.0F;

         for (int var15 = 0; var15 < var10; var15++) {
            for (int var16 = 0; var16 < var9; var16++) {
               int var17 = 9 + var15 * 9 + var16;
               ItemStack var18 = mc.player.getInventory().getStack(var17);
               float var19 = this.floatValue + var16 * (var5 + var6);
               float var20 = this.floatValue2 + var15 * (var5 + 0.5F);
               float var21 = Math.min(var13, var5 / 2.0F);
               ProxyDotRadiusData var22 = ProxyDotRadiusData.method8069(var21);
               if (!var18.isEmpty()) {
                  float var23 = 8.0F;
                  float var24 = var23 / 16.0F;
                  var1.pushMatrix();
                  var1.getMatrices().translate(var19 + (var5 - var23) / 2.0F, var20 + (var5 - var23) / 2.0F);
                  var1.getMatrices().scale(var24, var24);
                  var1.drawItem(var18, 0, 0);
                  var1.drawItemBar(var18, 0, 0);
                  var1.drawCooldownProgress(var18, 0, 0);
                  var1.popMatrix();
                  if (var18.getCount() > 1) {
                     String var25 = "x" + var18.getCount();
                     float var26 = var3.width(var25);
                     float var27 = var19 + var5 - var26 - 1.5F;
                     float var28 = var20 + var5 - var3.height() - 1.0F;
                     var1.drawText(var3, var25, var27, var28, var8.getTextEnable().getZenithColor());
                  }
               } else {
                  var1.drawText(
                     var4,
                     "M",
                     var19 + var14,
                     var20 + (var5 - var4.height()) / 2.0F,
                     Zenith.getInstance().getStyleManager().getCurrentStyle().getTextTertiary().getZenithColor()
                  );
               }
            }
         }

         var1.getMatrices().popMatrix();
      }
   }
}

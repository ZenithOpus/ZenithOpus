package org.zenith.client.hud;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.combat.AutoTotemModule;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class Hud1 extends Interface {
   private static final float CONST_floatValue = 22.0F;
   private static final ZenithColor zenithColor = new ZenithColor(80, 220, 120, 255);
   private static final ZenithColor zenithColor2 = new ZenithColor(255, 200, 60, 255);
   private static final ZenithColor zenithColor3 = new ZenithColor(255, 70, 70, 255);
   private static final ItemStack itemStack = new ItemStack(Items.TOTEM_OF_UNDYING);

   public Hud1(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      this.width = 44.0F;
      this.height = 22.0F;
   }

   @Override
   public void method8(CustomDrawContext var1) {
      if (mc.player != null) {
         ZenithStyle var2 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         this.width = 44.0F;
         this.height = 22.0F;
         float var3 = InterfaceModule.method6801();
         int var4 = 0;

         for (int var5 = 0; var5 < mc.player.getInventory().size(); var5++) {
            ItemStack var6 = mc.player.getInventory().getStack(var5);
            if (var6.isOf(Items.TOTEM_OF_UNDYING)) {
               var4 += var6.getCount();
            }
         }

         boolean var9 = mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING);
         ZenithColor var10 = AutoTotemModule.INSTANCE.isFlag() ? zenithColor2 : (var9 && AutoTotemModule.INSTANCE.method707() ? zenithColor : zenithColor3);
         var1.pushMatrix();
         var1.drawBlurHud(this.floatValue, this.floatValue2, this.width, this.height, 21.0F, ProxyDotRadiusData.method8069(var3), ZenithColor.INSTANCE);
         var1.drawRoundedRect(
            this.floatValue,
            this.floatValue2,
            this.width,
            this.height,
            ProxyDotRadiusData.method8069(var3),
            var2.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
         );
         var1.pushMatrix();
         var1.getMatrices().translate(this.floatValue + 4.6F, this.floatValue2 + 4.6F);
         var1.getMatrices().scale(0.8F, 0.8F);
         var1.drawItem(itemStack, 0, 0);
         var1.popMatrix();
         Font var7 = Fonts.NEW_MEDIUM.getFont(7.0F);
         String var8 = String.valueOf(var4);
         var1.drawText(
            var7,
            var8,
            this.floatValue + 22.0F + (22.0F - var7.width(var8)) / 2.0F,
            this.floatValue2 + (22.0F - var7.height()) / 2.0F,
            var4 > 0 ? var2.getTextEnable().getZenithColor() : zenithColor3
         );
         var1.drawRoundedRect(this.floatValue + 3.0F, this.floatValue2 + 22.0F - 3.0F, this.width - 6.0F, 1.5F, ProxyDotRadiusData.method8069(0.75F), var10);
         var1.popMatrix();
      }
   }
}

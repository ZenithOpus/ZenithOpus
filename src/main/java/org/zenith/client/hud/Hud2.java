package org.zenith.client.hud;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.game.ArmorSlots;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class Hud2 extends Interface {
   private final List<Hud2.Entry> items = new ArrayList<>();

   public Hud2(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      float var9 = 22.0F;
      this.width = var9 * 4.0F;
      this.height = var9;

      for (int var10 = 0; var10 < 4; var10++) {
         this.items.add(new Hud2.Entry(var10));
      }
   }

   @Override
   public void method8(CustomDrawContext var1) {
      ZenithStyle var2 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      float var3 = 22.0F;
      this.width = var3 * 4.0F;
      this.height = var3;
      float var4 = InterfaceModule.method6801();
      var1.pushMatrix();
      var1.drawBlurHud(this.floatValue, this.floatValue2, this.width, this.height, 21.0F, ProxyDotRadiusData.method8069(var4), ZenithColor.INSTANCE);
      var1.drawRoundedRect(
         this.floatValue,
         this.floatValue2,
         this.width,
         this.height,
         ProxyDotRadiusData.method8069(var4),
         var2.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
      );
      float var5 = this.floatValue;
      float var6 = this.floatValue2;

      for (Hud2.Entry var8 : this.items) {
         var8.method1529(var1, var5, var6, var2);
         var5 += var3;
      }

      var1.popMatrix();
   }

   public class Entry {
      private final int intValue;

      private Entry(int var2) {
         this.intValue = var2;
      }

      public void method1529(CustomDrawContext var1, float var2, float var3, ZenithStyle var4) {
         float var5 = InterfaceModule.method6801();
         ProxyDotRadiusData var6 = ProxyDotRadiusData.method8069(var5);
         Font var7 = Fonts.NEW_MEDIUM.getFont(5.4F);
         ZenithColor var8 = var4.getTextEnable().getZenithColor();
         ItemStack var9 = ArmorSlots.method5384(MinecraftAccess.mc.player).get(3 - this.intValue);
         if (!var9.isEmpty()) {
            var1.pushMatrix();
            var1.getMatrices().translate((float)(var2 + 4.6), (float)(var3 + 4.6));
            var1.getMatrices().scale(0.8F, 0.8F);
            var1.drawItem(var9, 0, 0);
            var1.drawItemBar(var9, 0, 0);
            var1.drawCooldownProgress(var9, 0, 0);
            var1.popMatrix();
            if (var9.getCount() > 1) {
               String var10 = "x" + var9.getCount();
               float var11 = var7.width(var10);
               float var12 = var2 + 22.0F - var11 - 3.0F;
               float var13 = var3 + 22.0F - var7.height() - 3.0F;
               var1.drawText(var7, var10, var12, var13, var8);
            }
         } else {
            Font var14 = Fonts.ICONS.getFont(4.5F);
            var1.drawText(
               var14,
               "M",
               var2 + (22.0F - var14.width("M")) / 2.0F,
               var3 + (22.0F - var14.height()) / 2.0F,
               Zenith.getInstance().getStyleManager().getCurrentStyle().getTextTertiary().getZenithColor()
            );
         }
      }
   }
}

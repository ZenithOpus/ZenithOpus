package org.zenith.utility.game;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import org.zenith.ZenithColor;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class StatusBarColors {
   private static final int CONST_intValue = 10;
   private static final float CONST_floatValue = 8.0F;
   private static final float CONST_floatValue2 = 2.0F;
   private static final float CONST_floatValue3 = 1.0F;
   private static final float CONST_floatValue4 = 3.0F;
   private static final ZenithColor zenithColor = new ZenithColor(255, 193, 74, 255);
   private static final ZenithColor zenithColor2 = new ZenithColor(255, 107, 74, 255);

   private StatusBarColors() {
   }

   public static void method1755(DrawContext var0, PlayerEntity var1, int var2, int var3) {
      if (var1 != null && InterfaceModule.INSTANCE.isEnabled() && InterfaceModule.INSTANCE.method6796()) {
         float var4 = Math.max(0.0F, var1.getHungerManager().getSaturationLevel());
         float var5 = Math.min(1.0F, var4 / 20.0F);
         ZenithStyle var6 = HudListLayout.style();
         CustomDrawContext var7 = new CustomDrawContext(var0);
         float var8 = var3;
         float var9 = var2 - 3.0F;
         float var10 = 79.0F;
         float var11 = var8 - 80.0F - 1.0F;
         ProxyDotRadiusData var12 = ProxyDotRadiusData.method8069(1.0F);
         var7.drawRoundedRect(var11, var9, var10, 2.0F, var12, var6.getHeaderHudBackground().getZenithColor().method7294(0.85F));
         if (!(var5 <= 0.0F)) {
            ZenithColor var13 = var5 < 0.15F ? zenithColor2 : zenithColor;
            var7.drawRoundedRect(var11, var9, Math.max(2.0F, var10 * var5), 2.0F, var12, var13);
         }
      }
   }
}

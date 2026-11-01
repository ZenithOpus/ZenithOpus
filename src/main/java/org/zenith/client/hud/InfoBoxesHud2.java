package org.zenith.client.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.zenith.Zenith;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.misc.ScoreboardParser;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class InfoBoxesHud2 extends Interface {
   private final MultiSelectSetting infoBoxes = new MultiSelectSetting("hud.infoBoxes", "hud.infoBoxes.desc");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.infoBoxes, "hud.infoBoxes.coordinates", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.infoBoxes, "BPS", true);

   public InfoBoxesHud2(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void method8(CustomDrawContext var1) {
      if (mc.player == null) {
         this.width = 0.0F;
         this.height = 0.0F;
      } else {
         ZenithStyle var2 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var2 == null) {
            this.width = 0.0F;
            this.height = 0.0F;
         } else {
            double var3 = Math.hypot(mc.player.getX() - mc.player.lastX, mc.player.getZ() - mc.player.lastZ) * 20.0;
            int var5 = (int)mc.player.getX();
            int var6 = (int)mc.player.getY();
            int var7 = (int)mc.player.getZ();
            if (ScoreboardParser.isActive()) {
               var5 = ScoreboardParser.method7069(var5);
               var7 = ScoreboardParser.method7070(var7);
            }

            String var8 = String.format(Locale.US, "%.2f", var3);
            boolean var9 = !this.entry.isEnabled() && !this.entry2.isEnabled();
            ArrayList var10 = new ArrayList(2);
            if (var9 || this.entry.isEnabled()) {
               var10.add(
                  new HudListLayout.Data(
                     "Y",
                     List.of(
                        HudListLayout.Data.method7660(String.valueOf(var5)),
                        HudListLayout.Data.method7661("x "),
                        HudListLayout.Data.method7660(String.valueOf(var6)),
                        HudListLayout.Data.method7661("y "),
                        HudListLayout.Data.method7660(String.valueOf(var7)),
                        HudListLayout.Data.method7661("z")
                     )
                  )
               );
            }

            if (var9 || this.entry2.isEnabled()) {
               var10.add(HudListLayout.Data.method7662("4", var8, " bps"));
            }

            this.width = HudListLayout.method7658(var10);
            this.height = var10.isEmpty() ? 0.0F : 17.0F;
            HudListLayout.method7659(var1, this.floatValue, this.floatValue2, var10);
         }
      }
   }
}

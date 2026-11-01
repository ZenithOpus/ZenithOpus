package org.zenith.client.hud;

import java.util.ArrayList;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import org.zenith.Zenith;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class InfoBoxesHud extends Interface {
   private final MultiSelectSetting infoBoxes = new MultiSelectSetting("hud.infoBoxes", "hud.infoBoxes.desc");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.infoBoxes, "TPS", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.infoBoxes, "Ping", true);
   private final MultiSelectSetting.Entry entry3 = new MultiSelectSetting.Entry(this.infoBoxes, "FPS", true);

   public InfoBoxesHud(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
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
            String var3 = String.format(Locale.US, "%.2f", Zenith.getInstance().getServerDetector().method1624());
            String var4 = this.method2260();
            String var5 = String.valueOf(MinecraftClient.getInstance().getCurrentFps());
            boolean var6 = !this.entry.isEnabled() && !this.entry2.isEnabled() && !this.entry3.isEnabled();
            ArrayList var7 = new ArrayList(3);
            if (var6 || this.entry.isEnabled()) {
               var7.add(HudListLayout.Data.method7662("h", var3, " tps"));
            }

            if (var6 || this.entry2.isEnabled()) {
               var7.add(HudListLayout.Data.method7662("H", var4, " ms"));
            }

            if (var6 || this.entry3.isEnabled()) {
               var7.add(HudListLayout.Data.method7662("F", var5, " fps"));
            }

            this.width = HudListLayout.method7658(var7);
            this.height = var7.isEmpty() ? 0.0F : 17.0F;
            HudListLayout.method7659(var1, this.floatValue, this.floatValue2, var7);
         }
      }
   }

   private String method2260() {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         PlayerListEntry var1 = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
         return var1 == null ? "-" : String.valueOf(Math.max(0, var1.getLatency()));
      } else {
         return "-";
      }
   }
}

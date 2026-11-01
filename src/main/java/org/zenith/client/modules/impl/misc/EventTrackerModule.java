package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.math.BlockPos;
import org.zenith.MinecraftAccess;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.filemanager.impl.way.Way;
import org.zenith.base.filemanager.impl.way.WayManager;
import org.zenith.client.events.ChatReceiveEvent;

@ModuleInfo(
   name = "EventTracker",
   category = ModuleCategory.MISC,
   description = "module.eventtracker.description"
)
public final class EventTrackerModule extends Module implements MinecraftAccess {
   public static final EventTrackerModule INSTANCE = new EventTrackerModule();
   private static final Pattern pattern = Pattern.compile("(?:Координаты:|на координатах)\\s*\\[?\\s*(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)");
   private static final Pattern pattern2 = Pattern.compile("\\[(.+?)]");
   private static final Pattern pattern3 = Pattern.compile("▶\\s*(.+?)(?:\\s+(?:находится|уже))?\\s+на\\s+координатах");
   private static final Pattern pattern4 = Pattern.compile("^\\s*\\[\\d+]\\s*(.+?):?\\s*$");
   private String text;
   private String text2;

   private EventTrackerModule() {
   }

   @EventTarget
   public void onChatReceive(ChatReceiveEvent var1) {
      String var2 = var1.getText().getString();
      Matcher var3 = pattern2.matcher(var2);
      String var4 = null;
      if (var3.find()) {
         String var5 = var3.group(1).trim();
         if (!var5.matches("[-\\d\\s]*")) {
            var4 = var5;
         }
      }

      if (var4 == null) {
         Matcher var10 = pattern3.matcher(var2);
         if (var10.find()) {
            var4 = var10.group(1).trim();
         }
      }

      if (var4 == null) {
         Matcher var11 = pattern4.matcher(var2);
         if (var11.find()) {
            var4 = var11.group(1).trim();
         }
      }

      Matcher var12 = pattern.matcher(var2);
      if (var12.find()) {
         String var13 = var4 != null ? var4 : this.text;
         if (var13 != null) {
            int var7 = Integer.parseInt(var12.group(1));
            int var8 = Integer.parseInt(var12.group(2));
            int var9 = Integer.parseInt(var12.group(3));
            this.method952(var13, var7, var8, var9);
            this.text = null;
         }
      } else if (var4 != null) {
         this.text = var4;
      } else if (var2.contains("▍")) {
         String var6 = var2.replace("▍", "").trim();
         if (!var6.isEmpty() && !var6.startsWith("Редкость") && !var6.startsWith("Уровень лута")) {
            this.text = var6;
         }
      }
   }

   private void method952(String var1, int var2, int var3, int var4) {
      WayManager var5 = Zenith.getInstance().getWayManager();
      if (this.text2 != null) {
         var5.deleteWay(this.text2);
      }

      var5.deleteWay(var1);
      String var6 = mc.getNetworkHandler() != null && mc.getNetworkHandler().getServerInfo() != null
         ? mc.getNetworkHandler().getServerInfo().address
         : "VANILLA";
      var5.addWay(new Way(var1, new BlockPos(var2, var3, var4), var6));
      this.text2 = var1;
   }

   @Override
   public void onDisable() {
      this.text = null;
      super.onDisable();
   }
}

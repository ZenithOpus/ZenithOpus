package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Locale;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.settings.BooleanSetting;

@ModuleInfo(
   name = "AutoAccept",
   category = ModuleCategory.MISC,
   description = "module.autoaccept.description"
)
public final class AutoAcceptModule extends Module {
   public static final AutoAcceptModule INSTANCE = new AutoAcceptModule();
   private final BooleanSetting onlyFriend = new BooleanSetting("module.autoAccept.onlyFriend", "module.autoAccept.onlyFriend.desc", false);

   private AutoAcceptModule() {
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (mc.player != null && mc.world != null) {
         if (var1.method7100()) {
            if (var1.getPacket3() instanceof GameMessageS2CPacket var2) {
               String var4 = var2.content().getString().toLowerCase(Locale.ROOT);
               if (var4.contains("телепортироваться") || var4.contains("has requested teleport") || var4.contains("просит к вам телепортироваться")) {
                  mc.execute(() -> this.method7736(var4));
               }
            }
         }
      }
   }

   private void method7736(String var1) {
      if (mc.player != null) {
         if (this.onlyFriend.isEnabled()) {
            boolean var2 = false;

            for (String var4 : Zenith.getInstance().getFriends().getItems()) {
               if (var1.contains(var4.toLowerCase(Locale.ROOT))) {
                  var2 = true;
                  break;
               }
            }

            if (!var2) {
               return;
            }
         }

         mc.player.networkHandler.sendChatCommand("tpaccept");
      }
   }
}

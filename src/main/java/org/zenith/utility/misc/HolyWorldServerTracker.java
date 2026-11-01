package org.zenith.utility.misc;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.Locale;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.ZenithAccess;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.network.InventoryUtils;

public class HolyWorldServerTracker implements ZenithAccess {
   private static final int CONST_intValue = 1;
   private static final int CONST_intValue2 = 10;
   private static final int CONST_intValue3 = 19;
   private static final int CONST_intValue4 = 20;
   private static final int CONST_intValue5 = 29;
   private static final int CONST_intValue6 = 30;
   private static final int CONST_intValue7 = 36;
   private final org.zenith.StopWatch stopWatch = new org.zenith.StopWatch();
   private boolean lobby;
   private int anarchy;

   public HolyWorldServerTracker() {
      EventManager.register(this);
   }

   public boolean isActive() {
      return this.anarchy != 0;
   }

   public boolean isHolyWorldHere() {
      return Zenith.getInstance().getServerDetector().method1642();
   }

   public boolean method6149() {
      return Zenith.getInstance().getServerDetector().method1635();
   }

   public int currentAnarchyHere() {
      return Zenith.getInstance().getServerDetector().getAnarchy();
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (this.anarchy != 0 && var1.method7100()) {
         if (var1.getPacket3() instanceof GameMessageS2CPacket var2) {
            String var4 = var2.content().getString().toLowerCase(Locale.ROOT);
            if (!var4.contains("хаб") && var4.contains("не удалось")) {
               mc.execute(() -> {
                  if (this.anarchy != 0) {
                     HudHolder.method6969().method6972("0", Text.literal(" На данную анархию " + Formatting.RED + "нельзя" + Formatting.RESET + " зайти"));
                     this.anarchy = 0;
                     this.lobby = false;
                  }
               });
            }
         }
      }
   }

   @EventTarget
   public void method6150(UpdateEvent var1) {
      if (this.anarchy != 0 && mc.player != null && mc.player.networkHandler != null && mc.world != null) {
         if (this.isHolyWorldHere() && !this.method6149()) {
            int var2 = this.currentAnarchyHere();
            if (this.lobby) {
               if (var2 == -1) {
                  this.lobby = false;
                  this.stopWatch.reset();
               } else if (this.stopWatch.method7544(1500L)) {
                  mc.player.networkHandler.sendChatCommand("hub");
               }
            } else if (var2 == this.anarchy) {
               this.anarchy = 0;
            } else if (mc.currentScreen instanceof GenericContainerScreen var3 && var3.getTitle().getString().toLowerCase(Locale.ROOT).contains("лайт анархии")
               )
             {
               if (System.currentTimeMillis() - ((Handler1)mc.currentScreen).zenithDLC$callGetStartTime() > 4000L) {
                  mc.player.closeHandledScreen();
               } else if (this.stopWatch.method7546(500L)) {
                  byte var6 = 0;
                  byte var5 = 1;
                  if (this.anarchy >= 10 && this.anarchy <= 19) {
                     var6 = 1;
                     var5 = 10;
                  } else if (this.anarchy >= 20 && this.anarchy <= 29) {
                     var6 = 2;
                     var5 = 20;
                  } else if (this.anarchy >= 30 && this.anarchy <= 36) {
                     var6 = 3;
                     var5 = 30;
                  }

                  if (var3.getScreenHandler().getInventory().size() < 10) {
                     InventoryUtils.method904(var6, 0, SlotActionType.PICKUP, false);
                  } else {
                     InventoryUtils.method904(18 + this.anarchy - var5, 0, SlotActionType.PICKUP, false);
                     mc.player.closeHandledScreen();
                  }
               }
            } else {
               if (this.stopWatch.method7544(3000L)) {
                  mc.player.networkHandler.sendChatCommand("lite");
               }
            }
         } else {
            this.anarchy = 0;
            this.lobby = false;
         }
      }
   }

   public void reconnect(int var1) {
      if (var1 >= 1 && var1 <= 36) {
         this.anarchy = var1;
         this.lobby = true;
         this.stopWatch.reset();
      } else {
         HudHolder.method6969().method6972("[RCT]", Text.literal(" Не верный " + Formatting.RED + "лайт"));
      }
   }
}

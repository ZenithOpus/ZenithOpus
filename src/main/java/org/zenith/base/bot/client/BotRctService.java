package org.zenith.base.bot.client;

import java.util.Locale;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.utility.misc.StopWatch;
import org.zenith.utility.render.ServerDetector;

public final class BotRctService {
   private static final int SOLO_MIN_ANARCHY = 1;
   private static final int DUO_MIN_ANARCHY = 18;
   private static final int DUO_MAX_ANARCHY = 38;
   private static final int TRIO_MIN_ANARCHY = 39;
   private static final int TRIO_MAX_ANARCHY = 57;
   private static final int CLAN_MIN_ANARCHY = 58;
   private static final int CLAN_MAX_ANARCHY = 74;
   private static final long COMMAND_INTERVAL_MS = 1500L;
   private static final long LOBBY_CONFIRM_MS = 1500L;
   private static final long MENU_CLICK_INTERVAL_MS = 500L;
   private static final long JOIN_TIMEOUT_MS = 10000L;
   private static final int MENU_CLICK_RETRY_LIMIT = 20;
   private static final String LITE_MENU_TITLE = "лайт анархии";
   private final BotClient client;
   private final StopWatch stopWatch = new StopWatch();
   private int anarchy;
   private boolean lobby;
   private boolean joining;
   private boolean hubCommandSent;
   private long lobbyDetectedAt;
   private boolean waitingForMenuUpdate;
   private int menuClickAttempts;
   private int clickedSyncId = -1;
   private int clickedInventorySize = -1;

   public BotRctService(BotClient var1) {
      this.client = var1;
   }

   public boolean isActive() {
      return this.anarchy != 0;
   }

   public int getTargetAnarchy() {
      return this.anarchy;
   }

   public boolean isHolyWorldHere() {
      BotPlayHandler var1 = this.client.getPlayHandler();
      return var1 != null && ServerDetector.method1634(var1.getBrand());
   }

   public int currentAnarchyHere() {
      return ServerDetector.method1633(this.client.getWorld());
   }

   public void reconnect(int var1) {
      if (this.client.isOnThread()) {
         this.reconnectNow(var1);
      } else {
         this.client.execute(() -> this.reconnectNow(var1));
      }
   }

   private void reconnectNow(int var1) {
      if (var1 >= 1 && var1 <= 74) {
         this.anarchy = var1;
         this.beginLobbyPhase();
      } else {
         this.client.systemMessage("RCT: неверный лайт " + var1);
      }
   }

   public void stop() {
      this.anarchy = 0;
      this.lobby = false;
      this.joining = false;
      this.hubCommandSent = false;
      this.lobbyDetectedAt = 0L;
      this.resetMenuUpdate();
   }

   public void onGameMessage(Text var1) {
      if (this.anarchy != 0) {
         String var2 = var1.getString().toLowerCase(Locale.ROOT);
         if (!var2.contains("хаб") && var2.contains("не удалось")) {
            this.client.systemMessage("RCT: на анархию " + this.anarchy + " нельзя зайти");
            this.stop();
         }
      }
   }

   public void tick(BotWorld var1, BotPlayer var2) {
      if (this.anarchy != 0) {
         BotPlayHandler var3 = this.client.getPlayHandler();
         if (var3 != null && var1 != null && var2 != null) {
            if (!this.isHolyWorldHere()) {
               this.stop();
            } else {
               int var4 = this.currentAnarchyHere();
               if (this.lobby) {
                  this.resetMenuUpdate();
                  if (!this.hubCommandSent) {
                     if (this.stopWatch.method318(1500.0)) {
                        var3.sendCommand("hub");
                        this.hubCommandSent = true;
                        this.lobbyDetectedAt = 0L;
                     }
                  } else if (var4 == -1) {
                     if (this.lobbyDetectedAt == 0L) {
                        this.lobbyDetectedAt = System.currentTimeMillis();
                     }

                     if (System.currentTimeMillis() - this.lobbyDetectedAt >= 1500L) {
                        this.lobby = false;
                        this.hubCommandSent = false;
                        this.lobbyDetectedAt = 0L;
                        this.stopWatch.reset();
                     }
                  } else {
                     this.lobbyDetectedAt = 0L;
                     if (this.stopWatch.method318(1500.0)) {
                        var3.sendCommand("hub");
                     }
                  }
               } else if (var4 == this.anarchy) {
                  this.closeLiteMenu(var2, var3);
                  this.stop();
               } else if (var4 != -1) {
                  this.closeLiteMenu(var2, var3);
                  this.beginLobbyPhase();
               } else if (this.joining) {
                  this.resetMenuUpdate();
                  if (this.stopWatch.method317(10000.0)) {
                     this.beginLobbyPhase();
                  }
               } else {
                  GenericContainerScreenHandler var5 = this.getLiteMenu(var2, var3);
                  if (var5 != null) {
                     if (this.waitingForMenuUpdate && this.hasMenuUpdated(var5)) {
                        this.resetMenuUpdate();
                     }

                     if (this.stopWatch.method318(500.0)) {
                        boolean var6 = var5.getInventory().size() < 10;
                        int[] var7 = this.getAnarchyMenuSlots(this.anarchy);
                        if (var6) {
                           if (this.menuClickAttempts >= 20) {
                              this.restartLiteMenu(var2, var3);
                              return;
                           }

                           var3.getInteractionManager().clickSlot(var5.syncId, var7[0], 0, SlotActionType.PICKUP, var2);
                           this.rememberMenuClick(var5);
                        } else {
                           this.resetMenuUpdate();
                           var3.getInteractionManager().clickSlot(var5.syncId, 18 + this.anarchy - var7[1], 0, SlotActionType.PICKUP, var2);
                           var2.closeHandledScreen();
                           this.joining = true;
                           this.stopWatch.reset();
                        }
                     }
                  } else {
                     this.resetMenuUpdate();
                     if (this.stopWatch.method318(1500.0)) {
                        var3.sendCommand("lite");
                     }
                  }
               }
            }
         }
      }
   }

   private void rememberMenuClick(GenericContainerScreenHandler var1) {
      this.waitingForMenuUpdate = true;
      this.menuClickAttempts++;
      this.clickedSyncId = var1.syncId;
      this.clickedInventorySize = var1.getInventory().size();
   }

   private boolean hasMenuUpdated(GenericContainerScreenHandler var1) {
      return var1.syncId != this.clickedSyncId || var1.getInventory().size() != this.clickedInventorySize;
   }

   private void restartLiteMenu(BotPlayer var1, BotPlayHandler var2) {
      this.resetMenuUpdate();
      var1.closeHandledScreen();
      var2.sendCommand("lite");
      this.stopWatch.reset();
   }

   private void resetMenuUpdate() {
      this.waitingForMenuUpdate = false;
      this.menuClickAttempts = 0;
      this.clickedSyncId = -1;
      this.clickedInventorySize = -1;
   }

   private void beginLobbyPhase() {
      this.lobby = true;
      this.joining = false;
      this.hubCommandSent = false;
      this.lobbyDetectedAt = 0L;
      this.resetMenuUpdate();
      this.stopWatch.method320(1500L);
   }

   private GenericContainerScreenHandler getLiteMenu(BotPlayer var1, BotPlayHandler var2) {
      Text var3 = var2.getCurrentScreenTitle();
      if (var3 != null && var3.getString().toLowerCase(Locale.ROOT).contains("лайт анархии")) {
         return var1.currentScreenHandler instanceof GenericContainerScreenHandler var4 ? var4 : null;
      } else {
         return null;
      }
   }

   private void closeLiteMenu(BotPlayer var1, BotPlayHandler var2) {
      if (this.getLiteMenu(var1, var2) != null) {
         var1.closeHandledScreen();
      }
   }

   private int[] getAnarchyMenuSlots(int var1) {
      if (var1 >= 18 && var1 <= 38) {
         return new int[]{1, 18};
      } else if (var1 >= 39 && var1 <= 57) {
         return new int[]{2, 39};
      } else {
         return var1 >= 58 && var1 <= 74 ? new int[]{3, 58} : new int[]{0, 1};
      }
   }
}

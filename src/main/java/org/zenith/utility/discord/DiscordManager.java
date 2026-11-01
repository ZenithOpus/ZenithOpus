package org.zenith.utility.discord;

import java.io.IOException;
import java.util.Locale;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import org.zenith.Zenith;
import org.zenith.client.screens.nlgui.chat.ChatCompose;
import org.zenith.client.screens.nlgui.panel.ProfilePanel;
import org.zenith.utility.api.CloudChatClient;
import org.zenith.utility.api.RegionUtil;
import org.zenith.utility.discord.utils.DiscordEventHandlers;
import org.zenith.utility.discord.utils.DiscordRPC;
import org.zenith.utility.discord.utils.DiscordRichPresence;
import org.zenith.utility.discord.utils.RPCButton;
import org.zenith.utility.misc.ServerNameUtils;
import org.zenith.utility.render.PartyManager;

public class DiscordManager {
   private static final DiscordManager.DiscordInfo OFFLINE = new DiscordManager.DiscordInfo("Unknown", "", "");
   private static final long PUSH_MS = 5000L;
   private final DiscordManager.DiscordDaemonThread discordDaemonThread = new DiscordManager.DiscordDaemonThread();
   private volatile boolean running = true;
   private volatile boolean connected;
   private DiscordManager.DiscordInfo info = OFFLINE;
   private final long sessionStart = System.currentTimeMillis() / 1000L;
   private String lastPushed = "";
   private long lastPushAt;
   private DiscordEventHandlers handlers;

   public void init() {
      String var1 = System.getProperty("os.name").toLowerCase();
      if (!var1.contains("linux")) {
         this.discordDaemonThread.start();
      }
   }

   public void stopRPC() {
      this.running = false;
      this.disconnect();
   }

   private boolean wanted() {
      try {
         ProfilePanel var1 = Zenith.getInstance().getNLMenuScreen().getProfilePanel();
         return var1 == null || var1.getDiscordRPC().getSetting().isEnabled();
      } catch (Exception var2) {
         return true;
      }
   }

   private void connect() {
      this.handlers = new DiscordEventHandlers.Builder()
         .ready(
            var1 -> {
               Zenith.getInstance()
                  .getDiscordManager()
                  .setInfo(
                     new DiscordManager.DiscordInfo(
                        var1.username, "https://cdn.discordapp.com/avatars/" + var1.userId + "/" + var1.avatar + ".png", var1.userId
                     )
                  );
               this.lastPushed = "";
               this.lastPushAt = 0L;
               this.push();
            }
         )
         .build();
      DiscordRPC.INSTANCE.Discord_Initialize("1524110600555331776", this.handlers, true, "");
      this.connected = true;
   }

   private void push() {
      String var1 = identity();
      String var2 = withStatus(where());
      String var3 = var1 + "\u0000" + var2;
      long var4 = System.currentTimeMillis();
      if (!var3.equals(this.lastPushed) && var4 - this.lastPushAt >= 5000L) {
         this.lastPushed = var3;
         this.lastPushAt = var4;
         DiscordRichPresence var6 = new DiscordRichPresence.Builder()
            .setStartTimestamp(this.sessionStart)
            .setDetails(var1)
            .setState(var2)
            .setLargeImage("https://s13.gifyu.com/images/blr1p.png", "https://t.me/moluchclient")
            .setButtons(RPCButton.create("Telegram", "https://t.me/moluchclient"), RPCButton.create("Сайт", RegionUtil.method2038().get(0) + "/"))
            .build();
         DiscordRPC.INSTANCE.Discord_UpdatePresence(var6);
      }
   }

   private static String identity() {
      return "cracked by myseleek ";
   }

   private static String roleLabel() {
      String var0 = "";

      try {
         String var1 = CloudChatClient.getInstance().getText();
         var0 = var1 == null ? "" : var1.trim();
      } catch (Throwable var4) {
      }

      if (var0.isEmpty()) {
         try {
            var0 = Zenith.getInstance().getEntry().method1504().getName();
         } catch (Exception var3) {
            return "";
         }
      }

      String var5 = var0.toLowerCase(Locale.ROOT);

      return switch (var5) {
         case "administrator", "admin" -> "Админ";
         case "support", "moderator" -> "Модератор";
         case "media" -> "Медиа";
         case "private" -> "Private";
         case "opus", "recode" -> "Opus";
         case "opus/private" -> "Opus/Private";
         case "legacy" -> "Legacy";
         case "autobuy" -> "AutoBuy";
         case "alpha" -> "Opus/Private";
         case "user" -> "Игрок";
         default -> var0;
      };
   }

   private static String withStatus(String var0) {
      try {
         PartyManager var1 = PartyManager.method17();
         if (var1 == null) {
            return var0;
         } else {
            String var2 = var1.method31();
            String var3 = Zenith.getInstance().getShare().text.getValue();
            String var4 = !"online".equals(var2) && !"menu".equals(var2) ? PartyManager.method50(var2) : "";
            if (var3 != null && !var3.isBlank()) {
               var4 = var4.isEmpty() ? var3.strip() : var4 + ": " + var3.strip();
            }

            String var5 = var4.isEmpty() ? var0 : var0 + " · " + var4;
            return var5.length() > 120 ? var5.substring(0, 120) : var5;
         }
      } catch (Throwable var6) {
         return var0;
      }
   }

   private static String where() {
      try {
         String var0 = ChatCompose.server();
         if (var0.isEmpty()) {
            return "В главном меню";
         } else if (var0.startsWith("singleplayer")) {
            return "Одиночная игра";
         } else {
            String var1 = ServerNameUtils.method5643(var0, ChatCompose.mode());
            return var1.isEmpty() ? "В игре" : var1;
         }
      } catch (Throwable var2) {
         return "В главном меню";
      }
   }

   private void disconnect() {
      if (this.connected) {
         this.connected = false;
         this.lastPushed = "";
         this.lastPushAt = 0L;
         DiscordRPC.INSTANCE.Discord_Shutdown();
         this.handlers = null;
         this.info = OFFLINE;
      }
   }

   public void load() throws IOException {
   }

   @Generated
   public void setRunning(boolean var1) {
      this.running = var1;
   }

   @Generated
   public void setConnected(boolean var1) {
      this.connected = var1;
   }

   @Generated
   public void setInfo(DiscordManager.DiscordInfo var1) {
      this.info = var1;
   }

   @Generated
   public void setLastPushed(String var1) {
      this.lastPushed = var1;
   }

   @Generated
   public void setLastPushAt(long var1) {
      this.lastPushAt = var1;
   }

   @Generated
   public DiscordManager.DiscordDaemonThread getDiscordDaemonThread() {
      return this.discordDaemonThread;
   }

   @Generated
   public boolean isRunning() {
      return this.running;
   }

   @Generated
   public boolean isConnected() {
      return this.connected;
   }

   @Generated
   public DiscordManager.DiscordInfo getInfo() {
      return this.info;
   }

   @Generated
   public long getSessionStart() {
      return this.sessionStart;
   }

   @Generated
   public String getLastPushed() {
      return this.lastPushed;
   }

   @Generated
   public long getLastPushAt() {
      return this.lastPushAt;
   }

   private class DiscordDaemonThread extends Thread {
      @Override
      public void run() {
         this.setName("Discord-RPC");

         try {
            for (MinecraftClient var1 = MinecraftClient.getInstance(); var1 == null || var1.getTextureManager() == null; var1 = MinecraftClient.getInstance()) {
               Thread.sleep(500L);
            }

            for (; DiscordManager.this.running; Thread.sleep(500L)) {
               boolean var2 = DiscordManager.this.wanted();
               if (var2 && !DiscordManager.this.connected) {
                  DiscordManager.this.connect();
               } else if (!var2 && DiscordManager.this.connected) {
                  DiscordManager.this.disconnect();
               }

               if (DiscordManager.this.connected) {
                  DiscordRPC.INSTANCE.Discord_RunCallbacks();
                  DiscordManager.this.push();
                  DiscordManager.this.load();
               }
            }
         } catch (Exception var3) {
            DiscordManager.this.stopRPC();
         }

         super.run();
      }
   }

   public static final class DiscordInfo {
      private final String userName;
      private final String avatarUrl;
      private final String userId;

      public String userName() {
         return this.userName;
      }

      public String avatarUrl() {
         return this.avatarUrl;
      }

      public String userId() {
         return this.userId;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof DiscordInfo other)) return false;
         return java.util.Objects.equals(this.userName, other.userName) && java.util.Objects.equals(this.avatarUrl, other.avatarUrl) && java.util.Objects.equals(this.userId, other.userId);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(userName, avatarUrl, userId);
      }

      @Override
      public String toString() {
         return "DiscordInfo[" + "userName=" + this.userName + ", " + "avatarUrl=" + this.avatarUrl + ", " + "userId=" + this.userId + "]";
      }

      public DiscordInfo(String var1, String var2, String var3) {
         this.userName = var1;
         this.avatarUrl = var2;
         this.userId = var3;
      }
   }
}

package org.zenith.base.bot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerInfo.ServerType;
import net.minecraft.client.network.ServerInfo.Status;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.network.NetworkingBackend;
import net.minecraft.util.Identifier;

public final class ServerIconCache {
   private static final MultiplayerServerListPinger pinger = new MultiplayerServerListPinger();
   private static final Map<String, ServerIconCache.Entry> entries = new ConcurrentHashMap<>();
   private static final ExecutorService pingExecutor = Executors.newFixedThreadPool(2, var0 -> {
      Thread var1 = new Thread(var0, "zenith-bot-server-pinger");
      var1.setDaemon(true);
      return var1;
   });

   private ServerIconCache() {
   }

   public static Identifier get(String var0) {
      if (var0 != null && !var0.isEmpty() && !"No Server".equalsIgnoreCase(var0)) {
         ServerIconCache.Entry var1 = entries.get(var0);
         if (var1 == null) {
            MinecraftClient var2 = MinecraftClient.getInstance();
            ServerInfo var3 = new ServerInfo(var0, var0, ServerType.OTHER);
            WorldIcon var4 = WorldIcon.forServer(var2.getTextureManager(), var0);
            var1 = new ServerIconCache.Entry(var3, var4);
            ServerIconCache.Entry var5 = entries.putIfAbsent(var0, var1);
            if (var5 != null) {
               var4.close();
               var1 = var5;
            } else {
               startPing(var1);
            }
         }

         return var1.icon.getTextureId();
      } else {
         return null;
      }
   }

   private static void startPing(ServerIconCache.Entry var0) {
      var0.info.setStatus(Status.PINGING);
      pingExecutor.execute(() -> {
         try {
            pinger.add(var0.info, () -> {}, () -> {}, NetworkingBackend.remote(false));
         } catch (Exception var2) {
            var0.info.setStatus(Status.UNREACHABLE);
         }
      });
   }

   public static void tick() {
      try {
         pinger.tick();
      } catch (Throwable var5) {
      }

      for (ServerIconCache.Entry var1 : entries.values()) {
         byte[] var2 = var1.info.getFavicon();
         if (var2 != null && var2 != var1.lastFavicon) {
            try {
               var1.icon.load(NativeImage.read(var2));
               var1.lastFavicon = var2;
            } catch (Throwable var4) {
            }
         }
      }
   }

   public static void clear() {
      try {
         pinger.cancel();
      } catch (Throwable var4) {
      }

      for (ServerIconCache.Entry var1 : entries.values()) {
         try {
            var1.icon.close();
         } catch (Throwable var3) {
         }
      }

      entries.clear();
   }

   private static final class Entry {
      public final ServerInfo info;
      public final WorldIcon icon;
      public byte[] lastFavicon;

      public Entry(ServerInfo var1, WorldIcon var2) {
         this.info = var1;
         this.icon = var2;
      }
   }
}

package org.zenith.base.bot.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.NetworkingBackend;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.zenith.ChatLogger;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.bot.net.BotProxy;

public final class HeadlessBots {
   private static final long MASS_CONNECT_STAGGER_MS = 500L;
   private static final int MAX_MASS_CONNECT_BOTS = 256;
   private static final Map<String, BotClient> BOTS = new ConcurrentHashMap<>();
   private static final Map<String, HeadlessBots.SavedBot> SAVED = new LinkedHashMap<>();
   private static final Map<String, JsonObject> PROFILES = new ConcurrentHashMap<>();
   private static final ScheduledExecutorService CONNECT_DELAY_EXECUTOR = Executors.newSingleThreadScheduledExecutor(var0 -> {
      Thread var1 = new Thread(var0, "hbot-connect-delay");
      var1.setDaemon(true);
      return var1;
   });
   private static final AtomicLong CONNECT_BATCH_GENERATION = new AtomicLong();
   private static volatile String lastAddress = "";
   private static final Set<String> SAVED_PROXIES = ConcurrentHashMap.newKeySet();
   private static final Map<String, HeadlessBots.ProxyPingState> PROXY_PINGS = new ConcurrentHashMap<>();
   private static final String PROXY_PING_TARGET_HOST = "mc.holyworld.ru";
   private static final int PROXY_PING_TARGET_PORT = 25565;
   private static final long PROXY_PING_INTERVAL_MS = 10000L;
   private static final long PROXY_PING_TIMEOUT_MS = 5000L;
   private static final int MAX_PENDING_PROXY_PINGS = 64;
   private static final ThreadPoolExecutor PROXY_PING_EXECUTOR = new ThreadPoolExecutor(
      2, 2, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(64), new ThreadFactory() {
         private int index;

         @Override
         public synchronized Thread newThread(Runnable var1) {
            Thread var2 = new Thread(var1, "hbot-proxy-ping-" + this.index++);
            var2.setDaemon(true);
            return var2;
         }
      }
   );

   private HeadlessBots() {
   }

   public static BotClient connect(String var0, String var1, int var2) {
      add(var0);
      setLastAddress(var1 + (var2 == 25565 ? "" : ":" + var2));
      return connect(BotClientConfig.offline(var0, var1, var2, getProxy(var0), getProtocolVersion(var0)));
   }

   public static BotClient connect(BotClientConfig var0) {
      String var1 = key(var0.name());
      BotClient var2 = BOTS.get(var1);
      if (var2 != null && var2.getPhase() != BotPhase.DISCONNECTED) {
         return var2;
      } else {
         BotClient var3 = BotClient.start(var0, new HeadlessBots.ChatReportingEvents());
         BOTS.put(var1, var3);
         applyProfile(var3);
         return var3;
      }
   }

   public static void connectAll(Collection<String> var0, String var1, int var2) {
      if (var0 != null && !var0.isEmpty()) {
         long var3 = CONNECT_BATCH_GENERATION.incrementAndGet();
         ArrayList var5 = new ArrayList();

         for (String var7 : var0) {
            if (var5.size() >= 256) {
               break;
            }

            if (var7 != null && !var7.isBlank()) {
               var5.add(var7.trim());
            }
         }

         if (!var5.isEmpty()) {
            connectMassBatch(var5, 0, var1, var2, var3);
         }
      }
   }

   private static void connectMassBatch(List<String> var0, int var1, String var2, int var3, long var4) {
      if (CONNECT_BATCH_GENERATION.get() == var4 && var1 < var0.size()) {
         connect((String)var0.get(var1), var2, var3);
         int var6 = var1 + 1;
         if (var6 < var0.size()) {
            CONNECT_DELAY_EXECUTOR.schedule(() -> connectMassBatch(var0, var6, var2, var3, var4), 500L, TimeUnit.MILLISECONDS);
         }
      }
   }

   @Nullable
   public static BotClient get(String var0) {
      return BOTS.get(key(var0));
   }

   public static Collection<BotClient> all() {
      return BOTS.values();
   }

   public static boolean isOnline(String var0) {
      BotClient var1 = get(var0);
      return var1 != null && var1.getPhase() != BotPhase.DISCONNECTED;
   }

   public static boolean disconnect(String var0) {
      BotClient var1 = BOTS.remove(key(var0));
      if (var1 == null) {
         return false;
      } else {
         var1.disconnect("disconnected by user");
         return true;
      }
   }

   public static void disconnectAll() {
      CONNECT_BATCH_GENERATION.incrementAndGet();

      for (BotClient var1 : BOTS.values()) {
         var1.disconnect("disconnected by user");
      }

      BOTS.clear();
   }

   public static boolean add(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String var1 = var0.trim();
         synchronized (SAVED) {
            HeadlessBots.SavedBot var3 = SAVED.putIfAbsent(key(var1), new HeadlessBots.SavedBot(var1, null, -1));
            return var3 == null;
         }
      } else {
         return false;
      }
   }

   public static void remove(String var0) {
      disconnect(var0);
      synchronized (SAVED) {
         SAVED.remove(key(var0));
      }

      PROFILES.remove(key(var0));
   }

   public static List<HeadlessBots.SavedBot> savedBots() {
      synchronized (SAVED) {
         return List.copyOf(SAVED.values());
      }
   }

   public static List<String> allNames() {
      ArrayList<String> var0 = new ArrayList<>();
      HashSet var1 = new HashSet();
      synchronized (SAVED) {
         for (HeadlessBots.SavedBot var4 : SAVED.values()) {
            var0.add(var4.name());
            var1.add(key(var4.name()));
         }
      }

      for (BotClient var8 : BOTS.values()) {
         if (var1.add(key(var8.getName()))) {
            var0.add(var8.getName());
         }
      }

      return var0;
   }

   public static void setProxy(String var0, @Nullable String var1) {
      String var2 = var1 != null && !var1.isBlank() ? var1.trim() : null;
      if (var2 != null) {
         addProxy(var2);
      }

      synchronized (SAVED) {
         HeadlessBots.SavedBot var4 = SAVED.get(key(var0));
         if (var4 != null) {
            SAVED.put(key(var0), new HeadlessBots.SavedBot(var4.name(), var2, var4.protocolVersion()));
         }
      }
   }

   @Nullable
   public static String getProxy(String var0) {
      synchronized (SAVED) {
         HeadlessBots.SavedBot var2 = SAVED.get(key(var0));
         return var2 == null ? null : var2.proxy();
      }
   }

   public static boolean hasProxy(String var0) {
      String var1 = getProxy(var0);
      return var1 != null && !var1.isBlank();
   }

   public static void setProtocolVersion(String var0, int var1) {
      int var2 = var1 <= 0 ? -1 : var1;
      synchronized (SAVED) {
         HeadlessBots.SavedBot var4 = SAVED.get(key(var0));
         if (var4 != null) {
            SAVED.put(key(var0), new HeadlessBots.SavedBot(var4.name(), var4.proxy(), var2));
         }
      }
   }

   public static int getProtocolVersion(String var0) {
      synchronized (SAVED) {
         HeadlessBots.SavedBot var2 = SAVED.get(key(var0));
         return var2 == null ? -1 : var2.protocolVersion();
      }
   }

   public static List<String> getProxyPool() {
      ArrayList var0 = new ArrayList();

      for (String var2 : SAVED_PROXIES) {
         addUniqueProxy(var0, var2);
      }

      synchronized (SAVED) {
         for (HeadlessBots.SavedBot var3 : SAVED.values()) {
            if (var3.proxy() != null) {
               addUniqueProxy(var0, var3.proxy());
            }
         }
      }

      return List.copyOf(var0);
   }

   public static boolean addProxy(String var0) {
      String var1 = var0 == null ? "" : var0.trim();
      if (!var1.isBlank() && !containsProxy(SAVED_PROXIES, var1)) {
         SAVED_PROXIES.add(var1);
         requestProxyPing(var1, true);
         return true;
      } else {
         return false;
      }
   }

   public static boolean removeProxy(String var0) {
      String var1 = var0 == null ? "" : var0.trim();
      if (var1.isBlank()) {
         return false;
      } else {
         boolean var2 = SAVED_PROXIES.removeIf(var1x -> var1x.equalsIgnoreCase(var1));
         synchronized (SAVED) {
            for (Entry var5 : SAVED.entrySet()) {
               HeadlessBots.SavedBot var6 = (HeadlessBots.SavedBot)var5.getValue();
               if (var6.proxy() != null && var6.proxy().equalsIgnoreCase(var1)) {
                  var5.setValue(new HeadlessBots.SavedBot(var6.name(), null, var6.protocolVersion()));
                  var2 = true;
               }
            }
         }

         PROXY_PINGS.remove(proxyPingKey(var1));
         return var2;
      }
   }

   public static int getProxyUseCount(String var0) {
      String var1 = var0 == null ? "" : var0.trim();
      if (var1.isBlank()) {
         return 0;
      } else {
         int var2 = 0;
         synchronized (SAVED) {
            for (HeadlessBots.SavedBot var5 : SAVED.values()) {
               if (var5.proxy() != null && var5.proxy().equalsIgnoreCase(var1)) {
                  var2++;
               }
            }

            return var2;
         }
      }
   }

   public static HeadlessBots.ProxyPingSnapshot getProxyPing(String var0) {
      String var1 = var0 == null ? "" : var0.trim();
      if (var1.isBlank()) {
         return new HeadlessBots.ProxyPingSnapshot(HeadlessBots.ProxyPingStatus.UNKNOWN, -1);
      } else {
         HeadlessBots.ProxyPingState var2 = requestProxyPing(var1, false);
         return new HeadlessBots.ProxyPingSnapshot(var2.status, var2.latencyMs);
      }
   }

   private static HeadlessBots.ProxyPingState requestProxyPing(String var0, boolean var1) {
      String var2 = var0 == null ? "" : var0.trim();
      if (var2.isBlank()) {
         return new HeadlessBots.ProxyPingState();
      } else {
         HeadlessBots.ProxyPingState var3 = PROXY_PINGS.computeIfAbsent(proxyPingKey(var2), var0x -> new HeadlessBots.ProxyPingState());
         long var4 = System.currentTimeMillis();
         synchronized (var3) {
            if (var3.inFlight) {
               return var3;
            }

            if (!var1 && var3.status != HeadlessBots.ProxyPingStatus.UNKNOWN && var4 - var3.lastStartedAt < 10000L) {
               return var3;
            }

            var3.inFlight = true;
            if (var3.status == HeadlessBots.ProxyPingStatus.UNKNOWN) {
               var3.status = HeadlessBots.ProxyPingStatus.CHECKING;
            }

            var3.lastStartedAt = var4;
         }

         try {
            PROXY_PING_EXECUTOR.execute(() -> pingProxy(var2, var3));
         } catch (RejectedExecutionException var10) {
            synchronized (var3) {
               var3.latencyMs = -1;
               var3.status = HeadlessBots.ProxyPingStatus.FAILED;
               var3.inFlight = false;
            }
         }

         return var3;
      }
   }

   private static void pingProxy(String var0, HeadlessBots.ProxyPingState var1) {
      long var2 = System.nanoTime();

      try {
         BotProxy var4 = BotProxy.parse(var0);
         if (var4 == null) {
            throw new IllegalArgumentException("Invalid proxy");
         }

         final ProxyHandler var5 = var4.createHandler();
         InetSocketAddress var6 = InetSocketAddress.createUnresolved("mc.holyworld.ru", 25565);
         EventLoopGroup var7 = NetworkingBackend.remote(false).getEventLoopGroup();
         Bootstrap var8 = (Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(var7)).channel(NioSocketChannel.class))
               .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000))
            .handler(new ChannelInitializer<Channel>() {
               public void initChannel(Channel var1) {
                  var1.pipeline().addLast("timeout", new ReadTimeoutHandler((int)Math.ceil(5.0)));
                  var1.pipeline().addLast("proxy", var5);
               }
            });
         ChannelFuture var9 = var8.connect(var6);
         Channel var10 = var9.channel();

         try {
            if (!var9.await(5000L)) {
               var9.cancel(true);
               throw new SocketTimeoutException("Proxy connect timeout");
            }

            if (!var9.isSuccess()) {
               throw asException(var9.cause());
            }

            Future var11 = var5.connectFuture();
            if (!var11.await(5000L)) {
               throw new SocketTimeoutException("Proxy handshake timeout");
            }

            if (!var11.isSuccess()) {
               throw asException(var11.cause());
            }
         } finally {
            var10.close();
         }

         int var23 = Math.max(1, Math.round((float)(System.nanoTime() - var2) / 1000000.0F));
         synchronized (var1) {
            var1.latencyMs = var23;
            var1.status = HeadlessBots.ProxyPingStatus.OK;
            var1.inFlight = false;
         }
      } catch (Exception var22) {
         synchronized (var1) {
            var1.latencyMs = -1;
            var1.status = HeadlessBots.ProxyPingStatus.FAILED;
            var1.inFlight = false;
         }
      }
   }

   private static Exception asException(Throwable var0) {
      return var0 instanceof Exception var1 ? var1 : new Exception(var0);
   }

   private static String proxyPingKey(String var0) {
      return var0 == null ? "" : var0.trim().toLowerCase(Locale.ROOT);
   }

   private static void addUniqueProxy(List<String> var0, String var1) {
      String var2 = var1 == null ? "" : var1.trim();
      if (!var2.isBlank() && !containsProxy(var0, var2)) {
         var0.add(var2);
      }
   }

   private static boolean containsProxy(Collection<String> var0, String var1) {
      for (String var3 : var0) {
         if (var3.equalsIgnoreCase(var1)) {
            return true;
         }
      }

      return false;
   }

   public static String getLastAddress() {
      return lastAddress;
   }

   public static void setLastAddress(String var0) {
      lastAddress = var0 == null ? "" : var0.trim();
   }

   private static void applyProfile(BotClient var0) {
      JsonObject var1 = PROFILES.get(key(var0.getName()));
      if (var1 != null) {
         var0.execute(() -> {
            for (BotModule var3 : var0.getModules().getModules()) {
               if (var1.has(var3.getName()) && var1.get(var3.getName()).isJsonObject()) {
                  var3.load(var1.getAsJsonObject(var3.getName()));
               }
            }
         });
      }
   }

   public static void persistProfile(BotClient var0) {
      JsonObject var1 = new JsonObject();

      for (BotModule var3 : var0.getModules().getModules()) {
         var1.add(var3.getName(), var3.save());
      }

      PROFILES.put(key(var0.getName()), var1);
   }

   public static void setModuleEnabled(String var0, String var1, boolean var2) {
      BotClient var3 = get(var0);
      if (var3 != null && var3.getPhase() != BotPhase.DISCONNECTED) {
         var3.execute(() -> {
            var3.getModules().setEnabled(var1, var2);
            persistProfile(var3);
         });
      } else {
         JsonObject var4 = PROFILES.computeIfAbsent(key(var0), var0x -> new JsonObject());
         JsonObject var5 = var4.has(var1) && var4.get(var1).isJsonObject() ? var4.getAsJsonObject(var1) : new JsonObject();
         var5.addProperty("enabled", var2);
         var4.add(var1, var5);
      }
   }

   public static boolean isModuleEnabledInProfile(String var0, String var1) {
      JsonObject var2 = PROFILES.get(key(var0));
      if (var2 != null && var2.has(var1) && var2.get(var1).isJsonObject()) {
         JsonObject var3 = var2.getAsJsonObject(var1);
         return var3.has("enabled") && var3.get("enabled").getAsBoolean();
      } else {
         return false;
      }
   }

   public static JsonObject saveState() {
      JsonObject var0 = new JsonObject();
      JsonArray var1 = new JsonArray();
      synchronized (SAVED) {
         for (HeadlessBots.SavedBot var4 : SAVED.values()) {
            JsonObject var5 = new JsonObject();
            var5.addProperty("name", var4.name());
            if (var4.proxy() != null) {
               var5.addProperty("proxy", var4.proxy());
            }

            if (var4.protocolVersion() > 0) {
               var5.addProperty("version", var4.protocolVersion());
            }

            var1.add(var5);
         }
      }

      var0.add("savedBots", var1);
      JsonObject var8 = new JsonObject();

      for (Entry var11 : PROFILES.entrySet()) {
         var8.add((String)var11.getKey(), (JsonElement)var11.getValue());
      }

      var0.add("botProfiles", var8);
      JsonArray var10 = new JsonArray();

      for (String var13 : getProxyPool()) {
         var10.add(var13);
      }

      var0.add("proxyPool", var10);
      var0.addProperty("lastAddress", lastAddress);
      return var0;
   }

   public static void loadState(JsonObject var0) {
      if (var0 != null) {
         if (var0.has("BotData") && var0.get("BotData").isJsonObject()) {
            var0 = var0.getAsJsonObject("BotData");
         }

         if (var0.has("savedBots") && var0.get("savedBots").isJsonArray()) {
            JsonArray var1 = var0.getAsJsonArray("savedBots");
            synchronized (SAVED) {
               for (int var3 = 0; var3 < var1.size(); var3++) {
                  try {
                     if (var1.get(var3).isJsonObject()) {
                        JsonObject var4 = var1.get(var3).getAsJsonObject();
                        String var5 = var4.get("name").getAsString();
                        String var6 = var4.has("proxy") ? var4.get("proxy").getAsString() : null;
                        int var7 = var4.has("version") ? var4.get("version").getAsInt() : -1;
                        if (var5 != null && !var5.isBlank()) {
                           SAVED.putIfAbsent(key(var5), new HeadlessBots.SavedBot(var5.trim(), var6, var7));
                           if (var6 != null && !var6.isBlank()) {
                              addProxy(var6);
                           }
                        }
                     } else {
                        String var17 = var1.get(var3).getAsString();
                        if (var17 != null && !var17.isBlank()) {
                           SAVED.putIfAbsent(key(var17), new HeadlessBots.SavedBot(var17.trim(), null, -1));
                        }
                     }
                  } catch (Exception var10) {
                  }
               }
            }
         }

         if (var0.has("botProfiles") && var0.get("botProfiles").isJsonObject()) {
            JsonObject var12 = var0.getAsJsonObject("botProfiles");

            for (String var16 : var12.keySet()) {
               if (var12.get(var16).isJsonObject()) {
                  PROFILES.put(var16.toLowerCase(Locale.ROOT), var12.getAsJsonObject(var16));
               }
            }
         }

         if (var0.has("proxyPool") && var0.get("proxyPool").isJsonArray()) {
            JsonArray var13 = var0.getAsJsonArray("proxyPool");

            for (int var15 = 0; var15 < var13.size(); var15++) {
               try {
                  addProxy(var13.get(var15).getAsString());
               } catch (Exception var9) {
               }
            }
         }

         if (var0.has("lastAddress")) {
            setLastAddress(var0.get("lastAddress").getAsString());
         }
      }
   }

   public static void loadPersistentState() {
      loadState(BotPersistence.load());
   }

   public static void savePersistentState() {
      try {
         BotPersistence.save(saveState());
      } catch (Exception var1) {
      }
   }

   private static String key(String var0) {
      return var0.toLowerCase(Locale.ROOT);
   }

   private static final class ChatReportingEvents implements BotClientEvents {
      @Override
      public void onPhaseChanged(BotClient var1, BotPhase var2, BotPhase var3) {
         info("[бот " + var1.getName() + "] фаза: " + var3);
      }

      @Override
      public void onJoined(BotClient var1) {
         info("[бот " + var1.getName() + "] зашёл в мир");
      }

      @Override
      public void onDisconnected(BotClient var1, Text var2) {
         HeadlessBots.BOTS.remove(var1.getName().toLowerCase(Locale.ROOT), var1);
         info("[бот " + var1.getName() + "] отключён: " + var2.getString());
      }

      private static void info(String var0) {
         MinecraftClient var1 = MinecraftClient.getInstance();
         if (var1 != null) {
            var1.execute(() -> ChatLogger.method2020(ChatLogger.Type.INFO, var0));
         }
      }
   }

   public static final class ProxyPingSnapshot {
      private final HeadlessBots.ProxyPingStatus status;
      private final int latencyMs;

      public HeadlessBots.ProxyPingStatus status() {
         return this.status;
      }

      public int latencyMs() {
         return this.latencyMs;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof ProxyPingSnapshot other)) return false;
         return java.util.Objects.equals(this.status, other.status) && java.util.Objects.equals(this.latencyMs, other.latencyMs);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(status, latencyMs);
      }

      @Override
      public String toString() {
         return "ProxyPingSnapshot[" + "status=" + this.status + ", " + "latencyMs=" + this.latencyMs + "]";
      }

      public ProxyPingSnapshot(HeadlessBots.ProxyPingStatus var1, int var2) {
         this.status = var1;
         this.latencyMs = var2;
      }

      public String label() {
         return switch (this.status) {
            case UNKNOWN -> "--";
            case CHECKING -> this.latencyMs >= 0 ? this.latencyMs + "ms" : "--";
            case OK -> this.latencyMs + "ms";
            case FAILED -> "err";
         };
      }
   }

   private static final class ProxyPingState {
      private volatile HeadlessBots.ProxyPingStatus status = HeadlessBots.ProxyPingStatus.UNKNOWN;
      private volatile int latencyMs = -1;
      private volatile long lastStartedAt;
      private volatile boolean inFlight;
   }

   public static enum ProxyPingStatus {
      UNKNOWN,
      CHECKING,
      OK,
      FAILED;
   }

   public static final class SavedBot {
      private final String name;
      @Nullable
      private final String proxy;
      private final int protocolVersion;

      public String name() {
         return this.name;
      }

      public String proxy() {
         return this.proxy;
      }

      public int protocolVersion() {
         return this.protocolVersion;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof SavedBot other)) return false;
         return java.util.Objects.equals(this.name, other.name) && java.util.Objects.equals(this.proxy, other.proxy) && java.util.Objects.equals(this.protocolVersion, other.protocolVersion);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(name, proxy, protocolVersion);
      }

      @Override
      public String toString() {
         return "SavedBot[" + "name=" + this.name + ", " + "proxy=" + this.proxy + ", " + "protocolVersion=" + this.protocolVersion + "]";
      }

      public SavedBot(String var1, @Nullable String var2, int var3) {
         this.name = var1;
         this.proxy = var2;
         this.protocolVersion = var3;
      }
   }
}

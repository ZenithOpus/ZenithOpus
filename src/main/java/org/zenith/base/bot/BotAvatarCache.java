package org.zenith.base.bot;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public final class BotAvatarCache {
   private static final String NAMESPACE = "zenith";
   private static final String PREFIX = "bot_avatar/";
   private static final String AVATAR_URL = "https://minotar.net/helm/%s/64.png";
   private static final Map<String, Identifier> nameToId = new ConcurrentHashMap<>();
   private static final Map<String, Long> loading = new ConcurrentHashMap<>();
   private static final AtomicLong generation = new AtomicLong();
   private static final AtomicLong requestIds = new AtomicLong();
   private static final long RETRY_MIN_MS = 30000L;
   private static final long RETRY_MAX_MS = 300000L;
   private static final Map<String, BotAvatarCache.Retry> retryAfter = new ConcurrentHashMap<>();
   private static final ThreadPoolExecutor loader = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(128), var0 -> {
      Thread var1 = new Thread(var0, "bot-avatar-loader");
      var1.setDaemon(true);
      return var1;
   });

   private BotAvatarCache() {
   }

   public static Identifier prewarm(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         String var1 = var0.toLowerCase(Locale.ROOT);
         Identifier var2 = nameToId.get(var1);
         if (var2 != null) {
            return var2;
         } else {
            requestLoad(var0, var1);
            return null;
         }
      } else {
         return null;
      }
   }

   private static void requestLoad(String var0, String var1) {
      BotAvatarCache.Retry var2 = retryAfter.get(var1);
      if (var2 == null || System.currentTimeMillis() >= var2.at()) {
         long var3 = generation.get();
         long var5 = requestIds.incrementAndGet();
         if (loading.putIfAbsent(var1, var5) == null) {
            Runnable var7 = () -> {
               try {
                  byte[] var6 = fetch(var0, var1);
                  if (var6 == null || var6.length == 0) {
                     markFailed(var1, var3);
                     loading.remove(var1, var5);
                     return;
                  }

                  NativeImage var7x;
                  try (ByteArrayInputStream var8 = new ByteArrayInputStream(var6)) {
                     var7x = NativeImage.read(var8);
                  }

                  MinecraftClient var15 = MinecraftClient.getInstance();

                  try {
                     var15.execute(() -> install(var1, var7x, var3, var5));
                  } catch (Throwable var12) {
                     var7x.close();
                     loading.remove(var1, var5);
                  }
               } catch (Throwable var14) {
                  markFailed(var1, var3);
                  loading.remove(var1, var5);
               }
            };

            try {
               loader.execute(var7);
            } catch (RejectedExecutionException var9) {
               loading.remove(var1, var5);
            }
         }
      }
   }

   private static void install(String var0, NativeImage var1, long var2, long var4) {
      NativeImageBackedTexture var6 = null;
      boolean var7 = false;

      try {
         if (var2 == generation.get()) {
            MinecraftClient var8 = MinecraftClient.getInstance();
            Identifier var9 = Identifier.of("zenith", "bot_avatar/" + Integer.toHexString(var0.hashCode()));
            var6 = new NativeImageBackedTexture(() -> "zenith", var1);
            var8.getTextureManager().registerTexture(var9, var6);
            var7 = true;
            nameToId.put(var0, var9);
            retryAfter.remove(var0);
            return;
         }
      } catch (Throwable var13) {
         return;
      } finally {
         if (!var7) {
            if (var6 != null) {
               var6.close();
            } else {
               var1.close();
            }
         }

         loading.remove(var0, var4);
      }
   }

   private static void markFailed(String var0, long var1) {
      if (var1 == generation.get()) {
         BotAvatarCache.Retry var3 = retryAfter.get(var0);
         long var4 = var3 == null ? 30000L : Math.min(var3.backoff() * 2L, 300000L);
         retryAfter.put(var0, new BotAvatarCache.Retry(System.currentTimeMillis() + var4, var4));
      }
   }

   private static byte[] fetch(String var0, String var1) throws Exception {
      Path var2 = cacheDir().resolve(Integer.toHexString(var1.hashCode()) + ".png");

      try {
         if (Files.isRegularFile(var2)) {
            byte[] var3 = Files.readAllBytes(var2);
            if (var3.length > 0) {
               return var3;
            }
         }
      } catch (Exception var6) {
      }

      byte[] var7 = download(String.format("https://minotar.net/helm/%s/64.png", var0));
      if (var7 != null && var7.length > 0) {
         try {
            Files.createDirectories(var2.getParent());
            Files.write(var2, var7);
         } catch (Exception var5) {
         }
      }

      return var7;
   }

   private static Path cacheDir() {
      return MinecraftClient.getInstance().runDirectory.toPath().resolve("zenith").resolve("bot_avatars");
   }

   private static byte[] download(String var0) throws Exception {
      HttpURLConnection var1 = (HttpURLConnection)new URL(var0).openConnection();
      var1.setConnectTimeout(5000);
      var1.setReadTimeout(5000);
      var1.setRequestProperty("User-Agent", "Zenith");

      byte[] var3;
      try (InputStream var2 = var1.getInputStream()) {
         var3 = var2.readAllBytes();
      } finally {
         var1.disconnect();
      }

      return var3;
   }

   public static void clear() {
      generation.incrementAndGet();
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0 != null && var0.getTextureManager() != null) {
         for (Identifier var2 : nameToId.values()) {
            try {
               var0.getTextureManager().destroyTexture(var2);
            } catch (Throwable var4) {
            }
         }
      }

      nameToId.clear();
      loading.clear();
      retryAfter.clear();
   }

   private static final class Retry {
      private final long at;
      private final long backoff;

      public long at() {
         return this.at;
      }

      public long backoff() {
         return this.backoff;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Retry other)) return false;
         return java.util.Objects.equals(this.at, other.at) && java.util.Objects.equals(this.backoff, other.backoff);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(at, backoff);
      }

      @Override
      public String toString() {
         return "Retry[" + "at=" + this.at + ", " + "backoff=" + this.backoff + "]";
      }

      private Retry(long var1, long var3) {
         this.at = var1;
         this.backoff = var3;
      }
   }
}

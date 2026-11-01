package org.zenith.utility.config;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.NativeImage.Format;
import net.minecraft.util.Identifier;
import org.zenith.Zenith;
import org.zenith.utility.api.RegionUtil;
import org.zenith.utility.misc.ImageLoader;

public final class CloudAvatarCache {
   private static final String text = "zenith";
   private static final String text2 = "cloud_avatar/";
   private static final String text3 = "/user/avatar/";
   private static final int CONST_intValue = 2097152;
   private static final long CONST_longValue = 12582912L;
   private static final long CONST_longValue2 = 2000L;
   private static final LinkedHashMap<String, CloudAvatarCache.Entry> linkedHashMap = new LinkedHashMap<>(32, 0.75F, true);
   private static long longValue;
   private static final Set<String> items = ConcurrentHashMap.newKeySet();
   private static final Set<String> items2 = ConcurrentHashMap.newKeySet();
   private static final Map<String, Long> items3 = new ConcurrentHashMap<>();
   private static final long CONST_longValue3 = 30000L;
   private static final long CONST_longValue4 = 120L;
   private static long longValue2;
   private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(64), var0 -> {
      Thread var1 = new Thread(var0, "cloud-avatar-loader");
      var1.setDaemon(true);
      return var1;
   });
   private static final long CONST_longValue5 = 86400000L;
   private static final int CONST_intValue2 = 1024;
   private static final Pattern pattern = Pattern.compile("[A-Za-z0-9_-]{1,64}");
   private static final long CONST_longValue6 = 10000L;
   private static volatile long longValue3;

   private CloudAvatarCache() {
   }

   public static Identifier get(String var0) {
      if (var0 != null && !var0.isBlank()) {
         synchronized (linkedHashMap) {
            CloudAvatarCache.Entry var2 = linkedHashMap.get(var0);
            if (var2 != null) {
               var2.longValue2 = System.currentTimeMillis();
               return var2.getIdentifier();
            }
         }

         if (items2.contains(var0)) {
            return null;
         } else {
            Long var6 = items3.get(var0);
            if (var6 != null && System.currentTimeMillis() < var6) {
               return null;
            } else if (!items.add(var0)) {
               return null;
            } else {
               try {
                  threadPoolExecutor.execute(() -> load(var0));
               } catch (RejectedExecutionException var4) {
                  items.remove(var0);
               }

               return null;
            }
         }
      } else {
         return null;
      }
   }

   private static void load(String var0) {
      if (!pattern.matcher(var0).matches()) {
         method2127(var0);
         items.remove(var0);
      } else {
         Path var1 = cacheDir().resolve(var0 + ".img");
         byte[] var2 = method2128(var1);
         boolean var3 = var2 != null && method2129(var1);
         if (var2 != null && var3) {
            NativeImage var4 = method2132(var2);
            if (var4 != null) {
               MinecraftClient.getInstance().execute(() -> method2136(var0, var4));
               return;
            }

            method2131(var1);
            var2 = null;
         }

         try {
            byte[] var8 = method2138(var0);
            if (var8 == null || var8.length == 0) {
               method2127(var0);
               items.remove(var0);
               return;
            }

            NativeImage var9 = method2132(var8);
            if (var9 == null) {
               method2127(var0);
               items.remove(var0);
               return;
            }

            method2130(var1, var8);
            MinecraftClient.getInstance().execute(() -> method2136(var0, var9));
         } catch (CloudAvatarCache.Entry2 var6) {
            method2131(var1);
            method2127(var0);
            items.remove(var0);
         } catch (Throwable var7) {
            NativeImage var5 = var2 == null ? null : method2132(var2);
            if (var5 != null) {
               MinecraftClient.getInstance().execute(() -> method2136(var0, var5));
               return;
            }

            if (items3.size() > 4096) {
               items3.clear();
            }

            items3.put(var0, System.currentTimeMillis() + 30000L);
            items.remove(var0);
         }
      }
   }

   private static void method2127(String var0) {
      if (items2.size() > 4096) {
         items2.clear();
      }

      items2.add(var0);
   }

   private static byte[] method2128(Path var0) {
      try {
         if (Files.isRegularFile(var0)) {
            byte[] var1 = Files.readAllBytes(var0);
            return var1.length > 0 ? var1 : null;
         }
      } catch (Exception var2) {
      }

      return null;
   }

   private static boolean method2129(Path var0) {
      try {
         return System.currentTimeMillis() - Files.getLastModifiedTime(var0).toMillis() < 86400000L;
      } catch (Exception var2) {
         return false;
      }
   }

   private static void method2130(Path var0, byte[] var1) {
      try {
         Files.createDirectories(var0.getParent());
         Path var2 = var0.resolveSibling(var0.getFileName() + ".tmp");
         Files.write(var2, var1);

         try {
            Files.move(var2, var0, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (AtomicMoveNotSupportedException var4) {
            Files.move(var2, var0, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (Exception var5) {
      }
   }

   private static void method2131(Path var0) {
      try {
         Files.deleteIfExists(var0);
      } catch (Exception var2) {
      }
   }

   private static NativeImage method2132(byte[] var0) {
      try {
         int[] var1 = ImageLoader.method231(var0);
         return var1[0] > 0 && var1[1] > 0 && var1[0] <= 1024 && var1[1] <= 1024 ? method2133(var0) : null;
      } catch (Exception var2) {
         return null;
      }
   }

   private static NativeImage method2133(byte[] var0) throws Exception {
      try {
         NativeImage var2;
         try (ByteArrayInputStream var1 = new ByteArrayInputStream(var0)) {
            var2 = NativeImage.read(var1);
         }

         return var2;
      } catch (Exception var6) {
         return method2134(var0);
      }
   }

   private static NativeImage method2134(byte[] var0) throws Exception {
      ByteArrayInputStream var2 = new ByteArrayInputStream(var0);

      BufferedImage var1;
      try {
         var1 = ImageIO.read(var2);
      } catch (Throwable var8) {
         try {
            var2.close();
         } catch (Throwable var7) {
            var8.addSuppressed(var7);
         }

         throw var8;
      }

      var2.close();
      if (var1 == null) {
         throw new IllegalStateException("формат аватара не распознан");
      } else {
         int var9 = var1.getWidth();
         int var3 = var1.getHeight();
         NativeImage var4 = new NativeImage(Format.RGBA, var9, var3, false);

         for (int var5 = 0; var5 < var3; var5++) {
            for (int var6 = 0; var6 < var9; var6++) {
               var4.setColorArgb(var6, var5, var1.getRGB(var6, var5));
            }
         }

         return var4;
      }
   }

   private static void method2135() throws InterruptedException {
      long var0 = System.currentTimeMillis();
      long var2;
      synchronized (CloudAvatarCache.class) {
         var2 = Math.max(0L, longValue2 - var0);
         longValue2 = Math.max(var0, longValue2) + 120L;
      }

      if (var2 > 0L) {
         Thread.sleep(var2);
      }
   }

   private static void method2136(String var0, NativeImage var1) {
      NativeImageBackedTexture var2 = null;
      boolean var3 = false;

      try {
         Identifier var4 = Identifier.of("zenith", "cloud_avatar/" + var0.toLowerCase(Locale.ROOT));
         long var5 = (long)var1.getWidth() * var1.getHeight() * 4L;
         var2 = new NativeImageBackedTexture(() -> "zenith", var1);
         MinecraftClient.getInstance().getTextureManager().registerTexture(var4, var2);
         var3 = true;
         method2137(var0, var4, var5);
      } catch (Throwable var10) {
      } finally {
         if (!var3) {
            if (var2 != null) {
               var2.close();
            } else {
               var1.close();
            }
         }

         items.remove(var0);
      }
   }

   private static void method2137(String var0, Identifier var1, long var2) {
      ArrayList<Identifier> var4 = new ArrayList();
      long var5 = System.currentTimeMillis();
      synchronized (linkedHashMap) {
         CloudAvatarCache.Entry var8 = linkedHashMap.put(var0, new CloudAvatarCache.Entry(var1, var2, var5));
         longValue = longValue + (var2 - (var8 == null ? 0L : var8.bytes()));
         var var9 = linkedHashMap.entrySet().iterator();

         while (longValue > 12582912L && linkedHashMap.size() > 1 && var9.hasNext()) {
            Map.Entry var10 = (Map.Entry)var9.next();
            if (!((String)var10.getKey()).equals(var0) && var5 - ((CloudAvatarCache.Entry)var10.getValue()).longValue2 >= 2000L) {
               longValue = longValue - ((CloudAvatarCache.Entry)var10.getValue()).bytes();
               var4.add(((CloudAvatarCache.Entry)var10.getValue()).getIdentifier());
               var9.remove();
            }
         }
      }

      for (Object var14_o : (Iterable<?>)(Object)(var4)) {
         Identifier var14 = (Identifier)var14_o;
         MinecraftClient.getInstance().getTextureManager().destroyTexture(var14);
      }
   }

   private static byte[] method2138(String var0) throws Exception {
      if (System.currentTimeMillis() < longValue3) {
         throw new IllegalStateException("avatar site backoff");
      } else {
         method2135();
         byte[] var1 = null;
         Exception var2 = null;

         for (String var4 : RegionUtil.method2037()) {
            try {
               var1 = download(var4 + "/user/avatar/" + var0);
               var2 = null;
               break;
            } catch (CloudAvatarCache.Entry2 var6) {
               throw var6;
            } catch (Exception var7) {
               var2 = var7;
            }
         }

         if (var2 != null) {
            longValue3 = System.currentTimeMillis() + 10000L;
            throw var2;
         } else {
            return var1;
         }
      }
   }

   private static byte[] download(String var0) throws Exception {
      HttpURLConnection var1 = (HttpURLConnection)new URL(var0).openConnection();
      var1.setConnectTimeout(5000);
      var1.setReadTimeout(5000);
      var1.setRequestProperty("User-Agent", "ZenithDLC");
      var1.setRequestProperty("Accept", "image/*");

      Object var4;
      try {
         int var2 = var1.getResponseCode();
         if (var2 == 404) {
            throw new CloudAvatarCache.Entry2();
         }

         if (var2 != 200) {
            throw new IllegalStateException("avatar http " + var2);
         }

         String var3 = var1.getContentType();
         if (var3 == null || var3.toLowerCase(Locale.ROOT).startsWith("image/")) {
            try (InputStream var14 = var1.getInputStream()) {
               return var14.readNBytes(2097152);
            }
         }

         var4 = null;
      } finally {
         var1.disconnect();
      }

      return (byte[])var4;
   }

   private static Path cacheDir() {
      return Zenith.file.toPath().resolve("avatars");
   }

   private static final class Entry {
      private final Identifier identifier;
      private final long longValue;
      private long longValue2;

      private Entry(Identifier var1, long var2, long var4) {
         this.identifier = var1;
         this.longValue = var2;
         this.longValue2 = var4;
      }

      public Identifier getIdentifier() {
         return this.identifier;
      }

      public long bytes() {
         return this.longValue;
      }
   }

   private static final class Entry2 extends Exception {
   }
}

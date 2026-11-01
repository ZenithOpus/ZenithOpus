package org.zenith.utility.config;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
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
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.NativeImage.Format;
import net.minecraft.util.Identifier;
import org.zenith.Zenith;
import org.zenith.utility.api.RegionUtil;
import org.zenith.utility.misc.ImageLoader;
import org.zenith.utility.render.TextureFilterUtils;

public final class ChatImageCache {
   private static final String text = "zenith";
   private static final String text2 = "chat_image/";
   private static final int CONST_intValue = 1048576;
   private static final long CONST_longValue = 30000L;
   private static final long CONST_longValue2 = 60L;
   private static final long CONST_longValue3 = 100663296L;
   private static final long CONST_longValue4 = 201326592L;
   private static final long CONST_longValue5 = 10000L;
   private static final LinkedHashMap<String, ChatImageCache.Entry> linkedHashMap = new LinkedHashMap<>(32, 0.75F, true);
   private static final Set<String> items = ConcurrentHashMap.newKeySet();
   private static final Set<String> items2 = ConcurrentHashMap.newKeySet();
   private static final Map<String, Long> items3 = new ConcurrentHashMap<>();
   private static final Map<Identifier, Long> items4 = new ConcurrentHashMap<>();
   private static long longValue;
   private static long longValue2;
   private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
      1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(128), var0 -> {
         Thread var1 = new Thread(var0, "zenith-chat-image");
         var1.setDaemon(true);
         return var1;
      }
   );
   private static final int CONST_intValue2 = 2048;

   private ChatImageCache() {
   }

   public static Identifier get(String var0) {
      if (var0 != null && !var0.isBlank() && var0.startsWith("https://")) {
         synchronized (linkedHashMap) {
            ChatImageCache.Entry var2 = linkedHashMap.get(var0);
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
      if (items2.size() > 4096) {
         items2.clear();
      }

      try {
         byte[] var1 = method1494(var0);
         boolean var2 = var1 != null;
         if (var1 == null) {
            method1499();
            var1 = method1493(var0);
         }

         if (var1 == null || var1.length == 0) {
            items2.add(var0);
            items.remove(var0);
            return;
         }

         if (var1.length >= 1048576) {
            items2.add(var0);
            items.remove(var0);
            return;
         }

         if (!method1490(var1)) {
            if (var2) {
               method1496(var0);
            }

            items2.add(var0);
            items.remove(var0);
            return;
         }

         NativeImage var3;
         try {
            var3 = method1491(var1);
         } catch (Exception var5) {
            if (var2 && method1496(var0)) {
               items.remove(var0);
               return;
            }

            throw var5;
         }

         if (!var2) {
            method1495(var0, var1);
         }

         MinecraftClient.getInstance().execute(() -> method1486(var0, var3));
      } catch (Throwable var6) {
         if (items3.size() > 4096) {
            items3.clear();
         }

         items3.put(var0, System.currentTimeMillis() + 30000L);
         items.remove(var0);
      }
   }

   private static void method1486(String var0, NativeImage var1) {
      NativeImageBackedTexture var2 = null;
      boolean var3 = false;

      try {
         Identifier var4 = Identifier.of("zenith", "chat_image/" + method1498(var0));
         long var5 = (long)var1.getWidth() * var1.getHeight() * 4L;
         int var7 = var1.getWidth();
         int var8 = var1.getHeight();
         var2 = new NativeImageBackedTexture(() -> "zenith", var1);
         MinecraftClient.getInstance().getTextureManager().registerTexture(var4, var2);
         var3 = true;
         TextureFilterUtils.smooth(var4);
         items4.put(var4, (long)var7 << 32 | var8 & 4294967295L);
         method1487(var0, var4, var5);
      } catch (Throwable var12) {
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

   private static void method1487(String var0, Identifier var1, long var2) {
      ArrayList<Identifier> var4 = new ArrayList();
      long var5 = System.currentTimeMillis();
      synchronized (linkedHashMap) {
         ChatImageCache.Entry var8 = linkedHashMap.put(var0, new ChatImageCache.Entry(var1, var2));
         longValue = longValue + (var2 - (var8 == null ? 0L : var8.bytes()));
         var var9 = linkedHashMap.entrySet().iterator();

         while (longValue > 100663296L && linkedHashMap.size() > 1 && var9.hasNext()) {
            Map.Entry var10 = (Map.Entry)var9.next();
            if (!((String)var10.getKey()).equals(var0) && (var5 - ((ChatImageCache.Entry)var10.getValue()).longValue2 >= 10000L || longValue > 201326592L)) {
               longValue = longValue - ((ChatImageCache.Entry)var10.getValue()).bytes();
               var4.add(((ChatImageCache.Entry)var10.getValue()).getIdentifier());
               var9.remove();
            }
         }
      }

      for (Object var14_o : (Iterable<?>)(Object)(var4)) {
         Identifier var14 = (Identifier)var14_o;
         items4.remove(var14);
         MinecraftClient.getInstance().getTextureManager().destroyTexture(var14);
      }
   }

   public static int method1488(Identifier var0) {
      Long var1 = var0 == null ? null : items4.get(var0);
      return var1 == null ? 0 : (int)(var1 >>> 32);
   }

   public static int method1489(Identifier var0) {
      Long var1 = var0 == null ? null : items4.get(var0);
      return var1 == null ? 0 : (int)(var1 & 4294967295L);
   }

   private static boolean method1490(byte[] var0) {
      try {
         int[] var1 = ImageLoader.method231(var0);
         return var1[0] > 0 && var1[1] > 0 && var1[0] <= 2048 && var1[1] <= 2048;
      } catch (Exception var2) {
         return false;
      }
   }

   private static NativeImage method1491(byte[] var0) throws Exception {
      try {
         NativeImage var2;
         try (ByteArrayInputStream var1 = new ByteArrayInputStream(var0)) {
            var2 = NativeImage.read(var1);
         }

         return var2;
      } catch (Exception var6) {
         return method1492(var0);
      }
   }

   private static NativeImage method1492(byte[] var0) throws Exception {
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
         throw new IllegalStateException("формат картинки не распознан");
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

   private static byte[] method1493(String var0) throws Exception {
      URL var1 = new URL(var0);
      if ("https".equalsIgnoreCase(var1.getProtocol()) && RegionUtil.method2044(var1.getHost())) {
         HttpURLConnection var2 = (HttpURLConnection)var1.openConnection();
         var2.setInstanceFollowRedirects(false);
         var2.setConnectTimeout(6000);
         var2.setReadTimeout(10000);
         var2.setRequestProperty("User-Agent", "Zenith");

         byte[] var5;
         try {
            int var3 = var2.getResponseCode();
            if (var3 == 404 || var3 == 410) {
               return null;
            }

            if (var3 != 200) {
               throw new IllegalStateException("сайт ответил " + var3);
            }

            try (InputStream var4 = var2.getInputStream()) {
               var5 = var4.readNBytes(1048576);
            }
         } finally {
            var2.disconnect();
         }

         return var5;
      } else {
         return null;
      }
   }

   private static byte[] method1494(String var0) {
      try {
         Path var1 = method1497().resolve(method1498(var0) + ".img");
         if (Files.isRegularFile(var1)) {
            byte[] var2 = Files.readAllBytes(var1);
            return var2.length == 0 ? null : var2;
         }
      } catch (Exception var3) {
      }

      return null;
   }

   private static void method1495(String var0, byte[] var1) {
      try {
         Path var2 = method1497();
         Files.createDirectories(var2);
         String var3 = method1498(var0) + ".img";
         Path var4 = var2.resolve(var3);
         Path var5 = var2.resolve(var3 + ".tmp");
         Files.write(var5, var1);

         try {
            Files.move(var5, var4, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (AtomicMoveNotSupportedException var7) {
            Files.move(var5, var4, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (Exception var8) {
      }
   }

   private static boolean method1496(String var0) {
      try {
         Files.deleteIfExists(method1497().resolve(method1498(var0) + ".img"));
         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   private static Path method1497() {
      return Zenith.file.toPath().resolve("chat-images");
   }

   private static String method1498(String var0) {
      try {
         MessageDigest var1 = MessageDigest.getInstance("SHA-256");
         byte[] var2 = var1.digest(var0.getBytes(StandardCharsets.UTF_8));
         byte[] var3 = new byte[8];
         System.arraycopy(var2, 0, var3, 0, 8);
         return HexFormat.of().formatHex(var3).toLowerCase(Locale.ROOT);
      } catch (Exception var4) {
         return Integer.toHexString(var0.hashCode());
      }
   }

   private static void method1499() throws InterruptedException {
      long var0 = System.currentTimeMillis();
      long var2;
      synchronized (ChatImageCache.class) {
         var2 = Math.max(0L, longValue2 - var0);
         longValue2 = Math.max(var0, longValue2) + 60L;
      }

      if (var2 > 0L) {
         Thread.sleep(var2);
      }
   }

   private static final class Entry {
      private final Identifier identifier;
      private final long longValue;
      private volatile long longValue2 = System.currentTimeMillis();

      private Entry(Identifier var1, long var2) {
         this.identifier = var1;
         this.longValue = var2;
      }

      private Identifier getIdentifier() {
         return this.identifier;
      }

      private long bytes() {
         return this.longValue;
      }
   }
}

package org.zenith.utility.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.MinecraftClient;
import org.zenith.Zenith;
import org.zenith.base.figura.FiguraMod;
import org.zenith.base.figura.avatar.Avatar;
import org.zenith.base.figura.avatar.AvatarLayer;
import org.zenith.base.figura.avatar.AvatarManager;
import org.zenith.base.figura.avatar.UserData;
import org.zenith.base.figura.avatar.local.LocalAvatarLoader;

public final class AvatarUtil {
   private static final AtomicLong atomicLong = new AtomicLong();
   private static final ConcurrentHashMap<AvatarUtil.Data2, AvatarUtil.Data> concurrentHashMap = new ConcurrentHashMap<>();
   private static final Map<Path, Long> items = new ConcurrentHashMap<>();
   private static final long CONST_longValue = 60000L;
   private static final ExecutorService executorService = Executors.newSingleThreadExecutor(var0 -> {
      Thread var1 = new Thread(var0, "Zenith Figura Loader");
      var1.setDaemon(true);
      var1.setPriority(Math.max(1, 4));
      return var1;
   });
   private static final String text = "cosmetics/";

   private AvatarUtil() {
   }

   public static void method7237(Path var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1 != null && var1.player != null) {
         method7240(var1.player.getUuid(), var0);
      }
   }

   public static UserData method7238(Path var0, UUID var1) {
      Path var2 = method7260(var0);
      if (var2 != null && var1 != null) {
         UserData var3 = new UserData(var1);
         LocalAvatarLoader.loadAvatar(var2, var3);
         return var3.getAvatars().isEmpty() ? null : var3;
      } else {
         return null;
      }
   }

   public static void method7239(UUID var0, String var1) {
      method7240(var0, method7258(var1));
   }

   public static void method7240(UUID var0, Path var1) {
      if (var0 != null) {
         Path var2 = method7260(var1);
         if (var2 != null) {
            method7266(var0);
            method7262(var0, var2, AvatarLayer.BODY, AvatarUtil.Type.REPLACE_ALL);
         }
      }
   }

   public static void method7241(UUID var0, String var1) {
      method7242(var0, method7258(var1));
   }

   public static void method7242(UUID var0, Path var1) {
      if (var0 != null) {
         Path var2 = method7260(var1);
         if (var2 != null) {
            method7262(var0, var2, AvatarLayer.BODY, AvatarUtil.Type.APPEND);
         }
      }
   }

   public static void method7243(UUID var0, String var1) {
      method7244(var0, method7258(var1));
   }

   public static void method7244(UUID var0, Path var1) {
      method7247(var0, var1, AvatarLayer.BODY);
   }

   public static void method7245(UUID var0, String var1) {
      method7246(var0, method7258(var1));
   }

   public static void method7246(UUID var0, Path var1) {
      method7247(var0, var1, AvatarLayer.WEAPON);
   }

   private static void method7247(UUID var0, Path var1, AvatarLayer var2) {
      if (var0 != null) {
         Path var3 = method7260(var1);
         if (var3 != null) {
            method7262(var0, var3, var2, AvatarUtil.Type.REPLACE_LAYER);
         }
      }
   }

   public static boolean method7248(UUID var0) {
      return AvatarManager.contains(var0);
   }

   public static boolean method7249(UUID var0) {
      return AvatarManager.contains(var0, AvatarLayer.BODY);
   }

   public static boolean method7250(UUID var0) {
      return AvatarManager.contains(var0, AvatarLayer.WEAPON);
   }

   public static void method7251(UUID var0) {
      if (var0 != null) {
         method7266(var0);
         method7261(() -> AvatarManager.clear(var0));
      }
   }

   public static void method7252(UUID var0) {
      method7254(var0, AvatarLayer.BODY);
   }

   public static void method7253(UUID var0) {
      method7254(var0, AvatarLayer.WEAPON);
   }

   private static void method7254(UUID var0, AvatarLayer var1) {
      if (var0 != null) {
         method7265(var0, var1);
         method7261(() -> AvatarManager.clear(var0, var1));
      }
   }

   public static void method7255(UUID var0) {
      method7265(var0, AvatarLayer.BODY);
   }

   public static void method7256(UUID var0) {
      method7265(var0, AvatarLayer.WEAPON);
   }

   public static String method7257(Path var0) {
      if (var0 == null) {
         return "";
      } else {
         String var1 = BuiltinCosmeticsPath.method4450(var0);
         if (!var1.isEmpty()) {
            return var1;
         } else {
            Path var2 = Zenith.file.toPath().toAbsolutePath().normalize();
            Path var3 = var0.toAbsolutePath().normalize();
            return var3.startsWith(var2) ? var2.relativize(var3).toString().replace('\\', '/') : "";
         }
      }
   }

   public static Path method7258(String var0) {
      if (var0 != null && !var0.isBlank()) {
         Path var1 = BuiltinCosmeticsPath.method4451(var0);
         if (var1 != null) {
            return var1;
         } else {
            Path var2 = Zenith.file.toPath().toAbsolutePath().normalize();
            Path var3 = var2.resolve(var0).normalize();
            if (!var3.startsWith(var2)) {
               return null;
            } else {
               return Files.isRegularFile(var3.resolve("avatar.json")) ? var3 : method7259(var0, var3);
            }
         }
      } else {
         return null;
      }
   }

   private static Path method7259(String var0, Path var1) {
      String var2 = var0.startsWith("cosmetics/") ? var0.substring("cosmetics/".length()) : var0;
      Path var3 = BuiltinCosmeticsPath.method4451("builtin/" + var2);
      return var3 != null && Files.isRegularFile(var3.resolve("avatar.json")) ? var3 : var1;
   }

   public static Path method7260(Path var0) {
      if (var0 == null) {
         return null;
      } else {
         Path var1 = var0.toAbsolutePath().normalize();
         return Files.isDirectory(var1) && Files.isRegularFile(var1.resolve("avatar.json")) ? var1 : null;
      }
   }

   private static void method7261(Runnable var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1 != null && !var1.isOnThread()) {
         var1.execute(var0);
      } else {
         var0.run();
      }
   }

   private static void method7262(UUID var0, Path var1, AvatarLayer var2, AvatarUtil.Type var3) {
      Long var4 = items.get(var1);
      if (var4 == null || System.currentTimeMillis() >= var4) {
         AvatarUtil.Data2 var5 = new AvatarUtil.Data2(var0, var2);
         AvatarUtil.Data var6 = concurrentHashMap.get(var5);
         if (var6 == null || !var6.getPath().equals(var1) || var6.getType() != var3) {
            AvatarUtil.Data var7 = new AvatarUtil.Data(var1, var3, atomicLong.incrementAndGet());
            concurrentHashMap.put(var5, var7);
            executorService.execute(() -> method7263(var5, var7));
         }
      }
   }

   private static void method7263(AvatarUtil.Data2 var0, AvatarUtil.Data var1) {
      if (concurrentHashMap.get(var0) == var1) {
         UserData var2 = new UserData(var0.getUUID());

         try {
            LocalAvatarLoader.loadAvatar(var1.getPath(), var2, var0.getAvatarLayer());
         } catch (Throwable var5) {
            FiguraMod.LOGGER.error("Failed to prepare local avatar {} for {}", new Object[]{var1.getPath(), var0.getUUID(), var5});
         }

         boolean var3 = var2.getAvatars(var0.getAvatarLayer()).stream().anyMatch(var0x -> var0x.loaded && var0x.renderer != null);
         MinecraftClient var4 = MinecraftClient.getInstance();
         if (var4 == null) {
            concurrentHashMap.remove(var0, var1);
         } else {
            var4.execute(() -> method7264(var0, var1, var2, var3));
         }
      }
   }

   private static void method7264(AvatarUtil.Data2 var0, AvatarUtil.Data var1, UserData var2, boolean var3) {
      if (concurrentHashMap.get(var0) == var1 && var3) {
         if (var1.getType() == AvatarUtil.Type.REPLACE_ALL) {
            AvatarManager.clear(var0.getUUID());
         }

         UserData var4 = AvatarManager.getOrCreate(var0.getUUID());
         if (var1.getType() == AvatarUtil.Type.REPLACE_LAYER) {
            var4.clear(var0.getAvatarLayer());
         }

         items.remove(var1.getPath());
         var var5 = var2.getAvatars();

         Avatar var6;
         while ((var6 = (Avatar)var5.poll()) != null) {
            var4.getAvatars().offer(var6);
         }

         concurrentHashMap.remove(var0, var1);
      } else {
         concurrentHashMap.remove(var0, var1);
         var2.clear();
         if (!var3) {
            items.put(var1.getPath(), System.currentTimeMillis() + 60000L);
            FiguraMod.LOGGER.warn("Ignoring unusable local avatar {} for {}", var1.getPath(), var0.getUUID());
         }
      }
   }

   private static void method7265(UUID var0, AvatarLayer var1) {
      if (var0 != null) {
         concurrentHashMap.remove(new AvatarUtil.Data2(var0, var1));
      }
   }

   private static void method7266(UUID var0) {
      if (var0 != null) {
         method7265(var0, AvatarLayer.BODY);
         method7265(var0, AvatarLayer.WEAPON);
      }
   }

   private static final class Data {
      private final Path path;
      private final AvatarUtil.Type type;
      private final long longValue;

      public Path path() {
         return this.path;
      }

      public AvatarUtil.Type type() {
         return this.type;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.path, other.path) && java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(path, type, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "path=" + this.path + ", " + "type=" + this.type + ", " + "longValue=" + this.longValue + "]";
      }

      private Data(Path var1, AvatarUtil.Type var2, long var3) {
         this.path = var1;
         this.type = var2;
         this.longValue = var3;
      }

      public Path getPath() {
         return this.path;
      }

      public AvatarUtil.Type getType() {
         return this.type;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Data2 {
      private final UUID uUID;
      private final AvatarLayer avatarLayer;

      public UUID uUID() {
         return this.uUID;
      }

      public AvatarLayer avatarLayer() {
         return this.avatarLayer;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.uUID, other.uUID) && java.util.Objects.equals(this.avatarLayer, other.avatarLayer);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(uUID, avatarLayer);
      }

      @Override
      public String toString() {
         return "Data2[" + "uUID=" + this.uUID + ", " + "avatarLayer=" + this.avatarLayer + "]";
      }

      private Data2(UUID var1, AvatarLayer var2) {
         this.uUID = var1;
         this.avatarLayer = var2;
      }

      public UUID getUUID() {
         return this.uUID;
      }

      public AvatarLayer getAvatarLayer() {
         return this.avatarLayer;
      }
   }

   private static enum Type {
      REPLACE_ALL,
      REPLACE_LAYER,
      APPEND;
   }
}

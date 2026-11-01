package org.zenith.client.screens.nlgui.cloud;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.zenith.Zenith;
import org.zenith.utility.api.StoreApi;

public final class CloudPreviewTextureCache {
   private static final String NAMESPACE = "zenith";
   private static final String PREFIX = "cloud_config_preview/";
   private static final int MAX_TEXTURES = 32;
   private static final int MAX_IN_FLIGHT = 6;
   private static final long RETRY_DELAY_MS = 15000L;
   private static final int MAX_PREVIEW_BYTES = 4194304;
   private static final int MAX_PREVIEW_SIDE = 2048;
   private static final long MAX_PREVIEW_PIXELS = 2359296L;
   private static final Map<String, Identifier> textures = new LinkedHashMap<>(16, 0.75F, true);
   private static final Set<String> inFlight = ConcurrentHashMap.newKeySet();
   private static final Map<String, Long> failures = new ConcurrentHashMap<>();
   private static Identifier draftId;
   private static int draftFingerprint;

   private CloudPreviewTextureCache() {
   }

   public static Identifier get(UUID var0, String var1) {
      String var2 = var1 == null ? null : var1.toLowerCase(Locale.ROOT);
      if (var0 != null && validKey(var2)) {
         synchronized (textures) {
            Identifier var4 = textures.get(var2);
            if (var4 != null) {
               return var4;
            }
         }

         Long var7 = failures.get(var2);
         if (var7 != null && System.currentTimeMillis() - var7 < 15000L) {
            return null;
         } else {
            StoreApi var8 = Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();
            if (var8 != null && !inFlight.contains(var2) && inFlight.size() < 6 && inFlight.add(var2)) {
               var8.method1971(var0).whenComplete((var1x, var2x) -> {
                  try {
                     if (var2x == null && var1x != null && var1x.length != 0) {
                        install(var2, var1x);
                        return;
                     }

                     failures.put(var2, System.currentTimeMillis());
                  } finally {
                     inFlight.remove(var2);
                  }
               });
               return null;
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private static void install(String var0, byte[] var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2 != null) {
         if (!sane(var1)) {
            failures.put(var0, System.currentTimeMillis());
         } else {
            NativeImage var3;
            try (ByteArrayInputStream var4 = new ByteArrayInputStream(var1)) {
               var3 = NativeImage.read(var4);
            } catch (Exception var9) {
               failures.put(var0, System.currentTimeMillis());
               return;
            }

            var2.execute(() -> {
               Identifier var3x = Identifier.of("zenith", "cloud_config_preview/" + var0);

               try {
                  var2.getTextureManager().registerTexture(var3x, new NativeImageBackedTexture(() -> "zenith", var3));
               } catch (Exception var7) {
                  var3.close();
                  failures.put(var0, System.currentTimeMillis());
                  return;
               }

               failures.remove(var0);
               synchronized (textures) {
                  textures.put(var0, var3x);
                  evictOverflow(var2);
               }
            });
         }
      }
   }

   private static boolean validKey(String var0) {
      if (var0 != null && !var0.isEmpty() && var0.length() <= 128) {
         for (int var1 = 0; var1 < var0.length(); var1++) {
            char var2 = var0.charAt(var1);
            if ((var2 < '0' || var2 > '9') && (var2 < 'a' || var2 > 'z') && var2 != '_' && var2 != '.' && var2 != '-') {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static boolean sane(byte[] var0) {
      if (var0.length >= 24
         && var0.length <= 4194304
         && (var0[0] & 255) == 137
         && var0[1] == 80
         && var0[2] == 78
         && var0[3] == 71
         && var0[12] == 73
         && var0[13] == 72
         && var0[14] == 68
         && var0[15] == 82) {
         int var1 = (var0[16] & 255) << 24 | (var0[17] & 255) << 16 | (var0[18] & 255) << 8 | var0[19] & 255;
         int var2 = (var0[20] & 255) << 24 | (var0[21] & 255) << 16 | (var0[22] & 255) << 8 | var0[23] & 255;
         return var1 > 0 && var2 > 0 && var1 <= 2048 && var2 <= 2048 && (long)var1 * var2 <= 2359296L;
      } else {
         return false;
      }
   }

   private static void evictOverflow(MinecraftClient var0) {
      var var1 = textures.entrySet().iterator();

      while (textures.size() > 32 && var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         var1.remove();

         try {
            var0.getTextureManager().destroyTexture((Identifier)var2.getValue());
         } catch (Exception var4) {
         }
      }
   }

   public static synchronized Identifier draft(byte[] var0) {
      if (var0 != null && var0.length != 0) {
         int var1 = Arrays.hashCode(var0);
         if (draftId != null && draftFingerprint == var1) {
            return draftId;
         } else {
            MinecraftClient var2 = MinecraftClient.getInstance();
            if (var2 == null) {
               return null;
            } else {
               clearDraft();

               try {
                  Identifier var6;
                  try (ByteArrayInputStream var3 = new ByteArrayInputStream(var0)) {
                     NativeImage var4 = NativeImage.read(var3);
                     Identifier var5 = Identifier.of("zenith", "cloud_config_preview/draft_" + Integer.toHexString(var1));
                     var2.getTextureManager().registerTexture(var5, new NativeImageBackedTexture(() -> "zenith", var4));
                     draftId = var5;
                     draftFingerprint = var1;
                     var6 = var5;
                  }

                  return var6;
               } catch (Exception var9) {
                  return null;
               }
            }
         }
      } else {
         return null;
      }
   }

   public static synchronized void clearDraft() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (draftId != null && var0 != null && var0.getTextureManager() != null) {
         try {
            var0.getTextureManager().destroyTexture(draftId);
         } catch (Exception var2) {
         }
      }

      draftId = null;
      draftFingerprint = 0;
   }

   public static void clear() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      synchronized (textures) {
         if (var0 != null && var0.getTextureManager() != null) {
            for (Identifier var3 : textures.values()) {
               try {
                  var0.getTextureManager().destroyTexture(var3);
               } catch (Exception var6) {
               }
            }
         }

         textures.clear();
      }

      failures.clear();
   }
}

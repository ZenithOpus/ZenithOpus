package org.zenith.client.screens.nlgui.elements;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kotlin.collections.EmptyMap;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.zenith.MinecraftAccess;
import org.zenith.utility.misc.TextureId;
import org.zenith.utility.render.ShapeRenderer;

public final class FriendSkinResolver implements MinecraftAccess {
   private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(var0 -> {
      Thread var1 = new Thread(var0, "antidumper228-loader");
      var1.setDaemon(true);
      return var1;
   });
   private static final Map<String, Identifier> CACHE = new ConcurrentHashMap<>();
   private static final Map<String, Boolean> LOADING = new ConcurrentHashMap<>();
   private static final Map<String, Long> FAILED_UNTIL = new ConcurrentHashMap<>();
   private static final Set<Identifier> EXTERNAL_TEXTURES = ConcurrentHashMap.newKeySet();

   private FriendSkinResolver() {
   }

   public static Identifier resolveSkin(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String var1 = var0.trim().toLowerCase();
         Identifier var2 = getOnlineSkin(var0);
         if (var2 != null) {
            CACHE.put(var1, var2);
            return var2;
         } else {
            Identifier var3 = CACHE.get(var1);
            if (var3 != null) {
               return var3;
            } else {
               long var4 = System.currentTimeMillis();
               Long var6 = FAILED_UNTIL.get(var1);
               if (var6 != null && var6 > var4) {
                  return DefaultSkinHelper.getSteve().body().texturePath();
               } else {
                  if (LOADING.putIfAbsent(var1, Boolean.TRUE) == null) {
                     Identifier var7 = getExternalTextureId(var1);
                     EXECUTOR.execute(() -> {
                        boolean var9 = false /* VF: Semaphore variable */;

                        label82: {
                           label83: {
                              try {
                                 var9 = true;
                                 byte[] var3x = downloadHeadPng(var0.trim());
                                 if (var3x == null || var3x.length <= 0) {
                                    FAILED_UNTIL.put(var1, System.currentTimeMillis() + 30000L);
                                    var9 = false;
                                 } else {
                                    boolean var10000;
                                    label61: {
                                       if (LOADING != null) {
                                          if (LOADING.isEmpty()) {
                                             var10000 = false;
                                             break label61;
                                          }
                                       }

                                       var10000 = LOADING.containsKey(var1);
                                    }

                                    if (!var10000) {
                                       var9 = false;
                                       break label82;
                                    }

                                    ShapeRenderer.method7178(new TextureId(var7), var3x);
                                    CACHE.put(var1, var7);
                                    EXTERNAL_TEXTURES.add(var7);
                                    FAILED_UNTIL.remove(var1);
                                    var9 = false;
                                 }
                                 break label83;
                              } catch (Exception var10) {
                                 FAILED_UNTIL.put(var1, System.currentTimeMillis() + 30000L);
                                 var9 = false;
                              } finally {
                                 if (var9) {
                                    LOADING.remove(var1);
                                 }
                              }

                              LOADING.remove(var1);
                              return;
                           }

                           LOADING.remove(var1);
                           return;
                        }

                        LOADING.remove(var1);
                     });
                  }

                  return DefaultSkinHelper.getSteve().body().texturePath();
               }
            }
         }
      } else {
         return DefaultSkinHelper.getSteve().body().texturePath();
      }
   }

   public static void clearExternalCache() {
      Set<Identifier> var0 = Set.copyOf(EXTERNAL_TEXTURES);

      for (Object var2_o : (Iterable<?>)(Object)(var0)) {
         Identifier var2 = (Identifier)var2_o;
         try {
            mc.getTextureManager().destroyTexture(var2);
         } catch (Exception var4) {
         }
      }

      EXTERNAL_TEXTURES.clear();
      CACHE.entrySet().removeIf(var1 -> var0.contains(var1.getValue()));
      FAILED_UNTIL.clear();
      LOADING.clear();
   }

   private static Identifier getOnlineSkin(String var0) {
      if (mc.getNetworkHandler() == null) {
         return null;
      } else {
         for (PlayerListEntry var2 : mc.getNetworkHandler().getPlayerList()) {
            if (var2 != null && var2.getProfile() != null && var2.getProfile().name() != null && var2.getProfile().name().equalsIgnoreCase(var0)) {
               if (var2.getSkinTextures() == null) {
                  return null;
               }

               return var2.getSkinTextures().body().texturePath();
            }
         }

         return null;
      }
   }

   private static Identifier getExternalTextureId(String var0) {
      UUID var1 = UUID.nameUUIDFromBytes(("friend_skin_" + var0).getBytes(StandardCharsets.UTF_8));
      return new TextureId("friend_heads/" + var1.toString().replace("-", "")).getIdentifier();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static byte[] downloadHeadPng(String var0) throws Exception {
      String var1 = URLEncoder.encode(var0, StandardCharsets.UTF_8).replace("+", "%20");
      String var2 = "https://minotar.net/helm/" + var1 + "/100.png";
      HttpURLConnection var3 = (HttpURLConnection)URI.create(var2).toURL().openConnection();
      var3.setConnectTimeout(2500);
      var3.setReadTimeout(2500);
      var3.setRequestProperty("User-Agent", "Sasske228");
      int var4;
      if ((var4 = var3.getResponseCode()) >= 200 && var4 < 300) {
         boolean var16 = false /* VF: Semaphore variable */;

         byte[] var9;
         try {
            var16 = true;

            try (
               InputStream var5 = var3.getInputStream();
               ByteArrayOutputStream var6 = new ByteArrayOutputStream();
            ) {
               byte[] var7 = new byte[4096];

               int var8;
               while ((var8 = var5.read(var7)) != -1) {
                  var6.write(var7, 0, var8);
               }

               var9 = var6.toByteArray();
            }
         } finally {
            if (var16) {
               var3.disconnect();
            }
         }

         var3.disconnect();
         return var9;
      } else {
         return null;
      }
   }
}

package org.zenith.client.screens.override.main.component;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo.TextureAssetInfo;
import org.zenith.Zenith;
import org.zenith.utility.api.HttpClients;

public final class SkinPreview {
   private static final Pattern VALID_NAME = Pattern.compile("[A-Za-z0-9_]{1,16}");
   private static final Map<String, SkinTextures> CACHE = new ConcurrentHashMap<>();
   private static final Map<String, Boolean> PENDING = new ConcurrentHashMap<>();
   private static final HttpClient HTTP = HttpClients.method2670(Duration.ofSeconds(10L));
   private static final String[][] BUNDLED = new String[][]{
      {"dinnerbone", "WIDE"},
      {"grumm", "WIDE"},
      {"technoblade", "WIDE"},
      {"captainsparklez", "WIDE"},
      {"ph1lza", "WIDE"},
      {"vurb", "SLIM"},
      {"purpled", "SLIM"},
      {"punz", "SLIM"}
   };
   private static final SkinTextures[] FALLBACKS = new SkinTextures[BUNDLED.length];

   private SkinPreview() {
   }

   public static SkinTextures textures(String var0, UUID var1) {
      String var2 = var0 == null ? "" : var0.toLowerCase(Locale.ROOT);
      SkinTextures var3 = CACHE.get(var2);
      if (var3 != null) {
         return var3;
      } else {
         if (var0 != null && VALID_NAME.matcher(var0).matches() && PENDING.putIfAbsent(var2, Boolean.TRUE) == null) {
            resolve(var2, var0, var1);
         }

         return fallback(var2, var1);
      }
   }

   private static SkinTextures fallback(String var0, UUID var1) {
      if (var0.isEmpty()) {
         return DefaultSkinHelper.getSkinTextures(var1);
      } else {
         int var2 = Math.floorMod(var0.hashCode(), FALLBACKS.length);
         SkinTextures var3 = FALLBACKS[var2];
         if (var3 == null) {
            var3 = new SkinTextures(
               new TextureAssetInfo(Zenith.method1501("skins/" + BUNDLED[var2][0]), Zenith.method1501("skins/" + BUNDLED[var2][0] + ".png")),
               null,
               null,
               PlayerSkinType.valueOf(BUNDLED[var2][1]),
               false
            );
            FALLBACKS[var2] = var3;
         }

         return var3;
      }
   }

   private static void resolve(String var0, String var1, UUID var2) {
      MinecraftClient var3 = MinecraftClient.getInstance();
      HttpRequest var4 = HttpRequest.newBuilder()
         .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + var1))
         .timeout(Duration.ofSeconds(10L))
         .GET()
         .build();
      HTTP.sendAsync(var4, BodyHandlers.ofString())
         .thenApply(var1x -> {
            if (var1x.statusCode() != 200) {
               return var2;
            } else {
               JsonObject var2x = JsonParser.parseString(var1x.body()).getAsJsonObject();
               return undash(var2x.get("id").getAsString());
            }
         })
         .exceptionally(var1x -> var2)
         .thenAccept(var3x -> var3.execute(() -> var3.getSkinProvider().fetchSkinTextures(new GameProfile(var3x, var1)).whenComplete((var1xxx, var2xxx) -> {
            if (var2xxx == null && var1xxx != null && var1xxx.isPresent()) {
               CACHE.put(var0, var1xxx.get());
            }

            PENDING.remove(var0);
         })));
   }

   private static UUID undash(String var0) {
      return UUID.fromString(var0.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
   }
}

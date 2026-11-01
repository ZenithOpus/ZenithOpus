package org.zenith.utility.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.codec.EmotecraftGsonCodec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.utility.misc.EmoteInfo;

public final class EmoteRegistry {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Emotes");
   private static final List<String> items = List.of(
      "waving", "clap", "crying", "point", "here", "palm", "backflip", "roblox_potion_dance", "kazotsky_kick", "twerk", "club_penguin_dance"
   );
   private static final List<String> items2 = List.of("/assets/zenith/emotes/spemotes/index.txt");
   private final CompletableFuture<Map<String, EmoteInfo>> completableFuture = CompletableFuture.<Map<String, EmoteInfo>>supplyAsync(
         EmoteRegistry::method1589, var0 -> {
            Thread var1 = new Thread(var0, "Zenith Emote Loader");
            var1.setDaemon(true);
            var1.start();
         }
      )
      .exceptionally(var0 -> {
         logger.error("Could not load built-in Zenith emotes", var0);
         return Map.of();
      });

   private static Map<String, EmoteInfo> method1589() {
      LinkedHashMap var0 = new LinkedHashMap();

      for (String var2 : items) {
         method1594(var2).ifPresent(var1 -> var0.put(var1.getText(), var1));
      }

      for (String var5 : items2) {
         method1593(var5).forEach(var1 -> method1594(var1).ifPresent(var1x -> var0.put(var1x.getText(), var1x)));
      }

      var var4 = Collections.unmodifiableMap(var0);
      logger.info("Loaded {} built-in Zenith emotes", var4.size());
      return var4;
   }

   private Map<String, EmoteInfo> method1590() {
      return this.completableFuture.join();
   }

   public Optional<EmoteInfo> method1591(String var1) {
      return var1 != null && !var1.isBlank() ? Optional.ofNullable(this.method1590().get(method1596(var1))) : Optional.empty();
   }

   public Collection<EmoteInfo> all() {
      return this.method1590().values();
   }

   public List<String> method1592() {
      return List.copyOf(this.method1590().keySet());
   }

   private static List<String> method1593(String var0) {
      try {
         List var3;
         try (InputStream var1 = EmoteRegistry.class.getResourceAsStream(var0)) {
            if (var1 == null) {
               logger.warn("Missing built-in emote index {}", var0);
               return List.of();
            }

            try (BufferedReader var2 = new BufferedReader(new InputStreamReader(var1, StandardCharsets.UTF_8))) {
               var3 = var2.lines().map(String::trim).filter(var0x -> !var0x.isEmpty() && !var0x.startsWith("#")).toList();
            }
         }

         return var3;
      } catch (IOException var9) {
         logger.warn("Could not load built-in emote index {}", var0, var9);
         return List.of();
      }
   }

   private static Optional<EmoteInfo> method1594(String var0) {
      String var1 = "/assets/zenith/emotes/" + var0 + ".json";

      try {
         Optional var5;
         try (InputStream var2 = EmoteRegistry.class.getResourceAsStream(var1)) {
            if (var2 == null) {
               logger.warn("Missing built-in emote resource {}", var1);
               return Optional.empty();
            }

            var var3 = EmotecraftGsonCodec.INSTANCE.decode(var2);
            if (var3.isEmpty()) {
               logger.warn("Built-in emote {} contains no animations", var0);
               return Optional.empty();
            }

            KeyframeAnimation var4 = (KeyframeAnimation)var3.iterator().next();
            var5 = Optional.of(
               new EmoteInfo(
                  method1596(var0),
                  var4.getUuid(),
                  method1595(var4, "name", method1597(var0)),
                  method1595(var4, "author", "Unknown"),
                  Identifier.of("Zenith".toLowerCase(Locale.ROOT), "emotes/" + var0 + ".png"),
                  var4
               )
            );
         }

         return var5;
      } catch (RuntimeException | IOException var8) {
         logger.warn("Could not load built-in emote {}", var0, var8);
         return Optional.empty();
      }
   }

   private static String method1595(KeyframeAnimation var0, String var1, String var2) {
      if (var0.extraData.get(var1) instanceof String var4 && !var4.isBlank()) {
         try {
            JsonElement var5 = JsonParser.parseString(var4);
            if (var5.isJsonPrimitive() && var5.getAsJsonPrimitive().isString()) {
               return var5.getAsString();
            }

            if (var5.isJsonObject()) {
               JsonObject var6 = var5.getAsJsonObject();
               if (var6.has("fallback") && var6.get("fallback").isJsonPrimitive()) {
                  return var6.get("fallback").getAsString();
               }
            }
         } catch (RuntimeException var7) {
         }

         return var4;
      } else {
         return var2;
      }
   }

   private static String method1596(String var0) {
      return var0.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
   }

   private static String method1597(String var0) {
      ArrayList var1 = new ArrayList();

      for (String var5 : var0.split("_")) {
         var1.add(var5.isEmpty() ? var5 : Character.toUpperCase(var5.charAt(0)) + var5.substring(1));
      }

      return String.join(" ", var1);
   }
}

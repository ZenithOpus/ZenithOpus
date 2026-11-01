package org.zenith.utility.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import org.zenith.utility.config.Data4;

public final class AltsFileImporter {
   private AltsFileImporter() {
   }

   public static Path method4853(MinecraftClient var0) {
      Path var1 = var0.runDirectory.toPath().resolve("alts");

      try {
         if (!Files.exists(var1)) {
            Files.createDirectories(var1);
         }
      } catch (Exception var3) {
      }

      return var1;
   }

   public static List<Data4> method4854(MinecraftClient var0) {
      Path var1 = method4853(var0);
      ArrayList<Data4> var2 = new ArrayList<>();
      if (!Files.isDirectory(var1)) {
         return var2;
      } else {
         try (Stream<java.nio.file.Path> var3 = Files.list(var1)) {
            var3.filter(var0x -> Files.isRegularFile(var0x)).forEach(var1x -> {
               String var2x = var1x.getFileName().toString();
               if (var2x.endsWith(".json")) {
                  try {
                     String var3x = Files.readString(var1x, StandardCharsets.UTF_8);
                     JsonObject var4 = JsonParser.parseString(var3x).getAsJsonObject();
                     String var5 = var4.has("username") ? var4.get("username").getAsString() : var2x.replace(".json", "");
                     UUID var6 = null;
                     if (var4.has("uuid")) {
                        try {
                           var6 = UUID.fromString(var4.get("uuid").getAsString());
                        } catch (Exception var8x) {
                        }
                     }

                     String var7 = var4.has("credential") ? var4.get("credential").getAsString() : var3x;
                     var2.add(new Data4(var5, var6, var1x, var7));
                  } catch (Exception var9) {
                  }
               } else if (var2x.endsWith(".txt")) {
                  String var10 = var2x.replaceAll("\\.txt$", "");
                  var2.add(new Data4(var10, null, var1x, null));
               }
            });
         } catch (Exception var8) {
         }

         return var2;
      }
   }

   public static void method4855(MinecraftClient var0, String var1, UUID var2, String var3) {
      try {
         Path var4 = method4853(var0);
         Path var5 = var4.resolve(var1 + ".json");
         JsonObject var6 = new JsonObject();
         var6.addProperty("username", var1);
         if (var2 != null) {
            var6.addProperty("uuid", var2.toString());
         }

         var6.addProperty("credential", var3 != null ? var3 : "");
         var6.addProperty("savedAt", System.currentTimeMillis());
         Files.writeString(var5, var6.toString(), StandardCharsets.UTF_8);
      } catch (Exception var7) {
         var7.printStackTrace();
      }
   }
}

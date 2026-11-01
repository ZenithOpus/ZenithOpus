package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JsonIo {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Json");
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

   private JsonIo() {
   }

   public static Gson getGson() {
      return gson;
   }

   public static JsonObject method1009(Path var0) {
      if (!Files.isRegularFile(var0)) {
         return null;
      } else {
         try {
            JsonObject var2;
            try (BufferedReader var1 = Files.newBufferedReader(var0, StandardCharsets.UTF_8)) {
               var2 = JsonParser.parseReader(var1).getAsJsonObject();
            }

            return var2;
         } catch (RuntimeException | IOException var6) {
            logger.error("Не удалось прочитать {}", var0, var6);
            return null;
         }
      }
   }

   public static boolean method1010(Path var0, JsonObject var1) {
      try {
         Files.createDirectories(var0.getParent());
         Path var2 = var0.resolveSibling(var0.getFileName() + ".tmp");

         try (BufferedWriter var3 = Files.newBufferedWriter(var2, StandardCharsets.UTF_8)) {
            gson.toJson(var1, var3);
         }

         Files.move(var2, var0, StandardCopyOption.REPLACE_EXISTING);
         return true;
      } catch (RuntimeException | IOException var8) {
         logger.error("Не удалось записать {}", var0, var8);
         return false;
      }
   }
}

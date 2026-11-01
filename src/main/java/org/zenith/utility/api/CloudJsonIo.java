package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CloudJsonIo {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Cloud");
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

   private CloudJsonIo() {
   }

   public static JsonObject method5992(Path var0) {
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

   public static boolean method5993(Path var0, JsonObject var1) {
      Path var2 = null;

      boolean var4;
      try {
         Files.createDirectories(var0.getParent());
         var2 = Files.createTempFile(var0.getParent(), var0.getFileName() + ".", ".tmp");
         Files.writeString(var2, gson.toJson(var1), StandardCharsets.UTF_8);

         try (FileChannel var3 = FileChannel.open(var2, StandardOpenOption.WRITE)) {
            var3.force(true);
         }

         try {
            Files.move(var2, var0, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
         } catch (AtomicMoveNotSupportedException var18) {
            Files.move(var2, var0, StandardCopyOption.REPLACE_EXISTING);
         }

         var2 = null;
         return true;
      } catch (RuntimeException | IOException var20) {
         logger.error("Не удалось записать {}", var0, var20);
         var4 = false;
      } finally {
         if (var2 != null) {
            try {
               Files.deleteIfExists(var2);
            } catch (IOException var16) {
            }
         }
      }

      return var4;
   }
}

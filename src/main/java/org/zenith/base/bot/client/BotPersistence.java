package org.zenith.base.bot.client;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import org.zenith.Zenith;
import org.zenith.utility.misc.AesGcmCrypto;

public final class BotPersistence {
   private static final String FILE_NAME = "bots." + "Zenith".toLowerCase();
   private static final String KEY = "siMunids";

   private BotPersistence() {
   }

   public static JsonObject load() {
      File var0 = file();
      if (!var0.exists()) {
         return null;
      } else {
         try {
            String var1 = Files.readString(var0.toPath(), StandardCharsets.UTF_8).trim();
            if (var1.isBlank()) {
               return null;
            } else {
               byte[] var2 = Base64.getDecoder().decode(var1);
               byte[] var3 = AesGcmCrypto.method5340(var2, "siMunids");
               return JsonParser.parseString(new String(var3, StandardCharsets.UTF_8)).getAsJsonObject();
            }
         } catch (Exception var4) {
            return null;
         }
      }
   }

   public static void save(JsonObject var0) {
      if (var0 != null) {
         try {
            File var1 = file();
            File var2 = var1.getParentFile();
            if (var2 != null) {
               var2.mkdirs();
            }

            String var3 = new GsonBuilder().setPrettyPrinting().create().toJson(var0);
            String var4 = Base64.getEncoder().encodeToString(AesGcmCrypto.method5339(var3.getBytes(StandardCharsets.UTF_8), "siMunids"));
            Files.writeString(var1.toPath(), var4, StandardCharsets.UTF_8);
         } catch (Exception var5) {
         }
      }
   }

   private static File file() {
      return new File(Zenith.file, FILE_NAME);
   }
}

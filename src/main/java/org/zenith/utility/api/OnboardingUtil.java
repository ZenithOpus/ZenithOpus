package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import org.zenith.Zenith;

public final class OnboardingUtil {
   private static final String text = "onboarding.json";
   private static final int CONST_intValue = 1;
   private static Boolean booleanValue;

   private OnboardingUtil() {
   }

   public static boolean method2705() {
      if (booleanValue != null) {
         return booleanValue;
      } else {
         File var0 = new File(Zenith.file, "onboarding.json");
         if (!var0.isFile()) {
            booleanValue = false;
            return false;
         } else {
            try (FileReader var1 = new FileReader(var0)) {
               JsonObject var2 = JsonParser.parseReader(var1).getAsJsonObject();
               booleanValue = var2.has("version") && var2.get("version").getAsInt() >= 1;
            } catch (Exception var6) {
               booleanValue = false;
            }

            return booleanValue;
         }
      }
   }

   public static void complete() {
      booleanValue = true;
      JsonObject var0 = new JsonObject();
      var0.addProperty("version", 1);
      var0.addProperty("completedAt", System.currentTimeMillis());
      File var1 = Zenith.file;
      if (var1.isDirectory() || var1.mkdirs()) {
         try (FileWriter var2 = new FileWriter(new File(var1, "onboarding.json"))) {
            new Gson().toJson(var0, var2);
         } catch (Exception var7) {
         }
      }
   }

   public static void reset() {
      booleanValue = false;
      File var0 = new File(Zenith.file, "onboarding.json");
      if (var0.isFile() && !var0.delete()) {
         booleanValue = null;
      }
   }
}

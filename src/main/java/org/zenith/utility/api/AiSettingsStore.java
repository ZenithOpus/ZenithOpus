package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.zenith.Zenith;
import org.zenith.utility.misc.AesGcmCrypto;
import org.zenith.utility.misc.ShownProvider;

public final class AiSettingsStore {
   private static AiSettingsStore INSTANCE;
   private static final ExecutorService executorService = Executors.newSingleThreadExecutor(var0 -> {
      Thread var1 = new Thread(var0, "zenith-ai-settings");
      var1.setDaemon(true);
      return var1;
   });
   private String text;
   private boolean flag;
   private final File file;
   private final Map<ShownProvider, String> items;
   private final Map<ShownProvider, String> items2;
   private final Map<ShownProvider, String> items3;
   private ShownProvider shownProvider;
   private boolean flag2;
   private boolean flag3;

   public AiSettingsStore() {
      this.file = new File(Zenith.file, "ai.zenith");
      this.items = new HashMap<>();
      this.items2 = new HashMap<>();
      this.items3 = new HashMap<>();
      this.shownProvider = ShownProvider.INSTANCE;
      this.flag2 = true;
      this.flag3 = true;
   }

   public static synchronized AiSettingsStore method4467() {
      if (INSTANCE == null) {
         INSTANCE = new AiSettingsStore();
         INSTANCE.load();
      }

      return INSTANCE;
   }

   private static String password() {
      return "zenith-ai:" + System.getProperty("user.name", "") + ":" + System.getProperty("os.name", "");
   }

   private void load() {
      if (this.file.isFile()) {
         try {
            byte[] var1 = AesGcmCrypto.method5340(Files.readAllBytes(this.file.toPath()), password());
            JsonObject var2 = JsonParser.parseString(new String(var1, StandardCharsets.UTF_8)).getAsJsonObject();
            if (var2.has("customProviders")) {
               for (JsonElement var4 : var2.getAsJsonArray("customProviders")) {
                  JsonObject var5 = var4.getAsJsonObject();
                  ShownProvider.method7333(
                     ShownProvider.method7332(
                        var5.get("id").getAsString(),
                        var5.get("title").getAsString(),
                        clean(var5.get("endpoint").getAsString()),
                        var5.has("model") ? clean(var5.get("model").getAsString()) : ""
                     )
                  );
               }
            }

            if (var2.has("provider")) {
               this.shownProvider = ShownProvider.method7337(var2.get("provider").getAsString());
            }

            if (var2.has("autoApply")) {
               this.flag2 = var2.get("autoApply").getAsBoolean();
            }

            if (var2.has("autoFix")) {
               this.flag3 = var2.get("autoFix").getAsBoolean();
            }

            for (ShownProvider var6 : ShownProvider.method7336()) {
               if (var2.has(var6.getText())) {
                  JsonObject var7 = var2.getAsJsonObject(var6.getText());
                  if (var7.has("key")) {
                     this.items.put(var6, clean(var7.get("key").getAsString()));
                  }

                  if (var7.has("model")) {
                     this.items2.put(var6, clean(var7.get("model").getAsString()));
                  }

                  if (var7.has("endpoint")) {
                     this.items3.put(var6, clean(var7.get("endpoint").getAsString()));
                  }
               }
            }

            if (var2.has("velorai") && ShownProvider.method7335().isEmpty()) {
               JsonObject var10 = var2.getAsJsonObject("velorai");
               if (var10.has("key")) {
                  String var12 = var10.has("endpoint") ? var10.get("endpoint").getAsString() : "https://velorai.lol/v1";
                  String var14 = var10.has("model") ? var10.get("model").getAsString() : "z-ai/glm-5.3-uncensored";
                  String var15 = URI.create(var12).getHost();
                  ShownProvider var16 = ShownProvider.method7332("c_legacy", var15 == null ? "API" : var15, var12, var14);
                  ShownProvider.method7333(var16);
                  this.items.put(var16, var10.get("key").getAsString());
                  this.items2.put(var16, var14);
                  if ("velorai".equals(var2.has("provider") ? var2.get("provider").getAsString() : "")) {
                     this.shownProvider = var16;
                  }

                  this.save();
               }
            }
         } catch (Exception var8) {
         }
      }
   }

   public synchronized void save() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("provider", this.shownProvider.getText());
      var1.addProperty("autoApply", this.flag2);
      var1.addProperty("autoFix", this.flag3);
      JsonArray var2 = new JsonArray();

      for (ShownProvider var4 : ShownProvider.method7335()) {
         JsonObject var5 = new JsonObject();
         var5.addProperty("id", var4.getText());
         var5.addProperty("title", var4.title());
         var5.addProperty("endpoint", var4.getText4());
         var5.addProperty("model", var4.getItems3().get(0));
         var2.add(var5);
      }

      var1.add("customProviders", var2);

      for (ShownProvider var6 : ShownProvider.method7336()) {
         JsonObject var7 = new JsonObject();
         if (!this.method4469(var6).isEmpty()) {
            var7.addProperty("key", this.method4469(var6));
         }

         if (this.items2.containsKey(var6)) {
            var7.addProperty("model", this.items2.get(var6));
         }

         if (this.items3.containsKey(var6)) {
            var7.addProperty("endpoint", this.items3.get(var6));
         }

         if (var7.size() > 0) {
            var1.add(var6.getText(), var7);
         }
      }

      this.text = var1.toString();
      if (!this.flag) {
         this.flag = true;
         executorService.execute(this::flush);
      }
   }

   private void flush() {
      while (true) {
         String var1;
         synchronized (this) {
            var1 = this.text;
            this.text = null;
            if (var1 == null) {
               this.flag = false;
               return;
            }
         }

         try {
            Files.write(this.file.toPath(), AesGcmCrypto.method5339(var1.getBytes(StandardCharsets.UTF_8), password()));
         } catch (Exception var4) {
         }
      }
   }

   public synchronized ShownProvider getShownProvider() {
      return this.shownProvider;
   }

   public synchronized void method4468(ShownProvider var1) {
      if (!this.shownProvider.equals(var1)) {
         this.shownProvider = var1;
         this.save();
      }
   }

   public synchronized String method4469(ShownProvider var1) {
      return this.items.getOrDefault(var1, "");
   }

   public synchronized void method4470(ShownProvider var1, String var2) {
      if (var2 != null && !var2.isBlank()) {
         this.items.put(var1, clean(var2));
      } else {
         this.items.remove(var1);
      }

      this.save();
   }

   public synchronized boolean method4471(ShownProvider var1) {
      return !var1.isFlag() || !this.method4469(var1).isEmpty();
   }

   public synchronized String method4472(ShownProvider var1) {
      String var2 = this.items2.get(var1);
      return var2 != null && !var2.isBlank() ? var2 : var1.getItems3().get(0);
   }

   public synchronized void method4473(ShownProvider var1, String var2) {
      if (var2 != null && !var2.isBlank()) {
         this.items2.put(var1, clean(var2));
      } else {
         this.items2.remove(var1);
      }

      this.save();
   }

   public synchronized String method4474(ShownProvider var1) {
      String var2 = this.items3.get(var1);
      return var2 != null && !var2.isBlank() ? var2 : var1.getText4();
   }

   public synchronized void method4475(ShownProvider var1, String var2) {
      if (var2 != null && !var2.isBlank() && !var2.strip().equals(var1.getText4())) {
         this.items3.put(var1, clean(var2));
      } else {
         this.items3.remove(var1);
      }

      this.save();
   }

   public synchronized ShownProvider method4476(String var1, String var2, String var3, String var4) {
      String var5 = "c_" + Long.toString(System.currentTimeMillis(), 36);
      ShownProvider var6 = ShownProvider.method7332(var5, var1.strip(), clean(var2), clean(var4));
      ShownProvider.method7333(var6);
      if (var3 != null && !var3.isBlank()) {
         this.items.put(var6, clean(var3));
      }

      if (var4 != null && !var4.isBlank()) {
         this.items2.put(var6, clean(var4));
      }

      this.shownProvider = var6;
      this.save();
      return var6;
   }

   public synchronized void method4477(ShownProvider var1) {
      if (var1 != null && var1.isFlag2()) {
         ShownProvider.method7334(var1);
         this.items.remove(var1);
         this.items2.remove(var1);
         this.items3.remove(var1);
         if (this.shownProvider.equals(var1)) {
            this.shownProvider = ShownProvider.INSTANCE;
         }

         this.save();
      }
   }

   public synchronized boolean isFlag2() {
      return this.flag2;
   }

   public synchronized void method4478(boolean var1) {
      this.flag2 = var1;
      this.save();
   }

   public synchronized boolean isFlag3() {
      return this.flag3;
   }

   public synchronized void method4479(boolean var1) {
      this.flag3 = var1;
      this.save();
   }

   public static String clean(String var0) {
      if (var0 == null) {
         return "";
      } else {
         String var1 = var0.strip();

         while (!var1.isEmpty() && "`'\".,;:".indexOf(var1.charAt(0)) >= 0) {
            var1 = var1.substring(1).strip();
         }

         while (!var1.isEmpty() && "`'\".,;:".indexOf(var1.charAt(var1.length() - 1)) >= 0) {
            var1 = var1.substring(0, var1.length() - 1).strip();
         }

         return var1;
      }
   }

   public static String method4480(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         return var0.length() <= 10 ? "••••" : var0.substring(0, 5) + "••••" + var0.substring(var0.length() - 4);
      } else {
         return "";
      }
   }
}

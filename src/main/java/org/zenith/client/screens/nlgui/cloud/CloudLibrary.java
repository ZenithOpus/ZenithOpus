package org.zenith.client.screens.nlgui.cloud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.zenith.Zenith;
import org.zenith.utility.api.StoreApi;

public final class CloudLibrary {
   private static final String FILE_NAME = "cloud-library.json";
   private static final int MAX_ENTRIES = 512;
   private final Map<UUID, StoreApi.Data2> entries = new LinkedHashMap<>();
   private final File file;

   public CloudLibrary() {
      this.file = new File(Zenith.file, "cloud-library.json");
      this.load();
   }

   public synchronized List<StoreApi.Data2> all() {
      return new ArrayList<>(this.entries.values());
   }

   public synchronized boolean contains(UUID var1) {
      return var1 != null && this.entries.containsKey(var1);
   }

   public synchronized void remember(StoreApi.Data2 var1) {
      if (var1 != null) {
         this.entries.remove(var1.getUUID());
         this.entries.put(var1.getUUID(), var1);

         while (this.entries.size() > 512) {
            this.entries.remove(this.entries.keySet().iterator().next());
         }

         this.save();
      }
   }

   public synchronized void forget(UUID var1) {
      if (var1 != null && this.entries.remove(var1) != null) {
         this.save();
      }
   }

   private void load() {
      if (this.file.isFile()) {
         try {
            JsonElement var1 = JsonParser.parseString(Files.readString(this.file.toPath(), StandardCharsets.UTF_8));
            if (!var1.isJsonArray()) {
               return;
            }

            for (JsonElement var3 : var1.getAsJsonArray()) {
               if (var3.isJsonObject()) {
                  try {
                     StoreApi.Data2 var4 = StoreApi.Data2.method2003(var3.getAsJsonObject());
                     this.entries.put(var4.getUUID(), var4);
                  } catch (Exception var5) {
                  }
               }
            }
         } catch (Exception var6) {
         }
      }
   }

   private void save() {
      try {
         JsonArray var1 = new JsonArray();

         for (StoreApi.Data2 var3 : this.entries.values()) {
            var1.add(toJson(var3));
         }

         Files.createDirectories(this.file.toPath().getParent());
         Files.writeString(this.file.toPath(), var1.toString(), StandardCharsets.UTF_8);
      } catch (Exception var4) {
      }
   }

   private static JsonObject toJson(StoreApi.Data2 var0) {
      JsonObject var1 = new JsonObject();
      var1.addProperty("id", var0.getUUID().toString());
      var1.addProperty("kind", var0.kind());
      var1.addProperty("name", var0.name());
      var1.addProperty("fileName", var0.fileName());
      var1.addProperty("description", var0.description());
      var1.addProperty("server", var0.server());
      var1.addProperty("sizeBytes", var0.getLongValue());
      var1.addProperty("sha256", var0.getText6());
      var1.addProperty("visibility", var0.getText7());
      var1.addProperty("version", var0.getLongValue2());
      var1.addProperty("createdAt", var0.getLongValue3());
      var1.addProperty("updatedAt", var0.getLongValue4());
      var1.addProperty("ownerUid", var0.getLongValue5());
      var1.addProperty("ownerName", var0.getText8());
      var1.addProperty("mine", var0.mine());
      var1.addProperty("hasPreview", var0.hasPreview());
      var1.addProperty("likes", var0.getIntValue());
      var1.addProperty("liked", var0.isFlag3());
      var1.addProperty("codes", var0.getIntValue2());
      return var1;
   }
}

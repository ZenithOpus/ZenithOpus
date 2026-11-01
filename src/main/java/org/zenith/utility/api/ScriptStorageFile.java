package org.zenith.utility.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import org.slf4j.LoggerFactory;
import org.zenith.client.scripting.ScriptValueUtils;

public final class ScriptStorageFile {
   public static final int CONST_intValue = 262144;
   private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
   private final Path path;
   private JsonObject jsonObject = new JsonObject();
   private boolean dirty;
   private int size;
   private int intValue;

   public ScriptStorageFile(Path var1) {
      this.path = var1;

      try {
         if (Files.isRegularFile(var1)) {
            if (Files.size(var1) > 262144L) {
               Path var2 = var1.resolveSibling(var1.getFileName() + ".too-big-" + System.currentTimeMillis());
               Files.move(var1, var2, StandardCopyOption.REPLACE_EXISTING);
               LoggerFactory.getLogger("ZenithScripts").warn("Хранилище {} больше {} КБ — сохранено как {}", new Object[]{var1, 256, var2.getFileName()});
            } else {
               JsonElement var6 = JsonParser.parseString(Files.readString(var1, StandardCharsets.UTF_8));
               if (var6.isJsonObject()) {
                  this.jsonObject = var6.getAsJsonObject();
               }
            }
         }
      } catch (Exception var5) {
         try {
            if (Files.isRegularFile(var1)) {
               Files.copy(var1, var1.resolveSibling(var1.getFileName() + ".broken-" + System.currentTimeMillis()), StandardCopyOption.REPLACE_EXISTING);
            }
         } catch (Exception var4) {
         }
      }

      this.size = method1005(gson.toJson(this.jsonObject));

      for (JsonElement var3 : this.jsonObject.asMap().values()) {
         if (!var3.isJsonNull()) {
            this.intValue++;
         }
      }
   }

   public static int method1005(CharSequence var0) {
      int var1 = 0;
      int var2 = 0;

      for (int var3 = var0.length(); var2 < var3; var2++) {
         char var4 = var0.charAt(var2);
         if (var4 < 128) {
            var1++;
         } else if (var4 < 2048) {
            var1 += 2;
         } else if (Character.isHighSurrogate(var4) && var2 + 1 < var3 && Character.isLowSurrogate(var0.charAt(var2 + 1))) {
            var1 += 4;
            var2++;
         } else {
            var1 += 3;
         }
      }

      return var1;
   }

   public JsonElement method1006(String var1) {
      return this.jsonObject.get(var1);
   }

   public Set<String> method1007() {
      return this.jsonObject.keySet();
   }

   public void method1008(String var1, JsonElement var2) {
      if (var1.length() > 128) {
         throw ScriptValueUtils.method6251("ключ длиннее 128 символов");
      } else {
         JsonElement var3 = this.jsonObject.get(var1);
         boolean var4 = var3 != null && !var3.isJsonNull();
         boolean var5 = var2 != null && !var2.isJsonNull();
         int var6 = !var4 && !var5 ? 0 : method1005(gson.toJson(var1)) + 1;
         int var7 = var4 ? var6 + method1005(gson.toJson(var3)) : 0;
         int var8 = var5 ? var6 + method1005(gson.toJson(var2)) : 0;
         int var9 = this.intValue - (var4 ? 1 : 0) + (var5 ? 1 : 0);
         int var10 = this.size - var7 + var8 - Math.max(0, this.intValue - 1) + Math.max(0, var9 - 1);
         if (var10 > 262144) {
            throw ScriptValueUtils.method6251("хранилище скрипта больше 256 КБ");
         } else {
            if (var5) {
               this.jsonObject.add(var1, var2);
            } else {
               this.jsonObject.remove(var1);
            }

            this.intValue = var9;
            this.size = var10;
            this.dirty = true;
         }
      }
   }

   public int size() {
      return this.size;
   }

   public void flush() {
      if (this.dirty) {
         this.dirty = false;

         try {
            Files.createDirectories(this.path.getParent());
            Path var1 = this.path.resolveSibling(this.path.getFileName() + ".tmp");
            Files.writeString(var1, gson.toJson(this.jsonObject), StandardCharsets.UTF_8);
            Files.move(var1, this.path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (Exception var2) {
            this.dirty = true;
         }
      }
   }
}

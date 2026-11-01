package org.zenith.utility.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.zenith.Zenith;

public final class Index2 {
   private static final byte[] CONST_byteValues = new byte[]{90, 87, 66, 49};
   private static final long CONST_longValue = TimeUnit.SECONDS.toNanos(20L);
   private static final Duration duration = Duration.ofSeconds(30L);
   private final Path path;
   private final Path path2;
   private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10L)).followRedirects(Redirect.NORMAL).build();
   private final Map<String, byte[]> items = new ConcurrentHashMap<>();
   private volatile Map<String, String> items2;
   private volatile long longValue;
   private volatile boolean flag;
   private volatile long longValue2;

   public Index2() {
      String var1 = System.getProperty("zenith.wardrobe.src");
      Path var2 = var1 != null && !var1.isBlank() && !Boolean.getBoolean("zenith.wardrobe.remote") ? Path.of(var1) : null;
      this.path = var2 != null && Files.isDirectory(var2.resolve("models")) ? var2 : null;
      this.path2 = Zenith.file.toPath().resolve("wardrobe").resolve("cache");
   }

   public Index2.Data method6140(String var1) throws Exception {
      if (this.path != null) {
         return new Index2.Data(
            Files.readAllBytes(this.path.resolve("models").resolve(var1 + ".json")), Files.readAllBytes(this.path.resolve("textures").resolve(var1 + ".png"))
         );
      } else {
         Index2.Data builtin = method6149(var1);
         if (builtin != null) {
            return builtin;
         }
         var var2 = this.method6141();
         String var3 = (String)var2.get(var1);
         if (var3 != null) {
            byte[] var4 = this.method6142(var1, var2);
            return method6144(var1, var4, this.method6143(var3));
         } else if (this.flag) {
            synchronized (this) {
               if (this.items2 == var2 && System.nanoTime() >= this.longValue) {
                  this.items2 = null;
               }
            }

            throw new Index2.Entry("модели нет в кэшированном индексе: " + var1);
         } else {
            throw new IOException("модели нет в индексе: " + var1);
         }
      }
   }

   private static Index2.Data method6149(String var0) {
      try (
         InputStream var1 = Index2.class.getResourceAsStream("/assets/zenith/wardrobe/models/" + var0 + ".json");
      ) {
         if (var1 == null) {
            return null;
         } else {
            byte[] var2 = var1.readAllBytes();

            Index2.Data var6;
            try (InputStream var3 = Index2.class.getResourceAsStream("/assets/zenith/wardrobe/textures/" + var0 + ".png")) {
               if (var3 == null) {
                  return null;
               }

               var6 = new Index2.Data(var2, var3.readAllBytes());
            }

            return var6;
         }
      } catch (IOException var8) {
         return null;
      }
   }

   private Map<String, String> method6141() throws Exception {
      var var1 = this.items2;
      if (var1 != null) {
         return var1;
      } else {
         synchronized (this) {
            if (this.items2 != null) {
               return this.items2;
            } else if (System.nanoTime() < this.longValue) {
               throw new Index2.Entry("индекс гардероба недоступен");
            } else {
               Path var3 = this.path2.resolve("index.json");
               boolean var5 = false;

               byte[] var4;
               try {
                  var4 = this.method6146(method6147() + "/wardrobe/index.json");
                  Files.createDirectories(this.path2);
                  Files.write(var3, var4);
               } catch (Exception var11) {
                  if (!Files.isRegularFile(var3)) {
                     this.longValue = System.nanoTime() + CONST_longValue;
                     throw new Index2.Entry("индекс гардероба не скачан: " + var11.getMessage());
                  }

                  var4 = Files.readAllBytes(var3);
                  var5 = true;
               }

               JsonObject var6 = JsonParser.parseString(new String(var4, StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonObject("models");
               HashMap var7 = new HashMap();

               for (Map.Entry var9 : var6.entrySet()) {
                  var7.put((String)var9.getKey(), ((JsonElement)var9.getValue()).getAsJsonObject().get("hash").getAsString());
               }

               this.flag = var5;
               if (var5) {
                  this.longValue = System.nanoTime() + CONST_longValue;
               }

               this.items2 = var7;
               return var7;
            }
         }
      }
   }

   private byte[] method6142(String var1, Map<String, String> var2) throws Exception {
      byte[] var3 = this.items.get(var1);
      if (var3 != null) {
         return var3;
      } else {
         synchronized (this.items) {
            var3 = this.items.get(var1);
            if (var3 != null) {
               return var3;
            } else if (System.nanoTime() < this.longValue2) {
               throw new Index2.Entry("ключи гардероба ещё не получены");
            } else {
               StoreApi var5 = Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();

               try {
                  if (var5 == null) {
                     throw new IOException("облако не готово");
                  }

                  this.items.putAll(var5.method1982(new ArrayList<>(var2.keySet())).get(duration.toSeconds(), TimeUnit.SECONDS));
               } catch (Exception var8) {
                  this.longValue2 = System.nanoTime() + CONST_longValue;
                  throw new Index2.Entry("ключи гардероба не получены: " + var8.getMessage());
               }

               var3 = this.items.get(var1);
               if (var3 == null) {
                  throw new IOException("узел не выдал ключ модели " + var1);
               } else {
                  return var3;
               }
            }
         }
      }
   }

   private byte[] method6143(String var1) throws Exception {
      Path var2 = this.path2.resolve(var1 + ".zwm");
      if (Files.isRegularFile(var2)) {
         byte[] var3 = Files.readAllBytes(var2);
         if (var1.equals(method6148(var3))) {
            return var3;
         }

         Files.deleteIfExists(var2);
      }

      byte[] var6;
      try {
         var6 = this.method6146(method6147() + "/wardrobe/blobs/" + var1 + ".zwm");
      } catch (Exception var5) {
         throw new Index2.Entry("модель гардероба не скачана: " + var5.getMessage());
      }

      if (!var1.equals(method6148(var6))) {
         throw new IOException("хеш модели гардероба не совпал: " + var1);
      } else {
         Files.createDirectories(this.path2);
         Path var4 = this.path2.resolve(var1 + ".part");
         Files.write(var4, var6);
         Files.move(var4, var2, StandardCopyOption.REPLACE_EXISTING);
         return var6;
      }
   }

   private static Index2.Data method6144(String var0, byte[] var1, byte[] var2) throws Exception {
      if (var2.length >= 32
         && var2[0] == CONST_byteValues[0]
         && var2[1] == CONST_byteValues[1]
         && var2[2] == CONST_byteValues[2]
         && var2[3] == CONST_byteValues[3]) {
         Cipher var3 = Cipher.getInstance("AES/GCM/NoPadding");
         var3.init(2, new SecretKeySpec(var1, "AES"), new GCMParameterSpec(128, var2, 4, 12));
         var3.updateAAD(var0.getBytes(StandardCharsets.UTF_8));
         byte[] var4 = var3.doFinal(var2, 16, var2.length - 16);
         ByteBuffer var5 = ByteBuffer.wrap(method6145(var4));
         byte[] var6 = new byte[var5.getInt()];
         var5.get(var6);
         byte[] var7 = new byte[var5.getInt()];
         var5.get(var7);
         return new Index2.Data(var6, var7);
      } else {
         throw new IOException("неизвестный формат модели гардероба");
      }
   }

   private static byte[] method6145(byte[] var0) throws Exception {
      Inflater var1 = new Inflater();

      byte[] var8;
      try {
         var1.setInput(var0);
         ByteArrayOutputStream var2 = new ByteArrayOutputStream(var0.length * 3);
         byte[] var3 = new byte[16384];

         while (!var1.finished()) {
            int var4 = var1.inflate(var3);
            if (var4 == 0 && (var1.needsInput() || var1.needsDictionary())) {
               throw new IOException("модель гардероба повреждена");
            }

            var2.write(var3, 0, var4);
         }

         var8 = var2.toByteArray();
      } finally {
         var1.end();
      }

      return var8;
   }

   private byte[] method6146(String var1) throws Exception {
      HttpResponse var2 = this.httpClient.send(HttpRequest.newBuilder(URI.create(var1)).timeout(duration).GET().build(), BodyHandlers.ofByteArray());
      if (var2.statusCode() != 200) {
         throw new IOException("узел ответил " + var2.statusCode());
      } else {
         return (byte[])var2.body();
      }
   }

   private static String method6147() throws IOException {
      StoreApi var0 = Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();
      if (var0 == null) {
         throw new IOException("облако не готово");
      } else {
         return var0.method1983();
      }
   }

   private static String method6148(byte[] var0) throws Exception {
      return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(var0));
   }

   public static final class Data {
      private final byte[] byteValues;
      private final byte[] byteValues2;

      public byte[] byteValues() {
         return this.byteValues;
      }

      public byte[] byteValues2() {
         return this.byteValues2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.byteValues, other.byteValues) && java.util.Objects.equals(this.byteValues2, other.byteValues2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(byteValues, byteValues2);
      }

      @Override
      public String toString() {
         return "Data[" + "byteValues=" + this.byteValues + ", " + "byteValues2=" + this.byteValues2 + "]";
      }

      public Data(byte[] var1, byte[] var2) {
         this.byteValues = var1;
         this.byteValues2 = var2;
      }

      public byte[] getByteValues() {
         return this.byteValues;
      }

      public byte[] getByteValues2() {
         return this.byteValues2;
      }
   }

   public static final class Entry extends Exception {
      public Entry(String var1) {
         super(var1, null, false, false);
      }
   }
}

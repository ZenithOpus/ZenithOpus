package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Index {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Cosmetics");
   private static final String text = "index.json";
   private static final String text2 = "/cosmetics/index.json";
   private static final String text3 = "/cosmetics/blobs/";
   private static final String text4 = ".model";
   private static final String text5 = "/cosmetics/overrides.json";
   private static final String text6 = "/cosmetics/overrides";
   private static final Duration duration = Duration.ofSeconds(10L);
   private static final Duration duration2 = Duration.ofSeconds(30L);
   private static final Duration duration3 = Duration.ofSeconds(60L);
   private final Path path;
   private final Path path2;
   private final Supplier<List<String>> supplier;
   private volatile String text7;
   private volatile HttpClient httpClient;
   private volatile String lastError = "";
   private volatile boolean flag;
   private static final int CONST_intValue = 3;
   private static final long CONST_longValue = 1500L;

   public Index(Path var1, Supplier<List<String>> var2) {
      this.path = var1;
      this.supplier = var2;
      this.path2 = var1.getParent() == null ? null : var1.getParent().resolve("pack");
   }

   private Path method1452() {
      if (this.path2 == null) {
         return null;
      } else {
         Path var1 = this.path2.resolve("index.json");
         return Files.isRegularFile(var1) ? var1 : null;
      }
   }

   public List<CosmeticItemInfo> method1453() {
      Path var1 = this.method1452();
      if (var1 != null) {
         var var2 = method1471(method1469(var1));
         if (!var2.isEmpty()) {
            this.lastError = "";
            logger.info("Косметика: каталог из локального пака {} ({} элементов)", var1, var2.size());
            return var2;
         }

         logger.warn("Косметика: локальный каталог {} пуст или битый — идём на узел", var1);
      }

      Path var7 = this.path.resolve("index.json");
      boolean var3 = Files.isRegularFile(var7);
      if (var3 && !this.flag) {
         var var4 = method1471(method1469(var7));
         if (!var4.isEmpty()) {
            return var4;
         }

         logger.warn("Косметика: кеш каталога пуст или битый — перечитываем с узла");
      }

      byte[] var8 = this.method1468("/cosmetics/index.json", duration2);
      var var5 = method1471(var8);
      if (!var5.isEmpty()) {
         this.flag = false;
         method1470(var7, var8);
         return var5;
      } else {
         if (var3) {
            var var6 = method1471(method1469(var7));
            if (!var6.isEmpty()) {
               logger.info("Косметика: узел недоступен, каталог из кеша ({} элементов)", var6.size());
               return var6;
            }
         }

         logger.warn("Косметика: каталог недоступен — ни сети, ни кеша");
         return List.of();
      }
   }

   public void method1454() {
      this.flag = true;
   }

   public byte[] method1455() {
      try {
         return this.method1465("/cosmetics/overrides.json", duration2);
      } catch (IOException var2) {
         logger.debug("Косметика: общей подгонки на узле нет ({})", var2.getMessage());
         return null;
      }
   }

   public String method1456(byte[] var1, String var2) {
      String var3 = this.method1463("/cosmetics/overrides");

      for (int var4 = 1; var4 <= 3; var4++) {
         try {
            HttpRequest var5 = HttpRequest.newBuilder(URI.create(var3))
               .timeout(duration2)
               .header("User-Agent", "ZenithDLC")
               .header("Content-Type", "application/json; charset=utf-8")
               .header("X-Zenith-Secret", var2 == null ? "" : var2)
               .POST(BodyPublishers.ofByteArray(var1))
               .build();
            HttpResponse var6 = this.method1466().send(var5, BodyHandlers.ofString());
            if (var6.statusCode() == 200) {
               this.lastError = "";
               return (String)var6.body();
            }

            this.lastError = var3 + " — узел ответил " + var6.statusCode();
            logger.warn("Косметика: публикация подгонки — {} {}", var6.statusCode(), var6.body());
            return null;
         } catch (InterruptedException var7) {
            Thread.currentThread().interrupt();
            this.lastError = "публикация прервана";
            return null;
         } catch (RuntimeException | IOException var8) {
            this.lastError = var3 + " — " + var8.getMessage();
            if (var4 >= 3) {
               logger.warn("Косметика: публикация подгонки не дошла после {} попыток: {}", var4, var8.toString());
               return null;
            }

            logger.warn("Косметика: публикация подгонки — попытка {} не дошла: {}", var4, var8.toString());
            method1457(1500L * var4);
         }
      }

      return null;
   }

   private static void method1457(long var0) {
      try {
         Thread.sleep(var0);
      } catch (InterruptedException var3) {
         Thread.currentThread().interrupt();
      }
   }

   public byte[] method1458(CosmeticItemInfo var1) throws IOException {
      if (this.path2 != null) {
         Path var2 = this.path2.resolve("blobs").resolve(var1.getText6() + ".model");
         if (Files.isRegularFile(var2)) {
            byte[] var3 = Files.readAllBytes(var2);
            if (method1472(var3, var1.getText6())) {
               return var3;
            }

            logger.warn("Косметика: {} в локальном паке не совпал с хешем — качаем с узла", var1.getText6());
         }
      }

      Path var4 = this.method1462(var1);
      if (Files.isRegularFile(var4)) {
         byte[] var5 = Files.readAllBytes(var4);
         if (method1472(var5, var1.getText6())) {
            return var5;
         }

         logger.warn("Косметика: {} в кеше битый — перекачиваем", var1.getText6());
         Files.deleteIfExists(var4);
      }

      byte[] var6 = this.method1465("/cosmetics/blobs/" + var1.getText6() + ".model", duration3);
      if (!method1472(var6, var1.getText6())) {
         throw new IOException("хеш скачанной модели не совпал: " + var1.getText6());
      } else {
         method1470(var4, var6);
         return var6;
      }
   }

   public boolean method1459(CosmeticItemInfo var1) {
      return Files.isRegularFile(this.method1462(var1));
   }

   public long method1460() {
      long var1 = 0L;

      try (Stream<java.nio.file.Path> var3 = Files.list(this.path)) {
         for (Object var5_o : (Iterable<?>)(Object)(var3.toList())) {
            Path var5 = (Path)var5_o;
            var1 += Files.size(var5);
         }
      } catch (RuntimeException | IOException var8) {
      }

      return var1;
   }

   public long method1461() {
      long var1 = 0L;

      try (Stream<java.nio.file.Path> var3 = Files.list(this.path)) {
         for (Object var5_o : (Iterable<?>)(Object)(var3.toList())) {
            Path var5 = (Path)var5_o;
            long var6 = Files.size(var5);
            if (Files.deleteIfExists(var5)) {
               var1 += var6;
            }
         }
      } catch (RuntimeException | IOException var10) {
         logger.warn("Косметика: кеш стёрся не полностью: {}", var10.toString());
      }

      this.flag = true;
      return var1;
   }

   public Path getPath() {
      return this.path;
   }

   private Path method1462(CosmeticItemInfo var1) {
      return this.path.resolve(var1.getText6() + ".model");
   }

   private String method1463(String var1) {
      var var2 = this.method1464();
      return var2.isEmpty() ? var1 : (String)var2.get(0) + var1;
   }

   private List<String> method1464() {
      List<String> var1 = this.supplier.get();
      ArrayList var2 = new ArrayList();
      if (var1 != null) {
         for (Object var4_o : (Iterable<?>)(Object)(var1)) {
            String var4 = (String)var4_o;
            if (var4 != null && !var4.isBlank()) {
               String var5 = var4.strip();

               while (var5.endsWith("/")) {
                  var5 = var5.substring(0, var5.length() - 1);
               }

               if (!var2.contains(var5)) {
                  var2.add(var5);
               }
            }
         }
      }

      return RegionUtil.method2043(var2, this.text7);
   }

   private byte[] method1465(String var1, Duration var2) throws IOException {
      List<String> var3 = this.method1464();
      if (var3.isEmpty()) {
         return this.method1467(var1, var2);
      } else {
         IOException var4 = null;

         for (Object var6_o : (Iterable<?>)(Object)(var3)) {
            String var6 = (String)var6_o;
            try {
               byte[] var7 = this.method1467(var6 + var1, var2);
               this.text7 = var6;
               return var7;
            } catch (IOException var8) {
               if (Thread.currentThread().isInterrupted()) {
                  throw var8;
               }

               var4 = var8;
            }
         }

         throw var4;
      }
   }

   private HttpClient method1466() {
      HttpClient var1 = this.httpClient;
      if (var1 == null) {
         var1 = HttpClients.method2671(duration, Redirect.NORMAL);
         this.httpClient = var1;
      }

      return var1;
   }

   private byte[] method1467(String var1, Duration var2) throws IOException {
      HttpRequest var3;
      try {
         var3 = HttpRequest.newBuilder(URI.create(var1)).timeout(var2).header("User-Agent", "ZenithDLC").GET().build();
      } catch (RuntimeException var6) {
         throw new IOException("неверный адрес " + var1, var6);
      }

      try {
         HttpResponse var4 = this.method1466().send(var3, BodyHandlers.ofByteArray());
         if (var4.statusCode() != 200) {
            throw new IOException("узел ответил " + var4.statusCode() + " на " + var1);
         } else {
            return (byte[])var4.body();
         }
      } catch (InterruptedException var5) {
         Thread.currentThread().interrupt();
         throw new IOException("загрузка прервана", var5);
      }
   }

   private byte[] method1468(String var1, Duration var2) {
      try {
         byte[] var3 = this.method1465(var1, var2);
         this.lastError = "";
         return var3;
      } catch (IOException var5) {
         String var4 = this.method1463(var1);
         this.lastError = var4 + " — " + var5.getMessage();
         logger.info("Косметика: {} недоступен ({})", var4, var5.getMessage());
         return null;
      }
   }

   public String getLastError() {
      return this.lastError;
   }

   private static byte[] method1469(Path var0) {
      try {
         return Files.readAllBytes(var0);
      } catch (IOException var2) {
         logger.warn("Косметика: не читается {}", var0, var2);
         return null;
      }
   }

   private static void method1470(Path var0, byte[] var1) {
      if (var1 != null) {
         try {
            Files.createDirectories(var0.getParent());
            Path var2 = var0.resolveSibling(var0.getFileName() + ".tmp");
            Files.write(var2, var1);
            Files.move(var2, var0, StandardCopyOption.REPLACE_EXISTING);
         } catch (IOException var3) {
            logger.warn("Косметика: не записать кеш {}", var0, var3);
         }
      }
   }

   private static List<CosmeticItemInfo> method1471(byte[] var0) {
      if (var0 != null && var0.length != 0) {
         try {
            JsonObject var1 = JsonParser.parseString(new String(var0, StandardCharsets.UTF_8)).getAsJsonObject();
            if (var1.has("items") && var1.get("items").isJsonArray()) {
               JsonArray var2 = var1.getAsJsonArray("items");
               List<CosmeticItemInfo> var3 = new ArrayList<>(var2.size());

               for (JsonElement var5 : var2) {
                  if (var5.isJsonObject()) {
                     CosmeticItemInfo var6 = CosmeticItemInfo.method7748(var5.getAsJsonObject());
                     if (var6 != null) {
                        var3.add(var6);
                     }
                  }
               }

               return var3;
            } else {
               return List.of();
            }
         } catch (RuntimeException var7) {
            logger.warn("Косметика: каталог не разобрался: {}", var7.toString());
            return List.of();
         }
      } else {
         return List.of();
      }
   }

   private static boolean method1472(byte[] var0, String var1) {
      return var1 != null && !var1.isBlank() ? method1473(var0).equalsIgnoreCase(var1) : true;
   }

   private static String method1473(byte[] var0) {
      try {
         byte[] var1 = MessageDigest.getInstance("SHA-256").digest(var0);
         StringBuilder var2 = new StringBuilder(var1.length * 2);

         for (int var6 : var1) {
            var2.append(Character.forDigit(var6 >> 4 & 15, 16));
            var2.append(Character.forDigit(var6 & 15, 16));
         }

         return var2.toString();
      } catch (NoSuchAlgorithmException var7) {
         throw new IllegalStateException("SHA-256 недоступен", var7);
      }
   }
}

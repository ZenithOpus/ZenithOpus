package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import net.minecraft.util.Util;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.Storage;
import org.zenith.utility.misc.ImageLoader;

public final class OriginUtil {
   public static final String text = ".zjs";
   private static final String text2 = ".origin.json";
   private static final long CONST_longValue = 8388608L;
   private static final long CONST_longValue2 = 8388608L;
   private static final int CONST_intValue = 256;
   private static final int CONST_intValue2 = 128;
   private static final Map<String, OriginUtil.Data3> items = new ConcurrentHashMap<>();
   private static final Map<Path, OriginUtil.Data> items2 = new ConcurrentHashMap<>();
   private static final long CONST_longValue3 = 3000L;
   private static final String text3 = ".package.json";

   private OriginUtil() {
   }

   public static String method6914(Script var0) {
      StringBuilder var1 = new StringBuilder("zjs");
      method6915(var1, "v", var0.getVersion(), 24);
      method6915(var1, "a", var0.getText2(), 40);
      method6915(var1, "c", var0.getColor() == -1 ? "" : Integer.toHexString(var0.getColor()), 8);
      method6915(var1, "t", var0.getText3(), 80);
      method6915(var1, "l", var0.getText4(), 80);
      String var2 = var1.toString();
      return var2.length() > 250 ? var2.substring(0, 250) : var2;
   }

   private static void method6915(StringBuilder var0, String var1, String var2, int var3) {
      if (var2 != null && !var2.isBlank()) {
         String var4 = var2.strip();
         if (var4.length() > var3) {
            var4 = var4.substring(0, var3);
         }

         var0.append('|').append(var1).append('=').append(URLEncoder.encode(var4, StandardCharsets.UTF_8));
      }
   }

   public static OriginUtil.Data3 method6916(StoreApi.Data2 var0) {
      String var1 = var0 == null ? null : var0.server();
      if (var1 != null && var1.startsWith("zjs")) {
         OriginUtil.Data3 var2 = items.get(var1);
         if (var2 != null) {
            return var2;
         } else {
            OriginUtil.Data3 var3 = method6917(var1);
            if (items.size() > 512) {
               items.clear();
            }

            items.put(var1, var3);
            return var3;
         }
      } else {
         return OriginUtil.Data3.INSTANCE;
      }
   }

   private static OriginUtil.Data3 method6917(String var0) {
      LinkedHashMap<String, String> var1 = new LinkedHashMap<>();

      for (String var5 : var0.split("\\|")) {
         int var6 = var5.indexOf(61);
         if (var6 > 0) {
            try {
               var1.put(var5.substring(0, var6), URLDecoder.decode(var5.substring(var6 + 1), StandardCharsets.UTF_8));
            } catch (IllegalArgumentException var9) {
            }
         }
      }

      ArrayList var10 = new ArrayList();

      for (String var15 : var1.getOrDefault("t", "").split(",")) {
         if (!var15.isBlank()) {
            var10.add(var15.strip());
         }
      }

      int var12 = -1;

      try {
         if (var1.containsKey("c")) {
            var12 = (int)Long.parseLong((String)var1.get("c"), 16);
         }
      } catch (NumberFormatException var8) {
      }

      return new OriginUtil.Data3(((String)var1.getOrDefault("v", "")), List.copyOf(var10), var12, var1.getOrDefault("a", ""), var1.getOrDefault("l", ""));
   }

   public static boolean method6918(StoreApi.Data2 var0) {
      return var0 != null && "script".equalsIgnoreCase(var0.kind()) && var0.fileName().toLowerCase(Locale.ROOT).endsWith(".zjs");
   }

   public static byte[] method6919(Script var0) throws IOException {
      ArrayList<Path> var1 = new ArrayList();
      Path var2;
      if (var0.getPath() == null) {
         var2 = null;
         var1.add(var0.getPath2());
      } else {
         var2 = var0.getPath();

         try (Stream<java.nio.file.Path> var3 = Files.walk(var2, 6)) {
            var3.filter(var0x -> Files.isRegularFile(var0x)).filter(var0x -> {
               String var1x = var0x.getFileName().toString();
               return !var1x.startsWith(".") && !var1x.equals("storage.json") && !var1x.endsWith(".tmp");
            }).sorted().forEach(var1::add);
         }
      }

      if (var1.size() > 256) {
         throw new IOException("слишком много файлов: " + var1.size());
      } else {
         ByteArrayOutputStream var12 = new ByteArrayOutputStream();
         ZipOutputStream var4 = new ZipOutputStream(var12);

         try {
            for (Object var6_o : (Iterable<?>)(Object)(var1)) {
               Path var6 = (Path)var6_o;
               String var7 = var2 == null ? "main.js" : var2.relativize(var6).toString().replace('\\', '/');
               var4.putNextEntry(new ZipEntry(var7));
               Files.copy(var6, var4);
               var4.closeEntry();
            }
         } catch (Throwable var11) {
            try {
               var4.close();
            } catch (Throwable var8) {
               var11.addSuppressed(var8);
            }

            throw var11;
         }

         var4.close();
         if (var12.size() > 8388608L) {
            throw new IOException("скрипт больше 8 МБ");
         } else {
            return var12.toByteArray();
         }
      }
   }

   public static byte[] method6920(Script var0) {
      Path var1 = var0.method1412();
      if (var1 != null && Files.isRegularFile(var1)) {
         try {
            if (Files.size(var1) > 4194304L) {
               return null;
            } else {
               BufferedImage var2 = ImageLoader.method234(Files.readAllBytes(var1), 512);
               BufferedImage var3 = new BufferedImage(128, 128, 2);
               Graphics2D var4 = var3.createGraphics();
               var4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
               var4.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
               int var5 = Math.min(var2.getWidth(), var2.getHeight());
               int var6 = (var2.getWidth() - var5) / 2;
               int var7 = (var2.getHeight() - var5) / 2;
               var4.drawImage(var2, 0, 0, 128, 128, var6, var7, var6 + var5, var7 + var5, null);
               var4.dispose();
               ByteArrayOutputStream var8 = new ByteArrayOutputStream();
               ImageIO.write(var3, "png", var8);
               return var8.toByteArray();
            }
         } catch (Exception var9) {
            return null;
         }
      } else {
         return null;
      }
   }

   public static OriginUtil.Data2 method6921(Script var0) {
      if (var0 != null && var0.getPath() != null) {
         Path var1 = var0.getPath();
         long var2 = System.currentTimeMillis();
         OriginUtil.Data var4 = items2.get(var1);
         if (var4 != null && var2 - var4.checkedAt() < 3000L) {
            return var4.getData2();
         } else {
            Path var5 = var1.resolve(".origin.json");
            long var6 = var5.toFile().lastModified();
            if (var4 != null && var4.modified() == var6) {
               items2.put(var1, new OriginUtil.Data(var2, var6, var4.getData2()));
               return var4.getData2();
            } else {
               OriginUtil.Data2 var8 = method6922(var5);
               items2.put(var1, new OriginUtil.Data(var2, var6, var8));
               return var8;
            }
         }
      } else {
         return null;
      }
   }

   private static OriginUtil.Data2 method6922(Path var0) {
      if (!Files.isRegularFile(var0)) {
         return null;
      } else {
         try {
            JsonObject var1 = JsonParser.parseString(Files.readString(var0, StandardCharsets.UTF_8)).getAsJsonObject();
            return new OriginUtil.Data2(
               UUID.fromString(var1.get("id").getAsString()),
               var1.has("version") ? var1.get("version").getAsLong() : 1L,
               var1.has("sha") ? var1.get("sha").getAsString() : "",
               var1.has("name") ? var1.get("name").getAsString() : "",
               var1.has("owner") ? var1.get("owner").getAsString() : "",
               var1.has("mine") && var1.get("mine").getAsBoolean(),
               var1.has("installedAt") ? var1.get("installedAt").getAsLong() : 0L
            );
         } catch (Exception var2) {
            return null;
         }
      }
   }

   public static void method6923(Path var0, StoreApi.Data2 var1, boolean var2) throws IOException {
      JsonObject var3 = new JsonObject();
      var3.addProperty("id", var1.getUUID().toString());
      var3.addProperty("version", var1.getLongValue2());
      var3.addProperty("sha", var1.getText6());
      var3.addProperty("name", var1.name());
      var3.addProperty("owner", var1.getText8());
      var3.addProperty("mine", var2);
      var3.addProperty("installedAt", System.currentTimeMillis());
      Files.writeString(var0.resolve(".origin.json"), var3.toString(), StandardCharsets.UTF_8);
      items2.remove(var0);
   }

   public static void method6924(Script var0) {
      if (var0 != null && var0.getPath() != null) {
         try {
            Files.deleteIfExists(var0.getPath().resolve(".origin.json"));
            items2.remove(var0.getPath());
         } catch (IOException var2) {
         }
      }
   }

   public static Map<UUID, Script> method6925(Storage var0) {
      LinkedHashMap var1 = new LinkedHashMap();
      if (var0 == null) {
         return var1;
      } else {
         for (Script var3 : var0.method1293()) {
            OriginUtil.Data2 var4 = method6921(var3);
            if (var4 != null) {
               var1.put(var4.getUUID(), var3);
            }
         }

         return var1;
      }
   }

   public static Script method6926(Storage var0, UUID var1) {
      if (var0 == null) {
         return null;
      } else {
         Script var2 = null;

         for (Script var4 : var0.method1293()) {
            OriginUtil.Data2 var5 = method6921(var4);
            if (var5 != null && var5.getUUID().equals(var1)) {
               var2 = var4;
            }
         }

         return var2;
      }
   }

   public static boolean method6927(Storage var0, StoreApi.Data2 var1) {
      if (var0 == null) {
         return false;
      } else {
         OriginUtil.Data2 var2 = null;

         for (Script var4 : var0.method1293()) {
            OriginUtil.Data2 var5 = method6921(var4);
            if (var5 != null && var5.getUUID().equals(var1.getUUID())) {
               var2 = var5;
            }
         }

         return var2 != null && !var2.mine() && !var2.getText().equals(var1.getText6());
      }
   }

   public static Script method6928(Storage var0, StoreApi.Data2 var1, byte[] var2) throws IOException {
      if (!method6918(var1)) {
         throw new IOException("это скрипт старого формата");
      } else {
         Script var3 = method6926(var0, var1.getUUID());
         Path var4 = var0.getPath();
         Path var5 = var4.resolve(".incoming-" + UUID.randomUUID().toString().substring(0, 8));
         Files.createDirectories(var5);

         Script var40;
         try {
            method6931(var2, var5);
            if (!Files.isRegularFile(var5.resolve("main.js")) && !Files.isRegularFile(var5.resolve("src").resolve("main.js"))) {
               throw new IOException("в пакете нет main.js");
            }

            Path var6;
            if (var3 != null && var3.getPath() != null) {
               var6 = var3.getPath();
               var var30 = method6929(var6);

               try (Stream<java.nio.file.Path> var32 = Files.walk(var6)) {
                  for (Object var10_o : (Iterable<?>)(Object)(var32.sorted(Comparator.reverseOrder()).toList())) {
                     Path var10 = (Path)var10_o;
                     if (!var10.equals(var6)) {
                        String var11 = var10.getFileName().toString();
                        if (!var11.equals("storage.json") && !var11.startsWith(".")) {
                           if (Files.isDirectory(var10)) {
                              label230:
                              try (Stream<java.nio.file.Path> var12 = Files.list(var10)) {
                                 if (!var12.findAny().isPresent()) {
                                    break label230;
                                 }
                                 continue;
                              }

                              Files.deleteIfExists(var10);
                           } else {
                              String var42 = var6.relativize(var10).toString().replace('\\', '/');
                              boolean var13 = var30 != null
                                 ? var30.contains(var42)
                                 : var42.toLowerCase(Locale.ROOT).endsWith(".js") || var42.startsWith("lang/");
                              if (var13) {
                                 Files.deleteIfExists(var10);
                              }
                           }
                        }
                     }
                  }
               }
            } else {
               String var7 = Storage.method1303(var1.name().isBlank() ? "script" : var1.name());
               String var8 = var7;

               for (int var9 = 2; var0.method1294(var8) != null || Files.exists(var4.resolve(var8)); var9++) {
                  var8 = var7 + "_" + var9;
               }

               var6 = var4.resolve(var8);
               Files.createDirectories(var6);
            }

            ArrayList var31 = new ArrayList();

            try (Stream<java.nio.file.Path> var33 = Files.walk(var5)) {
               for (Object var39_o : (Iterable<?>)(Object)(var33.sorted().toList())) {
                  Path var39 = (Path)var39_o;
                  if (!var39.equals(var5)) {
                     Path var41 = var6.resolve(var5.relativize(var39).toString());
                     if (Files.isDirectory(var39)) {
                        Files.createDirectories(var41);
                     } else {
                        Files.move(var39, var41, StandardCopyOption.REPLACE_EXISTING);
                        var31.add(var5.relativize(var39).toString().replace('\\', '/'));
                     }
                  }
               }
            }

            method6930(var6, var31);
            method6923(var6, var1, var1.mine());
            var0.method1300();
            var var34 = var0.method1293().iterator();

            Script var38;
            do {
               if (!var34.hasNext()) {
                  return null;
               }

               var38 = (Script)var34.next();
            } while (!var6.equals(var38.getPath()));

            var40 = var38;
         } finally {
            method6932(var5);
         }

         return var40;
      }
   }

   private static Set<String> method6929(Path var0) {
      Path var1 = var0.resolve(".package.json");
      if (!Files.isRegularFile(var1)) {
         return null;
      } else {
         try {
            HashSet<String> var2 = new HashSet<>();

            for (JsonElement var4 : JsonParser.parseString(Files.readString(var1, StandardCharsets.UTF_8)).getAsJsonArray()) {
               var2.add(var4.getAsString());
            }

            return var2;
         } catch (Exception var5) {
            return null;
         }
      }
   }

   private static void method6930(Path var0, List<String> var1) throws IOException {
      JsonArray var2 = new JsonArray();
      var1.forEach(var2::add);
      Files.writeString(var0.resolve(".package.json"), var2.toString(), StandardCharsets.UTF_8);
   }

   public static void uninstall(Storage var0, Script var1) throws IOException {
      var0.method1357(var1);
   }

   private static void method6931(byte[] var0, Path var1) throws IOException {
      long var2 = 0L;
      int var4 = 0;
      Path var5 = var1.toAbsolutePath().normalize();

      try (ZipInputStream var6 = new ZipInputStream(new ByteArrayInputStream(var0))) {
         byte[] var8 = new byte[8192];

         ZipEntry var7;
         while ((var7 = var6.getNextEntry()) != null) {
            if (++var4 > 256) {
               throw new IOException("слишком много файлов в пакете");
            }

            String var9 = var7.getName().replace('\\', '/');
            if (var9.startsWith("/") || var9.contains("..") || var9.contains(":")) {
               throw new IOException("недопустимый путь: " + var9);
            }

            String var10 = var9.substring(var9.lastIndexOf(47) + 1);
            if (!var10.startsWith(".") && !var10.equals("storage.json")) {
               Path var11 = var5.resolve(var9).normalize();
               if (!var11.startsWith(var5)) {
                  throw new IOException("недопустимый путь: " + var9);
               }

               if (var7.isDirectory()) {
                  Files.createDirectories(var11);
               } else {
                  Files.createDirectories(var11.getParent());

                  int var13;
                  try (OutputStream var12 = Files.newOutputStream(var11)) {
                     while ((var13 = var6.read(var8)) > 0) {
                        var2 += var13;
                        if (var2 > 8388608L) {
                           throw new IOException("пакет слишком большой после распаковки");
                        }

                        var12.write(var8, 0, var13);
                     }
                  }
               }
            }
         }
      }
   }

   private static void method6932(Path var0) {
      if (Files.exists(var0)) {
         try (Stream<java.nio.file.Path> var1 = Files.walk(var0)) {
            for (Object var3_o : (Iterable<?>)(Object)(var1.sorted(Comparator.reverseOrder()).toList())) {
               Path var3 = (Path)var3_o;
               Files.deleteIfExists(var3);
            }
         } catch (IOException var6) {
         }
      }
   }

   public static CompletableFuture<StoreApi.Data2> method6933(StoreApi var0, Storage var1, Script var2, String var3) {
      if (var2.getPath() == null) {
         return CompletableFuture.failedFuture(new IOException("сначала переведите скрипт в папку: одиночные .js не публикуются"));
      } else {
         OriginUtil.Data2 var4 = method6921(var2);
         UUID var5 = var4 != null && var4.mine() ? var4.getUUID() : null;
         long var6 = var5 == null ? 0L : var4.getLongValue();
         String var8 = var2.method1403();
         String var9 = var2.getId() + ".zjs";
         String var10 = var2.getDescription();
         String var11 = method6914(var2);
         return CompletableFuture.<byte[][]>supplyAsync(() -> {
               try {
                  return new byte[][]{method6919(var2), method6920(var2)};
               } catch (IOException var2x) {
                  throw new CompletionException(var2x);
               }
            }, Util.getIoWorkerExecutor())
            .thenCompose(var10x -> var0.method1973("script", var8, var9, var10, var3, var10x[0], var5, var6, var11).thenCompose(var3xx -> {
               byte[] var4xx = var10x[1];

               try {
                  method6923(var2.getPath(), var3xx, true);
               } catch (IOException var6xx) {
               }

               if (var4xx != null) {
                  return var0.method1976(var3xx.getUUID(), var4xx).exceptionally(var1xxx -> var3xx);
               } else {
                  return var3xx.hasPreview() ? var0.method1977(var3xx.getUUID()).exceptionally(var1xxx -> var3xx) : CompletableFuture.completedFuture(var3xx);
               }
            }));
      }
   }

   private static final class Data {
      private final long longValue;
      private final long longValue2;
      private final OriginUtil.Data2 data2;

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public OriginUtil.Data2 data2() {
         return this.data2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.data2, other.data2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, longValue2, data2);
      }

      @Override
      public String toString() {
         return "Data[" + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + ", " + "data2=" + this.data2 + "]";
      }

      private Data(long var1, long var3, OriginUtil.Data2 var5) {
         this.longValue = var1;
         this.longValue2 = var3;
         this.data2 = var5;
      }

      public long checkedAt() {
         return this.longValue;
      }

      public long modified() {
         return this.longValue2;
      }

      public OriginUtil.Data2 getData2() {
         return this.data2;
      }
   }

   public static final class Data2 {
      private final UUID uUID;
      private final long longValue;
      private final String text;
      private final String text2;
      private final String text3;
      private final boolean flag;
      private final long longValue2;

      public UUID uUID() {
         return this.uUID;
      }

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      public boolean flag() {
         return this.flag;
      }

      public long longValue2() {
         return this.longValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.uUID, other.uUID) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.longValue2, other.longValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(uUID, longValue, text, text2, text3, flag, longValue2);
      }

      @Override
      public String toString() {
         return "Data2[" + "uUID=" + this.uUID + ", " + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "flag=" + this.flag + ", " + "longValue2=" + this.longValue2 + "]";
      }

      public Data2(UUID var1, long var2, String var4, String var5, String var6, boolean var7, long var8) {
         this.uUID = var1;
         this.longValue = var2;
         this.text = var4;
         this.text2 = var5;
         this.text3 = var6;
         this.flag = var7;
         this.longValue2 = var8;
      }

      public UUID getUUID() {
         return this.uUID;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String getText() {
         return this.text;
      }

      public String name() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }

      public boolean mine() {
         return this.flag;
      }

      public long getLongValue2() {
         return this.longValue2;
      }
   }

   public static final class Data3 {
      private final String text;
      private final List<String> items;
      private final int intValue;
      private final String text2;
      private final String text3;

      public String text() {
         return this.text;
      }

      public List<String> items() {
         return this.items;
      }

      public int intValue() {
         return this.intValue;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, items, intValue, text2, text3);
      }

      @Override
      public String toString() {
         return "Data3[" + "text=" + this.text + ", " + "items=" + this.items + ", " + "intValue=" + this.intValue + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + "]";
      }
      public static final OriginUtil.Data3 INSTANCE = new OriginUtil.Data3("", List.of(), -1, "", "");

      public Data3(String var1, List<String> var2, int var3, String var4, String var5) {
         this.text = var1;
         this.items = var2;
         this.intValue = var3;
         this.text2 = var4;
         this.text3 = var5;
      }

      public String version() {
         return this.text;
      }

      public List<String> getItems() {
         return this.items;
      }

      public int color() {
         return this.intValue;
      }

      public String author() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }
   }
}

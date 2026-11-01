package org.zenith.utility.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class CosmeticFileLoader {
   private final Map<String, byte[]> items;
   private final String text;
   private final String text2;
   private final String text3;
   private final CosmeticFileLoader.Type type;

   private CosmeticFileLoader(Map<String, byte[]> var1, String var2, String var3, String var4, CosmeticFileLoader.Type var5) {
      this.items = var1;
      this.text = var2;
      this.text2 = var3;
      this.text3 = var4;
      this.type = var5;
   }

   public CosmeticFileLoader.Type getType() {
      return this.type;
   }

   public String getText() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public String getText3() {
      return this.text3;
   }

   public byte[] method7338(String var1) {
      return var1 == null ? null : this.items.get(var1);
   }

   public String method7339(String var1) {
      byte[] var2 = this.method7338(var1);
      return var2 == null ? null : new String(var2, StandardCharsets.UTF_8);
   }

   public Map<String, byte[]> getItems() {
      return this.items;
   }

   public byte[] method7340(String var1) {
      if (var1 != null && !var1.isBlank()) {
         byte[] var2 = this.items.get(var1);
         if (var2 != null) {
            return var2;
         } else {
            String var3 = method7342(var1).toLowerCase(Locale.ROOT);

            for (Entry var5 : this.items.entrySet()) {
               if (method7342((String)var5.getKey()).toLowerCase(Locale.ROOT).equals(var3)) {
                  return (byte[])var5.getValue();
               }
            }

            return this.method7341();
         }
      } else {
         return this.method7341();
      }
   }

   public byte[] method7341() {
      for (Entry var2 : this.items.entrySet()) {
         if (method7343((String)var2.getKey())) {
            return (byte[])var2.getValue();
         }
      }

      return null;
   }

   private static String method7342(String var0) {
      int var1 = Math.max(var0.lastIndexOf(47), var0.lastIndexOf(92));
      return var1 < 0 ? var0 : var0.substring(var1 + 1);
   }

   private static boolean method7343(String var0) {
      return var0.toLowerCase(Locale.ROOT).endsWith(".png");
   }

   public static CosmeticFileLoader method7344(Path var0) throws IOException {
      CosmeticFileLoader var2;
      try (InputStream var1 = Files.newInputStream(var0)) {
         var2 = method7345(var1);
      }

      return var2;
   }

   public static CosmeticFileLoader method7345(InputStream var0) throws IOException {
      LinkedHashMap var1 = new LinkedHashMap();

      ZipEntry var3;
      try (ZipInputStream var2 = new ZipInputStream(var0)) {
         while ((var3 = var2.getNextEntry()) != null) {
            if (!var3.isDirectory()) {
               var1.put(var3.getName(), method7347(var2));
            }
         }
      }

      String var14 = null;
      String var15 = null;
      String var4 = null;
      String var5 = null;
      String var6 = null;

      for (Object var8_o : (Iterable<?>)(Object)(var1.entrySet())) {
         Entry var8 = (Entry)var8_o;
         String var9 = (String)var8.getKey();
         String var10 = var9.toLowerCase(Locale.ROOT);
         if (var10.endsWith(".obj")) {
            var14 = var9;
         } else if (var10.endsWith(".mtl")) {
            var6 = var9;
         } else if (var10.endsWith(".json")) {
            String var11 = method7346((byte[])var8.getValue());
            if (var11.contains("minecraft:geometry")) {
               var15 = var9;
            } else if (var11.contains("\"Techne\"")) {
               var4 = var9;
            } else if (var11.contains("\"animations\"")) {
               var5 = var9;
            }
         }
      }

      String var16 = var14 != null ? var14 : (var15 != null ? var15 : var4);
      CosmeticFileLoader.Type var17 = var14 != null
         ? CosmeticFileLoader.Type.WAVEFRONT
         : (var15 != null ? CosmeticFileLoader.Type.BEDROCK : (var4 != null ? CosmeticFileLoader.Type.TECHNE : CosmeticFileLoader.Type.EMPTY));
      return new CosmeticFileLoader(Collections.unmodifiableMap(var1), var16, var5, var6, var17);
   }

   private static String method7346(byte[] var0) {
      return new String(var0, 0, Math.min(var0.length, 512), StandardCharsets.UTF_8);
   }

   private static byte[] method7347(InputStream var0) throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream(8192);
      byte[] var2 = new byte[8192];

      int var3;
      while ((var3 = var0.read(var2)) > 0) {
         var1.write(var2, 0, var3);
      }

      return var1.toByteArray();
   }

   public static enum Type {
      WAVEFRONT,
      BEDROCK,
      TECHNE,
      EMPTY;
   }
}

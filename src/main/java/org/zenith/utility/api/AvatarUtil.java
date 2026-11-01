package org.zenith.utility.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.zenith.Zenith;
import org.zenith.utility.config.BuiltinCosmeticsPath;
import org.zenith.utility.config.FiguraManager;

public final class AvatarUtil {
   private static final String text = "avatar.json";
   private static final String text2 = "cosmetics";
   private static final int CONST_intValue = 8;
   private static final long CONST_longValue = 1000L;
   private static volatile List<AvatarUtil.Data> items = List.of();
   private static volatile long longValue;
   private static final Map<String, AvatarUtil.Type> items2 = new ConcurrentHashMap<>();
   private static final String text3 = "head-";
   private static final String text4 = "weapon-";
   private static final String[] texts = new String[]{
      "weapon",
      "sword",
      "greatsword",
      "blade",
      "scythe",
      "hammer",
      "halberd",
      "axe",
      "spear",
      "dagger",
      "katana",
      "trident",
      "staff",
      "bow",
      "оружие",
      "меч",
      "коса",
      "молот",
      "топор",
      "клинок",
      "копье",
      "кинжал",
      "катана",
      "посох"
   };

   private AvatarUtil() {
   }

   public static Path method6670() {
      try {
         Path var0 = Zenith.file.toPath().resolve("cosmetics");
         Files.createDirectories(var0);
         return var0;
      } catch (Exception var1) {
         return null;
      }
   }

   public static List<AvatarUtil.Data> method6671() {
      long var0 = System.currentTimeMillis();
      var var2 = items;
      if (var0 - longValue < 1000L) {
         return var2;
      } else {
         longValue = var0;
         items = method6677();
         return items;
      }
   }

   public static void invalidate() {
      longValue = 0L;
      items2.clear();
   }

   public static String method6672() {
      List<AvatarUtil.Data> var0 = method6671();
      StringBuilder var1 = new StringBuilder(var0.size() * 16);

      for (Object var3_o : (Iterable<?>)(Object)(var0)) {
         AvatarUtil.Data var3 = (AvatarUtil.Data)var3_o;
         var1.append(var3.getText2()).append('|');
      }

      return var1.toString();
   }

   public static List<AvatarUtil.Data> method6673(AvatarUtil.Type var0) {
      ArrayList<AvatarUtil.Data> var1 = new ArrayList<>();

      for (AvatarUtil.Data var3 : method6671()) {
         if (var3.getType() == var0) {
            var1.add(var3);
         }
      }

      return var1;
   }

   public static boolean method6674(AvatarUtil.Data var0) {
      FiguraManager var1 = method6676();
      if (var1 == null) {
         return false;
      } else {
         Path var2 = var0.getType() == AvatarUtil.Type.WEAPON ? var1.getPath3() : var1.getPath();
         return method6682(var2, var0.getPath());
      }
   }

   public static void method6675(AvatarUtil.Data var0) {
      FiguraManager var1 = method6676();
      if (var1 != null) {
         Path var2 = method6674(var0) ? null : var0.getPath();
         if (var0.getType() == AvatarUtil.Type.WEAPON) {
            var1.method7673(var2);
         } else {
            var1.method7672(var2);
         }
      }
   }

   private static FiguraManager method6676() {
      Zenith var0 = Zenith.getInstance();
      return var0 == null ? null : var0.getFiguraManager();
   }

   private static List<AvatarUtil.Data> method6677() {
      ArrayList var0 = new ArrayList();
      HashSet var1 = new HashSet();
      method6678(method6670(), var0, var1);
      method6678(BuiltinCosmeticsPath.method4447(), var0, var1);
      var0.sort(Comparator.comparing((AvatarUtil.Data var0x) -> var0x.getText2().toLowerCase(Locale.ROOT)));
      return List.copyOf(var0);
   }

   private static void method6678(Path var0, List<AvatarUtil.Data> var1, Set<String> var2) {
      if (var0 != null) {
         Path var3 = var0.toAbsolutePath().normalize();
         ArrayList<Path> var4 = new ArrayList();
         method6681(var3, var4, 0);

         for (Object var6_o : (Iterable<?>)(Object)(var4)) {
            Path var6 = (Path)var6_o;
            String var7 = var3.relativize(var6).toString().replace('\\', '/');
            if (var2.add(var7.toLowerCase(Locale.ROOT))) {
               Path var8 = var6.getFileName();
               String var9 = var8 == null ? var7 : var8.toString();
               var1.add(new AvatarUtil.Data(var9, var7, var6, method6679(var6, var7, var9)));
            }
         }
      }
   }

   private static AvatarUtil.Type method6679(Path var0, String var1, String var2) {
      String var3 = var0.toString();
      AvatarUtil.Type var4 = items2.get(var3);
      if (var4 != null) {
         return var4;
      } else {
         AvatarUtil.Type var5 = AvatarUtil.Type.method6683(var1, var2, method6680(var0));
         items2.put(var3, var5);
         return var5;
      }
   }

   private static String method6680(Path var0) {
      String var10000;
      try {
         String var1 = Files.readString(var0.resolve("avatar.json"), StandardCharsets.UTF_8);
         JsonElement var2 = JsonParser.parseString(var1);
         if (!var2.isJsonObject()) {
            return null;
         }

         JsonElement var3 = var2.getAsJsonObject().get("category");
         var10000 = var3 != null && !var3.isJsonNull() ? var3.getAsString() : null;
      } catch (Exception var4) {
         return null;
      }

      return var10000;
   }

   private static void method6681(Path var0, List<Path> var1, int var2) {
      if (var2 <= 8 && Files.isDirectory(var0) && !isHidden(var0)) {
         if (Files.isRegularFile(var0.resolve("avatar.json"))) {
            var1.add(var0.toAbsolutePath().normalize());
         } else {
            try (Stream<java.nio.file.Path> var3 = Files.list(var0)) {
               var3.filter(var0x -> Files.isDirectory(var0x)).forEach(var2x -> method6681(var2x, var1, var2 + 1));
            } catch (Exception var8) {
            }
         }
      }
   }

   private static boolean isHidden(Path var0) {
      Path var1 = var0.getFileName();
      return (var1 != null && var1.toString().startsWith("."));
   }

   private static boolean method6682(Path var0, Path var1) {
      if (var0 != null && var1 != null) {
         return var0.toAbsolutePath().normalize().equals(var1.toAbsolutePath().normalize());
      } else {
         return (var0 == var1);
      }
   }

   public static final class Data {
      private final String text;
      private final String text2;
      private final Path path;
      private final AvatarUtil.Type type;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public Path path() {
         return this.path;
      }

      public AvatarUtil.Type type() {
         return this.type;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.path, other.path) && java.util.Objects.equals(this.type, other.type);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, path, type);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "path=" + this.path + ", " + "type=" + this.type + "]";
      }

      public Data(String var1, String var2, Path var3, AvatarUtil.Type var4) {
         this.text = var1;
         this.text2 = var2;
         this.path = var3;
         this.type = var4;
      }

      public String name() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public Path getPath() {
         return this.path;
      }

      public AvatarUtil.Type getType() {
         return this.type;
      }
   }

   public static enum Type {
      MODEL,
      HAT,
      WEAPON;

      private static AvatarUtil.Type method6683(String var0, String var1, String var2) {
         if (method6684(var0, var1, "weapon-")) {
            return WEAPON;
         } else if (method6684(var0, var1, "head-")) {
            return HAT;
         } else if (method6685(var2)) {
            return WEAPON;
         } else {
            return method6686(var0 + " " + var1) ? WEAPON : MODEL;
         }
      }

      private static boolean method6684(String var0, String var1, String var2) {
         return var0.toLowerCase(Locale.ROOT).startsWith(var2) || var1.toLowerCase(Locale.ROOT).startsWith(var2);
      }

      private static boolean method6685(String var0) {
         if (var0 == null) {
            return false;
         } else {
            String var1 = var0.strip().toLowerCase(Locale.ROOT);
            return var1.equals("weapon") || var1.equals("weapons") || var1.equals("оружие");
         }
      }

      private static boolean method6686(String var0) {
         String var1 = var0.toLowerCase(Locale.ROOT);

         for (String var5 : AvatarUtil.texts) {
            if (var1.contains(var5)) {
               return true;
            }
         }

         return false;
      }
   }
}

package org.zenith.utility.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.zenith.Zenith;

public final class LyricsFetcher {
   private static final Pattern pattern = Pattern.compile("\\[(\\d{1,2}):(\\d{1,2})(?:[.:](\\d{1,3}))?]");
   private static final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6L)).build();
   private static final Map<String, LyricsFetcher.Data2> items = new ConcurrentHashMap<>();
   private static final Set<String> items2 = ConcurrentHashMap.newKeySet();
   private static volatile LyricsFetcher.Data data;
   private static final Pattern pattern2 = Pattern.compile("(?i)\\s*[(\\[](feat|ft|prod|explicit|remaster)[^)\\]]*[)\\]]");
   private static final String[] texts = new String[]{
      "а",
      "a",
      "б",
      "b",
      "в",
      "v",
      "г",
      "g",
      "д",
      "d",
      "е",
      "e",
      "ё",
      "e",
      "ж",
      "zh",
      "з",
      "z",
      "и",
      "i",
      "й",
      "y",
      "к",
      "k",
      "л",
      "l",
      "м",
      "m",
      "н",
      "n",
      "о",
      "o",
      "п",
      "p",
      "р",
      "r",
      "с",
      "s",
      "т",
      "t",
      "у",
      "u",
      "ф",
      "f",
      "х",
      "kh",
      "ц",
      "ts",
      "ч",
      "ch",
      "ш",
      "sh",
      "щ",
      "sch",
      "ъ",
      "",
      "ы",
      "y",
      "ь",
      "",
      "э",
      "e",
      "ю",
      "yu",
      "я",
      "ya"
   };

   private LyricsFetcher() {
   }

   public static LyricsFetcher.Data2 method528(String var0, String var1, double var2) {
      if (var0 != null && !var0.isBlank()) {
         String var4 = method530(var0, var1);
         LyricsFetcher.Data2 var5 = items.get(var4);
         if (var5 != null) {
            return var5;
         } else {
            if (items2.add(var4)) {
               Thread var6 = new Thread(() -> {
                  LyricsFetcher.Data2 var5x = method533(var4, var0, var1, var2);
                  items.put(var4, var5x);
                  items2.remove(var4);
               }, "zenith-lyrics");
               var6.setDaemon(true);
               var6.start();
            }

            return LyricsFetcher.Data2.INSTANCE2;
         }
      } else {
         return LyricsFetcher.Data2.INSTANCE;
      }
   }

   public static double method529(String var0, String var1) {
      if (var0 != null && !var0.isBlank()) {
         LyricsFetcher.Data2 var2 = items.get(method530(var0, var1));
         return var2 == null ? 0.0 : var2.getDoubleValue();
      } else {
         return 0.0;
      }
   }

   private static String method530(String var0, String var1) {
      LyricsFetcher.Data var2 = data;
      if (var2 != null && var2.title() == var0 && var2.getText2() == var1) {
         return var2.key();
      } else {
         String var3 = (method531(var1) + " - " + method532(var0)).toLowerCase(Locale.ROOT);
         data = new LyricsFetcher.Data(var0, var1, var3);
         return var3;
      }
   }

   public static String method531(String var0) {
      String var1 = var0 == null ? "" : var0;

      for (String var5 : new String[]{" — ", " – ", " - "}) {
         int var6 = var1.indexOf(var5);
         if (var6 > 0) {
            var1 = var1.substring(0, var6);
            break;
         }
      }

      return var1.trim();
   }

   public static String method532(String var0) {
      return pattern2.matcher(var0).replaceAll("").trim();
   }

   private static LyricsFetcher.Data2 method533(String var0, String var1, String var2, double var3) {
      Path var5 = null;

      try {
         Path var6 = Zenith.file.toPath().resolve("media").resolve("lyrics");
         Files.createDirectories(var6);
         var5 = var6.resolve(Integer.toHexString(var0.hashCode()) + ".json");
         if (Files.isRegularFile(var5)) {
            LyricsFetcher.Data2 var7 = method539(JsonParser.parseString(Files.readString(var5, StandardCharsets.UTF_8)).getAsJsonObject());
            if (var7 != null) {
               return var7;
            }
         }
      } catch (Throwable var16) {
      }

      String var18 = method532(var1);
      String var19 = method531(var2);
      JsonObject var8 = null;

      try {
         JsonElement var9 = method540(
            "https://lrclib.net/api/get?track_name=" + encode(var18) + "&artist_name=" + encode(var19) + (var3 > 0.0 ? "&duration=" + Math.round(var3) : "")
         );
         if (var9 != null && var9.isJsonObject() && var9.getAsJsonObject().has("id")) {
            var8 = var9.getAsJsonObject();
         }

         if (var8 == null || !method538(var8, "syncedLyrics")) {
            ArrayList<String> var10 = new ArrayList();
            var10.add("track_name=" + encode(var18) + "&artist_name=" + encode(var19));
            var10.add("q=" + encode(var19 + " " + var18));
            String var11 = method537(var19);
            if (!var11.equalsIgnoreCase(var19)) {
               var10.add("q=" + encode(var11 + " " + var18));
               var10.add("q=" + encode(var11 + " " + method537(var18)));
               var10.add("q=" + encode(var11));
            }

            var10.add("track_name=" + encode(var18));

            for (Object var13_o : (Iterable<?>)(Object)(var10)) {
               String var13 = (String)var13_o;
               JsonObject var14 = method534(method540("https://lrclib.net/api/search?" + var13), var18, var19, var3);
               if (var14 != null && (var8 == null || method538(var14, "syncedLyrics"))) {
                  var8 = var14;
                  if (method538(var14, "syncedLyrics")) {
                     break;
                  }
               }
            }
         }
      } catch (Throwable var17) {
      }

      if (var8 == null) {
         return LyricsFetcher.Data2.INSTANCE;
      } else {
         try {
            if (var5 != null) {
               Files.writeString(var5, var8.toString(), StandardCharsets.UTF_8);
            }
         } catch (Throwable var15) {
         }

         LyricsFetcher.Data2 var20 = method539(var8);
         return var20 == null ? LyricsFetcher.Data2.INSTANCE : var20;
      }
   }

   private static JsonObject method534(JsonElement var0, String var1, String var2, double var3) {
      if (var0 != null && var0.isJsonArray()) {
         String var5 = method536(var1);
         String var6 = method536(method537(var2));
         JsonObject var7 = null;
         double var8 = Double.MAX_VALUE;

         for (JsonElement var11 : var0.getAsJsonArray()) {
            if (var11.isJsonObject()) {
               JsonObject var12 = var11.getAsJsonObject();
               String var13 = method536(method535(var12, "trackName"));
               String var14 = method536(method537(method535(var12, "artistName")));
               boolean var15 = !var5.isEmpty() && (var13.equals(var5) || var13.contains(var5) || var5.contains(var13) && var13.length() > 3);
               if (var15) {
                  double var16 = var12.has("duration") && !var12.get("duration").isJsonNull() ? var12.get("duration").getAsDouble() : 0.0;
                  boolean var18 = !var6.isEmpty() && (var14.contains(var6) || var6.contains(var14) || var13.contains(var6));
                  boolean var19 = var3 > 0.0 && var16 > 0.0 && Math.abs(var16 - var3) <= 3.0;
                  if (var18 || var19) {
                     double var20 = (var3 > 0.0 && var16 > 0.0 ? Math.abs(var16 - var3) : 20.0)
                        - (method538(var12, "syncedLyrics") ? 1000 : 0)
                        - (var18 ? 50 : 0);
                     if (var20 < var8) {
                        var8 = var20;
                        var7 = var12;
                     }
                  }
               }
            }
         }

         return var7;
      } else {
         return null;
      }
   }

   private static String method535(JsonObject var0, String var1) {
      return var0.has(var1) && !var0.get(var1).isJsonNull() ? var0.get(var1).getAsString() : "";
   }

   private static String method536(String var0) {
      StringBuilder var1 = new StringBuilder();

      for (char var5 : method537(var0.toLowerCase(Locale.ROOT)).toCharArray()) {
         if (Character.isLetterOrDigit(var5)) {
            var1.append(var5);
         }
      }

      return var1.toString();
   }

   public static String method537(String var0) {
      StringBuilder var1 = new StringBuilder();

      for (char var5 : var0.toCharArray()) {
         char var6 = Character.toLowerCase(var5);
         String var7 = null;

         for (byte var8 = 0; var8 < texts.length; var8 += 2) {
            if (texts[var8].charAt(0) == var6) {
               var7 = texts[var8 + 1];
               break;
            }
         }

         if (var7 == null) {
            var1.append(var5);
         } else if (Character.isUpperCase(var5) && !var7.isEmpty()) {
            var1.append(Character.toUpperCase(var7.charAt(0))).append(var7.substring(1));
         } else {
            var1.append(var7);
         }
      }

      return var1.toString();
   }

   private static boolean method538(JsonObject var0, String var1) {
      return var0.has(var1) && !var0.get(var1).isJsonNull() && !var0.get(var1).getAsString().isBlank();
   }

   private static LyricsFetcher.Data2 method539(JsonObject var0) {
      boolean var1 = var0.has("instrumental") && !var0.get("instrumental").isJsonNull() && var0.get("instrumental").getAsBoolean();
      double var2 = var0.has("duration") && !var0.get("duration").isJsonNull() ? var0.get("duration").getAsDouble() : 0.0;
      if (method538(var0, "syncedLyrics")) {
         ArrayList<LyricsFetcher.Data3> var19 = new ArrayList<>();

         for (String var23 : var0.get("syncedLyrics").getAsString().split("\n")) {
            Matcher var9 = pattern.matcher(var23);
            ArrayList<Double> var10 = new ArrayList();

            int var11;
            for (var11 = 0; var9.find(); var11 = var9.end()) {
               double var12 = Double.parseDouble(var9.group(1));
               double var14 = Double.parseDouble(var9.group(2));
               String var16 = var9.group(3);
               double var17 = var16 == null ? 0.0 : Double.parseDouble(var16) / Math.pow(10.0, var16.length());
               var10.add(var12 * 60.0 + var14 + var17);
            }

            if (!var10.isEmpty()) {
               String var24 = var23.substring(var11).trim();

               for (Object var25_o : (Iterable<?>)(Object)(var10)) {
                  double var25 = (Double)var25_o;
                  var19.add(new LyricsFetcher.Data3(var25, var24));
               }
            }
         }

         var19.sort((var0x, var1x) -> Double.compare(var0x.time(), var1x.time()));
         return new LyricsFetcher.Data2(Collections.unmodifiableList(var19), true, var1, var2);
      } else if (!method538(var0, "plainLyrics")) {
         return var1 ? new LyricsFetcher.Data2(List.of(), false, true, var2) : null;
      } else {
         ArrayList var4 = new ArrayList();

         for (String var8 : var0.get("plainLyrics").getAsString().split("\n")) {
            var4.add(new LyricsFetcher.Data3(-1.0, var8.trim()));
         }

         return new LyricsFetcher.Data2(Collections.unmodifiableList(var4), false, var1, var2);
      }
   }

   private static JsonElement method540(String var0) throws Exception {
      HttpRequest var1 = HttpRequest.newBuilder(URI.create(var0))
         .timeout(Duration.ofSeconds(8L))
         .header("User-Agent", "ZenithClient/3.5 (https://zenithdlc.com)")
         .GET()
         .build();
      HttpResponse var2 = httpClient.send(var1, BodyHandlers.ofString(StandardCharsets.UTF_8));
      return var2.statusCode() != 200 ? null : JsonParser.parseString((String)var2.body());
   }

   private static String encode(String var0) {
      return URLEncoder.encode(var0, StandardCharsets.UTF_8);
   }

   private static final class Data {
      private final String text;
      private final String text2;
      private final String text3;

      public String text() {
         return this.text;
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
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, text3);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + "]";
      }

      private Data(String var1, String var2, String var3) {
         this.text = var1;
         this.text2 = var2;
         this.text3 = var3;
      }

      public String title() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public String key() {
         return this.text3;
      }
   }

   public static final class Data2 {
      private final List<LyricsFetcher.Data3> items;
      private final boolean flag;
      private final boolean flag2;
      private final double doubleValue;

      public List<LyricsFetcher.Data3> items() {
         return this.items;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.doubleValue, other.doubleValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(items, flag, flag2, doubleValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "items=" + this.items + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "doubleValue=" + this.doubleValue + "]";
      }
      public static final LyricsFetcher.Data2 INSTANCE = new LyricsFetcher.Data2(List.of(), false, false, 0.0);
      public static final LyricsFetcher.Data2 INSTANCE2 = new LyricsFetcher.Data2(List.of(), false, false, 0.0);

      public Data2(List<LyricsFetcher.Data3> var1, boolean var2, boolean var3, double var4) {
         this.items = var1;
         this.flag = var2;
         this.flag2 = var3;
         this.doubleValue = var4;
      }

      public int method541(double var1) {
         if (this.flag && !this.items.isEmpty()) {
            int var3 = -1;
            int var4 = 0;

            while (var4 < this.items.size() && this.items.get(var4).time() <= var1 + 0.15) {
               var3 = var4++;
            }

            return var3;
         } else {
            return -1;
         }
      }

      public List<LyricsFetcher.Data3> lines() {
         return this.items;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }
   }

   public static final class Data3 {
      private final double doubleValue;
      private final String text;

      public double doubleValue() {
         return this.doubleValue;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(doubleValue, text);
      }

      @Override
      public String toString() {
         return "Data3[" + "doubleValue=" + this.doubleValue + ", " + "text=" + this.text + "]";
      }

      public Data3(double var1, String var3) {
         this.doubleValue = var1;
         this.text = var3;
      }

      public double time() {
         return this.doubleValue;
      }
   }
}

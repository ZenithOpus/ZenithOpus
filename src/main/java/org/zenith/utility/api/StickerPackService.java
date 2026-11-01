package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.utility.misc.Coord;

public final class StickerPackService {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Site");
   private static final long CONST_longValue = 60000L;
   private static final StickerPackService INSTANCE = new StickerPackService();
   private static final long CONST_longValue2 = 3000L;
   private static final Pattern pattern = Pattern.compile("_a([0-9]{1,3})x([0-9]{1,2})$");
   private static final Pattern pattern2 = Pattern.compile("[a-z0-9_-]{1,64}");
   private static final Object obj = new Object();
   private static final Map<String, Object> items = new ConcurrentHashMap<>();
   private static final int CONST_intValue = 1024;
   private final List<StickerPackService.Data> items2 = new CopyOnWriteArrayList<>();
   private volatile String text = "";
   private volatile boolean loading;
   private volatile long longValue;
   private volatile int revision;
   private volatile StickerPackService.Data2 data2 = new StickerPackService.Data2("", new ConcurrentHashMap<>(), new AtomicInteger());

   public static boolean method5503(String var0) {
      return var0 != null && pattern2.matcher(var0).matches();
   }

   public static StickerPackService.Data3 method5504(String var0) {
      if (var0 == null) {
         return null;
      } else {
         Object var1 = items.get(var0);
         if (var1 == null) {
            if (items.size() >= 1024) {
               items.clear();
            }

            StickerPackService.Data3 var2 = method5505(var0);
            var1 = var2 == null ? obj : var2;
            items.put(var0, var1);
         }

         return var1 == obj ? null : (StickerPackService.Data3)var1;
      }
   }

   private static StickerPackService.Data3 method5505(String var0) {
      Matcher var1 = pattern.matcher(var0);
      if (!var1.find()) {
         return null;
      } else {
         int var2 = Integer.parseInt(var1.group(1));
         int var3 = Integer.parseInt(var1.group(2));
         return var2 >= 2 && var3 >= 1 && var3 <= var2 ? new StickerPackService.Data3(var2, var3, (var2 + var3 - 1) / var3) : null;
      }
   }

   private StickerPackService() {
   }

   public static StickerPackService getInstance() {
      return INSTANCE;
   }

   public List<StickerPackService.Data> getItems2() {
      return this.items2;
   }

   public boolean method5506() {
      return !this.items2.isEmpty();
   }

   public int revision() {
      return this.revision;
   }

   public String method5507(String var1, String var2) {
      String var3 = this.text;
      if (!var3.isEmpty() && var1 != null && var2 != null) {
         StickerPackService.Data2 var4 = this.data2;
         if (var4.getText() != var3 || var4.getAtomicInteger().get() >= 4096) {
            var4 = new StickerPackService.Data2(var3, new ConcurrentHashMap<>(), new AtomicInteger());
            this.data2 = var4;
         }

         var var5 = var4.getItems().get(var1);
         String var6 = var5 == null ? null : (String)var5.get(var2);
         if (var6 == null) {
            if (!method5503(var1) || !method5503(var2)) {
               return "";
            }

            if (var5 == null) {
               var5 = var4.getItems().computeIfAbsent(var1, var0 -> new ConcurrentHashMap<>());
            }

            var6 = var3 + var1 + "/" + var2 + ".png";
            if (var5.putIfAbsent(var2, var6) == null) {
               var4.getAtomicInteger().incrementAndGet();
            }
         }

         return var6;
      } else {
         return "";
      }
   }

   public void method5508() {
      if (!this.loading && this.items2.isEmpty()) {
         if (System.currentTimeMillis() - this.longValue >= 60000L) {
            this.loading = true;
            this.longValue = System.currentTimeMillis();
            SiteHttpClient.method2412("/chat/stickers", new JsonObject())
               .whenComplete(
                  (var1, var2) -> {
                     this.loading = false;
                     if (var2 == null) {
                        this.text = method5509(SiteHttpClient.method2417(var1, "base", ""));
                        ArrayList var3 = new ArrayList();
                        JsonArray var4 = SiteHttpClient.method2419(var1, "packs");
                        if (var4 != null) {
                           for (JsonElement var6 : var4) {
                              if (var6.isJsonObject()) {
                                 JsonObject var7 = var6.getAsJsonObject();
                                 String var8 = SiteHttpClient.method2417(var7, "id", "");
                                 if (method5503(var8)) {
                                    ArrayList var9 = new ArrayList();
                                    JsonArray var10 = SiteHttpClient.method2419(var7, "items");
                                    if (var10 != null) {
                                       for (JsonElement var12 : var10) {
                                          if (var12.isJsonPrimitive() && method5503(var12.getAsString())) {
                                             var9.add(var12.getAsString());
                                          }
                                       }
                                    }

                                    if (!var9.isEmpty()) {
                                       String var13 = SiteHttpClient.method2417(var7, "cover", (String)var9.get(0));
                                       var3.add(
                                          new StickerPackService.Data(
                                             var8,
                                             SiteHttpClient.method2417(var7, "name", ""),
                                             method5503(var13) ? var13 : (String)var9.get(0),
                                             List.copyOf(var9)
                                          )
                                       );
                                    }
                                 }
                              }
                           }
                        }

                        this.items2.clear();
                        this.items2.addAll(var3);
                        this.revision++;
                     }
                  }
               );
         }
      }
   }

   private static String method5509(String var0) {
      if (var0 != null && !var0.isBlank()) {
         try {
            URI var1 = URI.create(var0.strip());
            if ("https".equalsIgnoreCase(var1.getScheme())
               && var1.getRawUserInfo() == null
               && var1.getRawQuery() == null
               && var1.getRawFragment() == null
               && RegionUtil.method2044(var1.getHost())) {
               String var2 = var1.toString();
               return var2.endsWith("/") ? var2 : var2 + "/";
            } else {
               logger.warn("Стикеры: корень адресов с сайта отвергнут: {}", var0.length() > 200 ? var0.substring(0, 200) + "…" : var0);
               return "";
            }
         } catch (RuntimeException var3) {
            return "";
         }
      } else {
         return "";
      }
   }

   public static String method5510(String var0, String var1) {
      return Coord.method5171("st").method5177("p", var0).method5177("i", var1).toString();
   }

   public static final class Data {
      private final String text;
      private final String text2;
      private final String text3;
      private final List<String> items;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      public List<String> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, text3, items);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "items=" + this.items + "]";
      }

      public Data(String var1, String var2, String var3, List<String> var4) {
         this.text = var1;
         this.text2 = var2;
         this.text3 = var3;
         this.items = var4;
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

      public List<String> getItems() {
         return this.items;
      }
   }

   private static final class Data2 {
      private final String text;
      private final Map<String, Map<String, String>> items;
      private final AtomicInteger atomicInteger;

      public String text() {
         return this.text;
      }

      public Map<String, Map<String, String>> items() {
         return this.items;
      }

      public AtomicInteger atomicInteger() {
         return this.atomicInteger;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.atomicInteger, other.atomicInteger);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, items, atomicInteger);
      }

      @Override
      public String toString() {
         return "Data2[" + "text=" + this.text + ", " + "items=" + this.items + ", " + "atomicInteger=" + this.atomicInteger + "]";
      }

      private Data2(String var1, Map<String, Map<String, String>> var2, AtomicInteger var3) {
         this.text = var1;
         this.items = var2;
         this.atomicInteger = var3;
      }

      public String getText() {
         return this.text;
      }

      public Map<String, Map<String, String>> getItems() {
         return this.items;
      }

      public AtomicInteger getAtomicInteger() {
         return this.atomicInteger;
      }
   }

   public static final class Data3 {
      private final int intValue;
      private final int columns;
      private final int intValue2;

      public int intValue() {
         return this.intValue;
      }

      public int columns() {
         return this.columns;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.columns, other.columns) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, columns, intValue2);
      }

      @Override
      public String toString() {
         return "Data3[" + "intValue=" + this.intValue + ", " + "columns=" + this.columns + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data3(int var1, int var2, int var3) {
         this.intValue = var1;
         this.columns = var2;
         this.intValue2 = var3;
      }

      public int method5511(long var1) {
         long var3 = Math.max(16L, 3000L / Math.max(1, this.intValue));
         return Math.floorMod(var1 / var3, this.intValue);
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int rows() {
         return this.intValue2;
      }
   }
}

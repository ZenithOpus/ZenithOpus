package org.zenith.utility.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.Util;

public final class Prefs {
   private static final String text = "nexus-friends-prefs.json";
   private final Path path;
   private final Map<String, Prefs.Entry> items = new ConcurrentHashMap<>();
   private volatile boolean dirty;
   private volatile boolean flag;
   private final Object obj = new Object();
   private long longValue;
   private long longValue2;

   public Prefs(Path var1) {
      this.path = var1.resolve("nexus-friends-prefs.json");
      this.load();
   }

   private Prefs.Entry method3305(String var1) {
      return this.items.computeIfAbsent(key(var1), var0 -> new Prefs.Entry());
   }

   private static String key(String var0) {
      return var0 == null ? "" : var0.toLowerCase(Locale.ROOT);
   }

   public void method3306(String var1) {
      Prefs.Entry var2 = this.method3305(var1);
      if (var2.longValue2 == 0L) {
         var2.longValue2 = System.currentTimeMillis();
      }

      var2.longValue = System.currentTimeMillis();
   }

   public void method3307(String var1) {
      Prefs.Entry var2 = this.method3305(var1);
      var2.longValue = System.currentTimeMillis();
      var2.longValue2 = 0L;
   }

   public void method3308() {
      for (Prefs.Entry var2 : this.items.values()) {
         var2.longValue2 = 0L;
      }
   }

   public long method3309(String var1) {
      Prefs.Entry var2 = this.items.get(key(var1));
      return var2 != null && var2.longValue2 != 0L ? System.currentTimeMillis() - var2.longValue2 : 0L;
   }

   public long method3310(String var1) {
      Prefs.Entry var2 = this.items.get(key(var1));
      return var2 == null ? 0L : var2.longValue;
   }

   public long method3311(String var1) {
      Prefs.Entry var2 = this.items.get(key(var1));
      return var2 == null ? 0L : var2.longValue3;
   }

   public void method3312(String var1, long var2) {
      if (var2 > 0L) {
         Prefs.Entry var4 = this.method3305(var1);
         if (var4.longValue3 != var2) {
            var4.longValue3 = var2;
            this.save();
         }
      }
   }

   public String method3313(String var1) {
      Prefs.Entry var2 = this.items.get(key(var1));
      return var2 != null && var2.text != null ? var2.text : "";
   }

   public void method3314(String var1, String var2) {
      if (var2 != null && !var2.isBlank()) {
         Prefs.Entry var3 = this.method3305(var1);
         if (!var2.equals(var3.text)) {
            var3.text = var2;
            this.save();
         }
      }
   }

   public boolean method3315(String var1) {
      Prefs.Entry var2 = this.items.get(key(var1));
      return var2 != null && var2.flag;
   }

   public boolean method3316(String var1) {
      Prefs.Entry var2 = this.items.get(key(var1));
      return var2 != null && var2.flag2;
   }

   public void method3317(String var1, boolean var2) {
      this.method3305(var1).flag = var2;
      this.save();
   }

   public void method3318(String var1, boolean var2) {
      Prefs.Entry var3 = this.method3305(var1);
      var3.flag2 = var2;
      if (var2) {
         var3.flag = true;
      }

      this.save();
   }

   public boolean method3319(String var1) {
      Prefs.Entry var2 = this.items.get(key(var1));
      return var2 != null && var2.flag4;
   }

   public void method3320(String var1, boolean var2) {
      this.method3305(var1).flag4 = var2;
      this.save();
   }

   public boolean method3321(String var1) {
      Prefs.Entry var2 = this.items.get(key(var1));
      return var2 != null && (var2.flag3 || var2.flag2);
   }

   public void method3322(String var1, boolean var2) {
      this.method3305(var1).flag3 = var2;
      this.save();
   }

   public int method3323(String var1) {
      Prefs.Entry var2 = this.items.get(key(var1));
      return var2 == null ? 100 : var2.intValue;
   }

   public void method3324(String var1, int var2) {
      int var3 = Math.max(0, Math.min(200, var2));
      Prefs.Entry var4 = this.method3305(var1);
      if (var4.intValue != var3) {
         var4.intValue = var3;
         this.save();
      }
   }

   public void save() {
      this.dirty = true;
   }

   public void flush() {
      if (this.dirty && !this.flag) {
         this.dirty = false;
         this.flag = true;
         long var1 = ++this.longValue;
         JsonObject var3 = this.method3327();
         Util.getIoWorkerExecutor().execute(() -> {
            try {
               this.method3326(var1, var3);
            } finally {
               this.flag = false;
            }
         });
      }
   }

   public void method3325() {
      this.dirty = false;
      this.method3326(++this.longValue, this.method3327());
   }

   private void method3326(long var1, JsonObject var3) {
      synchronized (this.obj) {
         if (var1 > this.longValue2) {
            if (CloudJsonIo.method5993(this.path, var3)) {
               this.longValue2 = var1;
            }
         }
      }
   }

   private JsonObject method3327() {
      JsonObject var1 = new JsonObject();
      this.items
         .forEach(
            (var1x, var2) -> {
               if (var2.longValue != 0L
                  || var2.flag
                  || var2.flag2
                  || var2.longValue3 != 0L
                  || var2.text != null && !var2.text.isEmpty()
                  || var2.flag3
                  || var2.intValue != 100
                  || var2.flag4) {
                  JsonObject var3 = new JsonObject();
                  var3.addProperty("lastSeen", var2.longValue);
                  if (var2.longValue3 > 0L) {
                     var3.addProperty("uid", var2.longValue3);
                  }

                  if (var2.text != null && !var2.text.isEmpty()) {
                     var3.addProperty("mcName", var2.text);
                  }

                  if (var2.flag) {
                     var3.addProperty("muted", true);
                  }

                  if (var2.flag2) {
                     var3.addProperty("blocked", true);
                  }

                  if (var2.flag3) {
                     var3.addProperty("voiceMuted", true);
                  }

                  if (var2.intValue != 100) {
                     var3.addProperty("voiceVolume", var2.intValue);
                  }

                  if (var2.flag4) {
                     var3.addProperty("favorite", true);
                  }

                  var1.add(var1x, var3);
               }
            }
         );
      return var1;
   }

   private void load() {
      JsonObject var1 = CloudJsonIo.method5992(this.path);
      if (var1 != null) {
         var1.entrySet().forEach(var1x -> {
            if (((JsonElement)var1x.getValue()).isJsonObject()) {
               JsonObject var2 = ((JsonElement)var1x.getValue()).getAsJsonObject();

               try {
                  this.items.put((String)var1x.getKey(), method3328(var2));
               } catch (RuntimeException var4) {
               }
            }
         });
      }
   }

   private static Prefs.Entry method3328(JsonObject var0) {
      Prefs.Entry var1 = new Prefs.Entry();
      var1.longValue = var0.has("lastSeen") ? var0.get("lastSeen").getAsLong() : 0L;
      var1.flag = var0.has("muted") && var0.get("muted").getAsBoolean();
      var1.flag2 = var0.has("blocked") && var0.get("blocked").getAsBoolean();
      var1.longValue3 = var0.has("uid") ? var0.get("uid").getAsLong() : 0L;
      var1.text = var0.has("mcName") ? var0.get("mcName").getAsString() : "";
      var1.flag3 = var0.has("voiceMuted") && var0.get("voiceMuted").getAsBoolean();
      var1.intValue = var0.has("voiceVolume") ? Math.max(0, Math.min(200, var0.get("voiceVolume").getAsInt())) : 100;
      var1.flag4 = var0.has("favorite") && var0.get("favorite").getAsBoolean();
      return var1;
   }

   public static final class Entry {
      public long longValue;
      public long longValue2;
      public boolean flag;
      public volatile boolean flag2;
      public long longValue3;
      public String text = "";
      public volatile boolean flag3;
      public volatile int intValue = 100;
      public boolean flag4;
   }
}

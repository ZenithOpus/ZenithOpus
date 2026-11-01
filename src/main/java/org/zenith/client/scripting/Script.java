package org.zenith.client.scripting;

import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.client.settings.Setting;
import org.zenith.utility.api.ScriptStorageFile;

public final class Script {
   private final Storage storage;
   private final String text;
   private final Path path;
   private final Path path2;
   public String name;
   public String text2 = "";
   public String version = "";
   public String description = "";
   public String text3 = "";
   public String text4 = "";
   public String text5 = "";
   public String text6 = "";
   public volatile boolean flag;
   public int intValue;
   public long longValue;
   public long longValue2;
   public long longValue3;
   public int intValue2;
   public final Map<String, Object> items = new LinkedHashMap<>();
   public volatile long longValue4;
   public volatile long longValue5;
   public volatile long longValue6;
   public volatile long longValue7 = System.currentTimeMillis();
   public int color = -1;
   public int intValue3 = 3;
   public Script.Type2 type2 = Script.Type2.LOADED;
   public String error = "";
   public int intValue4;
   public long longValue8;
   public long longValue9;
   public String source = "";
   public ScriptableObject scope;
   public final List<AutoCloseable> items2 = new ArrayList<>();
   public final Map<String, Module1> items3 = new LinkedHashMap<>();
   public final Map<Script.Type, List<Function>> items4 = new EnumMap<>(Script.Type.class);
   public final List<Storage.Entry> items5 = new ArrayList<>();
   public final Map<String, Setting> items6 = new LinkedHashMap<>();
   public JsonObject jsonObject = new JsonObject();
   public ScriptStorageFile scriptStorageFile;
   public final Map<String, Function> items7 = new LinkedHashMap<>();
   public final List<Function> items8 = new ArrayList<>();
   private static final Set<String> items9 = Set.of(
      "co.uk",
      "org.uk",
      "ac.uk",
      "gov.uk",
      "com.ru",
      "net.ru",
      "org.ru",
      "spb.ru",
      "msk.ru",
      "com.ua",
      "org.ua",
      "com.br",
      "com.au",
      "net.au",
      "co.jp",
      "co.kr",
      "com.cn",
      "com.tr",
      "com.pl",
      "co.in",
      "github.io",
      "gitlab.io",
      "vercel.app",
      "netlify.app",
      "herokuapp.com",
      "pages.dev",
      "workers.dev",
      "web.app",
      "firebaseapp.com",
      "glitch.me",
      "ngrok.io",
      "ngrok-free.app",
      "duckdns.org",
      "no-ip.org",
      "repl.co",
      "blogspot.com",
      "appspot.com",
      "azurewebsites.net",
      "cloudfront.net",
      "amazonaws.com"
   );
   public static final Pattern pattern = Pattern.compile("[,\\s]+");
   private static final Pattern pattern2 = Pattern.compile("[a-z0-9.-]+");
   private static final Pattern pattern3 = Pattern.compile("[a-z]{2,24}|xn--[a-z0-9-]{2,59}");
   private volatile Script.Data data;

   public Script(Storage var1, String var2, Path var3, Path var4) {
      this.storage = var1;
      this.text = var2;
      this.path = var3;
      this.path2 = var4;
      this.name = var2;
   }

   public Storage getStorage() {
      return this.storage;
   }

   public String getId() {
      return this.text;
   }

   public Path getPath() {
      return this.path;
   }

   public Path getPath2() {
      return this.path2;
   }

   public String method1403() {
      return this.name != null && !this.name.isBlank() ? this.name : this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public String getVersion() {
      return this.version;
   }

   public String getDescription() {
      return this.description;
   }

   public String getText3() {
      return this.text3;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public String getText6() {
      return this.text6;
   }

   public Map<String, Object> getItems() {
      return this.items;
   }

   public static boolean method1404(String var0) {
      if (var0.length() <= 253 && pattern2.matcher(var0).matches() && !var0.startsWith(".") && !var0.endsWith(".") && !var0.contains("..")) {
         String[] var1 = var0.split("\\.");
         if (var1.length < 2) {
            return false;
         } else {
            return !pattern3.matcher(var1[var1.length - 1]).matches() ? false : !items9.contains(var0);
         }
      } else {
         return false;
      }
   }

   private List<String> method1405() {
      String var1 = this.text5;
      Script.Data var2 = this.data;
      if (var2 == null || var2.source() != var1) {
         ArrayList var3 = new ArrayList();

         for (String var7 : pattern.split(var1)) {
            String var8 = var7.strip().toLowerCase(Locale.ROOT);
            if (var8.startsWith("*.")) {
               var8 = var8.substring(2);
            }

            if (method1404(var8)) {
               var3.add(var8);
            }
         }

         var2 = new Script.Data(var1, List.copyOf(var3));
         this.data = var2;
      }

      return var2.getItems();
   }

   public boolean method1406() {
      return !this.method1405().isEmpty();
   }

   public List<String> method1407() {
      return new ArrayList<>(this.method1405());
   }

   public List<String> method1408() {
      ArrayList<String> var1 = new ArrayList<>();

      for (String var5 : pattern.split(this.text5)) {
         String var6 = var5.strip().toLowerCase(Locale.ROOT);
         if (var6.startsWith("*.")) {
            var6 = var6.substring(2);
         }

         if (!var6.isEmpty() && !method1404(var6)) {
            var1.add(var5.strip());
         }
      }

      return var1;
   }

   public long[] method1409() {
      return new long[]{this.longValue4, this.longValue5, this.longValue6, this.longValue7};
   }

   public void method1410() {
      this.longValue4 = 0L;
      this.longValue5 = 0L;
      this.longValue6 = 0L;
      this.longValue7 = System.currentTimeMillis();
   }

   public ScriptableObject getScope() {
      return this.scope;
   }

   public Scriptable method1411() {
      if (this.scope == null) {
         return null;
      } else {
         return this.scope.get("exports", this.scope) instanceof Scriptable var2 ? var2 : null;
      }
   }

   public String getText4() {
      return this.text4;
   }

   public int getColor() {
      return this.color;
   }

   public Path method1412() {
      return this.path == null ? null : this.path.resolve("icon.png");
   }

   public Script.Type2 getType2() {
      return this.type2;
   }

   public String getError() {
      return this.error;
   }

   public boolean isRunning() {
      return this.type2 == Script.Type2.RUNNING;
   }

   public Map<String, Module1> getItems3() {
      return this.items3;
   }

   public <T extends AutoCloseable> T method1413(T var1) {
      this.items2.add(var1);
      return (T)var1;
   }

   public void method1414(Script.Type var1, Function var2) {
      this.method1416(var1, var2);
   }

   public Map<String, Setting> getItems6() {
      return this.items6;
   }

   public Map<String, Module1> getItems32() {
      return this.items3;
   }

   public Map<String, Function> getItems7() {
      return this.items7;
   }

   public ScriptStorageFile method1415() {
      if (this.scriptStorageFile == null) {
         throw ScriptValueUtils.method6251("хранилище недоступно");
      } else {
         return this.scriptStorageFile;
      }
   }

   public void method1416(Script.Type var1, Function var2) {
      this.items4.computeIfAbsent(var1, var0 -> new ArrayList<>()).add(var2);
   }

   public List<Function> method1417(Script.Type var1) {
      var var2 = this.items4.get(var1);
      return var2 == null ? List.of() : var2;
   }

   private static final class Data {
      private final String text;
      private final List<String> items;

      public String text() {
         return this.text;
      }

      public List<String> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, items);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "items=" + this.items + "]";
      }

      private Data(String var1, List<String> var2) {
         this.text = var1;
         this.items = var2;
      }

      public String source() {
         return this.text;
      }

      public List<String> getItems() {
         return this.items;
      }
   }

   public static enum Type {
      ENABLE,
      DISABLE,
      WORLD_CHANGE,
      HEALTH_CHANGE,
      DEATH,
      SERVER_JOIN,
      SERVER_LEAVE,
      SCREEN,
      CHAT_COMMAND,
      TITLE,
      ACTIONBAR,
      BOSSBAR,
      SOUND_RECEIVED,
      ITEM_PICKUP,
      BLOCK_UPDATE,
      CONTAINER_OPEN;
   }

   public static enum Type2 {
      LOADED,
      RUNNING,
      STOPPED,
      FAILED;
   }
}

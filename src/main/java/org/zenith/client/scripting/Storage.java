package org.zenith.client.scripting;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.ChatSendEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.settings.Setting;
import org.zenith.utility.api.AiChatStorage;
import org.zenith.utility.api.AiSettingsStore;
import org.zenith.utility.api.OriginUtil;
import org.zenith.utility.api.ScriptStorageFile;
import org.zenith.utility.api.Server;
import org.zenith.utility.config.ScriptApiDocs;
import org.zenith.utility.misc.ImageLoader;
import org.zenith.utility.misc.ScriptHeaderParser;

public final class Storage {
   public static final int CONST_intValue = 3;
   public static final int CONST_intValue2 = 1048576;
   public static final int CONST_intValue3 = 5;
   private static final int CONST_intValue4 = 1000;
   private static final Logger logger = LogUtils.getLogger();
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   private final Path path;
   private final Path path2;
   private final Map<String, Script> items = new LinkedHashMap<>();
   private final Deque<Storage.Data2> deque = new ArrayDeque<>();
   private JsonObject jsonObject = new JsonObject();
   private boolean flag;
   private int ticks;
   private int intValue;
   private boolean flag2;
   private final List<Module> items2 = new ArrayList<>();
   private final ScriptEventWatcher scriptEventWatcher = new ScriptEventWatcher(this);
   private final Map<String, Map<String, JsonElement>> items3 = new HashMap<>();
   private static final long CONST_longValue = 350000000L;
   private static final int CONST_intValue5 = 20;
   private final Map<String, long[]> items4 = new HashMap<>();
   private long longValue;
   private Script script;
   private Map<String, List<String>> items5;
   private static final Pattern pattern = Pattern.compile("\\(([^():]+):(\\d+)\\)");
   public static final List<String> items6 = List.of("name", "author", "version", "description", "tags", "link", "color", "api");
   private static final Storage.Data data = new Storage.Data(-1L, -1L, List.of());
   private final Map<Path, Storage.Data> items7 = new ConcurrentHashMap<>();

   public Storage() {
      this.path = Zenith.file.toPath().resolve("scripts");
      this.path2 = this.path.resolve(".state.json");

      try {
         Files.createDirectories(this.path);
         Files.createDirectories(this.path.resolve(".data"));
      } catch (IOException var2) {
         logger.error("Не удалось создать папку скриптов", var2);
      }

      this.method1338();
      ScriptApiDocs.method4768(this.path);
      EventManager.register(this);
      this.method1301(true);
      Server.getInstance().method217();
      Thread.ofPlatform().daemon(true).name("zenith-ai-preload").start(() -> {
         AiSettingsStore.method4467();
         AiChatStorage.method2741();
      });
   }

   public void method1292(String var1) {
      if (var1.length() > 1048576) {
         throw new IllegalArgumentException("код больше 1 МБ");
      } else {
         method1309(var1, "check.js");
      }
   }

   public Path getPath() {
      return this.path;
   }

   public Collection<Script> method1293() {
      return this.items.values();
   }

   public Script method1294(String var1) {
      if (var1 == null) {
         return null;
      } else {
         Script var2 = this.items.get(var1);
         if (var2 != null) {
            return var2;
         } else {
            for (Script var4 : this.items.values()) {
               if (var4.getId().equalsIgnoreCase(var1) || var4.method1403().equalsIgnoreCase(var1)) {
                  return var4;
               }
            }

            return null;
         }
      }
   }

   public List<Storage.Data2> method1295() {
      synchronized (this.deque) {
         return new ArrayList<>(this.deque);
      }
   }

   public void method1296() {
      synchronized (this.deque) {
         this.deque.clear();
         this.longValue++;
      }
   }

   public boolean method1297(Script var1) {
      JsonObject var2 = this.method1337(var1.getId());
      return var2.has("enabled") && var2.get("enabled").getAsBoolean();
   }

   public void method1298(Script var1, boolean var2) {
      this.method1337(var1.getId()).addProperty("enabled", var2);
      this.flag = true;
      if (var2) {
         if (!var1.isRunning()) {
            this.method1308(var1, null);
         }
      } else if (var1.type2 != Script.Type2.STOPPED || !var1.items3.isEmpty()) {
         this.method1319(var1, true);
         var1.type2 = Script.Type2.STOPPED;
         var1.error = "";
      }
   }

   public void method1299(Script var1) {
      if (this.method1297(var1)) {
         this.method1308(var1, var1.isRunning() ? var1.source : null);
      } else {
         var1.longValue9 = this.method1304(var1);
         this.method1307(var1, this.method1305(var1));
      }
   }

   public void method1300() {
      this.method1301(false);

      for (Script var2 : new ArrayList<>(this.items.values())) {
         this.method1299(var2);
      }
   }

   private void method1301(boolean var1) {
      ArrayList<Path> var2 = new ArrayList();

      try (Stream<java.nio.file.Path> var3 = Files.list(this.path)) {
         var3.sorted().forEach(var2::add);
      } catch (UncheckedIOException | IOException var10) {
         return;
      }

      LinkedHashMap var11 = new LinkedHashMap();

      for (Object var5_o : (Iterable<?>)(Object)(var2)) {
         Path var5 = (Path)var5_o;
         String var6 = var5.getFileName().toString();
         if (!var6.startsWith(".")) {
            if (Files.isDirectory(var5)) {
               Path var7 = var5.resolve("main.js");
               if (!Files.isRegularFile(var7)) {
                  var7 = var5.resolve("src").resolve("main.js");
               }

               if (Files.isRegularFile(var7)) {
                  var11.put(this.method1302(var11, method1303(var6), var5), new Path[]{var5, var7});
               }
            } else if (var6.toLowerCase(Locale.ROOT).endsWith(".js")) {
               String var17 = method1303(var6.substring(0, var6.length() - 3));
               if (!var11.containsKey(var17)) {
                  var11.put(var17, new Path[]{null, var5});
               }
            }
         }
      }

      var var12 = this.items.entrySet().iterator();

      while (var12.hasNext()) {
         Map.Entry var14 = (Map.Entry)var12.next();
         if (!var11.containsKey(var14.getKey())) {
            this.method1319((Script)var14.getValue(), true);
            var12.remove();
            this.method1327((Script)var14.getValue(), "info", "скрипт удалён с диска");
         }
      }

      for (Object var15_o : (Iterable<?>)(Object)(var11.entrySet())) {
         Map.Entry var15 = (Map.Entry)var15_o;
         if (!this.items.containsKey(var15.getKey())) {
            Script var16 = new Script(this, (String)var15.getKey(), ((Path[])var15.getValue())[0], ((Path[])var15.getValue())[1]);
            this.items.put(var16.getId(), var16);
            var16.longValue9 = this.method1304(var16);
            this.method1307(var16, this.method1305(var16));
            var16.type2 = Script.Type2.STOPPED;
            if (!var1) {
               this.method1327(var16, "info", "найден новый скрипт");
            }

            if (this.method1297(var16)) {
               this.method1308(var16, null);
            }
         }
      }
   }

   private String method1302(Map<String, Path[]> var1, String var2, Path var3) {
      Script var4 = this.items.get(var2);
      if (var1.containsKey(var2) || var4 != null && !var3.equals(var4.getPath())) {
         int var5 = 2;

         while (true) {
            String var6 = var2 + "~" + var5;
            Script var7 = this.items.get(var6);
            if (!var1.containsKey(var6) && (var7 == null || var3.equals(var7.getPath()))) {
               return var6;
            }

            var5++;
         }
      } else {
         return var2;
      }
   }

   public static String method1303(String var0) {
      String var1 = var0.replaceAll("[^\\p{L}\\p{N}_.-]", "_");
      return var1.length() > 48 ? var1.substring(0, 48) : var1;
   }

   private long method1304(Script var1) {
      long var2 = 17L;

      try {
         if (var1.getPath() == null) {
            BasicFileAttributes var8 = Files.readAttributes(var1.getPath2(), BasicFileAttributes.class);
            var2 = var2 * 31L + var8.lastModifiedTime().toMillis();
            return var2 * 31L + var8.size();
         }

         final TreeMap var4 = new TreeMap();
         Files.walkFileTree(var1.getPath(), EnumSet.noneOf(FileVisitOption.class), 4, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path var1, BasicFileAttributes var2x) {
               if (!var1.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".js")) {
                  return FileVisitResult.CONTINUE;
               } else {
                  BasicFileAttributes var3 = var2x;
                  if (var2x.isSymbolicLink()) {
                     try {
                        var3 = Files.readAttributes(var1, BasicFileAttributes.class);
                     } catch (IOException var5) {
                        return FileVisitResult.CONTINUE;
                     }
                  }

                  if (var3.isRegularFile()) {
                     var4.put(var1, new long[]{var3.lastModifiedTime().toMillis(), var3.size()});
                  }

                  return FileVisitResult.CONTINUE;
               }
            }

            public FileVisitResult visitFileFailed(Path var1, IOException var2x) {
               return FileVisitResult.CONTINUE;
            }

            public FileVisitResult postVisitDirectory(Path var1, IOException var2x) {
               return FileVisitResult.CONTINUE;
            }
         });

         for (Object var6_o : (Iterable<?>)(Object)(var4.entrySet())) {
            Map.Entry var6 = (Map.Entry)var6_o;
            var2 = var2 * 31L + ((Path)var6.getKey()).toString().hashCode();
            var2 = var2 * 31L + ((long[])var6.getValue())[0];
            var2 = var2 * 31L + ((long[])var6.getValue())[1];
         }
      } catch (UncheckedIOException | IOException var7) {
      }

      return var2;
   }

   private String method1305(Script var1) {
      try {
         return this.method1306(var1.getPath2());
      } catch (Exception var3) {
         return "";
      }
   }

   public String method1306(Path var1) throws IOException {
      if (Files.size(var1) > 1048576L) {
         throw new IOException("файл больше 1 МБ");
      } else {
         String var2 = Files.readString(var1, StandardCharsets.UTF_8);
         return var2.startsWith("\ufeff") ? var2.substring(1) : var2;
      }
   }

   private void method1307(Script var1, String var2) {
      var1.name = var1.getId();
      var1.text2 = "";
      var1.version = "";
      var1.description = "";
      var1.text3 = "";
      var1.text4 = "";
      var1.text5 = "";
      var1.text6 = "";
      var1.color = -1;
      String[] var3 = var2.split("\n", 40);

      for (int var4 = 0; var4 < Math.min(var3.length, 30); var4++) {
         String var5 = var3[var4].trim();
         if (var5.startsWith("//")) {
            var5 = var5.substring(2).trim();
            if (var5.startsWith("@")) {
               int var6 = var5.indexOf(32);
               if (var6 >= 0) {
                  String var7 = var5.substring(1, var6).toLowerCase(Locale.ROOT);
                  String var8 = var5.substring(var6 + 1).trim();
                  if (var8.length() > 120) {
                     var8 = var8.substring(0, 120);
                  }

                  switch (var7) {
                     case "name":
                        var1.name = var8;
                        break;
                     case "author":
                        var1.text2 = var8;
                        break;
                     case "version":
                        var1.version = var8;
                        break;
                     case "description":
                        var1.description = var8;
                        break;
                     case "tags":
                        var1.text3 = var8;
                        break;
                     case "link":
                        var1.text4 = var8;
                        break;
                     case "net":
                        var1.text5 = var8;
                        break;
                     case "permissions":
                     case "perms":
                        var1.text6 = var8;
                        break;
                     case "color":
                        try {
                           var1.color = (int)Long.parseLong(var8.replace("#", ""), 16) | 0xFF000000;
                        } catch (NumberFormatException var13) {
                        }
                        break;
                     case "api":
                        try {
                           var1.intValue3 = Integer.parseInt(var8);
                        } catch (NumberFormatException var12) {
                        }
                  }
               }
            }
         }
      }
   }

   private void method1308(Script var1, String var2) {
      String var3;
      try {
         var3 = this.method1306(var1.getPath2());
      } catch (Exception var10) {
         this.method1323(var1, "не удалось прочитать файл: " + var10.getMessage(), true);
         return;
      }

      var1.longValue9 = this.method1304(var1);

      org.mozilla.javascript.Script var4;
      try {
         var4 = method1309(var3, var1.getId() + "/main.js");
      } catch (RhinoException var11) {
         String var6 = method1325(var11);
         if (var2 != null && var1.isRunning()) {
            var1.error = var6;
            this.method1327(var1, "error", "не собрался, работает прошлая версия: " + var6);
            this.method1324(var1, var6);
            return;
         }

         this.method1323(var1, var6, true);
         return;
      }

      if (var1.isRunning() || !var1.items3.isEmpty()) {
         this.method1319(var1, true);
      }

      OriginUtil.Data2 var5 = OriginUtil.method6921(var1);
      var1.flag = var5 != null && !var5.mine();
      this.method1307(var1, var3);
      if (var1.intValue3 > 3) {
         this.method1323(var1, "скрипту нужна версия API " + var1.intValue3 + ", у клиента 3", true);
      } else {
         String var12 = this.method1310(var1, var4, var3);
         if (var12 != null) {
            if (var2 != null) {
               this.method1327(var1, "error", "ошибка при запуске, возвращаю прошлую версию: " + var12);
               this.method1324(var1, var12);

               try {
                  org.mozilla.javascript.Script var7 = method1309(var2, var1.getId() + "/main.js");
                  this.method1307(var1, var2);
                  String var8 = this.method1310(var1, var7, var2);
                  if (var8 == null) {
                     var1.error = var12;
                     return;
                  }
               } catch (RhinoException var9) {
               }
            }

            this.method1323(var1, var12, true);
         }
      }
   }

   public static org.mozilla.javascript.Script method1309(String var0, String var1) {
      Context var2 = ScriptContextFactory.method7409();

      org.mozilla.javascript.Script var3;
      try {
         var3 = var2.compileString(var0, var1, 1, null);
      } finally {
         Context.exit();
      }

      return var3;
   }

   private String method1310(Script var1, org.mozilla.javascript.Script var2, String var3) {
      var1.error = "";
      var1.intValue4 = 0;
      var1.jsonObject = this.method1337(var1.getId());
      var1.scriptStorageFile = new ScriptStorageFile(this.method1311(var1));
      Context var4 = ScriptContextFactory.method7409();

      try {
         var1.scope = ScriptContextFactory.method7408(var4);
         ScriptModuleApi.method325(this, var1, var1.scope);
      } catch (RuntimeException var7) {
         Context.exit();
         this.method1320(var1);
         return "не удалось подготовить окружение: " + var7.getMessage();
      }

      Context.exit();
      var1.type2 = Script.Type2.RUNNING;

      try {
         ScriptContextFactory.method7410(3000L, (var1x, var2x, var3x, var4x) -> var2.exec(var1x, var2x), var1.scope, new Object[0]);
      } catch (Throwable var6) {
         var1.type2 = Script.Type2.FAILED;
         this.method1319(var1, false);
         return method1325(var6);
      }

      var1.source = var3;
      this.method1312(var1);
      this.method1331(var1, Script.Type.ENABLE);
      this.flag2 = true;
      this.method1327(var1, "info", "запущен" + (var1.version.isBlank() ? "" : " v" + var1.version));
      return null;
   }

   private Path method1311(Script var1) {
      return var1.getPath() != null ? var1.getPath().resolve("storage.json") : this.path.resolve(".data").resolve(var1.getId() + ".json");
   }

   private void method1312(Script var1) {
      JsonObject var2 = var1.jsonObject.has("modules") && var1.jsonObject.get("modules").isJsonObject()
         ? var1.jsonObject.getAsJsonObject("modules")
         : new JsonObject();

      for (Module1 var4 : var1.items3.values()) {
         JsonObject var5 = var2.has(var4.getName()) && var2.get(var4.getName()).isJsonObject() ? var2.getAsJsonObject(var4.getName()) : null;
         if (var5 == null) {
            if (var4.flag2 && !var4.isEnabledRaw()) {
               var4.toggle();
            }
         } else {
            if (var5.has("keyCode")) {
               var4.setKeyCode(var5.get("keyCode").getAsInt());
            }

            if (var5.has("keyModifiers")) {
               var4.setKeyModifiers(var5.get("keyModifiers").getAsInt());
            }

            if (var5.has("enabled") && var5.get("enabled").getAsBoolean() && !var4.isEnabledRaw()) {
               var4.toggle();
            }
         }
      }
   }

   public JsonObject method1313(Script var1, String var2) {
      JsonObject var3 = var1.jsonObject.has("modules") && var1.jsonObject.get("modules").isJsonObject() ? var1.jsonObject.getAsJsonObject("modules") : null;
      return var3 != null && var3.has(var2) && var3.get(var2).isJsonObject() ? var3.getAsJsonObject(var2) : null;
   }

   public JsonObject method1314(Script var1) {
      return var1.jsonObject.has("settings") && var1.jsonObject.get("settings").isJsonObject() ? var1.jsonObject.getAsJsonObject("settings") : null;
   }

   private void method1315(Script var1) {
      if (!var1.items3.isEmpty() || !var1.items6.isEmpty()) {
         JsonObject var2 = this.method1337(var1.getId());
         boolean var3 = false;
         JsonObject var4;
         if (var2.has("modules") && var2.get("modules").isJsonObject()) {
            var4 = var2.getAsJsonObject("modules");
         } else {
            var4 = new JsonObject();
            var2.add("modules", var4);
            var3 = true;
         }

         for (Module1 var6 : var1.items3.values()) {
            JsonObject var7 = var6.save();
            if (!var7.equals(var4.get(var6.getName()))) {
               var4.add(var6.getName(), var7);
               var3 = true;
            }
         }

         if (!var1.items6.isEmpty()) {
            JsonObject var8 = new JsonObject();

            for (Setting var10 : var1.items6.values()) {
               var10.safe(var8);
            }

            if (!var8.equals(var2.get("settings"))) {
               var2.add("settings", var8);
               var3 = true;
            }
         }

         if (var3) {
            this.flag = true;
         }
      }
   }

   public JsonElement method1316(Script var1, String var2) {
      var var3 = this.items3.get(var1.getId());
      return var3 == null ? null : (JsonElement)var3.get(var2);
   }

   public void method1317(Script var1, String var2) {
      var var3 = this.items3.get(var1.getId());
      if (var3 != null) {
         var3.remove(var2);
      }
   }

   private void method1318(Script var1) {
      if (!var1.items.isEmpty()) {
         var var2 = this.items3.computeIfAbsent(var1.getId(), var0 -> new HashMap<>());

         try {
            ScriptContextFactory.method7410(250L, (var2x, var3, var4x, var5) -> {
               for (Map.Entry var7 : var1.items.entrySet()) {
                  try {
                     var2.put((String)var7.getKey(), ScriptValueUtils.method6271(var7.getValue(), 0));
                  } catch (Throwable var9) {
                  }
               }

               return null;
            }, var1.scope, new Object[0]);
         } catch (Throwable var4) {
         }

         var1.items.clear();
      }
   }

   public void method1319(Script var1, boolean var2) {
      if (var2 && var1.isRunning()) {
         this.method1331(var1, Script.Type.DISABLE);
      }

      this.method1318(var1);
      this.method1315(var1);

      for (Module1 var4 : var1.items3.values()) {
         var4.release();
         if (var4.isEnabledRaw()) {
            var4.toggle();
         }

         Zenith.getInstance().getModuleManager().method2483(var4);
         this.items2.add(var4);
      }

      if (!var1.items3.isEmpty()) {
         this.flag2 = true;
      }

      var1.items3.clear();
      this.method1320(var1);
      var1.items5.clear();
      var1.items4.clear();
      var1.items6.clear();
      var1.items7.clear();
      var1.items8.clear();
      if (var1.scriptStorageFile != null) {
         var1.scriptStorageFile.flush();
      }

      var1.scope = null;
      if (var1.type2 == Script.Type2.RUNNING) {
         var1.type2 = Script.Type2.STOPPED;
      }
   }

   private void method1320(Script var1) {
      for (int var2 = var1.items2.size() - 1; var2 >= 0; var2--) {
         try {
            var1.items2.get(var2).close();
         } catch (Throwable var4) {
            logger.warn("Скрипт {}: ошибка при снятии ресурса", var1.getId(), var4);
         }
      }

      var1.items2.clear();
   }

   public Object method1321(Script var1, Function var2, Object... var3) {
      if (var1 != null && var2 != null && var1.isRunning() && var1.scope != null) {
         MinecraftClient var4 = MinecraftClient.getInstance();
         if (var4 != null && !var4.isOnThread()) {
            return null;
         } else {
            long var5 = System.nanoTime();

            try {
               Object var7 = ScriptContextFactory.method7410(250L, var2, var1.scope, var3);
               var1.intValue4 = 0;
               return var7;
            } catch (ScriptContextFactory.Entry var18) {
               long var22 = System.currentTimeMillis();
               if (var18.getMessage() != null && var18.getMessage().contains("памят")) {
                  this.method1323(var1, var18.getMessage(), true);
               } else {
                  if (var22 - var1.longValue > 30000L) {
                     var1.longValue = var22;
                     var1.intValue = 0;
                  }

                  var1.intValue++;
                  var1.error = var18.getMessage();
                  if (var1.intValue >= 3) {
                     this.method1323(var1, "трижды за 30 секунд превысил лимит времени — " + var18.getMessage(), true);
                  } else {
                     this.method1327(var1, "warn", "вызов прерван: " + var18.getMessage() + " (" + var1.intValue + "/3)");
                  }
               }
            } catch (StackOverflowError var19) {
               this.method1323(var1, "переполнение стека (бесконечная рекурсия?)", true);
            } catch (Throwable var20) {
               String var8 = method1325(var20);
               var1.intValue4++;
               var1.error = var8;
               this.method1327(var1, "error", var8);
               if (var1.intValue4 >= 5) {
                  this.method1323(var1, "5 ошибок подряд, последняя: " + var8, true);
               } else if (var1.intValue4 == 1) {
                  this.method1324(var1, var8);
               }
            } finally {
               long var12 = System.nanoTime() - var5;
               var1.longValue4 += var12;
               var1.longValue5++;
               if (var12 > var1.longValue6) {
                  var1.longValue6 = var12;
               }

               this.method1322(var1, var12);
            }

            return null;
         }
      } else {
         return null;
      }
   }

   private void method1322(Script var1, long var2) {
      long var4 = System.nanoTime();
      if (var4 - var1.longValue2 >= 1000000000L) {
         if (var1.longValue2 != 0L) {
            if (var1.longValue3 > 350000000L) {
               var1.intValue2++;
               if (var1.intValue2 == 2) {
                  this.method1327(var1, "warn", "скрипт занимает " + var1.longValue3 / 1000000L + " мс в секунду — игра тормозит");
               }

               if (var1.intValue2 >= 5 && var1.isRunning()) {
                  var1.longValue2 = var4;
                  var1.longValue3 = 0L;
                  var1.intValue2 = 0;
                  this.method1323(var1, "пять секунд подряд занимал больше 350 мс в секунду и тормозил игру", true);
                  return;
               }
            } else {
               var1.intValue2 = 0;
            }
         }

         var1.longValue2 = var4;
         var1.longValue3 = 0L;
      }

      var1.longValue3 += var2;
   }

   public void method1323(Script var1, String var2, boolean var3) {
      boolean var4 = var1.isRunning();
      var1.error = var2;
      var1.longValue8 = System.currentTimeMillis();
      this.method1327(var1, "error", var2);
      if (var4 || !var1.items3.isEmpty()) {
         var1.type2 = Script.Type2.FAILED;
         this.method1319(var1, false);
      }

      var1.type2 = Script.Type2.FAILED;
      if (var3) {
         this.method1324(var1, "остановлен: " + var2);
      }
   }

   private void method1324(Script var1, String var2) {
      MinecraftClient var3 = MinecraftClient.getInstance();
      if (var3 != null && var3.player != null) {
         String var4 = var2.length() > 160 ? var2.substring(0, 160) + "…" : var2;
         ChatLogger.method2020(ChatLogger.Type.ERROR, "[JS] " + var1.method1403() + ": " + var4);
      }
   }

   public static String method1325(Throwable var0) {
      if (var0 instanceof JavaScriptException var10) {
         Object var2 = var10.getValue();

         String var3;
         try {
            var3 = String.valueOf(ScriptContextFactory.method7410(250L, (var1x, var2x, var3x, var4) -> {
               if (var2 instanceof Scriptable var5x) {
                  Object var6x = ScriptValueUtils.method6263(var5x, "message");
                  return ScriptValueUtils.method6253(var6x) ? Context.toString(var2) : Context.toString(var6x);
               } else {
                  return Context.toString(var2);
               }
            }, null, new Object[0]));
         } catch (Throwable var6) {
            var3 = var2 instanceof Scriptable var5 ? "[object " + var5.getClassName() + "]" : String.valueOf(var2);
         }

         return var3 + method1326(var10);
      } else if (var0 instanceof EcmaError var9) {
         return var9.getName() + ": " + var9.getErrorMessage() + method1326(var9);
      } else if (var0 instanceof EvaluatorException var8) {
         return var8.details() + method1326(var8);
      } else if (var0 instanceof RhinoException var7) {
         return var7.details() + method1326(var7);
      } else if (var0 instanceof ScriptContextFactory.Entry) {
         return var0.getMessage();
      } else {
         String var1 = var0.getMessage();
         return var0.getClass().getSimpleName() + (var1 == null ? "" : ": " + var1);
      }
   }

   private static String method1326(RhinoException var0) {
      String var1 = var0.sourceName();
      int var2 = var0.lineNumber();
      if (var1 != null && var2 > 0) {
         int var3 = var1.lastIndexOf(47);
         return " (" + (var3 >= 0 ? var1.substring(var3 + 1) : var1) + ":" + var2 + ")";
      } else {
         return "";
      }
   }

   public void method1327(Script var1, String var2, String var3) {
      String var4 = var1 == null ? "js" : var1.method1403();
      if (var3.length() > 2000) {
         var3 = var3.substring(0, 2000) + "…";
      }

      boolean var5 = "error".equals(var2) || "warn".equals(var2);
      boolean var6 = var5;
      long var7 = 0L;
      long var9 = System.currentTimeMillis();
      synchronized (this.deque) {
         this.deque.addLast(new Storage.Data2(var9, var4, var2, var3));

         while (this.deque.size() > 1000) {
            this.deque.removeFirst();
         }

         this.longValue++;
         if (!var5) {
            long[] var12 = this.items4.computeIfAbsent(var4, var0 -> new long[3]);
            if (var9 - var12[0] >= 1000L) {
               var7 = var12[2];
               var12[0] = var9;
               var12[1] = 0L;
               var12[2] = 0L;
            }

            if (var12[1] < 20L) {
               var12[1]++;
               var6 = true;
            } else {
               var12[2]++;
            }
         }
      }

      if (var7 > 0L) {
         logger.info("[JS:{}] … ещё {} строк не продублировано в лог игры (смотри консоль скриптов)", var4, var7);
      }

      if (var6) {
         if (var5) {
            logger.warn("[JS:{}] {}", var4, var3);
         } else {
            logger.info("[JS:{}] {}", var4, var3);
         }
      }
   }

   public long method1328() {
      synchronized (this.deque) {
         return this.longValue;
      }
   }

   public int method1329(Script var1, Function var2, long var3, boolean var5) {
      if (var1.items5.size() >= 256) {
         throw ScriptValueUtils.method6251("у скрипта больше 256 таймеров");
      } else {
         Storage.Entry var6 = new Storage.Entry(++this.intValue, var2, var5 ? Math.max(50L, var3) : var3, var5);
         var1.items5.add(var6);
         return var6.intValue;
      }
   }

   public void method1330(Script var1, int var2) {
      for (Storage.Entry var4 : var1.items5) {
         if (var4.intValue == var2) {
            var4.cancelled = true;
         }
      }
   }

   public void method1331(Script var1, Script.Type var2, Object... var3) {
      for (Function var5 : new ArrayList<>(var1.method1417(var2))) {
         if (!var1.isRunning() && var2 != Script.Type.DISABLE) {
            return;
         }

         if (var2 == Script.Type.DISABLE) {
            Script.Type2 var6 = var1.type2;
            var1.type2 = Script.Type2.RUNNING;
            this.method1321(var1, var5, var3);
            if (var1.type2 == Script.Type2.RUNNING) {
               var1.type2 = var6;
            }
         } else {
            this.method1321(var1, var5, var3);
         }
      }
   }

   public void method1332(Script.Type var1, java.util.function.Function<Script, Object[]> var2) {
      for (Script var4 : new ArrayList<>(this.items.values())) {
         if (var4.isRunning() && !var4.method1417(var1).isEmpty()) {
            this.method1331(var4, var1, (Object[])var2.apply(var4));
         }
      }
   }

   @EventTarget(0)
   public void onTick(TickEvent var1) {
      this.ticks++;
      if (this.ticks % 20 == 0) {
         this.method1334();
      }

      this.method1335();
      this.scriptEventWatcher.tick();
      if (this.ticks % 100 == 0) {
         this.flush();
      }

      if (this.flag2) {
         this.flag2 = false;
         this.method1336();
      }
   }

   @EventTarget(0)
   public void method1333(ChatSendEvent var1) {
      if (var1.method4603() && var1.getMessage() != null) {
         String var2 = var1.getMessage().trim();
         String var3 = Zenith.getInstance().getCommandManager().getPrefix();
         if (var2.startsWith(var3) && var2.length() > var3.length()) {
            String var4 = var2.substring(var3.length());
            int var5 = var4.indexOf(32);
            String var6 = (var5 < 0 ? var4 : var4.substring(0, var5)).toLowerCase(Locale.ROOT);
            String var7 = var5 < 0 ? "" : var4.substring(var5 + 1).trim();

            for (Script var9 : new ArrayList<>(this.items.values())) {
               var var10 = var9.isRunning() ? var9.items7.get(var6) : null;
               if (var10 != null) {
                  var1.cancel();
                  this.method1321(var9, var10, var7, ScriptValueUtils.method6268(var9.scope, var7.isEmpty() ? new Object[0] : var7.split("\\s+")));
                  return;
               }
            }
         }
      }
   }

   private void method1334() {
      this.method1301(false);

      for (Script var2 : new ArrayList<>(this.items.values())) {
         long var3 = this.method1304(var2);
         if (var3 != var2.longValue9) {
            var2.longValue9 = var3;
            if (this.method1297(var2)) {
               this.method1327(var2, "info", "файл изменён, перезагружаю");
               this.method1308(var2, var2.isRunning() ? var2.source : null);
            } else {
               this.method1307(var2, this.method1305(var2));
            }
         }
      }
   }

   private void method1335() {
      long var1 = System.currentTimeMillis();

      for (Script var4 : new ArrayList<>(this.items.values())) {
         if (var4.isRunning() && !var4.items5.isEmpty()) {
            for (Storage.Entry var6 : new ArrayList<>(var4.items5)) {
               if (!var6.cancelled && var1 >= var6.longValue2) {
                  if (var6.flag) {
                     var6.longValue2 = var1 + var6.longValue;
                  } else {
                     var6.cancelled = true;
                  }

                  this.method1321(var4, var6.function);
                  if (!var4.isRunning()) {
                     break;
                  }
               }
            }

            var4.items5.removeIf(var0 -> var0.cancelled);
         }
      }
   }

   private void method1336() {
      var var1 = new ArrayList<>(this.items2);
      this.items2.clear();

      try {
         if (Zenith.getInstance().getNLMenuScreen() != null) {
            Zenith.getInstance().getNLMenuScreen().refreshModules(var1);
         }
      } catch (Throwable var3) {
         logger.warn("Не удалось обновить меню после скриптов", var3);
      }
   }

   public void flush() {
      for (Script var2 : this.items.values()) {
         if (var2.isRunning()) {
            this.method1315(var2);
         }

         if (var2.scriptStorageFile != null) {
            var2.scriptStorageFile.flush();
         }
      }

      if (this.flag) {
         this.flag = false;
         this.method1339();
      }
   }

   public JsonObject method1337(String var1) {
      JsonElement var2 = this.jsonObject.get(var1);
      if (var2 != null && var2.isJsonObject()) {
         return var2.getAsJsonObject();
      } else {
         JsonObject var3 = new JsonObject();
         this.jsonObject.add(var1, var3);
         return var3;
      }
   }

   private void method1338() {
      try {
         if (Files.isRegularFile(this.path2)) {
            JsonElement var1 = JsonParser.parseString(Files.readString(this.path2, StandardCharsets.UTF_8));
            if (var1.isJsonObject()) {
               this.jsonObject = var1.getAsJsonObject();
            }
         }
      } catch (Exception var2) {
         logger.warn("Не удалось прочитать состояние скриптов", var2);
      }
   }

   private void method1339() {
      try {
         Path var1 = this.path2.resolveSibling(".state.json.tmp");
         Files.writeString(var1, gson.toJson(this.jsonObject), StandardCharsets.UTF_8);
         Files.move(var1, this.path2, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (Exception var2) {
         logger.warn("Не удалось сохранить состояние скриптов", var2);
      }
   }

   public Script method1340(String var1, String var2) throws IOException {
      String var3 = method1303(var1);
      if (!var3.isBlank() && this.method1294(var3) == null && !Files.exists(this.path.resolve(var3))) {
         Path var4 = this.path.resolve(var3);
         Files.createDirectories(var4);
         Files.writeString(var4.resolve("main.js"), ScriptHeaderParser.method1000(var2, var1), StandardCharsets.UTF_8);
         this.method1301(false);
         return this.method1294(var3);
      } else {
         throw new IOException("скрипт с таким именем уже есть");
      }
   }

   public Object method1341(Script var1, String var2) {
      if (var2.length() > 32768) {
         throw new IllegalArgumentException("код слишком длинный");
      } else {
         ScriptableObject var3;
         if (var1 != null && var1.isRunning()) {
            var3 = var1.scope;
         } else {
            Script var4 = this.method1342();
            var3 = var4.scope;
         }

         org.mozilla.javascript.Script var5 = method1309(var2, "console");
         return ScriptContextFactory.method7410(250L, (var1x, var2x, var3x, var4x) -> {
            Object var5x = var5.exec(var1x, var2x);
            if (var5x instanceof Scriptable var6 && !(var5x instanceof Function)) {
               try {
                  return gson.toJson(ScriptValueUtils.method6271(var6, 0));
               } catch (RuntimeException var8) {
                  return Context.toString(var5x);
               }
            } else {
               return ScriptValueUtils.method6253(var5x) ? "undefined" : Context.toString(var5x);
            }
         }, var3, new Object[0]);
      }
   }

   private Script method1342() {
      if (this.script != null && this.script.isRunning()) {
         return this.script;
      } else {
         this.script = new Script(this, "console", null, this.path.resolve(".console.js"));
         this.script.jsonObject = new JsonObject();
         this.script.scriptStorageFile = new ScriptStorageFile(this.path.resolve(".data").resolve(".console.json"));
         Context var1 = ScriptContextFactory.method7409();

         try {
            this.script.scope = ScriptContextFactory.method7408(var1);
            ScriptModuleApi.method325(this, this.script, this.script.scope);
         } finally {
            Context.exit();
         }

         this.script.type2 = Script.Type2.RUNNING;
         return this.script;
      }
   }

   public Map<String, List<String>> method1343() {
      if (this.items5 != null) {
         return this.items5;
      } else {
         LinkedHashMap var1 = new LinkedHashMap();

         try {
            Script var2 = this.method1342();
            ScriptableObject var3 = var2.scope;

            for (String var7 : ScriptModuleApi.texts) {
               Object var8 = ScriptableObject.getProperty(var3, var7);
               var1.put(var7, method1344(var8));
               if (var8 instanceof ScriptableObject var9) {
                  for (Object var13 : var9.getAllIds()) {
                     Object var14 = var9.get(String.valueOf(var13), var9);
                     if (var14 instanceof ScriptableObject var15 && !(var14 instanceof Function)) {
                        var1.put(var7 + "." + var13, method1344(var15));
                     }
                  }
               }
            }
         } catch (Throwable var16) {
            logger.warn("Не удалось собрать подсказки API", var16);
         }

         this.items5 = var1;
         return var1;
      }
   }

   private static List<String> method1344(Object var0) {
      ArrayList<String> var1 = new ArrayList<>();
      if (var0 instanceof ScriptableObject var2) {
         for (Object var6 : var2.getAllIds()) {
            var1.add(String.valueOf(var6));
         }
      }

      var1.sort(String.CASE_INSENSITIVE_ORDER);
      return var1;
   }

   public List<Path> method1345(Script var1) {
      ArrayList<Path> var2 = new ArrayList<>();
      if (var1.getPath() == null) {
         var2.add(var1.getPath2());
         return var2;
      } else {
         try (Stream<java.nio.file.Path> var3 = Files.walk(var1.getPath(), 4)) {
            var3.filter(var0 -> Files.isRegularFile(var0))
               .filter(
                  var0 -> {
                     String var1x = var0.getFileName().toString().toLowerCase(Locale.ROOT);
                     return !var1x.equals("storage.json")
                        && !var1x.endsWith(".tmp")
                        && !var1x.startsWith(".")
                        && (var1x.endsWith(".js") || var1x.endsWith(".json") || var1x.endsWith(".md") || var1x.endsWith(".txt"));
                  }
               )
               .sorted((java.nio.file.Path var1x, java.nio.file.Path var2x) -> var1x.equals(var1.getPath2()) ? -1 : (var2x.equals(var1.getPath2()) ? 1 : var1x.compareTo(var2x)))
               .limit(200L)
               .forEach(var2::add);
         } catch (UncheckedIOException | IOException var8) {
         }

         return var2;
      }
   }

   public Path method1346(Script var1, String var2) throws IOException {
      if (var1.getPath() == null) {
         Path var7 = var1.getPath2().toAbsolutePath().normalize();
         if (!var7.getFileName().toString().equals(Path.of(var2).getFileName().toString())) {
            throw new IOException("у одиночного скрипта есть только " + var7.getFileName());
         } else {
            return var7;
         }
      } else {
         Path var3 = var1.getPath().toAbsolutePath().normalize();
         Path var4 = var3.resolve(var2).toAbsolutePath().normalize();
         if (!var4.startsWith(var3)) {
            throw new IOException("путь вне папки скрипта");
         } else {
            for (Path var6 : var3.relativize(var4)) {
               if (var6.toString().startsWith(".")) {
                  throw new IOException("служебные файлы с точкой в начале имени трогать нельзя");
               }
            }

            return var4;
         }
      }
   }

   public void method1347(Script var1, Path var2, String var3) throws IOException {
      Path var4 = (var1.getPath() == null ? this.path : var1.getPath()).toAbsolutePath().normalize();
      Path var5 = var2.toAbsolutePath().normalize();
      if (!var5.startsWith(var4)) {
         throw new IOException("путь вне папки скрипта");
      } else {
         for (Path var7 : var4.relativize(var5)) {
            if (var7.toString().startsWith(".")) {
               throw new IOException("служебные файлы с точкой в начале имени трогать нельзя");
            }
         }

         if (ScriptStorageFile.method1005(var3) > 1048576) {
            throw new IOException("файл больше 1 МБ");
         } else {
            Files.createDirectories(var5.getParent());
            Path var8 = var5.resolveSibling(var5.getFileName() + ".tmp");
            Files.writeString(var8, var3, StandardCharsets.UTF_8);
            Files.move(var8, var5, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            if (var5.getFileName().toString().equalsIgnoreCase("CHANGELOG.md")) {
               this.items7.clear();
            }
         }
      }
   }

   public static int method1348(Script var0, Path var1) {
      String var2 = var0.getError();
      if (var2 != null && !var2.isEmpty() && var1 != null) {
         Matcher var3 = pattern.matcher(var2);
         int var4 = -1;

         while (var3.find()) {
            if (var1.getFileName().toString().equals(var3.group(1))) {
               var4 = Integer.parseInt(var3.group(2));
            }
         }

         return var4;
      } else {
         return -1;
      }
   }

   public List<Storage.Data2> method1349(Script var1) {
      ArrayList<Storage.Data2> var2 = new ArrayList<>();
      String var3 = var1 == null ? null : var1.method1403();
      synchronized (this.deque) {
         for (Storage.Data2 var6 : this.deque) {
            if (var3 == null || var6.getText().equals(var3)) {
               var2.add(var6);
            }
         }

         return var2;
      }
   }

   public void method1350(Script var1, Map<String, String> var2) throws IOException {
      String var3 = this.method1306(var1.getPath2());
      String[] var4 = var3.split("\n", -1);
      LinkedHashMap var5 = new LinkedHashMap(var2);
      ArrayList var6 = new ArrayList();
      int var7 = 0;

      for (int var8 = 0; var8 < var4.length; var8++) {
         String var9 = var4[var8];
         String var10 = var9.trim();
         if (var8 < 30 && var10.startsWith("//")) {
            String var11 = var10.substring(2).trim();
            if (var11.startsWith("@")) {
               int var12 = var11.indexOf(32);
               String var13 = (var12 < 0 ? var11.substring(1) : var11.substring(1, var12)).toLowerCase(Locale.ROOT);
               if (var5.containsKey(var13)) {
                  String var14 = clean((String)var5.remove(var13));
                  if (!var14.isEmpty()) {
                     var6.add("// @" + var13 + " " + var14);
                  }

                  var7 = var6.size();
               } else {
                  var6.add(var9);
                  var7 = var6.size();
               }
               continue;
            }
         }

         var6.add(var9);
      }

      ArrayList var15 = new ArrayList();

      for (Object var17_o : (Iterable<?>)(Object)(var5.entrySet())) {
         Map.Entry var17 = (Map.Entry)var17_o;
         String var18 = clean((String)var17.getValue());
         if (!var18.isEmpty()) {
            var15.add("// @" + (String)var17.getKey() + " " + var18);
         }
      }

      var6.addAll(var7, var15);
      this.method1347(var1, var1.getPath2(), String.join("\n", var6));
      this.method1307(var1, this.method1306(var1.getPath2()));
      var1.longValue9 = this.method1304(var1);
   }

   private static String clean(String var0) {
      if (var0 == null) {
         return "";
      } else {
         String var1 = var0.replace("\r", " ").replace("\n", " ").trim();
         return var1.length() > 120 ? var1.substring(0, 120) : var1;
      }
   }

   public String method1351(Script var1, int var2, String var3) throws IOException {
      String var4 = var1.getVersion().isBlank() ? "1.0.0" : var1.getVersion();
      String[] var5 = var4.split("\\.");
      int[] var6 = new int[3];

      for (int var7 = 0; var7 < 3; var7++) {
         try {
            var6[var7] = var7 < var5.length ? Integer.parseInt(var5[var7].replaceAll("[^0-9]", "")) : 0;
         } catch (NumberFormatException var14) {
            var6[var7] = 0;
         }
      }

      var6[var2]++;

      for (int var15 = var2 + 1; var15 < 3; var15++) {
         var6[var15] = 0;
      }

      String var16 = var6[0] + "." + var6[1] + "." + var6[2];
      this.method1350(var1, Map.of("version", var16));
      if (var1.getPath() != null) {
         Path var8 = var1.getPath().resolve("CHANGELOG.md");
         String var9 = Files.isRegularFile(var8) ? this.method1306(var8) : "# " + var1.method1403() + "\n";
         String var10 = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
         String var11 = "\n## v" + var16 + " — " + var10 + "\n" + (var3 != null && !var3.isBlank() ? "- " + clean(var3) : "- обновление") + "\n";
         int var12 = var9.indexOf("\n## ");
         String var13 = var12 < 0 ? var9 + var11 : var9.substring(0, var12) + var11 + var9.substring(var12);
         this.method1347(var1, var8, var13);
      }

      this.method1299(var1);
      return var16;
   }

   public List<String> method1352(Script var1, int var2) {
      ArrayList<String> var3 = new ArrayList<>();
      if (var1.getPath() == null) {
         return var3;
      } else {
         Path var4 = var1.getPath().resolve("CHANGELOG.md");
         Storage.Data var5 = this.items7.get(var4);

         Storage.Data var6;
         try {
            BasicFileAttributes var7 = Files.readAttributes(var4, BasicFileAttributes.class);
            if (!var7.isRegularFile()) {
               var6 = data;
            } else {
               long var8 = var7.lastModifiedTime().toMillis();
               if (var5 != null && var5.modified() == var8 && var5.size() == var7.size()) {
                  var6 = var5;
               } else {
                  ArrayList var10 = new ArrayList();

                  for (String var14 : this.method1306(var4).split("\n")) {
                     String var15 = var14.trim();
                     if (var15.startsWith("## ") || var15.startsWith("- ")) {
                        var10.add(var15);
                     }
                  }

                  var6 = new Storage.Data(var8, var7.size(), List.copyOf(var10));
               }
            }
         } catch (IOException var16) {
            var6 = data;
         }

         if (var6 != var5) {
            this.items7.put(var4, var6);
         }

         for (String var18 : var6.lines()) {
            var3.add(var18);
            if (var3.size() >= var2) {
               break;
            }
         }

         return var3;
      }
   }

   public void method1353(Script var1, Path var2) throws IOException {
      if (var1.getPath() == null) {
         throw new IOException("у одиночного .js нет папки для аватарки");
      } else if (Files.size(var2) > 4194304L) {
         throw new IOException("картинка больше 4 МБ");
      } else {
         byte[] var3 = Files.readAllBytes(var2);
         int[] var4 = ImageLoader.method231(var3);
         if (var4[0] <= 2048 && var4[1] <= 2048) {
            try (NativeImage var5 = NativeImage.read(new ByteArrayInputStream(var3))) {
               if (var5.getWidth() > 2048 || var5.getHeight() > 2048) {
                  throw new IOException("картинка больше 2048×2048");
               }
            } catch (IOException var10) {
               throw var10;
            } catch (Exception var11) {
               throw new IOException("нужен PNG");
            }

            Files.write(var1.method1412(), var3);
         } else {
            throw new IOException("картинка больше 2048×2048");
         }
      }
   }

   public void method1354(Script var1) throws IOException {
      if (var1.method1412() != null) {
         Files.deleteIfExists(var1.method1412());
      }
   }

   public Script method1355(Script var1) throws IOException {
      if (var1.getPath() == null) {
         throw new IOException("одиночный .js дублируется копированием файла");
      } else {
         String var2 = var1.getId() + "_copy";
         String var3 = var2;

         for (int var4 = 2; this.method1294(var3) != null || Files.exists(this.path.resolve(var3)); var4++) {
            var3 = var2 + var4;
         }

         Path var12 = this.path.resolve(var3);

         try (Stream<java.nio.file.Path> var5 = Files.walk(var1.getPath())) {
            for (Path var7 : (Iterable<Path>)var5::iterator) {
               String var8 = var7.getFileName().toString();
               if (!var8.equals("storage.json") && !var8.endsWith(".tmp") && !var8.equals(".origin.json")) {
                  Path var9 = var12.resolve(var1.getPath().relativize(var7).toString());
                  if (Files.isDirectory(var7)) {
                     Files.createDirectories(var9);
                  } else {
                     Files.copy(var7, var9);
                  }
               }
            }
         }

         this.method1301(false);
         Script var13 = this.method1294(var3);
         if (var13 != null) {
            this.method1350(var13, Map.of("name", var1.method1403() + " (копия)"));
         }

         return var13;
      }
   }

   public Script method1356(Script var1, String var2) throws IOException {
      String var3 = method1303(var2);
      if (var1.getPath() == null) {
         throw new IOException("переименовать можно только скрипт-папку");
      } else if (!var3.isBlank() && !var3.equals(var1.getId())) {
         if (this.method1294(var3) == null && !Files.exists(this.path.resolve(var3))) {
            boolean var4 = this.method1297(var1);
            this.method1319(var1, true);
            var1.type2 = Script.Type2.STOPPED;

            try {
               Files.move(var1.getPath(), this.path.resolve(var3));
            } catch (IOException var7) {
               if (var4) {
                  this.method1308(var1, null);
               }

               this.flag2 = true;
               throw var7;
            }

            JsonElement var5 = this.jsonObject.remove(var1.getId());
            this.items.remove(var1.getId());
            if (var5 != null) {
               this.jsonObject.add(var3, var5);
            }

            this.flag = true;
            this.method1301(false);
            Script var6 = this.method1294(var3);
            if (var6 != null && var4 && !var6.isRunning()) {
               this.method1308(var6, null);
            }

            this.flag2 = true;
            return var6;
         } else {
            throw new IOException("папка " + var3 + " уже есть");
         }
      } else {
         return var1;
      }
   }

   public void method1357(Script var1) throws IOException {
      this.method1319(var1, true);
      var1.type2 = Script.Type2.STOPPED;
      Path var2 = this.path.resolve(".trash");
      Files.createDirectories(var2);
      String var3 = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
      Path var4 = var1.getPath() == null ? var1.getPath2() : var1.getPath();
      Files.move(var4, var2.resolve(var1.getId() + "-" + var3 + (var1.getPath() == null ? ".js" : "")));
      this.jsonObject.remove(var1.getId());
      this.flag = true;
      this.items.remove(var1.getId());
      this.flag2 = true;
   }

   public void method1358(Script var1) throws IOException {
      Path var2 = this.method1311(var1);
      var1.scriptStorageFile = null;
      Files.deleteIfExists(var2);
      this.method1299(var1);
   }

   public void method1359(Script var1) {
      JsonObject var2 = this.method1337(var1.getId());
      boolean var3 = var1.isRunning();
      if (var3) {
         this.method1319(var1, true);
         var1.type2 = Script.Type2.STOPPED;
      }

      var2.remove("modules");
      var2.remove("settings");
      this.flag = true;
      if (var3) {
         this.method1308(var1, null);
      }
   }

   public Path method1360(Script var1) throws IOException {
      Path var2 = this.path.resolve(".exports");
      Files.createDirectories(var2);
      String var3 = var1.getVersion().isBlank() ? "" : "-v" + var1.getVersion();
      Path var4 = var2.resolve(var1.getId() + var3 + ".zjs");
      Files.write(var4, OriginUtil.method6919(var1));
      return var4;
   }

   public void shutdown() {
      Server.getInstance().stop();

      for (Script var2 : this.items.values()) {
         if (var2.isRunning()) {
            this.method1315(var2);
         }

         if (var2.scriptStorageFile != null) {
            var2.scriptStorageFile.flush();
         }
      }

      this.flag = true;
      this.flush();
   }

   private static final class Data {
      private final long longValue;
      private final long longValue2;
      private final List<String> items;

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public List<String> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, longValue2, items);
      }

      @Override
      public String toString() {
         return "Data[" + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + ", " + "items=" + this.items + "]";
      }

      private Data(long var1, long var3, List<String> var5) {
         this.longValue = var1;
         this.longValue2 = var3;
         this.items = var5;
      }

      public long modified() {
         return this.longValue;
      }

      public long size() {
         return this.longValue2;
      }

      public List<String> lines() {
         return this.items;
      }
   }

   public static final class Data2 {
      private final long longValue;
      private final String text;
      private final String text2;
      private final String text3;

      public long longValue() {
         return this.longValue;
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
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2, text3);
      }

      @Override
      public String toString() {
         return "Data2[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + "]";
      }

      public Data2(long var1, String var3, String var4, String var5) {
         this.longValue = var1;
         this.text = var3;
         this.text2 = var4;
         this.text3 = var5;
      }

      public long time() {
         return this.longValue;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public String text() {
         return this.text3;
      }
   }

   public static final class Entry {
      public final Function function;
      public final long longValue;
      public long longValue2;
      public final boolean flag;
      public final int intValue;
      public boolean cancelled;

      public Entry(int var1, Function var2, long var3, boolean var5) {
         this.intValue = var1;
         this.function = var2;
         this.longValue = Math.max(0L, var3);
         this.longValue2 = System.currentTimeMillis() + this.longValue;
         this.flag = var5;
      }
   }
}

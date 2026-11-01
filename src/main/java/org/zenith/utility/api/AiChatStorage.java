package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import org.zenith.Zenith;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.Storage;
import org.zenith.utility.config.AiImageStore;
import org.zenith.utility.misc.ScriptHeaderParser;
import org.zenith.utility.misc.ShownProvider;

public final class AiChatStorage {
   private static final AiChatStorage INSTANCE = new AiChatStorage();
   private static final Pattern pattern = Pattern.compile("```[a-zA-Z]*[ \\t]*(?:file|path|title)[ \\t]*[:=][ \\t]*\"?([^\\n`\"]+)\"?[ \\t]*\\n(.*?)```", 32);
   private static final Pattern pattern2 = Pattern.compile("//\\s*@name\\s+(.+)");
   private static final Pattern pattern3 = Pattern.compile("```[\\s\\S]*?(```|$)");
   private static final int CONST_intValue = 40;
   private static final int CONST_intValue2 = 160000;
   private static final int CONST_intValue3 = 24;
   private static final int CONST_intValue4 = 2;
   private static final int CONST_intValue5 = 80;
   private static final ExecutorService executorService = Executors.newSingleThreadExecutor(var0 -> {
      Thread var1 = new Thread(var0, "zenith-ai-chats");
      var1.setDaemon(true);
      return var1;
   });
   private final List<AiChatStorage.Entry> items = new CopyOnWriteArrayList<>();
   private volatile AiChatStorage.Entry entry;
   private volatile AiChatStorage.Entry entry2;
   private volatile boolean busy;
   private volatile boolean flag;
   private volatile int revision;
   private volatile boolean loaded;
   private final Path path = Zenith.file.toPath().resolve("ai-chats");

   public static AiChatStorage method2741() {
      INSTANCE.method2742();
      return INSTANCE;
   }

   private synchronized void method2742() {
      if (!this.loaded) {
         this.loaded = true;

         try {
            Files.createDirectories(this.path);

            try (Stream<java.nio.file.Path> var1 = Files.list(this.path)) {
               for (Object var3_o : (Iterable<?>)(Object)(var1.filter(var0 -> var0.toString().endsWith(".json")).toList())) {
                  Path var3 = (Path)var3_o;
                  try {
                     JsonObject var4 = JsonParser.parseString(Files.readString(var3, StandardCharsets.UTF_8)).getAsJsonObject();
                     AiChatStorage.Entry var5 = new AiChatStorage.Entry(var4.get("id").getAsString(), var4.get("createdAt").getAsLong());
                     var5.longValue2 = var4.has("updatedAt") ? var4.get("updatedAt").getAsLong() : var5.longValue;
                     var5.title = var4.has("title") ? var4.get("title").getAsString() : "";
                     var5.text2 = var4.has("target") ? var4.get("target").getAsString() : null;

                     for (JsonElement var7 : var4.getAsJsonArray("messages")) {
                        var5.items.add(AiChatStorage.Entry2.method2777(var7.getAsJsonObject()));
                     }

                     if (!var5.items.isEmpty()) {
                        this.items.add(var5);
                     }
                  } catch (Exception var9) {
                  }
               }
            }
         } catch (IOException var11) {
         }

         this.items.sort(Comparator.comparingLong(AiChatStorage.Entry::getLongValue2).reversed());
         this.entry = new AiChatStorage.Entry(method2743(), System.currentTimeMillis());
      }
   }

   private static String method2743() {
      return Long.toString(System.currentTimeMillis(), 36) + UUID.randomUUID().toString().substring(0, 4);
   }

   public List<AiChatStorage.Entry> method2744() {
      var var1 = new ArrayList<>(this.items);
      var1.sort(Comparator.comparingLong(AiChatStorage.Entry::getLongValue2).reversed());
      return var1;
   }

   public AiChatStorage.Entry getEntry() {
      return this.entry;
   }

   public List<AiChatStorage.Entry2> method2745() {
      return this.entry.items;
   }

   public boolean isBusy() {
      return this.busy;
   }

   public boolean method2746() {
      return this.busy && this.entry2 == this.entry;
   }

   public int revision() {
      return this.revision;
   }

   public String method2747() {
      return this.entry.text2;
   }

   public void method2748(String var1) {
      if (var1 == null ? this.entry.text2 != null : !var1.equals(this.entry.text2)) {
         this.entry.text2 = var1;
         if (!this.entry.empty()) {
            this.method2755(this.entry);
         }

         this.revision++;
      }
   }

   public void method2749(AiChatStorage.Entry var1) {
      if (var1 != null && var1 != this.entry) {
         if (this.entry.empty()) {
            this.items.remove(this.entry);
         }

         this.entry = var1;
         this.revision++;
      }
   }

   public void method2750(String var1) {
      if (this.entry.empty()) {
         this.entry.text2 = var1;
      } else {
         this.entry = new AiChatStorage.Entry(method2743(), System.currentTimeMillis());
         this.entry.text2 = var1;
      }

      this.revision++;
   }

   public void method2751(Script var1) {
      String var2 = var1 == null ? null : var1.getId();

      for (AiChatStorage.Entry var4 : this.method2744()) {
         if (var2 != null && var2.equals(var4.text2)) {
            this.method2749(var4);
            return;
         }
      }

      this.method2750(var2);
   }

   public synchronized void method2752(AiChatStorage.Entry var1) {
      if (var1 != null) {
         var1.deleted = true;
         if (this.entry2 == var1) {
            this.flag = true;
         }

         this.items.remove(var1);
         executorService.execute(() -> {
            try {
               Files.deleteIfExists(this.path.resolve(var1.text + ".json"));
            } catch (IOException var3) {
            }
         });
         ArrayList var2 = new ArrayList();

         for (AiChatStorage.Entry2 var4 : var1.items) {
            var2.addAll(var4.items);
         }

         AiImageStore.method4640(var2);
         if (this.entry == var1) {
            this.entry = new AiChatStorage.Entry(method2743(), System.currentTimeMillis());
         }

         this.revision++;
      }
   }

   public void method2753() {
      for (AiChatStorage.Entry var2 : new ArrayList<>(this.items)) {
         this.method2752(var2);
      }

      this.entry = new AiChatStorage.Entry(method2743(), System.currentTimeMillis());
      this.revision++;
   }

   public void clear() {
      this.method2750(this.entry.text2);
   }

   public void stop() {
      this.flag = true;
   }

   private void method2754(AiChatStorage.Entry var1) {
      if (!var1.deleted) {
         var1.longValue2 = System.currentTimeMillis();
         if (!this.items.contains(var1)) {
            this.items.add(var1);
         }

         while (this.items.size() > 80) {
            AiChatStorage.Entry var2 = this.items.stream().min(Comparator.comparingLong(AiChatStorage.Entry::getLongValue2)).orElse(null);
            if (var2 == null || var2 == this.entry) {
               break;
            }

            this.method2752(var2);
         }
      }
   }

   private void method2755(AiChatStorage.Entry var1) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("id", var1.text);
      var2.addProperty("createdAt", var1.longValue);
      var2.addProperty("updatedAt", var1.longValue2);
      var2.addProperty("title", var1.title);
      if (var1.text2 != null) {
         var2.addProperty("target", var1.text2);
      }

      JsonArray var3 = new JsonArray();

      for (AiChatStorage.Entry2 var5 : var1.items) {
         if (!var5.streaming()) {
            var3.add(var5.method2776());
         }
      }

      var2.add("messages", var3);
      String var6 = var2.toString();
      executorService.execute(() -> {
         try {
            Files.createDirectories(this.path);
            Files.writeString(this.path.resolve(var1.text + ".json"), var6, StandardCharsets.UTF_8);
         } catch (IOException var4) {
         }
      });
   }

   private static String method2756(String var0) {
      String var1 = var0.replace('\n', ' ').strip();
      if (var1.length() > 42) {
         int var2 = var1.lastIndexOf(32, 42);
         var1 = var1.substring(0, var2 > 20 ? var2 : 42) + "…";
      }

      return var1.isEmpty() ? "Новый чат" : Character.toUpperCase(var1.charAt(0)) + var1.substring(1);
   }

   public void method2757(Storage var1, String var2) {
      this.method2764(var1, this.entry, var2, false, List.of());
   }

   public void method2758(Storage var1, String var2, List<String> var3) {
      this.method2764(var1, this.entry, var2, false, var3);
   }

   public void method2759(String var1) {
      AiChatStorage.Entry2 var2 = new AiChatStorage.Entry2(false, var1, System.currentTimeMillis());
      var2.flag6 = true;
      this.entry.items.add(var2);
      this.method2754(this.entry);
      this.method2755(this.entry);
      this.revision++;
   }

   public Path method2760() throws IOException {
      AiChatStorage.Entry var1 = this.entry;
      StringBuilder var2 = new StringBuilder("# ").append(var1.title.isEmpty() ? "Чат" : var1.title).append("\n\n");
      SimpleDateFormat var3 = new SimpleDateFormat("dd.MM.yyyy HH:mm");

      for (AiChatStorage.Entry2 var5 : var1.items) {
         var2.append("### ")
            .append(var5.flag ? "Ты" : (var5.flag6 ? "Заметка" : (var5.text2.isEmpty() ? "AI" : var5.text2)))
            .append(" · ")
            .append(var3.format(new Date(var5.longValue)))
            .append("\n\n");

         for (String var7 : var5.items) {
            var2.append("![картинка](../images/").append(var7).append(")\n\n");
         }

         var2.append(var5.text()).append("\n\n");
         if (!var5.error.isEmpty()) {
            var2.append("> ошибка: ").append(var5.error).append("\n\n");
         }

         if (!var5.text.isEmpty()) {
            var2.append("> ").append(var5.text).append("\n\n");
         }
      }

      Path var8 = this.path.resolve("exports");
      Files.createDirectories(var8);
      String var9 = (var1.title.isEmpty() ? "chat" : var1.title).replaceAll("[^\\p{L}\\p{N} _-]", "").strip();
      if (var9.length() > 40) {
         var9 = var9.substring(0, 40).strip();
      }

      Path var10 = var8.resolve((var9.isEmpty() ? "chat" : var9) + "-" + var1.text + ".md");
      Files.writeString(var10, var2.toString(), StandardCharsets.UTF_8);
      return var10;
   }

   public void method2761(Storage var1) {
      AiChatStorage.Entry var2 = this.entry;
      if (!this.busy && !var2.items.isEmpty()) {
         int var3 = var2.items.size() - 1;
         if (!var2.items.get(var3).isFlag()) {
            var2.items.remove(var3);
            AiChatStorage.Entry2 var4 = null;

            for (int var5 = var2.items.size() - 1; var5 >= 0; var5--) {
               if (var2.items.get(var5).isFlag()) {
                  var4 = var2.items.remove(var5);
                  break;
               }
            }

            if (var4 != null) {
               this.method2764(var1, var2, var4.text(), var4.flag5, new ArrayList<>(var4.items));
            }
         }
      }
   }

   public String method2762() {
      for (int var1 = this.entry.items.size() - 1; var1 >= 0; var1--) {
         if (this.entry.items.get(var1).isFlag() && !this.entry.items.get(var1).isFlag5()) {
            return this.entry.items.get(var1).text();
         }
      }

      return "";
   }

   private void method2763(Storage var1, AiChatStorage.Entry var2, String var3, boolean var4) {
      this.method2764(var1, var2, var3, var4, List.of());
   }

   private void method2764(Storage var1, AiChatStorage.Entry var2, String var3, boolean var4, List<String> var5) {
      if (!this.busy && var3 != null && var1 != null && (!var3.isBlank() || !var5.isEmpty())) {
         AiSettingsStore var6 = AiSettingsStore.method4467();
         ShownProvider var7 = var6.getShownProvider();
         if (!var4) {
            var2.intValue = 0;
         }

         if (var2.title.isEmpty()) {
            var2.title = var3.isBlank() ? "Картинка" : method2756(var3);
         }

         AiChatStorage.Entry2 var8 = new AiChatStorage.Entry2(true, var3.strip(), System.currentTimeMillis());
         var8.flag5 = var4;
         var8.items.addAll(var5);
         var2.items.add(var8);
         AiChatStorage.Entry2 var9 = new AiChatStorage.Entry2(false, "", System.currentTimeMillis());
         var9.text2 = var7.title() + " · " + var6.method4472(var7);
         var9.flag2 = true;
         var2.items.add(var9);
         this.method2754(var2);
         this.revision++;
         if (!var6.method4471(var7)) {
            var9.flag2 = false;
            var9.error = "nokey";
            this.method2755(var2);
            this.revision++;
         } else {
            AiChatStorage.Data3 var10 = method2770(var1, var2);
            ArrayList<AiChatStorage.Data2> var11 = new ArrayList();
            var var12 = new ArrayList<>(var2.items);
            var12.remove(var12.size() - 1);
            int var13 = Math.max(0, var12.size() - 24);
            var var14 = var12.subList(var13, var12.size());

            for (int var15 = 0; var15 < var14.size(); var15++) {
               AiChatStorage.Entry2 var16 = (AiChatStorage.Entry2)var14.get(var15);
               if (!var16.flag6 && (var16.flag || var16.error.isEmpty() || !var16.text().isEmpty())) {
                  List<String> var17 = var16.flag && !var16.items.isEmpty() && var14.size() - var15 <= 8 ? (List<String>)(List)List.copyOf(var16.items) : List.<String>of();
                  var11.add(new AiChatStorage.Data2(var16.flag, var16.text(), var17));
               }
            }

            String var18 = var6.method4469(var7);
            String var19 = var6.method4472(var7);
            String var20 = var6.method4474(var7);
            this.busy = true;
            this.flag = false;
            this.entry2 = var2;
            Thread.ofPlatform().daemon(true).name("zenith-ai").start(() -> {
               try {
                  String var10x = method2771(var10);
                  ArrayList var11x = new ArrayList();
                  int var12x = 6;

                  for (Object var14x_o : (Iterable<?>)(Object)(var11)) {
                     AiChatStorage.Data2 var14x = (AiChatStorage.Data2)var14x_o;
                     ArrayList var15x = new ArrayList();
                     var var16x = var14x.getItems().iterator();

                     while (true) {
                        if (var16x.hasNext()) {
                           String var17x = (String)var16x.next();
                           if (var12x > 0) {
                              try {
                                 var15x.add(AiImageStore.method4636(var17x));
                                 var12x--;
                              } catch (IOException var30) {
                              }
                              continue;
                           }
                        }

                        if (!var14x.text().isBlank() || !var15x.isEmpty()) {
                           var11x.add(new AiStreamClient.Data(var14x.isFlag(), var14x.text(), var15x));
                        }
                        break;
                     }
                  }

                  while (!var11x.isEmpty() && !((AiStreamClient.Data)var11x.get(0)).isFlag()) {
                     var11x.remove(0);
                  }

                  if (!this.flag) {
                     AiStreamClient.method4739(var7, var18, var19, var20, var10x, var11x, var2xx -> {
                        var9.method2773(var2xx);
                        this.revision++;
                     }, () -> this.flag);
                  }

                  if (this.flag && var9.text().isEmpty()) {
                     var9.error = "остановлено";
                  }
               } catch (Exception var32) {
                  var9.error = var32.getMessage() == null ? var32.getClass().getSimpleName() : var32.getMessage();
               } finally {
                  var9.flag2 = false;
                  this.busy = false;
                  this.entry2 = null;
                  synchronized (this) {
                     if (!var2.deleted) {
                        this.method2754(var2);
                        this.method2755(var2);
                     }
                  }

                  this.revision++;
                  if (!this.flag && !var2.deleted && var9.error.isEmpty() && !var9.method2775().isEmpty() && AiSettingsStore.method4467().isFlag2()) {
                     MinecraftClient.getInstance().execute(() -> {
                        if (!var2.deleted) {
                           this.method2767(var1, var9);
                        }
                     });
                  }
               }
            });
         }
      }
   }

   public static List<AiChatStorage.Data> method2765(String var0) {
      ArrayList<AiChatStorage.Data> var1 = new ArrayList<>();
      Matcher var2 = pattern.matcher(var0);

      while (var2.find()) {
         String var3 = var2.group(1).strip().replace('\\', '/');

         while (var3.startsWith("./") || var3.startsWith("/")) {
            var3 = var3.substring(var3.startsWith("./") ? 2 : 1);
         }

         if (!var3.isEmpty() && !var3.contains("..")) {
            String var4 = var2.group(2);
            String var5 = var3;
            var1.removeIf(var1x -> var1x.getText().equals(var5));
            var1.add(new AiChatStorage.Data(var5, var4.endsWith("\n") ? var4 : var4 + "\n"));
         }
      }

      return var1;
   }

   private AiChatStorage.Entry method2766(AiChatStorage.Entry2 var1) {
      for (AiChatStorage.Entry var3 : this.items) {
         if (var3.items.contains(var1)) {
            return var3;
         }
      }

      return this.entry;
   }

   public void method2767(Storage var1, AiChatStorage.Entry2 var2) {
      List<AiChatStorage.Data> var3 = var2.method2775();
      if (!var3.isEmpty() && var1 != null) {
         AiChatStorage.Entry var4 = this.method2766(var2);

         try {
            Script var5 = var4.text2 == null ? null : var1.method1294(var4.text2);
            if (var5 == null) {
               String var6 = "ai-script";

               for (Object var8_o : (Iterable<?>)(Object)(var3)) {
                  AiChatStorage.Data var8 = (AiChatStorage.Data)var8_o;
                  Matcher var9 = pattern2.matcher(var8.getText2());
                  if (var8.getText().endsWith("main.js") && var9.find()) {
                     var6 = var9.group(1).strip();
                     break;
                  }
               }

               String var12 = Storage.method1303(var6);
               String var15 = var12;

               for (int var17 = 2; var1.method1294(var15) != null || Files.exists(var1.getPath().resolve(var15)); var17++) {
                  var15 = var12 + "_" + var17;
               }

               var5 = var1.method1340(var15, "empty");
               if (var5 == null) {
                  throw new IOException("не удалось создать скрипт");
               }

               var4.text2 = var5.getId();
            }

            int var11 = 0;

            for (Object var16_o : (Iterable<?>)(Object)(var3)) {
               AiChatStorage.Data var16 = (AiChatStorage.Data)var16_o;
               Path var18 = var5.getPath() == null && var16.getText().endsWith(".js") ? var5.getPath2() : var1.method1346(var5, var16.getText());
               var1.method1347(var5, var18, var16.getText2());
               var11++;
            }

            if (!var1.method1297(var5)) {
               var1.method1298(var5, true);
            } else {
               var1.method1299(var5);
            }

            var2.flag4 = true;
            String var14 = var5.getError();
            if (var14 != null && !var14.isBlank()) {
               var2.text = "записал " + method2768(var11) + ", но скрипт упал: " + var14;
               var2.flag3 = true;
               if (AiSettingsStore.method4467().isFlag3() && var4.intValue < 2) {
                  var4.intValue++;
                  this.method2755(var4);
                  this.revision++;
                  this.method2763(var1, var4, "Скрипт после применения упал с ошибкой:\n" + var14 + "\nИсправь и пришли исправленные файлы целиком.", true);
                  return;
               }
            } else {
               var2.text = "готово: " + method2768(var11) + " → «" + var5.method1403() + "» запущен";
               var2.flag3 = false;
            }
         } catch (Exception var10) {
            var2.text = "не применилось: " + var10.getMessage();
            var2.flag3 = true;
         }

         this.method2755(var4);
         this.revision++;
      }
   }

   private static String method2768(int var0) {
      int var1 = var0 % 10;
      int var2 = var0 % 100;
      String var3 = var1 == 1 && var2 != 11 ? "файл" : (var1 < 2 || var1 > 4 || var2 >= 12 && var2 <= 14 ? "файлов" : "файла");
      return var0 + " " + var3;
   }

   public void method2769(Storage var1) {
      Script var2 = this.entry.text2 == null ? null : var1.method1294(this.entry.text2);
      if (var2 != null) {
         StringBuilder var3 = new StringBuilder("Вот что пишет скрипт:\n");
         if (!var2.getError().isBlank()) {
            var3.append("Ошибка: ").append(var2.getError()).append('\n');
         }

         List<Storage.Data2> var4 = var1.method1349(var2);

         for (Object var6_o : (Iterable<?>)(Object)(var4.subList(Math.max(0, var4.size() - 15), var4.size()))) {
            Storage.Data2 var6 = (Storage.Data2)var6_o;
            var3.append('[').append(var6.getText2()).append("] ").append(var6.text()).append('\n');
         }

         var3.append("Разберись и исправь.");
         this.method2757(var1, var3.toString());
      }
   }

   private static AiChatStorage.Data3 method2770(Storage var0, AiChatStorage.Entry var1) {
      Path var2 = var0.getPath().resolve("API.md");

      Map var3;
      try {
         var3 = var0.method1343();
      } catch (RuntimeException var8) {
         var3 = Map.of();
      }

      String var4 = ScriptHeaderParser.method1000("hud", "Пример");
      Script var5 = var1.text2 == null ? null : var0.method1294(var1.text2);
      if (var5 == null) {
         return new AiChatStorage.Data3(var2, var3, var4, null, "", null, false, List.of(), List.of());
      } else {
         String var6 = "# Текущий скрипт: "
            + var5.method1403()
            + " (папка "
            + var5.getId()
            + ")\nСостояние: "
            + var5.getType2()
            + (var5.getError().isBlank() ? "" : ", ошибка: " + var5.getError())
            + "\n\n";
         Path var7 = var5.getPath() == null ? var5.getPath2().getParent() : var5.getPath();
         return new AiChatStorage.Data3(var2, var3, var4, var5, var6, var7, var5.getPath() == null, var0.method1345(var5), var0.method1349(var5));
      }
   }

   private static String method2771(AiChatStorage.Data3 var0) {
      StringBuilder var1 = new StringBuilder();
      var1.append(
         "Ты — встроенный помощник Zenith Client по JS-скриптам для Minecraft. Общайся как друг: просто, на «ты», без канцелярита.\nИгрок описывает, что хочет, — ты делаешь рабочий скрипт. Если просьба шуточная или странная — всё равно делай, если это можно сделать API.\nОтвечай по-русски и коротко: одна-две фразы, что сделал и КАК ВКЛЮЧИТЬ (в какой категории меню лежит модуль, какой бинд), потом файлы.\nЕсли чего-то в API нет — честно скажи и предложи ближайший вариант. Уточняющий вопрос задавай, только если без него никак.\n\nИгрок может прикладывать картинки (скриншоты игры, рисунки, фото): внимательно посмотри на них и учитывай —\nнапример, «построй как на скрине» значит повторить форму с картинки блок в блок.\n\nКак устроено:\n- Переключатель скрипта в Scripts только запускает скрипт. Модуль из Module(...) появляется в меню клиента в своей категории\n  (combat/movement/render/player/misc) и включается там отдельно или биндом. Если игрок хочет «сразу работало» — enabled: true.\n- Движок Rhino (ES6) в песочнице: нет Java, import, fetch, DOM, Promise-API браузера. Есть setTimeout/setInterval, include(),\n  require(\"id\") для чужих exports и все объекты из справочника ниже. Используй var/let/const, стрелочные функции можно.\n\nПравила кода:\n- Каждый изменённый или новый файл присылай ЦЕЛИКОМ в отдельном блоке ровно такого вида:\n```file:main.js\n...весь код файла...\n```\n- Путь — относительно папки скрипта. Главный файл — main.js, остальные подключаются через include(\"имя.js\").\n- main.js начинается с шапки: // @name, // @author, // @version, // @description, // @api 3 (можно // @tags a,b; для скриптов в маркет — // @permissions и // @net).\n- Для http.* домены обязательно перечисли в шапке: // @net example.com, api.site.org — иначе запрос отклонится.\n- Если правишь существующий скрипт — сохрани его имя и поднимай @version. Не присылай файлы, которые не менялись.\n- Если просят объяснить или спрашивают — отвечай без блоков файлов.\n\nЧастые ошибки — не делай так:\n- Сущности (player, entity, world.players()) отдают значения МЕТОДАМИ: player.x(), e.health(), e.name(). Не e.x.\n- Предметы, блоки, эффекты — снимки с ПОЛЯМИ: item.id, item.count, item.block, block.air, block.id. Не item.id().\n- world.block(x,y,z) и world.blockAt(x,y,z) возвращают объект блока (block.air, block.solid, block.id); world.blockId(...) — строку id.\n- player.place(x,y,z) ставит блок из руки В точку и требует опору рядом (земля снизу или стена сбоку): строй снизу вверх,\n  первый слой на уровне ног Math.floor(player.y()), не выше 5 блоков от игрока. place возвращает true/false — проверяй.\n- Блок в руку: var i = player.inventory.findBlock(); if (i >= 0) player.inventory.toHotbar(i); — индексы инвентаря 0-35 (0-8 хотбар).\n- Долгие действия (постройка, копание) делай по одному шагу за тик в events.update с задержкой, а не циклом за один вызов:\n  у одного вызова лимит 250 мс, и сервер не примет 50 блоков за тик.\n- Модуль: читай настройки через .get(), проверяй mod.enabled() в обработчиках, в mod.onEnable готовь состояние.\n- render.* работает только внутри events.render2d, render3d.* — только внутри events.render3d.\n- Цвета — 0xAARRGGBB, все 8 цифр (0xFFFF0000 — красный).\n- Свои картинки и звуки игрок кладёт в папку скрипта (images/, sounds/); если их нет — используй ванильные звуки,\n  particles.text/burst с символами и image.create.\n\n"
      );

      try {
         Path var2 = var0.getPath();
         if (Files.isRegularFile(var2)) {
            var1.append("# Справочник API\n\n").append(Files.readString(var2, StandardCharsets.UTF_8)).append("\n\n");
         }
      } catch (IOException var11) {
      }

      var var12 = var0.getItems();
      if (!var12.isEmpty()) {
         var1.append("# Точный список глобалов и их членов (из движка — используй только их)\n");

         for (Object var4_o : (Iterable<?>)(Object)(var12.entrySet())) {
            Map.Entry var4 = (Map.Entry)var4_o;
            var1.append((String)var4.getKey()).append(": ").append(String.join(", ", (Iterable<? extends CharSequence>)var4.getValue())).append('\n');
         }

         var1.append('\n');
      }

      var1.append("# Пример рабочего скрипта\n```file:main.js\n").append(var0.getText()).append("```\n\n");
      if (var0.script() == null) {
         var1.append("# Текущий скрипт\nПока нет: игрок создаёт новый. Придумай короткое понятное @name.\n");
         return var1.toString();
      } else {
         var1.append(var0.getText2());
         int var13 = 0;
         int var14 = 0;
         Path var5 = var0.getPath2();

         for (Path var7 : var0.getItems2()) {
            if (var14++ >= 40) {
               break;
            }

            try {
               String var8 = Files.readString(var7, StandardCharsets.UTF_8);
               if (var13 + var8.length() > 160000) {
                  var1.append("(файл ").append(var5.relativize(var7)).append(" не показан — лимит контекста)\n");
               } else {
                  var13 += var8.length();
                  String var9 = var0.isFlag() ? "main.js" : var5.relativize(var7).toString().replace('\\', '/');
                  var1.append("```file:").append(var9).append('\n').append(var8).append(var8.endsWith("\n") ? "" : "\n").append("```\n\n");
               }
            } catch (IOException var10) {
            }
         }

         List<Storage.Data2> var15 = var0.getItems3();
         if (!var15.isEmpty()) {
            var1.append("# Последние строки журнала скрипта\n");

            for (Object var17_o : (Iterable<?>)(Object)(var15.subList(Math.max(0, var15.size() - 10), var15.size()))) {
               Storage.Data2 var17 = (Storage.Data2)var17_o;
               var1.append('[').append(var17.getText2()).append("] ").append(var17.text()).append('\n');
            }
         }

         return var1.toString();
      }
   }

   public static final class Data {
      private final String text;
      private final String text2;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }

      public Data(String var1, String var2) {
         this.text = var1;
         this.text2 = var2;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }
   }

   private static final class Data2 {
      private final boolean flag;
      private final String text;
      private final List<String> items;

      public boolean flag() {
         return this.flag;
      }

      public String text() {
         return this.text;
      }

      public List<String> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, text, items);
      }

      @Override
      public String toString() {
         return "Data2[" + "flag=" + this.flag + ", " + "text=" + this.text + ", " + "items=" + this.items + "]";
      }

      private Data2(boolean var1, String var2, List<String> var3) {
         this.flag = var1;
         this.text = var2;
         this.items = var3;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public List<String> getItems() {
         return this.items;
      }
   }

   private static final class Data3 {
      private final Path path;
      private final Map<String, List<String>> items;
      private final String text;
      private final Script script;
      private final String text2;
      private final Path path2;
      private final boolean flag;
      private final List<Path> items2;
      private final List<Storage.Data2> items3;

      public Path path() {
         return this.path;
      }

      public Map<String, List<String>> items() {
         return this.items;
      }

      public String text() {
         return this.text;
      }

      public Script script() {
         return this.script;
      }

      public String text2() {
         return this.text2;
      }

      public Path path2() {
         return this.path2;
      }

      public boolean flag() {
         return this.flag;
      }

      public List<Path> items2() {
         return this.items2;
      }

      public List<Storage.Data2> items3() {
         return this.items3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.path, other.path) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.script, other.script) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.path2, other.path2) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.items2, other.items2) && java.util.Objects.equals(this.items3, other.items3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(path, items, text, script, text2, path2, flag, items2, items3);
      }

      @Override
      public String toString() {
         return "Data3[" + "path=" + this.path + ", " + "items=" + this.items + ", " + "text=" + this.text + ", " + "script=" + this.script + ", " + "text2=" + this.text2 + ", " + "path2=" + this.path2 + ", " + "flag=" + this.flag + ", " + "items2=" + this.items2 + ", " + "items3=" + this.items3 + "]";
      }

      private Data3(
         Path var1, Map<String, List<String>> var2, String var3, Script var4, String var5, Path var6, boolean var7, List<Path> var8, List<Storage.Data2> var9
      ) {
         this.path = var1;
         this.items = var2;
         this.text = var3;
         this.script = var4;
         this.text2 = var5;
         this.path2 = var6;
         this.flag = var7;
         this.items2 = var8;
         this.items3 = var9;
      }

      public Path getPath() {
         return this.path;
      }

      public Map<String, List<String>> getItems() {
         return this.items;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public Path getPath2() {
         return this.path2;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public List<Path> getItems2() {
         return this.items2;
      }

      public List<Storage.Data2> getItems3() {
         return this.items3;
      }
   }

   public static final class Entry {
      private final String text;
      private final long longValue;
      private volatile long longValue2;
      private volatile String title = "";
      private volatile String text2;
      private final List<AiChatStorage.Entry2> items = new CopyOnWriteArrayList<>();
      private int intValue;
      private volatile boolean deleted;

      private Entry(String var1, long var2) {
         this.text = var1;
         this.longValue = var2;
         this.longValue2 = var2;
      }

      public String getText() {
         return this.text;
      }

      public String title() {
         return this.title;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String getText2() {
         return this.text2;
      }

      public List<AiChatStorage.Entry2> getItems() {
         return this.items;
      }

      public boolean empty() {
         return this.items.isEmpty();
      }

      public String method2772() {
         for (int var1 = this.items.size() - 1; var1 >= 0; var1--) {
            AiChatStorage.Entry2 var2 = this.items.get(var1);
            String var3 = var2.method2774();
            if (!var3.isEmpty()) {
               return (var2.isFlag() ? "ты: " : "") + var3;
            }
         }

         return "";
      }
   }

   public static final class Entry2 {
      private final boolean flag;
      private final StringBuilder stringBuilder = new StringBuilder();
      private final long longValue;
      private volatile boolean flag2;
      private volatile String error = "";
      private volatile String text = "";
      private volatile boolean flag3;
      private volatile boolean flag4;
      private volatile boolean flag5;
      private volatile String text2 = "";
      private volatile boolean flag6;
      private final List<String> items = new CopyOnWriteArrayList<>();
      private String text3;
      private String text4;
      private int intValue = -1;
      private List<AiChatStorage.Data> files = List.of();
      private int intValue2 = -1;

      private Entry2(boolean var1, String var2, long var3) {
         this.flag = var1;
         this.stringBuilder.append(var2);
         this.longValue = var3;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public synchronized String text() {
         if (this.text3 == null) {
            this.text3 = this.stringBuilder.toString();
         }

         return this.text3;
      }

      private synchronized void method2773(String var1) {
         this.stringBuilder.append(var1);
         this.text3 = null;
      }

      private synchronized String method2774() {
         if (this.intValue != this.stringBuilder.length()) {
            this.text4 = AiChatStorage.pattern3.matcher(this.stringBuilder).replaceAll(" [код] ").replace('\n', ' ').strip();
            this.intValue = this.stringBuilder.length();
         }

         return this.text4;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public boolean streaming() {
         return this.flag2;
      }

      public String error() {
         return this.error;
      }

      public String result() {
         return this.text;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public boolean isFlag4() {
         return this.flag4;
      }

      public boolean isFlag5() {
         return this.flag5;
      }

      public String getText2() {
         return this.text2;
      }

      public List<String> getItems() {
         return this.items;
      }

      public boolean note() {
         return this.flag6;
      }

      public List<AiChatStorage.Data> method2775() {
         String var1;
         synchronized (this) {
            if (this.intValue2 == this.stringBuilder.length()) {
               return this.files;
            }

            var1 = this.text();
         }

         var var2 = List.copyOf(AiChatStorage.method2765(var1));
         synchronized (this) {
            this.files = var2;
            this.intValue2 = var1.length();
            return var2;
         }
      }

      private JsonObject method2776() {
         JsonObject var1 = new JsonObject();
         var1.addProperty("user", this.flag);
         var1.addProperty("text", this.text());
         var1.addProperty("at", this.longValue);
         if (!this.error.isEmpty()) {
            var1.addProperty("error", this.error);
         }

         if (!this.text.isEmpty()) {
            var1.addProperty("result", this.text);
         }

         if (this.flag3) {
            var1.addProperty("resultError", true);
         }

         if (this.flag4) {
            var1.addProperty("applied", true);
         }

         if (this.flag5) {
            var1.addProperty("automatic", true);
         }

         if (!this.text2.isEmpty()) {
            var1.addProperty("provider", this.text2);
         }

         if (this.flag6) {
            var1.addProperty("note", true);
         }

         if (!this.items.isEmpty()) {
            JsonArray var2 = new JsonArray();
            this.items.forEach(var2::add);
            var1.add("images", var2);
         }

         return var1;
      }

      private static AiChatStorage.Entry2 method2777(JsonObject var0) {
         AiChatStorage.Entry2 var1 = new AiChatStorage.Entry2(var0.get("user").getAsBoolean(), var0.get("text").getAsString(), var0.get("at").getAsLong());
         if (var0.has("error")) {
            var1.error = var0.get("error").getAsString();
         }

         if (var0.has("result")) {
            var1.text = var0.get("result").getAsString();
         }

         var1.flag3 = var0.has("resultError") && var0.get("resultError").getAsBoolean();
         var1.flag4 = var0.has("applied") && var0.get("applied").getAsBoolean();
         var1.flag5 = var0.has("automatic") && var0.get("automatic").getAsBoolean();
         if (var0.has("provider")) {
            var1.text2 = var0.get("provider").getAsString();
         }

         var1.flag6 = var0.has("note") && var0.get("note").getAsBoolean();
         if (var0.has("images")) {
            for (JsonElement var3 : var0.getAsJsonArray("images")) {
               var1.items.add(var3.getAsString());
            }
         }

         return var1;
      }
   }
}

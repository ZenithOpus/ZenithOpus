package org.zenith.client.scripting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Base64;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.Zenith;
import org.zenith.utility.api.AiSettingsStore;
import org.zenith.utility.api.AiStreamClient;
import org.zenith.utility.api.OriginUtil;
import org.zenith.utility.api.StoreApi;
import org.zenith.utility.misc.ScriptPermissions;
import org.zenith.utility.misc.ShownProvider;

public final class ScriptRequire {
   private static final int CONST_intValue = 10;
   private static final Map<String, Deque<Long>> items = new ConcurrentHashMap<>();
   private static final int CONST_intValue2 = 150;
   private static final Map<String, long[]> items2 = new ConcurrentHashMap<>();

   private ScriptRequire() {
   }

   public static void method4889(Storage var0, Script var1, ScriptableObject var2) {
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6246(var2, var2);
      var3.method6277("exports", ScriptValueUtils.method6247(var2).getScriptableObject());
      var3.method6274("require", var3x -> {
         String var4 = ScriptValueUtils.method6254(var3x, 0, "");
         Script var5 = var0.method1294(var4);
         if (var5 == null) {
            throw ScriptValueUtils.method6251("скрипт «" + var4 + "» не установлен");
         } else if (var5 == var1) {
            throw ScriptValueUtils.method6251("нельзя подключить самого себя");
         } else if (!var5.isRunning()) {
            throw ScriptValueUtils.method6251("скрипт «" + var5.method1403() + "» выключен — включи его");
         } else {
            Scriptable var6 = var5.method1411();
            if (var6 == null) {
               throw ScriptValueUtils.method6251("скрипт «" + var5.method1403() + "» ничего не экспортирует");
            } else {
               return method4890(var0, var2, var5, var6, 0);
            }
         }
      });
      var3.method6277("i18n", method4893(var1, var2));
      var3.method6277("anim", method4895(var2));
      var3.method6277("util", method4896(var2));
      var3.method6277("ai", method4897(var1, var0, var2));
   }

   private static Object method4890(Storage var0, Scriptable var1, Script var2, Object var3, int var4) {
      if (var4 > 6 || ScriptValueUtils.method6253(var3)) {
         return null;
      } else if (var3 instanceof Function var15) {
         return ScriptValueUtils.method6249(var1, "exported", var4x -> {
            if (var2.isRunning() && var2.getScope() != null) {
               ScriptableObject var5x = var2.getScope();
               Object[] var6x = new Object[var4x.length];

               for (int var7x = 0; var7x < var4x.length; var7x++) {
                  var6x[var7x] = var4x[var7x] instanceof Function ? null : ScriptValueUtils.method6273(var5x, ScriptValueUtils.method6271(var4x[var7x], 0));
               }

               Object var8 = var0.method1321(var2, var15, var6x);
               return var8 instanceof Function ? null : ScriptValueUtils.method6273(var1, ScriptValueUtils.method6271(var8, 0));
            } else {
               throw ScriptValueUtils.method6251("скрипт «" + var2.method1403() + "» выключен");
            }
         });
      } else if (var3 instanceof Scriptable var5 && !(var3 instanceof NativeArray)) {
         ScriptValueUtils.Entry2 var6 = ScriptValueUtils.method6247(var1);
         Object[] var7 = var5.getIds();
         if (var7.length > 2000) {
            throw ScriptValueUtils.method6251("exports больше 2000 ключей");
         } else {
            for (Object var11 : var7) {
               String var12 = ScriptValueUtils.method6255(var11);
               Object var13 = var11 instanceof Number var14 ? var5.get(var14.intValue(), var5) : var5.get(var12, var5);
               var6.method6277(var12, method4890(var0, var1, var2, var13, var4 + 1));
            }

            return var6.getScriptableObject();
         }
      } else {
         return ScriptValueUtils.method6273(var1, ScriptValueUtils.method6271(var3, 0));
      }
   }

   public static void method4891(Storage var0, Script var1, Scriptable var2, ScriptableObject var3) {
      ScriptValueUtils.Entry2 var4 = ScriptValueUtils.method6246(var2, var3);
      var4.method6274(
         "stats",
         var2x -> {
            long[] var3x = var1.method1409();
            double var4x = Math.max(0.001, (System.currentTimeMillis() - var3x[3]) / 1000.0);
            return ScriptValueUtils.method6247(var2)
               .method6277("totalMs", var3x[0] / 1000000.0)
               .method6277("calls", (double)var3x[1])
               .method6277("avgMs", var3x[1] == 0L ? 0.0 : var3x[0] / 1000000.0 / var3x[1])
               .method6277("peakMs", var3x[2] / 1000000.0)
               .method6277("msPerSecond", var3x[0] / 1000000.0 / var4x)
               .method6277("seconds", var4x)
               .getScriptableObject();
         }
      );
      var4.method6274("resetStats", var1x -> {
         var1.method1410();
         return null;
      });
      var4.method6274("dir", var1x -> var1.getPath() == null ? "" : var1.getId());
      var4.method6274(
         "checkUpdate",
         var3x -> {
            var var4x = ScriptValueUtils.method6260(var3x, 0);
            OriginUtil.Data2 var5 = OriginUtil.method6921(var1);
            StoreApi var6 = Zenith.getInstance().getStoreApi();
            if (var5 != null && var6 != null) {
               method4892(
                  var6,
                  var5,
                  var5x -> MinecraftClient.getInstance()
                     .execute(
                        () -> {
                           if (var1.isRunning()) {
                              ScriptValueUtils.Entry2 var6x = ScriptValueUtils.method6247(var2);
                              if (var5x == null) {
                                 var6x.method6277("available", false).method6277("reason", "запись не найдена в маркете");
                              } else {
                                 var6x.method6277("available", !var5.getText().equals(var5x.getText6()))
                                    .method6277("version", OriginUtil.method6916(var5x).version())
                                    .method6277("revision", (double)var5x.getLongValue2());
                              }

                              var0.method1321(var1, var4x, var6x.getScriptableObject());
                           }
                        }
                     )
               );
               return null;
            } else {
               RemoteUtil.method1847(
                  var0,
                  var1,
                  var4x,
                  ScriptValueUtils.method6247(var2).method6277("available", false).method6277("reason", "скрипт не из маркета").getScriptableObject()
               );
               return null;
            }
         }
      );
      var4.method6274("update", var2x -> {
         OriginUtil.Data2 var3x = OriginUtil.method6921(var1);
         StoreApi var4x = Zenith.getInstance().getStoreApi();
         if (var3x != null && var4x != null) {
            method4892(var4x, var3x, var4xx -> {
               if (var4xx != null && !var3x.getText().equals(var4xx.getText6())) {
                  var4x.method1970(var4xx.getUUID()).whenComplete((var3xxx, var4xxx) -> {
                     if (var4xxx == null) {
                        MinecraftClient.getInstance().execute(() -> {
                           try {
                              OriginUtil.method6928(var0, var4xx, var3xxx);
                           } catch (Exception var5) {
                              var0.method1327(var1, "error", "обновление не удалось: " + var5.getMessage());
                           }
                        });
                     }
                  });
               }
            });
            return null;
         } else {
            throw ScriptValueUtils.method6251("скрипт не из маркета");
         }
      });
   }

   private static void method4892(StoreApi var0, OriginUtil.Data2 var1, Consumer<StoreApi.Data2> var2) {
      var0.method1969("PUBLIC", "script").whenComplete((var3, var4) -> {
         if (var3 != null) {
            for (StoreApi.Data2 var6 : var3) {
               if (var6.getUUID().equals(var1.getUUID())) {
                  var2.accept(var6);
                  return;
               }
            }
         }

         var0.method1969("LIBRARY", "script").whenComplete((var2xx, var3x) -> {
            if (var2xx != null) {
               for (StoreApi.Data2 var5 : var2xx) {
                  if (var5.getUUID().equals(var1.getUUID())) {
                     var2.accept(var5);
                     return;
                  }
               }
            }

            var2.accept(null);
         });
      });
   }

   private static Scriptable method4893(Script var0, Scriptable var1) {
      HashMap var2 = new HashMap();
      java.util.function.Function var3 = var2x -> var2.computeIfAbsent(var2x, var1xx -> {
         if (var0.getPath() == null) {
            return new JsonObject();
         } else {
            Path var2xx = var0.getPath().resolve("lang").resolve(var1xx + ".json");

            try {
               if (Files.isRegularFile(var2xx) && Files.size(var2xx) < 524288L) {
                  JsonElement var3x = JsonParser.parseString(Files.readString(var2xx, StandardCharsets.UTF_8));
                  if (var3x.isJsonObject()) {
                     return var3x.getAsJsonObject();
                  }
               }
            } catch (Exception var4x) {
            }

            return new JsonObject();
         }
      });
      ScriptValueUtils.Entry2 var4 = ScriptValueUtils.method6247(var1);
      var4.method6274("lang", var0x -> Zenith.getInstance().getLocalization().getLanguageName().toLowerCase(Locale.ROOT));
      var4.method6274("t", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "");
         String var3x = Zenith.getInstance().getLocalization().getLanguageName().toLowerCase(Locale.ROOT);
         String var4x = null;

         for (String var8 : new String[]{var3x, "en", "ru"}) {
            JsonObject var9 = (JsonObject)var3.apply(var8);
            if (var9.has(var2x) && var9.get(var2x).isJsonPrimitive()) {
               var4x = var9.get(var2x).getAsString();
               break;
            }
         }

         Scriptable var10 = ScriptValueUtils.method6262(var1x, 1);
         if (var4x == null) {
            var4x = var10 == null ? ScriptValueUtils.method6254(var1x, 1, var2x) : var2x;
         }

         if (var10 != null) {
            for (Object var14 : var10.getIds()) {
               var4x = var4x.replace("{" + var14 + "}", ScriptValueUtils.method6255(var10.get(String.valueOf(var14), var10)));
            }
         }

         return var4x;
      });
      return var4.getScriptableObject();
   }

   public static double method4894(String var0, double var1) {
      var1 = Math.max(0.0, Math.min(1.0, var1));
      String var3 = var0.toLowerCase(Locale.ROOT);

      return switch (var3) {
         case "in", "quadin" -> var1 * var1;
         case "out", "quadout" -> 1.0 - (1.0 - var1) * (1.0 - var1);
         case "inout", "quadinout" -> var1 < 0.5 ? 2.0 * var1 * var1 : 1.0 - Math.pow(-2.0 * var1 + 2.0, 2.0) / 2.0;
         case "cubicout" -> 1.0 - Math.pow(1.0 - var1, 3.0);
         case "cubicin" -> var1 * var1 * var1;
         case "expoout" -> var1 == 1.0 ? 1.0 : 1.0 - Math.pow(2.0, -10.0 * var1);
         case "backout" -> {
            double var13 = 1.70158;
            double var14 = var13 + 1.0;
            yield 1.0 + var14 * Math.pow(var1 - 1.0, 3.0) + var13 * Math.pow(var1 - 1.0, 2.0);
         }
         case "elastic", "elasticout" -> var1 != 0.0 && var1 != 1.0
            ? Math.pow(2.0, -10.0 * var1) * Math.sin((var1 * 10.0 - 0.75) * (Math.PI * 2.0 / 3.0)) + 1.0
            : var1;
         case "bounce", "bounceout" -> {
            double var5 = 7.5625;
            double var7 = 2.75;
            if (var1 < 1.0 / var7) {
               yield var5 * var1 * var1;
            } else if (var1 < 2.0 / var7) {
               var1 -= 1.5 / var7;
               yield var5 * var1 * var1 + 0.75;
            } else if (var1 < 2.5 / var7) {
               var1 -= 2.25 / var7;
               yield var5 * var1 * var1 + 0.9375;
            } else {
               var1 -= 2.625 / var7;
               yield var5 * var1 * var1 + 0.984375;
            }
         }
         default -> var1;
      };
   }

   private static Scriptable method4895(Scriptable var0) {
      HashMap var1 = new HashMap();
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6247(var0);
      var2.method6274("to", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "");
         double var3 = ScriptValueUtils.method6256(var1x, 1, 0.0);
         double var5 = Math.max(1.0, ScriptValueUtils.method6256(var1x, 2, 250.0));
         String var7 = ScriptValueUtils.method6254(var1x, 3, "cubicOut");
         long var8 = System.currentTimeMillis();
         double[] var10 = ((double[])var1.computeIfAbsent(var2x, var4 -> new double[]{var3, var3, var3, var8}));
         double var11 = var10[0] + (var10[1] - var10[0]) * method4894(var7, (var8 - var10[3]) / var5);
         if (var10[1] != var3) {
            var10[0] = var11;
            var10[1] = var3;
            var10[3] = var8;
         }

         var10[2] = var10[0] + (var10[1] - var10[0]) * method4894(var7, (var8 - var10[3]) / var5);
         return var10[2];
      });
      var2.method6274("ease", var0x -> method4894(ScriptValueUtils.method6254(var0x, 0, "linear"), ScriptValueUtils.method6256(var0x, 1, 0.0)));
      var2.method6274(
         "pulse", var0x -> 0.5 + 0.5 * Math.sin(System.currentTimeMillis() / Math.max(1.0, ScriptValueUtils.method6256(var0x, 0, 500.0)) * Math.PI)
      );
      var2.method6274("reset", var1x -> {
         var1.remove(ScriptValueUtils.method6254(var1x, 0, ""));
         return null;
      });
      return var2.getScriptableObject();
   }

   private static Scriptable method4896(Scriptable var0) {
      ScriptValueUtils.Entry2 var1 = ScriptValueUtils.method6247(var0);
      var1.method6274(
         "lerp",
         var0x -> ScriptValueUtils.method6256(var0x, 0, 0.0)
            + (ScriptValueUtils.method6256(var0x, 1, 0.0) - ScriptValueUtils.method6256(var0x, 0, 0.0)) * ScriptValueUtils.method6256(var0x, 2, 0.0)
      );
      var1.method6274(
         "clamp",
         var0x -> Math.max(
            ScriptValueUtils.method6256(var0x, 1, 0.0), Math.min(ScriptValueUtils.method6256(var0x, 2, 1.0), ScriptValueUtils.method6256(var0x, 0, 0.0))
         )
      );
      var1.method6274(
         "random",
         var0x -> ScriptValueUtils.method6256(var0x, 0, 0.0)
            + Math.random() * (ScriptValueUtils.method6256(var0x, 1, 1.0) - ScriptValueUtils.method6256(var0x, 0, 0.0))
      );
      var1.method6274(
         "randomInt",
         var0x -> (double)(
            (int)Math.floor(
               ScriptValueUtils.method6256(var0x, 0, 0.0)
                  + Math.random() * (ScriptValueUtils.method6256(var0x, 1, 1.0) - ScriptValueUtils.method6256(var0x, 0, 0.0) + 1.0)
            )
         )
      );
      var1.method6274("pick", var0x -> {
         var var1x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var0x, 0));
         return var1x.isEmpty() ? null : var1x.get((int)(Math.random() * var1x.size()));
      });
      var1.method6274("now", var0x -> (double)System.currentTimeMillis());
      var1.method6274("format", var0x -> {
         double var1x = ScriptValueUtils.method6256(var0x, 0, 0.0);
         int var3 = Math.max(0, Math.min(6, ScriptValueUtils.method6257(var0x, 1, 1)));
         return String.format(Locale.ROOT, "%." + var3 + "f", var1x);
      });
      var1.method6274("time", var0x -> new SimpleDateFormat(ScriptValueUtils.method6254(var0x, 0, "HH:mm:ss")).format(new Date()));
      var1.method6274("distance", var0x -> {
         double var1x = ScriptValueUtils.method6256(var0x, 0, 0.0) - ScriptValueUtils.method6256(var0x, 3, 0.0);
         double var3 = ScriptValueUtils.method6256(var0x, 1, 0.0) - ScriptValueUtils.method6256(var0x, 4, 0.0);
         double var5 = ScriptValueUtils.method6256(var0x, 2, 0.0) - ScriptValueUtils.method6256(var0x, 5, 0.0);
         return Math.sqrt(var1x * var1x + var3 * var3 + var5 * var5);
      });
      var1.method6274("uuid", var0x -> UUID.randomUUID().toString());
      var1.method6274("base64", var0x -> Base64.getEncoder().encodeToString(ScriptValueUtils.method6254(var0x, 0, "").getBytes(StandardCharsets.UTF_8)));
      var1.method6274("unbase64", var0x -> {
         try {
            return new String(Base64.getDecoder().decode(ScriptValueUtils.method6254(var0x, 0, "")), StandardCharsets.UTF_8);
         } catch (IllegalArgumentException var2) {
            throw ScriptValueUtils.method6251("это не base64");
         }
      });
      return var1.getScriptableObject();
   }

   private static Scriptable method4897(Script var0, Storage var1, Scriptable var2) {
      var var3 = items.computeIfAbsent(var0.getId(), var0x -> new ArrayDeque<>());
      ScriptValueUtils.Entry2 var4 = ScriptValueUtils.method6247(var2);
      var4.method6274("ready", var0x -> AiSettingsStore.method4467().method4471(AiSettingsStore.method4467().getShownProvider()));
      var4.method6274("usage", var2x -> {
         long[] var3x = items2.get(var0.getId());
         long var4x = var3x != null && var3x[0] == System.currentTimeMillis() / 86400000L ? var3x[1] : 0L;
         return ScriptValueUtils.method6247(var2).method6277("today", (double)var4x).method6277("limit", 150.0).getScriptableObject();
      });
      var4.method6274(
         "ask",
         var3x -> {
            String var4x = ScriptValueUtils.method6254(var3x, 0, "");
            var var5 = ScriptValueUtils.method6260(var3x, 1);
            Scriptable var6 = ScriptValueUtils.method6262(var3x, 2);
            ScriptPermissions.method4(var0, "ai");
            if (var4x.isBlank()) {
               throw ScriptValueUtils.method6251("ai.ask: пустой запрос");
            } else if (var4x.length() > 8000) {
               throw ScriptValueUtils.method6251("ai.ask: запрос длиннее 8000 символов");
            } else {
               long var7 = System.currentTimeMillis();

               while (!var3.isEmpty() && var7 - var3.peekFirst() > 60000L) {
                  var3.pollFirst();
               }

               if (var3.size() >= 10) {
                  throw ScriptValueUtils.method6251("ai.ask: не чаще 10 раз в минуту");
               } else {
                  long var9 = var7 / 86400000L;
                  long[] var11 = items2.computeIfAbsent(var0.getId(), var2xx -> new long[]{var9, 0L});
                  if (var11[0] != var9) {
                     var11[0] = var9;
                     var11[1] = 0L;
                  }

                  if (var11[1] >= 150L) {
                     throw ScriptValueUtils.method6251("ai.ask: дневной лимит 150 запросов исчерпан — он защищает твой ключ");
                  } else {
                     var11[1]++;
                     var3.addLast(var7);
                     if (var11[1] == 1L || var11[1] % 25L == 0L) {
                        var1.method1327(var0, "info", "ai.ask: запрос " + var11[1] + " из 150 за сутки к ключу игрока");
                     }

                     AiSettingsStore var12 = AiSettingsStore.method4467();
                     ShownProvider var13 = var12.getShownProvider();
                     if (!var12.method4471(var13)) {
                        RemoteUtil.method1847(var1, var0, var5, null, "нет ключа нейросети — вставь его во вкладке Scripts → AI");
                        return null;
                     } else {
                        String var14 = var6 == null ? "Отвечай кратко и по делу." : ScriptValueUtils.method6266(var6, "system", "Отвечай кратко и по делу.");
                        String var15 = var12.method4469(var13);
                        String var16 = var12.method4472(var13);
                        String var17 = var12.method4474(var13);
                        Thread.ofPlatform()
                           .daemon(true)
                           .name("zenith-script-ai")
                           .start(
                              () -> {
                                 StringBuilder var9x = new StringBuilder();
                                 String var10 = null;

                                 try {
                                    AiStreamClient.method4739(
                                       var13, var15, var16, var17, var14, List.of(new AiStreamClient.Data(true, var4x)), var9x::append, () -> !var0.isRunning()
                                    );
                                 } catch (Exception var13x) {
                                    var10 = var13x.getMessage() == null ? var13x.getClass().getSimpleName() : var13x.getMessage();
                                 }

                                 String var11x = var9x.toString();
                                 RemoteUtil.method1847(var1, var0, var5, var10 == null ? var11x : null, var10);
                              }
                           );
                        return null;
                     }
                  }
               }
            }
         }
      );
      return var4.getScriptableObject();
   }
}

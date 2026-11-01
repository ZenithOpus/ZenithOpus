package org.zenith.client.scripting;

import com.darkmagician6.eventapi.EventManager;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ScriptTestApi {
   private static final Map<Script, Map<String, Function>> items = new WeakHashMap<>();
   private static final Map<Script, Map<String, String>> items2 = new WeakHashMap<>();
   private static final Map<Script, Boolean> items3 = new WeakHashMap<>();
   private static final Map<Script, Map<String, double[]>> items4 = new WeakHashMap<>();
   private static final Map<Script, Map<String, Long>> items5 = new WeakHashMap<>();
   private static final List<Object[]> items6 = new CopyOnWriteArrayList<>();
   private static boolean flag;

   private ScriptTestApi() {
   }

   private static synchronized void method5409() {
      if (!flag) {
         flag = true;
         EventManager.main().subscribe(DrawEvent.class, (byte)3, (var0, var1) -> method5414(((DrawEvent)var1).getCustomDrawContext()));
         EventManager.main().subscribe(WorldRenderEvent.class, (byte)3, (var0, var1) -> method5415());
      }
   }

   public static void method5410(Storage var0, Script var1, ScriptableObject var2) {
      method5409();
      var1.method1413(() -> {
         items2.remove(var1);
         items3.remove(var1);
         items6.removeIf(var1x -> var1x[0] == var1);
         items.remove(var1);
      });
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6246(var2, var2);
      var3.method6274("test", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "тест");
         var var3x = items.computeIfAbsent(var1, var0xx -> new LinkedHashMap<>());
         if (var3x.size() >= 200 && !var3x.containsKey(var2x)) {
            throw ScriptValueUtils.method6251("больше 200 тестов");
         } else {
            var3x.put(var2x, ScriptValueUtils.method6260(var1x, 1));
            return null;
         }
      });
      items.remove(var1);
      var3.method6274(
         "assert",
         var0x -> {
            if (!ScriptValueUtils.method6259(var0x, 0, false)) {
               throw ScriptValueUtils.method6251(
                  "проверка не прошла" + (ScriptValueUtils.method6252(var0x, 1) ? ": " + ScriptValueUtils.method6254(var0x, 1, "") : "")
               );
            } else {
               return true;
            }
         }
      );
      ScriptValueUtils.Entry2 var4 = ScriptValueUtils.method6246(var2, (ScriptableObject)var2.get("assert", var2));
      var4.method6274(
         "eq",
         var0x -> {
            String var1x = ScriptValueUtils.method6271(ScriptValueUtils.arg(var0x, 0), 0).toString();
            String var2x = ScriptValueUtils.method6271(ScriptValueUtils.arg(var0x, 1), 0).toString();
            if (!var1x.equals(var2x)) {
               throw ScriptValueUtils.method6251(
                  "ожидалось "
                     + var2x
                     + ", получено "
                     + var1x
                     + (ScriptValueUtils.method6252(var0x, 2) ? " — " + ScriptValueUtils.method6254(var0x, 2, "") : "")
               );
            } else {
               return true;
            }
         }
      );
      var4.method6274("near", var0x -> {
         double var1x = ScriptValueUtils.method6256(var0x, 0, 0.0);
         double var3x = ScriptValueUtils.method6256(var0x, 1, 0.0);
         double var5x = ScriptValueUtils.method6256(var0x, 2, 1.0E-6);
         if (Math.abs(var1x - var3x) > var5x) {
            throw ScriptValueUtils.method6251("ожидалось ≈" + var3x + ", получено " + var1x);
         } else {
            return true;
         }
      });
      var4.method6274(
         "throws",
         var1x -> {
            var var2x = ScriptValueUtils.method6260(var1x, 0);

            try {
               var2x.call(Context.getCurrentContext(), var2, var2, new Object[0]);
            } catch (RhinoException var4x) {
               return true;
            }

            throw ScriptValueUtils.method6251(
               "ожидалась ошибка" + (ScriptValueUtils.method6252(var1x, 1) ? ": " + ScriptValueUtils.method6254(var1x, 1, "") : "")
            );
         }
      );
      ScriptValueUtils.Entry2 var5 = ScriptValueUtils.method6247(var2);
      var5.method6274(
         "watch",
         var1x -> {
            var var2x = items2.computeIfAbsent(var1, var0xx -> new LinkedHashMap<>());
            String var3x = ScriptValueUtils.method6254(var1x, 0, "value");
            if (var2x.size() >= 40 && !var2x.containsKey(var3x)) {
               throw ScriptValueUtils.method6251("больше 40 наблюдений");
            } else {
               Object var4x = ScriptValueUtils.arg(var1x, 1);
               String var5x = var4x instanceof Scriptable var6x && !(var4x instanceof Function)
                  ? ScriptValueUtils.method6271(var6x, 0).toString()
                  : ScriptValueUtils.method6255(var4x);
               var2x.put(var3x, var5x.length() > 120 ? var5x.substring(0, 120) + "…" : var5x);
               return null;
            }
         }
      );
      var5.method6274("unwatch", var1x -> {
         var var2x = items2.get(var1);
         if (var2x != null) {
            var2x.remove(ScriptValueUtils.method6254(var1x, 0, ""));
         }

         return null;
      });
      var5.method6274("panel", var1x -> {
         boolean var2x = !ScriptValueUtils.method6252(var1x, 0) || ScriptValueUtils.method6259(var1x, 0, true);
         items3.put(var1, var2x);
         return var2x;
      });
      var5.method6274(
         "line",
         var1x -> method5411(
            var1,
            "line",
            new double[]{
               ScriptValueUtils.method6256(var1x, 0, 0.0),
               ScriptValueUtils.method6256(var1x, 1, 0.0),
               ScriptValueUtils.method6256(var1x, 2, 0.0),
               ScriptValueUtils.method6256(var1x, 3, 0.0),
               ScriptValueUtils.method6256(var1x, 4, 0.0),
               ScriptValueUtils.method6256(var1x, 5, 0.0)
            },
            ScriptValueUtils.method6258(var1x, 6, -43691),
            ScriptValueUtils.method6256(var1x, 7, 2000.0),
            null
         )
      );
      var5.method6274(
         "box",
         var1x -> method5411(
            var1,
            "box",
            new double[]{ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)},
            ScriptValueUtils.method6258(var1x, 3, -43691),
            ScriptValueUtils.method6256(var1x, 4, 2000.0),
            null
         )
      );
      var5.method6274(
         "point",
         var1x -> method5411(
            var1,
            "point",
            new double[]{ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)},
            ScriptValueUtils.method6258(var1x, 3, 65365),
            ScriptValueUtils.method6256(var1x, 4, 2000.0),
            null
         )
      );
      var5.method6274(
         "text",
         var1x -> method5411(
            var1,
            "text",
            new double[]{ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0), ScriptValueUtils.method6256(var1x, 3, 0.0)},
            ScriptValueUtils.method6258(var1x, 4, -1),
            ScriptValueUtils.method6256(var1x, 5, 2000.0),
            ScriptValueUtils.method6254(var1x, 0, "")
         )
      );
      var5.method6274("clear", var1x -> {
         items6.removeIf(var1xx -> var1xx[0] == var1);
         var var2x = items2.get(var1);
         if (var2x != null) {
            var2x.clear();
         }

         return null;
      });
      var3.method6277("debug", var5.getScriptableObject());
      ScriptValueUtils.Entry2 var6 = ScriptValueUtils.method6247(var2);
      var6.method6274("mark", var1x -> {
         items5.computeIfAbsent(var1, var0xx -> new LinkedHashMap<>()).put(ScriptValueUtils.method6254(var1x, 0, "mark"), System.nanoTime());
         return null;
      });
      var6.method6274("end", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "mark");
         var var3x = items5.get(var1);
         Long var4x = var3x == null ? null : (Long)var3x.remove(var2x);
         if (var4x == null) {
            return null;
         } else {
            double var5x = (System.nanoTime() - var4x) / 1000000.0;
            method5412(var1, var2x, var5x);
            return var5x;
         }
      });
      var6.method6274("measure", var2x -> {
         String var3x = ScriptValueUtils.method6254(var2x, 0, "measure");
         var var4x = ScriptValueUtils.method6260(var2x, 1);
         long var5x = System.nanoTime();

         Object var7x;
         try {
            var7x = var4x.call(Context.getCurrentContext(), var2, var2, new Object[0]);
         } finally {
            method5412(var1, var3x, (System.nanoTime() - var5x) / 1000000.0);
         }

         return var7x;
      });
      var6.method6274(
         "report",
         var2x -> {
            ArrayList var3x = new ArrayList();
            var var4x = items4.get(var1);
            if (var4x != null) {
               var4x.forEach(
                  (var2xx, var3xx) -> var3x.add(
                     ScriptValueUtils.method6247(var2)
                        .method6277("name", var2xx)
                        .method6277("count", var3xx[0])
                        .method6277("totalMs", var3xx[1])
                        .method6277("avgMs", var3xx[0] == 0.0 ? 0.0 : var3xx[1] / var3xx[0])
                        .method6277("maxMs", var3xx[2])
                        .getScriptableObject()
                  )
               );
            }

            return ScriptValueUtils.method6267(var2, var3x);
         }
      );
      var6.method6274("reset", var1x -> {
         items4.remove(var1);
         items5.remove(var1);
         return null;
      });
      var3.method6277("profile", var6.getScriptableObject());
      ScriptValueUtils.Entry2 var7 = ScriptValueUtils.method6246(var2, (ScriptableObject)var2.get("events", var2));
      var7.method6274("every", var2x -> {
         int var3x = MathHelper.clamp(ScriptValueUtils.method6257(var2x, 0, 20), 1, 72000);
         return (double)var0.method1329(var1, ScriptValueUtils.method6260(var2x, 1), var3x * 50L, true);
      });
      var7.method6274("debounce", var3x -> {
         long var4x = (long)MathHelper.clamp(ScriptValueUtils.method6256(var3x, 0, 200.0), 1.0, 600000.0);
         var var6x = ScriptValueUtils.method6260(var3x, 1);
         Object[][] var7x = new Object[][]{new Object[0]};
         int[] var8x = new int[]{-1};
         BaseFunction var9x = ScriptValueUtils.method6249(var2, "debounced", var3xx -> var6x.call(Context.getCurrentContext(), var2, var2, var7x[0]));
         return ScriptValueUtils.method6249(var2, "debounce", var7xx -> {
            var7x[0] = (Object[])var7xx.clone();
            if (var8x[0] >= 0) {
               var0.method1330(var1, var8x[0]);
            }

            var8x[0] = var0.method1329(var1, var9x, var4x, false);
            return null;
         });
      });
      var7.method6274("throttle", var1x -> {
         long var2x = (long)MathHelper.clamp(ScriptValueUtils.method6256(var1x, 0, 200.0), 1.0, 600000.0);
         var var4x = ScriptValueUtils.method6260(var1x, 1);
         long[] var5x = new long[]{0L};
         return ScriptValueUtils.method6249(var2, "throttle", var5xx -> {
            long var6x = System.currentTimeMillis();
            if (var6x - var5x[0] < var2x) {
               return null;
            } else {
               var5x[0] = var6x;
               return var4x.call(Context.getCurrentContext(), var2, var2, var5xx);
            }
         });
      });
      ScriptableObject var8 = (ScriptableObject)var2.get("script", var2);
      ScriptValueUtils.Entry2 var9 = ScriptValueUtils.method6246(var2, var8);
      var9.method6274("keep", var3x -> {
         String var4x = ScriptValueUtils.method6254(var3x, 0, "state");
         if (var1.getItems().size() >= 64 && !var1.getItems().containsKey(var4x)) {
            throw ScriptValueUtils.method6251("больше 64 сохраняемых значений");
         } else {
            JsonElement var5x = var0.method1316(var1, var4x);
            Object var6x = var5x != null ? ScriptValueUtils.method6273(var2, var5x) : ScriptValueUtils.arg(var3x, 1);
            var1.getItems().put(var4x, var6x);
            return var6x;
         }
      });
      var9.method6274("forget", var2x -> {
         String var3x = ScriptValueUtils.method6254(var2x, 0, "state");
         var1.getItems().remove(var3x);
         var0.method1317(var1, var3x);
         return null;
      });
      var9.method6274("settingsUI", var1x -> {
         MinecraftClient.getInstance().execute(() -> {
            NLMenuScreen var1xx = Zenith.getInstance().getNLMenuScreen();
            if (var1xx != null) {
               var1xx.openScriptSettings(var1);
            }
         });
         return null;
      });
   }

   private static Object method5411(Script var0, String var1, double[] var2, int var3, double var4, String var6) {
      if (items6.stream().filter(var1x -> var1x[0] == var0).count() >= 500L) {
         return false;
      } else {
         items6.add(new Object[]{var0, var1, var2, var3, System.currentTimeMillis() + (long)MathHelper.clamp(var4, 50.0, 120000.0), var6});
         return true;
      }
   }

   private static void method5412(Script var0, String var1, double var2) {
      var var4 = items4.computeIfAbsent(var0, var0x -> new LinkedHashMap<>());
      if (var4.size() < 200 || var4.containsKey(var1)) {
         double[] var5 = var4.computeIfAbsent(var1, var0x -> new double[3]);
         var5[0]++;
         var5[1] += var2;
         var5[2] = Math.max(var5[2], var2);
      }
   }

   public static String method5413(Storage var0, Script var1) {
      var var2 = items.get(var1);
      if (var2 != null && !var2.isEmpty()) {
         int var3 = 0;
         ArrayList var4 = new ArrayList();

         for (Object var6_o : (Iterable<?>)(Object)(new LinkedHashMap(var2).entrySet())) {
            Entry var6 = (Entry)var6_o;
            if (!var1.isRunning() || var1.getScope() == null) {
               break;
            }

            try {
               ScriptContextFactory.method7410(1000L, (Callable)var6.getValue(), var1.getScope(), new Object[0]);
               var3++;
               var0.method1327(var1, "info", "✔ " + (String)var6.getKey());
            } catch (Throwable var9) {
               String var8 = Storage.method1325(var9);
               var4.add((String)var6.getKey() + ": " + var8);
               var0.method1327(var1, "error", "✘ " + (String)var6.getKey() + " — " + var8);
            }
         }

         return "тесты «"
            + var1.method1403()
            + "»: прошло "
            + var3
            + " из "
            + (var3 + var4.size())
            + (var4.isEmpty() ? "" : " · упали: " + String.join("; ", var4));
      } else {
         return "в скрипте нет тестов: test(\"имя\", function () { assert(...) })";
      }
   }

   private static void method5414(CustomDrawContext var0) {
      float var1 = 40.0F;

      for (Entry var3 : new ArrayList<>(items3.entrySet())) {
         Script var4 = (Script)var3.getKey();
         if (Boolean.TRUE.equals(var3.getValue()) && var4.isRunning()) {
            var var5 = items2.getOrDefault(var4, Map.of());
            var var6 = items4.getOrDefault(var4, Map.of());
            Font var7 = Fonts.NEW_SEMIBOLD.getFont(6.0F);
            Font var8 = Fonts.NEW_MEDIUM.getFont(5.0F);
            ArrayList<String> var9 = new ArrayList();
            var5.forEach((var1x, var2) -> var9.add(var1x + " = " + var2));
            var6.forEach(
               (var1x, var2) -> var9.add(
                  "⏱ " + var1x + ": " + String.format(Locale.ROOT, "%.2f мс (макс %.2f)", var2[0] == 0.0 ? 0.0 : var2[1] / var2[0], var2[2])
               )
            );
            long[] var10 = var4.method1409();
            double var11 = Math.max(0.001, (System.currentTimeMillis() - var10[3]) / 1000.0);
            var9.add(String.format(Locale.ROOT, "нагрузка: %.1f мс/с · вызовов %d", var10[0] / 1000000.0 / var11, var10[1]));
            float var13 = var7.width(var4.method1403()) + 12.0F;

            for (Object var15_o : (Iterable<?>)(Object)(var9)) {
               String var15 = (String)var15_o;
               var13 = Math.max(var13, var8.width(var15) + 12.0F);
            }

            var13 = Math.min(var13, 260.0F);
            float var26 = 12.0F + var9.size() * (var8.height() + 2.0F) + 4.0F;
            var0.drawRoundedRect(6.0F, var1, var13, var26, ProxyDotRadiusData.method8069(4.0F), new ZenithColor(-1072952302));
            var0.drawText(var7, var4.method1403(), 12.0F, var1 + 4.0F, new ZenithColor(ScriptDrawApi.method2603()));
            float var27 = var1 + 12.0F;

            for (Object var17_o : (Iterable<?>)(Object)(var9)) {
               String var17 = (String)var17_o;
               var0.drawText(var8, var8.clip(var17, var13 - 12.0F), 12.0F, var27, new ZenithColor(-419430401));
               var27 += var8.height() + 2.0F;
            }

            var1 += var26 + 4.0F;
         }
      }

      long var18 = System.currentTimeMillis();

      for (Object[] var20 : items6) {
         if (var18 > (Long)var20[4] || !((Script)var20[0]).isRunning()) {
            items6.remove(var20);
         } else if ("text".equals(var20[1])) {
            double[] var21 = (double[])var20[2];
            Vec3d var22 = ProjectionUtils.method301(new Vec3d(var21[0], var21[1], var21[2]));
            if (!(var22.z <= 0.0) && !(var22.z >= 1.0)) {
               Font var23 = Fonts.NEW_MEDIUM.getFont(6.0F);
               String var24 = (String)var20[5];
               var0.drawText(var23, var24, (float)var22.x - var23.width(var24) / 2.0F, (float)var22.y, new ZenithColor((Integer)var20[3]));
            }
         }
      }
   }

   private static void method5415() {
      for (Object[] var1 : items6) {
         double[] var2 = (double[])var1[2];
         int var3 = (Integer)var1[3];
         String var4 = (String)var1[1];
         switch (var4) {
            case "line":
               GlowUtil.method2245(new Vec3d(var2[0], var2[1], var2[2]), new Vec3d(var2[3], var2[4], var2[5]), var3, 2.0F, false);
               break;
            case "box":
               double var6 = Math.floor(var2[0]);
               double var8 = Math.floor(var2[1]);
               double var10 = Math.floor(var2[2]);
               GlowUtil.method2232(new Box(var6, var8, var10, var6 + 1.0, var8 + 1.0, var10 + 1.0), var3, 2.0F, true, false, false);
               break;
            case "point":
               GlowUtil.method2232(
                  new Box(var2[0] - 0.08, var2[1] - 0.08, var2[2] - 0.08, var2[0] + 0.08, var2[1] + 0.08, var2[2] + 0.08), var3, 1.0F, false, true, false
               );
         }
      }
   }
}

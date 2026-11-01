package org.zenith.client.scripting;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.ZenithColor;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;

public final class ScriptSettingsApi {
   private ScriptSettingsApi() {
   }

   public static void method1221(ScriptValueUtils.Entry2 var0, Script var1, Storage var2, Scriptable var3, ScriptSettingsApi.Handler var4) {
      BooleanSetting[] var5 = new BooleanSetting[]{null};
      java.util.function.Consumer<org.zenith.client.settings.Setting> var6 = var2x -> {
         if (var5[0] != null) {
            BooleanSetting var3x = var5[0];
            var2x.setVisible(var3x::isEnabled);
         }

         if (var4.contains(var2x.getKey())) {
            throw ScriptValueUtils.method6251("настройка «" + var2x.getKey() + "» уже есть");
         } else {
            JsonObject var6x = var4.method1228();
            if (var6x != null) {
               try {
                  if (var6x.has(var2x.getKey()) || var2x instanceof MultiSelectSetting) {
                     var2x.load(var6x);
                  }
               } catch (RuntimeException var5x) {
               }
            }

            var4.method1227(var2x);
         }
      };
      var0.method6274("checkbox", var4x -> {
         BooleanSetting var5x = new BooleanSetting(method1222(var4x), ScriptValueUtils.method6259(var4x, 1, false));
         var6.accept(var5x);
         return method1224(var3, var5x, var1, var2);
      });
      var0.method6274(
         "slider",
         var4x -> {
            float var5x = (float)ScriptValueUtils.method6256(var4x, 2, 0.0);
            float var6x = (float)ScriptValueUtils.method6256(var4x, 3, 1.0);
            if (var6x < var5x) {
               float var7 = var5x;
               var5x = var6x;
               var6x = var7;
            }

            float var10 = (float)Math.max(1.0E-4, ScriptValueUtils.method6256(var4x, 4, 0.1));
            float var8 = (float)Math.max((double)var5x, Math.min((double)var6x, ScriptValueUtils.method6256(var4x, 1, var5x)));
            SliderSetting var9 = new SliderSetting(
               method1222(var4x),
               var8,
               var5x,
               var6x,
               var10,
               "",
               ScriptValueUtils.method6254(var4x, 5, ""),
               (Supplier<Boolean>)null,
               (SliderSetting.Handler2)null
            );
            var6.accept(var9);
            return method1224(var3, var9, var1, var2);
         }
      );
      var0.method6274("mode", var4x -> {
         var var5x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var4x, 1));
         if (var5x.isEmpty()) {
            throw ScriptValueUtils.method6251("mode: нужен массив вариантов");
         } else {
            String[] var6x = var5x.stream().map(ScriptValueUtils::method6255).toArray(String[]::new);
            ModeSetting var7 = new ModeSetting(method1222(var4x), "", var6x);
            String var8 = ScriptValueUtils.method6254(var4x, 2, var6x[0]);
            var7.set(var8);
            var6.accept(var7);
            return method1224(var3, var7, var1, var2);
         }
      });
      var0.method6274("multi", var4x -> {
         var var5x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var4x, 1));
         if (var5x.isEmpty()) {
            throw ScriptValueUtils.method6251("multi: нужен массив вариантов");
         } else {
            var var6x = var5x.stream().map(ScriptValueUtils::method6255).toList();
            MultiSelectSetting var7 = MultiSelectSetting.method5223(method1222(var4x), var6x);
            var var8 = ScriptValueUtils.method6269(ScriptValueUtils.arg(var4x, 2)).stream().map(ScriptValueUtils::method6255).toList();

            for (MultiSelectSetting.Entry var10 : var7.getItems2()) {
               var10.setEnabled(var8.stream().anyMatch(var1xx -> var1xx.equalsIgnoreCase(var10.getKey())));
            }

            var6.accept(var7);
            return method1224(var3, var7, var1, var2);
         }
      });
      var0.method6274("color", var4x -> {
         ColorSetting var5x = new ColorSetting(method1222(var4x), new ZenithColor(ScriptValueUtils.method6258(var4x, 1, -1)));
         var6.accept(var5x);
         return method1224(var3, var5x, var1, var2);
      });
      var0.method6274(
         "text",
         var4x -> {
            TextSetting var5x = new TextSetting(
               method1222(var4x), ScriptValueUtils.method6254(var4x, 1, ""), ScriptValueUtils.method6254(var4x, 2, ""), TextSetting.Entry.method2125(256)
            );
            var6.accept(var5x);
            return method1224(var3, var5x, var1, var2);
         }
      );
      var0.method6274("bind", var4x -> {
         BindSetting var5x = new BindSetting(method1222(var4x), ScriptValueUtils.method6257(var4x, 1, -1));
         var6.accept(var5x);
         return method1224(var3, var5x, var1, var2);
      });
      var0.method6274("button", var4x -> {
         var var5x = ScriptValueUtils.method6260(var4x, 1);
         ButtonSetting var6x = new ButtonSetting(method1222(var4x), () -> var2.method1321(var1, var5x));
         var6.accept(var6x);
         return method1224(var3, var6x, var1, var2);
      });
      var0.method6274(
         "group",
         var6x -> {
            BooleanSetting var7 = new BooleanSetting(
               "▾ " + method1222(var6x), !ScriptValueUtils.method6252(var6x, 1) || ScriptValueUtils.method6259(var6x, 1, true)
            );
            var6.accept(var7);
            ScriptableObject var8 = var0.getScriptableObject();
            ScriptValueUtils.Entry2 var9 = ScriptValueUtils.method6247(var3);

            for (String var13 : new String[]{"checkbox", "slider", "mode", "multi", "color", "text", "bind", "button"}) {
               var9.method6274(var13, var5xx -> {
                  Function var6xx = (Function)var8.get(var13, var8);
                  BooleanSetting var7x = var5[0];
                  var5[0] = var7;

                  Object var8x;
                  try {
                     var8x = var6xx.call(Context.getCurrentContext(), var3, var8, var5xx);
                  } finally {
                     var5[0] = var7x;
                  }

                  return var8x;
               });
            }

            var9.method6277("toggle", method1224(var3, var7, var1, var2));
            var9.method6274("open", var1xx -> var7.isEnabled());
            return var9.getScriptableObject();
         }
      );
   }

   private static String method1222(Object[] var0) {
      String var1 = ScriptValueUtils.method6254(var0, 0, "").trim();
      if (!var1.isEmpty() && var1.length() <= 64) {
         return var1;
      } else {
         throw ScriptValueUtils.method6251("имя настройки: от 1 до 64 символов");
      }
   }

   public static Scriptable method1223(Scriptable var0, Setting var1) {
      return method1224(var0, var1, null, null);
   }

   public static Scriptable method1224(Scriptable var0, Setting var1, Script var2, Storage var3) {
      ScriptValueUtils.Entry2 var4 = ScriptValueUtils.method6247(var0);
      var4.method6277("name", var1.getKey());
      if (var2 != null) {
         var var5 = var1.getVisible();
         var4.method6274("visibleIf", var5x -> {
            var var6x = ScriptValueUtils.method6260(var5x, 0);
            ScriptableObject var7x = var4.getScriptableObject();
            var1.setVisible(() -> {
               if (var5 != null && !(Boolean)var5.get()) {
                  return false;
               } else if (!var2.isRunning()) {
                  return true;
               } else {
                  Object var4xx = var3.method1321(var2, var6x);
                  return var4xx == null || Context.toBoolean(var4xx);
               }
            });
            return var7x;
         });
      }

      var4.method6274("visible", var1x -> var1.isVisible());
      if (var1 instanceof BooleanSetting var13) {
         var4.method6277("type", "checkbox");
         var4.method6274("get", var1x -> var13.isEnabled());
         var4.method6274("set", var1x -> {
            var13.setEnabled(ScriptValueUtils.method6259(var1x, 0, true));
            return null;
         });
         var4.method6274("toggle", var1x -> {
            var13.setEnabled(!var13.isEnabled());
            return var13.isEnabled();
         });
      } else if (var1 instanceof SliderSetting var6) {
         var4.method6277("type", "slider");
         var4.method6277("min", (double)var6.getMin()).method6277("max", (double)var6.getMax()).method6277("step", (double)var6.getFloatValue2());
         var4.method6274("get", var1x -> (double)var6.getValue3());
         var4.method6274("set", var1x -> {
            var6.method2400((float)ScriptValueUtils.method6256(var1x, 0, var6.getValue3()));
            return null;
         });
      } else if (var1 instanceof ModeSetting var7) {
         var4.method6277("type", "mode");
         var4.method6274("get", var1x -> var7.get());
         var4.method6274("set", var1x -> {
            var7.set(ScriptValueUtils.method6254(var1x, 0, var7.get()));
            return null;
         });
         var4.method6274("is", var1x -> var7.method2101(ScriptValueUtils.method6254(var1x, 0, "")));
         var4.method6274("options", var2x -> {
            ArrayList var3x = new ArrayList();

            for (ModeSetting.Entry var5x : var7.getValues()) {
               var3x.add(var5x.getKey());
            }

            return ScriptValueUtils.method6267(var0, var3x);
         });
      } else if (var1 instanceof MultiSelectSetting var8) {
         var4.method6277("type", "multi");
         var4.method6274("get", var2x -> ScriptValueUtils.method6267(var0, new ArrayList<>(var8.method5229())));
         var4.method6274("enabled", var1x -> {
            MultiSelectSetting.Entry var2x = var8.method5222(ScriptValueUtils.method6254(var1x, 0, ""));
            return var2x != null && var2x.isEnabled();
         });
         var4.method6274("set", var1x -> {
            var var2x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var1x, 0)).stream().map(ScriptValueUtils::method6255).toList();

            for (MultiSelectSetting.Entry var4x : var8.getItems2()) {
               var4x.setEnabled(var2x.stream().anyMatch(var1xx -> var1xx.equalsIgnoreCase(var4x.getKey())));
            }

            return null;
         });
      } else if (var1 instanceof ColorSetting var9) {
         var4.method6277("type", "color");
         var4.method6274("get", var1x -> (double)var9.method1445());
         var4.method6274("set", var1x -> {
            var9.method1446(ScriptValueUtils.method6258(var1x, 0, var9.method1445()));
            return null;
         });
      } else if (var1 instanceof TextSetting var10) {
         var4.method6277("type", "text");
         var4.method6274("get", var1x -> var10.getValue() == null ? "" : var10.getValue());
         var4.method6274("set", var1x -> {
            var10.method2122(ScriptValueUtils.method6254(var1x, 0, ""));
            return null;
         });
      } else if (var1 instanceof BindSetting var11) {
         var4.method6277("type", "bind");
         var4.method6274("get", var1x -> (double)var11.getKeyCode());
         var4.method6274("set", var1x -> {
            var11.setKeyCode(ScriptValueUtils.method6257(var1x, 0, -1));
            return null;
         });
         var4.method6274("isKey", var1x -> var11.getKeyCode() >= 0 && var11.getKeyCode() == ScriptValueUtils.method6257(var1x, 0, -2));
      } else if (var1 instanceof ButtonSetting var12) {
         var4.method6277("type", "button");
         var4.method6274("press", var1x -> {
            var12.getRunnable().run();
            return null;
         });
      } else {
         var4.method6277("type", "other");
      }

      return var4.getScriptableObject();
   }

   public static ScriptSettingsApi.Handler method1225(final List<Setting> var0, final JsonObject var1, final Consumer<Setting> var2) {
      return new ScriptSettingsApi.Handler() {
         @Override
         public void method1227(Setting var1x) {
            var0.add(var1x);
            var2.accept(var1x);
         }

         @Override
         public JsonObject method1228() {
            return var1;
         }

         @Override
         public boolean contains(String var1x) {
            return var0.stream().anyMatch(var1xxx -> var1xxx.getKey().equalsIgnoreCase(var1x));
         }
      };
   }

   public static ScriptSettingsApi.Handler method1226(Script var0, final JsonObject var1) {
      var var2 = ScriptModuleApi.method323(var0);
      return new ScriptSettingsApi.Handler() {
         @Override
         public void method1227(Setting var1x) {
            var2.put(var1x.getKey(), var1x);
         }

         @Override
         public JsonObject method1228() {
            return var1;
         }

         @Override
         public boolean contains(String var1x) {
            return var2.containsKey(var1x);
         }
      };
   }

   public interface Handler {
      void method1227(Setting var1);

      JsonObject method1228();

      boolean contains(String var1);
   }
}

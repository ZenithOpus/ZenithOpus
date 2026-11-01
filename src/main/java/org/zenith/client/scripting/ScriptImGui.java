package org.zenith.client.scripting;

import com.darkmagician6.eventapi.EventManager;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.utility.game.Util4;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ScriptImGui {
   private static long longValue;
   private static float floatValue = Float.NaN;
   private static float floatValue2 = Float.NaN;
   private static long longValue2 = -1L;
   private static boolean flag;
   private static float floatValue3;
   private static float floatValue4;
   private static final Map<Script, Map<String, Object>> items = new WeakHashMap<>();
   private static final Map<Script, String> items2 = new WeakHashMap<>();
   private static final Map<Script, String> items3 = new WeakHashMap<>();
   private static final List<ScriptImGui.Data> items4 = new CopyOnWriteArrayList<>();
   private static final List<ScriptImGui.Entry3> items5 = new CopyOnWriteArrayList<>();
   private static final List<Object[]> items6 = new ArrayList<>();
   private static ScriptImGui.Entry entry;
   private static boolean flag2;

   private ScriptImGui() {
   }

   private static MinecraftClient method632() {
      return MinecraftClient.getInstance();
   }

   public static synchronized void method633() {
      if (!flag2) {
         flag2 = true;
         EventManager.main().subscribe(DrawEvent.class, (byte)0, (var0, var1) -> beginFrame());
         EventManager.main().subscribe(DrawEvent.class, (byte)4, (var0, var1) -> method649(((DrawEvent)var1).getCustomDrawContext()));
         EventManager.main().subscribe(WorldRenderEvent.class, (byte)4, (var0, var1) -> method652());
         EventManager.main().subscribe(TickEvent.class, (byte)4, (var0, var1) -> method651());
         EventManager.main().subscribe(MouseButtonEvent.class, (byte)0, (var0, var1) -> {
            MouseButtonEvent var2 = (MouseButtonEvent)var1;
            if (var2.getIntValue() == 0 && var2.getIntValue2() == 1) {
               method634(ScriptImGui.Entry2.method656(), ScriptImGui.Entry2.method657());
            }
         });
      }
   }

   public static void method634(float var0, float var1) {
      floatValue = var0;
      floatValue2 = var1;
      flag = true;
   }

   private static void beginFrame() {
      longValue++;
      if (flag) {
         flag = false;
         longValue2 = longValue;
      } else if (longValue2 != longValue) {
         floatValue = Float.NaN;
      }
   }

   public static void method635(float var0, float var1) {
      floatValue3 = var0;
      floatValue4 = var1;
   }

   private static float method636() {
      return ScriptImGui.Entry2.method656() - floatValue3;
   }

   private static float method637() {
      return ScriptImGui.Entry2.method657() - floatValue4;
   }

   private static boolean method638(float var0, float var1, float var2, float var3) {
      if (longValue2 == longValue && !Float.isNaN(floatValue)) {
         float var4 = floatValue - floatValue3;
         float var5 = floatValue2 - floatValue4;
         if (var4 >= var0 && var4 <= var0 + var2 && var5 >= var1 && var5 <= var1 + var3) {
            floatValue = Float.NaN;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean method639(float var0, float var1, float var2, float var3) {
      float var4 = method636();
      float var5 = method637();
      return var4 >= var0 && var4 <= var0 + var2 && var5 >= var1 && var5 <= var1 + var3;
   }

   private static boolean method640() {
      return GLFW.glfwGetMouseButton(method632().getWindow().getHandle(), 0) == 1;
   }

   private static Map<String, Object> method641(Script var0) {
      return items.computeIfAbsent(var0, var0x -> new HashMap<>());
   }

   private static int method642() {
      return ScriptDrawApi.method2603();
   }

   private static Font font(float var0) {
      return Fonts.NEW_MEDIUM.getFont(Math.max(3.0F, Math.min(24.0F, var0)));
   }

   public static boolean method643(Script var0) {
      return items2.get(var0) != null;
   }

   public static void method644(Script var0, String var1) {
      String var2 = items2.get(var0);
      if (var2 != null) {
         var var3 = method641(var0);
         String var4 = String.valueOf(var3.getOrDefault("in:" + var2, ""));
         if (var4.length() + var1.length() <= 256) {
            var3.put("in:" + var2, var4 + var1);
         }
      }
   }

   public static boolean method645(Script var0, int var1) {
      String var2 = items2.get(var0);
      if (var2 == null) {
         return false;
      } else {
         var var3 = method641(var0);
         String var4 = String.valueOf(var3.getOrDefault("in:" + var2, ""));
         if (var1 == 259) {
            if (!var4.isEmpty()) {
               var3.put("in:" + var2, var4.substring(0, var4.length() - 1));
            }

            return true;
         } else if (var1 != 257 && var1 != 335 && var1 != 258) {
            return var1 != 256;
         } else {
            items2.remove(var0);
            var3.put("submit:" + var2, true);
            return true;
         }
      }
   }

   public static void method646(Script var0, Storage var1, Scriptable var2, ScriptableObject var3) {
      method633();
      var0.method1413(() -> {
         items.remove(var0);
         items2.remove(var0);
         items3.remove(var0);
         items4.removeIf(var1x -> var1x.script == var0);
         if (entry != null && entry.script == var0) {
            entry = null;
         }
      });
      ScriptValueUtils.Entry2 var4 = ScriptValueUtils.method6246(var2, var3);
      var4.method6274(
         "checkbox",
         var1x -> {
            CustomDrawContext var2x = ScriptDrawApi.method2601();
            String var3x = ScriptValueUtils.method6254(var1x, 0, "check");
            String var4x = ScriptValueUtils.method6254(var1x, 1, "");
            float var5x = (float)ScriptValueUtils.method6256(var1x, 2, 0.0);
            float var6x = (float)ScriptValueUtils.method6256(var1x, 3, 0.0);
            var var7 = method641(var0);
            boolean var8 = ((Boolean)var7.computeIfAbsent("cb:" + var3x, var1xx -> ScriptValueUtils.method6259(var1x, 4, false)));
            Font var9 = font(6.0F);
            float var10 = 12.0F + var9.width(var4x) + 4.0F;
            if (method638(var5x, var6x, var10, 10.0F)) {
               var8 = !var8;
               var7.put("cb:" + var3x, var8);
            }

            var2x.drawRoundedRect(var5x, var6x + 1.0F, 8.0F, 8.0F, ProxyDotRadiusData.method8069(2.0F), new ZenithColor(var8 ? method642() : 1090519039));
            if (var8) {
               var2x.drawRoundedRect(var5x + 2.5F, var6x + 3.5F, 3.0F, 3.0F, ProxyDotRadiusData.method8069(1.0F), new ZenithColor(-1));
            }

            var2x.drawText(
               var9, var4x, var5x + 12.0F, var6x + (10.0F - var9.height()) / 2.0F, new ZenithColor(method639(var5x, var6x, var10, 10.0F) ? -1 : -788529153)
            );
            return var8;
         }
      );
      var4.method6274(
         "slider",
         var1x -> {
            CustomDrawContext var2x = ScriptDrawApi.method2601();
            String var3x = ScriptValueUtils.method6254(var1x, 0, "slider");
            float var4x = (float)ScriptValueUtils.method6256(var1x, 1, 0.0);
            float var5x = (float)ScriptValueUtils.method6256(var1x, 2, 0.0);
            float var6x = (float)Math.max(20.0, ScriptValueUtils.method6256(var1x, 3, 80.0));
            double var7 = ScriptValueUtils.method6256(var1x, 4, 0.0);
            double var9 = ScriptValueUtils.method6256(var1x, 5, 1.0);
            double var11 = Math.max(0.0, ScriptValueUtils.method6256(var1x, 7, 0.0));
            var var13 = method641(var0);
            double var14 = (Double)var13.computeIfAbsent(
               "sl:" + var3x, var5xx -> MathHelper.clamp(ScriptValueUtils.method6256(var1x, 6, var7), Math.min(var7, var9), Math.max(var7, var9))
            );
            if (method638(var4x - 2.0F, var5x - 3.0F, var6x + 4.0F, 10.0F)) {
               items3.put(var0, var3x);
            }

            if (var3x.equals(items3.get(var0))) {
               if (!method640()) {
                  items3.remove(var0);
               } else {
                  double var16 = MathHelper.clamp((method636() - var4x) / var6x, 0.0F, 1.0F);
                  var14 = var7 + (var9 - var7) * var16;
                  if (var11 > 0.0) {
                     var14 = Math.round(var14 / var11) * var11;
                  }

                  var13.put("sl:" + var3x, var14);
               }
            }

            double var18 = var9 == var7 ? 0.0 : MathHelper.clamp((var14 - var7) / (var9 - var7), 0.0, 1.0);
            var2x.drawRoundedRect(var4x, var5x + 1.5F, var6x, 3.0F, ProxyDotRadiusData.method8069(1.5F), new ZenithColor(1090519039));
            var2x.drawRoundedRect(var4x, var5x + 1.5F, (float)(var6x * var18), 3.0F, ProxyDotRadiusData.method8069(1.5F), new ZenithColor(method642()));
            var2x.drawRoundedRect(var4x + (float)(var6x * var18) - 3.0F, var5x, 6.0F, 6.0F, ProxyDotRadiusData.method8069(3.0F), new ZenithColor(-1));
            return var14;
         }
      );
      var4.method6274("input", var1x -> {
         CustomDrawContext var2x = ScriptDrawApi.method2601();
         String var3x = ScriptValueUtils.method6254(var1x, 0, "input");
         float var4x = (float)ScriptValueUtils.method6256(var1x, 1, 0.0);
         float var5x = (float)ScriptValueUtils.method6256(var1x, 2, 0.0);
         float var6x = (float)Math.max(30.0, ScriptValueUtils.method6256(var1x, 3, 100.0));
         String var7 = ScriptValueUtils.method6254(var1x, 4, "");
         var var8 = method641(var0);
         var8.computeIfAbsent("in:" + var3x, var1xx -> ScriptValueUtils.method6254(var1x, 5, ""));
         float var9 = 14.0F;
         if (method638(var4x, var5x, var6x, var9)) {
            items2.put(var0, var3x);
         } else if (longValue2 == longValue && !Float.isNaN(floatValue) && var3x.equals(items2.get(var0))) {
            items2.remove(var0);
         }

         boolean var10 = var3x.equals(items2.get(var0));
         String var11 = String.valueOf(var8.get("in:" + var3x));
         Font var12 = font(6.0F);
         var2x.drawRoundedRect(var4x, var5x, var6x, var9, ProxyDotRadiusData.method8069(3.0F), new ZenithColor(1342177280));
         var2x.drawRoundedBorder(var4x, var5x, var6x, var9, 0.5F, ProxyDotRadiusData.method8069(3.0F), new ZenithColor(var10 ? method642() : 822083583));
         String var13 = var11.isEmpty() && !var10 ? var7 : var11;

         while (var13.length() > 1 && var12.width(var13) > var6x - 10.0F) {
            var13 = var13.substring(1);
         }

         var2x.drawText(var12, var13, var4x + 4.0F, var5x + (var9 - var12.height()) / 2.0F, new ZenithColor(var11.isEmpty() && !var10 ? -2130706433 : -1));
         if (var10 && System.currentTimeMillis() / 500L % 2L == 0L) {
            var2x.drawRect(var4x + 4.0F + var12.width(var13) + 1.0F, var5x + 3.0F, 0.7F, var9 - 6.0F, new ZenithColor(-1));
         }

         return var11;
      });
      var4.method6274("submitted", var1x -> Boolean.TRUE.equals(method641(var0).remove("submit:" + ScriptValueUtils.method6254(var1x, 0, "input"))));
      var4.method6274(
         "tabs",
         var1x -> {
            CustomDrawContext var2x = ScriptDrawApi.method2601();
            String var3x = ScriptValueUtils.method6254(var1x, 0, "tabs");
            var var4x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var1x, 1));
            float var5x = (float)ScriptValueUtils.method6256(var1x, 2, 0.0);
            float var6x = (float)ScriptValueUtils.method6256(var1x, 3, 0.0);
            float var7 = (float)ScriptValueUtils.method6256(var1x, 4, 120.0);
            float var8 = (float)ScriptValueUtils.method6256(var1x, 5, 14.0);
            var var9 = method641(var0);
            int var10 = ((Integer)var9.computeIfAbsent("tb:" + var3x, var0xx -> 0));
            if (var4x.isEmpty()) {
               return (double)var10;
            } else {
               float var11 = var7 / var4x.size();
               Font var12 = font(Math.min(7.0F, var8 * 0.45F));
               var2x.drawRoundedRect(var5x, var6x, var7, var8, ProxyDotRadiusData.method8069(var8 / 2.0F), new ZenithColor(1073741824));

               for (int var13 = 0; var13 < var4x.size(); var13++) {
                  float var14 = var5x + var11 * var13;
                  if (method638(var14, var6x, var11, var8)) {
                     var10 = var13;
                     var9.put("tb:" + var3x, var13);
                  }

                  if (var13 == var10) {
                     var2x.drawRoundedRect(
                        var14 + 1.0F,
                        var6x + 1.0F,
                        var11 - 2.0F,
                        var8 - 2.0F,
                        ProxyDotRadiusData.method8069((var8 - 2.0F) / 2.0F),
                        new ZenithColor(method642() & 16777215 | -1728053248)
                     );
                  }

                  String var15 = ScriptValueUtils.method6255(var4x.get(var13));
                  var2x.drawText(
                     var12,
                     var12.clip(var15, var11 - 6.0F),
                     var14 + (var11 - Math.min(var11 - 6.0F, var12.width(var15))) / 2.0F,
                     var6x + (var8 - var12.height()) / 2.0F,
                     new ZenithColor(var13 != var10 && !method639(var14, var6x, var11, var8) ? -1593835521 : -1)
                  );
               }

               return (double)var10;
            }
         }
      );
      var4.method6274(
         "list",
         var1x -> {
            CustomDrawContext var2x = ScriptDrawApi.method2601();
            String var3x = ScriptValueUtils.method6254(var1x, 0, "list");
            var var4x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var1x, 1));
            float var5x = (float)ScriptValueUtils.method6256(var1x, 2, 0.0);
            float var6x = (float)ScriptValueUtils.method6256(var1x, 3, 0.0);
            float var7 = (float)ScriptValueUtils.method6256(var1x, 4, 100.0);
            float var8 = (float)Math.max(8.0, ScriptValueUtils.method6256(var1x, 5, 12.0));
            int var9 = Math.max(1, Math.min(200, ScriptValueUtils.method6257(var1x, 6, 10)));
            var var10 = method641(var0);
            int var11 = ((Integer)var10.computeIfAbsent("ls:" + var3x, var0xx -> -1));
            int var12 = ((Integer)var10.computeIfAbsent("lo:" + var3x, var0xx -> 0));
            var12 = Math.max(0, Math.min(var12, Math.max(0, var4x.size() - var9)));
            Font var13 = font(Math.min(7.0F, var8 * 0.55F));

            for (int var14 = 0; var14 < Math.min(var9, var4x.size() - var12); var14++) {
               int var15 = var14 + var12;
               float var16 = var6x + var14 * var8;
               boolean var17 = method639(var5x, var16, var7, var8);
               if (method638(var5x, var16, var7, var8)) {
                  var11 = var15;
                  var10.put("ls:" + var3x, var15);
               }

               if (var15 == var11 || var17) {
                  var2x.drawRoundedRect(
                     var5x,
                     var16,
                     var7,
                     var8,
                     ProxyDotRadiusData.method8069(3.0F),
                     new ZenithColor(var15 == var11 ? method642() & 16777215 | 1879048192 : 637534207)
                  );
               }

               var2x.drawText(
                  var13,
                  var13.clip(ScriptValueUtils.method6255(var4x.get(var15)), var7 - 8.0F),
                  var5x + 4.0F,
                  var16 + (var8 - var13.height()) / 2.0F,
                  new ZenithColor(-251658241)
               );
            }

            if (var4x.size() > var9) {
               float var19 = var9 * var8;
               float var20 = var19 * var9 / var4x.size();
               float var21 = var6x + (var19 - var20) * var12 / Math.max(1, var4x.size() - var9);
               var2x.drawRoundedRect(var5x + var7 - 2.0F, var21, 2.0F, var20, ProxyDotRadiusData.method8069(1.0F), new ZenithColor(1627389951));
            }

            return (double)var11;
         }
      );
      var4.method6274("scrollList", var1x -> {
         var var2x = method641(var0);
         String var3x = ScriptValueUtils.method6254(var1x, 0, "list");
         int var4x = ((Integer)var2x.getOrDefault("lo:" + var3x, 0));
         var2x.put("lo:" + var3x, Math.max(0, var4x + ScriptValueUtils.method6257(var1x, 1, 1)));
         return null;
      });
      var4.method6274("graph", var0x -> {
         CustomDrawContext var1x = ScriptDrawApi.method2601();
         var var2x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var0x, 0));
         float var3x = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
         float var4x = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
         float var5x = (float)ScriptValueUtils.method6256(var0x, 3, 100.0);
         float var6x = (float)ScriptValueUtils.method6256(var0x, 4, 40.0);
         int var7 = ScriptValueUtils.method6258(var0x, 5, method642());
         Scriptable var8 = ScriptValueUtils.method6262(var0x, 6);
         if (var2x.size() < 2) {
            return null;
         } else {
            double[] var9 = new double[Math.min(var2x.size(), 512)];
            double var10 = Double.MAX_VALUE;
            double var12 = -Double.MAX_VALUE;

            for (int var14 = 0; var14 < var9.length; var14++) {
               var9[var14] = Context.toNumber(var2x.get(var2x.size() - var9.length + var14));
               var10 = Math.min(var10, var9[var14]);
               var12 = Math.max(var12, var9[var14]);
            }

            if (var8 != null) {
               var10 = ScriptValueUtils.method6264(var8, "min", var10);
               var12 = ScriptValueUtils.method6264(var8, "max", var12);
            }

            if (var12 - var10 < 1.0E-9) {
               var12 = var10 + 1.0;
            }

            boolean var26 = var8 == null || ScriptValueUtils.method6265(var8, "fill", true);
            float var15 = var5x / (var9.length - 1);
            ZenithColor var16 = new ZenithColor(var7 & 16777215 | 805306368);
            ZenithColor var17 = new ZenithColor(var7);

            for (int var18 = 0; var18 < var9.length; var18++) {
               float var19 = var3x + var15 * var18;
               float var20 = (float)(var4x + var6x - (var9[var18] - var10) / (var12 - var10) * var6x);
               if (var26) {
                  var1x.drawRect(var19, var20, Math.max(1.0F, var15), var4x + var6x - var20, var16);
               }

               if (var18 > 0) {
                  float var21 = var3x + var15 * (var18 - 1);
                  float var22 = (float)(var4x + var6x - (var9[var18 - 1] - var10) / (var12 - var10) * var6x);
                  float var23 = Math.max(1.0F, Math.abs(var20 - var22));

                  for (int var24 = 0; var24 <= var23; var24++) {
                     float var25 = var24 / var23;
                     var1x.drawRect(var21 + (var19 - var21) * var25, var22 + (var20 - var22) * var25, Math.max(1.0F, var15 * 0.6F), 1.0F, var17);
                  }
               }
            }

            return null;
         }
      });
      var4.method6274(
         "progress",
         var0x -> {
            CustomDrawContext var1x = ScriptDrawApi.method2601();
            float var2x = (float)ScriptValueUtils.method6256(var0x, 0, 0.0);
            float var3x = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
            float var4x = (float)ScriptValueUtils.method6256(var0x, 2, 80.0);
            float var5x = (float)ScriptValueUtils.method6256(var0x, 3, 4.0);
            double var6x = MathHelper.clamp(ScriptValueUtils.method6256(var0x, 4, 0.0), 0.0, 1.0);
            var1x.drawRoundedRect(var2x, var3x, var4x, var5x, ProxyDotRadiusData.method8069(var5x / 2.0F), new ZenithColor(1090519039));
            var1x.drawRoundedRect(
               var2x,
               var3x,
               (float)(var4x * var6x),
               var5x,
               ProxyDotRadiusData.method8069(var5x / 2.0F),
               new ZenithColor(ScriptValueUtils.method6258(var0x, 5, method642()))
            );
            return null;
         }
      );
      var4.method6274("value", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "");
         var var3x = method641(var0);

         for (String var7 : new String[]{"cb:", "sl:", "in:", "tb:", "ls:"}) {
            Object var8 = var3x.get(var7 + var2x);
            if (var8 != null) {
               return var8 instanceof Integer var9 ? (double)var9.intValue() : var8;
            }
         }

         return null;
      });
      var4.method6274("setValue", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "");
         Object var3x = ScriptValueUtils.arg(var1x, 1);
         var var4x = method641(var0);
         if (var3x instanceof Boolean var5x) {
            var4x.put("cb:" + var2x, var5x);
         } else if (var3x instanceof Number var6x) {
            var4x.put("sl:" + var2x, var6x.doubleValue());
            var4x.put("tb:" + var2x, var6x.intValue());
            var4x.put("ls:" + var2x, var6x.intValue());
         } else {
            var4x.put("in:" + var2x, ScriptValueUtils.method6255(var3x));
         }

         return null;
      });
      var4.method6274("island", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, var0.method1403());
         String var3x = ScriptValueUtils.method6254(var1x, 1, "");
         long var4x = (long)MathHelper.clamp(ScriptValueUtils.method6256(var1x, 2, 3000.0), 500.0, 20000.0);
         Scriptable var6x = ScriptValueUtils.method6262(var1x, 3);
         int var7 = var6x == null ? method642() : (int)((long)ScriptValueUtils.method6264(var6x, "color", method642() & 4294967295L));
         RemoteUtil.Entry var8 = null;
         if (var6x != null && !ScriptValueUtils.method6253(ScriptValueUtils.method6263(var6x, "icon"))) {
            var8 = RemoteUtil.method1827(ScriptValueUtils.method6263(var6x, "icon"));
         }

         items4.removeIf(var1xx -> var1xx.script == var0);
         if (items4.size() >= 3) {
            items4.remove(0);
         }

         items4.add(new ScriptImGui.Data(var0, var2x, var3x, var7, var8, System.currentTimeMillis(), var4x));
         return null;
      });
      ScriptValueUtils.Entry2 var5 = ScriptValueUtils.method6247(var2);
      var5.method6274("hide", var2x -> {
         entry = method647(var0, var1);
         entry.flag = true;
         return null;
      });
      var5.method6274("set", var2x -> {
         ScriptImGui.Entry var3x = method647(var0, var1);
         Object var4x = ScriptValueUtils.arg(var2x, 0);
         if (var4x instanceof Function var5x) {
            var3x.function = var5x;
         } else {
            var3x.entry = RemoteUtil.method1827(var4x);
         }

         var3x.size = (float)MathHelper.clamp(ScriptValueUtils.method6256(var2x, 1, 16.0), 2.0, 128.0);
         entry = var3x;
         return null;
      });
      var5.method6274("reset", var1x -> {
         if (entry != null && entry.script == var0) {
            entry = null;
         }

         return null;
      });
      var5.method6274("custom", var0x -> entry != null);
      ScriptableObject var6 = (ScriptableObject)var2;
      ScriptValueUtils.method6246(var2, var6).method6277("crosshair", var5.getScriptableObject());
   }

   private static ScriptImGui.Entry method647(Script var0, Storage var1) {
      ScriptImGui.Entry var2 = new ScriptImGui.Entry();
      var2.script = var0;
      var2.storage = var1;
      return var2;
   }

   public static boolean method648() {
      ScriptImGui.Entry var0 = entry;
      if (var0 == null) {
         return false;
      } else if (!var0.script.isRunning()) {
         entry = null;
         return false;
      } else {
         return true;
      }
   }

   private static void method649(CustomDrawContext var0) {
      try {
         method650(var0);
      } finally {
         items6.clear();
      }
   }

   private static void method650(CustomDrawContext var0) {
      int var1 = method632().getWindow().getScaledWidth();
      int var2 = method632().getWindow().getScaledHeight();
      ScriptImGui.Entry var3 = entry;
      if (var3 != null && var3.script.isRunning() && !var3.flag && method632().currentScreen == null) {
         float var4 = var1 / 2.0F;
         float var5 = var2 / 2.0F;
         if (var3.entry != null && !var3.entry.flag) {
            Identifier var6 = var3.entry.texture();
            if (var6 != null) {
               var0.drawRoundedTexture(
                  var6, var4 - var3.size / 2.0F, var5 - var3.size / 2.0F, var3.size, var3.size, ProxyDotRadiusData.method8069(0.0F), new ZenithColor(-1)
               );
            }
         } else if (var3.function != null) {
            ScriptDrawApi.method2597(var0);

            try {
               var3.storage.method1321(var3.script, var3.function, (double)var4, (double)var5);
            } finally {
               ScriptDrawApi.end();
            }
         }
      }

      for (Object[] var26 : items6) {
         Vec3d var27 = ProjectionUtils.method301(new Vec3d((Double)var26[1], (Double)var26[2], (Double)var26[3]));
         if (!(var27.z <= 0.0) && !(var27.z >= 1.0)) {
            float var7 = (Float)var26[4];
            var0.getMatrices().pushMatrix();
            var0.getMatrices().translate((float)var27.x - var7 / 2.0F, (float)var27.y - var7 / 2.0F);
            var0.getMatrices().scale(var7 / 16.0F, var7 / 16.0F);
            var0.drawItem((ItemStack)var26[0], 0, 0);
            var0.getMatrices().popMatrix();
         }
      }

      long var25 = System.currentTimeMillis();
      float var28 = 6.0F;

      for (ScriptImGui.Data var8 : items4) {
         long var9 = var25 - var8.longValue;
         if (var9 <= var8.longValue2 && var8.script.isRunning()) {
            float var11 = (float)Math.min(1.0, Math.min(var9, var8.longValue2 - var9) / 220.0);
            float var12 = 1.0F - (1.0F - var11) * (1.0F - var11) * (1.0F - var11);
            Font var13 = Fonts.NEW_SEMIBOLD.getFont(6.5F);
            Font var14 = Fonts.NEW_MEDIUM.getFont(5.5F);
            float var15 = var8.entry == null ? 0.0F : 16.0F;
            float var16 = Math.max(var13.width(var8.text), var14.width(var8.text2)) + 20.0F + var15 + (var15 > 0.0F ? 6 : 0);
            float var17 = var8.text2.isEmpty() ? 18.0F : 26.0F;
            float var18 = (var1 - var16 * (0.6F + 0.4F * var12)) / 2.0F;
            float var19 = var16 * (0.6F + 0.4F * var12);
            var0.drawBlurHud(var18, var28, var19, var17, 14.0F, ProxyDotRadiusData.method8069(var17 / 2.0F), new ZenithColor(-1341387758).method7294(var12));
            var0.drawRoundedRect(
               var18 + 6.0F, var28 + var17 / 2.0F - 2.0F, 4.0F, 4.0F, ProxyDotRadiusData.method8069(2.0F), new ZenithColor(var8.intValue).method7294(var12)
            );
            float var20 = var18 + 14.0F;
            if (var8.entry != null && !var8.entry.flag && var8.entry.texture() != null) {
               var0.drawRoundedTexture(
                  var8.entry.texture(),
                  var20,
                  var28 + (var17 - var15) / 2.0F,
                  var15,
                  var15,
                  ProxyDotRadiusData.method8069(4.0F),
                  new ZenithColor(-1).method7294(var12)
               );
               var20 += var15 + 6.0F;
            }

            if (var12 > 0.5F) {
               float var21 = (var12 - 0.5F) * 2.0F;
               var0.drawText(
                  var13,
                  var13.clip(var8.text, var19 - (var20 - var18) - 8.0F),
                  var20,
                  var28 + (var8.text2.isEmpty() ? (var17 - var13.height()) / 2.0F : 4.0F),
                  new ZenithColor(-1).method7294(var21)
               );
               if (!var8.text2.isEmpty()) {
                  var0.drawText(
                     var14,
                     var14.clip(var8.text2, var19 - (var20 - var18) - 8.0F),
                     var20,
                     var28 + 6.0F + var13.height(),
                     new ZenithColor(-1325400065).method7294(var21)
                  );
               }
            }

            var28 += (var17 + 4.0F) * var12;
         } else {
            items4.remove(var8);
         }
      }
   }

   private static void method651() {
      if (method632().world == null) {
         items5.clear();
      } else {
         long var0 = System.currentTimeMillis();

         for (ScriptImGui.Entry3 var3 : items5) {
            if (!var3.script.isRunning()) {
               items5.remove(var3);
            } else {
               Entity var4 = method632().world.getEntityById(var3.intValue);
               if (var4 != null) {
                  var3.deque.addLast(new double[]{var4.getX(), var4.getY() + var4.getHeight() * 0.1, var4.getZ(), var0});
               }

               while (!var3.deque.isEmpty() && var0 - ((double[])var3.deque.peekFirst())[3] > var3.longValue) {
                  var3.deque.pollFirst();
               }

               while (var3.deque.size() > 400) {
                  var3.deque.pollFirst();
               }
            }
         }
      }
   }

   private static void method652() {
      for (ScriptImGui.Entry3 var1 : items5) {
         double[] var2 = null;
         int var3 = 0;
         int var4 = var1.deque.size();

         for (double[] var6 : var1.deque) {
            if (var2 != null) {
               float var7 = (float)var3 / Math.max(1, var4);
               int var8 = (int)((var1.color >>> 24) * var7);
               GlowUtil.method2243(var2[0], var2[1], var2[2], var6[0], var6[1], var6[2], var8 << 24 | var1.color & 16777215, var1.width, false);
            }

            var2 = var6;
            var3++;
         }
      }
   }

   public static void method653(Script var0, Scriptable var1, ScriptableObject var2) {
      method633();
      var0.method1413(() -> items5.removeIf(var1x -> var1x.script == var0));
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6246(var1, var2);
      var3.method6274("trail", var1x -> {
         Entity var2x = ScriptScreenApi.method7048(ScriptValueUtils.arg(var1x, 0));
         if (var2x == null) {
            return false;
         } else {
            items5.removeIf(var2xx -> var2xx.script == var0 && var2xx.intValue == var2x.getId());
            if (items5.stream().filter(var1xx -> var1xx.script == var0).count() >= 32L) {
               throw ScriptValueUtils.method6251("больше 32 следов");
            } else {
               ScriptImGui.Entry3 var3x = new ScriptImGui.Entry3();
               var3x.script = var0;
               var3x.intValue = var2x.getId();
               var3x.longValue = (long)MathHelper.clamp(ScriptValueUtils.method6256(var1x, 1, 1500.0), 100.0, 20000.0);
               var3x.color = ScriptValueUtils.method6258(var1x, 2, method642());
               var3x.width = (float)MathHelper.clamp(ScriptValueUtils.method6256(var1x, 3, 2.0), 0.5, 8.0);
               items5.add(var3x);
               return true;
            }
         }
      });
      var3.method6274("clearTrails", var1x -> {
         items5.removeIf(var1xx -> var1xx.script == var0);
         return null;
      });
      var3.method6274(
         "sphere",
         var0x -> {
            ScriptDrawApi.method2602();
            double var1x = ScriptValueUtils.method6256(var0x, 0, 0.0);
            double var3x = ScriptValueUtils.method6256(var0x, 1, 0.0);
            double var5 = ScriptValueUtils.method6256(var0x, 2, 0.0);
            double var7 = MathHelper.clamp(ScriptValueUtils.method6256(var0x, 3, 1.0), 0.05, 64.0);
            int var9 = ScriptValueUtils.method6258(var0x, 4, method642());
            int var10 = MathHelper.clamp(ScriptValueUtils.method6257(var0x, 5, 16), 6, 48);
            float var11 = (float)MathHelper.clamp(ScriptValueUtils.method6256(var0x, 6, 1.5), 0.5, 6.0);

            for (int var12 = 1; var12 < var10 / 2; var12++) {
               double var13 = Math.PI * var12 / (var10 / 2.0);
               double var15 = Math.sin(var13) * var7;
               double var17 = var3x + Math.cos(var13) * var7;
               Vec3d var19 = null;

               for (int var20 = 0; var20 <= var10; var20++) {
                  double var21 = (Math.PI * 2) * var20 / var10;
                  Vec3d var23 = new Vec3d(var1x + Math.cos(var21) * var15, var17, var5 + Math.sin(var21) * var15);
                  if (var19 != null) {
                     GlowUtil.method2245(var19, var23, var9, var11, false);
                  }

                  var19 = var23;
               }
            }

            for (int var24 = 0; var24 < var10 / 2; var24++) {
               double var25 = (Math.PI * 2) * var24 / (var10 / 2.0);
               Vec3d var26 = null;

               for (int var16 = 0; var16 <= var10; var16++) {
                  double var27 = Math.PI * var16 / var10;
                  Vec3d var28 = new Vec3d(
                     var1x + Math.sin(var27) * Math.cos(var25) * var7, var3x + Math.cos(var27) * var7, var5 + Math.sin(var27) * Math.sin(var25) * var7
                  );
                  if (var26 != null) {
                     GlowUtil.method2245(var26, var28, var9, var11, false);
                  }

                  var26 = var28;
               }
            }

            return null;
         }
      );
      var3.method6274("beam", var0x -> {
         ScriptDrawApi.method2602();
         double var1x = ScriptValueUtils.method6256(var0x, 0, 0.0);
         double var3x = ScriptValueUtils.method6256(var0x, 1, 0.0);
         double var5 = ScriptValueUtils.method6256(var0x, 2, 0.0);
         double var7 = MathHelper.clamp(ScriptValueUtils.method6256(var0x, 3, 64.0), 0.1, 512.0);
         int var9 = ScriptValueUtils.method6258(var0x, 4, method642());
         float var10 = (float)MathHelper.clamp(ScriptValueUtils.method6256(var0x, 5, 3.0), 0.5, 10.0);
         GlowUtil.method2245(new Vec3d(var1x, var3x, var5), new Vec3d(var1x, var3x + var7, var5), var9, var10, false);
         GlowUtil.method2245(new Vec3d(var1x, var3x, var5), new Vec3d(var1x, var3x + var7, var5), var9 & 16777215 | 805306368, var10 * 4.0F, false);
         return null;
      });
      var3.method6274(
         "item",
         var0x -> {
            ItemStack var1x = ScriptDrawApi.method2608(ScriptValueUtils.method6254(var0x, 0, ""), 1);
            if (!var1x.isEmpty() && items6.size() < 256) {
               items6.add(
                  new Object[]{
                     var1x,
                     ScriptValueUtils.method6256(var0x, 1, 0.0),
                     ScriptValueUtils.method6256(var0x, 2, 0.0),
                     ScriptValueUtils.method6256(var0x, 3, 0.0),
                     (float)MathHelper.clamp(ScriptValueUtils.method6256(var0x, 4, 16.0), 4.0, 64.0)
                  }
               );
               return true;
            } else {
               return false;
            }
         }
      );
   }

   public static void method654(Scriptable var0, ScriptableObject var1) {
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6246(var0, var1);
      var2.method6274(
         "glow",
         var0x -> {
            CustomDrawContext var1x = ScriptDrawApi.method2601();
            float var2x = (float)ScriptValueUtils.method6256(var0x, 0, 0.0);
            float var3 = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
            float var4 = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
            float var5 = (float)ScriptValueUtils.method6256(var0x, 3, 0.0);
            int var6 = ScriptValueUtils.method6258(var0x, 4, method642());
            float var7 = (float)ScriptValueUtils.method6256(var0x, 5, 4.0);
            int var8 = MathHelper.clamp(ScriptValueUtils.method6257(var0x, 6, 8), 1, 24);
            int var9 = var6 >>> 24;

            for (int var10 = var8; var10 >= 1; var10--) {
               float var11 = (float)var10 / var8;
               int var12 = (int)(var9 * 0.18F * (1.0F - var11) * (1.0F - var11) + 2.0F);
               var1x.drawRoundedRect(
                  var2x - var10,
                  var3 - var10,
                  var4 + var10 * 2,
                  var5 + var10 * 2,
                  ProxyDotRadiusData.method8069(var7 + var10),
                  new ZenithColor(var12 << 24 | var6 & 16777215)
               );
            }

            return null;
         }
      );
      var2.method6274("glowText", var0x -> {
         CustomDrawContext var1x = ScriptDrawApi.method2601();
         String var2x = ScriptValueUtils.method6254(var0x, 0, "");
         float var3 = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
         float var4 = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
         int var5 = ScriptValueUtils.method6258(var0x, 3, -1);
         Font var6 = ScriptDrawApi.method2605(ScriptValueUtils.arg(var0x, 5), (float)ScriptValueUtils.method6256(var0x, 4, 8.0));
         int var7 = ScriptValueUtils.method6258(var0x, 6, method642());
         float var8 = (float)MathHelper.clamp(ScriptValueUtils.method6256(var0x, 7, 1.5), 0.5, 4.0);
         ZenithColor var9 = new ZenithColor((var7 >>> 24) / 6 << 24 | var7 & 16777215);

         for (int var10 = 1; var10 <= 2; var10++) {
            float var11 = var8 * var10 / 2.0F;

            for (int var12 = 0; var12 < 8; var12++) {
               double var13 = (Math.PI / 4) * var12;
               var1x.drawText(var6, var2x, var3 + (float)Math.cos(var13) * var11, var4 + (float)Math.sin(var13) * var11, var9);
            }
         }

         var1x.drawText(var6, var2x, var3, var4, new ZenithColor(var5));
         return (double)var6.width(var2x);
      });
      var2.method6274("outlineText", var0x -> {
         CustomDrawContext var1x = ScriptDrawApi.method2601();
         String var2x = ScriptValueUtils.method6254(var0x, 0, "");
         float var3 = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
         float var4 = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
         Font var5 = ScriptDrawApi.method2605(ScriptValueUtils.arg(var0x, 6), (float)ScriptValueUtils.method6256(var0x, 5, 8.0));
         ZenithColor var6 = new ZenithColor(ScriptValueUtils.method6258(var0x, 4, -16777216));
         float var7 = Math.max(0.4F, var5.height() / 14.0F);
         var1x.drawText(var5, var2x, var3 - var7, var4, var6);
         var1x.drawText(var5, var2x, var3 + var7, var4, var6);
         var1x.drawText(var5, var2x, var3, var4 - var7, var6);
         var1x.drawText(var5, var2x, var3, var4 + var7, var6);
         var1x.drawText(var5, var2x, var3, var4, new ZenithColor(ScriptValueUtils.method6258(var0x, 3, -1)));
         return (double)var5.width(var2x);
      });
      var2.method6274("chromaText", var0x -> {
         CustomDrawContext var1x = ScriptDrawApi.method2601();
         String var2x = ScriptValueUtils.method6254(var0x, 0, "");
         float var3 = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
         float var4 = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
         Font var5 = ScriptDrawApi.method2605(ScriptValueUtils.arg(var0x, 4), (float)ScriptValueUtils.method6256(var0x, 3, 8.0));
         double var6 = Math.max(1.0, ScriptValueUtils.method6256(var0x, 5, 8.0));
         double var8 = ScriptValueUtils.method6256(var0x, 6, 12.0);
         float var10 = var3;

         for (int var11 = 0; var11 < var2x.length(); var11++) {
            String var12 = String.valueOf(var2x.charAt(var11));
            float var13 = (float)((System.currentTimeMillis() / var6 + var11 * var8) % 360.0 / 360.0);
            var1x.drawText(var5, var12, var10, var4, new ZenithColor(Color.HSBtoRGB(var13, 0.75F, 1.0F) | 0xFF000000));
            var10 += var5.width(var12);
         }

         return (double)(var10 - var3);
      });
   }

   public static void method655(Script var0, Scriptable var1, ScriptableObject var2) {
      var0.method1413(() -> Util4.method1290(var0));
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6246(var1, var2);
      var3.method6274("skin", var1x -> {
         Object var2x = ScriptValueUtils.arg(var1x, 0);
         if (!ScriptValueUtils.method6253(var2x) && !(var2x instanceof Boolean)) {
            RemoteUtil.Entry var3x = RemoteUtil.method1827(var2x);
            if (var3x.width() == 64 && (var3x.height() == 64 || var3x.height() == 32)) {
               Identifier var4 = var3x.texture();
               if (var4 == null) {
                  throw ScriptValueUtils.method6251("картинка ещё не готова");
               } else {
                  Util4.method1288(var0, var4, ScriptValueUtils.method6259(var1x, 1, false));
                  return null;
               }
            } else {
               throw ScriptValueUtils.method6251("скин — картинка 64×64 (или 64×32)");
            }
         } else {
            Util4.method1288(var0, null, false);
            return null;
         }
      });
      var3.method6274("cape", var1x -> {
         Object var2x = ScriptValueUtils.arg(var1x, 0);
         if (!ScriptValueUtils.method6253(var2x) && !(var2x instanceof Boolean)) {
            RemoteUtil.Entry var3x = RemoteUtil.method1827(var2x);
            if (var3x.width() != 64 * var3x.height() / 32 && var3x.width() != 2 * var3x.height()) {
               throw ScriptValueUtils.method6251("плащ — картинка 64×32 (или кратная)");
            } else {
               Identifier var4 = var3x.texture();
               if (var4 == null) {
                  throw ScriptValueUtils.method6251("картинка ещё не готова");
               } else {
                  Util4.method1289(var0, var4);
                  return null;
               }
            }
         } else {
            Util4.method1289(var0, null);
            return null;
         }
      });
   }

   private static final class Data {
      private final Script script;
      private final String text;
      private final String text2;
      private final int intValue;
      private final RemoteUtil.Entry entry;
      private final long longValue;
      private final long longValue2;

      public Script script() {
         return this.script;
      }

      public String text2() {
         return this.text2;
      }

      public int intValue() {
         return this.intValue;
      }

      public RemoteUtil.Entry entry() {
         return this.entry;
      }

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.script, other.script) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.entry, other.entry) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(script, text, text2, intValue, entry, longValue, longValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "script=" + this.script + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "intValue=" + this.intValue + ", " + "entry=" + this.entry + ", " + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + "]";
      }

      private Data(Script var1, String var2, String var3, int var4, RemoteUtil.Entry var5, long var6, long var8) {
         this.script = var1;
         this.text = var2;
         this.text2 = var3;
         this.intValue = var4;
         this.entry = var5;
         this.longValue = var6;
         this.longValue2 = var8;
      }

      public Script getScript() {
         return this.script;
      }

      public String title() {
         return this.text;
      }

      public String text() {
         return this.text2;
      }

      public int color() {
         return this.intValue;
      }

      public RemoteUtil.Entry getEntry() {
         return this.entry;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public long getLongValue2() {
         return this.longValue2;
      }
   }

   private static final class Entry {
      public Script script;
      public Storage storage;
      public RemoteUtil.Entry entry;
      public Function function;
      public float size;
      public boolean flag;
   }

   public static final class Entry2 {
      public static float method656() {
         MinecraftClient var0 = MinecraftClient.getInstance();
         return (float)(var0.mouse.getX() * var0.getWindow().getScaledWidth() / Math.max(1, var0.getWindow().getWidth()));
      }

      public static float method657() {
         MinecraftClient var0 = MinecraftClient.getInstance();
         return (float)(var0.mouse.getY() * var0.getWindow().getScaledHeight() / Math.max(1, var0.getWindow().getHeight()));
      }
   }

   private static final class Entry3 {
      public Script script;
      public int intValue;
      public long longValue;
      public int color;
      public float width;
      public final Deque<double[]> deque = new ArrayDeque<>();
   }
}

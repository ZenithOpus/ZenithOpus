package org.zenith.client.scripting;

import java.awt.Color;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfFont;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ScriptDrawApi {
   private static CustomDrawContext customDrawContext;
   private static boolean flag;
   private static int intValue;
   private static int intValue2;
   private static final int CONST_intValue = 20000;

   private ScriptDrawApi() {
   }

   public static void method2597(CustomDrawContext var0) {
      customDrawContext = var0;
      intValue = 0;
      intValue2 = 0;
   }

   public static void end() {
      if (customDrawContext != null) {
         while (intValue > 0) {
            customDrawContext.disableScissor();
            intValue--;
         }
      }

      customDrawContext = null;
   }

   public static void method2598(MatrixStack var0) {
      flag = true;
      intValue2 = 0;
   }

   public static void method2599() {
      flag = false;
   }

   private static CustomDrawContext method2600() {
      if (customDrawContext == null) {
         throw ScriptValueUtils.method6251("render.* работает только внутри events.render2d");
      } else if (++intValue2 > 20000) {
         throw ScriptValueUtils.method6251("больше 20000 вызовов отрисовки за кадр");
      } else {
         return customDrawContext;
      }
   }

   public static CustomDrawContext method2601() {
      return method2600();
   }

   public static void method2602() {
      method2604();
   }

   public static int method2603() {
      return method2612();
   }

   private static void method2604() {
      if (!flag) {
         throw ScriptValueUtils.method6251("render3d.* работает только внутри events.render3d");
      } else if (++intValue2 > 20000) {
         throw ScriptValueUtils.method6251("больше 20000 вызовов отрисовки за кадр");
      }
   }

   public static Font method2605(Object var0, float var1) {
      String var3 = ScriptValueUtils.method6253(var0) ? "medium" : ScriptValueUtils.method6255(var0).toLowerCase(Locale.ROOT);

      MsdfFont var2 = switch (var3) {
         case "bold" -> Fonts.NEW_SEMIBOLD;
         case "semibold" -> Fonts.SEMIBOLD;
         case "regular" -> Fonts.NEW_REGULAR;
         case "icons" -> Fonts.NEW_ICONS;
         default -> Fonts.NEW_MEDIUM;
      };
      return var2.getFont(Math.max(3.0F, Math.min(64.0F, var1)));
   }

   private static ZenithColor method2606(Object[] var0, int var1, int var2) {
      return new ZenithColor(ScriptValueUtils.method6258(var0, var1, var2));
   }

   public static Scriptable method2607(Script var0, Storage var1, Scriptable var2) {
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6247(var2);
      var3.method6274(
         "text",
         var0x -> {
            String var1x = ScriptValueUtils.method6254(var0x, 0, "");
            Font var2x = method2605(ScriptValueUtils.arg(var0x, 5), (float)ScriptValueUtils.method6256(var0x, 4, 7.0));
            method2600()
               .drawText(
                  var2x, var1x, (float)ScriptValueUtils.method6256(var0x, 1, 0.0), (float)ScriptValueUtils.method6256(var0x, 2, 0.0), method2606(var0x, 3, -1)
               );
            return (double)var2x.width(var1x);
         }
      );
      var3.method6274(
         "textCenter",
         var0x -> {
            String var1x = ScriptValueUtils.method6254(var0x, 0, "");
            Font var2x = method2605(ScriptValueUtils.arg(var0x, 5), (float)ScriptValueUtils.method6256(var0x, 4, 7.0));
            float var3x = var2x.width(var1x);
            method2600()
               .drawText(
                  var2x,
                  var1x,
                  (float)ScriptValueUtils.method6256(var0x, 1, 0.0) - var3x / 2.0F,
                  (float)ScriptValueUtils.method6256(var0x, 2, 0.0),
                  method2606(var0x, 3, -1)
               );
            return (double)var3x;
         }
      );
      var3.method6274(
         "textRight",
         var0x -> {
            String var1x = ScriptValueUtils.method6254(var0x, 0, "");
            Font var2x = method2605(ScriptValueUtils.arg(var0x, 5), (float)ScriptValueUtils.method6256(var0x, 4, 7.0));
            float var3x = var2x.width(var1x);
            method2600()
               .drawText(
                  var2x,
                  var1x,
                  (float)ScriptValueUtils.method6256(var0x, 1, 0.0) - var3x,
                  (float)ScriptValueUtils.method6256(var0x, 2, 0.0),
                  method2606(var0x, 3, -1)
               );
            return (double)var3x;
         }
      );
      var3.method6274(
         "textWidth",
         var0x -> (double)method2605(ScriptValueUtils.arg(var0x, 2), (float)ScriptValueUtils.method6256(var0x, 1, 7.0))
            .width(ScriptValueUtils.method6254(var0x, 0, ""))
      );
      var3.method6274("textHeight", var0x -> (double)method2605(ScriptValueUtils.arg(var0x, 1), (float)ScriptValueUtils.method6256(var0x, 0, 7.0)).height());
      var3.method6274("rect", var0x -> {
         float var1x = (float)ScriptValueUtils.method6256(var0x, 5, 0.0);
         ZenithColor var2x = method2606(var0x, 4, -1);
         float var3x = (float)ScriptValueUtils.method6256(var0x, 0, 0.0);
         float var4 = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
         float var5 = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
         float var6 = (float)ScriptValueUtils.method6256(var0x, 3, 0.0);
         if (var1x <= 0.0F) {
            method2600().drawRect(var3x, var4, var5, var6, var2x);
         } else {
            method2600().drawRoundedRect(var3x, var4, var5, var6, ProxyDotRadiusData.method8069(var1x), var2x);
         }

         return null;
      });
      var3.method6274(
         "outline",
         var0x -> {
            method2600()
               .drawRoundedBorder(
                  (float)ScriptValueUtils.method6256(var0x, 0, 0.0),
                  (float)ScriptValueUtils.method6256(var0x, 1, 0.0),
                  (float)ScriptValueUtils.method6256(var0x, 2, 0.0),
                  (float)ScriptValueUtils.method6256(var0x, 3, 0.0),
                  (float)ScriptValueUtils.method6256(var0x, 4, 1.0),
                  ProxyDotRadiusData.method8069((float)ScriptValueUtils.method6256(var0x, 6, 0.0)),
                  method2606(var0x, 5, -1)
               );
            return null;
         }
      );
      var3.method6274("circle", var0x -> {
         float var1x = (float)ScriptValueUtils.method6256(var0x, 0, 0.0);
         float var2x = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
         float var3x = (float)ScriptValueUtils.method6256(var0x, 2, 1.0);
         method2600().drawRoundedRect(var1x - var3x, var2x - var3x, var3x * 2.0F, var3x * 2.0F, ProxyDotRadiusData.method8069(var3x), method2606(var0x, 3, -1));
         return null;
      });
      var3.method6274(
         "arc",
         var0x -> {
            float var1x = (float)ScriptValueUtils.method6256(var0x, 0, 0.0);
            float var2x = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
            float var3x = (float)ScriptValueUtils.method6256(var0x, 2, 5.0);
            method2600()
               .drawArcBorder(
                  var1x - var3x,
                  var2x - var3x,
                  var3x * 2.0F,
                  var3x * 2.0F,
                  (float)ScriptValueUtils.method6256(var0x, 4, 1.5),
                  (float)ScriptValueUtils.method6256(var0x, 3, 360.0),
                  1.0F,
                  method2606(var0x, 5, -1)
               );
            return null;
         }
      );
      var3.method6274(
         "blur",
         var0x -> {
            method2600()
               .drawBlurHud(
                  (float)ScriptValueUtils.method6256(var0x, 0, 0.0),
                  (float)ScriptValueUtils.method6256(var0x, 1, 0.0),
                  (float)ScriptValueUtils.method6256(var0x, 2, 0.0),
                  (float)ScriptValueUtils.method6256(var0x, 3, 0.0),
                  (float)ScriptValueUtils.method6256(var0x, 5, 12.0),
                  ProxyDotRadiusData.method8069((float)ScriptValueUtils.method6256(var0x, 4, 0.0)),
                  method2606(var0x, 6, 1711276032)
               );
            return null;
         }
      );
      var3.method6274("item", var0x -> {
         ItemStack var1x = method2608(ScriptValueUtils.method6254(var0x, 0, ""), ScriptValueUtils.method6257(var0x, 4, 1));
         if (var1x.isEmpty()) {
            return null;
         } else {
            float var2x = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
            float var3x = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
            float var4 = (float)ScriptValueUtils.method6256(var0x, 3, 16.0);
            CustomDrawContext var5 = method2600();
            var5.getMatrices().pushMatrix();
            var5.getMatrices().translate(var2x, var3x);
            var5.getMatrices().scale(var4 / 16.0F, var4 / 16.0F);
            var5.drawItem(var1x, 0, 0);
            if (var1x.getCount() > 1) {
               var5.drawStackOverlay(MinecraftClient.getInstance().textRenderer, var1x, 0, 0);
            }

            var5.getMatrices().popMatrix();
            return null;
         }
      });
      var3.method6274("clip", var1x -> {
         var var2x = ScriptValueUtils.method6260(var1x, 4);
         CustomDrawContext var3x = method2600();
         float var4 = (float)ScriptValueUtils.method6256(var1x, 0, 0.0);
         float var5 = (float)ScriptValueUtils.method6256(var1x, 1, 0.0);
         var3x.enableScissor(var4, var5, var4 + (float)ScriptValueUtils.method6256(var1x, 2, 0.0), var5 + (float)ScriptValueUtils.method6256(var1x, 3, 0.0));
         intValue++;

         try {
            var2x.call(Context.getCurrentContext(), var2, var2, new Object[0]);
         } finally {
            var3x.disableScissor();
            intValue--;
         }

         return null;
      });
      var3.method6274("width", var0x -> (double)ScriptEntityApi.method6575().getWindow().getScaledWidth());
      var3.method6274("height", var0x -> (double)ScriptEntityApi.method6575().getWindow().getScaledHeight());
      var3.method6274(
         "toScreen",
         var1x -> {
            Vec3d var2x = ProjectionUtils.method301(
               new Vec3d(ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0))
            );
            return !(var2x.z <= 0.0) && !(var2x.z >= 1.0)
               ? ScriptValueUtils.method6247(var2).method6277("x", var2x.x).method6277("y", var2x.y).getScriptableObject()
               : null;
         }
      );
      return var3.getScriptableObject();
   }

   public static ItemStack method2608(String var0, int var1) {
      if (var0.isBlank()) {
         return ItemStack.EMPTY;
      } else {
         Identifier var2 = Identifier.tryParse(var0.contains(":") ? var0 : "minecraft:" + var0);
         if (var2 != null && Registries.ITEM.containsId(var2)) {
            ItemStack var3 = Registries.ITEM.get(var2).getDefaultStack();
            var3.setCount(Math.max(1, Math.min(99, var1)));
            return var3;
         } else {
            return ItemStack.EMPTY;
         }
      }
   }

   public static Scriptable method2609(Scriptable var0) {
      ScriptValueUtils.Entry2 var1 = ScriptValueUtils.method6247(var0);
      var1.method6274(
         "box",
         var0x -> {
            method2604();
            Box var1x = new Box(
               ScriptValueUtils.method6256(var0x, 0, 0.0),
               ScriptValueUtils.method6256(var0x, 1, 0.0),
               ScriptValueUtils.method6256(var0x, 2, 0.0),
               ScriptValueUtils.method6256(var0x, 3, 0.0),
               ScriptValueUtils.method6256(var0x, 4, 0.0),
               ScriptValueUtils.method6256(var0x, 5, 0.0)
            );
            method2610(var1x, var0x, 6);
            return null;
         }
      );
      var1.method6274("blockBox", var0x -> {
         method2604();
         double var1x = Math.floor(ScriptValueUtils.method6256(var0x, 0, 0.0));
         double var3 = Math.floor(ScriptValueUtils.method6256(var0x, 1, 0.0));
         double var5 = Math.floor(ScriptValueUtils.method6256(var0x, 2, 0.0));
         method2610(new Box(var1x, var3, var5, var1x + 1.0, var3 + 1.0, var5 + 1.0), var0x, 3);
         return null;
      });
      var1.method6274("entityBox", var0x -> {
         method2604();
         Entity var1x = ScriptScreenApi.method7048(ScriptValueUtils.arg(var0x, 0));
         if (var1x == null) {
            return null;
         } else {
            float var2 = ScriptEntityApi.method6574();
            double var3 = var1x.lastX + (var1x.getX() - var1x.lastX) * var2 - var1x.getX();
            double var5 = var1x.lastY + (var1x.getY() - var1x.lastY) * var2 - var1x.getY();
            double var7 = var1x.lastZ + (var1x.getZ() - var1x.lastZ) * var2 - var1x.getZ();
            method2610(var1x.getBoundingBox().offset(var3, var5, var7), var0x, 1);
            return null;
         }
      });
      var1.method6274(
         "line",
         var0x -> {
            method2604();
            GlowUtil.method2245(
               new Vec3d(ScriptValueUtils.method6256(var0x, 0, 0.0), ScriptValueUtils.method6256(var0x, 1, 0.0), ScriptValueUtils.method6256(var0x, 2, 0.0)),
               new Vec3d(ScriptValueUtils.method6256(var0x, 3, 0.0), ScriptValueUtils.method6256(var0x, 4, 0.0), ScriptValueUtils.method6256(var0x, 5, 0.0)),
               ScriptValueUtils.method6258(var0x, 6, -1),
               (float)Math.max(0.5, Math.min(10.0, ScriptValueUtils.method6256(var0x, 7, 2.0))),
               ScriptValueUtils.method6259(var0x, 8, false)
            );
            return null;
         }
      );
      return var1.getScriptableObject();
   }

   private static void method2610(Box var0, Object[] var1, int var2) {
      int var3 = ScriptValueUtils.method6258(var1, var2, 1090519039);
      int var4 = ScriptValueUtils.method6258(var1, var2 + 1, 0);
      float var5 = (float)Math.max(0.5, Math.min(10.0, ScriptValueUtils.method6256(var1, var2 + 2, 2.0)));
      boolean var6 = ScriptValueUtils.method6259(var1, var2 + 3, false);
      if (var3 >>> 24 != 0) {
         GlowUtil.method2232(var0, var3, var5, false, true, var6);
      }

      if (var4 >>> 24 != 0) {
         GlowUtil.method2232(var0, var4, var5, true, false, var6);
      }
   }

   public static Scriptable method2611(Scriptable var0) {
      ScriptValueUtils.Entry2 var1 = ScriptValueUtils.method6247(var0);
      var1.method6274(
         "rgb",
         var0x -> (double)method2614(
            ScriptValueUtils.method6257(var0x, 3, 255),
            ScriptValueUtils.method6257(var0x, 0, 255),
            ScriptValueUtils.method6257(var0x, 1, 255),
            ScriptValueUtils.method6257(var0x, 2, 255)
         )
      );
      var1.method6274("hex", var0x -> {
         String var1x = ScriptValueUtils.method6254(var0x, 0, "").replace("#", "");

         try {
            long var2 = Long.parseLong(var1x, 16);
            if (var1x.length() <= 6) {
               var2 |= 4278190080L;
            }

            return (double)((int)var2);
         } catch (NumberFormatException var4) {
            return -1.0;
         }
      });
      var1.method6274("alpha", var0x -> {
         int var1x = ScriptValueUtils.method6258(var0x, 0, -1);
         int var2 = (int)Math.round(Math.max(0.0, Math.min(1.0, ScriptValueUtils.method6256(var0x, 1, 1.0))) * 255.0);
         return (double)(var1x & 16777215 | var2 << 24);
      });
      var1.method6274(
         "mix",
         var0x -> {
            int var1x = ScriptValueUtils.method6258(var0x, 0, -1);
            int var2 = ScriptValueUtils.method6258(var0x, 1, -1);
            double var3 = Math.max(0.0, Math.min(1.0, ScriptValueUtils.method6256(var0x, 2, 0.5)));
            return (double)method2614(
               method2613(var1x >>> 24, var2 >>> 24, var3),
               method2613(var1x >> 16 & 0xFF, var2 >> 16 & 0xFF, var3),
               method2613(var1x >> 8 & 0xFF, var2 >> 8 & 0xFF, var3),
               method2613(var1x & 0xFF, var2 & 0xFF, var3)
            );
         }
      );
      var1.method6274(
         "hsb",
         var0x -> {
            int var1x = Color.HSBtoRGB(
               (float)ScriptValueUtils.method6256(var0x, 0, 0.0),
               (float)ScriptValueUtils.method6256(var0x, 1, 1.0),
               (float)ScriptValueUtils.method6256(var0x, 2, 1.0)
            );
            int var2 = (int)Math.round(Math.max(0.0, Math.min(1.0, ScriptValueUtils.method6256(var0x, 3, 1.0))) * 255.0);
            return (double)(var1x & 16777215 | var2 << 24);
         }
      );
      var1.method6274("rainbow", var0x -> {
         double var1x = Math.max(1.0, ScriptValueUtils.method6256(var0x, 0, 10.0));
         double var3 = ScriptValueUtils.method6256(var0x, 1, 0.0);
         float var5 = (float)((System.currentTimeMillis() / var1x + var3) % 360.0 / 360.0);
         return (double)(Color.HSBtoRGB(var5, (float)ScriptValueUtils.method6256(var0x, 2, 0.8), 1.0F) | 0xFF000000);
      });
      var1.method6274("accent", var0x -> (double)method2612());
      var1.method6274("red", var0x -> (double)(ScriptValueUtils.method6258(var0x, 0, 0) >> 16 & 0xFF));
      var1.method6274("green", var0x -> (double)(ScriptValueUtils.method6258(var0x, 0, 0) >> 8 & 0xFF));
      var1.method6274("blue", var0x -> (double)(ScriptValueUtils.method6258(var0x, 0, 0) & 0xFF));
      var1.method6274("alphaOf", var0x -> (double)(ScriptValueUtils.method6258(var0x, 0, 0) >>> 24));
      return var1.getScriptableObject();
   }

   private static int method2612() {
      try {
         return Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287();
      } catch (RuntimeException var1) {
         return -9663233;
      }
   }

   private static int method2613(int var0, int var1, double var2) {
      return (int)Math.round(var0 + (var1 - var0) * var2);
   }

   private static int method2614(int var0, int var1, int var2, int var3) {
      return method2615(var0) << 24 | method2615(var1) << 16 | method2615(var2) << 8 | method2615(var3);
   }

   private static int method2615(int var0) {
      return Math.max(0, Math.min(255, var0));
   }
}

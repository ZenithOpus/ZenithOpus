package org.zenith.client.scripting;

import com.darkmagician6.eventapi.EventManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
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
import org.zenith.utility.misc.HudHolder;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ScriptGuiApi {
   private static final int CONST_intValue = 2500;
   private static final int CONST_intValue2 = 400;
   private static final int CONST_intValue3 = 64;
   private static final List<ScriptGuiApi.Entry> items = new CopyOnWriteArrayList<>();
   private static final List<ScriptGuiApi.Data> items2 = new ArrayList<>();
   private static final List<ScriptGuiApi.Data2> items3 = new CopyOnWriteArrayList<>();
   private static final Map<String, ScriptGuiApi.Entry3> items4 = new ConcurrentHashMap<>();
   private static boolean flag;
   private static final Map<Script, Long> items5 = new WeakHashMap<>();

   private ScriptGuiApi() {
   }

   public static synchronized void method5309() {
      if (!flag) {
         flag = true;
         EventManager.main().subscribe(TickEvent.class, (byte)4, (var0, var1) -> tick());
         EventManager.main().subscribe(DrawEvent.class, (byte)4, (var0, var1) -> method5312((DrawEvent)var1));
         EventManager.main().subscribe(WorldRenderEvent.class, (byte)4, (var0, var1) -> method5314());
         EventManager.main().subscribe(MouseButtonEvent.class, (byte)0, (var0, var1) -> method5318((MouseButtonEvent)var1));
      }
   }

   private static boolean method5310(ScriptGuiApi.Entry var0) {
      return var0.intValue2 >= var0.intValue || !var0.script.isRunning();
   }

   private static void tick() {
      boolean var0 = false;

      for (ScriptGuiApi.Entry var2 : items) {
         var2.intValue2++;
         if (method5310(var2)) {
            var0 = true;
         } else {
            var2.doubleValue5 = var2.doubleValue5 - var2.doubleValue7;
            var2.doubleValue4 = var2.doubleValue4 * var2.doubleValue8;
            var2.doubleValue5 = var2.doubleValue5 * var2.doubleValue8;
            var2.doubleValue6 = var2.doubleValue6 * var2.doubleValue8;
            var2.doubleValue = var2.doubleValue + var2.doubleValue4;
            var2.doubleValue2 = var2.doubleValue2 + var2.doubleValue5;
            var2.doubleValue3 = var2.doubleValue3 + var2.doubleValue6;
            var2.floatValue2 = var2.floatValue2 + var2.floatValue;
         }
      }

      if (var0) {
         items.removeIf(ScriptGuiApi::method5310);
      }
   }

   private static float method5311(int var0, double var1, double var3) {
      return (float)(var0 / (var1 * Math.max(0.3, var3)));
   }

   private static void method5312(DrawEvent var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      CustomDrawContext var2 = var0.getCustomDrawContext();
      if (var1.player != null && (!items.isEmpty() || !items2.isEmpty() || !items3.isEmpty())) {
         Vec3d var3 = var1.gameRenderer.getCamera().getCameraPos();
         float var4 = var0.getTickDelta();
         int var5 = var1.getWindow().getScaledHeight();
         double var6 = 2.0 * Math.tan(Math.toRadians(var1.options.getFov().getValue().intValue()) / 2.0);

         for (ScriptGuiApi.Entry var9 : items) {
            double var10 = var9.doubleValue + var9.doubleValue4 * var4;
            double var12 = var9.doubleValue2 + var9.doubleValue5 * var4;
            double var14 = var9.doubleValue3 + var9.doubleValue6 * var4;
            Vec3d var16 = new Vec3d(var10, var12, var14);
            Vec3d var17 = ProjectionUtils.method301(var16);
            if (!(var17.z <= 0.0) && !(var17.z >= 1.0)) {
               float var18 = var9.size * method5311(var5, var6, var3.distanceTo(var16));
               if (!(var18 < 0.5F) && !(var18 > 600.0F)) {
                  float var19 = var9.flag ? 1.0F - (var9.intValue2 + var4) / var9.intValue : 1.0F;
                  ZenithColor var20 = new ZenithColor(var9.color).method7294(Math.max(0.0F, var19));
                  if (var9.entry != null && !var9.entry.flag) {
                     Identifier var31 = var9.entry.texture();
                     if (var31 != null) {
                        float var23 = var18 * var9.entry.height() / Math.max(1, var9.entry.width());
                        var2.getMatrices().pushMatrix();
                        var2.getMatrices().translate((float)var17.x, (float)var17.y);
                        if (var9.floatValue2 != 0.0F) {
                           var2.getMatrices().rotate((float)Math.toRadians(var9.floatValue2));
                        }

                        var2.drawRoundedTexture(var31, -var18 / 2.0F, -var23 / 2.0F, var18, var23, ProxyDotRadiusData.method8069(0.0F), var20);
                        var2.getMatrices().popMatrix();
                     }
                  } else if (var9.text != null) {
                     Font var21 = Fonts.NEW_SEMIBOLD.getFont(Math.max(3.0F, Math.min(48.0F, var18)));
                     var2.drawText(var21, var9.text, (float)var17.x - var21.width(var9.text) / 2.0F, (float)var17.y - var21.height() / 2.0F, var20);
                  }
               }
            }
         }

         for (ScriptGuiApi.Data var26 : items2) {
            method5313(
               var2, var3, var5, var6, var26.text, var26.doubleValue, var26.doubleValue2, var26.doubleValue3, var26.floatValue, var26.intValue, var26.intValue2
            );
         }

         items2.clear();

         for (ScriptGuiApi.Data2 var27 : items3) {
            if (var27.script.isRunning()) {
               double var28 = var1.player.getEntityPos().distanceTo(new Vec3d(var27.doubleValue, var27.doubleValue2, var27.doubleValue3));
               String var29 = var27.text + "  " + (int)var28 + "m";
               Vec3d var13 = ProjectionUtils.method301(new Vec3d(var27.doubleValue, var27.doubleValue2 + 1.2, var27.doubleValue3));
               if (!(var13.z <= 0.0) && !(var13.z >= 1.0)) {
                  Font var30 = Fonts.NEW_MEDIUM.getFont(6.0F);
                  float var15 = var30.width(var29) + 10.0F;
                  var2.drawRoundedRect(
                     (float)var13.x - var15 / 2.0F, (float)var13.y - 6.0F, var15, 12.0F, ProxyDotRadiusData.method8069(6.0F), new ZenithColor(-1341124588)
                  );
                  var2.drawRoundedRect(
                     (float)var13.x - var15 / 2.0F + 3.0F,
                     (float)var13.y - 1.5F,
                     3.0F,
                     3.0F,
                     ProxyDotRadiusData.method8069(1.5F),
                     new ZenithColor(var27.intValue)
                  );
                  var2.drawText(var30, var29, (float)var13.x - var15 / 2.0F + 8.0F, (float)var13.y - var30.height() / 2.0F, new ZenithColor(-1));
               }
            }
         }
      } else {
         items2.clear();
      }

      method5317(var2);
   }

   private static void method5313(
      CustomDrawContext var0, Vec3d var1, int var2, double var3, String var5, double var6, double var8, double var10, float var12, int var13, int var14
   ) {
      Vec3d var15 = new Vec3d(var6, var8, var10);
      Vec3d var16 = ProjectionUtils.method301(var15);
      if (!(var16.z <= 0.0) && !(var16.z >= 1.0)) {
         float var17 = Math.max(3.0F, Math.min(40.0F, var12 * method5311(var2, var3, var1.distanceTo(var15)) * 0.25F));
         Font var18 = Fonts.NEW_MEDIUM.getFont(var17);
         float var19 = var18.width(var5);
         if (var14 >>> 24 != 0) {
            var0.drawRoundedRect(
               (float)var16.x - var19 / 2.0F - var17 * 0.4F,
               (float)var16.y - var18.height() / 2.0F - var17 * 0.25F,
               var19 + var17 * 0.8F,
               var18.height() + var17 * 0.5F,
               ProxyDotRadiusData.method8069(var17 * 0.3F),
               new ZenithColor(var14)
            );
         }

         var0.drawText(var18, var5, (float)var16.x - var19 / 2.0F, (float)var16.y - var18.height() / 2.0F, new ZenithColor(var13));
      }
   }

   private static void method5314() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0.player != null) {
         for (ScriptGuiApi.Data2 var2 : items3) {
            if (var2.flag && var2.script.isRunning()) {
               GlowUtil.method2245(
                  new Vec3d(var2.doubleValue, var2.doubleValue2, var2.doubleValue3),
                  new Vec3d(var2.doubleValue, var2.doubleValue2 + 64.0, var2.doubleValue3),
                  var2.intValue,
                  2.0F,
                  false
               );
            }
         }
      }
   }

   private static float method5315() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      return (float)(var0.mouse.getX() * var0.getWindow().getScaledWidth() / Math.max(1, var0.getWindow().getWidth()));
   }

   private static float method5316() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      return (float)(var0.mouse.getY() * var0.getWindow().getScaledHeight() / Math.max(1, var0.getWindow().getHeight()));
   }

   private static void method5317(CustomDrawContext var0) {
      if (!items4.isEmpty()) {
         MinecraftClient var1 = MinecraftClient.getInstance();
         boolean var2 = var1.currentScreen instanceof ChatScreen;
         float var3 = method5315();
         float var4 = method5316();

         for (ScriptGuiApi.Entry3 var6 : items4.values()) {
            if (var6.script.isRunning() && var6.visible) {
               if (var6.dragging) {
                  var6.floatValue = Math.max(0.0F, Math.min(var1.getWindow().getScaledWidth() - var6.floatValue3, var3 - var6.floatValue5));
                  var6.floatValue2 = Math.max(0.0F, Math.min((float)(var1.getWindow().getScaledHeight() - 12), var4 - var6.floatValue6));
               }

               float var7 = var6.flag ? 12.0F : 0.0F;
               if (var6.flag) {
                  var0.drawBlurHud(
                     var6.floatValue,
                     var6.floatValue2,
                     var6.floatValue3,
                     var6.floatValue4 + var7,
                     12.0F,
                     ProxyDotRadiusData.method8069(5.0F),
                     new ZenithColor(2047609874)
                  );
                  var0.drawRoundedBorder(
                     var6.floatValue,
                     var6.floatValue2,
                     var6.floatValue3,
                     var6.floatValue4 + var7,
                     0.5F,
                     ProxyDotRadiusData.method8069(5.0F),
                     new ZenithColor(var2 ? 1728053247 : 587202559)
                  );
                  Font var8 = Fonts.NEW_MEDIUM.getFont(5.0F);
                  var0.drawText(
                     var8,
                     var8.clip(var6.title, var6.floatValue3 - 10.0F),
                     var6.floatValue + 5.0F,
                     var6.floatValue2 + (var7 - var8.height()) / 2.0F,
                     new ZenithColor(-419430401)
                  );
                  if (var2) {
                     var0.drawRoundedRect(
                        var6.floatValue + var6.floatValue3 - 9.0F,
                        var6.floatValue2 + 4.5F,
                        4.0F,
                        4.0F,
                        ProxyDotRadiusData.method8069(2.0F),
                        new ZenithColor(-1426063361)
                     );
                  }
               }

               if (var6.function != null) {
                  var0.getMatrices().pushMatrix();
                  var0.getMatrices().translate(var6.floatValue, var6.floatValue2 + var7);
                  ScriptDrawApi.method2597(var0);
                  ScriptImGui.method635(var6.floatValue, var6.floatValue2 + var7);

                  try {
                     var0.enableScissor(0.0F, 0.0F, var6.floatValue3, var6.floatValue4);
                     var6.storage
                        .method1321(
                           var6.script,
                           var6.function,
                           (double)var6.floatValue3,
                           (double)var6.floatValue4,
                           (double)(var3 - var6.floatValue),
                           (double)(var4 - var6.floatValue2 - var7)
                        );
                  } finally {
                     ScriptImGui.method635(0.0F, 0.0F);
                     var0.disableScissor();
                     ScriptDrawApi.end();
                     var0.getMatrices().popMatrix();
                  }
               }
            }
         }
      }
   }

   private static void method5318(MouseButtonEvent var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1.currentScreen instanceof ChatScreen && !items4.isEmpty()) {
         float var10 = method5315();
         float var11 = method5316();
         boolean var4 = var0.getIntValue2() == 1;

         for (ScriptGuiApi.Entry3 var6 : items4.values()) {
            if (var4) {
               if (var6.visible && var6.script.isRunning()) {
                  float var7 = var6.flag ? 12.0F : 0.0F;
                  boolean var8 = var10 >= var6.floatValue
                     && var10 <= var6.floatValue + var6.floatValue3
                     && var11 >= var6.floatValue2
                     && var11 <= var6.floatValue2 + (var6.flag ? var7 : var6.floatValue4);
                  boolean var9 = var10 >= var6.floatValue
                     && var10 <= var6.floatValue + var6.floatValue3
                     && var11 >= var6.floatValue2 + var7
                     && var11 <= var6.floatValue2 + var7 + var6.floatValue4;
                  if (var0.getIntValue() == 0 && var8 && (var6.flag || var6.function2 == null)) {
                     var6.dragging = true;
                     var6.floatValue5 = var10 - var6.floatValue;
                     var6.floatValue6 = var11 - var6.floatValue2;
                     var0.setCancelled(true);
                     return;
                  }

                  if (var9 && var6.function2 != null) {
                     var6.storage
                        .method1321(
                           var6.script,
                           var6.function2,
                           (double)(var10 - var6.floatValue),
                           (double)(var11 - var6.floatValue2 - var7),
                           (double)var0.getIntValue()
                        );
                     var0.setCancelled(true);
                     return;
                  }
               }
            } else if (var6.dragging) {
               var6.dragging = false;
               method5319(var6);
            }
         }
      } else {
         for (ScriptGuiApi.Entry3 var3 : items4.values()) {
            var3.dragging = false;
         }
      }
   }

   private static void method5319(ScriptGuiApi.Entry3 var0) {
      JsonObject var1 = new JsonObject();
      var1.addProperty("x", var0.floatValue);
      var1.addProperty("y", var0.floatValue2);
      var0.script.method1415().method1008("__window." + var0.name, var1);
   }

   public static void method5320(Script var0, Storage var1, Scriptable var2, ScriptableObject var3) {
      ScriptValueUtils.Entry2 var4 = ScriptValueUtils.method6246(var2, var3);
      var4.method6274(
         "image",
         var0x -> {
            CustomDrawContext var1x = ScriptDrawApi.method2601();
            RemoteUtil.Entry var2x = RemoteUtil.method1827(ScriptValueUtils.arg(var0x, 0));
            Identifier var3x = var2x.texture();
            if (var3x == null) {
               return null;
            } else {
               float var4x = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
               float var5 = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
               float var6 = (float)ScriptValueUtils.method6256(var0x, 3, var2x.width());
               float var7 = (float)ScriptValueUtils.method6256(var0x, 4, var2x.height());
               Scriptable var8 = ScriptValueUtils.method6262(var0x, 5);
               float var9 = var8 == null ? 0.0F : (float)ScriptValueUtils.method6264(var8, "radius", 0.0);
               int var10 = var8 == null ? -1 : (int)((long)ScriptValueUtils.method6264(var8, "color", 4.294967295E9));
               double var11 = var8 == null ? 0.0 : ScriptValueUtils.method6264(var8, "rotation", 0.0);
               float var13 = var8 == null ? 0.0F : (float)ScriptValueUtils.method6264(var8, "u1", 0.0);
               float var14 = var8 == null ? 0.0F : (float)ScriptValueUtils.method6264(var8, "v1", 0.0);
               float var15 = var8 == null ? 1.0F : (float)ScriptValueUtils.method6264(var8, "u2", 1.0);
               float var16 = var8 == null ? 1.0F : (float)ScriptValueUtils.method6264(var8, "v2", 1.0);
               var1x.getMatrices().pushMatrix();

               try {
                  var1x.getMatrices().translate(var4x + var6 / 2.0F, var5 + var7 / 2.0F);
                  if (var11 != 0.0) {
                     var1x.getMatrices().rotate((float)Math.toRadians(var11));
                  }

                  var1x.drawRoundedTexture(
                     var3x, -var6 / 2.0F, -var7 / 2.0F, var6, var7, ProxyDotRadiusData.method8069(var9), new ZenithColor(var10), var13, var14, var15, var16
                  );
               } finally {
                  var1x.getMatrices().popMatrix();
               }

               return null;
            }
         }
      );
      var4.method6274(
         "head",
         var0x -> {
            CustomDrawContext var1x = ScriptDrawApi.method2601();
            MinecraftClient var2x = MinecraftClient.getInstance();
            if (var2x.getNetworkHandler() == null) {
               return false;
            } else {
               Object var3x = ScriptValueUtils.arg(var0x, 0);
               String var4x = var3x instanceof Scriptable
                  ? String.valueOf(ScriptScreenApi.method7048(var3x) == null ? "" : ScriptScreenApi.method7048(var3x).getName().getString())
                  : ScriptValueUtils.method6255(var3x);
               PlayerListEntry var5 = var2x.getNetworkHandler().getPlayerListEntry(var4x);
               if (var5 == null) {
                  return false;
               } else {
                  float var6 = (float)ScriptValueUtils.method6256(var0x, 3, 16.0);
                  var1x.drawPlayerHeadWithRoundedShader(
                     var5.getSkinTextures().body().texturePath(),
                     (float)ScriptValueUtils.method6256(var0x, 1, 0.0),
                     (float)ScriptValueUtils.method6256(var0x, 2, 0.0),
                     var6,
                     ProxyDotRadiusData.method8069((float)ScriptValueUtils.method6256(var0x, 4, var6 * 0.2F)),
                     new ZenithColor(-1)
                  );
                  return true;
               }
            }
         }
      );
      var4.method6274("gradient", var0x -> {
         CustomDrawContext var1x = ScriptDrawApi.method2601();
         int var2x = (int)ScriptValueUtils.method6256(var0x, 0, 0.0);
         int var3x = (int)ScriptValueUtils.method6256(var0x, 1, 0.0);
         int var4x = (int)ScriptValueUtils.method6256(var0x, 2, 0.0);
         int var5 = (int)ScriptValueUtils.method6256(var0x, 3, 0.0);
         int var6 = ScriptValueUtils.method6258(var0x, 4, -1);
         int var7 = ScriptValueUtils.method6258(var0x, 5, -16777216);
         if (ScriptValueUtils.method6259(var0x, 6, true)) {
            var1x.fillGradient(var2x, var3x, var2x + var4x, var3x + var5, var6, var7);
         } else {
            int var8 = Math.max(1, Math.min(64, var4x));

            for (int var9 = 0; var9 < var8; var9++) {
               int var10 = method5321(var6, var7, (double)var9 / Math.max(1, var8 - 1));
               int var11 = var2x + var4x * var9 / var8;
               int var12 = var2x + var4x * (var9 + 1) / var8;
               var1x.fill(var11, var3x, var12, var3x + var5, var10);
            }
         }

         return null;
      });
      var4.method6274(
         "mouse",
         var1x -> ScriptValueUtils.method6247(var2)
            .method6277("x", (double)method5315())
            .method6277("y", (double)method5316())
            .method6277("left", GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 0) == 1)
            .method6277("right", GLFW.glfwGetMouseButton(MinecraftClient.getInstance().getWindow().getHandle(), 1) == 1)
            .getScriptableObject()
      );
   }

   private static int method5321(int var0, int var1, double var2) {
      int var4 = (int)Math.round((var0 >>> 24) + ((var1 >>> 24) - (var0 >>> 24)) * var2);
      int var5 = (int)Math.round((var0 >> 16 & 0xFF) + ((var1 >> 16 & 0xFF) - (var0 >> 16 & 0xFF)) * var2);
      int var6 = (int)Math.round((var0 >> 8 & 0xFF) + ((var1 >> 8 & 0xFF) - (var0 >> 8 & 0xFF)) * var2);
      int var7 = (int)Math.round((var0 & 0xFF) + ((var1 & 0xFF) - (var0 & 0xFF)) * var2);
      return var4 << 24 | var5 << 16 | var6 << 8 | var7;
   }

   public static void method5322(Scriptable var0, ScriptableObject var1) {
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6246(var0, var1);
      var2.method6274(
         "text",
         var0x -> {
            if (items2.size() >= 400) {
               return null;
            } else {
               Scriptable var1x = ScriptValueUtils.method6262(var0x, 4);
               float var2x = var1x == null ? (float)ScriptValueUtils.method6256(var0x, 4, 1.0) : (float)ScriptValueUtils.method6264(var1x, "scale", 1.0);
               int var3 = var1x == null ? -1 : (int)((long)ScriptValueUtils.method6264(var1x, "color", 4.294967295E9));
               int var4 = var1x == null ? Integer.MIN_VALUE : (int)((long)ScriptValueUtils.method6264(var1x, "background", 2.1474836E9F));
               items2.add(
                  new ScriptGuiApi.Data(
                     ScriptValueUtils.method6254(var0x, 0, ""),
                     ScriptValueUtils.method6256(var0x, 1, 0.0),
                     ScriptValueUtils.method6256(var0x, 2, 0.0),
                     ScriptValueUtils.method6256(var0x, 3, 0.0),
                     var2x,
                     var3,
                     var4
                  )
               );
               return null;
            }
         }
      );
      var2.method6274("circle", var0x -> {
         ScriptDrawApi.method2602();
         double var1x = ScriptValueUtils.method6256(var0x, 0, 0.0);
         double var3 = ScriptValueUtils.method6256(var0x, 1, 0.0);
         double var5 = ScriptValueUtils.method6256(var0x, 2, 0.0);
         double var7 = ScriptValueUtils.method6256(var0x, 3, 1.0);
         int var9 = ScriptValueUtils.method6258(var0x, 4, -1);
         float var10 = (float)Math.max(0.5, Math.min(10.0, ScriptValueUtils.method6256(var0x, 5, 2.0)));
         int var11 = Math.max(6, Math.min(128, ScriptValueUtils.method6257(var0x, 6, 48)));
         Vec3d var12 = null;

         for (int var13 = 0; var13 <= var11; var13++) {
            double var14 = (Math.PI * 2) * var13 / var11;
            Vec3d var16 = new Vec3d(var1x + Math.cos(var14) * var7, var3, var5 + Math.sin(var14) * var7);
            if (var12 != null) {
               GlowUtil.method2245(var12, var16, var9, var10, false);
            }

            var12 = var16;
         }

         return null;
      });
      var2.method6274(
         "path",
         var0x -> {
            ScriptDrawApi.method2602();
            var var1x = ScriptValueUtils.method6269(ScriptValueUtils.arg(var0x, 0));
            int var2x = ScriptValueUtils.method6258(var0x, 1, -1);
            float var3 = (float)Math.max(0.5, Math.min(10.0, ScriptValueUtils.method6256(var0x, 2, 2.0)));
            Vec3d var4 = null;
            int var5 = 0;

            for (Object var7 : var1x) {
               if (++var5 > 2048) {
                  break;
               }

               var var8 = ScriptValueUtils.method6269(var7);
               Vec3d var9;
               if (var8.size() >= 3) {
                  var9 = new Vec3d(
                     ScriptValueUtils.method6256(new Object[]{var8.get(0)}, 0, 0.0),
                     ScriptValueUtils.method6256(new Object[]{var8.get(1)}, 0, 0.0),
                     ScriptValueUtils.method6256(new Object[]{var8.get(2)}, 0, 0.0)
                  );
               } else {
                  if (!(var7 instanceof Scriptable var10)) {
                     continue;
                  }

                  var9 = new Vec3d(
                     ScriptValueUtils.method6264(var10, "x", 0.0), ScriptValueUtils.method6264(var10, "y", 0.0), ScriptValueUtils.method6264(var10, "z", 0.0)
                  );
               }

               if (var4 != null) {
                  GlowUtil.method2245(var4, var9, var2x, var3, false);
               }

               var4 = var9;
            }

            return null;
         }
      );
   }

   public static Scriptable method5323(Script var0, Storage var1, Scriptable var2) {
      method5309();
      var0.method1413(() -> items.removeIf(var1x -> var1x.script == var0));
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6247(var2);
      var3.method6274(
         "spawn",
         var0x -> {
            MinecraftClient var1x = MinecraftClient.getInstance();
            if (var1x.world == null) {
               return false;
            } else {
               String var2x = ScriptValueUtils.method6254(var0x, 0, "flame");
               Identifier var3x = Identifier.tryParse(var2x.contains(":") ? var2x : "minecraft:" + var2x);
               if (var3x != null && Registries.PARTICLE_TYPE.containsId(var3x)) {
                  if (Registries.PARTICLE_TYPE.get(var3x) instanceof SimpleParticleType var4x) {
                     int var15 = Math.max(1, Math.min(200, ScriptValueUtils.method6257(var0x, 7, 1)));
                     double var6 = ScriptValueUtils.method6256(var0x, 8, 0.0);

                     for (int var8 = 0; var8 < var15; var8++) {
                        double var9 = var6 == 0.0 ? 0.0 : (Math.random() - 0.5) * var6;
                        double var11 = var6 == 0.0 ? 0.0 : (Math.random() - 0.5) * var6;
                        double var13 = var6 == 0.0 ? 0.0 : (Math.random() - 0.5) * var6;
                        var1x.world
                           .addParticleClient(
                              var4x,
                              ScriptValueUtils.method6256(var0x, 1, 0.0) + var9,
                              ScriptValueUtils.method6256(var0x, 2, 0.0) + var11,
                              ScriptValueUtils.method6256(var0x, 3, 0.0) + var13,
                              ScriptValueUtils.method6256(var0x, 4, 0.0),
                              ScriptValueUtils.method6256(var0x, 5, 0.0),
                              ScriptValueUtils.method6256(var0x, 6, 0.0)
                           );
                     }

                     return true;
                  } else {
                     throw ScriptValueUtils.method6251("частица " + var2x + " требует параметров — возьми простую");
                  }
               } else {
                  throw ScriptValueUtils.method6251("нет частицы " + var2x + " (список — particles.ids())");
               }
            }
         }
      );
      var3.method6274("ids", var1x -> {
         ArrayList var2x = new ArrayList();

         for (Identifier var4x : Registries.PARTICLE_TYPE.getIds()) {
            if (Registries.PARTICLE_TYPE.get(var4x) instanceof SimpleParticleType) {
               var2x.add(var4x.getPath());
            }
         }

         var2x.sort(null);
         return ScriptValueUtils.method6267(var2, var2x);
      });
      BiFunction<Object[], Boolean, Object> var4 = (var1x, var2x) -> {
         if (items.size() >= 2500) {
            return false;
         } else {
            ScriptGuiApi.Entry var3x = new ScriptGuiApi.Entry();
            var3x.script = var0;
            if (var2x) {
               var3x.text = ScriptValueUtils.method6254(var1x, 0, "");
            } else {
               var3x.entry = RemoteUtil.method1827(ScriptValueUtils.arg(var1x, 0));
            }

            var3x.doubleValue = ScriptValueUtils.method6256(var1x, 1, 0.0);
            var3x.doubleValue2 = ScriptValueUtils.method6256(var1x, 2, 0.0);
            var3x.doubleValue3 = ScriptValueUtils.method6256(var1x, 3, 0.0);
            Scriptable var4x = ScriptValueUtils.method6262(var1x, 4);
            var3x.doubleValue4 = var4x == null ? 0.0 : ScriptValueUtils.method6264(var4x, "vx", 0.0) / 20.0;
            var3x.doubleValue5 = var4x == null ? 0.0 : ScriptValueUtils.method6264(var4x, "vy", 0.0) / 20.0;
            var3x.doubleValue6 = var4x == null ? 0.0 : ScriptValueUtils.method6264(var4x, "vz", 0.0) / 20.0;
            var3x.doubleValue7 = var4x == null ? 0.0 : ScriptValueUtils.method6264(var4x, "gravity", 0.0) / 400.0;
            var3x.doubleValue8 = var4x == null ? 1.0 : Math.max(0.0, Math.min(1.0, ScriptValueUtils.method6264(var4x, "drag", 1.0)));
            var3x.size = var4x == null ? 0.5F : (float)Math.max(0.01, Math.min(20.0, ScriptValueUtils.method6264(var4x, "size", 0.5)));
            var3x.floatValue = var4x == null ? 0.0F : (float)(ScriptValueUtils.method6264(var4x, "spin", 0.0) / 20.0);
            var3x.intValue = var4x == null ? 40 : (int)Math.max(1.0, Math.min(1200.0, ScriptValueUtils.method6264(var4x, "life", 2.0) * 20.0));
            var3x.flag = var4x == null || ScriptValueUtils.method6265(var4x, "fade", true);
            var3x.color = var4x == null ? -1 : (int)((long)ScriptValueUtils.method6264(var4x, "color", 4.294967295E9));
            items.add(var3x);
            return true;
         }
      };
      var3.method6274("image", var1x -> var4.apply(var1x, false));
      var3.method6274("text", var1x -> var4.apply(var1x, true));
      var3.method6274("burst", var1x -> {
         RemoteUtil.Entry var2x = ScriptValueUtils.arg(var1x, 0) instanceof String ? null : RemoteUtil.method1827(ScriptValueUtils.arg(var1x, 0));
         String var3x = var2x == null ? ScriptValueUtils.method6254(var1x, 0, "") : null;
         int var4x = Math.max(1, Math.min(200, ScriptValueUtils.method6257(var1x, 4, 12)));
         double var5 = ScriptValueUtils.method6256(var1x, 5, 3.0);
         Scriptable var7 = ScriptValueUtils.method6262(var1x, 6);
         List<Entry> var8 = new ArrayList<>(var4x);

         for (int var9 = 0; var9 < var4x && items.size() + var8.size() < 2500; var9++) {
            ScriptGuiApi.Entry var10 = new ScriptGuiApi.Entry();
            var10.script = var0;
            var10.entry = var2x;
            var10.text = var3x;
            var10.doubleValue = ScriptValueUtils.method6256(var1x, 1, 0.0);
            var10.doubleValue2 = ScriptValueUtils.method6256(var1x, 2, 0.0);
            var10.doubleValue3 = ScriptValueUtils.method6256(var1x, 3, 0.0);
            double var11 = Math.random() * Math.PI * 2.0;
            double var13 = Math.acos(2.0 * Math.random() - 1.0);
            double var15 = var5 * (0.5 + Math.random() * 0.5) / 20.0;
            var10.doubleValue4 = Math.sin(var13) * Math.cos(var11) * var15;
            var10.doubleValue5 = Math.abs(Math.cos(var13)) * var15 + var15 * 0.5;
            var10.doubleValue6 = Math.sin(var13) * Math.sin(var11) * var15;
            var10.doubleValue7 = var7 == null ? 0.02 : ScriptValueUtils.method6264(var7, "gravity", 8.0) / 400.0;
            var10.doubleValue8 = var7 == null ? 0.96 : ScriptValueUtils.method6264(var7, "drag", 0.96);
            var10.size = var7 == null ? 0.35F : (float)ScriptValueUtils.method6264(var7, "size", 0.35);
            var10.floatValue = (float)((Math.random() - 0.5) * (var7 == null ? 20.0 : ScriptValueUtils.method6264(var7, "spin", 20.0)));
            var10.intValue = (int)(20.0 * (var7 == null ? 1.5 : ScriptValueUtils.method6264(var7, "life", 1.5)) * (0.7 + Math.random() * 0.6));
            var10.flag = true;
            var10.color = var7 == null ? -1 : (int)((long)ScriptValueUtils.method6264(var7, "color", 4.294967295E9));
            var8.add(var10);
         }

         items.addAll(var8);
         return null;
      });
      var3.method6274("clear", var1x -> {
         items.removeIf(var1xx -> var1xx.script == var0);
         return null;
      });
      var3.method6274("count", var1x -> (double)items.stream().filter(var1xx -> var1xx.script == var0).count());
      return var3.getScriptableObject();
   }

   public static Scriptable method5324(Script var0, Scriptable var1) {
      method5309();
      var0.method1413(() -> items3.removeIf(var1x -> var1x.script == var0));
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6247(var1);
      var2.method6274(
         "add",
         var1x -> {
            String var2x = ScriptValueUtils.method6254(var1x, 0, "метка");
            items3.removeIf(var2xx -> var2xx.script == var0 && var2xx.text.equals(var2x));
            if (items3.stream().filter(var1xx -> var1xx.script == var0).count() >= 64L) {
               throw ScriptValueUtils.method6251("больше 64 меток");
            } else {
               items3.add(
                  new ScriptGuiApi.Data2(
                     var0,
                     var2x,
                     ScriptValueUtils.method6256(var1x, 1, 0.0),
                     ScriptValueUtils.method6256(var1x, 2, 0.0),
                     ScriptValueUtils.method6256(var1x, 3, 0.0),
                     ScriptValueUtils.method6258(var1x, 4, -7686913),
                     ScriptValueUtils.method6259(var1x, 5, true)
                  )
               );
               return null;
            }
         }
      );
      var2.method6274("remove", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, "");
         items3.removeIf(var2xx -> var2xx.script == var0 && var2xx.text.equals(var2x));
         return null;
      });
      var2.method6274("clear", var1x -> {
         items3.removeIf(var1xx -> var1xx.script == var0);
         return null;
      });
      var2.method6274(
         "list",
         var2x -> {
            ArrayList var3 = new ArrayList();

            for (ScriptGuiApi.Data2 var5 : items3) {
               if (var5.script == var0) {
                  var3.add(
                     ScriptValueUtils.method6247(var1)
                        .method6277("name", var5.text)
                        .method6277("x", var5.doubleValue)
                        .method6277("y", var5.doubleValue2)
                        .method6277("z", var5.doubleValue3)
                        .getScriptableObject()
                  );
               }
            }

            return ScriptValueUtils.method6267(var1, var3);
         }
      );
      return var2.getScriptableObject();
   }

   public static Scriptable method5325(Script var0, Storage var1, Scriptable var2) {
      method5309();
      var0.method1413(() -> {
         items4.values().removeIf(var1xx -> var1xx.script == var0);
         MinecraftClient var1x = MinecraftClient.getInstance();
         if (var1x.currentScreen instanceof ScriptGuiApi.Entry2 var2x && var2x.script == var0) {
            var1x.execute(() -> var1x.setScreen(null));
         }
      });
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6247(var2);
      var3.method6274("toast", var1x -> {
         String var2x = ScriptValueUtils.method6254(var1x, 0, var0.method1403());
         String var3x = ScriptValueUtils.method6254(var1x, 1, "");
         long var4x = (long)Math.max(500.0, Math.min(15000.0, ScriptValueUtils.method6256(var1x, 2, 3000.0)));
         MutableText var6 = Text.literal(var3x.isEmpty() ? var2x : var2x + " — " + var3x);
         HudHolder.method6969().method6973("i", var6, var4x);
         return null;
      });
      BiFunction<Object[], Boolean, Object> var4 = (var3x, var4x) -> {
         String var5 = ScriptValueUtils.method6254(var3x, 0, "window");
         Scriptable var6 = ScriptValueUtils.method6262(var3x, 1);
         String var7 = var0.getId() + ":" + var5;
         ScriptGuiApi.Entry3 var8 = items4.getOrDefault(var7, new ScriptGuiApi.Entry3());
         var8.script = var0;
         var8.storage = var1;
         var8.scope = var2;
         var8.name = var5;
         var8.flag = var4x && (var6 == null || ScriptValueUtils.method6265(var6, "frame", true));
         var8.title = var6 == null ? var5 : ScriptValueUtils.method6266(var6, "title", var5);
         var8.floatValue3 = var6 == null ? 120.0F : (float)Math.max(20.0, Math.min(1000.0, ScriptValueUtils.method6264(var6, "width", 120.0)));
         var8.floatValue4 = var6 == null ? 60.0F : (float)Math.max(10.0, Math.min(800.0, ScriptValueUtils.method6264(var6, "height", 60.0)));
         JsonElement var9 = var0.method1415().method1006("__window." + var5);
         if (var9 != null && var9.isJsonObject()) {
            var8.floatValue = var9.getAsJsonObject().get("x").getAsFloat();
            var8.floatValue2 = var9.getAsJsonObject().get("y").getAsFloat();
         } else if (!items4.containsKey(var7)) {
            var8.floatValue = var6 == null ? 10.0F : (float)ScriptValueUtils.method6264(var6, "x", 10.0);
            var8.floatValue2 = var6 == null ? 40.0F : (float)ScriptValueUtils.method6264(var6, "y", 40.0);
         }

         var8.function = ScriptValueUtils.method6261(var3x, 2);
         var8.function2 = ScriptValueUtils.method6261(var3x, 3);
         items4.put(var7, var8);
         return ScriptValueUtils.method6247(var2).method6274("show", var1xx -> {
            var8.visible = true;
            return null;
         }).method6274("hide", var1xx -> {
            var8.visible = false;
            return null;
         }).method6274("visible", var1xx -> var8.visible).method6274("toggle", var1xx -> {
            var8.visible = !var8.visible;
            return var8.visible;
         }).method6274("resize", var1xx -> {
            var8.floatValue3 = (float)Math.max(20.0, ScriptValueUtils.method6256(var1xx, 0, var8.floatValue3));
            var8.floatValue4 = (float)Math.max(10.0, ScriptValueUtils.method6256(var1xx, 1, var8.floatValue4));
            return null;
         }).method6274("move", var1xx -> {
            var8.floatValue = (float)ScriptValueUtils.method6256(var1xx, 0, var8.floatValue);
            var8.floatValue2 = (float)ScriptValueUtils.method6256(var1xx, 1, var8.floatValue2);
            method5319(var8);
            return null;
         }).method6274("setTitle", var1xx -> {
            var8.title = ScriptValueUtils.method6254(var1xx, 0, var8.title);
            return null;
         }).method6274("x", var1xx -> (double)var8.floatValue).method6274("y", var1xx -> (double)var8.floatValue2).method6274("close", var1xx -> {
            items4.remove(var7);
            return null;
         }).getScriptableObject();
      };
      var3.method6274("window", var1x -> var4.apply(var1x, true));
      var3.method6274("hud", var1x -> var4.apply(var1x, false));
      var3.method6274(
         "screen",
         var2x -> {
            Long var3x = items5.get(var0);
            if (var3x != null && System.currentTimeMillis() - var3x < 3000L) {
               throw ScriptValueUtils.method6251("игрок только что закрыл экран — открыть снова можно через 3 секунды");
            } else {
               Scriptable var4x = ScriptValueUtils.method6262(var2x, 0);
               if (var4x == null) {
                  throw ScriptValueUtils.method6251("ui.screen({ title, render(w, h, mx, my), click(x, y, button), key(code), char(c), close() })");
               } else {
                  ScriptGuiApi.Entry2 var5 = new ScriptGuiApi.Entry2(
                     var0,
                     var1,
                     ScriptValueUtils.method6266(var4x, "title", var0.method1403()),
                     method5326(var4x, "render"),
                     method5326(var4x, "click"),
                     method5326(var4x, "release"),
                     method5326(var4x, "scroll"),
                     method5326(var4x, "key"),
                     method5326(var4x, "char"),
                     method5326(var4x, "close"),
                     ScriptValueUtils.method6265(var4x, "pause", false),
                     ScriptValueUtils.method6265(var4x, "blur", true)
                  );
                  MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(var5));
                  return null;
               }
            }
         }
      );
      var3.method6274("close", var1x -> {
         MinecraftClient var2x = MinecraftClient.getInstance();
         if (var2x.currentScreen instanceof ScriptGuiApi.Entry2 var3x && var3x.script == var0) {
            var2x.execute(() -> var2x.setScreen(null));
         }

         return null;
      });
      var3.method6274("isOpen", var1x -> MinecraftClient.getInstance().currentScreen instanceof ScriptGuiApi.Entry2 var2x && var2x.script == var0);
      var3.method6274(
         "button",
         var0x -> {
            CustomDrawContext var1x = ScriptDrawApi.method2601();
            float var2x = (float)ScriptValueUtils.method6256(var0x, 1, 0.0);
            float var3x = (float)ScriptValueUtils.method6256(var0x, 2, 0.0);
            float var4x = (float)ScriptValueUtils.method6256(var0x, 3, 60.0);
            float var5 = (float)ScriptValueUtils.method6256(var0x, 4, 16.0);
            float var6 = (float)ScriptValueUtils.method6256(var0x, 5, -1.0);
            float var7 = (float)ScriptValueUtils.method6256(var0x, 6, -1.0);
            boolean var8 = var6 >= var2x && var6 <= var2x + var4x && var7 >= var3x && var7 <= var3x + var5;
            int var9 = ScriptDrawApi.method2603();
            var1x.drawRoundedRect(
               var2x,
               var3x,
               var4x,
               var5,
               ProxyDotRadiusData.method8069(Math.min(var5 / 2.0F, 5.0F)),
               new ZenithColor(var8 ? var9 & 16777215 | 1711276032 : 1090519039)
            );
            Font var10 = Fonts.NEW_MEDIUM.getFont(Math.max(4.0F, Math.min(9.0F, var5 * 0.42F)));
            String var11 = ScriptValueUtils.method6254(var0x, 0, "");
            var1x.drawText(var10, var11, var2x + (var4x - var10.width(var11)) / 2.0F, var3x + (var5 - var10.height()) / 2.0F, new ZenithColor(-1));
            return var8;
         }
      );
      return var3.getScriptableObject();
   }

   private static Function method5326(Scriptable var0, String var1) {
      return ScriptValueUtils.method6263(var0, var1) instanceof Function var3 ? var3 : null;
   }

   public static final class Data {
      private final String text;
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final float floatValue;
      private final int intValue;
      private final int intValue2;

      public String text() {
         return this.text;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, doubleValue, doubleValue2, doubleValue3, floatValue, intValue, intValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "floatValue=" + this.floatValue + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data(String var1, double var2, double var4, double var6, float var8, int var9, int var10) {
         this.text = var1;
         this.doubleValue = var2;
         this.doubleValue2 = var4;
         this.doubleValue3 = var6;
         this.floatValue = var8;
         this.intValue = var9;
         this.intValue2 = var10;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public int color() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }

   public static final class Data2 {
      private final Script script;
      private final String text;
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final int intValue;
      private final boolean flag;

      public Script script() {
         return this.script;
      }

      public String text() {
         return this.text;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public int intValue() {
         return this.intValue;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.script, other.script) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(script, text, doubleValue, doubleValue2, doubleValue3, intValue, flag);
      }

      @Override
      public String toString() {
         return "Data2[" + "script=" + this.script + ", " + "text=" + this.text + ", " + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "intValue=" + this.intValue + ", " + "flag=" + this.flag + "]";
      }

      public Data2(Script var1, String var2, double var3, double var5, double var7, int var9, boolean var10) {
         this.script = var1;
         this.text = var2;
         this.doubleValue = var3;
         this.doubleValue2 = var5;
         this.doubleValue3 = var7;
         this.intValue = var9;
         this.flag = var10;
      }

      public Script getScript() {
         return this.script;
      }

      public String name() {
         return this.text;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }

      public int color() {
         return this.intValue;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   public static final class Entry {
      public Script script;
      public RemoteUtil.Entry entry;
      public String text;
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public double doubleValue7;
      public double doubleValue8;
      public float size;
      public float floatValue;
      public float floatValue2;
      public int color;
      public int intValue;
      public int intValue2;
      public boolean flag;
   }

   public static final class Entry2 extends Screen {
      public final Script script;
      private final Storage storage;
      private final Function function;
      private final Function function2;
      private final Function function3;
      private final Function function4;
      private final Function function5;
      private final Function function6;
      private final Function function7;
      private final boolean flag;
      private final boolean flag2;

      public Entry2(
         Script var1,
         Storage var2,
         String var3,
         Function var4,
         Function var5,
         Function var6,
         Function var7,
         Function var8,
         Function var9,
         Function var10,
         boolean var11,
         boolean var12
      ) {
         super(Text.literal(var3));
         this.script = var1;
         this.storage = var2;
         this.function = var4;
         this.function2 = var5;
         this.function3 = var6;
         this.function4 = var7;
         this.function5 = var8;
         this.function6 = var9;
         this.function7 = var10;
         this.flag = var11;
         this.flag2 = var12;
      }

      @Override
      public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
         if (this.flag2) {
            super.renderBackground(context, mouseX, mouseY, deltaTicks);
         }
      }

      @Override
      public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
         if (this.function != null && this.script.isRunning()) {
            CustomDrawContext var5 = CustomDrawContext.of(context);
            ScriptDrawApi.method2597(var5);
            ScriptImGui.method635(0.0F, 0.0F);

            try {
               this.storage.method1321(this.script, this.function, (double)this.width, (double)this.height, (double)mouseX, (double)mouseY);
            } finally {
               ScriptDrawApi.end();
            }
         }
      }

      @Override
      public boolean mouseClicked(Click click, boolean doubled) {
         if (click.button() == 0) {
            ScriptImGui.method634((float)click.x(), (float)click.y());
         }

         if (this.function2 != null && this.script.isRunning()) {
            this.storage.method1321(this.script, this.function2, click.x(), click.y(), (double)click.button());
         }

         return true;
      }

      @Override
      public boolean mouseReleased(Click click) {
         if (this.function3 != null && this.script.isRunning()) {
            this.storage.method1321(this.script, this.function3, click.x(), click.y(), (double)click.button());
         }

         return true;
      }

      @Override
      public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
         if (this.function4 != null && this.script.isRunning()) {
            this.storage.method1321(this.script, this.function4, verticalAmount, mouseX, mouseY);
         }

         return true;
      }

      @Override
      public boolean keyPressed(KeyInput input) {
         if (input.key() == 256) {
            ScriptGuiApi.items5.put(this.script, System.currentTimeMillis());
            this.close();
            return true;
         } else if (ScriptImGui.method643(this.script) && ScriptImGui.method645(this.script, input.key())) {
            return true;
         } else {
            if (this.function5 != null && this.script.isRunning()) {
               this.storage.method1321(this.script, this.function5, (double)input.key(), (double)input.modifiers());
            }

            return true;
         }
      }

      @Override
      public boolean charTyped(CharInput input) {
         if (ScriptImGui.method643(this.script)) {
            ScriptImGui.method644(this.script, new String(Character.toChars(input.codepoint())));
            return true;
         } else {
            if (this.function6 != null && this.script.isRunning()) {
               this.storage.method1321(this.script, this.function6, new String(Character.toChars(input.codepoint())));
            }

            return true;
         }
      }

      @Override
      public void removed() {
         if (this.function7 != null && this.script.isRunning()) {
            this.storage.method1321(this.script, this.function7);
         }
      }

      @Override
      public boolean shouldPause() {
         return this.flag;
      }
   }

   public static final class Entry3 {
      public Script script;
      public Storage storage;
      public Scriptable scope;
      public String name;
      public String title;
      public float floatValue;
      public float floatValue2;
      public float floatValue3;
      public float floatValue4;
      public boolean visible = true;
      public boolean flag = true;
      public Function function;
      public Function function2;
      public boolean dragging;
      public float floatValue5;
      public float floatValue6;
   }
}

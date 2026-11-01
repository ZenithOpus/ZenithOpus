package org.zenith.utility.game;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.Identifier;
import net.minecraft.util.AssetInfo.TextureAsset;
import net.minecraft.util.AssetInfo.TextureAssetInfo;
import org.zenith.client.scripting.Script;
import org.zenith.client.scripting.ScriptImGui;

public final class Util4 {
   private static final Map<Integer, Integer> items = new ConcurrentHashMap<>();
   private static final Map<Integer, Script> items2 = new ConcurrentHashMap<>();
   private static final Map<Integer, Script> items3 = new ConcurrentHashMap<>();
   private static final Map<Script, Float> items4 = new ConcurrentHashMap<>();
   private static volatile Identifier identifier;
   private static volatile boolean flag;
   private static volatile Identifier identifier2;
   private static volatile Script script;
   private static volatile Script script2;

   private Util4() {
   }

   public static boolean method1278(int var0) {
      return !items.isEmpty() && items.containsKey(var0);
   }

   public static int method1279(int var0, int var1) {
      Integer var2 = items.get(var0);
      return var2 == null ? var1 : var2 & 16777215;
   }

   public static boolean method1280(int var0) {
      return !items3.isEmpty() && items3.containsKey(var0);
   }

   public static float method1281() {
      if (items4.isEmpty()) {
         return 1.0F;
      } else {
         float var0 = 1.0F;

         for (float var2 : items4.values()) {
            var0 = Math.max(var0, var2);
         }

         return var0;
      }
   }

   public static void method1282(Script var0, int var1, Integer var2) {
      if (var2 == null) {
         items.remove(var1);
         items2.remove(var1);
      } else {
         items.put(var1, var2);
         items2.put(var1, var0);
      }
   }

   public static void method1283(Script var0, int var1, boolean var2) {
      if (var2) {
         items3.put(var1, var0);
      } else {
         items3.remove(var1);
      }
   }

   public static void method1284(Script var0, float var1) {
      if (var1 <= 1.001F) {
         items4.remove(var0);
      } else {
         items4.put(var0, Math.min(50.0F, var1));
      }
   }

   public static boolean method1285() {
      return identifier != null || identifier2 != null;
   }

   public static SkinTextures method1286(SkinTextures var0) {
      Identifier var1 = identifier;
      Identifier var2 = identifier2;
      if (var1 != null && (script == null || !script.isRunning())) {
         identifier = null;
         var1 = null;
      }

      if (var2 != null && (script2 == null || !script2.isRunning())) {
         identifier2 = null;
         var2 = null;
      }

      if (var1 == null && var2 == null) {
         return var0;
      } else {
         Object var3 = var1 == null ? var0.body() : new TextureAssetInfo(var1, var1);
         Object var4 = var2 == null ? var0.cape() : new TextureAssetInfo(var2, var2);
         Object var5 = var2 == null ? var0.elytra() : var4;
         PlayerSkinType var6 = var1 == null ? var0.model() : (flag ? PlayerSkinType.SLIM : PlayerSkinType.WIDE);
         return new SkinTextures((TextureAsset)var3, (TextureAsset)var4, (TextureAsset)var5, var6, var0.secure());
      }
   }

   public static boolean method1287() {
      return ScriptImGui.method648();
   }

   public static void method1288(Script var0, Identifier var1, boolean var2) {
      script = var1 == null ? null : var0;
      flag = var2;
      identifier = var1;
   }

   public static void method1289(Script var0, Identifier var1) {
      script2 = var1 == null ? null : var0;
      identifier2 = var1;
   }

   public static void method1290(Script var0) {
      if (script == var0) {
         identifier = null;
         script = null;
      }

      if (script2 == var0) {
         identifier2 = null;
         script2 = null;
      }
   }

   public static void method1291(Script var0) {
      items2.entrySet().removeIf(var1 -> {
         if (var1.getValue() == var0) {
            items.remove(var1.getKey());
            return true;
         } else {
            return false;
         }
      });
      items3.values().removeIf(var1 -> var1 == var0);
      items4.remove(var0);
   }
}

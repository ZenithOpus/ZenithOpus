package org.zenith.client.scripting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.util.math.Vec3d;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.zenith.utility.misc.Class12;
import org.zenith.utility.misc.ModuleHookDefinitions;

public final class ScriptHooks {
   private ScriptHooks() {
   }

   public static List<String> method5049() {
      ArrayList<String> var0 = new ArrayList<>();

      for (Entry var2 : ModuleHookDefinitions.method6117().entrySet()) {
         var0.add("`" + (String)var2.getKey() + "` — " + ((Class12)var2.getValue()).getText2());
      }

      return var0;
   }

   public static Scriptable method5050(Storage var0, Script var1, Scriptable var2) {
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6247(var2);
      var3.method6274("on", var3x -> {
         String var4 = ScriptValueUtils.method6254(var3x, 0, "");
         Class12 var5 = ModuleHookDefinitions.method6117().get(var4);
         if (var5 == null) {
            throw ScriptValueUtils.method6251("неизвестный хук: " + var4 + " (список — hooks.names())");
         } else {
            var var6 = ScriptValueUtils.method6260(var3x, 1);
            String var7 = ScriptValueUtils.method6254(var3x, 2, "late").toLowerCase(Locale.ROOT);

            byte var8 = switch (var7) {
               case "early", "before" -> -100;
               case "late", "after" -> 100;
               default -> throw ScriptValueUtils.method6251("порядок хука: early или late");
            };
            Runnable var9 = method5051(var5, var8, var0, var1, var2, var6);
            var1.method1413(var9::run);
            return null;
         }
      });
      var3.method6274("names", var1x -> ScriptValueUtils.method6267(var2, new ArrayList<>(ModuleHookDefinitions.method6117().keySet())));
      return var3.getScriptableObject();
   }

   private static Runnable method5051(Class12<?> var0, int var1, Storage var2, Script var3, Scriptable var4, Function var5) {
      Class var6 = var0.getClassValue();
      if (var6 == ModuleHookDefinitions.Entry6.class) {
         return ((Class12<ModuleHookDefinitions.Entry6>)(Class12)var0).method1646(
            var1,
            var4x -> method5052(
               var2,
               var3,
               var5,
               () -> ScriptValueUtils.method6247(var4).method6277("value", var4x.value).method6277("vanilla", var4x.doubleValue).method6274("set", var1xxx -> {
                  var4x.value = ScriptValueUtils.method6256(var1xxx, 0, var4x.value);
                  return null;
               }).getScriptableObject(),
               null
            )
         );
      } else if (var6 == ModuleHookDefinitions.Entry.class) {
         return ((Class12<ModuleHookDefinitions.Entry>)(Class12)var0).method1646(var1, var4x -> method5052(var2, var3, var5, () -> ScriptValueUtils.method6247(var4).method6274("cancel", var1xxx -> {
            var4x.cancelled = !ScriptValueUtils.method6252(var1xxx, 0) || ScriptValueUtils.method6259(var1xxx, 0, true);
            return null;
         }).method6274("cancelled", var1xxx -> var4x.cancelled).getScriptableObject(), () -> var4x.cancelled = true));
      } else if (var6 == ModuleHookDefinitions.Entry3.class) {
         return ((Class12<ModuleHookDefinitions.Entry3>)(Class12)var0).method1646(
            var1,
            var4x -> method5052(var2, var3, var5, () -> ScriptValueUtils.method6247(var4).method6277("kind", var4x.text).method6274("cancel", var1xxx -> {
               var4x.cancelled = !ScriptValueUtils.method6252(var1xxx, 0) || ScriptValueUtils.method6259(var1xxx, 0, true);
               return null;
            }).method6274("cancelled", var1xxx -> var4x.cancelled).getScriptableObject(), () -> var4x.cancelled = true)
         );
      } else if (var6 == ModuleHookDefinitions.Entry5.class) {
         return ((Class12<ModuleHookDefinitions.Entry5>)(Class12)var0).method1646(
            var1,
            var4x -> method5052(var2, var3, var5, () -> ScriptValueUtils.method6247(var4).method6277("text", var4x.text).method6274("setText", var1xxx -> {
               var4x.text = ScriptValueUtils.method6254(var1xxx, 0, "");
               return null;
            }).method6274("replace", var1xxx -> {
               String var2xx = ScriptValueUtils.method6254(var1xxx, 0, "");
               if (!var2xx.isEmpty()) {
                  var4x.text = var4x.text.replace(var2xx, ScriptValueUtils.method6254(var1xxx, 1, ""));
               }

               return null;
            }).getScriptableObject(), null)
         );
      } else if (var6 == ModuleHookDefinitions.Entry4.class) {
         return ((Class12<ModuleHookDefinitions.Entry4>)(Class12)var0).method1646(
            var1,
            var4x -> method5052(
               var2,
               var3,
               var5,
               () -> ScriptValueUtils.method6247(var4)
                  .method6277("velocity", ScriptValueUtils.method6270(var4, var4x.vec3d.x, var4x.vec3d.y, var4x.vec3d.z))
                  .method6277("player", ScriptValueUtils.method6270(var4, var4x.vec3d3.x, var4x.vec3d3.y, var4x.vec3d3.z))
                  .method6277("look", ScriptValueUtils.method6270(var4, var4x.vec3d4.x, var4x.vec3d4.y, var4x.vec3d4.z))
                  .method6277("life", (double)var4x.intValue)
                  .method6274(
                     "set",
                     var1xxx -> {
                        var4x.vec3d = new Vec3d(
                           ScriptValueUtils.method6256(var1xxx, 0, var4x.vec3d.x),
                           ScriptValueUtils.method6256(var1xxx, 1, var4x.vec3d.y),
                           ScriptValueUtils.method6256(var1xxx, 2, var4x.vec3d.z)
                        );
                        return null;
                     }
                  )
                  .getScriptableObject(),
               null
            )
         );
      } else if (var6 == ModuleHookDefinitions.Entry2.class) {
         return ((Class12<ModuleHookDefinitions.Entry2>)(Class12)var0).method1646(
            var1,
            var4x -> method5052(
               var2,
               var3,
               var5,
               () -> ScriptValueUtils.method6247(var4)
                  .method6277("id", var4x.text)
                  .method6277("category", var4x.text2)
                  .method6277("x", var4x.doubleValue)
                  .method6277("y", var4x.doubleValue2)
                  .method6277("z", var4x.doubleValue3)
                  .method6277("volume", (double)var4x.floatValue)
                  .method6277("pitch", (double)var4x.floatValue2)
                  .method6274("cancel", var1xxx -> {
                     var4x.cancelled = !ScriptValueUtils.method6252(var1xxx, 0) || ScriptValueUtils.method6259(var1xxx, 0, true);
                     return null;
                  })
                  .method6274("cancelled", var1xxx -> var4x.cancelled)
                  .getScriptableObject(),
               () -> var4x.cancelled = true
            )
         );
      } else {
         throw ScriptValueUtils.method6251("хук " + var0.name() + " не поддерживается");
      }
   }

   private static void method5052(Storage var0, Script var1, Function var2, Supplier<Scriptable> var3, Runnable var4) {
      if (var1.isRunning() && ScriptEntityApi.method6575().isOnThread()) {
         ScriptContextFactory.method7409();

         Scriptable var5;
         try {
            var5 = (Scriptable)var3.get();
         } finally {
            Context.exit();
         }

         Object var6 = var0.method1321(var1, var2, var5);
         if (var4 != null && var6 != null && !ScriptValueUtils.method6253(var6) && Context.toBoolean(var6)) {
            var4.run();
         }
      }
   }
}

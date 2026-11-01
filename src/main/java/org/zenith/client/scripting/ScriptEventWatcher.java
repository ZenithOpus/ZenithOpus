package org.zenith.client.scripting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public final class ScriptEventWatcher {
   private final Storage storage;
   private World world;
   private float floatValue = -1.0F;
   private boolean flag;
   private String text = "";
   private Screen screen;

   public ScriptEventWatcher(Storage var1) {
      this.storage = var1;
   }

   public void tick() {
      ScriptScreenApi.method7058();
      MinecraftClient var1 = MinecraftClient.getInstance();
      ClientWorld var2 = var1.world;
      if (var2 != this.world) {
         boolean var3 = this.world == null && var2 != null;
         boolean var4 = this.world != null && var2 == null;
         this.world = var2;
         if (var3) {
            String var5 = var1.getCurrentServerEntry() == null ? "" : var1.getCurrentServerEntry().address;
            this.text = var5;
            this.storage.method1332(Script.Type.SERVER_JOIN, var1x -> new Object[]{method6415(var1x, "address", var5)});
         }

         if (var4) {
            String var11 = this.text;
            this.storage.method1332(Script.Type.SERVER_LEAVE, var1x -> new Object[]{method6415(var1x, "address", var11)});
         }

         if (var2 != null) {
            this.storage
               .method1332(Script.Type.WORLD_CHANGE, var1x -> new Object[]{method6415(var1x, "dimension", var2.getRegistryKey().getValue().toString())});
         }
      }

      if (var1.player != null) {
         float var6 = var1.player.getHealth();
         if (this.floatValue >= 0.0F && var6 != this.floatValue) {
            float var8 = this.floatValue;
            this.storage.method1332(Script.Type.HEALTH_CHANGE, var2x -> new Object[]{method6416(var2x, var8, var6)});
         }

         this.floatValue = var6;
         boolean var9 = var1.player.isDead() || var6 <= 0.0F;
         if (var9 && !this.flag) {
            this.storage.method1332(Script.Type.DEATH, var0 -> new Object[]{method6415(var0, "health", 0.0)});
         }

         this.flag = var9;
      } else {
         this.floatValue = -1.0F;
         this.flag = false;
      }

      Screen var7 = var1.currentScreen;
      if (var7 != this.screen) {
         String var10 = ScriptScreenApi.method7047(this.screen);
         String var12 = ScriptScreenApi.method7047(var7);
         this.screen = var7;
         if (!var10.equals(var12)) {
            this.storage.method1332(Script.Type.SCREEN, var2x -> {
               Object var3x = method6415(var2x, "screen", var12);
               ((Scriptable)var3x).put("previous", (Scriptable)var3x, var10);
               return new Object[]{var3x};
            });
         }
      }
   }

   private static Object method6415(Script var0, String var1, Object var2) {
      ScriptContextFactory.method7409();

      ScriptableObject var3;
      try {
         var3 = ScriptValueUtils.method6247(var0.scope).method6277(var1, var2).getScriptableObject();
      } finally {
         Context.exit();
      }

      return var3;
   }

   private static Object method6416(Script var0, double var1, double var3) {
      ScriptContextFactory.method7409();

      ScriptableObject var5;
      try {
         var5 = ScriptValueUtils.method6247(var0.scope).method6277("from", var1).method6277("to", var3).getScriptableObject();
      } finally {
         Context.exit();
      }

      return var5;
   }
}

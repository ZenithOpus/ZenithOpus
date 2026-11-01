package org.zenith.utility.render;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.SimpleFramebuffer;

public class HandShaders {
   private static MinecraftClient mc = MinecraftClient.getInstance();
   private static ShaderProgram shaderProgram;
   private static final String text = "sirius_aqua";
   private static final Map<String, ShaderProgram> items = new HashMap<>();
   private static final Set<String> items2 = new HashSet<>();
   private static SimpleFramebuffer simpleFramebuffer;
   private static boolean initialized = false;
   private static long longValue;

   public static void method6278() {
      if (!initialized) {
         long var0 = System.currentTimeMillis();
         if (var0 < longValue) {
            return;
         }

         try {
            shaderProgram = method6280("sirius_aqua");
            initialized = true;
         } catch (Exception var3) {
            longValue = var0 + 5000L;
            System.err.println("Failed to initialize hand shaders!");
            var3.printStackTrace();
         }
      }
   }

   public static ShaderProgram method6279(String var0) {
      if (var0 == null || var0.isBlank()) {
         return shaderProgram;
      } else if (items2.contains(var0)) {
         return shaderProgram;
      } else {
         try {
            return method6280(var0);
         } catch (Exception var2) {
            items2.add(var0);
            System.err.println("Failed to initialize hand shader: " + var0);
            var2.printStackTrace();
            return shaderProgram;
         }
      }
   }

   private static ShaderProgram method6280(String var0) {
      ShaderProgram var1 = items.get(var0);
      if (var1 == null) {
         var1 = new ShaderProgram("hand", var0, "smoke");
         items.put(var0, var1);
      }

      if ("sirius_aqua".equals(var0)) {
         shaderProgram = var1;
      }

      return var1;
   }

   public static void method6281() {
      if (mc != null && mc.getWindow() != null) {
         int var0 = mc.getWindow().getFramebufferWidth();
         int var1 = mc.getWindow().getFramebufferHeight();
         if (simpleFramebuffer == null || simpleFramebuffer.textureWidth != var0 || simpleFramebuffer.textureHeight != var1) {
            if (simpleFramebuffer != null) {
               simpleFramebuffer.delete();
            }

            simpleFramebuffer = BlitUtils.method6448("zenith hand copy", var0, var1, true);
         }
      }
   }

   public static void method6282() {
      FramebufferUtils.method5654(ShaderProgram.getCurrent(), ImmediateRenderer.method8014());
   }

   @Generated
   public static ShaderProgram getShaderProgram() {
      return shaderProgram;
   }

   @Generated
   public static SimpleFramebuffer getSimpleFramebuffer() {
      return simpleFramebuffer;
   }

   @Generated
   public static boolean isInitialized() {
      return initialized;
   }
}

package org.zenith.init;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.zenith.Zenith;
import org.zenith.release.ReleaseStartup;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.LightUtil;
import org.zenith.utility.render.PostEffectManager;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.WardrobeFeatureRenderer;

public class ZenithInitializer implements ClientModInitializer {
   public void onInitializeClient() {
      ensureBaritoneSettingsExist();
      new Zenith();
      Zenith.getInstance().init();
      try {
         MinecraftClient.getInstance().getWindow().setTitle("hackkkk @yoezproject | @moluchclient");
      } catch (Throwable var4) {
      }
      LivingEntityFeatureRendererRegistrationCallback.EVENT.register((LivingEntityFeatureRendererRegistrationCallback)(var0, var1, var2, var3) -> {
         if (var1 instanceof PlayerEntityRenderer var4) {
            var2.register(new WardrobeFeatureRenderer(var4));
         }
      });
      ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
         public Identifier getFabricId() {
            return Zenith.method1501("after_shader_load");
         }

         public void reload(ResourceManager var1) {
            LightUtil.invalidate();
            PostEffectManager.method2823();
            KawaseShaderKey.method5167();
         }
      });
      RenderShaders.method5255();
      drainEarlyGlfwError();
      ReleaseStartup.openLinks();
   }

   private static void drainEarlyGlfwError() {
      try {
         MemoryStack var0 = MemoryStack.stackPush();

         try {
            PointerBuffer var1 = var0.mallocPointer(1);
            int var2 = GLFW.glfwGetError(var1);
            if (var2 != 0) {
               System.err.println("[Zenith] GLFW вызван до инициализации: [0x" + Integer.toHexString(var2) + "] " + MemoryUtil.memUTF8Safe(var1.get(0)));
            }
         } catch (Throwable var4) {
            if (var0 != null) {
               try {
                  var0.close();
               } catch (Throwable var3) {
                  var4.addSuppressed(var3);
               }
            }

            throw var4;
         }

         if (var0 != null) {
            var0.close();
         }
      } catch (Throwable var5) {
      }
   }

   private static void ensureBaritoneSettingsExist() {
      try {
         Path var0 = MinecraftClient.getInstance().runDirectory.toPath().resolve("baritone").resolve("settings.txt");
         if (Files.notExists(var0)) {
            Files.createDirectories(var0.getParent());
            Files.createFile(var0);
         }

         ensureSettingPresent(var0);
      } catch (IOException var1) {
      }
   }

   private static void ensureSettingPresent(Path var0) throws IOException {
      List<String> var1 = Files.readAllLines(var0);

      for (Object var3_o : (Iterable<?>)(Object)(var1)) {
         String var3 = (String)var3_o;
         if (var3.trim().equalsIgnoreCase("chunkCaching false")) {
            return;
         }
      }

      String var4 = var1.isEmpty() ? "" : System.lineSeparator();
      Files.writeString(var0, var4 + "chunkCaching false", StandardOpenOption.APPEND);
   }
}

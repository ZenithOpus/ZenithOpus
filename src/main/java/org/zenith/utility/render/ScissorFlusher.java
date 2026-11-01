package org.zenith.utility.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ScissorState;
import net.minecraft.client.render.fog.FogRenderer.FogType;
import org.zenith.RenderState;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.mixin.accessors.GameRendererAccessor;

public final class ScissorFlusher {
   private static int intValue;

   private ScissorFlusher() {
   }

   public static boolean method6646() {
      return intValue > 0;
   }

   public static void flush() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      if (var0 != null && var0.gameRenderer != null) {
         MsdfRenderer.flushBatch();
         GameRendererAccessor var1 = (GameRendererAccessor)(Object)var0.gameRenderer;
         if (((ScissorFlusher.Handler)var1.zenith$guiState()).zenith$pending()) {
            ScissorState var2 = RenderSystem.getScissorStateForRenderTypeDraws();
            boolean var3 = var2.isEnabled();
            int var4 = var2.getX();
            int var5 = var2.getY();
            int var6 = var2.getWidth();
            int var7 = var2.getHeight();
            RenderState.method1901();
            RenderState.method1881();
            intValue++;

            try {
               var1.zenith$guiRenderer().render(var1.zenith$fogRenderer().getFogBuffer(FogType.NONE));
            } finally {
               intValue--;
               if (var3) {
                  RenderState.method1900(var4, var5, var6, var7);
               }
            }
         }
      }
   }

   public interface Handler {
      boolean zenith$pending();
   }
}

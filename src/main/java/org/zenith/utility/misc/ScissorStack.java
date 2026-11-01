package org.zenith.utility.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.Nullable;
import org.zenith.base.font.MsdfRenderer;

public final class ScissorStack {
   private ScissorStack() {
   }

   public static void method1750(@Nullable ScreenRect var0) {
      MsdfRenderer.flushBatch();
      if (var0 == null) {
         RenderSystem.disableScissorForRenderTypeDraws();
      } else {
         Window var1 = MinecraftClient.getInstance().getWindow();
         int var2 = var1.getScaleFactor();
         RenderSystem.enableScissorForRenderTypeDraws(
            var0.getLeft() * var2, var1.getFramebufferHeight() - var0.getBottom() * var2, Math.max(0, var0.width() * var2), Math.max(0, var0.height() * var2)
         );
      }
   }

   public static void clear() {
      MsdfRenderer.flushBatch();
      RenderSystem.disableScissorForRenderTypeDraws();
   }
}

package org.zenith.utility.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.RenderSetup.Builder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public final class ChamsRenderLayer {
   private ChamsRenderLayer() {
   }

   @Nullable
   public static RenderLayer method3202(String var0, @Nullable RenderPipeline var1, @Nullable Identifier var2, boolean var3, boolean var4, boolean var5) {
      if (var1 == null) {
         return null;
      } else {
         Builder var6 = RenderSetup.builder(var1);
         if (var2 != null) {
            var6.texture("Sampler0", var2);
         }

         if (var3) {
            var6.useLightmap();
         }

         if (var4) {
            var6.useOverlay();
         }

         if (var5) {
            var6.translucent();
         }

         return RenderLayer.of(var0, var6.build());
      }
   }

   public static RenderPipeline method3203(RenderLayer var0) {
      return var0.renderSetup.pipeline;
   }
}

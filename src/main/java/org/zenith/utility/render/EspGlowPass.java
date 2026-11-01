package org.zenith.utility.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.PostEffectProcessor.FramebufferSet;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.util.Identifier;
import org.zenith.client.modules.impl.render.ShaderESPModule;

public final class EspGlowPass {
   private static final Identifier identifier = Identifier.of("zenith", "esp_glow");

   private EspGlowPass() {
   }

   public static boolean method2636(FrameGraphBuilder var0, int var1, int var2, FramebufferSet var3) {
      ShaderESPModule var4 = ShaderESPModule.INSTANCE;
      if (var4 != null && var4.isEnabled()) {
         PostEffectProcessor var5 = MinecraftClient.getInstance().getShaderLoader().loadPostEffect(identifier, DefaultFramebufferSet.MAIN_AND_ENTITY_OUTLINE);
         if (var5 == null) {
            return false;
         } else {
            PostEffectManager.method2819(
               var5,
               identifier,
               new PostEffectManager.Entry()
                  .method2828("OutlineWidth", var4.method4590())
                  .method2828("GlowStrength", var4.method4591())
                  .method2828("FillOpacity", var4.method4592())
                  .method2828("Pulse", var4.method4594())
                  .method2828("Radius", var4.method4593())
            );
            var5.render(var0, var1, var2, var3);
            return true;
         }
      } else {
         return false;
      }
   }
}

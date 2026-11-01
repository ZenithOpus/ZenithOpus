package org.zenith.utility.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.PostEffectProcessor.FramebufferSet;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.util.Identifier;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.client.modules.impl.render.PredictionsModule;

public final class FirePostPass {
   private static long lastFrameNanos = -1L;
   private static float floatValue;

   private FirePostPass() {
   }

   public static boolean method5079(FrameGraphBuilder var0, int var1, int var2, FramebufferSet var3, boolean var4) {
      Identifier var5 = PredictionsModule.INSTANCE.method2910();
      boolean var6 = PredictionsModule.INSTANCE.method2913();
      if (PredictionsModule.INSTANCE.method2909() && var5 != null && var6) {
         PostEffectProcessor var7 = MinecraftClient.getInstance().getShaderLoader().loadPostEffect(var5, DefaultFramebufferSet.MAIN_AND_ENTITY_OUTLINE);
         if (var7 == null) {
            return false;
         } else {
            method5080(var7, var5, method5082(), var4);
            var7.render(var0, var1, var2, var3);
            return true;
         }
      } else {
         lastFrameNanos = -1L;
         return false;
      }
   }

   private static void method5080(PostEffectProcessor var0, Identifier var1, float var2, boolean var3) {
      ZenithColor var4 = Zenith.getInstance().getStyleManager().getClientColor(0);
      ZenithColor var5 = Zenith.getInstance().getStyleManager().getClientColor(45);
      float[] var6 = method5081(var4);
      float[] var7 = method5081(var5);
      PostEffectManager.method2819(
         var0,
         var1,
         new PostEffectManager.Entry()
            .method2831("FireColor", var6[0], var6[1], var6[2], var6[3])
            .method2831("EmberColor", var7[0], var7[1], var7[2], var7[3])
            .method2828("Time", var2)
            .method2828("KeepForeign", var3 ? 1.0F : 0.0F)
      );
   }

   private static float[] method5081(ZenithColor var0) {
      return new float[]{var0.getIntValue() / 255.0F, var0.getIntValue2() / 255.0F, var0.getIntValue3() / 255.0F, var0.getIntValue4() / 255.0F};
   }

   private static float method5082() {
      long var0 = System.nanoTime();
      if (lastFrameNanos < 0L) {
         lastFrameNanos = var0;
         return floatValue;
      } else {
         float var2 = Math.min((float)(var0 - lastFrameNanos) / 1.0E9F, 0.1F);
         lastFrameNanos = var0;
         floatValue = (floatValue + var2) % 100000.0F;
         return floatValue;
      }
   }
}

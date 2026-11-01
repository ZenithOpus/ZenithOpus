package org.zenith.utility.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.PostEffectProcessor.FramebufferSet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.zenith.client.modules.impl.render.WeatherModule;
import org.zenith.utility.misc.Util15;
import org.zenith.utility.misc.WeatherUniforms;

public final class SkyPass {
   private static final Identifier identifier = Identifier.of("zenith", "weather_sky");
   private static final PostEffectManager.Entry entry = new PostEffectManager.Entry();
   private static final WeatherUniforms.Data data = new WeatherUniforms.Data(entry);

   private SkyPass() {
   }

   public static boolean method7267(FrameGraphBuilder var0, int var1, int var2, FramebufferSet var3, Camera var4) {
      WeatherModule var5 = WeatherModule.INSTANCE;
      if (!var5.method3099()) {
         return false;
      } else {
         PostEffectProcessor var6 = MinecraftClient.getInstance().getShaderLoader().loadPostEffect(identifier, DefaultFramebufferSet.MAIN_ONLY);
         if (var6 == null) {
            return false;
         } else {
            float var7 = var1 / Math.max((float)var2, 1.0F);
            float var8 = Util15.method2704();
            float var9 = (float)Math.toRadians(var4.getPitch());
            float var10 = (float)(Math.PI - Math.toRadians(var4.getYaw()));
            Vec3d var11 = var4.getCameraPos();
            PostEffectManager.Entry var12 = entry;
            WeatherUniforms.method799(
               data, var5.method3085(), var5.method3098(), var5.method3104(), var5.method3096(), var11.x, var11.y, var11.z, var9, var10, var8, var7
            );
            PostEffectManager.method2819(var6, identifier, var12);
            var6.render(var0, var1, var2, var3);
            return true;
         }
      }
   }
}

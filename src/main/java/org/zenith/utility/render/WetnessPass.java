package org.zenith.utility.render;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.PostEffectProcessor.FramebufferSet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector4f;
import org.zenith.client.modules.impl.render.WeatherModule;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.FlakeUtil;
import org.zenith.utility.misc.FrameTimer;
import org.zenith.utility.misc.Util15;
import org.zenith.utility.misc.WeatherDepthTexture;
import org.zenith.utility.misc.WeatherFlakeChannel;
import org.zenith.utility.misc.WeatherUniforms;

public final class WetnessPass {
   private static final Identifier identifier = Identifier.of("zenith", "weather_gloss");
   private static final float CONST_floatValue = 0.05F;
   private static final PostEffectManager.Entry entry = new PostEffectManager.Entry();
   private static final WeatherUniforms.Data data = new WeatherUniforms.Data(entry);
   private static boolean flag;

   private WetnessPass() {
   }

   public static boolean method3122(Camera var0) {
      return flag && var0.getSubmersionType() == CameraSubmersionType.NONE;
   }

   public static boolean method3123(FrameGraphBuilder var0, int var1, int var2, FramebufferSet var3, Camera var4) {
      flag = false;
      WeatherModule var5 = WeatherModule.INSTANCE;
      if (!var5.method3103()) {
         return false;
      } else if (var4.getSubmersionType() != CameraSubmersionType.NONE) {
         return false;
      } else {
         WeatherFlakeChannel var6 = var5.method3085();
         float var7 = var5.method3100();
         float var8 = var7 * var6.floatValue23;
         if (var8 <= 0.005F) {
            return false;
         } else {
            MinecraftClient var9 = MinecraftClient.getInstance();
            WeatherDepthTexture.method1386();
            PostEffectProcessor var10 = var9.getShaderLoader().loadPostEffect(identifier, DefaultFramebufferSet.MAIN_ONLY);
            if (var10 == null) {
               return false;
            } else {
               float var11 = var1 / Math.max((float)var2, 1.0F);
               float var12 = Util15.method2704();
               float var13 = (float)Math.toRadians(var4.getPitch());
               float var14 = (float)(Math.PI - Math.toRadians(var4.getYaw()));
               float var15 = var9.gameRenderer.getFarPlaneDistance();
               float var16 = var6.flag
                  ? Math.min(var15, Math.max(var6.floatValue14 * 1.6F, 192.0F))
                  : Math.clamp(var6.floatValue14 * 1.1F, 24.0F, Math.min(96.0F, var15));
               Vec3d var17 = var4.getCameraPos();
               PostEffectManager.Entry var18 = entry;
               float var19 = var5.method3104();
               WeatherUniforms.method799(data, var6, var5.method3098(), var19, var5.method3096(), var17.x, var17.y, var17.z, var13, var14, var12, var11);
               method3125(var18, "NearPlane", 0.05F);
               method3125(var18, "FarPlane", var15);
               method3125(var18, "WetAmount", var8);
               method3125(var18, "DarkAmount", ColorUtils.method6427(var6.intValue4));
               method3127(var18, "WetTint", var6.intValue5);
               method3125(var18, "ReflectAmount", var6.floatValue24);
               method3125(var18, "PuddleAmount", var6.floatValue26);
               method3125(var18, "GlintAmount", var6.floatValue25);
               method3125(var18, "Frost", var6.flag ? 1.0F : 0.0F);
               method3125(var18, "TerrainValid", WeatherDepthBuffer.method5133() ? 1.0F : 0.0F);
               method3125(var18, "MistAmount", var6.floatValue27 * var5.method3098());
               method3127(var18, "MistTint", var6.intValue8);
               method3125(var18, "MistBase", (float)(var17.y - 1.6));
               method3125(var18, "MistHeight", 3.2F);
               method3124(var18, "MistWind", var6.floatValue20, var6.floatValue21);
               float var20 = 0.0F;
               if (var6.flag) {
                  var20 = SnowShader.isFlag() ? FlakeUtil.method7506() : 1000000.0F;
               }

               method3125(var18, "CoverRadius", var20);
               method3125(var18, "FadeEnd", var16);
               method3125(var18, "RainAmount", var6.intValue2 == 1 ? var6.floatValue18 : 0.0F);
               float[] var21 = FrameTimer.method756();
               Vector4f var22 = Util26.getVector4f();
               method3125(var18, "ZenithSaturation", FrameTimer.method755());
               method3125(var18, "FogEnvironmentalStart", Util26.getFloatValue());
               method3125(var18, "FogEnvironmentalEnd", Util26.getFloatValue2());
               method3125(var18, "FogRenderDistanceStart", Util26.getFloatValue3());
               method3125(var18, "FogRenderDistanceEnd", Util26.getFloatValue4());
               var18.method2831("ZenithGrade", var21[0], var21[1], var21[2], var21[3]);
               var18.method2831("FogColor", var22.x, var22.y, var22.z, var22.w);
               PostEffectManager.method2819(var10, identifier, var18);
               var10.render(var0, var1, var2, var3);
               flag = true;
               return true;
            }
         }
      }
   }

   private static void method3124(PostEffectManager.Entry var0, String var1, float var2, float var3) {
      var0.method2829(var1, var2, var3);
   }

   private static void method3125(PostEffectManager.Entry var0, String var1, float var2) {
      var0.method2828(var1, var2);
   }

   private static void method3126(PostEffectManager.Entry var0, String var1, float var2, float var3, float var4) {
      var0.method2830(var1, var2, var3, var4);
   }

   private static void method3127(PostEffectManager.Entry var0, String var1, int var2) {
      method3126(var0, var1, ColorUtils.method6424(var2), ColorUtils.method6425(var2), ColorUtils.method6426(var2));
   }
}

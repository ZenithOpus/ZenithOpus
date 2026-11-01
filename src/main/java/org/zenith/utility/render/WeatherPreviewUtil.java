package org.zenith.utility.render;

import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;
import org.zenith.MatrixUtils;
import org.zenith.RenderState;
import org.zenith.utility.misc.WeatherFlakeChannel;
import org.zenith.utility.misc.WeatherUniforms;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class WeatherPreviewUtil {
   private static final String text = "/assets/gl/shaders/weather/weather_preview.vsh";
   private static final String text2 = "/assets/gl/shaders/weather/weather_sky.fsh";
   private static final float CONST_floatValue = 82.0F;
   private static final float CONST_floatValue2 = 12.0F;
   private static ShaderProgram shaderProgram;
   private static boolean flag;
   private static boolean flag2;

   private WeatherPreviewUtil() {
   }

   public static void method255(
      MenuDrawContext var0, float var1, float var2, float var3, float var4, WeatherFlakeChannel var5, float var6, float var7, float var8, float var9
   ) {
      if (!(var9 <= 0.004F) && !(var3 <= 1.0F) && !(var4 <= 1.0F) && method256()) {
         RoundedRectBatch.method2935();
         var0.draw();

         try {
            shaderProgram.method5517();
            shaderProgram.method5518("InSampler", 0);
            shaderProgram.method5521("InSize", var3, var4);
            shaderProgram.method5519("Opacity", var9);
            float var10 = var3 / var4;
            float var11 = (float)Math.tan(Math.toRadians(82.0) * 0.5);
            WeatherUniforms.method799(
               new WeatherPreviewUtil.Data(shaderProgram), var5, var6, var7, var8, 0.0, 0.0, 0.0, (float)Math.toRadians(12.0), (float) Math.PI, var11, var10
            );
            RenderState.method1879();
            RenderState.method1882(false);
            RenderState.method1871();
            RenderState.method1875(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
            method257(var0, var1, var2, var3, var4);
         } catch (Exception var15) {
            if (!flag2) {
               System.err.println("Weather sky preview render failed");
               var15.printStackTrace();
               flag2 = true;
            }
         } finally {
            shaderProgram.unbind();
            method258();
         }
      }
   }

   private static boolean method256() {
      if (shaderProgram != null) {
         return true;
      } else if (flag) {
         return false;
      } else {
         try {
            shaderProgram = ShaderProgram.method5512("/assets/gl/shaders/weather/weather_preview.vsh", "/assets/gl/shaders/weather/weather_sky.fsh");
         } catch (Exception var1) {
            System.err.println("Failed to initialize weather sky preview shader");
            var1.printStackTrace();
            flag = true;
         }

         return shaderProgram != null;
      }
   }

   private static void method257(MenuDrawContext var0, float var1, float var2, float var3, float var4) {
      MinecraftClient var5 = MinecraftClient.getInstance();
      float var6 = Math.max(1, var5.getWindow().getScaledWidth());
      float var7 = Math.max(1, var5.getWindow().getScaledHeight());
      Matrix4f var8 = MatrixUtils.method664(var0.getMatrices());
      Vector3f var9 = var8.transformPosition(new Vector3f(var1, var2, 0.0F));
      Vector3f var10 = var8.transformPosition(new Vector3f(var1 + var3, var2 + var4, 0.0F));
      float var11 = var9.x / var6 * 2.0F - 1.0F;
      float var12 = var10.x / var6 * 2.0F - 1.0F;
      float var13 = 1.0F - var9.y / var7 * 2.0F;
      float var14 = 1.0F - var10.y / var7 * 2.0F;
      shaderProgram.method5523("QuadRect", var11, var14, var12 - var11, var13 - var14);
      FramebufferUtils.method5655(ShaderProgram.getCurrent(), ImmediateRenderer.method8014(), var11, var14, var12, var13);
   }

   private static void method258() {
      RenderState.method1882(true);
      RenderState.method1878();
      RenderState.method1871();
      RenderState.method1873();
      RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
      GL20.glUseProgram(0);
   }

   private static final class Data implements WeatherUniforms.Handler {
      private final ShaderProgram shaderProgram;

      public ShaderProgram shaderProgram() {
         return this.shaderProgram;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.shaderProgram, other.shaderProgram);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(shaderProgram);
      }

      @Override
      public String toString() {
         return "Data[" + "shaderProgram=" + this.shaderProgram + "]";
      }

      private Data(ShaderProgram var1) {
         this.shaderProgram = var1;
      }

      @Override
      public void method259(String var1, float var2) {
         this.shaderProgram.method5519(var1, var2);
      }

      @Override
      public void method260(String var1, float var2, float var3, float var4) {
         this.shaderProgram.method5524(var1, var2, var3, var4);
      }

      public ShaderProgram getShaderProgram() {
         return this.shaderProgram;
      }
   }
}

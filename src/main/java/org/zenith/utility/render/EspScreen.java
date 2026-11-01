package org.zenith.utility.render;

import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ScissorState;
import net.minecraft.client.gl.SimpleFramebuffer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.zenith.MatrixUtils;
import org.zenith.RenderState;
import org.zenith.client.modules.impl.render.ShaderESPModule;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class EspScreen {
   private static final String text = "/assets/gl/shaders/esp/esp_screen.vsh";
   private static final String text2 = "/assets/gl/shaders/esp/esp_screen_blur.vsh";
   private static final String text3 = "/assets/gl/shaders/esp/esp_preview_tint.fsh";
   private static final String text4 = "/assets/gl/shaders/esp/esp_blur.fsh";
   private static final String text5 = "/assets/gl/shaders/esp/esp_glow.fsh";
   private static SimpleFramebuffer simpleFramebuffer;
   private static SimpleFramebuffer simpleFramebuffer2;
   private static SimpleFramebuffer simpleFramebuffer3;
   private static ShaderProgram shaderProgram;
   private static ShaderProgram shaderProgram2;
   private static ShaderProgram shaderProgram3;
   private static boolean initialized;
   private static boolean flag;
   private static boolean flag2;
   private static final long CONST_longValue = 2000L;
   private static String text6;
   private static long longValue;
   private static boolean flag3;

   private EspScreen() {
   }

   public static void method6551(MenuDrawContext var0, Runnable var1) {
      flag3 = false;
      if (method6560()) {
         RoundedRectBatch.method2935();
         var0.draw();
         EspScreen.Data var2 = EspScreen.Data.method6568();
         if (var2.intValue3 > 0 && var2.intValue4 > 0) {
            EspScreen.Entry var3 = EspScreen.Entry.method6569();

            try {
               method6561(var2.intValue3, var2.intValue4);
               BlitUtils.method6456(simpleFramebuffer, 0);
               ImmediateRenderer.method8013(simpleFramebuffer);
               RenderState.method1903(true, true, true, true);
               RenderState.method1882(true);
               RenderState.method1878();
               MinecraftClient var4 = MinecraftClient.getInstance();
               Framebuffer var5 = var4.framebuffer;
               var4.framebuffer = simpleFramebuffer;

               try {
                  var1.run();
               } finally {
                  var4.framebuffer = var5;
                  ImmediateRenderer.method8013(null);
               }

               flag3 = true;
            } catch (Exception var15) {
               method6567(var15);
            } finally {
               method6564(var2);
               var3.restore();
            }
         } else {
            method6566("no render target (" + var2.intValue3 + "x" + var2.intValue4 + ")");
         }
      }
   }

   public static void method6552(MenuDrawContext var0, float var1, float var2, float var3, float var4, float var5) {
      if (!flag3) {
         method6566("mask was not captured this frame");
      } else if (method6560()) {
         EspScreen.Data var6 = EspScreen.Data.method6568();
         EspScreen.Entry var7 = EspScreen.Entry.method6569();

         try {
            method6555(var5, method6553(var0, var1, var2, var3, var4));
            var7.restore();
            method6556(var0, var6, var1, var2, var3, var4, var5);
         } catch (Exception var12) {
            method6567(var12);
         } finally {
            flag3 = false;
            method6564(var6);
            var7.restore();
         }
      }
   }

   public static void release() {
      flag3 = false;
      if (simpleFramebuffer != null || simpleFramebuffer2 != null || simpleFramebuffer3 != null) {
         method6565(simpleFramebuffer);
         method6565(simpleFramebuffer2);
         method6565(simpleFramebuffer3);
         simpleFramebuffer = null;
         simpleFramebuffer2 = null;
         simpleFramebuffer3 = null;
      }
   }

   private static int[] method6553(MenuDrawContext var0, float var1, float var2, float var3, float var4) {
      MinecraftClient var5 = MinecraftClient.getInstance();
      float var6 = Math.max(1, var5.getWindow().getScaledWidth());
      float var7 = Math.max(1, var5.getWindow().getScaledHeight());
      Matrix4f var8 = MatrixUtils.method663(var0);
      Vector3f var9 = var8.transformPosition(new Vector3f(var1, var2, 0.0F));
      Vector3f var10 = var8.transformPosition(new Vector3f(var1 + var3, var2 + var4, 0.0F));
      int var11 = simpleFramebuffer.textureWidth;
      int var12 = simpleFramebuffer.textureHeight;
      float var13 = Math.min(var9.x, var10.x) / var6 * var11;
      float var14 = Math.max(var9.x, var10.x) / var6 * var11;
      float var15 = var12 - Math.max(var9.y, var10.y) / var7 * var12;
      float var16 = var12 - Math.min(var9.y, var10.y) / var7 * var12;
      return new int[]{(int)Math.floor(var13), (int)Math.floor(var15), (int)Math.ceil(var14), (int)Math.ceil(var16)};
   }

   private static void method6554(int[] var0, int var1, int var2) {
      int var3 = simpleFramebuffer.textureWidth;
      int var4 = simpleFramebuffer.textureHeight;
      int var5 = Math.max(0, var0[0] - var1);
      int var6 = Math.max(0, var0[1] - var2);
      int var7 = Math.min(var3, var0[2] + var1);
      int var8 = Math.min(var4, var0[3] + var2);
      RenderState.method1900(var5, var6, Math.max(0, var7 - var5), Math.max(0, var8 - var6));
   }

   private static void method6555(float var0, int[] var1) {
      ShaderESPModule var2 = ShaderESPModule.INSTANCE;
      float var3 = var2.method4593();
      int var4 = (int)Math.ceil(Math.max(0.0F, var3)) + 3;
      int var5 = (int)Math.ceil(Math.max(0.0F, var2.method4590())) + 3;
      int var6 = Math.max(var4 + 3, var5);

      try {
         ImmediateRenderer.method8013(simpleFramebuffer3);
         method6559();
         method6554(var1, var6, var6);
         shaderProgram.method5517();
         shaderProgram.method5518("InSampler", 0);
         shaderProgram.method5521("InSize", simpleFramebuffer.textureWidth, simpleFramebuffer.textureHeight);
         method6562(shaderProgram, "Tint", var2.method4588(), var0);
         RenderState.method1889(0, simpleFramebuffer.getColorAttachmentView());
         method6558();
         ImmediateRenderer.method8013(simpleFramebuffer2);
         method6559();
         method6554(var1, 3, var4);
         shaderProgram2.method5517();
         shaderProgram2.method5518("InSampler", 0);
         shaderProgram2.method5521("InSize", simpleFramebuffer3.textureWidth, simpleFramebuffer3.textureHeight);
         shaderProgram2.method5521("BlurDir", 1.0F, 0.0F);
         shaderProgram2.method5519("Radius", var3);
         RenderState.method1889(0, simpleFramebuffer3.getColorAttachmentView());
         method6558();
         ImmediateRenderer.method8013(simpleFramebuffer);
         method6559();
         method6554(var1, 3, 3);
         shaderProgram2.method5521("InSize", simpleFramebuffer2.textureWidth, simpleFramebuffer2.textureHeight);
         shaderProgram2.method5521("BlurDir", 0.0F, 1.0F);
         RenderState.method1889(0, simpleFramebuffer2.getColorAttachmentView());
         method6558();
         shaderProgram2.unbind();
      } finally {
         RenderState.method1901();
      }

      ImmediateRenderer.method8013(null);
   }

   private static void method6556(MenuDrawContext var0, EspScreen.Data var1, float var2, float var3, float var4, float var5, float var6) {
      method6563(var1);
      RenderState.method1879();
      RenderState.method1882(false);
      RenderState.method1871();
      RenderState.method1875(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
      ShaderESPModule var7 = ShaderESPModule.INSTANCE;
      shaderProgram3.method5517();
      shaderProgram3.method5518("InSampler", 0);
      shaderProgram3.method5518("GlowSampler", 1);
      shaderProgram3.method5521("InSize", simpleFramebuffer3.textureWidth, simpleFramebuffer3.textureHeight);
      shaderProgram3.method5519("OutlineWidth", var7.method4590());
      shaderProgram3.method5519("GlowStrength", var7.method4591());
      shaderProgram3.method5519("FillOpacity", var7.method4592());
      shaderProgram3.method5519("Pulse", var7.method4594());
      RenderState.method1889(0, simpleFramebuffer3.getColorAttachmentView());
      RenderState.method1889(1, simpleFramebuffer.getColorAttachmentView());
      method6557(var0, var2, var3, var4, var5);
      shaderProgram3.unbind();
      RenderState.method1897();
   }

   private static void method6557(MenuDrawContext var0, float var1, float var2, float var3, float var4) {
      MinecraftClient var5 = MinecraftClient.getInstance();
      float var6 = Math.max(1, var5.getWindow().getScaledWidth());
      float var7 = Math.max(1, var5.getWindow().getScaledHeight());
      Matrix4f var8 = MatrixUtils.method663(var0);
      Vector3f var9 = var8.transformPosition(new Vector3f(var1, var2, 0.0F));
      Vector3f var10 = var8.transformPosition(new Vector3f(var1 + var3, var2 + var4, 0.0F));
      float var11 = var9.x / var6 * 2.0F - 1.0F;
      float var12 = var10.x / var6 * 2.0F - 1.0F;
      float var13 = 1.0F - var9.y / var7 * 2.0F;
      float var14 = 1.0F - var10.y / var7 * 2.0F;
      FramebufferUtils.method5655(ShaderProgram.getCurrent(), ImmediateRenderer.method8014(), var11, var14, var12, var13);
   }

   private static void method6558() {
      FramebufferUtils.method5654(ShaderProgram.getCurrent(), ImmediateRenderer.method8014());
   }

   private static void method6559() {
      RenderState.method1879();
      RenderState.method1882(false);
      RenderState.method1872();
      RenderState.method1903(true, true, true, true);
   }

   private static boolean method6560() {
      if (initialized) {
         return true;
      } else if (flag) {
         return false;
      } else {
         try {
            shaderProgram = ShaderProgram.method5512("/assets/gl/shaders/esp/esp_screen.vsh", "/assets/gl/shaders/esp/esp_preview_tint.fsh");
            shaderProgram2 = ShaderProgram.method5512("/assets/gl/shaders/esp/esp_screen_blur.vsh", "/assets/gl/shaders/esp/esp_blur.fsh");
            shaderProgram3 = ShaderProgram.method5512("/assets/gl/shaders/esp/esp_screen.vsh", "/assets/gl/shaders/esp/esp_glow.fsh");
            initialized = true;
         } catch (Exception var1) {
            System.err.println("Failed to initialize ShaderESP preview shaders");
            var1.printStackTrace();
            flag = true;
         }

         return initialized;
      }
   }

   private static void method6561(int var0, int var1) {
      if (simpleFramebuffer == null
         || simpleFramebuffer2 == null
         || simpleFramebuffer3 == null
         || simpleFramebuffer.textureWidth != var0
         || simpleFramebuffer.textureHeight != var1) {
         release();
         simpleFramebuffer = BlitUtils.method6448("zenith esp preview", var0, var1, true);
         simpleFramebuffer2 = BlitUtils.method6448("zenith esp preview", var0, var1, false);
         simpleFramebuffer3 = BlitUtils.method6448("zenith esp preview", var0, var1, false);
      }
   }

   private static void method6562(ShaderProgram var0, String var1, int var2, float var3) {
      var0.method5523(var1, ColorUtils.method6424(var2), ColorUtils.method6425(var2), ColorUtils.method6426(var2), ColorUtils.method6427(var2) * var3);
   }

   private static void method6563(EspScreen.Data var0) {
      ImmediateRenderer.method8013(null);
   }

   private static void method6564(EspScreen.Data var0) {
      ImmediateRenderer.method8013(null);
      RenderState.method1903(true, true, true, true);
      RenderState.method1882(true);
      RenderState.method1878();
      RenderState.method1871();
      RenderState.method1873();
      RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
      RenderState.method1897();
   }

   private static void method6565(SimpleFramebuffer var0) {
      if (var0 != null) {
         var0.delete();
      }
   }

   private static void method6566(String var0) {
      long var1 = System.currentTimeMillis();
      if (!var0.equals(text6) || var1 - longValue >= 2000L) {
         text6 = var0;
         longValue = var1;
         System.err.println("ShaderESP preview skipped: " + var0);
      }
   }

   private static void method6567(Exception var0) {
      if (!flag2) {
         System.err.println("ShaderESP preview render failed");
         var0.printStackTrace();
         flag2 = true;
      }
   }

   private static final class Data {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public int intValue4() {
         return this.intValue4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.intValue4, other.intValue4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3, intValue4);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "intValue4=" + this.intValue4 + "]";
      }

      private Data(int var1, int var2, int var3, int var4) {
         this.intValue = var1;
         this.intValue2 = var2;
         this.intValue3 = var3;
         this.intValue4 = var4;
      }

      private static EspScreen.Data method6568() {
         Framebuffer var0 = BlitUtils.method6452();
         return new EspScreen.Data(0, 0, Math.max(1, var0.textureWidth), Math.max(1, var0.textureHeight));
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public int width() {
         return this.intValue3;
      }

      public int height() {
         return this.intValue4;
      }
   }

   private static final class Entry {
      private final boolean flag;
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;
      private boolean flag2;

      private Entry(boolean var1, int var2, int var3, int var4, int var5) {
         this.flag = var1;
         this.intValue = var2;
         this.intValue2 = var3;
         this.intValue3 = var4;
         this.intValue4 = var5;
      }

      private static EspScreen.Entry method6569() {
         ScissorState var0 = RenderSystem.getScissorStateForRenderTypeDraws();
         EspScreen.Entry var1 = new EspScreen.Entry(var0.isEnabled(), var0.getX(), var0.getY(), var0.getWidth(), var0.getHeight());
         RenderState.method1901();
         return var1;
      }

      private void restore() {
         if (!this.flag2) {
            this.flag2 = true;
            if (this.flag) {
               RenderState.method1900(this.intValue, this.intValue2, this.intValue3, this.intValue4);
            }
         }
      }
   }
}

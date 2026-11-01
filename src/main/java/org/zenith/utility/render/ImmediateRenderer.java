package org.zenith.utility.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderSystem.ShapeIndexBuffer;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat.IndexType;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ScissorState;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.ProjectionMatrix2;
import net.minecraft.client.render.BuiltBuffer.DrawParameters;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.zenith.RenderState;
import org.zenith.utility.misc.ScissorStack;

public final class ImmediateRenderer {
   private static final Matrix4f matrix4f = new Matrix4f();
   private static final Vector3f vector3f = new Vector3f();
   private static final Vector4f vector4f = new Vector4f();
   @Nullable
   private static ShaderDefinition shaderDefinition;
   @Nullable
   private static Framebuffer framebuffer;
   private static int intValue;
   @Nullable
   private static ProjectionMatrix2 projectionMatrix2;
   private static ImmediateRenderer.Type type = ImmediateRenderer.Type.DEFAULT;
   private static float floatValue;
   private static float floatValue2;
   @Nullable
   private static ProjectionUniform projectionUniform;
   @Nullable
   private static ProjectionMatrix2 projectionMatrix22;
   public static final Matrix4f matrix4f2 = new Matrix4f().setTranslation(0.0F, 0.0F, -11000.0F);

   private ImmediateRenderer() {
   }

   public static void method8003() {
      intValue++;
      RenderState.method1881();
   }

   public static void method8004() {
      if (intValue > 0) {
         intValue--;
      }
   }

   public static boolean method8005() {
      return intValue > 0;
   }

   public static void method8006(Runnable var0) {
      if (intValue <= 0) {
         var0.run();
      } else {
         RenderSystem.backupProjectionMatrix();
         RenderSystem.setProjectionMatrix(method8019(), ProjectionType.ORTHOGRAPHIC);
         RenderSystem.getModelViewStack().pushMatrix().set(matrix4f2);

         try {
            var0.run();
         } finally {
            RenderSystem.getModelViewStack().popMatrix();
            RenderSystem.restoreProjectionMatrix();
         }
      }
   }

   public static int method8007() {
      int var0 = intValue;
      intValue = 0;
      return var0;
   }

   public static void method8008(int var0) {
      intValue = var0;
   }

   public static void method8009() {
      intValue = 0;
      type = ImmediateRenderer.Type.DEFAULT;
      ScissorStack.clear();
   }

   public static void method8010(float var0, float var1) {
      type = ImmediateRenderer.Type.PIXEL;
      floatValue = Math.max(1.0F, var0);
      floatValue2 = Math.max(1.0F, var1);
   }

   public static void method8011() {
      type = ImmediateRenderer.Type.NDC;
   }

   public static void method8012(@Nullable ShaderDefinition var0) {
      shaderDefinition = var0;
   }

   public static void method8013(@Nullable Framebuffer var0) {
      framebuffer = var0;
   }

   @Nullable
   public static Framebuffer method8014() {
      Framebuffer var0 = framebuffer;
      framebuffer = null;
      return var0;
   }

   @Nullable
   public static ShaderDefinition getShaderDefinition() {
      return shaderDefinition;
   }

   public static void method8015(BuiltBuffer var0) {
      if (var0 != null) {
         if (!RenderSystem.isOnRenderThread()) {
            var0.close();
         } else {
            ShaderDefinition var1 = shaderDefinition;
            shaderDefinition = null;
            ImmediateRenderer.Type var2 = type;
            type = ImmediateRenderer.Type.DEFAULT;
            Framebuffer var3 = framebuffer;
            framebuffer = null;
            String var4 = RenderState.getText();
            RenderState.method1899();
            BuiltBuffer var5 = var0;

            label295: {
               label296: {
                  try {
                     DrawParameters var6 = var0.getDrawParameters();
                     if (var6.indexCount() == 0) {
                        break label295;
                     }

                     boolean var7 = var2 != ImmediateRenderer.Type.DEFAULT || intValue > 0;
                     Framebuffer var8 = var3 != null ? var3 : MinecraftClient.getInstance().getFramebuffer();
                     RenderState.Data2 var9 = RenderState.method1904();
                     if (var7) {
                        boolean var10 = var2 == ImmediateRenderer.Type.DEFAULT && RenderState.isFlag4() && var8.useDepthAttachment;
                        var9 = var10 ? var9.method1910() : var9.method1909();
                     }

                     RenderPipeline var34 = var1 != null
                        ? var1.method5561(var6.format(), var6.mode(), var9)
                        : LightUtil.method6706(var4 != null ? var4 : LightUtil.method6719(var6.format(), var6.mode()), var6.format(), var6.mode(), var9);
                     if (var34 == null) {
                        break label296;
                     }

                     GpuBuffer var11 = var34.getVertexFormat().uploadImmediateVertexBuffer(var0.getBuffer());
                     GpuBuffer var12;
                     IndexType var13;
                     if (var0.getSortedBuffer() == null) {
                        ShapeIndexBuffer var14 = RenderSystem.getSequentialBuffer(var6.mode());
                        var12 = var14.getIndexBuffer(var6.indexCount());
                        var13 = var14.getIndexType();
                     } else {
                        var12 = var34.getVertexFormat().uploadImmediateIndexBuffer(var0.getSortedBuffer());
                        var13 = var6.indexType();
                     }
                     Matrix4f var35 = switch (var2) {
                        case DEFAULT -> intValue > 0 ? matrix4f2 : RenderSystem.getModelViewMatrix();
                        case PIXEL -> matrix4f2;
                        case NDC -> matrix4f;
                     };
                     GpuBufferSlice var15 = RenderSystem.getDynamicUniforms()
                        .write(
                           var35,
                           vector4f.set(RenderState.getFloatValue(), RenderState.getFloatValue2(), RenderState.getFloatValue3(), RenderState.getAlpha()),
                           vector3f,
                           matrix4f
                        );
                     GpuTextureView var16 = RenderSystem.outputColorTextureOverride != null
                        ? RenderSystem.outputColorTextureOverride
                        : var8.getColorAttachmentView();
                     GpuTextureView var17 = var8.useDepthAttachment
                        ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : var8.getDepthAttachmentView())
                        : null;
                     if (var7) {
                        RenderSystem.backupProjectionMatrix();
                        RenderSystem.setProjectionMatrix(method8016(var2), ProjectionType.ORTHOGRAPHIC);
                     }

                     GpuBufferSlice var18 = var1 != null ? var1.method5562() : null;
                     CommandEncoder var19 = RenderSystem.getDevice().createCommandEncoder();

                     try (RenderPass var20 = var19.createRenderPass(() -> "Zenith immediate", var16, OptionalInt.empty(), var17, OptionalDouble.empty())) {
                        var20.setPipeline(var34);
                        ScissorState var21 = RenderSystem.getScissorStateForRenderTypeDraws();
                        if (var21.isEnabled()) {
                           var20.enableScissor(var21.getX(), var21.getY(), var21.getWidth(), var21.getHeight());
                        }

                        RenderSystem.bindDefaultUniforms(var20);
                        var20.setUniform("DynamicTransforms", var15);
                        if (var1 != null) {
                           var1.method5564(var20, var18);
                        }

                        method8020(var20, var34);
                        var20.setVertexBuffer(0, var11);
                        var20.setIndexBuffer(var12, var13);
                        var20.drawIndexed(0, 0, var6.indexCount(), 1);
                     } finally {
                        if (var7) {
                           RenderSystem.restoreProjectionMatrix();
                        }
                     }
                  } catch (Throwable var33) {
                     if (var0 != null) {
                        try {
                           var5.close();
                        } catch (Throwable var29) {
                           var33.addSuppressed(var29);
                        }
                     }

                     throw var33;
                  }

                  if (var0 != null) {
                     var0.close();
                  }

                  return;
               }

               if (var0 != null) {
                  var0.close();
               }

               return;
            }

            if (var0 != null) {
               var0.close();
            }
         }
      }
   }

   private static GpuBufferSlice method8016(ImmediateRenderer.Type var0) {
      return switch (var0) {
         case DEFAULT -> method8019();
         case PIXEL -> method8018(floatValue, floatValue2);
         case NDC -> method8017();
      };
   }

   private static GpuBufferSlice method8017() {
      if (projectionUniform == null) {
         projectionUniform = new ProjectionUniform("zenith ndc");
      }

      return projectionUniform.method551(matrix4f);
   }

   private static GpuBufferSlice method8018(float var0, float var1) {
      if (projectionMatrix22 == null) {
         projectionMatrix22 = new ProjectionMatrix2("zenith pixel", 1000.0F, 21000.0F, true);
      }

      return projectionMatrix22.set(var0, var1);
   }

   public static GpuBufferSlice method8019() {
      if (projectionMatrix2 == null) {
         projectionMatrix2 = new ProjectionMatrix2("zenith gui", 1000.0F, 21000.0F, true);
      }

      Window var0 = MinecraftClient.getInstance().getWindow();
      return projectionMatrix2.set((float)var0.getFramebufferWidth() / var0.getScaleFactor(), (float)var0.getFramebufferHeight() / var0.getScaleFactor());
   }

   private static void method8020(RenderPass var0, RenderPipeline var1) {
      for (int var2 = 0; var2 < 8; var2++) {
         GpuTextureView var3 = RenderState.method1894(var2);
         if (var3 != null) {
            String var4 = LightUtil.texts3[var2];
            if (var1.getSamplers().contains(var4)) {
               var0.bindTexture(var4, var3, RenderState.method1896(var2));
            }
         }
      }
   }

   private static enum Type {
      DEFAULT,
      PIXEL,
      NDC;
   }
}

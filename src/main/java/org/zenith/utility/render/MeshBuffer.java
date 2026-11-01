package org.zenith.utility.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderSystem.ShapeIndexBuffer;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.BuiltBuffer.DrawParameters;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.zenith.RenderState;

public final class MeshBuffer implements AutoCloseable {
   private static final Vector3f vector3f = new Vector3f();
   private static final Matrix4f matrix4f = new Matrix4f();
   private static final Vector4f vector4f = new Vector4f();
   private final String text;
   @Nullable
   private GpuBuffer gpuBuffer;
   private int intValue;
   private DrawMode drawMode = DrawMode.QUADS;

   public MeshBuffer(String var1) {
      this.text = var1;
   }

   public boolean isEmpty() {
      return this.gpuBuffer == null || this.intValue == 0;
   }

   public void method6583(@Nullable BuiltBuffer var1) {
      if (var1 != null) {
         BuiltBuffer var2 = var1;

         try {
            DrawParameters var3 = var1.getDrawParameters();
            ByteBuffer var4 = var1.getBuffer();
            if (this.gpuBuffer != null && !this.gpuBuffer.isClosed() && this.gpuBuffer.size() >= var4.remaining()) {
               RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.gpuBuffer.slice(0L, var4.remaining()), var4);
            } else {
               this.close();
               this.gpuBuffer = RenderSystem.getDevice().createBuffer(() -> this.text, 40, var4);
            }

            this.intValue = var3.indexCount();
            this.drawMode = var3.mode();
         } catch (Throwable var6) {
            if (var1 != null) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (var1 != null) {
            var1.close();
         }
      }
   }

   public void method6584(RenderPipeline var1, Matrix4f var2) {
      this.method6587(var1, var2, null, null);
   }

   public void method6585(ShaderDefinition var1, VertexFormat var2, Matrix4f var3, @Nullable Framebuffer var4) {
      this.method6587(var1.method5561(var2, this.drawMode, RenderState.method1904()), var3, var4, var1.method5562());
   }

   public void method6586(RenderPipeline var1, Matrix4f var2, @Nullable Framebuffer var3) {
      this.method6587(var1, var2, var3, null);
   }

   public void method6587(RenderPipeline var1, Matrix4f var2, @Nullable Framebuffer var3, @Nullable GpuBufferSlice var4) {
      method6589(List.of(this), var1, var2, var3, var4);
   }

   public static void method6588(List<MeshBuffer> var0, List<Matrix4f> var1, RenderPipeline var2, @Nullable Framebuffer var3) {
      int var4 = var0.size();
      int var5 = 0;
      DrawMode var6 = null;

      for (int var7 = 0; var7 < var4; var7++) {
         MeshBuffer var8 = (MeshBuffer)var0.get(var7);
         if (!var8.isEmpty()) {
            if (var6 != null && var8.drawMode != var6) {
               for (int var9 = 0; var9 < var4; var9++) {
                  ((MeshBuffer)var0.get(var9)).method6586(var2, (Matrix4f)var1.get(var9), var3);
               }

               return;
            }

            var6 = var8.drawMode;
            var5 = Math.max(var5, var8.intValue);
         }
      }

      if (var6 != null) {
         ShapeIndexBuffer var19 = RenderSystem.getSequentialBuffer(var6);
         GpuBuffer var20 = var19.getIndexBuffer(var5);
         vector4f.set(RenderState.getFloatValue(), RenderState.getFloatValue2(), RenderState.getFloatValue3(), RenderState.getAlpha());
         GpuBufferSlice[] var21 = new GpuBufferSlice[var4];

         for (int var10 = 0; var10 < var4; var10++) {
            if (!((MeshBuffer)var0.get(var10)).isEmpty()) {
               var21[var10] = RenderSystem.getDynamicUniforms().write((Matrix4fc)var1.get(var10), vector4f, vector3f, matrix4f);
            }
         }

         Framebuffer var22 = var3 != null ? var3 : BlitUtils.method6452();
         GpuTextureView var11 = var22.getColorAttachmentView();
         GpuTextureView var12 = var22.useDepthAttachment ? var22.getDepthAttachmentView() : null;

         try (RenderPass var13 = RenderSystem.getDevice()
               .createCommandEncoder()
               .createRenderPass(() -> "Zenith mesh", var11, OptionalInt.empty(), var12, OptionalDouble.empty())) {
            var13.setPipeline(var2);
            RenderSystem.bindDefaultUniforms(var13);

            for (int var14 = 0; var14 < 8; var14++) {
               GpuTextureView var15 = RenderState.method1894(var14);
               if (var15 != null) {
                  String var16 = LightUtil.texts3[var14];
                  if (var2.getSamplers().contains(var16)) {
                     var13.bindTexture(var16, var15, RenderState.method1896(var14));
                  }
               }
            }

            var13.setIndexBuffer(var20, var19.getIndexType());

            for (int var23 = 0; var23 < var4; var23++) {
               MeshBuffer var24 = (MeshBuffer)var0.get(var23);
               if (!var24.isEmpty()) {
                  var13.setUniform("DynamicTransforms", var21[var23]);
                  var13.setVertexBuffer(0, var24.gpuBuffer);
                  var13.drawIndexed(0, 0, var24.intValue, 1);
               }
            }
         }
      }
   }

   public static void method6589(List<MeshBuffer> var0, RenderPipeline var1, Matrix4f var2, @Nullable Framebuffer var3, @Nullable GpuBufferSlice var4) {
      int var5 = 0;
      DrawMode var6 = null;

      for (MeshBuffer var8 : var0) {
         if (!var8.isEmpty()) {
            var5 = Math.max(var5, var8.intValue);
            var6 = var8.drawMode;
         }
      }

      if (var6 != null) {
         ShapeIndexBuffer var19 = RenderSystem.getSequentialBuffer(var6);
         GpuBuffer var20 = var19.getIndexBuffer(var5);
         GpuBufferSlice var9 = RenderSystem.getDynamicUniforms()
            .write(
               var2,
               vector4f.set(RenderState.getFloatValue(), RenderState.getFloatValue2(), RenderState.getFloatValue3(), RenderState.getAlpha()),
               vector3f,
               matrix4f
            );
         Framebuffer var10 = var3 != null ? var3 : BlitUtils.method6452();
         GpuTextureView var11 = var10.getColorAttachmentView();
         GpuTextureView var12 = var10.useDepthAttachment ? var10.getDepthAttachmentView() : null;

         try (RenderPass var13 = RenderSystem.getDevice()
               .createCommandEncoder()
               .createRenderPass(() -> "Zenith mesh", var11, OptionalInt.empty(), var12, OptionalDouble.empty())) {
            var13.setPipeline(var1);
            RenderSystem.bindDefaultUniforms(var13);
            var13.setUniform("DynamicTransforms", var9);
            if (var4 != null) {
               var13.setUniform("ZenithData", var4);
            }

            for (int var14 = 0; var14 < 8; var14++) {
               GpuTextureView var15 = RenderState.method1894(var14);
               if (var15 != null) {
                  String var16 = LightUtil.texts3[var14];
                  if (var1.getSamplers().contains(var16)) {
                     var13.bindTexture(var16, var15, RenderState.method1896(var14));
                  }
               }
            }

            var13.setIndexBuffer(var20, var19.getIndexType());

            for (MeshBuffer var22 : var0) {
               if (!var22.isEmpty() && var22.drawMode == var6) {
                  var13.setVertexBuffer(0, var22.gpuBuffer);
                  var13.drawIndexed(0, 0, var22.intValue, 1);
               }
            }
         }
      }
   }

   @Override
   public void close() {
      if (this.gpuBuffer != null) {
         this.gpuBuffer.close();
         this.gpuBuffer = null;
      }

      this.intValue = 0;
   }
}

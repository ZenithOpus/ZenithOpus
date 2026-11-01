package org.zenith.utility.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

public final class ProjectionUniform {
   private final GpuBuffer gpuBuffer;
   private final GpuBufferSlice gpuBufferSlice;
   private final Matrix4f matrix4f = new Matrix4f().scale(0.0F);

   public ProjectionUniform(String var1) {
      this.gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Projection matrix UBO " + var1, 136, RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
      this.gpuBufferSlice = this.gpuBuffer.slice(0L, RenderSystem.PROJECTION_MATRIX_UBO_SIZE);
   }

   public GpuBufferSlice method551(Matrix4f var1) {
      if (!this.matrix4f.equals(var1)) {
         this.matrix4f.set(var1);
         MemoryStack var2 = MemoryStack.stackPush();

         try {
            ByteBuffer var3 = Std140Builder.onStack(var2, RenderSystem.PROJECTION_MATRIX_UBO_SIZE).putMat4f(var1).get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.gpuBuffer.slice(), var3);
         } catch (Throwable var6) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (var2 != null) {
            var2.close();
         }
      }

      return this.gpuBufferSlice;
   }
}

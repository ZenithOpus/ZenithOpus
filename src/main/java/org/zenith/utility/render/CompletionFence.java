package org.zenith.utility.render;

import com.mojang.blaze3d.buffers.GpuFence;
import net.minecraft.client.gl.GlGpuFence;
import org.lwjgl.opengl.GL11;

public final class CompletionFence implements GpuFence {
   private static CompletionFence.Entry entry;
   private final CompletionFence.Entry entry2;
   private boolean closed;

   private CompletionFence(CompletionFence.Entry var1) {
      this.entry2 = var1;
   }

   public static GpuFence method5163() {
      CompletionFence.Entry var0 = entry;
      if (var0 == null) {
         var0 = new CompletionFence.Entry();
         entry = var0;
      }

      var0.intValue++;
      return new CompletionFence(var0);
   }

   public static void endFrame() {
      CompletionFence.Entry var0 = entry;
      if (var0 != null) {
         entry = null;
         if (var0.intValue > 0) {
            var0.glGpuFence = new GlGpuFence();
         }
      }
   }

   @Override
   public boolean awaitCompletion(long var1) {
      if (this.closed) {
         return true;
      } else {
         CompletionFence.Entry var3 = this.entry2;
         if (var3.glGpuFence == null) {
            if (entry == var3) {
               entry = null;
            }

            var3.glGpuFence = new GlGpuFence();
            GL11.glFlush();
         }

         return var3.glGpuFence.awaitCompletion(var1);
      }
   }

   @Override
   public void close() {
      if (!this.closed) {
         this.closed = true;
         CompletionFence.Entry var1 = this.entry2;
         if (--var1.intValue == 0 && var1.glGpuFence != null) {
            var1.glGpuFence.close();
            var1.glGpuFence = null;
         }
      }
   }

   private static final class Entry {
      private GlGpuFence glGpuFence;
      private int intValue;
   }
}

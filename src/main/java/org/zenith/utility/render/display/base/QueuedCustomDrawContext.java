package org.zenith.utility.render.display.base;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.zenith.MatrixUtils;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.utility.render.Jebriksheet;

public final class QueuedCustomDrawContext extends CustomDrawContext {
   private boolean replaying;
   private static final Matrix4f QUEUE_MATRIX = new Matrix4f();

   private QueuedCustomDrawContext(CustomDrawContext var1) {
      super(var1);
   }

   public static QueuedCustomDrawContext of(CustomDrawContext var0) {
      return var0 instanceof QueuedCustomDrawContext var1 ? var1 : new QueuedCustomDrawContext(var0);
   }

   private Matrix4f queueMatrix() {
      return MatrixUtils.method665(this.getMatrices(), QUEUE_MATRIX);
   }

   private void queueContent(Runnable var1) {
      Matrix4f var2 = MatrixUtils.method664(this.getMatrices());
      Jebriksheet.method2349(() -> this.runWithMatrix(var2, var1));
   }

   private void queueBackground(Runnable var1) {
      Matrix4f var2 = MatrixUtils.method664(this.getMatrices());
      Jebriksheet.method2353(() -> this.runWithMatrix(var2, var1));
   }

   private void runWithMatrix(Matrix4f var1, Runnable var2) {
      Matrix3x2fStack var3 = this.getMatrices();
      boolean var4 = this.replaying;
      var3.pushMatrix();
      MatrixUtils.method669(var3, var1);
      this.replaying = true;

      try {
         var2.run();
      } finally {
         this.replaying = var4;
         var3.popMatrix();
      }
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, ZenithColor var5) {
      if (this.replaying) {
         super.drawText(var1, var2, var3, var4, var5);
      } else {
         this.queueContent(() -> super.drawText(var1, var2, var3, var4, var5));
      }
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, ZenithColor var5, boolean var6, float var7, float var8, float var9) {
      if (this.replaying) {
         super.drawText(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      } else {
         this.queueContent(() -> super.drawText(var1, var2, var3, var4, var5, var6, var7, var8, var9));
      }
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, int var5, boolean var6, float var7, float var8, float var9) {
      if (this.replaying) {
         super.drawText(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      } else {
         this.queueContent(() -> super.drawText(var1, var2, var3, var4, var5, var6, var7, var8, var9));
      }
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, GlowColor var5) {
      if (this.replaying) {
         super.drawText(var1, var2, var3, var4, var5);
      } else {
         this.queueContent(() -> super.drawText(var1, var2, var3, var4, var5));
      }
   }

   @Override
   public void drawText(Font var1, Text var2, float var3, float var4, int var5) {
      if (this.replaying) {
         super.drawText(var1, var2, var3, var4, var5);
      } else {
         this.queueContent(() -> super.drawText(var1, var2, var3, var4, var5));
      }
   }

   @Override
   public void drawRoundedRect(float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, ZenithColor var6) {
      if (this.replaying) {
         super.drawRoundedRect(var1, var2, var3, var4, var5, var6);
      } else {
         Jebriksheet.method2364(this.queueMatrix(), var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public void drawRect(float var1, float var2, float var3, float var4, ZenithColor var5) {
      if (this.replaying) {
         super.drawRect(var1, var2, var3, var4, var5);
      } else {
         Jebriksheet.method2365(this.queueMatrix(), var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void drawBlurHudBooleanCheck(
      float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7, boolean var8, boolean var9
   ) {
      if (this.replaying) {
         super.drawBlurHudBooleanCheck(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      } else {
         Jebriksheet.method2363(this.queueMatrix(), var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

   @Override
   public void drawPlayerHeadWithRoundedShader(Identifier var1, float var2, float var3, float var4, ProxyDotRadiusData var5, ZenithColor var6) {
      if (this.replaying) {
         super.drawPlayerHeadWithRoundedShader(var1, var2, var3, var4, var5, var6);
      } else {
         this.queueContent(() -> super.drawPlayerHeadWithRoundedShader(var1, var2, var3, var4, var5, var6));
      }
   }

   @Override
   public void drawItem(ItemStack item, int x, int y) {
      if (this.replaying) {
         super.drawItem(item, x, y);
      } else {
         this.queueContent(() -> super.drawItem(item, x, y));
      }
   }

   @Override
   public void drawItem(ItemStack stack, int x, int y, int seed) {
      if (this.replaying) {
         super.drawItem(stack, x, y, seed);
      } else {
         this.queueContent(() -> super.drawItem(stack, x, y, seed));
      }
   }

   @Override
   public void fill(int x1, int y1, int x2, int y2, int color) {
      if (this.replaying) {
         super.fill(x1, y1, x2, y2, color);
      } else {
         this.queueRect(x1, y1, x2, y2, color);
      }
   }

   @Override
   public void fill(RenderPipeline pipeline, int x1, int y1, int x2, int y2, int color) {
      if (this.replaying) {
         super.fill(pipeline, x1, y1, x2, y2, color);
      } else {
         this.queueRect(x1, y1, x2, y2, color);
      }
   }

   private void queueRect(int var1, int var2, int var3, int var4, int var5) {
      int var6 = Math.min(var1, var3);
      int var7 = Math.min(var2, var4);
      int var8 = Math.max(var1, var3);
      int var9 = Math.max(var2, var4);
      Jebriksheet.method2365(this.queueMatrix(), var6, var7, var8 - var6, var9 - var7, ZenithColor.method7291(var5));
   }

   @Override
   public void enableScissor(int x1, int y1, int x2, int y2) {
      if (this.replaying) {
         super.enableScissor(x1, y1, x2, y2);
      } else {
         this.queueBackground(() -> super.enableScissor(x1, y1, x2, y2));
         this.queueContent(() -> super.enableScissor(x1, y1, x2, y2));
      }
   }

   @Override
   public void disableScissor() {
      if (this.replaying) {
         super.disableScissor();
      } else {
         this.queueBackground(() -> super.disableScissor());
         this.queueContent(() -> super.disableScissor());
      }
   }
}

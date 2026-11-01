package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import lombok.Generated;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import org.joml.Matrix4f;
import org.zenith.RenderState;
import org.zenith.ZenithColor;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class RoundedRectBatch2 {
   private BufferBuilder builder;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private float batchSmoothness;
   private boolean flag;

   public void method1534(ProxyDotRadiusData var1) {
      this.method1535(var1, 0.8F);
   }

   public void method1535(ProxyDotRadiusData var1, float var2) {
      MsdfRenderer.flushBatch();
      float var3 = var1.getFloatValue();
      float var4 = var1.getFloatValue4();
      float var5 = var1.getFloatValue2();
      float var6 = var1.getFloatValue3();
      if (this.flag) {
         if (var3 == this.floatValue && var4 == this.floatValue2 && var5 == this.floatValue3 && var6 == this.floatValue4 && var2 == this.batchSmoothness) {
            return;
         }

         this.flush();
      }

      this.floatValue = var3;
      this.floatValue2 = var4;
      this.floatValue3 = var5;
      this.floatValue4 = var6;
      this.batchSmoothness = var2;
      KawaseShaderKey var7 = RenderShaders.kawaseShaderKey2;
      var7.method5165();
      var7.method5166("Radius").method7415(var3, var4, var5, var6);
      var7.method5166("Smoothness").method7412(var2);
      RenderState.method1871();
      RenderState.method1873();
      this.builder = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
      this.flag = true;
   }

   public void method1536(Matrix4f var1, float var2, float var3, float var4, float var5, ZenithColor var6) {
      int var11 = var6.method7287();
      this.builder.vertex(var1, var2, var3, 0.0F).texture(var4, var5).color(var11);
      this.builder.vertex(var1, var2, var3 + var5, 0.0F).texture(var4, var5).color(var11);
      this.builder.vertex(var1, var2 + var4, var3 + var5, 0.0F).texture(var4, var5).color(var11);
      this.builder.vertex(var1, var2 + var4, var3, 0.0F).texture(var4, var5).color(var11);
   }

   public void flush() {
      if (this.flag) {
         ImmediateRenderer.method8015(this.builder.end());
         RenderState.method1872();
         this.builder = null;
         this.flag = false;
      }
   }

   @Generated
   public boolean isFlag() {
      return this.flag;
   }
}

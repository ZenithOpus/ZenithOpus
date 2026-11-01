package org.zenith.utility.misc;

import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class ShadowShaderKey extends KawaseShaderKey implements Handler2 {
   private ShaderUniform shaderUniform;
   private ShaderUniform shaderUniform2;
   private ShaderUniform shaderUniform3;
   private ShaderUniform shaderUniform4;
   private ShaderUniform shaderUniform5;
   private ShaderUniform shaderUniform6;

   public ShadowShaderKey(Identifier var1) {
      super(var1, VertexFormats.POSITION_COLOR);
   }

   public void method5497(float var1, float var2, float var3, float var4, float var5, float var6, float var7, ProxyDotRadiusData var8) {
      this.shaderUniform.method7413(var1, var2);
      this.shaderUniform2.method7413(var3, var4);
      this.shaderUniform3.method7415(var8.getFloatValue(), var8.getFloatValue4(), var8.getFloatValue2(), var8.getFloatValue3());
      this.shaderUniform4.method7412(var5);
      this.shaderUniform5.method7412(var6 * 0.5F);
      this.shaderUniform6.method7412(var7);
   }

   @Override
   public void method4659() {
      this.shaderUniform = this.method5166("Size");
      this.shaderUniform2 = this.method5166("ShapeSize");
      this.shaderUniform3 = this.method5166("Radius");
      this.shaderUniform4 = this.method5166("Padding");
      this.shaderUniform5 = this.method5166("Sigma");
      this.shaderUniform6 = this.method5166("Spread");
      super.method4659();
   }
}

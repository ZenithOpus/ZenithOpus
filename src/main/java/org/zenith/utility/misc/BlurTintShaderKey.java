package org.zenith.utility.misc;

import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class BlurTintShaderKey extends KawaseShaderKey implements Handler2 {
   private ShaderUniform shaderUniform;
   private ShaderUniform shaderUniform2;
   private ShaderUniform shaderUniform3;
   private ShaderUniform shaderUniform4;
   private ShaderUniform shaderUniform5;

   public BlurTintShaderKey(Identifier var1) {
      super(var1, VertexFormats.POSITION_TEXTURE_COLOR);
   }

   public void method4658(float var1) {
      this.shaderUniform2.method7412(var1);
      this.shaderUniform.method7413(1.0F / window.getWidth(), 1.0F / window.getHeight());
      this.shaderUniform3.method7412(1.0F);
      this.shaderUniform4.method7412(0.0F);
      this.shaderUniform5.method7414(1.0F, 1.0F, 1.0F);
   }

   @Override
   public void method4659() {
      this.shaderUniform = this.method5166("Resolution");
      this.shaderUniform2 = this.method5166("Offset");
      this.shaderUniform3 = this.method5166("Saturation");
      this.shaderUniform4 = this.method5166("TintIntensity");
      this.shaderUniform5 = this.method5166("TintColor");
      super.method4659();
   }
}

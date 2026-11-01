package org.zenith.utility.misc;

import org.zenith.utility.render.ShaderDefinition;

public final class ShaderUniform {
   private final ShaderDefinition shaderDefinition;
   private final String text;
   private float[] floatValues;

   public ShaderUniform(ShaderDefinition var1, String var2) {
      this.shaderDefinition = var1;
      this.text = var2;
   }

   public String name() {
      return this.text;
   }

   private float[] method7411(int var1) {
      float[] var2 = this.floatValues;
      if (var2 == null || var2.length != var1) {
         var2 = new float[var1];
         this.floatValues = var2;
         this.shaderDefinition.method5559(this.text, var2);
      }

      return var2;
   }

   public void method7412(float var1) {
      this.method7411(1)[0] = var1;
   }

   public void method7413(float var1, float var2) {
      float[] var3 = this.method7411(2);
      var3[0] = var1;
      var3[1] = var2;
   }

   public void method7414(float var1, float var2, float var3) {
      float[] var4 = this.method7411(3);
      var4[0] = var1;
      var4[1] = var2;
      var4[2] = var3;
   }

   public void method7415(float var1, float var2, float var3, float var4) {
      float[] var5 = this.method7411(4);
      var5[0] = var1;
      var5[1] = var2;
      var5[2] = var3;
      var5[3] = var4;
   }

   public void set(int var1) {
      this.method7412(var1);
   }

   public void method7416(int var1, int var2) {
      this.method7413(var1, var2);
   }

   public void method7417(float[] var1) {
      System.arraycopy(var1, 0, this.method7411(var1.length), 0, var1.length);
   }
}

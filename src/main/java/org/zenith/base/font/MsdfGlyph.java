package org.zenith.base.font;

import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import org.zenith.utility.render.display.base.GlowColor;

public final class MsdfGlyph {
   private final int code;
   private final float minU;
   private final float maxU;
   private final float minV;
   private final float maxV;
   private final float advance;
   private final float topPosition;
   private final float width;
   private final float height;

   public MsdfGlyph(FontData.GlyphData var1, float var2, float var3) {
      this.code = var1.unicode();
      this.advance = var1.advance();
      FontData.BoundsData var4 = var1.atlasBounds();
      if (var4 != null) {
         this.minU = var4.left() / var2;
         this.maxU = var4.right() / var2;
         this.minV = 1.0F - var4.top() / var3;
         this.maxV = 1.0F - var4.bottom() / var3;
      } else {
         this.minU = this.maxU = this.minV = this.maxV = 0.0F;
      }

      FontData.BoundsData var5 = var1.planeBounds();
      if (var5 != null) {
         this.width = var5.right() - var5.left();
         this.height = var5.top() - var5.bottom();
         this.topPosition = var5.top();
      } else {
         this.width = this.height = this.topPosition = 0.0F;
      }
   }

   private static void vertex(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      float var8 = var1.m00() * var2 + var1.m10() * var3 + var1.m20() * var4 + var1.m30();
      float var9 = var1.m01() * var2 + var1.m11() * var3 + var1.m21() * var4 + var1.m31();
      float var10 = var1.m02() * var2 + var1.m12() * var3 + var1.m22() * var4 + var1.m32();
      var0.vertex(var8, var9, var10).texture(var5, var6).color(var7);
   }

   public float apply(Matrix4f var1, VertexConsumer var2, float var3, float var4, float var5, float var6, int var7) {
      var5 -= this.topPosition * var3;
      float var8 = var4 + this.width * var3;
      float var9 = var5 + this.height * var3;
      vertex(var2, var1, var4, var5, var6, this.minU, this.minV, var7);
      vertex(var2, var1, var4, var9, var6, this.minU, this.maxV, var7);
      vertex(var2, var1, var8, var9, var6, this.maxU, this.maxV, var7);
      vertex(var2, var1, var8, var5, var6, this.maxU, this.minV, var7);
      return this.advance * var3;
   }

   public float apply(Matrix4f var1, VertexConsumer var2, float var3, float var4, float var5, float var6, GlowColor var7) {
      var5 -= this.topPosition * var3;
      float var8 = var4 + this.width * var3;
      float var9 = var5 + this.height * var3;
      vertex(var2, var1, var4, var5, var6, this.minU, this.minV, var7.getZenithColor().method7287());
      vertex(var2, var1, var4, var9, var6, this.minU, this.maxV, var7.getZenithColor2().method7287());
      vertex(var2, var1, var8, var9, var6, this.maxU, this.maxV, var7.getZenithColor4().method7287());
      vertex(var2, var1, var8, var5, var6, this.maxU, this.minV, var7.getZenithColor3().method7287());
      return this.advance * var3;
   }

   public float getWidth(float var1) {
      return this.advance * var1;
   }

   public float getInkWidth(float var1) {
      return this.width * var1;
   }

   public float getInkHeight(float var1) {
      return this.height * var1;
   }

   public float getInkTop(float var1) {
      return this.topPosition * var1;
   }

   public int getCharCode() {
      return this.code;
   }
}

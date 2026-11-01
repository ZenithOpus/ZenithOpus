package org.zenith.utility.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.HoveredData;

public final class Thumbnails implements MinecraftAccess {
   private static final int CONST_intValue = 15728880;
   private static final long CONST_longValue = System.nanoTime();
   private static final float CONST_floatValue = 12.0F;
   private static final int CONST_intValue2 = 30;
   private static final float[][] CONST_floatValues = new float[][]{
      {0.0F, 0.0F, 0.0F}, {0.0F, 0.0F, 0.0F}, {-5.0F, 2.0F, 0.0F}, {5.0F, 2.0F, 0.0F}, {-1.9F, 12.0F, 0.0F}, {1.9F, 12.0F, 0.0F}
   };
   private final List<Thumbnails.Data> items = new ArrayList<>();
   private final Quaternionf quaternionf = new Quaternionf();

   public boolean method7855(HoveredData var1, Class15 var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10) {
      if (var2 != null && var2.intValue3 != 0 && this.items.size() < 30) {
         this.items.add(new Thumbnails.Data(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10));
         return true;
      } else {
         return false;
      }
   }

   public void method7856() {
      this.items.clear();
   }

   public void method7857(DrawContext var1) {
      if (!this.items.isEmpty()) {
         float var2 = (float)(System.nanoTime() - CONST_longValue) / 1.0E9F;
         MatrixStack var3 = MatrixUtils.method666(var1.getMatrices());
         ScissorFlusher.flush();
         RenderState.method1878();
         RenderState.method1882(true);
         Immediate var4 = mc.getBufferBuilders().getEntityVertexConsumers();
         OutlineVertexProvider.method1538();

         try {
            for (Thumbnails.Data var6 : this.items) {
               var1.enableScissor(
                  (int)Math.floor(var6.floatValue4), (int)Math.floor(var6.floatValue5), (int)Math.ceil(var6.floatValue6), (int)Math.ceil(var6.floatValue7)
               );
               var3.push();

               try {
                  Class15 var7 = var6.class15;
                  float[] var8 = var7.floatValues7;
                  float var9 = (var8[0] + var8[3]) / 2.0F;
                  float var10 = (var8[1] + var8[4]) / 2.0F;
                  float var11 = (var8[2] + var8[5]) / 2.0F;
                  float var12 = 0.0F;

                  for (int var13 = 0; var13 < 4; var13++) {
                     float var14 = (var13 & 1) == 0 ? var8[0] - var9 : var8[3] - var9;
                     float var15 = (var13 & 2) == 0 ? var8[2] - var11 : var8[5] - var11;
                     var12 = Math.max(var12, (float)Math.sqrt(var14 * var14 + var15 * var15));
                  }

                  float var24 = Math.max((var8[4] - var8[1]) / 2.0F, var12);
                  float var25 = var6.floatValue3 / 2.0F / Math.max(0.02F, var24);
                  var3.translate(var6.floatValue, var6.floatValue2, 50.0F);
                  var3.scale(var25, var25, -var25);
                  var3.multiply(new Quaternionf().rotationX((float)Math.toRadians(-12.0)));
                  var3.multiply(this.quaternionf.rotationY((float)Math.toRadians(200.0 + var2 / 12.0F * 360.0 + var6.floatValue8 * 25.0)));
                  var3.translate(-var9, -var10, -var11);
                  OutlineVertexProvider.method1542(
                     var4,
                     var3x -> {
                        for (int var4x = 0; var4x <= 6; var4x++) {
                           if (var7.method2033(var4x)) {
                              var3.push();
                              if (var4x < 6) {
                                 float[] var5 = CONST_floatValues[var4x];
                                 var3.translate(var5[0] / 16.0F, var5[1] / 16.0F, var5[2] / 16.0F);
                              }

                              BoneMatrices.method4787(
                                 var7, null, var4x, var3, var3x, 15728880, OverlayTexture.DEFAULT_UV, true, var6.hoveredData.getRarity().isFlag(), -1
                              );
                              var3.pop();
                           }
                        }
                     }
                  );
               } finally {
                  var3.pop();
               }

               var4.draw();
               var1.disableScissor();
            }
         } finally {
            OutlineVertexProvider.method1539();
         }

         this.items.clear();
      }
   }

   private static final class Data {
      private final HoveredData hoveredData;
      private final Class15 class15;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;

      public HoveredData hoveredData() {
         return this.hoveredData;
      }

      public Class15 class15() {
         return this.class15;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      public float floatValue8() {
         return this.floatValue8;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.hoveredData, other.hoveredData) && java.util.Objects.equals(this.class15, other.class15) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(hoveredData, class15, floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8);
      }

      @Override
      public String toString() {
         return "Data[" + "hoveredData=" + this.hoveredData + ", " + "class15=" + this.class15 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + "]";
      }

      private Data(HoveredData var1, Class15 var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10) {
         this.hoveredData = var1;
         this.class15 = var2;
         this.floatValue = var3;
         this.floatValue2 = var4;
         this.floatValue3 = var5;
         this.floatValue4 = var6;
         this.floatValue5 = var7;
         this.floatValue6 = var8;
         this.floatValue7 = var9;
         this.floatValue8 = var10;
      }

      public HoveredData getHoveredData() {
         return this.hoveredData;
      }

      public Class15 getClass15() {
         return this.class15;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float size() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }
   }
}

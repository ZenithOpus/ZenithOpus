package org.zenith.utility.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.utility.misc.AnimationState;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.Class16;
import org.zenith.utility.misc.HoveredData;

public final class Stage implements MinecraftAccess {
   private static final int CONST_intValue = 15728880;
   private static final long CONST_longValue = System.nanoTime();
   private static final Stage.Data[] datas = new Stage.Data[]{
      new Stage.Data("idle", 3.0F, false),
      new Stage.Data("play", 2.2F, true),
      new Stage.Data("sit", 3.2F, false),
      new Stage.Data("walk", 2.4F, false),
      new Stage.Data("lie", 3.4F, false),
      new Stage.Data("idle2", 2.6F, true),
      new Stage.Data("perch", 3.0F, false),
      new Stage.Data("happy", 1.8F, true)
   };
   private static final Stage.Data[] datas2 = new Stage.Data[]{
      new Stage.Data("idle", 3.0F, false),
      new Stage.Data("play", 2.2F, true),
      new Stage.Data("fly", 2.6F, false),
      new Stage.Data("perch", 3.2F, false),
      new Stage.Data("idle2", 2.6F, true),
      new Stage.Data("sit", 3.0F, false),
      new Stage.Data("happy", 1.8F, true)
   };
   private final AnimationState animationState = new AnimationState();
   private final Class16 class16 = new Class16();
   private Class15 class15;
   private int intValue = -1;
   private float floatValue;

   public void reset() {
      this.class15 = null;
      this.intValue = -1;
   }

   public void method893(DrawContext var1, HoveredData var2, Class15 var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      if (var3 != null && var3.intValue3 != 0) {
         float var10 = (float)(System.nanoTime() - CONST_longValue) / 1.0E9F;
         if (var3 != this.class15) {
            this.class15 = var3;
            this.intValue = -1;
         }

         Stage.Data[] var11 = var3.data3 == null || var3.data3.getType4() != Class15.Type4.FLYER && var3.data3.getType4() != Class15.Type4.FLOATER
            ? datas
            : datas2;
         if (this.intValue < 0 || var10 - this.floatValue >= var11[this.intValue].getFloatValue()) {
            this.intValue = (this.intValue + 1) % var11.length;
            this.floatValue = var10;
            Stage.Data var12 = var11[this.intValue];
            if (var12.isFlag()) {
               if (!this.animationState.method6540(var3, var12.getText(), var10)) {
                  this.animationState.method6539(var3, "idle", var10);
               }
            } else {
               this.animationState.method6539(var3, var12.getText(), var10);
               this.animationState.method6541(var10);
            }
         }

         float[] var26 = this.class16.method2139(var3, this.animationState, var10, null);
         MatrixStack var13 = MatrixUtils.method666(var1.getMatrices());
         ScissorFlusher.flush();
         RenderState.method1878();
         RenderState.method1882(true);
         Immediate var14 = mc.getBufferBuilders().getEntityVertexConsumers();
         var1.enableScissor((int)Math.floor(var4), (int)Math.floor(var5), (int)Math.ceil(var6), (int)Math.ceil(var7));
         var13.push();

         try {
            float[] var15 = var3.floatValues7;
            float var16 = (var15[0] + var15[3]) / 2.0F;
            float var17 = (var15[1] + var15[4]) / 2.0F;
            float var18 = (var15[2] + var15[5]) / 2.0F;
            float var19 = Math.max(Math.max(var15[3] - var15[0], var15[4] - var15[1]), var15[5] - var15[2]);
            float var20 = Math.min(var6 - var4, var7 - var5) * 0.62F;
            float var21 = var20 / Math.max(0.05F, var19);
            var13.translate((var4 + var6) / 2.0F, (var5 + var7) / 2.0F + (var7 - var5) * 0.06F, 80.0F);
            var13.scale(var21, var21, -var21);
            var13.multiply(new Quaternionf().rotationX((float)Math.toRadians(var9)));
            var13.multiply(new Quaternionf().rotationY((float)Math.toRadians(var8)));
            var13.translate(-var16, -var17, -var18);
            boolean var22 = var2.getRarity().isFlag();
            OutlineVertexProvider.method1542(var14, var4x -> {
               for (int var5x = 0; var5x <= 6; var5x++) {
                  if (var3.method2033(var5x)) {
                     BoneMatrices.method4787(var3, var26, var5x, var13, var4x, 15728880, OverlayTexture.DEFAULT_UV, true, var22, -1);
                  }
               }
            });
         } finally {
            var13.pop();
         }

         var14.draw();
         var1.disableScissor();
      }
   }

   private static final class Data {
      private final String text;
      private final float floatValue;
      private final boolean flag;

      public String text() {
         return this.text;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, floatValue, flag);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "floatValue=" + this.floatValue + ", " + "flag=" + this.flag + "]";
      }

      private Data(String var1, float var2, boolean var3) {
         this.text = var1;
         this.floatValue = var2;
         this.flag = var3;
      }

      public String getText() {
         return this.text;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }
}

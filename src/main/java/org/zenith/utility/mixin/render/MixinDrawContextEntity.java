package org.zenith.utility.mixin.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.joml.Matrix3x2f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({DrawContext.class})
public abstract class MixinDrawContextEntity {
   @Unique
   private boolean zenith$entityTransformed;

   @Inject(
      method = {"addEntity(Lnet/minecraft/client/render/entity/state/EntityRenderState;FLorg/joml/Vector3f;Lorg/joml/Quaternionf;Lorg/joml/Quaternionf;IIII)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$followPose(
      EntityRenderState var1, float var2, Vector3f var3, Quaternionf var4, Quaternionf var5, int var6, int var7, int var8, int var9, CallbackInfo var10
   ) {
      if (!this.zenith$entityTransformed) {
         DrawContext var11 = (DrawContext)(Object)this;
         Matrix3x2f var12 = new Matrix3x2f(var11.getMatrices());
         float var13 = (float)Math.sqrt(var12.m00 * var12.m00 + var12.m01 * var12.m01);
         float var14 = (float)Math.sqrt(var12.m10 * var12.m10 + var12.m11 * var12.m11);
         boolean var15 = Math.abs(var13 - 1.0F) < 0.001F && Math.abs(var14 - 1.0F) < 0.001F && Math.abs(var12.m20) < 0.01F && Math.abs(var12.m21) < 0.01F;
         if (!var15) {
            Vector2f var16 = var12.transformPosition(new Vector2f(var6, var7));
            Vector2f var17 = var12.transformPosition(new Vector2f(var8, var9));
            int var18 = Math.round(Math.min(var16.x, var17.x));
            int var19 = Math.round(Math.min(var16.y, var17.y));
            int var20 = Math.round(Math.max(var16.x, var17.x));
            int var21 = Math.round(Math.max(var16.y, var17.y));
            if (var20 - var18 >= 1 && var21 - var19 >= 1) {
               float var22 = (var13 + var14) / 2.0F;
               this.zenith$entityTransformed = true;
               var11.getMatrices().pushMatrix();

               try {
                  var11.getMatrices().identity();
                  var11.addEntity(var1, var2 * var22, var3, var4, var5, var18, var19, var20, var21);
               } finally {
                  var11.getMatrices().popMatrix();
                  this.zenith$entityTransformed = false;
               }

               var10.cancel();
            } else {
               var10.cancel();
            }
         }
      }
   }
}

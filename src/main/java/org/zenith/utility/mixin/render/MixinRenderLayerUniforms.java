package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderPass;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import org.joml.Vector4fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.render.GuiVertexBatch;
import org.zenith.utility.render.UniformBinder;

@Mixin({RenderLayer.class})
public abstract class MixinRenderLayerUniforms {
   @Shadow
   @Final
   public RenderSetup renderSetup;

   @Inject(
      method = {"draw(Lnet/minecraft/client/render/BuiltBuffer;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/systems/RenderSystem;bindDefaultUniforms(Lcom/mojang/blaze3d/systems/RenderPass;)V",
         shift = Shift.AFTER
      )}
   )
   private void zenith$bindLayerUniforms(BuiltBuffer var1, CallbackInfo var2, @Local RenderPass var3) {
      UniformBinder.method5482(var3, this.renderSetup.pipeline);
   }

   @ModifyArg(
      method = {"draw(Lnet/minecraft/client/render/BuiltBuffer;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gl/DynamicUniforms;write(Lorg/joml/Matrix4fc;Lorg/joml/Vector4fc;Lorg/joml/Vector3fc;Lorg/joml/Matrix4fc;)Lcom/mojang/blaze3d/buffers/GpuBufferSlice;"
      ),
      index = 1
   )
   private Vector4fc zenith$shaderColor(Vector4fc var1) {
      return GuiVertexBatch.method105(var1);
   }
}

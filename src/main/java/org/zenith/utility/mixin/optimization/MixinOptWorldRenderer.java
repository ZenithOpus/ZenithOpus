package org.zenith.utility.mixin.optimization;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.Handle;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.utility.misc.OptimizationFlags;
import org.zenith.utility.misc.Util29;

@Mixin({WorldRenderer.class})
public class MixinOptWorldRenderer {
   @Shadow
   @Final
   private MinecraftClient client;
   @Shadow
   @Final
   private WorldRenderState worldRenderState;
   @Unique
   private boolean zenith$outlineDirty = true;

   @WrapOperation(
      method = {"method_62214(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/render/state/WorldRenderState;Lnet/minecraft/util/profiler/Profiler;Lorg/joml/Matrix4f;Lnet/minecraft/client/util/Handle;Lnet/minecraft/client/util/Handle;ZLnet/minecraft/client/util/Handle;Lnet/minecraft/client/util/Handle;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/systems/CommandEncoder;clearColorAndDepthTextures(Lcom/mojang/blaze3d/textures/GpuTexture;ILcom/mojang/blaze3d/textures/GpuTexture;D)V"
      )}
   )
   private void zenith$skipCleanOutlineClear(CommandEncoder var1, GpuTexture var2, int var3, GpuTexture var4, double var5, Operation<Void> var7) {
      boolean var8 = this.worldRenderState.hasOutline;
      if (!OptimizationFlags.flag11 || var8 || this.zenith$outlineDirty) {
         var7.call(new Object[]{var1, var2, var3, var4, var5});
      }

      this.zenith$outlineDirty = var8;
   }

   @Inject(
      method = {"drawEntityOutlinesFramebuffer()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$skipEmptyOutlineBlit(CallbackInfo var1) {
      if (OptimizationFlags.flag11 && !this.zenith$outlineDirty) {
         var1.cancel();
      }
   }

   @WrapMethod(
      method = {"fillEntityRenderStates(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/Frustum;Lnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/client/render/state/WorldRenderState;)V"}
   )
   private void zenith$markEntityStates(Camera var1, Frustum var2, RenderTickCounter var3, WorldRenderState var4, Operation<Void> var5) {
      boolean var6 = Util29.flag;
      Util29.flag = (Object)this == this.client.worldRenderer;

      try {
         var5.call(new Object[]{var1, var2, var3, var4});
      } finally {
         Util29.flag = var6;
      }
   }

   @WrapMethod(
      method = {"method_62214(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/render/state/WorldRenderState;Lnet/minecraft/util/profiler/Profiler;Lorg/joml/Matrix4f;Lnet/minecraft/client/util/Handle;Lnet/minecraft/client/util/Handle;ZLnet/minecraft/client/util/Handle;Lnet/minecraft/client/util/Handle;)V"}
   )
   private void zenith$markWorldPass(
      GpuBufferSlice var1,
      WorldRenderState var2,
      Profiler var3,
      Matrix4f var4,
      Handle var5,
      Handle var6,
      boolean var7,
      Handle var8,
      Handle var9,
      Operation<Void> var10
   ) {
      boolean var11 = Util29.flag2;
      Util29.flag2 = (Object)this == this.client.worldRenderer;

      try {
         var10.call(new Object[]{var1, var2, var3, var4, var5, var6, var7, var8, var9});
      } finally {
         Util29.flag2 = var11;
      }
   }
}

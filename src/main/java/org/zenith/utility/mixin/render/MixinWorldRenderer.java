package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.PostEffectProcessor.FramebufferSet;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.FramePass;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.memory.ObjectAllocator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.client.modules.impl.render.WeatherModule;
import org.zenith.utility.misc.FrustumHolder;
import org.zenith.utility.render.EspGlowPass;
import org.zenith.utility.render.FirePostPass;
import org.zenith.utility.render.SkyPass;
import org.zenith.utility.render.UniformBinder;
import org.zenith.utility.render.WeatherDepthBuffer;
import org.zenith.utility.render.WetnessPass;
import org.zenith.utility.render.WorldGradePass;
import org.zenith.utility.render.WorldProfiler;

@Mixin({WorldRenderer.class})
public abstract class MixinWorldRenderer {
   @Shadow
   @Final
   private MinecraftClient client;
   @Shadow
   @Final
   private DefaultFramebufferSet framebufferSet;
   @Shadow
   private ClientWorld world;
   @Shadow
   private BuiltChunkStorage chunks;
   @Unique
   private static final Matrix4f zenith$worldMatrix = new Matrix4f();

   @Inject(
      method = {"setupFrustum(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/client/render/Frustum;"},
      at = {@At("RETURN")}
   )
   private void zenith$captureFrustum(Matrix4f var1, Matrix4f var2, Vec3d var3, CallbackInfoReturnable<Frustum> var4) {
      FrustumHolder.method3035((Frustum)var4.getReturnValue());
   }

   @Inject(
      method = {"isRenderingReady(Lnet/minecraft/util/math/BlockPos;)Z"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$guardRenderingReadyWithoutChunks(BlockPos var1, CallbackInfoReturnable<Boolean> var2) {
      if (this.world == null || this.chunks == null) {
         var2.setReturnValue(false);
      }
   }

   @Inject(
      method = {"tick(Lnet/minecraft/client/render/Camera;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$skipTickWithoutWorld(Camera var1, CallbackInfo var2) {
      if (this.world == null) {
         var2.cancel();
      }
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/util/memory/ObjectAllocator;Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$skipMainRenderWithoutWorld(
      ObjectAllocator var1,
      RenderTickCounter var2,
      boolean var3,
      Camera var4,
      Matrix4f var5,
      Matrix4f var6,
      Matrix4f var7,
      GpuBufferSlice var8,
      Vector4f var9,
      boolean var10,
      CallbackInfo var11
   ) {
      if ((Object)this == this.client.worldRenderer && (this.world == null || this.chunks == null)) {
         var11.cancel();
      }
   }

   @Inject(
      method = {"scheduleBlockRerenderIfNeeded(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$skipOffscreenBlockRerender(BlockPos var1, BlockState var2, BlockState var3, CallbackInfo var4) {
      if (this.world != this.client.world) {
         var4.cancel();
      }
   }

   @Inject(
      method = {"scheduleBlockRenders(IIIIII)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$skipOffscreenBlockRenders(int var1, int var2, int var3, int var4, int var5, int var6, CallbackInfo var7) {
      if (this.world != this.client.world) {
         var7.cancel();
      }
   }

   @Inject(
      method = {"scheduleSectionRender(Lnet/minecraft/util/math/BlockPos;Z)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$skipOffscreenSectionRender(BlockPos var1, boolean var2, CallbackInfo var3) {
      if (this.world != this.client.world) {
         var3.cancel();
      }
   }

   @Inject(
      method = {"scheduleChunkRenders3x3x3(III)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$skipOffscreenChunkRenders3x3x3(int var1, int var2, int var3, CallbackInfo var4) {
      if (this.world != this.client.world) {
         var4.cancel();
      }
   }

   @Redirect(
      method = {"render(Lnet/minecraft/client/util/memory/ObjectAllocator;Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gl/PostEffectProcessor;render(Lnet/minecraft/client/render/FrameGraphBuilder;IILnet/minecraft/client/gl/PostEffectProcessor$FramebufferSet;)V",
         ordinal = 0
      )
   )
   private void zenith$renderShaderEspOutline(PostEffectProcessor var1, FrameGraphBuilder var2, int var3, int var4, FramebufferSet var5) {
      boolean var6 = EspGlowPass.method2636(var2, var3, var4, var5);
      boolean var7 = var6 | FirePostPass.method5079(var2, var3, var4, var5, var6);
      if (!var7) {
         var1.render(var2, var3, var4, var5);
      }
   }

   @Inject(
      method = {"pushEntityRenders(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/state/WorldRenderState;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;)V"},
      at = {@At("HEAD")}
   )
   private void zenith$captureTerrainDepth(MatrixStack var1, WorldRenderState var2, OrderedRenderCommandQueue var3, CallbackInfo var4) {
      if (WetnessPass.method3122(this.client.gameRenderer.getCamera())) {
         int var5 = WorldProfiler.method785(0);
         WeatherDepthBuffer.method5130();
         WorldProfiler.method786(var5);
      }
   }

   @Inject(
      method = {"renderSky(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/Camera;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/FramePass;setRenderer(Ljava/lang/Runnable;)V",
         shift = Shift.AFTER
      )}
   )
   private void zenith$renderSkyEffects(FrameGraphBuilder var1, Camera var2, GpuBufferSlice var3, CallbackInfo var4) {
      int var5 = this.client.getFramebuffer().textureWidth;
      int var6 = this.client.getFramebuffer().textureHeight;
      long var7 = WorldProfiler.method782();
      WorldProfiler.method787(0);
      SkyPass.method7267(var1, var5, var6, this.framebufferSet, var2);
      WorldProfiler.method788();
      WorldProfiler.method783(25, var7);
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/util/memory/ObjectAllocator;Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/FrameGraphBuilder;run(Lnet/minecraft/client/util/memory/ObjectAllocator;Lnet/minecraft/client/render/FrameGraphBuilder$Profiler;)V"
      )}
   )
   private void zenith$appendWeatherGloss(
      ObjectAllocator var1,
      RenderTickCounter var2,
      boolean var3,
      Camera var4,
      Matrix4f var5,
      Matrix4f var6,
      Matrix4f var7,
      GpuBufferSlice var8,
      Vector4f var9,
      boolean var10,
      CallbackInfo var11,
      @Local FrameGraphBuilder var12
   ) {
      int var13 = this.client.getFramebuffer().textureWidth;
      int var14 = this.client.getFramebuffer().textureHeight;
      WeatherDepthBuffer.method5131();
      long var15 = WorldProfiler.method782();
      WorldProfiler.method787(2);
      boolean var17 = WetnessPass.method3123(var12, var13, var14, this.framebufferSet, var4);
      WorldProfiler.method788();
      WorldProfiler.method783(27, var15);
      if (!var17) {
         var15 = WorldProfiler.method782();
         WorldProfiler.method787(1);
         WorldGradePass.method6105(var12, var13, var14, this.framebufferSet, var4);
         WorldProfiler.method788();
         WorldProfiler.method783(26, var15);
      }

      if (WeatherModule.INSTANCE.method3116()) {
         FramePass var18 = var12.createPass("zenith_weather");
         this.framebufferSet.mainFramebuffer = var18.transfer(this.framebufferSet.mainFramebuffer);
         var18.setRenderer(() -> {
            int var0 = WorldProfiler.method785(13);
            WeatherModule.INSTANCE.method3117(zenith$worldMatrix);
            WorldProfiler.method786(var0);
         });
      }
   }

   @Inject(
      method = {"renderWeather(Lnet/minecraft/client/render/FrameGraphBuilder;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$skipVanillaWeather(FrameGraphBuilder var1, GpuBufferSlice var2, CallbackInfo var3) {
      if (WeatherModule.INSTANCE.method3113()) {
         var3.cancel();
      }
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/util/memory/ObjectAllocator;Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V"},
      at = {@At("HEAD")}
   )
   private void zenith$captureWorldMatrix(
      ObjectAllocator var1,
      RenderTickCounter var2,
      boolean var3,
      Camera var4,
      Matrix4f var5,
      Matrix4f var6,
      Matrix4f var7,
      GpuBufferSlice var8,
      Vector4f var9,
      boolean var10,
      CallbackInfo var11
   ) {
      if (WorldProfiler.enabled && (Object)this == this.client.worldRenderer) {
         WorldProfiler.method790();
      }

      zenith$worldMatrix.set(var5);
      UniformBinder.method5481();
   }
}

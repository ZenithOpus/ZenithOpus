package org.zenith.utility.mixin.render;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.client.events.CrosshairTargetEvent;
import org.zenith.client.events.FovEvent;
import org.zenith.client.events.ProjectionMatrixEvent;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.render.ShaderHandModule;
import org.zenith.client.screens.media.NowPlayingScreen;
import org.zenith.utility.misc.FrameTimer;
import org.zenith.utility.misc.Util15;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.HandShaders;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.MenuDrawContext;

@Mixin({GameRenderer.class})
public abstract class MixinGameRenderer {
   @Shadow
   @Final
   private Camera camera;

   @Shadow
   public abstract float getFarPlaneDistance();

   @Shadow
   public abstract void renderHand(float var1, boolean var2, Matrix4f var3);

   @Inject(
      method = {"getBasicProjectionMatrix(F)Lorg/joml/Matrix4f;"},
      at = {@At("TAIL")},
      cancellable = true
   )
   public void getBasicProjectionMatrixHook(float var1, CallbackInfoReturnable<Matrix4f> var2) {
      ProjectionMatrixEvent var3 = new ProjectionMatrixEvent();
      EventManager.call(var3);
      if (var3.isCancelled()) {
         Matrix4f var4 = new Matrix4f();
         var4.perspective(var1 * (float) (Math.PI / 180.0), var3.getFloatValue(), 0.05F, this.getFarPlaneDistance());
         var2.setReturnValue(var4);
      }
   }

   @Inject(
      method = {"updateCrosshairTarget(F)V"},
      at = {@At("RETURN")}
   )
   public void getBasicProjectionMatrixHook(float var1, CallbackInfo var2) {
      EventManager.call(new CrosshairTargetEvent());
   }

   @ModifyExpressionValue(
      method = {"getFov(Lnet/minecraft/client/render/Camera;FZ)F"},
      at = {@At(
         value = "INVOKE",
         target = "Ljava/lang/Integer;intValue()I",
         remap = false
      )}
   )
   private int hookGetFov(int var1) {
      FovEvent var2 = new FovEvent();
      EventManager.call(var2);
      return var2.isCancelled() ? var2.getIntValue() : var1;
   }

   @Inject(
      method = {"getFov(Lnet/minecraft/client/render/Camera;FZ)F"},
      at = {@At("RETURN")}
   )
   private void captureWeatherFov(Camera var1, float var2, boolean var3, CallbackInfoReturnable<Float> var4) {
      if (var3) {
         Util15.method2703(var4.getReturnValueF());
      }
   }

   @Inject(
      method = {"renderWorld(Lnet/minecraft/client/render/RenderTickCounter;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$skipCoveredWorld(RenderTickCounter var1, CallbackInfo var2) {
      if (NowPlayingScreen.coversWorld()) {
         var2.cancel();
      }
   }

   @Inject(
      method = {"renderWorld(Lnet/minecraft/client/render/RenderTickCounter;)V"},
      at = {@At("HEAD")}
   )
   private void beginWorldGrade(RenderTickCounter var1, CallbackInfo var2) {
      FrameTimer.beginFrame();
   }

   @Inject(
      method = {"renderWorld(Lnet/minecraft/client/render/RenderTickCounter;)V"},
      at = {@At("RETURN")}
   )
   private void endWorldGrade(RenderTickCounter var1, CallbackInfo var2) {
      FrameTimer.endFrame();
   }

   @Redirect(
      method = {"renderWorld(Lnet/minecraft/client/render/RenderTickCounter;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/GameRenderer;renderHand(FZLorg/joml/Matrix4f;)V"
      )
   )
   public void afterRenderHand(GameRenderer var1, float var2, boolean var3, Matrix4f var4) {
      ShaderHandModule var5 = ShaderHandModule.INSTANCE;
      if (var5 != null && var5.isEnabled()) {
         if (!HandShaders.isInitialized()) {
            HandShaders.method6278();
         }

         HandShaders.method6281();
         var5.method1239(() -> this.renderHand(var2, var3, var4), var2);
      } else {
         this.renderHand(var2, var3, var4);
      }
   }

   @Inject(
      method = {"renderWorld(Lnet/minecraft/client/render/RenderTickCounter;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/util/memory/ObjectAllocator;Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V",
         shift = Shift.AFTER
      )}
   )
   public void hookWorldRender(RenderTickCounter var1, CallbackInfo var2, @Local(ordinal = 0) Matrix4f var3, @Local(ordinal = 1) Matrix4f var4) {
      MatrixStack var5 = new MatrixStack();
      var5.multiplyPositionMatrix(var4);
      GlowUtil.method2250(new Matrix4f(var3));
      GlowUtil.method2251(RenderSystem.getModelViewMatrix());
      GlowUtil.method2252(var4);
      ProjectionUtils.method299();
      WorldRenderEvent var6 = new WorldRenderEvent(var5, var1.getTickProgress(false));
      EventManager.call(var6);
      GlowUtil.method2226(var6.getMatrixStack());
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/render/RenderTickCounter;Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/MinecraftClient;getOverlay()Lnet/minecraft/client/gui/screen/Overlay;",
         ordinal = 0
      )}
   )
   private void renderScreenHook(
      RenderTickCounter var1, boolean var2, CallbackInfo var3, @Local(ordinal = 0) int var4, @Local(ordinal = 1) int var5, @Local DrawContext var6
   ) {
      ScissorFlusher.flush();
      var6.getMatrices().pushMatrix();

      try {
         EventManager.call(
            new ScreenRenderEvent(MenuDrawContext.method8080(var6, var4, var5, MinecraftAccess.mc.getRenderTickCounter().getTickProgress(false)))
         );
         ScissorFlusher.flush();
      } catch (Exception var8) {
      }

      var6.getMatrices().popMatrix();
      Framebuffer var7 = MinecraftAccess.mc.getFramebuffer();
      if (var7.getDepthAttachment() != null) {
         RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(var7.getDepthAttachment(), 1.0);
      }

      RenderState.method1881();
   }
}

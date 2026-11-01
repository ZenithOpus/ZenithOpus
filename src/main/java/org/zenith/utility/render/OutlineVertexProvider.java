package org.zenith.utility.render;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.DiffuseLighting.Type;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.command.RenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.zenith.RenderState;

public final class OutlineVertexProvider {
   private static final OutlineVertexConsumerProvider outlineVertexConsumerProvider = new OutlineVertexConsumerProvider();
   private static boolean flag;

   public static void method1538() {
      if (!flag && ImmediateRenderer.method8005()) {
         method1543(MinecraftClient.getInstance());
         flag = true;
      }
   }

   public static void method1539() {
      if (flag) {
         flag = false;
         method1544(MinecraftClient.getInstance());
      }
   }

   private OutlineVertexProvider() {
   }

   public static void method1540(EntityRenderState var0) {
      if (var0 instanceof BipedEntityRenderState var1) {
         var1.equippedHeadStack = ItemStack.EMPTY;
         var1.equippedChestStack = ItemStack.EMPTY;
         var1.equippedLegsStack = ItemStack.EMPTY;
         var1.equippedFeetStack = ItemStack.EMPTY;
      }

      if (var0 instanceof LivingEntityRenderState var2) {
         var2.headItemRenderState.clear();
         var2.wearingSkullType = null;
      }
   }

   public static CameraRenderState method1541(Vec3d var0) {
      CameraRenderState var1 = new CameraRenderState();
      var1.pos = var0;
      var1.entityPos = var0;
      var1.blockPos = BlockPos.ofFloored(var0);
      var1.initialized = true;
      return var1;
   }

   public static void method1542(Immediate var0, Consumer<OrderedRenderCommandQueue> var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      boolean var3 = ImmediateRenderer.method8005() && !flag;
      if (var3) {
         method1543(var2);
      }

      try {
         OrderedRenderCommandQueueImpl var4 = new OrderedRenderCommandQueueImpl();

         try (RenderDispatcher var5 = new RenderDispatcher(
               var4, var2.getBlockRenderManager(), var0, var2.getAtlasManager(), outlineVertexConsumerProvider, var0, var2.textRenderer
            )) {
            var1.accept(var4);
            var5.render();
         }

         var0.draw();
      } finally {
         if (var3) {
            method1544(var2);
         }

         RenderState.method1881();
      }
   }

   private static void method1543(MinecraftClient var0) {
      RenderSystem.backupProjectionMatrix();
      RenderSystem.setProjectionMatrix(ImmediateRenderer.method8019(), ProjectionType.ORTHOGRAPHIC);
      RenderSystem.getModelViewStack().pushMatrix().set(ImmediateRenderer.matrix4f2);
      var0.gameRenderer.getDiffuseLighting().setShaderLights(Type.ENTITY_IN_UI);
      Framebuffer var1 = var0.getFramebuffer();
      if (var1.getDepthAttachment() != null) {
         RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(var1.getDepthAttachment(), 1.0);
      }
   }

   private static void method1544(MinecraftClient var0) {
      var0.gameRenderer.getDiffuseLighting().setShaderLights(Type.ITEMS_3D);
      RenderSystem.getModelViewStack().popMatrix();
      RenderSystem.restoreProjectionMatrix();
   }

   public static <S extends EntityRenderState> void method1545(EntityRenderer<?, S> var0, S var1, MatrixStack var2, Immediate var3, CameraRenderState var4) {
      method1542(var3, var4x -> var0.render(var1, var2, var4x, var4));
   }
}

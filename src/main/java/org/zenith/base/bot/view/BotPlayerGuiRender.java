package org.zenith.base.bot.view;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.zenith.MatrixUtils;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.utility.render.ScissorFlusher;

public final class BotPlayerGuiRender {
   private static final BotPlayerRenderer RENDERER = new BotPlayerRenderer();

   private BotPlayerGuiRender() {
   }

   public static void drawEntity(
      DrawContext var0, int var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, BotPlayer var9, @Nullable BotPlayHandler var10
   ) {
      float var11 = (var1 + var3) / 2.0F;
      float var12 = (var2 + var4) / 2.0F;
      var0.enableScissor(var1, var2, var3, var4);
      float var13 = (float)Math.atan((var11 - var7) / 40.0F);
      float var14 = (float)Math.atan((var12 - var8) / 40.0F);
      Quaternionf var15 = new Quaternionf().rotateZ((float) Math.PI);
      var15.mul(new Quaternionf().rotateX(var14 * 20.0F * (float) (Math.PI / 180.0)));
      float var16 = var9.getScale();
      Vector3f var17 = new Vector3f(0.0F, var9.getHeight() / 2.0F + var6 * var16, 0.0F);
      float var18 = var5 / var16;
      MatrixStack var19 = MatrixUtils.method666(var0.getMatrices());
      var19.translate(var11, var12, 50.0F);
      var19.scale(var18, var18, -var18);
      var19.translate(var17.x, var17.y, var17.z);
      var19.multiply(var15);
      ScissorFlusher.flush();
      MinecraftClient var20 = MinecraftClient.getInstance();
      Immediate var21 = var20.getBufferBuilders().getEntityVertexConsumers();
      synchronized (var9.networkHandler.getClient().getRenderStateLock()) {
         float var23 = var9.bodyYaw;
         float var24 = var9.getYaw();
         float var25 = var9.getPitch();
         float var26 = var9.lastHeadYaw;
         float var27 = var9.headYaw;
         var9.bodyYaw = 180.0F + var13 * 20.0F;
         var9.setYaw(180.0F + var13 * 40.0F);
         var9.setPitch(-var14 * 20.0F);
         var9.headYaw = var9.getYaw();
         var9.lastHeadYaw = var9.getYaw();

         try {
            RENDERER.render(var9, var10, var9.getX(), var9.getY(), var9.getZ(), 1.0F, var19, var21, 15728880, false);
            var21.draw();
         } finally {
            var9.bodyYaw = var23;
            var9.setYaw(var24);
            var9.setPitch(var25);
            var9.lastHeadYaw = var26;
            var9.headYaw = var27;
         }
      }

      var0.disableScissor();
   }
}

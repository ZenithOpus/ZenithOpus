package org.zenith.client.screens.builder;

import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.zenith.MatrixUtils;
import org.zenith.base.figura.model.rendering.EntityRenderMode;
import org.zenith.base.figura.render.FiguraGuiRenderContext;
import org.zenith.utility.render.OutlineVertexProvider;
import org.zenith.utility.render.ScissorFlusher;

public class PlayerPreview3D {
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private PlayerPreview3D.QueueOverlay queueOverlay;
   private float x;
   private float y;
   private float width;
   private float height;
   private float zoom = 1.0F;
   private float rotationY = 0.0F;
   private float rotationX = 0.0F;
   private boolean isDragging = false;
   private float maxSize = 60.0F;
   private float idleSpin = 0.0F;
   private boolean hideHeldItems = false;
   private ItemStack forcedMainHand = null;
   private boolean freePitch = false;
   private long lastFrameNanos;
   private double lastMouseX;
   private double lastMouseY;
   private boolean turnedByHand;
   private static final float MAX_PITCH = 45.0F;
   private static final float DRAG_SENSITIVITY = 1.0F;
   private float focusHeight = Float.NaN;
   private float focusZoom = 1.0F;
   private float focusYaw = Float.NaN;
   private float shownHeight = Float.NaN;
   private float shownZoom = 1.0F;

   public PlayerPreview3D(float var1, float var2, float var3, float var4) {
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
   }

   public void setMaxSize(float var1) {
      this.maxSize = Math.max(12.0F, var1);
   }

   public void setIdleSpin(float var1) {
      this.idleSpin = var1;
   }

   public void setHideHeldItems(boolean var1) {
      this.hideHeldItems = var1;
   }

   public void setForcedMainHand(ItemStack var1) {
      this.forcedMainHand = var1;
   }

   public void setFreePitch(boolean var1) {
      this.freePitch = var1;
   }

   public void resetRotation() {
      this.rotationX = 0.0F;
      this.rotationY = 0.0F;
      this.turnedByHand = false;
   }

   private void advanceIdleSpin() {
      long var1 = System.nanoTime();
      long var3 = this.lastFrameNanos;
      this.lastFrameNanos = var1;
      if (this.idleSpin != 0.0F && !this.isDragging && !this.turnedByHand && var3 != 0L) {
         float var5 = (float)((var1 - var3) / 1.0E9);
         if (!(var5 <= 0.0F) && !(var5 > 0.25F)) {
            this.rotationY = (this.rotationY + this.idleSpin * var5) % 360.0F;
         }
      }
   }

   public void setFocus(float var1, float var2, float var3) {
      if (!Float.isNaN(var3) && (Float.isNaN(this.focusYaw) || Math.abs(MathHelper.wrapDegrees(var3 - this.focusYaw)) > 1.0F)) {
         this.turnedByHand = false;
      }

      this.focusHeight = var1;
      this.focusZoom = var2;
      this.focusYaw = var3;
   }

   public void clearFocus() {
      this.setFocus(Float.NaN, 1.0F, Float.NaN);
   }

   private void advanceFocus(LivingEntity var1) {
      float var2 = Float.isNaN(this.focusHeight) ? var1.getHeight() / 2.0F : this.focusHeight;
      if (Float.isNaN(this.shownHeight)) {
         this.shownHeight = var2;
      }

      this.shownHeight = this.shownHeight + (var2 - this.shownHeight) * 0.12F;
      this.shownZoom = this.shownZoom + (this.focusZoom - this.shownZoom) * 0.12F;
      if (!Float.isNaN(this.focusYaw) && !this.isDragging && !this.turnedByHand) {
         float var3 = (float)Math.sin(System.nanoTime() / 1.0E9 * 0.6) * 18.0F / Math.max(1.0F, this.focusZoom * this.focusZoom);
         float var4 = this.focusYaw + var3;
         this.rotationY = this.rotationY + MathHelper.wrapDegrees(var4 - this.rotationY) * 0.1F;
      }
   }

   public void setBounds(float var1, float var2, float var3, float var4) {
      this.x = var1;
      this.y = var2;
      this.width = var3;
      this.height = var4;
   }

   public void render(DrawContext var1, LivingEntity var2, float var3, float var4) {
      this.render(var1, var2, var3, var4, null);
   }

   public void render(DrawContext var1, LivingEntity var2, float var3, float var4, PlayerPreview3D.PreviewOverlay var5) {
      if (var2 != null) {
         float var6 = this.x + this.width / 2.0F;
         float var7 = this.y + this.height / 2.0F;
         this.advanceFocus(var2);
         int var8 = (int)Math.max(12.0F, Math.min(this.maxSize, 60.0F * this.zoom) * this.shownZoom);
         float var9 = var2.bodyYaw;
         float var10 = var2.lastBodyYaw;
         float var11 = var2.getYaw();
         float var12 = var2.lastYaw;
         float var13 = var2.getPitch();
         float var14 = var2.lastPitch;
         float var15 = var2.headYaw;
         float var16 = var2.lastHeadYaw;
         ItemStack var17 = var2.getStackInHand(Hand.MAIN_HAND);
         ItemStack var18 = var2.getStackInHand(Hand.OFF_HAND);
         float var19 = var2.limbAnimator.getSpeed();

         try {
            var2.bodyYaw = 0.0F;
            var2.lastBodyYaw = 0.0F;
            var2.setYaw(0.0F);
            var2.lastYaw = 0.0F;
            var2.setPitch(0.0F);
            var2.lastPitch = 0.0F;
            var2.headYaw = 0.0F;
            var2.lastHeadYaw = 0.0F;
            var2.limbAnimator.setSpeed(0.0F);
            if (this.forcedMainHand != null) {
               var2.setStackInHand(Hand.MAIN_HAND, this.forcedMainHand);
               var2.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
            } else if (this.hideHeldItems) {
               var2.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
               var2.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
            }

            if (Float.isNaN(this.focusYaw)) {
               this.advanceIdleSpin();
            }

            this.renderEntityWithRotation(var1, var2, var6, var7, var8, this.rotationX, this.rotationY, var5);
         } finally {
            if (this.forcedMainHand != null || this.hideHeldItems) {
               var2.setStackInHand(Hand.MAIN_HAND, var17);
               var2.setStackInHand(Hand.OFF_HAND, var18);
            }

            var2.bodyYaw = var9;
            var2.lastBodyYaw = var10;
            var2.setYaw(var11);
            var2.lastYaw = var12;
            var2.setPitch(var13);
            var2.lastPitch = var14;
            var2.headYaw = var15;
            var2.lastHeadYaw = var16;
            var2.limbAnimator.setSpeed(var19);
         }
      }
   }

   private void renderEntityWithRotation(
      DrawContext var1, LivingEntity var2, float var3, float var4, int var5, float var6, float var7, PlayerPreview3D.PreviewOverlay var8
   ) {
      MatrixStack var9 = MatrixUtils.method666(var1.getMatrices());
      var9.push();
      EntityRenderManager var10 = mc.getEntityRenderDispatcher();

      try {
         float var11 = var2.getScale();
         float var12 = var5 / Math.max(var11, 1.0E-4F);
         var9.translate(var3, var4, 50.0F);
         var9.scale(var12, var12, -var12);
         var9.translate(0.0F, (Float.isNaN(this.shownHeight) ? var2.getHeight() / 2.0F : this.shownHeight) + 0.0625F * var11, 0.0F);
         Quaternionf var13 = new Quaternionf().rotateX((float)Math.toRadians(var6)).rotateY((float)Math.toRadians(180.0F + var7)).rotateZ((float) Math.PI);
         var9.multiply(var13);
         ScissorFlusher.flush();
         float var14 = mc.getRenderTickCounter().getTickProgress(true);
         Immediate var15 = mc.getBufferBuilders().getEntityVertexConsumers();
         EntityRenderState var16 = var10.getAndUpdateRenderState(var2, var14);
         var16.light = 15728880;
         var16.shadowPieces.clear();
         var16.outlineColor = 0;
         OutlineVertexProvider.method1540(var16);
         CameraRenderState var17 = OutlineVertexProvider.method1541(var2.getEntityPos());
         var17.orientation = new Quaternionf().rotateX((float)Math.toRadians(var6)).conjugate().rotateY((float) Math.PI);
         FiguraGuiRenderContext.mark(var2, EntityRenderMode.FIGURA_GUI);
         OutlineVertexProvider.method1542(var15, var9x -> {
            var10.render(var16, var17, 0.0, 0.0, 0.0, var9, var9x);
            if (this.queueOverlay != null) {
               var9.push();

               try {
                  this.queueOverlay.render(var9, var9x, 15728880, var2, var14);
               } finally {
                  var9.pop();
               }
            }

            if (var8 != null) {
               var9.push();

               try {
                  var8.render(var9, var15, 15728880, var2, var14);
               } finally {
                  var9.pop();
               }
            }
         });
         ScissorFlusher.flush();
      } finally {
         var9.pop();
      }
   }

   public float getPlayerCenterX() {
      return this.x + this.width / 2.0F;
   }

   public float getPlayerCenterY() {
      return this.y + this.height / 2.0F + 40.0F;
   }

   public boolean onMouseClicked(double var1, double var3, int var5) {
      if (this.isInBounds(var1, var3) && var5 == 0) {
         this.isDragging = true;
         this.lastMouseX = var1;
         this.lastMouseY = var3;
         return true;
      } else {
         return false;
      }
   }

   public boolean onMouseReleased(double var1, double var3, int var5) {
      if (var5 == 0 && this.isDragging) {
         this.isDragging = false;
         return true;
      } else {
         return false;
      }
   }

   public boolean onMouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.isDragging && var5 == 0) {
         float var10 = (float)(var1 - this.lastMouseX) * 1.0F;
         float var11 = (float)(var3 - this.lastMouseY) * 1.0F;
         this.rotationY = (this.rotationY - var10) % 360.0F;
         this.rotationX = this.freePitch ? (this.rotationX - var11) % 360.0F : MathHelper.clamp(this.rotationX - var11, -45.0F, 45.0F);
         this.turnedByHand = true;
         this.lastMouseX = var1;
         this.lastMouseY = var3;
         return true;
      } else {
         return false;
      }
   }

   public boolean onMouseScrolled(double var1, double var3, double var5) {
      if (this.isInBounds(var1, var3)) {
         this.zoom += (float)var5 * 0.1F;
         this.zoom = Math.max(0.3F, Math.min(3.0F, this.zoom));
         return true;
      } else {
         return false;
      }
   }

   public boolean isInBounds(double var1, double var3) {
      return var1 >= this.x && var1 <= this.x + this.width && var3 >= this.y && var3 <= this.y + this.height;
   }

   @Generated
   public PlayerPreview3D.QueueOverlay getQueueOverlay() {
      return this.queueOverlay;
   }

   @Generated
   public float getX() {
      return this.x;
   }

   @Generated
   public float getY() {
      return this.y;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public float getZoom() {
      return this.zoom;
   }

   @Generated
   public float getRotationY() {
      return this.rotationY;
   }

   @Generated
   public float getRotationX() {
      return this.rotationX;
   }

   @Generated
   public boolean isDragging() {
      return this.isDragging;
   }

   @Generated
   public float getMaxSize() {
      return this.maxSize;
   }

   @Generated
   public float getIdleSpin() {
      return this.idleSpin;
   }

   @Generated
   public boolean isHideHeldItems() {
      return this.hideHeldItems;
   }

   @Generated
   public ItemStack getForcedMainHand() {
      return this.forcedMainHand;
   }

   @Generated
   public boolean isFreePitch() {
      return this.freePitch;
   }

   @Generated
   public long getLastFrameNanos() {
      return this.lastFrameNanos;
   }

   @Generated
   public double getLastMouseX() {
      return this.lastMouseX;
   }

   @Generated
   public double getLastMouseY() {
      return this.lastMouseY;
   }

   @Generated
   public boolean isTurnedByHand() {
      return this.turnedByHand;
   }

   @Generated
   public float getFocusHeight() {
      return this.focusHeight;
   }

   @Generated
   public float getFocusZoom() {
      return this.focusZoom;
   }

   @Generated
   public float getFocusYaw() {
      return this.focusYaw;
   }

   @Generated
   public float getShownHeight() {
      return this.shownHeight;
   }

   @Generated
   public float getShownZoom() {
      return this.shownZoom;
   }

   @Generated
   public void setQueueOverlay(PlayerPreview3D.QueueOverlay var1) {
      this.queueOverlay = var1;
   }

   @Generated
   public void setX(float var1) {
      this.x = var1;
   }

   @Generated
   public void setY(float var1) {
      this.y = var1;
   }

   @Generated
   public void setWidth(float var1) {
      this.width = var1;
   }

   @Generated
   public void setHeight(float var1) {
      this.height = var1;
   }

   @Generated
   public void setZoom(float var1) {
      this.zoom = var1;
   }

   @Generated
   public void setRotationY(float var1) {
      this.rotationY = var1;
   }

   @Generated
   public void setRotationX(float var1) {
      this.rotationX = var1;
   }

   @Generated
   public void setDragging(boolean var1) {
      this.isDragging = var1;
   }

   @Generated
   public void setLastFrameNanos(long var1) {
      this.lastFrameNanos = var1;
   }

   @Generated
   public void setLastMouseX(double var1) {
      this.lastMouseX = var1;
   }

   @Generated
   public void setLastMouseY(double var1) {
      this.lastMouseY = var1;
   }

   @Generated
   public void setTurnedByHand(boolean var1) {
      this.turnedByHand = var1;
   }

   @Generated
   public void setFocusHeight(float var1) {
      this.focusHeight = var1;
   }

   @Generated
   public void setFocusZoom(float var1) {
      this.focusZoom = var1;
   }

   @Generated
   public void setFocusYaw(float var1) {
      this.focusYaw = var1;
   }

   @Generated
   public void setShownHeight(float var1) {
      this.shownHeight = var1;
   }

   @Generated
   public void setShownZoom(float var1) {
      this.shownZoom = var1;
   }

   @FunctionalInterface
   public interface PreviewOverlay {
      void render(MatrixStack var1, VertexConsumerProvider var2, int var3, LivingEntity var4, float var5);
   }

   @FunctionalInterface
   public interface QueueOverlay {
      void render(MatrixStack var1, OrderedRenderCommandQueue var2, int var3, LivingEntity var4, float var5);
   }
}

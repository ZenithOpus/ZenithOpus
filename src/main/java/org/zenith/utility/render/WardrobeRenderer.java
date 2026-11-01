package org.zenith.utility.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerSkinType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.zenith.MinecraftAccess;
import org.zenith.client.modules.impl.render.ShaderHandModule;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.AnimationCache;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.misc.WardrobeCategory;

public final class WardrobeRenderer implements MinecraftAccess {
   private static final List<WardrobeRenderer.Data> items = new ArrayList<>();

   private WardrobeRenderer() {
   }

   public static boolean method6687(PlayerEntityModel var0, PlayerEntityRenderState var1) {
      Wardrobe var2 = Wardrobe.method2488();
      HoveredData var3 = WardrobeFeatureRenderer.method4445(var1);
      if (var2 != null && var3 != null) {
         Class15 var4 = var2.getRegistry().method1427(var3);
         if (var4 == null) {
            return false;
         } else {
            if (var4.data != null && var4.data.getFloatValues() != null) {
               float var5 = 1.0F - Math.min(1.0F, var1.limbSwingAmplitude * 2.5F);
               if (var1.handSwingProgress > 0.0F || var1.isUsingItem || var1.isGliding || var1.isSwimming || var1.hasVehicle || var1.isInSneakingPose) {
                  var5 = 0.0F;
               }

               if (var5 > 0.0F) {
                  float[] var6 = var4.data.getFloatValues();
                  ModelPart[] var7 = new ModelPart[]{var0.head, var0.body, var0.rightArm, var0.leftArm, var0.rightLeg, var0.leftLeg};

                  for (int var8 = 0; var8 < var7.length; var8++) {
                     ModelPart var9 = var7[var8];
                     float var10 = var6[var8 * 3];
                     float var11 = var6[var8 * 3 + 1];
                     float var12 = var6[var8 * 3 + 2];
                     if (var8 == 0) {
                        var9.pitch += var10 * var5;
                        var9.yaw += var11 * var5;
                        var9.roll += var12 * var5;
                     } else if (var10 != 0.0F || var11 != 0.0F || var12 != 0.0F) {
                        var9.pitch = var9.pitch + (var10 - var9.pitch) * var5;
                        var9.yaw = var9.yaw + (var11 - var9.yaw) * var5;
                        var9.roll = var9.roll + (var12 - var9.roll) * var5;
                     }
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean method6688(PlayerEntityRenderState var0) {
      Wardrobe var1 = Wardrobe.method2488();
      if (var1 != null && !var1.armorOnSkins.isEnabled()) {
         HoveredData var2 = WardrobeFeatureRenderer.method4445(var0);
         if (var2 == null) {
            return false;
         } else {
            Class15 var3 = var1.getRegistry().method1427(var2);
            return var3 != null && (var3.data == null || var3.data.isFlag());
         }
      } else {
         return false;
      }
   }

   public static boolean method6689(PlayerEntityRenderState var0) {
      Wardrobe var1 = Wardrobe.method2488();
      if (var1 != null && !var1.armorOnSkins.isEnabled() && !var0.isGliding) {
         var var2 = WardrobeFeatureRenderer.method4444(var0);
         return var1.method2499(var2, FocusSlotEnum.BACK) != null || var1.method2499(var2, FocusSlotEnum.CAPE) != null || method6688(var0);
      } else {
         return false;
      }
   }

   public static boolean method6690(
      PlayerEntityRenderer var0, MatrixStack var1, OrderedRenderCommandQueue var2, int var3, ModelPart var4, boolean var5, boolean var6
   ) {
      Wardrobe var7 = Wardrobe.method2488();
      if (var7 != null && mc.player != null && var7.showSelf.isEnabled()) {
         if (var0.getModel() instanceof PlayerEntityModel var8) {
            List<HoveredData> var16 = List.copyOf(var7.method2498(mc.player.getGameProfile().name(), true));
            HoveredData var10 = var7.method2499(var16, FocusSlotEnum.CHARACTER);
            int var11 = var4 == var8.rightArm ? Class15.Type.RIGHT_ARM.ordinal() : Class15.Type.LEFT_ARM.ordinal();
            if (!var6 || var10 != null && method6696(var7, var10, var11)) {
               if (!var6 && var10 != null) {
                  return false;
               } else {
                  boolean var12 = mc.player.getSkin().model() == PlayerSkinType.SLIM;
                  Util3.Entry var13 = Util3.method827(var7, var16, var10, var12, var11, var5);
                  if (var6) {
                     var4.resetTransform();
                     var4.visible = true;
                     var8.leftArm.roll = -0.1F;
                     var8.rightArm.roll = 0.1F;
                     method6697(var7, var10, var11, var1, var2, var3, var4, false, Util3.Data.INSTANCE);
                  }

                  for (Object var15_o : (Iterable<?>)(Object)(var16)) {
                     HoveredData var15 = (HoveredData)var15_o;
                     if (var15 != var10
                        && Util3.method824(var15.getFocusSlotEnum()) >= 0
                        && method6696(var7, var15, var11)
                        && (var10 == null || var15.method1546() == WardrobeCategory.ACCESSORY)) {
                        method6697(var7, var15, var11, var1, var2, var3, var4, var15.method1546() == WardrobeCategory.ACCESSORY, var13.method845(var15, var11));
                     }
                  }

                  return var6;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private static void method6691(MatrixStack var0, WardrobeRenderer.Data var1) {
      var1.getModelPart().applyTransform(var0);
      var1.getData().method842(var0);
   }

   public static boolean method6692() {
      return !items.isEmpty();
   }

   public static void method6693() {
      items.clear();
   }

   public static void method6694(Immediate var0) {
      Matrix4fStack var1 = RenderSystem.getModelViewStack();

      for (WardrobeRenderer.Data var3 : items) {
         MatrixStack var4 = new MatrixStack();
         var4.peek().getPositionMatrix().set(var3.getMatrix4f());
         var4.peek().getNormalMatrix().set(var3.getMatrix3f());
         method6691(var4, var3);
         var1.pushMatrix();

         try {
            var1.set(var3.getMatrix4f2());
            BoneMatrices.method4793(
               var3.getClass15(),
               var3.getFloatValues(),
               var3.getIntValue(),
               var4,
               var0,
               var3.getIntValue2(),
               OverlayTexture.DEFAULT_UV,
               true,
               var3.isFlag(),
               -1
            );
            var0.draw();
         } finally {
            var1.popMatrix();
         }
      }

      items.clear();
   }

   public static boolean method6695(OrderedRenderCommandQueue var0) {
      if (items.isEmpty()) {
         return false;
      } else {
         for (WardrobeRenderer.Data var2 : items) {
            MatrixStack var3 = new MatrixStack();
            var3.peek().getPositionMatrix().set(var2.getMatrix4f());
            var3.peek().getNormalMatrix().set(var2.getMatrix3f());
            method6691(var3, var2);
            BoneMatrices.method4787(
               var2.getClass15(),
               var2.getFloatValues(),
               var2.getIntValue(),
               var3,
               var0 != null ? var0 : var2.getOrderedRenderCommandQueue(),
               var2.getIntValue2(),
               OverlayTexture.DEFAULT_UV,
               true,
               var2.isFlag(),
               -1
            );
         }

         items.clear();
         return true;
      }
   }

   private static boolean method6696(Wardrobe var0, HoveredData var1, int var2) {
      Class15 var3 = var0.getRegistry().method1427(var1);
      return var3 != null && var3.method2033(var2);
   }

   private static void method6697(
      Wardrobe var0, HoveredData var1, int var2, MatrixStack var3, OrderedRenderCommandQueue var4, int var5, ModelPart var6, boolean var7, Util3.Data var8
   ) {
      Class15 var9 = var0.getRegistry().method1427(var1);
      AnimationCache.Entry var10 = AnimationCache.method7621(mc.player.getId(), var1, false);
      float var11 = (mc.player.age + mc.getRenderTickCounter().getTickProgress(true)) / 20.0F;
      var10.animationState.method6539(var9, "idle", var11);
      float[] var12 = var10.class16.method2139(var9, var10.animationState, var11, null);
      if (var1.method1546() == WardrobeCategory.CHARACTER) {
         var10.class16.method2140(var9);
      }

      if (var7 && ShaderHandModule.INSTANCE != null && ShaderHandModule.INSTANCE.isFlag23()) {
         items.add(
            new WardrobeRenderer.Data(
               var9,
               (float[])var12.clone(),
               var2,
               new Matrix4f(var3.peek().getPositionMatrix()),
               new Matrix3f(var3.peek().getNormalMatrix()),
               var5,
               var1.getRarity().isFlag(),
               var4,
               new Matrix4f(RenderSystem.getModelViewMatrix()),
               var6,
               var8
            )
         );
      } else {
         boolean var13 = !var7 && var1.method1546() != WardrobeCategory.CHARACTER;
         BoneMatrices.method4791(
            var9,
            var12,
            var2,
            var3,
            var4,
            var5,
            OverlayTexture.DEFAULT_UV,
            var1.getRarity().isFlag(),
            var6,
            var8 == Util3.Data.INSTANCE ? null : var8::method842,
            var13
         );
      }
   }

   private static final class Data {
      private final Class15 class15;
      private final float[] floatValues;
      private final int intValue;
      private final Matrix4f matrix4f;
      private final Matrix3f matrix3f;
      private final int intValue2;
      private final boolean flag;
      private final OrderedRenderCommandQueue orderedRenderCommandQueue;
      private final Matrix4f matrix4f2;
      private final ModelPart modelPart;
      private final Util3.Data data;

      public Class15 class15() {
         return this.class15;
      }

      public float[] floatValues() {
         return this.floatValues;
      }

      public int intValue() {
         return this.intValue;
      }

      public Matrix4f matrix4f() {
         return this.matrix4f;
      }

      public Matrix3f matrix3f() {
         return this.matrix3f;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public boolean flag() {
         return this.flag;
      }

      public OrderedRenderCommandQueue orderedRenderCommandQueue() {
         return this.orderedRenderCommandQueue;
      }

      public Matrix4f matrix4f2() {
         return this.matrix4f2;
      }

      public ModelPart modelPart() {
         return this.modelPart;
      }

      public Util3.Data data() {
         return this.data;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.class15, other.class15) && java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.matrix4f, other.matrix4f) && java.util.Objects.equals(this.matrix3f, other.matrix3f) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.orderedRenderCommandQueue, other.orderedRenderCommandQueue) && java.util.Objects.equals(this.matrix4f2, other.matrix4f2) && java.util.Objects.equals(this.modelPart, other.modelPart) && java.util.Objects.equals(this.data, other.data);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(class15, floatValues, intValue, matrix4f, matrix3f, intValue2, flag, orderedRenderCommandQueue, matrix4f2, modelPart, data);
      }

      @Override
      public String toString() {
         return "Data[" + "class15=" + this.class15 + ", " + "floatValues=" + this.floatValues + ", " + "intValue=" + this.intValue + ", " + "matrix4f=" + this.matrix4f + ", " + "matrix3f=" + this.matrix3f + ", " + "intValue2=" + this.intValue2 + ", " + "flag=" + this.flag + ", " + "orderedRenderCommandQueue=" + this.orderedRenderCommandQueue + ", " + "matrix4f2=" + this.matrix4f2 + ", " + "modelPart=" + this.modelPart + ", " + "data=" + this.data + "]";
      }

      private Data(
         Class15 var1,
         float[] var2,
         int var3,
         Matrix4f var4,
         Matrix3f var5,
         int var6,
         boolean var7,
         OrderedRenderCommandQueue var8,
         Matrix4f var9,
         ModelPart var10,
         Util3.Data var11
      ) {
         this.class15 = var1;
         this.floatValues = var2;
         this.intValue = var3;
         this.matrix4f = var4;
         this.matrix3f = var5;
         this.intValue2 = var6;
         this.flag = var7;
         this.orderedRenderCommandQueue = var8;
         this.matrix4f2 = var9;
         this.modelPart = var10;
         this.data = var11;
      }

      public Class15 getClass15() {
         return this.class15;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public Matrix4f getMatrix4f() {
         return this.matrix4f;
      }

      public Matrix3f getMatrix3f() {
         return this.matrix3f;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public OrderedRenderCommandQueue getOrderedRenderCommandQueue() {
         return this.orderedRenderCommandQueue;
      }

      public Matrix4f getMatrix4f2() {
         return this.matrix4f2;
      }

      public ModelPart getModelPart() {
         return this.modelPart;
      }

      public Util3.Data getData() {
         return this.data;
      }
   }
}

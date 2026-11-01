package org.zenith.utility.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.zenith.MinecraftAccess;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.combat.AimAssistModule;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.game.PetBehavior;
import org.zenith.utility.misc.AnimationState;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.Class16;
import org.zenith.utility.misc.HoveredData;

public final class WardrobePreview implements MinecraftAccess {
   private static final double CONST_doubleValue = 64.0;
   private static final Matrix4f matrix4f = new Matrix4f();
   private static final Vector3f vector3f = new Vector3f();
   private static final Map<Integer, PetBehavior> items = new HashMap<>();
   private static double doubleValue;
   private static final AnimationState animationState = new AnimationState();
   private static final Class16 class16 = new Class16();
   private static WeakReference<World> weakReference = new WeakReference<>(null);
   private long longValue;

   public WardrobePreview() {
      EventManager.register(this);
   }

   @EventTarget
   public void method3143(WorldRenderEvent var1) {
      Wardrobe var2 = Wardrobe.method2488();
      if (var2 != null && mc.world != null && mc.player != null) {
         if (weakReference.get() != mc.world) {
            weakReference = new WeakReference<>(mc.world);
            items.clear();
         }

         long var3 = System.nanoTime();
         double var5 = this.longValue == 0L ? 0.0 : Math.min(0.25, (var3 - this.longValue) / 1.0E9);
         this.longValue = var3;
         float var7 = var1.getFloatValue();
         doubleValue += var5;
         float var8 = (float)(doubleValue % 100000.0);
         Vec3d var9 = mc.gameRenderer.getCamera().getCameraPos();
         ArrayList<WardrobePreview.Data> var10 = new ArrayList();
         HashSet var11 = new HashSet();

         for (AbstractClientPlayerEntity var13 : mc.world.getPlayers()) {
            boolean var14 = var13 == mc.player;
            if ((var14 ? var2.showSelf.isEnabled() : var2.showOthers.isEnabled())
               && !var13.isSpectator()
               && !var13.isInvisible()
               && !(var13.squaredDistanceTo(var9) > 4096.0)) {
               HoveredData var15 = var2.method2499(var2.method2498(var13.getGameProfile().name(), var14), FocusSlotEnum.PET);
               if (var15 != null) {
                  Class15 var16 = var2.getRegistry().method1427(var15);
                  if (var16 != null && var16.data3 != null) {
                     PetBehavior var17 = items.get(var13.getId());
                     if (var17 == null || var17.hoveredData != var15) {
                        var17 = new PetBehavior(var15, var16);
                        items.put(var13.getId(), var17);
                     }

                     var11.add(var13.getId());
                     var17.method7926(var13, var14 ? method3149() : null, var5, var8);
                     float var18 = var17.class3.method438();
                     Vec3d var19 = var17.class3.method435(var18);
                     float var20 = var17.class3.method436(var18);
                     float var21 = var2.petScale.getValue3() * var17.data3.getFloatValue();
                     float[] var22 = method3144(var17, var8, var18);
                     if (var17.isFlag4()) {
                        Vec3d var23 = var13.getLerpedPos(var7);
                        var20 = MathHelper.lerpAngleDegrees(var7, var13.lastBodyYaw, var13.bodyYaw);
                        Vec3d var24 = var17.method7934(var13, var23, var20);
                        float var25 = var17.method7927(var18);
                        Vec3d var26 = var17.getVec3d4();
                        if (var25 < 1.0F && var26 != null) {
                           float var27 = var25 * var25 * (3.0F - 2.0F * var25);
                           var19 = var26.lerp(var24, var27).add(0.0, Math.sin(Math.PI * var25) * 0.55, 0.0);
                        } else {
                           var19 = var24;
                        }

                        var21 *= MathHelper.lerp(var25, 1.0F, var17.method7935());
                        var17.method7945((float)var5, var22);
                        if (var14 && mc.options.getPerspective().isFirstPerson() && var25 > 0.5F) {
                           continue;
                        }
                     } else {
                        var17.method7945((float)var5, var22);
                     }

                     int var30 = WorldRenderer.getLightmapCoordinates(mc.world, BlockPos.ofFloored(var19.x, var19.y + 0.3, var19.z));
                     var10.add(new WardrobePreview.Data(var17, var19, var20, var17.class3.method437(var18), (float[])var22.clone(), var30, var21));
                  }
               }
            }
         }

         items.keySet().retainAll(var11);
         if (!var10.isEmpty()) {
            MatrixStack var28 = var1.getMatrixStack();
            Immediate var29 = mc.getBufferBuilders().getEntityVertexConsumers();
            OutlineVertexProvider.method1542(
               var29,
               var3x -> {
                  for (Object var5x_o : (Iterable<?>)(Object)(var10)) {
                     WardrobePreview.Data var5x = (WardrobePreview.Data)var5x_o;
                     var28.push();

                     try {
                        var28.translate(var5x.vec3d.x - var9.x, var5x.vec3d.y - var9.y, var5x.vec3d.z - var9.z);
                        method3145(var28, var5x.floatValue, var5x.floatValue3, var5x.petBehavior.getFloatValue3());
                        int var6 = Math.round(Math.max(0.1F, Math.min(1.0F, var5x.petBehavior.getAlpha())) * 255.0F);
                        method3146(
                           var5x.petBehavior.class15,
                           var5x.floatValues,
                           var28,
                           var3x,
                           var5x.intValue,
                           var5x.petBehavior.hoveredData.getRarity().isFlag(),
                           var6 << 24 | 16777215,
                           true
                        );
                     } finally {
                        var28.pop();
                     }
                  }
               }
            );
            var29.draw();
         }
      } else {
         items.clear();
      }
   }

   private static float[] method3144(PetBehavior var0, float var1, float var2) {
      float[] var3 = var0.class16.method2139(var0.class15, var0.animationState, var1, null);
      int var4 = var0.class15.method2031("head");
      if (var4 >= 0 && !var0.isFlag3()) {
         var3[var4 * 9 + 1] = var3[var4 * 9 + 1] + (float)Math.toRadians(-var0.class3.method437(var2));
      }

      return var3;
   }

   private static void method3145(MatrixStack var0, float var1, float var2, float var3) {
      var0.multiply(new Quaternionf().rotationY((float)Math.toRadians(180.0 - var1)));
      float var4 = 1.0F + var3 * 0.18F;
      var0.scale(var2 * var4, var2 / var4, var2 * var4);
      var0.scale(-1.0F, -1.0F, 1.0F);
      var0.translate(0.0F, -1.501F, 0.0F);
   }

   private static void method3146(Class15 var0, float[] var1, MatrixStack var2, OrderedRenderCommandQueue var3, int var4, boolean var5, int var6, boolean var7) {
      BoneMatrices.method4787(var0, var1, 6, var2, var3, var4, OverlayTexture.DEFAULT_UV, var7, var5, var6);
   }

   public static void method3147(HoveredData var0, Class15 var1, MatrixStack var2, OrderedRenderCommandQueue var3, int var4, float var5) {
      if (var1 != null) {
         animationState.method6539(var1, "idle", var5);
         float[] var6 = class16.method2139(var1, animationState, var5, null);
         method3146(var1, var6, var2, var3, var4, var0.getRarity().isFlag(), -1, true);
      }
   }

   public static Vec3d method3148(PetBehavior var0, float[] var1, int var2, float var3, float var4, float var5) {
      float var6 = var0.class3.method438();
      Vec3d var7 = var0.class3.method435(var6);
      float var8 = Wardrobe.method2488() == null ? 1.0F : Wardrobe.method2488().petScale.getValue3() * var0.data3.getFloatValue();
      matrix4f.identity();
      matrix4f.translate((float)var7.x, (float)var7.y, (float)var7.z);
      matrix4f.rotateY((float)Math.toRadians(180.0 - var0.class3.method436(var6)));
      matrix4f.scale(var8);
      matrix4f.scale(-1.0F, -1.0F, 1.0F);
      matrix4f.translate(0.0F, -1.501F, 0.0F);
      if (var2 >= 0) {
         BoneMatrices.method4808(var0.class15, var1, var2, matrix4f);
      }

      matrix4f.transformPosition(var3 / 16.0F, var4 / 16.0F, var5 / 16.0F, vector3f);
      return new Vec3d(vector3f.x, vector3f.y, vector3f.z);
   }

   private static LivingEntity method3149() {
      try {
         LivingEntity var0 = AuraModule.INSTANCE.method6358();
         return var0 == null ? AimAssistModule.INSTANCE.method5156() : var0;
      } catch (Throwable var1) {
         return null;
      }
   }

   public static void clear() {
      items.clear();
   }

   private static final class Data {
      private final PetBehavior petBehavior;
      private final Vec3d vec3d;
      private final float floatValue;
      private final float floatValue2;
      private final float[] floatValues;
      private final int intValue;
      private final float floatValue3;

      public PetBehavior petBehavior() {
         return this.petBehavior;
      }

      public Vec3d vec3d() {
         return this.vec3d;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float[] floatValues() {
         return this.floatValues;
      }

      public int intValue() {
         return this.intValue;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.petBehavior, other.petBehavior) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.floatValue3, other.floatValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(petBehavior, vec3d, floatValue, floatValue2, floatValues, intValue, floatValue3);
      }

      @Override
      public String toString() {
         return "Data[" + "petBehavior=" + this.petBehavior + ", " + "vec3d=" + this.vec3d + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValues=" + this.floatValues + ", " + "intValue=" + this.intValue + ", " + "floatValue3=" + this.floatValue3 + "]";
      }

      private Data(PetBehavior var1, Vec3d var2, float var3, float var4, float[] var5, int var6, float var7) {
         this.petBehavior = var1;
         this.vec3d = var2;
         this.floatValue = var3;
         this.floatValue2 = var4;
         this.floatValues = var5;
         this.intValue = var6;
         this.floatValue3 = var7;
      }

      public PetBehavior getPetBehavior() {
         return this.petBehavior;
      }

      public Vec3d getVec3d() {
         return this.vec3d;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }
   }
}

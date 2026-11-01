package org.zenith.utility.render;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.zenith.utility.api.Wardrobe;
import org.zenith.utility.misc.AnimationCache;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.WardrobeSetInfo;

public final class Util19 {
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private static final Random random = Random.create();
   private static final Vector3f vector3f = new Vector3f();
   private static final Matrix4f matrix4f = new Matrix4f();
   private static float floatValue;
   private static long longValue;

   private Util19() {
   }

   public static boolean enabled() {
      Wardrobe var0 = Wardrobe.method2488();
      return var0 != null
         && var0.particles.isEnabled()
         && !var0.isFlag()
         && mc.world != null
         && mc.options != null
         && mc.options.getParticles().getValue().ordinal() < 2;
   }

   public static boolean method5042() {
      Wardrobe var0 = Wardrobe.method2488();
      long var1 = System.nanoTime();
      float var3 = var0 == null ? 60.0F : var0.particleBudget.getValue3();
      if (longValue == 0L) {
         longValue = var1;
         floatValue = var3 * 0.25F;
      }

      floatValue = Math.min(var3 * 0.5F, floatValue + (float)(var1 - longValue) / 1.0E9F * var3);
      longValue = var1;
      if (floatValue < 1.0F) {
         return false;
      } else {
         floatValue--;
         return true;
      }
   }

   public static void method5043(ParticleEffect var0, double var1, double var3, double var5, double var7, double var9, double var11) {
      if (mc.world != null && var0 != null && method5042()) {
         mc.world.addParticleClient(var0, var1, var3, var5, var7, var9, var11);
      }
   }

   public static void method5044(
      Class15 var0,
      AnimationCache.Entry var1,
      PlayerEntityRenderState var2,
      PlayerEntityModel var3,
      float[] var4,
      float var5,
      boolean var6,
      boolean var7,
      WardrobeSetInfo var8,
      List<Class15.Data4> var9
   ) {
      if (!var9.isEmpty() && enabled() && !(var2.squaredDistanceToCamera > 2304.0)) {
         float[] var10 = var1.method7625(var9.size());

         for (int var11 = 0; var11 < var9.size(); var11++) {
            Class15.Data4 var12 = (Class15.Data4)var9.get(var11);
            if (!method5046(var12.getType3(), var6, var7, var2.isInSneakingPose, var8 != null)) {
               var10[var11] = 0.0F;
            } else {
               var10[var11] += var12.getFloatValue4() * var5;

               while (var10[var11] >= 1.0F) {
                  var10[var11]--;
                  matrix4f.identity();
                  matrix4f.translate((float)var2.x, (float)var2.y, (float)var2.z);
                  matrix4f.rotateY((float)Math.toRadians(180.0 - var2.bodyYaw));
                  matrix4f.scale(var2.baseScale);
                  matrix4f.scale(-1.0F, -1.0F, 1.0F);
                  matrix4f.translate(0.0F, -1.501F, 0.0F);
                  if (var12.getIntValue() >= 0) {
                     int var13 = var12.getIntValue();

                     while (var0.intValues[var13] >= 0) {
                        var13 = var0.intValues[var13];
                     }

                     ModelPart var14 = method5047(var3, var0.intValues2[var13]);
                     if (var14 != null) {
                        method5048(var3.getRootPart(), matrix4f);
                        method5048(var14, matrix4f);
                     }

                     BoneMatrices.method4808(var0, var4, var12.getIntValue(), matrix4f);
                  }

                  matrix4f.transformPosition(var12.getFloatValue() / 16.0F, var12.getFloatValue2() / 16.0F, var12.getFloatValue3() / 16.0F, vector3f);
                  double var15 = var12.getFloatValue8();
                  method5043(
                     var12.getParticleEffect(),
                     vector3f.x + (random.nextDouble() - 0.5) * var15,
                     vector3f.y + (random.nextDouble() - 0.5) * var15,
                     vector3f.z + (random.nextDouble() - 0.5) * var15,
                     var12.getFloatValue5() + (random.nextDouble() - 0.5) * var15 * 0.2,
                     var12.getFloatValue6(),
                     var12.getFloatValue7() + (random.nextDouble() - 0.5) * var15 * 0.2
                  );
               }
            }
         }
      }
   }

   public static void method5045(WardrobeSetInfo var0, PlayerEntityRenderState var1, AnimationCache.Entry var2, float var3, ParticleEffect var4) {
      if (var0 != null && var4 != null && enabled() && !(var1.squaredDistanceToCamera > 2304.0)) {
         var2.floatValue += 5.0F * var3;

         while (var2.floatValue >= 1.0F) {
            var2.floatValue--;
            double var5 = random.nextDouble() * Math.PI * 2.0;
            double var7 = 0.55 + random.nextDouble() * 0.15;
            method5043(var4, var1.x + Math.cos(var5) * var7, var1.y + 0.1 + random.nextDouble() * 1.7, var1.z + Math.sin(var5) * var7, 0.0, 0.015, 0.0);
         }
      }
   }

   private static boolean method5046(Class15.Type3 var0, boolean var1, boolean var2, boolean var3, boolean var4) {
      return switch (var0) {
         case ALWAYS -> true;
         case MOVING -> var1;
         case IDLE -> !var1;
         case AIRBORNE -> var2;
         case SNEAKING -> var3;
         case SET_BONUS -> var4;
      };
   }

   public static ModelPart method5047(PlayerEntityModel var0, int var1) {
      return switch (var1) {
         case 0 -> var0.head;
         case 1 -> var0.body;
         case 2 -> var0.rightArm;
         case 3 -> var0.leftArm;
         case 4 -> var0.rightLeg;
         case 5 -> var0.leftLeg;
         default -> null;
      };
   }

   private static void method5048(ModelPart var0, Matrix4f var1) {
      var1.translate(var0.originX / 16.0F, var0.originY / 16.0F, var0.originZ / 16.0F);
      if (var0.pitch != 0.0F || var0.yaw != 0.0F || var0.roll != 0.0F) {
         var1.rotateZYX(var0.roll, var0.yaw, var0.pitch);
      }

      if (var0.xScale != 1.0F || var0.yScale != 1.0F || var0.zScale != 1.0F) {
         var1.scale(var0.xScale, var0.yScale, var0.zScale);
      }
   }
}

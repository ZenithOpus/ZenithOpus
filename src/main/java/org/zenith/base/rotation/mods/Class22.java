package org.zenith.base.rotation.mods;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.Rotation;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.api.IntentLibraryRt;
import org.zenith.utility.api.IntentSelector;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.ReplayState;
import org.zenith.utility.misc.Util22;
import org.zenith.utility.misc.Vec2f;

public final class Class22 extends Util22 {
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final float CONST_floatValue = 0.21217355F;
   private final ReplayState replayState = method3519();
   private int intValue = Integer.MIN_VALUE;
   private boolean flag = false;
   private float floatValue = 0.0F;
   private float floatValue2 = 0.0F;
   private boolean flag2 = false;
   private float floatValue3 = 0.0F;
   private float floatValue4 = 0.0F;
   private Vec3d vec3d = null;
   private Box box = null;

   private static ReplayState method3519() {
      IntentSelector var0 = IntentSelector.method7321();
      IntentLibraryRt var1 = IntentLibraryRt.method1382();
      if (var0 != null && var1 != null) {
         return new ReplayState(var0, var1);
      } else {
         System.err.println("[MotorIntent] artifacts not found; TestRotationModeV2 disabled");
         return null;
      }
   }

   public Rotation method3520(Rotation var1) {
      Rotation var2 = rotationManager.method7844();
      if (this.replayState == null) {
         return var2;
      } else if (var1 != null && !var1.method3137()) {
         LivingEntity var3 = this.method3527();
         if (var3 != null && var3.isAlive()) {
            Box var4 = var3.getBoundingBox();
            if (var4 == null) {
               this.method3532();
               return var2;
            } else {
               int var5 = var3.getId();
               if (this.intValue != var5) {
                  this.method3532();
                  this.intValue = var5;
               }

               float var6 = var2.getFloatValue();
               float var7 = var2.getFloatValue2();
               if (!this.flag) {
                  this.floatValue = var6;
                  this.floatValue2 = var7;
                  this.flag = true;
                  return var2;
               } else {
                  float var8 = MathHelper.wrapDegrees(var6 - this.floatValue);
                  float var9 = var7 - this.floatValue2;
                  int var10 = Math.round(var8 / 0.21217355F);
                  int var11 = Math.round(var9 / 0.21217355F);
                  this.floatValue = var6;
                  this.floatValue2 = var7;
                  Vec3d var12 = mc.player.getEyePos();
                  Vec3d var13 = mc.player.getEntityPos();
                  Vec3d var14 = this.method3529(var3, var4);
                  Vec3d var15 = this.method3528(var13);
                  ReplayState.Entry var16 = this.method3522(var2, var12, var3, var4, var14, var15, var10, var11);
                  this.vec3d = var13;
                  this.box = var4;
                  if (var16 == null) {
                     this.flag2 = false;
                     this.replayState.reset();
                     return var2;
                  } else {
                     this.flag2 = true;
                     this.floatValue3 = (float)var16.doubleValue;
                     this.floatValue4 = (float)var16.doubleValue2;
                     int[] var17 = this.replayState.method7983(var16);
                     return var17[0] == 0 && var17[1] == 0 ? var2 : var2.method3140(var17[0] * 0.21217355F, var17[1] * 0.21217355F);
                  }
               }
            }
         } else {
            mc.player.setYaw(rotationManager.method7844().getFloatValue());
            mc.player.setPitch(rotationManager.method7844().getFloatValue2());
            this.method3532();
            return var2;
         }
      } else {
         this.method3532();
         return var2;
      }
   }

   public String method3521() {
      return this.replayState == null ? "unavailable" : this.replayState.getText();
   }

   private ReplayState.Entry method3522(Rotation var1, Vec3d var2, LivingEntity var3, Box var4, Vec3d var5, Vec3d var6, int var7, int var8) {
      Vec3d var9 = this.method3530(var4);
      Vec2f var10 = var1.method3131(this.method3524(var2, var9));
      float var11 = var10.getFloatValue();
      float var12 = var10.getFloatValue2();
      if (this.isFinite(var11) && this.isFinite(var12)) {
         float var13 = Float.MAX_VALUE;
         float var14 = -Float.MAX_VALUE;
         float var15 = Float.MAX_VALUE;
         float var16 = -Float.MAX_VALUE;

         for (Vec3d var20 : this.method3523(var4)) {
            Vec2f var21 = var1.method3131(this.method3524(var2, var20));
            float var22 = var21.getFloatValue();
            float var23 = var21.getFloatValue2();
            if (this.isFinite(var22) && this.isFinite(var23)) {
               var22 = this.method3531(var22, var11);
               var13 = Math.min(var13, var22);
               var14 = Math.max(var14, var22);
               var15 = Math.min(var15, var23);
               var16 = Math.max(var16, var23);
            }
         }

         if (var13 == Float.MAX_VALUE) {
            var13 = var11;
            var14 = var11;
            var15 = var12;
            var16 = var12;
         }

         Vec3d var26 = this.method3525(var1, var2);
         Box var27 = this.method3526(var3, var4, var5);
         Vec2f var28 = var1.method3131(this.method3524(var26, this.method3530(var27)));
         float var29 = var28.getFloatValue();
         float var30 = var28.getFloatValue2();
         if (!this.isFinite(var29) || !this.isFinite(var30)) {
            var29 = var11;
            var30 = var12;
         }

         float var32 = var11 / 0.21217355F;
         float var33 = var12 / 0.21217355F;
         int var24 = var13 <= 0.0F && var14 >= 0.0F && var15 <= 0.0F && var16 >= 0.0F ? 1 : 0;
         ReplayState.Entry var25 = new ReplayState.Entry();
         var25.doubleValue = var32;
         var25.doubleValue2 = var33;
         var25.doubleValue3 = var13 / 0.21217355F;
         var25.doubleValue4 = var14 / 0.21217355F;
         var25.doubleValue5 = var15 / 0.21217355F;
         var25.doubleValue6 = var16 / 0.21217355F;
         if (this.flag2) {
            var25.doubleValue7 = var32 - this.floatValue3;
            var25.doubleValue8 = var33 - this.floatValue4;
            var25.doubleValue9 = var32 - (this.floatValue3 - var7);
            var25.doubleValue10 = var33 - (this.floatValue4 - var8);
         }

         var25.doubleValue11 = var29 / 0.21217355F;
         var25.doubleValue12 = var30 / 0.21217355F;
         var25.doubleValue13 = var24 != 0 ? 1.0 : 0.0;
         var25.doubleValue14 = var2.distanceTo(var9);
         var25.doubleValue15 = var5.x;
         var25.doubleValue16 = var5.y;
         var25.doubleValue17 = var5.z;
         var25.doubleValue18 = var6.x;
         var25.doubleValue19 = var6.y;
         var25.doubleValue20 = var6.z;
         return var25;
      } else {
         return null;
      }
   }

   private Vec3d[] method3523(Box var1) {
      return new Vec3d[]{
         new Vec3d(var1.minX, var1.minY, var1.minZ),
         new Vec3d(var1.minX, var1.minY, var1.maxZ),
         new Vec3d(var1.minX, var1.maxY, var1.minZ),
         new Vec3d(var1.minX, var1.maxY, var1.maxZ),
         new Vec3d(var1.maxX, var1.minY, var1.minZ),
         new Vec3d(var1.maxX, var1.minY, var1.maxZ),
         new Vec3d(var1.maxX, var1.maxY, var1.minZ),
         new Vec3d(var1.maxX, var1.maxY, var1.maxZ)
      };
   }

   private Rotation method3524(Vec3d var1, Vec3d var2) {
      return RotationUtils.method6912(var2.subtract(var1));
   }

   private Vec3d method3525(Rotation var1, Vec3d var2) {
      Vec3d var3 = mc.player.getEntityPos();
      Vec3d var4 = var2.subtract(var3);

      try {
         PlayerSimulation var5 = PlayerSimulation.method6033(PlayerSimulation.Entry.method6092(mc.player.input.playerInput));
         var5.floatValue = var1.getFloatValue();
         var5.floatValue2 = var1.getFloatValue2();
         var5.tick();
         var5.tick();
         Vec3d var6 = var5.vec3d.add(var4);
         return this.method3533(var6) ? var6 : var2;
      } catch (Throwable var7) {
         return var2;
      }
   }

   private Box method3526(LivingEntity var1, Box var2, Vec3d var3) {
      if (var1 instanceof PlayerEntity var4) {
         try {
            PlayerSimulation var5 = PlayerSimulation.method6034(var4, PlayerSimulation.Entry.method6093(var4));
            var5.tick();
            var5.tick();
            if (var5.box != null && this.method3533(var5.vec3d)) {
               return var5.box;
            }
         } catch (Throwable var6) {
         }
      }

      Vec3d var7 = var3 == null ? Vec3d.ZERO : var3.multiply(2.0);
      return this.method3533(var7) ? var2.offset(var7) : var2;
   }

   private LivingEntity method3527() {
      LivingEntity var1 = AuraModule.INSTANCE.method6358();
      return var1 != null && var1.isAlive() ? var1 : null;
   }

   private Vec3d method3528(Vec3d var1) {
      if (this.vec3d != null && var1 != null) {
         Vec3d var2 = var1.subtract(this.vec3d);
         if (this.method3533(var2) && var2.lengthSquared() > 1.0E-10) {
            return var2;
         }
      }

      Vec3d var3 = mc.player.getVelocity();
      return this.method3533(var3) ? var3 : Vec3d.ZERO;
   }

   private Vec3d method3529(LivingEntity var1, Box var2) {
      if (this.box != null && var2 != null) {
         Vec3d var3 = this.method3530(var2).subtract(this.method3530(this.box));
         if (this.method3533(var3) && var3.lengthSquared() > 1.0E-10) {
            return var3;
         }
      }

      Vec3d var4 = var1 == null ? Vec3d.ZERO : var1.getVelocity();
      return this.method3533(var4) ? var4 : Vec3d.ZERO;
   }

   private Vec3d method3530(Box var1) {
      return new Vec3d((var1.minX + var1.maxX) * 0.5, (var1.minY + var1.maxY) * 0.5, (var1.minZ + var1.maxZ) * 0.5);
   }

   private float method3531(float var1, float var2) {
      float var3 = var1 - var2;
      return !Float.isFinite(var3) ? var1 : var2 + MathHelper.wrapDegrees(var3);
   }

   private void method3532() {
      this.intValue = Integer.MIN_VALUE;
      this.flag = false;
      this.floatValue = 0.0F;
      this.floatValue2 = 0.0F;
      this.flag2 = false;
      this.floatValue3 = 0.0F;
      this.floatValue4 = 0.0F;
      this.vec3d = null;
      this.box = null;
      if (this.replayState != null) {
         this.replayState.reset();
      }
   }

   private boolean isFinite(float var1) {
      return !Float.isNaN(var1) && !Float.isInfinite(var1);
   }

   private boolean method3533(Vec3d var1) {
      return var1 != null && this.isFinite((float)var1.x) && this.isFinite((float)var1.y) && this.isFinite((float)var1.z);
   }
}

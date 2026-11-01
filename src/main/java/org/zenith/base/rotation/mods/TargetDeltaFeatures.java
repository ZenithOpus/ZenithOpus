package org.zenith.base.rotation.mods;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.Rotation;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.api.AimPolicy;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.Class36;
import org.zenith.utility.misc.Class6;
import org.zenith.utility.misc.Resettable;
import org.zenith.utility.misc.Util22;
import org.zenith.utility.misc.Vec2f;

public final class TargetDeltaFeatures extends Util22 {
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final int CONST_intValue2 = 59;
   private static final int CONST_intValue3 = 0;
   private static final int CONST_intValue4 = 1;
   private static final int CONST_intValue5 = 16;
   private static final float CONST_floatValue = 50.0F;
   private static final float CONST_floatValue2 = 176.0F;
   private static final float CONST_floatValue3 = 8.0F;
   private static final float CONST_floatValue4 = 8.0F;
   private static final String[] texts = new String[]{
      "target_center_delta_yaw_count_units",
      "target_center_delta_pitch_count_units",
      "target_box_min_delta_yaw_count_units",
      "target_box_max_delta_yaw_count_units",
      "target_box_min_delta_pitch_count_units",
      "target_box_max_delta_pitch_count_units",
      "target_box_width_yaw_count_units",
      "target_box_height_pitch_count_units",
      "target_center_yaw_by_box_width",
      "target_center_pitch_by_box_height",
      "target_center_angular_distance_count_units",
      "target_center_direction_sin",
      "target_center_direction_cos",
      "aim_x_norm_in_target_box",
      "aim_y_norm_in_target_box",
      "aim_inside_target_box",
      "target_center_distance",
      "target_center_horizontal_distance",
      "target_center_vertical_distance_abs",
      "aim_inside_target_box_ticks_clamped",
      "aim_outside_target_box_ticks",
      "ticks_since_entered_target_box",
      "ticks_since_left_target_box",
      "target_center_delta_yaw_change_count_units",
      "target_center_delta_pitch_change_count_units",
      "target_center_distance_change",
      "yaw_target_drift_count_units",
      "pitch_target_drift_count_units",
      "sim_2t_target_center_delta_yaw_count_units",
      "sim_2t_target_center_delta_pitch_count_units",
      "target_motion_x",
      "target_motion_y",
      "target_motion_z",
      "yaw_mouse_count",
      "pitch_mouse_count",
      "yaw_velocity_mouse_counts",
      "pitch_velocity_mouse_counts",
      "yaw_acceleration_mouse_counts",
      "pitch_acceleration_mouse_counts",
      "yaw_jerk_mouse_counts",
      "pitch_jerk_mouse_counts",
      "mouse_dx_nonzero_streak_ticks",
      "mouse_dy_nonzero_streak_ticks",
      "yaw_sign_flip_ticks_ago",
      "pitch_sign_flip_ticks_ago",
      "yaw_mouse_static_help",
      "pitch_mouse_static_help",
      "yaw_current_to_peak_abs_ratio",
      "pitch_current_to_peak_abs_ratio",
      "yaw_peak_mouse_count_abs",
      "pitch_peak_mouse_count_abs",
      "movement_input_forward",
      "movement_input_backward",
      "movement_input_left",
      "movement_input_right",
      "movement_input_jump",
      "player_motion_x",
      "player_motion_y",
      "player_motion_z"
   };
   private int intValue = Integer.MIN_VALUE;
   private boolean flag = false;
   private float floatValue = 0.0F;
   private float floatValue2 = 0.0F;
   private float floatValue3 = 0.0F;
   private float floatValue4 = 0.0F;
   private float floatValue5 = 0.0F;
   private float floatValue6 = 0.0F;
   private boolean flag2 = false;
   private float floatValue7 = 0.0F;
   private float floatValue8 = 0.0F;
   private float floatValue9 = 0.0F;
   private float floatValue10 = 0.0F;
   private float floatValue11 = 0.0F;
   private float floatValue12 = 0.0F;
   private int intValue2 = 0;
   private int intValue3 = 0;
   private float floatValue13 = 0.0F;
   private float floatValue14 = 0.0F;
   private float floatValue15 = 0.0F;
   private float floatValue16 = 0.0F;
   private TargetDeltaFeatures.Entry entry = null;
   private float[] floatValues = null;
   private final Resettable resettable = method3840();
   public float offset;

   private static Resettable method3840() {
      AimPolicy var0 = AimPolicy.method5432();
      if (var0 != null) {
         return new Class36(var0);
      } else {
         System.err.println("[AimPolicy] artifact not found; TestRotationMode disabled");
         return null;
      }
   }

   public Rotation method3841(Class6 var1, Rotation var2) {
      return this.method3842(var1, var2, this.method3852());
   }

   private Rotation method3842(Class6 var1, Rotation var2, LivingEntity var3) {
      Rotation var4 = rotationManager.method7844();
      if (this.resettable == null) {
         return var4;
      } else if (var2 == null || var2.method3137()) {
         this.method3864();
         return var4;
      } else if (var3 == null) {
         return RotationUtils.method6911();
      } else if (var3 != null && var3.isAlive()) {
         Box var5 = var3.getBoundingBox();
         if (var5 == null) {
            this.method3864();
            return var4;
         } else {
            int var6 = var3.getId();
            if (this.intValue != var6) {
               this.method3864();
               this.intValue = var6;
            }

            float var7 = var4.getFloatValue();
            float var8 = var4.getFloatValue2();
            if (!this.flag) {
               this.floatValue = var7;
               this.floatValue2 = var8;
               this.flag = true;
               return var4;
            } else {
               float var9 = MathHelper.wrapDegrees(var7 - this.floatValue);
               float var10 = var8 - this.floatValue2;
               Vec3d var11 = mc.player.getEntityPos();
               Vec3d var12 = this.method3854(var3, var5);
               Vec3d var13 = this.method3853(var11);
               float var14 = 0.21217355F;
               if (!this.isFinite(var14)) {
                  this.floatValues = null;
                  this.floatValue = var7;
                  this.floatValue2 = var8;
                  return var4;
               } else {
                  float[] var15 = this.method3848(var4, mc.player.getEyePos(), var3, var5, var9, var10, var12, var13, var14);
                  this.floatValue = var7;
                  this.floatValue2 = var8;
                  if (var15 != null && var15.length == 59 && this.method3865(var15)) {
                     this.floatValues = (float[])var15.clone();
                     this.entry = new TargetDeltaFeatures.Entry(var15, var11, var5, var14);
                     int[] var16 = this.resettable.method2865(var15);
                     if (mc.player.ticksSinceLastAttack < 6) {
                        this.offset = MathHelper.clamp(this.offset - 0.3F, 0.0F, 1.0F);
                     } else {
                        this.offset = MathHelper.clamp(this.offset + 1.0F, 0.0F, 1.0F);
                     }

                     int[] var17 = new int[]{(int)(this.method3843(var16[0], var15[0], 1.0F, 0.9F) * this.offset), var16[1]};
                     return var4.method3140(var17[0] * var14, var17[1] * var14);
                  } else {
                     this.floatValues = null;
                     this.resettable.reset();
                     return var4;
                  }
               }
            }
         }
      } else {
         this.method3864();
         return var4;
      }
   }

   private double method3843(double var1, float var3, float var4, float var5) {
      boolean var6 = Math.signum(var1) == Math.signum(var3);
      float var7 = var6 ? var4 : var5;
      return var1 * var7;
   }

   public String method3844() {
      return this.resettable == null ? "unavailable" : this.resettable.method2866();
   }

   public float[] method3845() {
      return this.floatValues == null ? null : (float[])this.floatValues.clone();
   }

   public String[] method3846() {
      return (String[])texts.clone();
   }

   public void method3847() {
      this.method3864();
   }

   private float[] method3848(Rotation var1, Vec3d var2, LivingEntity var3, Box var4, float var5, float var6, Vec3d var7, Vec3d var8, float var9) {
      Vec3d[] var10 = new Vec3d[]{
         new Vec3d(var4.minX, var4.minY, var4.minZ),
         new Vec3d(var4.minX, var4.minY, var4.maxZ),
         new Vec3d(var4.minX, var4.maxY, var4.minZ),
         new Vec3d(var4.minX, var4.maxY, var4.maxZ),
         new Vec3d(var4.maxX, var4.minY, var4.minZ),
         new Vec3d(var4.maxX, var4.minY, var4.maxZ),
         new Vec3d(var4.maxX, var4.maxY, var4.minZ),
         new Vec3d(var4.maxX, var4.maxY, var4.maxZ)
      };
      Vec3d var11 = this.method3855(var4);
      Rotation var12 = this.method3849(var2, var11);
      Vec2f var13 = var1.method3131(var12);
      float var14 = var13.getFloatValue();
      float var15 = var13.getFloatValue2();
      if (this.isFinite(var14) && this.isFinite(var15)) {
         Vec3d var16 = this.method3850(var1, var2);
         Box var17 = this.method3851(var3, var4, var7);
         Vec2f var18 = var1.method3131(this.method3849(var16, this.method3855(var17)));
         float var19 = var18.getFloatValue();
         float var20 = var18.getFloatValue2();
         if (this.isFinite(var19) && this.isFinite(var20)) {
            float var21 = Float.MAX_VALUE;
            float var22 = -Float.MAX_VALUE;
            float var23 = Float.MAX_VALUE;
            float var24 = -Float.MAX_VALUE;

            for (Vec3d var28 : var10) {
               Rotation var29 = this.method3849(var2, var28);
               Vec2f var30 = var1.method3131(var29);
               float var31 = var30.getFloatValue();
               float var32 = var30.getFloatValue2();
               if (this.isFinite(var31) && this.isFinite(var32)) {
                  var31 = this.method3863(var31, var14);
                  var21 = Math.min(var21, var31);
                  var22 = Math.max(var22, var31);
                  var23 = Math.min(var23, var32);
                  var24 = Math.max(var24, var32);
               }
            }

            if (var21 == Float.MAX_VALUE) {
               var21 = var14;
               var22 = var14;
               var23 = var15;
               var24 = var15;
            }

            var14 = this.method3863(var14, var14);
            float var74 = var22 - var21;
            float var75 = var24 - var23;
            float var76 = (float)(var11.x - var2.x);
            float var77 = Math.abs((float)(var11.y - var2.y));
            float var78 = (float)(var11.z - var2.z);
            float var79 = (float)var2.distanceTo(var11);
            float var81 = this.hypot(var76, var78);
            boolean var82 = var21 <= 0.0F && var22 >= 0.0F;
            boolean var33 = var23 <= 0.0F && var24 >= 0.0F;
            float var34 = 0.0F;
            if (var82 && Math.abs(var74) > 1.0E-6F) {
               var34 = MathHelper.clamp((0.0F - var21) / var74, 0.0F, 1.0F);
            }

            float var35 = 0.0F;
            if (var33 && Math.abs(var75) > 1.0E-6F) {
               var35 = MathHelper.clamp((0.0F - var23) / var75, 0.0F, 1.0F);
            }

            boolean var36 = var82 && var33;
            this.floatValue7 = var36 ? MathHelper.clamp(this.floatValue7 + 1.0F, 0.0F, 50.0F) : 0.0F;
            this.floatValue8 = var36 ? 0.0F : Math.min(this.floatValue8 + 1.0F, 8.0F);
            float var39 = var5 - this.floatValue3;
            float var40 = var6 - this.floatValue4;
            float var41 = var39 - this.floatValue5;
            float var42 = var40 - this.floatValue6;
            float var43 = this.entry != null ? this.method3858(this.entry.floatValues[0], this.entry.floatValue) : var14;
            float var44 = this.entry != null ? this.method3858(this.entry.floatValues[1], this.entry.floatValue) : var15;
            float var45 = this.entry != null ? this.entry.floatValues[16] : var79;
            float var46 = MathHelper.wrapDegrees(var14 - var43);
            float var47 = var15 - var44;
            float var48 = var79 - var45;
            float var49 = var14 / Math.max(Math.abs(var74), 0.1F);
            float var50 = var15 / Math.max(Math.abs(var75), 0.1F);
            float var51 = (float)Math.sqrt(var14 * var14 + var15 * var15);
            float var52 = (float)Math.atan2(var15, var14);
            float var53 = (float)Math.sin(var52);
            float var54 = (float)Math.cos(var52);
            float var55 = this.method3857(var5, var9);
            float var56 = this.method3857(var6, var9);
            float var57 = this.method3856(var14, var9);
            float var58 = this.method3856(var15, var9);
            float var59 = 0.0F;
            float var60 = 0.0F;
            float var61 = 0.0F;
            float var62 = 0.0F;
            if (this.entry != null) {
               float var63 = this.entry.floatValues[0];
               float var64 = this.entry.floatValues[1];
               float var65 = var63 - var55;
               float var66 = var64 - var56;
               var59 = Math.abs(var63) - Math.abs(var65);
               var60 = Math.abs(var64) - Math.abs(var66);
               var61 = var57 - var65;
               var62 = var58 - var66;
            }

            this.floatValue11 = var55 != 0.0F ? this.floatValue11 + 1.0F : 0.0F;
            this.floatValue12 = var56 != 0.0F ? this.floatValue12 + 1.0F : 0.0F;
            int var83 = this.method3859(var55);
            int var84 = this.method3859(var56);
            this.floatValue15 = this.method3861(var55, var83, this.intValue2, this.floatValue15);
            this.floatValue16 = this.method3861(var56, var84, this.intValue3, this.floatValue16);
            float var85 = this.method3862(var55, this.floatValue15);
            float var86 = this.method3862(var56, this.floatValue16);
            this.floatValue13 = this.method3860(var83, this.intValue2, this.floatValue13);
            this.floatValue14 = this.method3860(var84, this.intValue3, this.floatValue14);
            this.intValue2 = var83 != 0 ? var83 : this.intValue2;
            this.intValue3 = var84 != 0 ? var84 : this.intValue3;
            float var67 = mc.player.input.playerInput.forward() ? 1.0F : 0.0F;
            float var68 = mc.player.input.playerInput.backward() ? 1.0F : 0.0F;
            float var69 = mc.player.input.playerInput.left() ? 1.0F : 0.0F;
            float var70 = mc.player.input.playerInput.right() ? 1.0F : 0.0F;
            float var71 = mc.player.input.playerInput.jump() ? 1.0F : 0.0F;
            if (var36) {
               this.floatValue9 = MathHelper.clamp(this.flag2 ? this.floatValue9 + 1.0F : 1.0F, 0.0F, 176.0F);
               this.floatValue10 = 0.0F;
            } else {
               this.floatValue9 = 0.0F;
               this.floatValue10 = MathHelper.clamp(this.flag2 ? 1.0F : this.floatValue10 + 1.0F, 0.0F, 8.0F);
            }

            float[] var72 = new float[]{
               var57,
               var58,
               this.method3856(var21, var9),
               this.method3856(var22, var9),
               this.method3856(var23, var9),
               this.method3856(var24, var9),
               this.method3856(var74, var9),
               this.method3856(var75, var9),
               var49,
               var50,
               this.method3856(var51, var9),
               var53,
               var54,
               var34,
               var35,
               var36 ? 1.0F : 0.0F,
               var79,
               var81,
               var77,
               this.floatValue7,
               this.floatValue8,
               this.floatValue9,
               this.floatValue10,
               this.method3856(var46, var9),
               this.method3856(var47, var9),
               var48,
               var61,
               var62,
               this.method3856(var19, var9),
               this.method3856(var20, var9),
               (float)var7.x,
               (float)var7.y,
               (float)var7.z,
               var55,
               var56,
               var55,
               var56,
               this.method3857(var39, var9),
               this.method3857(var40, var9),
               this.method3857(var41, var9),
               this.method3857(var42, var9),
               this.floatValue11,
               this.floatValue12,
               this.floatValue13,
               this.floatValue14,
               var59,
               var60,
               var85,
               var86,
               this.floatValue15,
               this.floatValue16,
               var67,
               var68,
               var69,
               var70,
               var71,
               (float)var8.x,
               (float)var8.y,
               (float)var8.z
            };
            if (!this.method3865(var72)) {
               return null;
            } else {
               this.floatValue3 = var5;
               this.floatValue4 = var6;
               this.floatValue5 = var39;
               this.floatValue6 = var40;
               this.flag2 = var36;
               return var72;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private Rotation method3849(Vec3d var1, Vec3d var2) {
      return RotationUtils.method6912(var2.subtract(var1));
   }

   private Vec3d method3850(Rotation var1, Vec3d var2) {
      Vec3d var3 = mc.player.getEntityPos();
      Vec3d var4 = var2.subtract(var3);

      try {
         PlayerSimulation var5 = PlayerSimulation.method6033(PlayerSimulation.Entry.method6092(mc.player.input.playerInput));
         var5.floatValue = var1.getFloatValue();
         var5.floatValue2 = var1.getFloatValue2();
         var5.tick();
         var5.tick();
         Vec3d var6 = var5.vec3d.add(var4);
         return this.method3866(var6) ? var6 : var2;
      } catch (Throwable var7) {
         return var2;
      }
   }

   private Box method3851(LivingEntity var1, Box var2, Vec3d var3) {
      if (var1 instanceof PlayerEntity var4) {
         try {
            PlayerSimulation var5 = PlayerSimulation.method6034(var4, PlayerSimulation.Entry.method6093(var4));
            var5.tick();
            var5.tick();
            if (var5.box != null && this.method3866(var5.vec3d)) {
               return var5.box;
            }
         } catch (Throwable var6) {
         }
      }

      Vec3d var7 = var3 == null ? Vec3d.ZERO : var3.multiply(2.0);
      return this.method3866(var7) ? var2.offset(var7) : var2;
   }

   private LivingEntity method3852() {
      LivingEntity var1 = AuraModule.INSTANCE.method6358();
      return var1 != null && var1.isAlive() ? var1 : null;
   }

   private Vec3d method3853(Vec3d var1) {
      if (this.entry != null && var1 != null) {
         Vec3d var2 = var1.subtract(this.entry.vec3d);
         if (this.method3866(var2) && var2.lengthSquared() > 1.0E-10) {
            return var2;
         }
      }

      Vec3d var3 = mc.player.getVelocity();
      return this.method3866(var3) ? var3 : Vec3d.ZERO;
   }

   private Vec3d method3854(LivingEntity var1, Box var2) {
      if (this.entry != null && var2 != null && this.entry.box != null) {
         Vec3d var3 = this.method3855(var2).subtract(this.method3855(this.entry.box));
         if (this.method3866(var3) && var3.lengthSquared() > 1.0E-10) {
            return var3;
         }
      }

      Vec3d var4 = var1 == null ? Vec3d.ZERO : var1.getVelocity();
      return this.method3866(var4) ? var4 : Vec3d.ZERO;
   }

   private Vec3d method3855(Box var1) {
      return new Vec3d((var1.minX + var1.maxX) * 0.5, (var1.minY + var1.maxY) * 0.5, (var1.minZ + var1.maxZ) * 0.5);
   }

   private float hypot(float var1, float var2) {
      return (float)Math.sqrt(var1 * var1 + var2 * var2);
   }

   private float method3856(float var1, float var2) {
      return var1 / var2;
   }

   private float method3857(float var1, float var2) {
      return Math.round(var1 / var2);
   }

   private float method3858(float var1, float var2) {
      return this.isFinite(var2) && var2 > 0.0F ? var1 * var2 : var1;
   }

   private int method3859(float var1) {
      if (var1 > 0.0F) {
         return 1;
      } else {
         return var1 < 0.0F ? -1 : 0;
      }
   }

   private float method3860(int var1, int var2, float var3) {
      return var1 != 0 && var2 != 0 && var1 != var2 ? 0.0F : var3 + 1.0F;
   }

   private float method3861(float var1, int var2, int var3, float var4) {
      float var5 = Math.abs(var1);
      if (var2 == 0) {
         return 0.0F;
      } else {
         return var3 != 0 && var2 == var3 ? Math.max(var4, var5) : var5;
      }
   }

   private float method3862(float var1, float var2) {
      return var2 > 0.0F ? Math.abs(var1) / var2 : 0.0F;
   }

   private float method3863(float var1, float var2) {
      float var3 = var1 - var2;
      return !Float.isFinite(var3) ? var1 : var2 + MathHelper.wrapDegrees(var3);
   }

   private void method3864() {
      this.intValue = Integer.MIN_VALUE;
      this.flag = false;
      this.floatValue = 0.0F;
      this.floatValue2 = 0.0F;
      this.floatValue3 = 0.0F;
      this.floatValue4 = 0.0F;
      this.floatValue5 = 0.0F;
      this.floatValue6 = 0.0F;
      this.flag2 = false;
      this.floatValue7 = 0.0F;
      this.floatValue8 = 0.0F;
      this.floatValue9 = 0.0F;
      this.floatValue10 = 0.0F;
      this.floatValue11 = 0.0F;
      this.floatValue12 = 0.0F;
      this.intValue2 = 0;
      this.intValue3 = 0;
      this.floatValue13 = 0.0F;
      this.floatValue14 = 0.0F;
      this.floatValue15 = 0.0F;
      this.floatValue16 = 0.0F;
      this.entry = null;
      this.floatValues = null;
      if (this.resettable != null) {
         this.resettable.reset();
      }
   }

   private boolean method3865(float[] var1) {
      if (var1 == null) {
         return false;
      } else {
         for (float var5 : var1) {
            if (!this.isFinite(var5)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean isFinite(float var1) {
      return !Float.isNaN(var1) && !Float.isInfinite(var1);
   }

   private boolean method3866(Vec3d var1) {
      return var1 != null && this.isFinite((float)var1.x) && this.isFinite((float)var1.y) && this.isFinite((float)var1.z);
   }

   private static final class Entry {
      private final float[] floatValues;
      private final Vec3d vec3d;
      private final Box box;
      private final float floatValue;

      private Entry(float[] var1, Vec3d var2, Box var3, float var4) {
         this.floatValues = (float[])var1.clone();
         this.vec3d = var2;
         this.box = var3;
         this.floatValue = var4;
      }
   }
}

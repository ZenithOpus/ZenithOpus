package org.zenith.base.rotation.mods;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.Rotation;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.api.JointCodebook;
import org.zenith.utility.misc.Class6;
import org.zenith.utility.misc.Util22;
import org.zenith.utility.misc.Vec2f;

public class RawDiffFeatures extends Util22 {
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private static final float CONST_floatValue = 50.0F;
   private static final float CONST_floatValue2 = 176.0F;
   private static final float CONST_floatValue3 = 8.0F;
   private static final float CONST_floatValue4 = 8.0F;
   private static final float CONST_floatValue5 = 20.0F;
   private static final float CONST_floatValue6 = 0.18F;
   private static final float CONST_floatValue7 = 0.055F;
   private static final float CONST_floatValue8 = 0.08F;
   private static final float CONST_floatValue9 = 0.14F;
   private static final float CONST_floatValue10 = 6.0F;
   private static final float CONST_floatValue11 = 1.15F;
   private static final float CONST_floatValue12 = 1.0F;
   private static final float CONST_floatValue13 = 8.0F;
   private static final float CONST_floatValue14 = 4.0F;
   private static final float CONST_floatValue15 = 3.0F;
   private static final int CONST_intValue2 = 2;
   private static final float CONST_floatValue16 = 6.0F;
   private static final float CONST_floatValue17 = 1.0F;
   private static final float CONST_floatValue18 = 2.0F;
   private static final float CONST_floatValue19 = 0.12F;
   private static final float CONST_floatValue20 = 18.0F;
   private static final float CONST_floatValue21 = 10.0F;
   private boolean flag = false;
   private int intValue = Integer.MIN_VALUE;
   private float[] floatValues = null;
   private JointCodebook.Entry7 entry7 = null;
   private float floatValue = 0.0F;
   private float floatValue2 = 0.0F;
   private int intValue2 = 0;
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
   private boolean flag3 = false;
   private float floatValue13 = 0.0F;
   private float floatValue14 = 0.0F;
   private float floatValue15 = 0.0F;
   private float floatValue16 = 0.0F;
   private double doubleValue = 0.0;
   private double doubleValue2 = 0.0;
   private double doubleValue3 = 0.0;
   private double doubleValue4 = 0.0;
   private double doubleValue5 = 0.0;
   private double doubleValue6 = 0.0;
   private boolean flag4 = false;
   private float floatValue17 = 0.0F;
   private float floatValue18 = 0.0F;
   private float floatValue19 = 0.0F;
   private float floatValue20 = 0.0F;
   private float floatValue21 = 0.0F;
   private float floatValue22 = 0.0F;

   public Rotation method3699(Class6 var1, Rotation var2) {
      return this.method3700(var1, var2, this.method3719());
   }

   public Rotation method3700(Class6 var1, Rotation var2, LivingEntity var3) {
      Rotation var4 = rotationManager.method7844();
      this.method3720();
      if (!jointCodebook.method2545()) {
         this.method3721();
         return var4;
      } else {
         mc.player.tick();
         if (var2 == null || var2.method3137()) {
            this.method3721();
            return var4;
         } else if (var3 != null && var3.isAlive()) {
            int var5 = var3.getId();
            if (this.intValue != var5) {
               this.method3721();
               this.intValue = var5;
            }

            RawDiffFeatures.Entry var6 = this.method3705(var4, var3);
            if (var6 == null) {
               this.method3721();
               return var4;
            } else {
               String[] var7 = jointCodebook.method2539();
               float[] var8 = this.method3703(var6, var7);
               int var9 = jointCodebook.method2538();
               if (var8 != null && var8.length == var9 && this.method3725(var8)) {
                  this.floatValues = (float[])var8.clone();
                  JointCodebook.Entry7 var10 = jointCodebook.method2546(var8, var6.floatValue25, var6.floatValue26);
                  if (var10 == null) {
                     this.entry7 = null;
                     return var4;
                  } else {
                     RawDiffFeatures.Entry2 var11 = this.method3707(var10.getFloatValue(), var10.getFloatValue2());
                     if (var11 == null) {
                        this.entry7 = null;
                        return var4;
                     } else {
                        RawDiffFeatures.Entry2 var12 = this.method3708(var11);
                        if (var12 != null && this.isFinite(var12.floatValue) && this.isFinite(var12.floatValue2)) {
                           var12 = this.method3709(var6, var12);
                           if (var12 != null && this.isFinite(var12.floatValue) && this.isFinite(var12.floatValue2)) {
                              this.entry7 = var10.method2593(
                                 var12.floatValue,
                                 var12.floatValue2,
                                 var6.floatValue25,
                                 var6.floatValue26,
                                 var12.floatValue3,
                                 var12.floatValue4,
                                 var12.floatValue5,
                                 var12.floatValue6,
                                 var12.floatValue7,
                                 var12.floatValue8
                              );
                              this.floatValue5 = this.floatValue3;
                              this.floatValue6 = this.floatValue4;
                              this.floatValue3 = var12.floatValue5;
                              this.floatValue4 = var12.floatValue6;
                              Rotation var13 = var4.method3140(var12.floatValue, var12.floatValue2);
                              return new Rotation(var13.getFloatValue(), MathHelper.clamp(var13.getFloatValue2(), -90.0F, 90.0F));
                           } else {
                              this.entry7 = null;
                              return var4;
                           }
                        } else {
                           this.entry7 = null;
                           return var4;
                        }
                     }
                  }
               } else {
                  this.method3721();
                  return var4;
               }
            }
         } else {
            this.method3721();
            return var4;
         }
      }
   }

   public JointCodebook.Entry7 getEntry7() {
      return this.entry7;
   }

   public float[] method3701() {
      return this.floatValues == null ? null : (float[])this.floatValues.clone();
   }

   public void method3702() {
      this.method3721();
   }

   private float[] method3703(RawDiffFeatures.Entry var1, String[] var2) {
      if (var1 != null && var2 != null && var2.length != 0) {
         float[] var3 = new float[var2.length];

         for (int var4 = 0; var4 < var2.length; var4++) {
            float var5 = this.method3704(var1, var2[var4]);
            if (!this.isFinite(var5)) {
               return null;
            }

            var3[var4] = var5;
         }

         return var3;
      } else {
         return null;
      }
   }

   private float method3704(RawDiffFeatures.Entry var1, String var2) {
      if (var2 == null) {
         return Float.NaN;
      } else {
         switch (var2) {
            case "raw_center_Diffyaw":
               return this.method3717(var1.floatValue, var1.floatValue45);
            case "raw_center_Diffpitch":
               return this.method3717(var1.floatValue2, var1.floatValue45);
            case "raw_min_Diffyaw":
               return this.method3717(var1.floatValue3, var1.floatValue45);
            case "raw_max_Diffyaw":
               return this.method3717(var1.floatValue4, var1.floatValue45);
            case "raw_min_Diffpitch":
               return this.method3717(var1.floatValue5, var1.floatValue45);
            case "raw_max_Diffpitch":
               return this.method3717(var1.floatValue6, var1.floatValue45);
            case "raw_width_Diffyaw":
               return this.method3717(var1.floatValue7, var1.floatValue45);
            case "raw_height_Diffpitch":
               return this.method3717(var1.floatValue8, var1.floatValue45);
            case "raw_target_x_norm":
               return var1.floatValue9;
            case "raw_target_y_norm":
               return var1.floatValue10;
            case "raw_distance":
               return var1.floatValue12;
            case "raw_delta_center_Diffyaw":
               return this.method3717(var1.floatValue16, var1.floatValue45);
            case "raw_delta_center_Diffpitch":
               return this.method3717(var1.floatValue17, var1.floatValue45);
            case "raw_delta_distance":
               return var1.floatValue18;
            case "raw_inside_box":
               return var1.flag ? 1.0F : 0.0F;
            case "raw_inside_ticks_clamped":
               return var1.floatValue14;
            case "outside_box_ticks":
               return var1.floatValue15;
            case "yaw_vel":
               return this.method3716(var1.floatValue25, var1.floatValue45);
            case "pitch_vel":
               return this.method3716(var1.floatValue26, var1.floatValue45);
            case "yaw_acc":
               return this.method3716(var1.floatValue27, var1.floatValue45);
            case "pitch_acc":
               return this.method3716(var1.floatValue28, var1.floatValue45);
            case "normalized_center_yaw":
               return var1.floatValue31;
            case "normalized_center_pitch":
               return var1.floatValue32;
            case "yaw_jerk":
               return this.method3716(var1.floatValue29, var1.floatValue45);
            case "pitch_jerk":
               return this.method3716(var1.floatValue30, var1.floatValue45);
            case "ang_dist":
               return this.method3717(var1.floatValue33, var1.floatValue45);
            case "dir_sin":
               return var1.floatValue34;
            case "dir_cos":
               return var1.floatValue35;
            case "t_since_entered_box":
               return var1.floatValue36;
            case "t_since_left_box":
               return var1.floatValue37;
            case "effort_fatigue":
               return var1.floatValue38;
            case "active_burst_ticks":
               return var1.floatValue39;
            case "input_forward":
               return var1.floatValue40;
            case "input_backward":
               return var1.floatValue41;
            case "input_left":
               return var1.floatValue42;
            case "input_right":
               return var1.floatValue43;
            case "input_jump":
               return var1.floatValue44;
            case "diff_y":
               return var1.floatValue11;
            case "distance_xz":
               return var1.floatValue13;
            case "target_motion_x":
               return var1.floatValue19;
            case "target_motion_y":
               return var1.floatValue20;
            case "target_motion_z":
               return var1.floatValue21;
            case "player_motion_x":
               return var1.floatValue22;
            case "player_motion_y":
               return var1.floatValue23;
            case "player_motion_z":
               return var1.floatValue24;
            case "v18_geometry_unstable":
               return var1.floatValue46;
            case "v18_geometry_confidence":
               return var1.floatValue47;
            case "v18_inside_confidence":
               return var1.floatValue48;
            case "v18_close_overlap":
               return var1.floatValue49;
            case "v18_center_absmax":
               return var1.floatValue50;
            case "v18_prev_vel_absmax":
               return var1.floatValue51;
            case "v18_nearest_yaw_error":
               return var1.floatValue52;
            case "v18_nearest_pitch_error":
               return var1.floatValue53;
            case "v18_aim_yaw_error":
               return var1.floatValue54;
            case "v18_aim_pitch_error":
               return var1.floatValue55;
            case "prev_action_yaw_count":
               return var1.floatValue56;
            case "prev_action_pitch_count":
               return var1.floatValue57;
            case "prev2_action_yaw_count":
               return var1.floatValue58;
            case "prev2_action_pitch_count":
               return var1.floatValue59;
            default:
               return Float.NaN;
         }
      }
   }

   private RawDiffFeatures.Entry method3705(Rotation var1, LivingEntity var2) {
      if (var1 != null && var2 != null) {
         Box var3 = var2.getBoundingBox();
         Vec3d var4 = mc.player.getEntityPos();
         Vec3d var5 = mc.player.getEyePos();
         Vec3d var6 = var3.getCenter();
         Rotation var7 = RotationUtils.method6912(var6.subtract(var5));
         Vec2f var8 = var1.method3131(var7);
         float var9 = var8.getFloatValue();
         float var10 = var8.getFloatValue2();
         if (this.isFinite(var9) && this.isFinite(var10)) {
            float var11 = Float.MAX_VALUE;
            float var12 = -Float.MAX_VALUE;
            float var13 = Float.MAX_VALUE;
            float var14 = -Float.MAX_VALUE;
            Vec3d[] var15 = new Vec3d[]{
               new Vec3d(var3.minX, var3.minY, var3.minZ),
               new Vec3d(var3.minX, var3.minY, var3.maxZ),
               new Vec3d(var3.minX, var3.maxY, var3.minZ),
               new Vec3d(var3.minX, var3.maxY, var3.maxZ),
               new Vec3d(var3.maxX, var3.minY, var3.minZ),
               new Vec3d(var3.maxX, var3.minY, var3.maxZ),
               new Vec3d(var3.maxX, var3.maxY, var3.minZ),
               new Vec3d(var3.maxX, var3.maxY, var3.maxZ)
            };

            for (Vec3d var19 : var15) {
               Rotation var20 = RotationUtils.method6912(var19.subtract(var5));
               Vec2f var21 = var1.method3131(var20);
               float var22 = var21.getFloatValue2();
               if (this.isFinite(var21.getFloatValue()) && this.isFinite(var22)) {
                  float var23 = this.method3718(var21.getFloatValue(), var9);
                  var11 = Math.min(var11, var23);
                  var12 = Math.max(var12, var23);
                  var13 = Math.min(var13, var22);
                  var14 = Math.max(var14, var22);
               }
            }

            if (var11 == Float.MAX_VALUE) {
               var11 = var9;
               var12 = var9;
               var13 = var10;
               var14 = var10;
            }

            float var43 = var12 - var11;
            float var44 = var14 - var13;
            boolean var45 = var11 <= 0.0F && var12 >= 0.0F;
            boolean var46 = var13 <= 0.0F && var14 >= 0.0F;
            boolean var47 = var45 && var46;
            float var48 = 0.0F;
            if (var45 && Math.abs(var43) > 1.0E-6F) {
               var48 = MathHelper.clamp((0.0F - var11) / var43, 0.0F, 1.0F);
            }

            float var49 = 0.0F;
            if (var46 && Math.abs(var44) > 1.0E-6F) {
               var49 = MathHelper.clamp((0.0F - var13) / var44, 0.0F, 1.0F);
            }

            float var50 = 0.0F;
            float var24 = 0.0F;
            if (this.flag2) {
               var50 = MathHelper.wrapDegrees(var1.getFloatValue() - this.floatValue7);
               var24 = var1.getFloatValue2() - this.floatValue8;
            }

            float var25 = var50 - this.floatValue9;
            float var26 = var24 - this.floatValue10;
            float var27 = var25 - this.floatValue11;
            float var28 = var26 - this.floatValue12;
            this.floatValue17 = var47 ? MathHelper.clamp(this.floatValue17 + 1.0F, 0.0F, 50.0F) : 0.0F;
            this.floatValue18 = var47 ? 0.0F : Math.min(this.floatValue18 + 1.0F, 8.0F);
            if (var47) {
               this.floatValue19 = MathHelper.clamp(this.flag4 ? this.floatValue19 + 1.0F : 1.0F, 0.0F, 176.0F);
               this.floatValue20 = 0.0F;
            } else {
               this.floatValue19 = 0.0F;
               this.floatValue20 = MathHelper.clamp(this.flag4 ? 1.0F : this.floatValue20 + 1.0F, 0.0F, 8.0F);
            }

            this.method3706(var43, var44, var50, var24, var47);
            float var29 = (float)(var6.x - var5.x);
            float var30 = Math.abs((float)(var6.y - var5.y));
            float var31 = (float)(var6.z - var5.z);
            float var32 = this.hypot(var29, var31);
            float var33 = this.hypot(var32, var30);
            float var34 = this.flag3 ? this.hypot(this.floatValue15, this.floatValue16) : var33;
            float var35 = this.flag3 ? MathHelper.wrapDegrees(var9 - this.floatValue13) : 0.0F;
            float var36 = this.flag3 ? var10 - this.floatValue14 : 0.0F;
            float var37 = var33 - var34;
            Vec3d var38 = this.flag3 ? var6.subtract(this.doubleValue, this.doubleValue2, this.doubleValue3) : Vec3d.ZERO;
            if (!this.method3726(var38) || var38.lengthSquared() <= 1.0E-10) {
               Vec3d var39 = var2.getVelocity();
               var38 = this.method3726(var39) ? var39 : Vec3d.ZERO;
            }

            Vec3d var51 = this.flag3 ? var4.subtract(this.doubleValue4, this.doubleValue5, this.doubleValue6) : Vec3d.ZERO;
            if (!this.method3726(var51) || var51.lengthSquared() <= 1.0E-10) {
               Vec3d var40 = mc.player.getVelocity();
               var51 = this.method3726(var40) ? var40 : Vec3d.ZERO;
            }

            float var52 = jointCodebook.method2540();
            if (this.isFinite(var52) && !(var52 <= 0.0F)) {
               RawDiffFeatures.Entry var41 = new RawDiffFeatures.Entry();
               var41.floatValue = var9;
               var41.floatValue2 = var10;
               var41.floatValue3 = var11;
               var41.floatValue4 = var12;
               var41.floatValue5 = var13;
               var41.floatValue6 = var14;
               var41.floatValue7 = var43;
               var41.floatValue8 = var44;
               var41.floatValue9 = var48;
               var41.floatValue10 = var49;
               var41.floatValue11 = var30;
               var41.floatValue12 = var33;
               var41.floatValue13 = var32;
               var41.flag = var47;
               var41.floatValue14 = this.floatValue17;
               var41.floatValue15 = this.floatValue18;
               var41.floatValue16 = var35;
               var41.floatValue17 = var36;
               var41.floatValue18 = var37;
               var41.floatValue19 = (float)var38.x;
               var41.floatValue20 = (float)var38.y;
               var41.floatValue21 = (float)var38.z;
               var41.floatValue22 = (float)var51.x;
               var41.floatValue23 = (float)var51.y;
               var41.floatValue24 = (float)var51.z;
               var41.floatValue25 = var50;
               var41.floatValue26 = var24;
               var41.floatValue27 = var25;
               var41.floatValue28 = var26;
               var41.floatValue29 = var27;
               var41.floatValue30 = var28;
               var41.floatValue31 = var9 / Math.max(Math.abs(var43), 0.1F);
               var41.floatValue32 = var10 / Math.max(Math.abs(var44), 0.1F);
               var41.floatValue33 = this.hypot(var9, var10);
               float var42 = (float)Math.atan2(var10, var9);
               var41.floatValue34 = (float)Math.sin(var42);
               var41.floatValue35 = (float)Math.cos(var42);
               var41.floatValue36 = this.floatValue19;
               var41.floatValue37 = this.floatValue20;
               var41.floatValue38 = this.floatValue21;
               var41.floatValue39 = this.floatValue22;
               var41.floatValue40 = mc.player.input.playerInput.forward() ? 1.0F : 0.0F;
               var41.floatValue41 = mc.player.input.playerInput.backward() ? 1.0F : 0.0F;
               var41.floatValue42 = mc.player.input.playerInput.left() ? 1.0F : 0.0F;
               var41.floatValue43 = mc.player.input.playerInput.right() ? 1.0F : 0.0F;
               var41.floatValue44 = mc.player.input.playerInput.jump() ? 1.0F : 0.0F;
               var41.floatValue45 = var52;
               var41.floatValue56 = this.floatValue3;
               var41.floatValue57 = this.floatValue4;
               var41.floatValue58 = this.floatValue5;
               var41.floatValue59 = this.floatValue6;
               this.method3722(var41);
               this.flag2 = true;
               this.floatValue7 = var1.getFloatValue();
               this.floatValue8 = var1.getFloatValue2();
               this.floatValue9 = var50;
               this.floatValue10 = var24;
               this.floatValue11 = var25;
               this.floatValue12 = var26;
               this.flag3 = true;
               this.floatValue13 = var9;
               this.floatValue14 = var10;
               this.floatValue15 = var32;
               this.floatValue16 = var30;
               this.doubleValue = var6.x;
               this.doubleValue2 = var6.y;
               this.doubleValue3 = var6.z;
               this.doubleValue4 = var4.x;
               this.doubleValue5 = var4.y;
               this.doubleValue6 = var4.z;
               this.flag4 = var47;
               return var41;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private void method3706(float var1, float var2, float var3, float var4, boolean var5) {
      float var6 = Math.max(Math.abs(var1) * 0.4F, 0.25F);
      float var7 = Math.max(Math.abs(var2) * 0.4F, 0.18F);
      float var8 = (float)Math.sqrt(this.method3724(var3 / var6) + this.method3724(var4 / var7));
      var8 = Math.min(var8, 1.5F);
      if (var8 > 0.18F) {
         this.floatValue22 = Math.min(this.floatValue22 + 1.0F, 20.0F);
         this.floatValue21 = Math.min(this.floatValue21 + var8 * 0.055F, 1.0F);
      } else {
         this.floatValue22 = Math.max(this.floatValue22 - 2.0F, 0.0F);
         float var9 = var5 ? 0.14F : 0.08F;
         this.floatValue21 = Math.max(this.floatValue21 - var9, 0.0F);
      }
   }

   private RawDiffFeatures.Entry2 method3707(float var1, float var2) {
      return this.isFinite(var1) && this.isFinite(var2) ? new RawDiffFeatures.Entry2(var1, var2) : null;
   }

   private RawDiffFeatures.Entry2 method3708(RawDiffFeatures.Entry2 var1) {
      if (var1 == null) {
         return null;
      } else {
         float var2 = jointCodebook.method2540();
         if (this.isFinite(var2) && !(var2 <= 0.0F)) {
            float var3 = var1.floatValue / var2 + this.floatValue;
            float var4 = var1.floatValue2 / var2 + this.floatValue2;
            if (this.isFinite(var3) && this.isFinite(var4)) {
               int var5 = Math.round(var3);
               int var6 = Math.round(var4);
               this.floatValue = var3 - var5;
               this.floatValue2 = var4 - var6;
               return new RawDiffFeatures.Entry2(var5 * var2, var6 * var2, var3, var4, var5, var6, this.floatValue, this.floatValue2);
            } else {
               this.floatValue = 0.0F;
               this.floatValue2 = 0.0F;
               return null;
            }
         } else {
            this.floatValue = 0.0F;
            this.floatValue2 = 0.0F;
            return null;
         }
      }
   }

   private RawDiffFeatures.Entry2 method3709(RawDiffFeatures.Entry var1, RawDiffFeatures.Entry2 var2) {
      if (var1 != null && var2 != null) {
         RawDiffFeatures.Entry2 var3 = this.method3710(var1, var2);
         if (var3 != null) {
            return var3;
         } else {
            RawDiffFeatures.Entry2 var4 = this.method3713(var1, var2);
            return var4 != null ? var4 : var2;
         }
      } else {
         return var2;
      }
   }

   private RawDiffFeatures.Entry2 method3710(RawDiffFeatures.Entry var1, RawDiffFeatures.Entry2 var2) {
      if (var1 != null && var2 != null && !var1.flag && !(var1.floatValue15 < 3.0F)) {
         float var3 = var1.floatValue52;
         float var4 = var1.floatValue53;
         float var5 = Math.max(Math.abs(var3), Math.abs(var4));
         if (this.isFinite(var5) && !(var5 < 6.0F)) {
            boolean var6 = this.method3711(var3, var2.floatValue5);
            boolean var7 = this.method3711(var4, var2.floatValue6);
            if (!var6 && !var7) {
               this.intValue2 = 0;
               return null;
            } else {
               this.intValue2++;
               if (this.intValue2 < 2) {
                  return null;
               } else {
                  this.intValue2 = 0;
                  float var8 = var6 ? this.method3712(var3, 18.0F) : var2.floatValue5;
                  float var9 = var7 ? this.method3712(var4, 10.0F) : var2.floatValue6;
                  return this.method3715(var8, var9);
               }
            }
         } else {
            this.intValue2 = 0;
            return null;
         }
      } else {
         this.intValue2 = 0;
         return null;
      }
   }

   private boolean method3711(float var1, float var2) {
      if (!this.isFinite(var1) || Math.abs(var1) < 6.0F) {
         return false;
      } else {
         return this.isFinite(var2) && !(Math.abs(var2) <= 1.0F) ? Math.signum(var1) != Math.signum(var2) : true;
      }
   }

   private float method3712(float var1, float var2) {
      float var3 = MathHelper.clamp(Math.abs(var1) * 0.12F, 2.0F, var2);
      return Math.copySign((float)Math.round(var3), var1);
   }

   private RawDiffFeatures.Entry2 method3713(RawDiffFeatures.Entry var1, RawDiffFeatures.Entry2 var2) {
      if (var1 != null && var2 != null && var1.flag && !(var1.floatValue14 < 6.0F)) {
         float var3 = var2.floatValue5;
         float var4 = var2.floatValue6;
         boolean var5 = false;
         if (this.method3714(var3, var2.floatValue3, var1.floatValue56, 1.15F, 8.0F)) {
            var3 = 0.0F;
            var5 = true;
         }

         if (this.method3714(var4, var2.floatValue4, var1.floatValue57, 1.0F, 4.0F)) {
            var4 = 0.0F;
            var5 = true;
         }

         return var5 ? this.method3715(var3, var4) : null;
      } else {
         return null;
      }
   }

   private boolean method3714(float var1, float var2, float var3, float var4, float var5) {
      return this.isFinite(var1) && this.isFinite(var2) && this.isFinite(var3) && Math.abs(var1) == 1.0F && Math.abs(var2) <= var4 && Math.abs(var3) <= var5;
   }

   private RawDiffFeatures.Entry2 method3715(float var1, float var2) {
      float var3 = jointCodebook.method2540();
      if (this.isFinite(var3) && !(var3 <= 0.0F) && this.isFinite(var1) && this.isFinite(var2)) {
         var1 = Math.round(var1);
         var2 = Math.round(var2);
         this.floatValue = 0.0F;
         this.floatValue2 = 0.0F;
         return new RawDiffFeatures.Entry2(var1 * var3, var2 * var3, var1, var2, var1, var2, 0.0F, 0.0F);
      } else {
         this.floatValue = 0.0F;
         this.floatValue2 = 0.0F;
         return null;
      }
   }

   private float method3716(float var1, float var2) {
      return this.isFinite(var1) && this.isFinite(var2) && !(var2 <= 0.0F) ? Math.round(var1 / var2) : 0.0F;
   }

   private float method3717(float var1, float var2) {
      return this.isFinite(var1) && this.isFinite(var2) && !(var2 <= 0.0F) ? var1 / var2 : 0.0F;
   }

   private float method3718(float var1, float var2) {
      float var3 = var1 - var2;
      return !Float.isFinite(var3) ? var1 : var2 + MathHelper.wrapDegrees(var3);
   }

   private LivingEntity method3719() {
      LivingEntity var1 = AuraModule.INSTANCE.method6358();
      return var1 != null && var1.isAlive() ? var1 : null;
   }

   private void method3720() {
      if (!jointCodebook.isLoaded() && !this.flag) {
         this.flag = true;
         jointCodebook.method2529();
      }
   }

   private void method3721() {
      jointCodebook.method2536();
      this.intValue = Integer.MIN_VALUE;
      this.floatValues = null;
      this.entry7 = null;
      this.floatValue = 0.0F;
      this.floatValue2 = 0.0F;
      this.intValue2 = 0;
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
      this.flag3 = false;
      this.floatValue13 = 0.0F;
      this.floatValue14 = 0.0F;
      this.floatValue15 = 0.0F;
      this.floatValue16 = 0.0F;
      this.doubleValue = 0.0;
      this.doubleValue2 = 0.0;
      this.doubleValue3 = 0.0;
      this.doubleValue4 = 0.0;
      this.doubleValue5 = 0.0;
      this.doubleValue6 = 0.0;
      this.flag4 = false;
      this.floatValue17 = 0.0F;
      this.floatValue18 = 0.0F;
      this.floatValue19 = 0.0F;
      this.floatValue20 = 0.0F;
      this.floatValue21 = 0.0F;
      this.floatValue22 = 0.0F;
   }

   private void method3722(RawDiffFeatures.Entry var1) {
      if (var1 != null) {
         float var2 = jointCodebook.method2541();
         float var3 = jointCodebook.method2542();
         float var4 = jointCodebook.method2543();
         float var5 = Math.max(0.05F, jointCodebook.method2544());
         float var6 = this.method3717(var1.floatValue, var1.floatValue45);
         float var7 = this.method3717(var1.floatValue2, var1.floatValue45);
         float var8 = this.method3717(var1.floatValue3, var1.floatValue45);
         float var9 = this.method3717(var1.floatValue4, var1.floatValue45);
         float var10 = this.method3717(var1.floatValue5, var1.floatValue45);
         float var11 = this.method3717(var1.floatValue6, var1.floatValue45);
         float var12 = Math.abs(this.method3717(var1.floatValue7, var1.floatValue45));
         float var13 = Math.abs(this.method3717(var1.floatValue8, var1.floatValue45));
         float var14 = Math.max(Math.abs(var6), Math.abs(var7));
         float var15 = Math.max(
            Math.abs(this.method3716(var1.floatValue25, var1.floatValue45)), Math.abs(this.method3716(var1.floatValue26, var1.floatValue45))
         );
         boolean var16 = var1.flag;
         float var17 = var1.floatValue13;
         float var18 = MathHelper.clamp(var2 / Math.max(var12, 1.0E-6F), 0.0F, 1.0F);
         float var19 = MathHelper.clamp(var3 / Math.max(var13, 1.0E-6F), 0.0F, 1.0F);
         float var20 = var16 ? MathHelper.clamp(var4 / Math.max(var14, 1.0E-6F), 0.0F, 1.0F) : 1.0F;
         float var21 = MathHelper.clamp(var17 / Math.max(var5, 1.0E-6F), 0.0F, 1.0F);
         float var22 = Math.min(Math.min(var18, var19), Math.min(var20, var21));
         boolean var23 = var22 < 0.999F;
         boolean var24 = var17 < var5 || var16 && var23;
         float var25 = this.method3723(var8, var9);
         float var26 = this.method3723(var10, var11);
         float var27 = var24 ? MathHelper.clamp(var6, -var4, var4) : var6;
         float var28 = var24 ? MathHelper.clamp(var7, -var4, var4) : var7;
         var1.floatValue46 = var23 ? 1.0F : 0.0F;
         var1.floatValue47 = var22;
         var1.floatValue48 = (var16 ? 1.0F : 0.0F) * var22;
         var1.floatValue49 = var24 ? 1.0F : 0.0F;
         var1.floatValue50 = var14;
         var1.floatValue51 = var15;
         var1.floatValue52 = var25;
         var1.floatValue53 = var26;
         var1.floatValue54 = var27;
         var1.floatValue55 = var28;
      }
   }

   private float method3723(float var1, float var2) {
      if (var1 <= 0.0F && var2 >= 0.0F) {
         return 0.0F;
      } else {
         return Math.abs(var1) <= Math.abs(var2) ? var1 : var2;
      }
   }

   private float hypot(float var1, float var2) {
      return (float)Math.sqrt(var1 * var1 + var2 * var2);
   }

   private float method3724(float var1) {
      return var1 * var1;
   }

   private boolean method3725(float[] var1) {
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

   private boolean method3726(Vec3d var1) {
      return var1 != null && this.isFinite((float)var1.x) && this.isFinite((float)var1.y) && this.isFinite((float)var1.z);
   }

   private static final class Entry {
      public float floatValue;
      public float floatValue2;
      public float floatValue3;
      public float floatValue4;
      public float floatValue5;
      public float floatValue6;
      public float floatValue7;
      public float floatValue8;
      public float floatValue9;
      public float floatValue10;
      public float floatValue11;
      public float floatValue12;
      public float floatValue13;
      public boolean flag;
      public float floatValue14;
      public float floatValue15;
      public float floatValue16;
      public float floatValue17;
      public float floatValue18;
      public float floatValue19;
      public float floatValue20;
      public float floatValue21;
      public float floatValue22;
      public float floatValue23;
      public float floatValue24;
      public float floatValue25;
      public float floatValue26;
      public float floatValue27;
      public float floatValue28;
      public float floatValue29;
      public float floatValue30;
      public float floatValue31;
      public float floatValue32;
      public float floatValue33;
      public float floatValue34;
      public float floatValue35;
      public float floatValue36;
      public float floatValue37;
      public float floatValue38;
      public float floatValue39;
      public float floatValue40;
      public float floatValue41;
      public float floatValue42;
      public float floatValue43;
      public float floatValue44;
      public float floatValue45;
      public float floatValue46;
      public float floatValue47;
      public float floatValue48;
      public float floatValue49;
      public float floatValue50;
      public float floatValue51;
      public float floatValue52;
      public float floatValue53;
      public float floatValue54;
      public float floatValue55;
      public float floatValue56;
      public float floatValue57;
      public float floatValue58;
      public float floatValue59;
   }

   private static final class Entry2 {
      public final float floatValue;
      public final float floatValue2;
      public final float floatValue3;
      public final float floatValue4;
      public final float floatValue5;
      public final float floatValue6;
      public final float floatValue7;
      public final float floatValue8;

      public Entry2(float var1, float var2) {
         this(var1, var2, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      }

      public Entry2(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
         this.floatValue6 = var6;
         this.floatValue7 = var7;
         this.floatValue8 = var8;
      }
   }
}

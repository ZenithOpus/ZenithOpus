package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.zenith.utility.misc.AnimationState;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.Class16;
import org.zenith.utility.misc.Class3;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.render.Util19;
import org.zenith.utility.render.WardrobePreview;

public final class PetBehavior {
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private static final int CONST_intValue = 600;
   private static final double CONST_doubleValue = 12.0;
   public final HoveredData hoveredData;
   public final Class15 class15;
   public final Class15.Data3 data3;
   public final Class3 class3;
   public final AnimationState animationState = new AnimationState();
   private double doubleValue;
   private boolean flag;
   private boolean running;
   public final Class16 class16 = new Class16();
   public final Random random = Random.create();
   private int ticks;
   private int intValue;
   private Vec3d vec3d;
   private float floatValue;
   private float floatValue2;
   private int intValue2;
   private int intValue3 = 60;
   private int intValue4 = 40;
   private int intValue5;
   private int intValue6;
   private int intValue7;
   private int intValue8;
   private boolean flag2;
   private int intValue9;
   private int intValue10;
   private boolean flag3;
   private Vec3d vec3d2;
   private int intValue11;
   private float alpha = 1.0F;
   private float floatValue3;
   private float[] floatValues = new float[0];
   private float floatValue4;
   private float floatValue5;
   private String text = "";
   private int intValue12;
   private int intValue13 = 120;
   private Vec3d vec3d3;
   private double doubleValue2;
   private boolean flag4;
   private float floatValue6;
   private float floatValue7;
   private Vec3d vec3d4;
   public long longValue;

   public PetBehavior(HoveredData var1, Class15 var2) {
      this.hoveredData = var1;
      this.class15 = var2;
      this.data3 = var2.data3;
      double var3 = Math.max(0.2, (double)this.data3.getFloatValue());
      this.class3 = new Class3(this.data3.getType4(), 0.18 * var3, 0.6 * var3);
      this.intValue2 = this.random.nextBoolean() ? 1 : -1;
      this.intValue5 = 200 + this.random.nextInt(200);
      this.intValue6 = 160 + this.random.nextInt(160);
   }

   public float getAlpha() {
      return this.alpha;
   }

   public float getFloatValue3() {
      return this.floatValue3;
   }

   public boolean isFlag3() {
      return this.flag3;
   }

   public void method7926(PlayerEntity var1, LivingEntity var2, double var3, float var5) {
      Vec3d var6 = var1.getEntityPos();
      if (!this.class3.flag2) {
         this.class3.method425(this.method7939(var1, var6));
         this.class3.floatValue = this.class3.floatValue2 = var1.bodyYaw;
         this.class3.flag2 = true;
         this.vec3d = var6;
         this.floatValue5 = var1.bodyYaw;
      }

      int var7 = this.class3.method424(var3);

      for (int var8 = 0; var8 < var7; var8++) {
         this.method7928(var1, var6, var2, var5);
      }
   }

   public boolean isFlag4() {
      return this.flag4;
   }

   public float method7927(float var1) {
      return MathHelper.clamp(MathHelper.lerp(var1, this.floatValue7, this.floatValue6), 0.0F, 1.0F);
   }

   public Vec3d getVec3d4() {
      return this.vec3d4;
   }

   private void method7928(PlayerEntity var1, Vec3d var2, LivingEntity var3, float var4) {
      this.ticks++;
      this.class3.begin();
      this.method7936();
      this.floatValue7 = this.floatValue6;
      double var5 = var2.x - var1.lastX;
      double var7 = var2.y - var1.lastY;
      double var9 = var2.z - var1.lastZ;
      double var11 = this.vec3d == null ? 0.0 : Math.sqrt(var5 * var5 + var9 * var9);
      if (var11 > 0.02 && this.vec3d != null) {
         float var13 = (float)Math.toDegrees(Math.atan2(var9, var5)) - 90.0F;
         this.floatValue5 = this.floatValue5 + MathHelper.wrapDegrees(var13 - this.floatValue5) * 0.35F;
      }

      boolean var28 = this.vec3d != null
         && var5 * var5 + var7 * var7 + var9 * var9 < 4.0E-4
         && Math.abs(MathHelper.wrapDegrees(var1.getYaw() - this.floatValue)) < 1.0F
         && Math.abs(var1.getPitch() - this.floatValue2) < 1.0F;
      this.vec3d = var2;
      this.floatValue = var1.getYaw();
      this.floatValue2 = var1.getPitch();
      this.intValue = var28 ? this.intValue + 1 : 0;
      boolean var14 = var1.isSprinting() || var11 > 0.2;
      boolean var15 = var11 > 0.03;
      if (var1.hurtTime <= 0 && (var3 == null || !var3.isAlive())) {
         if (this.intValue8 > 0) {
            this.intValue8--;
         }
      } else {
         this.intValue8 = 60;
      }

      boolean var16 = this.intValue8 > 0 || "hide_in_shell".equals(this.data3.getText()) && this.method7946(var2, 8.0);
      if (this.flag4) {
         this.method7933(var1, var2, var14, var16, var4);
      } else {
         double var17 = method7951(this.class3.doubleValue3, this.class3.doubleValue5, var2.x, var2.z);
         if (!(var17 > 12.0) && !(Math.abs(this.class3.doubleValue4 - var2.y) > 12.0)) {
            boolean var19 = this.flag3;
            this.flag3 = this.intValue > 600 && !var16;
            if (var19 && !this.flag3) {
               this.animationState.method6540(this.class15, "react", var4);
               this.intValue3 = 80;
            }

            boolean var20 = this.text.equals("shoulder") || this.text.equals("head");
            if (this.flag3 || var16) {
               this.method7930();
            } else if (!this.text.isEmpty() && var15 && !this.text.equals("orbit") && !var20) {
               this.method7930();
            } else if (!this.text.isEmpty() && --this.intValue12 <= 0) {
               this.method7930();
            }

            if (this.text.isEmpty() && !this.flag3 && !var16 && !var15 && this.intValue13 == 0 && var17 < 4.0 && this.vec3d2 == null) {
               this.method7929(var1, var2, var4);
            }

            var20 = this.text.equals("shoulder") || this.text.equals("head");
            Vec3d var21 = this.method7937(var1, var2, var3, var16, var15, var14, var17);
            double var22 = this.data3.getFloatValue2();
            double var24 = method7951(this.class3.doubleValue3, this.class3.doubleValue5, var21.x, var21.z);
            if (var24 > 5.0) {
               var22 *= 1.9;
            } else if (var24 > 2.5 || var14) {
               var22 *= 1.35;
            }

            double var26 = this.data3.hover();
            if (this.class3.method423() && (this.text.equals("sit") || this.text.equals("lie"))) {
               var26 = 0.0;
            }

            if (!this.flag3 && (!(var24 < 0.3) || this.vec3d2 != null)) {
               this.class3.method426(var21.x, var21.y, var21.z, var22, var26);
            } else {
               this.class3.method426(this.class3.doubleValue3, var21.y, this.class3.doubleValue5, 0.0, var26);
            }

            if (var20 && method7951(this.class3.doubleValue3, this.class3.doubleValue5, var2.x, var2.z) < 1.1) {
               this.method7932();
            }

            if (this.data3.getType4() == Class15.Type4.HOPPER && this.class3.flag && var24 > 0.6 && this.intValue10 == 0) {
               this.class3.method427(0.42);
               this.intValue10 = 7;
               this.floatValue3 = 1.0F;
            }

            if (this.class3.flag3) {
               this.floatValue3 = 1.0F;
               if ("rain_cloud".equals(this.data3.getText())) {
                  this.method7948(ParticleTypes.CLOUD, 4, 0.2, 0.02);
               }
            }

            this.floatValue3 = Math.max(0.0F, this.floatValue3 - 0.12F);
            if (this.flag && !this.flag3) {
               this.class3.method428(var21.x, var21.z, 14.0F);
            } else if (!this.text.equals("lie") && !this.text.equals("sit")) {
               this.class3.method428(var2.x, var2.z, 6.0F);
            } else {
               this.class3.method428(var2.x, var2.z, 3.0F);
            }

            this.class3.method429(var2.x, var2.z);
            this.method7943(var1, var2, var16, var4);
            this.method7940(var1, var2, var4);
            this.method7941(var4);
            this.method7942();
         } else {
            this.method7949(this.class3.doubleValue3, this.class3.doubleValue4, this.class3.doubleValue5);
            this.class3.method425(this.method7939(var1, var2));
            this.method7949(this.class3.doubleValue3, this.class3.doubleValue4, this.class3.doubleValue5);
            this.method7930();
         }
      }
   }

   private void method7929(PlayerEntity var1, Vec3d var2, float var3) {
      ArrayList var4 = new ArrayList();

      for (String var6 : this.method7931()) {
         switch (var6) {
            case "sit":
            case "lie":
            case "wander":
            case "play":
            case "orbit":
               var4.add(var6);
               break;
            case "shoulder":
            case "head":
               if (var1.isOnGround() && !var1.isSprinting()) {
                  var4.add(var6);
               }
         }
      }

      if (var4.isEmpty()) {
         this.intValue13 = 200;
      } else {
         this.text = (String)var4.get(this.random.nextInt(var4.size()));
         String var11 = this.text;
         switch (var11) {
            case "sit":
               this.intValue12 = 100 + this.random.nextInt(140);
               break;
            case "lie":
               this.intValue12 = 160 + this.random.nextInt(180);
               break;
            case "orbit":
               this.intValue12 = 120 + this.random.nextInt(100);
               this.doubleValue2 = Math.atan2(this.class3.doubleValue5 - var2.z, this.class3.doubleValue3 - var2.x);
               break;
            case "wander":
               this.intValue12 = 140;
               double var7 = this.random.nextDouble() * Math.PI * 2.0;
               double var9 = 1.3 + this.random.nextDouble() * 1.6;
               this.vec3d3 = new Vec3d(var2.x + Math.cos(var7) * var9, var2.y, var2.z + Math.sin(var7) * var9);
               break;
            case "play":
               this.intValue12 = 50;
               if (!this.animationState.method6540(this.class15, "play", var3)) {
                  this.animationState.method6540(this.class15, "happy", var3);
               }
               break;
            default:
               this.intValue12 = 260 + this.random.nextInt(340);
         }
      }
   }

   private void method7930() {
      if (!this.text.isEmpty()) {
         this.text = "";
         this.vec3d3 = null;
         this.intValue13 = 70 + this.random.nextInt(160);
      }
   }

   private List<String> method7931() {
      if (!this.data3.getItems().isEmpty()) {
         return this.data3.getItems();
      } else {
         return switch (this.data3.getType4()) {
            case FLYER -> List.of("orbit", "sit", "play");
            case FLOATER -> List.of("orbit", "wander", "play");
            case HOPPER -> List.of("pace", "sit", "wander", "play");
            case CRAWLER -> List.of("sit", "lie", "wander");
            default -> List.of("pace", "sit", "lie", "wander", "play");
         };
      }
   }

   private void method7932() {
      this.flag4 = true;
      this.vec3d4 = new Vec3d(this.class3.doubleValue3, this.class3.doubleValue4, this.class3.doubleValue5);
      this.floatValue6 = 0.0F;
      this.floatValue7 = 0.0F;
      this.class3.doubleValue9 = this.class3.doubleValue10 = this.class3.doubleValue11 = 0.0;
   }

   private void method7933(PlayerEntity var1, Vec3d var2, boolean var3, boolean var4, float var5) {
      this.floatValue6 = Math.min(1.0F, this.floatValue6 + 0.16F);
      this.intValue12--;
      Vec3d var6 = this.method7934(var1, var2, var1.bodyYaw);
      this.class3.doubleValue3 = this.class3.doubleValue6 = var6.x;
      this.class3.doubleValue4 = this.class3.doubleValue7 = var6.y;
      this.class3.doubleValue5 = this.class3.doubleValue8 = var6.z;
      this.class3.floatValue = this.class3.floatValue2 = var1.bodyYaw;
      this.class3.doubleValue13 = 0.0;
      boolean var7 = !var1.isOnGround() && !var1.hasVehicle() && !var1.isTouchingWater();
      if (this.intValue12 > 0 && !var3 && !var4 && !var7 && !var1.isDead()) {
         this.class3.floatValue3 = this.class3.floatValue3 + (0.0F - this.class3.floatValue3) * 0.2F;
         this.method7941(var5);
      } else {
         this.flag4 = false;
         this.floatValue6 = 0.0F;
         this.floatValue7 = 0.0F;
         double var8 = Math.toRadians(var1.bodyYaw + (this.intValue2 > 0 ? -90.0 : 90.0));
         this.class3.doubleValue9 = -Math.sin(var8) * 0.12;
         this.class3.doubleValue11 = Math.cos(var8) * 0.12;
         this.class3.doubleValue10 = 0.18;
         this.class3.flag = false;
         this.method7930();
         this.animationState.method6540(this.class15, "react", var5);
      }
   }

   public Vec3d method7934(PlayerEntity var1, Vec3d var2, float var3) {
      double var4 = Math.toRadians(var3);
      boolean var6 = this.text.equals("head");
      double var7 = var6 ? (var1.isSneaking() ? 1.72 : 2.0) : (var1.isSneaking() ? 1.24 : 1.5);
      double var9 = var6 ? 0.0 : 0.31 * this.intValue2;
      double var11 = -Math.cos(var4);
      double var13 = -Math.sin(var4);
      double var15 = Math.sin(var4) * 0.03;
      double var17 = -Math.cos(var4) * 0.03;
      return new Vec3d(var2.x + var11 * var9 + var15, var2.y + var7, var2.z + var13 * var9 + var17);
   }

   public float method7935() {
      return this.text.equals("head") ? 0.5F : 0.55F;
   }

   private void method7936() {
      if (this.intValue13 > 0) {
         this.intValue13--;
      }

      if (this.intValue3 > 0) {
         this.intValue3--;
      }

      if (this.intValue4 > 0) {
         this.intValue4--;
      }

      if (this.intValue5 > 0) {
         this.intValue5--;
      }

      if (this.intValue6 > 0) {
         this.intValue6--;
      }

      if (this.intValue7 > 0) {
         this.intValue7--;
      }

      if (this.intValue10 > 0) {
         this.intValue10--;
      }

      if (this.intValue11 > 0) {
         this.intValue11--;
         if (this.intValue11 == 0) {
            this.vec3d2 = null;
         }
      }
   }

   private Vec3d method7937(PlayerEntity var1, Vec3d var2, LivingEntity var3, boolean var4, boolean var5, boolean var6, double var7) {
      if (this.vec3d2 != null) {
         return this.vec3d2;
      } else if (!var4) {
         String var19 = this.text;
         switch (var19) {
            case "wander":
               if (this.vec3d3 != null) {
                  if (method7951(this.class3.doubleValue3, this.class3.doubleValue5, this.vec3d3.x, this.vec3d3.z) < 0.4) {
                     this.intValue12 = Math.min(this.intValue12, 20);
                  }

                  return this.vec3d3;
               }
               break;
            case "orbit":
               this.doubleValue2 += 0.055;
               double var11 = 1.7 + 0.2 * Math.sin(this.ticks * 0.07);
               return new Vec3d(var2.x + Math.cos(this.doubleValue2) * var11, var2.y, var2.z + Math.sin(this.doubleValue2) * var11);
            case "shoulder":
            case "head":
               return var2;
            case "sit":
            case "lie":
            case "play":
               if (!(var7 > 3.5)) {
                  return new Vec3d(this.class3.doubleValue3, var2.y, this.class3.doubleValue5);
               }

               this.method7930();
         }

         if (var5) {
            double var20 = Math.toRadians(this.floatValue5);
            double var22 = -Math.sin(var20);
            double var13 = Math.cos(var20);
            double var15 = -Math.cos(var20);
            double var17 = -Math.sin(var20);
            return var6 && this.method7931().contains("pace")
               ? new Vec3d(var2.x + var15 * 1.15 * this.intValue2 + var22 * 0.35, var2.y, var2.z + var17 * 1.15 * this.intValue2 + var13 * 0.35)
               : new Vec3d(var2.x - var22 * 1.3 + var15 * 0.9 * this.intValue2, var2.y, var2.z - var13 * 1.3 + var17 * 0.9 * this.intValue2);
         } else {
            return this.method7938(var2, var7, 0.9, 3.0);
         }
      } else {
         Vec3d var9 = var3 != null && var3.isAlive() ? var3.getEntityPos() : null;
         double var10 = 2.2;
         if (var9 != null) {
            double var12 = var2.x - var9.x;
            double var14 = var2.z - var9.z;
            double var16 = Math.max(0.001, Math.sqrt(var12 * var12 + var14 * var14));
            return new Vec3d(var2.x + var12 / var16 * var10, var2.y, var2.z + var14 / var16 * var10);
         } else {
            return this.method7938(var2, var7, 1.6, 2.4);
         }
      }
   }

   private Vec3d method7938(Vec3d var1, double var2, double var4, double var6) {
      if (var2 >= var4 && var2 <= var6) {
         return new Vec3d(this.class3.doubleValue3, var1.y, this.class3.doubleValue5);
      } else {
         double var8 = this.class3.doubleValue3 - var1.x;
         double var10 = this.class3.doubleValue5 - var1.z;
         double var12 = Math.sqrt(var8 * var8 + var10 * var10);
         if (var12 < 0.05) {
            double var14 = Math.toRadians(this.floatValue5 + 90.0 * this.intValue2);
            var8 = -Math.sin(var14);
            var10 = Math.cos(var14);
            var12 = 1.0;
         }

         double var16 = (var4 + var6) / 2.0 * 0.85;
         return new Vec3d(var1.x + var8 / var12 * var16, var1.y, var1.z + var10 / var12 * var16);
      }
   }

   private Vec3d method7939(PlayerEntity var1, Vec3d var2) {
      double var3 = Math.toRadians(var1.bodyYaw);
      double var5 = var2.x + Math.sin(var3) * 1.25 - Math.cos(var3) * 0.9 * this.intValue2;
      double var7 = var2.z - Math.cos(var3) * 1.25 - Math.sin(var3) * 0.9 * this.intValue2;
      return new Vec3d(var5, var2.y + (this.class3.method423() ? this.data3.hover() : 0.0), var7);
   }

   private void method7940(PlayerEntity var1, Vec3d var2, float var3) {
      if (!this.flag3) {
         double var4 = this.class3.doubleValue3 - var2.x;
         double var6 = this.class3.doubleValue4 - var2.y;
         double var8 = this.class3.doubleValue5 - var2.z;
         double var10 = Math.sqrt(var4 * var4 + var6 * var6 + var8 * var8);
         if (this.intValue4 == 0 && var1.isSneaking() && var10 < 2.6 && this.animationState.method6540(this.class15, "happy", var3)) {
            this.method7948(ParticleTypes.HEART, 3, 0.35, 0.05);
            this.intValue4 = 120;
            this.intValue3 = Math.max(this.intValue3, 40);
         } else {
            if (this.intValue3 == 0 && var10 < 5.5) {
               Vec3d var12 = var1.getEyePos();
               Vec3d var13 = var1.getRotationVec(1.0F);
               Vec3d var14 = new Vec3d(
                     this.class3.doubleValue3 - var12.x,
                     this.class3.doubleValue4 + this.class3.doubleValue2 * 0.5 - var12.y,
                     this.class3.doubleValue5 - var12.z
                  )
                  .normalize();
               if (var13.dotProduct(var14) > 0.975 && this.animationState.method6540(this.class15, "react", var3)) {
                  this.intValue3 = 160;
               }
            }
         }
      }
   }

   private void method7941(float var1) {
      this.doubleValue = this.doubleValue + (this.class3.doubleValue13 - this.doubleValue) * 0.3;
      if (this.flag ? this.doubleValue < 0.3 : this.doubleValue > 0.85) {
         this.flag = !this.flag;
      }

      if (this.running ? this.doubleValue < 3.4 : this.doubleValue > 4.4) {
         this.running = !this.running;
      }

      this.running = this.running & this.flag;
      String var2;
      if (this.flag4) {
         var2 = "perch";
      } else if (this.flag3) {
         var2 = "sleep";
      } else if (this.text.equals("sit") && !this.flag) {
         var2 = "sit";
      } else if (this.text.equals("lie") && !this.flag) {
         var2 = "lie";
      } else if (this.class3.method423()) {
         var2 = !this.flag && !this.text.equals("orbit") ? "idle" : "fly";
      } else if (!this.class3.flag && Math.abs(this.class3.doubleValue10) > 0.12) {
         var2 = "jump";
      } else if (this.running) {
         var2 = "run";
      } else if (this.flag) {
         var2 = "walk";
      } else {
         var2 = "idle";
      }

      if (var2.equals("walk")) {
         this.animationState.setFloatValue6((float)MathHelper.clamp(this.doubleValue / 2.4, 0.55, 1.7));
      } else if (var2.equals("run")) {
         this.animationState.setFloatValue6((float)MathHelper.clamp(this.doubleValue / 5.2, 0.75, 1.6));
      } else if (var2.equals("fly")) {
         this.animationState.setFloatValue6((float)MathHelper.clamp(0.8 + this.doubleValue / 8.0, 0.8, 1.5));
      } else {
         this.animationState.setFloatValue6(1.0F);
      }

      this.animationState.method6539(this.class15, var2, var1);
      if (var2.equals("idle") && this.intValue6 == 0 && !this.animationState.isFlag()) {
         this.animationState.method6540(this.class15, "idle2", var1);
         this.intValue6 = 200 + this.random.nextInt(240);
      }
   }

   private void method7942() {
      if (this.flag3) {
         if (this.intValue7 == 0) {
            this.method7950(
               ParticleTypes.NOTE, this.class3.doubleValue3, this.class3.doubleValue4 + this.class3.doubleValue2 + 0.1, this.class3.doubleValue5, 0.3, 0.0, 0.0
            );
            this.intValue7 = 70 + this.random.nextInt(30);
         }
      }
   }

   private void method7943(PlayerEntity var1, Vec3d var2, boolean var3, float var4) {
      String var5 = this.data3.getText() == null ? "" : this.data3.getText();
      switch (var5) {
         case "hide_in_shell":
            if (var3 && !this.animationState.getCurrent().equals("hide")) {
               this.animationState.method6540(this.class15, "hide", var4);
            } else if (!var3 && this.animationState.getCurrent().equals("hide")) {
               this.animationState.method6541(var4);
            }
            break;
         case "ghost_phase":
            float var10 = this.intValue11 > 0 ? 0.35F + 0.25F * MathHelper.sin(this.ticks * 0.6F) : 0.85F;
            this.alpha = this.alpha + (var10 - this.alpha) * 0.2F;
            break;
         case "shadow_step":
            double var8 = method7951(this.class3.doubleValue3, this.class3.doubleValue5, var2.x, var2.z);
            if (var8 > 6.0 && this.intValue5 == 0) {
               this.method7948(ParticleTypes.LARGE_SMOKE, 8, 0.3, 0.02);
               this.class3.method425(this.method7939(var1, var2));
               this.method7948(ParticleTypes.SMOKE, 10, 0.3, 0.02);
               this.animationState.method6540(this.class15, "quirk", var4);
               this.intValue5 = 60;
            }
            break;
         case "ink_splash":
            if (var1.hurtTime == 9 && this.intValue5 == 0) {
               this.method7948(ParticleTypes.SQUID_INK, 12, 0.35, 0.08);
               this.animationState.method6540(this.class15, "quirk", var4);
               this.intValue5 = 40;
            }

            if (this.ticks % 30 == 0 && !this.flag3) {
               this.method7950(
                  ParticleTypes.BUBBLE_POP,
                  this.class3.doubleValue3,
                  this.class3.doubleValue4 + this.class3.doubleValue2,
                  this.class3.doubleValue5,
                  0.0,
                  0.02,
                  0.0
               );
            }
            break;
         default:
            this.method7944(var5, var1, var2, var3, var4);
      }
   }

   private void method7944(String var1, PlayerEntity var2, Vec3d var3, boolean var4, float var5) {
      if (this.intValue5 <= 0 && !this.flag3 && !var4 && !(this.class3.doubleValue13 > 3.0) && !var1.isEmpty()) {
         this.intValue5 = 260 + this.random.nextInt(260);
         boolean var6 = this.animationState.method6540(this.class15, "quirk", var5);
         switch (var1) {
            case "sneeze_sparks":
               this.method7947(ParticleTypes.FLAME, 6, 0.12);
               this.method7947(ParticleTypes.LAVA, 2, 0.05);
               break;
            case "smoke_rings":
               for (int var15 = 0; var15 < 10; var15++) {
                  double var17 = var15 / 10.0 * Math.PI * 2.0;
                  this.method7950(
                     ParticleTypes.SMOKE,
                     this.class3.doubleValue3 + Math.cos(var17) * 0.2,
                     this.class3.doubleValue4 + this.class3.doubleValue2 + 0.2,
                     this.class3.doubleValue5 + Math.sin(var17) * 0.2,
                     0.0,
                     0.03,
                     0.0
                  );
               }
               break;
            case "firefly_dance":
               for (int var14 = 0; var14 < 8; var14++) {
                  double var16 = var14 / 8.0 * Math.PI * 2.0;
                  this.method7950(
                     ParticleTypes.END_ROD,
                     this.class3.doubleValue3 + Math.cos(var16) * 0.6,
                     this.class3.doubleValue4 + 0.4,
                     this.class3.doubleValue5 + Math.sin(var16) * 0.6,
                     -Math.sin(var16) * 0.03,
                     0.02,
                     Math.cos(var16) * 0.03
                  );
               }
               break;
            case "scan_beam":
               for (int var13 = 0; var13 < 10; var13++) {
                  double var10 = Math.toRadians(this.class3.floatValue + 90.0F) + (var13 - 5) * 0.12;
                  this.method7950(
                     ParticleTypes.ELECTRIC_SPARK,
                     this.class3.doubleValue3 + Math.cos(var10) * 1.2,
                     this.class3.doubleValue4 + 0.2,
                     this.class3.doubleValue5 + Math.sin(var10) * 1.2,
                     0.0,
                     0.0,
                     0.0
                  );
               }
               break;
            case "ghost_phase":
               this.intValue11 = 60;
               break;
            case "spore_puff":
               this.method7948(ParticleTypes.SPORE_BLOSSOM_AIR, 10, 0.4, 0.01);
               this.method7948(ParticleTypes.MYCELIUM, 8, 0.3, 0.01);
            case "head_spin":
               break;
            case "slime_bounce":
               this.class3.method427(0.75);
               this.floatValue3 = 1.0F;
               this.method7948(ParticleTypes.ITEM_SLIME, 8, 0.3, 0.05);
               break;
            case "catch_stars":
               double var12 = this.random.nextDouble() * Math.PI * 2.0;
               this.vec3d2 = new Vec3d(var3.x + Math.cos(var12) * 2.2, var3.y, var3.z + Math.sin(var12) * 2.2);
               this.intValue11 = 40;

               for (int var11 = 0; var11 < 6; var11++) {
                  this.method7950(ParticleTypes.END_ROD, this.vec3d2.x, this.vec3d2.y + 1.6 + var11 * 0.2, this.vec3d2.z, 0.0, -0.08, 0.0);
               }
               break;
            case "offer_flower":
               this.vec3d2 = new Vec3d(var3.x + (this.class3.doubleValue3 - var3.x) * 0.3, var3.y, var3.z + (this.class3.doubleValue5 - var3.z) * 0.3);
               this.intValue11 = 50;
               this.method7948(ParticleTypes.HAPPY_VILLAGER, 4, 0.3, 0.02);
               break;
            case "bear_hug":
               this.vec3d2 = new Vec3d(var3.x + (this.class3.doubleValue3 - var3.x) * 0.2, var3.y, var3.z + (this.class3.doubleValue5 - var3.z) * 0.2);
               this.intValue11 = 55;
               this.method7947(ParticleTypes.HEART, 3, 0.15);
               break;
            case "spooky_grin":
               this.method7947(ParticleTypes.SOUL_FIRE_FLAME, 6, 0.08);
               break;
            case "rain_cloud":
               for (int var9 = 0; var9 < 12; var9++) {
                  this.method7950(
                     ParticleTypes.FALLING_WATER,
                     this.class3.doubleValue3 + (this.random.nextDouble() - 0.5) * 0.5,
                     this.class3.doubleValue4 + this.class3.doubleValue2 + 0.3,
                     this.class3.doubleValue5 + (this.random.nextDouble() - 0.5) * 0.5,
                     0.0,
                     0.0,
                     0.0
                  );
               }
               break;
            default:
               if (!var6) {
                  this.intValue5 = 400;
               }
         }
      }
   }

   public void method7945(float var1, float[] var2) {
      if (Util19.enabled()
         && mc.player != null
         && !(mc.player.squaredDistanceTo(this.class3.doubleValue3, this.class3.doubleValue4, this.class3.doubleValue5) > 1600.0)) {
         if (this.data3.getParticleEffect() != null && this.class3.doubleValue13 > 1.2) {
            this.floatValue4 += var1 * 8.0F;

            while (this.floatValue4 >= 1.0F) {
               this.floatValue4--;
               this.method7950(
                  this.data3.getParticleEffect(),
                  this.class3.doubleValue3 + (this.random.nextDouble() - 0.5) * 0.2,
                  this.class3.doubleValue4 + 0.1,
                  this.class3.doubleValue5 + (this.random.nextDouble() - 0.5) * 0.2,
                  0.0,
                  0.01,
                  0.0
               );
            }
         }

         if (!this.class15.items2.isEmpty()) {
            if (this.floatValues.length != this.class15.items2.size()) {
               this.floatValues = new float[this.class15.items2.size()];
            }

            for (int var3 = 0; var3 < this.class15.items2.size(); var3++) {
               Class15.Data4 var4 = this.class15.items2.get(var3);

               boolean var5 = switch (var4.getType3()) {
                  case MOVING -> this.class3.doubleValue13 > 0.6;
                  case IDLE -> this.class3.doubleValue13 <= 0.6;
                  case AIRBORNE -> !this.class3.flag;
                  default -> true;
               };
               if (var5 && !this.flag3) {
                  this.floatValues[var3] = this.floatValues[var3] + var4.getFloatValue4() * var1;

                  while (this.floatValues[var3] >= 1.0F) {
                     this.floatValues[var3]--;
                     Vec3d var6 = WardrobePreview.method3148(this, var2, var4.getIntValue(), var4.getFloatValue(), var4.getFloatValue2(), var4.getFloatValue3());
                     double var7 = var4.getFloatValue8();
                     this.method7950(
                        var4.getParticleEffect(),
                        var6.x + (this.random.nextDouble() - 0.5) * var7,
                        var6.y + (this.random.nextDouble() - 0.5) * var7,
                        var6.z + (this.random.nextDouble() - 0.5) * var7,
                        var4.getFloatValue5(),
                        var4.getFloatValue6(),
                        var4.getFloatValue7()
                     );
                  }
               }
            }
         }
      }
   }

   private boolean method7946(Vec3d var1, double var2) {
      if (mc.world == null) {
         this.flag2 = false;
         return false;
      } else {
         if (this.ticks >= this.intValue9) {
            this.intValue9 = this.ticks + 10;
            this.flag2 = !mc.world.getEntitiesByClass(HostileEntity.class, new Box(var1, var1).expand(var2), LivingEntity::isAlive).isEmpty();
         }

         return this.flag2;
      }
   }

   private void method7947(ParticleEffect var1, int var2, double var3) {
      double var5 = Math.toRadians(this.class3.floatValue + 90.0);
      double var7 = this.class3.doubleValue3 + Math.cos(var5) * 0.25;
      double var9 = this.class3.doubleValue5 + Math.sin(var5) * 0.25;

      for (int var11 = 0; var11 < var2; var11++) {
         this.method7950(
            var1,
            var7,
            this.class3.doubleValue4 + this.class3.doubleValue2 * 0.6,
            var9,
            Math.cos(var5) * var3 + (this.random.nextDouble() - 0.5) * var3,
            this.random.nextDouble() * var3 * 0.5,
            Math.sin(var5) * var3 + (this.random.nextDouble() - 0.5) * var3
         );
      }
   }

   private void method7948(ParticleEffect var1, int var2, double var3, double var5) {
      for (int var7 = 0; var7 < var2; var7++) {
         this.method7950(
            var1,
            this.class3.doubleValue3 + (this.random.nextDouble() - 0.5) * var3,
            this.class3.doubleValue4 + this.class3.doubleValue2 * 0.5 + (this.random.nextDouble() - 0.5) * var3,
            this.class3.doubleValue5 + (this.random.nextDouble() - 0.5) * var3,
            (this.random.nextDouble() - 0.5) * var5,
            this.random.nextDouble() * var5,
            (this.random.nextDouble() - 0.5) * var5
         );
      }
   }

   private void method7949(double var1, double var3, double var5) {
      for (int var7 = 0; var7 < 8; var7++) {
         this.method7950(
            ParticleTypes.POOF,
            var1 + (this.random.nextDouble() - 0.5) * 0.4,
            var3 + this.random.nextDouble() * 0.4,
            var5 + (this.random.nextDouble() - 0.5) * 0.4,
            0.0,
            0.02,
            0.0
         );
      }
   }

   private void method7950(ParticleEffect var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      if (Util19.enabled()) {
         Util19.method5043(var1, var2, var4, var6, var8, var10, var12);
      }
   }

   private static double method7951(double var0, double var2, double var4, double var6) {
      double var8 = var0 - var4;
      double var10 = var2 - var6;
      return Math.sqrt(var8 * var8 + var10 * var10);
   }
}

package org.zenith.base.rotation.mods;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.Rotation;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.Util22;
import org.zenith.utility.misc.Vec2f;

public class Class26 extends Util22 {
   private Random random = new Random();
   private boolean flag;
   private boolean flag2;
   private float floatValue = 1.0F;
   private long longValue;
   private int intValue;
   private Class26.Entry2 entry2 = new Class26.Entry2();
   private float floatValue2 = 1.0F;
   private long longValue2 = System.currentTimeMillis();
   private long longValue3 = System.currentTimeMillis();
   private int intValue2 = Integer.MIN_VALUE;
   private boolean flag3;
   private boolean flag4;
   private int intValue3 = Integer.MIN_VALUE;
   private float floatValue3;
   private float floatValue4;
   private float floatValue5;
   private float floatValue6;
   private float floatValue7;
   private float floatValue8;
   private float floatValue9;
   private float floatValue10;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private float floatValue11;
   private float floatValue12;
   private long longValue4;
   private int intValue4;
   private boolean flag5;
   private int intValue5;
   private int intValue6;
   private int intValue7;
   private float floatValue13;
   private float floatValue14;
   private float floatValue15;
   private SecureRandom secureRandom = new SecureRandom();
   private boolean flag6;
   private float floatValue16;
   private float floatValue17;
   private float floatValue18;
   private int intValue8 = Integer.MIN_VALUE;
   private int intValue9 = Integer.MIN_VALUE;
   private long longValue5;
   private long longValue6;
   private long longValue7;
   private float floatValue19;
   private float floatValue20;
   private float floatValue21 = 1.0F;
   private float floatValue22 = 1.0F;
   private float floatValue23;
   private float floatValue24;
   private int intValue10 = Integer.MIN_VALUE;
   private Vec3d vec3d = Vec3d.ZERO;
   private long longValue8;
   private float floatValue25;
   private float floatValue26;
   private int intValue11 = Integer.MIN_VALUE;
   private Vec3d vec3d2 = Vec3d.ZERO;
   private long longValue9;
   private float floatValue27;
   private float floatValue28;
   private int intValue12 = 1;
   private int intValue13 = Integer.MIN_VALUE;
   private long longValue10;
   private long longValue11;
   private long longValue12;
   private Rotation rotation = Rotation.INSTANCE;
   private Rotation rotation2 = Rotation.INSTANCE;
   private float floatValue29;
   private int intValue14 = Integer.MIN_VALUE;
   private Rotation rotation3 = Rotation.INSTANCE;
   private boolean flag7;
   private float floatValue30;
   private int intValue15 = Integer.MIN_VALUE;
   private float floatValue31;
   private float floatValue32;
   private float floatValue33;
   private float floatValue34;
   private int intValue16 = Integer.MIN_VALUE;
   private int intValue17;
   private int intValue18;
   private float floatValue35;
   private float floatValue36;
   private int intValue19 = Integer.MIN_VALUE;
   private long longValue13;
   private float floatValue37;
   private float floatValue38;
   private int intValue20 = Integer.MIN_VALUE;
   private float floatValue39;
   private float floatValue40;
   private float floatValue41;
   private float floatValue42;
   private float floatValue43;
   private float floatValue44;
   private float floatValue45;
   private float floatValue46;
   private double doubleValue4;
   private double doubleValue5;
   private double doubleValue6;
   private float floatValue47;
   private float floatValue48;
   private long longValue14;
   private int intValue21;
   private boolean flag8;
   private int intValue22;
   private int intValue23;
   private int intValue24;
   private float floatValue49;
   private Class26.Entry3[] entry3s = new Class26.Entry3[12];
   private Class26.Entry[] entrys = new Class26.Entry[10];
   public int intValue25 = 0;

   public Rotation method3747(Rotation var1) {
      if (this.secureRandom == null) {
         this.secureRandom = new SecureRandom();
         this.random = new Random();
         this.entry2 = new Class26.Entry2();
      }

      Rotation var2 = rotationManager.method7844();
      LivingEntity var3 = AuraModule.INSTANCE.method6358();
      if (mc.player != null && var3 != null && var3.isAlive()) {
         if (var3.getId() != this.intValue2) {
            this.method3834();
            this.intValue2 = var3.getId();
         }

         if (AuraModule.INSTANCE.isFlag26() || AuraModule.INSTANCE.isFlag9()) {
            this.longValue3 = System.currentTimeMillis();
         }

         if (mc.player.age % 100 == 0) {
            this.intValue25++;
            if (this.intValue25 > 50) {
               this.intValue25 = 41;
            }
         }

         int var4 = this.intValue25;

         return switch (var4) {
            case 0 -> this.method3748(var3, var2);
            case 1 -> this.method3749(var3);
            case 2 -> this.method3750(var3);
            case 3 -> this.method3751(var3, var2);
            case 4 -> this.method3752(var3, var2);
            case 5 -> this.method3753(var3);
            case 6 -> this.method3754(var3);
            case 7 -> this.method3755(var3);
            case 8 -> this.method3756(var3, var2);
            case 9 -> this.method3757(var3, var2);
            case 10 -> this.method3758(var3, var2);
            case 11 -> this.method3759(var3, var2);
            case 12 -> this.method3760(var3, var2);
            case 13 -> this.method3761(var3, var2);
            case 14 -> this.method3762(var3, var2);
            case 15 -> this.method3763(var3, var2);
            case 16 -> this.method3764(var3);
            case 17 -> this.method3765(var3, var2);
            case 18 -> this.method3766(var3);
            case 19 -> this.method3767(var3, var2);
            case 20 -> this.method3768(var3, var2);
            case 21 -> this.method3769(var3, var2);
            case 22 -> this.method3770(var3, var2);
            case 23 -> this.method3771(var3, var2);
            case 24 -> this.method3772(var3, var2);
            case 25 -> this.method3773(var3, var2);
            case 26 -> this.method3774(var3, var2);
            case 27 -> this.method3775(var3, var2);
            case 28 -> this.method3781(var3, var2);
            case 29 -> this.method3788(0, var3, var2);
            case 30 -> this.method3788(1, var3, var2);
            case 31 -> this.method3788(2, var3, var2);
            case 32 -> this.method3788(3, var3, var2);
            case 33 -> this.method3788(4, var3, var2);
            case 34 -> this.method3788(5, var3, var2);
            case 35 -> this.method3788(6, var3, var2);
            case 36 -> this.method3788(7, var3, var2);
            case 37 -> this.method3788(8, var3, var2);
            case 38 -> this.method3788(9, var3, var2);
            case 39 -> this.method3788(10, var3, var2);
            case 40 -> this.method3788(11, var3, var2);
            case 41 -> this.method3799(0, var3, var2);
            case 42 -> this.method3799(1, var3, var2);
            case 43 -> this.method3799(2, var3, var2);
            case 44 -> this.method3799(3, var3, var2);
            case 45 -> this.method3799(4, var3, var2);
            case 46 -> this.method3799(5, var3, var2);
            case 47 -> this.method3799(6, var3, var2);
            case 48 -> this.method3799(7, var3, var2);
            case 49 -> this.method3799(8, var3, var2);
            case 50 -> this.method3799(9, var3, var2);
            default -> var1 != null && !var1.method3137() ? var1 : var2;
         };
      } else {
         this.method3834();
         return var2;
      }
   }

   private Rotation method3748(LivingEntity var1, Rotation var2) {
      Vec3d var3 = var1.getEntityPos().add(0.0, var1.getHeight() * 0.9, 0.0).subtract(mc.player.getEyePos());
      float var4 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var3.z, var3.x)) - 90.0);
      float var5 = (float)(-Math.toDegrees(Math.atan2(var3.y, Math.sqrt(var3.x * var3.x + var3.z * var3.z))));
      boolean var6 = this.flag;
      this.flag = TargetUtils.method3209(16.0, new Rotation(var4, var5), var1);
      if (this.flag != var6) {
         if (this.flag) {
            this.floatValue = 0.2F;
            this.flag2 = false;
         } else {
            this.flag2 = true;
            this.longValue = System.currentTimeMillis();
            this.intValue = 100 + this.random.nextInt(21);
         }
      }

      if (this.flag2 && System.currentTimeMillis() - this.longValue >= this.intValue) {
         float var7 = Math.min((float)(System.currentTimeMillis() - this.longValue - this.intValue) / 200.0F, 1.0F);
         this.floatValue = 0.2F + var7;
         if (var7 >= 1.0F) {
            this.flag2 = false;
            this.floatValue = 1.0F;
         }
      }

      float var21 = MathHelper.wrapDegrees(var4 - var2.getFloatValue());
      float var8 = MathHelper.wrapDegrees(var5 - var2.getFloatValue2());
      float var9 = Math.min(Math.max(Math.abs(var21), 1.0F), this.method3831(Math.abs(var21), 120.0F * this.floatValue));
      float var10 = Math.min(Math.max(Math.abs(var8), 1.0F), this.method3831(Math.abs(var8), 52.5F * this.floatValue));
      float var11 = this.method3832(Math.abs(var21));
      var9 *= var11;
      var10 *= var11;
      float var12 = var2.getFloatValue() + (var21 > 0.0F ? var9 : -var9);
      float var13 = MathHelper.clamp(var2.getFloatValue2() + (var8 > 0.0F ? var10 : -var10), -89.0F, 89.0F);
      float var14 = mc.player.age * 0.8F;
      float var15 = 1.8F + (float)Math.sin(var14 * 0.4F) * 2.2F;
      var12 += (float)Math.sin(var14 * 1.3F) * var15 * 0.9F;
      var13 += (float)Math.cos(var14 * 1.1F) * var15 * 0.7F;
      float var16 = this.flag ? 0.1F : 0.3F;
      if (mc.player.age % 2 == 0) {
         var12 += (this.random.nextFloat() - 0.5F) * var16;
         var13 += (this.random.nextFloat() - 0.5F) * var16 * 0.6F;
      }

      var12 += (float)Math.sin(var14 * 2.1F) * 0.008F;
      var13 += (float)Math.cos(var14 * 1.8F) * 0.015F;
      float var17 = Rotation.method3138();
      float var18 = 1.85F + this.random.nextFloat() * 0.45F;
      var12 -= (var12 - var2.getFloatValue()) % (var17 * var18);
      var13 -= (var13 - var2.getFloatValue2()) % (var17 * var18);
      float var19 = 32.0F + (float)Math.sin(var14 * 0.7F) * 6.0F;
      float var20 = 25.0F + (float)Math.cos(var14 * 0.8F) * 5.0F;
      var12 = var2.getFloatValue() + MathHelper.clamp(var12 - var2.getFloatValue(), -var19, var19);
      var13 = MathHelper.clamp(var2.getFloatValue2() + MathHelper.clamp(var13 - var2.getFloatValue2(), -var20, var20), -89.0F, 89.0F);
      return new Rotation(var12, var13);
   }

   private Rotation method3749(LivingEntity var1) {
      return this.method3826(this.method3824(var1));
   }

   private Rotation method3750(LivingEntity var1) {
      return this.method3826(var1.getEntityPos().add(0.0, var1.getHeight() / 2.0F, 0.0));
   }

   private Rotation method3751(LivingEntity var1, Rotation var2) {
      this.method3828();
      Rotation var3 = this.method3826(this.method3825(var1));
      boolean var4 = System.currentTimeMillis() - this.longValue3 >= 300L;
      float var5 = var3.getFloatValue();
      float var6 = var3.getFloatValue2();
      if (var4) {
         if (mc.player.isSprinting()) {
            var5 += 5.0F;
            var6 -= 10.0F;
         } else {
            var5 -= 5.0F;
         }
      }

      long var7 = System.currentTimeMillis() - this.longValue2;
      var5 += (float)this.entry2.method3835(var7 * 5.0E-4) * 25.0F * this.floatValue2;
      var6 += (float)this.entry2.method3836(var7 * 5.0E-4, 10.0) * 25.0F * this.floatValue2;
      this.method3829(var2, var3);
      return new Rotation(var5, var6);
   }

   private Rotation method3752(LivingEntity var1, Rotation var2) {
      Box var3 = var1.getBoundingBox();
      double var4 = this.method3830((float)Math.cos(System.currentTimeMillis() / 1000.0)) * 0.15;
      double var6 = this.method3830((float)Math.cos(System.currentTimeMillis() / 10000.0)) * 0.15;
      double var8 = this.method3830((float)Math.cos(System.currentTimeMillis() / 1000.0)) * 0.15;
      Vec3d var10 = this.method3825(var1);
      Rotation var11 = this.method3826(
         new Vec3d(
            MathHelper.clamp(var10.x + var4, var3.minX, var3.maxX),
            var10.y + var1.getHeight() / 2.0F + var6,
            MathHelper.clamp(var10.z + var8, var3.minZ, var3.maxZ)
         )
      );
      float var12 = var2.getFloatValue2();
      if (!TargetUtils.method3209(16.0, new Rotation(var11.getFloatValue(), var12), var1)) {
         var12 = this.method3826(var10).getFloatValue2();
      }

      return new Rotation(var11.getFloatValue(), var12);
   }

   private Rotation method3753(LivingEntity var1) {
      return this.method3826(var1.getBoundingBox().getCenter());
   }

   private Rotation method3754(LivingEntity var1) {
      Box var2 = var1.getBoundingBox();
      Vec3d var3 = new Vec3d(
         MathHelper.lerp(this.random.nextDouble(), var2.minX, var2.maxX),
         MathHelper.lerp(this.random.nextDouble(), var2.minY, var2.maxY),
         MathHelper.lerp(this.random.nextDouble(), var2.minZ, var2.maxZ)
      );
      double var4 = System.currentTimeMillis() / 500.0;
      return this.method3826(var3.add(Math.sin(var4) * 0.15, Math.cos(var4 * 1.2) * 0.2, Math.cos(var4 * 0.8) * 0.15));
   }

   private Rotation method3755(LivingEntity var1) {
      Vec3d var2 = this.method3824(var1);
      if (mc.player.getEyePos().distanceTo(var1.getEyePos()) > 3.0) {
         var2 = var1.getEntityPos().add(0.0, var1.getEyeHeight(var1.getPose()), 0.0);
      }

      return this.method3826(var2);
   }

   private Rotation method3756(LivingEntity var1, Rotation var2) {
      float var3 = Rotation.method3138();
      double var4 = Math.sin(System.currentTimeMillis() / 150.0) * 0.15;
      double var6 = Math.cos(System.currentTimeMillis() / 150.0) * 0.15;
      Vec3d var8 = var1.getEntityPos().add(0.0, MathHelper.clamp(mc.player.getEyeY() - var1.getY(), 0.0, var1.getHeight() * 0.8), 0.0).add(var4, var6, var4);
      Vec2f var9 = var2.method3131(this.method3826(var8));
      float var10 = (float)Math.hypot(var9.getFloatValue(), var9.getFloatValue2());
      float var11 = MathHelper.clamp(var10 / 18.0F, 0.2F, 0.95F);
      float var12 = var2.getFloatValue() + Math.round(var9.getFloatValue() * var11 / var3) * var3;
      float var13 = var2.getFloatValue2() + Math.round(var9.getFloatValue2() * var11 / var3) * var3;
      return new Rotation(var12, var13);
   }

   private Rotation method3757(LivingEntity var1, Rotation var2) {
      this.method3828();
      Rotation var3 = this.method3826(this.method3825(var1));
      boolean var4 = System.currentTimeMillis() - this.longValue3 >= 300L;
      float var5 = var3.getFloatValue();
      float var6 = var3.getFloatValue2();
      Vec2f var7 = var2.method3131(var3);
      if (var4) {
         if (mc.player.isSprinting()) {
            var5 += 5.0F;
            var6 -= 10.0F;
         } else {
            var5 -= 5.0F;
         }
      }

      float var8 = Math.max(
            (90.0F - Math.abs(var7.getFloatValue())) / (var4 ? (mc.player.fallDistance > 0.0 ? 20.0F : 60.0F) : 40.0F), this.method3833(1.0F, 5.0F)
         )
         * this.method3833(0.9F, 1.1F);
      float var9 = Math.abs(var7.getFloatValue2()) / (var4 ? (mc.player.fallDistance > 0.0 ? 60.0F : 100.0F) : 30.0F) * this.method3833(0.9F, 1.1F);
      return this.method3827(var2, new Rotation(var5, var6), var8 * 25.0F, var9 * 25.0F);
   }

   private Rotation method3758(LivingEntity var1, Rotation var2) {
      this.method3828();
      boolean var3 = var1.getBoundingBox().expand(1.0).intersects(mc.player.getBoundingBox());
      Vec3d var4 = this.method3824(var1);
      Rotation var5 = this.method3826(this.method3825(var1));
      Rotation var6 = this.method3826(var4);
      if (var3) {
         var5 = this.method3826(var1.getEntityPos().add(0.0, 0.5, 0.0));
      }

      if (!TargetUtils.method3209(16.0, var5, var1) && mc.player.getEyePos().distanceTo(var1.getEyePos()) > 3.0) {
         var5 = new Rotation(var5.getFloatValue(), var6.getFloatValue2() + 10.0F);
      }

      boolean var7 = System.currentTimeMillis() - this.longValue3 >= (var3 ? 500L : 200L);
      float var8 = var5.getFloatValue();
      float var9 = var5.getFloatValue2();
      Vec2f var10 = var2.method3131(var5);
      boolean var11 = mc.world.getBlockState(mc.player.getBlockPos().up(2)).isAir();
      if (!mc.player.isSprinting() && var7 && var11) {
         var8 += 10.0F;
         var9 -= 20.0F;
      }

      float var12 = Math.max(
            (90.0F - Math.abs(var10.getFloatValue()))
               / (!mc.player.isSprinting() && var7 ? 5.0F : (var7 ? (mc.player.fallDistance > 0.0 ? 25.0F : 10.0F) : 60.0F)),
            this.method3833(1.0F, 5.0F)
         )
         * this.method3833(0.9F, 1.1F);
      float var13 = Math.abs(var10.getFloatValue2())
         / (!mc.player.isSprinting() && var7 ? 5.0F : (var7 ? (mc.player.fallDistance > 0.0 ? 25.0F : 10.0F) : 40.0F))
         * this.method3833(0.9F, 1.1F);
      long var14 = System.currentTimeMillis() - this.longValue2;
      var8 += (float)this.entry2.method3835(var14 * 5.0E-4) * 25.0F * this.floatValue2;
      var9 += (float)this.entry2.method3836(var14 * 5.0E-4, 10.0) * 25.0F * this.floatValue2;
      this.method3829(var2, var5);
      return this.method3827(var2, new Rotation(var8, var9), var12 * 25.0F, var13 * 25.0F);
   }

   private Rotation method3759(LivingEntity var1, Rotation var2) {
      Rotation var3 = this.method3813(var1.getEntityPos().add(0.0, var1.getHeight() - 0.2, 0.0));
      Vec2f var4 = var2.method3131(var3);
      float var5 = Math.min(Math.max(Math.abs(var4.getFloatValue()), 0.0F), 150.0F);
      float var6 = Math.max(Math.abs(var4.getFloatValue2()), 0.0F);
      float var7 = var2.getFloatValue() + (var4.getFloatValue() > 0.0F ? var5 : -var5);
      float var8 = MathHelper.clamp(var2.getFloatValue2() + (var4.getFloatValue2() > 0.0F ? var6 : -var6), -89.0F, 89.0F);
      float var9 = Rotation.method3138();
      var7 -= (var7 - var2.getFloatValue()) % var9;
      var8 -= (var8 - var2.getFloatValue2()) % var9;
      return new Rotation(var7, var8);
   }

   private Rotation method3760(LivingEntity var1, Rotation var2) {
      Rotation var3 = this.method3813(var1.getEntityPos().add(0.0, var1.getHeight() - 0.2, 0.0));
      Vec2f var4 = var2.method3131(var3);
      float var5 = mc.player.ticksSinceLastAttack;
      boolean var6 = var5 > 0.0F && var5 < 0.7F;
      boolean var7 = var5 >= 0.7F && var5 < 1.0F;
      if ((var6 || var7) && !this.flag3) {
         this.flag3 = true;
      } else if (!var6 && !var7 && this.flag3) {
         this.flag4 = true;
         this.flag3 = false;
      }

      if (this.flag4) {
         this.flag4 = false;
      }

      float var8;
      float var9;
      float var10;
      float var11;
      if (var7) {
         var8 = 0.4F;
         var9 = 175.0F;
         var10 = 8.0F;
         var11 = 0.01F;
      } else if (var6) {
         var8 = 0.5F;
         var9 = 70.0F;
         var10 = 65.0F;
         var11 = 0.09F;
      } else {
         var8 = 0.8F;
         var9 = 45.0F;
         var10 = 40.0F;
         var11 = 0.15F;
      }

      float var12 = MathHelper.clamp(Math.abs(var4.getFloatValue()), 0.3F, var9);
      float var13 = MathHelper.clamp(Math.abs(var4.getFloatValue2()), 0.25F, var10);
      if (Math.abs(var4.getFloatValue()) < 5.0F && !var7) {
         var12 *= 0.8F + 0.05F * (float)Math.sin(0.0);
      }

      if (var7 || var6) {
         float var14 = 2.0F - var5 * var5;
         var12 *= var14;
         var13 *= var14;
      }

      float var15;
      float var17;
      if (var7) {
         var17 = var2.getFloatValue();
         var15 = MathHelper.clamp(var2.getFloatValue2() + (int)var4.getFloatValue(), -60.0F, 60.0F);
      } else {
         var17 = var2.getFloatValue() + (var4.getFloatValue() > 0.0F ? var12 : -var12) * (1.0F - var11 * (float)Math.abs(Math.sin(0.0)));
         var15 = MathHelper.clamp(var2.getFloatValue2() + (var4.getFloatValue2() > 0.0F ? var13 : -var13) - 12.0F, -85.0F, 85.0F + var13);
      }

      if (!var7 && !var6 && !this.flag4) {
         var17 = (float)(var17 + (Math.random() - 0.5) * 0.2);
         var15 = (float)(var15 + (Math.random() - 0.5) * 0.1);
      }

      float var16 = Rotation.method3138() * (!var7 && !var6 ? 1.0F : 0.7F);
      var17 -= (var17 - var2.getFloatValue()) % var16;
      var15 -= (var15 - var2.getFloatValue2()) % var16;
      return new Rotation(var17 * var8 + var2.getFloatValue() * (1.0F - var8), var15 * var8 + var2.getFloatValue2() * (1.0F - var8));
   }

   private Rotation method3761(LivingEntity var1, Rotation var2) {
      if (this.intValue3 != var1.getId()) {
         this.intValue3 = var1.getId();
         this.floatValue3 = var2.getFloatValue();
         this.floatValue4 = var2.getFloatValue2();
         this.floatValue9 = this.floatValue7 = this.floatValue3;
         this.floatValue10 = this.floatValue8 = this.floatValue4;
         this.floatValue5 = this.floatValue6 = 0.0F;
         this.method3814(var1);
         this.floatValue11 = (float)(Math.random() * Math.PI * 2.0);
         this.floatValue12 = (float)(Math.random() * Math.PI * 2.0);
         float var3 = var2.method3130(this.method3826(var1.getBoundingBox().getCenter()));
         this.intValue4 = var3 > 130.0F
            ? 160 + this.random.nextInt(70)
            : (var3 > 70.0F ? 100 + this.random.nextInt(55) : (var3 > 30.0F ? 55 + this.random.nextInt(30) : 18 + this.random.nextInt(16)));
         this.longValue4 = System.currentTimeMillis();
         this.flag5 = false;
         this.intValue5 = this.intValue6 = 0;
         this.intValue7 = var1.hurtTime;
      }

      float var19 = this.method3817();
      if (!this.flag5) {
         long var4 = System.currentTimeMillis() - this.longValue4;
         if (var4 < this.intValue4) {
            float var21 = (float)var4 / Math.max(this.intValue4, 1);
            float var22 = 1.0F - this.method3821(var21) * 0.4F;
            float var25 = this.floatValue9 + (this.random.nextFloat() - 0.5F) * 0.3F * var22;
            float var27 = MathHelper.clamp(this.floatValue10 + (this.random.nextFloat() - 0.5F) * 0.19F * var22, -89.0F, 89.0F);
            var25 -= (var25 - this.floatValue9) % var19;
            var27 -= (var27 - this.floatValue10) % var19;
            this.floatValue9 = var25;
            this.floatValue10 = var27;
            return new Rotation(var25, var27);
         }

         this.flag5 = true;
      }

      Vec3d var20 = mc.player.getEyePos();
      Vec3d var5 = var1.getBoundingBox().getCenter();
      float var6 = (float)var20.distanceTo(var5);
      float[] var7 = this.method3815(var6);
      if (var1.hurtTime > this.intValue7 && var1.hurtTime > 0) {
         this.intValue5 = 1;
         this.intValue6 = 0;
         this.floatValue13 = this.floatValue4;
      }

      this.intValue7 = var1.hurtTime;
      if (this.intValue5 > 0) {
         this.intValue6++;
         if (this.intValue5 == 1) {
            float var23 = this.method3820(MathHelper.clamp(this.intValue6 / 18.0F, 0.0F, 1.0F));
            this.floatValue4 = MathHelper.lerp(var23, this.floatValue13, -88.0F);
            if (this.intValue6 >= 18) {
               this.intValue5 = 2;
               this.intValue6 = 0;
            }
         } else {
            float var24 = this.method3821(MathHelper.clamp(this.intValue6 / 26.0F, 0.0F, 1.0F));
            this.floatValue4 = MathHelper.lerp(var24, -88.0F, this.floatValue13);
            if (this.intValue6 >= 26) {
               this.intValue5 = this.intValue6 = 0;
            }
         }

         return this.method3816(this.floatValue3 + var7[0], this.floatValue4 + var7[1], var19);
      } else {
         if (Math.random() < 0.018) {
            this.method3814(var1);
         }

         Vec3d var8 = new Vec3d(var1.getX() - var1.lastX, var1.getY() - var1.lastY, var1.getZ() - var1.lastZ);
         int var9 = !mc.player.isGliding() && !var1.isGliding() ? 2 : 0;
         Vec3d var10 = var5.add(var8.multiply(var9)).add(this.doubleValue, this.doubleValue2, this.doubleValue3);
         Rotation var11 = this.method3826(var10);
         float var12 = MathHelper.wrapDegrees(var11.getFloatValue() - this.floatValue3);
         float var13 = var11.getFloatValue2() - this.floatValue4;
         float var14 = 1.0F;
         if (mc.player.isGliding()) {
            float var15 = Math.abs(var12) + Math.abs(var13);
            if (var15 > 120.0F) {
               var14 = 0.22F;
            } else if (var15 > 80.0F) {
               var14 = MathHelper.lerp(this.method3821((var15 - 80.0F) / 40.0F), 0.4F, 0.22F);
            } else if (var15 > 25.0F) {
               var14 = MathHelper.lerp(this.method3821((var15 - 25.0F) / 55.0F), 0.7F, 0.4F);
            } else {
               var14 = 0.7F + 0.3F * (1.0F - var15 / 25.0F);
            }
         } else if (mc.options.forwardKey.isPressed() || this.method3818(var1)) {
            var14 = 0.55F;
         }

         float var29 = (0.045F + this.random.nextFloat() * 0.011F) * var14;
         float var16 = 0.72F + 0.1F * (1.0F - var14);
         float var17 = (float)Math.sqrt(var12 * var12 + var13 * var13);
         if (var17 > 34.0F) {
            var29 += 0.021F * var14;
         } else if (var17 < 3.8F) {
            var29 *= 0.42F;
         }

         var29 += MathHelper.clamp((var6 - 1.5F) / 8.0F, 0.0F, 0.05F) * var14;
         this.floatValue5 = this.floatValue5 + (var12 * var29 - this.floatValue5 * var16);
         this.floatValue6 = this.floatValue6 + (var13 * var29 * 0.92F - this.floatValue6 * var16);
         this.floatValue5 = MathHelper.clamp(this.floatValue5, -8.2F * var14, 8.2F * var14);
         this.floatValue6 = MathHelper.clamp(this.floatValue6, -6.3F * var14, 6.3F * var14);
         this.floatValue3 = this.floatValue3 + this.floatValue5;
         this.floatValue4 = MathHelper.clamp(this.floatValue4 + this.floatValue6, -89.0F, 89.0F);
         float var18 = mc.player.isGliding() ? 0.25F + var14 * 0.45F : 0.8F;
         this.floatValue7 = this.method3822(this.floatValue7, this.floatValue3, var18);
         this.floatValue8 = this.method3822(this.floatValue8, this.floatValue4, var18 * 0.9F);
         return this.method3816(this.floatValue7 + var7[0], this.floatValue8 + var7[1], var19);
      }
   }

   private Rotation method3762(LivingEntity var1, Rotation var2) {
      Box var3 = this.method3819(var1);
      Vec3d var4 = mc.player.getEyePos();
      Vec3d var5 = var4.add(var2.method3135().multiply(999.0));
      var var6 = var3.raycast(var4, var5);
      boolean var7 = var6.isPresent() || var3.contains(var4);
      ThreadLocalRandom var8 = ThreadLocalRandom.current();
      if (var7) {
         this.floatValue14 = MathHelper.clamp(this.floatValue14 - var8.nextFloat(0.005F, 0.02F), 0.0F, 1.0F);
         this.floatValue15 = MathHelper.clamp(this.floatValue15 - var8.nextFloat(0.005F, 0.02F), 0.0F, 1.0F);
      } else if (mc.player.isGliding()) {
         this.floatValue14 = MathHelper.clamp(this.floatValue14 + var8.nextFloat(9.0E-5F, 0.009F), 0.0F, 1.0F);
         this.floatValue15 = MathHelper.clamp(this.floatValue15 + var8.nextFloat(9.0E-4F, 0.009F), 0.0F, 1.0F);
      } else if (var1.isInSwimmingPose()) {
         this.floatValue14 = MathHelper.clamp(this.floatValue14 + var8.nextFloat(9.0E-5F, 0.009F), 0.0F, 1.0F);
         this.floatValue15 = MathHelper.clamp(this.floatValue15 + var8.nextFloat(9.0E-5F, 9.0E-4F), 0.0F, 1.0F);
      } else {
         this.floatValue14 = MathHelper.clamp(this.floatValue14 + var8.nextFloat(9.0E-5F, 0.009F), 0.0F, 1.0F);
         this.floatValue15 = MathHelper.clamp(this.floatValue15 + var8.nextFloat(9.0E-4F, 0.009F), 0.0F, 1.0F);
      }

      Rotation var9 = this.method3826(var1.getBoundingBox().getCenter());
      Vec2f var10 = var2.method3131(var9);
      return new Rotation(var2.getFloatValue() + var10.getFloatValue() * this.floatValue14, var2.getFloatValue2() + var10.getFloatValue2() * this.floatValue15);
   }

   private Rotation method3763(LivingEntity var1, Rotation var2) {
      Rotation var3 = this.method3826(this.method3824(var1));
      Vec2f var4 = var2.method3131(var3);
      float var5 = (float)Math.hypot(Math.abs(var4.getFloatValue()), Math.abs(var4.getFloatValue2()));
      if (var5 <= 1.0E-4F) {
         return var2;
      } else {
         double var6 = System.currentTimeMillis() - this.longValue3;
         float var8 = AuraModule.INSTANCE.isFlag26() ? 0.3F : 1.0F;
         float var9 = (float)(this.method3823(3.0F, 6.0F) * Math.cos(var6 / 60.0)) * var8;
         float var10 = (float)(this.method3823(2.0F, 4.0F) * Math.sin(var6 / 40.0)) * var8;
         boolean var11 = TargetUtils.method3209(16.0, var2, var1);
         float var12 = Math.abs(var4.getFloatValue() / var5) * 180.0F;
         float var13 = Math.abs(var4.getFloatValue2() / var5) * 180.0F;
         float var14 = MathHelper.clamp(var4.getFloatValue(), -var12, var12);
         float var15 = MathHelper.clamp(var4.getFloatValue2(), -var13, var13);
         boolean var16 = var6 < 250.0;
         float var17 = var11 ? this.method3823(0.25F, 0.35F) : this.method3823(0.5F, 0.7F);
         if (var16) {
            var17 = this.secureRandom.nextFloat() < 0.1F ? this.method3823(0.5F, 0.8F) : this.method3823(0.3F, 0.45F);
         }

         return new Rotation(
            MathHelper.lerp(var17, var2.getFloatValue(), var2.getFloatValue() + var14) + var9,
            MathHelper.lerp(var17, var2.getFloatValue2(), var2.getFloatValue2() + var15) + var10
         );
      }
   }

   private Rotation method3764(LivingEntity var1) {
      return this.method3826(var1.getBoundingBox().getCenter());
   }

   private Rotation method3765(LivingEntity var1, Rotation var2) {
      if (this.intValue8 != var1.getId()) {
         this.intValue8 = var1.getId();
         this.flag6 = false;
         this.floatValue16 = 0.0F;
         this.floatValue17 = var2.getFloatValue();
         this.floatValue18 = MathHelper.clamp(var2.getFloatValue2(), -40.0F, 40.0F);
      }

      Rotation var3 = this.method3826(var1.getBoundingBox().getCenter());
      Vec3d var4 = mc.player.getEyePos();
      boolean var5 = var1.getBoundingBox().expand(-0.5).raycast(var4, var4.add(var2.method3135().multiply(999.0))).isPresent();
      if (this.flag6) {
         if (this.floatValue16 >= -0.01F) {
            this.floatValue16 = this.floatValue16 - (Math.abs(MathHelper.wrapDegrees(var3.getFloatValue() - this.floatValue17)) > 80.0F ? 0.055F : 0.0055F);
         }

         if (this.floatValue16 <= -0.01F) {
            this.flag6 = false;
         }
      } else {
         this.floatValue16 += 0.0034F;
         if (this.floatValue16 >= 0.36F || var5) {
            this.flag6 = true;
         }
      }

      float var6 = MathHelper.wrapDegrees(var3.getFloatValue() - this.floatValue17);
      float var7 = MathHelper.clamp(var3.getFloatValue2(), -40.0F, 40.0F) - this.floatValue18;
      float var8 = Math.max(this.floatValue16, 0.0F);
      float var9 = this.floatValue17 + var6 * MathHelper.clamp(var8 * 1.3F, 0.0F, 1.0F);
      float var10 = this.floatValue18 + var7 * MathHelper.clamp(var8 / 1.7F, 0.0F, 1.0F);
      float var11 = Rotation.method3138();
      var9 -= (var9 - this.floatValue17) % var11;
      var10 -= (var10 - this.floatValue18) % var11;
      this.floatValue17 = var9;
      this.floatValue18 = MathHelper.clamp(var10, -40.0F, 40.0F);
      return new Rotation(this.floatValue17, this.floatValue18);
   }

   private Rotation method3766(LivingEntity var1) {
      Vec3d var2 = this.method3824(var1).subtract(mc.player.getEyePos());
      float var3 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var2.z, var2.x)) - 90.0);
      float var4 = (float)(-Math.toDegrees(Math.atan2(var2.y, Math.hypot(var2.x, var2.z))));
      float var5 = this.method3833(0.0F, 3.0F);
      return new Rotation(var3 + this.method3833(-var5, var5), var4 + 0.2958859F);
   }

   private Rotation method3767(LivingEntity var1, Rotation var2) {
      if (this.intValue9 != var1.getId()) {
         this.intValue9 = var1.getId();
         this.longValue5 = 0L;
         this.floatValue19 = this.floatValue20 = 0.0F;
         this.floatValue21 = this.floatValue22 = MathHelper.lerp(0.80275816F, 0.75F, 1.2F);
         this.floatValue23 = this.method3833(0.0F, (float) (Math.PI * 2));
         this.floatValue24 = this.method3833(0.0F, (float) (Math.PI * 2));
         this.longValue7 = System.currentTimeMillis() + 120L + this.random.nextInt(160);
      }

      long var3 = System.nanoTime();
      float var5 = this.longValue5 == 0L ? 0.016666668F : (float)(var3 - this.longValue5) / 1.0E9F;
      this.longValue5 = var3;
      var5 = MathHelper.clamp(var5, 0.001F, 0.1F);
      long var6 = System.currentTimeMillis();
      if (var6 >= this.longValue6) {
         this.floatValue22 = Math.random() < 0.12 ? MathHelper.lerp(0.80275816F, 0.1F, 0.3F) : MathHelper.lerp(0.80275816F, 0.75F, 1.2F);
         this.longValue6 = var6 + 250L + this.random.nextInt(550);
      }

      this.floatValue21 = this.floatValue21 + (this.floatValue22 - this.floatValue21) * MathHelper.clamp(4.0F * var5, 0.0F, 1.0F);
      if (var6 < this.longValue7) {
         this.floatValue19 = this.floatValue19 * Math.max(0.0F, 1.0F - 6.0F * var5);
         this.floatValue20 = this.floatValue20 * Math.max(0.0F, 1.0F - 6.0F * var5);
         return this.method3811(var2, var2.getFloatValue() + this.floatValue19 * var5, var2.getFloatValue2() + this.floatValue20 * var5);
      } else {
         Vec3d var8 = this.method3810(var1, var2.getFloatValue());
         this.floatValue23 += var5 * 1.5F;
         this.floatValue24 += var5 * 1.1F;
         float var9 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var8.z, var8.x)) - 90.0) + (float)Math.sin(this.floatValue23) * 0.18F;
         float var10 = MathHelper.clamp(
            (float)(-Math.toDegrees(Math.atan2(var8.y, Math.hypot(var8.x, var8.z)))) + (float)Math.sin(this.floatValue24) * 0.09F, -89.0F, 89.0F
         );
         float var11 = MathHelper.wrapDegrees(var9 - var2.getFloatValue());
         float var12 = var10 - var2.getFloatValue2();
         float var13 = (float)Math.sqrt(var11 * var11 + var12 * var12);
         if (var13 > 1.0E-4F) {
            float var14 = Math.min(140.0F * this.floatValue21, var13 * 5.5F);
            float var15 = var11 * var13 * var14;
            float var16 = var12 * var13 * var14 * 0.78F;
            float var17 = MathHelper.clamp(10.0F * var5, 0.0F, 1.0F);
            this.floatValue19 = this.floatValue19 + (var15 - this.floatValue19) * var17;
            this.floatValue20 = this.floatValue20 + (var16 - this.floatValue20) * var17 * 0.85F;
         } else {
            this.floatValue19 = this.floatValue19 * Math.max(0.0F, 1.0F - 8.0F * var5);
            this.floatValue20 = this.floatValue20 * Math.max(0.0F, 1.0F - 8.0F * var5);
         }

         return this.method3811(var2, var2.getFloatValue() + this.floatValue19 * var5, var2.getFloatValue2() + this.floatValue20 * var5);
      }
   }

   private Rotation method3768(LivingEntity var1, Rotation var2) {
      if (this.intValue10 != var1.getId()) {
         this.intValue10 = var1.getId();
         this.vec3d = Vec3d.ZERO;
         this.longValue8 = 0L;
         this.floatValue25 = this.floatValue26 = 0.0F;
      }

      Box var3 = var1.getBoundingBox();
      long var4 = System.currentTimeMillis();
      if (var4 >= this.longValue8 || this.vec3d.equals(Vec3d.ZERO)) {
         Vec3d var6 = new Vec3d(
            ThreadLocalRandom.current().nextDouble(-var3.getLengthX() * 0.035, var3.getLengthX() * 0.035),
            var3.getLengthY() * ThreadLocalRandom.current().nextDouble(0.7, 0.79),
            ThreadLocalRandom.current().nextDouble(-var3.getLengthZ() * 0.045, var3.getLengthZ() * 0.045)
         );
         this.vec3d = this.vec3d.equals(Vec3d.ZERO)
            ? var6
            : new Vec3d(
               MathHelper.lerp((double)this.method3833(0.16F, 0.3F), this.vec3d.x, var6.x),
               MathHelper.lerp((double)this.method3833(0.08F, 0.18F), this.vec3d.y, var6.y),
               MathHelper.lerp((double)this.method3833(0.16F, 0.3F), this.vec3d.z, var6.z)
            );
         this.longValue8 = var4 + ThreadLocalRandom.current().nextLong(170L, 420L);
      }

      Vec3d var24 = mc.player.getEyePos().subtract(var1.getEntityPos()).multiply(1.0, 0.0, 1.0);
      if (var24.lengthSquared() < 1.0E-6) {
         var24 = new Vec3d(0.0, 0.0, 1.0);
      }

      Vec3d var7 = var24.normalize();
      Vec3d var8 = new Vec3d(var7.z, 0.0, -var7.x);
      Vec3d var9 = var1.getEntityPos()
         .add(var8.multiply(Math.max(0.1, var3.getLengthX() * 0.3)))
         .add(var8.multiply(this.vec3d.x))
         .add(var7.multiply(this.vec3d.z))
         .add(0.0, this.vec3d.y, 0.0);
      Rotation var10 = this.method3826(var9);
      Vec2f var11 = var2.method3131(var10);
      boolean var12 = TargetUtils.method3209(16.0, var2, var1);
      float var13 = MathHelper.clamp((Math.abs(var11.getFloatValue2()) - this.method3833(var12 ? 0.55F : 0.45F, var12 ? 1.1F : 0.95F)) / 5.5F, 0.0F, 1.0F);
      float var14 = this.method3833(var12 ? 0.24F : 0.37F, var12 ? 0.43F : 0.68F);
      float var15 = this.method3833(var12 ? 0.16F : 0.24F, var12 ? 0.3F : 0.44F) * var13;
      float var16 = (float)mc.player.getEyePos().distanceTo(var9);
      float var17 = var16 < 2.4F ? this.method3833(1.9F, 3.9F) : (var16 < 4.6F ? this.method3833(2.5F, 5.2F) : this.method3833(3.4F, 6.9F));
      float var18 = var16 < 2.4F ? this.method3833(0.55F, 1.8F) : (var16 < 4.6F ? this.method3833(0.8F, 2.7F) : this.method3833(1.1F, 3.6F));
      float var19 = MathHelper.clamp(var11.getFloatValue() * var14, -var17, var17);
      float var20 = var13 < 0.08F ? 0.0F : MathHelper.clamp(var11.getFloatValue2() * var15, -var18, var18);
      this.floatValue25 = this.floatValue25 + MathHelper.clamp(var19 - this.floatValue25, -this.method3833(0.1F, 0.29F), this.method3833(0.1F, 0.29F));
      this.floatValue26 = this.floatValue26 + MathHelper.clamp(var20 - this.floatValue26, -this.method3833(0.05F, 0.37F), this.method3833(0.05F, 0.37F));
      float var21 = Rotation.method3138();
      float var22 = this.method3812(this.floatValue25, var11.getFloatValue(), var21);
      float var23 = this.method3812(this.floatValue26, var11.getFloatValue2(), var21);
      return new Rotation(var2.getFloatValue() + var22, var2.getFloatValue2() + var23);
   }

   private Rotation method3769(LivingEntity var1, Rotation var2) {
      if (this.intValue11 != var1.getId()) {
         this.intValue11 = var1.getId();
         this.vec3d2 = Vec3d.ZERO;
         this.longValue9 = 0L;
         this.floatValue27 = this.floatValue28 = 0.0F;
         this.intValue12 = 1;
      }

      Box var3 = var1.getBoundingBox();
      long var4 = System.currentTimeMillis();
      if (var4 >= this.longValue9 || this.vec3d2.equals(Vec3d.ZERO)) {
         Vec3d var6 = new Vec3d(
            ThreadLocalRandom.current().nextDouble(-var3.getLengthX() * 0.19, var3.getLengthX() * 0.19),
            var3.getLengthY() * 0.72,
            ThreadLocalRandom.current().nextDouble(-var3.getLengthZ() * 0.21, var3.getLengthZ() * 0.21)
         );
         this.vec3d2 = this.vec3d2.equals(Vec3d.ZERO)
            ? var6
            : new Vec3d(
               MathHelper.lerp(0.22, this.vec3d2.x, var6.x), MathHelper.lerp(0.16, this.vec3d2.y, var6.y), MathHelper.lerp(0.22, this.vec3d2.z, var6.z)
            );
         this.longValue9 = var4 + ThreadLocalRandom.current().nextLong(140L, 380L);
         if (this.random.nextBoolean()) {
            this.intValue12 = -this.intValue12;
         }
      }

      Vec3d var13 = mc.player.getEntityPos().subtract(var1.getEntityPos()).multiply(1.0, 0.0, 1.0);
      if (var13.lengthSquared() < 1.0E-6) {
         var13 = new Vec3d(0.0, 0.0, 1.0);
      }

      var13 = var13.normalize();
      Vec3d var7 = new Vec3d(var13.z, 0.0, -var13.x);
      Vec3d var8 = var1.getEntityPos()
         .add(var7.multiply(var3.getLengthX() * 0.15 * this.intValue12))
         .add(var7.multiply(this.vec3d2.x))
         .add(var13.multiply(this.vec3d2.z))
         .add(0.0, this.vec3d2.y, 0.0);
      Vec2f var9 = var2.method3131(this.method3826(var8));
      float var10 = MathHelper.clamp(var9.getFloatValue() * 0.38F, -6.0F, 6.0F);
      float var11 = Math.abs(var9.getFloatValue2()) < 0.65F ? 0.0F : MathHelper.clamp(var9.getFloatValue2() * 0.3F, -2.9F, 2.9F);
      this.floatValue27 = this.floatValue27 + MathHelper.clamp(var10 - this.floatValue27, -0.42F, 0.42F);
      this.floatValue28 = this.floatValue28 + MathHelper.clamp(var11 - this.floatValue28, -0.28F, 0.28F);
      this.floatValue27 = MathHelper.lerp(0.22F, this.floatValue27, this.floatValue27 + this.method3833(-0.14F, 0.14F));
      this.floatValue28 = MathHelper.lerp(0.14F, this.floatValue28, this.floatValue28 + this.method3833(-0.11F, 0.11F));
      float var12 = Rotation.method3138();
      return new Rotation(
         var2.getFloatValue() + this.method3812(this.floatValue27, var9.getFloatValue(), var12),
         var2.getFloatValue2() + this.method3812(this.floatValue28, var9.getFloatValue2(), var12)
      );
   }

   private Rotation method3770(LivingEntity var1, Rotation var2) {
      Rotation var3 = this.method3826(this.method3824(var1));
      Vec2f var4 = var2.method3131(var3);
      float var5 = var4.getFloatValue();
      float var6 = var4.getFloatValue2();
      float var7 = (float)Math.hypot(var5, var6);
      boolean var8 = this.method3778();
      float var9 = var8 ? 0.4F : 1.0F;
      float var10 = Math.min(Math.abs(var5), (45.0F + this.method3823(0.0F, 0.5F)) * var9);
      float var11 = Math.min(Math.abs(var6), 20.0F * var9);
      float var12 = var2.getFloatValue();
      float var13 = var2.getFloatValue2();
      if (var7 > 0.5F) {
         boolean var14 = Math.abs(var6) >= var11;
         float var15 = var8 ? this.method3823(3.0F, 5.0F) : this.method3823(6.0F, 9.0F);
         float var16 = var8 ? this.method3823(12.0F, 18.0F) : this.method3823(25.0F, 35.0F);
         float var17 = Math.min(var7, var14 ? var16 : var15) / var7;
         if (!var14) {
            var17 = this.method3779(var17);
         }

         float var18 = var8 ? 3.0F : 15.0F;
         var13 = MathHelper.clamp(var2.getFloatValue2() + MathHelper.clamp(var6 * var17, -var18, var18), -89.0F, 90.0F);
         boolean var19 = Math.abs(var5) >= var10;
         var15 = var8 ? this.method3823(3.0F, 5.0F) : this.method3823(6.0F, 9.0F);
         var16 = var8 ? this.method3823(12.0F, 18.0F) : this.method3823(25.0F, 35.0F);
         float var20 = Math.min(var7, var19 ? var16 : var15) / var7;
         if (!var19) {
            var20 = this.method3779(var20);
         }

         var12 = var2.getFloatValue() + var5 * var20;
      }

      if (var7 < 5.0F) {
         float var21 = (float)(System.currentTimeMillis() % 12000L) / 1200.0F;
         float var23 = var8 ? 0.3F : 1.0F;
         var12 += MathHelper.sin(var21 * 2.0F * (float) (Math.PI * 2)) * 0.5F * var23 * (0.5F + this.random.nextFloat() * 0.5F);
      }

      return this.method3780(var2, var12, var13);
   }

   private Rotation method3771(LivingEntity var1, Rotation var2) {
      Rotation var3 = this.method3826(this.method3777(var1, 0.5, 0.05));
      long var4 = System.currentTimeMillis();
      if (this.intValue13 != var1.getId()) {
         this.intValue13 = var1.getId();
         this.longValue10 = var4 + 55L + this.random.nextInt(115);
         this.longValue12 = 0L;
         this.floatValue29 = 0.0F;
      }

      if (var4 < this.longValue10) {
         return this.method3780(var2, var2.getFloatValue() + this.method3833(-0.08F, 0.08F), var2.getFloatValue2() + this.method3833(-0.05F, 0.05F));
      } else {
         if (this.longValue12 <= 0L || var4 >= this.longValue11 + this.longValue12 || this.rotation2.method3130(var3) > 2.5F) {
            float var6 = Math.max(var2.method3130(var3), 0.01F);
            this.rotation = var2;
            this.rotation2 = var3;
            this.longValue11 = var4;
            this.longValue12 = (long)MathHelper.clamp(75.0F + var6 * 4.2F + this.method3833(-20.0F, 35.0F), 90.0F, 520.0F);
         }

         float var12 = MathHelper.clamp((float)(var4 - this.longValue11) / (float)Math.max(this.longValue12, 1L), 0.0F, 1.0F);
         float var7 = var12 * var12 * var12 * (10.0F + var12 * (-15.0F + 6.0F * var12));
         Vec2f var8 = this.rotation.method3131(this.rotation2);
         this.floatValue29 = this.floatValue29 * 0.72F + this.method3833(-0.12F, 0.12F);
         float var9 = (float)Math.sin(Math.PI * var12);
         float var10 = this.rotation.getFloatValue() + var8.getFloatValue() * var7 + this.floatValue29 * var9;
         float var11 = this.rotation.getFloatValue2() + var8.getFloatValue2() * var7 + this.floatValue29 * 0.45F * var9;
         return this.method3780(var2, var10, var11);
      }
   }

   private Rotation method3772(LivingEntity var1, Rotation var2) {
      Rotation var3 = this.method3826(this.method3777(var1, 0.56, 0.04));
      if (this.intValue14 != var1.getId()) {
         this.intValue14 = var1.getId();
         this.method3776(var2, var3);
      }

      Rotation var4 = this.flag7 ? var3 : this.rotation3;
      Vec2f var5 = var2.method3131(var4);
      float var6 = var5.method5327();
      if (!this.flag7 && var6 < 1.2F) {
         this.flag7 = true;
         var5 = var2.method3131(var3);
         this.floatValue30 *= 0.48F;
      } else if (this.flag7 && var2.method3130(var3) > 12.0F) {
         this.method3776(var2, var3);
         var4 = this.rotation3;
         var5 = var2.method3131(var4);
      }

      float var7 = MathHelper.clamp(var5.method5327() * (this.flag7 ? 0.18F : 0.32F), 0.18F, this.flag7 ? 2.8F : 8.5F);
      this.floatValue30 = this.floatValue30 + MathHelper.clamp(var7 - this.floatValue30, -0.65F, 0.85F);
      float var8 = Math.max(var5.method5327(), 1.0E-4F);
      float var9 = var5.getFloatValue() / var8 * Math.min(var8, this.floatValue30);
      float var10 = var5.getFloatValue2() / var8 * Math.min(var8, this.floatValue30 * 0.72F);
      return this.method3780(var2, var2.getFloatValue() + var9, var2.getFloatValue2() + var10);
   }

   private Rotation method3773(LivingEntity var1, Rotation var2) {
      if (this.intValue15 != var1.getId()) {
         this.intValue15 = var1.getId();
         this.floatValue31 = this.floatValue32 = 0.0F;
         this.floatValue33 = this.floatValue34 = 0.0F;
      }

      Rotation var3 = this.method3826(this.method3777(var1, 0.48, 0.025));
      Vec2f var4 = var2.method3131(var3);
      float var5 = var4.method5327();
      float var6 = MathHelper.lerp(MathHelper.clamp(var5 / 35.0F, 0.0F, 1.0F), 0.1F, 0.24F);
      float var7 = var5 < 2.0F ? 0.82F : 0.68F;
      this.floatValue31 = this.floatValue31 * var7 + var4.getFloatValue() * var6;
      this.floatValue32 = this.floatValue32 * (var7 + 0.04F) + var4.getFloatValue2() * var6 * 0.78F;
      this.floatValue31 = MathHelper.clamp(this.floatValue31, -7.2F, 7.2F);
      this.floatValue32 = MathHelper.clamp(this.floatValue32, -4.6F, 4.6F);
      this.floatValue33 = this.floatValue33 * 0.86F + this.method3833(-0.075F, 0.075F);
      this.floatValue34 = this.floatValue34 * 0.81F + this.method3833(-0.045F, 0.045F);
      float var8 = MathHelper.clamp(1.0F - var5 / 18.0F, 0.12F, 1.0F);
      return this.method3780(
         var2, var2.getFloatValue() + this.floatValue31 + this.floatValue33 * var8, var2.getFloatValue2() + this.floatValue32 + this.floatValue34 * var8
      );
   }

   private Rotation method3774(LivingEntity var1, Rotation var2) {
      if (this.intValue16 != var1.getId()) {
         this.intValue16 = var1.getId();
         this.intValue17 = 0;
         this.intValue18 = 1 + this.random.nextInt(3);
      }

      Rotation var3 = this.method3826(this.method3777(var1, 0.52, 0.065));
      Vec2f var4 = var2.method3131(var3);
      if (this.intValue18 > 0) {
         this.intValue18--;
         return var2;
      } else {
         if (this.intValue17 <= 0) {
            float var5 = Math.max(var4.method5327(), 0.01F);
            this.intValue17 = MathHelper.clamp(2 + Math.round(var5 / this.method3833(3.5F, 7.5F)), 2, 9);
            float var6 = var5 < 4.0F ? this.method3833(0.35F, 0.7F) : this.method3833(0.68F, 1.05F);
            this.floatValue35 = var4.getFloatValue() * var6 / this.intValue17;
            this.floatValue36 = var4.getFloatValue2() * var6 / this.intValue17;
         }

         float var7 = 1.0F - (float)(this.intValue17 - 1) / Math.max(this.intValue17 + 1, 2);
         float var8 = 0.65F + 0.55F * (float)Math.sin(Math.PI * var7);
         this.intValue17--;
         if (this.intValue17 <= 0) {
            this.intValue18 = this.random.nextFloat() < 0.22F ? 1 + this.random.nextInt(3) : 0;
         }

         return this.method3780(var2, var2.getFloatValue() + this.floatValue35 * var8, var2.getFloatValue2() + this.floatValue36 * var8);
      }
   }

   private Rotation method3775(LivingEntity var1, Rotation var2) {
      Rotation var3 = this.method3826(var1.getBoundingBox().getCenter());
      long var4 = System.currentTimeMillis();
      if (this.intValue19 != var1.getId()) {
         this.intValue19 = var1.getId();
         this.longValue13 = var4 + 70L + this.random.nextInt(140);
         this.floatValue37 = this.floatValue38 = 0.0F;
      }

      if (var4 < this.longValue13) {
         return var2;
      } else {
         Vec2f var6 = var2.method3131(var3);
         float var7 = Math.max(var6.method5327(), 0.001F);
         double var8 = Math.max(mc.player.getEyePos().distanceTo(var1.getBoundingBox().getCenter()), 0.1);
         double var10 = Math.max((double)var1.getWidth(), 0.1);
         float var12 = (float)Math.toDegrees(2.0 * Math.atan(var10 / (2.0 * var8)));
         float var13 = (float)(Math.log(var7 / Math.max(var12, 0.05F) + 1.0) / Math.log(2.0));
         float var14 = 85.0F + 92.0F * var13;
         float var15 = Math.max(var14 / 50.0F, 1.0F);
         float var16 = var6.getFloatValue() / var15;
         float var17 = var6.getFloatValue2() / var15;
         float var18 = var7 > var12 ? 0.42F : 0.2F;
         this.floatValue37 = this.floatValue37 + MathHelper.clamp(var16 - this.floatValue37, -var18 * 1.25F, var18 * 1.25F);
         this.floatValue38 = this.floatValue38 + MathHelper.clamp(var17 - this.floatValue38, -var18 * 0.8F, var18 * 0.8F);
         if (var7 < var12 * 0.45F) {
            this.floatValue37 *= 0.55F;
            this.floatValue38 *= 0.5F;
         }

         return this.method3780(
            var2,
            var2.getFloatValue() + this.floatValue37 + this.method3833(-0.025F, 0.025F),
            var2.getFloatValue2() + this.floatValue38 + this.method3833(-0.018F, 0.018F)
         );
      }
   }

   private void method3776(Rotation var1, Rotation var2) {
      Vec2f var3 = var1.method3131(var2);
      float var4 = var3.getFloatValue() == 0.0F ? (this.random.nextBoolean() ? 1.0F : -1.0F) : Math.signum(var3.getFloatValue());
      float var5 = var3.getFloatValue2() == 0.0F ? (this.random.nextBoolean() ? 1.0F : -1.0F) : Math.signum(var3.getFloatValue2());
      float var6 = MathHelper.clamp(var3.method5327() / 70.0F, 0.25F, 1.0F);
      this.rotation3 = new Rotation(
         var2.getFloatValue() + var4 * this.method3833(0.8F, 3.8F) * var6, var2.getFloatValue2() + var5 * this.method3833(0.25F, 1.35F) * var6
      );
      this.flag7 = false;
      this.floatValue30 = this.method3833(0.25F, 0.65F);
   }

   private Vec3d method3777(LivingEntity var1, double var2, double var4) {
      Box var6 = var1.getBoundingBox();
      double var7 = MathHelper.clamp(var1.getX() + this.method3833((float)(-var4), (float)var4) * var6.getLengthX(), var6.minX, var6.maxX);
      double var9 = MathHelper.clamp(
         var6.minY + var6.getLengthY() * var2 + this.method3833((float)(-var4), (float)var4) * var6.getLengthY(), var6.minY, var6.maxY
      );
      double var11 = MathHelper.clamp(var1.getZ() + this.method3833((float)(-var4), (float)var4) * var6.getLengthZ(), var6.minZ, var6.maxZ);
      return new Vec3d(var7, var9, var11);
   }

   private boolean method3778() {
      return !mc.world.isAir(mc.player.getBlockPos().up())
         || !mc.world.isAir(mc.player.getBlockPos().west())
         || !mc.world.isAir(mc.player.getBlockPos().east())
         || !mc.world.isAir(mc.player.getBlockPos().north())
         || !mc.world.isAir(mc.player.getBlockPos().south());
   }

   private float method3779(float var1) {
      return var1 * (0.5F + 0.5F * var1);
   }

   private Rotation method3780(Rotation var1, float var2, float var3) {
      float var4 = Rotation.method3138();
      float var5 = var1.getFloatValue() + Math.round(MathHelper.wrapDegrees(var2 - var1.getFloatValue()) / var4) * var4;
      float var6 = var1.getFloatValue2() + Math.round((var3 - var1.getFloatValue2()) / var4) * var4;
      return new Rotation(var5, MathHelper.clamp(var6, -89.0F, 89.0F));
   }

   private Rotation method3781(LivingEntity var1, Rotation var2) {
      if (this.intValue20 != var1.getId()) {
         this.intValue20 = var1.getId();
         this.floatValue39 = var2.getFloatValue();
         this.floatValue40 = var2.getFloatValue2();
         this.floatValue45 = this.floatValue43 = this.floatValue39;
         this.floatValue46 = this.floatValue44 = this.floatValue40;
         this.floatValue41 = this.floatValue42 = 0.0F;
         this.floatValue47 = this.floatValue48 = 0.0F;
         this.method3782(var1);
         float var3 = var2.method3130(this.method3826(var1.getBoundingBox().getCenter()));
         int var4 = var3 > 130.0F ? 140 : (var3 > 70.0F ? 90 : (var3 > 30.0F ? 45 : 12));
         int var5 = var3 > 130.0F ? 30 : (var3 > 70.0F ? 20 : (var3 > 30.0F ? 15 : 5));
         this.intValue21 = Math.max(5, Math.round((var4 + (float)this.random.nextGaussian() * var5) * 0.275F));
         this.longValue14 = System.currentTimeMillis();
         this.flag8 = false;
         this.intValue22 = this.intValue23 = 0;
         this.intValue24 = var1.hurtTime;
      }

      float var19 = this.method3817();
      if (!this.flag8) {
         if (System.currentTimeMillis() - this.longValue14 < this.intValue21) {
            float var21 = this.floatValue45 + (float)this.random.nextGaussian() * 0.15F;
            float var23 = MathHelper.clamp(this.floatValue46 + (float)this.random.nextGaussian() * 0.1F, -89.0F, 89.0F);
            return this.method3783(var21, var23, var19);
         }

         this.flag8 = true;
      }

      Vec3d var20 = mc.player.getEyePos();
      Vec3d var22 = var1.getBoundingBox().getCenter();
      float var6 = (float)var20.distanceTo(var22);
      float var7 = MathHelper.clamp(var6 / 4.5F, 0.25F, 1.0F);
      this.floatValue47 = (this.floatValue47 + (float)this.random.nextGaussian() * 0.4F * var7) * 0.85F;
      this.floatValue48 = (this.floatValue48 + (float)this.random.nextGaussian() * 0.3F * var7) * 0.85F;
      if (var1.hurtTime > this.intValue24 && var1.hurtTime > 0) {
         this.intValue22 = 1;
         this.intValue23 = 0;
         this.floatValue49 = this.floatValue40;
      }

      this.intValue24 = var1.hurtTime;
      if (this.intValue22 > 0) {
         this.intValue23++;
         if (this.intValue22 == 1) {
            float var24 = this.method3787(MathHelper.clamp(this.intValue23 / 25.0F, 0.0F, 1.0F));
            this.floatValue40 = MathHelper.lerp(var24, this.floatValue49, -89.0F);
            if (this.intValue23 >= 25) {
               this.intValue22 = 2;
               this.intValue23 = 0;
            }
         } else {
            float var25 = this.method3786(MathHelper.clamp(this.intValue23 / 20.0F, 0.0F, 1.0F));
            this.floatValue40 = MathHelper.lerp(var25, -89.0F, this.floatValue49);
            if (this.intValue23 >= 20) {
               this.intValue22 = this.intValue23 = 0;
            }
         }

         return this.method3783(this.floatValue39 + this.floatValue47, this.floatValue40 + this.floatValue48, var19);
      } else {
         if (this.random.nextDouble() < 0.015) {
            this.method3782(var1);
         }

         Vec3d var8 = new Vec3d(var1.getX() - var1.lastX, var1.getY() - var1.lastY, var1.getZ() - var1.lastZ);
         int var9 = !mc.player.isGliding() && !var1.isGliding() ? 2 : 0;
         Vec3d var10 = var22.add(var8.multiply(var9)).add(this.doubleValue4, this.doubleValue5, this.doubleValue6);
         Rotation var11 = this.method3826(var10);
         float var12 = MathHelper.wrapDegrees(var11.getFloatValue() - this.floatValue39);
         float var13 = var11.getFloatValue2() - this.floatValue40;
         float var14 = 1.0F;
         if (mc.player.isGliding()) {
            float var15 = Math.abs(var12) + Math.abs(var13);
            if (var15 > 120.0F) {
               var14 = 0.18F;
            } else if (var15 > 80.0F) {
               var14 = MathHelper.lerp(this.method3786((var15 - 80.0F) / 40.0F), 0.35F, 0.18F);
            } else if (var15 > 25.0F) {
               var14 = MathHelper.lerp(this.method3786((var15 - 25.0F) / 55.0F), 0.65F, 0.35F);
            } else {
               var14 = 0.65F + 0.35F * (1.0F - var15 / 25.0F);
            }
         } else if (mc.options.forwardKey.isPressed() || this.method3818(var1)) {
            var14 = 0.78F;
         }

         float var26 = (0.13F + (float)Math.abs(this.random.nextGaussian()) * 0.024F) * var14;
         float var16 = 0.62F + 0.1F * (1.0F - var14);
         float var17 = (float)Math.sqrt(var12 * var12 + var13 * var13);
         if (var17 > 32.0F) {
            var26 += 0.064F * var14;
         } else if (var17 < 4.2F) {
            var26 *= 0.48F;
         }

         var26 += MathHelper.clamp((var6 - 1.6F) / 7.5F, 0.0F, 0.045F) * var14;
         this.floatValue41 = this.method3784(var12, this.floatValue41, var26, var16, true);
         this.floatValue42 = this.method3784(var13, this.floatValue42, var26 * 0.87F, var16, false);
         this.floatValue41 = this.method3785(this.floatValue41, 55.0F);
         this.floatValue42 = this.method3785(this.floatValue42, 40.0F);
         this.floatValue39 = this.floatValue39 + this.floatValue41;
         this.floatValue40 = MathHelper.clamp(this.floatValue40 + this.floatValue42, -89.0F, 89.0F);
         float var18 = mc.player.isGliding() ? 0.6F + var14 * 0.38F : 0.98F;
         this.floatValue43 = this.method3822(this.floatValue43, this.floatValue39, var18);
         this.floatValue44 = this.method3822(this.floatValue44, this.floatValue40, var18 * 0.95F);
         return this.method3783(this.floatValue43 + this.floatValue47, this.floatValue44 + this.floatValue48, var19);
      }
   }

   private void method3782(LivingEntity var1) {
      Box var2 = var1.getBoundingBox();
      this.doubleValue4 = MathHelper.clamp(this.random.nextGaussian() * 0.15, -0.5, 0.5) * var2.getLengthX() * 0.4;
      this.doubleValue5 = MathHelper.clamp(this.random.nextGaussian() * 0.15, -0.5, 0.5) * var2.getLengthY() * 0.4;
      this.doubleValue6 = MathHelper.clamp(this.random.nextGaussian() * 0.15, -0.5, 0.5) * var2.getLengthZ() * 0.4;
   }

   private Rotation method3783(float var1, float var2, float var3) {
      var2 = MathHelper.clamp(var2, -89.0F, 89.0F);
      var1 -= (var1 - this.floatValue45) % var3;
      var2 -= (var2 - this.floatValue46) % var3;
      this.floatValue45 = var1;
      this.floatValue46 = var2;
      return new Rotation(var1, var2);
   }

   private float method3784(float var1, float var2, float var3, float var4, boolean var5) {
      if (var5 && Math.abs(var1) > 45.0F) {
         var3 *= 1.2F;
         var4 *= 0.8F;
      }

      return var2 + var1 * var3 - var2 * var4;
   }

   private float method3785(float var1, float var2) {
      return (float)(var2 * Math.tanh(var1 / var2));
   }

   private float method3786(float var1) {
      var1 = MathHelper.clamp(var1, 0.0F, 1.0F);
      return var1 * var1 * (3.0F - 2.0F * var1);
   }

   private float method3787(float var1) {
      var1 = MathHelper.clamp(var1, 0.0F, 1.0F);
      return 1.0F - (1.0F - var1) * (1.0F - var1);
   }

   private Rotation method3788(int var1, LivingEntity var2, Rotation var3) {
      Class26.Entry3 var4 = this.method3790(var1);
      long var5 = System.currentTimeMillis();
      if (var4.intValue != var2.getId()) {
         this.method3791(var4, var1, var2, var3, var5);
      }

      if (var5 < var4.longValue) {
         return var1 % 3 == 0
            ? this.method3780(var3, var3.getFloatValue() + this.method3833(-0.035F, 0.035F), var3.getFloatValue2() + this.method3833(-0.025F, 0.025F))
            : var3;
      } else if (var5 < var4.longValue3) {
         var4.floatValue *= 0.35F;
         var4.floatValue2 *= 0.35F;
         return var3;
      } else if (var5 >= var4.longValue2) {
         var4.longValue3 = var5 + 65L + this.random.nextInt(240);
         var4.longValue2 = var4.longValue3 + 550L + this.random.nextInt(1900);
         var4.intValue2 = 0;
         return var3;
      } else {
         if (var5 >= var4.longValue5 || var4.vec3d.equals(Vec3d.ZERO)) {
            var4.vec3d = this.method3792(var1, var2, var4, var5);
            var4.longValue5 = var5 + this.method3797(var1);
         }

         Rotation var7 = this.method3826(var4.vec3d);
         boolean var8 = TargetUtils.method3209(16.0, var3, var2);
         if (var8 && var4.intValue2 <= 0) {
            if (var4.longValue4 == 0L) {
               var4.longValue4 = var5 + 130L + this.random.nextInt(520);
            }

            if (var5 < var4.longValue4) {
               var4.floatValue *= 0.45F;
               var4.floatValue2 *= 0.45F;
               if (var1 != 4 && var1 != 10) {
                  return var3;
               }

               return this.method3780(var3, var3.getFloatValue() + var4.floatValue3 * 0.08F, var3.getFloatValue2() + var4.floatValue4 * 0.06F);
            }

            var4.intValue2 = 1 + this.random.nextInt(var1 % 4 == 0 ? 3 : 2);
            var4.longValue4 = var5 + 180L + this.random.nextInt(650);
         } else if (!var8) {
            var4.longValue4 = 0L;
            var4.intValue2 = Math.max(var4.intValue2, 1);
         }

         Rotation var9 = this.method3789(var1, var2, var3, var7, var4, var5);
         if (var4.intValue2 > 0) {
            var4.intValue2--;
         }

         return var9;
      }
   }

   private Rotation method3789(int var1, LivingEntity var2, Rotation var3, Rotation var4, Class26.Entry3 var5, long var6) {
      Vec2f var8 = var3.method3131(var4);
      float var9 = var8.getFloatValue();
      float var10 = var8.getFloatValue2();
      float var11 = Math.max(var8.method5327(), 1.0E-4F);
      var5.floatValue3 = var5.floatValue3 * (0.72F + var1 % 3 * 0.06F) + this.method3833(-0.055F, 0.055F);
      var5.floatValue4 = var5.floatValue4 * (0.68F + var1 % 4 * 0.05F) + this.method3833(-0.035F, 0.035F);
      float var12;
      float var13;
      switch (var1) {
         case 0:
            float var32 = (float)Math.sin(MathHelper.clamp(var11 / 75.0F, 0.0F, 1.0F) * Math.PI * 0.5);
            float var40 = MathHelper.lerp(var32, 0.45F, 60.0F);
            var12 = var9 / var11 * Math.min(var11, var40);
            var13 = var10 / var11 * Math.min(var11, var40 * 0.67F);
            break;
         case 1:
            float var31 = 1.0F - (float)Math.exp(-0.085F * var11);
            float var39 = 0.35F + var31 * 59.65F;
            var12 = Math.copySign(Math.min(Math.abs(var9), var39), var9);
            var13 = Math.copySign(Math.min(Math.abs(var10), var39 * 0.67F), var10);
            break;
         case 2:
            float var30 = 1.0F / (1.0F + (float)Math.exp(-0.13F * (var11 - 17.0F)));
            float var38 = 0.5F + var30 * 59.5F;
            var12 = var9 / var11 * Math.min(var11, var38);
            var13 = var10 / var11 * Math.min(var11, var38 * 0.67F);
            break;
         case 3:
            float var29 = 0.17F + MathHelper.clamp(var11 / 100.0F, 0.0F, 0.11F);
            var5.floatValue = var5.floatValue * 0.31F + var9 * var29;
            var5.floatValue2 = var5.floatValue2 * 0.36F + var10 * var29 * 0.76F;
            var12 = MathHelper.clamp(var5.floatValue, -60.0F, 60.0F);
            var13 = MathHelper.clamp(var5.floatValue2, -40.0F, 40.0F);
            break;
         case 4:
            float var28 = var11 > 35.0F ? 0.24F : 0.12F;
            var5.floatValue = var5.floatValue * 0.58F + var9 * var28;
            var5.floatValue2 = var5.floatValue2 * 0.62F + var10 * var28 * 0.68F;
            var12 = MathHelper.clamp(var5.floatValue, -60.0F, 60.0F);
            var13 = MathHelper.clamp(var5.floatValue2, -40.0F, 40.0F);
            break;
         case 5:
            if (var5.longValue8 <= 0L || var6 >= var5.longValue7 + var5.longValue8 || var5.rotation2.method3130(var4) > 3.5F) {
               var5.rotation = var3;
               var5.rotation2 = var4;
               var5.longValue7 = var6;
               var5.longValue8 = (long)MathHelper.clamp(95.0F + var11 * 5.0F + this.method3833(-25.0F, 55.0F), 110.0F, 650.0F);
            }

            float var27 = MathHelper.clamp((float)(var6 - var5.longValue7) / (float)var5.longValue8, 0.0F, 1.0F);
            float var37 = var27 * var27 * (3.0F - 2.0F * var27);
            Vec2f var41 = var5.rotation.method3131(var5.rotation2);
            float var17 = var5.rotation.getFloatValue() + var41.getFloatValue() * var37;
            float var18 = var5.rotation.getFloatValue2() + var41.getFloatValue2() * var37;
            var12 = MathHelper.wrapDegrees(var17 - var3.getFloatValue());
            var13 = var18 - var3.getFloatValue2();
            break;
         case 6:
            var5.floatValue5 = var5.floatValue5 + (0.28F + MathHelper.clamp(var11 / 180.0F, 0.0F, 0.22F));
            float var26 = 0.35F + (0.5F - 0.5F * (float)Math.cos(var5.floatValue5)) * 0.95F;
            float var36 = Math.min(var11, 46.0F * var26);
            var12 = var9 / var11 * var36;
            var13 = var10 / var11 * var36 * 0.67F;
            break;
         case 7:
            float var25 = MathHelper.clamp((float)Math.pow(var11, 0.67) * 3.9F, 0.35F, 60.0F);
            var12 = var9 / var11 * Math.min(var11, var25);
            var13 = var10 / var11 * Math.min(var11, var25 * 0.67F);
            break;
         case 8:
            float var24 = this.method3798(var2) * 0.38F;
            if (var11 <= Math.max(var24, 0.35F)) {
               var5.floatValue *= 0.25F;
               var5.floatValue2 *= 0.25F;
               return var3;
            }

            float var35 = MathHelper.clamp((var11 - var24) * 1.8F, 0.35F, 60.0F);
            var12 = var9 / var11 * Math.min(var11, var35);
            var13 = var10 / var11 * Math.min(var11, var35 * 0.67F);
            break;
         case 9:
            float var23 = 1.0F - (float)Math.exp(-0.11F * Math.abs(var9));
            float var34 = 1.0F - (float)Math.exp(-0.065F * Math.abs(var10));
            var12 = Math.copySign(Math.min(Math.abs(var9), 0.4F + var23 * 59.6F), var9);
            var13 = Math.copySign(Math.min(Math.abs(var10), 0.3F + var34 * 39.7F), var10);
            break;
         case 10:
            float var22 = (float)(var6 - var5.longValue6) / 1000.0F;
            float var15 = MathHelper.clamp(1.0F - var22 / 14.0F, 0.58F, 1.0F);
            float var16 = MathHelper.clamp(var11 * 1.45F, 0.4F, 60.0F) * var15;
            var5.floatValue6 = var5.floatValue6 + this.method3833(-0.012F, 0.012F);
            var5.floatValue6 *= 0.985F;
            var12 = var9 / var11 * Math.min(var11, var16) + var5.floatValue6;
            var13 = var10 / var11 * Math.min(var11, var16 * 0.68F) + var5.floatValue6 * 0.32F;
            break;
         default:
            if (var5.intValue4 <= 0) {
               var5.intValue4 = 2 + this.random.nextInt(7);
               float var14 = var11 < 5.0F ? this.method3833(0.35F, 0.75F) : this.method3833(0.72F, 1.08F);
               var5.floatValue7 = var9 * var14 / var5.intValue4;
               var5.floatValue8 = var10 * var14 / var5.intValue4;
            }

            float var21 = 0.72F + 0.38F * (float)Math.sin(Math.PI / Math.max(var5.intValue4, 2));
            var12 = var5.floatValue7 * var21;
            var13 = var5.floatValue8 * var21;
            var5.intValue4--;
      }

      float var33 = MathHelper.clamp(1.0F - var11 / 35.0F, 0.18F, 1.0F);
      var12 = MathHelper.clamp(var12 + var5.floatValue3 * var33, -60.0F, 60.0F);
      var13 = MathHelper.clamp(var13 + var5.floatValue4 * var33, -40.0F, 40.0F);
      return this.method3780(var3, var3.getFloatValue() + var12, var3.getFloatValue2() + var13);
   }

   private Class26.Entry3 method3790(int var1) {
      if (this.entry3s == null || this.entry3s.length != 12) {
         this.entry3s = new Class26.Entry3[12];
      }

      if (this.entry3s[var1] == null) {
         this.entry3s[var1] = new Class26.Entry3();
      }

      return this.entry3s[var1];
   }

   private void method3791(Class26.Entry3 var1, int var2, LivingEntity var3, Rotation var4, long var5) {
      var1.intValue = var3.getId();
      var1.longValue = var5 + 25L + this.random.nextInt(65 + var2 * 7);
      var1.longValue2 = var5 + 650L + this.random.nextInt(1150 + var2 * 65);
      var1.longValue3 = 0L;
      var1.longValue4 = 0L;
      var1.longValue5 = 0L;
      var1.intValue2 = 0;
      var1.floatValue = var1.floatValue2 = 0.0F;
      var1.floatValue3 = var1.floatValue4 = 0.0F;
      var1.floatValue5 = this.method3833(0.0F, (float) (Math.PI * 2));
      var1.floatValue6 = 0.0F;
      var1.longValue8 = 0L;
      var1.intValue4 = 0;
      var1.longValue6 = var5;
      var1.vec3d = Vec3d.ZERO;
   }

   private Vec3d method3792(int var1, LivingEntity var2, Class26.Entry3 var3, long var4) {
      Box var6 = var2.getBoundingBox();
      Vec3d var7 = new Vec3d(var2.getX() - var2.lastX, var2.getY() - var2.lastY, var2.getZ() - var2.lastZ);

      return switch (var1) {
         case 0 -> var6.getCenter();
         case 1 -> this.method3824(var2);
         case 2 -> this.method3793(var2, var3.intValue3, 0.7);
         case 3 -> this.method3794(var6, 0.52, 0.16);
         case 4 -> this.method3796(var6, var6.getCenter().add(var7.multiply(2.0)));
         case 5 -> this.method3795(var6);
         case 6 -> {
            double var8 = var4 / 700.0;
            yield this.method3796(
               var6,
               var6.getCenter()
                  .add(
                     Math.sin(var8) * var6.getLengthX() * 0.16,
                     Math.cos(var8 * 0.73) * var6.getLengthY() * 0.08,
                     Math.cos(var8 * 0.87) * var6.getLengthZ() * 0.16
                  )
            );
         }
         case 7 -> new Vec3d(var2.getX(), var6.minY + var6.getLengthY() * 0.38, var2.getZ());
         case 8 -> new Vec3d(var2.getX(), var6.minY + var6.getLengthY() * 0.78, var2.getZ());
         case 9 -> {
            var3.intValue3 = -var3.intValue3;
            yield this.method3793(var2, var3.intValue3, 0.58);
         }
         case 10 -> this.method3796(var6, this.method3824(var2).add(var7.multiply(1.4)));
         default -> this.method3794(var6, this.method3833(0.42F, 0.72F), 0.26);
      };
   }

   private Vec3d method3793(LivingEntity var1, int var2, double var3) {
      Box var5 = var1.getBoundingBox();
      Vec3d var6 = mc.player.getEntityPos().subtract(var1.getEntityPos()).multiply(1.0, 0.0, 1.0);
      if (var6.lengthSquared() < 1.0E-6) {
         var6 = new Vec3d(0.0, 0.0, 1.0);
      }

      var6 = var6.normalize();
      Vec3d var7 = new Vec3d(var6.z, 0.0, -var6.x);
      return this.method3796(
         var5,
         new Vec3d(
            var1.getX() + var7.x * var5.getLengthX() * 0.28 * var2,
            var5.minY + var5.getLengthY() * var3,
            var1.getZ() + var7.z * var5.getLengthZ() * 0.28 * var2
         )
      );
   }

   private Vec3d method3794(Box var1, double var2, double var4) {
      return this.method3796(
         var1,
         new Vec3d(
            var1.getCenter().x + this.random.nextGaussian() * var1.getLengthX() * var4,
            var1.minY + var1.getLengthY() * var2 + this.random.nextGaussian() * var1.getLengthY() * var4 * 0.45,
            var1.getCenter().z + this.random.nextGaussian() * var1.getLengthZ() * var4
         )
      );
   }

   private Vec3d method3795(Box var1) {
      Vec3d var2 = mc.player.getEyePos();
      double var3 = Math.abs(var2.x - var1.minX) < Math.abs(var2.x - var1.maxX) ? var1.minX : var1.maxX;
      double var5 = Math.abs(var2.z - var1.minZ) < Math.abs(var2.z - var1.maxZ) ? var1.minZ : var1.maxZ;
      return new Vec3d(
         MathHelper.lerp(0.72, var1.getCenter().x, var3),
         var1.minY + var1.getLengthY() * this.method3833(0.44F, 0.68F),
         MathHelper.lerp(0.72, var1.getCenter().z, var5)
      );
   }

   private Vec3d method3796(Box var1, Vec3d var2) {
      return new Vec3d(
         MathHelper.clamp(var2.x, var1.minX, var1.maxX), MathHelper.clamp(var2.y, var1.minY, var1.maxY), MathHelper.clamp(var2.z, var1.minZ, var1.maxZ)
      );
   }

   private long method3797(int var1) {
      short var2 = switch (var1) {
         case 1, 8 -> 90;
         default -> 150;
         case 3, 5, 11 -> 240;
         case 4, 10 -> 70;
         case 6 -> 45;
      };
      return var2 + this.random.nextInt(var2 + 90);
   }

   private float method3798(LivingEntity var1) {
      double var2 = Math.max(mc.player.getEyePos().distanceTo(var1.getBoundingBox().getCenter()), 0.1);
      return (float)Math.toDegrees(Math.atan2(var1.getWidth() * 0.5, var2));
   }

   private Rotation method3799(int var1, LivingEntity var2, Rotation var3) {
      Class26.Entry var4 = this.method3801(var1);
      if (var4.intValue != var2.getId()) {
         this.method3802(var4, var1, var2, var3);
      }

      var4.intValue9++;
      boolean var5 = TargetUtils.method3209(16.0, var3, var2);
      if (var4.intValue2 > 0) {
         var4.intValue2--;
         return var3;
      } else if (var4.intValue3 > 0) {
         var4.intValue3--;
         var4.floatValue *= 0.35F;
         var4.floatValue2 *= 0.35F;
         var4.floatValue3 *= 0.25F;
         var4.floatValue4 *= 0.25F;
         return var3;
      } else {
         if (var4.intValue4 <= 0 || var4.vec3d.equals(Vec3d.ZERO)) {
            var4.vec3d = this.method3807(var1, var2, var4);
            var4.intValue4 = this.method3806(var1);
            var4.intValue5 = 0;
         }

         var4.intValue4--;
         Rotation var6 = this.method3826(var4.vec3d);
         Vec2f var7 = var3.method3131(var6);
         if (var4.intValue5 <= 0 || var4.intValue6 >= var4.intValue5 || var4.rotation2.method3130(var6) > (var5 ? 4.0F : 7.0F)) {
            if (var4.intValue5 > 0 && var4.intValue6 >= var4.intValue5) {
               var4.intValue3 = this.method3805(var5);
               if (var4.intValue3 > 0) {
                  var4.intValue5 = 0;
                  return var3;
               }
            }

            this.method3803(var1, var4, var3, var6, var7.method5327());
         }

         Rotation var8 = this.method3800(var1, var2, var3, var6, var5, var4);
         var4.intValue6++;
         var4.flag = var5;
         return var8;
      }
   }

   private Rotation method3800(int var1, LivingEntity var2, Rotation var3, Rotation var4, boolean var5, Class26.Entry var6) {
      Vec2f var7 = var3.method3131(var4);
      float var8 = var7.getFloatValue();
      float var9 = var7.getFloatValue2();
      float var10 = Math.max(Rotation.method3138(), 1.0E-4F);
      float var11 = MathHelper.clamp((var6.intValue6 + 1.0F) / Math.max(var6.intValue5, 1), 0.0F, 1.0F);
      float var12;
      float var13;
      switch (var1) {
         case 0:
            float var38 = this.method3809(MathHelper.clamp((float)var6.intValue6 / Math.max(var6.intValue5, 1), 0.0F, 1.0F));
            float var47 = this.method3809(var11);
            Vec2f var52 = var6.rotation.method3131(var6.rotation2);
            var12 = var52.getFloatValue() * (var47 - var38);
            var13 = var52.getFloatValue2() * (var47 - var38);
            var12 = MathHelper.clamp(var12, -19.65F, 19.65F);
            var13 = MathHelper.clamp(var13, -10.5F, 10.5F);
            break;
         case 1:
            float var37 = MathHelper.clamp(var6.floatValue7, 0.18F, 0.84F);
            float var45 = var11 < var37 ? (float)Math.pow(var11 / var37, 1.35) : (float)Math.pow((1.0F - var11) / (1.0F - var37), 0.72);
            var45 = MathHelper.clamp(var45, 0.12F, 1.0F);
            float var51 = Math.max(var6.intValue5 - var6.intValue6, 1);
            var12 = var8 / var51 * (0.65F + var45 * 0.72F);
            var13 = var9 / var51 * (0.58F + var45 * 0.68F);
            var12 = MathHelper.clamp(var12, -12.6F, 12.6F);
            var13 = MathHelper.clamp(var13, -7.5F, 7.5F);
            break;
         case 2:
            Vec2f var36 = var3.method3131(var6.rotation2);
            float var44 = Math.max(var36.method5327(), 1.0E-4F);
            float var50 = MathHelper.clamp(var44 * 0.74F, 1.35F, 41.7F);
            var12 = var36.getFloatValue() / var44 * Math.min(var44, var50);
            var13 = var36.getFloatValue2() / var44 * Math.min(var44, Math.min(var50 * 0.55F, 17.8F));
            if (var6.intValue6 > var6.intValue5 * 0.58F) {
               var12 = MathHelper.clamp(var8 * 0.42F, -19.65F, 19.65F);
               var13 = MathHelper.clamp(var9 * 0.36F, -10.5F, 10.5F);
            }
            break;
         case 3:
            float var35 = Math.max(this.method3798(var2), 0.25F);
            if (var5 && var7.method5327() < var35 * 0.65F) {
               var6.floatValue5 = var6.floatValue5 * 0.72F + this.method3833(-0.16F, 0.16F);
               var6.floatValue6 = var6.floatValue6 * 0.68F + this.method3833(-0.11F, 0.11F);
               var12 = var8 * 0.14F + var6.floatValue5;
               var13 = var9 * 0.11F + var6.floatValue6;
            } else {
               var12 = var8 * 0.38F;
               var13 = var9 * 0.31F;
            }

            var12 = MathHelper.clamp(var12, -15.9F, 15.9F);
            var13 = MathHelper.clamp(var13, -9.9F, 9.9F);
            break;
         case 4:
            if (var6.intValue7 > 0) {
               var6.intValue7--;
            }

            if (!var6.flag3 && this.random.nextFloat() < 0.48F) {
               var6.flag3 = true;
            }

            if (!var6.flag4 && var6.intValue7 <= 0 && this.random.nextFloat() < 0.42F) {
               var6.flag4 = true;
            }

            var12 = var6.flag3 ? var8 * this.method3833(0.28F, 0.62F) : 0.0F;
            var13 = var6.flag4 ? var9 * this.method3833(0.22F, 0.51F) : 0.0F;
            if (this.random.nextFloat() < 0.067F) {
               var13 = 0.0F;
            }

            var12 = MathHelper.clamp(var12, -30.0F, 30.0F);
            var13 = MathHelper.clamp(var13, -14.0F, 14.0F);
            break;
         case 5:
            float var34 = (float)Math.sin(var11 * Math.PI);
            float var43 = Math.max(var6.intValue5 - var6.intValue6, 1);
            var12 = var8 / var43 * (0.72F + var34 * 0.7F);
            var13 = var9 / var43 * (0.66F + var34 * 0.62F);
            var6.floatValue5 = var6.floatValue5 * 0.8F + this.method3833(-0.18F, 0.18F);
            var6.floatValue6 = var6.floatValue6 * 0.76F + this.method3833(-0.12F, 0.12F);
            var12 = MathHelper.clamp(var12 + var6.floatValue5, -19.65F, 19.65F);
            var13 = MathHelper.clamp(var13 + var6.floatValue6, -10.5F, 10.5F);
            break;
         case 6:
            float var33 = var6.flag2 ? 0.24F : 0.42F;
            float var42 = var6.flag2 ? 0.43F : 0.62F;
            var6.floatValue = var6.floatValue * var42 + var8 * var33;
            var6.floatValue2 = var6.floatValue2 * (var42 + 0.05F) + var9 * var33 * 0.72F;
            if (!var6.flag2 && var11 > var6.floatValue7) {
               var6.flag2 = true;
               var6.floatValue = var6.floatValue * this.method3833(0.28F, 0.52F);
               var6.floatValue2 = var6.floatValue2 * this.method3833(0.3F, 0.58F);
            }

            var12 = MathHelper.clamp(var6.floatValue, -24.0F, 24.0F);
            var13 = MathHelper.clamp(var6.floatValue2, -12.0F, 12.0F);
            break;
         case 7:
            float var32 = MathHelper.clamp(var8 * 0.46F, -41.7F, 41.7F);
            float var41 = MathHelper.clamp(var9 * 0.39F, -17.8F, 17.8F);
            float var49 = MathHelper.clamp(var32 - var6.floatValue, -11.0F * var10, 11.0F * var10);
            float var53 = MathHelper.clamp(var41 - var6.floatValue2, -8.0F * var10, 8.0F * var10);
            var6.floatValue3 = var6.floatValue3 + MathHelper.clamp(var49 - var6.floatValue3, -17.0F * var10, 17.0F * var10);
            var6.floatValue4 = var6.floatValue4 + MathHelper.clamp(var53 - var6.floatValue4, -11.0F * var10, 11.0F * var10);
            var6.floatValue = var6.floatValue + var6.floatValue3;
            var6.floatValue2 = var6.floatValue2 + var6.floatValue4;
            var12 = MathHelper.clamp(var6.floatValue, -41.7F, 41.7F);
            var13 = MathHelper.clamp(var6.floatValue2, -17.8F, 17.8F);
            break;
         case 8:
            float var31 = var5 ? 15.9F : 24.9F;
            float var40 = var5 ? 9.9F : 11.45F;
            float var48 = var5 ? 0.26F : 0.47F;
            if (var5 != var6.flag) {
               var6.floatValue *= var5 ? 0.56F : 0.82F;
               var6.floatValue2 *= var5 ? 0.52F : 0.8F;
            }

            var6.floatValue = var6.floatValue * (var5 ? 0.38F : 0.56F) + var8 * var48;
            var6.floatValue2 = var6.floatValue2 * (var5 ? 0.42F : 0.6F) + var9 * var48 * 0.72F;
            var12 = MathHelper.clamp(var6.floatValue, -var31, var31);
            var13 = MathHelper.clamp(var6.floatValue2, -var40, var40);
            break;
         default:
            float[] var14 = new float[]{5.4F, 12.6F, 19.65F};
            float[] var15 = new float[]{3.6F, 7.5F, 10.5F};
            float[] var16 = new float[]{0.23F, 0.38F, 0.55F};
            int var17 = MathHelper.clamp(var6.intValue8, 0, 2);
            float var18 = MathHelper.clamp(1.0F - var6.intValue9 / 900.0F, 0.72F, 1.0F);
            var6.floatValue = var6.floatValue * (0.46F + var17 * 0.07F) + var8 * var16[var17];
            var6.floatValue2 = var6.floatValue2 * (0.5F + var17 * 0.06F) + var9 * var16[var17] * 0.7F;
            var12 = MathHelper.clamp(var6.floatValue * var18, -var14[var17], var14[var17]);
            var13 = MathHelper.clamp(var6.floatValue2 * var18, -var15[var17], var15[var17]);
      }

      var12 = MathHelper.clamp(var12, -60.0F, 60.0F);
      var13 = MathHelper.clamp(var13, -40.0F, 40.0F);
      boolean var39 = var1 == 2 && var6.intValue6 <= var6.intValue5 * 0.58F || var1 == 6 && !var6.flag2;
      if (!var39) {
         if (Math.signum(var12) == Math.signum(var8) && Math.abs(var12) > Math.abs(var8)) {
            var12 = var8;
         }

         if (Math.signum(var13) == Math.signum(var9) && Math.abs(var13) > Math.abs(var9)) {
            var13 = var9;
         }
      }

      return this.method3780(var3, var3.getFloatValue() + var12, var3.getFloatValue2() + var13);
   }

   private Class26.Entry method3801(int var1) {
      if (this.entrys == null || this.entrys.length != 10) {
         this.entrys = new Class26.Entry[10];
      }

      if (this.entrys[var1] == null) {
         this.entrys[var1] = new Class26.Entry();
      }

      return this.entrys[var1];
   }

   private void method3802(Class26.Entry var1, int var2, LivingEntity var3, Rotation var4) {
      var1.intValue = var3.getId();
      var1.intValue2 = this.random.nextFloat() < 0.7F ? this.random.nextInt(3) : 3 + this.random.nextInt(4);
      var1.intValue3 = 0;
      var1.intValue4 = 0;
      var1.intValue5 = 0;
      var1.intValue6 = 0;
      var1.intValue9 = 0;
      var1.intValue8 = this.random.nextFloat() < 0.28F ? 0 : (this.random.nextFloat() < 0.78F ? 1 : 2);
      var1.floatValue = var1.floatValue2 = 0.0F;
      var1.floatValue3 = var1.floatValue4 = 0.0F;
      var1.floatValue5 = var1.floatValue6 = 0.0F;
      var1.floatValue8 = 0.52F;
      var1.floatValue9 = 0.48F;
      var1.rotation = var4;
      var1.rotation2 = var4;
      var1.vec3d = Vec3d.ZERO;
      var1.flag = false;
      var1.flag2 = false;
      var1.flag3 = var2 != 4;
      var1.flag4 = var2 != 4;
      var1.intValue7 = 0;
   }

   private void method3803(int var1, Class26.Entry var2, Rotation var3, Rotation var4, float var5) {
      var2.intValue5 = this.method3804(var1, var5);
      var2.intValue6 = 0;
      var2.rotation = var3;
      var2.rotation2 = var4;
      var2.floatValue7 = this.method3833(0.18F, 0.84F);
      var2.flag2 = false;
      var2.flag3 = var1 != 4 || this.random.nextFloat() < 0.72F;
      var2.flag4 = var1 != 4 || this.random.nextFloat() < 0.61F;
      var2.intValue7 = var1 == 4 ? this.random.nextInt(4) : 0;
      if (var1 == 2 || var1 == 6) {
         Vec2f var6 = var3.method3131(var4);
         float var7 = MathHelper.clamp(var5 / 70.0F, 0.2F, 1.0F);
         float var8 = Math.copySign(
            this.method3833(0.45F, 3.2F) * var7, var6.getFloatValue() == 0.0F ? (this.random.nextBoolean() ? 1.0F : -1.0F) : var6.getFloatValue()
         );
         float var9 = Math.copySign(
            this.method3833(0.2F, 1.35F) * var7, var6.getFloatValue2() == 0.0F ? (this.random.nextBoolean() ? 1.0F : -1.0F) : var6.getFloatValue2()
         );
         var2.rotation2 = new Rotation(var4.getFloatValue() + var8, var4.getFloatValue2() + var9);
      }
   }

   private int method3804(int var1, float var2) {
      if (var1 == 2) {
         return 2 + this.random.nextInt(5);
      } else {
         float var3 = this.random.nextFloat();
         int var4;
         if (var3 < 0.5F) {
            var4 = 3 + this.random.nextInt(7);
         } else if (var3 < 0.75F) {
            var4 = 10 + this.random.nextInt(8);
         } else if (var3 < 0.9F) {
            var4 = 18 + this.random.nextInt(13);
         } else if (var3 < 0.95F) {
            var4 = 31 + this.random.nextInt(11);
         } else {
            var4 = 42 + this.random.nextInt(30);
         }

         if (var1 == 1) {
            var4 = Math.max(var4, 10 + this.random.nextInt(12));
         }

         if (var2 > 80.0F) {
            var4 = Math.max(3, Math.round(var4 * 0.72F));
         }

         return var4;
      }
   }

   private int method3805(boolean var1) {
      float var2 = var1 ? 0.34F : 0.28F;
      if (this.random.nextFloat() < var2) {
         return 0;
      } else {
         float var3 = this.random.nextFloat();
         if (var3 < 0.7F) {
            return 1;
         } else if (var3 < 0.9F) {
            return 2 + this.random.nextInt(3);
         } else {
            return var3 < 0.98F ? 5 + this.random.nextInt(3) : 8 + this.random.nextInt(10);
         }
      }
   }

   private int method3806(int var1) {
      if (var1 == 3) {
         return 4 + this.random.nextInt(9);
      } else {
         return var1 == 5 ? 6 + this.random.nextInt(24) : 8 + this.random.nextInt(22);
      }
   }

   private Vec3d method3807(int var1, LivingEntity var2, Class26.Entry var3) {
      float var4;
      float var5;
      switch (var1) {
         case 0:
            var4 = this.method3833(0.28F, 0.76F);
            var5 = this.method3833(0.25F, 0.7F);
            break;
         case 1:
            var4 = MathHelper.clamp(0.52F + (float)this.random.nextGaussian() * 0.17F, 0.05F, 0.98F);
            var5 = MathHelper.clamp(0.48F + (float)this.random.nextGaussian() * 0.15F, 0.05F, 0.95F);
            break;
         case 2:
            var4 = this.method3833(0.12F, 0.92F);
            var5 = this.method3833(0.18F, 0.82F);
            break;
         case 3:
            var4 = MathHelper.clamp(var3.floatValue8 + this.method3833(-0.1F, 0.1F), 0.12F, 0.9F);
            var5 = MathHelper.clamp(var3.floatValue9 + this.method3833(-0.08F, 0.08F), 0.12F, 0.86F);
            break;
         case 4:
            var4 = this.method3833(0.18F, 0.86F);
            var5 = this.method3833(0.16F, 0.8F);
            break;
         case 5:
            var4 = this.method3833(0.03F, 0.99F);
            var5 = this.method3833(0.05F, 0.95F);
            break;
         case 6:
            var4 = var3.floatValue8 < 0.5F ? this.method3833(0.5F, 0.88F) : this.method3833(0.15F, 0.5F);
            var5 = this.method3833(0.22F, 0.78F);
            break;
         case 7:
            var4 = MathHelper.clamp(0.52F + (float)this.random.nextGaussian() * 0.22F, 0.04F, 0.98F);
            var5 = MathHelper.clamp(0.48F + (float)this.random.nextGaussian() * 0.19F, 0.04F, 0.95F);
            break;
         case 8:
            Vec3d var8 = this.method3824(var2);
            Box var7 = var2.getBoundingBox();
            var4 = (float)MathHelper.clamp((var8.x - var7.minX) / Math.max(var7.getLengthX(), 1.0E-5), 0.08, 0.94);
            var5 = (float)MathHelper.clamp((var8.y - var7.minY) / Math.max(var7.getLengthY(), 1.0E-5), 0.08, 0.92);
            break;
         default:
            float var6 = var3.intValue8 == 0 ? 0.22F : (var3.intValue8 == 1 ? 0.34F : 0.46F);
            var4 = MathHelper.clamp(0.52F + this.method3833(-var6, var6), 0.03F, 0.99F);
            var5 = MathHelper.clamp(0.48F + this.method3833(-var6 * 0.86F, var6 * 0.86F), 0.04F, 0.95F);
      }

      var3.floatValue8 = var4;
      var3.floatValue9 = var5;
      return this.method3808(var2, var4, var5, this.method3833(0.22F, 0.78F));
   }

   private Vec3d method3808(LivingEntity var1, float var2, float var3, float var4) {
      Box var5 = var1.getBoundingBox();
      Vec3d var6 = mc.player.getEyePos().subtract(var5.getCenter()).multiply(1.0, 0.0, 1.0);
      if (var6.lengthSquared() < 1.0E-6) {
         var6 = new Vec3d(0.0, 0.0, 1.0);
      }

      var6 = var6.normalize();
      Vec3d var7 = new Vec3d(var6.z, 0.0, -var6.x);
      double var8 = Math.max(var5.getLengthX(), var5.getLengthZ()) * 0.5;
      Vec3d var10 = var5.getCenter().add(var7.multiply((var2 - 0.5F) * var8 * 1.86)).add(var6.multiply((var4 - 0.5F) * var8 * 1.2));
      return this.method3796(var5, new Vec3d(var10.x, var5.minY + var5.getLengthY() * var3, var10.z));
   }

   private float method3809(float var1) {
      var1 = MathHelper.clamp(var1, 0.0F, 1.0F);
      return var1 * var1 * var1 * (10.0F + var1 * (-15.0F + 6.0F * var1));
   }

   private Vec3d method3810(LivingEntity var1, float var2) {
      Vec3d var3 = mc.player.getEyePos();
      Vec3d var4 = var1.getEntityPos();
      Vec3d var5 = var4.add(0.0, var1.getHeight() * 0.52, 0.0);
      Vec3d var6 = var5.subtract(var3);
      float var7 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(var6.z, var6.x)) - 90.0);
      float var8 = MathHelper.wrapDegrees(var7 - var2);
      float var9 = MathHelper.clamp(Math.abs(var8) / 40.0F, 0.0F, 1.0F);
      if (var9 <= 0.01F) {
         return var6;
      } else {
         Vec3d var10 = new Vec3d(var6.x, 0.0, var6.z);
         if (var10.lengthSquared() > 1.0E-6) {
            var10 = var10.normalize();
         }

         Vec3d var11 = new Vec3d(-var10.z, 0.0, var10.x);
         double var12 = var8 > 0.0F ? -1.0 : 1.0;
         Vec3d var14 = new Vec3d(
               var5.x + var11.x * var1.getWidth() * 0.32 * var12, var4.y + var1.getHeight() * 0.78, var5.z + var11.z * var1.getWidth() * 0.32 * var12
            )
            .subtract(var3);
         return new Vec3d(
            MathHelper.lerp((double)var9, var6.x, var14.x), MathHelper.lerp((double)var9, var6.y, var14.y), MathHelper.lerp((double)var9, var6.z, var14.z)
         );
      }
   }

   private Rotation method3811(Rotation var1, float var2, float var3) {
      float var4 = Rotation.method3138();
      var2 = var1.getFloatValue() + Math.round((var2 - var1.getFloatValue()) / var4) * var4;
      var3 = var1.getFloatValue2() + Math.round((var3 - var1.getFloatValue2()) / var4) * var4;
      return new Rotation(var2, MathHelper.clamp(var3, -89.0F, 89.0F));
   }

   private float method3812(float var1, float var2, float var3) {
      float var4 = Math.round(var1 / var3) * var3;
      if (var4 == 0.0F && Math.abs(var2) > var3 * 0.6F) {
         var4 = Math.copySign(var3, var2);
      }

      if (Math.signum(var4) == Math.signum(var2) && Math.abs(var4) > Math.abs(var2)) {
         var4 = var2;
      }

      return var4;
   }

   private Rotation method3813(Vec3d var1) {
      return Rotation.method3128(var1, mc.player.getEntityPos());
   }

   private void method3814(LivingEntity var1) {
      Box var2 = var1.getBoundingBox();
      this.doubleValue = (Math.random() - 0.5) * var2.getLengthX() * 0.15;
      this.doubleValue2 = (Math.random() - 0.5) * var2.getLengthY() * 0.09;
      this.doubleValue3 = (Math.random() - 0.5) * var2.getLengthZ() * 0.15;
   }

   private float[] method3815(float var1) {
      this.floatValue11 = this.floatValue11 + (0.031F + this.random.nextFloat() * 0.021F);
      this.floatValue12 = this.floatValue12 + (0.057F + this.random.nextFloat() * 0.014F);
      float var2 = 1.35F * MathHelper.clamp(var1 / 5.0F, 0.2F, 1.0F);
      float var3 = ((float)Math.sin(this.floatValue11 * 1.05) * 0.33F + (float)Math.sin(this.floatValue12 * 1.31 + 0.48) * 0.29F) * var2;
      float var4 = ((float)Math.cos(this.floatValue11 * 0.62 + 1.1) * 0.24F + (float)Math.cos(this.floatValue12 * 1.87 + 2.05) * 0.2F) * var2 * 0.46F;
      var3 += (this.random.nextFloat() - 0.5F) * var2 * 0.1F;
      var4 += (this.random.nextFloat() - 0.5F) * var2 * 0.07F;
      return new float[]{var3, var4};
   }

   private Rotation method3816(float var1, float var2, float var3) {
      var2 = MathHelper.clamp(var2, -89.0F, 89.0F);
      var1 -= (var1 - this.floatValue9) % var3;
      var2 -= (var2 - this.floatValue10) % var3;
      this.floatValue9 = var1;
      this.floatValue10 = var2;
      return new Rotation(var1, var2);
   }

   private float method3817() {
      double var1 = mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
      return (float)(var1 * var1 * var1 * 1.2);
   }

   private boolean method3818(LivingEntity var1) {
      Vec3d var2 = new Vec3d(mc.player.getX() - mc.player.lastX, mc.player.getY() - mc.player.lastY, mc.player.getZ() - mc.player.lastZ);
      Vec3d var3 = new Vec3d(var1.getX() - var1.lastX, var1.getY() - var1.lastY, var1.getZ() - var1.lastZ);
      Vec3d var4 = var1.getEntityPos().subtract(mc.player.getEntityPos()).normalize();
      double var5 = var2.dotProduct(var4) + var3.dotProduct(var4.multiply(-1.0));
      double var7 = Math.hypot(mc.player.getX() - var1.getX(), mc.player.getZ() - var1.getZ());
      return var5 > 0.05 && var7 < 4.0;
   }

   private Box method3819(LivingEntity var1) {
      Box var2 = var1.getBoundingBox();
      double var3 = var1.isGliding() ? -0.5 : 0.1;
      double var5 = var1.isGliding() ? -0.5 : 0.1;
      return new Box(
         var2.minX + var2.getLengthX() * var3 / 2.0,
         var2.minY,
         var2.minZ + var2.getLengthZ() * var3 / 2.0,
         var2.maxX - var2.getLengthX() * var3 / 2.0,
         var2.maxY - var2.getLengthY() * var5,
         var2.maxZ - var2.getLengthZ() * var3 / 2.0
      );
   }

   private float method3820(float var1) {
      var1 = MathHelper.clamp(var1, 0.0F, 1.0F);
      float var2 = 1.0F - var1;
      return 1.0F - var2 * var2 * var2;
   }

   private float method3821(float var1) {
      var1 = MathHelper.clamp(var1, 0.0F, 1.0F);
      return var1 < 0.5F ? 2.0F * var1 * var1 : 1.0F - (float)Math.pow(-2.0F * var1 + 2.0F, 2.0) / 2.0F;
   }

   private float method3822(float var1, float var2, float var3) {
      return var1 + MathHelper.wrapDegrees(var2 - var1) * MathHelper.clamp(var3, 0.0F, 1.0F);
   }

   private float method3823(float var1, float var2) {
      return MathHelper.lerp(this.secureRandom.nextFloat(), var1, var2);
   }

   private Vec3d method3824(LivingEntity var1) {
      Box var2 = var1.getBoundingBox();
      Vec3d var3 = mc.player.getEyePos();
      return new Vec3d(
         MathHelper.clamp(var3.x, var2.minX, var2.maxX), MathHelper.clamp(var3.y, var2.minY, var2.maxY), MathHelper.clamp(var3.z, var2.minZ, var2.maxZ)
      );
   }

   private Vec3d method3825(LivingEntity var1) {
      Vec3d var2 = this.method3824(var1);
      Box var3 = var1.getBoundingBox();
      return new Vec3d(var2.x, MathHelper.clamp(MathHelper.lerp(0.5, mc.player.getY(), var1.getEyeY()), var3.minY, var3.maxY), var2.z);
   }

   private Rotation method3826(Vec3d var1) {
      return Rotation.method3128(var1, mc.player.getEyePos());
   }

   private Rotation method3827(Rotation var1, Rotation var2, float var3, float var4) {
      Vec2f var5 = var1.method3131(var2);
      return new Rotation(
         var1.getFloatValue() + MathHelper.clamp(var5.getFloatValue(), -var3, var3),
         var1.getFloatValue2() + MathHelper.clamp(var5.getFloatValue2(), -var4, var4)
      );
   }

   private void method3828() {
      if (mc.player.age % 500 == 0) {
         this.entry2 = new Class26.Entry2();
         this.floatValue2 = 1.0F;
         this.longValue2 = System.currentTimeMillis();
      }
   }

   private void method3829(Rotation var1, Rotation var2) {
      Vec2f var3 = var1.method3131(var2);
      if (Math.abs(var3.getFloatValue()) + Math.abs(var3.getFloatValue2()) < 10.0F) {
         this.floatValue2 = Math.max(0.0F, this.floatValue2 - 0.05F);
      }
   }

   private float method3830(float var1) {
      float var2 = Rotation.method3138();
      return Math.round(var1 / var2) * var2;
   }

   private float method3831(float var1, float var2) {
      return MathHelper.clamp(var1 / 90.0F, 0.2F, 1.0F) * var2;
   }

   private float method3832(float var1) {
      return MathHelper.clamp(var1 / 45.0F, 0.2F, 1.0F);
   }

   private float method3833(float var1, float var2) {
      return var1 + this.random.nextFloat() * (var2 - var1);
   }

   private void method3834() {
      this.flag = false;
      this.flag2 = false;
      this.floatValue = 1.0F;
      this.entry2 = new Class26.Entry2();
      this.floatValue2 = 1.0F;
      this.longValue2 = System.currentTimeMillis();
      this.longValue3 = this.longValue2;
      this.intValue2 = Integer.MIN_VALUE;
      this.flag3 = false;
      this.flag4 = false;
      this.intValue3 = Integer.MIN_VALUE;
      this.floatValue14 = 0.0F;
      this.floatValue15 = 0.0F;
      this.intValue8 = Integer.MIN_VALUE;
      this.intValue9 = Integer.MIN_VALUE;
      this.intValue10 = Integer.MIN_VALUE;
      this.intValue11 = Integer.MIN_VALUE;
      this.intValue13 = Integer.MIN_VALUE;
      this.intValue14 = Integer.MIN_VALUE;
      this.intValue15 = Integer.MIN_VALUE;
      this.intValue16 = Integer.MIN_VALUE;
      this.intValue19 = Integer.MIN_VALUE;
      this.intValue20 = Integer.MIN_VALUE;
      this.entry3s = new Class26.Entry3[12];
      this.entrys = new Class26.Entry[10];
   }

   private static final class Entry {
      private int intValue = Integer.MIN_VALUE;
      private int intValue2;
      private int intValue3;
      private int intValue4;
      private int intValue5;
      private int intValue6;
      private int intValue7;
      private int intValue8;
      private int intValue9;
      private float floatValue;
      private float floatValue2;
      private float floatValue3;
      private float floatValue4;
      private float floatValue5;
      private float floatValue6;
      private float floatValue7;
      private float floatValue8 = 0.52F;
      private float floatValue9 = 0.48F;
      private boolean flag;
      private boolean flag2;
      private boolean flag3;
      private boolean flag4;
      private Vec3d vec3d = Vec3d.ZERO;
      private Rotation rotation = Rotation.INSTANCE;
      private Rotation rotation2 = Rotation.INSTANCE;
   }

   private static final class Entry2 {
      private final int intValue = new Random().nextInt();

      private double method3835(double var1) {
         return this.method3836(var1, 0.0);
      }

      private double method3836(double var1, double var3) {
         int var5 = (int)Math.floor(var1);
         int var6 = (int)Math.floor(var3);
         double var7 = method3838(var1 - var5);
         double var9 = method3838(var3 - var6);
         double var11 = method3839(this.method3837(var5, var6), this.method3837(var5 + 1, var6), var7);
         double var13 = method3839(this.method3837(var5, var6 + 1), this.method3837(var5 + 1, var6 + 1), var7);
         return method3839(var11, var13, var9);
      }

      private double method3837(int var1, int var2) {
         int var3 = var1 * 374761393 + var2 * 668265263 + this.intValue * 1442695041;
         var3 = (var3 ^ var3 >>> 13) * 1274126177;
         return ((var3 ^ var3 >>> 16) & 2147483647) / 1.073741823E9 - 1.0;
      }

      private static double method3838(double var0) {
         return var0 * var0 * var0 * (var0 * (var0 * 6.0 - 15.0) + 10.0);
      }

      private static double method3839(double var0, double var2, double var4) {
         return var0 + (var2 - var0) * var4;
      }
   }

   private static final class Entry3 {
      private int intValue = Integer.MIN_VALUE;
      private long longValue;
      private long longValue2;
      private long longValue3;
      private long longValue4;
      private long longValue5;
      private long longValue6;
      private int intValue2;
      private int intValue3 = 1;
      private int intValue4;
      private float floatValue;
      private float floatValue2;
      private float floatValue3;
      private float floatValue4;
      private float floatValue5;
      private float floatValue6;
      private float floatValue7;
      private float floatValue8;
      private Vec3d vec3d = Vec3d.ZERO;
      private Rotation rotation = Rotation.INSTANCE;
      private Rotation rotation2 = Rotation.INSTANCE;
      private long longValue7;
      private long longValue8;
   }
}

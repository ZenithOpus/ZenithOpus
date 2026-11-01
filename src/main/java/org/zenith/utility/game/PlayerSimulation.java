package org.zenith.utility.game;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import org.zenith.Zenith;
import org.zenith.ZenithAccess;
import org.zenith.utility.misc.Handler6;
import org.zenith.utility.misc.MovementInputUtils;

public class PlayerSimulation implements ZenithAccess {
   public final PlayerEntity playerEntity;
   public final PlayerSimulation.Entry entry;
   public Vec3d vec3d;
   public Vec3d vec3d2;
   public Box box;
   public float floatValue;
   public float floatValue2;
   public boolean flag;
   public float floatValue3;
   public int intValue;
   public boolean flag2;
   public boolean flag3;
   public boolean flag4;
   public boolean flag5;
   public boolean flag6;
   public boolean flag7;
   public boolean flag8;
   public boolean flag9;
   public EntityPose entityPose;
   public boolean inSneakingPose;
   public boolean flag10;
   private final Object2DoubleMap<TagKey<Fluid>> object2DoubleMap;
   private final HashSet<TagKey<Fluid>> hashSet;
   private int intValue2 = 0;
   private boolean flag11 = false;

   public PlayerSimulation(
      PlayerEntity var1,
      PlayerSimulation.Entry var2,
      Vec3d var3,
      Vec3d var4,
      Box var5,
      float var6,
      float var7,
      boolean var8,
      float var9,
      int var10,
      boolean var11,
      boolean var12,
      boolean var13,
      boolean var14,
      boolean var15,
      boolean var16,
      boolean var17,
      boolean var18,
      EntityPose var19,
      boolean var20,
      boolean var21,
      Object2DoubleMap<TagKey<Fluid>> var22,
      HashSet<TagKey<Fluid>> var23
   ) {
      this.playerEntity = var1;
      this.entry = var2;
      this.vec3d = var3;
      this.vec3d2 = var4;
      this.box = var5;
      this.floatValue = var6;
      this.floatValue2 = var7;
      this.flag = var8;
      this.floatValue3 = var9;
      this.intValue = var10;
      this.flag2 = var11;
      this.flag3 = var12;
      this.flag4 = var13;
      this.flag5 = var14;
      this.flag6 = var15;
      this.flag7 = var16;
      this.flag8 = var17;
      this.flag9 = var18;
      this.entityPose = var19;
      this.inSneakingPose = var20;
      this.flag10 = var21;
      this.object2DoubleMap = var22;
      this.hashSet = var23;
   }

   public static PlayerSimulation method6031(int var0) {
      PlayerSimulation var1 = method6033(PlayerSimulation.Entry.method6092(mc.player.input.playerInput));

      for (int var2 = 0; var2 < var0; var2++) {
         var1.tick();
      }

      return var1;
   }

   public static PlayerSimulation method6032(PlayerEntity var0, int var1) {
      PlayerSimulation var2 = method6034(var0, PlayerSimulation.Entry.method6093(var0));

      for (int var3 = 0; var3 < var1; var3++) {
         var2.tick();
      }

      return var2;
   }

   public static PlayerSimulation method6033(PlayerSimulation.Entry var0) {
      ClientPlayerEntity var1 = mc.player;
      return new PlayerSimulation(
         var1,
         var0,
         var1.getEntityPos(),
         var1.getVelocity(),
         var1.getBoundingBox(),
         Zenith.getInstance().getRotationManager().method7844().getFloatValue(),
         Zenith.getInstance().getRotationManager().method7844().getFloatValue2(),
         var1.isSprinting(),
         (float)var1.fallDistance,
         var1.jumpingCooldown,
         var1.jumping,
         var1.isGliding(),
         var1.isOnGround(),
         var1.horizontalCollision,
         var1.verticalCollision,
         var1.isTouchingWater(),
         var1.isSwimming(),
         var1.isSubmergedInWater(),
         var1.getPose(),
         var1.isInSneakingPose(),
         var1.isCrawling(),
         new Object2DoubleArrayMap(var1.fluidHeight),
         new HashSet<>(var1.submergedFluidTag)
      );
   }

   public static PlayerSimulation method6034(PlayerEntity var0, PlayerSimulation.Entry var1) {
      Vec3d var2 = var0.getTrackedPosition().getPos();
      Vec3d var3 = ((Handler6)var0).zenithDLC$getServerDelta();
      return new PlayerSimulation(
         var0,
         var1,
         var2,
         var3.multiply(0.5),
         var0.getDimensions(var0.getPose()).getBoxAt(var2),
         var0.getInterpolator() == null ? var0.getYaw() : var0.getInterpolator().getLerpedYaw(),
         var0.getInterpolator() == null ? var0.getPitch() : var0.getInterpolator().getLerpedPitch(),
         var0.isSprinting(),
         (float)var0.fallDistance,
         var0.jumpingCooldown,
         var0.jumping,
         var0.isGliding(),
         var0.isOnGround(),
         var0.horizontalCollision,
         var0.verticalCollision,
         var0.isTouchingWater(),
         var0.isSwimming(),
         var0.isSubmergedInWater(),
         var0.getPose(),
         var0.isInSneakingPose(),
         var0.isCrawling(),
         new Object2DoubleArrayMap(var0.fluidHeight),
         new HashSet<>(var0.submergedFluidTag)
      );
   }

   public Vec3d getVec3d() {
      return this.vec3d;
   }

   public void tick() {
      this.intValue2++;
      this.flag11 = false;
      if (!(this.vec3d.y <= -70.0)) {
         this.method6065();
         this.method6080(FluidTags.LAVA, 0.007);
         this.method6075();
         this.method6066();
         this.method6067();
         this.method6068();
         this.entry.update();
         this.method6070();
         this.method6069();
         if (this.isFlag7() && this.entry.playerInput.sneak() && this.playerEntity.shouldSwimInFluids()) {
            this.method6060();
         }

         if (this.intValue > 0) {
            this.intValue--;
         }

         this.flag2 = this.entry.playerInput.jump();
         double var1 = this.vec3d2.x;
         double var3 = this.vec3d2.y;
         double var5 = this.vec3d2.z;
         if (Math.abs(this.vec3d2.x) < 0.003) {
            var1 = 0.0;
         }

         if (Math.abs(this.vec3d2.y) < 0.003) {
            var3 = 0.0;
         }

         if (Math.abs(this.vec3d2.z) < 0.003) {
            var5 = 0.0;
         }

         if (this.flag4) {
            this.flag3 = false;
         }

         this.vec3d2 = new Vec3d(var1, var3, var5);
         if (this.flag2 && this.playerEntity.shouldSwimInFluids()) {
            double var7 = this.method6064() ? this.method6079(FluidTags.LAVA) : this.method6079(FluidTags.WATER);
            boolean var9 = this.isFlag7() && var7 > 0.0;
            double var10 = this.method6063();
            if (!var9 || this.flag4 && !(var7 > var10)) {
               if (!this.method6064() || this.flag4 && !(var7 > var10)) {
                  if ((this.flag4 || var9 && var7 <= var10) && this.intValue == 0) {
                     this.method6044();
                     this.intValue = 10;
                  }
               } else {
                  this.method6061(FluidTags.LAVA);
               }
            } else {
               this.method6061(FluidTags.WATER);
            }
         } else {
            this.intValue = 0;
         }

         float var12 = this.entry.floatValue2 * 0.98F;
         float var8 = this.entry.floatValue * 0.98F;
         float var13 = 0.0F;
         if (this.method6084(StatusEffects.SLOW_FALLING) || this.method6084(StatusEffects.LEVITATION)) {
            this.method6043();
         }

         this.method6035(new Vec3d(var12, var13, var8));
      }
   }

   private void method6035(Vec3d var1) {
      if (this.flag8 && !this.playerEntity.hasVehicle()) {
         double var2 = this.method6082().y;
         double var4 = var2 < -0.2 ? 0.085 : 0.06;
         BlockPos var6 = new BlockPos(MathHelper.floor(this.vec3d.x), MathHelper.floor(this.vec3d.y + 1.0 - 0.1), MathHelper.floor(this.vec3d.z));
         if (var2 <= 0.0 || this.entry.playerInput.jump() || !this.playerEntity.getEntityWorld().getBlockState(var6).getFluidState().isEmpty()) {
            this.vec3d2 = this.vec3d2.add(0.0, (var2 - this.vec3d2.y) * var4, 0.0);
         }
      }

      double var18 = this.vec3d2.y;
      double var19 = this.method6057();
      boolean var20 = this.vec3d2.y <= 0.0;
      if (this.isFlag7() && this.playerEntity.shouldSwimInFluids()) {
         double var24 = this.vec3d.y;
         float var27 = this.isFlag() ? 0.9F : 0.8F;
         float var29 = 0.02F;
         float var31 = (float)this.method6086(EntityAttributes.WATER_MOVEMENT_EFFICIENCY);
         if (!this.flag4) {
            var31 *= 0.5F;
         }

         if (var31 > 0.0F) {
            var27 += (0.54600006F - var27) * var31;
            var29 += (this.method6040() - var29) * var31;
         }

         if (this.method6084(StatusEffects.DOLPHINS_GRACE)) {
            var27 = 0.96F;
         }

         this.method6037(var29, var1);
         this.method6041(this.vec3d2);
         Vec3d var32 = this.vec3d2;
         if (this.flag5 && this.method6046()) {
            var32 = new Vec3d(var32.x, 0.2, var32.z);
         }

         this.vec3d2 = var32.multiply(var27, 0.8, var27);
         Vec3d var33 = this.method6058(var19, var20, this.vec3d2);
         this.vec3d2 = var33;
         if (this.flag5 && this.method6055(var33.x, var33.y + 0.6 - this.vec3d.y + var24, var33.z)) {
            this.vec3d2 = new Vec3d(var33.x, 0.3, var33.z);
         }
      } else if (this.method6064() && this.playerEntity.shouldSwimInFluids()) {
         double var23 = this.vec3d.y;
         this.method6037(0.02F, var1);
         this.method6041(this.vec3d2);
         if (this.method6079(FluidTags.LAVA) <= this.method6063()) {
            this.vec3d2 = this.vec3d2.multiply(0.5, 0.8, 0.5);
            this.vec3d2 = this.method6058(var19, var20, this.vec3d2);
         } else {
            this.vec3d2 = this.vec3d2.multiply(0.5);
         }

         if (var19 != 0.0) {
            this.vec3d2 = this.vec3d2.add(0.0, -var19 / 4.0, 0.0);
         }

         if (this.flag5 && this.method6055(this.vec3d2.x, this.vec3d2.y + 0.6 - this.vec3d.y + var23, this.vec3d2.z)) {
            this.vec3d2 = new Vec3d(this.vec3d2.x, 0.3, this.vec3d2.z);
         }
      } else if (this.flag3) {
         Vec3d var9 = this.vec3d2;
         Vec3d var10 = this.method6082();
         float var11 = this.floatValue2 * (float) (Math.PI / 180.0);
         double var12 = Math.sqrt(var10.x * var10.x + var10.z * var10.z);
         double var14 = this.vec3d2.horizontalLength();
         double var16 = MathHelper.square(MathHelper.cos(var11));
         var9 = this.vec3d2.add(0.0, var19 * (-1.0 + var16 * 0.75), 0.0);
         if (var9.y < 0.0 && var12 > 0.0) {
            double var7 = var9.y * -0.1 * var16;
            var9 = var9.add(var10.x * var7 / var12, var7, var10.z * var7 / var12);
         }

         if (var11 < 0.0F && var12 > 0.0) {
            double var21 = var14 * -MathHelper.sin(var11) * 0.04;
            var9 = var9.add(-var10.x * var21 / var12, var21 * 3.2, -var10.z * var21 / var12);
         }

         if (var12 > 0.0) {
            var9 = var9.add((var10.x / var12 * var14 - var9.x) * 0.1, 0.0, (var10.z / var12 * var14 - var9.z) * 0.1);
         }

         this.vec3d2 = var9.multiply(0.99, 0.98, 0.99);
         this.method6041(this.vec3d2);
      } else {
         BlockPos var22 = this.method6062();
         float var8 = this.flag4 ? this.playerEntity.getEntityWorld().getBlockState(var22).getBlock().getSlipperiness() : 1.0F;
         float var26 = var8 * 0.91F;
         Vec3d var28 = this.method6036(var1, var8);
         double var30 = var28.y;
         if (this.method6084(StatusEffects.LEVITATION)) {
            StatusEffectInstance var13 = this.method6085(StatusEffects.LEVITATION);
            if (var13 != null) {
               var30 += (0.05 * (var13.getAmplifier() + 1) - var28.y) * 0.2;
            }
         } else if (this.playerEntity.getEntityWorld().isClient() && !this.playerEntity.getEntityWorld().isChunkLoaded(var22)) {
            var30 = this.vec3d.y > this.playerEntity.getEntityWorld().getBottomY() ? -0.1 : 0.0;
         } else if (var19 != 0.0) {
            var30 -= var19;
         }

         if (this.playerEntity.hasNoDrag()) {
            this.vec3d2 = new Vec3d(var28.x, var30, var28.z);
         } else {
            this.vec3d2 = new Vec3d(var28.x * var26, var30 * 0.98F, var28.z * var26);
         }
      }

      if (this.playerEntity.getAbilities().flying && !this.playerEntity.hasVehicle()) {
         this.vec3d2 = new Vec3d(this.vec3d2.x, var18 * 0.6, this.vec3d2.z);
         this.method6043();
      }
   }

   private Vec3d method6036(Vec3d var1, float var2) {
      this.method6037(this.method6038(var2), var1);
      this.vec3d2 = this.method6045(this.vec3d2);
      this.method6041(this.vec3d2);
      Vec3d var3 = this.vec3d2;
      BlockPos var4 = this.method6089(this.vec3d);
      BlockState var5 = this.method6090(var4);
      if ((this.flag5 || this.flag2)
         && (this.method6046() || var5 != null && var5.isOf(Blocks.POWDER_SNOW) && PowderSnowBlock.canWalkOnPowderSnow(this.playerEntity))) {
         var3 = new Vec3d(var3.x, 0.2, var3.z);
      }

      return var3;
   }

   private void method6037(float var1, Vec3d var2) {
      Vec3d var3 = Entity.movementInputToVelocity(var2, var1, this.floatValue);
      this.vec3d2 = this.vec3d2.add(var3);
   }

   private float method6038(float var1) {
      return this.flag4 ? this.method6040() * (0.21600002F / (var1 * var1 * var1)) : this.method6039();
   }

   private float method6039() {
      return 0.02F;
   }

   private float method6040() {
      return (float)this.method6086(EntityAttributes.MOVEMENT_SPEED);
   }

   private void method6041(Vec3d var1) {
      Vec3d var2 = this.method6048(var1);
      Vec3d var3 = this.method6042(var2);
      if (var3.lengthSquared() > 1.0E-7) {
         this.vec3d = this.vec3d.add(var3);
         this.box = this.box.offset(var3);
      }

      boolean var4 = !MathHelper.approximatelyEquals(var2.x, var3.x);
      boolean var5 = !MathHelper.approximatelyEquals(var2.z, var3.z);
      this.flag5 = var4 || var5;
      this.flag6 = var2.y != var3.y;
      this.flag4 = this.flag6 && var2.y < 0.0;
      if (!this.isFlag7()) {
         this.method6065();
      }

      if (this.flag4) {
         this.method6043();
      } else if (var2.y < 0.0) {
         this.floatValue3 = this.floatValue3 - (float)var2.y;
      }

      Vec3d var6 = this.vec3d2;
      if (this.flag5 || this.flag6) {
         this.vec3d2 = new Vec3d(var4 ? 0.0 : var6.x, this.flag4 ? 0.0 : var6.y, var5 ? 0.0 : var6.z);
      }

      float var7 = this.method6059();
      this.vec3d2 = this.vec3d2.multiply(var7, 1.0, var7);
   }

   private Vec3d method6042(Vec3d var1) {
      Box var2 = this.box;
      java.util.List<net.minecraft.util.shape.VoxelShape> var3 = Collections.emptyList();
      Vec3d var4;
      if (var1.lengthSquared() == 0.0) {
         var4 = var1;
      } else {
         var4 = Entity.adjustMovementForCollisions(this.playerEntity, var1, var2, this.playerEntity.getEntityWorld(), var3);
      }

      boolean var5 = var1.x != var4.x;
      boolean var6 = var1.y != var4.y;
      boolean var7 = var1.z != var4.z;
      boolean var8 = this.flag4 || var6 && var1.y < 0.0;
      if (this.playerEntity.getStepHeight() > 0.0F && var8 && (var5 || var7)) {
         Vec3d var9 = Entity.adjustMovementForCollisions(
            this.playerEntity, new Vec3d(var1.x, this.playerEntity.getStepHeight(), var1.z), var2, this.playerEntity.getEntityWorld(), var3
         );
         Vec3d var10 = Entity.adjustMovementForCollisions(
            this.playerEntity,
            new Vec3d(0.0, this.playerEntity.getStepHeight(), 0.0),
            var2.stretch(var1.x, 0.0, var1.z),
            this.playerEntity.getEntityWorld(),
            var3
         );
         Vec3d var11 = Entity.adjustMovementForCollisions(
               this.playerEntity, new Vec3d(var1.x, 0.0, var1.z), var2.offset(var10), this.playerEntity.getEntityWorld(), var3
            )
            .add(var10);
         if (var10.y < this.playerEntity.getStepHeight() && var11.horizontalLengthSquared() > var9.horizontalLengthSquared()) {
            var9 = var11;
         }

         if (var9.horizontalLengthSquared() > var4.horizontalLengthSquared()) {
            return var9.add(
               Entity.adjustMovementForCollisions(
                  this.playerEntity, new Vec3d(0.0, -var9.y + var1.y, 0.0), var2.offset(var9), this.playerEntity.getEntityWorld(), var3
               )
            );
         }
      }

      return var4;
   }

   private void method6043() {
      this.floatValue3 = 0.0F;
   }

   public void method6044() {
      float var1 = this.method6052();
      if (!(var1 <= 1.0E-5F)) {
         this.vec3d2 = new Vec3d(this.vec3d2.x, Math.max((double)var1, this.vec3d2.y), this.vec3d2.z);
         if (this.isFlag()) {
            float var2 = (float)Math.toRadians(this.floatValue);
            this.vec3d2 = this.vec3d2.add(-MathHelper.sin(var2) * 0.2, 0.0, MathHelper.cos(var2) * 0.2);
         }
      }
   }

   private Vec3d method6045(Vec3d var1) {
      if (!this.method6046()) {
         return var1;
      } else {
         this.method6043();
         double var2 = MathHelper.clamp(var1.x, -0.15F, 0.15F);
         double var4 = MathHelper.clamp(var1.z, -0.15F, 0.15F);
         double var6 = Math.max(var1.y, -0.15F);
         if (var6 < 0.0 && !this.method6090(this.method6089(this.vec3d)).isOf(Blocks.SCAFFOLDING) && this.method6072()) {
            var6 = 0.0;
         }

         return new Vec3d(var2, var6, var4);
      }
   }

   public boolean method6046() {
      BlockPos var1 = this.method6089(this.vec3d);
      BlockState var2 = this.method6090(var1);
      return var2.isIn(BlockTags.CLIMBABLE) ? true : var2.getBlock() instanceof TrapdoorBlock && this.method6047(var1, var2);
   }

   private boolean method6047(BlockPos var1, BlockState var2) {
      if (!var2.get(TrapdoorBlock.OPEN)) {
         return false;
      } else {
         BlockState var3 = this.playerEntity.getEntityWorld().getBlockState(var1.down());
         return var3.isOf(Blocks.LADDER) && var3.get(LadderBlock.FACING).equals(var2.get(TrapdoorBlock.FACING));
      }
   }

   private Vec3d method6048(Vec3d var1) {
      double var2 = this.playerEntity.getStepHeight();
      if (var1.y <= 0.0 && !this.playerEntity.getAbilities().flying && this.method6049() && this.method6050(var2)) {
         double var4 = var1.x;
         double var6 = var1.z;
         double var8 = 0.05;
         double var10 = Math.signum(var4) * var8;

         double var12;
         for (var12 = Math.signum(var6) * var8; var4 != 0.0 && this.method6051(var4, 0.0, var2); var4 -= var10) {
            if (Math.abs(var4) <= var8) {
               var4 = 0.0;
               break;
            }
         }

         while (var6 != 0.0 && this.method6051(0.0, var6, var2)) {
            if (Math.abs(var6) <= var8) {
               var6 = 0.0;
               break;
            }

            var6 -= var12;
         }

         while (var4 != 0.0 && var6 != 0.0 && this.method6051(var4, var6, var2)) {
            if (Math.abs(var4) <= var8) {
               var4 = 0.0;
            } else {
               var4 -= var10;
            }

            if (Math.abs(var6) <= var8) {
               var6 = 0.0;
               break;
            }

            var6 -= var12;
         }

         if (var1.x != var4 || var1.z != var6) {
            this.flag11 = true;
         }

         var1 = new Vec3d(var4, var1.y, var6);
      }

      return var1;
   }

   public boolean method6049() {
      return this.entry.playerInput.sneak() || this.entry.flag;
   }

   private boolean method6050(double var1) {
      return this.flag4 || this.floatValue3 < var1 && !this.method6051(0.0, 0.0, var1 - this.floatValue3);
   }

   private boolean method6051(double var1, double var3, double var5) {
      Box var7 = this.box;
      return this.playerEntity
         .getEntityWorld()
         .isSpaceEmpty(this.playerEntity, new Box(var7.minX + var1, var7.minY - var5 - 1.0E-5, var7.minZ + var3, var7.maxX + var1, var7.minY, var7.maxZ + var3));
   }

   private boolean isFlag() {
      return this.flag;
   }

   private float method6052() {
      return (float)this.method6086(EntityAttributes.JUMP_STRENGTH) * this.method6054() + this.method6053();
   }

   private float method6053() {
      if (this.method6084(StatusEffects.JUMP_BOOST)) {
         StatusEffectInstance var1 = this.method6085(StatusEffects.JUMP_BOOST);
         return 0.1F * (var1.getAmplifier() + 1);
      } else {
         return 0.0F;
      }
   }

   private float method6054() {
      float var1 = 0.0F;
      Block var2 = this.method6090(this.method6089(this.vec3d)).getBlock();
      if (var2 != null) {
         var1 = var2.getJumpVelocityMultiplier();
      }

      float var3 = 0.0F;
      Block var4 = this.method6090(this.method6062()).getBlock();
      if (var4 != null) {
         var3 = var4.getJumpVelocityMultiplier();
      }

      return var1 == 1.0F ? var3 : var1;
   }

   private boolean method6055(double var1, double var3, double var5) {
      return this.method6056(this.box.offset(var1, var3, var5));
   }

   private boolean method6056(Box var1) {
      return this.playerEntity.getEntityWorld().isSpaceEmpty(this.playerEntity, var1) && !this.playerEntity.getEntityWorld().containsFluid(var1);
   }

   private double method6057() {
      double var1 = this.playerEntity.hasNoGravity() ? 0.0 : this.method6086(EntityAttributes.GRAVITY);
      return this.vec3d2.y <= 0.0 && this.method6084(StatusEffects.SLOW_FALLING) ? Math.min(var1, 0.01) : var1;
   }

   private Vec3d method6058(double var1, boolean var3, Vec3d var4) {
      if (var1 != 0.0 && !this.isFlag()) {
         double var5 = var3 && Math.abs(var4.y - 0.005) >= 0.003 && Math.abs(var4.y - var1 / 16.0) < 0.003 ? -0.003 : var4.y - var1 / 16.0;
         return new Vec3d(var4.x, var5, var4.z);
      } else {
         return var4;
      }
   }

   private float method6059() {
      BlockState var1 = this.method6090(this.method6089(this.vec3d));
      float var2 = var1.getBlock().getVelocityMultiplier();
      if (!var1.isOf(Blocks.WATER) && !var1.isOf(Blocks.BUBBLE_COLUMN)) {
         var2 = var2 == 1.0F ? this.method6090(this.method6062()).getBlock().getVelocityMultiplier() : var2;
      }

      return MathHelper.lerp((float)this.method6086(EntityAttributes.MOVEMENT_EFFICIENCY), var2, 1.0F);
   }

   private void method6060() {
      this.vec3d2 = this.vec3d2.add(0.0, -0.04F, 0.0);
   }

   private void method6061(TagKey<Fluid> var1) {
      this.vec3d2 = this.vec3d2.add(0.0, 0.04F, 0.0);
   }

   private BlockPos method6062() {
      return BlockPos.ofFloored(this.vec3d.x, this.box.minY - 0.5000001, this.vec3d.z);
   }

   private double method6063() {
      return this.method6077() < 0.4 ? 0.0 : 0.4;
   }

   private boolean isFlag7() {
      return this.flag7;
   }

   public boolean method6064() {
      return this.object2DoubleMap.getDouble(FluidTags.LAVA) > 0.0;
   }

   private void method6065() {
      if (this.playerEntity.getVehicle() instanceof BoatEntity) {
         BoatEntity var1 = (BoatEntity)this.playerEntity.getVehicle();
         if (!var1.isSubmergedInWater()) {
            this.flag7 = false;
            return;
         }
      }

      if (this.method6080(FluidTags.WATER, 0.014)) {
         this.method6043();
         this.flag7 = true;
      } else {
         this.flag7 = false;
      }
   }

   private void method6066() {
      if (this.flag8) {
         this.flag8 = this.isFlag() && this.isFlag7() && !this.playerEntity.hasVehicle();
      } else {
         this.flag8 = this.isFlag()
            && this.method6078()
            && !this.playerEntity.hasVehicle()
            && this.playerEntity.getEntityWorld().getFluidState(this.method6089(this.vec3d)).isIn(FluidTags.WATER);
      }
   }

   private void method6067() {
      if (!this.method6073(EntityPose.SWIMMING)) {
         this.flag10 = this.entityPose == EntityPose.SWIMMING && !this.isFlag7();
      } else {
         EntityPose var1;
         if (this.flag3) {
            var1 = EntityPose.GLIDING;
         } else if (this.playerEntity.isSleeping()) {
            var1 = EntityPose.SLEEPING;
         } else if (this.flag8) {
            var1 = EntityPose.SWIMMING;
         } else if (this.playerEntity.isUsingRiptide()) {
            var1 = EntityPose.SPIN_ATTACK;
         } else if (this.method6072() && !this.playerEntity.getAbilities().flying) {
            var1 = EntityPose.CROUCHING;
         } else {
            var1 = EntityPose.STANDING;
         }

         EntityPose var2;
         if (this.playerEntity.isSpectator() || this.playerEntity.hasVehicle() || this.method6073(var1)) {
            var2 = var1;
         } else if (this.method6073(EntityPose.CROUCHING)) {
            var2 = EntityPose.CROUCHING;
         } else {
            var2 = EntityPose.SWIMMING;
         }

         this.method6074(var2);
         this.flag10 = this.entityPose == EntityPose.SWIMMING && !this.isFlag7();
      }
   }

   private void method6068() {
      this.inSneakingPose = !this.playerEntity.getAbilities().flying
         && !this.flag8
         && !this.playerEntity.hasVehicle()
         && this.method6073(EntityPose.CROUCHING)
         && (this.method6072() || !this.playerEntity.isSleeping() && !this.method6073(EntityPose.STANDING));
   }

   private void method6069() {
      if (this.playerEntity.isUsingItem() && !this.playerEntity.hasVehicle()) {
         this.entry.floatValue2 *= 0.2F;
         this.entry.floatValue *= 0.2F;
      }

      if (this.shouldSlowDown()) {
         float var1 = (float)this.method6086(EntityAttributes.SNEAKING_SPEED);
         this.entry.floatValue2 *= var1;
         this.entry.floatValue *= var1;
      }
   }

   private void method6070() {
      if (this.shouldStopSprinting()) {
         this.flag = false;
      }

      if ((!this.isFlag7() || this.method6078()) && this.entry.playerInput.sprint() && this.canStartSprinting()) {
         this.flag = true;
      }

      if (this.flag) {
         boolean var1 = !this.method6071() || !this.canSprint();
         boolean var2 = var1 || this.flag5 && !this.playerEntity.collidedSoftly || this.isFlag7() && !this.method6078();
         if (this.flag8) {
            if (!this.flag4 && !this.entry.playerInput.sneak() && var1 || !this.isFlag7()) {
               this.flag = false;
            }
         } else if (var2) {
            this.flag = false;
         }
      }
   }

   private boolean canStartSprinting() {
      return !this.flag
         && this.isWalking()
         && this.canSprint()
         && !this.playerEntity.isUsingItem()
         && !this.method6084(StatusEffects.BLINDNESS)
         && !this.playerEntity.hasVehicle()
         && !this.flag3
         && (!this.shouldSlowDown() || this.method6078());
   }

   private boolean isWalking() {
      return this.method6078() ? this.method6071() : this.entry.floatValue >= 0.8F;
   }

   private boolean shouldStopSprinting() {
      return this.flag3
         || this.method6084(StatusEffects.BLINDNESS)
         || this.shouldSlowDown()
         || this.playerEntity.hasVehicle() && !this.isRidingCamel()
         || this.playerEntity.isUsingItem() && !this.playerEntity.hasVehicle() && !this.method6078();
   }

   private boolean isRidingCamel() {
      Entity var1 = this.playerEntity.getVehicle();
      return var1 != null && var1.getType() == EntityType.CAMEL;
   }

   private boolean method6071() {
      return this.entry.floatValue > 1.0E-5F;
   }

   private boolean canSprint() {
      return this.playerEntity.hasVehicle() || this.playerEntity.getHungerManager().getFoodLevel() > 6.0F || this.playerEntity.getAbilities().allowFlying;
   }

   private boolean shouldSlowDown() {
      return this.inSneakingPose || this.flag10;
   }

   private boolean method6072() {
      return this.entry.playerInput.sneak();
   }

   private boolean method6073(EntityPose var1) {
      return this.playerEntity.getEntityWorld().isSpaceEmpty(this.playerEntity, this.playerEntity.getDimensions(var1).getBoxAt(this.vec3d).contract(1.0E-7));
   }

   private void method6074(EntityPose var1) {
      if (this.entityPose != var1 || !this.box.equals(this.playerEntity.getDimensions(var1).getBoxAt(this.vec3d))) {
         this.entityPose = var1;
         this.box = this.playerEntity.getDimensions(var1).getBoxAt(this.vec3d);
      }
   }

   private void method6075() {
      this.flag9 = this.hashSet.contains(FluidTags.WATER);
      this.hashSet.clear();
      double var1 = this.method6076() - 0.11111111F;
      if (!(
         this.playerEntity.getVehicle() instanceof BoatEntity var4
            && !var4.isSubmergedInWater()
            && var4.getBoundingBox().maxY >= var1
            && var4.getBoundingBox().minY <= var1
      )) {
         BlockPos var8 = BlockPos.ofFloored(this.vec3d.x, var1, this.vec3d.z);
         FluidState var5 = this.playerEntity.getEntityWorld().getFluidState(var8);
         double var6 = var8.getY() + var5.getHeight(this.playerEntity.getEntityWorld(), var8);
         if (var6 > var1) {
            this.hashSet.addAll(var5.streamTags().toList());
         }
      }
   }

   private double method6076() {
      return this.vec3d.y + this.method6077();
   }

   private float method6077() {
      return this.playerEntity.getDimensions(this.entityPose).eyeHeight();
   }

   public boolean method6078() {
      return this.flag9 && this.isFlag7();
   }

   private double method6079(TagKey<Fluid> var1) {
      return this.object2DoubleMap.getDouble(var1);
   }

   private boolean method6080(TagKey<Fluid> var1, double var2) {
      if (this.method6081()) {
         return false;
      } else {
         Box var4 = this.box.contract(0.001);
         int var5 = MathHelper.floor(var4.minX);
         int var6 = MathHelper.ceil(var4.maxX);
         int var7 = MathHelper.floor(var4.minY);
         int var8 = MathHelper.ceil(var4.maxY);
         int var9 = MathHelper.floor(var4.minZ);
         int var10 = MathHelper.ceil(var4.maxZ);
         double var11 = 0.0;
         boolean var13 = true;
         boolean var14 = false;
         Vec3d var15 = Vec3d.ZERO;
         int var16 = 0;
         Mutable var17 = new Mutable();

         for (int var18 = var5; var18 < var6; var18++) {
            for (int var19 = var7; var19 < var8; var19++) {
               for (int var20 = var9; var20 < var10; var20++) {
                  var17.set(var18, var19, var20);
                  FluidState var21 = this.playerEntity.getEntityWorld().getFluidState(var17);
                  if (var21.isIn(var1)) {
                     double var22 = var19 + var21.getHeight(this.playerEntity.getEntityWorld(), var17);
                     if (var22 >= var4.minY) {
                        var14 = true;
                        var11 = Math.max(var22 - var4.minY, var11);
                        if (var13) {
                           Vec3d var24 = var21.getVelocity(this.playerEntity.getEntityWorld(), var17);
                           if (var11 < 0.4) {
                              var24 = var24.multiply(var11);
                           }

                           var15 = var15.add(var24);
                           var16++;
                        }
                     }
                  }
               }
            }
         }

         if (var15.length() > 0.0) {
            if (var16 > 0) {
               var15 = var15.multiply(1.0 / var16);
            }

            var15 = var15.multiply(var2);
            if (Math.abs(this.vec3d2.x) < 0.003 && Math.abs(this.vec3d2.z) < 0.003 && var15.length() < 0.0045) {
               var15 = var15.normalize().multiply(0.0045);
            }

            this.vec3d2 = this.vec3d2.add(var15);
         }

         this.object2DoubleMap.put(var1, var11);
         return var14;
      }
   }

   private boolean method6081() {
      Box var1 = this.box.expand(1.0);
      int var2 = MathHelper.floor(var1.minX);
      int var3 = MathHelper.ceil(var1.maxX);
      int var4 = MathHelper.floor(var1.minZ);
      int var5 = MathHelper.ceil(var1.maxZ);
      return !this.playerEntity.getEntityWorld().isRegionLoaded(var2, var4, var3, var5);
   }

   private Vec3d method6082() {
      return this.method6083(this.floatValue2, this.floatValue);
   }

   private Vec3d method6083(float var1, float var2) {
      float var3 = (float)(var1 * Math.PI / 180.0);
      float var4 = (float)(-var2 * Math.PI / 180.0);
      float var5 = MathHelper.cos(var4);
      float var6 = MathHelper.sin(var4);
      float var7 = MathHelper.cos(var3);
      float var8 = MathHelper.sin(var3);
      return new Vec3d(var6 * var7, -var8, var5 * var7);
   }

   public boolean method6084(RegistryEntry<StatusEffect> var1) {
      StatusEffectInstance var2 = this.playerEntity.getStatusEffect(var1);
      return var2 != null && var2.getDuration() >= this.intValue2;
   }

   private StatusEffectInstance method6085(RegistryEntry<StatusEffect> var1) {
      StatusEffectInstance var2 = this.playerEntity.getStatusEffect(var1);
      return var2 != null && var2.getDuration() >= this.intValue2 ? var2 : null;
   }

   public double method6086(RegistryEntry<EntityAttribute> var1) {
      return this.playerEntity.getAttributes().getValue(var1);
   }

   public PlayerSimulation method6087() {
      return new PlayerSimulation(
         this.playerEntity,
         this.entry.method6091(),
         this.vec3d,
         this.vec3d2,
         this.box,
         this.floatValue,
         this.floatValue2,
         this.flag,
         this.floatValue3,
         this.intValue,
         this.flag2,
         this.flag3,
         this.flag4,
         this.flag5,
         this.flag6,
         this.flag7,
         this.flag8,
         this.flag9,
         this.entityPose,
         this.inSneakingPose,
         this.flag10,
         new Object2DoubleArrayMap(this.object2DoubleMap),
         new HashSet<>(this.hashSet)
      );
   }

   public BlockPos method6088() {
      return new BlockPos(MathHelper.floor(this.vec3d.x), MathHelper.floor(this.vec3d.y), MathHelper.floor(this.vec3d.z));
   }

   public BlockPos method6089(Vec3d var1) {
      return new BlockPos(MathHelper.floor(var1.x), MathHelper.floor(var1.y), MathHelper.floor(var1.z));
   }

   public BlockState method6090(BlockPos var1) {
      return this.playerEntity.getEntityWorld().getBlockState(var1);
   }

   public static class Entry extends Input {
      public boolean flag = false;
      public float floatValue;
      public float floatValue2;
      public PlayerInput playerInput;
      public static final double CONST_doubleValue = 0.121;

      public Entry(PlayerInput var1) {
         this.playerInput = var1;
      }

      private PlayerSimulation.Entry method6091() {
         PlayerSimulation.Entry var1 = new PlayerSimulation.Entry(this.playerInput);
         var1.flag = this.flag;
         var1.floatValue = this.floatValue;
         var1.floatValue2 = this.floatValue2;
         return var1;
      }

      public void update() {
         if (this.playerInput.forward() != this.playerInput.backward()) {
            this.floatValue = this.playerInput.forward() ? 1.0F : -1.0F;
         } else {
            this.floatValue = 0.0F;
         }

         if (this.playerInput.left() == this.playerInput.right()) {
            this.floatValue2 = 0.0F;
         } else {
            this.floatValue2 = this.playerInput.left() ? 1.0F : -1.0F;
         }
      }

      @Override
      public String toString() {
         return "SimulatedPlayerInput(forwards={"
            + this.playerInput.forward()
            + "}, backwards={"
            + this.playerInput.backward()
            + "}, left={"
            + this.playerInput.left()
            + "}, right={"
            + this.playerInput.right()
            + "}, jumping={"
            + this.playerInput.jump()
            + "}, sprinting="
            + this.playerInput.sprint()
            + ", slowDown="
            + this.playerInput.sneak()
            + ")";
      }

      public static PlayerSimulation.Entry method6092(PlayerInput var0) {
         return new PlayerSimulation.Entry(var0);
      }

      public static PlayerSimulation.Entry method6093(PlayerEntity var0) {
         Vec3d var1 = var0.getEntityPos().subtract(new Vec3d(var0.lastX, var0.lastY, var0.lastZ));
         double var2 = var1.horizontalLengthSquared();
         PlayerInput var4 = new PlayerInput(false, false, false, false, !var0.isOnGround(), var0.isSneaking(), var2 >= 0.014641);
         if (var2 > 0.0025000000000000005) {
            double var5 = MovementInputUtils.method7424(var1, var0.getYaw());
            double var7 = MathHelper.wrapDegrees(var5);
            var4 = MovementInputUtils.method7428(var4, var7);
         }

         return new PlayerSimulation.Entry(var4);
      }
   }
}

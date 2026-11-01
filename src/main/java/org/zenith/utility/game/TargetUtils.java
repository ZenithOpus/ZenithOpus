package org.zenith.utility.game;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.jetbrains.annotations.Nullable;
import org.zenith.Rotation;
import org.zenith.ZenithAccess;
import org.zenith.client.modules.impl.combat.AuraModule;

public final class TargetUtils implements ZenithAccess {
   public static BlockHitResult method3204(double var0, Rotation var2, boolean var3) {
      return method3205(Objects.requireNonNull(mc.player).getCameraPosVec(1.0F), var0, var2, var3);
   }

   public static BlockHitResult method3205(Vec3d var0, double var1, Rotation var3, boolean var4) {
      Entity var5 = mc.getCameraEntity();
      if (var5 == null) {
         return null;
      } else {
         Vec3d var6 = var3.method3135();
         Vec3d var7 = var0.add(var6.x * var1, var6.y * var1, var6.z * var1);
         ClientWorld var8 = mc.world;
         if (var8 == null) {
            return null;
         } else {
            FluidHandling var9 = var4 ? FluidHandling.ANY : FluidHandling.NONE;
            RaycastContext var10 = new RaycastContext(var0, var7, ShapeType.OUTLINE, var9, var5);
            return var8.raycast(var10);
         }
      }
   }

   public static HitResult method3206(double var0, Rotation var2, float var3, boolean var4) {
      Vec3d var5 = mc.getCameraEntity().getCameraPosVec(var3);
      Vec3d var6 = var2.method3135();
      Vec3d var7 = var5.add(var6.x * var0, var6.y * var0, var6.z * var0);
      return mc.world.raycast(new RaycastContext(var5, var7, ShapeType.OUTLINE, var4 ? FluidHandling.ANY : FluidHandling.NONE, mc.getCameraEntity()));
   }

   public static BlockHitResult method3207(Vec3d var0, Vec3d var1, ShapeType var2) {
      return method3208(var0, var1, var2, mc.player);
   }

   public static BlockHitResult method3208(Vec3d var0, Vec3d var1, ShapeType var2, Entity var3) {
      return mc.world.raycast(new RaycastContext(var0, var1, var2, FluidHandling.NONE, var3));
   }

   public static boolean method3209(double var0, Rotation var2, LivingEntity var3) {
      return method3210(var0, var2, var1 -> var1 == var3) != null;
   }

   public static EntityHitResult method3210(double var0, Rotation var2, Predicate<Entity> var3) {
      Entity var4 = mc.getCameraEntity();
      if (var4 == null) {
         return null;
      } else {
         Vec3d var5 = var4.getCameraPosVec(1.0F);
         Vec3d var6 = var2.method3135();
         Vec3d var7 = var5.add(var6.x * var0, var6.y * var0, var6.z * var0);
         Box var8 = var4.getBoundingBox().stretch(var6.multiply(var0)).expand(1.0, 1.0, 1.0);
         return ProjectileUtil.raycast(var4, var5, var7, var8, var1 -> !var1.isSpectator() && var3.test(var1), var0 * var0);
      }
   }

   public static EntityHitResult method3211(Rotation var0, Predicate<Entity> var1) {
      Entity var2 = mc.getCameraEntity();
      if (var2 != null && mc.world != null) {
         Vec3d var3 = var2.getCameraPosVec(1.0F);
         Vec3d var4 = var0.method3135();
         Entity var5 = null;
         Vec3d var6 = null;
         double var7 = Double.MAX_VALUE;

         for (Entity var10 : mc.world.getEntities()) {
            if (var10 != var2 && !var10.isSpectator() && var1.test(var10)) {
               Box var11 = var10.getBoundingBox().expand(var10.getTargetingMargin());
               Vec3d var12;
               if (var11.contains(var3)) {
                  var12 = var3;
               } else {
                  double var13 = var3.distanceTo(var11.getCenter()) + var11.getLengthX() + var11.getLengthY() + var11.getLengthZ();
                  var var15 = var11.raycast(var3, var3.add(var4.multiply(var13)));
                  if (var15.isEmpty()) {
                     continue;
                  }

                  var12 = (Vec3d)var15.get();
               }

               double var16 = var3.squaredDistanceTo(var12);
               if (var16 < var7) {
                  var5 = var10;
                  var6 = var12;
                  var7 = var16;
               }
            }
         }

         return var5 == null ? null : new EntityHitResult(var5, var6);
      } else {
         return null;
      }
   }

   public static boolean method3212(Rotation var0, Vec3d var1, Box var2, double var3, boolean var5) {
      double var6 = Math.max(mc.player.getBlockInteractionRange(), var3);
      double var8 = MathHelper.square(var6);
      BlockHitResult var10 = method3216(var1, var0, var6, AuraModule.INSTANCE::method6362);
      double var11 = var10.getPos().squaredDistanceTo(var1);
      if (var5 && var10.getType() != Type.MISS) {
         var8 = var11;
         var6 = Math.sqrt(var11);
      }

      Vec3d var13 = var0.method3135();
      Vec3d var14 = var1.add(var13.x * var6, var13.y * var6, var13.z * var6);
      Vec3d var15 = method3214(var1, var14, var2, var8);
      return var15 != null && (var15.squaredDistanceTo(var1) < var11 || !var5) && var15.isInRange(var1, var3);
   }

   public static HitResult method3213(Vec3d var0, Rotation var1, double var2) {
      Vec3d var5 = var1.method3135();
      Vec3d var6 = var0.add(var5.x * var2, var5.y * var2, var5.z * var2);
      return mc.world.raycast(new RaycastContext(var0, var6, ShapeType.OUTLINE, FluidHandling.NONE, mc.player));
   }

   @Nullable
   public static Vec3d method3214(Vec3d var0, Vec3d var1, Box var2, double var3) {
      Vec3d var5 = null;
      var var6 = var2.raycast(var0, var1);
      if (var2.contains(var0)) {
         if (var3 >= 0.0) {
            var5 = var6.orElse(var0);
         }
      } else if (var6.isPresent()) {
         Vec3d var7 = (Vec3d)var6.get();
         double var8 = var0.squaredDistanceTo(var7);
         if (var8 < var3 || var3 == 0.0) {
            var5 = var7;
         }
      }

      return var5;
   }

   private static HitResult ensureTargetInRange(HitResult var0, Vec3d var1, double var2) {
      Vec3d var4 = var0.getPos();
      if (!var4.isInRange(var1, var2)) {
         Vec3d var5 = var0.getPos();
         Direction var6 = Direction.getFacing(var5.x - var1.x, var5.y - var1.y, var5.z - var1.z);
         return BlockHitResult.createMissed(var5, var6, BlockPos.ofFloored(var5));
      } else {
         return var0;
      }
   }

   public static boolean method3215(Vec3d var0, double var1, Box var3) {
      Vec3d var4 = Objects.requireNonNull(mc.player).getEyePos();
      return var3.contains(var4) || var3.raycast(var4, var4.add(var0.multiply(var1))).isPresent();
   }

   public static BlockHitResult method3216(Vec3d var0, Rotation var1, double var2, Predicate<BlockHitResult> var4) {
      Vec3d var5 = var1.method3135();
      Vec3d var6 = var0.add(var5.x * var2, var5.y * var2, var5.z * var2);
      return method3217(new RaycastContext(var0, var6, ShapeType.OUTLINE, FluidHandling.NONE, mc.player), var4);
   }

   public static BlockHitResult method3217(RaycastContext var0, Predicate<BlockHitResult> var1) {
      return BlockView.raycast(var0.getStart(), var0.getEnd(), var0, (var1x, var2) -> {
         BlockState var3 = mc.world.getBlockState(var2);
         FluidState var4 = mc.world.getFluidState(var2);
         Vec3d var5 = var1x.getStart();
         Vec3d var6 = var1x.getEnd();
         VoxelShape var7 = var1x.getBlockShape(var3, mc.world, var2);
         BlockHitResult var8 = mc.world.raycastBlock(var5, var6, var2, var7, var3);
         VoxelShape var9 = var1x.getFluidShape(var4, mc.world, var2);
         BlockHitResult var10 = var9.raycast(var5, var6, var2);
         double var11 = var8 == null ? Double.MAX_VALUE : var1x.getStart().squaredDistanceTo(var8.getPos());
         double var13 = var10 == null ? Double.MAX_VALUE : var1x.getStart().squaredDistanceTo(var10.getPos());
         if (!var1.test(var11 <= var13 ? var8 : var10)) {
            return null;
         } else {
            return var11 <= var13 ? var8 : var10;
         }
      }, var0x -> {
         Vec3d var1x = var0x.getStart().subtract(var0x.getEnd());
         return BlockHitResult.createMissed(var0x.getEnd(), Direction.getFacing(var1x.x, var1x.y, var1x.z), BlockPos.ofFloored(var0x.getEnd()));
      });
   }

   public static BlockHitResult method3218(Vec3d var0, Vec3d var1, BlockPos var2, VoxelShape var3, BlockState var4) {
      BlockHitResult var5 = var3.raycast(var0, var1, var2);
      if (var5 != null) {
         BlockHitResult var6 = var4.getRaycastShape(mc.world, var2).raycast(var0, var1, var2);
         if (var6 != null && var6.getPos().subtract(var0).lengthSquared() < var5.getPos().subtract(var0).lengthSquared()) {
            return var5.withSide(var6.getSide());
         }
      }

      return var5;
   }

   @Generated
   private TargetUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

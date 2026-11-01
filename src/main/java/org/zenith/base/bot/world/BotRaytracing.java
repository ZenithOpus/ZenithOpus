package org.zenith.base.bot.world;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.zenith.Rotation;

public final class BotRaytracing {
   private BotRaytracing() {
   }

   public static BlockHitResult rayTracePos(BotWorld var0, BotPlayer var1, Vec3d var2, Rotation var3, double var4, Predicate<BlockHitResult> var6) {
      Vec3d var7 = var3.method3135();
      Vec3d var8 = var2.add(var7.x * var4, var7.y * var4, var7.z * var4);
      RaycastContext var9 = new RaycastContext(var2, var8, ShapeType.OUTLINE, FluidHandling.NONE, var1);
      return BlockView.raycast(var9.getStart(), var9.getEnd(), var9, (var2x, var3x) -> {
         BlockState var4x = var0.getBlockState(var3x);
         FluidState var5 = var0.getFluidState(var3x);
         Vec3d var6x = var2x.getStart();
         Vec3d var7x = var2x.getEnd();
         VoxelShape var8x = var2x.getBlockShape(var4x, var0, var3x);
         BlockHitResult var9x = var0.raycastBlock(var6x, var7x, var3x, var8x, var4x);
         VoxelShape var10 = var2x.getFluidShape(var5, var0, var3x);
         BlockHitResult var11 = var10.raycast(var6x, var7x, var3x);
         double var12 = var9x == null ? Double.MAX_VALUE : var6x.squaredDistanceTo(var9x.getPos());
         double var14 = var11 == null ? Double.MAX_VALUE : var6x.squaredDistanceTo(var11.getPos());
         BlockHitResult var16 = var12 <= var14 ? var9x : var11;
         return !var6.test(var16) ? null : var16;
      }, var0x -> {
         Vec3d var1x = var0x.getStart().subtract(var0x.getEnd());
         return BlockHitResult.createMissed(var0x.getEnd(), Direction.getFacing(var1x.x, var1x.y, var1x.z), BlockPos.ofFloored(var0x.getEnd()));
      });
   }
}

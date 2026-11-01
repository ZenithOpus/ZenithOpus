package org.zenith.utility.render;

import lombok.Generated;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;
import org.zenith.MinecraftAccess;
import org.zenith.Rotation;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.utility.misc.FrustumHolder;

public final class ProjectionUtils implements MinecraftAccess {
   private static final int[] CONST_intValues = new int[]{0, 0, 1, 1};
   private static boolean flag = false;
   private static final Vector3f vector3f = new Vector3f();
   private static final Vector4f vector4f = new Vector4f();

   public static void method299() {
      Window var0 = mc.getWindow();
      CONST_intValues[0] = 0;
      CONST_intValues[1] = 0;
      CONST_intValues[2] = Math.max(1, var0.getFramebufferWidth());
      CONST_intValues[3] = Math.max(1, var0.getFramebufferHeight());
      flag = true;
   }

   public static void method300() {
      flag = false;
   }

   @NotNull
   public static Vec3d method301(Vec3d var0) {
      if (!flag) {
         method299();
      }

      Vec3d var1 = mc.getEntityRenderDispatcher().camera.getCameraPos();
      float var2 = (float)(var0.x - var1.x);
      float var3 = (float)(var0.y - var1.y);
      float var4 = (float)(var0.z - var1.z);
      boolean var5 = mc.isOnThread();
      Vector3f var6 = var5 ? vector3f : new Vector3f();
      Vector4f var7 = var5 ? vector4f : new Vector4f();
      Vector4f var8 = var7.set(var2, var3, var4, 1.0F).mul(GlowUtil.getMatrix4f3());
      GlowUtil.getMatrix4f().project(var8.x(), var8.y(), var8.z(), CONST_intValues, var6);
      double var9 = mc.getWindow().getScaleFactor();
      return new Vec3d(var6.x / var9, (mc.getWindow().getFramebufferHeight() - var6.y) / var9, var6.z);
   }

   public static boolean method302(Vec3d var0) {
      Camera var1 = mc.getEntityRenderDispatcher().camera;
      Rotation var2 = RotationUtils.method6913(var0);
      return Math.abs(MathHelper.wrapDegrees(var2.getFloatValue() - var1.getYaw())) < 90.0F
            && Math.abs(MathHelper.wrapDegrees(var2.getFloatValue2() - var1.getPitch())) < 60.0F
         || method303(new Box(BlockPos.ofFloored(var0)));
   }

   public static boolean method303(Box var0) {
      Frustum var1 = FrustumHolder.getFrustum();
      return var0 != null && var1 != null && var1.isVisible(var0);
   }

   public static boolean method304(Vector4d var0) {
      return var0 == null || var0.x < 0.0 && var0.z < 1.0 || var0.y < 0.0 && var0.w < 1.0;
   }

   public static double method305(Vector4d var0) {
      return var0.x + (var0.z - var0.x) / 2.0;
   }

   @NotNull
   public static Vec3d[] method306(Entity var0, Vec3d var1) {
      Box var2 = var0.getBoundingBox();
      Box var3 = new Box(
         var2.minX - var0.getX() + var1.x - 0.1F,
         var2.minY - var0.getY() + var1.y - 0.1F,
         var2.minZ - var0.getZ() + var1.z - 0.1F,
         var2.maxX - var0.getX() + var1.x + 0.1F,
         var2.maxY - var0.getY() + var1.y + 0.1F,
         var2.maxZ - var0.getZ() + var1.z + 0.1F
      );
      return new Vec3d[]{
         new Vec3d(var3.minX, var3.minY, var3.minZ),
         new Vec3d(var3.minX, var3.maxY, var3.minZ),
         new Vec3d(var3.maxX, var3.minY, var3.minZ),
         new Vec3d(var3.maxX, var3.maxY, var3.minZ),
         new Vec3d(var3.minX, var3.minY, var3.maxZ),
         new Vec3d(var3.minX, var3.maxY, var3.maxZ),
         new Vec3d(var3.maxX, var3.minY, var3.maxZ),
         new Vec3d(var3.maxX, var3.maxY, var3.maxZ)
      };
   }

   public static Vector4d method307(Entity var0) {
      Vector4d var1 = null;

      for (Vec3d var5 : method306(var0, MathUtils.method2656(var0))) {
         var5 = method301(var5);
         if (var5.z > 0.0 && var5.z < 1.0) {
            if (var1 == null) {
               var1 = new Vector4d(var5.x, var5.y, var5.z, 0.0);
            }

            var1.x = Math.min(var5.x, var1.x);
            var1.y = Math.min(var5.y, var1.y);
            var1.z = Math.max(var5.x, var1.z);
            var1.w = Math.max(var5.y, var1.w);
         }
      }

      return var1;
   }

   @Generated
   private ProjectionUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

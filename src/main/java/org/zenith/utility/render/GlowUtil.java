package org.zenith.utility.render;

import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.client.modules.impl.render.EntityESPModule;
import org.zenith.utility.misc.ColorUtils;

public final class GlowUtil implements MinecraftAccess {
   private static final List<GlowUtil.Data3> items = new ArrayList<>();
   private static final List<GlowUtil.Data2> items2 = new ArrayList<>();
   private static final GlowUtil.Entry2 entry2 = new GlowUtil.Entry2();
   private static final GlowUtil.Entry2 entry22 = new GlowUtil.Entry2();
   private static final GlowUtil.Entry entry = new GlowUtil.Entry();
   private static final GlowUtil.Entry entry3 = new GlowUtil.Entry();
   private static Tessellator tessellator = Tessellator.getInstance();
   private static Matrix4f matrix4f = new Matrix4f();
   private static Matrix4f matrix4f2 = new Matrix4f();
   private static Matrix4f matrix4f3 = new Matrix4f();
   public static final Identifier identifier = Zenith.method1501("textures/glow.png");
   private static float[] floatValues = new float[8];
   private static int[][] intValues = new int[8][];
   private static int[] intValues2 = new int[8];
   private static int intValue = 0;

   private static void method2223(GlowUtil.Entry2 var0) {
      intValue = 0;
      float[] var1 = var0.floatValues2;
      int var2 = 0;

      for (int var3 = var0.size; var2 < var3; var2++) {
         float var4 = var1[var2];
         int var5 = -1;

         for (int var6 = 0; var6 < intValue; var6++) {
            if (floatValues[var6] == var4) {
               var5 = var6;
               break;
            }
         }

         if (var5 < 0) {
            if (intValue == floatValues.length) {
               floatValues = Arrays.copyOf(floatValues, intValue * 2);
               intValues = Arrays.copyOf(intValues, intValue * 2);
               intValues2 = Arrays.copyOf(intValues2, intValue * 2);
            }

            var5 = intValue;
            floatValues[var5] = var4;
            intValues2[var5] = 0;
            if (intValues[var5] == null) {
               intValues[var5] = new int[64];
            }

            intValue++;
         }

         int[] var8 = intValues[var5];
         int var7 = intValues2[var5];
         if (var7 == var8.length) {
            var8 = Arrays.copyOf(var8, var7 * 2);
            intValues[var5] = var8;
         }

         var8[var7] = var2;
         intValues2[var5] = var7 + 1;
      }
   }

   private static void method2224(MatrixStack var0, GlowUtil.Entry2 var1) {
      MatrixStack.Entry var2 = var0.peek();
      float[] var3 = var1.floatValues;
      int[] var4 = var1.intValues;

      for (int var5 = 0; var5 < intValue; var5++) {
         RenderState.method1902(floatValues[var5]);
         int[] var6 = intValues[var5];
         BufferBuilder var7 = tessellator.begin(DrawMode.LINES, net.minecraft.client.render.VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH);
         int var8 = 0;

         for (int var9 = intValues2[var5]; var8 < var9; var8++) {
            int var10 = var6[var8];
            int var11 = var10 * 6;
            method2237(
               var2,
               var7,
               var3[var11],
               var3[var11 + 1],
               var3[var11 + 2],
               var3[var11 + 3],
               var3[var11 + 4],
               var3[var11 + 5],
               var4[var10 * 2],
               var4[var10 * 2 + 1]
            );
         }

         ImmediateRenderer.method8015(var7.end());
      }

      var1.size = 0;
   }

   private static void method2225(MatrixStack.Entry var0, GlowUtil.Entry var1) {
      BufferBuilder var2 = tessellator.begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      float[] var3 = var1.floatValues;
      int[] var4 = var1.intValues;
      int var5 = 0;

      for (int var6 = var1.size; var5 < var6; var5++) {
         int var7 = var5 * 12;
         int var8 = var5 * 4;
         var2.vertex(var0, var3[var7], var3[var7 + 1], var3[var7 + 2]).color(var4[var8]);
         var2.vertex(var0, var3[var7 + 3], var3[var7 + 4], var3[var7 + 5]).color(var4[var8 + 1]);
         var2.vertex(var0, var3[var7 + 6], var3[var7 + 7], var3[var7 + 8]).color(var4[var8 + 2]);
         var2.vertex(var0, var3[var7 + 9], var3[var7 + 10], var3[var7 + 11]).color(var4[var8 + 3]);
      }

      ImmediateRenderer.method8015(var2.end());
      var1.size = 0;
   }

   public static void method2226(MatrixStack var0) {
      MatrixStack.Entry var1 = var0.peek();
      if (!entry3.isEmpty()) {
         RenderState.method1871();
         RenderState.method1877();
         RenderState.method1879();
         RenderState.method1874(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_CONSTANT_ALPHA);
         RenderState.method1898("core/position_color");
         method2225(var1, entry3);
         RenderState.method1878();
         RenderState.method1876();
         RenderState.method1872();
         RenderState.method1873();
         entry3.clear();
      }

      if (!entry22.isEmpty()) {
         method2223(entry22);
         RenderState.method1871();
         RenderState.method1877();
         RenderState.method1879();
         RenderState.method1874(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_CONSTANT_ALPHA);
         RenderState.method1898("core/rendertype_lines");
         method2224(var0, entry22);
         RenderState.method1878();
         RenderState.method1876();
         RenderState.method1872();
         RenderState.method1873();
         entry22.clear();
      }

      if (!entry2.isEmpty()) {
         method2223(entry2);
         RenderState.method1871();
         RenderState.method1877();
         RenderState.method1878();
         RenderState.method1882(false);
         RenderState.method1874(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_CONSTANT_ALPHA);
         RenderState.method1898("core/rendertype_lines");
         method2224(var0, entry2);
         RenderState.method1882(true);
         RenderState.method1876();
         RenderState.method1872();
         RenderState.method1873();
         entry2.clear();
      }

      if (!entry.isEmpty()) {
         RenderState.method1871();
         RenderState.method1877();
         RenderState.method1878();
         RenderState.method1874(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_CONSTANT_ALPHA);
         RenderState.method1898("core/position_color");
         method2225(var1, entry);
         RenderState.method1876();
         RenderState.method1872();
         RenderState.method1873();
         entry.clear();
      }
   }

   public static void method2227(BlockPos var0, VoxelShape var1, int var2, float var3) {
      method2228(var0, var1, var2, var3, true, false);
   }

   public static void method2228(BlockPos var0, VoxelShape var1, int var2, float var3, boolean var4, boolean var5) {
      if (ProjectionUtils.method303(var1.getBoundingBox().offset(var0))) {
         items2.stream()
            .filter(var1x -> var1x.voxelShape.equals(var1))
            .findFirst()
            .ifPresentOrElse(
               var5x -> var5x.items.forEach(var5xx -> method2232(var5xx.offset(var0), var2, var3, true, var4, var5)),
               () -> items2.add(new GlowUtil.Data2(var1, var1.getBoundingBoxes()))
            );
      }
   }

   public static void method2229(BlockPos var0, VoxelShape var1, int var2, float var3, boolean var4, boolean var5) {
      Vec3d var6 = Vec3d.of(var0);
      if (ProjectionUtils.method303(var1.getBoundingBox().offset(var6))) {
         var var7 = var1.getBoundingBoxes();
         items.stream()
            .filter(var1x -> var1x.items2.equals(var7))
            .findFirst()
            .ifPresentOrElse(
               var5x -> {
                  var5x.items2.forEach(var5xx -> method2232(var5xx.offset(var6), var2, var3, false, var4, var5));
                  var5x.items.forEach(var4xx -> method2245(var4xx.vec3d.add(var6), var4xx.vec3d2.add(var6), var2, var3, var5));
               },
               () -> {
                  ArrayList var1x = new ArrayList();
                  var1.forEachEdge(
                     (var1xx, var3x, var5x, var7x, var9, var11) -> var1x.add(
                        new GlowUtil.Data(new Vec3d(var1xx, var3x, var5x), new Vec3d(var7x, var9, var11), 0, 0, 0.0F)
                     )
                  );
                  items.add(new GlowUtil.Data3(var1, var1x, var1.getBoundingBoxes()));
               }
            );
      }
   }

   public static void method2230(Box var0, int var1, float var2) {
      method2231(var0, var1, var1, var2);
   }

   public static void method2231(Box var0, int var1, int var2, float var3) {
      method2233(var0, var1, var2, var3, true, true, false);
   }

   public static void method2232(Box var0, int var1, float var2, boolean var3, boolean var4, boolean var5) {
      method2233(var0, var1, var1, var2, var3, var4, var5);
   }

   public static void method2233(Box var0, int var1, int var2, float var3, boolean var4, boolean var5, boolean var6) {
      var0 = var0.expand(0.001);
      if (ProjectionUtils.method303(var0)) {
         double var7 = var0.minX;
         double var9 = var0.minY;
         double var11 = var0.minZ;
         double var13 = var0.maxX;
         double var15 = var0.maxY;
         double var17 = var0.maxZ;
         boolean var19 = EntityESPModule.INSTANCE.method567();
         int var20 = var19 ? ColorUtils.method6439(var1, 0.9F) : ColorUtils.method6439(var1, 0.1F);
         int var21 = var19 ? ColorUtils.method6439(var2, 0.1F) : var20;
         int var22 = var19 ? ColorUtils.method6439(var1, 0.5F) : var1;
         int var23 = var19 ? ColorUtils.method6439(var2, 1.0F) : var1;
         Vec3d var24 = mc.getEntityRenderDispatcher().camera.getCameraPos();
         float var25 = (float)(var7 - var24.x);
         float var26 = (float)(var9 - var24.y);
         float var27 = (float)(var11 - var24.z);
         float var28 = (float)(var13 - var24.x);
         float var29 = (float)(var15 - var24.y);
         float var30 = (float)(var17 - var24.z);
         if (var5) {
            GlowUtil.Entry var31 = var6 ? entry : entry3;
            var31.method2253(var25, var26, var27, var28, var26, var27, var28, var26, var30, var25, var26, var30, var20, var20, var20, var20);
            var31.method2253(var25, var26, var27, var25, var29, var27, var28, var29, var27, var28, var26, var27, var20, var21, var21, var20);
            var31.method2253(var28, var26, var27, var28, var29, var27, var28, var29, var30, var28, var26, var30, var20, var21, var21, var20);
            var31.method2253(var25, var26, var30, var28, var26, var30, var28, var29, var30, var25, var29, var30, var20, var20, var21, var21);
            var31.method2253(var25, var26, var27, var25, var26, var30, var25, var29, var30, var25, var29, var27, var20, var20, var21, var21);
            var31.method2253(var25, var29, var27, var25, var29, var30, var28, var29, var30, var28, var29, var27, var21, var21, var21, var21);
         }

         if (var4) {
            GlowUtil.Entry2 var33 = var6 ? entry2 : entry22;
            var33.method2254(var25, var26, var27, var28, var26, var27, var22, var22, var3);
            var33.method2254(var28, var26, var27, var28, var26, var30, var22, var22, var3);
            var33.method2254(var28, var26, var30, var25, var26, var30, var22, var22, var3);
            var33.method2254(var25, var26, var30, var25, var26, var27, var22, var22, var3);
            var33.method2254(var25, var26, var30, var25, var29, var30, var22, var23, var3);
            var33.method2254(var25, var26, var27, var25, var29, var27, var22, var23, var3);
            var33.method2254(var28, var26, var30, var28, var29, var30, var22, var23, var3);
            var33.method2254(var28, var26, var27, var28, var29, var27, var22, var23, var3);
            var33.method2254(var25, var29, var27, var28, var29, var27, var23, var23, var3);
            var33.method2254(var28, var29, var27, var28, var29, var30, var23, var23, var3);
            var33.method2254(var28, var29, var30, var25, var29, var30, var23, var23, var3);
            var33.method2254(var25, var29, var30, var25, var29, var27, var23, var23, var3);
         }
      }
   }

   public static void method2234(@NotNull MatrixStack var0, @NotNull VertexConsumer var1, Vec3d var2, Vec3d var3, int var4) {
      method2236(var0, var1, var2.toVector3f(), var3.toVector3f(), var4, var4);
   }

   public static void method2235(@NotNull MatrixStack var0, @NotNull VertexConsumer var1, Vec3d var2, Vec3d var3, int var4, int var5) {
      method2236(var0, var1, var2.toVector3f(), var3.toVector3f(), var4, var5);
   }

   public static void method2236(@NotNull MatrixStack var0, @NotNull VertexConsumer var1, Vector3f var2, Vector3f var3, int var4, int var5) {
      method2237(var0.peek(), var1, var2.x, var2.y, var2.z, var3.x, var3.y, var3.z, var4, var5);
   }

   private static void method2237(
      MatrixStack.Entry var0, VertexConsumer var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8, int var9
   ) {
      float var10 = var5 - var2;
      float var11 = var6 - var3;
      float var12 = var7 - var4;
      float var13 = MathHelper.sqrt(var10 * var10 + var11 * var11 + var12 * var12);
      float var14 = var10 / var13;
      float var15 = var11 / var13;
      float var16 = var12 / var13;
      float var17 = RenderState.getFloatValue4();
      var1.vertex(var0, var2, var3, var4).color(var8).normal(var0, var14, var15, var16).lineWidth(var17);
      var1.vertex(var0, var5, var6, var7).color(var9).normal(var0, var14, var15, var16).lineWidth(var17);
   }

   public static void method2238(@NotNull MatrixStack.Entry var0, @NotNull VertexConsumer var1, Vec3d var2, Vec3d var3, Vec3d var4, Vec3d var5, int var6) {
      method2241(var0, var1, var2.toVector3f(), var3.toVector3f(), var4.toVector3f(), var5.toVector3f(), var6, var6, var6, var6);
   }

   public static void method2239(
      @NotNull MatrixStack.Entry var0, @NotNull VertexConsumer var1, Vector3f var2, Vector3f var3, Vector3f var4, Vector3f var5, int var6
   ) {
      method2241(var0, var1, var2, var3, var4, var5, var6, var6, var6, var6);
   }

   public static void method2240(
      @NotNull MatrixStack.Entry var0, @NotNull VertexConsumer var1, Vec3d var2, Vec3d var3, Vec3d var4, Vec3d var5, int var6, int var7, int var8, int var9
   ) {
      method2241(var0, var1, var2.toVector3f(), var3.toVector3f(), var4.toVector3f(), var5.toVector3f(), var6, var7, var8, var9);
   }

   public static void method2241(
      @NotNull MatrixStack.Entry var0,
      @NotNull VertexConsumer var1,
      Vector3f var2,
      Vector3f var3,
      Vector3f var4,
      Vector3f var5,
      int var6,
      int var7,
      int var8,
      int var9
   ) {
      var1.vertex(var0, var2).color(var6);
      var1.vertex(var0, var3).color(var7);
      var1.vertex(var0, var4).color(var8);
      var1.vertex(var0, var5).color(var9);
   }

   @NotNull
   public static Vector3f method2242(float var0, float var1, float var2, float var3, float var4, float var5) {
      float var6 = var3 - var0;
      float var7 = var4 - var1;
      float var8 = var5 - var2;
      float var9 = MathHelper.sqrt(var6 * var6 + var7 * var7 + var8 * var8);
      return new Vector3f(var6 / var9, var7 / var9, var8 / var9);
   }

   public static void method2243(double var0, double var2, double var4, double var6, double var8, double var10, int var12, float var13, boolean var14) {
      method2244(var0, var2, var4, var6, var8, var10, var12, var12, var13, var14);
   }

   public static void method2244(
      double var0, double var2, double var4, double var6, double var8, double var10, int var12, int var13, float var14, boolean var15
   ) {
      Vec3d var16 = mc.getEntityRenderDispatcher().camera.getCameraPos();
      (var15 ? entry2 : entry22)
         .method2254(
            (float)(var0 - var16.x),
            (float)(var2 - var16.y),
            (float)(var4 - var16.z),
            (float)(var6 - var16.x),
            (float)(var8 - var16.y),
            (float)(var10 - var16.z),
            var12,
            var13,
            var14
         );
   }

   public static void method2245(Vec3d var0, Vec3d var1, int var2, float var3, boolean var4) {
      method2244(var0.x, var0.y, var0.z, var1.x, var1.y, var1.z, var2, var2, var3, var4);
   }

   public static void method2246(Vec3d var0, Vec3d var1, int var2, int var3, float var4, boolean var5) {
      method2244(var0.x, var0.y, var0.z, var1.x, var1.y, var1.z, var2, var3, var4, var5);
   }

   public static void method2247(Vec3d var0, Vec3d var1, Vec3d var2, Vec3d var3, int var4, boolean var5) {
      method2248(var0, var1, var2, var3, var4, var4, var4, var4, var5);
   }

   public static void method2248(Vec3d var0, Vec3d var1, Vec3d var2, Vec3d var3, int var4, int var5, int var6, int var7, boolean var8) {
      Vec3d var9 = mc.getEntityRenderDispatcher().camera.getCameraPos();
      double var10 = var9.x;
      double var12 = var9.y;
      double var14 = var9.z;
      (var8 ? entry : entry3)
         .method2253(
            (float)(var0.x - var10),
            (float)(var0.y - var12),
            (float)(var0.z - var14),
            (float)(var1.x - var10),
            (float)(var1.y - var12),
            (float)(var1.z - var14),
            (float)(var2.x - var10),
            (float)(var2.y - var12),
            (float)(var2.z - var14),
            (float)(var3.x - var10),
            (float)(var3.y - var12),
            (float)(var3.z - var14),
            var4,
            var5,
            var6,
            var7
         );
   }

   public static void method2249(float var0, float var1, float var2, float var3, Runnable var4) {
      RenderState.method1883(
         MathHelper.clamp(var0, 0.0F, 1.0F), MathHelper.clamp(var1, 0.0F, 1.0F), MathHelper.clamp(var2, 0.0F, 1.0F), MathHelper.clamp(var3, 0.0F, 1.0F)
      );
      var4.run();
      RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static float getTickDelta() {
      return mc.getRenderTickCounter().getTickProgress(false);
   }

   @Generated
   private GlowUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   @Generated
   public static void method2250(Matrix4f var0) {
      matrix4f = var0;
   }

   @Generated
   public static void method2251(Matrix4f var0) {
      matrix4f2 = var0;
   }

   @Generated
   public static void method2252(Matrix4f var0) {
      matrix4f3 = var0;
   }

   @Generated
   public static Matrix4f getMatrix4f() {
      return matrix4f;
   }

   @Generated
   public static Matrix4f getMatrix4f2() {
      return matrix4f2;
   }

   @Generated
   public static Matrix4f getMatrix4f3() {
      return matrix4f3;
   }

   public static final class Data {
      private final Vec3d vec3d;
      private final Vec3d vec3d2;
      private final int intValue;
      private final int intValue2;
      private final float floatValue;

      public Vec3d vec3d() {
         return this.vec3d;
      }

      public Vec3d vec3d2() {
         return this.vec3d2;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public float floatValue() {
         return this.floatValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.vec3d2, other.vec3d2) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.floatValue, other.floatValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(vec3d, vec3d2, intValue, intValue2, floatValue);
      }

      @Override
      public String toString() {
         return "Data[" + "vec3d=" + this.vec3d + ", " + "vec3d2=" + this.vec3d2 + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "floatValue=" + this.floatValue + "]";
      }

      public Data(Vec3d var1, Vec3d var2, int var3, int var4, float var5) {
         this.vec3d = var1;
         this.vec3d2 = var2;
         this.intValue = var3;
         this.intValue2 = var4;
         this.floatValue = var5;
      }

      public Vec3d getVec3d() {
         return this.vec3d;
      }

      public Vec3d getVec3d2() {
         return this.vec3d2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public float width() {
         return this.floatValue;
      }
   }

   public static final class Data2 {
      private final VoxelShape voxelShape;
      private final List<Box> items;

      public VoxelShape voxelShape() {
         return this.voxelShape;
      }

      public List<Box> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.voxelShape, other.voxelShape) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(voxelShape, items);
      }

      @Override
      public String toString() {
         return "Data2[" + "voxelShape=" + this.voxelShape + ", " + "items=" + this.items + "]";
      }

      public Data2(VoxelShape var1, List<Box> var2) {
         this.voxelShape = var1;
         this.items = var2;
      }

      public VoxelShape getVoxelShape() {
         return this.voxelShape;
      }

      public List<Box> getItems() {
         return this.items;
      }
   }

   public static final class Data3 {
      private final VoxelShape voxelShape;
      private final List<GlowUtil.Data> items;
      private final List<Box> items2;

      public VoxelShape voxelShape() {
         return this.voxelShape;
      }

      public List<GlowUtil.Data> items() {
         return this.items;
      }

      public List<Box> items2() {
         return this.items2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.voxelShape, other.voxelShape) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.items2, other.items2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(voxelShape, items, items2);
      }

      @Override
      public String toString() {
         return "Data3[" + "voxelShape=" + this.voxelShape + ", " + "items=" + this.items + ", " + "items2=" + this.items2 + "]";
      }

      public Data3(VoxelShape var1, List<GlowUtil.Data> var2, List<Box> var3) {
         this.voxelShape = var1;
         this.items = var2;
         this.items2 = var3;
      }

      public VoxelShape getVoxelShape() {
         return this.voxelShape;
      }

      public List<GlowUtil.Data> lines() {
         return this.items;
      }

      public List<Box> getItems2() {
         return this.items2;
      }
   }

   private static final class Entry {
      private float[] floatValues = new float[3072];
      private int[] intValues = new int[1024];
      private int size;

      private void method2253(
         float var1,
         float var2,
         float var3,
         float var4,
         float var5,
         float var6,
         float var7,
         float var8,
         float var9,
         float var10,
         float var11,
         float var12,
         int var13,
         int var14,
         int var15,
         int var16
      ) {
         if (this.size * 4 == this.intValues.length) {
            int var17 = this.size * 2;
            this.floatValues = Arrays.copyOf(this.floatValues, var17 * 12);
            this.intValues = Arrays.copyOf(this.intValues, var17 * 4);
         }

         int var19 = this.size * 12;
         this.floatValues[var19] = var1;
         this.floatValues[var19 + 1] = var2;
         this.floatValues[var19 + 2] = var3;
         this.floatValues[var19 + 3] = var4;
         this.floatValues[var19 + 4] = var5;
         this.floatValues[var19 + 5] = var6;
         this.floatValues[var19 + 6] = var7;
         this.floatValues[var19 + 7] = var8;
         this.floatValues[var19 + 8] = var9;
         this.floatValues[var19 + 9] = var10;
         this.floatValues[var19 + 10] = var11;
         this.floatValues[var19 + 11] = var12;
         int var18 = this.size * 4;
         this.intValues[var18] = var13;
         this.intValues[var18 + 1] = var14;
         this.intValues[var18 + 2] = var15;
         this.intValues[var18 + 3] = var16;
         this.size++;
      }

      private boolean isEmpty() {
         return this.size == 0;
      }

      private void clear() {
         this.size = 0;
      }
   }

   private static final class Entry2 {
      private float[] floatValues = new float[1536];
      private int[] intValues = new int[512];
      private float[] floatValues2 = new float[256];
      private int size;

      private void method2254(float var1, float var2, float var3, float var4, float var5, float var6, int var7, int var8, float var9) {
         if (this.size == this.floatValues2.length) {
            int var10 = this.size * 2;
            this.floatValues = Arrays.copyOf(this.floatValues, var10 * 6);
            this.intValues = Arrays.copyOf(this.intValues, var10 * 2);
            this.floatValues2 = Arrays.copyOf(this.floatValues2, var10);
         }

         int var11 = this.size * 6;
         this.floatValues[var11] = var1;
         this.floatValues[var11 + 1] = var2;
         this.floatValues[var11 + 2] = var3;
         this.floatValues[var11 + 3] = var4;
         this.floatValues[var11 + 4] = var5;
         this.floatValues[var11 + 5] = var6;
         this.intValues[this.size * 2] = var7;
         this.intValues[this.size * 2 + 1] = var8;
         this.floatValues2[this.size] = var9;
         this.size++;
      }

      private boolean isEmpty() {
         return this.size == 0;
      }

      private void clear() {
         this.size = 0;
      }
   }
}

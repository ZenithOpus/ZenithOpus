package org.zenith.utility.render;

import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.zenith.RenderState;
import org.zenith.ZenithColor;
import org.zenith.utility.misc.FireflyParticle;
import org.zenith.utility.misc.Handler7;
import org.zenith.utility.misc.ParticleType;
import org.zenith.utility.misc.SpaceStarParticle;

public class ParticleRenderer {
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private static double doubleValue;
   private static double doubleValue2;
   private static double doubleValue3;
   private static final Quaternionf quaternionf = new Quaternionf();
   private static final Quaternionf quaternionf2 = new Quaternionf();
   private static final MatrixStack matrixStack = new MatrixStack();
   private static final Map<String, List<Handler7>> items = new HashMap<>();

   public static void method1476(Camera var0) {
      Vec3d var1 = var0.getCameraPos();
      doubleValue = var1.x;
      doubleValue2 = var1.y;
      doubleValue3 = var1.z;
      float var2 = var0.getPitch();
      float var3 = var0.getYaw();
      quaternionf.identity();
      quaternionf.rotateX((float)Math.toRadians(var2));
      quaternionf.rotateY((float)Math.toRadians(var3 + 180.0F));
      quaternionf2.identity();
      quaternionf2.rotateY((float)Math.toRadians(-var3));
      quaternionf2.rotateX((float)Math.toRadians(var2));
   }

   @SafeVarargs
   public static void method1477(MatrixStack var0, float var1, List<? extends Handler7>... var2) {
      items.clear();
      int var3 = 0;

      for (List var7 : var2) {
         if (var7 != null) {
            for (Object var9_o : (Iterable<?>)(Object)(var7)) {
               Handler7 var9 = (Handler7)var9_o;
               if (!var9.method1555()) {
                  items.computeIfAbsent(var9.method1559(), var0x -> new ArrayList<>()).add(var9);
                  var3++;
               }
            }
         }
      }

      if (var3 != 0) {
         method1484();
         Tessellator var13 = Tessellator.getInstance();

         for (Entry var15 : items.entrySet()) {
            Identifier var16 = ParticleType.method1705((String)var15.getKey());
            if (var16 != null) {
               RenderState.method1890(0, var16);
               List<Handler7> var17 = (List)var15.getValue();
               BufferBuilder var18 = var13.begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
               boolean var10 = false;

               for (Object var12_o : (Iterable<?>)(Object)(var17)) {
                  Handler7 var12 = (Handler7)var12_o;
                  if (var12.method1449(var1) >= 0.001F) {
                     method1481(var18, var12, var1);
                     var10 = true;
                  }
               }

               if (var10) {
                  ImmediateRenderer.method8015(var18.end());
               }

               var17.clear();
            }
         }

         items.clear();
         method1485();
      }
   }

   public static void method1478(MatrixStack var0, List<FireflyParticle> var1, float var2, float var3) {
      if (!var1.isEmpty()) {
         method1484();
         RenderState.method1890(0, ParticleType.Type.FIREFLY.getSupplier().get());
         Tessellator var4 = Tessellator.getInstance();
         BufferBuilder var5 = var4.begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
         float var6 = var3 + 2.5F;
         boolean var7 = false;

         for (FireflyParticle var9 : var1) {
            Vec3d var10 = var9.method1557().lerp(var9.method1556(), var2);
            Vec3d var11 = var9.getVec3d3();
            double var12 = var11.length();
            float var14 = 1.0F - (float)var9.getIntValue() / var9.getIntValue3();
            float var15 = Math.min(var14 / 0.15F, 1.0F);
            float var16 = var14 > 0.8F ? 1.0F - (var14 - 0.8F) / 0.2F : 1.0F;
            float var17 = var15 * var16;
            Vec3d var18 = var12 > 0.001 ? var11.normalize() : new Vec3d(0.0, 1.0, 0.0);
            float var19 = (float)Math.min(var12 / 0.2, 1.0);
            float var20 = Math.max(var19 * var17, 0.1F);
            byte var21 = 80;

            for (int var22 = 0; var22 < var21; var22++) {
               float var23 = (float)var22 / var21;
               if (!(var23 >= var20)) {
                  float var24 = var23 / var20;
                  float var25 = var9.getSize() * (1.0F - var24 * 0.5F) * 2.0F * var17;
                  float var26 = (float)Math.pow(1.0F - var24, 0.4) * var9.method1449(var2) * var17 * 0.8F;
                  if (!(var26 < 0.01F)) {
                     Vec3d var27 = var10.subtract(var18.multiply(var24 * var20 * var6));
                     float var28 = (float)Math.pow(1.0F - var24, 1.2) * var17;
                     float var29 = var25 * (1.8F + var28 * 0.8F);
                     float var30 = var26 * 0.5F * var28;
                     if (var30 > 0.01F) {
                        method1482(var5, var27, var29, var30, var9.method1558());
                        var7 = true;
                     }

                     method1482(var5, var27, var25, var26, var9.method1558());
                     var7 = true;
                  }
               }
            }
         }

         if (var7) {
            ImmediateRenderer.method8015(var5.end());
         }

         method1485();
      }
   }

   public static void method1479(MatrixStack var0, List<SpaceStarParticle> var1, float var2, float var3) {
      if (!var1.isEmpty()) {
         method1484();
         Tessellator var4 = Tessellator.getInstance();
         RenderState.method1890(0, ParticleType.Type.SPACE_GLOW.getSupplier().get());
         BufferBuilder var5 = var4.begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);

         for (SpaceStarParticle var7 : var1) {
            if (!var7.method1555()) {
               Vec3d var8 = var7.method1557().lerp(var7.method1556(), var2);
               float var9 = var7.method1449(var2);
               if (!(var9 < 0.005F)) {
                  method1483(var5, var8, var7.method3081(var2), var9 * 0.22F, var7.method1558(), 0.0F);
                  method1483(var5, var8, var7.method3083(var2), var9 * 0.48F, var7.method1558(), 0.0F);
                  Vec3d var10 = var7.getVec3d3();
                  double var11 = var10.length();
                  if (!(var11 <= 5.0E-4)) {
                     Vec3d var13 = var10.normalize();
                     float var14 = var7.method3084(var3);
                     byte var15 = 16;

                     for (int var16 = 1; var16 <= var15; var16++) {
                        float var17 = (float)var16 / var15;
                        float var18 = var9 * (float)Math.pow(1.0F - var17, 1.45F) * 0.42F;
                        if (!(var18 < 0.01F)) {
                           Vec3d var19 = var8.subtract(var13.multiply(var17 * var14));
                           float var20 = var7.method3083(var2) * (1.0F - var17 * 0.72F);
                           method1483(var5, var19, var20, var18, var7.method1558(), 0.0F);
                        }
                     }
                  }
               }
            }
         }

         ShapeRenderer.method7212(var5);
         RenderState.method1890(0, ParticleType.Type.SPACE_STAR.getSupplier().get());
         BufferBuilder var21 = var4.begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);

         for (SpaceStarParticle var23 : var1) {
            if (!var23.method1555()) {
               Vec3d var24 = var23.method1557().lerp(var23.method1556(), var2);
               float var25 = var23.method1449(var2);
               if (!(var25 < 0.005F)) {
                  method1483(var21, var24, var23.method3082(var2), var25, var23.method1558().method7303(0.35F), var23.method1560());
               }
            }
         }

         ShapeRenderer.method7212(var21);
         method1485();
      }
   }

   public static void method1480(MatrixStack var0, Vec3d var1, Vec3d var2, float var3, float var4, ZenithColor var5, float var6) {
      method1484();
      RenderState.method1890(0, ParticleType.Type.FIREFLY.getSupplier().get());
      Tessellator var7 = Tessellator.getInstance();
      BufferBuilder var8 = var7.begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
      double var9 = var2.length();
      Vec3d var11 = var9 > 0.001 ? var2.normalize() : new Vec3d(0.0, 0.0, -1.0);
      byte var12 = 60;
      boolean var13 = false;

      for (int var14 = 0; var14 < var12; var14++) {
         float var15 = (float)var14 / var12;
         float var16 = var4 * (1.0F - var15 * 0.7F) * 2.0F;
         float var17 = (float)Math.pow(1.0F - var15, 0.5) * 0.9F;
         if (!(var17 < 0.01F) && !(var16 < 0.01F)) {
            Vec3d var18 = var1.subtract(var11.multiply(var15 * var3));
            method1482(var8, var18, var16 * 1.8F, var17 * 0.4F, var5);
            method1482(var8, var18, var16, var17, var5);
            var13 = true;
         }
      }

      if (var13) {
         ImmediateRenderer.method8015(var8.end());
      }

      method1485();
   }

   private static void method1481(BufferBuilder var0, Handler7 var1, float var2) {
      Vec3d var3 = var1.method1557().lerp(var1.method1556(), var2);
      float var4 = (float)(var3.x - doubleValue);
      float var5 = (float)(var3.y - doubleValue2);
      float var6 = (float)(var3.z - doubleValue3);
      float var7 = var1.method1449(var2);
      if (!(var7 < 0.001F)) {
         float var8 = var1.getSize();
         ZenithColor var9 = var1.method1558();
         int var10 = (int)(var7 * var9.getIntValue4()) << 24 | var9.getIntValue() << 16 | var9.getIntValue2() << 8 | var9.getIntValue3();
         matrixStack.push();
         matrixStack.multiply(quaternionf);
         matrixStack.translate(var4, var5, var6);
         matrixStack.multiply(quaternionf2);
         float var11 = var1.method1560();
         if (var11 != 0.0F) {
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var11));
         }

         Matrix4f var12 = matrixStack.peek().getPositionMatrix();
         var0.vertex(var12, 0.0F, -var8, 0.0F).texture(0.0F, 1.0F).color(var10);
         var0.vertex(var12, -var8, -var8, 0.0F).texture(1.0F, 1.0F).color(var10);
         var0.vertex(var12, -var8, 0.0F, 0.0F).texture(1.0F, 0.0F).color(var10);
         var0.vertex(var12, 0.0F, 0.0F, 0.0F).texture(0.0F, 0.0F).color(var10);
         matrixStack.pop();
      }
   }

   private static void method1482(BufferBuilder var0, Vec3d var1, float var2, float var3, ZenithColor var4) {
      method1483(var0, var1, var2, var3, var4, 0.0F);
   }

   private static void method1483(BufferBuilder var0, Vec3d var1, float var2, float var3, ZenithColor var4, float var5) {
      if (!(var3 < 0.001F)) {
         float var6 = (float)(var1.x - doubleValue);
         float var7 = (float)(var1.y - doubleValue2);
         float var8 = (float)(var1.z - doubleValue3);
         int var9 = (int)(var3 * var4.getIntValue4()) << 24 | var4.getIntValue() << 16 | var4.getIntValue2() << 8 | var4.getIntValue3();
         float var10 = var2 / 2.0F;
         matrixStack.push();
         matrixStack.multiply(quaternionf);
         matrixStack.translate(var6, var7, var8);
         matrixStack.multiply(quaternionf2);
         if (var5 != 0.0F) {
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var5));
         }

         Matrix4f var11 = matrixStack.peek().getPositionMatrix();
         var0.vertex(var11, var10, -var10, 0.0F).texture(0.0F, 1.0F).color(var9);
         var0.vertex(var11, -var10, -var10, 0.0F).texture(1.0F, 1.0F).color(var9);
         var0.vertex(var11, -var10, var10, 0.0F).texture(1.0F, 0.0F).color(var9);
         var0.vertex(var11, var10, var10, 0.0F).texture(0.0F, 0.0F).color(var9);
         matrixStack.pop();
      }
   }

   private static void method1484() {
      RenderState.method1871();
      RenderState.method1874(SourceFactor.SRC_ALPHA, DestFactor.ONE);
      RenderState.method1878();
      RenderState.method1882(false);
      RenderState.method1898("core/position_tex_color");
   }

   private static void method1485() {
      RenderState.method1882(true);
      RenderState.method1872();
      RenderState.method1873();
   }
}

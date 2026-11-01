package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import lombok.Generated;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.ImageBlur;
import org.zenith.utility.misc.TextureId;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ShapeRenderer implements MinecraftAccess {
   public static HashMap<ShapeRenderer.Data, ShapeRenderer.Entry2> hashMap = new HashMap<>();
   public static HashMap<Integer, ShapeRenderer.Entry2> hashMap2 = new HashMap<>();
   public static final Stack<ShapeRenderer.Data3> stack = new Stack<>();
   private static final List<ShapeRenderer.Data2> items = new ArrayList<>();
   private static final int CONST_intValue = 5;
   private static final Set<ShapeRenderer.Data> items2 = ConcurrentHashMap.newKeySet();
   private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
   private static final HashMap<ShapeRenderer.Data, ShapeRenderer.Data> hashMap3 = new HashMap<>();

   public static void method7153() {
      MsdfRenderer.flushBatch();
      RenderState.method1901();
   }

   public static void method7154(
      BufferBuilder var0, Matrix4f var1, float var2, float var3, float var4, float var5, Color var6, Color var7, Color var8, Color var9
   ) {
      var0.vertex(var1, var2, var5, 0.0F).color(var6.getRGB());
      var0.vertex(var1, var4, var5, 0.0F).color(var7.getRGB());
      var0.vertex(var1, var4, var3, 0.0F).color(var8.getRGB());
      var0.vertex(var1, var2, var3, 0.0F).color(var9.getRGB());
   }

   public static boolean method7155(double var0, double var2, double var4, double var6, double var8, double var10) {
      return var0 >= var4 && var0 - var8 <= var4 && var2 >= var6 && var2 - var10 <= var6;
   }

   public static void method7156(MatrixStack var0, float var1, float var2, float var3, float var4, int var5, Color var6) {
   }

   public static void method7157(DrawContext var0) {
      MatrixStack var1 = MatrixUtils.method666(var0.getMatrices());
      Matrix4f var2 = var1.peek().getPositionMatrix();
      if (!items.isEmpty()) {
         MsdfRenderer.flushBatch();
         RenderState.method1871();
         RenderState.method1873();
         RenderState.method1898("core/position_color");
         BufferBuilder var3 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
         items.forEach(var2x -> method7161(var2, var3, var2x.floatValue, var2x.floatValue2, var2x.floatValue3, var2x.floatValue4, var2x.intValue));
         ImmediateRenderer.method8015(var3.end());
         RenderState.method1872();
         items.clear();
      }
   }

   public static void method7158(float var0, float var1, float var2, float var3, int var4) {
      items.add(new ShapeRenderer.Data2(var0, var1, var2, var3, ColorUtils.method6439(var4, RenderState.getAlpha())));
   }

   public static void method7159(
      MatrixStack var0,
      double var1,
      double var3,
      double var5,
      double var7,
      float var9,
      float var10,
      double var11,
      double var13,
      double var15,
      double var17,
      int var19,
      int var20,
      int var21,
      int var22
   ) {
      MsdfRenderer.flushBatch();
      RenderState.method1898("core/position_tex_color");
      BufferBuilder var23 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
      method7181(var23, var0, var1, var3, var5, var7, var9, var10, var11, var13, var15, var17, var19, var20, var21, var22);
      ImmediateRenderer.method8015(var23.end());
   }

   public static void method7160(Matrix4f var0, BufferBuilder var1, float var2, float var3, float var4, float var5) {
      var1.vertex(var0, var2, var3, 0.0F);
      var1.vertex(var0, var2, var3 + var5, 0.0F);
      var1.vertex(var0, var2 + var4, var3 + var5, 0.0F);
      var1.vertex(var0, var2 + var4, var3, 0.0F);
   }

   public static void method7161(Matrix4f var0, BufferBuilder var1, float var2, float var3, float var4, float var5, int var6) {
      var1.vertex(var0, var2, var3, 0.0F).color(var6);
      var1.vertex(var0, var2, var3 + var5, 0.0F).color(var6);
      var1.vertex(var0, var2 + var4, var3 + var5, 0.0F).color(var6);
      var1.vertex(var0, var2 + var4, var3, 0.0F).color(var6);
   }

   public static void method7162(Matrix4f var0, float var1, float var2, float var3, float var4, int var5) {
      MsdfRenderer.flushBatch();
      RenderState.method1898("core/position_tex_color");
      BufferBuilder var6 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
      var6.vertex(var0, var1, var2 + var4, 0.0F).texture(0.0F, 0.0F).color(var5);
      var6.vertex(var0, var1 + var3, var2 + var4, 0.0F).texture(0.0F, 1.0F).color(var5);
      var6.vertex(var0, var1 + var3, var2, 0.0F).texture(1.0F, 1.0F).color(var5);
      var6.vertex(var0, var1, var2, 0.0F).texture(1.0F, 0.0F).color(var5);
      ImmediateRenderer.method8015(var6.end());
   }

   public static void method7163(Matrix4f var0, BufferBuilder var1, float var2, float var3, float var4, float var5, int var6) {
      var1.vertex(var0, var2, var3 + var5, 0.0F).texture(0.0F, 0.0F).color(var6);
      var1.vertex(var0, var2 + var4, var3 + var5, 0.0F).texture(0.0F, 1.0F).color(var6);
      var1.vertex(var0, var2 + var4, var3, 0.0F).texture(1.0F, 1.0F).color(var6);
      var1.vertex(var0, var2, var3, 0.0F).texture(1.0F, 0.0F).color(var6);
   }

   private static ShapeRenderer.Data method7164(float var0, float var1, int var2, ProxyDotRadiusData var3) {
      int var4 = Math.max(1, Math.round(var0));
      int var5 = Math.max(1, Math.round(var1));
      int var6 = Math.max(1, var4 - var2 * 2);
      int var7 = Math.max(1, var5 - var2 * 2);
      ProxyDotRadiusData var8 = method7171(var6, var7, var3);
      return new ShapeRenderer.Data(
         var4,
         var5,
         var2,
         Math.round(var8.getFloatValue()),
         Math.round(var8.getFloatValue2()),
         Math.round(var8.getFloatValue3()),
         Math.round(var8.getFloatValue4())
      );
   }

   private static ShapeRenderer.Data method7165(ShapeRenderer.Data var0) {
      ShapeRenderer.Data var1 = null;
      int var2 = Integer.MAX_VALUE;

      for (ShapeRenderer.Data var4 : hashMap.keySet()) {
         int var5 = method7173(var4, var0);
         if (var5 < var2) {
            var2 = var5;
            var1 = var4;
         }
      }

      return var1;
   }

   public static void method7166(MatrixStack var0, float var1, float var2, float var3, float var4, int var5, ProxyDotRadiusData var6, GlowColor var7) {
      method7167(var0.peek().getPositionMatrix(), var1, var2, var3, var4, var5, var6, var7);
   }

   public static void method7167(Matrix4f var0, float var1, float var2, float var3, float var4, int var5, ProxyDotRadiusData var6, GlowColor var7) {
      var3 += var5 * 2;
      var4 += var5 * 2;
      var1 -= var5;
      var2 -= var5;
      ShapeRenderer.Data var8 = method7164(var3, var4, var5, var6);
      ShapeRenderer.Entry2 var9 = hashMap.get(var8);
      ShapeRenderer.Data var10 = var8;
      if (var9 == null) {
         var10 = hashMap3.get(var8);
         if (var10 == null || !hashMap.containsKey(var10)) {
            var10 = method7165(var8);
            if (var10 != null) {
               if (hashMap3.size() > 512) {
                  hashMap3.clear();
               }

               hashMap3.put(var8, var10);
            }
         }

         if (var10 != null) {
            var9 = hashMap.get(var10);
         }
      }

      if (var9 == null || method7173(var10, var8) >= 5 || var8.intValue7 != var10.intValue7) {
         method7168(var8);
      }

      if (var9 != null) {
         var9.reset();
         RenderShaders.method5281(var0, var9.textureId.getIdentifier(), var1, var2, var3, var4, var7);
      }
   }

   private static void method7168(ShapeRenderer.Data var0) {
      if (!hashMap.containsKey(var0) && items2.add(var0)) {
         executorService.execute(() -> {
            NativeImage var1 = null;

            try {
               BufferedImage var2 = method7170(var0);
               BufferedImage var3 = var0.getIntValue3() > 0 ? new ImageBlur(var0.getIntValue3()).method2287(var2, null) : var2;
               var1 = method7169(var3);
               NativeImage var4 = var1;
               var1 = null;
               mc.execute(() -> {
                  try {
                     if (!hashMap.containsKey(var0)) {
                        hashMap.put(var0, new ShapeRenderer.Entry2(var4));
                     } else {
                        var4.close();
                     }
                  } finally {
                     items2.remove(var0);
                  }
               });
            } catch (Exception var5) {
               if (var1 != null) {
                  var1.close();
               }

               items2.remove(var0);
               var5.printStackTrace();
            }
         });
      }
   }

   private static NativeImage method7169(BufferedImage var0) {
      int var1 = var0.getWidth();
      int var2 = var0.getHeight();
      int[] var3 = var0.getRGB(0, 0, var1, var2, null, 0, var1);
      NativeImage var4 = new NativeImage(var1, var2, false);

      for (int var5 = 0; var5 < var2; var5++) {
         int var6 = var5 * var1;

         for (int var7 = 0; var7 < var1; var7++) {
            var4.setColorArgb(var7, var5, var3[var6 + var7]);
         }
      }

      return var4;
   }

   private static BufferedImage method7170(ShapeRenderer.Data var0) {
      BufferedImage var1 = new BufferedImage(var0.width(), var0.height(), 2);
      int[] var2 = new int[var0.width() * var0.height()];
      int var3 = Math.max(1, var0.width() - var0.getIntValue3() * 2);
      int var4 = Math.max(1, var0.height() - var0.getIntValue3() * 2);
      float var5 = var3 * 0.5F;
      float var6 = var4 * 0.5F;
      float var7 = Math.max(0.0F, var5 - 1.0F);
      float var8 = Math.max(0.0F, var6 - 1.0F);

      for (int var9 = 0; var9 < var0.height(); var9++) {
         float var10 = var9 + 0.5F - var0.getIntValue3();

         for (int var11 = 0; var11 < var0.width(); var11++) {
            float var12 = var11 + 0.5F - var0.getIntValue3();
            float var13 = var5 - var12;
            float var14 = var6 - var10;
            float var15 = method7174(var13, var14, var7, var8, var0.getIntValue4(), var0.getIntValue7(), var0.getIntValue5(), var0.getIntValue6());
            float var16 = 1.0F - method7175(0.0F, 1.0F, var15);
            if (!(var16 <= 0.0F)) {
               int var17 = MathHelper.clamp(Math.round(var16 * 255.0F), 0, 255);
               var2[var9 * var0.width() + var11] = var17 << 24 | 16777215;
            }
         }
      }

      var1.setRGB(0, 0, var0.width(), var0.height(), var2, 0, var0.width());
      return var1;
   }

   private static ProxyDotRadiusData method7171(int var0, int var1, ProxyDotRadiusData var2) {
      float var3 = Math.max(0.0F, var0 * 0.5F - 1.0F);
      float var4 = Math.max(0.0F, var1 * 0.5F - 1.0F);
      return new ProxyDotRadiusData(
         method7172(var2.getFloatValue(), var3, var4),
         method7172(var2.getFloatValue2(), var3, var4),
         method7172(var2.getFloatValue3(), var3, var4),
         method7172(var2.getFloatValue4(), var3, var4)
      );
   }

   private static float method7172(float var0, float var1, float var2) {
      return Math.max(0.0F, Math.min(var0, Math.min(var1, var2)));
   }

   private static int method7173(ShapeRenderer.Data var0, ShapeRenderer.Data var1) {
      return var0 != null && var1 != null
         ? Math.abs(var0.width() - var1.width())
            + Math.abs(var0.height() - var1.height())
            + Math.abs(var0.getIntValue3() - var1.getIntValue3())
            + Math.abs(var0.getIntValue4() - var1.getIntValue4())
            + Math.abs(var0.getIntValue5() - var1.getIntValue5())
            + Math.abs(var0.getIntValue6() - var1.getIntValue6())
            + Math.abs(var0.getIntValue7() - var1.getIntValue7())
         : Integer.MAX_VALUE;
   }

   private static float method7174(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = var0 > 0.0F ? var4 : var6;
      float var9 = var0 > 0.0F ? var5 : var7;
      float var10 = var1 > 0.0F ? var8 : var9;
      float var11 = Math.abs(var0) - var2 + var10;
      float var12 = Math.abs(var1) - var3 + var10;
      return Math.min(Math.max(var11, var12), 0.0F) + (float)Math.hypot(Math.max(var11, 0.0F), Math.max(var12, 0.0F)) - var10;
   }

   private static float method7175(float var0, float var1, float var2) {
      if (var0 == var1) {
         return var2 < var0 ? 0.0F : 1.0F;
      } else {
         float var3 = MathHelper.clamp((var2 - var0) / (var1 - var0), 0.0F, 1.0F);
         return var3 * var3 * (3.0F - 2.0F * var3);
      }
   }

   public static void method7176(TextureId var0, BufferedImage var1) {
      try {
         ByteArrayOutputStream var2 = new ByteArrayOutputStream();
         ImageIO.write(var1, "png", var2);
         byte[] var3 = var2.toByteArray();
         method7178(var0, var3);
      } catch (Exception var4) {
      }
   }

   public static void method7177(TextureId var0, byte[] var1) {
      try {
         ByteBuffer var2 = BufferUtils.createByteBuffer(var1.length).put(var1);
         var2.flip();
         NativeImageBackedTexture var3 = new NativeImageBackedTexture(var0.getIdentifier()::toString, NativeImage.read(var2));
         mc.getTextureManager().registerTexture(var0.getIdentifier(), var3);
      } catch (Exception var4) {
      }
   }

   public static void method7178(TextureId var0, byte[] var1) {
      try {
         ByteBuffer var2 = BufferUtils.createByteBuffer(var1.length).put(var1);
         var2.flip();
         NativeImage var3 = NativeImage.read(var2);
         mc.execute(() -> mc.getTextureManager().registerTexture(var0.getIdentifier(), new NativeImageBackedTexture(var0.getIdentifier()::toString, var3)));
      } catch (Exception var4) {
      }
   }

   public static void method7179(
      MatrixStack var0, double var1, double var3, double var5, double var7, float var9, float var10, double var11, double var13, double var15, double var17
   ) {
      MsdfRenderer.flushBatch();
      double var19 = var1 + var5;
      double var21 = var3 + var7;
      double var23 = 0.0;
      Matrix4f var25 = var0.peek().getPositionMatrix();
      RenderState.method1898("core/position_tex_color");
      BufferBuilder var26 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE);
      var26.vertex(var25, (float)var1, (float)var21, (float)var23).texture(var9 / (float)var15, (var10 + (float)var13) / (float)var17);
      var26.vertex(var25, (float)var19, (float)var21, (float)var23).texture((var9 + (float)var11) / (float)var15, (var10 + (float)var13) / (float)var17);
      var26.vertex(var25, (float)var19, (float)var3, (float)var23).texture((var9 + (float)var11) / (float)var15, var10 / (float)var17);
      var26.vertex(var25, (float)var1, (float)var3, (float)var23).texture(var9 / (float)var15, (var10 + 0.0F) / (float)var17);
      ImmediateRenderer.method8015(var26.end());
   }

   public static void method7180(
      MatrixStack var0,
      double var1,
      double var3,
      double var5,
      double var7,
      float var9,
      float var10,
      double var11,
      double var13,
      double var15,
      double var17,
      Color var19,
      Color var20,
      Color var21,
      Color var22
   ) {
      MsdfRenderer.flushBatch();
      RenderState.method1898("core/position_tex_color");
      BufferBuilder var23 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
      method7182(var23, var0, var1, var3, var5, var7, var9, var10, var11, var13, var15, var17, var19, var20, var21, var22);
      ImmediateRenderer.method8015(var23.end());
   }

   public static void method7181(
      BufferBuilder var0,
      MatrixStack var1,
      double var2,
      double var4,
      double var6,
      double var8,
      float var10,
      float var11,
      double var12,
      double var14,
      double var16,
      double var18,
      int var20,
      int var21,
      int var22,
      int var23
   ) {
      double var24 = var2 + var6;
      double var26 = var4 + var8;
      double var28 = 0.0;
      Matrix4f var30 = var1.peek().getPositionMatrix();
      var0.vertex(var30, (float)var2, (float)var26, (float)var28).texture(var10 / (float)var16, (var11 + (float)var14) / (float)var18).color(var20);
      var0.vertex(var30, (float)var24, (float)var26, (float)var28)
         .texture((var10 + (float)var12) / (float)var16, (var11 + (float)var14) / (float)var18)
         .color(var21);
      var0.vertex(var30, (float)var24, (float)var4, (float)var28).texture((var10 + (float)var12) / (float)var16, var11 / (float)var18).color(var22);
      var0.vertex(var30, (float)var2, (float)var4, (float)var28).texture(var10 / (float)var16, (var11 + 0.0F) / (float)var18).color(var23);
   }

   public static void method7182(
      BufferBuilder var0,
      MatrixStack var1,
      double var2,
      double var4,
      double var6,
      double var8,
      float var10,
      float var11,
      double var12,
      double var14,
      double var16,
      double var18,
      Color var20,
      Color var21,
      Color var22,
      Color var23
   ) {
      double var24 = var2 + var6;
      double var26 = var4 + var8;
      double var28 = 0.0;
      Matrix4f var30 = var1.peek().getPositionMatrix();
      var0.vertex(var30, (float)var2, (float)var26, (float)var28).texture(var10 / (float)var16, (var11 + (float)var14) / (float)var18).color(var20.getRGB());
      var0.vertex(var30, (float)var24, (float)var26, (float)var28)
         .texture((var10 + (float)var12) / (float)var16, (var11 + (float)var14) / (float)var18)
         .color(var21.getRGB());
      var0.vertex(var30, (float)var24, (float)var4, (float)var28).texture((var10 + (float)var12) / (float)var16, var11 / (float)var18).color(var22.getRGB());
      var0.vertex(var30, (float)var2, (float)var4, (float)var28).texture(var10 / (float)var16, (var11 + 0.0F) / (float)var18).color(var23.getRGB());
   }

   public static void method7183() {
      RenderState.method1871();
      RenderState.method1873();
      RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void method7184(MatrixStack var0, float var1, float var2, float var3, float var4, float var5, boolean var6, boolean var7, int var8) {
   }

   public static void method7185(MatrixStack var0, float var1, float var2, float var3, Color var4) {
   }

   public static void method7186(MatrixStack var0, float var1, float var2, float var3, float var4, float var5, boolean var6, boolean var7, int var8) {
      MsdfRenderer.flushBatch();
      if (var7) {
         method7156(var0, var1 - var3 * var4, var2, var1 + var3 * var4 - (var1 - var3 * var4), var3, 10, method7189(new Color(var8), 140));
      }

      var0.push();
      method7183();
      Matrix4f var9 = var0.peek().getPositionMatrix();
      RenderState.method1898("core/position_color");
      BufferBuilder var10 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
      var10.vertex(var9, var1, var2, 0.0F).color(var8);
      var10.vertex(var9, var1 - var3 * var4, var2 + var3, 0.0F).color(var8);
      var10.vertex(var9, var1, var2 + var3 - var5, 0.0F).color(var8);
      var10.vertex(var9, var1, var2, 0.0F).color(var8);
      var8 = method7197(new Color(var8), 0.8F).getRGB();
      var10.vertex(var9, var1, var2, 0.0F).color(var8);
      var10.vertex(var9, var1, var2 + var3 - var5, 0.0F).color(var8);
      var10.vertex(var9, var1 + var3 * var4, var2 + var3, 0.0F).color(var8);
      var10.vertex(var9, var1, var2, 0.0F).color(var8);
      if (var6) {
         var8 = method7197(new Color(var8), 0.6F).getRGB();
         var10.vertex(var9, var1 - var3 * var4, var2 + var3, 0.0F).color(var8);
         var10.vertex(var9, var1 + var3 * var4, var2 + var3, 0.0F).color(var8);
         var10.vertex(var9, var1, var2 + var3 - var5, 0.0F).color(var8);
         var10.vertex(var9, var1 - var3 * var4, var2 + var3, 0.0F).color(var8);
      }

      ImmediateRenderer.method8015(var10.end());
      method7187();
      var0.pop();
   }

   public static void method7187() {
      RenderState.method1873();
      RenderState.method1872();
      RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static float method7188(float var0, float var1, float var2) {
      boolean var3 = var0 > var1;
      if (var2 < 0.0F) {
         var2 = 0.0F;
      } else if (var2 > 1.0F) {
         var2 = 1.0F;
      }

      float var4 = Math.max(var0, var1) - Math.min(var0, var1);
      float var5 = var4 * var2;
      return var1 + (var3 ? var5 : -var5);
   }

   public static Color method7189(Color var0, int var1) {
      return new Color(var0.getRed(), var0.getGreen(), var0.getBlue(), MathHelper.clamp(var1, 0, 255));
   }

   public static Color method7190(Color var0, Color var1, double var2, double var4) {
      int var6 = (int)((System.currentTimeMillis() / var2 + var4) % 360.0);
      var6 = (var6 >= 180 ? 360 - var6 : var6) * 2;
      return method7200(var0, var1, var6 / 360.0F);
   }

   public static Color method7191(boolean var0, int var1) {
      float var2 = var0 ? 3500.0F : 3000.0F;
      float var3 = (float)(System.currentTimeMillis() % (int)var2 + var1);
      if (var3 > var2) {
         var3 -= var2;
      }

      var3 /= var2;
      if (var3 > 0.5F) {
         var3 = 0.5F - (var3 - 0.5F);
      }

      var3 += 0.5F;
      return Color.getHSBColor(var3, 0.4F, 1.0F);
   }

   public static Color method7192(int var0, float var1, float var2) {
      double var3 = Math.ceil((float)(System.currentTimeMillis() + var0) / 16.0F);
      var3 %= 360.0;
      return Color.getHSBColor((float)(var3 / 360.0), var1, var2);
   }

   public static Color method7193(int var0, int var1) {
      int var2 = (int)((System.currentTimeMillis() / var0 + var1) % 360L);
      int var3;
      return Color.getHSBColor((float)((var3 = var2 % 360) / 360.0) < 0.5 ? -((float)(var3 / 360.0)) : (float)(var3 / 360.0), 0.5F, 1.0F);
   }

   public static Color method7194(Color var0) {
      float[] var1 = Color.RGBtoHSB(var0.getRed(), var0.getGreen(), var0.getBlue(), null);
      float var2 = 0.84F;
      float var3 = var1[0] - var2;
      return new Color(Color.HSBtoRGB(var3, var1[1], var1[2]));
   }

   public static Color method7195(Color var0, float var1) {
      var1 = Math.min(1.0F, Math.max(0.0F, var1));
      return new Color(var0.getRed(), var0.getGreen(), var0.getBlue(), (int)(var0.getAlpha() * var1));
   }

   public static int method7196(int var0, float var1) {
      var1 = Math.min(1.0F, Math.max(0.0F, var1));
      Color var2 = new Color(var0);
      return new Color(var2.getRed(), var2.getGreen(), var2.getBlue(), (int)(var2.getAlpha() * var1)).getRGB();
   }

   public static Color method7197(Color var0, float var1) {
      return new Color(
         Math.max((int)(var0.getRed() * var1), 0), Math.max((int)(var0.getGreen() * var1), 0), Math.max((int)(var0.getBlue() * var1), 0), var0.getAlpha()
      );
   }

   public static Color method7198(int var0, int var1, float var2, float var3, float var4) {
      int var5 = (int)((System.currentTimeMillis() / var0 + var1) % 360L);
      float var6 = var5 / 360.0F;
      Color var7 = new Color(Color.HSBtoRGB(var6, var2, var3));
      return new Color(var7.getRed(), var7.getGreen(), var7.getBlue(), Math.max(0, Math.min(255, (int)(var4 * 255.0F))));
   }

   public static Color method7199(int var0, int var1, Color var2, Color var3, boolean var4) {
      int var5 = (int)((System.currentTimeMillis() / var0 + var1) % 360L);
      var5 = (var5 >= 180 ? 360 - var5 : var5) * 2;
      return var4 ? method7201(var2, var3, var5 / 360.0F) : method7200(var2, var3, var5 / 360.0F);
   }

   public static Color method7200(Color var0, Color var1, float var2) {
      var2 = Math.min(1.0F, Math.max(0.0F, var2));
      return new Color(
         method7204(var0.getRed(), var1.getRed(), var2),
         method7204(var0.getGreen(), var1.getGreen(), var2),
         method7204(var0.getBlue(), var1.getBlue(), var2),
         method7204(var0.getAlpha(), var1.getAlpha(), var2)
      );
   }

   public static Color method7201(Color var0, Color var1, float var2) {
      var2 = Math.min(1.0F, Math.max(0.0F, var2));
      float[] var3 = Color.RGBtoHSB(var0.getRed(), var0.getGreen(), var0.getBlue(), null);
      float[] var4 = Color.RGBtoHSB(var1.getRed(), var1.getGreen(), var1.getBlue(), null);
      Color var5 = Color.getHSBColor(method7203(var3[0], var4[0], var2), method7203(var3[1], var4[1], var2), method7203(var3[2], var4[2], var2));
      return new Color(var5.getRed(), var5.getGreen(), var5.getBlue(), method7204(var0.getAlpha(), var1.getAlpha(), var2));
   }

   public static double method7202(double var0, double var2, double var4) {
      return var0 + (var2 - var0) * var4;
   }

   public static float method7203(float var0, float var1, double var2) {
      return (float)method7202(var0, var1, (float)var2);
   }

   public static int method7204(int var0, int var1, double var2) {
      return (int)method7202(var0, var1, (float)var2);
   }

   public static BufferBuilder method7205(MatrixStack var0, float var1, float var2, float var3, float var4) {
      method7183();
      Matrix4f var5 = var0.peek().getPositionMatrix();
      BufferBuilder var6 = Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION);
      method7206(var6, var5, var1, var2, var1 + var3, var2 + var4);
      return var6;
   }

   public static void method7206(BufferBuilder var0, Matrix4f var1, float var2, float var3, float var4, float var5) {
      var0.vertex(var1, var2, var3, 0.0F);
      var0.vertex(var1, var2, var5, 0.0F);
      var0.vertex(var1, var4, var5, 0.0F);
      var0.vertex(var1, var4, var3, 0.0F);
   }

   public static boolean method7207(Color var0) {
      return method7208(var0.getRed() / 255.0F, var0.getGreen() / 255.0F, var0.getBlue() / 255.0F);
   }

   public static boolean method7208(float var0, float var1, float var2) {
      return method7209(var0, var1, var2, 0.0F, 0.0F, 0.0F) < method7209(var0, var1, var2, 1.0F, 1.0F, 1.0F);
   }

   public static float method7209(float var0, float var1, float var2, float var3, float var4, float var5) {
      float var6 = var3 - var0;
      float var7 = var4 - var1;
      float var8 = var5 - var2;
      return (float)Math.sqrt(var6 * var6 + var7 * var7 + var8 * var8);
   }

   @NotNull
   public static Color method7210(@NotNull Color var0, @NotNull Color var1, float var2, boolean var3) {
      if (!var3) {
         return var2 >= 0.95 ? var1 : var0;
      } else {
         int var4 = var1.getRed() - var0.getRed();
         int var5 = var1.getGreen() - var0.getGreen();
         int var6 = var1.getBlue() - var0.getBlue();
         int var7 = var1.getAlpha() - var0.getAlpha();
         return new Color(
            method7211(var0.getRed() + (int)(var4 * var2)),
            method7211(var0.getGreen() + (int)(var5 * var2)),
            method7211(var0.getBlue() + (int)(var6 * var2)),
            method7211(var0.getAlpha() + (int)(var7 * var2))
         );
      }
   }

   private static int method7211(int var0) {
      return var0 > 255 ? 255 : Math.max(var0, 0);
   }

   public static void method7212(BufferBuilder var0) {
      MsdfRenderer.flushBatch();
      BuiltBuffer var1 = var0.endNullable();
      if (var1 != null) {
         ImmediateRenderer.method8015(var1);
      }
   }

   @Generated
   private ShapeRenderer() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static final class Data {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;
      private final int intValue5;
      private final int intValue6;
      private final int intValue7;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public int intValue4() {
         return this.intValue4;
      }

      public int intValue5() {
         return this.intValue5;
      }

      public int intValue6() {
         return this.intValue6;
      }

      public int intValue7() {
         return this.intValue7;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.intValue4, other.intValue4) && java.util.Objects.equals(this.intValue5, other.intValue5) && java.util.Objects.equals(this.intValue6, other.intValue6) && java.util.Objects.equals(this.intValue7, other.intValue7);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3, intValue4, intValue5, intValue6, intValue7);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "intValue4=" + this.intValue4 + ", " + "intValue5=" + this.intValue5 + ", " + "intValue6=" + this.intValue6 + ", " + "intValue7=" + this.intValue7 + "]";
      }

      public Data(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
         this.intValue = var1;
         this.intValue2 = var2;
         this.intValue3 = var3;
         this.intValue4 = var4;
         this.intValue5 = var5;
         this.intValue6 = var6;
         this.intValue7 = var7;
      }

      public int width() {
         return this.intValue;
      }

      public int height() {
         return this.intValue2;
      }

      public int getIntValue3() {
         return this.intValue3;
      }

      public int getIntValue4() {
         return this.intValue4;
      }

      public int getIntValue5() {
         return this.intValue5;
      }

      public int getIntValue6() {
         return this.intValue6;
      }

      public int getIntValue7() {
         return this.intValue7;
      }
   }

   public static final class Data2 {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final int intValue;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, intValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "intValue=" + this.intValue + "]";
      }

      public Data2(float var1, float var2, float var3, float var4, int var5) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.intValue = var5;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float width() {
         return this.floatValue3;
      }

      public float height() {
         return this.floatValue4;
      }

      public int color() {
         return this.intValue;
      }
   }

   public static final class Data3 {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4);
      }

      @Override
      public String toString() {
         return "Data3[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + "]";
      }

      public Data3(float var1, float var2, float var3, float var4) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
      }

      public boolean contains(double var1, double var3) {
         return var1 >= this.floatValue && var1 <= this.floatValue3 && var3 >= this.floatValue2 && var3 <= this.floatValue4;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }
   }

   public static class Entry {
      public final int intValue;
      private int intValue2 = 0;

      public Entry(int var1) {
         this.intValue = var1;
      }

      public void reset() {
         this.intValue2 = 0;
      }

      public boolean method7213() {
         return ++this.intValue2 > 4;
      }
   }

   public static class Entry2 {
      public final TextureId textureId;
      private int intValue = 0;

      public Entry2(NativeImage var1) {
         this.textureId = new TextureId("shadow_" + RandomStringUtils.randomAlphanumeric(8));

         try {
            NativeImageBackedTexture var2 = new NativeImageBackedTexture(this.textureId.getIdentifier()::toString, var1);
            MinecraftAccess.mc.getTextureManager().registerTexture(this.textureId.getIdentifier(), var2);
         } catch (Exception var3) {
            var1.close();
            var3.printStackTrace();
         }
      }

      public Entry2(BufferedImage var1) {
         this.textureId = new TextureId("shadow_" + RandomStringUtils.randomAlphanumeric(8));

         try {
            ByteArrayOutputStream var2 = new ByteArrayOutputStream();
            ImageIO.write(var1, "png", var2);
            byte[] var3 = var2.toByteArray();
            ByteBuffer var4 = BufferUtils.createByteBuffer(var3.length).put(var3);
            var4.flip();
            NativeImageBackedTexture var5 = new NativeImageBackedTexture(this.textureId.getIdentifier()::toString, NativeImage.read(var4));
            MinecraftAccess.mc.getTextureManager().registerTexture(this.textureId.getIdentifier(), var5);
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }

      public void reset() {
         this.intValue = 0;
      }

      public boolean method7214() {
         return ++this.intValue > 300;
      }

      public void method7215() {
         MinecraftAccess.mc.getTextureManager().destroyTexture(this.textureId.getIdentifier());
      }
   }
}

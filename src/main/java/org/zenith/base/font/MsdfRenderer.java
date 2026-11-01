package org.zenith.base.font;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.zenith.RenderState;
import org.zenith.ZenithColor;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.utility.misc.HudBatchState;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.RoundedRectBatch;
import org.zenith.utility.render.display.base.GlowColor;

public final class MsdfRenderer {
   public static final KawaseShaderKey MSDF_FONT_PROGRAM = new KawaseShaderKey(
      ResourceProvider.getShaderIdentifier("msdf_font/data"), VertexFormats.POSITION_TEXTURE_COLOR
   );
   private static final int VANILLA_TEXT_LIGHT = 15728880;
   private static Immediate fallbackImmediate;
   public static final boolean USE_VANILLA_FALLBACK_FOR_TEXT = true;
   private static final int BATCH_INITIAL_BUFFER_SIZE = 65536;
   private static BufferAllocator batchAllocator;
   private static BufferBuilder batchBuilder;
   private static GpuTextureView batchTextureId;
   private static boolean uniformsDirty = true;
   private static float batchRange;
   private static float batchThickness;
   private static float batchSmoothness;
   private static boolean batchFadeout;
   private static float batchFadeoutStart;
   private static float batchFadeoutEnd;
   private static float batchMaxWidth;
   private static float batchTextPosX;
   private static float batchShaderColorR = 1.0F;
   private static float batchShaderColorG = 1.0F;
   private static float batchShaderColorB = 1.0F;
   private static float batchShaderColorA = 1.0F;
   private static float lastUniformRange = Float.NaN;
   private static float lastUniformThickness = Float.NaN;
   private static float lastUniformSmoothness = Float.NaN;
   private static int lastUniformFadeout = -1;
   private static float lastUniformFadeoutStart = Float.NaN;
   private static float lastUniformFadeoutEnd = Float.NaN;
   private static float lastUniformMaxWidth = Float.NaN;
   private static float lastUniformTextPosX = Float.NaN;

   private static Immediate fallbackConsumers() {
      if (fallbackImmediate == null) {
         fallbackImmediate = VertexConsumerProvider.immediate(new BufferAllocator(8192));
      }

      return fallbackImmediate;
   }

   private static VertexConsumer beginBatch(MsdfFont var0, float var1, float var2, boolean var3, float var4, float var5, float var6, float var7) {
      if (!var3) {
         var4 = 0.0F;
         var5 = 0.0F;
         var6 = 0.0F;
         var7 = 0.0F;
      }

      if (HintedText.pending()) {
         HintedText.flush();
      }

      GpuTextureView var8 = var0.getTextureView();
      float var9 = var0.getAtlas().range();
      float[] var10 = RenderState.method1885();
      float var11 = var10[0];
      float var12 = var10[1];
      float var13 = var10[2];
      float var14 = var10[3];
      if (batchBuilder != null
         && (
            batchTextureId != var8
               || batchRange != var9
               || batchThickness != var1
               || batchSmoothness != var2
               || batchFadeout != var3
               || batchFadeoutStart != var4
               || batchFadeoutEnd != var5
               || batchMaxWidth != var6
               || batchTextPosX != var7
               || batchShaderColorR != var11
               || batchShaderColorG != var12
               || batchShaderColorB != var13
               || batchShaderColorA != var14
         )) {
         flushBatch();
      }

      if (batchBuilder == null) {
         if (batchAllocator == null) {
            batchAllocator = new BufferAllocator(65536);
         }

         batchBuilder = new BufferBuilder(batchAllocator, DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         batchTextureId = var8;
         batchRange = var9;
         batchThickness = var1;
         batchSmoothness = var2;
         batchFadeout = var3;
         batchFadeoutStart = var4;
         batchFadeoutEnd = var5;
         batchMaxWidth = var6;
         batchTextPosX = var7;
         batchShaderColorR = var11;
         batchShaderColorG = var12;
         batchShaderColorB = var13;
         batchShaderColorA = var14;
      }

      return batchBuilder;
   }

   public static void flushBatch() {
      RoundedRectBatch.method2935();
      HudBatchState.method7759();
      HintedText.flush();
      flushMsdfPending();
   }

   public static void flushMsdfPending() {
      if (batchBuilder != null) {
         BufferBuilder var0 = batchBuilder;
         batchBuilder = null;
         BuiltBuffer var1 = var0.endNullable();
         if (var1 == null) {
            batchAllocator.clear();
         } else {
            float[] var2 = RenderState.method1885();
            float var3 = var2[0];
            float var4 = var2[1];
            float var5 = var2[2];
            float var6 = var2[3];
            boolean var7 = var3 != batchShaderColorR || var4 != batchShaderColorG || var5 != batchShaderColorB || var6 != batchShaderColorA;
            RenderState.method1871();
            RenderState.method1873();
            RenderState.method1877();
            if (var7) {
               RenderState.method1883(batchShaderColorR, batchShaderColorG, batchShaderColorB, batchShaderColorA);
            }

            RenderState.method1889(0, batchTextureId);
            KawaseShaderKey var8 = MSDF_FONT_PROGRAM.method5165();
            if (uniformsDirty) {
               uniformsDirty = false;
               lastUniformRange = Float.NaN;
               lastUniformThickness = Float.NaN;
               lastUniformSmoothness = Float.NaN;
               lastUniformFadeout = -1;
               lastUniformFadeoutStart = Float.NaN;
               lastUniformFadeoutEnd = Float.NaN;
               lastUniformMaxWidth = Float.NaN;
               lastUniformTextPosX = Float.NaN;
            }

            if (lastUniformRange != batchRange) {
               lastUniformRange = batchRange;
               var8.method5166("Range").method7412(batchRange);
            }

            if (lastUniformThickness != batchThickness) {
               lastUniformThickness = batchThickness;
               var8.method5166("Thickness").method7412(batchThickness);
            }

            if (lastUniformSmoothness != batchSmoothness) {
               lastUniformSmoothness = batchSmoothness;
               var8.method5166("Smoothness").method7412(batchSmoothness);
            }

            int var9 = batchFadeout ? 1 : 0;
            if (lastUniformFadeout != var9) {
               lastUniformFadeout = var9;
               var8.method5166("EnableFadeout").set(var9);
            }

            if (batchFadeout) {
               if (lastUniformFadeoutStart != batchFadeoutStart) {
                  lastUniformFadeoutStart = batchFadeoutStart;
                  var8.method5166("FadeoutStart").method7412(batchFadeoutStart);
               }

               if (lastUniformFadeoutEnd != batchFadeoutEnd) {
                  lastUniformFadeoutEnd = batchFadeoutEnd;
                  var8.method5166("FadeoutEnd").method7412(batchFadeoutEnd);
               }

               if (lastUniformMaxWidth != batchMaxWidth) {
                  lastUniformMaxWidth = batchMaxWidth;
                  var8.method5166("MaxWidth").method7412(batchMaxWidth);
               }

               if (lastUniformTextPosX != batchTextPosX) {
                  lastUniformTextPosX = batchTextPosX;
                  var8.method5166("TextPosX").method7412(batchTextPosX);
               }
            }

            try {
               ImmediateRenderer.method8015(var1);
            } finally {
               RenderState.method1893(0);
               if (var7) {
                  RenderState.method1883(var3, var4, var5, var6);
               }

               RenderState.method1876();
               RenderState.method1872();
               batchAllocator.clear();
            }
         }
      }
   }

   public static void renderText(MsdfFont var0, String var1, float var2, int var3, Matrix4f var4, float var5, float var6, float var7) {
      renderText(var0, var1, var2, var3, var4, var5, var6, var7, false, 0.0F, 1.0F, 0.0F);
   }

   public static void renderText(
      MsdfFont var0, String var1, float var2, int var3, Matrix4f var4, float var5, float var6, float var7, boolean var8, float var9, float var10, float var11
   ) {
      float var12 = 0.0F;
      float var13 = 0.45F;
      float var14 = 0.0F;
      var1 = NameProtectModule.method5149(var1);
      if (var8 || !HintedText.render(var0, var1, var2, var3, var4, var5, var6 + textLineOffset(var0, var2), var7)) {
         float var15 = var5;
         float var16 = var11;
         if (var8 && var11 > 0.0F) {
            Vector4f var17 = new Vector4f(var5, 0.0F, 0.0F, 1.0F);
            Vector4f var18 = new Vector4f(var5 + var11, 0.0F, 0.0F, 1.0F);
            var4.transform(var17);
            var4.transform(var18);
            var15 = var17.x;
            var16 = var18.x - var17.x;
         }

         VertexConsumer var20 = beginBatch(var0, var12, var13, var8, var9, var10, var16, var15);
         float var21 = var6 + textLineOffset(var0, var2);
         var0.applyGlyphs(var4, var20, var1, var2, var12 * 0.5F * var2, var14, var5, var21, var7, var3);
      }
   }

   public static void renderText(
      MsdfFont var0, String var1, float var2, int var3, Matrix4f var4, float var5, float var6, float var7, boolean var8, float var9, float var10
   ) {
      float var11 = var0.getWidth(var1, var2) * 2.0F;
      renderText(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public static void renderText(MsdfFont var0, Text var1, float var2, Matrix4f var3, float var4, float var5, float var6) {
      renderTextWithVanillaFallback(var0, var1, var2, var3, var4, var5, var6);
   }

   public static void renderText(
      MsdfFont var0, Text var1, float var2, Matrix4f var3, float var4, float var5, float var6, boolean var7, float var8, float var9, float var10
   ) {
      renderTextWithVanillaFallback(var0, var1, var2, var3, var4, var5, var6);
   }

   public static void renderText(
      MsdfFont var0, Text var1, float var2, Matrix4f var3, float var4, float var5, float var6, boolean var7, float var8, float var9, float var10, int var11
   ) {
      renderTextWithVanillaFallback(var0, var1, var2, var3, var4, var5, var6, var11);
   }

   public static void renderTextWithVanillaFallback(MsdfFont var0, Text var1, float var2, Matrix4f var3, float var4, float var5, float var6) {
      renderTextWithVanillaFallback(var0, var1, var2, var3, var4, var5, var6, ZenithColor.INSTANCE.method7287());
   }

   public static void renderTextWithVanillaFallback(MsdfFont var0, Text var1, float var2, Matrix4f var3, float var4, float var5, float var6, int var7) {
      MinecraftClient var8 = MinecraftClient.getInstance();
      renderTextWithVanillaFallback(var0, var1, var2, var8.textRenderer, fallbackConsumers(), var3, var4, var5, var6, var7);
   }

   public static void renderTextWithVanillaFallback(
      MsdfFont var0, Text var1, float var2, TextRenderer var3, VertexConsumerProvider var4, Matrix4f var5, float var6, float var7, float var8, int var9
   ) {
      List<FormattedTextProcessor.TextSegment> var10 = FormattedTextProcessor.processText(var1, var9);
      float var11 = var6;

      for (Object var13_o : (Iterable<?>)(Object)(var10)) {
         FormattedTextProcessor.TextSegment var13 = (FormattedTextProcessor.TextSegment)var13_o;
         var11 = renderTextSegmentWithFallback(var0, var3, var4, normalizeTextForMsdf(var13.text()), var2, var13.color(), var5, var11, var7, var8);
      }
   }

   public static void renderText(MsdfFont var0, Text var1, float var2, Matrix4f var3, float var4, float var5, float var6, boolean var7, float var8, float var9) {
      renderTextWithVanillaFallback(var0, var1, var2, var3, var4, var5, var6);
   }

   public static void renderText(MsdfFont var0, String var1, float var2, GlowColor var3, Matrix4f var4, float var5, float var6, float var7) {
      renderText(var0, var1, var2, var3, var4, var5, var6, var7, false, 0.0F, 1.0F, 0.0F);
   }

   public static void renderText(
      MsdfFont var0,
      String var1,
      float var2,
      GlowColor var3,
      Matrix4f var4,
      float var5,
      float var6,
      float var7,
      boolean var8,
      float var9,
      float var10,
      float var11
   ) {
      var1 = var1.replace("і", "i").replace("І", "I");
      float var12 = 0.05F;
      float var13 = 0.5F;
      float var14 = 0.0F;
      var1 = NameProtectModule.method5149(var1);
      float var15 = var5;
      float var16 = var11;
      if (var8 && var11 > 0.0F) {
         Vector4f var17 = new Vector4f(var5, 0.0F, 0.0F, 1.0F);
         Vector4f var18 = new Vector4f(var5 + var11, 0.0F, 0.0F, 1.0F);
         var4.transform(var17);
         var4.transform(var18);
         var15 = var17.x;
         var16 = var18.x - var17.x;
      }

      VertexConsumer var21 = beginBatch(var0, var12, var13, var8, var9, var10, var16, var15);
      float var22 = var6 + textLineOffset(var0, var2);
      var0.applyGlyphs(var4, var21, var1, var2, var12 * 0.5F * var2, var14, var5, var22, var7, var3);
   }

   public static float textLineOffset(MsdfFont var0, float var1) {
      return var0.getBaseline() * var1;
   }

   private static float renderTextSegmentWithFallback(
      MsdfFont var0, TextRenderer var1, VertexConsumerProvider var2, String var3, float var4, int var5, Matrix4f var6, float var7, float var8, float var9
   ) {
      StringBuilder var10 = new StringBuilder();
      boolean var11 = false;
      boolean var12 = false;
      int var13 = 0;

      while (var13 < var3.length()) {
         int var14 = var3.codePointAt(var13);
         int var15 = var13 + Character.charCount(var14);
         if (var14 == 167) {
            var13 = skipFormattingCode(var3, var15);
         } else {
            boolean var16 = canRenderWithMsdf(var0, var14);
            if (var11 && var12 != var16) {
               var7 = renderTextRun(var0, var1, var2, var10.toString(), var12, var4, var5, var6, var7, var8, var9);
               var10.setLength(0);
            }

            var10.append(var3, var13, var15);
            var11 = true;
            var12 = var16;
            var13 = var15;
         }
      }

      if (var11) {
         var7 = renderTextRun(var0, var1, var2, var10.toString(), var12, var4, var5, var6, var7, var8, var9);
      }

      return var7;
   }

   private static float renderTextRun(
      MsdfFont var0,
      TextRenderer var1,
      VertexConsumerProvider var2,
      String var3,
      boolean var4,
      float var5,
      int var6,
      Matrix4f var7,
      float var8,
      float var9,
      float var10
   ) {
      if (var3.isEmpty()) {
         return var8;
      } else if (var4) {
         renderText(var0, var3, var5, var6, var7, var8, var9, var10);
         return var8 + var0.getWidth(var3, var5);
      } else {
         renderVanillaText(var0, var1, var2, var3, var5, var6, var7, var8, var9, var10);
         return var8 + var1.getWidth(var3) * vanillaTextScale(var1, var5);
      }
   }

   private static void renderVanillaText(
      MsdfFont var0, TextRenderer var1, VertexConsumerProvider var2, String var3, float var4, int var5, Matrix4f var6, float var7, float var8, float var9
   ) {
      flushBatch();
      float var10 = vanillaTextScale(var1, var4);
      float var11 = var8 + textLineOffset(var0, var4) - (9 - 2) * var10;
      Matrix4f var12 = new Matrix4f(var6).translate(var7, var11, var9).scale(var10, var10, 1.0F);
      boolean var13 = var2 == fallbackImmediate;
      TextLayerType var14 = var13 && ImmediateRenderer.method8005() ? TextLayerType.SEE_THROUGH : TextLayerType.NORMAL;
      var1.draw(var3, 0.0F, 0.0F, var5, false, var12, var2, var14, 0, 15728880);
      if (var13) {
         ImmediateRenderer.method8006(fallbackImmediate::draw);
      }
   }

   private static float vanillaTextScale(TextRenderer var0, float var1) {
      return var1 / Math.max(1.0F, 9.0F);
   }

   private static boolean canRenderWithMsdf(MsdfFont var0, int var1) {
      return var1 <= 65535 && var0.hasGlyph(var1);
   }

   private static int skipFormattingCode(String var0, int var1) {
      return var1 >= var0.length() ? var1 : var1 + Character.charCount(var0.codePointAt(var1));
   }

   private static String normalizeTextForMsdf(String var0) {
      return MsdfFont.normalizeSmallCaps(var0);
   }

   public static void renderText(
      MsdfFont var0, String var1, float var2, GlowColor var3, Matrix4f var4, float var5, float var6, float var7, boolean var8, float var9, float var10
   ) {
      float var11 = var0.getWidth(var1, var2) * 2.0F;
      renderText(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @Generated
   private MsdfRenderer() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}

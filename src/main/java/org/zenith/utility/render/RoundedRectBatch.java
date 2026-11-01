package org.zenith.utility.render;

import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.zenith.RenderState;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.MsdfFont;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class RoundedRectBatch {
   public static boolean enabled = true;
   private static final RoundedRectBatch INSTANCE = new RoundedRectBatch();
   private static RoundedRectBatch INSTANCE2;
   private static final Comparator<RoundedRectBatch.Entry3> comparator = Comparator.comparingInt(var0 -> var0.intValue2);
   private final List<RoundedRectBatch.Entry2> items = new ArrayList<>(256);
   private final List<RoundedRectBatch.Entry3> items2 = new ArrayList<>(256);
   private final RoundedRectBatch.Entry<RoundedRectBatch.Entry2> entry = new RoundedRectBatch.Entry<>(
      RoundedRectBatch.Entry2::new, RoundedRectBatch.Entry2::method2957
   );
   private final RoundedRectBatch.Entry<RoundedRectBatch.Entry3> entry2 = new RoundedRectBatch.Entry<>(
      RoundedRectBatch.Entry3::new, RoundedRectBatch.Entry3::method2958
   );
   private final MatrixStack matrixStack = new MatrixStack();
   private boolean replaying;

   private RoundedRectBatch() {
   }

   public static void begin() {
      if (!enabled) {
         INSTANCE2 = null;
      } else {
         INSTANCE.method2955();
         INSTANCE.replaying = false;
         INSTANCE2 = INSTANCE;
      }
   }

   public static void method2933() {
      RoundedRectBatch var0 = INSTANCE2;
      INSTANCE2 = null;
      if (var0 != null) {
         try {
            var0.method2945();
         } finally {
            var0.method2955();
         }
      }
   }

   public static boolean method2934() {
      RoundedRectBatch var0 = INSTANCE2;
      return var0 != null && !var0.replaying;
   }

   public static void method2935() {
      RoundedRectBatch var0 = INSTANCE2;
      if (var0 != null && !var0.replaying && (!var0.items.isEmpty() || !var0.items2.isEmpty())) {
         var0.method2945();
      }
   }

   public static void method2936(Matrix4f var0, float var1, float var2, float var3, float var4, ZenithColor var5) {
      RoundedRectBatch.Entry2 var6 = INSTANCE2.entry.method2956();
      var6.type = RoundedRectBatch.Type.RECT;
      var6.matrix4f.set(var0);
      var6.floatValue = var1;
      var6.floatValue2 = var2;
      var6.width = var3;
      var6.height = var4;
      var6.zenithColor = var5;
      INSTANCE2.items.add(var6);
   }

   public static void method2937(Matrix4f var0, float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, ZenithColor var6) {
      RoundedRectBatch.Entry2 var7 = INSTANCE2.entry.method2956();
      var7.type = RoundedRectBatch.Type.BATCH_ROUNDED;
      var7.matrix4f.set(var0);
      var7.floatValue = var1;
      var7.floatValue2 = var2;
      var7.width = var3;
      var7.height = var4;
      var7.floatValue3 = var5.getFloatValue();
      var7.floatValue4 = var5.getFloatValue4();
      var7.floatValue5 = var5.getFloatValue2();
      var7.floatValue6 = var5.getFloatValue3();
      var7.intValue = var6.method7287();
      INSTANCE2.items.add(var7);
   }

   public static void method2938(Matrix4f var0, float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, ZenithColor var6) {
      if (var5.getFloatValue() == var5.getFloatValue4() && var5.getFloatValue() == var5.getFloatValue2() && var5.getFloatValue() == var5.getFloatValue3()) {
         method2937(var0, var1, var2, var3, var4, var5, var6);
      } else {
         RoundedRectBatch.Entry2 var7 = INSTANCE2.entry.method2956();
         var7.type = RoundedRectBatch.Type.SINGLE_ROUNDED;
         var7.matrix4f.set(var0);
         var7.floatValue = var1;
         var7.floatValue2 = var2;
         var7.width = var3;
         var7.height = var4;
         var7.proxyDotRadiusData = var5;
         var7.zenithColor = var6;
         INSTANCE2.items.add(var7);
      }
   }

   public static void method2939(Matrix4f var0, float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, GlowColor var6) {
      RoundedRectBatch.Entry2 var7 = INSTANCE2.entry.method2956();
      var7.type = RoundedRectBatch.Type.SINGLE_GRADIENT;
      var7.matrix4f.set(var0);
      var7.floatValue = var1;
      var7.floatValue2 = var2;
      var7.width = var3;
      var7.height = var4;
      var7.proxyDotRadiusData = var5;
      var7.zenithColor = var6.getZenithColor();
      var7.zenithColor2 = var6.getZenithColor2();
      var7.zenithColor3 = var6.getZenithColor4();
      var7.zenithColor4 = var6.getZenithColor3();
      INSTANCE2.items.add(var7);
   }

   public static void method2940(
      Matrix4f var0,
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      ProxyDotRadiusData var6,
      ZenithColor var7,
      ZenithColor var8,
      ZenithColor var9,
      ZenithColor var10
   ) {
      RoundedRectBatch.Entry2 var11 = INSTANCE2.entry.method2956();
      var11.type = RoundedRectBatch.Type.BORDER;
      var11.matrix4f.set(var0);
      var11.floatValue = var1;
      var11.floatValue2 = var2;
      var11.width = var3;
      var11.height = var4;
      var11.floatValue7 = var5;
      var11.proxyDotRadiusData = var6;
      var11.zenithColor = var7;
      var11.zenithColor2 = var8;
      var11.zenithColor3 = var9;
      var11.zenithColor4 = var10;
      INSTANCE2.items.add(var11);
   }

   public static void method2941(Font var0, String var1, Matrix4f var2, float var3, float var4, int var5) {
      if (var1 != null && !var1.isEmpty()) {
         RoundedRectBatch.Entry3 var6 = method2944(var0, var1, var2, var3, var4);
         var6.type2 = RoundedRectBatch.Type2.PLAIN;
         var6.intValue = var5;
         INSTANCE2.items2.add(var6);
      }
   }

   public static void method2942(Font var0, String var1, Matrix4f var2, float var3, float var4, int var5, boolean var6, float var7, float var8, float var9) {
      if (var1 != null && !var1.isEmpty()) {
         RoundedRectBatch.Entry3 var10 = method2944(var0, var1, var2, var3, var4);
         var10.type2 = RoundedRectBatch.Type2.FADEOUT;
         var10.intValue = var5;
         var10.flag = var6;
         var10.floatValue3 = var7;
         var10.floatValue4 = var8;
         var10.maxWidth = var9;
         INSTANCE2.items2.add(var10);
      }
   }

   public static void method2943(Font var0, String var1, Matrix4f var2, float var3, float var4, GlowColor var5) {
      if (var1 != null && !var1.isEmpty()) {
         RoundedRectBatch.Entry3 var6 = method2944(var0, var1, var2, var3, var4);
         var6.type2 = RoundedRectBatch.Type2.GRADIENT;
         var6.glowColor = var5;
         INSTANCE2.items2.add(var6);
      }
   }

   private static RoundedRectBatch.Entry3 method2944(Font var0, String var1, Matrix4f var2, float var3, float var4) {
      RoundedRectBatch.Entry3 var5 = INSTANCE2.entry2.method2956();
      var5.font = var0.getFont();
      var5.size = var0.getSize();
      var5.text = var1;
      var5.matrix4f.set(var2);
      var5.floatValue = var3;
      var5.floatValue2 = var4;
      var5.intValue2 = var5.font.getTextureId();
      return var5;
   }

   private void method2945() {
      if (!this.items.isEmpty() || !this.items2.isEmpty()) {
         this.replaying = true;

         try {
            this.method2946();
            this.method2954();
         } finally {
            this.replaying = false;
            this.method2955();
         }
      }
   }

   private void method2946() {
      if (!this.items.isEmpty()) {
         BufferBuilder var1 = null;
         BufferBuilder var2 = null;
         float var3 = 0.0F;
         float var4 = 0.0F;
         float var5 = 0.0F;
         float var6 = 0.0F;

         for (int var7 = 0; var7 < this.items.size(); var7++) {
            RoundedRectBatch.Entry2 var8 = this.items.get(var7);
            if (var8.type == RoundedRectBatch.Type.RECT) {
               var1 = this.method2951(var1);
               if (var2 == null) {
                  var2 = this.method2947();
               }

               this.method2949(var2, var8);
            } else {
               var2 = this.method2948(var2);
               if (var8.type == RoundedRectBatch.Type.BATCH_ROUNDED) {
                  if (var1 == null || var8.floatValue3 != var3 || var8.floatValue4 != var4 || var8.floatValue5 != var5 || var8.floatValue6 != var6) {
                     var1 = this.method2951(var1);
                     var1 = this.method2950(var8);
                     var3 = var8.floatValue3;
                     var4 = var8.floatValue4;
                     var5 = var8.floatValue5;
                     var6 = var8.floatValue6;
                  }

                  this.method2952(var1, var8);
               } else {
                  var1 = this.method2951(var1);
                  this.method2953(var8);
               }
            }
         }

         this.method2951(var1);
         this.method2948(var2);
      }
   }

   private BufferBuilder method2947() {
      RenderState.method1898("core/position_color");
      RenderState.method1871();
      RenderState.method1873();
      return Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_COLOR);
   }

   private BufferBuilder method2948(BufferBuilder var1) {
      if (var1 != null) {
         ImmediateRenderer.method8015(var1.end());
         RenderState.method1872();
      }

      return null;
   }

   private void method2949(BufferBuilder var1, RoundedRectBatch.Entry2 var2) {
      int var3 = var2.zenithColor.method7287();
      float var4 = var2.floatValue;
      float var5 = var2.floatValue2;
      float var6 = var2.width;
      float var7 = var2.height;
      var1.vertex(var2.matrix4f, var4, var5 + var7, 0.0F).color(var3);
      var1.vertex(var2.matrix4f, var4 + var6, var5 + var7, 0.0F).color(var3);
      var1.vertex(var2.matrix4f, var4 + var6, var5, 0.0F).color(var3);
      var1.vertex(var2.matrix4f, var4, var5, 0.0F).color(var3);
   }

   private BufferBuilder method2950(RoundedRectBatch.Entry2 var1) {
      KawaseShaderKey var2 = RenderShaders.kawaseShaderKey2;
      var2.method5165();
      var2.method5166("Radius").method7415(var1.floatValue3, var1.floatValue4, var1.floatValue5, var1.floatValue6);
      var2.method5166("Smoothness").method7412(0.8F);
      RenderState.method1871();
      RenderState.method1873();
      return Tessellator.getInstance().begin(DrawMode.QUADS, net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR);
   }

   private BufferBuilder method2951(BufferBuilder var1) {
      if (var1 != null) {
         ImmediateRenderer.method8015(var1.end());
         RenderState.method1872();
      }

      return null;
   }

   private void method2952(BufferBuilder var1, RoundedRectBatch.Entry2 var2) {
      float var3 = var2.floatValue;
      float var4 = var2.floatValue2;
      float var5 = var2.width;
      float var6 = var2.height;
      int var7 = var2.intValue;
      var1.vertex(var2.matrix4f, var3, var4, 0.0F).texture(var2.width, var2.height).color(var7);
      var1.vertex(var2.matrix4f, var3, var4 + var6, 0.0F).texture(var2.width, var2.height).color(var7);
      var1.vertex(var2.matrix4f, var3 + var5, var4 + var6, 0.0F).texture(var2.width, var2.height).color(var7);
      var1.vertex(var2.matrix4f, var3 + var5, var4, 0.0F).texture(var2.width, var2.height).color(var7);
   }

   private void method2953(RoundedRectBatch.Entry2 var1) {
      this.matrixStack.peek().getPositionMatrix().set(var1.matrix4f);
      switch (var1.type) {
         case RECT:
            RenderShaders.method5260(this.matrixStack.peek().getPositionMatrix(), var1.floatValue, var1.floatValue2, var1.width, var1.height, var1.zenithColor);
         case BATCH_ROUNDED:
         default:
            break;
         case SINGLE_ROUNDED:
            RenderShaders.method5268(
               this.matrixStack.peek().getPositionMatrix(),
               var1.floatValue,
               var1.floatValue2,
               var1.width,
               var1.height,
               var1.proxyDotRadiusData,
               var1.zenithColor
            );
            break;
         case SINGLE_GRADIENT:
            RenderShaders.method5269(
               this.matrixStack.peek().getPositionMatrix(),
               var1.floatValue,
               var1.floatValue2,
               var1.width,
               var1.height,
               var1.proxyDotRadiusData,
               var1.zenithColor,
               var1.zenithColor2,
               var1.zenithColor3,
               var1.zenithColor4
            );
            break;
         case BORDER:
            RenderShaders.method5273(
               this.matrixStack.peek().getPositionMatrix(),
               var1.floatValue,
               var1.floatValue2,
               var1.width,
               var1.height,
               var1.floatValue7,
               var1.proxyDotRadiusData,
               var1.zenithColor,
               var1.zenithColor2,
               var1.zenithColor3,
               var1.zenithColor4
            );
      }
   }

   private void method2954() {
      if (!this.items2.isEmpty()) {
         this.items2.sort(comparator);

         for (int var1 = 0; var1 < this.items2.size(); var1++) {
            RoundedRectBatch.Entry3 var2 = this.items2.get(var1);
            switch (var2.type2) {
               case PLAIN:
                  MsdfRenderer.renderText(var2.font, var2.text, var2.size, var2.intValue, var2.matrix4f, var2.floatValue, var2.floatValue2, 0.0F);
                  break;
               case FADEOUT:
                  MsdfRenderer.renderText(
                     var2.font,
                     var2.text,
                     var2.size,
                     var2.intValue,
                     var2.matrix4f,
                     var2.floatValue,
                     var2.floatValue2,
                     0.0F,
                     var2.flag,
                     var2.floatValue3,
                     var2.floatValue4,
                     var2.maxWidth
                  );
                  break;
               case GRADIENT:
                  MsdfRenderer.renderText(var2.font, var2.text, var2.size, var2.glowColor, var2.matrix4f, var2.floatValue, var2.floatValue2, 0.0F);
            }
         }
      }

      MsdfRenderer.flushBatch();
   }

   private void method2955() {
      this.items.clear();
      this.items2.clear();
      this.entry.reset();
      this.entry2.reset();
   }

   private static final class Entry<T> {
      private final ArrayList<T> arrayList = new ArrayList<>();
      private final Supplier<T> supplier;
      private final Consumer<T> consumer;
      private int intValue;

      public Entry(Supplier<T> var1, Consumer<T> var2) {
         this.supplier = var1;
         this.consumer = var2;
      }

      public T method2956() {
         if (this.intValue < this.arrayList.size()) {
            return this.arrayList.get(this.intValue++);
         } else {
            Object var1 = this.supplier.get();
            this.arrayList.add((T)var1);
            this.intValue++;
            return (T)var1;
         }
      }

      public void reset() {
         for (int var1 = 0; var1 < this.intValue; var1++) {
            this.consumer.accept(this.arrayList.get(var1));
         }

         this.intValue = 0;
      }
   }

   private static final class Entry2 {
      public final Matrix4f matrix4f = new Matrix4f();
      public RoundedRectBatch.Type type;
      public float floatValue;
      public float floatValue2;
      public float width;
      public float height;
      public float floatValue3;
      public float floatValue4;
      public float floatValue5;
      public float floatValue6;
      public float floatValue7;
      public int intValue;
      public ProxyDotRadiusData proxyDotRadiusData;
      public ZenithColor zenithColor;
      public ZenithColor zenithColor2;
      public ZenithColor zenithColor3;
      public ZenithColor zenithColor4;

      public void method2957() {
         this.proxyDotRadiusData = null;
         this.zenithColor = null;
         this.zenithColor2 = null;
         this.zenithColor3 = null;
         this.zenithColor4 = null;
      }
   }

   private static final class Entry3 {
      public final Matrix4f matrix4f = new Matrix4f();
      public RoundedRectBatch.Type2 type2;
      public MsdfFont font;
      public String text;
      public float size;
      public float floatValue;
      public float floatValue2;
      public int intValue;
      public int intValue2;
      public boolean flag;
      public float floatValue3;
      public float floatValue4;
      public float maxWidth;
      public GlowColor glowColor;

      public void method2958() {
         this.font = null;
         this.text = null;
         this.glowColor = null;
      }
   }

   private static enum Type {
      RECT,
      BATCH_ROUNDED,
      SINGLE_ROUNDED,
      SINGLE_GRADIENT,
      BORDER;
   }

   private static enum Type2 {
      PLAIN,
      FADEOUT,
      GRADIENT;
   }
}

package org.zenith.utility.render;

import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.utility.misc.HudBatchState;
import org.zenith.utility.misc.HudQueuedDraw;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.display.base.HudQueuedContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class Jebriksheet {
   public static boolean flag = true;
   private static final Comparator<Jebriksheet.Handler2> comparator = Comparator.comparingInt(Jebriksheet::method2384);
   private static Jebriksheet INSTANCE;
   private static final ZenithColor zenithColor = new ZenithColor(255, 255, 255, 18);
   private static final Identifier identifier = Zenith.method1501("menu/jebriksheet.png");
   private static final float CONST_floatValue = 1024.0F;
   private final List<Jebriksheet.Entry3> items = new ArrayList<>(64);
   private final List<Jebriksheet.Handler> items2 = new ArrayList<>(192);
   private final List<Jebriksheet.Handler2> items3 = new ArrayList<>(256);
   private final List<Jebriksheet.Handler2> items4 = new ArrayList<>(128);
   private final List<Jebriksheet.Handler2> items5 = new ArrayList<>(128);
   private final List<Jebriksheet.Handler2> items6 = new ArrayList<>(64);
   private final List<KawaseBlur.Data> items7 = new ArrayList<>(64);
   private final List<KawaseBlur.Data2> items8 = new ArrayList<>(64);
   private static final float CONST_floatValue2 = 10.0F;
   private static final float CONST_floatValue3 = 0.08F;
   private final RoundedRectRenderer roundedRectRenderer = new RoundedRectRenderer();
   private final RoundedRectBatch2 roundedRectBatch2 = new RoundedRectBatch2();
   private final RectBatch rectBatch = new RectBatch();
   private final Matrix4f matrix4f = new Matrix4f();
   private boolean flag2;
   private final Jebriksheet.Entry<HudQueuedDraw> entry = new Jebriksheet.Entry<>(HudQueuedDraw::new, HudQueuedDraw::method2487);
   private final Jebriksheet.Entry<Jebriksheet.Entry4> entry2 = new Jebriksheet.Entry<>(Jebriksheet.Entry4::new, Jebriksheet.Entry4::method2388);
   private final Jebriksheet.Entry<Jebriksheet.Entry5> entry3 = new Jebriksheet.Entry<>(Jebriksheet.Entry5::new, Jebriksheet.Entry5::method2389);
   private final Jebriksheet.Entry<Jebriksheet.Entry3> entry4 = new Jebriksheet.Entry<>(Jebriksheet.Entry3::new, Jebriksheet.Entry3::method2387);
   private final Jebriksheet.Entry<Jebriksheet.Entry2> entry5 = new Jebriksheet.Entry<>(Jebriksheet.Entry2::new, Jebriksheet.Entry2::method2386);
   private final List<Jebriksheet.Entry2> items9 = new ArrayList<>(8);
   private final List<Jebriksheet.Entry2> items10 = new ArrayList<>(8);
   private final List<Jebriksheet.Entry2> items11 = new ArrayList<>(8);
   private final List<Jebriksheet.Entry2> items12 = new ArrayList<>(8);
   private final List<Jebriksheet.Entry2> items13 = new ArrayList<>(8);
   private float floatValue = Float.NaN;
   private ProxyDotRadiusData proxyDotRadiusData = ProxyDotRadiusData.INSTANCE;

   public static void method2346(Jebriksheet var0) {
      var0.clear();
      INSTANCE = var0;
   }

   public static void method2347(Jebriksheet var0) {
      if (INSTANCE == var0) {
         INSTANCE = null;
         var0.clear();
      }
   }

   public static boolean method2348() {
      return INSTANCE != null && !INSTANCE.flag2;
   }

   public static void method2349(Runnable var0) {
      Jebriksheet var1 = method2366();
      var1.method2368(var1.items3, var1.items11);
      var1.items3.add(new Jebriksheet.Data(var0));
   }

   public static void method2350(Matrix4f var0, Consumer<Matrix4f> var1) {
      Matrix4f var2 = new Matrix4f(var0);
      Jebriksheet var3 = method2366();
      var3.method2368(var3.items3, var3.items11);
      var3.items3.add(() -> var1.accept(new Matrix4f(var2)));
   }

   public static void method2351(Runnable var0) {
      Jebriksheet var1 = method2366();
      var1.method2368(var1.items4, var1.items12);
      var1.items4.add(new Jebriksheet.Data(var0));
   }

   public static void method2352(Runnable var0) {
      Jebriksheet var1 = method2366();
      var1.method2368(var1.items5, var1.items13);
      var1.items5.add(new Jebriksheet.Data(var0));
   }

   public static void method2353(Runnable var0) {
      Jebriksheet var1 = method2366();
      var1.method2368(var1.items2, var1.items10);
      var1.items2.add(new Jebriksheet.Data(var0));
   }

   public static void method2354(Matrix4f var0, Consumer<Matrix4f> var1) {
      Matrix4f var2 = new Matrix4f(var0);
      Jebriksheet var3 = method2366();
      var3.method2368(var3.items2, var3.items10);
      var3.items2.add(new Jebriksheet.Data(() -> var1.accept(new Matrix4f(var2))));
   }

   public static HudQueuedDraw method2355(HudQueuedContext var0) {
      HudQueuedDraw var1 = method2366().entry.method2385();
      var1.hudQueuedContext = var0;
      return var1;
   }

   public static void method2356(HudQueuedDraw var0) {
      Jebriksheet var1 = method2366();
      var1.method2368(var1.items2, var1.items10);
      var1.items2.add(var0);
   }

   public static void method2357(HudQueuedDraw var0) {
      Jebriksheet var1 = method2366();
      var1.method2368(var1.items3, var1.items11);
      var1.items3.add(var0);
   }

   public static void method2358(HudQueuedDraw var0) {
      Jebriksheet var1 = method2366();
      var1.method2368(var1.items4, var1.items12);
      var1.items4.add(var0);
   }

   public static void method2359(HudQueuedDraw var0) {
      Jebriksheet var1 = method2366();
      var1.method2368(var1.items5, var1.items13);
      var0.intValue10 = method2360(var0);
      var1.items5.add(var0);
   }

   private static int method2360(HudQueuedDraw var0) {
      if (var0.obj instanceof Font var1) {
         return switch (var0.type) {
            case TEXT_FONT_STRING_RGBA, TEXT_FONT_STRING_RGBA_FADEOUT, TEXT_FONT_STRING_INT_FADEOUT, TEXT_FONT_STRING_GRADIENT, TEXT_FONT_TEXT_INT, TEXT_FONT_TEXT -> var1.getFont()
               .getTextureId();
            default -> -1;
         };
      } else {
         return -1;
      }
   }

   public static void method2361(HudQueuedContext var0, Matrix4f var1, int var2, int var3, int var4, int var5) {
      Jebriksheet var6 = method2366();
      Jebriksheet.Entry2 var7 = var6.entry5.method2385();
      var7.hudQueuedContext = var0;
      var7.matrix4f.set(var1);
      var7.intValue = var2;
      var7.intValue2 = var3;
      var7.intValue3 = var4;
      var7.intValue4 = var5;
      var6.items9.add(var7);
   }

   public static void method2362() {
      Jebriksheet var0 = method2366();
      if (!var0.items9.isEmpty()) {
         var0.items9.removeLast();
      }
   }

   public static void method2363(
      Matrix4f var0, float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7, boolean var8, boolean var9
   ) {
      Jebriksheet var10 = method2366();
      Jebriksheet.Entry3 var11 = var10.entry4.method2385();
      var11.matrix4f.set(var0);
      var11.floatValue = var1;
      var11.floatValue2 = var2;
      var11.width = var3;
      var11.height = var4;
      var11.blurRadius = var5;
      var11.proxyDotRadiusData = var6;
      var11.zenithColor = var7;
      var11.flag = var8;
      var11.flag2 = var9;
      var11.flag3 = InterfaceModule.INSTANCE.method6820();
      var11.flag4 = InterfaceModule.INSTANCE.method6818();
      var11.flag5 = InterfaceModule.INSTANCE.method6821();
      var11.intValue = InterfaceModule.INSTANCE.method6823();
      var11.proxyDotRadiusData2 = var10.method2367();
      var11.floatValues = RenderShaders.floatValues;
      var10.items.add(var11);
   }

   public static void method2364(Matrix4f var0, float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, ZenithColor var6) {
      Jebriksheet var7 = method2366();
      var7.method2368(var7.items2, var7.items10);
      Jebriksheet.Entry4 var8 = var7.entry2.method2385();
      var8.matrix4f.set(var0);
      var8.floatValue = var1;
      var8.floatValue2 = var2;
      var8.width = var3;
      var8.height = var4;
      var8.proxyDotRadiusData = var5;
      var8.zenithColor = var6;
      var8.floatValues = RenderShaders.floatValues;
      var7.items2.add(var8);
   }

   public static void method2365(Matrix4f var0, float var1, float var2, float var3, float var4, ZenithColor var5) {
      Jebriksheet var6 = method2366();
      var6.method2368(var6.items2, var6.items10);
      Jebriksheet.Entry5 var7 = var6.entry3.method2385();
      var7.matrix4f.set(var0);
      var7.floatValue = var1;
      var7.floatValue2 = var2;
      var7.width = var3;
      var7.height = var4;
      var7.zenithColor = var5;
      var6.items2.add(var7);
   }

   private static Jebriksheet method2366() {
      if (!method2348()) {
         throw new IllegalStateException("HudRenderQueue is not recording");
      } else {
         return INSTANCE;
      }
   }

   private ProxyDotRadiusData method2367() {
      float var1 = InterfaceModule.method6801();
      if (var1 != this.floatValue) {
         this.floatValue = var1;
         this.proxyDotRadiusData = ProxyDotRadiusData.method8069(var1);
      }

      return this.proxyDotRadiusData;
   }

   private void method2368(List<? super HudQueuedDraw> var1, List<Jebriksheet.Entry2> var2) {
      int var3 = var2.size();
      int var4 = this.items9.size();
      if (var3 != 0 || var4 != 0) {
         int var5 = 0;

         while (var5 < var3 && var5 < var4 && var2.get(var5) == this.items9.get(var5)) {
            var5++;
         }

         for (int var6 = var3 - 1; var6 >= var5; var6--) {
            var1.add(this.method2370(((Jebriksheet.Entry2)var2.get(var6)).hudQueuedContext));
            var2.remove(var6);
         }

         for (int var8 = var5; var8 < var4; var8++) {
            Jebriksheet.Entry2 var7 = this.items9.get(var8);
            var1.add(this.method2369(var7));
            var2.add(var7);
         }
      }
   }

   private HudQueuedDraw method2369(Jebriksheet.Entry2 var1) {
      HudQueuedDraw var2 = this.entry.method2385();
      var2.hudQueuedContext = var1.hudQueuedContext;
      var2.type = HudQueuedDraw.Type.SCISSOR_ENABLE;
      var2.matrix4f.set(var1.matrix4f);
      var2.intValue = var1.intValue;
      var2.intValue2 = var1.intValue2;
      var2.intValue3 = var1.intValue3;
      var2.intValue4 = var1.intValue4;
      return var2;
   }

   private HudQueuedDraw method2370(HudQueuedContext var1) {
      HudQueuedDraw var2 = this.entry.method2385();
      var2.hudQueuedContext = var1;
      var2.type = HudQueuedDraw.Type.SCISSOR_DISABLE;
      var2.matrix4f.identity();
      return var2;
   }

   private void method2371() {
      this.method2372(this.items2, this.items10);
      this.method2372(this.items3, this.items11);
      this.method2372(this.items4, this.items12);
      this.method2372(this.items5, this.items13);
   }

   private void method2372(List<? super HudQueuedDraw> var1, List<Jebriksheet.Entry2> var2) {
      for (int var3 = var2.size() - 1; var3 >= 0; var3--) {
         var1.add(this.method2370(((Jebriksheet.Entry2)var2.get(var3)).hudQueuedContext));
      }

      var2.clear();
   }

   public void flush() {
      if (!this.flag2) {
         this.method2371();
         this.flag2 = true;
         HudBatchState.begin();

         try {
            this.method2374(true);
            this.method2375();
            this.method2374(false);
            this.method2376();
            this.method2373();
            this.method2377();
            this.method2378();
            ScissorFlusher.flush();
            this.method2381();
         } finally {
            HudBatchState.end();
            this.flag2 = false;
            this.clear();
         }
      }
   }

   private void method2373() {
      boolean var1 = false;
      BufferBuilder var2 = null;

      for (int var3 = 0; var3 < this.items.size(); var3++) {
         Jebriksheet.Entry3 var4 = this.items.get(var3);
         if (var4.flag3) {
            if (!var1) {
               MsdfRenderer.flushBatch();
               KawaseShaderKey var5 = RenderShaders.kawaseShaderKey4;
               var5.method5165();
               var5.method5166("Smoothness").method7413(0.8F, 1.0F);
               var5.method5166("TextureSize").method7412(1024.0F);
               RenderState.method1890(0, identifier);
               RenderState.method1871();
               RenderState.method1875(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
               var2 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.vertexFormat);
               var1 = true;
            }

            RoundedRectRenderer.method4785(
               var2, var4.matrix4f, var4.floatValue, var4.floatValue2, var4.width, var4.height, var4.proxyDotRadiusData, zenithColor, 0.0F, 0.0F
            );
         }
      }

      if (var1) {
         ImmediateRenderer.method8015(var2.end());
         RenderState.method1873();
         RenderState.method1872();
         RenderState.method1892(0, 0);
      }
   }

   private void clear() {
      this.items.clear();
      this.items2.clear();
      this.items3.clear();
      this.items4.clear();
      this.items5.clear();
      this.items6.clear();
      this.items7.clear();
      this.items9.clear();
      this.items10.clear();
      this.items11.clear();
      this.items12.clear();
      this.items13.clear();
      this.entry.reset();
      this.entry2.reset();
      this.entry3.reset();
      this.entry4.reset();
      this.entry5.reset();
   }

   private void method2374(boolean var1) {
      for (int var2 = 0; var2 < this.items.size(); var2++) {
         Jebriksheet.Entry3 var3 = this.items.get(var2);
         if (var3.flag2 && var3.flag5 == var1) {
            RenderShaders.method5290(var3.matrix4f, var3.floatValue, var3.floatValue2, var3.width, var3.height, var3.intValue, var3.proxyDotRadiusData2);
         }
      }
   }

   private void method2375() {
      this.items7.clear();
      this.items8.clear();

      for (int var1 = 0; var1 < this.items.size(); var1++) {
         Jebriksheet.Entry3 var2 = this.items.get(var1);
         if (var2.flag) {
            if (var2.flag4) {
               this.items8
                  .add(
                     new KawaseBlur.Data2(
                        var2.matrix4f,
                        var2.floatValue,
                        var2.floatValue2,
                        var2.width,
                        var2.height,
                        var2.proxyDotRadiusData,
                        var2.zenithColor,
                        10.0F,
                        0.08F,
                        var2.floatValues
                     )
                  );
            } else if (var2.floatValues != null) {
               float[] var3 = var2.floatValues;
               this.items7
                  .add(
                     new KawaseBlur.Data(
                        var2.matrix4f, var2.floatValue, var2.floatValue2, var2.width, var3[5] - var2.floatValue2, var2.proxyDotRadiusData, var2.zenithColor
                     )
                  );
               this.items7
                  .add(new KawaseBlur.Data(var2.matrix4f, var3[0], var3[1], var3[2], var3[3], ProxyDotRadiusData.method8069(var3[4]), var2.zenithColor));
            } else {
               this.items7
                  .add(
                     new KawaseBlur.Data(var2.matrix4f, var2.floatValue, var2.floatValue2, var2.width, var2.height, var2.proxyDotRadiusData, var2.zenithColor)
                  );
            }
         }
      }

      if (!this.items7.isEmpty()) {
         Zenith.getInstance().getKawaseBlur().method7354(this.items7);
      }

      if (!this.items8.isEmpty()) {
         Zenith.getInstance().getKawaseBlur().method7369(this.items8);
      }
   }

   private void method2376() {
      for (int var1 = 0; var1 < this.items2.size(); var1++) {
         Jebriksheet.Handler var2 = this.items2.get(var1);
         if (var2 instanceof Jebriksheet.Entry4 var3 && var3.floatValues != null) {
            this.roundedRectRenderer.flush();
            RenderShaders.floatValues = var3.floatValues;

            try {
               RenderShaders.method5268(
                  new Matrix4f(var3.matrix4f), var3.floatValue, var3.floatValue2, var3.width, var3.height, var3.proxyDotRadiusData, var3.zenithColor
               );
            } finally {
               RenderShaders.floatValues = null;
            }
         } else if (var2 instanceof Jebriksheet.Entry4 var7) {
            this.roundedRectRenderer.begin();
            this.roundedRectRenderer
               .method4779(var7.matrix4f, var7.floatValue, var7.floatValue2, var7.width, var7.height, var7.proxyDotRadiusData, var7.zenithColor);
         } else if (var2 instanceof Jebriksheet.Entry5 var8) {
            this.roundedRectRenderer.begin();
            this.roundedRectRenderer.method4780(var8.matrix4f, var8.floatValue, var8.floatValue2, var8.width, var8.height, var8.zenithColor);
         } else {
            this.roundedRectRenderer.flush();
            if (var2 instanceof Jebriksheet.Handler2 var9) {
               var9.render();
            }
         }
      }

      this.roundedRectRenderer.flush();
   }

   private void method2377() {
      this.method2379(this.items3);
   }

   private void method2378() {
      this.method2379(this.items4);
   }

   private void method2379(List<Jebriksheet.Handler2> var1) {
      for (int var2 = 0; var2 < var1.size(); var2++) {
         Jebriksheet.Handler2 var3 = (Jebriksheet.Handler2)var1.get(var2);
         if (!(var3 instanceof HudQueuedDraw var4 && this.method2380(var4))) {
            this.roundedRectRenderer.flush();
            var3.render();
         }
      }

      this.roundedRectRenderer.flush();
   }

   private boolean method2380(HudQueuedDraw var1) {
      if (var1.type != HudQueuedDraw.Type.ROUNDED_BORDER_RGBA
         && var1.type != HudQueuedDraw.Type.ROUNDED_CORNER_ONLY
         && var1.type != HudQueuedDraw.Type.ARC_BORDER) {
         return false;
      } else if (var1.type == HudQueuedDraw.Type.ARC_BORDER) {
         this.roundedRectRenderer.begin();
         this.roundedRectRenderer
            .method4783(
               var1.matrix4f,
               var1.floatValue,
               var1.floatValue2,
               var1.floatValue3,
               var1.floatValue4,
               var1.floatValue5,
               var1.floatValue6,
               var1.floatValue7,
               (ZenithColor)var1.obj
            );
         return true;
      } else {
         Matrix4f var2 = var1.matrix4f;
         ProxyDotRadiusData var3 = (ProxyDotRadiusData)var1.obj;
         ZenithColor var4 = (ZenithColor)var1.obj2;
         this.roundedRectRenderer.begin();
         if (var1.type == HudQueuedDraw.Type.ROUNDED_BORDER_RGBA) {
            this.roundedRectRenderer.method4781(var2, var1.floatValue, var1.floatValue2, var1.floatValue3, var1.floatValue4, var1.floatValue5, var3, var4);
         } else {
            this.roundedRectRenderer
               .method4782(var2, var1.floatValue, var1.floatValue2, var1.floatValue3, var1.floatValue4, var1.floatValue5, var3, var4, var1.floatValue6);
         }

         return true;
      }
   }

   private void method2381() {
      for (int var1 = 0; var1 < this.items5.size(); var1++) {
         Jebriksheet.Handler2 var2 = this.items5.get(var1);
         if (flag && method2383(var2)) {
            this.items6.add(var2);
         } else {
            this.method2382();
            var2.render();
         }
      }

      this.method2382();
   }

   private void method2382() {
      int var1 = this.items6.size();
      if (var1 != 0) {
         if (var1 > 1) {
            this.items6.sort(comparator);
         }

         for (int var2 = 0; var2 < var1; var2++) {
            this.items6.get(var2).render();
         }

         this.items6.clear();
      }
   }

   private static boolean method2383(Jebriksheet.Handler2 var0) {
      return var0 instanceof HudQueuedDraw var1 && var1.intValue10 >= 0;
   }

   private static int method2384(Jebriksheet.Handler2 var0) {
      return ((HudQueuedDraw)var0).intValue10;
   }

   private static final class Data implements Jebriksheet.Handler2 {
      private final Runnable runnable;

      public Runnable runnable() {
         return this.runnable;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.runnable, other.runnable);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(runnable);
      }

      @Override
      public String toString() {
         return "Data[" + "runnable=" + this.runnable + "]";
      }

      private Data(Runnable var1) {
         this.runnable = var1;
      }

      @Override
      public void render() {
         this.runnable.run();
      }

      public Runnable getRunnable() {
         return this.runnable;
      }
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

      public T method2385() {
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

   public static final class Entry2 {
      public final Matrix4f matrix4f = new Matrix4f();
      public int intValue;
      public int intValue2;
      public int intValue3;
      public int intValue4;
      public HudQueuedContext hudQueuedContext;

      public void method2386() {
         this.hudQueuedContext = null;
      }
   }

   private static final class Entry3 {
      public final Matrix4f matrix4f = new Matrix4f();
      public float floatValue;
      public float floatValue2;
      public float width;
      public float height;
      public float blurRadius;
      public ProxyDotRadiusData proxyDotRadiusData;
      public ZenithColor zenithColor;
      public boolean flag;
      public boolean flag2;
      public boolean flag3;
      public boolean flag4;
      public boolean flag5;
      public int intValue;
      public ProxyDotRadiusData proxyDotRadiusData2;
      public float[] floatValues;

      public void method2387() {
         this.proxyDotRadiusData = null;
         this.zenithColor = null;
         this.proxyDotRadiusData2 = null;
         this.floatValues = null;
      }
   }

   private static final class Entry4 implements Jebriksheet.Handler {
      public final Matrix4f matrix4f = new Matrix4f();
      public float floatValue;
      public float floatValue2;
      public float width;
      public float height;
      public ProxyDotRadiusData proxyDotRadiusData;
      public ZenithColor zenithColor;
      public float[] floatValues;

      public void method2388() {
         this.proxyDotRadiusData = null;
         this.zenithColor = null;
         this.floatValues = null;
      }
   }

   private static final class Entry5 implements Jebriksheet.Handler {
      public final Matrix4f matrix4f = new Matrix4f();
      public float floatValue;
      public float floatValue2;
      public float width;
      public float height;
      public ZenithColor zenithColor;

      public void method2389() {
         this.zenithColor = null;
      }
   }

   public interface Handler {
   }

   public interface Handler2 extends Jebriksheet.Handler {
      void render();
   }
}

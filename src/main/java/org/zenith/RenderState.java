package org.zenith;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.Arrays;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.zenith.utility.render.TextureFilterUtils;

public final class RenderState {
   private static boolean flag;
   private static SourceFactor sourceFactor = SourceFactor.SRC_ALPHA;
   private static DestFactor destFactor = DestFactor.ONE_MINUS_SRC_ALPHA;
   private static SourceFactor sourceFactor2 = SourceFactor.ONE;
   private static DestFactor destFactor2 = DestFactor.ZERO;
   private static boolean flag2 = true;
   private static DepthTestFunction depthTestFunction = DepthTestFunction.LEQUAL_DEPTH_TEST;
   private static boolean flag3 = true;
   private static boolean flag4;
   private static float floatValue = 1.0F;
   private static float floatValue2 = 1.0F;
   private static float floatValue3 = 1.0F;
   private static float alpha = 1.0F;
   private static final GpuTextureView[] gpuTextureViews = new GpuTextureView[8];
   private static final FilterMode[] filterModes = new FilterMode[8];
   @Nullable
   private static String text;
   private static float floatValue4 = 1.0F;
   private static boolean flag5 = true;
   private static boolean flag6 = true;

   private RenderState() {
   }

   public static void method1871() {
      flag = true;
   }

   public static void method1872() {
      flag = false;
   }

   public static void method1873() {
      method1875(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
   }

   public static void method1874(SourceFactor var0, DestFactor var1) {
      method1875(var0, var1, var0, var1);
   }

   public static void method1875(SourceFactor var0, DestFactor var1, SourceFactor var2, DestFactor var3) {
      sourceFactor = var0;
      destFactor = var1;
      sourceFactor2 = var2;
      destFactor2 = var3;
   }

   public static void method1876() {
      flag2 = true;
   }

   public static void method1877() {
      flag2 = false;
   }

   public static void method1878() {
      depthTestFunction = DepthTestFunction.LEQUAL_DEPTH_TEST;
      flag4 = true;
   }

   public static void method1879() {
      depthTestFunction = DepthTestFunction.NO_DEPTH_TEST;
      flag4 = false;
   }

   public static void method1880(DepthTestFunction var0) {
      depthTestFunction = var0;
      flag4 = var0 != DepthTestFunction.NO_DEPTH_TEST;
   }

   public static boolean isFlag4() {
      return flag4;
   }

   public static void method1881() {
      flag4 = false;
   }

   public static void method1882(boolean var0) {
      flag3 = var0;
   }

   public static void method1883(float var0, float var1, float var2, float var3) {
      floatValue = var0;
      floatValue2 = var1;
      floatValue3 = var2;
      alpha = var3;
   }

   public static void method1884() {
      method1883(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static float[] method1885() {
      return new float[]{floatValue, floatValue2, floatValue3, alpha};
   }

   public static float getFloatValue() {
      return floatValue;
   }

   public static float getFloatValue2() {
      return floatValue2;
   }

   public static float getFloatValue3() {
      return floatValue3;
   }

   public static float getAlpha() {
      return alpha;
   }

   public static boolean method1886() {
      return floatValue == 1.0F && floatValue2 == 1.0F && floatValue3 == 1.0F && alpha == 1.0F;
   }

   public static void method1887(int var0, @Nullable GpuTextureView var1) {
      method1888(var0, var1, FilterMode.NEAREST);
   }

   public static void method1888(int var0, @Nullable GpuTextureView var1, FilterMode var2) {
      if (var0 >= 0 && var0 < gpuTextureViews.length) {
         gpuTextureViews[var0] = var1;
         filterModes[var0] = var2;
      }
   }

   public static void method1889(int var0, @Nullable GpuTextureView var1) {
      method1888(var0, var1, FilterMode.LINEAR);
   }

   public static void method1890(int var0, @Nullable Identifier var1) {
      method1888(var0, TextureFilterUtils.method7269(var1), TextureFilterUtils.method7268(var1));
   }

   public static void method1891(int var0, @Nullable AbstractTexture var1) {
      method1887(var0, TextureFilterUtils.method7270(var1));
   }

   public static void method1892(int var0, int var1) {
      method1887(var0, (GpuTextureView)null);
   }

   public static void method1893(int var0) {
      method1887(var0, (GpuTextureView)null);
   }

   @Nullable
   public static GpuTextureView method1894(int var0) {
      return var0 >= 0 && var0 < gpuTextureViews.length ? gpuTextureViews[var0] : null;
   }

   public static FilterMode method1895(int var0) {
      FilterMode var1 = var0 >= 0 && var0 < filterModes.length ? filterModes[var0] : null;
      return var1 != null ? var1 : FilterMode.NEAREST;
   }

   public static GpuSampler method1896(int var0) {
      FilterMode var1 = method1895(var0);
      AddressMode var2 = TextureFilterUtils.method7271(method1894(var0));
      return RenderSystem.getSamplerCache().get(var2, var2, var1, var1, false);
   }

   public static void method1897() {
      Arrays.fill(gpuTextureViews, null);
      Arrays.fill(filterModes, null);
   }

   public static void method1898(String var0) {
      text = var0;
   }

   @Nullable
   public static String getText() {
      return text;
   }

   public static void method1899() {
      text = null;
   }

   public static void method1900(int var0, int var1, int var2, int var3) {
      RenderSystem.enableScissorForRenderTypeDraws(var0, var1, var2, var3);
   }

   public static void method1901() {
      RenderSystem.disableScissorForRenderTypeDraws();
   }

   public static void method1902(float var0) {
      floatValue4 = var0;
   }

   public static float getFloatValue4() {
      return floatValue4;
   }

   public static void method1903(boolean var0, boolean var1, boolean var2, boolean var3) {
      flag5 = var0 || var1 || var2;
      flag6 = var3;
   }

   public static boolean isFlag5() {
      return flag5;
   }

   public static boolean isFlag6() {
      return flag6;
   }

   public static RenderState.Data2 method1904() {
      return new RenderState.Data2(flag, sourceFactor, destFactor, sourceFactor2, destFactor2, flag2, depthTestFunction, flag3);
   }

   public static void method1905(RenderState.Data2 var0) {
      flag = var0.isFlag();
      sourceFactor = var0.getSourceFactor();
      destFactor = var0.getDestFactor();
      sourceFactor2 = var0.getSourceFactor2();
      destFactor2 = var0.getDestFactor2();
      flag2 = var0.isFlag2();
      depthTestFunction = var0.getDepthTestFunction();
      flag3 = var0.isFlag3();
   }

   public static RenderState.Data method1906() {
      return new RenderState.Data(method1904(), flag4, method1885(), (GpuTextureView[])gpuTextureViews.clone(), (FilterMode[])filterModes.clone());
   }

   public static void method1907(RenderState.Data var0) {
      method1905(var0.getData2());
      flag4 = var0.isFlag();
      method1883(var0.getFloatValues()[0], var0.getFloatValues()[1], var0.getFloatValues()[2], var0.getFloatValues()[3]);
      System.arraycopy(var0.getGpuTextureViews(), 0, gpuTextureViews, 0, gpuTextureViews.length);
      System.arraycopy(var0.getFilterModes(), 0, filterModes, 0, filterModes.length);
   }

   public static void reset() {
      flag4 = false;
      flag = false;
      flag2 = true;
      depthTestFunction = DepthTestFunction.LEQUAL_DEPTH_TEST;
      flag3 = true;
      method1873();
      method1884();
      method1897();
   }

   public static final class Data {
      private final RenderState.Data2 data2;
      private final boolean flag;
      private final float[] floatValues;
      private final GpuTextureView[] gpuTextureViews;
      private final FilterMode[] filterModes;

      public RenderState.Data2 data2() {
         return this.data2;
      }

      public boolean flag() {
         return this.flag;
      }

      public float[] floatValues() {
         return this.floatValues;
      }

      public GpuTextureView[] gpuTextureViews() {
         return this.gpuTextureViews;
      }

      public FilterMode[] filterModes() {
         return this.filterModes;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.data2, other.data2) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.gpuTextureViews, other.gpuTextureViews) && java.util.Objects.equals(this.filterModes, other.filterModes);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(data2, flag, floatValues, gpuTextureViews, filterModes);
      }

      @Override
      public String toString() {
         return "Data[" + "data2=" + this.data2 + ", " + "flag=" + this.flag + ", " + "floatValues=" + this.floatValues + ", " + "gpuTextureViews=" + this.gpuTextureViews + ", " + "filterModes=" + this.filterModes + "]";
      }

      public Data(RenderState.Data2 var1, boolean var2, float[] var3, GpuTextureView[] var4, FilterMode[] var5) {
         this.data2 = var1;
         this.flag = var2;
         this.floatValues = var3;
         this.gpuTextureViews = var4;
         this.filterModes = var5;
      }

      public RenderState.Data2 getData2() {
         return this.data2;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }

      public GpuTextureView[] getGpuTextureViews() {
         return this.gpuTextureViews;
      }

      public FilterMode[] getFilterModes() {
         return this.filterModes;
      }
   }

   public static final class Data2 {
      private final boolean flag;
      private final SourceFactor sourceFactor;
      private final DestFactor destFactor;
      private final SourceFactor sourceFactor2;
      private final DestFactor destFactor2;
      private final boolean flag2;
      private final DepthTestFunction depthTestFunction;
      private final boolean flag3;

      public boolean flag() {
         return this.flag;
      }

      public SourceFactor sourceFactor() {
         return this.sourceFactor;
      }

      public DestFactor destFactor() {
         return this.destFactor;
      }

      public SourceFactor sourceFactor2() {
         return this.sourceFactor2;
      }

      public DestFactor destFactor2() {
         return this.destFactor2;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public DepthTestFunction depthTestFunction() {
         return this.depthTestFunction;
      }

      public boolean flag3() {
         return this.flag3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.sourceFactor, other.sourceFactor) && java.util.Objects.equals(this.destFactor, other.destFactor) && java.util.Objects.equals(this.sourceFactor2, other.sourceFactor2) && java.util.Objects.equals(this.destFactor2, other.destFactor2) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.depthTestFunction, other.depthTestFunction) && java.util.Objects.equals(this.flag3, other.flag3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, sourceFactor, destFactor, sourceFactor2, destFactor2, flag2, depthTestFunction, flag3);
      }

      @Override
      public String toString() {
         return "Data2[" + "flag=" + this.flag + ", " + "sourceFactor=" + this.sourceFactor + ", " + "destFactor=" + this.destFactor + ", " + "sourceFactor2=" + this.sourceFactor2 + ", " + "destFactor2=" + this.destFactor2 + ", " + "flag2=" + this.flag2 + ", " + "depthTestFunction=" + this.depthTestFunction + ", " + "flag3=" + this.flag3 + "]";
      }

      public Data2(boolean var1, SourceFactor var2, DestFactor var3, SourceFactor var4, DestFactor var5, boolean var6, DepthTestFunction var7, boolean var8) {
         this.flag = var1;
         this.sourceFactor = var2;
         this.destFactor = var3;
         this.sourceFactor2 = var4;
         this.destFactor2 = var5;
         this.flag2 = var6;
         this.depthTestFunction = var7;
         this.flag3 = var8;
      }

      public BlendFunction method1908() {
         return this.flag ? new BlendFunction(this.sourceFactor, this.destFactor, this.sourceFactor2, this.destFactor2) : null;
      }

      public RenderState.Data2 method1909() {
         return new RenderState.Data2(
            this.flag, this.sourceFactor, this.destFactor, this.sourceFactor2, this.destFactor2, false, DepthTestFunction.NO_DEPTH_TEST, false
         );
      }

      public RenderState.Data2 method1910() {
         return new RenderState.Data2(
            this.flag, this.sourceFactor, this.destFactor, this.sourceFactor2, this.destFactor2, false, this.depthTestFunction, this.flag3
         );
      }

      public boolean isFlag() {
         return this.flag;
      }

      public SourceFactor getSourceFactor() {
         return this.sourceFactor;
      }

      public DestFactor getDestFactor() {
         return this.destFactor;
      }

      public SourceFactor getSourceFactor2() {
         return this.sourceFactor2;
      }

      public DestFactor getDestFactor2() {
         return this.destFactor2;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public DepthTestFunction getDepthTestFunction() {
         return this.depthTestFunction;
      }

      public boolean isFlag3() {
         return this.flag3;
      }
   }
}

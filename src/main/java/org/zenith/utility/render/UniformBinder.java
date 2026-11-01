package org.zenith.utility.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public final class UniformBinder {
   private static final Map<RenderPipeline, UniformBinder.Data> items = new IdentityHashMap<>();
   private static final Map<RenderPipeline, GpuBufferSlice> items2 = new IdentityHashMap<>();

   private UniformBinder() {
   }

   public static void method5478(RenderPipeline var0, ShaderDefinition var1, Runnable var2) {
      method5479(var0, var1, var2, null);
   }

   public static void method5479(@Nullable RenderPipeline var0, ShaderDefinition var1, Runnable var2, @Nullable Supplier<GpuTextureView> var3) {
      method5480(var0, var1, var2, var3, null);
   }

   public static void method5480(
      @Nullable RenderPipeline var0, ShaderDefinition var1, Runnable var2, @Nullable Supplier<GpuTextureView> var3, @Nullable BooleanSupplier var4
   ) {
      if (var0 != null) {
         items.put(var0, new UniformBinder.Data(var1, var2, var3, var4));
      }
   }

   public static void method5481() {
      if (!items.isEmpty()) {
         for (Entry var1 : items.entrySet()) {
            UniformBinder.Data var2 = (UniformBinder.Data)var1.getValue();
            if (var2.getBooleanSupplier() != null && !var2.getBooleanSupplier().getAsBoolean()) {
               items2.remove(var1.getKey());
            } else {
               var2.getRunnable().run();
               GpuBufferSlice var3 = var2.getShaderDefinition().method5562();
               if (var3 != null) {
                  items2.put((RenderPipeline)var1.getKey(), var3);
               }
            }
         }
      }
   }

   public static void method5482(RenderPass var0, RenderPipeline var1) {
      GpuBufferSlice var2 = items2.get(var1);
      if (var2 != null) {
         var0.setUniform("ZenithData", var2);
      }

      UniformBinder.Data var3 = items.get(var1);
      if (var3 != null && var3.getSupplier() != null && var1.getSamplers().contains("Sampler0")) {
         GpuTextureView var4 = var3.getSupplier().get();
         if (var4 != null) {
            var0.bindTexture("Sampler0", var4, RenderSystem.getSamplerCache().get(FilterMode.LINEAR));
         }
      }
   }

   private static final class Data {
      private final ShaderDefinition shaderDefinition;
      private final Runnable runnable;
      @Nullable
      private final Supplier<GpuTextureView> supplier;
      @Nullable
      private final BooleanSupplier booleanSupplier;

      public ShaderDefinition shaderDefinition() {
         return this.shaderDefinition;
      }

      public Runnable runnable() {
         return this.runnable;
      }

      public Supplier<GpuTextureView> supplier() {
         return this.supplier;
      }

      public BooleanSupplier booleanSupplier() {
         return this.booleanSupplier;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.shaderDefinition, other.shaderDefinition) && java.util.Objects.equals(this.runnable, other.runnable) && java.util.Objects.equals(this.supplier, other.supplier) && java.util.Objects.equals(this.booleanSupplier, other.booleanSupplier);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(shaderDefinition, runnable, supplier, booleanSupplier);
      }

      @Override
      public String toString() {
         return "Data[" + "shaderDefinition=" + this.shaderDefinition + ", " + "runnable=" + this.runnable + ", " + "supplier=" + this.supplier + ", " + "booleanSupplier=" + this.booleanSupplier + "]";
      }

      private Data(ShaderDefinition var1, Runnable var2, @Nullable Supplier<GpuTextureView> var3, @Nullable BooleanSupplier var4) {
         this.shaderDefinition = var1;
         this.runnable = var2;
         this.supplier = var3;
         this.booleanSupplier = var4;
      }

      public ShaderDefinition getShaderDefinition() {
         return this.shaderDefinition;
      }

      public Runnable getRunnable() {
         return this.runnable;
      }

      @Nullable
      public Supplier<GpuTextureView> getSupplier() {
         return this.supplier;
      }

      @Nullable
      public BooleanSupplier getBooleanSupplier() {
         return this.booleanSupplier;
      }
   }
}

package org.zenith.utility.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Builder;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.UniformType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.zenith.RenderState;

public final class LightUtil {
   public static final String text = "core/position_color";
   public static final String text2 = "core/position_tex_color";
   public static final String text3 = "core/rendertype_lines";
   private static final Map<LightUtil.Data, RenderPipeline> items = new ConcurrentHashMap<>();
   private static final String[] texts = new String[]{"Sampler0"};
   private static final String[] texts2 = new String[0];
   private static final String text4 = "Sampler0";
   private static final String text5 = "";
   public static final String[] texts3 = new String[]{"Sampler0", "Sampler1", "Sampler2", "Sampler3", "Sampler4", "Sampler5", "Sampler6", "Sampler7"};

   private LightUtil() {
   }

   public static RenderPipeline method6706(String var0, VertexFormat var1, DrawMode var2, RenderState.Data2 var3) {
      boolean var4 = method6718(var1);
      return method6708(var0, var1, var2, var3, var4 ? texts : texts2, var4 ? "Sampler0" : "");
   }

   public static RenderPipeline method6707(String var0, VertexFormat var1, DrawMode var2, RenderState.Data2 var3, String[] var4) {
      return method6708(var0, var1, var2, var3, var4, String.join(",", var4));
   }

   private static RenderPipeline method6708(String var0, VertexFormat var1, DrawMode var2, RenderState.Data2 var3, String[] var4, String var5) {
      LightUtil.Data var6 = new LightUtil.Data(var0, var1, var2, var3, var5, false);
      RenderPipeline var7 = items.get(var6);
      return var7 != null ? var7 : items.computeIfAbsent(var6, var1x -> method6711(var1x, var4, null, false));
   }

   @Nullable
   public static RenderPipeline method6709(Identifier var0, VertexFormat var1, DrawMode var2, RenderState.Data2 var3, String[] var4, boolean var5) {
      return method6710(var0, var0.toString(), String.join(",", var4), var1, var2, var3, var4, var5);
   }

   @Nullable
   public static RenderPipeline method6710(
      Identifier var0, String var1, String var2, VertexFormat var3, DrawMode var4, RenderState.Data2 var5, String[] var6, boolean var7
   ) {
      LightUtil.Data var8 = new LightUtil.Data(var1, var3, var4, var5, var2, var7);
      RenderPipeline var9 = items.get(var8);
      if (var9 != null) {
         return var9;
      } else if (!method6712(var0)) {
         return null;
      } else {
         RenderPipeline var10 = method6711(var8, var6, var0, var7);
         items.put(var8, var10);
         return var10;
      }
   }

   private static RenderPipeline method6711(LightUtil.Data var0, String[] var1, Identifier var2, boolean var3) {
      Identifier var4;
      Identifier var5;
      if (var2 != null) {
         String var6 = var2.getPath();
         int var7 = var6.lastIndexOf(47);
         var6 = var7 < 0 ? var6 : var6.substring(0, var7);
         var5 = Identifier.of(var2.getNamespace(), var6 + "/fragment");
         Identifier var8 = Identifier.of(var2.getNamespace(), var6 + "/vertex");
         boolean var9 = var0.getVertexFormat().contains(VertexFormatElement.UV0);
         var4 = method6715(var8, ".vsh") ? var8 : Identifier.of(var2.getNamespace(), "core/" + (var9 ? "position_texture_color" : "position_color"));
      } else {
         var4 = Identifier.ofVanilla(var0.getText());
         var5 = var4;
      }

      Builder var12 = RenderPipeline.builder()
         .withLocation(Identifier.of("zenith", method6716(var0)))
         .withVertexShader(var4)
         .withFragmentShader(var5)
         .withVertexFormat(var0.getVertexFormat(), var0.getDrawMode())
         .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
         .withUniform("Projection", UniformType.UNIFORM_BUFFER)
         .withCull(var0.getData2().isFlag2())
         .withDepthTestFunction(var0.getData2().getDepthTestFunction())
         .withDepthWrite(var0.getData2().isFlag3());

      for (String var10 : var1) {
         var12.withSampler(var10);
      }

      if (method6713(var4, var5, "minecraft:fog.glsl")) {
         var12.withUniform("Fog", UniformType.UNIFORM_BUFFER);
      }

      if (method6713(var4, var5, "minecraft:light.glsl")) {
         var12.withUniform("Lighting", UniformType.UNIFORM_BUFFER);
      }

      if (var3) {
         var12.withUniform("ZenithData", UniformType.UNIFORM_BUFFER);
      }

      BlendFunction var14 = var0.getData2().method1908();
      if (var14 != null) {
         var12.withBlend(var14);
      } else {
         var12.withoutBlend();
      }

      return var12.build();
   }

   public static void invalidate() {
      items.clear();
   }

   public static boolean method6712(Identifier var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1 != null && var1.getShaderLoader() != null) {
         String var2 = var0.getPath();
         int var3 = var2.lastIndexOf(47);
         var2 = var3 < 0 ? var2 : var2.substring(0, var3);
         Identifier var4 = Identifier.of(var0.getNamespace(), var2 + "/fragment");
         return var1.getShaderLoader().getSource(var4, ShaderType.FRAGMENT) != null;
      } else {
         return false;
      }
   }

   private static boolean method6713(Identifier var0, Identifier var1, String var2) {
      return method6714(var0, ShaderType.VERTEX).contains(var2) || method6714(var1, ShaderType.FRAGMENT).contains(var2);
   }

   private static String method6714(Identifier var0, ShaderType var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      if (var2 != null && var2.getShaderLoader() != null) {
         String var3 = var2.getShaderLoader().getSource(var0, var1);
         return var3 == null ? "" : var3;
      } else {
         return "";
      }
   }

   private static boolean method6715(Identifier var0, String var1) {
      MinecraftClient var2 = MinecraftClient.getInstance();
      return var2 != null && var2.getResourceManager() != null
         ? var2.getResourceManager().getResource(Identifier.of(var0.getNamespace(), "shaders/" + var0.getPath() + var1)).isPresent()
         : false;
   }

   private static String method6716(LightUtil.Data var0) {
      String var1 = var0.getText().replace(':', '_').replace('/', '_').toLowerCase(Locale.ROOT);
      return "pipeline/" + var1 + "_" + Integer.toHexString(var0.hashCode() & 2147483647);
   }

   public static String[] method6717(VertexFormat var0) {
      return method6718(var0) ? new String[]{"Sampler0"} : new String[0];
   }

   private static boolean method6718(VertexFormat var0) {
      return var0 == net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR
         || var0 == net.minecraft.client.render.VertexFormats.POSITION_TEXTURE
         || var0 == net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR_LIGHT;
   }

   public static String method6719(VertexFormat var0, DrawMode var1) {
      if (var1 == DrawMode.LINES || var1 == DrawMode.DEBUG_LINE_STRIP) {
         return var0 == net.minecraft.client.render.VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH ? "core/rendertype_lines" : "core/position_color";
      } else {
         return var0 != net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR && var0 != net.minecraft.client.render.VertexFormats.POSITION_TEXTURE
            ? "core/position_color"
            : "core/position_tex_color";
      }
   }

   private static final class Data {
      private final String text;
      private final VertexFormat vertexFormat;
      private final DrawMode drawMode;
      private final RenderState.Data2 data2;
      private final String text2;
      private final boolean flag;

      public String text() {
         return this.text;
      }

      public VertexFormat vertexFormat() {
         return this.vertexFormat;
      }

      public DrawMode drawMode() {
         return this.drawMode;
      }

      public RenderState.Data2 data2() {
         return this.data2;
      }

      public String text2() {
         return this.text2;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.vertexFormat, other.vertexFormat) && java.util.Objects.equals(this.drawMode, other.drawMode) && java.util.Objects.equals(this.data2, other.data2) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, vertexFormat, drawMode, data2, text2, flag);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "vertexFormat=" + this.vertexFormat + ", " + "drawMode=" + this.drawMode + ", " + "data2=" + this.data2 + ", " + "text2=" + this.text2 + ", " + "flag=" + this.flag + "]";
      }

      private Data(String var1, VertexFormat var2, DrawMode var3, RenderState.Data2 var4, String var5, boolean var6) {
         this.text = var1;
         this.vertexFormat = var2;
         this.drawMode = var3;
         this.data2 = var4;
         this.text2 = var5;
         this.flag = var6;
      }

      public String getText() {
         return this.text;
      }

      public VertexFormat getVertexFormat() {
         return this.vertexFormat;
      }

      public DrawMode getDrawMode() {
         return this.drawMode;
      }

      public RenderState.Data2 getData2() {
         return this.data2;
      }

      public String getText2() {
         return this.text2;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }
}

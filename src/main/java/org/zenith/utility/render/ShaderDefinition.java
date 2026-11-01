package org.zenith.utility.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.DynamicUniformStorage;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.zenith.RenderState;
import org.zenith.utility.misc.ShaderUniform;

public abstract class ShaderDefinition {
   private static final Map<String, Integer> items = Map.of(
      "float", 1, "int", 1, "vec2", 2, "ivec2", 2, "vec3", 3, "ivec3", 3, "vec4", 4, "ivec4", 4, "mat4", 16
   );
   private static final Set<String> items2 = Set.of("ModelViewMat", "ProjMat", "ColorModulator", "TextureMat", "ModelOffset");
   private final Identifier identifier;
   private final VertexFormat vertexFormat;
   private final Map<String, float[]> items3 = new HashMap<>();
   private final Map<String, ShaderUniform> items4 = new HashMap<>();
   private final String[] texts;
   private Map<String, Integer> items5 = new LinkedHashMap<>();
   private boolean flag;
   private final Set<String> items6 = new HashSet<>();
   private int[] intValues = new int[0];
   private int[] intValues2 = new int[0];
   private float[][] floatValues = new float[0][];
   private Map<String, Integer> items7 = new HashMap<>();
   private float[] floatValues2 = new float[0];
   private int intValue;
   @Nullable
   private UniformStorage.Entry entry;
   private int intValue2 = -1;
   private final String text;
   private String text2;
   @Nullable
   private DynamicUniformStorage<UniformStorage.Entry> dynamicUniformStorage;
   private int intValue3;
   @Nullable
   private String[] texts2;
   @Nullable
   public String text3;

   public ShaderDefinition(Identifier var1, VertexFormat var2, String[] var3) {
      this.identifier = var1;
      this.vertexFormat = var2;
      this.texts = var3;
      this.text = var1.toString();
      this.text2 = String.join(",", var3);
   }

   public Identifier getIdentifier() {
      return this.identifier;
   }

   public VertexFormat getVertexFormat() {
      return this.vertexFormat;
   }

   public ShaderUniform method5166(String var1) {
      return this.items4.computeIfAbsent(var1, var1x -> new ShaderUniform(this, var1x));
   }

   public void method5559(String var1, float[] var2) {
      this.items3.put(var1, var2);
      Integer var3 = this.items7.get(var1);
      if (var3 != null) {
         this.floatValues[var3] = var2;
      }
   }

   public boolean method5560(String var1) {
      this.method5567();
      return this.items5.containsKey(var1);
   }

   public RenderPipeline method5561(VertexFormat var1, DrawMode var2, RenderState.Data2 var3) {
      this.method5567();
      return LightUtil.method6710(
         this.identifier, this.text, this.text2, var1, var2, var3, this.texts2 != null ? this.texts2 : this.texts, !this.items5.isEmpty()
      );
   }

   @Nullable
   public GpuBufferSlice method5562() {
      this.method5567();
      if (this.items5.isEmpty()) {
         return null;
      } else {
         if (this.items3.size() != this.intValue2) {
            this.method5563();
            this.intValue2 = this.items3.size();
         }

         int[] var1 = this.intValues;
         float[] var2 = this.floatValues2;

         for (int var3 = 0; var3 < var1.length; var3++) {
            float[] var4 = this.floatValues[var3];
            int var5 = var1[var3];
            int var6 = this.intValues2[var3];
            int var7 = var4 == null ? 0 : Math.min(var4.length, var5);
            if (var7 > 0) {
               System.arraycopy(var4, 0, var2, var6, var7);
            }

            for (int var8 = var7; var8 < var5; var8++) {
               var2[var6 + var8] = 0.0F;
            }
         }

         UniformStorage.Entry var9 = this.entry;
         if (var9 == null || !var9.method1277(var1, var2)) {
            var9 = new UniformStorage.Entry((float[])var2.clone(), var1);
            this.entry = var9;
         }

         return this.method5565().write(var9);
      }
   }

   private void method5563() {
      for (String var2 : this.items3.keySet()) {
         if (!this.items5.containsKey(var2) && this.items6.add(var2)) {
            LogUtils.getLogger()
               .warn("Zenith: в блоке {} шейдера {} нет поля {} — значение никуда не попадёт", new Object[]{"ZenithData", this.identifier, var2});
         }
      }
   }

   public void method5564(RenderPass var1, @Nullable GpuBufferSlice var2) {
      if (var2 != null) {
         var1.setUniform("ZenithData", var2);
      }
   }

   private DynamicUniformStorage<UniformStorage.Entry> method5565() {
      int var1 = this.intValue;
      if (this.dynamicUniformStorage == null || this.intValue3 != var1) {
         if (this.dynamicUniformStorage != null) {
            UniformStorage.method1275(this.dynamicUniformStorage);
         }

         this.intValue3 = var1;
         this.dynamicUniformStorage = UniformStorage.method1274("zenith ZenithData " + this.identifier, var1);
      }

      return this.dynamicUniformStorage;
   }

   public void method5566() {
      this.flag = false;
      this.method5567();
   }

   private void method5567() {
      if (!this.flag) {
         MinecraftClient var1 = MinecraftClient.getInstance();
         if (var1 != null && var1.getShaderLoader() != null) {
            this.items5 = new LinkedHashMap<>();
            this.texts2 = null;
            this.method5573(var1);
            this.text2 = String.join(",", this.texts2 != null ? this.texts2 : this.texts);
            this.flag = this.method5569(var1);
            this.method5568();
         }
      }
   }

   private void method5568() {
      int var1 = this.items5.size();
      int[] var2 = new int[var1];
      int[] var3 = new int[var1];
      float[][] var4 = new float[var1][];
      HashMap var5 = new HashMap();
      int var6 = 0;
      int var7 = 0;

      for (Entry var9 : this.items5.entrySet()) {
         var2[var7] = (Integer)var9.getValue();
         var3[var7] = var6;
         var4[var7] = this.items3.get(var9.getKey());
         var5.put((String)var9.getKey(), var7);
         var6 += var2[var7];
         var7++;
      }

      this.intValues = var2;
      this.intValues2 = var3;
      this.floatValues = var4;
      this.items7 = var5;
      this.floatValues2 = new float[var6];
      this.intValue = var1 == 0 ? 0 : UniformStorage.method1273(var2);
      this.entry = null;
      this.intValue2 = -1;
   }

   private boolean method5569(MinecraftClient var1) {
      String var2 = this.method5570(var1, "fragment", ShaderType.FRAGMENT);
      var var3 = method5571(var2);
      String var4 = null;
      if (var3.isEmpty()) {
         var4 = this.method5570(var1, "vertex", ShaderType.VERTEX);
         var3 = method5571(var4);
      }

      this.items5.putAll(var3);
      return var2 != null || var4 != null;
   }

   @Nullable
   private String method5570(MinecraftClient var1, String var2, ShaderType var3) {
      String var4 = this.identifier.getPath();
      int var5 = var4.lastIndexOf(47);
      String var6 = var5 < 0 ? var4 : var4.substring(0, var5);
      return var1.getShaderLoader().getSource(Identifier.of(this.identifier.getNamespace(), var6 + "/" + var2), var3);
   }

   private static Map<String, Integer> method5571(@Nullable String var0) {
      LinkedHashMap var1 = new LinkedHashMap();
      if (var0 == null) {
         return var1;
      } else {
         int var2 = var0.indexOf("uniform ZenithData");
         if (var2 < 0) {
            return var1;
         } else {
            int var3 = var0.indexOf(123, var2);
            int var4 = var0.indexOf(125, var3);
            if (var3 >= 0 && var4 >= 0) {
               for (String var8 : var0.substring(var3 + 1, var4).split(";")) {
                  ArrayList var9 = new ArrayList();
                  StringTokenizer var10 = new StringTokenizer(var8);

                  while (var10.hasMoreTokens()) {
                     var9.add(var10.nextToken());
                  }

                  if (var9.size() >= 2) {
                     Integer var11 = items.get(var9.get(var9.size() - 2));
                     if (var11 != null) {
                        var1.put((String)var9.get(var9.size() - 1), var11);
                     }
                  }
               }

               return method5572(var0, var1);
            } else {
               return var1;
            }
         }
      }
   }

   private static Map<String, Integer> method5572(String var0, Map<String, Integer> var1) {
      LinkedHashMap var2 = new LinkedHashMap();

      for (Entry var4 : var1.entrySet()) {
         String var5 = (String)var4.getKey();

         for (String var7 : var0.lines().toList()) {
            String var8 = var7.trim();
            if (var8.startsWith("#define ")) {
               StringTokenizer var9 = new StringTokenizer(var8);
               var9.nextToken();
               if (var9.countTokens() >= 2) {
                  String var10 = var9.nextToken();
                  String var11 = var9.nextToken();
                  if (var11.equals(var5 + ".xyz") || var11.equals(var5 + ".rgb")) {
                     var5 = var10;
                     break;
                  }
               }
            }
         }

         var2.put(var5, (Integer)var4.getValue());
      }

      return var2;
   }

   private void method5573(MinecraftClient var1) {
      if (var1.getResourceManager() != null) {
         Identifier var2 = Identifier.of(this.identifier.getNamespace(), "shaders/" + this.identifier.getPath() + ".json");
         var var3 = var1.getResourceManager().getResource(var2);
         if (!var3.isEmpty()) {
            try {
               try (BufferedReader var4 = ((Resource)var3.get()).getReader()) {
                  JsonObject var5 = JsonParser.parseReader(var4).getAsJsonObject();
                  JsonArray var6 = var5.getAsJsonArray("samplers");
                  if (var6 != null) {
                     String[] var7 = new String[var6.size()];

                     for (int var8 = 0; var8 < var7.length; var8++) {
                        var7[var8] = var6.get(var8).getAsJsonObject().get("name").getAsString();
                     }

                     this.texts2 = var7;
                     return;
                  }
               }
            } catch (Exception var11) {
               this.text3 = "не удалось прочитать сэмплеры " + var2 + ": " + var11.getMessage();
            }
         }
      }
   }
}

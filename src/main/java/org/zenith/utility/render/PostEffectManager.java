package org.zenith.utility.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.GpuBuffer.MappedView;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.BufferedReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.zenith.utility.mixin.accessors.PostEffectPassAccessor;
import org.zenith.utility.mixin.accessors.PostEffectProcessorAccessor;

public final class PostEffectManager {
   public static final String text = "ZenithPost";
   private static final Map<Identifier, List<List<PostEffectManager.Data>>> items = new HashMap<>();
   private static final Map<PostEffectPass, MappableRingBuffer> items2 = new IdentityHashMap<>();

   private PostEffectManager() {
   }

   public static void method2819(PostEffectProcessor var0, Identifier var1, PostEffectManager.Entry var2) {
      if (var0 != null && !var2.isEmpty()) {
         var var3 = method2822(var1);
         if (!var3.isEmpty()) {
            List var4 = ((PostEffectProcessorAccessor)(Object)var0).getPasses();
            CommandEncoder var5 = RenderSystem.getDevice().createCommandEncoder();

            for (int var6 = 0; var6 < var4.size() && var6 < var3.size(); var6++) {
               List<PostEffectManager.Data> var7 = (List)var3.get(var6);
               if (!var7.isEmpty()) {
                  PostEffectPass var8 = (PostEffectPass)var4.get(var6);
                  Map var9 = ((PostEffectPassAccessor)(Object)var8).zenith$uniformBuffers();
                  GpuBuffer var10 = (GpuBuffer)var9.get("ZenithPost");
                  if (var10 != null) {
                     GpuBuffer var11 = method2820(var8, var10).getBlocking();
                     var9.put("ZenithPost", var11);

                     try (MappedView var12 = var5.mapBuffer(var11, false, true)) {
                        ByteBuffer var13 = var12.data();
                        Std140Builder var14 = Std140Builder.intoBuffer(var13);

                        for (Object var16_o : (Iterable<?>)(Object)(var7)) {
                           PostEffectManager.Data var16 = (PostEffectManager.Data)var16_o;
                           method2821(var14, var16.intValue, var2.items.getOrDefault(var16.text, var16.floatValues));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static MappableRingBuffer method2820(PostEffectPass var0, GpuBuffer var1) {
      MappableRingBuffer var2 = items2.get(var0);
      if (var2 == null) {
         var2 = new MappableRingBuffer(() -> "zenith ZenithPost", 130, (int)var1.size());
         items2.put(var0, var2);
         var1.close();
      } else {
         var2.rotate();
      }

      return var2;
   }

   private static void method2821(Std140Builder var0, int var1, float[] var2) {
      float var3 = var2.length > 0 ? var2[0] : 0.0F;
      float var4 = var2.length > 1 ? var2[1] : 0.0F;
      float var5 = var2.length > 2 ? var2[2] : 0.0F;
      float var6 = var2.length > 3 ? var2[3] : 0.0F;
      switch (var1) {
         case 1:
            var0.putFloat(var3);
            break;
         case 2:
            var0.putVec2(var3, var4);
            break;
         case 3:
            var0.putVec3(var3, var4, var5);
            break;
         default:
            var0.putVec4(var3, var4, var5, var6);
      }
   }

   private static List<List<PostEffectManager.Data>> method2822(Identifier var0) {
      var var1 = items.get(var0);
      if (var1 != null) {
         return var1;
      } else {
         var var2 = method2824(var0);
         items.put(var0, var2);
         return var2;
      }
   }

   public static void method2823() {
      items.clear();

      for (MappableRingBuffer var1 : items2.values()) {
         var1.close();
      }

      items2.clear();
   }

   private static List<List<PostEffectManager.Data>> method2824(Identifier var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1 != null && var1.getResourceManager() != null) {
         Identifier var2 = Identifier.of(var0.getNamespace(), "post_effect/" + var0.getPath() + ".json");
         var var3 = var1.getResourceManager().getResource(var2);
         if (var3.isEmpty()) {
            return List.of();
         } else {
            try {
               ArrayList var13;
               try (BufferedReader var4 = ((Resource)var3.get()).getReader()) {
                  JsonObject var5 = JsonParser.parseReader(var4).getAsJsonObject();
                  JsonArray var6 = var5.getAsJsonArray("passes");
                  if (var6 == null) {
                     return List.of();
                  }

                  var var7 = new ArrayList<>(var6.size());

                  for (int var8 = 0; var8 < var6.size(); var8++) {
                     var7.add(method2825(var6.get(var8).getAsJsonObject()));
                  }

                  var13 = var7;
               }

               return var13;
            } catch (Exception var11) {
               return List.of();
            }
         }
      } else {
         return List.of();
      }
   }

   private static List<PostEffectManager.Data> method2825(JsonObject var0) {
      JsonObject var1 = var0.getAsJsonObject("uniforms");
      if (var1 == null) {
         return List.of();
      } else {
         JsonArray var2 = var1.getAsJsonArray("ZenithPost");
         if (var2 == null) {
            return List.of();
         } else {
            List<Data> var3 = new ArrayList<>(var2.size());

            for (int var4 = 0; var4 < var2.size(); var4++) {
               JsonObject var5 = var2.get(var4).getAsJsonObject();
               String var6 = var5.get("name").getAsString();
               int var7 = method2826(var5.get("type").getAsString());
               var3.add(new PostEffectManager.Data(var6, var7, method2827(var5.get("value"), var7)));
            }

            return var3;
         }
      }
   }

   private static int method2826(String var0) {
      return switch (var0) {
         case "vec2", "ivec2" -> 2;
         case "vec3", "ivec3" -> 3;
         case "vec4", "ivec4" -> 4;
         default -> 1;
      };
   }

   private static float[] method2827(JsonElement var0, int var1) {
      float[] var2 = new float[var1];
      if (var0 == null) {
         return var2;
      } else {
         if (var0.isJsonArray()) {
            JsonArray var3 = var0.getAsJsonArray();

            for (int var4 = 0; var4 < var1 && var4 < var3.size(); var4++) {
               var2[var4] = var3.get(var4).getAsFloat();
            }
         } else {
            var2[0] = var0.getAsFloat();
         }

         return var2;
      }
   }

   private static final class Data {
      private final String text;
      private final int intValue;
      private final float[] floatValues;

      public String text() {
         return this.text;
      }

      public int intValue() {
         return this.intValue;
      }

      public float[] floatValues() {
         return this.floatValues;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.floatValues, other.floatValues);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, intValue, floatValues);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "intValue=" + this.intValue + ", " + "floatValues=" + this.floatValues + "]";
      }

      private Data(String var1, int var2, float[] var3) {
         this.text = var1;
         this.intValue = var2;
         this.floatValues = var3;
      }

      public String name() {
         return this.text;
      }

      public int size() {
         return this.intValue;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }
   }

   public static final class Entry {
      private final Map<String, float[]> items = new LinkedHashMap<>();

      public PostEffectManager.Entry method2828(String var1, float var2) {
         this.items.put(var1, new float[]{var2});
         return this;
      }

      public PostEffectManager.Entry method2829(String var1, float var2, float var3) {
         this.items.put(var1, new float[]{var2, var3});
         return this;
      }

      public PostEffectManager.Entry method2830(String var1, float var2, float var3, float var4) {
         this.items.put(var1, new float[]{var2, var3, var4});
         return this;
      }

      public PostEffectManager.Entry method2831(String var1, float var2, float var3, float var4, float var5) {
         this.items.put(var1, new float[]{var2, var3, var4, var5});
         return this;
      }

      public boolean isEmpty() {
         return this.items.isEmpty();
      }
   }
}

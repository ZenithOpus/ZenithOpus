package org.zenith.utility.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.Class37;

public final class ModelJsonParser {
   private static final float[][] CONST_floatValues = new float[][]{
      {0.0F, 0.0F, 0.0F}, {0.0F, 0.0F, 0.0F}, {-5.0F, 2.0F, 0.0F}, {5.0F, 2.0F, 0.0F}, {-1.9F, 12.0F, 0.0F}, {1.9F, 12.0F, 0.0F}
   };
   private static final String[] texts = new String[]{"down", "up", "west", "north", "east", "south"};
   private static final float[][] CONST_floatValues2 = new float[][]{
      {0.0F, -1.0F, 0.0F}, {0.0F, 1.0F, 0.0F}, {-1.0F, 0.0F, 0.0F}, {0.0F, 0.0F, -1.0F}, {1.0F, 0.0F, 0.0F}, {0.0F, 0.0F, 1.0F}
   };
   private static final int[][] CONST_intValues = new int[][]{{5, 4, 0, 1}, {2, 3, 7, 6}, {0, 4, 7, 3}, {1, 0, 3, 2}, {5, 1, 2, 6}, {4, 5, 6, 7}};
   private static final float CONST_floatValue = 0.14375F;
   private static final float CONST_floatValue2 = 0.0125F;

   private ModelJsonParser() {
   }

   public static Class15 method107(String var0, JsonObject var1, Identifier var2) {
      JsonObject var3 = method124(var1, "texture");
      float[] var4 = method129(var3 == null ? null : var3.get("size"), 2, 64.0F);
      int var5 = Math.max(1, (int)var4[0]);
      int var6 = Math.max(1, (int)var4[1]);
      JsonArray var7 = method125(var1, "bones");
      var var8 = method123(var7);
      int var9 = Math.max(1, var8.size());
      String[] var10 = new String[var9];
      int[] var11 = new int[var9];
      float[] var12 = new float[var9 * 3];
      float[] var13 = new float[var9 * 3];
      int[] var14 = new int[var9];
      int[] var15 = new int[var9];
      Class15.Type2[] var16 = new Class15.Type2[var9];
      float[] var17 = new float[var9 * 4];
      HashMap var18 = new HashMap();
      if (var8.isEmpty()) {
         var10[0] = "root";
         var11[0] = -1;
         var14[0] = -1;
         var15[0] = 6;
         var16[0] = Class15.Type2.NONE;
      }

      for (int var19 = 0; var19 < var8.size(); var19++) {
         JsonObject var20 = (JsonObject)var8.get(var19);
         String var21 = method126(var20, "name", "bone" + var19);
         var10[var19] = var21;
         Integer var22 = (Integer)var18.get(method126(var20, "parent", ""));
         var11[var19] = var22 == null ? -1 : var22;
         float[] var23 = method129(var20.get("pivot"), 3, 0.0F);
         float[] var24 = method129(var20.get("rotation"), 3, 0.0F);

         for (int var25 = 0; var25 < 3; var25++) {
            var12[var19 * 3 + var25] = var23[var25];
            var13[var19 * 3 + var25] = (float)Math.toRadians(var24[var25]);
         }

         var14[var19] = var11[var19] < 0 ? method121(method126(var20, "anchor", null)) : -1;
         var15[var19] = var11[var19] >= 0 ? var15[var11[var19]] : (var14[var19] >= 0 ? var14[var19] : 6);
         JsonObject var49 = method124(var20, "driver");
         var16[var19] = var49 == null ? Class15.Type2.NONE : method122(method126(var49, "type", "none"));
         float[] var26 = method129(var49 == null ? null : var49.get("args"), 4, 0.0F);
         System.arraycopy(var26, 0, var17, var19 * 4, 4);
         var18.put(var21, var19);
      }

      ArrayList<ModelJsonParser.Data> var38 = new ArrayList<>();

      for (int var39 = 0; var39 < var8.size(); var39++) {
         JsonArray var41 = method125((JsonObject)var8.get(var39), "cubes");
         float var43 = Float.NaN;
         if (var11[var39] < 0 && var14[var39] == Class15.Type.RIGHT_ARM.ordinal()) {
            var43 = -1.0F;
         } else if (var11[var39] < 0 && var14[var39] == Class15.Type.LEFT_ARM.ordinal()) {
            var43 = 1.0F;
         }

         for (JsonElement var47 : var41) {
            if (var47.isJsonObject()) {
               method110(var38, var39, var47.getAsJsonObject(), var5, var6, var43);
            }
         }
      }

      var38.sort((var1x, var2x) -> {
         int var3x = Integer.compare(var15[var1x.intValue], var15[var2x.intValue]);
         return var3x != 0 ? var3x : Integer.compare(var1x.intValue, var2x.intValue);
      });
      int var40 = var38.size();
      float[] var42 = new float[var40 * 12];
      float[] var44 = new float[var40 * 8];
      float[] var46 = new float[var40 * 3];
      int[] var48 = new int[var40];
      int[] var50 = new int[var40];

      for (int var51 = 0; var51 < var40; var51++) {
         ModelJsonParser.Data var27 = (ModelJsonParser.Data)var38.get(var51);
         System.arraycopy(var27.floatValues, 0, var42, var51 * 12, 12);
         System.arraycopy(var27.floatValues2, 0, var44, var51 * 8, 8);
         System.arraycopy(var27.floatValues3, 0, var46, var51 * 3, 3);
         var48[var51] = var27.intValue;
         var50[var51] = var27.intValue2;
      }

      if (var0 != null && (var0.startsWith("clothing/") || var0.startsWith("characters/") || var0.startsWith("politicians/"))) {
         method108(var42, var48, var11, var14);
      }

      var var52 = method113(method124(var1, "clips"), var18, var9);
      var var53 = method115(method125(var1, "effects"), var18);
      Class15.Data3 var28 = method117(method124(var1, "pet"));
      Class15.Data2 var29 = method118(method124(var1, "weapon"), var18);
      Class15.Data var30 = method119(method124(var1, "skin"), var18);
      float var31 = method127(var1, "lod", 24.0F);
      float[] var32 = method112(var42, var48, var11, var12, var13, var14);
      byte var33 = 0;

      for (JsonElement var35 : method125(var1, "hides")) {
         String var36 = var35.getAsString().toLowerCase(Locale.ROOT);

         var33 |= switch (var36) {
            case "hat" -> 1;
            case "jacket" -> 2;
            case "sleeves" -> 4;
            case "pants" -> 8;
            case "cape" -> 16;
            default -> 0;
         };
      }

      return new Class15(
         var0,
         var2,
         var5,
         var6,
         var10,
         var11,
         var12,
         var13,
         var14,
         var15,
         var16,
         var17,
         var42,
         var44,
         var46,
         var48,
         var50,
         var52,
         var53,
         var28,
         var29,
         var30,
         var31,
         var32,
         var33
      );
   }

   private static void method108(float[] var0, int[] var1, int[] var2, int[] var3) {
      int var4 = Class15.Type.RIGHT_ARM.ordinal();
      int var5 = Class15.Type.LEFT_ARM.ordinal();

      for (int var6 = 0; var6 < var1.length; var6++) {
         int var7 = var1[var6];
         if (var2[var7] < 0 && (var3[var7] == var4 || var3[var7] == var5)) {
            float var8 = var3[var7] == var4 ? -0.0625F : 0.0625F;

            for (int var9 = 0; var9 < 4; var9++) {
               int var10 = var6 * 12 + var9 * 3;
               var0[var10] = var8 + method109(var0[var10] - var8);
               var0[var10 + 2] = method109(var0[var10 + 2]);
            }
         }
      }
   }

   private static float method109(float var0) {
      float var1 = Math.abs(var0);
      if (var1 <= 0.14375F) {
         return var0;
      } else {
         float var2 = var1 - 0.14375F;
         float var3 = 0.14375F + 0.0125F * var2 / (var2 + 0.0125F);
         return Math.copySign(var3, var0);
      }
   }

   private static void method110(List<ModelJsonParser.Data> var0, int var1, JsonObject var2, int var3, int var4, float var5) {
      float[] var6 = method129(var2.get("from"), 3, 0.0F);
      float[] var7 = method129(var2.get("size"), 3, 0.0F);
      float var8 = method127(var2, "inflate", 0.0F);
      boolean var9 = method128(var2, "mirror");
      float[] var10 = method129(var2.get("rotation"), 3, 0.0F);
      boolean var11 = var10[0] == 0.0F && var10[1] == 0.0F && var10[2] == 0.0F;
      boolean var12 = !Float.isNaN(var5)
         && var11
         && var6[0] - var8 < var5
         && var6[0] + var7[0] + var8 > var5
         && var6[2] - var8 < 0.0F
         && var6[2] + var7[2] + var8 > 0.0F;
      int var13 = (var12 ? 32 : 0)
         | (method128(var2, "glow") ? 1 : 0)
         | (method128(var2, "translucent") ? 2 : 0)
         | (method128(var2, "detail") ? 4 : 0)
         | (method128(var2, "glass") ? 10 : 0)
         | (var2.has("sparkle") && var2.get("sparkle").isJsonPrimitive() && method128(var2, "sparkle") ? 16 : 0);
      HashSet var14 = new HashSet();
      if (var2.has("sparkle") && var2.get("sparkle").isJsonArray()) {
         for (JsonElement var16 : var2.getAsJsonArray("sparkle")) {
            var14.add(var16.getAsString());
         }
      }

      float var36 = var6[0] - var8;
      float var37 = var6[1] - var8;
      float var17 = var6[2] - var8;
      float var18 = var6[0] + var7[0] + var8;
      float var19 = var6[1] + var7[1] + var8;
      float var20 = var6[2] + var7[2] + var8;
      if (var9) {
         float var21 = var18;
         var18 = var36;
         var36 = var21;
      }

      float[][] var38 = new float[][]{
         {var36, var37, var17},
         {var18, var37, var17},
         {var18, var19, var17},
         {var36, var19, var17},
         {var36, var37, var20},
         {var18, var37, var20},
         {var18, var19, var20},
         {var36, var19, var20}
      };
      float[] var22 = method129(var2.get("rotation"), 3, 0.0F);
      Matrix3f var23 = null;
      if (var22[0] != 0.0F || var22[1] != 0.0F || var22[2] != 0.0F) {
         float[] var24 = method129(var2.get("origin"), 3, 0.0F);
         var23 = new Matrix3f().rotationZYX((float)Math.toRadians(var22[2]), (float)Math.toRadians(var22[1]), (float)Math.toRadians(var22[0]));
         Vector3f var25 = new Vector3f();

         for (float[] var29 : var38) {
            var25.set(var29[0] - var24[0], var29[1] - var24[1], var29[2] - var24[2]);
            var23.transform(var25);
            var29[0] = var25.x + var24[0];
            var29[1] = var25.y + var24[1];
            var29[2] = var25.z + var24[2];
         }
      }

      float[][] var39 = method111(var2, var7);

      for (int var40 = 0; var40 < 6; var40++) {
         float[] var41 = var39[var40];
         if (var41 != null) {
            int[] var42 = CONST_intValues[var40];
            float[] var43 = new float[12];

            for (int var44 = 0; var44 < 4; var44++) {
               float[] var30 = var38[var42[var9 ? 3 - var44 : var44]];
               var43[var44 * 3] = var30[0] / 16.0F;
               var43[var44 * 3 + 1] = var30[1] / 16.0F;
               var43[var44 * 3 + 2] = var30[2] / 16.0F;
            }

            float var45 = var41[0] / var3;
            float var46 = var41[1] / var4;
            float var31 = var41[2] / var3;
            float var32 = var41[3] / var4;
            float[] var33 = var9
               ? new float[]{var31, var32, var45, var32, var45, var46, var31, var46}
               : new float[]{var31, var46, var45, var46, var45, var32, var31, var32};
            Vector3f var34 = new Vector3f(CONST_floatValues2[var40][0] * (var9 ? -1 : 1), CONST_floatValues2[var40][1], CONST_floatValues2[var40][2]);
            if (var23 != null) {
               var23.transform(var34);
            }

            int var35 = var14.contains(texts[var40]) ? var13 | 16 : var13;
            var0.add(new ModelJsonParser.Data(var1, var35, var43, var33, new float[]{var34.x, var34.y, var34.z}));
         }
      }
   }

   private static float[][] method111(JsonObject var0, float[] var1) {
      float[][] var2 = new float[6][];
      JsonElement var3 = var0.get("faces");
      if (var3 != null && var3.isJsonObject()) {
         JsonObject var20 = var3.getAsJsonObject();

         for (int var21 = 0; var21 < 6; var21++) {
            JsonElement var22 = var20.get(texts[var21]);
            if (var22 != null && var22.isJsonArray() && var22.getAsJsonArray().size() >= 4) {
               var2[var21] = method129(var22, 4, 0.0F);
            }
         }

         return var2;
      } else {
         JsonElement var4 = var0.get("uv");
         if (var4 != null && var4.isJsonArray()) {
            float[] var5 = method129(var4, 2, 0.0F);
            float var6 = var5[0];
            float var7 = var5[1];
            float var8 = var1[0];
            float var9 = var1[1];
            float var10 = var1[2];
            float var12 = var6 + var10;
            float var13 = var6 + var10 + var8;
            float var14 = var6 + var10 + var8 + var8;
            float var15 = var6 + var10 + var8 + var10;
            float var16 = var6 + var10 + var8 + var10 + var8;
            float var18 = var7 + var10;
            float var19 = var7 + var10 + var9;
            var2[0] = new float[]{var12, var7, var13, var18};
            var2[1] = new float[]{var13, var18, var14, var7};
            var2[2] = new float[]{var6, var18, var12, var19};
            var2[3] = new float[]{var12, var18, var13, var19};
            var2[4] = new float[]{var13, var18, var15, var19};
            var2[5] = new float[]{var15, var18, var16, var19};
            return var2;
         } else {
            return var2;
         }
      }
   }

   private static float[] method112(float[] var0, int[] var1, int[] var2, float[] var3, float[] var4, int[] var5) {
      int var6 = var2.length;
      Matrix4f[] var7 = new Matrix4f[var6];

      for (int var8 = 0; var8 < var6; var8++) {
         Matrix4f var9 = var2[var8] >= 0 ? new Matrix4f(var7[var2[var8]]) : new Matrix4f();
         if (var2[var8] < 0 && var5[var8] >= 0) {
            float[] var10 = CONST_floatValues[var5[var8]];
            var9.translate(var10[0] / 16.0F, var10[1] / 16.0F, var10[2] / 16.0F);
         } else {
            var9.translate(var3[var8 * 3] / 16.0F, var3[var8 * 3 + 1] / 16.0F, var3[var8 * 3 + 2] / 16.0F);
         }

         var9.rotateZYX(var4[var8 * 3 + 2], var4[var8 * 3 + 1], var4[var8 * 3]);
         var7[var8] = var9;
      }

      float[] var14 = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
      Vector3f var15 = new Vector3f();

      for (int var16 = 0; var16 < var1.length; var16++) {
         Matrix4f var11 = var7[var1[var16]];

         for (int var12 = 0; var12 < 4; var12++) {
            int var13 = var16 * 12 + var12 * 3;
            var11.transformPosition(var0[var13], var0[var13 + 1], var0[var13 + 2], var15);
            var14[0] = Math.min(var14[0], var15.x);
            var14[1] = Math.min(var14[1], var15.y);
            var14[2] = Math.min(var14[2], var15.z);
            var14[3] = Math.max(var14[3], var15.x);
            var14[4] = Math.max(var14[4], var15.y);
            var14[5] = Math.max(var14[5], var15.z);
         }
      }

      return var1.length == 0 ? new float[6] : var14;
   }

   private static Map<String, Class37> method113(JsonObject var0, Map<String, Integer> var1, int var2) {
      if (var0 == null) {
         return Collections.emptyMap();
      } else {
         LinkedHashMap var3 = new LinkedHashMap();

         for (Entry var5 : var0.entrySet()) {
            if (((JsonElement)var5.getValue()).isJsonObject()) {
               JsonObject var6 = ((JsonElement)var5.getValue()).getAsJsonObject();
               HashMap var7 = new HashMap();
               JsonObject var8 = method124(var6, "bones");
               float var9 = 0.0F;
               if (var8 != null) {
                  for (Entry var11 : var8.entrySet()) {
                     Integer var12 = (Integer)var1.get(var11.getKey());
                     if (var12 != null && ((JsonElement)var11.getValue()).isJsonObject()) {
                        JsonObject var13 = ((JsonElement)var11.getValue()).getAsJsonObject();
                        boolean var14 = !method128(var13, "linear");
                        Class37.Entry[] var15 = new Class37.Entry[]{
                           method114(var13.get("rotation"), var14, 0.0F),
                           method114(var13.get("position"), var14, 0.0F),
                           method114(var13.get("scale"), var14, 1.0F)
                        };

                        for (Class37.Entry var19 : var15) {
                           if (var19 != null) {
                              var9 = Math.max(var9, var19.floatValues[var19.floatValues.length - 1]);
                           }
                        }

                        var7.put(var12, var15);
                     }
                  }
               }

               float var20 = method127(var6, "length", var9);
               boolean var21 = !var6.has("loop") || method128(var6, "loop");
               var3.put((String)var5.getKey(), new Class37((String)var5.getKey(), var20 <= 0.0F ? var9 : var20, var21, var7, var2));
            }
         }

         return var3;
      }
   }

   private static Class37.Entry method114(JsonElement var0, boolean var1, float var2) {
      if (var0 != null && var0.isJsonArray()) {
         JsonArray var3 = var0.getAsJsonArray();
         ArrayList<float[]> var4 = new ArrayList<>(var3.size());

         for (JsonElement var6 : var3) {
            if (var6.isJsonArray()) {
               JsonArray var7 = var6.getAsJsonArray();
               float var8 = var7.size() > 0 ? var7.get(0).getAsFloat() : 0.0F;
               float var9 = var7.size() > 1 ? var7.get(1).getAsFloat() : var2;
               float var10 = var7.size() > 2 ? var7.get(2).getAsFloat() : var9;
               float var11 = var7.size() > 3 ? var7.get(3).getAsFloat() : var10;
               var4.add(new float[]{var8, var9, var10, var11});
            }
         }

         if (var4.isEmpty()) {
            return null;
         } else {
            var4.sort((var0x, var1x) -> Float.compare(var0x[0], var1x[0]));
            float[] var12 = new float[var4.size()];
            float[] var13 = new float[var4.size() * 3];

            for (int var14 = 0; var14 < var4.size(); var14++) {
               float[] var15 = (float[])var4.get(var14);
               var12[var14] = var15[0];
               var13[var14 * 3] = var15[1];
               var13[var14 * 3 + 1] = var15[2];
               var13[var14 * 3 + 2] = var15[3];
            }

            return new Class37.Entry(var12, var13, var1);
         }
      } else {
         return null;
      }
   }

   private static List<Class15.Data4> method115(JsonArray var0, Map<String, Integer> var1) {
      ArrayList<Class15.Data4> var2 = new ArrayList<>();

      for (JsonElement var4 : var0) {
         if (var4.isJsonObject()) {
            Class15.Data4 var5 = method116(var4.getAsJsonObject(), var1);
            if (var5 != null) {
               var2.add(var5);
            }
         }
      }

      return var2;
   }

   private static Class15.Data4 method116(JsonObject var0, Map<String, Integer> var1) {
      ParticleEffect var2 = method120(method126(var0, "particle", null));
      if (var2 == null) {
         return null;
      } else {
         Integer var3 = (Integer)var1.get(method126(var0, "bone", ""));
         float[] var4 = method129(var0.get("offset"), 3, 0.0F);
         float[] var5 = method129(var0.get("velocity"), 3, 0.0F);

         Class15.Type3 var6;
         try {
            var6 = Class15.Type3.valueOf(method126(var0, "when", "always").toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var8) {
            var6 = Class15.Type3.ALWAYS;
         }

         return new Class15.Data4(
            var2,
            var3 == null ? -1 : var3,
            var4[0],
            var4[1],
            var4[2],
            method127(var0, "rate", 1.0F),
            var5[0],
            var5[1],
            var5[2],
            method127(var0, "spread", 0.05F),
            var6
         );
      }
   }

   private static Class15.Data3 method117(JsonObject var0) {
      if (var0 == null) {
         return null;
      } else {
         Class15.Type4 var1;
         try {
            var1 = Class15.Type4.valueOf(method126(var0, "kind", "walker").toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var5) {
            var1 = Class15.Type4.WALKER;
         }

         ArrayList var2 = new ArrayList();

         for (JsonElement var4 : method125(var0, "behaviors")) {
            var2.add(var4.getAsString().toLowerCase(Locale.ROOT));
         }

         return new Class15.Data3(
            var1,
            method127(var0, "scale", 1.0F),
            method127(var0, "speed", 1.0F),
            method127(var0, "hover", 0.0F),
            method126(var0, "quirk", ""),
            method120(method126(var0, "accent", null)),
            List.copyOf(var2)
         );
      }
   }

   private static Class15.Data2 method118(JsonObject var0, Map<String, Integer> var1) {
      if (var0 == null) {
         return null;
      } else {
         float[] var2 = method129(var0.get("grip"), 3, 0.0F);
         Integer var3 = (Integer)var1.get(method126(var0, "arrowBone", ""));
         return new Class15.Data2(
            var2[0],
            var2[1],
            var2[2],
            method127(var0, "angle", 0.0F),
            method127(var0, "roll", 0.0F),
            method127(var0, "scale", 1.0F),
            method120(method126(var0, "trail", null)),
            method120(method126(var0, "hit", null)),
            var3 == null ? -1 : var3
         );
      }
   }

   private static Class15.Data method119(JsonObject var0, Map<String, Integer> var1) {
      if (var0 == null) {
         return null;
      } else {
         float[] var2 = new float[18];
         boolean var3 = false;
         JsonObject var4 = method124(var0, "idlePose");
         if (var4 != null) {
            String[] var5 = new String[]{"head", "body", "right_arm", "left_arm", "right_leg", "left_leg"};

            for (int var6 = 0; var6 < var5.length; var6++) {
               if (var4.has(var5[var6])) {
                  float[] var7 = method129(var4.get(var5[var6]), 3, 0.0F);

                  for (int var8 = 0; var8 < 3; var8++) {
                     var2[var6 * 3 + var8] = (float)Math.toRadians(var7[var8]);
                  }

                  var3 = true;
               }
            }
         }

         return new Class15.Data(var3 ? var2 : null, !var0.has("hideArmor") || method128(var0, "hideArmor"), method115(method125(var0, "aura"), var1));
      }
   }

   public static ParticleEffect method120(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String var1 = var0.strip().toLowerCase(Locale.ROOT);
         if (var1.startsWith("dust:")) {
            String[] var6 = var1.split(":");

            try {
               int var7 = Integer.parseInt(var6[1].replace("#", ""), 16);
               float var8 = var6.length > 2 ? Float.parseFloat(var6[2]) : 1.0F;
               return new DustParticleEffect(var7, var8);
            } catch (RuntimeException var5) {
               return null;
            }
         } else {
            Identifier var2 = Identifier.tryParse(var1.contains(":") ? var1 : "minecraft:" + var1);
            if (var2 == null) {
               return null;
            } else {
               ParticleType var3 = Registries.PARTICLE_TYPE.get(var2);
               return var3 instanceof SimpleParticleType var4 ? var4 : null;
            }
         }
      } else {
         return null;
      }
   }

   private static int method121(String var0) {
      if (var0 == null) {
         return -1;
      } else {
         try {
            return Class15.Type.valueOf(var0.strip().toUpperCase(Locale.ROOT)).ordinal();
         } catch (IllegalArgumentException var2) {
            return -1;
         }
      }
   }

   private static Class15.Type2 method122(String var0) {
      try {
         return Class15.Type2.valueOf(var0.strip().toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException var2) {
         return Class15.Type2.NONE;
      }
   }

   private static List<JsonObject> method123(JsonArray var0) {
      ArrayList var1 = new ArrayList();

      for (JsonElement var3 : var0) {
         if (var3.isJsonObject()) {
            var1.add(var3.getAsJsonObject());
         }
      }

      List<JsonObject> var8 = new ArrayList<>(var1.size());
      HashSet var9 = new HashSet();
      boolean var4 = true;

      while (!var1.isEmpty() && var4) {
         var4 = false;
         int var5 = 0;

         while (var5 < var1.size()) {
            JsonObject var6 = (JsonObject)var1.get(var5);
            String var7 = method126(var6, "parent", null);
            if (var7 != null && !var9.contains(var7)) {
               var5++;
            } else {
               var8.add(var6);
               var9.add(method126(var6, "name", ""));
               var1.remove(var5);
               var4 = true;
            }
         }
      }

      var8.addAll(var1);
      return var8;
   }

   private static JsonObject method124(JsonObject var0, String var1) {
      return var0 != null && var0.has(var1) && var0.get(var1).isJsonObject() ? var0.getAsJsonObject(var1) : null;
   }

   private static JsonArray method125(JsonObject var0, String var1) {
      return var0 != null && var0.has(var1) && var0.get(var1).isJsonArray() ? var0.getAsJsonArray(var1) : new JsonArray();
   }

   private static String method126(JsonObject var0, String var1, String var2) {
      try {
         return var0 != null && var0.has(var1) && var0.get(var1).isJsonPrimitive() ? var0.get(var1).getAsString() : var2;
      } catch (RuntimeException var4) {
         return var2;
      }
   }

   private static float method127(JsonObject var0, String var1, float var2) {
      try {
         return var0 != null && var0.has(var1) && var0.get(var1).isJsonPrimitive() ? var0.get(var1).getAsFloat() : var2;
      } catch (RuntimeException var4) {
         return var2;
      }
   }

   private static boolean method128(JsonObject var0, String var1) {
      try {
         return var0 != null && var0.has(var1) && var0.get(var1).isJsonPrimitive() && var0.get(var1).getAsBoolean();
      } catch (RuntimeException var3) {
         return false;
      }
   }

   private static float[] method129(JsonElement var0, int var1, float var2) {
      float[] var3 = new float[var1];
      Arrays.fill(var3, var2);
      if (var0 != null && var0.isJsonArray()) {
         JsonArray var4 = var0.getAsJsonArray();

         for (int var5 = 0; var5 < var1 && var5 < var4.size(); var5++) {
            try {
               var3[var5] = var4.get(var5).getAsFloat();
            } catch (RuntimeException var7) {
               var3[var5] = var2;
            }
         }

         return var3;
      } else {
         return var3;
      }
   }

   private static final class Data {
      private final int intValue;
      private final int intValue2;
      private final float[] floatValues;
      private final float[] floatValues2;
      private final float[] floatValues3;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public float[] floatValues() {
         return this.floatValues;
      }

      public float[] floatValues2() {
         return this.floatValues2;
      }

      public float[] floatValues3() {
         return this.floatValues3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.floatValues, other.floatValues) && java.util.Objects.equals(this.floatValues2, other.floatValues2) && java.util.Objects.equals(this.floatValues3, other.floatValues3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, floatValues, floatValues2, floatValues3);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "floatValues=" + this.floatValues + ", " + "floatValues2=" + this.floatValues2 + ", " + "floatValues3=" + this.floatValues3 + "]";
      }

      private Data(int var1, int var2, float[] var3, float[] var4, float[] var5) {
         this.intValue = var1;
         this.intValue2 = var2;
         this.floatValues = var3;
         this.floatValues2 = var4;
         this.floatValues3 = var5;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public float[] getFloatValues() {
         return this.floatValues;
      }

      public float[] getFloatValues2() {
         return this.floatValues2;
      }

      public float[] getFloatValues3() {
         return this.floatValues3;
      }
   }
}

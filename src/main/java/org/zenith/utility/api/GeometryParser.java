package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.zenith.utility.misc.CosmeticSkeleton;

public final class GeometryParser {
   private static final float CONST_floatValue = 16.0F;

   private GeometryParser() {
   }

   public static CosmeticSkeleton method2261(String var0, String var1) {
      CosmeticSkeleton.Entry var2 = new CosmeticSkeleton.Entry();
      int var3 = var2.method7147(var1 == null ? "" : var1);
      JsonObject var4 = JsonParser.parseString(var0).getAsJsonObject();
      JsonArray var5 = var4.has("minecraft:geometry") && var4.get("minecraft:geometry").isJsonArray()
         ? var4.getAsJsonArray("minecraft:geometry")
         : new JsonArray();
      JsonObject var6 = null;

      for (JsonElement var8 : var5) {
         if (var8.isJsonObject()) {
            var6 = var8.getAsJsonObject();
            break;
         }
      }

      if (var6 == null) {
         return var2.method7152();
      } else {
         JsonObject var21 = method2267(var6, "description");
         float var22 = method2269(var21, "texture_width", 64.0F);
         float var9 = method2269(var21, "texture_height", 64.0F);
         if (var22 <= 0.0F) {
            var22 = 64.0F;
         }

         if (var9 <= 0.0F) {
            var9 = 64.0F;
         }

         JsonArray var10 = var6.has("bones") && var6.get("bones").isJsonArray() ? var6.getAsJsonArray("bones") : new JsonArray();
         HashMap var11 = new HashMap();
         List<JsonObject> var12 = method2262(var10);

         for (Object var14_o : (Iterable<?>)(Object)(var12)) {
            JsonObject var14 = (JsonObject)var14_o;
            String var15 = method2268(var14, "name", "bone" + var11.size());
            String var16 = method2268(var14, "parent", null);
            Integer var17 = var16 == null ? null : (Integer)var11.get(var16);
            float[] var18 = method2271(var14.get("pivot"), 0.0F);
            float[] var19 = method2271(var14.get("rotation"), 0.0F);
            int var20 = var2.method7146(var15, var17 == null ? -1 : var17, -var18[0] / 16.0F, var18[1] / 16.0F, -var18[2] / 16.0F, var19[0], var19[1], var19[2]);
            var11.put(var15, var20);
         }

         for (int var23 = 0; var23 < var12.size(); var23++) {
            JsonObject var24 = (JsonObject)var12.get(var23);
            if (var24.has("cubes") && var24.get("cubes").isJsonArray()) {
               for (JsonElement var26 : var24.getAsJsonArray("cubes")) {
                  if (var26.isJsonObject()) {
                     method2263(var2, var23, var3, var26.getAsJsonObject(), var22, var9);
                  }
               }
            }
         }

         return var2.method7152();
      }
   }

   private static List<JsonObject> method2262(JsonArray var0) {
      List<JsonObject> var1 = new ArrayList<>(var0.size());

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
            String var7 = method2268(var6, "parent", null);
            if (var7 != null && !var9.contains(var7)) {
               var5++;
            } else {
               var8.add(var6);
               var9.add(method2268(var6, "name", ""));
               var1.remove(var5);
               var4 = true;
            }
         }
      }

      var8.addAll(var1);
      return var8;
   }

   private static void method2263(CosmeticSkeleton.Entry var0, int var1, int var2, JsonObject var3, float var4, float var5) {
      float[] var6 = method2271(var3.get("origin"), 0.0F);
      float[] var7 = method2271(var3.get("size"), 0.0F);
      float var8 = method2269(var3, "inflate", 0.0F);
      boolean var9 = method2270(var3, "mirror");
      float var10 = (-var6[0] - var7[0] - var8) / 16.0F;
      float var11 = (var6[1] - var8) / 16.0F;
      float var12 = (-var6[2] - var7[2] - var8) / 16.0F;
      float var13 = (-var6[0] + var8) / 16.0F;
      float var14 = (var6[1] + var7[1] + var8) / 16.0F;
      float var15 = (-var6[2] + var8) / 16.0F;
      float[][] var16 = new float[][]{
         {var10, var11, var12},
         {var13, var11, var12},
         {var13, var14, var12},
         {var10, var14, var12},
         {var10, var11, var15},
         {var13, var11, var15},
         {var13, var14, var15},
         {var10, var14, var15}
      };
      float[] var17 = method2271(var3.get("rotation"), 0.0F);
      if (var17[0] != 0.0F || var17[1] != 0.0F || var17[2] != 0.0F) {
         float[] var18 = method2271(var3.get("pivot"), 0.0F);
         float var19 = -var18[0] / 16.0F;
         float var20 = var18[1] / 16.0F;
         float var21 = -var18[2] / 16.0F;
         method2266(var16, var19, var20, var21, -var17[0], var17[1], -var17[2]);
      }

      method2264(var0, var1, var2, var16, 5, 4, 7, 6, method2265(var3, "north", var7, var9, 0, var4, var5));
      method2264(var0, var1, var2, var16, 0, 1, 2, 3, method2265(var3, "south", var7, var9, 1, var4, var5));
      method2264(var0, var1, var2, var16, 4, 0, 3, 7, method2265(var3, "east", var7, var9, 2, var4, var5));
      method2264(var0, var1, var2, var16, 1, 5, 6, 2, method2265(var3, "west", var7, var9, 3, var4, var5));
      method2264(var0, var1, var2, var16, 3, 2, 6, 7, method2265(var3, "up", var7, var9, 4, var4, var5));
      method2264(var0, var1, var2, var16, 4, 5, 1, 0, method2265(var3, "down", var7, var9, 5, var4, var5));
   }

   private static void method2264(CosmeticSkeleton.Entry var0, int var1, int var2, float[][] var3, int var4, int var5, int var6, int var7, float[] var8) {
      if (var8 != null) {
         int[] var9 = new int[]{var4, var5, var6, var7};
         float[] var10 = new float[12];

         for (int var11 = 0; var11 < 4; var11++) {
            var10[var11 * 3] = var3[var9[var11]][0];
            var10[var11 * 3 + 1] = var3[var9[var11]][1];
            var10[var11 * 3 + 2] = var3[var9[var11]][2];
         }

         var0.method7148(var1, var2, var10, var8, null);
      }
   }

   private static float[] method2265(JsonObject var0, String var1, float[] var2, boolean var3, int var4, float var5, float var6) {
      JsonElement var7 = var0.get("uv");
      if (var7 == null) {
         return null;
      } else {
         float var8;
         float var9;
         float var10;
         float var11;
         if (var7.isJsonArray()) {
            float[] var12 = method2271(var7, 0.0F);
            float var13 = var2[2];
            float var14 = var2[0];
            float var15 = var2[1];
            switch (var4) {
               case 0:
                  var8 = var12[0] + var13 + var14 + var13;
                  var9 = var12[1] + var13;
                  var10 = var14;
                  var11 = var15;
                  break;
               case 1:
                  var8 = var12[0] + var13;
                  var9 = var12[1] + var13;
                  var10 = var14;
                  var11 = var15;
                  break;
               case 2:
                  var8 = var12[0];
                  var9 = var12[1] + var13;
                  var10 = var13;
                  var11 = var15;
                  break;
               case 3:
                  var8 = var12[0] + var13 + var14;
                  var9 = var12[1] + var13;
                  var10 = var13;
                  var11 = var15;
                  break;
               case 4:
                  var8 = var12[0] + var13;
                  var9 = var12[1];
                  var10 = var14;
                  var11 = var13;
                  break;
               default:
                  var8 = var12[0] + var13 + var14;
                  var9 = var12[1];
                  var10 = var14;
                  var11 = var13;
            }
         } else {
            if (!var7.isJsonObject()) {
               return null;
            }

            JsonObject var17 = var7.getAsJsonObject();
            if (!var17.has(var1) || !var17.get(var1).isJsonObject()) {
               return null;
            }

            JsonObject var19 = var17.getAsJsonObject(var1);
            float[] var21 = method2271(var19.get("uv"), 0.0F);
            float[] var23 = method2271(var19.get("uv_size"), 0.0F);
            var8 = var21[0];
            var9 = var21[1];
            var10 = var23[0];
            var11 = var23[1];
         }

         float var18 = var8 / var5;
         float var20 = var9 / var6;
         float var22 = (var8 + var10) / var5;
         float var24 = (var9 + var11) / var6;
         if (var3) {
            float var16 = var18;
            var18 = var22;
            var22 = var16;
         }

         return new float[]{var18, var24, var22, var24, var22, var20, var18, var20};
      }
   }

   private static void method2266(float[][] var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      double var7 = Math.toRadians(var4);
      double var9 = Math.toRadians(var5);
      double var11 = Math.toRadians(var6);
      float var13 = (float)Math.sin(var7);
      float var14 = (float)Math.cos(var7);
      float var15 = (float)Math.sin(var9);
      float var16 = (float)Math.cos(var9);
      float var17 = (float)Math.sin(var11);
      float var18 = (float)Math.cos(var11);

      for (float[] var22 : var0) {
         float var23 = var22[0] - var1;
         float var24 = var22[1] - var2;
         float var25 = var22[2] - var3;
         float var26 = var23 * var18 - var24 * var17;
         float var27 = var23 * var17 + var24 * var18;
         float var29 = var26 * var16 + var25 * var15;
         float var31 = -var26 * var15 + var25 * var16;
         float var33 = var27 * var14 - var31 * var13;
         float var34 = var27 * var13 + var31 * var14;
         var22[0] = var29 + var1;
         var22[1] = var33 + var2;
         var22[2] = var34 + var3;
      }
   }

   private static JsonObject method2267(JsonObject var0, String var1) {
      return var0 != null && var0.has(var1) && var0.get(var1).isJsonObject() ? var0.getAsJsonObject(var1) : null;
   }

   private static String method2268(JsonObject var0, String var1, String var2) {
      try {
         return var0 != null && var0.has(var1) && var0.get(var1).isJsonPrimitive() ? var0.get(var1).getAsString() : var2;
      } catch (RuntimeException var4) {
         return var2;
      }
   }

   private static float method2269(JsonObject var0, String var1, float var2) {
      try {
         return var0 != null && var0.has(var1) && var0.get(var1).isJsonPrimitive() ? var0.get(var1).getAsFloat() : var2;
      } catch (RuntimeException var4) {
         return var2;
      }
   }

   private static boolean method2270(JsonObject var0, String var1) {
      try {
         return var0 != null && var0.has(var1) && var0.get(var1).isJsonPrimitive() && var0.get(var1).getAsBoolean();
      } catch (RuntimeException var3) {
         return false;
      }
   }

   private static float[] method2271(JsonElement var0, float var1) {
      float[] var2 = new float[]{var1, var1, var1};
      if (var0 != null && var0.isJsonArray()) {
         JsonArray var3 = var0.getAsJsonArray();

         for (int var4 = 0; var4 < 3 && var4 < var3.size(); var4++) {
            try {
               var2[var4] = var3.get(var4).getAsFloat();
            } catch (RuntimeException var6) {
            }
         }

         return var2;
      } else {
         return var2;
      }
   }
}

package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.zenith.utility.misc.CosmeticSkeleton;

public final class TechneParser {
   private static final float CONST_floatValue = 16.0F;

   private TechneParser() {
   }

   public static CosmeticSkeleton method7430(String var0) {
      return method7431(var0, null);
   }

   public static CosmeticSkeleton method7431(String var0, String var1) {
      CosmeticSkeleton.Entry var2 = new CosmeticSkeleton.Entry();
      JsonObject var3 = null;

      try {
         var3 = method7439(JsonParser.parseString(var0).getAsJsonObject(), "Techne");
      } catch (RuntimeException var10) {
      }

      if (var3 == null) {
         return var2.method7152();
      } else {
         HashSet var4 = new HashSet();
         int var5 = 0;

         for (JsonElement var7 : method7437(var3.get("Models"))) {
            if (var7.isJsonObject()) {
               JsonObject var8 = var7.getAsJsonObject();
               JsonObject var9 = method7439(var8, "Model");
               method7432(var2, var9 == null ? var8 : var9, var1, var4, var5++);
            }
         }

         return var2.method7152();
      }
   }

   private static void method7432(CosmeticSkeleton.Entry var0, JsonObject var1, String var2, Set<String> var3, int var4) {
      String var5 = var2 != null && !var2.isBlank() ? var2 : method7440(var1, "@texture", "");
      int var6 = var0.method7147(var5);
      float[] var7 = method7441(method7440(var1, "TextureSize", ""), 2, 0.0F);
      float var8 = var7[0] > 0.0F ? var7[0] : 64.0F;
      float var9 = var7[1] > 0.0F ? var7[1] : 32.0F;
      float[] var10 = method7441(method7440(var1, "GlScale", ""), 3, 1.0F);

      for (int var11 = 0; var11 < 3; var11++) {
         if (var10[var11] == 0.0F) {
            var10[var11] = 1.0F;
         }
      }

      int var15 = var0.method7146(method7438(var3, method7440(var1, "Name", "model" + var4)), -1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      JsonObject var12 = method7439(var1, "Geometry");
      if (var12 != null) {
         for (JsonElement var14 : method7437(var12.get("Shape"))) {
            if (var14.isJsonObject()) {
               method7433(var0, var15, var6, var14.getAsJsonObject(), var3, var10, var8, var9);
            }
         }
      }
   }

   private static void method7433(CosmeticSkeleton.Entry var0, int var1, int var2, JsonObject var3, Set<String> var4, float[] var5, float var6, float var7) {
      float[] var8 = method7441(method7440(var3, "Position", ""), 3, 0.0F);
      float[] var9 = method7441(method7440(var3, "Offset", ""), 3, 0.0F);
      float[] var10 = method7441(method7440(var3, "Size", ""), 3, 0.0F);
      float[] var11 = method7441(method7440(var3, "Rotation", ""), 3, 0.0F);
      float[] var12 = method7441(method7440(var3, "TextureOffset", ""), 2, 0.0F);
      boolean var13 = "true".equals(method7440(var3, "IsMirrored", "False").strip().toLowerCase(Locale.ROOT));
      int var14 = var0.method7146(
         method7438(var4, method7440(var3, "@Name", "shape")),
         var1,
         method7436(var8[0], var5[0]),
         method7436(var8[1], var5[1]),
         method7436(var8[2], var5[2]),
         (float)(-Math.toDegrees(var11[0])),
         (float)Math.toDegrees(var11[1]),
         (float)(-Math.toDegrees(var11[2]))
      );
      float[] var15 = new float[3];
      float[] var16 = new float[3];

      for (int var17 = 0; var17 < 3; var17++) {
         float var18 = method7436(var8[var17] + var9[var17], var5[var17]);
         float var19 = method7436(var8[var17] + var9[var17] + var10[var17], var5[var17]);
         var15[var17] = Math.min(var18, var19);
         var16[var17] = Math.max(var18, var19);
      }

      float var24 = var15[0];
      float var25 = var15[1];
      float var26 = var15[2];
      float var20 = var16[0];
      float var21 = var16[1];
      float var22 = var16[2];
      float[][] var23 = new float[][]{
         {var24, var25, var26},
         {var20, var25, var26},
         {var20, var21, var26},
         {var24, var21, var26},
         {var24, var25, var22},
         {var20, var25, var22},
         {var20, var21, var22},
         {var24, var21, var22}
      };
      method7434(var0, var14, var2, var23, 5, 4, 7, 6, method7435(var12, var10, var13, 0, var6, var7));
      method7434(var0, var14, var2, var23, 0, 1, 2, 3, method7435(var12, var10, var13, 1, var6, var7));
      method7434(var0, var14, var2, var23, 4, 0, 3, 7, method7435(var12, var10, var13, 2, var6, var7));
      method7434(var0, var14, var2, var23, 1, 5, 6, 2, method7435(var12, var10, var13, 3, var6, var7));
      method7434(var0, var14, var2, var23, 3, 2, 6, 7, method7435(var12, var10, var13, 4, var6, var7));
      method7434(var0, var14, var2, var23, 4, 5, 1, 0, method7435(var12, var10, var13, 5, var6, var7));
   }

   private static void method7434(CosmeticSkeleton.Entry var0, int var1, int var2, float[][] var3, int var4, int var5, int var6, int var7, float[] var8) {
      int[] var9 = new int[]{var4, var5, var6, var7};
      float[] var10 = new float[12];

      for (int var11 = 0; var11 < 4; var11++) {
         var10[var11 * 3] = var3[var9[var11]][0];
         var10[var11 * 3 + 1] = var3[var9[var11]][1];
         var10[var11 * 3 + 2] = var3[var9[var11]][2];
      }

      var0.method7148(var1, var2, var10, var8, null);
   }

   private static float[] method7435(float[] var0, float[] var1, boolean var2, int var3, float var4, float var5) {
      float var6 = var1[0];
      float var7 = var1[1];
      float var8 = var1[2];
      float var9;
      float var10;
      float var11;
      float var12;
      switch (var3) {
         case 0:
            var9 = var0[0] + var8 + var6 + var8;
            var10 = var0[1] + var8;
            var11 = var6;
            var12 = var7;
            break;
         case 1:
            var9 = var0[0] + var8;
            var10 = var0[1] + var8;
            var11 = var6;
            var12 = var7;
            break;
         case 2:
            var9 = var0[0];
            var10 = var0[1] + var8;
            var11 = var8;
            var12 = var7;
            break;
         case 3:
            var9 = var0[0] + var8 + var6;
            var10 = var0[1] + var8;
            var11 = var8;
            var12 = var7;
            break;
         case 4:
            var9 = var0[0] + var8;
            var10 = var0[1];
            var11 = var6;
            var12 = var8;
            break;
         default:
            var9 = var0[0] + var8 + var6;
            var10 = var0[1];
            var11 = var6;
            var12 = var8;
      }

      float var13 = var9 / var4;
      float var14 = var10 / var5;
      float var15 = (var9 + var11) / var4;
      float var16 = (var10 + var12) / var5;
      if (var2) {
         float var17 = var13;
         var13 = var15;
         var15 = var17;
      }

      return new float[]{var13, var16, var15, var16, var15, var14, var13, var14};
   }

   private static float method7436(float var0, float var1) {
      return -var0 * var1 / 16.0F;
   }

   private static JsonArray method7437(JsonElement var0) {
      if (var0 == null || var0.isJsonNull()) {
         return new JsonArray();
      } else if (var0.isJsonArray()) {
         return var0.getAsJsonArray();
      } else {
         JsonArray var1 = new JsonArray(1);
         var1.add(var0);
         return var1;
      }
   }

   private static String method7438(Set<String> var0, String var1) {
      String var2 = var1 != null && !var1.isBlank() ? var1 : "bone";
      String var3 = var2;
      int var4 = 2;

      while (!var0.add(var3)) {
         var3 = var2 + "#" + var4++;
      }

      return var3;
   }

   private static JsonObject method7439(JsonObject var0, String var1) {
      return var0 != null && var0.has(var1) && var0.get(var1).isJsonObject() ? var0.getAsJsonObject(var1) : null;
   }

   private static String method7440(JsonObject var0, String var1, String var2) {
      try {
         return var0 != null && var0.has(var1) && var0.get(var1).isJsonPrimitive() ? var0.get(var1).getAsString() : var2;
      } catch (RuntimeException var4) {
         return var2;
      }
   }

   private static float[] method7441(String var0, int var1, float var2) {
      float[] var3 = new float[var1];
      Arrays.fill(var3, var2);
      if (var0 != null && !var0.isBlank()) {
         String[] var4 = var0.split(",");

         for (int var5 = 0; var5 < var1 && var5 < var4.length; var5++) {
            try {
               var3[var5] = Float.parseFloat(var4[var5].strip());
            } catch (NumberFormatException var7) {
            }
         }

         return var3;
      } else {
         return var3;
      }
   }
}

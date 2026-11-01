package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class CosmeticAnimation {
   private static final float CONST_floatValue = 16.0F;
   private final CosmeticAnimation.Entry3 entry3;
   private final CosmeticAnimation.Entry3 entry32;
   private static final String[] texts = new String[]{"idle", "stand", "loop"};
   private static final String[] texts2 = new String[]{"walk", "run", "move", "fly", "swim"};

   private CosmeticAnimation(CosmeticAnimation.Entry3 var1, CosmeticAnimation.Entry3 var2) {
      this.entry3 = var1;
      this.entry32 = var2;
   }

   public String name() {
      return this.entry3.text;
   }

   public String method7806() {
      return this.entry32 == null ? "" : this.entry32.text;
   }

   public boolean method7807() {
      return this.entry3.flag;
   }

   public float method7808() {
      return this.entry3.floatValue;
   }

   public boolean method7809(String var1, float var2, float[] var3) {
      return this.method7810(var1, var2, false, var3);
   }

   public boolean method7810(String var1, float var2, boolean var3, float[] var4) {
      CosmeticAnimation.Entry3 var5 = var3 && this.entry32 != null ? this.entry32 : this.entry3;
      CosmeticAnimation.Entry2 var6 = var5.items.get(var1);
      if (var6 != null && var4 != null && var4.length >= 9) {
         float var7 = var5.method7825(var2);
         method7811(var6.entry, var7, var4, 0, 0.0F);
         method7811(var6.entry2, var7, var4, 3, 0.0F);
         method7811(var6.entry3, var7, var4, 6, 1.0F);
         var4[3] = -var4[3] / 16.0F;
         var4[4] /= 16.0F;
         var4[5] = -var4[5] / 16.0F;
         return true;
      } else {
         return false;
      }
   }

   private static void method7811(CosmeticAnimation.Entry var0, float var1, float[] var2, int var3, float var4) {
      if (var0 == null) {
         var2[var3] = var4;
         var2[var3 + 1] = var4;
         var2[var3 + 2] = var4;
      } else {
         float[] var5 = var0.floatValues;
         int var6 = var5.length - 1;
         if (var1 <= var5[0]) {
            method7812(var0.floatValues3, 0, var2, var3);
         } else if (var1 >= var5[var6]) {
            method7812(var0.floatValues2, var6, var2, var3);
         } else {
            int var7 = 0;

            while (var7 < var6 && var5[var7 + 1] < var1) {
               var7++;
            }

            float var8 = var5[var7];
            float var9 = var5[var7 + 1];
            float var10 = var9 - var8 <= 1.0E-6F ? 0.0F : (var1 - var8) / (var9 - var8);
            if (var0.flags[var7]) {
               int var14 = Math.max(var7 - 1, 0);
               int var15 = Math.min(var7 + 2, var6);

               for (int var16 = 0; var16 < 3; var16++) {
                  var2[var3 + var16] = method7813(
                     var0.floatValues3[var14 * 3 + var16],
                     var0.floatValues3[var7 * 3 + var16],
                     var0.floatValues2[(var7 + 1) * 3 + var16],
                     var0.floatValues2[var15 * 3 + var16],
                     var10
                  );
               }
            } else {
               for (int var11 = 0; var11 < 3; var11++) {
                  float var12 = var0.floatValues3[var7 * 3 + var11];
                  float var13 = var0.floatValues2[(var7 + 1) * 3 + var11];
                  var2[var3 + var11] = var12 + (var13 - var12) * var10;
               }
            }
         }
      }
   }

   private static void method7812(float[] var0, int var1, float[] var2, int var3) {
      var2[var3] = var0[var1 * 3];
      var2[var3 + 1] = var0[var1 * 3 + 1];
      var2[var3 + 2] = var0[var1 * 3 + 2];
   }

   private static float method7813(float var0, float var1, float var2, float var3, float var4) {
      float var5 = var4 * var4;
      float var6 = var5 * var4;
      return 0.5F
         * (2.0F * var1 + (var2 - var0) * var4 + (2.0F * var0 - 5.0F * var1 + 4.0F * var2 - var3) * var5 + (3.0F * var1 - var0 - 3.0F * var2 + var3) * var6);
   }

   public static CosmeticAnimation method7814(String var0) {
      if (var0 != null && !var0.isBlank()) {
         try {
            JsonElement var1 = JsonParser.parseString(var0);
            if (!var1.isJsonObject()) {
               return null;
            } else {
               JsonElement var2 = var1.getAsJsonObject().get("animations");
               if (var2 != null && var2.isJsonObject()) {
                  LinkedHashMap var3 = new LinkedHashMap();

                  for (Map.Entry var5 : var2.getAsJsonObject().entrySet()) {
                     if (((JsonElement)var5.getValue()).isJsonObject()) {
                        var3.put((String)var5.getKey(), method7815((String)var5.getKey(), ((JsonElement)var5.getValue()).getAsJsonObject()));
                     }
                  }

                  CosmeticAnimation.Entry3 var7 = method7816(var3, texts, true);
                  return var7 == null ? null : new CosmeticAnimation(var7, method7816(var3, texts2, false));
               } else {
                  return null;
               }
            }
         } catch (RuntimeException var6) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static CosmeticAnimation.Entry3 method7815(String var0, JsonObject var1) {
      HashMap var2 = new HashMap();
      float var3 = method7817(var1, var2);
      float var4 = method7823(var1, "animation_length", var3);
      return new CosmeticAnimation.Entry3(var0, method7824(var1, "loop"), var4 <= 0.0F ? var3 : var4, var2);
   }

   private static CosmeticAnimation.Entry3 method7816(Map<String, CosmeticAnimation.Entry3> var0, String[] var1, boolean var2) {
      for (int var3 = 0; var3 < 2; var3++) {
         boolean var4 = var3 == 0;

         for (String var8 : var1) {
            for (CosmeticAnimation.Entry3 var10 : var0.values()) {
               String var11 = var10.text.toLowerCase(Locale.ROOT);
               if (var11.equals(var8) && !var11.contains("rare") && (var10.flag || !var4)) {
                  return var10;
               }
            }
         }

         for (String var18 : var1) {
            for (CosmeticAnimation.Entry3 var20 : var0.values()) {
               String var21 = var20.text.toLowerCase(Locale.ROOT);
               if (var21.contains(var18) && !var21.contains("rare") && (var20.flag || !var4)) {
                  return var20;
               }
            }
         }
      }

      if (!var2) {
         return null;
      } else {
         CosmeticAnimation.Entry3 var12 = null;

         for (CosmeticAnimation.Entry3 var15 : var0.values()) {
            if (var12 == null) {
               var12 = var15;
            }

            if (var15.flag) {
               return var15;
            }
         }

         return var12;
      }
   }

   private static float method7817(JsonObject var0, Map<String, CosmeticAnimation.Entry2> var1) {
      JsonElement var2 = var0.get("bones");
      if (var2 != null && var2.isJsonObject()) {
         float var3 = 0.0F;

         for (Map.Entry var5 : var2.getAsJsonObject().entrySet()) {
            if (((JsonElement)var5.getValue()).isJsonObject()) {
               JsonObject var6 = ((JsonElement)var5.getValue()).getAsJsonObject();
               CosmeticAnimation.Entry2 var7 = new CosmeticAnimation.Entry2();
               var7.entry = method7819(var6.get("rotation"));
               var7.entry2 = method7819(var6.get("position"));
               var7.entry3 = method7819(var6.get("scale"));
               if (var7.entry != null || var7.entry2 != null || var7.entry3 != null) {
                  var3 = Math.max(var3, Math.max(method7818(var7.entry), Math.max(method7818(var7.entry2), method7818(var7.entry3))));
                  var1.put((String)var5.getKey(), var7);
               }
            }
         }

         return var3;
      } else {
         return 0.0F;
      }
   }

   private static float method7818(CosmeticAnimation.Entry var0) {
      return var0 == null ? 0.0F : var0.floatValues[var0.floatValues.length - 1];
   }

   private static CosmeticAnimation.Entry method7819(JsonElement var0) {
      if (var0 == null) {
         return null;
      } else if (var0.isJsonArray()) {
         float[] var14 = method7820(var0, 0.0F);
         return new CosmeticAnimation.Entry(new float[]{0.0F}, var14, (float[])var14.clone(), new boolean[]{false});
      } else if (!var0.isJsonObject()) {
         return null;
      } else {
         JsonObject var1 = var0.getAsJsonObject();
         ArrayList<float[]> var2 = new ArrayList<>(var1.size());

         for (Map.Entry var4 : var1.entrySet()) {
            float var5;
            try {
               var5 = Float.parseFloat((String)var4.getKey());
            } catch (NumberFormatException var13) {
               continue;
            }

            JsonElement var6 = (JsonElement)var4.getValue();
            boolean var9 = false;
            float[] var7;
            float[] var8;
            if (var6.isJsonObject()) {
               JsonObject var10 = var6.getAsJsonObject();
               JsonElement var11 = var10.get("post");
               JsonElement var12 = var10.get("pre");
               var8 = method7820(var11 != null ? var11 : var12, 0.0F);
               var7 = var12 != null ? method7820(var12, 0.0F) : (float[])var8.clone();
               var9 = "catmullrom".equalsIgnoreCase(method7822(var10, "lerp_mode"));
            } else {
               var8 = method7820(var6, 0.0F);
               var7 = (float[])var8.clone();
            }

            var2.add(new float[]{var5, var7[0], var7[1], var7[2], var8[0], var8[1], var8[2], var9 ? 1.0F : 0.0F});
         }

         if (var2.isEmpty()) {
            return null;
         } else {
            var2.sort((var0x, var1x) -> Float.compare(var0x[0], var1x[0]));
            int var15 = var2.size();
            float[] var16 = new float[var15];
            float[] var17 = new float[var15 * 3];
            float[] var18 = new float[var15 * 3];
            boolean[] var19 = new boolean[var15];

            for (int var20 = 0; var20 < var15; var20++) {
               float[] var21 = (float[])var2.get(var20);
               var16[var20] = var21[0];
               var17[var20 * 3] = var21[1];
               var17[var20 * 3 + 1] = var21[2];
               var17[var20 * 3 + 2] = var21[3];
               var18[var20 * 3] = var21[4];
               var18[var20 * 3 + 1] = var21[5];
               var18[var20 * 3 + 2] = var21[6];
               var19[var20] = var21[7] != 0.0F;
            }

            return new CosmeticAnimation.Entry(var16, var17, var18, var19);
         }
      }
   }

   private static float[] method7820(JsonElement var0, float var1) {
      float[] var2 = new float[]{var1, var1, var1};
      if (var0 == null) {
         return var2;
      } else if (var0.isJsonPrimitive()) {
         float var5 = method7821(var0, var1);
         var2[0] = var5;
         var2[1] = var5;
         var2[2] = var5;
         return var2;
      } else if (!var0.isJsonArray()) {
         return var2;
      } else {
         JsonArray var3 = var0.getAsJsonArray();

         for (int var4 = 0; var4 < 3 && var4 < var3.size(); var4++) {
            var2[var4] = method7821(var3.get(var4), var1);
         }

         return var2;
      }
   }

   private static float method7821(JsonElement var0, float var1) {
      try {
         return var0.getAsFloat();
      } catch (RuntimeException var3) {
         return var1;
      }
   }

   private static String method7822(JsonObject var0, String var1) {
      try {
         return var0.has(var1) && var0.get(var1).isJsonPrimitive() ? var0.get(var1).getAsString() : null;
      } catch (RuntimeException var3) {
         return null;
      }
   }

   private static float method7823(JsonObject var0, String var1, float var2) {
      try {
         return var0.has(var1) && var0.get(var1).isJsonPrimitive() ? var0.get(var1).getAsFloat() : var2;
      } catch (RuntimeException var4) {
         return var2;
      }
   }

   private static boolean method7824(JsonObject var0, String var1) {
      try {
         return var0.has(var1) && var0.get(var1).isJsonPrimitive() && var0.get(var1).getAsBoolean();
      } catch (RuntimeException var3) {
         return false;
      }
   }

   private static final class Entry {
      private final float[] floatValues;
      private final float[] floatValues2;
      private final float[] floatValues3;
      private final boolean[] flags;

      private Entry(float[] var1, float[] var2, float[] var3, boolean[] var4) {
         this.floatValues = var1;
         this.floatValues2 = var2;
         this.floatValues3 = var3;
         this.flags = var4;
      }
   }

   private static final class Entry2 {
      private CosmeticAnimation.Entry entry;
      private CosmeticAnimation.Entry entry2;
      private CosmeticAnimation.Entry entry3;
   }

   private static final class Entry3 {
      private final String text;
      private final boolean flag;
      private final float floatValue;
      private final Map<String, CosmeticAnimation.Entry2> items;

      private Entry3(String var1, boolean var2, float var3, Map<String, CosmeticAnimation.Entry2> var4) {
         this.text = var1;
         this.flag = var2;
         this.floatValue = var3;
         this.items = var4;
      }

      private float method7825(float var1) {
         if (this.floatValue <= 0.0F) {
            return 0.0F;
         } else {
            float var2 = var1 % this.floatValue;
            return var2 < 0.0F ? var2 + this.floatValue : var2;
         }
      }
   }
}

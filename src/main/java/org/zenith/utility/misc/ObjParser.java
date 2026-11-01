package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ObjParser {
   private ObjParser() {
   }

   public static CosmeticSkeleton method8021(String var0, String var1) {
      CosmeticSkeleton.Entry var2 = new CosmeticSkeleton.Entry();
      int var3 = var2.method7146("root", -1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      var var4 = method8025(var1);
      ArrayList var5 = new ArrayList();
      ArrayList var6 = new ArrayList();
      ArrayList var7 = new ArrayList();
      int var8 = var2.method7147("");

      for (String var12 : var0.split("\n")) {
         String var13 = var12.strip();
         if (!var13.isEmpty() && var13.charAt(0) != '#') {
            String[] var14 = var13.split("\\s+");
            String var15 = var14[0];
            switch (var15) {
               case "v":
                  var5.add(method8024(var14, 3, 0.0F));
                  break;
               case "vt":
                  var6.add(method8024(var14, 2, 0.0F));
                  break;
               case "vn":
                  var7.add(method8024(var14, 3, 0.0F));
                  break;
               case "usemtl":
                  String var17 = var14.length > 1 ? (String)var4.get(var14[1]) : null;
                  if (var17 != null) {
                     var8 = var2.method7147(var17);
                  }
                  break;
               case "f":
                  method8022(var2, var3, var8, var14, var5, var6, var7);
            }
         }
      }

      return var2.method7152();
   }

   private static void method8022(CosmeticSkeleton.Entry var0, int var1, int var2, String[] var3, List<float[]> var4, List<float[]> var5, List<float[]> var6) {
      int var7 = var3.length - 1;
      if (var7 >= 3) {
         for (byte var8 = 2; var8 <= var7 - 1; var8 += 2) {
            byte var9 = 1;
            int var11 = Math.min(var8 + 1, var7);
            int var12 = Math.min(var8 + 2, var7);
            float[] var13 = new float[12];
            float[] var14 = new float[8];
            float[] var15 = null;
            int[] var16 = new int[]{var9, var8, var11, var12};

            for (int var17 = 0; var17 < 4; var17++) {
               String[] var18 = var3[var16[var17]].split("/", -1);
               float[] var19 = method8023(var4, var18, 0);
               if (var19 == null) {
                  return;
               }

               var13[var17 * 3] = var19[0];
               var13[var17 * 3 + 1] = var19[1];
               var13[var17 * 3 + 2] = var19[2];
               float[] var20 = method8023(var5, var18, 1);
               if (var20 != null) {
                  var14[var17 * 2] = var20[0];
                  var14[var17 * 2 + 1] = 1.0F - var20[1];
               }

               if (var15 == null) {
                  float[] var21 = method8023(var6, var18, 2);
                  if (var21 != null) {
                     var15 = new float[]{var21[0], var21[1], var21[2]};
                  }
               }
            }

            var0.method7148(var1, var2, var13, var14, var15);
         }
      }
   }

   private static float[] method8023(List<float[]> var0, String[] var1, int var2) {
      if (!var0.isEmpty() && var2 < var1.length && !var1[var2].isBlank()) {
         try {
            int var3 = Integer.parseInt(var1[var2]);
            int var4 = var3 < 0 ? var0.size() + var3 : var3 - 1;
            return var4 >= 0 && var4 < var0.size() ? (float[])var0.get(var4) : null;
         } catch (NumberFormatException var5) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static float[] method8024(String[] var0, int var1, float var2) {
      float[] var3 = new float[var1];

      for (int var4 = 0; var4 < var1; var4++) {
         var3[var4] = var2;
         if (var4 + 1 < var0.length) {
            try {
               var3[var4] = Float.parseFloat(var0[var4 + 1]);
            } catch (NumberFormatException var6) {
            }
         }
      }

      return var3;
   }

   private static Map<String, String> method8025(String var0) {
      HashMap var1 = new HashMap();
      if (var0 == null) {
         return var1;
      } else {
         String var2 = null;

         for (String var6 : var0.split("\n")) {
            String var7 = var6.strip();
            if (var7.startsWith("newmtl ")) {
               var2 = var7.substring(7).strip();
            } else if (var7.startsWith("map_Kd ") && var2 != null) {
               var1.put(var2, var7.substring(7).strip());
            }
         }

         return var1;
      }
   }
}

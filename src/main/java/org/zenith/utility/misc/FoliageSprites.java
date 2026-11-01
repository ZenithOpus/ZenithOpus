package org.zenith.utility.misc;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

public final class FoliageSprites {
   public static volatile boolean enabled;
   private static final float CONST_floatValue = 3.2F;
   private static final int CONST_intValue = 16777215;
   private static final int CONST_intValue2 = 14213872;
   private static final String[] texts = new String[]{
      "leaves",
      "grass",
      "fern",
      "bush",
      "sapling",
      "vine",
      "flower",
      "tulip",
      "orchid",
      "dandelion",
      "poppy",
      "allium",
      "bluet",
      "daisy",
      "cornflower",
      "lily",
      "rose",
      "peony",
      "lilac",
      "sunflower",
      "azalea",
      "petals",
      "mushroom",
      "wheat",
      "carrots",
      "potatoes",
      "beetroots",
      "sugar_cane",
      "bamboo",
      "moss_carpet",
      "seagrass",
      "kelp"
   };
   private static final String[] texts2 = new String[]{"grass_block", "grass_path", "short_grass_top", "seagrass_", "kelp_plant"};
   private static final float CONST_floatValue2 = 0.8F;
   public static int intValue;

   private FoliageSprites() {
   }

   public static boolean method1431(Identifier var0) {
      if (enabled && var0 != null && "minecraft".equals(var0.getNamespace())) {
         String var1 = var0.getPath();
         if (!var1.startsWith("block/")) {
            return false;
         } else {
            for (String var5 : texts2) {
               if (var1.contains(var5)) {
                  return false;
               }
            }

            for (String var9 : texts) {
               if (var1.contains(var9)) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public static void method1432(Identifier var0, NativeImage var1) {
      int var2 = var1.getWidth();
      int var3 = var1.getHeight();
      if (var2 > 0 && var3 > 0) {
         intValue++;
         boolean var4 = var0.getPath().contains("leaves");
         if (var4) {
            for (int var5 = 0; var5 < var2; var5++) {
               for (int var6 = 0; var6 < var3; var6++) {
                  int var7 = var1.getColorArgb(var5, var6);
                  if (var7 >>> 24 > 16) {
                     int var8 = method1434(14213872, 16777215, method1436(method1433(var0), var5, var6));
                     var1.setColorArgb(var5, var6, var7 & 0xFF000000 | method1435(var7, var8, 0.8F));
                  }
               }
            }
         }

         float var18 = var2 / 16.0F;
         long var19 = method1433(var0);

         for (int var20 = 0; var20 < var2; var20++) {
            int var9 = -1;

            for (int var10 = 0; var10 < var3; var10++) {
               if (var1.getColorArgb(var20, var10) >>> 24 > 128) {
                  var9 = var10;
                  break;
               }
            }

            if (var9 >= 0) {
               float var21 = method1436(var19, var20, 0) + 0.5F * method1436(var19, var20, 7);
               int var11 = Math.max(1, Math.round(3.2F * var18 * (0.55F + 0.75F * var21)));

               for (int var12 = 0; var12 < var11; var12++) {
                  int var13 = var9 + var12;
                  if (var13 >= var3) {
                     break;
                  }

                  int var14 = var1.getColorArgb(var20, var13);
                  int var15 = var14 >>> 24;
                  if (var15 <= 128) {
                     break;
                  }

                  int var16 = method1434(14213872, 16777215, method1436(var19, var20, var13 + 3));
                  float var17 = var12 == var11 - 1 ? 0.45F : 1.0F;
                  var1.setColorArgb(var20, var13, var15 << 24 | method1435(var14, var16, var17));
               }
            }
         }
      }
   }

   private static long method1433(Identifier var0) {
      return var0.hashCode() * -7046029254386353131L;
   }

   private static int method1434(int var0, int var1, float var2) {
      int var3 = channel(var0 >> 16, var1 >> 16, var2);
      int var4 = channel(var0 >> 8, var1 >> 8, var2);
      int var5 = channel(var0, var1, var2);
      return var3 << 16 | var4 << 8 | var5;
   }

   private static int method1435(int var0, int var1, float var2) {
      int var3 = channel(var0 >> 16, var1 >> 16, var2);
      int var4 = channel(var0 >> 8, var1 >> 8, var2);
      int var5 = channel(var0, var1, var2);
      return var3 << 16 | var4 << 8 | var5;
   }

   private static int channel(int var0, int var1, float var2) {
      int var3 = var0 & 0xFF;
      int var4 = var1 & 0xFF;
      return Math.clamp((long)((int)(var3 + (var4 - var3) * var2 + 0.5F)), 0, 255);
   }

   private static float method1436(long var0, int var2, int var3) {
      long var4 = var0 ^ var2 * -6752110988234923001L ^ var3 * -6939452855193903323L;
      var4 ^= var4 >>> 29;
      var4 *= -4658895280553007687L;
      var4 ^= var4 >>> 32;
      return (float)(var4 >>> 40) / 1.6777216E7F;
   }
}

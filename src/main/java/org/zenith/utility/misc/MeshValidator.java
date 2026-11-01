package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class MeshValidator {
   private MeshValidator() {
   }

   public static List<String> method6095(Class32 var0) {
      ArrayList<String> var1 = new ArrayList<>();
      method6097(var1);
      method6098(var1);
      method6099(var1);
      method6100(var1);
      method6101(var1);
      method6102(var0, var1);
      method6096(var1);
      return var1;
   }

   private static void method6096(List<String> var0) {
      Class32 var1 = new Class32();
      byte var2 = 24;
      int var3 = var2 / 2;
      float var4 = 7.0F;

      for (int var5 = 0; var5 < var2; var5++) {
         for (int var6 = 0; var6 < var2; var6++) {
            for (int var7 = 0; var7 < var2; var7++) {
               float var8 = var5 - var3;
               float var9 = var6 - var3;
               float var10 = var7 - var3;
               float var11 = (float)Math.sqrt(var8 * var8 + var9 * var9 + var10 * var10);
               float var12 = (var4 - var11) / var4 * 127.0F;
               var1.method4904(var5, var6, var7, (byte)Math.clamp((long)Math.round(var12), -127, 127));
            }
         }
      }

      FloatArrayBuffer var16 = new FloatArrayBuffer();
      MeshImporter.Entry var17 = MeshImporter.method658(var1, var16, 0, 0, 0, var2, var2, var2);
      if (var16.isEmpty()) {
         var0.add("сетка пустая на заведомо непустом поле");
      } else {
         if (var17.intValue == 0) {
            var0.add("вершин ноль при непустой сетке");
         }

         for (int var18 = 0; var18 < var16.getIntValue(); var18++) {
            int var20 = 0;

            while (var20 < 4) {
               float var22 = var16.method380(var18, var20);
               float var23 = var16.method381(var18, var20);
               float var24 = var16.method382(var18, var20);
               if (!Float.isNaN(var22) && !Float.isNaN(var23) && !Float.isNaN(var24)) {
                  if (!(var22 < -1.0F) && !(var23 < -1.0F) && !(var24 < -1.0F) && !(var22 > var2 + 1.0F) && !(var23 > var2 + 1.0F) && !(var24 > var2 + 1.0F)) {
                     float var25 = var16.method383(var18, var20);
                     float var13 = var16.method384(var18, var20);
                     float var14 = var16.method385(var18, var20);
                     float var15 = (float)Math.sqrt(var25 * var25 + var13 * var13 + var14 * var14);
                     if (Math.abs(var15 - 1.0F) > 0.01F) {
                        var0.add("нормаль не единичная: длина " + var15);
                        return;
                     }

                     var20++;
                     continue;
                  }

                  var0.add("вершина вне области: " + var22 + " " + var23 + " " + var24);
                  return;
               }

               var0.add("в сетке NaN на квадрате " + var18);
               return;
            }
         }

         Class32 var19 = new Class32();
         FloatArrayBuffer var21 = new FloatArrayBuffer();
         MeshImporter.method658(var19, var21, 0, 0, 0, var2, var2, var2);
         if (!var21.isEmpty()) {
            var0.add("на пустом поле построилась сетка");
         }
      }
   }

   private static void method6097(List<String> var0) {
      Class32 var1 = new Class32();
      Random var2 = new Random(20260911L);

      for (int var3 = 0; var3 < 4000; var3++) {
         int var4 = var2.nextInt(4096) - 2048;
         int var5 = var2.nextInt(512) - 256;
         int var6 = var2.nextInt(4096) - 2048;
         byte var7 = (byte)(var2.nextInt(255) - 127);
         var1.method4904(var4, var5, var6, var7);
         byte var8 = var1.method4902(var4, var5, var6);
         if (var8 != var7) {
            var0.add("запись не читается: " + var4 + " " + var5 + " " + var6 + " записано " + var7 + ", прочитано " + var8);
            return;
         }
      }
   }

   private static void method6098(List<String> var0) {
      Class32 var1 = new Class32();

      try {
         if (var1.method4902(1000000, 64536, 999999) != -127) {
            var0.add("пустое пространство отдаёт не воздух");
         }

         var1.method4904(5, 5, 5, (byte)-127);
         if (var1.method4906() != 0) {
            var0.add("запись воздуха создала чанк");
         }
      } catch (RuntimeException var3) {
         var0.add("выход за границы бросает исключение: " + var3);
      }
   }

   private static void method6099(List<String> var0) {
      Class32 var1 = new Class32();
      byte var2 = 64;
      var1.method4904(var2 - 1, var2 - 1, var2 - 1, (byte)40);
      var1.method4904(var2, var2, var2, (byte)-40);
      if (var1.method4902(var2 - 1, var2 - 1, var2 - 1) != 40 || var1.method4902(var2, var2, var2) != -40) {
         var0.add("соседние чанки перетирают друг друга на границе");
      }

      if (var1.method4906() != 2) {
         var0.add("на границе чанков создано " + var1.method4906() + " вместо 2");
      }
   }

   private static void method6100(List<String> var0) {
      Class32 var1 = new Class32();
      var1.method4904(-1, -1, -1, (byte)77);
      if (var1.method4902(-1, -1, -1) != 77) {
         var0.add("отрицательные координаты читаются неверно");
      }

      if (var1.method4902(-65, -1, -1) != -127) {
         var0.add("отрицательные координаты заворачиваются в чужой чанк");
      }
   }

   private static void method6101(List<String> var0) {
      Class32 var1 = new Class32();
      var1.method4904(3, 3, 3, (byte)127);
      var1.method4904(3, 3, 3, (byte)-127);
      long var2 = var1.method4909();
      var1.method4910();
      long var4 = var1.method4909();
      if (var1.method4906() != 0) {
         var0.add("пустой чанк не убран при сжатии");
      }

      if (var4 > var2) {
         var0.add("сжатие увеличило память: " + var2 + " -> " + var4);
      }
   }

   private static void method6102(Class32 var0, List<String> var1) {
      if (var0 != null && var0.method4906() != 0) {
         long var2 = var0.method4908();
         if (var2 == 0L) {
            var1.add("чанки есть, а заполненных вокселей нет");
         }

         long var4 = var0.method4909();
         long var6 = var0.method4906() * 64L * 64L * 64L;
         if (var4 > var6 + var0.method4906() * 16L) {
            var1.add("память больше плотного хранения: " + var4 + " > " + var6);
         }
      }
   }
}

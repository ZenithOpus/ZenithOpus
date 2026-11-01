package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class Util6 {
   private Util6() {
   }

   public static int method1439(int var0, int var1) {
      if (var0 > var1) {
         throw new IllegalArgumentException("min > max");
      } else {
         return ThreadLocalRandom.current().nextInt(var0, var1 + 1);
      }
   }

   public static int method1440(int var0, int var1) {
      return method1439(var0, var1);
   }

   public static boolean method1441(int var0, int var1) {
      int var2 = method1439(var0, var1);
      return var2 > ThreadLocalRandom.current().nextInt(100);
   }

   public static <T> List<T> method1442(List<T> var0) {
      var var1 = new ArrayList<>(var0);
      Collections.shuffle(var1, (Random)ThreadLocalRandom.current());
      return var1;
   }

   public static <T> void method1443(List<T> var0) {
      Collections.shuffle(var0, (Random)ThreadLocalRandom.current());
   }
}

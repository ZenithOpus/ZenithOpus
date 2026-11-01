package org.zenith.utility.misc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector3f;

public final class Util7 {
   private Util7() {
   }

   public static float[] method1610(String var0, float var1, boolean var2) {
      InputStream var3 = Util7.class.getResourceAsStream(var0);
      if (var3 == null) {
         return null;
      } else {
         try {
            float[] var32;
            try (BufferedReader var4 = new BufferedReader(new InputStreamReader(var3, StandardCharsets.UTF_8))) {
               ArrayList var5 = new ArrayList();
               HashMap var6 = new HashMap();
               float var7 = Float.POSITIVE_INFINITY;
               float var8 = Float.POSITIVE_INFINITY;
               float var9 = Float.POSITIVE_INFINITY;
               float var10 = Float.NEGATIVE_INFINITY;
               float var11 = Float.NEGATIVE_INFINITY;
               float var12 = Float.NEGATIVE_INFINITY;

               String var13;
               while ((var13 = var4.readLine()) != null) {
                  var13 = var13.trim();
                  if (var13.startsWith("v ")) {
                     String[] var14 = var13.split("\\s+");
                     float var15 = Float.parseFloat(var14[1]);
                     float var16 = Float.parseFloat(var14[2]) - var1;
                     float var17 = Float.parseFloat(var14[3]);
                     var5.add(new Vector3f(var15, var16, var17));
                     var7 = Math.min(var7, var15);
                     var10 = Math.max(var10, var15);
                     var8 = Math.min(var8, var16);
                     var11 = Math.max(var11, var16);
                     var9 = Math.min(var9, var17);
                     var12 = Math.max(var12, var17);
                  } else if (var13.startsWith("f ")) {
                     method1611(var13.split("\\s+"), var5, var6);
                  }
               }

               float var25 = 0.0F;
               float var26 = 0.0F;
               float var27 = 0.0F;
               float var28 = 1.0F;
               if (var2) {
                  var25 = (var7 + var10) * 0.5F;
                  var26 = (var8 + var11) * 0.5F;
                  var27 = (var9 + var12) * 0.5F;
                  float var18;
                  var28 = (var18 = Math.max(var10 - var7, Math.max(var11 - var8, var12 - var9))) > 1.0E-5F ? 1.0F / var18 : 1.0F;
               }

               ArrayList var29 = new ArrayList();

               for (Object var20_o : (Iterable<?>)(Object)(var6.values())) {
                  Util7.Entry var20 = (Util7.Entry)var20_o;
                  if (var20.count == 1 || !var20.flag) {
                     var29.add((var20.floatValue - var25) * var28);
                     var29.add((var20.floatValue2 - var26) * var28);
                     var29.add((var20.floatValue3 - var27) * var28);
                     var29.add((var20.floatValue4 - var25) * var28);
                     var29.add((var20.floatValue5 - var26) * var28);
                     var29.add((var20.floatValue6 - var27) * var28);
                  }
               }

               float[] var30 = new float[var29.size()];

               for (int var31 = 0; var31 < var29.size(); var31++) {
                  var30[var31] = (Float)var29.get(var31);
               }

               var32 = var30;
            }

            return var32;
         } catch (Exception var23) {
            return null;
         }
      }
   }

   private static void method1611(String[] var0, List<Vector3f> var1, Map<Long, Util7.Entry> var2) {
      for (int var3 = 1; var3 < var0.length; var3++) {
         int var4 = var3 == var0.length - 1 ? 1 : var3 + 1;
         int var5 = Integer.parseInt(var0[var3].split("/")[0]) - 1;
         int var6 = Integer.parseInt(var0[var4].split("/")[0]) - 1;
         if (var5 != var6) {
            String[] var7 = var0[var3].split("/");
            int var8 = var7.length > 2 && !var7[2].isEmpty() ? Integer.parseInt(var7[2]) - 1 : -1;
            long var9 = (long)Math.min(var5, var6) << 32 | Math.max(var5, var6) & 4294967295L;
            Util7.Entry var11 = (Util7.Entry)var2.get(var9);
            if (var11 == null) {
               Vector3f var12 = (Vector3f)var1.get(var5);
               Vector3f var13 = (Vector3f)var1.get(var6);
               var2.put(var9, new Util7.Entry(var12.x, var12.y, var12.z, var13.x, var13.y, var13.z, var8));
            } else {
               var11.count++;
               if (var11.intValue != var8) {
                  var11.flag = false;
               }
            }
         }
      }
   }

   private static final class Entry {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final int intValue;
      private int count = 1;
      private boolean flag = true;

      private Entry(float var1, float var2, float var3, float var4, float var5, float var6, int var7) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
         this.floatValue6 = var6;
         this.intValue = var7;
      }
   }
}

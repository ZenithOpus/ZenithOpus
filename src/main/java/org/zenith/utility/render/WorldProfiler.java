package org.zenith.utility.render;

import java.util.Arrays;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WorldProfiler {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/WProf");
   public static boolean enabled = Boolean.getBoolean("zenith.wprof");
   public static final int CONST_intValue = 0;
   public static final int CONST_intValue2 = 1;
   public static final int CONST_intValue3 = 2;
   public static final int CONST_intValue4 = 3;
   public static final int CONST_intValue5 = 4;
   public static final int CONST_intValue6 = 5;
   public static final int CONST_intValue7 = 6;
   public static final int CONST_intValue8 = 7;
   public static final int CONST_intValue9 = 8;
   public static final int CONST_intValue10 = 9;
   public static final int CONST_intValue11 = 10;
   public static final int CONST_intValue12 = 11;
   public static final int CONST_intValue13 = 12;
   public static final int CONST_intValue14 = 13;
   public static final int CONST_intValue15 = 14;
   public static final int CONST_intValue16 = 15;
   public static final int CONST_intValue17 = 16;
   public static final int CONST_intValue18 = 17;
   public static final int CONST_intValue19 = 18;
   public static final int CONST_intValue20 = 19;
   public static final int CONST_intValue21 = 20;
   public static final int CONST_intValue22 = 21;
   public static final int CONST_intValue23 = 22;
   public static final int CONST_intValue24 = 23;
   public static final int CONST_intValue25 = 24;
   public static final int CONST_intValue26 = 25;
   public static final int CONST_intValue27 = 26;
   public static final int CONST_intValue28 = 27;
   private static final int CONST_intValue29 = 28;
   private static final String[] texts = new String[]{
      "depth.copy",
      "sky (sum)",
      "  sky.shader",
      "  sky.blit",
      "grade (sum)",
      "  grade.shader",
      "  grade.blit",
      "gloss (sum)",
      "  gloss.gloss",
      "  gloss.blurH",
      "  gloss.blurV",
      "  gloss.combine",
      "  gloss.blit",
      "weather.pass",
      "  snow.flush",
      "    snow.upload",
      "  batch.flush",
      "  surfaces",
      "    settling",
      "  footprints",
      "  snow.emit",
      "    snow.grid",
      "    snow.mesh",
      "  mist",
      "  precip",
      "build.sky",
      "build.grade",
      "build.gloss"
   };
   private static final int[] CONST_intValues = new int[28];
   private static final boolean[] CONST_flags = new boolean[28];
   public static final int CONST_intValue30 = 0;
   public static final int CONST_intValue31 = 1;
   public static final int CONST_intValue32 = 2;
   private static final int CONST_intValue33 = 3;
   private static final String[] texts2;
   public static final int CONST_intValue34 = 0;
   public static final int CONST_intValue35 = 1;
   public static final int CONST_intValue36 = 2;
   private static final int[] CONST_intValues2;
   private static final int[] CONST_intValues3;
   private static int intValue;
   private static int intValue2;
   private static final int CONST_intValue37 = 6;
   private static final long CONST_longValue = 3000000000L;
   private static final WorldProfiler.Entry[] entrys;
   private static int slot;
   private static WorldProfiler.Entry entry;
   private static boolean flag;
   private static final long[] CONST_longValues;
   private static final boolean[] CONST_flags2;
   private static final int[] CONST_intValues4;
   private static final long[] CONST_longValues2;
   private static final boolean[] CONST_flags3;
   private static long longValue;
   private static long lastFrame;
   private static int intValue3;
   private static long longValue2;
   private static long longValue3;
   private static int intValue4;
   private static int intValue5;
   private static int intValue6;
   private static final long[] CONST_longValues3;
   private static final long[] CONST_longValues4;
   private static final int[] CONST_intValues5;
   private static final long[] CONST_longValues5;
   private static final long[] CONST_longValues6;
   private static final int[] CONST_intValues6;
   private static final long[] CONST_longValues7;
   private static final int[] CONST_intValues7;

   private WorldProfiler() {
   }

   public static void setEnabled(boolean var0) {
      if (var0 != enabled) {
         enabled = var0;
         flag = true;
         entry = null;
         intValue = -1;

         for (WorldProfiler.Entry var4 : entrys) {
            if (var4 != null) {
               var4.flag = false;
               var4.intValue2 = 0;
               var4.intValue = 0;
            }
         }

         logger.info("[WPROF] {}", var0 ? "enabled" : "disabled");
      }
   }

   public static long method782() {
      return enabled ? System.nanoTime() : 0L;
   }

   public static void method783(int var0, long var1) {
      if (var1 != 0L && enabled) {
         CONST_longValues[var0] = CONST_longValues[var0] + (System.nanoTime() - var1);
         CONST_flags2[var0] = true;
      }
   }

   public static void method784(int var0) {
      if (enabled) {
         CONST_intValues4[var0]++;
      }
   }

   public static int method785(int var0) {
      if (enabled && entry != null) {
         WorldProfiler.Entry var1 = entry;
         int var2 = var1.method798();
         int var3 = var1.method797();
         var1.intValues2[var2] = var0;
         var1.intValues3[var2] = var3;
         var1.intValues4[var2] = -1;
         GL33.glQueryCounter(var1.intValues[var3], 36392);
         var1.longValues[var2] = System.nanoTime();
         return slot << 20 | var2;
      } else {
         return -1;
      }
   }

   public static void method786(int var0) {
      if (var0 >= 0 && enabled && entry != null) {
         WorldProfiler.Entry var1 = entry;
         int var2 = var0 & 1048575;
         if (var0 >>> 20 == slot && var2 < var1.intValue2) {
            long var3 = System.nanoTime();
            int var5 = var1.intValues2[var2];
            CONST_longValues[var5] = CONST_longValues[var5] + (var3 - var1.longValues[var2]);
            CONST_flags2[var5] = true;
            int var6 = var1.method797();
            var1.intValues4[var2] = var6;
            GL33.glQueryCounter(var1.intValues[var6], 36392);
         }
      }
   }

   public static void method787(int var0) {
      if (enabled) {
         intValue = var0;
         intValue2 = 0;
      }
   }

   public static void method788() {
      intValue = -1;
   }

   public static Runnable method789(Runnable var0) {
      if (enabled && intValue >= 0) {
         int var1 = CONST_intValues2[intValue] + Math.min(intValue2, CONST_intValues3[intValue] - 1);
         intValue2++;
         return () -> {
            int var2 = method785(var1);
            var0.run();
            method786(var2);
         };
      } else {
         return var0;
      }
   }

   public static void method790() {
      if (enabled) {
         long var0 = System.nanoTime();
         if (lastFrame != 0L && var0 - lastFrame > 1000000000L) {
            flag = true;
         }

         if (flag) {
            flag = false;
            method794(var0);
            lastFrame = 0L;
            Arrays.fill(CONST_longValues, 0L);
            Arrays.fill(CONST_flags2, false);
            Arrays.fill(CONST_intValues4, 0);
         } else if (lastFrame != 0L) {
            method791(var0 - lastFrame);
         }

         lastFrame = var0;
         if (entry != null) {
            entry.flag = entry.intValue2 > 0;
         }

         method792();
         slot = (slot + 1) % 6;
         WorldProfiler.Entry var2 = entrys[slot];
         if (var2 == null) {
            var2 = new WorldProfiler.Entry();
            entrys[slot] = var2;
         }

         if (var2.flag) {
            intValue5++;
            var2.flag = false;
         }

         var2.intValue2 = 0;
         var2.intValue = 0;
         entry = var2;
         if (var0 - longValue >= 3000000000L) {
            method795(var0);
            method794(var0);
         }
      }
   }

   private static void method791(long var0) {
      intValue3++;
      longValue2 += var0;
      longValue3 = Math.max(longValue3, var0);

      for (int var2 = 0; var2 < 28; var2++) {
         if (CONST_flags2[var2]) {
            long var3 = CONST_longValues[var2];
            CONST_longValues3[var2] = CONST_longValues3[var2] + var3;
            CONST_longValues4[var2] = Math.max(CONST_longValues4[var2], var3);
            CONST_intValues5[var2]++;
            int var5 = CONST_intValues[var2];
            if (var5 >= 0) {
               CONST_longValues[var5] = CONST_longValues[var5] + var3;
            }

            CONST_longValues[var2] = 0L;
            CONST_flags2[var2] = false;
         }
      }

      for (int var6 = 0; var6 < 28; var6++) {
         if (CONST_longValues[var6] != 0L) {
            long var8 = CONST_longValues[var6];
            CONST_longValues3[var6] = CONST_longValues3[var6] + var8;
            CONST_longValues4[var6] = Math.max(CONST_longValues4[var6], var8);
            CONST_intValues5[var6]++;
            CONST_longValues[var6] = 0L;
         }
      }

      for (int var7 = 0; var7 < 3; var7++) {
         CONST_longValues7[var7] = CONST_longValues7[var7] + CONST_intValues4[var7];
         CONST_intValues7[var7] = Math.max(CONST_intValues7[var7], CONST_intValues4[var7]);
         CONST_intValues4[var7] = 0;
      }
   }

   private static void method792() {
      for (int var0 = 1; var0 <= 6; var0++) {
         int var1 = (slot + var0 + 6) % 6;
         WorldProfiler.Entry var2 = entrys[var1];
         if (var2 != null && var2.flag) {
            int var3 = var2.intValues[var2.intValue - 1];
            if (GL15.glGetQueryObjecti(var3, 34919) == 0) {
               return;
            }

            intValue6 = intValue6 + (slot - var1 + 6) % 6 + 1;
            method793(var2);
            var2.flag = false;
         }
      }
   }

   private static void method793(WorldProfiler.Entry var0) {
      for (int var1 = 0; var1 < var0.intValue2; var1++) {
         int var2 = var0.intValues4[var1];
         if (var2 >= 0) {
            long var3 = GL33.glGetQueryObjecti64(var0.intValues[var0.intValues3[var1]], 34918);
            long var5 = GL33.glGetQueryObjecti64(var0.intValues[var2], 34918);
            int var7 = var0.intValues2[var1];
            long var8 = Math.max(0L, var5 - var3);
            CONST_longValues2[var7] = CONST_longValues2[var7] + var8;
            CONST_flags3[var7] = true;
            int var10 = CONST_intValues[var7];
            if (var10 >= 0) {
               CONST_longValues2[var10] = CONST_longValues2[var10] + var8;
               CONST_flags3[var10] = true;
            }
         }
      }

      intValue4++;

      for (int var11 = 0; var11 < 28; var11++) {
         if (CONST_flags3[var11]) {
            long var12 = CONST_longValues2[var11];
            CONST_longValues5[var11] = CONST_longValues5[var11] + var12;
            CONST_longValues6[var11] = Math.max(CONST_longValues6[var11], var12);
            CONST_intValues6[var11]++;
            CONST_longValues2[var11] = 0L;
            CONST_flags3[var11] = false;
         }
      }
   }

   private static void method794(long var0) {
      longValue = var0;
      intValue3 = 0;
      longValue2 = 0L;
      longValue3 = 0L;
      intValue4 = 0;
      intValue5 = 0;
      intValue6 = 0;
      Arrays.fill(CONST_longValues3, 0L);
      Arrays.fill(CONST_longValues4, 0L);
      Arrays.fill(CONST_intValues5, 0);
      Arrays.fill(CONST_longValues5, 0L);
      Arrays.fill(CONST_longValues6, 0L);
      Arrays.fill(CONST_intValues6, 0);
      Arrays.fill(CONST_longValues7, 0L);
      Arrays.fill(CONST_intValues7, 0);
   }

   private static void method795(long var0) {
      if (intValue3 != 0) {
         MinecraftClient var2 = MinecraftClient.getInstance();
         Framebuffer var3 = var2.getFramebuffer();
         String var4 = var3 != null ? var3.textureWidth + "x" + var3.textureHeight : "?";
         double var5 = method796(longValue2) / intValue3;
         double var7 = 0.0;
         double var9 = 0.0;

         for (int var11 = 0; var11 < 28; var11++) {
            if (CONST_flags[var11]) {
               if (intValue4 > 0) {
                  var7 += method796(CONST_longValues5[var11]) / intValue4;
               }

               var9 += method796(CONST_longValues3[var11]) / intValue3;
            }
         }

         logger.info(
            String.format(
               Locale.ROOT,
               "[WPROF] ==== %.1fs | frames %d | frame avg %.3f ms (%.0f fps) max %.3f | res=%s | gpu frames %d lag %.1f dropped %d | weather total/frame: gpu %.3f ms, cpu %.3f ms",
               method796(var0 - longValue) / 1000.0,
               intValue3,
               var5,
               var5 > 0.0 ? 1000.0 / var5 : 0.0,
               method796(longValue3),
               var4,
               intValue4,
               intValue4 > 0 ? (double)intValue6 / intValue4 : 0.0,
               intValue5,
               var7,
               var9
            )
         );

         for (int var13 = 0; var13 < 28; var13++) {
            if (CONST_intValues5[var13] != 0 || CONST_intValues6[var13] != 0) {
               StringBuilder var12 = new StringBuilder(128);
               var12.append("[WPROF] ").append(String.format(Locale.ROOT, "%-18s", texts[var13]));
               if (CONST_intValues6[var13] > 0) {
                  var12.append(
                     String.format(
                        Locale.ROOT,
                        " gpu avg %7.3f max %7.3f",
                        method796(CONST_longValues5[var13]) / CONST_intValues6[var13],
                        method796(CONST_longValues6[var13])
                     )
                  );
               } else {
                  var12.append("                              ");
               }

               if (CONST_intValues5[var13] > 0) {
                  var12.append(
                     String.format(
                        Locale.ROOT,
                        " | cpu avg %7.3f max %7.3f",
                        method796(CONST_longValues3[var13]) / CONST_intValues5[var13],
                        method796(CONST_longValues4[var13])
                     )
                  );
               }

               var12.append(String.format(Locale.ROOT, " | n %d/%d", Math.max(CONST_intValues5[var13], CONST_intValues6[var13]), intValue3));
               logger.info(var12.toString());
            }
         }

         for (int var14 = 0; var14 < 3; var14++) {
            if (CONST_longValues7[var14] != 0L) {
               logger.info(
                  String.format(
                     Locale.ROOT,
                     "[WPROF] %-18s avg %.3f/frame max %d total %d",
                     texts2[var14],
                     (double)CONST_longValues7[var14] / intValue3,
                     CONST_intValues7[var14],
                     CONST_longValues7[var14]
                  )
               );
            }
         }
      }
   }

   private static double method796(long var0) {
      return var0 / 1000000.0;
   }

   static {
      Arrays.fill(CONST_intValues, -1);
      CONST_intValues[2] = 1;
      CONST_intValues[3] = 1;
      CONST_intValues[5] = 4;
      CONST_intValues[6] = 4;

      for (int var0 = 8; var0 <= 12; var0++) {
         CONST_intValues[var0] = 7;
      }

      CONST_flags[0] = true;
      CONST_flags[1] = true;
      CONST_flags[4] = true;
      CONST_flags[7] = true;
      CONST_flags[13] = true;
      CONST_flags[25] = true;
      CONST_flags[26] = true;
      CONST_flags[27] = true;
      texts2 = new String[]{"snow.gridRebuilds", "snow.meshRebuilds", "snow.uploads"};
      CONST_intValues2 = new int[]{2, 5, 8};
      CONST_intValues3 = new int[]{2, 2, 5};
      intValue = -1;
      entrys = new WorldProfiler.Entry[6];
      slot = -1;
      flag = true;
      CONST_longValues = new long[28];
      CONST_flags2 = new boolean[28];
      CONST_intValues4 = new int[3];
      CONST_longValues2 = new long[28];
      CONST_flags3 = new boolean[28];
      CONST_longValues3 = new long[28];
      CONST_longValues4 = new long[28];
      CONST_intValues5 = new int[28];
      CONST_longValues5 = new long[28];
      CONST_longValues6 = new long[28];
      CONST_intValues6 = new int[28];
      CONST_longValues7 = new long[3];
      CONST_intValues7 = new int[3];
   }

   private static final class Entry {
      public int[] intValues = new int[0];
      public int intValue;
      public int[] intValues2 = new int[32];
      public int[] intValues3 = new int[32];
      public int[] intValues4 = new int[32];
      public long[] longValues = new long[32];
      public int intValue2;
      public boolean flag;

      public int method797() {
         if (this.intValue == this.intValues.length) {
            int[] var1 = Arrays.copyOf(this.intValues, Math.max(16, this.intValues.length * 2));

            for (int var2 = this.intValues.length; var2 < var1.length; var2++) {
               var1[var2] = GL15.glGenQueries();
            }

            this.intValues = var1;
         }

         return this.intValue++;
      }

      public int method798() {
         if (this.intValue2 == this.intValues2.length) {
            int var1 = this.intValue2 * 2;
            this.intValues2 = Arrays.copyOf(this.intValues2, var1);
            this.intValues3 = Arrays.copyOf(this.intValues3, var1);
            this.intValues4 = Arrays.copyOf(this.intValues4, var1);
            this.longValues = Arrays.copyOf(this.longValues, var1);
         }

         return this.intValue2++;
      }
   }
}

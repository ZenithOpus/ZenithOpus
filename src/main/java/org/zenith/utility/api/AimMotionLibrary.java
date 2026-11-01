package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class AimMotionLibrary {
   public static final int CONST_intValue = 6;
   private static final String text = "/assets/zenith/aimpipe/aim_motion_library.json";
   private static final Path path = Path.of("aim_motion_library.json");
   private static final Path[] paths = new Path[]{
      Path.of("..", "scripts", "out", "aim_motion_library_6t.json"),
      Path.of("scripts", "out", "aim_motion_library_6t.json"),
      Path.of("..", "scripts", "test", "aimpipe", "out", "aim_motion_library.json"),
      Path.of("scripts", "test", "aimpipe", "out", "aim_motion_library.json")
   };
   private final List<AimMotionLibrary.Entry> items;

   private AimMotionLibrary(List<AimMotionLibrary.Entry> var1) {
      this.items = List.copyOf(var1);
   }

   public List<AimMotionLibrary.Entry> getItems() {
      return this.items;
   }

   public static AimMotionLibrary method1266() {
      AimMotionLibrary var0 = method1267(path);
      if (var0 != null) {
         return var0;
      } else {
         for (Path var4 : paths) {
            var0 = method1267(var4);
            if (var0 != null) {
               return var0;
            }
         }

         return method1268();
      }
   }

   private static AimMotionLibrary method1267(Path var0) {
      try {
         if (!Files.isRegularFile(var0)) {
            return null;
         } else {
            String var1 = Files.readString(var0, StandardCharsets.UTF_8);
            return method1269(var1, var0.toAbsolutePath().toString());
         }
      } catch (Throwable var2) {
         System.err.println("[AimMotionLibrary] failed to load " + var0 + ": " + var2);
         return null;
      }
   }

   private static AimMotionLibrary method1268() {
      try {
         AimMotionLibrary var2;
         try (InputStream var0 = AimMotionLibrary.class.getResourceAsStream("/assets/zenith/aimpipe/aim_motion_library.json")) {
            if (var0 == null) {
               return null;
            }

            String var1 = new String(var0.readAllBytes(), StandardCharsets.UTF_8);
            var2 = method1269(var1, "/assets/zenith/aimpipe/aim_motion_library.json");
         }

         return var2;
      } catch (Throwable var5) {
         System.err.println("[AimMotionLibrary] failed to load /assets/zenith/aimpipe/aim_motion_library.json: " + var5);
         return null;
      }
   }

   private static AimMotionLibrary method1269(String var0, String var1) {
      JsonObject var2 = JsonParser.parseString(var0).getAsJsonObject();
      int var3 = var2.get("format_version").getAsInt();
      if (var3 != 1) {
         throw new IllegalStateException("unsupported motion-library format_version " + var3);
      } else {
         JsonElement var4 = var2.get("source_gcd");
         if (var4 != null && !var4.isJsonNull()) {
            float var5 = var4.getAsFloat();
            if (Float.isFinite(var5) && !(var5 <= 0.0F)) {
               JsonArray var6 = var2.getAsJsonArray("primitives");
               List<Entry> var7 = new ArrayList<>(var6.size());

               for (JsonElement var9 : var6) {
                  JsonObject var10 = var9.getAsJsonObject();
                  int var11 = var10.get("length").getAsInt();
                  if (var11 >= 1 && var11 <= 6) {
                     JsonArray var12 = var10.getAsJsonArray("commands");
                     if (var12.size() != var11) {
                        throw new IllegalStateException("primitive length does not match command count");
                     }

                     int[] var13 = new int[var11];
                     int[] var14 = new int[var11];
                     int var15 = 0;
                     int var16 = 0;

                     for (int var17 = 0; var17 < var11; var17++) {
                        JsonArray var18 = var12.get(var17).getAsJsonArray();
                        if (var18.size() != 2) {
                           throw new IllegalStateException("primitive command must contain yaw and pitch");
                        }

                        var13[var17] = var18.get(0).getAsInt();
                        var14[var17] = var18.get(1).getAsInt();
                        if (var13[var17] == 0 && var14[var17] == 0) {
                           throw new IllegalStateException("primitive contains an internal joint-zero command");
                        }

                        var15 += var13[var17];
                        var16 += var14[var17];
                     }

                     int var20 = var10.get("sum_yaw").getAsInt();
                     int var21 = var10.get("sum_pitch").getAsInt();
                     if (var15 == var20 && var16 == var21) {
                        var7.add(new AimMotionLibrary.Entry(var13, var14, var20, var21));
                        continue;
                     }

                     throw new IllegalStateException("primitive sum does not match commands");
                  }

                  throw new IllegalStateException("primitive length outside 1..6: " + var11);
               }

               if (var7.isEmpty()) {
                  throw new IllegalStateException("motion library contains no primitives");
               } else if (var2.has("count") && var2.get("count").getAsInt() != var7.size()) {
                  throw new IllegalStateException("motion-library count does not match primitive array");
               } else {
                  AimMotionLibrary var19 = new AimMotionLibrary(var7);
                  System.out.println("[AimMotionLibrary] loaded " + var1 + " (primitives=" + var7.size() + ", maxTicks=6)");
                  return var19;
               }
            } else {
               throw new IllegalStateException("motion-library source_gcd must be finite and positive");
            }
         } else {
            throw new IllegalStateException("motion library must declare one source_gcd");
         }
      }
   }

   public static final class Entry {
      private final int[] intValues;
      private final int[] intValues2;
      private final int intValue;
      private final int intValue2;

      private Entry(int[] var1, int[] var2, int var3, int var4) {
         this.intValues = var1;
         this.intValues2 = var2;
         this.intValue = var3;
         this.intValue2 = var4;
      }

      public int length() {
         return this.intValues.length;
      }

      public int method1270(int var1) {
         return this.intValues[var1];
      }

      public int method1271(int var1) {
         return this.intValues2[var1];
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }
}

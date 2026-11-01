package org.zenith.utility.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;

public final class PvpDetector implements MinecraftAccess {
   public static final int CONST_intValue = -2070;
   public static final int CONST_intValue2 = -1921;
   public static final int CONST_intValue3 = -2076;
   public static final int CONST_intValue4 = -1929;
   public static final int CONST_intValue5 = -60;
   public static final int CONST_intValue6 = -35;
   private static final Pattern pattern = Pattern.compile("(\\d{1,3}):(\\d{2})");
   private static final Pattern pattern2 = Pattern.compile("(\\d+):(\\d+)");
   private static final Pattern pattern3 = Pattern.compile("(\\d+)");
   private static final int CONST_intValue7 = 3;
   private static final long CONST_longValue = 600000L;
   public static final long CONST_longValue2 = 20000L;
   private static final Map<Integer, Map<BlockPos, PvpDetector.Entry>> items = new HashMap<>();
   private static List<BlockPos> items2 = Collections.emptyList();
   private static int intValue = Integer.MIN_VALUE;

   private PvpDetector() {
   }

   public static void reset() {
      items2 = Collections.emptyList();
      intValue = Integer.MIN_VALUE;
   }

   private static Map<BlockPos, PvpDetector.Entry> method2716() {
      int var0 = method2724();
      if (var0 < 0) {
         return Collections.emptyMap();
      } else {
         var var1 = items.get(var0);
         return var1 == null ? Collections.emptyMap() : var1;
      }
   }

   private static Map<BlockPos, PvpDetector.Entry> method2717() {
      int var0 = method2724();
      return var0 < 0 ? null : items.computeIfAbsent(var0, var0x -> new HashMap<>());
   }

   public static List<BlockPos> method2718() {
      return new ArrayList<>(method2716().keySet());
   }

   public static boolean method2719(BlockPos var0) {
      return var0 != null && !method2716().containsKey(var0);
   }

   public static boolean method2720(BlockPos var0) {
      PvpDetector.Entry var1 = var0 == null ? null : method2716().get(var0);
      return var1 == null || var1.longValue2 == 0L || var1.method2734() <= 0L;
   }

   public static long method2721(BlockPos var0) {
      return method2720(var0) ? 0L : method2722(var0);
   }

   public static long method2722(BlockPos var0) {
      PvpDetector.Entry var1 = var0 == null ? null : method2716().get(var0);
      return var1 == null ? -1L : var1.method2734();
   }

   public static void method2723(BlockPos var0) {
      var var1 = method2717();
      if (var0 != null && var1 != null) {
         var1.computeIfAbsent(var0.toImmutable(), PvpDetector.Entry::new).method2733();
      }
   }

   public static int method2724() {
      if (mc.player != null && mc.world != null) {
         try {
            return Zenith.getInstance().getServerDetector().getAnarchy();
         } catch (Exception var1) {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public static boolean method2725() {
      if (mc.player != null && mc.inGameHud != null) {
         for (ClientBossBar var1 : mc.inGameHud.getBossBarHud().bossBars.values()) {
            String var2 = var1.getName().getString().toLowerCase(Locale.ROOT);
            if (var2.contains("pvp") || var2.contains("пвп") || var2.contains("дуэль")) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static int method2726() {
      if (mc.player != null && mc.inGameHud != null) {
         for (ClientBossBar var1 : mc.inGameHud.getBossBarHud().bossBars.values()) {
            String var2 = var1.getName().getString().toLowerCase(Locale.ROOT);
            if (var2.contains("pvp") || var2.contains("пвп")) {
               Matcher var3 = pattern2.matcher(var2);
               if (var3.find()) {
                  try {
                     return Integer.parseInt(var3.group(1)) * 60 + Integer.parseInt(var3.group(2));
                  } catch (NumberFormatException var6) {
                     return -1;
                  }
               }

               Matcher var4 = pattern3.matcher(var2);
               if (var4.find()) {
                  try {
                     return Integer.parseInt(var4.group(1));
                  } catch (NumberFormatException var7) {
                     return -1;
                  }
               }
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static boolean method2727() {
      return mc.player != null && mc.world != null
         ? mc.world.getRegistryKey().getValue().toString().equals("minecraft:overworld")
            && mc.player.getX() <= -1921.0
            && mc.player.getX() >= -2070.0
            && mc.player.getZ() <= -1929.0
            && mc.player.getZ() >= -2076.0
         : false;
   }

   public static List<BlockPos> method2728() {
      if (mc.player != null && mc.world != null) {
         if (intValue == mc.player.age) {
            return items2;
         } else {
            intValue = mc.player.age;
            ArrayList<BlockPos> var0 = new ArrayList<>();

            for (int var1 = 65406; var1 <= -121; var1++) {
               for (int var2 = 65406; var2 <= -121; var2++) {
                  if (mc.world.isChunkLoaded(var1, var2)) {
                     WorldChunk var3 = mc.world.getChunk(var1, var2);
                     if (var3 != null) {
                        for (BlockEntity var5 : var3.getBlockEntities().values()) {
                           BlockPos var6 = var5.getPos();
                           if (var6.getY() >= -60
                              && var6.getY() <= -35
                              && var6.getX() >= 63466
                              && var6.getX() <= 63615
                              && var6.getZ() >= 63460
                              && var6.getZ() <= 63607) {
                              BlockEntityType var7 = var5.getType();
                              if (var7 == BlockEntityType.CHEST || var7 == BlockEntityType.TRAPPED_CHEST) {
                                 var0.add(var6.toImmutable());
                              }
                           }
                        }
                     }
                  }
               }
            }

            items2 = var0;
            return var0;
         }
      } else {
         return Collections.emptyList();
      }
   }

   public static void update() {
      if (mc.player != null && mc.world != null) {
         var var0 = method2728();
         var var1 = method2717();
         if (!var0.isEmpty() && var1 != null) {
            for (Entity var3 : mc.world.getEntities()) {
               long var4 = method2732(var3);
               if (var4 >= 0L) {
                  BlockPos var6 = method2730(var0, var3.getBlockPos());
                  if (var6 != null) {
                     var1.computeIfAbsent(var6.toImmutable(), PvpDetector.Entry::new).method2735(var4);
                  }
               }
            }
         }
      }
   }

   public static long method2729(BlockPos var0) {
      if (var0 != null && mc.world != null) {
         long var1 = method2722(var0);
         return var1 <= 0L ? -1L : var1;
      } else {
         return -1L;
      }
   }

   private static BlockPos method2730(List<BlockPos> var0, BlockPos var1) {
      for (BlockPos var3 : var0) {
         if (method2731(var1, var3)) {
            return var3;
         }
      }

      return null;
   }

   private static boolean method2731(BlockPos var0, BlockPos var1) {
      return var0.getX() == var1.getX() && var0.getZ() == var1.getZ() && var0.getY() >= var1.getY() && var0.getY() <= var1.getY() + 3;
   }

   private static long method2732(Entity var0) {
      if (!(var0 instanceof ArmorStandEntity) && !(var0 instanceof TextDisplayEntity)) {
         return -1L;
      } else {
         String var1 = null;
         if (var0 instanceof TextDisplayEntity var2 && var2.getText() != null) {
            var1 = var2.getText().getString();
         }

         if ((var1 == null || var1.isEmpty()) && var0.getCustomName() != null) {
            var1 = var0.getCustomName().getString();
         }

         if (var1 != null && !var1.isEmpty()) {
            Matcher var5 = pattern.matcher(var1);
            if (!var5.find()) {
               return -1L;
            } else {
               try {
                  return (Integer.parseInt(var5.group(1)) * 60L + Integer.parseInt(var5.group(2))) * 1000L;
               } catch (NumberFormatException var4) {
                  return -1L;
               }
            }
         } else {
            return -1L;
         }
      }
   }

   private static final class Entry {
      private long longValue = 0L;
      private long longValue2;

      private Entry(BlockPos var1) {
      }

      private void method2733() {
         this.longValue2 = System.currentTimeMillis();
         this.longValue = this.longValue2 + 600000L;
      }

      private long method2734() {
         return Math.max(0L, this.longValue - System.currentTimeMillis());
      }

      private void method2735(long var1) {
         if (Math.abs(var1 / 1000L - this.method2734() / 1000L) > 5L) {
            this.longValue = System.currentTimeMillis() + var1;
         }
      }
   }
}

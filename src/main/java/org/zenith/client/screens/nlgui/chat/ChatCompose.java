package org.zenith.client.screens.nlgui.chat;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.zenith.Zenith;
import org.zenith.utility.api.Share;
import org.zenith.utility.misc.Coord;
import org.zenith.utility.misc.ServerNameUtils;

public final class ChatCompose {
   private static final int GLOBAL_BUDGET = 380;
   private static final int DIRECT_BUDGET = 860;
   private static final int NODE_BUDGET = 240;
   private static final int MAIN_SIZE = 36;
   private static final int ARMOR_FIRST = 36;
   private static final int OFFHAND_SLOT = 40;
   private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

   private ChatCompose() {
   }

   private static MinecraftClient mc() {
      return MinecraftClient.getInstance();
   }

   public static String server() {
      MinecraftClient var0 = mc();
      if (var0 != null && var0.world != null) {
         ServerInfo var1 = var0.getCurrentServerEntry();
         if (var1 != null && var1.isRealm()) {
            return "realms";
         } else if (var1 != null && var1.address != null && !var1.address.isBlank()) {
            return ServerNameUtils.normalize(var1.address);
         } else {
            return var0.isInSingleplayer() ? "singleplayer" : "";
         }
      } else {
         return "";
      }
   }

   public static String mode() {
      MinecraftClient var0 = mc();
      if (var0 != null && var0.world != null) {
         try {
            Scoreboard var1 = var0.world.getScoreboard();
            ScoreboardObjective var2 = var1.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
            return var2 == null ? "" : ServerNameUtils.method5641(var2.getDisplayName().getString());
         } catch (RuntimeException var3) {
            return "";
         }
      } else {
         return "";
      }
   }

   public static String dimension() {
      MinecraftClient var0 = mc();
      if (var0 != null && var0.world != null) {
         String var1 = var0.world.getRegistryKey().getValue().toString();
         int var2 = var1.indexOf(58);
         return var2 < 0 ? var1 : var1.substring(var2 + 1);
      } else {
         return "";
      }
   }

   public static String whereTag() {
      Zenith var0 = Zenith.getInstance();
      Share var1 = var0 == null ? null : var0.getShare();
      if (var1 != null && !var1.server.isEnabled()) {
         return "";
      } else {
         String var2 = server();
         return var2.isEmpty() ? "" : Coord.method5171("at").method5177("s", var2).method5177("m", mode()).method5177("d", dimension()).toString();
      }
   }

   public static boolean inWorld() {
      MinecraftClient var0 = mc();
      return var0 != null && var0.player != null && var0.world != null;
   }

   public static boolean hasItems() {
      MinecraftClient var0 = mc();
      if (var0 != null && var0.player != null) {
         PlayerInventory var1 = var0.player.getInventory();

         for (int var2 = 0; var2 < var1.getMainStacks().size() && var2 < 36; var2++) {
            if (!var1.getMainStacks().get(var2).isEmpty()) {
               return true;
            }
         }

         for (EquipmentSlot var5 : ARMOR_SLOTS) {
            if (!var0.player.getEquippedStack(var5).isEmpty()) {
               return true;
            }
         }

         return !var0.player.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty();
      } else {
         return false;
      }
   }

   public static String coordsHere(String var0) {
      MinecraftClient var1 = mc();
      if (var1 != null && var1.player != null) {
         BlockPos var2 = var1.player.getBlockPos();
         return coords(var2.getX(), var2.getY(), var2.getZ(), dimension(), var0);
      } else {
         return "";
      }
   }

   public static String coords(int var0, int var1, int var2, String var3, String var4) {
      return Coord.method5171("coord")
         .method5178("x", var0)
         .method5178("y", var1)
         .method5178("z", var2)
         .method5177("d", var3)
         .method5177("s", server())
         .method5177("n", var4)
         .toString();
   }

   public static String inventory(boolean var0, boolean var1) {
      MinecraftClient var2 = mc();
      if (var2 != null && var2.player != null) {
         PlayerInventory var3 = var2.player.getInventory();
         ArrayList var4 = new ArrayList();

         for (int var5 = 0; var5 < var3.getMainStacks().size() && var5 < 36; var5++) {
            add(var4, var5, var3.getMainStacks().get(var5));
         }

         for (int var8 = 0; var8 < ARMOR_SLOTS.length; var8++) {
            add(var4, 36 + var8, var2.player.getEquippedStack(ARMOR_SLOTS[var8]));
         }

         ItemStack var9 = var2.player.getEquippedStack(EquipmentSlot.OFFHAND);
         if (!var9.isEmpty()) {
            add(var4, 40, var9);
         }

         if (var4.isEmpty()) {
            return "";
         } else {
            int var6 = var1 ? (var0 ? 860 : 380) : 240;
            String var7 = pack(var4, var6);
            return var7 == null ? "" : var7;
         }
      } else {
         return "";
      }
   }

   private static void add(List<String> var0, int var1, ItemStack var2) {
      if (var2 != null && !var2.isEmpty()) {
         Identifier var3 = Registries.ITEM.getId(var2.getItem());
         String var4 = var3.getNamespace().equals("minecraft") ? var3.getPath() : var3.toString();
         StringBuilder var5 = new StringBuilder().append(var1).append(':').append(var4).append(':').append(Math.max(1, Math.min(127, var2.getCount())));
         if (var2.hasGlint()) {
            var5.append(":g");
         }

         var0.add(var5.toString());
      }
   }

   private static String pack(List<String> var0, int var1) {
      var var2 = new ArrayList<>(var0);
      String var3 = server();

      while (true) {
         String var4 = Coord.method5171("inv").method5177("s", var3).method5177("v", encode(String.join(";", var2))).toString();
         if (var4.length() <= var1) {
            return var4;
         }

         int var5 = lastBackpack(var2);
         if (var5 < 0) {
            return null;
         }

         var2.remove(var5);
      }
   }

   private static int lastBackpack(List<String> var0) {
      for (int var1 = var0.size() - 1; var1 >= 0; var1--) {
         int var2 = slotOf((String)var0.get(var1));
         if (var2 >= 9 && var2 < 36) {
            return var1;
         }
      }

      for (int var3 = var0.size() - 1; var3 >= 0; var3--) {
         int var4 = slotOf((String)var0.get(var3));
         if (var4 >= 0 && var4 < 9) {
            return var3;
         }
      }

      return -1;
   }

   private static int slotOf(String var0) {
      int var1 = var0.indexOf(58);
      if (var1 <= 0) {
         return -1;
      } else {
         try {
            return Integer.parseInt(var0.substring(0, var1));
         } catch (NumberFormatException var3) {
            return -1;
         }
      }
   }

   private static String encode(String var0) {
      byte[] var1 = var0.getBytes(StandardCharsets.UTF_8);
      Deflater var2 = new Deflater(9);

      String var5;
      try {
         var2.setInput(var1);
         var2.finish();
         byte[] var3 = new byte[Math.max(64, var1.length)];
         int var4 = var2.deflate(var3);
         if (var2.finished()) {
            byte[] var10 = new byte[var4];
            System.arraycopy(var3, 0, var10, 0, var4);
            return "1" + Base64.getUrlEncoder().withoutPadding().encodeToString(var10);
         }

         var5 = "0" + Base64.getUrlEncoder().withoutPadding().encodeToString(var1);
      } finally {
         var2.end();
      }

      return var5;
   }

   private static String decode(String var0) {
      if (var0 != null && var0.length() >= 2) {
         char var1 = var0.charAt(0);

         byte[] var2;
         try {
            var2 = Base64.getUrlDecoder().decode(var0.substring(1));
         } catch (IllegalArgumentException var11) {
            return "";
         }

         if (var1 == '0') {
            return new String(var2, StandardCharsets.UTF_8);
         } else {
            Inflater var3 = new Inflater();

            String var5;
            try {
               var3.setInput(var2);
               byte[] var4 = new byte[4096];
               int var14 = var3.inflate(var4);
               return new String(var4, 0, var14, StandardCharsets.UTF_8);
            } catch (DataFormatException var12) {
               var5 = "";
            } finally {
               var3.end();
            }

            return var5;
         }
      } else {
         return "";
      }
   }

   public static CloudInventoryData inventoryOf(Coord var0) {
      if (var0 == null) {
         return null;
      } else {
         String var1 = decode(var0.method5169("v", ""));
         if (var1.isEmpty()) {
            return null;
         } else {
            ArrayList var2 = new ArrayList();
            ArrayList var3 = new ArrayList();

            for (int var4 = 0; var4 < 36; var4++) {
               var2.add(ItemStack.EMPTY);
            }

            for (int var19 = 0; var19 < 4; var19++) {
               var3.add(ItemStack.EMPTY);
            }

            ItemStack var20 = ItemStack.EMPTY;

            for (String var8 : var1.split(";")) {
               String[] var9 = var8.split(":");
               if (var9.length >= 3) {
                  boolean var10 = var9.length > 3 && "g".equals(var9[var9.length - 1]);
                  int var11 = var10 ? var9.length - 2 : var9.length - 1;
                  if (var11 >= 2) {
                     int var12;
                     int var13;
                     try {
                        var12 = Integer.parseInt(var9[0]);
                        var13 = Integer.parseInt(var9[var11]);
                     } catch (NumberFormatException var18) {
                        continue;
                     }

                     String var14 = String.join(":", Arrays.copyOfRange(var9, 1, var11));
                     Identifier var15 = Identifier.tryParse(var14.indexOf(58) < 0 ? "minecraft:" + var14 : var14);
                     if (var15 != null && Registries.ITEM.containsId(var15)) {
                        Item var16 = Registries.ITEM.get(var15);
                        ItemStack var17 = new ItemStack(var16, Math.max(1, Math.min(127, var13)));
                        if (var10) {
                           var17.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
                        }

                        if (var12 >= 0 && var12 < 36) {
                           var2.set(var12, var17);
                        } else if (var12 >= 36 && var12 < 40) {
                           var3.set(var12 - 36, var17);
                        } else if (var12 == 40) {
                           var20 = var17;
                        }
                     }
                  }
               }
            }

            return new CloudInventoryData(var2, var3, var20);
         }
      }
   }

   public static String dimensionLabel(String var0) {
      if (var0 == null) {
         return "";
      } else {
         String var1 = var0.toLowerCase(Locale.ROOT);

         return switch (var1) {
            case "overworld", "minecraft:overworld" -> "Верхний мир";
            case "the_nether", "minecraft:the_nether" -> "Ад";
            case "the_end", "minecraft:the_end" -> "Край";
            default -> var0;
         };
      }
   }
}

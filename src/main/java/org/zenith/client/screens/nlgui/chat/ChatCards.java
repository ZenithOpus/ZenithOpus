package org.zenith.client.screens.nlgui.chat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.StickerPackService;
import org.zenith.utility.config.ChatImageCache;
import org.zenith.utility.misc.Coord;
import org.zenith.utility.misc.ServerNameUtils;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ChatCards {
   private static final float PAD = 4.0F;
   private static final float CELL = 12.0F;
   private static final float CELL_GAP = 1.0F;
   private static final int COLUMNS = 9;
   private static final float STICKER = 44.0F;
   private static final float COORD_WIDTH = 132.0F;
   private static final float MAP_HEIGHT = 42.0F;
   private static final float MAP_GRID = 9.0F;
   private static final float MAP_CELL = 3.0F;
   private static final int MAP_SCALE = 6;
   private static final int GEAR_SLOTS = 5;
   private static final float GEAR_GAP = 5.0F;
   private static final Map<String, CloudInventoryData> INVENTORIES = new LinkedHashMap<>();
   private static final int MAX_INVENTORIES = 48;
   private static final Map<ChatCards.MapKey, ZenithColor[]> MAPS = new LinkedHashMap<>();
   private static final int MAX_MAPS = 64;
   private static final Map<String, Animation> STICKER_FADE = new ConcurrentHashMap<>();
   private static final List<Runnable> PENDING_COUNTS = new ArrayList<>();

   private ChatCards() {
   }

   public static boolean isCard(Coord var0) {
      return var0 != null && (var0.kind().equals("coord") || var0.kind().equals("inv") || var0.kind().equals("st"));
   }

   public static float width(Coord var0) {
      String var1 = var0.kind();

      return switch (var1) {
         case "coord" -> coordWidth(var0);
         case "inv" -> 133.0F;
         case "st" -> 44.0F;
         default -> 0.0F;
      };
   }

   public static float height(Coord var0) {
      String var1 = var0.kind();

      return switch (var1) {
         case "coord" -> coordHeight(var0);
         case "inv" -> inventoryHeight(var0);
         case "st" -> 44.0F;
         default -> 0.0F;
      };
   }

   public static void render(MenuDrawContext var0, Coord var1, float var2, float var3, float var4, ZenithStyle var5) {
      String var6 = var1.kind();
      switch (var6) {
         case "coord":
            renderCoord(var0, var1, var2, var3, var4, var5);
            break;
         case "inv":
            renderInventory(var0, var1, var2, var3, var4, var5);
            break;
         case "st":
            renderSticker(var0, var1, var2, var3, var4, var5);
      }
   }

   private static Font coordFont() {
      return Fonts.NEW_MEDIUM.getFont(5.4F);
   }

   private static Font underFont() {
      return Fonts.NEW_REGULAR.getFont(4.2F);
   }

   private static String coordLine(Coord var0) {
      return var0.method5170("x", 0) + "  " + var0.method5170("y", 0) + "  " + var0.method5170("z", 0);
   }

   private static String coordUnder(Coord var0) {
      String var1 = ChatCompose.dimensionLabel(var0.method5169("d", ""));
      String var2 = ServerNameUtils.display(var0.method5169("s", ""));
      if (var1.isEmpty()) {
         return var2;
      } else {
         return var2.isEmpty() ? var1 : var1 + " · " + var2;
      }
   }

   private static float coordWidth(Coord var0) {
      return 132.0F;
   }

   private static float coordHeight(Coord var0) {
      float var1 = coordFont().height() + 1.0F + underFont().height();
      if (!var0.method5169("n", "").isEmpty()) {
         var1 += underFont().height() + 1.0F;
      }

      return 46.0F + var1 + 4.0F;
   }

   private static void renderCoord(MenuDrawContext var0, Coord var1, float var2, float var3, float var4, ZenithStyle var5) {
      float var6 = 132.0F;
      float var7 = coordHeight(var1);
      ProxyDotRadiusData var8 = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.5F);
      var0.drawRoundedRect(var2, var3, var6, var7, var8, var5.getPanelLeftBackground().getZenithColor().method7294(var4));
      renderMap(var0, var1, var2, var3, var6, var5, var4);
      var0.drawRect(var2, var3 + 42.0F, var6, 1.0F, var5.getFieldBorder().getZenithColor().method7294(var4 * 2.0F));
      Font var9 = coordFont();
      Font var10 = underFont();
      float var11 = var3 + 42.0F + 4.0F;
      var0.drawText(var9, coordLine(var1), var2 + 4.0F, var11, var5.getTextEnable().getZenithColor().method7294(var4));
      var11 += var9.height() + 1.0F;
      var0.drawText(var10, fit(var10, coordUnder(var1), var6 - 8.0F), var2 + 4.0F, var11, var5.getTextTertiary().getZenithColor().method7294(var4));
      String var12 = var1.method5169("n", "");
      if (!var12.isEmpty()) {
         var0.drawText(
            var10, fit(var10, var12, var6 - 8.0F), var2 + 4.0F, var11 + var10.height() + 1.0F, var5.getTextSecondary().getZenithColor().method7294(var4)
         );
      }

      var0.drawRoundedBorder(var2, var3, var6, var7, 0.15F, var8, var5.getFieldBorder().getZenithColor().method7294(var4 * 2.0F));
   }

   private static void renderMap(MenuDrawContext var0, Coord var1, float var2, float var3, float var4, ZenithStyle var5, float var6) {
      var0.drawRoundedRect(
         var2,
         var3,
         var4,
         42.0F,
         ProxyDotRadiusData.method8075(GuiStyle.ROUND.intValue() * 0.5F),
         var5.getSurfaceEnableBackground().getZenithColor().method7294(var6 * 2.6F)
      );
      int var7 = var1.method5170("x", 0);
      int var8 = var1.method5170("z", 0);
      int var9 = paletteOf(var1.method5169("d", ""));
      int var10 = (int)Math.ceil(var4 / 3.0F);
      int var11 = (int)Math.ceil(14.0);
      ZenithColor[] var12 = mapColors(var7, var8, var9, var10, var11);

      for (int var13 = 0; var13 < var11; var13++) {
         for (int var14 = 0; var14 < var10; var14++) {
            float var15 = var2 + var14 * 3.0F;
            float var16 = var3 + var13 * 3.0F;
            float var17 = Math.min(3.0F, var2 + var4 - var15);
            float var18 = Math.min(3.0F, var3 + 42.0F - var16);
            if (!(var17 <= 0.0F) && !(var18 <= 0.0F)) {
               ZenithColor var19 = var12[var13 * var10 + var14];
               var0.drawRect(var15, var16, var17, var18, var6 >= 1.0F ? var19 : var19.method7294(var6));
            }
         }
      }

      ZenithColor var20 = var5.getFieldBorder().getZenithColor().method7294(var6 * 0.9F);
      float var21 = Math.floorMod(var7, 9);
      float var22 = Math.floorMod(var8, 9);

      for (float var23 = var2 + var21; var23 < var2 + var4; var23 += 9.0F) {
         var0.drawRect(var23, var3, 0.5F, 42.0F, var20);
      }

      for (float var24 = var3 + var22; var24 < var3 + 42.0F; var24 += 9.0F) {
         var0.drawRect(var2, var24, var4, 0.5F, var20);
      }

      ZenithColor var25 = var5.getPanelLeftBackground().getZenithColor().method7294(var6 * 0.55F);

      for (int var26 = 0; var26 < 4; var26++) {
         float var27 = var26;
         ZenithColor var28 = var25.method7294(0.38F - var26 * 0.08F);
         var0.drawRect(var2 + var27, var3 + var27, var4 - var27 * 2.0F, 1.0F, var28);
         var0.drawRect(var2 + var27, var3 + 42.0F - var27 - 1.0F, var4 - var27 * 2.0F, 1.0F, var28);
         var0.drawRect(var2 + var27, var3 + var27, 1.0F, 42.0F - var27 * 2.0F, var28);
         var0.drawRect(var2 + var4 - var27 - 1.0F, var3 + var27, 1.0F, 42.0F - var27 * 2.0F, var28);
      }

      compass(var0, var5, var2 + var4 - 8.0F, var3 + 4.0F, var6);
      scaleBar(var0, var5, var2 + 5.0F, var3 + 42.0F - 6.0F, var6);
      pin(var0, var5, var2 + var4 / 2.0F, var3 + 21.0F + 3.0F, var6);
   }

   private static ZenithColor[] mapColors(int var0, int var1, int var2, int var3, int var4) {
      ChatCards.MapKey var5 = new ChatCards.MapKey(var0, var1, var2, var3, var4);
      synchronized (MAPS) {
         ZenithColor[] var7 = MAPS.get(var5);
         if (var7 != null) {
            return var7;
         }
      }

      int var21 = var0 - var3 / 2 * 6;
      int var22 = var1 - var4 / 2 * 6;
      ZenithColor[] var8 = new ZenithColor[var3 * var4];

      for (int var9 = 0; var9 < var4; var9++) {
         for (int var10 = 0; var10 < var3; var10++) {
            int var11 = var21 + var10 * 6;
            int var12 = var22 + var9 * 6;
            float var13 = terrain(var11, var12);
            float var14 = terrain(var11 + 8123, var12 - 4517);
            float var15 = terrain(var11, var12 - 6);
            int var16 = surface(var2, var13, var14);
            float var17 = Math.clamp(0.5F + (var13 - var15) * 5.5F, 0.18F, 0.92F);
            var8[var9 * var3 + var10] = ZenithColor.method7291(shade(var16, 0.78F + 0.44F * var17));
         }
      }

      synchronized (MAPS) {
         if (MAPS.size() >= 64) {
            MAPS.remove(MAPS.keySet().iterator().next());
         }

         MAPS.put(var5, var8);
         return var8;
      }
   }

   private static int paletteOf(String var0) {
      String var1 = var0 == null ? "" : var0;
      if (var1.contains("nether")) {
         return 1;
      } else {
         return var1.contains("end") ? 2 : 0;
      }
   }

   private static float terrain(int var0, int var1) {
      float var2 = smooth(var0 / 96.0F, var1 / 96.0F) * 0.62F
         + smooth(var0 / 34.0F + 11.7F, var1 / 34.0F - 4.1F) * 0.28F
         + smooth(var0 / 13.0F - 7.3F, var1 / 13.0F + 2.9F) * 0.1F;
      return Math.clamp(var2, 0.0F, 1.0F);
   }

   private static float smooth(float var0, float var1) {
      int var2 = (int)Math.floor(var0);
      int var3 = (int)Math.floor(var1);
      float var4 = var0 - var2;
      float var5 = var1 - var3;
      var4 = var4 * var4 * (3.0F - 2.0F * var4);
      var5 = var5 * var5 * (3.0F - 2.0F * var5);
      float var6 = spot(var2, var3);
      float var7 = spot(var2 + 1, var3);
      float var8 = spot(var2, var3 + 1);
      float var9 = spot(var2 + 1, var3 + 1);
      return (var6 * (1.0F - var4) + var7 * var4) * (1.0F - var5) + (var8 * (1.0F - var4) + var9 * var4) * var5;
   }

   private static float spot(int var0, int var1) {
      int var2 = var0 * 73856093 ^ var1 * 19349663;
      var2 ^= var2 >>> 13;
      var2 *= 1274126177;
      var2 ^= var2 >>> 16;
      return (var2 >>> 8 & 65535) / 65535.0F;
   }

   private static int surface(int var0, float var1, float var2) {
      if (var0 == 1) {
         if (var1 < 0.34F) {
            return 2755594;
         } else if (var1 < 0.44F) {
            return 7215632;
         } else if (var1 < 0.62F) {
            return var2 < 0.45F ? 9185814 : 8004627;
         } else {
            return var1 < 0.78F ? 4860450 : 7035474;
         }
      } else if (var0 == 2) {
         if (var1 < 0.38F) {
            return 723474;
         } else if (var1 < 0.46F) {
            return 1709862;
         } else if (var1 < 0.7F) {
            return var2 < 0.5F ? 13222026 : 12432510;
         } else {
            return 14209702;
         }
      } else if (var1 < 0.34F) {
         return 2902650;
      } else if (var1 < 0.42F) {
         return 4023961;
      } else if (var1 < 0.45F) {
         return 13089930;
      } else if (var1 < 0.6F) {
         return var2 < 0.42F ? 7245642 : 6061886;
      } else if (var1 < 0.72F) {
         return var2 < 0.5F ? 4153648 : 3494442;
      } else {
         return var1 < 0.84F ? 7236194 : 14081252;
      }
   }

   private static int shade(int var0, float var1) {
      int var2 = Math.clamp((long)((int)((var0 >> 16 & 0xFF) * var1)), 0, 255);
      int var3 = Math.clamp((long)((int)((var0 >> 8 & 0xFF) * var1)), 0, 255);
      int var4 = Math.clamp((long)((int)((var0 & 0xFF) * var1)), 0, 255);
      return 0xFF000000 | var2 << 16 | var3 << 8 | var4;
   }

   private static void compass(MenuDrawContext var0, ZenithStyle var1, float var2, float var3, float var4) {
      ZenithColor var5 = var1.getTextTertiary().getZenithColor().method7294(var4 * 1.6F);

      for (int var6 = 0; var6 < 3; var6++) {
         float var7 = 1.0F + var6 * 2.0F;
         var0.drawRect(var2 - var7 / 2.0F, var3 + var6, var7, 1.0F, var5);
      }

      var0.drawRect(var2 - 0.5F, var3 + 3.0F, 1.0F, 4.0F, var5);
   }

   private static void scaleBar(MenuDrawContext var0, ZenithStyle var1, float var2, float var3, float var4) {
      ZenithColor var5 = var1.getTextTertiary().getZenithColor().method7294(var4 * 1.4F);
      float var6 = 18.0F;
      var0.drawRect(var2, var3 + 2.0F, var6, 0.8F, var5);
      var0.drawRect(var2, var3, 0.8F, 3.0F, var5);
      var0.drawRect(var2 + var6 - 0.8F, var3, 0.8F, 3.0F, var5);
   }

   private static void pin(MenuDrawContext var0, ZenithStyle var1, float var2, float var3, float var4) {
      ZenithColor var5 = var1.getPrimaryColor().getZenithColor();
      float var6 = 7.0F;
      float var7 = var3 - 5.0F - var6;

      for (int var8 = 3; var8 >= 1; var8--) {
         float var9 = 3.0F + var8 * 2.6F;
         var0.drawRoundedRect(
            var2 - var9 / 2.0F, var3 - var9 / 4.0F, var9, var9 / 2.0F, ProxyDotRadiusData.method8069(var9 / 4.0F), var5.method7294(var4 * 0.07F * var8)
         );
      }

      for (int var10 = 0; var10 < 5; var10++) {
         float var12 = 5.0F - var10;
         var0.drawRect(var2 - var12 / 2.0F, var7 + var6 - 1.0F + var10, var12, 1.0F, var5.method7294(var4));
      }

      var0.drawRoundedRect(var2 - var6 / 2.0F, var7, var6, var6, ProxyDotRadiusData.method8069(var6 / 2.0F), var5.method7294(var4));
      float var11 = 2.6F;
      var0.drawRoundedRect(
         var2 - var11 / 2.0F,
         var7 + (var6 - var11) / 2.0F,
         var11,
         var11,
         ProxyDotRadiusData.method8069(var11 / 2.0F),
         var1.getPanelLeftBackground().getZenithColor().method7294(var4 * 4.0F)
      );
   }

   private static String fit(Font var0, String var1, float var2) {
      if (var1 != null && !(var2 <= 0.0F)) {
         if (var0.width(var1) <= var2) {
            return var1;
         } else {
            float var3 = var0.width("…");
            if (var3 > var2) {
               return "";
            } else {
               int var4 = var1.length();

               while (var4 > 0 && var0.width(var1.substring(0, var4)) + var3 > var2) {
                  var4--;
               }

               return var4 == 0 ? "" : var1.substring(0, var4) + "…";
            }
         }
      } else {
         return "";
      }
   }

   public static String coordText(Coord var0) {
      return var0.method5170("x", 0) + " " + var0.method5170("y", 0) + " " + var0.method5170("z", 0);
   }

   private static CloudInventoryData inventory(Coord var0) {
      String var1 = var0.method5169("v", "");
      if (var1.isEmpty()) {
         return null;
      } else {
         synchronized (INVENTORIES) {
            CloudInventoryData var3 = INVENTORIES.get(var1);
            if (var3 != null) {
               return var3;
            }
         }

         CloudInventoryData var8 = ChatCompose.inventoryOf(var0);
         if (var8 == null) {
            return null;
         } else {
            synchronized (INVENTORIES) {
               if (INVENTORIES.size() >= 48) {
                  INVENTORIES.remove(INVENTORIES.keySet().iterator().next());
               }

               INVENTORIES.put(var1, var8);
               return var8;
            }
         }
      }
   }

   private static int backpackRows(CloudInventoryData var0) {
      int var1 = -1;

      for (int var2 = 9; var2 < 36; var2++) {
         if (!var0.main().get(var2).isEmpty()) {
            var1 = var2;
         }
      }

      return var1 < 0 ? 0 : (var1 - 9) / 9 + 1;
   }

   private static float inventoryHeight(Coord var0) {
      CloudInventoryData var1 = inventory(var0);
      if (var1 == null) {
         return 0.0F;
      } else {
         Font var2 = underFont();
         int var3 = Math.max(5, 1 + backpackRows(var1));
         return 8.0F + var2.height() + 2.0F + var3 * 12.0F;
      }
   }

   private static void renderInventory(MenuDrawContext var0, Coord var1, float var2, float var3, float var4, ZenithStyle var5) {
      CloudInventoryData var6 = inventory(var1);
      if (var6 != null) {
         float var7 = width(var1);
         float var8 = inventoryHeight(var1);
         ProxyDotRadiusData var9 = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.5F);
         var0.drawRoundedRect(var2, var3, var7, var8, var9, var5.getPanelLeftBackground().getZenithColor().method7294(var4));
         Font var10 = underFont();
         float var11 = var3 + 4.0F;
         MenuGlyph.CHESTPLATE.draw(var0, var2 + 4.0F, var11 + (var10.height() - 6.0F) / 2.0F, var5.getTextTertiary().getZenithColor().method7294(var4));
         var0.drawText(var10, "инвентарь", var2 + 4.0F + 6.0F + 3.0F, var11, var5.getTextTertiary().getZenithColor().method7294(var4));
         String var12 = ServerNameUtils.display(var1.method5169("s", ""));
         if (!var12.isEmpty()) {
            String var13 = fit(var10, var12, var7 - 8.0F - 6.0F - 3.0F - var10.width("инвентарь") - 6.0F);
            var0.drawText(var10, var13, var2 + var7 - 4.0F - var10.width(var13), var11, var5.getTextTertiary().getZenithColor().method7294(var4 * 0.7F));
         }

         float var23 = var3 + 4.0F + var10.height() + 3.0F;
         var0.drawRect(var2 + 4.0F, var23 - 2.0F, var7 - 8.0F, 1.0F, var5.getFieldBorder().getZenithColor().method7294(var4 * 1.6F));
         float var14 = var2 + 4.0F;
         float var15 = var14 + 12.0F + 5.0F;

         for (int var16 = 0; var16 < 2; var16++) {
            boolean var17 = var16 == 1;
            var var18 = var6.armor();

            for (int var19 = 0; var19 < var18.size() && var19 < 4; var19++) {
               slot(var0, (ItemStack)var18.get(var18.size() - 1 - var19), var14, var23 + var19 * 12.0F, var4, var5, var17);
            }

            slot(var0, var6.offhand(), var14, var23 + 48.0F, var4, var5, var17);
            if (!var17) {
               var0.drawRect(var14 + 12.0F + 2.5F, var23 + 1.0F, 1.0F, 58.0F, var5.getFieldBorder().getZenithColor().method7294(var4 * 1.6F));
               var0.drawRect(var15 - 2.5F + 1.5F, var23 + 2.0F, 1.0F, 8.0F, var5.getPrimaryColor().getZenithColor().method7294(var4 * 0.8F));
            }

            for (int var24 = 0; var24 < 9; var24++) {
               slot(var0, var6.hotbar(var24), var15 + var24 * 12.0F, var23, var4, var5, var17);
            }

            int var25 = backpackRows(var6);

            for (int var20 = 0; var20 < var25; var20++) {
               for (int var21 = 0; var21 < 9; var21++) {
                  ItemStack var22 = var6.main().get(9 + var20 * 9 + var21);
                  slot(var0, var22, var15 + var21 * 12.0F, var23 + 12.0F + var20 * 12.0F, var4, var5, var17);
               }
            }
         }

         if (!PENDING_COUNTS.isEmpty()) {
            ScissorFlusher.flush();
            PENDING_COUNTS.forEach(Runnable::run);
            PENDING_COUNTS.clear();
         }

         var0.drawRoundedBorder(var2, var3, var7, var8, 0.15F, var9, var5.getFieldBorder().getZenithColor().method7294(var4 * 2.0F));
      }
   }

   private static void slot(MenuDrawContext var0, ItemStack var1, float var2, float var3, float var4, ZenithStyle var5, boolean var6) {
      boolean var7 = var1 != null && !var1.isEmpty();
      float var8 = 11.0F;
      if (var6) {
         if (var7 && var4 >= 0.35F) {
            item(var0, var1, var2, var3, var4);
         }
      } else {
         var0.drawRoundedRect(
            var2 + 0.5F,
            var3 + 0.5F,
            var8,
            var8,
            ProxyDotRadiusData.method8069(1.5F),
            var5.getSurfaceEnableBackground().getZenithColor().method7294(var4 * (var7 ? 2.4F : 1.1F))
         );
         if (var7) {
            var0.drawRoundedBorder(
               var2 + 0.5F, var3 + 0.5F, var8, var8, 0.15F, ProxyDotRadiusData.method8069(1.5F), var5.getFieldBorder().getZenithColor().method7294(var4 * 1.8F)
            );
         }
      }
   }

   private static void item(MenuDrawContext var0, ItemStack var1, float var2, float var3, float var4) {
      var0.pushMatrix();
      float var5 = 0.5625F;
      var0.getMatrices().translate(var2 + 0.5F + 1.0F, var3 + 0.5F + 1.0F);
      var0.getMatrices().scale(var5, var5);
      var0.drawItem(var1, 0, 0);
      var0.drawItemBar(var1, 0, 0);
      var0.popMatrix();
      if (var1.getCount() > 1) {
         Font var6 = Fonts.NEW_MEDIUM.getFont(3.6F);
         String var7 = Integer.toString(var1.getCount());
         PENDING_COUNTS.add(
            () -> var0.drawText(
               var6, var7, var2 + 12.0F - 0.5F - 1.0F - var6.width(var7), var3 + 12.0F - 0.5F - 1.0F - var6.height(), ZenithColor.INSTANCE.method7294(var4)
            )
         );
      }
   }

   private static void renderSticker(MenuDrawContext var0, Coord var1, float var2, float var3, float var4, ZenithStyle var5) {
      String var6 = StickerPackService.getInstance().method5507(var1.method5169("p", ""), var1.method5169("i", ""));
      Identifier var7 = ChatImageCache.get(var6);
      ProxyDotRadiusData var8 = ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() * 0.5F);
      if (var7 == null) {
         float var11 = 0.7F + 0.3F * (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 300.0));
         var0.drawRoundedRect(var2, var3, 44.0F, 44.0F, var8, var5.getSurfaceEnableBackground().getZenithColor().method7294(var4 * 1.4F * var11));
      } else {
         if (STICKER_FADE.size() > 512 && !STICKER_FADE.containsKey(var6)) {
            STICKER_FADE.clear();
         }

         float var9 = STICKER_FADE.computeIfAbsent(var6, var0x -> new Animation(240L, 0.0F, Easing.INSTANCE12)).method2808(1.0F);
         if (var9 < 1.0F) {
            var0.drawRoundedRect(var2, var3, 44.0F, 44.0F, var8, var5.getSurfaceEnableBackground().getZenithColor().method7294(var4 * 1.4F * (1.0F - var9)));
         }

         float var10 = 44.0F * (0.86F + 0.14F * var9);
         drawSticker(
            var0,
            var1.method5169("i", ""),
            var7,
            var2 + (44.0F - var10) / 2.0F,
            var3 + (44.0F - var10) / 2.0F,
            var10,
            var8,
            ZenithColor.INSTANCE.method7294(var4 * var9)
         );
      }
   }

   public static void drawSticker(
      MenuDrawContext var0, String var1, Identifier var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7
   ) {
      StickerPackService.Data3 var8 = StickerPackService.method5504(var1);
      if (var8 == null) {
         var0.drawRoundedTexture(var2, var3, var4, var5, var5, var6, var7);
      } else {
         int var9 = var8.method5511(System.currentTimeMillis());
         float var10 = 1.0F / var8.columns();
         float var11 = 1.0F / var8.rows();
         float var12 = var9 % var8.columns() * var10;
         float var13 = var9 / var8.columns() * var11;
         int var14 = ChatImageCache.method1488(var2);
         int var15 = ChatImageCache.method1489(var2);
         float var16 = var14 > 0 ? 0.5F / var14 : 0.0F;
         float var17 = var15 > 0 ? 0.5F / var15 : 0.0F;
         var0.drawRoundedTexture(var2, var3, var4, var5, var5, var6, var7, var12 + var16, var13 + var17, var12 + var10 - var16, var13 + var11 - var17);
      }
   }

   private static final class MapKey {
      private final int worldX;
      private final int worldZ;
      private final int palette;
      private final int columns;
      private final int rows;

      public int worldX() {
         return this.worldX;
      }

      public int worldZ() {
         return this.worldZ;
      }

      public int palette() {
         return this.palette;
      }

      public int columns() {
         return this.columns;
      }

      public int rows() {
         return this.rows;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof MapKey other)) return false;
         return java.util.Objects.equals(this.worldX, other.worldX) && java.util.Objects.equals(this.worldZ, other.worldZ) && java.util.Objects.equals(this.palette, other.palette) && java.util.Objects.equals(this.columns, other.columns) && java.util.Objects.equals(this.rows, other.rows);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(worldX, worldZ, palette, columns, rows);
      }

      @Override
      public String toString() {
         return "MapKey[" + "worldX=" + this.worldX + ", " + "worldZ=" + this.worldZ + ", " + "palette=" + this.palette + ", " + "columns=" + this.columns + ", " + "rows=" + this.rows + "]";
      }

      private MapKey(int var1, int var2, int var3, int var4, int var5) {
         this.worldX = var1;
         this.worldZ = var2;
         this.palette = var3;
         this.columns = var4;
         this.rows = var5;
      }
   }
}

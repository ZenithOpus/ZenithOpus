package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public enum AutoPotionBrewingEnum {
   FIRE_RESISTANCE("module.autoPotionBrewing.fireResistance", Items.MAGMA_CREAM, Items.REDSTONE),
   INVISIBILITY("module.autoPotionBrewing.invisibility", Items.GOLDEN_CARROT, Items.FERMENTED_SPIDER_EYE, Items.REDSTONE),
   STRENGTH("module.autoPotionBrewing.strength", Items.BLAZE_POWDER, Items.GLOWSTONE_DUST),
   SPEED("module.autoPotionBrewing.speed", Items.SUGAR, Items.GLOWSTONE_DUST),
   JUMP_BOOST("module.autoPotionBrewing.jumpBoost", Items.RABBIT_FOOT, Items.REDSTONE);

   public final String text;
   private final List<Item> items;

   private AutoPotionBrewingEnum(String var3, Item... var4) {
      this.text = var3;
      this.items = List.of(var4);
   }

   public List<Item> method6() {
      List<Item> var1 = new ArrayList<>(this.items.size() + 1);
      var1.add(Items.NETHER_WART);
      var1.addAll(this.items);
      return var1;
   }

   public static AutoPotionBrewingEnum method7(String var0) {
      for (AutoPotionBrewingEnum var4 : values()) {
         if (var4.text.equals(var0)) {
            return var4;
         }
      }

      return FIRE_RESISTANCE;
   }
}

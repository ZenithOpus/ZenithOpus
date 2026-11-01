package org.zenith.utility.game;

import java.util.Locale;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import org.zenith.utility.misc.WardrobeCategory;

public enum FocusSlotEnum {
   PET(WardrobeCategory.PET, null, null),
   HEAD(WardrobeCategory.CLOTHING, null, null),
   BODY(WardrobeCategory.CLOTHING, null, null),
   LEGS(WardrobeCategory.CLOTHING, null, null),
   BACK(WardrobeCategory.CLOTHING, null, null),
   CAPE(WardrobeCategory.CAPE, null, null),
   WATCH(WardrobeCategory.ACCESSORY, null, null),
   NECK(WardrobeCategory.ACCESSORY, null, null),
   CHARACTER(WardrobeCategory.CHARACTER, null, null),
   SWORD(WardrobeCategory.WEAPON, ItemTags.SWORDS, null),
   AXE(WardrobeCategory.WEAPON, ItemTags.AXES, null),
   BOW(WardrobeCategory.WEAPON, null, Items.BOW),
   MACE(WardrobeCategory.WEAPON, null, Items.MACE),
   SPEAR(WardrobeCategory.WEAPON, ItemTags.SPEARS, null),
   CROSSBOW(WardrobeCategory.WEAPON, null, Items.CROSSBOW),
   GAPPLE(WardrobeCategory.ITEM, null, Items.GOLDEN_APPLE),
   ENCHANTED_GAPPLE(WardrobeCategory.ITEM, null, Items.ENCHANTED_GOLDEN_APPLE),
   TOTEM(WardrobeCategory.ITEM, null, Items.TOTEM_OF_UNDYING),
   ANCHOR(WardrobeCategory.ITEM, null, Items.RESPAWN_ANCHOR),
   CRYSTAL(WardrobeCategory.ITEM, null, Items.END_CRYSTAL);

   private static final FocusSlotEnum[] focusSlotEnums = new FocusSlotEnum[]{
      SWORD, AXE, BOW, MACE, SPEAR, CROSSBOW, GAPPLE, ENCHANTED_GAPPLE, TOTEM, ANCHOR, CRYSTAL
   };
   private final WardrobeCategory wardrobeCategory;
   private final TagKey<Item> tagKey;
   private final Item item;

   private FocusSlotEnum(WardrobeCategory var3, TagKey<Item> var4, Item var5) {
      this.wardrobeCategory = var3;
      this.tagKey = var4;
      this.item = var5;
   }

   public WardrobeCategory getWardrobeCategory() {
      return this.wardrobeCategory;
   }

   public boolean method542() {
      return this.wardrobeCategory == WardrobeCategory.WEAPON;
   }

   public boolean method543() {
      return this.wardrobeCategory == WardrobeCategory.WEAPON || this.wardrobeCategory == WardrobeCategory.ITEM;
   }

   public boolean matches(ItemStack var1) {
      return var1 != null && !var1.isEmpty() ? this.tagKey != null && var1.isIn(this.tagKey) || this.item != null && var1.isOf(this.item) : false;
   }

   public ItemStack method544() {
      return switch (this) {
         case SWORD -> new ItemStack(Items.DIAMOND_SWORD);
         case AXE -> new ItemStack(Items.DIAMOND_AXE);
         case BOW -> new ItemStack(Items.BOW);
         case MACE -> new ItemStack(Items.MACE);
         case SPEAR -> new ItemStack(Items.IRON_SPEAR);
         case CROSSBOW -> new ItemStack(Items.CROSSBOW);
         case GAPPLE -> new ItemStack(Items.GOLDEN_APPLE);
         case ENCHANTED_GAPPLE -> new ItemStack(Items.ENCHANTED_GOLDEN_APPLE);
         case TOTEM -> new ItemStack(Items.TOTEM_OF_UNDYING);
         case ANCHOR -> new ItemStack(Items.RESPAWN_ANCHOR);
         case CRYSTAL -> new ItemStack(Items.END_CRYSTAL);
         default -> ItemStack.EMPTY;
      };
   }

   public static FocusSlotEnum method545(ItemStack var0) {
      if (var0 != null && !var0.isEmpty()) {
         for (FocusSlotEnum var4 : focusSlotEnums) {
            if (var4.matches(var0)) {
               return var4;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public String key() {
      return "wardrobe.slot." + this.name().toLowerCase(Locale.ROOT);
   }

   public static FocusSlotEnum method546(String var0) {
      if (var0 == null) {
         return null;
      } else {
         try {
            return valueOf(var0.strip().toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var2) {
            return null;
         }
      }
   }
}

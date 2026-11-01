package org.zenith.utility.game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import org.zenith.utility.misc.EnchantTranslations;

public class HolyWorldItems {
   private final ArrayList<ItemMatcher> arrayList = new ArrayList<>();
   private final ArrayList<ItemMatcher> arrayList2 = new ArrayList<>();
   private final ArrayList<ItemMatcher> arrayList3 = new ArrayList<>();
   private final ArrayList<ItemMatcher> arrayList4 = new ArrayList<>();

   public HolyWorldItems() {
      this.init();
   }

   private void init() {
      var var1 = EnchantTranslations.method4710(
         "[EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:respiration, level=3], EnchantVanilla [checked=minecraft:mending, level=1], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:aqua_affinity, level=1]]"
      );
      EnchantedItemMatcher var2 = new EnchantedItemMatcher(Items.NETHERITE_HELMET.getDefaultStack(), "Шлем крушителя", ItemMatcher.Type.FUNTIME);
      var1.forEach(var2::method4844);
      var var3 = EnchantTranslations.method4710(
         "[EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:mending, level=1]]"
      );
      EnchantedItemMatcher var4 = new EnchantedItemMatcher(Items.NETHERITE_CHESTPLATE.getDefaultStack(), "Нагрудник Крушителя", ItemMatcher.Type.FUNTIME);
      var3.forEach(var4::method4844);
      EnchantedItemMatcher var5 = new EnchantedItemMatcher(Items.NETHERITE_LEGGINGS.getDefaultStack(), "Поножи Крушителя", ItemMatcher.Type.FUNTIME);
      var3.forEach(var5::method4844);
      var var6 = EnchantTranslations.method4710(
         "[EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:soul_speed, level=3], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5], EnchantVanilla [checked=minecraft:depth_strider, level=3], EnchantVanilla [checked=minecraft:feather_falling, level=4], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:mending, level=1]]"
      );
      EnchantedItemMatcher var7 = new EnchantedItemMatcher(Items.NETHERITE_BOOTS.getDefaultStack(), "Ботинки Крушителя", ItemMatcher.Type.FUNTIME);
      var6.forEach(var7::method4844);
      var var8 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=oxidation, level=2], EnchantCustom [checked=detection, level=3], EnchantCustom [checked=poison, level=3], EnchantCustom [checked=vampirism, level=2], EnchantCustom [checked=skilled, level=3], EnchantVanilla [checked=minecraft:looting, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:fire_aspect, level=2], EnchantVanilla [checked=minecraft:sweeping_edge, level=3], EnchantVanilla [checked=minecraft:smite, level=7], EnchantVanilla [checked=minecraft:sharpness, level=7], EnchantVanilla [checked=minecraft:bane_of_arthropods, level=7], EnchantVanilla [checked=minecraft:mending, level=1]]"
      );
      EnchantedItemMatcher var9 = new EnchantedItemMatcher(Items.NETHERITE_SWORD.getDefaultStack(), "Меч Крушителя", ItemMatcher.Type.FUNTIME);
      var8.forEach(var9::method4844);
      var var10 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=skilled, level=3], EnchantCustom [checked=smelting, level=1], EnchantCustom [checked=magnet, level=1], EnchantCustom [checked=pinger, level=1], EnchantCustom [checked=web, level=1], EnchantCustom [checked=buldozing, level=2], EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:efficiency, level=10], EnchantVanilla [checked=minecraft:mending, level=1], EnchantVanilla [checked=minecraft:fortune, level=5]]"
      );
      EnchantedItemMatcher var11 = new EnchantedItemMatcher(Items.NETHERITE_PICKAXE.getDefaultStack(), "Кирка Крушителя", ItemMatcher.Type.FUNTIME);
      var10.forEach(var11::method4844);
      var var12 = EnchantTranslations.method4710(
         "[EnchantVanilla [checked=minecraft:unbreaking, level=3], EnchantVanilla [checked=minecraft:mending, level=1], EnchantVanilla [checked=minecraft:multishot, level=1], EnchantVanilla [checked=minecraft:piercing, level=5], EnchantVanilla [checked=minecraft:quick_charge, level=3]]"
      );
      EnchantedItemMatcher var13 = new EnchantedItemMatcher(Items.CROSSBOW.getDefaultStack(), "Арбалет Крушителя", ItemMatcher.Type.FUNTIME);
      var12.forEach(var13::method4844);
      var var14 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=detection, level=3], EnchantCustom [checked=poison, level=3], EnchantCustom [checked=demolishing, level=1], EnchantCustom [checked=returning, level=1], EnchantCustom [checked=oxidation, level=2], EnchantCustom [checked=pulling, level=2], EnchantCustom [checked=stupor, level=3], EnchantCustom [checked=vampirism, level=2], EnchantCustom [checked=skilled, level=3], EnchantCustom [checked=scout, level=3], EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:fire_aspect, level=2], EnchantVanilla [checked=minecraft:loyalty, level=3], EnchantVanilla [checked=minecraft:impaling, level=5], EnchantVanilla [checked=minecraft:channeling, level=1], EnchantVanilla [checked=minecraft:sharpness, level=7], EnchantVanilla [checked=minecraft:mending, level=1]]"
      );
      EnchantedItemMatcher var15 = new EnchantedItemMatcher(Items.TRIDENT.getDefaultStack(), "Трезубец Крушителя", ItemMatcher.Type.FUNTIME);
      var14.forEach(var15::method4844);
      ItemStack var16 = Items.SPLASH_POTION.getDefaultStack();
      var16.set(
         DataComponentTypes.POTION_CONTENTS,
         new PotionContentsComponent(Optional.of(Potions.SWIFTNESS), Optional.of(new Color(214, 0, 191).getRGB()), List.of(), Optional.empty())
      );
      PotionItemMatcher var17 = new PotionItemMatcher(
         var16,
         "Зелье Медика",
         ItemMatcher.Type.FUNTIME,
         "custom_potion_effects",
         "[{ambient:0b,amplifier:2b,duration:900,id:\"minecraft:health_boost\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:2b,duration:900,id:\"minecraft:regeneration\",show_icon:1b,show_particles:1b}]"
      );
      ItemStack var18 = Items.SPLASH_POTION.getDefaultStack();
      var18.set(
         DataComponentTypes.POTION_CONTENTS,
         new PotionContentsComponent(Optional.of(Potions.SWIFTNESS), Optional.of(new Color(11, 188, 4).getRGB()), List.of(), Optional.empty())
      );
      PotionItemMatcher var19 = new PotionItemMatcher(
         var18,
         "Зелье Победителя",
         ItemMatcher.Type.FUNTIME,
         "custom_potion_effects",
         "[{ambient:0b,amplifier:1b,duration:3600,id:\"minecraft:health_boost\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:0b,duration:18000,id:\"minecraft:invisibility\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:1b,duration:1200,id:\"minecraft:regeneration\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:0b,duration:1200,id:\"minecraft:resistance\",show_icon:1b,show_particles:1b}]"
      );
      ItemStack var20 = Items.SPLASH_POTION.getDefaultStack();
      var20.set(
         DataComponentTypes.POTION_CONTENTS,
         new PotionContentsComponent(Optional.of(Potions.SWIFTNESS), Optional.of(new Color(246, 250, 78).getRGB()), List.of(), Optional.empty())
      );
      PotionItemMatcher var21 = new PotionItemMatcher(
         var20,
         "Зелье Агента",
         ItemMatcher.Type.FUNTIME,
         "custom_potion_effects",
         "[{ambient:0b,amplifier:0b,duration:18000,id:\"minecraft:fire_resistance\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:0b,duration:3600,id:\"minecraft:haste\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:0b,duration:18000,id:\"minecraft:invisibility\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:2b,duration:18000,id:\"minecraft:speed\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:2b,duration:6000,id:\"minecraft:strength\",show_icon:1b,show_particles:1b}]"
      );
      ItemStack var22 = Items.SPLASH_POTION.getDefaultStack();
      var22.set(
         DataComponentTypes.POTION_CONTENTS,
         new PotionContentsComponent(Optional.of(Potions.SWIFTNESS), Optional.of(new Color(135, 0, 0).getRGB()), List.of(), Optional.empty())
      );
      PotionItemMatcher var23 = new PotionItemMatcher(
         var22,
         "Зелье Киллера",
         ItemMatcher.Type.FUNTIME,
         "custom_potion_effects",
         "[{ambient:0b,amplifier:0b,duration:3600,id:\"minecraft:resistance\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:3b,duration:1800,id:\"minecraft:strength\",show_icon:1b,show_particles:1b}]"
      );
      ItemStack var24 = Items.SPLASH_POTION.getDefaultStack();
      var24.set(
         DataComponentTypes.POTION_CONTENTS,
         new PotionContentsComponent(Optional.of(Potions.SWIFTNESS), Optional.of(new Color(164, 252, 76).getRGB()), List.of(), Optional.empty())
      );
      PotionItemMatcher var25 = new PotionItemMatcher(
         var24,
         "Серная кислота",
         ItemMatcher.Type.FUNTIME,
         "custom_potion_effects",
         "[{ambient:0b,amplifier:1b,duration:1000,id:\"minecraft:poison\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:3b,duration:1800,id:\"minecraft:slowness\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:2b,duration:1800,id:\"minecraft:weakness\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:4b,duration:600,id:\"minecraft:wither\",show_icon:1b,show_particles:1b}]"
      );
      ItemStack var26 = Items.SPLASH_POTION.getDefaultStack();
      var26.set(
         DataComponentTypes.POTION_CONTENTS,
         new PotionContentsComponent(Optional.of(Potions.SWIFTNESS), Optional.of(new Color(243, 59, 128).getRGB()), List.of(), Optional.empty())
      );
      PotionItemMatcher var27 = new PotionItemMatcher(
         var26,
         "Исцел",
         ItemMatcher.Type.FUNTIME,
         "custom_potion_effects",
         "[{ambient:1b,amplifier:1b,duration:1,id:\"minecraft:instant_health\",show_icon:1b,show_particles:1b},{ambient:1b,amplifier:0b,duration:600,id:\"minecraft:regeneration\",show_icon:1b,show_particles:1b}]"
      );
      ItemStack var28 = Items.SPLASH_POTION.getDefaultStack();
      var28.set(
         DataComponentTypes.POTION_CONTENTS,
         new PotionContentsComponent(Optional.of(Potions.SWIFTNESS), Optional.of(new Color(16737792).getRGB()), List.of(), Optional.empty())
      );
      PotionItemMatcher var29 = new PotionItemMatcher(var28, "Отрыжка", ItemMatcher.Type.FUNTIME, "CustomPotionColor", "16737792");
      ItemStack var30 = Items.TIPPED_ARROW.getDefaultStack();
      var30.set(
         DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.of(Potions.SWIFTNESS), Optional.of(65535), List.of(), Optional.empty())
      );
      PotionItemMatcher var31 = new PotionItemMatcher(
         var30,
         "Ледяная стрела",
         ItemMatcher.Type.FUNTIME,
         "custom_potion_effects",
         "[{ambient:0b,amplifier:50b,duration:100,id:\"minecraft:slowness\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:0b,duration:120,id:\"minecraft:weakness\",show_icon:1b,show_particles:1b}]"
      );
      ItemStack var32 = Items.TIPPED_ARROW.getDefaultStack();
      var32.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.of(Potions.SWIFTNESS), Optional.of(0), List.of(), Optional.empty()));
      PotionItemMatcher var33 = new PotionItemMatcher(
         var32,
         "Проклятая стрела",
         ItemMatcher.Type.FUNTIME,
         "custom_potion_effects",
         "[{ambient:0b,amplifier:0b,duration:40,id:\"minecraft:blindness\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:0b,duration:100,id:\"minecraft:nausea\",show_icon:1b,show_particles:1b},{ambient:0b,amplifier:0b,duration:200,id:\"minecraft:weakness\",show_icon:1b,show_particles:1b}]"
      );
      PotionItemMatcher var34 = new PotionItemMatcher(Items.DRIED_KELP.getDefaultStack(), "Пласт", ItemMatcher.Type.FUNTIME, "stratum", "1b");
      PotionItemMatcher var35 = new PotionItemMatcher(Items.NETHERITE_SCRAP.getDefaultStack(), "Трапка", ItemMatcher.Type.FUNTIME, "trap", "1b");
      PotionItemMatcher var36 = new PotionItemMatcher(Items.ENDER_EYE.getDefaultStack(), "Дезориентация", ItemMatcher.Type.FUNTIME, "desorientation", "1b");
      PotionItemMatcher var37 = new PotionItemMatcher(Items.SUGAR.getDefaultStack(), "Явная пыль", ItemMatcher.Type.FUNTIME, "sheerdust", "1b");
      PotionItemMatcher var38 = new PotionItemMatcher(Items.PHANTOM_MEMBRANE.getDefaultStack(), "Божья аура", ItemMatcher.Type.FUNTIME, "godsaura", "1b");
      SkullItemMatcher var39 = new SkullItemMatcher(
         "Сфера Андромеды",
         ItemMatcher.Type.FUNTIME,
         "ewogICJ0aW1lc3RhbXAiIDogMTcxNzM2NTEwODQzNywKICAicHJvZmlsZUlkIiA6ICIzMjNiYjlkYzkwZWU0Nzk5YjUxYzE3NjRmZDRhNjI3OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOcGllIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQ0ZmZlM2YzNThmMjA5YmFkOGZmZjRkYzQ4MjQ1ZDliYWYwYTAzMWIzYzFlZTZiNzU4NDYwYTMzOWIxNTE5ZTIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="
      );
      SkullItemMatcher var40 = new SkullItemMatcher(
         "Сфера Пандоры",
         ItemMatcher.Type.FUNTIME,
         "ewogICJ0aW1lc3RhbXAiIDogMTcxNzM2NTY2NTExNCwKICAicHJvZmlsZUlkIiA6ICJkNzJlNGJjZDIyZGI0NjQ4OTUxNTc0M2UyYTRmMWFjMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJhdnZheSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84ZTUxZTY1ZWI0MDUyNzcyMzgyYzllNTA3YTU0YmRlZDQzZTM5Zjc1NWI1ZGRmNTViM2YzOTQ0M2NlZDQ2N2Y0IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="
      );
      SkullItemMatcher var41 = new SkullItemMatcher(
         "Сфера Титана",
         ItemMatcher.Type.FUNTIME,
         "ewogICJ0aW1lc3RhbXAiIDogMTc1MDM1NDQ1NTE5MiwKICAicHJvZmlsZUlkIiA6ICJkOTcwYzEzZTM4YWI0NzlhOTY1OGM1ZDQ1MjZkMTM0YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJDcmltcHlMYWNlODUxMjciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODFlOTY5ODQ1OGI3ODQxYzk2YWU0ZjI0ZWM4NGFlMDE3MjQxMDA2NDFjNTY0ZTJhN2IxODVmNDA2ZThlZDIzIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="
      );
      SkullItemMatcher var42 = new SkullItemMatcher(
         "Сфера Аполлона",
         ItemMatcher.Type.FUNTIME,
         "ewogICJ0aW1lc3RhbXAiIDogMTcxNzM2NjYyNTM0NywKICAicHJvZmlsZUlkIiA6ICJhMjk1ODZmYmU1ZDk0Nzk2OWZjOGQ4ZGE0NzlhNDNlZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJMZXZlMjQiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQxMTdiNjAxOGZlZjBkNTE1NjcyMTczZTNiMjZlNjYwZDY1MWU1ODc2YmE2ZDAzZTUzNDIyNzBjNDliZWM4MCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"
      );
      SkullItemMatcher var43 = new SkullItemMatcher(
         "Сфера Астрея",
         ItemMatcher.Type.FUNTIME,
         "ewogICJ0aW1lc3RhbXAiIDogMTcxNzM2NTA2MjQwNywKICAicHJvZmlsZUlkIiA6ICJlMzcxMWU2Y2E0ZmY0NzA4YjY5ZjhiNGZlYzNhZjdhMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNckJ1cnN0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzFhNWFhZGQ1MmE1ZmFiOTcwODgxNDUxYWRmNTZmYmI0OTNhMzU4NTZlYTk2ZjU0ZTMyZWVhNjYyZDc4N2VkMjAiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="
      );
      SkullItemMatcher var44 = new SkullItemMatcher(
         "Сфера Осириса",
         ItemMatcher.Type.FUNTIME,
         "ewogICJ0aW1lc3RhbXAiIDogMTcxNzM2NjY2Mzg3NiwKICAicHJvZmlsZUlkIiA6ICI3NGEwMzQxNWY1OTI0ZTA4YjMyMGM2MmU1NGE3ZjJhYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNZXp6aXIiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDgxMzYzNWJkODZiMTcxYmJlMTQzYWQ3MWUwOTAyMjkyNjQ5Y2IzYWI4NDQwZWQwMGY4NWNhNmNhMzgyOTkzNiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"
      );
      SkullItemMatcher var45 = new SkullItemMatcher(
         "Сфера Химеры",
         ItemMatcher.Type.FUNTIME,
         "ewogICJ0aW1lc3RhbXAiIDogMTcxNzM2NjE4MTEwOSwKICAicHJvZmlsZUlkIiA6ICJiNzRiMGQzNTBkNTk0NTU4YmYyYjBlMDJlYmE4NjE4NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJCcmFuZG9uYnBtMjg0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzlmYWJlZWQ0MjRiMjUyYTg5NDVhNjQ0MmI0NjJkNWYzMTQ3MDFhODE2ZGEyZDBhNjljY2RmY2ZkNzQ2ZTU4OGUiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="
      );
      PotionItemMatcher var46 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultStack(),
         "Талисман Грани",
         ItemMatcher.Type.FUNTIME,
         "AttributeModifiers",
         "[{Amount:-4.0d,AttributeName:\"minecraft:generic.max_health\",Name:\"максимальное здоровье\",Operation:0,Slot:\"offhand\",UUID:[I;-1243469000,-221950864,-1992446485,-2021417908]},{Amount:0.15d,AttributeName:\"minecraft:generic.movement_speed\",Name:\"скорость\",Operation:1,Slot:\"offhand\",UUID:[I;421185474,-1675802507,-1302866561,493632362]},{Amount:3.0d,AttributeName:\"minecraft:generic.attack_damage\",Name:\"сила\",Operation:0,Slot:\"offhand\",UUID:[I;1519226946,1096501307,-1211766788,1772678932]}]"
      );
      PotionItemMatcher var47 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultStack(),
         "Талисман Дедала",
         ItemMatcher.Type.FUNTIME,
         "AttributeModifiers",
         "[{Amount:-4.0d,AttributeName:\"minecraft:generic.max_health\",Name:\"максимальное здоровье\",Operation:0,Slot:\"offhand\",UUID:[I;359257458,1778207792,-1338766924,848576712]},{Amount:5.0d,AttributeName:\"minecraft:generic.attack_damage\",Name:\"сила\",Operation:0,Slot:\"offhand\",UUID:[I;-1723975978,-172144109,-1549361344,1422474545]}]"
      );
      PotionItemMatcher var48 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultStack(),
         "Талисман Тритона",
         ItemMatcher.Type.FUNTIME,
         "AttributeModifiers",
         "[{Amount:2.0d,AttributeName:\"minecraft:generic.max_health\",Name:\"максимальное здоровье\",Operation:0,Slot:\"offhand\",UUID:[I;1523385351,-602324415,-1760945561,-270836422]},{Amount:-2.0d,AttributeName:\"minecraft:generic.armor_toughness\",Name:\"твёрдость брони\",Operation:0,Slot:\"offhand\",UUID:[I;1017626540,670256083,-1935748919,-786940866]},{Amount:2.0d,AttributeName:\"minecraft:generic.armor\",Name:\"броня\",Operation:0,Slot:\"offhand\",UUID:[I;1409389356,857884230,-1271577328,-932163262]}]"
      );
      PotionItemMatcher var49 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultStack(),
         "Талисман Гармонии",
         ItemMatcher.Type.FUNTIME,
         "AttributeModifiers",
         "[{Amount:2.0d,AttributeName:\"minecraft:generic.max_health\",Name:\"максимальное здоровье\",Operation:0,Slot:\"offhand\",UUID:[I;1253805553,182601342,-1805352146,-1397322752]},{Amount:2.0d,AttributeName:\"minecraft:generic.attack_damage\",Name:\"сила\",Operation:0,Slot:\"offhand\",UUID:[I;1648014473,711805426,-1962672765,-537567144]},{Amount:2.0d,AttributeName:\"minecraft:generic.armor\",Name:\"броня\",Operation:0,Slot:\"offhand\",UUID:[I;-432853915,2131774585,-1617568671,-621826558]}]"
      );
      PotionItemMatcher var50 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultStack(),
         "Талисман Феникса",
         ItemMatcher.Type.FUNTIME,
         "AttributeModifiers",
         "[{Amount:6.0d,AttributeName:\"minecraft:generic.max_health\",Name:\"максимальное здоровье\",Operation:0,Slot:\"offhand\",UUID:[I;587791282,-2058138192,-2013554380,1529824133]},{Amount:0.1d,AttributeName:\"minecraft:generic.attack_speed\",Name:\"скорость атаки\",Operation:1,Slot:\"offhand\",UUID:[I;233538602,1321813146,-1704635325,1580943681]}]"
      );
      PotionItemMatcher var51 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultStack(),
         "Талисман Ехидны",
         ItemMatcher.Type.FUNTIME,
         "AttributeModifiers",
         "[{Amount:-4.0d,AttributeName:\"minecraft:generic.max_health\",Name:\"максимальное здоровье\",Operation:0,Slot:\"offhand\",UUID:[I;951469257,944390782,-1639200535,1882164017]},{Amount:6.0d,AttributeName:\"minecraft:generic.attack_damage\",Name:\"сила\",Operation:0,Slot:\"offhand\",UUID:[I;955551735,1646086503,-2027411701,279678977]},{Amount:-2.0d,AttributeName:\"minecraft:generic.armor_toughness\",Name:\"твёрдость брони\",Operation:0,Slot:\"offhand\",UUID:[I;1301352305,-411349527,-1975888927,1331290909]},{Amount:-2.0d,AttributeName:\"minecraft:generic.armor\",Name:\"броня\",Operation:0,Slot:\"offhand\",UUID:[I;-1591608852,-1821816734,-1777269705,-1095126461]}]"
      );
      PotionItemMatcher var52 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultStack(),
         "Талисман Крушителя",
         ItemMatcher.Type.FUNTIME,
         "AttributeModifiers",
         "[{Amount:4.0d,AttributeName:\"minecraft:generic.max_health\",Name:\"максимальное здоровье\",Operation:0,Slot:\"offhand\",UUID:[I;-2029680951,-1707392264,-1958707995,-389772071]},{Amount:3.0d,AttributeName:\"minecraft:generic.attack_damage\",Name:\"сила\",Operation:0,Slot:\"offhand\",UUID:[I;430632003,-1617212859,-1216331408,-1833541566]},{Amount:2.0d,AttributeName:\"minecraft:generic.armor_toughness\",Name:\"твёрдость брони\",Operation:0,Slot:\"offhand\",UUID:[I;-1348659832,-1401469259,-1365362331,376666792]},{Amount:2.0d,AttributeName:\"minecraft:generic.armor\",Name:\"броня\",Operation:0,Slot:\"offhand\",UUID:[I;897201681,1928088706,-1491637188,-648449433]}]"
      );
      PotionItemMatcher var53 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultStack(),
         "Талисман Карателя",
         ItemMatcher.Type.FUNTIME,
         "AttributeModifiers",
         "[{Amount:7.0d,AttributeName:\"minecraft:generic.attack_damage\",Name:\"сила\",Operation:0,Slot:\"offhand\",UUID:[I;-1651993925,-1873589036,-2067416271,-818067434]},{Amount:-4.0d,AttributeName:\"minecraft:generic.max_health\",Name:\"максимальное здоровье\",Operation:0,Slot:\"offhand\",UUID:[I;-473822740,-120697814,-1795206788,-16471115]},{Amount:0.1d,AttributeName:\"minecraft:generic.movement_speed\",Name:\"скорость\",Operation:1,Slot:\"offhand\",UUID:[I;1026697411,-1939651121,-2079096398,-1055446047]}]"
      );
      this.arrayList2.add(var2);
      this.arrayList2.add(var4);
      this.arrayList2.add(var5);
      this.arrayList2.add(var7);
      this.arrayList2.add(var9);
      this.arrayList2.add(var11);
      this.arrayList2.add(var13);
      this.arrayList2.add(var15);
      this.arrayList2.add(var17);
      this.arrayList2.add(var19);
      this.arrayList2.add(var21);
      this.arrayList2.add(var23);
      this.arrayList2.add(var25);
      this.arrayList2.add(var27);
      this.arrayList2.add(var31);
      this.arrayList2.add(var33);
      this.arrayList2.add(var29);
      this.arrayList2.add(var34);
      this.arrayList2.add(var35);
      this.arrayList2.add(var36);
      this.arrayList2.add(var37);
      this.arrayList2.add(var38);
      this.arrayList2.add(var39);
      this.arrayList2.add(var40);
      this.arrayList2.add(var41);
      this.arrayList2.add(var42);
      this.arrayList2.add(var43);
      this.arrayList2.add(var44);
      this.arrayList2.add(var45);
      this.arrayList2.add(var46);
      this.arrayList2.add(var47);
      this.arrayList2.add(var48);
      this.arrayList2.add(var49);
      this.arrayList2.add(var50);
      this.arrayList2.add(var51);
      this.arrayList2.add(var52);
      this.arrayList2.add(var53);
      this.arrayList.add(new ItemMatcher(Items.ENCHANTED_GOLDEN_APPLE.getDefaultStack(), "Зачарованное золотое яблоко", ItemMatcher.Type.ANY));
      this.arrayList.add(new ItemMatcher(Items.GOLDEN_APPLE.getDefaultStack(), "Золотое яблоко", ItemMatcher.Type.ANY));
      this.arrayList.add(new ItemMatcher(Items.FIREWORK_ROCKET.getDefaultStack(), "Фейерверк", ItemMatcher.Type.ANY));
      this.arrayList.add(new ItemMatcher(Items.GOLDEN_CARROT.getDefaultStack(), "Золотая морковь", ItemMatcher.Type.ANY));
      this.arrayList.add(new ItemMatcher(Items.ENDER_PEARL.getDefaultStack(), "Эндер жемчуг", "Эндер-жемчуг", ItemMatcher.Type.ANY));
      this.arrayList.add(new ItemMatcher(Items.CHORUS_FRUIT.getDefaultStack(), "Плод хоруса", ItemMatcher.Type.ANY));
      this.arrayList.add(new ItemMatcher(Items.TOTEM_OF_UNDYING.getDefaultStack(), "Тотем бессмертия", ItemMatcher.Type.ANY));
      this.arrayList.add(new ItemMatcher(Items.EXPERIENCE_BOTTLE.getDefaultStack(), "Пузырёк опыта", ItemMatcher.Type.ANY));
      this.arrayList.add(new ItemMatcher(Items.ELYTRA.getDefaultStack(), "Элитры", ItemMatcher.Type.ANY));
      this.arrayList.add(new PotionTypeMatcher(Items.POTION, Potions.TURTLE_MASTER, "Зелье черепашьей мощи", ItemMatcher.Type.HOLLYWORLD));
      this.arrayList.add(new ItemMatcher(Items.CONDUIT.getDefaultStack(), "Артефакт", ItemMatcher.Type.HOLLYWORLD));
      var1 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:impenetrable-enchant-custom, level=2], EnchantCustom [checked=minecraft:aqua_affinity, level=1], EnchantCustom [checked=minecraft:blast_protection, level=5], EnchantCustom [checked=minecraft:fire_protection, level=5], EnchantCustom [checked=minecraft:projectile_protection, level=5], EnchantCustom [checked=minecraft:protection, level=5], EnchantCustom [checked=minecraft:respiration, level=3], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:aqua_affinity, level=1], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5], EnchantVanilla [checked=minecraft:respiration, level=3], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5]]"
      );
      var2 = new EnchantedItemMatcher(Items.NETHERITE_HELMET.getDefaultStack(), "Шлем infinity", ItemMatcher.Type.HOLLYWORLD);
      var1.forEach(var2::method4844);
      var3 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:impenetrable-enchant-custom, level=2], EnchantCustom [checked=minecraft:blast_protection, level=5], EnchantCustom [checked=minecraft:fire_protection, level=5], EnchantCustom [checked=minecraft:projectile_protection, level=5], EnchantCustom [checked=minecraft:protection, level=5], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5]]"
      );
      var4 = new EnchantedItemMatcher(Items.NETHERITE_CHESTPLATE.getDefaultStack(), "Нагрудник infinity", ItemMatcher.Type.HOLLYWORLD);
      var3.forEach(var4::method4844);
      var var79 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:impenetrable-enchant-custom, level=2], EnchantCustom [checked=minecraft:blast_protection, level=5], EnchantCustom [checked=minecraft:fire_protection, level=5], EnchantCustom [checked=minecraft:projectile_protection, level=5], EnchantCustom [checked=minecraft:protection, level=5], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5]]"
      );
      EnchantedItemMatcher var80 = new EnchantedItemMatcher(Items.NETHERITE_LEGGINGS.getDefaultStack(), "Штаны infinity", ItemMatcher.Type.HOLLYWORLD);
      var79.forEach(var80::method4844);
      var var81 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:impenetrable-enchant-custom, level=2], EnchantCustom [checked=minecraft:blast_protection, level=5], EnchantCustom [checked=minecraft:depth_strider, level=3], EnchantCustom [checked=minecraft:feather_falling, level=4], EnchantCustom [checked=minecraft:fire_protection, level=5], EnchantCustom [checked=minecraft:projectile_protection, level=5], EnchantCustom [checked=minecraft:protection, level=5], EnchantCustom [checked=minecraft:soul_speed, level=3], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:depth_strider, level=3], EnchantVanilla [checked=minecraft:feather_falling, level=4], EnchantVanilla [checked=minecraft:fire_protection, level=5], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:soul_speed, level=3], EnchantVanilla [checked=minecraft:unbreaking, level=5]]"
      );
      EnchantedItemMatcher var82 = new EnchantedItemMatcher(Items.NETHERITE_BOOTS.getDefaultStack(), "Ботинки infinity", ItemMatcher.Type.HOLLYWORLD);
      var81.forEach(var82::method4844);
      var var83 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:impenetrable-enchant-custom, level=1], EnchantCustom [checked=minecraft:aqua_affinity, level=1], EnchantCustom [checked=minecraft:blast_protection, level=5], EnchantCustom [checked=minecraft:fire_protection, level=5], EnchantCustom [checked=minecraft:projectile_protection, level=5], EnchantCustom [checked=minecraft:protection, level=5], EnchantCustom [checked=minecraft:respiration, level=3], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:aqua_affinity, level=1], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5], EnchantVanilla [checked=minecraft:respiration, level=3], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5]]"
      );
      EnchantedItemMatcher var84 = new EnchantedItemMatcher(Items.NETHERITE_HELMET.getDefaultStack(), "Шлем eternity", ItemMatcher.Type.HOLLYWORLD);
      var83.forEach(var84::method4844);
      var var85 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:impenetrable-enchant-custom, level=1], EnchantCustom [checked=minecraft:blast_protection, level=5], EnchantCustom [checked=minecraft:fire_protection, level=5], EnchantCustom [checked=minecraft:projectile_protection, level=5], EnchantCustom [checked=minecraft:protection, level=5], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5]]"
      );
      EnchantedItemMatcher var86 = new EnchantedItemMatcher(Items.NETHERITE_CHESTPLATE.getDefaultStack(), "Нагрудник eternity", ItemMatcher.Type.HOLLYWORLD);
      var85.forEach(var86::method4844);
      var13 = new EnchantedItemMatcher(Items.NETHERITE_LEGGINGS.getDefaultStack(), "Штаны eternity", ItemMatcher.Type.HOLLYWORLD);
      var85.forEach(var13::method4844);
      var14 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=minecraft:blast_protection, level=5], EnchantCustom [checked=minecraft:depth_strider, level=3], EnchantCustom [checked=minecraft:feather_falling, level=4], EnchantCustom [checked=minecraft:fire_protection, level=5], EnchantCustom [checked=minecraft:projectile_protection, level=5], EnchantCustom [checked=minecraft:protection, level=5], EnchantCustom [checked=minecraft:soul_speed, level=3], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:depth_strider, level=3], EnchantVanilla [checked=minecraft:blast_protection, level=5], EnchantVanilla [checked=minecraft:fire_protection, level=5], EnchantVanilla [checked=minecraft:projectile_protection, level=5], EnchantVanilla [checked=minecraft:protection, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:soul_speed, level=3], EnchantVanilla [checked=minecraft:feather_falling, level=4]]"
      );
      var15 = new EnchantedItemMatcher(Items.NETHERITE_BOOTS.getDefaultStack(), "Ботинки eternity", ItemMatcher.Type.HOLLYWORLD);
      var14.forEach(var15::method4844);
      PotionItemMatcher var90 = new PotionItemMatcher(
         Items.GOLDEN_HELMET.getDefaultStack(), "Шлем Солнца", ItemMatcher.Type.HOLLYWORLD, "kringeItems", "SunHelmet"
      );
      var var91 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:critical-enchant-custom, level=2], EnchantCustom [checked=enchantments:destroyer-enchant-custom, level=2], EnchantCustom [checked=enchantments:rich-enchant-custom, level=1], EnchantCustom [checked=minecraft:bane_of_arthropods, level=7], EnchantCustom [checked=minecraft:fire_aspect, level=2], EnchantCustom [checked=minecraft:looting, level=5], EnchantCustom [checked=minecraft:sharpness, level=7], EnchantCustom [checked=minecraft:smite, level=7], EnchantCustom [checked=minecraft:sweeping, level=3], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:sweeping_edge, level=3], EnchantVanilla [checked=minecraft:bane_of_arthropods, level=7], EnchantVanilla [checked=minecraft:looting, level=5], EnchantVanilla [checked=minecraft:sharpness, level=7], EnchantVanilla [checked=minecraft:fire_aspect, level=2], EnchantVanilla [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:smite, level=7]]"
      );
      EnchantedItemMatcher var92 = new EnchantedItemMatcher(Items.NETHERITE_SWORD.getDefaultStack(), "Меч eternity", ItemMatcher.Type.HOLLYWORLD);
      var91.forEach(var92::method4844);
      var var93 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:drill-enchant-custom, level=2], EnchantCustom [checked=enchantments:exp-enchant-custom, level=3], EnchantCustom [checked=enchantments:filter-enchant-custom, level=1], EnchantCustom [checked=enchantments:foundry-enchant-custom, level=1], EnchantCustom [checked=enchantments:internal-enchant-custom, level=1], EnchantCustom [checked=enchantments:magnet-enchant-custom, level=1], EnchantCustom [checked=minecraft:efficiency, level=10], EnchantCustom [checked=minecraft:fortune, level=5], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:efficiency, level=10], EnchantVanilla [checked=minecraft:fortune, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5]]"
      );
      EnchantedItemMatcher var94 = new EnchantedItemMatcher(Items.NETHERITE_PICKAXE.getDefaultStack(), "Кирка eternity", ItemMatcher.Type.HOLLYWORLD);
      var93.forEach(var94::method4844);
      var var95 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=enchantments:stun-enchant-custom, level=2], EnchantCustom [checked=minecraft:multishot, level=1], EnchantCustom [checked=minecraft:piercing, level=5], EnchantCustom [checked=minecraft:quick_charge, level=3], EnchantCustom [checked=minecraft:unbreaking, level=3], EnchantVanilla [checked=minecraft:multishot, level=1], EnchantVanilla [checked=minecraft:piercing, level=5], EnchantVanilla [checked=minecraft:quick_charge, level=3], EnchantVanilla [checked=minecraft:unbreaking, level=3]]"
      );
      EnchantedItemMatcher var96 = new EnchantedItemMatcher(Items.CROSSBOW.getDefaultStack(), "Арбалет eternity", ItemMatcher.Type.HOLLYWORLD);
      var95.forEach(var96::method4844);
      var var97 = EnchantTranslations.method4710(
         "[EnchantCustom [checked=minecraft:impaling, level=5], EnchantCustom [checked=minecraft:looting, level=5], EnchantCustom [checked=minecraft:loyalty, level=3], EnchantCustom [checked=minecraft:unbreaking, level=5], EnchantVanilla [checked=minecraft:loyalty, level=3], EnchantVanilla [checked=minecraft:looting, level=5], EnchantVanilla [checked=minecraft:impaling, level=5], EnchantVanilla [checked=minecraft:unbreaking, level=5]]"
      );
      EnchantedItemMatcher var98 = new EnchantedItemMatcher(Items.TRIDENT.getDefaultStack(), "Громовержец", ItemMatcher.Type.HOLLYWORLD);
      var97.forEach(var98::method4844);
      var25 = new PotionItemMatcher(Items.NETHERITE_SWORD.getDefaultStack(), "Фармер", ItemMatcher.Type.HOLLYWORLD, "RepairCost", "Фармер");
      PotionItemMatcher var100 = new PotionItemMatcher(
         Items.ELYTRA.getDefaultStack(), "Броневая элитра", ItemMatcher.Type.HOLLYWORLD, "kringeItems", "ArmorElytra"
      );
      var27 = new PotionItemMatcher(
         Items.MAGENTA_SHULKER_BOX.getDefaultStack(), "Рюкзак 4 уровень", ItemMatcher.Type.HOLLYWORLD, "PublicBukkitValues", "litebackpacks:backpack"
      );
      PotionItemMatcher var102 = new PotionItemMatcher(
         Items.RED_SHULKER_BOX.getDefaultStack(), "Рюкзак 3 уровень", ItemMatcher.Type.HOLLYWORLD, "PublicBukkitValues", "litebackpacks:backpack"
      );
      var29 = new PotionItemMatcher(
         Items.LIGHT_BLUE_SHULKER_BOX.getDefaultStack(), "Рюкзак 2 уровень", ItemMatcher.Type.HOLLYWORLD, "PublicBukkitValues", "litebackpacks:backpack"
      );
      PotionItemMatcher var104 = new PotionItemMatcher(
         Items.PINK_SHULKER_BOX.getDefaultStack(), "Рюкзак 1 уровень", ItemMatcher.Type.HOLLYWORLD, "PublicBukkitValues", "litebackpacks:backpack"
      );
      var31 = new PotionItemMatcher(
         Items.EXPERIENCE_BOTTLE.getDefaultStack(), "Пузырек с 50 уровнем", "50", ItemMatcher.Type.HOLLYWORLD, "holy-exp-bottle-value", "5345"
      );
      PotionItemMatcher var106 = new PotionItemMatcher(
         Items.EXPERIENCE_BOTTLE.getDefaultStack(), "Пузырь опыта", ItemMatcher.Type.HOLLYWORLD, "kringeItems", "ExpBottle"
      );
      var33 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultStack(),
         "Талисман Сатиры",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":2,\"nbtName\":\"hms-rush\"},{\"lvl\":3,\"nbtName\":\"hms-damage\"}"
      );
      var34 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultStack(),
         "Талисман infinity",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":2,\"nbtName\":\"hms-speed\"},{\"lvl\":2,\"nbtName\":\"hms-health\"},{\"lvl\":2,\"nbtName\":\"hms-damage\"},{\"lvl\":2,\"nbtName\":\"hms-armor\"}"
      );
      var35 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultStack(),
         "Талисман eternity",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":2,\"nbtName\":\"hms-armor\"},{\"lvl\":2,\"nbtName\":\"hms-speed\"},{\"lvl\":2,\"nbtName\":\"hms-damage\"}"
      );
      var36 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultStack(),
         "Талисман stinger",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":2,\"nbtName\":\"hms-damage\"},{\"lvl\":2,\"nbtName\":\"hms-armor\"},{\"lvl\":1,\"nbtName\":\"hms-speed\"}"
      );
      var37 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultStack(),
         "Мифический талисман на урон 3 броню 2",
         "Мифический талисман",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":3,\"nbtName\":\"hms-damage\"},{\"lvl\":2,\"nbtName\":\"hms-armor\"}"
      );
      var38 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultStack(),
         "Мифический талисман на броню 3 урон 2",
         "Мифический талисман",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":3,\"nbtName\":\"hms-armor\"},{\"lvl\":2,\"nbtName\":\"hms-damage\"}"
      );
      PotionItemMatcher var113 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultStack(),
         "Мифический талисман на броню 3 скорость 2",
         "Мифический талисман",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":3,\"nbtName\":\"hms-armor\"},{\"lvl\":2,\"nbtName\":\"hms-speed\"}"
      );
      PotionItemMatcher var114 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultStack(),
         "Легендарный талисман на урон 3",
         "Сфера на урон 3",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":3,\"nbtName\":\"hms-damage\"}"
      );
      PotionItemMatcher var115 = new PotionItemMatcher(
         Items.TOTEM_OF_UNDYING.getDefaultStack(),
         "Легендарный талисман на скорость 3",
         "Сфера на скорость 3",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":3,\"nbtName\":\"hms-speed\"}"
      );
      var42 = new SkullItemMatcher(
         "Сфера Цербера",
         ItemMatcher.Type.HOLLYWORLD,
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA5NWE3ZmQ5MGRhYTFiYmU3MDY5MDg5NzQwZTA1ZDBiZmM2NjI5NmVlM2M0MGVlNzFhNGUwYTY2MTZiMmJiYyJ9fX0="
      );
      var43 = new SkullItemMatcher(
         "Сфера Флеша",
         ItemMatcher.Type.HOLLYWORLD,
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc0MDBlYTE5ZGJkODRmNzVjMzlhZDY4MjNhYzRlZjc4NmYzOWY0OGZjNmY4NDYwMjM2NmFjMjliODM3NDIyIn19fQ=="
      );
      var44 = new SkullItemMatcher(
         "Сфера Имморталити",
         ItemMatcher.Type.HOLLYWORLD,
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODNlZDRjZTIzOTMzZTY2ZTA0ZGYxNjA3MDY0NGY3NTk5ZWViNTUzMDdmN2VhZmU4ZDkyZjQwZmIzNTIwODYzYyJ9fX0="
      );
      var45 = new SkullItemMatcher(
         "Сфера Арморталити",
         ItemMatcher.Type.HOLLYWORLD,
         "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWE2MmI5ZGU2YTI2Yjg2ODY5Y2EyMmVhNDBmMWJkZTgwYTA0MzBhNTQ1NDdiZWNjZThmZGE4NzA3Nzc3MjU4ZiJ9fX0="
      );
      ItemStack var120 = new SkullItemMatcher(
            "",
            ItemMatcher.Type.HOLLYWORLD,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM5MzY1NjQyYzZlZGRjZmVkZjViNWUxNGUyYmM3MTI1N2Q5ZTRhMzM2M2QxMjNjNmYzM2M1NWNhZmJmNmQifX19"
         )
         .getItemStack();
      var47 = new PotionItemMatcher(var120, "Сфера на урон 3", ItemMatcher.Type.HOLLYWORLD, "sphereEffect", "{\"lvl\":3,\"nbtName\":\"hms-damage\"}");
      var48 = new PotionItemMatcher(var120, "Сфера на Скорость 3", ItemMatcher.Type.HOLLYWORLD, "sphereEffect", "{\"lvl\":3,\"nbtName\":\"hms-speed\"}");
      var49 = new PotionItemMatcher(
         var120,
         "Сфера eternity",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":2,\"nbtName\":\"hms-speed\"},{\"lvl\":2,\"nbtName\":\"hms-armor\"},{\"lvl\":2,\"nbtName\":\"hms-damage\"}"
      );
      var50 = new PotionItemMatcher(
         var120,
         "Сфера stinger",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":1,\"nbtName\":\"hms-speed\"},{\"lvl\":2,\"nbtName\":\"hms-damage\"},{\"lvl\":2,\"nbtName\":\"hms-armor\"}"
      );
      ItemStack var125 = new SkullItemMatcher(
            "",
            ItemMatcher.Type.HOLLYWORLD,
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmFmZjJlYjQ5OGU1YzZhMDQ0ODRmMGM5Zjc4NWI0NDg0NzlhYjIxM2RmOTVlYzkxMTc2YTMwOGExMmFkZDcwIn19fQ=="
         )
         .getItemStack();
      var52 = new PotionItemMatcher(
         var125,
         "Мифическая сфера на урон 3 броню 2",
         "Мифическая сфера",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":3,\"nbtName\":\"hms-damage\"},{\"lvl\":2,\"nbtName\":\"hms-armor\"}"
      );
      var53 = new PotionItemMatcher(
         var125,
         "Мифическая сфера на броню 3 урон 2",
         "Мифическая сфера",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":3,\"nbtName\":\"hms-armor\"},{\"lvl\":2,\"nbtName\":\"hms-damage\"}"
      );
      PotionItemMatcher var54 = new PotionItemMatcher(
         var125,
         "Мифическая сфера на броню 3 скорость 2",
         "Мифическая сфера",
         ItemMatcher.Type.HOLLYWORLD,
         "sphereEffect",
         "{\"lvl\":3,\"nbtName\":\"hms-armor\"},{\"lvl\":2,\"nbtName\":\"hms-speed\"}"
      );
      PotionItemMatcher var55 = new PotionItemMatcher(
         Items.POTION.getDefaultStack(), "Улучшенное зелье силы", ItemMatcher.Type.HOLLYWORLD, "custom_potion_effects", "minecraft:strength@2"
      );
      PotionItemMatcher var56 = new PotionItemMatcher(
         Items.POTION.getDefaultStack(), "Улучшенное зелье скорости", ItemMatcher.Type.HOLLYWORLD, "custom_potion_effects", "minecraft:speed@2"
      );
      PotionItemMatcher var57 = new PotionItemMatcher(
         Items.POTION.getDefaultStack(), "Зелье Победителя", ItemMatcher.Type.HOLLYWORLD, "kringeItems", "win-potion"
      );
      PotionItemMatcher var58 = new PotionItemMatcher(
         Items.POTION.getDefaultStack(), "Зелье исцеления 2", "Зелье исцеление", ItemMatcher.Type.HOLLYWORLD, "Potion", "minecraft:strong_healing"
      );
      PotionItemMatcher var59 = new PotionItemMatcher(
         Items.POPPED_CHORUS_FRUIT.getDefaultStack(), "Трапка", ItemMatcher.Type.HOLLYWORLD, "pyrotechnic-item", "ALTERNATIVE_TRAP"
      );
      PotionItemMatcher var60 = new PotionItemMatcher(
         Items.PRISMARINE_SHARD.getDefaultStack(), "Взрывная Трапка", ItemMatcher.Type.HOLLYWORLD, "pyrotechnic-item", "EXPLOSIVE_TRAP"
      );
      PotionItemMatcher var61 = new PotionItemMatcher(Items.NETHER_STAR.getDefaultStack(), "Стан", ItemMatcher.Type.HOLLYWORLD, "pyrotechnic-item", "STUN_STAR");
      PotionItemMatcher var62 = new PotionItemMatcher(Items.SNOWBALL.getDefaultStack(), "Ком снега", ItemMatcher.Type.HOLLYWORLD, "kringeItems", "SnowBall");
      PotionItemMatcher var63 = new PotionItemMatcher(
         Items.FIRE_CHARGE.getDefaultStack(), "Взрывная штучка", ItemMatcher.Type.HOLLYWORLD, "kringeItems", "ExplosiveStuff"
      );
      PotionItemMatcher var64 = new PotionItemMatcher(
         Items.ORANGE_DYE.getDefaultStack(), "Руна Бессмертие", "Бессмертие", ItemMatcher.Type.HOLLYWORLD, "PublicBukkitValues", "literunes:rune-id"
      );
      PotionItemMatcher var65 = new PotionItemMatcher(
         Items.DIAMOND_SWORD.getDefaultStack(), "Охотник", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "EXP_DROPPER"
      );
      PotionItemMatcher var66 = new PotionItemMatcher(Items.SNOW_BLOCK.getDefaultStack(), "Снеговик", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "BLINDNESS");
      PotionItemMatcher var67 = new PotionItemMatcher(
         Items.SEA_LANTERN.getDefaultStack(), "Иллюминатор", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "PORTHOLE"
      );
      PotionItemMatcher var68 = new PotionItemMatcher(Items.ENDER_PEARL.getDefaultStack(), "Эндермен", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "ENDERMAN");
      PotionItemMatcher var69 = new PotionItemMatcher(
         Items.PHANTOM_MEMBRANE.getDefaultStack(), "Анти Фантом", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "ANTI_PHANTOM"
      );
      PotionItemMatcher var70 = new PotionItemMatcher(
         Items.HONEY_BLOCK.getDefaultStack(), "Телекинез", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "TELEKINESIS"
      );
      PotionItemMatcher var71 = new PotionItemMatcher(Items.FEATHER.getDefaultStack(), "Гравитация", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "GRAVITY");
      PotionItemMatcher var72 = new PotionItemMatcher(
         Items.WITHER_SKELETON_SKULL.getDefaultStack(), "Вампиризм", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "VAMPIRISM"
      );
      PotionItemMatcher var73 = new PotionItemMatcher(Items.POTION.getDefaultStack(), "Справедливость", ItemMatcher.Type.HOLLYWORLD, "kringeEffect", "JUSTICE");
      PotionItemMatcher var74 = new PotionItemMatcher(
         Items.TRIPWIRE_HOOK.getDefaultStack(), "Универсальный ключ", ItemMatcher.Type.HOLLYWORLD, "CustomModelData", "123433"
      );
      this.arrayList3.add(var2);
      this.arrayList3.add(var4);
      this.arrayList3.add(var80);
      this.arrayList3.add(var82);
      this.arrayList3.add(var84);
      this.arrayList3.add(var86);
      this.arrayList3.add(var13);
      this.arrayList3.add(var15);
      this.arrayList3.add(var90);
      this.arrayList3.add(var100);
      this.arrayList3.add(var92);
      this.arrayList3.add(var94);
      this.arrayList3.add(var96);
      this.arrayList3.add(var98);
      this.arrayList3.add(var42);
      this.arrayList3.add(var43);
      this.arrayList3.add(var44);
      this.arrayList3.add(var45);
      this.arrayList3.add(var52);
      this.arrayList3.add(var53);
      this.arrayList3.add(var54);
      this.arrayList3.add(var47);
      this.arrayList3.add(var48);
      this.arrayList3.add(var49);
      this.arrayList3.add(var50);
      this.arrayList3.add(var33);
      this.arrayList3.add(var34);
      this.arrayList3.add(var35);
      this.arrayList3.add(var36);
      this.arrayList3.add(var37);
      this.arrayList3.add(var38);
      this.arrayList3.add(var113);
      this.arrayList3.add(var114);
      this.arrayList3.add(var115);
      this.arrayList3.add(var55);
      this.arrayList3.add(var56);
      this.arrayList3.add(var58);
      this.arrayList3.add(var57);
      this.arrayList3.add(var31);
      this.arrayList3.add(var27);
      this.arrayList3.add(var102);
      this.arrayList3.add(var29);
      this.arrayList3.add(var104);
      this.arrayList3.add(var59);
      this.arrayList3.add(var60);
      this.arrayList3.add(var61);
      this.arrayList3.add(var63);
      this.arrayList3.add(var62);
      this.arrayList3.add(var64);
      this.arrayList3.add(var106);
      this.arrayList3.add(var65);
      this.arrayList3.add(var66);
      this.arrayList3.add(var67);
      this.arrayList3.add(var68);
      this.arrayList3.add(var69);
      this.arrayList3.add(var70);
      this.arrayList3.add(var71);
      this.arrayList3.add(var72);
      this.arrayList3.add(var73);
      this.arrayList3.add(var74);
      this.arrayList3.add(var25);
      this.arrayList4.add(new ItemMatcher(Items.GUNPOWDER.getDefaultStack(), "Порох", ItemMatcher.Type.ANY));
      this.arrayList4
         .add(
            new PotionItemMatcher(Items.PRISMARINE_CRYSTALS.getDefaultStack(), "Боевой фрагмент", ItemMatcher.Type.HOLLYWORLD, "kringeItems", "BattleFragment")
         );
      this.arrayList4
         .add(
            new PotionItemMatcher(Items.CLAY.getDefaultStack(), "Взрывчатое вещество", ItemMatcher.Type.HOLLYWORLD, "pyrotechnic-item", "EXPLOSIVE_SUBSTANCE")
         );
      this.arrayList4.add(new PotionItemMatcher(Items.TNT.getDefaultStack(), "Динамит A", "динамит а", ItemMatcher.Type.HOLLYWORLD, "pyrotechnic-item", "A"));
      this.arrayList4
         .add(new PotionItemMatcher(Items.GOLDEN_PICKAXE.getDefaultStack(), "Золотая кирка Джейка", ItemMatcher.Type.HOLLYWORLD, "kringeItems", "jake-pickaxe"));
      this.arrayList4.add(new ItemMatcher(Items.NETHERITE_INGOT.getDefaultStack(), "Незеритовый слиток", ItemMatcher.Type.HOLLYWORLD));
      this.arrayList4.add(new PotionItemMatcher(Items.TNT.getDefaultStack(), "Динамит B", "динамит б", ItemMatcher.Type.HOLLYWORLD, "pyrotechnic-item", "B"));
      this.arrayList4
         .add(
            new SkullItemMatcher(
               "Осколок сферы",
               ItemMatcher.Type.HOLLYWORLD,
               "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY3YmJjZTIzZTgxNjJlNDJkMjA3MDU1YjBjZTkwZjBlZDU3YjAxNWU1MjEyMTM5YWM4ZmM3ZTZkNDVkZGZjYSJ9fX0="
            )
         );
      this.arrayList4
         .add(new PotionItemMatcher(Items.TNT.getDefaultStack(), "Динамит B2", "динамит б2", ItemMatcher.Type.HOLLYWORLD, "pyrotechnic-item", "B2"));
      this.arrayList4
         .add(new PotionItemMatcher(Items.TNT.getDefaultStack(), "С4 ВзРыВчАтКа", "с4 взрывчатка", ItemMatcher.Type.HOLLYWORLD, "pyrotechnic-item", "C4"));
   }

   private String method1766(ItemMatcher var1) {
      return var1.getText22() + var1.getText2() + var1.getType().name();
   }

   @Generated
   public ArrayList<ItemMatcher> getArrayList() {
      return this.arrayList;
   }

   @Generated
   public ArrayList<ItemMatcher> getArrayList2() {
      return this.arrayList2;
   }

   @Generated
   public ArrayList<ItemMatcher> getArrayList3() {
      return this.arrayList3;
   }

   @Generated
   public ArrayList<ItemMatcher> getArrayList4() {
      return this.arrayList4;
   }
}

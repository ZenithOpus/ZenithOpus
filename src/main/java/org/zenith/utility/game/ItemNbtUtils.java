package org.zenith.utility.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.MergedComponentMap;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.zenith.utility.misc.CustomEnchantMatcher;
import org.zenith.utility.misc.EnchantMatcher;
import org.zenith.utility.misc.VanillaEnchantMatcher;
import org.zenith.utility.mixin.accessors.ItemStackAccessor;

public final class ItemNbtUtils {
   private static final Pattern pattern = Pattern.compile("Цена:\\s*([\\d,\\s]+)");
   private static final Pattern pattern2 = Pattern.compile("\\$\\s*([0-9][0-9\\s,]*)");
   private static final Pattern pattern3 = Pattern.compile("\\$(\\d+(?:[\\s,]\\d{3})*(?:\\.\\d{2})?)");
   private static final Pattern pattern4 = Pattern.compile("\\$(\\d+(?:[\\s,  \u200f]\\d{3})*(?:\\.\\d{2})?)");
   private static final Pattern pattern5 = Pattern.compile("\\$\\s*([0-9][0-9\\s,   \u200f]*(?:\\.\\d{1,2})?)");
   private static final Pattern pattern6 = Pattern.compile("\\$\\s*[^0-9]{0,40}([0-9][0-9\\s,   \u200f]*(?:\\.\\d{1,2})?)");
   private static final Pattern pattern7 = Pattern.compile("Цена:(?:.*?\\{\"text\":\"([\\d ]+)\")");
   private static final Pattern pattern8 = Pattern.compile("Цена за 1 ед\\.:(?:.*?\\{\"text\":\"([\\d ]+)\")");
   private static final Pattern pattern9 = Pattern.compile("([0-9][0-9\\s,]*)");
   private static final Pattern pattern10 = Pattern.compile("(?:Количество|Кол-во)\\D{0,3}(\\d[\\d ]*)");
   private static final Pattern pattern11 = Pattern.compile("[x×х]\\s*(\\d{1,6})\\s*$", 2);
   private static final Pattern pattern12 = Pattern.compile("^(\\d{1,6})\\s*[x×х]\\s", 2);
   private static final Pattern pattern13 = Pattern.compile("Цена за 1 ед\\.?:\\s*([\\d ]+)\\s*¤");
   private static final Pattern pattern14 = Pattern.compile("Цена:\\s*([\\d ]+)\\s*¤");
   private static final String text = "literal{ $";
   private static final String text2 = "}[style={color=green}]";
   private static final Map<ItemStack, Integer> items = Collections.synchronizedMap(new LinkedHashMap<ItemStack, Integer>(128, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<ItemStack, Integer> var1) {
         return this.size() > 256;
      }
   });
   private static final Map<ItemStack, NbtCompound> items2 = new LinkedHashMap<ItemStack, NbtCompound>(100, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<ItemStack, NbtCompound> var1) {
         return this.size() > 100;
      }
   };
   public static List<String> items3 = new ArrayList<>();
   private static final Map<ItemNbtUtils.Data, Integer> items4 = new LinkedHashMap<ItemNbtUtils.Data, Integer>(256, 0.75F, true) {
      @Override
      public boolean removeEldestEntry(Entry<ItemNbtUtils.Data, Integer> var1) {
         return this.size() > 512;
      }
   };

   public static int method59(String var0) {
      Matcher var1 = pattern2.matcher(var0);
      if (var1.find()) {
         return method72(var1.group(1));
      } else {
         Matcher var2 = pattern.matcher(var0);
         if (var2.find()) {
            String var4 = var2.group(1).replace(",", "").replace(" ", "");
            return method75(method74(var4));
         } else {
            Matcher var3 = pattern7.matcher(var0);
            return var3.find() ? method72(var3.group(1)) : Integer.MAX_VALUE;
         }
      }
   }

   private static long method60(String var0) {
      int var1 = method59(var0);
      return var1 == Integer.MAX_VALUE ? Long.MAX_VALUE : var1;
   }

   public static boolean method61(ItemStack var0) {
      return var0.getCustomName().getString().contains("★");
   }

   public static int method62(ItemStack var0) {
      if (var0 != null && !var0.isEmpty()) {
         Integer var1 = items.get(var0);
         if (var1 != null) {
            return var1;
         } else {
            int var2 = method63(var0);
            items.put(var0, var2);
            return var2;
         }
      } else {
         return Integer.MAX_VALUE;
      }
   }

   private static int method63(ItemStack var0) {
      int var1 = method66(var0);
      if (var1 == Integer.MAX_VALUE) {
         var1 = method67(var0);
      }

      if (var1 == Integer.MAX_VALUE) {
         String var2 = method76(var0);
         var1 = method69(var2);
      }

      if (var1 == Integer.MAX_VALUE) {
         LoreComponent var3 = var0.get(DataComponentTypes.LORE);
         if (var3 != null) {
            var1 = method59(var3.styledLines().stream().map(Text::getString).collect(Collectors.joining()));
         }
      }

      if (var1 == Integer.MAX_VALUE) {
         var1 = method59(method76(var0));
      }

      return var1;
   }

   public static int method64(ItemStack var0) {
      if (var0 != null && !var0.isEmpty()) {
         int var1 = method62(var0);
         if (var1 == Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
         } else {
            int var2 = Math.max(1, var0.getCount());
            return Math.max(1, var1 / var2);
         }
      } else {
         return Integer.MAX_VALUE;
      }
   }

   public static int method65(String var0, int var1) {
      Matcher var2 = pattern8.matcher(var0);
      if (var2.find()) {
         String var6 = var2.group(1).replace(" ", "");
         return method75(method74(var6));
      } else {
         long var3 = method60(var0);
         int var5 = Math.max(1, var1);
         return var3 == Long.MAX_VALUE ? Integer.MAX_VALUE : method75(Math.max(1L, var3 / var5));
      }
   }

   private static int method66(ItemStack var0) {
      ComponentMap var1 = var0.getComponents();
      if (var1 != null) {
         String var2 = var1.toString();
         int var3 = method70(method73(var2, "literal{ $", "}[style={color=green}]"));
         if (var3 != Integer.MAX_VALUE) {
            return var3;
         }

         int var4 = method69(var2);
         if (var4 != Integer.MAX_VALUE) {
            return var4;
         }
      }

      if (var0.getName() != null) {
         int var5 = method69(var0.getName().getString());
         if (var5 != Integer.MAX_VALUE) {
            return var5;
         }
      }

      return Integer.MAX_VALUE;
   }

   private static int method67(ItemStack var0) {
      LoreComponent var1 = var0.get(DataComponentTypes.LORE);
      if (var1 == null) {
         return Integer.MAX_VALUE;
      } else {
         int var2 = Integer.MAX_VALUE;

         for (Text var4 : var1.styledLines()) {
            if (var4 != null) {
               String var5 = var4.getString();
               if (var5 != null && !var5.isEmpty()) {
                  int var6 = method68(var5);
                  if (var6 != Integer.MAX_VALUE && (var2 == Integer.MAX_VALUE || var6 < var2)) {
                     var2 = var6;
                  }
               }
            }
         }

         return var2;
      }
   }

   private static int method68(String var0) {
      String var1 = var0.toLowerCase(Locale.ROOT);
      if (!var1.contains("$") && !var1.contains("цен") && !var1.contains("price")) {
         return Integer.MAX_VALUE;
      } else {
         int var2 = method69(var0);
         return var2 != Integer.MAX_VALUE ? var2 : method71(var0);
      }
   }

   private static int method69(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         Matcher var1 = pattern5.matcher(var0);
         if (var1.find()) {
            int var2 = method70(var1.group(1));
            if (var2 != Integer.MAX_VALUE) {
               return var2;
            }
         }

         var1 = pattern6.matcher(var0);
         if (var1.find()) {
            int var5 = method70(var1.group(1));
            if (var5 != Integer.MAX_VALUE) {
               return var5;
            }
         }

         var1 = pattern4.matcher(var0);
         return var1.find() ? method70(var1.group(1)) : Integer.MAX_VALUE;
      } else {
         return Integer.MAX_VALUE;
      }
   }

   private static int method70(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         String var1 = var0.replaceAll("[\\s,  \u200f]", "");
         int var2 = var1.indexOf(46);
         if (var2 >= 0) {
            var1 = var1.substring(0, var2);
         }

         if (var1.isEmpty()) {
            return Integer.MAX_VALUE;
         } else {
            try {
               long var3 = Long.parseLong(var1);
               return var3 > 0L && var3 <= 2147483647L ? (int)var3 : Integer.MAX_VALUE;
            } catch (NumberFormatException var5) {
               return Integer.MAX_VALUE;
            }
         }
      } else {
         return Integer.MAX_VALUE;
      }
   }

   private static int method71(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         int var1 = Integer.MAX_VALUE;
         Matcher var2 = pattern9.matcher(var0);

         while (var2.find()) {
            int var3 = method72(var2.group(1));
            if (var3 != Integer.MAX_VALUE && (var1 == Integer.MAX_VALUE || var3 > var1)) {
               var1 = var3;
            }
         }

         return var1;
      } else {
         return Integer.MAX_VALUE;
      }
   }

   private static int method72(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         String var1 = var0.replaceAll("[^0-9]", "");
         if (var1.isEmpty()) {
            return Integer.MAX_VALUE;
         } else {
            try {
               long var2 = Long.parseLong(var1);
               return var2 > 0L && var2 <= 2147483647L ? (int)var2 : Integer.MAX_VALUE;
            } catch (NumberFormatException var4) {
               return Integer.MAX_VALUE;
            }
         }
      } else {
         return Integer.MAX_VALUE;
      }
   }

   private static String method73(String var0, String var1, String var2) {
      if (var0 != null && var1 != null && var2 != null) {
         int var3 = var0.indexOf(var1);
         if (var3 < 0) {
            return null;
         } else {
            int var4 = var3 + var1.length();
            int var5 = var0.indexOf(var2, var4);
            return var5 >= 0 && var5 > var4 ? var0.substring(var4, var5) : null;
         }
      } else {
         return null;
      }
   }

   private static long method74(String var0) {
      try {
         return Long.parseLong(var0);
      } catch (NumberFormatException var2) {
         return Long.MAX_VALUE;
      }
   }

   private static int method75(long var0) {
      return var0 > 2147483647L ? Integer.MAX_VALUE : (int)var0;
   }

   public static String method76(ItemStack var0) {
      return method78(var0).toString();
   }

   public static String method77(ItemStack var0) {
      NbtComponent var1 = var0.get(DataComponentTypes.CUSTOM_DATA);
      if (var1 != null && var1.copyNbt().contains("sphereEffect")) {
         NbtElement var2 = var1.copyNbt().get("sphereEffect");
         MinecraftClient.getInstance().keyboard.setClipboard(var2.toString());
         return var2.toString();
      } else {
         return "";
      }
   }

   public static NbtCompound method78(ItemStack var0) {
      MergedComponentMap var1 = ((ItemStackAccessor)(Object)var0).getComponents();
      ComponentChanges var2 = var1.getChanges();
      ClientWorld var3 = MinecraftClient.getInstance().world;
      return var3 == null
         ? new NbtCompound()
         : items2.computeIfAbsent(
            var0, var2x -> (NbtCompound)ComponentChanges.CODEC.encodeStart(var3.getRegistryManager().getOps(NbtOps.INSTANCE), var2).getOrThrow()
         );
   }

   public static String method79(ItemStack var0) {
      MergedComponentMap var1 = ((ItemStackAccessor)(Object)var0).getComponents();
      ComponentChanges var2 = var1.getChanges();
      ClientWorld var3 = MinecraftClient.getInstance().world;
      return var3 == null
         ? ""
         : items2.computeIfAbsent(
               var0, var2x -> (NbtCompound)ComponentChanges.CODEC.encodeStart(var3.getRegistryManager().getOps(NbtOps.INSTANCE), var2).getOrThrow()
            )
            .toString()
            .replaceAll(",?\\s*PublicBukkitValues:\\{[^}]*\\}", "")
            .replaceAll("'\\{[^']*Истeкaeт:[^']*\\}',?", "")
            .replaceAll(",?UUID:\\[I;[-0-9]+,[-0-9]+,[-0-9]+,[-0-9]+]", "")
            .replaceAll("minecraft:[0-9a-f\\-]{36}", "minecraft:UUID");
   }

   public static ArrayList<EnchantMatcher> method80(ItemStack var0) {
      ArrayList<EnchantMatcher> var1 = new ArrayList<>();
      NbtComponent var2 = var0.get(DataComponentTypes.CUSTOM_DATA);
      if (var2 != null && var2.copyNbt().contains("Enchantments")) {
         NbtList var3 = var2.copyNbt().getListOrEmpty("Enchantments");

         for (int var4 = 0; var4 < var3.size(); var4++) {
            NbtCompound var5 = var3.getCompoundOrEmpty(var4);
            String var6 = var5.getString("id", "");
            int var7 = var5.getInt("lvl", 0);
            var1.add(new CustomEnchantMatcher(var6, var6, var7));
         }
      }

      ItemEnchantmentsComponent var8 = var0.getEnchantments();

      for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry var10 : var8.getEnchantmentEntries()) {
         String var11 = ((RegistryKey)((RegistryEntry)var10.getKey()).getKey().get()).getValue().toString();
         var1.add(new VanillaEnchantMatcher(var11, var11, var10.getIntValue()));
      }

      return var1;
   }

   public static boolean method81(ScreenHandler var0) {
      return var0 == null ? false : method82(var0) || method83(var0);
   }

   public static boolean method82(ScreenHandler var0) {
      return var0.slots.size() == 90 && var0.getSlot(49).getStack().getItem() == Items.NETHER_STAR;
   }

   public static boolean method83(ScreenHandler var0) {
      return var0.slots.size() <= 49 ? false : method85(method84(var0, 45)) || method86(method84(var0, 49));
   }

   private static ItemStack method84(ScreenHandler var0, int var1) {
      return var0.slots.size() <= var1 ? ItemStack.EMPTY : var0.getSlot(var1).getStack();
   }

   private static boolean method85(ItemStack var0) {
      if (!var0.isEmpty() && var0.getItem() == Items.PAPER) {
         String var1 = var0.getName().getString();
         return var1.contains("[⟲]") && var1.toLowerCase(Locale.ROOT).contains("обновить");
      } else {
         return false;
      }
   }

   private static boolean method86(ItemStack var0) {
      return !var0.isEmpty() && var0.getItem() == Items.NETHER_STAR;
   }

   public static boolean method87(ItemStack var0) {
      if (var0 != null && !var0.isEmpty()) {
         String var1 = var0.getName().getString().toLowerCase(Locale.ROOT);
         if (!var1.contains("не актуален") && !var1.contains("продано")) {
            LoreComponent var2 = var0.get(DataComponentTypes.LORE);
            if (var2 == null) {
               return false;
            } else {
               for (Text var4 : var2.styledLines()) {
                  String var5 = var4.getString().toLowerCase(Locale.ROOT);
                  if (var5.contains("продано")) {
                     return true;
                  }
               }

               return false;
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   public static boolean method88(ScreenHandler var0) {
      return var0.slots.size() == 63 && var0.getSlot(0).getStack().getItem() == Items.LIME_STAINED_GLASS_PANE;
   }

   public static void method89(int var0) {
   }

   public static int method90(ItemStack var0) {
      if (var0 != null && !var0.isEmpty()) {
         int var1 = Math.max(method92(var0), method95(var0));
         return Math.max(var0.getCount(), var1);
      } else {
         return 0;
      }
   }

   public static int method91(ItemStack var0) {
      if (var0 != null && !var0.isEmpty()) {
         ItemNbtUtils.Data var1 = new ItemNbtUtils.Data(var0.get(DataComponentTypes.LORE), var0.getName(), Language.getInstance());
         Integer var2 = items4.get(var1);
         if (var2 == null) {
            var2 = Math.max(method92(var0), method95(var0));
            items4.put(var1, var2);
         }

         return Math.max(var0.getCount(), var2);
      } else {
         return 0;
      }
   }

   private static int method92(ItemStack var0) {
      LoreComponent var1 = var0.get(DataComponentTypes.LORE);
      if (var1 == null) {
         return 0;
      } else {
         long var2 = 0L;
         long var4 = 0L;

         for (Text var7 : var1.lines()) {
            String var8 = var7.getString().replace(' ', ' ');
            int var9 = method96(pattern10.matcher(var8));
            if (var9 > 0) {
               return var9;
            }

            if (var4 <= 0L) {
               Matcher var10 = pattern13.matcher(var8);
               if (var10.find()) {
                  var4 = method94(var10.group(1));
                  continue;
               }
            }

            if (var2 <= 0L) {
               Matcher var11 = pattern14.matcher(var8);
               if (var11.find()) {
                  var2 = method94(var11.group(1));
               }
            }
         }

         return method93(var2, var4);
      }
   }

   private static int method93(long var0, long var2) {
      if (var0 > 0L && var2 > 0L && var0 >= var2) {
         long var4 = Math.round((double)var0 / var2);
         if (var4 >= 1L && var4 <= 1000000L) {
            return Math.abs(var4 * var2 - var0) > var4 ? 0 : (int)var4;
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   private static long method94(String var0) {
      try {
         return Long.parseLong(var0.replace(" ", ""));
      } catch (NumberFormatException var2) {
         return 0L;
      }
   }

   private static int method95(ItemStack var0) {
      if (var0.getName() == null) {
         return 0;
      } else {
         String var1 = var0.getName().getString().replace(' ', ' ').trim();
         if (var1.isEmpty()) {
            return 0;
         } else {
            int var2 = method96(pattern11.matcher(var1));
            return var2 > 0 ? var2 : method96(pattern12.matcher(var1));
         }
      }
   }

   private static int method96(Matcher var0) {
      if (!var0.find()) {
         return 0;
      } else {
         try {
            long var1 = Long.parseLong(var0.group(1).replace(" ", ""));
            if (var1 > 0L && var1 <= 1000000L) {
               return (int)var1;
            }
         } catch (NumberFormatException var3) {
         }

         return 0;
      }
   }

   @Generated
   private ItemNbtUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   private static final class Data {
      private final Object obj;
      private final Object obj2;
      private final Object obj3;

      public Object obj() {
         return this.obj;
      }

      public Object obj2() {
         return this.obj2;
      }

      public Object obj3() {
         return this.obj3;
      }

      @Override
      public String toString() {
         return "Data[" + "obj=" + this.obj + ", " + "obj2=" + this.obj2 + ", " + "obj3=" + this.obj3 + "]";
      }

      private Data(Object var1, Object var2, Object var3) {
         this.obj = var1;
         this.obj2 = var2;
         this.obj3 = var3;
      }

      @Override
      public boolean equals(Object var1) {
         return var1 instanceof ItemNbtUtils.Data var2 && var2.obj == this.obj && var2.obj2 == this.obj2 && var2.obj3 == this.obj3;
      }

      @Override
      public int hashCode() {
         return 31 * (31 * System.identityHashCode(this.obj) + System.identityHashCode(this.obj2)) + System.identityHashCode(this.obj3);
      }

      public Object getObj() {
         return this.obj;
      }

      public Object getObj2() {
         return this.obj2;
      }

      public Object getObj3() {
         return this.obj3;
      }
   }
}

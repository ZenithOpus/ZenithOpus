package org.zenith.utility.network;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.datafixers.util.Pair;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.HoverEvent.ShowItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.zenith.MinecraftAccess;
import org.zenith.PacketEvent;
import org.zenith.Zenith;
import org.zenith.client.events.UpdateEvent;

public final class Names implements MinecraftAccess {
   private static final Type type = (new TypeToken<LinkedHashMap<String, String>>() {}).getType();
   private static final String text = "item-names.json";
   private static final long CONST_longValue = 15000L;
   private static final int CONST_intValue = 4000;
   private static final int CONST_intValue2 = 12;
   private static final Map<String, String> items = new ConcurrentHashMap<>();
   private static final Pattern pattern = Pattern.compile("§.");
   private static volatile boolean dirty;
   private static long longValue;
   private static final Names.Data data = new Names.Data(null, null, null, null, null, null, null, null, null);
   private static final Map<ItemStack, Names.Data> items2 = new WeakHashMap<>();
   private static final ExecutorService executorService = Executors.newSingleThreadExecutor(var0 -> {
      Thread var1 = new Thread(var0, "zenith-item-names");
      var1.setDaemon(true);
      return var1;
   });

   public Names() {
      load();
      EventManager.register(this);
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100()) {
         Packet var2 = var1.getPacket3();
         if (var2 instanceof ScreenHandlerSlotUpdateS2CPacket var3) {
            method4515(var3.getStack());
         } else if (var2 instanceof InventoryS2CPacket var4) {
            for (ItemStack var10 : var4.contents()) {
               method4515(var10);
            }
         } else if (var2 instanceof EntityEquipmentUpdateS2CPacket var5) {
            for (Pair var12 : var5.getEquipmentList()) {
               method4515((ItemStack)var12.getSecond());
            }
         } else if (var2 instanceof GameMessageS2CPacket var6) {
            method4514(var6.content(), 0);
         } else if (var2 instanceof ProfilelessChatMessageS2CPacket var7) {
            method4514(var7.message(), 0);
         } else if (var2 instanceof ChatMessageS2CPacket var8) {
            method4514(var8.unsignedContent(), 0);
         }
      }
   }

   private static void method4514(Text var0, int var1) {
      if (var0 != null && var1 <= 12) {
         if ((var0.getStyle() == null ? null : var0.getStyle().getHoverEvent()) instanceof ShowItem var3) {
            method4515(var3.item());
         }

         for (Text var4 : var0.getSiblings()) {
            method4514(var4, var1 + 1);
         }
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null) {
         for (ItemStack var3 : mc.player.getInventory().getMainStacks()) {
            method4515(var3);
         }

         method4515(mc.player.getOffHandStack());
      }

      method4524();
   }

   public static void method4515(ItemStack var0) {
      if (!mc.isOnThread()) {
         String var2 = method4522(var0);
         if (var2 != null) {
            method4521(method4520(var0), var2);
            method4521(method4523(var0), var2);
         }
      } else {
         Names.Data var1 = method4518(var0);
         if (var1.getText2() != null) {
            method4521(var1.getText3(), var1.getText2());
            method4521(var1.getText4(), var1.getText2());
         }
      }
   }

   public static String method4516(ItemStack var0) {
      if (!mc.isOnThread()) {
         return method4522(var0) != null ? null : method4517(method4520(var0), method4523(var0));
      } else {
         Names.Data var1 = method4518(var0);
         return var1.getText2() != null ? null : method4517(var1.getText3(), var1.getText4());
      }
   }

   private static String method4517(String var0, String var1) {
      for (String var5 : new String[]{var0, var1}) {
         if (var5 != null) {
            String var6 = items.get(var5);
            if (var6 != null) {
               return var6;
            }
         }
      }

      return null;
   }

   private static Names.Data method4518(ItemStack var0) {
      if (var0 != null && !var0.isEmpty()) {
         NbtComponent var1 = var0.get(DataComponentTypes.CUSTOM_DATA);
         Text var2 = var0.get(DataComponentTypes.CUSTOM_NAME);
         ItemEnchantmentsComponent var3 = var0.get(DataComponentTypes.ENCHANTMENTS);
         Identifier var4 = var0.get(DataComponentTypes.ITEM_MODEL);
         CustomModelDataComponent var5 = var0.get(DataComponentTypes.CUSTOM_MODEL_DATA);
         Language var6 = Language.getInstance();
         Names.Data var7 = items2.get(var0);
         if (var7 != null && var7.method4526(var1, var2, var3, var4, var5, var6)) {
            return var7;
         } else {
            var7 = new Names.Data(var1, var2, var3, var4, var5, var6, method4522(var0), method4520(var0), method4523(var0));
            items2.put(var0, var7);
            return var7;
         }
      } else {
         return data;
      }
   }

   public static List<String> method4519(ItemStack var0) {
      if (var0 != null && !var0.isEmpty()) {
         NbtComponent var1 = var0.get(DataComponentTypes.CUSTOM_DATA);
         if (var1 == null) {
            return List.of();
         } else {
            NbtCompound var2 = var1.copyNbt();
            if (!var2.contains("Enchantments")) {
               return List.of();
            } else {
               NbtList var3 = var2.getListOrEmpty("Enchantments");
               ArrayList<String> var4 = new ArrayList<>();

               for (int var5 = 0; var5 < var3.size(); var5++) {
                  NbtCompound var6 = var3.getCompoundOrEmpty(var5);
                  String var7 = var6.getString("id", "");
                  if (!var7.isEmpty()) {
                     var4.add(var7 + ":" + var6.getShort("lvl", (short)0));
                  }
               }

               return var4;
            }
         }
      } else {
         return List.of();
      }
   }

   private static String method4520(ItemStack var0) {
      var var1 = new ArrayList<>(method4519(var0));
      if (var1.isEmpty()) {
         return null;
      } else {
         Collections.sort(var1);
         return Registries.ITEM.getId(var0.getItem()) + "|nbt|" + String.join(",", var1);
      }
   }

   private static void method4521(String var0, String var1) {
      if (var0 != null) {
         String var2 = items.get(var0);
         if (!var1.equals(var2)) {
            if (var2 != null || items.size() < 4000) {
               items.put(var0, var1);
               dirty = true;
            }
         }
      }
   }

   public static int size() {
      return items.size();
   }

   private static String method4522(ItemStack var0) {
      if (var0 != null && !var0.isEmpty()) {
         NbtComponent var1 = var0.get(DataComponentTypes.CUSTOM_DATA);
         if (var1 != null) {
            NbtCompound var2 = var1.copyNbt();
            if (var2.contains("itemServiceId")) {
               String var3 = var2.getCompoundOrEmpty("itemServiceId").getString("name", "");
               if (!var3.isEmpty()) {
                  return var3.replace("_", " ");
               }
            }
         }

         Text var4 = var0.get(DataComponentTypes.CUSTOM_NAME);
         if (var4 == null) {
            return null;
         } else {
            String var5 = pattern.matcher(var4.getString()).replaceAll("").trim();
            return var5.isEmpty() ? null : var5;
         }
      } else {
         return null;
      }
   }

   private static String method4523(ItemStack var0) {
      if (var0 != null && !var0.isEmpty()) {
         ItemEnchantmentsComponent var1 = var0.get(DataComponentTypes.ENCHANTMENTS);
         if (var1 != null && !var1.isEmpty()) {
            ArrayList var2 = new ArrayList();

            for (RegistryEntry var4 : var1.getEnchantments()) {
               String var5 = (String)var4.getKey().map(var0x -> ((net.minecraft.registry.RegistryKey<?>)var0x).getValue().toString()).orElse(null);
               if (var5 != null) {
                  var2.add(var5 + ":" + var1.getLevel(var4));
               }
            }

            if (var2.isEmpty()) {
               return null;
            } else {
               Collections.sort(var2);
               StringBuilder var6 = new StringBuilder(Registries.ITEM.getId(var0.getItem()).toString());
               var6.append("|lvl|").append(String.join(",", var2));
               Identifier var7 = var0.get(DataComponentTypes.ITEM_MODEL);
               if (var7 != null) {
                  var6.append("|model=").append(var7);
               }

               CustomModelDataComponent var8 = var0.get(DataComponentTypes.CUSTOM_MODEL_DATA);
               if (var8 != null) {
                  var6.append("|cmd=").append(var8);
               }

               return var6.toString();
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static void method4524() {
      if (dirty) {
         long var0 = System.currentTimeMillis();
         if (var0 - longValue >= 15000L) {
            longValue = var0;
            dirty = false;
            save();
         }
      }
   }

   public static void save() {
      LinkedHashMap var0 = new LinkedHashMap<>(items);

      try {
         executorService.execute(() -> method4525(var0));
      } catch (Exception var2) {
      }
   }

   private static void method4525(Map<String, String> var0) {
      try {
         Path var1 = new File(Zenith.file, "item-names.json").toPath();
         Path var2 = var1.resolveSibling("item-names.json.tmp");
         Files.writeString(var2, new GsonBuilder().create().toJson(var0), Charset.defaultCharset());

         try {
            Files.move(var2, var1, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (AtomicMoveNotSupportedException var4) {
            Files.move(var2, var1, StandardCopyOption.REPLACE_EXISTING);
         }
      } catch (Exception var5) {
      }
   }

   private static void load() {
      File var0 = new File(Zenith.file, "item-names.json");
      if (var0.exists()) {
         try (FileReader var1 = new FileReader(var0)) {
            Map var2 = (Map)new GsonBuilder().create().fromJson(var1, type);
            if (var2 != null) {
               items.putAll(var2);
            }
         } catch (Exception var6) {
         }
      }
   }

   private static final class Data {
      private final NbtComponent nbtComponent;
      private final Text text;
      private final ItemEnchantmentsComponent itemEnchantmentsComponent;
      private final Identifier identifier;
      private final CustomModelDataComponent customModelDataComponent;
      private final Language language;
      private final String text2;
      private final String text3;
      private final String text4;

      public NbtComponent nbtComponent() {
         return this.nbtComponent;
      }

      public Text text() {
         return this.text;
      }

      public ItemEnchantmentsComponent itemEnchantmentsComponent() {
         return this.itemEnchantmentsComponent;
      }

      public Identifier identifier() {
         return this.identifier;
      }

      public CustomModelDataComponent customModelDataComponent() {
         return this.customModelDataComponent;
      }

      public Language language() {
         return this.language;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      public String text4() {
         return this.text4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.nbtComponent, other.nbtComponent) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.itemEnchantmentsComponent, other.itemEnchantmentsComponent) && java.util.Objects.equals(this.identifier, other.identifier) && java.util.Objects.equals(this.customModelDataComponent, other.customModelDataComponent) && java.util.Objects.equals(this.language, other.language) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(nbtComponent, text, itemEnchantmentsComponent, identifier, customModelDataComponent, language, text2, text3, text4);
      }

      @Override
      public String toString() {
         return "Data[" + "nbtComponent=" + this.nbtComponent + ", " + "text=" + this.text + ", " + "itemEnchantmentsComponent=" + this.itemEnchantmentsComponent + ", " + "identifier=" + this.identifier + ", " + "customModelDataComponent=" + this.customModelDataComponent + ", " + "language=" + this.language + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + "]";
      }

      private Data(
         NbtComponent var1,
         Text var2,
         ItemEnchantmentsComponent var3,
         Identifier var4,
         CustomModelDataComponent var5,
         Language var6,
         String var7,
         String var8,
         String var9
      ) {
         this.nbtComponent = var1;
         this.text = var2;
         this.itemEnchantmentsComponent = var3;
         this.identifier = var4;
         this.customModelDataComponent = var5;
         this.language = var6;
         this.text2 = var7;
         this.text3 = var8;
         this.text4 = var9;
      }

      public boolean method4526(NbtComponent var1, Text var2, ItemEnchantmentsComponent var3, Identifier var4, CustomModelDataComponent var5, Language var6) {
         return this.nbtComponent == var1
            && this.text == var2
            && this.itemEnchantmentsComponent == var3
            && this.identifier == var4
            && this.customModelDataComponent == var5
            && this.language == var6;
      }

      public NbtComponent getNbtComponent() {
         return this.nbtComponent;
      }

      public Text getText() {
         return this.text;
      }

      public ItemEnchantmentsComponent getItemEnchantmentsComponent() {
         return this.itemEnchantmentsComponent;
      }

      public Identifier getIdentifier() {
         return this.identifier;
      }

      public CustomModelDataComponent getCustomModelDataComponent() {
         return this.customModelDataComponent;
      }

      public Language getLanguage() {
         return this.language;
      }

      public String getText2() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }

      public String getText4() {
         return this.text4;
      }
   }
}

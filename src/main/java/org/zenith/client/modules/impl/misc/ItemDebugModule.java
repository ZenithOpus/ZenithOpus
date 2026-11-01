package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.datafixers.util.Pair;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.component.Component;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.base.Module;
import org.zenith.client.events.SlotClickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;

@ModuleInfo(
   name = "ItemDebug",
   category = ModuleCategory.MISC,
   description = "module.itemdebug.description"
)
public class ItemDebugModule extends Module {
   public static final ItemDebugModule INSTANCE = new ItemDebugModule();
   private final BooleanSetting clickedSlots = new BooleanSetting("module.itemDebug.clickedSlots", true);
   private final BooleanSetting crosshairTarget = new BooleanSetting("module.itemDebug.crosshairTarget", true);
   private final BooleanSetting nearbyPlayers = new BooleanSetting("module.itemDebug.nearbyPlayers", false);
   private final BooleanSetting drops = new BooleanSetting("module.itemDebug.drops", false);
   private final BooleanSetting equipmentPackets = new BooleanSetting("module.itemDebug.equipmentPackets", true);
   private final BooleanSetting containerPackets = new BooleanSetting("module.itemDebug.containerPackets", false);
   private final BooleanSetting chatSummary = new BooleanSetting("module.itemDebug.chatSummary", true);
   private final BooleanSetting toFile = new BooleanSetting("module.itemDebug.toFile", true);
   private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
   private static final double CONST_doubleValue = 16.0;
   private static final long CONST_longValue = 500L;
   private final Map<String, String> items = new HashMap<>();
   private PrintWriter printWriter;
   private File file;
   private long longValue;

   private ItemDebugModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.items.clear();
      this.longValue = 0L;
      if (this.toFile.isEnabled()) {
         this.method4627();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.items.clear();
      this.method4629();
   }

   @EventTarget
   public void method4620(SlotClickEvent var1) {
      if (this.clickedSlots.isEnabled() && mc.player != null) {
         ScreenHandler var2 = mc.player.currentScreenHandler;
         if (var2 != null) {
            int var3 = var1.getIntValue2();
            if (var2.isValid(var3)) {
               ItemStack var4 = var2.getSlot(var3).getStack();
               if (!var4.isEmpty()) {
                  this.method4624("click", "self", "slot" + var3, var4, true);
               }
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100()) {
         Packet var2 = var1.getPacket3();
         if (var2 instanceof EntityEquipmentUpdateS2CPacket var10) {
            if (this.equipmentPackets.isEnabled()) {
               int var12 = var10.getEntityId();
               ArrayList<Pair> var14 = new ArrayList();

               for (Pair var19 : var10.getEquipmentList()) {
                  ItemStack var8 = (ItemStack)var19.getSecond();
                  if (var8 != null && !var8.isEmpty()) {
                     var14.add(Pair.of((EquipmentSlot)var19.getFirst(), var8.copy()));
                  }
               }

               if (!var14.isEmpty()) {
                  this.method4621(() -> {
                     String var3x = this.method4623(var12);

                     for (Object var5x_o : (Iterable<?>)(Object)(var14)) {
                        Pair var5x = (Pair)var5x_o;
                        this.method4624("packet:equip", var3x, ((EquipmentSlot)var5x.getFirst()).asString(), (ItemStack)var5x.getSecond(), false);
                     }
                  });
               }
            }
         } else if (var2 instanceof ScreenHandlerSlotUpdateS2CPacket var9) {
            if (this.containerPackets.isEnabled()) {
               ItemStack var11 = var9.getStack();
               if (var11 != null && !var11.isEmpty()) {
                  ItemStack var13 = var11.copy();
                  String var16 = "window" + var9.getSyncId();
                  String var18 = "slot" + var9.getSlot();
                  this.method4621(() -> this.method4624("packet:slot", var16, var18, var13, false));
               }
            }
         } else {
            if (var2 instanceof InventoryS2CPacket var3) {
               if (!this.containerPackets.isEnabled()) {
                  return;
               }

               var var4 = var3.contents();
               ArrayList<Pair> var5 = new ArrayList();

               for (int var6 = 0; var6 < var4.size(); var6++) {
                  ItemStack var7 = (ItemStack)var4.get(var6);
                  if (var7 != null && !var7.isEmpty()) {
                     var5.add(Pair.of("slot" + var6, var7.copy()));
                  }
               }

               if (var5.isEmpty()) {
                  return;
               }

               String var15 = "window" + var3.syncId();
               this.method4621(() -> {
                  for (Object var4x_o : (Iterable<?>)(Object)(var5)) {
                     Pair var4x = (Pair)var4x_o;
                     this.method4624("packet:inv", var15, (String)var4x.getFirst(), (ItemStack)var4x.getSecond(), false);
                  }
               });
            }
         }
      }
   }

   private void method4621(Runnable var1) {
      mc.execute(() -> {
         if (this.isEnabled()) {
            var1.run();
         }
      });
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null) {
         long var2 = System.currentTimeMillis();
         if (var2 - this.longValue >= 500L) {
            this.longValue = var2;
            if (this.crosshairTarget.isEnabled() && mc.targetedEntity instanceof LivingEntity var4) {
               this.method4622("crosshair", var4);
            }

            if (this.nearbyPlayers.isEnabled()) {
               for (PlayerEntity var10 : mc.world.getPlayers()) {
                  if (var10 != mc.player && !(var10.squaredDistanceTo(mc.player) > 256.0)) {
                     this.method4622("nearby", var10);
                  }
               }
            }

            if (this.drops.isEnabled()) {
               for (Entity var11 : mc.world.getEntities()) {
                  if (var11 instanceof ItemEntity var6 && !(var6.squaredDistanceTo(mc.player) > 256.0)) {
                     ItemStack var7 = var6.getStack();
                     if (var7 != null && !var7.isEmpty()) {
                        this.method4624("drop", "id" + var6.getId(), "stack", var7, false);
                     }
                  }
               }
            }
         }
      }
   }

   private void method4622(String var1, LivingEntity var2) {
      String var3 = var2.getName().getString();

      for (EquipmentSlot var7 : EquipmentSlot.values()) {
         ItemStack var8;
         try {
            var8 = var2.getEquippedStack(var7);
         } catch (Exception var10) {
            continue;
         }

         if (var8 != null && !var8.isEmpty()) {
            this.method4624(var1, var3, var7.asString(), var8, false);
         }
      }
   }

   private String method4623(int var1) {
      if (mc.world == null) {
         return "id" + var1;
      } else {
         Entity var2 = mc.world.getEntityById(var1);
         return var2 == null ? "id" + var1 : var2.getName().getString() + "/id" + var1;
      }
   }

   private void method4624(String var1, String var2, String var3, ItemStack var4, boolean var5) {
      String var6 = var1 + "|" + var2 + "|" + var3;
      String var7 = this.method4626(var4);
      if (var5 || !var7.equals(this.items.get(var6))) {
         this.items.put(var6, var7);
         StringBuilder var8 = new StringBuilder();
         var8.append("==================== ItemDebug [")
            .append(var1)
            .append("] ")
            .append(simpleDateFormat.format(new Date()))
            .append(" ====================\n");
         var8.append("Owner: ").append(var2).append("  Slot: ").append(var3).append("\n");
         var8.append("Item: ").append(Registries.ITEM.getId(var4.getItem())).append("\n");
         var8.append("Name: ").append(var4.getName().getString()).append("\n");
         var8.append("Count: ").append(var4.getCount()).append("  Damage: ").append(var4.getDamage()).append("\n");
         Text var9 = var4.get(DataComponentTypes.CUSTOM_NAME);
         var8.append("CustomName: ").append(var9 == null ? "<none>" : var9.getString()).append("\n");
         Text var10 = var4.get(DataComponentTypes.ITEM_NAME);
         var8.append("ItemName: ").append(var10 == null ? "<none>" : var10.getString()).append("\n");
         LoreComponent var11 = var4.get(DataComponentTypes.LORE);
         if (var11 != null && !var11.lines().isEmpty()) {
            var8.append("Lore:\n");

            for (Text var13 : var11.lines()) {
               var8.append("  ").append(var13.getString()).append("\n");
            }
         } else {
            var8.append("Lore: <none>\n");
         }

         ItemEnchantmentsComponent var16 = var4.get(DataComponentTypes.ENCHANTMENTS);
         if (var16 != null && !var16.isEmpty()) {
            var8.append("Enchantments:\n");

            for (RegistryEntry var14 : var16.getEnchantments()) {
               String var15 = (String)var14.getKey().map(var0 -> ((net.minecraft.registry.RegistryKey<?>)var0).getValue().toString()).orElse((Object)"unknown");
               var8.append("  ").append(var15).append(" ").append(var16.getLevel(var14)).append("\n");
            }
         } else {
            var8.append("Enchantments: <none>\n");
         }

         var8.append("ComponentChanges: ").append(var4.getComponentChanges()).append("\n");
         var8.append("Components:\n");

         for (Component var19 : var4.getComponents()) {
            var8.append("  ").append(var19.type()).append(" = ").append(var19.value()).append("\n");
         }

         var8.append("===================================================");
         System.out.println(var8);
         this.method4628(var8.toString());
         if (this.chatSummary.isEnabled()) {
            ChatLogger.method2019(this.method4625(var1, var2, var3, var4));
         }
      }
   }

   private String method4625(String var1, String var2, String var3, ItemStack var4) {
      StringBuilder var5 = new StringBuilder();
      if (var4.contains(DataComponentTypes.CUSTOM_NAME)) {
         var5.append("name ");
      }

      if (var4.contains(DataComponentTypes.ITEM_NAME)) {
         var5.append("itemname ");
      }

      if (var4.contains(DataComponentTypes.LORE)) {
         var5.append("lore ");
      }

      if (var4.contains(DataComponentTypes.ENCHANTMENTS)) {
         var5.append("ench ");
      }

      if (var4.contains(DataComponentTypes.CUSTOM_DATA)) {
         var5.append("nbt ");
      }

      if (var4.contains(DataComponentTypes.CUSTOM_MODEL_DATA)) {
         var5.append("model ");
      }

      if (var4.contains(DataComponentTypes.ITEM_MODEL)) {
         var5.append("itemmodel ");
      }

      if (var4.contains(DataComponentTypes.DAMAGE)) {
         var5.append("damage ");
      }

      if (var5.isEmpty()) {
         var5.append("<empty>");
      }

      int var6 = var4.getComponentChanges().size();
      return var1 + " " + var2 + " " + var3 + ": " + Registries.ITEM.getId(var4.getItem()) + " changes=" + var6 + " [" + var5.toString().trim() + "]";
   }

   private String method4626(ItemStack var1) {
      return Registries.ITEM.getId(var1.getItem()) + "x" + var1.getCount() + ":" + var1.getComponentChanges().hashCode();
   }

   private void method4627() {
      try {
         File var1 = new File(mc.runDirectory, "zenith-itemdump");
         if (!var1.exists() && !var1.mkdirs()) {
            return;
         }

         this.file = new File(var1, "dump-" + System.currentTimeMillis() + ".log");
         this.printWriter = new PrintWriter(new FileWriter(this.file, true), true);
         ChatLogger.method2019("ItemDebug: лог " + this.file.getAbsolutePath());
      } catch (Exception var2) {
         this.printWriter = null;
         this.file = null;
         ChatLogger.method2022("ItemDebug: не открылся файл лога — " + var2.getMessage());
      }
   }

   private void method4628(String var1) {
      if (this.toFile.isEnabled()) {
         if (this.printWriter == null) {
            this.method4627();
         }

         if (this.printWriter != null) {
            this.printWriter.println(var1);
         }
      }
   }

   private void method4629() {
      if (this.printWriter != null) {
         try {
            this.printWriter.close();
         } catch (Exception var2) {
         }

         this.printWriter = null;
      }
   }
}

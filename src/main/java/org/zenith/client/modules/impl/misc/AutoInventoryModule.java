package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.collections.EmptyList;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.zenith.ChatLogger;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.api.InventoryPreset;
import org.zenith.utility.api.ServerNameNormalizer;
import org.zenith.utility.game.InventorySnapshot;
import org.zenith.utility.game.ItemMatcher;
import org.zenith.utility.game.ItemNbtUtils;

@ModuleInfo(
   name = "AutoInventory",
   category = ModuleCategory.MISC,
   description = "module.autoinventory.description"
)
public class AutoInventoryModule extends Module {
   public static final AutoInventoryModule INSTANCE = new AutoInventoryModule();
   private static final int CONST_intValue = 36;
   private static final long CONST_longValue = 1000L;
   private static final long CONST_longValue2 = 300L;
   private static final long CONST_longValue3 = 100L;
   private static final long CONST_longValue4 = 700L;
   private static final int CONST_intValue2 = 45;
   private static final int CONST_intValue3 = 50;
   private static final int CONST_intValue4 = 2;
   private static final int CONST_intValue5 = 3;
   private static final int CONST_intValue6 = 4;
   private static final int CONST_intValue7 = 27;
   private static final int CONST_intValue8 = 0;
   private static final String text = "[Кyпить]";
   private static final long CONST_longValue5 = 6000L;
   private static final long CONST_longValue6 = 6000L;
   private static final int CONST_intValue9 = 3;
   private static final Pattern pattern = Pattern.compile("Цена за 1 ед\\.?:\\s*([\\d ]+)\\s*¤");
   private static final Pattern pattern2 = Pattern.compile("Цена:\\s*([\\d ]+)\\s*¤");
   private static final Pattern pattern3 = Pattern.compile("\\$\\s*(?:(?:Цена|Ценa):\\s*)?([0-9][\\d, ]*)");
   private static final Pattern pattern4 = Pattern.compile("(\\d+)/(\\d+)");
   private static final String text2 = "следующая страница";
   private static final String text3 = "предыдущая страница";
   private static final String text4 = "нажмите, чтобы забрать";
   private static final int CONST_intValue10 = 4;
   private static final int CONST_intValue11 = 10;
   private static final int CONST_intValue12 = 8;
   private static final int CONST_intValue13 = 200;
   private static final String text5 = "Funtime 1.21";
   private static final String text6 = "HolyWorld";
   private static final String text7 = "SpookyTime";
   private final InventoryPreset inventoryPreset = new InventoryPreset("Funtime 1.21");
   private final InventoryPreset inventoryPreset2 = new InventoryPreset("HolyWorld");
   private final InventoryPreset inventoryPreset3 = new InventoryPreset("SpookyTime");
   private final ModeSetting server = new ModeSetting(
      "module.autoInventory.server", "module.autoInventory.server.desc", "Funtime 1.21", "HolyWorld", "SpookyTime"
   );
   private final ButtonSetting openMenu = new ButtonSetting("module.autoInventory.openMenu", () -> Zenith.getInstance().getNLMenuScreen().openTool(1));
   private final BooleanSetting anarchyHop = new BooleanSetting(
      "module.autoInventory.anarchyHop", "module.autoInventory.anarchyHop.desc", false, () -> this.server.method2101("Funtime 1.21")
   );
   private final InventorySnapshot inventorySnapshot = new InventorySnapshot();
   private final ServerNameNormalizer serverNameNormalizer = new ServerNameNormalizer();
   private final List<AutoInventoryModule.Entry2> items = new ArrayList<>();
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private AutoInventoryModule.Type type = AutoInventoryModule.Type.IDLE;
   private AutoInventoryModule.Entry2 entry2;
   private int intValue = -1;
   private int intValue2;
   private int intValue3;
   private AutoInventoryModule.Entry3 entry3;
   private int intValue4;
   private int intValue5;
   private int intValue6;
   private long longValue;
   private final AutoInventoryModule.Entry entry = new AutoInventoryModule.Entry();
   private final AutoInventoryModule.Entry entry4 = new AutoInventoryModule.Entry();
   private final List<AutoInventoryModule.Data> items2 = new ArrayList<>();
   private AutoInventoryModule.Data data;
   private volatile int intValue7;
   private int intValue8;
   private int intValue9;
   private volatile boolean flag;
   private volatile boolean flag2;
   private volatile boolean flag3;
   private volatile boolean flag4;
   private boolean flag5;

   private AutoInventoryModule() {
   }

   @Override
   public void onEnable() {
      this.method160();
      this.method163();
      this.intValue4 = this.items.size();
      if (this.items != null && this.items.getClass() == EmptyList.class ? true : this.items.isEmpty()) {
         if (this.method207().isEmpty()) {
            ChatLogger.method2021("АвтоСбор: набор пуст, добавьте предметы через меню");
         } else {
            ChatLogger.method2019("АвтоСбор: все предметы из набора уже есть в инвентаре");
         }

         this.type = AutoInventoryModule.Type.FINISHED;
         super.onEnable();
      } else {
         super.onEnable();
         if (this.method210()) {
            ChatLogger.method2021("АвтоСбор: берутся самые дешёвые лоты из тех, что есть, — будьте внимательны");
            this.entry.method213();
            this.method184();
         } else {
            this.method164();
         }
      }
   }

   @Override
   public void onDisable() {
      this.method160();
      super.onDisable();
   }

   private void method160() {
      this.items.clear();
      this.stopWatch.reset();
      this.stopWatch2.reset();
      this.type = AutoInventoryModule.Type.IDLE;
      this.entry2 = null;
      this.intValue = -1;
      this.intValue2 = 0;
      this.intValue3 = 0;
      this.entry3 = null;
      this.intValue4 = 0;
      this.intValue5 = 0;
      this.intValue6 = 0;
      this.longValue = 0L;
      this.items2.clear();
      this.data = null;
      this.intValue7 = 0;
      this.intValue8 = 0;
      this.intValue9 = 0;
      this.flag = false;
      this.flag2 = false;
      this.flag3 = false;
      this.flag4 = false;
      this.flag5 = false;
   }

   @EventTarget
   public void method161(TickEvent var1) {
      if (this.type == AutoInventoryModule.Type.FINISHED) {
         this.method162();
         this.setToggled(false);
      } else if (mc.player == null || mc.world == null || mc.player.networkHandler == null) {
         this.setToggled(false);
      } else if (this.method210()) {
         this.method177();
      } else {
         switch (this.type) {
            case IDLE:
               if (this.stopWatch.method7546(1000L)) {
                  this.method164();
               }
               break;
            case AUCTION:
               if (this.stopWatch.method7546(6000L)) {
                  this.method166();
                  return;
               }

               this.method167();
               break;
            case PURCHASE:
               if (this.stopWatch.method7546(6000L)) {
                  this.method166();
                  return;
               }

               this.method170();
               break;
            case INVENTORY:
               this.method176();
         }
      }
   }

   private void method162() {
      if (this.intValue4 > 0) {
         String var1 = "АвтоСбор: закуплено " + this.intValue5 + " из " + this.intValue4;
         if (this.intValue6 > 0) {
            var1 = var1 + ", пропущено " + this.intValue6;
         }

         if (this.longValue > 0L) {
            var1 = var1 + ", потрачено " + this.longValue;
         }

         ChatLogger.method2019(var1);
         this.intValue4 = 0;
      }
   }

   private void method163() {
      this.items.clear();
      HashMap var1 = new HashMap();
      HashMap var2 = new HashMap();
      HashMap var3 = new HashMap();
      InventoryPreset var4 = this.method207();

      for (int var5 = 0; var5 < 36; var5++) {
         ItemMatcher var6 = var4.method870(var5);
         if (var6 != null && !var6.getItemStack().isEmpty()) {
            String var7 = var6.getText22();
            if (var7 != null && !var7.isBlank()) {
               int var8 = var4.method877(var5);
               float var9 = var4.method881(var5);
               long var10 = var4.method879(var5);
               String var12 = this.method203(var6, var9);
               int var13 = ((Integer)var1.computeIfAbsent(var12, var3x -> this.method202(var6, var9)));
               int var14 = ((Integer)var2.getOrDefault(var12, 0)) + var8;
               int var15 = ((Integer)var3.getOrDefault(var12, var13));
               var2.put(var12, var14);
               var3.put(var12, Math.max(var15, var14));
               if (var15 < var14) {
                  this.items.add(new AutoInventoryModule.Entry2(var6, var7, var14 - var15, var14, var9, var10));
               }
            }
         }
      }
   }

   private void method164() {
      for (this.entry2 = null; this.items != null && this.items.getClass() == EmptyList.class ? false : !this.items.isEmpty(); this.intValue5++) {
         AutoInventoryModule.Entry2 var1 = this.items.removeFirst();
         if (this.method202(var1.itemMatcher, var1.floatValue) < var1.intValue2) {
            this.intValue3 = 0;
            this.method165(var1);
            return;
         }
      }

      this.type = AutoInventoryModule.Type.FINISHED;
   }

   private void method165(AutoInventoryModule.Entry2 var1) {
      if (mc.player != null && mc.world != null && mc.player.networkHandler != null) {
         this.method205();
         this.entry3 = null;
         this.entry2 = var1;
         var1.intValue3 = this.method202(var1.itemMatcher, var1.floatValue);
         mc.player.networkHandler.sendChatCommand("ah search " + var1.text);
         this.stopWatch.reset();
         this.stopWatch2.reset();
         this.type = AutoInventoryModule.Type.AUCTION;
         this.intValue = -1;
      } else {
         this.items.addFirst(var1);
         this.entry2 = null;
         this.type = AutoInventoryModule.Type.IDLE;
         this.stopWatch.reset();
      }
   }

   private void method166() {
      if (this.entry2 == null) {
         this.type = AutoInventoryModule.Type.IDLE;
         this.stopWatch.reset();
         this.method164();
      } else {
         int var1;
         if ((var1 = this.method202(this.entry2.itemMatcher, this.entry2.floatValue)) > this.entry2.intValue3) {
            this.method175();
            this.intValue3 = 0;
         } else {
            this.intValue3++;
         }

         this.entry3 = null;
         if (var1 >= this.entry2.intValue2) {
            this.intValue5++;
            this.entry2 = null;
            this.type = AutoInventoryModule.Type.IDLE;
            this.stopWatch.reset();
         } else if (this.intValue3 >= 3) {
            ChatLogger.method2021("АвтоСбор: не удалось закупить «" + this.entry2.text + "», пропускаю");
            this.intValue6++;
            this.entry2 = null;
            this.type = AutoInventoryModule.Type.IDLE;
            this.stopWatch.reset();
            this.method164();
         } else {
            this.method165(
               new AutoInventoryModule.Entry2(
                  this.entry2.itemMatcher, this.entry2.text, this.entry2.intValue2 - var1, this.entry2.intValue2, this.entry2.floatValue, this.entry2.longValue
               )
            );
         }
      }
   }

   private void method167() {
      if (mc.interactionManager != null) {
         if (mc.currentScreen != null && mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
            ScreenHandler var1 = mc.player.currentScreenHandler;
            if (ItemNbtUtils.method81(var1)) {
               if (this.entry2 == null) {
                  this.type = AutoInventoryModule.Type.IDLE;
                  this.stopWatch.reset();
               } else if (this.intValue != var1.syncId) {
                  this.intValue = var1.syncId;
                  this.stopWatch2.reset();
               } else if (this.stopWatch2.method7546(300L)) {
                  int var2 = this.method168(var1, this.entry2);
                  this.intValue = -1;
                  if (var2 >= 0) {
                     this.type = AutoInventoryModule.Type.PURCHASE;
                     this.intValue2 = this.method210() ? 0 : var2;
                     this.intValue = -1;
                     this.stopWatch.reset();
                     this.stopWatch2.reset();
                  } else if (!this.method169(var1)) {
                     ChatLogger.method2021("АвтоСбор: нет подходящих лотов «" + this.entry2.text + "»");
                     this.intValue6++;
                     this.entry2 = null;
                     this.type = AutoInventoryModule.Type.IDLE;
                     this.stopWatch.reset();
                     this.method164();
                  }
               }
            }
         }
      }
   }

   private int method168(ScreenHandler var1, AutoInventoryModule.Entry2 var2) {
      this.entry3 = null;
      if (var2 != null && mc.interactionManager != null && mc.player != null) {
         Slot var3 = null;
         long var4 = Long.MAX_VALUE;
         int var6 = Math.min(45, var1.slots.size());

         for (int var7 = 0; var7 < var6; var7++) {
            Slot var8 = var1.getSlot(var7);
            if (var8 != null && var8.hasStack()) {
               ItemStack var9 = var8.getStack();
               long var10;
               if (var2.itemMatcher.method2672(var9)
                  && this.method204(var9, var2.floatValue)
                  && !ItemNbtUtils.method87(var9)
                  && (var10 = this.method198(var9)) != Long.MAX_VALUE
                  && !this.method197(var2, var9, var10)
                  && var10 < var4) {
                  var3 = var8;
                  var4 = var10;
               }
            }
         }

         if (var3 == null) {
            return -1;
         } else {
            int var12 = ItemNbtUtils.method90(var3.getStack());
            boolean var13 = !this.method210() && var12 > var2.intValue;
            int var14 = Math.max(1, var13 ? var2.intValue : var12);
            String var15 = var3.getStack().getName().getString();
            this.entry3 = new AutoInventoryModule.Entry3(
               var3.getStack(), var15 != null && !var15.isBlank() ? var15 : var2.text, var14, this.method200(var4, var14)
            );
            int var11 = var13 ? 1 : 0;
            mc.interactionManager.clickSlot(var1.syncId, var3.id, var11, SlotActionType.PICKUP, mc.player);
            return var13 ? Math.max(0, var2.intValue - 1) : 0;
         }
      } else {
         return -1;
      }
   }

   private boolean method169(ScreenHandler var1) {
      if (var1.slots.size() <= 50) {
         return false;
      } else if (mc.interactionManager != null && mc.player != null) {
         Slot var2 = var1.getSlot(50);
         if (var2 != null && var2.hasStack()) {
            if (var2.getStack().getItem() != Items.LIME_DYE) {
               return false;
            } else {
               this.entry3 = null;
               mc.interactionManager.clickSlot(var1.syncId, var2.id, 0, SlotActionType.PICKUP, mc.player);
               this.intValue = -1;
               this.stopWatch.reset();
               this.stopWatch2.reset();
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void method170() {
      if (mc.interactionManager != null) {
         if (mc.currentScreen != null && mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
            ScreenHandler var1 = mc.player.currentScreenHandler;
            if (ItemNbtUtils.method81(var1)) {
               this.method171(var1);
            } else if (this.method210()) {
               this.method172(var1);
            } else if (mc.currentScreen.getTitle() != null && mc.currentScreen.getTitle().getString().contains("Покупка предмета")) {
               if (var1.slots.size() > 4) {
                  if (this.intValue != var1.syncId) {
                     this.intValue = var1.syncId;
                     this.stopWatch2.reset();
                  } else if (this.stopWatch2.method7546(100L)) {
                     if (this.intValue2 >= 10) {
                        this.method174(var1, 4);
                        this.intValue2 -= 10;
                        this.stopWatch2.reset();
                     } else if (this.intValue2 > 0) {
                        this.method174(var1, 3);
                        this.intValue2--;
                        this.stopWatch2.reset();
                     } else if (var1.slots.size() > 2) {
                        this.method174(var1, 2);
                        this.method175();
                        this.type = AutoInventoryModule.Type.INVENTORY;
                        this.intValue = -1;
                        this.stopWatch.reset();
                        this.stopWatch2.reset();
                     }
                  }
               }
            }
         }
      }
   }

   private void method171(ScreenHandler var1) {
      if (this.entry2 == null) {
         this.type = AutoInventoryModule.Type.IDLE;
         this.stopWatch.reset();
      } else if (this.stopWatch2.method7546(300L)) {
         int var2 = this.method168(var1, this.entry2);
         this.intValue = -1;
         if (var2 < 0) {
            if (!this.method169(var1)) {
               this.type = AutoInventoryModule.Type.IDLE;
               this.stopWatch.reset();
               this.method164();
            }
         } else {
            this.intValue2 = this.method210() ? 0 : var2;
            this.stopWatch.reset();
            this.stopWatch2.reset();
         }
      }
   }

   private void method172(ScreenHandler var1) {
      if (mc.currentScreen.getTitle() != null) {
         if (mc.currentScreen.getTitle().getString().contains("Подтверждение покупки")) {
            int var2;
            if ((var2 = this.method173(var1)) >= 0) {
               if (this.intValue != var1.syncId) {
                  this.intValue = var1.syncId;
                  this.stopWatch2.reset();
               } else if (this.stopWatch2.method7546(100L)) {
                  this.method174(var1, var2);
                  this.type = AutoInventoryModule.Type.INVENTORY;
                  this.intValue = -1;
                  this.stopWatch.reset();
                  this.stopWatch2.reset();
               }
            }
         }
      }
   }

   private int method173(ScreenHandler var1) {
      int var2;
      if ((var2 = Math.max(0, var1.slots.size() - 36)) == 27) {
         return 0;
      } else if (var2 <= 27) {
         return -1;
      } else {
         int var3 = Math.min(var2, var1.slots.size());

         for (int var4 = 0; var4 < var3; var4++) {
            Slot var5 = var1.getSlot(var4);
            if (var5 != null && var5.hasStack()) {
               ItemStack var6 = var5.getStack();
               if (var6.getItem() == Items.PAPER && "[Кyпить]".equals(var6.getName().getString())) {
                  return var4;
               }
            }
         }

         return -1;
      }
   }

   private void method174(ScreenHandler var1, int var2) {
      if (var2 >= 0 && var2 < var1.slots.size()) {
         if (mc.interactionManager != null && mc.player != null) {
            mc.interactionManager.clickSlot(var1.syncId, var1.getSlot(var2).id, 0, SlotActionType.PICKUP, mc.player);
         }
      }
   }

   private void method175() {
      if (this.entry3 != null) {
         this.inventorySnapshot.method1862(this.entry3.itemStack, this.entry3.text, this.entry3.intValue, this.entry3.longValue);
         this.longValue = this.longValue + Math.max(0L, this.entry3.longValue);
         this.entry3 = null;
      }
   }

   private void method176() {
      if (this.entry2 == null) {
         this.type = AutoInventoryModule.Type.IDLE;
         this.stopWatch.reset();
      } else if (this.method202(this.entry2.itemMatcher, this.entry2.floatValue) < this.entry2.intValue2) {
         if (this.stopWatch2.method7546(700L)) {
            this.method166();
         }
      } else {
         this.method175();
         this.intValue5++;
         this.entry2 = null;
         this.type = AutoInventoryModule.Type.IDLE;
         this.stopWatch.reset();
      }
   }

   private void method177() {
      if (this.flag5) {
         ChatLogger.method2019("АвтоСбор: работа принудительно завершена");
         this.type = AutoInventoryModule.Type.FINISHED;
      } else if (this.flag2) {
         ChatLogger.method2021("АвтоСбор: не хватает монет, выключаюсь");
         this.method192();
         this.type = AutoInventoryModule.Type.FINISHED;
      } else {
         if (this.flag) {
            this.flag = false;
            this.data = null;
            this.items2.clear();
            this.entry3 = null;
         }

         if (this.flag3) {
            this.flag3 = false;
            this.intValue8 = 10;
         }

         if (this.intValue8 > 0) {
            this.intValue8--;
            ThreadLocalRandom var1 = ThreadLocalRandom.current();
            mc.player.setYaw(mc.player.getYaw() + (float)var1.nextDouble(-2.0, 2.0));
            mc.player.setPitch(Math.max(-90.0F, Math.min(90.0F, mc.player.getPitch() + (float)var1.nextDouble(-1.0, 1.0))));
         }

         if (this.flag4) {
            this.flag4 = false;
            if (this.anarchyHop.isEnabled()) {
               ThreadLocalRandom var4 = ThreadLocalRandom.current();
               int var2 = var4.nextBoolean() ? var4.nextInt(205, 231) : var4.nextInt(305, 325);
               mc.player.networkHandler.sendChatCommand("an" + var2);
               ChatLogger.method2019("АвтоСбор: аукцион тормозит, перехожу на " + var2 + " анархию");
            }
         }

         this.method182();
         if (this.entry2 == null) {
            this.type = AutoInventoryModule.Type.FINISHED;
         } else if (mc.player.getInventory().getEmptySlot() == -1) {
            ChatLogger.method2021("АвтоСбор: нет свободных слотов в инвентаре, освободите место");
            this.method192();
            this.type = AutoInventoryModule.Type.FINISHED;
         } else {
            this.intValue7++;
            if (mc.currentScreen instanceof GenericContainerScreen var5) {
               ScreenHandler var7 = var5.getScreenHandler();
               String var3 = var5.getTitle().getString().replaceAll("§.", "").toLowerCase(Locale.ROOT).trim();
               if (var3.contains(this.entry2.text.toLowerCase(Locale.ROOT)) || ItemNbtUtils.method83(var7)) {
                  this.method178(var7, var3, this.entry2);
               } else if (!var3.contains("подтверждение покупки") && !var3.contains("подозрительная цена")) {
                  this.method192();
               } else {
                  this.type = AutoInventoryModule.Type.PURCHASE;
                  if (this.entry.method214(200L, 90L)) {
                     this.method191(var7, "[Кyпить]");
                     this.entry.reset();
                  }
               }
            } else {
               this.type = AutoInventoryModule.Type.AUCTION;
               if (mc.player.age >= 200 && this.entry.method214(1000L, 300L)) {
                  mc.player.networkHandler.sendChatCommand("ah search " + this.entry2.text);
                  this.entry.reset();
                  this.intValue7 = 0;
               }
            }
         }
      }
   }

   private void method178(ScreenHandler var1, String var2, AutoInventoryModule.Entry2 var3) {
      this.type = AutoInventoryModule.Type.AUCTION;
      int var4 = this.method202(var3.itemMatcher, var3.floatValue);
      if (this.entry.method214(300L, 80L) && var4 >= var3.intValue2) {
         ChatLogger.method2019("АвтоСбор: «" + var3.text + "» куплен, перехожу к следующему");
         this.intValue5++;
         this.method184();
         this.method192();
      } else if (this.entry4.method214(1000L, 150L)) {
         boolean var5 = InventoryPreset.method886(var3.itemMatcher);
         if (this.entry.method214(var5 ? 400L : 550L, 120L)) {
            Matcher var6 = pattern4.matcher(var2);
            int var7 = 1;
            int var8 = 1;
            if (var6.find()) {
               var7 = Integer.parseInt(var6.group(1));
               var8 = Integer.parseInt(var6.group(2));
            }

            if (var5) {
               this.method179(var1, var3, var4, var7, Math.min(4, var8));
            } else {
               this.method180(var1, var3, var4, var7, Math.min(4, var8));
            }

            this.entry.reset();
         }
      }
   }

   private void method179(ScreenHandler var1, AutoInventoryModule.Entry2 var2, int var3, int var4, int var5) {
      List<Slot> var6 = this.method185(var1, var2);
      if (this.data != null) {
         if (var4 == this.data.intValue) {
            Slot var9 = var6.stream()
               .filter(var1x -> this.method190(var1x.getStack()) == this.data.longValue)
               .findFirst()
               .orElseGet(() -> var6.stream().min(Comparator.comparingLong(var1xx -> this.method190(var1xx.getStack()))).orElse(null));
            if (var9 != null) {
               this.method181(var1, var9, var2, var3);
            } else {
               ChatLogger.method2019("АвтоСбор: лот пропал, пересканирую");
               this.data = null;
               this.items2.clear();
               this.method192();
            }
         } else {
            this.method191(var1, var4 < this.data.intValue ? "следующая страница" : "предыдущая страница");
         }
      } else {
         if (this.items2.stream().noneMatch(var1x -> var1x.intValue == var4)) {
            for (Object var8_o : (Iterable<?>)(Object)(var6)) {
               Slot var8 = (Slot)var8_o;
               this.items2.add(new AutoInventoryModule.Data(var4, this.method190(var8.getStack())));
            }
         }

         if (var4 >= var5 || !this.method191(var1, "следующая страница")) {
            this.data = this.items2.stream().min(Comparator.comparingLong(AutoInventoryModule.Data::getLongValue)).orElse(null);
            if (this.data == null) {
               this.method183(var2);
            }
         }
      }
   }

   private void method180(ScreenHandler var1, AutoInventoryModule.Entry2 var2, int var3, int var4, int var5) {
      var var6 = this.method185(var1, var2);
      long var7 = var6.stream().mapToLong(var1x -> this.method190(var1x.getStack())).min().orElse(0L);
      int var9;
      int var10 = (var9 = Math.max(1, var2.intValue2)) + Math.round(var9 * 0.2F) - var3;
      int var11 = var9 - var3;
      var var12 = var6.stream()
         .filter(var3x -> this.method190(var3x.getStack()) <= Math.round((float)var7 * 2.0F))
         .filter(var1x -> ItemNbtUtils.method90(var1x.getStack()) <= var10)
         .toList();
      var var13 = Comparator.comparingLong((net.minecraft.screen.slot.Slot var1x) -> this.method190(var1x.getStack()));
      Slot var14 = var12.stream()
         .filter(var1x -> ItemNbtUtils.method90(var1x.getStack()) >= var11)
         .min(var13)
         .orElseGet(() -> (Slot)var12.stream().min(var13).orElse(null));
      if (var14 != null) {
         this.method181(var1, var14, var2, var3);
      } else if (var4 >= var5 || !this.method191(var1, "следующая страница")) {
         this.method183(var2);
      }
   }

   private void method181(ScreenHandler var1, Slot var2, AutoInventoryModule.Entry2 var3, int var4) {
      ItemStack var5 = var2.getStack();
      String var6 = var5.getName().getString();
      long var7 = this.method199(var5);
      this.entry3 = new AutoInventoryModule.Entry3(
         var5, var6 != null && !var6.isBlank() ? var6 : var3.text, Math.max(1, ItemNbtUtils.method90(var5)), var7 == Long.MAX_VALUE ? 0L : var7
      );
      this.intValue9 = var4;
      mc.interactionManager.clickSlot(var1.syncId, var2.id, 0, SlotActionType.QUICK_MOVE, mc.player);
   }

   private void method182() {
      if (this.entry3 != null && this.entry2 != null) {
         if (this.method202(this.entry2.itemMatcher, this.entry2.floatValue) > this.intValue9) {
            this.method175();
         }
      }
   }

   private void method183(AutoInventoryModule.Entry2 var1) {
      ChatLogger.method2021("АвтоСбор: нет подходящих лотов «" + var1.text + "», пропускаю");
      this.intValue6++;
      this.method184();
      this.method192();
   }

   private void method184() {
      this.entry2 = null;
      this.entry3 = null;
      this.data = null;
      this.items2.clear();

      while (this.items != null && this.items.getClass() == EmptyList.class ? false : !this.items.isEmpty()) {
         AutoInventoryModule.Entry2 var1 = this.items.removeFirst();
         if (this.method202(var1.itemMatcher, var1.floatValue) < var1.intValue2) {
            this.entry2 = var1;
            this.type = AutoInventoryModule.Type.AUCTION;
            ChatLogger.method2019("АвтоСбор: собираю «" + var1.text + "»");
            return;
         }

         this.intValue5++;
      }

      this.type = AutoInventoryModule.Type.FINISHED;
   }

   private List<Slot> method185(ScreenHandler var1, AutoInventoryModule.Entry2 var2) {
      ArrayList<Slot> var3 = new ArrayList<>();
      ItemStack var4 = this.method187(var2.itemMatcher);
      int var5 = Math.max(0, var1.slots.size() - 36);

      for (int var6 = 0; var6 < var5; var6++) {
         Slot var7 = var1.getSlot(var6);
         if (var7 != null && var7.hasStack() && this.method186(var7.getStack(), var2, var4)) {
            var3.add(var7);
         }
      }

      return var3;
   }

   private boolean method186(ItemStack var1, AutoInventoryModule.Entry2 var2, ItemStack var3) {
      if (!var2.itemMatcher.method2672(var1)) {
         return false;
      } else if (!this.method204(var1, var2.floatValue)) {
         return false;
      } else if (ItemNbtUtils.method87(var1)) {
         return false;
      } else if (this.method189(var1)) {
         return false;
      } else {
         long var4;
         if ((var4 = this.method190(var1)) <= 0L || var4 == Long.MAX_VALUE) {
            return false;
         } else if (var2.longValue > 0L && var4 > var2.longValue) {
            return false;
         } else {
            if (!var3.isEmpty()) {
               if (method188(var1.getItem()) && !Objects.equals(var1.get(DataComponentTypes.POTION_CONTENTS), var3.get(DataComponentTypes.POTION_CONTENTS))) {
                  return false;
               }

               if (!var1.getName().getString().trim().equalsIgnoreCase(var3.getName().getString().trim())) {
                  return false;
               }
            }

            return !var1.isOf(Items.TOTEM_OF_UNDYING) || var2.itemMatcher.getClass() != ItemMatcher.class || !var1.hasGlint();
         }
      }
   }

   private ItemStack method187(ItemMatcher var1) {
      for (int var2 = 0; var2 < mc.player.getInventory().size(); var2++) {
         ItemStack var3 = mc.player.getInventory().getStack(var2);
         if (!var3.isEmpty() && var1.method2672(var3)) {
            return var3;
         }
      }

      return ItemStack.EMPTY;
   }

   private static boolean method188(Item var0) {
      return var0 == Items.POTION || var0 == Items.SPLASH_POTION || var0 == Items.LINGERING_POTION;
   }

   private boolean method189(ItemStack var1) {
      LoreComponent var2 = var1.get(DataComponentTypes.LORE);
      if (var2 == null) {
         return false;
      } else {
         for (Text var4 : var2.lines()) {
            if (var4.getString().toLowerCase(Locale.ROOT).contains("нажмите, чтобы забрать")) {
               return true;
            }
         }

         return false;
      }
   }

   private long method190(ItemStack var1) {
      long var2;
      return (var2 = this.method199(var1)) == Long.MAX_VALUE ? Long.MAX_VALUE : var2 / Math.max(1, ItemNbtUtils.method90(var1));
   }

   private boolean method191(ScreenHandler var1, String var2) {
      String var3 = var2.toLowerCase(Locale.ROOT);
      int var4 = Math.max(0, var1.slots.size() - 36);

      for (int var5 = 0; var5 < var4; var5++) {
         Slot var6 = var1.getSlot(var5);
         if (var6 != null && var6.hasStack() && var6.getStack().getName().getString().toLowerCase(Locale.ROOT).contains(var3)) {
            mc.interactionManager.clickSlot(var1.syncId, var6.id, 0, SlotActionType.QUICK_MOVE, mc.player);
            return true;
         }
      }

      return false;
   }

   private void method192() {
      if (mc.player != null && mc.currentScreen instanceof GenericContainerScreen) {
         mc.player.closeHandledScreen();
      }
   }

   @EventTarget
   public void method193(PacketEvent var1) {
      if (var1.method7100() && this.entry2 != null && this.method210()) {
         if (var1.getPacket3() instanceof GameMessageS2CPacket var2) {
            String var4 = var2.content().getString().toLowerCase(Locale.ROOT);
            if (var4.contains("этот товар уже купили")) {
               this.flag = true;
            } else if (var4.contains("не хватает монет")) {
               this.flag2 = true;
            } else if (var4.contains("недоступна в режиме afk")) {
               this.flag3 = true;
            }
         }

         if (var1.getPacket3() instanceof OpenScreenS2CPacket && !(mc.currentScreen instanceof GenericContainerScreen)) {
            if (this.intValue7 >= 8) {
               this.flag4 = true;
            }

            this.entry4.reset();
         }
      }
   }

   @EventTarget
   public void method194(MoveInputEvent var1) {
      if (this.method210() && this.entry2 != null) {
         if (this.intValue8 > 0) {
            ThreadLocalRandom var2 = ThreadLocalRandom.current();
            var1.method3042(var2.nextBoolean() ? 1.0F : -1.0F, var2.nextBoolean() ? 1.0F : -1.0F);
         } else if (mc.currentScreen instanceof HandledScreen) {
            var1.method3042(0.0F, 0.0F);
         }
      }
   }

   @EventTarget
   public void method195(KeyEvent var1) {
      if (this.method210() && this.entry2 != null && var1.getKeyCode() == 256 && var1.getIntValue() == 1) {
         this.flag5 = true;
      }
   }

   private long method196(ItemStack var1) {
      LoreComponent var2 = var1.get(DataComponentTypes.LORE);
      if (var2 == null) {
         return Long.MAX_VALUE;
      } else {
         long var3 = Long.MAX_VALUE;

         for (Text var6 : var2.lines()) {
            String var7 = var6.getString().replace(' ', ' ');
            Matcher var8 = pattern.matcher(var7);
            if (var8.find()) {
               long var9;
               if ((var9 = this.method201(var8.group(1))) != Long.MAX_VALUE) {
                  return var9;
               }
            } else if (var3 == Long.MAX_VALUE) {
               Matcher var12 = pattern2.matcher(var7);
               if (var12.find()) {
                  var3 = this.method201(var12.group(1));
               }
            }
         }

         if (var3 != Long.MAX_VALUE) {
            return Math.max(1L, var3 / Math.max(1, ItemNbtUtils.method90(var1)));
         } else {
            long var11;
            return (var11 = this.method199(var1)) != Long.MAX_VALUE ? Math.max(1L, var11 / Math.max(1, ItemNbtUtils.method90(var1))) : Long.MAX_VALUE;
         }
      }
   }

   private boolean method197(AutoInventoryModule.Entry2 var1, ItemStack var2, long var3) {
      return var1.longValue <= 0L ? false : (this.method210() ? Math.max(1L, var3 / Math.max(1, ItemNbtUtils.method90(var2))) : var3) > var1.longValue;
   }

   private long method198(ItemStack var1) {
      if (!this.method210()) {
         return this.method196(var1);
      } else {
         long var2;
         if ((var2 = this.method199(var1)) != Long.MAX_VALUE) {
            return var2;
         } else {
            long var4;
            return (var4 = this.method196(var1)) == Long.MAX_VALUE ? Long.MAX_VALUE : var4 * Math.max(1, ItemNbtUtils.method90(var1));
         }
      }
   }

   private long method199(ItemStack var1) {
      LoreComponent var2 = var1.get(DataComponentTypes.LORE);
      if (var2 == null) {
         return Long.MAX_VALUE;
      } else {
         for (Text var4 : var2.lines()) {
            String var5 = var4.getString().replace(' ', ' ');
            Matcher var6 = pattern3.matcher(var5);
            long var7;
            if (var6.find() && (var7 = this.method201(var6.group(1))) != Long.MAX_VALUE) {
               return var7;
            }
         }

         return Long.MAX_VALUE;
      }
   }

   private long method200(long var1, int var3) {
      return this.method210() ? var1 : var1 * var3;
   }

   private long method201(String var1) {
      try {
         return Long.parseLong(var1.replace(" ", "").replace(",", ""));
      } catch (NumberFormatException var3) {
         return Long.MAX_VALUE;
      }
   }

   private int method202(ItemMatcher var1, float var2) {
      if (mc.player == null) {
         return 0;
      } else {
         int var3 = 0;

         for (int var4 = 0; var4 < mc.player.getInventory().size(); var4++) {
            ItemStack var5 = mc.player.getInventory().getStack(var4);
            if (var5 != null && !var5.isEmpty() && var1.method2672(var5) && this.method204(var5, var2)) {
               var3 += Math.max(1, var5.getCount());
            }
         }

         return var3;
      }
   }

   private String method203(ItemMatcher var1, float var2) {
      return var1.getText22() + "|" + var1.getItemStack().getItem() + "|" + var2;
   }

   private boolean method204(ItemStack var1, float var2) {
      if (var2 <= 0.0F) {
         return true;
      } else {
         int var3;
         return (var3 = var1.getMaxDamage()) <= 0 ? false : (float)(var3 - var1.getDamage()) / var3 >= var2;
      }
   }

   private void method205() {
      if (mc.player != null) {
         if (mc.currentScreen != null) {
            if (mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
               mc.player.closeHandledScreen();
            }

            mc.setScreen(null);
         }
      }
   }

   public InventorySnapshot getInventorySnapshot() {
      return this.inventorySnapshot;
   }

   public ModeSetting getServer() {
      return this.server;
   }

   public String method206() {
      return this.server.get();
   }

   public InventoryPreset method207() {
      if (this.method210()) {
         return this.inventoryPreset;
      } else {
         return "SpookyTime".equals(this.method206()) ? this.inventoryPreset3 : this.inventoryPreset2;
      }
   }

   public ServerNameNormalizer getServerNameNormalizer() {
      return this.serverNameNormalizer;
   }

   public AutoInventoryModule.Data2 method208() {
      if (mc.player == null) {
         return new AutoInventoryModule.Data2(0, 0, 0);
      } else {
         InventoryPreset var1 = this.method207();
         var1.method876();
         PlayerInventory var2 = mc.player.getInventory();
         int var3 = 0;
         int var4 = 0;
         int var5 = 0;

         for (int var6 = 0; var6 < 36 && var6 < var2.size(); var6++) {
            ItemStack var7 = var2.getStack(var6);
            if (var7 != null && !var7.isEmpty()) {
               int var8 = var6 < 9 ? 27 + var6 : var6 - 9;
               switch (method209(var1, var7, var8)) {
                  case 0:
                     var4++;
                     break;
                  case 1:
                     var3++;
                     break;
                  default:
                     var5++;
               }
            }
         }

         for (int var9 = 36; var9 < var2.size(); var9++) {
            ItemStack var10 = var2.getStack(var9);
            if (var10 != null && !var10.isEmpty()) {
               switch (method209(var1, var10, var1.method873())) {
                  case 0:
                     var4++;
                     break;
                  case 1:
                     var3++;
                     break;
                  default:
                     var5++;
               }
            }
         }

         return new AutoInventoryModule.Data2(var3, var4, var5);
      }
   }

   private static int method209(InventoryPreset var0, ItemStack var1, int var2) {
      ItemMatcher var3 = var0.method890(var1);
      if (var3 == null) {
         return 0;
      } else if (var2 < 0) {
         return -1;
      } else {
         var0.method874(var2, var3);
         var0.method878(var2, var1.getCount());
         return 1;
      }
   }

   private boolean method210() {
      return "Funtime 1.21".equals(this.method206());
   }

   public String method211() {
      return switch (this.type) {
         case IDLE -> this.isEnabled() ? "пауза" : "остановлен";
         case AUCTION -> "поиск";
         case PURCHASE -> "покупка";
         case INVENTORY -> "ожидание";
         case FINISHED -> "готово";
      };
   }

   public String method212() {
      return this.entry2 == null ? "" : this.entry2.text;
   }

   public int getIntValue4() {
      return this.intValue4;
   }

   public int getIntValue5() {
      return this.intValue5;
   }

   public long getLongValue() {
      return this.longValue;
   }

   @Override
   public JsonObject save() {
      JsonObject var1 = super.save();
      var1.add("SetFuntime", this.inventoryPreset.save());
      var1.add("SetHolyWorld", this.inventoryPreset2.save());
      var1.add("SetSpookyTime", this.inventoryPreset3.save());
      var1.add("Presets", this.serverNameNormalizer.method1234());
      return var1;
   }

   @Override
   public void load(JsonObject var1) {
      super.load(var1);
      if (var1 != null) {
         if (var1.has("SetFuntime") && var1.get("SetFuntime").isJsonObject()) {
            this.inventoryPreset.load(var1.getAsJsonObject("SetFuntime"));
         }

         if (var1.has("SetHolyWorld") && var1.get("SetHolyWorld").isJsonObject()) {
            this.inventoryPreset2.load(var1.getAsJsonObject("SetHolyWorld"));
         }

         if (var1.has("SetSpookyTime") && var1.get("SetSpookyTime").isJsonObject()) {
            this.inventoryPreset3.load(var1.getAsJsonObject("SetSpookyTime"));
         }

         if (var1.has("Presets") && var1.get("Presets").isJsonArray()) {
            this.serverNameNormalizer.method1235(var1.getAsJsonArray("Presets"));
         }
      }
   }

   private static final class Data {
      private final int intValue;
      private final long longValue;

      public int intValue() {
         return this.intValue;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "longValue=" + this.longValue + "]";
      }

      private Data(int var1, long var2) {
         this.intValue = var1;
         this.longValue = var2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   public static final class Data2 {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3);
      }

      @Override
      public String toString() {
         return "Data2[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + "]";
      }

      public Data2(int var1, int var2, int var3) {
         this.intValue = var1;
         this.intValue2 = var2;
         this.intValue3 = var3;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public int getIntValue3() {
         return this.intValue3;
      }
   }

   private static final class Entry {
      private volatile long last = System.currentTimeMillis();
      private volatile long longValue;

      public void reset() {
         this.last = System.currentTimeMillis();
         this.longValue = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
      }

      public void method213() {
         this.last = 0L;
      }

      public boolean method214(long var1, long var3) {
         return System.currentTimeMillis() - (var1 + this.longValue % (var3 + 1L)) >= this.last;
      }
   }

   private static class Entry2 {
      private final ItemMatcher itemMatcher;
      private final String text;
      private final int intValue;
      private final int intValue2;
      private final float floatValue;
      private final long longValue;
      private int intValue3;

      private Entry2(ItemMatcher var1, String var2, int var3, int var4, float var5, long var6) {
         this.itemMatcher = var1;
         this.text = var2;
         this.intValue = var3;
         this.intValue2 = var4;
         this.floatValue = var5;
         this.longValue = var6;
      }
   }

   private static class Entry3 {
      private final ItemStack itemStack;
      private final String text;
      private final int intValue;
      private final long longValue;

      private Entry3(ItemStack var1, String var2, int var3, long var4) {
         this.itemStack = var1.copy();
         this.text = var2;
         this.intValue = var3;
         this.longValue = var4;
      }
   }

   private static enum Type {
      IDLE,
      AUCTION,
      PURCHASE,
      INVENTORY,
      FINISHED;
   }
}

package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetPlayerInventoryS2CPacket;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.Event5;
import org.zenith.client.events.HealthUpdateEvent;
import org.zenith.client.modules.impl.misc.InventorySettingModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.Class35;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.KeyBindUtils;

@ModuleInfo(
   name = "AutoTotem",
   category = ModuleCategory.COMBAT,
   description = "module.autototem.description"
)
public final class AutoTotemModule extends Module {
   public static final AutoTotemModule INSTANCE = new AutoTotemModule();
   private final SliderSetting healthSetting = new SliderSetting(
      "module.autoTotem.healthSetting", 4.0F, 2.0F, 20.0F, 0.5F, "module.autoTotem.healthSetting.desc", "hp"
   );
   private final SliderSetting elytraHealthSetting = new SliderSetting(
      "module.autoTotem.elytraHealthSetting", 4.0F, 2.0F, 20.0F, 0.5F, "module.autoTotem.elytraHealthSetting.desc", "hp"
   );
   private final MultiSelectSetting triggerSetting = MultiSelectSetting.method5224(
      "module.autoTotem.triggerSetting",
      "module.autoTotem.triggerSetting.desc",
      List.of(
         "module.autoTotem.triggerCrystal",
         "module.autoTotem.triggerTNT",
         "module.autoTotem.trident",
         "module.autoTotem.minecartTNT",
         "module.autoTotem.triggerPotential",
         "module.autoTotem.triggerCrystalAura",
         "module.autoTotem.triggerCombat"
      )
   );
   private final SliderSetting combatRange = new SliderSetting(
      "module.autoTotem.combatRange", 10.0F, 3.0F, 30.0F, 1.0F, "module.autoTotem.combatRange.desc", "b", () -> this.triggerSetting.method5227(6), null
   );
   private final SliderSetting crystalMargin = new SliderSetting(
      "module.autoTotem.crystalMargin",
      2.0F,
      0.0F,
      10.0F,
      0.5F,
      "module.autoTotem.crystalMargin.desc",
      "hp",
      () -> this.triggerSetting.method5227(0) || this.triggerSetting.method5227(4) || this.triggerSetting.method5227(5),
      null
   );
   private final SliderSetting tNTRangeSetting = new SliderSetting(
      "module.autoTotem.TNTRangeSetting", 8.0F, 0.0F, 50.0F, 4.0F, "module.autoTotem.TNTRangeSetting.desc", "b", () -> this.triggerSetting.method5227(1), null
   );
   private final SliderSetting tridentRangeSetting = new SliderSetting(
      "module.autoTotem.tridentRangeSetting",
      8.0F,
      0.0F,
      50.0F,
      4.0F,
      "module.autoTotem.tridentRangeSetting.desc",
      "b",
      () -> this.triggerSetting.method5227(2),
      null
   );
   private final BooleanSetting predictSetting = new BooleanSetting("module.autoTotem.predictSetting", "module.autoTotem.predictSetting.desc", false);
   private final SliderSetting predictTicksSetting = new SliderSetting(
      "module.autoTotem.predictTicksSetting", 10.0F, 1.0F, 40.0F, 1.0F, "module.autoTotem.predictTicksSetting.desc", "t", this.predictSetting::isEnabled, null
   );
   private final SliderSetting time = new SliderSetting("module.autoTotem.time", 300.0F, 0.0F, 2000.0F, 100.0F, "module.autoTotem.time.desc", "ms");
   private final BooleanSetting hotbarTotems = new BooleanSetting("module.autoTotem.hotbarTotems", "module.autoTotem.hotbarTotems.desc", true);
   private final MultiSelectSetting keepSlots = new MultiSelectSetting("module.autoTotem.keepSlots", "module.autoTotem.keepSlots.desc");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepCrystals", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepObsidian", true);
   private final MultiSelectSetting.Entry entry3 = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepAnchors", true);
   private final MultiSelectSetting.Entry entry4 = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepGlowstone", true);
   private final MultiSelectSetting.Entry entry5 = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepPickaxe", true);
   private final MultiSelectSetting.Entry entry6 = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepSword", false);
   private final MultiSelectSetting.Entry entry7 = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepGapples", false);
   private final MultiSelectSetting.Entry entry8 = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepPearls", false);
   private final MultiSelectSetting.Entry entry9 = new MultiSelectSetting.Entry(this.keepSlots, "module.autoTotem.keepWebs", false).method5230();
   private static final int CONST_intValue = 45;
   private static final int CONST_intValue2 = 40;
   private Slot slot = null;
   private StopWatch stopWatch = new StopWatch();
   private int intValue = Integer.MIN_VALUE;
   private volatile Boolean booleanValue;
   private static final int CONST_intValue3 = 3;
   private static final int CONST_intValue4 = 10;
   private int intValue2 = Integer.MIN_VALUE;
   private int intValue3 = Integer.MIN_VALUE;
   private int intValue4 = Integer.MIN_VALUE;
   private int intValue5 = -1;
   private volatile boolean flag;
   private volatile boolean flag2;
   private volatile int intValue6;
   private int intValue7 = Integer.MIN_VALUE;
   private final Deque<AutoTotemModule.Entry> deque = new ArrayDeque<>();
   private float floatValue = Float.NaN;
   private int intValue8 = -1;
   private final Class35 class35 = new Class35();
   private volatile boolean flag3;
   private volatile int intValue9 = Integer.MIN_VALUE;
   private int intValue10 = Integer.MIN_VALUE;
   private int intValue11 = Integer.MIN_VALUE;
   private int intValue12 = Integer.MIN_VALUE;
   private Item item;
   private int intValue13 = -1;
   private int intValue14 = Integer.MIN_VALUE;
   private final Map<Item, Integer> items = new HashMap<>();
   private static final int CONST_intValue5 = 10;
   private boolean flag4;
   private boolean flag5;
   private int intValue15 = Integer.MIN_VALUE;
   private float floatValue2;

   private AutoTotemModule() {
   }

   @Override
   public void onEnable() {
      this.method734();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.deque.clear();
      this.floatValue = Float.NaN;
      this.intValue8 = -1;
      super.onDisable();
   }

   @EventTarget
   public void method700(PacketEvent var1) {
      if (var1.method7100() && mc.player != null) {
         if (var1.getPacket3() instanceof EntityStatusS2CPacket var2 && var2.getStatus() == 35 && var2.entityId == mc.player.getId()) {
            this.booleanValue = Boolean.FALSE;
            this.flag3 = true;
            this.intValue9 = mc.player.age;
            this.log("поп: левая рука на сервере пуста");
         } else if (var1.getPacket3() instanceof ScreenHandlerSlotUpdateS2CPacket var3 && var3.getSyncId() == 0 && var3.getSlot() == 45) {
            this.method701(var3.getStack(), "слот 45");
         } else if (var1.getPacket3() instanceof InventoryS2CPacket var4 && var4.syncId() == 0 && var4.contents().size() > 45) {
            this.method701(var4.contents().get(45), "весь инвентарь");
         } else if (var1.getPacket3() instanceof SetPlayerInventoryS2CPacket var5 && var5.slot() == 40) {
            this.method701(var5.contents(), "слот 40");
         } else if (var1.getPacket3() instanceof DeathMessageS2CPacket var6 && var6.playerId() == mc.player.getId() && !CrystalAuraModule.INSTANCE.isEnabled()) {
            this.log("СМЕРТЬ: " + var6.message().getString() + ", тотемов в инвентаре " + this.method723());
         }
      }
   }

   private void method701(ItemStack var1, String var2) {
      boolean var3 = var1.isOf(Items.TOTEM_OF_UNDYING);
      this.booleanValue = var3;
      if (var3 && this.flag) {
         this.flag = false;
         this.log("сервер подтвердил тотем в левой руке (" + var2 + ")");
      } else if (!var3) {
         if (this.flag
            && mc.player != null
            && mc.player.age > this.intValue11
            && !this.method702()
            && mc.player.age - this.intValue11 >= Math.max(1, this.method724() - 1)) {
            this.flag = false;
            this.intValue6++;
         }

         this.log("сервер: в левой руке " + (var1.isEmpty() ? "пусто" : var1.getItem().toString()) + " (" + var2 + ")");
      }
   }

   private boolean method702() {
      int var1 = this.intValue9;
      return var1 != Integer.MIN_VALUE && mc.player != null && mc.player.age >= var1 && mc.player.age - var1 <= 2 + this.method724();
   }

   public boolean method703() {
      return mc.player != null && InventoryUtils.method906(Items.TOTEM_OF_UNDYING) != null;
   }

   public boolean method704() {
      return mc.player != null
         && this.isEnabled()
         && (
            this.intValue11 != Integer.MIN_VALUE && mc.player.age >= this.intValue11 && mc.player.age - this.intValue11 <= 1
               || !KeyBindUtils.method2629(AutoTotemModule.class)
         );
   }

   public boolean method705() {
      return mc.player != null && this.isEnabled() && !KeyBindUtils.method2629(AutoTotemModule.class);
   }

   public boolean method706() {
      return mc.player != null
         && this.isEnabled()
         && this.intValue11 != Integer.MIN_VALUE
         && mc.player.age >= this.intValue11
         && mc.player.age - this.intValue11 <= this.method724() + 3
         && this.flag5;
   }

   public boolean method707() {
      return mc.player != null && mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) && this.booleanValue != Boolean.FALSE && !this.flag;
   }

   public boolean isFlag() {
      return this.flag;
   }

   @EventTarget
   public void method708(HealthUpdateEvent var1) {
      this.method710();
   }

   @EventTarget
   public void method709(Event5 var1) {
      this.method710();
   }

   public void method710() {
      if (this.stopWatch == null) {
         this.stopWatch = new StopWatch();
      }

      this.method731();
      if (!UseItemUtils.method6526() && !mc.player.isDead()) {
         int var1 = mc.player.age;
         CrystalAuraModule.INSTANCE.method5926();
         int var2 = 0;

         for (int var3 = 0; var3 < 9; var3++) {
            if (mc.player.getInventory().getStack(var3).isOf(Items.TOTEM_OF_UNDYING)) {
               var2++;
            }
         }

         if (var2 != this.intValue5) {
            this.log("в хотбаре тотемов: " + var2 + " (было " + Math.max(0, this.intValue5) + "), всего " + this.method723());
            this.intValue5 = var2;
         }

         if (this.intValue11 > var1) {
            this.intValue11 = Integer.MIN_VALUE;
            this.intValue = Integer.MIN_VALUE;
            this.intValue2 = Integer.MIN_VALUE;
            this.intValue3 = Integer.MIN_VALUE;
            this.intValue4 = Integer.MIN_VALUE;
            this.flag = false;
            this.booleanValue = null;
            this.slot = null;
            this.intValue9 = Integer.MIN_VALUE;
         }

         if (this.flag3) {
            this.flag3 = false;
            this.intValue10 = var1;
         }

         boolean var9 = this.method728();
         boolean var4 = this.method725();
         if (var4) {
            this.intValue = var1;
         }

         if (this.flag) {
            if (var1 <= this.intValue7) {
               return;
            }

            if (!this.flag2) {
               this.flag2 = true;
               InventoryUtils.method899();
               this.intValue7 = var1 + this.method724() + 4;
               this.log("сервер молчит " + (var1 - this.intValue11) + " т — запрашиваем инвентарь");
               return;
            }

            this.flag = false;
            this.booleanValue = null;
            this.log("ответа нет и на запрос — верим клиенту");
         }

         if (this.intValue6 > 0) {
            this.log("сервер откатил своп (" + this.intValue6 + ") — повторяем");
            this.intValue6 = 0;
         }

         boolean var5 = this.method711();
         if (!var4) {
            if (this.slot != null
               && var5
               && this.intValue != Integer.MIN_VALUE
               && var1 - this.intValue >= Math.max(1, Math.round(this.time.getValue3() / 50.0F)) + 20
               && !this.method726()
               && KeyBindUtils.method2629(AutoTotemModule.class)) {
               Slot var10 = this.slot;
               this.slot = null;
               this.intValue11 = var1;
               this.flag4 = true;
               this.flag5 = false;
               this.method714(var10);
            } else {
               this.method715(var1);
            }
         } else if (var5) {
            this.method715(var1);
         } else {
            Slot var6 = this.method720();
            if (var6 != null) {
               boolean var7 = var9 || this.method727() || this.method726() || mc.player.hurtTime > 0;
               boolean var8 = var6.id >= 36 && var6.id <= 44;
               if (!var8 && !var7 && InventorySettingModule.INSTANCE.method2397()) {
                  if (KeyBindUtils.method2629(AutoTotemModule.class)) {
                     this.intValue12 = var1;
                     KeyBindUtils.method2628(AutoTotemModule.class, () -> {
                        Slot var1x = this.method720();
                        if (var1x != null && !this.method711()) {
                           this.method712(var1x, mc.player.age);
                        }
                     }, 100);
                  } else if (this.intValue12 == Integer.MIN_VALUE || var1 - this.intValue12 > 10) {
                     Zenith.getInstance().getTaskScheduler().method5114(AutoTotemModule.class);
                     this.log("не дождались остановки за " + (var1 - this.intValue12) + " т — берём тотем на ходу");
                     this.method712(var6, var1);
                  }
               } else {
                  if (!var8 && !var9 && !var7 && !this.stopWatch.method7546(150L)) {
                     return;
                  }

                  if (!KeyBindUtils.method2629(AutoTotemModule.class)) {
                     Zenith.getInstance().getTaskScheduler().method5114(AutoTotemModule.class);
                  }

                  this.method712(var6, var1);
               }
            }
         }
      }
   }

   private boolean method711() {
      return mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) && this.booleanValue != Boolean.FALSE;
   }

   private void method712(Slot var1, int var2) {
      ItemStack var3 = mc.player.getOffHandStack();
      this.stopWatch.reset();
      this.intValue11 = var2;
      int var4 = var1.id >= 36 && var1.id <= 44 ? var1.id - 36 : -1;
      this.flag4 = var4 == -1;
      this.flag5 = false;
      if (var4 != -1) {
         this.method713(var4);
         this.flag5 = true;
         this.log("кладём тотем из хотбара " + var4 + " (смена рук, без кликов по инвентарю)");
      } else {
         if (this.method722() && this.intValue3 != Integer.MIN_VALUE && var2 - this.intValue3 < 10) {
            return;
         }

         this.intValue3 = var2;
         this.method714(var1);
         this.flag5 = true;
         this.log("кладём тотем из инвентаря, слот " + var1.id + " (клик: в хотбаре тотемов нет)");
      }

      this.flag = true;
      this.flag2 = false;
      this.intValue7 = var2 + this.method724() + 4;
      if (this.slot == null && !var3.isEmpty() && !var3.isOf(Items.TOTEM_OF_UNDYING)) {
         this.slot = var1;
      }
   }

   private void method713(int var1) {
      int var2 = mc.player.getInventory().getSelectedSlot();
      if (var2 != var1) {
         mc.player.getInventory().setSelectedSlot(var1);
         mc.interactionManager.syncSelectedSlot();
      }

      mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
      if (var2 != var1) {
         mc.player.getInventory().setSelectedSlot(var2);
         mc.interactionManager.syncSelectedSlot();
      }
   }

   private void method714(Slot var1) {
      if (!(mc.player.currentScreenHandler instanceof PlayerScreenHandler)) {
         InventoryUtils.closeScreen();
      }

      PlayerScreenHandler var2 = mc.player.playerScreenHandler;
      mc.interactionManager.clickSlot(var2.syncId, var1.id, 40, SlotActionType.SWAP, mc.player);
      if (mc.currentScreen == null) {
         mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(var2.syncId));
      }
   }

   private void method715(int var1) {
      if (this.hotbarTotems.isEnabled()
         && (this.intValue2 == Integer.MIN_VALUE || var1 - this.intValue2 >= 3)
         && mc.player.currentScreenHandler == mc.player.playerScreenHandler) {
         if (mc.player.hurtTime <= 0 && !this.method727() && (this.intValue4 == Integer.MIN_VALUE || var1 - this.intValue4 >= 20)) {
            PlayerInventory var2 = mc.player.getInventory();
            boolean[] var3 = new boolean[9];

            for (Predicate var5 : this.method716()) {
               int var6 = -1;

               for (int var7 = 0; var7 < 9; var7++) {
                  if (!var3[var7] && var5.test(var2.getStack(var7))) {
                     var6 = var7;
                     break;
                  }
               }

               if (var6 == -1) {
                  int var12 = this.method717(var5);
                  if (var12 != -1) {
                     int var8 = method718(var3, var2);
                     if (var8 != -1) {
                        Item var9 = mc.player.playerScreenHandler.getSlot(var12).getStack().getItem();
                        if (this.items.getOrDefault(var9, Integer.MIN_VALUE) <= var1) {
                           if (var9 != this.item || var8 != this.intValue13 || var1 - this.intValue14 >= 60) {
                              this.item = var9;
                              this.intValue13 = var8;
                              this.intValue14 = var1;
                              this.method719(var12, var8, var1, "занесли в хотбар " + var9);
                              return;
                           }

                           this.items.put(var9, var1 + 400);
                           this.log("забивка хотбара зациклилась на " + var9 + " в слот " + var8 + " — не трогаем 20 с");
                        }
                     }
                  }
               } else {
                  var3[var6] = true;
               }
            }

            int var10 = this.method717(var0 -> var0.isOf(Items.TOTEM_OF_UNDYING));
            if (var10 != -1) {
               for (int var11 = 0; var11 < 9; var11++) {
                  if (!var3[var11] && !var2.getStack(var11).isOf(Items.TOTEM_OF_UNDYING) && (var11 != var2.getSelectedSlot() || !mc.player.isUsingItem())) {
                     this.method719(var10, var11, var1, "тотем в хотбар, слот " + var11);
                     return;
                  }
               }
            }
         }
      }
   }

   private List<Predicate<ItemStack>> method716() {
      ArrayList<Predicate<ItemStack>> var1 = new ArrayList<>();
      if (this.entry.isEnabled()) {
         var1.add(var0 -> var0.isOf(Items.END_CRYSTAL));
      }

      if (this.entry2.isEnabled()) {
         var1.add(var0 -> var0.isOf(Items.OBSIDIAN));
      }

      if (this.entry3.isEnabled()) {
         var1.add(var0 -> var0.isOf(Items.RESPAWN_ANCHOR));
      }

      if (this.entry4.isEnabled()) {
         var1.add(var0 -> var0.isOf(Items.GLOWSTONE));
      }

      if (this.entry5.isEnabled()) {
         var1.add(var0 -> var0.isIn(ItemTags.PICKAXES));
      }

      if (this.entry6.isEnabled() || CrystalAuraModule.INSTANCE.method5744()) {
         var1.add(var0 -> var0.isIn(ItemTags.SWORDS) || var0.isIn(ItemTags.AXES));
      }

      if (this.entry7.isEnabled()) {
         var1.add(var0 -> var0.isOf(Items.GOLDEN_APPLE) || var0.isOf(Items.ENCHANTED_GOLDEN_APPLE));
      }

      if (this.entry8.isEnabled()) {
         var1.add(var0 -> var0.isOf(Items.ENDER_PEARL));
      }

      if (this.entry9.isEnabled() || CrystalAuraModule.INSTANCE.method5769()) {
         var1.add(var0 -> var0.isOf(Items.COBWEB));
      }

      return var1;
   }

   private int method717(Predicate<ItemStack> var1) {
      for (int var2 = 9; var2 <= 35; var2++) {
         if (var1.test(mc.player.playerScreenHandler.getSlot(var2).getStack())) {
            return var2;
         }
      }

      return -1;
   }

   private static int method718(boolean[] var0, PlayerInventory var1) {
      for (int var2 = 0; var2 < 3; var2++) {
         for (int var3 = 0; var3 < 9; var3++) {
            if (!var0[var3]) {
               ItemStack var4 = var1.getStack(var3);
               boolean var5 = var2 == 0 ? var4.isEmpty() : (var2 == 1 ? !var4.isOf(Items.TOTEM_OF_UNDYING) : true);
               if (var5) {
                  return var3;
               }
            }
         }
      }

      return -1;
   }

   private void method719(int var1, int var2, int var3, String var4) {
      this.intValue2 = var3;
      mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, var1, var2, SlotActionType.SWAP, mc.player);
      if (mc.currentScreen == null) {
         mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.playerScreenHandler.syncId));
      }

      this.log(var4);
   }

   private Slot method720() {
      Slot var1 = this.method721(var0 -> var0.id >= 36 && var0.id <= 44);
      return var1 != null ? var1 : this.method721(var0 -> var0.id != 45);
   }

   private Slot method721(Predicate<Slot> var1) {
      return InventoryUtils.method912(
         mc.player.playerScreenHandler,
         Items.TOTEM_OF_UNDYING,
         Comparator.<Slot, Boolean>comparing(var0 -> !var0.getStack().hasEnchantments()).thenComparing((var0, var1x) -> {
            NbtComponent var2 = var0.getStack().get(DataComponentTypes.CUSTOM_DATA);
            NbtComponent var3 = var1x.getStack().get(DataComponentTypes.CUSTOM_DATA);
            if (var2 == null && var3 != null) {
               return -1;
            } else if (var2 != null && var3 == null) {
               return 1;
            } else if (var2 == null) {
               return 0;
            } else {
               boolean var4 = var2.copyNbt().contains("sphereEffect");
               boolean var5 = var3.copyNbt().contains("sphereEffect");
               if (var4 && !var5) {
                  return 1;
               } else if (!var4 && var5) {
                  return -1;
               } else if (!var4) {
                  return 0;
               } else {
                  String var6 = var2.copyNbt().get("sphereEffect").toString();
                  String var7 = var3.copyNbt().get("sphereEffect").toString();
                  return Integer.compare(var7.length(), var6.length());
               }
            }
         }).thenComparing(Comparator.comparingInt(var0 -> var0.id)),
         var1x -> var1x.id != 45 && var1.test(var1x)
      );
   }

   private void log(String var1) {
      CrystalAuraModule.INSTANCE.method5927(var1);
   }

   private boolean method722() {
      return this.method726() || mc.player.hurtTime > 0 || this.method727();
   }

   private int method723() {
      int var1 = 0;

      for (int var2 = 0; var2 < mc.player.getInventory().size(); var2++) {
         if (mc.player.getInventory().getStack(var2).isOf(Items.TOTEM_OF_UNDYING)) {
            var1++;
         }
      }

      return var1;
   }

   private int method724() {
      if (mc.getNetworkHandler() == null) {
         return 2;
      } else {
         PlayerListEntry var1 = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
         return var1 == null ? 2 : MathHelper.clamp(Math.round(var1.getLatency() / 50.0F), 0, 20);
      }
   }

   public boolean method725() {
      if (UseItemUtils.method6526()) {
         return false;
      } else if (this.isEnabled() && InventoryUtils.method906(Items.TOTEM_OF_UNDYING) != null) {
         boolean var1 = mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Items.ELYTRA);
         float var2 = mc.player.getHealth() + mc.player.getAbsorptionAmount();
         if (mc.player.getItemCooldownManager().isCoolingDown(Items.TOTEM_OF_UNDYING.getDefaultStack())) {
            return false;
         } else if (var2 < (var1 ? this.elytraHealthSetting.getValue3() : this.healthSetting.getValue3())) {
            return true;
         } else if (this.predictSetting.isEnabled() && var2 <= this.method732()) {
            return true;
         } else if (this.method727()) {
            return true;
         } else if (this.triggerSetting.method5227(6) && this.method726()) {
            return true;
         } else if ((this.triggerSetting.method5227(0) || this.triggerSetting.method5227(4) || this.triggerSetting.method5227(5))
            && var2 - this.method729() <= this.crystalMargin.getValue3()) {
            return true;
         } else {
            return this.triggerSetting.method5227(2)
                  && UseItemUtils.method6510()
                     .anyMatch(
                        var1x -> var1x instanceof TridentEntity
                           && mc.player.squaredDistanceTo(var1x) < this.tridentRangeSetting.getValue3() * this.tridentRangeSetting.getValue3()
                     )
               ? true
               : this.triggerSetting.method5227(1)
                  && UseItemUtils.method6510()
                     .anyMatch(
                        var1x -> (var1x instanceof TntEntity || var1x instanceof TntMinecartEntity && this.triggerSetting.method5227(3))
                           && mc.player.squaredDistanceTo(var1x) < this.tNTRangeSetting.getValue3() * this.tNTRangeSetting.getValue3()
                     );
         }
      } else {
         return false;
      }
   }

   private boolean method726() {
      double var1 = this.combatRange.getValue3();

      for (PlayerEntity var4 : mc.world.getPlayers()) {
         if (var4 != mc.player
            && CrystalAuraModule.method5901(var4)
            && mc.player.squaredDistanceTo(var4) <= var1 * var1
            && !Zenith.getInstance().getFriends().method7626(var4)
            && !AntiBotModule.INSTANCE.method2785(var4)) {
            return true;
         }
      }

      return false;
   }

   private boolean method727() {
      if (this.intValue10 == Integer.MIN_VALUE) {
         return false;
      } else {
         int var1 = mc.player.age - this.intValue10;
         return var1 >= 0 && var1 <= 20;
      }
   }

   private boolean method728() {
      if (this.method727()) {
         return true;
      } else {
         float var1 = mc.player.getHealth() + mc.player.getAbsorptionAmount();
         return (this.triggerSetting.method5227(0) || this.triggerSetting.method5227(4) || this.triggerSetting.method5227(5))
            && this.method729() >= var1 - 0.5F;
      }
   }

   private float method729() {
      int var1 = mc.player.age;
      if (this.intValue15 == var1) {
         return this.floatValue2;
      } else {
         this.intValue15 = var1;
         this.class35.reset();
         Box var2 = mc.player.getBoundingBox();
         float var3 = mc.player.getHealth() + mc.player.getAbsorptionAmount();
         float var4 = 0.0F;
         if (this.triggerSetting.method5227(0)) {
            for (EndCrystalEntity var6 : mc.world.getEntitiesByClass(EndCrystalEntity.class, var2.expand(12.0), Entity::isAlive)) {
               var4 = Math.max(var4, this.class35.method5452(var6.getEntityPos(), 6.0F, mc.player, var2, null, null));
            }

            if (mc.world.getRegistryKey() != World.NETHER) {
               BlockPos var10 = mc.player.getBlockPos();

               for (BlockPos var7 : BlockPos.iterate(var10.add(-5, -3, -5), var10.add(5, 4, 5))) {
                  BlockState var8 = mc.world.getBlockState(var7);
                  if (var8.isOf(Blocks.RESPAWN_ANCHOR) && var8.get(RespawnAnchorBlock.CHARGES) > 0) {
                     BlockPos var9 = var7.toImmutable();
                     var4 = Math.max(var4, this.class35.method5452(Vec3d.ofCenter(var9), 5.0F, mc.player, var2, var9, null));
                  }
               }
            }
         }

         if (this.triggerSetting.method5227(5) && CrystalAuraModule.INSTANCE.isEnabled()) {
            var4 = Math.max(var4, CrystalAuraModule.INSTANCE.method5742());
         }

         if (this.triggerSetting.method5227(4) && var4 < var3 - this.crystalMargin.getValue3()) {
            var4 = Math.max(var4, this.method730(var2, var3));
         }

         this.floatValue2 = var4;
         return var4;
      }
   }

   private float method730(Box var1, float var2) {
      ArrayList<PlayerEntity> var3 = new ArrayList();

      for (PlayerEntity var5 : mc.world.getPlayers()) {
         if (var5 != mc.player
            && CrystalAuraModule.method5901(var5)
            && mc.player.squaredDistanceTo(var5) <= 100.0
            && !Zenith.getInstance().getFriends().method7626(var5)) {
            var3.add(var5);
         }
      }

      if (var3.isEmpty()) {
         return 0.0F;
      } else {
         float var14 = var2 - this.crystalMargin.getValue3();
         float var15 = 0.0F;
         BlockPos var6 = mc.player.getBlockPos();

         for (BlockPos var8 : BlockPos.iterate(var6.add(-4, -2, -4), var6.add(4, 2, 4))) {
            BlockState var9 = mc.world.getBlockState(var8);
            if ((var9.isOf(Blocks.OBSIDIAN) || var9.isOf(Blocks.BEDROCK)) && mc.world.isAir(var8.up())) {
               Vec3d var10 = new Vec3d(var8.getX() + 0.5, var8.getY() + 1.0, var8.getZ() + 0.5);
               boolean var11 = false;

               for (Object var13_o : (Iterable<?>)(Object)(var3)) {
                  PlayerEntity var13 = (PlayerEntity)var13_o;
                  if (var13.getEyePos().squaredDistanceTo(var10) <= 36.0) {
                     var11 = true;
                     break;
                  }
               }

               if (var11) {
                  Box var16 = new Box(var8.getX(), var8.getY() + 1.0, var8.getZ(), var8.getX() + 1.0, var8.getY() + 3.0, var8.getZ() + 1.0);
                  if (!var16.intersects(var1)
                     && mc.world.getOtherEntities(null, var16, var0 -> !(var0 instanceof EndCrystalEntity)).isEmpty()
                     && !(this.class35.method5453(var10, 6.0F, mc.player, var1) < Math.max(var15, var14))) {
                     var15 = Math.max(var15, this.class35.method5452(var10, 6.0F, mc.player, var1, null, null));
                     if (var15 >= var14) {
                        return var15;
                     }
                  }
               }
            }
         }

         return var15;
      }
   }

   private void method731() {
      if (mc.player == null) {
         this.deque.clear();
         this.floatValue = Float.NaN;
         this.intValue8 = -1;
      } else {
         int var1 = mc.player.age;
         float var2 = mc.player.getHealth() + mc.player.getAbsorptionAmount();
         if (this.intValue8 > var1) {
            this.deque.clear();
            this.floatValue = Float.NaN;
         }

         if (!Float.isNaN(this.floatValue)) {
            float var3 = this.floatValue - var2;
            if (var3 > 0.0F) {
               this.deque.addLast(new AutoTotemModule.Entry(var1, var3));
               this.intValue4 = var1;
            }
         }

         this.floatValue = var2;
         this.intValue8 = var1;
         this.method733(var1);
      }
   }

   private float method732() {
      this.method733(mc.player.age);
      float var1 = 0.0F;

      for (AutoTotemModule.Entry var3 : this.deque) {
         var1 = Math.max(var1, var3.floatValue);
      }

      return var1;
   }

   private void method733(int var1) {
      int var2 = var1 - (int)this.predictTicksSetting.getValue3() + 1;

      while (!this.deque.isEmpty() && this.deque.peekFirst().intValue < var2) {
         this.deque.removeFirst();
      }
   }

   private void method734() {
      this.deque.clear();
      if (mc.player == null) {
         this.floatValue = Float.NaN;
         this.intValue8 = -1;
      } else {
         this.floatValue = mc.player.getHealth() + mc.player.getAbsorptionAmount();
         this.intValue8 = mc.player.age;
      }
   }

   @Generated
   public SliderSetting getHealthSetting() {
      return this.healthSetting;
   }

   @Generated
   public SliderSetting getElytraHealthSetting() {
      return this.elytraHealthSetting;
   }

   @Generated
   public MultiSelectSetting getTriggerSetting() {
      return this.triggerSetting;
   }

   @Generated
   public SliderSetting getCombatRange() {
      return this.combatRange;
   }

   @Generated
   public SliderSetting getCrystalMargin() {
      return this.crystalMargin;
   }

   @Generated
   public SliderSetting getTNTRangeSetting() {
      return this.tNTRangeSetting;
   }

   @Generated
   public SliderSetting getTridentRangeSetting() {
      return this.tridentRangeSetting;
   }

   @Generated
   public BooleanSetting getPredictSetting() {
      return this.predictSetting;
   }

   @Generated
   public SliderSetting getPredictTicksSetting() {
      return this.predictTicksSetting;
   }

   @Generated
   public SliderSetting getTime() {
      return this.time;
   }

   @Generated
   public BooleanSetting getHotbarTotems() {
      return this.hotbarTotems;
   }

   @Generated
   public MultiSelectSetting getKeepSlots() {
      return this.keepSlots;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry() {
      return this.entry;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry2() {
      return this.entry2;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry3() {
      return this.entry3;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry4() {
      return this.entry4;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry5() {
      return this.entry5;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry6() {
      return this.entry6;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry7() {
      return this.entry7;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry8() {
      return this.entry8;
   }

   @Generated
   public MultiSelectSetting.Entry getEntry9() {
      return this.entry9;
   }

   @Generated
   public Slot getSlot() {
      return this.slot;
   }

   @Generated
   public StopWatch getStopWatch() {
      return this.stopWatch;
   }

   @Generated
   public int getIntValue2() {
      return this.intValue;
   }

   @Generated
   public Boolean getBooleanValue() {
      return this.booleanValue;
   }

   @Generated
   public int getIntValue22() {
      return this.intValue2;
   }

   @Generated
   public int getIntValue3() {
      return this.intValue3;
   }

   @Generated
   public int getIntValue42() {
      return this.intValue4;
   }

   @Generated
   public int getIntValue52() {
      return this.intValue5;
   }

   @Generated
   public boolean isFlag2() {
      return this.flag;
   }

   @Generated
   public boolean isFlag22() {
      return this.flag2;
   }

   @Generated
   public int getIntValue6() {
      return this.intValue6;
   }

   @Generated
   public int getIntValue7() {
      return this.intValue7;
   }

   @Generated
   public Deque<AutoTotemModule.Entry> getDeque() {
      return this.deque;
   }

   @Generated
   public float getFloatValue2() {
      return this.floatValue;
   }

   @Generated
   public int getIntValue8() {
      return this.intValue8;
   }

   @Generated
   public Class35 getClass35() {
      return this.class35;
   }

   @Generated
   public boolean isFlag3() {
      return this.flag3;
   }

   @Generated
   public int getIntValue9() {
      return this.intValue9;
   }

   @Generated
   public int getIntValue10() {
      return this.intValue10;
   }

   @Generated
   public int getIntValue11() {
      return this.intValue11;
   }

   @Generated
   public int getIntValue12() {
      return this.intValue12;
   }

   @Generated
   public Item getItem() {
      return this.item;
   }

   @Generated
   public int getIntValue13() {
      return this.intValue13;
   }

   @Generated
   public int getIntValue14() {
      return this.intValue14;
   }

   @Generated
   public Map<Item, Integer> getItems() {
      return this.items;
   }

   @Generated
   public boolean isFlag4() {
      return this.flag4;
   }

   @Generated
   public boolean isFlag5() {
      return this.flag5;
   }

   @Generated
   public int getIntValue15() {
      return this.intValue15;
   }

   @Generated
   public float getFloatValue22() {
      return this.floatValue2;
   }

   private static final class Entry {
      private final int intValue;
      private final float floatValue;

      private Entry(int var1, float var2) {
         this.intValue = var1;
         this.floatValue = var2;
      }
   }
}

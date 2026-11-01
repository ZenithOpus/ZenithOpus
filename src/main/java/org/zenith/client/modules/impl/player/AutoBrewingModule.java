package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.collections.EmptyList;
import kotlin.collections.EmptyMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.BcCameraTestnerv;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.Event5;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.game.AutoPotionBrewingEnum;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "AutoBrewing",
   category = ModuleCategory.PLAYER,
   description = "module.autopotionbrewing.description"
)
public final class AutoBrewingModule extends Module {
   public static final AutoBrewingModule INSTANCE = new AutoBrewingModule();
   private static final long CONST_longValue = 50L;
   private static final long CONST_longValue2 = 75L;
   private static final long CONST_longValue3 = 20000L;
   private static final long CONST_longValue4 = 1500L;
   private static final int CONST_intValue = 5;
   private static final int CONST_intValue2 = 3;
   private static final int CONST_intValue3 = 4;
   private static final int[] CONST_intValues = new int[]{0, 1, 2};
   private static final ModeSetting potion = new ModeSetting(
      "module.autoPotionBrewing.potion",
      "",
      AutoPotionBrewingEnum.FIRE_RESISTANCE.text,
      AutoPotionBrewingEnum.INVISIBILITY.text,
      AutoPotionBrewingEnum.STRENGTH.text,
      AutoPotionBrewingEnum.SPEED.text,
      AutoPotionBrewingEnum.JUMP_BOOST.text
   );
   private final StopWatch stopWatch = new StopWatch();
   private final List<BlockPos> items = new ArrayList<>();
   private final List<BlockPos> items2 = new ArrayList<>();
   private List<Item> items3 = List.of();
   private Map<Item, Integer> items4 = Map.of();
   private AutoBrewingModule.Type2 type2 = AutoBrewingModule.Type2.IDLE;
   private Rotation rotation;
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private int intValue;
   private int intValue2;
   private int intValue3;
   private int intValue4;
   private int intValue5;
   private int intValue6;
   private int intValue7 = -1;
   private int intValue8 = -1;
   private AutoBrewingModule.Type3 type3 = AutoBrewingModule.Type3.IDLE;
   private boolean flag;
   private boolean flag2;
   private AutoBrewingModule.Type type = AutoBrewingModule.Type.SETUP;

   @Override
   public void onEnable() {
      if (mc.player != null && mc.player.networkHandler != null) {
         this.items3 = this.method4274();
         this.items4 = this.method4275(this.items3);
         this.intValue = 0;
         this.intValue2 = 0;
         this.intValue3 = 0;
         this.intValue4 = 0;
         this.intValue5 = 0;
         this.intValue6 = 0;
         this.method4308();
         this.flag = false;
         this.flag2 = false;
         this.type = AutoBrewingModule.Type.SETUP;
         this.blockPos = null;
         this.blockPos2 = null;
         this.rotation = null;
         this.items.clear();
         this.items2.clear();
         this.method4269();
         this.stopWatch.reset();
         super.onEnable();
      } else {
         this.setEnabled(false);
      }
   }

   @Override
   public void onDisable() {
      this.type2 = AutoBrewingModule.Type2.IDLE;
      this.rotation = null;
      this.blockPos = null;
      this.blockPos2 = null;
      this.items.clear();
      this.items2.clear();
      this.intValue2 = 0;
      this.intValue3 = 0;
      this.type = AutoBrewingModule.Type.SETUP;
      super.onDisable();
   }

   @EventTarget
   public void method4268(Event5 var1) {
      if (this.rotation != null) {
         Zenith.getInstance().getRotationManager().method7849(new RotationRequest(this.rotation, () -> {
            BcCameraTestnerv var1x = Zenith.getInstance().getRotationManager().getBcCameraTestnerv();
            return var1x.method1851(var1x.getRotationConfig(), this.rotation);
         }, Zenith.getInstance().getRotationManager().getBcCameraTestnerv().getRotationConfig()), 5, this);
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         switch (this.type2) {
            case IDLE:
            default:
               break;
            case SCAN_CHESTS:
               this.method4278();
               break;
            case OPEN_CHEST:
               this.method4280();
               break;
            case LOOT_CHEST:
               this.method4281();
               break;
            case SCAN_OUTPUT_CHESTS:
               this.method4285();
               break;
            case OPEN_OUTPUT_CHEST:
               this.method4288();
               break;
            case STORE_RESULTS:
               this.method4290();
               break;
            case SCAN_STANDS:
               this.method4293();
               break;
            case OPEN_STAND:
               this.method4298();
               break;
            case LOAD_BOTTLES:
               this.method4299();
               break;
            case LOAD_FUEL:
               this.method4300();
               break;
            case LOAD_INGREDIENT:
               this.method4301();
               break;
            case WAIT_PHASE_DELAY:
               this.method4302();
               break;
            case TAKE_RESULT:
               this.method4303();
         }
      } else {
         this.setToggled(false);
      }
   }

   private void method4269() {
      this.intValue = this.method4273();
      int var1 = this.intValue * 3;
      if (this.intValue <= 0) {
         if (!this.flag) {
            this.flag = true;
            this.flag2 = false;
            this.type2 = AutoBrewingModule.Type2.SCAN_CHESTS;
         } else {
            this.method4277();
            this.setToggled(false);
         }
      } else {
         ChatLogger.method2019("Ресурсов хватает на " + var1 + " зелий (" + this.intValue + " варки)");
         this.type2 = AutoBrewingModule.Type2.SCAN_STANDS;
      }
   }

   private void method4270() {
      this.method4272();
      this.setToggled(false);
   }

   private void method4271() {
      this.type2 = AutoBrewingModule.Type2.OPEN_STAND;
      this.stopWatch.reset();
   }

   private void method4272() {
      ScreenHandler var1 = mc.player.currentScreenHandler;
      if (var1 != null && !var1.getCursorStack().isEmpty()) {
         this.method4309(var1);
         if (!var1.getCursorStack().isEmpty()) {
            return;
         }
      }

      InventoryUtils.closeScreen();
   }

   private int method4273() {
      int var1 = this.method4316() / 3;

      for (Entry var3 : this.items4.entrySet()) {
         var1 = Math.min(var1, this.method4317((Item)var3.getKey()) / (Integer)var3.getValue());
      }

      return var1;
   }

   private List<Item> method4274() {
      return AutoPotionBrewingEnum.method7(potion.get()).method6();
   }

   private Map<Item, Integer> method4275(List<Item> var1) {
      LinkedHashMap var2 = new LinkedHashMap();
      this.method4276(var2, Items.BLAZE_POWDER, 1);

      for (Item var4 : var1) {
         this.method4276(var2, var4, 1);
      }

      return var2;
   }

   private void method4276(Map<Item, Integer> var1, Item var2, int var3) {
      var1.put(var2, var1.getOrDefault(var2, 0) + var3);
   }

   private void method4277() {
      ArrayList var1 = new ArrayList();
      int var2;
      if ((var2 = this.method4316()) < 3) {
         var1.add(Items.POTION.getName().getString() + " " + var2 + "/3");
      }

      for (Entry var4 : this.items4.entrySet()) {
         int var5 = this.method4317((Item)var4.getKey());
         int var6 = (Integer)var4.getValue();
         if (var5 < var6) {
            var1.add(((Item)var4.getKey()).getName().getString() + " " + var5 + "/" + var6);
         }
      }

      String var7 = potion.getValue2().getName();
      if (var1.isEmpty()) {
         ChatLogger.method2019("Для зелья " + var7 + " ресурсов хватает");
      } else {
         ChatLogger.method2021("Для зелья " + var7 + " не хватает: " + String.join(", ", var1));
      }
   }

   private void method4278() {
      this.method4286();
      if (this.items2 != null && this.items2.getClass() == EmptyList.class ? true : this.items2.isEmpty()) {
         this.method4277();
         this.setToggled(false);
      } else {
         this.method4279();
      }
   }

   private void method4279() {
      if (this.items2 != null && this.items2.getClass() == EmptyList.class ? true : this.items2.isEmpty()) {
         this.method4272();
         if (!this.flag2) {
            this.method4277();
            this.setToggled(false);
         } else {
            this.method4269();
         }
      } else {
         this.blockPos2 = this.items2.removeFirst();
         this.type2 = AutoBrewingModule.Type2.OPEN_CHEST;
         this.stopWatch.reset();
      }
   }

   private void method4280() {
      this.method4289(AutoBrewingModule.Type2.LOOT_CHEST, this::method4279);
   }

   private void method4281() {
      GenericContainerScreenHandler var1 = this.method4305();
      if (var1 == null) {
         this.method4280();
      } else if (this.stopWatch.method7546(50L)) {
         Slot var2 = this.method4282(var1);
         if (var2 == null) {
            this.method4272();
            this.blockPos2 = null;
            this.method4279();
         } else if (!this.method4284(var2.getStack())) {
            this.method4272();
            this.blockPos2 = null;
            this.method4279();
         } else {
            InventoryUtils.method905(var1.syncId, var2.id, 0, SlotActionType.QUICK_MOVE, false);
            this.flag2 = true;
            this.stopWatch.reset();
         }
      }
   }

   private Slot method4282(GenericContainerScreenHandler var1) {
      int var2 = var1.getInventory().size();

      for (int var3 = 0; var3 < var2; var3++) {
         Slot var4 = var1.getSlot(var3);
         if (this.method4283(var4.getStack())) {
            return var4;
         }
      }

      return null;
   }

   private boolean method4283(ItemStack var1) {
      if (!this.method4314(var1)) {
         boolean var2;
         label20: {
            var var10000 = this.items4;
            Item var10001 = var1.getItem();
            if (var10000 != null) {
               if (var10000.isEmpty()) {
                  var2 = false;
                  break label20;
               }
            }

            var2 = var10000.containsKey(var10001);
         }

         if (!var2) {
            return false;
         }
      }

      return true;
   }

   private boolean method4284(ItemStack var1) {
      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = mc.player.getInventory().getStack(var2);
         if (var3.isEmpty() || this.method4312(var3, var1) && var3.getCount() < var3.getMaxCount()) {
            return true;
         }
      }

      return false;
   }

   private void method4285() {
      this.method4286();
      this.method4287();
   }

   private void method4286() {
      this.items2.clear();

      for (BlockPos var2 : UseItemUtils.method6506(mc.player.getBlockPos(), 5.0F, 3.0F)) {
         BlockPos var3 = var2.toImmutable();
         if (this.method4320(mc.world.getBlockState(var3)) && this.method4319(var3) != null) {
            this.items2.add(var3);
         }
      }

      this.items2.sort(Comparator.comparingDouble(var0 -> var0.toCenterPos().squaredDistanceTo(mc.player.getEyePos())));
   }

   private void method4287() {
      if (this.items2 != null && this.items2.getClass() == EmptyList.class ? true : this.items2.isEmpty()) {
         this.method4292();
      } else {
         this.blockPos2 = this.items2.removeFirst();
         this.type2 = AutoBrewingModule.Type2.OPEN_OUTPUT_CHEST;
         this.stopWatch.reset();
      }
   }

   private void method4288() {
      this.method4289(AutoBrewingModule.Type2.STORE_RESULTS, this::method4287);
   }

   private void method4289(AutoBrewingModule.Type2 var1, Runnable var2) {
      if (this.blockPos2 == null) {
         var2.run();
      } else if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler) {
         this.type2 = var1;
         this.rotation = null;
         this.stopWatch.reset();
      } else if (this.stopWatch.method7546(1500L)) {
         this.blockPos2 = null;
         var2.run();
      } else {
         BlockHitResult var3 = this.method4319(this.blockPos2);
         if (var3 == null) {
            this.blockPos2 = null;
            var2.run();
         } else {
            this.rotation = RotationUtils.method6913(var3.getPos());
            if (this.method4318(this.blockPos2) && this.stopWatch.method7546(50L)) {
               UseItemUtils.method6514(var3, Hand.MAIN_HAND);
               this.stopWatch.reset();
            }
         }
      }
   }

   private void method4290() {
      GenericContainerScreenHandler var1 = this.method4305();
      if (var1 == null) {
         this.method4288();
      } else if (this.stopWatch.method7546(50L)) {
         Slot var2 = this.method4313(var1, this::method4315);
         if (var2 == null) {
            this.method4272();
            this.method4292();
         } else if (!this.method4291(var1, var2.getStack())) {
            this.method4272();
            this.blockPos2 = null;
            this.method4287();
         } else {
            InventoryUtils.method905(var1.syncId, var2.id, 0, SlotActionType.QUICK_MOVE, false);
            this.stopWatch.reset();
         }
      }
   }

   private boolean method4291(GenericContainerScreenHandler var1, ItemStack var2) {
      int var3 = var1.getInventory().size();

      for (int var4 = 0; var4 < var3; var4++) {
         ItemStack var5 = var1.getSlot(var4).getStack();
         if (var5.isEmpty() || this.method4312(var5, var2) && var5.getCount() < var5.getMaxCount()) {
            return true;
         }
      }

      return false;
   }

   private void method4292() {
      this.method4272();
      this.blockPos2 = null;
      if (this.intValue <= 0) {
         ChatLogger.method2019("Автозельеварение завершено");
         this.setToggled(false);
      } else {
         this.method4294();
      }
   }

   private void method4293() {
      this.items.clear();

      for (BlockPos var2 : UseItemUtils.method6506(mc.player.getBlockPos(), 5.0F, 3.0F)) {
         BlockPos var3 = var2.toImmutable();
         if (mc.world.getBlockState(var3).isOf(Blocks.BREWING_STAND) && this.method4319(var3) != null) {
            this.items.add(var3);
         }
      }

      this.items.sort(Comparator.comparingDouble(var0 -> var0.toCenterPos().squaredDistanceTo(mc.player.getEyePos())));
      if (this.items != null && this.items.getClass() == EmptyList.class ? true : this.items.isEmpty()) {
         ChatLogger.method2021("Рядом нет доступных зельеварок");
         this.setToggled(false);
      } else {
         this.method4294();
      }
   }

   private void method4294() {
      this.intValue2 = Math.min(this.intValue, this.items.size());
      this.intValue3 = 0;
      this.intValue4 = 0;
      this.type = AutoBrewingModule.Type.SETUP;
      this.method4295();
   }

   private void method4295() {
      if (this.intValue3 >= this.intValue2) {
         this.method4297();
      } else {
         this.blockPos = this.items.get(this.intValue3);
         this.intValue5 = 0;
         this.intValue6 = 0;
         this.method4308();
         this.type2 = AutoBrewingModule.Type2.OPEN_STAND;
         this.stopWatch.reset();
      }
   }

   private void method4296() {
      this.method4272();
      this.intValue3++;
      this.method4295();
   }

   private void method4297() {
      this.method4272();
      this.intValue3 = 0;
      this.method4308();
      if (this.type == AutoBrewingModule.Type.SETUP) {
         this.type = AutoBrewingModule.Type.INGREDIENT_WAIT;
         this.blockPos = null;
         this.type2 = AutoBrewingModule.Type2.WAIT_PHASE_DELAY;
         this.stopWatch.reset();
      } else if (this.type == AutoBrewingModule.Type.INGREDIENT_LOAD) {
         this.type = AutoBrewingModule.Type.INGREDIENT_WAIT;
         this.blockPos = null;
         this.type2 = AutoBrewingModule.Type2.WAIT_PHASE_DELAY;
         this.stopWatch.reset();
      } else {
         this.intValue = this.intValue - this.intValue2;
         this.type2 = AutoBrewingModule.Type2.SCAN_OUTPUT_CHESTS;
         this.stopWatch.reset();
      }
   }

   private void method4298() {
      if (this.blockPos == null) {
         this.type2 = AutoBrewingModule.Type2.SCAN_STANDS;
      } else if (mc.player.currentScreenHandler instanceof BrewingStandScreenHandler) {
         this.type2 = switch (this.type) {
            case SETUP -> AutoBrewingModule.Type2.LOAD_BOTTLES;
            case INGREDIENT_LOAD -> AutoBrewingModule.Type2.LOAD_INGREDIENT;
            case INGREDIENT_WAIT -> AutoBrewingModule.Type2.LOAD_INGREDIENT;
            case COLLECT -> AutoBrewingModule.Type2.TAKE_RESULT;
         };
         this.intValue5 = 0;
         this.stopWatch.reset();
         this.rotation = null;
      } else if (this.stopWatch.method7546(1500L)) {
         this.blockPos = null;
         this.type2 = AutoBrewingModule.Type2.SCAN_STANDS;
         this.rotation = null;
      } else {
         BlockHitResult var1 = this.method4319(this.blockPos);
         if (var1 == null) {
            this.blockPos = null;
            this.type2 = AutoBrewingModule.Type2.SCAN_STANDS;
            this.rotation = null;
         } else {
            this.rotation = RotationUtils.method6913(var1.getPos());
            if (this.method4318(this.blockPos) && this.stopWatch.method7546(50L)) {
               UseItemUtils.method6514(var1, Hand.MAIN_HAND);
               this.stopWatch.reset();
            }
         }
      }
   }

   private void method4299() {
      BrewingStandScreenHandler var1 = this.method4304();
      if (var1 == null) {
         this.method4271();
      } else if (this.stopWatch.method7546(75L)) {
         if (this.type3 == AutoBrewingModule.Type3.IDLE && !var1.getCursorStack().isEmpty()) {
            this.method4309(var1);
            this.stopWatch.reset();
         } else if (this.intValue5 >= CONST_intValues.length) {
            int var4;
            if ((var4 = this.method4306(var1)) != -1) {
               this.intValue5 = var4;
               this.stopWatch.reset();
            } else {
               this.type2 = AutoBrewingModule.Type2.LOAD_FUEL;
               this.stopWatch.reset();
            }
         } else {
            int var2 = CONST_intValues[this.intValue5];
            if (this.type3 != AutoBrewingModule.Type3.IDLE) {
               this.method4307(var1, this::method4314, var2);
               this.stopWatch.reset();
            } else {
               ItemStack var3 = var1.getSlot(var2).getStack();
               if (this.method4314(var3)) {
                  this.method4308();
                  this.intValue5++;
                  this.stopWatch.reset();
               } else if (!var3.isEmpty()) {
                  this.intValue6 = 0;
                  this.type2 = AutoBrewingModule.Type2.TAKE_RESULT;
                  this.stopWatch.reset();
               } else if (!this.method4307(var1, this::method4314, var2)) {
                  ChatLogger.method2021("Не найдены бутылочки с водой");
                  this.method4270();
               } else {
                  this.stopWatch.reset();
               }
            }
         }
      }
   }

   private void method4300() {
      BrewingStandScreenHandler var1 = this.method4304();
      if (var1 == null) {
         this.method4271();
      } else if (this.stopWatch.method7546(50L)) {
         if (this.type3 == AutoBrewingModule.Type3.IDLE && !var1.getCursorStack().isEmpty()) {
            this.method4309(var1);
            this.stopWatch.reset();
         } else if (this.type3 != AutoBrewingModule.Type3.IDLE) {
            this.method4307(var1, var0 -> var0.isOf(Items.BLAZE_POWDER), 4);
            this.stopWatch.reset();
         } else if (!var1.getSlot(4).getStack().isEmpty()) {
            int var2;
            if ((var2 = this.method4306(var1)) != -1) {
               this.intValue5 = var2;
               this.type2 = AutoBrewingModule.Type2.LOAD_BOTTLES;
               this.stopWatch.reset();
            } else {
               this.method4308();
               this.type2 = AutoBrewingModule.Type2.LOAD_INGREDIENT;
            }
         } else if (!this.method4307(var1, var0 -> var0.isOf(Items.BLAZE_POWDER), 4)) {
            ChatLogger.method2021("Не найдено топливо для зельеварки");
            this.method4270();
         } else {
            this.stopWatch.reset();
         }
      }
   }

   private void method4301() {
      BrewingStandScreenHandler var1 = this.method4304();
      if (var1 == null) {
         this.method4271();
      } else if (this.stopWatch.method7546(50L)) {
         if (this.type3 == AutoBrewingModule.Type3.IDLE && !var1.getCursorStack().isEmpty()) {
            this.method4309(var1);
            this.stopWatch.reset();
         } else if (this.intValue4 >= this.items3.size()) {
            this.method4296();
         } else {
            Item var2 = this.items3.get(this.intValue4);
            if (this.type3 != AutoBrewingModule.Type3.IDLE) {
               this.method4307(var1, var1x -> var1x.isOf(var2), 3);
               this.stopWatch.reset();
            } else if (!var1.getSlot(3).getStack().isEmpty()) {
               this.method4308();
               this.method4296();
            } else if (!this.method4307(var1, var1x -> var1x.isOf(var2), 3)) {
               ChatLogger.method2021("Не найден ингредиент: " + var2.getName().getString());
               this.method4270();
            } else {
               this.stopWatch.reset();
            }
         }
      }
   }

   private void method4302() {
      if (this.stopWatch.method7546(20000L)) {
         this.intValue4++;
         if (this.intValue4 >= this.items3.size()) {
            this.type = AutoBrewingModule.Type.COLLECT;
         } else {
            this.type = AutoBrewingModule.Type.INGREDIENT_LOAD;
         }

         this.method4295();
      }
   }

   private void method4303() {
      BrewingStandScreenHandler var1 = this.method4304();
      if (var1 == null) {
         this.method4271();
      } else if (this.stopWatch.method7546(50L)) {
         if (this.intValue6 >= CONST_intValues.length) {
            this.method4296();
         } else {
            int var2 = CONST_intValues[this.intValue6];
            if (var1.getSlot(var2).getStack().isEmpty()) {
               this.intValue6++;
               this.stopWatch.reset();
            } else {
               InventoryUtils.method905(var1.syncId, var2, 0, SlotActionType.QUICK_MOVE, false);
               this.stopWatch.reset();
            }
         }
      }
   }

   private BrewingStandScreenHandler method4304() {
      return mc.player.currentScreenHandler instanceof BrewingStandScreenHandler var2 ? var2 : null;
   }

   private GenericContainerScreenHandler method4305() {
      return mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var2 ? var2 : null;
   }

   private int method4306(BrewingStandScreenHandler var1) {
      if (var1.getCursorStack().isEmpty() && this.type3 == AutoBrewingModule.Type3.IDLE) {
         for (int var2 = 0; var2 < CONST_intValues.length; var2++) {
            if (!this.method4314(var1.getSlot(CONST_intValues[var2]).getStack())) {
               return var2;
            }
         }

         return -1;
      } else {
         return 0;
      }
   }

   private boolean method4307(ScreenHandler var1, AutoBrewingModule.Handler var2, int var3) {
      if (this.type3 == AutoBrewingModule.Type3.IDLE) {
         Slot var4 = this.method4313(var1, var2);
         if (var4 == null) {
            return false;
         } else {
            this.intValue7 = var4.id;
            this.intValue8 = var3;
            this.type3 = AutoBrewingModule.Type3.PICK_SOURCE;
            InventoryUtils.method905(var1.syncId, var4.id, 0, SlotActionType.PICKUP, false);
            return true;
         }
      } else if (this.intValue8 != var3) {
         this.method4308();
         return true;
      } else if (this.type3 == AutoBrewingModule.Type3.PICK_SOURCE) {
         if (var1.getCursorStack().isEmpty()) {
            this.method4308();
            return true;
         } else {
            InventoryUtils.method905(var1.syncId, this.intValue8, 1, SlotActionType.PICKUP, false);
            this.type3 = AutoBrewingModule.Type3.PLACE_ONE;
            return true;
         }
      } else if (this.type3 == AutoBrewingModule.Type3.PLACE_ONE) {
         if (var1.getCursorStack().isEmpty()) {
            this.method4308();
            return true;
         } else {
            this.method4309(var1);
            this.type3 = AutoBrewingModule.Type3.RETURN_REMAINDER;
            return true;
         }
      } else {
         if (var1.getCursorStack().isEmpty()) {
            this.method4308();
         } else {
            this.method4309(var1);
         }

         return true;
      }
   }

   private void method4308() {
      this.intValue7 = -1;
      this.intValue8 = -1;
      this.type3 = AutoBrewingModule.Type3.IDLE;
   }

   private void method4309(ScreenHandler var1) {
      if (var1.getCursorStack().isEmpty()) {
         this.method4308();
      } else {
         Slot var2 = this.method4310(var1, this.intValue7);
         if (this.method4311(var2, var1.getCursorStack())) {
            InventoryUtils.method905(var1.syncId, var2.id, 0, SlotActionType.PICKUP, false);
         } else {
            var2 = this.method4313(var1, ItemStack::isEmpty);
            if (var2 != null) {
               InventoryUtils.method905(var1.syncId, var2.id, 0, SlotActionType.PICKUP, false);
            } else {
               ChatLogger.method2021("Нет свободного слота, чтобы вернуть предмет с курсора");
            }
         }
      }
   }

   private Slot method4310(ScreenHandler var1, int var2) {
      return var1.slots.stream().filter(var1x -> var1x.id == var2).findFirst().orElse(null);
   }

   private boolean method4311(Slot var1, ItemStack var2) {
      return var1 != null
         && var1.inventory instanceof PlayerInventory
         && (var1.getStack().isEmpty() || this.method4312(var1.getStack(), var2) && var1.getStack().getCount() < var1.getStack().getMaxCount());
   }

   private boolean method4312(ItemStack var1, ItemStack var2) {
      return ItemStack.areItemsAndComponentsEqual(var1, var2);
   }

   private Slot method4313(ScreenHandler var1, AutoBrewingModule.Handler var2) {
      return var1.slots
         .stream()
         .filter(var0 -> var0.inventory instanceof PlayerInventory)
         .filter(var1x -> var2.matches(var1x.getStack()))
         .findFirst()
         .orElse(null);
   }

   private boolean method4314(ItemStack var1) {
      if (!var1.isOf(Items.POTION)) {
         return false;
      } else {
         PotionContentsComponent var2 = var1.get(DataComponentTypes.POTION_CONTENTS);
         return var2 != null && var2.potion().isPresent() && var2.potion().get().equals(Potions.WATER);
      }
   }

   private boolean method4315(ItemStack var1) {
      if (!var1.isOf(Items.POTION)) {
         return false;
      } else {
         PotionContentsComponent var2 = var1.get(DataComponentTypes.POTION_CONTENTS);
         return var2 != null && var2.potion().isPresent() && !var2.potion().get().equals(Potions.WATER);
      }
   }

   private int method4316() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         ItemStack var3 = mc.player.getInventory().getStack(var2);
         if (this.method4314(var3)) {
            var1 += var3.getCount();
         }
      }

      return var1;
   }

   private int method4317(Item var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < 36; var3++) {
         if (mc.player.getInventory().getStack(var3).isOf(var1)) {
            var2 += mc.player.getInventory().getStack(var3).getCount();
         }
      }

      return var2;
   }

   private boolean method4318(BlockPos var1) {
      Rotation var2 = Zenith.getInstance().getRotationManager().method7844();
      BlockHitResult var3 = TargetUtils.method3216(
         mc.player.getCameraPosVec(1.0F), var2, mc.player.getBlockInteractionRange(), var1x -> var1x != null && var1x.getBlockPos().equals(var1)
      );
      return var3 != null && var3.getType() != HitResult.Type.MISS && var3.getBlockPos().equals(var1);
   }

   private BlockHitResult method4319(BlockPos var1) {
      BlockState var2 = mc.world.getBlockState(var1);
      if (!var2.isOf(Blocks.BREWING_STAND) && !this.method4320(var2)) {
         return null;
      } else {
         Vec3d var3 = var1.toCenterPos();
         ArrayList<Vec3d> var4 = new ArrayList();
         var4.add(var3);

         for (Direction var8 : Direction.values()) {
            var4.add(var3.add(Vec3d.of(var8.getVector()).multiply(0.5)));
         }

         Vec3d var12 = mc.player.getCameraPosVec(1.0F);
         double var13 = mc.player.getBlockInteractionRange();

         for (Object var9_o : (Iterable<?>)(Object)(var4)) {
            Vec3d var9 = (Vec3d)var9_o;
            Rotation var10 = RotationUtils.method6913(var9);
            BlockHitResult var11 = TargetUtils.method3216(var12, var10, var13, var1x -> var1x != null && var1x.getBlockPos().equals(var1));
            if (var11 != null && var11.getType() != HitResult.Type.MISS && var11.getBlockPos().equals(var1)) {
               return var11;
            }
         }

         return null;
      }
   }

   private boolean method4320(BlockState var1) {
      return var1.isOf(Blocks.CHEST) || var1.isOf(Blocks.TRAPPED_CHEST) || var1.isOf(Blocks.BARREL);
   }

   @FunctionalInterface
   private interface Handler {
      boolean matches(ItemStack var1);
   }

   private static enum Type {
      SETUP,
      INGREDIENT_LOAD,
      INGREDIENT_WAIT,
      COLLECT;
   }

   private static enum Type2 {
      IDLE,
      SCAN_CHESTS,
      OPEN_CHEST,
      LOOT_CHEST,
      SCAN_OUTPUT_CHESTS,
      OPEN_OUTPUT_CHEST,
      STORE_RESULTS,
      SCAN_STANDS,
      OPEN_STAND,
      LOAD_BOTTLES,
      LOAD_FUEL,
      LOAD_INGREDIENT,
      WAIT_PHASE_DELAY,
      TAKE_RESULT;
   }

   private static enum Type3 {
      IDLE,
      PICK_SOURCE,
      PLACE_ONE,
      RETURN_REMAINDER;
   }
}

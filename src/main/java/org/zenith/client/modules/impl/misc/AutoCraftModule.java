package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.ShapedCraftingRecipeDisplay;
import net.minecraft.recipe.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.api.Class1;
import org.zenith.utility.game.CraftBindingController;
import org.zenith.utility.game.CraftPresetSaver;
import org.zenith.utility.game.CraftRecipe;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.AutoCraftState;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "AutoCraft",
   category = ModuleCategory.MISC,
   description = "module.autocraft.description"
)
public final class AutoCraftModule extends Module {
   public static final AutoCraftModule INSTANCE = new AutoCraftModule();
   private static final int CONST_intValue = 0;
   private static final int CONST_intValue2 = 36;
   private static final String text = "servers";
   private static final long CONST_longValue = 85L;
   private static final long CONST_longValue2 = 600L;
   private static final float CONST_floatValue = 0.5F;
   private static final int CONST_intValue3 = 30;
   private static final int CONST_intValue4 = 5;
   private static final int CONST_intValue5 = 5;
   private static final Map<Item, String> items = new ConcurrentHashMap<>();
   private static final Pattern pattern = Pattern.compile("[^a-z0-9_]+");
   private final ButtonSetting openEditor = new ButtonSetting("module.autoCraft.openEditor", this::method1011);
   private final BooleanSetting autoTake = new BooleanSetting("module.autoCraft.autoTake", "module.autoCraft.autoTake.desc", true);
   private final BooleanSetting autoStore = new BooleanSetting("module.autoCraft.autoStore", "module.autoCraft.autoStore.desc", true);
   private final BooleanSetting batch = new BooleanSetting("module.autoCraft.batch", "module.autoCraft.batch.desc", true);
   private final List<CraftRecipe> items2 = new ArrayList<>();
   private final Map<String, RecipeDisplayEntry> items3 = new HashMap<>();
   private final Map<BlockPos, AutoCraftModule.Entry> items4 = new HashMap<>();
   private final List<BlockPos> items5 = new ArrayList<>();
   private final Map<String, Integer> items6 = new HashMap<>();
   private final StopWatch stopWatch = new StopWatch();
   private final CraftBindingController craftBindingController = new CraftBindingController(this);
   private final CraftPresetSaver craftPresetSaver = new CraftPresetSaver(this);
   private final AutoCraftState autoCraftState = new AutoCraftState(this);
   private AutoCraftModule.Type type = AutoCraftModule.Type.IDLE;
   private String text2 = "";
   private String statusText = "";
   private String text3 = "";
   private long longValue;
   private List<RecipeResultCollection> items7;
   private DynamicRegistryManager dynamicRegistryManager;
   private Language language;
   private boolean flag = true;
   private ClientWorld clientWorld;
   private int intValue;
   private int intValue2;
   private int intValue3;
   private int intValue4 = -1;
   private int intValue5 = -1;
   private int intValue6;
   private int intValue7;
   private int intValue8 = -1;
   private int intValue9;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;
   private AutoCraftModule.Type2 type2 = AutoCraftModule.Type2.IDLE;
   private ItemStack itemStack = ItemStack.EMPTY;
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private BlockPos blockPos3;
   private BlockPos blockPos4;
   private Rotation rotation;

   private AutoCraftModule() {
      for (Setting var4 : new Setting[]{this.autoTake, this.autoStore, this.batch}) {
         var4.setVisible(() -> false);
      }

      this.method1091();
   }

   private void method1011() {
      Zenith.getInstance().getNLMenuScreen().openTool(2);
   }

   @Override
   public void onEnable() {
      this.craftBindingController.reset();
      super.onEnable();
      this.statusText = "";
      this.autoCraftState.reset();
      this.method1078();
      this.method1079();
      this.method1018();
      this.method1022();
      this.flag4 = false;
      this.method1036();
      this.clientWorld = mc.world;
      this.method1081(true);
   }

   @Override
   public void onDisable() {
      if (mc.player != null) {
         ScreenHandler var2 = mc.player.currentScreenHandler;
         if (var2 instanceof ScreenHandler) {
            this.method1075(var2);
         }
      }

      this.type = AutoCraftModule.Type.IDLE;
      this.text3 = "";
      this.intValue = 0;
      this.craftBindingController.reset();
      this.craftPresetSaver.reset();
      this.statusText = "";
      this.autoCraftState.reset();
      this.method1078();
      this.method1079();
      this.method1018();
      this.method1022();
      this.flag4 = false;
      this.method1058();
      this.clientWorld = null;
      super.onDisable();
   }

   @EventTarget
   public void onTick(TickEvent var1) {
      if (!this.craftPresetSaver.method3191()) {
         this.method1015();
      }
   }

   @EventTarget
   public void method1012(Event5 var1) {
      if (this.rotation != null) {
         Rotation var2 = this.rotation;
         rotationManager.method7849(
            new RotationRequest(var2, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var2), bcCameraTestnerv.getRotationConfig()),
            5,
            this
         );
      }
   }

   @EventTarget
   public void method1013(MouseButtonEvent var1) {
      this.craftBindingController.method7228(var1);
   }

   @EventTarget
   public void method1014(DrawEvent var1) {
      this.method1080(var1);
   }

   public void method1015() {
      if (mc.world != this.clientWorld) {
         this.clientWorld = mc.world;
         this.method1036();
      }

      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (!this.craftBindingController.isBinding()) {
            this.method1081(false);
            this.method1091();
            CraftRecipe var1 = this.method1096();
            if (var1 == null) {
               this.method1076("В списке нет серверных рецептов");
            } else {
               ScreenHandler var2 = mc.player.currentScreenHandler;
               if (var2 instanceof CraftingScreenHandler var3) {
                  this.method1016(var1, var3);
               } else if (this.method1019(var2)) {
                  if (this.type != AutoCraftModule.Type.OPENING_OUTPUT && !var2.getCursorStack().isEmpty() && this.stopWatch.method7546(85L)) {
                     this.method1075(var2);
                     this.stopWatch.reset();
                  } else {
                     this.method1023(var1, var2);
                  }
               } else if (this.intValue > 0 && this.method1059()) {
                  this.intValue--;

                  this.statusText = switch (this.type) {
                     case OPENING_ANALYZE -> "Анализирую сундуки...";
                     case OPENING_WORKBENCH -> "Открываю верстак...";
                     default -> "Открываю хранилище...";
                  };
               } else if (this.method1059()) {
                  this.method1057();
               } else if (mc.currentScreen == null) {
                  if (this.flag2 && this.type != AutoCraftModule.Type.SCANNING) {
                     this.method1035(var1);
                  } else {
                     this.method1037(var1);
                  }
               }
            }
         }
      }
   }

   private void method1016(CraftRecipe var1, CraftingScreenHandler var2) {
      this.intValue = 0;
      this.type = AutoCraftModule.Type.CRAFTING;
      this.statusText = "Крафчу " + var1.method5400(this);
      if (this.stopWatch.method7546(85L)) {
         if (!var2.getCursorStack().isEmpty()) {
            this.method1075(var2);
            this.stopWatch.reset();
         } else if (!var2.getSlot(0).getStack().isEmpty()) {
            if (!this.method1048(var1)) {
               this.flag4 = true;
               this.method1077("Инвентарь заполнен, складываю результат", true);
            } else {
               mc.interactionManager.clickSlot(var2.syncId, 0, 0, SlotActionType.QUICK_MOVE, mc.player);
               this.flag4 = true;
               this.method1018();
               this.stopWatch.reset();
            }
         } else if (this.flag4 && this.method1046(var1)) {
            this.method1077("Складываю результат крафта", true);
         } else if (this.intValue2 > 0) {
            this.intValue2--;
            if (this.intValue2 == 0) {
               String var4 = this.flag4 ? "Складываю результат крафта" : "Не хватает ресурсов или сервер отклонил рецепт";
               this.method1077(var4, false);
            }
         } else {
            RecipeDisplayEntry var3 = this.items3.get(var1.getId());
            if (var3 == null) {
               this.method1076("Рецепт больше не доступен в книге");
            } else {
               this.method1017(var2, var3);
            }
         }
      }
   }

   private void method1017(CraftingScreenHandler var1, RecipeDisplayEntry var2) {
      if (mc.getNetworkHandler() == null) {
         this.method1076("Нет сетевого подключения к серверу");
      } else {
         mc.getNetworkHandler().sendPacket(new CraftRequestC2SPacket(var1.syncId, var2.id(), this.method1045()));
         this.intValue2 = 5;
         this.statusText = "Запрашиваю рецепт из книги...";
         this.stopWatch.reset();
      }
   }

   private void method1018() {
      this.intValue2 = 0;
   }

   private boolean method1019(ScreenHandler var1) {
      return var1 instanceof GenericContainerScreenHandler || var1 instanceof ShulkerBoxScreenHandler;
   }

   private int method1020(ScreenHandler var1) {
      return Math.max(0, var1.slots.size() - 36);
   }

   private boolean method1021(ScreenHandler var1) {
      if (this.intValue4 != var1.syncId) {
         this.intValue4 = var1.syncId;
         this.intValue3 = 5;
      }

      if (this.intValue3 <= 0) {
         return false;
      } else {
         this.intValue3--;

         this.statusText = switch (this.type) {
            case OPENING_ANALYZE -> "Считываю содержимое сундука...";
            case OPENING_SOURCE -> "Синхронизирую сундук с ресурсами...";
            case OPENING_OUTPUT -> "Синхронизирую сундук-склад...";
            default -> "Синхронизирую контейнер...";
         };
         this.stopWatch.reset();
         return true;
      }
   }

   private void method1022() {
      this.intValue4 = -1;
      this.intValue3 = 0;
   }

   private void method1023(CraftRecipe var1, ScreenHandler var2) {
      if (!this.method1021(var2)) {
         this.intValue = 0;
         if (this.type == AutoCraftModule.Type.OPENING_ANALYZE) {
            this.method1040(var2, this.blockPos2);
            mc.player.closeHandledScreen();
            this.method1022();
            this.blockPos2 = null;
            this.type = AutoCraftModule.Type.SCANNING;
            this.stopWatch.reset();
         } else if (this.type == AutoCraftModule.Type.OPENING_SOURCE) {
            if (!this.text3.isBlank() && this.blockPos3 != null) {
               this.method1024(var1, var2, this.text3, this.blockPos3);
            } else {
               this.method1077(this.statusText, true);
            }
         } else {
            this.autoCraftState.method744();
            if (this.type == AutoCraftModule.Type.OPENING_OUTPUT) {
               if (this.blockPos4 != null) {
                  this.method1040(var2, this.blockPos4);
               }

               this.method1026(var1, var2);
            } else {
               mc.player.closeHandledScreen();
               this.method1022();
               this.type = AutoCraftModule.Type.IDLE;
            }
         }
      }
   }

   private void method1024(CraftRecipe var1, ScreenHandler var2, String var3, BlockPos var4) {
      String var5 = var1.method5395(var3);
      String var6 = var1.method5396(var3);
      this.statusText = "Забираю " + this.method1107(var5, var6);
      if (this.stopWatch.method7546(85L)) {
         if (!this.autoCraftState.method746(var1, var3)) {
            int var7 = this.items6.getOrDefault(var3, var1.method5389(var3));
            int var8 = this.method1067(var5, var6);
            if (var8 < var7 && this.method1049(var5, var6)) {
               int var9 = this.method1020(var2);
               int var10 = Math.max(1, var7 - var8);
               int var11 = this.method1025(var2, var9, var5, var6, var10);
               if (var11 != -1) {
                  mc.interactionManager.clickSlot(var2.syncId, var11, 0, SlotActionType.QUICK_MOVE, mc.player);
                  this.autoCraftState.method750(var1, var3);
                  this.stopWatch.reset();
               } else {
                  int var12 = this.autoCraftState.method749(var1, var3);
                  this.method1040(var2, var4);
                  this.autoCraftState.method751(var1, var3, var5, var6, var12 <= this.autoCraftState.method745());
                  this.method1077("Источник пуст: " + this.method1107(var5, var6), true);
               }
            } else {
               this.method1040(var2, var4);
               this.autoCraftState.method750(var1, var3);
               this.method1077(this.statusText, true);
            }
         }
      }
   }

   private int method1025(ScreenHandler var1, int var2, String var3, String var4, int var5) {
      int var6 = -1;
      int var7 = -1;
      int var8 = -1;
      int var9 = Integer.MAX_VALUE;
      int var10 = -1;
      int var11 = -1;

      for (int var12 = 0; var12 < var2; var12++) {
         Slot var13 = var1.getSlot(var12);
         if (var13.hasStack() && this.method1073(var13.getStack(), var3, var4)) {
            int var14 = var13.getStack().getCount();
            if (var14 == var5) {
               return var12;
            }

            if (var14 < var5 && var14 > var11) {
               var11 = var14;
               var10 = var12;
            }

            if (var14 <= var5 && var14 > var7) {
               var7 = var14;
               var6 = var12;
            }

            if (var14 > var5 && var14 < var9) {
               var9 = var14;
               var8 = var12;
            }
         }
      }

      if (var6 != -1) {
         return var6;
      } else {
         return var8 != -1 ? var8 : var10;
      }
   }

   private void method1026(CraftRecipe var1, ScreenHandler var2) {
      this.statusText = "Складываю " + var1.method5400(this);
      int var3 = this.method1020(var2);
      if (this.type2 != AutoCraftModule.Type2.IDLE) {
         this.method1028(var2, var3);
      } else if (!var2.getCursorStack().isEmpty()) {
         this.type2 = AutoCraftModule.Type2.DEPOSIT_CURSOR;
         this.method1028(var2, var3);
      } else if (this.stopWatch.method7546(85L)) {
         int var4 = this.method1067(var1.getText4(), var1.getText5());
         if (var4 <= 0 && this.method1066() <= 1) {
            var4 = this.method1068(var1);
         }

         if (var4 <= 0) {
            this.method1034();
         } else {
            if (this.intValue5 == -1) {
               this.intValue5 = var4;
               this.intValue6 = 0;
            } else if (var4 < this.intValue5) {
               this.intValue6 = 0;
               this.intValue5 = var4;
            } else if (var4 == this.intValue5) {
               this.intValue6++;
            } else {
               this.intValue6 = 0;
               this.intValue5 = var4;
            }

            if (this.method1069(var1, var2, var3) && this.intValue6 < 3) {
               for (int var5 = var3; var5 < var2.slots.size(); var5++) {
                  Slot var6 = var2.getSlot(var5);
                  if (var6.hasStack() && var1.method5399(var6.getStack(), this)) {
                     this.method1027(var2, var5);
                     this.stopWatch.reset();
                     return;
                  }
               }

               if (this.method1066() <= 1) {
                  for (int var7 = var3; var7 < var2.slots.size(); var7++) {
                     Slot var8 = var2.getSlot(var7);
                     if (var8.hasStack() && !this.method1070(var1, var8.getStack())) {
                        this.method1027(var2, var7);
                        this.stopWatch.reset();
                        return;
                     }
                  }
               }

               this.method1034();
            } else {
               this.method1040(var2, this.blockPos4);
               this.method1033();
            }
         }
      }
   }

   private void method1027(ScreenHandler var1, int var2) {
      if (!var1.getCursorStack().isEmpty()) {
         this.type2 = AutoCraftModule.Type2.DEPOSIT_CURSOR;
      } else {
         Slot var3 = var1.getSlot(var2);
         if (var3.hasStack()) {
            this.itemStack = var3.getStack().copy();
            this.intValue8 = var2;
            this.intValue9 = 2;
            this.type2 = AutoCraftModule.Type2.QUICK_MOVE;
            mc.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.PICKUP, mc.player);
         }
      }
   }

   private void method1028(ScreenHandler var1, int var2) {
      if (this.type2 == AutoCraftModule.Type2.QUICK_MOVE) {
         int var4 = this.method1030(var1, var2);
         if (var4 != -1) {
            mc.interactionManager.clickSlot(var1.syncId, var4, 0, SlotActionType.QUICK_MOVE, mc.player);
         }

         this.intValue9--;
         if (this.intValue9 <= 0) {
            this.type2 = AutoCraftModule.Type2.DEPOSIT_CURSOR;
         }
      } else if (this.type2 == AutoCraftModule.Type2.DEPOSIT_CURSOR) {
         if (var1.getCursorStack().isEmpty()) {
            this.method1029(var1);
         } else {
            int var3 = this.method1031(var1, var2);
            if (var3 != -1) {
               mc.interactionManager.clickSlot(var1.syncId, var3, 0, SlotActionType.PICKUP, mc.player);
               if (var1.getCursorStack().isEmpty()) {
                  this.method1029(var1);
               }
            } else {
               this.method1040(var1, this.blockPos4);
               this.method1032(var1);
               if (!var1.getCursorStack().isEmpty()) {
                  this.statusText = "Нет места под hovered-предмет";
               } else {
                  this.method1033();
               }
            }
         }
      }
   }

   private void method1029(ScreenHandler var1) {
      this.method1040(var1, this.blockPos4);
      this.method1079();
      this.stopWatch.reset();
   }

   private int method1030(ScreenHandler var1, int var2) {
      if (this.itemStack.isEmpty()) {
         return -1;
      } else {
         for (int var3 = var2; var3 < var1.slots.size(); var3++) {
            if (var3 != this.intValue8) {
               Slot var4 = var1.getSlot(var3);
               if (var4.hasStack() && this.method1074(var4.getStack(), this.itemStack)) {
                  return var3;
               }
            }
         }

         return -1;
      }
   }

   private int method1031(ScreenHandler var1, int var2) {
      ItemStack var3 = var1.getCursorStack();
      if (var3.isEmpty()) {
         return -1;
      } else {
         for (int var4 = 0; var4 < var2; var4++) {
            Slot var5 = var1.getSlot(var4);
            if (var5.hasStack() && this.method1074(var5.getStack(), var3) && var5.getStack().getCount() < var5.getStack().getMaxCount()) {
               return var4;
            }
         }

         for (int var6 = 0; var6 < var2; var6++) {
            if (!var1.getSlot(var6).hasStack()) {
               return var6;
            }
         }

         return -1;
      }
   }

   private void method1032(ScreenHandler var1) {
      if (!var1.getCursorStack().isEmpty()) {
         if (this.intValue8 >= 0 && this.intValue8 < var1.slots.size()) {
            Slot var2 = var1.getSlot(this.intValue8);
            if (!var2.hasStack() || this.method1074(var2.getStack(), var1.getCursorStack())) {
               mc.interactionManager.clickSlot(var1.syncId, this.intValue8, 0, SlotActionType.PICKUP, mc.player);
               return;
            }
         }

         int var5 = this.method1020(var1);

         for (int var3 = var5; var3 < var1.slots.size(); var3++) {
            ItemStack var4 = var1.getSlot(var3).getStack();
            if (var4.isEmpty() || this.method1074(var4, var1.getCursorStack()) && var4.getCount() < var4.getMaxCount()) {
               mc.interactionManager.clickSlot(var1.syncId, var3, 0, SlotActionType.PICKUP, mc.player);
               return;
            }
         }
      }
   }

   private void method1033() {
      if (mc.player != null && mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
         mc.player.closeHandledScreen();
      }

      this.statusText = "Сундук заполнен, ищу другой склад";
      this.type = AutoCraftModule.Type.IDLE;
      this.blockPos4 = null;
      this.flag4 = true;
      this.stopWatch.reset();
      this.method1078();
      this.method1022();
   }

   private void method1034() {
      if (mc.player != null && mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
         mc.player.closeHandledScreen();
      }

      this.type = AutoCraftModule.Type.IDLE;
      this.blockPos4 = null;
      this.stopWatch.reset();
      this.method1078();
      this.method1022();
      this.flag4 = false;
   }

   private void method1035(CraftRecipe var1) {
      this.method1041(var1);
      this.method1042(var1);
      boolean var2 = this.method1066() <= 1;
      boolean var3 = this.flag4 && this.method1064(var1);
      boolean var4 = var2 && this.method1068(var1) > 0;
      boolean var5 = this.method1063(var1);
      if (this.autoStore.isEnabled() && (var3 || var5 || var4)) {
         var var6 = this.method1053(var1);
         if (!var6.isEmpty()) {
            this.blockPos4 = (BlockPos)var6.get();
            var1.method5407(Class1.method55(this.blockPos4));
            this.statusText = var2 ? "Инвентарь полон, складываю..." : "Открываю сундук-склад";
            if (this.method1054(this.blockPos4, AutoCraftModule.Type.OPENING_OUTPUT) == AutoCraftModule.Type3.FAILED) {
               this.items4.remove(this.blockPos4);
               this.blockPos4 = null;
            }

            return;
         }

         if (var2) {
            this.method1126("Автокрафт остановлен: нет сундука со свободным местом.");
            return;
         }

         this.flag4 = false;
         this.statusText = "Нет свободного склада, продолжаю крафт";
      }

      String var8 = this.method1065(var1);
      if (this.intValue7 <= 0 && !var8.isBlank() && !this.method1044(var1, 1)) {
         this.method1076("Нет места под ресурсы");
      } else if (!var8.isBlank()) {
         if (this.autoTake.isEnabled()) {
            if (!this.method1049(var1.method5395(var8), var1.method5396(var8))) {
               this.method1076("Нет места под ресурсы");
            } else {
               var var7 = this.method1052(var1, var8);
               if (var7.isPresent()) {
                  this.blockPos3 = (BlockPos)var7.get();
                  var1.getItems3().put(var8, Class1.method55(this.blockPos3));
                  this.statusText = "Открываю сундук с ресурсами";
                  this.text3 = var8;
                  if (this.method1054(this.blockPos3, AutoCraftModule.Type.OPENING_SOURCE) == AutoCraftModule.Type3.FAILED) {
                     this.items4.remove(this.blockPos3);
                     this.blockPos3 = null;
                     this.text3 = "";
                  }
               } else {
                  this.method1076("Не найден ресурс: " + this.method1108(var8));
               }
            }
         } else {
            this.method1076("Не хватает ингредиентов");
         }
      } else if (this.blockPos != null) {
         var1.method5408(Class1.method55(this.blockPos));
         this.statusText = "Открываю верстак";
         if (this.method1054(this.blockPos, AutoCraftModule.Type.OPENING_WORKBENCH) == AutoCraftModule.Type3.FAILED) {
            this.blockPos = null;
         }
      } else {
         this.method1076("Рядом нет доступного верстака");
      }
   }

   private void method1036() {
      this.flag2 = false;
      this.flag3 = false;
      this.items5.clear();
      this.items4.clear();
      this.items6.clear();
      this.intValue7 = 0;
      this.blockPos2 = null;
      this.blockPos3 = null;
      this.blockPos4 = null;
      this.blockPos = null;
      this.flag4 = false;
      this.type = AutoCraftModule.Type.SCANNING;
      this.method1078();
      this.method1058();
      this.method1022();
   }

   private void method1037(CraftRecipe var1) {
      if (!this.flag3) {
         this.method1038();
      }

      if (this.items5.isEmpty()) {
         this.flag2 = true;
         this.type = AutoCraftModule.Type.IDLE;
         this.method1041(var1);
         this.statusText = this.items4.isEmpty() ? "Сундуки в зоне досягаемости не найдены" : "Анализ сундуков завершен";
      } else {
         this.blockPos2 = this.items5.getFirst();
         this.statusText = "Анализирую сундук " + this.method1062(this.blockPos2);
         AutoCraftModule.Type3 var2 = this.method1054(this.blockPos2, AutoCraftModule.Type.OPENING_ANALYZE);
         if (var2 == AutoCraftModule.Type3.OPENED || var2 == AutoCraftModule.Type3.FAILED) {
            this.items5.removeFirst();
            if (var2 == AutoCraftModule.Type3.FAILED) {
               this.blockPos2 = null;
               this.stopWatch.reset();
            }
         }
      }
   }

   private void method1038() {
      this.items4.clear();
      this.items5.clear();
      this.blockPos = null;
      this.flag3 = true;
      double var1 = this.method1061();
      BlockPos var3 = mc.player.getBlockPos();
      int var4 = (int)Math.ceil(var1);
      ArrayList var5 = new ArrayList();
      HashSet var6 = new HashSet();
      BlockPos.stream(var3.add(-var4, -var4, -var4), var3.add(var4, var4, var4))
         .map(BlockPos::toImmutable)
         .forEach(
            var3x -> {
               BlockState var4x = mc.world.getBlockState(var3x);
               if (var4x.getBlock() == Blocks.CRAFTING_TABLE
                  && this.method1056(var3x) != null
                  && (
                     this.blockPos == null
                        || Vec3d.ofCenter(var3x).squaredDistanceTo(mc.player.getEyePos())
                           < Vec3d.ofCenter(this.blockPos).squaredDistanceTo(mc.player.getEyePos())
                  )) {
                  this.blockPos = var3x;
               }

               if (this.method1060(var3x) && this.method1056(var3x) != null && var6.add(this.method1039(var3x, var4x))) {
                  var5.add(var3x);
               }
            }
         );
      var5.sort(Comparator.comparingDouble((net.minecraft.util.math.Vec3i var0) -> Vec3d.ofCenter(var0).squaredDistanceTo(mc.player.getEyePos())));
      this.items5.addAll(var5);
   }

   private BlockPos method1039(BlockPos var1, BlockState var2) {
      if (var2.contains(ChestBlock.CHEST_TYPE) && var2.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
         BlockPos var3 = var1.offset(ChestBlock.getFacing(var2));
         return var3.asLong() < var1.asLong() ? var3.toImmutable() : var1.toImmutable();
      } else {
         return var1.toImmutable();
      }
   }

   private void method1040(ScreenHandler var1, BlockPos var2) {
      if (var2 != null) {
         int var3 = this.method1020(var1);
         AutoCraftModule.Entry var4 = new AutoCraftModule.Entry(var2.toImmutable());

         for (int var5 = 0; var5 < var3; var5++) {
            ItemStack var6 = var1.getSlot(var5).getStack();
            if (var6.isEmpty()) {
               var4.intValue++;
            } else {
               var4.method1132(new AutoCraftModule.Data(var5, method1116(var6.getItem()), this.method1090(var6), var6.getCount(), var6.getMaxCount()));
            }
         }

         this.items4.put(var2.toImmutable(), var4);
      }
   }

   private void method1041(CraftRecipe var1) {
      if (var1 != null) {
         if (this.blockPos != null) {
            var1.method5408(Class1.method55(this.blockPos));
         }

         for (String var3 : var1.method5388().keySet()) {
            this.method1052(var1, var3).ifPresent(var2 -> var1.getItems3().put(var3, Class1.method55(var2)));
         }

         this.method1053(var1).ifPresent(var1x -> var1.method5407(Class1.method55(var1x)));
      }
   }

   private void method1042(CraftRecipe var1) {
      this.items6.clear();
      this.intValue7 = this.method1043(var1);
      int var2 = Math.max(1, this.intValue7);

      for (Map.Entry var4 : var1.method5388().entrySet()) {
         int var5 = (Integer)var4.getValue() * var2;
         this.items6.put((String)var4.getKey(), var5);
      }
   }

   private int method1043(CraftRecipe var1) {
      int var2 = Integer.MAX_VALUE;

      for (Map.Entry var4 : var1.method5388().entrySet()) {
         String var5 = (String)var4.getKey();
         int var6 = this.method1067(var1.method5395(var5), var1.method5396(var5)) + this.method1051(var1.method5395(var5), var1.method5396(var5));
         var2 = Math.min(var2, var6 / Math.max(1, (Integer)var4.getValue()));
      }

      if (var2 != Integer.MAX_VALUE && var2 > 0) {
         if (!this.batch.isEnabled()) {
            return 1;
         } else {
            int var7 = 1;
            int var8 = var2;
            int var9 = 0;

            while (var7 <= var8) {
               int var10 = var7 + (var8 - var7) / 2;
               if (this.method1044(var1, var10)) {
                  var9 = var10;
                  var7 = var10 + 1;
               } else {
                  var8 = var10 - 1;
               }
            }

            return var9;
         }
      } else {
         return 0;
      }
   }

   private boolean method1044(CraftRecipe var1, int var2) {
      int var3 = 0;
      int var4 = this.method1066();

      for (Map.Entry var6 : var1.method5388().entrySet()) {
         String var7 = (String)var6.getKey();
         String var8 = var1.method5395(var7);
         String var9 = var1.method5396(var7);
         int var10 = (Integer)var6.getValue() * var2;
         int var11 = this.method1067(var8, var9);
         int var12 = Math.max(0, var10 - var11);
         if (var12 != 0) {
            int var13 = this.method1050(var8, var9);
            int var14 = Math.max(0, var12 - var13);
            if (var14 > 0) {
               int var15 = Math.max(1, this.method1129(var8));
               var3 += (var14 + var15 - 1) / var15;
               if (var3 > var4) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private boolean method1045() {
      return this.batch.isEnabled();
   }

   private boolean method1046(CraftRecipe var1) {
      return !this.autoStore.isEnabled() ? false : !this.method1047(var1) || this.method1063(var1) || !this.method1048(var1);
   }

   private boolean method1047(CraftRecipe var1) {
      for (Map.Entry var3 : var1.method5388().entrySet()) {
         String var4 = (String)var3.getKey();
         if (this.method1067(var1.method5395(var4), var1.method5396(var4)) < (Integer)var3.getValue()) {
            return false;
         }
      }

      return true;
   }

   private boolean method1048(CraftRecipe var1) {
      return var1.getText4().isBlank() ? false : this.method1050(var1.getText4(), var1.getText5()) > 0 || this.method1066() > 0;
   }

   private boolean method1049(String var1, String var2) {
      return this.method1050(var1, var2) > 0 || this.method1066() > 0;
   }

   private int method1050(String var1, String var2) {
      int var3 = 0;

      for (int var4 = 0; var4 < 36; var4++) {
         ItemStack var5 = mc.player.getInventory().getStack(var4);
         if (!var5.isEmpty() && this.method1073(var5, var1, var2)) {
            var3 += Math.max(0, var5.getMaxCount() - var5.getCount());
         }
      }

      return var3;
   }

   private int method1051(String var1, String var2) {
      int var3 = 0;

      for (AutoCraftModule.Entry var5 : this.items4.values()) {
         var3 += var5.method1133(var1, var2);
      }

      return var3;
   }

   private Optional<BlockPos> method1052(CraftRecipe var1, String var2) {
      String var3 = var1.method5395(var2);
      String var4 = var1.method5396(var2);
      return this.items4
         .values()
         .stream()
         .filter(var2x -> var2x.method1133(var3, var4) > 0)
         .min(
            Comparator.<AutoCraftModule.Entry>comparingInt(var2x -> var2x.method1133(var3, var4))
               .reversed()
               .thenComparingDouble(var0 -> Vec3d.ofCenter(var0.blockPos).squaredDistanceTo(mc.player.getEyePos()))
         )
         .map(var0 -> var0.blockPos);
   }

   private Optional<BlockPos> method1053(CraftRecipe var1) {
      String var2 = var1.getText4();
      String var3 = var1.getText5();
      return this.items4
         .values()
         .stream()
         .filter(var2x -> var2x.method1134(var2, var3))
         .min(Comparator.comparingDouble(var0 -> Vec3d.ofCenter(var0.blockPos).squaredDistanceTo(mc.player.getEyePos())))
         .map(var0 -> var0.blockPos);
   }

   private AutoCraftModule.Type3 method1054(BlockPos var1, AutoCraftModule.Type var2) {
      if (var1 != null && this.stopWatch.method7546(85L)) {
         BlockHitResult var3 = this.method1056(var1);
         if (var3 == null) {
            this.statusText = "Нет луча до блока " + this.method1062(var1);
            this.method1058();
            return AutoCraftModule.Type3.FAILED;
         } else {
            this.rotation = RotationUtils.method6913(var3.getPos());
            if (!this.method1055(var1)) {
               this.statusText = "Поворачиваюсь к " + this.method1062(var1);
               return AutoCraftModule.Type3.WAITING;
            } else {
               UseItemUtils.method6514(var3, Hand.MAIN_HAND);
               this.type = var2;
               this.intValue = 30;
               this.stopWatch.reset();
               this.method1022();
               this.method1058();
               return AutoCraftModule.Type3.OPENED;
            }
         }
      } else {
         return AutoCraftModule.Type3.WAITING;
      }
   }

   private boolean method1055(BlockPos var1) {
      Rotation var2 = rotationManager.method7844();
      BlockHitResult var3 = TargetUtils.method3216(
         mc.player.getCameraPosVec(1.0F), var2, this.method1061(), var1x -> var1x != null && var1x.getBlockPos().equals(var1)
      );
      return var3 != null && var3.getType() != HitResult.Type.MISS && var3.getBlockPos().equals(var1);
   }

   private BlockHitResult method1056(BlockPos var1) {
      Vec3d var2 = Vec3d.ofCenter(var1);
      ArrayList<Vec3d> var3 = new ArrayList();
      var3.add(var2);

      for (Direction var7 : Direction.values()) {
         var3.add(var2.add(Vec3d.of(var7.getVector()).multiply(0.5)));
      }

      Vec3d var11 = mc.player.getCameraPosVec(1.0F);
      double var12 = this.method1061();

      for (Object var8_o : (Iterable<?>)(Object)(var3)) {
         Vec3d var8 = (Vec3d)var8_o;
         Rotation var9 = RotationUtils.method6913(var8);
         BlockHitResult var10 = TargetUtils.method3216(var11, var9, var12, var1x -> var1x != null && var1x.getBlockPos().equals(var1));
         if (var10 != null && var10.getType() != HitResult.Type.MISS && var10.getBlockPos().equals(var1)) {
            return var10;
         }
      }

      return null;
   }

   private void method1057() {
      this.method1058();
      this.method1022();
      if (this.type == AutoCraftModule.Type.OPENING_ANALYZE) {
         this.blockPos2 = null;
         this.type = AutoCraftModule.Type.SCANNING;
      } else if (this.type == AutoCraftModule.Type.OPENING_SOURCE) {
         this.method1076("Не удалось открыть сундук с ресурсами");
      } else if (this.type == AutoCraftModule.Type.OPENING_OUTPUT) {
         this.method1076("Не удалось открыть сундук-склад");
      } else {
         if (this.type == AutoCraftModule.Type.OPENING_WORKBENCH) {
            this.method1076("Не удалось открыть верстак");
         }
      }
   }

   private void method1058() {
      this.rotation = null;
   }

   private boolean method1059() {
      return this.type == AutoCraftModule.Type.OPENING_ANALYZE
         || this.type == AutoCraftModule.Type.OPENING_SOURCE
         || this.type == AutoCraftModule.Type.OPENING_OUTPUT
         || this.type == AutoCraftModule.Type.OPENING_WORKBENCH;
   }

   private boolean method1060(BlockPos var1) {
      BlockEntity var2 = mc.world.getBlockEntity(var1);
      return var2 instanceof ChestBlockEntity || var2 instanceof BarrelBlockEntity || var2 instanceof ShulkerBoxBlockEntity;
   }

   private double method1061() {
      return mc.player.getBlockInteractionRange() + 0.15;
   }

   private String method1062(BlockPos var1) {
      return var1 == null ? "?" : var1.getX() + " " + var1.getY() + " " + var1.getZ();
   }

   private boolean method1063(CraftRecipe var1) {
      if (var1.getText4().isBlank()) {
         return false;
      } else {
         int var2 = 0;

         for (int var3 = 0; var3 < 36; var3++) {
            ItemStack var4 = mc.player.getInventory().getStack(var3);
            if (!var4.isEmpty() && var1.method5399(var4, this)) {
               var2++;
            }
         }

         return var2 / 36.0F >= 0.5F;
      }
   }

   private boolean method1064(CraftRecipe var1) {
      return !var1.getText4().isBlank() && this.method1067(var1.getText4(), var1.getText5()) > 0;
   }

   private String method1065(CraftRecipe var1) {
      for (Map.Entry var3 : var1.method5388().entrySet()) {
         String var4 = (String)var3.getKey();
         int var5 = this.items6.getOrDefault(var4, (Integer)var3.getValue());
         int var6 = this.method1067(var1.method5395(var4), var1.method5396(var4));
         if (var6 < var5) {
            return var4;
         }
      }

      return "";
   }

   private int method1066() {
      int var1 = 0;

      for (int var2 = 0; var2 < 36; var2++) {
         if (mc.player.getInventory().getStack(var2).isEmpty()) {
            var1++;
         }
      }

      return var1;
   }

   private int method1067(String var1, String var2) {
      int var3 = 0;

      for (int var4 = 0; var4 < 36; var4++) {
         ItemStack var5 = mc.player.getInventory().getStack(var4);
         if (!var5.isEmpty() && this.method1073(var5, var1, var2)) {
            var3 += var5.getCount();
         }
      }

      return var3;
   }

   private int method1068(CraftRecipe var1) {
      int var2 = 0;

      for (int var3 = 0; var3 < 36; var3++) {
         ItemStack var4 = mc.player.getInventory().getStack(var3);
         if (!var4.isEmpty() && !this.method1070(var1, var4)) {
            var2 += var4.getCount();
         }
      }

      return var2;
   }

   private boolean method1069(CraftRecipe var1, ScreenHandler var2, int var3) {
      String var4 = var1.getText4();
      String var5 = var1.getText5();

      for (int var6 = 0; var6 < var3; var6++) {
         Slot var7 = var2.getSlot(var6);
         if (!var7.hasStack()) {
            return true;
         }

         ItemStack var8 = var7.getStack();
         if (this.method1073(var8, var4, var5) && var8.getCount() < var8.getMaxCount()) {
            return true;
         }
      }

      return false;
   }

   private boolean method1070(CraftRecipe var1, ItemStack var2) {
      for (Map.Entry var4 : var1.method5388().entrySet()) {
         String var5 = (String)var4.getKey();
         if (this.method1073(var2, var1.method5395(var5), var1.method5396(var5))) {
            return true;
         }
      }

      return false;
   }

   private boolean method1071(Item var1, String var2) {
      return Objects.equals(method1072(var1), var2);
   }

   private static String method1072(Item var0) {
      return items.computeIfAbsent(var0, var0x -> Registries.ITEM.getId(var0x).toString());
   }

   public boolean method1073(ItemStack var1, String var2, String var3) {
      if (var1 == null || var1.isEmpty() || !this.method1071(var1.getItem(), var2)) {
         return false;
      } else if (var3 != null && !var3.isBlank()) {
         String var4 = var1.getName().getString();
         return var4.equalsIgnoreCase(var3) || var4.toLowerCase(Locale.ROOT).contains(var3.toLowerCase(Locale.ROOT));
      } else {
         return true;
      }
   }

   private boolean method1074(ItemStack var1, ItemStack var2) {
      if (var1 != null && var2 != null && !var1.isEmpty() && !var2.isEmpty()) {
         ItemStack var3 = var1.copy();
         ItemStack var4 = var2.copy();
         var3.setCount(1);
         var4.setCount(1);
         return ItemStack.areItemsEqual(var3, var4) && ItemStack.areEqual(var3, var4);
      } else {
         return false;
      }
   }

   private void method1075(ScreenHandler var1) {
      ItemStack var2 = var1.getCursorStack();
      if (!var2.isEmpty()) {
         for (Slot var4 : var1.slots) {
            if (var4.inventory instanceof PlayerInventory && var4.getIndex() < 36 && var4.getStack().isEmpty()) {
               mc.interactionManager.clickSlot(var1.syncId, var4.id, 0, SlotActionType.PICKUP, mc.player);
               return;
            }
         }

         mc.interactionManager.clickSlot(var1.syncId, 64537, 0, SlotActionType.PICKUP, mc.player);
      }
   }

   private void method1076(String var1) {
      this.statusText = var1;
      this.text3 = "";
      this.type = AutoCraftModule.Type.IDLE;
      this.autoCraftState.method744();
      this.method1018();
      this.method1058();
      this.method1022();
   }

   private void method1077(String var1, boolean var2) {
      if (mc.player != null && mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
         mc.player.closeHandledScreen();
      }

      this.blockPos3 = null;
      this.method1076(var1);
      if (var2) {
         this.stopWatch.reset();
      }
   }

   private void method1078() {
      this.intValue5 = -1;
      this.intValue6 = 0;
      this.method1079();
   }

   private void method1079() {
      this.type2 = AutoCraftModule.Type2.IDLE;
      this.itemStack = ItemStack.EMPTY;
      this.intValue8 = -1;
      this.intValue9 = 0;
   }

   private void method1080(DrawEvent var1) {
      if (this.isEnabled() || this.craftBindingController.isBinding() || this.craftPresetSaver.isActive()) {
         String var2;
         if (this.craftBindingController.isBinding()) {
            var2 = this.craftBindingController.method7223();
         } else if (this.craftPresetSaver.isActive()) {
            var2 = this.craftPresetSaver.method3189();
         } else {
            var2 = this.statusText;
         }

         if (var2 != null && !var2.isBlank()) {
            Font var3 = Fonts.NEW_REGULAR.getFont(8.0F);
            float var4 = var3.width(var2) + 14.0F;
            float var5 = mc.getWindow().getScaledWidth() / 2.0F - var4 / 2.0F;
            float var6 = mc.getWindow().getScaledHeight() - 54;
            ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
            ZenithColor var8 = var7 == null ? new ZenithColor(14, 14, 16, 160) : var7.getPanelLeftBackground().getZenithColor().method7293(160);
            var1.getCustomDrawContext().drawRoundedRect(var5, var6, var4, 16.0F, ProxyDotRadiusData.method8069(5.0F), var8);
            float var9 = var6 + (16.0F - var3.height()) / 2.0F - 1.0F;
            var1.getCustomDrawContext().drawText(var3, var2, var5 + var4 / 2.0F - var3.width(var2) / 2.0F, var9, ZenithColor.INSTANCE);
         }
      }
   }

   private void method1081(boolean var1) {
      if (mc.player != null && mc.world != null) {
         long var2 = System.currentTimeMillis();
         if (var1 || var2 - this.longValue >= 600L) {
            this.longValue = var2;
            ClientRecipeBook var4 = mc.player.getRecipeBook();
            List<RecipeResultCollection> var5 = var4.getOrderedResults();
            DynamicRegistryManager var6 = mc.world.getRegistryManager();
            Language var7 = Language.getInstance();
            if (!var1 && !this.flag && var5 == this.items7 && var6 == this.dynamicRegistryManager && var7 == this.language) {
               this.method1091();
            } else {
               this.items7 = var5;
               this.dynamicRegistryManager = var6;
               this.language = var7;
               this.flag = false;
               ContextParameterMap var8 = SlotDisplayContexts.createParameters(mc.world);
               HashSet var9 = new HashSet();
               HashMap var10 = new HashMap();
               this.items3.clear();

               for (Object var12_o : (Iterable<?>)(Object)(var5)) {
                  RecipeResultCollection var12 = (RecipeResultCollection)var12_o;
                  for (RecipeDisplayEntry var14 : var12.getAllRecipes()) {
                     RecipeDisplay var15 = var14.display();
                     if (this.method1088(var15)) {
                        ItemStack var16 = var15.result().getFirst(var8);
                        if (!var16.isEmpty() && this.method1089(var16)) {
                           AutoCraftModule.Data3 var17 = this.method1082(var14, var16, var8);
                           String var18 = var17.getText();
                           int var19 = (Integer)var10.merge(var18, 1, (a, b) -> (Integer)a + (Integer)b);
                           if (var19 > 1) {
                              var18 = var18 + "_" + var19;
                           }

                           CraftRecipe var20 = this.method1095(var18);
                           if (var20 == null) {
                              var20 = new CraftRecipe(var18, "servers", var17.getText2());
                              this.items2.add(var20);
                           }

                           this.method1086(var20, var17);
                           this.items3.put(var18, var14);
                           var9.add(var18);
                        }
                     }
                  }
               }

               if (!var9.isEmpty()) {
                  this.items2.removeIf(var1x -> var1x.isFlag2() && !var9.contains(var1x.getId()));
               }

               this.method1091();
            }
         }
      }
   }

   private AutoCraftModule.Data3 method1082(RecipeDisplayEntry var1, ItemStack var2, ContextParameterMap var3) {
      String var4 = method1116(var2.getItem());
      String var5 = this.method1090(var2);
      String var6 = this.method1107(var4, var5);
      String[] var7 = new String[9];
      String[] var8 = new String[9];

      for (int var9 = 0; var9 < 9; var9++) {
         var7[var9] = "";
         var8[var9] = "";
      }

      RecipeDisplay var12 = var1.display();
      if (var12 instanceof ShapedCraftingRecipeDisplay var10) {
         this.method1083(var10, var3, var7, var8);
      } else if (var12 instanceof ShapelessCraftingRecipeDisplay var11) {
         this.method1084(var11, var3, var7, var8);
      }

      String var13 = this.method1087(var4, var5, var7, var8);
      return new AutoCraftModule.Data3(var13, var6, var4, var5, var7, var8);
   }

   private void method1083(ShapedCraftingRecipeDisplay var1, ContextParameterMap var2, String[] var3, String[] var4) {
      var var5 = var1.ingredients();
      int var6 = Math.min(3, var1.width());
      int var7 = Math.min(3, var1.height());

      for (int var8 = 0; var8 < var7; var8++) {
         for (int var9 = 0; var9 < var6; var9++) {
            int var10 = var8 * var1.width() + var9;
            int var11 = var8 * 3 + var9;
            if (var10 < var5.size()) {
               this.method1085((SlotDisplay)var5.get(var10), var2, var3, var4, var11);
            }
         }
      }
   }

   private void method1084(ShapelessCraftingRecipeDisplay var1, ContextParameterMap var2, String[] var3, String[] var4) {
      var var5 = var1.ingredients();

      for (int var6 = 0; var6 < Math.min(9, var5.size()); var6++) {
         this.method1085((SlotDisplay)var5.get(var6), var2, var3, var4, var6);
      }
   }

   private void method1085(SlotDisplay var1, ContextParameterMap var2, String[] var3, String[] var4, int var5) {
      ItemStack var6 = var1.getFirst(var2);
      if (!var6.isEmpty()) {
         var3[var5] = method1116(var6.getItem());
         var4[var5] = this.method1090(var6);
      }
   }

   private void method1086(CraftRecipe var1, AutoCraftModule.Data3 var2) {
      var1.setFlag2(true);
      var1.setLocked(true);
      var1.method5402(var2.getText2());
      var1.method5403(var2.getText3());
      var1.method5404(var2.getText4());
      var1.method5405(var2.getText3());
      var1.setFlag(false);

      for (int var3 = 0; var3 < 9; var3++) {
         var1.method5390(var3, var2.getTexts()[var3]);
         var1.method5392(var3, var2.getTexts2()[var3]);
      }
   }

   private String method1087(String var1, String var2, String[] var3, String[] var4) {
      StringBuilder var5 = new StringBuilder("servers").append('|').append(var1).append('|').append(var2);

      for (int var6 = 0; var6 < 9; var6++) {
         var5.append('|').append(var3[var6]).append('#').append(var4[var6]);
      }

      String var9 = var1;
      int var7 = var1.indexOf(58);
      if (var7 != -1 && var7 + 1 < var1.length()) {
         var9 = var1.substring(var7 + 1);
      }

      String var8 = (var2 != null && !var2.isBlank() ? var2 : var9).toLowerCase(Locale.ROOT).replace(" ", "_");
      var8 = pattern.matcher(var8).replaceAll("_");
      if (var8.isBlank()) {
         var8 = "recipe";
      }

      return "servers_" + var8 + "_" + Integer.toHexString(var5.toString().hashCode());
   }

   private boolean method1088(RecipeDisplay var1) {
      return var1 instanceof ShapedCraftingRecipeDisplay || var1 instanceof ShapelessCraftingRecipeDisplay;
   }

   private boolean method1089(ItemStack var1) {
      if (var1.isEmpty()) {
         return false;
      } else {
         Identifier var2 = Registries.ITEM.getId(var1.getItem());
         if (!"minecraft".equals(var2.getNamespace())) {
            return true;
         } else {
            String var3 = var1.getItem().getName().getString();
            String var4 = var1.getName().getString();
            return !var4.equals(var3)
               || var1.contains(DataComponentTypes.CUSTOM_NAME)
               || var1.contains(DataComponentTypes.LORE)
               || var1.contains(DataComponentTypes.CUSTOM_DATA)
               || var1.contains(DataComponentTypes.CUSTOM_MODEL_DATA)
               || var1.contains(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
         }
      }
   }

   private String method1090(ItemStack var1) {
      if (var1 != null && !var1.isEmpty()) {
         String var2 = var1.getItem().getName().getString();
         String var3 = var1.getName().getString();
         return var3.equals(var2) ? "" : var3;
      } else {
         return "";
      }
   }

   public void method1091() {
      if (this.method1095(this.text2) == null) {
         var var1 = this.method1093();
         if (!var1.isEmpty()) {
            this.text2 = ((CraftRecipe)var1.getFirst()).getId();
         }
      }
   }

   public List<CraftRecipe> method1092(String var1) {
      this.method1081(false);
      return this.method1093();
   }

   private List<CraftRecipe> method1093() {
      return this.items2.stream().filter(var0 -> var0.getText2().equalsIgnoreCase("servers")).sorted(Comparator.comparing(CraftRecipe::getText3)).toList();
   }

   public CraftRecipe method1094(String var1, String var2) {
      this.method1081(false);
      return this.method1095(var2);
   }

   private CraftRecipe method1095(String var1) {
      return var1 != null && !var1.isBlank()
         ? this.items2.stream().filter(var1x -> var1x.getText2().equalsIgnoreCase("servers") && var1x.getId().equalsIgnoreCase(var1)).findFirst().orElse(null)
         : null;
   }

   public CraftRecipe method1096() {
      return this.method1094("servers", this.getText2());
   }

   public CraftRecipe method1097() {
      this.method1091();
      return this.method1095(this.getText2());
   }

   public String method1098() {
      return "servers";
   }

   public void method1099(String var1) {
      this.autoCraftState.reset();
      this.method1078();
      this.method1018();
   }

   public String getText2() {
      return this.text2;
   }

   public void method1100(String var1) {
      String var2 = var1 == null ? "" : var1;
      if (!this.text2.equals(var2)) {
         this.text2 = var2;
         this.autoCraftState.reset();
         this.method1078();
         this.method1018();
         this.method1036();
      }
   }

   public CraftRecipe method1101(String var1) {
      this.method1128("Ручные пресеты отключены: рецепты берутся из книги");
      return null;
   }

   public boolean method1102(CraftRecipe var1) {
      return false;
   }

   public boolean method1103(String var1) {
      return false;
   }

   public void method1104(CraftRecipe var1) {
      if (var1 != null) {
         this.items2.add(var1);
         this.flag = true;
      }
   }

   public void method1105(String var1) {
      this.craftBindingController.method7225(var1);
   }

   public void method1106() {
      this.autoCraftState.reset();
   }

   public String method1107(String var1, String var2) {
      return var2 != null && !var2.isBlank() ? var2 : this.method1118(var1);
   }

   public String method1108(String var1) {
      int var2 = var1.indexOf(35);
      return var2 == -1 ? this.method1118(var1) : this.method1107(var1.substring(0, var2), var1.substring(var2 + 1));
   }

   public void method1109() {
      this.craftBindingController.method7226();
   }

   public void method1110() {
      this.craftBindingController.method7227();
   }

   public void method1111() {
      this.method1128("Ручное добавление отключено: используйте список рецептов");
   }

   public void method1112() {
      this.craftBindingController.method7224();
   }

   public void method1113() {
      this.craftBindingController.method7229();
   }

   public void method1114() {
      this.craftBindingController.method7230();
   }

   public void method1115(String var1, String var2) {
      this.text2 = var2 == null ? "" : var2;
   }

   private static String method1116(Item var0) {
      return var0 != null && var0 != Items.AIR ? method1072(var0) : "";
   }

   public Item method1117(String var1) {
      if (var1 != null && !var1.isBlank()) {
         Identifier var2 = Identifier.tryParse(var1);
         return var2 != null && Registries.ITEM.containsId(var2) ? Registries.ITEM.get(var2) : Items.AIR;
      } else {
         return Items.AIR;
      }
   }

   public String method1118(String var1) {
      Item var2 = this.method1117(var1);
      return var2 == Items.AIR ? "Пусто" : var2.getName().getString();
   }

   public BooleanSetting getAutoTake() {
      return this.autoTake;
   }

   public BooleanSetting getAutoStore() {
      return this.autoStore;
   }

   public BooleanSetting getBatch() {
      return this.batch;
   }

   public String method1119() {
      return this.statusText == null ? "" : this.statusText;
   }

   public boolean isBinding() {
      return this.craftBindingController.isBinding();
   }

   public AutoCraftModule.Data2 method1120() {
      if (mc.player != null && mc.world != null) {
         double var1 = this.method1061();
         double var3 = var1 * var1;
         Vec3d var5 = mc.player.getEyePos();
         BlockPos var6 = mc.player.getBlockPos();
         int var7 = (int)Math.ceil(var1);
         Mutable var8 = new Mutable();
         HashSet var9 = new HashSet();
         BlockPos var10 = null;
         double var11 = Double.MAX_VALUE;

         for (int var13 = var6.getX() - var7; var13 <= var6.getX() + var7; var13++) {
            for (int var14 = var6.getY() - var7; var14 <= var6.getY() + var7; var14++) {
               for (int var15 = var6.getZ() - var7; var15 <= var6.getZ() + var7; var15++) {
                  double var16 = var13 + 0.5 - var5.x;
                  double var18 = var14 + 0.5 - var5.y;
                  double var20 = var15 + 0.5 - var5.z;
                  double var22 = var16 * var16 + var18 * var18 + var20 * var20;
                  if (!(var22 > var3)) {
                     var8.set(var13, var14, var15);
                     BlockState var24 = mc.world.getBlockState(var8);
                     if (var24.getBlock() == Blocks.CRAFTING_TABLE) {
                        if (var22 < var11) {
                           var11 = var22;
                           var10 = var8.toImmutable();
                        }
                     } else if (var24.hasBlockEntity() && this.method1060(var8)) {
                        var9.add(this.method1039(var8, var24));
                     }
                  }
               }
            }
         }

         return new AutoCraftModule.Data2(var10, var9.size());
      } else {
         return new AutoCraftModule.Data2(null, 0);
      }
   }

   public int method1121(String var1, String var2) {
      return mc.player != null && var1 != null && !var1.isBlank() ? this.method1067(var1, var2) : 0;
   }

   public Map<String, Integer> method1122() {
      HashMap var1 = new HashMap();
      if (mc.player == null) {
         return var1;
      } else {
         for (int var2 = 0; var2 < 36; var2++) {
            ItemStack var3 = mc.player.getInventory().getStack(var2);
            if (var3 != null && !var3.isEmpty()) {
               var1.merge(method1116(var3.getItem()), var3.getCount(), (a, b) -> (Integer)a + (Integer)b);
            }
         }

         return var1;
      }
   }

   @Override
   public JsonObject save() {
      JsonObject var1 = super.save();
      var1.add("AutoCraftData", this.method1123());
      return var1;
   }

   @Override
   public void load(JsonObject var1) {
      super.load(var1);
      if (var1 == null) {
         this.method1091();
      } else {
         JsonObject var2 = null;
         if (var1.has("AutoCraftData") && var1.get("AutoCraftData").isJsonObject()) {
            var2 = var1.getAsJsonObject("AutoCraftData");
         } else if (var1.has("presets") || var1.has("selectedProfile")) {
            var2 = var1;
         }

         if (var2 != null) {
            this.method1124(var2);
         } else {
            this.method1091();
         }
      }
   }

   public JsonObject method1123() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("selectedProfile", "servers");
      var1.addProperty("selectedServersPreset", this.text2);
      var1.addProperty("batch", this.batch.isEnabled());
      JsonArray var2 = new JsonArray();

      for (CraftRecipe var4 : new ArrayList<>(this.items2)) {
         if (this.method1125(var4)) {
            JsonObject var5 = new JsonObject();
            var5.addProperty("id", var4.getId());
            var5.addProperty("profile", "servers");
            JsonObject var6 = new JsonObject();

            for (Map.Entry var8 : var4.getItems3().entrySet()) {
               var6.add((String)var8.getKey(), ((Class1)var8.getValue()).toJson());
            }

            var5.add("sources", var6);
            var5.add("output", var4.getClass1().toJson());
            var5.add("workbench", var4.getClass12().toJson());
            var2.add(var5);
         }
      }

      var1.add("presets", var2);
      return var1;
   }

   public void method1124(JsonObject var1) {
      this.flag = true;
      if (var1.has("selectedServersPreset")) {
         this.text2 = var1.get("selectedServersPreset").getAsString();
      } else if (var1.has("selectedFunTimePreset")) {
         this.text2 = var1.get("selectedFunTimePreset").getAsString();
      }

      if (var1.has("batch")) {
         this.batch.setEnabled(var1.get("batch").getAsBoolean());
      }

      if (var1.has("presets") && var1.get("presets").isJsonArray()) {
         for (JsonElement var3 : var1.getAsJsonArray("presets")) {
            if (var3.isJsonObject()) {
               JsonObject var4 = var3.getAsJsonObject();
               String var5 = var4.has("id") ? var4.get("id").getAsString() : "";
               if (!var5.isBlank()) {
                  CraftRecipe var6 = this.method1095(var5);
                  if (var6 == null) {
                     var6 = new CraftRecipe(var5, "servers", var5);
                     var6.setFlag2(true);
                     var6.setLocked(true);
                     this.items2.add(var6);
                  }

                  if (var4.has("sources") && var4.get("sources").isJsonObject()) {
                     JsonObject var7 = var4.getAsJsonObject("sources");
                     var6.getItems3().clear();

                     for (Map.Entry var9 : var7.entrySet()) {
                        var6.getItems3().put((String)var9.getKey(), Class1.method58(((JsonElement)var9.getValue()).getAsJsonObject()));
                     }
                  }

                  if (var4.has("output") && var4.get("output").isJsonObject()) {
                     var6.method5407(Class1.method58(var4.getAsJsonObject("output")));
                  }

                  if (var4.has("workbench") && var4.get("workbench").isJsonObject()) {
                     var6.method5408(Class1.method58(var4.getAsJsonObject("workbench")));
                  }
               }
            }
         }
      }

      this.method1091();
   }

   private boolean method1125(CraftRecipe var1) {
      return var1 != null && (!var1.getItems3().isEmpty() || var1.getClass1().method56() || var1.getClass12().method56());
   }

   private void method1126(String var1) {
      this.statusText = var1;
      this.method1128(var1);
      if (mc.player != null && mc.player.currentScreenHandler != mc.player.playerScreenHandler) {
         mc.player.closeHandledScreen();
      }

      if (this.isEnabledRaw()) {
         this.setToggled(false);
      }
   }

   public void method1127(String var1) {
      Zenith.getInstance().getHudHolder().method6972("S", Text.literal(var1));
   }

   public void method1128(String var1) {
      Zenith.getInstance().getHudHolder().method6972("!", Text.literal(var1));
   }

   private int method1129(String var1) {
      Item var2 = this.method1117(var1);
      return var2 == Items.AIR ? 64 : var2.getMaxCount();
   }

   private boolean method1130(AutoCraftModule.Data var1, String var2, String var3) {
      if (!Objects.equals(var1.getText(), var2)) {
         return false;
      } else if (var3 != null && !var3.isBlank()) {
         String var4 = var1.getText2();
         return var4.equalsIgnoreCase(var3) || var4.toLowerCase(Locale.ROOT).contains(var3.toLowerCase(Locale.ROOT));
      } else {
         return true;
      }
   }

   private static final class Data {
      private final int intValue;
      private final String text;
      private final String text2;
      private final int intValue2;
      private final int intValue3;

      public int intValue() {
         return this.intValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
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
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, text, text2, intValue2, intValue3);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + "]";
      }

      private Data(int var1, String var2, String var3, int var4, int var5) {
         this.intValue = var1;
         this.text = var2;
         this.text2 = var3;
         this.intValue2 = var4;
         this.intValue3 = var5;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public int count() {
         return this.intValue2;
      }

      public int getIntValue3() {
         return this.intValue3;
      }
   }

   public static final class Data2 {
      private final BlockPos blockPos;
      private final int intValue;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, intValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "blockPos=" + this.blockPos + ", " + "intValue=" + this.intValue + "]";
      }

      public Data2(BlockPos var1, int var2) {
         this.blockPos = var1;
         this.intValue = var2;
      }

      public boolean method1131() {
         return this.blockPos != null;
      }

      public BlockPos getBlockPos() {
         return this.blockPos;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private static final class Data3 {
      private final String text;
      private final String text2;
      private final String text3;
      private final String text4;
      private final String[] texts;
      private final String[] texts2;

      public String text() {
         return this.text;
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

      public String[] texts() {
         return this.texts;
      }

      public String[] texts2() {
         return this.texts2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.texts, other.texts) && java.util.Objects.equals(this.texts2, other.texts2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, text3, text4, texts, texts2);
      }

      @Override
      public String toString() {
         return "Data3[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + ", " + "texts=" + this.texts + ", " + "texts2=" + this.texts2 + "]";
      }

      private Data3(String var1, String var2, String var3, String var4, String[] var5, String[] var6) {
         this.text = var1;
         this.text2 = var2;
         this.text3 = var3;
         this.text4 = var4;
         this.texts = var5;
         this.texts2 = var6;
      }

      public String getText() {
         return this.text;
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

      public String[] getTexts() {
         return this.texts;
      }

      public String[] getTexts2() {
         return this.texts2;
      }
   }

   private final class Entry {
      private final BlockPos blockPos;
      private final List<AutoCraftModule.Data> items = new ArrayList<>();
      private int intValue;

      private Entry(BlockPos var2) {
         this.blockPos = var2;
      }

      private void method1132(AutoCraftModule.Data var1) {
         if (var1.count() > 0) {
            this.items.add(var1);
         }
      }

      private int method1133(String var1, String var2) {
         int var3 = 0;

         for (AutoCraftModule.Data var5 : this.items) {
            if (AutoCraftModule.this.method1130(var5, var1, var2)) {
               var3 += var5.count();
            }
         }

         return var3;
      }

      private boolean method1134(String var1, String var2) {
         if (this.intValue > 0) {
            return true;
         } else {
            for (AutoCraftModule.Data var4 : this.items) {
               if (AutoCraftModule.this.method1130(var4, var1, var2) && var4.count() < var4.getIntValue3()) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   private static enum Type {
      IDLE,
      SCANNING,
      OPENING_ANALYZE,
      OPENING_SOURCE,
      OPENING_OUTPUT,
      OPENING_WORKBENCH,
      CRAFTING;
   }

   private static enum Type2 {
      IDLE,
      QUICK_MOVE,
      DEPOSIT_CURSOR;
   }

   private static enum Type3 {
      WAITING,
      OPENED,
      FAILED;
   }
}

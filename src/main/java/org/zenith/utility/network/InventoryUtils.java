package org.zenith.utility.network;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.IntPredicate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.Generated;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.BundleItemSelectedC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.zenith.Zenith;
import org.zenith.ZenithAccess;
import org.zenith.client.events.HandleInputEvent;
import org.zenith.client.events.HandleInputPostEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.modules.impl.combat.AutoTotemModule;
import org.zenith.client.modules.impl.misc.ElytraHelperModule;
import org.zenith.client.modules.impl.misc.InventorySettingModule;
import org.zenith.utility.misc.TaskScheduler;
import org.zenith.utility.render.KeyBindUtils;
import org.zenith.utility.render.MathUtils;

public final class InventoryUtils implements ZenithAccess {
   public static final List<KeyBinding> items = List.of(mc.options.forwardKey, mc.options.backKey, mc.options.leftKey, mc.options.rightKey, mc.options.jumpKey);
   private static final int CONST_intValue = 9;
   private static final int CONST_intValue2 = 36;
   private static final int CONST_intValue3 = 44;
   private static final int CONST_intValue4 = 45;

   public static void method897() {
      if (InventorySettingModule.INSTANCE.method2398()) {
         method898();
      }
   }

   public static void method898() {
      ScreenHandler var0 = mc.player.currentScreenHandler;
      ItemStack var1 = Registries.ITEM.get((int)MathUtils.method2651(0.0, 100.0)).getDefaultStack();
      ItemStackHash var2 = ItemStackHash.fromItemStack(var1, mc.player.networkHandler.getComponentHasher());
      mc.player
         .networkHandler
         .sendPacket(
            new ClickSlotC2SPacket(var0.syncId, var0.getRevision(), (short)0, (byte)0, SlotActionType.PICKUP_ALL, Int2ObjectMaps.singleton(0, var2), var2)
         );
   }

   public static void method899() {
      ScreenHandler var0 = mc.player.currentScreenHandler;
      mc.player
         .networkHandler
         .sendPacket(
            new ClickSlotC2SPacket(
               var0.syncId, var0.getRevision() + 1, (short)0, (byte)0, SlotActionType.PICKUP_ALL, Int2ObjectMaps.emptyMap(), ItemStackHash.EMPTY
            )
         );
   }

   public static void closeScreen() {
      if (mc.player.currentScreenHandler instanceof PlayerScreenHandler) {
         mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
      } else {
         mc.player.closeHandledScreen();
      }
   }

   public static void method900(Slot var0, Hand var1, boolean var2) {
      if (var0 != null
         && var0.id != -1
         && (!var1.equals(Hand.OFF_HAND) || var0.inventory instanceof PlayerInventory || var0.inventory instanceof EnderChestInventory)) {
         int var3 = var1.equals(Hand.MAIN_HAND) ? mc.player.getInventory().getSelectedSlot() : 40;
         method901(var0, var3, var2);
      }
   }

   public static void method901(Slot var0, int var1, boolean var2) {
      method903(var0, var1, SlotActionType.SWAP, false);
      if (var2) {
         method897();
      }
   }

   public static void method902(Slot var0, int var1) {
      method903(var0, var1, SlotActionType.SWAP, false);
   }

   public static void method903(Slot var0, int var1, SlotActionType var2, boolean var3) {
      if (var0 != null) {
         method904(var0.id, var1, var2, var3);
      }
   }

   public static void method904(int var0, int var1, SlotActionType var2, boolean var3) {
      method905(mc.player.currentScreenHandler.syncId, var0, var1, var2, var3);
   }

   public static void method905(int var0, int var1, int var2, SlotActionType var3, boolean var4) {
      mc.interactionManager.clickSlot(var0, var1, var2, var3, mc.player);
      if (var4) {
         mc.player.currentScreenHandler.onSlotClick(var1, var2, var3, mc.player);
      }
   }

   public static Slot method906(Item var0) {
      return method907(var0, var0x -> true);
   }

   public static Slot method907(Item var0, Predicate<Slot> var1) {
      return method913(var0, Comparator.comparingInt(var0x -> var0x.id), var1);
   }

   public static Slot method908(ScreenHandler var0, Item var1) {
      return var0.slots.stream().filter(var1x -> (var1x.getStack().getItem() == var1)).findFirst().orElse(null);
   }

   public static Slot method909(ScreenHandler var0, Predicate<Slot> var1) {
      return var0.slots.stream().filter(var1).findFirst().orElse(null);
   }

   public static Slot method910(Predicate<Slot> var0) {
      return method926().filter(var0).findFirst().orElse(null);
   }

   public static Slot method911(Predicate<Slot> var0, Comparator<Slot> var1) {
      return method926().filter(var0).max(var1).orElse(null);
   }

   public static Slot method912(ScreenHandler var0, Item var1, Comparator<Slot> var2, Predicate<Slot> var3) {
      return var0.slots.stream().filter(var1x -> var1x.getStack().getItem().equals(var1)).filter(var3).max(var2).orElse(null);
   }

   public static Slot method913(Item var0, Comparator<Slot> var1, Predicate<Slot> var2) {
      return method926().filter(var1x -> var1x.getStack().getItem().equals(var0)).filter(var2).max(var1).orElse(null);
   }

   public static Slot method914() {
      return method926()
         .filter(
            var0 -> (var0.getStack().get(DataComponentTypes.FOOD) != null && !var0.getStack().get(DataComponentTypes.FOOD).canAlwaysEat())
         )
         .max(Comparator.comparingDouble(var0 -> var0.getStack().get(DataComponentTypes.FOOD).saturation()))
         .orElse(null);
   }

   public static Slot method915(List<Item> var0) {
      return method926().filter(var1 -> var0.contains(var1.getStack().getItem())).findFirst().orElse(null);
   }

   public static Slot method916(List<Item> var0, Comparator<Slot> var1) {
      return method926().filter(var1x -> var0.contains(var1x.getStack().getItem())).max(var1).orElse(null);
   }

   public static Slot method917(RegistryEntry<StatusEffect> var0) {
      return method926()
         .filter(
            var1 -> {
               PotionContentsComponent var2 = var1.getStack().get(DataComponentTypes.POTION_CONTENTS);
               return (boolean)(var2 == null
                  ? 0
                  : StreamSupport.stream(var2.getEffects().spliterator(), false).anyMatch(var1x -> var1x.getEffectType().equals(var0)));
            }
         )
         .findFirst()
         .orElse(null);
   }

   public static boolean method918(ItemStack var0, String var1) {
      if (var0.isEmpty()) {
         return false;
      } else {
         NbtComponent var2 = var0.get(DataComponentTypes.CUSTOM_DATA);
         return (boolean)(var2 == null ? 0 : var2.copyNbt().toString().contains(var1));
      }
   }

   public static Slot method919(StatusEffectCategory var0) {
      return method926()
         .filter(
            var1 -> {
               ItemStack var2 = var1.getStack();
               PotionContentsComponent var3 = var2.get(DataComponentTypes.POTION_CONTENTS);
               if (var2.getItem().equals(Items.SPLASH_POTION) && var3 != null) {
                  StatusEffectCategory var4 = var0.equals(StatusEffectCategory.BENEFICIAL) ? StatusEffectCategory.HARMFUL : StatusEffectCategory.BENEFICIAL;
                  long var5 = StreamSupport.stream(var3.getEffects().spliterator(), false)
                     .filter(var1x -> var1x.getEffectType().value().getCategory().equals(var0))
                     .count();
                  long var7 = StreamSupport.stream(var3.getEffects().spliterator(), false)
                     .filter(var1x -> var1x.getEffectType().value().getCategory().equals(var4))
                     .count();
                  return (var5 >= var7);
               } else {
                  return false;
               }
            }
         )
         .findFirst()
         .orElse(null);
   }

   public static int method920(Item var0) {
      return IntStream.range(0, 45)
         .filter(var1 -> Objects.requireNonNull(mc.player).getInventory().getStack(var1).getItem().equals(var0))
         .map(var0x -> mc.player.getInventory().getStack(var0x).getCount())
         .sum();
   }

   public static int method921(List<Item> var0) {
      return IntStream.range(0, 9).filter(var1 -> var0.contains(mc.player.getInventory().getStack(var1).getItem())).findFirst().orElse(-1);
   }

   public static int method922(IntPredicate var0) {
      return IntStream.range(0, 9).filter(var0).findFirst().orElse(-1);
   }

   public static int method923(Predicate<Slot> var0) {
      return method926().filter(var0).mapToInt(var0x -> var0x.getStack().getCount()).sum();
   }

   public static Slot method924() {
      long var0;
      int var2 = (var0 = method926().count()) == 46L ? 10 : 9;
      return method926().toList().get(Math.toIntExact(var0 - var2 + mc.player.getInventory().getSelectedSlot()));
   }

   public static boolean method925() {
      return (method926().toList().size() != 46);
   }

   public static Stream<Slot> method926() {
      return mc.player.currentScreenHandler.slots.stream();
   }

   public static void method927(Item var0) {
      method928(var0, var0x -> true);
   }

   public static void method928(Item var0, Predicate<Slot> var1) {
      method930(var0, var1, Hand.MAIN_HAND);
   }

   public static void method929(Item var0, Hand var1) {
      method930(var0, var0x -> true, var1);
   }

   public static void method930(Item var0, Predicate<Slot> var1, Hand var2) {
      float var3;
      if ((var3 = mc.player.getItemCooldownManager().getCooldownProgress(var0.getDefaultStack(), 0.0F)) > 0.0F) {
         String cooldown = MathUtils.method2652(var3, 0.1) + "с";
         Zenith.getInstance()
            .getHudHolder()
            .method6972(
               "N",
               Text.of(
                  var0.getName()
                     .copy()
                     .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()))
                     .append(
                        Text.of(" находиться в кд")
                           .copy()
                           .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287()))
                     )
               )
            );
      } else {
         Slot var4 = method907(var0, var1);
         if (var4 == null) {
            Zenith.getInstance()
               .getHudHolder()
               .method6972(
                  "M",
                  Text.of(
                     var0.getName()
                        .copy()
                        .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()))
                        .append(
                           Text.of(" не найден")
                              .copy()
                              .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287()))
                        )
                  )
               );
         } else if (mc.player.isUsingItem() && Zenith.getInstance().getServerDetector().method1642()) {
            Zenith.getInstance()
               .getHudHolder()
               .method6972(
                  "M",
                  Text.of(
                     Text.of("Хавать")
                        .copy()
                        .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()))
                        .append(
                           Text.of(" нельзя")
                              .copy()
                              .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287()))
                        )
                  )
               );
         } else {
            TaskScheduler.Entry2 var5 = new TaskScheduler.Entry2();
            if (!InventorySettingModule.INSTANCE.method2397() && InventorySettingModule.INSTANCE.method2396()) {
               var5.method5120(HandleInputEvent.class, var2x -> {
                  if (var2x.isCancelled()) {
                     return false;
                  } else {
                     method931(var4, var2);
                     closeScreen();
                     return true;
                  }
               });
            } else {
               int var6 = Math.toIntExact(method926().count());
               int var7 = var4.id - 36;
               if (var2 == Hand.MAIN_HAND && var7 >= 0 && var7 <= 8 && var6 == 46) {
                  AtomicInteger var10 = new AtomicInteger(mc.player.inventory.getSelectedSlot());
                  if (!InventorySettingModule.INSTANCE.method2396()) {
                     var5.method5120(HandleInputEvent.class, var2x -> {
                        var10.set(mc.player.inventory.getSelectedSlot());
                        mc.player.inventory.setSelectedSlot(var7);
                        return true;
                     });
                     var5.method5120(HandleInputEvent.class, var1x -> {
                        if (var1x.isCancelled()) {
                           return false;
                        } else {
                           UseItemUtils.useItem(Hand.MAIN_HAND);
                           if (Zenith.getInstance().getServerDetector().method1642()) {
                              mc.interactionManager.stopUsingItem(mc.player);
                           }

                           mc.player.inventory.setSelectedSlot(var10.get());
                           var1x.cancel();
                           return true;
                        }
                     });
                  } else {
                     var5.method5120(HandleInputEvent.class, var1x -> {
                        if (var1x.isCancelled()) {
                           return false;
                        } else {
                           int var2x = mc.player.inventory.getSelectedSlot();
                           mc.player.inventory.setSelectedSlot(var7);
                           UseItemUtils.useItem(Hand.MAIN_HAND);
                           mc.player.inventory.setSelectedSlot(var2x);
                           var1x.cancel();
                           return true;
                        }
                     });
                  }
               } else if (!InventorySettingModule.INSTANCE.method2396()) {
                  if (KeyBindUtils.method2630(AutoTotemModule.class) && !AutoTotemModule.INSTANCE.method725() || var2 != Hand.OFF_HAND) {
                     int var8 = var2.equals(Hand.MAIN_HAND) ? mc.player.getInventory().getSelectedSlot() : 40;
                     int var9 = mc.player.getInventory().getSelectedSlot();
                     var5.method5120(
                        HandleInputPostEvent.class,
                        var2x -> {
                           if (!InventorySettingModule.INSTANCE.method2397()
                              || !mc.player.lastPlayerInput.jump()
                                 && !mc.player.isSprinting()
                                 && !mc.player.lastPlayerInput.forward()
                                 && !mc.player.lastPlayerInput.backward()
                                 && !mc.player.lastPlayerInput.left()
                                 && !mc.player.lastPlayerInput.right()) {
                              method901(var4, var8, false);
                              closeScreen();
                              return true;
                           } else {
                              return false;
                           }
                        }
                     );
                     var5.method5120(HandleInputEvent.class, var3x -> {
                        if (var3x.isCancelled()) {
                           return false;
                        } else {
                           if (var2 == Hand.MAIN_HAND) {
                              mc.player.getInventory().setSelectedSlot(var8);
                              UseItemUtils.useItem(var2);
                              if (Zenith.getInstance().getServerDetector().method1642()) {
                                 UseItemUtils.useItem(var2);
                              }

                              mc.player.getInventory().setSelectedSlot(var9);
                              if (Zenith.getInstance().getServerDetector().method1642()) {
                                 mc.interactionManager.stopUsingItem(mc.player);
                              }
                           } else {
                              UseItemUtils.useItem(var2);
                              if (Zenith.getInstance().getServerDetector().method1642()) {
                                 UseItemUtils.useItem(var2);
                              }
                           }

                           var3x.cancel();
                           return true;
                        }
                     });
                     var5.method5120(
                        HandleInputPostEvent.class,
                        var2x -> {
                           if (!InventorySettingModule.INSTANCE.method2397()
                              || !mc.player.lastPlayerInput.jump()
                                 && !mc.player.isSprinting()
                                 && !mc.player.lastPlayerInput.forward()
                                 && !mc.player.lastPlayerInput.backward()
                                 && !mc.player.lastPlayerInput.left()
                                 && !mc.player.lastPlayerInput.right()) {
                              method901(var4, var8, false);
                              closeScreen();
                              return true;
                           } else {
                              return false;
                           }
                        }
                     );
                     if (InventorySettingModule.INSTANCE.method2397()) {
                        var5.method5123(MoveInputEvent.class, var0x -> {
                           var0x.method3044();
                           return true;
                        });
                     }
                  }
               } else {
                  var5.method5120(
                     HandleInputEvent.class,
                     var2x -> {
                        if ((!KeyBindUtils.method2630(AutoTotemModule.class) || AutoTotemModule.INSTANCE.method725()) && var2 == Hand.OFF_HAND) {
                           return true;
                        } else if (var2x.isCancelled()) {
                           return false;
                        } else if (!InventorySettingModule.INSTANCE.method2397()
                           || !mc.player.lastPlayerInput.jump()
                              && !mc.player.isSprinting()
                              && !mc.player.lastPlayerInput.forward()
                              && !mc.player.lastPlayerInput.backward()
                              && !mc.player.lastPlayerInput.left()
                              && !mc.player.lastPlayerInput.right()) {
                           method931(var4, var2);
                           closeScreen();
                           return true;
                        } else {
                           return false;
                        }
                     }
                  );
                  if (InventorySettingModule.INSTANCE.method2397()) {
                     var5.method5123(MoveInputEvent.class, var0x -> {
                        var0x.method3044();
                        return true;
                     });
                  }
               }
            }

            Zenith.getInstance().getTaskScheduler().method5110(var5);
         }
      }
   }

   public static void method931(Slot var0, Hand var1) {
      method900(var0, var1, false);
      closeScreen();
      UseItemUtils.useItem(var1);
      method900(var0, var1, true);
   }

   public static void method932(Slot var0, int var1) {
      if (var0 != null) {
         method935(var0.id, var1, false, false);
      }
   }

   public static void method933(Slot var0, int var1, boolean var2) {
      method934(var0, var1, var2, false);
   }

   public static void method934(Slot var0, int var1, boolean var2, boolean var3) {
      if (var0 != null) {
         method935(var0.id, var1, var2, var3);
      }
   }

   public static void method935(int var0, int var1, boolean var2, boolean var3) {
      if (var0 != var1 && var0 != -1) {
         int var4 = Math.toIntExact(method926().count());
         int var5;
         if ((var5 = var0 - 36) >= 0 && var5 <= 8 && var4 == 46) {
            if (var2) {
               KeyBindUtils.method2627(InventoryUtils.class, () -> method904(var1, var5, SlotActionType.SWAP, false));
            } else {
               method904(var1, var5, SlotActionType.SWAP, false);
               closeScreen();
            }
         } else if (!ElytraHelperModule.INSTANCE.isEnabled() || var1 != 6 || !ElytraHelperModule.INSTANCE.method7456()) {
            if (var2) {
               if (InventorySettingModule.INSTANCE.method2396()) {
                  KeyBindUtils.method2627(InventoryUtils.class, () -> method936(var0, var1, var3));
               } else {
                  TaskScheduler.Entry2 var6 = Zenith.getInstance()
                     .getTaskScheduler()
                     .method5109(InventoryUtils.class)
                     .method5121(
                        MoveInputEvent.class,
                        var1x -> {
                           if (InventorySettingModule.INSTANCE.method2397()) {
                              var1x.method3044();
                              if (mc.player.lastPlayerInput.jump()
                                 || mc.player.isSprinting()
                                 || mc.player.lastPlayerInput.forward()
                                 || mc.player.lastPlayerInput.backward()
                                 || mc.player.lastPlayerInput.left()
                                 || mc.player.lastPlayerInput.right()) {
                                 return false;
                              }
                           }

                           method904(var0, 0, SlotActionType.SWAP, false);
                           closeScreen();
                           return true;
                        },
                        0
                     )
                     .method5120(MoveInputEvent.class, var0x -> true)
                     .method5120(
                        MoveInputEvent.class,
                        var2x -> {
                           if (InventorySettingModule.INSTANCE.method2397()) {
                              var2x.method3044();
                              if (mc.player.lastPlayerInput.jump()
                                 || mc.player.isSprinting()
                                 || mc.player.lastPlayerInput.forward()
                                 || mc.player.lastPlayerInput.backward()
                                 || mc.player.lastPlayerInput.left()
                                 || mc.player.lastPlayerInput.right()) {
                                 return false;
                              }
                           }

                           method904(var1, 0, SlotActionType.SWAP, false);
                           closeScreen();
                           method904(var0, 0, SlotActionType.SWAP, false);
                           closeScreen();
                           return true;
                        }
                     );
                  Zenith.getInstance().getTaskScheduler().method5111(var6, 0);
               }
            } else {
               method936(var0, var1, var3);
               closeScreen();
            }
         }
      }
   }

   public static void method936(int var0, int var1, boolean var2) {
      method904(var0, 0, SlotActionType.SWAP, false);
      closeScreen();
      method904(var1, 0, SlotActionType.SWAP, false);
      method904(var0, 0, SlotActionType.SWAP, false);
      if (var2) {
         method897();
      }
   }

   public static InventoryUtils.Data method937(Item var0, Comparator<ItemStack> var1, Predicate<Slot> var2) {
      return method938(var1x -> var1x.getItem().equals(var0), var1, var2);
   }

   public static InventoryUtils.Data method938(Predicate<ItemStack> var0, Comparator<ItemStack> var1, Predicate<Slot> var2) {
      if (mc.player != null && mc.player.currentScreenHandler != null) {
         InventoryUtils.Data var3 = null;
         InventoryUtils.Data var4 = null;

         for (Slot var6 : mc.player.currentScreenHandler.slots) {
            if (var2.test(var6)) {
               ItemStack var7 = var6.getStack();
               if (var7.isEmpty() || !var0.test(var7)) {
                  BundleContentsComponent var8 = var7.get(DataComponentTypes.BUNDLE_CONTENTS);
                  if (var8 != null) {
                     for (int var9 = 0; var9 < var8.size(); var9++) {
                        ItemStack var10 = var8.get(var9);
                        if (!var10.isEmpty() && var0.test(var10) && (var4 == null || var1.compare(var10, var4.getItemStack()) > 0)) {
                           var4 = new InventoryUtils.Data(var6, var9, var10);
                        }
                     }
                  }
               } else if (var3 == null || var1.compare(var7, var3.getItemStack()) > 0) {
                  var3 = InventoryUtils.Data.method944(var6);
               }
            }
         }

         return var3 != null ? var3 : var4;
      } else {
         return null;
      }
   }

   public static int method939(Predicate<ItemStack> var0, Predicate<Slot> var1) {
      if (mc.player != null && mc.player.currentScreenHandler != null) {
         int var2 = 0;

         for (Slot var4 : mc.player.currentScreenHandler.slots) {
            if (var1.test(var4)) {
               ItemStack var5 = var4.getStack();
               if (!var5.isEmpty() && var0.test(var5)) {
                  var2 += var5.getCount();
               } else {
                  BundleContentsComponent var6 = var5.get(DataComponentTypes.BUNDLE_CONTENTS);
                  if (var6 != null) {
                     for (int var7 = 0; var7 < var6.size(); var7++) {
                        ItemStack var8 = var6.get(var7);
                        if (!var8.isEmpty() && var0.test(var8)) {
                           var2 += var8.getCount();
                        }
                     }
                  }
               }
            }
         }

         return var2;
      } else {
         return 0;
      }
   }

   public static boolean method940(InventoryUtils.Data var0, Hand var1, boolean var2) {
      if (var0 == null) {
         return false;
      } else if (!var0.method945()) {
         method900(var0.getSlot(), var1, var2);
         return true;
      } else {
         return method941(var0.getSlot(), var0.getIntValue(), var1, var2);
      }
   }

   public static boolean method941(Slot var0, int var1, Hand var2, boolean var3) {
      if (var0 != null && mc.player != null) {
         ScreenHandler var4 = mc.player.currentScreenHandler;
         if (!(var4 instanceof PlayerScreenHandler) || !var4.getCursorStack().isEmpty()) {
            return false;
         } else if (!(var0.getStack().getItem() instanceof BundleItem)) {
            return false;
         } else {
            BundleContentsComponent var5 = var0.getStack().get(DataComponentTypes.BUNDLE_CONTENTS);
            if (var5 != null && var1 >= 0 && var1 < var5.size()) {
               int var6;
               if ((var6 = var2.equals(Hand.OFF_HAND) ? 45 : 36 + mc.player.getInventory().getSelectedSlot()) == var0.id) {
                  return false;
               } else {
                  method942(var0, var1);
                  method904(var0.id, 1, SlotActionType.PICKUP, false);
                  if (var4.getCursorStack().isEmpty()) {
                     return false;
                  } else {
                     method904(var6, 0, SlotActionType.PICKUP, false);
                     if (!var4.getCursorStack().isEmpty()) {
                        method904(var0.id, 0, SlotActionType.PICKUP, false);
                     }

                     if (!var4.getCursorStack().isEmpty()) {
                        method943(var4, var0, var6);
                     }

                     closeScreen();
                     if (var3) {
                        method897();
                     }

                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private static void method942(Slot var0, int var1) {
      if (BundleItem.getSelectedStackIndex(var0.getStack()) != var1) {
         mc.player.networkHandler.sendPacket(new BundleItemSelectedC2SPacket(var0.id, var1));
         BundleItem.setSelectedStackIndex(var0.getStack(), var1);
      }
   }

   private static void method943(ScreenHandler var0, Slot var1, int var2) {
      for (Slot var4 : var0.slots) {
         if (var4.id >= 9 && var4.id <= 44 && var4.getStack().isEmpty()) {
            method904(var4.id, 0, SlotActionType.PICKUP, false);
            return;
         }
      }

      method904(var2, 0, SlotActionType.PICKUP, false);
      method904(var1.id, 0, SlotActionType.PICKUP, false);
   }

   @Generated
   private InventoryUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }

   public static final class Data {
      private final Slot slot;
      private final int intValue;
      private final ItemStack itemStack;

      public Slot slot() {
         return this.slot;
      }

      public int intValue() {
         return this.intValue;
      }

      public ItemStack itemStack() {
         return this.itemStack;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.slot, other.slot) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.itemStack, other.itemStack);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(slot, intValue, itemStack);
      }

      @Override
      public String toString() {
         return "Data[" + "slot=" + this.slot + ", " + "intValue=" + this.intValue + ", " + "itemStack=" + this.itemStack + "]";
      }

      public Data(Slot var1, int var2, ItemStack var3) {
         this.slot = var1;
         this.intValue = var2;
         this.itemStack = var3;
      }

      public static InventoryUtils.Data method944(Slot var0) {
         return var0 == null ? null : new InventoryUtils.Data(var0, -1, var0.getStack());
      }

      public boolean method945() {
         return this.intValue >= 0;
      }

      public Slot getSlot() {
         return this.slot;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public ItemStack getItemStack() {
         return this.itemStack;
      }
   }
}

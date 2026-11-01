package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Comparator;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.misc.Util10;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.render.KeyBindUtils;

@ModuleInfo(
   name = "OffHandManager",
   category = ModuleCategory.COMBAT,
   description = "module.offhandmanager.description"
)
public final class OffHandManagerModule extends Module {
   public static final OffHandManagerModule INSTANCE = new OffHandManagerModule();
   private final ModeSetting swapHealMode = new ModeSetting(
      "module.offhand.swapHealMode",
      "module.offhand.swapHealMode.desc",
      "module.offhand.mode.mythic",
      "module.offhand.mode.legendary",
      "module.offhand.mode.talisman",
      "module.offhand.mode.none"
   );
   private final ModeSetting swapEnemyMode = new ModeSetting(
      "module.offhand.swapEnemyMode",
      "module.offhand.swapEnemyMode.desc",
      "module.offhand.mode.cerberus",
      "module.offhand.mode.mythic",
      "module.offhand.mode.legendary",
      "module.offhand.mode.talisman",
      "module.offhand.mode.none"
   );
   private final StopWatch stopWatch = new StopWatch();

   private OffHandManagerModule() {
   }

   public void reset() {
      this.stopWatch.reset();
   }

   @EventTarget
   public void method7216(MoveInputEvent var1) {
      LivingEntity var2 = AuraModule.INSTANCE.method6358();
      if (this.method7218()) {
         Slot var3;
         if (!this.swapEnemyMode.isSelected(4) && var2 != null && this.method7221(var2)) {
            var3 = this.method7219(this.swapEnemyMode);
         } else if (mc.player.isUsingItem() && !this.swapHealMode.isSelected(3)) {
            var3 = this.method7219(this.swapHealMode);
         } else {
            var3 = null;
         }

         if (var3 != null
            && !this.method7217(var3.getStack())
            && KeyBindUtils.method2630(AutoTotemModule.class)
            && KeyBindUtils.method2630(AutoSwapModule.class)
            && KeyBindUtils.method2630(OffHandManagerModule.class)) {
            KeyBindUtils.method2627(OffHandManagerModule.class, () -> {
               if (KeyBindUtils.method2630(AutoTotemModule.class)) {
                  InventoryUtils.method900(var3, Hand.OFF_HAND, true);
               }
            });
         }
      }
   }

   private boolean method7217(ItemStack var1) {
      ItemStack var2 = mc.player.getOffHandStack();
      return var1 == var2 || ItemStack.areItemsAndComponentsEqual(var1, var2);
   }

   private boolean method7218() {
      return this.stopWatch.method7546(1000L);
   }

   private Slot method7219(ModeSetting var1) {
      try {
         if (var1.get().equals("module.offhand.mode.talisman")) {
            return InventoryUtils.method912(
               mc.player.playerScreenHandler,
               Items.TOTEM_OF_UNDYING,
               Comparator.<Slot, Boolean>comparing(var0 -> !var0.getStack().hasEnchantments()).thenComparing((var0, var1x) -> {
                  NbtComponent var2x = var0.getStack().get(DataComponentTypes.CUSTOM_DATA);
                  NbtComponent var3x = var1x.getStack().get(DataComponentTypes.CUSTOM_DATA);
                  if (var2x == null && var3x != null) {
                     return -1;
                  } else if (var2x != null && var3x == null) {
                     return 1;
                  } else if (var2x == null) {
                     return 0;
                  } else {
                     boolean var4 = var2x.copyNbt().contains("sphereEffect");
                     boolean var5 = var3x.copyNbt().contains("sphereEffect");
                     if (var4 && !var5) {
                        return 1;
                     } else if (!var4 && var5) {
                        return -1;
                     } else if (!var4) {
                        return 0;
                     } else {
                        String var6 = var2x.copyNbt().get("sphereEffect").toString();
                        String var7 = var3x.copyNbt().get("sphereEffect").toString();
                        return Integer.compare(var7.length(), var6.length());
                     }
                  }
               }).thenComparingInt(var0 -> var0.id).reversed(),
               var0 -> true
            );
         } else {
            String var2 = this.method7220(var1.get());
            return InventoryUtils.method907(Items.PLAYER_HEAD, var1x -> {
               if (!var1x.getStack().isEmpty()) {
                  NbtComponent var2x = var1x.getStack().get(DataComponentTypes.CUSTOM_DATA);
                  if (var2x != null && var2x.copyNbt().contains("SkullOwner") && var2x.copyNbt().get("SkullOwner").toString().contains(var2)) {
                     return true;
                  }
               }

               return false;
            });
         }
      } catch (Exception var3) {
         var3.printStackTrace();
         return null;
      }
   }

   private String method7220(String var1) {
      return switch (var1) {
         case "module.offhand.mode.cerberus" -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA5NWE3ZmQ5MGRhYTFiYmU3MDY5MDg5NzQwZTA1ZDBiZmM2NjI5NmVlM2M0MGVlNzFhNGUwYTY2MTZiMmJiYyJ9fX0=";
         case "module.offhand.mode.mythic" -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmFmZjJlYjQ5OGU1YzZhMDQ0ODRmMGM5Zjc4NWI0NDg0NzlhYjIxM2RmOTVlYzkxMTc2YTMwOGExMmFkZDcwIn19fQ==";
         case "module.offhand.mode.legendary" -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM5MzY1NjQyYzZlZGRjZmVkZjViNWUxNGUyYmM3MTI1N2Q5ZTRhMzM2M2QxMjNjNmYzM2M1NWNhZmJmNmQifX19";
         default -> throw new RuntimeException("Unknown key: " + var1);
      };
   }

   private boolean method7221(LivingEntity var1) {
      if (var1.isUsingItem()) {
         return true;
      } else {
         return Util10.method2178(var1.getMainHandStack())
            ? false
            : var1.getOffHandStack().getItem() != Items.PLAYER_HEAD || this.method7222(var1.getOffHandStack());
      }
   }

   private boolean method7222(ItemStack var1) {
      NbtComponent var2 = var1.get(DataComponentTypes.CUSTOM_DATA);
      return var2 != null
         && var2.copyNbt().contains("SkullOwner")
         && var2.copyNbt()
            .get("SkullOwner")
            .toString()
            .contains(
               "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM5MzY1NjQyYzZlZGRjZmVkZjViNWUxNGUyYmM3MTI1N2Q5ZTRhMzM2M2QxMjNjNmYzM2M1NWNhZmJmNmQifX19"
            );
   }
}

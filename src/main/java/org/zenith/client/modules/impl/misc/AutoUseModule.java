package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.potion.Potions;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.StopUsingItemEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "AutoUse",
   category = ModuleCategory.MISC,
   description = "module.autouse.description"
)
public final class AutoUseModule extends Module {
   public static final AutoUseModule INSTANCE = new AutoUseModule();
   private final MultiSelectSetting mode = MultiSelectSetting.method5224(
      "module.autoUse.mode", "", List.of("module.autoUse.invisibility", "module.autoUse.food")
   );
   private int intValue = -1;
   private int intValue2 = -1;
   private boolean usingItem;

   private AutoUseModule() {
   }

   @Override
   public void onDisable() {
      if (this.usingItem && mc.player != null && mc.interactionManager != null && mc.player.isUsingItem()) {
         mc.interactionManager.stopUsingItem(mc.player);
      }

      this.method7841();
      super.onDisable();
   }

   @EventTarget(0)
   public void onUpdate(UpdateEvent var1) {
      if (mc.player == null || mc.world == null || mc.interactionManager == null) {
         this.method7842();
      } else if (this.usingItem) {
         if (!mc.player.isUsingItem()) {
            this.method7841();
         }
      } else if (!mc.player.isUsingItem()) {
         if (!this.mode.method5227(0) || mc.player.isInvisible() || !this.method7840(this::method7843)) {
            if (this.mode.method5227(1) && mc.player.getHungerManager().getFoodLevel() <= 7) {
               this.method7840(var0 -> var0.getUseAction() == UseAction.EAT);
            }
         }
      }
   }

   @EventTarget(0)
   public void method7839(StopUsingItemEvent var1) {
      if (this.usingItem && mc.player != null && mc.player.isUsingItem()) {
         var1.cancel();
      }
   }

   private boolean method7840(Predicate<ItemStack> var1) {
      if (mc.player == null) {
         return false;
      } else {
         PlayerInventory var2 = mc.player.getInventory();
         int var3 = InventoryUtils.method922(var2x -> var1.test(var2.getStack(var2x)));
         this.intValue = var2.getSelectedSlot();
         if (var3 != -1) {
            var2.setSelectedSlot(var3);
         } else {
            Slot var4 = InventoryUtils.method910(
               var1x -> var1x.inventory instanceof PlayerInventory && var1x.getIndex() >= 9 && var1x.getIndex() < 36 && var1.test(var1x.getStack())
            );
            if (var4 == null) {
               this.method7842();
               return false;
            }

            this.intValue2 = var4.getIndex();
            InventoryUtils.method900(var4, Hand.MAIN_HAND, false);
         }

         this.usingItem = true;
         UseItemUtils.useItem(Hand.MAIN_HAND);
         if (!mc.player.isUsingItem()) {
            this.method7841();
         }

         return true;
      }
   }

   private void method7841() {
      if (mc.player != null && this.intValue != -1) {
         if (this.intValue2 != -1) {
            Slot var1 = InventoryUtils.method910(var1x -> var1x.inventory instanceof PlayerInventory && var1x.getIndex() == this.intValue2);
            if (var1 != null) {
               InventoryUtils.method901(var1, this.intValue, true);
            }
         }

         mc.player.getInventory().setSelectedSlot(this.intValue);
      }

      this.method7842();
   }

   private void method7842() {
      this.intValue = -1;
      this.intValue2 = -1;
      this.usingItem = false;
   }

   private boolean method7843(ItemStack var1) {
      if (!var1.isOf(Items.POTION) && !var1.isOf(Items.SPLASH_POTION) && !var1.isOf(Items.LINGERING_POTION)) {
         return false;
      } else {
         PotionContentsComponent var2 = var1.get(DataComponentTypes.POTION_CONTENTS);
         if (var2 == null) {
            return false;
         } else if (var2.potion().isPresent() && var2.potion().get().value().equals(Potions.INVISIBILITY)) {
            return true;
         } else {
            for (StatusEffectInstance var4 : var2.getEffects()) {
               if (var4.getEffectType().equals(StatusEffects.INVISIBILITY)) {
                  return true;
               }
            }

            return false;
         }
      }
   }
}

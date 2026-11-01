package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import org.zenith.ChatLogger;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.utility.network.InventoryUtils;

@ModuleInfo(
   name = "ElytraHelper",
   description = "module.elytrahelper.description",
   category = ModuleCategory.MISC
)
public final class ElytraHelperModule extends Module {
   public static final ElytraHelperModule INSTANCE = new ElytraHelperModule();
   private final BindSetting elytraSetting = new BindSetting("module.elytraHelper.elytraSetting", "module.elytraHelper.elytraSetting.desc");
   private final BindSetting fireworkSetting = new BindSetting("module.elytraHelper.fireworkSetting", "module.elytraHelper.fireworkSetting.desc");
   private final BooleanSetting startSetting = new BooleanSetting("module.elytraHelper.startSetting", "module.elytraHelper.startSetting.desc", false);
   private final BooleanSetting sprintSetting = new BooleanSetting(
      "module.elytraHelper.sprintSetting", "module.elytraHelper.sprintSetting.desc", false, this.startSetting::isEnabled
   );
   private final BooleanSetting onlyHotBar = new BooleanSetting("module.elytraHelper.onlyHotBar", "module.elytraHelper.onlyHotBar.desc", false);

   @Override
   public void onEnable() {
      ChatLogger.method2019("Скорость больше с вкл спринтом + надо быть на версии 1.21.1 и ниже но на хв банит");
      super.onEnable();
   }

   private ElytraHelperModule() {
   }

   @EventTarget
   public void method7452(MoveInputEvent var1) {
      if (mc.player.isAlive() && mc.player.currentScreenHandler instanceof PlayerScreenHandler) {
         if (Zenith.getInstance().getTaskScheduler().method5116()
            && this.startSetting.isEnabled()
            && Objects.requireNonNull(mc.player).getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Items.ELYTRA)) {
            if (mc.player.isOnGround()) {
               var1.method3041(true);
            } else if (!mc.player.isGliding()) {
               if (this.sprintSetting.isEnabled()
                  && mc.player.canSprint(true)
                  && !mc.player.horizontalCollision
                  && !mc.player.isUsingItem()
                  && !mc.player.isSubmergedInWater()) {
                  mc.player.setSprinting(true);
               }

               var1.method3041(!mc.player.lastPlayerInput.jump());
            } else if (mc.player.isGliding() && mc.player.horizontalCollision) {
               var1.setPlayerInput(
                  new PlayerInput(
                     false,
                     var1.getPlayerInput().backward(),
                     var1.getPlayerInput().left(),
                     var1.getPlayerInput().right(),
                     var1.getPlayerInput().jump(),
                     var1.getPlayerInput().sneak(),
                     var1.getPlayerInput().sprint()
                  )
               );
            }
         }
      }
   }

   @EventTarget
   public void method7453(KeyEvent var1) {
      if (var1.isPress(this.elytraSetting.getKeyCode())) {
         this.method7454();
      } else if (var1.isPress(this.fireworkSetting.getKeyCode()) && mc.player.isGliding()) {
         InventoryUtils.method929(Items.FIREWORK_ROCKET, Hand.OFF_HAND);
      }
   }

   public void method7454() {
      Slot var1 = this.method7455();
      if (var1 != null) {
         InventoryUtils.method934(var1, 6, true, false);
      }
   }

   private Slot method7455() {
      return Objects.requireNonNull(mc.player).getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Items.ELYTRA)
         ? InventoryUtils.method916(
            List.of(
               Items.NETHERITE_CHESTPLATE,
               Items.DIAMOND_CHESTPLATE,
               Items.CHAINMAIL_CHESTPLATE,
               Items.IRON_CHESTPLATE,
               Items.GOLDEN_CHESTPLATE,
               Items.LEATHER_CHESTPLATE
            ),
            Comparator.comparingInt(var0 -> var0.id)
         )
         : InventoryUtils.method912(mc.player.playerScreenHandler, Items.ELYTRA, Comparator.comparingInt(var0 -> var0.id), var0 -> true);
   }

   public boolean method7456() {
      return this.onlyHotBar.isEnabled();
   }
}

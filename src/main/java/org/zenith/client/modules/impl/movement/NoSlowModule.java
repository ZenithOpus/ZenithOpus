package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.util.Hand;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.NoSlowEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.misc.ViaCompat;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "NoSlow",
   category = ModuleCategory.MOVEMENT,
   description = "module.noslow.description"
)
public final class NoSlowModule extends Module {
   private static final int[] CONST_intValues = new int[]{2, 3, 3};
   public static final NoSlowModule INSTANCE = new NoSlowModule();
   private int intValue;
   private int intValue2;
   private final ModeSetting mode = new ModeSetting("module.noSlow.mode", "module.noSlow.mode.desc");
   private final ModeSetting.Entry entry = new ModeSetting.Entry(this.mode, "module.noSlow.mode.grimNew");
   private final ModeSetting.Entry entry2 = new ModeSetting.Entry(this.mode, "module.noSlow.mode.realyWorld");
   private final ModeSetting.Entry entry3 = new ModeSetting.Entry(this.mode, "module.noSlow.mode.grimOld").method2103();
   private final BooleanSetting sprint = new BooleanSetting("module.noSlow.sprint", "module.noSlow.sprint.desc", true, this.entry3::isSelected);

   private NoSlowModule() {
   }

   @Override
   public void onEnable() {
      this.method1475();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.method1475();
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (this.entry2.isSelected() && mc.player != null && mc.world != null && !mc.player.isGliding() && mc.player.isUsingItem()) {
         this.intValue++;
      } else {
         this.method1475();
      }
   }

   @EventTarget
   public void method1474(NoSlowEvent var1) {
      if (this.entry.isSelected() && mc.player.getItemUseTime() % 2 == 0) {
         var1.setCancelled(true);
      }

      if (this.entry2.isSelected() && !mc.player.isGliding()) {
         int var2 = CONST_intValues[this.intValue2 % CONST_intValues.length];
         if (this.intValue >= var2) {
            var1.setCancelled(true);
            this.intValue = 0;
            this.intValue2++;
         }
      }

      if (this.entry3.isSelected()) {
         Hand var3 = mc.player.getActiveHand();
         if (this.sprint.isEnabled()) {
            mc.player
               .setSprinting(
                  mc.player.canSprint(true)
                     && (mc.player.isSubmergedInWater() ? mc.player.input.hasForwardMovement() : ViaCompat.method4846(mc.player.input) >= 0.8F)
                     && !mc.player.isGliding()
                     && (!mc.player.shouldSlowDown() || mc.player.isSubmergedInWater())
               );
         }

         UseItemUtils.useItem(var3.equals(Hand.MAIN_HAND) ? Hand.OFF_HAND : Hand.MAIN_HAND);
         var1.setCancelled(true);
      }
   }

   private void method1475() {
      this.intValue = 0;
      this.intValue2 = 0;
   }
}

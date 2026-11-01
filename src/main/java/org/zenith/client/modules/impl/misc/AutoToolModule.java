package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Comparator;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.BlockBreakingEvent;
import org.zenith.client.events.MouseScrollEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.misc.StopWatch;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.KeyBindUtils;

@ModuleInfo(
   name = "AutoTool",
   category = ModuleCategory.MISC,
   description = "module.autotool.description"
)
public final class AutoToolModule extends Module {
   public static final AutoToolModule INSTANCE = new AutoToolModule();
   private final StopWatch stopWatch = new StopWatch();
   private Slot slot = null;

   private AutoToolModule() {
   }

   @EventTarget
   public void method261(MouseScrollEvent var1) {
      if (this.slot != null) {
         var1.setCancelled(true);
      }
   }

   @EventTarget
   public void method262(BlockBreakingEvent var1) {
      this.stopWatch.reset();
      if (!Objects.requireNonNull(mc.player).isCreative()) {
         Slot var2 = this.method264(var1.getBlockPos());
         if (var2 != null && var2 != InventoryUtils.method924()) {
            if (this.slot == null) {
               this.slot = var2;
            }

            if (KeyBindUtils.method2630(AutoToolModule.class)) {
               KeyBindUtils.method2627(AutoToolModule.class, () -> {
                  if (!(mc.player.currentScreenHandler instanceof PlayerScreenHandler)) {
                     InventoryUtils.closeScreen();
                  }

                  InventoryUtils.method900(var2, Hand.MAIN_HAND, true);
               });
            }
         }
      }
   }

   @EventTarget
   public void method263(UpdateEvent var1) {
      if (KeyBindUtils.method2630(AutoToolModule.class) && this.slot != null && this.stopWatch.method317(400.0) && !mc.options.attackKey.isPressed()) {
         Slot var2 = this.slot;
         KeyBindUtils.method2627(AutoToolModule.class, () -> {
            if (!(mc.player.currentScreenHandler instanceof PlayerScreenHandler)) {
               InventoryUtils.closeScreen();
            }

            InventoryUtils.method900(var2, Hand.MAIN_HAND, true);
         });
         this.slot = null;
      }
   }

   @Override
   public void onDisable() {
      if (this.slot != null && mc.player != null) {
         Slot var1 = this.slot;
         KeyBindUtils.method2627(AutoToolModule.class, () -> {
            if (!(mc.player.currentScreenHandler instanceof PlayerScreenHandler)) {
               InventoryUtils.closeScreen();
            }

            InventoryUtils.method900(var1, Hand.MAIN_HAND, true);
         });
      }

      this.slot = null;
      super.onDisable();
   }

   private Slot method264(BlockPos var1) {
      BlockState var2 = mc.world.getBlockState(var1);
      return UseItemUtils.method6524(var2)
         ? InventoryUtils.method924()
         : mc.player
            .playerScreenHandler
            .slots
            .stream()
            .sorted(Comparator.comparing(var0 -> var0.equals(InventoryUtils.method924())))
            .filter(var1x -> var1x.getStack().getMiningSpeedMultiplier(var2) != 1.0F)
            .max(Comparator.comparingDouble(var1x -> var1x.getStack().getMiningSpeedMultiplier(var2)))
            .orElse(null);
   }
}

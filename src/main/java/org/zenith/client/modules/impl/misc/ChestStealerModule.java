package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "ChestStealer",
   category = ModuleCategory.MISC,
   description = "module.cheststealer.description"
)
public final class ChestStealerModule extends Module {
   private final ModeSetting mode = new ModeSetting(
      "module.chestStealer.mode",
      "module.chestStealer.mode.desc",
      "module.chestStealer.mode.funTime",
      "module.chestStealer.mode.holyWorld",
      "module.chestStealer.mode.reallyWorld",
      "module.chestStealer.mode.custom"
   );
   public static final ChestStealerModule INSTANCE = new ChestStealerModule();
   private final SliderSetting startDelay = new SliderSetting(
      "module.chestStealer.startDelay", 4.0F, 0.0F, 60.0F, 1.0F, "module.chestStealer.startDelay.desc", "t"
   );
   private final SliderSetting delay = new SliderSetting("module.chestStealer.delay", 0.0F, 0.0F, 60.0F, 1.0F, "module.chestStealer.delay.desc", "t");
   private final SliderSetting maxDelay = new SliderSetting("module.chestStealer.maxDelay", 6.0F, 0.0F, 60.0F, 1.0F, "module.chestStealer.maxDelay.desc", "t");
   private final BooleanSetting closeScreen = new BooleanSetting("module.chestStealer.closeScreen", "module.chestStealer.closeScreen.desc", true);
   private final SliderSetting closeDelay = new SliderSetting(
      "module.chestStealer.closeDelay", 4.0F, 0.0F, 60.0F, 1.0F, "module.chestStealer.closeDelay.desc", "t"
   );
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private final StopWatch stopWatch3 = new StopWatch();
   private int lastId = -1;
   private boolean flag = false;
   private boolean flag2 = false;

   private ChestStealerModule() {
      this.startDelay.setVisible(() -> this.mode.method2101("module.chestStealer.mode.custom"));
      this.delay.setVisible(() -> this.mode.method2101("module.chestStealer.mode.custom"));
      this.maxDelay.setVisible(() -> this.mode.method2101("module.chestStealer.mode.custom"));
      this.closeDelay.setVisible(() -> this.mode.method2101("module.chestStealer.mode.custom") && this.closeScreen.isEnabled());
   }

   private void resetState() {
      this.lastId = -1;
      this.flag = false;
      this.flag2 = false;
      this.stopWatch2.reset();
      this.stopWatch3.reset();
   }

   private float method3071() {
      if (this.mode.method2101("module.chestStealer.mode.funTime")) {
         return 8.0F;
      } else if (this.mode.method2101("module.chestStealer.mode.holyWorld")) {
         return 1.0F;
      } else {
         return this.mode.method2101("module.chestStealer.mode.reallyWorld") ? 5.0F : this.startDelay.getValue3();
      }
   }

   private float method3072() {
      if (this.mode.method2101("module.chestStealer.mode.funTime")) {
         return 4.0F;
      } else if (this.mode.method2101("module.chestStealer.mode.holyWorld")) {
         return 1.0F;
      } else {
         return this.mode.method2101("module.chestStealer.mode.reallyWorld") ? 1.0F : this.delay.getValue3();
      }
   }

   private float method3073() {
      if (this.mode.method2101("module.chestStealer.mode.funTime")) {
         return 8.0F;
      } else if (this.mode.method2101("module.chestStealer.mode.holyWorld")) {
         return 1.0F;
      } else {
         return this.mode.method2101("module.chestStealer.mode.reallyWorld") ? 4.0F : this.maxDelay.getValue3();
      }
   }

   private float method3074() {
      if (this.mode.method2101("module.chestStealer.mode.funTime")) {
         return 7.0F;
      } else if (this.mode.method2101("module.chestStealer.mode.holyWorld")) {
         return 0.0F;
      } else {
         return this.mode.method2101("module.chestStealer.mode.reallyWorld") ? 2.0F : this.closeDelay.getValue3();
      }
   }

   @EventTarget
   public void method3075(WorldRenderEvent var1) {
      if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var2 && mc.currentScreen != null) {
         if (this.lastId != var2.syncId) {
            this.lastId = var2.syncId;
            this.stopWatch2.reset();
            this.stopWatch3.reset();
            this.stopWatch.reset();
            this.flag = false;
            this.flag2 = false;
         }

         if (!this.flag) {
            if (!this.stopWatch2.method7546((long)this.method3071() * 50L)) {
               return;
            }

            this.flag = true;
         }

         String var14 = mc.currentScreen.getTitle().getString();
         String var4 = Zenith.getInstance().getLocalization().translate("module.chestStealer.title.auction");
         String var5 = Zenith.getInstance().getLocalization().translate("module.chestStealer.title.purchases");
         boolean var6 = var14.contains(var4) || var14.contains(var5);
         int var7 = var2.getInventory().size();
         List<Integer> var8 = IntStream.range(0, var7).boxed().collect(Collectors.toList());
         Collections.shuffle(var8);
         if (!var6) {
            for (Object var10_o : (Iterable<?>)(Object)(var8)) {
               int var10 = (Integer)var10_o;
               Slot var11 = var2.getSlot(var10);
               if (var11.hasStack()) {
                  long var12 = (long)MathUtils.method2651(Math.min(this.method3072(), this.method3073()), Math.max(this.method3072(), this.method3073()));
                  if (this.stopWatch.method7546(var12 * 50L)) {
                     mc.interactionManager.clickSlot(var2.syncId, var10, 0, SlotActionType.QUICK_MOVE, mc.player);
                     this.stopWatch.reset();
                     this.flag2 = false;
                     break;
                  }
               }
            }
         }

         boolean var15 = this.method3076(var2);
         if (this.closeScreen.isEnabled() && var15) {
            if (!this.flag2) {
               this.flag2 = true;
               this.stopWatch3.reset();
               return;
            }

            if (this.stopWatch3.method7546((long)this.method3074() * 50L)) {
               mc.player.closeHandledScreen();
               this.flag2 = false;
            }
         } else {
            this.flag2 = false;
         }
      } else {
         this.resetState();
      }
   }

   private boolean method3076(GenericContainerScreenHandler var1) {
      for (int var2 = 0; var2 < (var1.getInventory().size() == 90 ? 54 : 27); var2++) {
         if (var1.getSlot(var2).hasStack()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.resetState();
      this.stopWatch.reset();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.resetState();
      this.stopWatch.reset();
   }
}

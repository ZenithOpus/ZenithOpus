package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;

@ModuleInfo(
   name = "AutoLeave",
   category = ModuleCategory.MISC,
   description = "module.autoleave.description"
)
public final class AutoLeaveModule extends Module {
   public static final AutoLeaveModule INSTANCE = new AutoLeaveModule();
   private final MultiSelectSetting factor = MultiSelectSetting.method5224(
      "leave.factor", "module.autoLeave.leaveFactor.desc", List.of("leave.health", "leave.players", "leave.distance")
   );
   private final SliderSetting health = new SliderSetting(
      "leave.health", 15.0F, 1.0F, 20.0F, 1.0F, "module.autoLeave.minHealth.desc", "hp", () -> this.factor.method5227(0), null
   );
   private final SliderSetting leaveRadius = new SliderSetting(
      "module.autoLeave.leaveRadius", 300.0F, 3.0F, 300.0F, 10.0F, "module.autoLeave.leaveRadius.desc", "b", () -> this.factor.method5227(2), null
   );
   private final SliderSetting time = new SliderSetting("leave.time", 0.0F, 0.0F, 200.0F, 10.0F, "module.autoLeave.leaveTime.desc", "t");
   private final TextSetting command = new TextSetting("leave.command", "module.autoLeave.command.desc", "/hub", "leave.command.description");
   private int intValue = 0;

   private AutoLeaveModule() {
   }

   @Override
   public void onEnable() {
      this.intValue = -1;
      super.onEnable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (this.method2709()) {
         this.intValue++;
      } else {
         this.intValue = -1;
      }

      if (this.intValue >= this.time.getValue3()) {
         if (this.command.getValue().startsWith("/")) {
            mc.player.networkHandler.sendChatCommand(this.command.getValue().substring(1));
         } else {
            mc.player.networkHandler.sendChatMessage(this.command.getValue());
         }

         this.toggle();
      }
   }

   private boolean method2709() {
      if (mc.player.getHealth() < this.health.getValue3() && this.factor.method5227(0)) {
         return true;
      } else {
         boolean var1 = this.factor.method5227(1);
         boolean var2 = this.factor.method5227(2);
         if (!var1 && !var2) {
            return false;
         } else {
            for (PlayerEntity var4 : mc.world.getPlayers()) {
               if (!Zenith.getInstance().getFriends().method7626(var4)
                  && var4 != mc.player
                  && (!var2 || !(var4.squaredDistanceTo(mc.player) > this.leaveRadius.getValue3() * this.leaveRadius.getValue3()))) {
                  return true;
               }
            }

            return false;
         }
      }
   }
}

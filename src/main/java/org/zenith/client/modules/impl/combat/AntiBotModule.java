package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.AttackEvent;

@ModuleInfo(
   name = "AntiBot",
   category = ModuleCategory.COMBAT,
   description = "module.antibot.description"
)
public final class AntiBotModule extends Module {
   public static final AntiBotModule INSTANCE = new AntiBotModule();
   private final List<PlayerEntity> items = new ArrayList<>();
   private final StopWatch stopWatch = new StopWatch();
   private final Set<Integer> items2 = HashSet.newHashSet(4);

   private AntiBotModule() {
   }

   @EventTarget
   public void method2784(AttackEvent var1) {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (!this.items.isEmpty()) {
         this.items.clear();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (!this.items.isEmpty()) {
         this.items.clear();
      }
   }

   public boolean method2785(PlayerEntity var1) {
      if (!this.isEnabled()) {
         return false;
      } else if (mc.player == null) {
         return false;
      } else {
         double var2 = mc.player.distanceTo(var1);
         if (var2 < 8.0) {
            if (mc.getNetworkHandler() == null) {
               return false;
            } else {
               PlayerListEntry var4 = mc.getNetworkHandler().getPlayerListEntry(var1.getUuid());
               if (var4 == null) {
                  return true;
               } else {
                  return var4.getDisplayName() == null ? true : var4.getDisplayName().getString().equals(var1.getGameProfile().name());
               }
            }
         } else {
            return false;
         }
      }
   }
}

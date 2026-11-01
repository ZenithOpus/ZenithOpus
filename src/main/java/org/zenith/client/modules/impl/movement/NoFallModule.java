package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.PacketUtils;
import org.zenith.utility.misc.Util13;

@ModuleInfo(
   name = "NoFall",
   description = "module.nofall.description",
   category = ModuleCategory.MOVEMENT
)
public final class NoFallModule extends Module {
   public static final NoFallModule INSTANCE = new NoFallModule();
   private static final double CONST_doubleValue = 3.0;
   private final ModeSetting mode = new ModeSetting("module.noFall.mode", "module.noFall.mode.desc", "module.noFall.mode.grim72", "module.noFall.mode.grim73");
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private boolean falling;
   private boolean flag;
   private int intValue;
   private boolean flag2;

   private NoFallModule() {
   }

   @Override
   public void onEnable() {
      this.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.reset();
      super.onDisable();
   }

   private void reset() {
      this.falling = false;
      this.flag = false;
      this.intValue = 0;
      if (this.flag2) {
         Util13.method2404(1.0F);
         this.flag2 = false;
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null) {
         this.falling = mc.player.fallDistance > 3.0;
         if (this.method1734()) {
            if (this.falling && !mc.player.isOnGround() && this.stopWatch2.method7546(200L) && PlayerSimulation.method6031(1).flag4) {
               PacketUtils.method3150(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
               PacketUtils.method3150(new ClientCommandC2SPacket(mc.player, Mode.START_FALL_FLYING));
               this.stopWatch2.reset();
               this.stopWatch.reset();
               this.flag = true;
            }
         } else if (this.intValue == 2) {
            Util13.method2404(0.5F);
            this.flag2 = true;
         } else if (this.flag2) {
            Util13.method2404(1.0F);
            this.flag2 = false;
         }

         if (this.intValue > 0) {
            this.intValue--;
         }

         if (this.stopWatch.method7546(300L)) {
            this.flag = false;
            this.intValue = 0;
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (mc.player != null) {
         if (var1.method7099()) {
            if (this.method1735()
               && var1.getPacket3() instanceof PlayerMoveC2SPacket var2
               && !PacketUtils.getItems().contains(var2)
               && this.falling
               && var2.isOnGround()
               && this.intValue == 0) {
               PacketUtils.method3150(new OnGroundOnly(true, false));
               this.stopWatch.reset();
               this.flag = true;
            }
         } else if (!(var1.getPacket3() instanceof GameJoinS2CPacket) && !(var1.getPacket3() instanceof PlayerRespawnS2CPacket)) {
            if (var1.getPacket3() instanceof PlayerPositionLookS2CPacket) {
               mc.execute(() -> {
                  if (this.flag) {
                     this.intValue = 2;
                     this.flag = false;
                  }
               });
            }
         } else {
            mc.execute(this::reset);
         }
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent var1) {
      if (this.flag || this.intValue == 2) {
         var1.method3041(false);
      } else if (this.intValue == 1) {
         var1.method3041(true);
      }
   }

   private boolean method1734() {
      return this.mode.isSelected(0);
   }

   private boolean method1735() {
      return this.mode.isSelected(1);
   }
}

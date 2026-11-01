package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Generated;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.PacketProcessEndEvent;
import org.zenith.client.modules.impl.combat.ReachV3Module;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "Velocity",
   category = ModuleCategory.MOVEMENT,
   description = "module.velocity.description"
)
public final class VelocityModule extends Module {
   public static final VelocityModule INSTANCE = new VelocityModule();
   private static final long CONST_longValue = 2000L;
   private final SliderSetting delay = new SliderSetting("module.backtrack.delay", 300.0F, 50.0F, 1000.0F, 50.0F, "module.backtrack.delay.desc", "ms");
   private final Queue<VelocityModule.Entry> queue = new ConcurrentLinkedQueue<>();
   private final StopWatch stopWatch = new StopWatch();
   private boolean flag;
   private boolean flag2;
   private long longValue;
   private final AtomicBoolean atomicBoolean = new AtomicBoolean();

   private VelocityModule() {
   }

   @Override
   public void onEnable() {
      this.flag2 = false;
      if (ReachV3Module.INSTANCE.isEnabled()) {
         ChatLogger.method2019("Не работает с ReachV3");
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.flag2 = true;
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (ReachV3Module.INSTANCE.isEnabled()) {
         if (this.atomicBoolean.compareAndSet(false, true)) {
            mc.execute(() -> {
               this.atomicBoolean.set(false);
               if (this.isEnabled() && ReachV3Module.INSTANCE.isEnabled()) {
                  ChatLogger.method2019("Не работает с ReachV3");
                  this.setToggled(false);
               }
            });
         }
      } else if (var1.method7100()) {
         try {
            Packet var2 = var1.getPacket3();
            if (var2 instanceof DisconnectS2CPacket) {
               this.method2625();
               return;
            }

            if (var2 instanceof PlayerPositionLookS2CPacket) {
               this.method2625();
               this.longValue = System.currentTimeMillis() + 2000L;
               return;
            }

            if (mc.player == null || mc.world == null) {
               return;
            }

            if (System.currentTimeMillis() < this.longValue) {
               return;
            }

            if (!this.flag) {
               if (!this.method2626(var2) || mc.player.isOnGround()) {
                  return;
               }

               this.flag = true;
               this.stopWatch.reset();
            }

            if (var2 instanceof ChatMessageC2SPacket
               || var2 instanceof HealthUpdateS2CPacket
               || var2 instanceof PlaySoundS2CPacket
               || var2 instanceof GameMessageS2CPacket
               || var2 instanceof CommandExecutionC2SPacket) {
               return;
            }

            var1.cancel();
            this.queue.add(new VelocityModule.Entry(var2));
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }
   }

   @EventTarget
   public void method2624(PacketProcessEndEvent var1) {
      if (mc.world == null || mc.player == null) {
         this.queue.clear();
         this.flag = false;
      }

      if (this.flag && !this.queue.isEmpty()) {
         boolean var2 = mc.player != null && mc.player.isOnGround();
         boolean var3 = this.stopWatch.method7546((long)this.delay.getValue3());
         if (var2 || var3) {
            this.method2625();
         }
      }

      if (this.flag2) {
         this.flag2 = false;
         this.method2625();
         super.onDisable();
      }
   }

   private void method2625() {
      while (!this.queue.isEmpty()) {
         VelocityModule.Entry var1 = this.queue.poll();
         if (var1 != null) {
            try {
               var1.getPacket().apply(mc.getNetworkHandler());
            } catch (Throwable var3) {
               if (!(var3 instanceof ClassCastException) && !(var3 instanceof OffThreadException)) {
                  var3.printStackTrace();
               }
            }
         }
      }

      this.flag = false;
   }

   private boolean method2626(Packet<?> var1) {
      return mc.player != null && var1 instanceof EntityVelocityUpdateS2CPacket var2 && var2.getEntityId() == mc.player.getId();
   }

   public static class Entry {
      private final Packet<?> packet;
      private final long longValue;

      public Entry(Packet<?> var1) {
         this.packet = var1;
         this.longValue = System.currentTimeMillis();
      }

      public Packet<ClientPlayPacketListener> getPacket() {
         return (Packet<ClientPlayPacketListener>)this.packet;
      }

      @Generated
      public Packet<?> getPacket2() {
         return this.packet;
      }

      @Generated
      public long getTime() {
         return this.longValue;
      }
   }
}

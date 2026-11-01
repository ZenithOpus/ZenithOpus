package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.PacketProcessEndEvent;
import org.zenith.client.events.PlayerTickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.render.GlowUtil;

@ModuleInfo(
   name = "ReachV3",
   description = "module.backtrack.description",
   category = ModuleCategory.COMBAT
)
public final class ReachV3Module extends Module {
   public static final ReachV3Module INSTANCE = new ReachV3Module();
   private final SliderSetting delay = new SliderSetting("module.backtrack.delay", 300.0F, 50.0F, 1000.0F, 50.0F, "module.backtrack.delay.desc", "ms");
   private final BooleanSetting renderBox = new BooleanSetting("module.backtrack.renderBox", "module.backtrack.renderBox.desc", true);
   private static final int CONST_intValue = Integer.MIN_VALUE;
   private final Queue<ReachV3Module.Entry> queue = new ConcurrentLinkedQueue<>();
   private Vec3d vec3d = null;
   private volatile int intValue = -32768 << 16 | 0;
   private Vec3d vec3d2 = null;
   private StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private final StopWatch stopWatch3 = new StopWatch();
   private Box box = null;

   @Override
   public void onDisable() {
      super.onDisable();
      mc.execute(this::releaseAll);
   }

   private void releaseAll() {
      if (mc.getNetworkHandler() == null) {
         this.queue.clear();
      }

      ReachV3Module.Entry var1;
      while ((var1 = this.queue.poll()) != null) {
         this.method5536(var1);
      }

      this.method5537();
   }

   private void method5536(ReachV3Module.Entry var1) {
      try {
         var1.getPacket().apply(mc.getNetworkHandler());
      } catch (ClassCastException var3) {
      } catch (Throwable var4) {
         var4.printStackTrace();
      }
   }

   private void method5537() {
      this.vec3d = null;
      this.intValue = -32768 << 16 | 0;
      this.box = null;
      this.vec3d2 = null;
   }

   private void method5538(LivingEntity var1) {
      Vec3d var2 = var1.getTrackedPosition().getPos();

      for (ReachV3Module.Entry var4 : this.queue) {
         Vec3d var5 = this.method5539(var4.getPacket(), var1, var2);
         if (var5 != null) {
            var2 = var5;
         }
      }

      this.vec3d = var2;
      this.vec3d2 = var2;
      this.intValue = var1.getId();
   }

   private Vec3d method5539(Packet<?> var1, Entity var2, Vec3d var3) {
      int var4 = var2.getId();
      if (var1 instanceof EntityPositionS2CPacket var5 && var5.entityId() == var4) {
         return var5.change().position();
      } else if (var1 instanceof EntityPositionSyncS2CPacket var6 && var6.id() == var4) {
         return var6.values().position();
      } else {
         return var1 instanceof EntityS2CPacket var7 && var7.id == var4
            ? var3.add(var7.getDeltaX() / 4096.0, var7.getDeltaY() / 4096.0, var7.getDeltaZ() / 4096.0)
            : null;
      }
   }

   public void method5540(Vec3d var1) {
      if (var1 != null && !this.queue.isEmpty()) {
         for (ReachV3Module.Entry var3 : this.queue) {
            var3.setFlag(true);
            if (var3.getVec3d() != null && var3.getVec3d().equals(var1)) {
               break;
            }
         }
      }
   }

   @EventTarget
   public void method5541(UpdateEvent var1) {
      LivingEntity var2 = this.method5542();
      if (var2 == null) {
         this.method5537();
      } else {
         if (this.vec3d == null || var2.getId() != this.intValue || this.stopWatch.method7546(1000L)) {
            this.method5538(var2);
         }

         Vec3d var3 = this.vec3d;
         this.box = var3 != null ? var2.dimensions.getBoxAt(var3) : null;
      }
   }

   private LivingEntity method5542() {
      return TriggerBotModule.INSTANCE.method5598() != null ? TriggerBotModule.INSTANCE.method5598() : AuraModule.INSTANCE.method6358();
   }

   @EventTarget
   public void method5543(PlayerTickEvent var1) {
      LivingEntity var2 = this.method5542();
      if (mc.player != null && var2 != null) {
         int var3 = var2.getId();
         mc.player.getCameraPosVec(1.0F);
         mc.player.getBoundingBox();
         ReachV3Module.Entry var7 = null;
         int var8 = 0;

         for (ReachV3Module.Entry var10 : this.queue) {
            if (var10.getVec3d() != null && var10.getIntValue() == var3 && var2.dimensions.getBoxAt(var10.getVec3d()).intersects(mc.player.getBoundingBox())) {
               var7 = var10;
               if (var8 > 0) {
                  break;
               }

               var8++;
            }
         }

         ReachV3Module.Entry var6;
         if (var7 != null) {
            var6 = var7;
         } else {
            double var16 = Double.POSITIVE_INFINITY;
            ReachV3Module.Entry var11 = null;

            for (ReachV3Module.Entry var13 : this.queue) {
               double var14;
               if (var13.getVec3d() != null && var13.getIntValue() == var3 && (var14 = mc.player.squaredDistanceTo(var13.vec3d)) < var16) {
                  var16 = var14;
                  var11 = var13;
               }
            }

            var6 = var11;
         }

         Vec3d var17 = var6 != null ? var6.getVec3d() : null;
         this.vec3d2 = var17;
         this.method5540(var17);
      } else {
         this.vec3d2 = null;
      }
   }

   @EventTarget
   public void method5544(WorldRenderEvent var1) {
      if (this.box != null && this.renderBox.isEnabled()) {
         GlowUtil.method2230(this.box, -1, 1.0F);
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100()) {
         if (this.stopWatch == null) {
            this.stopWatch = new StopWatch();
         }

         try {
            Packet var2 = var1.getPacket3();
            if (var2 instanceof DisconnectS2CPacket || var2 instanceof PlayerPositionLookS2CPacket) {
               this.stopWatch2.reset();
               this.stopWatch3.reset();
            }

            LivingEntity var3 = mc.world == null ? null : this.method5542();
            Vec3d var4 = null;
            if (var3 != null) {
               if (var3.getId() != this.intValue || this.vec3d == null) {
                  this.method5538(var3);
               }

               Vec3d var5 = this.vec3d;
               Vec3d var6 = var5 == null ? null : this.method5539(var2, var3, var5);
               if (var6 != null) {
                  this.vec3d = var6;
                  var4 = var6;
                  this.stopWatch.reset();
               }
            }

            if (this.queue.isEmpty() && (!this.stopWatch2.method7546(200L) || mc.world == null || var3 == null || !this.stopWatch3.method7546(200L))) {
               return;
            }

            if (var2 instanceof ChatMessageC2SPacket
               || var2 instanceof HealthUpdateS2CPacket
               || var2 instanceof PlaySoundS2CPacket
               || var2 instanceof GameMessageS2CPacket
               || var2 instanceof CommandExecutionC2SPacket) {
               return;
            }

            var1.cancel();
            this.queue.add(new ReachV3Module.Entry(var2, var4, var4 != null ? var3.getId() : -32768 << 16 | 0));
            if (!this.isEnabled()) {
               mc.execute(this::releaseAll);
            }
         } catch (Exception var7) {
            var7.printStackTrace();
         }
      }
   }

   @EventTarget
   public void method5545(PacketProcessEndEvent var1) {
      long var2 = System.currentTimeMillis();
      if (mc.world == null || mc.player == null) {
         this.queue.clear();
      }

      try {
         this.queue.removeIf(var3 -> {
            boolean var4 = (float)(var2 - var3.getTime()) >= this.delay.getValue3();
            boolean var5x = var3.isFlag();
            if (!var4 && this.stopWatch2.method7546(200L) && !var5x && mc.world != null && this.method5542() != null) {
               return false;
            } else {
               this.method5536(var3);
               return true;
            }
         });
      } catch (Exception var5) {
         var5.printStackTrace();
      }
   }

   @Generated
   public Vec3d getVec3d() {
      return this.vec3d;
   }

   @Generated
   public void setVec3d(Vec3d var1) {
      this.vec3d = var1;
   }

   @Generated
   public Vec3d getVec3d2() {
      return this.vec3d2;
   }

   public static class Entry {
      private final Packet<?> packet;
      private final long longValue;
      private final Vec3d vec3d;
      private final int intValue;
      private boolean flag;

      public Entry(Packet<?> var1, Vec3d var2) {
         this(var1, var2, Integer.MIN_VALUE);
      }

      public Entry(Packet<?> var1, Vec3d var2, int var3) {
         this.packet = var1;
         this.longValue = System.currentTimeMillis();
         this.vec3d = var2;
         this.intValue = var3;
         this.flag = false;
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

      @Generated
      public Vec3d getVec3d() {
         return this.vec3d;
      }

      @Generated
      public int getIntValue() {
         return this.intValue;
      }

      @Generated
      public boolean isFlag() {
         return this.flag;
      }

      @Generated
      public void setFlag(boolean var1) {
         this.flag = var1;
      }
   }
}

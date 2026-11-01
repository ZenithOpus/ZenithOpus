package org.zenith.base.comand.impl;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.render.TotemParticlesModule;
import org.zenith.utility.game.Npc;

public class NpcCommand extends CommandAbstract {
   private static final double SPAWN_DISTANCE = 3.0;
   private Npc npc;
   private Vec3d anchor;
   private int nextEntityId = Integer.MAX_VALUE;

   public NpcCommand() {
      super("npc");
      EventManager.register(this);
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)literal("spawn").then(literal("naked").executes(var1x -> {
         this.spawn(false);
         return 1;
      }))).then(literal("wear").executes(var1x -> {
         this.spawn(true);
         return 1;
      }))).executes(var1x -> {
         this.spawn(true);
         return 1;
      }));
      var1.then(literal("kill").executes(var1x -> {
         this.kill();
         return 1;
      }));
      var1.then(literal("totem").executes(var1x -> {
         this.totem();
         return 1;
      }));
      var1.executes(var0 -> {
         ChatLogger.method2019("Использование: .npc spawn [wear|naked], .npc totem и .npc kill");
         return 1;
      });
   }

   private void totem() {
      if (mc.world == null || mc.player == null) {
         ChatLogger.method2019("Тотем можно разыграть только в мире");
      } else if (!TotemParticlesModule.INSTANCE.isEnabled()) {
         ChatLogger.method2019("Раздел Totem выключен: Particles -> Тотем");
      } else {
         Object var1 = this.npc != null ? this.npc : mc.player;
         TotemParticlesModule.INSTANCE.method4697((Entity)var1);
         ChatLogger.method2019(this.npc != null ? "Тотем разыгран на манекене" : "Тотем разыгран на тебе");
      }
   }

   private void spawn(boolean var1) {
      if (mc.world != null && mc.player != null) {
         if (this.npc != null) {
            this.remove(RemovalReason.DISCARDED);
         }

         float var2 = mc.player.getYaw();
         Vec3d var3 = mc.player.getEntityPos().add(Vec3d.fromPolar(0.0F, var2).multiply(3.0));
         float var4 = var2 + 180.0F;
         this.npc = new Npc(mc.world, new GameProfile(UUID.randomUUID(), "NPC"));
         this.npc.setId(this.nextEntityId());
         this.npc.refreshPositionAndAngles(var3, var4, 0.0F);
         this.npc.setHeadYaw(var4);
         this.npc.bodyYaw = var4;
         this.npc.lastBodyYaw = var4;
         this.npc.lastHeadYaw = var4;
         this.npc.setHealth(this.npc.getMaxHealth());
         if (var1) {
            equipNetherite(this.npc);
         }

         mc.world.addEntity(this.npc);
         this.anchor = var3;
         ChatLogger.method2019("Манекен поставлен, убрать: .npc kill");
      } else {
         ChatLogger.method2019("Манекен можно поставить только в мире");
      }
   }

   private static void equipNetherite(Npc var0) {
      var0.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
      var0.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
      var0.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS));
      var0.equipStack(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));
   }

   private void kill() {
      if (this.npc == null) {
         ChatLogger.method2019("Манекена нет");
      } else {
         this.remove(RemovalReason.KILLED);
         ChatLogger.method2019("Манекен убран");
      }
   }

   private void remove(RemovalReason var1) {
      if (this.npc != null) {
         if (mc.world != null && mc.world.getEntityById(this.npc.getId()) == this.npc) {
            mc.world.removeEntity(this.npc.getId(), var1);
         } else {
            this.npc.setRemoved(var1);
            this.npc.onRemoved();
         }

         this.npc = null;
         this.anchor = null;
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (this.npc != null) {
         if (mc.world != null && mc.world.getEntityById(this.npc.getId()) == this.npc) {
            this.npc.setHealth(this.npc.getMaxHealth());
            this.npc.deathTime = 0;
            this.npc.hurtTime = Math.min(this.npc.hurtTime, 10);
            this.npc.fallDistance = 0.0;
            this.npc.setVelocity(Vec3d.ZERO);
            this.npc.velocityDirty = false;
            if (this.anchor != null) {
               this.npc.setPosition(this.anchor);
               this.npc.updateTrackedPosition(this.anchor.x, this.anchor.y, this.anchor.z);
            }
         } else {
            this.npc = null;
            this.anchor = null;
         }
      }
   }

   private int nextEntityId() {
      for (int var1 = 0; var1 < 1000000; var1++) {
         if (this.nextEntityId <= 2146483647) {
            this.nextEntityId = Integer.MAX_VALUE;
         }

         int var2 = --this.nextEntityId;
         if (mc.world == null || mc.world.getEntityById(var2) == null) {
            return var2;
         }
      }

      return Integer.MAX_VALUE - ThreadLocalRandom.current().nextInt(1, 1000000);
   }
}

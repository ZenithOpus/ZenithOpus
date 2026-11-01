package org.zenith.client.scripting;

import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Vec3d;
import org.mozilla.javascript.Scriptable;

public final class ScriptEntityEvents {
   private ScriptEntityEvents() {
   }

   public static String method7557(Packet<?> var0) {
      return var0 == null ? "" : var0.getPacketType().id().getPath();
   }

   public static Object method7558(Scriptable var0, Packet<?> var1) {
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6247(var0);
      MinecraftClient var3 = ScriptEntityApi.method6575();
      switch (var1) {
         case EntityVelocityUpdateS2CPacket var6:
            Vec3d var30 = var6.getVelocity();
            var2.method6277("entityId", (double)var6.getEntityId()).method6277("velocity", ScriptValueUtils.method6270(var0, var30.x, var30.y, var30.z));
            method7559(var0, var2, var6.getEntityId());
            break;
         case HealthUpdateS2CPacket var7:
            var2.method6277("health", (double)var7.getHealth())
               .method6277("food", (double)var7.getFood())
               .method6277("saturation", (double)var7.getSaturation());
            break;
         case EntityStatusS2CPacket var8:
            var2.method6277("status", (double)var8.getStatus());
            if (var3.world != null) {
               Entity var31 = var8.getEntity(var3.world);
               if (var31 != null) {
                  var2.method6277("entityId", (double)var31.getId()).method6277("entity", ScriptEntityApi.method6576(var0, var31));
               }
            }
            break;
         case GameMessageS2CPacket var9:
            var2.method6277("text", var9.content().getString()).method6277("overlay", var9.overlay());
            break;
         case TitleS2CPacket var10:
            var2.method6277("text", var10.text().getString());
            break;
         case SubtitleS2CPacket var11:
            var2.method6277("text", var11.text().getString());
            break;
         case PlayerPositionLookS2CPacket var12:
            Vec3d var32 = var12.change().position();
            var2.method6277("teleportId", (double)var12.teleportId())
               .method6277("x", var32.x)
               .method6277("y", var32.y)
               .method6277("z", var32.z)
               .method6277("yaw", (double)var12.change().yaw())
               .method6277("pitch", (double)var12.change().pitch());
            break;
         case UpdateSelectedSlotS2CPacket var13:
            var2.method6277("slot", (double)var13.slot());
            break;
         case ScreenHandlerSlotUpdateS2CPacket var14:
            var2.method6277("syncId", (double)var14.getSyncId())
               .method6277("slot", (double)var14.getSlot())
               .method6277("item", ScriptEntityApi.method6579(var0, var14.getStack()));
            break;
         case OpenScreenS2CPacket var15:
            var2.method6277("syncId", (double)var15.getSyncId()).method6277("title", var15.getName().getString());
            break;
         case PlaySoundS2CPacket var16:
            var2.method6277("sound", var16.getSound().getIdAsString())
               .method6277("x", var16.getX())
               .method6277("y", var16.getY())
               .method6277("z", var16.getZ())
               .method6277("volume", (double)var16.getVolume())
               .method6277("pitch", (double)var16.getPitch());
            break;
         case EntityAnimationS2CPacket var17:
            var2.method6277("entityId", (double)var17.getEntityId()).method6277("animation", (double)var17.getAnimationId());
            method7559(var0, var2, var17.getEntityId());
            break;
         case BlockUpdateS2CPacket var18:
            var2.method6277("x", (double)var18.getPos().getX())
               .method6277("y", (double)var18.getPos().getY())
               .method6277("z", (double)var18.getPos().getZ())
               .method6277("block", Registries.BLOCK.getId(var18.getState().getBlock()).toString());
            break;
         case ExplosionS2CPacket var19:
            Vec3d var33 = var19.center();
            var2.method6277("x", var33.x).method6277("y", var33.y).method6277("z", var33.z).method6277("radius", (double)var19.radius());
            var19.playerKnockback().ifPresent(var2x -> var2.method6277("knockback", ScriptValueUtils.method6270(var0, var2x.x, var2x.y, var2x.z)));
            break;
         case PlayerMoveC2SPacket var20:
            ClientPlayerEntity var34 = var3.player;
            double var35 = var34 == null ? 0.0 : var34.getX();
            double var36 = var34 == null ? 0.0 : var34.getY();
            double var37 = var34 == null ? 0.0 : var34.getZ();
            float var28 = var34 == null ? 0.0F : var34.getYaw();
            float var29 = var34 == null ? 0.0F : var34.getPitch();
            var2.method6277("x", var20.getX(var35))
               .method6277("y", var20.getY(var36))
               .method6277("z", var20.getZ(var37))
               .method6277("yaw", (double)var20.getYaw(var28))
               .method6277("pitch", (double)var20.getPitch(var29))
               .method6277("onGround", var20.isOnGround())
               .method6277("changesPosition", var20.changesPosition())
               .method6277("changesLook", var20.changesLook());
            break;
         case ClickSlotC2SPacket var21:
            var2.method6277("syncId", (double)var21.syncId())
               .method6277("slot", (double)var21.slot())
               .method6277("button", (double)var21.button())
               .method6277("action", var21.actionType().name().toLowerCase(Locale.ROOT));
            break;
         case UpdateSelectedSlotC2SPacket var22:
            var2.method6277("slot", (double)var22.getSelectedSlot());
            break;
         case ChatMessageC2SPacket var23:
            var2.method6277("text", var23.chatMessage());
            break;
         case CommandExecutionC2SPacket var24:
            var2.method6277("command", var24.command());
            break;
         case HandSwingC2SPacket var25:
            var2.method6277("hand", var25.getHand().name().toLowerCase(Locale.ROOT));
            break;
         case PlayerActionC2SPacket var26:
            var2.method6277("action", var26.getAction().name().toLowerCase(Locale.ROOT))
               .method6277("x", (double)var26.getPos().getX())
               .method6277("y", (double)var26.getPos().getY())
               .method6277("z", (double)var26.getPos().getZ())
               .method6277("side", var26.getDirection().asString());
            break;
         default:
            return null;
      }

      return var2.getScriptableObject();
   }

   private static void method7559(Scriptable var0, ScriptValueUtils.Entry2 var1, int var2) {
      if (ScriptEntityApi.method6575().world != null) {
         Entity var3 = ScriptEntityApi.method6575().world.getEntityById(var2);
         if (var3 != null) {
            var1.method6277("entity", ScriptEntityApi.method6576(var0, var3));
         }
      }
   }
}

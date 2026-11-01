package org.zenith.base.bot.modules.impl;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Locale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity;
import net.minecraft.text.Text;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.bot.client.BotRctService;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.client.events.BotUpdateEvent;
import org.zenith.client.modules.impl.player.WarpFarmModule;
import org.zenith.utility.game.Pathfinder;
import org.zenith.utility.misc.ViaCompat;

@ModuleInfo(
   name = "BotWarpFarm",
   category = ModuleCategory.PLAYER,
   description = "module.botwarpfarm.description"
)
public class BotWarpFarm extends BotModule {
   private static final int WARP_COOLDOWN_TICKS = 100;
   private static final int SETTLE_TICKS = 80;
   private static final int RCT_RETRY_COOLDOWN_TICKS = 40;
   private static final int PATH_FAIL_LIMIT = 5;
   private int warpCooldownTicks;
   private int settleTicks;
   private int rctRetryCooldownTicks;
   private int pathFailStreak;
   private int lastAnarchy = Integer.MIN_VALUE;
   private long observedEvacuationSequence;
   private BlockPos walkTarget;
   private BlockPos nextRouteBlock;
   private BotWorld world;
   private BotPlayer player;

   @Override
   public void onEnable() {
      this.resetState();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.world = this.bot().getWorld();
      this.player = this.bot().getPlayer();
      this.stopWalking();
      this.applyInput(false, false);
      this.resetState();
      super.onDisable();
   }

   @EventTarget
   public void onBotUpdate(BotUpdateEvent var1) {
      this.world = var1.getWorld();
      this.player = var1.getPlayer();
      BotPlayHandler var2 = this.handler();
      if (this.world != null && this.player != null && var2 != null) {
         if (!this.handleEmergencyEvacuation(var2)) {
            this.tickCooldowns();
            boolean var3 = this.tickFarm(var2);
            if (var3 && this.nextRouteBlock != null) {
               this.rotateToRouteBlock();
               this.applyRouteMovement();
            } else {
               this.applyInput(false, false);
            }
         }
      }
   }

   private boolean tickFarm(BotPlayHandler var1) {
      if (this.player.isDead() || this.player.getHealth() <= 0.0F) {
         this.stopWalking();
         return false;
      } else if (var1.hasOpenScreen()) {
         return false;
      } else {
         int var2 = WarpFarmModule.getIntValue17();
         if (var2 == -1) {
            this.stopWalking();
            return false;
         } else if (this.rct().isActive()) {
            this.stopWalking();
            return false;
         } else {
            int var3 = this.rct().currentAnarchyHere();
            if (var3 != this.lastAnarchy) {
               this.lastAnarchy = var3;
               this.settleTicks = 80;
               this.stopWalking();
            }

            if (var3 != var2) {
               this.stopWalking();
               if (this.rctRetryCooldownTicks > 0) {
                  return false;
               } else {
                  this.rctRetryCooldownTicks = 40;
                  this.bot().systemMessage("BotWarpFarm: иду на анку №" + var2 + " за маином");
                  this.rct().reconnect(var2);
                  return false;
               }
            } else {
               double var4 = this.horizontalDistanceSq(Vec3d.ofCenter(WarpFarmModule.blockPos));
               if (var4 > 40000.0) {
                  this.stopWalking();
                  this.sendWarpPvp(var1);
                  return false;
               } else if (this.settleTicks > 0) {
                  this.stopWalking();
                  return false;
               } else {
                  Entity var6 = this.findHologram();
                  if (var6 != null) {
                     this.setWalkTarget(var6.getBlockPos());
                  } else {
                     if (!(var4 > 4.0)) {
                        this.stopWalking();
                        return false;
                     }

                     this.setWalkTarget(WarpFarmModule.blockPos);
                  }

                  this.updateRoute(var1);
                  return true;
               }
            }
         }
      }
   }

   private boolean handleEmergencyEvacuation(BotPlayHandler var1) {
      long var2 = WarpFarmModule.method4114();
      if (var2 == this.observedEvacuationSequence) {
         return false;
      } else {
         this.stopWalking();
         this.applyInput(false, false);
         if (!this.player.isDead() && !(this.player.getHealth() <= 0.0F)) {
            this.observedEvacuationSequence = var2;
            this.rct().stop();
            var1.sendCommand("hub");
            this.settleTicks = 80;
            return true;
         } else {
            return true;
         }
      }
   }

   private void tickCooldowns() {
      if (this.warpCooldownTicks > 0) {
         this.warpCooldownTicks--;
      }

      if (this.settleTicks > 0) {
         this.settleTicks--;
      }

      if (this.rctRetryCooldownTicks > 0) {
         this.rctRetryCooldownTicks--;
      }
   }

   private void sendWarpPvp(BotPlayHandler var1) {
      if (this.warpCooldownTicks <= 0) {
         var1.sendCommand("warp pvp");
         this.warpCooldownTicks = 100;
         this.settleTicks = 80;
      }
   }

   private void setWalkTarget(BlockPos var1) {
      if (!var1.equals(this.walkTarget)) {
         this.walkTarget = var1;
         this.nextRouteBlock = null;
      }
   }

   private void updateRoute(BotPlayHandler var1) {
      this.nextRouteBlock = null;
      if (this.walkTarget != null) {
         BlockPos var2 = WarpFarmModule.method4126(this.world, this.player.getEntityPos(), this.player.getBlockPos());
         Pathfinder.Entry2 var3 = Pathfinder.method759(this.world, var2, this.walkTarget, WarpFarmModule.data).orElse(null);
         if (var3 == null) {
            if (!this.player.isOnGround()) {
               this.nextRouteBlock = this.walkTarget;
               this.pathFailStreak = 0;
            } else {
               if (++this.pathFailStreak >= 5) {
                  this.pathFailStreak = 0;
                  this.stopWalking();
                  this.sendWarpPvp(var1);
               }
            }
         } else {
            this.pathFailStreak = 0;
            this.nextRouteBlock = WarpFarmModule.method4127(var3, this.player.getEntityPos());
         }
      }
   }

   private void rotateToRouteBlock() {
      Vec3d var1 = Pathfinder.method761(this.nextRouteBlock);
      Vec3d var2 = this.player.getCameraPosVec(1.0F);
      Rotation var3 = Rotation.method3128(new Vec3d(var1.x, var2.y, var1.z), var2);
      this.player.setYaw(MathHelper.wrapDegrees(var3.getFloatValue()));
      this.player.setPitch(MathHelper.clamp(var3.getFloatValue2(), -90.0F, 90.0F));
   }

   private void applyRouteMovement() {
      this.applyInput(true, true);
   }

   private void applyInput(boolean var1, boolean var2) {
      if (this.player != null) {
         ViaCompat.method4848(this.player.input, var1 ? 1.0F : 0.0F);
         ViaCompat.method4849(this.player.input, 0.0F);
         this.player.input.playerInput = new PlayerInput(var1, false, false, false, var2, false, var1);
      }
   }

   private void stopWalking() {
      this.walkTarget = null;
      this.nextRouteBlock = null;
   }

   private Entity findHologram() {
      String var1 = WarpFarmModule.method4115().toLowerCase(Locale.ROOT);
      Entity var2 = null;
      double var3 = 16384.0;

      for (Entity var6 : this.world.getEntities()) {
         if (var6 != this.player) {
            double var7 = this.player.squaredDistanceTo(var6);
            if (!(var7 > var3)) {
               Text var9 = var6 instanceof TextDisplayEntity var10 ? var10.getText() : var6.getCustomName();
               if (var9 != null && var9.getString().toLowerCase(Locale.ROOT).contains(var1)) {
                  var2 = var6;
                  var3 = var7;
               }
            }
         }
      }

      return var2;
   }

   private double horizontalDistanceSq(Vec3d var1) {
      double var2 = this.player.getX() - var1.x;
      double var4 = this.player.getZ() - var1.z;
      return var2 * var2 + var4 * var4;
   }

   private BotRctService rct() {
      return this.bot().getRct();
   }

   private void resetState() {
      this.warpCooldownTicks = 0;
      this.settleTicks = 0;
      this.rctRetryCooldownTicks = 0;
      this.pathFailStreak = 0;
      this.lastAnarchy = Integer.MIN_VALUE;
      this.observedEvacuationSequence = WarpFarmModule.method4114();
      this.walkTarget = null;
      this.nextRouteBlock = null;
   }
}

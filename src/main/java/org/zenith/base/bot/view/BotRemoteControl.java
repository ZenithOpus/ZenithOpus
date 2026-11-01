package org.zenith.base.bot.view;

import com.darkmagician6.eventapi.EventTarget;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.ActionResult.Fail;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.ActionResult.SwingSource;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotInteractionManager;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.client.events.BotUpdateEvent;
import org.zenith.utility.misc.ViaCompat;

public final class BotRemoteControl {
   private final BotClient client;
   private volatile boolean forward;
   private volatile boolean backward;
   private volatile boolean left;
   private volatile boolean right;
   private volatile boolean jump;
   private volatile boolean sneak;
   private volatile boolean sprint;
   private volatile boolean attackHeld;
   private volatile boolean useHeld;
   private final AtomicInteger attackPresses = new AtomicInteger();
   private final AtomicInteger usePresses = new AtomicInteger();
   private final AtomicInteger requestedSlot = new AtomicInteger(-1);
   private final AtomicInteger dropRequest = new AtomicInteger();
   private volatile boolean swapHandsRequested;
   private volatile boolean uiScreenOpen;
   private int attackCooldown;
   private int itemUseCooldown;
   private boolean wasMovementActive;
   private boolean wasAttackActive;
   private boolean wasUseActive;
   @Nullable
   private volatile HitResult crosshairTarget;
   private volatile boolean attached;

   public BotRemoteControl(BotClient var1) {
      this.client = var1;
   }

   public void attach() {
      if (!this.attached) {
         this.attached = true;
         this.client.execute(() -> this.client.getEventBus().register(this, BotUpdateEvent.class));
      }
   }

   public void detach() {
      if (this.attached) {
         this.attached = false;
         this.client.execute(() -> {
            this.client.getEventBus().unregister(this);
            this.releaseAll();
            BotPlayer var1 = this.client.getPlayer();
            BotPlayHandler var2 = this.client.getPlayHandler();
            if (var1 != null && var2 != null) {
               if (this.wasMovementActive) {
                  clearMovementInput(var1);
                  this.wasMovementActive = false;
               }

               try {
                  if (this.wasAttackActive) {
                     var2.getInteractionManager().cancelBlockBreaking();
                  }

                  if (this.wasUseActive && var1.isUsingItem()) {
                     var2.getInteractionManager().stopUsingItem(var1);
                  }
               } catch (IllegalStateException var4) {
               }

               this.wasAttackActive = false;
               this.wasUseActive = false;
            }
         });
      }
   }

   public void setMovement(boolean var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6, boolean var7) {
      this.forward = var1;
      this.backward = var2;
      this.left = var3;
      this.right = var4;
      this.jump = var5;
      this.sneak = var6;
      this.sprint = var7;
   }

   public void releaseAll() {
      this.forward = this.backward = this.left = this.right = this.jump = this.sneak = this.sprint = false;
      this.attackHeld = false;
      this.useHeld = false;
      this.attackPresses.set(0);
      this.usePresses.set(0);
      this.dropRequest.set(0);
      this.swapHandsRequested = false;
   }

   public void setUiScreenOpen(boolean var1) {
      this.uiScreenOpen = var1;
   }

   public void pressAttack() {
      this.attackHeld = true;
      this.attackPresses.incrementAndGet();
   }

   public void releaseAttack() {
      this.attackHeld = false;
   }

   public void pressUse() {
      this.useHeld = true;
      this.usePresses.incrementAndGet();
   }

   public void releaseUse() {
      this.useHeld = false;
   }

   public void requestSlot(int var1) {
      if (var1 >= 0 && var1 <= 8) {
         this.requestedSlot.set(var1);
      }
   }

   public void requestDrop(boolean var1) {
      this.dropRequest.set(var1 ? 2 : 1);
   }

   public void requestSwapHands() {
      this.swapHandsRequested = true;
   }

   public void turn(double var1, double var3) {
      BotPlayer var5 = this.client.getPlayer();
      if (var5 != null && !this.uiScreenOpen) {
         synchronized (this.client.getRenderStateLock()) {
            var5.changeLookDirection(var1, var3);
         }
      }
   }

   public void clickSlot(int var1, int var2, SlotActionType var3) {
      this.client.execute(() -> {
         BotPlayer var4 = this.client.getPlayer();
         BotPlayHandler var5 = this.client.getPlayHandler();
         if (var4 != null && var5 != null) {
            try {
               var5.getInteractionManager().clickSlot(var4.currentScreenHandler.syncId, var1, var2, var3, var4);
            } catch (Exception var7) {
            }
         }
      });
   }

   public void closeScreen() {
      this.client.execute(() -> {
         BotPlayer var1 = this.client.getPlayer();
         if (var1 != null) {
            var1.closeHandledScreen();
         }
      });
   }

   @Nullable
   public HitResult getCrosshairTarget() {
      return this.crosshairTarget;
   }

   @EventTarget
   public void onBotUpdate(BotUpdateEvent var1) {
      BotWorld var2 = var1.getWorld();
      BotPlayer var3 = var1.getPlayer();
      BotPlayHandler var4 = this.client.getPlayHandler();
      if (var2 != null && var3 != null && var4 != null) {
         BotInteractionManager var5;
         try {
            var5 = var4.getInteractionManager();
         } catch (IllegalStateException var12) {
            return;
         }

         if (this.attackCooldown > 0) {
            this.attackCooldown--;
         }

         if (this.itemUseCooldown > 0) {
            this.itemUseCooldown--;
         }

         boolean var6 = this.uiScreenOpen || var4.hasOpenScreen();
         this.crosshairTarget = findCrosshairTarget(var3);
         int var7 = this.requestedSlot.getAndSet(-1);
         if (var7 >= 0) {
            var3.getInventory().setSelectedSlot(var7);
         }

         if (var6) {
            this.attackPresses.set(0);
            this.usePresses.set(0);
            if (this.wasMovementActive) {
               clearMovementInput(var3);
               this.wasMovementActive = false;
            }

            if (this.wasAttackActive) {
               var5.cancelBlockBreaking();
               this.wasAttackActive = false;
            }

            if (this.wasUseActive) {
               if (var3.isUsingItem()) {
                  var5.stopUsingItem(var3);
               }

               this.wasUseActive = false;
            }
         } else {
            boolean var8 = this.forward || this.backward || this.left || this.right || this.jump || this.sneak || this.sprint;
            if (var8 || this.wasMovementActive) {
               this.applyMovementInput(var3);
            }

            this.wasMovementActive = var8;
            int var9 = this.dropRequest.getAndSet(0);
            if (var9 > 0 && !var3.isSpectator()) {
               var3.dropSelectedItem(var9 == 2);
            }

            if (this.swapHandsRequested) {
               this.swapHandsRequested = false;
               if (!var3.isSpectator()) {
                  var4.sendPacket(new PlayerActionC2SPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
               }
            }

            if (var3.isUsingItem()) {
               if (!this.useHeld && this.wasUseActive) {
                  var5.stopUsingItem(var3);
                  this.wasUseActive = false;
               }

               this.attackPresses.set(0);
               this.usePresses.set(0);
            } else {
               this.wasUseActive = false;
               int var10 = this.attackPresses.getAndSet(0);

               for (int var11 = 0; var11 < var10; var11++) {
                  this.doAttack(var2, var3, var5);
               }

               boolean var14;
               if (this.useHeld) {
                  var14 = this.itemUseCooldown == 0;
                  if (var14) {
                     this.usePresses.set(0);
                  }
               } else {
                  var14 = this.usePresses.getAndSet(0) > 0 && this.itemUseCooldown == 0;
               }

               if (var14) {
                  this.doItemUse(var2, var3, var5);
                  this.wasUseActive = true;
               }
            }

            boolean var13 = this.attackHeld;
            if (var13 || this.wasAttackActive) {
               this.handleBlockBreaking(var2, var3, var5, var13);
            }

            this.wasAttackActive = var13;
         }
      }
   }

   private void applyMovementInput(BotPlayer var1) {
      var1.input.playerInput = new PlayerInput(this.forward, this.backward, this.left, this.right, this.jump, this.sneak, this.sprint);
      ViaCompat.method4848(var1.input, movementMultiplier(this.forward, this.backward));
      ViaCompat.method4849(var1.input, movementMultiplier(this.left, this.right));
   }

   private static void clearMovementInput(BotPlayer var0) {
      var0.input.playerInput = PlayerInput.DEFAULT;
      ViaCompat.method4848(var0.input, 0.0F);
      ViaCompat.method4849(var0.input, 0.0F);
   }

   private static float movementMultiplier(boolean var0, boolean var1) {
      if (var0 == var1) {
         return 0.0F;
      } else {
         return var0 ? 1.0F : -1.0F;
      }
   }

   private void doAttack(BotWorld var1, BotPlayer var2, BotInteractionManager var3) {
      if (this.attackCooldown <= 0) {
         HitResult var4 = this.crosshairTarget;
         if (var4 != null && !var2.isRiding()) {
            ItemStack var5 = var2.getStackInHand(Hand.MAIN_HAND);
            if (var5.isItemEnabled(var1.getEnabledFeatures())) {
               switch (var4.getType()) {
                  case ENTITY:
                     var3.attackEntity(var2, ((EntityHitResult)var4).getEntity());
                     break;
                  case BLOCK:
                     BlockHitResult var6 = (BlockHitResult)var4;
                     BlockPos var7 = var6.getBlockPos();
                     if (!var1.getBlockState(var7).isAir()) {
                        var3.attackBlock(var7, var6.getSide());
                     } else {
                        this.onAttackMiss(var2, var3);
                     }
                     break;
                  case MISS:
                     this.onAttackMiss(var2, var3);
               }

               var2.swingHand(Hand.MAIN_HAND);
            }
         }
      }
   }

   private void onAttackMiss(BotPlayer var1, BotInteractionManager var2) {
      if (var2.getCurrentGameMode() != GameMode.CREATIVE) {
         this.attackCooldown = 10;
      }

      var1.resetTicksSinceLastAttack();
   }

   private void doItemUse(BotWorld var1, BotPlayer var2, BotInteractionManager var3) {
      if (!var3.isBreakingBlock()) {
         this.itemUseCooldown = 4;
         if (!var2.isRiding()) {
            HitResult var4 = this.crosshairTarget;

            for (Hand var8 : Hand.values()) {
               ItemStack var9 = var2.getStackInHand(var8);
               if (!var9.isItemEnabled(var1.getEnabledFeatures())) {
                  return;
               }

               if (var4 != null) {
                  switch (var4.getType()) {
                     case ENTITY:
                        EntityHitResult var14 = (EntityHitResult)var4;
                        Entity var16 = var14.getEntity();
                        if (!var1.getWorldBorder().contains(var16.getBlockPos())) {
                           return;
                        }

                        ActionResult var18 = var3.interactEntityAtLocation(var2, var16, var14, var8);
                        if (!var18.isAccepted()) {
                           var18 = var3.interactEntity(var2, var16, var8);
                        }

                        if (var18 instanceof Success var13) {
                           if (var13.swingSource() == SwingSource.CLIENT) {
                              var2.swingHand(var8);
                           }

                           return;
                        }
                        break;
                     case BLOCK:
                        BlockHitResult var10 = (BlockHitResult)var4;
                        ActionResult var11 = var3.interactBlock(var2, var8, var10);
                        if (var11 instanceof Success var12) {
                           if (var12.swingSource() == SwingSource.CLIENT) {
                              var2.swingHand(var8);
                           }

                           return;
                        }

                        if (var11 instanceof Fail) {
                           return;
                        }
                  }
               }

               if (!var9.isEmpty() && var3.interactItem(var2, var8) instanceof Success var15) {
                  if (var15.swingSource() == SwingSource.CLIENT) {
                     var2.swingHand(var8);
                  }

                  return;
               }
            }
         }
      }
   }

   private void handleBlockBreaking(BotWorld var1, BotPlayer var2, BotInteractionManager var3, boolean var4) {
      if (!var4) {
         this.attackCooldown = 0;
      }

      if (this.attackCooldown <= 0 && !var2.isUsingItem()) {
         HitResult var5 = this.crosshairTarget;
         if (var4 && var5 != null && var5.getType() == Type.BLOCK) {
            BlockHitResult var6 = (BlockHitResult)var5;
            BlockPos var7 = var6.getBlockPos();
            if (!var1.getBlockState(var7).isAir() && var3.updateBlockBreakingProgress(var7, var6.getSide())) {
               var2.swingHand(Hand.MAIN_HAND);
            }
         } else {
            var3.cancelBlockBreaking();
         }
      }
   }

   private static HitResult findCrosshairTarget(BotPlayer var0) {
      double var1 = var0.getBlockInteractionRange();
      double var3 = var0.getEntityInteractionRange();
      double var5 = Math.max(var1, var3);
      double var7 = MathHelper.square(var5);
      Vec3d var9 = var0.getCameraPosVec(1.0F);
      HitResult var10 = var0.raycast(var5, 1.0F, false);
      double var11 = var10.getPos().squaredDistanceTo(var9);
      if (var10.getType() != Type.MISS) {
         var7 = var11;
         var5 = Math.sqrt(var11);
      }

      Vec3d var13 = var0.getRotationVec(1.0F);
      Vec3d var14 = var9.add(var13.x * var5, var13.y * var5, var13.z * var5);
      Box var15 = var0.getBoundingBox().stretch(var13.multiply(var5)).expand(1.0, 1.0, 1.0);
      EntityHitResult var16 = ProjectileUtil.raycast(var0, var9, var14, var15, EntityPredicates.CAN_HIT, var7);
      return var16 != null && var16.getPos().squaredDistanceTo(var9) < var11 ? ensureTargetInRange(var16, var9, var3) : ensureTargetInRange(var10, var9, var1);
   }

   private static HitResult ensureTargetInRange(HitResult var0, Vec3d var1, double var2) {
      Vec3d var4 = var0.getPos();
      if (!var4.isInRange(var1, var2)) {
         Direction var5 = Direction.getFacing(var4.x - var1.x, var4.y - var1.y, var4.z - var1.z);
         return BlockHitResult.createMissed(var4, var5, BlockPos.ofFloored(var4));
      } else {
         return var0;
      }
   }
}

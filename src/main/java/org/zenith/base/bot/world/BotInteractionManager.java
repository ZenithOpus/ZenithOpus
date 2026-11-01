package org.zenith.base.bot.world;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OperatorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult.PassToDefaultBlockAction;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.zenith.base.bot.net.BotPlayHandler;

public final class BotInteractionManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final BotPlayHandler networkHandler;
   private BlockPos currentBreakingPos = new BlockPos(-1, -1, -1);
   private ItemStack selectedStack = ItemStack.EMPTY;
   public float currentBreakingProgress;
   public int blockBreakingCooldown;
   private boolean breakingBlock;
   private GameMode gameMode = GameMode.DEFAULT;
   @Nullable
   private GameMode previousGameMode;
   private int lastSelectedSlot;

   public BotInteractionManager(BotPlayHandler var1) {
      this.networkHandler = var1;
   }

   @Nullable
   private BotPlayer player() {
      return this.networkHandler.getPlayer();
   }

   @Nullable
   private BotWorld world() {
      return this.networkHandler.getWorld();
   }

   public void copyAbilities(PlayerEntity var1) {
      this.gameMode.setAbilities(var1.getAbilities());
   }

   public void setGameModes(GameMode var1, @Nullable GameMode var2) {
      this.gameMode = var1;
      this.previousGameMode = var2;
      BotPlayer var3 = this.player();
      if (var3 != null) {
         this.gameMode.setAbilities(var3.getAbilities());
      }
   }

   public void setGameMode(GameMode var1) {
      if (var1 != this.gameMode) {
         this.previousGameMode = this.gameMode;
      }

      this.gameMode = var1;
      BotPlayer var2 = this.player();
      if (var2 != null) {
         this.gameMode.setAbilities(var2.getAbilities());
      }
   }

   public boolean breakBlock(BlockPos var1) {
      BotPlayer var2 = this.player();
      BotWorld var3 = this.world();
      if (var2 == null || var3 == null) {
         return false;
      } else if (var2.isBlockBreakingRestricted(var3, var1, this.gameMode)) {
         return false;
      } else {
         BlockState var4 = var3.getBlockState(var1);
         if (!var2.getMainHandStack().getItem().canMine(var2.getMainHandStack(), var4, var3, var1, var2)) {
            return false;
         } else {
            Block var5 = var4.getBlock();
            if (var5 instanceof OperatorBlock && !var2.isCreativeLevelTwoOp()) {
               return false;
            } else if (var4.isAir()) {
               return false;
            } else {
               var5.onBreak(var3, var1, var4, var2);
               FluidState var6 = var3.getFluidState(var1);
               boolean var7 = var3.setBlockState(var1, var6.getBlockState(), 11);
               if (var7) {
                  var5.onBroken(var3, var1, var4);
               }

               return var7;
            }
         }
      }
   }

   public boolean attackBlock(BlockPos var1, Direction var2) {
      BotPlayer var3 = this.player();
      BotWorld var4 = this.world();
      if (var3 == null || var4 == null) {
         return false;
      } else if (var3.isBlockBreakingRestricted(var4, var1, this.gameMode)) {
         return false;
      } else if (!var4.getWorldBorder().contains(var1)) {
         return false;
      } else {
         if (this.gameMode.isCreative()) {
            this.sendSequencedPacket(var4, var3x -> {
               this.breakBlock(var1);
               return new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var1, var2, var3x);
            });
            this.blockBreakingCooldown = 5;
         } else if (!this.breakingBlock || !this.isCurrentlyBreaking(var1)) {
            if (this.breakingBlock) {
               this.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, var2));
            }

            BlockState var5 = var4.getBlockState(var1);
            this.sendSequencedPacket(var4, var6 -> {
               boolean var7 = !var5.isAir();
               if (var7 && this.currentBreakingProgress == 0.0F) {
                  var5.onBlockBreakStart(var4, var1, var3);
               }

               if (var7 && var5.calcBlockBreakingDelta(var3, var3.getEntityWorld(), var1) >= 1.0F) {
                  this.breakBlock(var1);
               } else {
                  this.breakingBlock = true;
                  this.currentBreakingPos = var1;
                  this.selectedStack = var3.getMainHandStack();
                  this.currentBreakingProgress = 0.0F;
               }

               return new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var1, var2, var6);
            });
         }

         return true;
      }
   }

   public void cancelBlockBreaking() {
      if (this.breakingBlock) {
         this.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, Direction.DOWN));
         this.breakingBlock = false;
         this.currentBreakingProgress = 0.0F;
         BotPlayer var1 = this.player();
         if (var1 != null) {
            var1.resetTicksSinceLastAttack();
         }
      }
   }

   public boolean updateBlockBreakingProgress(BlockPos var1, Direction var2) {
      BotPlayer var3 = this.player();
      BotWorld var4 = this.world();
      if (var3 != null && var4 != null) {
         this.syncSelectedSlot();
         if (this.blockBreakingCooldown > 0) {
            this.blockBreakingCooldown--;
            return true;
         } else if (this.gameMode.isCreative() && var4.getWorldBorder().contains(var1)) {
            this.blockBreakingCooldown = 5;
            this.sendSequencedPacket(var4, var3x -> {
               this.breakBlock(var1);
               return new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var1, var2, var3x);
            });
            return true;
         } else if (this.isCurrentlyBreaking(var1)) {
            BlockState var5 = var4.getBlockState(var1);
            if (var5.isAir()) {
               this.breakingBlock = false;
               return false;
            } else {
               this.currentBreakingProgress = this.currentBreakingProgress + var5.calcBlockBreakingDelta(var3, var3.getEntityWorld(), var1);
               if (this.currentBreakingProgress >= 1.0F) {
                  this.breakingBlock = false;
                  this.sendSequencedPacket(var4, var3x -> {
                     this.breakBlock(var1);
                     return new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, var1, var2, var3x);
                  });
                  this.currentBreakingProgress = 0.0F;
                  this.blockBreakingCooldown = 5;
               }

               return true;
            }
         } else {
            return this.attackBlock(var1, var2);
         }
      } else {
         return false;
      }
   }

   public void sendSequencedPacket(BotWorld var1, BotInteractionManager.SequencedPacketCreator var2) {
      try (BotPendingUpdateManager var3 = var1.getPendingUpdateManager().incrementSequence()) {
         int var4 = var3.getSequence();
         Packet var5 = var2.predict(var4);
         this.networkHandler.sendPacket(var5);
      }
   }

   public void tick() {
      this.syncSelectedSlot();
   }

   private boolean isCurrentlyBreaking(BlockPos var1) {
      BotPlayer var2 = this.player();
      if (var2 == null) {
         return false;
      } else {
         ItemStack var3 = var2.getMainHandStack();
         return var1.equals(this.currentBreakingPos) && ItemStack.areItemsAndComponentsEqual(var3, this.selectedStack);
      }
   }

   public void syncSelectedSlot() {
      BotPlayer var1 = this.player();
      if (var1 != null) {
         int var2 = var1.getInventory().getSelectedSlot();
         if (var2 != this.lastSelectedSlot) {
            this.lastSelectedSlot = var2;
            this.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.lastSelectedSlot));
         }
      }
   }

   public ActionResult interactBlock(BotPlayer var1, Hand var2, BlockHitResult var3) {
      this.syncSelectedSlot();
      BotWorld var4 = this.world();
      if (var4 != null && var4.getWorldBorder().contains(var3.getBlockPos())) {
         MutableObject var5 = new MutableObject();
         this.sendSequencedPacket(var4, var5x -> {
            var5.setValue(this.interactBlockInternal(var1, var2, var3));
            return new PlayerInteractBlockC2SPacket(var2, var3, var5x);
         });
         return (ActionResult)var5.getValue();
      } else {
         return ActionResult.FAIL;
      }
   }

   private ActionResult interactBlockInternal(BotPlayer var1, Hand var2, BlockHitResult var3) {
      BotWorld var4 = Objects.requireNonNull(this.world());
      BlockPos var5 = var3.getBlockPos();
      ItemStack var6 = var1.getStackInHand(var2);
      if (this.gameMode == GameMode.SPECTATOR) {
         return ActionResult.CONSUME;
      } else {
         boolean var7 = !var1.getMainHandStack().isEmpty() || !var1.getOffHandStack().isEmpty();
         boolean var8 = var1.shouldCancelInteraction() && var7;
         if (!var8) {
            BlockState var9 = var4.getBlockState(var5);
            if (!var9.getBlock().getRequiredFeatures().isSubsetOf(this.networkHandler.getEnabledFeatures())) {
               return ActionResult.FAIL;
            }

            ActionResult var10 = var9.onUseWithItem(var1.getStackInHand(var2), var4, var1, var2, var3);
            if (var10.isAccepted()) {
               return var10;
            }

            if (var10 instanceof PassToDefaultBlockAction && var2 == Hand.MAIN_HAND) {
               ActionResult var11 = var9.onUse(var4, var1, var3);
               if (var11.isAccepted()) {
                  return var11;
               }
            }
         }

         if (!var6.isEmpty() && !var1.getItemCooldownManager().isCoolingDown(var6)) {
            ItemUsageContext var12 = new ItemUsageContext(var1, var2, var3);
            ActionResult var13;
            if (this.gameMode.isCreative()) {
               int var14 = var6.getCount();
               var13 = var6.useOnBlock(var12);
               var6.setCount(var14);
            } else {
               var13 = var6.useOnBlock(var12);
            }

            return var13;
         } else {
            return ActionResult.PASS;
         }
      }
   }

   public ActionResult interactItem(BotPlayer var1, Hand var2) {
      if (this.gameMode == GameMode.SPECTATOR) {
         return ActionResult.PASS;
      } else {
         BotWorld var3 = this.world();
         if (var3 == null) {
            return ActionResult.PASS;
         } else {
            this.syncSelectedSlot();
            MutableObject var4 = new MutableObject();
            this.sendSequencedPacket(var3, var4x -> {
               PlayerInteractItemC2SPacket var5 = new PlayerInteractItemC2SPacket(var2, var4x, var1.getYaw(), var1.getPitch());
               ItemStack var6 = var1.getStackInHand(var2);
               if (var1.getItemCooldownManager().isCoolingDown(var6)) {
                  var4.setValue(ActionResult.PASS);
                  return var5;
               } else {
                  ActionResult var7 = var6.use(var3, var1, var2);
                  ItemStack var8;
                  if (var7 instanceof Success var9) {
                     var8 = Objects.requireNonNullElseGet(var9.getNewHandStack(), () -> var1.getStackInHand(var2));
                  } else {
                     var8 = var1.getStackInHand(var2);
                  }

                  if (var8 != var6) {
                     var1.setStackInHand(var2, var8);
                  }

                  var4.setValue(var7);
                  return var5;
               }
            });
            return (ActionResult)var4.getValue();
         }
      }
   }

   public void attackEntity(BotPlayer var1, Entity var2) {
      this.syncSelectedSlot();
      this.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(var2, var1.isSneaking()));
      if (this.gameMode != GameMode.SPECTATOR) {
         var1.attack(var2);
         var1.resetTicksSinceLastAttack();
      }
   }

   public ActionResult interactEntity(BotPlayer var1, Entity var2, Hand var3) {
      this.syncSelectedSlot();
      this.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.interact(var2, var1.isSneaking(), var3));
      return (ActionResult)(this.gameMode == GameMode.SPECTATOR ? ActionResult.PASS : var1.interact(var2, var3));
   }

   public ActionResult interactEntityAtLocation(BotPlayer var1, Entity var2, EntityHitResult var3, Hand var4) {
      this.syncSelectedSlot();
      Vec3d var5 = var3.getPos().subtract(var2.getX(), var2.getY(), var2.getZ());
      this.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.interactAt(var2, var1.isSneaking(), var4, var5));
      return (ActionResult)(this.gameMode == GameMode.SPECTATOR ? ActionResult.PASS : var2.interactAt(var1, var5, var4));
   }

   public void clickSlot(int var1, int var2, int var3, SlotActionType var4, PlayerEntity var5) {
      ScreenHandler var6 = var5.currentScreenHandler;
      if (var1 != var6.syncId) {
         LOGGER.warn("Ignoring click in mismatching container. Click in {}, player has {}.", var1, var6.syncId);
      } else {
         DefaultedList var7 = var6.slots;
         int var8 = var7.size();
         var var9 = Lists.newArrayListWithCapacity(var8);

         for (Object var11_o : (Iterable<?>)(Object)(var7)) {
            Slot var11 = (Slot)var11_o;
            var9.add(var11.getStack().copy());
         }

         var6.onSlotClick(var2, var3, var4, var5);
         Int2ObjectOpenHashMap var14 = new Int2ObjectOpenHashMap();

         for (int var15 = 0; var15 < var8; var15++) {
            ItemStack var12 = (ItemStack)var9.get(var15);
            ItemStack var13 = ((Slot)var7.get(var15)).getStack();
            if (!ItemStack.areEqual(var12, var13)) {
               var14.put(var15, ItemStackHash.fromItemStack(var13, this.networkHandler.getComponentHasher()));
            }
         }

         this.networkHandler
            .sendPacket(
               new ClickSlotC2SPacket(
                  var1,
                  var6.getRevision(),
                  (short)var2,
                  (byte)var3,
                  var4,
                  var14,
                  ItemStackHash.fromItemStack(var6.getCursorStack(), this.networkHandler.getComponentHasher())
               )
            );
      }
   }

   public void clickButton(int var1, int var2) {
      this.networkHandler.sendPacket(new ButtonClickC2SPacket(var1, var2));
   }

   public void clickCreativeStack(ItemStack var1, int var2) {
      if (this.gameMode.isCreative() && var1.getItem().getRequiredFeatures().isSubsetOf(this.networkHandler.getEnabledFeatures())) {
         this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(var2, var1));
      }
   }

   public void stopUsingItem(PlayerEntity var1) {
      this.syncSelectedSlot();
      this.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
      var1.stopUsingItem();
   }

   public boolean isFlyingLocked() {
      return this.gameMode == GameMode.SPECTATOR;
   }

   @Nullable
   public GameMode getPreviousGameMode() {
      return this.previousGameMode;
   }

   public GameMode getCurrentGameMode() {
      return this.gameMode;
   }

   public boolean isBreakingBlock() {
      return this.breakingBlock;
   }

   public int getBlockBreakingProgress() {
      return this.currentBreakingProgress > 0.0F ? (int)(this.currentBreakingProgress * 10.0F) : -1;
   }

   @FunctionalInterface
   public interface SequencedPacketCreator {
      Packet<ServerPlayPacketListener> predict(int var1);
   }
}

package org.zenith.base.bot.modules.impl;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import kotlin.collections.EmptyList;
import lombok.Generated;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.ActionResult.SwingSource;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.bot.client.BotRctService;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotInteractionManager;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotRaytracing;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.client.events.BotUpdateEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.game.MineTargetFinder;
import org.zenith.utility.game.Pathfinder;
import org.zenith.utility.misc.Util10;
import org.zenith.utility.misc.ViaCompat;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "BotAutoMine",
   category = ModuleCategory.PLAYER,
   description = "module.botautomine.description"
)
public class BotAutoMine extends BotModule {
   private static final int MINE_DATA_COOLDOWN_TICKS = 40;
   private static final Pathfinder.Data MOVE_PATH_SETTINGS = new Pathfinder.Data(30000, 192, 100, 130, false, true, Pathfinder.Type.CLOSEST_REACHABLE);
   private static final Set<Item> BUYER_ITEMS = Set.of(
      Items.REDSTONE,
      Items.QUARTZ,
      Items.DIAMOND,
      Items.ENDER_PEARL,
      Items.END_STONE,
      Items.LAPIS_LAZULI,
      Items.IRON_INGOT,
      Items.GOLD_INGOT,
      Items.GOLD_NUGGET,
      Items.OBSIDIAN,
      Items.NETHER_BRICK,
      Items.NETHER_BRICKS,
      Items.COAL
   );
   private final ModeSetting mineMode = new ModeSetting(
      "module.autoMine.mode", "module.autoMine.mode.desc", "module.autoMine.modeNormal", "module.autoMine.modeNether", "module.autoMine.modeCombined"
   );
   public static final int airBlocks = 600;
   private final Set<BlockPos> skippedBlocks = new HashSet<>();
   private MineTargetFinder.Data selectedMine;
   private BlockPos nextRouteBlock;
   private int mineDataCooldown;
   private boolean combinedMineSwitched;
   private BotWorld world;
   private BotPlayer player;
   private BotInteractionManager interaction;
   private Rotation currentRotation;
   private static final Comparator<BotAutoMine.BreakCandidate> BREAK_CANDIDATE_ORDER = Comparator.comparingInt(BotAutoMine.BreakCandidate::drillCoverage)
      .reversed()
      .thenComparingDouble(BotAutoMine.BreakCandidate::distanceSquared);

   @Override
   public void onEnable() {
      this.selectedMine = null;
      this.nextRouteBlock = null;
      this.mineDataCooldown = 0;
      this.combinedMineSwitched = false;
      this.skippedBlocks.clear();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.bindContext();
      this.applyInput(false);
      super.onDisable();
   }

   @EventTarget
   public void onBotUpdate(BotUpdateEvent var1) {
      this.world = var1.getWorld();
      this.player = var1.getPlayer();
      BotPlayHandler var2 = this.handler();
      this.interaction = var2 == null ? null : var2.getInteractionManager();
      if (this.world != null && this.player != null && this.interaction != null && var2 != null) {
         this.currentRotation = new Rotation(this.player.getYaw(), this.player.getPitch());
         this.nextRouteBlock = null;
         var var3 = this.onTeleportation();
         if (var3 != null) {
            this.onRepair();
            if (!this.isEnabled()) {
               return;
            }

            this.onDrop();
            this.onBreak(var3);
            if (!this.onMove(var3)) {
               this.setRotation(this.currentRotation.method3140((float)MathUtils.method2651(-5.0, 5.0), (float)MathUtils.method2651(-5.0, 5.0)));
            }
         }

         this.applyInput((!this.rct().isActive()));
      }
   }

   private void bindContext() {
      this.world = this.world();
      this.player = this.player();
      this.interaction = this.interaction();
   }

   private boolean onMove(List<BotAutoMine.BlockMine> var1) {
      BlockPos var2 = this.findBestMovingTarget(var1);
      if (var2 == null) {
         return false;
      } else {
         Pathfinder.Entry2 var3 = Pathfinder.method759(this.world, this.player.getBlockPos(), var2, MOVE_PATH_SETTINGS).orElse(null);
         this.nextRouteBlock = var3 == null ? (!this.player.isOnGround() ? var2 : null) : var3.method779(this.player.getEntityPos());
         if (this.nextRouteBlock == null) {
            return false;
         } else {
            Vec3d var4 = this.player.getCameraPosVec(1.0F);
            Vec3d var5 = Pathfinder.method761(this.nextRouteBlock);
            Rotation var6 = Rotation.method3128(new Vec3d(var5.x, var4.y, var5.z), var4);
            Rotation var7 = this.currentRotation
               .method3141(this.currentRotation.method3131(var6))
               .method3140((float)MathUtils.method2651(-5.0, 5.0), (float)MathUtils.method2651(-5.0, 5.0));
            this.setRotation(var7);
            return true;
         }
      }
   }

   private void applyInput(boolean var1) {
      if (this.player != null) {
         ViaCompat.method4848(this.player.input, var1 ? 1.0F : 0.0F);
         ViaCompat.method4849(this.player.input, 0.0F);
         this.player.input.playerInput = new PlayerInput(var1, false, false, false, false, false, var1);
      }
   }

   private void setRotation(Rotation var1) {
      this.currentRotation = var1;
      this.player.setYaw(MathHelper.wrapDegrees(var1.getFloatValue()));
      this.player.setPitch(MathHelper.clamp(var1.getFloatValue2(), -90.0F, 90.0F));
   }

   private void onDrop() {
      BotPlayHandler var1 = this.handler();
      Text var2 = var1.getCurrentScreenTitle();
      if (var2 != null && var2.getString().toLowerCase(Locale.ROOT).contains("для продажи")) {
         if (this.player.currentScreenHandler instanceof GenericContainerScreenHandler var3) {
            int var5;
            if (this.moveBuyerResources() && (var5 = this.getContainerSlotCount(var3)) > 0) {
               this.clickSlot(var5 - 1, 0, SlotActionType.QUICK_MOVE);
            }

            Slot var6 = this.findTrashSlot();
            if (var6 != null) {
               this.clickSlot(var6.id, 1, SlotActionType.THROW);
            }
         }
      } else {
         if (this.player.age % 40 == 0) {
            var1.sendCommand("buyer");
         }
      }
   }

   private boolean moveBuyerResources() {
      byte var1 = 0;
      int var2 = 0;

      for (Slot var4 : this.player.currentScreenHandler.slots) {
         if (this.isPlayerInventorySlot(var4) && this.isBuyerResource(var4.getStack())) {
            if (var2 > 5) {
               break;
            }

            this.clickSlot(var4.id, 0, SlotActionType.QUICK_MOVE);
            var2++;
            var1 = 1;
         }
      }

      return (var1 != 0);
   }

   private boolean isBuyerResource(ItemStack var1) {
      return (!var1.isEmpty() && BUYER_ITEMS.contains(var1.getItem()));
   }

   private Slot findTrashSlot() {
      return this.player
         .currentScreenHandler
         .slots
         .stream()
         .filter(this::isPlayerInventorySlot)
         .filter(var1 -> this.isTrash(var1.getStack()))
         .findFirst()
         .orElse(null);
   }

   private boolean isPlayerInventorySlot(Slot var1) {
      if (this.player.currentScreenHandler instanceof GenericContainerScreenHandler var2) {
         int var4 = this.getContainerSlotCount(var2);
         return !(var1.inventory != this.player.getInventory() && (var1.id < var4 || var1.id >= var4 + 36));
      } else {
         return (var1.inventory == this.player.getInventory() && var1.id >= 9 && var1.id <= 44);
      }
   }

   private int getContainerSlotCount(GenericContainerScreenHandler var1) {
      return Math.min(var1.getInventory().size(), Math.max(0, this.player.currentScreenHandler.slots.size() - 36));
   }

   private boolean isTrash(ItemStack var1) {
      if (!var1.isEmpty() && !var1.isOf(Items.PLAYER_HEAD) && !var1.isOf(Items.TRIPWIRE_HOOK) && !this.isBuyerResource(var1)) {
         return (var1.getItem() instanceof BlockItem var2 && !MineTargetFinder.method1934(var2.getBlock()));
      } else {
         return false;
      }
   }

   private void clickSlot(int var1, int var2, SlotActionType var3) {
      this.interaction.clickSlot(this.player.currentScreenHandler.syncId, var1, var2, var3, this.player);
   }

   private void onBreak(List<BotAutoMine.BlockMine> var1) {
      ItemStack var2 = this.player.getMainHandStack();
      if (Util10.method2177(var2) && var2.getMaxDamage() - var2.getDamage() >= 30) {
         ArrayList var3 = new ArrayList();

         for (BotAutoMine.BlockMine var5 : var1) {
            if (this.isStaticMineable(var5)) {
               var3.add(var5.pos());
            }
         }

         this.skippedBlocks.clear();

         for (int var6 = 0; var6 < 6; var6++) {
            BotAutoMine.BreakTarget var7 = this.findBestBreakTarget(var3);
            if (var7 == null) {
               break;
            }

            this.grimSuperBypass(0.0, var7.rotation());
            if (this.interaction.updateBlockBreakingProgress(var7.hitResult().getBlockPos(), var7.hitResult().getSide())) {
               this.skipDrillArea(var7.hitResult().getBlockPos());
            }
         }
      }
   }

   private BotAutoMine.BreakTarget findBestBreakTarget(List<BlockPos> var1) {
      Vec3d var2 = this.player.getCameraPosVec(1.0F);
      double var3 = this.player.getBlockInteractionRange();
      double var5 = 5.4;
      HashSet<BlockPos> var7 = new HashSet();

      for (BlockPos var9 : var1) {
         if (!this.skippedBlocks.contains(var9)) {
            var7.add(var9);
         }
      }

      ArrayList<BotAutoMine.BreakCandidate> var15 = new ArrayList();
      Mutable var16 = new Mutable();

      for (Object var11_o : (Iterable<?>)(Object)(var7)) {
         BlockPos var11 = (BlockPos)var11_o;
         if (this.isWithinMineRange(var11, var2, var5, var3)) {
            var15.add(new BotAutoMine.BreakCandidate(var11, this.countDrillCoverage(var11, var7, var16), MineTargetFinder.method1939(var2, var11)));
         }
      }

      var15.sort(BREAK_CANDIDATE_ORDER);

      for (Object var18_o : (Iterable<?>)(Object)(var15)) {
         BotAutoMine.BreakCandidate var18 = (BotAutoMine.BreakCandidate)var18_o;
         BlockPos var12 = var18.pos();
         Rotation var13 = this.currentRotation.method3141(this.currentRotation.method3131(Rotation.method3128(var12.toCenterPos(), var2)));
         BlockHitResult var14 = BotRaytracing.rayTracePos(
            this.world, this.player, var2, var13, var3, var1x -> (var1x != null && var12.equals(var1x.getBlockPos()))
         );
         if (var14 != null && var14.getType() != Type.MISS) {
            return new BotAutoMine.BreakTarget(var13, var14, var18.drillCoverage(), var18.distanceSquared());
         }
      }

      return null;
   }

   private BlockPos findBestMovingTarget(List<BotAutoMine.BlockMine> var1) {
      Vec3d var2 = this.player.getCameraPosVec(1.0F);
      double var3 = this.player.getBlockInteractionRange();
      BlockPos var5 = null;
      double var6 = Double.MAX_VALUE;

      for (BotAutoMine.BlockMine var9 : var1) {
         if (this.isMineable(var9) && !(Math.abs(var9.pos().getY() + 0.5 - var2.y) > var3)) {
            double var10 = var9.pos().getX() + 0.5 - var2.x;
            double var12 = var9.pos().getZ() + 0.5 - var2.z;
            double var14;
            if ((var14 = var10 * var10 + var12 * var12) < var6) {
               var5 = var9.pos();
               var6 = var14;
            }
         }
      }

      return var5 != null ? new BlockPos(var5.getX(), this.player.getBlockY(), var5.getZ()) : null;
   }

   private boolean isMineable(BotAutoMine.BlockMine var1) {
      return (!this.skippedBlocks.contains(var1.pos()) && this.isStaticMineable(var1));
   }

   private boolean isStaticMineable(BotAutoMine.BlockMine var1) {
      BlockState var2 = var1.state();
      return (!var2.isAir() && var2.getFluidState().isEmpty() && var2.getHardness(this.world, var1.pos()) >= 0.0F && !var2.isOf(Blocks.OBSIDIAN));
   }

   private boolean isWithinMineRange(BlockPos var1, Vec3d var2, double var3, double var5) {
      Vec3d var7 = var1.toCenterPos();
      return (Math.abs(var7.x - var2.x) <= var3 && Math.abs(var7.y - var2.y) <= var5 && Math.abs(var7.z - var2.z) <= var3);
   }

   private int countDrillCoverage(BlockPos var1, Set<BlockPos> var2, Mutable var3) {
      int var4 = 0;

      for (int var5 = -1; var5 <= 1; var5++) {
         for (int var6 = -1; var6 <= 1; var6++) {
            for (int var7 = -1; var7 <= 1; var7++) {
               if (var2.contains(var3.set(var1, var5, var6, var7))) {
                  var4++;
               }
            }
         }
      }

      return var4;
   }

   private void skipDrillArea(BlockPos var1) {
      for (BlockPos var3 : BlockPos.iterate(var1.add(-1, -1, -1), var1.add(1, 1, 1))) {
         this.skippedBlocks.add(var3.toImmutable());
      }
   }

   private void grimSuperBypass(double var1, Rotation var3) {
      this.handler()
         .sendPacket(
            new Full(
               this.player.getX(),
               this.player.getY() + var1,
               this.player.getZ(),
               var3.getFloatValue(),
               var3.getFloatValue2(),
               this.player.isOnGround(),
               this.player.horizontalCollision
            )
         );
   }

   private List<BotAutoMine.BlockMine> onTeleportation() {
      if (this.rct().isActive()) {
         return null;
      } else if (this.rct().currentAnarchyHere() == -1) {
         this.reconnectToRandomAnarchy();
         return null;
      } else if (this.isWaitingForMineData()) {
         return null;
      } else if (this.mineMode.method2101("module.autoMine.modeCombined")) {
         return this.selectCombinedMine();
      } else {
         this.combinedMineSwitched = false;
         this.selectedMine = null;
         MineTargetFinder.Data var1 = MineTargetFinder.method1933(this.mineMode.method2101("module.autoMine.modeNether"));
         if (MineTargetFinder.method1938(var1, this.world) && !(this.getMineDistance(var1) > 25.0)) {
            return this.getAvailableMine(var1);
         } else {
            this.handleWarpTp();
            return null;
         }
      }
   }

   private List<BotAutoMine.BlockMine> selectCombinedMine() {
      MineTargetFinder.Data var1 = MineTargetFinder.method1933(false);
      MineTargetFinder.Data var2 = MineTargetFinder.method1933(true);
      if (MineTargetFinder.method1938(var1, this.world) && MineTargetFinder.method1938(var2, this.world)) {
         var var3 = this.getArena(var1);
         var var4 = this.getArena(var2);
         boolean var5 = this.isMineAvailable(var3);
         boolean var6 = this.isMineAvailable(var4);
         if (this.selectedMine != null) {
            if (this.getMineDistance(this.selectedMine) > 25.0) {
               this.handleWarpTp();
               return null;
            }

            boolean var7;
            if ((var7 = this.selectedMine.equals(var2)) && var6) {
               return var4;
            }

            if (!var7 && var5) {
               return var3;
            }

            if (this.combinedMineSwitched) {
               this.reconnectToRandomAnarchy();
               return null;
            }
         }

         if (!var5 && !var6) {
            MineTargetFinder.Data var10 = this.selectedMine == null ? var1 : this.selectedMine;
            if (MineTargetFinder.method1938(var10, this.world) && !(this.getMineDistance(var10) > 25.0)) {
               this.reconnectToRandomAnarchy();
               return null;
            } else {
               this.handleWarpTp();
               return null;
            }
         } else {
            MineTargetFinder.Data var9 = this.selectedMine;
            int var8 = !var6 || var5 && !(this.getFillPercentage(var4) > this.getFillPercentage(var3)) ? 0 : 1;
            this.selectedMine = var8 != 0 ? var2 : var1;
            if (!this.selectedMine.equals(var9)) {
               this.combinedMineSwitched = (var9 != null);
               this.sendWarpCommand();
               return null;
            } else {
               return var8 != 0 ? var4 : var3;
            }
         }
      } else {
         this.handleWarpTp();
         return null;
      }
   }

   private boolean isWaitingForMineData() {
      if (this.player.age >= 40 && this.mineDataCooldown <= 0) {
         return false;
      } else if (this.hasLoadedMineBlocks(this.getExpectedMineBounds())) {
         this.mineDataCooldown = 0;
         return false;
      } else {
         if (this.mineDataCooldown > 0) {
            this.mineDataCooldown--;
         }

         return true;
      }
   }

   private MineTargetFinder.Data getExpectedMineBounds() {
      return this.mineMode.method2101("module.autoMine.modeCombined") && this.selectedMine != null
         ? this.selectedMine
         : MineTargetFinder.method1933(this.mineMode.method2101("module.autoMine.modeNether"));
   }

   private boolean hasLoadedMineBlocks(MineTargetFinder.Data var1) {
      for (BlockPos var3 : BlockPos.iterate(
         var1.getIntValue(), var1.getIntValue3(), var1.getIntValue5(), var1.getIntValue2(), var1.getIntValue4(), var1.getIntValue6()
      )) {
         if (this.world.isChunkLoaded(var3) && !this.world.getBlockState(var3).isAir()) {
            return true;
         }
      }

      return false;
   }

   private void displayMineDistance(MineTargetFinder.Data var1) {
      this.bot().systemMessage(String.valueOf(this.getMineDistance(var1)));
   }

   private double getMineDistance(MineTargetFinder.Data var1) {
      double var2 = (var1.getIntValue() + var1.getIntValue2() + 1.0) * 0.5;
      double var4 = (var1.getIntValue5() + var1.getIntValue6() + 1.0) * 0.5;
      double var6 = Math.abs(this.player.getX() - var2);
      double var8 = Math.abs(this.player.getZ() - var4);
      return Math.hypot(var6, var8);
   }

   private List<BotAutoMine.BlockMine> getAvailableMine(MineTargetFinder.Data var1) {
      var var2 = this.getArena(var1);
      if (!this.isMineAvailable(var2)) {
         this.reconnectToRandomAnarchy();
         return null;
      } else {
         return var2;
      }
   }

   private boolean isMineAvailable(List<BotAutoMine.BlockMine> var1) {
      if (var1.size() - this.countAirBlocks(var1) < 600L) {
         return false;
      } else {
         return !(this.player.isOnGround() && !this.hasReachableBlockByHeight(var1));
      }
   }

   private boolean hasReachableBlockByHeight(List<BotAutoMine.BlockMine> var1) {
      int var2 = MathHelper.floor(this.player.getEyeY() + this.player.getBlockInteractionRange() - 1.0);
      return var1.stream().anyMatch(var1x -> (!var1x.state().isAir() && !var1x.state().isOf(Blocks.OBSIDIAN) && var1x.pos().getY() <= var2));
   }

   private long countAirBlocks(List<BotAutoMine.BlockMine> var1) {
      return var1.stream().filter(var0 -> var0.state().isAir()).count();
   }

   private double getFillPercentage(List<BotAutoMine.BlockMine> var1) {
      if (var1 != null) {
         if (var1.isEmpty()) {
            return 1 != 0 ? 0.0 : 1.0 - (double)this.countAirBlocks(var1) / var1.size();
         }
      }

      return var1.isEmpty() ? 0.0 : 1.0 - (double)this.countAirBlocks(var1) / var1.size();
   }

   public void onRepair() {
      if (this.player.age % 20 == 0) {
         ItemStack var1 = this.player.getMainHandStack();
         if (Util10.method2177(var1) && var1.getMaxDamage() - var1.getDamage() < 30) {
            if (this.player.getStackInHand(Hand.OFF_HAND).isOf(Items.EXPERIENCE_BOTTLE)) {
               this.useItem(Hand.OFF_HAND);
            } else {
               Slot var2 = this.player
                  .currentScreenHandler
                  .slots
                  .stream()
                  .filter(var0 -> var0.getStack().isOf(Items.EXPERIENCE_BOTTLE))
                  .max(Comparator.comparingInt(var0 -> var0.id))
                  .orElse(null);
               if (var2 == null) {
                  this.bot().systemMessage("AutoMine: в хотбаре закончились пузырьки опыта");
                  this.toggle();
               } else if (var2.inventory instanceof PlayerInventory || var2.inventory instanceof EnderChestInventory) {
                  this.clickSlot(var2.id, 40, SlotActionType.SWAP);
               }
            }
         }
      }
   }

   private void useItem(Hand var1) {
      if (this.interaction.interactItem(this.player, var1) instanceof Success var3 && var3.swingSource() == SwingSource.CLIENT) {
         this.player.swingHand(var1);
      }
   }

   private List<BotAutoMine.BlockMine> getArena(MineTargetFinder.Data var1) {
      return this.getBlocksInZone(
         new BlockPos(var1.getIntValue(), var1.getIntValue3(), var1.getIntValue5()),
         new BlockPos(var1.getIntValue2(), var1.getIntValue4(), var1.getIntValue6())
      );
   }

   private List<BotAutoMine.BlockMine> getBlocksInZone(BlockPos var1, BlockPos var2) {
      ArrayList<BotAutoMine.BlockMine> var3 = new ArrayList<>();
      int var4 = Math.min(var1.getX(), var2.getX());
      int var5 = Math.max(var1.getX(), var2.getX());
      int var6 = Math.min(var1.getY(), var2.getY());
      int var7 = Math.max(var1.getY(), var2.getY());
      int var8 = Math.min(var1.getZ(), var2.getZ());
      int var9 = Math.max(var1.getZ(), var2.getZ());

      for (int var10 = var4; var10 <= var5; var10++) {
         for (int var11 = var6; var11 <= var7; var11++) {
            for (int var12 = var8; var12 <= var9; var12++) {
               BlockPos var13 = new BlockPos(var10, var11, var12);
               var3.add(new BotAutoMine.BlockMine(var13, this.world.getBlockState(var13)));
            }
         }
      }

      return var3;
   }

   public void handleWarpTp() {
      if (this.player.age % 60 == 0) {
         this.sendWarpCommand();
      }
   }

   private void sendWarpCommand() {
      boolean var1 = this.mineMode.method2101("module.autoMine.modeCombined")
         ? MineTargetFinder.method1933(true).equals(this.selectedMine)
         : this.mineMode.method2101("module.autoMine.modeNether");
      this.handler().sendCommand(var1 ? "warp mine2" : "warp mine");
      this.mineDataCooldown = 20;
   }

   private void reconnectToRandomAnarchy() {
      this.selectedMine = null;
      this.nextRouteBlock = null;
      this.mineDataCooldown = 0;
      this.combinedMineSwitched = false;
      this.skippedBlocks.clear();
      int var1 = this.rct().currentAnarchyHere();

      int var2;
      do {
         while ((var2 = ThreadLocalRandom.current().nextInt(1, 64)) == var1) {
         }
      } while (var2 == 2 || var2 == 17 || var2 == 33 || var2 == 49);

      this.rct().reconnect(var2);
   }

   private BotRctService rct() {
      return this.bot().getRct();
   }

   @Generated
   public ModeSetting getMineMode() {
      return this.mineMode;
   }

   private static final class BlockMine {
      private final BlockPos pos;
      private final BlockState state;

      public BlockPos pos() {
         return this.pos;
      }

      public BlockState state() {
         return this.state;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof BlockMine other)) return false;
         return java.util.Objects.equals(this.pos, other.pos) && java.util.Objects.equals(this.state, other.state);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(pos, state);
      }

      @Override
      public String toString() {
         return "BlockMine[" + "pos=" + this.pos + ", " + "state=" + this.state + "]";
      }

      private BlockMine(BlockPos var1, BlockState var2) {
         this.pos = var1;
         this.state = var2;
      }
   }

   private static final class BreakCandidate {
      private final BlockPos pos;
      private final int drillCoverage;
      private final double distanceSquared;

      public BlockPos pos() {
         return this.pos;
      }

      public int drillCoverage() {
         return this.drillCoverage;
      }

      public double distanceSquared() {
         return this.distanceSquared;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof BreakCandidate other)) return false;
         return java.util.Objects.equals(this.pos, other.pos) && java.util.Objects.equals(this.drillCoverage, other.drillCoverage) && java.util.Objects.equals(this.distanceSquared, other.distanceSquared);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(pos, drillCoverage, distanceSquared);
      }

      @Override
      public String toString() {
         return "BreakCandidate[" + "pos=" + this.pos + ", " + "drillCoverage=" + this.drillCoverage + ", " + "distanceSquared=" + this.distanceSquared + "]";
      }

      private BreakCandidate(BlockPos var1, int var2, double var3) {
         this.pos = var1;
         this.drillCoverage = var2;
         this.distanceSquared = var3;
      }
   }

   private static final class BreakTarget {
      private final Rotation rotation;
      private final BlockHitResult hitResult;
      private final int drillCoverage;
      private final double distanceSquared;

      public Rotation rotation() {
         return this.rotation;
      }

      public BlockHitResult hitResult() {
         return this.hitResult;
      }

      public int drillCoverage() {
         return this.drillCoverage;
      }

      public double distanceSquared() {
         return this.distanceSquared;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof BreakTarget other)) return false;
         return java.util.Objects.equals(this.rotation, other.rotation) && java.util.Objects.equals(this.hitResult, other.hitResult) && java.util.Objects.equals(this.drillCoverage, other.drillCoverage) && java.util.Objects.equals(this.distanceSquared, other.distanceSquared);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(rotation, hitResult, drillCoverage, distanceSquared);
      }

      @Override
      public String toString() {
         return "BreakTarget[" + "rotation=" + this.rotation + ", " + "hitResult=" + this.hitResult + ", " + "drillCoverage=" + this.drillCoverage + ", " + "distanceSquared=" + this.distanceSquared + "]";
      }

      private BreakTarget(Rotation var1, BlockHitResult var2, int var3, double var4) {
         this.rotation = var1;
         this.hitResult = var2;
         this.drillCoverage = var3;
         this.distanceSquared = var4;
      }
   }
}

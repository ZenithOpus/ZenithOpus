package org.zenith.client.modules.impl.player;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.Settings;
import baritone.api.Settings.Setting;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalGetToBlock;
import baritone.api.process.IBaritoneProcess;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.input.Input;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.World;
import org.zenith.ChatLogger;
import org.zenith.MinecraftAccess;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.misc.ServerHelperModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "AutoFarmRW",
   category = ModuleCategory.PLAYER,
   description = "module.autofarmrw.description"
)
public final class AutoFarmRW extends Module {
   public static final AutoFarmRW INSTANCE = new AutoFarmRW();
   private static final long CONST_longValue = 15000L;
   private static final long CONST_longValue2 = 10000L;
   private static final int CONST_intValue = 8;
   private static final int CONST_intValue2 = 8;
   private static final Set<Item> items = Set.of(
      Items.WHEAT, Items.BEETROOT, Items.CARROT, Items.POTATO, Items.PUMPKIN, Items.MELON, Items.MELON_SLICE, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS
   );
   private final SliderSetting sellInterval = new SliderSetting(
      "module.autoFarmRW.sellInterval", 180.0F, 30.0F, 600.0F, 15.0F, "module.autoFarmRW.sellInterval.desc", "с"
   );
   private final SliderSetting warpDelay = new SliderSetting("module.autoFarmRW.warpDelay", 8.0F, 3.0F, 30.0F, 1.0F, "module.autoFarmRW.warpDelay.desc", "с");
   private final SliderSetting radius = new SliderSetting("module.autoFarmRW.radius", 48.0F, 16.0F, 96.0F, 8.0F, "module.autoFarmRW.radius.desc", "");
   private final BooleanSetting sellWhenFull = new BooleanSetting("module.autoFarmRW.sellWhenFull", "module.autoFarmRW.sellWhenFull.desc", true);
   private final AutoFarmRW.HarvestProcess harvestProcess = new AutoFarmRW.HarvestProcess();
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private final StopWatch stopWatch3 = new StopWatch();
   private AutoFarmRW.Type type = AutoFarmRW.Type.READY;
   private volatile boolean flag;
   private World world;
   private BlockPos center;

   private AutoFarmRW() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.isEnabled()) {
         if (mc.player != null && ServerHelperModule.INSTANCE.method5181()) {
            this.reset();
         } else {
            ChatLogger.method2021("AutoFarmRW работает только на ReallyWorld");
            this.setToggled(false);
         }
      }
   }

   @Override
   public void onDisable() {
      this.reset();
      super.onDisable();
   }

   private void reset() {
      this.harvestProcess.close();
      this.type = AutoFarmRW.Type.READY;
      this.flag = false;
      this.world = null;
      this.center = null;
   }

   private void method3881(String var1) {
      ChatLogger.method2021("AutoFarmRW: " + var1);
      this.setToggled(false);
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100() && this.type == AutoFarmRW.Type.WARP && var1.getPacket3() instanceof PlayerPositionLookS2CPacket) {
         this.flag = true;
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.interactionManager != null && mc.getNetworkHandler() != null) {
         if (!mc.player.isAlive()) {
            this.method3881("сбор остановлен — игрок погиб");
         } else {
            switch (this.type) {
               case READY:
                  if (!method3884()) {
                     return;
                  }

                  this.flag = false;
                  this.stopWatch.reset();
                  this.type = AutoFarmRW.Type.WARP;
                  mc.player.networkHandler.sendChatCommand("warp farm");
                  break;
               case WARP:
                  if (this.flag) {
                     this.stopWatch.reset();
                     this.type = AutoFarmRW.Type.LOADING;
                  } else if (this.stopWatch.method7546(15000L)) {
                     this.method3881("не дождался /warp farm — проверь доступ к варпу");
                  }
                  break;
               case LOADING:
                  if (!this.stopWatch.method7546((long)this.warpDelay.getValue3() * 1000L)) {
                     return;
                  }

                  this.center = mc.player.getBlockPos();
                  this.world = mc.world;
                  if (!mc.world.isPosLoaded(this.center)) {
                     this.method3881("ферма не загрузилась — включи модуль ещё раз");
                     return;
                  }

                  this.stopWatch2.reset();
                  this.stopWatch3.reset();
                  this.type = AutoFarmRW.Type.FARM;
                  break;
               case FARM:
                  this.method3882();
            }
         }
      }
   }

   private void method3882() {
      if (mc.world != this.world) {
         this.method3881("сбор остановлен — мир сменился");
      } else {
         int var1 = (int)this.radius.getValue3();
         if (!method3886(this.center, mc.player.getBlockPos(), var1 + 8)) {
            this.method3881("сбор остановлен — игрок ушёл с фермы");
         } else {
            if (this.stopWatch2.method7546((long)this.sellInterval.getValue3() * 1000L)) {
               this.method3883();
            } else if (this.sellWhenFull.isEnabled() && this.stopWatch3.method7546(10000L) && mc.player.getInventory().getEmptySlot() == -1) {
               this.method3883();
            }

            if (mc.isPaused() || !method3884()) {
               this.harvestProcess.pause();
            } else if (!this.harvestProcess.isActive()) {
               this.harvestProcess.start(this.center, var1);
            }
         }
      }
   }

   private void method3883() {
      this.stopWatch2.reset();
      this.stopWatch3.reset();
      mc.player.networkHandler.sendChatCommand("sellfarm");
   }

   private static boolean method3884() {
      return mc.currentScreen == null
         && mc.getOverlay() == null
         && mc.player.currentScreenHandler == mc.player.playerScreenHandler
         && mc.player.currentScreenHandler.getCursorStack().isEmpty();
   }

   public static boolean method3885(BlockState var0) {
      if (var0.isOf(Blocks.PUMPKIN) || var0.isOf(Blocks.MELON)) {
         return true;
      } else {
         return !var0.isOf(Blocks.WHEAT) && !var0.isOf(Blocks.CARROTS) && !var0.isOf(Blocks.POTATOES) && !var0.isOf(Blocks.BEETROOTS)
            ? false
            : var0.getBlock() instanceof CropBlock var1 && var1.isMature(var0);
      }
   }

   public static boolean method3886(BlockPos var0, BlockPos var1, int var2) {
      if (var0 != null && var1 != null && var2 >= 0) {
         long var3 = (long)var1.getX() - var0.getX();
         long var5 = (long)var1.getZ() - var0.getZ();
         return var3 * var3 + var5 * var5 <= (long)var2 * var2 && Math.abs((long)var1.getY() - var0.getY()) <= 8L;
      } else {
         return false;
      }
   }

   public static boolean isHarvesting() {
      return INSTANCE.isEnabled() && INSTANCE.type == AutoFarmRW.Type.FARM && INSTANCE.harvestProcess.isHarvesting() && mc.currentScreen == null;
   }

   private static final class HarvestProcess implements IBaritoneProcess {
      private static final int SCAN_PER_TICK = 4096;
      private static final int SCAN_HALF_HEIGHT = 6;
      private static final int TARGET_TIMEOUT_TICKS = 400;
      private static final int BLACKLIST_TICKS = 600;
      private static final int HARVEST_GRACE_TICKS = 6;
      private static final double DROP_PRIORITY_SQ = 36.0;
      private final Set<BlockPos> crops = new HashSet<>();
      private final Map<BlockPos, Integer> blacklist = new HashMap<>();
      private final List<Runnable> settingRestores = new ArrayList<>();
      private IBaritone baritone;
      private World world;
      private BlockPos center;
      private int radius;
      private int scanCursor;
      private int ticks;
      private int targetSince;
      private int lastHarvestTick = 64536;
      private BlockPos cropTarget;
      private ItemEntity dropTarget;
      private boolean active;

      public void start(BlockPos var1, int var2) {
         if (MinecraftAccess.mc.player != null && MinecraftAccess.mc.world != null && var1 != null) {
            if (this.baritone == null) {
               this.baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
               this.baritone.getPathingControlManager().registerProcess(this);
            }

            this.baritone.getPathingBehavior().cancelEverything();
            if (this.settingRestores.isEmpty()) {
               this.configureSettings();
            }

            this.world = MinecraftAccess.mc.world;
            this.center = var1.toImmutable();
            this.radius = Math.max(8, Math.min(96, var2));
            this.scanCursor = 0;
            this.ticks = 0;
            this.lastHarvestTick = 64536;
            this.crops.clear();
            this.blacklist.clear();
            this.cropTarget = null;
            this.dropTarget = null;
            this.active = true;

            for (BlockPos var4 : BlockPos.iterate(this.center.add(-6, -6, -6), this.center.add(6, 6, 6))) {
               if (this.withinFarm(var4, this.radius) && this.world.isPosLoaded(var4) && AutoFarmRW.method3885(this.world.getBlockState(var4))) {
                  this.crops.add(var4.toImmutable());
               }
            }
         }
      }

      public void pause() {
         boolean var1 = this.active
            || this.baritone != null && this.baritone.getPathingControlManager().mostRecentInControl().filter(var1x -> var1x == this).isPresent();
         this.active = false;
         this.cropTarget = null;
         this.dropTarget = null;
         if (this.baritone != null && var1) {
            this.baritone.getInputOverrideHandler().clearAllKeys();
            this.baritone.getPathingBehavior().cancelEverything();
         }
      }

      public void close() {
         this.pause();

         for (int var1 = this.settingRestores.size() - 1; var1 >= 0; var1--) {
            this.settingRestores.get(var1).run();
         }

         this.settingRestores.clear();
         this.crops.clear();
         this.blacklist.clear();
         this.world = null;
         this.center = null;
      }

      public boolean isHarvesting() {
         return this.active && this.ticks - this.lastHarvestTick <= 6;
      }

      private <T> void override(Setting<T> var1, T var2) {
         T var3 = var1.value;
         this.settingRestores.add(() -> var1.value = var3);
         var1.value = var2;
      }

      private void configureSettings() {
         Settings var1 = BaritoneAPI.getSettings();
         this.override(var1.allowBreak, false);
         this.override(var1.allowPlace, false);
         this.override(var1.allowInventory, false);
         this.override(var1.autoTool, false);
         this.override(var1.assumeExternalAutoTool, true);
         this.override(var1.allowParkour, false);
         this.override(var1.allowParkourPlace, false);
         this.override(var1.allowSprint, false);
         this.override(var1.allowDownward, false);
         this.override(var1.maxFallHeightNoWater, 1);
         this.override(var1.allowWaterBucketFall, false);
         this.override(var1.allowVines, false);
         this.override(var1.walkWhileBreaking, false);
         this.override(var1.freeLook, true);
         this.override(var1.blockFreeLook, true);
         this.override(var1.antiCheatCompatibility, true);
         this.override(var1.disconnectOnArrival, false);
      }

      public PathingCommand onTick(boolean var1, boolean var2) {
         this.ticks++;
         this.baritone.getInputOverrideHandler().clearAllKeys();
         if (MinecraftAccess.mc.player == null || MinecraftAccess.mc.world != this.world || !MinecraftAccess.mc.player.isAlive()) {
            this.onLostControl();
            return pauseCommand();
         } else if (MinecraftAccess.mc.currentScreen == null && MinecraftAccess.mc.player.currentScreenHandler == MinecraftAccess.mc.player.playerScreenHandler
            )
          {
            if (!this.withinFarm(MinecraftAccess.mc.player.getBlockPos(), this.radius + 8)) {
               this.onLostControl();
               return new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);
            } else {
               this.scan();
               if (this.ticks % 100 == 0) {
                  this.blacklist.values().removeIf(var1x -> var1x <= this.ticks);
               }

               if ((this.cropTarget != null || this.dropTarget != null) && (var1 || this.ticks - this.targetSince > 400)) {
                  BlockPos var3 = this.cropTarget != null ? this.cropTarget : this.dropTarget.getBlockPos();
                  this.blacklist.put(var3.toImmutable(), this.ticks + 600);
                  this.cropTarget = null;
                  this.dropTarget = null;
               }

               if (this.cropTarget != null && (!this.world.isPosLoaded(this.cropTarget) || !AutoFarmRW.method3885(this.world.getBlockState(this.cropTarget)))) {
                  this.crops.remove(this.cropTarget);
                  this.cropTarget = null;
               }

               if (this.dropTarget != null
                  && (!this.dropTarget.isAlive() || !this.withinFarm(this.dropTarget.getBlockPos(), this.radius) || !this.canFit(this.dropTarget.getStack()))) {
                  this.dropTarget = null;
               }

               if (this.cropTarget == null && this.dropTarget == null) {
                  this.chooseTarget();
               }

               if (this.dropTarget != null) {
                  return path(new GoalBlock(this.dropTarget.getBlockPos()));
               } else if (this.cropTarget == null) {
                  return pauseCommand();
               } else {
                  var var4 = RotationUtils.reachable(
                     this.baritone.getPlayerContext(), this.cropTarget, MinecraftAccess.mc.player.getBlockInteractionRange()
                  );
                  if (var4.isEmpty()) {
                     return path(new GoalGetToBlock(this.cropTarget));
                  } else {
                     this.baritone.getLookBehavior().updateTarget((Rotation)var4.get(), true);
                     if (AutoFarmRW.method3885(this.world.getBlockState(this.cropTarget)) && this.baritone.getPlayerContext().isLookingAt(this.cropTarget)) {
                        this.baritone.getInputOverrideHandler().setInputForceState(Input.CLICK_LEFT, true);
                        this.lastHarvestTick = this.ticks;
                     }

                     return pauseCommand();
                  }
               }
            }
         } else {
            return pauseCommand();
         }
      }

      private void chooseTarget() {
         double var1 = Double.MAX_VALUE;
         Box var3 = new Box(this.center).expand(this.radius, 6.0, this.radius);

         for (ItemEntity var5 : this.world
            .getEntitiesByClass(
               ItemEntity.class, var3, var1x -> var1x.isAlive() && AutoFarmRW.items.contains(var1x.getStack().getItem()) && this.canFit(var1x.getStack())
            )) {
            if (this.withinFarm(var5.getBlockPos(), this.radius) && !this.isBlacklisted(var5.getBlockPos())) {
               double var6 = MinecraftAccess.mc.player.squaredDistanceTo(var5);
               if (var6 < var1) {
                  var1 = var6;
                  this.dropTarget = var5;
               }
            }
         }

         double var10 = Double.MAX_VALUE;
         var var11 = this.crops.iterator();

         while (var11.hasNext()) {
            BlockPos var7 = (BlockPos)var11.next();
            if (!this.world.isPosLoaded(var7) || !AutoFarmRW.method3885(this.world.getBlockState(var7))) {
               var11.remove();
            } else if (!this.isBlacklisted(var7)) {
               double var8 = MinecraftAccess.mc.player.squaredDistanceTo(var7.getX() + 0.5, var7.getY() + 0.5, var7.getZ() + 0.5);
               if (var8 < var10) {
                  var10 = var8;
                  this.cropTarget = var7;
               }
            }
         }

         if (this.dropTarget == null || !(var1 <= 36.0) && !(var1 <= var10)) {
            this.dropTarget = null;
         } else {
            this.cropTarget = null;
         }

         this.targetSince = this.ticks;
      }

      private void scan() {
         int var1 = this.radius * 2 + 1;
         byte var2 = 13;
         int var3 = var1 * var1 * var2;
         Mutable var4 = new Mutable();

         for (int var5 = 0; var5 < 4096; var5++) {
            int var6 = this.scanCursor;
            this.scanCursor = (this.scanCursor + 1) % var3;
            int var7 = var6 / (var1 * var1);
            int var8 = var6 % (var1 * var1);
            var4.set(this.center.getX() + var8 % var1 - this.radius, this.center.getY() + var7 - 6, this.center.getZ() + var8 / var1 - this.radius);
            if (this.withinFarm(var4, this.radius) && this.world.isPosLoaded(var4)) {
               if (AutoFarmRW.method3885(this.world.getBlockState(var4))) {
                  this.crops.add(var4.toImmutable());
               } else {
                  this.crops.remove(var4);
               }
            }
         }
      }

      private boolean canFit(ItemStack var1) {
         for (int var2 = 0; var2 < 36; var2++) {
            ItemStack var3 = MinecraftAccess.mc.player.getInventory().getStack(var2);
            if (var3.isEmpty() || ItemStack.areItemsAndComponentsEqual(var3, var1) && var3.getCount() < var3.getMaxCount()) {
               return true;
            }
         }

         return false;
      }

      private boolean withinFarm(BlockPos var1, int var2) {
         return AutoFarmRW.method3886(this.center, var1, var2);
      }

      private boolean isBlacklisted(BlockPos var1) {
         return this.blacklist.getOrDefault(var1, 0) > this.ticks;
      }

      private static PathingCommand path(Goal var0) {
         return new PathingCommand(var0, PathingCommandType.REVALIDATE_GOAL_AND_PATH);
      }

      private static PathingCommand pauseCommand() {
         return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
      }

      public boolean isActive() {
         return this.active;
      }

      public boolean isTemporary() {
         return false;
      }

      public void onLostControl() {
         boolean var1 = this.active;
         this.active = false;
         this.cropTarget = null;
         this.dropTarget = null;
         if (this.baritone != null && var1) {
            this.baritone.getInputOverrideHandler().clearAllKeys();
         }
      }

      public String displayName0() {
         return "AutoFarmRW";
      }
   }

   private static enum Type {
      READY,
      WARP,
      LOADING,
      FARM;
   }
}

package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.rotation.BcCameraTestnerv;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.Pathfinder;
import org.zenith.utility.misc.RotationRequest;

@ModuleInfo(
   name = "WarpFarm",
   category = ModuleCategory.PLAYER,
   description = "module.warpfarm.description"
)
public final class WarpFarmModule extends Module {
   public static final WarpFarmModule INSTANCE = new WarpFarmModule();
   public static final BlockPos blockPos = new BlockPos(-320, 59, 25);
   public static final double CONST_doubleValue = 4.0;
   public static final double CONST_doubleValue2 = 40000.0;
   private static final long CONST_longValue = 5000L;
   public static final double CONST_doubleValue3 = 16384.0;
   private static final double CONST_doubleValue4 = 900.0;
   private static final long CONST_longValue2 = 500L;
   private static final int CONST_intValue = 5;
   private static final int CONST_intValue2 = 1;
   private static final int CONST_intValue3 = 74;
   public static Pathfinder.Data data = new Pathfinder.Data(0 << 16 | (char)-15536, 192, 1000, 3, false, true);
   private static final Pathfinder.Data data2 = new Pathfinder.Data(2 << 16 | 18928, 200, 100, 13, false, true);
   private static volatile int intValue = -1;
   private static final AtomicLong atomicLong = new AtomicLong();
   private static final String text = "Монеты";
   private static final String text2 = "Опыт";
   private final ModeSetting farmMode = new ModeSetting("module.warpFarm.farmMode", "module.warpFarm.farmMode.desc", "Монеты", "Опыт");
   private final SliderSetting deathLimit = new SliderSetting("module.warpFarm.deathLimit", 3.0F, 1.0F, 50.0F, 1.0F, "module.warpFarm.deathLimit.desc", "");
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private BlockPos walkTarget;
   private BlockPos nextRouteBlock;
   private int pathFailStreak;
   private int lastAnarchy = -32768 << 16 | 0;
   private int intValue2;
   private boolean flag;
   private int intValue3;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;

   private WarpFarmModule() {
   }

   public static int getIntValue17() {
      return intValue;
   }

   public static long method4114() {
      return atomicLong.get();
   }

   public static String method4115() {
      return INSTANCE.farmMode.get();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.stopWalking();
      this.stopWatch.setLongValue(0L);
      this.stopWatch2.reset();
      this.lastAnarchy = -32768 << 16 | 0;
      this.intValue2 = 0;
      this.flag = false;
      intValue = -1;
      this.intValue3 = 0;
      this.flag2 = false;
      this.pathFailStreak = 0;
      if (mc.options != null) {
         this.flag3 = mc.options.pauseOnLostFocus;
         this.flag4 = true;
         mc.options.pauseOnLostFocus = false;
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.stopWalking();
      intValue = -1;
      if (this.flag4 && mc.options != null) {
         mc.options.pauseOnLostFocus = this.flag3;
      }

      this.flag4 = false;
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null) {
         if (mc.options != null) {
            mc.options.pauseOnLostFocus = false;
         }

         this.method4116();
         if (mc.currentScreen instanceof DeathScreen && mc.player.deathTime > 5) {
            mc.player.requestRespawn();
            this.stopWalking();
         } else if (mc.player.isDead() || mc.player.getHealth() <= 0.0F) {
            this.stopWalking();
         } else if (this.scopedRCTRepository().isActive()) {
            this.stopWalking();
         } else if (this.method4117()) {
            if (this.flag) {
               this.method4121();
            } else if (this.intValue3 >= (int)this.deathLimit.getValue3()) {
               ChatLogger.method2019("WarpFarm: " + this.intValue3 + " смертей на анке, ищу другую");
               this.method4118();
            } else {
               double var2;
               if ((var2 = this.horizontalDistanceSq(Vec3d.ofCenter(blockPos))) > 40000.0) {
                  this.stopWalking();
                  this.method4120();
               } else if (this.method4124()) {
                  ChatLogger.method2019("WarpFarm: посторонний игрок у центра, всем отправлен /hub");
                  this.method4119();
               } else {
                  Entity var4 = this.findHologram();
                  if (var4 != null) {
                     this.setWalkTarget(var4.getBlockPos());
                  } else {
                     if (!(var2 > 4.0)) {
                        this.stopWalking();
                        return;
                     }

                     this.setWalkTarget(blockPos);
                  }

                  this.method4123();
               }
            }
         }
      }
   }

   private void method4116() {
      boolean var1;
      if ((var1 = mc.player.isDead() || mc.player.getHealth() <= 0.0F) && !this.flag2) {
         this.intValue3++;
         ChatLogger.method2019("WarpFarm: смерть " + this.intValue3 + "/" + (int)this.deathLimit.getValue3());
      }

      this.flag2 = var1;
   }

   private boolean method4117() {
      int var1;
      if ((var1 = this.scopedRCTRepository().currentAnarchyHere()) != this.lastAnarchy) {
         this.lastAnarchy = var1;
         intValue = -1;
         this.intValue3 = 0;
         this.flag2 = false;
         this.stopWalking();
         this.flag = false;
         if (var1 != -1) {
            mc.player.networkHandler.sendChatCommand("warp pvp");
            this.stopWatch.reset();
            this.stopWatch2.reset();
            this.flag = true;
         }
      }

      if (var1 == -1) {
         this.method4118();
         return false;
      } else {
         return true;
      }
   }

   private void method4118() {
      this.stopWalking();
      this.flag = false;
      intValue = -1;
      this.intValue3 = 0;
      int var1 = this.scopedRCTRepository().currentAnarchyHere();

      int var2;
      do {
         while ((var2 = ThreadLocalRandom.current().nextInt(1, 75)) == var1) {
         }
      } while (var2 == this.intValue2);

      this.intValue2 = var2;
      this.scopedRCTRepository().reconnect(var2);
   }

   private void method4119() {
      intValue = -1;
      atomicLong.incrementAndGet();
      this.stopWalking();
      mc.player.networkHandler.sendChatCommand("hub");
      this.method4118();
   }

   private void method4120() {
      if (this.stopWatch.method7546(5000L)) {
         mc.player.networkHandler.sendChatCommand("warp pvp");
         this.stopWatch.reset();
         this.stopWatch2.reset();
      }
   }

   private void method4121() {
      this.stopWalking();
      if (this.stopWatch2.method7546(2000L)) {
         if (this.method4124()) {
            ChatLogger.method2019("WarpFarm: посторонний игрок у центра, всем отправлен /hub");
            this.method4119();
         } else {
            Entity var1 = this.findHologram();
            if (var1 == null) {
               ChatLogger.method2019("WarpFarm: голограмма не подходит под режим «" + this.farmMode.get() + "», ищу другую анку");
               this.method4118();
            } else {
               this.flag = false;
               intValue = this.scopedRCTRepository().currentAnarchyHere();
               ChatLogger.method2019("WarpFarm: анка №" + intValue + " пустая, голограмма «" + this.farmMode.get() + "» подходит");
            }
         }
      }
   }

   @EventTarget
   public void method4122(Event5 var1) {
      if (this.nextRouteBlock != null && mc.player != null) {
         Vec3d var2 = Pathfinder.method761(this.nextRouteBlock);
         Vec3d var3 = mc.player.getCameraPosVec(1.0F);
         Rotation var4 = Rotation.method3128(new Vec3d(var2.x, var3.y, var2.z), var3);
         this.scopedRotationManager().method7849(new RotationRequest(var4, () -> {
            BcCameraTestnerv var2x = this.scopedRotationManager().getBcCameraTestnerv();
            return var2x.method1851(var2x.getRotationConfig(), var4);
         }, this.scopedRotationManager().getBcCameraTestnerv().getRotationConfig()), 20, this);
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent var1) {
      if (this.nextRouteBlock != null && mc.player != null) {
         var1.method3044();
         var1.method3042(1.0F, 0.0F);
         var1.method3039(true);
         boolean var2 = mc.world != null
            && (
               mc.world.getBlockState(this.nextRouteBlock).getBlock() instanceof SlabBlock
                  || mc.world.getBlockState(this.nextRouteBlock.down()).getBlock() instanceof SlabBlock
            );
         var1.method3041(!var2 && this.nextRouteBlock.getY() > MathHelper.floor(mc.player.getY() + 0.01));
      }
   }

   private void setWalkTarget(BlockPos var1) {
      if (!var1.equals(this.walkTarget)) {
         this.walkTarget = var1;
         this.nextRouteBlock = null;
      }
   }

   private void method4123() {
      this.nextRouteBlock = null;
      if (this.walkTarget != null) {
         data = data2;
         BlockPos var1 = method4126(mc.world, mc.player.getEntityPos(), mc.player.getBlockPos());
         Pathfinder.Entry2 var2 = Pathfinder.method758(var1, this.walkTarget, data).orElse(null);
         if (var2 == null) {
            if (!mc.player.isOnGround()) {
               this.nextRouteBlock = this.walkTarget;
               this.pathFailStreak = 0;
            } else {
               if (++this.pathFailStreak >= 5) {
                  this.pathFailStreak = 0;
                  this.stopWalking();
                  this.method4120();
               }
            }
         } else {
            this.pathFailStreak = 0;
            this.nextRouteBlock = method4127(var2, mc.player.getEntityPos());
         }
      }
   }

   private void stopWalking() {
      this.walkTarget = null;
      this.nextRouteBlock = null;
   }

   private boolean method4124() {
      Vec3d var1 = Vec3d.ofCenter(blockPos);

      for (PlayerEntity var3 : mc.world.getPlayers()) {
         if (var3 != mc.player && !this.method4125(var3)) {
            double var4 = var3.getX() - var1.x;
            double var6 = var3.getZ() - var1.z;
            if (var4 * var4 + var6 * var6 <= 900.0) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean method4125(PlayerEntity var1) {
      return HeadlessBots.get(var1.getGameProfile().name()) != null;
   }

   public static BlockPos method4126(World var0, Vec3d var1, BlockPos var2) {
      if (Pathfinder.method762(var0, var2, false)) {
         return var2;
      } else {
         BlockPos var3 = null;
         double var4 = Double.MAX_VALUE;

         for (int var6 = -1; var6 <= 1; var6++) {
            for (int var7 = -1; var7 <= 1; var7++) {
               if (var6 != 0 || var7 != 0) {
                  BlockPos var8 = var2.add(var6, 0, var7);
                  if (Pathfinder.method762(var0, var8, false)) {
                     double var9 = var1.x - (var8.getX() + 0.5);
                     double var11 = var1.z - (var8.getZ() + 0.5);
                     double var13;
                     if ((var13 = var9 * var9 + var11 * var11) < var4) {
                        var3 = var8;
                        var4 = var13;
                     }
                  }
               }
            }
         }

         return var3 != null ? var3 : var2;
      }
   }

   public static BlockPos method4127(Pathfinder.Entry2 var0, Vec3d var1) {
      var var2 = var0.getItems();
      if (var2.size() < 2) {
         return null;
      } else {
         int var3 = 0;
         double var4 = Double.MAX_VALUE;

         for (int var6 = 0; var6 < var2.size(); var6++) {
            BlockPos var7 = (BlockPos)var2.get(var6);
            double var8 = var1.x - (var7.getX() + 0.5);
            double var10 = var1.y - var7.getY();
            double var12 = var1.z - (var7.getZ() + 0.5);
            double var14;
            if ((var14 = var8 * var8 + var10 * var10 + var12 * var12) <= var4) {
               var3 = var6;
               var4 = var14;
            }
         }

         int var16;
         return (var16 = var3 + 1) < var2.size() ? (BlockPos)var2.get(var16) : null;
      }
   }

   private Entity findHologram() {
      String var1 = this.farmMode.get().toLowerCase(Locale.ROOT);
      Entity var2 = null;
      double var3 = 16384.0;

      for (Entity var6 : mc.world.getEntities()) {
         double var7;
         if (var6 != mc.player && !((var7 = mc.player.squaredDistanceTo(var6)) > var3)) {
            Text var9 = var6 instanceof TextDisplayEntity var10 ? var10.getText() : var6.getCustomName();
            if (var9 != null && var9.getString().toLowerCase(Locale.ROOT).contains(var1)) {
               var2 = var6;
               var3 = var7;
            }
         }
      }

      return var2;
   }

   private double horizontalDistanceSq(Vec3d var1) {
      double var2 = mc.player.getX() - var1.x;
      double var4 = mc.player.getZ() - var1.z;
      return var2 * var2 + var4 * var4;
   }
}

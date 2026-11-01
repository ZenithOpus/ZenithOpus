package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import kotlin.collections.EmptyList;
import kotlin.collections.EmptySet;
import lombok.Generated;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.Event2;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.game.MineTargetFinder;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.misc.Util10;
import org.zenith.utility.misc.Vec2f;
import org.zenith.utility.network.InventoryUtils;

@ModuleInfo(
   name = "AutoMine",
   category = ModuleCategory.PLAYER,
   description = "module.automine.description"
)
public final class AutoMineModule extends Module {
   public static final AutoMineModule INSTANCE = new AutoMineModule();
   private static final int CONST_intValue = 40;
   private static final Set<Item> items = Set.of(
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
   private static final int CONST_intValue2 = 30;
   private static final int CONST_intValue3 = 150;
   private static final double CONST_doubleValue = 0.3;
   private static final double CONST_doubleValue2 = 0.2;
   private static final int CONST_intValue4 = 48;
   private static final double CONST_doubleValue3 = 6.0;
   private static final double[][] CONST_doubleValues = new double[][]{{0.5, 0.5}, {0.3, 0.3}, {0.7, 0.3}, {0.3, 0.7}, {0.7, 0.7}};
   private final ModeSetting mode = new ModeSetting(
      "module.autoMine.mode", "module.autoMine.mode.desc", "module.autoMine.modeNormal", "module.autoMine.modeNether", "module.autoMine.modeCombined"
   );
   public static final int CONST_intValue5 = 600;
   private MineTargetFinder.Data selectedMine;
   private MineTargetFinder.Data data;
   private int mineDataCooldown;
   private boolean combinedMineSwitched;
   private int ticks;
   private AutoMineModule.Data3 data3;
   private BlockPos blockPos;
   private boolean flag;
   private int intValue;
   private final List<AutoMineModule.Entry> items2 = new ArrayList<>();
   private int intValue2 = 2;
   private int intValue3 = -1;
   private boolean flag2;
   private boolean flag3;
   private int intValue4;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;

   private AutoMineModule() {
   }

   @Override
   public void onEnable() {
      this.selectedMine = null;
      this.data = null;
      this.mineDataCooldown = 0;
      this.combinedMineSwitched = false;
      this.items2.clear();
      this.intValue2 = 2;
      this.intValue3 = -1;
      this.flag2 = false;
      this.intValue4 = 0;
      this.method4388();
      ChatLogger.method2019("Смените версию на 1.17.1–1.20.6 в ViaFabric, если вы этого не сделали");
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.flag = false;
      this.releaseUse();
      if (mc.player != null) {
         this.method4358();
      }

      super.onDisable();
   }

   public boolean method4352() {
      return this.flag && this.isEnabled();
   }

   @EventTarget
   public void method4353(Event5 var1) {
      if (mc.player != null && mc.world != null) {
         this.ticks++;
         this.flag = false;
         var var2 = this.onTeleportation();
         if (var2 != null && this.data != null) {
            this.method4373();
            if (this.method4377() || !this.method4379()) {
               this.method4356(null);
            } else if (this.intValue > 0) {
               this.intValue--;
               this.method4356(null);
            } else {
               Vec3d var3 = mc.player.getEyePos();
               double var4 = mc.player.getBlockInteractionRange();
               Rotation var6 = rotationManager.method7844();
               if (this.data3 != null) {
                  this.data3 = this.method4361(this.data3, var3, var4, var6);
               }

               if (this.data3 == null) {
                  this.data3 = this.method4360(var3, var4, var6);
               }

               if (this.data3 == null) {
                  this.data3 = this.method4362(var3, var4, var6);
               }

               this.method4356(this.data3 == null ? null : this.method4359(this.data3, var3, var4, var6));
            }
         } else {
            this.data = null;
            this.method4388();
            this.method4358();
         }
      }
   }

   @EventTarget
   public void method4354(Event2 var1) {
      this.flag = false;
      this.blockPos = null;
      if (mc.player != null && this.data != null && !this.flag2 && this.intValue <= 0) {
         ItemStack var2 = mc.player.getMainHandStack();
         if (Util10.method2177(var2) && method4380(var2) >= 30) {
            if (mc.attackCooldown <= 0) {
               this.flag = true;
               if (mc.crosshairTarget instanceof BlockHitResult var3 && var3.getType() == Type.BLOCK) {
                  this.blockPos = var3.getBlockPos().toImmutable();
               }
            }
         }
      }
   }

   @EventTarget
   public void method4355(MoveInputEvent var1) {
      if (!zenith.getHolyWorldServerTracker().isActive() && mc.player != null) {
         var1.method3038(true);
      }
   }

   private void method4356(Rotation var1) {
      Rotation var2 = var1 != null ? var1 : rotationManager.method7844();
      rotationManager.method7849(new RotationRequest(var2, () -> this.method4357(var2), bcCameraTestnerv.getRotationConfig()), 1, this);
   }

   private Rotation method4357(Rotation var1) {
      Rotation var2 = rotationManager.method7844();
      Vec2f var3 = var2.method3131(var1);
      ThreadLocalRandom var4 = ThreadLocalRandom.current();
      float var5 = (float)Math.hypot(var3.getFloatValue(), var3.getFloatValue2());
      float var6 = 0.0F;
      float var7 = 0.0F;
      if (var5 > 0.2F) {
         float var8 = Math.min(var5 * (0.5F + var4.nextFloat() * 0.18F), 45.0F);
         var6 = var3.getFloatValue() / var5 * var8;
         var7 = var3.getFloatValue2() / var5 * var8 * 0.92F;
      }

      float var15 = 7.0F + var5 * 0.2F;
      this.floatValue = this.floatValue + MathHelper.clamp(var6 - this.floatValue, -var15, var15);
      this.floatValue2 = this.floatValue2 + MathHelper.clamp(var7 - this.floatValue2, -var15, var15);
      if (var5 <= 0.2F && Math.abs(this.floatValue) + Math.abs(this.floatValue2) < 0.3F) {
         this.floatValue = this.floatValue2 = 0.0F;
         return var2;
      } else {
         float var9 = 0.05F + (Math.abs(this.floatValue) + Math.abs(this.floatValue2)) * 0.012F;
         float var10 = this.floatValue + (float)var4.nextGaussian() * var9 + this.floatValue3;
         float var11 = this.floatValue2 + (float)var4.nextGaussian() * var9 * 0.7F + this.floatValue4;
         float var12;
         if (!((var12 = Rotation.method3138()) > 0.0F) || Float.isInfinite(var12)) {
            var12 = 0.15F;
         }

         float var13 = Math.round(var10 / var12) * var12;
         float var14 = Math.round(var11 / var12) * var12;
         this.floatValue3 = var10 - var13;
         this.floatValue4 = var11 - var14;
         return new Rotation(var2.getFloatValue() + var13, var2.getFloatValue2() + var14, true);
      }
   }

   private void method4358() {
      Rotation var1 = rotationManager.getCurrentRotation();
      if (var1 != null && rotationManager.getModule() == this) {
         mc.player.setYaw(var1.getFloatValue());
         mc.player.setPitch(var1.getFloatValue2());
         this.floatValue = this.floatValue2 = this.floatValue3 = this.floatValue4 = 0.0F;
      }
   }

   private Rotation method4359(AutoMineModule.Data3 var1, Vec3d var2, double var3, Rotation var5) {
      if (method4386(method4385(var2, var3, var5), var1.pos())) {
         return null;
      } else {
         Rotation var6 = Rotation.method3128(var1.getVec3d(), var2);
         return var5.method3141(var5.method3131(var6));
      }
   }

   private AutoMineModule.Data3 method4360(Vec3d var1, double var2, Rotation var4) {
      BlockHitResult var5 = method4385(var1, var2, var4);
      if (var5 != null && var5.getType() == Type.BLOCK) {
         BlockPos var6 = var5.getBlockPos();
         return this.method4382(var6) && this.method4372(var6) ? new AutoMineModule.Data3(var6.toImmutable(), var5.getPos()) : null;
      } else {
         return null;
      }
   }

   private AutoMineModule.Data3 method4361(AutoMineModule.Data3 var1, Vec3d var2, double var3, Rotation var5) {
      BlockPos var6 = var1.pos();
      if (this.method4382(var6) && method4384(var6, var2, var3)) {
         if (method4386(method4385(var2, var3, var5), var6)) {
            return var1;
         } else if (this.method4364(var6, var1.getVec3d(), var2, var3)) {
            return var1;
         } else {
            Vec3d var7 = var5.method3135();
            AutoMineModule.Data3 var8 = null;
            double var9 = Double.MAX_VALUE;

            for (Direction var14 : Direction.values()) {
               if (this.method4366(var6, var14, var2)) {
                  AutoMineModule.Data3 var15 = this.method4363(var6, var14, method4367(var6, var14, var2, var7), var2, var3, var5);
                  double var16;
                  if (var15 != null && (var16 = method4365(var5, Rotation.method3128(var15.getVec3d(), var2))) < var9) {
                     var9 = var16;
                     var8 = var15;
                  }
               }
            }

            return var8;
         }
      } else {
         return null;
      }
   }

   private AutoMineModule.Data3 method4362(Vec3d var1, double var2, Rotation var4) {
      MineTargetFinder.Data var5 = this.data;
      int var6 = (int)Math.ceil(var2) + 1;
      BlockPos var7 = BlockPos.ofFloored(var1);
      int var8 = Math.max(var5.getIntValue(), var7.getX() - var6);
      int var9 = Math.min(var5.getIntValue2(), var7.getX() + var6);
      int var10 = Math.max(var5.getIntValue3(), var7.getY() - var6);
      int var11 = Math.min(var5.getIntValue4(), var7.getY() + var6);
      int var12 = Math.max(var5.getIntValue5(), var7.getZ() - var6);
      int var13 = Math.min(var5.getIntValue6(), var7.getZ() + var6);
      if (var8 <= var9 && var10 <= var11 && var12 <= var13) {
         Vec3d var14 = var4.method3135();
         ArrayList<AutoMineModule.Data2> var15 = new ArrayList();
         Mutable var16 = new Mutable();

         for (int var17 = var8; var17 <= var9; var17++) {
            for (int var18 = var10; var18 <= var11; var18++) {
               for (int var19 = var12; var19 <= var13; var19++) {
                  var16.set(var17, var18, var19);
                  if (this.method4382(var16) && method4384(var16, var1, var2)) {
                     BlockPos var20 = var16.toImmutable();
                     if (this.method4372(var20)) {
                        double var21 = Math.sqrt(MineTargetFinder.method1939(var1, var20)) * 6.0;

                        for (Direction var26 : Direction.values()) {
                           if (this.method4366(var20, var26, var1)) {
                              Vec3d var27 = method4367(var20, var26, var1, var14);
                              var15.add(new AutoMineModule.Data2(var20, var26, var27, var21 + method4365(var4, Rotation.method3128(var27, var1))));
                           }
                        }
                     }
                  }
               }
            }
         }

         var15.sort(Comparator.comparingDouble(AutoMineModule.Data2::getDoubleValue));
         int var28 = 0;

         for (Object var30_o : (Iterable<?>)(Object)(var15)) {
            AutoMineModule.Data2 var30 = (AutoMineModule.Data2)var30_o;
            if (var28++ >= 48) {
               break;
            }

            AutoMineModule.Data3 var31 = this.method4363(var30.pos(), var30.getDirection(), var30.getVec3d(), var1, var2, var4);
            if (var31 != null) {
               return var31;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private AutoMineModule.Data3 method4363(BlockPos var1, Direction var2, Vec3d var3, Vec3d var4, double var5, Rotation var7) {
      ArrayList<Vec3d> var8 = new ArrayList(CONST_doubleValues.length + 1);
      var8.add(var3);

      for (double[] var12 : CONST_doubleValues) {
         var8.add(method4368(var1, var2, var12[0], var12[1]));
      }

      var8.subList(1, var8.size()).sort(Comparator.comparingDouble(var2x -> method4365(var7, Rotation.method3128(var2x, var4))));

      for (Object var14_o : (Iterable<?>)(Object)(var8)) {
         Vec3d var14 = (Vec3d)var14_o;
         if (this.method4364(var1, var14, var4, var5)) {
            return new AutoMineModule.Data3(var1, var14);
         }
      }

      return null;
   }

   private boolean method4364(BlockPos var1, Vec3d var2, Vec3d var3, double var4) {
      BlockHitResult var6 = method4385(var3, var4, Rotation.method3128(var2, var3));
      return method4386(var6, var1) && !this.method4387(var3, var6.getPos());
   }

   private static double method4365(Rotation var0, Rotation var1) {
      Vec2f var2 = var0.method3131(var1);
      return Math.abs(var2.getFloatValue()) + Math.abs(var2.getFloatValue2()) * 1.5;
   }

   private boolean method4366(BlockPos var1, Direction var2, Vec3d var3) {
      Vec3i var4 = var2.getVector();
      Vec3d var5 = var1.toCenterPos().add(var4.getX() * 0.5, var4.getY() * 0.5, var4.getZ() * 0.5);
      return (var3.x - var5.x) * var4.getX() + (var3.y - var5.y) * var4.getY() + (var3.z - var5.z) * var4.getZ() <= 0.02
         ? false
         : !mc.world.getBlockState(var1.offset(var2)).isOpaqueFullCube();
   }

   private static Vec3d method4367(BlockPos var0, Direction var1, Vec3d var2, Vec3d var3) {
      Axis var4 = var1.getAxis();
      double var5 = method4369(var0, var1);
      double var7 = var4.choose(var3.x, var3.y, var3.z);
      Vec3d var9 = var0.toCenterPos();
      double var10;
      if (Math.abs(var7) > 1.0E-4 && (var10 = (var5 - var4.choose(var2.x, var2.y, var2.z)) / var7) > 0.0) {
         var9 = var2.add(var3.multiply(var10));
      }

      double var12;
      switch (var4) {
         case X:
            var10 = var9.y - var0.getY();
            var12 = var9.z - var0.getZ();
            break;
         case Y:
            var10 = var9.x - var0.getX();
            var12 = var9.z - var0.getZ();
            break;
         default:
            var10 = var9.x - var0.getX();
            var12 = var9.y - var0.getY();
      }

      return method4368(var0, var1, MathHelper.clamp(var10, 0.2, 0.8), MathHelper.clamp(var12, 0.2, 0.8));
   }

   private static Vec3d method4368(BlockPos var0, Direction var1, double var2, double var4) {
      double var6 = method4369(var0, var1) - var1.getDirection().offset() * 0.01;

      return switch (var1.getAxis()) {
         case X -> new Vec3d(var6, var0.getY() + var2, var0.getZ() + var4);
         case Y -> new Vec3d(var0.getX() + var2, var6, var0.getZ() + var4);
         case Z -> new Vec3d(var0.getX() + var2, var0.getY() + var4, var6);
      };
   }

   private static double method4369(BlockPos var0, Direction var1) {
      int var2 = var1.getAxis().choose(var0.getX(), var0.getY(), var0.getZ());
      return var1.getDirection() == AxisDirection.POSITIVE ? var2 + 1 : var2;
   }

   private Collection<BlockPos> method4370(BlockPos var1) {
      return this.intValue2 > 0 ? method4371(var1) : List.of(var1);
   }

   private static List<BlockPos> method4371(BlockPos var0) {
      ArrayList var1 = new ArrayList(27);

      for (BlockPos var3 : BlockPos.iterate(var0.add(-1, -1, -1), var0.add(1, 1, 1))) {
         var1.add(var3.toImmutable());
      }

      return var1;
   }

   private boolean method4372(BlockPos var1) {
      if (!mc.player.isOnGround()) {
         return true;
      } else {
         var var2 = this.method4370(var1);
         BlockPos var3 = mc.player.getBlockPos();
         if (!var2.contains(var3.down())) {
            return true;
         } else {
            Mutable var4 = var3.down().mutableCopy();
            int var5 = 0;

            while (var5 <= 3) {
               if (!var2.contains(var4) && this.method4383(var4)) {
                  return true;
               }

               var5++;
               var4.move(Direction.DOWN);
            }

            return false;
         }
      }
   }

   private void method4373() {
      if (this.blockPos != null && mc.world.getBlockState(this.blockPos).isAir()) {
         this.method4374(this.blockPos);
      }

      this.blockPos = null;
      var var1 = this.items2.iterator();

      while (var1.hasNext()) {
         AutoMineModule.Entry var2 = (AutoMineModule.Entry)var1.next();
         var2.getItems().removeIf(var1x -> {
            if (!mc.world.getBlockState(var1x).isAir()) {
               return false;
            } else {
               var2.flag2 = true;
               return true;
            }
         });
         var var10000 = var2.getItems();
         if ((var10000.isEmpty()) || this.ticks >= var2.getIntValue()) {
            this.intValue2 = var2.flag2 ? Math.min(3, this.intValue2 + 1) : Math.max(-2, this.intValue2 - 1);
            var1.remove();
         }
      }
   }

   private void method4374(BlockPos var1) {
      if (this.data3 != null && this.data3.pos().equals(var1)) {
         this.data3 = null;
      }

      ThreadLocalRandom var2 = ThreadLocalRandom.current();
      if (var2.nextFloat() < 0.005F) {
         this.intValue = 6 + var2.nextInt(10);
      }

      if (MineTargetFinder.method1935(this.data, var1)) {
         HashSet var3 = new HashSet();

         for (BlockPos var5 : method4371(var1)) {
            if (!var5.equals(var1) && MineTargetFinder.method1935(this.data, var5) && !mc.world.getBlockState(var5).isAir()) {
               var3.add(var5);
            }
         }

         if (!var3.isEmpty()) {
            this.items2.add(new AutoMineModule.Entry(var3, this.ticks + this.method4375(), this.intValue2 > 0));
         }
      }
   }

   private int method4375() {
      PlayerListEntry var1 = mc.getNetworkHandler() == null ? null : mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
      return MathHelper.clamp((var1 == null ? 100 : var1.getLatency()) * 2 / 50 + 4, 4, 30);
   }

   private boolean method4376(BlockPos var1) {
      for (AutoMineModule.Entry var3 : this.items2) {
         if (var3.isFlag() && var3.getItems().contains(var1)) {
            return true;
         }
      }

      return false;
   }

   private boolean method4377() {
      PlayerInventory var1 = mc.player.getInventory();
      if (!this.flag2) {
         ItemStack var4 = mc.player.getMainHandStack();
         if (Util10.method2177(var4) && method4380(var4) < 30) {
            this.flag2 = true;
            this.intValue3 = var1.getSelectedSlot();
            this.intValue4 = 2 + ThreadLocalRandom.current().nextInt(3);
            this.data3 = null;
            return true;
         } else {
            return false;
         }
      } else if (this.intValue4 > 0) {
         this.intValue4--;
         return true;
      } else {
         ItemStack var2 = PlayerInventory.isValidHotbarIndex(this.intValue3) ? var1.getStack(this.intValue3) : ItemStack.EMPTY;
         if (Util10.method2177(var2) && method4380(var2) < 150 && var2.getDamage() != 0) {
            if (mc.player.getOffHandStack().isOf(Items.EXPERIENCE_BOTTLE)) {
               if (var1.getSelectedSlot() != this.intValue3) {
                  var1.setSelectedSlot(this.intValue3);
                  this.intValue4 = 2 + ThreadLocalRandom.current().nextInt(3);
                  return true;
               } else {
                  mc.options.useKey.setPressed(true);
                  this.flag3 = true;
                  return true;
               }
            } else {
               this.releaseUse();

               for (int var3 = 0; var3 < 9; var3++) {
                  if (var1.getStack(var3).isOf(Items.EXPERIENCE_BOTTLE)) {
                     if (var1.getSelectedSlot() != var3) {
                        var1.setSelectedSlot(var3);
                        this.intValue4 = 2 + ThreadLocalRandom.current().nextInt(3);
                        return true;
                     }

                     mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
                     this.intValue4 = 6 + ThreadLocalRandom.current().nextInt(4);
                     return true;
                  }
               }

               Slot var5 = InventoryUtils.method906(Items.EXPERIENCE_BOTTLE);
               if (var5 == null) {
                  ChatLogger.method2019("AutoMine: закончились пузырьки опыта");
                  this.method4378();
                  this.toggle();
                  return true;
               } else {
                  InventoryUtils.method900(var5, Hand.OFF_HAND, true);
                  this.intValue4 = 6 + ThreadLocalRandom.current().nextInt(4);
                  return true;
               }
            }
         } else {
            this.method4378();
            return false;
         }
      }
   }

   private void method4378() {
      this.releaseUse();
      this.flag2 = false;
      if (PlayerInventory.isValidHotbarIndex(this.intValue3) && mc.player.getInventory().getSelectedSlot() != this.intValue3) {
         mc.player.getInventory().setSelectedSlot(this.intValue3);
      }
   }

   private void releaseUse() {
      if (this.flag3) {
         mc.options.useKey.setPressed(false);
         this.flag3 = false;
      }
   }

   private boolean method4379() {
      PlayerInventory var1 = mc.player.getInventory();
      ItemStack var2 = mc.player.getMainHandStack();
      if (Util10.method2177(var2) && method4380(var2) >= 30) {
         this.intValue3 = var1.getSelectedSlot();
         return true;
      } else {
         int var3 = -1;

         for (int var4 = 0; var4 < 9; var4++) {
            ItemStack var5 = var1.getStack(var4);
            if (Util10.method2177(var5) && method4380(var5) >= 30 && (var3 == -1 || var4 == this.intValue3)) {
               var3 = var4;
            }
         }

         if (var3 >= 0) {
            var1.setSelectedSlot(var3);
         }

         return false;
      }
   }

   private static int method4380(ItemStack var0) {
      return var0.isDamageable() ? var0.getMaxDamage() - var0.getDamage() : 32767 << 16 | (char)-1;
   }

   private boolean method4381(BlockPos var1, BlockState var2) {
      return !var2.isAir() && var2.getFluidState().isEmpty() && var2.getHardness(mc.world, var1) >= 0.0F && !var2.isOf(Blocks.OBSIDIAN);
   }

   private boolean method4382(BlockPos var1) {
      return this.data != null && MineTargetFinder.method1935(this.data, var1) && this.method4381(var1, mc.world.getBlockState(var1)) && !this.method4376(var1);
   }

   private boolean method4383(BlockPos var1) {
      return !mc.world.getBlockState(var1).getCollisionShape(mc.world, var1).isEmpty();
   }

   private static boolean method4384(BlockPos var0, Vec3d var1, double var2) {
      double var4 = var2 - 0.3;
      return MineTargetFinder.method1939(var1, var0) <= var4 * var4;
   }

   private static BlockHitResult method4385(Vec3d var0, double var1, Rotation var3) {
      return TargetUtils.method3205(var0, var1, var3, false);
   }

   private static boolean method4386(BlockHitResult var0, BlockPos var1) {
      return var0 != null && var0.getType() == Type.BLOCK && var1.equals(var0.getBlockPos());
   }

   private boolean method4387(Vec3d var1, Vec3d var2) {
      Box var3 = new Box(var1, var2).expand(1.0);

      for (Entity var5 : mc.world.getOtherEntities(mc.player, var3, var0 -> !var0.isSpectator() && var0.canHit())) {
         if (var5.getBoundingBox().expand(var5.getTargetingMargin()).raycast(var1, var2).isPresent()) {
            return true;
         }
      }

      return false;
   }

   private void method4388() {
      this.data3 = null;
      this.blockPos = null;
      this.flag = false;
      this.intValue = 0;
      this.floatValue = this.floatValue2 = this.floatValue3 = this.floatValue4 = 0.0F;
      if (this.flag2 && mc.player != null) {
         this.method4378();
      }

      this.releaseUse();
   }

   private void onDrop() {
      if (mc.currentScreen != null && mc.currentScreen.getTitle().getString().toLowerCase(Locale.ROOT).contains("для продажи")) {
         if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var1) {
            int var3;
            if (this.moveBuyerResources() && (var3 = this.getContainerSlotCount(var1)) > 0) {
               InventoryUtils.method904(var3 - 1, 0, SlotActionType.QUICK_MOVE, false);
            }

            Slot var4 = this.findTrashSlot();
            if (var4 != null) {
               InventoryUtils.method903(var4, 1, SlotActionType.THROW, false);
            }
         }
      }
   }

   private boolean moveBuyerResources() {
      boolean var1 = false;
      int var2 = 0;

      for (Slot var4 : mc.player.currentScreenHandler.slots) {
         if (this.isPlayerInventorySlot(var4) && this.isBuyerResource(var4.getStack())) {
            if (var2 > 5) {
               break;
            }

            InventoryUtils.method903(var4, 0, SlotActionType.QUICK_MOVE, false);
            var2++;
            var1 = true;
         }
      }

      return var1;
   }

   private boolean isBuyerResource(ItemStack var1) {
      return !var1.isEmpty() && items.contains(var1.getItem());
   }

   private Slot findTrashSlot() {
      return mc.player
         .currentScreenHandler
         .slots
         .stream()
         .filter(this::isPlayerInventorySlot)
         .filter(var1 -> this.isTrash(var1.getStack()))
         .findFirst()
         .orElse(null);
   }

   private boolean isPlayerInventorySlot(Slot var1) {
      if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler var2) {
         int var4 = this.getContainerSlotCount(var2);
         return var1.inventory == mc.player.getInventory() || var1.id >= var4 && var1.id < var4 + 36;
      } else {
         return var1.inventory == mc.player.getInventory() && var1.id >= 9 && var1.id <= 44;
      }
   }

   private int getContainerSlotCount(GenericContainerScreenHandler var1) {
      return Math.min(var1.getInventory().size(), Math.max(0, mc.player.currentScreenHandler.slots.size() - 36));
   }

   private boolean isTrash(ItemStack var1) {
      return !var1.isEmpty() && !var1.isOf(Items.PLAYER_HEAD) && !var1.isOf(Items.TRIPWIRE_HOOK) && !this.isBuyerResource(var1)
         ? var1.getItem() instanceof BlockItem var2 && !MineTargetFinder.method1934(var2.getBlock())
         : false;
   }

   private List<AutoMineModule.Data> onTeleportation() {
      this.data = null;
      if (zenith.getHolyWorldServerTracker().isActive()) {
         return null;
      } else if (zenith.getHolyWorldServerTracker().currentAnarchyHere() == -1) {
         this.reconnectToRandomAnarchy();
         return null;
      } else if (this.isWaitingForMineData()) {
         return null;
      } else if (this.mode.method2101("module.autoMine.modeCombined")) {
         return this.selectCombinedMine();
      } else {
         this.combinedMineSwitched = false;
         this.selectedMine = null;
         MineTargetFinder.Data var1 = MineTargetFinder.method1933(this.mode.method2101("module.autoMine.modeNether"));
         if (MineTargetFinder.method1938(var1, mc.world) && !(this.getMineDistance(var1) > 25.0)) {
            return this.getAvailableMine(var1);
         } else {
            this.handleWarpTp();
            return null;
         }
      }
   }

   private List<AutoMineModule.Data> selectCombinedMine() {
      MineTargetFinder.Data var1 = MineTargetFinder.method1933(false);
      MineTargetFinder.Data var2 = MineTargetFinder.method1933(true);
      if (MineTargetFinder.method1938(var1, mc.world) && MineTargetFinder.method1938(var2, mc.world)) {
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
               return this.method4389(var2, var4);
            }

            if (!var7 && var5) {
               return this.method4389(var1, var3);
            }

            if (this.combinedMineSwitched) {
               this.reconnectToRandomAnarchy();
               return null;
            }
         }

         if (!var5 && !var6) {
            MineTargetFinder.Data var10 = this.selectedMine == null ? var1 : this.selectedMine;
            if (MineTargetFinder.method1938(var10, mc.world) && !(this.getMineDistance(var10) > 25.0)) {
               this.reconnectToRandomAnarchy();
               return null;
            } else {
               this.handleWarpTp();
               return null;
            }
         } else {
            MineTargetFinder.Data var9 = this.selectedMine;
            boolean var8 = var6 && (!var5 || this.getFillPercentage(var4) > this.getFillPercentage(var3));
            this.selectedMine = var8 ? var2 : var1;
            if (!this.selectedMine.equals(var9)) {
               this.combinedMineSwitched = var9 != null;
               this.sendWarpCommand();
               return null;
            } else {
               return var8 ? this.method4389(var2, var4) : this.method4389(var1, var3);
            }
         }
      } else {
         this.handleWarpTp();
         return null;
      }
   }

   private List<AutoMineModule.Data> method4389(MineTargetFinder.Data var1, List<AutoMineModule.Data> var2) {
      this.data = var1;
      return var2;
   }

   private boolean isWaitingForMineData() {
      if (mc.player.age >= 40 && this.mineDataCooldown <= 0) {
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
      return this.mode.method2101("module.autoMine.modeCombined") && this.selectedMine != null
         ? this.selectedMine
         : MineTargetFinder.method1933(this.mode.method2101("module.autoMine.modeNether"));
   }

   private boolean hasLoadedMineBlocks(MineTargetFinder.Data var1) {
      for (BlockPos var3 : BlockPos.iterate(
         var1.getIntValue(), var1.getIntValue3(), var1.getIntValue5(), var1.getIntValue2(), var1.getIntValue4(), var1.getIntValue6()
      )) {
         if (mc.world.isChunkLoaded(var3) && !mc.world.getBlockState(var3).isAir()) {
            return true;
         }
      }

      return false;
   }

   private double getMineDistance(MineTargetFinder.Data var1) {
      double var2 = (var1.getIntValue() + var1.getIntValue2() + 1.0) * 0.5;
      double var4 = (var1.getIntValue5() + var1.getIntValue6() + 1.0) * 0.5;
      double var6 = Math.abs(mc.player.getX() - var2);
      double var8 = Math.abs(mc.player.getZ() - var4);
      return Math.hypot(var6, var8);
   }

   private List<AutoMineModule.Data> getAvailableMine(MineTargetFinder.Data var1) {
      var var2 = this.getArena(var1);
      if (!this.isMineAvailable(var2)) {
         this.reconnectToRandomAnarchy();
         return null;
      } else {
         return this.method4389(var1, var2);
      }
   }

   private boolean isMineAvailable(List<AutoMineModule.Data> var1) {
      return var1.size() - this.countAirBlocks(var1) < 600L ? false : !mc.player.isOnGround() || this.hasReachableBlockByHeight(var1);
   }

   private boolean hasReachableBlockByHeight(List<AutoMineModule.Data> var1) {
      int var2 = MathHelper.floor(mc.player.getEyeY() + mc.player.getBlockInteractionRange() - 1.0);
      return var1.stream().anyMatch(var1x -> !var1x.state().isAir() && !var1x.state().isOf(Blocks.OBSIDIAN) && var1x.pos().getY() <= var2);
   }

   private long countAirBlocks(List<AutoMineModule.Data> var1) {
      return var1.stream().filter(var0 -> var0.state().isAir()).count();
   }

   private double getFillPercentage(List<AutoMineModule.Data> var1) {
      return (var1.isEmpty()) ? 0.0 : 1.0 - (double)this.countAirBlocks(var1) / var1.size();
   }

   private List<AutoMineModule.Data> getArena(MineTargetFinder.Data var1) {
      return this.getBlocksInZone(
         new BlockPos(var1.getIntValue(), var1.getIntValue3(), var1.getIntValue5()),
         new BlockPos(var1.getIntValue2(), var1.getIntValue4(), var1.getIntValue6())
      );
   }

   private List<AutoMineModule.Data> getBlocksInZone(BlockPos var1, BlockPos var2) {
      ArrayList<AutoMineModule.Data> var3 = new ArrayList<>();
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
               var3.add(new AutoMineModule.Data(var13, mc.world.getBlockState(var13)));
            }
         }
      }

      return var3;
   }

   public void handleWarpTp() {
      if (mc.player.age % 60 == 0) {
         this.sendWarpCommand();
      }
   }

   private void sendWarpCommand() {
      boolean var1 = this.mode.method2101("module.autoMine.modeCombined")
         ? MineTargetFinder.method1933(true).equals(this.selectedMine)
         : this.mode.method2101("module.autoMine.modeNether");
      mc.getNetworkHandler().sendChatCommand(var1 ? "warp mine2" : "warp mine");
      this.mineDataCooldown = 20;
   }

   private void reconnectToRandomAnarchy() {
      this.selectedMine = null;
      this.mineDataCooldown = 0;
      this.combinedMineSwitched = false;
      this.items2.clear();
      this.method4388();
      int var1 = zenith.getHolyWorldServerTracker().currentAnarchyHere();

      int var2;
      do {
         while ((var2 = ThreadLocalRandom.current().nextInt(1, 64)) == var1) {
         }
      } while (var2 == 2 || var2 == 17 || var2 == 33 || var2 == 49);

      zenith.getHolyWorldServerTracker().reconnect(var2);
   }

   @Generated
   public ModeSetting getMineMode() {
      return this.mode;
   }

   private static final class Data {
      private final BlockPos blockPos;
      private final BlockState blockState;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public BlockState blockState() {
         return this.blockState;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.blockState, other.blockState);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, blockState);
      }

      @Override
      public String toString() {
         return "Data[" + "blockPos=" + this.blockPos + ", " + "blockState=" + this.blockState + "]";
      }

      private Data(BlockPos var1, BlockState var2) {
         this.blockPos = var1;
         this.blockState = var2;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public BlockState state() {
         return this.blockState;
      }
   }

   private static final class Data2 {
      private final BlockPos blockPos;
      private final Direction direction;
      private final Vec3d vec3d;
      private final double doubleValue;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public Direction direction() {
         return this.direction;
      }

      public Vec3d vec3d() {
         return this.vec3d;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.direction, other.direction) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.doubleValue, other.doubleValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, direction, vec3d, doubleValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "blockPos=" + this.blockPos + ", " + "direction=" + this.direction + ", " + "vec3d=" + this.vec3d + ", " + "doubleValue=" + this.doubleValue + "]";
      }

      private Data2(BlockPos var1, Direction var2, Vec3d var3, double var4) {
         this.blockPos = var1;
         this.direction = var2;
         this.vec3d = var3;
         this.doubleValue = var4;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public Direction getDirection() {
         return this.direction;
      }

      public Vec3d getVec3d() {
         return this.vec3d;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }
   }

   private static final class Data3 {
      private final BlockPos blockPos;
      private final Vec3d vec3d;

      public BlockPos blockPos() {
         return this.blockPos;
      }

      public Vec3d vec3d() {
         return this.vec3d;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.blockPos, other.blockPos) && java.util.Objects.equals(this.vec3d, other.vec3d);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(blockPos, vec3d);
      }

      @Override
      public String toString() {
         return "Data3[" + "blockPos=" + this.blockPos + ", " + "vec3d=" + this.vec3d + "]";
      }

      private Data3(BlockPos var1, Vec3d var2) {
         this.blockPos = var1;
         this.vec3d = var2;
      }

      public BlockPos pos() {
         return this.blockPos;
      }

      public Vec3d getVec3d() {
         return this.vec3d;
      }
   }

   private static final class Entry {
      private final Set<BlockPos> items;
      private final int intValue;
      private final boolean flag;
      private boolean flag2;

      private Entry(Set<BlockPos> var1, int var2, boolean var3) {
         this.items = var1;
         this.intValue = var2;
         this.flag = var3;
      }

      public Set<BlockPos> getItems() {
         return this.items;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }
}

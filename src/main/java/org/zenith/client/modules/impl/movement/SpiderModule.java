package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.stream.Stream;
import lombok.Generated;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.Event2;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.KeyBindUtils;

@ModuleInfo(
   name = "Spider",
   category = ModuleCategory.MOVEMENT,
   description = "module.spider.description"
)
public final class SpiderModule extends Module {
   public static final SpiderModule INSTANCE = new SpiderModule();
   private static final double CONST_doubleValue = 0.3F;
   private static final int CONST_intValue = 44;
   private final ModeSetting mode = new ModeSetting("module.spider.mode", "module.spider.mode.desc");
   private final ModeSetting.Entry entry = new ModeSetting.Entry(this.mode, "module.spider.mode.blocks").method2103();
   private final ModeSetting.Entry entry2 = new ModeSetting.Entry(this.mode, "module.spider.mode.water");
   private final ModeSetting.Entry entry3 = new ModeSetting.Entry(this.mode, "module.spider.mode.head");
   private long longValue;
   private int intValue = -1;
   private int intValue2 = -1;
   private boolean flag;
   private Slot slot;

   private SpiderModule() {
   }

   @EventTarget
   public void method7024(Event5 var1) {
      if (mc.player != null && mc.world != null) {
         if (this.entry2.isSelected()) {
            if (mc.player.horizontalCollision) {
               BlockPos var8 = this.method7033();
               if (var8 != null) {
                  Rotation var9 = RotationUtils.method6913(this.method7034(var8));
                  rotationManager.method7848(
                     new RotationRequest(
                        var9, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var9), bcCameraTestnerv.getRotationConfig()
                     ),
                     1,
                     this,
                     2
                  );
               }
            }
         } else if (this.entry.isSelected()) {
            boolean var2 = mc.player.getOffHandStack().getItem() instanceof BlockItem;
            int var3 = InventoryUtils.method922(var0 -> mc.player.getInventory().getStack(var0).getItem() instanceof BlockItem);
            BlockPos var4 = this.method7027();
            if ((var2 || var3 != -1) && !var4.equals(BlockPos.ORIGIN)) {
               Vec3d var5 = var4.toCenterPos().add(0.0, 0.0, 0.0);
               Direction var6 = Direction.getFacing(var5.x - mc.player.getX(), var5.y - mc.player.getY(), var5.z - mc.player.getZ());
               Rotation var7 = new Rotation(
                  mc.player.getYaw(), RotationUtils.method6913(var5.subtract(new Vec3d(var6.getVector()).multiply(0.5))).getFloatValue2()
               );
               rotationManager.method7848(
                  new RotationRequest(var7, () -> bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig(), var7), bcCameraTestnerv.getRotationConfig()),
                  1,
                  this,
                  2
               );
            }
         }
      }
   }

   @EventTarget
   public void method7025(Event2 var1) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (this.entry2.isSelected()) {
            if (mc.player.horizontalCollision
               && mc.player.getMainHandStack().isOf(Items.WATER_BUCKET)
               && this.method7033() != null
               && mc.crosshairTarget instanceof BlockHitResult) {
               ActionResult var9 = mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
               if (var9 instanceof Success) {
                  this.flag = true;
               }
            }
         } else if (this.entry.isSelected()) {
            boolean var2 = mc.player.getOffHandStack().getItem() instanceof BlockItem;
            int var3 = InventoryUtils.method922(var0 -> mc.player.getInventory().getStack(var0).getItem() instanceof BlockItem);
            BlockPos var4 = this.method7027();
            if ((var2 || var3 != -1) && !var4.equals(BlockPos.ORIGIN)) {
               ItemStack var5 = var2 ? mc.player.getOffHandStack() : mc.player.getInventory().getStack(var3);
               Hand var6 = var2 ? Hand.OFF_HAND : Hand.MAIN_HAND;
               if (this.method7026(var5) && mc.crosshairTarget instanceof BlockHitResult var7) {
                  int var10 = mc.player.inventory.getSelectedSlot();
                  if (!var2) {
                     mc.player.inventory.setSelectedSlot(var3);
                  }

                  UseItemUtils.method6514(var7, var6);
                  if (!var2) {
                     mc.player.inventory.setSelectedSlot(var10);
                  }
               }
            }
         }
      }
   }

   private boolean method7026(ItemStack var1) {
      BlockPos var2 = this.method7028();
      BlockItem var3 = (BlockItem)var1.getItem();
      VoxelShape var4 = var3.getBlock().getDefaultState().getCollisionShape(mc.world, var2);
      if (var4.isEmpty()) {
         return false;
      } else {
         Box var5 = var4.getBoundingBox().offset(var2);
         return !var5.intersects(mc.player.getBoundingBox()) && var5.intersects(PlayerSimulation.method6031(2).box);
      }
   }

   private BlockPos method7027() {
      BlockPos var1 = this.method7028();
      return mc.world.getBlockState(var1).isSolid()
         ? BlockPos.ORIGIN
         : Stream.of(var1.west(), var1.east(), var1.south(), var1.north())
            .filter(var0 -> mc.world.getBlockState(var0).isSolid())
            .findFirst()
            .orElse(BlockPos.ORIGIN);
   }

   private BlockPos method7028() {
      return BlockPos.ofFloored(PlayerSimulation.method6031(1).vec3d.add(0.0, -0.001, 0.0));
   }

   @EventTarget
   public void method7029(MoveInputEvent var1) {
      if (mc.player.horizontalCollision && mc.player.lastOnGround) {
         var1.method3041(true);
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (this.entry2.isSelected()) {
            this.method7030();
         } else {
            this.method7037();
            if (this.entry3.isSelected()) {
               this.method7035();
            }
         }
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.slot = null;
      if (this.entry2.isSelected()) {
         this.method7031();
      }
   }

   @Override
   public void onDisable() {
      this.method7037();
      this.method7032();
      this.longValue = 0L;
      super.onDisable();
   }

   private void method7030() {
      if (!mc.player.horizontalCollision) {
         this.method7037();
      } else if (this.method7033() == null) {
         this.method7037();
      } else {
         if (this.flag && !mc.player.isOnGround() && mc.player.isTouchingWater()) {
            Vec3d var1 = mc.player.getVelocity();
            if (var1.y < 0.3F) {
               mc.player.setVelocity(var1.x, 0.3F, var1.z);
            }
         }

         int var2;
         if ((var2 = this.method7036()) == -1) {
            this.method7037();
         } else {
            if (!mc.player.getMainHandStack().isOf(Items.WATER_BUCKET)) {
               if (this.intValue == -1) {
                  this.intValue = mc.player.getInventory().getSelectedSlot();
               }

               this.intValue2 = var2;
               mc.player.getInventory().setSelectedSlot(var2);
            }
         }
      }
   }

   private void method7031() {
      if (mc.player != null && mc.interactionManager != null) {
         Slot var1 = mc.player.playerScreenHandler.getSlot(44);
         if (!var1.getStack().isOf(Items.WATER_BUCKET)) {
            Slot var2 = InventoryUtils.method908(mc.player.playerScreenHandler, Items.WATER_BUCKET);
            if (var2 != null && var2.id != 44) {
               KeyBindUtils.method2627(SpiderModule.class, () -> {
                  if (this.isEnabled() && this.entry2.isSelected() && mc.player != null && mc.interactionManager != null) {
                     Slot var1x = mc.player.playerScreenHandler.getSlot(44);
                     if (!var1x.getStack().isOf(Items.WATER_BUCKET)) {
                        Slot var2x = InventoryUtils.method908(mc.player.playerScreenHandler, Items.WATER_BUCKET);
                        if (var2x != null && var2x.id != 44) {
                           this.slot = var2x;
                           InventoryUtils.method934(var2x, 44, false, false);
                        }
                     }
                  }
               });
            }
         }
      }
   }

   private void method7032() {
      if (this.slot != null) {
         Slot var1 = this.slot;
         this.slot = null;
         KeyBindUtils.method2627(SpiderModule.class, () -> {
            if (mc.player != null && mc.interactionManager != null) {
               Slot var1x = mc.player.playerScreenHandler.getSlot(44);
               InventoryUtils.method934(var1x, var1.id, false, false);
            }
         });
      }
   }

   private BlockPos method7033() {
      BlockPos var1 = BlockPos.ofFloored(mc.player.getEntityPos());

      for (int var2 = 1; var2 >= -1; var2--) {
         BlockPos var3 = var1.up(var2);
         BlockPos var4 = null;
         double var5 = Double.MAX_VALUE;

         for (Direction var10 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
            BlockPos var11 = var3.offset(var10);
            VoxelShape var12 = mc.world.getBlockState(var11).getCollisionShape(mc.world, var11);
            double var13;
            if (!var12.isEmpty() && (var13 = var11.toCenterPos().squaredDistanceTo(mc.player.getEntityPos())) < var5) {
               var5 = var13;
               var4 = var11;
            }
         }

         if (var4 != null) {
            return var4;
         }
      }

      return null;
   }

   private Vec3d method7034(BlockPos var1) {
      Vec3d var2 = var1.toCenterPos();
      double var3 = mc.player.getX() - var2.x;
      double var5 = mc.player.getZ() - var2.z;
      double var7 = Math.max(var1.getX() + 0.1, Math.min(var1.getX() + 0.9, mc.player.getX()));
      double var9 = Math.max(var1.getZ() + 0.1, Math.min(var1.getZ() + 0.9, mc.player.getZ()));
      if (var1.getY() < Math.floor(mc.player.getY())) {
         return new Vec3d(var7, var1.getY() + 1.001, var9);
      } else {
         if (Math.abs(var3) > Math.abs(var5)) {
            var7 = var2.x + Math.copySign(0.501, var3);
         } else {
            var9 = var2.z + Math.copySign(0.501, var5);
         }

         return new Vec3d(var7, var1.getY() + 0.75, var9);
      }
   }

   private void method7035() {
      if (mc.player.horizontalCollision) {
         if (mc.player.isOnGround()) {
            mc.player.jump();
         }

         long var1;
         if ((var1 = System.currentTimeMillis()) - this.longValue >= 150L && mc.player.getOffHandStack().isOf(Items.PLAYER_HEAD)) {
            float var3 = mc.player.getYaw();
            float var4 = mc.player.getPitch();
            Vec3d var5 = mc.player.getEyePos();
            Vec3d var6 = var5.add(this.method7038(var4, var3).multiply(4.0));
            BlockHitResult var7 = mc.world.raycast(new RaycastContext(var5, var6, ShapeType.OUTLINE, FluidHandling.NONE, mc.player));
            if (var7.getType() == Type.BLOCK && mc.world.getBlockState(var7.getBlockPos()).isSolidBlock(mc.world, var7.getBlockPos())) {
               mc.player.networkHandler.sendPacket(new LookAndOnGround(var3, var4, mc.player.isOnGround(), mc.player.horizontalCollision));
               mc.player.swingHand(Hand.OFF_HAND);
               mc.interactionManager.interactBlock(mc.player, Hand.OFF_HAND, var7);
               mc.player.fallDistance = 0.0;
               this.longValue = var1;
            }
         }
      }
   }

   private int method7036() {
      for (int var1 = 0; var1 < 9; var1++) {
         if (mc.player.getInventory().getStack(var1).isOf(Items.WATER_BUCKET)) {
            return var1;
         }
      }

      return -1;
   }

   private void method7037() {
      if (mc.player != null && this.intValue != -1 && (this.intValue2 == -1 || mc.player.getInventory().getSelectedSlot() == this.intValue2)) {
         mc.player.getInventory().setSelectedSlot(this.intValue);
      }

      this.intValue = -1;
      this.intValue2 = -1;
      this.flag = false;
   }

   private Vec3d method7038(float var1, float var2) {
      float var3 = (float)Math.toRadians(var1);
      float var4;
      float var5 = (float)Math.cos(var4 = (float)Math.toRadians(-var2));
      float var6 = (float)Math.sin(var4);
      float var7 = (float)Math.cos(var3);
      float var8 = (float)Math.sin(var3);
      return new Vec3d(var6 * var7, -var8, var5 * var7);
   }

   @Generated
   public void method7039(long var1) {
      this.longValue = var1;
   }

   @Generated
   public void method7040(int var1) {
      this.intValue = var1;
   }

   @Generated
   public void method7041(int var1) {
      this.intValue2 = var1;
   }

   @Generated
   public void method7042(boolean var1) {
      this.flag = var1;
   }

   @Generated
   public void setSlot(Slot var1) {
      this.slot = var1;
   }

   @Generated
   public ModeSetting getMode2() {
      return this.mode;
   }

   @Generated
   public ModeSetting.Entry getEntry10() {
      return this.entry;
   }

   @Generated
   public ModeSetting.Entry getEntry22() {
      return this.entry2;
   }

   @Generated
   public ModeSetting.Entry getEntry32() {
      return this.entry3;
   }

   @Generated
   public long getLongValue6() {
      return this.longValue;
   }

   @Generated
   public int getIntValue19() {
      return this.intValue;
   }

   @Generated
   public int getIntValue24() {
      return this.intValue2;
   }

   @Generated
   public boolean isFlag11() {
      return this.flag;
   }

   @Generated
   public Slot getSlot2() {
      return this.slot;
   }
}

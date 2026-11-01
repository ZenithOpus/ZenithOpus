package org.zenith.client.modules.impl.player;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.zenith.ChatLogger;
import org.zenith.MinecraftAccess;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;

@ModuleInfo(
   name = "ClanUpgrade",
   category = ModuleCategory.PLAYER,
   description = "module.clanupgrade.description"
)
public final class ClanUpgradeModule extends Module implements MinecraftAccess {
   public static final ClanUpgradeModule INSTANCE = new ClanUpgradeModule();
   private static final float CONST_floatValue = 90.0F;
   private static final float CONST_floatValue2 = 0.0F;
   private float floatValue = 0.0F;
   private float floatValue2 = 0.0F;
   private boolean flag = false;
   private boolean flag2 = false;

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         ItemStack var2 = mc.player.getStackInHand(Hand.MAIN_HAND);
         if (var2.getItem() != Items.REDSTONE) {
            if (!this.flag2) {
               this.flag2 = true;
               ChatLogger.method2022("Возьмите редстоун в руку!");
            }
         } else {
            this.flag2 = false;
            BlockPos var3 = mc.player.getBlockPos();
            BlockState var4 = mc.world.getBlockState(var3);
            if (!this.flag) {
               this.floatValue = mc.player.getPitch();
               this.floatValue2 = mc.player.getYaw();
               this.flag = true;
            }

            mc.player.setPitch(90.0F);
            mc.player.setYaw(0.0F);
            if (var4.isAir()) {
               this.method3947(var3);
            } else if (var4.getBlock() == Blocks.REDSTONE_WIRE) {
               this.method3948(var3);
            }
         }
      }
   }

   private void method3947(BlockPos var1) {
      BlockPos var2 = var1.down();
      BlockState var3 = mc.world.getBlockState(var2);
      if (var3.isSolid()) {
         Vec3d var4 = new Vec3d(var2.getX() + 0.5, var2.getY() + 1.0, var2.getZ() + 0.5);
         BlockHitResult var5 = new BlockHitResult(var4, Direction.UP, var2, false);
         mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, var5);
         mc.player.swingHand(Hand.MAIN_HAND);
      }
   }

   private void method3948(BlockPos var1) {
      mc.interactionManager.attackBlock(var1, Direction.UP);
      mc.player.swingHand(Hand.MAIN_HAND);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.flag && mc.player != null) {
         mc.player.setPitch(this.floatValue);
         mc.player.setYaw(this.floatValue2);
         this.flag = false;
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.flag = false;
      this.flag2 = false;
      if (mc.player != null) {
         this.floatValue = mc.player.getPitch();
         this.floatValue2 = mc.player.getYaw();
      }
   }
}

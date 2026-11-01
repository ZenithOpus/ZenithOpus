package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.PlayerMoveEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.network.InventoryUtils;

@ModuleInfo(
   name = "SlimeFlight",
   category = ModuleCategory.MOVEMENT,
   description = "module.slimeflight.description"
)
public final class SlimeFlightModule extends Module {
   public static final SlimeFlightModule INSTANCE = new SlimeFlightModule();
   public int cooldown;
   public boolean flag = false;

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (this.flag) {
         mc.player.setPitch(60.0F);
         this.flag = false;
      }
   }

   private Block method4512(BlockPos var1) {
      return mc.world.getBlockState(var1).getBlock();
   }

   @EventTarget
   public void method4513(PlayerMoveEvent var1) {
      BlockPos var2 = new BlockPos(mc.player.getBlockX() + 1, mc.player.getBlockY(), mc.player.getBlockZ());
      BlockPos var3 = new BlockPos(mc.player.getBlockX(), mc.player.getBlockY(), mc.player.getBlockZ() + 1);
      BlockPos var4 = new BlockPos(mc.player.getBlockX() - 1, mc.player.getBlockY(), mc.player.getBlockZ());
      BlockPos var5 = new BlockPos(mc.player.getBlockX(), mc.player.getBlockY(), mc.player.getBlockZ() - 1);
      int var6 = InventoryUtils.method922(var0 -> mc.player.getInventory().getStack(var0).getItem() == Items.SLIME_BLOCK);
      HitResult var7 = mc.crosshairTarget;
      if (mc.player.horizontalCollision) {
         if (this.method4512(var2) == Blocks.SLIME_BLOCK
            || this.method4512(var3) == Blocks.SLIME_BLOCK
            || this.method4512(var4) == Blocks.SLIME_BLOCK
            || this.method4512(var5) == Blocks.SLIME_BLOCK) {
            if (!(mc.player.getVelocity().y <= -1.0)) {
               if (var7 instanceof BlockHitResult var8) {
                  Direction var9 = var8.getSide();
                  if (this.method4512(var8.getBlockPos()) == Blocks.AIR) {
                     return;
                  }

                  BlockHitResult var10 = new BlockHitResult(var8.getPos(), var9, var8.getBlockPos(), true);
                  if (var6 != -1) {
                     mc.player.getInventory().setSelectedSlot(var6);
                  }

                  this.flag = true;
                  if (this.cooldown >= 1) {
                     mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, var10);
                     mc.player.setVelocity(mc.player.getVelocity().x, 0.62, mc.player.getVelocity().z);
                     mc.player.swingHand(Hand.MAIN_HAND);
                     this.cooldown = 0;
                  } else {
                     this.cooldown++;
                  }
               }
            }
         }
      }
   }
}

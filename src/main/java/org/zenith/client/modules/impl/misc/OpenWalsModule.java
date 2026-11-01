package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.CrosshairTargetEvent;
import org.zenith.utility.game.TargetUtils;

@ModuleInfo(
   name = "OpenWals",
   category = ModuleCategory.MISC,
   description = "module.openwals.description"
)
public final class OpenWalsModule extends Module {
   public static final OpenWalsModule INSTANCE = new OpenWalsModule();

   private OpenWalsModule() {
   }

   @EventTarget
   public void method950(CrosshairTargetEvent var1) {
      if (mc.player != null && mc.world != null) {
         Rotation var2 = Zenith.getInstance().getRotationManager().method7844();
         BlockHitResult var3 = TargetUtils.method3216(mc.player.getCameraPosVec(1.0F), var2, mc.player.getBlockInteractionRange(), this::method951);
         if (var3 != null && var3.getType() == Type.BLOCK) {
            mc.crosshairTarget = var3;
         }
      }
   }

   private boolean method951(BlockHitResult var1) {
      if (var1 != null && var1.getType() == Type.BLOCK) {
         BlockState var2 = mc.world.getBlockState(var1.getBlockPos());
         if (var2.getBlock() instanceof EnderChestBlock) {
            return true;
         } else {
            BlockEntity var3 = mc.world.getBlockEntity(var1.getBlockPos());
            return var3 instanceof Inventory && var2.createScreenHandlerFactory(mc.world, var1.getBlockPos()) != null;
         }
      } else {
         return false;
      }
   }
}

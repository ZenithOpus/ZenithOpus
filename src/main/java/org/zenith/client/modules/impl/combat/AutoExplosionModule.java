package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.Blocks;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.BlockPlacedEvent;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.client.events.Event5;
import org.zenith.client.events.Event9;
import org.zenith.utility.game.AttackUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.KeyBindUtils;

@ModuleInfo(
   name = "AutoExplosion",
   description = "module.autoexplosion.description",
   category = ModuleCategory.COMBAT
)
public final class AutoExplosionModule extends Module {
   public static final AutoExplosionModule INSTANCE = new AutoExplosionModule();
   private int ticks = 0;
   private BlockPos blockPos = null;
   private BlockPos blockPos2 = null;
   public int intValue = 0;
   private Slot slot = null;

   @EventTarget
   public void method4898(BlockPlacedEvent var1) {
      if (var1.getBlockState().getBlock() == Blocks.OBSIDIAN) {
         this.ticks = 5;
         Slot var2 = InventoryUtils.method906(Items.END_CRYSTAL);
         if (var2 == null) {
            return;
         }

         this.blockPos2 = var1.getBlockPos();
      }
   }

   @EventTarget
   public void method4899(Event5 var1) {
      if (this.blockPos2 != null) {
         Rotation var2 = rotationManager.method7844()
            .method3141(
               Zenith.getInstance().getRotationManager().method7844().method3131(RotationUtils.method6913(this.blockPos2.toCenterPos().add(0.0, 0.5, 0.0)))
            );
         rotationManager.method7848(new RotationRequest(var2, () -> var2, bcCameraTestnerv.getRotationConfig()), 4, this, 1);
      }

      if (this.blockPos != null) {
         if (this.intValue > 0) {
            Rotation var3 = rotationManager.method7844()
               .method3141(
                  Zenith.getInstance().getRotationManager().method7844().method3131(RotationUtils.method6913(this.blockPos.toCenterPos().add(0.0, 1.2F, 0.0)))
               );
            rotationManager.method7848(new RotationRequest(var3, () -> var3, bcCameraTestnerv.getRotationConfig()), 4, this, 1);
            this.intValue--;
         }
      }
   }

   @EventTarget
   public void method4900(EntityAddEvent var1) {
      if (var1.getEntity() instanceof EndCrystalEntity var2 && var2.getBlockPos().equals(this.blockPos)) {
         this.intValue = 4;
      }
   }

   @EventTarget
   public void method4901(Event9 var1) {
      if (!var1.isCancelled()
         && this.blockPos != null
         && this.intValue > 0
         && mc.crosshairTarget instanceof EntityHitResult var2
         && var2.getEntity() instanceof EndCrystalEntity) {
         AttackUtils.method816(var2.getEntity());
         this.blockPos = null;
         var1.setCancelled(true);
      }

      if (this.ticks > 0) {
         this.ticks--;
      }

      if (this.ticks <= 0) {
         this.blockPos2 = null;
         if (this.slot != null && KeyBindUtils.method2630(AutoExplosionModule.class)) {
            Slot var6 = this.slot;
            KeyBindUtils.method2627(AutoExplosionModule.class, () -> InventoryUtils.method900(var6, Hand.MAIN_HAND, true));
            this.slot = null;
         }
      } else if (this.blockPos2 != null) {
         Slot var5 = InventoryUtils.method906(Items.END_CRYSTAL);
         if (var5 != null) {
            if (!var1.isCancelled()
               && mc.crosshairTarget instanceof BlockHitResult var7
               && mc.world.getBlockState(var7.getBlockPos()).getBlock() == Blocks.OBSIDIAN) {
               if (mc.player.getMainHandStack().getItem() == Items.END_CRYSTAL) {
                  UseItemUtils.method6514(var7, Hand.MAIN_HAND);
                  var1.setCancelled(true);
                  this.blockPos = var7.getBlockPos().up();
                  this.blockPos2 = null;
                  this.ticks = -4;
               } else if (KeyBindUtils.method2629(AutoExplosionModule.class)) {
                  KeyBindUtils.method2627(AutoExplosionModule.class, () -> InventoryUtils.method900(var5, Hand.MAIN_HAND, true));
                  if (this.slot == null) {
                     this.slot = var5;
                  }
               }
            }
         }
      }
   }
}

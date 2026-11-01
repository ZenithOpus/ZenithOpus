package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.Event5;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "ElytraFly",
   category = ModuleCategory.MOVEMENT,
   description = "module.elytrafly.description"
)
public final class ElytraFlyModule extends Module {
   public static final ElytraFlyModule INSTANCE = new ElytraFlyModule();
   public boolean flag = false;

   private ElytraFlyModule() {
   }

   @Override
   public void onDisable() {
      this.flag = false;
      super.onDisable();
   }

   @EventTarget
   public void method2257(MoveInputEvent var1) {
      if (mc.player != null) {
         ItemStack var2 = mc.player.getEquippedStack(EquipmentSlot.CHEST);
         if (var2.isOf(Items.ELYTRA)) {
            if (mc.player.isOnGround()) {
               var1.method3041(true);
               this.flag = true;
            } else if (!mc.player.isGliding()) {
               if (!this.flag) {
                  UseItemUtils.method6503();
               } else {
                  this.flag = false;
               }
            } else {
               this.flag = false;
            }
         }
      }
   }

   @EventTarget
   public void method2258(Event5 var1) {
      zenith.getRotationManager()
         .method7849(
            new RotationRequest(new Rotation(mc.player.getYaw(), 0.0F), () -> new Rotation(mc.player.getYaw(), 0.0F), bcCameraTestnerv.getRotationConfig()),
            2,
            this
         );
   }

   @EventTarget
   public void method2259(UpdateEvent var1) {
      if (mc.player != null && mc.world != null) {
         ItemStack var2 = mc.player.getEquippedStack(EquipmentSlot.CHEST);
         if (var2.isOf(Items.ELYTRA)) {
            if (mc.player.isGliding()) {
               mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y + 0.0305, mc.player.getVelocity().z);
            }
         }
      }
   }
}

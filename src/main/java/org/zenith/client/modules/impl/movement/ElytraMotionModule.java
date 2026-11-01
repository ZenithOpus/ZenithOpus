package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import lombok.Generated;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.zenith.KeyEvent;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.PlayerMoveEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "ElytraMotion",
   description = "module.elytramotion.description",
   category = ModuleCategory.MOVEMENT
)
public final class ElytraMotionModule extends Module {
   public static final ElytraMotionModule INSTANCE = new ElytraMotionModule();
   private final ModeSetting mode = new ModeSetting(
      "module.elytraMotion.mode", "module.elytraMotion.mode.desc", "module.elytraMotion.mode.distance", "module.elytraMotion.mode.smart"
   );
   private final SliderSetting distance = new SliderSetting(
      "module.elytraMotion.distance", 0.1F, 0.1F, 4.0F, 1.0F, "module.elytraMotion.distance.desc", "b", () -> this.mode.isSelected(0), null
   );
   private boolean flag = false;
   private final StopWatch stopWatch = new StopWatch();

   private ElytraMotionModule() {
   }

   @Override
   public void onEnable() {
      this.flag = false;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.flag = false;
      super.onDisable();
   }

   @EventTarget
   public void method6853(KeyEvent var1) {
   }

   @EventTarget
   public void method6854(UpdateEvent var1) {
      if (!mc.player.isGliding()) {
         this.flag = false;
      } else {
         if (this.method6855()) {
            if (UseItemUtils.method6517() != null) {
               this.flag = true;
            } else if (this.stopWatch.method7546(300L) && !this.flag) {
               InventoryUtils.method929(Items.FIREWORK_ROCKET, Hand.OFF_HAND);
               this.stopWatch.reset();
            }
         } else {
            this.flag = false;
         }
      }
   }

   public boolean method6855() {
      Box var1 = ElytraTargetModule.INSTANCE.getBox();
      if (var1 == null) {
         return false;
      } else {
         return this.mode.isSelected(0)
            ? mc.player.squaredDistanceTo(var1.getCenter()) <= this.distance.getValue3() * this.distance.getValue3()
            : mc.player.getBoundingBox().intersects(ElytraTargetModule.INSTANCE.getBox());
      }
   }

   @EventTarget
   public void method6856(PlayerMoveEvent var1) {
      if (this.flag) {
         var1.setVec3d(Vec3d.ZERO);
      }
   }

   @EventTarget
   public void method6857(PacketEvent var1) {
   }

   @Generated
   public boolean isFlag10() {
      return this.flag;
   }
}

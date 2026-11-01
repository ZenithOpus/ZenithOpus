package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Vec3d;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.Module;
import org.zenith.client.events.BlockCollisionEvent;
import org.zenith.client.settings.SliderSetting;

@ModuleInfo(
   name = "NoSweetSlow",
   category = ModuleCategory.MOVEMENT,
   description = "module.nosweetslow.description"
)
public final class NoSweetSlowModule extends Module {
   public static final NoSweetSlowModule INSTANCE = new NoSweetSlowModule();
   private final SliderSetting speed = new SliderSetting("module.noSweetSlow.speed", 1.5F, 0.8F, 1.8F, 0.05F, "module.noSweetSlow.speed.desc", "x");

   private NoSweetSlowModule() {
   }

   @EventTarget
   public void method106(BlockCollisionEvent var1) {
      if (var1.getBlock() == Blocks.SWEET_BERRY_BUSH) {
         var1.setCancelled(true);
         mc.player.slowMovement(mc.world.getBlockState(var1.getBlockPos()), new Vec3d(this.speed.getValue3(), 0.75, this.speed.getValue3()));
      }
   }
}

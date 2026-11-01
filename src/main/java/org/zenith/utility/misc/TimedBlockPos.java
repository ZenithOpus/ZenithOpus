package org.zenith.utility.misc;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import lombok.Generated;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.zenith.Zenith;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.utility.render.GlowUtil;

public class TimedBlockPos {
   private BlockPos blockPos;
   private BlockPos blockPos2;
   private final org.zenith.StopWatch stopWatch = new org.zenith.StopWatch();

   public TimedBlockPos() {
      EventManager.register(this);
   }

   public void method474(BlockPos var1) {
      this.blockPos = var1;
      this.stopWatch.reset();
   }

   public void method475(BlockPos var1) {
      this.blockPos2 = var1;
      this.stopWatch.reset();
   }

   @EventTarget
   public void method476(WorldRenderEvent var1) {
      if (this.blockPos != null && this.blockPos2 != null && !this.stopWatch.method7546(5000L)) {
         GlowUtil.method2230(
            new Box(new Vec3d(this.blockPos), new Vec3d(this.blockPos2)).expand(1.5, 0.0, 1.5),
            Zenith.getInstance().getStyleManager().getClientColor(90).method7287(),
            1.0F
         );
      } else {
         if (this.blockPos != null) {
            GlowUtil.method2230(new Box(this.blockPos), Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 1.0F);
         }

         if (this.blockPos2 != null) {
            GlowUtil.method2230(new Box(this.blockPos2), Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 1.0F);
         }
      }
   }

   public void clear() {
      this.blockPos = null;
      this.blockPos2 = null;
   }

   @Generated
   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   @Generated
   public BlockPos getBlockPos2() {
      return this.blockPos2;
   }

   @Generated
   public org.zenith.StopWatch getStopWatch() {
      return this.stopWatch;
   }
}

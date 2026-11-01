package org.zenith.client.events;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.zenith.CancellableEvent;

public class BlockBreakingEvent extends CancellableEvent {
   private final BlockPos blockPos;
   private final Direction direction;

   public BlockBreakingEvent(BlockPos var1, Direction var2) {
      this.blockPos = var1;
      this.direction = var2;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   public Direction getDirection() {
      return this.direction;
   }
}

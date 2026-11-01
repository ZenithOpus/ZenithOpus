package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import org.zenith.CancellableEvent;

public class Event8 extends CancellableEvent {
   private final Block block;
   private final BlockPos blockPos;

   @Generated
   public Event8(Block var1, BlockPos var2) {
      this.block = var1;
      this.blockPos = var2;
   }

   @Generated
   public Block getBlock() {
      return this.block;
   }

   @Generated
   public BlockPos getBlockPos2() {
      return this.blockPos;
   }
}

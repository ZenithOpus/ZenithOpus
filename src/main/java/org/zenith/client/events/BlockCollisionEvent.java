package org.zenith.client.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import lombok.Generated;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class BlockCollisionEvent extends EventCancellable {
   private final Block block;
   private final BlockPos blockPos;

   @Generated
   public Block getBlock() {
      return this.block;
   }

   @Generated
   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   @Generated
   public BlockCollisionEvent(Block var1, BlockPos var2) {
      this.block = var1;
      this.blockPos = var2;
   }
}

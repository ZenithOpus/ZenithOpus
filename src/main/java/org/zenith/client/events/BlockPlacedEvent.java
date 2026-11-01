package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class BlockPlacedEvent implements Event {
   private final BlockPos blockPos;
   private final BlockState blockState;

   @Generated
   public BlockPlacedEvent(BlockPos var1, BlockState var2) {
      this.blockPos = var1;
      this.blockState = var2;
   }

   @Generated
   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   @Generated
   public BlockState getBlockState() {
      return this.blockState;
   }
}

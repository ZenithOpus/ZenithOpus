package org.zenith.base.bot.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class BotPendingUpdateManager implements AutoCloseable {
   private final Long2ObjectOpenHashMap<BotPendingUpdateManager.PendingUpdate> blockPosToPendingUpdate = new Long2ObjectOpenHashMap();
   private int sequence;
   private boolean pendingSequence;

   public void addPendingUpdate(BlockPos var1, BlockState var2, PlayerEntity var3) {
      this.blockPosToPendingUpdate
         .compute(
            var1.asLong(),
            (var3x, var4) -> var4 != null
               ? var4.withSequence(this.sequence)
               : new BotPendingUpdateManager.PendingUpdate(this.sequence, var2, var3.getEntityPos())
         );
   }

   public boolean hasPendingUpdate(BlockPos var1, BlockState var2) {
      BotPendingUpdateManager.PendingUpdate var3 = (BotPendingUpdateManager.PendingUpdate)this.blockPosToPendingUpdate.get(var1.asLong());
      if (var3 == null) {
         return false;
      } else {
         var3.setBlockState(var2);
         return true;
      }
   }

   public void processPendingUpdates(int var1, BotWorld var2) {
      ObjectIterator var3 = this.blockPosToPendingUpdate.long2ObjectEntrySet().iterator();

      while (var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         BotPendingUpdateManager.PendingUpdate var5 = (BotPendingUpdateManager.PendingUpdate)var4.getValue();
         if (var5.sequence <= var1) {
            BlockPos var6 = BlockPos.fromLong(var4.getLongKey());
            var3.remove();
            var2.processPendingUpdate(var6, var5.blockState, var5.playerPos);
         }
      }
   }

   public BotPendingUpdateManager incrementSequence() {
      this.sequence++;
      this.pendingSequence = true;
      return this;
   }

   @Override
   public void close() {
      this.pendingSequence = false;
   }

   public int getSequence() {
      return this.sequence;
   }

   public boolean hasPendingSequence() {
      return this.pendingSequence;
   }

   private static final class PendingUpdate {
      public final Vec3d playerPos;
      public int sequence;
      public BlockState blockState;

      public PendingUpdate(int var1, BlockState var2, Vec3d var3) {
         this.sequence = var1;
         this.blockState = var2;
         this.playerPos = var3;
      }

      public BotPendingUpdateManager.PendingUpdate withSequence(int var1) {
         this.sequence = var1;
         return this;
      }

      public void setBlockState(BlockState var1) {
         this.blockState = var1;
      }
   }
}

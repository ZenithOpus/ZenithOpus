package org.zenith.base.bot.world;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData.BlockEntityVisitor;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class BotChunkManager extends ChunkManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final WorldChunk emptyChunk;
   private final LightingProvider lightingProvider;
   private volatile BotChunkManager.BotChunkMap chunks;
   private final BotWorld world;

   public BotChunkManager(BotWorld var1, int var2) {
      this.world = var1;
      this.emptyChunk = new EmptyChunk(var1, new ChunkPos(0, 0), var1.getRegistryManager().getOrThrow(RegistryKeys.BIOME).getOrThrow(BiomeKeys.PLAINS));
      this.lightingProvider = new LightingProvider(this, true, var1.getDimension().hasSkyLight());
      this.chunks = new BotChunkManager.BotChunkMap(getChunkMapRadius(var2));
   }

   @Override
   public LightingProvider getLightingProvider() {
      return this.lightingProvider;
   }

   private static boolean positionEquals(@Nullable WorldChunk var0, int var1, int var2) {
      if (var0 == null) {
         return false;
      } else {
         ChunkPos var3 = var0.getPos();
         return var3.x == var1 && var3.z == var2;
      }
   }

   public void unload(ChunkPos var1) {
      if (this.chunks.isInRadius(var1.x, var1.z)) {
         int var2 = this.chunks.getIndex(var1.x, var1.z);
         WorldChunk var3 = this.chunks.getChunk(var2);
         if (positionEquals(var3, var1.x, var1.z)) {
            this.chunks.unloadChunk(var2, var3);

            for (BotWorld.RenderListener var7 : this.world.getRenderListeners()) {
               var7.onChunkChanged(var1.x, var1.z);
            }
         }
      }
   }

   @Nullable
   public WorldChunk getChunk(int var1, int var2, ChunkStatus var3, boolean var4) {
      if (this.chunks.isInRadius(var1, var2)) {
         WorldChunk var5 = this.chunks.getChunk(this.chunks.getIndex(var1, var2));
         if (positionEquals(var5, var1, var2)) {
            return var5;
         }
      }

      return var4 ? this.emptyChunk : null;
   }

   @Override
   public BlockView getWorld() {
      return this.world;
   }

   public void onChunkBiomeData(int var1, int var2, PacketByteBuf var3) {
      if (!this.chunks.isInRadius(var1, var2)) {
         LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", var1, var2);
      } else {
         int var4 = this.chunks.getIndex(var1, var2);
         WorldChunk var5 = this.chunks.getChunk(var4);
         if (!positionEquals(var5, var1, var2)) {
            LOGGER.warn("Ignoring chunk since it's not present: {}, {}", var1, var2);
         } else {
            var5.loadBiomeFromPacket(var3);
         }
      }
   }

   @Nullable
   public WorldChunk loadChunkFromPacket(int var1, int var2, PacketByteBuf var3, Map<Type, long[]> var4, Consumer<BlockEntityVisitor> var5) {
      if (!this.chunks.isInRadius(var1, var2)) {
         LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", var1, var2);
         return null;
      } else {
         int var6 = this.chunks.getIndex(var1, var2);
         WorldChunk var7 = this.chunks.getChunk(var6);
         ChunkPos var8 = new ChunkPos(var1, var2);
         if (!positionEquals(var7, var1, var2)) {
            var7 = new WorldChunk(this.world, var8);
            var7.loadFromPacket(var3, var4, var5);
            this.chunks.set(var6, var7);
         } else {
            var7.loadFromPacket(var3, var4, var5);
            this.chunks.refreshSections(var7);
         }

         this.world.resetChunkColor(var8);
         return var7;
      }
   }

   @Override
   public void tick(BooleanSupplier shouldKeepTicking, boolean tickChunks) {
   }

   public void setChunkMapCenter(int var1, int var2) {
      this.chunks.centerChunkX = var1;
      this.chunks.centerChunkZ = var2;
   }

   public void updateLoadDistance(int var1) {
      int var2 = this.chunks.radius;
      int var3 = getChunkMapRadius(var1);
      if (var2 != var3) {
         BotChunkManager.BotChunkMap var4 = new BotChunkManager.BotChunkMap(var3);
         var4.centerChunkX = this.chunks.centerChunkX;
         var4.centerChunkZ = this.chunks.centerChunkZ;

         for (int var5 = 0; var5 < this.chunks.chunks.length(); var5++) {
            WorldChunk var6 = this.chunks.chunks.get(var5);
            if (var6 != null) {
               ChunkPos var7 = var6.getPos();
               if (var4.isInRadius(var7.x, var7.z)) {
                  var4.set(var4.getIndex(var7.x, var7.z), var6);
               }
            }
         }

         this.chunks = var4;
      }
   }

   private static int getChunkMapRadius(int var0) {
      return Math.max(2, var0) + 3;
   }

   @Override
   public String getDebugString() {
      return this.chunks.chunks.length() + ", " + this.getLoadedChunkCount();
   }

   @Override
   public int getLoadedChunkCount() {
      return this.chunks.loadedChunkCount;
   }

   @Override
   public void onLightUpdate(LightType type, ChunkSectionPos pos) {
   }

   public LongOpenHashSet getActiveSections() {
      return this.chunks.activeSections;
   }

   @Override
   public void onSectionStatusChanged(int x, int sectionY, int z, boolean previouslyEmpty) {
      this.chunks.onSectionStatusChanged(x, sectionY, z, previouslyEmpty);
   }

   private final class BotChunkMap {
      public final AtomicReferenceArray<WorldChunk> chunks;
      public final LongOpenHashSet activeSections = new LongOpenHashSet();
      public final int radius;
      private final int diameter;
      public volatile int centerChunkX;
      public volatile int centerChunkZ;
      public int loadedChunkCount;

      public BotChunkMap(int var2) {
         this.radius = var2;
         this.diameter = var2 * 2 + 1;
         this.chunks = new AtomicReferenceArray<>(this.diameter * this.diameter);
      }

      public int getIndex(int var1, int var2) {
         return Math.floorMod(var2, this.diameter) * this.diameter + Math.floorMod(var1, this.diameter);
      }

      public void set(int var1, @Nullable WorldChunk var2) {
         WorldChunk var3 = this.chunks.getAndSet(var1, var2);
         if (var3 != null) {
            this.loadedChunkCount--;
            this.unloadChunkSections(var3);
            BotChunkManager.this.world.unloadBlockEntities(var3);
         }

         if (var2 != null) {
            this.loadedChunkCount++;
            this.loadChunkSections(var2);
         }
      }

      public void unloadChunk(int var1, WorldChunk var2) {
         if (this.chunks.compareAndSet(var1, var2, null)) {
            this.loadedChunkCount--;
            this.unloadChunkSections(var2);
         }

         BotChunkManager.this.world.unloadBlockEntities(var2);
      }

      public void onSectionStatusChanged(int var1, int var2, int var3, boolean var4) {
         if (this.isInRadius(var1, var3)) {
            long var5 = ChunkSectionPos.asLong(var1, var2, var3);
            if (var4) {
               this.activeSections.add(var5);
            } else if (this.activeSections.remove(var5)) {
               BotChunkManager.this.world.onChunkUnload(var5);
            }
         }
      }

      private void unloadChunkSections(WorldChunk var1) {
         ChunkSection[] var2 = var1.getSectionArray();

         for (int var3 = 0; var3 < var2.length; var3++) {
            ChunkPos var4 = var1.getPos();
            this.activeSections.remove(ChunkSectionPos.asLong(var4.x, var1.sectionIndexToCoord(var3), var4.z));
         }
      }

      private void loadChunkSections(WorldChunk var1) {
         ChunkSection[] var2 = var1.getSectionArray();

         for (int var3 = 0; var3 < var2.length; var3++) {
            if (var2[var3].isEmpty()) {
               ChunkPos var4 = var1.getPos();
               this.activeSections.add(ChunkSectionPos.asLong(var4.x, var1.sectionIndexToCoord(var3), var4.z));
            }
         }
      }

      public void refreshSections(WorldChunk var1) {
         ChunkPos var2 = var1.getPos();
         ChunkSection[] var3 = var1.getSectionArray();

         for (int var4 = 0; var4 < var3.length; var4++) {
            long var5 = ChunkSectionPos.asLong(var2.x, var1.sectionIndexToCoord(var4), var2.z);
            if (var3[var4].isEmpty()) {
               this.activeSections.add(var5);
            } else if (this.activeSections.remove(var5)) {
               BotChunkManager.this.world.onChunkUnload(var5);
            }
         }
      }

      public boolean isInRadius(int var1, int var2) {
         return Math.abs(var1 - this.centerChunkX) <= this.radius && Math.abs(var2 - this.centerChunkZ) <= this.radius;
      }

      @Nullable
      public WorldChunk getChunk(int var1) {
         return this.chunks.get(var1);
      }
   }
}

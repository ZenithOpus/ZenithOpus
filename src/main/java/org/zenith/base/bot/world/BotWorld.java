package org.zenith.base.bot.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld.Properties;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.map.MapState;
import net.minecraft.network.packet.Packet;
import net.minecraft.particle.BlockParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.collection.WeightedPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EntityList;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;
import net.minecraft.world.WorldProperties.SpawnPoint;
import net.minecraft.world.attribute.WorldEnvironmentAttributeAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.entity.ClientEntityManager;
import net.minecraft.world.entity.EntityHandler;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.tick.EmptyTickSchedulers;
import net.minecraft.world.tick.QueryableTickScheduler;
import net.minecraft.world.tick.TickManager;
import org.jetbrains.annotations.Nullable;
import org.zenith.base.bot.net.BotPlayHandler;

public final class BotWorld extends World {
   public final EntityList entityList = new EntityList();
   private final ClientEntityManager<Entity> entityManager = new ClientEntityManager<>(Entity.class, new BotWorld.BotEntityHandler());
   private final BotPlayHandler networkHandler;
   private final Properties clientWorldProperties;
   private final TickManager tickManager;
   public final List<PlayerEntity> players = Lists.newArrayList();
   public final List<EnderDragonPart> enderDragonParts = Lists.newArrayList();
   private final Map<MapIdComponent, MapState> mapStates = new ConcurrentHashMap<>();
   private final BotChunkManager chunkManager;
   private final WorldEnvironmentAttributeAccess environmentAttributes = WorldEnvironmentAttributeAccess.builder().world(this).build();
   private final Deque<Runnable> chunkUpdaters = Queues.newArrayDeque();
   private int simulationDistance;
   private final BotPendingUpdateManager pendingUpdateManager = new BotPendingUpdateManager();
   private final int seaLevel;
   private boolean shouldTickTimeOfDay;
   @Nullable
   private BotPlayer botPlayer;
   private static final BotWorld.RenderListener[] NO_LISTENERS = new BotWorld.RenderListener[0];
   private volatile BotWorld.RenderListener[] renderListeners = NO_LISTENERS;
   private volatile int entitySetVersion;
   private final WorldBorder zenithWorldBorder = new WorldBorder();

   public BotWorld(
      BotPlayHandler var1, Properties var2, RegistryKey<World> var3, RegistryEntry<DimensionType> var4, int var5, int var6, boolean var7, long var8, int var10
   ) {
      super(var2, var3, var1.getRegistryManager(), var4, true, var7, var8, 1000000);
      this.networkHandler = var1;
      this.chunkManager = new BotChunkManager(this, var5);
      this.tickManager = new TickManager();
      this.clientWorldProperties = var2;
      this.seaLevel = var10;
      this.setSpawnPos(new BlockPos(8, 64, 8), 0.0F);
      this.simulationDistance = var6;
      this.calculateAmbientDarkness();
      this.initWeatherGradients();
   }

   public synchronized void addRenderListener(BotWorld.RenderListener var1) {
      BotWorld.RenderListener[] var2 = this.renderListeners;

      for (BotWorld.RenderListener var6 : var2) {
         if (var6 == var1) {
            return;
         }
      }

      BotWorld.RenderListener[] var7 = Arrays.copyOf(var2, var2.length + 1);
      var7[var2.length] = var1;
      this.renderListeners = var7;
   }

   public synchronized void removeRenderListener(BotWorld.RenderListener var1) {
      BotWorld.RenderListener[] var2 = this.renderListeners;

      for (int var3 = 0; var3 < var2.length; var3++) {
         if (var2[var3] == var1) {
            if (var2.length == 1) {
               this.renderListeners = NO_LISTENERS;
            } else {
               BotWorld.RenderListener[] var4 = new BotWorld.RenderListener[var2.length - 1];
               System.arraycopy(var2, 0, var4, 0, var3);
               System.arraycopy(var2, var3 + 1, var4, var3, var2.length - var3 - 1);
               this.renderListeners = var4;
            }

            return;
         }
      }
   }

   public BotWorld.RenderListener[] getRenderListeners() {
      return this.renderListeners;
   }

   public int getEntitySetVersion() {
      return this.entitySetVersion;
   }

   public void setBotPlayer(@Nullable BotPlayer var1) {
      this.botPlayer = var1;
   }

   @Nullable
   public BotPlayer getBotPlayer() {
      return this.botPlayer;
   }

   public void handlePlayerActionResponse(int var1) {
      this.pendingUpdateManager.processPendingUpdates(var1, this);
   }

   public void handleBlockUpdate(BlockPos var1, BlockState var2, int var3) {
      if (!this.pendingUpdateManager.hasPendingUpdate(var1, var2)) {
         super.setBlockState(var1, var2, var3, 512);
      }
   }

   public void processPendingUpdate(BlockPos var1, BlockState var2, Vec3d var3) {
      BlockState var4 = this.getBlockState(var1);
      if (var4 != var2) {
         this.setBlockState(var1, var2, 19);
         BotPlayer var5 = this.botPlayer;
         if (var5 != null && this == var5.getEntityWorld() && var5.collidesWithStateAtPos(var1, var2)) {
            var5.updatePosition(var3.x, var3.y, var3.z);
         }
      }
   }

   public BotPendingUpdateManager getPendingUpdateManager() {
      return this.pendingUpdateManager;
   }

   @Override
   public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
      if (this.pendingUpdateManager.hasPendingSequence() && this.botPlayer != null) {
         BlockState var5 = this.getBlockState(pos);
         boolean var6 = super.setBlockState(pos, state, flags, maxUpdateDepth);
         if (var6) {
            this.pendingUpdateManager.addPendingUpdate(pos, var5, this.botPlayer);
         }

         return var6;
      } else {
         return super.setBlockState(pos, state, flags, maxUpdateDepth);
      }
   }

   public void enqueueChunkUpdate(Runnable var1) {
      this.chunkUpdaters.add(var1);
   }

   public void runQueuedChunkUpdates() {
      int var1 = this.chunkUpdaters.size();
      int var2 = var1 < 1000 ? Math.max(10, var1 / 10) : var1;

      for (int var3 = 0; var3 < var2; var3++) {
         Runnable var4 = this.chunkUpdaters.poll();
         if (var4 == null) {
            break;
         }

         var4.run();
      }
   }

   public void tick(BooleanSupplier var1) {
      this.getWorldBorder().tick();
      this.calculateAmbientDarkness();
      if (this.getTickManager().shouldTick()) {
         this.tickTime();
      }

      this.chunkManager.tick(var1, true);
   }

   private void tickTime() {
      this.clientWorldProperties.setTime(this.clientWorldProperties.getTime() + 1L);
      if (this.shouldTickTimeOfDay) {
         this.clientWorldProperties.setTimeOfDay(this.clientWorldProperties.getTimeOfDay() + 1L);
      }
   }

   public void setTime(long var1, long var3, boolean var5) {
      this.clientWorldProperties.setTime(var1);
      this.clientWorldProperties.setTimeOfDay(var3);
      this.shouldTickTimeOfDay = var5;
   }

   public Iterable<Entity> getEntities() {
      return this.getEntityLookup().iterate();
   }

   public void tickEntities() {
      this.entityList.forEach(var1 -> {
         if (!var1.isRemoved() && !var1.hasVehicle() && !this.tickManager.shouldSkipTick(var1)) {
            this.tickEntity(this::tickEntity, var1);
         }
      });
      this.tickBlockEntities();
   }

   public boolean hasEntity(Entity var1) {
      return this.entityList.has(var1);
   }

   @Override
   public boolean shouldUpdatePostDeath(Entity entity) {
      BotPlayer var2 = this.botPlayer;
      return var2 != null && entity.getChunkPos().getChebyshevDistance(var2.getChunkPos()) <= this.simulationDistance;
   }

   public void tickEntity(Entity var1) {
      var1.resetPosition();
      var1.age++;
      var1.tick();

      for (Entity var3 : var1.getPassengerList()) {
         this.tickPassenger(var1, var3);
      }
   }

   private void tickPassenger(Entity var1, Entity var2) {
      if (!var2.isRemoved() && var2.getVehicle() == var1) {
         if (var2 instanceof PlayerEntity || this.entityList.has(var2)) {
            var2.resetPosition();
            var2.age++;
            var2.tickRiding();

            for (Entity var4 : var2.getPassengerList()) {
               this.tickPassenger(var2, var4);
            }
         }
      } else {
         var2.stopRiding();
      }
   }

   public void unloadBlockEntities(WorldChunk var1) {
      var1.clear();
      this.chunkManager.getLightingProvider().setColumnEnabled(var1.getPos(), false);
      this.entityManager.stopTicking(var1.getPos());
   }

   public void resetChunkColor(ChunkPos var1) {
      this.entityManager.startTicking(var1);

      for (BotWorld.RenderListener var5 : this.renderListeners) {
         var5.onChunkChanged(var1.x, var1.z);
      }
   }

   public void onChunkUnload(long var1) {
      for (BotWorld.RenderListener var6 : this.renderListeners) {
         var6.onSectionChanged(ChunkSectionPos.unpackX(var1), ChunkSectionPos.unpackY(var1), ChunkSectionPos.unpackZ(var1));
      }
   }

   @Override
   public boolean isChunkLoaded(int chunkX, int chunkZ) {
      return true;
   }

   public int getRegularEntityCount() {
      return this.entityManager.getEntityCount();
   }

   public void addEntity(Entity var1) {
      this.removeEntity(var1.getId(), RemovalReason.DISCARDED);
      this.entityManager.addEntity(var1);
   }

   public void removeEntity(int var1, RemovalReason var2) {
      Entity var3 = this.getEntityLookup().get(var1);
      if (var3 != null) {
         var3.setRemoved(var2);
         var3.onRemoved();
      }
   }

   @Nullable
   @Override
   public Entity getEntityById(int id) {
      return this.getEntityLookup().get(id);
   }

   public void disconnect() {
      this.networkHandler.getConnection().disconnect(Text.translatable("multiplayer.status.quitting"));
   }

   @Override
   public void playSound(
      @Nullable Entity source, double x, double y, double z, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed
   ) {
   }

   @Override
   public void playSoundFromEntity(
      @Nullable Entity source, Entity entity, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed
   ) {
   }

   public void playSoundFromEntity(Entity var1, SoundEvent var2, SoundCategory var3, float var4, float var5) {
   }

   public void playSound(double var1, double var3, double var5, SoundEvent var7, SoundCategory var8, float var9, float var10, boolean var11) {
   }

   @Override
   public void addParticleClient(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
   }

   @Override
   public void addParticleClient(
      ParticleEffect parameters, boolean force, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ
   ) {
   }

   public void addImportantParticle(ParticleEffect var1, double var2, double var4, double var6, double var8, double var10, double var12) {
   }

   public void addImportantParticle(ParticleEffect var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
   }

   @Override
   public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
      for (BotWorld.RenderListener var8 : this.renderListeners) {
         var8.onBlockChanged(pos);
      }
   }

   @Override
   public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {
   }

   @Override
   public void syncGlobalEvent(int eventId, BlockPos pos, int data) {
   }

   @Override
   public void syncWorldEvent(@Nullable Entity source, int eventId, BlockPos pos, int data) {
   }

   @Override
   public void addBlockBreakParticles(BlockPos pos, BlockState state) {
   }

   @Override
   public void emitGameEvent(RegistryEntry<GameEvent> event, Vec3d emitterPos, Emitter emitter) {
   }

   @Override
   public void createExplosion(
      @Nullable Entity entity,
      @Nullable DamageSource damageSource,
      @Nullable ExplosionBehavior behavior,
      double x,
      double y,
      double z,
      float power,
      boolean createFire,
      ExplosionSourceType explosionSourceType,
      ParticleEffect smallParticle,
      ParticleEffect largeParticle,
      WeightedPool<BlockParticleEffect> blockParticles,
      RegistryEntry<SoundEvent> soundEvent
   ) {
   }

   @Override
   public void sendPacket(Packet<?> packet) {
      this.networkHandler.sendPacket(packet);
   }

   @Override
   public RecipeManager getRecipeManager() {
      return this.networkHandler.getRecipeManager();
   }

   @Override
   public TickManager getTickManager() {
      return this.tickManager;
   }

   @Override
   public QueryableTickScheduler<Block> getBlockTickScheduler() {
      return EmptyTickSchedulers.getClientTickScheduler();
   }

   @Override
   public QueryableTickScheduler<Fluid> getFluidTickScheduler() {
      return EmptyTickSchedulers.getClientTickScheduler();
   }

   public BotChunkManager getChunkManager() {
      return this.chunkManager;
   }

   @Nullable
   @Override
   public MapState getMapState(MapIdComponent id) {
      return this.mapStates.get(id);
   }

   public void putClientsideMapState(MapIdComponent var1, MapState var2) {
      this.mapStates.put(var1, var2);
   }

   public void putMapState(MapIdComponent var1, MapState var2) {
   }

   public MapIdComponent increaseAndGetMapId() {
      return new MapIdComponent(0);
   }

   @Override
   public Scoreboard getScoreboard() {
      return this.networkHandler.getScoreboard();
   }

   @Override
   public List<PlayerEntity> getPlayers() {
      return this.players;
   }

   public List<EnderDragonPart> getEnderDragonParts() {
      return this.enderDragonParts;
   }

   @Override
   public RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
      return this.getRegistryManager().getOrThrow(RegistryKeys.BIOME).getOrThrow(BiomeKeys.PLAINS);
   }

   @Override
   public float getBrightness(Direction direction, boolean shaded) {
      if (!shaded) {
         return 1.0F;
      } else {
         return switch (direction) {
            case DOWN -> 0.5F;
            case UP -> 1.0F;
            case NORTH, SOUTH -> 0.8F;
            case WEST, EAST -> 0.6F;
         };
      }
   }

   @Override
   public int getColor(BlockPos pos, ColorResolver colorResolver) {
      return colorResolver.getColor(this.getBiome(pos).value(), pos.getX(), pos.getZ());
   }

   public void setSpawnPos(BlockPos var1, float var2) {
      this.setSpawnPoint(new SpawnPoint(GlobalPos.create(this.getRegistryKey(), var1), var2, 0.0F));
   }

   @Override
   public void setSpawnPoint(SpawnPoint spawnPoint) {
      this.properties.setSpawnPoint(spawnPoint);
   }

   @Override
   public SpawnPoint getSpawnPoint() {
      return this.properties.getSpawnPoint();
   }

   @Override
   public WorldEnvironmentAttributeAccess getEnvironmentAttributes() {
      return this.environmentAttributes;
   }

   @Override
   public String toString() {
      return "BotLevel";
   }

   public Properties getLevelProperties() {
      return this.clientWorldProperties;
   }

   public Map<MapIdComponent, MapState> getMapStates() {
      return Map.copyOf(this.mapStates);
   }

   public void putMapStates(Map<MapIdComponent, MapState> var1) {
      this.mapStates.putAll(var1);
   }

   @Override
   public EntityLookup<Entity> getEntityLookup() {
      return this.entityManager.getLookup();
   }

   @Override
   public String asString() {
      return "Chunks[Bot] W: " + this.chunkManager.getDebugString() + " E: " + this.entityManager.getDebugString();
   }

   public void setSimulationDistance(int var1) {
      this.simulationDistance = var1;
   }

   public int getSimulationDistance() {
      return this.simulationDistance;
   }

   @Override
   public FeatureSet getEnabledFeatures() {
      return this.networkHandler.getEnabledFeatures();
   }

   @Override
   public BrewingRecipeRegistry getBrewingRecipeRegistry() {
      return this.networkHandler.getBrewingRecipeRegistry();
   }

   @Override
   public FuelRegistry getFuelRegistry() {
      return this.networkHandler.getFuelRegistry();
   }

   @Override
   public int getSeaLevel() {
      return this.seaLevel;
   }

   @Override
   public WorldBorder getWorldBorder() {
      return this.zenithWorldBorder;
   }

   private final class BotEntityHandler implements EntityHandler<Entity> {
      public void create(Entity var1) {
         BotWorld.this.entitySetVersion++;
      }

      public void destroy(Entity var1) {
         BotWorld.this.entitySetVersion++;
      }

      public void startTicking(Entity var1) {
         BotWorld.this.entityList.add(var1);
      }

      public void stopTicking(Entity var1) {
         BotWorld.this.entityList.remove(var1);
      }

      public void startTracking(Entity var1) {
         if (var1 instanceof PlayerEntity var2) {
            BotWorld.this.players.add(var2);
         } else if (var1 instanceof EnderDragonEntity var3) {
            BotWorld.this.enderDragonParts.addAll(Arrays.asList(var3.getBodyParts()));
         }
      }

      public void stopTracking(Entity var1) {
         var1.detach();
         if (var1 instanceof PlayerEntity var2) {
            BotWorld.this.players.remove(var2);
         } else if (var1 instanceof EnderDragonEntity var3) {
            BotWorld.this.enderDragonParts.removeAll(Arrays.asList(var3.getBodyParts()));
         }
      }

      public void updateLoadStatus(Entity var1) {
      }
   }

   public interface RenderListener {
      void onBlockChanged(BlockPos var1);

      void onChunkChanged(int var1, int var2);

      void onSectionChanged(int var1, int var2, int var3);
   }
}

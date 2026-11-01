package org.zenith.base.bot.net;

import com.google.common.hash.HashCode;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.network.ChunkBatchSizeCalculator;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.recipebook.ClientRecipeManager;
import net.minecraft.client.world.ClientWorld.Properties;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPosition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TrackedPosition;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.encryption.NetworkEncryptionUtils.SecureRandomUtil;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.message.LastSeenMessageList.Acknowledgment;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.AcknowledgeChunksC2SPacket;
import net.minecraft.network.packet.c2s.play.AcknowledgeReconfigurationC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientTickEndC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerLoadedC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.s2c.common.ClearDialogS2CPacket;
import net.minecraft.network.packet.s2c.common.ShowDialogS2CPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockValueDebugS2CPacket;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkBiomeDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkSentS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkValueDebugS2CPacket;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.CommonPlayerSpawnInfo;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.DamageTiltS2CPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.DebugSampleS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EndCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterReconfigurationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityValueDebugS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EventDebugS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.GameTestHighlightPosS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.LightData;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.MoveMinecartAlongTrackS2CPacket;
import net.minecraft.network.packet.s2c.play.NbtQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenMountScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRotationS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ProjectilePowerS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookAddS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.RecipeBookSettingsS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreResetS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket;
import net.minecraft.network.packet.s2c.play.ServerMetadataS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCursorItemS2CPacket;
import net.minecraft.network.packet.s2c.play.SetPlayerInventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.network.packet.s2c.play.SimulationDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.StartChunkSendS2CPacket;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.TestInstanceBlockStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.TickStepS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateTickRateS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.network.packet.s2c.play.WaypointS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderCenterChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInitializeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInterpolateSizeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderSizeChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningBlocksChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningTimeChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkBiomeDataS2CPacket.Serialized;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket.NodeFactory;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket.Entry;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket.Reason;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket.Operation;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.display.CuttingRecipeDisplay.Grouping;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.DynamicRegistryManager.Immutable;
import net.minecraft.registry.Registry.PendingTagLoad;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.AbstractTeam.CollisionRule;
import net.minecraft.scoreboard.AbstractTeam.VisibilityRule;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.sync.ComponentChangesHash.ComponentHasher;
import net.minecraft.storage.NbtReadView;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ErrorReporter.Logging;
import net.minecraft.util.dynamic.HashCodeOps;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.tick.TickManager;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.BotPhase;
import org.zenith.base.bot.world.BotInteractionManager;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotRemotePlayer;
import org.zenith.base.bot.world.BotWorld;

public final class BotPlayHandler extends BotCommonHandler implements ClientPlayPacketListener, TickablePacketListener {
   private ComponentHasher zenith$componentHasher;
   private static final Logger LOGGER = LogUtils.getLogger();
   private final GameProfile profile;
   private final Immutable registries;
   private final FeatureSet enabledFeatures;
   private final ChunkBatchSizeCalculator chunkBatchSizeCalculator = new ChunkBatchSizeCalculator();
   private final Random random = Random.createThreadSafe();
   private final Map<UUID, PlayerListEntry> playerListEntries = new ConcurrentHashMap<>();
   private final Scoreboard scoreboard = new Scoreboard();
   private final BotCommandSource commandSource = new BotCommandSource(this);
   private volatile CommandDispatcher<CommandSource> commandDispatcher = new CommandDispatcher();
   private volatile Set<RegistryKey<World>> worldKeys = Set.of();
   private final BrewingRecipeRegistry brewingRecipeRegistry;
   private FuelRegistry fuelRegistry;
   private ClientRecipeManager recipeManager = new ClientRecipeManager(Map.of(), Grouping.empty());
   @Nullable
   private volatile BotWorld world;
   @Nullable
   private volatile BotPlayer player;
   @Nullable
   private volatile BotInteractionManager interactionManager;
   @Nullable
   private Properties worldProperties;
   @Nullable
   private volatile Text currentScreenTitle;
   private volatile int currentScreenSyncId = -1;
   private int chunkLoadDistance = 3;
   private int simulationDistance = 3;
   private boolean secureChatEnforced;
   private OptionalInt removedPlayerVehicleId = OptionalInt.empty();
   private boolean playerLoadedSent;
   private int loadWaitTicks;
   private boolean initialChunksComing;

   public ComponentHasher getComponentHasher() {
      if (this.zenith$componentHasher == null) {
         RegistryOps var1 = this.registries.getOps(HashCodeOps.INSTANCE);
         this.zenith$componentHasher = var1x -> ((HashCode)((com.mojang.serialization.DataResult<HashCode>)(Object)var1x.encode(var1))
               .<IllegalArgumentException>getOrThrow(var1xx -> new IllegalArgumentException("Failed to hash " + var1x + ": " + var1xx)))
            .asInt();
      }

      return this.zenith$componentHasher;
   }

   public boolean isPlayerLoaded() {
      return this.playerLoadedSent;
   }

   public BotPlayHandler(BotClient var1, BotConnection var2, BotConnectionState var3) {
      super(var1, var2, var3);
      this.profile = var3.profile();
      this.registries = var3.receivedRegistries();
      this.enabledFeatures = var3.enabledFeatures();
      this.brewingRecipeRegistry = BrewingRecipeRegistry.create(this.enabledFeatures);
      this.fuelRegistry = FuelRegistry.createDefault(this.registries, this.enabledFeatures);
   }

   public GameProfile getProfile() {
      return this.profile;
   }

   public Immutable getRegistryManager() {
      return this.registries;
   }

   public FeatureSet getEnabledFeatures() {
      return this.enabledFeatures;
   }

   public BotClient getClient() {
      return this.client;
   }

   @Nullable
   public BotWorld getWorld() {
      return this.world;
   }

   @Nullable
   public BotPlayer getPlayer() {
      return this.player;
   }

   public BotInteractionManager getInteractionManager() {
      BotInteractionManager var1 = this.interactionManager;
      if (var1 == null) {
         throw new IllegalStateException("Bot interaction manager is not initialized (no GameJoin yet)");
      } else {
         return var1;
      }
   }

   @Nullable
   public BotInteractionManager getInteractionManagerOrNull() {
      return this.interactionManager;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   @Nullable
   public Text getCurrentScreenTitle() {
      BotPlayer var1 = this.player;
      if (var1 != null && var1.currentScreenHandler != null) {
         return var1.currentScreenHandler.syncId == this.currentScreenSyncId ? this.currentScreenTitle : null;
      } else {
         return null;
      }
   }

   public boolean hasOpenScreen() {
      return this.getCurrentScreenTitle() != null;
   }

   public RecipeManager getRecipeManager() {
      return this.recipeManager;
   }

   public BrewingRecipeRegistry getBrewingRecipeRegistry() {
      return this.brewingRecipeRegistry;
   }

   public FuelRegistry getFuelRegistry() {
      return this.fuelRegistry;
   }

   @Nullable
   public PlayerListEntry getPlayerListEntry(UUID var1) {
      return this.playerListEntries.get(var1);
   }

   @Nullable
   public PlayerListEntry getPlayerListEntry(String var1) {
      for (PlayerListEntry var3 : this.playerListEntries.values()) {
         if (var3.getProfile().name().equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public Iterable<PlayerListEntry> getPlayerList() {
      return this.playerListEntries.values();
   }

   public CommandDispatcher<CommandSource> getCommandDispatcher() {
      return this.commandDispatcher;
   }

   public BotCommandSource getCommandSource() {
      return this.commandSource;
   }

   public Set<RegistryKey<World>> getWorldKeys() {
      return this.worldKeys;
   }

   @Override
   public void tick() {
      if (this.client.getPhase() == BotPhase.PLAY && this.connection.getPacketListener() == this) {
         BotWorld var1 = this.world;
         BotPlayer var2 = this.player;
         BotInteractionManager var3 = this.interactionManager;
         if (var1 != null && var2 != null && var3 != null) {
            var1.runQueuedChunkUpdates();
            var1.getChunkManager().getLightingProvider().doLightUpdates();
            var3.tick();
            this.client.onBotUpdate(var1, var2);
            var1.tick(() -> true);
            var1.tickEntities();
            this.tickPlayerLoaded(var1, var2);
         }

         this.sendPacket(ClientTickEndC2SPacket.INSTANCE);
      }
   }

   private void tickPlayerLoaded(BotWorld var1, BotPlayer var2) {
      if (!this.playerLoadedSent) {
         boolean var3 = var1.getChunkManager().getChunk(var2.getChunkPos().x, var2.getChunkPos().z, null, false) != null;
         if (var3 || ++this.loadWaitTicks >= 60) {
            this.playerLoadedSent = true;
            this.loadWaitTicks = 0;
            this.sendPacket(new PlayerLoadedC2SPacket());
         }
      }
   }

   public void sendChatMessage(String var1) {
      this.sendPacket(new ChatMessageC2SPacket(var1, Instant.now(), SecureRandomUtil.nextLong(), null, new Acknowledgment(0, new BitSet(), (byte)0)));
   }

   public void sendCommand(String var1) {
      this.sendPacket(new CommandExecutionC2SPacket(var1));
   }

   @Override
   public void onGameJoin(GameJoinS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      CommonPlayerSpawnInfo var2 = packet.commonPlayerSpawnInfo();
      this.worldKeys = new LinkedHashSet<>(packet.dimensionIds());
      this.chunkLoadDistance = packet.viewDistance();
      this.simulationDistance = packet.simulationDistance();
      this.secureChatEnforced = packet.enforcesSecureChat();
      BotInteractionManager var3 = new BotInteractionManager(this);
      this.interactionManager = var3;
      Properties var4 = new Properties(Difficulty.NORMAL, packet.hardcore(), var2.isFlat());
      this.worldProperties = var4;
      BotWorld var5 = new BotWorld(
         this, var4, var2.dimension(), var2.dimensionType(), this.chunkLoadDistance, this.simulationDistance, var2.isDebug(), var2.seed(), var2.seaLevel()
      );
      this.world = var5;
      BotPlayer var6 = new BotPlayer(var5, this, false, false);
      this.player = var6;
      var6.init();
      var6.setId(packet.playerEntityId());
      var6.setYaw(-180.0F);
      var5.setBotPlayer(var6);
      var5.addEntity(var6);
      var3.copyAbilities(var6);
      var6.setReducedDebugInfo(packet.reducedDebugInfo());
      var6.setLastDeathPos(var2.lastDeathLocation());
      var6.setPortalCooldown(var2.portalCooldown());
      var3.setGameModes(var2.gameMode(), var2.lastGameMode());
      this.playerLoadedSent = false;
      this.loadWaitTicks = 0;
      this.initialChunksComing = false;
      LOGGER.debug("Bot {} joined dimension={} entityId={}", new Object[]{this.client.getName(), var2.dimension().getValue(), packet.playerEntityId()});
      this.client.onGameJoin(var5, var6);
   }

   @Override
   public void onPlayerRespawn(PlayerRespawnS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      BotWorld var3 = this.world;
      BotInteractionManager var4 = this.interactionManager;
      if (var2 != null && var3 != null && var4 != null && this.worldProperties != null) {
         CommonPlayerSpawnInfo var5 = packet.commonPlayerSpawnInfo();
         RegistryKey var6 = var5.dimension();
         RegistryEntry var7 = var5.dimensionType();
         boolean var8 = var6 != var2.getEntityWorld().getRegistryKey();
         BotWorld var9 = var3;
         if (var8) {
            var var10 = var3.getMapStates();
            Properties var11 = new Properties(this.worldProperties.getDifficulty(), this.worldProperties.isHardcore(), var5.isFlat());
            this.worldProperties = var11;
            var9 = new BotWorld(this, var11, var6, var7, this.chunkLoadDistance, this.simulationDistance, var5.isDebug(), var5.seed(), var5.seaLevel());
            var9.putMapStates(var10);
            this.world = var9;
         }

         if (var2.shouldCloseHandledScreenOnRespawn()) {
            var2.closeHandledScreen();
         }

         boolean var13 = packet.hasFlag((byte)2);
         BotPlayer var14 = new BotPlayer(var9, this, var13 && var2.isSneaking(), var13 && var2.isSprinting());
         var14.setId(var2.getId());
         this.player = var14;
         if (var13) {
            var var12 = var2.getDataTracker().getChangedEntries();
            if (var12 != null) {
               var14.getDataTracker().writeUpdatedEntries(var12);
            }

            var14.setVelocity(var2.getVelocity());
            var14.setYaw(var2.getYaw());
            var14.setPitch(var2.getPitch());
         } else {
            var14.init();
            var14.setYaw(-180.0F);
         }

         if (packet.hasFlag((byte)1)) {
            var14.getAttributes().setFrom(var2.getAttributes());
         } else {
            var14.getAttributes().setBaseFrom(var2.getAttributes());
         }

         var9.setBotPlayer(var14);
         var9.addEntity(var14);
         var4.copyAbilities(var14);
         var14.setReducedDebugInfo(var2.hasReducedDebugInfo());
         var14.setLastDeathPos(var5.lastDeathLocation());
         var14.setPortalCooldown(var5.portalCooldown());
         var4.setGameModes(var5.gameMode(), var5.lastGameMode());
         this.playerLoadedSent = false;
         this.loadWaitTicks = 0;
         if (var8) {
            this.client.onGameJoin(var9, var14);
         } else {
            this.client.onRespawn(var9, var14);
         }
      }
   }

   @Override
   public void onEnterReconfiguration(EnterReconfigurationS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.world = null;
      this.player = null;
      this.interactionManager = null;
      this.playerLoadedSent = false;
      this.loadWaitTicks = 0;
      this.client.setPhase(BotPhase.CONFIGURATION);
      this.client.onWorldUnload();
      BotConnectionState var2 = new BotConnectionState(
         this.profile, this.registries, this.enabledFeatures, this.brand, this.serverCookies, this.customReportDetails, this.serverLinks
      );
      BotConfigHandler var3 = new BotConfigHandler(this.client, this.connection, var2);
      this.connection.transitionInbound(ConfigurationStates.S2C, var3);
      this.connection.send(AcknowledgeReconfigurationC2SPacket.INSTANCE);
      this.connection.transitionOutbound(ConfigurationStates.C2S);
   }

   @Override
   public void onBundle(BundleS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());

      for (Packet var3 : packet.getPackets()) {
         var3.apply(this);
      }
   }

   @Override
   public void onPlayerPositionLook(PlayerPositionLookS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 == null) {
         this.sendPacket(new TeleportConfirmC2SPacket(packet.teleportId()));
      } else {
         if (!var2.hasVehicle()) {
            setPosition(packet.change(), packet.relatives(), var2, false);
         }

         this.sendPacket(new TeleportConfirmC2SPacket(packet.teleportId()));
         this.sendPacket(new Full(var2.getX(), var2.getY(), var2.getZ(), var2.getYaw(), var2.getPitch(), false, false));
      }
   }

   private static boolean setPosition(EntityPosition var0, Set<PositionFlag> var1, Entity var2, boolean var3) {
      EntityPosition var4 = EntityPosition.fromEntity(var2);
      EntityPosition var5 = EntityPosition.apply(var4, var0, var1);
      boolean var6 = var4.position().squaredDistanceTo(var5.position()) > 4096.0;
      if (var3 && !var6) {
         var2.updateTrackedPositionAndAngles(var5.position(), var5.yaw(), var5.pitch());
         var2.setVelocity(var5.deltaMovement());
         return true;
      } else {
         var2.setPosition(var5.position());
         var2.setVelocity(var5.deltaMovement());
         var2.setYaw(var5.yaw());
         var2.setPitch(var5.pitch());
         EntityPosition var7 = new EntityPosition(var2.getLastRenderPos(), Vec3d.ZERO, var2.lastYaw, var2.lastPitch);
         EntityPosition var8 = EntityPosition.apply(var7, var0, var1);
         var2.setLastPositionAndAngles(var8.position(), var8.yaw(), var8.pitch());
         return false;
      }
   }

   @Override
   public void onPlayerRotation(PlayerRotationS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         var var3 = PositionFlag.ofRot(packet.relativeYaw(), packet.relativePitch());
         EntityPosition var4 = EntityPosition.fromEntity(var2);
         EntityPosition var5 = EntityPosition.apply(var4, var4.withRotation(packet.yaw(), packet.pitch()), var3);
         var2.setYaw(var5.yaw());
         var2.setPitch(var5.pitch());
         var2.updateLastAngles();
         this.sendPacket(new LookAndOnGround(var2.getYaw(), var2.getPitch(), false, false));
      } else {
         this.sendPacket(new LookAndOnGround(packet.yaw(), packet.pitch(), false, false));
      }
   }

   @Override
   public void onVehicleMove(VehicleMoveS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         Entity var3 = var2.getRootVehicle();
         if (var3 != var2 && var3.isLogicalSideForUpdatingMovement()) {
            Vec3d var4 = packet.position();
            Vec3d var5 = var3.isInterpolating() ? var3.getInterpolator().getLerpedPos() : var3.getEntityPos();
            if (var4.distanceTo(var5) > 1.0E-5F) {
               if (var3.isInterpolating()) {
                  var3.getInterpolator().clear();
               }

               var3.updatePositionAndAngles(var4.getX(), var4.getY(), var4.getZ(), packet.yaw(), packet.pitch());
            }

            this.connection.send(VehicleMoveC2SPacket.fromVehicle(var3));
         }
      }
   }

   @Override
   public void onStartChunkSend(StartChunkSendS2CPacket packet) {
      this.chunkBatchSizeCalculator.onStartChunkSend();
   }

   @Override
   public void onChunkSent(ChunkSentS2CPacket packet) {
      this.chunkBatchSizeCalculator.onChunkSent(packet.batchSize());
      this.sendPacket(new AcknowledgeChunksC2SPacket(this.chunkBatchSizeCalculator.getDesiredChunksPerTick()));
   }

   @Override
   public void onChunkData(ChunkDataS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         int var3 = packet.getChunkX();
         int var4 = packet.getChunkZ();
         ChunkData var5 = packet.getChunkData();
         var2.getChunkManager().loadChunkFromPacket(var3, var4, var5.getSectionsDataBuf(), var5.getHeightmap(), var5.getBlockEntities(var3, var4));
         LightData var6 = packet.getLightData();
         var2.enqueueChunkUpdate(() -> {
            this.readLightData(var3, var4, var6);
            WorldChunk var5x = var2.getChunkManager().getWorldChunk(var3, var4, false);
            if (var5x != null) {
               this.markChunkSectionsReady(var2, var5x);
            }
         });
      }
   }

   private void markChunkSectionsReady(BotWorld var1, WorldChunk var2) {
      LightingProvider var3 = var1.getChunkManager().getLightingProvider();
      ChunkSection[] var4 = var2.getSectionArray();
      ChunkPos var5 = var2.getPos();

      for (int var6 = 0; var6 < var4.length; var6++) {
         ChunkSection var7 = var4[var6];
         int var8 = var1.sectionIndexToCoord(var6);
         var3.setSectionStatus(ChunkSectionPos.from(var5, var8), var7.isEmpty());
      }
   }

   @Override
   public void onChunkBiomeData(ChunkBiomeDataS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         for (Serialized var4 : packet.chunkBiomeData()) {
            var2.getChunkManager().onChunkBiomeData(var4.pos().x, var4.pos().z, var4.toReadingBuf());
         }

         for (Serialized var6 : packet.chunkBiomeData()) {
            var2.resetChunkColor(new ChunkPos(var6.pos().x, var6.pos().z));
         }
      }
   }

   @Override
   public void onUnloadChunk(UnloadChunkS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getChunkManager().unload(packet.pos());
         ChunkPos var3 = packet.pos();
         var2.enqueueChunkUpdate(() -> {
            LightingProvider var2x = var2.getLightingProvider();
            var2x.setColumnEnabled(var3, false);

            for (int var3x = var2x.getBottomY(); var3x < var2x.getTopY(); var3x++) {
               ChunkSectionPos var4 = ChunkSectionPos.from(var3, var3x);
               var2x.enqueueSectionData(LightType.BLOCK, var4, null);
               var2x.enqueueSectionData(LightType.SKY, var4, null);
            }

            for (int var5 = var2.getBottomSectionCoord(); var5 <= var2.getTopSectionCoord(); var5++) {
               var2x.setSectionStatus(ChunkSectionPos.from(var3, var5), true);
            }
         });
      }
   }

   @Override
   public void onLightUpdate(LightUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         int var3 = packet.getChunkX();
         int var4 = packet.getChunkZ();
         LightData var5 = packet.getData();
         var2.enqueueChunkUpdate(() -> this.readLightData(var3, var4, var5));
      }
   }

   private void readLightData(int var1, int var2, LightData var3) {
      BotWorld var4 = this.world;
      if (var4 != null) {
         LightingProvider var5 = var4.getChunkManager().getLightingProvider();
         BitSet var6 = var3.getInitedSky();
         BitSet var7 = var3.getUninitedSky();
         var var8 = var3.getSkyNibbles().iterator();
         this.updateLighting(var1, var2, var5, LightType.SKY, var6, var7, var8);
         BitSet var9 = var3.getInitedBlock();
         BitSet var10 = var3.getUninitedBlock();
         var var11 = var3.getBlockNibbles().iterator();
         this.updateLighting(var1, var2, var5, LightType.BLOCK, var9, var10, var11);
         var5.setColumnEnabled(new ChunkPos(var1, var2), true);
      }
   }

   private void updateLighting(int var1, int var2, LightingProvider var3, LightType var4, BitSet var5, BitSet var6, Iterator<byte[]> var7) {
      for (int var8 = 0; var8 < var3.getHeight(); var8++) {
         int var9 = var3.getBottomY() + var8;
         boolean var10 = var5.get(var8);
         boolean var11 = var6.get(var8);
         if (var10 || var11) {
            var3.enqueueSectionData(
               var4, ChunkSectionPos.from(var1, var9, var2), var10 ? new ChunkNibbleArray((byte[])((byte[])var7.next()).clone()) : new ChunkNibbleArray()
            );
         }
      }
   }

   @Override
   public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         packet.visitUpdates((var1, var2x) -> var2.handleBlockUpdate(var1, var2x, 19));
      }
   }

   @Override
   public void onBlockUpdate(BlockUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.handleBlockUpdate(packet.getPos(), packet.getState(), 19);
      }
   }

   @Override
   public void onChunkLoadDistance(ChunkLoadDistanceS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.chunkLoadDistance = packet.getDistance();
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getChunkManager().updateLoadDistance(packet.getDistance());
      }
   }

   @Override
   public void onSimulationDistance(SimulationDistanceS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.simulationDistance = packet.simulationDistance();
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.setSimulationDistance(this.simulationDistance);
      }
   }

   @Override
   public void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getChunkManager().setChunkMapCenter(packet.getChunkX(), packet.getChunkZ());
      }
   }

   @Override
   public void onPlayerActionResponse(PlayerActionResponseS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.handlePlayerActionResponse(packet.sequence());
      }
   }

   @Override
   public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getBlockEntity(packet.getPos(), packet.getBlockEntityType()).ifPresent(var2x -> {
            NbtCompound var3 = packet.getNbt();
            if (!var3.isEmpty()) {
               try (Logging var4 = new Logging(var2x.getReporterContext(), LOGGER)) {
                  var2x.read(NbtReadView.create(var4, this.registries, var3));
               }
            }
         });
      }
   }

   @Override
   public void onBlockEvent(BlockEventS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.addSyncedBlockEvent(packet.getPos(), packet.getBlock(), packet.getType(), packet.getData());
      }
   }

   @Override
   public void onBlockBreakingProgress(BlockBreakingProgressS2CPacket packet) {
   }

   @Override
   public void onWorldEvent(WorldEventS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         if (packet.isGlobal()) {
            var2.syncGlobalEvent(packet.getEventId(), packet.getPos(), packet.getData());
         } else {
            var2.syncWorldEvent(packet.getEventId(), packet.getPos(), packet.getData());
         }
      }
   }

   @Override
   public void onEntitySpawn(EntitySpawnS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == packet.getEntityId()) {
            this.removedPlayerVehicleId = OptionalInt.empty();
         }

         Entity var3 = this.createEntity(var2, packet);
         if (var3 != null) {
            var3.onSpawnPacket(packet);
            var2.addEntity(var3);
         } else {
            LOGGER.warn("Bot {}: skipping entity with id {}", this.client.getName(), packet.getEntityType());
         }
      }
   }

   @Nullable
   private Entity createEntity(BotWorld var1, EntitySpawnS2CPacket var2) {
      EntityType var3 = var2.getEntityType();
      if (var3 == EntityType.PLAYER) {
         PlayerListEntry var4 = this.getPlayerListEntry(var2.getUuid());
         if (var4 == null) {
            LOGGER.warn("Bot {}: server added player prior to sending player info (id {})", this.client.getName(), var2.getUuid());
            return null;
         } else {
            return new BotRemotePlayer(var1, this, var4.getProfile());
         }
      } else {
         return var3.create(var1, SpawnReason.LOAD);
      }
   }

   @Override
   public void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = var2.getEntityById(packet.getEntityId());
         if (var3 != null) {
            var3.setVelocityClient(packet.getVelocity());
         }
      }
   }

   @Override
   public void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = var2.getEntityById(packet.id());
         if (var3 != null) {
            var3.getDataTracker().writeUpdatedEntries(packet.trackedValues());
         }
      }
   }

   @Override
   public void onEntityPositionSync(EntityPositionSyncS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      BotPlayer var3 = this.player;
      if (var2 != null && var3 != null) {
         Entity var4 = var2.getEntityById(packet.id());
         if (var4 != null) {
            Vec3d var5 = packet.values().position();
            var4.getTrackedPosition().setPos(var5);
            if (!var4.isLogicalSideForUpdatingMovement()) {
               float var6 = packet.values().yaw();
               float var7 = packet.values().pitch();
               boolean var8 = var4.getEntityPos().squaredDistanceTo(var5) > 4096.0;
               if (var2.hasEntity(var4) && !var8) {
                  var4.updateTrackedPositionAndAngles(var5, var6, var7);
               } else {
                  var4.refreshPositionAndAngles(var5.x, var5.y, var5.z, var6, var7);
                  if (var4.hasPassengerDeep(var3)) {
                     var4.updatePassengerPosition(var3);
                     var3.resetPosition();
                  }
               }

               var4.setOnGround(packet.onGround());
            }
         }
      }
   }

   @Override
   public void onEntityPosition(EntityPositionS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      BotPlayer var3 = this.player;
      if (var2 != null && var3 != null) {
         Entity var4 = var2.getEntityById(packet.entityId());
         if (var4 == null) {
            if (this.removedPlayerVehicleId.isPresent() && this.removedPlayerVehicleId.getAsInt() == packet.entityId()) {
               LOGGER.debug("Bot {}: teleport for removed vehicle {}, applying to player", this.client.getName(), packet.entityId());
               setPosition(packet.change(), packet.relatives(), var3, false);
               this.connection.send(new Full(var3.getX(), var3.getY(), var3.getZ(), var3.getYaw(), var3.getPitch(), false, false));
            }
         } else {
            boolean var5 = packet.relatives().contains(PositionFlag.X)
               || packet.relatives().contains(PositionFlag.Y)
               || packet.relatives().contains(PositionFlag.Z);
            boolean var6 = var2.hasEntity(var4) || !var4.isLogicalSideForUpdatingMovement() || var5;
            boolean var7 = setPosition(packet.change(), packet.relatives(), var4, var6);
            var4.setOnGround(packet.onGround());
            if (!var7 && var4.hasPassengerDeep(var3)) {
               var4.updatePassengerPosition(var3);
               var3.resetPosition();
               if (var4.isLogicalSideForUpdatingMovement()) {
                  this.connection.send(VehicleMoveC2SPacket.fromVehicle(var4));
               }
            }
         }
      }
   }

   @Override
   public void onEntity(EntityS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = packet.getEntity(var2);
         if (var3 != null) {
            if (var3.isLogicalSideForUpdatingMovement()) {
               TrackedPosition var4 = var3.getTrackedPosition();
               Vec3d var5 = var4.withDelta(packet.getDeltaX(), packet.getDeltaY(), packet.getDeltaZ());
               var4.setPos(var5);
            } else {
               if (packet.isPositionChanged()) {
                  TrackedPosition var6 = var3.getTrackedPosition();
                  Vec3d var7 = var6.withDelta(packet.getDeltaX(), packet.getDeltaY(), packet.getDeltaZ());
                  var6.setPos(var7);
                  if (packet.hasRotation()) {
                     var3.updateTrackedPositionAndAngles(var7, packet.getYaw(), packet.getPitch());
                  } else {
                     var3.updateTrackedPosition(var7);
                  }
               } else if (packet.hasRotation()) {
                  var3.updateTrackedAngles(packet.getYaw(), packet.getPitch());
               }

               var3.setOnGround(packet.isOnGround());
            }
         }
      }
   }

   @Override
   public void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = packet.getEntity(var2);
         if (var3 != null) {
            var3.updateTrackedHeadRotation(packet.getHeadYaw(), 3);
         }
      }
   }

   @Override
   public void onEntitiesDestroy(EntitiesDestroyS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      BotPlayer var3 = this.player;
      if (var2 != null) {
         packet.getEntityIds().forEach(var3x -> {
            Entity var4 = var2.getEntityById(var3x);
            if (var4 != null) {
               if (var3 != null && var4.hasPassengerDeep(var3)) {
                  this.removedPlayerVehicleId = OptionalInt.of(var3x);
               }

               var2.removeEntity(var3x, RemovalReason.DISCARDED);
            }
         });
      }
   }

   @Override
   public void onEntityAnimation(EntityAnimationS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = var2.getEntityById(packet.getEntityId());
         if (var3 != null) {
            if (packet.getAnimationId() == 0) {
               ((LivingEntity)var3).swingHand(Hand.MAIN_HAND);
            } else if (packet.getAnimationId() == 3) {
               ((LivingEntity)var3).swingHand(Hand.OFF_HAND);
            } else if (packet.getAnimationId() == 2) {
               ((PlayerEntity)var3).wakeUp(false, false);
            }
         }
      }
   }

   @Override
   public void onEntityStatus(EntityStatusS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = packet.getEntity(var2);
         if (var3 != null) {
            switch (packet.getStatus()) {
               case 21:
               case 35:
               case 63:
                  break;
               default:
                  var3.handleStatus(packet.getStatus());
            }
         }
      }
   }

   @Override
   public void onEntityAttach(EntityAttachS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null && var2.getEntityById(packet.getAttachedEntityId()) instanceof Leashable var3) {
         var3.setUnresolvedLeashHolderId(packet.getHoldingEntityId());
      }
   }

   @Override
   public void onEntityPassengersSet(EntityPassengersSetS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      BotPlayer var3 = this.player;
      if (var2 != null) {
         Entity var4 = var2.getEntityById(packet.getEntityId());
         if (var4 == null) {
            LOGGER.warn("Bot {}: received passengers for unknown entity", this.client.getName());
         } else {
            var4.removeAllPassengers();

            for (int var8 : packet.getPassengerIds()) {
               Entity var9 = var2.getEntityById(var8);
               if (var9 != null) {
                  var9.startRiding(var4, true, false);
                  if (var9 == var3) {
                     this.removedPlayerVehicleId = OptionalInt.empty();
                  }
               }
            }
         }
      }
   }

   @Override
   public void onEntityAttributes(EntityAttributesS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = var2.getEntityById(packet.getEntityId());
         if (var3 != null) {
            if (!(var3 instanceof LivingEntity var4)) {
               throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + var3 + ")");
            } else {
               AttributeContainer var5 = var4.getAttributes();

               for (Entry var7 : packet.getEntries()) {
                  EntityAttributeInstance var8 = var5.getCustomInstance(var7.attribute());
                  if (var8 == null) {
                     LOGGER.warn("Bot {}: entity {} does not have attribute {}", new Object[]{this.client.getName(), var3, var7.attribute().getIdAsString()});
                  } else {
                     var8.setBaseValue(var7.base());
                     var8.clearModifiers();

                     for (EntityAttributeModifier var10 : var7.modifiers()) {
                        var8.addTemporaryModifier(var10);
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void onEntityStatusEffect(EntityStatusEffectS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         if (var2.getEntityById(packet.getEntityId()) instanceof LivingEntity var4) {
            RegistryEntry var5 = packet.getEffectId();
            StatusEffectInstance var6 = new StatusEffectInstance(
               var5, packet.getDuration(), packet.getAmplifier(), packet.isAmbient(), packet.shouldShowParticles(), packet.shouldShowIcon(), null
            );
            if (!packet.keepFading()) {
               var6.skipFading();
            }

            var4.setStatusEffect(var6, null);
         }
      }
   }

   @Override
   public void onRemoveEntityStatusEffect(RemoveEntityStatusEffectS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null && packet.getEntity(var2) instanceof LivingEntity var3) {
         var3.removeStatusEffectInternal(packet.effect());
      }
   }

   @Override
   public void onEntityEquipmentUpdate(EntityEquipmentUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null && var2.getEntityById(packet.getEntityId()) instanceof LivingEntity var3) {
         packet.getEquipmentList().forEach(var1 -> var3.equipStack((EquipmentSlot)var1.getFirst(), (ItemStack)var1.getSecond()));
      }
   }

   @Override
   public void onEntityDamage(EntityDamageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = var2.getEntityById(packet.entityId());
         if (var3 != null) {
            var3.onDamaged(packet.createDamageSource(var2));
         }
      }
   }

   @Override
   public void onDamageTilt(DamageTiltS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = var2.getEntityById(packet.id());
         if (var3 != null) {
            var3.animateDamage(packet.yaw());
         }
      }
   }

   @Override
   public void onMoveMinecartAlongTrack(MoveMinecartAlongTrackS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = packet.getEntity(var2);
         if (var3 instanceof AbstractMinecartEntity var4
            && !var3.isLogicalSideForUpdatingMovement()
            && var4.getController() instanceof ExperimentalMinecartController var5) {
            var5.stagingLerpSteps.addAll(packet.lerpSteps());
         }
      }
   }

   @Override
   public void onProjectilePower(ProjectilePowerS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null && var2.getEntityById(packet.getEntityId()) instanceof ExplosiveProjectileEntity var3) {
         var3.accelerationPower = packet.getAccelerationPower();
      }
   }

   @Override
   public void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         Entity var3 = var2.getEntityById(packet.getEntityId());
         if (var3 != null) {
            if (var3 instanceof ItemEntity var4) {
               ItemStack var5 = var4.getStack();
               if (!var5.isEmpty()) {
                  var5.decrement(packet.getStackAmount());
               }

               if (var5.isEmpty()) {
                  var2.removeEntity(packet.getEntityId(), RemovalReason.DISCARDED);
               }
            } else if (!(var3 instanceof ExperienceOrbEntity)) {
               var2.removeEntity(packet.getEntityId(), RemovalReason.DISCARDED);
            }
         }
      }
   }

   @Override
   public void onExplosion(ExplosionS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         packet.playerKnockback().ifPresent(var2::addVelocityInternal);
      }
   }

   @Override
   public void onLookAt(LookAtS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      BotPlayer var3 = this.player;
      if (var2 != null && var3 != null) {
         Vec3d var4 = packet.getTargetPosition(var2);
         if (var4 != null) {
            var3.lookAt(packet.getSelfAnchor(), var4);
         }
      }
   }

   @Override
   public void onHealthUpdate(HealthUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         var2.updateHealth(packet.getHealth());
         var2.getHungerManager().setFoodLevel(packet.getFood());
         var2.getHungerManager().setSaturationLevel(packet.getSaturation());
      }
   }

   @Override
   public void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         var2.setExperience(packet.getBarProgress(), packet.getExperienceLevel(), packet.getExperience());
      }
   }

   @Override
   public void onUpdateSelectedSlot(UpdateSelectedSlotS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null && PlayerInventory.isValidHotbarIndex(packet.slot())) {
         var2.getInventory().setSelectedSlot(packet.slot());
      }
   }

   @Override
   public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         ItemStack var3 = packet.getStack();
         int var4 = packet.getSlot();
         if (packet.getSyncId() == 0) {
            var2.playerScreenHandler.setStackInSlot(var4, packet.getRevision(), var3);
         } else if (packet.getSyncId() == var2.currentScreenHandler.syncId) {
            var2.currentScreenHandler.setStackInSlot(var4, packet.getRevision(), var3);
         }
      }
   }

   @Override
   public void onSetCursorItem(SetCursorItemS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         var2.currentScreenHandler.setCursorStack(packet.contents());
      }
   }

   @Override
   public void onSetPlayerInventory(SetPlayerInventoryS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         var2.getInventory().setStack(packet.slot(), packet.contents());
      }
   }

   @Override
   public void onInventory(InventoryS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         if (packet.syncId() == 0) {
            var2.playerScreenHandler.updateSlotStacks(packet.revision(), packet.contents(), packet.cursorStack());
         } else if (packet.syncId() == var2.currentScreenHandler.syncId) {
            var2.currentScreenHandler.updateSlotStacks(packet.revision(), packet.contents(), packet.cursorStack());
         }
      }
   }

   @Override
   public void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null && var2.currentScreenHandler.syncId == packet.getSyncId()) {
         var2.currentScreenHandler.setProperty(packet.getPropertyId(), packet.getValue());
      }
   }

   @Override
   public void onOpenScreen(OpenScreenS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         ScreenHandler var3 = packet.getScreenHandlerType().create(packet.getSyncId(), var2.getInventory());
         var2.currentScreenHandler = var3;
         this.currentScreenTitle = packet.getName();
         this.currentScreenSyncId = packet.getSyncId();
      }
   }

   @Override
   public void onCloseScreen(CloseScreenS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         var2.closeScreen();
      }
   }

   @Override
   public void onSetTradeOffers(SetTradeOffersS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         ScreenHandler var3 = var2.currentScreenHandler;
         if (packet.getSyncId() == var3.syncId && var3 instanceof MerchantScreenHandler var4) {
            var4.setOffers(packet.getOffers());
            var4.setExperienceFromServer(packet.getExperience());
            var4.setLevelProgress(packet.getLevelProgress());
            var4.setLeveled(packet.isLeveled());
            var4.setCanRefreshTrades(packet.isRefreshable());
         }
      }
   }

   @Override
   public void onCooldownUpdate(CooldownUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         if (packet.cooldown() == 0) {
            var2.getItemCooldownManager().remove(packet.cooldownGroup());
         } else {
            var2.getItemCooldownManager().set(packet.cooldownGroup(), packet.cooldown());
         }
      }
   }

   @Override
   public void onPlayerAbilities(PlayerAbilitiesS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotPlayer var2 = this.player;
      if (var2 != null) {
         var2.getAbilities().flying = packet.isFlying();
         var2.getAbilities().creativeMode = packet.isCreativeMode();
         var2.getAbilities().invulnerable = packet.isInvulnerable();
         var2.getAbilities().allowFlying = packet.allowFlying();
         var2.getAbilities().setFlySpeed(packet.getFlySpeed());
         var2.getAbilities().setWalkSpeed(packet.getWalkSpeed());
      }
   }

   @Override
   public void onDeathMessage(DeathMessageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      BotPlayer var3 = this.player;
      if (var2 != null && var3 != null) {
         Entity var4 = var2.getEntityById(packet.playerId());
         if (var4 == var3) {
            var3.requestRespawn();
         }
      }
   }

   @Override
   public void onGameStateChange(GameStateChangeS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      Reason var3 = packet.getReason();
      float var4 = packet.getValue();
      int var5 = MathHelper.floor(var4 + 0.5F);
      if (var3 == GameStateChangeS2CPacket.RAIN_STARTED && var2 != null) {
         var2.getLevelProperties().setRaining(true);
         var2.setRainGradient(0.0F);
      } else if (var3 == GameStateChangeS2CPacket.RAIN_STOPPED && var2 != null) {
         var2.getLevelProperties().setRaining(false);
         var2.setRainGradient(1.0F);
      } else if (var3 == GameStateChangeS2CPacket.GAME_MODE_CHANGED) {
         BotInteractionManager var6 = this.interactionManager;
         if (var6 != null) {
            var6.setGameMode(GameMode.byIndex(var5));
         }
      } else if (var3 == GameStateChangeS2CPacket.GAME_WON) {
         this.sendPacket(new ClientStatusC2SPacket(Mode.PERFORM_RESPAWN));
      } else if (var3 == GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED && var2 != null) {
         var2.setRainGradient(var4);
      } else if (var3 == GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED && var2 != null) {
         var2.setThunderGradient(var4);
      } else if (var3 == GameStateChangeS2CPacket.INITIAL_CHUNKS_COMING) {
         this.initialChunksComing = true;
      }
   }

   @Override
   public void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.setTime(packet.time(), packet.timeOfDay(), packet.tickDayTime());
      }
   }

   @Override
   public void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.setSpawnPoint(packet.respawnData());
      }
   }

   @Override
   public void onDifficulty(DifficultyS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      if (this.worldProperties != null) {
         this.worldProperties.setDifficulty(packet.difficulty());
         this.worldProperties.setDifficultyLocked(packet.difficultyLocked());
      }
   }

   @Override
   public void onWorldBorderInitialize(WorldBorderInitializeS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         WorldBorder var3 = var2.getWorldBorder();
         var3.setCenter(packet.getCenterX(), packet.getCenterZ());
         long var4 = packet.getSizeLerpTime();
         if (var4 > 0L) {
            var3.interpolateSize(packet.getSize(), packet.getSizeLerpTarget(), var4, 0L);
         } else {
            var3.setSize(packet.getSizeLerpTarget());
         }

         var3.setMaxRadius(packet.getMaxRadius());
         var3.setWarningBlocks(packet.getWarningBlocks());
         var3.setWarningTime(packet.getWarningTime());
      }
   }

   @Override
   public void onWorldBorderCenterChanged(WorldBorderCenterChangedS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getWorldBorder().setCenter(packet.getCenterX(), packet.getCenterZ());
      }
   }

   @Override
   public void onWorldBorderInterpolateSize(WorldBorderInterpolateSizeS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getWorldBorder().interpolateSize(packet.getSize(), packet.getSizeLerpTarget(), packet.getSizeLerpTime(), 0L);
      }
   }

   @Override
   public void onWorldBorderSizeChanged(WorldBorderSizeChangedS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getWorldBorder().setSize(packet.getSizeLerpTarget());
      }
   }

   @Override
   public void onWorldBorderWarningBlocksChanged(WorldBorderWarningBlocksChangedS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getWorldBorder().setWarningBlocks(packet.getWarningBlocks());
      }
   }

   @Override
   public void onWorldBorderWarningTimeChanged(WorldBorderWarningTimeChangedS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getWorldBorder().setWarningTime(packet.getWarningTime());
      }
   }

   @Override
   public void onMapUpdate(MapUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         MapIdComponent var3 = packet.mapId();
         MapState var4 = var2.getMapState(var3);
         if (var4 == null) {
            var4 = MapState.of(packet.scale(), packet.locked(), var2.getRegistryKey());
            var2.putClientsideMapState(var3, var4);
         }

         packet.apply(var4);
      }
   }

   @Override
   public void onUpdateTickRate(UpdateTickRateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         TickManager var3 = var2.getTickManager();
         var3.setTickRate(packet.tickRate());
         var3.setFrozen(packet.isFrozen());
      }
   }

   @Override
   public void onTickStep(TickStepS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      BotWorld var2 = this.world;
      if (var2 != null) {
         var2.getTickManager().setStepTicks(packet.tickSteps());
      }
   }

   @Override
   public void onPlayerList(PlayerListS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());

      for (net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry var3 : packet.getPlayerAdditionEntries()) {
         BotPlayerListEntry var4 = new BotPlayerListEntry(Objects.requireNonNull(var3.profile()), false);
         this.playerListEntries.putIfAbsent(var3.profileId(), var4);
      }

      for (net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry var9 : packet.getEntries()) {
         PlayerListEntry var10 = this.playerListEntries.get(var9.profileId());
         if (var10 instanceof BotPlayerListEntry var5) {
            for (Action var7 : packet.getActions()) {
               this.handlePlayerListAction(var7, var9, var5);
            }
         } else {
            LOGGER.warn(
               "Bot {}: ignoring player info update for unknown player {} ({})", new Object[]{this.client.getName(), var9.profileId(), packet.getActions()}
            );
         }
      }
   }

   private void handlePlayerListAction(Action var1, net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry var2, BotPlayerListEntry var3) {
      BotPlayer var4 = this.player;
      switch (var1) {
         case INITIALIZE_CHAT:
            var3.resetBotSession(false);
            break;
         case UPDATE_GAME_MODE:
            if (var3.getGameMode() != var2.gameMode() && var4 != null && var4.getUuid().equals(var2.profileId())) {
               var4.onGameModeChanged(var2.gameMode());
            }

            var3.setBotGameMode(var2.gameMode());
            break;
         case UPDATE_LATENCY:
            var3.setBotLatency(var2.latency());
            break;
         case UPDATE_DISPLAY_NAME:
            var3.setDisplayName(var2.displayName());
            break;
         case UPDATE_HAT:
            var3.setShowHat(var2.showHat());
            break;
         case UPDATE_LIST_ORDER:
            var3.setListOrder(var2.listOrder());
         case UPDATE_LISTED:
      }
   }

   @Override
   public void onPlayerRemove(PlayerRemoveS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());

      for (UUID var3 : packet.profileIds()) {
         this.playerListEntries.remove(var3);
      }
   }

   @Override
   public void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      String var2 = packet.getName();
      synchronized (this.scoreboard) {
         if (packet.getMode() == 0) {
            this.scoreboard
               .addObjective(var2, ScoreboardCriterion.DUMMY, packet.getDisplayName(), packet.getType(), false, packet.getNumberFormat().orElse(null));
         } else {
            ScoreboardObjective var4 = this.scoreboard.getNullableObjective(var2);
            if (var4 != null) {
               if (packet.getMode() == 1) {
                  this.scoreboard.removeObjective(var4);
               } else if (packet.getMode() == 2) {
                  var4.setRenderType(packet.getType());
                  var4.setDisplayName(packet.getDisplayName());
                  var4.setNumberFormat(packet.getNumberFormat().orElse(null));
               }
            }
         }
      }
   }

   @Override
   public void onScoreboardScoreUpdate(ScoreboardScoreUpdateS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      String var2 = packet.objectiveName();
      ScoreHolder var3 = ScoreHolder.fromName(packet.scoreHolderName());
      synchronized (this.scoreboard) {
         ScoreboardObjective var5 = this.scoreboard.getNullableObjective(var2);
         if (var5 != null) {
            ScoreAccess var6 = this.scoreboard.getOrCreateScore(var3, var5, true);
            var6.setScore(packet.score());
            var6.setDisplayText(packet.display().orElse(null));
            var6.setNumberFormat(packet.numberFormat().orElse(null));
            return;
         }
      }

      LOGGER.warn("Bot {}: received packet for unknown scoreboard objective: {}", this.client.getName(), var2);
   }

   @Override
   public void onScoreboardScoreReset(ScoreboardScoreResetS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      String var2 = packet.objectiveName();
      ScoreHolder var3 = ScoreHolder.fromName(packet.scoreHolderName());
      synchronized (this.scoreboard) {
         if (var2 == null) {
            this.scoreboard.removeScores(var3);
         } else {
            ScoreboardObjective var5 = this.scoreboard.getNullableObjective(var2);
            if (var5 != null) {
               this.scoreboard.removeScore(var3, var5);
            }
         }
      }
   }

   @Override
   public void onScoreboardDisplay(ScoreboardDisplayS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      String var2 = packet.getName();
      synchronized (this.scoreboard) {
         ScoreboardObjective var4 = var2 == null ? null : this.scoreboard.getNullableObjective(var2);
         this.scoreboard.setObjectiveSlot(packet.getSlot(), var4);
      }
   }

   @Override
   public void onTeam(TeamS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      synchronized (this.scoreboard) {
         this.applyTeam(packet);
      }
   }

   private void applyTeam(TeamS2CPacket var1) {
      Operation var2 = var1.getTeamOperation();
      Team var3;
      if (var2 == Operation.ADD) {
         var3 = this.scoreboard.addTeam(var1.getTeamName());
      } else {
         var3 = this.scoreboard.getTeam(var1.getTeamName());
         if (var3 == null) {
            LOGGER.warn("Bot {}: received packet for unknown team {}", this.client.getName(), var1.getTeamName());
            return;
         }
      }

      var1.getTeam().ifPresent(var1x -> {
         var3.setDisplayName(var1x.getDisplayName());
         var3.setColor(var1x.getColor());
         var3.setFriendlyFlagsBitwise(var1x.getFriendlyFlagsBitwise());
         VisibilityRule var2x = var1x.getNameTagVisibilityRule();
         if (var2x != null) {
            var3.setNameTagVisibilityRule(var2x);
         }

         CollisionRule var3x = var1x.getCollisionRule();
         if (var3x != null) {
            var3.setCollisionRule(var3x);
         }

         var3.setPrefix(var1x.getPrefix());
         var3.setSuffix(var1x.getSuffix());
      });
      Operation var4 = var1.getPlayerListOperation();
      if (var4 == Operation.ADD) {
         for (String var6 : var1.getPlayerNames()) {
            this.scoreboard.addScoreHolderToTeam(var6, var3);
         }
      } else if (var4 == Operation.REMOVE) {
         for (String var8 : var1.getPlayerNames()) {
            this.scoreboard.removeScoreHolderFromTeam(var8, var3);
         }
      }

      if (var2 == Operation.REMOVE) {
         this.scoreboard.removeTeam(var3);
      }
   }

   @Override
   public void onSynchronizeRecipes(SynchronizeRecipesS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.recipeManager = new ClientRecipeManager(packet.itemSets(), packet.stonecutterRecipes());
   }

   @Override
   public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      var var2 = new ArrayList<>(packet.getGroups().size());
      packet.getGroups().forEach((var2x, var3) -> {
         if (SerializableRegistries.isSynced((RegistryKey<? extends Registry<?>>)var2x)) {
            var2.add(this.startTagReload((RegistryKey<? extends Registry<?>>)var2x, var3));
         }
      });
      var2.forEach(x -> ((PendingTagLoad)x).apply());
      this.fuelRegistry = FuelRegistry.createDefault(this.registries, this.enabledFeatures);
   }

   private <T> PendingTagLoad<T> startTagReload(
      RegistryKey<? extends Registry<? extends T>> var1, net.minecraft.registry.tag.TagPacketSerializer.Serialized var2
   ) {
      Registry var3 = this.registries.getOrThrow(var1);
      return var3.startTagReload(var2.toRegistryTags(var3));
   }

   @Override
   public void onGameMessage(GameMessageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.client.onChat(packet.content());
   }

   @Override
   public void onChatMessage(ChatMessageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      Object var2 = packet.unsignedContent() != null ? packet.unsignedContent() : Text.literal(packet.body().content());
      this.client.onChat(packet.serializedParameters().applyChatDecoration((Text)var2));
   }

   @Override
   public void onProfilelessChatMessage(ProfilelessChatMessageS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.client.onChat(packet.chatType().applyChatDecoration(packet.message()));
   }

   @Override
   public void onRemoveMessage(RemoveMessageS2CPacket packet) {
   }

   @Override
   public void onParticle(ParticleS2CPacket packet) {
   }

   @Override
   public void onPlaySound(PlaySoundS2CPacket packet) {
   }

   @Override
   public void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket packet) {
   }

   @Override
   public void onStopSound(StopSoundS2CPacket packet) {
   }

   @Override
   public void onSignEditorOpen(SignEditorOpenS2CPacket packet) {
   }

   @Override
   public void onOpenWrittenBook(OpenWrittenBookS2CPacket packet) {
   }

   @Override
   public void onCraftFailedResponse(CraftFailedResponseS2CPacket packet) {
   }

   @Override
   public void onStatistics(StatisticsS2CPacket packet) {
   }

   @Override
   public void onRecipeBookAdd(RecipeBookAddS2CPacket packet) {
   }

   @Override
   public void onRecipeBookRemove(RecipeBookRemoveS2CPacket packet) {
   }

   @Override
   public void onRecipeBookSettings(RecipeBookSettingsS2CPacket packet) {
   }

   @Override
   public void onAdvancements(AdvancementUpdateS2CPacket packet) {
   }

   @Override
   public void onSelectAdvancementTab(SelectAdvancementTabS2CPacket packet) {
   }

   @Override
   public void onCommandTree(CommandTreeS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.commandDispatcher = new CommandDispatcher(
         packet.getCommandTree(CommandRegistryAccess.of(this.registries, this.enabledFeatures), new NodeFactory<CommandSource>() {
            @Override
            public ArgumentBuilder<CommandSource, ?> literal(String name) {
               return LiteralArgumentBuilder.literal(name);
            }

            @Override
            public ArgumentBuilder<CommandSource, ?> argument(String name, ArgumentType<?> type, @Nullable Identifier suggestionProviderId) {
               return RequiredArgumentBuilder.argument(name, type);
            }

            @Override
            public ArgumentBuilder<CommandSource, ?> modifyNode(ArgumentBuilder<CommandSource, ?> arg, boolean disableExecution, boolean requireTrusted) {
               return arg;
            }
         })
      );
   }

   @Override
   public void onCommandSuggestions(CommandSuggestionsS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.commandSource.onCommandSuggestions(packet.id(), packet.getSuggestions());
   }

   @Override
   public void onChatSuggestions(ChatSuggestionsS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, this.client.getPacketApplyBatcher());
      this.commandSource.onChatSuggestions(packet.action(), packet.entries());
   }

   @Override
   public void onNbtQueryResponse(NbtQueryResponseS2CPacket packet) {
   }

   @Override
   public void onEndCombat(EndCombatS2CPacket packet) {
   }

   @Override
   public void onEnterCombat(EnterCombatS2CPacket packet) {
   }

   @Override
   public void onSetCameraEntity(SetCameraEntityS2CPacket packet) {
   }

   @Override
   public void onTitle(TitleS2CPacket packet) {
   }

   @Override
   public void onSubtitle(SubtitleS2CPacket packet) {
   }

   @Override
   public void onOverlayMessage(OverlayMessageS2CPacket packet) {
   }

   @Override
   public void onTitleFade(TitleFadeS2CPacket packet) {
   }

   @Override
   public void onTitleClear(ClearTitleS2CPacket packet) {
   }

   @Override
   public void onServerMetadata(ServerMetadataS2CPacket packet) {
   }

   @Override
   public void onPlayerListHeader(PlayerListHeaderS2CPacket packet) {
   }

   @Override
   public void onBossBar(BossBarS2CPacket packet) {
   }

   @Override
   public void onDebugSample(DebugSampleS2CPacket packet) {
   }

   @Override
   public void onPingResult(PingResultS2CPacket packet) {
   }

   @Override
   public void onOpenMountScreen(OpenMountScreenS2CPacket packet) {
   }

   @Override
   public void onTestInstanceBlockStatus(TestInstanceBlockStatusS2CPacket packet) {
   }

   @Override
   public void onWaypoint(WaypointS2CPacket packet) {
   }

   @Override
   public void onChunkValueDebug(ChunkValueDebugS2CPacket packet) {
   }

   @Override
   public void onBlockValueDebug(BlockValueDebugS2CPacket packet) {
   }

   @Override
   public void onEntityValueDebug(EntityValueDebugS2CPacket packet) {
   }

   @Override
   public void onEventDebug(EventDebugS2CPacket packet) {
   }

   @Override
   public void onGameTestHighlightPos(GameTestHighlightPosS2CPacket packet) {
   }

   @Override
   public void onShowDialog(ShowDialogS2CPacket packet) {
   }

   @Override
   public void onClearDialog(ClearDialogS2CPacket packet) {
   }
}

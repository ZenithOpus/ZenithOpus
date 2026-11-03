package org.zenith.utility.session;

import com.mojang.datafixers.util.Pair;
import java.util.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.world.chunk.ChunkStatus;
import org.zenith.utility.mixin.accessors.SessionPlayerListAccessor;

/** Creates a current world snapshot, rather than retaining an unbounded packet history. Game thread only. */
final class SessionSnapshot {
    private static final Set<String> INITIAL_STATE = Set.of(
        "commands", "update_recipes", "update_tags", "difficulty", "server_data", "tab_list",
        "set_simulation_distance", "set_chunk_cache_radius", "custom_report_details", "server_links"
    );
    private final Map<String, Packet<?>> initial = new LinkedHashMap<>();
    private final Map<Integer, Integer> entityData = new HashMap<>();
    private final Map<net.minecraft.recipe.NetworkRecipeId, RecipeBookAddS2CPacket.Entry> recipes = new LinkedHashMap<>();
    private GameJoinS2CPacket join;
    private CommonPlayerSpawnInfo spawn;
    private int teleport = -1;

    void clear() { initial.clear(); entityData.clear(); recipes.clear(); join = null; spawn = null; }
    boolean isReady() { return join != null; }

    void record(Packet<?> packet) {
        if (packet instanceof GameJoinS2CPacket p) {
            clear();
            join = p;
            spawn = p.commonPlayerSpawnInfo();
        } else if (packet instanceof PlayerRespawnS2CPacket p) {
            spawn = p.commonPlayerSpawnInfo();
            entityData.clear();
        } else if (packet instanceof EntitySpawnS2CPacket p) entityData.put(p.getEntityId(), p.getEntityData());
        else if (packet instanceof EntitiesDestroyS2CPacket p) p.getEntityIds().forEach(entityData::remove);
        else if (packet instanceof RecipeBookAddS2CPacket p) {
            if (p.replace()) recipes.clear();
            for (var recipe : p.entries()) recipes.put(recipe.contents().id(), recipe);
        } else if (packet instanceof RecipeBookRemoveS2CPacket p) p.recipes().forEach(recipes::remove);
        else if (INITIAL_STATE.contains(packet.getPacketType().id().getPath())) initial.put(packet.getPacketType().id().getPath(), packet);
    }

    void sendTo(SessionGuest guest, MinecraftClient mc) {
        var world = mc.world;
        var player = mc.player;
        var network = mc.getNetworkHandler();
        CommonPlayerSpawnInfo currentSpawn = new CommonPlayerSpawnInfo(spawn.dimensionType(), world.getRegistryKey(), spawn.seed(),
            mc.interactionManager.getCurrentGameMode(), spawn.lastGameMode(), spawn.isDebug(), spawn.isFlat(),
            spawn.lastDeathLocation(), spawn.portalCooldown(), spawn.seaLevel());
        guest.send(new GameJoinS2CPacket(player.getId(), join.hardcore(), join.dimensionIds(), join.maxPlayers(),
            join.viewDistance(), join.simulationDistance(), join.reducedDebugInfo(), join.showDeathScreen(),
            join.doLimitedCrafting(), currentSpawn, false));
        initial.values().forEach(guest::send);
        guest.send(new RecipeBookSettingsS2CPacket(player.getRecipeBook().getOptions()));
        guest.send(new RecipeBookAddS2CPacket(List.copyOf(recipes.values()), true));

        PlayerListS2CPacket players = new PlayerListS2CPacket(EnumSet.allOf(PlayerListS2CPacket.Action.class), List.of());
        List<PlayerListS2CPacket.Entry> entries = network.getPlayerList().stream().map(p -> new PlayerListS2CPacket.Entry(
            p.getProfile().id(), p.getProfile(), true, p.getLatency(), p.getGameMode(), p.getDisplayName(), p.shouldShowHat(),
            p.getListOrder(), null)).toList();
        ((SessionPlayerListAccessor)(Object)players).zenith$setEntries(entries);
        guest.send(players);
        guest.send(new WorldBorderInitializeS2CPacket(world.getWorldBorder()));
        guest.send(new WorldTimeUpdateS2CPacket(world.getTime(), world.getTimeOfDay(), true));
        guest.send(new GameStateChangeS2CPacket(world.isRaining() ? GameStateChangeS2CPacket.RAIN_STARTED : GameStateChangeS2CPacket.RAIN_STOPPED, 0));
        guest.send(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED, world.getRainGradient(1)));
        guest.send(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, world.getThunderGradient(1)));
        guest.send(new PlayerAbilitiesS2CPacket(player.getAbilities()));
        guest.send(new HealthUpdateS2CPacket(player.getHealth(), player.getHungerManager().getFoodLevel(), player.getHungerManager().getSaturationLevel()));
        guest.send(new ExperienceBarUpdateS2CPacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
        guest.send(new UpdateSelectedSlotS2CPacket(player.getInventory().getSelectedSlot()));
        guest.send(new InventoryS2CPacket(0, player.playerScreenHandler.getRevision(),
            player.playerScreenHandler.getStacks().stream().map(stack -> stack.copy()).toList(), player.playerScreenHandler.getCursorStack().copy()));
        guest.send(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.INITIAL_CHUNKS_COMING, 0));
        int cx = player.getChunkPos().x;
        int cz = player.getChunkPos().z;
        guest.send(new ChunkRenderDistanceCenterS2CPacket(cx, cz));
        guest.send(StartChunkSendS2CPacket.INSTANCE);
        int chunks = 0;
        int radius = Math.min(32, join.viewDistance() + 3);
        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                var chunk = world.getChunkManager().getChunk(x, z, ChunkStatus.FULL, false);
                if (chunk != null) {
                    guest.send(new ChunkDataS2CPacket(chunk, world.getLightingProvider(), null, null));
                    chunks++;
                }
            }
        }
        guest.send(new ChunkSentS2CPacket(chunks));
        for (Entity entity : world.getEntities()) {
            if (entity == player) continue;
            guest.send(new EntitySpawnS2CPacket(entity.getId(), entity.getUuid(), entity.getX(), entity.getY(), entity.getZ(),
                entity.getPitch(), entity.getYaw(), entity.getType(), entityData.getOrDefault(entity.getId(), 0),
                entity.getVelocity(), entity.getHeadYaw()));
            sendEntityState(guest, entity);
        }
        sendEntityState(guest, player);
        for (Entity entity : world.getEntities()) if (entity.hasPassengers()) guest.send(new EntityPassengersSetS2CPacket(entity));
        var scoreboard = world.getScoreboard();
        scoreboard.getTeams().forEach(team -> guest.send(TeamS2CPacket.updateTeam(team, true)));
        scoreboard.getObjectives().forEach(objective -> {
            guest.send(new ScoreboardObjectiveUpdateS2CPacket(objective, 0));
            scoreboard.getScoreboardEntries(objective).forEach(score -> guest.send(new ScoreboardScoreUpdateS2CPacket(
                score.owner(), objective.getName(), score.value(), Optional.ofNullable(score.display()), Optional.ofNullable(score.numberFormatOverride()))));
        });
        for (ScoreboardDisplaySlot slot : ScoreboardDisplaySlot.values()) {
            var objective = scoreboard.getObjectiveForSlot(slot);
            if (objective != null) guest.send(new ScoreboardDisplayS2CPacket(slot, objective));
        }
        sendPosition(guest, mc);
    }

    private void sendEntityState(SessionGuest guest, Entity entity) {
        var tracked = entity.getDataTracker().getChangedEntries();
        if (tracked != null) guest.send(new EntityTrackerUpdateS2CPacket(entity.getId(), tracked));
        if (entity instanceof LivingEntity living) {
            guest.send(new EntityAttributesS2CPacket(entity.getId(), living.getAttributes().getAttributesToSend()));
            List<Pair<EquipmentSlot, net.minecraft.item.ItemStack>> equipment = new ArrayList<>();
            for (EquipmentSlot slot : EquipmentSlot.values()) equipment.add(Pair.of(slot, living.getEquippedStack(slot).copy()));
            guest.send(new EntityEquipmentUpdateS2CPacket(entity.getId(), equipment));
            living.getStatusEffects().forEach(effect -> guest.send(new EntityStatusEffectS2CPacket(entity.getId(), effect, false)));
        }
    }

    void sendPosition(SessionGuest guest, MinecraftClient mc) {
        if (mc.player == null) return;
        int id = teleport--;
        guest.awaitTeleport(id);
        guest.send(PlayerPositionLookS2CPacket.of(id, EntityPosition.fromEntity(mc.player), Set.of()));
    }
}

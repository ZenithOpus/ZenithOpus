package org.zenith.utility.session;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.*;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.common.*;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.network.state.*;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.client.screens.session.SharedSessionScreen;

/** Keeps the original authenticated connection alive; LAN guests never authenticate upstream. */
public final class SharedSession {
    public static final Logger LOGGER = LoggerFactory.getLogger("ZenithOpus/Session");
    private static final SharedSession INSTANCE = new SharedSession();
    private static final Set<String> GAMEPLAY = Set.of(
        "move_player_pos", "move_player_pos_rot", "move_player_rot", "move_player_status_only",
        "player_input", "player_command", "player_action", "use_item", "use_item_on", "interact",
        "swing", "set_carried_item", "container_click", "container_button_click", "container_close",
        "set_creative_mode_slot", "player_abilities", "move_vehicle", "paddle_boat", "pick_item_from_block",
        "pick_item_from_entity", "rename_item", "select_trade", "set_beacon", "sign_update", "edit_book",
        "place_recipe", "recipe_book_change_settings", "recipe_book_seen_recipe", "client_command"
    );
    private final Set<SessionGuest> guests = ConcurrentHashMap.newKeySet();
    private final List<byte[]> configuration = new ArrayList<>();
    private final SessionSnapshot snapshot = new SessionSnapshot();
    private final SessionTray tray = new SessionTray();
    private volatile ClientConnection source;
    private volatile ServerSocket listener;
    private volatile SessionGuest controller;
    private volatile String address = "";
    private volatile String secret = "";
    private NetworkState<?> playInbound;
    private NetworkState<?> playOutbound;
    private boolean forwarding;
    private boolean hidden;
    private boolean messages = true;
    private volatile boolean configReady;
    private int configBytes;
    private String error = "";
    private boolean alwaysOpen;
    private boolean autoAttempted;
    private boolean internetEnabled = true;
    private PublicSessionTunnel tunnel;

    private SharedSession() {
        try {
            Path file = preferencesFile();
            if (Files.exists(file)) {
                Properties properties = new Properties();
                try (var input = Files.newInputStream(file)) { properties.load(input); }
                alwaysOpen = Boolean.parseBoolean(properties.getProperty("alwaysOpen", "false"));
                messages = Boolean.parseBoolean(properties.getProperty("messages", "true"));
                internetEnabled = Boolean.parseBoolean(properties.getProperty("internetEnabled", alwaysOpen ? "false" : "true"));
            }
        } catch (IOException e) { LOGGER.debug("Cannot load session preferences", e); }
    }
    public static SharedSession getInstance() { return INSTANCE; }
    MinecraftClient client() { return MinecraftClient.getInstance(); }
    public boolean isActive() { return listener != null && !listener.isClosed(); }
    public boolean isControlled() { return controller != null && controller.open && controller.playing; }
    public boolean isHidden() { return hidden; }
    public String address() { return tunnel == null ? address : tunnel.address(); }
    public String localAddress() { return address; }
    public boolean internetEnabled() { return internetEnabled; }
    public boolean isInternetSession() { return tunnel != null; }
    public boolean isGettingPublicAddress() { return tunnel != null && tunnel.state() == PublicSessionTunnel.State.CONNECTING; }
    public boolean hasTunnelFailed() { return tunnel != null && tunnel.state() == PublicSessionTunnel.State.FAILED; }
    public void setInternetEnabled(boolean value) { internetEnabled = value; savePreferences(); }
    public String takeCommand() { return "/" + secret + " take"; }
    public String error() { return error; }
    public int guestCount() { return (int)guests.stream().filter(g -> g.open && g.playing).count(); }
    public boolean showMessages() { return messages; }
    public void setShowMessages(boolean value) { messages = value; savePreferences(); }
    public boolean alwaysOpen() { return alwaysOpen; }
    public void setAlwaysOpen(boolean value) { alwaysOpen = value; autoAttempted = true; savePreferences(); }
    private Path preferencesFile() { return client().runDirectory.toPath().resolve("config/zenithopus-session.properties"); }
    private void savePreferences() {
        try {
            Path file = preferencesFile();
            Files.createDirectories(file.getParent());
            Properties properties = new Properties();
            properties.setProperty("alwaysOpen", Boolean.toString(alwaysOpen));
            properties.setProperty("messages", Boolean.toString(messages));
            properties.setProperty("internetEnabled", Boolean.toString(internetEnabled));
            try (var output = Files.newOutputStream(file)) { properties.store(output, "ZenithOpus shared session"); }
        } catch (IOException e) { LOGGER.warn("Cannot save session preferences", e); }
    }
    GameProfile profile() { return client().player.getGameProfile(); }
    NetworkState<?> playInbound() { return playInbound; }
    NetworkState<?> playOutbound() { return playOutbound; }

    public void open(Screen parent) { client().setScreen(new SharedSessionScreen(parent)); }

    public boolean start() {
        MinecraftClient mc = client();
        error = "";
        if (isActive()) return true;
        if (mc.world == null || mc.player == null || mc.getNetworkHandler() == null || mc.isInSingleplayer()) {
            error = "Сначала зайди на сервер Minecraft.";
            return false;
        }
        if (source != mc.getNetworkHandler().getConnection() || !configReady || !snapshot.isReady()) {
            error = "Перезайди на сервер: нужно сохранить данные подключения.";
            return false;
        }
        try {
            playOutbound = PlayStateFactories.S2C.bind(RegistryByteBuf.makeFactory(mc.world.getRegistryManager()));
            playInbound = PlayStateFactories.C2S.bind(RegistryByteBuf.makeFactory(mc.world.getRegistryManager()),
                () -> mc.player != null && mc.player.isCreative());
            byte[] random = new byte[12];
            new SecureRandom().nextBytes(random);
            secret = "zo" + HexFormat.of().formatHex(random);
            ServerSocket server = new ServerSocket();
            server.setReuseAddress(true);
            server.bind(new InetSocketAddress(0), 8);
            listener = server;
            address = lanAddress() + ":" + server.getLocalPort();
            Thread acceptor = new Thread(() -> acceptLoop(server), "ZenithOpus-session-LAN");
            acceptor.setDaemon(true);
            acceptor.start();
            autoAttempted = true;
            if (internetEnabled) retryPublicTunnel();
            else notifyHost("Сессия открыта: " + address + ". Второй ПК: Minecraft 1.21.11.");
            return true;
        } catch (IOException | RuntimeException e) {
            stop();
            error = "Не удалось открыть сессию: " + e.getMessage();
            LOGGER.warn("Cannot start LAN session", e);
            return false;
        }
    }

    public void retryPublicTunnel() {
        if (!isActive()) return;
        PublicSessionTunnel previous = tunnel;
        tunnel = null;
        if (previous != null) previous.close();
        error = "";
        PublicSessionTunnel created = new PublicSessionTunnel(listener.getLocalPort());
        tunnel = created;
        notifyHost("Подключаем интернет-сессию. Получаем публичный адрес…");
        created.start().whenComplete((publicAddress, failure) -> client().execute(() -> {
            if (tunnel != created || !isActive()) return;
            if (failure == null) notifyHost("Интернет-сессия открыта: " + publicAddress + ". Второй ПК: Minecraft 1.21.11.");
            else tunnelFailed(created);
        }));
    }

    public void useLocalNetwork() {
        PublicSessionTunnel previous = tunnel;
        tunnel = null;
        if (previous != null) previous.close();
        error = "";
        setInternetEnabled(false);
        notifyHost("Локальная сессия: " + address);
    }

    private void tunnelFailed(PublicSessionTunnel failed) {
        if (!error.isEmpty()) return;
        error = "Не удалось подключить интернет-сессию. Повтори попытку или выбери локальную сеть.";
        LOGGER.warn("Public session tunnel failed", failed.failure());
        notifyHost(error);
    }

    private void acceptLoop(ServerSocket server) {
        while (!server.isClosed()) {
            try {
                Socket socket = server.accept();
                InetAddress peer = socket.getInetAddress();
                if (!(peer.isLoopbackAddress() || peer.isSiteLocalAddress() || peer.isLinkLocalAddress()) || guests.size() >= 4) {
                    socket.close();
                    continue;
                }
                SessionGuest guest = new SessionGuest(this, socket);
                guests.add(guest);
                Thread reader = new Thread(guest::run, "ZenithOpus-session-reader");
                reader.setDaemon(true);
                reader.start();
            } catch (IOException e) {
                if (!server.isClosed()) LOGGER.warn("LAN session accept failed", e);
            }
        }
    }

    public void stop() {
        PublicSessionTunnel currentTunnel = tunnel;
        tunnel = null;
        if (currentTunnel != null) currentTunnel.close();
        ServerSocket server = listener;
        listener = null;
        if (server != null) try { server.close(); } catch (IOException ignored) {}
        for (SessionGuest guest : guests) guest.close();
        guests.clear();
        controller = null;
        secret = "";
        address = "";
        if (hidden) restoreWindow();
    }

    /** Called before normal packet handling, including the initial configuration phase. */
    public void incoming(ClientConnection connection, Packet<?> packet) {
        if (connection.getSide() != NetworkSide.CLIENTBOUND || connection.isLocal()) return;
        if (packet instanceof LoginSuccessS2CPacket) {
            source = connection;
            synchronized (configuration) {
                configuration.clear();
                configReady = false;
                configBytes = 0;
            }
            client().execute(() -> { stop(); snapshot.clear(); autoAttempted = false; });
            return;
        }
        if (source != connection || connection.getPacketListener() == null) return;
        if (packet instanceof EnterReconfigurationS2CPacket) {
            synchronized (configuration) {
                configuration.clear();
                configReady = false;
                configBytes = 0;
            }
        }
        if (connection.getPacketListener().getPhase() == NetworkPhase.CONFIGURATION) {
            // A vanilla peer shares the same built-in packs, so the upstream registry stream can be reused.
            if (packet instanceof KeepAliveS2CPacket || packet instanceof CommonPingS2CPacket
                || packet instanceof CustomPayloadS2CPacket || packet instanceof CookieRequestS2CPacket || packet instanceof StoreCookieS2CPacket) return;
            try {
                byte[] encoded = SessionWire.encode(ConfigurationStates.S2C, packet);
                synchronized (configuration) {
                    configBytes += encoded.length;
                    if (configBytes > 32 * 1024 * 1024) {
                        configuration.clear();
                        configReady = false;
                        return;
                    }
                    configuration.add(encoded);
                    if (packet instanceof ReadyS2CPacket) configReady = true;
                }
            } catch (RuntimeException e) {
                LOGGER.warn("Cannot capture session configuration", e);
            }
        }
    }

    List<byte[]> configuration() {
        synchronized (configuration) { return configReady ? List.copyOf(configuration) : List.of(); }
    }

    /** Runs on the game thread after the host has applied a packet. */
    public void applied(Packet<?> packet) {
        if (source == null || packet instanceof BundleS2CPacket) return;
        snapshot.record(packet);
        if (!isActive()) return;
        if (packet instanceof EnterReconfigurationS2CPacket || packet instanceof ServerTransferS2CPacket) {
            stop();
            notifyHost("Сервер изменил подключение. Создай сессию заново после загрузки.");
            return;
        }
        if (packet instanceof KeepAliveS2CPacket || packet instanceof CommonPingS2CPacket
            || packet instanceof CustomPayloadS2CPacket || packet instanceof CookieRequestS2CPacket || packet instanceof StoreCookieS2CPacket
            || packet instanceof StartChunkSendS2CPacket || packet instanceof ChunkSentS2CPacket) return;
        // Signed upstream messages belong to the host's chat session; display them as system messages to guests.
        if (packet instanceof ChatMessageS2CPacket chat) {
            broadcast(new GameMessageS2CPacket(Text.literal(chat.body().content()), false));
            return;
        }
        // The host already acknowledged the upstream teleport. Wait for the controller to
        // acknowledge its own absolute position before accepting more predicted movement.
        if (packet instanceof PlayerPositionLookS2CPacket) {
            if (isControlled()) snapshot.sendPosition(controller, client());
            return;
        }
        broadcast(packet);
        if (packet instanceof PlayerRespawnS2CPacket) {
            for (SessionGuest guest : guests) if (guest.open && guest.playing && guest != controller) {
                snapshot.sendPosition(guest, client());
                guest.view.start(guest, client());
            }
        }
    }

    void join(SessionGuest guest) {
        if (!isActive() || !guest.open || client().world == null || client().player == null) { guest.close(); return; }
        try {
            snapshot.sendTo(guest, client());
            guest.view.start(guest, client());
            guest.playing = true;
            guest.send(new GameMessageS2CPacket(Text.literal("ZenithOpus: режим просмотра. Используй секретную команду из окна сессии, чтобы взять управление."), false));
            notifyHost(guest.name() + " подключился к сессии (просмотр).");
        } catch (RuntimeException e) {
            LOGGER.warn("Cannot synchronize session guest", e);
            guest.close();
        }
    }

    void left(SessionGuest guest) {
        guests.remove(guest);
        if (controller == guest) {
            controller = null;
            notifyHost("Второй ПК отключился. Управление возвращено на основной ПК.");
        }
    }

    void receive(SessionGuest guest, Packet<?> packet) {
        if (!guest.playing) return;
        if (guest.reserveTask()) client().execute(() -> {
            try { receiveOnGameThread(guest, packet); }
            finally { guest.completeTask(); }
        });
    }

    private void receiveOnGameThread(SessionGuest guest, Packet<?> packet) {
        if (!isActive() || !guest.open || client().player == null || client().getNetworkHandler() == null) return;
        if (packet instanceof TeleportConfirmC2SPacket confirm) {
            guest.confirmTeleport(confirm.getTeleportId());
            return;
        }
        String command = packet instanceof CommandExecutionC2SPacket p ? p.command()
            : packet instanceof ChatCommandSignedC2SPacket p ? p.command() : null;
        if (command != null && command.startsWith(secret + " ")) {
            switch (command.substring(secret.length() + 1)) {
                case "take" -> {
                    if (controller == guest) return;
                    forwarding = true;
                    try {
                        if (client().player.currentScreenHandler != client().player.playerScreenHandler) client().player.closeHandledScreen();
                    } finally { forwarding = false; }
                    SessionGuest previous = controller;
                    controller = guest;
                    if (previous != null && previous.open && previous.playing) {
                        snapshot.sendPosition(previous, client());
                        previous.view.start(previous, client());
                    }
                    guest.view.stop(guest, client());
                    snapshot.sendPosition(guest, client());
                    client().options.forwardKey.setPressed(false);
                    client().options.backKey.setPressed(false);
                    client().options.leftKey.setPressed(false);
                    client().options.rightKey.setPressed(false);
                    guest.send(new GameMessageS2CPacket(Text.literal("Управление у тебя. /" + secret + " release — вернуть на основной ПК."), false));
                    notifyHost("Управление передано: " + guest.name() + ". Вернуть можно в окне сессии.");
                }
                case "release" -> { if (controller == guest) reclaim(); }
                case "stop" -> { if (controller == guest) stop(); }
                default -> { if (guest.wrongCommand()) guest.close(); }
            }
            return;
        }
        if (command != null && command.startsWith("zo") && command.endsWith(" take")) {
            if (guest.wrongCommand()) guest.close();
            return;
        }
        if (controller != guest || !guest.canControl()) return;
        forwarding = true;
        try {
            if (packet instanceof ChatMessageC2SPacket chat) client().getNetworkHandler().sendChatMessage(chat.chatMessage());
            else if (command != null) client().getNetworkHandler().sendChatCommand(command);
            else if (isGameplay(packet)) {
                if (packet instanceof PlayerMoveC2SPacket move) {
                    var player = client().player;
                    player.setPosition(move.getX(player.getX()), move.getY(player.getY()), move.getZ(player.getZ()));
                    player.setYaw(move.getYaw(player.getYaw()));
                    player.setPitch(move.getPitch(player.getPitch()));
                    player.setOnGround(move.isOnGround());
                } else if (packet instanceof UpdateSelectedSlotC2SPacket slot) {
                    client().player.getInventory().setSelectedSlot(slot.getSelectedSlot());
                } else if (packet instanceof VehicleMoveC2SPacket move && client().player.hasVehicle()) {
                    var vehicle = client().player.getRootVehicle();
                    vehicle.setPosition(move.position());
                    vehicle.setYaw(move.yaw());
                    vehicle.setPitch(move.pitch());
                    vehicle.setOnGround(move.onGround());
                } else if (packet instanceof CloseHandledScreenC2SPacket) {
                    client().player.currentScreenHandler = client().player.playerScreenHandler;
                    client().setScreen(null);
                }
                applyPrediction(packet);
                source.send(packet);
            }
        } finally { forwarding = false; }
    }

    public void reclaim() {
        SessionGuest previous = controller;
        controller = null;
        if (client().player != null) client().player.setVelocity(net.minecraft.util.math.Vec3d.ZERO);
        if (previous != null && previous.open && previous.playing && client().player != null) {
            snapshot.sendPosition(previous, client());
            previous.view.start(previous, client());
        }
        broadcast(new GameMessageS2CPacket(Text.literal("Управление возвращено на основной ПК."), false));
    }

    /** The server assumes the controlling vanilla client already predicted its own inventory clicks. */
    private void applyPrediction(Packet<?> packet) {
        var player = client().player;
        if (packet instanceof ClickSlotC2SPacket click) {
            var handler = player.currentScreenHandler;
            if (handler.syncId == click.syncId() && (handler.isValid(click.slot()) || click.slot() == -999)) {
                handler.onSlotClick(click.slot(), click.button(), click.actionType(), player);
            }
        } else if (packet instanceof CreativeInventoryActionC2SPacket creative) {
            if (creative.slot() >= 1 && creative.slot() < player.playerScreenHandler.slots.size()) {
                player.playerScreenHandler.getSlot(creative.slot()).setStack(creative.stack().copy());
            }
        } else if (packet instanceof UpdatePlayerAbilitiesC2SPacket abilities) {
            player.getAbilities().flying = abilities.isFlying();
        } else if (packet instanceof PlayerActionC2SPacket action) {
            var inventory = player.getInventory();
            switch (action.getAction()) {
                case DROP_ITEM -> inventory.removeStack(inventory.getSelectedSlot(), 1);
                case DROP_ALL_ITEMS -> inventory.removeStack(inventory.getSelectedSlot());
                case SWAP_ITEM_WITH_OFFHAND -> {
                    var main = player.getMainHandStack().copy();
                    var off = player.getOffHandStack().copy();
                    inventory.setStack(inventory.getSelectedSlot(), off);
                    inventory.setStack(40, main);
                }
                default -> { }
            }
        }
    }

    public boolean suppressOutgoing(ClientConnection connection, Packet<?> packet) {
        return isControlled() && connection == source && !forwarding && isGameplay(packet);
    }

    private static boolean isGameplay(Packet<?> packet) {
        return GAMEPLAY.contains(packet.getPacketType().id().getPath());
    }

    private void broadcast(Packet<?> packet) {
        byte[] encoded = null;
        try {
            for (SessionGuest guest : guests) if (guest.open && guest.playing) {
                Packet<?> adapted = guest.view.adapt(packet, client());
                if (adapted == null) continue;
                if (adapted != packet) guest.send(adapted);
                else {
                    if (encoded == null) encoded = SessionWire.encode(playOutbound, packet);
                    guest.sendRaw(encoded);
                }
            }
        } catch (RuntimeException e) { LOGGER.debug("Session packet not forwarded: {}", packet.getPacketType(), e); }
    }

    public void tick() {
        if (alwaysOpen && !autoAttempted && !isActive() && client().world != null && snapshot.isReady() && configReady) {
            autoAttempted = true;
            start();
        }
        if (!isActive()) return;
        if (hasTunnelFailed()) tunnelFailed(tunnel);
        if (source == null || !source.isOpen() || client().world == null) { stop(); return; }
        for (SessionGuest guest : guests) {
            guest.tick();
            if (guest.playing && guest.open && guest != controller) {
                guest.view.tick(guest, client());
                if (guest.view.needsBodySync(client())) snapshot.sendPosition(guest, client());
            }
        }
    }

    public boolean keepInBackground() {
        if (!isActive() || source == null || !source.isOpen()) return false;
        hidden = true;
        client().setScreen(null);
        client().mouse.unlockCursor();
        long window = client().getWindow().getHandle();
        GLFW.glfwSetWindowShouldClose(window, false);
        if (tray.show(this)) GLFW.glfwHideWindow(window);
        else GLFW.glfwIconifyWindow(window);
        return true;
    }

    public void restoreWindow() {
        hidden = false;
        tray.remove();
        long window = client().getWindow().getHandle();
        GLFW.glfwShowWindow(window);
        GLFW.glfwRestoreWindow(window);
        GLFW.glfwFocusWindow(window);
    }

    private void notifyHost(String message) {
        if (messages && client().inGameHud != null) client().inGameHud.getChatHud().addMessage(Text.literal("§9[ZenithOpus] §r" + message));
    }

    private static String lanAddress() throws SocketException {
        List<String> addresses = new ArrayList<>();
        for (NetworkInterface network : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (!network.isUp() || network.isLoopback()) continue;
            for (InetAddress address : Collections.list(network.getInetAddresses())) {
                if (address instanceof Inet4Address && address.isSiteLocalAddress()) addresses.add(address.getHostAddress());
            }
        }
        // Prefer ordinary home LAN addresses over common VPN/virtual adapter ranges.
        addresses.sort(Comparator.comparingInt(a -> a.startsWith("192.168.") ? 0 : a.startsWith("10.") ? 1 : 2));
        return addresses.isEmpty() ? "127.0.0.1" : addresses.getFirst();
    }
}

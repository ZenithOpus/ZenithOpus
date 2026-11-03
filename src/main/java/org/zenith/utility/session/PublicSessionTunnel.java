package org.zenith.utility.session;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.incubator.codec.quic.*;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.io.IOException;
import java.net.IDN;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Embedded QUIClime client for the public e4mc relay. The original game connection stays on this PC.
 * Wire protocol: https://github.com/vgskye/e4mc-quiclime (MIT/Apache-2.0).
 * No account, subprocess, executable download, UPnP, or router configuration is involved.
 */
public final class PublicSessionTunnel implements AutoCloseable {
    public enum State { CONNECTING, READY, FAILED, CLOSED }
    private final int localPort;
    private final EventLoopGroup group = new NioEventLoopGroup(1, new DefaultThreadFactory("ZenithOpus-tunnel", true));
    private final ChannelGroup channels = new DefaultChannelGroup("ZenithOpus-tunnel", GlobalEventExecutor.INSTANCE, true);
    private final AtomicBoolean started = new AtomicBoolean();
    private final AtomicBoolean closed = new AtomicBoolean();
    private final AtomicInteger streams = new AtomicInteger();
    private final CompletableFuture<String> ready = new CompletableFuture<>();
    private volatile State state = State.CONNECTING;
    private volatile String domain = "";
    private volatile Throwable failure;

    public PublicSessionTunnel(int localPort) {
        if (localPort < 1 || localPort > 65535) throw new IllegalArgumentException("Invalid session port");
        this.localPort = localPort;
    }

    public State state() { return state; }
    public String address() { return state == State.READY ? domain : ""; }
    public Throwable failure() { return failure; }

    public CompletableFuture<String> start() {
        if (started.compareAndSet(false, true) && !closed.get()) {
            group.schedule(() -> {
                if (!ready.isDone()) fail(new TimeoutException("Public address was not assigned in time"));
            }, 30, TimeUnit.SECONDS);
            Thread startup = new Thread(this::connect, "ZenithOpus-tunnel-start");
            startup.setDaemon(true);
            startup.start();
        }
        return ready;
    }

    private void connect() {
        try {
            BundledQuicNative.load();
            JsonObject relay;
            try (HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build()) {
                var request = HttpRequest.newBuilder(URI.create("https://broker.e4mc.link/getBestRelay"))
                    .timeout(Duration.ofSeconds(10)).header("Accept", "application/json").GET().build();
                var response = http.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200 || response.body().length() > 8192) throw new IOException("Relay discovery failed");
                relay = JsonParser.parseString(response.body()).getAsJsonObject();
            }
            if (closed.get()) return;
            String host = validateDomain(relay.get("host").getAsString());
            int port = relay.get("port").getAsInt();
            if (port < 1 || port > 65535) throw new IOException("Invalid relay port");
            QuicSslContext tls = QuicSslContextBuilder.forClient().trustManager(new RelayTrustManager(host)).applicationProtocols("quiclime").build();
            var codec = new QuicClientCodecBuilder().sslContext(tls)
                .sslEngineProvider(channel -> tls.newEngine(channel.alloc(), host, port))
                .initialMaxStreamsBidirectional(8).maxIdleTimeout(20, TimeUnit.SECONDS)
                .initialMaxData(16 * 1024 * 1024L)
                .initialMaxStreamDataBidirectionalRemote(1024 * 1024L)
                .initialMaxStreamDataBidirectionalLocal(65536)
                .initialMaxStreamDataUnidirectional(0).build();
            var bound = new Bootstrap().group(group).channel(NioDatagramChannel.class).handler(codec).bind(0);
            channels.add(bound.channel());
            bound.addListener((ChannelFutureListener) boundResult -> {
                if (!boundResult.isSuccess()) { fail(boundResult.cause()); return; }
                if (closed.get()) return;
                var connecting = QuicChannel.newBootstrap(boundResult.channel())
                    .streamHandler(new ChannelInitializer<QuicStreamChannel>() {
                        @Override protected void initChannel(QuicStreamChannel channel) { bridge(channel); }
                    })
                    .handler(new ChannelInboundHandlerAdapter() {
                        @Override public void exceptionCaught(ChannelHandlerContext context, Throwable error) { fail(error); }
                        @Override public void channelInactive(ChannelHandlerContext context) {
                            if (!closed.get()) fail(new IOException("Relay connection closed"));
                        }
                    })
                    .remoteAddress(new InetSocketAddress(host, port)).connect();
                connecting.addListener(result -> {
                    if (!result.isSuccess()) { fail(result.cause()); return; }
                    QuicChannel connection = (QuicChannel) result.getNow();
                    channels.add(connection);
                    if (closed.get()) return;
                    connection.createStream(QuicStreamType.BIDIRECTIONAL, new ChannelInitializer<QuicStreamChannel>() {
                        @Override protected void initChannel(QuicStreamChannel channel) {
                            channels.add(channel);
                            channel.pipeline().addLast(new ControlCodec(), new SimpleChannelInboundHandler<JsonObject>() {
                                @Override protected void channelRead0(ChannelHandlerContext context, JsonObject message) {
                                    if ("domain_assignment_complete".equals(message.get("kind").getAsString())) {
                                        if (closed.get()) return;
                                        domain = validateDomain(message.get("domain").getAsString());
                                        state = State.READY;
                                        ready.complete(domain);
                                    }
                                }
                                @Override public void exceptionCaught(ChannelHandlerContext context, Throwable error) { fail(error); }
                                @Override public void channelInactive(ChannelHandlerContext context) {
                                    if (!closed.get()) fail(new IOException("Relay control channel closed"));
                                }
                            });
                        }
                    }).addListener(controlResult -> {
                        if (!controlResult.isSuccess()) { fail(controlResult.cause()); return; }
                        JsonObject request = new JsonObject();
                        request.addProperty("kind", "request_domain_assignment");
                        ((QuicStreamChannel) controlResult.getNow()).writeAndFlush(request)
                            .addListener((ChannelFutureListener) sent -> { if (!sent.isSuccess()) fail(sent.cause()); });
                    });
                });
            });
        } catch (Exception | LinkageError e) { fail(e); }
    }

    private void bridge(QuicStreamChannel stream) {
        channels.add(stream);
        if (closed.get() || stream.type() != QuicStreamType.BIDIRECTIONAL) {
            stream.close();
            return;
        }
        if (streams.incrementAndGet() > 4) { streams.decrementAndGet(); stream.close(); return; }
        stream.closeFuture().addListener(ignored -> streams.decrementAndGet());
        stream.config().setAutoRead(false);
        var connection = new Bootstrap().group(group).channel(NioSocketChannel.class)
            .option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.AUTO_READ, false).handler(new Pipe(stream))
            .connect(new InetSocketAddress("127.0.0.1", localPort));
        channels.add(connection.channel());
        connection.addListener((ChannelFutureListener) result -> {
            if (!result.isSuccess() || !stream.isActive() || closed.get()) { result.channel().close(); stream.close(); return; }
            stream.pipeline().addLast(new Pipe(result.channel()));
            result.channel().config().setAutoRead(true);
            stream.config().setAutoRead(true);
        });
    }

    private void fail(Throwable error) {
        if (!closed.compareAndSet(false, true)) return;
        failure = error;
        domain = "";
        state = State.FAILED;
        ready.completeExceptionally(error);
        channels.close();
        group.shutdownGracefully(0, 2, TimeUnit.SECONDS);
    }

    @Override public void close() {
        if (!closed.compareAndSet(false, true)) return;
        state = State.CLOSED;
        domain = "";
        ready.cancel(false);
        channels.close();
        group.shutdownGracefully(0, 2, TimeUnit.SECONDS);
    }

    static String validateDomain(String value) {
        String ascii = IDN.toASCII(value, IDN.USE_STD3_ASCII_RULES).toLowerCase(Locale.ROOT);
        if (ascii.length() > 253 || !ascii.endsWith(".e4mc.link") || ascii.contains("..")) {
            throw new IllegalArgumentException("Unexpected relay domain");
        }
        return ascii;
    }

    /** Server-bound messages use VarInt lengths; client-bound QUIClime messages use unsigned bytes. */
    static final class ControlCodec extends ByteToMessageCodec<JsonObject> {
        @Override protected void encode(ChannelHandlerContext context, JsonObject message, ByteBuf output) {
            byte[] data = message.toString().getBytes(StandardCharsets.UTF_8);
            if (data.length > 8192) throw new IllegalArgumentException("Control message too large");
            int length = data.length;
            while ((length & ~127) != 0) { output.writeByte((length & 127) | 128); length >>>= 7; }
            output.writeByte(length).writeBytes(data);
        }
        @Override protected void decode(ChannelHandlerContext context, ByteBuf input, List<Object> output) {
            if (!input.isReadable()) return;
            int length = input.getUnsignedByte(input.readerIndex());
            if (length == 0) throw new CorruptedFrameException("Empty control message");
            if (input.readableBytes() < length + 1) return;
            input.skipBytes(1);
            String json = input.readCharSequence(length, StandardCharsets.UTF_8).toString();
            JsonObject message = JsonParser.parseString(json).getAsJsonObject();
            if (!message.has("kind") || !message.get("kind").isJsonPrimitive()) throw new CorruptedFrameException("Invalid control message");
            output.add(message);
        }
    }

    private static final class Pipe extends ChannelInboundHandlerAdapter {
        private final Channel peer;
        private Pipe(Channel peer) { this.peer = peer; }
        @Override public void channelRead(ChannelHandlerContext context, Object message) {
            if (!peer.isActive()) { ReferenceCountUtil.release(message); context.close(); return; }
            peer.writeAndFlush(message).addListener((ChannelFutureListener) result -> {
                if (!result.isSuccess()) context.close();
            });
            if (!peer.isWritable()) context.channel().config().setAutoRead(false);
        }
        @Override public void channelWritabilityChanged(ChannelHandlerContext context) {
            if (peer.isActive()) peer.config().setAutoRead(context.channel().isWritable());
        }
        @Override public void channelInactive(ChannelHandlerContext context) {
            if (peer.isActive()) peer.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
        @Override public void exceptionCaught(ChannelHandlerContext context, Throwable error) { context.close(); peer.close(); }
    }
}

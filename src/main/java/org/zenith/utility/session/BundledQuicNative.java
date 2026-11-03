package org.zenith.utility.session;

import io.netty.incubator.codec.quic.Quic;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Properties;

/** Loads the verified binary shipped inside this mod; never downloads native code at runtime. */
final class BundledQuicNative {
    private static boolean loaded;

    private BundledQuicNative() {}

    static synchronized void load() throws IOException {
        if (loaded) return;
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String architecture = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);
        String arch = switch (architecture) {
            case "amd64", "x86_64" -> "x86_64";
            case "aarch64", "arm64" -> "aarch_64";
            default -> throw new IOException("Unsupported tunnel architecture: " + architecture);
        };
        String file;
        if (os.contains("win") && arch.equals("x86_64")) file = "netty_quiche_windows_x86_64_74.dll";
        else if (os.contains("linux")) file = "libnetty_quiche_linux_" + arch + "_74.so";
        else if (os.contains("mac")) file = "libnetty_quiche_osx_" + arch + "_74.dylib";
        else throw new IOException("Unsupported tunnel platform: " + os + "/" + arch);
        Properties hashes = new Properties();
        try (var manifest = BundledQuicNative.class.getResourceAsStream("/natives/session-quic/sha256.properties")) {
            if (manifest == null) throw new IOException("Missing bundled tunnel checksums");
            hashes.load(manifest);
        }
        byte[] bytes;
        try (var resource = BundledQuicNative.class.getResourceAsStream("/natives/session-quic/" + file)) {
            if (resource == null) throw new IOException("Missing bundled tunnel native: " + file);
            bytes = resource.readAllBytes();
        }
        try {
            String hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
            if (!hash.equals(hashes.getProperty(file))) throw new IOException("Tunnel native checksum mismatch");
        } catch (NoSuchAlgorithmException e) { throw new IOException(e); }
        Path directory = Files.createTempDirectory("zenithopus-quic-");
        directory.toFile().deleteOnExit();
        Path binary = directory.resolve(file);
        Files.write(binary, bytes);
        binary.toFile().deleteOnExit();
        // The e4mc Netty fork supports an explicit native path. Set it before Quic is initialized,
        // bypassing its optional network downloader entirely.
        String previous = System.getProperty("link.e4mc.native_path");
        System.setProperty("link.e4mc.native_path", binary.toAbsolutePath().toString());
        try {
            Quic.ensureAvailability();
            loaded = true;
        } finally {
            if (previous == null) System.clearProperty("link.e4mc.native_path");
            else System.setProperty("link.e4mc.native_path", previous);
        }
    }
}

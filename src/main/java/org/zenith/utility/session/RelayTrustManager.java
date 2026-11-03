package org.zenith.utility.session;

import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;
import javax.net.ssl.*;

/** QUIClime's engine does not support SSLParameters; verify the relay's DNS SAN explicitly. */
final class RelayTrustManager extends X509ExtendedTrustManager {
    private final String host;
    private final X509TrustManager platform;

    RelayTrustManager(String host) throws GeneralSecurityException {
        this.host = host;
        TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        factory.init((KeyStore) null);
        X509TrustManager found = null;
        for (TrustManager manager : factory.getTrustManagers()) if (manager instanceof X509TrustManager trust) found = trust;
        if (found == null) throw new GeneralSecurityException("Platform certificate verification unavailable");
        platform = found;
    }

    @Override public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        platform.checkServerTrusted(chain, authType);
        if (chain.length == 0) throw new CertificateException("Empty relay certificate chain");
        var names = chain[0].getSubjectAlternativeNames();
        if (names != null) for (var name : names) {
            if (name.size() >= 2 && Integer.valueOf(2).equals(name.get(0)) && name.get(1) instanceof String dns && matches(host, dns)) return;
        }
        throw new CertificateException("Relay certificate does not match its hostname");
    }

    static boolean matches(String host, String certificateName) {
        String name = certificateName.toLowerCase(Locale.ROOT);
        if (!name.startsWith("*.")) return host.equals(name);
        String suffix = name.substring(1);
        if (!host.endsWith(suffix)) return false;
        String firstLabel = host.substring(0, host.length() - suffix.length());
        return !firstLabel.isEmpty() && firstLabel.indexOf('.') < 0;
    }

    @Override public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException { checkServerTrusted(chain, authType); }
    @Override public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException { checkServerTrusted(chain, authType); }
    @Override public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException { platform.checkClientTrusted(chain, authType); }
    @Override public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException { platform.checkClientTrusted(chain, authType); }
    @Override public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException { platform.checkClientTrusted(chain, authType); }
    @Override public X509Certificate[] getAcceptedIssuers() { return platform.getAcceptedIssuers(); }
}

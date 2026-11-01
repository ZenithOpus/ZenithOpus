package org.zenith.utility.api;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class HttpClients {
   private static final Map<String, HttpClient> items = new ConcurrentHashMap<>();

   private HttpClients() {
   }

   public static HttpClient method2670(Duration var0) {
      return method2671(var0, Redirect.NEVER);
   }

   public static HttpClient method2671(Duration var0, Redirect var1) {
      String var2 = var0.toMillis() + ":" + var1;
      return items.computeIfAbsent(var2, var2x -> HttpClient.newBuilder().connectTimeout(var0).followRedirects(var1).build());
   }
}

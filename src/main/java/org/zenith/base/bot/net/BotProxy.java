package org.zenith.base.bot.net;

import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public final class BotProxy {
   private final BotProxy.Type type;
   private final String host;
   private final int port;
   @Nullable
   private final String username;
   @Nullable
   private final String password;

   public BotProxy.Type type() {
      return this.type;
   }

   public String host() {
      return this.host;
   }

   public int port() {
      return this.port;
   }

   public String username() {
      return this.username;
   }

   public String password() {
      return this.password;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof BotProxy other)) return false;
      return java.util.Objects.equals(this.type, other.type) && java.util.Objects.equals(this.host, other.host) && java.util.Objects.equals(this.port, other.port) && java.util.Objects.equals(this.username, other.username) && java.util.Objects.equals(this.password, other.password);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(type, host, port, username, password);
   }

   @Override
   public String toString() {
      return "BotProxy[" + "type=" + this.type + ", " + "host=" + this.host + ", " + "port=" + this.port + ", " + "username=" + this.username + ", " + "password=" + this.password + "]";
   }

   public BotProxy(BotProxy.Type var1, String var2, int var3, @Nullable String var4, @Nullable String var5) {
      this.type = var1;
      this.host = var2;
      this.port = var3;
      this.username = var4;
      this.password = var5;
   }

   @Nullable
   public static BotProxy parse(@Nullable String var0) {
      if (var0 != null && !var0.isBlank()) {
         String var1 = var0.trim();
         return var1.contains("://") ? parseUri(var1) : parseLegacy(var1);
      } else {
         return null;
      }
   }

   private static BotProxy parseUri(String var0) {
      URI var1;
      try {
         var1 = URI.create(var0);
      } catch (IllegalArgumentException var6) {
         throw new IllegalArgumentException("Invalid proxy address", var6);
      }

      BotProxy.Type var2 = BotProxy.Type.fromScheme(var1.getScheme());
      String var3 = var1.getHost();
      int var4 = var1.getPort();
      if (var3 != null && !var3.isBlank() && var4 > 0) {
         BotProxy.Credentials var5 = BotProxy.Credentials.parse(var1.getUserInfo());
         return new BotProxy(var2, var3, validatePort(var4), var5.username(), var5.password());
      } else {
         throw new IllegalArgumentException("Invalid proxy address");
      }
   }

   private static BotProxy parseLegacy(String var0) {
      String var1 = var0;
      BotProxy.Credentials var2 = BotProxy.Credentials.EMPTY;
      int var3 = var0.lastIndexOf(64);
      if (var3 >= 0) {
         var2 = BotProxy.Credentials.parse(var0.substring(0, var3));
         var1 = var0.substring(var3 + 1);
      }

      BotProxy.HostPort var4 = BotProxy.HostPort.parse(var1);
      if (var2 == BotProxy.Credentials.EMPTY && var4.tail().length > 0) {
         var2 = BotProxy.Credentials.fromTail(var4.tail());
      }

      return new BotProxy(BotProxy.Type.SOCKS5, var4.host(), var4.port(), var2.username(), var2.password());
   }

   public ProxyHandler createHandler() {
      InetSocketAddress var1 = new InetSocketAddress(this.host, this.port);
      boolean var2 = this.username != null && !this.username.isBlank();
      String var3 = this.password == null ? "" : this.password;

      return (ProxyHandler)(switch (this.type) {
         case SOCKS5 -> var2 ? new Socks5ProxyHandler(var1, this.username, var3) : new Socks5ProxyHandler(var1);
         case SOCKS4 -> var2 ? new Socks4ProxyHandler(var1, this.username) : new Socks4ProxyHandler(var1);
         case HTTP -> var2 ? new HttpProxyHandler(var1, this.username, var3) : new HttpProxyHandler(var1);
      });
   }

   private static int validatePort(int var0) {
      if (var0 >= 1 && var0 <= 65535) {
         return var0;
      } else {
         throw new IllegalArgumentException("Invalid proxy port");
      }
   }

   private static final class Credentials {
      @Nullable
      private final String username;
      @Nullable
      private final String password;

      public String username() {
         return this.username;
      }

      public String password() {
         return this.password;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Credentials other)) return false;
         return java.util.Objects.equals(this.username, other.username) && java.util.Objects.equals(this.password, other.password);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(username, password);
      }

      @Override
      public String toString() {
         return "Credentials[" + "username=" + this.username + ", " + "password=" + this.password + "]";
      }
      private static final BotProxy.Credentials EMPTY = new BotProxy.Credentials(null, null);

      private Credentials(@Nullable String var1, @Nullable String var2) {
         this.username = var1;
         this.password = var2;
      }

      private static BotProxy.Credentials parse(@Nullable String var0) {
         if (var0 != null && !var0.isBlank()) {
            int var1 = var0.indexOf(58);
            return var1 < 0 ? new BotProxy.Credentials(var0, null) : new BotProxy.Credentials(var0.substring(0, var1), var0.substring(var1 + 1));
         } else {
            return EMPTY;
         }
      }

      private static BotProxy.Credentials fromTail(String[] var0) {
         if (var0.length != 0 && !var0[0].isBlank()) {
            return var0.length == 1
               ? new BotProxy.Credentials(var0[0], null)
               : new BotProxy.Credentials(var0[0], String.join(":", Arrays.copyOfRange(var0, 1, var0.length)));
         } else {
            return EMPTY;
         }
      }
   }

   private static final class HostPort {
      private final String host;
      private final int port;
      private final String[] tail;

      public String host() {
         return this.host;
      }

      public int port() {
         return this.port;
      }

      public String[] tail() {
         return this.tail;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof HostPort other)) return false;
         return java.util.Objects.equals(this.host, other.host) && java.util.Objects.equals(this.port, other.port) && java.util.Objects.equals(this.tail, other.tail);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(host, port, tail);
      }

      @Override
      public String toString() {
         return "HostPort[" + "host=" + this.host + ", " + "port=" + this.port + ", " + "tail=" + this.tail + "]";
      }

      private HostPort(String var1, int var2, String[] var3) {
         this.host = var1;
         this.port = var2;
         this.tail = var3;
      }

      private static BotProxy.HostPort parse(String var0) {
         if (var0 == null || var0.isBlank()) {
            throw new IllegalArgumentException("Invalid proxy address");
         } else if (var0.startsWith("[")) {
            int var4 = var0.indexOf(93);
            if (var4 >= 0 && var4 + 1 < var0.length() && var0.charAt(var4 + 1) == ':') {
               String var2 = var0.substring(1, var4);
               String[] var3 = var0.substring(var4 + 2).split(":", -1);
               return create(var2, var3);
            } else {
               throw new IllegalArgumentException("Invalid proxy address");
            }
         } else {
            String[] var1 = var0.split(":", -1);
            if (var1.length < 2) {
               throw new IllegalArgumentException("Invalid proxy address");
            } else {
               return create(var1[0], Arrays.copyOfRange(var1, 1, var1.length));
            }
         }
      }

      private static BotProxy.HostPort create(String var0, String[] var1) {
         if (var0 != null && !var0.isBlank() && var1.length != 0) {
            int var2;
            try {
               var2 = Integer.parseInt(var1[0]);
            } catch (NumberFormatException var4) {
               throw new IllegalArgumentException("Invalid proxy port", var4);
            }

            return new BotProxy.HostPort(var0, BotProxy.validatePort(var2), Arrays.copyOfRange(var1, 1, var1.length));
         } else {
            throw new IllegalArgumentException("Invalid proxy address");
         }
      }
   }

   public static enum Type {
      SOCKS5,
      SOCKS4,
      HTTP;

      private static BotProxy.Type fromScheme(String var0) {
         if (var0 != null && !var0.isBlank()) {
            String var1 = var0.toLowerCase(Locale.ROOT);

            return switch (var1) {
               case "socks", "socks5" -> SOCKS5;
               case "socks4" -> SOCKS4;
               case "http", "https" -> HTTP;
               default -> throw new IllegalArgumentException("Unsupported proxy type: " + var0);
            };
         } else {
            return SOCKS5;
         }
      }
   }
}

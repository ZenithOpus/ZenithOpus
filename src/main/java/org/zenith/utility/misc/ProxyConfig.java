package org.zenith.utility.misc;

import lombok.Generated;

public class ProxyConfig {
   private String host = "";
   private int port = 1080;
   private ProxyConfig.Type type = ProxyConfig.Type.SOCKS5;
   private String username = "";
   private String password = "";

   public ProxyConfig() {
   }

   public ProxyConfig(String var1, int var2, ProxyConfig.Type var3, String var4, String var5) {
      this.host = var1;
      this.port = var2;
      this.type = var3;
      this.username = var4 == null ? "" : var4;
      this.password = var5 == null ? "" : var5;
   }

   public static ProxyConfig method439(String var0, ProxyConfig.Type var1) {
      if (var0 == null) {
         return null;
      } else {
         String[] var2 = var0.trim().split(":", 4);
         if (var2.length < 2) {
            return null;
         } else {
            int var3;
            try {
               var3 = Integer.parseInt(var2[1].trim());
            } catch (NumberFormatException var6) {
               return null;
            }

            if (var3 > 0 && var3 <= 65535) {
               String var4 = var2.length > 2 ? var2[2] : "";
               String var5 = var2.length > 3 ? var2[3] : "";
               return new ProxyConfig(var2[0].trim(), var3, var1, var4, var5);
            } else {
               return null;
            }
         }
      }
   }

   public boolean isValid() {
      return this.host != null && !this.host.isBlank() && this.port > 0 && this.port <= 65535;
   }

   public boolean method440() {
      return this.username != null && !this.username.isEmpty();
   }

   public String method441() {
      return this.host + ":" + this.port;
   }

   @Override
   public String toString() {
      return this.type + " " + this.method441();
   }

   @Generated
   public String getHost() {
      return this.host;
   }

   @Generated
   public int getPort() {
      return this.port;
   }

   @Generated
   public ProxyConfig.Type getType() {
      return this.type;
   }

   @Generated
   public String getUsername() {
      return this.username;
   }

   @Generated
   public String getPassword() {
      return this.password;
   }

   @Generated
   public void setHost(String var1) {
      this.host = var1;
   }

   @Generated
   public void setPort(int var1) {
      this.port = var1;
   }

   @Generated
   public void setType(ProxyConfig.Type var1) {
      this.type = var1;
   }

   @Generated
   public void setUsername(String var1) {
      this.username = var1;
   }

   @Generated
   public void setPassword(String var1) {
      this.password = var1;
   }

   public static enum Type {
      SOCKS5,
      SOCKS4;
   }
}

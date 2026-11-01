package org.zenith.utility.network;

import com.google.gson.reflect.TypeToken;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Generated;
import org.zenith.utility.api.JsonListStorage;
import org.zenith.utility.misc.ProxyConfig;

public final class Proxies extends JsonListStorage<ProxyConfig> {
   private volatile int intValue = -1;

   public Proxies() {
      super("proxies.json", "", (new TypeToken<List<ProxyConfig>>() {}).getType(), CopyOnWriteArrayList::new);
      CopyOnWriteArrayList var1 = new CopyOnWriteArrayList<>(this.items);
      this.items = var1;
   }

   public List<ProxyConfig> method6535() {
      return new ArrayList<>(this.items);
   }

   public int size() {
      return this.items.size();
   }

   public ProxyConfig method6536() {
      var var1 = this.method6535();
      if (this.intValue >= 0 && this.intValue < var1.size()) {
         ProxyConfig var2 = (ProxyConfig)var1.get(this.intValue);
         return var2 != null && var2.isValid() ? var2 : null;
      } else {
         return null;
      }
   }

   public boolean isEnabled() {
      return this.method6536() != null;
   }

   public ProxyHandler createHandler() {
      ProxyConfig var1 = this.method6536();
      if (var1 == null) {
         return null;
      } else {
         InetSocketAddress var2 = new InetSocketAddress(var1.getHost(), var1.getPort());
         boolean var3 = var1.method440();
         String var4 = var1.getPassword() == null ? "" : var1.getPassword();
         if (var1.getType() == ProxyConfig.Type.SOCKS4) {
            return var3 ? new Socks4ProxyHandler(var2, var1.getUsername()) : new Socks4ProxyHandler(var2);
         } else {
            return var3 ? new Socks5ProxyHandler(var2, var1.getUsername(), var4) : new Socks5ProxyHandler(var2);
         }
      }
   }

   public void select(int var1) {
      this.intValue = var1 == this.intValue ? -1 : var1;
   }

   public void disable() {
      this.intValue = -1;
   }

   public void method6537(ProxyConfig var1) {
      if (var1 != null && var1.isValid()) {
         this.add(var1);
         this.save();
      }
   }

   public void method6538(int var1) {
      var var2 = this.method6535();
      if (var1 >= 0 && var1 < var2.size()) {
         this.remove((ProxyConfig)var2.get(var1));
         if (this.intValue == var1) {
            this.intValue = -1;
         } else if (this.intValue > var1) {
            this.intValue--;
         }

         this.save();
      }
   }

   @Generated
   public int getIntValue2() {
      return this.intValue;
   }
}

package org.zenith.utility.discord.utils;

import java.io.Serializable;
import lombok.Generated;

public class RPCButton implements Serializable {
   private final String url;
   private final String label;

   public static RPCButton create(String var0, String var1) {
      var0 = var0.substring(0, Math.min(var0.length(), 31));
      return new RPCButton(var0, var1);
   }

   public RPCButton(String var1, String var2) {
      this.label = var1;
      this.url = var2;
   }

   @Generated
   public String getUrl() {
      return this.url;
   }

   @Generated
   public String getLabel() {
      return this.label;
   }
}

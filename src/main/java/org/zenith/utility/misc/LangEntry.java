package org.zenith.utility.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;

public class LangEntry {
   private final String text;
   private final Map<String, String> items = new HashMap<>();

   public LangEntry(String var1, InputStream var2) throws IOException {
      this.text = var1;
      this.method3303(var2);
   }

   private void method3303(InputStream var1) throws IOException {
      String var3;
      try (BufferedReader var2 = new BufferedReader(new InputStreamReader(var1, StandardCharsets.UTF_8))) {
         while ((var3 = var2.readLine()) != null) {
            var3 = var3.trim();
            if (!var3.isEmpty() && !var3.startsWith("#")) {
               String[] var4 = var3.split("=", 2);
               if (var4.length == 2) {
                  this.items.put(var4[0].trim(), var4[1].trim());
               }
            }
         }
      }
   }

   public String method3304(String var1) {
      String var2 = this.items.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         int var3 = var1.lastIndexOf(46);
         if (var3 >= 0 && var3 + 1 < var1.length()) {
            String var4 = this.items.get(var1.substring(0, var3 + 1) + "*");
            if (var4 != null) {
               return var4.replace("{value}", var1.substring(var3 + 1));
            }
         }

         return var1;
      }
   }

   @Generated
   public String getName() {
      return this.text;
   }
}

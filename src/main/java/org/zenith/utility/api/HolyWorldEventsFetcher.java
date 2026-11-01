package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Generated;
import org.zenith.Zenith;
import org.zenith.utility.misc.HolyWorldEvent;

public class HolyWorldEventsFetcher {
   private static final String text = "https://api.holyworld.me/v1/events";
   private static final String text2 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";
   private static final int CONST_intValue = 5000;
   private volatile List<HolyWorldEvent> items = List.of();
   private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

   public HolyWorldEventsFetcher() {
      this.scheduledExecutorService.scheduleAtFixedRate(this::method1718, 0L, 3L, TimeUnit.SECONDS);
   }

   public void method1718() {
      if (!Zenith.getInstance().getServerDetector().method1642()) {
         this.items = List.of();
      } else {
         HttpURLConnection var1 = null;

         try {
            var1 = (HttpURLConnection)URI.create("https://api.holyworld.me/v1/events").toURL().openConnection();
            var1.setRequestMethod("GET");
            var1.setConnectTimeout(5000);
            var1.setReadTimeout(5000);
            var1.setRequestProperty("Accept", "application/json");
            var1.setRequestProperty(
               "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"
            );
            if (var1.getResponseCode() == 200) {
               List var2;
               try (BufferedInputStream var3 = new BufferedInputStream(var1.getInputStream())) {
                  var2 = this.method1719(var3);
               }

               this.items = List.copyOf(var2);
               return;
            }
         } catch (Exception var13) {
            return;
         } finally {
            if (var1 != null) {
               var1.disconnect();
            }
         }
      }
   }

   private List<HolyWorldEvent> method1719(InputStream var1) throws IOException {
      InputStreamReader var3 = new InputStreamReader(var1, StandardCharsets.UTF_8);

      JsonElement var2;
      try {
         var2 = JsonParser.parseReader(var3);
      } catch (Throwable var13) {
         try {
            var3.close();
         } catch (Throwable var12) {
            var13.addSuppressed(var12);
         }

         throw var13;
      }

      var3.close();
      if (!var2.isJsonObject()) {
         throw new IOException("HolyWorld events response must be a JSON object");
      } else {
         TreeMap var14 = new TreeMap();

         for (Entry var5 : var2.getAsJsonObject().entrySet()) {
            int var6 = this.method1720((String)var5.getKey());
            if (var6 > 0 && ((JsonElement)var5.getValue()).isJsonArray()) {
               var14.put(var6, ((JsonElement)var5.getValue()).getAsJsonArray());
            }
         }

         LinkedHashMap var15 = new LinkedHashMap();

         for (Object var18_o : (Iterable<?>)(Object)(var14.entrySet())) {
            Entry var18 = (Entry)var18_o;
            for (JsonElement var8 : (JsonArray)var18.getValue()) {
               if (var8.isJsonObject()) {
                  JsonObject var9 = var8.getAsJsonObject();
                  JsonElement var10 = var9.get("id");
                  if (var10 != null && var10.isJsonPrimitive()) {
                     String var11 = var10.getAsString();
                     if (!var11.isBlank()) {
                        ((java.util.List)var15.computeIfAbsent(var11, var0 -> new TreeSet<>())).add((Integer)var18.getKey());
                     }
                  }
               }
            }
         }

         ArrayList<HolyWorldEvent> var17 = new ArrayList<>();

         for (Object var20_o : (Iterable<?>)(Object)(var15.entrySet())) {
            Entry var20 = (Entry)var20_o;
            String var21 = this.method1721((TreeSet<Integer>)var20.getValue());
            HolyWorldEvent var22 = new HolyWorldEvent((String)var20.getKey(), var21, 0L);
            if (var22.method7743()) {
               var17.add(var22);
            }
         }

         return var17;
      }
   }

   private int method1720(String var1) {
      int var2 = var1.lastIndexOf(95);
      if (var2 >= 0 && var2 != var1.length() - 1) {
         try {
            return Integer.parseInt(var1.substring(var2 + 1));
         } catch (NumberFormatException var4) {
            return -1;
         }
      } else {
         return -1;
      }
   }

   private String method1721(TreeSet<Integer> var1) {
      StringBuilder var2 = new StringBuilder();

      for (int var4 : var1) {
         if (!var2.isEmpty()) {
            var2.append(", ");
         }

         var2.append('#').append(var4);
      }

      return var2.toString();
   }

   @Generated
   public List<HolyWorldEvent> getItems() {
      return this.items;
   }
}

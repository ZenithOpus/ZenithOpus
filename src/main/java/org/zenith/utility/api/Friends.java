package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class Friends {
   private final Path path;
   private final Set<String> items = new LinkedHashSet<>();
   private Friends.Handler handler;

   public Friends(Path var1) {
      this.path = var1.resolve("nexus-friends.json");
      this.load();
   }

   public void setHandler(Friends.Handler var1) {
      this.handler = var1;
   }

   public boolean method6442(String var1) {
      return var1 != null && !this.items.isEmpty() ? this.items.contains(var1.toLowerCase(Locale.ROOT)) : false;
   }

   public boolean add(String var1) {
      String var2 = var1.toLowerCase(Locale.ROOT);
      if (!this.isFull() && this.items.add(var2)) {
         if (this.handler != null) {
            this.handler.method6445(var2);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean method6443(String var1) {
      String var2 = var1.toLowerCase(Locale.ROOT);
      if (!this.items.remove(var2)) {
         return false;
      } else {
         if (this.handler != null) {
            this.handler.method6446(var2);
         }

         return true;
      }
   }

   public boolean method6444(Collection<String> var1) {
      LinkedHashSet<String> var2 = new LinkedHashSet();

      for (String var4 : var1) {
         if (var4 != null && !var4.isBlank()) {
            var2.add(var4.toLowerCase(Locale.ROOT));
         }
      }

      if (var2.equals(this.items)) {
         return false;
      } else if (var2.size() >= this.items.size()) {
         this.items.clear();
         this.items.addAll(var2);
         return true;
      } else {
         boolean var6 = false;

         for (Object var5_o : (Iterable<?>)(Object)(var2)) {
            String var5 = (String)var5_o;
            if (this.items.size() >= 200) {
               break;
            }

            var6 |= this.items.add(var5);
         }

         return var6;
      }
   }

   public boolean isFull() {
      return this.items.size() >= 200;
   }

   public List<String> all() {
      return new ArrayList<>(this.items);
   }

   public int size() {
      return this.items.size();
   }

   public void clear() {
      this.items.clear();
      if (this.handler != null) {
         this.handler.method6447();
      }
   }

   public void save() {
      JsonArray var1 = new JsonArray();
      this.items.forEach(var1::add);
      JsonObject var2 = new JsonObject();
      var2.add("friends", var1);
      CloudJsonIo.method5993(this.path, var2);
   }

   private void load() {
      JsonObject var1 = CloudJsonIo.method5992(this.path);
      if (var1 != null && var1.has("friends") && var1.get("friends").isJsonArray()) {
         var1.getAsJsonArray("friends").forEach(var1x -> {
            if (var1x.isJsonPrimitive() && this.items.size() < 200) {
               String var2 = var1x.getAsString();
               if (!var2.isBlank() && var2.length() <= 32) {
                  this.items.add(var2.toLowerCase(Locale.ROOT));
               }
            }
         });
      }
   }

   public interface Handler {
      void method6445(String var1);

      void method6446(String var1);

      void method6447();
   }
}

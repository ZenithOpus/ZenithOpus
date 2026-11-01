package org.zenith.utility.misc;

import com.google.gson.reflect.TypeToken;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.zenith.Zenith;
import org.zenith.utility.api.JsonListStorage;
import org.zenith.utility.network.Cloud;

public class Friends extends JsonListStorage<String> {
   public Friends() {
      super("friends.json", "", (new TypeToken<Set<String>>() {}).getType(), ConcurrentHashMap::newKeySet);
      KeySetView var1 = ConcurrentHashMap.newKeySet();
      var1.addAll(this.items);
      this.items = var1;
   }

   public boolean method7626(Entity var1) {
      return var1 instanceof PlayerEntity var2 ? this.method7627(var2.getGameProfile().name()) : false;
   }

   public boolean method7627(String var1) {
      return this.getItems().contains(var1) || this.method7628(var1);
   }

   public boolean method7628(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         Zenith var2 = Zenith.getInstance();
         Cloud var3 = var2 == null ? null : var2.getCloud();
         if (var3 != null && var3.method7862()) {
            for (PartyPlayerInfo var5 : var3.state().method8038()) {
               if (var1.equalsIgnoreCase(var5.getText2())) {
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void method7629(String var1) {
      this.getItems().remove(var1);
   }
}

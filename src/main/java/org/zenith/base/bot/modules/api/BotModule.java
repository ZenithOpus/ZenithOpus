package org.zenith.base.bot.modules.api;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.Generated;
import org.jetbrains.annotations.Nullable;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.BotEventBus;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotInteractionManager;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.client.settings.Setting;

public abstract class BotModule {
   public final ModuleInfo info = this.getClass().getAnnotation(ModuleInfo.class);
   private final String name = this.info.name();
   private final String nameLower = this.name.toLowerCase(Locale.ROOT);
   private final ModuleCategory category = this.info.category();
   private final String description = this.info.description();
   private volatile boolean enabled;
   private BotClient bot;
   private BotEventBus eventBus;

   public final void bind(BotClient var1) {
      this.bot = var1;
      this.eventBus = var1.getEventBus();
   }

   public void toggle() {
      this.enabled = !this.enabled;
      if (this.enabled) {
         this.onEnable();
      } else {
         this.onDisable();
      }
   }

   public void setToggled(boolean var1) {
      if (var1 && !this.enabled) {
         this.toggle();
      }

      if (!var1 && this.enabled) {
         this.toggle();
      }
   }

   public final void disableSelf() {
      this.setToggled(false);
   }

   public void onEnable() {
      this.eventBus.register(this);
   }

   public void onDisable() {
      this.eventBus.unregister(this);
   }

   public final BotClient bot() {
      return this.bot;
   }

   @Nullable
   public final BotWorld world() {
      return this.bot.getWorld();
   }

   @Nullable
   public final BotPlayer player() {
      return this.bot.getPlayer();
   }

   @Nullable
   public final BotPlayHandler handler() {
      return this.bot.getPlayHandler();
   }

   @Nullable
   public final BotInteractionManager interaction() {
      BotPlayHandler var1 = this.bot.getPlayHandler();
      return var1 != null && var1.getWorld() != null ? var1.getInteractionManager() : null;
   }

   public List<Setting> getSettings() {
      return Arrays.stream(this.getClass().getDeclaredFields()).map(var1 -> {
         try {
            var1.setAccessible(true);
            return var1.get(this);
         } catch (IllegalAccessException var3) {
            var3.printStackTrace();
            return null;
         }
      }).filter(var0 -> var0 instanceof Setting).map(var0 -> (Setting)var0).collect(Collectors.toList());
   }

   public List<Setting> getUiSettings() {
      return this.getSettings();
   }

   public JsonObject save() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("enabled", this.enabled);
      JsonObject var2 = new JsonObject();

      for (Setting var4 : this.getSettings()) {
         var4.safe(var2);
      }

      var1.add("Settings", var2);
      return var1;
   }

   public void load(JsonObject var1) {
      if (var1 != null) {
         JsonObject var2 = null;

         try {
            var2 = var1.getAsJsonObject("Settings");
         } catch (Exception var8) {
            var8.printStackTrace();
         }

         if (var2 != null) {
            for (Setting var4 : this.getSettings()) {
               if (var2.has(var4.getKey())) {
                  try {
                     var4.load(var2);
                  } catch (Exception var7) {
                     System.err.println("[BotConfig] bad value " + this.name + "#" + var4.getKey() + ": " + var7);
                  }
               }
            }
         }

         try {
            if (var1.has("enabled")) {
               this.setToggled(var1.get("enabled").getAsBoolean());
            }
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }
   }

   @Generated
   public ModuleInfo getInfo() {
      return this.info;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public String getNameLower() {
      return this.nameLower;
   }

   @Generated
   public ModuleCategory getCategory() {
      return this.category;
   }

   @Generated
   public String getDescription() {
      return this.description;
   }

   @Generated
   public boolean isEnabled() {
      return this.enabled;
   }

   @Generated
   public BotClient getBot() {
      return this.bot;
   }

   @Generated
   public BotEventBus getEventBus() {
      return this.eventBus;
   }
}

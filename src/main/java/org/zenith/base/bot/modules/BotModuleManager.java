package org.zenith.base.bot.modules;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.zenith.ModuleInfo;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.bot.modules.impl.BotAutoCapcha;
import org.zenith.base.bot.modules.impl.BotAutoMine;
import org.zenith.base.bot.modules.impl.BotWarpFarm;

public final class BotModuleManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final List<String> SUPPORTED_MODULE_NAMES = List.of(
      BotAutoMine.class.getAnnotation(ModuleInfo.class).name(),
      BotWarpFarm.class.getAnnotation(ModuleInfo.class).name(),
      BotAutoCapcha.class.getAnnotation(ModuleInfo.class).name()
   );
   private static final Map<String, String> SUPPORTED_MODULE_DESCRIPTIONS = Map.of(
      BotAutoMine.class.getAnnotation(ModuleInfo.class).name(),
      BotAutoMine.class.getAnnotation(ModuleInfo.class).description(),
      BotWarpFarm.class.getAnnotation(ModuleInfo.class).name(),
      BotWarpFarm.class.getAnnotation(ModuleInfo.class).description(),
      BotAutoCapcha.class.getAnnotation(ModuleInfo.class).name(),
      BotAutoCapcha.class.getAnnotation(ModuleInfo.class).description()
   );
   private final BotClient client;
   private final Map<String, BotModule> modules = new LinkedHashMap<>();

   public static List<String> supportedModuleNames() {
      return SUPPORTED_MODULE_NAMES;
   }

   @Nullable
   public static String supportedModuleDescription(String var0) {
      return var0 == null ? null : SUPPORTED_MODULE_DESCRIPTIONS.get(var0);
   }

   public BotModuleManager(BotClient var1) {
      this.client = var1;
      this.register(new BotAutoMine());
      this.register(new BotWarpFarm());
      BotAutoCapcha var2 = new BotAutoCapcha();
      this.register(var2);
      var2.setToggled(true);
   }

   public void register(BotModule var1) {
      var1.bind(this.client);
      this.modules.put(var1.getNameLower(), var1);
   }

   public Collection<BotModule> getModules() {
      return this.modules.values();
   }

   @Nullable
   public BotModule getModule(String var1) {
      return this.modules.get(var1.toLowerCase(Locale.ROOT));
   }

   @Nullable
   public <T extends BotModule> T getModule(Class<T> var1) {
      for (BotModule var3 : this.modules.values()) {
         if (var1.isInstance(var3)) {
            return (T)var3;
         }
      }

      return null;
   }

   public boolean setEnabled(String var1, boolean var2) {
      BotModule var3 = this.getModule(var1);
      if (var3 == null) {
         return false;
      } else {
         var3.setToggled(var2);
         return true;
      }
   }

   public void applySettings(String var1, JsonObject var2) {
      BotModule var3 = this.getModule(var1);
      if (var3 != null) {
         var3.load(var2);
      }
   }

   public void disableAll() {
      for (BotModule var2 : this.modules.values()) {
         try {
            var2.setToggled(false);
         } catch (RuntimeException var4) {
            LOGGER.error("Bot {}: failed to disable module {}", new Object[]{this.client.getName(), var2.getName(), var4});
         }
      }
   }
}

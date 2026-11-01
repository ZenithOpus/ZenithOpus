package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.utility.misc.EmoteAccess;

public final class EmoteCommand extends CommandAbstract {
   public EmoteCommand() {
      super("emote");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.executes(var0 -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, "Использование: .emote <id>, .emote stop или .emote list");
         return 1;
      });
      var1.then(literal("list").executes(var0 -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, "Доступные анимации: " + String.join(", ", EmoteAccess.method7916()));
         return 1;
      }));
      var1.then(literal("stop").executes(var0 -> {
         if (mc.player != null) {
            EmoteAccess.method7922(mc.player.getUuid());
         }

         return 1;
      }));
      var1.then(arg("id", StringArgumentType.word()).executes(var0 -> {
         String var1x = StringArgumentType.getString(var0, "id");
         if (!EmoteAccess.method7920(var1x)) {
            ChatLogger.method2020(ChatLogger.Type.ERROR, "Неизвестная анимация: " + var1x + ". Используйте .emote list");
         }

         return 1;
      }));
   }
}

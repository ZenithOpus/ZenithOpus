package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.client.modules.impl.misc.StreamerModeModule;

public class StreamerModeCommand extends CommandAbstract {
   public StreamerModeCommand() {
      super("streamermode");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      for (String var5 : new String[]{"parseAnarchy", "parseanarchy"}) {
         var1.then(literal(var5).then(arg("anarchy", IntegerArgumentType.integer(1, 999)).executes(var0 -> {
            StreamerModeModule.INSTANCE.method6283(IntegerArgumentType.getInteger(var0, "anarchy"));
            return 1;
         })));
      }

      var1.then(literal("clear").executes(var0 -> {
         StreamerModeModule.INSTANCE.method6284();
         return 1;
      }));
      var1.executes(var0 -> {
         ChatLogger.method2019("Использование: .streamermode parseAnarchy <номер анки> и .streamermode clear");
         return 1;
      });
   }
}

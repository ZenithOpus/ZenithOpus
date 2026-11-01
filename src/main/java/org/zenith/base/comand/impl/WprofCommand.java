package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.utility.render.WorldProfiler;

public class WprofCommand extends CommandAbstract {
   public WprofCommand() {
      super("wprof");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.executes(var1x -> this.set(!WorldProfiler.enabled));
      var1.then(literal("on").executes(var1x -> this.set(true)));
      var1.then(literal("off").executes(var1x -> this.set(false)));
   }

   private int set(boolean var1) {
      WorldProfiler.setEnabled(var1);
      ChatLogger.method2020(ChatLogger.Type.INFO, "Замер погоды " + (var1 ? "включён: строки [WPROF] в логе раз в 3 с" : "выключен"));
      return 1;
   }
}

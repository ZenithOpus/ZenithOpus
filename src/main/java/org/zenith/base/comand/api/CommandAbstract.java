package org.zenith.base.comand.api;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import lombok.Generated;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;
import org.zenith.ZenithAccess;

public abstract class CommandAbstract implements ZenithAccess {
   private final String command;

   public CommandAbstract(String var1) {
      this.command = var1;
   }

   public void register(CommandDispatcher<CommandSource> var1) {
      LiteralArgumentBuilder var2 = LiteralArgumentBuilder.literal(this.command);
      this.execute(var2);
      var1.register(var2);
   }

   public abstract void execute(LiteralArgumentBuilder<CommandSource> var1);

   @NotNull
   public static <T> RequiredArgumentBuilder<CommandSource, T> arg(String var0, ArgumentType<T> var1) {
      return RequiredArgumentBuilder.argument(var0, var1);
   }

   @NotNull
   public static LiteralArgumentBuilder<CommandSource> literal(String var0) {
      return LiteralArgumentBuilder.literal(var0);
   }

   @Generated
   public String getCommand() {
      return this.command;
   }
}

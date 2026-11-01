package org.zenith.base.comand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.command.permission.PermissionPredicate;
import org.zenith.Zenith;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.BindsCommand;
import org.zenith.base.comand.impl.CalcCommand;
import org.zenith.base.comand.impl.ClipCommand;
import org.zenith.base.comand.impl.ConfigCommand;
import org.zenith.base.comand.impl.EmoteCommand;
import org.zenith.base.comand.impl.ExchangeCommand;
import org.zenith.base.comand.impl.FriendCommand;
import org.zenith.base.comand.impl.GpsCommand;
import org.zenith.base.comand.impl.HBotCommand;
import org.zenith.base.comand.impl.JoinCommand;
import org.zenith.base.comand.impl.JsCommand;
import org.zenith.base.comand.impl.MacroCommand;
import org.zenith.base.comand.impl.NameProtectCommand;
import org.zenith.base.comand.impl.NeuroCommand;
import org.zenith.base.comand.impl.NpcCommand;
import org.zenith.base.comand.impl.RCTCommand;
import org.zenith.base.comand.impl.RegionCommand;
import org.zenith.base.comand.impl.RouteCommand;
import org.zenith.base.comand.impl.SnowCommand;
import org.zenith.base.comand.impl.SortCommand;
import org.zenith.base.comand.impl.StaffCommand;
import org.zenith.base.comand.impl.StreamerModeCommand;
import org.zenith.base.comand.impl.TPCommand;
import org.zenith.base.comand.impl.WardenCommand;
import org.zenith.base.comand.impl.WayCommand;
import org.zenith.base.comand.impl.WprofCommand;

public class CommandManager {
   private String prefix = ".";
   private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher();
   private final CommandSource source = new ClientCommandSource(null, MinecraftClient.getInstance(), PermissionPredicate.ALL);
   private final List<CommandAbstract> commands = new ArrayList<>();

   public CommandManager() {
      this.init();
   }

   private void init() {
      this.registerCommand(new FriendCommand());
      this.registerCommand(new MacroCommand());
      this.registerCommand(new ClipCommand());
      this.registerCommand(new TPCommand());
      this.registerCommand(new ConfigCommand());
      this.registerCommand(new RCTCommand());
      this.registerCommand(new JoinCommand());
      this.registerCommand(new WayCommand());
      this.registerCommand(new GpsCommand());
      this.registerCommand(new BindsCommand());
      this.registerCommand(new NameProtectCommand());
      this.registerCommand(new StreamerModeCommand());
      this.registerCommand(new RegionCommand());
      this.registerCommand(new StaffCommand());
      this.registerCommand(new CalcCommand());
      this.registerCommand(new JsCommand());
      this.registerCommand(new ExchangeCommand());
      this.registerCommand(new RouteCommand());
      this.registerCommand(new HBotCommand());
      this.registerCommand(new SortCommand());
      this.registerCommand(new EmoteCommand());
      this.registerCommand(new WardenCommand());
      if (Zenith.method1502()) {
         this.registerCommand(new NeuroCommand());
         this.registerCommand(new SnowCommand());
         this.registerCommand(new NpcCommand());
      }

      if (Zenith.method1502() || System.getProperty("zenith.wprof") != null) {
         this.registerCommand(new WprofCommand());
      }
   }

   public Set<String> commandNames() {
      HashSet<String> var1 = new HashSet<>();

      for (CommandNode var3 : this.dispatcher.getRoot().getChildren()) {
         var1.add(var3.getName().toLowerCase(Locale.ROOT));
      }

      return var1;
   }

   public void registerCommand(CommandAbstract var1) {
      if (var1 != null) {
         var1.register(this.dispatcher);
         this.commands.add(var1);
      }
   }

   @Generated
   public String getPrefix() {
      return this.prefix;
   }

   @Generated
   public CommandDispatcher<CommandSource> getDispatcher() {
      return this.dispatcher;
   }

   @Generated
   public CommandSource getSource() {
      return this.source;
   }

   @Generated
   public List<CommandAbstract> getCommands() {
      return this.commands;
   }
}

package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.WayArgumentType;
import org.zenith.base.filemanager.impl.way.Way;

public class WayCommand extends CommandAbstract {
   private final SuggestionProvider<CommandSource> SUGGEST_X = (var0, var1) -> {
      ClientPlayerEntity var2 = MinecraftClient.getInstance().player;
      if (var2 != null) {
         BlockPos var3 = var2.getBlockPos();
         var1.suggest(var3.getX());
      }

      return var1.buildFuture();
   };
   private final SuggestionProvider<CommandSource> SUGGEST_Y = (var0, var1) -> {
      ClientPlayerEntity var2 = MinecraftClient.getInstance().player;
      if (var2 != null) {
         BlockPos var3 = var2.getBlockPos();
         var1.suggest(var3.getY());
      }

      return var1.buildFuture();
   };
   private final SuggestionProvider<CommandSource> SUGGEST_Z = (var0, var1) -> {
      ClientPlayerEntity var2 = MinecraftClient.getInstance().player;
      if (var2 != null) {
         BlockPos var3 = var2.getBlockPos();
         var1.suggest(var3.getZ());
      }

      return var1.buildFuture();
   };

   public WayCommand() {
      super("way");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.then(
         literal("add")
            .then(
               arg("name", StringArgumentType.word())
                  .then(
                     arg("x", IntegerArgumentType.integer())
                        .suggests(this.SUGGEST_X)
                        .then(
                           arg("y", IntegerArgumentType.integer())
                              .suggests(this.SUGGEST_Y)
                              .then(arg("z", IntegerArgumentType.integer()).suggests(this.SUGGEST_Z).executes(var1x -> {
                                 String var2 = (String)var1x.getArgument("name", String.class);
                                 int var3 = IntegerArgumentType.getInteger(var1x, "x");
                                 int var4 = IntegerArgumentType.getInteger(var1x, "y");
                                 int var5 = IntegerArgumentType.getInteger(var1x, "z");
                                 this.handleAddWay(var2, var3, var4, var5);
                                 return 1;
                              }))
                        )
                  )
            )
      );
      var1.then(literal("event").executes(var0 -> {
         Zenith.getInstance().getWayManager().startEventCapture();
         ChatLogger.method2019("Ожидание координат из следующих 2 сообщений чата");
         mc.getNetworkHandler().sendChatCommand("event");
         return 1;
      }));
      var1.then(literal("clear").executes(var0 -> {
         Zenith.getInstance().getWayManager().clearList();
         return 1;
      }));
      var1.then(literal("remove").then(arg("name", WayArgumentType.create()).executes(var0 -> {
         String var1x = (String)var0.getArgument("name", String.class);
         Zenith.getInstance().getWayManager().deleteWay(var1x);
         ChatLogger.method2020(ChatLogger.Type.INFO, var1x + " удален ");
         return 1;
      })));
      var1.then(literal("list").executes(var0 -> {
         if (!Zenith.getInstance().getWayManager().getItems().isEmpty()) {
            for (Way var2 : Zenith.getInstance().getWayManager().getItems()) {
               mc.player.sendMessage(var2.toText(), false);
            }
         } else {
            ChatLogger.method2019("Список пуст!");
         }

         return 1;
      }));
   }

   private void handleAddWay(String var1, int var2, int var3, int var4) {
      Way var5 = Zenith.getInstance().getWayManager().getWay(var1);
      if (var5 != null) {
         ChatLogger.method2021("Метка с таким именем уже есть в списке!");
         ChatLogger.method2019("Наведи мышку на сообщение ниже чтобы удалить ее");
         mc.player.sendMessage(var5.toText(), false);
      } else {
         String var6 = mc.getNetworkHandler().getServerInfo() != null ? mc.getNetworkHandler().getServerInfo().address : "VANILLA";
         Way var7 = new Way(var1, new BlockPos(var2, var3, var4), var6);
         mc.player.sendMessage(var7.toText(), false);
         Zenith.getInstance().getWayManager().addWay(var7);
      }
   }
}

package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import org.zenith.Zenith;
import org.zenith.base.comand.api.CommandAbstract;

public class RegionCommand extends CommandAbstract {
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

   public RegionCommand() {
      super("region");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.then(
         literal("pos1")
            .then(
               arg("x", IntegerArgumentType.integer())
                  .suggests(this.SUGGEST_X)
                  .then(
                     arg("y", IntegerArgumentType.integer())
                        .suggests(this.SUGGEST_Y)
                        .then(arg("z", IntegerArgumentType.integer()).suggests(this.SUGGEST_Z).executes(var0 -> {
                           int var1x = IntegerArgumentType.getInteger(var0, "x");
                           int var2 = IntegerArgumentType.getInteger(var0, "y");
                           int var3 = IntegerArgumentType.getInteger(var0, "z");
                           Zenith.getInstance().getTimedBlockPos().method474(new BlockPos(var1x, var2, var3));
                           return 1;
                        }))
                  )
            )
      );
      var1.then(
         literal("pos2")
            .then(
               arg("x", IntegerArgumentType.integer())
                  .suggests(this.SUGGEST_X)
                  .then(
                     arg("y", IntegerArgumentType.integer())
                        .suggests(this.SUGGEST_Y)
                        .then(arg("z", IntegerArgumentType.integer()).suggests(this.SUGGEST_Z).executes(var0 -> {
                           int var1x = IntegerArgumentType.getInteger(var0, "x");
                           int var2 = IntegerArgumentType.getInteger(var0, "y");
                           int var3 = IntegerArgumentType.getInteger(var0, "z");
                           Zenith.getInstance().getTimedBlockPos().method475(new BlockPos(var1x, var2, var3));
                           return 1;
                        }))
                  )
            )
      );
      var1.then(literal("clear").executes(var0 -> {
         Zenith.getInstance().getTimedBlockPos().clear();
         return 1;
      }));
   }
}

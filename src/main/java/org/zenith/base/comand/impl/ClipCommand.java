package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.CoordinateArgumentType;

public class ClipCommand extends CommandAbstract {
   public ClipCommand() {
      super("clip");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.then(literal("vclip").then(arg("distance", CoordinateArgumentType.create()).executes(var0 -> {
         double var1x = (Double)var0.getArgument("distance", Double.class);
         ClientPlayerEntity var3 = MinecraftClient.getInstance().player;
         if (var3 != null) {
            var3.setPosition(var3.getX(), var3.getY() + var1x + 0.1, var3.getZ());
            ChatLogger.method2020(ChatLogger.Type.INFO, "Вертикальный вклип на " + var1x + " блоков");
         }

         return 1;
      })));
      var1.then(literal("hclip").then(arg("distance", CoordinateArgumentType.create()).executes(var0 -> {
         double var1x = (Double)var0.getArgument("distance", Double.class);
         ClientPlayerEntity var3 = MinecraftClient.getInstance().player;
         if (var3 != null) {
            double var4 = Math.toRadians(var3.getYaw());
            var3.setPosition(var3.getX() - Math.sin(var4) * var1x, var3.getY() + 0.1, var3.getZ() + Math.cos(var4) * var1x);
            ChatLogger.method2020(ChatLogger.Type.INFO, "Горизонтальный вклип на " + var1x + " блоков");
         }

         return 1;
      })));
      var1.then(literal("up").then(arg("distance", CoordinateArgumentType.create()).executes(var0 -> {
         double var1x = (Double)var0.getArgument("distance", Double.class);
         ClientPlayerEntity var3 = MinecraftClient.getInstance().player;
         if (var3 != null) {
            var3.setPosition(var3.getX(), var3.getY() + var1x + 0.1, var3.getZ());
            ChatLogger.method2020(ChatLogger.Type.INFO, "Вклип вверх на " + var1x + " блоков");
         }

         return 1;
      })));
      var1.then(literal("down").then(arg("distance", CoordinateArgumentType.create()).executes(var0 -> {
         double var1x = (Double)var0.getArgument("distance", Double.class);
         ClientPlayerEntity var3 = MinecraftClient.getInstance().player;
         if (var3 != null) {
            var3.setPosition(var3.getX(), var3.getY() - var1x + 0.1, var3.getZ());
            ChatLogger.method2020(ChatLogger.Type.INFO, "Вклип вниз на " + var1x + " блоков");
         }

         return 1;
      })));
      var1.then(literal("forward").then(arg("distance", CoordinateArgumentType.create()).executes(var0 -> {
         double var1x = (Double)var0.getArgument("distance", Double.class);
         ClientPlayerEntity var3 = MinecraftClient.getInstance().player;
         if (var3 != null) {
            double var4 = Math.toRadians(var3.getYaw());
            var3.setPosition(var3.getX() - Math.sin(var4) * var1x, var3.getY() + 0.1, var3.getZ() + Math.cos(var4) * var1x);
            ChatLogger.method2020(ChatLogger.Type.INFO, "Вклип вперед на " + var1x + " блоков");
         }

         return 1;
      })));
      var1.then(literal("back").then(arg("distance", CoordinateArgumentType.create()).executes(var0 -> {
         double var1x = (Double)var0.getArgument("distance", Double.class);
         ClientPlayerEntity var3 = MinecraftClient.getInstance().player;
         if (var3 != null) {
            double var4 = Math.toRadians(var3.getYaw());
            var3.setPosition(var3.getX() + Math.sin(var4) * var1x, var3.getY() + 0.1, var3.getZ() - Math.cos(var4) * var1x);
            ChatLogger.method2020(ChatLogger.Type.INFO, "Вклип назад на " + var1x + " блоков");
         }

         return 1;
      })));
      var1.then(literal("help").executes(var0 -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, "Использование: .clip <vclip/hclip/up/down/forward/back/help> [расстояние]");
         ChatLogger.method2020(ChatLogger.Type.INFO, "Примеры:");
         ChatLogger.method2020(ChatLogger.Type.INFO, ".clip vclip 10 - вклип вверх на 10 блоков");
         ChatLogger.method2020(ChatLogger.Type.INFO, ".clip hclip 5 - горизонтальный вклип на 5 блоков");
         ChatLogger.method2020(ChatLogger.Type.INFO, ".clip up 3 - вклип вверх на 3 блока");
         return 1;
      }));
   }
}

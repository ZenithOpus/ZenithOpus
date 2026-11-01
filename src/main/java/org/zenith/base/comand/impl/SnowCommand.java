package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.utility.game.SnowStats;
import org.zenith.utility.misc.Class32;
import org.zenith.utility.misc.MeshImporter;
import org.zenith.utility.misc.MeshValidator;
import org.zenith.utility.misc.SnowVisibility;
import org.zenith.utility.render.SnowShader;

public class SnowCommand extends CommandAbstract {
   private static final Class32 VOXELS = new Class32();

   public SnowCommand() {
      super("snow");
   }

   public static Class32 voxels() {
      return VOXELS;
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.executes(var0 -> {
         ChatLogger.method2020(ChatLogger.Type.INFO, "snow fill [радиус] | snow stats | snow check | snow clear");
         return 1;
      });
      var1.then(
         ((LiteralArgumentBuilder)literal("fill").executes(var1x -> this.fill(24)))
            .then(arg("radius", IntegerArgumentType.integer(1, 64)).executes(var1x -> this.fill(IntegerArgumentType.getInteger(var1x, "radius"))))
      );
      var1.then(literal("stats").executes(var1x -> {
         this.stats();
         return 1;
      }));
      var1.then(literal("check").executes(var1x -> {
         this.check();
         return 1;
      }));
      var1.then(
         ((LiteralArgumentBuilder)literal("mesh").executes(var1x -> this.mesh(24)))
            .then(arg("radius", IntegerArgumentType.integer(1, 64)).executes(var1x -> this.mesh(IntegerArgumentType.getInteger(var1x, "radius"))))
      );
      var1.then(literal("show").executes(var0 -> {
         SnowVisibility.setVisible(!SnowVisibility.isVisible());
         ChatLogger.method2020(ChatLogger.Type.INFO, "Снег: отладочная отрисовка " + (SnowVisibility.isVisible() ? "включена" : "выключена"));
         return 1;
      }));
      var1.then(
         literal("shader")
            .executes(
               var0 -> {
                  ChatLogger.method2020(
                     ChatLogger.Type.INFO,
                     SnowShader.method390() ? "Снег: шейдер загружен, слой идёт через него" : "Снег: шейдер НЕ загружен (" + SnowShader.method389() + ")"
                  );
                  return 1;
               }
            )
      );
      var1.then(literal("screen").executes(var0 -> {
         boolean var1x = SnowShader.method388();
         ChatLogger.method2020(ChatLogger.Type.INFO, "Снег: экранный слой " + (var1x ? "включён" : "выключен"));
         return 1;
      }));
      var1.then(literal("clear").executes(var0 -> {
         VOXELS.clear();
         ChatLogger.method2020(ChatLogger.Type.INFO, "Снег: хранилище очищено");
         return 1;
      }));
   }

   private int fill(int var1) {
      ClientPlayerEntity var2 = MinecraftClient.getInstance().player;
      if (var2 != null && var2.getEntityWorld() != null) {
         VOXELS.clear();
         long var3 = System.nanoTime();
         SnowStats.Entry var5 = SnowStats.method803(var2.getEntityWorld(), VOXELS, var2.getBlockX(), var2.getBlockY(), var2.getBlockZ(), var1, 0.35F);
         long var6 = (System.nanoTime() - var3) / 1000L;
         ChatLogger.method2020(ChatLogger.Type.INFO, "Снег: " + var5 + ", за " + var6 + " мкс");
         this.stats();
         return 1;
      } else {
         ChatLogger.method2020(ChatLogger.Type.WARN, "Снег: нет мира");
         return 1;
      }
   }

   private int mesh(int var1) {
      ClientPlayerEntity var2 = MinecraftClient.getInstance().player;
      if (var2 == null) {
         ChatLogger.method2020(ChatLogger.Type.WARN, "Снег: нет мира");
         return 1;
      } else if (VOXELS.method4906() == 0) {
         ChatLogger.method2020(ChatLogger.Type.WARN, "Снег: хранилище пустое, сначала snow fill");
         return 1;
      } else {
         int var3 = var1 * 2 * 4;
         int var4 = Class32.method4911(var2.getBlockX() - var1);
         int var5 = Class32.method4911(var2.getBlockY() - 8);
         int var6 = Class32.method4911(var2.getBlockZ() - var1);
         byte var7 = 96;
         long var8 = System.nanoTime();
         MeshImporter.Entry var10 = MeshImporter.method658(VOXELS, SnowVisibility.getFloatArrayBuffer(), var4, var5, var6, var3, var7, var3);
         long var11 = (System.nanoTime() - var8) / 1000L;
         SnowVisibility.method2401(var4, var5, var6);
         ChatLogger.method2020(
            ChatLogger.Type.INFO,
            "Снег: сетка — "
               + var10
               + ", квадратов "
               + SnowVisibility.getFloatArrayBuffer().getIntValue()
               + ", память "
               + SnowVisibility.getFloatArrayBuffer().method386() / 1024L
               + " КБ, за "
               + var11
               + " мкс"
         );
         return 1;
      }
   }

   private void stats() {
      ChatLogger.method2020(
         ChatLogger.Type.INFO,
         "Снег: чанков "
            + VOXELS.method4906()
            + " (однородных "
            + VOXELS.method4907()
            + "), вокселей "
            + VOXELS.method4908()
            + ", память "
            + VOXELS.method4909() / 1024L
            + " КБ"
      );
   }

   private void check() {
      List<String> var1 = MeshValidator.method6095(VOXELS);
      if (var1.isEmpty()) {
         ChatLogger.method2020(ChatLogger.Type.INFO, "Снег: проверка пройдена, ошибок нет");
      } else {
         ChatLogger.method2020(ChatLogger.Type.WARN, "Снег: найдено проблем — " + var1.size());

         for (Object var3_o : (Iterable<?>)(Object)(var1)) {
            String var3 = (String)var3_o;
            ChatLogger.method2020(ChatLogger.Type.WARN, "  " + var3);
         }
      }
   }
}

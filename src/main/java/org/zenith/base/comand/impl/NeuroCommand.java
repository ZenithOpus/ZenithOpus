package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandSource;
import org.zenith.ChatLogger;
import org.zenith.Zenith;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.NeuroArgumentType;

public class NeuroCommand extends CommandAbstract {
   public static final File neuroDirectory = new File(Zenith.file, "neuro");

   public NeuroCommand() {
      super("neuro");
      ensureNeuroDirectory();
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.then(literal("load").then(arg("name", NeuroArgumentType.create()).executes(var0 -> {
         String var1x = (String)var0.getArgument("name", String.class);
         File var2 = findNeuroFile(var1x);
         if (var2 == null) {
            ChatLogger.method2020(ChatLogger.Type.WARN, "Файл нейромодели не найден");
            return 1;
         } else {
            try {
               Zenith.getInstance().getJointCodebook().method2533(var2);
               ChatLogger.method2020(ChatLogger.Type.INFO, "Нейромодель загружена: " + var2.getName());
            } catch (IOException var4) {
               ChatLogger.method2020(ChatLogger.Type.WARN, "Ошибка при загрузке нейромодели");
            }

            return 1;
         }
      })));
      var1.then(literal("dir").executes(var0 -> {
         if (!ensureNeuroDirectory()) {
            ChatLogger.method2020(ChatLogger.Type.WARN, "Ошибка при создании папки нейромоделей");
            return 1;
         } else {
            try {
               Runtime.getRuntime().exec(new String[]{"explorer", neuroDirectory.getAbsolutePath()});
            } catch (IOException var2) {
               ChatLogger.method2020(ChatLogger.Type.WARN, "Ошибка при открытии папки нейромоделей");
            }

            return 1;
         }
      }));
   }

   public static List<String> neuroNames() {
      if (!ensureNeuroDirectory()) {
         return new ArrayList<>();
      } else {
         File[] var0 = neuroDirectory.listFiles();
         ArrayList<String> var1 = new ArrayList<>();
         if (var0 != null) {
            for (File var5 : var0) {
               if (var5.isFile()) {
                  var1.add(var5.getName());
               }
            }
         }

         var1.sort(String.CASE_INSENSITIVE_ORDER);
         return var1;
      }
   }

   private static boolean ensureNeuroDirectory() {
      return neuroDirectory.isDirectory() || !neuroDirectory.exists() && neuroDirectory.mkdirs();
   }

   private static File findNeuroFile(String var0) {
      if (var0 != null && !var0.isBlank()) {
         try {
            File var1 = neuroDirectory.getCanonicalFile();
            File var2 = new File(var1, var0).getCanonicalFile();
            return var2.toPath().startsWith(var1.toPath()) && var2.isFile() ? var2 : null;
         } catch (IOException var3) {
            return null;
         }
      } else {
         return null;
      }
   }
}

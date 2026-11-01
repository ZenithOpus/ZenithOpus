package org.zenith.base.comand.impl;

import com.google.gson.JsonElement;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.JsonOps;
import java.util.List;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.zenith.ChatLogger;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.PlayerArgumentType;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.client.modules.impl.misc.StreamerModeModule;
import org.zenith.utility.misc.TextUtils;

public class NameProtectCommand extends CommandAbstract {
   public NameProtectCommand() {
      super("nameprotect");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      ((LiteralArgumentBuilder)var1.then(literal("parse").then(arg("player", PlayerArgumentType.createDisplayed()).executes(var0 -> {
         var var1x = mc.inGameHud.getPlayerListHud().collectPlayerEntries();
         String var2 = (String)var0.getArgument("player", String.class);

         for (int var3 = 0; var3 < var1x.size(); var3++) {
            PlayerListEntry var4 = (PlayerListEntry)var1x.get(var3);
            String var5 = var4.getProfile().name();
            String var6 = StreamerModeModule.INSTANCE.method6291(var5);
            if (var5.equals(var2) || var6.equals(var2)) {
               Text var7 = mc.inGameHud.getPlayerListHud().getPlayerName(var4);
               MutableText var8 = TextUtils.method2113(var7, var6);
               if (var8.getString().isEmpty()) {
                  ChatLogger.method2019("У данного игрока видимо нету доната");
                  return 1;
               }

               JsonElement var9 = (JsonElement)TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, var8).result().get();
               NameProtectModule.INSTANCE.method5151(var9, var3);
            }
         }

         return 1;
      })))).executes(var0 -> {
         ChatLogger.method2019("Использование .nameprotect parse nick и .nameprotect clear ");
         return 1;
      });
      var1.then(literal("clear").executes(var0 -> {
         NameProtectModule.INSTANCE.method5152();
         return 1;
      }));
   }
}

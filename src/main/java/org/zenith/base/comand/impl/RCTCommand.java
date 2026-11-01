package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.zenith.Zenith;
import org.zenith.ZenithAccess;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.utility.misc.HolyWorldServerTracker;
import org.zenith.utility.misc.HudHolder;

public class RCTCommand extends CommandAbstract implements ZenithAccess {
   public RCTCommand() {
      super("rct");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.executes(var1x -> {
         HolyWorldServerTracker var2 = this.repository();
         if (!var2.isHolyWorldHere()) {
            HudHolder.method6969().method6972("0", Text.literal(" Не работает на этом " + Formatting.RED + "сервере"));
            return 1;
         } else if (var2.method6149()) {
            HudHolder.method6969().method6972("️0", Text.literal(" Вы находитесь в режиме " + Formatting.RED + "пвп"));
            return 1;
         } else {
            var2.reconnect(var2.currentAnarchyHere());
            return 1;
         }
      });
      var1.then(CommandAbstract.arg("anarchy", IntegerArgumentType.integer(1, 69)).executes(var1x -> {
         HolyWorldServerTracker var2 = this.repository();
         if (!var2.isHolyWorldHere()) {
            HudHolder.method6969().method6972("0", Text.literal(" Не работает на этом " + Formatting.RED + "сервере"));
            return 1;
         } else if (var2.method6149()) {
            HudHolder.method6969().method6972("0", Text.literal(" Вы находитесь в режиме " + Formatting.RED + "пвп"));
            return 1;
         } else {
            int var3 = (Integer)var1x.getArgument("anarchy", Integer.class);
            var2.reconnect(var3);
            return 1;
         }
      }));
   }

   private HolyWorldServerTracker repository() {
      return Zenith.getInstance().getHolyWorldServerTracker();
   }
}

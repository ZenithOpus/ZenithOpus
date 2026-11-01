package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import lombok.Generated;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.StopWatch;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.misc.ScoreboardUtils;

@ModuleInfo(
   name = "AutoPay",
   category = ModuleCategory.MISC,
   description = "module.autopay.description"
)
public final class AutoPayModule extends Module {
   public static final AutoPayModule INSTANCE = new AutoPayModule();
   private static final long CONST_longValue = 5000L;
   private final TextSetting nick = new TextSetting("autopay.nick", "", "autopay.nick.empty", TextSetting.Entry.method2125(16));
   private final TextSetting maxAmount = new TextSetting(
      "autopay.maxAmount",
      "100000",
      "autopay.maxAmount.empty",
      TextSetting.Entry.method2126(12, var0 -> var0.isEmpty() || var0.chars().allMatch(Character::isDigit))
   );
   private final StopWatch stopWatch = new StopWatch();
   private long longValue = -1L;

   private AutoPayModule() {
   }

   @Override
   public void onEnable() {
      this.longValue = -1L;
      this.stopWatch.reset();
      super.onEnable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null) {
         long var2 = ScoreboardUtils.method5530(mc.world.getScoreboard(), mc.player.getNameForScoreboard());
         if (var2 >= 0L) {
            this.longValue = var2;
         }

         if (this.longValue >= 0L) {
            String var4 = this.nick.getValue().trim();
            long var5 = method1220(this.maxAmount.getValue().trim());
            if (!var4.isEmpty() && var5 >= 0L) {
               long var7 = this.longValue - var5;
               if (var7 > 0L && this.stopWatch.method7546(5000L)) {
                  mc.player.networkHandler.sendChatCommand("pay " + var4 + " " + var7);
                  this.stopWatch.reset();
               }
            }
         }
      }
   }

   private static long method1220(String var0) {
      if (var0.isEmpty()) {
         return -1L;
      } else {
         try {
            return Long.parseLong(var0);
         } catch (NumberFormatException var2) {
            return -1L;
         }
      }
   }

   @Generated
   public long getLongValue2() {
      return this.longValue;
   }
}

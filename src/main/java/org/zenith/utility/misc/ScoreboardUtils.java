package org.zenith.utility.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;

public final class ScoreboardUtils {
   private static final Pattern pattern = Pattern.compile("Монеток:\\s*([\\d][\\d\\s\\u00A0.,]*)([kкKК]*)");

   private ScoreboardUtils() {
   }

   public static long method5530(Scoreboard var0, String var1) {
      if (var0 == null) {
         return -1L;
      } else {
         ScoreboardObjective var2 = null;
         if (var1 != null) {
            Team var3 = var0.getScoreHolderTeam(var1);
            if (var3 != null) {
               ScoreboardDisplaySlot var4 = ScoreboardDisplaySlot.fromFormatting(var3.getColor());
               if (var4 != null) {
                  var2 = var0.getObjectiveForSlot(var4);
               }
            }
         }

         if (var2 == null) {
            var2 = var0.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
         }

         if (var2 == null) {
            return -1L;
         } else {
            for (ScoreboardEntry var10 : var0.getScoreboardEntries(var2)) {
               if (!var10.hidden()) {
                  Team var5 = var0.getScoreHolderTeam(var10.owner());
                  String var6 = method5532(Team.decorateName(var5, var10.name()).getString());
                  long var7 = method5531(var6);
                  if (var7 >= 0L) {
                     return var7;
                  }
               }
            }

            return -1L;
         }
      }
   }

   public static long method5531(String var0) {
      Matcher var1 = pattern.matcher(var0);
      if (!var1.find()) {
         return -1L;
      } else {
         try {
            String var2 = var1.group(1).replaceAll("[\\s\\u00A0]", "").replace(',', '.');
            String var3 = var1.group(2);
            long var4 = var2.chars().filter(var0x -> var0x == 46).count();
            if (var4 > 1L || var4 == 1L && var3.isEmpty() && var2.length() - var2.indexOf(46) - 1 == 3) {
               var2 = var2.replace(".", "");
            }

            double var6 = Double.parseDouble(var2);

            for (int var8 = 0; var8 < var3.length(); var8++) {
               var6 *= 1000.0;
            }

            return Math.round(var6);
         } catch (Exception var9) {
            return -1L;
         }
      }
   }

   public static String method5532(String var0) {
      StringBuilder var1 = new StringBuilder(var0.length());

      for (int var2 = 0; var2 < var0.length(); var2++) {
         char var3 = var0.charAt(var2);
         if (var3 == 167) {
            var2++;
         } else {
            var1.append(var3);
         }
      }

      return var1.toString();
   }
}

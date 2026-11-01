package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.misc.ScoreboardParser;
import org.zenith.utility.misc.TextUtils;

@ModuleInfo(
   name = "StreamerMode",
   category = ModuleCategory.MISC,
   description = "module.streamermode.description"
)
public final class StreamerModeModule extends Module {
   public static final StreamerModeModule INSTANCE = new StreamerModeModule();
   private static final String[] texts = new String[]{
      "Dark",
      "Shadow",
      "Night",
      "Ice",
      "Fire",
      "Storm",
      "Ghost",
      "Sky",
      "Red",
      "Black",
      "White",
      "Mega",
      "Pro",
      "Lord",
      "King",
      "Big",
      "Crazy",
      "Toxic",
      "Demon",
      "Star",
      "Moon",
      "Blood",
      "Steel",
      "Frost",
      "Hyper",
      "Mad",
      "Evil",
      "Gold",
      "Neo",
      "Top"
   };
   private static final String[] texts2 = new String[]{
      "Wolf",
      "Killer",
      "Hunter",
      "Player",
      "Gamer",
      "Craft",
      "Boy",
      "Man",
      "Cat",
      "Fox",
      "Knight",
      "Tiger",
      "Ninja",
      "Dragon",
      "Reaper",
      "Master",
      "Warrior",
      "Legend",
      "Sniper",
      "Panda",
      "Bear",
      "Zombie",
      "Creeper",
      "God",
      "PvP",
      "Dog",
      "Bro",
      "Kid",
      "Rex",
      "Ace"
   };
   private final List<StreamerModeModule.Data> items = new ArrayList<>();
   private final Map<String, Integer> items2 = new HashMap<>();
   private boolean flag;
   private StreamerModeModule.Type type = StreamerModeModule.Type.IDLE;
   private int intValue = -1;
   private int intValue2 = -1;
   private int intValue3 = -1;
   @Nullable
   private Text text;
   @Nullable
   private Text text2;
   @Nullable
   private Text text3;
   @Nullable
   private Text text4;
   @Nullable
   private String text5;
   private final StopWatch stopWatch = new StopWatch();
   private static final int CONST_intValue = 1024;
   private final Map<String, String> items3 = new HashMap<>();
   private final List<String> items4 = new ArrayList<>();

   private StreamerModeModule() {
   }

   public void method6283(int var1) {
      if (!this.isEnabled()) {
         ChatLogger.method2021("Сначала включи StreamerMode");
      } else if (mc.player != null && mc.player.networkHandler != null) {
         if (this.type != StreamerModeModule.Type.IDLE) {
            ChatLogger.method2021("Парс уже идёт");
         } else {
            int var2 = Zenith.getInstance().getServerDetector().getAnarchy();
            if (var2 == -1) {
               ChatLogger.method2022("Не вижу номер текущей анархии, зайди на анку");
            } else if (var2 == var1) {
               ChatLogger.method2022("Ты уже на ан" + var1 + ", выбери другую");
            } else {
               this.intValue2 = var2;
               this.intValue = var1;
               this.flag = false;
               this.method6285();
               this.type = StreamerModeModule.Type.GO_TARGET;
               this.stopWatch.reset();
               this.method6286(var1);
               ChatLogger.method2019("Иду на ан" + var1 + " за табом, потом вернусь на ан" + var2);
            }
         }
      }
   }

   public void method6284() {
      this.items.clear();
      this.items2.clear();
      this.flag = false;
      this.method6285();
      this.type = StreamerModeModule.Type.IDLE;
      ChatLogger.method2019("Спаршенный таб очищен");
   }

   private void method6285() {
      this.intValue3 = -1;
      this.text = null;
      this.text2 = null;
      this.text3 = null;
      this.text4 = null;
      this.text5 = null;
   }

   public int getIntValue32() {
      return this.intValue3;
   }

   @Nullable
   public Text getText3() {
      return this.text;
   }

   @Nullable
   public Text getText22() {
      return this.text2;
   }

   @Nullable
   public Text getText32() {
      return this.text3;
   }

   @Nullable
   public Text getText4() {
      return this.text4;
   }

   @Nullable
   public String getText5() {
      return this.text5;
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (this.type != StreamerModeModule.Type.IDLE && mc.player != null && mc.world != null) {
         int var2 = Zenith.getInstance().getServerDetector().getAnarchy();
         switch (this.type) {
            case GO_TARGET:
               if (var2 == this.intValue) {
                  this.type = StreamerModeModule.Type.WAIT_TAB;
                  this.stopWatch.reset();
               } else if (this.stopWatch.method7546(60000L)) {
                  this.type = StreamerModeModule.Type.IDLE;
                  ChatLogger.method2022("Не дождался захода на ан" + this.intValue);
               }
               break;
            case WAIT_TAB:
               int var3 = mc.player.networkHandler.getListedPlayerListEntries().size();
               if (this.stopWatch.method7546(5000L) && var3 > 1 || this.stopWatch.method7546(15000L)) {
                  this.method6287();
                  this.type = StreamerModeModule.Type.RETURN;
                  this.stopWatch.reset();
                  this.method6286(this.intValue2);
               }
               break;
            case RETURN:
               if (var2 == this.intValue2 || this.stopWatch.method7546(60000L)) {
                  this.type = StreamerModeModule.Type.IDLE;
                  this.flag = !this.items.isEmpty();
                  if (this.flag) {
                     ChatLogger.method2019("Спарсил " + this.items.size() + " ников с ан" + this.intValue + ", таб подменён");
                  } else {
                     ChatLogger.method2022("Таб на ан" + this.intValue + " спарсить не вышло");
                  }
               }
         }
      }
   }

   private void method6286(int var1) {
      if (Zenith.getInstance().getServerDetector().method1642()) {
         Zenith.getInstance().getHolyWorldServerTracker().reconnect(var1);
      } else {
         mc.player.networkHandler.sendChatCommand("an" + var1);
      }
   }

   private void method6287() {
      this.items.clear();
      this.items2.clear();
      this.method6285();
      String var1 = mc.player.getNameForScoreboard();

      for (PlayerListEntry var3 : mc.inGameHud.getPlayerListHud().collectPlayerEntries()) {
         String var4 = var3.getProfile().name();
         if (!var4.equals(var1)) {
            MutableText var5 = NameProtectModule.INSTANCE.method5146(var3).copy();
            this.items.add(new StreamerModeModule.Data(var4, var5, this.method6304(var5, var4)));
         }
      }

      this.method6288();
   }

   private void method6288() {
      if (mc.player != null && mc.world != null) {
         this.intValue3 = mc.player.networkHandler.getListedPlayerListEntries().size();
         PlayerListHud var1 = mc.inGameHud.getPlayerListHud();
         this.text3 = ScoreboardParser.method7082(var1.header);
         if (this.text3 == null) {
            this.text3 = ScoreboardParser.method7082(var1.footer);
         }

         this.text4 = ScoreboardParser.method7084(var1.header);
         if (this.text4 == null) {
            this.text4 = ScoreboardParser.method7084(var1.footer);
         }

         if (this.text4 != null) {
            int var2 = ScoreboardParser.method7085(this.text4);
            if (var2 > 0) {
               this.intValue3 = var2;
            }
         }

         Scoreboard var9 = mc.world.getScoreboard();
         ScoreboardObjective var3 = var9.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
         if (var3 != null) {
            for (ScoreboardEntry var5 : var9.getScoreboardEntries(var3)) {
               if (!var5.hidden()) {
                  Team var6 = var9.getScoreHolderTeam(var5.owner());
                  MutableText var7 = Team.decorateName(var6, var5.name());
                  if (ScoreboardParser.method7078(var7)) {
                     this.text = var7.copy();
                  } else if (ScoreboardParser.method7079(var7)) {
                     this.text2 = var7.copy();
                     int var8 = ScoreboardParser.method7085(var7);
                     if (var8 > 0) {
                        this.intValue3 = var8;
                     }
                  }
               }
            }
         }

         this.text5 = ScoreboardParser.method7083(this.text);
         if (this.text5 == null) {
            this.text5 = ScoreboardParser.method7083(this.text3);
         }
      }
   }

   public boolean method6289() {
      return this.isEnabled() && this.flag && this.type == StreamerModeModule.Type.IDLE && !this.items.isEmpty();
   }

   public Text method6290(PlayerListEntry var1) {
      if (this.method6289() && var1 != null && mc.player != null) {
         String var2 = var1.getProfile().name();
         if (var2.equals(mc.player.getNameForScoreboard())) {
            return null;
         } else {
            StreamerModeModule.Data var3 = this.method6297(var2);
            return var3 == null ? null : var3.getText2().copy();
         }
      } else {
         return null;
      }
   }

   public String method6291(String var1) {
      if (!this.method6289() || var1 == null || mc.player == null) {
         return var1;
      } else if (var1.equals(mc.player.getNameForScoreboard())) {
         return var1;
      } else {
         StreamerModeModule.Data var2 = this.method6297(var1);
         return var2 == null ? var1 : var2.getText();
      }
   }

   public Text method6292(Text var1, String var2) {
      if (!this.method6289() || var1 == null || var2 == null || mc.player == null) {
         return var1;
      } else if (var2.equals(mc.player.getNameForScoreboard())) {
         return var1;
      } else {
         StreamerModeModule.Data var3 = this.method6297(var2);
         return (Text)(var3 == null ? var1 : TextUtils.method2107(var1, var2, var3.getText3().copy()));
      }
   }

   public Text method6293(Text var1) {
      if (this.method6289() && var1 != null && mc.player != null && mc.player.networkHandler != null) {
         String var2 = this.method6294(var1.getString());
         return var2 == null ? var1 : this.method6292(var1, var2);
      } else {
         return var1;
      }
   }

   private String method6294(String var1) {
      this.method6295();
      if (this.items3.containsKey(var1)) {
         return this.items3.get(var1);
      } else {
         String var2 = null;

         for (String var4 : this.items4) {
            if ((var2 == null || var4.length() > var2.length()) && var1.contains(var4)) {
               var2 = var4;
            }
         }

         if (this.items3.size() >= 1024) {
            this.items3.clear();
         }

         this.items3.put(var1, var2);
         return var2;
      }
   }

   private void method6295() {
      Collection<PlayerListEntry> var1 = mc.player.networkHandler.getListedPlayerListEntries();
      boolean var2 = var1.size() == this.items4.size();
      if (var2) {
         int var3 = 0;

         for (Object var5_o : (Iterable<?>)(Object)(var1)) {
            PlayerListEntry var5 = (PlayerListEntry)var5_o;
            if (!var5.getProfile().name().equals(this.items4.get(var3++))) {
               var2 = false;
               break;
            }
         }
      }

      if (!var2) {
         this.items4.clear();

         for (Object var7_o : (Iterable<?>)(Object)(var1)) {
            PlayerListEntry var7 = (PlayerListEntry)var7_o;
            this.items4.add(var7.getProfile().name());
         }

         this.items3.clear();
      }
   }

   public void method6296(List<PlayerListEntry> var1) {
      if (this.method6289()) {
         for (PlayerListEntry var3 : var1) {
            this.method6297(var3.getProfile().name());
         }

         var1.sort(Comparator.comparingInt(var1x -> this.items2.getOrDefault(var1x.getProfile().name(), Integer.MAX_VALUE)));
      }
   }

   private StreamerModeModule.Data method6297(String var1) {
      if (this.items.isEmpty()) {
         return null;
      } else {
         Integer var2 = this.items2.get(var1);
         if (var2 == null) {
            var2 = this.method6298();
            this.items2.put(var1, var2);
         }

         return this.items.get(var2);
      }
   }

   private int method6298() {
      HashSet var1 = new HashSet();
      if (mc.player != null && mc.player.networkHandler != null) {
         for (PlayerListEntry var3 : mc.player.networkHandler.getListedPlayerListEntries()) {
            Integer var4 = this.items2.get(var3.getProfile().name());
            if (var4 != null) {
               var1.add(var4);
            }
         }
      } else {
         var1.addAll(this.items2.values());
      }

      for (int var5 = 0; var5 < this.items.size(); var5++) {
         if (!var1.contains(var5)) {
            return var5;
         }
      }

      this.items.add(this.method6299());
      return this.items.size() - 1;
   }

   private StreamerModeModule.Data method6299() {
      String var1 = this.method6300();
      MutableText var2 = Text.literal(var1).setStyle(this.method6303());
      return new StreamerModeModule.Data(var1, var2, var2);
   }

   private String method6300() {
      ThreadLocalRandom var1 = ThreadLocalRandom.current();

      for (int var2 = 0; var2 < 100; var2++) {
         String var3 = this.method6301(var1);
         if (var3.length() <= 16 && !this.method6302(var3)) {
            return var3;
         }
      }

      String var4 = "Player" + var1.nextInt(1000, 10000);

      while (this.method6302(var4)) {
         var4 = "Player" + var1.nextInt(1000, 10000);
      }

      return var4;
   }

   private String method6301(ThreadLocalRandom var1) {
      String var2 = texts[var1.nextInt(texts.length)];
      String var3 = texts2[var1.nextInt(texts2.length)];

      return switch (var1.nextInt(7)) {
         case 0 -> var2 + var3;
         case 1 -> var2 + var3 + var1.nextInt(1, 100);
         case 2 -> var2 + var3 + var1.nextInt(2007, 2013);
         case 3 -> var2 + "_" + var3;
         case 4 -> (var2 + var3).toLowerCase(Locale.ROOT);
         case 5 -> "_" + var2 + var3 + "_";
         default -> "x" + var2 + var3 + "x";
      };
   }

   private boolean method6302(String var1) {
      for (StreamerModeModule.Data var3 : this.items) {
         if (var3.getText().equalsIgnoreCase(var1)) {
            return true;
         }
      }

      if (mc.player != null && mc.player.networkHandler != null) {
         for (PlayerListEntry var5 : mc.player.networkHandler.getListedPlayerListEntries()) {
            if (var5.getProfile().name().equalsIgnoreCase(var1)) {
               return true;
            }
         }
      }

      return false;
   }

   private Style method6303() {
      for (StreamerModeModule.Data var2 : this.items) {
         if (var2.getText3().getString().equals(var2.getText())) {
            Style[] var3 = new Style[1];
            var2.getText3().asOrderedText().accept((var1, var2x, var3x) -> {
               var3[0] = var2x;
               return false;
            });
            if (var3[0] != null) {
               return var3[0];
            }
         }
      }

      return Style.EMPTY;
   }

   private Text method6304(Text var1, String var2) {
      ArrayList var3 = new ArrayList();
      StringBuilder var4 = new StringBuilder();
      var1.asOrderedText().accept((var2x, var3x, var4x) -> {
         var3.add(Text.literal(new String(Character.toChars(var4x))).setStyle(var3x));
         var4.appendCodePoint(var4x);
         return true;
      });
      int var5 = var4.indexOf(var2);
      if (var5 == -1) {
         return var1.copy();
      } else {
         int var6 = var4.toString().codePointCount(0, var5 + var2.length());
         MutableText var7 = Text.empty();

         for (int var8 = 0; var8 < var6 && var8 < var3.size(); var8++) {
            var7.append((Text)var3.get(var8));
         }

         return var7;
      }
   }

   private static final class Data {
      private final String text;
      private final Text text2;
      private final Text text3;

      public String text() {
         return this.text;
      }

      public Text text2() {
         return this.text2;
      }

      public Text text3() {
         return this.text3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, text3);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + "]";
      }

      private Data(String var1, Text var2, Text var3) {
         this.text = var1;
         this.text2 = var2;
         this.text3 = var3;
      }

      public String getText() {
         return this.text;
      }

      public Text getText2() {
         return this.text2;
      }

      public Text getText3() {
         return this.text3;
      }
   }

   private static enum Type {
      IDLE,
      GO_TARGET,
      WAIT_TAB,
      RETURN;
   }
}

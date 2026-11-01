package org.zenith.client.hud;

import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.modules.impl.misc.NameProtectModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.misc.ScoreboardParser;
import org.zenith.utility.misc.TextUtils;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class ScoreboardHud extends Interface {
   private static final String text = "k";
   private Text text2;
   private int intValue;
   private Text text3;

   public ScoreboardHud(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void method8(CustomDrawContext var1) {
      Scoreboard var2 = mc.world.getScoreboard();
      ScoreboardObjective var3 = null;
      Team var4 = var2.getScoreHolderTeam(mc.player.getNameForScoreboard());
      if (var4 != null) {
         ScoreboardDisplaySlot var5 = ScoreboardDisplaySlot.fromFormatting(var4.getColor());
         if (var5 != null) {
            var3 = var2.getObjectiveForSlot(var5);
         }
      }

      ScoreboardObjective var6 = var3 != null ? var3 : var2.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
      if (var6 != null) {
         this.method2665(var1, var6);
      } else {
         this.height = 0.0F;
      }
   }

   private void method2665(CustomDrawContext var1, ScoreboardObjective var2) {
      Scoreboard var3 = var2.getScoreboard();
      Font var4 = HudListLayout.nameFont();
      Font var5 = HudListLayout.method7631();
      Font var6 = HudListLayout.method7630();
      NumberFormat var7 = var2.getNumberFormatOr(StyledNumberFormat.RED);
      List<ScoreboardHud.Entry> var8 = var3.getScoreboardEntries(var2)
         .stream()
         .filter(var0 -> !var0.hidden())
         .sorted(InGameHud.SCOREBOARD_ENTRY_COMPARATOR)
         .limit(15L)
         .map(var3x -> {
            Team var4x = var3.getScoreHolderTeam(var3x.owner());
            MutableText var5x = Team.decorateName(var4x, var3x.name());
            Text var6x = ScoreboardParser.method7076(var5x);
            if (var6x == null) {
               return null;
            } else {
               MutableText var7x = var3x.formatted(var7);
               float var8x = var4.width(var7x);
               return new ScoreboardHud.Entry(var6x, var7x, (int)var8x);
            }
         })
         .filter(var0 -> var0 != null)
         .collect(Collectors.toList());
      ZenithStyle var9 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      Text var10 = this.method2668(var2.getDisplayName(), var9);
      float var11 = var5.width(var10);
      float var12 = 0.0F;

      for (Object var14_o : (Iterable<?>)(Object)(var8)) {
         ScoreboardHud.Entry var14 = (ScoreboardHud.Entry)var14_o;
         var12 = Math.max(var12, var4.width(var14.name()));
      }

      float var31 = 16.5F + HudListLayout.dividerWidth() + var11 + HudListLayout.CONST_floatValue7 + var6.width("m") + 6.0F;
      float var32 = 12.0F + var12;
      int var15 = var8.size();
      byte var16 = 9;
      byte var17 = 20;
      int var18 = var15 * var16;
      int var19 = (int)this.floatValue2;
      int var20 = var19 + var16;
      int var21 = var19 + var17;
      int var22 = method2667(var8, var20, var16, var21);
      int var23 = (int)(this.floatValue + 6.0F);
      this.width = Math.max(var31, var32);
      this.height = Math.max(var17, var18 + 10 + var22);
      HudListLayout.method7635(var1, this.getX(), this.getY(), this.width, this.height);
      this.method2666(var1, var9, this.getX(), var19, this.width, var5, var6, var10);
      this.method288(var1.getScaledWindowWidth(), var1.getScaledWindowHeight());
      int var24 = 0;

      for (int var25 = 0; var25 < var15; var25++) {
         ScoreboardHud.Entry var26 = (ScoreboardHud.Entry)var8.get(var25);
         int var27 = var20 + var25 * var16 + var24;
         Object var28 = var26.name();
         boolean var29 = var26.flag;
         if (NameProtectModule.INSTANCE.isEnabled()) {
            Object var30 = var28;
            if (var26.text3.contains(mc.player.getNameForScoreboard())) {
               var28 = TextUtils.method2110((Text)var28, mc.player.getNameForScoreboard(), NameProtectModule.method5141());
            } else if (NameProtectModule.INSTANCE.getText() != null) {
               if (var26.text3.contains("Группа:")) {
                  var28 = TextUtils.method2114((Text)var28, "Группа:", NameProtectModule.INSTANCE.getText());
               } else if (var26.text3.contains("Ранг:")) {
                  var28 = TextUtils.method2114((Text)var28, "Ранг:", NameProtectModule.INSTANCE.getText());
               }
            }

            if (var28 != var30) {
               var29 = method2669(((net.minecraft.text.Text)var28).getString());
            }
         }

         if (var29 && var27 < var21) {
            int var33 = var21 - var27;
            var24 += var33;
            var27 += var33;
         }

         var1.drawText(var4, (Text)var28, var23, var27 + (var16 - var4.height()) / 2.0F, var9.getTextEnable().method1445());
      }
   }

   private void method2666(CustomDrawContext var1, ZenithStyle var2, float var3, float var4, float var5, Font var6, Font var7, Text var8) {
      float var9 = var4 + 3.5F;
      var1.drawRoundedRect(var3 + 3.5F, var9, 13.0F, 13.0F, ProxyDotRadiusData.method8069(3.9F), var2.getPrimaryColor().getZenithColor());
      HudListLayout.method7637(var1, var7, "k", var3 + 3.5F, var9, 13.0F, ZenithColor.INSTANCE);
      float var10 = HudListLayout.method7634(var1, var3 + 3.5F + 13.0F, var4, 20.0F);
      var1.drawText(var6, var8, var10, var4 + (20.0F - var6.height()) / 2.0F);
      var1.drawText(var7, "m", var3 + var5 - 6.0F - var7.width("m"), var4 + (20.0F - var7.height()) / 2.0F, var2.getTextTertiary().getZenithColor());
   }

   private static int method2667(List<ScoreboardHud.Entry> var0, int var1, int var2, int var3) {
      int var4 = 0;

      for (int var5 = 0; var5 < var0.size(); var5++) {
         int var6 = var1 + var5 * var2 + var4;
         if (((ScoreboardHud.Entry)var0.get(var5)).flag && var6 < var3) {
            var4 += var3 - var6;
         }
      }

      return var4;
   }

   private Text method2668(Text var1, ZenithStyle var2) {
      int var3 = var2.getTextEnable().method1445();
      if (this.text3 != null && var1 == this.text2 && var3 == this.intValue) {
         return this.text3;
      } else {
         MutableText var4;
         if (var1.getSiblings().isEmpty()) {
            var4 = var1.copy().setStyle(Style.EMPTY.withColor(var2.getTextEnable().getZenithColor().method7287()));
         } else {
            MutableText var5 = Text.literal("");
            MutableText var6 = MutableText.of(var1.getContent());
            if (!var6.getString().isEmpty()) {
               var5.append(var6.setStyle(var1.getStyle().getColor() == null ? Style.EMPTY.withColor(var3) : var1.getStyle()));
            }

            for (Text var8 : var1.getSiblings()) {
               var5.append(var8.copy().setStyle(var8.getStyle().getColor() == null ? Style.EMPTY.withColor(var3) : var8.getStyle()));
            }

            var4 = var5;
         }

         this.text2 = var1;
         this.intValue = var3;
         this.text3 = var4;
         return var4;
      }
   }

   private static boolean method2669(String var0) {
      if (var0.isBlank()) {
         return false;
      } else {
         for (int var1 = 0; var1 < var0.length(); var1++) {
            if (var0.charAt(var1) == 167) {
               var1++;
            } else if (!Character.isWhitespace(var0.charAt(var1))) {
               return true;
            }
         }

         return false;
      }
   }

   @Environment(EnvType.CLIENT)
   private static final class Entry {
      private final Text text;
      private final Text text2;
      private final int intValue;
      private final String text3;
      private final boolean flag;

      private Entry(Text var1, Text var2, int var3) {
         this.text = var1;
         this.text2 = var2;
         this.intValue = var3;
         this.text3 = var1.getString();
         this.flag = ScoreboardHud.method2669(this.text3);
      }

      public Text name() {
         return this.text;
      }

      public Text score() {
         return this.text2;
      }

      public int scoreWidth() {
         return this.intValue;
      }
   }
}

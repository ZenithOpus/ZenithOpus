package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.api.Share;
import org.zenith.utility.misc.ServerNameUtils;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class WeekBlock {
   private static final float INSET = 8.0F;
   private static final long REFRESH_MS = 30000L;
   private static final ZenithColor UP = new ZenithColor(-11870592);
   private static final ZenithColor DOWN = new ZenithColor(-495247);
   private static final ZenithColor[] MEDALS = new ZenithColor[]{new ZenithColor(-278748), new ZenithColor(-3418655), new ZenithColor(-2525434)};
   private final FriendsSectionPanel host;
   private final Map<String, Animation> hover = new HashMap<>();
   private final Map<String, Integer> hovered = new HashMap<>();
   private final Animation fill = new Animation(700L, 0.0F, Easing.INSTANCE12);
   private int week;
   private int sort;
   private long requestedAt;

   public WeekBlock(FriendsSectionPanel var1) {
      this.host = var1;
   }

   public String signature(Cloud var1) {
      PartyMember.Data14 var2 = var1 == null ? null : var1.getPartyMember().getData14();
      return this.week + "|" + this.sort + "|" + (var2 == null ? 0L : var2.getLongValue());
   }

   public void tick(Cloud var1, boolean var2) {
      if (var2 && var1 != null && var1.method7860() && System.currentTimeMillis() - this.requestedAt > 30000L) {
         this.request(var1);
      }
   }

   public void opened(Cloud var1) {
      this.fill.reset(0.0F);
      this.fill.method2810(1.0F);
      if (var1 != null && var1.method7860()) {
         this.request(var1);
      }
   }

   private void request(Cloud var1) {
      this.requestedAt = System.currentTimeMillis();
      PartyMember.Data14 var2 = var1.getPartyMember().getData14();
      var1.getPartyMember().method1170(this.week == 1 && var2 != null ? var2.getText2() : "");
   }

   public void build(Cloud var1) {
      if (var1 != null && var1.method7860()) {
         PartyMember.Data14 var2 = var1.getPartyMember().getData14();
         if (var2 == null) {
            this.line(t("gui.party.stats.loading"));
         } else {
            this.chips("week", new String[]{t("gui.party.stats.thisWeek"), t("gui.party.stats.lastWeek")}, () -> this.week, var2x -> {
               this.week = var2x;
               this.request(var1);
               this.fill.reset(0.0F);
               this.fill.method2810(1.0F);
            });
            PartyMember.Data5 var3 = var2.method1219();
            PartyMember.Data9 var4 = var3 == null ? PartyMember.Data9.INSTANCE : var3.method1213();
            PartyMember.Data9 var5 = var3 == null ? PartyMember.Data9.INSTANCE : var3.method1214();
            this.hero(var4, var5);
            ArrayList<PartyMember.Data5> var6 = new ArrayList<>(var2.getItems2());
            boolean var7 = true;

            for (Object var9_o : (Iterable<?>)(Object)(var6)) {
               PartyMember.Data5 var9 = (PartyMember.Data5)var9_o;
               var7 &= var9.method1213().getLongValue() < 60000L && var9.method1213().getLongValue2() == 0L && var9.method1213().getLongValue3() == 0L;
            }

            if (var7) {
               this.line(t(this.week == 1 ? "gui.party.stats.emptyLast" : "gui.party.stats.empty"));
            } else if (var6.size() <= 1) {
               this.line(t("gui.party.stats.alone"));
            } else {
               this.host.caption(t("gui.party.caption.board") + " · " + var6.size());
               this.chips("sort", new String[]{t("gui.party.stats.byTime"), t("gui.party.stats.byKills")}, () -> this.sort, var1x -> this.sort = var1x);
               var6.sort(
                  (var1x, var2x) -> this.sort == 1
                     ? Long.compare(var2x.method1213().getLongValue2(), var1x.method1213().getLongValue2())
                     : Long.compare(var2x.method1213().getLongValue(), var1x.method1213().getLongValue())
               );
               long var13 = 1L;

               for (Object var11_o : (Iterable<?>)(Object)(var6)) {
                  PartyMember.Data5 var11 = (PartyMember.Data5)var11_o;
                  var13 = Math.max(var13, this.sort == 1 ? var11.method1213().getLongValue2() : var11.method1213().getLongValue());
               }

               for (int var14 = 0; var14 < var6.size(); var14++) {
                  PartyMember.Data5 var15 = (PartyMember.Data5)var6.get(var14);
                  float var12 = (float)(this.sort == 1 ? var15.method1213().getLongValue2() : var15.method1213().getLongValue()) / (float)var13;
                  this.row(var14, var15, var12);
               }
            }

            this.aside(var1, var2);
         }
      } else {
         this.line(t(var1 != null && var1.method7862() ? "gui.party.nodeOld" : "gui.party.offline"));
      }
   }

   private void chips(String var1, String[] var2, WeekBlock.Selected var3, WeekBlock.Choose var4) {
      this.host
         .custom(
            18.0F,
            (var4x, var5, var6, var7, var8, var9, var10, var11) -> {
               Font var12 = Fonts.NEW_MEDIUM.getFont(4.8F);
               float var13 = var7 + 8.0F;
               int var14 = -1;

               for (int var15 = 0; var15 < var2.length; var15++) {
                  float var16 = var12.width(var2[var15]) + 14.0F;
                  BoundsData var17 = new BoundsData(var13, var8 + 3.0F, var16, 12.0F);
                  boolean var18 = var17.contains(var5, var6);
                  if (var18) {
                     var14 = var15;
                  }

                  boolean var19 = var3.get() == var15;
                  float var20 = this.hover(var1 + var15, var18);
                  var4x.drawRoundedRect(
                     var17.getFloatValue(),
                     var17.getFloatValue2(),
                     var17.width(),
                     var17.height(),
                     ProxyDotRadiusData.method8069(6.0F),
                     (var19
                           ? var11.getPrimaryColor().getZenithColor().method7292(0.85F)
                           : var11.getDisableActiveBg().getZenithColor().method7294(0.7F + 0.3F * var20))
                        .method7294(var10)
                  );
                  var4x.drawText(
                     var12,
                     var2[var15],
                     var17.getFloatValue() + 7.0F,
                     var17.getFloatValue2() + (var17.height() - var12.height()) / 2.0F,
                     (var19 ? new ZenithColor(255, 255, 255) : var11.getTextSecondary().getZenithColor()).method7294(var10)
                  );
                  var13 += var16 + 4.0F;
               }

               this.hovered.put(var1, var14);
            },
            () -> {
               int var4x = this.hovered.getOrDefault(var1, -1);
               if (var4x >= 0 && var4x != var3.get()) {
                  var4.pick(var4x);
                  this.host.invalidate();
               }
            },
            null
         );
   }

   private void hero(PartyMember.Data9 var1, PartyMember.Data9 var2) {
      this.host
         .custom(
            46.0F,
            (var2x, var3, var4, var5, var6, var7, var8, var9) -> {
               float var10 = var5 + 8.0F;
               float var11 = var7 - 16.0F;
               float var12 = var6 + 3.0F;
               float var13 = 40.0F;
               var2x.drawRoundedRect(
                  var10, var12, var11, var13, ProxyDotRadiusData.method8069(4.0F), var9.getDisableActiveBg().getZenithColor().method7294(var8 * 0.5F)
               );
               String[] var14 = new String[]{hours(var1.getLongValue()), String.valueOf(var1.getLongValue2()), String.valueOf(var1.getLongValue3()), kd(var1)};
               String[] var15 = new String[]{t("gui.party.stats.played"), t("gui.party.stats.kills"), t("gui.party.stats.deaths"), t("gui.party.stats.kd")};
               String[] var16 = new String[]{
                  delta(var1.getLongValue(), var2.getLongValue()),
                  delta(var1.getLongValue2(), var2.getLongValue2()),
                  delta(var1.getLongValue3(), var2.getLongValue3()),
                  ""
               };
               float var17 = var11 / 4.0F;
               Font var18 = Fonts.NEW_MEDIUM.getFont(7.5F);
               Font var19 = Fonts.NEW_REGULAR.getFont(4.4F);

               for (int var20 = 0; var20 < 4; var20++) {
                  float var21 = var10 + var17 * var20 + 8.0F;
                  var2x.drawText(var18, var14[var20], var21, var12 + 7.0F, var9.getTextEnable().getZenithColor().method7294(var8));
                  float var22 = var12 + 7.0F + var18.height() + 3.0F;
                  var2x.drawText(var19, var15[var20], var21, var22, var9.getTextTertiary().getZenithColor().method7294(var8));
                  if (!var16[var20].isEmpty()) {
                     boolean var23 = var16[var20].startsWith("+");
                     boolean var24 = var20 == 2 ? !var23 : var23;
                     var2x.drawText(var19, var16[var20], var21 + var19.width(var15[var20]) + 4.0F, var22, (var24 ? UP : DOWN).method7294(var8));
                  }

                  if (var20 > 0) {
                     var2x.drawRect(var10 + var17 * var20, var12 + 8.0F, 0.5F, var13 - 16.0F, var9.getTextTertiary().getZenithColor().method7294(var8 * 0.3F));
                  }
               }
            }
         );
   }

   private void row(int var1, PartyMember.Data5 var2, float var3) {
      this.host
         .custom(
            19.0F,
            (var4, var5, var6, var7, var8, var9, var10, var11) -> {
               float var12 = var7 + 8.0F;
               float var13 = var9 - 16.0F;
               float var14 = this.fill.method2808(1.0F) * MathHelper.clamp(var3, 0.0F, 1.0F);
               boolean var15 = var2.isFlag();
               var4.drawRoundedRect(
                  var12,
                  var8 + 2.0F,
                  var13,
                  15.0F,
                  ProxyDotRadiusData.method8069(4.0F),
                  var11.getDisableActiveBg().getZenithColor().method7294(var10 * (var15 ? 0.9F : 0.5F))
               );
               if (var13 * var14 >= 8.0F) {
                  var4.drawRoundedRect(
                     var12,
                     var8 + 2.0F,
                     var13 * var14,
                     15.0F,
                     ProxyDotRadiusData.method8069(4.0F),
                     var11.getPrimaryColor().getZenithColor().method7294(var10 * (var15 ? 0.45F : 0.22F))
                  );
               }

               float var16 = 10.0F;
               ZenithColor var17 = var1 < MEDALS.length ? MEDALS[var1] : var11.getTextTertiary().getZenithColor().method7294(0.5F);
               var4.drawRoundedRect(
                  var12 + 3.0F, var8 + 2.0F + (15.0F - var16) / 2.0F, var16, var16, ProxyDotRadiusData.method8069(var16 / 2.0F), var17.method7294(var10)
               );
               Font var18 = Fonts.NEW_MEDIUM.getFont(4.4F);
               String var19 = String.valueOf(var1 + 1);
               var4.drawText(
                  var18,
                  var19,
                  var12 + 3.0F + (var16 - var18.width(var19)) / 2.0F,
                  var8 + 2.0F + (15.0F - var18.height()) / 2.0F,
                  new ZenithColor(20, 20, 20).method7294(var10)
               );
               Font var20 = Fonts.NEW_MEDIUM.getFont(5.0F);
               Font var21 = Fonts.NEW_REGULAR.getFont(4.4F);
               String var22 = hours(var2.method1213().getLongValue()) + " · " + var2.method1213().getLongValue2() + "/" + var2.method1213().getLongValue3();
               var4.drawText(
                  var21,
                  var22,
                  var12 + var13 - 6.0F - var21.width(var22),
                  var8 + 2.0F + (15.0F - var21.height()) / 2.0F,
                  var11.getTextSecondary().getZenithColor().method7294(var10)
               );
               String var23 = var2.name() + (var15 ? " " + t("gui.party.you") : "");
               var4.drawText(
                  var20,
                  var23,
                  var12 + 18.0F,
                  var8 + 2.0F + (15.0F - var20.height()) / 2.0F,
                  (var15 ? var11.getTextEnable() : var11.getTextSecondary()).getZenithColor().method7294(var10)
               );
               if (var2.isFlag2()) {
                  var4.drawRoundedRect(
                     var12 + 18.0F + var20.width(var23) + 4.0F,
                     var8 + 2.0F + 5.5F,
                     4.0F,
                     4.0F,
                     ProxyDotRadiusData.method8069(2.0F),
                     var11.getFriendColor().getZenithColor().method7294(var10)
                  );
               }
            }
         );
   }

   private void aside(Cloud var1, PartyMember.Data14 var2) {
      this.host.asideCaption("gui.party.stats.report");
      PartyMember.Data5 var3 = var2.method1219();
      if (var3 != null) {
         PartyMember.Data9 var4 = var3.method1213();
         PartyMember.Data9 var5 = var3.method1214();
         String var6 = delta(var4.getLongValue(), var5.getLongValue());
         this.host
            .asideHint(
               "• "
                  + (
                     var6.isEmpty()
                        ? t("gui.party.report.playedPlain").replace("{h}", hours(var4.getLongValue()))
                        : t("gui.party.report.played").replace("{h}", hours(var4.getLongValue())).replace("{d}", var6)
                  )
            );
         var var7 = new ArrayList<>(var3.getItems().entrySet());
         var7.removeIf(var0 -> ((PartyMember.Data9)var0.getValue()).getLongValue() < 60000L);
         var7.sort((var0, var1x) -> Long.compare(((PartyMember.Data9)var1x.getValue()).getLongValue(), ((PartyMember.Data9)var0.getValue()).getLongValue()));
         if (!var7.isEmpty()) {
            this.host.asideCaption("gui.party.caption.servers");
         }

         for (int var8 = 0; var8 < Math.min(4, var7.size()); var8++) {
            Entry var9 = (Entry)var7.get(var8);
            this.host
               .asideInfo(serverName((String)var9.getKey()), () -> hours(((PartyMember.Data9)var9.getValue()).getLongValue()), "gui.party.caption.servers");
         }
      }

      PartyMember.Data5 var10 = null;
      PartyMember.Data5 var11 = null;

      for (PartyMember.Data5 var14 : var2.getItems2()) {
         if (var10 == null || var14.method1213().getLongValue() > var10.method1213().getLongValue()) {
            var10 = var14;
         }

         if (var11 == null || var14.method1213().getLongValue2() > var11.method1213().getLongValue2()) {
            var11 = var14;
         }
      }

      if (var10 != null && var2.getItems2().size() > 1 && var10.method1213().getLongValue() > 0L) {
         this.host
            .asideHint(
               "• "
                  + t(var10.isFlag() ? "gui.party.report.leaderYou" : "gui.party.report.leader")
                     .replace("{n}", var10.name())
                     .replace("{h}", hours(var10.method1213().getLongValue()))
            );
      }

      if (var11 != null && var11.method1213().getLongValue2() > 0L && var2.getItems2().size() > 1) {
         this.host
            .asideHint("• " + t("gui.party.report.killer").replace("{n}", var11.name()).replace("{k}", String.valueOf(var11.method1213().getLongValue2())));
      }

      Share var13 = Zenith.getInstance().getShare();
      this.host.asideCaption("gui.party.stats.privacy");
      this.host.asideAction("cloud.share.stats", MenuGlyph.TIMER, () -> t(var13.stats.isEnabled() ? "gui.value.enabled" : "gui.value.disabled"), () -> {
         var13.stats.setEnabled(!var13.stats.isEnabled());
         this.host.invalidate();
      }, "cloud.share.stats.desc");
      if (!var13.server.isEnabled()) {
         this.host.asideHint(t("gui.party.stats.serverHidden"));
      }
   }

   private void line(String var1) {
      Font var2 = Fonts.NEW_REGULAR.getFont(4.8F);
      List<String> var3 = SectionPanel.wrapText(var1, var2, this.host.mainWidth() - 16.0F, 4);
      float var4 = var2.height() + 2.0F;
      this.host.custom(var3.size() * var4 + 6.0F, (var3x, var4x, var5, var6, var7, var8, var9, var10) -> {
         float var11 = var7 + 3.0F;

         for (Object var13_o : (Iterable<?>)(Object)(var3)) {
            String var13 = (String)var13_o;
            var3x.drawText(var2, var13, var6 + 8.0F, var11, var10.getTextTertiary().getZenithColor().method7294(var9));
            var11 += var4;
         }
      });
   }

   private float hover(String var1, boolean var2) {
      return this.hover.computeIfAbsent(var1, var0 -> new Animation(120L, 0.0F, Easing.INSTANCE12)).method2808(var2 ? 1.0F : 0.0F);
   }

   private static String serverName(String var0) {
      if ("hidden".equals(var0)) {
         return t("gui.party.stats.hiddenServer");
      } else {
         String var1 = ServerNameUtils.display(var0);
         return var1.isEmpty() ? var0 : var1;
      }
   }

   private static String hours(long var0) {
      long var2 = var0 / 60000L;
      long var4 = var2 / 60L;
      return var4 > 0L ? var4 + " " + t("gui.unit.h") + " " + var2 % 60L + " " + t("gui.unit.min") : var2 + " " + t("gui.unit.min");
   }

   private static String kd(PartyMember.Data9 var0) {
      return var0.getLongValue3() == 0L
         ? String.valueOf(var0.getLongValue2())
         : String.format(Locale.ROOT, "%.2f", (double)var0.getLongValue2() / var0.getLongValue3());
   }

   private static String delta(long var0, long var2) {
      if (var2 <= 0L) {
         return "";
      } else {
         long var4 = Math.round((var0 - var2) * 100.0 / var2);
         return (var4 >= 0L ? "+" : "") + var4 + "%";
      }
   }

   private static String t(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   private interface Choose {
      void pick(int var1);
   }

   private interface Selected {
      int get();
   }
}

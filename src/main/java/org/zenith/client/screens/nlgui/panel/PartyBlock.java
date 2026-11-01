package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.CloudWaypointsModule;
import org.zenith.client.modules.impl.render.PartyMarksModule;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.config.CloudAvatarCache;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.misc.ServerNameUtils;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.render.PartyManager;
import org.zenith.utility.render.Voice;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class PartyBlock implements MinecraftAccess {
   private static final float INSET = 8.0F;
   private static final float HEADER = 24.0F;
   private static final float SMALL_CHIP = 16.0F;
   private static final float AVATAR = 14.0F;
   private static final float ITEM = 16.0F;
   private static final long DEATH_WINDOW_MS = 1800000L;
   private static final ZenithColor DANGER = new ZenithColor(-495247);
   private static final ZenithColor LEADER = new ZenithColor(-278748);
   private static final ZenithColor DEATH = new ZenithColor(-1096636);
   private final FriendsSectionPanel host;
   private final Map<String, Animation> hover = new HashMap<>();
   private final Map<String, Integer> hovered = new HashMap<>();
   private final Map<Long, Animation> avatarFade = new HashMap<>();
   private final Animation appear = new Animation(420L, 1.0F, Easing.INSTANCE12);
   private long member;
   private long mark;
   private long death;
   private boolean inviteOpen;

   public PartyBlock(FriendsSectionPanel var1) {
      this.host = var1;
   }

   public boolean hasSelection() {
      return this.member != 0L || this.mark != 0L || this.death != 0L;
   }

   public void clearSelection() {
      this.member = 0L;
      this.mark = 0L;
      this.death = 0L;
   }

   public String signature(Cloud var1) {
      PartyMember.Data4 var2 = this.recentDeath(var1);
      return this.inviteOpen + "|" + this.member + "|" + this.mark + "|" + this.death + "|" + (var2 == null ? 0L : var2.getLongValue());
   }

   public void build(Cloud var1) {
      if (var1 != null && var1.method7860()) {
         PartyMember var2 = var1.getPartyMember();
         PartyMember.Data6 var3 = var2.getData6();
         if (var3 == null) {
            var var4 = this.invitable(var1, null);
            if (!var4.isEmpty()) {
               this.startCard(var1, var4);
            }
         } else {
            this.partyCard(var1, var3);
         }

         PartyMember.Data4 var5 = this.recentDeath(var1);
         if (var5 != null) {
            this.deathRow(var1, var5);
         }
      }
   }

   private List<PartyPlayerInfo> invitable(Cloud var1, PartyMember.Data6 var2) {
      ArrayList<PartyPlayerInfo> var3 = new ArrayList<>();
      var var4 = var1.state().method8038().iterator();

      while (true) {
         PartyPlayerInfo var5;
         while (true) {
            if (!var4.hasNext()) {
               var3.sort((var1x, var2x) -> Boolean.compare(var1.prefs().method3319(var2x.username()), var1.prefs().method3319(var1x.username())));
               return var3;
            }

            var5 = (PartyPlayerInfo)var4.next();
            if (!var1.prefs().method3316(var5.username())) {
               if (var2 == null) {
                  break;
               }

               if (var2.method1216(var5.uid()) == null) {
                  boolean var6 = false;

                  for (PartyMember.Data8 var8 : var2.getItems2()) {
                     var6 |= var8.uid() == var5.uid();
                  }

                  if (!var6) {
                     break;
                  }
               }
            }
         }

         var3.add(var5);
      }
   }

   private void startCard(Cloud var1, List<PartyPlayerInfo> var2) {
      String var3 = "party:start";
      var var4 = var2.subList(0, Math.min(5, var2.size()));
      this.host
         .custom(
            36.0F,
            (var3x, var4x, var5, var6, var7, var8, var9, var10) -> {
               var9 *= this.appear.method2808(1.0F);
               float var11 = var6 + 8.0F;
               float var12 = var8 - 16.0F;
               float var13 = var7 + 2.0F;
               float var14 = 32.0F;
               plate(var3x, var11, var13, var12, var14, 0.0F, var9, var10);
               var3x.drawRoundedRect(
                  var11, var13, var12, var14, ProxyDotRadiusData.method8069(4.0F), var10.getPrimaryColor().getZenithColor().method7294(var9 * 0.05F)
               );
               float var15 = 18.0F;
               float var16 = 4.0F;
               float var17 = var4.size() * (var15 + var16) - var16;
               float var18 = var11 + var12 - 8.0F - var17;
               int var19 = -1;

               for (int var20 = 0; var20 < var4.size(); var20++) {
                  PartyPlayerInfo var21 = (PartyPlayerInfo)var4.get(var20);
                  float var22 = var18 + var20 * (var15 + var16);
                  float var23 = var13 + (var14 - var15) / 2.0F;
                  boolean var24 = new BoundsData(var22 - 1.0F, var23 - 1.0F, var15 + 2.0F, var15 + 2.0F).contains(var4x, var5);
                  if (var24) {
                     var19 = var20;
                     HoverInfo.request(var21.username(), t("gui.party.start.invite").replace("{n}", var21.username()));
                  }

                  float var25 = this.hover(var3 + var20, var24);
                  float var26 = var25 * 1.5F;
                  this.avatar(var3x, var21.username(), var21.uid(), var22, var23 - var26, var15, var9, var10);
                  float var27 = 7.0F;
                  var3x.drawRoundedRect(
                     var22 + var15 - var27 + 1.0F,
                     var23 + var15 - var27 + 1.0F - var26,
                     var27,
                     var27,
                     ProxyDotRadiusData.method8069(var27 / 2.0F),
                     var10.getPrimaryColor().getZenithColor().method7294(var9 * (0.75F + 0.25F * var25))
                  );
                  var3x.drawRect(
                     var22 + var15 - var27 / 2.0F + 1.0F - 0.5F,
                     var23 + var15 - var27 + 2.5F - var26,
                     1.0F,
                     var27 - 3.0F,
                     new ZenithColor(255, 255, 255).method7294(var9)
                  );
                  var3x.drawRect(
                     var22 + var15 - var27 + 2.5F,
                     var23 + var15 - var27 / 2.0F + 1.0F - 0.5F - var26,
                     var27 - 3.0F,
                     1.0F,
                     new ZenithColor(255, 255, 255).method7294(var9)
                  );
               }

               this.hovered.put(var3, var19);
               Font var29 = Fonts.NEW_MEDIUM.getFont(5.6F);
               Font var30 = Fonts.NEW_REGULAR.getFont(4.4F);
               float var31 = var11 + 8.0F;
               float var32 = var18 - 8.0F - var31;
               float var33 = var13 + (var14 - var29.height() - 3.0F - var30.height()) / 2.0F;
               var3x.drawText(var29, fit(var29, t("gui.party.start.title"), var32), var31, var33, var10.getTextEnable().getZenithColor().method7294(var9));
               var3x.drawText(
                  var30,
                  fit(var30, t("gui.party.start.note"), var32),
                  var31,
                  var33 + var29.height() + 3.0F,
                  var10.getTextTertiary().getZenithColor().method7294(var9)
               );
            },
            () -> {
               int var4x = this.hovered.getOrDefault(var3, -1);
               if (var4x >= 0 && var4x < var4.size()) {
                  PartyPlayerInfo var5 = (PartyPlayerInfo)var4.get(var4x);
                  var1.getPartyMember().method1153(var5.uid());
                  this.host.partyNotice(t("gui.friends.party.invited").replace("{n}", var5.username()));
                  this.host.invalidate();
               }
            },
            null
         );
   }

   private void partyCard(Cloud var1, PartyMember.Data6 var2) {
      Font var3 = Fonts.NEW_MEDIUM.getFont(4.8F);
      Font var4 = Fonts.NEW_REGULAR.getFont(4.2F);
      float var5 = this.host.mainWidth() - 16.0F;
      float var6 = 7.0F;
      float var7 = var5 - var6 * 2.0F;
      ArrayList var8 = new ArrayList();
      ArrayList<PartyMember.Data3> var9 = new ArrayList();

      for (PartyMember.Data3 var11 : var2.getItems()) {
         if (var11.uid() != var1.getLongValue()) {
            var9.add(var11);
         }
      }

      var9.sort((var0, var1x) -> Boolean.compare(var1x.isFlag(), var0.isFlag()));
      float var25 = 0.0F;
      float var31 = 20.0F;
      float var12 = 4.0F;
      float var13 = 18.0F;
      float var14 = var4.width("9999 м") + 6.0F;

      for (Object var16_o : (Iterable<?>)(Object)(var9)) {
         PartyMember.Data3 var16 = (PartyMember.Data3)var16_o;
         float var17 = 20.0F + var3.width(var16.username()) + 4.0F + var14;
         if (var25 > 0.0F && var25 + var17 > var7) {
            var25 = 0.0F;
            var31 += var13 + var12;
         }

         var8.add(new PartyBlock.Item(PartyBlock.Kind.MEMBER, new BoundsData(var25, var31, var17, var13), var16));
         var25 += var17 + var12;
      }

      for (PartyMember.Data8 var40 : var2.getItems2()) {
         String var43 = var40.name().isEmpty() ? var1.getPartyMember().method1149(var40.uid()) : var40.name();
         float var18 = 20.0F + var3.width(var43) + 4.0F + var4.width("…") + 6.0F;
         if (var25 > 0.0F && var25 + var18 > var7) {
            var25 = 0.0F;
            var31 += var13 + var12;
         }

         var8.add(new PartyBlock.Item(PartyBlock.Kind.INVITED, new BoundsData(var25, var31, var18, var13), var40));
         var25 += var18 + var12;
      }

      if (var2.getItems().size() < 6) {
         if (var9.isEmpty() && var2.getItems2().isEmpty()) {
            float var37 = 18.0F + var3.width(t("gui.party.chip.inviteFriends")) + 8.0F;
            var8.add(new PartyBlock.Item(PartyBlock.Kind.INVITE_ALL, new BoundsData(var25, var31, var37, var13), null));
            var25 += var37 + var12;
         } else {
            if (var25 > 0.0F && var25 + var13 > var7) {
               var25 = 0.0F;
               var31 += var13 + var12;
            }

            var8.add(new PartyBlock.Item(PartyBlock.Kind.PLUS, new BoundsData(var25, var31, var13, var13), null));
            var25 += var13 + var12;
         }
      }

      var31 += var13;
      if (this.inviteOpen) {
         List<PartyPlayerInfo> var38 = this.invitable(var1, var2);
         var31 += 5.0F;
         var25 = 0.0F;
         if (var38.isEmpty()) {
            var8.add(new PartyBlock.Item(PartyBlock.Kind.HINT, new BoundsData(0.0F, var31, var7, 14.0F), t("gui.party.noFriendsOnline")));
            var31 += 14.0F;
         } else {
            for (Object var44_o : (Iterable<?>)(Object)(var38)) {
               PartyPlayerInfo var44 = (PartyPlayerInfo)var44_o;
               float var46 = 17.0F + var3.width(var44.username()) + 12.0F;
               if (var25 > 0.0F && var25 + var46 > var7) {
                  var25 = 0.0F;
                  var31 += 16.0F + var12;
               }

               var8.add(new PartyBlock.Item(PartyBlock.Kind.CANDIDATE, new BoundsData(var25, var31, var46, 16.0F), var44));
               var25 += var46 + var12;
            }

            var31 += 16.0F;
         }
      }

      var31 += 7.0F;
      float var39 = var31;
      var25 = 0.0F;
      float var42 = 14.0F;
      String var45 = t("gui.party.marks.here");
      float var47 = 12.0F + var4.width(var45) + 6.0F;
      var8.add(new PartyBlock.Item(PartyBlock.Kind.HERE, new BoundsData(var25, var31, var47, var42), null));
      var25 += var47 + var12;
      ArrayList<PartyMember.Data12> var19 = new ArrayList();
      String var20 = var1.method7911();

      for (PartyMember.Data12 var22 : var1.getPartyMember().method1138()) {
         if (var22.server().equals(var20)) {
            var19.add(var22);
         }
      }

      var19.sort(
         (var0, var1x) -> Double.compare(
            distance(var0.getDoubleValue(), var0.getDoubleValue2(), var0.getDoubleValue3()),
            distance(var1x.getDoubleValue(), var1x.getDoubleValue2(), var1x.getDoubleValue3())
         )
      );
      int var48 = 1;

      for (Object var23_o : (Iterable<?>)(Object)(var19)) {
         PartyMember.Data12 var23 = (PartyMember.Data12)var23_o;
         float var24 = 13.0F + var4.width(var23.name() + " · 9999 м") + 6.0F;
         if (var25 + var24 > var7) {
            if (var48 >= 2) {
               break;
            }

            var48++;
            var25 = 0.0F;
            var31 += var42 + var12;
         }

         var8.add(new PartyBlock.Item(PartyBlock.Kind.MARK, new BoundsData(var25, var31, var24, var42), var23));
         var25 += var24 + var12;
      }

      if (var19.isEmpty()) {
         String var50 = markHint();
         if (!var50.isEmpty()) {
            var8.add(new PartyBlock.Item(PartyBlock.Kind.HINT, new BoundsData(var25, var31, var7 - var25, var42), var50));
         }
      }

      var31 += var42;
      float var51 = var31 + var6;
      String var52 = "party:card";
      this.host
         .custom(
            var51 + 6.0F,
            (var11x, var12x, var13x, var14x, var15, var16x, var17x, var18x) -> {
               var17x *= this.appear.method2808(1.0F);
               float var19x = var14x + 8.0F;
               float var20x = var15 + 3.0F;
               float var21 = var16x - 16.0F;
               var11x.drawRoundedRect(
                  var19x, var20x, var21, var51, ProxyDotRadiusData.method8069(6.0F), var18x.getDisableActiveBg().getZenithColor().method7294(var17x * 0.5F)
               );
               float var22x = var19x + var6;
               float var23x = var20x + var6 - 3.0F;
               Font var24x = Fonts.NEW_MEDIUM.getFont(5.6F);
               float var25x = var23x + 8.0F;
               MenuGlyph.FRIENDS.draw(var11x, var22x, var25x - 3.0F, var18x.getPrimaryColor().getZenithColor().method7294(var17x));
               String var26x = t("gui.party.strip.title");
               var11x.drawText(var24x, var26x, var22x + 11.0F, var25x - var24x.height() / 2.0F, var18x.getTextEnable().getZenithColor().method7294(var17x));
               var11x.drawText(
                  var4,
                  var2.method1217() + "/" + var2.getItems().size(),
                  var22x + 11.0F + var24x.width(var26x) + 4.0F,
                  var25x - var4.height() / 2.0F,
                  var18x.getTextTertiary().getZenithColor().method7294(var17x)
               );
               Voice var27x = Zenith.getInstance().getVoice();
               boolean var28x = var27x != null && var27x.isEnabled() && var27x.defaultChannel.getIndex() == 1;
               String var29x = t(var28x ? "gui.party.voice.on" : "gui.party.voice.off");
               String var30x = t("gui.party.leaveShort");
               Font var31x = Fonts.NEW_MEDIUM.getFont(4.5F);
               float var32x = 12.0F;
               float var33x = var19x + var21 - var6;
               BoundsData var34x = new BoundsData(var33x - (var31x.width(var30x) + 12.0F), var25x - var32x / 2.0F, var31x.width(var30x) + 12.0F, var32x);
               BoundsData var35x = new BoundsData(
                  var34x.getFloatValue() - 4.0F - (var31x.width(var29x) + 20.0F), var34x.getFloatValue2(), var31x.width(var29x) + 20.0F, var32x
               );
               int var36 = -1;
               if (var35x.contains(var12x, var13x)) {
                  var36 = 1001;
                  HoverInfo.request(t("gui.party.voice"), t("gui.party.voice.desc"));
               } else if (var34x.contains(var12x, var13x)) {
                  var36 = 1002;
                  HoverInfo.request(t("gui.party.leave"), t("gui.party.leave.desc"));
               }

               this.pill(var11x, var52 + ":v", var35x, var29x, var28x, var36 == 1001, var17x, var18x, null, 15.0F);
               MenuGlyph.MIC
                  .draw(
                     var11x,
                     var35x.getFloatValue() + 5.0F,
                     var35x.getFloatValue2() + (var32x - 6.0F) / 2.0F,
                     (var28x ? new ZenithColor(255, 255, 255) : var18x.getTextSecondary().getZenithColor()).method7294(var17x)
                  );
               this.pill(var11x, var52 + ":l", var34x, var30x, false, var36 == 1002, var17x, var18x, DANGER, 0.0F);
               var11x.drawRect(var22x, var23x + var39 - 4.0F, var21 - var6 * 2.0F, 0.5F, var18x.getTextTertiary().getZenithColor().method7294(var17x * 0.18F));

               for (int var37x = 0; var37x < var8.size(); var37x++) {
                  PartyBlock.Item var38x = (PartyBlock.Item)var8.get(var37x);
                  BoundsData var39x = new BoundsData(
                     var22x + var38x.area().getFloatValue(), var23x + var38x.area().getFloatValue2(), var38x.area().width(), var38x.area().height()
                  );
                  boolean var40x = var39x.contains(var12x, var13x) && var38x.kind() != PartyBlock.Kind.HINT;
                  if (var40x) {
                     var36 = var37x;
                  }

                  float var41 = this.hover(var52 + var37x + var38x.kind(), var40x);
                  switch (var38x.kind()) {
                     case MEMBER:
                        this.memberPill(var11x, var1, var2, (PartyMember.Data3)var38x.value(), var39x, var41, var40x, var17x, var18x);
                        break;
                     case INVITED:
                        this.invitedPill(var11x, var1, (PartyMember.Data8)var38x.value(), var39x, var17x, var18x);
                        break;
                     case PLUS:
                        var11x.drawRoundedRect(
                           var39x.getFloatValue(),
                           var39x.getFloatValue2(),
                           var39x.width(),
                           var39x.height(),
                           ProxyDotRadiusData.method8069(var39x.height() / 2.0F),
                           var18x.getPrimaryColor().getZenithColor().method7294(var17x * (0.15F + 0.3F * var41 + (this.inviteOpen ? 0.25F : 0.0F)))
                        );
                        cross(
                           var11x,
                           var39x.getFloatValue() + var39x.width() / 2.0F,
                           var39x.getFloatValue2() + var39x.height() / 2.0F,
                           7.0F,
                           this.inviteOpen ? 45.0F : 0.0F,
                           var18x.getPrimaryColor().getZenithColor().method7295(new ZenithColor(255, 255, 255), var41).method7294(var17x)
                        );
                        if (var40x) {
                           HoverInfo.request(t("gui.party.chip.invite"), t("gui.party.chip.invite.desc"));
                        }
                        break;
                     case INVITE_ALL:
                        var11x.drawRoundedRect(
                           var39x.getFloatValue(),
                           var39x.getFloatValue2(),
                           var39x.width(),
                           var39x.height(),
                           ProxyDotRadiusData.method8069(var39x.height() / 2.0F),
                           var18x.getPrimaryColor().getZenithColor().method7294(var17x * (0.18F + 0.3F * var41 + (this.inviteOpen ? 0.2F : 0.0F)))
                        );
                        cross(
                           var11x,
                           var39x.getFloatValue() + 9.0F,
                           var39x.getFloatValue2() + var39x.height() / 2.0F,
                           6.0F,
                           this.inviteOpen ? 45.0F : 0.0F,
                           var18x.getPrimaryColor().getZenithColor().method7295(new ZenithColor(255, 255, 255), var41).method7294(var17x)
                        );
                        var11x.drawText(
                           var3,
                           t("gui.party.chip.inviteFriends"),
                           var39x.getFloatValue() + 18.0F,
                           var39x.getFloatValue2() + (var39x.height() - var3.height()) / 2.0F,
                           var18x.getTextEnable().getZenithColor().method7294(var17x)
                        );
                        break;
                     case CANDIDATE:
                        PartyPlayerInfo var42x = (PartyPlayerInfo)var38x.value();
                        var11x.drawRoundedRect(
                           var39x.getFloatValue(),
                           var39x.getFloatValue2(),
                           var39x.width(),
                           var39x.height(),
                           ProxyDotRadiusData.method8069(var39x.height() / 2.0F),
                           var18x.getDisableActiveBg()
                              .getZenithColor()
                              .method7295(var18x.getPrimaryColor().getZenithColor(), var41 * 0.5F)
                              .method7294(var17x * (0.8F + 0.2F * var41))
                        );
                        this.avatar(
                           var11x, var42x.username(), var42x.uid(), var39x.getFloatValue() + 3.0F, var39x.getFloatValue2() + 3.0F, 10.0F, var17x, var18x
                        );
                        var11x.drawText(
                           var3,
                           var42x.username(),
                           var39x.getFloatValue() + 17.0F,
                           var39x.getFloatValue2() + (var39x.height() - var3.height()) / 2.0F,
                           var18x.getTextSecondary().getZenithColor().method7295(var18x.getTextEnable().getZenithColor(), var41).method7294(var17x)
                        );
                        cross(
                           var11x,
                           var39x.getFloatValue() + var39x.width() - 6.0F,
                           var39x.getFloatValue2() + var39x.height() / 2.0F,
                           5.0F,
                           0.0F,
                           var18x.getPrimaryColor().getZenithColor().method7294(var17x)
                        );
                        if (var40x) {
                           HoverInfo.request(var42x.username(), t("gui.party.start.invite").replace("{n}", var42x.username()));
                        }
                        break;
                     case HERE:
                        var11x.drawRoundedRect(
                           var39x.getFloatValue(),
                           var39x.getFloatValue2(),
                           var39x.width(),
                           var39x.height(),
                           ProxyDotRadiusData.method8069(var39x.height() / 2.0F),
                           var18x.getPrimaryColor().getZenithColor().method7294(var17x * (0.2F + 0.4F * var41))
                        );
                        MenuGlyph.GPS
                           .draw(
                              var11x,
                              var39x.getFloatValue() + 3.0F,
                              var39x.getFloatValue2() + (var39x.height() - 6.0F) / 2.0F,
                              new ZenithColor(255, 255, 255).method7294(var17x)
                           );
                        var11x.drawText(
                           var4,
                           var45,
                           var39x.getFloatValue() + 3.0F + 6.0F + 3.0F,
                           var39x.getFloatValue2() + (var39x.height() - var4.height()) / 2.0F,
                           new ZenithColor(255, 255, 255).method7294(var17x)
                        );
                        if (var40x) {
                           HoverInfo.request(t("gui.party.marks.here"), t("gui.party.marks.here.desc"));
                        }
                        break;
                     case MARK:
                        this.markChip(var11x, (PartyMember.Data12)var38x.value(), var39x, var4, var41, var40x, var17x, var18x);
                        break;
                     case HINT:
                        var11x.drawText(
                           var4,
                           fit(var4, (String)var38x.value(), var39x.width()),
                           var39x.getFloatValue(),
                           var39x.getFloatValue2() + (var39x.height() - var4.height()) / 2.0F,
                           var18x.getTextTertiary().getZenithColor().method7294(var17x * 0.8F)
                        );
                  }
               }

               this.hovered.put(var52, var36);
            },
            () -> {
               int var4x = this.hovered.getOrDefault(var52, -1);
               if (var4x == 1001) {
                  this.toggleVoice();
               } else if (var4x == 1002) {
                  var1.getPartyMember().method1156();
                  this.clearSelection();
                  this.inviteOpen = false;
               } else if (var4x >= 0 && var4x < var8.size()) {
                  PartyBlock.Item var5x = (PartyBlock.Item)var8.get(var4x);
                  switch (var5x.kind()) {
                     case MEMBER:
                        PartyMember.Data3 var9x = (PartyMember.Data3)var5x.value();
                        this.clearSelection();
                        if (var1.getFriends().method6442(var9x.username())) {
                           this.host.selectFriendFromParty(var9x.username());
                        } else {
                           this.member = var9x.uid();
                           this.host.partySelectionChanged();
                        }
                     case INVITED:
                     default:
                        break;
                     case PLUS:
                     case INVITE_ALL:
                        this.inviteOpen = !this.inviteOpen;
                        break;
                     case CANDIDATE:
                        PartyPlayerInfo var8x = (PartyPlayerInfo)var5x.value();
                        var1.getPartyMember().method1153(var8x.uid());
                        this.host.partyNotice(t("gui.friends.party.invited").replace("{n}", var8x.username()));
                        break;
                     case HERE:
                        if (PartyMarksModule.INSTANCE.method5424()) {
                           PartyMarksModule.INSTANCE.setToggled(true);
                           this.host.partyNotice(t("gui.party.markPlacedShort"));
                        }
                        break;
                     case MARK:
                        PartyMember.Data12 var6x = (PartyMember.Data12)var5x.value();
                        boolean var7x = this.mark == var6x.getLongValue();
                        this.clearSelection();
                        this.mark = var7x ? 0L : var6x.getLongValue();
                        this.host.partySelectionChanged();
                  }
               }

               this.host.invalidate();
            },
            null,
            () -> {
               int var3x = this.hovered.getOrDefault(var52, -1);
               if (var3x >= 0 && var3x < var8.size() && ((PartyBlock.Item)var8.get(var3x)).kind() == PartyBlock.Kind.MARK) {
                  PartyMember.Data12 var4x = (PartyMember.Data12)((PartyBlock.Item)var8.get(var3x)).value();
                  if (PartyMarksModule.INSTANCE.getLongValue5() == var4x.getLongValue()) {
                     PartyMarksModule.INSTANCE.clearFocus();
                  } else {
                     PartyMarksModule.INSTANCE.method5420(var4x.getLongValue());
                  }

                  this.host.invalidate();
               }
            }
         );
   }

   private void toggleVoice() {
      Voice var1 = Zenith.getInstance().getVoice();
      if (var1 != null) {
         boolean var2 = var1.isEnabled() && var1.defaultChannel.getIndex() == 1;
         if (!var1.isEnabled()) {
            var1.setEnabled(true);
         }

         var1.defaultChannel.getValues().get(var2 ? 0 : 1).method2103();
         this.host.partyNotice(t(var2 ? "gui.party.voice.nowNear" : "gui.party.voice.nowParty"));
      }
   }

   private void memberPill(
      MenuDrawContext var1, Cloud var2, PartyMember.Data6 var3, PartyMember.Data3 var4, BoundsData var5, float var6, boolean var7, float var8, ZenithStyle var9
   ) {
      boolean var10 = this.member == var4.uid() || var4.username().equals(this.host.selectedFriendName());
      float var11 = var4.isFlag() ? 1.0F : 0.5F;
      var1.drawRoundedRect(
         var5.getFloatValue(),
         var5.getFloatValue2(),
         var5.width(),
         var5.height(),
         ProxyDotRadiusData.method8069(var5.height() / 2.0F),
         var9.getDisableActiveBg()
            .getZenithColor()
            .method7295(var9.getPrimaryColor().getZenithColor(), var10 ? 0.3F : var6 * 0.12F)
            .method7294(var8 * (0.85F + 0.15F * var6))
      );
      PartyPlayerInfo var12 = var4.isFlag() ? var2.state().method8040(var4.uid()) : null;
      Voice var13 = Zenith.getInstance().getVoice();
      boolean var14 = var13 != null && var13.getVoiceState().method5365(var4.uid());
      float var15 = 12.0F;
      float var16 = var5.getFloatValue() + 3.0F;
      float var17 = var5.getFloatValue2() + (var5.height() - var15) / 2.0F;
      if (var14) {
         var1.drawRoundedRect(
            var16 - 1.5F,
            var17 - 1.5F,
            var15 + 3.0F,
            var15 + 3.0F,
            ProxyDotRadiusData.method8069((var15 + 3.0F) / 2.0F),
            var9.getPrimaryColor().getZenithColor().method7294(var8)
         );
      }

      this.avatar(var1, var4.username(), var4.uid(), var16, var17, var15, var8 * var11, var9);
      String var18 = var2.getPartyMember().method1141(var4.uid()).getText();
      float var19 = 3.5F;
      var1.drawRoundedRect(
         var16 + var15 - var19 + 1.0F,
         var17 + var15 - var19 + 1.0F,
         var19,
         var19,
         ProxyDotRadiusData.method8069(var19 / 2.0F),
         (var4.isFlag() ? PartyManager.method51(var18) : var9.getTextTertiary().getZenithColor()).method7294(var8)
      );
      Font var20 = Fonts.NEW_MEDIUM.getFont(4.8F);
      Font var21 = Fonts.NEW_REGULAR.getFont(4.2F);
      float var22 = var16 + var15 + 4.0F;
      float var23 = var5.getFloatValue2() + (var5.height() - var20.height()) / 2.0F - (var12 != null ? 1.0F : 0.0F);
      ZenithColor var24 = var3.method1215(var4.uid()) ? LEADER : var9.getTextEnable().getZenithColor();
      var1.drawText(var20, var4.username(), var22, var23, var24.method7294(var8 * var11));
      String var25 = var12 != null ? where(var2, var12) : (var4.isFlag() ? "" : t("hud.party.offline"));
      if (!var25.isEmpty()) {
         var1.drawText(
            var21,
            var25,
            var5.getFloatValue() + var5.width() - 7.0F - var21.width(var25),
            var5.getFloatValue2() + (var5.height() - var21.height()) / 2.0F - (var12 != null ? 1.0F : 0.0F),
            var9.getTextTertiary().getZenithColor().method7294(var8)
         );
      }

      if (var12 != null) {
         float var27 = var5.getFloatValue() + var5.width() - 7.0F - var22;
         float var28 = MathHelper.clamp(var12.getFloatValue3() / Math.max(1.0F, var12.getFloatValue4()), 0.0F, 1.0F);
         float var29 = var5.getFloatValue2() + var5.height() - 4.5F;
         var1.drawRoundedRect(var22, var29, var27, 1.5F, ProxyDotRadiusData.method8069(0.75F), var9.getTextTertiary().getZenithColor().method7294(var8 * 0.25F));
         var1.drawRoundedRect(var22, var29, var27 * var28, 1.5F, ProxyDotRadiusData.method8069(0.75F), DANGER.method7294(var8));
      }

      if (var7) {
         String var26 = var4.isFlag() ? PartyManager.method52(var2.getPartyMember().method1141(var4.uid())) : t("gui.friends.status.offline");
         HoverInfo.request(var4.username() + (var3.method1215(var4.uid()) ? " · " + t("gui.party.role.leader") : ""), var26);
      }
   }

   private void invitedPill(MenuDrawContext var1, Cloud var2, PartyMember.Data8 var3, BoundsData var4, float var5, ZenithStyle var6) {
      String var7 = var3.name().isEmpty() ? var2.getPartyMember().method1149(var3.uid()) : var3.name();
      float var8 = 0.55F + 0.45F * (float)Math.sin(System.currentTimeMillis() / 380.0);
      var1.drawRoundedRect(
         var4.getFloatValue(),
         var4.getFloatValue2(),
         var4.width(),
         var4.height(),
         ProxyDotRadiusData.method8069(var4.height() / 2.0F),
         var6.getDisableActiveBg().getZenithColor().method7294(var5 * 0.5F)
      );
      var1.drawRoundedBorder(
         var4.getFloatValue(),
         var4.getFloatValue2(),
         var4.width(),
         var4.height(),
         0.5F,
         ProxyDotRadiusData.method8069(var4.height() / 2.0F),
         var6.getPrimaryColor().getZenithColor().method7294(var5 * 0.5F * var8)
      );
      float var9 = 12.0F;
      this.avatar(var1, var7, var3.uid(), var4.getFloatValue() + 3.0F, var4.getFloatValue2() + (var4.height() - var9) / 2.0F, var9, var5 * 0.5F, var6);
      Font var10 = Fonts.NEW_MEDIUM.getFont(4.8F);
      Font var11 = Fonts.NEW_REGULAR.getFont(4.2F);
      var1.drawText(
         var10,
         var7,
         var4.getFloatValue() + 3.0F + var9 + 4.0F,
         var4.getFloatValue2() + (var4.height() - var10.height()) / 2.0F,
         var6.getTextSecondary().getZenithColor().method7294(var5 * 0.8F)
      );
      var1.drawText(
         var11,
         "…",
         var4.getFloatValue() + var4.width() - 7.0F - var11.width("…"),
         var4.getFloatValue2() + (var4.height() - var11.height()) / 2.0F,
         var6.getPrimaryColor().getZenithColor().method7294(var5 * var8)
      );
   }

   private void markChip(MenuDrawContext var1, PartyMember.Data12 var2, BoundsData var3, Font var4, float var5, boolean var6, float var7, ZenithStyle var8) {
      boolean var9 = PartyMarksModule.INSTANCE.getLongValue5() == var2.getLongValue() || this.mark == var2.getLongValue();
      PartyMarksModule.Data2 var10 = PartyMarksModule.method5419(var2.getText4());
      ZenithColor var11 = new ZenithColor(var2.color() == -1 ? var10.color() : var2.color());
      var1.drawRoundedRect(
         var3.getFloatValue(),
         var3.getFloatValue2(),
         var3.width(),
         var3.height(),
         ProxyDotRadiusData.method8069(var3.height() / 2.0F),
         var8.getDisableActiveBg().getZenithColor().method7295(var11, var9 ? 0.3F : var5 * 0.15F).method7294(var7 * 0.9F)
      );
      var1.drawRoundedRect(
         var3.getFloatValue() + 4.0F,
         var3.getFloatValue2() + (var3.height() - 6.0F) / 2.0F,
         6.0F,
         6.0F,
         ProxyDotRadiusData.method8069(3.0F),
         var11.method7294(var7)
      );
      String var12 = var2.name() + " · " + meters(distance(var2.getDoubleValue(), var2.getDoubleValue2(), var2.getDoubleValue3()));
      var1.drawText(
         var4,
         fit(var4, var12, var3.width() - 19.0F),
         var3.getFloatValue() + 13.0F,
         var3.getFloatValue2() + (var3.height() - var4.height()) / 2.0F,
         var8.getTextSecondary().getZenithColor().method7295(var8.getTextEnable().getZenithColor(), var5).method7294(var7)
      );
      if (var6) {
         HoverInfo.request(var2.name(), t("gui.party.mark.chip.desc"));
      }
   }

   private static void cross(MenuDrawContext var0, float var1, float var2, float var3, float var4, ZenithColor var5) {
      var0.pushMatrix();
      var0.getMatrices().translate(var1, var2);
      var0.getMatrices().rotate((float)Math.toRadians(var4));
      var0.drawRect(-var3 / 2.0F, -0.5F, var3, 1.0F, var5);
      var0.drawRect(-0.5F, -var3 / 2.0F, 1.0F, var3, var5);
      var0.popMatrix();
   }

   private static String markHint() {
      int var0 = CloudWaypointsModule.INSTANCE.getMarkBind().getKeyCode();
      return var0 > 0 ? t("gui.party.marks.hint").replace("{k}", Voice.method364(CloudWaypointsModule.INSTANCE.getMarkBind())) : "";
   }

   private PartyMember.Data4 recentDeath(Cloud var1) {
      if (var1 == null) {
         return null;
      } else {
         long var2 = System.currentTimeMillis() - 1800000L;

         for (PartyMember.Data4 var5 : var1.getPartyMember().method1139()) {
            if (var5.time() >= var2) {
               return var5;
            }
         }

         return null;
      }
   }

   private void deathRow(Cloud var1, PartyMember.Data4 var2) {
      String var3 = "party:death";
      boolean var4 = var2.uid() == var1.getLongValue() || var2.getLongValue() < 0L;
      this.host
         .custom(
            24.0F,
            (var5, var6, var7, var8, var9, var10, var11, var12) -> {
               var11 *= this.appear.method2808(1.0F);
               float var13 = var8 + 8.0F;
               float var14 = var10 - 16.0F;
               BoundsData var15 = new BoundsData(var13, var9 + 3.0F, var14, 18.0F);
               boolean var16 = var15.contains(var6, var7);
               this.hovered.put(var3, var16 ? 0 : -1);
               float var17 = this.hover(var3, var16);
               var5.drawRoundedRect(
                  var15.getFloatValue(),
                  var15.getFloatValue2(),
                  var15.width(),
                  var15.height(),
                  ProxyDotRadiusData.method8069(4.0F),
                  DEATH.method7294(var11 * (0.08F + 0.08F * var17 + (this.death == var2.getLongValue() ? 0.08F : 0.0F)))
               );
               float var18 = 10.0F;
               var5.drawRoundedRect(
                  var15.getFloatValue() + 4.0F,
                  var15.getFloatValue2() + (var15.height() - var18) / 2.0F,
                  var18,
                  var18,
                  ProxyDotRadiusData.method8069(var18 / 2.0F),
                  DEATH.method7294(var11)
               );
               MenuGlyph.CLOSE
                  .draw(
                     var5,
                     var15.getFloatValue() + 4.0F + (var18 - 6.0F) / 2.0F,
                     var15.getFloatValue2() + (var15.height() - 6.0F) / 2.0F,
                     new ZenithColor(255, 255, 255).method7294(var11)
                  );
               Font var19 = Fonts.NEW_MEDIUM.getFont(4.8F);
               Font var20 = Fonts.NEW_REGULAR.getFont(4.3F);
               String var21 = var2.getItems().size()
                  + " "
                  + t("gui.party.deaths.items")
                  + " · "
                  + (
                     sameWorld(var1, var2.server(), var2.dimension())
                        ? meters(distance(var2.getDoubleValue(), var2.getDoubleValue2(), var2.getDoubleValue3()))
                        : ServerNameUtils.display(var2.server())
                  );
               float var22 = var20.width(var21);
               var5.drawText(
                  var20,
                  var21,
                  var15.getFloatValue() + var15.width() - 6.0F - var22,
                  var15.getFloatValue2() + (var15.height() - var20.height()) / 2.0F,
                  var12.getTextTertiary().getZenithColor().method7294(var11)
               );
               String var23 = (var4 ? t("gui.party.death.yours") : t("gui.party.death.mate").replace("{n}", var2.name())) + " · " + ago(var2.time());
               var5.drawText(
                  var19,
                  fit(var19, var23, var15.width() - 30.0F - var22),
                  var15.getFloatValue() + 18.0F,
                  var15.getFloatValue2() + (var15.height() - var19.height()) / 2.0F,
                  var12.getTextEnable().getZenithColor().method7294(var11)
               );
               if (var16) {
                  HoverInfo.request(t("gui.party.death.row"), var2.getText4().isEmpty() ? t("gui.party.death.row.desc") : var2.getText4());
               }
            },
            () -> {
               if (this.hovered.getOrDefault(var3, -1) == 0) {
                  boolean var3x = this.death == var2.getLongValue();
                  this.clearSelection();
                  this.death = var3x ? 0L : var2.getLongValue();
                  this.host.partySelectionChanged();
                  this.host.invalidate();
               }
            },
            null
         );
   }

   public void buildAside(Cloud var1) {
      if (var1 != null) {
         PartyMember var2 = var1.getPartyMember();
         if (this.member != 0L) {
            PartyMember.Data6 var8 = var2.getData6();
            PartyMember.Data3 var11 = var8 == null ? null : var8.method1216(this.member);
            if (var11 == null) {
               this.member = 0L;
            } else {
               PartyPlayerInfo var14 = var11.isFlag() ? var1.state().method8040(var11.uid()) : null;
               this.host.asideCaption(var11.username());
               this.host
                  .asideInfo(
                     "gui.friends.status",
                     () -> !var11.isFlag() ? t("gui.friends.status.offline") : PartyManager.method52(var2.method1141(var11.uid())),
                     "gui.friends.presence.desc"
                  );
               if (var14 != null) {
                  this.host.asideInfo("gui.friends.playingOn", () -> {
                     String var3x = ServerNameUtils.method5643(var14.server(), var2.method1141(var11.uid()).mode());
                     return var3x.isEmpty() ? "-" : var3x;
                  }, "gui.friends.playingOn.desc");
                  this.host
                     .asideInfo(
                        "gui.friends.health",
                        () -> Math.round(var14.getFloatValue3() + var14.getFloatValue5()) + " / " + Math.round(var14.getFloatValue4()),
                        "gui.party.hp.desc"
                     );
                  if (var14.method3291(var1.method7911(), var1.method7913())) {
                     this.host
                        .asideInfo(
                           "gui.friends.distance",
                           () -> meters(distance(var14.getDoubleValue(), var14.getDoubleValue2(), var14.getDoubleValue3())),
                           "gui.friends.distance.desc"
                        );
                  }
               }

               this.host.asideInfo("gui.friends.mcName", var11::getText2, "gui.friends.mcName.desc");
               this.host.asideHint(t("gui.party.notFriend"));
               this.leaderActions(var1, var11.uid());
            }
         } else if (this.mark != 0L) {
            PartyMember.Data12 var7 = null;

            for (PartyMember.Data12 var12 : var2.method1138()) {
               if (var12.getLongValue() == this.mark) {
                  var7 = var12;
               }
            }

            if (var7 == null) {
               this.mark = 0L;
            } else {
               PartyMember.Data12 var10 = var7;
               this.host.asideCaption(var10.name());
               this.host
                  .asideInfo(
                     "gui.party.mark.coords",
                     () -> coords(var10.getDoubleValue(), var10.getDoubleValue2(), var10.getDoubleValue3()),
                     "gui.party.mark.coords.desc"
                  );
               this.host.asideInfo("gui.party.mark.dim", () -> dimension(var10.dimension()), "gui.party.mark.dim.desc");
               this.host.asideInfo("gui.party.mark.author", var10::getText5, "gui.party.mark.author.desc");
               this.host.asideInfo("gui.party.mark.time", () -> ago(var10.time()), "gui.party.mark.time.desc");
               boolean var13 = PartyMarksModule.INSTANCE.getLongValue5() == var10.getLongValue();
               this.host.asideAction(var13 ? "gui.party.unfocus" : "gui.party.focus", MenuGlyph.GPS, () -> "", () -> {
                  if (PartyMarksModule.INSTANCE.getLongValue5() == var10.getLongValue()) {
                     PartyMarksModule.INSTANCE.clearFocus();
                  } else {
                     PartyMarksModule.INSTANCE.method5420(var10.getLongValue());
                  }

                  this.host.invalidate();
               }, "gui.party.focus.desc");
               this.host.asideAction("gui.party.copyCoords", MenuGlyph.COPY, () -> "", () -> {
                  copy(coords(var10.getDoubleValue(), var10.getDoubleValue2(), var10.getDoubleValue3()));
                  this.host.partyNotice(t("gui.friends.copied"));
               }, "gui.party.copyCoords.desc");
               PartyMember.Data6 var6 = var2.getData6();
               if (var10.getLongValue2() == var1.getLongValue() || var6 != null && var6.method1215(var1.getLongValue())) {
                  this.host.asideAction("gui.party.removeMark", MenuGlyph.CLOSE, () -> "", () -> {
                     var2.method1161(var10.getLongValue());
                     this.mark = 0L;
                     this.host.invalidate();
                  }, "gui.party.removeMark.desc");
               }
            }
         } else {
            if (this.death != 0L) {
               PartyMember.Data4 var3 = var2.method1140(this.death);
               if (var3 == null) {
                  this.death = 0L;
                  return;
               }

               boolean var4 = var3.uid() == var1.getLongValue() || var3.getLongValue() < 0L;
               this.host.asideCaption(var4 ? t("cloud.death.mine") : var3.name());
               this.host.asideInfo("gui.party.death.cause", () -> var3.getText4().isEmpty() ? "-" : var3.getText4(), "gui.party.death.cause.desc");
               this.host.asideInfo("gui.party.death.when", () -> ago(var3.time()), "gui.party.death.when.desc");
               this.host
                  .asideInfo(
                     "gui.party.mark.coords", () -> coords(var3.getDoubleValue(), var3.getDoubleValue2(), var3.getDoubleValue3()), "gui.party.mark.coords.desc"
                  );
               this.host
                  .asideInfo(
                     "gui.party.mark.server", () -> ServerNameUtils.display(var3.server()) + " · " + dimension(var3.dimension()), "gui.party.mark.server.desc"
                  );
               if (!var3.getItems().isEmpty()) {
                  this.host.asideCaption(t("gui.party.death.loot") + " · " + var3.getItems().size());
                  this.itemGrid(var3.getItems());
               }

               boolean var5 = PartyMarksModule.INSTANCE.getLongValue22() == var3.getLongValue();
               this.host.asideAction(var5 ? "gui.party.unfocus" : "gui.party.focus", MenuGlyph.GPS, () -> "", () -> {
                  if (PartyMarksModule.INSTANCE.getLongValue22() == var3.getLongValue()) {
                     PartyMarksModule.INSTANCE.clearFocus();
                  } else {
                     PartyMarksModule.INSTANCE.method5421(var3.getLongValue());
                  }

                  this.host.invalidate();
               }, "gui.party.focus.desc");
               this.host.asideAction("gui.party.copyCoords", MenuGlyph.COPY, () -> "", () -> {
                  copy(coords(var3.getDoubleValue(), var3.getDoubleValue2(), var3.getDoubleValue3()));
                  this.host.partyNotice(t("gui.friends.copied"));
               }, "gui.party.copyCoords.desc");
               this.host.asideAction("gui.party.forgetDeath", MenuGlyph.CLOSE, () -> "", () -> {
                  var2.method1164(var3.getLongValue());
                  this.death = 0L;
                  this.host.invalidate();
               }, "gui.party.forgetDeath.desc");
            }
         }
      }
   }

   public void leaderActions(Cloud var1, long var2) {
      PartyMember.Data6 var4 = var1.getPartyMember().getData6();
      if (var4 != null && var4.method1216(var2) != null && var2 != var1.getLongValue()) {
         this.host.asideCaption("gui.party.aside.caption");
         if (var4.method1215(var1.getLongValue())) {
            this.host.asideAction("gui.party.promote", MenuGlyph.PIN, () -> "", () -> {
               var1.getPartyMember().method1158(var2);
               this.host.invalidate();
            }, "gui.party.promote.desc");
            this.host.asideAction("gui.party.kick", MenuGlyph.CLOSE, () -> "", () -> {
               var1.getPartyMember().method1157(var2);
               this.member = 0L;
               this.host.invalidate();
            }, "gui.party.kick.desc");
         } else {
            this.host.asideInfo("gui.party.role", () -> t(var4.method1215(var2) ? "gui.party.role.leader" : "gui.party.role.member"), "gui.party.role.desc");
         }
      }
   }

   private void itemGrid(List<PartyMember.Data11> var1) {
      float var2 = this.host.asideWidth() - 16.0F;
      int var3 = Math.max(1, (int)((var2 + 2.0F) / 19.0F));
      int var4 = (var1.size() + var3 - 1) / var3;
      var var5 = new ArrayList<>(var1.size());

      for (PartyMember.Data11 var7 : var1) {
         Identifier var8 = Identifier.tryParse(var7.getText());
         ItemStack var9 = var8 == null ? ItemStack.EMPTY : new ItemStack(Registries.ITEM.get(var8), Math.max(1, var7.count()));
         if (!var9.isEmpty() && var9.isDamageable()) {
            var9.setDamage(Math.min(var7.getIntValue2(), var9.getMaxDamage()));
         }

         if (!var9.isEmpty() && var7.isFlag()) {
            var9.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
         }

         var5.add(var9);
      }

      this.host
         .asideCustom(
            var4 * 19.0F + 4.0F,
            (var2x, var3x, var4x, var5x, var6, var7x, var8x, var9x) -> {
               for (int var10 = 0; var10 < var5.size(); var10++) {
                  float var11 = var5x + 8.0F + var10 % var3 * 19.0F;
                  float var12 = var6 + 2.0F + var10 / var3 * 19.0F;
                  var2x.drawRoundedRect(
                     var11 - 1.0F,
                     var12 - 1.0F,
                     18.0F,
                     18.0F,
                     ProxyDotRadiusData.method8069(3.0F),
                     var9x.getDisableActiveBg().getZenithColor().method7294(var8x * 0.6F)
                  );
                  ItemStack var13 = (ItemStack)var5.get(var10);
                  if (!var13.isEmpty()) {
                     var2x.getMatrices().pushMatrix();
                     var2x.getMatrices().translate(var11, var12);
                     var2x.drawItem(var13, 0, 0);
                     var2x.drawItemBar(var13, 0, 0);
                     if (var13.getCount() > 1) {
                        Font var14 = Fonts.NEW_MEDIUM.getFont(4.6F);
                        String var15 = String.valueOf(var13.getCount());
                        var2x.drawText(var14, var15, 16.0F - var14.width(var15), 16.0F - var14.height(), new ZenithColor(255, 255, 255).method7294(var8x));
                     }

                     var2x.getMatrices().popMatrix();
                     if (new BoundsData(var11, var12, 16.0F, 16.0F).contains(var3x, var4x)) {
                        HoverInfo.request(var13.getName().getString(), var13.getCount() + " ×");
                     }
                  }
               }
            }
         );
   }

   private static String where(Cloud var0, PartyPlayerInfo var1) {
      if (var1.method3291(var0.method7911(), var0.method7913())) {
         return meters(distance(var1.getDoubleValue(), var1.getDoubleValue2(), var1.getDoubleValue3()));
      } else {
         String var2 = var1.server();
         if (var2.isEmpty()) {
            return t("cloud.presence.menu");
         } else {
            String var3 = ServerNameUtils.display(var2);
            return var3.length() > 12 ? var3.substring(0, 12) : var3;
         }
      }
   }

   private static boolean sameWorld(Cloud var0, String var1, String var2) {
      return mc.player != null && var1.equals(var0.method7911()) && var2.equals(var0.method7913());
   }

   private void avatar(MenuDrawContext var1, String var2, long var3, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      Identifier var10 = var3 > 0L ? CloudAvatarCache.get(Long.toString(var3)) : null;
      ProxyDotRadiusData var11 = ProxyDotRadiusData.method8069(var7 / 2.0F);
      if (var10 != null) {
         float var14 = this.avatarFade.computeIfAbsent(var3, var0 -> new Animation(220L, 0.0F, Easing.INSTANCE12)).method2808(1.0F);
         var1.drawRoundedTexture(var10, var5, var6, var7, var7, var11, ZenithColor.INSTANCE.method7294(var8 * var14));
      } else {
         var1.drawRoundedRect(var5, var6, var7, var7, var11, var9.getSurfaceEnableBackground().getZenithColor().method7294(var8 * 2.4F));
         String var12 = var2 != null && !var2.isEmpty() ? var2.substring(0, 1).toUpperCase(Locale.ROOT) : "?";
         Font var13 = Fonts.NEW_MEDIUM.getFont(var7 * 0.5F);
         var1.drawText(
            var13,
            var12,
            var5 + (var7 - var13.width(var12)) / 2.0F,
            var6 + (var7 - var13.height()) / 2.0F,
            var9.getTextTertiary().getZenithColor().method7294(var8)
         );
      }
   }

   private void pill(
      MenuDrawContext var1, String var2, BoundsData var3, String var4, boolean var5, boolean var6, float var7, ZenithStyle var8, ZenithColor var9, float var10
   ) {
      float var11 = this.hover(var2, var6);
      ProxyDotRadiusData var12 = ProxyDotRadiusData.method8069(var3.height() / 2.0F);
      if (var5) {
         var1.drawRoundedRect(
            var3.getFloatValue(),
            var3.getFloatValue2(),
            var3.width(),
            var3.height(),
            var12,
            var8.getPrimaryColor().getZenithColor().method7292(0.75F + var11 * 0.25F).method7294(var7)
         );
      } else if (var9 != null) {
         var1.drawRoundedRect(
            var3.getFloatValue(), var3.getFloatValue2(), var3.width(), var3.height(), var12, var9.method7292(0.1F + var11 * 0.2F).method7294(var7)
         );
      } else {
         var1.drawRoundedRect(
            var3.getFloatValue(),
            var3.getFloatValue2(),
            var3.width(),
            var3.height(),
            var12,
            var8.getDisableActiveBg().getZenithColor().method7294(var7 * (0.8F + var11 * 0.6F))
         );
      }

      Font var13 = Fonts.NEW_MEDIUM.getFont(4.6F);
      ZenithColor var14 = var5
         ? new ZenithColor(255, 255, 255).method7294(var7)
         : (
            var9 != null
               ? var9.method7295(new ZenithColor(255, 255, 255), var11 * 0.4F).method7294(var7)
               : var8.getTextSecondary().getZenithColor().method7295(var8.getTextEnable().getZenithColor(), var11).method7294(var7)
         );
      float var15 = var10 > 0.0F ? var3.getFloatValue() + var10 : var3.getFloatValue() + (var3.width() - var13.width(var4)) / 2.0F;
      var1.drawText(var13, var4, var15, var3.getFloatValue2() + (var3.height() - var13.height()) / 2.0F, var14);
   }

   private static void plate(MenuDrawContext var0, float var1, float var2, float var3, float var4, float var5, float var6, ZenithStyle var7) {
      var0.drawRoundedRect(
         var1, var2, var3, var4, ProxyDotRadiusData.method8069(4.0F), var7.getDisableActiveBg().getZenithColor().method7294(var6 * (0.5F + 0.25F * var5))
      );
   }

   private static void diamond(MenuDrawContext var0, float var1, float var2, float var3, ZenithColor var4) {
      var0.pushMatrix();
      var0.getMatrices().translate(var1, var2);
      var0.getMatrices().rotate((float)Math.toRadians(45.0));
      var0.drawRect(-var3 / 2.0F, -var3 / 2.0F, var3, var3, var4);
      var0.popMatrix();
   }

   private float hover(String var1, boolean var2) {
      return this.hover.computeIfAbsent(var1, var0 -> new Animation(120L, 0.0F, Easing.INSTANCE12)).method2808(var2 ? 1.0F : 0.0F);
   }

   private static String fit(Font var0, String var1, float var2) {
      if (!(var2 <= 0.0F) && var1 != null) {
         if (var0.width(var1) <= var2) {
            return var1;
         } else {
            int var3 = var1.length();

            while (var3 > 0 && var0.width(var1.substring(0, var3) + "…") > var2) {
               var3--;
            }

            return var3 == 0 ? "" : var1.substring(0, var3) + "…";
         }
      } else {
         return "";
      }
   }

   private static double distance(double var0, double var2, double var4) {
      if (mc.player == null) {
         return -1.0;
      } else {
         double var6 = var0 - mc.player.getX();
         double var8 = var2 - mc.player.getY();
         double var10 = var4 - mc.player.getZ();
         return Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
      }
   }

   private static String meters(double var0) {
      if (var0 < 0.0) {
         return "—";
      } else {
         return var0 >= 1000.0 ? String.format(Locale.ROOT, "%.1f", var0 / 1000.0) + " " + t("gui.unit.km") : Math.round(var0) + " " + t("gui.unit.meter");
      }
   }

   private static String coords(double var0, double var2, double var4) {
      return (int)Math.floor(var0) + " " + (int)Math.floor(var2) + " " + (int)Math.floor(var4);
   }

   private static String dimension(String var0) {
      return switch (var0) {
         case "minecraft:overworld" -> t("gui.party.dim.overworld");
         case "minecraft:the_nether" -> t("gui.party.dim.nether");
         case "minecraft:the_end" -> t("gui.party.dim.end");
         default -> var0.contains(":") ? var0.substring(var0.indexOf(58) + 1) : var0;
      };
   }

   private static String ago(long var0) {
      long var2 = Math.max(0L, (System.currentTimeMillis() - var0) / 1000L);
      if (var2 < 60L) {
         return t("gui.party.ago.now");
      } else if (var2 < 3600L) {
         return t("gui.party.ago.min").replace("{n}", String.valueOf(var2 / 60L));
      } else {
         return var2 < 86400L
            ? t("gui.party.ago.hour").replace("{n}", String.valueOf(var2 / 3600L))
            : t("gui.party.ago.day").replace("{n}", String.valueOf(var2 / 86400L));
      }
   }

   private static void copy(String var0) {
      if (mc.keyboard != null) {
         mc.keyboard.setClipboard(var0);
      }
   }

   private static String t(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   private static final class Item {
      private final PartyBlock.Kind kind;
      private final BoundsData area;
      private final Object value;

      public PartyBlock.Kind kind() {
         return this.kind;
      }

      public BoundsData area() {
         return this.area;
      }

      public Object value() {
         return this.value;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Item other)) return false;
         return java.util.Objects.equals(this.kind, other.kind) && java.util.Objects.equals(this.area, other.area) && java.util.Objects.equals(this.value, other.value);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(kind, area, value);
      }

      @Override
      public String toString() {
         return "Item[" + "kind=" + this.kind + ", " + "area=" + this.area + ", " + "value=" + this.value + "]";
      }

      private Item(PartyBlock.Kind var1, BoundsData var2, Object var3) {
         this.kind = var1;
         this.area = var2;
         this.value = var3;
      }
   }

   private static enum Kind {
      MEMBER,
      INVITED,
      PLUS,
      INVITE_ALL,
      CANDIDATE,
      HERE,
      MARK,
      HINT,
      VOICE,
      LEAVE;
   }
}

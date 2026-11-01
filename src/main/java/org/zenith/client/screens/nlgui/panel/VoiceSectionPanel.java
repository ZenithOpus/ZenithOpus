package org.zenith.client.screens.nlgui.panel;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.api.VoiceDeviceSetting;
import org.zenith.utility.misc.AudioDevices;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.render.Voice;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class VoiceSectionPanel extends SectionPanel {
   private static final float HERO_HEIGHT = 40.0F;
   private static final float PICKER_HEIGHT = 34.0F;
   private static final float TEST_HEIGHT = 50.0F;
   private static final float TILE_HEIGHT = 28.0F;
   private static final float FRIEND_HEIGHT = 18.0F;
   private static final float DEVICE_ROW_HEIGHT = 15.0F;
   private static final int METER_SEGMENTS = 32;
   private static final long REOPEN_GAP_MS = 400L;
   private static final long PING_EVERY_MS = 2000L;
   private static final int PICKER_NONE = -1;
   private static final int PICKER_INPUT = 0;
   private static final ZenithColor RADIO = new ZenithColor(-10443270);
   private static final ZenithColor LIVE = new ZenithColor(-495247);
   private final Animation enterWave = new Animation(560L, 1.0F, Easing.INSTANCE12);
   private final Animation asideWave = new Animation(560L, 1.0F, Easing.INSTANCE12);
   private final Animation listWave = new Animation(380L, 1.0F, Easing.INSTANCE12);
   private final Animation toggleKnob = new Animation(180L, 0.0F, Easing.INSTANCE12);
   private final Animation heroOn = new Animation(260L, 0.0F, Easing.INSTANCE12);
   private final Animation testOn = new Animation(220L, 0.0F, Easing.INSTANCE12);
   private final Animation meterFill = new Animation(70L, 0.0F, Easing.INSTANCE12);
   private final TextSetting roomName = new TextSetting(
      "gui.voice.room.name", "gui.voice.room.name.desc", "", "gui.voice.room.name.empty", TextSetting.Entry.method2125(24)
   );
   private final TextSetting roomCode = new TextSetting(
      "gui.voice.room.code", "gui.voice.room.code.desc", "", "gui.voice.room.code.empty", TextSetting.Entry.method2125(8)
   );
   private final Map<String, Animation> hover = new HashMap<>();
   private final Map<String, Animation> pick = new HashMap<>();
   private final Map<String, Integer> hoveredTile = new HashMap<>();
   private int openPicker = -1;
   private boolean advanced;
   private String lastSignature = "";
   private long lastTickAt;
   private long lastPingAt;
   private int order;
   private int asideOrder;
   private float breath;
   private float meterPeak;
   private long meterPeakAt;

   private static Voice voice() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getVoice();
   }

   @Override
   public String headerTitle() {
      return "Voice";
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.MIC;
   }

   @Override
   public String sectionDescription() {
      return "gui.voice.desc";
   }

   @Override
   public void build() {
      this.order = 0;
      this.asideOrder = 0;
      Voice var1 = voice();
      if (var1 == null) {
         this.hint(t("gui.voice.missing"));
      } else {
         if (!var1.isFlag()) {
            var1.method335();
         }

         this.hero(var1);
         if (!var1.isEnabled()) {
            this.hint(t("gui.voice.off.hint"));
         }

         this.caption("gui.voice.caption.devices");
         this.pickers(var1);
         if (this.openPicker != -1) {
            boolean var2 = this.openPicker == 0;
            this.deviceList(var2 ? var1.input : var1.output, var2 ? var1.method338() : var1.method339());
         }

         this.setting(var1.micGain);
         this.setting(var1.volume);
         this.setting(var1.denoise);
         if (var1.denoise.isEnabled()) {
            this.setting(var1.rnnoise);
         }

         this.micTest(var1);
         this.caption("gui.voice.caption.talk");
         this.choice(
            "activation",
            List.of(
               new VoiceSectionPanel.Tile("gui.voice.activation.ptt", "gui.voice.activation.ptt.note"),
               new VoiceSectionPanel.Tile("gui.voice.activation.voice", "gui.voice.activation.voice.note")
            ),
            var1.activation::getIndex,
            var1x -> select(var1.activation, var1x)
         );
         if (var1.activation.isSelected(0)) {
            this.setting(var1.talkKey);
         } else {
            this.setting(var1.sensitivity);
         }

         this.setting(var1.radioKey);
         this.caption("gui.voice.caption.hear");
         this.choice(
            "range",
            List.of(
               new VoiceSectionPanel.Tile("gui.voice.range.whisper", "gui.voice.range.whisper.note"),
               new VoiceSectionPanel.Tile("gui.voice.range.normal", "gui.voice.range.normal.note"),
               new VoiceSectionPanel.Tile("gui.voice.range.loud", "gui.voice.range.loud.note")
            ),
            var1.range::getIndex,
            var1x -> select(var1.range, var1x)
         );
         this.setting(var1.radioVolume);
         this.setting(var1.acceptRadio);
         this.setting(var1.muffle);
         this.channels(var1);
         this.row("gui.voice.caption.more").icon(MenuGlyph.MODE).value(() -> t(this.advanced ? "gui.voice.more.hide" : "gui.voice.more.show")).onClick(() -> {
            this.advanced = !this.advanced;
            this.invalidate();
         }).description("gui.voice.more.desc").add();
         if (this.advanced) {
            this.setting(var1.quality);
            this.setting(var1.spatial);
            if (var1.spatial.isEnabled()) {
               this.setting(var1.hrtf);
            }

            this.setting(var1.overlay);
            this.setting(var1.heads);
            this.setting(var1.muteKey);
            this.setting(var1.deafenKey);
            this.action("gui.voice.refresh", MenuGlyph.RESET, () -> t("gui.voice.refresh.value"), () -> {
               var1.method335();
               this.invalidate();
            }, "gui.voice.refresh.desc");
         }

         this.buildAside(var1);
      }
   }

   private void channels(Voice var1) {
      Cloud var2 = Zenith.getInstance().getCloud();
      if (var2 != null) {
         PartyMember var3 = var2.getPartyMember();
         this.caption("gui.voice.caption.channels");
         if (!var2.method7860()) {
            this.hint(t("gui.voice.channels.unsupported"));
         } else {
            this.choice(
               "channel",
               List.of(
                  new VoiceSectionPanel.Tile("gui.voice.channel.near", "gui.voice.channel.near.note"),
                  new VoiceSectionPanel.Tile("gui.voice.channel.party", "gui.voice.channel.party.note"),
                  new VoiceSectionPanel.Tile("gui.voice.channel.room", "gui.voice.channel.room.note")
               ),
               var1.defaultChannel::getIndex,
               var1x -> select(var1.defaultChannel, var1x)
            );
            this.setting(var1.partyKey);
            this.setting(var1.roomKey);
            this.setting(var1.whisperKey);
            this.setting(var1.channelVolume);
            PartyMember.Data var4 = var3.getData();
            if (var4 == null) {
               this.setting(this.roomName);
               this.action("gui.voice.room.create", MenuGlyph.ACTION, () -> t("gui.voice.room.create.value"), () -> {
                  var3.method1171(this.roomName.getValue() == null ? "" : this.roomName.getValue());
                  this.invalidate();
               }, "gui.voice.room.create.desc");
               this.setting(this.roomCode);
               this.action("gui.voice.room.join", MenuGlyph.ACTION, () -> t("gui.voice.room.join.value"), () -> {
                  String var2x = this.roomCode.getValue() == null ? "" : this.roomCode.getValue().strip();
                  if (var2x.length() == 5) {
                     var3.method1172(var2x);
                  }

                  this.invalidate();
               }, "gui.voice.room.join.desc");

               for (PartyMember.Data16 var6 : var3.method1145()) {
                  this.row(t("gui.voice.room.invite").replace("{n}", var6.getText3()).replace("{r}", var6.name()))
                     .icon(MenuGlyph.SOUND)
                     .value(() -> t("gui.voice.room.join.value"))
                     .onClick(() -> {
                        var3.method1172(var6.getText());
                        this.invalidate();
                     })
                     .onRightClick(() -> {
                        var3.method1173(var6);
                        this.invalidate();
                     })
                     .description("gui.voice.room.invite.desc")
                     .add();
               }
            } else {
               this.info("gui.voice.room.current", () -> var4.name() + " · " + var4.getText(), "gui.voice.room.current.desc");
               this.action("gui.voice.room.copy", MenuGlyph.COPY, () -> var4.getText(), () -> {
                  if (mc.keyboard != null) {
                     mc.keyboard.setClipboard(var4.getText());
                  }
               }, "gui.voice.room.copy.desc");

               for (PartyMember.Data3 var13 : var4.getItems()) {
                  boolean var7 = var1.getVoiceState().method5365(var13.uid());
                  this.info(
                     var13.uid() == var4.getLongValue() ? "★ " + var13.username() : var13.username(),
                     () -> var1.getVoiceState().method5365(var13.uid())
                        ? t("gui.voice.room.speaking")
                        : (var13.uid() == var2.getLongValue() ? t("gui.party.you") : ""),
                     var7 ? "gui.voice.room.speaking" : "gui.voice.room.member.desc"
                  );
               }

               for (PartyPlayerInfo var14 : var2.state().method8038()) {
                  boolean var15 = false;

                  for (PartyMember.Data3 var9 : var4.getItems()) {
                     if (var9.uid() == var14.uid()) {
                        var15 = true;
                        break;
                     }
                  }

                  if (!var15 && var14.method3289()) {
                     this.action(
                        t("gui.voice.room.inviteFriend").replace("{n}", Voice.method367(var14)),
                        MenuGlyph.FRIENDS,
                        () -> t("gui.voice.room.inviteFriend.value"),
                        () -> var3.method1175(var14.uid()),
                        "gui.voice.room.inviteFriend.desc"
                     );
                  }
               }

               this.action("gui.voice.room.leave", MenuGlyph.CLOSE, () -> t("gui.voice.room.leave.value"), () -> {
                  var3.method1174();
                  this.invalidate();
               }, "gui.voice.room.leave.desc");
            }

            long var12 = var3.getLongValue();
            if (var12 != 0L) {
               this.action(t("gui.voice.whisper.target").replace("{n}", var1.method348(var12)), MenuGlyph.SOUND, () -> t("gui.voice.whisper.clear"), () -> {
                  var3.method1176(0L);
                  this.invalidate();
               }, "gui.voice.whisper.target.desc");
            } else {
               this.hint(t("gui.voice.whisper.hint"));
            }
         }
      }
   }

   private static void select(ModeSetting var0, int var1) {
      var0.getValues().get(var1).method2103();
   }

   private static float wave(Animation var0, int var1) {
      float var2 = var0.method2808(1.0F);
      return MathHelper.clamp((var2 - var1 * 0.07F) / 0.5F, 0.0F, 1.0F);
   }

   private static void restart(Animation var0) {
      var0.reset(0.0F);
      var0.method2810(1.0F);
   }

   private void hero(Voice var1) {
      int var2 = this.order++;
      this.custom(
         40.0F,
         (var3, var4, var5, var6, var7, var8, var9, var10) -> {
            float var11 = wave(this.enterWave, var2);
            if (!(var11 <= 0.002F)) {
               var9 *= var11;
               var6 += (1.0F - var11) * 8.0F;
               float var12 = var6 + 8.0F;
               float var13 = var6 + var8 - 8.0F;
               float var14 = var7 + 3.0F;
               float var15 = 34.0F;
               boolean var16 = new BoundsData(var12, var14, var13 - var12, var15).contains(var4, var5);
               boolean var17 = var1.isEnabled();
               float var18 = this.heroOn.method2808(var17 ? 1.0F : 0.0F);
               plate(var3, var12, var14, var13 - var12, var15, this.hover("hero", var16), 0.0F, var9, var10);
               var3.drawRoundedRect(
                  var12,
                  var14,
                  var13 - var12,
                  var15,
                  ProxyDotRadiusData.method8069(4.0F),
                  var10.getPrimaryColor().getZenithColor().method7294(var9 * 0.07F * var18)
               );
               if (var16) {
                  HoverInfo.request(t("gui.voice.enabled"), t("gui.voice.enabled.desc"));
               }

               Voice.Type var19 = var1.method357();
               boolean var20 = var19 == Voice.Type.TALKING || var19 == Voice.Type.RADIO || var19 == Voice.Type.TESTING;
               float var21 = var20 ? var1.getVoiceState().getFloatValue2() : -100.0F;
               this.breath = this.breath + (MathHelper.clamp((var21 + 60.0F) / 45.0F, 0.0F, 1.0F) - this.breath) * 0.25F;
               ZenithColor var22 = var10.getTextTertiary().getZenithColor().method7295(var10.getPrimaryColor().getZenithColor(), var18);
               float var23 = var12 + 8.0F;
               float var24 = var14 + (var15 - 12.0F) / 2.0F;
               if (this.breath > 0.01F) {
                  float var25 = 18.0F + this.breath * 5.0F;
                  float var26 = var23 + 6.0F;
                  float var27 = var24 + 6.0F;
                  var3.drawRoundedRect(
                     var26 - var25 / 2.0F,
                     var27 - var25 / 2.0F,
                     var25,
                     var25,
                     ProxyDotRadiusData.method8069(var25 / 2.0F),
                     var10.getPrimaryColor().getZenithColor().method7294(var9 * 0.18F * this.breath)
                  );
               }

               bigGlyph(var3, var23, var24, 1.0F + this.breath * 0.12F, var22.method7294(var9));
               Font var38 = Fonts.NEW_MEDIUM.getFont(6.0F);
               Font var39 = Fonts.NEW_REGULAR.getFont(4.6F);
               float var40 = var12 + 8.0F + 12.0F + 8.0F;
               float var28 = var14 + (var15 - var38.height() - 3.0F - var39.height()) / 2.0F;
               var3.drawText(var38, t("gui.voice.enabled"), var40, var28, var10.getTextEnable().getZenithColor().method7294(var9));
               float var29 = 3.0F;
               float var30 = var28 + var38.height() + 3.0F;
               float var31 = var20 ? 0.55F + 0.45F * (float)Math.sin(System.currentTimeMillis() / 160.0) : 1.0F;
               var3.drawRoundedRect(
                  var40,
                  var30 + var39.height() / 2.0F - var29 / 2.0F,
                  var29,
                  var29,
                  ProxyDotRadiusData.method8069(var29 / 2.0F),
                  var1.method360().method7294(var9 * var31)
               );
               String var32 = var17 ? hintLine(var1) : "";
               String var33 = var32.isEmpty() ? var1.method359() : var1.method359() + " · " + var32;
               float var34 = 20.0F;
               float var35 = var13 - 8.0F - var34 - 8.0F - (var40 + var29 + 4.0F);
               var3.drawText(var39, fitText(var39, var33, var35), var40 + var29 + 4.0F, var30, var10.getTextTertiary().getZenithColor().method7294(var9));
               this.drawToggle(var3, var13 - 8.0F - var34, var14 + (var15 - 10.0F) / 2.0F, var34, var17, var9, var10);
            }
         },
         () -> {
            var1.setEnabled(!var1.isEnabled());
            Zenith.getInstance().getClientSounds().method1562();
            this.invalidate();
         },
         null
      );
   }

   private static String hintLine(Voice var0) {
      if (var0.method357() != Voice.Type.READY) {
         return "";
      } else {
         return var0.activation.isSelected(0) ? t("gui.voice.hint.hold") + " " + Voice.method364(var0.talkKey) : t("gui.voice.hint.speak");
      }
   }

   private void drawToggle(MenuDrawContext var1, float var2, float var3, float var4, boolean var5, float var6, ZenithStyle var7) {
      float var8 = 10.0F;
      float var9 = this.toggleKnob.method2808(var5 ? 1.0F : 0.0F);
      ZenithColor var10 = var7.getTextTertiary().getZenithColor().method7294(0.35F).method7295(var7.getPrimaryColor().getZenithColor(), var9);
      var1.drawRoundedRect(var2, var3, var4, var8, ProxyDotRadiusData.method8069(var8 / 2.0F), var10.method7294(var6));
      float var11 = var8 - 3.0F;
      float var12 = var2 + 1.5F + (var4 - var11 - 3.0F) * var9;
      var1.drawRoundedRect(var12, var3 + 1.5F, var11, var11, ProxyDotRadiusData.method8069(var11 / 2.0F), new ZenithColor(255, 255, 255).method7294(var6));
   }

   private void pickers(Voice var1) {
      int var2 = this.order++;
      this.custom(
         34.0F,
         (var3, var4, var5, var6, var7, var8, var9, var10) -> {
            float var11 = wave(this.enterWave, var2);
            if (!(var11 <= 0.002F)) {
               var9 *= var11;
               var6 += (1.0F - var11) * 8.0F;
               float var12 = 4.0F;
               float var13 = var6 + 8.0F;
               float var14 = (var8 - 16.0F - var12) / 2.0F;
               int var15 = -1;

               for (int var16 = 0; var16 < 2; var16++) {
                  boolean var17 = var16 == 0;
                  float var18 = var13 + var16 * (var14 + var12);
                  float var19 = var7 + 2.0F;
                  float var20 = 30.0F;
                  boolean var21 = new BoundsData(var18, var19, var14, var20).contains(var4, var5);
                  if (var21) {
                     var15 = var16;
                     HoverInfo.request(t(var17 ? "gui.voice.input" : "gui.voice.output"), t(var17 ? "gui.voice.input.desc" : "gui.voice.output.desc"));
                  }

                  float var22 = this.hover("picker:" + var16, var21);
                  float var23 = this.pick("picker:" + var16, this.openPicker == var16);
                  plate(var3, var18, var19, var14, var20, var22, var23, var9, var10);
                  Font var24 = Fonts.NEW_MEDIUM.getFont(4.0F);
                  Font var25 = Fonts.NEW_MEDIUM.getFont(5.2F);
                  Font var26 = Fonts.NEW_REGULAR.getFont(4.2F);
                  float var27 = var18 + 6.0F;
                  ZenithColor var28 = var10.getTextTertiary().getZenithColor().method7294(var9);
                  (var17 ? MenuGlyph.MIC : MenuGlyph.SOUND).draw(var3, var27, var19 + 4.5F, var28);
                  var3.drawText(
                     var24, HoverInfo.spaced(t(var17 ? "gui.voice.input" : "gui.voice.output").toUpperCase(Locale.ROOT)), var27 + 9.0F, var19 + 5.5F, var28
                  );
                  VoiceDeviceSetting var29 = var17 ? var1.input : var1.output;
                  String var30 = var17 ? var1.inputName() : var1.method336();
                  if (var30.isEmpty()) {
                     var30 = t("gui.voice.device.none");
                  }

                  float var31 = var14 - 12.0F - 10.0F;
                  var3.drawText(
                     var25, fitText(var25, var30, var31), var27 + var22 * 1.5F, var19 + 13.0F, var10.getTextEnable().getZenithColor().method7294(var9)
                  );
                  String var32 = t(var29.method11().isEmpty() ? "gui.voice.device.auto" : "gui.voice.device.manual");
                  var3.drawText(var26, fitText(var26, var32, var31), var27 + var22 * 1.5F, var19 + 13.0F + var25.height() + 2.0F, var28);
                  chevron(
                     var3,
                     var18 + var14 - 10.0F,
                     var19 + var20 / 2.0F,
                     var23,
                     var10.getTextTertiary().getZenithColor().method7295(var10.getTextEnable().getZenithColor(), Math.max(var22, var23)).method7294(var9)
                  );
               }

               this.hoveredTile.put("pickers", var15);
            }
         },
         () -> {
            int var2x = this.hoveredTile.getOrDefault("pickers", -1);
            if (var2x >= 0) {
               if (this.openPicker == var2x) {
                  this.openPicker = -1;
               } else {
                  var1.method335();
                  this.openPicker = var2x;
                  restart(this.listWave);
               }

               this.invalidate();
            }
         },
         null
      );
   }

   private void deviceList(VoiceDeviceSetting var1, String var2) {
      String var3 = t("gui.voice.device.default") + (var2.isEmpty() ? "" : " · " + var2);
      this.deviceRow(0, var3, "", var1, t("gui.voice.device.auto.desc"));
      var var4 = var1.getValues();

      for (int var5 = 1; var5 < var4.size(); var5++) {
         String var6 = ((ModeSetting.Entry)var4.get(var5)).getKey();
         this.deviceRow(var5, AudioDevices.display(var6), var6, var1, "");
      }

      this.gap(GuiStyle.PADDING.intValue());
   }

   private void deviceRow(int var1, String var2, String var3, VoiceDeviceSetting var4, String var5) {
      String var6 = "device:" + var3;
      this.custom(
         15.0F,
         (var7, var8, var9, var10, var11, var12, var13, var14) -> {
            float var15 = wave(this.listWave, var1);
            if (!(var15 <= 0.002F)) {
               var13 *= var15;
               float var16 = (1.0F - var15) * -4.0F;
               float var17 = var10 + 8.0F + 4.0F;
               float var18 = var12 - 16.0F - 4.0F;
               float var19 = var11 + 1.0F + var16;
               float var20 = 13.0F;
               boolean var21 = new BoundsData(var17, var19, var18, var20).contains(var8, var9);
               boolean var22 = var3.equals(var4.method11());
               float var23 = this.hover(var6, var21);
               float var24 = this.pick(var6, var22);
               var7.drawRoundedRect(
                  var17,
                  var19,
                  var18,
                  var20,
                  ProxyDotRadiusData.method8069(3.0F),
                  var14.getDisableActiveBg().getZenithColor().method7294(var13 * Math.max(var24 * 0.8F, var23 * 0.45F))
               );
               if (var21 && !var5.isEmpty()) {
                  HoverInfo.request(var2, var5);
               }

               Font var25 = Fonts.NEW_MEDIUM.getFont(5.01F);
               float var26 = 4.0F;
               float var27 = var19 + var20 / 2.0F;
               var7.drawRoundedRect(
                  var17 + 5.0F,
                  var27 - var26 / 2.0F,
                  var26,
                  var26,
                  ProxyDotRadiusData.method8069(var26 / 2.0F),
                  var14.getTextTertiary().getZenithColor().method7294(var13 * 0.5F)
               );
               if (var24 > 0.01F) {
                  float var28 = var26 * var24;
                  var7.drawRoundedRect(
                     var17 + 5.0F + (var26 - var28) / 2.0F,
                     var27 - var28 / 2.0F,
                     var28,
                     var28,
                     ProxyDotRadiusData.method8069(var28 / 2.0F),
                     var14.getPrimaryColor().getZenithColor().method7294(var13)
                  );
               }

               var7.drawText(
                  var25,
                  fitText(var25, var2, var18 - 18.0F),
                  var17 + 13.0F + var23 * 1.5F,
                  var27 - var25.height() / 2.0F,
                  var14.getTextSecondary().getZenithColor().method7295(var14.getTextEnable().getZenithColor(), Math.max(var24, var23)).method7294(var13)
               );
            }
         },
         () -> {
            var4.method10(var3);
            this.openPicker = -1;
            this.invalidate();
         },
         null
      );
   }

   private void micTest(Voice var1) {
      int var2 = this.order++;
      this.custom(
         50.0F,
         (var3, var4, var5, var6, var7, var8, var9, var10) -> {
            float var11 = wave(this.enterWave, var2);
            if (!(var11 <= 0.002F)) {
               var9 *= var11;
               var6 += (1.0F - var11) * 8.0F;
               float var12 = var6 + 8.0F;
               float var13 = var6 + var8 - 8.0F;
               float var14 = var7 + 3.0F;
               float var15 = 44.0F;
               boolean var16 = var1.isFlag3();
               float var17 = this.testOn.method2808(var16 ? 1.0F : 0.0F);
               boolean var18 = new BoundsData(var12, var14, var13 - var12, var15).contains(var4, var5);
               if (var18) {
                  HoverInfo.request(t("gui.voice.micTest"), t("gui.voice.micTest.desc"));
               }

               plate(var3, var12, var14, var13 - var12, var15, this.hover("test", var18), 0.0F, var9, var10);
               Font var19 = Fonts.NEW_MEDIUM.getFont(5.4F);
               Font var20 = Fonts.NEW_REGULAR.getFont(4.4F);
               Font var21 = Fonts.NEW_MEDIUM.getFont(5.01F);
               String var22 = t(var16 ? "gui.voice.micTest.stop" : "gui.voice.micTest.start");
               float var23 = var21.width(var22) + 16.0F;
               float var24 = 13.0F;
               float var25 = var13 - 8.0F - var23;
               float var26 = var14 + 7.0F;
               float var27 = this.hover("test:button", var18);
               ZenithColor var28 = var10.getPrimaryColor().getZenithColor().method7295(var10.getDisableActiveBg().getZenithColor(), var17);
               float var29 = var27 * 1.0F;
               var3.drawRoundedRect(
                  var25 - var29, var26 - var29 * 0.5F, var23 + var29 * 2.0F, var24 + var29, ProxyDotRadiusData.method8069(var24 / 2.0F), var28.method7294(var9)
               );
               var3.drawText(
                  var21,
                  var22,
                  var25 + (var23 - var21.width(var22)) / 2.0F,
                  var26 + (var24 - var21.height()) / 2.0F,
                  new ZenithColor(255, 255, 255).method7295(var10.getTextEnable().getZenithColor(), var17).method7294(var9)
               );
               float var30 = var12 + 8.0F;
               if (var17 > 0.01F) {
                  float var31 = 3.0F * var17;
                  float var32 = 0.6F + 0.4F * (float)Math.sin(System.currentTimeMillis() / 180.0);
                  var3.drawRoundedRect(
                     var30,
                     var14 + 7.0F + var19.height() / 2.0F - var31 / 2.0F,
                     var31,
                     var31,
                     ProxyDotRadiusData.method8069(var31 / 2.0F),
                     LIVE.method7294(var9 * var32)
                  );
                  var30 += 6.0F * var17;
               }

               float var35 = var25 - 6.0F - var30;
               var3.drawText(var19, fitText(var19, t("gui.voice.micTest"), var35), var30, var14 + 7.0F, var10.getTextEnable().getZenithColor().method7294(var9));
               String var36 = t(var16 ? "gui.voice.micTest.live" : "gui.voice.micTest.note");
               var3.drawText(
                  var20, fitText(var20, var36, var35), var30, var14 + 7.0F + var19.height() + 3.0F, var10.getTextTertiary().getZenithColor().method7294(var9)
               );
               this.drawMeter(var3, var1, var12 + 8.0F, var14 + var15 - 11.0F, var13 - 8.0F - (var12 + 8.0F), var9, var10);
            }
         },
         () -> {
            var1.method333(!var1.isFlag3());
            this.invalidate();
         },
         null
      );
   }

   private void drawMeter(MenuDrawContext var1, Voice var2, float var3, float var4, float var5, float var6, ZenithStyle var7) {
      float var8 = var2.getVoiceState().getFloatValue2();
      float var9 = this.meterFill.method2808(MathHelper.clamp((var8 + 70.0F) / 60.0F, 0.0F, 1.0F));
      long var10 = System.currentTimeMillis();
      if (var9 >= this.meterPeak) {
         this.meterPeak = var9;
         this.meterPeakAt = var10;
      } else if (var10 - this.meterPeakAt > 500L) {
         this.meterPeak = Math.max(var9, this.meterPeak - 0.012F);
      }

      boolean var12 = var2.activation.isSelected(1);
      float var13 = MathHelper.clamp((var2.method341() + 70.0F) / 60.0F, 0.0F, 1.0F);
      float var14 = 1.5F;
      float var15 = (var5 - var14 * 31.0F) / 32.0F;
      float var16 = 5.0F;
      int var17 = this.meterPeak > 0.02F ? Math.min(31, (int)(this.meterPeak * 32.0F)) : -1;

      for (int var18 = 0; var18 < 32; var18++) {
         float var19 = (var18 + 0.5F) / 32.0F;
         boolean var20 = var19 <= var9;
         ZenithColor var21;
         if (var18 == var17 && !var20) {
            var21 = var7.getPrimaryColor().getZenithColor().method7294(0.55F);
         } else if (!var20) {
            var21 = var7.getTextTertiary().getZenithColor().method7294(0.22F);
         } else if (var12 && var19 < var13) {
            var21 = var7.getTextTertiary().getZenithColor().method7294(0.8F);
         } else {
            var21 = var7.getPrimaryColor().getZenithColor();
         }

         float var22 = var20 ? var16 : var16 - 1.0F;
         var1.drawRoundedRect(
            var3 + var18 * (var15 + var14), var4 + (var16 - var22) / 2.0F, var15, var22, ProxyDotRadiusData.method8069(1.0F), var21.method7294(var6)
         );
      }

      if (var12) {
         float var23 = var3 + var5 * var13;
         var1.drawRect(var23 - 0.5F, var4 - 2.0F, 1.0F, var16 + 4.0F, var7.getTextEnable().getZenithColor().method7294(var6));
      }
   }

   private void choice(String var1, List<VoiceSectionPanel.Tile> var2, IntSupplier var3, IntConsumer var4) {
      int var5 = this.order++;
      this.custom(
         28.0F,
         (var5x, var6, var7, var8, var9, var10, var11, var12) -> {
            float var13 = wave(this.enterWave, var5);
            if (!(var13 <= 0.002F)) {
               var11 *= var13;
               var8 += (1.0F - var13) * 8.0F;
               float var14 = 4.0F;
               float var15 = var8 + 8.0F;
               float var16 = (var10 - 16.0F - var14 * (var2.size() - 1)) / var2.size();
               int var17 = -1;

               for (int var18 = 0; var18 < var2.size(); var18++) {
                  VoiceSectionPanel.Tile var19 = (VoiceSectionPanel.Tile)var2.get(var18);
                  float var20 = var15 + var18 * (var16 + var14);
                  float var21 = var9 + 2.0F;
                  float var22 = 24.0F;
                  boolean var23 = new BoundsData(var20, var21, var16, var22).contains(var6, var7);
                  if (var23) {
                     var17 = var18;
                     HoverInfo.request(t(var19.title()), t(var19.note()));
                  }

                  String var24 = var1 + ":" + var18;
                  float var25 = this.pick(var24, var3.getAsInt() == var18);
                  float var26 = this.hover(var24, var23);
                  plate(var5x, var20, var21, var16, var22, var26, var25, var11, var12);
                  Font var27 = Fonts.NEW_MEDIUM.getFont(5.2F);
                  Font var28 = Fonts.NEW_REGULAR.getFont(4.2F);
                  float var29 = var16 - 10.0F;
                  float var30 = var26 * 1.5F;
                  var5x.drawText(
                     var27,
                     fitText(var27, t(var19.title()), var29),
                     var20 + 5.0F + var30,
                     var21 + 4.0F,
                     var12.getTextSecondary()
                        .getZenithColor()
                        .method7295(var12.getTextEnable().getZenithColor(), Math.max(var25, var26 * 0.6F))
                        .method7294(var11)
                  );
                  var5x.drawText(
                     var28,
                     fitText(var28, t(var19.note()), var29),
                     var20 + 5.0F + var30,
                     var21 + 4.0F + var27.height() + 2.0F,
                     var12.getTextTertiary().getZenithColor().method7294(var11)
                  );
               }

               this.hoveredTile.put(var1, var17);
            }
         },
         () -> {
            int var4x = this.hoveredTile.getOrDefault(var1, -1);
            if (var4x >= 0 && var4x != var3.getAsInt()) {
               var4.accept(var4x);
               this.invalidate();
            }
         },
         null
      );
   }

   private void buildAside(Voice var1) {
      this.asideCaption("gui.voice.caption.now");
      this.asideInfo("gui.voice.aside.status", var1::method359, "gui.voice.aside.status.desc");
      this.asideInfo("gui.voice.aside.ping", () -> {
         int var1x = var1.method361();
         return var1x < 0 ? "—" : var1x + " " + t("gui.voice.ms");
      }, "gui.voice.aside.ping.desc");
      this.asideInfo("gui.voice.aside.latency", () -> {
         int var1x = var1.latencyMs();
         return var1x < 0 ? "—" : "≈ " + var1x + " " + t("gui.voice.ms");
      }, "gui.voice.aside.latency.desc");
      this.asideInfo("gui.voice.aside.range", () -> var1.method340() + " " + t("gui.voice.blocks"), "gui.voice.aside.range.desc");
      this.asideCaption("gui.voice.caption.devices");
      this.asideInfo("gui.voice.input", var1::inputName, "gui.voice.input.desc");
      this.asideInfo("gui.voice.output", var1::method336, "gui.voice.output.desc");
      this.asideCaption("gui.voice.caption.keys");
      this.asideInfo(
         "gui.voice.aside.key.talk",
         () -> var1.activation.isSelected(0) ? Voice.method364(var1.talkKey) : t("gui.voice.aside.key.byVoice"),
         "gui.voice.talkKey.desc"
      );
      this.asideInfo("gui.voice.aside.key.radio", () -> Voice.method364(var1.radioKey), "gui.voice.radioKey.desc");
      this.asideInfo("gui.voice.aside.key.mute", () -> Voice.method364(var1.muteKey), "gui.voice.muteKey.desc");
      this.asideInfo("gui.voice.aside.key.deafen", () -> Voice.method364(var1.deafenKey), "gui.voice.deafenKey.desc");
      this.asideCaption("gui.voice.caption.sound");
      this.asideInfo("gui.voice.quality", () -> t(var1.quality.get()) + " · " + var1.method363() + " " + t("gui.voice.kbps"), "gui.voice.quality.desc");
      this.asideInfo(
         "gui.voice.denoise",
         () -> !var1.denoise.isEnabled() ? onOff(false) : (var1.rnnoise.isEnabled() ? "RNNoise" : t("gui.voice.denoise.gate")),
         "gui.voice.denoise.desc"
      );
      this.asideInfo("gui.voice.muffle", () -> onOff(var1.muffle.isEnabled()), "gui.voice.muffle.desc");
      this.asideInfo("gui.voice.spatial", () -> onOff(var1.spatial.isEnabled()), "gui.voice.spatial.desc");
      List<PartyPlayerInfo> var2 = var1.method365();
      this.asideCaption(t("gui.voice.caption.friends") + " · " + var2.size());

      for (Object var4_o : (Iterable<?>)(Object)(var2)) {
         PartyPlayerInfo var4 = (PartyPlayerInfo)var4_o;
         this.friendRow(var1, var4);
      }

      if (var2.isEmpty()) {
         this.asideHint(t("gui.voice.friends.empty"));
         this.asideCaption("gui.voice.caption.howto");
         this.step(1, t("gui.voice.howto.1"));
         this.step(2, t("gui.voice.howto.2"));
         this.step(3, t("gui.voice.howto.3"));
      } else {
         this.asideHint(t("gui.voice.friends.hint"));
      }
   }

   private static String onOff(boolean var0) {
      return t(var0 ? "gui.value.enabled" : "gui.value.disabled");
   }

   private void friendRow(Voice var1, PartyPlayerInfo var2) {
      int var3 = this.asideOrder++;
      String var4 = Voice.method367(var2);
      this.asideCustom(
         18.0F,
         (var5, var6, var7, var8, var9, var10, var11, var12) -> {
            float var13 = wave(this.asideWave, var3);
            if (!(var13 <= 0.002F)) {
               var11 *= var13;
               var8 += (1.0F - var13) * 6.0F;
               boolean var14 = var1.getVoiceState().method5365(var2.uid());
               boolean var15 = var1.getVoiceState().method5367(var2.uid());
               ZenithColor var16 = var15 ? RADIO : var12.getPrimaryColor().getZenithColor();
               float var17 = this.pick("talk:" + var2.uid(), var14);
               float var18 = var9 + 9.0F;
               float var19 = 3.0F + var17;
               var5.drawRoundedRect(
                  var8 + 8.0F + (4.0F - var19) / 2.0F,
                  var18 - var19 / 2.0F,
                  var19,
                  var19,
                  ProxyDotRadiusData.method8069(var19 / 2.0F),
                  var12.getTextTertiary().getZenithColor().method7295(var16, var17).method7294(var11)
               );
               Font var20 = Fonts.NEW_MEDIUM.getFont(5.0F);
               Font var21 = Fonts.NEW_REGULAR.getFont(4.2F);
               String var22 = var1.method366(var2);
               float var23 = var8 + 8.0F + 8.0F;
               float var24 = var10 - 16.0F - 8.0F - var21.width(var22) - 18.0F;
               var5.drawText(
                  var20,
                  fitText(var20, var4, var24),
                  var23,
                  var18 - var20.height() / 2.0F,
                  var12.getTextSecondary().getZenithColor().method7295(var12.getTextEnable().getZenithColor(), 0.5F + 0.5F * var17).method7294(var11)
               );
               float var25 = var8 + var10 - 8.0F - var21.width(var22);
               if (var17 > 0.01F) {
                  Voice.method371(var5, var25 - 14.0F, var18, var1.getVoiceState().method5366(var2.uid()), var16.method7294(var11 * var17));
               }

               var5.drawText(var21, var22, var25, var18 - var21.height() / 2.0F, var12.getTextTertiary().getZenithColor().method7294(var11));
            }
         }
      );
   }

   private void step(int var1, String var2) {
      int var3 = this.asideOrder++;
      Font var4 = Fonts.NEW_REGULAR.getFont(4.4F);
      List<String> var5 = wrapText(var2, var4, this.asideWidth() - 16.0F - 11.0F, 3);
      float var6 = var4.height() + 2.0F;
      float var7 = Math.max(11.0F, var5.size() * var6) + 4.0F;
      this.asideCustom(
         var7,
         (var6x, var7x, var8, var9, var10, var11, var12, var13) -> {
            float var14 = wave(this.asideWave, var3);
            if (!(var14 <= 0.002F)) {
               var12 *= var14;
               var9 += (1.0F - var14) * 6.0F;
               float var15 = 8.0F;
               var6x.drawRoundedRect(
                  var9 + 8.0F,
                  var10 + 1.0F,
                  var15,
                  var15,
                  ProxyDotRadiusData.method8069(var15 / 2.0F),
                  var13.getPrimaryColor().getZenithColor().method7294(var12 * 0.85F)
               );
               Font var16 = Fonts.NEW_MEDIUM.getFont(4.4F);
               String var17 = String.valueOf(var1);
               var6x.drawText(
                  var16,
                  var17,
                  var9 + 8.0F + (var15 - var16.width(var17)) / 2.0F,
                  var10 + 1.0F + (var15 - var16.height()) / 2.0F,
                  new ZenithColor(255, 255, 255).method7294(var12)
               );
               float var18 = var10 + 1.0F + (var15 - var4.height()) / 2.0F;

               for (Object var20_o : (Iterable<?>)(Object)(var5)) {
                  String var20 = (String)var20_o;
                  var6x.drawText(var4, var20, var9 + 8.0F + var15 + 4.0F, var18, var13.getTextSecondary().getZenithColor().method7294(var12));
                  var18 += var6;
               }
            }
         }
      );
   }

   private static void plate(MenuDrawContext var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      var0.drawRoundedRect(
         var1,
         var2,
         var3,
         var4,
         ProxyDotRadiusData.method8069(4.0F),
         var8.getDisableActiveBg().getZenithColor().method7294(var7 * (0.45F + 0.25F * var5 + 0.3F * var6))
      );
      if (var6 > 0.002F) {
         float var9 = (var3 - 8.0F) * var6;
         var0.drawRoundedRect(
            var1 + (var3 - var9) / 2.0F,
            var2 + var4 - 2.0F,
            var9,
            1.5F,
            ProxyDotRadiusData.method8069(0.75F),
            var8.getPrimaryColor().getZenithColor().method7294(var7 * var6)
         );
      }
   }

   private static void bigGlyph(MenuDrawContext var0, float var1, float var2, float var3, ZenithColor var4) {
      float var5 = 12.0F;
      var0.pushMatrix();
      var0.getMatrices().translate(var1 + var5 / 2.0F, var2 + var5 / 2.0F);
      var0.getMatrices().scale(2.0F * var3, 2.0F * var3);
      MenuGlyph.MIC.draw(var0, -3.0F, -3.0F, var4);
      var0.popMatrix();
   }

   private static void chevron(MenuDrawContext var0, float var1, float var2, float var3, ZenithColor var4) {
      float var5 = 3.2F;
      var0.pushMatrix();
      var0.getMatrices().translate(var1 + 2.5F, var2);
      var0.getMatrices().rotate((float)(Math.PI * var3));

      for (byte var6 = -1; var6 <= 1; var6 += 2) {
         var0.pushMatrix();
         var0.getMatrices().rotate((float)Math.toRadians(45 * var6));
         var0.drawRect(-0.5F, -var5 + 0.6F, 1.0F, var5, var4);
         var0.popMatrix();
      }

      var0.popMatrix();
   }

   private float hover(String var1, boolean var2) {
      return this.hover.computeIfAbsent(var1, var0 -> new Animation(120L, 0.0F, Easing.INSTANCE12)).method2808(var2 ? 1.0F : 0.0F);
   }

   private float pick(String var1, boolean var2) {
      return this.pick.computeIfAbsent(var1, var0 -> new Animation(180L, 0.0F, Easing.INSTANCE12)).method2808(var2 ? 1.0F : 0.0F);
   }

   private void hint(String var1) {
      Font var2 = Fonts.NEW_REGULAR.getFont(4.8F);
      List<String> var3 = wrapText(var1, var2, this.mainWidth() - 16.0F, 4);
      float var4 = var2.height() + 2.0F;
      int var5 = this.order++;
      this.custom(var3.size() * var4 + GuiStyle.PADDING.intValue(), (var5x, var6, var7, var8, var9, var10, var11, var12) -> {
         float var13 = wave(this.enterWave, var5);
         float var14 = var9 + 2.0F;

         for (Object var16_o : (Iterable<?>)(Object)(var3)) {
            String var16 = (String)var16_o;
            var5x.drawText(var2, var16, var8 + 8.0F + (1.0F - var13) * 8.0F, var14, var12.getTextTertiary().getZenithColor().method7294(var11 * var13));
            var14 += var4;
         }
      });
   }

   private void asideHint(String var1) {
      Font var2 = Fonts.NEW_REGULAR.getFont(4.4F);
      List<String> var3 = wrapText(var1, var2, this.asideWidth() - 16.0F, 5);
      float var4 = var2.height() + 2.0F;
      int var5 = this.asideOrder++;
      this.asideCustom(var3.size() * var4 + GuiStyle.PADDING.intValue(), (var5x, var6, var7, var8, var9, var10, var11, var12) -> {
         float var13 = wave(this.asideWave, var5);
         float var14 = var9 + 2.0F;

         for (Object var16_o : (Iterable<?>)(Object)(var3)) {
            String var16 = (String)var16_o;
            var5x.drawText(var2, var16, var8 + 8.0F + (1.0F - var13) * 6.0F, var14, var12.getTextTertiary().getZenithColor().method7294(var11 * var13));
            var14 += var4;
         }
      });
   }

   private static String fitText(Font var0, String var1, float var2) {
      if (var2 <= 0.0F) {
         return "";
      } else if (var0.width(var1) <= var2) {
         return var1;
      } else {
         String var3 = "…";
         int var4 = var1.length();

         while (var4 > 0 && var0.width(var1.substring(0, var4) + var3) > var2) {
            var4--;
         }

         return var4 == 0 ? "" : var1.substring(0, var4) + var3;
      }
   }

   @Override
   public void tick() {
      Voice var1 = voice();
      if (var1 != null) {
         long var2 = System.currentTimeMillis();
         if (var2 - this.lastTickAt > 400L) {
            restart(this.enterWave);
            restart(this.asideWave);
         }

         this.lastTickAt = var2;
         if (var2 - this.lastPingAt > 2000L) {
            this.lastPingAt = var2;
            var1.method362();
         }

         if (var1.isFlag3()) {
            var1.method334();
         }

         StringBuilder var4 = new StringBuilder()
            .append(var1.isEnabled())
            .append(var1.activation.getIndex())
            .append(var1.spatial.isEnabled())
            .append(var1.isFlag3())
            .append(var1.input.getValues().size())
            .append(var1.output.getValues().size());

         for (PartyPlayerInfo var6 : var1.method365()) {
            var4.append(';').append(var6.uid());
         }

         Cloud var7 = Zenith.getInstance().getCloud();
         if (var7 != null) {
            PartyMember.Data var8 = var7.getPartyMember().getData();
            var4.append('|')
               .append(var7.method7860())
               .append(var1.denoise.isEnabled())
               .append(var8 == null ? "-" : var8.getText() + var8.getItems().size())
               .append(var7.getPartyMember().getLongValue())
               .append(var7.getPartyMember().method1145().size());
         }

         String var9 = var4.toString();
         if (!var9.equals(this.lastSignature)) {
            this.lastSignature = var9;
            this.invalidate();
         }
      }
   }

   @Override
   public void close() {
      Voice var1 = voice();
      if (var1 != null) {
         var1.method333(false);
      }

      this.openPicker = -1;
      super.close();
   }

   private static final class Tile {
      private final String title;
      private final String note;

      public String title() {
         return this.title;
      }

      public String note() {
         return this.note;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Tile other)) return false;
         return java.util.Objects.equals(this.title, other.title) && java.util.Objects.equals(this.note, other.note);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(title, note);
      }

      @Override
      public String toString() {
         return "Tile[" + "title=" + this.title + ", " + "note=" + this.note + "]";
      }

      private Tile(String var1, String var2) {
         this.title = var1;
         this.note = var2;
      }
   }
}

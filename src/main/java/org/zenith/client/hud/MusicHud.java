package org.zenith.client.hud;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.media.MediaGlyphs;
import org.zenith.client.screens.media.NowPlayingScreen;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.LyricsFetcher;
import org.zenith.utility.config.MediaHelperProcess;
import org.zenith.utility.misc.MediaPlayerState;
import org.zenith.utility.misc.MediaSessionManager;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.Voice;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class MusicHud extends Interface {
   private static MusicHud INSTANCE;
   private static final float CONST_floatValue = 122.0F;
   private static final float CONST_floatValue2 = 250.0F;
   private static final float CONST_floatValue3 = 4.0F;
   private static final float CONST_floatValue4 = 17.0F;
   private static final float CONST_floatValue5 = 25.0F;
   private static final float CONST_floatValue6 = 22.0F;
   private static final float CONST_floatValue7 = 15.0F;
   private static final float CONST_floatValue8 = 8.0F;
   private static final long CONST_longValue = 520L;
   private static final float CONST_floatValue9 = 10.0F;
   private static final float CONST_floatValue10 = 300.0F;
   private static final float CONST_floatValue11 = 200.0F;
   private static final float CONST_floatValue12 = 0.6F;
   private final BooleanSetting colors = new BooleanSetting("module.interface.music.colors", "module.interface.music.colors.desc", true);
   private final BooleanSetting equalizer = new BooleanSetting("module.interface.music.equalizer", "module.interface.music.equalizer.desc", true);
   private final BooleanSetting vinyl = new BooleanSetting("module.interface.music.vinyl", "module.interface.music.vinyl.desc", true);
   private final BooleanSetting lyrics = new BooleanSetting("module.interface.music.lyrics", "module.interface.music.lyrics.desc", true);
   private final BooleanSetting duck = new BooleanSetting("module.interface.music.duck", "module.interface.music.duck.desc", true);
   private final SliderSetting duckLevel = new SliderSetting(
      "module.interface.music.duckLevel", 30.0F, 0.0F, 90.0F, 5.0F, "module.interface.music.duckLevel.desc", "%", this.duck::isEnabled, null
   );
   private final SliderSetting lyricsOffset = new SliderSetting(
      "module.interface.music.lyricsOffset", 0.0F, -3.0F, 3.0F, 0.1F, "module.interface.music.lyricsOffset.desc", "s", this.lyrics::isEnabled, null
   );
   private final BindSetting openKey = new BindSetting("module.interface.music.openKey", "module.interface.music.openKey.desc");
   private final Animation animation = new Animation(420L, 0.0F, Easing.INSTANCE4);
   private final Animation animation2 = new Animation(240L, Easing.INSTANCE15);
   private final Animation animation3 = new Animation(160L, Easing.INSTANCE15);
   private final float[] floatValues = new float[3];
   private long lastFrame;
   private long lastPoll;
   private float animWidth = 122.0F;
   private float discRotation;
   private float floatValue3;
   private final MusicHud.Entry entry = new MusicHud.Entry();
   private final MusicHud.Entry entry2 = new MusicHud.Entry();
   private int intValue = -1;
   private String text = "";
   private boolean flag;
   private float seekPreview;
   private boolean flag2;
   private BoundsData boundsData;
   private BoundsData boundsData2;
   private boolean flag3;
   private BoundsData boundsData3;
   private BoundsData boundsData4;
   private BoundsData boundsData5;
   private BoundsData playBounds;
   private BoundsData boundsData6;
   private BoundsData boundsData7;

   public MusicHud(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      INSTANCE = this;
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)var1x -> this.method6120());
      ClientLifecycleEvents.CLIENT_STOPPING.register((ClientStopping)var0 -> {
         if (MediaPlayerState.getInstance().isFlag()) {
            MediaPlayerState.getInstance().method1677(false, 0.0F, false);
         }
      });
   }

   private void method6120() {
      if (MediaPlayerState.getInstance().isFlag() && (mc.player == null || !this.isEnabled() || !InterfaceModule.INSTANCE.isEnabled())) {
         MediaPlayerState.getInstance().method1677(false, 0.0F, false);
      }
   }

   public static float method6121() {
      MusicHud var0 = INSTANCE;
      return 0.6F + (var0 == null ? 0.0F : var0.lyricsOffset.getValue3());
   }

   public static boolean method6122(double var0) {
      MusicHud var2 = INSTANCE;
      if (var2 != null && var2.isEnabled() && MediaSessionManager.getInstance().active() && !(var2.animation.getValue() <= 0.01F)) {
         Vector2f var3 = var2.method2099();
         if (!var2.inside(var3.x(), var3.y(), var2.floatValue, var2.floatValue2, var2.width, var2.height)) {
            return false;
         } else {
            MediaPlayerState.getInstance().method1676((float)Math.signum(var0) * 0.05F);
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public void tick() {
      if (mc.player != null) {
         long var1 = System.currentTimeMillis();
         if (var1 - this.lastPoll > 500L) {
            this.lastPoll = var1;
            MediaSessionManager.getInstance().method6000();
         }

         Voice var3 = Zenith.getInstance().getVoice();
         boolean var4 = var3 != null && var3.method358();
         MediaPlayerState.getInstance().method1677(var4, this.duckLevel.getValue3() / 100.0F, this.duck.isEnabled());
         boolean var5 = this.openKey.getKeyCode() > 0 && mc.currentScreen == null && UseItemUtils.method6521(this.openKey);
         if (var5 && !this.flag2) {
            mc.setScreen(new NowPlayingScreen());
         }

         this.flag2 = var5;
      }
   }

   @Override
   public boolean method2056(MouseButtonEvent var1) {
      if (var1.getIntValue2() == 1 && var1.getIntValue() == 0 && MediaSessionManager.getInstance().active()) {
         Vector2f var2 = this.method2099();
         float var3 = var2.x();
         float var4 = var2.y();
         MediaSessionManager var5 = MediaSessionManager.getInstance();
         if (this.method6138(this.boundsData, var3, var4)) {
            mc.setScreen(new NowPlayingScreen());
            return true;
         } else if (this.boundsData3 != null
            && this.inside(
               var3,
               var4,
               this.boundsData3.getFloatValue(),
               this.boundsData3.getFloatValue2() - 3.0F,
               this.boundsData3.width(),
               this.boundsData3.height() + 6.0F
            )
            && var5.method6008() > 0.0) {
            this.flag = true;
            this.seekPreview = this.method6137(var3);
            return true;
         } else if (this.method6138(this.playBounds, var3, var4) || this.method6138(this.boundsData2, var3, var4)) {
            var5.playPause();
            return true;
         } else if (this.method6138(this.boundsData5, var3, var4)) {
            var5.previous();
            return true;
         } else if (this.method6138(this.boundsData6, var3, var4)) {
            var5.next();
            return true;
         } else if (this.method6138(this.boundsData4, var3, var4) && var5.getIMediaSession() != null) {
            MediaHelperProcess.getInstance().method6864(var5.getIMediaSession().getOwner());
            return true;
         } else if (this.method6138(this.boundsData7, var3, var4) && var5.getIMediaSession() != null) {
            MediaHelperProcess.getInstance().method6865(var5.getIMediaSession().getOwner());
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public float method254() {
      return this.animation.getValue();
   }

   @Override
   public void method8(CustomDrawContext var1) {
      if (!(mc.currentScreen instanceof GameMenuScreen)) {
         MediaSessionManager var2 = MediaSessionManager.getInstance();
         long var3 = System.currentTimeMillis();
         if (var3 - this.lastPoll > 500L) {
            this.lastPoll = var3;
            var2.method6000();
         }

         float var5 = this.lastFrame == 0L ? 0.016F : Math.min(0.1F, (float)(var3 - this.lastFrame) / 1000.0F);
         this.lastFrame = var3;
         boolean var6 = mc.currentScreen instanceof ChatScreen;
         boolean var7 = var2.active();
         boolean var8 = var7 && (var2.method6007() || var3 - var2.getLongValue9() < 1500L);
         boolean var9 = var8 && var3 - var2.getSeenAt() < 1200L || this.method2074();
         if (var9 && !this.flag3) {
            this.entry.previous = "";
            this.entry.changedAt = var3 + 120L;
            this.entry2.previous = "";
            this.entry2.changedAt = var3 + 200L;
         }

         this.flag3 = var9;
         this.animation.method2807(var9);
         if (this.animation.getValue() <= 0.001F) {
            this.flag = false;
            this.clearBounds();
         } else {
            Vector2f var10 = this.method2099();
            float var11 = var10.x();
            float var12 = var10.y();
            boolean var13 = var6 || this.method2074();
            boolean var14 = var13 && var7 && (this.inside(var11, var12, this.floatValue, this.floatValue2, this.animWidth, this.height) || this.flag);
            this.animation2.method2807(var14);
            float var15 = this.animation2.getValue();
            double var16 = var2.method6009();
            double var18 = var2.method6008();
            String var20 = var7 ? var2.title() : Zenith.getInstance().getLocalization().translate("gui.music.nothing");
            String var21 = this.method6123(var2, var7, var16, var18);
            this.entry.method6139(var20, false, var3);
            this.entry2.method6139(var21 != null ? var21 : (var7 ? var2.method6006() : ""), var21 != null, var3);
            Font var22 = Fonts.NEW_SEMIBOLD.getFont(6.0F);
            Font var23 = Fonts.NEW_MEDIUM.getFont(5.5F);
            float var24 = 63.0F;
            float var25 = Math.max(var22.width(this.entry.text), var23.width(this.entry2.text)) + var24 + 2.0F;
            float var26 = Math.max(122.0F, Math.min(250.0F, var25));
            this.animWidth = this.animWidth + (var26 - this.animWidth) * Math.min(1.0F, var5 * 7.0F);
            if (Math.abs(var26 - this.animWidth) < 0.2F) {
               this.animWidth = var26;
            }

            this.width = this.animWidth;
            this.height = 25.0F + 22.0F * var15;
            ZenithStyle var27 = Zenith.getInstance().getStyleManager().getCurrentStyle();
            ZenithColor var28 = var27.getPrimaryColor().getZenithColor();
            boolean var29 = this.colors.isEnabled() && var7;
            ZenithColor var30 = var29 ? var2.color(0) : var28;
            ZenithColor var31 = var27.getTextEnable().getZenithColor();
            ZenithColor var32 = var27.getTextSecondary().getZenithColor().method7295(var31, 0.35F);
            ZenithColor var33 = var29 ? var31.method7295(var30.method7295(ZenithColor.INSTANCE, 0.45F).method7293(var31.getIntValue4()), 0.4F) : var31;
            float var34 = InterfaceModule.method6801();
            float var35 = Math.max(0.0F, this.animation.getValue());
            var1.pushMatrix();
            float var36 = this.floatValue + this.width / 2.0F;
            float var37 = this.floatValue2 + this.height / 2.0F;
            float var38 = 0.55F + 0.45F * var35;
            float var39 = 0.35F + 0.65F * var35;
            var1.getMatrices().translate(var36, var37 + (1.0F - Math.min(1.0F, var35)) * 6.0F);
            var1.getMatrices().scale(var38, var39);
            var1.getMatrices().translate(-var36, -var37);
            float var40 = 25.0F;
            var1.drawBlurHud(this.floatValue, this.floatValue2, this.width, this.height, 21.0F, ProxyDotRadiusData.method8069(var34), ZenithColor.INSTANCE);
            var1.drawRoundedRect(
               this.floatValue,
               this.floatValue2,
               this.width,
               this.height,
               ProxyDotRadiusData.method8069(var34),
               var27.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
            );
            ZenithColor var41 = var27.getHeaderHudBackground().getZenithColor();
            if (var29) {
               var41 = var41.method7295(var30.method7293(var41.getIntValue4()), 0.16F);
            }

            var1.drawRoundedRect(this.floatValue, this.floatValue2, var40, 25.0F, ProxyDotRadiusData.method8069(var34), var41);
            this.boundsData = new BoundsData(this.floatValue, this.floatValue2, var40, 25.0F);
            this.method6128(var1, var2, this.floatValue + 4.0F, this.floatValue2 + 4.0F, var3, var5, var7);
            float var42 = this.floatValue + this.width - 4.0F - 15.0F;
            float var43 = this.floatValue2 + 5.0F;
            float var44 = this.floatValue + var40 + 4.0F;
            boolean var45 = this.equalizer.isEnabled() && var7;
            float var46 = var42 - 4.0F - 8.0F;
            float var47 = !var45 && !this.method6131(var3) ? var42 - 3.0F : var46 - 3.0F;
            float var48 = Math.max(10.0F, var47 - var44);
            boolean var49 = var14 && this.inside(var11, var12, var44, this.floatValue2, var48, 25.0F);
            float var50 = this.floatValue2 + 5.5F;
            float var51 = var50 + var22.height() + 1.5F;
            this.method6124(var1, var22, this.entry, var44, var50, var48, this.floatValue2 + 1.0F, var51 - 0.5F, var31, var31, var3, var49);
            this.method6124(var1, var23, this.entry2, var44, var51, var48, var51 - 0.5F, this.floatValue2 + 25.0F - 1.0F, var32, var33, var3, var49);
            if (this.method6131(var3)) {
               this.method6132(var1, var46 - 3.0F, var43 + 2.0F, var3, var31, var30);
            } else if (var45) {
               this.method6130(var1, var46, this.floatValue2 + 8.0F, var2, var5, var30);
            }

            this.boundsData2 = new BoundsData(var42 - 1.0F, var43 - 1.0F, 17.0F, 17.0F);
            this.method6133(
               var1, var42, var43, var16, var18, var30, var31, var27, var7 && var2.method6007(), this.method6138(this.boundsData2, var11, var12) && var6
            );
            this.method6134(var1, var2, var15, var30, var31, var32, var11, var12, var16, var18);
            var1.popMatrix();
         }
      }
   }

   private String method6123(MediaSessionManager var1, boolean var2, double var3, double var5) {
      if (this.lyrics.isEnabled() && var2) {
         LyricsFetcher.Data2 var7 = LyricsFetcher.method528(var1.title(), var1.method6006(), var5);
         if (!var7.isFlag()) {
            return null;
         } else {
            if (!var1.getText().equals(this.text)) {
               this.text = var1.getText();
               this.intValue = -1;
            }

            int var8 = var7.method541(var3 + method6121());
            this.intValue = var8;
            if (var8 < 0) {
               return null;
            } else {
               String var9 = var7.lines().get(var8).text().trim();
               return var9.isEmpty() ? null : var9;
            }
         }
      } else {
         return null;
      }
   }

   private void method6124(
      CustomDrawContext var1,
      Font var2,
      MusicHud.Entry var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      ZenithColor var9,
      ZenithColor var10,
      long var11,
      boolean var13
   ) {
      float var14 = (float)(var11 - var3.changedAt);
      var1.enableScissor((int)Math.floor(var4 - 1.0F), (int)Math.floor(var7), (int)Math.ceil(var4 + var6 + 1.0F), (int)Math.ceil(var8));
      if (!var3.previous.isEmpty()) {
         String var15 = MediaGlyphs.ellipsize(var2, var3.previous, var6);
         if (var14 < 200.0F + var15.length() * 10.0F * 0.5F) {
            this.method6125(var1, var2, var15, var4, var5, var3.flag2 ? var10 : var9, var14, false);
         }
      }

      if (!var3.text.isEmpty()) {
         float var19 = !var3.previous.isEmpty() && !var3.flag ? 60.0F : 0.0F;
         float var16 = var14 - var19;
         float var17 = 300.0F + var3.text.length() * 10.0F;
         ZenithColor var18 = var3.flag ? var10 : var9;
         if (var16 >= var17) {
            this.method6127(var1, var2, var3.text, var4, var5, var6, var18, var13);
         } else if (var16 > 0.0F) {
            this.method6125(var1, var2, MediaGlyphs.ellipsize(var2, var3.text, var6), var4, var5, var18, var16, true);
         }
      }

      var1.disableScissor();
   }

   private void method6125(CustomDrawContext var1, Font var2, String var3, float var4, float var5, ZenithColor var6, float var7, boolean var8) {
      float var9 = var4;

      for (int var10 = 0; var10 < var3.length(); var10++) {
         String var11 = String.valueOf(var3.charAt(var10));
         float var12 = var2.width(var11);
         if (!var11.isBlank()) {
            if (var8) {
               float var13 = clamp01((var7 - var10 * 10.0F) / 300.0F);
               if (var13 > 0.0F) {
                  float var14 = method6126(var13);
                  float var15 = 1.0F - (1.0F - var13) * (1.0F - var13);
                  var1.drawText(var2, var11, var9 - (1.0F - var13) * 1.5F, var5 + (1.0F - var14) * 6.0F, var6.method7294(var15));
               }
            } else {
               float var16 = clamp01((var7 - var10 * 10.0F * 0.5F) / 200.0F);
               if (var16 < 1.0F) {
                  float var17 = var16 * var16;
                  var1.drawText(var2, var11, var9 + var17 * 1.0F, var5 - var17 * 5.0F, var6.method7294(1.0F - var16));
               }
            }
         }

         var9 += var12;
      }
   }

   private static float method6126(float var0) {
      float var1 = 1.9F;
      float var2 = var1 + 1.0F;
      float var3 = var0 - 1.0F;
      return 1.0F + var2 * var3 * var3 * var3 + var1 * var3 * var3;
   }

   private void method6127(CustomDrawContext var1, Font var2, String var3, float var4, float var5, float var6, ZenithColor var7, boolean var8) {
      float var9 = var2.width(var3);
      if (var9 <= var6) {
         var1.drawText(var2, var3, var4, var5, var7);
      } else if (var8) {
         float var10 = 18.0F;
         float var11 = var9 + var10;
         long var12 = (long)(var11 / 22.0F * 1000.0F);
         long var14 = 900L;
         long var16 = System.currentTimeMillis() % (var12 + var14);
         float var18 = var16 < var14 ? 0.0F : (float)(var16 - var14) / (float)var12 * var11;
         var1.drawText(var2, var3, var4 - var18, var5, var7);
         var1.drawText(var2, var3, var4 - var18 + var11, var5, var7);
      } else {
         var1.drawText(var2, MediaGlyphs.ellipsize(var2, var3, var6), var4, var5, var7);
      }
   }

   private void method6128(CustomDrawContext var1, MediaSessionManager var2, float var3, float var4, long var5, float var7, boolean var8) {
      boolean var9 = this.vinyl.isEnabled();
      float var10 = var9 && var2.method6007() ? 55.0F : 0.0F;
      this.floatValue3 = this.floatValue3 + (var10 - this.floatValue3) * Math.min(1.0F, var7 * (var10 > this.floatValue3 ? 2.2F : 3.5F));
      this.discRotation = (this.discRotation + this.floatValue3 * var7) % 360.0F;
      Identifier var11 = var8 ? var2.method6013() : null;
      Identifier var12 = var2.method6014();
      float var13 = Math.min(1.0F, (float)(var5 - var2.getLongValue4()) / 520.0F);
      float var14 = 1.0F - (1.0F - var13) * (1.0F - var13) * (1.0F - var13);
      float var15 = var3 + 8.5F;
      float var16 = var4 + 8.5F;
      if (var11 == null) {
         var1.drawRoundedRect(var3, var4, 17.0F, 17.0F, ProxyDotRadiusData.method8069(var9 ? 8.5F : 3.0F), ZenithColor.INSTANCE.method7292(0.08F));
         MediaGlyphs.playPause(var1, var15, var16, 7.0F, false, ZenithColor.INSTANCE.method7292(0.45F));
      } else {
         if (var14 < 1.0F && var12 != null) {
            this.method6129(var1, var12, var15, var16, 17.0F * (1.0F - 0.35F * var14), 1.0F - var14, var9);
         }

         this.method6129(var1, var11, var15, var16, 17.0F * (0.65F + 0.35F * method6126(var14)), var14, var9);
         if (var9) {
            float var17 = 3.6F;
            var1.drawArcBorder(var15 - 6.1200004F, var16 - 6.1200004F, 12.240001F, 12.240001F, 0.4F, 360.0F, 0.5F, ZenithColor.INSTANCE.method7292(0.08F));
            var1.drawRoundedRect(
               var15 - var17 / 2.0F,
               var16 - var17 / 2.0F,
               var17,
               var17,
               ProxyDotRadiusData.method8069(var17 / 2.0F),
               new ZenithColor(14, 14, 18).method7292(0.92F)
            );
            var1.drawArcBorder(
               var15 - var17 / 2.0F - 0.5F, var16 - var17 / 2.0F - 0.5F, var17 + 1.0F, var17 + 1.0F, 0.5F, 360.0F, 0.5F, ZenithColor.INSTANCE.method7292(0.3F)
            );
         }
      }
   }

   private void method6129(CustomDrawContext var1, Identifier var2, float var3, float var4, float var5, float var6, boolean var7) {
      if (!(var6 <= 0.01F)) {
         var1.pushMatrix();
         var1.getMatrices().translate(var3, var4);
         if (var7) {
            var1.getMatrices().rotate((float)Math.toRadians(this.discRotation));
         }

         var1.drawRoundedTexture(
            var2, -var5 / 2.0F, -var5 / 2.0F, var5, var5, ProxyDotRadiusData.method8069(var7 ? var5 / 4.0F : 1.5F), ZenithColor.INSTANCE.method7292(var6)
         );
         var1.popMatrix();
      }
   }

   private void method6130(CustomDrawContext var1, float var2, float var3, MediaSessionManager var4, float var5, ZenithColor var6) {
      float var7 = MediaPlayerState.getInstance().method1672();
      boolean var8 = var4.method6007();
      float var9;
      if (!var8) {
         var9 = 0.0F;
      } else if (var7 >= 0.0F) {
         double var10 = 20.0 * Math.log10(Math.max(var7, 1.0E-4F));
         var9 = (float)Math.max(0.0, Math.min(1.0, (var10 + 38.0) / 36.0));
      } else {
         var9 = 0.62F;
      }

      double var24 = System.currentTimeMillis() / 1000.0;
      float var12 = 9.0F;
      float var13 = 1.6F;
      float var14 = (8.0F - var13 * this.floatValues.length) / (this.floatValues.length - 1);
      float var15 = var3 + var12;
      ZenithColor var16 = var6.method7295(ZenithColor.INSTANCE, 0.2F);

      for (int var17 = 0; var17 < this.floatValues.length; var17++) {
         double var18 = 0.5 + 0.3 * Math.sin(var24 * (7.1 + var17 * 2.3) + var17 * 1.9) + 0.2 * Math.sin(var24 * (13.7 - var17 * 1.7) + var17);
         float var20 = (float)(var9 * (0.35 + 0.65 * var18));
         float var21 = var20 > this.floatValues[var17] ? 22.0F : 7.0F;
         this.floatValues[var17] = this.floatValues[var17] + (var20 - this.floatValues[var17]) * Math.min(1.0F, var5 * var21);
         float var22 = 1.6F + (var12 - 1.6F) * clamp01(this.floatValues[var17]);
         float var23 = var2 + var17 * (var13 + var14);
         var1.drawRoundedRect(var23, var15 - var22, var13, var22, ProxyDotRadiusData.method8069(0.8F), var16.method7292(var8 ? 0.95F : 0.4F));
      }
   }

   private boolean method6131(long var1) {
      return MediaPlayerState.getInstance().method1673() && var1 - MediaPlayerState.getInstance().getChangedAt() < 1400L;
   }

   private void method6132(CustomDrawContext var1, float var2, float var3, long var4, ZenithColor var6, ZenithColor var7) {
      float var8 = Math.min(1.0F, (float)(1400L - (var4 - MediaPlayerState.getInstance().getChangedAt())) / 250.0F);
      float var9 = MediaPlayerState.getInstance().method1679();
      float var10 = 11.0F;
      Font var11 = Fonts.NEW_SEMIBOLD.getFont(4.5F);
      String var12 = String.valueOf(Math.round(var9 * 100.0F));
      var1.drawText(var11, var12, var2 + (var10 - var11.width(var12)) / 2.0F, var3, var6.method7294(var8));
      float var13 = var3 + 8.0F;
      var1.drawRoundedRect(var2, var13, var10, 1.6F, ProxyDotRadiusData.method8069(0.8F), ZenithColor.INSTANCE.method7292(0.18F * var8));
      var1.drawRoundedRect(
         var2, var13, Math.max(1.6F, var10 * var9), 1.6F, ProxyDotRadiusData.method8069(0.8F), var7.method7295(ZenithColor.INSTANCE, 0.2F).method7292(var8)
      );
   }

   private void method6133(
      CustomDrawContext var1,
      float var2,
      float var3,
      double var4,
      double var6,
      ZenithColor var8,
      ZenithColor var9,
      ZenithStyle var10,
      boolean var11,
      boolean var12
   ) {
      float var13 = var6 > 0.0 ? (float)Math.max(0.0, Math.min(1.0, var4 / var6)) : 0.0F;
      if (this.flag) {
         var13 = this.seekPreview;
      }

      if (var12) {
         var1.drawRoundedRect(var2, var3, 15.0F, 15.0F, ProxyDotRadiusData.method8069(7.5F), ZenithColor.INSTANCE.method7292(0.08F));
      }

      var1.drawArcBorder(var2, var3, 15.0F, 15.0F, 1.1F, 360.0F, 0.5F, var10.getFieldBorder().getZenithColor());
      if (var13 > 0.001F) {
         var1.drawArcBorder(var2, var3, 15.0F, 15.0F, 1.1F, 360.0F * var13, 0.5F, var8.method7295(ZenithColor.INSTANCE, 0.15F));
      }

      MediaGlyphs.playPause(var1, var2 + 7.5F, var3 + 7.5F, 5.6F, var11, var12 ? var9 : var9.method7294(0.85F));
   }

   private void method6134(
      CustomDrawContext var1,
      MediaSessionManager var2,
      float var3,
      ZenithColor var4,
      ZenithColor var5,
      ZenithColor var6,
      float var7,
      float var8,
      double var9,
      double var11
   ) {
      if (this.flag) {
         if (GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), 0) == 1) {
            this.seekPreview = this.method6137(var7);
         } else {
            this.flag = false;
            MediaSessionManager.getInstance().method6019(this.seekPreview * var11);
         }
      }

      if (var3 <= 0.02F) {
         this.boundsData3 = this.boundsData4 = this.boundsData5 = this.playBounds = this.boundsData6 = this.boundsData7 = null;
      } else {
         float var14 = (1.0F - var3) * 4.0F;
         float var15 = this.floatValue + 4.0F + 2.0F;
         float var16 = this.width - 8.0F - 4.0F;
         float var17 = this.floatValue2 + 25.0F + 3.0F - var14;
         boolean var18 = this.inside(var7, var8, var15, var17 - 3.0F, var16, 8.0F);
         this.animation3.method2807(var18 || this.flag);
         float var19 = 1.6F + 1.2F * this.animation3.getValue();
         this.boundsData3 = new BoundsData(var15, var17, var16, var19);
         float var20 = this.flag ? this.seekPreview : (var11 > 0.0 ? clamp01((float)(var9 / var11)) : 0.0F);
         float var21 = var17 + (2.8F - var19) / 2.0F;
         var1.drawRoundedRect(var15, var21, var16, var19, ProxyDotRadiusData.method8069(var19 / 2.0F), ZenithColor.INSTANCE.method7292(0.14F * var3));
         var1.drawRoundedRect(
            var15,
            var21,
            Math.max(var19, var16 * var20),
            var19,
            ProxyDotRadiusData.method8069(var19 / 2.0F),
            var4.method7295(ZenithColor.INSTANCE, 0.2F).method7294(var3)
         );
         if (this.animation3.getValue() > 0.02F) {
            float var22 = 4.0F * this.animation3.getValue();
            var1.drawRoundedRect(
               var15 + var16 * var20 - var22 / 2.0F,
               var21 + var19 / 2.0F - var22 / 2.0F,
               var22,
               var22,
               ProxyDotRadiusData.method8069(var22 / 2.0F),
               var5.method7294(var3)
            );
         }

         float var32 = var17 + 11.0F;
         Font var23 = Fonts.NEW_MEDIUM.getFont(4.6F);
         double var24 = this.flag ? this.seekPreview * var11 : var9;
         String var26 = "-" + MediaGlyphs.time(Math.max(0.0, var11 - var24));
         var1.drawText(var23, MediaGlyphs.time(var24), var15, var32 - var23.height() / 2.0F, var6.method7294(var3));
         var1.drawText(var23, var26, var15 + var16 - var23.width(var26), var32 - var23.height() / 2.0F, var6.method7294(var3));
         float var27 = 15.0F;
         float var28 = this.floatValue + this.width / 2.0F;
         this.boundsData4 = this.method6136(var28 - var27 * 2.0F, var32, 11.0F);
         this.boundsData5 = this.method6136(var28 - var27, var32, 11.0F);
         this.playBounds = this.method6136(var28, var32, 13.0F);
         this.boundsData6 = this.method6136(var28 + var27, var32, 11.0F);
         this.boundsData7 = this.method6136(var28 + var27 * 2.0F, var32, 11.0F);
         MediaHelperProcess var29 = MediaHelperProcess.getInstance();
         ZenithColor var30 = var4.method7295(ZenithColor.INSTANCE, 0.3F).method7294(var3);
         ZenithColor var31 = var5.method7294(var3 * 0.5F);
         MediaGlyphs.shuffle(var1, var28 - var27 * 2.0F, var32, 6.0F, this.method6135(var29.isFlag() ? var30 : var31, this.boundsData4, var7, var8));
         MediaGlyphs.skip(var1, var28 - var27, var32, 7.0F, false, this.method6135(var5.method7294(var3), this.boundsData5, var7, var8));
         MediaGlyphs.playPause(var1, var28, var32, 8.0F, var2.method6007(), this.method6135(var5.method7294(var3), this.playBounds, var7, var8));
         MediaGlyphs.skip(var1, var28 + var27, var32, 7.0F, true, this.method6135(var5.method7294(var3), this.boundsData6, var7, var8));
         MediaGlyphs.repeat(
            var1,
            var28 + var27 * 2.0F,
            var32,
            6.0F,
            var29.getIntValue() == 1,
            this.method6135(var29.getIntValue() != 0 ? var30 : var31, this.boundsData7, var7, var8)
         );
      }
   }

   private ZenithColor method6135(ZenithColor var1, BoundsData var2, float var3, float var4) {
      return this.method6138(var2, var3, var4) ? var1.method7295(ZenithColor.INSTANCE.method7293(var1.getIntValue4()), 0.5F) : var1;
   }

   private BoundsData method6136(float var1, float var2, float var3) {
      return new BoundsData(var1 - var3 / 2.0F, var2 - var3 / 2.0F, var3, var3);
   }

   private float method6137(float var1) {
      return this.boundsData3 != null && !(this.boundsData3.width() <= 0.0F)
         ? clamp01((var1 - this.boundsData3.getFloatValue()) / this.boundsData3.width())
         : 0.0F;
   }

   private static float clamp01(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }

   private void clearBounds() {
      this.boundsData2 = null;
      this.boundsData = this.boundsData3 = this.boundsData4 = this.boundsData5 = this.playBounds = this.boundsData6 = this.boundsData7 = null;
   }

   private boolean method6138(BoundsData var1, float var2, float var3) {
      return var1 != null && var1.contains(var2, var3);
   }

   private boolean inside(double var1, double var3, float var5, float var6, float var7, float var8) {
      return var1 >= var5 && var1 <= var5 + var7 && var3 >= var6 && var3 <= var6 + var8;
   }

   private static final class Entry {
      public String text = "";
      public String previous = "";
      public long changedAt;
      public boolean flag;
      public boolean flag2;

      public void method6139(String var1, boolean var2, long var3) {
         String var5 = var1 == null ? "" : var1;
         if (!var5.equals(this.text)) {
            this.previous = this.text;
            this.flag2 = this.flag;
            this.text = var5;
            this.changedAt = var3;
         }

         this.flag = var2;
      }
   }
}

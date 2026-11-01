package org.zenith.client.screens.media;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.hud.MusicHud;
import org.zenith.utility.api.LyricsFetcher;
import org.zenith.utility.config.MediaHelperProcess;
import org.zenith.utility.misc.MediaPlayerState;
import org.zenith.utility.misc.MediaSessionManager;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class NowPlayingScreen extends Screen {
   private static final ZenithColor WHITE = ZenithColor.INSTANCE;
   private static final ZenithColor BLACK = new ZenithColor(0, 0, 0);
   private final long openedAt = System.currentTimeMillis();
   private long closingAt = -1L;
   private long lastFrame;
   private int ticks;
   private double phase;
   private float energy;
   private float coverScale = 1.0F;
   private float lyricScroll = Float.NaN;
   private float lyricVelocity;
   private float manualOffset;
   private long manualUntil;
   private String layoutKey = "";
   private float layoutWidth;
   private float layoutSize;
   private final List<NowPlayingScreen.Block> blocks = new ArrayList<>();
   private float blocksHeight;
   private float[] emphasis = new float[0];
   private boolean seekDrag;
   private float seekPreview;
   private boolean volumeDrag;
   private float barHover;
   private float volumeHover;
   private BoundsData seekBar;
   private BoundsData volumeBar;
   private BoundsData closeButton;
   private BoundsData shuffleButton;
   private BoundsData previousButton;
   private BoundsData playButton;
   private BoundsData nextButton;
   private BoundsData repeatButton;
   private BoundsData leftPanel;
   private final List<NowPlayingScreen.Hit> lyricHits = new ArrayList<>();

   public NowPlayingScreen() {
      super(Text.literal("Now Playing"));
   }

   public static boolean coversWorld() {
      MinecraftClient var0 = MinecraftClient.getInstance();
      return !(var0.currentScreen instanceof NowPlayingScreen var1) ? false : var1.closingAt < 0L && System.currentTimeMillis() - var1.openedAt > 400L;
   }

   @Override
   public void tick() {
      if (this.ticks++ % 5 == 0) {
         MediaSessionManager.getInstance().method6000();
      }
   }

   @Override
   public boolean shouldPause() {
      return false;
   }

   @Override
   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
   }

   @Override
   public void close() {
      if (this.closingAt < 0L) {
         this.closingAt = System.currentTimeMillis();
      }
   }

   private float visibility(long var1) {
      float var3 = Math.min(1.0F, (float)(var1 - this.openedAt) / 360.0F);
      float var4 = 1.0F - (1.0F - var3) * (1.0F - var3) * (1.0F - var3);
      if (this.closingAt >= 0L) {
         float var5 = Math.min(1.0F, (float)(var1 - this.closingAt) / 240.0F);
         var4 *= 1.0F - var5 * var5;
      }

      return var4;
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      long var5 = System.currentTimeMillis();
      float var7 = this.lastFrame == 0L ? 0.016F : Math.min(0.1F, (float)(var5 - this.lastFrame) / 1000.0F);
      this.lastFrame = var5;
      float var8 = this.visibility(var5);
      if (this.closingAt >= 0L && var5 - this.closingAt >= 240L) {
         if (this.client != null) {
            this.client.setScreen(null);
         }
      } else {
         MenuDrawContext var9 = MenuDrawContext.method8080(context, mouseX, mouseY, deltaTicks);
         MediaSessionManager var10 = MediaSessionManager.getInstance();
         float var11 = this.width;
         float var12 = this.height;
         this.drawBackdrop(var9, var10, var11, var12, var5, var7, var8);
         float var13 = 0.97F + 0.03F * var8;
         var9.getMatrices().pushMatrix();
         var9.getMatrices().translate(var11 / 2.0F, var12 / 2.0F);
         var9.getMatrices().scale(var13, var13);
         var9.getMatrices().translate(-var11 / 2.0F, -var12 / 2.0F);
         float var14 = (mouseX - var11 / 2.0F) / var13 + var11 / 2.0F;
         float var15 = (mouseY - var12 / 2.0F) / var13 + var12 / 2.0F;
         this.drawClose(var9, var8, var14, var15);
         if (!var10.active()) {
            Font var16 = Fonts.BOLD.getFont(14.0F);
            String var17 = t("gui.music.nothing");
            var9.drawText(var16, var17, (var11 - var16.width(var17)) / 2.0F, var12 / 2.0F - var16.height() / 2.0F, WHITE.method7292(0.7F * var8));
            this.clearHits();
         } else {
            boolean var22 = var11 > var12 * 1.15F;
            if (var22) {
               float var23 = Math.min(var12 * 0.56F, var11 * 0.29F);
               float var18 = var23 + 124.0F;
               float var19 = Math.max(26.0F, (var12 - var18) / 2.0F - 6.0F);
               this.drawNowPlaying(var9, var10, var11 * 0.28F, var19, var23, var5, var7, var8, var14, var15);
               float var20 = var11 * 0.49F;
               float var21 = var11 * 0.4F;
               this.drawLyrics(var9, var10, var20, 0.0F, var21, var12, var5, var7, var8, var14, var15);
            } else {
               float var24 = Math.min(var12 * 0.3F, var11 * 0.6F);
               float var25 = 30.0F;
               this.drawNowPlaying(var9, var10, var11 / 2.0F, var25, var24, var5, var7, var8, var14, var15);
               float var26 = var25 + var24 + 130.0F;
               this.drawLyrics(var9, var10, var11 * 0.1F, var26, var11 * 0.8F, var12 - var26, var5, var7, var8, var14, var15);
            }
         }

         var9.getMatrices().popMatrix();
      }
   }

   private void drawBackdrop(MenuDrawContext var1, MediaSessionManager var2, float var3, float var4, long var5, float var7, float var8) {
      float var9 = MediaPlayerState.getInstance().method1672();
      float var10 = 0.0F;
      if (var2.method6007() && var9 >= 0.0F) {
         double var11 = 20.0 * Math.log10(Math.max(var9, 1.0E-4F));
         var10 = (float)Math.max(0.0, Math.min(1.0, (var11 + 36.0) / 34.0));
      }

      this.energy = this.energy + (var10 - this.energy) * Math.min(1.0F, var7 * 1.5F);
      this.phase = this.phase + var7 * (var2.method6007() ? 1.0 : 0.35);
      ZenithColor var18 = var2.color(0);
      ZenithColor var12 = var2.color(1);
      ZenithColor var13 = var2.color(2);
      ZenithColor var14 = var18.method7295(var13, 0.5F).method7296(0.4F);
      var1.drawRect(0.0F, 0.0F, var3, var4, new ZenithColor(8, 8, 10).method7292(var8));
      RenderShaders.method5262(
         MatrixUtils.method664(var1.getMatrices()),
         var3,
         var4,
         new ZenithColor[]{var18, var12, var13, var14},
         (float)(this.phase % 100000.0),
         this.energy,
         var8
      );
      float var15 = Math.min(1.0F, (float)(var5 - var2.getLongValue4()) / 1200.0F);
      Identifier var16 = var2.method6017();
      Identifier var17 = var2.method6018();
      if (var16 != null) {
         if (var17 != null && var15 < 1.0F) {
            this.drawSwirl(var1, var17, var3, var4, var8 * 0.5F * (1.0F - var15));
         }

         this.drawSwirl(var1, var16, var3, var4, var8 * 0.5F * (var17 == null ? 1.0F : var15));
      }

      var1.drawRoundedRect(
         0.0F,
         0.0F,
         var3,
         var4,
         ProxyDotRadiusData.INSTANCE,
         GlowColor.method8096(BLACK.method7292(0.25F * var8), BLACK.method7292(0.45F * var8), BLACK.method7292(0.35F * var8), BLACK.method7292(0.5F * var8))
      );
   }

   private void drawSwirl(MenuDrawContext var1, Identifier var2, float var3, float var4, float var5) {
      if (!(var5 <= 0.01F)) {
         double var6 = this.phase;
         float var8 = Math.max(var3, var4);
         this.layer(
            var1,
            var2,
            var3 * 0.3F + (float)Math.sin(var6 * 0.05) * var3 * 0.05F,
            var4 * 0.45F + (float)Math.cos(var6 * 0.04) * var4 * 0.05F,
            var8 * 1.9F,
            (float)(var6 * 1.6),
            var5
         );
         this.layer(
            var1,
            var2,
            var3 * 0.75F + (float)Math.cos(var6 * 0.045) * var3 * 0.06F,
            var4 * 0.6F + (float)Math.sin(var6 * 0.035) * var4 * 0.06F,
            var8 * 1.5F,
            (float)(-var6 * 2.2 + 140.0),
            var5 * 0.7F
         );
      }
   }

   private void layer(MenuDrawContext var1, Identifier var2, float var3, float var4, float var5, float var6, float var7) {
      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(var3, var4);
      var1.getMatrices().rotate((float)Math.toRadians(var6));
      var1.drawTexture(var2, -var5 / 2.0F, -var5 / 2.0F, var5, var5, WHITE.method7292(var7));
      var1.getMatrices().popMatrix();
   }

   private void drawClose(MenuDrawContext var1, float var2, float var3, float var4) {
      this.closeButton = new BoundsData(10.0F, 10.0F, 18.0F, 18.0F);
      boolean var5 = this.closeButton.contains(var3, var4);
      if (var5) {
         var1.drawRoundedRect(
            this.closeButton.getFloatValue(),
            this.closeButton.getFloatValue2(),
            18.0F,
            18.0F,
            ProxyDotRadiusData.method8069(9.0F),
            WHITE.method7292(0.14F * var2)
         );
      }

      MediaGlyphs.close(
         var1, this.closeButton.getFloatValue() + 9.0F, this.closeButton.getFloatValue2() + 9.0F, 9.0F, WHITE.method7292((var5 ? 1.0F : 0.75F) * var2)
      );
   }

   private float appear(long var1, float var3, float var4) {
      float var5 = clamp01(((float)(var1 - this.openedAt) - var3) / var4);
      if (var5 <= 0.0F) {
         return 0.0F;
      } else {
         float var6 = 1.4F;
         float var7 = var6 + 1.0F;
         float var8 = var5 - 1.0F;
         return 1.0F + var7 * var8 * var8 * var8 + var6 * var8 * var8;
      }
   }

   private void drawNowPlaying(
      MenuDrawContext var1, MediaSessionManager var2, float var3, float var4, float var5, long var6, float var8, float var9, float var10, float var11
   ) {
      float var12 = var3 - var5 / 2.0F;
      this.leftPanel = new BoundsData(var12 - 10.0F, var4 - 10.0F, var5 + 20.0F, var5 + 140.0F);
      float var13 = this.appear(var6, 40.0F, 700.0F);
      float var14 = this.appear(var6, 150.0F, 600.0F);
      float var15 = this.appear(var6, 240.0F, 600.0F);
      float var16 = var9 * clamp01(var13);
      float var17 = var9 * clamp01(var14);
      float var18 = var9 * clamp01(var15);
      float var19 = var2.method6007() ? 1.0F : 0.88F;
      this.coverScale = this.coverScale + (var19 - this.coverScale) * Math.min(1.0F, var8 * 6.0F);
      float var20 = Math.min(1.0F, (float)(var6 - var2.getLongValue4()) / 600.0F);
      float var21 = 1.0F - (1.0F - var20) * (1.0F - var20) * (1.0F - var20);
      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(var3, var4 + var5 / 2.0F + (1.0F - var13) * 30.0F);
      var1.getMatrices().scale(0.82F + 0.18F * var13, 0.82F + 0.18F * var13);
      var1.getMatrices().translate(-var3, -(var4 + var5 / 2.0F));
      float var22 = var5 * this.coverScale;
      float var23 = var3 - var22 / 2.0F;
      float var24 = var4 + (var5 - var22) / 2.0F;
      float var25 = Math.max(4.0F, var5 * 0.03F);
      RenderShaders.method5288(
         MatrixUtils.method664(var1.getMatrices()),
         var23,
         var24,
         var22,
         var22,
         0.0F,
         var5 * 0.05F,
         var5 * 0.16F,
         0.0F,
         ProxyDotRadiusData.method8069(var25),
         BLACK.method7292(0.45F * var16 * this.coverScale)
      );
      Identifier var26 = var2.method6011();
      Identifier var27 = var2.method6012();
      if (var26 == null) {
         var1.drawRoundedRect(var23, var24, var22, var22, ProxyDotRadiusData.method8069(var25), WHITE.method7292(0.1F * var16));
         MediaGlyphs.playPause(var1, var3, var24 + var22 / 2.0F, var22 * 0.22F, false, WHITE.method7292(0.4F * var16));
      } else {
         if (var27 != null && var21 < 1.0F) {
            var1.drawRoundedTexture(var27, var23, var24, var22, var22, ProxyDotRadiusData.method8069(var25 / 2.0F), WHITE.method7292(var16 * (1.0F - var21)));
         }

         var1.drawRoundedTexture(
            var26, var23, var24, var22, var22, ProxyDotRadiusData.method8069(var25 / 2.0F), WHITE.method7292(var16 * (var27 == null ? 1.0F : var21))
         );
      }

      var1.getMatrices().popMatrix();
      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(0.0F, (1.0F - var14) * 16.0F);
      float var28 = var4 + var5 + 11.0F;
      Font var29 = Fonts.NEW_SEMIBOLD.getFont(8.0F);
      Font var30 = Fonts.NEW_MEDIUM.getFont(7.5F);
      boolean var31 = var10 >= var12 && var10 <= var12 + var5 && var11 >= var28 - 2.0F && var11 <= var28 + 20.0F;
      this.text(var1, var29, var2.title(), var12, var28, var5, WHITE.method7292(0.95F * var17), var31);
      this.text(var1, var30, var2.method6006(), var12, var28 + 10.5F, var5, WHITE.method7292(0.6F * var17), var31);
      var1.getMatrices().popMatrix();
      var1.getMatrices().pushMatrix();
      var1.getMatrices().translate(0.0F, (1.0F - var15) * 18.0F);
      float var32 = var28 + 30.0F;
      double var33 = var2.method6008();
      double var35 = var2.method6009();
      if (this.seekDrag) {
         this.seekPreview = clamp01((var10 - var12) / var5);
      }

      boolean var37 = var10 >= var12 - 3.0F && var10 <= var12 + var5 + 3.0F && var11 >= var32 - 5.0F && var11 <= var32 + 6.0F;
      this.barHover = this.barHover + ((!var37 && !this.seekDrag ? 0.0F : 1.0F) - this.barHover) * Math.min(1.0F, var8 * 12.0F);
      this.seekBar = new BoundsData(var12 - 3.0F, var32 - 5.0F, var5 + 6.0F, 11.0F);
      float var38 = this.seekDrag ? this.seekPreview : (var33 > 0.0 ? clamp01((float)(var35 / var33)) : 0.0F);
      this.slider(var1, var12, var32, var5, var38, 2.2F + 1.4F * this.barHover, 5.0F + 2.0F * this.barHover, var18);
      Font var39 = Fonts.NEW_MEDIUM.getFont(5.2F);
      double var40 = this.seekDrag ? this.seekPreview * var33 : var35;
      String var42 = MediaGlyphs.time(var40);
      String var43 = "-" + MediaGlyphs.time(Math.max(0.0, var33 - var40));
      var1.drawText(var39, var42, var12, var32 + 5.0F, WHITE.method7292(0.45F * var18));
      var1.drawText(var39, var43, var12 + var5 - var39.width(var43), var32 + 5.0F, WHITE.method7292(0.45F * var18));
      float var44 = var32 + 27.0F;
      MediaHelperProcess var45 = MediaHelperProcess.getInstance();
      float var46 = Math.min(34.0F, var5 * 0.13F);
      this.previousButton = square(var3 - var46, var44, 20.0F);
      this.playButton = square(var3, var44, 24.0F);
      this.nextButton = square(var3 + var46, var44, 20.0F);
      this.shuffleButton = var45.isFlag2() ? square(var3 - var46 * 2.0F, var44, 16.0F) : null;
      this.repeatButton = var45.isFlag3() ? square(var3 + var46 * 2.0F, var44, 16.0F) : null;
      this.hoverBack(var1, this.previousButton, var10, var11, var18);
      this.hoverBack(var1, this.playButton, var10, var11, var18);
      this.hoverBack(var1, this.nextButton, var10, var11, var18);
      MediaGlyphs.skip(var1, var3 - var46, var44, 13.0F, false, WHITE.method7292(0.92F * var18));
      MediaGlyphs.playPause(var1, var3, var44, 15.0F, var2.method6007(), WHITE.method7292(var18));
      MediaGlyphs.skip(var1, var3 + var46, var44, 13.0F, true, WHITE.method7292(0.92F * var18));
      if (this.shuffleButton != null) {
         this.hoverBack(var1, this.shuffleButton, var10, var11, var18);
         MediaGlyphs.shuffle(var1, var3 - var46 * 2.0F, var44, 8.0F, WHITE.method7292((var45.isFlag() ? 1.0F : 0.5F) * var18));
      }

      if (this.repeatButton != null) {
         this.hoverBack(var1, this.repeatButton, var10, var11, var18);
         MediaGlyphs.repeat(
            var1, var3 + var46 * 2.0F, var44, 8.5F, var45.getIntValue() == 1, WHITE.method7292((var45.getIntValue() != 0 ? 1.0F : 0.5F) * var18)
         );
      }

      float var47 = var44 + 25.0F;
      float var48 = var12 + 12.0F;
      float var49 = var5 - 12.0F;
      MediaPlayerState var50 = MediaPlayerState.getInstance();
      if (this.volumeDrag) {
         var50.method1675(clamp01((var10 - var48) / var49));
      }

      boolean var51 = var10 >= var48 - 3.0F && var10 <= var48 + var49 + 3.0F && var11 >= var47 - 5.0F && var11 <= var47 + 5.0F;
      this.volumeHover = this.volumeHover + ((!var51 && !this.volumeDrag ? 0.0F : 1.0F) - this.volumeHover) * Math.min(1.0F, var8 * 12.0F);
      this.volumeBar = new BoundsData(var48 - 3.0F, var47 - 5.0F, var49 + 6.0F, 10.0F);
      float var52 = var50.method1673() ? var50.method1679() : 0.0F;
      int var53 = var52 < 0.02F ? 0 : (var52 < 0.5F ? 1 : 2);
      MediaGlyphs.speaker(var1, var12 + 3.5F, var47, 7.0F, var53, WHITE.method7292(0.6F * var18));
      this.slider(var1, var48, var47, var49, var52, 2.2F + 1.2F * this.volumeHover, 5.0F + 2.0F * this.volumeHover, var18);
      if (var50.isFlag()) {
         Font var54 = Fonts.NEW_MEDIUM.getFont(5.2F);
         String var55 = t("gui.music.ducked");
         var1.drawText(var54, var55, var3 - var54.width(var55) / 2.0F, var47 + 6.0F, WHITE.method7292(0.45F * var18));
      }

      var1.getMatrices().popMatrix();
   }

   private void slider(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = var3 - var6 / 2.0F;
      var1.drawRoundedRect(var2, var9, var4, var6, ProxyDotRadiusData.method8069(var6 / 2.0F), WHITE.method7292(0.22F * var8));
      var1.drawRoundedRect(var2, var9, Math.max(var6, var4 * var5), var6, ProxyDotRadiusData.method8069(var6 / 2.0F), WHITE.method7292(0.82F * var8));
      float var10 = var2 + var4 * var5;
      var1.drawRoundedRect(var10 - var7 / 2.0F, var3 - var7 / 2.0F, var7, var7, ProxyDotRadiusData.method8069(var7 / 2.0F), WHITE.method7292(var8));
   }

   private void drawLyrics(
      MenuDrawContext var1,
      MediaSessionManager var2,
      float var3,
      float var4,
      float var5,
      float var6,
      long var7,
      float var9,
      float var10,
      float var11,
      float var12
   ) {
      this.lyricHits.clear();
      LyricsFetcher.Data2 var13 = LyricsFetcher.method528(var2.title(), var2.method6006(), var2.method6008());
      float var14 = Math.max(15.0F, Math.min(34.0F, var6 * 0.058F));
      Font var15 = Fonts.BOLD.getFont(var14);
      if (var13 != LyricsFetcher.Data2.INSTANCE2 && !var13.lines().isEmpty()) {
         String var44 = var2.getText() + var13.isFlag();
         if (!var44.equals(this.layoutKey) || Math.abs(var5 - this.layoutWidth) > 0.5F || Math.abs(var14 - this.layoutSize) > 0.1F) {
            this.layout(var13, var15, var5, var14);
            this.layoutKey = var44;
            this.layoutWidth = var5;
            this.layoutSize = var14;
            this.lyricScroll = Float.NaN;
            this.manualOffset = 0.0F;
         }

         double var45 = var2.method6009() + MusicHud.method6121();
         int var19 = var13.isFlag() ? this.activeBlock(var45) : -1;
         float var20 = var4 + var6 * 0.36F;
         float var21;
         if (var19 >= 0) {
            var21 = this.blocks.get(var19).top;
         } else if (var13.isFlag()) {
            var21 = -var14 * 2.5F;
         } else {
            var21 = 0.0F;
            var20 = var4 + var6 * 0.12F;
         }

         if (var7 > this.manualUntil) {
            this.manualOffset = this.manualOffset + -this.manualOffset * Math.min(1.0F, var9 * 4.0F);
         }

         var21 += this.manualOffset;
         if (Float.isNaN(this.lyricScroll)) {
            this.lyricScroll = var21;
         }

         float var22 = 90.0F;
         float var23 = 2.0F * (float)Math.sqrt(var22) * 0.95F;
         this.lyricVelocity = this.lyricVelocity + ((var21 - this.lyricScroll) * var22 - this.lyricVelocity * var23) * var9;
         this.lyricScroll = this.lyricScroll + this.lyricVelocity * var9;
         if (this.emphasis.length != this.blocks.size()) {
            this.emphasis = new float[this.blocks.size()];
         }

         var1.enableScissor((int)(var3 - 30.0F), (int)var4, (int)(var3 + var5 + 40.0F), (int)(var4 + var6));
         float var24 = var15.height() * 1.08F;
         int var25 = 0;

         for (int var26 = 0; var26 < this.blocks.size(); var26++) {
            NowPlayingScreen.Block var27 = this.blocks.get(var26);
            int var28 = var19 >= 0 ? var26 - var19 : 2;
            float var29 = var19 >= 0 ? Math.min(4, Math.max(0, var28)) * 0.035F : 0.0F;
            float var30 = var20 + var27.top - this.lyricScroll + var29 * (var21 - this.lyricScroll);
            if (!(var30 + var27.height < var4 - 20.0F) && !(var30 > var4 + var6 + 20.0F)) {
               boolean var31 = var26 == var19;
               this.emphasis[var26] = this.emphasis[var26] + ((var31 ? 1.0F : 0.0F) - this.emphasis[var26]) * Math.min(1.0F, var9 * 10.0F);
               float var32 = this.emphasis[var26];
               float var33 = this.appear(var7, 200.0F + var25 * 55.0F, 700.0F);
               var25++;
               boolean var34 = var11 >= var3 - 8.0F && var11 <= var3 + var5 && var12 >= var30 - 4.0F && var12 <= var30 + var27.height + 4.0F;
               float var35;
               if (!var13.isFlag()) {
                  var35 = 0.75F;
               } else if (var28 < 0) {
                  var35 = Math.max(0.05F, 0.22F + var28 * 0.06F);
               } else {
                  var35 = Math.max(0.12F, 0.34F - var28 * 0.05F);
               }

               float var36 = var35 + (1.0F - var35) * var32;
               if (var34 && var13.isFlag()) {
                  var36 = Math.max(var36, 0.7F);
               }

               float var37 = clamp01(Math.min(var30 - var4, var4 + var6 - (var30 + var27.height)) / (var14 * 3.0F) + 0.15F);
               var36 *= var37 * var10 * clamp01(var33);
               if (var13.isFlag()) {
                  this.lyricHits.add(new NowPlayingScreen.Hit(new BoundsData(var3 - 8.0F, var30 - 4.0F, var5 + 8.0F, var27.height + 8.0F), var27.time));
                  if (var34 && !var31) {
                     var1.drawRoundedRect(
                        var3 - 8.0F, var30 - 5.0F, var5 + 8.0F, var27.height + 10.0F, ProxyDotRadiusData.method8069(8.0F), WHITE.method7292(0.08F * var10)
                     );
                  }
               }

               float var38 = var13.isFlag() && !var34 ? (1.0F - var32) * Math.min(1.0F, 0.45F + Math.abs(var28) * 0.18F) : 0.0F;
               float var39 = 0.96F + 0.04F * var32;
               float var40 = (1.0F - var33) * 50.0F;
               var1.getMatrices().pushMatrix();
               var1.getMatrices().translate(var3 + var40, var30 + var27.height / 2.0F);
               var1.getMatrices().scale(var39, var39);
               var1.getMatrices().translate(-var3, -(var30 + var27.height / 2.0F));
               if (var27.dots) {
                  this.drawDots(var1, var3, var30 + var27.height / 2.0F, var14, var31, var7, var36);
               } else {
                  float var41 = var30;

                  for (String var43 : var27.lines) {
                     this.drawSoftText(var1, var15, var43, var3, var41, var36, var38 * var14 * 0.09F);
                     var41 += var24;
                  }
               }

               var1.getMatrices().popMatrix();
            }
         }

         if (var13.isFlag() && var19 < 0 && !this.blocks.isEmpty() && this.blocks.get(0).time > 2.5) {
            float var47 = var20 - var14 * 2.5F - this.lyricScroll + var21 - this.manualOffset + var14 * 0.5F;
            this.drawDots(var1, var3, var47, var14, true, var7, var10);
         }

         var1.disableScissor();
      } else {
         Font var16 = Fonts.BOLD.getFont(var14 * 0.8F);
         String var17 = t(var13 == LyricsFetcher.Data2.INSTANCE2 ? "gui.music.searching" : (var13.isFlag2() ? "gui.music.instrumental" : "gui.music.noLyrics"));
         var1.drawText(var16, var17, var3, var4 + var6 * 0.42F, WHITE.method7292(0.5F * var10));
         if (var13 != LyricsFetcher.Data2.INSTANCE2 && !var13.isFlag2()) {
            Font var18 = Fonts.NEW_MEDIUM.getFont(6.5F);
            var1.drawText(var18, t("gui.music.source"), var3, var4 + var6 * 0.42F + var16.height() + 4.0F, WHITE.method7292(0.3F * var10));
         }
      }
   }

   private void drawSoftText(MenuDrawContext var1, Font var2, String var3, float var4, float var5, float var6, float var7) {
      if (!(var6 <= 0.01F)) {
         if (var7 < 0.25F) {
            var1.drawText(var2, var3, var4, var5, WHITE.method7292(var6));
         } else {
            ZenithColor var8 = WHITE.method7292(var6 * 0.2F);

            for (int var9 = 1; var9 <= 2; var9++) {
               float var10 = var7 * var9 / 2.0F;

               for (int var11 = 0; var11 < 6; var11++) {
                  double var12 = (Math.PI * 2) * var11 / 6.0 + var9 * 0.5;
                  var1.drawText(var2, var3, var4 + (float)Math.cos(var12) * var10, var5 + (float)Math.sin(var12) * var10, var8);
               }
            }

            var1.drawText(var2, var3, var4, var5, WHITE.method7292(var6 * 0.25F));
         }
      }
   }

   private void drawDots(MenuDrawContext var1, float var2, float var3, float var4, boolean var5, long var6, float var8) {
      float var9 = var4 * 0.26F;

      for (int var10 = 0; var10 < 3; var10++) {
         float var11 = var5 ? 0.5F + 0.5F * (float)Math.sin(var6 / 280.0 - var10 * 0.9) : 0.0F;
         float var12 = var9 * (1.0F + 0.3F * var11);
         var1.drawRoundedRect(
            var2 + var10 * var9 * 1.9F + (var9 - var12) / 2.0F,
            var3 - var12 / 2.0F,
            var12,
            var12,
            ProxyDotRadiusData.method8069(var12 / 2.0F),
            WHITE.method7292(var8 * (var5 ? 0.5F + 0.5F * var11 : 0.4F))
         );
      }
   }

   private int activeBlock(double var1) {
      int var3 = -1;
      int var4 = 0;

      while (var4 < this.blocks.size() && this.blocks.get(var4).time <= var1) {
         var3 = var4++;
      }

      return var3;
   }

   private void layout(LyricsFetcher.Data2 var1, Font var2, float var3, float var4) {
      this.blocks.clear();
      float var5 = var2.height() * 1.08F;
      float var6 = var4 * 0.85F;
      float var7 = 0.0F;
      var var8 = var1.lines();

      for (int var9 = 0; var9 < var8.size(); var9++) {
         LyricsFetcher.Data3 var10 = (LyricsFetcher.Data3)var8.get(var9);
         String var11 = var10.text().trim();
         boolean var12 = var11.isEmpty();
         if (var12 && var1.isFlag()) {
            double var13 = var9 + 1 < var8.size() ? ((LyricsFetcher.Data3)var8.get(var9 + 1)).time() : var10.time() + 10.0;
            if (var13 - var10.time() < 4.0) {
               continue;
            }
         }

         if (var12 && !var1.isFlag()) {
            var7 += var6 * 0.5F;
         } else {
            List<String> var15 = var12 ? List.<String>of() : wrap(var2, var11, var3);
            float var14 = var12 ? var4 * 0.6F : (var15.size() - 1) * var5 + var2.height();
            this.blocks.add(new NowPlayingScreen.Block(var10.time(), var15, var7, var14, var12));
            var7 += var14 + var6;
         }
      }

      this.blocksHeight = var7;
   }

   private static List<String> wrap(Font var0, String var1, float var2) {
      ArrayList<String> var3 = new ArrayList<>();
      StringBuilder var4 = new StringBuilder();

      for (String var8 : var1.split(" ")) {
         String var9 = var4.isEmpty() ? var8 : var4 + " " + var8;
         if (!(var0.width(var9) <= var2) && !var4.isEmpty()) {
            var3.add(var4.toString());
            var4.setLength(0);
            var4.append(var8);
         } else {
            var4.setLength(0);
            var4.append(var9);
         }
      }

      if (!var4.isEmpty()) {
         var3.add(var4.toString());
      }

      return var3;
   }

   private void text(MenuDrawContext var1, Font var2, String var3, float var4, float var5, float var6, ZenithColor var7, boolean var8) {
      if (var3 != null && !var3.isEmpty()) {
         float var9 = var2.width(var3);
         if (var9 <= var6) {
            var1.drawText(var2, var3, var4, var5, var7);
         } else if (!var8) {
            var1.drawText(var2, MediaGlyphs.ellipsize(var2, var3, var6), var4, var5, var7);
         } else {
            float var10 = 30.0F;
            float var11 = var9 + var10;
            long var12 = (long)(var11 / 30.0F * 1000.0F);
            long var14 = 1200L;
            long var16 = System.currentTimeMillis() % (var12 + var14);
            float var18 = var16 < var14 ? 0.0F : (float)(var16 - var14) / (float)var12 * var11;
            var1.enableScissor((int)var4, (int)(var5 - 2.0F), (int)Math.ceil(var4 + var6), (int)Math.ceil(var5 + var2.height() + 3.0F));
            var1.drawText(var2, var3, var4 - var18, var5, var7);
            var1.drawText(var2, var3, var4 - var18 + var11, var5, var7);
            var1.disableScissor();
         }
      }
   }

   private void hoverBack(MenuDrawContext var1, BoundsData var2, float var3, float var4, float var5) {
      if (var2 != null && var2.contains(var3, var4)) {
         var1.drawRoundedRect(
            var2.getFloatValue(),
            var2.getFloatValue2(),
            var2.width(),
            var2.height(),
            ProxyDotRadiusData.method8069(var2.width() / 2.0F),
            WHITE.method7292(0.1F * var5)
         );
      }
   }

   private static BoundsData square(float var0, float var1, float var2) {
      return new BoundsData(var0 - var2 / 2.0F, var1 - var2 / 2.0F, var2, var2);
   }

   private void clearHits() {
      this.seekBar = this.volumeBar = this.shuffleButton = this.previousButton = this.playButton = this.nextButton = this.repeatButton = this.leftPanel = null;
      this.lyricHits.clear();
   }

   private float[] toContent(double var1, double var3) {
      float var5 = this.visibility(System.currentTimeMillis());
      float var6 = 0.97F + 0.03F * var5;
      return new float[]{(float)((var1 - this.width / 2.0F) / var6 + this.width / 2.0F), (float)((var3 - this.height / 2.0F) / var6 + this.height / 2.0F)};
   }

   @Override
   public boolean mouseClicked(Click click, boolean doubled) {
      if (click.button() != 0) {
         return super.mouseClicked(click, doubled);
      } else {
         float[] var3 = this.toContent(click.x(), click.y());
         float var4 = var3[0];
         float var5 = var3[1];
         MediaSessionManager var6 = MediaSessionManager.getInstance();
         if (this.closeButton != null && this.closeButton.contains(var4, var5)) {
            this.close();
            return true;
         } else if (this.seekBar != null && this.seekBar.contains(var4, var5) && var6.method6008() > 0.0) {
            this.seekDrag = true;
            this.seekPreview = clamp01((var4 - this.seekBar.getFloatValue() - 3.0F) / (this.seekBar.width() - 6.0F));
            return true;
         } else if (this.volumeBar != null && this.volumeBar.contains(var4, var5)) {
            this.volumeDrag = true;
            MediaPlayerState.getInstance().method1675(clamp01((var4 - this.volumeBar.getFloatValue() - 3.0F) / (this.volumeBar.width() - 6.0F)));
            return true;
         } else if (contains(this.playButton, var4, var5)) {
            var6.playPause();
            return true;
         } else if (contains(this.previousButton, var4, var5)) {
            var6.previous();
            return true;
         } else if (contains(this.nextButton, var4, var5)) {
            var6.next();
            return true;
         } else if (contains(this.shuffleButton, var4, var5) && var6.getIMediaSession() != null) {
            MediaHelperProcess.getInstance().method6864(var6.getIMediaSession().getOwner());
            return true;
         } else if (contains(this.repeatButton, var4, var5) && var6.getIMediaSession() != null) {
            MediaHelperProcess.getInstance().method6865(var6.getIMediaSession().getOwner());
            return true;
         } else {
            for (NowPlayingScreen.Hit var8 : this.lyricHits) {
               if (var8.rect.contains(var4, var5)) {
                  var6.method6019(Math.max(0.0, var8.time - MusicHud.method6121() + 0.05));
                  this.manualOffset = 0.0F;
                  this.manualUntil = 0L;
                  return true;
               }
            }

            return super.mouseClicked(click, doubled);
         }
      }
   }

   @Override
   public boolean mouseReleased(Click click) {
      if (this.seekDrag) {
         this.seekDrag = false;
         MediaSessionManager.getInstance().method6019(this.seekPreview * MediaSessionManager.getInstance().method6008());
         return true;
      } else if (this.volumeDrag) {
         this.volumeDrag = false;
         return true;
      } else {
         return super.mouseReleased(click);
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      float[] var9 = this.toContent(mouseX, mouseY);
      if (this.leftPanel != null && this.leftPanel.contains(var9[0], var9[1])) {
         MediaPlayerState.getInstance().method1676((float)Math.signum(verticalAmount) * 0.05F);
         return true;
      } else if (!this.blocks.isEmpty()) {
         this.manualOffset = this.manualOffset - (float)verticalAmount * this.layoutSize * 2.0F;
         this.manualOffset = Math.max(-this.blocksHeight, Math.min(this.blocksHeight, this.manualOffset));
         this.manualUntil = System.currentTimeMillis() + 3500L;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(KeyInput input) {
      int var2 = input.key();
      MediaSessionManager var3 = MediaSessionManager.getInstance();
      if (var2 == 32) {
         var3.playPause();
         return true;
      } else if (var2 == 262) {
         var3.method6019(var3.method6009() + 5.0);
         return true;
      } else if (var2 == 263) {
         var3.method6019(var3.method6009() - 5.0);
         return true;
      } else if (var2 != 265 && var2 != 264) {
         return super.keyPressed(input);
      } else {
         MediaPlayerState.getInstance().method1676(var2 == 265 ? 0.05F : -0.05F);
         return true;
      }
   }

   private static boolean contains(BoundsData var0, float var1, float var2) {
      return var0 != null && var0.contains(var1, var2);
   }

   private static float clamp01(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }

   private static String t(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   private static final class Block {
      private final double time;
      private final List<String> lines;
      private final float top;
      private final float height;
      private final boolean dots;

      public double time() {
         return this.time;
      }

      public List<String> lines() {
         return this.lines;
      }

      public float top() {
         return this.top;
      }

      public float height() {
         return this.height;
      }

      public boolean dots() {
         return this.dots;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Block other)) return false;
         return java.util.Objects.equals(this.time, other.time) && java.util.Objects.equals(this.lines, other.lines) && java.util.Objects.equals(this.top, other.top) && java.util.Objects.equals(this.height, other.height) && java.util.Objects.equals(this.dots, other.dots);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(time, lines, top, height, dots);
      }

      @Override
      public String toString() {
         return "Block[" + "time=" + this.time + ", " + "lines=" + this.lines + ", " + "top=" + this.top + ", " + "height=" + this.height + ", " + "dots=" + this.dots + "]";
      }

      private Block(double var1, List<String> var3, float var4, float var5, boolean var6) {
         this.time = var1;
         this.lines = var3;
         this.top = var4;
         this.height = var5;
         this.dots = var6;
      }
   }

   private static final class Hit {
      private final BoundsData rect;
      private final double time;

      public BoundsData rect() {
         return this.rect;
      }

      public double time() {
         return this.time;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Hit other)) return false;
         return java.util.Objects.equals(this.rect, other.rect) && java.util.Objects.equals(this.time, other.time);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(rect, time);
      }

      @Override
      public String toString() {
         return "Hit[" + "rect=" + this.rect + ", " + "time=" + this.time + "]";
      }

      private Hit(BoundsData var1, double var2) {
         this.rect = var1;
         this.time = var2;
      }
   }
}

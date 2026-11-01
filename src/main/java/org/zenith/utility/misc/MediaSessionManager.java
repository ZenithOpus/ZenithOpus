package org.zenith.utility.misc;

import dev.redstones.mediaplayerinfo.IMediaSession;
import dev.redstones.mediaplayerinfo.MediaInfo;
import dev.redstones.mediaplayerinfo.MediaPlayerInfo;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.lwjgl.BufferUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.utility.api.LyricsFetcher;
import org.zenith.utility.config.MediaHelperProcess;
import org.zenith.utility.render.TextureFilterUtils;

public final class MediaSessionManager {
   private static final Identifier[] identifiers = new Identifier[]{Zenith.method1501("media/art_a"), Zenith.method1501("media/art_b")};
   private static final Identifier[] identifiers2 = new Identifier[]{Zenith.method1501("media/blur_a"), Zenith.method1501("media/blur_b")};
   private static final Identifier[] identifiers3 = new Identifier[]{Zenith.method1501("media/small_a"), Zenith.method1501("media/small_b")};
   private static final int CONST_intValue = 64;
   private static final int CONST_intValue2 = 96;
   private static final ZenithColor[] zenithColors = new ZenithColor[]{new ZenithColor(-8758017), new ZenithColor(-12965270), new ZenithColor(-5022465)};
   private static final MediaSessionManager INSTANCE = new MediaSessionManager();
   private final ExecutorService executorService = Executors.newSingleThreadExecutor(var0 -> {
      Thread var1 = new Thread(var0, "zenith-media");
      var1.setDaemon(true);
      return var1;
   });
   private volatile IMediaSession iMediaSession;
   private volatile MediaInfo mediaInfo;
   private volatile boolean flag;
   private volatile long longValue;
   private long longValue2;
   private long seenAt;
   private String text = "";
   private long longValue3;
   private int intValue = -1;
   private int intValue2 = -1;
   private byte[] byteValues = new byte[0];
   private final boolean[] flags = new boolean[2];
   private final boolean[] flags2 = new boolean[2];
   private ZenithColor[] zenithColors2 = zenithColors;
   private ZenithColor[] zenithColors3 = zenithColors;
   private long longValue4;
   private double doubleValue = -1.0;
   private long longValue5;
   private double doubleValue2;
   private double doubleValue3;
   private double doubleValue4;
   private long longValue6;
   private long longValue7;
   private boolean flag2;
   private long longValue8;
   private long longValue9;
   private long longValue10;
   private boolean flag3;
   private double doubleValue5;
   private long longValue11;
   private boolean flag4;
   private double doubleValue6;
   private boolean flag5;
   private long longValue12;

   private MediaSessionManager() {
   }

   public static MediaSessionManager getInstance() {
      return INSTANCE;
   }

   public void method6000() {
      if (!this.flag) {
         this.flag = true;
         this.executorService.execute(() -> {
            try {
               long var1 = System.currentTimeMillis();
               ArrayList var3 = new ArrayList();
               ArrayList var4 = new ArrayList();

               for (IMediaSession var6 : MediaPlayerInfo.Instance.getMediaSessions()) {
                  MediaInfo var7 = var6.getMedia();
                  if (var7 != null && (!var7.getTitle().isEmpty() || !var7.getArtist().isEmpty())) {
                     var3.add(var6);
                     var4.add(var7);
                  }
               }

               IMediaSession var25 = this.iMediaSession;
               int var26 = -1;

               for (int var27 = 0; var27 < var3.size() && var26 < 0; var27++) {
                  if (((MediaInfo)var4.get(var27)).getPlaying()) {
                     var26 = var27;
                  }
               }

               for (int var28 = 0; var28 < var3.size() && var26 < 0 && var25 != null; var28++) {
                  if (((IMediaSession)var3.get(var28)).getOwner().equals(var25.getOwner())) {
                     var26 = var28;
                  }
               }

               if (var26 >= 0) {
                  IMediaSession var29 = (IMediaSession)var3.get(var26);
                  MediaInfo var8 = (MediaInfo)var4.get(var26);
                  String var9 = var8.getTitle() + "\u0000" + var8.getArtist();
                  boolean var10 = !var9.equals(this.text);
                  boolean var11 = var10 || (this.intValue < 0 ? var1 - this.longValue > 3000L : var1 - this.longValue > 20000L);
                  byte[] var12 = var11 ? var8.getArtworkPng() : null;
                  if (var11) {
                     this.longValue = var1;
                  }

                  boolean var13 = var12 != null && var12.length > 0 && (var10 || !Arrays.equals(var12, this.byteValues));
                  BufferedImage var14 = var13 ? method6023(var12) : null;
                  if (var14 == null) {
                     var13 = false;
                  }

                  boolean var15 = var13;
                  ZenithColor[] var16 = var14 == null ? null : method6026(var14);
                  int[] var17 = var14 == null ? null : method6024(var14);
                  int[] var18 = var14 == null ? null : method6016(var14, 96);
                  NativeImage var19 = var13 ? method6021(var12) : null;
                  MinecraftClient.getInstance().execute(() -> this.method6001(var29, var8, var9, var12, var15, var19, var16, var17, var18));
                  return;
               }

               MinecraftClient.getInstance().execute(() -> this.iMediaSession = null);
            } catch (Throwable var23) {
               return;
            } finally {
               this.flag = false;
            }
         });
      }
   }

   private void method6001(
      IMediaSession var1, MediaInfo var2, String var3, byte[] var4, boolean var5, NativeImage var6, ZenithColor[] var7, int[] var8, int[] var9
   ) {
      if (!var5 && var6 != null) {
         var6.close();
      }

      this.iMediaSession = var1;
      long var10 = System.currentTimeMillis();
      if (!var3.equals(this.text)) {
         boolean var12 = this.flag5;
         this.flag5 = true;
         this.text = var3;
         this.longValue3 = var10;
         this.doubleValue = -1.0;
         this.longValue7 = 0L;
         this.longValue8 = 0L;
         this.doubleValue6 = 0.0;
         this.flag3 = var12;
         this.doubleValue5 = 0.0;
         this.longValue11 = var10;
         this.flag4 = var2.getPlaying();
         MediaPlayerState.getInstance().method1669(var1.getOwner());
         if (!var5 && this.intValue >= 0) {
            this.intValue2 = this.intValue;
            this.intValue = -1;
            this.byteValues = new byte[0];
            this.zenithColors3 = this.zenithColors2;
            this.zenithColors2 = zenithColors;
            this.longValue4 = var10;
         }
      }

      if (var2.getPlaying()) {
         this.longValue9 = var10;
      }

      if (this.flag3 && var2.getPlaying() != this.flag4) {
         this.doubleValue5 = this.method6003(var10);
         this.longValue11 = var10;
         this.flag4 = var2.getPlaying();
      }

      if (var5) {
         this.byteValues = var4;
         this.intValue2 = this.intValue;
         this.intValue = this.intValue2 == 0 ? 1 : 0;
         method6022(identifiers[this.intValue], var6);
         TextureFilterUtils.smooth(identifiers[this.intValue]);
         this.flags[this.intValue] = var8 != null && method6015(identifiers2[this.intValue], var8, 64);
         this.flags2[this.intValue] = var9 != null && method6015(identifiers3[this.intValue], var9, 96);
         this.zenithColors3 = this.zenithColors2;
         this.zenithColors2 = var7 == null ? zenithColors : var7;
         this.longValue4 = var10;
      }

      this.mediaInfo = var2;
      this.longValue2 = var10;
      this.seenAt = var10;
      if (var10 - this.longValue8 > 900L && MediaHelperProcess.getInstance().method6859()) {
         this.longValue8 = var10;
         MediaHelperProcess.getInstance().method6866(var1.getOwner());
         if (var10 - this.longValue10 > 3000L) {
            this.longValue10 = var10;
            MediaHelperProcess.getInstance().method6868(var1.getOwner());
            MediaHelperProcess.getInstance().method6863(var1.getOwner());
         }
      }
   }

   public void method6002(double var1, double var3, long var5, boolean var7) {
      long var8 = System.currentTimeMillis();
      this.doubleValue3 = var1;
      this.doubleValue4 = var3;
      this.longValue6 = var5 > 0L && var5 <= var8 ? var5 : var8;
      this.longValue7 = var8;
      this.longValue12 = var8;
      this.flag2 = var7;
      this.doubleValue6 = var3;
      this.flag3 = true;
      this.doubleValue5 = var1 + (var7 ? (var8 - this.longValue6) / 1000.0 : 0.0);
      this.longValue11 = var8;
      this.flag4 = var7;
   }

   private double method6003(long var1) {
      return this.doubleValue5 + (this.flag4 ? (var1 - this.longValue11) / 1000.0 : 0.0);
   }

   private boolean method6004() {
      return System.currentTimeMillis() - this.longValue12 < 15000L;
   }

   private boolean method6005() {
      return this.doubleValue4 > 0.0 && System.currentTimeMillis() - this.longValue7 < 4000L;
   }

   public boolean active() {
      return this.iMediaSession != null && this.mediaInfo != null;
   }

   public long getSeenAt() {
      return this.seenAt;
   }

   public long getLongValue9() {
      return this.longValue9;
   }

   public IMediaSession getIMediaSession() {
      return this.iMediaSession;
   }

   public String title() {
      return this.mediaInfo == null ? "" : this.mediaInfo.getTitle();
   }

   public String method6006() {
      return this.mediaInfo == null ? "" : this.mediaInfo.getArtist();
   }

   public boolean method6007() {
      return this.mediaInfo != null && this.mediaInfo.getPlaying();
   }

   public double method6008() {
      if (this.doubleValue6 > 0.0) {
         return this.doubleValue6;
      } else {
         double var1 = LyricsFetcher.method529(this.title(), this.method6006());
         if (var1 > 0.0) {
            return var1;
         } else if (!this.method6004() && !this.flag3) {
            return this.mediaInfo == null ? 0.0 : this.mediaInfo.getDuration();
         } else {
            return 0.0;
         }
      }
   }

   public double method6009() {
      if (this.mediaInfo == null) {
         return 0.0;
      } else {
         long var1 = System.currentTimeMillis();
         if (this.doubleValue >= 0.0 && var1 - this.longValue5 < 2500L) {
            double var9 = this.method6007() ? (var1 - this.longValue5) / 1000.0 : 0.0;
            return this.method6010(this.doubleValue + var9);
         } else {
            double var3;
            if (this.method6005()) {
               double var5 = this.flag2 ? (var1 - this.longValue6) / 1000.0 : 0.0;
               var3 = this.method6010(this.doubleValue3 + var5);
            } else if (this.flag3) {
               var3 = this.method6010(this.method6003(var1));
            } else if (this.method6004()) {
               var3 = 0.0;
            } else {
               double var10 = this.mediaInfo.getPosition();
               double var7 = this.method6007() ? Math.min(2.0, (var1 - this.longValue2) / 1000.0) : 0.0;
               var3 = this.method6010(var10 + var7);
            }

            if (Math.abs(var3 - this.doubleValue2) < 1.2 && var3 < this.doubleValue2 && this.method6007()) {
               var3 = this.doubleValue2;
            }

            this.doubleValue2 = var3;
            return var3;
         }
      }
   }

   private double method6010(double var1) {
      double var3 = this.method6008();
      return var3 > 0.0 ? Math.max(0.0, Math.min(var3, var1)) : Math.max(0.0, var1);
   }

   public String getText() {
      return this.text;
   }

   public long getLongValue3() {
      return this.longValue3;
   }

   public Identifier method6011() {
      return this.intValue < 0 ? null : identifiers[this.intValue];
   }

   public Identifier method6012() {
      return this.intValue2 < 0 ? null : identifiers[this.intValue2];
   }

   public Identifier method6013() {
      return this.intValue < 0 ? null : (this.flags2[this.intValue] ? identifiers3[this.intValue] : identifiers[this.intValue]);
   }

   public Identifier method6014() {
      return this.intValue2 < 0 ? null : (this.flags2[this.intValue2] ? identifiers3[this.intValue2] : identifiers[this.intValue2]);
   }

   private static boolean method6015(Identifier var0, int[] var1, int var2) {
      try {
         NativeImage var3 = new NativeImage(var2, var2, false);

         for (int var4 = 0; var4 < var2; var4++) {
            for (int var5 = 0; var5 < var2; var5++) {
               var3.setColorArgb(var5, var4, var1[var4 * var2 + var5]);
            }
         }

         MinecraftClient.getInstance().getTextureManager().registerTexture(var0, new NativeImageBackedTexture(var0::toString, var3));
         TextureFilterUtils.smooth(var0);
         return true;
      } catch (Throwable var6) {
         return false;
      }
   }

   private static int[] method6016(BufferedImage var0, int var1) {
      int var2 = var0.getWidth();
      int var3 = var0.getHeight();
      int[] var4 = new int[var1 * var1];

      for (int var5 = 0; var5 < var1; var5++) {
         for (int var6 = 0; var6 < var1; var6++) {
            float var7 = (float)(var6 * var2) / var1;
            float var8 = (float)((var6 + 1) * var2) / var1;
            float var9 = (float)(var5 * var3) / var1;
            float var10 = (float)((var5 + 1) * var3) / var1;
            float var11 = 0.0F;
            float var12 = 0.0F;
            float var13 = 0.0F;
            float var14 = 0.0F;

            for (int var15 = (int)var9; var15 < Math.ceil(var10) && var15 < var3; var15++) {
               float var16 = Math.min((float)(var15 + 1), var10) - Math.max((float)var15, var9);

               for (int var17 = (int)var7; var17 < Math.ceil(var8) && var17 < var2; var17++) {
                  float var18 = var16 * (Math.min((float)(var17 + 1), var8) - Math.max((float)var17, var7));
                  if (!(var18 <= 0.0F)) {
                     int var19 = var0.getRGB(var17, var15);
                     var11 += (var19 >> 16 & 0xFF) * var18;
                     var12 += (var19 >> 8 & 0xFF) * var18;
                     var13 += (var19 & 0xFF) * var18;
                     var14 += var18;
                  }
               }
            }

            if (var14 <= 0.0F) {
               var14 = 1.0F;
            }

            var4[var5 * var1 + var6] = 0xFF000000 | (int)(var11 / var14) << 16 | (int)(var12 / var14) << 8 | (int)(var13 / var14);
         }
      }

      return var4;
   }

   public Identifier method6017() {
      return this.intValue >= 0 && this.flags[this.intValue] ? identifiers2[this.intValue] : null;
   }

   public Identifier method6018() {
      return this.intValue2 >= 0 && this.flags[this.intValue2] ? identifiers2[this.intValue2] : null;
   }

   public long getLongValue4() {
      return this.longValue4;
   }

   public ZenithColor[] getZenithColors2() {
      return this.zenithColors2;
   }

   public ZenithColor color(int var1) {
      float var2 = Math.min(1.0F, (float)(System.currentTimeMillis() - this.longValue4) / 700.0F);
      ZenithColor var3 = this.zenithColors3[Math.min(var1, this.zenithColors3.length - 1)];
      ZenithColor var4 = this.zenithColors2[Math.min(var1, this.zenithColors2.length - 1)];
      return var3.method7295(var4, var2);
   }

   public void playPause() {
      this.method6020(IMediaSession::playPause);
   }

   public void next() {
      this.method6020(IMediaSession::next);
   }

   public void previous() {
      this.method6020(IMediaSession::previous);
   }

   public void method6019(double var1) {
      IMediaSession var3 = this.iMediaSession;
      if (var3 != null && !(this.method6008() <= 0.0)) {
         this.doubleValue = this.method6010(var1);
         this.longValue5 = System.currentTimeMillis();
         this.flag3 = true;
         this.doubleValue5 = this.doubleValue;
         this.longValue11 = this.longValue5;
         this.flag4 = this.method6007();
         MediaHelperProcess.getInstance().method6860(var3.getOwner(), this.doubleValue);
      }
   }

   private void method6020(Consumer<IMediaSession> var1) {
      IMediaSession var2 = this.iMediaSession;
      if (var2 != null) {
         this.executorService.execute(() -> {
            try {
               var1.accept(var2);
            } catch (Throwable var4) {
            }

            MinecraftClient.getInstance().execute(this::method6000);
         });
      }
   }

   private static NativeImage method6021(byte[] var0) {
      try {
         ByteBuffer var1 = BufferUtils.createByteBuffer(var0.length).put(var0);
         var1.flip();
         return NativeImage.read(var1);
      } catch (Exception var2) {
         return null;
      }
   }

   private static void method6022(Identifier var0, NativeImage var1) {
      if (var1 != null) {
         try {
            MinecraftClient.getInstance().getTextureManager().registerTexture(var0, new NativeImageBackedTexture(var0::toString, var1));
         } catch (Exception var3) {
         }
      }
   }

   private static BufferedImage method6023(byte[] var0) {
      try {
         return ImageIO.read(new ByteArrayInputStream(var0));
      } catch (Throwable var2) {
         return null;
      }
   }

   private static int[] method6024(BufferedImage var0) {
      byte var1 = 64;
      float[][] var2 = new float[3][var1 * var1];
      int var3 = var0.getWidth();
      int var4 = var0.getHeight();

      for (int var5 = 0; var5 < var1; var5++) {
         for (int var6 = 0; var6 < var1; var6++) {
            int var7 = var6 * var3 / var1;
            int var8 = Math.max(var7 + 1, (var6 + 1) * var3 / var1);
            int var9 = var5 * var4 / var1;
            int var10 = Math.max(var9 + 1, (var5 + 1) * var4 / var1);
            int var11 = Math.max(1, (var8 - var7) / 4);
            int var12 = Math.max(1, (var10 - var9) / 4);
            float var13 = 0.0F;
            float var14 = 0.0F;
            float var15 = 0.0F;
            int var16 = 0;

            for (int var17 = var9; var17 < var10; var17 += var12) {
               for (int var18 = var7; var18 < var8; var18 += var11) {
                  int var19 = var0.getRGB(Math.min(var3 - 1, var18), Math.min(var4 - 1, var17));
                  var13 += var19 >> 16 & 0xFF;
                  var14 += var19 >> 8 & 0xFF;
                  var15 += var19 & 0xFF;
                  var16++;
               }
            }

            int var37 = var5 * var1 + var6;
            var2[0][var37] = var13 / var16;
            var2[1][var37] = var14 / var16;
            var2[2][var37] = var15 / var16;
         }
      }

      for (int var20 = 0; var20 < 3; var20++) {
         for (float[] var28 : var2) {
            method6025(var28, var1, 5);
         }
      }

      int[] var21 = new int[var1 * var1];

      for (int var23 = 0; var23 < var21.length; var23++) {
         float var25 = var2[0][var23];
         float var27 = var2[1][var23];
         float var29 = var2[2][var23];
         float[] var30 = Color.RGBtoHSB((int)var25, (int)var27, (int)var29, null);
         int var31 = Color.HSBtoRGB(var30[0], Math.min(1.0F, var30[1] * 1.25F), Math.min(1.0F, var30[2] * 0.95F));
         float var32 = (var23 % var1 + 0.5F) / var1 - 0.5F;
         float var33 = (var23 / var1 + 0.5F) / var1 - 0.5F;
         float var34 = (float)Math.sqrt(var32 * var32 + var33 * var33);
         float var35 = Math.max(0.0F, Math.min(1.0F, (0.5F - var34) / 0.2F));
         var35 = var35 * var35 * (3.0F - 2.0F * var35);
         var21[var23] = (int)(var35 * 255.0F) << 24 | var31 & 16777215;
      }

      return var21;
   }

   private static void method6025(float[] var0, int var1, int var2) {
      float[] var3 = new float[var0.length];

      for (int var4 = 0; var4 < var1; var4++) {
         for (int var5 = 0; var5 < var1; var5++) {
            float var6 = 0.0F;

            for (int var7 = -var2; var7 <= var2; var7++) {
               var6 += var0[var4 * var1 + Math.max(0, Math.min(var1 - 1, var5 + var7))];
            }

            var3[var4 * var1 + var5] = var6 / (var2 * 2 + 1);
         }
      }

      for (int var8 = 0; var8 < var1; var8++) {
         for (int var9 = 0; var9 < var1; var9++) {
            float var10 = 0.0F;

            for (int var11 = -var2; var11 <= var2; var11++) {
               var10 += var3[Math.max(0, Math.min(var1 - 1, var8 + var11)) * var1 + var9];
            }

            var0[var8 * var1 + var9] = var10 / (var2 * 2 + 1);
         }
      }
   }

   private static ZenithColor[] method6026(BufferedImage var0) {
      try {
         byte var1 = 32;
         float[][] var2 = new float[12][5];
         float[] var3 = new float[4];

         for (int var4 = 0; var4 < var1; var4++) {
            for (int var5 = 0; var5 < var1; var5++) {
               int var6 = var0.getRGB(var5 * var0.getWidth() / var1, var4 * var0.getHeight() / var1);
               int var7 = var6 >> 16 & 0xFF;
               int var8 = var6 >> 8 & 0xFF;
               int var9 = var6 & 0xFF;
               float[] var10 = Color.RGBtoHSB(var7, var8, var9, null);
               var3[0] += var7;
               var3[1] += var8;
               var3[2] += var9;
               var3[3]++;
               float var11 = var10[1] * var10[1] * (0.25F + var10[2]);
               if (!(var10[2] < 0.12F)) {
                  int var12 = Math.min(11, (int)(var10[0] * 12.0F));
                  var2[var12][0] = var2[var12][0] + var7 * var11;
                  var2[var12][1] = var2[var12][1] + var8 * var11;
                  var2[var12][2] = var2[var12][2] + var9 * var11;
                  var2[var12][3] = var2[var12][3] + var11;
                  var2[var12][4]++;
               }
            }
         }

         ArrayList<float[]> var14 = new ArrayList<>();

         for (float[] var21 : var2) {
            if (var21[3] > 1.0E-4F) {
               var14.add(var21);
            }
         }

         var14.sort((var0x, var1x) -> Float.compare(var1x[3], var0x[3]));
         ZenithColor var16 = new ZenithColor((int)(var3[0] / var3[3]), (int)(var3[1] / var3[3]), (int)(var3[2] / var3[3]));
         ZenithColor[] var18 = new ZenithColor[3];

         for (int var20 = 0; var20 < 3; var20++) {
            if (var20 < var14.size()) {
               float[] var22 = (float[])var14.get(var20);
               var18[var20] = method6027(new ZenithColor((int)(var22[0] / var22[3]), (int)(var22[1] / var22[3]), (int)(var22[2] / var22[3])));
            } else {
               var18[var20] = var20 == 0 ? method6027(var16) : var18[0].method7295(var16, 0.5F * var20);
            }
         }

         return var18;
      } catch (Throwable var13) {
         return null;
      }
   }

   private static ZenithColor method6027(ZenithColor var0) {
      float[] var1 = Color.RGBtoHSB(var0.getIntValue(), var0.getIntValue2(), var0.getIntValue3(), null);
      float var2 = Math.min(1.0F, Math.max(0.35F, var1[1] * 1.15F));
      float var3 = Math.min(0.95F, Math.max(0.55F, var1[2]));
      return new ZenithColor(0xFF000000 | Color.HSBtoRGB(var1[0], var2, var3));
   }
}

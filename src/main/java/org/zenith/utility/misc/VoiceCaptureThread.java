package org.zenith.utility.misc;

import io.github.jaredmdobson.concentus.OpusApplication;
import io.github.jaredmdobson.concentus.OpusEncoder;
import io.github.jaredmdobson.concentus.OpusException;
import io.github.jaredmdobson.concentus.OpusSignal;
import java.util.concurrent.locks.LockSupport;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VoiceCaptureThread implements Runnable {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Voice");
   private static final int CONST_intValue = 24000;
   private static final long CONST_longValue = 3000L;
   private static final long CONST_longValue2 = 300L;
   private static final int CONST_intValue2 = 3;
   private static final int CONST_intValue3 = 20;
   private static final int CONST_intValue4 = 2;
   private final VoiceState voiceState;
   private volatile boolean running = true;
   private final Thread thread;
   private long longValue;
   private String text;
   private boolean flag;
   private long longValue2;
   private final Class9 class9 = new Class9();
   private final VoiceDenoiser voiceDenoiser = new VoiceDenoiser();
   private OpusEncoder opusEncoder;
   private int intValue;
   private final short[] shortValues = new short[960];
   private final byte[] byteValues = new byte[1275];
   private final short[][] shortValues2 = new short[2][960];
   private int intValue2;
   private int intValue3;
   private int intValue4;
   private boolean flag2;
   private int intValue5;
   private int intValue6;
   private int intValue7;

   public VoiceCaptureThread(VoiceState var1) {
      this.voiceState = var1;
      this.thread = new Thread(this, "Zenith Voice Capture");
      this.thread.setDaemon(true);
      this.thread.setPriority(7);
   }

   public void start() {
      this.thread.start();
   }

   public void stop() {
      this.running = false;
      LockSupport.unpark(this.thread);

      try {
         this.thread.join(2000L);
      } catch (InterruptedException var2) {
         Thread.currentThread().interrupt();
      }
   }

   @Override
   public void run() {
      try {
         while (this.running) {
            this.method267();
         }
      } catch (Throwable var5) {
         logger.error("Голос: поток микрофона упал", var5);
         this.voiceState.setText("сбой захвата: " + var5);
      } finally {
         this.method272();
         this.method277();
         this.voiceDenoiser.close();
      }
   }

   private void method267() {
      VoiceState.Data var1 = this.voiceState.getData();
      long var2 = System.currentTimeMillis();
      if (!this.voiceState.method5378(var2, 300L)) {
         this.method272();
         if (this.flag) {
            ALC11.alcCaptureStop(this.longValue);
            this.flag = false;
         }

         this.voiceState.setFloatValue2(-100.0F);
         LockSupport.parkNanos(10000000L);
      } else {
         String var4 = var1.getText();
         if (this.longValue != 0L && !var4.equals(this.text)) {
            this.method277();
         }

         if (this.longValue != 0L || var2 >= this.longValue2 && this.method275(var4)) {
            if (!this.flag) {
               ALC11.alcCaptureStart(this.longValue);
               this.flag = true;
               this.class9.reset();
               this.method276();
            }

            if (ALC10.alcGetInteger(this.longValue, 787) == 0) {
               logger.info("Голос: микрофон отключён");
               this.voiceState.setText("микрофон отключён");
               this.method277();
               this.longValue2 = var2 + 3000L;
            } else {
               int var5 = ALC10.alcGetInteger(this.longValue, 786);
               if (var5 < 960) {
                  LockSupport.parkNanos(2000000L);
               } else {
                  ALC11.alcCaptureSamples(this.longValue, this.shortValues, 960);
                  this.method268(this.shortValues, var1);
               }
            }
         } else {
            LockSupport.parkNanos(20000000L);
         }
      }
   }

   private void method268(short[] var1, VoiceState.Data var2) {
      boolean var3 = var2.isFlag3() && var2.isFlag4() && this.voiceDenoiser.method802(var1, var1.length);
      float var4 = this.class9.method1530(var1, var2.getFloatValue2(), var2.isFlag3() && !var3);
      this.voiceState.setFloatValue2(var4);
      this.method274(var2.getIntValue());
      if (this.opusEncoder != null) {
         if (this.voiceState.isFlag5()) {
            this.method272();
            int var11 = this.method273(var1);
            if (var11 > 0) {
               this.voiceState.method5381(this.intValue4, this.byteValues, var11);
               this.intValue4 = this.intValue4 + 1 & 65535;
            }

            this.voiceState.setFlag6(false);
         } else {
            boolean var5 = this.voiceState.isFlag2();
            int var6 = this.voiceState.getIntValue();
            boolean var7 = var6 != 0;
            boolean var8;
            if (var2.isFlag()) {
               if (var5 || var7) {
                  this.intValue6 = 3;
               }

               var8 = this.intValue6 > 0;
               if (this.intValue6 > 0 && !var5 && !var7) {
                  this.intValue6--;
               }
            } else {
               if (var7 || var4 > var2.getFloatValue()) {
                  this.intValue7 = 20;
               }

               var8 = this.intValue7 > 0;
               if (this.intValue7 > 0) {
                  this.intValue7--;
               }
            }

            int var9 = var2.getIntValue3() & 29;
            int var10 = var7 ? var6 : (this.flag2 && this.intValue5 != var9 && !var5 ? this.intValue5 : var9);
            if (!this.voiceState.method5379()) {
               var8 = false;
            }

            if (var8 && this.flag2 && var10 != this.intValue5) {
               this.method272();
            }

            if (var8 && !this.flag2) {
               this.flag2 = true;
               this.intValue5 = var10;
               this.opusEncoder.resetState();
               if (!var2.isFlag()) {
                  this.method269(var2);
               }
            }

            if (var8) {
               this.method271(var1, var2, 0);
            } else {
               this.method272();
            }

            this.method270(var1);
            this.voiceState.setFlag6(this.flag2);
         }
      }
   }

   private void method269(VoiceState.Data var1) {
      for (int var2 = 0; var2 < this.intValue2; var2++) {
         int var3 = (this.intValue3 - this.intValue2 + var2 + 2) % 2;
         this.method271(this.shortValues2[var3], var1, 0);
      }

      this.intValue2 = 0;
   }

   private void method270(short[] var1) {
      System.arraycopy(var1, 0, this.shortValues2[this.intValue3], 0, var1.length);
      this.intValue3 = (this.intValue3 + 1) % 2;
      this.intValue2 = Math.min(2, this.intValue2 + 1);
   }

   private void method271(short[] var1, VoiceState.Data var2, int var3) {
      int var4 = this.method273(var1);
      if (var4 > 0) {
         int var5 = this.intValue5 | var3;
         this.voiceState.method5380(var5, var2.getIntValue2(), this.intValue4, this.byteValues, var4);
         this.intValue4 = this.intValue4 + 1 & 65535;
      }
   }

   private void method272() {
      if (this.flag2) {
         this.flag2 = false;
         this.intValue6 = 0;
         this.intValue7 = 0;
         int var1 = 2 | this.intValue5;
         this.voiceState.method5380(var1, this.voiceState.getData().getIntValue2(), this.intValue4, this.byteValues, 0);
         this.intValue4 = this.intValue4 + 1 & 65535;
         this.voiceState.setFlag6(false);
      }
   }

   private int method273(short[] var1) {
      try {
         return this.opusEncoder.encode(var1, 0, 960, this.byteValues, 0, this.byteValues.length);
      } catch (RuntimeException | OpusException var3) {
         logger.debug("Голос: кадр не закодирован: {}", var3.toString());
         this.opusEncoder.resetState();
         return 0;
      }
   }

   private void method274(int var1) {
      try {
         if (this.opusEncoder == null) {
            this.opusEncoder = new OpusEncoder(48000, 1, OpusApplication.OPUS_APPLICATION_VOIP);
            this.opusEncoder.setSignalType(OpusSignal.OPUS_SIGNAL_VOICE);
            this.opusEncoder.setComplexity(8);
            this.opusEncoder.setUseVBR(true);
            this.opusEncoder.setUseInbandFEC(true);
            this.opusEncoder.setPacketLossPercent(3);
            this.opusEncoder.setUseDTX(false);
            this.intValue = 0;
         }

         if (this.intValue != var1) {
            this.opusEncoder.setBitrate(var1);
            this.intValue = var1;
         }
      } catch (OpusException var3) {
         logger.error("Голос: кодер Opus не создан: {}", var3.toString());
         this.voiceState.setText("кодек недоступен");
         this.opusEncoder = null;
      }
   }

   private boolean method275(String var1) {
      long var2 = ALC11.alcCaptureOpenDevice(var1.isEmpty() ? null : var1, 48000, 4353, 24000);
      if (var2 == 0L && !var1.isEmpty()) {
         logger.info("Голос: микрофон «{}» недоступен, беру системный", var1);
         var2 = ALC11.alcCaptureOpenDevice((CharSequence)null, 48000, 4353, 24000);
      }

      if (var2 == 0L) {
         this.voiceState.setText("микрофон не открывается");
         this.longValue2 = System.currentTimeMillis() + 3000L;
         return false;
      } else {
         this.longValue = var2;
         this.text = var1;
         this.flag = false;
         this.voiceState.setText("");
         String var4 = ALC10.alcGetString(this.longValue, 784);
         this.voiceState.method5382(var4);
         logger.info("Голос: микрофон открыт — {}", var4);
         return true;
      }
   }

   private void method276() {
      for (int var1 = ALC10.alcGetInteger(this.longValue, 786); var1 >= 960; var1 -= 960) {
         ALC11.alcCaptureSamples(this.longValue, this.shortValues, 960);
      }
   }

   private void method277() {
      if (this.longValue != 0L) {
         if (this.flag) {
            ALC11.alcCaptureStop(this.longValue);
            this.flag = false;
         }

         ALC11.alcCaptureCloseDevice(this.longValue);
         this.longValue = 0L;
         this.text = null;
         this.voiceState.method5382("");
      }
   }
}

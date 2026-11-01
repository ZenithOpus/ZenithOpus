package org.zenith.utility.misc;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.locks.LockSupport;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.EXTThreadLocalContext;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VoicePlaybackThread implements Runnable {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Voice");
   private static final int CONST_intValue = 2;
   private static final int CONST_intValue2 = 100;
   private static final int CONST_intValue3 = 6;
   private static final long CONST_longValue = 3000L;
   private static final long CONST_longValue2 = 3000L;
   private static final long CONST_longValue3 = 30000L;
   private final VoiceState voiceState;
   private final Thread thread;
   private volatile boolean running = true;
   private volatile boolean flag;
   private long longValue;
   private long longValue2;
   private ShortBuffer shortBuffer;
   private long longValue3;
   private String text;
   private boolean flag2;

   public VoicePlaybackThread(VoiceState var1) {
      this.voiceState = var1;
      this.thread = new Thread(this, "Zenith Voice Playback");
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

   public void method1583() {
      this.flag = true;
   }

   @Override
   public void run() {
      try {
         while (this.running) {
            this.method1584();
         }
      } catch (Throwable var5) {
         logger.error("Голос: поток вывода упал", var5);
         this.voiceState.setText2("сбой вывода: " + var5);
      } finally {
         this.close();
      }
   }

   private void method1584() {
      long var1 = System.currentTimeMillis();
      VoiceState.Data var3 = this.voiceState.getData();
      if (this.longValue2 != 0L && (this.flag || !var3.getText2().equals(this.text) || var3.isFlag2() != this.flag2)) {
         this.flag = false;
         this.close();
      }

      if (this.longValue2 != 0L || var1 >= this.longValue3 && this.method1588(var3.getText2(), var3.isFlag2())) {
         if (ALC10.alcGetInteger(this.longValue, 787) == 0) {
            logger.info("Голос: устройство вывода отключено");
            this.voiceState.setText2("устройство вывода отключено");
            this.close();
            this.longValue3 = var1 + 3000L;
         } else {
            for (VoiceSpeakerStream var5 : this.voiceState.method5373()) {
               this.method1585(var5, var1);
            }

            this.voiceState.method5374(var1, 30000L);
            LockSupport.parkNanos(2000000L);
         }
      } else {
         this.voiceState.method5375(var1);
         LockSupport.parkNanos(20000000L);
      }
   }

   private void method1585(VoiceSpeakerStream var1, long var2) {
      if (var1.intValue3 != 0 || var1.voiceFrameBuffer.active() && this.method1586(var1)) {
         int var4 = var1.intValue3;
         int var5 = AL10.alGetSourcei(var4, 4118);

         while (var5-- > 0) {
            var1.arrayDeque.addLast(AL10.alSourceUnqueueBuffers(var4));
         }

         int var6 = AL10.alGetSourcei(var4, 4117);
         VoiceState.Data2 var7 = this.voiceState.method5376(var1);
         int var8 = 0;

         while (var6 < 2 && !var1.arrayDeque.isEmpty() && var8++ < 6) {
            VoiceFrameBuffer.Data var9 = var1.voiceFrameBuffer.method8028(var2);
            if (var9.getType2() == VoiceFrameBuffer.Type2.NONE) {
               break;
            }

            if (var9.getType2() == VoiceFrameBuffer.Type2.END) {
               var1.method1553();
               break;
            }

            int var10 = var1.method1552(var9);
            if (var10 > 0) {
               float var11 = Class9.method1531(var1.shortValues, var10);
               var1.floatValue2 = var11;
               if (var9.getType2() != VoiceFrameBuffer.Type2.PLC && var11 > -55.0F) {
                  var1.longValue3 = var2;
               }

               var1.method1550(var10, var7.getFloatValue5());
               int var12 = var1.arrayDeque.pollFirst();
               this.shortBuffer.clear();
               this.shortBuffer.put(var1.shortValues, 0, var10).flip();
               AL10.alBufferData(var12, 4353, this.shortBuffer, 48000);
               AL10.alSourceQueueBuffers(var4, var12);
               var6++;
            }
         }

         AL10.alSource3f(var4, 4100, var7.getFloatValue(), var7.getFloatValue2(), var7.getFloatValue3());
         AL10.alSourcef(var4, 4106, var7.getFloatValue4());
         int var13 = AL10.alGetSourcei(var4, 4112);
         if (var6 > 0 && var13 != 4114) {
            AL10.alSourcePlay(var4);
         }

         if (var6 == 0 && !var1.voiceFrameBuffer.active() && var2 - var1.longValue2 > 3000L) {
            this.method1587(var1);
         }
      }
   }

   private boolean method1586(VoiceSpeakerStream var1) {
      AL10.alGetError();
      int var2 = AL10.alGenSources();
      if (AL10.alGetError() == 0 && var2 != 0) {
         int[] var3 = new int[6];
         AL10.alGenBuffers(var3);
         if (AL10.alGetError() != 0) {
            AL10.alDeleteSources(var2);
            return false;
         } else {
            AL10.alSourcei(var2, 514, 1);
            AL10.alSourcef(var2, 4129, 0.0F);
            AL10.alSourcef(var2, 4110, 4.0F);
            AL10.alSourcef(var2, 4106, 0.0F);
            var1.intValue3 = var2;
            var1.intValues = var3;
            var1.arrayDeque.clear();

            for (int var7 : var3) {
               var1.arrayDeque.addLast(var7);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private void method1587(VoiceSpeakerStream var1) {
      if (var1.intValue3 != 0) {
         AL10.alSourceStop(var1.intValue3);
         AL10.alSourcei(var1.intValue3, 4105, 0);
         AL10.alDeleteSources(var1.intValue3);
         AL10.alDeleteBuffers(var1.intValues);
         var1.intValue3 = 0;
         var1.intValues = new int[0];
         var1.arrayDeque.clear();
      }
   }

   private boolean method1588(String var1, boolean var2) {
      long var3 = ALC10.alcOpenDevice(var1.isEmpty() ? null : var1);
      if (var3 == 0L && !var1.isEmpty()) {
         logger.info("Голос: вывод «{}» недоступен, беру системный", var1);
         var3 = ALC10.alcOpenDevice((CharSequence)null);
      }

      if (var3 == 0L) {
         this.voiceState.setText2("устройство вывода не открывается");
         this.longValue3 = System.currentTimeMillis() + 3000L;
         return false;
      } else {
         ALCCapabilities var5 = ALC.createCapabilities(var3);
         if (!var5.ALC_EXT_thread_local_context) {
            ALC10.alcCloseDevice(var3);
            this.voiceState.setText2("OpenAL без thread-local контекста");
            this.longValue3 = Long.MAX_VALUE;
            return false;
         } else {
            MemoryStack var8 = MemoryStack.stackPush();

            long var6;
            try {
               IntBuffer var9 = var8.mallocInt(7);
               var9.put(4103).put(48000);
               var9.put(4104).put(100);
               if (var5.ALC_SOFT_HRTF) {
                  var9.put(6546).put(var2 ? 1 : 0);
               }

               var9.put(0).flip();
               var6 = ALC10.alcCreateContext(var3, var9);
            } catch (Throwable var12) {
               if (var8 != null) {
                  try {
                     var8.close();
                  } catch (Throwable var11) {
                     var12.addSuppressed(var11);
                  }
               }

               throw var12;
            }

            if (var8 != null) {
               var8.close();
            }

            if (var6 != 0L && EXTThreadLocalContext.alcSetThreadContext(var6)) {
               AL.setCurrentThread(AL.createCapabilities(var5));
               AL10.alDistanceModel(0);
               AL10.alListenerf(4106, 1.0F);
               this.longValue = var3;
               this.longValue2 = var6;
               this.text = var1;
               this.flag2 = var2;
               this.shortBuffer = MemoryUtil.memAllocShort(960);
               this.voiceState.setText2("");
               String var13 = var5.ALC_ENUMERATE_ALL_EXT ? ALC10.alcGetString(this.longValue, 4115) : ALC10.alcGetString(this.longValue, 4101);
               this.voiceState.method5383(var13);
               logger.info("Голос: вывод открыт — {}{}", var13, var5.ALC_SOFT_HRTF && var2 ? " (HRTF)" : "");
               return true;
            } else {
               if (var6 != 0L) {
                  ALC10.alcDestroyContext(var6);
               }

               ALC10.alcCloseDevice(var3);
               this.voiceState.setText2("контекст OpenAL не создан");
               this.longValue3 = System.currentTimeMillis() + 3000L;
               return false;
            }
         }
      }
   }

   private void close() {
      if (this.longValue2 != 0L) {
         for (VoiceSpeakerStream var2 : this.voiceState.method5373()) {
            this.method1587(var2);
         }

         EXTThreadLocalContext.alcSetThreadContext(0L);
         AL.setCurrentThread(null);
         ALC10.alcDestroyContext(this.longValue2);
         this.longValue2 = 0L;
      }

      if (this.longValue != 0L) {
         ALC10.alcCloseDevice(this.longValue);
         this.longValue = 0L;
      }

      this.voiceState.method5383("");
      if (this.shortBuffer != null) {
         MemoryUtil.memFree(this.shortBuffer);
         this.shortBuffer = null;
      }

      this.text = null;
   }
}

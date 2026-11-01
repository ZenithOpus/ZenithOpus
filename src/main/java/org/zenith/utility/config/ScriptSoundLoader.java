package org.zenith.utility.config;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFormat.Encoding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class ScriptSoundLoader {
   public static final long CONST_longValue = 8388608L;
   public static final int CONST_intValue = 10584000;
   public static final int CONST_intValue2 = 24;
   private static final AtomicInteger atomicInteger = new AtomicInteger();
   public static final Map<Integer, ScriptSoundLoader.Entry> items = new ConcurrentHashMap<>();

   private ScriptSoundLoader() {
   }

   public static ScriptSoundLoader.Data method735(Path var0) throws IOException {
      long var1 = Files.size(var0);
      if (var1 > 8388608L) {
         throw new IOException("звук больше 8 МБ");
      } else {
         String var3 = var0.getFileName().toString().toLowerCase(Locale.ROOT);
         if (var3.endsWith(".ogg")) {
            return method736(Files.readAllBytes(var0));
         } else if (var3.endsWith(".wav")) {
            return method737(var0);
         } else {
            throw new IOException("поддерживаются .ogg и .wav");
         }
      }
   }

   private static ScriptSoundLoader.Data method736(byte[] var0) throws IOException {
      ByteBuffer var1 = MemoryUtil.memAlloc(var0.length);

      ScriptSoundLoader.Data var41;
      try {
         MemoryStack var2 = MemoryStack.stackPush();

         try {
            var1.put(var0).flip();
            IntBuffer var3 = var2.mallocInt(1);
            IntBuffer var4 = var2.mallocInt(1);
            IntBuffer var5 = var2.mallocInt(1);
            long var6 = STBVorbis.stb_vorbis_open_memory(var1, var5, null);
            if (var6 == 0L) {
               throw new IOException("не удалось разобрать ogg");
            }

            try {
               STBVorbisInfo var8 = STBVorbisInfo.malloc(var2);
               STBVorbis.stb_vorbis_get_info(var6, var8);
               int var9 = Math.max(1, Math.min(2, var8.channels()));
               int var10 = Math.max(8000, Math.min(192000, var8.sample_rate()));
               int var11 = 10584000 - 10584000 % var9;
               int var12 = STBVorbis.stb_vorbis_stream_length_in_samples(var6);
               short[] var13 = new short[var12 > 0 ? (int)Math.min((long)var12 * var9, (long)var11) : var11];
               ShortBuffer var14 = MemoryUtil.memAllocShort(4096 * var9);

               try {
                  int var15 = 0;

                  while (var15 < var11) {
                     var14.clear();
                     int var16 = STBVorbis.stb_vorbis_get_samples_short_interleaved(var6, var9, var14);
                     if (var16 <= 0) {
                        break;
                     }

                     int var17 = Math.min(var16 * var9, var11 - var15);
                     if (var15 + var17 > var13.length) {
                        var13 = Arrays.copyOf(var13, (int)Math.min((long)var11, Math.max(var13.length * 2L, (long)(var15 + var17))));
                     }

                     var14.get(var13, var15, var17);
                     var15 += var17;
                  }

                  if (var15 == 0) {
                     throw new IOException("пустой ogg");
                  }

                  var41 = new ScriptSoundLoader.Data(var15 == var13.length ? var13 : Arrays.copyOf(var13, var15), var9, var10);
               } finally {
                  MemoryUtil.memFree(var14);
               }
            } finally {
               STBVorbis.stb_vorbis_close(var6);
            }
         } catch (Throwable var39) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var36) {
                  var39.addSuppressed(var36);
               }
            }

            throw var39;
         }

         if (var2 != null) {
            var2.close();
         }
      } finally {
         MemoryUtil.memFree(var1);
      }

      return var41;
   }

   private static ScriptSoundLoader.Data method737(Path var0) throws IOException {
      try {
         ScriptSoundLoader.Data var17;
         try (
            BufferedInputStream var1 = new BufferedInputStream(Files.newInputStream(var0));
            AudioInputStream var2 = AudioSystem.getAudioInputStream(var1);
         ) {
            AudioFormat var3 = var2.getFormat();
            int var4 = Math.max(1, Math.min(2, var3.getChannels()));
            AudioFormat var5 = new AudioFormat(Encoding.PCM_SIGNED, var3.getSampleRate(), 16, var4, var4 * 2, var3.getSampleRate(), false);

            try (AudioInputStream var6 = AudioSystem.getAudioInputStream(var5, var2)) {
               byte[] var7 = var6.readNBytes(21168000);
               short[] var8 = new short[var7.length / 2];

               for (int var9 = 0; var9 < var8.length; var9++) {
                  var8[var9] = (short)(var7[var9 * 2] & 255 | var7[var9 * 2 + 1] << 8);
               }

               var17 = new ScriptSoundLoader.Data(var8, var4, (int)var3.getSampleRate());
            }
         }

         return var17;
      } catch (UnsupportedAudioFileException var16) {
         throw new IOException("неподдерживаемый wav");
      }
   }

   public static ScriptSoundLoader.Entry method738(ScriptSoundLoader.Data var0, float var1, float var2, boolean var3) {
      if (items.size() >= 24) {
         throw new IllegalStateException("одновременно играет больше 24 звуков");
      } else {
         ScriptSoundLoader.Entry var4 = new ScriptSoundLoader.Entry(atomicInteger.incrementAndGet(), var0, var1, var2);
         var4.flag = var3;
         items.put(var4.intValue, var4);
         Thread.ofPlatform().daemon(true).name("zenith-script-sound").start(() -> method742(var4));
         return var4;
      }
   }

   public static void method739() {
      for (ScriptSoundLoader.Entry var1 : items.values()) {
         var1.stopped = true;
      }
   }

   private static float method740() {
      try {
         return MinecraftClient.getInstance().options.getSoundVolume(SoundCategory.MASTER);
      } catch (RuntimeException var1) {
         return 1.0F;
      }
   }

   private static float[] method741(ScriptSoundLoader.Entry var0) {
      float var1 = var0.floatValue * method740();
      if (Double.isNaN(var0.doubleValue)) {
         return new float[]{var1, var1};
      } else {
         MinecraftClient var2 = MinecraftClient.getInstance();
         if (var2.player == null) {
            return new float[]{0.0F, 0.0F};
         } else {
            double var3 = var0.doubleValue - var2.player.getX();
            double var5 = var0.doubleValue2 - var2.player.getEyeY();
            double var7 = var0.doubleValue3 - var2.player.getZ();
            double var9 = Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
            float var11 = (float)Math.max(0.0, 1.0 - var9 / Math.max(1.0, var0.doubleValue4));
            double var12 = Math.toDegrees(Math.atan2(var7, var3)) - 90.0 - var2.player.getYaw();
            double var14 = var9 < 0.5 ? 0.0 : -Math.sin(Math.toRadians(var12));
            float var16 = (float)Math.min(1.0, 1.0 - var14) * 0.5F + 0.5F * (float)(1.0 - Math.abs(var14));
            float var17 = (float)Math.min(1.0, 1.0 + var14) * 0.5F + 0.5F * (float)(1.0 - Math.abs(var14));
            return new float[]{var1 * var11 * var16, var1 * var11 * var17};
         }
      }
   }

   private static void method742(ScriptSoundLoader.Entry var0) {
      ScriptSoundLoader.Data var1 = var0.data;
      int var2 = Math.max(4000, Math.min(192000, Math.round(var1.getIntValue2() * Math.max(0.25F, Math.min(4.0F, var0.floatValue2)))));
      AudioFormat var3 = new AudioFormat(var2, 16, 2, true, false);

      try (SourceDataLine var4 = AudioSystem.getSourceDataLine(var3)) {
         var4.open(var3, 8192);
         var4.start();
         int var5 = var1.getShortValues().length / var1.getIntValue();
         short var6 = 1024;
         byte[] var7 = new byte[var6 * 4];

         for (int var8 = 0; !var0.stopped; var0.doubleValue5 = (double)var8 / var5) {
            if (var8 >= var5) {
               if (!var0.flag) {
                  break;
               }

               var8 = 0;
            }

            float[] var9 = method741(var0);
            int var10 = Math.min(var6, var5 - var8);

            for (int var11 = 0; var11 < var10; var11++) {
               int var12 = (var8 + var11) * var1.getIntValue();
               short var13 = var1.getShortValues()[var12];
               short var14 = var1.getIntValue() > 1 ? var1.getShortValues()[var12 + 1] : var13;
               int var15 = Math.max(32768, Math.min(32767, Math.round(var13 * var9[0])));
               int var16 = Math.max(32768, Math.min(32767, Math.round(var14 * var9[1])));
               var7[var11 * 4] = (byte)var15;
               var7[var11 * 4 + 1] = (byte)(var15 >> 8);
               var7[var11 * 4 + 2] = (byte)var16;
               var7[var11 * 4 + 3] = (byte)(var16 >> 8);
            }

            var4.write(var7, 0, var10 * 4);
            var8 += var10;
         }

         if (!var0.stopped) {
            var4.drain();
         }

         var4.stop();
      } catch (Exception var24) {
      } finally {
         items.remove(var0.intValue);
      }
   }

   public static final class Data {
      private final short[] shortValues;
      private final int intValue;
      private final int intValue2;

      public short[] shortValues() {
         return this.shortValues;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.shortValues, other.shortValues) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(shortValues, intValue, intValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "shortValues=" + this.shortValues + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data(short[] var1, int var2, int var3) {
         this.shortValues = var1;
         this.intValue = var2;
         this.intValue2 = var3;
      }

      public double method743() {
         return (double)this.shortValues.length / this.intValue / this.intValue2;
      }

      public short[] getShortValues() {
         return this.shortValues;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }

   public static final class Entry {
      public final int intValue;
      public final ScriptSoundLoader.Data data;
      public volatile float floatValue;
      public volatile float floatValue2;
      public volatile boolean flag;
      public volatile boolean stopped;
      public volatile double doubleValue = Double.NaN;
      public volatile double doubleValue2;
      public volatile double doubleValue3;
      public volatile double doubleValue4 = 32.0;
      public volatile double doubleValue5;

      public Entry(int var1, ScriptSoundLoader.Data var2, float var3, float var4) {
         this.intValue = var1;
         this.data = var2;
         this.floatValue = var3;
         this.floatValue2 = var4;
      }
   }
}

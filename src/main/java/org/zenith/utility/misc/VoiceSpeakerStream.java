package org.zenith.utility.misc;

import io.github.jaredmdobson.concentus.OpusDecoder;
import io.github.jaredmdobson.concentus.OpusException;
import java.util.ArrayDeque;

public final class VoiceSpeakerStream {
   public final long longValue;
   public final VoiceFrameBuffer voiceFrameBuffer = new VoiceFrameBuffer();
   public volatile int intValue;
   public float floatValue;
   public volatile int intValue2 = 48;
   public volatile long longValue2;
   public volatile long longValue3;
   public volatile float floatValue2 = -100.0F;
   private OpusDecoder opusDecoder;
   public final short[] shortValues = new short[960];
   public int intValue3;
   public final ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
   public int[] intValues = new int[0];
   public boolean flag;

   public VoiceSpeakerStream(long var1) {
      this.longValue = var1;
   }

   public void method1549(int var1, byte[] var2, boolean var3, int var4, int var5, long var6) {
      this.intValue = var4;
      this.intValue2 = var5;
      this.longValue2 = var6;
      this.voiceFrameBuffer.method8027(var1, var2, var3, var6);
   }

   public void method1550(int var1, float var2) {
      if (var2 <= 0.01F) {
         if (var1 > 0) {
            this.floatValue = this.shortValues[var1 - 1];
         }
      } else {
         float var3 = 1.0F - var2 * 0.9F;
         float var4 = 1.0F - var2 * 0.35F;
         float var5 = this.floatValue;

         for (int var6 = 0; var6 < var1; var6++) {
            var5 += var3 * (this.shortValues[var6] - var5);
            this.shortValues[var6] = (short)Math.max(32768, Math.min(32767, Math.round(var5 * var4)));
         }

         this.floatValue = var5;
      }
   }

   public boolean method1551(long var1) {
      return var1 - this.longValue3 < 250L;
   }

   public int method1552(VoiceFrameBuffer.Data var1) {
      try {
         OpusDecoder var2 = this.method1554();

         return switch (var1.getType2()) {
            case NORMAL -> var2.decode(var1.getByteValues(), 0, var1.getByteValues().length, this.shortValues, 0, 960, false);
            case FEC -> var2.decode(var1.getByteValues(), 0, var1.getByteValues().length, this.shortValues, 0, 960, true);
            case PLC -> var2.decode(null, 0, 0, this.shortValues, 0, 960, false);
            default -> 0;
         };
      } catch (RuntimeException | OpusException var3) {
         this.method1553();
         return 0;
      }
   }

   public void method1553() {
      if (this.opusDecoder != null) {
         this.opusDecoder.resetState();
      }
   }

   private OpusDecoder method1554() throws OpusException {
      if (this.opusDecoder == null) {
         this.opusDecoder = new OpusDecoder(48000, 1);
      }

      return this.opusDecoder;
   }
}

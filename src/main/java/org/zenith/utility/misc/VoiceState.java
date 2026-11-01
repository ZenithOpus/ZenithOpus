package org.zenith.utility.misc;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.zenith.utility.network.CloudPackets;

public final class VoiceState {
   public static final int CONST_intValue = 1;
   public static final int CONST_intValue2 = 2;
   public static final int CONST_intValue3 = 4;
   public static final int CONST_intValue4 = 8;
   public static final int CONST_intValue5 = 16;
   public static final int CONST_intValue6 = 29;
   private final VoiceState.Handler handler;
   private final ConcurrentHashMap<Long, VoiceSpeakerStream> concurrentHashMap = new ConcurrentHashMap<>();
   private volatile VoiceState.Data data = VoiceState.Data.INSTANCE;
   private volatile Map<Long, Float> items = Map.of();
   private volatile Map<Long, VoiceState.Data2> items2 = Map.of();
   private volatile boolean flag = true;
   private volatile boolean flag2;
   private volatile int intValue;
   private volatile long longValue;
   private volatile boolean flag3;
   private volatile boolean flag4;
   private volatile boolean flag5;
   private volatile float floatValue = 1.0F;
   private volatile boolean flag6;
   private volatile float floatValue2 = -100.0F;
   private volatile String text = "";
   private volatile String text2 = "";
   private volatile String text3 = "";
   private volatile String text4 = "";
   private VoiceCaptureThread voiceCaptureThread;
   private VoicePlaybackThread voicePlaybackThread;
   private volatile boolean flag7;

   public VoiceState(VoiceState.Handler var1) {
      this.handler = var1;
   }

   public synchronized void method5359(VoiceState.Data var1) {
      if (this.voiceCaptureThread == null) {
         this.data = var1;
         this.text = "";
         this.text2 = "";
         this.voiceCaptureThread = new VoiceCaptureThread(this);
         this.voicePlaybackThread = new VoicePlaybackThread(this);
         this.flag7 = true;
         this.voicePlaybackThread.start();
         this.voiceCaptureThread.start();
      }
   }

   public synchronized void stop() {
      if (this.voiceCaptureThread != null) {
         this.flag7 = false;
         this.voiceCaptureThread.stop();
         this.voicePlaybackThread.stop();
         this.voiceCaptureThread = null;
         this.voicePlaybackThread = null;
         this.concurrentHashMap.clear();
         this.flag6 = false;
         this.flag5 = false;
         this.floatValue2 = -100.0F;
      }
   }

   public synchronized boolean method5360() {
      return this.voiceCaptureThread != null;
   }

   public void setData(VoiceState.Data var1) {
      this.data = var1;
   }

   public VoiceState.Data getData() {
      return this.data;
   }

   public void method5361(boolean var1, int var2) {
      if ((this.flag2 || this.intValue != 0) && !var1 && var2 == 0) {
         this.longValue = System.currentTimeMillis();
      }

      this.flag2 = var1;
      this.intValue = var2 & 29;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public void setFlag3(boolean var1) {
      this.flag3 = var1;
   }

   public boolean isFlag3() {
      return this.flag3;
   }

   public void method5362(boolean var1) {
      this.flag4 = var1;
      if (var1) {
         for (VoiceSpeakerStream var3 : this.concurrentHashMap.values()) {
            var3.voiceFrameBuffer.reset();
         }
      }
   }

   public boolean isFlag4() {
      return this.flag4;
   }

   public void setFlag(boolean var1) {
      this.flag = var1;
   }

   public void setItems(Map<Long, Float> var1) {
      this.items = var1;
   }

   public void setItems2(Map<Long, VoiceState.Data2> var1) {
      this.items2 = var1;
   }

   public void method5363(boolean var1) {
      this.flag5 = var1;
      if (!var1) {
         VoiceSpeakerStream var2 = this.concurrentHashMap.get(-1L);
         if (var2 != null) {
            var2.voiceFrameBuffer.reset();
         }
      }
   }

   public boolean isFlag5() {
      return this.flag5;
   }

   public void setFloatValue(float var1) {
      this.floatValue = var1;
   }

   public boolean isFlag6() {
      return this.flag6;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public String getText() {
      return this.text;
   }

   public String getText2() {
      return this.text2;
   }

   public String getText3() {
      return this.text3;
   }

   public String getText4() {
      return this.text4;
   }

   public Set<Long> method5364() {
      long var1 = System.currentTimeMillis();
      HashSet<Long> var3 = new HashSet<>();
      this.concurrentHashMap.forEach((var3x, var4) -> {
         if (var4.voiceFrameBuffer.active() || var1 - var4.longValue2 < 1000L) {
            var3.add(var3x);
         }
      });
      return var3;
   }

   public boolean method5365(long var1) {
      VoiceSpeakerStream var3 = this.concurrentHashMap.get(var1);
      return var3 != null && var3.method1551(System.currentTimeMillis());
   }

   public float method5366(long var1) {
      VoiceSpeakerStream var3 = this.concurrentHashMap.get(var1);
      return var3 != null && var3.method1551(System.currentTimeMillis()) ? var3.floatValue2 : -100.0F;
   }

   public boolean method5367(long var1) {
      VoiceSpeakerStream var3 = this.concurrentHashMap.get(var1);
      return var3 != null && (var3.intValue & 1) != 0;
   }

   public int method5368(long var1) {
      VoiceSpeakerStream var3 = this.concurrentHashMap.get(var1);
      return var3 == null ? 0 : var3.intValue;
   }

   public boolean method5369(long var1) {
      VoiceSpeakerStream var3 = this.concurrentHashMap.get(var1);
      return var3 != null && (var3.intValue & 29) != 0;
   }

   public int method5370(long var1) {
      VoiceSpeakerStream var3 = this.concurrentHashMap.get(var1);
      return var3 == null ? this.data.getIntValue2() : var3.intValue2;
   }

   public void method5371(CloudPackets.Data16 var1) {
      if (this.flag7 && !this.flag4) {
         if (this.items.containsKey(var1.uid())) {
            if (!var1.method1823() || this.flag) {
               this.method5372(
                  var1.uid(),
                  var1.getIntValue3(),
                  var1.getByteValues(),
                  var1.method1824(),
                  var1.getIntValue() & 29,
                  Math.max(8, Math.min(128, var1.getIntValue2()))
               );
            }
         }
      }
   }

   private void method5372(long var1, int var3, byte[] var4, boolean var5, int var6, int var7) {
      VoiceSpeakerStream var8 = this.concurrentHashMap.get(var1);
      if (var8 == null) {
         if (var5 || this.concurrentHashMap.size() >= 16) {
            return;
         }

         var8 = this.concurrentHashMap.computeIfAbsent(var1, VoiceSpeakerStream::new);
      }

      var8.method1549(var3, var4, var5, var6, var7, System.currentTimeMillis());
   }

   public Collection<VoiceSpeakerStream> method5373() {
      return this.concurrentHashMap.values();
   }

   public void method5374(long var1, long var3) {
      this.concurrentHashMap.values().removeIf(var4 -> var4.intValue3 == 0 && !var4.voiceFrameBuffer.active() && var1 - var4.longValue2 > var3);
   }

   public void method5375(long var1) {
      for (VoiceSpeakerStream var4 : this.concurrentHashMap.values()) {
         if (var4.voiceFrameBuffer.active()) {
            var4.voiceFrameBuffer.reset();
         }
      }
   }

   public VoiceState.Data2 method5376(VoiceSpeakerStream var1) {
      if (var1.longValue == -1L) {
         return new VoiceState.Data2(0.0F, 0.0F, 0.0F, this.floatValue);
      } else if (this.flag4) {
         return VoiceState.Data2.INSTANCE;
      } else {
         VoiceState.Data2 var2 = this.items2.get(var1.longValue);
         return var2 == null ? VoiceState.Data2.INSTANCE : var2;
      }
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public boolean method5377() {
      return this.intValue != 0;
   }

   public boolean method5378(long var1, long var3) {
      if (this.flag3) {
         return false;
      } else if (this.flag5) {
         return true;
      } else if (!this.handler.method377()) {
         return false;
      } else {
         return !this.data.isFlag() ? true : this.flag2 || this.intValue != 0 || var1 - this.longValue < var3;
      }
   }

   public boolean method5379() {
      return !this.flag3 && this.handler.method377();
   }

   public void method5380(int var1, int var2, int var3, byte[] var4, int var5) {
      if (this.handler.method377()) {
         this.handler.method378(var1, var2, var3, var4, var5);
      }
   }

   public void method5381(int var1, byte[] var2, int var3) {
      if (this.flag5) {
         byte[] var4 = Arrays.copyOf(var2, var3);
         this.method5372(-1L, var1, var4, false, 1, 8);
      }
   }

   public void setFlag6(boolean var1) {
      this.flag6 = var1;
   }

   public void setFloatValue2(float var1) {
      this.floatValue2 = var1;
   }

   public void setText(String var1) {
      this.text = var1;
   }

   public void setText2(String var1) {
      this.text2 = var1;
   }

   public void method5382(String var1) {
      this.text3 = var1 == null ? "" : var1;
   }

   public void method5383(String var1) {
      this.text4 = var1 == null ? "" : var1;
   }

   public static final class Data {
      private final boolean flag;
      private final float floatValue;
      private final float floatValue2;
      private final int intValue;
      private final int intValue2;
      private final boolean flag2;
      private final String text;
      private final String text2;
      private final boolean flag3;
      private final boolean flag4;
      private final int intValue3;

      public boolean flag() {
         return this.flag;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      public boolean flag4() {
         return this.flag4;
      }

      public int intValue3() {
         return this.intValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.flag3, other.flag3) && java.util.Objects.equals(this.flag4, other.flag4) && java.util.Objects.equals(this.intValue3, other.intValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, floatValue, floatValue2, intValue, intValue2, flag2, text, text2, flag3, flag4, intValue3);
      }

      @Override
      public String toString() {
         return "Data[" + "flag=" + this.flag + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "flag2=" + this.flag2 + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "flag3=" + this.flag3 + ", " + "flag4=" + this.flag4 + ", " + "intValue3=" + this.intValue3 + "]";
      }
      public static final VoiceState.Data INSTANCE = new VoiceState.Data(true, -45.0F, 1.0F, 32000, 48, false, "", "", true, true, 0);

      public Data(boolean var1, float var2, float var3, int var4, int var5, boolean var6, String var7, String var8, boolean var9, boolean var10, int var11) {
         this.flag = var1;
         this.floatValue = var2;
         this.floatValue2 = var3;
         this.intValue = var4;
         this.intValue2 = var5;
         this.flag2 = var6;
         this.text = var7;
         this.text2 = var8;
         this.flag3 = var9;
         this.flag4 = var10;
         this.intValue3 = var11;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public boolean isFlag4() {
         return this.flag4;
      }

      public int getIntValue3() {
         return this.intValue3;
      }
   }

   public static final class Data2 {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5);
      }

      @Override
      public String toString() {
         return "Data2[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + "]";
      }
      public static final VoiceState.Data2 INSTANCE = new VoiceState.Data2(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

      public Data2(float var1, float var2, float var3, float var4) {
         this(var1, var2, var3, var4, 0.0F);
      }

      public Data2(float var1, float var2, float var3, float var4, float var5) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }
   }

   public interface Handler {
      boolean method377();

      void method378(int var1, int var2, int var3, byte[] var4, int var5);
   }
}

package org.zenith.utility.misc;

public final class VoiceFrameBuffer {
   private static final int CONST_intValue = 64;
   private static final int CONST_intValue2 = 2;
   private static final int CONST_intValue3 = 12;
   private static final int CONST_intValue4 = 4;
   private static final int CONST_intValue5 = 5;
   private static final int CONST_intValue6 = 8;
   private static final long CONST_longValue = 8000L;
   private static final long CONST_longValue2 = 1000L;
   private static final long CONST_longValue3 = 400L;
   private final byte[][] byteValues = new byte[64][];
   private final int[] intValues = new int[64];
   private VoiceFrameBuffer.Type type = VoiceFrameBuffer.Type.IDLE;
   private int next;
   private int intValue;
   private int intValue2 = -1;
   private int intValue3;
   private long longValue;
   private long longValue2;
   private long longValue3;
   private int intValue4;
   private double doubleValue = 10.0;
   private int intValue5;
   private long longValue4;

   public static int method8026(int var0, int var1) {
      return (short)(var0 - var1);
   }

   public synchronized void method8027(int var1, byte[] var2, boolean var3, long var4) {
      var1 &= 65535;
      this.longValue2 = var4;
      if (this.type == VoiceFrameBuffer.Type.IDLE) {
         this.clear();
         this.type = VoiceFrameBuffer.Type.BUFFERING;
         this.longValue = var4;
         this.next = var1;
         this.intValue = var1;
         this.intValue2 = -1;
         this.intValue3 = 0;
         this.longValue3 = var4;
         this.intValue4 = var1;
      } else {
         int var6 = method8026(var1, this.intValue4);
         if (var6 > 0) {
            double var7 = var4 - this.longValue3 - var6 * 20.0;
            this.doubleValue = this.doubleValue + (Math.abs(var7) - this.doubleValue) / 16.0;
            this.longValue3 = var4;
            this.intValue4 = var1;
         }

         int var11 = method8026(var1, this.next);
         if (var11 < 0) {
            return;
         }

         if (var11 >= 64) {
            this.clear();
            this.type = VoiceFrameBuffer.Type.BUFFERING;
            this.longValue = var4;
            this.next = var1;
            this.intValue = var1;
            this.intValue2 = -1;
         }
      }

      if (var3) {
         this.intValue2 = var1;
      }

      if (var2 != null && var2.length > 0) {
         int var10 = var1 & 63;
         this.byteValues[var10] = var2;
         this.intValues[var10] = var1;
      }

      if (method8026(var1, this.intValue) > 0) {
         this.intValue = var1;
      }
   }

   public synchronized VoiceFrameBuffer.Data method8028(long var1) {
      switch (this.type) {
         case IDLE:
            return VoiceFrameBuffer.Data.INSTANCE;
         case BUFFERING:
            int var3 = this.method8033();
            boolean var4 = var3 >= this.method8029() || this.intValue2 != -1 || var3 > 0 && var1 - this.longValue > 400L;
            if (!var4) {
               if (var1 - this.longValue2 > 1000L) {
                  this.type = VoiceFrameBuffer.Type.IDLE;
               }

               return VoiceFrameBuffer.Data.INSTANCE;
            } else {
               if (var3 > 0) {
                  this.next = this.method8034();
               }

               this.type = VoiceFrameBuffer.Type.PLAYING;
               this.intValue3 = 0;
            }
         default:
            this.method8030(var1);
            int var7 = method8026(this.intValue, this.next) + 1;
            int var8 = this.method8029();
            if (var7 > var8 + 4) {
               int var5 = var7 - var8;

               for (int var6 = 0; var6 < var5; var6++) {
                  this.method8031(this.next);
                  this.next = this.next + 1 & 65535;
               }
            }

            byte[] var9 = this.method8031(this.next);
            if (var9 != null) {
               this.next = this.next + 1 & 65535;
               this.intValue3 = 0;
               return new VoiceFrameBuffer.Data(VoiceFrameBuffer.Type2.NORMAL, var9);
            } else if (this.intValue2 == -1 || method8026(this.next, this.intValue2) < 0) {
               if (var1 - this.longValue2 > 1000L) {
                  this.type = VoiceFrameBuffer.Type.IDLE;
                  return VoiceFrameBuffer.Data.INSTANCE3;
               } else if (method8026(this.intValue, this.next) > 0) {
                  byte[] var11 = this.method8032(this.next + 1 & 65535);
                  this.next = this.next + 1 & 65535;
                  return var11 != null ? new VoiceFrameBuffer.Data(VoiceFrameBuffer.Type2.FEC, var11) : VoiceFrameBuffer.Data.INSTANCE2;
               } else {
                  if (this.intValue3 == 0) {
                     this.intValue5 = Math.min(8, this.intValue5 + 1);
                     this.longValue4 = var1;
                  }

                  if (++this.intValue3 > 5) {
                     this.type = VoiceFrameBuffer.Type.BUFFERING;
                     this.longValue = var1;
                     return VoiceFrameBuffer.Data.INSTANCE;
                  } else {
                     return VoiceFrameBuffer.Data.INSTANCE2;
                  }
               }
            } else if (this.method8033() > 0) {
               this.intValue2 = -1;
               this.next = this.method8034();
               byte[] var10 = this.method8031(this.next);
               this.next = this.next + 1 & 65535;
               return var10 == null ? VoiceFrameBuffer.Data.INSTANCE2 : new VoiceFrameBuffer.Data(VoiceFrameBuffer.Type2.NORMAL, var10);
            } else {
               this.type = VoiceFrameBuffer.Type.IDLE;
               return VoiceFrameBuffer.Data.INSTANCE3;
            }
      }
   }

   public synchronized boolean active() {
      return this.type != VoiceFrameBuffer.Type.IDLE;
   }

   public synchronized void reset() {
      this.clear();
      this.type = VoiceFrameBuffer.Type.IDLE;
      this.intValue2 = -1;
   }

   private int method8029() {
      int var1 = (int)Math.ceil((this.doubleValue * 2.0 + 20.0) / 20.0);
      return Math.max(2, Math.min(12, var1 + this.intValue5));
   }

   private void method8030(long var1) {
      if (this.intValue5 > 0 && var1 - this.longValue4 > 8000L) {
         this.intValue5--;
         this.longValue4 = var1;
      }
   }

   private byte[] method8031(int var1) {
      int var2 = var1 & 63;
      byte[] var3 = this.byteValues[var2];
      if (var3 != null && this.intValues[var2] == var1) {
         this.byteValues[var2] = null;
         return var3;
      } else {
         return null;
      }
   }

   private byte[] method8032(int var1) {
      int var2 = var1 & 63;
      byte[] var3 = this.byteValues[var2];
      return var3 != null && this.intValues[var2] == var1 ? var3 : null;
   }

   private int method8033() {
      int var1 = 0;

      for (int var2 = 0; var2 < 64; var2++) {
         if (this.byteValues[var2] != null && method8026(this.intValues[var2], this.next) >= 0) {
            var1++;
         }
      }

      return var1;
   }

   private int method8034() {
      int var1 = this.next;
      int var2 = Integer.MAX_VALUE;

      for (int var3 = 0; var3 < 64; var3++) {
         if (this.byteValues[var3] != null) {
            int var4 = method8026(this.intValues[var3], this.next);
            if (var4 >= 0 && var4 < var2) {
               var2 = var4;
               var1 = this.intValues[var3];
            }
         }
      }

      return var1;
   }

   private void clear() {
      for (int var1 = 0; var1 < 64; var1++) {
         this.byteValues[var1] = null;
      }
   }

   public static final class Data {
      private final VoiceFrameBuffer.Type2 type2;
      private final byte[] byteValues;

      public VoiceFrameBuffer.Type2 type2() {
         return this.type2;
      }

      public byte[] byteValues() {
         return this.byteValues;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.type2, other.type2) && java.util.Objects.equals(this.byteValues, other.byteValues);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(type2, byteValues);
      }

      @Override
      public String toString() {
         return "Data[" + "type2=" + this.type2 + ", " + "byteValues=" + this.byteValues + "]";
      }
      public static final VoiceFrameBuffer.Data INSTANCE = new VoiceFrameBuffer.Data(VoiceFrameBuffer.Type2.NONE, null);
      public static final VoiceFrameBuffer.Data INSTANCE2 = new VoiceFrameBuffer.Data(VoiceFrameBuffer.Type2.PLC, null);
      public static final VoiceFrameBuffer.Data INSTANCE3 = new VoiceFrameBuffer.Data(VoiceFrameBuffer.Type2.END, null);

      public Data(VoiceFrameBuffer.Type2 var1, byte[] var2) {
         this.type2 = var1;
         this.byteValues = var2;
      }

      public VoiceFrameBuffer.Type2 getType2() {
         return this.type2;
      }

      public byte[] getByteValues() {
         return this.byteValues;
      }
   }

   private static enum Type {
      IDLE,
      BUFFERING,
      PLAYING;
   }

   public static enum Type2 {
      NONE,
      NORMAL,
      FEC,
      PLC,
      END;
   }
}

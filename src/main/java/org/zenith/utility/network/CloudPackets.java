package org.zenith.utility.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.PacketByteBuf;
import org.zenith.utility.misc.CosmeticType;

public final class CloudPackets {
   public static final int CONST_intValue = 5;
   public static final int CONST_intValue2 = 1;
   public static final int CONST_intValue3 = 2;
   public static final int CONST_intValue4 = 3;
   public static final int CONST_intValue5 = 4;
   public static final int CONST_intValue6 = 5;
   public static final int CONST_intValue7 = 6;
   public static final int CONST_intValue8 = 16;
   public static final int CONST_intValue9 = 17;
   public static final int CONST_intValue10 = 18;
   public static final int CONST_intValue11 = 19;
   public static final int CONST_intValue12 = 20;
   public static final int CONST_intValue13 = 32;
   public static final int CONST_intValue14 = 33;
   public static final int CONST_intValue15 = 34;
   public static final int CONST_intValue16 = 35;
   public static final int CONST_intValue17 = 36;
   public static final int CONST_intValue18 = 128;
   public static final int CONST_intValue19 = 129;
   public static final int CONST_intValue20 = 130;
   public static final int CONST_intValue21 = 131;
   public static final int CONST_intValue22 = 132;
   public static final int CONST_intValue23 = 133;
   public static final int CONST_intValue24 = 134;
   public static final int CONST_intValue25 = 135;
   public static final int CONST_intValue26 = 136;
   public static final int CONST_intValue27 = 128;
   public static final int CONST_intValue28 = 0;
   public static final int CONST_intValue29 = 1;
   public static final int CONST_intValue30 = 2;
   public static final int CONST_intValue31 = 0;
   public static final int CONST_intValue32 = 1;
   public static final int CONST_intValue33 = 0;
   public static final int CONST_intValue34 = 1;
   public static final int CONST_intValue35 = 0;
   public static final int CONST_intValue36 = 1;
   public static final int CONST_intValue37 = 1;
   public static final int CONST_intValue38 = 2;
   public static final int CONST_intValue39 = 3;
   public static final int CONST_intValue40 = 4;
   public static final int CONST_intValue41 = 5;
   public static final int CONST_intValue42 = 6;
   public static final int CONST_intValue43 = 32;
   public static final int CONST_intValue44 = 16;
   public static final int CONST_intValue45 = 200;
   public static final int CONST_intValue46 = 64;
   public static final int CONST_intValue47 = 96;
   public static final int CONST_intValue48 = 40;
   public static final int CONST_intValue49 = 12;
   public static final int CONST_intValue50 = 63;
   public static final int CONST_intValue51 = 36;
   public static final int CONST_intValue52 = 256;
   public static final int CONST_intValue53 = 32;
   public static final int CONST_intValue54 = 32;
   public static final int CONST_intValue55 = 48;
   public static final int CONST_intValue56 = 32;
   public static final int CONST_intValue57 = 2048;
   public static final int CONST_intValue58 = 1;
   public static final int CONST_intValue59 = 2;
   public static final int CONST_intValue60 = 4;
   public static final int CONST_intValue61 = 37;
   public static final int CONST_intValue62 = 8192;
   public static final int CONST_intValue63 = 4;
   public static final int CONST_intValue64 = 8;
   public static final int CONST_intValue65 = 16;
   public static final int CONST_intValue66 = 1;
   public static final int CONST_intValue67 = 2;
   public static final int CONST_intValue68 = 3;
   public static final int CONST_intValue69 = 4;
   public static final int CONST_intValue70 = 5;
   public static final int CONST_intValue71 = 6;
   public static final int CONST_intValue72 = 7;
   public static final int CONST_intValue73 = 8;
   public static final int CONST_intValue74 = 9;
   public static final int CONST_intValue75 = 10;
   public static final int CONST_intValue76 = 11;
   public static final int CONST_intValue77 = 12;
   public static final int CONST_intValue78 = 13;
   public static final int CONST_intValue79 = 14;
   public static final int CONST_intValue80 = 15;
   public static final int CONST_intValue81 = 16;
   public static final int CONST_intValue82 = 17;
   public static final int CONST_intValue83 = 18;
   public static final int CONST_intValue84 = 19;
   public static final int CONST_intValue85 = 20;
   public static final int CONST_intValue86 = 21;
   public static final int CONST_intValue87 = 22;
   public static final int CONST_intValue88 = 23;
   public static final int CONST_intValue89 = 24;
   public static final int CONST_intValue90 = 40;
   public static final int CONST_intValue91 = 41;
   public static final int CONST_intValue92 = 42;
   public static final int CONST_intValue93 = 43;
   public static final int CONST_intValue94 = 44;
   public static final int CONST_intValue95 = 45;
   public static final int CONST_intValue96 = 46;
   public static final int CONST_intValue97 = 47;
   public static final int CONST_intValue98 = 48;
   public static final int CONST_intValue99 = 49;
   public static final int CONST_intValue100 = 50;
   public static final int CONST_intValue101 = 51;
   public static final int CONST_intValue102 = 1;
   public static final int CONST_intValue103 = 2;
   public static final int CONST_intValue104 = 1;
   public static final int CONST_intValue105 = 2;
   public static final int CONST_intValue106 = 400;

   private CloudPackets() {
   }

   public static ByteBuffer method1783(long var0, String var2, String var3, String var4, String var5, byte[] var6, String var7) {
      boolean var8 = var7 != null && !var7.isEmpty() && var7.length() <= 2048;
      PacketByteBuf var9 = method1820(1, 128 + (var8 ? var7.length() + 3 : 0));
      var9.writeVarInt(5);
      var9.writeVarLong(var0);
      var9.writeString(var2, 32);
      var9.writeString(var3, 16);
      var9.writeString(var4, 36);
      var9.writeString(var5, 32);
      var9.writeBytes(var6, 0, 32);
      if (var8) {
         var9.writeString(var7, 2048);
      }

      return method1821(var9);
   }

   public static ByteBuffer method1784(Collection<String> var0) {
      ArrayList<String> var1 = new ArrayList(Math.min(var0.size(), 200));

      for (String var3 : var0) {
         if (var1.size() >= 200) {
            break;
         }

         if (var3 != null && !var3.isEmpty() && var3.length() <= 32) {
            var1.add(var3);
         }
      }

      int var6 = var1.size();
      PacketByteBuf var7 = method1820(2, 16 + var6 * 20);
      var7.writeVarInt(var6);

      for (Object var5_o : (Iterable<?>)(Object)(var1)) {
         String var5 = (String)var5_o;
         var7.writeString(var5, 32);
      }

      return method1821(var7);
   }

   public static ByteBuffer method1785(int var0, String var1) {
      PacketByteBuf var2 = method1820(3, 32);
      var2.writeByte(var0);
      var2.writeString(var1, 32);
      return method1821(var2);
   }

   public static ByteBuffer method1786(String var0) {
      PacketByteBuf var1 = method1820(4, 24);
      var1.writeString(var0, 32);
      return method1821(var1);
   }

   public static ByteBuffer method1787(String var0) {
      PacketByteBuf var1 = method1820(5, 40);
      var1.writeString(var0, 32);
      return method1821(var1);
   }

   public static ByteBuffer method1788(String var0) {
      PacketByteBuf var1 = method1820(6, 40);
      var1.writeString(var0, 32);
      return method1821(var1);
   }

   public static ByteBuffer method1789(String var0, String var1) {
      PacketByteBuf var2 = method1820(16, 128);
      var2.writeString(method1819(var0, 64), 64);
      var2.writeString(method1819(var1, 64), 64);
      return method1821(var2);
   }

   public static ByteBuffer method1790(double var0, double var2, double var4, float var6, float var7) {
      PacketByteBuf var8 = method1820(17, 40);
      var8.writeDouble(var0);
      var8.writeDouble(var2);
      var8.writeDouble(var4);
      var8.writeFloat(var6);
      var8.writeFloat(var7);
      return method1821(var8);
   }

   public static ByteBuffer method1791(float var0, float var1, float var2, int var3) {
      PacketByteBuf var4 = method1820(18, 16);
      var4.writeFloat(var0);
      var4.writeFloat(var1);
      var4.writeFloat(var2);
      var4.writeByte(var3);
      return method1821(var4);
   }

   public static ByteBuffer method1792(List<CloudPackets.Data7> var0) {
      PacketByteBuf var1 = method1820(19, 16 + var0.size() * 24);
      var1.writeVarInt(var0.size());

      for (CloudPackets.Data7 var3 : var0) {
         var1.writeString(var3.getText().length() > 96 ? "" : var3.getText(), 96);
         var1.writeByte(var3.count());
         var1.writeVarInt(var3.getIntValue2());
      }

      return method1821(var1);
   }

   public static ByteBuffer method1793(String var0, List<CloudPackets.Data9> var1) {
      int var2 = Math.min(var1.size(), 12);
      PacketByteBuf var3 = method1820(20, 16 + var2 * 48);
      var3.writeString(method1819(var0, 16), 16);
      var3.writeVarInt(var2);

      for (int var4 = 0; var4 < var2; var4++) {
         CloudPackets.Data9 var5 = (CloudPackets.Data9)var1.get(var4);
         var3.writeString(var5.getText(), 40);
         var3.writeVarInt(var5.getIntValue());
         var3.writeInt(var5.getIntValue2());
      }

      return method1821(var3);
   }

   public static ByteBuffer method1794(int var0, String var1) {
      PacketByteBuf var2 = method1820(32, 16 + var1.length() * 3);
      var2.writeByte(var0);
      var2.writeString(var1, 256);
      return method1821(var2);
   }

   public static ByteBuffer method1795(long var0) {
      PacketByteBuf var2 = method1820(34, 10);
      var2.writeVarLong(var0);
      return method1821(var2);
   }

   public static ByteBuffer method1796(String var0, String var1, double var2, double var4, double var6, String var8, int var9, String var10) {
      PacketByteBuf var11 = method1820(33, 232);
      var11.writeString(method1819(var0, 64), 64);
      var11.writeString(method1819(var1, 64), 64);
      var11.writeDouble(var2);
      var11.writeDouble(var4);
      var11.writeDouble(var6);
      var11.writeString(var8, 32);
      var11.writeInt(var9);
      var11.writeString(var10, 36);
      return method1821(var11);
   }

   public static ByteBuffer method1797(int var0, int var1, int var2, byte[] var3, int var4) {
      ByteBuffer var5 = ByteBuffer.allocate(5 + var4);
      var5.put((byte)35);
      var5.put((byte)var0);
      var5.put((byte)Math.max(0, Math.min(255, var1)));
      var5.putShort((short)var2);
      var5.put(var3, 0, var4);
      return var5.flip();
   }

   public static ByteBuffer method1798(int var0) {
      return ByteBuffer.wrap(new byte[]{36, (byte)var0});
   }

   public static ByteBuffer method1799(int var0, String var1) {
      PacketByteBuf var2 = method1820(37, 16 + var1.length() * 3);
      var2.writeVarInt(var0);
      var2.writeString(var1, 8192);
      return method1821(var2);
   }

   public static CloudPackets.Data4 method1800(PacketByteBuf var0) {
      return new CloudPackets.Data4(var0.readVarInt(), var0.readString(64), var0.readBoolean());
   }

   public static CloudPackets.Data12 method1801(PacketByteBuf var0) {
      long var1 = var0.readVarLong();
      String var3 = var0.readString(32);
      long var4 = var0.readLong();
      int var6 = var0.readVarInt();
      int var7 = var0.isReadable() ? var0.readVarInt() : 0;
      return new CloudPackets.Data12(var1, var3, var4, var6, var7);
   }

   public static CloudPackets.Data16 method1802(PacketByteBuf var0) {
      long var1 = var0.readVarLong();
      short var3 = var0.readUnsignedByte();
      short var4 = var0.readUnsignedByte();
      int var5 = var0.readUnsignedShort();
      int var6 = var0.readableBytes();
      if (var6 > 400) {
         throw new IllegalStateException("голос: " + var6 + " байт");
      } else {
         byte[] var7 = new byte[var6];
         var0.readBytes(var7);
         return new CloudPackets.Data16(var1, var3, var4, var5, var7);
      }
   }

   public static CloudPackets.Data6 method1803(PacketByteBuf var0) {
      return new CloudPackets.Data6(var0.readVarLong(), var0.readUnsignedByte());
   }

   public static CloudPackets.Data18 method1804(PacketByteBuf var0) {
      int var1 = var0.readVarInt();
      long var2 = var0.readVarLong();
      String var4 = var0.readString(32);
      String var5 = var0.readString(1048576);
      JsonElement var6 = JsonParser.parseString(var5.isEmpty() ? "{}" : var5);
      return new CloudPackets.Data18(var1, var2, var4, var6.isJsonObject() ? var6.getAsJsonObject() : new JsonObject());
   }

   public static CloudPackets.Data method1805(PacketByteBuf var0) {
      return new CloudPackets.Data(var0.readUnsignedByte(), var0.readString(256));
   }

   public static CloudPackets.Data8 method1806(PacketByteBuf var0) {
      return new CloudPackets.Data8(var0.readVarLong(), var0.readString(32), var0.readString(16));
   }

   public static long method1807(PacketByteBuf var0) {
      return var0.readVarLong();
   }

   public static List<CloudPackets.Data14> method1808(PacketByteBuf var0) {
      int var1 = var0.readVarInt();
      if (var1 == 0) {
         return List.of();
      } else if (var1 >= 0 && var1 <= 200) {
         List<Data14> var2 = new ArrayList<>(var1);

         for (int var3 = 0; var3 < var1; var3++) {
            var2.add(new CloudPackets.Data14(var0.readString(32), var0.readUnsignedByte() == 1));
         }

         return var2;
      } else {
         return null;
      }
   }

   public static CloudPackets.Data17 method1809(PacketByteBuf var0) {
      return new CloudPackets.Data17(var0.readString(32), var0.readUnsignedByte());
   }

   public static CloudPackets.Data5 method1810(PacketByteBuf var0) {
      return new CloudPackets.Data5(var0.readVarLong(), var0.readString(64), var0.readString(64));
   }

   public static CloudPackets.Data2 method1811(PacketByteBuf var0) {
      return new CloudPackets.Data2(var0.readVarLong(), var0.readDouble(), var0.readDouble(), var0.readDouble(), var0.readFloat(), var0.readFloat());
   }

   public static CloudPackets.Data3 method1812(PacketByteBuf var0) {
      return new CloudPackets.Data3(var0.readVarLong(), var0.readFloat(), var0.readFloat(), var0.readFloat(), var0.readUnsignedByte());
   }

   public static CloudPackets.Data11 method1813(PacketByteBuf var0) {
      long var1 = var0.readVarLong();
      int var3 = var0.readVarInt();
      if (var3 >= 0 && var3 <= 48) {
         CloudPackets.Data7[] var4 = new CloudPackets.Data7[var3];

         for (int var5 = 0; var5 < var3; var5++) {
            String var6 = var0.readString(96);
            short var7 = var0.readUnsignedByte();
            int var8 = var0.readVarInt();
            var4[var5] = var6.isEmpty() ? CloudPackets.Data7.INSTANCE : new CloudPackets.Data7(var6, var7, var8);
         }

         return new CloudPackets.Data11(var1, var4);
      } else {
         throw new IllegalStateException("инвентарь: " + var3 + " слотов");
      }
   }

   public static CloudPackets.Data15 method1814(PacketByteBuf var0) {
      long var1 = var0.readVarLong();
      String var3 = var0.readString(16);
      int var4 = var0.readVarInt();
      if (var4 >= 0 && var4 <= 12) {
         CloudPackets.Data9[] var5 = new CloudPackets.Data9[var4];

         for (int var6 = 0; var6 < var4; var6++) {
            var5[var6] = new CloudPackets.Data9(var0.readString(40), var0.readVarInt(), var0.readInt());
         }

         return new CloudPackets.Data15(var1, var3, List.of(var5));
      } else {
         throw new IllegalStateException("косметика: " + var4 + " слотов");
      }
   }

   public static CloudPackets.Data13 method1815(PacketByteBuf var0) {
      long var1 = var0.readVarLong();
      String var3 = var0.readString(32);
      short var4 = var0.readUnsignedByte();
      long var5 = var0.readLong();
      String var7 = var0.readString(256);
      long var8 = var0.isReadable() ? var0.readVarLong() : 0L;
      return new CloudPackets.Data13(var1, var3, var4, var5, var7, var8);
   }

   public static long method1816(PacketByteBuf var0) {
      return var0.readVarLong();
   }

   public static CloudPackets.Data10 method1817(PacketByteBuf var0) {
      return new CloudPackets.Data10(
         var0.readVarLong(),
         var0.readString(32),
         var0.readString(64),
         var0.readString(64),
         var0.readDouble(),
         var0.readDouble(),
         var0.readDouble(),
         var0.readString(32),
         var0.readInt(),
         var0.readString(36)
      );
   }

   public static PacketByteBuf method1818(ByteBuffer var0) {
      return new PacketByteBuf(Unpooled.wrappedBuffer(var0));
   }

   private static String method1819(String var0, int var1) {
      if (var0 == null) {
         return "";
      } else {
         return var0.length() > var1 ? var0.substring(0, var1) : var0;
      }
   }

   private static PacketByteBuf method1820(int var0, int var1) {
      PacketByteBuf var2 = new PacketByteBuf(Unpooled.buffer(var1));
      var2.writeByte(var0);
      return var2;
   }

   private static ByteBuffer method1821(PacketByteBuf var0) {
      return var0.nioBuffer();
   }

   public static final class Data {
      private final int intValue;
      private final String text;

      public int intValue() {
         return this.intValue;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, text);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "text=" + this.text + "]";
      }

      public Data(int var1, String var2) {
         this.intValue = var1;
         this.text = var2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public String getText() {
         return this.text;
      }
   }

   public static final class Data10 {
      private final long longValue;
      private final String text;
      private final String text2;
      private final String text3;
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final String text4;
      private final int intValue;
      private final String text5;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public String text4() {
         return this.text4;
      }

      public int intValue() {
         return this.intValue;
      }

      public String text5() {
         return this.text5;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data10 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text5, other.text5);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2, text3, doubleValue, doubleValue2, doubleValue3, text4, intValue, text5);
      }

      @Override
      public String toString() {
         return "Data10[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "text4=" + this.text4 + ", " + "intValue=" + this.intValue + ", " + "text5=" + this.text5 + "]";
      }

      public Data10(long var1, String var3, String var4, String var5, double var6, double var8, double var10, String var12, int var13, String var14) {
         this.longValue = var1;
         this.text = var3;
         this.text2 = var4;
         this.text3 = var5;
         this.doubleValue = var6;
         this.doubleValue2 = var8;
         this.doubleValue3 = var10;
         this.text4 = var12;
         this.intValue = var13;
         this.text5 = var14;
      }

      public long uid() {
         return this.longValue;
      }

      public String username() {
         return this.text;
      }

      public String server() {
         return this.text2;
      }

      public String dimension() {
         return this.text3;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }

      public String label() {
         return this.text4;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public String getText5() {
         return this.text5;
      }
   }

   public static final class Data11 {
      private final long longValue;
      private final CloudPackets.Data7[] data7s;

      public long longValue() {
         return this.longValue;
      }

      public CloudPackets.Data7[] data7s() {
         return this.data7s;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data11 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.data7s, other.data7s);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, data7s);
      }

      @Override
      public String toString() {
         return "Data11[" + "longValue=" + this.longValue + ", " + "data7s=" + this.data7s + "]";
      }

      public Data11(long var1, CloudPackets.Data7[] var3) {
         this.longValue = var1;
         this.data7s = var3;
      }

      public long uid() {
         return this.longValue;
      }

      public CloudPackets.Data7[] getData7s() {
         return this.data7s;
      }
   }

   public static final class Data12 {
      private final long longValue;
      private final String text;
      private final long longValue2;
      private final int intValue;
      private final int intValue2;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public long longValue2() {
         return this.longValue2;
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
         if (!(o instanceof Data12 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, longValue2, intValue, intValue2);
      }

      @Override
      public String toString() {
         return "Data12[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "longValue2=" + this.longValue2 + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data12(long var1, String var3, long var4, int var6, int var7) {
         this.longValue = var1;
         this.text = var3;
         this.longValue2 = var4;
         this.intValue = var6;
         this.intValue2 = var7;
      }

      public long uid() {
         return this.longValue;
      }

      public String username() {
         return this.text;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }

   public static final class Data13 {
      private final long longValue;
      private final String text;
      private final int intValue;
      private final long longValue2;
      private final String text2;
      private final long longValue3;

      public long longValue() {
         return this.longValue;
      }

      public int intValue() {
         return this.intValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public String text2() {
         return this.text2;
      }

      public long longValue3() {
         return this.longValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data13 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.longValue3, other.longValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, intValue, longValue2, text2, longValue3);
      }

      @Override
      public String toString() {
         return "Data13[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "intValue=" + this.intValue + ", " + "longValue2=" + this.longValue2 + ", " + "text2=" + this.text2 + ", " + "longValue3=" + this.longValue3 + "]";
      }

      public Data13(long var1, String var3, int var4, long var5, String var7, long var8) {
         this.longValue = var1;
         this.text = var3;
         this.intValue = var4;
         this.longValue2 = var5;
         this.text2 = var7;
         this.longValue3 = var8;
      }

      public long uid() {
         return this.longValue;
      }

      public String username() {
         return this.text;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public long time() {
         return this.longValue2;
      }

      public String text() {
         return this.text2;
      }

      public long getLongValue3() {
         return this.longValue3;
      }
   }

   public static final class Data14 {
      private final String text;
      private final boolean flag;

      public String text() {
         return this.text;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data14 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, flag);
      }

      @Override
      public String toString() {
         return "Data14[" + "text=" + this.text + ", " + "flag=" + this.flag + "]";
      }

      public Data14(String var1, boolean var2) {
         this.text = var1;
         this.flag = var2;
      }

      public String username() {
         return this.text;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   public static final class Data15 {
      private final long longValue;
      private final String text;
      private final List<CloudPackets.Data9> items;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public List<CloudPackets.Data9> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data15 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, items);
      }

      @Override
      public String toString() {
         return "Data15[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "items=" + this.items + "]";
      }

      public Data15(long var1, String var3, List<CloudPackets.Data9> var4) {
         this.longValue = var1;
         this.text = var3;
         this.items = var4;
      }

      public long uid() {
         return this.longValue;
      }

      public String getText() {
         return this.text;
      }

      public List<CloudPackets.Data9> getItems() {
         return this.items;
      }
   }

   public static final class Data16 {
      private final long longValue;
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final byte[] byteValues;

      public long longValue() {
         return this.longValue;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public byte[] byteValues() {
         return this.byteValues;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data16 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.byteValues, other.byteValues);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, intValue, intValue2, intValue3, byteValues);
      }

      @Override
      public String toString() {
         return "Data16[" + "longValue=" + this.longValue + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "byteValues=" + this.byteValues + "]";
      }

      public Data16(long var1, int var3, int var4, int var5, byte[] var6) {
         this.longValue = var1;
         this.intValue = var3;
         this.intValue2 = var4;
         this.intValue3 = var5;
         this.byteValues = var6;
      }

      public boolean method1823() {
         return (this.intValue & 1) != 0;
      }

      public boolean method1824() {
         return (this.intValue & 2) != 0;
      }

      public long uid() {
         return this.longValue;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public int getIntValue3() {
         return this.intValue3;
      }

      public byte[] getByteValues() {
         return this.byteValues;
      }
   }

   public static final class Data17 {
      private final String text;
      private final int intValue;

      public String text() {
         return this.text;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data17 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, intValue);
      }

      @Override
      public String toString() {
         return "Data17[" + "text=" + this.text + ", " + "intValue=" + this.intValue + "]";
      }

      public Data17(String var1, int var2) {
         this.text = var1;
         this.intValue = var2;
      }

      public String getText() {
         return this.text;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   public static final class Data18 {
      private final int intValue;
      private final long longValue;
      private final String text;
      private final JsonObject jsonObject;

      public int intValue() {
         return this.intValue;
      }

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public JsonObject jsonObject() {
         return this.jsonObject;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data18 other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.jsonObject, other.jsonObject);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, longValue, text, jsonObject);
      }

      @Override
      public String toString() {
         return "Data18[" + "intValue=" + this.intValue + ", " + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "jsonObject=" + this.jsonObject + "]";
      }

      public Data18(int var1, long var2, String var4, JsonObject var5) {
         this.intValue = var1;
         this.longValue = var2;
         this.text = var4;
         this.jsonObject = var5;
      }

      public int kind() {
         return this.intValue;
      }

      public long uid() {
         return this.longValue;
      }

      public String username() {
         return this.text;
      }

      public JsonObject getJsonObject() {
         return this.jsonObject;
      }
   }

   public static final class Data2 {
      private final long longValue;
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final float floatValue;
      private final float floatValue2;

      public long longValue() {
         return this.longValue;
      }

      public double doubleValue() {
         return this.doubleValue;
      }

      public double doubleValue2() {
         return this.doubleValue2;
      }

      public double doubleValue3() {
         return this.doubleValue3;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, doubleValue, doubleValue2, doubleValue3, floatValue, floatValue2);
      }

      @Override
      public String toString() {
         return "Data2[" + "longValue=" + this.longValue + ", " + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + "]";
      }

      public Data2(long var1, double var3, double var5, double var7, float var9, float var10) {
         this.longValue = var1;
         this.doubleValue = var3;
         this.doubleValue2 = var5;
         this.doubleValue3 = var7;
         this.floatValue = var9;
         this.floatValue2 = var10;
      }

      public long uid() {
         return this.longValue;
      }

      public double getDoubleValue() {
         return this.doubleValue;
      }

      public double getDoubleValue2() {
         return this.doubleValue2;
      }

      public double getDoubleValue3() {
         return this.doubleValue3;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }
   }

   public static final class Data3 {
      private final long longValue;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final int intValue;

      public long longValue() {
         return this.longValue;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, floatValue, floatValue2, floatValue3, intValue);
      }

      @Override
      public String toString() {
         return "Data3[" + "longValue=" + this.longValue + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "intValue=" + this.intValue + "]";
      }

      public Data3(long var1, float var3, float var4, float var5, int var6) {
         this.longValue = var1;
         this.floatValue = var3;
         this.floatValue2 = var4;
         this.floatValue3 = var5;
         this.intValue = var6;
      }

      public long uid() {
         return this.longValue;
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

      public int getIntValue() {
         return this.intValue;
      }
   }

   public static final class Data4 {
      private final int intValue;
      private final String text;
      private final boolean flag;

      public int intValue() {
         return this.intValue;
      }

      public String text() {
         return this.text;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, text, flag);
      }

      @Override
      public String toString() {
         return "Data4[" + "intValue=" + this.intValue + ", " + "text=" + this.text + ", " + "flag=" + this.flag + "]";
      }

      public Data4(int var1, String var2, boolean var3) {
         this.intValue = var1;
         this.text = var2;
         this.flag = var3;
      }

      public int protocol() {
         return this.intValue;
      }

      public String getText() {
         return this.text;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   public static final class Data5 {
      private final long longValue;
      private final String text;
      private final String text2;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data5 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2);
      }

      @Override
      public String toString() {
         return "Data5[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }

      public Data5(long var1, String var3, String var4) {
         this.longValue = var1;
         this.text = var3;
         this.text2 = var4;
      }

      public long uid() {
         return this.longValue;
      }

      public String server() {
         return this.text;
      }

      public String dimension() {
         return this.text2;
      }
   }

   public static final class Data6 {
      private final long longValue;
      private final int intValue;

      public long longValue() {
         return this.longValue;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data6 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, intValue);
      }

      @Override
      public String toString() {
         return "Data6[" + "longValue=" + this.longValue + ", " + "intValue=" + this.intValue + "]";
      }

      public Data6(long var1, int var3) {
         this.longValue = var1;
         this.intValue = var3;
      }

      public long uid() {
         return this.longValue;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   public static final class Data7 {
      private final String text;
      private final int intValue;
      private final int intValue2;

      public String text() {
         return this.text;
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
         if (!(o instanceof Data7 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, intValue, intValue2);
      }

      @Override
      public String toString() {
         return "Data7[" + "text=" + this.text + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + "]";
      }
      public static final CloudPackets.Data7 INSTANCE = new CloudPackets.Data7("", 0, 0);

      public Data7(String var1, int var2, int var3) {
         this.text = var1;
         this.intValue = var2;
         this.intValue2 = var3;
      }

      public boolean isEmpty() {
         return this.text.isEmpty() || this.intValue <= 0;
      }

      public String getText() {
         return this.text;
      }

      public int count() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }

   public static final class Data8 {
      private final long longValue;
      private final String text;
      private final String text2;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data8 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2);
      }

      @Override
      public String toString() {
         return "Data8[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + "]";
      }

      public Data8(long var1, String var3, String var4) {
         this.longValue = var1;
         this.text = var3;
         this.text2 = var4;
      }

      public long uid() {
         return this.longValue;
      }

      public String username() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }
   }

   public static final class Data9 {
      private final String text;
      private final int intValue;
      private final int intValue2;

      public String text() {
         return this.text;
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
         if (!(o instanceof Data9 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, intValue, intValue2);
      }

      @Override
      public String toString() {
         return "Data9[" + "text=" + this.text + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data9(String var1, int var2, int var3) {
         this.text = var1;
         this.intValue = var2;
         this.intValue2 = var3;
      }

      public CosmeticType method1822() {
         CosmeticType[] var1 = CosmeticType.values();
         return this.intValue >= 0 && this.intValue < var1.length ? var1[this.intValue] : CosmeticType.UNKNOWN;
      }

      public String getText() {
         return this.text;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }
}

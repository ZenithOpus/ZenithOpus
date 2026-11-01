package org.zenith.utility.misc;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.collections.EmptyList;
import kotlin.collections.EmptyMap;
import org.zenith.utility.api.AvatarUtil;
import org.zenith.utility.network.CloudPackets;

public final class ShareCodec {
   public static final int CONST_intValue = 63;
   private static final String text = "@fb:";
   private static final String text2 = "@fw:";
   private static final String text3 = "@fp:";
   private static final int CONST_intValue2 = 32;
   private static final long CONST_longValue = 5000L;
   private static volatile Map<String, String> items;
   private static volatile long longValue;
   private static final Map<Long, ShareCodec.Data> items2 = new ConcurrentHashMap<>();

   private ShareCodec() {
   }

   public static List<CloudPackets.Data9> method6721(String var0, String var1, String var2, float var3) {
      ArrayList var4 = new ArrayList(3);
      method6722(var4, "@fb:", var0, 0);
      method6722(var4, "@fw:", var1, 0);
      method6722(var4, "@fp:", var2, Float.floatToIntBits(var3));
      return var4;
   }

   private static void method6722(List<CloudPackets.Data9> var0, String var1, String var2, int var3) {
      if (var2 != null && !var2.isBlank()) {
         var0.add(new CloudPackets.Data9(var1 + method6728(var2), 63, var3));
      }
   }

   public static boolean method6723(String var0) {
      return !(var0 == null || !var0.startsWith("@fb:") && !var0.startsWith("@fw:") && !var0.startsWith("@fp:"));
   }

   public static ShareCodec.Data2 method6724(long var0, List<CloudPackets.Data9> var2) {
      if (var2 != null) {
         boolean var10000;
         label54: {
            if (var2 != null) {
               if (var2.isEmpty()) {
                  var10000 = true;
                  break label54;
               }
            }

            var10000 = var2.isEmpty();
         }

         if (!var10000) {
            ShareCodec.Data var3 = items2.get(var0);
            if (var3 != null && var3.getItems() == var2) {
               return var3.getData2();
            }

            String var4 = "";
            String var5 = "";
            String var6 = "";
            float var7 = 0.4F;
            boolean var8 = false;

            for (CloudPackets.Data9 var10 : var2) {
               String var11 = var10.getText();
               if (var10.getIntValue() == 63 && var11 != null) {
                  if (var11.startsWith("@fb:")) {
                     var4 = method6726(var11.substring("@fb:".length()));
                     var8 |= var4.isEmpty();
                  } else if (var11.startsWith("@fw:")) {
                     var5 = method6726(var11.substring("@fw:".length()));
                     var8 |= var5.isEmpty();
                  } else if (var11.startsWith("@fp:")) {
                     var6 = method6726(var11.substring("@fp:".length()));
                     var8 |= var6.isEmpty();
                     float var12;
                     var7 = Float.isFinite(var12 = Float.intBitsToFloat(var10.getIntValue2())) ? Math.max(0.1F, Math.min(2.0F, var12)) : 0.4F;
                  }
               }
            }

            ShareCodec.Data2 var13 = new ShareCodec.Data2(var4, var5, var6, var7);
            if (!var8) {
               items2.put(var0, new ShareCodec.Data(var2, var13));
            }

            return var13;
         }
      }

      items2.remove(var0);
      return ShareCodec.Data2.INSTANCE;
   }

   public static void method6725(Set<Long> var0) {
      items2.keySet().retainAll(var0);
   }

   private static String method6726(String var0) {
      var var1 = items;
      long var2 = System.currentTimeMillis();
      if (var1 != null) {
         boolean var10000;
         label22: {
            if (var1 != null) {
               if (var1.isEmpty()) {
                  var10000 = false;
                  break label22;
               }
            }

            var10000 = var1.containsKey(var0);
         }

         if (var10000 || var2 - longValue < 5000L) {
            return var1.getOrDefault(var0, "");
         }
      }

      var1 = method6727();
      longValue = var2;
      return var1.getOrDefault(var0, "");
   }

   private static synchronized Map<String, String> method6727() {
      HashMap var0 = new HashMap();

      for (AvatarUtil.Data var2 : AvatarUtil.method6671()) {
         String var3 = org.zenith.utility.config.AvatarUtil.method7257(var2.getPath());
         if (!var3.isBlank()) {
            var0.put(method6728(var3), var3);
         }
      }

      items = var0;
      return var0;
   }

   public static String method6728(String var0) {
      try {
         byte[] var1 = MessageDigest.getInstance("SHA-256").digest(var0.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8));
         return HexFormat.of().formatHex(var1).substring(0, 32);
      } catch (NoSuchAlgorithmException var2) {
         throw new IllegalStateException("SHA-256 недоступен", var2);
      }
   }

   private static final class Data {
      private final List<CloudPackets.Data9> items;
      private final ShareCodec.Data2 data2;

      public List<CloudPackets.Data9> items() {
         return this.items;
      }

      public ShareCodec.Data2 data2() {
         return this.data2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.data2, other.data2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(items, data2);
      }

      @Override
      public String toString() {
         return "Data[" + "items=" + this.items + ", " + "data2=" + this.data2 + "]";
      }

      private Data(List<CloudPackets.Data9> var1, ShareCodec.Data2 var2) {
         this.items = var1;
         this.data2 = var2;
      }

      public List<CloudPackets.Data9> getItems() {
         return this.items;
      }

      public ShareCodec.Data2 getData2() {
         return this.data2;
      }
   }

   public static final class Data2 {
      private final String text;
      private final String text2;
      private final String text3;
      private final float floatValue;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      public float floatValue() {
         return this.floatValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.floatValue, other.floatValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, text3, floatValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "floatValue=" + this.floatValue + "]";
      }
      public static final ShareCodec.Data2 INSTANCE = new ShareCodec.Data2("", "", "", 0.4F);

      public Data2(String var1, String var2, String var3, float var4) {
         this.text = var1;
         this.text2 = var2;
         this.text3 = var3;
         this.floatValue = var4;
      }

      public String body() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }

      public float getFloatValue() {
         return this.floatValue;
      }
   }
}

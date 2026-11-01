package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.zenith.utility.config.SiteSession;

public final class CloudChatClient {
   private static final int CONST_intValue = 200;
   private static final int CONST_intValue2 = 100;
   public static final int CONST_intValue3 = 500;
   private static final long CONST_longValue = 60000L;
   private static final long CONST_longValue2 = 5000L;
   private static final CloudChatClient INSTANCE = new CloudChatClient();
   private volatile List<CloudChatClient.Data3> messages = List.of();
   private volatile List<CloudChatClient.Data> online = List.of();
   private final Object obj = new Object();
   private volatile CloudChatClient.Data2 data2 = new CloudChatClient.Data2(0, 0, 0, 0);
   private volatile CloudChatClient.Data3 data3;
   private volatile long longValue;
   private volatile String text = "";
   private volatile boolean flag;
   private volatile String error = "";
   private volatile int revision;
   private volatile long longValue2;
   private volatile long longValue3;
   private volatile boolean loading;
   private final WebSocketClient webSocketClient = new WebSocketClient("/chat/ws", this::method2170);
   private volatile long longValue4;
   private volatile String owner = "";
   private static final Comparator<CloudChatClient.Data3> comparator = Comparator.comparingLong(CloudChatClient.Data3::getLongValue3)
      .thenComparingLong(CloudChatClient.Data3::getLongValue);

   private CloudChatClient() {
   }

   public static CloudChatClient getInstance() {
      return INSTANCE;
   }

   public List<CloudChatClient.Data3> method2164() {
      List<CloudChatClient.Data3> var1 = this.messages;
      boolean var2 = false;

      for (Object var4_o : (Iterable<?>)(Object)(var1)) {
         CloudChatClient.Data3 var4 = (CloudChatClient.Data3)var4_o;
         if (CloudDmClient.method4858(var4.name())) {
            var2 = true;
            break;
         }
      }

      if (!var2) {
         return var1;
      } else {
         List<Data3> var6 = new ArrayList<>(var1.size());

         for (Object var5_o : (Iterable<?>)(Object)(var1)) {
            CloudChatClient.Data3 var5 = (CloudChatClient.Data3)var5_o;
            if (!CloudDmClient.method4858(var5.name())) {
               var6.add(var5);
            }
         }

         return var6;
      }
   }

   public void method2165(long var1) {
      if (this.longValue4 != 0L && System.currentTimeMillis() - this.longValue4 > var1) {
         this.longValue4 = 0L;
         this.webSocketClient.release();
      }
   }

   public List<CloudChatClient.Data> getOnline() {
      return this.online;
   }

   public CloudChatClient.Data2 getData2() {
      return this.data2;
   }

   public CloudChatClient.Data3 getData3() {
      return this.data3;
   }

   public long getLongValue() {
      return this.longValue;
   }

   public String getText() {
      return this.text;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public String error() {
      return this.error;
   }

   public int revision() {
      return this.revision;
   }

   public boolean isLive() {
      return this.webSocketClient.isLive();
   }

   public boolean method2166() {
      return this.longValue2 != 0L;
   }

   public void method2167() {
      String var1 = SiteSession.method2289();
      if (!var1.equals(this.owner)) {
         this.owner = var1;
         this.longValue = 0L;
         this.text = "";
         this.flag = false;
         this.longValue3 = 0L;
         this.revision++;
         if (var1.isEmpty()) {
            this.webSocketClient.release();
         }
      }

      if (!var1.isEmpty()) {
         this.webSocketClient.method491();
         long var2 = System.currentTimeMillis();
         this.longValue4 = var2;
         if (!this.loading && var2 - this.longValue3 > (this.longValue2 == 0L ? 5000L : 60000L)) {
            this.refresh();
         }
      }
   }

   public void release() {
      this.webSocketClient.release();
   }

   public void refresh() {
      if (!this.loading) {
         this.loading = true;
         this.longValue3 = System.currentTimeMillis();
         JsonObject var1 = new JsonObject();
         var1.addProperty("limit", 100);
         SiteHttpClient.method2411("/chat/history", var1)
            .whenComplete(
               (var1x, var2) -> {
                  this.loading = false;
                  if (var2 != null) {
                     this.error = SiteHttpClient.method2424(var2);
                     this.revision++;
                  } else {
                     this.longValue2 = System.currentTimeMillis();
                     this.error = "";
                     ArrayList var3 = new ArrayList();
                     JsonArray var4 = SiteHttpClient.method2419(var1x, "messages");
                     if (var4 != null) {
                        for (JsonElement var6 : var4) {
                           if (var6.isJsonObject()) {
                              var3.add(method2175(var6.getAsJsonObject()));
                           }
                        }
                     }

                     synchronized (this.obj) {
                        this.messages = method2174(method2173(var3, this.messages));
                     }

                     ArrayList var13 = new ArrayList();
                     JsonArray var14 = SiteHttpClient.method2419(var1x, "online");
                     if (var14 != null) {
                        for (JsonElement var8 : var14) {
                           if (var8.isJsonObject()) {
                              var13.add(method2176(var8.getAsJsonObject()));
                           }
                        }
                     }

                     synchronized (this.obj) {
                        this.online = List.copyOf(var13);
                     }

                     JsonObject var16 = SiteHttpClient.method2418(var1x, "stats");
                     if (var16 != null) {
                        this.data2 = new CloudChatClient.Data2(
                           SiteHttpClient.method2421(var16, "messages_total", 0),
                           SiteHttpClient.method2421(var16, "messages_today", 0),
                           SiteHttpClient.method2421(var16, "users_total", 0),
                           SiteHttpClient.method2421(var16, "users_today", 0)
                        );
                     }

                     JsonObject var17 = SiteHttpClient.method2418(var1x, "pinned");
                     this.data3 = var17 == null ? null : method2175(var17);
                     JsonObject var9 = SiteHttpClient.method2418(var1x, "me");
                     if (var9 != null) {
                        this.longValue = SiteHttpClient.method2421(var9, "id", 0);
                        this.text = SiteHttpClient.method2417(var9, "role_label", "");
                        this.flag = SiteHttpClient.method2420(var9, "is_staff", false);
                     }

                     this.revision++;
                  }
               }
            );
      }
   }

   public CompletableFuture<Void> method2168(String var1) {
      String var2 = var1 == null ? "" : var1.strip();
      if (var2.isEmpty()) {
         return CompletableFuture.completedFuture(null);
      } else {
         JsonObject var3 = new JsonObject();
         var3.addProperty("body", var2.length() > 500 ? var2.substring(0, 500) : var2);
         return SiteHttpClient.method2411("/chat/send", var3).handle((var1x, var2x) -> {
            if (var2x != null) {
               this.error = SiteHttpClient.method2424(var2x);
               this.revision++;
               throw new SiteHttpClient.Entry2(this.error);
            } else {
               this.error = "";
               if (!this.webSocketClient.isLive()) {
                  JsonObject var3x = SiteHttpClient.method2418(var1x, "message_data");
                  if (var3x != null) {
                     this.method2171(method2175(var3x));
                  }
               }

               return null;
            }
         });
      }
   }

   public CompletableFuture<Void> method2169(long var1) {
      if (var1 <= 0L) {
         return CompletableFuture.completedFuture(null);
      } else {
         JsonObject var3 = new JsonObject();
         var3.addProperty("message_id", var1);
         return SiteHttpClient.method2411("/chat/delete", var3).handle((var3x, var4) -> {
            if (var4 != null) {
               this.error = SiteHttpClient.method2424(var4);
               this.revision++;
               throw new SiteHttpClient.Entry2(this.error);
            } else {
               this.error = "";
               if (!this.webSocketClient.isLive()) {
                  this.method2172(var1);
                  CloudChatClient.Data3 var5 = this.data3;
                  if (var5 != null && var5.getLongValue() == var1) {
                     this.data3 = null;
                  }

                  this.revision++;
               }

               return null;
            }
         });
      }
   }

   private void method2170(JsonObject var1) {
      String var2 = SiteHttpClient.method2417(var1, "type", "");
      switch (var2) {
         case "message":
            JsonObject var16 = SiteHttpClient.method2418(var1, "msg");
            if (var16 != null) {
               this.method2171(method2175(var16));
            }
            break;
         case "delete":
            long var15 = SiteHttpClient.method2421(var1, "message_id", 0);
            this.method2172(var15);
            CloudChatClient.Data3 var18 = this.data3;
            if (var18 != null && var18.getLongValue() == var15) {
               this.data3 = null;
            }

            this.revision++;
            break;
         case "pin":
            JsonObject var14 = SiteHttpClient.method2418(var1, "pinned");
            this.data3 = var14 == null ? null : method2175(var14);
            this.revision++;
            break;
         case "join":
            JsonObject var13 = SiteHttpClient.method2418(var1, "member");
            if (var13 != null) {
               CloudChatClient.Data var6 = method2176(var13);
               synchronized (this.obj) {
                  var var19 = new ArrayList<>(this.online);
                  var19.removeIf(var1x -> var1x.getLongValue() == var6.getLongValue());
                  var19.add(var6);
                  this.online = List.copyOf(var19);
               }

               this.revision++;
            }
            break;
         case "leave":
            long var5 = SiteHttpClient.method2421(var1, "user_id", 0);
            synchronized (this.obj) {
               var var8 = new ArrayList<>(this.online);
               if (var8.removeIf(var2x -> var2x.getLongValue() == var5)) {
                  this.online = List.copyOf(var8);
               }
            }

            this.revision++;
      }
   }

   private void method2171(CloudChatClient.Data3 var1) {
      synchronized (this.obj) {
         List<CloudChatClient.Data3> var3 = this.messages;

         for (Object var5_o : (Iterable<?>)(Object)(var3)) {
            CloudChatClient.Data3 var5 = (CloudChatClient.Data3)var5_o;
            if (var5.getLongValue() == var1.getLongValue()) {
               return;
            }
         }

         List<Data3> var8 = new ArrayList<>(var3.size() + 1);
         var8.addAll(var3);
         var8.add(var1);
         this.messages = method2174(var8);
      }

      this.revision++;
   }

   private void method2172(long var1) {
      synchronized (this.obj) {
         var var4 = new ArrayList<>(this.messages);
         if (var4.removeIf(var2 -> var2.getLongValue() == var1)) {
            this.messages = List.copyOf(var4);
         }
      }
   }

   public static List<CloudChatClient.Data3> method2173(List<CloudChatClient.Data3> var0, List<CloudChatClient.Data3> var1) {
      long var2 = 0L;
      long var4 = 0L;
      LinkedHashMap var6 = new LinkedHashMap(var0.size() + 8);

      for (CloudChatClient.Data3 var8 : var0) {
         var2 = Math.max(var2, var8.getLongValue());
         var4 = Math.max(var4, var8.getLongValue3());
         var6.put(var8.getLongValue(), var8);
      }

      for (CloudChatClient.Data3 var11 : var1) {
         if (!var6.containsKey(var11.getLongValue()) && (var0.isEmpty() || var11.getLongValue() > var2 || var11.getLongValue3() > var4)) {
            var6.put(var11.getLongValue(), var11);
         }
      }

      var var10 = new ArrayList<>(var6.values());
      var10.sort(comparator);
      return var10;
   }

   private static List<CloudChatClient.Data3> method2174(List<CloudChatClient.Data3> var0) {
      int var1 = var0.size();
      return List.copyOf(var1 > 200 ? var0.subList(var1 - 200, var1) : var0);
   }

   private static CloudChatClient.Data3 method2175(JsonObject var0) {
      return new CloudChatClient.Data3(
         SiteHttpClient.method2421(var0, "id", 0),
         SiteHttpClient.method2421(var0, "user_id", 0),
         SiteHttpClient.method2417(var0, "name", "—"),
         SiteHttpClient.method2417(var0, "role", "User"),
         SiteHttpClient.method2417(var0, "avatar_path", null),
         SiteHttpClient.method2417(var0, "body", ""),
         SiteHttpClient.method2422(SiteHttpClient.method2417(var0, "created_at", "")),
         SiteHttpClient.method2420(var0, "pinned", false),
         SiteHttpClient.method2417(var0, "title", "")
      );
   }

   private static CloudChatClient.Data method2176(JsonObject var0) {
      return new CloudChatClient.Data(
         SiteHttpClient.method2421(var0, "user_id", 0),
         SiteHttpClient.method2417(var0, "name", "—"),
         SiteHttpClient.method2417(var0, "role", "User"),
         SiteHttpClient.method2417(var0, "avatar_path", null)
      );
   }

   public static final class Data {
      private final long longValue;
      private final String text;
      private final String text2;
      private final String text3;

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

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2, text3);
      }

      @Override
      public String toString() {
         return "Data[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + "]";
      }

      public Data(long var1, String var3, String var4, String var5) {
         this.longValue = var1;
         this.text = var3;
         this.text2 = var4;
         this.text3 = var5;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String name() {
         return this.text;
      }

      public String role() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }
   }

   public static final class Data2 {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public int intValue4() {
         return this.intValue4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.intValue4, other.intValue4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3, intValue4);
      }

      @Override
      public String toString() {
         return "Data2[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "intValue4=" + this.intValue4 + "]";
      }

      public Data2(int var1, int var2, int var3, int var4) {
         this.intValue = var1;
         this.intValue2 = var2;
         this.intValue3 = var3;
         this.intValue4 = var4;
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

      public int getIntValue4() {
         return this.intValue4;
      }
   }

   public static final class Data3 {
      private final long longValue;
      private final long longValue2;
      private final String text;
      private final String text2;
      private final String text3;
      private final String text4;
      private final long longValue3;
      private final boolean flag;
      private final String text5;

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
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

      public String text4() {
         return this.text4;
      }

      public long longValue3() {
         return this.longValue3;
      }

      public boolean flag() {
         return this.flag;
      }

      public String text5() {
         return this.text5;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.longValue3, other.longValue3) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.text5, other.text5);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, longValue2, text, text2, text3, text4, longValue3, flag, text5);
      }

      @Override
      public String toString() {
         return "Data3[" + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + ", " + "longValue3=" + this.longValue3 + ", " + "flag=" + this.flag + ", " + "text5=" + this.text5 + "]";
      }

      public Data3(long var1, long var3, String var5, String var6, String var7, String var8, long var9, boolean var11, String var12) {
         this.longValue = var1;
         this.longValue2 = var3;
         this.text = var5;
         this.text2 = var6;
         this.text3 = var7;
         this.text4 = var8;
         this.longValue3 = var9;
         this.flag = var11;
         this.text5 = var12;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public String name() {
         return this.text;
      }

      public String role() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }

      public String body() {
         return this.text4;
      }

      public long getLongValue3() {
         return this.longValue3;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public String title() {
         return this.text5;
      }
   }
}

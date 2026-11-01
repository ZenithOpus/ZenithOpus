package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.MinecraftClient;
import org.zenith.Zenith;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.utility.config.SiteSession;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.render.PartyManager;

public final class CloudDmClient {
   public static final int CONST_intValue = 1000;
   private static final int CONST_intValue2 = 100;
   private static final long CONST_longValue = 20000L;
   private static final long CONST_longValue2 = 30000L;
   private static final CloudDmClient INSTANCE = new CloudDmClient();
   private volatile List<CloudDmClient.Data2> items = List.of();
   private volatile List<CloudDmClient.Data> messages = List.of();
   private final Object obj = new Object();
   private volatile long longValue;
   private volatile String peerName = "";
   private volatile String error = "";
   private volatile int revision;
   private volatile int intValue;
   private volatile long longValue2;
   private volatile boolean flag;
   private volatile boolean flag2;
   private volatile boolean flag3;
   private volatile long longValue3;
   private final WebSocketClient webSocketClient = new WebSocketClient("/dm/ws", this::method4870);
   private volatile long longValue4;
   private volatile String owner = "";
   private volatile int intValue2;
   private static final Comparator<CloudDmClient.Data> comparator = Comparator.comparingLong(CloudDmClient.Data::getLongValue4)
      .thenComparingLong(CloudDmClient.Data::getLongValue);

   private CloudDmClient() {
   }

   public static CloudDmClient getInstance() {
      return INSTANCE;
   }

   public List<CloudDmClient.Data2> method4856() {
      List<CloudDmClient.Data2> var1 = this.items;
      boolean var2 = false;

      for (Object var4_o : (Iterable<?>)(Object)(var1)) {
         CloudDmClient.Data2 var4 = (CloudDmClient.Data2)var4_o;
         if (method4858(var4.getText())) {
            var2 = true;
            break;
         }
      }

      if (!var2) {
         return var1;
      } else {
         List<Data2> var6 = new ArrayList<>(var1.size());

         for (Object var5_o : (Iterable<?>)(Object)(var1)) {
            CloudDmClient.Data2 var5 = (CloudDmClient.Data2)var5_o;
            if (!method4858(var5.getText())) {
               var6.add(var5);
            }
         }

         return var6;
      }
   }

   public List<CloudDmClient.Data> method4857() {
      return method4858(this.peerName) ? List.of() : this.messages;
   }

   public static boolean method4858(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         Zenith var1 = Zenith.getInstance();
         Cloud var2 = var1 == null ? null : var1.getCloud();
         return var2 != null && var2.prefs().method3316(var0);
      } else {
         return false;
      }
   }

   private boolean method4859() {
      String var1 = SiteSession.method2289();
      if (this.owner.isEmpty() && !var1.isEmpty()) {
         this.owner = var1;
      } else if (!var1.equals(this.owner)) {
         synchronized (this.obj) {
            this.owner = var1;
            this.intValue2++;
            this.items = List.of();
            this.messages = List.of();
         }

         this.longValue = 0L;
         this.peerName = "";
         this.error = "";
         this.intValue = 0;
         this.longValue2 = 0L;
         this.longValue3 = 0L;
         this.revision++;
         if (var1.isEmpty()) {
            this.webSocketClient.release();
         }
      }

      return !var1.isEmpty();
   }

   public void method4860() {
      if (this.method4859()) {
         this.webSocketClient.method491();
         if (!this.flag && System.currentTimeMillis() - this.longValue2 > 120000L) {
            this.method4864();
         }
      }
   }

   public void method4861(long var1) {
      if (this.longValue4 != 0L && System.currentTimeMillis() - this.longValue4 > var1) {
         this.longValue4 = 0L;
         this.webSocketClient.release();
      }
   }

   public long getLongValue() {
      return this.longValue;
   }

   public String getPeerName() {
      return this.peerName;
   }

   public boolean hasPeer() {
      return this.longValue != 0L;
   }

   public String error() {
      return this.error;
   }

   public int revision() {
      return this.revision;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public boolean isLive() {
      return this.webSocketClient.isLive();
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public void method4862() {
      this.error = "";
   }

   public void method4863() {
      if (this.method4859()) {
         this.webSocketClient.method491();
         long var1 = System.currentTimeMillis();
         this.longValue4 = var1;
         if (!this.flag && var1 - this.longValue2 > 20000L) {
            this.method4864();
         }
      }
   }

   public void release() {
      this.webSocketClient.release();
   }

   public void method4864() {
      if (!this.flag) {
         this.flag = true;
         this.longValue2 = System.currentTimeMillis();
         int var1 = this.intValue2;
         SiteHttpClient.method2411("/dm/threads", new JsonObject())
            .whenComplete(
               (var2, var3) -> {
                  this.flag = false;
                  if (var1 == this.intValue2) {
                     if (var3 != null) {
                        this.error = SiteHttpClient.method2424(var3);
                        this.revision++;
                     } else {
                        this.error = "";
                        ArrayList var4 = new ArrayList();
                        JsonArray var5 = SiteHttpClient.method2419(var2, "threads");
                        if (var5 != null) {
                           for (JsonElement var7 : var5) {
                              if (var7.isJsonObject()) {
                                 JsonObject var8 = var7.getAsJsonObject();
                                 var4.add(
                                    new CloudDmClient.Data2(
                                       SiteHttpClient.method2421(var8, "peer_id", 0),
                                       SiteHttpClient.method2417(var8, "peer_name", ""),
                                       SiteHttpClient.method2417(var8, "peer_avatar_path", null),
                                       SiteHttpClient.method2417(var8, "last_body", ""),
                                       SiteHttpClient.method2422(SiteHttpClient.method2417(var8, "last_at", "")),
                                       SiteHttpClient.method2420(var8, "last_mine", false),
                                       SiteHttpClient.method2421(var8, "unread", 0)
                                    )
                                 );
                              }
                           }
                        }

                        synchronized (this.obj) {
                           if (var1 != this.intValue2) {
                              return;
                           }

                           this.items = List.copyOf(var4);
                        }

                        this.intValue = SiteHttpClient.method2421(var2, "unread_total", 0);
                        this.revision++;
                     }
                  }
               }
            );
      }
   }

   public void method4865(long var1, String var3) {
      if (var1 != this.longValue) {
         this.longValue = var1;
         this.peerName = var3 == null ? "" : var3;
         synchronized (this.obj) {
            this.messages = List.of();
         }

         this.revision++;
         if (var1 != 0L) {
            this.method4866(var1);
         }
      }
   }

   public void close() {
      this.longValue = 0L;
      this.peerName = "";
      synchronized (this.obj) {
         this.messages = List.of();
      }

      this.revision++;
   }

   private void method4866(long var1) {
      if (!this.flag2) {
         this.flag2 = true;
         JsonObject var3 = new JsonObject();
         var3.addProperty("peer_id", var1);
         var3.addProperty("limit", 100);
         int var4 = this.intValue2;
         SiteHttpClient.method2411("/dm/history", var3).whenComplete((var4x, var5) -> {
            this.flag2 = false;
            if (var4 == this.intValue2) {
               long var6 = this.longValue;
               boolean var8 = var1 != var6;
               if (var5 != null) {
                  this.error = SiteHttpClient.method2424(var5);
                  this.revision++;
                  if (var8 && var6 != 0L && this.messages.isEmpty()) {
                     this.method4866(var6);
                  }
               } else if (var8) {
                  if (var6 != 0L && this.messages.isEmpty()) {
                     this.method4866(var6);
                  }
               } else {
                  this.error = "";
                  ArrayList var9 = new ArrayList();
                  JsonArray var10 = SiteHttpClient.method2419(var4x, "messages");
                  if (var10 != null) {
                     for (JsonElement var12 : var10) {
                        if (var12.isJsonObject()) {
                           var9.add(method4876(var12.getAsJsonObject()));
                        }
                     }
                  }

                  synchronized (this.obj) {
                     if (var4 != this.intValue2) {
                        return;
                     }

                     this.messages = List.copyOf(method4873(var9, this.messages));
                  }

                  JsonObject var16 = SiteHttpClient.method2418(var4x, "peer");
                  if (var16 != null) {
                     this.peerName = SiteHttpClient.method2417(var16, "name", this.peerName);
                  }

                  this.method4875(var1);
                  this.revision++;
               }
            }
         });
      }
   }

   public CompletableFuture<CloudDmClient.Data> method4867(long var1, String var3) {
      String var4 = var3 == null ? "" : var3.strip();
      if (var4.isEmpty()) {
         return CompletableFuture.failedFuture(new SiteHttpClient.Entry2("пустое сообщение"));
      } else {
         JsonObject var5 = new JsonObject();
         var5.addProperty("peer_id", var1);
         var5.addProperty("body", var4.length() > 1000 ? var4.substring(0, 1000) : var4);
         return SiteHttpClient.method2411("/dm/send", var5).handle((var3x, var4x) -> {
            if (var4x != null) {
               this.error = SiteHttpClient.method2424(var4x);
               this.revision++;
               throw new SiteHttpClient.Entry2(this.error);
            } else {
               this.error = "";
               CloudDmClient.Data var5x = method4876(SiteHttpClient.method2418(var3x, "message_data"));
               if (!this.webSocketClient.isLive() && var1 == this.longValue) {
                  this.method4872(var5x);
               }

               this.longValue2 = 0L;
               return var5x;
            }
         });
      }
   }

   public CompletableFuture<Integer> method4868() {
      int var1 = this.intValue2;
      return SiteHttpClient.method2411("/dm/unread", new JsonObject()).thenApply(var2 -> {
         int var3 = SiteHttpClient.method2421(var2, "unread_total", 0);
         if (var1 == this.intValue2) {
            this.intValue = var3;
         }

         return var3;
      });
   }

   public int method4869() {
      if (!this.method4859()) {
         return 0;
      } else {
         long var1 = System.currentTimeMillis();
         if (!this.flag3 && var1 - this.longValue3 > 30000L) {
            this.flag3 = true;
            this.longValue3 = var1;
            this.method4868().whenComplete((var1x, var2) -> this.flag3 = false);
         }

         return this.intValue;
      }
   }

   private void method4870(JsonObject var1) {
      String var2 = SiteHttpClient.method2417(var1, "type", "");
      if (var2.equals("dm")) {
         JsonObject var5 = SiteHttpClient.method2418(var1, "msg");
         if (var5 != null) {
            CloudDmClient.Data var4 = method4876(var5);
            this.method4871(var4);
            if (var4.getLongValue2() == this.longValue || var4.getLongValue3() == this.longValue) {
               this.method4872(var4);
            }

            this.longValue2 = 0L;
            this.revision++;
         }
      } else {
         if (var2.equals("read")) {
            long var3 = SiteHttpClient.method2421(var1, "peer_id", 0);
            if (var3 == this.longValue) {
               this.method4874();
            }

            this.revision++;
         }
      }
   }

   private void method4871(CloudDmClient.Data var1) {
      Zenith var2 = Zenith.getInstance();
      if (var2 != null && var2.getEntry() != null) {
         long var3;
         try {
            var3 = Long.parseLong(var2.getEntry().getText2().trim());
         } catch (RuntimeException var12) {
            return;
         }

         if (var1.getLongValue2() != var3) {
            Share var5 = var2.getShare();
            if (var5 == null || var5.notifyMessages.isEnabled()) {
               PartyManager var6 = PartyManager.method17();
               if (var6 == null || !var6.method32()) {
                  String var7 = "";

                  for (CloudDmClient.Data2 var9 : this.items) {
                     if (var9.getLongValue() == var1.getLongValue2()) {
                        var7 = var9.getText();
                        break;
                     }
                  }

                  if (var7.isEmpty() && var1.getLongValue2() == this.longValue) {
                     var7 = this.peerName;
                  }

                  if (!method4858(var7)) {
                     MinecraftClient var13 = MinecraftClient.getInstance();
                     boolean var14 = var1.getLongValue2() == this.longValue && var13 != null && var13.currentScreen instanceof NLMenuScreen;
                     if (!var14) {
                        String var10 = var1.body() == null ? "" : var1.body();
                        if (var10.contains("[z:")) {
                           var10 = var2.getLocalization().translate("cloud.dm.attachment");
                        }

                        String var11 = ((var7.isEmpty() ? var2.getLocalization().translate("cloud.dm.someone") : var7)
                              + ": "
                              + (var10.length() > 60 ? var10.substring(0, 60) + "…" : var10))
                           .replace('§', ' ');
                        if (var13 != null) {
                           var13.execute(() -> Cloud.method7914(var11));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void method4872(CloudDmClient.Data var1) {
      synchronized (this.obj) {
         List<CloudDmClient.Data> var3 = this.messages;

         for (Object var5_o : (Iterable<?>)(Object)(var3)) {
            CloudDmClient.Data var5 = (CloudDmClient.Data)var5_o;
            if (var5.getLongValue() == var1.getLongValue()) {
               return;
            }
         }

         List<Data> var8 = new ArrayList<>(var3.size() + 1);
         var8.addAll(var3);
         var8.add(var1);
         this.messages = List.copyOf(var8);
      }

      this.revision++;
   }

   private static List<CloudDmClient.Data> method4873(List<CloudDmClient.Data> var0, List<CloudDmClient.Data> var1) {
      long var2 = 0L;
      long var4 = 0L;
      LinkedHashMap var6 = new LinkedHashMap(var0.size() + 8);

      for (CloudDmClient.Data var8 : var0) {
         var2 = Math.max(var2, var8.getLongValue());
         var4 = Math.max(var4, var8.getLongValue4());
         var6.put(var8.getLongValue(), var8);
      }

      for (CloudDmClient.Data var11 : var1) {
         if (!var6.containsKey(var11.getLongValue()) && (var0.isEmpty() || var11.getLongValue() > var2 || var11.getLongValue4() > var4)) {
            var6.put(var11.getLongValue(), var11);
         }
      }

      var var10 = new ArrayList<>(var6.values());
      var10.sort(comparator);
      return var10;
   }

   private void method4874() {
      synchronized (this.obj) {
         var var2 = this.messages;
         ArrayList var3 = null;

         for (int var4 = 0; var4 < var2.size(); var4++) {
            CloudDmClient.Data var5 = (CloudDmClient.Data)var2.get(var4);
            if (!var5.read()) {
               if (var3 == null) {
                  var3 = new ArrayList(var2);
               }

               var3.set(var4, new CloudDmClient.Data(var5.getLongValue(), var5.getLongValue2(), var5.getLongValue3(), var5.body(), var5.getLongValue4(), true));
            }
         }

         if (var3 != null) {
            this.messages = List.copyOf(var3);
         }
      }
   }

   private void method4875(long var1) {
      synchronized (this.obj) {
         var var4 = this.items;

         for (int var5 = 0; var5 < var4.size(); var5++) {
            CloudDmClient.Data2 var6 = (CloudDmClient.Data2)var4.get(var5);
            if (var6.getLongValue() == var1 && var6.getIntValue() > 0) {
               this.intValue = Math.max(0, this.intValue - var6.getIntValue());
               var var7 = new ArrayList<>(var4);
               var7.set(
                  var5, new CloudDmClient.Data2(var6.getLongValue(), var6.getText(), var6.getText2(), var6.getText3(), var6.getLongValue2(), var6.isFlag(), 0)
               );
               this.items = List.copyOf(var7);
               return;
            }
         }
      }
   }

   private static CloudDmClient.Data method4876(JsonObject var0) {
      return var0 == null
         ? new CloudDmClient.Data(0L, 0L, 0L, "", 0L, false)
         : new CloudDmClient.Data(
            SiteHttpClient.method2421(var0, "id", 0),
            SiteHttpClient.method2421(var0, "sender_id", 0),
            SiteHttpClient.method2421(var0, "recipient_id", 0),
            SiteHttpClient.method2417(var0, "body", ""),
            SiteHttpClient.method2422(SiteHttpClient.method2417(var0, "created_at", "")),
            var0.has("read_at") && !var0.get("read_at").isJsonNull()
         );
   }

   public static final class Data {
      private final long longValue;
      private final long longValue2;
      private final long longValue3;
      private final String text;
      private final long longValue4;
      private final boolean flag;

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public long longValue3() {
         return this.longValue3;
      }

      public String text() {
         return this.text;
      }

      public long longValue4() {
         return this.longValue4;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.longValue3, other.longValue3) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.longValue4, other.longValue4) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, longValue2, longValue3, text, longValue4, flag);
      }

      @Override
      public String toString() {
         return "Data[" + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + ", " + "longValue3=" + this.longValue3 + ", " + "text=" + this.text + ", " + "longValue4=" + this.longValue4 + ", " + "flag=" + this.flag + "]";
      }

      public Data(long var1, long var3, long var5, String var7, long var8, boolean var10) {
         this.longValue = var1;
         this.longValue2 = var3;
         this.longValue3 = var5;
         this.text = var7;
         this.longValue4 = var8;
         this.flag = var10;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public long getLongValue3() {
         return this.longValue3;
      }

      public String body() {
         return this.text;
      }

      public long getLongValue4() {
         return this.longValue4;
      }

      public boolean read() {
         return this.flag;
      }
   }

   public static final class Data2 {
      private final long longValue;
      private final String text;
      private final String text2;
      private final String text3;
      private final long longValue2;
      private final boolean flag;
      private final int intValue;

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

      public long longValue2() {
         return this.longValue2;
      }

      public boolean flag() {
         return this.flag;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2, text3, longValue2, flag, intValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "longValue2=" + this.longValue2 + ", " + "flag=" + this.flag + ", " + "intValue=" + this.intValue + "]";
      }

      public Data2(long var1, String var3, String var4, String var5, long var6, boolean var8, int var9) {
         this.longValue = var1;
         this.text = var3;
         this.text2 = var4;
         this.text3 = var5;
         this.longValue2 = var6;
         this.flag = var8;
         this.intValue = var9;
      }

      public long getLongValue() {
         return this.longValue;
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

      public long getLongValue2() {
         return this.longValue2;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }
}

package org.zenith.utility.api;

import com.darkmagician6.eventapi.EventManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.client.events.Event10;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;

public final class PartyMember implements MinecraftAccess {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Cloud");
   public static final int CONST_intValue = 6;
   private static final long CONST_longValue = 300000L;
   private static final int CONST_intValue2 = 8;
   private static final int CONST_intValue3 = 32;
   private static final int CONST_intValue4 = 128;
   private static final int CONST_intValue5 = 64;
   private static final int CONST_intValue6 = 256;
   private static final long CONST_longValue2 = 15000L;
   private final Cloud cloud;
   private volatile PartyMember.Data6 data6;
   private final Map<Long, PartyMember.Data12> items = new LinkedHashMap<>();
   private final Map<Long, PartyMember.Data4> items2 = new LinkedHashMap<>();
   private final Map<Long, PartyMember.Data2> items3 = new ConcurrentHashMap<>();
   private final Map<String, List<String>> items4 = new ConcurrentHashMap<>();
   private final List<PartyMember.Data7> items5 = new ArrayList<>();
   private final List<PartyMember.Data16> items6 = new ArrayList<>();
   private final List<PartyMember.Data13> items7 = new ArrayList<>();
   private volatile PartyMember.Data data;
   private volatile long longValue;
   private volatile PartyMember.Data14 data14;
   private volatile PartyMember.Data10 data10;
   private volatile long longValue2;
   private volatile int intValue;
   private volatile String text = "";
   private volatile PartyMember.Data2 data2 = PartyMember.Data2.INSTANCE;
   private int version;
   private final List<Consumer<PartyMember.Data15>> items8 = new ArrayList<>();
   private final List<Consumer<String>> items9 = new ArrayList<>();
   private volatile long longValue3;
   private volatile long longValue4;
   private long longValue5 = -1L;

   public PartyMember(Cloud var1, Path var2) {
      this.cloud = var1;
   }

   public boolean method1135() {
      return this.cloud.method7860();
   }

   public int version() {
      return this.version;
   }

   public PartyMember.Data6 getData6() {
      return this.data6;
   }

   public boolean method1136() {
      return this.data6 != null;
   }

   public boolean method1137(long var1) {
      PartyMember.Data6 var3 = this.data6;
      return var3 != null && var1 != this.cloud.getLongValue() && var3.method1216(var1) != null;
   }

   public synchronized List<PartyMember.Data12> method1138() {
      return new ArrayList<>(this.items.values());
   }

   public synchronized List<PartyMember.Data4> method1139() {
      var var1 = new ArrayList<>(this.items2.values());
      var1.sort((var0, var1x) -> Long.compare(var1x.time(), var0.time()));
      return var1;
   }

   public synchronized PartyMember.Data4 method1140(long var1) {
      return this.items2.get(var1);
   }

   public PartyMember.Data2 method1141(long var1) {
      return var1 != 0L && var1 == this.cloud.getLongValue() ? this.data2 : this.items3.getOrDefault(var1, PartyMember.Data2.INSTANCE);
   }

   public boolean method1142(long var1) {
      return this.items3.containsKey(var1);
   }

   public PartyMember.Data2 getData2() {
      return this.data2;
   }

   public List<String> method1143(String var1) {
      return var1 == null ? List.of() : this.items4.getOrDefault(var1.toLowerCase(Locale.ROOT), List.of());
   }

   public synchronized List<PartyMember.Data7> method1144() {
      this.method1206();
      return new ArrayList<>(this.items5);
   }

   public synchronized List<PartyMember.Data16> method1145() {
      this.method1206();
      return new ArrayList<>(this.items6);
   }

   public synchronized List<PartyMember.Data13> method1146() {
      this.method1206();
      return new ArrayList<>(this.items7);
   }

   public synchronized int method1147() {
      this.method1206();
      return this.items5.size() + this.items6.size() + this.items7.size();
   }

   public PartyMember.Data getData() {
      return this.data;
   }

   public boolean method1148(long var1) {
      PartyMember.Data var3 = this.data;
      if (var3 != null && var1 != this.cloud.getLongValue()) {
         for (PartyMember.Data3 var5 : var3.getItems()) {
            if (var5.uid() == var1) {
               return !this.cloud.prefs().method3316(var5.username());
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public long getLongValue() {
      return this.longValue;
   }

   public PartyMember.Data14 getData14() {
      return this.data14;
   }

   public PartyMember.Data10 getData10() {
      return this.data10;
   }

   public String getText() {
      return this.text;
   }

   public String method1149(long var1) {
      if (var1 == this.cloud.getLongValue()) {
         return Cloud.accountName();
      } else {
         PartyPlayerInfo var3 = this.cloud.state().method8040(var1);
         if (var3 != null) {
            return var3.username();
         } else {
            PartyMember.Data6 var4 = this.data6;
            if (var4 != null) {
               PartyMember.Data3 var5 = var4.method1216(var1);
               if (var5 != null) {
                  return var5.username();
               }
            }

            return "#" + var1;
         }
      }
   }

   public void method1150() {
      this.version++;
   }

   public synchronized void method1151() {
      this.data6 = null;
      this.items.clear();
      this.items3.clear();
      this.items4.clear();
      this.data = null;
      this.longValue = 0L;
      this.items6.clear();
      this.items5.clear();
      this.items8.clear();
      this.items9.clear();
      this.longValue3 = 0L;
      this.version++;
   }

   public void method1152(String var1) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("name", method1211(var1, 24));
      this.cloud.method7861(2, var2);
   }

   public void method1153(long var1) {
      if (this.data6 == null) {
         if (this.method1135()) {
            this.longValue3 = var1;
            this.longValue4 = System.currentTimeMillis();
            this.method1152("");
         }
      } else {
         this.cloud.method7861(3, method1208(var1));
      }
   }

   public synchronized void method1154(PartyMember.Data7 var1) {
      this.items5.removeIf(var1x -> var1x.getLongValue() == var1.getLongValue());
      this.longValue3 = 0L;
      JsonObject var2 = new JsonObject();
      var2.addProperty("id", var1.getLongValue());
      this.cloud.method7861(4, var2);
      this.version++;
   }

   public synchronized void method1155(PartyMember.Data7 var1) {
      this.items5.removeIf(var1x -> var1x.getLongValue() == var1.getLongValue());
      JsonObject var2 = new JsonObject();
      var2.addProperty("id", var1.getLongValue());
      this.cloud.method7861(5, var2);
      this.version++;
   }

   public void method1156() {
      this.cloud.method7861(6, new JsonObject());
   }

   public void method1157(long var1) {
      this.cloud.method7861(7, method1208(var1));
   }

   public void method1158(long var1) {
      this.cloud.method7861(8, method1208(var1));
   }

   public void method1159(String var1) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("name", method1211(var1, 24));
      this.cloud.method7861(9, var2);
   }

   public void method1160(long var1, String var3, String var4, double var5, double var7, double var9, String var11, String var12, int var13) {
      JsonObject var14 = new JsonObject();
      if (var1 > 0L) {
         var14.addProperty("id", var1);
      }

      var14.addProperty("server", var3);
      var14.addProperty("dim", var4);
      var14.addProperty("x", var5);
      var14.addProperty("y", var7);
      var14.addProperty("z", var9);
      var14.addProperty("name", method1211(var11, 32));
      var14.addProperty("icon", var12);
      var14.addProperty("color", var13);
      this.cloud.method7861(10, var14);
   }

   public void method1161(long var1) {
      JsonObject var3 = new JsonObject();
      var3.addProperty("id", var1);
      this.cloud.method7861(11, var3);
   }

   public void method1162(String var1, String var2, double var3, double var5, double var7, String var9, List<PartyMember.Data11> var10, int var11) {
      JsonObject var12 = new JsonObject();
      var12.addProperty("server", var1);
      var12.addProperty("dim", var2);
      var12.addProperty("x", var3);
      var12.addProperty("y", var5);
      var12.addProperty("z", var7);
      var12.addProperty("cause", method1211(var9, 120));
      var12.addProperty("xp", var11);
      JsonArray var13 = new JsonArray();

      for (PartyMember.Data11 var15 : var10) {
         if (var13.size() >= 64) {
            break;
         }

         JsonArray var16 = new JsonArray();
         var16.add(var15.getText());
         var16.add(var15.count());
         var16.add(var15.getIntValue2());
         var16.add(var15.getIntValue3());
         var16.add(var15.isFlag() ? 1 : 0);
         var13.add(var16);
      }

      var12.add("items", var13);
      this.cloud.method7861(12, var12);
   }

   public synchronized void method1163(String var1, String var2, double var3, double var5, double var7, String var9, List<PartyMember.Data11> var10, int var11) {
      PartyMember.Data4 var12 = new PartyMember.Data4(
         this.longValue5--,
         this.cloud.getLongValue() == 0L ? Cloud.accountUid() : this.cloud.getLongValue(),
         Cloud.accountName(),
         var1,
         var2,
         var3,
         var5,
         var7,
         var9,
         List.copyOf(var10),
         var11,
         System.currentTimeMillis()
      );
      this.items2.put(var12.getLongValue(), var12);
      this.method1191();
      this.version++;
   }

   public synchronized void method1164(long var1) {
      PartyMember.Data4 var3 = this.items2.get(var1);
      if (var3 != null) {
         if (var3.getLongValue() > 0L && var3.uid() == this.cloud.getLongValue() && this.method1135()) {
            JsonObject var4 = new JsonObject();
            var4.addProperty("id", var1);
            this.cloud.method7861(24, var4);
         } else {
            this.items2.remove(var1);
            this.version++;
         }
      }
   }

   public void method1165(PartyMember.Data2 var1) {
      this.data2 = var1;
      JsonObject var2 = new JsonObject();
      var2.addProperty("status", var1.getText());
      var2.addProperty("text", method1211(var1.text(), 48));
      var2.addProperty("join", method1211(var1.getText3(), 80));
      var2.addProperty("mode", method1211(var1.mode(), 40));
      var2.addProperty("server", method1211(var1.server(), 64));
      var2.addProperty("since", var1.getSince());
      this.cloud.method7861(1, var2);
   }

   public void method1166(Collection<String> var1) {
      JsonObject var2 = new JsonObject();
      JsonArray var3 = new JsonArray();

      for (String var5 : var1) {
         if (var3.size() < 24) {
            var3.add(var5);
         }
      }

      var2.add("items", var3);
      this.cloud.method7861(13, var2);
   }

   public synchronized void method1167(String var1, Collection<String> var2, Consumer<String> var3) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("name", method1211(var1, 32));
      JsonArray var5 = new JsonArray();

      for (String var7 : var2) {
         if (var5.size() < 24) {
            var5.add(var7);
         }
      }

      var4.add("items", var5);
      if (var3 != null && this.method1135()) {
         this.items9.add(var3);
      }

      this.cloud.method7861(22, var4);
   }

   public synchronized void method1168(String var1, Consumer<PartyMember.Data15> var2) {
      JsonObject var3 = new JsonObject();
      var3.addProperty("code", var1.trim().toUpperCase(Locale.ROOT));
      if (var2 != null && this.method1135()) {
         this.items8.add(var2);
      }

      this.cloud.method7861(23, var3);
   }

   public void method1169(int var1, int var2) {
      JsonObject var3 = new JsonObject();
      var3.addProperty("kills", var1);
      var3.addProperty("deaths", var2);
      this.cloud.method7861(14, var3);
   }

   public void method1170(String var1) {
      JsonObject var2 = new JsonObject();
      if (var1 != null && !var1.isEmpty()) {
         var2.addProperty("week", var1);
      }

      this.cloud.method7861(15, var2);
   }

   public void method1171(String var1) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("name", method1211(var1, 24));
      this.cloud.method7861(16, var2);
   }

   public synchronized void method1172(String var1) {
      String var2 = var1.trim().toUpperCase(Locale.ROOT);
      this.items6.removeIf(var1x -> var1x.getText().equals(var2));
      JsonObject var3 = new JsonObject();
      var3.addProperty("code", var2);
      this.cloud.method7861(17, var3);
      this.version++;
   }

   public synchronized void method1173(PartyMember.Data16 var1) {
      this.items6.remove(var1);
      this.version++;
   }

   public void method1174() {
      this.cloud.method7861(18, new JsonObject());
   }

   public void method1175(long var1) {
      this.cloud.method7861(19, method1208(var1));
   }

   public void method1176(long var1) {
      this.longValue = var1;
      this.cloud.method7861(20, method1208(var1));
      this.version++;
   }

   public void method1177(long var1, String var3, String var4, String var5, String var6) {
      JsonObject var7 = method1208(var1);
      var7.addProperty("join", method1211(var3, 80));
      var7.addProperty("mode", method1211(var4, 40));
      var7.addProperty("server", method1211(var5, 64));
      var7.addProperty("note", method1211(var6, 60));
      this.cloud.method7861(21, var7);
   }

   public synchronized void method1178(PartyMember.Data13 var1) {
      this.items7.remove(var1);
      this.version++;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public long getLongValue2() {
      return this.longValue2;
   }

   public void method1179(JsonObject var1) {
      this.cloud.method7861(50, var1);
   }

   public void method1180(CloudPackets.Data18 var1) {
      JsonObject var2 = var1.getJsonObject();

      try {
         switch (var1.kind()) {
            case 1:
               this.items3.put(var1.uid(), method1201(var2));
               break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 22:
            case 23:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            default:
               return;
            case 10:
               this.method1186(var2);
               break;
            case 11:
               this.method1188(var2);
               break;
            case 12:
               this.method1190(var2);
               break;
            case 13:
               this.method1193(var1, var2);
               break;
            case 21:
               this.method1197(var1, var2);
               break;
            case 24:
               this.method1192(var2);
               break;
            case 40:
               this.method1181(var2);
               break;
            case 41:
               this.method1184(var1, var2);
               break;
            case 42:
               this.method1185(var2);
               break;
            case 43:
               this.method1189(var2);
               break;
            case 44:
               this.data14 = method1204(var2);
               break;
            case 45:
               this.method1195(var2);
               break;
            case 46:
               this.method1196(var1, var2);
               break;
            case 47:
               this.method1198(var2);
               break;
            case 48:
               this.method1199(var2);
               break;
            case 49:
               this.method1200(var2);
            case 50:
               break;
            case 51:
               this.longValue2 = var1.uid();
               this.intValue++;
         }
      } catch (RuntimeException var6) {
         logger.warn("Облако: событие {} от {}#{} не разобрано — {}", new Object[]{var1.kind(), var1.username(), var1.uid(), var6.toString()});
         return;
      }

      synchronized (this) {
         this.version++;
      }

      EventManager.call(new Event10(var1));
   }

   private void method1181(JsonObject var1) {
      long var2 = var1.has("id") ? var1.get("id").getAsLong() : 0L;
      if (var2 == 0L) {
         this.data6 = null;
         synchronized (this) {
            this.items.clear();
         }

         this.cloud.state().method8043(Set.of());
      } else {
         ArrayList var4 = new ArrayList();
         HashSet var5 = new HashSet();

         for (JsonElement var7 : var1.getAsJsonArray("members")) {
            JsonObject var8 = var7.getAsJsonObject();
            PartyMember.Data3 var9 = new PartyMember.Data3(
               var8.get("uid").getAsLong(), method1209(var8, "name"), method1209(var8, "mc"), var8.has("online") && var8.get("online").getAsBoolean()
            );
            var4.add(var9);
            if (var9.uid() != this.cloud.getLongValue() && var9.isFlag()) {
               var5.add(var9.uid());
               if (this.cloud.state().method8039(var9.uid()) == null) {
                  this.cloud.state().method8042(var9.uid(), var9.username(), var9.getText2());
               }
            }
         }

         ArrayList var13 = new ArrayList();
         if (var1.has("invited")) {
            for (JsonElement var16 : var1.getAsJsonArray("invited")) {
               JsonObject var18 = var16.getAsJsonObject();
               var13.add(new PartyMember.Data8(var18.get("uid").getAsLong(), method1209(var18, "name")));
            }
         }

         this.cloud.state().method8043(var5);
         PartyMember.Data6 var15 = this.data6;
         this.data6 = new PartyMember.Data6(
            var2,
            method1209(var1, "name"),
            var1.get("leader").getAsLong(),
            var1.has("created") ? var1.get("created").getAsLong() : 0L,
            Collections.unmodifiableList(var4),
            Collections.unmodifiableList(var13)
         );
         if (var15 == null || var15.getLongValue() != var2) {
            synchronized (this) {
               this.items5.removeIf(var2x -> var2x.getLongValue() == var2);
            }

            long var17 = this.longValue3;
            if (var17 != 0L) {
               this.longValue3 = 0L;
               if (System.currentTimeMillis() - this.longValue4 <= 15000L) {
                  this.cloud.method7861(3, method1208(var17));
               }
            }
         }
      }
   }

   private boolean method1182(CloudPackets.Data18 var1) {
      return this.cloud.prefs().method3316(var1.username());
   }

   private static boolean method1183(long var0, CloudPackets.Data18 var2) {
      return var2.uid() != 0L && var0 == var2.uid();
   }

   private synchronized void method1184(CloudPackets.Data18 var1, JsonObject var2) {
      long var3 = var2.get("id").getAsLong();
      if (!this.method1182(var1)) {
         this.items5.removeIf(var3x -> var3x.getLongValue() == var3 || method1183(var3x.getLongValue2(), var1));
         ArrayList var5 = new ArrayList();
         if (var2.has("members")) {
            for (JsonElement var7 : var2.getAsJsonArray("members")) {
               if (var5.size() >= 12) {
                  break;
               }

               var5.add(method1211(var7.getAsString(), 32));
            }
         }

         this.items5.add(new PartyMember.Data7(var3, method1209(var2, "name"), var1.uid(), var1.username(), var5, System.currentTimeMillis()));
         method1207(this.items5);
      }
   }

   private synchronized void method1185(JsonObject var1) {
      this.items.clear();

      for (JsonElement var3 : var1.getAsJsonArray("marks")) {
         PartyMember.Data12 var4 = method1202(var3.getAsJsonObject());
         this.items.put(var4.getLongValue(), var4);
      }

      this.method1187();
   }

   private synchronized void method1186(JsonObject var1) {
      PartyMember.Data12 var2 = method1202(var1);
      this.items.put(var2.getLongValue(), var2);
      this.method1187();
   }

   private void method1187() {
      var var1 = this.items.keySet().iterator();

      while (this.items.size() > 128 && var1.hasNext()) {
         var1.next();
         var1.remove();
      }
   }

   private synchronized void method1188(JsonObject var1) {
      this.items.remove(var1.get("id").getAsLong());
   }

   private synchronized void method1189(JsonObject var1) {
      this.items2.values().removeIf(var0 -> var0.getLongValue() > 0L);

      for (JsonElement var3 : var1.getAsJsonArray("deaths")) {
         PartyMember.Data4 var4 = method1203(var3.getAsJsonObject());
         this.items2.put(var4.getLongValue(), var4);
      }

      this.method1191();
   }

   private synchronized void method1190(JsonObject var1) {
      PartyMember.Data4 var2 = method1203(var1);
      this.items2.put(var2.getLongValue(), var2);
      this.method1191();
   }

   private void method1191() {
      while (this.items2.size() > 32) {
         PartyMember.Data4 var1 = null;

         for (PartyMember.Data4 var3 : this.items2.values()) {
            if (var1 == null || var3.time() < var1.time()) {
               var1 = var3;
            }
         }

         this.items2.remove(var1.getLongValue());
      }
   }

   private synchronized void method1192(JsonObject var1) {
      this.items2.remove(var1.get("id").getAsLong());
   }

   private void method1193(CloudPackets.Data18 var1, JsonObject var2) {
      String var3 = method1209(var2, "mc");
      if (var3.isEmpty()) {
         logger.warn("Гардероб: образ от {}#{} пришёл без ника Minecraft — не к кому привязать", var1.username(), var1.uid());
      } else {
         List<String> var4 = method1210(var2);
         var var5 = this.items4.put(var3.toLowerCase(Locale.ROOT), List.copyOf(var4));
         Wardrobe var6 = Wardrobe.method2488();
         if (var6 != null) {
            var var7 = method1194(var6, var4);
            var6.method2501(var3, var7);
            if (!var4.equals(var5)) {
               ArrayList var8 = new ArrayList();

               for (Object var10_o : (Iterable<?>)(Object)(var4)) {
                  String var10 = (String)var10_o;
                  if (var6.getRegistry().method1421(var10) == null) {
                     var8.add(var10);
                  }
               }

               if (var8.isEmpty()) {
                  logger.info("Гардероб: образ {}#{} (ник {}) — {} вещей", new Object[]{var1.username(), var1.uid(), var3, var7.size()});
               } else {
                  logger.warn(
                     "Гардероб: образ {}#{} (ник {}) — {} вещей, неизвестны реестру: {}", new Object[]{var1.username(), var1.uid(), var3, var7.size(), var8}
                  );
               }
            }
         }
      }
   }

   public static Map<FocusSlotEnum, String> method1194(Wardrobe var0, Collection<String> var1) {
      EnumMap var2 = new EnumMap<>(FocusSlotEnum.class);

      for (String var4 : var1) {
         HoveredData var5 = var0.getRegistry().method1421(var4);
         if (var5 != null) {
            var2.put(var5.getFocusSlotEnum(), var5.getText());
         }
      }

      return var2;
   }

   private void method1195(JsonObject var1) {
      String var2 = method1209(var1, "code");
      if (var2.isEmpty()) {
         this.data = null;
      } else {
         ArrayList var3 = new ArrayList();

         for (JsonElement var5 : var1.getAsJsonArray("members")) {
            JsonObject var6 = var5.getAsJsonObject();
            var3.add(new PartyMember.Data3(var6.get("uid").getAsLong(), method1209(var6, "name"), method1209(var6, "mc"), true));
         }

         this.data = new PartyMember.Data(var2, method1209(var1, "name"), var1.get("owner").getAsLong(), Collections.unmodifiableList(var3));
      }
   }

   private synchronized void method1196(CloudPackets.Data18 var1, JsonObject var2) {
      String var3 = method1209(var2, "code");
      if (!this.method1182(var1)) {
         this.items6.removeIf(var2x -> var2x.getText().equals(var3) || method1183(var2x.getLongValue(), var1));
         this.items6
            .add(
               new PartyMember.Data16(
                  var3,
                  method1209(var2, "name"),
                  var1.uid(),
                  var1.username(),
                  var2.has("members") ? var2.get("members").getAsInt() : 0,
                  System.currentTimeMillis()
               )
            );
         method1207(this.items6);
      }
   }

   private synchronized void method1197(CloudPackets.Data18 var1, JsonObject var2) {
      if (!this.method1182(var1)) {
         this.items7.removeIf(var1x -> var1x.getLongValue() == var1.uid());
         this.items7
            .add(
               new PartyMember.Data13(
                  var1.uid(),
                  var1.username(),
                  method1209(var2, "mc"),
                  method1209(var2, "join"),
                  method1209(var2, "mode"),
                  method1209(var2, "server"),
                  method1209(var2, "note"),
                  System.currentTimeMillis()
               )
            );
         method1207(this.items7);
      }
   }

   private void method1198(JsonObject var1) {
      String var2 = method1209(var1, "code");
      this.text = var2;
      ArrayList<Consumer> var3;
      synchronized (this) {
         var3 = new ArrayList<>(this.items9);
         this.items9.clear();
      }

      for (Object var5_o : (Iterable<?>)(Object)(var3)) {
         Consumer var5 = (Consumer)var5_o;
         var5.accept(var2);
      }
   }

   private void method1199(JsonObject var1) {
      var var2 = method1210(var1);
      PartyMember.Data15 var3 = new PartyMember.Data15(
         method1209(var1, "code"),
         method1209(var1, "name"),
         var1.has("owner") ? var1.get("owner").getAsLong() : 0L,
         method1209(var1, "ownerName"),
         List.copyOf(var2)
      );
      ArrayList<Consumer> var4;
      synchronized (this) {
         var4 = new ArrayList<>(this.items8);
         this.items8.clear();
      }

      for (Object var6_o : (Iterable<?>)(Object)(var4)) {
         Consumer var6 = (Consumer)var6_o;
         var6.accept(var3);
      }
   }

   private void method1200(JsonObject var1) {
      String var2 = method1209(var1, "code");
      long var3 = var1.has("uid") ? var1.get("uid").getAsLong() : 0L;
      this.data10 = new PartyMember.Data10(var2, var3, System.currentTimeMillis());
      if ("look_missing".equals(var2)) {
         ArrayList<Consumer> var5;
         synchronized (this) {
            var5 = new ArrayList<>(this.items8);
            this.items8.clear();
         }

         for (Object var7_o : (Iterable<?>)(Object)(var5)) {
            Consumer var7 = (Consumer)var7_o;
            var7.accept(null);
         }
      }

      if ("mate_gone".equals(var2) && var3 != 0L) {
         PartyMember.Data6 var10 = this.data6;
         if (var10 == null || var10.method1216(var3) == null) {
            HashSet var11 = new HashSet();

            for (PartyPlayerInfo var8 : this.cloud.state().method8041()) {
               if (var8.uid() != var3) {
                  var11.add(var8.uid());
               }
            }

            this.cloud.state().method8043(var11);
         }
      }
   }

   private static PartyMember.Data2 method1201(JsonObject var0) {
      return new PartyMember.Data2(
         method1209(var0, "status"),
         method1209(var0, "text"),
         method1209(var0, "join"),
         method1209(var0, "mode"),
         method1209(var0, "server"),
         var0.has("since") ? var0.get("since").getAsLong() : 0L
      );
   }

   private static PartyMember.Data12 method1202(JsonObject var0) {
      return new PartyMember.Data12(
         var0.get("id").getAsLong(),
         method1209(var0, "server"),
         method1209(var0, "dim"),
         var0.get("x").getAsDouble(),
         var0.get("y").getAsDouble(),
         var0.get("z").getAsDouble(),
         method1209(var0, "name"),
         method1209(var0, "icon"),
         var0.has("color") ? var0.get("color").getAsInt() : -1,
         var0.has("author") ? var0.get("author").getAsLong() : 0L,
         method1209(var0, "authorName"),
         var0.has("time") ? var0.get("time").getAsLong() : 0L
      );
   }

   private static PartyMember.Data4 method1203(JsonObject var0) {
      ArrayList var1 = new ArrayList();
      if (var0.has("items")) {
         for (JsonElement var3 : var0.getAsJsonArray("items")) {
            if (var1.size() >= 64) {
               break;
            }

            JsonArray var4 = var3.getAsJsonArray();
            var1.add(
               new PartyMember.Data11(
                  method1211(var4.get(0).getAsString(), 256),
                  var4.get(1).getAsInt(),
                  var4.size() > 2 ? var4.get(2).getAsInt() : 0,
                  var4.size() > 3 ? var4.get(3).getAsInt() : 0,
                  var4.size() > 4 && var4.get(4).getAsInt() != 0
               )
            );
         }
      }

      return new PartyMember.Data4(
         var0.get("id").getAsLong(),
         var0.get("uid").getAsLong(),
         method1209(var0, "name"),
         method1209(var0, "server"),
         method1209(var0, "dim"),
         var0.get("x").getAsDouble(),
         var0.get("y").getAsDouble(),
         var0.get("z").getAsDouble(),
         method1209(var0, "cause"),
         List.copyOf(var1),
         var0.has("xp") ? var0.get("xp").getAsInt() : 0,
         var0.has("time") ? var0.get("time").getAsLong() : 0L
      );
   }

   private static PartyMember.Data14 method1204(JsonObject var0) {
      ArrayList var1 = new ArrayList();
      if (var0.has("weeks")) {
         for (JsonElement var3 : var0.getAsJsonArray("weeks")) {
            var1.add(var3.getAsString());
         }
      }

      ArrayList var6 = new ArrayList();

      for (JsonElement var4 : var0.getAsJsonArray("entries")) {
         JsonObject var5 = var4.getAsJsonObject();
         var6.add(
            new PartyMember.Data5(
               var5.get("uid").getAsLong(),
               method1209(var5, "name"),
               var5.has("self") && var5.get("self").getAsBoolean(),
               var5.has("online") && var5.get("online").getAsBoolean(),
               method1205(var5.getAsJsonObject("servers")),
               method1205(var5.getAsJsonObject("prev"))
            )
         );
      }

      return new PartyMember.Data14(
         method1209(var0, "week"), method1209(var0, "prevWeek"), method1209(var0, "current"), List.copyOf(var1), List.copyOf(var6), System.currentTimeMillis()
      );
   }

   private static Map<String, PartyMember.Data9> method1205(JsonObject var0) {
      LinkedHashMap var1 = new LinkedHashMap();
      if (var0 != null) {
         for (Entry var3 : var0.entrySet()) {
            JsonObject var4 = ((JsonElement)var3.getValue()).getAsJsonObject();
            var1.put((String)var3.getKey(), new PartyMember.Data9(var4.get("ms").getAsLong(), var4.get("kills").getAsLong(), var4.get("deaths").getAsLong()));
         }
      }

      return var1;
   }

   private synchronized void method1206() {
      long var1 = System.currentTimeMillis() - 300000L;
      this.items5.removeIf(var2 -> var2.getLongValue3() < var1);
      this.items6.removeIf(var2 -> var2.getLongValue2() < var1);
      this.items7.removeIf(var2 -> var2.getLongValue2() < var1);
   }

   private static <T> void method1207(List<T> var0) {
      while (var0.size() > 8) {
         var0.remove(0);
      }
   }

   private static JsonObject method1208(long var0) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("uid", var0);
      return var2;
   }

   public static String method1209(JsonObject var0, String var1) {
      JsonElement var2 = var0.get(var1);
      if (var2 != null && var2.isJsonPrimitive()) {
         String var3 = var2.getAsString();
         return var3.length() > 256 ? var3.substring(0, 256) : var3;
      } else {
         return "";
      }
   }

   private static List<String> method1210(JsonObject var0) {
      ArrayList<String> var1 = new ArrayList<>();

      for (JsonElement var3 : var0.getAsJsonArray("items")) {
         if (var1.size() >= 64) {
            break;
         }

         var1.add(method1211(var3.getAsString(), 256));
      }

      return var1;
   }

   private static String method1211(String var0, int var1) {
      if (var0 == null) {
         return "";
      } else {
         String var2 = var0.strip();
         return var2.length() > var1 ? var2.substring(0, var1) : var2;
      }
   }

   public static Zenith method1212() {
      return Zenith.getInstance();
   }

   public static final class Data {
      private final String text;
      private final String text2;
      private final long longValue;
      private final List<PartyMember.Data3> items;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public long longValue() {
         return this.longValue;
      }

      public List<PartyMember.Data3> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, longValue, items);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "longValue=" + this.longValue + ", " + "items=" + this.items + "]";
      }

      public Data(String var1, String var2, long var3, List<PartyMember.Data3> var5) {
         this.text = var1;
         this.text2 = var2;
         this.longValue = var3;
         this.items = var5;
      }

      public String getText() {
         return this.text;
      }

      public String name() {
         return this.text2;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public List<PartyMember.Data3> getItems() {
         return this.items;
      }
   }

   public static final class Data10 {
      private final String text;
      private final long longValue;
      private final long longValue2;

      public String text() {
         return this.text;
      }

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data10 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, longValue, longValue2);
      }

      @Override
      public String toString() {
         return "Data10[" + "text=" + this.text + ", " + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + "]";
      }

      public Data10(String var1, long var2, long var4) {
         this.text = var1;
         this.longValue = var2;
         this.longValue2 = var4;
      }

      public String getText() {
         return this.text;
      }

      public long uid() {
         return this.longValue;
      }

      public long time() {
         return this.longValue2;
      }
   }

   public static final class Data11 {
      private final String text;
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final boolean flag;

      public String text() {
         return this.text;
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

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data11 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, intValue, intValue2, intValue3, flag);
      }

      @Override
      public String toString() {
         return "Data11[" + "text=" + this.text + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "flag=" + this.flag + "]";
      }

      public Data11(String var1, int var2, int var3, int var4, boolean var5) {
         this.text = var1;
         this.intValue = var2;
         this.intValue2 = var3;
         this.intValue3 = var4;
         this.flag = var5;
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

      public int getIntValue3() {
         return this.intValue3;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   public static final class Data12 {
      private final long longValue;
      private final String text;
      private final String text2;
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final String text3;
      private final String text4;
      private final int color;
      private final long longValue2;
      private final String text5;
      private final long longValue3;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
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

      public String text3() {
         return this.text3;
      }

      public String text4() {
         return this.text4;
      }

      public int color() {
         return this.color;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public String text5() {
         return this.text5;
      }

      public long longValue3() {
         return this.longValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data12 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.color, other.color) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.text5, other.text5) && java.util.Objects.equals(this.longValue3, other.longValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2, doubleValue, doubleValue2, doubleValue3, text3, text4, color, longValue2, text5, longValue3);
      }

      @Override
      public String toString() {
         return "Data12[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + ", " + "color=" + this.color + ", " + "longValue2=" + this.longValue2 + ", " + "text5=" + this.text5 + ", " + "longValue3=" + this.longValue3 + "]";
      }

      public Data12(
         long var1,
         String var3,
         String var4,
         double var5,
         double var7,
         double var9,
         String var11,
         String var12,
         int var13,
         long var14,
         String var16,
         long var17
      ) {
         this.longValue = var1;
         this.text = var3;
         this.text2 = var4;
         this.doubleValue = var5;
         this.doubleValue2 = var7;
         this.doubleValue3 = var9;
         this.text3 = var11;
         this.text4 = var12;
         this.color = var13;
         this.longValue2 = var14;
         this.text5 = var16;
         this.longValue3 = var17;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String server() {
         return this.text;
      }

      public String dimension() {
         return this.text2;
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

      public String name() {
         return this.text3;
      }

      public String getText4() {
         return this.text4;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public String getText5() {
         return this.text5;
      }

      public long time() {
         return this.longValue3;
      }
   }

   public static final class Data13 {
      private final long longValue;
      private final String text;
      private final String text2;
      private final String text3;
      private final String text4;
      private final String text5;
      private final String text6;
      private final long longValue2;

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

      public String text4() {
         return this.text4;
      }

      public String text5() {
         return this.text5;
      }

      public String text6() {
         return this.text6;
      }

      public long longValue2() {
         return this.longValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data13 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.text5, other.text5) && java.util.Objects.equals(this.text6, other.text6) && java.util.Objects.equals(this.longValue2, other.longValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2, text3, text4, text5, text6, longValue2);
      }

      @Override
      public String toString() {
         return "Data13[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + ", " + "text5=" + this.text5 + ", " + "text6=" + this.text6 + ", " + "longValue2=" + this.longValue2 + "]";
      }

      public Data13(long var1, String var3, String var4, String var5, String var6, String var7, String var8, long var9) {
         this.longValue = var1;
         this.text = var3;
         this.text2 = var4;
         this.text3 = var5;
         this.text4 = var6;
         this.text5 = var7;
         this.text6 = var8;
         this.longValue2 = var9;
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

      public String mode() {
         return this.text4;
      }

      public String server() {
         return this.text5;
      }

      public String note() {
         return this.text6;
      }

      public long getLongValue2() {
         return this.longValue2;
      }
   }

   public static final class Data14 {
      private final String text;
      private final String text2;
      private final String current;
      private final List<String> items;
      private final List<PartyMember.Data5> items2;
      private final long longValue;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public String current() {
         return this.current;
      }

      public List<String> items() {
         return this.items;
      }

      public List<PartyMember.Data5> items2() {
         return this.items2;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data14 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.current, other.current) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.items2, other.items2) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, current, items, items2, longValue);
      }

      @Override
      public String toString() {
         return "Data14[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "current=" + this.current + ", " + "items=" + this.items + ", " + "items2=" + this.items2 + ", " + "longValue=" + this.longValue + "]";
      }

      public Data14(String var1, String var2, String var3, List<String> var4, List<PartyMember.Data5> var5, long var6) {
         this.text = var1;
         this.text2 = var2;
         this.current = var3;
         this.items = var4;
         this.items2 = var5;
         this.longValue = var6;
      }

      public PartyMember.Data5 method1219() {
         for (PartyMember.Data5 var2 : this.items2) {
            if (var2.isFlag()) {
               return var2;
            }
         }

         return null;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public String getCurrent() {
         return this.current;
      }

      public List<String> getItems() {
         return this.items;
      }

      public List<PartyMember.Data5> getItems2() {
         return this.items2;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   public static final class Data15 {
      private final String text;
      private final String text2;
      private final long longValue;
      private final String text3;
      private final List<String> items;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public long longValue() {
         return this.longValue;
      }

      public String text3() {
         return this.text3;
      }

      public List<String> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data15 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, longValue, text3, items);
      }

      @Override
      public String toString() {
         return "Data15[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "longValue=" + this.longValue + ", " + "text3=" + this.text3 + ", " + "items=" + this.items + "]";
      }

      public Data15(String var1, String var2, long var3, String var5, List<String> var6) {
         this.text = var1;
         this.text2 = var2;
         this.longValue = var3;
         this.text3 = var5;
         this.items = var6;
      }

      public String getText() {
         return this.text;
      }

      public String name() {
         return this.text2;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String getText3() {
         return this.text3;
      }

      public List<String> getItems() {
         return this.items;
      }
   }

   public static final class Data16 {
      private final String text;
      private final String text2;
      private final long longValue;
      private final String text3;
      private final int intValue;
      private final long longValue2;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public long longValue() {
         return this.longValue;
      }

      public String text3() {
         return this.text3;
      }

      public int intValue() {
         return this.intValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data16 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue2, other.longValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, longValue, text3, intValue, longValue2);
      }

      @Override
      public String toString() {
         return "Data16[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "longValue=" + this.longValue + ", " + "text3=" + this.text3 + ", " + "intValue=" + this.intValue + ", " + "longValue2=" + this.longValue2 + "]";
      }

      public Data16(String var1, String var2, long var3, String var5, int var6, long var7) {
         this.text = var1;
         this.text2 = var2;
         this.longValue = var3;
         this.text3 = var5;
         this.intValue = var6;
         this.longValue2 = var7;
      }

      public String getText() {
         return this.text;
      }

      public String name() {
         return this.text2;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String getText3() {
         return this.text3;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public long getLongValue2() {
         return this.longValue2;
      }
   }

   public static final class Data2 {
      private final String text;
      private final String text2;
      private final String text3;
      private final String text4;
      private final String text5;
      private final long since;

      public String text2() {
         return this.text2;
      }

      public String text3() {
         return this.text3;
      }

      public String text4() {
         return this.text4;
      }

      public String text5() {
         return this.text5;
      }

      public long since() {
         return this.since;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.text5, other.text5) && java.util.Objects.equals(this.since, other.since);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, text3, text4, text5, since);
      }

      @Override
      public String toString() {
         return "Data2[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "text4=" + this.text4 + ", " + "text5=" + this.text5 + ", " + "since=" + this.since + "]";
      }
      public static final PartyMember.Data2 INSTANCE = new PartyMember.Data2("online", "", "", "", "", 0L);

      public Data2(String var1, String var2, String var3, String var4, String var5, long var6) {
         this.text = var1;
         this.text2 = var2;
         this.text3 = var3;
         this.text4 = var4;
         this.text5 = var5;
         this.since = var6;
      }

      public String getText() {
         return this.text;
      }

      public String text() {
         return this.text2;
      }

      public String getText3() {
         return this.text3;
      }

      public String mode() {
         return this.text4;
      }

      public String server() {
         return this.text5;
      }

      public long getSince() {
         return this.since;
      }
   }

   public static final class Data3 {
      private final long longValue;
      private final String text;
      private final String text2;
      private final boolean flag;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2, flag);
      }

      @Override
      public String toString() {
         return "Data3[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "flag=" + this.flag + "]";
      }

      public Data3(long var1, String var3, String var4, boolean var5) {
         this.longValue = var1;
         this.text = var3;
         this.text2 = var4;
         this.flag = var5;
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

      public boolean isFlag() {
         return this.flag;
      }
   }

   public static final class Data4 {
      private final long longValue;
      private final long longValue2;
      private final String text;
      private final String text2;
      private final String text3;
      private final double doubleValue;
      private final double doubleValue2;
      private final double doubleValue3;
      private final String text4;
      private final List<PartyMember.Data11> items;
      private final int intValue;
      private final long longValue3;

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

      public List<PartyMember.Data11> items() {
         return this.items;
      }

      public int intValue() {
         return this.intValue;
      }

      public long longValue3() {
         return this.longValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.text3, other.text3) && java.util.Objects.equals(this.doubleValue, other.doubleValue) && java.util.Objects.equals(this.doubleValue2, other.doubleValue2) && java.util.Objects.equals(this.doubleValue3, other.doubleValue3) && java.util.Objects.equals(this.text4, other.text4) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue3, other.longValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, longValue2, text, text2, text3, doubleValue, doubleValue2, doubleValue3, text4, items, intValue, longValue3);
      }

      @Override
      public String toString() {
         return "Data4[" + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "text3=" + this.text3 + ", " + "doubleValue=" + this.doubleValue + ", " + "doubleValue2=" + this.doubleValue2 + ", " + "doubleValue3=" + this.doubleValue3 + ", " + "text4=" + this.text4 + ", " + "items=" + this.items + ", " + "intValue=" + this.intValue + ", " + "longValue3=" + this.longValue3 + "]";
      }

      public Data4(
         long var1,
         long var3,
         String var5,
         String var6,
         String var7,
         double var8,
         double var10,
         double var12,
         String var14,
         List<PartyMember.Data11> var15,
         int var16,
         long var17
      ) {
         this.longValue = var1;
         this.longValue2 = var3;
         this.text = var5;
         this.text2 = var6;
         this.text3 = var7;
         this.doubleValue = var8;
         this.doubleValue2 = var10;
         this.doubleValue3 = var12;
         this.text4 = var14;
         this.items = var15;
         this.intValue = var16;
         this.longValue3 = var17;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public long uid() {
         return this.longValue2;
      }

      public String name() {
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

      public String getText4() {
         return this.text4;
      }

      public List<PartyMember.Data11> getItems() {
         return this.items;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public long time() {
         return this.longValue3;
      }
   }

   public static final class Data5 {
      private final long longValue;
      private final String text;
      private final boolean flag;
      private final boolean flag2;
      private final Map<String, PartyMember.Data9> items;
      private final Map<String, PartyMember.Data9> items2;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public Map<String, PartyMember.Data9> items() {
         return this.items;
      }

      public Map<String, PartyMember.Data9> items2() {
         return this.items2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data5 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.items2, other.items2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, flag, flag2, items, items2);
      }

      @Override
      public String toString() {
         return "Data5[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "items=" + this.items + ", " + "items2=" + this.items2 + "]";
      }

      public Data5(long var1, String var3, boolean var4, boolean var5, Map<String, PartyMember.Data9> var6, Map<String, PartyMember.Data9> var7) {
         this.longValue = var1;
         this.text = var3;
         this.flag = var4;
         this.flag2 = var5;
         this.items = var6;
         this.items2 = var7;
      }

      public PartyMember.Data9 method1213() {
         PartyMember.Data9 var1 = PartyMember.Data9.INSTANCE;

         for (PartyMember.Data9 var3 : this.items.values()) {
            var1 = var1.method1218(var3);
         }

         return var1;
      }

      public PartyMember.Data9 method1214() {
         PartyMember.Data9 var1 = PartyMember.Data9.INSTANCE;

         for (PartyMember.Data9 var3 : this.items2.values()) {
            var1 = var1.method1218(var3);
         }

         return var1;
      }

      public long uid() {
         return this.longValue;
      }

      public String name() {
         return this.text;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public Map<String, PartyMember.Data9> getItems() {
         return this.items;
      }

      public Map<String, PartyMember.Data9> getItems2() {
         return this.items2;
      }
   }

   public static final class Data6 {
      private final long longValue;
      private final String text;
      private final long longValue2;
      private final long longValue3;
      private final List<PartyMember.Data3> items;
      private final List<PartyMember.Data8> items2;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public long longValue3() {
         return this.longValue3;
      }

      public List<PartyMember.Data3> items() {
         return this.items;
      }

      public List<PartyMember.Data8> items2() {
         return this.items2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data6 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.longValue3, other.longValue3) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.items2, other.items2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, longValue2, longValue3, items, items2);
      }

      @Override
      public String toString() {
         return "Data6[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "longValue2=" + this.longValue2 + ", " + "longValue3=" + this.longValue3 + ", " + "items=" + this.items + ", " + "items2=" + this.items2 + "]";
      }

      public Data6(long var1, String var3, long var4, long var6, List<PartyMember.Data3> var8, List<PartyMember.Data8> var9) {
         this.longValue = var1;
         this.text = var3;
         this.longValue2 = var4;
         this.longValue3 = var6;
         this.items = var8;
         this.items2 = var9;
      }

      public boolean method1215(long var1) {
         return this.longValue2 == var1;
      }

      public PartyMember.Data3 method1216(long var1) {
         for (PartyMember.Data3 var4 : this.items) {
            if (var4.uid() == var1) {
               return var4;
            }
         }

         return null;
      }

      public int method1217() {
         int var1 = 0;

         for (PartyMember.Data3 var3 : this.items) {
            if (var3.isFlag()) {
               var1++;
            }
         }

         return var1;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String name() {
         return this.text;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public long getLongValue3() {
         return this.longValue3;
      }

      public List<PartyMember.Data3> getItems() {
         return this.items;
      }

      public List<PartyMember.Data8> getItems2() {
         return this.items2;
      }
   }

   public static final class Data7 {
      private final long longValue;
      private final String text;
      private final long longValue2;
      private final String text2;
      private final List<String> items;
      private final long longValue3;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public String text2() {
         return this.text2;
      }

      public List<String> items() {
         return this.items;
      }

      public long longValue3() {
         return this.longValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data7 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.longValue3, other.longValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, longValue2, text2, items, longValue3);
      }

      @Override
      public String toString() {
         return "Data7[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "longValue2=" + this.longValue2 + ", " + "text2=" + this.text2 + ", " + "items=" + this.items + ", " + "longValue3=" + this.longValue3 + "]";
      }

      public Data7(long var1, String var3, long var4, String var6, List<String> var7, long var8) {
         this.longValue = var1;
         this.text = var3;
         this.longValue2 = var4;
         this.text2 = var6;
         this.items = var7;
         this.longValue3 = var8;
      }

      public long getLongValue() {
         return this.longValue;
      }

      public String name() {
         return this.text;
      }

      public long getLongValue2() {
         return this.longValue2;
      }

      public String getText2() {
         return this.text2;
      }

      public List<String> getItems() {
         return this.items;
      }

      public long getLongValue3() {
         return this.longValue3;
      }
   }

   public static final class Data8 {
      private final long longValue;
      private final String text;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data8 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text);
      }

      @Override
      public String toString() {
         return "Data8[" + "longValue=" + this.longValue + ", " + "text=" + this.text + "]";
      }

      public Data8(long var1, String var3) {
         this.longValue = var1;
         this.text = var3;
      }

      public long uid() {
         return this.longValue;
      }

      public String name() {
         return this.text;
      }
   }

   public static final class Data9 {
      private final long longValue;
      private final long longValue2;
      private final long longValue3;

      public long longValue() {
         return this.longValue;
      }

      public long longValue2() {
         return this.longValue2;
      }

      public long longValue3() {
         return this.longValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data9 other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.longValue2, other.longValue2) && java.util.Objects.equals(this.longValue3, other.longValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, longValue2, longValue3);
      }

      @Override
      public String toString() {
         return "Data9[" + "longValue=" + this.longValue + ", " + "longValue2=" + this.longValue2 + ", " + "longValue3=" + this.longValue3 + "]";
      }
      public static final PartyMember.Data9 INSTANCE = new PartyMember.Data9(0L, 0L, 0L);

      public Data9(long var1, long var3, long var5) {
         this.longValue = var1;
         this.longValue2 = var3;
         this.longValue3 = var5;
      }

      public PartyMember.Data9 method1218(PartyMember.Data9 var1) {
         return new PartyMember.Data9(this.longValue + var1.longValue, this.longValue2 + var1.longValue2, this.longValue3 + var1.longValue3);
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
   }
}

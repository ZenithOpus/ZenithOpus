package org.zenith.utility.misc;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.zenith.Zenith;
import org.zenith.utility.network.CloudPackets;

public final class CloudConnectionState {
   private static final int CONST_intValue = 200;
   private static final int CONST_intValue2 = 100;
   private static final int CONST_intValue3 = 4096;
   private volatile CloudConnectionState.Type type = CloudConnectionState.Type.OFFLINE;
   private volatile String lastError = "";
   private volatile String text = "";
   private volatile int intValue;
   private final Map<Long, PartyPlayerInfo> items = new ConcurrentHashMap<>();
   private final Map<Long, PartyPlayerInfo> items2 = new ConcurrentHashMap<>();
   private final ArrayDeque<CloudPackets.Data13> arrayDeque = new ArrayDeque<>();
   private final Map<String, CloudPackets.Data8> items3 = new LinkedHashMap<>();
   private final Set<String> items4 = new HashSet<>();
   private final Map<String, List<CloudPackets.Data9>> items5 = new ConcurrentHashMap<>();
   private final Map<Long, String> items6 = new ConcurrentHashMap<>();
   private final Map<String, Long> items7 = new ConcurrentHashMap<>();
   private final Map<String, Long> items8 = new ConcurrentHashMap<>();

   public CloudConnectionState.Type getType() {
      return this.type;
   }

   public void setType(CloudConnectionState.Type var1) {
      this.type = var1;
   }

   public boolean method8035() {
      return this.type == CloudConnectionState.Type.ONLINE;
   }

   public String getLastError() {
      return this.lastError;
   }

   public void method8036(String var1) {
      this.lastError = var1 == null ? "" : var1;
   }

   public String note() {
      return this.text;
   }

   public void method8037(String var1) {
      this.text = var1 == null ? "" : var1;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public void setIntValue(int var1) {
      this.intValue = var1;
   }

   public Collection<PartyPlayerInfo> method8038() {
      return this.items.values();
   }

   public PartyPlayerInfo method8039(long var1) {
      return this.items.get(var1);
   }

   public PartyPlayerInfo method8040(long var1) {
      PartyPlayerInfo var3 = this.items.get(var1);
      return var3 != null ? var3 : this.items2.get(var1);
   }

   public Collection<PartyPlayerInfo> method8041() {
      return this.items2.values();
   }

   public PartyPlayerInfo method8042(long var1, String var3, String var4) {
      PartyPlayerInfo var5 = this.items2.get(var1);
      if (var5 != null && var5.getText2().equalsIgnoreCase(var4) && var5.username().equalsIgnoreCase(var3)) {
         return var5;
      } else {
         PartyPlayerInfo var6 = new PartyPlayerInfo(var1, var3, var4);
         this.items2.put(var1, var6);
         return var6;
      }
   }

   public void method8043(Set<Long> var1) {
      this.items2.keySet().retainAll(var1);
   }

   public PartyPlayerInfo method8044(String var1) {
      if (var1 != null && !this.items.isEmpty()) {
         for (PartyPlayerInfo var3 : this.items.values()) {
            if (var3.username().equalsIgnoreCase(var1)) {
               return var3;
            }
         }

         for (PartyPlayerInfo var5 : this.items.values()) {
            if (var5.getText2().equalsIgnoreCase(var1)) {
               return var5;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public int method8045() {
      return this.items.size();
   }

   public PartyPlayerInfo method8046(CloudPackets.Data8 var1) {
      this.items8.put(var1.username().toLowerCase(Locale.ROOT), var1.uid());
      PartyPlayerInfo var2 = this.items.get(var1.uid());
      if (var2 != null && var2.getText2().equalsIgnoreCase(var1.getText2()) && var2.username().equalsIgnoreCase(var1.username())) {
         return var2;
      } else {
         PartyPlayerInfo var3 = new PartyPlayerInfo(var1.uid(), var1.username(), var1.getText2());
         this.items.put(var1.uid(), var3);
         return var3;
      }
   }

   public long uidOf(String var1) {
      if (var1 == null) {
         return 0L;
      } else {
         Long var2 = this.items8.get(var1.toLowerCase(Locale.ROOT));
         return var2 == null ? 0L : var2;
      }
   }

   public PartyPlayerInfo method8047(long var1) {
      return this.items.remove(var1);
   }

   public void method8048() {
      this.items.clear();
   }

   public List<CloudPackets.Data13> method8049() {
      return new ArrayList<>(this.arrayDeque);
   }

   public ArrayDeque<CloudPackets.Data13> getArrayDeque() {
      return this.arrayDeque;
   }

   public void method8050(CloudPackets.Data13 var1) {
      if (this.arrayDeque.size() >= 200) {
         this.arrayDeque.pollFirst();
      }

      this.arrayDeque.addLast(var1);
   }

   public void method8051(long var1) {
      this.arrayDeque.removeIf(var2 -> var2.getLongValue3() == var1);
   }

   public void method8052(List<CloudPackets.Data13> var1) {
      if (!var1.isEmpty()) {
         ArrayList<CloudPackets.Data13> var2 = new ArrayList(var1.size() + this.arrayDeque.size());
         HashSet var3 = new HashSet();
         HashSet var4 = new HashSet();

         for (CloudPackets.Data13 var6 : var1) {
            method8053(var2, var3, var4, var6);
         }

         for (CloudPackets.Data13 var9 : this.arrayDeque) {
            method8053(var2, var3, var4, var9);
         }

         var2.sort(Comparator.comparingLong(CloudPackets.Data13::time).thenComparingLong(CloudPackets.Data13::getLongValue3));
         this.arrayDeque.clear();

         for (Object var10_o : (Iterable<?>)(Object)(var2.subList(Math.max(0, var2.size() - 200), var2.size()))) {
            CloudPackets.Data13 var10 = (CloudPackets.Data13)var10_o;
            this.arrayDeque.addLast(var10);
         }
      }
   }

   private static void method8053(List<CloudPackets.Data13> var0, Set<Long> var1, Set<String> var2, CloudPackets.Data13 var3) {
      if (var3.getLongValue3() <= 0L || var1.add(var3.getLongValue3())) {
         if (var2.add(method8054(var3))) {
            var0.add(var3);
         }
      }
   }

   private static String method8054(CloudPackets.Data13 var0) {
      return var0.uid() + "@" + var0.time() + "@" + var0.text();
   }

   public Collection<CloudPackets.Data8> method8055() {
      return this.items3.values();
   }

   public boolean method8056() {
      return !this.items3.isEmpty();
   }

   public boolean method8057(CloudPackets.Data8 var1) {
      String var2 = var1.username().toLowerCase(Locale.ROOT);
      return !this.items3.containsKey(var2) && this.items3.size() >= 100 ? false : this.items3.put(var2, var1) == null;
   }

   public List<CloudPackets.Data9> method8058(String var1) {
      return var1 != null && !this.items5.isEmpty() ? this.items5.getOrDefault(var1.toLowerCase(Locale.ROOT), List.of()) : List.of();
   }

   public void method8059(long var1, String var3, List<CloudPackets.Data9> var4) {
      if (var3 != null && !var3.isBlank()) {
         String var5 = var3.toLowerCase(Locale.ROOT);
         String var6 = this.items6.put(var1, var5);
         if (var6 != null && !var6.equals(var5) && this.items7.remove(var6, var1)) {
            this.items5.remove(var6);
         }

         if (this.items5.containsKey(var5) || this.items5.size() < 4096) {
            this.items7.put(var5, var1);
            this.items5.put(var5, var4);
         }
      }
   }

   public void method8060(String var1) {
      this.items3.remove(var1.toLowerCase(Locale.ROOT));
   }

   public boolean method8061(String var1) {
      return var1 != null && this.items4.contains(var1.toLowerCase(Locale.ROOT));
   }

   public int method8062() {
      return this.items4.size();
   }

   public void method8063(List<CloudPackets.Data14> var1) {
      this.items4.clear();

      for (CloudPackets.Data14 var3 : var1) {
         if (!var3.isFlag()) {
            this.items4.add(var3.username().toLowerCase(Locale.ROOT));
         }
      }
   }

   public void method8064() {
      this.items.clear();
      this.items2.clear();
      this.items5.clear();
      this.items6.clear();
      this.items7.clear();
      this.intValue = 0;
   }

   public static enum Type {
      OFFLINE("gui.cloud.state.offline"),
      CONNECTING("gui.cloud.state.connecting"),
      ONLINE("gui.cloud.state.online"),
      FAILED("gui.cloud.state.failed");

      private final String text;

      private Type(String var3) {
         this.text = var3;
      }

      public String label() {
         return Zenith.getInstance().getLocalization().translate(this.text);
      }
   }
}

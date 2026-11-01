package org.zenith.utility.config;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.atomic.AtomicInteger;
import org.zenith.Zenith;
import org.zenith.utility.api.Manager;
import org.zenith.utility.api.StoreApi;

public final class ConfigSyncService {
   private static final ConfigSyncService INSTANCE = new ConfigSyncService();
   private static final long CONST_longValue = 1200L;
   private static final long CONST_longValue2 = 30000L;
   private static final String text = "current_config";
   private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(var0 -> {
      Thread var1 = new Thread(var0, "zenith-config-sync");
      var1.setDaemon(true);
      return var1;
   });
   private final Map<String, ConfigSyncService.Type> items = new ConcurrentHashMap<>();
   private final Set<String> items2 = ConcurrentHashMap.newKeySet();
   private volatile List<StoreApi.Data2> items3 = List.of();
   private volatile Map<String, StoreApi.Data2> items4 = Map.of();
   private final Object obj = new Object();
   private final Map<UUID, ConfigSyncService.Data> items5 = new ConcurrentHashMap<>();
   private static final long CONST_longValue3 = 120000L;
   private final AtomicInteger atomicInteger = new AtomicInteger();
   private volatile ScheduledFuture<?> scheduledFuture;
   private volatile boolean flag;
   private volatile long longValue;
   private volatile String lastError = "";

   private ConfigSyncService() {
   }

   public static ConfigSyncService getInstance() {
      return INSTANCE;
   }

   public int revision() {
      return this.atomicInteger.get();
   }

   public boolean isFlag() {
      return this.flag;
   }

   public boolean method670() {
      return !this.items2.isEmpty() || this.scheduledFuture != null && !this.scheduledFuture.isDone();
   }

   public long getLongValue() {
      return this.longValue;
   }

   public String getLastError() {
      return this.lastError;
   }

   public ConfigSyncService.Type method671(String var1) {
      return this.items.getOrDefault(key(var1), this.flag ? ConfigSyncService.Type.SYNCED : ConfigSyncService.Type.LOCAL);
   }

   public List<StoreApi.Data2> getItems3() {
      return this.items3;
   }

   public StoreApi.Data2 method672(String var1) {
      return this.items4.get(key(var1));
   }

   private void method673(List<StoreApi.Data2> var1) {
      HashMap var2 = new HashMap();

      for (StoreApi.Data2 var4 : var1) {
         var2.putIfAbsent(key(var4.name()), var4);
      }

      this.items3 = List.copyOf(var1);
      this.items4 = var2;
   }

   private void method674(UUID var1, StoreApi.Data2 var2) {
      synchronized (this.obj) {
         var var4 = new ArrayList<>(this.items3);
         var4.removeIf(var1x -> var1x.getUUID().equals(var1));
         if (var2 != null) {
            var4.add(var2);
         }

         this.method673(var4);
      }
   }

   public void method675() {
      ScheduledFuture var1 = this.scheduledFuture;
      if (var1 != null) {
         var1.cancel(false);
      }

      this.scheduledFuture = this.scheduledExecutorService.schedule(this::run, 1200L, TimeUnit.MILLISECONDS);
   }

   public void method676() {
      if (System.currentTimeMillis() - this.longValue > 30000L && (this.scheduledFuture == null || this.scheduledFuture.isDone())) {
         this.scheduledFuture = this.scheduledExecutorService.schedule(this::run, 0L, TimeUnit.MILLISECONDS);
      }
   }

   public void method677(UUID var1) {
      this.method674(var1, null);
      this.bump();
   }

   public void method678(String var1, String var2) {
      StoreApi.Data2 var3 = this.method672(var1);
      StoreApi var4 = store();
      this.items.remove(key(var1));
      if (var3 != null && var4 != null) {
         this.items2.add(key(var2));
         this.items5.put(var3.getUUID(), new ConfigSyncService.Data(key(var2)));
         var4.method1974(var3.getUUID(), var2, null, null).whenComplete((var3x, var4x) -> {
            this.items2.remove(key(var2));
            if (var4x != null) {
               this.items5.remove(var3.getUUID());
               this.lastError = method687(var4x);
            } else {
               this.items5.put(var3x.getUUID(), new ConfigSyncService.Data(key(var3x.name())));
               this.method674(var3x.getUUID(), var3x);
            }

            this.bump();
            this.method675();
         });
      } else {
         this.method675();
      }
   }

   public void method679(StoreApi.Data2 var1) {
      this.method674(var1.getUUID(), var1.mine() ? var1 : null);
      this.bump();
   }

   public void method680(String var1) {
      StoreApi.Data2 var2 = this.method672(var1);
      this.items.remove(key(var1));
      if (var2 != null) {
         synchronized (this.obj) {
            var var4 = new ArrayList<>(this.items3);
            var4.remove(var2);
            this.method673(var4);
         }

         StoreApi var7 = store();
         if (var7 != null) {
            this.items5.put(var2.getUUID(), new ConfigSyncService.Data(""));
            var7.method1975(var2.getUUID()).whenComplete((var2x, var3) -> {
               if (var3 != null) {
                  this.items5.remove(var2.getUUID());
                  this.lastError = method687(var3);
               }

               this.bump();
            });
         }
      }
   }

   private void run() {
      this.longValue = System.currentTimeMillis();
      StoreApi var1 = store();
      if (var1 == null) {
         this.flag = false;
         this.bump();
      } else {
         List<StoreApi.Data2> var2;
         try {
            var2 = var1.method1968("config").get(15L, TimeUnit.SECONDS);
         } catch (Exception var18) {
            this.flag = false;
            this.lastError = method687(var18);
            this.bump();
            return;
         }

         this.flag = true;
         this.lastError = "";
         HashSet var3 = new HashSet();

         for (Object var5_o : (Iterable<?>)(Object)(var2)) {
            StoreApi.Data2 var5 = (StoreApi.Data2)var5_o;
            var3.add(var5.getUUID());
         }

         long var19 = System.currentTimeMillis();
         this.items5.entrySet().removeIf(var3x -> !var3.contains(var3x.getKey()) || var19 - var3x.getValue().getLongValue() > 120000L);
         ArrayList var6 = new ArrayList();

         for (Object var8_o : (Iterable<?>)(Object)(var2)) {
            StoreApi.Data2 var8 = (StoreApi.Data2)var8_o;
            if (var8.mine() && "config".equalsIgnoreCase(var8.kind())) {
               ConfigSyncService.Data var9 = this.items5.get(var8.getUUID());
               if (var9 != null) {
                  if (var9.getText().isEmpty() || !var9.getText().equals(key(var8.name()))) {
                     continue;
                  }

                  this.items5.remove(var8.getUUID());
               }

               var6.add(var8);
            }
         }

         synchronized (this.obj) {
            this.method673(var6);
         }

         File var21 = Manager.file;
         String var22 = "." + "Zenith".toLowerCase(Locale.ROOT);
         File[] var23 = var21 == null ? null : var21.listFiles();
         KeySetView var10 = ConcurrentHashMap.newKeySet();
         if (var23 != null) {
            for (File var14 : var23) {
               String var15 = var14.getName();
               if (var14.isFile() && var15.toLowerCase(Locale.ROOT).endsWith(var22)) {
                  String var16 = var15.substring(0, var15.length() - var22.length());
                  if (!var16.equalsIgnoreCase("current_config")) {
                     var10.add(key(var16));
                     if (!this.method684(key(var16))) {
                        this.method681(var1, var14, var16);
                     }
                  }
               }
            }
         }

         for (StoreApi.Data2 var25 : this.items3) {
            if (!this.items5.containsKey(var25.getUUID())
               && !var10.contains(key(var25.name()))
               && !var10.contains(key(method685(var25.name())))
               && !key(var25.name()).equals("current_config")) {
               this.method683(var1, var25, new File(var21, method685(var25.name()) + var22));
            }
         }

         this.bump();
      }
   }

   private void method681(StoreApi var1, File var2, String var3) {
      String var4 = key(var3);
      if (!this.items2.contains(var4)) {
         byte[] var5;
         try {
            var5 = Files.readAllBytes(var2.toPath());
         } catch (Exception var7) {
            this.items.put(var4, ConfigSyncService.Type.ERROR);
            return;
         }

         StoreApi.Data2 var6 = this.method672(var3);
         if (var6 == null) {
            this.method682(var1, var4, var3, var2.getName(), var5, null, 0L, "PRIVATE", "");
         } else if (method686(var5).equalsIgnoreCase(var6.getText6())) {
            this.items.put(var4, ConfigSyncService.Type.SYNCED);
         } else {
            if (var2.lastModified() >= var6.getLongValue4()) {
               this.method682(var1, var4, var3, var6.fileName(), var5, var6.getUUID(), var6.getLongValue2(), var6.getText7(), var6.description());
            } else {
               this.method683(var1, var6, var2);
            }
         }
      }
   }

   private void method682(StoreApi var1, String var2, String var3, String var4, byte[] var5, UUID var6, long var7, String var9, String var10) {
      this.items2.add(var2);
      this.items.put(var2, ConfigSyncService.Type.UPLOADING);
      this.bump();

      try {
         StoreApi.Data2 var11 = var1.method1972("config", var3, var4, var10, var9, var5, var6, var7).get(30L, TimeUnit.SECONDS);
         this.method674(var11.getUUID(), var11);
         this.items.put(var2, ConfigSyncService.Type.SYNCED);
      } catch (Exception var15) {
         this.items.put(var2, ConfigSyncService.Type.ERROR);
         this.lastError = method687(var15);
      } finally {
         this.items2.remove(var2);
         this.bump();
      }
   }

   private void method683(StoreApi var1, StoreApi.Data2 var2, File var3) {
      String var4 = key(var2.name());
      if (this.items2.add(var4)) {
         this.items.put(var4, ConfigSyncService.Type.DOWNLOADING);
         this.bump();

         try {
            byte[] var5 = var1.method1970(var2.getUUID()).get(30L, TimeUnit.SECONDS);
            var3.getParentFile().mkdirs();
            Files.write(var3.toPath(), var5);
            var3.setLastModified(Math.max(var2.getLongValue4(), 0L));
            this.items.put(var4, ConfigSyncService.Type.SYNCED);
         } catch (Exception var9) {
            this.items.put(var4, ConfigSyncService.Type.ERROR);
            this.lastError = method687(var9);
         } finally {
            this.items2.remove(var4);
            this.bump();
         }
      }
   }

   private boolean method684(String var1) {
      for (ConfigSyncService.Data var3 : this.items5.values()) {
         if (!var3.getText().isEmpty() && var3.getText().equals(var1)) {
            return true;
         }
      }

      return false;
   }

   private void bump() {
      this.atomicInteger.incrementAndGet();
   }

   private static StoreApi store() {
      return Zenith.getInstance() == null ? null : Zenith.getInstance().getStoreApi();
   }

   private static String key(String var0) {
      return var0 == null ? "" : var0.strip().toLowerCase(Locale.ROOT);
   }

   private static String method685(String var0) {
      StringBuilder var1 = new StringBuilder();

      for (char var5 : (var0 == null ? "config" : var0.strip()).toCharArray()) {
         var1.append(var5 >= ' ' && "\\/:*?\"<>|".indexOf(var5) < 0 ? var5 : '_');
      }

      String var6 = var1.toString().strip();
      return var6.isEmpty() ? "config" : var6;
   }

   private static String method686(byte[] var0) {
      try {
         return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(var0));
      } catch (Exception var2) {
         return "";
      }
   }

   private static String method687(Throwable var0) {
      Throwable var1 = var0;

      while (var1.getCause() != null && var1.getCause() != var1) {
         var1 = var1.getCause();
      }

      String var2 = var1.getMessage();
      return var2 != null && !var2.isBlank() ? var2 : var1.getClass().getSimpleName();
   }

   private static final class Data {
      private final String text;
      private final long longValue;

      public String text() {
         return this.text;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "longValue=" + this.longValue + "]";
      }

      public Data(String var1) {
         this(var1, System.currentTimeMillis());
      }

      private Data(String var1, long var2) {
         this.text = var1;
         this.longValue = var2;
      }

      public String getText() {
         return this.text;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   public static enum Type {
      SYNCED,
      UPLOADING,
      DOWNLOADING,
      ERROR,
      LOCAL;
   }
}

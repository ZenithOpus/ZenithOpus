package org.zenith.utility.api;

import com.darkmagician6.eventapi.EventManager;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Generated;
import org.zenith.MinecraftAccess;
import org.zenith.ModuleCategory;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.utility.config.ConfigSyncService;
import org.zenith.utility.misc.AesGcmCrypto;

public class Manager implements MinecraftAccess {
   public static final File file = new File(Zenith.file, "configs");
   private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(var0 -> {
      Thread var1 = new Thread(var0, "Config-AutoSave");
      var1.setDaemon(true);
      var1.setPriority(1);
      return var1;
   });
   private static final ExecutorService executorService = Executors.newSingleThreadExecutor(var0 -> {
      Thread var1 = new Thread(var0, "Config-IO");
      var1.setDaemon(true);
      var1.setPriority(4);
      return var1;
   });
   private final AtomicInteger atomicInteger = new AtomicInteger();
   private final Set<String> items = ConcurrentHashMap.newKeySet();
   private final String text = "siMunids";
   private static final long CONST_longValue = 400L;
   private final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private static final Object obj = new Object();
   private final AtomicLong atomicLong = new AtomicLong();
   private final Map<String, Long> items2 = new HashMap<>();
   private final Map<String, Object[]> items3 = new ConcurrentHashMap<>();

   public Manager() {
      file.mkdirs();
      this.method7766("current_config");
      EventManager.register(this);
      scheduledExecutorService.scheduleAtFixedRate(() -> {
         try {
            this.method7789();
         } catch (Exception var2) {
         }
      }, 5L, 5L, TimeUnit.MINUTES);
   }

   public boolean method7766(String var1) {
      return this.method7767(var1, Manager.Type.ALL);
   }

   public boolean method7767(String var1, Manager.Type var2) {
      try {
         if (var1 == null) {
            return false;
         } else {
            ConfigFile var3 = this.method7783(var1);
            if (var3 == null) {
               return false;
            } else {
               try {
                  boolean var6;
                  try (BufferedReader var4 = new BufferedReader(new FileReader(var3.getFile()))) {
                     String var5 = var4.readLine();
                     if (var5 == null || var5.isBlank()) {
                        return false;
                     }

                     var6 = this.method7775(this.method7774(var5), var2, var3);
                  }

                  return var6;
               } catch (Exception var9) {
                  var9.printStackTrace();
                  return false;
               }
            }
         }
      } catch (Exception var10) {
         return false;
      }
   }

   public CompletableFuture<Boolean> method7768(String var1, Manager.Type var2) {
      return CompletableFuture.<Object[]>supplyAsync(() -> {
         ConfigFile var2x = this.method7783(var1);
         if (var2x == null) {
            return null;
         } else {
            try {
               Object[] var5;
               try (BufferedReader var3 = new BufferedReader(new FileReader(var2x.getFile()))) {
                  String var4 = var3.readLine();
                  if (var4 == null || var4.isBlank()) {
                     return null;
                  }

                  var5 = new Object[]{var2x, this.method7774(var4)};
               }

               return var5;
            } catch (Exception var8) {
               return null;
            }
         }
      }, executorService).thenApplyAsync(var2x -> {
         if (var2x == null) {
            return false;
         } else {
            try {
               return this.method7775((JsonObject)var2x[1], var2, (ConfigFile)var2x[0]);
            } catch (Exception var4) {
               var4.printStackTrace();
               return false;
            }
         }
      }, mc);
   }

   public CompletableFuture<Boolean> method7769(byte[] var1, Manager.Type var2) {
      return CompletableFuture.<JsonObject>supplyAsync(() -> {
         if (var1 != null && var1.length != 0) {
            try {
               JsonObject var4;
               try (BufferedReader var2x = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(var1), StandardCharsets.UTF_8))) {
                  String var3 = var2x.readLine();
                  var4 = var3 != null && !var3.isBlank() ? this.method7774(var3) : null;
               }

               return var4;
            } catch (Exception var7) {
               return null;
            }
         } else {
            return null;
         }
      }, executorService).thenApplyAsync(var2x -> var2x != null && this.method7775(var2x, var2, null), mc);
   }

   public int method7770() {
      return this.atomicInteger.get();
   }

   private static File method7771(String var0) {
      String var1 = var0 == null ? "" : var0.replace("." + "Zenith".toLowerCase(), "");
      return new File(file, var1 + "." + "Zenith".toLowerCase());
   }

   public Manager.Data method7772(String var1) {
      File var2 = method7771(var1);
      Object[] var3 = this.items3.get(var2.getAbsolutePath());
      if (var3 != null && (Long)var3[0] == var2.lastModified()) {
         return (Manager.Data)var3[1];
      } else {
         if (this.items.add(var2.getAbsolutePath())) {
            executorService.execute(() -> {
               try {
                  this.method7784(var1);
               } finally {
                  this.items.remove(var2.getAbsolutePath());
                  this.atomicInteger.incrementAndGet();
               }
            });
         }

         return var3 == null ? null : (Manager.Data)var3[1];
      }
   }

   public boolean method7773(byte[] var1, Manager.Type var2) {
      if (var1 != null && var1.length != 0) {
         try {
            boolean var5;
            try (BufferedReader var3 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(var1), StandardCharsets.UTF_8))) {
               String var4 = var3.readLine();
               if (var4 == null || var4.isBlank()) {
                  return false;
               }

               var5 = this.method7775(this.method7774(var4), var2, null);
            }

            return var5;
         } catch (Exception var8) {
            return false;
         }
      } else {
         return false;
      }
   }

   private JsonObject method7774(String var1) throws Exception {
      byte[] var2 = Base64.getDecoder().decode(var1);
      byte[] var3 = AesGcmCrypto.method5340(var2, "siMunids");
      String var4 = new String(var3, StandardCharsets.UTF_8);
      return JsonParser.parseString(var4).getAsJsonObject();
   }

   private boolean method7775(JsonObject var1, Manager.Type var2, ConfigFile var3) {
      if (var2 == Manager.Type.ONLY_THEMES) {
         JsonObject var9 = new JsonObject();
         if (var1.has("Styles")) {
            var9.add("Styles", var1.get("Styles"));
         }

         this.method7777(var3, var9);
         this.method7778();
         return true;
      } else {
         HashMap var4 = new HashMap();

         for (Module var6 : Zenith.getInstance().getModuleManager().getItems()) {
            var4.put(var6.getId(), new int[]{var6.getKeyCode(), var6.getKeyModifiers()});
         }

         this.method7777(var3, var1);
         JsonObject var10 = var1.has("Modules") && var1.get("Modules").isJsonObject() ? var1.getAsJsonObject("Modules") : null;

         for (Module var7 : Zenith.getInstance().getModuleManager().getItems()) {
            int[] var8 = (int[])var4.get(var7.getId());
            if (var8 != null) {
               if (var2 == Manager.Type.IGNORE_BINDS) {
                  var7.setKeyCode(var8[0]);
                  var7.setKeyModifiers(var8[1]);
               } else if (method7776(var10, var7)) {
                  var7.setKeyModifiers(var8[1]);
               }
            }
         }

         this.method7778();
         return true;
      }
   }

   private static boolean method7776(JsonObject var0, Module var1) {
      if (var0 == null) {
         return false;
      } else {
         JsonElement var2 = var0.get(var1.getId());
         if ((var2 == null || !var2.isJsonObject()) && var1.getId().startsWith("zenith:")) {
            var2 = var0.get(var1.getName());
         }

         return var2 != null && var2.isJsonObject() && !var2.getAsJsonObject().has("keyCode");
      }
   }

   private void method7777(ConfigFile var1, JsonObject var2) {
      if (var1 == null) {
         ConfigFile.loadState(var2);
      } else {
         var1.load(var2);
      }
   }

   private void method7778() {
      if (mc.player != null && Zenith.getInstance().getCommandManager() != null) {
         try {
            Zenith.getInstance().getCommandManager().getDispatcher().execute("binds list", Zenith.getInstance().getCommandManager().getSource());
         } catch (Exception var2) {
         }
      }
   }

   public boolean method7779(String var1) {
      return this.method7780(var1, true, true, true);
   }

   public boolean method7780(String var1, boolean var2, boolean var3, boolean var4) {
      try {
         if (var1 == null) {
            return false;
         } else {
            ConfigFile var5 = this.method7783(var1);
            if (var5 == null) {
               var5 = new ConfigFile(var1);
            }

            long var6 = this.atomicLong.incrementAndGet();
            JsonObject var8 = var5.save();
            if (var8 == null) {
               return false;
            } else {
               this.method7782(var8, var2, var3, var4);
               this.method7781(var1, var5, var8, var6);
               return true;
            }
         }
      } catch (Exception var9) {
         return false;
      }
   }

   private void method7781(String var1, ConfigFile var2, JsonObject var3, long var4) throws Exception {
      String var6 = new GsonBuilder().setPrettyPrinting().create().toJson(var3);
      String var7 = Base64.getEncoder().encodeToString(AesGcmCrypto.method5339(var6.getBytes(), "siMunids"));
      byte[] var8 = var7.getBytes(StandardCharsets.US_ASCII);
      Path var9 = var2.getFile().toPath().toAbsolutePath();
      Path var10 = Zenith.file.toPath().toAbsolutePath().resolve(var9.getFileName() + ".tmp");
      String var11 = var9.toString();
      synchronized (obj) {
         Long var13 = this.items2.get(var11);
         if (var13 != null && var13 >= var4) {
            return;
         }

         Files.write(var10, var8);

         try {
            Files.move(var10, var9, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (IOException var16) {
            Files.write(var9, var8);
            Files.deleteIfExists(var10);
         }

         this.items2.put(var11, var4);
      }

      if (!var1.replace("." + "Zenith".toLowerCase(), "").equalsIgnoreCase("current_config")) {
         ConfigSyncService.getInstance().method675();
      }
   }

   private void method7782(JsonObject var1, boolean var2, boolean var3, boolean var4) {
      if (!var2) {
         var1.remove("Styles");
         var1.remove("FiguraData");
         var1.remove("PetData");
         var1.remove("Wardrobe");
         var1.remove("Language");
         var1.remove("Interface");
      }

      if (!var3) {
         var1.remove("BotData");
      }

      JsonObject var5 = var1.has("Modules") && var1.get("Modules").isJsonObject() ? var1.getAsJsonObject("Modules") : null;
      if (var5 != null) {
         for (Module var7 : Zenith.getInstance().getModuleManager().getItems()) {
            ModuleCategory var8 = var7.getCategory();
            boolean var9 = var8 == ModuleCategory.RENDER || var8 == ModuleCategory.THEMES;
            if ((!var9 || var2) && (var9 || var3)) {
               if (!var4 && var5.has(var7.getId()) && var5.get(var7.getId()).isJsonObject()) {
                  var5.getAsJsonObject(var7.getId()).remove("keyCode");
                  var5.getAsJsonObject(var7.getId()).remove("keyModifiers");
               }
            } else {
               var5.remove(var7.getId());
            }
         }
      }
   }

   public ConfigFile method7783(String var1) {
      if (var1 == null) {
         return null;
      } else {
         var1 = var1.replace("." + "Zenith".toLowerCase(), "");
         File var2 = new File(file, var1 + "." + "Zenith".toLowerCase());
         return var2.exists() ? new ConfigFile(var1) : null;
      }
   }

   public Manager.Data method7784(String var1) {
      ConfigFile var2 = this.method7783(var1);
      if (var2 == null) {
         this.items3.put(method7771(var1).getAbsolutePath(), new Object[]{0L, Manager.Data.INSTANCE});
         return Manager.Data.INSTANCE;
      } else {
         File var3 = var2.getFile();
         String var4 = var3.getAbsolutePath();
         Object[] var5 = this.items3.get(var4);
         if (var5 != null && (Long)var5[0] == var3.lastModified()) {
            return (Manager.Data)var5[1];
         } else {
            Manager.Data var6 = Manager.Data.INSTANCE;

            try (BufferedReader var7 = new BufferedReader(new FileReader(var3))) {
               String var8 = var7.readLine();
               if (var8 != null && !var8.isBlank()) {
                  JsonObject var9 = this.method7774(var8);
                  HashSet var10 = new HashSet();
                  int var11 = 0;
                  if (var9.has("Modules") && var9.get("Modules").isJsonObject()) {
                     for (Entry var13 : var9.getAsJsonObject("Modules").entrySet()) {
                        if (((JsonElement)var13.getValue()).isJsonObject()) {
                           JsonObject var14 = ((JsonElement)var13.getValue()).getAsJsonObject();
                           if (var14.has("enabled") && var14.get("enabled").getAsBoolean()) {
                              var10.add((String)var13.getKey());
                           }

                           if (var14.has("keyCode") && var14.get("keyCode").getAsInt() > 0) {
                              var11++;
                           }
                        }
                     }
                  }

                  var6 = new Manager.Data(Set.copyOf(var10), var11, var9.has("Styles"));
               }
            } catch (Exception var17) {
            }

            this.items3.put(var4, new Object[]{var3.lastModified(), var6});
            return var6;
         }
      }
   }

   public List<String> method7785() {
      File[] var1 = file.listFiles();
      ArrayList<String> var2 = new ArrayList<>();
      if (var1 != null) {
         for (File var6 : var1) {
            var2.add(var6.getName());
         }
      }

      return var2;
   }

   public boolean method7786(String var1) {
      if (var1 == null) {
         return false;
      } else {
         ConfigFile var2 = this.method7783(var1);
         if (var2 == null) {
            return false;
         } else {
            File var3 = var2.getFile();
            boolean var4 = var3.exists() && var3.delete();
            if (var4) {
               ConfigSyncService.getInstance().method680(var1.replace("." + "Zenith".toLowerCase(), ""));
            }

            return var4;
         }
      }
   }

   public synchronized boolean method7787(String var1, String var2) {
      if (var1 != null && var2 != null) {
         String var3 = var1.replace("." + "Zenith".toLowerCase(), "").trim();
         String var4 = var2.replace("." + "Zenith".toLowerCase(), "").trim();
         if (var3.isEmpty() || var4.isEmpty()) {
            return false;
         } else if (var3.equalsIgnoreCase(var4)) {
            return true;
         } else {
            ConfigFile var5 = this.method7783(var3);
            if (var5 != null && this.method7783(var4) == null) {
               try {
                  ConfigFile var6 = new ConfigFile(var4);
                  Files.copy(var5.getFile().toPath(), var6.getFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
                  if (!var5.getFile().delete()) {
                     var6.getFile().delete();
                     return false;
                  } else {
                     ConfigSyncService.getInstance().method678(var3, var4);
                     return true;
                  }
               } catch (Exception var7) {
                  return false;
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public void save() {
      ConfigSyncService.getInstance().method676();
      this.method7779("current_config");
   }

   public void method7788() {
      if (this.atomicBoolean.compareAndSet(false, true)) {
         scheduledExecutorService.schedule(() -> {
            this.atomicBoolean.set(false);

            try {
               this.method7789();
            } catch (Exception var2) {
            }
         }, 400L, TimeUnit.MILLISECONDS);
      }
   }

   private void method7789() {
      ConfigSyncService.getInstance().method676();
      mc.execute(() -> {
         try {
            ConfigFile var1 = this.method7783("current_config");
            if (var1 == null) {
               var1 = new ConfigFile("current_config");
            }

            long var2 = this.atomicLong.incrementAndGet();
            JsonObject var4 = var1.save();
            if (var4 == null) {
               return;
            }

            ConfigFile var5 = var1;
            executorService.execute(() -> {
               try {
                  this.method7781("current_config", var5, var4, var2);
               } catch (Exception var6x) {
               }
            });
         } catch (Exception var6) {
         }
      });
   }

   public boolean method7790() {
      return this.atomicBoolean.get();
   }

   @Generated
   public AtomicInteger getAtomicInteger() {
      return this.atomicInteger;
   }

   @Generated
   public Set<String> getItems() {
      return this.items;
   }

   @Generated
   public String method7791() {
      return "siMunids";
   }

   @Generated
   public AtomicBoolean getAtomicBoolean() {
      return this.atomicBoolean;
   }

   @Generated
   public AtomicLong getAtomicLong() {
      return this.atomicLong;
   }

   @Generated
   public Map<String, Long> getItems2() {
      return this.items2;
   }

   @Generated
   public Map<String, Object[]> getItems3() {
      return this.items3;
   }

   public static final class Data {
      private final Set<String> items;
      private final int intValue;
      private final boolean flag;

      public Set<String> items() {
         return this.items;
      }

      public int intValue() {
         return this.intValue;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(items, intValue, flag);
      }

      @Override
      public String toString() {
         return "Data[" + "items=" + this.items + ", " + "intValue=" + this.intValue + ", " + "flag=" + this.flag + "]";
      }
      public static final Manager.Data INSTANCE = new Manager.Data(Set.of(), 0, false);

      public Data(Set<String> var1, int var2, boolean var3) {
         this.items = var1;
         this.intValue = var2;
         this.flag = var3;
      }

      public Set<String> getItems() {
         return this.items;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   public static enum Type {
      ALL,
      IGNORE_BINDS,
      ONLY_THEMES;
   }
}

package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.utility.config.CosmeticFileLoader;
import org.zenith.utility.misc.CosmeticModel;
import org.zenith.utility.misc.CosmeticSkeleton;
import org.zenith.utility.misc.CosmeticTextureCache;
import org.zenith.utility.misc.CosmeticType;
import org.zenith.utility.misc.Data1;
import org.zenith.utility.misc.ObjParser;
import org.zenith.utility.misc.StoreEntry;
import org.zenith.utility.network.Cloud;

public final class Cosmetics implements MinecraftAccess {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Cosmetics");
   private static final String text = "cosmetics.json";
   private static final String text2 = "cache";
   private static final String text3 = "zenith.cosmetics.url";
   private static final int CONST_intValue = 64;
   private static final long CONST_longValue = 250000000L;
   private static final int CONST_intValue2 = 128;
   private static final int CONST_intValue3 = 2;
   private static final String text4 = "прочее";
   private final Path path;
   private final Path path2;
   private final Index index;
   private final CosmeticTextureCache cosmeticTextureCache = new CosmeticTextureCache();
   private final Overrides overrides;
   private final Presets presets;
   private final LinkedBlockingDeque<Runnable> linkedBlockingDeque = new LinkedBlockingDeque<Runnable>() {
      public boolean offer(Runnable var1) {
         if (var1 instanceof Cosmetics.Entry && this.size() >= 128) {
            Runnable var2 = this.pollLast();
            if (var2 instanceof Cosmetics.Entry var3) {
               var3.method3019();
            } else if (var2 != null) {
               this.offerLast(var2);
            }
         }

         return this.offerFirst(var1);
      }
   };
   private final ExecutorService executorService;
   private volatile List<CosmeticItemInfo> items = List.of();
   private volatile Map<String, CosmeticItemInfo> items2 = Map.of();
   private volatile List<String> items3 = List.of();
   private volatile Map<String, List<CosmeticItemInfo>> items4 = Map.of();
   private final LinkedHashMap<String, CosmeticModel> linkedHashMap = new LinkedHashMap<>(32, 0.75F, true);
   private final Map<String, Long> items5 = new HashMap<>();
   private final Set<String> items6 = ConcurrentHashMap.newKeySet();
   private final Set<String> items7 = ConcurrentHashMap.newKeySet();
   private final Map<String, Cosmetics.Data> items8 = new ConcurrentHashMap<>();
   private static final int CONST_intValue4 = 3;
   private static final long CONST_longValue2 = 4000L;
   private final Set<String> items9 = new LinkedHashSet<>();
   private volatile Set<String> items10 = Set.of();
   private volatile List<String> items11 = List.of();
   private volatile int intValue;
   private String url = "";
   private String text5 = "";
   private float floatValue = 1.35F;
   private final Map<String, StoreEntry> items12 = new ConcurrentHashMap<>();
   private final Map<String, StoreEntry> items13 = new ConcurrentHashMap<>();
   private final Set<String> items14 = ConcurrentHashMap.newKeySet();

   public Cosmetics(Path var1) {
      this.path = var1.resolve("cosmetics");
      this.path2 = this.path.resolve("cosmetics.json");
      this.index = new Index(this.path.resolve("cache"), this::method2997);
      this.overrides = new Overrides(this.path);
      this.presets = new Presets(this.path);
      AtomicInteger var2 = new AtomicInteger();
      this.executorService = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, this.linkedBlockingDeque, var1x -> {
         Thread var2x = new Thread(var1x, "Nexus-Cosmetics-" + var2.incrementAndGet());
         var2x.setDaemon(true);
         var2x.setPriority(1);
         return var2x;
      });
      this.method3001();
   }

   public void method2959() {
      this.method3009(this::reload);
   }

   public static Cosmetics method2960() {
      Zenith var0 = Zenith.getInstance();
      return var0 == null ? null : var0.getCosmetics();
   }

   public void method2961(StoreEntry var1) {
      this.method2962(var1, false);
   }

   public void method2962(StoreEntry var1, boolean var2) {
      if (var1 != null) {
         StoreEntry var3 = this.items13.get(var1.userId());
         if (var2 || var3 == null || var1.method1681(var3)) {
            this.items13.put(var1.userId(), var1);
            if (var3 != null && var3.name() != null && !var3.name().isBlank()) {
               this.items12.remove(var3.name().toLowerCase(Locale.ROOT));
            }

            if (var1.name() != null && !var1.name().isBlank()) {
               this.items12.put(var1.name().toLowerCase(Locale.ROOT), var1);
            }

            for (Data1 var5 : var1.getItems()) {
               this.method2990(var5.getText());
            }
         }
      }
   }

   public List<Data1> method2963(String var1) {
      if (var1 != null && !var1.isBlank()) {
         String var2 = var1.toLowerCase(Locale.ROOT);
         if (this.items14.contains(var2)) {
            return List.of();
         } else {
            StoreEntry var3 = this.items12.get(var2);
            return var3 == null ? List.of() : var3.getItems();
         }
      } else {
         return List.of();
      }
   }

   public boolean method2964(String var1) {
      return var1 != null && this.items12.containsKey(var1.toLowerCase(Locale.ROOT));
   }

   public StoreEntry method2965(String var1) {
      return var1 == null ? null : this.items13.get(var1);
   }

   public void method2966(String var1, boolean var2) {
      if (var1 != null && !var1.isBlank()) {
         String var3 = var1.toLowerCase(Locale.ROOT);
         if (var2 ? this.items14.add(var3) : this.items14.remove(var3)) {
            this.intValue++;
         }
      }
   }

   public boolean method2967(String var1) {
      return var1 != null && this.items14.contains(var1.toLowerCase(Locale.ROOT));
   }

   public void method2968() {
      this.items12.clear();
      this.items13.clear();
   }

   public List<CosmeticItemInfo> all() {
      return this.items;
   }

   public List<String> getItems3() {
      return this.items3;
   }

   public List<CosmeticItemInfo> method2969(String var1) {
      var var2 = this.items4.get(var1);
      return var2 == null ? List.of() : var2;
   }

   public List<CosmeticItemInfo> method2970(String var1) {
      if (var1 != null && !var1.isBlank()) {
         String var2 = var1.strip().toLowerCase(Locale.ROOT);
         ArrayList var3 = new ArrayList();

         for (CosmeticItemInfo var5 : this.items) {
            if (!this.overrides.method6459(var5.getText())
               && (
                  this.overrides.method6460(var5).toLowerCase(Locale.ROOT).contains(var2)
                     || var5.method7749().toLowerCase(Locale.ROOT).contains(var2)
                     || method3011(var5).toLowerCase(Locale.ROOT).contains(var2)
               )) {
               var3.add(var5);
            }
         }

         return List.copyOf(var3);
      } else {
         return this.items;
      }
   }

   public CosmeticItemInfo method2971(String var1) {
      return var1 == null ? null : this.items2.get(var1);
   }

   public boolean method2972() {
      return !this.items.isEmpty();
   }

   public void method2973() {
      this.index.method1454();
      this.method3009(this::reload);
   }

   public Index getIndex() {
      return this.index;
   }

   public Overrides getOverrides() {
      return this.overrides;
   }

   public Presets getPresets() {
      return this.presets;
   }

   public boolean method2974(String var1) {
      return this.overrides.method6459(var1);
   }

   public void method2975(String var1, boolean var2) {
      Overrides.Data var3 = this.overrides.method6458(var1);
      this.overrides.method6464(var1, method2978(var3, var3 == null ? null : var3.getCosmeticType(), var2, method2979(var3), method2980(var3)));
      if (var2) {
         this.method2994(var1);
      }

      this.method2981(var1);
   }

   public int method2976() {
      int var1 = 0;

      for (CosmeticItemInfo var3 : this.items) {
         if (this.overrides.method6459(var3.getText())) {
            this.method2975(var3.getText(), false);
            var1++;
         }
      }

      if (var1 > 0) {
         this.intValue++;
      }

      return var1;
   }

   public void rename(String var1, String var2) {
      Overrides.Data var3 = this.overrides.method6458(var1);
      this.overrides
         .method6464(
            var1,
            method2978(var3, var3 == null ? null : var3.getCosmeticType(), var3 != null && var3.isFlag(), var2 == null ? "" : var2.strip(), method2980(var3))
         );
      this.intValue++;
   }

   public void method2977(String var1, String var2) {
      Overrides.Data var3 = this.overrides.method6458(var1);
      this.overrides
         .method6464(
            var1,
            method2978(var3, var3 == null ? null : var3.getCosmeticType(), var3 != null && var3.isFlag(), method2979(var3), var2 == null ? "" : var2.strip())
         );
      this.intValue++;
   }

   private static Overrides.Data method2978(Overrides.Data var0, CosmeticType var1, boolean var2, String var3, String var4) {
      return new Overrides.Data(
         var1,
         var0 == null ? new float[3] : var0.getFloatValues(),
         var0 == null ? new float[3] : var0.getFloatValues2(),
         var0 == null ? new float[]{1.0F, 1.0F, 1.0F} : var0.getFloatValues3(),
         var2,
         var3,
         var4
      );
   }

   private static String method2979(Overrides.Data var0) {
      return var0 != null && var0.name() != null ? var0.name() : "";
   }

   private static String method2980(Overrides.Data var0) {
      return var0 != null && var0.getText2() != null ? var0.getText2() : "";
   }

   public void method2981(String var1) {
      if (var1 != null) {
         synchronized (this.linkedHashMap) {
            this.linkedHashMap.remove(var1);
            this.items5.remove(var1);
         }

         this.items7.remove(var1);
         this.method2990(var1);
      }
   }

   public long method2982() {
      long var1 = this.index.method1461();
      this.items7.clear();
      this.items8.clear();
      synchronized (this.linkedHashMap) {
         this.linkedHashMap.clear();
         this.items5.clear();
      }

      this.cosmeticTextureCache.releaseAll();
      this.method3009(this::reload);
      return var1;
   }

   public boolean method2983(CosmeticItemInfo var1) {
      return var1 != null && this.index.method1459(var1);
   }

   public CosmeticModel method2984(String var1) {
      if (var1 == null) {
         return null;
      } else {
         synchronized (this.linkedHashMap) {
            CosmeticModel var3 = this.linkedHashMap.get(var1);
            if (var3 != null) {
               this.items5.put(var1, System.nanoTime());
            }

            return var3;
         }
      }
   }

   public boolean method2985(String var1) {
      return var1 != null && this.items6.contains(var1);
   }

   public int method2986() {
      synchronized (this.linkedHashMap) {
         return this.linkedHashMap.size();
      }
   }

   public int method2987() {
      return this.items6.size();
   }

   public int method2988() {
      return this.items7.size();
   }

   public boolean method2989(String var1) {
      return var1 != null && this.items7.contains(var1);
   }

   public void method2990(String var1) {
      if (this.method2972() && var1 != null && !var1.isBlank() && !this.items7.contains(var1)) {
         Cosmetics.Data var2 = this.items8.get(var1);
         if (var2 == null || System.currentTimeMillis() >= var2.getLongValue()) {
            synchronized (this.linkedHashMap) {
               if (this.linkedHashMap.containsKey(var1)) {
                  return;
               }
            }

            if (this.items6.add(var1)) {
               if (!this.method3009(new Cosmetics.Entry(var1))) {
                  this.items6.remove(var1);
               }
            }
         }
      }
   }

   public Set<String> getItems10() {
      return this.items10;
   }

   public List<String> getItems11() {
      return this.items11;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public boolean method2991(String var1) {
      return var1 != null && this.items10.contains(var1);
   }

   public void method2992(String var1) {
      if (this.method2971(var1) != null) {
         if (this.items9.add(var1)) {
            this.method2996();
         }

         this.method2990(var1);
      }
   }

   public void method2993(Collection<String> var1) {
      LinkedHashSet var2 = new LinkedHashSet();
      if (var1 != null) {
         for (String var4 : var1) {
            if (var4 != null && !var4.isBlank() && this.method2971(var4) != null) {
               var2.add(var4);
            }
         }
      }

      if (!var2.equals(this.items9)) {
         this.items9.clear();
         this.items9.addAll(var2);
         this.method2996();
      }
   }

   public void method2994(String var1) {
      if (var1 != null && this.items9.remove(var1)) {
         this.method2996();
      }
   }

   public void method2995() {
      if (!this.items9.isEmpty()) {
         this.items9.clear();
         this.method2996();
      }
   }

   private void method2996() {
      this.items10 = Collections.unmodifiableSet(new LinkedHashSet<>(this.items9));
      this.items11 = List.copyOf(this.items9);
      this.intValue++;

      for (String var2 : this.items9) {
         this.method2990(var2);
      }

      this.method3002();
   }

   public JsonObject save() {
      JsonObject var1 = new JsonObject();
      JsonArray var2 = new JsonArray();

      for (String var4 : this.items9) {
         var2.add(var4);
      }

      var1.add("equipped", var2);
      return var1;
   }

   public void load(JsonObject var1) {
      this.items9.clear();
      if (var1 != null && var1.has("equipped") && var1.get("equipped").isJsonArray()) {
         for (JsonElement var3 : var1.getAsJsonArray("equipped")) {
            if (var3.isJsonPrimitive()) {
               this.items9.add(var3.getAsString());
            }
         }
      }

      this.items10 = Collections.unmodifiableSet(new LinkedHashSet<>(this.items9));
      this.items11 = List.copyOf(this.items9);
      this.intValue++;
      if (this.method2972()) {
         for (String var5 : this.items9) {
            this.method2990(var5);
         }
      }
   }

   public String url() {
      String var1 = System.getProperty("zenith.cosmetics.url", "").strip();
      if (!var1.isEmpty()) {
         return var1;
      } else {
         return !this.url.isBlank() ? this.url : this.method2997().get(0);
      }
   }

   public List<String> method2997() {
      String var1 = System.getProperty("zenith.cosmetics.url", "").strip();
      if (!var1.isEmpty()) {
         return List.of(var1);
      } else if (!this.url.isBlank()) {
         return List.of(this.url);
      } else {
         ArrayList<String> var2 = new ArrayList<>();
         Zenith var3 = Zenith.getInstance();
         Cloud var4 = var3 == null ? null : var3.getCloud();
         if (var4 != null) {
            String var5 = StoreApi.method1994(var4.method7870());
            if (!var5.isEmpty()) {
               var2.add(var5);
            }
         }

         for (String var6 : RegionUtil.method2040()) {
            String var7 = StoreApi.method1994(var6);
            if (!var7.isEmpty() && !var2.contains(var7)) {
               var2.add(var7);
            }
         }

         return var2;
      }
   }

   public String method2998() {
      return this.index.getLastError();
   }

   public String method2999() {
      if (!System.getProperty("zenith.cosmetics.url", "").strip().isEmpty()) {
         return "-Dzenith.cosmetics.url";
      } else {
         return !this.url.isBlank() ? "cosmetics.json" : "узел облака Nexus";
      }
   }

   public void method3000(String var1) {
      this.url = var1 == null ? "" : var1.strip();
      this.method3002();
      this.method2973();
   }

   private void method3001() {
      JsonObject var1 = JsonIo.method1009(this.path2);
      if (var1 != null) {
         if (var1.has("url")) {
            this.url = var1.get("url").getAsString();
         }

         if (var1.has("secret")) {
            this.text5 = var1.get("secret").getAsString();
         }

         if (var1.has("petScale")) {
            this.floatValue = method3004(var1.get("petScale").getAsFloat());
         }

         if (var1.has("equipped")) {
            this.load(var1);
         }
      }
   }

   private void method3002() {
      JsonObject var1 = this.save();
      var1.addProperty("url", this.url);
      var1.addProperty("secret", this.text5);
      var1.addProperty("petScale", this.floatValue);
      JsonIo.method1010(this.path2, var1);
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public void method3003(float var1) {
      this.floatValue = method3004(var1);
      this.method3002();
   }

   private static float method3004(float var0) {
      return Math.max(0.4F, Math.min(4.0F, var0));
   }

   public String getText5() {
      return this.text5;
   }

   public void method3005(String var1) {
      this.text5 = var1 == null ? "" : var1.strip();
      this.method3002();
   }

   public void method3006(Consumer<String> var1) {
      JsonObject var2 = this.overrides.getJsonObject2();
      if (var2.isEmpty()) {
         var1.accept("Править нечего: своих правок нет");
      } else if (this.text5.isBlank()) {
         var1.accept("Нужен секрет узла: .cosmetics secret <секрет>");
      } else {
         int var3 = var2.size();
         byte[] var4 = var2.toString().getBytes(StandardCharsets.UTF_8);
         this.method3009(() -> {
            String var4x = this.index.method1456(var4, this.text5);
            byte[] var5 = var4x == null ? null : this.index.method1455();
            mc.execute(() -> {
               if (var4x == null) {
                  var1.accept("Не отправилось: " + this.index.getLastError());
               } else {
                  if (var5 != null) {
                     this.overrides.method6466(var5);
                  }

                  var1.accept("Применено всем: " + var3 + " " + (var3 == 1 ? "вещь" : "вещей"));
               }
            });
         });
      }
   }

   private void method3007() {
      byte[] var1 = this.index.method1455();
      if (var1 != null) {
         mc.execute(() -> {
            int var2 = this.overrides.method6466(var1);
            if (var2 >= 0) {
               this.method3008();
            }
         });
      }
   }

   private void method3008() {
      List<String> var1 = List.copyOf(this.items9);
      synchronized (this.linkedHashMap) {
         this.linkedHashMap.clear();
         this.items5.clear();
      }

      this.items7.clear();

      for (Object var3_o : (Iterable<?>)(Object)(var1)) {
         String var3 = (String)var3_o;
         this.method2990(var3);
      }

      this.intValue++;
   }

   public void shutdown() {
      this.executorService.shutdownNow();
      this.cosmeticTextureCache.releaseAll();
      synchronized (this.linkedHashMap) {
         this.linkedHashMap.clear();
         this.items5.clear();
      }
   }

   private boolean method3009(Runnable var1) {
      try {
         this.executorService.execute(var1);
         return true;
      } catch (RejectedExecutionException var3) {
         return false;
      }
   }

   private void reload() {
      var var1 = this.index.method1453();
      mc.execute(() -> this.method3010(var1));
      this.method3007();
   }

   private void method3010(List<CosmeticItemInfo> var1) {
      if (var1.isEmpty()) {
         logger.warn("Косметика: каталог пуст — узел недоступен и кеша нет");
         String var10 = this.index.getLastError();
         logger.warn("Косметика: каталог не приехал — " + (var10.isEmpty() ? "узел недоступен" : var10));
      } else {
         LinkedHashMap var2 = new LinkedHashMap();
         HashMap var3 = new HashMap(var1.size() * 2);
         int var4 = 0;

         for (CosmeticItemInfo var6 : var1) {
            if (!var6.method7750()) {
               var4++;
            } else {
               var3.putIfAbsent(var6.getText(), var6);
               ((java.util.List)var2.computeIfAbsent(method3011(var6), var0 -> new ArrayList<>())).add(var6);
            }
         }

         if (var2.isEmpty()) {
            logger.warn("Косметика: в каталоге {} записей, и ни одной пригодной", var1.size());
         } else {
            var var11 = Comparator.comparing(CosmeticItemInfo::method7749, String.CASE_INSENSITIVE_ORDER).thenComparing(CosmeticItemInfo::getText);
            List<CosmeticItemInfo> var12 = new ArrayList<>(var1.size());
            LinkedHashMap var7 = new LinkedHashMap();

            for (Object var9_o : (Iterable<?>)(Object)(var2.entrySet())) {
               Map.Entry var9 = (Map.Entry)var9_o;
               ((List)var9.getValue()).sort(var11);
               var7.put((String)var9.getKey(), List.copyOf((Collection)var9.getValue()));
               var12.addAll((Collection)var9.getValue());
            }

            this.items7.clear();
            this.items8.clear();
            this.items = List.copyOf(var12);
            this.items2 = Map.copyOf(var3);
            this.items4 = Collections.unmodifiableMap(var7);
            this.items3 = List.copyOf(var7.keySet());
            logger.info("Косметика: каталог {} элементов, {} категорий (пропущено непригодных: {})", new Object[]{this.items.size(), this.items3.size(), var4});
            this.intValue++;

            for (String var14 : this.items9) {
               this.method2990(var14);
            }
         }
      }
   }

   private static String method3011(CosmeticItemInfo var0) {
      if (var0.getText3() != null && !var0.getText3().isBlank()) {
         return var0.getText3();
      } else {
         return var0.getText4() != null && !var0.getText4().isBlank() ? var0.getText4() : "прочее";
      }
   }

   private void load(String var1) {
      Cosmetics.Data2 var2 = null;
      boolean var3 = false;

      try {
         var2 = this.method3012(this.method2971(var1));
      } catch (Exception var6) {
         var3 = true;
         logger.warn("Косметика: {} не загрузился: {}", var1, var6.toString());
      }

      Cosmetics.Data2 var4 = var2;
      boolean var5 = var3;
      mc.execute(() -> this.method3013(var1, var4, var5));
   }

   private Cosmetics.Data2 method3012(CosmeticItemInfo var1) throws Exception {
      if (var1 == null) {
         return null;
      } else {
         CosmeticFileLoader var2 = CosmeticFileLoader.method7345(new ByteArrayInputStream(this.index.method1458(var1)));

         CosmeticSkeleton var3 = switch (var2.getType()) {
            case WAVEFRONT -> ObjParser.method8021(var2.method7339(var2.getText()), var2.method7339(var2.getText3()));
            case BEDROCK -> GeometryParser.method2261(var2.method7339(var2.getText()), var1.getText7());
            case TECHNE -> TechneParser.method7431(var2.method7339(var2.getText()), var1.getText7());
            case EMPTY -> null;
         };
         if (var3 != null && var3.method7138() != 0) {
            String[] var4 = var3.getTexts();
            byte[][] var5 = new byte[Math.max(1, var4.length)][];

            for (int var6 = 0; var6 < var5.length; var6++) {
               String var7 = var6 < var4.length && !var4[var6].isBlank() ? var4[var6] : var1.getText7();
               var5[var6] = var2.method7340(var7);
            }

            String var8 = var2.method7339(var2.getText2());
            CosmeticAnimation var9 = var8 == null ? null : CosmeticAnimation.method7814(var8);
            return new Cosmetics.Data2(var1, var3, var5, var9);
         } else {
            return null;
         }
      }
   }

   private void method3013(String var1, Cosmetics.Data2 var2, boolean var3) {
      this.items6.remove(var1);
      if (var2 == null) {
         if (!var3 || !this.method3014(var1)) {
            this.items7.add(var1);
            this.items8.remove(var1);
         }
      } else {
         String var4 = var2.getCosmeticItemInfo().getText6();
         String[] var5 = var2.getCosmeticSkeleton().getTexts();
         Identifier[] var6 = new Identifier[var2.getByteValues().length];

         for (int var7 = 0; var7 < var6.length; var7++) {
            String var8 = var7 < var5.length ? var5[var7] : "";
            var6[var7] = this.cosmeticTextureCache.method2616(CosmeticTextureCache.method2623(var4, var8), var2.getByteValues()[var7]);
         }

         if (var6[0] == null) {
            this.items7.add(var1);
            this.items8.remove(var1);
         } else {
            for (int var11 = 1; var11 < var6.length; var11++) {
               if (var6[var11] == null) {
                  var6[var11] = var6[0];
               }
            }

            this.items8.remove(var1);
            CosmeticModel var12 = new CosmeticModel(var2.getCosmeticItemInfo(), var2.getCosmeticSkeleton(), var6, var2.getCosmeticAnimation(), this.overrides);
            synchronized (this.linkedHashMap) {
               this.linkedHashMap.put(var1, var12);
               this.method3015();
            }
         }
      }
   }

   private boolean method3014(String var1) {
      Cosmetics.Data var2 = this.items8.get(var1);
      int var3 = var2 == null ? 1 : var2.getIntValue() + 1;
      if (var3 >= 3) {
         return false;
      } else {
         this.items8.put(var1, new Cosmetics.Data(var3, System.currentTimeMillis() + 4000L * var3));
         return true;
      }
   }

   private void method3015() {
      if (this.linkedHashMap.size() > 64) {
         long var1 = System.nanoTime();
         var var3 = this.linkedHashMap.entrySet().iterator();

         while (this.linkedHashMap.size() > 64 && var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            String var5 = (String)var4.getKey();
            if (!this.items9.contains(var5) && !this.method3016(var5, var1)) {
               String var6 = ((CosmeticModel)var4.getValue()).getCosmeticItemInfo().getText6();
               var3.remove();
               this.items5.remove(var5);
               if (!this.method3017(var6)) {
                  this.cosmeticTextureCache.method2622(var6);
               }
            }
         }
      }
   }

   private boolean method3016(String var1, long var2) {
      Long var4 = this.items5.get(var1);
      return var4 != null && var2 - var4 < 250000000L;
   }

   private boolean method3017(String var1) {
      for (CosmeticModel var3 : this.linkedHashMap.values()) {
         if (var3.getCosmeticItemInfo().getText6().equals(var1)) {
            return true;
         }
      }

      return false;
   }

   private static final class Data {
      private final int intValue;
      private final long longValue;

      public int intValue() {
         return this.intValue;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "longValue=" + this.longValue + "]";
      }

      private Data(int var1, long var2) {
         this.intValue = var1;
         this.longValue = var2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Data2 {
      private final CosmeticItemInfo cosmeticItemInfo;
      private final CosmeticSkeleton cosmeticSkeleton;
      private final byte[][] byteValues;
      private final CosmeticAnimation cosmeticAnimation;

      public CosmeticItemInfo cosmeticItemInfo() {
         return this.cosmeticItemInfo;
      }

      public CosmeticSkeleton cosmeticSkeleton() {
         return this.cosmeticSkeleton;
      }

      public byte[][] byteValues() {
         return this.byteValues;
      }

      public CosmeticAnimation cosmeticAnimation() {
         return this.cosmeticAnimation;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.cosmeticItemInfo, other.cosmeticItemInfo) && java.util.Objects.equals(this.cosmeticSkeleton, other.cosmeticSkeleton) && java.util.Objects.equals(this.byteValues, other.byteValues) && java.util.Objects.equals(this.cosmeticAnimation, other.cosmeticAnimation);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(cosmeticItemInfo, cosmeticSkeleton, byteValues, cosmeticAnimation);
      }

      @Override
      public String toString() {
         return "Data2[" + "cosmeticItemInfo=" + this.cosmeticItemInfo + ", " + "cosmeticSkeleton=" + this.cosmeticSkeleton + ", " + "byteValues=" + this.byteValues + ", " + "cosmeticAnimation=" + this.cosmeticAnimation + "]";
      }

      private Data2(CosmeticItemInfo var1, CosmeticSkeleton var2, byte[][] var3, CosmeticAnimation var4) {
         this.cosmeticItemInfo = var1;
         this.cosmeticSkeleton = var2;
         this.byteValues = var3;
         this.cosmeticAnimation = var4;
      }

      public CosmeticItemInfo getCosmeticItemInfo() {
         return this.cosmeticItemInfo;
      }

      public CosmeticSkeleton getCosmeticSkeleton() {
         return this.cosmeticSkeleton;
      }

      public byte[][] getByteValues() {
         return this.byteValues;
      }

      public CosmeticAnimation getCosmeticAnimation() {
         return this.cosmeticAnimation;
      }
   }

   private final class Entry implements Runnable {
      private final String text;

      private Entry(String var2) {
         this.text = var2;
      }

      @Override
      public void run() {
         Cosmetics.this.load(this.text);
      }

      private void method3019() {
         Cosmetics.this.items6.remove(this.text);
      }
   }
}

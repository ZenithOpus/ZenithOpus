package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.Class15;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.misc.Rarity;
import org.zenith.utility.misc.WardrobeCategory;
import org.zenith.utility.misc.WardrobeSetInfo;
import org.zenith.utility.render.ModelJsonParser;

public final class Registry {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Wardrobe");
   private static final String text = "/assets/zenith/wardrobe/";
   private final Map<String, HoveredData> items = new LinkedHashMap<>();
   private final Map<String, WardrobeSetInfo> items2 = new LinkedHashMap<>();
   private final Map<String, Class15> items3 = new ConcurrentHashMap<>();
   private final Set<String> items4 = ConcurrentHashMap.newKeySet();
   private final Set<String> items5 = ConcurrentHashMap.newKeySet();
   private final Map<String, Long> items6 = new ConcurrentHashMap<>();
   private final Queue<Registry.Data> queue = new ConcurrentLinkedQueue<>();
   private final Index2 index2 = new Index2();
   private volatile String text2;
   private final ExecutorService executorService = Executors.newFixedThreadPool(2, var0 -> {
      Thread var1 = new Thread(var0, "zenith-wardrobe-loader");
      var1.setDaemon(true);
      var1.setPriority(1);
      return var1;
   });

   public void load() {
      this.items.clear();
      this.items2.clear();
      this.items3.clear();
      this.items4.clear();
      JsonObject var1 = method1429("/assets/zenith/wardrobe/registry.json");
      if (var1 == null) {
         logger.warn("Гардероб: registry.json не найден");
      } else {
         for (JsonElement var4 : var1.has("items") && var1.get("items").isJsonArray() ? var1.getAsJsonArray("items") : new JsonArray()) {
            if (var4.isJsonObject()) {
               JsonObject var5 = var4.getAsJsonObject();
               String var6 = method1430(var5, "id");
               FocusSlotEnum var7 = FocusSlotEnum.method546(method1430(var5, "slot"));
               if (var6 != null && var7 != null) {
                  String var8 = method1430(var5, "model");
                  this.items
                     .put(
                        var6,
                        new HoveredData(
                           var6,
                           var7,
                           Rarity.method4709(method1430(var5, "rarity")),
                           method1430(var5, "set"),
                           var8 == null ? var6 : var8,
                           method1430(var5, "theme")
                        )
                     );
               }
            }
         }

         for (JsonElement var14 : var1.has("sets") && var1.get("sets").isJsonArray() ? var1.getAsJsonArray("sets") : new JsonArray()) {
            if (var14.isJsonObject()) {
               JsonObject var15 = var14.getAsJsonObject();
               String var16 = method1430(var15, "id");
               if (var16 != null) {
                  ArrayList var17 = new ArrayList();

                  for (HoveredData var10 : this.items.values()) {
                     if (var16.equals(var10.getText2())) {
                        var17.add(var10.getText());
                     }
                  }

                  int var18 = 16777215;

                  try {
                     var18 = Integer.parseInt(method1430(var15, "color").replace("#", ""), 16);
                  } catch (RuntimeException var11) {
                     var18 = 16777215;
                  }

                  this.items2.put(var16, new WardrobeSetInfo(var16, Collections.unmodifiableList(var17), var18, method1430(var15, "particle")));
               }
            }
         }

         logger.info("Гардероб: {} предметов, {} сетов", this.items.size(), this.items2.size());
      }
   }

   public HoveredData method1421(String var1) {
      return var1 == null ? null : this.items.get(var1);
   }

   public void method1422(MinecraftClient var1, int var2) {
      if (var1.getTextureManager() != null) {
         Registry.Data var3;
         for (int var4 = 0; var4 < var2 && (var3 = this.queue.poll()) != null; var4++) {
            var1.getTextureManager()
               .registerTexture(var3.getClass15().identifier, new NativeImageBackedTexture(() -> "zenith wardrobe", var3.getNativeImage()));
            this.items3.put(var3.name(), var3.getClass15());
            this.items5.remove(var3.name());
         }
      }
   }

   private void method1423(String var1) {
      Long var2 = this.items6.get(var1);
      if (var2 == null || System.nanoTime() - var2 >= 15000000000L) {
         if (this.items5.add(var1)) {
            this.executorService.execute(() -> this.load(var1));
         }
      }
   }

   private void load(String var1) {
      try {
         Index2.Data var2 = this.index2.method6140(var1);
         JsonObject var9 = JsonParser.parseString(new String(var2.getByteValues(), StandardCharsets.UTF_8)).getAsJsonObject();
         Identifier var4 = Identifier.of("zenith", "wardrobe/dyn/" + var1);
         Class15 var5 = ModelJsonParser.method107(var1, var9, var4);
         NativeImage var6 = NativeImage.read(var2.getByteValues2());
         this.items6.remove(var1);
         this.queue.add(new Registry.Data(var1, var5, var6));
         this.text2 = null;
      } catch (Index2.Entry var7) {
         this.items6.put(var1, System.nanoTime());
         this.items5.remove(var1);
         String var3 = var7.getMessage();
         if (!Objects.equals(var3, this.text2)) {
            this.text2 = var3;
            logger.warn("Гардероб: модель {} отложена — {}", var1, var3);
         }
      } catch (LinkageError | Exception var8) {
         this.items4.add(var1);
         this.items5.remove(var1);
         logger.error("Гардероб: модель {} не загружена", var1, var8);
      }
   }

   public List<HoveredData> all() {
      return new ArrayList<>(this.items.values());
   }

   public List<HoveredData> method1424(WardrobeCategory var1) {
      ArrayList<HoveredData> var2 = new ArrayList<>();

      for (HoveredData var4 : this.items.values()) {
         if (var4.method1546() == var1) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public WardrobeSetInfo method1425(String var1) {
      return var1 == null ? null : this.items2.get(var1);
   }

   public List<WardrobeSetInfo> method1426() {
      return new ArrayList<>(this.items2.values());
   }

   public Class15 method1427(HoveredData var1) {
      if (var1 == null) {
         return null;
      } else {
         Class15 var2 = this.items3.get(var1.getText3());
         if (var2 == null && !this.items4.contains(var1.getText3())) {
            this.method1423(var1.getText3());
         }

         return var2;
      }
   }

   public void method1428() {
      this.items3.clear();
      this.items4.clear();
      this.items6.clear();
   }

   private static JsonObject method1429(String var0) {
      try {
         JsonObject var4;
         try (InputStream var1 = Registry.class.getResourceAsStream(var0)) {
            if (var1 == null) {
               return null;
            }

            try (InputStreamReader var2 = new InputStreamReader(var1, StandardCharsets.UTF_8)) {
               JsonElement var3 = JsonParser.parseReader(var2);
               var4 = var3.isJsonObject() ? var3.getAsJsonObject() : null;
            }
         }

         return var4;
      } catch (RuntimeException | IOException var9) {
         logger.error("Гардероб: не прочитан {}", var0, var9);
         return null;
      }
   }

   private static String method1430(JsonObject var0, String var1) {
      try {
         return var0.has(var1) && var0.get(var1).isJsonPrimitive() ? var0.get(var1).getAsString() : null;
      } catch (RuntimeException var3) {
         return null;
      }
   }

   private static final class Data {
      private final String text;
      private final Class15 class15;
      private final NativeImage nativeImage;

      public String text() {
         return this.text;
      }

      public Class15 class15() {
         return this.class15;
      }

      public NativeImage nativeImage() {
         return this.nativeImage;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.class15, other.class15) && java.util.Objects.equals(this.nativeImage, other.nativeImage);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, class15, nativeImage);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "class15=" + this.class15 + ", " + "nativeImage=" + this.nativeImage + "]";
      }

      private Data(String var1, Class15 var2, NativeImage var3) {
         this.text = var1;
         this.class15 = var2;
         this.nativeImage = var3;
      }

      public String name() {
         return this.text;
      }

      public Class15 getClass15() {
         return this.class15;
      }

      public NativeImage getNativeImage() {
         return this.nativeImage;
      }
   }
}

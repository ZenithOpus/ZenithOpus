package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.item.ItemStack;
import org.zenith.Zenith;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.FocusSlotEnum;
import org.zenith.utility.misc.HoveredData;
import org.zenith.utility.misc.WardrobeCategory;
import org.zenith.utility.misc.WardrobeSetInfo;

public final class Wardrobe {
   private static final int CONST_intValue = 1;
   private static final FocusSlotEnum[] focusSlotEnums = FocusSlotEnum.values();
   private static final long CONST_longValue = 500L;
   private static final long CONST_longValue2 = 10000L;
   public final BooleanSetting showSelf = new BooleanSetting("wardrobe.showSelf", "wardrobe.showSelf.desc", true);
   public final BooleanSetting showOthers = new BooleanSetting("wardrobe.showOthers", "wardrobe.showOthers.desc", true);
   public final BooleanSetting particles = new BooleanSetting("wardrobe.particles", "wardrobe.particles.desc", true);
   public final BooleanSetting armorOnSkins = new BooleanSetting("wardrobe.armorOnSkins", "wardrobe.armorOnSkins.desc", false);
   public final BooleanSetting clothesOverArmor = new BooleanSetting("wardrobe.clothesOverArmor", "wardrobe.clothesOverArmor.desc", true);
   public final SliderSetting petScale = new SliderSetting("wardrobe.petScale", 1.0F, 0.5F, 2.0F, 0.05F, "wardrobe.petScale.desc", "x", null, null);
   public final SliderSetting particleBudget = new SliderSetting(
      "wardrobe.particleBudget", 60.0F, 10.0F, 200.0F, 5.0F, "wardrobe.particleBudget.desc", "/s", null, null
   );
   private final Registry registry = new Registry();
   private final Path path;
   private final EnumMap<FocusSlotEnum, String> enumMap = new EnumMap<>(FocusSlotEnum.class);
   private final Map<String, EnumMap<FocusSlotEnum, String>> items = new HashMap<>();
   private final List<HoveredData> items2 = new ArrayList<>(8);
   private final List<Wardrobe.Data> items3 = new ArrayList<>();
   private HoveredData hoveredData;
   private boolean flag;
   private int version;
   private String text = "";
   private long longValue;

   public Wardrobe(Path var1) {
      this.path = var1.resolve("wardrobe").resolve("wardrobe.json");
      this.registry.load();
      this.load();
      this.text = this.method2514();
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)var1x -> {
         this.tick();
         if (var1x.getOverlay() == null) {
            this.registry.method1422(var1x, 2);
         }
      });
      ClientLifecycleEvents.CLIENT_STOPPING.register((ClientStopping)var1x -> {
         if (!this.method2514().equals(this.text)) {
            this.method2518();
         }
      });
   }

   public static Wardrobe method2488() {
      Zenith var0 = Zenith.getInstance();
      return var0 == null ? null : var0.getWardrobe();
   }

   public Registry getRegistry() {
      return this.registry;
   }

   public List<Setting> method2489() {
      return List.of(this.showSelf, this.showOthers, this.particles, this.armorOnSkins, this.clothesOverArmor, this.petScale, this.particleBudget);
   }

   public int version() {
      return this.version;
   }

   public boolean method2490(HoveredData var1) {
      return var1 != null && var1.getText().equals(this.enumMap.get(var1.getFocusSlotEnum()));
   }

   public HoveredData method2491(FocusSlotEnum var1) {
      return this.registry.method1421(this.enumMap.get(var1));
   }

   public void method2492(HoveredData var1) {
      if (var1 != null) {
         this.enumMap.put(var1.getFocusSlotEnum(), var1.getText());
         if (var1.getFocusSlotEnum() == FocusSlotEnum.CAPE) {
            this.enumMap.remove(FocusSlotEnum.BACK);
         } else if (var1.getFocusSlotEnum() == FocusSlotEnum.BACK) {
            this.enumMap.remove(FocusSlotEnum.CAPE);
         }

         this.changed();
      }
   }

   public void method2493(FocusSlotEnum var1) {
      if (this.enumMap.remove(var1) != null) {
         this.changed();
      }
   }

   public void method2494(HoveredData var1) {
      if (this.method2490(var1)) {
         this.method2493(var1.getFocusSlotEnum());
      } else {
         this.method2492(var1);
      }
   }

   public void method2495(WardrobeSetInfo var1) {
      if (var1 != null) {
         for (String var3 : var1.pieces()) {
            HoveredData var4 = this.registry.method1421(var3);
            if (var4 != null) {
               this.enumMap.put(var4.getFocusSlotEnum(), var4.getText());
               if (var4.getFocusSlotEnum() == FocusSlotEnum.CAPE) {
                  this.enumMap.remove(FocusSlotEnum.BACK);
               } else if (var4.getFocusSlotEnum() == FocusSlotEnum.BACK) {
                  this.enumMap.remove(FocusSlotEnum.CAPE);
               }
            }
         }

         this.enumMap.remove(FocusSlotEnum.CHARACTER);
         this.changed();
      }
   }

   public void method2496() {
      if (!this.enumMap.isEmpty()) {
         this.enumMap.clear();
         this.changed();
      }
   }

   public int method2497() {
      return this.enumMap.size();
   }

   public void setHoveredData(HoveredData var1) {
      this.hoveredData = var1;
   }

   public void setFlag(boolean var1) {
      this.flag = var1;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public List<HoveredData> method2498(String var1, boolean var2) {
      this.items2.clear();
      EnumMap var3 = var2 ? this.enumMap : (var1 == null ? null : this.items.get(var1.toLowerCase(Locale.ROOT)));
      if (var3 == null) {
         return this.items2;
      } else {
         HoveredData var4 = var2 && this.flag ? this.hoveredData : null;

         for (FocusSlotEnum var8 : focusSlotEnums) {
            HoveredData var9 = var4 != null && var4.getFocusSlotEnum() == var8 ? var4 : this.registry.method1421((String)var3.get(var8));
            if (var9 != null) {
               this.items2.add(var9);
            }
         }

         HoveredData var10 = this.method2499(this.items2, FocusSlotEnum.CAPE);
         HoveredData var11 = this.method2499(this.items2, FocusSlotEnum.BACK);
         if (var10 != null && var11 != null) {
            this.items2.remove(var4 == var11 ? var10 : var11);
         }

         return this.items2;
      }
   }

   public HoveredData method2499(List<HoveredData> var1, FocusSlotEnum var2) {
      for (int var3 = 0; var3 < var1.size(); var3++) {
         if (((HoveredData)var1.get(var3)).getFocusSlotEnum() == var2) {
            return (HoveredData)var1.get(var3);
         }
      }

      return null;
   }

   public WardrobeSetInfo method2500(List<HoveredData> var1) {
      if (this.method2499(var1, FocusSlotEnum.CHARACTER) != null) {
         return null;
      } else {
         String var2 = null;
         int var3 = 0;

         for (HoveredData var5 : var1) {
            if (var5.method1546() == WardrobeCategory.CLOTHING) {
               if (var5.getText2() == null) {
                  return null;
               }

               if (var2 == null) {
                  var2 = var5.getText2();
               } else if (!var2.equals(var5.getText2())) {
                  return null;
               }

               var3++;
            }
         }

         WardrobeSetInfo var6 = this.registry.method1425(var2);
         return var6 != null && var3 == var6.pieces().size() && var3 > 0 ? var6 : null;
      }
   }

   public void method2501(String var1, Map<FocusSlotEnum, String> var2) {
      if (var1 != null) {
         String var3 = var1.toLowerCase(Locale.ROOT);
         if (var2 != null && !var2.isEmpty()) {
            this.items.put(var3, new EnumMap<>(var2));
         } else {
            this.items.remove(var3);
         }
      }
   }

   public void method2502() {
      this.items.clear();
   }

   public int method2503(Collection<String> var1) {
      EnumMap var2 = new EnumMap<>(FocusSlotEnum.class);

      for (String var4 : var1) {
         HoveredData var5 = this.registry.method1421(var4);
         if (var5 != null) {
            var2.put(var5.getFocusSlotEnum(), var5.getText());
         }
      }

      if (var2.containsKey(FocusSlotEnum.CAPE)) {
         var2.remove(FocusSlotEnum.BACK);
      }

      this.enumMap.clear();
      this.enumMap.putAll(var2);
      this.changed();
      return var2.size();
   }

   public Map<FocusSlotEnum, String> method2504() {
      return new EnumMap<>(this.enumMap);
   }

   public List<Wardrobe.Data> method2505() {
      return List.copyOf(this.items3);
   }

   public Wardrobe.Data method2506(String var1) {
      String var2 = var1 == null ? "" : var1.strip();
      if (!var2.isEmpty() && !this.enumMap.isEmpty()) {
         Wardrobe.Data var3 = new Wardrobe.Data(var2, new EnumMap<>(this.enumMap), System.currentTimeMillis());
         this.items3.removeIf(var1x -> var1x.name().equalsIgnoreCase(var2));
         this.items3.add(0, var3);
         this.changed();
         return var3;
      } else {
         return null;
      }
   }

   public boolean method2507(String var1) {
      String var2 = var1 == null ? "" : var1.strip();

      for (Wardrobe.Data var4 : this.items3) {
         if (var4.name().equalsIgnoreCase(var2)) {
            return true;
         }
      }

      return false;
   }

   public int method2508(Wardrobe.Data var1) {
      return var1 == null ? 0 : this.method2503(var1.getItems().values());
   }

   public void method2509(Wardrobe.Data var1) {
      if (this.items3.remove(var1)) {
         this.changed();
      }
   }

   public boolean method2510(Wardrobe.Data var1) {
      return var1 != null && !this.enumMap.isEmpty() && this.enumMap.equals(var1.getItems());
   }

   public JsonObject method2511() {
      return this.method2519();
   }

   public void method2512(JsonObject var1) {
      if (var1 != null) {
         this.method2517(var1, var1.has("presets"));
         this.changed();
      }
   }

   public boolean method2513(ItemStack var1) {
      FocusSlotEnum var2 = FocusSlotEnum.method545(var1);
      return var2 != null && this.enumMap.containsKey(var2);
   }

   private void changed() {
      this.version++;
      this.method2518();
   }

   private void tick() {
      long var1 = System.currentTimeMillis();
      if (var1 >= this.longValue && !this.method2514().equals(this.text)) {
         this.longValue = var1 + (this.method2518() ? 500L : 10000L);
      }
   }

   private String method2514() {
      StringBuilder var1 = new StringBuilder();

      for (Setting var3 : this.method2489()) {
         if (var3 instanceof BooleanSetting var4) {
            var1.append(var4.isEnabled()).append('|');
         } else if (var3 instanceof SliderSetting var5) {
            var1.append(var5.getValue3()).append('|');
         }
      }

      return var1.toString();
   }

   private void load() {
      JsonObject var1 = JsonIo.method1009(this.path);
      if (var1 != null) {
         this.method2517(var1, true);
      }
   }

   private EnumMap<FocusSlotEnum, String> method2515(JsonObject var1) {
      EnumMap var2 = new EnumMap<>(FocusSlotEnum.class);

      for (Entry var4 : var1.entrySet()) {
         FocusSlotEnum var5 = FocusSlotEnum.method546((String)var4.getKey());
         if (var5 != null && ((JsonElement)var4.getValue()).isJsonPrimitive()) {
            HoveredData var6 = this.registry.method1421(((JsonElement)var4.getValue()).getAsString());
            if (var6 != null && var6.getFocusSlotEnum() == var5) {
               var2.put(var5, var6.getText());
            }
         }
      }

      return var2;
   }

   private static JsonObject method2516(Map<FocusSlotEnum, String> var0) {
      JsonObject var1 = new JsonObject();

      for (Entry var3 : var0.entrySet()) {
         var1.addProperty(((FocusSlotEnum)var3.getKey()).name(), (String)var3.getValue());
      }

      return var1;
   }

   private void method2517(JsonObject var1, boolean var2) {
      try {
         if (var1.has("equipped") && var1.get("equipped").isJsonObject()) {
            EnumMap var3 = this.method2515(var1.getAsJsonObject("equipped"));
            this.enumMap.clear();
            this.enumMap.putAll(var3);
         }
      } catch (RuntimeException var11) {
         this.enumMap.clear();
      }

      if (var2 && var1.has("presets") && var1.get("presets").isJsonArray()) {
         this.items3.clear();

         for (JsonElement var4 : var1.getAsJsonArray("presets")) {
            try {
               if (var4.isJsonObject()) {
                  JsonObject var5 = var4.getAsJsonObject();
                  String var6 = var5.has("name") ? var5.get("name").getAsString() : "";
                  if (!var6.isBlank() && var5.has("items") && var5.get("items").isJsonObject()) {
                     EnumMap var7 = this.method2515(var5.getAsJsonObject("items"));
                     if (!var7.isEmpty()) {
                        this.items3.add(new Wardrobe.Data(var6, var7, var5.has("createdAt") ? var5.get("createdAt").getAsLong() : 0L));
                     }
                  }
               }
            } catch (RuntimeException var10) {
            }
         }
      }

      JsonObject var13 = var1.has("settings") && var1.get("settings").isJsonObject() ? var1.getAsJsonObject("settings") : new JsonObject();

      for (Setting var15 : this.method2489()) {
         try {
            JsonElement var16 = var13.get(var15.getKey());
            if (var16 != null && var16.isJsonPrimitive()) {
               if (var15 instanceof BooleanSetting var17) {
                  var17.setEnabled(var16.getAsBoolean());
               } else if (var15 instanceof SliderSetting var8) {
                  var8.method2400(var16.getAsFloat());
               }
            }
         } catch (RuntimeException var9) {
         }
      }
   }

   private boolean method2518() {
      if (JsonIo.method1010(this.path, this.method2519())) {
         this.text = this.method2514();
         return true;
      } else {
         return false;
      }
   }

   private JsonObject method2519() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("format", 1);
      var1.add("equipped", method2516(this.enumMap));
      JsonArray var2 = new JsonArray();

      for (Wardrobe.Data var4 : this.items3) {
         JsonObject var5 = new JsonObject();
         var5.addProperty("name", var4.name());
         var5.addProperty("createdAt", var4.getLongValue());
         var5.add("items", method2516(var4.getItems()));
         var2.add(var5);
      }

      var1.add("presets", var2);
      JsonObject var8 = new JsonObject();

      for (Setting var10 : this.method2489()) {
         if (var10 instanceof BooleanSetting var6) {
            var8.addProperty(var10.getKey(), var6.isEnabled());
         } else if (var10 instanceof SliderSetting var7) {
            var8.addProperty(var10.getKey(), var7.getValue3());
         }
      }

      var1.add("settings", var8);
      return var1;
   }

   public static final class Data {
      private final String text;
      private final Map<FocusSlotEnum, String> items;
      private final long longValue;

      public String text() {
         return this.text;
      }

      public Map<FocusSlotEnum, String> items() {
         return this.items;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.items, other.items) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, items, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "items=" + this.items + ", " + "longValue=" + this.longValue + "]";
      }

      public Data(String var1, Map<FocusSlotEnum, String> var2, long var3) {
         this.text = var1;
         this.items = var2;
         this.longValue = var3;
      }

      public String name() {
         return this.text;
      }

      public Map<FocusSlotEnum, String> getItems() {
         return this.items;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }
}

package org.zenith.base;

import com.darkmagician6.eventapi.EventManager;
import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.Generated;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RotationManager;
import org.zenith.Zenith;
import org.zenith.ZenithAccess;
import org.zenith.client.events.ModuleToggleEvent;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.HolyWorldServerTracker;

public class Module implements Comparable<Module>, ZenithAccess {
   public ModuleInfo info = this.getClass().getAnnotation(ModuleInfo.class);
   private final String text;
   private String name;
   private final String nameLower;
   private final ModuleCategory category;
   private final String description;
   private final boolean botModule;
   private volatile boolean enabled;
   private boolean priority = false;
   private int keyCode;
   private int keyModifiers;

   public Module() {
      if (this.info == null) {
         throw new IllegalStateException("ModuleAnnotation is required for " + this.getClass().getName());
      } else {
         this.text = "zenith:" + this.getClass().getSimpleName().toLowerCase(Locale.ROOT);
         this.name = this.info.name();
         this.nameLower = this.name.toLowerCase(Locale.ROOT);
         this.category = this.info.category();
         this.description = this.info.description();
         this.botModule = this.info.method894();
         this.enabled = false;
         this.keyCode = -1;
      }
   }

   public Module(String var1, String var2, String var3, ModuleCategory var4) {
      this.info = null;
      this.text = var1;
      this.name = var2;
      this.nameLower = var2.toLowerCase(Locale.ROOT);
      this.category = var4;
      this.description = var3 == null ? "" : var3;
      this.botModule = false;
      this.enabled = false;
      this.keyCode = -1;
   }

   public boolean isEnabledRaw() {
      return this.enabled;
   }

   public String getDescription() {
      return this.description != null && !this.description.isBlank() ? Zenith.getInstance().getLocalization().translate(this.description) : "";
   }

   public boolean isBotModule() {
      return this.botModule;
   }

   public String getGroup() {
      return this.info == null ? "" : this.info.method895();
   }

   public boolean isHidden() {
      return this.info != null && this.info.method896();
   }

   public List<Module> getSurfacedModules() {
      return Collections.emptyList();
   }

   public ModulePreviewStage.Kind previewKind() {
      return null;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setToggled(boolean var1) {
      if (var1 && !this.isEnabledRaw()) {
         this.toggle();
      }

      if (!var1 && this.isEnabledRaw()) {
         this.toggle();
      }
   }

   public final void disableSelf() {
      this.setToggled(false);
   }

   public void toggle() {
      this.enabled = !this.enabled;
      if (this.enabled) {
         this.onEnable();
      } else {
         this.onDisable();
      }
   }

   public final RotationManager scopedRotationManager() {
      return Zenith.getInstance().getRotationManager();
   }

   public final HolyWorldServerTracker scopedRCTRepository() {
      return Zenith.getInstance().getHolyWorldServerTracker();
   }

   public void onEnable() {
      if (this.isPremium() && Zenith.getInstance().getEntry().method1504() == Zenith.Entry.Type.USER) {
         this.setToggled(false);
         Zenith.getInstance()
            .getHudHolder()
            .method6972(
               this.category.getIcon(),
               Text.of(
                  Text.of("Работает только ")
                     .copy()
                     .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()))
                     .append(
                        Text.of("с Opus / Private")
                           .copy()
                           .setStyle(Style.EMPTY.withColor(zenith.getStyleManager().getCurrentStyle().getTextEnable().getZenithColor().method7287()))
                     )
               )
            );
      } else {
         EventManager.register(this);
         EventManager.call(new ModuleToggleEvent(this, this.enabled));
      }
   }

   public void onDisable() {
      EventManager.unregister(this);
      EventManager.call(new ModuleToggleEvent(this, this.enabled));
   }

   public List<Setting> getSettings() {
      return Arrays.stream(this.getClass().getDeclaredFields()).map(var1 -> {
         try {
            var1.setAccessible(true);
            return var1.get(this);
         } catch (IllegalAccessException var3) {
            var3.printStackTrace();
            return null;
         }
      }).filter(var0 -> var0 instanceof Setting).map(var0 -> (Setting)var0).collect(Collectors.toList());
   }

   public boolean isRenderSetting() {
      return true;
   }

   public JsonObject save() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("enabled", this.enabled);
      var1.addProperty("priority", this.priority);
      var1.addProperty("keyCode", this.keyCode);
      var1.addProperty("keyModifiers", this.keyModifiers);
      JsonObject var2 = new JsonObject();

      for (Setting var4 : this.getSettings()) {
         var4.safe(var2);
      }

      var1.add("Settings", var2);
      return var1;
   }

   public void load(JsonObject var1) {
      if (var1 != null) {
         Boolean var2 = null;

         try {
            var2 = var1.has("enabled") ? var1.get("enabled").getAsBoolean() : null;
            if (var1.has("priority")) {
               this.priority = var1.get("priority").getAsBoolean();
            }

            if (var1.has("keyCode")) {
               this.setKeyCode(var1.get("keyCode").getAsInt());
            }

            this.keyModifiers = var1.has("keyModifiers") ? var1.get("keyModifiers").getAsInt() : 0;
         } catch (Exception var11) {
            var11.printStackTrace();
         }

         JsonObject var3 = null;

         try {
            var3 = var1.getAsJsonObject("Settings");
         } catch (Exception var10) {
            var10.printStackTrace();
         }

         if (var3 != null) {
            for (Setting var5 : this.getSettings()) {
               String var6 = var5.getKey();
               if (var3.has(var6) || var5 instanceof GroupSetting) {
                  try {
                     var5.load(var3);
                  } catch (Exception var9) {
                     System.err.println("[Config] bad value " + this.name + "#" + var6 + ": " + var9);
                  }
               }
            }
         }

         try {
            if (var2 != null) {
               if (var2 && !this.isEnabledRaw()) {
                  this.toggle();
               }

               if (!var2 && this.isEnabledRaw()) {
                  this.toggle();
               }
            }
         } catch (Exception var8) {
            var8.printStackTrace();
         }
      }
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1;
   }

   @Override
   public int hashCode() {
      return System.identityHashCode(this);
   }

   public int compareTo(@NotNull Module var1) {
      return var1.getName().compareTo(this.name);
   }

   public boolean isPremium() {
      return false;
   }

   @Generated
   public ModuleInfo getInfo() {
      return this.info;
   }

   @Generated
   public String getId() {
      return this.text;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public String getNameLower() {
      return this.nameLower;
   }

   @Generated
   public ModuleCategory getCategory() {
      return this.category;
   }

   @Generated
   public boolean isPriority() {
      return this.priority;
   }

   @Generated
   public int getKeyCode() {
      return this.keyCode;
   }

   @Generated
   public int getKeyModifiers() {
      return this.keyModifiers;
   }

   @Generated
   public void setInfo(ModuleInfo var1) {
      this.info = var1;
   }

   @Generated
   public void setName(String var1) {
      this.name = var1;
   }

   @Generated
   public void setEnabled(boolean var1) {
      this.enabled = var1;
   }

   @Generated
   public void setPriority(boolean var1) {
      this.priority = var1;
   }

   @Generated
   public void setKeyCode(int var1) {
      this.keyCode = var1;
   }

   @Generated
   public void setKeyModifiers(int var1) {
      this.keyModifiers = var1;
   }

   @Generated
   @Override
   public String toString() {
      return "Module(info="
         + this.getInfo()
         + ", id="
         + this.getId()
         + ", name="
         + this.getName()
         + ", nameLower="
         + this.getNameLower()
         + ", category="
         + this.getCategory()
         + ", description="
         + this.getDescription()
         + ", botModule="
         + this.isBotModule()
         + ", enabled="
         + this.isEnabled()
         + ", priority="
         + this.isPriority()
         + ", keyCode="
         + this.getKeyCode()
         + ", keyModifiers="
         + this.getKeyModifiers()
         + ")";
   }
}

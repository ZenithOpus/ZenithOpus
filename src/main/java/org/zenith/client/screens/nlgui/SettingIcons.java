package org.zenith.client.screens.nlgui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.zenith.base.font.SvgIcon;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.Setting;

public final class SettingIcons {
   private SettingIcons() {
   }

   public static SvgIcon resolve(Setting var0, String var1, String var2) {
      if (var0 == null) {
         return SvgIcon.MODE;
      } else {
         SvgIcon var3 = explicit(var0.getKey(), var1, var2);
         if (var3 != null) {
            return var3;
         } else if (var0 instanceof ColorSetting) {
            return SvgIcon.COLOR;
         } else {
            return var0 instanceof BindSetting ? SvgIcon.KEY : MenuGlyph.forSettingPurpose(var0).svg();
         }
      }
   }

   public static SvgIcon explicit(String var0, String var1, String var2) {
      if (var0 != null && !var0.isBlank()) {
         if (var2 != null && !var2.isBlank()) {
            var var3 = SettingIcons.Registry.MODULES.get(var2);
            SvgIcon var4 = find(var3, var0, var1);
            if (var4 != null) {
               return var4;
            }
         }

         return find(SettingIcons.Registry.SHARED, var0, var1);
      } else {
         return null;
      }
   }

   private static SvgIcon find(Map<String, SvgIcon> var0, String var1, String var2) {
      if (var0 == null) {
         return null;
      } else {
         if (var2 != null && !var2.isBlank()) {
            SvgIcon var3 = (SvgIcon)var0.get(var2 + ">" + var1);
            if (var3 != null) {
               return var3;
            }
         }

         return (SvgIcon)var0.get(var1);
      }
   }

   private static Map<String, SvgIcon> readEntries(JsonObject var0) {
      HashMap var1 = new HashMap();
      if (var0 != null) {
         for (Entry var3 : var0.entrySet()) {
            String var4 = ((JsonElement)var3.getValue()).getAsString().toUpperCase(Locale.ROOT).replace('-', '_');
            var1.put((String)var3.getKey(), SvgIcon.valueOf(var4));
         }
      }

      return Map.copyOf(var1);
   }

   private static final class Registry {
      private static final Map<String, SvgIcon> SHARED;
      private static final Map<String, Map<String, SvgIcon>> MODULES;

      static {
         String var0 = "/assets/zenith/icons/setting-icons.json";

         try {
            try (InputStream var1 = SettingIcons.class.getResourceAsStream(var0)) {
               if (var1 == null) {
                  throw new IllegalStateException("Missing setting icon registry: " + var0);
               }

               JsonObject var2 = JsonParser.parseReader(new InputStreamReader(var1, StandardCharsets.UTF_8)).getAsJsonObject();
               SHARED = SettingIcons.readEntries(var2.getAsJsonObject("keys"));
               HashMap var3 = new HashMap();
               JsonObject var4 = var2.getAsJsonObject("modules");
               if (var4 != null) {
                  for (Entry var6 : var4.entrySet()) {
                     var3.put((String)var6.getKey(), SettingIcons.readEntries(((JsonElement)var6.getValue()).getAsJsonObject()));
                  }
               }

               MODULES = Map.copyOf(var3);
            }
         } catch (Exception var9) {
            throw new IllegalStateException("Invalid setting icon registry: " + var0, var9);
         }
      }
   }
}

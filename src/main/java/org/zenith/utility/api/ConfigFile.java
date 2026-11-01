package org.zenith.utility.api;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import lombok.Generated;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.bot.client.HeadlessBots;

public class ConfigFile {
   private final String text;
   private final File file;
   private boolean priority;

   public ConfigFile(String var1) {
      this.text = var1;
      this.file = new File(Manager.file, var1 + "." + "Zenith".toLowerCase());
      if (!this.file.exists()) {
         try {
            this.file.createNewFile();
         } catch (IOException var3) {
            var3.printStackTrace();
         }
      }
   }

   public void method7593() {
      this.priority = !this.priority;
   }

   public JsonObject save() {
      try {
         JsonObject var1 = new JsonObject();
         JsonObject var2 = new JsonObject();
         var2.addProperty("name", this.text);
         var2.addProperty("priority", this.priority);
         var1.add("ConfigData", var2);
         var1.add("Styles", Zenith.getInstance().getStyleManager().save());
         JsonObject var3 = new JsonObject();
         var3.addProperty("name", org.zenith.utility.config.AvatarUtil.method7257(Zenith.getInstance().getFiguraManager().getPath()));
         var3.addProperty("weapon", org.zenith.utility.config.AvatarUtil.method7257(Zenith.getInstance().getFiguraManager().getPath3()));
         var1.add("FiguraData", var3);
         var1.add("PetData", Zenith.getInstance().getPetManager().save());
         Wardrobe var4 = Wardrobe.method2488();
         if (var4 != null) {
            var1.add("Wardrobe", var4.method2511());
         }

         JsonObject var5 = new JsonObject();

         for (Module var7 : Zenith.getInstance().getModuleManager().getItems()) {
            var5.add(var7.getId(), var7.save());
         }

         var1.add("Modules", var5);
         JsonObject var9 = new JsonObject();
         var9.addProperty("language", Zenith.getInstance().getLocalization().getLanguageName());
         var1.add("Language", var9);
         var1.add("BotData", HeadlessBots.saveState());
         JsonObject var10 = new JsonObject();
         var1.add("Interface", var10);
         return var1;
      } catch (Exception var8) {
         var8.printStackTrace();
         return null;
      }
   }

   public void load(JsonObject var1) {
      if (var1.has("ConfigData")) {
         JsonObject var2 = var1.getAsJsonObject("ConfigData");
         if (var2.has("priority")) {
            this.priority = var2.get("priority").getAsBoolean();
         }
      }

      loadState(var1);
   }

   public static void loadState(JsonObject var0) {
      method7594(var0);
      if (var0.has("Styles")) {
         Zenith.getInstance().getStyleManager().load(var0.getAsJsonObject("Styles"));
      }

      if (var0.has("FiguraData")) {
         JsonObject var1 = var0.getAsJsonObject("FiguraData");
         if (var1.has("name")) {
            Zenith.getInstance().getFiguraManager().method7672(org.zenith.utility.config.AvatarUtil.method7258(var1.get("name").getAsString()));
         }

         if (var1.has("weapon")) {
            Zenith.getInstance().getFiguraManager().method7673(org.zenith.utility.config.AvatarUtil.method7258(var1.get("weapon").getAsString()));
         }
      }

      if (var0.has("Wardrobe") && var0.get("Wardrobe").isJsonObject() && Wardrobe.method2488() != null) {
         Wardrobe.method2488().method2512(var0.getAsJsonObject("Wardrobe"));
      }

      if (var0.has("PetData")) {
         JsonObject var6 = var0.getAsJsonObject("PetData");
         Zenith.getInstance().getPetManager().load(var6);
      }

      if (var0.has("Language")) {
         JsonObject var7 = var0.getAsJsonObject("Language");
         if (var7.has("language")) {
            Zenith.getInstance().getLocalization().method1728(var7.get("language").getAsString());
         }
      }

      if (var0.has("Modules")) {
         JsonObject var8 = var0.getAsJsonObject("Modules");

         for (Module var3 : Zenith.getInstance().getModuleManager().getItems()) {
            try {
               JsonObject var4 = var8.getAsJsonObject(var3.getId());
               if (var4 == null && var3.getId().startsWith("zenith:")) {
                  var4 = var8.getAsJsonObject(var3.getName());
               }

               var3.load(var4);
            } catch (Exception var5) {
               var5.printStackTrace();
            }
         }
      }
   }

   private static void method7594(JsonObject var0) {
      try {
         if (var0.has("BotData") && var0.get("BotData").isJsonObject()) {
            HeadlessBots.loadState(var0.getAsJsonObject("BotData"));
         }
      } catch (Exception var2) {
      }
   }

   @Generated
   public String getName() {
      return this.text;
   }

   @Generated
   public File getFile() {
      return this.file;
   }

   @Generated
   public boolean isPriority() {
      return this.priority;
   }
}

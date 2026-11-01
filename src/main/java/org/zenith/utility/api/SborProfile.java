package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.item.ItemStack;

public class SborProfile {
   private static final String text = "HolyWorld";
   private final String text2;
   private final File file;
   private String text3;
   private List<SborItem> items = new ArrayList<>();

   public SborProfile(String var1) {
      this(var1, "HolyWorld", new File(KitManager.file, var1 + "." + "Zenith".toLowerCase()));
   }

   public SborProfile(String var1, String var2) {
      this(var1, var2, new File(KitManager.file, method2408(var1, var2) + "." + "Zenith".toLowerCase()));
   }

   public SborProfile(String var1, String var2, File var3) {
      this.text2 = var1;
      this.text3 = method2407(var2);
      this.file = var3;
      if (!var3.exists()) {
         try {
            var3.createNewFile();
         } catch (IOException var5) {
            var5.printStackTrace();
         }
      }
   }

   public JsonObject save() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("name", this.text2);
      var1.addProperty("server", this.text3);
      JsonArray var2 = new JsonArray();

      for (SborItem var4 : this.items) {
         var2.add(var4.save());
      }

      var1.add("items", var2);
      return var1;
   }

   public void load(JsonObject var1) {
      this.items.clear();
      if (var1.has("server")) {
         this.text3 = method2407(var1.get("server").getAsString());
      }

      if (var1.has("items")) {
         JsonArray var2 = var1.getAsJsonArray("items");

         for (int var3 = 0; var3 < var2.size(); var3++) {
            JsonObject var4 = var2.get(var3).getAsJsonObject();
            SborItem var5 = new SborItem();
            var5.load(var4);
            this.items.add(var5);
         }
      }
   }

   public List<ItemStack> method2405() {
      ArrayList<ItemStack> var1 = new ArrayList<>();

      for (SborItem var3 : this.items) {
         var1.add(var3.method1749());
      }

      return var1;
   }

   public boolean method2406(String var1) {
      return this.text3.equals(method2407(var1));
   }

   private static String method2407(String var0) {
      return var0 != null && !var0.isBlank() ? var0 : "HolyWorld";
   }

   private static String method2408(String var0, String var1) {
      String var2 = method2407(var1).replaceAll("[^\\p{L}\\p{N}._-]", "_");
      return var2 + "_" + var0;
   }

   @Generated
   public String getName() {
      return this.text2;
   }

   @Generated
   public File getFile() {
      return this.file;
   }

   @Generated
   public String getText3() {
      return this.text3;
   }

   @Generated
   public List<SborItem> getItems() {
      return this.items;
   }

   @Generated
   public void setText3(String var1) {
      this.text3 = var1;
   }

   @Generated
   public void setItems(List<SborItem> var1) {
      this.items = var1;
   }
}

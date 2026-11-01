package org.zenith.utility.api;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import lombok.Generated;
import org.zenith.Zenith;
import org.zenith.utility.misc.AesGcmCrypto;

public class KitManager {
   public static final File file = new File(Zenith.file, "kits");
   private final String text = "kit";
   private final List<SborProfile> items = new ArrayList<>();

   public KitManager() {
      file.mkdirs();
      this.method7274();
   }

   public void method7274() {
      this.items.clear();
      File[] var1 = file.listFiles();
      if (var1 != null) {
         for (File var5 : var1) {
            if (var5.getName().endsWith("." + "Zenith".toLowerCase())) {
               SborProfile var6 = this.method7276(var5);
               if (var6 != null) {
                  this.items.add(var6);
               }
            }
         }
      }
   }

   public SborProfile method7275(String var1) {
      if (var1 == null) {
         return null;
      } else {
         File var2 = new File(file, var1 + "." + "Zenith".toLowerCase());
         return !var2.exists() ? null : this.method7276(var2);
      }
   }

   private SborProfile method7276(File var1) {
      if (var1 != null && var1.exists()) {
         try {
            SborProfile var13;
            try (BufferedReader var2 = new BufferedReader(new FileReader(var1))) {
               JsonParser var3 = new JsonParser();
               String var4 = var2.readLine();
               byte[] var5 = Base64.getDecoder().decode(var4);
               byte[] var6 = AesGcmCrypto.method5340(var5, "kit");
               String var7 = new String(var6, StandardCharsets.UTF_8);
               JsonObject var8 = (JsonObject)var3.parse(var7);
               String var9 = var1.getName().replace("." + "Zenith".toLowerCase(), "");
               String var10 = var8.has("name") ? var8.get("name").getAsString() : var9;
               String var11 = var8.has("server") ? var8.get("server").getAsString() : "HolyWorld";
               SborProfile var12 = new SborProfile(var10, var11, var1);
               var12.load(var8);
               var13 = var12;
            }

            return var13;
         } catch (Exception var16) {
            var16.printStackTrace();
            return null;
         }
      } else {
         return null;
      }
   }

   public boolean method7277(SborProfile var1) {
      try {
         if (var1 == null) {
            return false;
         } else {
            String var2 = new GsonBuilder().setPrettyPrinting().create().toJson(var1.save());
            var2 = Base64.getEncoder().encodeToString(AesGcmCrypto.method5339(var2.getBytes(), "kit"));

            try {
               FileWriter var3 = new FileWriter(var1.getFile());
               var3.write(var2);
               var3.close();
               return true;
            } catch (IOException var4) {
               return false;
            }
         }
      } catch (Exception var5) {
         return false;
      }
   }

   public SborProfile method7278(String var1, List<SborItem> var2) {
      SborProfile var3 = new SborProfile(var1);
      var3.setItems(var2);
      if (this.method7277(var3)) {
         this.items.add(var3);
         return var3;
      } else {
         return null;
      }
   }

   public SborProfile method7279(String var1, List<SborItem> var2, String var3) {
      SborProfile var4 = new SborProfile(var1, var3);
      var4.setItems(var2);
      if (this.method7277(var4)) {
         this.items.add(var4);
         return var4;
      } else {
         return null;
      }
   }

   public boolean method7280(String var1) {
      if (var1 == null) {
         return false;
      } else {
         SborProfile var2;
         if ((var2 = this.method7282(var1)) != null) {
            File var3 = var2.getFile();
            if (var3.exists() && var3.delete()) {
               this.items.remove(var2);
               return true;
            }
         }

         return false;
      }
   }

   public boolean method7281(SborProfile var1) {
      if (var1 == null) {
         return false;
      } else {
         File var2 = var1.getFile();
         if (var2.exists() && var2.delete()) {
            this.items.remove(var1);
            return true;
         } else {
            return false;
         }
      }
   }

   public SborProfile method7282(String var1) {
      return var1 == null ? null : this.items.stream().filter(var1x -> var1x.getName().equals(var1)).findFirst().orElse(null);
   }

   public SborProfile method7283(String var1, String var2) {
      return var1 == null ? null : this.items.stream().filter(var2x -> var2x.getName().equals(var1) && var2x.method2406(var2)).findFirst().orElse(null);
   }

   public List<SborProfile> method7284(String var1) {
      return this.items.stream().filter(var1x -> var1x.method2406(var1)).toList();
   }

   public List<String> method7285() {
      ArrayList<String> var1 = new ArrayList<>();

      for (SborProfile var3 : this.items) {
         if (!var1.contains(var3.getName())) {
            var1.add(var3.getName());
         }
      }

      return var1;
   }

   @Generated
   public String method7286() {
      return "kit";
   }

   @Generated
   public List<SborProfile> getItems() {
      return this.items;
   }
}

package org.zenith.utility.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.zenith.Zenith;

public final class ServerNameNormalizer {
   private final List<SborList> items = new ArrayList<>();
   private boolean flag;

   public List<SborList> method1229(String var1) {
      this.method1236();
      ArrayList<SborList> var2 = new ArrayList<>();

      for (SborList var4 : this.items) {
         if (var4.method1751(var1)) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public SborList method1230(String var1, String var2) {
      this.method1236();

      for (SborList var4 : this.items) {
         if (var4.method1751(var2) && var4.getName().equalsIgnoreCase(var1)) {
            return var4;
         }
      }

      return null;
   }

   public void method1231(SborList var1) {
      if (var1 != null) {
         this.method1236();
         SborList var2 = this.method1230(var1.getName(), var1.getText2());
         if (var2 != null) {
            this.items.set(this.items.indexOf(var2), var1);
         } else {
            this.items.add(var1);
         }
      }
   }

   public boolean method1232(SborList var1) {
      return var1 != null && this.items.remove(var1);
   }

   public String method1233(String var1, String var2) {
      this.method1236();
      if (this.method1230(var1, var2) == null) {
         return var1;
      } else {
         for (int var3 = 2; var3 < 1000; var3++) {
            String var4 = var1 + " " + var3;
            if (this.method1230(var4, var2) == null) {
               return var4;
            }
         }

         return var1 + " " + System.currentTimeMillis();
      }
   }

   public JsonArray method1234() {
      this.method1236();
      JsonArray var1 = new JsonArray();

      for (SborList var3 : this.items) {
         var1.add(var3.save());
      }

      return var1;
   }

   public void method1235(JsonArray var1) {
      this.flag = true;
      this.items.clear();
      if (var1 != null) {
         for (JsonElement var3 : var1) {
            if (var3.isJsonObject()) {
               SborList var4 = SborList.method1754(var3.getAsJsonObject());
               if (var4 != null) {
                  this.items.add(var4);
               }
            }
         }
      }
   }

   private void method1236() {
      if (!this.flag) {
         this.flag = true;
         KitManager var1 = Zenith.getInstance() == null ? null : Zenith.getInstance().getKitManager();
         if (var1 != null) {
            for (SborProfile var3 : var1.getItems()) {
               if (var3 != null && var3.getName() != null && !var3.getName().isBlank()) {
                  this.items.add(method1237(var3));
               }
            }
         }
      }
   }

   private static SborList method1237(SborProfile var0) {
      ArrayList var1 = new ArrayList();

      for (SborItem var3 : var0.getItems()) {
         if (var3 != null && var3.getText2() != null && !var3.getText2().isBlank()) {
            var1.add(new SborList.Data(var3.getIntValue(), var3.getText2(), var3.getText(), var3.getIntValue2(), var3.getLongValue(), -1.0F));
         }
      }

      return new SborList(var0.getName(), normalize(var0.getText3()), var1);
   }

   private static String normalize(String var0) {
      if (var0 == null) {
         return "HolyWorld";
      } else {
         String var1 = var0.toLowerCase(Locale.ROOT);
         if (var1.startsWith("funtime")) {
            return "Funtime 1.21";
         } else {
            return var1.startsWith("spooky") ? "SpookyTime" : "HolyWorld";
         }
      }
   }
}

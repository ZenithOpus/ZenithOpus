package org.zenith.utility.misc;

import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.Session;
import org.zenith.utility.api.JsonListStorage;
import org.zenith.utility.api.MicrosoftAuth;

public final class Accounts extends JsonListStorage<Account> {
   private static final String text = "zenith-accounts-v1";
   private Session session;
   private int intValue = -1;
   private String status = "";

   public Accounts() {
      super("accounts.json", "zenith-accounts-v1", (new TypeToken<List<Account>>() {}).getType(), CopyOnWriteArrayList::new);
      this.items = new CopyOnWriteArrayList<>(this.items);
   }

   public Session method4750() {
      if (this.session == null) {
         this.session = MinecraftClient.getInstance().getSession();
      }

      return this.session;
   }

   public List<Account> method4751() {
      return new ArrayList<>(this.items);
   }

   public int size() {
      return this.items.size();
   }

   public Account method4752() {
      var var1 = this.method4751();
      return this.intValue >= 0 && this.intValue < var1.size() ? (Account)var1.get(this.intValue) : null;
   }

   public void method4753(Account var1) {
      if (var1 != null && var1.getName() != null && !var1.getName().isBlank()) {
         for (Account var3 : this.items) {
            if (var3.getName().equalsIgnoreCase(var1.getName()) && var3.getType() == var1.getType()) {
               if (var1.method5473()) {
                  var3.setText(var1.getText());
                  var3.setText2(var1.getText2());
                  var3.setText3(var1.getText3());
                  this.save();
               }

               return;
            }
         }

         this.add(var1);
         this.save();
      }
   }

   public void method4754(int var1) {
      var var2 = this.method4751();
      if (var1 >= 0 && var1 < var2.size()) {
         this.remove((Account)var2.get(var1));
         if (this.intValue == var1) {
            this.intValue = -1;
         } else if (this.intValue > var1) {
            this.intValue--;
         }

         this.save();
      }
   }

   public void select(int var1) {
      var var2 = this.method4751();
      if (var1 >= 0 && var1 < var2.size()) {
         Account var3 = (Account)var2.get(var1);
         if (!var3.method5473()) {
            this.method4756(new Session(var3.getName(), var3.method5472(), "", Optional.empty(), Optional.empty()));
            this.intValue = var1;
            this.status = "Switched to " + var3.getName();
         } else if (var3.getText3() != null && !var3.getText3().isEmpty()) {
            this.status = "Refreshing " + var3.getName() + "...";
            MicrosoftAuth.method3059(var3.getText3(), var1x -> this.status = var1x)
               .whenComplete((var2x, var3x) -> MinecraftClient.getInstance().execute(() -> {
                  if (var3x == null && var2x != null) {
                     var3.setName(var2x.name());
                     var3.setText(var2x.uuid().toString());
                     var3.setText2(var2x.getText2());
                     var3.setText3(var2x.getText3());
                     this.save();
                     this.method4756(new Session(var2x.name(), var2x.uuid(), var2x.getText2(), Optional.empty(), Optional.empty()));
                     this.intValue = this.method4751().indexOf(var3);
                     this.status = "Switched to " + var2x.name();
                  } else {
                     this.status = "Failed to refresh: " + rootMessage(var3x);
                  }
               }));
         } else {
            this.method4756(new Session(var3.getName(), var3.method5472(), var3.getText2(), Optional.empty(), Optional.empty()));
            this.intValue = var1;
            this.status = "Switched to " + var3.getName();
         }
      }
   }

   public void method4755() {
      Session var1 = this.method4750();
      this.method4756(var1);
      this.intValue = -1;
      this.status = "Restored " + var1.getUsername();
   }

   private void method4756(Session var1) {
      MinecraftClient.getInstance().session = var1;
   }

   public void method4757(String var1) {
      this.status = var1 == null ? "" : var1;
   }

   public static String rootMessage(Throwable var0) {
      Throwable var1 = var0;

      while (var1 != null && var1.getCause() != null) {
         var1 = var1.getCause();
      }

      if (var1 == null) {
         return "unknown error";
      } else {
         String var2 = var1.getMessage();
         return var2 != null && !var2.isBlank() ? var2 : var1.getClass().getSimpleName();
      }
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public String getStatus() {
      return this.status;
   }
}

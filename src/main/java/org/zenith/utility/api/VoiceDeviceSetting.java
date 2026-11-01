package org.zenith.utility.api;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Function;
import org.zenith.client.settings.ModeSetting;

public class VoiceDeviceSetting extends ModeSetting {
   private final String text;
   private final Function<String, String> function;
   private String text2 = "";
   private String shown = "";

   public VoiceDeviceSetting(String var1, String var2, String var3, Function<String, String> var4) {
      super(var1, var2);
      this.text = var3;
      this.function = var4;
      new ModeSetting.Entry(this, var3);
   }

   public void method9(List<String> var1) {
      this.method12();
      this.getValues().clear();
      new ModeSetting.Entry(this, this.text);

      for (String var3 : var1) {
         if (var3 != null && !var3.isBlank() && !var3.equals(this.text)) {
            new VoiceDeviceSetting.Entry(this, var3);
         }
      }

      this.set(this.text2.isEmpty() ? this.text : this.text2);
      if (this.getValue2() == null || !this.getValues().contains(this.getValue2())) {
         this.getValues().getFirst().method2103();
      }

      this.shown = this.method11();
   }

   public void method10(String var1) {
      this.set(var1 != null && !var1.isEmpty() ? var1 : this.text);
      this.method12();
   }

   public String method11() {
      String var1 = this.get();
      return var1.equals(this.text) ? "" : var1;
   }

   public boolean method12() {
      String var1 = this.method11();
      if (var1.equals(this.shown)) {
         return false;
      } else {
         this.text2 = var1;
         this.shown = var1;
         return true;
      }
   }

   @Override
   public void safe(JsonObject var1) {
      this.method12();
      var1.addProperty(String.valueOf(this.key), this.text2);
   }

   @Override
   public void load(JsonObject var1) {
      this.text2 = var1.get(String.valueOf(this.key)).getAsString();
      this.set(this.text2.isEmpty() ? this.text : this.text2);
      this.shown = this.method11();
   }

   private final class Entry extends ModeSetting.Entry {
      private Entry(ModeSetting var2, String var3) {
         super(var2, var3);
      }

      @Override
      public String getName() {
         return VoiceDeviceSetting.this.function.apply(this.getKey());
      }
   }
}

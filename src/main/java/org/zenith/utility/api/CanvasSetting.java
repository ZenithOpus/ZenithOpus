package org.zenith.utility.api;

import com.google.gson.JsonObject;
import lombok.Generated;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.Canvas;

public class CanvasSetting extends Setting {
   private final Canvas canvas = new Canvas();

   public CanvasSetting(String var1) {
      super(var1, "");
   }

   @Override
   public void safe(JsonObject var1) {
      var1.addProperty(this.key, this.canvas.method145());
   }

   @Override
   public void load(JsonObject var1) {
      if (var1.has(this.key)) {
         this.canvas.method147(var1.get(this.key).getAsString());
      }
   }

   @Generated
   public Canvas getCanvas() {
      return this.canvas;
   }
}

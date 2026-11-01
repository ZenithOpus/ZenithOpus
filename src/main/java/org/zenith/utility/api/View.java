package org.zenith.utility.api;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.zenith.Zenith;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;

public final class View {
   private static final String text = "view.json";
   public final BooleanSetting renderSelf = new BooleanSetting("cosmetics.renderSelf", "cosmetics.renderSelf.desc", true);
   public final BooleanSetting renderOthers = new BooleanSetting("cosmetics.renderOthers", "cosmetics.renderOthers.desc", true);
   public final SliderSetting petScale = new SliderSetting("cosmetics.petScale", 1.35F, 0.4F, 4.0F, 0.05F, "cosmetics.petScale.desc", "x", null, null);
   private final Cosmetics cosmetics;
   private final Path path;
   private String lastSignature = "";

   public View(Cosmetics var1, Path var2) {
      this.cosmetics = Objects.requireNonNull(var1, "manager");
      this.path = var2.resolve("cosmetics").resolve("view.json");
      this.load();
      this.lastSignature = this.method7331();
      EventManager.register(this);
   }

   public List<Setting> method7327() {
      return List.of(this.renderSelf, this.renderOthers, this.petScale);
   }

   public static boolean method7328() {
      View var0 = method7330();
      return var0 == null || var0.renderSelf.isEnabled();
   }

   public static boolean method7329() {
      View var0 = method7330();
      return var0 == null || var0.renderOthers.isEnabled();
   }

   private static View method7330() {
      Zenith var0 = Zenith.getInstance();
      return var0 == null ? null : var0.getView();
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      float var2 = this.petScale.getValue3();
      if (Math.abs(var2 - this.cosmetics.getFloatValue()) > 0.001F) {
         this.cosmetics.method3003(var2);
      }

      String var3 = this.method7331();
      if (!var3.equals(this.lastSignature)) {
         this.lastSignature = var3;
         this.save();
      }
   }

   private String method7331() {
      return this.renderSelf.isEnabled() + "|" + this.renderOthers.isEnabled() + "|" + this.petScale.getValue3();
   }

   private void load() {
      JsonObject var1 = JsonIo.method1009(this.path);
      if (var1 == null) {
         this.petScale.method2400(this.cosmetics.getFloatValue());
      } else {
         if (var1.has("renderSelf")) {
            this.renderSelf.setEnabled(var1.get("renderSelf").getAsBoolean());
         }

         if (var1.has("renderOthers")) {
            this.renderOthers.setEnabled(var1.get("renderOthers").getAsBoolean());
         }

         this.petScale.method2400(var1.has("petScale") ? var1.get("petScale").getAsFloat() : this.cosmetics.getFloatValue());
      }
   }

   private void save() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("renderSelf", this.renderSelf.isEnabled());
      var1.addProperty("renderOthers", this.renderOthers.isEnabled());
      var1.addProperty("petScale", this.petScale.getValue3());
      JsonIo.method1010(this.path, var1);
   }
}

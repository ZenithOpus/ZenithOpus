package org.zenith.client.screens.nlgui.style;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import org.zenith.ZenithColor;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.utility.misc.ColorCycleIcon;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.render.display.base.GlowColor;

public class StyleManager {
   private final List<ZenithStyle> styles = new ArrayList<>();
   private final ColorCycleIcon colorCycleIcon;
   private ZenithStyle currentStyle;

   public StyleManager() {
      EventManager.register(this);
      this.colorCycleIcon = new ColorCycleIcon(1700L);
      this.styles.addAll(this.createDefaultStyles());
      if (this.currentStyle == null && !this.styles.isEmpty()) {
         this.currentStyle = this.styles.getFirst();
      }
   }

   @EventTarget
   public void eventRender(ScreenRenderEvent var1) {
      this.colorCycleIcon.update();
   }

   public GlowColor getColorCycleIconGradient() {
      ZenithStyle var1 = this.getCurrentStyle();
      return var1 == null ? GlowColor.method8097(ZenithColor.INSTANCE) : this.colorCycleIcon.method807(var1.getPrimaryColor().getZenithColor());
   }

   public ZenithStyle getCurrentStyle() {
      if (this.currentStyle != null) {
         return this.currentStyle;
      } else {
         return this.styles.isEmpty() ? null : this.styles.getFirst();
      }
   }

   private List<ZenithStyle> createDefaultStyles() {
      ArrayList<ZenithStyle> var1 = new ArrayList<>();
      var1.add(this.createDefaultStyle("Zenith", "#9AA8FF", "#D1BBFD", "#0B0B147A"));
      var1.add(this.createDefaultStyle("Amethyst", "#CF96FB", "#F5ADFA", "#0E08117A"));
      var1.add(this.createDefaultStyle("Synthwave", "#FF83D1", "#BFC1FC", "#1609127A"));
      var1.add(this.createDefaultStyle("Rosewood", "#F98EA3", "#F8B6A5", "#140A0C7A"));
      var1.add(this.createDefaultStyle("Ember", "#FF9151", "#FABB3C", "#130A067A"));
      var1.add(this.createDefaultStyle("Goldleaf", "#D1A917", "#CACC24", "#120F087A"));
      var1.add(this.createDefaultStyle("Toxic", "#7BC005", "#37E30C", "#0D12087A"));
      var1.add(this.createDefaultStyle("Verdant", "#19C770", "#31DEB5", "#08110D7A"));
      var1.add(this.createDefaultStyle("Lagoon", "#1FC0C0", "#50DBBB", "#0811117A"));
      var1.add(this.createDefaultStyle("Glacier", "#58B5F8", "#B0C5F9", "#0A10157A"));
      var1.add(this.createDefaultStyle("Graphite", "#A9ADBB", "#C5C4CF", "#0E0E117A"));
      var1.add(this.createPorcelainStyle());
      return var1;
   }

   private ZenithStyle createDefaultStyle(String var1, String var2, String var3, String var4) {
      ZenithStyle var5 = new ZenithStyle(var1);
      var5.getPrimaryColor().setZenithColor(new ZenithColor(var2));
      var5.getSecondaryPrimaryColor().setZenithColor(new ZenithColor(var3));
      var5.setDefaultsFromGuiStyle(new ZenithColor(var4));
      return var5;
   }

   private ZenithStyle createPorcelainStyle() {
      ZenithStyle var1 = new ZenithStyle("Porcelain");
      var1.getPrimaryColor().setZenithColor(new ZenithColor("#45618A"));
      var1.getSecondaryPrimaryColor().setZenithColor(new ZenithColor("#6E86A8"));
      var1.setDefaultsFromGuiStyle();
      var1.getGlowColor1().setZenithColor(new ZenithColor("#45618A3D"));
      var1.getGlowColor2().setZenithColor(new ZenithColor("#6E86A814"));
      var1.getLeftBackground().setZenithColor(new ZenithColor("#F4F6FAC4"));
      var1.getRightBackground().setZenithColor(new ZenithColor("#FAFBFDE0"));
      var1.getPanelLeftBackground().setZenithColor(new ZenithColor("#F4F6FAC4"));
      var1.getSurfaceEnableBackground().setZenithColor(new ZenithColor("#FFFFFF8F"));
      var1.getHeaderDisableBackground().setZenithColor(new ZenithColor("#E9EDF47A"));
      var1.getSurfaceDisableBackground().setZenithColor(new ZenithColor("#EDF1F76B"));
      var1.getFieldSurfaceBackground().setZenithColor(new ZenithColor("#1E293B0F"));
      var1.getFieldBorder().setZenithColor(new ZenithColor("#1E293B24"));
      var1.getDisableActiveBg().setZenithColor(new ZenithColor("#64748B2E"));
      var1.getHudBackground().setZenithColor(new ZenithColor("#E7ECF5D6"));
      var1.getHeaderHudBackground().setZenithColor(new ZenithColor("#D3DCE9D6"));
      var1.getTextEnable().setZenithColor(new ZenithColor("#1E293B"));
      var1.getTextSecondary().setZenithColor(new ZenithColor("#0F172AC4"));
      var1.getTextTertiary().setZenithColor(new ZenithColor("#33415594"));
      var1.getFriendColor().setZenithColor(new ZenithColor("#0E7A50"));
      var1.getGlareColor().setZenithColor(new ZenithColor("#1E293B1F"));
      var1.getHeartIcon().setZenithColor(new ZenithColor("#C93F58"));
      var1.getHeartActiveBg().setZenithColor(new ZenithColor("#C93F5829"));
      return var1;
   }

   public JsonObject save() {
      JsonObject var1 = new JsonObject();
      JsonArray var2 = new JsonArray();

      for (ZenithStyle var4 : this.styles) {
         JsonObject var5 = new JsonObject();
         var4.safe(var5);
         var2.add(var5);
      }

      var1.add("array", var2);
      ZenithStyle var6 = this.getCurrentStyle();
      if (var6 != null) {
         var1.addProperty("select", var6.getName());
      }

      return var1;
   }

   public void load(JsonObject var1) {
      try {
         if (var1.has("select")) {
            this.currentStyle = this.getStyleByName(var1.get("select").getAsString());
         }

         if (var1.has("array")) {
            for (JsonElement var4 : var1.getAsJsonArray("array")) {
               if (var4.isJsonObject()) {
                  JsonObject var5 = var4.getAsJsonObject();
                  if (var5.has("name")) {
                     ZenithStyle var6 = this.getStyleByName(var5.get("name").getAsString());
                     if (var6 != null) {
                        var6.load(var5);
                     }
                  }
               }
            }
         }

         if (this.currentStyle == null && !this.styles.isEmpty()) {
            this.currentStyle = this.styles.getFirst();
         }
      } catch (Exception var7) {
      }
   }

   public ZenithStyle getStyleByName(String var1) {
      if (var1 == null) {
         return null;
      } else {
         for (ZenithStyle var3 : this.styles) {
            if (var1.equals(var3.getName())) {
               return var3;
            }
         }

         return null;
      }
   }

   public GlowColor getClientColor() {
      return GlowColor.method8096(this.getClientColor(0), this.getClientColor(90), this.getClientColor(180), this.getClientColor(270));
   }

   public ZenithColor getClientColor(int var1) {
      return ColorUtils.method6435(
         4, var1, this.getCurrentStyle().getPrimaryColor().getZenithColor(), this.getCurrentStyle().getSecondaryPrimaryColor().getZenithColor()
      );
   }

   public ZenithColor getGlowColor(int var1) {
      return ColorUtils.method6435(4, var1, this.getCurrentStyle().getGlowColor1().getZenithColor(), this.getCurrentStyle().getGlowColor2().getZenithColor());
   }

   public GlowColor getGlowColor() {
      return GlowColor.method8096(this.getGlowColor(0), this.getGlowColor(90), this.getGlowColor(180), this.getGlowColor(270));
   }

   @Generated
   public List<ZenithStyle> getStyles() {
      return this.styles;
   }

   @Generated
   public ColorCycleIcon getColorCycleIcon() {
      return this.colorCycleIcon;
   }

   @Generated
   public void setCurrentStyle(ZenithStyle var1) {
      this.currentStyle = var1;
   }
}

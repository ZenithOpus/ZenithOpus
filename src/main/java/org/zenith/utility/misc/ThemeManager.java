package org.zenith.utility.misc;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.ZenithColor;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.utility.render.display.base.GlowColor;

public class ThemeManager {
   private ThemeStyle themeStyle;
   private ThemeStyle themeStyle2 = ThemeStyle.INSTANCE;
   private final Animation animation;
   private final ColorCycleIcon colorCycleIcon;
   private final List<ThemeStyle> items = new ArrayList<>(List.of(ThemeStyle.INSTANCE, ThemeStyle.INSTANCE2, ThemeStyle.INSTANCE3));
   private int intValue = 0;

   public ThemeManager() {
      EventManager.register(this);
      this.animation = new Animation(200L, 1.0F, Easing.INSTANCE10);
      this.animation.setDone(true);
      this.colorCycleIcon = new ColorCycleIcon(1700L);
   }

   public void method4454(ThemeStyle var1) {
      if (this.animation.isDone()) {
         this.themeStyle = this.themeStyle2;
         this.themeStyle2 = var1;
         int var2 = this.items.indexOf(var1);
         if (var2 != -1) {
            this.intValue = var2;
         }

         this.animation.reset();
      }
   }

   public void method4455() {
      if (this.animation.isDone()) {
         this.themeStyle = this.themeStyle2;
         this.intValue = (this.intValue + 1) % this.items.size();
         this.themeStyle2 = this.items.get(this.intValue);
         this.animation.reset();
      }
   }

   public void method4456(String var1) {
      if (var1 != null) {
         ThemeStyle var2;
         switch (var1) {
            case "Dark":
               var2 = ThemeStyle.INSTANCE;
               break;
            case "Light":
               var2 = ThemeStyle.INSTANCE2;
               break;
            case "Custom":
               var2 = ThemeStyle.INSTANCE3;
               break;
            default:
               return;
         }

         this.method4454(var2);
      }
   }

   @EventTarget
   public void eventRender(ScreenRenderEvent var1) {
      this.colorCycleIcon.update();
      this.animation.method2808(1.0F);
   }

   private void method4457(List<ZenithColor> var1, ZenithColor var2, ZenithColor var3, ZenithColor var4, ZenithColor var5) {
      var1.set(0, var2);
      var1.set(1, var3);
      var1.set(2, var4);
      var1.set(3, var5);
   }

   public ThemeStyle method4458() {
      return this.animation.isDone() ? this.themeStyle2 : this.themeStyle.method2837(this.themeStyle2, this.animation.getValue());
   }

   public boolean method4459(ThemeStyle var1) {
      return this.themeStyle2 == var1;
   }

   public GlowColor getClientColor() {
      return GlowColor.method8096(this.getClientColor(0), this.getClientColor(90), this.getClientColor(180), this.getClientColor(270));
   }

   public ZenithColor getClientColor(int var1) {
      return ColorUtils.method6435(4, var1, this.method4458().getZenithColor(), this.method4458().getZenithColor2());
   }

   public ZenithColor getGlowColor(int var1) {
      return ColorUtils.method6435(4, var1, this.method4458().getZenithColor4(), this.method4458().getZenithColor5());
   }

   public GlowColor getGlowColor() {
      return GlowColor.method8096(this.getGlowColor(0), this.getGlowColor(90), this.getGlowColor(180), this.getGlowColor(270));
   }

   @Generated
   public ThemeStyle getThemeStyle() {
      return this.themeStyle;
   }

   @Generated
   public Animation getAnimation() {
      return this.animation;
   }

   @Generated
   public ColorCycleIcon getColorCycleIcon() {
      return this.colorCycleIcon;
   }

   @Generated
   public List<ThemeStyle> getItems() {
      return this.items;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }
}

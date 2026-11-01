package org.zenith.utility.misc;

import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.utility.render.display.base.GlowColor;

public class ColorCycleIcon {
   private final Animation animation;
   private float floatValue;

   public ColorCycleIcon(long var1) {
      this.animation = new Animation(var1, Easing.INSTANCE10);
   }

   public void update() {
      this.animation.method2808(1.0F);
      this.floatValue = this.animation.getValue() * 360.0F;
      if (this.floatValue >= 360.0F) {
         this.floatValue = 0.0F;
         this.animation.reset();
      }
   }

   public GlowColor method806() {
      ZenithColor var1 = Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor();
      return this.method807(var1);
   }

   public GlowColor method807(ZenithColor var1) {
      return this.method808(var1, var1.method7294(0.4F));
   }

   public GlowColor method808(ZenithColor var1, ZenithColor var2) {
      float var3 = (float)((Math.sin(Math.toRadians(this.floatValue)) + 1.0) / 2.0);
      float var4 = (float)((Math.sin(Math.toRadians(this.floatValue + 90.0F)) + 1.0) / 2.0);
      float var5 = (float)((Math.sin(Math.toRadians(this.floatValue + 180.0F)) + 1.0) / 2.0);
      float var6 = (float)((Math.sin(Math.toRadians(this.floatValue + 270.0F)) + 1.0) / 2.0);
      ZenithColor var7 = var1.method7295(var2, var3);
      ZenithColor var8 = var1.method7295(var2, var4);
      ZenithColor var9 = var1.method7295(var2, var5);
      ZenithColor var10 = var1.method7295(var2, var6);
      return GlowColor.method8096(var7, var10, var8, var9);
   }
}

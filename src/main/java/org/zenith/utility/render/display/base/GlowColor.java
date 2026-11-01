package org.zenith.utility.render.display.base;

import java.util.List;
import lombok.Generated;
import org.zenith.ZenithColor;

public class GlowColor {
   public final ZenithColor zenithColor;
   public final ZenithColor zenithColor2;
   public final ZenithColor zenithColor3;
   public final ZenithColor zenithColor4;

   public GlowColor(ZenithColor var1, ZenithColor var2, ZenithColor var3, ZenithColor var4) {
      this.zenithColor = var1;
      this.zenithColor2 = var2;
      this.zenithColor3 = var3;
      this.zenithColor4 = var4;
   }

   public static GlowColor method8096(ZenithColor var0, ZenithColor var1, ZenithColor var2, ZenithColor var3) {
      return new GlowColor(var0, var1, var2, var3);
   }

   public static GlowColor method8097(ZenithColor var0) {
      return new GlowColor(var0, var0, var0, var0);
   }

   public static GlowColor method8098(List<ZenithColor> var0) {
      return new GlowColor((ZenithColor)var0.get(0), (ZenithColor)var0.get(1), (ZenithColor)var0.get(2), (ZenithColor)var0.get(3));
   }

   public GlowColor method8099() {
      return this;
   }

   public GlowColor method8100(float var1) {
      return new GlowColor(
         this.zenithColor.method7294(var1), this.zenithColor2.method7294(var1), this.zenithColor3.method7294(var1), this.zenithColor4.method7294(var1)
      );
   }

   @Generated
   public ZenithColor getZenithColor() {
      return this.zenithColor;
   }

   @Generated
   public ZenithColor getZenithColor2() {
      return this.zenithColor2;
   }

   @Generated
   public ZenithColor getZenithColor3() {
      return this.zenithColor3;
   }

   @Generated
   public ZenithColor getZenithColor4() {
      return this.zenithColor4;
   }
}

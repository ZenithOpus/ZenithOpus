package org.zenith.client.screens.nlgui.elements;

import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class RollingText {
   private static final long DURATION_MS = 230L;
   private static final long STAGGER_MS = 22L;
   private static final float DISTANCE = 3.5F;
   private String current;
   private String previous;
   private long changedAt = -1L;
   private int direction = 1;

   public void snap(String var1) {
      this.current = var1;
      this.previous = null;
      this.changedAt = -1L;
   }

   public float draw(MenuDrawContext var1, Font var2, String var3, float var4, float var5, ZenithColor var6, int var7) {
      if (var3 == null) {
         var3 = "";
      }

      long var8 = System.currentTimeMillis();
      if (this.current == null) {
         this.current = var3;
      } else if (!this.current.equals(var3)) {
         this.previous = this.current;
         this.current = var3;
         this.changedAt = var8;
         this.direction = var7 >= 0 ? 1 : -1;
      }

      float var10 = var2.width(this.current);
      if (this.previous == null) {
         var1.drawText(var2, this.current, var4 - var10, var5, var6);
         return var10;
      } else {
         int var11 = this.current.length();
         int var12 = this.previous.length();
         int var13 = 0;

         while (var13 < var11 && var13 < var12 && this.current.charAt(var11 - 1 - var13) == this.previous.charAt(var12 - 1 - var13)) {
            var13++;
         }

         long var14 = var8 - this.changedAt;
         int var16 = Math.max(var11, var12) - var13;
         if (var14 >= 230L + 22L * Math.max(0, var16 - 1)) {
            this.previous = null;
            this.changedAt = -1L;
            var1.drawText(var2, this.current, var4 - var10, var5, var6);
            return var10;
         } else {
            float var17 = var6.getIntValue4() / 255.0F;
            float var18 = var4;

            for (int var19 = 0; var19 < var11; var19++) {
               String var20 = String.valueOf(this.current.charAt(var11 - 1 - var19));
               var18 -= var2.width(var20);
               if (var19 < var13) {
                  var1.drawText(var2, var20, var18, var5, var6);
               } else {
                  float var21 = progress(var14 - (var19 - var13) * 22L);
                  if (!(var21 <= 0.0F)) {
                     float var22 = easeOut(var21);
                     var1.drawText(var2, var20, var18, var5 + this.direction * 3.5F * (1.0F - var22), var6.method7292(var17 * var21));
                  }
               }
            }

            var18 = var4;

            for (int var24 = 0; var24 < var12; var24++) {
               String var25 = String.valueOf(this.previous.charAt(var12 - 1 - var24));
               var18 -= var2.width(var25);
               if (var24 >= var13) {
                  float var26 = progress(var14 - (var24 - var13) * 22L);
                  if (!(var26 >= 1.0F)) {
                     float var27 = easeOut(var26);
                     var1.drawText(var2, var25, var18, var5 - this.direction * 3.5F * var27, var6.method7292(var17 * (1.0F - var26)));
                  }
               }
            }

            return var10;
         }
      }
   }

   public boolean isRolling() {
      return this.previous != null;
   }

   private static float progress(long var0) {
      return MathHelper.clamp((float)var0 / 230.0F, 0.0F, 1.0F);
   }

   private static float easeOut(float var0) {
      float var1 = 1.0F - var0;
      return 1.0F - var1 * var1 * var1;
   }
}

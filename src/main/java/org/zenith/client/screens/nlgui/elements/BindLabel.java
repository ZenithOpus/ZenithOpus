package org.zenith.client.screens.nlgui.elements;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class BindLabel {
   public static final String HINT = "+";
   private static final long HINT_DELAY_MS = 350L;
   private static final long HINT_FADE_MS = 220L;
   private static final float HIT_PAD = 3.0F;
   private static final long ANIM_MS = 800L;
   private static final long ENTER_DELAY_MS = 90L;
   private static final String[] ASCII = new String[128];
   private static final Map<String, String> SHORT_NAMES = new ConcurrentHashMap<>();
   private final Animation hintFade = new Animation(220L, 0.0F, Easing.INSTANCE12);
   private BindLabel.Kind kind = BindLabel.Kind.NONE;
   private long startedAt;
   private String from;
   private String shown;
   private long hoverStartAt = -1L;
   private boolean binding;

   private static String glyph(char var0) {
      return var0 < ASCII.length ? ASCII[var0] : String.valueOf(var0);
   }

   public void waiting() {
      this.start(BindLabel.Kind.WAIT);
   }

   public void set() {
      this.start(BindLabel.Kind.SET);
   }

   public void cleared() {
      this.start(BindLabel.Kind.CLEAR);
   }

   private void start(BindLabel.Kind var1) {
      this.from = this.shown;
      this.kind = var1;
      this.startedAt = System.currentTimeMillis();
   }

   public static String waitText() {
      return Zenith.getInstance().getLocalization().translate("gui.modules.bind.wait");
   }

   public static String text(boolean var0, int var1, int var2) {
      if (var0) {
         return waitText();
      } else if (var1 != -1 && var1 != 0) {
         try {
            String var3 = KeyName.method6702(var1, var2);
            if (var3 != null && !var3.isBlank()) {
               String var4 = SHORT_NAMES.get(var3);
               if (var4 == null) {
                  var4 = shortKeyName(var3.toUpperCase());
                  if (SHORT_NAMES.size() < 512) {
                     SHORT_NAMES.put(var3, var4);
                  }
               }

               return var4;
            }
         } catch (Exception var5) {
         }

         return "?";
      } else {
         return null;
      }
   }

   public static String shortKeyName(String var0) {
      String var1 = var0.replace(" + ", "+")
         .replace("SHIFT", "SHFT")
         .replace("SUPER", "WIN")
         .replace("RIGHT_", "R")
         .replace("LEFT_", "L")
         .replace("CONTROL", "CTRL")
         .replace("KEY_PAD_", "NUM")
         .replace("PAGE_", "PG");
      int var2 = var1.indexOf(43) >= 0 ? 14 : 8;
      return var1.length() <= var2 ? var1 : var1.substring(0, var2);
   }

   public BoundsData render(
      MenuDrawContext var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      String var7,
      boolean var8,
      Font var9,
      float var10,
      boolean var11,
      float var12,
      BoundsData var13,
      ZenithStyle var14
   ) {
      this.binding = var8;
      long var15 = System.currentTimeMillis();
      if (var11) {
         if (this.hoverStartAt < 0L) {
            this.hoverStartAt = var15;
         }
      } else {
         this.hoverStartAt = -1L;
      }

      ZenithColor var17 = var14.getPrimaryColor().getZenithColor();
      ZenithColor var18 = var14.getTextTertiary().getZenithColor();
      ZenithColor var19 = var14.getTextEnable().getZenithColor();
      float var20 = var4 + (var5 - var9.height()) / 2.0F;
      float var21 = var20 + var9.height() + 0.6F;
      long var22 = this.kind == BindLabel.Kind.NONE ? Long.MAX_VALUE : var15 - this.startedAt;
      if (var22 >= 800L) {
         this.kind = BindLabel.Kind.NONE;
         this.from = null;
      }

      boolean var24 = this.kind != BindLabel.Kind.NONE;
      boolean var25 = var7 == null && !var24 && this.hoverStartAt >= 0L && var15 - this.hoverStartAt >= 350L && var10 <= var6 - var9.width("+");
      float var26 = this.hintFade.method2808(var25 ? 1.0F : 0.0F);
      if (var24 || var7 != null) {
         this.hintFade.method2809(0.0F);
         var26 = 0.0F;
      }

      String var27 = var7 != null ? var7 : (var25 && var26 > 0.3F ? "+" : null);
      BoundsData var28 = null;
      if (var27 != null) {
         float var29 = var9.width(var27);
         var28 = new BoundsData(var6 - var29 - 3.0F, var4, var29 + 6.0F, var5);
      }

      boolean var39 = var28 != null && var28.contains(var2, var3) && (var13 == null || var13.contains(var2, var3));
      if (var24 && this.from != null) {
         boolean var30 = this.from.equals(waitText());
         ZenithColor var31 = var30 ? var17.method7294(0.8F) : var18.method7295(var19, 0.1F);
         this.drawExit(var1, var9, this.from, var6, var20, var22, var31, var12);
         if (this.kind == BindLabel.Kind.CLEAR) {
            float var32 = MathHelper.clamp((float)var22 / 260.0F, 0.0F, 1.0F);
            float var33 = var9.width(this.from) * (1.0F - easeOut(var32));
            float var34 = var6 - var9.width(this.from) / 2.0F;
            if (var33 > 0.3F) {
               var1.drawRect(var34 - var33 / 2.0F, var21, var33, 0.5F, (var30 ? var17 : var18).method7294((1.0F - var32) * 0.8F * var12));
            }
         }
      }

      if (var7 != null) {
         float var40 = var9.width(var7);
         float var42 = var6 - var40;
         ZenithColor var44 = var8 ? var17 : var18.method7295(var19, var39 ? 0.55F : 0.1F);
         long var45 = var24 ? var22 - 90L : Long.MAX_VALUE;
         float var35 = "+".equals(var7) ? hintY(var9, var4, var5, var20) : var20;
         this.drawEnter(var1, var9, var7, var42, var35, var45, var44, var17, var15, var12);
         if (var8) {
            float var36 = var24 ? MathHelper.clamp((float)var45 / 220.0F, 0.0F, 1.0F) : 1.0F;
            var1.drawRect(var42, var21, var40 * var36, 0.5F, var17.method7294(0.22F * var12));
            float var37 = var40 * 0.32F;
            float var38 = (0.5F + 0.5F * MathHelper.sin((float)var15 / 260.0F)) * (var40 - var37);
            var1.drawRect(var42 + var38, var21, var37 * var36, 0.5F, var17.method7294(0.9F * var36 * var12));
         } else if (var24 && this.kind == BindLabel.Kind.SET) {
            float var46 = easeOut(MathHelper.clamp((float)var45 / 260.0F, 0.0F, 1.0F));
            float var47 = 1.0F - MathHelper.clamp(((float)var45 - 300.0F) / 320.0F, 0.0F, 1.0F);
            if (var45 > 0L && var47 > 0.0F) {
               float var48 = var40 * var46;
               var1.drawRect(var42 + (var40 - var48) / 2.0F, var21, var48, 0.5F, var17.method7295(ZenithColor.INSTANCE, 0.3F).method7294(var47 * var12));
            }

            drawSparks(var1, var42 + var40 / 2.0F, var20 + var9.height() / 2.0F, var45, var17, var12);
         } else if (var39 && !var24) {
            var1.drawRect(var42, var21, var40, 0.5F, var18.method7294(0.5F * var12));
         }
      } else if (var26 > 0.0F) {
         float var41 = var9.width("+");
         ZenithColor var43 = var18.method7295(var19, var39 ? 0.6F : 0.0F).method7294((var39 ? 0.9F : 0.45F) * var26 * var12);
         var1.drawText(var9, "+", var6 - var41, hintY(var9, var4, var5, var20) + (1.0F - easeOut(var26)) * 1.5F, var43);
      }

      this.shown = var7 != null ? var7 : (var26 > 0.5F ? "+" : null);
      return var28;
   }

   private static float hintY(Font var0, float var1, float var2, float var3) {
      float[] var4 = var0.inkBounds("+");
      return var4 == null ? var3 : var1 + (var2 - var4[1]) / 2.0F - var0.baseline() + var4[2];
   }

   private void drawExit(MenuDrawContext var1, Font var2, String var3, float var4, float var5, long var6, ZenithColor var8, float var9) {
      boolean var10 = this.kind == BindLabel.Kind.CLEAR;
      long var11 = var10 ? 28L : 16L;
      float var13 = var10 ? 360.0F : 190.0F;
      int var14 = var3.length();
      float var15 = var4 - var2.width(var3);

      for (int var16 = 0; var16 < var14; var16++) {
         String var17 = glyph(var3.charAt(var16));
         float var18 = var2.width(var17);
         long var19 = (var10 ? var14 - 1 - var16 : var16) * var11;
         float var21 = MathHelper.clamp((float)(var6 - var19) / var13, 0.0F, 1.0F);
         if (var21 >= 1.0F) {
            var15 += var18;
         } else {
            float var22 = 0.0F;
            float var23;
            if (var10) {
               var23 = 5.0F * var21 * var21;
               var22 = (hash01(var16 * 31 + var14) - 0.5F) * 3.5F * var21;
            } else {
               var23 = -3.0F * var21 * var21;
            }

            var1.drawText(var2, var17, var15 + var22, var5 + var23, var8.method7294((1.0F - var21) * var9));
            var15 += var18;
         }
      }
   }

   private void drawEnter(
      MenuDrawContext var1, Font var2, String var3, float var4, float var5, long var6, ZenithColor var8, ZenithColor var9, long var10, float var12
   ) {
      int var13 = var3.length();
      boolean var14 = var6 == Long.MAX_VALUE;

      for (int var15 = 0; var15 < var13; var15++) {
         String var16 = glyph(var3.charAt(var15));
         float var17 = var2.width(var16);
         float var18 = 0.0F;
         float var19 = 1.0F;
         ZenithColor var20 = var8;
         if (!var14) {
            if (this.kind == BindLabel.Kind.SET) {
               float var21 = MathHelper.clamp((float)(var6 - var15 * 35L) / 420.0F, 0.0F, 1.0F);
               var18 = -4.0F * (float)(Math.exp(-7.0F * var21) * Math.cos(11.0F * var21));
               var19 = Math.min(1.0F, var21 * 4.0F);
               var20 = var8.method7295(var9.method7295(ZenithColor.INSTANCE, 0.5F), (1.0F - var21) * (1.0F - var21));
            } else {
               float var23 = MathHelper.clamp((float)(var6 - var15 * 18L) / 200.0F, 0.0F, 1.0F);
               var18 = (1.0F - easeOut(var23)) * 3.0F;
               var19 = var23;
            }
         }

         if (this.binding) {
            float var24 = (float)(var10 % 900L) / 900.0F * (var13 + 4.0F) - 2.0F;
            float var22 = Math.max(0.0F, 1.0F - Math.abs(var15 - var24) / 1.6F);
            var20 = var20.method7295(ZenithColor.INSTANCE, 0.55F * var22);
         }

         if (var19 > 0.01F) {
            var1.drawText(var2, var16, var4, var5 + var18, var20.method7294(var19 * var12));
         }

         var4 += var17;
      }
   }

   private static void drawSparks(MenuDrawContext var0, float var1, float var2, long var3, ZenithColor var5, float var6) {
      float var7 = MathHelper.clamp((float)var3 / 450.0F, 0.0F, 1.0F);
      if (var3 >= 0L && !(var7 >= 1.0F)) {
         float var8 = easeOut(var7);

         for (int var9 = 0; var9 < 6; var9++) {
            float var10 = (var9 * 60.0F + 30.0F) * (float) (Math.PI / 180.0);
            float var11 = var8 * (6.0F + var9 % 2 * 3.0F);
            float var12 = 1.2F * (1.0F - var7);
            if (!(var12 < 0.05F)) {
               float var13 = var1 + MathHelper.cos(var10) * var11 * 1.4F;
               float var14 = var2 + MathHelper.sin(var10) * var11 * 0.8F;
               var0.drawRoundedRect(
                  var13 - var12 / 2.0F,
                  var14 - var12 / 2.0F,
                  var12,
                  var12,
                  ProxyDotRadiusData.method8069(var12 / 2.0F),
                  (var9 % 2 == 0 ? var5 : var5.method7295(ZenithColor.INSTANCE, 0.5F)).method7294((1.0F - var7) * var6)
               );
            }
         }
      }
   }

   private static float easeOut(float var0) {
      float var1 = 1.0F - var0;
      return 1.0F - var1 * var1 * var1;
   }

   private static float hash01(int var0) {
      int var1 = var0 * 668265261;
      var1 ^= var1 >>> 15;
      var1 *= -2048144789;
      var1 ^= var1 >>> 13;
      return (var1 >>> 8) / 1.6777216E7F;
   }

   static {
      for (int var0 = 0; var0 < ASCII.length; var0++) {
         ASCII[var0] = String.valueOf((char)var0);
      }
   }

   private static enum Kind {
      NONE,
      WAIT,
      SET,
      CLEAR;
   }
}

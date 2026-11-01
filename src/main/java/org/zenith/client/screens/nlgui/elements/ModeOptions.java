package org.zenith.client.screens.nlgui.elements;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3x2fStack;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class ModeOptions {
   private static final float MARGIN_TOP = 1.5F;
   private static final float MARGIN_BOTTOM = 2.0F;
   private static final float INSET = 2.0F;
   private static final float ROW = 11.0F;
   private static final float ROW_GAP = 1.0F;
   private static final float ROW_H = 10.0F;
   private static final float DOT = 2.5F;
   private static final float DOT_X = 4.0F;
   private static final float TEXT_X = 5.0F;
   private static final float TEXT_SHIFT = 5.5F;
   private static final float TEXT_RIGHT = 3.0F;
   private static final float MIN_WIDTH = 12.0F;
   private static final float PANEL_ROUND = 3.0F;
   private static final float ROW_ROUND = 2.2F;
   private static final float INDICATOR_SPEED = 22.0F;
   private static final long SNAP_GAP_NANOS = 250000000L;
   private final ModeSetting setting;
   private final Map<ModeSetting.Entry, ModeOptions.Option> options = new IdentityHashMap<>();
   private final List<ModeOptions.Option> layout = new ArrayList<>();
   private float layoutWidth = Float.NaN;
   private float layoutHeight;
   private float indicatorY;
   private boolean indicatorKnown;
   private long lastFrameNanos;

   public ModeOptions(ModeSetting var1) {
      this.setting = var1;
   }

   public void prepare(float var1) {
      var1 = Float.isFinite(var1) ? Math.max(0.0F, var1) : 0.0F;
      var var2 = this.setting.getValues();
      boolean var3 = var1 != this.layoutWidth || this.layout.size() != var2.size();

      for (int var4 = 0; var4 < var2.size(); var4++) {
         ModeSetting.Entry var5 = (ModeSetting.Entry)var2.get(var4);
         ModeOptions.Option var6 = this.options.computeIfAbsent(var5, var0 -> new ModeOptions.Option());
         var6.value = var5;
         var6.name = var5.getName();
         if (var4 >= this.layout.size() || this.layout.get(var4) != var6) {
            var3 = true;
         }
      }

      if (var3) {
         this.clearBounds();
         this.options.keySet().retainAll(var2);
         this.layout.clear();
         this.layoutWidth = var1;
         this.indicatorKnown = false;
         if (!var2.isEmpty() && !(var1 <= 4.0F)) {
            for (int var8 = 0; var8 < var2.size(); var8++) {
               ModeOptions.Option var9 = this.options.get(var2.get(var8));
               var9.y = 3.5F + var8 * 11.0F;
               this.layout.add(var9);
            }

            this.layoutHeight = 5.5F + var2.size() * 11.0F - 1.0F + 2.0F;
         } else {
            this.layoutHeight = 0.0F;
         }
      }
   }

   public float height() {
      return this.layoutHeight;
   }

   public float height(float var1) {
      this.prepare(var1);
      return this.layoutHeight;
   }

   public void clearBounds() {
      for (ModeOptions.Option var2 : this.options.values()) {
         var2.bounds = null;
      }
   }

   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, boolean var9, ZenithStyle var10) {
      this.prepare(var6);
      this.clearBounds();
      if (!(var6 <= 4.0F) && !this.layout.isEmpty() && !(var7 <= 0.01F) && !(var8 <= 0.01F)) {
         Font var11 = Fonts.NEW_MEDIUM.getFont(5.01F);
         ZenithColor var12 = var10.getPrimaryColor().getZenithColor();
         ZenithColor var13 = var10.getTextEnable().getZenithColor();
         ZenithColor var14 = var10.getTextSecondary().getZenithColor();
         ZenithColor var15 = var10.getFieldBorder().getZenithColor();
         long var16 = System.nanoTime();
         float var18 = MathHelper.clamp(var7 / Math.max(this.layoutHeight, 1.0F), 0.0F, 1.0F);
         boolean var19 = isMouseInsideClip(var1, var2, var3);
         float var20 = var5 + 1.5F;
         float var21 = this.layoutHeight - 1.5F - 2.0F;
         float var22 = var8 * MathHelper.clamp(var18 * 1.6F, 0.0F, 1.0F);
         ProxyDotRadiusData var23 = ProxyDotRadiusData.method8069(3.0F);
         var1.drawRoundedRect(var4, var20, var6, var21, var23, var10.getFieldSurfaceBackground().getZenithColor().method7294(var22));
         var1.drawRoundedBorder(var4, var20, var6, var21, 0.35F, var23, var15.method7294(0.45F * var22));
         float var24 = var4 + 2.0F;
         float var25 = var6 - 4.0F;
         ProxyDotRadiusData var26 = ProxyDotRadiusData.method8069(2.2F);
         int var27 = -1;

         for (int var28 = 0; var28 < this.layout.size(); var28++) {
            ModeOptions.Option var29 = this.layout.get(var28);
            var29.selection.method2808(var29.value.isSelected() ? 1.0F : 0.0F);
            if (var29.value.isSelected()) {
               var27 = var28;
            }
         }

         if (var27 >= 0) {
            this.updateIndicator(this.layout.get(var27).y, var16);
            float var42 = reveal(var18, var27);
            float var44 = var8 * var42;
            if (var44 > 0.001F && this.indicatorY < var7) {
               float var30 = var5 + this.indicatorY + (1.0F - var42) * 1.5F;
               var1.drawRoundedRect(var24, var30, var25, 10.0F, var26, var12.method7292(0.1F * var44));
               var1.drawRoundedBorder(var24, var30, var25, 10.0F, 0.35F, var26, var12.method7292(0.3F * var44));
            }
         } else {
            this.indicatorKnown = false;
         }

         this.lastFrameNanos = var16;

         for (int var43 = 0; var43 < this.layout.size(); var43++) {
            ModeOptions.Option var45 = this.layout.get(var43);
            if (var45.y >= var7) {
               break;
            }

            float var46 = reveal(var18, var43);
            float var31 = var8 * var46;
            if (!(var31 <= 0.001F)) {
               float var32 = var5 + var45.y + (1.0F - var46) * 1.5F;
               if (var9 && var46 >= 0.92F && var25 >= 12.0F && var45.y + 11.0F <= var7 + 0.01F) {
                  var45.bounds = new BoundsData(var24, var32, var25, 10.0F);
               }

               boolean var33 = var19 && var45.bounds != null && var45.bounds.contains(var2, var3);
               float var34 = var45.hover.method2808(var33 ? 1.0F : 0.0F);
               float var35 = var45.selection.getValue();
               float var36 = var34 * (1.0F - var35);
               if (var36 > 0.01F) {
                  var1.drawRoundedRect(var24, var32, var25, 10.0F, var26, var13.method7292(0.04F * var36 * var31));
               }

               float var37 = 2.5F * var35;
               if (var37 > 0.05F) {
                  var1.drawRoundedRect(
                     var24 + 4.0F + (2.5F - var37) / 2.0F,
                     var32 + (10.0F - var37) / 2.0F,
                     var37,
                     var37,
                     ProxyDotRadiusData.method8069(var37 / 2.0F),
                     var12.method7294(var31)
                  );
               }

               float var38 = var24 + 5.0F + 5.5F * var35;
               float var39 = Math.max(0.0F, var24 + var25 - 3.0F - var38);
               ZenithColor var40 = var14.method7295(var13, Math.max(var35, var34 * 0.7F));
               var45.label.render(var1, var11, var45.name, var38, var32 + (10.0F - var11.height()) / 2.0F, var39, var40.method7294(var31), var33);
               if (var33) {
                  String var41 = var45.value.getDescription();
                  HoverInfo.request(var45.name, var41.isEmpty() ? this.setting.getDescription() : Zenith.getInstance().getLocalization().translate(var41));
               }
            }
         }
      }
   }

   private static float reveal(float var0, int var1) {
      float var2 = Math.min(var1 * 0.03F, 0.2F);
      float var3 = MathHelper.clamp((var0 - var2) / (1.0F - var2), 0.0F, 1.0F);
      return var3 * var3 * (3.0F - 2.0F * var3);
   }

   private void updateIndicator(float var1, long var2) {
      long var4 = var2 - this.lastFrameNanos;
      if (this.indicatorKnown && var4 >= 0L && var4 <= 250000000L) {
         float var6 = 1.0F - (float)Math.exp(-22.0F * (float)var4 / 1.0E9);
         this.indicatorY = this.indicatorY + (var1 - this.indicatorY) * var6;
      } else {
         this.indicatorY = var1;
         this.indicatorKnown = true;
      }
   }

   private static boolean isMouseInsideClip(MenuDrawContext var0, float var1, float var2) {
      ScreenRect var3 = var0.scissorStack.peekLast();
      if (var3 == null) {
         return true;
      } else {
         Matrix3x2fStack var4 = var0.getMatrices();
         float var5 = var4.m00() * var1 + var4.m10() * var2 + var4.m20();
         float var6 = var4.m01() * var1 + var4.m11() * var2 + var4.m21();
         return var5 >= var3.getLeft() && var5 < var3.getRight() && var6 >= var3.getTop() && var6 < var3.getBottom();
      }
   }

   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.setting.isVisible() && var5 == Enum3.LEFT) {
         for (ModeSetting.Entry var7 : this.setting.getValues()) {
            ModeOptions.Option var8 = this.options.get(var7);
            if (var8 != null && var8.bounds != null && var8.bounds.contains(var1, var3)) {
               if (!var7.isSelected()) {
                  var7.method2103();
                  Zenith.getInstance().getClientSounds().method1570(false);
               }

               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static final class Option {
      public final Animation hover = new Animation(110L, 0.0F, Easing.INSTANCE12);
      public final Animation selection = new Animation(180L, 0.0F, Easing.INSTANCE15);
      public final HoverScrollText label = new HoverScrollText();
      public ModeSetting.Entry value;
      public String name;
      public float y;
      public BoundsData bounds;
   }
}

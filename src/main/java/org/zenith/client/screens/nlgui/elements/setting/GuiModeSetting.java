package org.zenith.client.screens.nlgui.elements.setting;

import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.elements.HoverScrollText;
import org.zenith.client.screens.nlgui.elements.ModeOptions;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public class GuiModeSetting extends GuiSetting<ModeSetting> {
   private final Animation animationExpanded = new Animation(180L, 0.0F, Easing.INSTANCE13);
   private final ModeOptions options;
   private final HoverScrollText labelScroll = new HoverScrollText();
   private final HoverScrollText valueScroll = new HoverScrollText();
   private BoundsData bounds;
   private boolean expanded;

   public GuiModeSetting(ModeSetting var1) {
      this(var1, 166.0F);
   }

   public GuiModeSetting(ModeSetting var1, float var2) {
      super(var2, var1);
      this.options = new ModeOptions(var1);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.setting.isVisible() && (var5 == Enum3.LEFT || var5 == Enum3.RIGHT)) {
         if (this.expanded && this.options.onMouseClicked(var1, var3, var5)) {
            return true;
         } else if (this.bounds != null && this.bounds.contains(var1, var3)) {
            this.expanded = (!this.expanded);
            this.options.clearBounds();
            Zenith.getInstance().getClientSounds().method1570(false);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      this.options.clearBounds();
      this.bounds = null;
      this.animationVisible.method2807(this.setting.isVisible());
      var6 *= this.animationVisible.getValue();
      ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var7 != null && !(var6 <= 0.01F)) {
         float var8 = this.animationExpanded.method2808(this.expanded ? 1.0F : 0.0F);
         Font var9 = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font var10 = Fonts.NEW_MEDIUM.getFont(5.01F);
         this.bounds = new BoundsData(var4, var5, this.width, this.rowHeight());
         boolean var11 = this.bounds.contains(var2, var3);
         ZenithColor var12 = var7.getTextEnable().getZenithColor().method7294(var6);
         String var13 = this.expanded
            ? String.valueOf(this.setting.getValues().size())
            : (this.setting.getValue2() == null ? "—" : this.setting.getValue2().getName());
         float var14 = Math.min(this.width * 0.4F, Math.max(10.0F, var10.width(var13)));
         float var15 = var4 + this.width - var14 - 10.0F;
         float var16 = var4;
         if (Zenith.getInstance().getNLMenuScreen().isRenderIcon()) {
            MenuGlyph.MODE.draw(var1, var4, var5 + (this.rowHeight() - 6.0F) / 2.0F, var7.getPrimaryColor().getZenithColor().method7294(var6));
            var16 = var4 + (6.0F + GuiStyle.PADDING.intValue());
         }

         this.labelScroll
            .render(
               var1,
               var9,
               this.setting.getName(),
               var16,
               var5 + (this.rowHeight() - var9.height()) / 2.0F,
               Math.max(0.0F, var15 - GuiStyle.PADDING.intValue() - var16),
               var12,
               var11
            );
         this.valueScroll
            .render(
               var1,
               var10,
               var13,
               var15,
               var5 + (this.rowHeight() - var10.height()) / 2.0F,
               var14,
               var7.getTextTertiary().getZenithColor().method7294(var6),
               var11
            );
         MenuGlyph.drawChevron(var1, var4 + this.width - 5.0F, var5 + this.rowHeight() / 2.0F, var8, var12);
         if (var11) {
            HoverInfo.request(this.setting.getName(), this.setting.getDescription());
         }

         float var17;
         if ((var17 = this.options.height(this.width) * var8) > 0.01F) {
            float var18 = var5 + this.rowHeight();
            var1.enableScissor(var4, var18, var4 + this.width, var18 + var17);

            try {
               this.options.render(var1, var2, var3, var4, var18, this.width, var17, var6 * var8, (this.expanded && var6 > 0.99F), var7);
            } finally {
               var1.disableScissor();
            }
         }
      }
   }

   @Override
   public float getHeight() {
      return this.rowHeight() + this.options.height(this.width) * this.animationExpanded.getValue();
   }

   @Override
   public float rowHeight() {
      return 14.0F;
   }
}

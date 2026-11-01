package org.zenith.client.screens.nlgui.elements;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import kotlin.collections.EmptyList;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3x2fStack;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.SvgIcon;
import org.zenith.client.screens.nlgui.BindCapture;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.MouseBindTarget;
import org.zenith.client.screens.nlgui.SettingIcons;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.BlockListSetting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.misc.Localization;
import org.zenith.utility.misc.ModuleHeaderSetting;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class InspectorRow implements MouseBindTarget {
   private static final float INSET = 8.0F;
   private static final float INDENT = 8.0F;
   private static final float VALUE_COLUMN = 30.0F;
   private static final float TRACK_WIDTH = 34.0F;
   private static final float TRACK_MIN = 18.0F;
   private static final float SWATCH = 7.0F;
   private static final float SWATCH_FILL_INSET = 0.5F;
   private static final float ICON_COLUMN = 9.0F;
   private static final float SWITCH_WIDTH = 9.0F;
   private static final float SWITCH_HEIGHT = 5.0F;
   private static final float CHEVRON_SIZE = 5.0F;
   private static final float CHEVRON_COLUMN = 7.0F;
   private static final float TRACK_GRAB = 4.0F;
   private static final float HANDLE_GRAB = 5.0F;
   private final Setting setting;
   private final int depth;
   private final List<InspectorRow> children = new ArrayList<>();
   private final Animation expandAnimation = new Animation(180L, 0.0F, Easing.INSTANCE13);
   private final Animation visibleAnimation;
   private final Animation toggleAnimation;
   private final RollingText valueRoll = new RollingText();
   private final RollingText hexRoll = new RollingText();
   private long lastFrameAt;
   private float frameDt;
   private final Animation hoverAnimation = new Animation(120L, 0.0F, Easing.INSTANCE12);
   private HoverScrollText labelScroll;
   private float springPos = Float.NaN;
   private float springVel;
   private float lastNumber = Float.NaN;
   private int numberDirection = 1;
   private float numberTextValue = Float.NaN;
   private SliderSetting.Handler numberTextDisplay;
   private String numberText;
   private int hexKey = -1;
   private String hexText;
   private final Animation dragAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private long edgeHitAt = -1L;
   private int edgeSide;
   private boolean edgePinned;
   private long flashAt = -1L;
   private long shakeAt = -1L;
   private long editStartAt = -1L;
   private long typedAt = -1L;
   private ZenithColor shownColor;
   private long pickerToggledAt = -1L;
   private static final int KEY_SET = 1;
   private static final int KEY_CLEAR = 2;
   private static final long KEY_ANIM_MS = 720L;
   private int keyAnim;
   private long keyAnimAt;
   private String keyFrom;
   private String keyBeforeBinding;
   private final Animation keyCapWidth = new Animation(200L, 0.0F, Easing.INSTANCE12);
   private boolean keyCapKnown;
   private final Animation keyHover = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private static final long FLASH_MS = 520L;
   private static final long SHAKE_MS = 360L;
   private static final long EDGE_MS = 380L;
   private float trackLeftPx = Float.NaN;
   private float trackSpanPx = 34.0F;
   private float sharedTrackSpan = Float.NaN;
   private float valueLeftPx = Float.NaN;
   private boolean expanded;
   private boolean dragging;
   private float dragOffset;
   private boolean binding;
   private final BindSetting bindSetting;
   private final BindLabel bindLabel = new BindLabel();
   private boolean editing;
   private StringBuilder editBuffer;
   private String editOriginal = "";
   private final SvgIcon subjectGlyph;
   private final ColorPicker picker;
   private final ModeOptions modeOptions;
   private boolean modeHoverAllowed = true;
   private float pickerHeight;
   private BoundsData bounds;
   private BoundsData valueBounds;
   private String lastCounter;
   private static final int LETTERS_RISE = 0;
   private static final int LETTERS_DROP = 1;
   private static final int LETTERS_CRUMBLE = 2;

   public InspectorRow(Setting var1, int var2) {
      this(var1, var2, "");
   }

   public InspectorRow(Setting var1, int var2, String var3) {
      this(var1, var2, "", var3);
   }

   private InspectorRow(Setting var1, int var2, String var3, String var4) {
      this.setting = var1;
      this.bindSetting = (BindSetting)(var1 instanceof ModuleHeaderSetting var6
         ? var6.getHeaderBindSetting()
         : (var1 instanceof BindSetting var5 ? var5 : null));
      this.depth = var2;
      this.subjectGlyph = SettingIcons.resolve(var1, var3, var4);
      this.picker = var1 instanceof ColorSetting var9 ? new ColorPicker(var9) : null;
      this.modeOptions = var1 instanceof ModeSetting var10 ? new ModeOptions(var10) : null;
      this.pickerHeight = this.picker == null ? 0.0F : this.picker.height(this.pickerWidth(144.0F));
      this.visibleAnimation = new Animation(160L, var1 != null && var1.isVisible() ? 1.0F : 0.0F, Easing.INSTANCE13);
      this.toggleAnimation = new Animation(220L, var1 instanceof BooleanSetting var11 && var11.isEnabled() ? 1.0F : 0.0F, Easing.INSTANCE10);
      if (var1 instanceof GroupSetting var12) {
         for (Setting var8 : var12.getSettings()) {
            this.children.add(new InspectorRow(var8, var2 + 1, var1.getKey(), var4));
         }
      } else if (var1 instanceof MultiSelectSetting var13) {
         for (MultiSelectSetting.Entry var15 : var13.getItems2()) {
            this.children.add(new InspectorRow(new InspectorRow.MultiValueAdapter(var13, var15), var2 + 1, var1.getKey(), var4));
         }
      }
   }

   public boolean isVisible() {
      return this.visibleAnimation.getValue() > 0.001F || this.setting.isVisible();
   }

   public static void alignTracks(List<InspectorRow> var0, float var1) {
      float var2 = 34.0F;

      for (InspectorRow var4 : var0) {
         var4.prepareLayout(var1);
         var2 = Math.min(var2, var4.narrowestTrack(var1));
      }

      for (InspectorRow var6 : var0) {
         var6.shareTrack(var2);
      }
   }

   private void prepareLayout(float var1) {
      if (this.modeOptions != null) {
         this.modeOptions.prepare(Math.max(0.0F, var1 - 16.0F - this.depth * 8.0F));
      }

      for (InspectorRow var3 : this.children) {
         var3.prepareLayout(var1);
      }
   }

   private float narrowestTrack(float var1) {
      float var2 = 34.0F;
      if (this.setting instanceof SliderSetting && this.setting.isVisible()) {
         Font var3 = Fonts.NEW_MEDIUM.getFont(5.2F);
         float var4 = this.labelOffset() + var3.width(this.label());
         var2 = MathHelper.clamp(var1 - 8.0F - 30.0F - var4 - GuiStyle.PADDING.intValue(), 18.0F, 34.0F);
      }

      for (InspectorRow var6 : this.children) {
         var2 = Math.min(var2, var6.narrowestTrack(var1));
      }

      return var2;
   }

   private void shareTrack(float var1) {
      this.sharedTrackSpan = var1;

      for (InspectorRow var3 : this.children) {
         var3.shareTrack(var1);
      }
   }

   private float labelOffset() {
      float var1 = 8.0F + this.depth * 8.0F;
      if (Zenith.getInstance().getNLMenuScreen().isRenderIcon()) {
         var1 += 9.0F;
      }

      return var1;
   }

   private boolean expandable() {
      return (this.children != null && this.children.getClass() == EmptyList.class ? false : !this.children.isEmpty())
         || this.picker != null
         || this.modeOptions != null;
   }

   private float ownHeight() {
      return 16.0F;
   }

   private float pickerWidth(float var1) {
      return Math.max(40.0F, var1 - 16.0F - this.depth * 8.0F);
   }

   public float height() {
      float var1 = this.ownHeight() * this.visibleAnimation.getValue();
      if (this.modeOptions != null) {
         return var1 + this.modeOptions.height() * this.expandAnimation.getValue() * this.visibleAnimation.getValue();
      } else if (this.picker != null) {
         return var1 + this.pickerHeight * this.expandAnimation.getValue() * this.visibleAnimation.getValue();
      } else if (this.children != null && this.children.getClass() == EmptyList.class ? true : this.children.isEmpty()) {
         return var1;
      } else {
         float var2 = 0.0F;

         for (InspectorRow var4 : this.children) {
            if (var4.isVisible()) {
               var2 += var4.height();
            }
         }

         return var1 + var2 * this.expandAnimation.getValue();
      }
   }

   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      if (this.binding && BindCapture.owner() != this) {
         this.binding = false;
      }

      if (this.modeOptions != null) {
         this.modeOptions.clearBounds();
      }

      this.visibleAnimation.method2808(this.setting.isVisible() ? 1.0F : 0.0F);
      float var8;
      if (!((var8 = this.visibleAnimation.getValue()) <= 0.001F)) {
         ZenithStyle var9 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var9 != null) {
            long var10 = System.currentTimeMillis();
            this.frameDt = this.lastFrameAt == 0L ? 0.0F : Math.min(0.05F, (float)(var10 - this.lastFrameAt) / 1000.0F);
            this.lastFrameAt = var10;
            float var12 = var7 * var8;
            float var13 = this.ownHeight() * var8;
            this.bounds = new BoundsData(var4, var5, var6, var13);
            boolean var14 = this.bounds.contains(var2, var3);
            float var15;
            if ((var15 = this.hoverAnimation.method2808(var14 ? 1.0F : 0.0F)) > 0.01F) {
               var1.drawRect(var4, var5, var6, var13, var9.getFieldSurfaceBackground().getZenithColor().method7294(2.0F).method7294(var12 * var15));
            }

            if (var14) {
               HoverInfo.request(this.label(), this.setting.getDescription());
            }

            this.renderFlash(var1, var4, var5, var6, var13, var10, var12, var9);
            float var39;
            float var16 = (var39 = var4 + (1.0F - var8) * 8.0F) + 8.0F + this.depth * 8.0F;
            Font var17 = Fonts.NEW_MEDIUM.getFont(5.2F);
            float var18 = var5 + (var13 - var17.height()) / 2.0F;
            ZenithColor var19 = var9.getTextSecondary()
               .getZenithColor()
               .method7295(var9.getTextEnable().getZenithColor(), var14 ? 0.6F : 0.0F)
               .method7294(var12);
            if (Zenith.getInstance().getNLMenuScreen().isRenderIcon()) {
               this.subjectGlyph.draw(var1, var16, var5 + (var13 - 6.0F) / 2.0F, 6.0F, var19);
            }

            float var20 = var39 + this.labelOffset();
            String var21 = this.label();
            float var22 = this.labelLimit(var39, var6, var20);
            if (this.labelScroll == null) {
               this.labelScroll = new HoverScrollText();
            }

            float var23 = this.labelScroll.render(var1, var17, var21, var20, var18, var22, var19, var14);
            float var24 = var20 + var23;
            this.valueLeftPx = Float.NaN;
            this.renderValue(var1, var2, var3, var39, var5, var6, var13, var12, var9, this.expandable() ? var24 + 7.0F : var24);
            if (this.expandable() && !Float.isNaN(this.valueLeftPx)) {
               MenuGlyph.drawChevron(var1, this.valueLeftPx - GuiStyle.PADDING.intValue() - 5.0F, var5 + var13 / 2.0F, this.expandAnimation.getValue(), var19);
            }

            float var25;
            if (!((var25 = this.expandAnimation.method2808(this.expanded ? 1.0F : 0.0F)) <= 0.001F)) {
               if (this.modeOptions != null) {
                  float var41 = Math.max(0.0F, var6 - 16.0F - this.depth * 8.0F);
                  float var44 = var5 + var13;
                  float var47 = this.modeOptions.height(var41) * var25 * var8;
                  var1.enableScissor(var39, var44, var39 + var6, var44 + var47);

                  try {
                     this.modeOptions
                        .render(
                           var1,
                           this.modeHoverAllowed ? var2 : Float.NaN,
                           this.modeHoverAllowed ? var3 : Float.NaN,
                           var39 + 8.0F + this.depth * 8.0F,
                           var44,
                           var41,
                           var47,
                           var12 * var25,
                           this.expanded && var8 > 0.99F && var7 > 0.99F,
                           var9
                        );
                  } finally {
                     var1.disableScissor();
                  }
               } else if (this.picker != null) {
                  float var40 = this.pickerWidth(var6);
                  this.pickerHeight = this.picker.height(var40);
                  float var43 = this.pickerHeight * var25 * var8;
                  float var46 = var5 + var13;
                  var1.enableScissor(var39, var46, var39 + var6, var46 + var43);
                  this.picker.render(var1, var2, var3, var39 + 8.0F + this.depth * 8.0F, var46, var40, var12);
                  var1.disableScissor();
               } else if (this.children != null && this.children.getClass() == EmptyList.class ? false : !this.children.isEmpty()) {
                  float var26 = 0.0F;

                  for (InspectorRow var28 : this.children) {
                     if (var28.isVisible()) {
                        var26 += var28.height();
                     }
                  }

                  float var42 = var26 * var25;
                  float var45 = var5 + var13;
                  var1.enableScissor(var39, var45, var39 + var6, var45 + var42);
                  boolean var29 = var2 >= var39 && var2 <= var39 + var6 && var3 >= var45 && var3 <= var45 + var42;
                  float var30 = var45;
                  int var31 = 0;

                  for (InspectorRow var33 : this.children) {
                     if (var33.isVisible()) {
                        float var34 = MathHelper.clamp(var25 * 1.5F - var31 * 0.07F, 0.0F, 1.0F);
                        float var35 = 1.0F - (1.0F - var34) * (1.0F - var34);
                        var33.modeHoverAllowed = this.modeHoverAllowed && var29;
                        var33.render(var1, var2, var3, var39 + (1.0F - var35) * 7.0F, var30, var6, var12 * var35);
                        var30 += var33.height();
                        var31++;
                     }
                  }

                  var1.disableScissor();
               }
            }
         }
      }
   }

   private void renderModeValue(
      MenuDrawContext var1, ModeSetting var2, float var3, float var4, float var5, float var6, Font var7, float var8, ZenithStyle var9, float var10
   ) {
      String var11 = this.expanded ? String.valueOf(var2.getValues().size()) : (var2.getValue2() == null ? "—" : var2.getValue2().getName());
      String var12 = fit(var7, var11, Math.max(0.0F, var10));
      float var13 = this.valueRoll
         .draw(
            var1,
            var7,
            var12,
            var3 + this.shakeOffset(),
            var4,
            (this.expanded ? var9.getTextTertiary().getZenithColor() : var9.getTextEnable().getZenithColor()).method7294(var8),
            1
         );
      this.valueLeftPx = var3 - var13;
      this.valueBounds = new BoundsData(this.valueLeftPx, var5, var13 + 8.0F, var6);
   }

   private void renderValue(
      MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9, float var10
   ) {
      Font var11 = Fonts.NEW_MEDIUM.getFont(5.01F);
      float var12;
      float var13 = (var12 = var4 + var6 - 8.0F) - var10 - GuiStyle.PADDING.intValue();
      float var14 = var5 + (var7 - var11.height()) / 2.0F;
      if (this.setting instanceof SliderSetting var31) {
         float var39 = var31.getValue3();
         if (!Float.isNaN(this.lastNumber) && var39 != this.lastNumber) {
            this.numberDirection = var39 > this.lastNumber ? 1 : -1;
         }

         this.lastNumber = var39;
         this.renderTrack(var1, var2, var3, var5, var7, var8, this.springNumber(var31), this.numberText(var31, var39), var11, var14, var12, var10, var9);
         this.valueBounds = this.trackBounds(var5, var7);
      } else if (this.setting instanceof ColorSetting var30) {
         boolean var38 = this.expanded;
         ZenithColor var43 = var30.getZenithColor();
         this.shownColor = this.shownColor == null ? var43 : this.shownColor.method7295(var43, 1.0F - (float)Math.exp(-this.frameDt * 18.0F));
         int var46 = var43.getIntValue() << 16 | var43.getIntValue2() << 8 | var43.getIntValue3();
         if (this.hexText == null || var46 != this.hexKey) {
            this.hexKey = var46;
            this.hexText = String.format("%02X%02X%02X", var43.getIntValue(), var43.getIntValue2(), var43.getIntValue3());
         }

         String var48 = this.hexText;
         float var20 = this.hexRoll.draw(var1, var11, var48, var12, var14, var9.getTextEnable().getZenithColor().method7294(var8), 1);
         float var21 = this.expandAnimation.getValue();
         float var22 = 7.0F + var21;
         float var23 = var12 - var20 - GuiStyle.PADDING.intValue() - var22;
         float var24 = var5 + (var7 - var22) / 2.0F;
         float var25;
         if ((var25 = this.pickerToggledAt < 0L ? 1.0F : MathHelper.clamp((float)(System.currentTimeMillis() - this.pickerToggledAt) / 420.0F, 0.0F, 1.0F))
               < 1.0F
            && var38) {
            float var26 = var25 * 4.0F;
            var1.drawRoundedBorder(
               var23 - var26,
               var24 - var26,
               var22 + var26 * 2.0F,
               var22 + var26 * 2.0F,
               0.5F,
               ProxyDotRadiusData.method8069(var26 * 0.6F),
               this.shownColor.method7292(1.0F).method7294((1.0F - var25) * (1.0F - var25) * var8)
            );
         }

         var1.drawRect(var23 + 0.5F, var24 + 0.5F, var22 - 1.0F, var22 - 1.0F, this.shownColor.method7294(var8));
         var1.drawRoundedBorder(
            var23,
            var24,
            var22,
            var22,
            0.1F,
            ProxyDotRadiusData.INSTANCE,
            var9.getFieldBorder().getZenithColor().method7294(var8 * 3.0F).method7295(var9.getPrimaryColor().getZenithColor().method7294(var8), var21)
         );
         this.valueBounds = new BoundsData(var23, var5, var12 - var23, var7);
         this.valueLeftPx = var23;
      } else if (this.setting instanceof BooleanSetting var29) {
         String var37 = this.valueText();
         float var42 = this.valueRoll
            .draw(
               var1,
               var11,
               var37,
               var12,
               var14,
               var29.isEnabled() ? var9.getTextEnable().getZenithColor().method7294(var8) : var9.getTextTertiary().getZenithColor().method7294(var8),
               var29.isEnabled() ? 1 : -1
            );
         float var45 = var12 - var42 - GuiStyle.PADDING.intValue() - 9.0F;
         this.renderSwitch(var1, var45, var5 + (var7 - 5.0F) / 2.0F, this.toggleAnimation.method2808(var29.isEnabled() ? 1.0F : 0.0F), var8, var9);
         this.valueBounds = new BoundsData(var45, var5, var12 - var45, var7);
      } else if (this.bindSetting == null) {
         if (this.setting instanceof ModeSetting var28) {
            this.renderModeValue(var1, var28, var12, var14, var5, var7, var11, var8, var9, var13);
         } else if (this.editing && this.setting instanceof TextSetting) {
            this.renderEditing(var1, var11, var12, var14, var5, var7, var8, var9, var13);
         } else {
            ZenithColor var27 = this.valueStrong() ? var9.getTextEnable().getZenithColor() : var9.getTextTertiary().getZenithColor();
            float var36;
            if ((var36 = this.flashProgress()) < 1.0F) {
               var27 = var27.method7295(var9.getPrimaryColor().getZenithColor(), (1.0F - var36) * (1.0F - var36));
            }

            float var41 = var12 + this.shakeOffset();
            if (this.setting instanceof ButtonSetting) {
               var41 += 1.5F * this.hoverAnimation.getValue();
            }

            String var44 = fit(var11, this.valueText(), var13);
            float var47 = this.valueRoll.draw(var1, var11, var44, var41, var14, var27.method7294(var8), this.counterDirection(var44));
            this.valueBounds = new BoundsData(var12 - Math.max(var47, 30.0F), var5, Math.max(var47, 30.0F) + 8.0F, var7);
            this.valueLeftPx = var12 - var47;
         }
      } else {
         boolean var15 = this.bounds != null && this.bounds.contains(var2, var3);
         ScreenRect var34 = var1.scissorStack.peekLast();
         if (var34 != null) {
            Matrix3x2fStack var17 = var1.getMatrices();
            float var18 = var17.m00() * var2 + var17.m10() * var3 + var17.m20();
            float var19 = var17.m01() * var2 + var17.m11() * var3 + var17.m21();
            var15 &= var18 >= var34.getLeft() && var18 < var34.getRight() && var19 >= var34.getTop() && var19 < var34.getBottom();
         }

         String var40 = BindLabel.text(this.binding, this.bindSetting.getKeyCode(), this.bindSetting.getModifiers());
         if (var40 == null && this.setting instanceof ModuleHeaderSetting) {
            var40 = "+";
         }

         this.valueBounds = this.bindLabel
            .render(
               var1,
               var15 ? var2 : Float.NaN,
               var15 ? var3 : Float.NaN,
               var5,
               var7,
               var12,
               var40,
               this.binding,
               var11,
               var10 + GuiStyle.PADDING.intValue(),
               var15,
               var8,
               null,
               var9
            );
         if (var15 && this.setting instanceof ModuleHeaderSetting && this.valueBounds != null && this.valueBounds.contains(var2, var3)) {
            HoverInfo.request(this.bindSetting.getName(), this.bindSetting.getDescription());
         }
      }
   }

   private int counterDirection(String var1) {
      byte var2 = 1;
      if (this.lastCounter != null && !this.lastCounter.equals(var1)) {
         int var3 = leadingNumber(this.lastCounter);
         int var4 = leadingNumber(var1);
         if (var3 >= 0 && var4 >= 0 && var4 < var3) {
            var2 = -1;
         }
      }

      this.lastCounter = var1;
      return var2;
   }

   private static int leadingNumber(String var0) {
      int var1 = 0;

      while (var1 < var0.length() && Character.isDigit(var0.charAt(var1))) {
         var1++;
      }

      return var1 != 0 && var1 <= 9 ? Integer.parseInt(var0.substring(0, var1)) : -1;
   }

   private void renderEditing(MenuDrawContext var1, Font var2, float var3, float var4, float var5, float var6, float var7, ZenithStyle var8, float var9) {
      long var10 = System.currentTimeMillis();
      ZenithColor var12 = var8.getPrimaryColor().getZenithColor();
      ZenithColor var13 = var8.getTextEnable().getZenithColor().method7294(var7);
      float var14 = 1.5F;
      String var15 = this.editBuffer.toString();
      String var16 = var15;

      while (!var16.isEmpty() && var2.width(var16) > var9 - var14 - 2.0F) {
         var16 = var16.substring(1);
      }

      float var17 = var3 - var14 - 1.0F;
      float var18 = var2.width(var16);
      float var19 = var17 - var18;
      float var20;
      if ((var20 = this.typedAt < 0L ? 1.0F : MathHelper.clamp((float)(var10 - this.typedAt) / 160.0F, 0.0F, 1.0F)) < 1.0F && !var16.isEmpty()) {
         String var21 = var16.substring(0, var16.length() - 1);
         String var22 = var16.substring(var16.length() - 1);
         var1.drawText(var2, var21, var19, var4, var13);
         float var23 = 1.0F - (1.0F - var20) * (1.0F - var20);
         var1.drawText(
            var2, var22, var19 + var2.width(var21), var4 + (1.0F - var23) * 2.5F, var13.method7295(var12.method7294(var7), 1.0F - var23).method7294(var23)
         );
      } else {
         var1.drawText(var2, var16, var19, var4, var13);
      }

      float var24 = 0.35F + 0.65F * (0.5F + 0.5F * MathHelper.cos((float)var10 / 170.0F));
      var1.drawRect(var17 + var14 - 0.5F, var4 - 0.5F, 0.6F, var2.height() + 1.0F, var12.method7294(var24 * var7));
      float var25 = this.editStartAt < 0L ? 1.0F : MathHelper.clamp((float)(var10 - this.editStartAt) / 240.0F, 0.0F, 1.0F);
      float var26 = Math.max(var18 + var14 + 2.0F, 14.0F) * (1.0F - (1.0F - var25) * (1.0F - var25));
      var1.drawRect(var3 - var26, var4 + var2.height() + 1.0F, var26, 0.5F, var12.method7294(0.7F * var7));
      this.valueBounds = new BoundsData(var3 - Math.max(var18, 30.0F), var5, Math.max(var18, 30.0F) + 8.0F, var6);
   }

   private void renderKeyCap(MenuDrawContext var1, float var2, float var3, Font var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      long var10 = System.currentTimeMillis();
      ZenithColor var12 = var9.getPrimaryColor().getZenithColor();
      ZenithColor var13 = var9.getTextEnable().getZenithColor();
      String var14 = this.binding ? null : this.valueText();
      boolean var15 = var14 != null && !"-".equals(var14);
      float var16 = this.binding ? 17.0F : Math.max(var4.width(var14) + 6.0F, 10.0F);
      if (!this.keyCapKnown) {
         this.keyCapKnown = true;
         this.keyCapWidth.method2809(var16);
      }

      float var17 = this.keyCapWidth.method2808(var16);
      float var18 = 8.0F;
      float var19 = 1.1F;
      float var20 = var5 - var17 + this.shakeOffset();
      float var21 = var6 + (var7 - var18 - var19) / 2.0F;
      BoundsData var22 = new BoundsData(var20, var6, var17, var7);
      boolean var23 = var22.contains(var2, var3);
      float var24 = this.keyHover.method2808(var23 && !this.binding ? 1.0F : 0.0F);
      long var25;
      if ((var25 = this.keyAnim == 0 ? Long.MAX_VALUE : var10 - this.keyAnimAt) >= 720L) {
         this.keyAnim = 0;
         this.keyFrom = null;
      }

      float var27 = 0.0F;
      if (this.keyAnim == 1 && var25 < 320L) {
         float var28;
         var27 = (var28 = (float)var25 / 320.0F) < 0.3F ? var28 / 0.3F : 1.0F - (var28 - 0.3F) / 0.7F;
      }

      float var40 = var21 - var24 * 0.6F + var27 * var19;
      if (this.binding) {
         float var29;
         float var30 = (var29 = (float)(var10 % 950L) / 950.0F) * 4.0F;
         var1.drawRoundedBorder(
            var20 - var30,
            var40 - var30,
            var17 + var30 * 2.0F,
            var18 + var30 * 2.0F,
            0.5F,
            ProxyDotRadiusData.method8069(1.5F + var30),
            var12.method7294((1.0F - var29) * (1.0F - var29) * 0.6F * var8)
         );
      }

      if (this.keyAnim == 1 && var25 < 480L) {
         float var41 = (float)var25 / 480.0F;
         float var43 = (1.0F - (1.0F - var41) * (1.0F - var41)) * 5.0F;
         var1.drawRoundedBorder(
            var20 - var43,
            var40 - var43,
            var17 + var43 * 2.0F,
            var18 + var43 * 2.0F,
            0.6F,
            ProxyDotRadiusData.method8069(1.5F + var43),
            var12.method7295(ZenithColor.INSTANCE, 0.3F).method7294((1.0F - var41) * (1.0F - var41) * var8)
         );
      }

      var1.drawRoundedRect(
         var20, var21 + var19, var17, var18, ProxyDotRadiusData.method8069(1.5F), ZenithColor.INSTANCE2.method7294((0.35F + 0.15F * var24) * var8)
      );
      ZenithColor var42 = var9.getFieldSurfaceBackground().getZenithColor().method7294(2.4F).method7295(var13, 0.06F * var24);
      ZenithColor var44 = var9.getFieldBorder().getZenithColor().method7294(4.0F).method7295(var13.method7294(0.5F), 0.4F * var24);
      if (this.binding) {
         float var31 = 0.5F + 0.5F * MathHelper.sin((float)var10 / 170.0F);
         var42 = var42.method7295(var12.method7294(0.35F), 0.5F + 0.25F * var31);
         var44 = var12.method7294(0.5F + 0.5F * var31);
      }

      if (this.keyAnim == 1 && var25 < 520L) {
         float var45 = 1.0F - (float)var25 / 520.0F;
         var42 = var42.method7295(var12.method7295(ZenithColor.INSTANCE, 0.35F), 0.6F * var45 * var45);
         var44 = var44.method7295(var12, var45);
      }

      var1.drawRoundedRect(var20, var40, var17, var18, ProxyDotRadiusData.method8069(1.5F), var42.method7294(var8));
      var1.drawRoundedBorder(var20, var40, var17, var18, 0.1F, ProxyDotRadiusData.method8069(1.5F), var44.method7294(var8));
      float var46 = var20 + var17 / 2.0F;
      float var32 = var40 + (var18 - var4.height()) / 2.0F;
      if (this.binding) {
         for (int var33 = 0; var33 < 3; var33++) {
            float var34 = Math.max(0.0F, MathHelper.sin((float)var10 / 130.0F - var33 * 0.9F)) * 1.2F;
            var1.drawRoundedRect(
               var46 + (var33 - 1) * 2.8F - 0.6F,
               var40 + var18 / 2.0F - 0.6F - var34,
               1.2F,
               1.2F,
               ProxyDotRadiusData.method8069(0.6F),
               var12.method7295(ZenithColor.INSTANCE, 0.3F).method7294(var8)
            );
         }
      } else {
         ZenithColor var47 = (var15 ? var13 : var9.getTextTertiary().getZenithColor()).method7295(var13, 0.3F * var24);
         if (this.keyAnim == 2 && this.keyFrom != null) {
            drawKeyLetters(var1, var4, this.keyFrom, var46, var32, var25, var47.method7294(var8), var12, 2);
         }

         long var49 = this.keyAnim == 2 ? var25 - 150L : (this.keyAnim == 1 ? var25 - 60L : Long.MAX_VALUE);
         drawKeyLetters(var1, var4, var14, var46, var32, var49, var47.method7294(var8), var12, this.keyAnim == 1 ? 1 : 0);
      }

      if (this.keyAnim == 1 && var25 < 460L) {
         float var48 = (float)var25 / 460.0F;
         float var50 = 1.0F - (1.0F - var48) * (1.0F - var48) * (1.0F - var48);
         float var35 = var40 + var18 / 2.0F;

         for (int var36 = 0; var36 < 6; var36++) {
            float var37 = (var36 * 60.0F + 30.0F) * (float) (Math.PI / 180.0);
            float var38 = var50 * (var17 / 2.0F + 4.0F + var36 % 2 * 2.0F);
            float var39;
            if (!((var39 = 1.2F * (1.0F - var48)) < 0.05F)) {
               var1.drawRoundedRect(
                  var46 + MathHelper.cos(var37) * var38 - var39 / 2.0F,
                  var35 + MathHelper.sin(var37) * var38 * 0.7F - var39 / 2.0F,
                  var39,
                  var39,
                  ProxyDotRadiusData.method8069(var39 / 2.0F),
                  (var36 % 2 == 0 ? var12 : var12.method7295(ZenithColor.INSTANCE, 0.5F)).method7294((1.0F - var48) * var8)
               );
            }
         }
      }

      this.valueBounds = new BoundsData(var5 - var17, var6, var17, var7);
   }

   private static void drawKeyLetters(
      MenuDrawContext var0, Font var1, String var2, float var3, float var4, long var5, ZenithColor var7, ZenithColor var8, int var9
   ) {
      float var10 = var3 - var1.width(var2) / 2.0F;
      int var11 = var2.length();
      boolean var12 = var9 == 1;
      boolean var13 = var9 == 2;

      for (int var14 = 0; var14 < var11; var14++) {
         String var15 = String.valueOf(var2.charAt(var14));
         float var16 = var1.width(var15);
         float var17 = 0.0F;
         float var18 = 0.0F;
         float var19 = 1.0F;
         ZenithColor var20 = var7;
         if (var5 != Long.MAX_VALUE) {
            if (var12) {
               float var21 = MathHelper.clamp((float)(var5 - var14 * 35L) / 420.0F, 0.0F, 1.0F);
               var18 = -4.0F * (float)(Math.exp(-7.0F * var21) * Math.cos(11.0F * var21));
               var19 = Math.min(1.0F, var21 * 4.0F);
               var20 = var7.method7295(var8.method7295(ZenithColor.INSTANCE, 0.5F).method7293(var7.getIntValue4()), (1.0F - var21) * (1.0F - var21));
            } else if (var13) {
               float var25 = MathHelper.clamp((float)(var5 - (var11 - 1 - var14) * 28L) / 340.0F, 0.0F, 1.0F);
               var18 = 4.5F * var25 * var25;
               int var24;
               int var27;
               var17 = (
                     (
                              (
                                    (var27 = ((var24 = (var14 * 31 + var11) * (10196 << 16 | (char)-5331)) ^ var24 >>> 15) * (-31253 << 16 | (char)-13717))
                                       ^ var27 >>> 13
                                 )
                                 >>> 8
                           )
                           / 1.6777216E7F
                        - 0.5F
                  )
                  * 3.5F
                  * var25;
               var19 = 1.0F - var25;
            } else {
               float var26 = MathHelper.clamp((float)(var5 - var14 * 18L) / 200.0F, 0.0F, 1.0F);
               float var22;
               var18 = (var22 = 1.0F - var26) * var22 * var22 * 3.0F;
               var19 = var26;
            }
         }

         if (var19 > 0.01F) {
            var0.drawText(var1, var15, var10 + var17, var4 + var18, var20.method7294(var19));
         }

         var10 += var16;
      }
   }

   private static float hash01(int var0) {
      int var1;
      int var2;
      return (((var2 = ((var1 = var0 * (10196 << 16 | (char)-5331)) ^ var1 >>> 15) * (-31253 << 16 | (char)-13717)) ^ var2 >>> 13) >>> 8) / 1.6777216E7F;
   }

   private void renderTrack(
      MenuDrawContext var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      String var8,
      Font var9,
      float var10,
      float var11,
      float var12,
      ZenithStyle var13
   ) {
      ZenithColor var14 = var13.getPrimaryColor().getZenithColor();
      float var15 = this.dragAnimation.method2808(this.dragging ? 1.0F : 0.0F);
      ZenithColor var16 = var13.getTextEnable().getZenithColor().method7295(var14, var15 * 0.6F);
      this.valueRoll.draw(var1, var9, var8, var11, var10, var16.method7294(var6), this.numberDirection);
      float var17 = var11 - 30.0F;
      this.trackSpanPx = Float.isNaN(this.sharedTrackSpan) ? MathHelper.clamp(var17 - var12 - GuiStyle.PADDING.intValue(), 18.0F, 34.0F) : this.sharedTrackSpan;
      this.trackLeftPx = var17 - this.trackSpanPx;
      boolean var18 = this.bounds != null && this.trackBounds(var4, var5).method8092(var2, var3, 4.0F);
      float var19 = Math.max(var15, var18 ? 0.4F : 0.0F);
      float var20 = 1.0F + 0.5F * var15;
      float var21 = var4 + var5 / 2.0F - var20 / 2.0F;
      var1.drawRect(this.trackLeftPx, var21, this.trackSpanPx, var20, var13.getFieldBorder().getZenithColor().method7294(var6 * 3.0F));
      float var22;
      if ((var22 = this.edgeHitAt < 0L ? 1.0F : MathHelper.clamp((float)(System.currentTimeMillis() - this.edgeHitAt) / 380.0F, 0.0F, 1.0F)) >= 1.0F) {
         this.edgeHitAt = -1L;
      }

      float var23 = var22 >= 1.0F ? 0.0F : MathHelper.sin(var22 * (float) Math.PI * 3.0F) * (1.0F - var22) * 2.2F * this.edgeSide;
      float var24 = MathHelper.clamp(var7, 0.0F, 1.0F);
      float var25 = this.trackLeftPx + this.trackSpanPx * var24 + var23;
      var1.drawRect(this.trackLeftPx, var21, Math.max(0.0F, var25 - this.trackLeftPx), var20, var14.method7294(var6));
      if (var22 < 1.0F) {
         float var26 = this.edgeSide < 0 ? this.trackLeftPx : this.trackLeftPx + this.trackSpanPx;
         float var27 = 3.5F * (1.0F - var22) + 1.0F;
         var1.drawRoundedRect(
            var26 - var27,
            var4 + var5 / 2.0F - var27,
            var27 * 2.0F,
            var27 * 2.0F,
            ProxyDotRadiusData.method8069(var27),
            var14.method7294(0.35F * (1.0F - var22) * var6)
         );
      }

      float var29 = 5.0F + 2.0F * var19;
      float var30 = 1.5F + 0.5F * var19;
      if (var15 > 0.01F) {
         float var28 = 3.0F + 1.5F * var15;
         var1.drawRoundedRect(
            var25 - var28 + var30 / 2.0F,
            var4 + var5 / 2.0F - var28,
            var28 * 2.0F,
            var28 * 2.0F,
            ProxyDotRadiusData.method8069(var28),
            var14.method7294(0.22F * var15 * var6)
         );
      }

      var1.drawRect(
         var25 - var30 / 2.0F + 0.25F, var4 + var5 / 2.0F - var29 / 2.0F, var30, var29, var14.method7295(ZenithColor.INSTANCE, 0.25F * var15).method7294(var6)
      );
   }

   private float springNumber(SliderSetting var1) {
      float var2;
      float var3 = (var2 = var1.getMax() - var1.getMin()) <= 0.0F ? 0.0F : (var1.getValue3() - var1.getMin()) / var2;
      if (Float.isNaN(this.springPos)) {
         this.springPos = var3;
         this.springVel = 0.0F;
         return var3;
      } else {
         float var4 = this.frameDt;
         float var5 = 0.004166667F;

         while (var4 > 0.0F) {
            float var6 = Math.min(var5, var4);
            this.springVel = this.springVel + (380.0F * (var3 - this.springPos) - 30.0F * this.springVel) * var6;
            this.springPos = this.springPos + this.springVel * var6;
            var4 -= var6;
         }

         if (Math.abs(var3 - this.springPos) < 5.0E-4F && Math.abs(this.springVel) < 0.002F) {
            this.springPos = var3;
            this.springVel = 0.0F;
         }

         return this.springPos;
      }
   }

   private float labelLimit(float var1, float var2, float var3) {
      float var4 = var1 + var2 - 8.0F - var3;
      Font var6 = Fonts.NEW_MEDIUM.getFont(5.01F);
      float var5;
      if (this.setting instanceof SliderSetting) {
         float var8 = Float.isNaN(this.sharedTrackSpan) ? 18.0F : this.sharedTrackSpan;
         var5 = 30.0F + var8;
      } else if (this.setting instanceof BooleanSetting) {
         Localization var9 = Zenith.getInstance().getLocalization();
         var5 = Math.max(var6.width(var9.translate("gui.inspector.on")), var6.width(var9.translate("gui.inspector.off"))) + GuiStyle.PADDING.intValue() + 9.0F;
      } else if (this.setting instanceof ColorSetting) {
         var5 = var6.width("FFFFFF") + GuiStyle.PADDING.intValue() + 7.0F + 1.0F;
      } else if (this.bindSetting != null) {
         String var10 = BindLabel.text(this.binding, this.bindSetting.getKeyCode(), this.bindSetting.getModifiers());
         var5 = Math.max(30.0F, var6.width(var10 == null ? "+" : var10));
      } else if (this.setting instanceof ModeSetting var7) {
         String var12 = this.expanded ? String.valueOf(var7.getValues().size()) : (var7.getValue2() == null ? "—" : var7.getValue2().getName());
         var5 = Math.min(Math.max(0.0F, var4 * 0.45F), Math.max(10.0F, var6.width(var12)));
      } else {
         var5 = Math.max(30.0F, Math.min(var6.width(this.valueText()), var4 * 0.45F));
      }

      return Math.max(0.0F, var4 - var5 - GuiStyle.PADDING.intValue() - (this.expandable() ? 7.0F : 0.0F));
   }

   private void renderSwitch(MenuDrawContext var1, float var2, float var3, float var4, float var5, ZenithStyle var6) {
      var1.drawRect(var2, var3, 9.0F, 5.0F, var6.getFieldSurfaceBackground().getZenithColor().method7294(var5 * 3.0F));
      var1.drawRoundedBorder(var2, var3, 9.0F, 5.0F, 0.1F, ProxyDotRadiusData.INSTANCE, var6.getFieldBorder().getZenithColor().method7294(var5 * 4.0F));
      float var7 = var4 < 0.5F ? 2.0F * var4 * var4 : 1.0F - (float)Math.pow(-2.0F * var4 + 2.0F, 2.0) / 2.0F;
      float var8 = 3.0F + 1.8F * MathHelper.sin(var4 * (float) Math.PI);
      float var9 = MathHelper.lerp(var7, var2 + 1.0F, var2 + 9.0F - var8 - 1.0F);
      ZenithColor var10 = var6.getTextTertiary().getZenithColor();
      ZenithColor var11 = var6.getPrimaryColor().getZenithColor();
      var1.drawRect(var9, var3 + 1.0F, var8, 3.0F, var10.method7295(var11, var4).method7294(var5));
      float var12;
      if ((var12 = this.flashProgress()) < 1.0F && var4 > 0.5F) {
         float var13 = var12 * 4.0F;
         var1.drawRoundedBorder(
            var2 - var13,
            var3 - var13,
            9.0F + var13 * 2.0F,
            5.0F + var13 * 2.0F,
            0.5F,
            ProxyDotRadiusData.method8069(var13 * 0.7F),
            var11.method7294((1.0F - var12) * (1.0F - var12) * 0.8F * var5)
         );
      }
   }

   private float flashProgress() {
      if (this.flashAt < 0L) {
         return 1.0F;
      } else {
         float var1;
         if ((var1 = MathHelper.clamp((float)(System.currentTimeMillis() - this.flashAt) / 520.0F, 0.0F, 1.0F)) >= 1.0F) {
            this.flashAt = -1L;
         }

         return var1;
      }
   }

   private float shakeOffset() {
      if (this.shakeAt < 0L) {
         return 0.0F;
      } else {
         float var1;
         if ((var1 = MathHelper.clamp((float)(System.currentTimeMillis() - this.shakeAt) / 360.0F, 0.0F, 1.0F)) >= 1.0F) {
            this.shakeAt = -1L;
            return 0.0F;
         } else {
            return MathHelper.sin(var1 * (float) Math.PI * 6.0F) * 2.0F * (1.0F - var1);
         }
      }
   }

   private void renderFlash(MenuDrawContext var1, float var2, float var3, float var4, float var5, long var6, float var8, ZenithStyle var9) {
      if (this.flashAt >= 0L) {
         float var10;
         if (!((var10 = MathHelper.clamp((float)(var6 - this.flashAt) / 520.0F, 0.0F, 1.0F)) >= 1.0F)) {
            ZenithColor var11 = var9.getPrimaryColor().getZenithColor();
            var1.drawRect(var2, var3, var4, var5, var11.method7294(0.07F * (1.0F - var10) * var8));
            float var12 = 1.0F - (1.0F - var10) * (1.0F - var10);
            float var13 = var2 - 20.0F + (var4 + 40.0F) * var12;
            float var14 = 18.0F;
            float var15;
            if (!((var15 = 0.16F * MathHelper.sin(var10 * (float) Math.PI) * var8) <= 0.002F)) {
               ZenithColor var16 = var11.method7294(0.0F);
               ZenithColor var17 = var11.method7295(ZenithColor.INSTANCE, 0.3F).method7294(var15);
               band(var1, var13 - var14, var13, var2, var3, var4, var5, var16, var17);
               band(var1, var13, var13 + var14, var2, var3, var4, var5, var17, var16);
            }
         }
      }
   }

   private static void band(MenuDrawContext var0, float var1, float var2, float var3, float var4, float var5, float var6, ZenithColor var7, ZenithColor var8) {
      float var9 = Math.max(var1, var3);
      float var10;
      if (!((var10 = Math.min(var2, var3 + var5)) - var9 <= 0.1F)) {
         float var11 = var2 - var1;
         ZenithColor var12 = var7.method7295(var8, (var9 - var1) / var11);
         ZenithColor var13 = var7.method7295(var8, (var10 - var1) / var11);
         var0.drawRoundedRect(var9, var4, var10 - var9, var6, ProxyDotRadiusData.INSTANCE, GlowColor.method8096(var12, var12, var13, var13));
      }
   }

   private BoundsData trackBounds(float var1, float var2) {
      return new BoundsData(this.trackLeftPx, var1, this.trackSpanPx, var2);
   }

   private String label() {
      return this.setting.getName();
   }

   private boolean valueStrong() {
      if (this.setting instanceof BooleanSetting var3) {
         return var3.isEnabled();
      } else {
         return this.setting instanceof BindSetting var1 ? var1.getKeyCode() != -1 : true;
      }
   }

   private String valueText() {
      if (this.setting instanceof BooleanSetting var11) {
         return Zenith.getInstance().getLocalization().translate(var11.isEnabled() ? "gui.inspector.on" : "gui.inspector.off");
      } else if (this.setting instanceof ModeSetting var10) {
         return var10.getValue2() == null ? "-" : var10.getValue2().getName().toLowerCase(Locale.ROOT);
      } else if (this.setting instanceof BindSetting var9) {
         if (this.binding) {
            return "...";
         } else {
            int var20;
            if ((var20 = var9.getKeyCode()) != -1 && var20 != 0) {
               try {
                  String var21 = KeyName.method6702(var20, var9.getModifiers());
                  return var21 != null && !var21.isBlank() ? var21.toUpperCase() : "-";
               } catch (Exception var5) {
                  return "-";
               }
            } else {
               return "-";
            }
         }
      } else if (this.setting instanceof MultiSelectSetting var8) {
         int var19 = 0;

         for (MultiSelectSetting.Entry var4 : var8.getItems2()) {
            if (var4.isEnabled()) {
               var19++;
            }
         }

         return var19 + "/" + var8.getItems2().size();
      } else if (this.setting instanceof GroupSetting var7) {
         return String.valueOf(var7.getSettings().size());
      } else if (this.setting instanceof TextSetting var6) {
         if (this.editing) {
            return this.editBuffer + "_";
         } else {
            String var18 = var6.getValue();
            return var18 != null && !var18.isBlank() ? var18 : "-";
         }
      } else if (this.setting instanceof BlockListSetting var1) {
         return String.valueOf(var1.getItems().size());
      } else {
         return this.setting instanceof ButtonSetting ? "›" : "";
      }
   }

   private static String fit(Font var0, String var1, float var2) {
      if (var1 != null && !var1.isEmpty() && !(var0.width(var1) <= var2)) {
         String var3 = "...";
         float var4 = var0.width(var3);
         int var5 = var1.length();

         while (var5 > 0 && var0.width(var1.substring(0, var5)) + var4 > var2) {
            var5--;
         }

         return var5 <= 0 ? "" : var1.substring(0, var5).stripTrailing() + var3;
      } else {
         return var1 == null ? "" : var1;
      }
   }

   private String numberText(SliderSetting var1, float var2) {
      SliderSetting.Handler var3 = var1.getHandler();
      if (this.numberText == null || Float.floatToIntBits(var2) != Float.floatToIntBits(this.numberTextValue) || var3 != this.numberTextDisplay) {
         this.numberText = formatNumber(var1, var2) + var1.getSuffix();
         this.numberTextValue = var2;
         this.numberTextDisplay = var3;
      }

      return this.numberText;
   }

   private static String formatNumber(SliderSetting var0, float var1) {
      SliderSetting.Handler var2 = var0.getHandler();
      if (var2 != null) {
         return var2.text(var1);
      } else {
         String var3 = String.valueOf(var0.getFloatValue2());
         int var4 = 0;
         int var5;
         if ((var5 = var3.indexOf(46)) != -1 && (var3.charAt(var5 + 1) != '0' || var3.length() > var5 + 2 && var3.charAt(var5 + 2) != '0')) {
            var4 = var3.length() - var5 - 1;
         }

         return String.format(Locale.US, "%." + var4 + "f", var1);
      }
   }

   @Override
   public boolean bindMouse(Enum3 var1) {
      if (this.binding && BindCapture.owner() != this) {
         this.binding = false;
      }

      if (this.binding && this.bindSetting != null && this.setting.isVisible() && MouseBindTarget.bindable(var1)) {
         this.bindSetting.setBind(var1.getIntValue(), 0);
         this.binding = false;
         BindCapture.end(this);
         this.bindLabel.set();
         Zenith.getInstance().getClientSounds().method1566();
         return true;
      } else {
         return false;
      }
   }

   private void startKeyAnim(int var1, String var2) {
      this.keyAnim = var1;
      this.keyAnimAt = System.currentTimeMillis();
      this.keyFrom = var2 != null && !"-".equals(var2) ? var2 : null;
   }

   public void cancelBinding() {
      this.binding = false;
      BindCapture.end(this);

      for (InspectorRow var2 : this.children) {
         var2.cancelBinding();
      }
   }

   public void cancelModuleBindingOutside(double var1, double var3) {
      if (this.setting instanceof ModuleHeaderSetting && (this.valueBounds == null || !this.valueBounds.contains(var1, var3))) {
         this.cancelBinding();
      }
   }

   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (!this.setting.isVisible()) {
         this.cancelBinding();
         return false;
      } else {
         if (!MouseBindTarget.bindable(var5)) {
            this.cancelModuleBindingOutside(var1, var3);
         }

         if (this.modeOptions != null) {
            if (!this.setting.isVisible() || this.visibleAnimation.getValue() < 0.99F) {
               return false;
            }

            if (this.expanded && this.modeOptions.onMouseClicked(var1, var3, var5)) {
               return true;
            }
         }

         if (this.expanded && this.picker != null && this.picker.onMouseClicked(var1, var3, var5)) {
            return true;
         } else {
            if (this.expanded
               && this.bounds != null
               && var1 >= this.bounds.getFloatValue()
               && var1 <= this.bounds.getFloatValue() + this.bounds.width()
               && var3 >= this.bounds.getFloatValue2() + this.bounds.height()
               && var3 <= this.bounds.getFloatValue2() + this.height()) {
               for (InspectorRow var7 : this.children) {
                  if (var7.isVisible() && var7.onMouseClicked(var1, var3, var5)) {
                     return true;
                  }
               }
            }

            if (this.bounds == null || !this.bounds.contains(var1, var3)) {
               return false;
            } else if (this.picker != null) {
               this.expanded = !this.expanded;
               if (!this.expanded) {
                  this.picker.remember();
               } else {
                  this.pickerToggledAt = System.currentTimeMillis();
               }

               Zenith.getInstance().getClientSounds().method1570(false);
               return true;
            } else if (this.setting instanceof SliderSetting var11) {
               if (this.valueBounds != null && this.valueBounds.method8092(var1, var3, 4.0F)) {
                  this.dragging = true;
                  this.dragOffset = this.grabOffset(var11, var1);
                  Zenith.getInstance().getNLMenuScreen().lockGuiScale();
                  this.applyDrag(var1);
                  return true;
               } else {
                  return true;
               }
            } else if (this.setting instanceof BooleanSetting var10) {
               var10.toggle();
               this.flashAt = var10.isEnabled() ? System.currentTimeMillis() : -1L;
               playToggleSound(var10.isEnabled());
               return true;
            } else if (this.modeOptions != null) {
               if (var5 != Enum3.LEFT && var5 != Enum3.RIGHT) {
                  return false;
               } else {
                  this.expanded = !this.expanded;
                  this.modeOptions.clearBounds();
                  Zenith.getInstance().getClientSounds().method1570(false);
                  return true;
               }
            } else if (this.bindSetting == null
               || this.setting instanceof ModuleHeaderSetting && (this.valueBounds == null || !this.valueBounds.contains(var1, var3))) {
               if (this.setting instanceof ButtonSetting var9) {
                  var9.toggle();
                  Zenith.getInstance().getClientSounds().method1562();
                  return true;
               } else if (this.setting instanceof TextSetting var8) {
                  this.editing = true;
                  this.editBuffer = new StringBuilder(var8.getValue() == null ? "" : var8.getValue());
                  this.editOriginal = this.editBuffer.toString();
                  this.editStartAt = System.currentTimeMillis();
                  this.typedAt = -1L;
                  Zenith.getInstance().getClientSounds().method1570(false);
                  return true;
               } else if (this.children != null && this.children.getClass() == EmptyList.class ? false : !this.children.isEmpty()) {
                  this.expanded = !this.expanded;
                  Zenith.getInstance().getClientSounds().method1570(false);
                  return true;
               } else {
                  return true;
               }
            } else if (this.bindMouse(var5)) {
               return true;
            } else {
               if (var5 == Enum3.RIGHT) {
                  this.cancelBinding();
                  this.bindSetting.setBind(-1, 0);
                  this.bindLabel.cleared();
                  Zenith.getInstance().getClientSounds().method1567();
               } else {
                  this.keyBeforeBinding = this.binding
                     ? this.keyBeforeBinding
                     : BindLabel.text(false, this.bindSetting.getKeyCode(), this.bindSetting.getModifiers());
                  this.binding = true;
                  this.bindLabel.waiting();
                  BindCapture.begin(this);
                  Zenith.getInstance().getClientSounds().method1570(false);
               }

               return true;
            }
         }
      }
   }

   public boolean isEditing() {
      return this.editing;
   }

   public void beginEdit() {
      if (!this.editing && this.setting instanceof TextSetting var1) {
         this.editing = true;
         this.editBuffer = new StringBuilder(var1.getValue() == null ? "" : var1.getValue());
         this.editOriginal = this.editBuffer.toString();
         this.editStartAt = System.currentTimeMillis();
         this.typedAt = -1L;
      }
   }

   public void endEdit() {
      this.editing = false;
   }

   public void onMouseReleased(double var1, double var3, Enum3 var5) {
      this.dragging = false;
      this.dragOffset = 0.0F;
      this.edgePinned = false;
      if (this.picker != null) {
         this.picker.onMouseReleased();
      }

      for (InspectorRow var7 : this.children) {
         var7.onMouseReleased(var1, var3, var5);
      }
   }

   public boolean onMouseDragged(double var1, double var3) {
      if (this.dragging) {
         this.applyDrag(var1);
         return true;
      } else {
         boolean var5 = false;
         if (this.expanded) {
            for (InspectorRow var7 : this.children) {
               if (var7.onMouseDragged(var1, var3)) {
                  var5 = true;
               }
            }
         }

         return var5;
      }
   }

   private float pointerAt(double var1) {
      return this.bounds != null && !Float.isNaN(this.trackLeftPx) && !(this.trackSpanPx <= 0.0F)
         ? MathHelper.clamp((float)(var1 - this.trackLeftPx) / this.trackSpanPx, 0.0F, 1.0F)
         : 0.0F;
   }

   private float grabOffset(SliderSetting var1, double var2) {
      float var4;
      float var5 = (var4 = var1.getMax() - var1.getMin()) <= 0.0F ? 0.0F : (var1.getValue3() - var1.getMin()) / var4;
      float var6;
      return Math.abs((var6 = this.pointerAt(var2)) - var5) * this.trackSpanPx <= 5.0F ? var5 - var6 : 0.0F;
   }

   private void applyDrag(double var1) {
      if (this.bounds != null) {
         float var3 = MathHelper.clamp(this.pointerAt(var1) + this.dragOffset, 0.0F, 1.0F);
         if (!Float.isNaN(this.trackLeftPx) && this.trackSpanPx > 0.0F) {
            float var4;
            int var5;
            if ((var5 = (var4 = (float)(var1 - this.trackLeftPx) / this.trackSpanPx + this.dragOffset) < -0.02F ? -1 : (var4 > 1.02F ? 1 : 0)) != 0
               && !this.edgePinned) {
               this.edgePinned = true;
               this.edgeSide = var5;
               this.edgeHitAt = System.currentTimeMillis();
            } else if (var5 == 0) {
               this.edgePinned = false;
            }
         }

         if (this.setting instanceof SliderSetting var8) {
            float var10 = var8.getMin() + (var8.getMax() - var8.getMin()) * var3;
            float var6;
            if ((var6 = var8.getFloatValue2()) > 0.0F) {
               var10 = Math.round(var10 / var6) * var6;
            }

            float var7 = var8.getValue3();
            var8.method2400(MathHelper.clamp(var10, var8.getMin(), var8.getMax()));
            if (var8.getValue3() != var7) {
               Zenith.getInstance().getClientSounds().method1571(var3);
            }
         }
      }
   }

   public boolean keyPressed(KeyInput var1) {
      return this.keyPressed(var1.key(), var1.scancode(), var1.modifiers());
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.binding && BindCapture.owner() != this) {
         this.binding = false;
      }

      if (this.binding && this.bindSetting != null) {
         if (var1 == 256) {
            this.bindSetting.setBind(-1, 0);
            this.bindLabel.cleared();
            Zenith.getInstance().getClientSounds().method1567();
         } else {
            this.bindSetting.setBind(var1, KeyName.method6700(var1, var3));
            this.bindLabel.set();
            Zenith.getInstance().getClientSounds().method1566();
         }

         this.binding = false;
         BindCapture.end(this);
         return true;
      } else if (this.editing && this.setting instanceof TextSetting var6) {
         if (var1 == 259) {
            if (!this.editBuffer.isEmpty()) {
               this.editBuffer.deleteCharAt(this.editBuffer.length() - 1);
               this.publishEdit(var6);
            }

            return true;
         } else if (var1 == 257 || var1 == 335) {
            var6.method2122(this.editBuffer.toString());
            this.editing = false;
            this.flashAt = System.currentTimeMillis();
            Zenith.getInstance().getClientSounds().method1568();
            return true;
         } else if (var1 == 256) {
            var6.setValue(this.editOriginal);
            this.editBuffer.setLength(0);
            this.editBuffer.append(this.editOriginal);
            this.editing = false;
            this.shakeAt = System.currentTimeMillis();
            Zenith.getInstance().getClientSounds().method1569();
            return true;
         } else {
            return true;
         }
      } else if (this.expanded && this.picker != null && this.picker.keyPressed(var1, var2, var3)) {
         return true;
      } else {
         if (this.expanded) {
            for (InspectorRow var7 : this.children) {
               if (var7.isVisible() && var7.keyPressed(var1, var2, var3)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean charTyped(CharInput var1) {
      return this.charTyped((char)var1.codepoint(), var1.modifiers());
   }

   public boolean charTyped(char var1, int var2) {
      if (this.editing) {
         this.editBuffer.append(var1);
         this.typedAt = System.currentTimeMillis();
         if (this.setting instanceof TextSetting var5) {
            this.publishEdit(var5);
         }

         return true;
      } else if (this.expanded && this.picker != null && this.picker.charTyped(var1, var2)) {
         return true;
      } else {
         if (this.expanded) {
            for (InspectorRow var4 : this.children) {
               if (var4.isVisible() && var4.charTyped(var1, var2)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private void publishEdit(TextSetting var1) {
      var1.setValue(this.editBuffer.toString());
   }

   private static void playToggleSound(boolean var0) {
      Zenith.getInstance()
         .getClientSounds()
         .method1561(var0 ? Zenith.getInstance().getClientSounds().soundEvent5 : Zenith.getInstance().getClientSounds().soundEvent6);
   }

   private static final class MultiValueAdapter extends BooleanSetting {
      private final MultiSelectSetting.Entry value;

      private MultiValueAdapter(MultiSelectSetting var1, MultiSelectSetting.Entry var2) {
         super(var2.getKey(), var1.getDescription(), var2.isEnabled());
         this.value = var2;
      }

      @Override
      public boolean isEnabled() {
         return this.value.isEnabled();
      }

      @Override
      public void setEnabled(boolean var1) {
         this.value.setEnabled(var1);
      }

      @Override
      public void toggle() {
         this.value.setEnabled(!this.value.isEnabled());
      }

      @Override
      public String getName() {
         return this.value.getName();
      }

      @Override
      public void safe(JsonObject var1) {
      }

      @Override
      public void load(JsonObject var1) {
      }
   }
}

package org.zenith.client.screens.nlgui;

import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.misc.StreamerModeModule;
import org.zenith.client.modules.impl.render.MenuModule;
import org.zenith.client.screens.crosshair.CrosshairEditorPanel;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.screens.nlgui.elements.InspectorRow;
import org.zenith.client.screens.nlgui.panel.CategoryPanel;
import org.zenith.client.screens.nlgui.panel.ClientPanel;
import org.zenith.client.screens.nlgui.panel.CloudChatSectionPanel;
import org.zenith.client.screens.nlgui.panel.CloudSectionPanel;
import org.zenith.client.screens.nlgui.panel.FriendsSectionPanel;
import org.zenith.client.screens.nlgui.panel.GuiModulePanel;
import org.zenith.client.screens.nlgui.panel.InspectorPanel;
import org.zenith.client.screens.nlgui.panel.InterfaceSectionPanel;
import org.zenith.client.screens.nlgui.panel.ProfilePanel;
import org.zenith.client.screens.nlgui.panel.QuickMenuPanel;
import org.zenith.client.screens.nlgui.panel.ScriptsSectionPanel;
import org.zenith.client.screens.nlgui.panel.VoiceSectionPanel;
import org.zenith.client.screens.nlgui.panel.WardrobeSectionPanel;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;
import org.zenith.client.screens.nlgui.panel.tools.ToolsSectionPanel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.scripting.Script;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.misc.WindowUtils;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.RoundedRectBatch;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;
import org.zenith.utility.render.display.base.ZenithDrawContext;

public class NLMenuScreen extends Screen implements MinecraftAccess {
   private final Animation animationClose = new Animation(300L, 0.0F, Easing.INSTANCE4);
   private final Animation searchFocusAnimation = new Animation(170L, 0.0F, Easing.INSTANCE12);
   private final Animation languageAnimation = new Animation(260L, 1.0F, Easing.INSTANCE13);
   private String seenLanguage;
   private float contentDip = 1.0F;
   private ProfilePanel profilePanel;
   private ClientPanel clientPanel;
   private CategoryPanel categoryPanel;
   private final InspectorPanel inspectorPanel = new InspectorPanel();
   private final ModulePreviewStage previewStage = new ModulePreviewStage();
   private final CrosshairEditorPanel crosshairEditor = new CrosshairEditorPanel();
   private GuiModulePanel guiModulePanel;
   private ToolsSectionPanel toolsSectionPanel;
   private CloudSectionPanel cloudSectionPanel;
   private WardrobeSectionPanel cosmeticsSectionPanel;
   private InterfaceSectionPanel interfaceSectionPanel;
   private ScriptsSectionPanel scriptsSectionPanel;
   private QuickMenuPanel quickMenuPanel;
   private NLMenuScreen.ElementsType type = NLMenuScreen.ElementsType.CATEGORY;
   private boolean closing = false;
   public static float widthPanel = 480.0F;
   public static float heightPanel = 320.0F;
   public static float leftWidthPanel = 100.0F;
   public static float rightWidthPanel = 380.0F;
   public static float contentBottom = 320.0F;
   private static final float PANEL_HEIGHT = 320.0F;
   private final Animation previewWidthAnimation = new Animation(220L, 0.0F, Easing.INSTANCE13);
   private final Animation extraWidthAnimation = new Animation(340L, 0.0F, Easing.INSTANCE15);
   private final Animation leftSwapAnimation = new Animation(420L, 0.0F, Easing.INSTANCE15);
   private ElementPanel leftOwner;
   public static float extraWidth;
   private final Animation crosshairWidthAnimation = new Animation(220L, 0.0F, Easing.INSTANCE13);
   private final Animation clientSwapAnimation = new Animation(460L, 0.0F, Easing.INSTANCE15);
   private static final float SWAP_SHIFT = 16.0F;
   private static final float SEAM_FADE_IN = 0.8F;
   private static final float SEAM_OVERLAP = 0.5F;
   private static final float SWAP_FADE_OUT = 0.45F;
   private static final float SWAP_FADE_IN = 0.3F;
   private float x;
   private float y;
   private float rawX;
   private float rawY;
   private boolean init = false;
   private SearchBox searchField;
   private SearchBox commandField;
   private String commandFeedback = "";
   private float smoothScale = Float.NaN;
   private float smoothBlur = Float.NaN;
   private BoundsData dragBounds;
   private BoundsData commandBarBounds;
   private boolean draggingGui;
   private boolean scaleLocked;
   private float lockedScale = 1.0F;
   private final Animation seamRetractAnimation = new Animation(160L, 0.0F, Easing.INSTANCE13);
   private final Animation setTypeAnimation = new Animation(220L, 1.0F, Easing.INSTANCE10);
   private NLMenuScreen.ElementsType prevType;
   private static final float TYPE_TAKEOVER = 0.5F;

   public NLMenuScreen() {
      super(Text.literal("NLMenu"));
   }

   @Override
   public void init() {
      this.animationClose.method2809(0.0F);
      this.animationClose.method2808(1.0F);
      BindCapture.reset();
      this.crosshairWidthAnimation.method2809(0.0F);
      this.unlockGuiScale();
      this.ensurePanelsInitialized();
      this.closing = false;
      if (this.searchField == null) {
         this.searchField = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.MEDIUM.getFont(7.0F), "Search for elements...", 100.0F);
         this.searchField.setType(SearchBox.Type.ENGLISH);
         this.searchField.setType2(SearchBox.Type2.ONLY_ONE_WORLD);
      }

      if (this.commandField == null) {
         this.commandField = new SearchBox(
            new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.4F), Zenith.getInstance().getLocalization().translate("gui.command.hint"), 100.0F
         );
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void renderTop(MenuDrawContext var1, int var2, int var3) {
      ZenithStyle var4 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var4 != null) {
         HoverInfo.beginFrame();
         RoundedRectBatch.begin();

         try {
            this.renderTopDeferred(ZenithDrawContext.method8095(var1), var2, var3, var4);
         } catch (Throwable var7) {
            RoundedRectBatch.method2933();
            throw var7;
         }

         RoundedRectBatch.method2933();
      }
   }

   private void renderTopDeferred(MenuDrawContext var1, int var2, int var3, ZenithStyle var4) {
      this.updateSmoothValues();
      float var5 = GuiStyle.PANEL_ROUND.intValue();
      this.animationClose.setDuration(MenuModule.INSTANCE.method5345());
      this.setTypeAnimation.setDuration(MenuModule.INSTANCE.method5346());
      float var6 = this.animationClose.method2808(this.closing ? 0.0F : 1.0F);
      this.contentDip = 0.32F + 0.68F * this.languageSwitch();
      float var7 = var6 * this.contentDip;
      ElementPanel var8 = this.type.getPanelSupplier().get();
      ElementPanel var9 = this.prevType != null ? this.prevType.getPanelSupplier().get() : null;
      float var10 = this.setTypeAnimation.getValue();
      float var11 = mc.getWindow().getScaledWidth();
      float var12 = mc.getWindow().getScaledHeight();
      boolean var13 = var8 == this.guiModulePanel;
      leftWidthPanel = 100.0F;
      boolean var14;
      if (var14 = var13 && this.inspectorPanel.hasPreview()) {
         this.previewStage.show(this.inspectorPanel.previewKind(), this.inspectorPanel.previewOwner());
      } else if (this.previewStage.isOpen()) {
         this.previewStage.hide();
      }

      float var15 = this.previewWidthAnimation.method2808(var14 ? 128.0F : 0.0F);
      float var16 = this.crosshairWidthAnimation.method2808(this.crosshairEditor.isOpen() ? 176.0F : 0.0F);
      extraWidth = this.extraWidthAnimation.method2808(var8.extraWidth());
      widthPanel = leftWidthPanel + 380.0F + var15 + var16 + extraWidth;
      heightPanel = 320.0F;
      float var17 = var11 / 2.0F - widthPanel / 2.0F;
      float var18 = var12 / 1.9F - heightPanel / 2.0F;
      this.rawX = MenuModule.INSTANCE.method5348() ? MenuModule.INSTANCE.method5351(var17) : var17;
      this.rawY = MenuModule.INSTANCE.method5348() ? MenuModule.INSTANCE.method5352(var18) : var18;
      this.x = this.clampRenderPosition(this.rawX, widthPanel, var11);
      this.y = this.clampRenderPosition(this.rawY, heightPanel, var12);
      float var19 = this.getGuiScale();
      float var20 = this.x + widthPanel / 2.0F;
      float var21 = this.y + heightPanel / 2.0F;
      var2 = (int)((var2 - var20) / var19 + var20);
      var3 = (int)((var3 - var21) / var19 + var21);
      var1.pushMatrix();
      var1.getMatrices().translate(var20, var21);
      var1.getMatrices().scale(var19, var19);
      var1.getMatrices().translate(-var20, -var21);
      float var22;
      float var23 = (var22 = this.x + leftWidthPanel) + 236.0F;
      float var24 = this.x + widthPanel + GuiStyle.PADDING * 2;
      float var25;
      if ((var25 = this.getBlurPower() * var6) != 0.0F) {
         RenderShaders.method5287(
            MatrixUtils.method664(var1.getMatrices()),
            this.x,
            this.y,
            widthPanel,
            heightPanel,
            var25,
            ProxyDotRadiusData.method8069(var5),
            ZenithColor.INSTANCE.method7294(var6),
            true,
            false
         );
      }

      var1.drawRoundedRect(
         this.x,
         this.y,
         leftWidthPanel + 0.5F,
         heightPanel,
         ProxyDotRadiusData.method8078(var5, var5),
         var4.getLeftBackground().getZenithColor().method7294(var6)
      );
      var1.drawRoundedRect(
         var22 - 0.5F,
         this.y,
         380.0F + var15 + var16 + extraWidth + 0.5F,
         heightPanel,
         ProxyDotRadiusData.method8079(var5, var5),
         var4.getRightBackground().getZenithColor().method7294(var6)
      );
      this.dragBounds = new BoundsData(this.x, this.y, widthPanel, 23.0F);
      float var26 = this.clientSwapAnimation.method2808(this.quickMenuPanel.isExpanded() ? 1.0F : 0.0F);
      boolean var27 = this.quickMenuPanel.isExpanded();
      float var28 = swapOut(var26, var27);
      float var29 = swapIn(var26, var27);
      boolean var30;
      if (var30 = var8.hasLeftColumn()) {
         this.leftOwner = var8;
      }

      float var31 = this.leftSwapAnimation.method2808(var30 ? 1.0F : 0.0F);
      float var32 = 1.0F - MathHelper.clamp(var31 * 1.6F, 0.0F, 1.0F);
      boolean var33;
      if (var33 = var26 > 0.001F && var26 < 0.999F || var31 > 0.001F && var31 < 0.999F) {
         var1.enableScissor(this.x, this.y, this.x + leftWidthPanel, this.y + heightPanel);
      }

      try {
         if (var32 > 0.001F) {
            this.categoryPanel
               .render(
                  var1,
                  var2,
                  var3,
                  var7 * (var27 ? var28 : var29) * var32,
                  this.x - (var27 ? 16.0F * var26 : 0.0F) - 16.0F * var31,
                  this.y,
                  var27 ? 1.0F : var29
               );
         }

         if (var31 > 0.001F && this.leftOwner != null) {
            float var34 = MathHelper.clamp((var31 - 0.25F) / 0.75F, 0.0F, 1.0F);
            this.leftOwner.renderLeftColumn(var1, var2, var3, var7 * var34, this.x + 16.0F * (1.0F - var31), this.y, leftWidthPanel, heightPanel);
         }

         if (var30 && this.quickMenuPanel.isExpanded()) {
            this.quickMenuPanel.setExpanded(false);
         }

         this.quickMenuPanel
            .renderColumn(var1, var2, var3, var7 * (var27 ? var29 : var28) * var32, this.x + (var27 ? 0.0F : 16.0F * (1.0F - var26)), this.y, leftWidthPanel);
      } finally {
         if (var33) {
            var1.disableScissor();
         }
      }

      if (this.setTypeAnimation.isDone() && this.prevType != null) {
         this.prevType.getPanelSupplier().get().close();
         this.prevType = null;
      } else {
         this.setTypeAnimation.method2808(1.0F);
      }

      float var41 = this.y
         + (var9 != null ? MathHelper.lerp(var10, this.headerContentOffset(var9), this.headerContentOffset(var8)) : this.headerContentOffset(var8));
      contentBottom = this.y + heightPanel;
      float var35 = MathHelper.lerp(this.seamRetract(), this.y + GuiStyle.PADDING.intValue(), var41);
      this.renderColumnSeams(var1, var4, var22, var23, var35, var6, var13, var10);
      this.renderContentHeader(var1, var2, var3, var7, var22, this.y, var8, var9, var10, var13);
      if (var9 != null) {
         rightWidthPanel = var9.getContentWidth();
         var9.render(var1, var2, var3, var7 * (1.0F - var10), var22, var41);
      }

      rightWidthPanel = var8.getContentWidth();
      var8.render(var1, var2, var3, var7 * var10, var22, var41);
      this.inspectorPanel.setActive(var13);
      if (var13) {
         this.inspectorPanel.render(var1, var2, var3, var7 * var10, var23, this.y);
         if (var15 > 1.0F) {
            this.renderPreviewColumn(var1, var2, var3, var23 + 144.0F, this.y, var15, var6 * var10, var4);
         }

         this.renderCommandBar(var1, var2, var3, var22, this.y + heightPanel - 20.0F, 236.0F, var7 * var10);
      } else {
         this.commandBarBounds = null;
         if (this.commandField != null) {
            this.commandField.setSelected(false);
         }
      }

      if (var9 != null && var9.isRender()) {
         var9.renderRightPanel(var1, var2, var3, var6 * (1.0F - var10), var24, this.y, 1.0F - var10);
      }

      if (var8.isRender()) {
         var8.renderRightPanel(var1, var2, var3, var6 * var10, var24, this.y, var10);
      }

      ScissorFlusher.flush();
      Zenith.getInstance().getKawaseBlur().method7350(var1);
      if (var9 != null) {
         var9.renderPriority(var1, var2, var3, var7 * (1.0F - var10), var22, var41);
      }

      var8.renderPriority(var1, var2, var3, var7 * var10, var22, var41);
      if (!this.closing && this.prevType != null) {
         float var36 = var9 != null ? Math.max(var9.getContentWidth(), var8.getContentWidth()) : var8.getContentWidth();
         this.renderElementSwapBlur(var1, var22, var41, var36, contentBottom - var41, ProxyDotRadiusData.method8076(0.0F, var5), 1.0F - var10);
      }

      this.renderMenuBlurLayers(var1, var6, var8, var9, var10, var24, this.y, var5);
      if (var16 > 1.0F) {
         this.renderCrosshairColumn(var1, var2, var3, this.x + widthPanel - var16, this.y, var16, var6);
      }

      var1.popMatrix();
   }

   private float languageSwitch() {
      Zenith var1 = Zenith.getInstance();
      String var2 = var1 == null ? null : var1.getLocalization().getLanguageName();
      if (var2 == null) {
         return 1.0F;
      } else {
         if (this.seenLanguage == null) {
            this.seenLanguage = var2;
         } else if (!this.seenLanguage.equals(var2)) {
            this.seenLanguage = var2;
            this.languageAnimation.reset(0.0F);
            this.languageAnimation.method2810(1.0F);
         }

         return this.languageAnimation.update();
      }
   }

   private static float swapOut(float var0, boolean var1) {
      float var2 = var1 ? var0 : 1.0F - var0;
      return MathHelper.clamp(1.0F - var2 / 0.45F, 0.0F, 1.0F);
   }

   private static float swapIn(float var0, boolean var1) {
      return MathHelper.clamp(((var1 ? var0 : 1.0F - var0) - 0.3F) / 0.7F, 0.0F, 1.0F);
   }

   private void renderPreviewColumn(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      var1.drawRect(
         var4,
         var5 + GuiStyle.PADDING.intValue(),
         1.0F,
         heightPanel - GuiStyle.PADDING.intValue() * 2.0F,
         var8.getFieldBorder().getZenithColor().method7294(var7 * 2.0F)
      );
      float var9 = MathHelper.clamp(var6 / 128.0F, 0.0F, 1.0F);
      float var10;
      if (!((var10 = var7 * var9 * var9) <= 0.01F)) {
         Font var11 = Fonts.NEW_MEDIUM.getFont(5.0F);
         Font var12 = Fonts.NEW_REGULAR.getFont(4.3F);
         float var13 = var4 + GuiStyle.PADDING * 2;
         float var14 = var5 + GuiStyle.PADDING * 2;
         var1.drawText(
            var11,
            Zenith.getInstance().getLocalization().translate("esp.preview"),
            var13,
            var14 + 1.0F,
            var8.getTextEnable().getZenithColor().method7294(var10)
         );
         var1.drawText(
            var12,
            Zenith.getInstance().getLocalization().translate("esp.preview.desc"),
            var13,
            var14 + var11.height() + 3.0F,
            var8.getTextTertiary().getZenithColor().method7294(var10)
         );
         if (!this.previewStage.isLive()) {
            Font var15 = Fonts.NEW_REGULAR.getFont(4.6F);
            String var16 = Zenith.getInstance().getLocalization().translate("esp.preview.disabled");
            var1.drawText(
               var15,
               var16,
               var4 + (var6 - var15.width(var16)) / 2.0F,
               var5 + 23.0F + GuiStyle.PADDING * 2,
               var8.getTextTertiary().getZenithColor().method7294(var10)
            );
         }

         boolean var20;
         if (var20 = var9 < 0.995F) {
            var1.enableScissor(var4, var5, var4 + var6, var5 + heightPanel);
         }

         try {
            this.previewStage.render(var1, var2, var3, var4, var5 + 23.0F, 128.0F, heightPanel - 23.0F, var10, var9);
         } finally {
            if (var20) {
               var1.disableScissor();
            }
         }
      }
   }

   private void renderCrosshairColumn(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      ZenithStyle var8 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var8 != null) {
         var1.drawRect(
            var4,
            var5 + GuiStyle.PADDING.intValue(),
            1.0F,
            heightPanel - GuiStyle.PADDING.intValue() * 2.0F,
            var8.getFieldBorder().getZenithColor().method7294(var7 * 2.0F)
         );
         float var9 = MathHelper.clamp(var6 / 176.0F, 0.0F, 1.0F);
         this.crosshairEditor.render(var1, var2, var3, var4, var5, 176.0F, heightPanel, var7, var9);
      }
   }

   private void renderColumnSeams(MenuDrawContext var1, ZenithStyle var2, float var3, float var4, float var5, float var6, boolean var7, float var8) {
      float var9;
      if (!((var9 = this.y + heightPanel - GuiStyle.PADDING.intValue() - var5) <= 0.0F)) {
         var1.drawRect(var3, var5, 1.0F, var9, var2.getFieldBorder().getZenithColor().method7294(var6 * 2.0F));
         if (var7) {
            float var10;
            if ((var10 = MathHelper.clamp((var8 - 0.8F) / 0.19999999F, 0.0F, 1.0F)) > 0.001F) {
               var1.drawRect(var4, var5, 1.0F, var9, var2.getFieldBorder().getZenithColor().method7294(var6 * var10 * 2.0F));
            }
         }
      }
   }

   private float seamRetract() {
      boolean var1 = !this.closing && this.prevType != null && MenuModule.INSTANCE.method5344() && this.getBlurPower() != 0.0F;
      return this.seamRetractAnimation.method2808(var1 ? 1.0F : 0.0F);
   }

   private void renderContentHeader(
      MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, ElementPanel var7, ElementPanel var8, float var9, boolean var10
   ) {
      float var11 = var6 + GuiStyle.PADDING.intValue();
      float var12 = this.headerSearchWidth(var7);
      float var13;
      float var14 = (var13 = this.headerSearchX(var5, var7, var12)) + var12;
      float var15 = var13;
      float var16 = var12;
      float var17 = this.headerBandHeight(var7);
      if (var8 != null) {
         float var18 = this.headerSearchWidth(var8);
         float var19;
         float var20 = (var19 = this.headerSearchX(var5, var8, var18)) + var18;
         var17 = MathHelper.lerp(var9, this.headerBandHeight(var8), var17);
         var15 = MathHelper.lerp(var9, var19, var13);
         var16 = MathHelper.lerp(var9, var20, var14) - var15;
      }

      if (var8 != null) {
         this.renderPanelHeader(var1, var2, var3, var8, var5, var11, var4 * (1.0F - var9), 1.0F - var9);
      }

      this.renderPanelHeader(var1, var2, var3, var7, var5, var11, var4 * var9, var9);
      this.renderSearch(var1, var15, var11, var17, var16, var4, var10, var10 ? var9 : 0.0F);
   }

   private void renderPanelHeader(MenuDrawContext var1, int var2, int var3, ElementPanel var4, float var5, float var6, float var7, float var8) {
      if (!(var7 <= 0.001F)) {
         float var9 = Math.max(0.0F, var4.getContentWidth() - this.headerSearchWidth(var4) - GuiStyle.PADDING.intValue());
         var4.renderHeader(var1, var2, var3, var7, var5, var6, var9);
         float var10;
         if ((var10 = var4.getButtonWidth()) > 0.01F) {
            var4.renderHeaderButtons(var1, var2, var3, var7, var5 + var4.getHeaderTitleWidth() + GuiStyle.PADDING.intValue() * 2.0F, var6, var10, var8);
         }
      }
   }

   private float headerSearchWidth(ElementPanel var1) {
      return var1.isHeaderSearchOnly() ? 236.0F : Math.min(120.0F, var1.getContentWidth());
   }

   private float headerSearchX(float var1, ElementPanel var2, float var3) {
      return var2.isHeaderSearchOnly() ? var1 : var1 + var2.getContentWidth() - var3;
   }

   private float headerBandHeight(ElementPanel var1) {
      return var1.isHeaderSearchOnly() ? 23.0F - GuiStyle.PADDING.intValue() * 2.0F : 23.0F;
   }

   private float headerContentOffset(ElementPanel var1) {
      return var1.isHeaderSearchOnly() ? 23.0F : 23.0F + GuiStyle.PADDING.intValue() * 2.0F;
   }

   private void renderSearch(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, boolean var7, float var8) {
      ZenithStyle var9 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var9 != null) {
         boolean var10 = this.searchField != null && this.searchField.isSelected();
         float var11 = 8.0F;
         float var12 = this.searchFocusAnimation.method2808(var10 ? 1.0F : 0.0F);
         ProxyDotRadiusData var13 = ProxyDotRadiusData.method8069(var4 / 2.0F);
         var1.drawRoundedRectBatched(
            var2 + GuiStyle.PADDING.intValue(),
            var3,
            var5 - GuiStyle.PADDING.intValue() * 2.0F,
            var4,
            var13,
            var9.getPanelLeftBackground().getZenithColor().method7294(var6)
         );
         var1.flushRoundedRects();
         if (var12 > 0.01F) {
            var1.drawRoundedBorder(
               var2 + GuiStyle.PADDING.intValue(),
               var3,
               var5 - GuiStyle.PADDING.intValue() * 2.0F,
               var4,
               0.15F,
               var13,
               var9.getPrimaryColor().getZenithColor().method7292(0.34F).method7294(var6 * var12)
            );
         }

         Font var14 = Fonts.NEW_MEDIUM.getFont(5.2F);
         float var15 = var3 + (var4 - var14.height()) / 2.0F;
         MenuGlyph.SEARCH
            .draw(
               var1,
               var2 + var11 + GuiStyle.PADDING.intValue(),
               var3 + (var4 - 6.0F) / 2.0F,
               var9.getTextTertiary().getZenithColor().method7295(var9.getTextEnable().getZenithColor(), var12).method7294(var6)
            );
         this.searchField.setFont(var14);
         this.searchField.setWidth(var5 - var11 * 2.0F - 30.0F);
         this.searchField
            .method987(
               var1,
               var2 + var11 + GuiStyle.PADDING.intValue() + 8.0F,
               var15,
               var9.getTextEnable().getZenithColor().method7294(var6),
               var9.getTextTertiary().getZenithColor().method7294(var6)
            );
         if (var7 && this.guiModulePanel != null && var8 > 0.01F) {
            String var16 = String.valueOf(this.guiModulePanel.getVisibleCount());
            Font var17 = Fonts.NEW_REGULAR.getFont(4.8F);
            var1.drawText(
               var17,
               var16,
               var2 + var5 - var11 - GuiStyle.PADDING.intValue() - var17.width(var16),
               var3 + (var4 - var17.height()) / 2.0F,
               var9.getTextTertiary().getZenithColor().method7294(var6 * var8)
            );
         }
      }
   }

   private void renderCommandBar(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      ZenithStyle var8 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var8 != null) {
         var1.drawRect(
            var4 + GuiStyle.PADDING.intValue() * 2.0F,
            var5,
            var6 - GuiStyle.PADDING.intValue() * 4.0F,
            1.0F,
            var8.getFieldBorder().getZenithColor().method7294(var7 * 2.0F)
         );
         Font var9 = Fonts.NEW_MEDIUM.getFont(5.0F);
         float var10 = var5 + (20.0F - var9.height()) / 2.0F + 1.0F;
         float var11 = 8.0F;
         boolean var12 = this.commandField != null && this.commandField.isSelected();
         var1.drawText(var9, ":", var4 + var11, var10, (var12 ? var8.getPrimaryColor() : var8.getTextTertiary()).getZenithColor().method7294(var7));
         this.commandField.setFont(var9);
         this.commandField.setWidth(var6 - var11 * 2.0F - 12.0F);
         this.commandField
            .method987(
               var1,
               var4 + var11 + 8.0F,
               var10,
               var8.getTextEnable().getZenithColor().method7294(var7),
               var8.getTextTertiary().getZenithColor().method7294(var7)
            );
         this.commandBarBounds = new BoundsData(var4, var5, var6, 20.0F);
         if (!this.commandFeedback.isEmpty() && !var12) {
            Font var13 = Fonts.NEW_REGULAR.getFont(4.8F);
            var1.drawText(
               var13,
               this.commandFeedback,
               var4 + var6 - var11 - var13.width(this.commandFeedback),
               var10,
               var8.getTextTertiary().getZenithColor().method7294(var7)
            );
         }
      }
   }

   private void runCommand() {
      String var1 = this.commandField.getText();
      if (var1 != null && !var1.isBlank()) {
         String var2;
         try {
            Zenith.getInstance().getCommandManager().getDispatcher().execute(var1.trim(), Zenith.getInstance().getCommandManager().getSource());
            var2 = "gui.command.ok";
         } catch (Exception var4) {
            var2 = "gui.command.error";
         }

         this.commandFeedback = Zenith.getInstance().getLocalization().translate(var2);
         this.commandField.setText("");
         this.commandField.setCursor(0);
      }
   }

   private void renderMenuBlurLayers(MenuDrawContext var1, float var2, ElementPanel var3, ElementPanel var4, float var5, float var6, float var7, float var8) {
      if (MenuModule.INSTANCE.method5344()) {
         float var9;
         if ((var9 = this.getBlurPower()) != 0.0F) {
            if (this.closing) {
               this.drawBlurLayer(var1, this.x, this.y, widthPanel, heightPanel, var8, var2, var9);
               this.drawRightPanelBlur(var1, var4, var2 * (1.0F - var5), 1.0F - var5, var6, var7, var2, var9);
               this.drawRightPanelBlur(var1, var3, var2 * var5, var5, var6, var7, var2, var9);
            }
         }
      }
   }

   private void drawRightPanelBlur(MenuDrawContext var1, ElementPanel var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if (var2 != null && var2.isRender()) {
         BoundsData var9 = var2.getRightPanelBlurBounds(var5, var6, var3, var4);
         if (var9 != null) {
            this.drawBlurLayer(var1, var9.getFloatValue(), var9.getFloatValue2(), var9.width(), var9.height(), GuiStyle.ROUND.intValue(), var7, var8);
         }
      }
   }

   private void drawBlurLayer(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.drawBlurLayer(var1, var2, var3, var4, var5, ProxyDotRadiusData.method8069(var6), var7, var8);
   }

   private void drawBlurLayer(MenuDrawContext var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, float var7, float var8) {
      float var9;
      if (!((var9 = this.getBlurLayerAlpha(var7)) <= 0.001F)) {
         RenderShaders.method5289(MatrixUtils.method664(var1.getMatrices()), var2, var3, var4, var5, var8, var6, ZenithColor.INSTANCE.method7294(var9));
      }
   }

   public void renderElementSwapBlur(MenuDrawContext var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, float var7) {
      if (MenuModule.INSTANCE.method5344()) {
         float var8;
         if ((var8 = this.getBlurPower()) != 0.0F) {
            this.drawBlurLayer(var1, var2, var3, var4, var5, var6, var7, var8);
         }
      }
   }

   private float getBlurLayerAlpha(float var1) {
      float var2;
      if ((var2 = MathHelper.clamp(var1, 0.0F, 1.0F)) >= 0.75F) {
         float var4;
         return (var4 = (1.0F - var2) / 0.25F) * var4 * (3.0F - 2.0F * var4);
      } else {
         float var3;
         return var2 >= 0.3F ? 1.0F : (float)Math.sqrt(var3 = var2 / 0.3F);
      }
   }

   public void resetSearch() {
      if (this.searchField != null) {
         this.searchField.setText("");
         this.searchField.setSelected(false);
         this.searchField.setCursor(0);
      }
   }

   public String getSearchValue() {
      return this.searchField == null ? "" : this.searchField.getText();
   }

   public void openTool(int var1) {
      this.ensurePanelsInitialized();
      this.resetSearch();
      this.setType(NLMenuScreen.ElementsType.TOOLS);
      this.toolsSectionPanel.open(var1);
      if (mc.currentScreen != this) {
         mc.setScreen(this);
      }
   }

   public void openCosmetics(int var1) {
      this.ensurePanelsInitialized();
      this.resetSearch();
      this.setType(NLMenuScreen.ElementsType.COSMETICS);
      if (var1 >= 0) {
         this.cosmeticsSectionPanel.devTab(var1);
      }

      if (mc.currentScreen != this) {
         mc.setScreen(this);
      }
   }

   public void openMarket(int var1) {
      this.ensurePanelsInitialized();
      this.resetSearch();
      this.setType(NLMenuScreen.ElementsType.SCRIPTS);
      this.scriptsSectionPanel.openMarket(var1);
      if (mc.currentScreen != this) {
         mc.setScreen(this);
      }
   }

   public void openScriptSettings(Script var1) {
      this.ensurePanelsInitialized();
      this.resetSearch();
      this.setType(NLMenuScreen.ElementsType.SCRIPTS);
      this.scriptsSectionPanel.openScriptSettings(var1);
      if (mc.currentScreen != this) {
         mc.setScreen(this);
      }
   }

   public void devShowModule(Module var1) {
      this.ensurePanelsInitialized();
      this.setType(NLMenuScreen.ElementsType.CATEGORY);
      this.inspectorPanel.showFor(var1);
   }

   public void openHudElementSettings(Interface var1) {
      if (var1 != null) {
         this.ensurePanelsInitialized();
         this.resetSearch();
         this.setType(NLMenuScreen.ElementsType.INTERFACE);
         this.interfaceSectionPanel.openHudElementSettings(var1);
      }
   }

   public ClientPanel getClientPanel() {
      return this.clientPanel;
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      float var9 = this.getGuiScale();
      float var10 = this.x + widthPanel / 2.0F;
      float var11 = this.y + heightPanel / 2.0F;
      mouseX = (int)((mouseX - var10) / var9 + var10);
      mouseY = (int)((mouseY - var11) / var9 + var11);
      if (this.crosshairEditor.onMouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
         return true;
      } else if (this.quickMenuPanel != null && this.quickMenuPanel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
         return true;
      } else if (this.inspectorPanel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
         return true;
      } else {
         this.type.getPanelSupplier().get().mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
         return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
      }
   }

   @Override
   public boolean keyPressed(KeyInput input) {
      return this.keyPressed(input.key(), input.scancode(), input.modifiers());
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.crosshairEditor.keyPressed(var1, var2, var3)) {
         return true;
      } else {
         if (BindCapture.isActive()) {
            if (BindCapture.owner() == this.inspectorPanel && this.inspectorPanel.keyPressed(var1, var2, var3)) {
               return true;
            }

            if (this.getType().getPanelSupplier().get().keyPressed(var1, var2, var3)) {
               return true;
            }

            if (this.inspectorPanel.keyPressed(var1, var2, var3)) {
               return true;
            }

            BindCapture.reset();
         }

         if (this.commandField != null && this.commandField.isSelected()) {
            if (var1 == 257 || var1 == 335) {
               this.runCommand();
               return true;
            }

            if (var1 == 256) {
               this.commandField.setSelected(false);
               return true;
            }

            if (this.commandField.keyPressed(var1, var2, var3)) {
               return true;
            }
         }

         if (this.searchField.keyPressed(var1, var2, var3)) {
            return true;
         } else if (this.getType().getPanelSupplier().get().keyPressed(var1, var2, var3)) {
            return true;
         } else if (this.inspectorPanel.keyPressed(var1, var2, var3)) {
            return true;
         } else if (this.handleNavigation(var1)) {
            return true;
         } else {
            if (var1 == 256 && !this.closing) {
               if (this.quickMenuPanel != null) {
                  this.quickMenuPanel.collapseOpenRow();
                  this.quickMenuPanel.setExpanded(false);
               }

               if (this.searchField != null) {
                  this.searchField.setSelected(false);
               }

               BindCapture.reset();
               this.closing = true;
               this.mouseReleased(0.0, 0.0, 0);
               this.mouseReleased(0.0, 0.0, 0);
               this.mouseReleased(0.0, 0.0, 0);
               Zenith.getInstance().getClientSounds().method1561(Zenith.getInstance().getClientSounds().soundEvent2);
            }

            return super.keyPressed(new KeyInput(var1, var2, var3));
         }
      }
   }

   private boolean handleNavigation(int var1) {
      if (this.type != NLMenuScreen.ElementsType.CATEGORY || this.guiModulePanel == null) {
         return false;
      } else if (var1 == 70 && WindowUtils.method5475()) {
         this.searchField.setSelected(true);
         return true;
      } else {
         boolean var2 = this.searchField != null && this.searchField.isSelected();
         switch (var1) {
            case 257:
            case 335:
               return this.guiModulePanel.toggleCaret();
            case 262:
               if (var2) {
                  return false;
               }

               return this.guiModulePanel.openCaret();
            case 263:
               if (var2) {
                  return false;
               } else {
                  if (this.inspectorPanel.isOpen()) {
                     this.inspectorPanel.close();
                     return true;
                  }

                  return false;
               }
            case 264:
               this.guiModulePanel.moveCaret(1);
               return true;
            case 265:
               this.guiModulePanel.moveCaret(-1);
               return true;
            default:
               return false;
         }
      }
   }

   @Override
   public boolean charTyped(CharInput input) {
      return this.charTyped((char)input.codepoint(), input.modifiers());
   }

   public boolean charTyped(char var1, int var2) {
      if (this.crosshairEditor.charTyped(var1, var2)) {
         return true;
      } else if (this.commandField != null && this.commandField.isSelected() && this.commandField.charTyped(var1, var2)) {
         return true;
      } else if (this.searchField.charTyped(var1, var2)) {
         return true;
      } else if (this.inspectorPanel.charTyped(var1, var2)) {
         return true;
      } else {
         return this.getType().getPanelSupplier().get().charTyped(var1, var2) ? true : super.charTyped(new CharInput(var1, var2));
      }
   }

   @Override
   public boolean mouseClicked(Click click, boolean doubled) {
      return this.mouseClicked(click.x(), click.y(), click.button());
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      float var6 = this.getGuiScale();
      float var7 = this.x + widthPanel / 2.0F;
      float var8 = this.y + heightPanel / 2.0F;
      var1 = (int)((var1 - var7) / var6 + var7);
      var3 = (int)((var3 - var8) / var6 + var8);
      Enum3 var9 = Enum3.method6094(var5);
      if (BindCapture.owner() == this.inspectorPanel && !MouseBindTarget.bindable(var9)) {
         this.inspectorPanel.cancelHeaderBindingOutside(var1, var3);
      }

      if (BindCapture.owner() instanceof InspectorRow var10 && !MouseBindTarget.bindable(var9)) {
         var10.cancelModuleBindingOutside(var1, var3);
      }

      if (this.crosshairEditor.onMouseClicked(var1, var3, var9)) {
         return true;
      } else if (BindCapture.owner() instanceof MouseBindTarget var15 && MouseBindTarget.bindable(var9) && var15.bindMouse(var9)) {
         return true;
      } else if (this.quickMenuPanel != null
         && !this.getType().getPanelSupplier().get().hasLeftColumn()
         && this.quickMenuPanel.onMouseClicked(var1, var3, var9)) {
         return true;
      } else if (this.previewStage.onMouseClicked(var1, var3, var9)) {
         return true;
      } else if (this.searchField.onMouseClicked(var1, var3, var9)) {
         this.commandField.setSelected(false);
         return true;
      } else if (this.commandBarBounds != null && this.commandBarBounds.contains(var1, var3)) {
         this.searchField.setSelected(false);
         this.commandField.setSelected(true);
         this.commandFeedback = "";
         return true;
      } else if (this.commandField.onMouseClicked(var1, var3, var9)) {
         return true;
      } else {
         try {
            ElementPanel var16 = this.getType().getPanelSupplier().get();
            if (var16.onHeaderButtonsClicked(var1, var3, var9)) {
               return true;
            }

            if (MenuModule.INSTANCE.method5348() && var5 == 2 && this.dragBounds != null && this.dragBounds.contains(var1, var3)) {
               this.draggingGui = true;
               return true;
            }

            if (this.inspectorPanel.onMouseClicked(var1, var3, var9)) {
               return true;
            }

            if (var16.onMouseClicked(var1, var3, var9)) {
               return true;
            }
         } catch (Exception var12) {
            var12.printStackTrace();
         }

         return !this.getType().getPanelSupplier().get().hasLeftColumn() && this.categoryPanel.onMouseClicked(var1, var3, var9)
            ? true
            : super.mouseClicked(new Click(var1, var3, new MouseInput(var5, 0)), false);
      }
   }

   @Override
   public boolean mouseReleased(Click click) {
      return this.mouseReleased(click.x(), click.y(), click.button());
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (var5 == 2) {
         this.draggingGui = false;
      }

      this.unlockGuiScale();
      float var6 = this.getGuiScale();
      float var7 = this.x + widthPanel / 2.0F;
      float var8 = this.y + heightPanel / 2.0F;
      var1 = (int)((var1 - var7) / var6 + var7);
      var3 = (int)((var3 - var8) / var6 + var8);
      this.crosshairEditor.onMouseReleased(var1, var3, Enum3.method6094(var5));
      this.previewStage.onMouseReleased();
      if (this.quickMenuPanel != null) {
         this.quickMenuPanel.onMouseReleased(var1, var3, Enum3.method6094(var5));
      }

      if (this.getType().getPanelSupplier().get().onMouseReleased(var1, var3, Enum3.method6094(var5))) {
         return true;
      } else {
         this.inspectorPanel.onMouseReleased(var1, var3, Enum3.method6094(var5));
         return super.mouseReleased(new Click(var1, var3, new MouseInput(var5, 0)));
      }
   }

   @Override
   public boolean mouseDragged(Click click, double offsetX, double offsetY) {
      return this.mouseDragged(click.x(), click.y(), click.button(), offsetX, offsetY);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (MenuModule.INSTANCE.method5348() && this.draggingGui && var5 == 2) {
         this.rawX += (float)var6;
         this.rawY += (float)var8;
         MenuModule.INSTANCE.method5353(this.rawX, this.rawY);
         return true;
      } else {
         float var10 = this.getGuiScale();
         float var11 = this.x + widthPanel / 2.0F;
         float var12 = this.y + heightPanel / 2.0F;
         double var13 = (int)((var1 - var11) / var10 + var11);
         double var15 = (int)((var3 - var12) / var10 + var12);
         if (this.quickMenuPanel != null && this.quickMenuPanel.onMouseDragged(var6, var10)) {
            return true;
         } else if (this.inspectorPanel.onMouseDragged(var13, var15)) {
            return true;
         } else if (this.crosshairEditor.onMouseDragged(var6, var8)) {
            return true;
         } else if (this.previewStage.onMouseDragged(var13, var15, var6, var8)) {
            return true;
         } else {
            return this.getType().getPanelSupplier().get().onMouseDragged(var13, var15, var5, var6, var8)
               ? true
               : super.mouseDragged(new Click(var1, var3, new MouseInput(var5, 0)), var6, var8);
         }
      }
   }

   @Override
   public void tick() {
      if (this.closing && this.animationClose.getValue() == 0.0F) {
         this.close();
      }

      this.getType().getPanelSupplier().get().tick();
      super.tick();
   }

   @Override
   public void onFilesDropped(List<Path> paths) {
      if (this.scriptsSectionPanel != null) {
         this.scriptsSectionPanel.filesDropped(paths);
      }
   }

   @Override
   public void removed() {
      this.closing = true;
      this.crosshairEditor.close();
      BindCapture.reset();
      this.previewStage.releaseModel();
      if (this.searchField != null) {
         this.searchField.setSelected(false);
      }

      this.draggingGui = false;
      this.mouseReleased(0.0, 0.0, 0);
      this.mouseReleased(0.0, 0.0, 0);
      this.mouseReleased(0.0, 0.0, 0);
      super.removed();
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
   }

   @Override
   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
   }

   public boolean isRenderHud() {
      return mc.currentScreen == this && !this.closing && this.getType() == NLMenuScreen.ElementsType.INTERFACE && this.interfaceSectionPanel.isHudTab();
   }

   private float clampRenderPosition(float var1, float var2, float var3) {
      float var4 = Math.max(0.0F, var3 - var2);
      return MathHelper.clamp(var1, 0.0F, var4);
   }

   public boolean isFinish() {
      return this.animationClose.getValue() == 0.0F && this.closing;
   }

   public void initialize() {
      this.ensurePanelsInitialized();
   }

   public boolean isShortMode() {
      return this.clientPanel != null && !this.clientPanel.getRenderDescription().getSetting().isEnabled();
   }

   public void setRenderIcon(boolean var1) {
      this.clientPanel.getRenderIcon().getSetting().setEnabled(var1);
   }

   public boolean isRenderIcon() {
      return this.clientPanel.getRenderIcon().getSetting().isEnabled();
   }

   public boolean isStreamerMode() {
      return StreamerModeModule.INSTANCE.isEnabled();
   }

   public void toggleStreamerMode() {
      StreamerModeModule.INSTANCE.toggle();
   }

   public void lockGuiScale() {
      if (!this.scaleLocked) {
         this.lockedScale = this.getGuiScale();
         this.scaleLocked = true;
      }
   }

   public void unlockGuiScale() {
      this.scaleLocked = false;
   }

   public float getGuiScale() {
      if (this.scaleLocked) {
         return this.lockedScale;
      } else if (this.clientPanel == null) {
         return 1.0F;
      } else {
         float var1 = (Float.isNaN(this.smoothScale) ? this.clientPanel.getGuiScale().getApplayValue() : this.smoothScale) / 100.0F;
         float var2 = 0.35F;
         float var3 = 100.0F;
         float var4 = mc.getWindow().getScaledWidth();
         float var5 = mc.getWindow().getScaledHeight();
         float var6 = var4 / (widthPanel + var3);
         float var7 = var5 / (heightPanel + var3);
         float var8 = Math.min(var6, var7);
         if (Float.isNaN(var1) || Float.isInfinite(var1)) {
            return 1.0F;
         } else if (var8 <= 0.0F) {
            return var2;
         } else {
            return var8 < var2 ? var8 : MathHelper.clamp(var1, var2, var8);
         }
      }
   }

   public void safe(JsonObject var1) {
      this.ensurePanelsInitialized();
      JsonObject var2 = new JsonObject();
      var2.addProperty("type", this.type.name());
      JsonObject var3 = new JsonObject();
      this.profilePanel.safe(var3);
      var2.add("profilePanel", var3);
      JsonObject var4 = new JsonObject();
      this.clientPanel.safe(var4);
      var2.add("clientPanel", var4);
      var1.add("NLMenuScreen", var2);
   }

   public void load(JsonObject var1) {
      this.ensurePanelsInitialized();
      if (var1.has("NLMenuScreen") && var1.get("NLMenuScreen").isJsonObject()) {
         JsonObject var2 = var1.getAsJsonObject("NLMenuScreen");
         if (var2.has("type")) {
            try {
               this.setType(NLMenuScreen.ElementsType.valueOf(var2.get("type").getAsString()));
            } catch (IllegalArgumentException var4) {
            }
         }

         if (var2.has("profilePanel") && var2.get("profilePanel").isJsonObject()) {
            this.profilePanel.load(var2.getAsJsonObject("profilePanel"));
         }

         if (var2.has("clientPanel") && var2.get("clientPanel").isJsonObject()) {
            this.clientPanel.load(var2.getAsJsonObject("clientPanel"));
         }
      }
   }

   public void refreshModules(Collection<Module> var1) {
      for (Module var3 : var1) {
         this.inspectorPanel.forget(var3);
      }

      if (this.init && this.guiModulePanel != null) {
         this.guiModulePanel.rebuild();
      }
   }

   private void ensurePanelsInitialized() {
      if (!this.init) {
         this.guiModulePanel = new GuiModulePanel();
         this.toolsSectionPanel = new ToolsSectionPanel();
         this.cloudSectionPanel = new CloudSectionPanel(new FriendsSectionPanel(), new CloudChatSectionPanel(), new VoiceSectionPanel());
         this.cosmeticsSectionPanel = new WardrobeSectionPanel();
         this.cosmeticsSectionPanel.setEmbedded(true);
         this.interfaceSectionPanel = new InterfaceSectionPanel();
         this.scriptsSectionPanel = new ScriptsSectionPanel();
         this.profilePanel = new ProfilePanel();
         this.clientPanel = new ClientPanel();
         this.categoryPanel = new CategoryPanel();
         this.init = true;
         this.quickMenuPanel = new QuickMenuPanel();
      }
   }

   public float getBlurPower() {
      if (this.clientPanel == null) {
         return 0.0F;
      } else {
         return Float.isNaN(this.smoothBlur) ? this.clientPanel.getBlurStrength().getSetting().getValue3() : this.smoothBlur;
      }
   }

   private void updateSmoothValues() {
      if (this.clientPanel != null) {
         this.smoothBlur = approach(this.smoothBlur, this.clientPanel.getBlurStrength().getSetting().getValue3(), 0.3F, 0.05F);
         this.smoothScale = approach(this.smoothScale, this.clientPanel.getGuiScale().getApplayValue(), 0.3F, 0.15F);
      }
   }

   private static float approach(float var0, float var1, float var2, float var3) {
      if (Float.isNaN(var0)) {
         return var1;
      } else {
         float var4 = MathHelper.lerp(var2, var0, var1);
         return Math.abs(var1 - var4) < var3 ? var1 : var4;
      }
   }

   public boolean isElementSwapBlurActive() {
      return this.prevType != null || this.guiModulePanel != null && this.guiModulePanel.isCategorySwitching();
   }

   public void setType(NLMenuScreen.ElementsType var1) {
      if (var1 != this.type) {
         if (this.prevType != null && !this.setTypeAnimation.isDone()) {
            float var2 = this.setTypeAnimation.getValue();
            NLMenuScreen.ElementsType var3 = this.type;
            if (this.prevType == var1) {
               this.prevType = var3;
               this.type = var1;
               this.setTypeAnimation.reset(1.0F - var2);
               this.setTypeAnimation.method2810(1.0F);
               return;
            }

            if (var2 < 0.5F) {
               var3.getPanelSupplier().get().close();
               this.type = var1;
               return;
            }

            this.prevType.getPanelSupplier().get().close();
         }

         this.setTypeAnimation.reset(0.0F);
         this.setTypeAnimation.method2810(1.0F);
         this.prevType = this.type;
         this.type = var1;
      }
   }

   public boolean isSearch() {
      return this.searchField != null && this.searchField.isSelected();
   }

   @Generated
   public float getContentDip() {
      return this.contentDip;
   }

   @Generated
   public ProfilePanel getProfilePanel() {
      return this.profilePanel;
   }

   @Generated
   public InspectorPanel getInspectorPanel() {
      return this.inspectorPanel;
   }

   @Generated
   public ModulePreviewStage getPreviewStage() {
      return this.previewStage;
   }

   @Generated
   public CrosshairEditorPanel getCrosshairEditor() {
      return this.crosshairEditor;
   }

   @Generated
   public GuiModulePanel getGuiModulePanel() {
      return this.guiModulePanel;
   }

   @Generated
   public ToolsSectionPanel getToolsSectionPanel() {
      return this.toolsSectionPanel;
   }

   @Generated
   public CloudSectionPanel getCloudSectionPanel() {
      return this.cloudSectionPanel;
   }

   @Generated
   public WardrobeSectionPanel getCosmeticsSectionPanel() {
      return this.cosmeticsSectionPanel;
   }

   @Generated
   public InterfaceSectionPanel getInterfaceSectionPanel() {
      return this.interfaceSectionPanel;
   }

   @Generated
   public QuickMenuPanel getQuickMenuPanel() {
      return this.quickMenuPanel;
   }

   @Generated
   public NLMenuScreen.ElementsType getType() {
      return this.type;
   }

   @Generated
   public boolean isClosing() {
      return this.closing;
   }

   @Generated
   public float getX() {
      return this.x;
   }

   @Generated
   public float getY() {
      return this.y;
   }

   public static enum ElementsType {
      CATEGORY("Category", "0", () -> Zenith.getInstance().getNLMenuScreen().guiModulePanel),
      TOOLS("Tools", "7", () -> Zenith.getInstance().getNLMenuScreen().toolsSectionPanel),
      CLOUD("Cloud", "8", () -> Zenith.getInstance().getNLMenuScreen().cloudSectionPanel),
      COSMETICS("Cosmetics", "9", () -> Zenith.getInstance().getNLMenuScreen().cosmeticsSectionPanel),
      INTERFACE("Interface", ":", () -> Zenith.getInstance().getNLMenuScreen().interfaceSectionPanel),
      SCRIPTS("Scripts & Configs", ";", () -> Zenith.getInstance().getNLMenuScreen().scriptsSectionPanel);

      public final String name;
      public final String icon;
      public final Supplier<ElementPanel> panelSupplier;

      @Generated
      private ElementsType(String var3, String var4, Supplier<ElementPanel> var5) {
         this.name = var3;
         this.icon = var4;
         this.panelSupplier = var5;
      }

      @Generated
      public String getName() {
         return this.name;
      }

      @Generated
      public String getIcon() {
         return this.icon;
      }

      @Generated
      public Supplier<ElementPanel> getPanelSupplier() {
         return this.panelSupplier;
      }
   }
}

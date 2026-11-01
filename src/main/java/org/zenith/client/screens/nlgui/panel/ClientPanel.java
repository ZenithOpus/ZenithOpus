package org.zenith.client.screens.nlgui.panel;

import com.google.gson.JsonObject;
import java.io.IOException;
import lombok.Generated;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.setting.GuiBooleanSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiButtonSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiNumberSetting;
import org.zenith.client.screens.nlgui.panel.api.Panel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.PlayTimeTracker;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class ClientPanel extends Panel {
   private final Animation animationVisible = new Animation(300L, 0.0F, Easing.INSTANCE13);
   private boolean expanded = false;
   private final GuiNumberSetting guiScale = new GuiNumberSetting(
      new SliderSetting("panel.client.setting.guiScale", 100.0F, 100.0F, 250.0F, 5.0F, "", "%", () -> true, (var0, var1) -> {}), 112.0F
   );
   private final GuiNumberSetting blurStrength = new GuiNumberSetting(
      new SliderSetting("panel.client.setting.backgroundBlur", 20.0F, 0.0F, 30.0F, 5.0F, "", "px"), 112.0F
   );
   private final GuiBooleanSetting renderDescription = new GuiBooleanSetting(new BooleanSetting("panel.client.setting.description", true), 112.0F);
   private final GuiBooleanSetting renderIcon = new GuiBooleanSetting(new BooleanSetting("panel.client.setting.settingIcon", true), 112.0F);
   private final GuiButtonSetting webProfile = new GuiButtonSetting(new ButtonSetting("panel.client.setting.telegram", "W", () -> {
      try {
         Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler https://t.me/zenithdlcdevlog");
      } catch (IOException var1) {
         var1.printStackTrace();
      }
   }), 112.0F);
   private final GuiButtonSetting openFolder = new GuiButtonSetting(new ButtonSetting("panel.client.setting.chat", "T", () -> {
      try {
         Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler https://t.me/+J-lV24z-Wmg1M2Y6");
      } catch (IOException var1) {
         var1.printStackTrace();
      }
   }), 112.0F);
   private BoundsData exitBounds;

   @Override
   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.animationVisible.method2807(this.expanded);
      this.animationVisible.setDuration(200L);
      var4 *= this.animationVisible.getValue();
      float var7 = 128.0F;
      float var8 = 227.0F;
      var1.enableScissor(var5 - GuiStyle.PADDING * 3, var6, var5 + var7 + GuiStyle.PADDING * 4, var6 + var8);
      var5 -= (var7 + GuiStyle.PADDING.intValue()) * (1.0F - this.animationVisible.getValue());
      if (Zenith.getInstance().getNLMenuScreen().getBlurPower() != 0.0F) {
         RenderShaders.method5287(
            MatrixUtils.method664(var1.getMatrices()),
            var5,
            var6,
            var7,
            var8,
            Zenith.getInstance().getNLMenuScreen().getBlurPower(),
            ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()),
            ZenithColor.INSTANCE.method7294(var4),
            true,
            false
         );
      }

      float var9 = 29.0F;
      float var10 = 45.0F;
      float var11 = 67.0F;
      float var12 = 56.0F;
      float var13 = 30.0F;
      ZenithStyle var14 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      this.renderHeader(var1, var2, var3, var4, var5, var6, var7, var9, var14);
      this.renderTill(var1, var2, var3, var4, var5, var6 + var9, var7, var10, var14);
      this.renderSettings(var1, var2, var3, var4, var5, var6 + var9 + var10, var7, var11, var14);
      this.renderInfo(var1, var2, var3, var4, var5, var6 + var9 + var10 + var11, var7, var12, var14);
      this.renderButton(var1, var2, var3, var4, var5, var6 + var9 + var10 + var11 + var12, var7, var13, var14);
      var1.disableScissor();
   }

   public void renderButton(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      var1.drawRoundedRect(
         var5, var6, var7, var8, ProxyDotRadiusData.method8077(GuiStyle.ROUND.intValue()), var9.getRightBackground().getZenithColor().method7294(var4)
      );
      float var10 = var5 + GuiStyle.PADDING * 2;
      float var11 = var6 + GuiStyle.PADDING * 2;
      float var12 = 112.0F;
      float var13 = 14.0F;
      Font var14 = Fonts.NEW_REGULAR.getFont(5.0F);
      Font var15 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var16 = Fonts.NEW_ICONS.getFont(5.0F);
      float var17 = var12 / 3.0F;
      var1.drawRoundedRect(
         var10 + var17, var11 + 1.0F, 0.5F, var13 - 2.0F, ProxyDotRadiusData.INSTANCE, var9.getTextTertiary().getZenithColor().method7294(var4)
      );
      var1.drawRoundedRect(
         var10 + var17 * 2.0F, var11 + 1.0F, 0.5F, var13 - 2.0F, ProxyDotRadiusData.INSTANCE, var9.getTextTertiary().getZenithColor().method7294(var4)
      );
      this.renderBox(var1, var14, var15, var16, "Version", "P", "3.5", var10, var11, var17, var13, var9, var4);
      this.renderBox(var1, var14, var15, var16, "Commit", "Q", "129", var10 + var17, var11, var17, var13, var9, var4);
      this.renderBox(
         var1,
         var14,
         var15,
         var16,
         "Build",
         "R",
         Zenith.getInstance().getEntry().method1504().getName(),
         var10 + var17 + var17,
         var11,
         var17,
         var13,
         var9,
         var4
      );
   }

   private void renderBox(
      MenuDrawContext var1,
      Font var2,
      Font var3,
      Font var4,
      String var5,
      String var6,
      String var7,
      float var8,
      float var9,
      float var10,
      float var11,
      ZenithStyle var12,
      float var13
   ) {
      var1.drawText(var2, var5, var8 + (var10 - var2.width(var5)) / 2.0F, var9, var12.getTextSecondary().method1447(var13));
      float var14;
      float var15 = (var14 = var4.width(var6)) + GuiStyle.PADDING.intValue() / 2.0F + var3.width(var7);
      var1.drawText(
         var4,
         var6,
         var8 + (var10 - var15) / 2.0F,
         var9 + var11 - var4.height() - GuiStyle.PADDING.intValue() / 2.0F,
         var12.getPrimaryColor().method1447(var13)
      );
      var1.drawText(
         var3,
         var7,
         var8 + (var10 - var15) / 2.0F + var14 + GuiStyle.PADDING.intValue() / 2.0F,
         var9 + var11 - var3.height() - GuiStyle.PADDING.intValue() / 2.0F,
         var12.getTextEnable().method1447(var13)
      );
   }

   public void renderHeader(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      var1.drawRoundedRect(
         var5, var6, var7, var8, ProxyDotRadiusData.method8075(GuiStyle.ROUND.intValue()), var9.getRightBackground().getZenithColor().method7294(var4)
      );
      float var10 = var5 + GuiStyle.PADDING * 2;
      float var11 = var6 + GuiStyle.PADDING * 2;
      Font var12 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var13 = Fonts.NEW_REGULAR.getFont(5.0F);
      Zenith.getInstance().getEntry();
      var1.drawText(var12, "Client", var10, var11 + 1.0F, var9.getTextEnable().getZenithColor().method7294(var4));
      var1.drawText(var13, "Global system settings", var10, var11 + var12.height() + 3.0F, var9.getTextTertiary().getZenithColor().method7294(var4));
      Font var15 = Fonts.NEW_ICONS.getFont(4.0F);
      float var16 = var5 + var7 - var15.width("2") - GuiStyle.PADDING * 2;
      float var17 = var11 + 2.0F;
      this.exitBounds = new BoundsData(var16, var17, 5.0F, 5.0F);
      var1.drawText(var15, "2", var16, var17, var9.getTextTertiary().getZenithColor().method7294(var4));
   }

   public void renderTill(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      var1.drawRoundedRect(var5, var6, var7, var8, ProxyDotRadiusData.INSTANCE, var9.getLeftBackground().getZenithColor().method7294(var4));
      float var10 = var5 + GuiStyle.PADDING * 2;
      float var11 = var6 + GuiStyle.PADDING * 2;
      Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var13 = Fonts.NEW_REGULAR.getFont(5.0F);
      var1.drawText(var13, "Navigation", var10, var11, var9.getTextSecondary().getZenithColor().method7294(var4));
      var11 += var13.height() + GuiStyle.PADDING.intValue();
      this.webProfile.render(var1, var2, var3, var10, var11, var4);
      var11 += this.webProfile.getAnimHeight() + GuiStyle.PADDING.intValue();
      this.openFolder.render(var1, var2, var3, var10, var11, var4);
   }

   public void renderSettings(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      var1.drawRoundedRect(var5, var6, var7, var8, ProxyDotRadiusData.INSTANCE, var9.getRightBackground().getZenithColor().method7294(var4));
      float var10 = var5 + GuiStyle.PADDING * 2;
      float var11 = var6 + GuiStyle.PADDING * 2;
      Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var13 = Fonts.NEW_REGULAR.getFont(5.0F);
      var1.drawText(var13, "Settings", var10, var11, var9.getTextSecondary().getZenithColor().method7294(var4));
      var11 += var13.height() + GuiStyle.PADDING * 2;
      this.guiScale.render(var1, var2, var3, var10, var11, var4);
      var11 += this.guiScale.getAnimHeight() + GuiStyle.PADDING.intValue();
      this.blurStrength.render(var1, var2, var3, var10, var11, var4);
      var11 += this.blurStrength.getAnimHeight() + GuiStyle.PADDING.intValue();
      this.renderDescription.render(var1, var2, var3, var10, var11, var4);
      var11 += this.renderDescription.getAnimHeight() + GuiStyle.PADDING.intValue();
      this.renderIcon.render(var1, var2, var3, var10, var11, var4);
   }

   public void renderInfo(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      var1.drawRoundedRect(var5, var6, var7, var8, ProxyDotRadiusData.INSTANCE, var9.getLeftBackground().getZenithColor().method7294(var4));
      float var10 = var5 + GuiStyle.PADDING * 2;
      float var11 = var6 + GuiStyle.PADDING * 2;
      float var12 = var7 - GuiStyle.PADDING * 4;
      Font var13 = Fonts.NEW_REGULAR.getFont(5.0F);
      Font var14 = Fonts.NEW_MEDIUM.getFont(5.5F);
      String var15 = Zenith.getInstance().getLocalization().translate("panel.client.playtime");
      var1.drawText(var13, var15, var10, var11, var9.getTextSecondary().getZenithColor().method7294(var4));
      var11 += var13.height() + GuiStyle.PADDING.intValue();
      PlayTimeTracker var16 = Zenith.getInstance().getPlayTimeTracker();
      float var17 = var14.height() + GuiStyle.PADDING * 2;
      this.renderTimeRow(
         var1,
         var14,
         var14,
         Zenith.getInstance().getLocalization().translate("panel.client.playtime.session"),
         PlayTimeTracker.method1402(var16.method1399()),
         var10,
         var11,
         var12,
         var9,
         var4
      );
      var11 += var17;
      this.renderTimeRow(
         var1,
         var14,
         var14,
         Zenith.getInstance().getLocalization().translate("panel.client.playtime.week"),
         PlayTimeTracker.method1402(var16.method1400()),
         var10,
         var11,
         var12,
         var9,
         var4
      );
      var11 += var17;
      this.renderTimeRow(
         var1,
         var14,
         var14,
         Zenith.getInstance().getLocalization().translate("panel.client.playtime.total"),
         PlayTimeTracker.method1402(var16.method1401()),
         var10,
         var11,
         var12,
         var9,
         var4
      );
   }

   private void renderTimeRow(
      MenuDrawContext var1, Font var2, Font var3, String var4, String var5, float var6, float var7, float var8, ZenithStyle var9, float var10
   ) {
      float var11 = GuiStyle.PADDING.intValue();
      float var12 = GuiStyle.PADDING.intValue() / 2.0F;
      float var13 = var3.width(var5) + var11 * 2.0F;
      float var14 = var3.height() + var12 * 2.0F;
      float var15 = var6 + var8 - var13;
      float var16 = var7 - var12;
      var1.drawRoundedRect(var15, var16, var13, var14, ProxyDotRadiusData.method8069(2.0F), GuiStyle.FIELD_SURFACE_BACKGROUND.method7294(var10));
      var1.drawRoundedBorder(var15, var16, var13, var14, 0.5F, ProxyDotRadiusData.method8069(2.0F), GuiStyle.FIELD_BORDER.method7294(var10));
      var1.drawText(var3, var5, var15 + var11, var7, var9.getPrimaryColor().getZenithColor().method7294(var10));
      var1.drawRoundedRect(
         var6, var7 + var2.height() / 2.0F - 0.6F, 3.0F, 3.0F, ProxyDotRadiusData.method8069(1.5F), var9.getPrimaryColor().getZenithColor().method7294(var10)
      );
      var1.drawText(var2, var4, var6 + 7.0F, var7, var9.getTextEnable().getZenithColor().method7294(var10));
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (var5.getIntValue() != 0) {
         return false;
      } else if (this.expanded && this.exitBounds != null && this.exitBounds.method8092(var1, var3, 2.0F)) {
         this.expanded = false;
         return true;
      } else if (this.renderDescription.onMouseClicked(var1, var3, var5)) {
         return true;
      } else if (this.renderIcon.onMouseClicked(var1, var3, var5)) {
         return true;
      } else if (this.webProfile.onMouseClicked(var1, var3, var5)) {
         return true;
      } else if (this.guiScale.onMouseClicked(var1, var3, var5)) {
         return true;
      } else {
         return this.blurStrength.onMouseClicked(var1, var3, var5) ? true : this.openFolder.onMouseClicked(var1, var3, var5);
      }
   }

   @Override
   public boolean onMouseReleased(double var1, double var3, Enum3 var5) {
      this.guiScale.onMouseReleased(var1, var3, var5);
      this.blurStrength.onMouseReleased(var1, var3, var5);
      return super.onMouseReleased(var1, var3, var5);
   }

   public void toggleExpanded() {
      this.expanded = !this.expanded;
   }

   public boolean isRender() {
      return this.animationVisible.getValue() > 0.0F || this.expanded;
   }

   public float getAnimationProgress() {
      return this.animationVisible.getValue();
   }

   public void safe(JsonObject var1) {
      var1.addProperty("expanded", this.expanded);
      this.guiScale.getSetting().safe(var1);
      this.blurStrength.getSetting().safe(var1);
      this.renderDescription.getSetting().safe(var1);
      this.renderIcon.getSetting().safe(var1);
   }

   public void load(JsonObject var1) {
      if (var1.has("expanded")) {
         this.expanded = var1.get("expanded").getAsBoolean();
      }

      if (var1.has(this.guiScale.getSetting().getKey())) {
         this.guiScale.getSetting().load(var1);
      }

      if (var1.has(this.blurStrength.getSetting().getKey())) {
         this.blurStrength.getSetting().load(var1);
      }

      if (var1.has(this.renderDescription.getSetting().getKey())) {
         this.renderDescription.getSetting().load(var1);
      }

      if (var1.has(this.renderIcon.getSetting().getKey())) {
         this.renderIcon.getSetting().load(var1);
      }
   }

   @Generated
   public Animation getAnimationVisible() {
      return this.animationVisible;
   }

   @Generated
   public boolean isExpanded() {
      return this.expanded;
   }

   @Generated
   public GuiNumberSetting getGuiScale() {
      return this.guiScale;
   }

   @Generated
   public GuiNumberSetting getBlurStrength() {
      return this.blurStrength;
   }

   @Generated
   public GuiBooleanSetting getRenderDescription() {
      return this.renderDescription;
   }

   @Generated
   public GuiBooleanSetting getRenderIcon() {
      return this.renderIcon;
   }

   @Generated
   public GuiButtonSetting getWebProfile() {
      return this.webProfile;
   }

   @Generated
   public GuiButtonSetting getOpenFolder() {
      return this.openFolder;
   }

   @Generated
   public BoundsData getExitBounds() {
      return this.exitBounds;
   }

   @Generated
   public void setExpanded(boolean var1) {
      this.expanded = var1;
   }
}

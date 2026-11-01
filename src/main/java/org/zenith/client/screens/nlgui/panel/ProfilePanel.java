package org.zenith.client.screens.nlgui.panel;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Locale;
import lombok.Generated;
import net.minecraft.util.Util;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.setting.GuiBooleanSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiButtonSetting;
import org.zenith.client.screens.nlgui.panel.api.Panel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.utility.api.Manager;
import org.zenith.utility.api.RegionUtil;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class ProfilePanel extends Panel {
   private final Animation animationVisible = new Animation(300L, 0.0F, Easing.INSTANCE13);
   private boolean expanded = false;
   private final GuiBooleanSetting steamMode = new GuiBooleanSetting(new BooleanSetting("panel.profile.setting.streamerMode", false), 112.0F);
   private final GuiBooleanSetting discordRPC = new GuiBooleanSetting(new BooleanSetting("panel.profile.setting.discordRpc", true), 112.0F);
   private final GuiButtonSetting webProfile = new GuiButtonSetting(
      new ButtonSetting("panel.profile.setting.webProfile", "J", () -> Util.getOperatingSystem().open(RegionUtil.method2038().get(0) + "/dashboard")), 112.0F
   );
   private final GuiButtonSetting openFolder = new GuiButtonSetting(new ButtonSetting("panel.profile.setting.openFolder", "K", () -> {
      try {
         Runtime.getRuntime().exec("explorer " + Manager.file.getAbsolutePath());
      } catch (IOException var1) {
      }
   }), 112.0F);
   private final Animation languageLeftHoverAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private final Animation languageRightHoverAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private final Animation languageSwitchAnimation = new Animation(120L, 1.0F, Easing.INSTANCE12);
   private BoundsData languageLeftBounds;
   private BoundsData languageRightBounds;
   private BoundsData exitBounds;
   private String displayedLanguageName;
   private String outgoingLanguageName;
   private int languageSwitchDirection = 1;
   private boolean languageSwitchActive;

   @Override
   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.animationVisible.method2807(this.expanded);
      this.animationVisible.setDuration(200L);
      var4 *= this.animationVisible.getValue();
      float var7 = 128.0F;
      float var8 = 190.0F;
      var1.enableScissor(var5, var6, var5 + var7 + GuiStyle.PADDING * 4, var6 + var8);
      var5 += (var7 + GuiStyle.PADDING.intValue()) * (1.0F - this.animationVisible.getValue());
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
      float var10 = 27.0F;
      float var11 = 56.0F;
      float var12 = 45.0F;
      float var13 = 33.0F;
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
      float var12 = var7 - GuiStyle.PADDING * 4;
      float var13 = var8 - GuiStyle.PADDING * 4;
      var1.drawRoundedRect(
         var10, var11, var12, var13, ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F), new ZenithColor("#FF97A0").method7294(var4)
      );
      Font var14 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var15 = Fonts.NEW_ICONS.getFont(5.0F);
      float var16 = var15.width("I");
      float var17 = var14.width("Русская рулетка");
      float var18 = var16 + GuiStyle.PADDING.intValue() + var17;
      float var19 = var10 + (var12 - var18) / 2.0F;
      var1.drawText(var15, "I", var19, var11 + (var13 - var15.height()) / 2.0F, var9.getTextEnable().method1447(var4));
      var1.drawText(
         var14, "Русская рулетка", var19 + var16 + GuiStyle.PADDING.intValue(), var11 + (var13 - var14.height()) / 2.0F, var9.getTextEnable().method1447(var4)
      );
   }

   public void renderHeader(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      var1.drawRoundedRect(
         var5, var6, var7, var8, ProxyDotRadiusData.method8075(GuiStyle.ROUND.intValue()), var9.getRightBackground().getZenithColor().method7294(var4)
      );
      float var10 = var5 + GuiStyle.PADDING * 2;
      float var11 = var6 + GuiStyle.PADDING * 2;
      float var12 = 13.0F;
      RenderShaders.method5294(
         MatrixUtils.method664(var1.getMatrices()),
         Zenith.method1501("icons/avatar.png"),
         var10,
         var11,
         var12,
         var12,
         ProxyDotRadiusData.method8069(4.0F),
         ZenithColor.INSTANCE.method7294(var4)
      );
      Font var13 = Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var14 = Fonts.NEW_REGULAR.getFont(5.0F);
      float var15 = var10 + var12 + GuiStyle.PADDING.intValue();
      Zenith.Entry var16 = Zenith.getInstance().getEntry();
      var1.drawText(var13, var16.getUsername(), var15, var11 + 1.0F, var9.getTextEnable().getZenithColor().method7294(var4));
      var1.drawText(var14, "uid: " + var16.getText2(), var15, var11 + var12 - var14.height() - 1.0F, var9.getTextTertiary().getZenithColor().method7294(var4));
      Font var17 = Fonts.NEW_ICONS.getFont(4.0F);
      this.exitBounds = new BoundsData(var5 + var7 - var17.width("2") - GuiStyle.PADDING * 2, var11 + 2.0F, 5.0F, 5.0F);
      var1.drawText(var17, "2", var5 + var7 - var17.width("2") - GuiStyle.PADDING * 2, var11 + 2.0F, var9.getTextTertiary().getZenithColor().method7294(var4));
   }

   public void renderTill(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      var1.drawRoundedRect(var5, var6, var7, var8, ProxyDotRadiusData.INSTANCE, var9.getLeftBackground().getZenithColor().method7294(var4));
      float var10 = var5 + GuiStyle.PADDING * 2;
      float var11 = var6 + GuiStyle.PADDING * 2;
      Font var12 = Fonts.NEW_MEDIUM.getFont(4.8F);
      Zenith.Entry var13 = Zenith.getInstance().getEntry();
      float var14 = 1.4F;
      long var15;
      float var17;
      String var18;
      if ((var15 = var13.method1506()) == Long.MAX_VALUE) {
         var17 = 1.0F;
         var18 = Zenith.getInstance().getLocalization().translate("gui.sub.lifetime");
      } else if (var15 == -1L) {
         var17 = 0.0F;
         var18 = Zenith.getInstance().getLocalization().translate("gui.sub.active");
      } else {
         var17 = Math.min(1.0F, Math.max(0.0F, (float)var15 / 365.0F));
         var18 = var15 + " day";
      }

      var1.drawText(var12, "Active: " + var18, var10, var11, var9.getTextEnable().getZenithColor().method7294(var4));
      Font var19 = Fonts.NEW_ICONS.getFont(5.0F);
      float var20 = var19.width("G");
      var1.drawText(
         var12,
         "Extend",
         var5 + var7 - GuiStyle.PADDING * 2 - var20 - var12.width("Extend") - GuiStyle.PADDING.intValue(),
         var11,
         var9.getPrimaryColor().getZenithColor().method7294(var4)
      );
      var1.drawText(var19, "G", var5 + var7 - GuiStyle.PADDING * 2 - var20, var11, var9.getPrimaryColor().getZenithColor().method7294(var4));
      float var21 = var6 + var8 - GuiStyle.PADDING * 2 - var14;
      float var22;
      float var23 = (var22 = var7 - GuiStyle.PADDING * 4) * var17;
      var1.drawRoundedRectBatched(
         var10, var21, var22, var14, ProxyDotRadiusData.method8069(0.01F), var9.getFieldSurfaceBackground().getZenithColor().method7294(var4)
      );
      var1.drawRoundedRectBatched(var10, var21, var23, var14, ProxyDotRadiusData.method8069(0.01F), var9.getPrimaryColor().getZenithColor().method7294(var4));
      var1.flushRoundedRects();
   }

   public void renderSettings(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      var1.drawRoundedRect(var5, var6, var7, var8, ProxyDotRadiusData.INSTANCE, var9.getRightBackground().getZenithColor().method7294(var4));
      float var10 = var5 + GuiStyle.PADDING * 2;
      float var11 = var6 + GuiStyle.PADDING * 2;
      Font var12 = Fonts.NEW_MEDIUM.getFont(5.01F);
      Font var13 = Fonts.NEW_REGULAR.getFont(5.01F);
      var1.drawText(var13, "Settings", var10, var11, var9.getTextSecondary().getZenithColor().method7294(var4));
      var11 += var13.height() + GuiStyle.PADDING.intValue();
      boolean var14 = Zenith.getInstance().getNLMenuScreen().isRenderIcon();
      Zenith.getInstance().getNLMenuScreen().setRenderIcon(false);
      this.steamMode.render(var1, var2, var3, var10, var11, var4);
      float var15 = var11 + (this.steamMode.getAnimHeight() + GuiStyle.PADDING.intValue());
      this.discordRPC.render(var1, var2, var3, var10, var15, var4);
      Zenith.getInstance().getNLMenuScreen().setRenderIcon(var14);
      float var16 = this.steamMode.getHeight();
      float var17 = var15 + this.discordRPC.getAnimHeight() + GuiStyle.PADDING.intValue();
      String var18 = "A";
      float var19 = GuiStyle.PADDING.intValue();
      float var20 = 6.0F;
      float var21 = 2.0F;
      Font var22 = Fonts.ICONS.getFont(5.01F);
      float var23 = var22.width(var18);
      float var24 = this.getLanguageBlockWidth(var13, var20, var21);
      float var26;
      float var27;
      float var28 = (var27 = (var26 = var5 + var7 - GuiStyle.PADDING.intValue() * 2.0F - var23) - var19 - var24) - var19 - var23;
      float var29 = var17 + (var16 - var12.height()) / 2.0F;
      float var30;
      float var31 = (var30 = var17 + (var16 - var22.height()) / 2.0F) + 0.9F;
      float var32 = var17 + 1.4F;
      float var33 = var27 + var20 + var21;
      float var34 = var17 + (var16 - var13.height()) / 2.0F;
      float var35 = 2.0F;
      this.languageLeftBounds = new BoundsData(var28 - var35, var17 - var35, var23 + var35 * 2.0F, var16 + var35 * 2.0F);
      this.languageRightBounds = new BoundsData(var26 - var35, var17 - var35, var23 + var35 * 2.0F, var16 + var35 * 2.0F);
      boolean var36 = this.languageLeftBounds.contains(var2, var3);
      boolean var37 = this.languageRightBounds.contains(var2, var3);
      this.languageLeftHoverAnimation.method2808(var36 ? 1.0F : 0.0F);
      this.languageRightHoverAnimation.method2808(var37 ? 1.0F : 0.0F);
      String var40 = Zenith.getInstance().getLocalization().getLanguageName();
      this.syncDisplayedLanguage(var40);
      var1.drawText(var12, "Language", var10, var29, var9.getTextEnable().getZenithColor().method7294(var4));
      this.drawLanguageArrow(var1, var22, var18, var28, var31, var9.getPrimaryColor().getZenithColor().method7294(var4), 180.0F);
      if (this.languageSwitchActive) {
         float var41 = this.clamp01(this.languageSwitchAnimation.update());
         float var42 = Math.max(8.0F, var24 + var19);
         float var43 = -this.languageSwitchDirection * var41 * var42;
         float var44 = this.languageSwitchDirection * (1.0F - var41) * var42;
         var1.enableScissor(var27 - 1.0F, var17 - var35, var27 + var24 + 1.0F, var17 + var16 + var35);
         this.drawLanguageValue(var1, var13, this.outgoingLanguageName, var27 + var43, var32, var33 + var43, var34, var20, var4, var9);
         this.drawLanguageValue(var1, var13, this.displayedLanguageName, var27 + var44, var32, var33 + var44, var34, var20, var4, var9);
         var1.disableScissor();
         if (this.languageSwitchAnimation.isDone() || var41 >= 0.999F) {
            this.languageSwitchActive = false;
            this.outgoingLanguageName = null;
         }
      } else {
         this.drawLanguageValue(var1, var13, this.displayedLanguageName, var27, var32, var33, var34, var20, var4, var9);
      }

      this.drawLanguageArrow(var1, var22, var18, var26, var30, var9.getPrimaryColor().getZenithColor().method7294(var4), 0.0F);
   }

   private void drawLanguageArrow(MenuDrawContext var1, Font var2, String var3, float var4, float var5, ZenithColor var6, float var7) {
      float var8 = var4 + var2.width(var3) / 2.0F;
      float var9 = var5 + var2.height() / 2.0F;
      var1.pushMatrix();
      var1.getMatrices().translate(var8, var9);
      var1.getMatrices().rotate((float)Math.toRadians(var7));
      var1.getMatrices().translate(-var8, -var9);
      var1.drawText(var2, var3, var4, var5, var6);
      var1.popMatrix();
   }

   private void drawLanguageValue(
      MenuDrawContext var1, Font var2, String var3, float var4, float var5, float var6, float var7, float var8, float var9, ZenithStyle var10
   ) {
      String var11 = this.normalizeLanguageName(var3);
      RenderShaders.method5294(
         MatrixUtils.method664(var1.getMatrices()),
         Zenith.method1501(this.getLanguageFlagPath(var11)),
         var4,
         var5,
         var8,
         5.5F,
         ProxyDotRadiusData.method8069(1.0F),
         ZenithColor.INSTANCE.method7294(var9)
      );
      var1.drawText(var2, var11.toUpperCase(Locale.ENGLISH), var6, var7, var10.getTextEnable().getZenithColor().method7294(var9));
   }

   private float getLanguageBlockWidth(Font var1, float var2, float var3) {
      float var4 = Math.max(Math.max(var1.width("RU"), var1.width("EN")), Math.max(var1.width("PL"), var1.width("TR")));
      return var2 + var3 + var4;
   }

   private String getLanguageFlagPath(String var1) {
      String var2 = this.normalizeLanguageName(var1);

      return switch (var2) {
         case "ru" -> "icons/russia.png";
         case "pl" -> "icons/poland.png";
         case "tr" -> "icons/turkey.png";
         default -> "icons/usa.png";
      };
   }

   private void syncDisplayedLanguage(String var1) {
      if (this.displayedLanguageName == null) {
         this.displayedLanguageName = var1;
      } else {
         if (!this.languageSwitchActive && !this.sameLanguage(this.displayedLanguageName, var1)) {
            this.displayedLanguageName = var1;
         }
      }
   }

   private void switchLanguage(boolean var1) {
      String var2 = Zenith.getInstance().getLocalization().getLanguageName();
      Zenith.getInstance().getLocalization().method1730(var1);
      String var3 = Zenith.getInstance().getLocalization().getLanguageName();
      if (!this.sameLanguage(var2, var3)) {
         this.outgoingLanguageName = this.displayedLanguageName == null ? var2 : this.displayedLanguageName;
         this.displayedLanguageName = var3;
         this.languageSwitchDirection = var1 ? 1 : -1;
         this.languageSwitchActive = true;
         this.languageSwitchAnimation.reset(0.0F);
         this.languageSwitchAnimation.method2810(1.0F);
      }
   }

   private boolean sameLanguage(String var1, String var2) {
      return this.normalizeLanguageName(var1).equals(this.normalizeLanguageName(var2));
   }

   private String normalizeLanguageName(String var1) {
      return var1 != null && !var1.isBlank() ? var1.trim().toLowerCase(Locale.ENGLISH) : "ru";
   }

   private float clamp01(float var1) {
      return Math.max(0.0F, Math.min(1.0F, var1));
   }

   public void renderInfo(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, ZenithStyle var9) {
      var1.drawRoundedRect(var5, var6, var7, var8, ProxyDotRadiusData.INSTANCE, var9.getLeftBackground().getZenithColor().method7294(var4));
      float var10 = var5 + GuiStyle.PADDING * 2;
      float var11 = var6 + GuiStyle.PADDING * 2;
      Fonts.NEW_MEDIUM.getFont(5.0F);
      Font var13 = Fonts.NEW_REGULAR.getFont(5.0F);
      var1.drawText(var13, "Info", var10, var11, var9.getTextSecondary().getZenithColor().method7294(var4));
      var11 += var13.height() + GuiStyle.PADDING.intValue();
      this.webProfile.render(var1, var2, var3, var10, var11, var4);
      var11 += this.webProfile.getAnimHeight() + GuiStyle.PADDING.intValue();
      this.openFolder.render(var1, var2, var3, var10, var11, var4);
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (var5.getIntValue() != 0 || !this.expanded) {
         return false;
      } else if (this.steamMode.onMouseClicked(var1, var3, var5)) {
         return true;
      } else if (this.discordRPC.onMouseClicked(var1, var3, var5)) {
         return true;
      } else if (this.webProfile.onMouseClicked(var1, var3, var5)) {
         return true;
      } else if (this.openFolder.onMouseClicked(var1, var3, var5)) {
         return true;
      } else {
         if (this.exitBounds != null && this.exitBounds.method8092(var1, var3, 2.0F)) {
            this.expanded = false;
         }

         if (this.languageLeftBounds != null && this.languageLeftBounds.contains(var1, var3)) {
            this.switchLanguage(false);
            return true;
         } else if (this.languageRightBounds != null && this.languageRightBounds.contains(var1, var3)) {
            this.switchLanguage(true);
            return true;
         } else {
            return false;
         }
      }
   }

   public void setExpanded(boolean var1) {
      this.expanded = var1;
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
      var1.addProperty("language", Zenith.getInstance().getLocalization().getText());
      this.steamMode.getSetting().safe(var1);
      this.discordRPC.getSetting().safe(var1);
   }

   public void load(JsonObject var1) {
      if (var1.has("expanded")) {
         this.expanded = var1.get("expanded").getAsBoolean();
      }

      if (var1.has("language")) {
         Zenith.getInstance().getLocalization().method1728(var1.get("language").getAsString());
      }

      if (var1.has(this.steamMode.getSetting().getKey())) {
         this.steamMode.getSetting().load(var1);
      }

      if (var1.has(this.discordRPC.getSetting().getKey())) {
         this.discordRPC.getSetting().load(var1);
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
   public GuiBooleanSetting getSteamMode() {
      return this.steamMode;
   }

   @Generated
   public GuiBooleanSetting getDiscordRPC() {
      return this.discordRPC;
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
   public Animation getLanguageLeftHoverAnimation() {
      return this.languageLeftHoverAnimation;
   }

   @Generated
   public Animation getLanguageRightHoverAnimation() {
      return this.languageRightHoverAnimation;
   }

   @Generated
   public Animation getLanguageSwitchAnimation() {
      return this.languageSwitchAnimation;
   }

   @Generated
   public BoundsData getLanguageLeftBounds() {
      return this.languageLeftBounds;
   }

   @Generated
   public BoundsData getLanguageRightBounds() {
      return this.languageRightBounds;
   }

   @Generated
   public BoundsData getExitBounds() {
      return this.exitBounds;
   }

   @Generated
   public String getDisplayedLanguageName() {
      return this.displayedLanguageName;
   }

   @Generated
   public String getOutgoingLanguageName() {
      return this.outgoingLanguageName;
   }

   @Generated
   public int getLanguageSwitchDirection() {
      return this.languageSwitchDirection;
   }

   @Generated
   public boolean isLanguageSwitchActive() {
      return this.languageSwitchActive;
   }
}

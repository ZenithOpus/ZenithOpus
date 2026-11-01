package org.zenith.client.screens.nlgui.panel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.ModuleCategory;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.panel.api.Panel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.config.CloudAvatarCache;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.Localization;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class CategoryPanel extends Panel {
   private static final float ROW_HEIGHT = 15.0F;
   private static final float TEXT_INSET = 8.0F;
   private static final float MARKER_WIDTH = 2.0F;
   private static final float CARD_HEIGHT = 26.0F;
   private static final float CARD_INSET = 6.0F;
   private static final float CARD_PADDING = 5.0F;
   private static final float BOTTOM_BLOCK_HEIGHT = 38.0F;
   private static final float AVATAR_SIZE = 16.0F;
   private static final float STATUS_DOT = 4.0F;
   private static final float CHIP_HEIGHT = 6.5F;
   private static final ZenithColor DOT_RING = new ZenithColor("#121214");
   private static final float ICON_COLUMN = 10.0F;
   private static final float MAX_ROW_DELAY = 0.4F;
   private static final String BRAND = HoverInfo.spaced("ZENITH");
   private static final int[] ENABLED_BY_CATEGORY = new int[ModuleCategory.values().length];
   private final List<CategoryPanel.Row> rows = new ArrayList<>();
   private long daysLeftFor = Long.MIN_VALUE;
   private long daysLeftUntil;
   private long daysLeftValue;
   private String statusText = "";
   private String statusLanguage;
   private final Animation profileHover = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private final Animation profileSelect = new Animation(160L, 0.0F, Easing.INSTANCE12);
   private BoundsData profileBounds;

   public CategoryPanel() {
      for (ModuleCategory var4 : ModuleCategory.values()) {
         if (var4 != ModuleCategory.THEMES) {
            this.rows.add(new CategoryPanel.Row(var4.getName(), MenuGlyph.forCategory(var4), new CategoryPanel.CategoryTarget(var4)));
         }
      }

      for (NLMenuScreen.ElementsType var8 : NLMenuScreen.ElementsType.values()) {
         if (var8 != NLMenuScreen.ElementsType.CATEGORY) {
            this.rows.add(new CategoryPanel.Row(var8.getName(), MenuGlyph.forSection(var8), new CategoryPanel.SectionTarget(var8)));
         }
      }
   }

   @Override
   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.render(var1, var2, var3, var4, var5, var6, 1.0F);
   }

   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      ZenithStyle var8 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var8 != null) {
         if (!(var4 <= 0.01F)) {
            float var20 = 100.0F;
            float var21 = NLMenuScreen.heightPanel;
            Font var11 = Fonts.NEW_MEDIUM.getFont(4.6F);
            var1.drawText(var11, BRAND, var5 + 8.0F, var6 + (23.0F - var11.height()) / 2.0F, var8.getTextTertiary().getZenithColor().method7294(var4));
            float var12 = var6 + 23.0F + GuiStyle.PADDING.intValue();
            int var13 = 0;

            for (int var14 = 0; var14 < this.rows.size(); var14++) {
               if (this.rows.get(var14).target instanceof CategoryPanel.CategoryTarget) {
                  var13 = var14;
               }
            }

            float var22 = GuiStyle.PADDING.intValue() * 2.0F + 1.0F;
            float var15 = var21 - 23.0F - GuiStyle.PADDING.intValue() - var22 - 38.0F;
            float var16 = Math.min(15.0F, var15 / this.rows.size());
            countEnabled();

            for (int var17 = 0; var17 < this.rows.size(); var17++) {
               CategoryPanel.Row var18 = this.rows.get(var17);
               float var19;
               if ((var19 = rowEntrance(var7, var17)) > 0.001F) {
                  var18.render(var1, var2, var3, var5 - (1.0F - var19) * 7.0F, var12, var20, var16, var4 * var19);
               } else {
                  var18.bounds = null;
               }

               var12 += var16;
               if (var17 == var13) {
                  var1.drawRect(var5 + 8.0F, var12 + GuiStyle.PADDING.intValue(), var20 - 16.0F, 1.0F, var8.getFieldBorder().getZenithColor().method7294(var4));
                  var12 += var22;
               }
            }

            float var23;
            if ((var23 = rowEntrance(var7, this.rows.size())) > 0.001F) {
               this.renderProfileCard(var1, var2, var3, var5, var6 + var21, var20, var4 * var23);
            } else {
               this.profileBounds = null;
            }
         } else {
            this.profileBounds = null;

            for (CategoryPanel.Row var10 : this.rows) {
               var10.bounds = null;
            }
         }
      }
   }

   private static float rowEntrance(float var0, int var1) {
      if (var0 >= 1.0F) {
         return 1.0F;
      } else {
         float var2 = Math.min(var1 * 0.045F, 0.4F);
         return MathHelper.clamp((var0 - var2) / 0.6F, 0.0F, 1.0F);
      }
   }

   private void renderProfileCard(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      ZenithStyle var8 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var8 != null) {
         float var9 = var4 + 6.0F;
         float var10 = var6 - 12.0F;
         float var11 = var5 - 6.0F - 26.0F;
         ProxyDotRadiusData var12 = ProxyDotRadiusData.method8069(GuiStyle.HUD_ROUND.intValue());
         this.profileBounds = new BoundsData(var9, var11, var10, 26.0F);
         boolean var13 = this.profileBounds.contains(var2, var3);
         boolean var14 = Zenith.getInstance().getNLMenuScreen().getQuickMenuPanel() != null
            && Zenith.getInstance().getNLMenuScreen().getQuickMenuPanel().isExpanded();
         float var15 = this.profileHover.method2808(var13 ? 1.0F : 0.0F);
         float var16 = this.profileSelect.method2808(var14 ? 1.0F : 0.0F);
         var1.drawRoundedRect(
            var9, var11, var10, 26.0F, var12, var8.getFieldSurfaceBackground().getZenithColor().method7294(var7 * (2.5F + var15 * 2.0F + var16 * 1.5F))
         );
         ZenithColor var17 = var8.getFieldBorder()
            .getZenithColor()
            .method7294(1.6F + var15 * 1.4F)
            .method7295(var8.getPrimaryColor().getZenithColor().method7293(110), var16);
         var1.drawRoundedBorder(var9, var11, var10, 26.0F, 0.15F, var12, var17.method7294(var7));
         Zenith.Entry var18 = Zenith.getInstance().getEntry();
         String var19 = var18.getUsername() != null && !var18.getUsername().isBlank() ? var18.getUsername() : "guest";
         Zenith.Entry.Type var20 = var18.method1504();
         String var21 = var20 == null ? "user" : var20.getName().toLowerCase(Locale.ROOT);
         long var22;
         boolean var24 = (var22 = this.daysLeft(var18)) == Long.MAX_VALUE;
         boolean var25 = var22 == -1L;
         boolean var26;
         boolean var27 = !(var26 = var22 == 0L) && !var24 && !var25 && var22 <= 365L;
         float var28 = var9 + 5.0F;
         float var29 = var11 + 5.0F;
         this.drawAvatar(var1, var18.getText2(), var19, var28, var29, 16.0F, var7, var8);
         float var30 = var28 + 16.0F - 4.0F + 0.5F;
         float var31 = var29 + 16.0F - 4.0F + 0.5F;
         var1.drawRoundedRect(var30 - 1.0F, var31 - 1.0F, 6.0F, 6.0F, ProxyDotRadiusData.method8069(3.0F), DOT_RING.method7294(var7));
         var1.drawRoundedRect(
            var30,
            var31,
            4.0F,
            4.0F,
            ProxyDotRadiusData.method8069(2.0F),
            (var26 ? var8.getHeartIcon() : var8.getFriendColor()).getZenithColor().method7293(255).method7294(var7)
         );
         Font var32 = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font var33 = Fonts.NEW_REGULAR.getFont(4.2F);
         Font var34 = Fonts.NEW_MEDIUM.getFont(3.4F);
         float var35 = var28 + 16.0F + 5.0F;
         float var36 = var9 + var10 - 5.0F;
         float var37 = var32.height() + 2.0F + 6.5F;
         float var38;
         float var39 = (var38 = var29 + (16.0F - var37) / 2.0F) + var32.height() + 2.0F;
         var1.drawText(var32, var32.clip(var19, var36 - var35), var35, var38, var8.getTextEnable().getZenithColor().method7294(var7));
         String var40 = var21.toUpperCase(Locale.ROOT);
         float var41 = Math.min(var34.width(var40) + 5.0F, var36 - var35);
         boolean var42;
         ZenithColor var43 = (var42 = var20 == null || var20 == Zenith.Entry.Type.USER)
            ? var8.getTextSecondary().getZenithColor()
            : var8.getPrimaryColor().getZenithColor();
         var1.drawRoundedRect(var35, var39, var41, 6.5F, ProxyDotRadiusData.method8069(3.25F), var43.method7293(var42 ? 20 : 38).method7294(var7));
         var1.drawText(
            var34,
            var34.clip(var40, var41 - 4.0F),
            var35 + (var41 - var34.width(var34.clip(var40, var41 - 4.0F))) / 2.0F,
            var39 + (6.5F - var34.height()) / 2.0F,
            var43.method7294(var7)
         );
         String var44 = this.statusText;
         ZenithColor var45 = var26
            ? var8.getHeartIcon().getZenithColor()
            : (var27 ? var8.getPrimaryColor().getZenithColor() : var8.getTextTertiary().getZenithColor());
         float var46 = var35 + var41 + 3.0F;
         var1.drawText(var33, var33.clip(var44, var36 - var46), var46, var39 + (6.5F - var33.height()) / 2.0F, var45.method7294(var7));
         if (var27) {
            float var47 = var11 + 26.0F - 3.0F;
            float var48 = var36 - var35;
            var1.drawRoundedRectBatched(
               var35, var47, var48, 1.0F, ProxyDotRadiusData.method8069(0.5F), var8.getFieldSurfaceBackground().getZenithColor().method7294(var7 * 3.0F)
            );
            var1.drawRoundedRectBatched(
               var35,
               var47,
               var48 * ((float)var22 / 365.0F),
               1.0F,
               ProxyDotRadiusData.method8069(0.5F),
               var8.getPrimaryColor().getZenithColor().method7294(var7)
            );
            var1.flushRoundedRects();
         }

         if (var13) {
            HoverInfo.request(
               var19, Zenith.getInstance().getLocalization().translate("gui.profileCard.desc").replace("{uid}", var18.getText2()).replace("{role}", var21)
            );
         }
      }
   }

   private void drawAvatar(MenuDrawContext var1, String var2, String var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      Identifier var9 = CloudAvatarCache.get(var2);
      if (var9 != null) {
         var1.drawRoundedTexture(var9, var4, var5, var6, var6, ProxyDotRadiusData.method8069(var6 / 2.0F), ZenithColor.INSTANCE.method7294(var7));
      } else {
         var1.drawRoundedRectBatched(
            var4, var5, var6, var6, ProxyDotRadiusData.method8069(var6 / 2.0F), var8.getPrimaryColor().getZenithColor().method7294(var7 * 0.3F)
         );
         var1.flushRoundedRects();
         Font var10 = Fonts.NEW_MEDIUM.getFont(var6 * 0.42F);
         String var11 = var3.substring(0, 1).toUpperCase(Locale.ROOT);
         var1.drawText(
            var10,
            var11,
            var4 + (var6 - var10.width(var11)) / 2.0F,
            var5 + (var6 - var10.height()) / 2.0F,
            var8.getTextEnable().getZenithColor().method7294(var7)
         );
      }
   }

   private long daysLeft(Zenith.Entry var1) {
      long var2 = System.currentTimeMillis();
      long var4 = var1.method1510();
      String var6 = Zenith.getInstance().getLocalization().translate("gui.sub.till");
      if (var2 >= this.daysLeftUntil || var4 != this.daysLeftFor || !var6.equals(this.statusLanguage)) {
         ZoneId var7 = ZoneId.systemDefault();
         LocalDate var8 = LocalDate.now(var7);
         this.daysLeftValue = var1.method1506();
         this.daysLeftFor = var4;
         this.daysLeftUntil = var8.plusDays(1L).atStartOfDay(var7).toInstant().toEpochMilli();
         this.statusLanguage = var6;
         this.statusText = statusText(var1, this.daysLeftValue);
      }

      return this.daysLeftValue;
   }

   private static String statusText(Zenith.Entry var0, long var1) {
      Localization var3 = Zenith.getInstance().getLocalization();
      if (var1 == Long.MAX_VALUE) {
         return var3.translate("gui.sub.lifetime");
      } else if (var1 == -1L) {
         return var3.translate("gui.sub.active");
      } else if (var1 == 0L) {
         return var3.translate("gui.sub.expired");
      } else {
         return var1 <= 365L ? var1 + " " + var3.translate("gui.sub.daysShort") : var3.translate("gui.sub.till").replace("{date}", var0.method1509());
      }
   }

   private static void countEnabled() {
      Arrays.fill(ENABLED_BY_CATEGORY, 0);

      for (Module var1 : Zenith.getInstance().getModuleManager().getItems()) {
         if (!var1.isHidden() && var1.isEnabled() && var1.getCategory() != null) {
            ENABLED_BY_CATEGORY[var1.getCategory().ordinal()]++;
         }
      }
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.profileBounds != null && this.profileBounds.contains(var1, var3)) {
         NLMenuScreen var8 = Zenith.getInstance().getNLMenuScreen();
         if (var8.getQuickMenuPanel() != null) {
            var8.getQuickMenuPanel().toggleExpanded();
         }

         Zenith.getInstance().getClientSounds().method1570(false);
         return true;
      } else {
         for (CategoryPanel.Row var7 : this.rows) {
            if (var7.onMouseClicked(var1, var3)) {
               return true;
            }
         }

         return false;
      }
   }

   private static final class CategoryTarget implements CategoryPanel.Target {
      private final ModuleCategory category;

      public ModuleCategory category() {
         return this.category;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof CategoryTarget other)) return false;
         return java.util.Objects.equals(this.category, other.category);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(category);
      }

      @Override
      public String toString() {
         return "CategoryTarget[" + "category=" + this.category + "]";
      }

      private CategoryTarget(ModuleCategory var1) {
         this.category = var1;
      }

      @Override
      public boolean isSelected() {
         NLMenuScreen var1 = Zenith.getInstance().getNLMenuScreen();
         return var1.getType() == NLMenuScreen.ElementsType.CATEGORY && var1.getGuiModulePanel().getCurrentCategory() == this.category;
      }

      @Override
      public void activate() {
         NLMenuScreen var1 = Zenith.getInstance().getNLMenuScreen();
         var1.resetSearch();
         var1.getGuiModulePanel().setCategory(this.category);
         var1.setType(NLMenuScreen.ElementsType.CATEGORY);
      }

      @Override
      public String counter() {
         return String.valueOf(CategoryPanel.ENABLED_BY_CATEGORY[this.category.ordinal()]);
      }
   }

   private static final class Row {
      private final String label;
      private final MenuGlyph icon;
      private final CategoryPanel.Target target;
      private final Animation selection = new Animation(140L, 0.0F, Easing.INSTANCE12);
      private final Animation hover = new Animation(120L, 0.0F, Easing.INSTANCE12);
      private BoundsData bounds;

      private Row(String var1, MenuGlyph var2, CategoryPanel.Target var3) {
         this.label = var1;
         this.icon = var2;
         this.target = var3;
      }

      private void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8) {
         ZenithStyle var9 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var9 != null) {
            this.bounds = new BoundsData(var4, var5, var6, var7);
            boolean var10 = this.target.isSelected();
            boolean var11 = this.bounds.contains(var2, var3);
            float var12 = this.selection.method2808(var10 ? 1.0F : 0.0F);
            float var13 = this.hover.method2808(var11 ? 1.0F : 0.0F);
            if (var12 > 0.01F) {
               var1.drawRect(var4, var5 + 2.0F, 2.0F, var7 - 4.0F, var9.getPrimaryColor().getZenithColor().method7294(var8 * var12));
            }

            Font var14 = Fonts.NEW_MEDIUM.getFont(5.5F);
            ZenithColor var15 = var9.getTextSecondary()
               .getZenithColor()
               .method7295(var9.getTextEnable().getZenithColor(), Math.max(var12, var13))
               .method7294(var8);
            this.icon.draw(var1, var4 + 8.0F, var5 + (var7 - 6.0F) / 2.0F, var15);
            var1.drawText(var14, this.label, var4 + 8.0F + 10.0F, var5 + (var7 - var14.height()) / 2.0F, var15);
            String var16 = this.target.counter();
            if (var16 != null) {
               Font var17 = Fonts.NEW_REGULAR.getFont(5.0F);
               var1.drawText(
                  var17,
                  var16,
                  var4 + var6 - 8.0F - var17.width(var16),
                  var5 + (var7 - var17.height()) / 2.0F,
                  var9.getTextTertiary().getZenithColor().method7294(var8)
               );
            }
         }
      }

      private boolean onMouseClicked(double var1, double var3) {
         if (this.bounds != null && this.bounds.contains(var1, var3)) {
            this.target.activate();
            if (this.target instanceof CategoryPanel.CategoryTarget var5) {
               ModuleCategory var7 = var5.category();
               Zenith.getInstance().getClientSounds().method1564(var7.ordinal(), ModuleCategory.values().length);
            } else {
               Zenith.getInstance().getClientSounds().method1570(false);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   private static final class SectionTarget implements CategoryPanel.Target {
      private final NLMenuScreen.ElementsType type;

      public NLMenuScreen.ElementsType type() {
         return this.type;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof SectionTarget other)) return false;
         return java.util.Objects.equals(this.type, other.type);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(type);
      }

      @Override
      public String toString() {
         return "SectionTarget[" + "type=" + this.type + "]";
      }

      private SectionTarget(NLMenuScreen.ElementsType var1) {
         this.type = var1;
      }

      @Override
      public boolean isSelected() {
         return Zenith.getInstance().getNLMenuScreen().getType() == this.type;
      }

      @Override
      public void activate() {
         NLMenuScreen var1 = Zenith.getInstance().getNLMenuScreen();
         var1.resetSearch();
         var1.setType(this.type);
      }
   }

   private interface Target {
      boolean isSelected();

      void activate();

      default String counter() {
         return null;
      }
   }
}

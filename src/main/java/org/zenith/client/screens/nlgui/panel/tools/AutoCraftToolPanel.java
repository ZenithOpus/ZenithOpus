package org.zenith.client.screens.nlgui.panel.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.misc.AutoCraftModule;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.elements.ChipStrip;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.utility.game.CraftRecipe;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class AutoCraftToolPanel extends ToolTab {
   private static final int FILTER_ALL = 0;
   private static final int FILTER_CRAFTABLE = 1;
   private static final float HERO_HEIGHT = 88.0F;
   private static final float CELL = 15.0F;
   private static final float CELL_GAP = 2.0F;
   private static final float CELL_ICON = 11.0F;
   private static final float RESULT = 28.0F;
   private static final float RESULT_ICON = 18.0F;
   private static final float BUTTON_HEIGHT = 22.0F;
   private static final float LIST_HEAD = 20.0F;
   private static final float RECIPE_ROW = 25.0F;
   private static final float STAT_HEIGHT = 36.0F;
   private static final float SOURCE_ROW = 28.0F;
   private static final float TOGGLE_ROW = 22.0F;
   private static final long PROBE_DELAY_MS = 400L;
   private final ChipStrip filterStrip = new ChipStrip("gui.tools.craft.filter.all", "gui.tools.craft.filter.craftable");
   private final Map<String, String> summaries = new HashMap<>();
   private int filter = 0;
   private int recipeCount;
   private int sourcesFrom = 3;
   private int recipesFrom = 3;
   private BoundsData filterBounds;
   private BoundsData buttonBounds;
   private String contentSignature = "";
   private String shownPreset = "";
   private AutoCraftModule.Data2 nearby = new AutoCraftModule.Data2(null, 0);
   private Map<String, Integer> inventory = Map.of();
   private long probedAt;

   private static AutoCraftModule module() {
      return AutoCraftModule.INSTANCE;
   }

   @Override
   public String headerTitle() {
      return "Crafter";
   }

   @Override
   public MenuGlyph headerIcon() {
      return MenuGlyph.CRAFT;
   }

   @Override
   public String sectionDescription() {
      return "gui.tools.craft.desc";
   }

   public boolean isLive() {
      return module().isEnabled();
   }

   @Override
   public void build() {
      AutoCraftModule var1 = module();
      CraftRecipe var2 = var1.method1097();
      String var3 = var2 == null ? "" : var2.getId();
      if (!var3.equals(this.shownPreset)) {
         this.shownPreset = var3;
         this.mark("preset");
      }

      this.custom(92.0F, this.entering(0, this::renderHero));
      this.custom(32.0F, this.entering(1, this::renderButton));
      this.custom(20.0F, this.entering(2, this::renderListHead));
      this.recipesFrom = this.mainEntryCount();
      this.buildRecipes(var1);
      this.buildAside(var1, var2);
   }

   private void buildRecipes(AutoCraftModule var1) {
      String var2 = this.searchQuery();
      List<CraftRecipe> var3 = var1.method1092(var1.method1098());
      ArrayList<CraftRecipe> var4 = new ArrayList();

      for (Object var6_o : (Iterable<?>)(Object)(var3)) {
         CraftRecipe var6 = (CraftRecipe)var6_o;
         String var7 = var6.getText3();
         if (var7 != null
            && !var7.isBlank()
            && (var2.isEmpty() || var7.toLowerCase(Locale.ROOT).contains(var2))
            && (this.filter != 1 || this.craftableNow(var6))) {
            var4.add(var6);
         }
      }

      this.recipeCount = var4.size();
      if (var4.isEmpty()) {
         String var9;
         String var11;
         if (var3.isEmpty()) {
            var9 = "gui.tools.craft.none";
            var11 = "gui.tools.craft.none.desc";
         } else if (this.filter == 1 && var2.isEmpty()) {
            var9 = "gui.tools.craft.noneCraftable";
            var11 = "gui.tools.craft.noneCraftable.desc";
         } else {
            var9 = "gui.tools.craft.noneFound";
            var11 = "gui.tools.craft.noneFound.desc";
         }

         this.custom(
            76.0F,
            this.entering(
               3,
               (var3x, var4x, var5, var6x, var7x, var8x, var9x, var10) -> this.emptyCard(
                  var3x, var10, var6x + 8.0F, var7x + 2.0F, var8x - 16.0F, 70.0F, MenuGlyph.CRAFT, capitalize(t(var9)), t(var11), var9x
               )
            )
         );
      } else {
         int var8 = 3;

         for (Object var12_o : (Iterable<?>)(Object)(var4)) {
            CraftRecipe var12 = (CraftRecipe)var12_o;
            this.custom(
               25.0F,
               this.entering(
                  var8++,
                  (var2x, var3x, var4x, var5, var6x, var7x, var8x, var9x) -> this.renderRecipe(var2x, var3x, var4x, var5, var6x, var7x, var8x, var9x, var12)
               ),
               () -> this.selectPreset(var1, var12),
               () -> var12.getId().equals(var1.getText2())
            );
         }
      }
   }

   private void buildAside(AutoCraftModule var1, CraftRecipe var2) {
      this.asideCaption("gui.tools.craft.caption.around");
      this.asideCustom(36.0F, this.entering(1, this::renderAround));
      this.asideCaption("gui.tools.craft.caption.sources");
      this.sourcesFrom = this.asideEntryCount();
      if (var2 == null) {
         this.asideCustom(
            62.0F,
            this.entering(
               2,
               (var1x, var2x, var3x, var4x, var5, var6x, var7x, var8x) -> this.miniEmpty(
                  var1x, var8x, var4x + 8.0F, var5 + 2.0F, var6x - 16.0F, 56.0F, t("gui.tools.craft.noPreset.desc"), var7x
               )
            )
         );
      } else {
         var var3 = var2.method5388();
         if (var3.isEmpty()) {
            this.asideCustom(
               62.0F,
               this.entering(
                  2,
                  (var1x, var2x, var3x, var4x, var5, var6x, var7x, var8x) -> this.miniEmpty(
                     var1x, var8x, var4x + 8.0F, var5 + 2.0F, var6x - 16.0F, 56.0F, t("gui.tools.craft.noIngredients.desc"), var7x
                  )
               )
            );
         } else {
            int var4 = 2;

            for (Object var6_o : (Iterable<?>)(Object)(var3.entrySet())) {
               Map.Entry var6 = (Map.Entry)var6_o;
               String var7 = (String)var6.getKey();
               int var8 = Math.max(1, (Integer)var6.getValue());
               this.asideCustom(
                  28.0F,
                  this.entering(
                     var4++,
                     (var4x, var5, var6x, var7x, var8x, var9, var10, var11) -> this.renderSource(
                        var4x, var5, var6x, var7x, var8x, var9, var10, var11, var2, var7, var8
                     )
                  )
               );
            }
         }
      }

      this.asideCaption("gui.tools.craft.caption.options");
      this.toggle("gui.tools.craft.option.take", "gui.tools.craft.option.take.desc", var1.getAutoTake(), 6);
      this.toggle("gui.tools.craft.option.store", "gui.tools.craft.option.store.desc", var1.getAutoStore(), 7);
      this.toggle("gui.tools.craft.option.batch", "gui.tools.craft.option.batch.desc", var1.getBatch(), 8);
   }

   private void toggle(String var1, String var2, BooleanSetting var3, int var4) {
      this.asideCustom(
         22.0F,
         this.entering(
            var4,
            (var4x, var5, var6, var7, var8, var9, var10, var11) -> {
               BoundsData var12 = new BoundsData(var7 + 8.0F, var8 + 1.0F, var9 - 16.0F, 19.0F);
               boolean var13 = var12.contains(var5, var6);
               float var14 = this.hot("tg:" + var1, var13);
               card(var4x, var11, var12.getFloatValue(), var12.getFloatValue2(), var12.width(), var12.height(), var14, var10);
               float var15 = 14.0F;
               float var16 = 7.5F;
               float var17 = var12.getFloatValue() + var12.width() - 6.0F - var15;
               this.drawSwitch(var4x, var1, var17, var12.getFloatValue2() + (var12.height() - var16) / 2.0F, var15, var16, var3.isEnabled(), var10, var11);
               Font var18 = Fonts.NEW_MEDIUM.getFont(5.01F);
               var4x.drawText(
                  var18,
                  var18.clip(t(var1), var17 - var12.getFloatValue() - 12.0F),
                  var12.getFloatValue() + 7.0F,
                  var12.getFloatValue2() + (var12.height() - var18.height()) / 2.0F,
                  var11.getTextSecondary()
                     .getZenithColor()
                     .method7295(var11.getTextEnable().getZenithColor(), var14 * 0.7F + (var3.isEnabled() ? 0.3F : 0.0F))
                     .method7294(var10)
               );
               if (var13) {
                  HoverInfo.request(t(var1), t(var2));
               }
            }
         ),
         () -> {
            var3.toggle();
            Zenith.getInstance().getManager().method7788();
         },
         null
      );
   }

   private void selectPreset(AutoCraftModule var1, CraftRecipe var2) {
      if (!var2.getId().equals(var1.getText2())) {
         var1.method1100(var2.getId());
         Zenith.getInstance().getManager().method7788();
         this.resetAsideScroll();
         this.replayAsideFrom(this.sourcesFrom);
         this.invalidate();
      }
   }

   private void renderHero(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      AutoCraftModule var9 = module();
      CraftRecipe var10 = var9.method1097();
      boolean var11 = var9.isEnabled();
      BoundsData var12 = new BoundsData(var4 + 8.0F, var5 + 2.0F, var6 - 16.0F, 88.0F);
      ZenithColor var13 = var8.getPrimaryColor().getZenithColor();
      accentCard(
         var1,
         var8,
         var12.getFloatValue(),
         var12.getFloatValue2(),
         var12.width(),
         var12.height(),
         0.0F,
         var7,
         var13,
         var11 ? 0.45F + 0.2F * pulse(0.7F) : 0.18F
      );
      if (var10 == null) {
         float var36 = 30.0F;
         float var37 = var12.getFloatValue() + 14.0F;
         float var38 = var12.getFloatValue2() + (var12.height() - var36) / 2.0F + (float)Math.sin(System.currentTimeMillis() / 600.0) * 1.2F;
         tile(var1, var8, var37, var38, var36, var13, 0.55F, var7);
         bigGlyph(var1, MenuGlyph.CRAFT, var37 + var36 / 2.0F, var38 + var36 / 2.0F, 2.0F, var13.method7294(var7));
         Font var39 = Fonts.NEW_SEMIBOLD.getFont(6.2F);
         Font var42 = Fonts.NEW_REGULAR.getFont(4.5F);
         float var45 = var37 + var36 + 12.0F;
         float var48 = var12.getFloatValue() + var12.width() - 10.0F - var45;
         List<String> var50 = wrapText(t("gui.tools.craft.pick.hint"), var42, var48, 3);
         float var52 = var39.height() + 4.0F + var50.size() * (var42.height() + 2.0F);
         float var54 = var12.getFloatValue2() + (var12.height() - var52) / 2.0F;
         var1.drawText(var39, t("gui.tools.craft.pick"), var45, var54, var8.getTextEnable().getZenithColor().method7294(var7));
         var54 += var39.height() + 4.0F;

         for (Object var59_o : (Iterable<?>)(Object)(var50)) {
            String var59 = (String)var59_o;
            var1.drawText(var42, var59, var45, var54, var8.getTextTertiary().getZenithColor().method7294(var7));
            var54 += var42.height() + 2.0F;
         }
      } else {
         if (var12.contains(var2, var3)) {
            HoverInfo.request(var10.method5400(var9), t("gui.tools.craft.recipe.desc"));
         }

         float var14 = this.since("preset", 520L);
         float var15 = var12.getFloatValue() + 10.0F;
         float var16 = var12.getFloatValue2() + 9.0F;
         float var17 = 49.0F;

         for (int var18 = 0; var18 < 9; var18++) {
            float var19 = var15 + var18 % 3 * 17.0F;
            float var20 = var16 + var18 / 3 * 17.0F;
            var1.drawRoundedRect(
               var19, var20, 15.0F, 15.0F, ProxyDotRadiusData.method8069(3.5F), var8.getPanelLeftBackground().getZenithColor().method7294(var7)
            );
         }

         for (int var40 = 0; var40 < 9; var40++) {
            String var43 = var10.method5391(var40);
            if (var43 != null && !var43.isBlank()) {
               float var46 = Math.max(0.0F, Math.min(1.0F, var14 * 1.8F - var40 * 0.07F));
               float var21 = var46 <= 0.0F ? 0.0F : 1.0F - (1.0F - var46) * (1.0F - var46) * (1.0F - var46);
               float var22 = 1.0F + (float)Math.sin(var46 * Math.PI) * 0.12F;
               float var23 = 11.0F * var21 * var22;
               if (!(var23 < 1.0F)) {
                  float var24 = var15 + var40 % 3 * 17.0F + (15.0F - var23) / 2.0F;
                  float var25 = var16 + var40 / 3 * 17.0F + (15.0F - var23) / 2.0F;
                  this.drawItem(var1, var9.method1117(var43).getDefaultStack(), var24, var25, var23, var7);
               }
            }
         }

         float var41 = var15 + var17 + 5.0F;
         float var44 = var41 + 18.0F;
         float var47 = var16 + (var17 - 28.0F) / 2.0F;
         this.drawFlow(var1, var41, var47 + 14.0F, 13.0F, var11, var8, var7);
         tile(var1, var8, var44, var47, 28.0F, var13, 0.35F, var7);
         float var49 = Math.max(0.0F, Math.min(1.0F, var14 * 1.6F - 0.35F));
         float var51 = 18.0F * (1.0F - (1.0F - var49) * (1.0F - var49)) * (1.0F + (float)Math.sin(var49 * Math.PI) * 0.1F);
         if (var51 > 1.0F) {
            this.drawItem(var1, var10.method5401(var9), var44 + (28.0F - var51) / 2.0F, var47 + (28.0F - var51) / 2.0F, var51, var7);
         }

         int var53 = this.possibleCrafts(var10);
         Font var56 = Fonts.NEW_SEMIBOLD.getFont(6.2F);
         Font var58 = Fonts.NEW_REGULAR.getFont(4.4F);
         Font var26 = Fonts.NEW_MEDIUM.getFont(4.3F);
         float var27 = var44 + 28.0F + 10.0F;
         float var28 = var12.getFloatValue() + var12.width() - 9.0F - var27;
         float var29 = var16 + 4.0F;
         var1.drawText(var56, var56.clip(var10.method5400(var9), var28), var27, var29, var8.getTextEnable().getZenithColor().method7294(var7));
         var29 += var56.height() + 3.0F;
         var1.drawText(var58, var58.clip(this.summary(var9, var10), var28), var27, var29, var8.getTextTertiary().getZenithColor().method7294(var7));
         var29 += var58.height() + 5.0F;
         String var30 = var53 > 0 ? t("gui.tools.craft.stock.enough").replace("{n}", String.valueOf(var53)) : t("gui.tools.craft.stock.none");
         pill(var1, var26, var27, var29, var30, var53 > 0 ? OK : var8.getTextTertiary().getZenithColor(), var7);
         AutoCraftToolPanel.Readiness var31 = this.readiness(var10, var53);
         ZenithColor var32 = this.stateColor(var31, var8);
         float var33 = var12.getFloatValue2() + var12.height() - 17.0F;
         var1.drawRect(var12.getFloatValue() + 8.0F, var33, var12.width() - 16.0F, 0.5F, var8.getFieldBorder().getZenithColor().method7294(var7 * 2.5F));
         float var34 = var33 + 8.5F;
         boolean var35 = var11 || var9.isBinding();
         dot(var1, var12.getFloatValue() + 12.0F, var34, 3.2F, var32, var35 ? pulse(1.2F) : 0.0F, var7);
         var1.drawText(
            var58,
            var58.clip(this.stateText(var31), var12.width() - 30.0F),
            var12.getFloatValue() + 18.0F,
            var34 - var58.height() / 2.0F,
            var32.method7294(var7)
         );
      }
   }

   private void drawFlow(MenuDrawContext var1, float var2, float var3, float var4, boolean var5, ZenithStyle var6, float var7) {
      ZenithColor var8 = var6.getTextTertiary().getZenithColor();
      ZenithColor var9 = var6.getPrimaryColor().getZenithColor();
      if (!var5) {
         var1.drawRect(var2, var3 - 0.5F, var4 - 3.0F, 1.0F, var8.method7294(var7));
         var1.drawRect(var2 + var4 - 5.0F, var3 - 2.5F, 1.0F, 2.0F, var8.method7294(var7));
         var1.drawRect(var2 + var4 - 5.0F, var3 + 0.5F, 1.0F, 2.0F, var8.method7294(var7));
         var1.drawRect(var2 + var4 - 4.0F, var3 - 1.5F, 1.0F, 3.0F, var8.method7294(var7));
      } else {
         float var10 = (float)(System.currentTimeMillis() % 900L) / 900.0F;

         for (int var11 = 0; var11 < 3; var11++) {
            float var12 = (var10 + var11 / 3.0F) % 1.0F;
            float var13 = var2 + var12 * var4;
            float var14 = (float)Math.sin(var12 * Math.PI);
            var1.drawRoundedRect(var13 - 1.2F, var3 - 1.2F, 2.4F, 2.4F, ProxyDotRadiusData.method8069(1.2F), var9.method7294(var7 * var14));
         }
      }
   }

   private void renderButton(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      AutoCraftModule var9 = module();
      boolean var10 = var9.isEnabled();
      boolean var11 = var9.method1097() != null || var10;
      BoundsData var12 = new BoundsData(var4 + 8.0F, var5 + 4.0F, var6 - 16.0F, 22.0F);
      this.buttonBounds = var11 ? var12 : null;
      String var13 = t(var11 ? (var10 ? "gui.tools.craft.stop" : "gui.tools.craft.start") : "gui.tools.craft.start.disabled");
      boolean var14 = this.button(var1, var8, "start", var12, var10 ? MenuGlyph.CLOSE : MenuGlyph.CRAFT, var13, var10 ? 2 : 1, var11, var2, var3, var7);
      if (var10) {
         this.shimmer(var1, var12, DANGER, var7);
      }

      if (var14) {
         HoverInfo.request(var13, t(var10 ? "gui.tools.craft.stop.desc" : "gui.tools.craft.start.desc"));
      }
   }

   private void renderListHead(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      Font var9 = Fonts.NEW_MEDIUM.getFont(4.0F);
      float var10 = var5 + (20.0F - var9.height()) / 2.0F;
      String var11 = t("gui.tools.craft.caption.presets");
      caption(var1, var8, var11, var4 + 8.0F, var10, var7);
      String var12 = String.valueOf(this.recipeCount);
      float var13 = var4 + 8.0F + var9.width(HoverInfo.spaced(var11.toUpperCase(Locale.ROOT))) + 5.0F;
      Font var14 = Fonts.NEW_MEDIUM.getFont(4.0F);
      var1.drawText(var14, var12, var13, var10, var8.getTextSecondary().getZenithColor().method7294(var7));
      float var15 = this.filterStrip.width();
      float var16 = var4 + var6 - 8.0F - var15;
      float var17 = var5 + 4.5F;
      this.filterBounds = new BoundsData(var16, var17, var15, 11.0F);
      this.filterStrip.render(var1, var2, var3, var16, var17, var15 + 1.0F, this.filter, var7, var8);
      if (this.filterStrip.hovered() >= 0) {
         HoverInfo.request(this.filterStrip.label(this.filterStrip.hovered()), t("gui.tools.craft.filter.desc"));
      }
   }

   private void renderRecipe(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8, CraftRecipe var9) {
      AutoCraftModule var10 = module();
      BoundsData var11 = new BoundsData(var4 + 8.0F, var5 + 1.0F, var6 - 16.0F, 22.0F);
      boolean var12 = var11.contains(var2, var3);
      boolean var13 = var9.getId().equals(var10.getText2());
      float var14 = this.hot("r:" + var9.getId(), var12);
      float var15 = this.hot("rs:" + var9.getId(), var13);
      ZenithColor var16 = var8.getPrimaryColor().getZenithColor();
      if (var15 > 0.01F) {
         accentCard(var1, var8, var11.getFloatValue(), var11.getFloatValue2(), var11.width(), var11.height(), var14, var7, var16, var15 * 0.75F);
      } else {
         card(var1, var8, var11.getFloatValue(), var11.getFloatValue2(), var11.width(), var11.height(), var14, var7);
      }

      float var17 = var11.height() - 6.0F;
      float var18 = var11.getFloatValue() + 3.0F;
      float var19 = var11.getFloatValue2() + 3.0F;
      tile(var1, var8, var18, var19, var17, var16, var15 * 0.6F + var14 * 0.2F, var7);
      float var20 = var14 * 0.8F;
      this.drawItem(var1, var9.method5401(var10), var18 + (var17 - 12.0F) / 2.0F, var19 + (var17 - 12.0F) / 2.0F - var20, 12.0F, var7);
      boolean var21 = this.craftableNow(var9);
      Font var22 = Fonts.NEW_MEDIUM.getFont(5.01F);
      Font var23 = Fonts.NEW_REGULAR.getFont(4.0F);
      Font var24 = Fonts.NEW_MEDIUM.getFont(4.0F);
      String var25 = t("gui.tools.craft.list.can");
      float var26 = var11.getFloatValue() + var11.width() - 6.0F;
      if (var21) {
         float var27 = pillWidth(var24, var25);
         pill(var1, var24, var26 - var27, var11.getFloatValue2() + (var11.height() - var24.height() - 3.6F) / 2.0F, var25, OK, var7);
         var26 -= var27 + 5.0F;
      }

      float var29 = var18 + var17 + 6.0F;
      float var28 = var26 - var29;
      var1.drawText(
         var22,
         var22.clip(var9.getText3(), var28),
         var29,
         var11.getFloatValue2() + 4.0F,
         var8.getTextSecondary().getZenithColor().method7295(var8.getTextEnable().getZenithColor(), Math.max(var15, var14 * 0.7F)).method7294(var7)
      );
      var1.drawText(
         var23,
         var23.clip(this.summary(var10, var9), var28),
         var29,
         var11.getFloatValue2() + 6.0F + var22.height(),
         var8.getTextTertiary().getZenithColor().method7294(var7)
      );
      if (var12) {
         HoverInfo.request(var9.getText3(), t("gui.tools.craft.preset.desc"));
      }
   }

   private String summary(AutoCraftModule var1, CraftRecipe var2) {
      return this.summaries.computeIfAbsent(var2.getId(), var2x -> {
         ArrayList var3 = new ArrayList();

         for (Map.Entry var5 : var2.method5388().entrySet()) {
            var3.add(Math.max(1, (Integer)var5.getValue()) + "× " + var2.method5397((String)var5.getKey(), var1));
         }

         return String.join("  ·  ", var3);
      });
   }

   private void renderAround(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8) {
      float var9 = var6 - 16.0F;
      float var10 = (var9 - 4.0F) / 2.0F;
      boolean var11 = this.nearby.method1131();
      this.statTile(
         var1,
         var8,
         var2,
         var3,
         var4 + 8.0F,
         var5 + 1.0F,
         var10,
         32.0F,
         Items.CRAFTING_TABLE.getDefaultStack(),
         t("gui.tools.craft.around.workbench"),
         t(var11 ? "gui.tools.craft.near.yes" : "gui.tools.craft.near.no"),
         var11 ? OK : DANGER,
         "gui.tools.craft.around.workbench.desc",
         var7
      );
      int var12 = this.nearby.getIntValue();
      this.statTile(
         var1,
         var8,
         var2,
         var3,
         var4 + 8.0F + var10 + 4.0F,
         var5 + 1.0F,
         var10,
         32.0F,
         Items.CHEST.getDefaultStack(),
         t("gui.tools.craft.around.storages"),
         String.valueOf(var12),
         var12 > 0 ? var8.getTextEnable().getZenithColor() : var8.getTextTertiary().getZenithColor(),
         "gui.tools.craft.around.storages.desc",
         var7
      );
   }

   private void statTile(
      MenuDrawContext var1,
      ZenithStyle var2,
      int var3,
      int var4,
      float var5,
      float var6,
      float var7,
      float var8,
      ItemStack var9,
      String var10,
      String var11,
      ZenithColor var12,
      String var13,
      float var14
   ) {
      boolean var15 = new BoundsData(var5, var6, var7, var8).contains(var3, var4);
      float var16 = this.hot("stat:" + var10, var15);
      card(var1, var2, var5, var6, var7, var8, var16, var14);
      float var17 = 16.0F;
      tile(var1, var2, var5 + 5.0F, var6 + (var8 - var17) / 2.0F, var17, var12, 0.35F, var14);
      this.drawItem(var1, var9, var5 + 7.0F, var6 + (var8 - 12.0F) / 2.0F, 12.0F, var14);
      Font var18 = Fonts.NEW_REGULAR.getFont(4.1F);
      Font var19 = Fonts.NEW_MEDIUM.getFont(5.0F);
      float var20 = var5 + 5.0F + var17 + 5.0F;
      float var21 = var5 + var7 - 4.0F - var20;
      float var22 = var18.height() + 2.0F + var19.height();
      float var23 = var6 + (var8 - var22) / 2.0F;
      var1.drawText(var18, var18.clip(var10, var21), var20, var23, var2.getTextTertiary().getZenithColor().method7294(var14));
      var1.drawText(var19, var19.clip(var11, var21), var20, var23 + var18.height() + 2.0F, var12.method7294(var14));
      if (var15) {
         HoverInfo.request(var10, t(var13));
      }
   }

   private void renderSource(
      MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, ZenithStyle var8, CraftRecipe var9, String var10, int var11
   ) {
      AutoCraftModule var12 = module();
      String var13 = var9.method5395(var10);
      String var14 = var9.method5397(var10, var12);
      int var15 = var12.method1121(var13, var9.method5396(var10));
      boolean var16 = var15 >= var11;
      BoundsData var17 = new BoundsData(var4 + 8.0F, var5 + 1.0F, var6 - 16.0F, 25.0F);
      boolean var18 = var17.contains(var2, var3);
      card(var1, var8, var17.getFloatValue(), var17.getFloatValue2(), var17.width(), var17.height(), this.hot("src:" + var10, var18), var7);
      float var19 = 16.0F;
      tile(
         var1,
         var8,
         var17.getFloatValue() + 4.0F,
         var17.getFloatValue2() + (var17.height() - var19) / 2.0F,
         var19,
         var16 ? OK : var8.getPrimaryColor().getZenithColor(),
         0.3F,
         var7
      );
      this.drawItem(
         var1, var12.method1117(var13).getDefaultStack(), var17.getFloatValue() + 6.0F, var17.getFloatValue2() + (var17.height() - 12.0F) / 2.0F, 12.0F, var7
      );
      Font var20 = Fonts.NEW_MEDIUM.getFont(4.8F);
      Font var21 = Fonts.NEW_MEDIUM.getFont(4.4F);
      String var22 = Math.min(var15, 9999) + " / " + var11;
      float var23 = var17.getFloatValue() + 4.0F + var19 + 5.0F;
      float var24 = var17.getFloatValue() + var17.width() - 6.0F;
      var1.drawText(
         var21, var22, var24 - var21.width(var22), var17.getFloatValue2() + 5.0F, (var16 ? OK : var8.getTextSecondary().getZenithColor()).method7294(var7)
      );
      var1.drawText(
         var20,
         var20.clip(var14, var24 - var21.width(var22) - 5.0F - var23),
         var23,
         var17.getFloatValue2() + 4.5F,
         var8.getTextEnable().getZenithColor().method7294(var7)
      );
      progress(
         var1,
         var8,
         var23,
         var17.getFloatValue2() + var17.height() - 7.0F,
         var24 - var23,
         2.5F,
         (float)var15 / var11,
         var16 ? OK : var8.getPrimaryColor().getZenithColor(),
         false,
         var7
      );
      if (var18) {
         HoverInfo.request(var14, t("gui.tools.craft.source.desc"));
      }
   }

   private void miniEmpty(MenuDrawContext var1, ZenithStyle var2, float var3, float var4, float var5, float var6, String var7, float var8) {
      card(var1, var2, var3, var4, var5, var6, 0.0F, var8);

      for (int var9 = 0; var9 < 2; var9++) {
         float var10 = var4 + 7.0F + var9 * 13.0F;
         skeleton(var1, var2, var3 + 6.0F, var10, 10.0F, 10.0F, var9, var8 * 0.8F);
         skeleton(var1, var2, var3 + 21.0F, var10 + 1.5F, (var5 - 30.0F) * (var9 == 0 ? 0.7F : 0.5F), 3.0F, var9, var8 * 0.8F);
         skeleton(var1, var2, var3 + 21.0F, var10 + 6.5F, (var5 - 30.0F) * 0.9F, 2.0F, var9 + 1, var8 * 0.6F);
      }

      Font var11 = Fonts.NEW_REGULAR.getFont(4.3F);
      String var12 = var11.clip(var7, var5 - 12.0F);
      var1.drawText(
         var11, var12, var3 + (var5 - var11.width(var12)) / 2.0F, var4 + var6 - var11.height() - 6.0F, var2.getTextTertiary().getZenithColor().method7294(var8)
      );
   }

   private static String capitalize(String var0) {
      return var0 != null && !var0.isEmpty() ? var0.substring(0, 1).toUpperCase(Locale.ROOT) + var0.substring(1) : "";
   }

   private AutoCraftToolPanel.Readiness readiness(CraftRecipe var1, int var2) {
      if (var1 == null) {
         return AutoCraftToolPanel.Readiness.NO_PRESET;
      } else if (!this.nearby.method1131()) {
         return AutoCraftToolPanel.Readiness.NO_WORKBENCH;
      } else if (var2 > 0) {
         return AutoCraftToolPanel.Readiness.READY;
      } else {
         return this.nearby.getIntValue() > 0 ? AutoCraftToolPanel.Readiness.FROM_CHESTS : AutoCraftToolPanel.Readiness.NO_RESOURCES;
      }
   }

   private String stateText(AutoCraftToolPanel.Readiness var1) {
      AutoCraftModule var2 = module();
      if (var2.isBinding()) {
         return t("gui.tools.craft.state.binding");
      } else if (var2.isEnabled()) {
         String var3 = var2.method1119();
         return var3.isBlank() ? t("gui.tools.craft.state.running") : var3;
      } else {
         return switch (var1) {
            case NO_PRESET -> t("gui.tools.craft.state.noPreset");
            case NO_WORKBENCH -> t("gui.tools.craft.state.noWorkbench");
            case NO_RESOURCES -> t("gui.tools.craft.state.noResources");
            case FROM_CHESTS -> t("gui.tools.craft.state.fromChests");
            case READY -> t("gui.tools.craft.state.ready");
         };
      }
   }

   private ZenithColor stateColor(AutoCraftToolPanel.Readiness var1, ZenithStyle var2) {
      AutoCraftModule var3 = module();
      if (!var3.isEnabled() && !var3.isBinding()) {
         return switch (var1) {
            case NO_PRESET -> var2.getTextTertiary().getZenithColor();
            default -> DANGER;
            case FROM_CHESTS -> var2.getPrimaryColor().getZenithColor();
            case READY -> OK;
         };
      } else {
         return var2.getPrimaryColor().getZenithColor();
      }
   }

   private int possibleCrafts(CraftRecipe var1) {
      AutoCraftModule var2 = module();
      var var3 = var1.method5388();
      if (var3.isEmpty()) {
         return 0;
      } else {
         int var4 = Integer.MAX_VALUE;

         for (Object var6_o : (Iterable<?>)(Object)(var3.entrySet())) {
            Map.Entry var6 = (Map.Entry)var6_o;
            String var7 = (String)var6.getKey();
            int var8 = Math.max(1, (Integer)var6.getValue());
            int var9 = var2.method1121(var1.method5395(var7), var1.method5396(var7));
            var4 = Math.min(var4, var9 / var8);
            if (var4 == 0) {
               return 0;
            }
         }

         return var4 == Integer.MAX_VALUE ? 0 : var4;
      }
   }

   private boolean craftableNow(CraftRecipe var1) {
      var var2 = var1.method5388();
      if (!var2.isEmpty() && !this.inventory.isEmpty()) {
         for (Object var4_o : (Iterable<?>)(Object)(var2.entrySet())) {
            Map.Entry var4 = (Map.Entry)var4_o;
            String var5 = var1.method5395((String)var4.getKey());
            if (this.inventory.getOrDefault(var5, 0) < Math.max(1, (Integer)var4.getValue())) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.buttonBounds = null;
      this.filterBounds = null;
      super.render(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      BoundsData var6 = this.mainViewport();
      if (var6 != null && var6.contains(var1, var3) && var5 == Enum3.LEFT) {
         if (this.buttonBounds != null && this.buttonBounds.contains(var1, var3)) {
            this.pressButton("start", var1, var3);
            module().toggle();
            Zenith.getInstance().getClientSounds().method1570(false);
            this.invalidate();
            return true;
         }

         if (this.filterBounds != null && this.filterBounds.contains(var1, var3)) {
            int var7 = this.filterStrip.hovered();
            if (var7 >= 0) {
               if (var7 != this.filter) {
                  this.filter = var7;
                  this.resetScroll();
                  this.replayMainFrom(this.recipesFrom);
                  this.invalidate();
               }

               Zenith.getInstance().getClientSounds().method1570(false);
               return true;
            }
         }
      }

      return super.onMouseClicked(var1, var3, var5);
   }

   @Override
   public void tick() {
      AutoCraftModule var1 = module();
      long var2 = System.currentTimeMillis();
      if (var2 - this.probedAt >= 400L) {
         this.probedAt = var2;
         this.nearby = var1.method1120();
         this.inventory = var1.method1122();
      }

      int var4 = var1.method1092(var1.method1098()).size();
      String var5 = var1.getText2() + "|" + var4 + "|" + this.filter + "|" + this.searchQuery() + (this.filter == 1 ? "|" + this.inventory.hashCode() : "");
      if (!var5.equals(this.contentSignature)) {
         if (this.summaries.size() > 2048) {
            this.summaries.clear();
         }

         this.contentSignature = var5;
         this.invalidate();
      }
   }

   private static enum Readiness {
      NO_PRESET,
      NO_WORKBENCH,
      NO_RESOURCES,
      FROM_CHESTS,
      READY;
   }
}

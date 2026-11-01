package org.zenith.client.screens.nlgui.elements.setting;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.math.Vector2f;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.BlockListSetting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.ScrollHandler;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiItemSelectSetting extends GuiSetting<BlockListSetting> {
   private final Animation animationExpanded = new Animation(200L, Easing.INSTANCE13);
   private SearchBox searchBox;
   private final ScrollHandler scrollHandler = new ScrollHandler();
   private boolean sortBySelected = false;
   private BoundsData bounds;
   private BoundsData rectBounds;
   private BoundsData exitBounds;
   private BoundsData searchBounds;
   private BoundsData sortBounds;
   private BoundsData listScissorBounds;
   private final Map<Block, BoundsData> itemBounds = new HashMap<>();
   private boolean expanded;

   public GuiItemSelectSetting(BlockListSetting var1) {
      this(var1, 166.0F);
   }

   public GuiItemSelectSetting(BlockListSetting var1, float var2) {
      super(var2, var1);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.bounds != null && this.bounds.method8092(var1, var3, 2.0F)) {
         this.expanded = (!this.expanded);
         return true;
      } else if (this.expanded && this.rectBounds != null && this.rectBounds.contains(var1, var3)) {
         return true;
      } else {
         this.expanded = false;
         return false;
      }
   }

   @Override
   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var7 != null) {
         this.animationVisible.method2807(this.setting.isVisible());
         var6 *= this.animationVisible.getValue();
         Font var8 = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font var9 = Fonts.NEW_REGULAR.getFont(5.4F);
         float var10 = this.width / 1.4F - GuiStyle.PADDING.intValue();
         ZenithColor var11 = var7.getTextEnable().getZenithColor().method7294(var6);
         ZenithColor var12 = var7.getTextSecondary().getZenithColor().method7294(var6);
         ZenithColor var13 = var7.getPrimaryColor().getZenithColor().method7294(var6);
         this.drawDefault(var1, var2, var3, "i", this.setting.getName(), this.setting.getDescription(), var8, var9, var4, var5, var10, var11, var12, var13);
         float var14 = 6.0F;
         float var15 = 6.0F;
         this.bounds = new BoundsData(var4 + this.width - var14, var5 + (this.getHeight() - var15) / 2.0F, var14, var15);
         var1.drawRoundedRect(
            this.bounds.getFloatValue(),
            this.bounds.getFloatValue2(),
            var14,
            var15,
            ProxyDotRadiusData.method8069(1.0F),
            var7.getFieldSurfaceBackground().getZenithColor().method7294(var6)
         );
         var1.drawRoundedBorder(
            this.bounds.getFloatValue(),
            this.bounds.getFloatValue2(),
            var14,
            var15,
            -0.5F,
            ProxyDotRadiusData.method8069(1.0F),
            var7.getFieldBorder().getZenithColor().method7294(var6)
         );
         Font var16 = Fonts.NEW_ICONS.getFont(5.5F);
         var1.drawText(
            var16,
            "v",
            this.bounds.getFloatValue() + (var14 - var16.width("v")) / 2.0F,
            this.bounds.getFloatValue2() + (var15 - var16.height()) / 2.0F,
            var7.getPrimaryColor().getZenithColor().method7294(var6)
         );
      }
   }

   @Override
   public void renderPriority(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.animationExpanded.method2807(this.expanded);
      if (!(this.animationExpanded.getValue() <= 0.0F) && this.bounds != null) {
         if (this.searchBox == null) {
            this.searchBox = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.3F), "Search...", 0.0F);
            this.searchBox.setType(SearchBox.Type.ENGLISH);
         }

         float var8;
         float var9 = (var8 = GuiStyle.PADDING.intValue() * 2.0F) * 2.0F + this.getHeight();
         float var10 = 17.0F;
         float var11 = 115.0F;
         float var12 = 126.0F;
         float var13 = var8 * 2.0F;
         float var14 = var9 + var8 + var10 + var13 + var11;
         float var15 = this.bounds.getFloatValue() + GuiStyle.PADDING.intValue() * 2.0F;
         float var16 = this.bounds.getFloatValue2() - var14 / 2.0F;
         this.rectBounds = new BoundsData(var15, var16, var12, var14);
         var6 *= this.animationExpanded.getValue();
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(var15, this.bounds.getFloatValue2());
         var1.getMatrices().scale(this.animationExpanded.getValue(), this.animationExpanded.getValue());
         var1.getMatrices().translate(-var15, -this.bounds.getFloatValue2());
         ZenithStyle var17 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var17 != null) {
            RenderShaders.method5289(
               MatrixUtils.method664(var1.getMatrices()),
               var15,
               var16,
               var12,
               var14,
               12.0F,
               ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
               ZenithColor.INSTANCE.method7294(var6)
            );
            var1.drawRoundedRect(
               var15,
               var16,
               var12,
               var9,
               ProxyDotRadiusData.method8074(GuiStyle.ROUND.intValue() / 2.0F, GuiStyle.ROUND.intValue() / 2.0F),
               var17.getRightBackground().getZenithColor().method7294(var6)
            );
            var1.drawRoundedRect(
               var15, var16 + var9, var12, var8 + var10 + var13, ProxyDotRadiusData.INSTANCE, var17.getPanelLeftBackground().getZenithColor().method7294(var6)
            );
            var1.drawRoundedRect(
               var15,
               var16 + var9 + var10 + var13,
               var12,
               var11 + var8,
               ProxyDotRadiusData.method8076(GuiStyle.ROUND.intValue() / 2.0F, GuiStyle.ROUND.intValue() / 2.0F),
               var17.getRightBackground().getZenithColor().method7294(var6)
            );
            Font var18 = Fonts.NEW_ICONS.getFont(4.0F);
            float var19 = var15 + var12 - var18.width("2") - var8;
            float var20 = var16 + var8 + var18.height();
            this.exitBounds = new BoundsData(var19, var20, 5.0F, 5.0F);
            var1.drawText(var18, "2", var19, var20, var17.getTextTertiary().getZenithColor().method7294(var6));
            Font var21 = Fonts.NEW_MEDIUM.getFont(5.5F);
            Font var22 = Fonts.NEW_REGULAR.getFont(5.3F);
            float var23 = var12 / 1.4F - GuiStyle.PADDING.intValue();
            ZenithColor var24 = var17.getTextEnable().getZenithColor().method7294(var6);
            ZenithColor var25 = var17.getTextSecondary().getZenithColor().method7294(var6);
            ZenithColor var26 = var17.getPrimaryColor().getZenithColor().method7294(var6);
            this.drawDefault(
               var1,
               var2,
               var3,
               "i",
               this.setting.getName(),
               this.setting.getDescription(),
               var21,
               var22,
               var15 + var8,
               var16 + var8,
               var23,
               var24,
               var25,
               var26
            );
            float var27 = var16 + var9 + var8;
            this.searchBounds = new BoundsData(var15 + var8, var27, var12 - var8 * 2.0F, var10);
            var1.drawRoundedRect(
               this.searchBounds.getFloatValue(),
               this.searchBounds.getFloatValue2(),
               this.searchBounds.width(),
               this.searchBounds.height(),
               ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
               var17.getFieldSurfaceBackground().getZenithColor().method7294(var6)
            );
            var1.drawRoundedBorder(
               this.searchBounds.getFloatValue(),
               this.searchBounds.getFloatValue2(),
               this.searchBounds.width(),
               this.searchBounds.height(),
               0.1F,
               ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
               var17.getFieldBorder().getZenithColor().method7294(var6)
            );
            float var28 = 14.0F;
            float var29 = this.searchBounds.width() - var8 * 2.0F - var28 - var8;
            this.searchBox.setWidth(var29);
            this.searchBox.setMaxLength(35);
            this.searchBox
               .method987(
                  var1,
                  this.searchBounds.getFloatValue() + var8,
                  this.searchBounds.getFloatValue2() + (var10 - Fonts.NEW_MEDIUM.getFont(5.3F).height()) / 2.0F,
                  var17.getTextEnable().getZenithColor().method7294(var6),
                  var17.getTextTertiary().getZenithColor().method7294(var6)
               );
            this.sortBounds = new BoundsData(
               this.searchBounds.getFloatValue() + this.searchBounds.width() - var8 - var28, var27 + (var10 - var28) / 2.0F, var28, var28
            );
            var1.drawRoundedRect(
               this.sortBounds.getFloatValue(),
               this.sortBounds.getFloatValue2(),
               var28,
               var28,
               ProxyDotRadiusData.method8069(2.0F),
               var17.getFieldSurfaceBackground().getZenithColor().method7294(var6)
            );
            Font var30 = Fonts.NEW_ICONS.getFont(5.01F);
            var1.drawText(
               var30,
               "W",
               this.sortBounds.getFloatValue() + (var28 - var30.width("W")) / 2.0F,
               this.sortBounds.getFloatValue2() + (var28 - var30.height()) / 2.0F,
               var17.getPrimaryColor().getZenithColor().method7294(var6)
            );
            List<Block> var31 = this.getFilteredAndSortedBlocks();
            float var32 = 13.0F;
            float var33 = 13.0F + GuiStyle.PADDING.intValue();
            float var34 = var31.size() * var33;
            this.scrollHandler.setDoubleValue(Math.max(0.0F, var34 - var11));
            this.scrollHandler.update();
            float var35 = var27 + var10 + var13;
            this.listScissorBounds = new BoundsData(var15, var35, var12, var11 - var8);
            float var36 = var35 - (float)this.scrollHandler.method6103();
            float var37 = var15 + var8;
            float var38 = 105.0F;
            this.itemBounds.clear();
            var1.enableScissor((int)var15, (int)var35, (int)(var15 + var12), (int)(var35 + var11));
            Font var39 = Fonts.NEW_MEDIUM.getFont(5.3F);
            float var40 = 8.0F;
            float var41 = 0.5F;
            int var42 = 0;
            ZenithColor var43 = var17.getFieldSurfaceBackground().method1447(var6);
            ZenithColor var44 = var17.getPrimaryColor().getZenithColor().method7294(0.25F * var6);
            ZenithColor var45 = var17.getFieldBorder().method1447(var6);

            for (Object var47_o : (Iterable<?>)(Object)(var31)) {
               Block var47 = (Block)var47_o;
               if (var36 + var42 * var33 < var35 - var33) {
                  var42++;
               } else {
                  float var48;
                  if ((var48 = var36 + var42 * var33) > var35 + var11) {
                     break;
                  }

                  boolean var49 = this.setting.method1621(var47);
                  BoundsData var50 = new BoundsData(var37, var48, var38, var32);
                  this.itemBounds.put(var47, var50);
                  var1.drawRoundedRect(
                     var50.getFloatValue(),
                     var50.getFloatValue2(),
                     var50.width(),
                     var50.height(),
                     ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
                     var49 ? var44 : var43
                  );
                  var1.drawRoundedBorder(
                     var50.getFloatValue(),
                     var50.getFloatValue2(),
                     var50.width(),
                     var50.height(),
                     0.1F,
                     ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() / 2.0F),
                     var45
                  );
                  var1.getMatrices().pushMatrix();
                  var1.getMatrices().translate(var37 + 4.0F, var48 + (var32 - var40) / 2.0F);
                  var1.getMatrices().scale(var41, var41);
                  var1.drawItem(var47.asItem().getDefaultStack(), 0, 0);
                  var1.getMatrices().popMatrix();
                  String var51 = var47.getTranslationKey().replaceFirst("^block\\.minecraft\\.", "").replaceAll("_", " ");
                  if (!var51.isEmpty()) {
                     var51 = var51.substring(0, 1).toUpperCase() + var51.substring(1);
                  }

                  float var52 = var37 + GuiStyle.PADDING.intValue() + var40 + GuiStyle.PADDING.intValue() / 2.0F;
                  var1.drawText(
                     var39,
                     var39.clip(var51, var50.getFloatValue() + var50.width() - GuiStyle.PADDING.intValue() - var52),
                     var52,
                     var48 + (var32 - var39.height()) / 2.0F,
                     (var49 ? var17.getTextEnable() : var17.getTextSecondary()).getZenithColor().method7294(var6)
                  );
                  var42++;
               }
            }

            var1.disableScissor();
            float var54 = 1.5F;
            float var55 = var15 + var12 - var8 - var54;
            float var56 = var11 - var8 / 2.0F;
            if (this.scrollHandler.getDoubleValue() > 0.0) {
               var1.drawRoundedRect(
                  var55, var35, var54, var56, ProxyDotRadiusData.method8069(0.5F), var17.getFieldSurfaceBackground().getZenithColor().method7294(var6)
               );
               float var57 = (float)(this.scrollHandler.method6103() / this.scrollHandler.getDoubleValue());
               float var58 = Math.max(var56 * (var56 / (float)(var56 + this.scrollHandler.getDoubleValue())), 12.0F);
               float var59 = Math.max(1.0F, var56 - var58);
               float var60 = var35 + var59 * var57;
               var60 = Math.min(var35 + var56 - var58, var60);
               var1.drawRoundedRect(var55, var60, var54, var58, ProxyDotRadiusData.method8069(0.5F), var17.getFieldBorder().getZenithColor().method7294(var6));
            }

            var1.getMatrices().popMatrix();
         }
      }
   }

   private List<Block> getFilteredAndSortedBlocks() {
      String var1 = this.searchBox != null && !this.searchBox.isEmpty() ? this.searchBox.getText().toLowerCase().trim() : "";
      var var2 = getAllBlocks().filter(var0 -> (var0 != Blocks.AIR));
      if (!var1.isEmpty()) {
         var2 = var2.filter(var1x -> {
            String var2x = var1x.getTranslationKey().replaceFirst("^block\\.minecraft\\.", "").replaceAll("_", " ");
            return var2x.toLowerCase().contains(var1);
         });
      }

      var var3 = new ArrayList<>(var2.toList());
      if (this.sortBySelected || var1.isEmpty()) {
         var3.sort((var1x, var2x) -> {
            boolean var3x = this.setting.method1621(var1x);
            boolean var4 = this.setting.method1621(var2x);
            return Boolean.compare(!var3x, !var4);
         });
      }

      return var3;
   }

   private static Stream<Block> getAllBlocks() {
      return Stream.of(Blocks.class.getDeclaredFields())
         .filter(var0 -> Modifier.isStatic(var0.getModifiers()))
         .filter(var0 -> Modifier.isPublic(var0.getModifiers()))
         .filter(var0 -> Block.class.isAssignableFrom(var0.getType()))
         .map(var0 -> {
            try {
               return (Block)var0.get(null);
            } catch (IllegalAccessException var2) {
               throw new RuntimeException(var2);
            }
         });
   }

   @Override
   public boolean onMousePriorityClicked(double var1, double var3, Enum3 var5) {
      if (!this.expanded || this.rectBounds == null) {
         return false;
      } else if (!this.rectBounds.contains(var1, var3)) {
         this.expanded = false;
         if (this.searchBox != null) {
            this.searchBox.setSelected(false);
         }

         return false;
      } else if (this.exitBounds != null && this.exitBounds.method8092(var1, var3, 2.0F)) {
         this.expanded = false;
         if (this.searchBox != null) {
            this.searchBox.setSelected(false);
         }

         return true;
      } else if (this.searchBounds != null && this.searchBounds.contains(var1, var3)) {
         if (this.searchBox != null) {
            this.searchBox.setSelected(true);
         }

         return true;
      } else {
         if (this.searchBox != null) {
            this.searchBox.setSelected(false);
         }

         if (this.sortBounds != null && this.sortBounds.method8092(var1, var3, 2.0F)) {
            this.sortBySelected = (!this.sortBySelected);
            this.scrollHandler.setDoubleValue2(0.0);
            return true;
         } else {
            if (this.listScissorBounds != null && this.listScissorBounds.contains(var1, var3) && var5.getIntValue() == 0) {
               for (Entry var7 : this.itemBounds.entrySet()) {
                  if (((BoundsData)var7.getValue()).contains(var1, var3)) {
                     if (this.setting.method1621((Block)var7.getKey())) {
                        this.setting.method1619((Block)var7.getKey());
                     } else {
                        this.setting.method1617((Block)var7.getKey());
                     }

                     return true;
                  }
               }
            }

            return true;
         }
      }
   }

   @Override
   public boolean onMousePriorityScroll(double var1, double var3, double var5, double var7) {
      if (!this.expanded) {
         return false;
      } else if (this.listScissorBounds != null && this.listScissorBounds.contains(var1, var3)) {
         this.scrollHandler.method6104(var7 * 10.0);
         return true;
      } else {
         this.expanded = false;
         if (this.searchBox != null) {
            this.searchBox.setSelected(false);
         }

         return false;
      }
   }

   @Override
   public boolean keyPressed(KeyInput var1) {
      return this.keyPressed(var1.key(), var1.scancode(), var1.modifiers());
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      return (boolean)(this.expanded && this.searchBox != null && this.searchBox.keyPressed(var1, var2, var3)
         ? 1
         : super.keyPressed(new KeyInput(var1, var2, var3)));
   }

   @Override
   public boolean charTyped(CharInput var1) {
      return this.charTyped((char)var1.codepoint(), var1.modifiers());
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return (boolean)(this.expanded && this.searchBox != null && this.searchBox.charTyped(var1, var2) ? 1 : super.charTyped(new CharInput(var1, var2)));
   }
}

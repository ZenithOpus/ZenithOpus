package org.zenith.client.screens.emote;

import dev.kosmx.playerAnim.api.IPlayer;
import dev.kosmx.playerAnim.api.layered.AnimationContainer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import kotlin.collections.EmptyList;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.text.Text;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.misc.EmotesModule;
import org.zenith.client.screens.builder.PlayerPreview3D;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.EmoteAccess;
import org.zenith.utility.misc.EmoteInfo;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.PreviewEmotePlayer;
import org.zenith.utility.misc.SearchBox;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class EmoteFavoriteScreen extends Screen {
   private static final int PREVIEW_ANIMATION_PRIORITY = 2000;
   private static final int COLUMNS = 3;
   private static final float GAP = 5.0F;
   private static final float PREVIEW_GAP = 12.0F;
   private static final float CARD_HEIGHT = 72.0F;
   private static final float SCROLL_SPEED = 38.0F;
   private static final float SCROLL_SMOOTH = 0.25F;
   private final EmotesModule owner;
   private final int favoriteSlot;
   private final List<EmoteInfo> allEmotes;
   private final SearchBox searchBox;
   private final Animation openAnimation = new Animation(280L, 0.0F, Easing.INSTANCE4);
   private final Animation contentAnimation = new Animation(220L, 0.0F, Easing.INSTANCE3);
   private final Animation searchAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private final Animation closeAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private final Animation scrollBarAnimation = new Animation(140L, 0.0F, Easing.INSTANCE12);
   private final Map<String, Animation> cardAnimations = new HashMap<>();
   private final PlayerPreview3D playerPreview = new PlayerPreview3D(0.0F, 0.0F, 0.0F, 0.0F);
   private final AnimationContainer<PreviewEmotePlayer> previewAnimation = new AnimationContainer(null);
   private List<EmoteInfo> filteredEmotes;
   private ClientPlayerEntity previewAnimationEntity;
   private PreviewEmotePlayer previewEmotePlayer;
   private String previewEmoteId;
   private String previousSearch = "";
   private float scroll;
   private float scrollTarget;
   private float interactiveScale = 1.0F;
   private boolean closing;
   private boolean draggingScrollBar;
   private float scrollBarDragOffset;

   public EmoteFavoriteScreen(EmotesModule var1, int var2) {
      super(Text.literal("Favorite emote"));
      this.owner = var1;
      this.favoriteSlot = var2;
      this.allEmotes = EmoteAccess.method7917().stream().sorted(Comparator.comparing(EmoteInfo::getText2, String.CASE_INSENSITIVE_ORDER)).toList();
      this.filteredEmotes = this.allEmotes;
      this.searchBox = new SearchBox(new Vector2f(0.0F, 0.0F), Fonts.NEW_MEDIUM.getFont(5.0F), t("gui.emote.search"), 180.0F);
      this.searchBox.setMaxLength(80);

      for (EmoteInfo var4 : this.allEmotes) {
         this.cardAnimations.put(var4.getText(), new Animation(140L, 0.0F, Easing.INSTANCE12));
      }
   }

   @Override
   public void init() {
      this.openAnimation.method2809(0.0F);
      this.openAnimation.method2808(1.0F);
      this.restartContentAnimation();
      this.closing = false;
      this.searchBox.setSelected(false);
   }

   @Override
   public void tick() {
      if (this.previewEmotePlayer != null) {
         this.previewEmotePlayer.tick();
      }

      super.tick();
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      float var5;
      float var6 = Math.clamp(var5 = this.openAnimation.method2808(this.closing ? 0.0F : 1.0F), 0.0F, 1.0F);
      if (this.closing && var6 <= 0.001F) {
         this.finishClose();
      } else {
         ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var7 != null) {
            MenuDrawContext var8 = MenuDrawContext.method8080(context, mouseX, mouseY, deltaTicks);
            var8.drawRect(0.0F, 0.0F, this.width, this.height, var7.getRightBackground().getZenithColor().method7293((int)(145.0F * var6)));
            EmoteFavoriteScreen.Layout var9 = this.layout();
            this.updateFilter();
            this.interactiveScale = 0.9F + 0.1F * var5;
            float var10 = this.transformX(mouseX, var9);
            float var11 = this.transformY(mouseY, var9);
            var8.getMatrices().pushMatrix();
            var8.getMatrices().translate(var9.centerX(), var9.centerY());
            var8.getMatrices().scale(this.interactiveScale, this.interactiveScale);
            var8.getMatrices().translate(-var9.centerX(), -var9.centerY());
            ZenithColor var12 = var7.getLeftBackground().getZenithColor().method7294(var6);
            ZenithColor var13 = var7.getRightBackground().getZenithColor().method7294(var6);
            ZenithColor var14 = var7.getSurfaceDisableBackground().getZenithColor().method7294(var6);
            ZenithColor var15 = var7.getSurfaceEnableBackground().getZenithColor().method7294(var6);
            ZenithColor var16 = var7.getDisableActiveBg().getZenithColor().method7294(var6);
            ZenithColor var17 = var7.getTextEnable().getZenithColor().method7294(var6);
            ZenithColor var18 = var7.getTextSecondary().getZenithColor().method7294(var6);
            ZenithColor var19 = var7.getTextTertiary().getZenithColor().method7294(var6);
            ZenithColor var20 = var7.getPrimaryColor().getZenithColor().method7294(var6);
            float var21;
            if ((var21 = Zenith.getInstance().getNLMenuScreen().getBlurPower()) != 0.0F) {
               RenderShaders.method5287(
                  MatrixUtils.method664(var8.getMatrices()),
                  var9.panelX,
                  var9.panelY,
                  var9.panelWidth,
                  var9.panelHeight,
                  var21,
                  ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()),
                  ZenithColor.INSTANCE.method7294(var6),
                  true,
                  false
               );
            }

            var8.drawRoundedRect(var9.panelX, var9.panelY, var9.panelWidth, var9.panelHeight, ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()), var12);
            var8.drawRoundedRect(
               var9.panelX + 6.0F, var9.panelY + 6.0F, var9.panelWidth - 12.0F, 37.0F, ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue() - 1.0F), var13
            );
            this.renderHeader(var8, var9, var10, var11, var7, var14, var15, var17, var18, var6);
            this.renderCards(var8, var9, var10, var11, var14, var15, var16, var17, var19, var20, var6);
            this.renderScrollBar(var8, var9, var10, var11, var7, var6);
            this.renderPlayerPreview(context, var8, var9, var10, var11, var14, var6);
            var8.getMatrices().popMatrix();
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void renderPlayerPreview(
      DrawContext var1, MenuDrawContext var2, EmoteFavoriteScreen.Layout var3, float var4, float var5, ZenithColor var6, float var7
   ) {
      var2.drawRoundedRect(var3.previewX, var3.gridY, var3.previewWidth, var3.gridHeight, ProxyDotRadiusData.method8069(6.0F), var6);
      if (this.client != null && this.client.player != null) {
         this.playerPreview.setBounds(var3.previewX, var3.gridY, var3.previewWidth, var3.gridHeight);
         var2.enableScissor((int)var3.previewX, (int)var3.gridY, (int)(var3.previewX + var3.previewWidth), (int)(var3.gridY + var3.gridHeight));
         RenderState.method1883(1.0F, 1.0F, 1.0F, var7);
         boolean var12 = false /* VF: Semaphore variable */;

         try {
            var12 = true;
            if (this.previewEmotePlayer == null) {
               this.detachPreviewAnimationLayer();
               this.playerPreview.render(var1, this.client.player, var4, var5);
               var12 = false;
            } else {
               this.attachPreviewAnimation(this.client.player);
               this.previewAnimation.setAnim(this.previewEmotePlayer);

               try {
                  this.playerPreview.render(var1, this.client.player, var4, var5);
               } catch (Throwable var15) {
                  this.previewAnimation.setAnim(null);
                  throw var15;
               }

               this.previewAnimation.setAnim(null);
               var12 = false;
            }
         } finally {
            if (var12) {
               RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
               var2.disableScissor();
            }
         }

         RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
         var2.disableScissor();
      }
   }

   private void renderHeader(
      MenuDrawContext var1,
      EmoteFavoriteScreen.Layout var2,
      float var3,
      float var4,
      ZenithStyle var5,
      ZenithColor var6,
      ZenithColor var7,
      ZenithColor var8,
      ZenithColor var9,
      float var10
   ) {
      Font var11 = Fonts.NEW_MEDIUM.getFont(5.8F);
      String var12 = t("gui.emote.pick");
      var1.drawText(var11, var12, var2.contentX, var2.panelY + (37.0F - var11.height()) / 2.0F + 6.0F, var8);
      double var10000 = var3;
      double var10001 = var4;
      float var26 = 18.0F;
      float var25 = 18.0F;
      float var24 = var2.closeY;
      float var23 = var2.closeX;
      double var21 = var10001;
      double var19 = var10000;
      int var13 = var10000 >= var23 && var21 >= var24 && var19 <= var23 + var25 && var21 <= var24 + var26 ? 1 : 0;
      float var14 = this.closeAnimation.method2808(var13 != 0 ? 1.0F : 0.0F);
      var1.drawRoundedRect(var2.closeX, var2.closeY, 18.0F, 18.0F, ProxyDotRadiusData.method8069(5.0F), var6.method7295(var7, var14));
      Font var15 = Fonts.NEW_SEMIBOLD.getFont(6.0F);
      var1.drawText(var15, "×", var2.closeX + (18.0F - var15.width("×")) / 2.0F, var2.closeY + (18.0F - var15.height()) / 2.0F - 0.5F, var8);
      float var16 = this.searchAnimation.method2808(this.searchBox.isSelected() ? 1.0F : 0.0F);
      ZenithColor var17 = var5.getFieldSurfaceBackground()
         .getZenithColor()
         .method7294(var10)
         .method7295(var5.getPanelLeftBackground().getZenithColor().method7294(var10), var16 * 0.35F);
      var1.drawRoundedRect(var2.searchX, var2.searchY, var2.searchWidth, 20.0F, ProxyDotRadiusData.method8069(GuiStyle.ROUND.intValue()), var17);
      this.searchBox.setWidth(var2.searchWidth - 23.0F);
      this.searchBox.method987(var1, var2.searchX + 7.0F, var2.searchY + (20.0F - this.searchBox.getFont().height()) / 2.0F, var8, var9);
      Font var18 = Fonts.NEW_ICONS.getFont(4.5F);
      var1.drawText(var18, "S", var2.searchX + var2.searchWidth - var18.width("S") - 7.0F, var2.searchY + (20.0F - var18.height()) / 2.0F, var9);
   }

   private void renderCards(
      MenuDrawContext var1,
      EmoteFavoriteScreen.Layout var2,
      float var3,
      float var4,
      ZenithColor var5,
      ZenithColor var6,
      ZenithColor var7,
      ZenithColor var8,
      ZenithColor var9,
      ZenithColor var10,
      float var11
   ) {
      float var12 = this.contentHeight();
      this.clampScroll(var12, var2.gridHeight);
      this.scroll = this.scroll + (this.scrollTarget - this.scroll) * 0.25F;
      float var13 = Math.clamp(this.contentAnimation.method2808(1.0F), 0.0F, 1.0F);
      String var14 = this.owner.method6905(this.favoriteSlot);
      Font var15 = Fonts.NEW_MEDIUM.getFont(5.2F);
      Font var16 = Fonts.NEW_REGULAR.getFont(4.3F);
      var1.enableScissor((int)var2.gridX, (int)var2.gridY, (int)(var2.gridX + var2.gridWidth), (int)(var2.gridY + var2.gridHeight));
      int var17 = 0;
      EmoteInfo var18 = null;

      for (int var19 = 0; var19 < this.filteredEmotes.size(); var19++) {
         EmoteInfo var20 = this.filteredEmotes.get(var19);
         int var21 = var19 % 3;
         int var22 = var19 / 3;
         float var23 = var2.gridX + var21 * (var2.cardWidth + 5.0F);
         float var24;
         if (!((var24 = var2.gridY + var22 * 77.0F + this.scroll) + 72.0F < var2.gridY) && !(var24 > var2.gridY + var2.gridHeight)) {
            byte var53;
            label82: {
               double var10000 = var3;
               double var10001 = var4;
               float var45 = var2.gridHeight;
               float var44 = var2.gridWidth;
               float var43 = var2.gridY;
               float var42 = var2.gridX;
               double var40 = var10001;
               double var38 = var10000;
               if ((var10000 >= var42 && var40 >= var43 && var38 <= var42 + var44 && var40 <= var43 + var45 ? 1 : 0) != 0) {
                  double var52 = var3;
                  var10001 = var4;
                  var45 = 72.0F;
                  var44 = var2.cardWidth;
                  var40 = var10001;
                  var38 = var52;
                  if ((var52 >= var23 && var40 >= var24 && var38 <= var23 + var44 && var40 <= var24 + var45 ? 1 : 0) != 0) {
                     var53 = 1;
                     break label82;
                  }
               }

               var53 = 0;
            }

            byte var25 = var53;
            if (var53 != 0) {
               var18 = var20;
            }

            boolean var26 = var20.getText().equals(var14);
            float var27 = this.cardAnimations.get(var20.getText()).method2808(var25 != 0 ? 1.0F : 0.0F);
            float var28 = Math.clamp(var13 * 1.25F - var17 * 0.035F, 0.0F, 1.0F);
            float var29 = var24 + (1.0F - var28) * 8.0F;
            float var30 = var11 * var28;
            var17++;
            ZenithColor var31 = var5.method7294(var28).method7295(var6.method7294(var28), var27);
            if (var26) {
               var31 = var31.method7295(var7.method7294(var28), 0.75F);
            }

            var1.drawRoundedRect(var23, var29, var2.cardWidth, 72.0F, ProxyDotRadiusData.method8069(6.0F), var31);
            float var32 = 39.0F + var27 * 3.0F;
            float var33 = var23 + (var2.cardWidth - var32) / 2.0F;
            float var34 = var29 + 5.0F - var27;
            var1.drawTexture(var20.getIdentifier(), var33, var34, var32, var32, ZenithColor.INSTANCE.method7294(var30));
            String var35 = fit(var15, var20.getText2(), var2.cardWidth - 8.0F);
            var1.drawText(var15, var35, var23 + (var2.cardWidth - var15.width(var35)) / 2.0F, var29 + 72.0F - 17.0F, var8.method7294(var28));
            String var36 = var20.getText().startsWith("spemotes/") ? "SPEmotes" : "Zenith";
            ZenithColor var37 = var26 ? var10.method7294(var28) : var9.method7294(var28);
            var1.drawText(var16, var36, var23 + (var2.cardWidth - var16.width(var36)) / 2.0F, var29 + 72.0F - 8.0F, var37);
         }
      }

      if (var18 != null) {
         this.updatePreviewEmote(var18);
      }

      boolean var54;
      label67: {
         if (this.filteredEmotes != null) {
            if (this.filteredEmotes.getClass() == EmptyList.class) {
               var54 = true;
               break label67;
            }
         }

         var54 = this.filteredEmotes.isEmpty();
      }

      if (var54) {
         Font var46 = Fonts.NEW_MEDIUM.getFont(5.5F);
         String var47 = t("gui.configs.empty.query");
         var1.drawText(var46, var47, var2.gridX + (var2.gridWidth - var46.width(var47)) / 2.0F, var2.gridY + (var2.gridHeight - var46.height()) / 2.0F, var9);
      }

      var1.disableScissor();
   }

   private void renderScrollBar(MenuDrawContext var1, EmoteFavoriteScreen.Layout var2, float var3, float var4, ZenithStyle var5, float var6) {
      EmoteFavoriteScreen.ScrollBarMetrics var7 = this.scrollBarMetrics(var2);
      if (var7 != null) {
         double var10000 = var3;
         double var10001 = var4;
         float var10002 = var2.scrollBarX - 2.0F;
         float var21 = var2.gridHeight;
         float var20 = 7.0F;
         float var19 = var2.gridY;
         float var18 = var10002;
         double var16 = var10001;
         double var14 = var10000;
         int var8 = var10000 >= var18 && var16 >= var19 && var14 <= var18 + var20 && var16 <= var19 + var21 ? 1 : 0;
         float var9 = this.scrollBarAnimation.method2808(var8 == 0 && !this.draggingScrollBar ? 0.0F : 1.0F);
         float var10 = 3.0F + var9;
         float var11 = var2.scrollBarX - var9 / 2.0F;
         ZenithColor var12 = var5.getFieldSurfaceBackground().getZenithColor().method7294(var6);
         ZenithColor var13 = var5.getTextTertiary()
            .getZenithColor()
            .method7294(var6)
            .method7295(var5.getPrimaryColor().getZenithColor().method7294(var6), var9 * 0.35F);
         var1.drawRoundedRect(var11, var2.gridY, var10, var2.gridHeight, ProxyDotRadiusData.method8069(var10 / 2.0F), var12);
         var1.drawRoundedRect(var11, var7.thumbY, var10, var7.thumbHeight, ProxyDotRadiusData.method8069(var10 / 2.0F), var13);
      }
   }

   @Override
   public boolean mouseClicked(Click click, boolean doubled) {
      return this.mouseClicked(click.x(), click.y(), click.button());
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.closing) {
         return true;
      } else {
         EmoteFavoriteScreen.Layout var6 = this.layout();
         var1 = this.transformX(var1, var6);
         var3 = this.transformY(var3, var6);
         float var16 = 18.0F;
         float var15 = 18.0F;
         float var14 = var6.closeY;
         float var13 = var6.closeX;
         if ((var1 >= var13 && var3 >= var14 && var1 <= var13 + var15 && var3 <= var14 + var16 ? 1 : 0) != 0) {
            this.close();
            return true;
         } else if (this.searchBox.onMouseClicked(var1, var3, Enum3.method6094(var5))) {
            return true;
         } else if (this.playerPreview.onMouseClicked(var1, var3, var5)) {
            return true;
         } else {
            EmoteFavoriteScreen.ScrollBarMetrics var7 = this.scrollBarMetrics(var6);
            if (var5 == 0 && var7 != null) {
               float var10002 = var6.scrollBarX - 2.0F;
               var16 = var6.gridHeight;
               var15 = 7.0F;
               var14 = var6.gridY;
               var13 = var10002;
               if ((var1 >= var13 && var3 >= var14 && var1 <= var13 + var15 && var3 <= var14 + var16 ? 1 : 0) != 0) {
                  this.draggingScrollBar = true;
                  var10002 = var6.scrollBarX - 2.0F;
                  var16 = var7.thumbHeight;
                  var15 = 7.0F;
                  var14 = var7.thumbY;
                  var13 = var10002;
                  if ((var1 >= var13 && var3 >= var14 && var1 <= var13 + var15 && var3 <= var14 + var16 ? 1 : 0) != 0) {
                     this.scrollBarDragOffset = (float)var3 - var7.thumbY;
                  } else {
                     this.scrollBarDragOffset = var7.thumbHeight / 2.0F;
                     this.updateDraggedScrollBar(var3, var6, var7);
                  }

                  return true;
               }
            }

            if (var5 == 0) {
               var16 = var6.gridHeight;
               var15 = var6.gridWidth;
               var14 = var6.gridY;
               var13 = var6.gridX;
               if ((var1 >= var13 && var3 >= var14 && var1 <= var13 + var15 && var3 <= var14 + var16 ? 1 : 0) != 0) {
                  int var8;
                  if ((var8 = this.cardAt(var1, var3, var6)) >= 0 && var8 < this.filteredEmotes.size()) {
                     this.owner.method6903(this.favoriteSlot, this.filteredEmotes.get(var8).getText());
                     this.close();
                     return true;
                  }

                  return false;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean mouseDragged(Click click, double offsetX, double offsetY) {
      return this.mouseDragged(click.x(), click.y(), click.button(), offsetX, offsetY);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      EmoteFavoriteScreen.Layout var10 = this.layout();
      double var11 = this.transformX(var1, var10);
      double var13 = this.transformY(var3, var10);
      if (this.playerPreview.onMouseDragged(var11, var13, var5, var6, var8)) {
         return true;
      } else {
         if (this.draggingScrollBar && var5 == 0) {
            var3 = var13;
            EmoteFavoriteScreen.ScrollBarMetrics var15 = this.scrollBarMetrics(var10);
            if (var15 != null) {
               this.updateDraggedScrollBar(var13, var10, var15);
               return true;
            }
         }

         return super.mouseDragged(new Click(var1, var3, new MouseInput(var5, 0)), var6, var8);
      }
   }

   @Override
   public boolean mouseReleased(Click click) {
      return this.mouseReleased(click.x(), click.y(), click.button());
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (this.playerPreview.onMouseReleased(var1, var3, var5)) {
         return true;
      } else if (var5 == 0 && this.draggingScrollBar) {
         this.draggingScrollBar = false;
         return true;
      } else {
         return super.mouseReleased(new Click(var1, var3, new MouseInput(var5, 0)));
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (!this.closing && verticalAmount != 0.0) {
         EmoteFavoriteScreen.Layout var9 = this.layout();
         mouseX = this.transformX(mouseX, var9);
         mouseY = this.transformY(mouseY, var9);
         if (this.playerPreview.onMouseScrolled(mouseX, mouseY, verticalAmount)) {
            return true;
         } else {
            float var18 = var9.gridHeight;
            float var17 = var9.gridWidth;
            float var16 = var9.gridY;
            float var15 = var9.gridX;
            if ((mouseX >= var15 && mouseY >= var16 && mouseX <= var15 + var17 && mouseY <= var16 + var18 ? 1 : 0) == 0) {
               return false;
            } else {
               float var10;
               if ((var10 = this.contentHeight()) <= var9.gridHeight) {
                  return false;
               } else {
                  this.scrollTarget += (float)verticalAmount * 38.0F;
                  this.clampScroll(var10, var9.gridHeight);
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(KeyInput input) {
      return this.keyPressed(input.key(), input.scancode(), input.modifiers());
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.closing) {
         return true;
      } else if (var1 == 256) {
         this.close();
         return true;
      } else {
         return !(!this.searchBox.keyPressed(var1, var2, var3) && !super.keyPressed(new KeyInput(var1, var2, var3)));
      }
   }

   @Override
   public boolean charTyped(CharInput input) {
      return this.charTyped((char)input.codepoint(), input.modifiers());
   }

   public boolean charTyped(char var1, int var2) {
      if (this.closing) {
         return true;
      } else {
         return !(!this.searchBox.charTyped(var1, var2) && !super.charTyped(new CharInput(var1, var2)));
      }
   }

   @Override
   public void close() {
      if (!this.closing) {
         this.closing = true;
         this.draggingScrollBar = false;
         this.searchBox.setSelected(false);
      }
   }

   @Override
   public boolean shouldPause() {
      return false;
   }

   @Override
   public void removed() {
      this.detachPreviewAnimation();
      super.removed();
   }

   @Override
   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
   }

   private void updateFilter() {
      String var1 = this.searchBox.getText().trim().toLowerCase(Locale.ROOT);
      if (!var1.equals(this.previousSearch)) {
         this.previousSearch = var1;
         this.filteredEmotes = var1.isEmpty()
            ? this.allEmotes
            : this.allEmotes
               .stream()
               .filter(
                  var1x -> !(!var1x.getText2().toLowerCase(Locale.ROOT).contains(var1)
                        && !var1x.getText().toLowerCase(Locale.ROOT).contains(var1)
                        && !var1x.author().toLowerCase(Locale.ROOT).contains(var1))
               )
               .toList();
         this.scroll = 0.0F;
         this.scrollTarget = 0.0F;
         this.draggingScrollBar = false;
         this.restartContentAnimation();
      }
   }

   private int cardAt(double var1, double var3, EmoteFavoriteScreen.Layout var5) {
      for (int var6 = 0; var6 < this.filteredEmotes.size(); var6++) {
         int var7 = var6 % 3;
         int var8 = var6 / 3;
         float var9 = var5.gridX + var7 * (var5.cardWidth + 5.0F);
         float var10 = var5.gridY + var8 * 77.0F + this.scroll;
         float var18 = 72.0F;
         float var17 = var5.cardWidth;
         if ((var1 >= var9 && var3 >= var10 && var1 <= var9 + var17 && var3 <= var10 + var18 ? 1 : 0) != 0) {
            return var6;
         }
      }

      return -1;
   }

   private float contentHeight() {
      if (this.filteredEmotes != null) {
         if (this.filteredEmotes.getClass() == EmptyList.class) {
            int var1;
            return 1 != 0 ? 0.0F : (var1 = (this.filteredEmotes.size() + 3 - 1) / 3) * 72.0F + Math.max(0, var1 - 1) * 5.0F;
         }
      }

      int var2;
      return this.filteredEmotes.isEmpty() ? 0.0F : (var2 = (this.filteredEmotes.size() + 3 - 1) / 3) * 72.0F + Math.max(0, var2 - 1) * 5.0F;
   }

   private void clampScroll(float var1, float var2) {
      if (var1 <= var2) {
         this.scroll = 0.0F;
         this.scrollTarget = 0.0F;
      } else {
         float var3 = var2 - var1;
         this.scrollTarget = Math.clamp(this.scrollTarget, var3, 0.0F);
         this.scroll = Math.clamp(this.scroll, var3, 0.0F);
      }
   }

   private EmoteFavoriteScreen.ScrollBarMetrics scrollBarMetrics(EmoteFavoriteScreen.Layout var1) {
      float var2;
      if ((var2 = this.contentHeight()) <= var1.gridHeight) {
         return null;
      } else {
         float var3 = var1.gridHeight - var2;
         float var4 = Math.max(24.0F, var1.gridHeight * var1.gridHeight / var2);
         var4 = Math.min(var1.gridHeight, var4);
         float var5 = var1.gridHeight - var4;
         float var6 = Math.clamp(this.scroll / var3, 0.0F, 1.0F);
         return new EmoteFavoriteScreen.ScrollBarMetrics(var1.gridY + var5 * var6, var4, var5, var3);
      }
   }

   private void updateDraggedScrollBar(double var1, EmoteFavoriteScreen.Layout var3, EmoteFavoriteScreen.ScrollBarMetrics var4) {
      float var5 = Math.clamp((float)var1 - this.scrollBarDragOffset, var3.gridY, var3.gridY + var4.travel);
      float var6 = var4.travel <= 0.0F ? 0.0F : (var5 - var3.gridY) / var4.travel;
      this.scroll = var4.minimum * var6;
      this.scrollTarget = this.scroll;
   }

   private void restartContentAnimation() {
      this.contentAnimation.method2809(0.0F);
      this.contentAnimation.method2808(1.0F);
   }

   private void updatePreviewEmote(EmoteInfo var1) {
      String var2 = var1 == null ? null : var1.getText();
      if (!Objects.equals(this.previewEmoteId, var2) || this.previewEmotePlayer != null && !this.previewEmotePlayer.isActive()) {
         if (this.previewEmotePlayer != null) {
            this.previewEmotePlayer.stop();
         }

         this.previewEmoteId = var2;
         this.previewEmotePlayer = var1 == null ? null : new PreviewEmotePlayer(var1, 0);
         if (var1 == null) {
            this.detachPreviewAnimationLayer();
         }
      }
   }

   private void attachPreviewAnimation(ClientPlayerEntity var1) {
      if (this.previewAnimationEntity != var1) {
         this.detachPreviewAnimationLayer();
         ((IPlayer)var1).playerAnimator$getAnimationStack().addAnimLayer(2000, this.previewAnimation);
         this.previewAnimationEntity = var1;
      }
   }

   private void detachPreviewAnimationLayer() {
      this.previewAnimation.setAnim(null);
      if (this.previewAnimationEntity != null) {
         ((IPlayer)this.previewAnimationEntity).playerAnimator$getAnimationStack().removeLayer(this.previewAnimation);
         this.previewAnimationEntity = null;
      }
   }

   private void detachPreviewAnimation() {
      this.detachPreviewAnimationLayer();
      if (this.previewEmotePlayer != null) {
         this.previewEmotePlayer.stop();
      }

      this.previewEmotePlayer = null;
      this.previewEmoteId = null;
   }

   private void finishClose() {
      if (this.client != null && this.client.currentScreen == this) {
         this.client.setScreen(null);
      }
   }

   private EmoteFavoriteScreen.Layout layout() {
      float var1 = Math.min(this.width - 30.0F, 520.0F);
      float var2 = Math.min(this.height - 24.0F, 360.0F);
      float var3 = (this.width - var1) / 2.0F;
      float var4 = (this.height - var2) / 2.0F;
      float var5 = var3 + 14.0F;
      float var6 = var1 - 28.0F;
      float var7 = Math.min(190.0F, var6 * 0.4F);
      float var8 = var3 + var1 - 14.0F - var7;
      float var9 = var4 + 14.5F;
      float var10 = var4 + 51.0F;
      float var11 = var4 + var2 - 14.0F - var10;
      float var12 = var6 * 0.39F;
      float var13 = var6 - var12 - 12.0F;
      float var14 = var5 + var13 + 3.0F;
      float var15 = var5 + var13 + 12.0F;
      float var16 = (var13 - 10.0F) / 3.0F;
      return new EmoteFavoriteScreen.Layout(
         var3, var4, var1, var2, var5, var8, var9, var7, var5, var10, var13, var11, var16, var14, var15, var12, var8 - 24.0F, var4 + 15.5F
      );
   }

   private float transformX(double var1, EmoteFavoriteScreen.Layout var3) {
      return (float)((var1 - var3.centerX()) / this.interactiveScale + var3.centerX());
   }

   private float transformY(double var1, EmoteFavoriteScreen.Layout var3) {
      return (float)((var1 - var3.centerY()) / this.interactiveScale + var3.centerY());
   }

   private static String fit(Font var0, String var1, float var2) {
      if (var0.width(var1) <= var2) {
         return var1;
      } else {
         String var3 = "…";
         int var4 = var1.length();

         while (var4 > 0 && var0.width(var1.substring(0, var4) + var3) > var2) {
            var4--;
         }

         return var1.substring(0, var4) + var3;
      }
   }

   private static boolean inside(double var0, double var2, float var4, float var5, float var6, float var7) {
      return (var0 >= var4 && var2 >= var5 && var0 <= var4 + var6 && var2 <= var5 + var7);
   }

   private static String t(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   private static final class Layout {
      private final float panelX;
      private final float panelY;
      private final float panelWidth;
      private final float panelHeight;
      private final float contentX;
      private final float searchX;
      private final float searchY;
      private final float searchWidth;
      private final float gridX;
      private final float gridY;
      private final float gridWidth;
      private final float gridHeight;
      private final float cardWidth;
      private final float scrollBarX;
      private final float previewX;
      private final float previewWidth;
      private final float closeX;
      private final float closeY;

      public float panelX() {
         return this.panelX;
      }

      public float panelY() {
         return this.panelY;
      }

      public float panelWidth() {
         return this.panelWidth;
      }

      public float panelHeight() {
         return this.panelHeight;
      }

      public float contentX() {
         return this.contentX;
      }

      public float searchX() {
         return this.searchX;
      }

      public float searchY() {
         return this.searchY;
      }

      public float searchWidth() {
         return this.searchWidth;
      }

      public float gridX() {
         return this.gridX;
      }

      public float gridY() {
         return this.gridY;
      }

      public float gridWidth() {
         return this.gridWidth;
      }

      public float gridHeight() {
         return this.gridHeight;
      }

      public float cardWidth() {
         return this.cardWidth;
      }

      public float scrollBarX() {
         return this.scrollBarX;
      }

      public float previewX() {
         return this.previewX;
      }

      public float previewWidth() {
         return this.previewWidth;
      }

      public float closeX() {
         return this.closeX;
      }

      public float closeY() {
         return this.closeY;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Layout other)) return false;
         return java.util.Objects.equals(this.panelX, other.panelX) && java.util.Objects.equals(this.panelY, other.panelY) && java.util.Objects.equals(this.panelWidth, other.panelWidth) && java.util.Objects.equals(this.panelHeight, other.panelHeight) && java.util.Objects.equals(this.contentX, other.contentX) && java.util.Objects.equals(this.searchX, other.searchX) && java.util.Objects.equals(this.searchY, other.searchY) && java.util.Objects.equals(this.searchWidth, other.searchWidth) && java.util.Objects.equals(this.gridX, other.gridX) && java.util.Objects.equals(this.gridY, other.gridY) && java.util.Objects.equals(this.gridWidth, other.gridWidth) && java.util.Objects.equals(this.gridHeight, other.gridHeight) && java.util.Objects.equals(this.cardWidth, other.cardWidth) && java.util.Objects.equals(this.scrollBarX, other.scrollBarX) && java.util.Objects.equals(this.previewX, other.previewX) && java.util.Objects.equals(this.previewWidth, other.previewWidth) && java.util.Objects.equals(this.closeX, other.closeX) && java.util.Objects.equals(this.closeY, other.closeY);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(panelX, panelY, panelWidth, panelHeight, contentX, searchX, searchY, searchWidth, gridX, gridY, gridWidth, gridHeight, cardWidth, scrollBarX, previewX, previewWidth, closeX, closeY);
      }

      @Override
      public String toString() {
         return "Layout[" + "panelX=" + this.panelX + ", " + "panelY=" + this.panelY + ", " + "panelWidth=" + this.panelWidth + ", " + "panelHeight=" + this.panelHeight + ", " + "contentX=" + this.contentX + ", " + "searchX=" + this.searchX + ", " + "searchY=" + this.searchY + ", " + "searchWidth=" + this.searchWidth + ", " + "gridX=" + this.gridX + ", " + "gridY=" + this.gridY + ", " + "gridWidth=" + this.gridWidth + ", " + "gridHeight=" + this.gridHeight + ", " + "cardWidth=" + this.cardWidth + ", " + "scrollBarX=" + this.scrollBarX + ", " + "previewX=" + this.previewX + ", " + "previewWidth=" + this.previewWidth + ", " + "closeX=" + this.closeX + ", " + "closeY=" + this.closeY + "]";
      }

      private Layout(
         float var1,
         float var2,
         float var3,
         float var4,
         float var5,
         float var6,
         float var7,
         float var8,
         float var9,
         float var10,
         float var11,
         float var12,
         float var13,
         float var14,
         float var15,
         float var16,
         float var17,
         float var18
      ) {
         this.panelX = var1;
         this.panelY = var2;
         this.panelWidth = var3;
         this.panelHeight = var4;
         this.contentX = var5;
         this.searchX = var6;
         this.searchY = var7;
         this.searchWidth = var8;
         this.gridX = var9;
         this.gridY = var10;
         this.gridWidth = var11;
         this.gridHeight = var12;
         this.cardWidth = var13;
         this.scrollBarX = var14;
         this.previewX = var15;
         this.previewWidth = var16;
         this.closeX = var17;
         this.closeY = var18;
      }

      private float centerX() {
         return this.panelX + this.panelWidth / 2.0F;
      }

      private float centerY() {
         return this.panelY + this.panelHeight / 2.0F;
      }
   }

   private static final class ScrollBarMetrics {
      private final float thumbY;
      private final float thumbHeight;
      private final float travel;
      private final float minimum;

      public float thumbY() {
         return this.thumbY;
      }

      public float thumbHeight() {
         return this.thumbHeight;
      }

      public float travel() {
         return this.travel;
      }

      public float minimum() {
         return this.minimum;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof ScrollBarMetrics other)) return false;
         return java.util.Objects.equals(this.thumbY, other.thumbY) && java.util.Objects.equals(this.thumbHeight, other.thumbHeight) && java.util.Objects.equals(this.travel, other.travel) && java.util.Objects.equals(this.minimum, other.minimum);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(thumbY, thumbHeight, travel, minimum);
      }

      @Override
      public String toString() {
         return "ScrollBarMetrics[" + "thumbY=" + this.thumbY + ", " + "thumbHeight=" + this.thumbHeight + ", " + "travel=" + this.travel + ", " + "minimum=" + this.minimum + "]";
      }

      private ScrollBarMetrics(float var1, float var2, float var3, float var4) {
         this.thumbY = var1;
         this.thumbHeight = var2;
         this.travel = var3;
         this.minimum = var4;
      }
   }
}

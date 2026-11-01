package org.zenith.client.screens.nlgui.panel.api;

import java.util.List;
import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public abstract class ElementPanel extends Panel {
   public abstract void renderHeader(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7);

   public abstract void close();

   public abstract List<? extends Element> getElements();

   public void renderPriority(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
   }

   public float getButtonWidth() {
      return 0.0F;
   }

   public float getHeaderTitleWidth() {
      return 0.0F;
   }

   public float getContentWidth() {
      return 380.0F;
   }

   public float extraWidth() {
      return 0.0F;
   }

   public boolean hasLeftColumn() {
      return false;
   }

   public void renderLeftColumn(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8) {
   }

   public boolean isHeaderSearchOnly() {
      return false;
   }

   public void renderHeaderButtons(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8) {
   }

   public boolean onHeaderButtonsClicked(double var1, double var3, Enum3 var5) {
      return false;
   }

   public boolean onMouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return false;
   }

   public boolean isRightDrawerOpen() {
      return false;
   }

   public void closeRightDrawer() {
   }

   public boolean isRender() {
      return false;
   }

   public void tick() {
   }

   public void renderRightPanel(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
   }

   public BoundsData getRightPanelBlurBounds(float var1, float var2, float var3, float var4) {
      return null;
   }

   public float getRightPanelBlurProgress(float var1, float var2) {
      return var1 * var2;
   }
}

package org.zenith.client.screens.override.main.page;

import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public interface MainMenuPage {
   float contentWidth();

   float contentHeight();

   default float footerHeight() {
      return 0.0F;
   }

   default float footerWidth() {
      return this.contentWidth();
   }

   default void update() {
   }

   void layout(BoundsData var1);

   void render(MenuDrawContext var1, float var2, float var3, BoundsData var4);

   default void renderFooter(MenuDrawContext var1, float var2, float var3, BoundsData var4) {
   }

   default void onShow() {
   }

   default void onHide() {
   }

   default void tick() {
   }

   default boolean mouseClicked(double var1, double var3, int var5) {
      return false;
   }

   default boolean mouseReleased(double var1, double var3, int var5) {
      return false;
   }

   default boolean mouseDragged(double var1, double var3, int var5) {
      return false;
   }

   default boolean mouseScrolled(double var1, double var3, double var5) {
      return false;
   }

   default boolean keyPressed(int var1, int var2, int var3) {
      return false;
   }

   default boolean charTyped(char var1, int var2) {
      return false;
   }
}

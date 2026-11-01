package org.zenith.client.screens.override.main.widget;

import lombok.Generated;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public abstract class MenuWidget {
   public float x;
   public float y;
   public float width;
   public float height;
   public final Animation hoverAnimation = new Animation(160L, Easing.INSTANCE12);
   public boolean hovered;
   public boolean visible = true;
   public boolean enabled = true;

   public void setBounds(float var1, float var2, float var3, float var4) {
      this.x = MenuTheme.snap(var1);
      this.y = MenuTheme.snap(var2);
      this.width = MenuTheme.snap(var1 + var3) - this.x;
      this.height = MenuTheme.snap(var2 + var4) - this.y;
   }

   public BoundsData bounds() {
      return new BoundsData(this.x, this.y, this.width, this.height);
   }

   public boolean contains(double var1, double var3) {
      return var1 >= this.x && var1 <= this.x + this.width && var3 >= this.y && var3 <= this.y + this.height;
   }

   public MenuWidget setVisible(boolean var1) {
      this.visible = var1;
      return this;
   }

   public MenuWidget setEnabled(boolean var1) {
      this.enabled = var1;
      return this;
   }

   public final void render(MenuDrawContext var1, float var2, float var3) {
      if (this.visible) {
         this.hovered = this.enabled && this.contains(var2, var3);
         this.hoverAnimation.method2807(this.hovered);
         this.draw(var1, var2, var3);
      }
   }

   public abstract void draw(MenuDrawContext var1, float var2, float var3);

   public boolean mouseClicked(double var1, double var3, int var5) {
      return false;
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return false;
   }

   public boolean mouseDragged(double var1, double var3, int var5) {
      return false;
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      return false;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return false;
   }

   public boolean charTyped(char var1, int var2) {
      return false;
   }

   public float hover() {
      return this.hoverAnimation.getValue();
   }

   @Generated
   public float getX() {
      return this.x;
   }

   @Generated
   public float getY() {
      return this.y;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public boolean isHovered() {
      return this.hovered;
   }

   @Generated
   public boolean isVisible() {
      return this.visible;
   }

   @Generated
   public boolean isEnabled() {
      return this.enabled;
   }
}

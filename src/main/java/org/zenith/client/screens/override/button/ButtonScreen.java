package org.zenith.client.screens.override.button;

import net.minecraft.client.input.KeyInput;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.Bounds;
import org.zenith.utility.render.display.base.MenuDrawContext;

public abstract class ButtonScreen {
   public Bounds bounds;

   public ButtonScreen(float var1, float var2) {
      this.bounds = new Bounds(0.0F, 0.0F, var1, var2);
   }

   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5) {
      this.bounds.setX(var4);
      this.bounds.setY(var5);
   }

   public float getWidth() {
      return this.bounds.getWidth();
   }

   public float getHeight() {
      return this.bounds.getHeight();
   }

   public void onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.bounds.contains(var1, var3)) {
         this.onClick(var1, var3, var5);
      }
   }

   public abstract void onClick(double var1, double var3, Enum3 var5);

   public void onMouseReleased(double var1, double var3, Enum3 var5) {
   }

   public void onMouseDragged(double var1, double var3, Enum3 var5, double var6, double var8) {
   }

   public boolean keyPressed(KeyInput var1) {
      return this.keyPressed(var1.key(), var1.scancode(), var1.modifiers());
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return false;
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      return false;
   }
}

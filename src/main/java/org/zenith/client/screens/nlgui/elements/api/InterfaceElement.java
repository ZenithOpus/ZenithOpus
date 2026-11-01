package org.zenith.client.screens.nlgui.elements.api;

import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.MenuDrawContext;

public abstract class InterfaceElement extends Element {
   @Override
   public abstract float getWidth();

   public void renderPriority(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
   }

   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, int var7) {
   }

   public boolean onMousePriorityClicked(double var1, double var3, Enum3 var5) {
      return false;
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      return false;
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      return false;
   }

   @Override
   public void onMouseReleased(double var1, double var3, Enum3 var5) {
      super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean keyPressed(KeyInput var1) {
      return this.keyPressed(var1.key(), var1.scancode(), var1.modifiers());
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      return super.keyPressed(new KeyInput(var1, var2, var3));
   }

   @Override
   public boolean charTyped(CharInput var1) {
      return this.charTyped((char)var1.codepoint(), var1.modifiers());
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return super.charTyped(new CharInput(var1, var2));
   }
}

package org.zenith.client.screens.nlgui.elements.api;

import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import org.zenith.MinecraftAccess;
import org.zenith.utility.misc.Enum3;

public abstract class Element implements MinecraftAccess {
   public abstract String getName();

   public boolean isVisible() {
      return true;
   }

   public abstract float getHeight();

   public abstract float getWidth();

   public abstract boolean onMouseClicked(double var1, double var3, Enum3 var5);

   public boolean keyPressed(KeyInput var1) {
      return this.keyPressed(var1.key(), var1.scancode(), var1.modifiers());
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return false;
   }

   public void onMouseReleased(double var1, double var3, Enum3 var5) {
   }

   public boolean charTyped(CharInput var1) {
      return this.charTyped((char)var1.codepoint(), var1.modifiers());
   }

   public boolean charTyped(char var1, int var2) {
      return false;
   }
}

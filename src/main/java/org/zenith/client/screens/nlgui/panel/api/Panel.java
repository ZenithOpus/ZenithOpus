package org.zenith.client.screens.nlgui.panel.api;

import org.zenith.MinecraftAccess;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.render.display.base.MenuDrawContext;

public abstract class Panel implements MinecraftAccess {
   public abstract void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6);

   public abstract boolean onMouseClicked(double var1, double var3, Enum3 var5);

   public boolean keyPressed(int var1, int var2, int var3) {
      return false;
   }

   public boolean onMouseReleased(double var1, double var3, Enum3 var5) {
      return false;
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      return false;
   }

   public boolean charTyped(char var1, int var2) {
      return false;
   }
}

package org.zenith.utility.render.display.base;

import lombok.Generated;
import net.minecraft.client.gui.DrawContext;

public class MenuDrawContext extends CustomDrawContext {
   private final int mouseX;
   private final int mouseY;
   private final float delta;

   public MenuDrawContext(DrawContext var1, int var2, int var3, float var4) {
      super(var1);
      this.mouseX = var2;
      this.mouseY = var3;
      this.delta = var4;
   }

   public static MenuDrawContext method8080(DrawContext var0, int var1, int var2, float var3) {
      return new MenuDrawContext(var0, var1, var2, var3);
   }

   @Generated
   public int getMouseX() {
      return this.mouseX;
   }

   @Generated
   public int getMouseY() {
      return this.mouseY;
   }

   @Generated
   public float getDelta() {
      return this.delta;
   }
}

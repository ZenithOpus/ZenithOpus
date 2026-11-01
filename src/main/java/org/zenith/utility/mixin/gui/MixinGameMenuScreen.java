package org.zenith.utility.mixin.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.screens.override.main.MenuSurface;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.client.screens.override.main.component.VanillaOptionStyle;
import org.zenith.utility.render.display.base.MenuDrawContext;

@Mixin({GameMenuScreen.class})
public class MixinGameMenuScreen {
   private static final float COLUMN = MenuTheme.px(320.0F);
   private static final float GAP = MenuTheme.px(8.0F);
   private static final float ROW_HEIGHT = MenuTheme.px(42.0F);
   private static final float PLATE_PADDING = MenuTheme.px(32.0F);
   private static final float PLATE_RADIUS = MenuTheme.px(24.0F);

   @Inject(
      method = {"init()V"},
      at = {@At("TAIL")}
   )
   private void zenith$relayout(CallbackInfo var1) {
      Screen var2 = (Screen)(Object)this;
      List<List<ClickableWidget>> var3 = this.zenith$rows(var2);
      if (!var3.isEmpty()) {
         float var4 = COLUMN * 2.0F + GAP;
         float var5 = var3.size() * ROW_HEIGHT + (var3.size() - 1) * GAP;
         float var6 = MenuTheme.snap((var2.width - var4) / 2.0F);
         float var7 = MenuTheme.snap((var2.height - var5) / 2.0F);

         for (Object var9_o : (Iterable<?>)(Object)(var3)) {
            List var9 = (List)var9_o;
            float var10 = var9.size() == 1 ? var4 : COLUMN;

            for (int var11 = 0; var11 < var9.size(); var11++) {
               ClickableWidget var12 = (ClickableWidget)var9.get(var11);
               var12.setX(Math.round(var6 + var11 * (COLUMN + GAP)));
               var12.setY(Math.round(var7));
               var12.setWidth(Math.round(var10));
               var12.setHeight(Math.round(ROW_HEIGHT));
            }

            var7 += ROW_HEIGHT + GAP;
         }
      }
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/gui/DrawContext;IIF)V"},
      at = {@At("HEAD")}
   )
   private void zenith$beginStyle(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      MenuDrawContext var6 = MenuDrawContext.method8080(var1, var2, var3, var4);
      MenuSurface.backdrop(var6, var6.getScaledWindowWidth(), var6.getScaledWindowHeight(), 0.45F);
      List<List<ClickableWidget>> var7 = this.zenith$rows((Screen)(Object)this);
      if (!var7.isEmpty()) {
         float var8 = Float.MAX_VALUE;
         float var9 = Float.MAX_VALUE;
         float var10 = -Float.MAX_VALUE;
         float var11 = -Float.MAX_VALUE;

         for (Object var13_o : (Iterable<?>)(Object)(var7)) {
            List<ClickableWidget> var13 = (List)var13_o;
            for (Object var15_o : (Iterable<?>)(Object)(var13)) {
               ClickableWidget var15 = (ClickableWidget)var15_o;
               var8 = Math.min(var8, (float)var15.getX());
               var9 = Math.min(var9, (float)var15.getY());
               var10 = Math.max(var10, (float)(var15.getX() + var15.getWidth()));
               var11 = Math.max(var11, (float)(var15.getY() + var15.getHeight()));
            }
         }

         MenuSurface.card(
            var6, var8 - PLATE_PADDING, var9 - PLATE_PADDING, var10 - var8 + PLATE_PADDING * 2.0F, var11 - var9 + PLATE_PADDING * 2.0F, PLATE_RADIUS
         );
      }

      VanillaOptionStyle.begin();
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/gui/DrawContext;IIF)V"},
      at = {@At("RETURN")}
   )
   private void zenith$endStyle(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      VanillaOptionStyle.end();
   }

   private List<List<ClickableWidget>> zenith$rows(Screen var1) {
      ArrayList var2 = new ArrayList();
      int var3 = Integer.MIN_VALUE;

      for (Element var5 : var1.children()) {
         if (var5 instanceof ClickableWidget var6 && var6.visible) {
            if (var6.getY() != var3 || var2.isEmpty()) {
               var2.add(new ArrayList());
               var3 = var6.getY();
            }

            ((List)var2.get(var2.size() - 1)).add(var6);
         }
      }

      return var2;
   }
}

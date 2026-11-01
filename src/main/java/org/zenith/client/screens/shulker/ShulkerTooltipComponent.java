package org.zenith.client.screens.shulker;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public final class ShulkerTooltipComponent implements TooltipComponent {
   private final DefaultedList<ItemStack> items;
   private static final Identifier shulkerGuiTexture = Identifier.of("zenith", "textures/container.png");
   private static final int guiWidth = 176;
   private static final int guiHeight = 67;
   private static final int slotOffsetX = 8;
   private static final int slotOffsetY = 7;

   public ShulkerTooltipComponent(DefaultedList<ItemStack> var1) {
      this.items = var1;
   }

   @Override
   public int getHeight(TextRenderer textRenderer) {
      return 67;
   }

   @Override
   public int getWidth(TextRenderer textRenderer) {
      return 176;
   }

   @Override
   public void drawText(DrawContext context, TextRenderer textRenderer, int x, int y) {
   }

   @Override
   public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
      context.drawTexture(RenderPipelines.GUI_TEXTURED, shulkerGuiTexture, x, y, 0.0F, 0.0F, 176, 67, 176, 67);

      for (int var7 = 0; var7 < 27; var7++) {
         ItemStack var8 = this.items.get(var7);
         if (!var8.isEmpty()) {
            int var9 = x + 8 + var7 % 9 * 18;
            int var10 = y + 7 + var7 / 9 * 18;
            context.drawItem(var8, var9, var10);
            context.drawStackOverlay(textRenderer, var8, var9, var10);
         }
      }
   }
}

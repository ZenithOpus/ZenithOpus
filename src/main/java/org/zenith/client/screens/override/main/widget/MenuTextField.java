package org.zenith.client.screens.override.main.widget;

import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuIcon;
import org.zenith.client.screens.override.main.MenuSkin;
import org.zenith.client.screens.override.main.MenuSurface;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class MenuTextField extends MenuWidget {
   public static final float HEIGHT = MenuSkin.HEADER_HEIGHT;
   private static final float RADIUS = MenuSkin.radius(HEIGHT);
   private static final float PADDING = MenuTheme.px(16.0F);
   private static final float GAP = MenuTheme.px(8.0F);
   private static final float ICON_SIZE = 12.0F;
   private static final float TEXT_SIZE = 12.0F;
   private final MenuIcon icon;
   private final String placeholder;
   private String value = "";
   private boolean focused;
   private int maxLength = 128;
   private Runnable onEnter;
   private Runnable onChange;
   private long caretResetTime = Util.getMeasuringTimeMs();

   public MenuTextField(MenuIcon var1, String var2) {
      this.icon = var1;
      this.placeholder = var2;
   }

   public void setValue(String var1) {
      this.value = var1 == null ? "" : var1;
      if (this.value.length() > this.maxLength) {
         this.value = this.value.substring(0, this.maxLength);
      }

      this.caretResetTime = Util.getMeasuringTimeMs();
      if (this.onChange != null) {
         this.onChange.run();
      }
   }

   public void clear() {
      this.setValue("");
   }

   public void setFocused(boolean var1) {
      this.focused = var1;
      this.caretResetTime = Util.getMeasuringTimeMs();
   }

   @Override
   public void draw(MenuDrawContext var1, float var2, float var3) {
      MenuSurface.plate(var1, this.x, this.y, this.width, this.height, RADIUS, MenuTheme.plate().method7295(MenuTheme.track(), this.hover() * 0.5F));
      if (this.focused) {
         var1.drawRoundedBorder(this.x, this.y, this.width, this.height, -0.1F, ProxyDotRadiusData.method8069(RADIUS), MenuTheme.accent().method7294(0.6F));
      }

      float var4 = this.x + PADDING;
      if (this.icon != null) {
         Font var5 = this.icon.font(12.0F);
         ZenithColor var6 = this.focused ? MenuTheme.accent() : MenuTheme.textTertiary();
         var1.drawText(var5, this.icon.glyph(), var4, this.y + (this.height - var5.height()) / 2.0F, var6);
         var4 += this.icon.width(12.0F) + GAP;
      }

      Font var12 = MenuTheme.medium(12.0F);
      float var13 = this.y + (this.height - var12.height()) / 2.0F;
      float var7 = this.x + this.width - PADDING - var4;
      boolean var8 = this.value.isEmpty();
      String var9 = var8 && !this.focused ? this.placeholder : this.value;
      ZenithColor var10 = var8 ? MenuTheme.textSecondary() : MenuTheme.text();

      while (var9.length() > 1 && var12.width(var9) > var7) {
         var9 = var9.substring(1);
      }

      var1.enableScissor(var4, this.y, this.x + this.width - PADDING, this.y + this.height);
      var1.drawText(var12, var9, var4, var13, var10);
      var1.disableScissor();
      if (this.focused && (Util.getMeasuringTimeMs() - this.caretResetTime) / 500L % 2L == 0L) {
         float var11 = var4 + var12.width(var9) + MenuTheme.px(1.0F);
         var1.drawRect(var11, var13, MenuTheme.px(1.0F), var12.height(), MenuTheme.accent());
      }
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.visible && this.enabled) {
         boolean var6 = this.contains(var1, var3);
         this.setFocused(var6 && var5 == 0);
         return var6;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.focused && this.visible && this.enabled) {
         switch (var1) {
            case 65:
               if ((var3 & 2) != 0) {
                  return true;
               }
               break;
            case 67:
               if ((var3 & 2) != 0) {
                  MinecraftClient.getInstance().keyboard.setClipboard(this.value);
                  return true;
               }
               break;
            case 86:
               if ((var3 & 2) != 0) {
                  this.setValue(this.value + MinecraftClient.getInstance().keyboard.getClipboard());
                  return true;
               }
               break;
            case 256:
               this.setFocused(false);
               return true;
            case 257:
            case 335:
               if (this.onEnter != null) {
                  this.onEnter.run();
               }

               return true;
            case 259:
               if (!this.value.isEmpty()) {
                  this.setValue(this.value.substring(0, this.value.length() - 1));
               }

               return true;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      if (!this.focused || !this.visible || !this.enabled) {
         return false;
      } else if (var1 >= ' ' && var1 != 127) {
         if (this.value.length() >= this.maxLength) {
            return true;
         } else {
            this.setValue(this.value + var1);
            return true;
         }
      } else {
         return false;
      }
   }

   @Generated
   public String getValue() {
      return this.value;
   }

   @Generated
   public boolean isFocused() {
      return this.focused;
   }

   @Generated
   public void setMaxLength(int var1) {
      this.maxLength = var1;
   }

   @Generated
   public void setOnEnter(Runnable var1) {
      this.onEnter = var1;
   }

   @Generated
   public void setOnChange(Runnable var1) {
      this.onChange = var1;
   }
}

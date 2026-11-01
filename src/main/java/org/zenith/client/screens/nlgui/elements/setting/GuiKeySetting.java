package org.zenith.client.screens.nlgui.elements.setting;

import net.minecraft.client.input.KeyInput;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.BindCapture;
import org.zenith.client.screens.nlgui.MouseBindTarget;
import org.zenith.client.screens.nlgui.elements.BindLabel;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public class GuiKeySetting extends GuiSetting<BindSetting> implements MouseBindTarget {
   private BoundsData bounds;
   private boolean binding;
   private final BindLabel bindLabel = new BindLabel();

   public GuiKeySetting(BindSetting var1) {
      super(166.0F, var1);
   }

   public GuiKeySetting(BindSetting var1, float var2) {
      super(var2, var1);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMousePriorityClicked(double var1, double var3, Enum3 var5) {
      return this.bindMouse(var5);
   }

   @Override
   public boolean bindMouse(Enum3 var1) {
      if (this.binding && MouseBindTarget.bindable(var1)) {
         this.setting.setKeyCode(var1.getIntValue());
         this.stopBinding();
         this.bindLabel.set();
         Zenith.getInstance().getClientSounds().method1566();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.bindMouse(var5)) {
         return true;
      } else if (this.bounds != null && this.bounds.contains(var1, var3)) {
         if (var5 == Enum3.RIGHT) {
            this.setting.setKeyCode(-1);
            this.stopBinding();
            this.bindLabel.cleared();
            Zenith.getInstance().getClientSounds().method1567();
         } else {
            this.binding = true;
            this.bindLabel.waiting();
            BindCapture.begin(this);
            Zenith.getInstance().getClientSounds().method1570(false);
         }

         return true;
      } else {
         if (this.binding) {
            this.stopBinding();
         }

         return false;
      }
   }

   @Override
   public boolean keyPressed(KeyInput var1) {
      return this.keyPressed(var1.key(), var1.scancode(), var1.modifiers());
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.binding) {
         if (var1 == 256) {
            this.setting.setBind(-1, 0);
            this.bindLabel.cleared();
            Zenith.getInstance().getClientSounds().method1567();
         } else {
            this.setting.setBind(var1, KeyName.method6700(var1, var3));
            this.bindLabel.set();
            Zenith.getInstance().getClientSounds().method1566();
         }

         this.stopBinding();
         return true;
      } else {
         return super.keyPressed(new KeyInput(var1, var2, var3));
      }
   }

   private void stopBinding() {
      this.binding = false;
      BindCapture.end(this);
   }

   @Override
   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      try {
         ZenithStyle var7 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         if (var7 == null) {
            return;
         }

         this.animationVisible.method2807(this.setting.isVisible());
         var6 *= this.animationVisible.getValue();
         Font var8 = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font var9 = Fonts.NEW_REGULAR.getFont(5.4F);
         float var10 = this.width / 1.4F - GuiStyle.PADDING.intValue();
         ZenithColor var11 = var7.getTextEnable().getZenithColor().method7294(var6);
         ZenithColor var12 = var7.getTextSecondary().getZenithColor().method7294(var6);
         ZenithColor var13 = var7.getPrimaryColor().getZenithColor().method7294(var6);
         this.drawDefault(var1, var2, var3, "N", this.setting.getName(), this.setting.getDescription(), var8, var9, var4, var5, var10, var11, var12, var13);
         Font var14 = Fonts.NEW_MEDIUM.getFont(5.01F);
         float var15 = var4 + this.width;
         int var16 = var2 >= var4 && var2 <= var15 && var3 >= var5 && var3 <= var5 + this.getHeight() ? 1 : 0;
         this.bindLabel
            .render(
               var1,
               var2,
               var3,
               var5,
               this.getHeight(),
               var15,
               BindLabel.text(this.binding, this.setting.getKeyCode(), this.setting.getModifiers()),
               this.binding,
               var14,
               var4 + var10,
               (var16 != 0),
               var6,
               null,
               var7
            );
         this.bounds = new BoundsData(var4, var5, this.width, this.getHeight());
      } catch (Exception var17) {
         var17.printStackTrace();
      }
   }
}

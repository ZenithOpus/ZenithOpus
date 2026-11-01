package org.zenith.client.screens.override.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Generated;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.override.button.ServerButton;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.ThemeStyle;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class MenuMultiPlayerScreen extends Screen implements MinecraftAccess {
   private final Screen parent;
   private final MultiplayerServerListPinger serverListPinger = new MultiplayerServerListPinger();
   private final List<ServerButton> serverButtons = new CopyOnWriteArrayList<>();
   private ServerList serverList;
   private float scrollY = 0.0F;

   public MenuMultiPlayerScreen(Screen var1) {
      super(Text.literal("Multiplayer"));
      this.parent = var1;
   }

   @Override
   public void init() {
      Zenith.getInstance().getMenuParticleRenderer().init();
      this.serverList = new ServerList(mc);
      this.serverList.loadFile();
      this.clearButtons();
      float var1 = 260.0F;
      float var2 = 30.0F;

      for (int var3 = 0; var3 < this.serverList.size(); var3++) {
         this.serverButtons.add(new ServerButton(this, this.serverList.get(var3), var1, var2));
      }
   }

   private void clearButtons() {
      for (ServerButton var2 : this.serverButtons) {
         var2.close();
      }

      this.serverButtons.clear();
   }

   @Override
   public void tick() {
      this.serverListPinger.tick();
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      MenuDrawContext var5 = MenuDrawContext.method8080(context, mouseX, mouseY, deltaTicks);
      this.renderBackgroundAndParticles(var5);
      this.renderServerList(var5, mouseX, mouseY);
   }

   private void renderBackgroundAndParticles(MenuDrawContext var1) {
      Window var2 = mc.getWindow();
      float var3 = var2.getScaledWidth();
      float var4 = var2.getScaledHeight();
      var1.drawTexture(Zenith.method1501("menu/background.png"), 0.0F, 0.0F, var3, var4, ZenithColor.INSTANCE);
      Zenith.getInstance().getMenuParticleRenderer().renderParticles(var1);
   }

   public void renderServerList(MenuDrawContext var1, float var2, float var3) {
      Window var4 = mc.getWindow();
      float var5 = var4.getScaledWidth();
      float var6 = var4.getScaledHeight();
      ThemeStyle var7 = ThemeStyle.INSTANCE;
      float var8 = 265.0F;
      float var9 = 209.0F;
      float var10 = (var5 - var8) / 2.0F;
      float var11 = (var6 - var9) / 2.0F;
      float var12 = 4.0F;
      float var13 = this.serverButtons.size() * (30.0F + var12);
      float var14 = Math.max(0.0F, var13 - var9);
      if (this.scrollY > 0.0F) {
         this.scrollY = 0.0F;
      }

      if (this.scrollY < -var14) {
         this.scrollY = -var14;
      }

      float var15 = var11 + this.scrollY;
      var1.enableScissor((int)var10, (int)var11, (int)(var10 + var8), (int)(var11 + var9));

      for (ServerButton var17 : this.serverButtons) {
         if (var15 + var17.getHeight() > var11 && var15 < var11 + var9) {
            var17.render(var1, var2, var3, var10 + (var8 - var17.getWidth()) / 2.0F, var15);
         }

         var15 += var17.getHeight() + var12;
      }

      var1.disableScissor();
      if (var13 > var9) {
         float var21 = 1.0F;
         float var23 = var10 + var8 + 4.0F - var21 - 2.0F;
         float var18 = var9 * (var9 / var13);
         float var19 = var11 + -this.scrollY / var13 * var9;
         var1.drawRoundedRect(var23, var11, var21, var9, ProxyDotRadiusData.INSTANCE, var7.getZenithColor12());
         var1.drawRoundedRect(var23, var19, var21, var18, ProxyDotRadiusData.INSTANCE, var7.getZenithColor13());
      }

      Font var22 = Fonts.ICONS.getFont(20.0F);
      float var24 = var22.height();
      String var25 = "J";
      float var26 = (var5 - var22.width(var25)) / 2.0F;
      float var20 = var11 - 32.0F - var24;
      var1.drawText(var22, var25, var26, var20, Zenith.getInstance().getStyleManager().getColorCycleIcon().method806());
   }

   @Override
   public boolean mouseClicked(Click click, boolean doubled) {
      return this.mouseClicked(click.x(), click.y(), click.button());
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      for (ServerButton var7 : this.serverButtons) {
         var7.onMouseClicked(var1, var3, Enum3.method6094(var5));
      }

      return super.mouseClicked(new Click(var1, var3, new MouseInput(var5, 0)), false);
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.scrollY += (float)(verticalAmount * 15.0);
      float var9 = Math.max(0, this.serverButtons.size() * 30);
      if (this.scrollY > 0.0F) {
         this.scrollY = 0.0F;
      }

      if (this.scrollY < -var9) {
         this.scrollY = -var9;
      }

      return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   @Override
   public void close() {
      mc.setScreen(this.parent);
   }

   @Override
   public void removed() {
      this.clearButtons();
      this.serverListPinger.cancel();
   }

   @Generated
   public MultiplayerServerListPinger getServerListPinger() {
      return this.serverListPinger;
   }
}

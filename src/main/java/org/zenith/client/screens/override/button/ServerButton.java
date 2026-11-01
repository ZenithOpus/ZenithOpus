package org.zenith.client.screens.override.button;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.ServerInfo.Status;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.network.NetworkingBackend;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.override.server.MenuMultiPlayerScreen;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.TextUtils;
import org.zenith.utility.misc.ThemeStyle;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class ServerButton extends ButtonScreen implements MinecraftAccess {
   private final ServerInfo serverInfo;
   private final WorldIcon icon;
   private final Animation hoverAnimation = new Animation(250L, Easing.INSTANCE10);
   private final MenuMultiPlayerScreen multiplayerScreen;
   private static final ThreadPoolExecutor PINGER_EXECUTOR = (ThreadPoolExecutor)Executors.newFixedThreadPool(5);
   private byte[] lastFavicon;
   private String pingText = "...";

   public ServerButton(MenuMultiPlayerScreen var1, ServerInfo var2, float var3, float var4) {
      super(var3, var4);
      this.multiplayerScreen = var1;
      this.serverInfo = var2;
      this.icon = WorldIcon.forServer(mc.getTextureManager(), var2.address);
      this.pingServer();
   }

   public void pingServer() {
      this.serverInfo.setStatus(Status.PINGING);
      PINGER_EXECUTOR.submit(() -> {
         try {
            this.multiplayerScreen.getServerListPinger().add(this.serverInfo, () -> {
               this.pingText = this.serverInfo.ping + "ms";
               this.updateFavicon();
            }, () -> {}, NetworkingBackend.remote(false));
         } catch (Exception var2) {
            this.serverInfo.setStatus(Status.UNREACHABLE);
            this.pingText = "Error";
         }
      });
   }

   private void updateFavicon() {
      byte[] var1 = this.serverInfo.getFavicon();
      if (!Arrays.equals(var1, this.lastFavicon) && var1 != null) {
         try {
            this.icon.load(NativeImage.read(var1));
            this.lastFavicon = var1;
         } catch (Exception var3) {
         }
      }
   }

   @Override
   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5) {
      super.render(var1, var2, var3, var4, var5);
      ThemeStyle var6 = ThemeStyle.INSTANCE;
      this.hoverAnimation.method2807(this.bounds.contains(var2, var3));
      var1.drawRoundedRect(
         var4,
         var5,
         this.getWidth(),
         this.getHeight(),
         ProxyDotRadiusData.method8069(6.0F),
         var6.getZenithColor12().method7295(var6.getZenithColor8(), this.hoverAnimation.getValue())
      );
      var1.drawRoundedBorder(var4, var5, this.getWidth(), this.getHeight(), -0.1F, ProxyDotRadiusData.method8069(4.0F), var6.getZenithColor13());
      this.updateFavicon();
      float var7 = 14.0F;
      float var8 = (this.getHeight() - var7) / 2.0F;
      Identifier var9 = this.icon.getTextureId();
      RenderShaders.method5294(
         MatrixUtils.method664(var1.getMatrices()), var9, var4 + var8, var5 + var8, var7, var7, ProxyDotRadiusData.method8069(2.0F), ZenithColor.INSTANCE
      );
      Font var10 = Fonts.MEDIUM.getFont(8.0F);
      Font var11 = Fonts.MEDIUM.getFont(7.0F);
      float var12 = var4 + var8 + var7 + 6.0F;
      var1.drawText(var10, this.serverInfo.name, var12, var5 + var8 - 1.0F, ZenithColor.INSTANCE);
      Text var13 = TextUtils.method2105(this.serverInfo.label != null ? this.serverInfo.label : Text.of(this.serverInfo.address));
      var1.enableScissor((int)var12, 0, (int)(var12 + 170.0F), mc.getWindow().getScaledHeight());
      var1.drawText(var11, var13, var12, var5 + var8 + var7 - var11.height(), var6.getZenithColor7().method7287());
      var1.disableScissor();
      String var14 = this.serverInfo.playerCountLabel != null ? this.serverInfo.playerCountLabel.getString() : "0/0";
      float var15 = var11.width(this.pingText);
      var1.drawText(var11, this.pingText, var4 + this.getWidth() - var15 - 5.0F, var5 + var8 - 1.0F, var6.getZenithColor6());
      var1.drawText(var11, var14, var4 + this.getWidth() - var11.width(var14) - 5.0F, var5 + var8 + 10.0F, var6.getZenithColor6());
      RenderShaders.method5277(
         MatrixUtils.method664(var1.getMatrices()),
         var4,
         var5,
         this.getWidth(),
         this.getHeight(),
         0.1F,
         15.0F,
         ZenithColor.INSTANCE8
            .method7295(Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor(), this.hoverAnimation.getValue()),
         ProxyDotRadiusData.method8069(4.0F)
      );
   }

   @Override
   public void onClick(double var1, double var3, Enum3 var5) {
      if (var5 == Enum3.LEFT) {
         ServerAddress var6 = ServerAddress.parse(this.serverInfo.address);
         ConnectScreen.connect(mc.currentScreen, mc, var6, this.serverInfo, false, null);
      }
   }

   public void close() {
      this.icon.close();
   }
}

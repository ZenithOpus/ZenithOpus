package org.zenith.client.screens.override.particle;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.client.events.WindowResizeEvent;
import org.zenith.utility.misc.Handler1;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.MenuDrawContext;

public class MenuParticleRenderer implements MinecraftAccess {
   private static final Identifier GLOW = Zenith.method1501("textures/glow.png");
   private final List<MenuParticleRenderer.MenuParticle> particles = new ArrayList<>();

   public MenuParticleRenderer() {
      for (int var1 = 0; var1 < 100; var1++) {
         this.particles.add(new MenuParticleRenderer.MenuParticle(1920.0F, 1080.0F, true));
      }

      EventManager.register(this);
   }

   @EventTarget
   public void resize(WindowResizeEvent var1) {
      this.init();
   }

   public void init() {
      this.particles.clear();

      for (int var1 = 0; var1 < 100; var1++) {
         this.particles.add(new MenuParticleRenderer.MenuParticle(mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), true));
      }
   }

   public static void renderBackgroundAndParticles(MenuDrawContext var0) {
      Window var1 = mc.getWindow();
      float var2 = var1.getScaledWidth();
      float var3 = var1.getScaledHeight();
      ZenithColor var4 = Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor();
      ZenithColor var5 = Zenith.getInstance().getStyleManager().getCurrentStyle().getSecondaryPrimaryColor().getZenithColor();
      RenderShaders.method5261(MatrixUtils.method664(var0.getMatrices()), var2, var3, var4, var5, 1.0F);
      Zenith.getInstance().getMenuParticleRenderer().renderParticles(var0, getScreenFadeProgress());
   }

   public static float getScreenFadeProgress() {
      Screen var0 = mc.currentScreen;
      if (var0 == null) {
         return 1.0F;
      } else {
         Handler1 var1 = (Handler1)var0;
         long var2 = var1.zenithDLC$callGetStartTime();
         long var4 = System.currentTimeMillis();
         float var6 = (float)(var4 - var2) / 200.0F;
         return Math.max(0.0F, Math.min(1.0F, var6));
      }
   }

   public void renderParticles(MenuDrawContext var1) {
      this.renderParticles(var1, 1.0F);
   }

   public void renderParticles(MenuDrawContext var1, float var2) {
      ZenithColor var3 = Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7293(255);
      Window var4 = mc.getWindow();
      float var5 = var4.getScaledWidth();
      float var6 = var4.getScaledHeight();
      ZenithColor var7 = ZenithColor.INSTANCE.method7295(var3, 0.55F);

      for (MenuParticleRenderer.MenuParticle var9 : this.particles) {
         var9.update(var5, var6, var1.getDelta());
         int var10 = (int)(var9.alpha * 180.0F);
         ZenithColor var11 = var7.method7293((int)(var10 * var2));
         var1.drawTexture(GLOW, var9.x, var9.y, var9.size, var9.size, var11);
      }
   }

   private class MenuParticle {
      public float x;
      public float y;
      public float size;
      public float speed;
      public float time;
      public float maxTime;
      public float alpha;

      public MenuParticle(float var2, float var3, boolean var4) {
         this.reset(var2, var3, var4);
      }

      public void reset(float var1, float var2, boolean var3) {
         this.size = 2 + (int)(Math.random() * 4.0);
         this.speed = 2.0F + (float)(Math.random() * 0.8F);
         this.maxTime = 100.0F + (float)(Math.random() * 200.0);
         this.time = var3 ? (float)(Math.random() * this.maxTime) : 0.0F;
         if (var3) {
            this.x = (float)(Math.random() * var1);
            this.y = (float)(Math.random() * var2);
         } else if (Math.random() > 0.5) {
            this.x = -20.0F;
            this.y = (float)(Math.random() * var2);
         } else {
            this.x = (float)(Math.random() * var1);
            this.y = -20.0F;
         }
      }

      public void update(float var1, float var2, float var3) {
         this.x = this.x + this.speed * var3;
         this.y = this.y + this.speed * var3;
         this.time += var3;
         float var4 = this.maxTime * 0.2F;
         if (this.time < var4) {
            this.alpha = this.time / var4;
         } else if (this.time > this.maxTime - var4) {
            this.alpha = (this.maxTime - this.time) / var4;
         } else {
            this.alpha = 1.0F;
         }

         this.alpha = Math.max(0.0F, Math.min(1.0F, this.alpha));
         if (this.time >= this.maxTime || this.x > var1 + 50.0F || this.y > var2 + 50.0F) {
            this.reset(var1, var2, false);
         }
      }
   }
}

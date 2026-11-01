package org.zenith.client.screens.override.main;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.MinecraftClient;
import org.joml.Matrix4f;
import org.zenith.MatrixUtils;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.render.KawaseBlur;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.ShapeRenderer;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class MenuSurface {
   private static final float BLUR = MenuTheme.px(72.0F);
   private static final float GLASS_SQUIRT = 10.0F;
   private static final float GLASS_POWER = 0.08F;
   private static MainMenuBlurRenderer backgroundBlur;
   private static GpuTextureView menuSource;

   public static void useBackgroundBlur(MainMenuBlurRenderer var0) {
      backgroundBlur = var0;
   }

   public static void clearBackgroundBlur() {
      backgroundBlur = null;
   }

   private MenuSurface() {
   }

   public static void card(MenuDrawContext var0, float var1, float var2, float var3, float var4, float var5) {
      card(var0, var1, var2, var3, var4, var5, MenuTheme.panel(), null);
   }

   public static void plate(MenuDrawContext var0, float var1, float var2, float var3, float var4, float var5, ZenithColor var6) {
      var0.drawRoundedRect(var1, var2, var3, var4, ProxyDotRadiusData.method8069(var5), var6);
   }

   public static void card(MenuDrawContext var0, float var1, float var2, float var3, float var4, float var5, ZenithColor var6, ZenithColor var7) {
      ProxyDotRadiusData var8 = ProxyDotRadiusData.method8069(var5);
      blur(var0, var1, var2, var3, var4, var8, 1.0F);
      var0.drawRoundedRect(var1, var2, var3, var4, var8, var6);
      if (var7 != null) {
         var0.drawRoundedBorder(var1, var2, var3, var4, -0.1F, var8, var7);
      }
   }

   public static void setMenuSource(GpuTextureView var0) {
      menuSource = var0;
   }

   public static void blur(MenuDrawContext var0, float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, float var6) {
      if (!(var6 <= 0.01F) && !(var3 <= 0.0F) && !(var4 <= 0.0F)) {
         MsdfRenderer.flushBatch();
         ZenithColor var7 = ZenithColor.INSTANCE.method7294(var6);
         Matrix4f var8 = MatrixUtils.method664(var0.getMatrices());
         if (MinecraftClient.getInstance().world == null && menuSource != null) {
            RenderShaders.gpuTextureView = menuSource;

            try {
               RenderShaders.method5264(var8, var1, var2, var3, var4, 10.0F, 0.08F, var5, var7);
            } finally {
               RenderShaders.gpuTextureView = null;
            }
         } else if (backgroundBlur != null) {
            backgroundBlur.render(var8, var1, var2, var3, var4, var5, var7);
         } else {
            KawaseBlur var9 = Zenith.getInstance().getKawaseBlur();
            if (var9 != null) {
               if (MinecraftClient.getInstance().world != null) {
                  var9.method7353(var8, var1, var2, var3, var4, var5, var7);
               } else {
                  var9.method7355(var8, var1, var2, var3, var4, var5, var7);
               }
            }
         }
      }
   }

   public static void backdrop(MenuDrawContext var0, float var1, float var2, float var3) {
      blur(var0, 0.0F, 0.0F, var1, var2, ProxyDotRadiusData.INSTANCE, 1.0F);
      if (var3 > 0.0F) {
         var0.drawRect(0.0F, 0.0F, var1, var2, ZenithColor.INSTANCE2.method7294(var3));
      }
   }

   public static void glow(MenuDrawContext var0, float var1, float var2, float var3, float var4, float var5, ZenithColor var6, float var7) {
      if (!(var7 <= 0.01F)) {
         ShapeRenderer.method7167(
            MatrixUtils.method664(var0.getMatrices()),
            var1,
            var2,
            var3,
            var4,
            (int)MenuTheme.px(24.0F),
            ProxyDotRadiusData.method8069(var5),
            GlowColor.method8097(var6.method7294(var7))
         );
      }
   }
}

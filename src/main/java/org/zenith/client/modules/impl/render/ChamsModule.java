package org.zenith.client.modules.impl.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.gl.ScissorState;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.modules.impl.combat.AntiBotModule;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.KawaseShaderKey;
import org.zenith.utility.render.BlitUtils;
import org.zenith.utility.render.ChamsRenderLayer;
import org.zenith.utility.render.HandShaders;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.LightUtil;
import org.zenith.utility.render.ShaderProgram;
import org.zenith.utility.render.UniformBinder;

@ModuleInfo(
   name = "Chams",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.chams.description"
)
public final class ChamsModule extends Module {
   public static final ChamsModule INSTANCE = new ChamsModule();
   private static final KawaseShaderKey kawaseShaderKey = new KawaseShaderKey(
      Zenith.method1501("core/chams_entity/data"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
   );
   private static RenderLayer renderLayer;
   private static final KawaseShaderKey kawaseShaderKey2 = new KawaseShaderKey(
      Zenith.method1501("core/chams_oil/data"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
   );
   private static final KawaseShaderKey kawaseShaderKey3 = new KawaseShaderKey(
      Zenith.method1501("core/chams_frost/data"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
   );
   private static final KawaseShaderKey kawaseShaderKey4 = new KawaseShaderKey(
      Zenith.method1501("core/chams_fracture/data"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
   );
   private static final KawaseShaderKey kawaseShaderKey5 = new KawaseShaderKey(
      Zenith.method1501("core/chams_liquid/data"), VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
   );
   private static RenderLayer renderLayer2;
   private static RenderLayer renderLayer3;
   private static RenderLayer renderLayer4;
   private static RenderLayer renderLayer5;
   private static final float CONST_floatValue = 1.0F;
   private static final float CONST_floatValue2 = 0.0F;
   private static final float CONST_floatValue3 = 1.8F;
   private static final float CONST_floatValue4 = 1.15F;
   private static final float CONST_floatValue5 = 3.4F;
   private static final float CONST_floatValue6 = 0.54F;
   private static final long CONST_longValue = 9000L;
   private static final float CONST_floatValue7 = 0.1F;
   private static final float CONST_floatValue8 = 0.72F;
   private static final int CONST_intValue = 2;
   private static final Object obj = new Object();
   private static final int[] CONST_intValues = new int[4];
   private static SimpleFramebuffer simpleFramebuffer;
   private static SimpleFramebuffer simpleFramebuffer2;
   private static SimpleFramebuffer simpleFramebuffer3;
   private static long longValue = Long.MIN_VALUE;
   private static ShaderProgram shaderProgram;
   private static ShaderProgram shaderProgram2;
   private static boolean flag;
   private static boolean flag2;
   private static final Set<KawaseShaderKey> items = ConcurrentHashMap.newKeySet();
   private final ModeSetting texture = new ModeSetting(
      "module.chams.texture",
      "module.chams.texture.desc",
      "module.chams.texture.glass",
      "module.chams.texture.oil",
      "module.chams.texture.frost",
      "module.chams.texture.fracture",
      "module.chams.texture.liquid"
   );
   private final SliderSetting blurStrength = new SliderSetting(
      "module.chams.blurStrength", 8.3F, 0.0F, 20.0F, 0.05F, "module.chams.blurStrength.desc", "", this::method5615, null
   );
   private final MultiSelectSetting targets = new MultiSelectSetting(
      "module.chams.targets",
      "module.chams.targets.desc",
      MultiSelectSetting.Entry.method5231("module.chams.players", true),
      MultiSelectSetting.Entry.method5231("module.chams.self", false),
      MultiSelectSetting.Entry.method5231("module.chams.friends", true)
   );
   private final ModeSetting colorMode = new ModeSetting(
      "module.chams.colorMode",
      "module.chams.colorMode.desc",
      this::method5614,
      "module.chams.color.rainbow",
      "module.chams.color.client",
      "module.chams.color.custom"
   );
   private final ColorSetting customColor = new ColorSetting(
      "module.chams.customColor", "module.chams.customColor.desc", ZenithColor.INSTANCE, this::method5613
   );
   private final BooleanSetting secondColor = new BooleanSetting("module.chams.secondColor", "module.chams.secondColor.desc", false, this::method5613);
   private final ColorSetting colorSetting = new ColorSetting(
      "module.chams.customSecondColor",
      "module.chams.customSecondColor.desc",
      new ZenithColor(170, 120, 255),
      () -> this.method5613() && this.secondColor.isEnabled()
   );
   private final SliderSetting brightness = new SliderSetting("module.chams.brightness", 2.15F, 1.0F, 3.0F, 0.05F, "module.chams.brightness.desc", "");
   public static volatile boolean flag3;
   private static final long CONST_longValue2 = 100000L;

   private ChamsModule() {
   }

   @Override
   public void onDisable() {
      super.onDisable();
      synchronized (obj) {
         method5634(simpleFramebuffer);
         method5634(simpleFramebuffer2);
         method5634(simpleFramebuffer3);
         simpleFramebuffer = null;
         simpleFramebuffer2 = null;
         simpleFramebuffer3 = null;
         longValue = Long.MIN_VALUE;
      }

      method5625();
   }

   public void method5599() {
      this.method5617();
   }

   @Override
   public ModulePreviewStage.Kind previewKind() {
      return ModulePreviewStage.Kind.CHAMS;
   }

   public boolean method5600(LivingEntityRenderState var1) {
      if (!flag3) {
         if (this.isEnabled() && mc.player != null && mc.world != null && var1 instanceof PlayerEntityRenderState var2) {
            return mc.world.getEntityById(var2.id) instanceof AbstractClientPlayerEntity var3 ? this.method5609(var3) : false;
         } else {
            return false;
         }
      } else {
         return this.isEnabled() && var1 instanceof PlayerEntityRenderState;
      }
   }

   @Nullable
   public RenderLayer method5601(Identifier var1) {
      if (this.texture.isSelected(1)) {
         return renderLayer2 != null ? renderLayer2 : (renderLayer2 = method5616("zenith_chams_oil", kawaseShaderKey2));
      } else if (this.texture.isSelected(2)) {
         return renderLayer3 != null ? renderLayer3 : (renderLayer3 = method5616("zenith_chams_frost", kawaseShaderKey3));
      } else if (this.texture.isSelected(3)) {
         return renderLayer4 != null ? renderLayer4 : (renderLayer4 = method5616("zenith_chams_fracture", kawaseShaderKey4));
      } else if (this.texture.isSelected(4)) {
         return renderLayer5 != null ? renderLayer5 : (renderLayer5 = method5616("zenith_chams_liquid", kawaseShaderKey5));
      } else {
         if (renderLayer == null) {
            renderLayer = method5616("zenith_chams_glass", kawaseShaderKey);
         }

         return renderLayer;
      }
   }

   public boolean method5602() {
      return this.method5606() && this.method5608();
   }

   public int method5603(int var1) {
      int var2 = Math.clamp((long)Math.round(255.0F), 0, 255);
      return var2 << 24 | var1 & 16777215;
   }

   public int method5604(int var1) {
      int[] var2 = this.method5629();
      int var3 = var2.length > 0 ? var2[0] : var1;
      return this.method5603(var3);
   }

   public static ChamsModule.Entry method5605() {
      return ChamsModule.Entry.method5635();
   }

   private boolean method5606() {
      KawaseShaderKey var1 = this.method5607();
      if (var1 == kawaseShaderKey && flag2) {
         return true;
      } else if (items.contains(var1)) {
         return true;
      } else {
         boolean var2 = LightUtil.method6712(var1.getIdentifier());
         if (var1 == kawaseShaderKey) {
            flag2 = var2;
         } else if (var2) {
            items.add(var1);
         }

         return var2;
      }
   }

   private KawaseShaderKey method5607() {
      if (this.texture.isSelected(1)) {
         return kawaseShaderKey2;
      } else if (this.texture.isSelected(2)) {
         return kawaseShaderKey3;
      } else if (this.texture.isSelected(3)) {
         return kawaseShaderKey4;
      } else {
         return this.texture.isSelected(4) ? kawaseShaderKey5 : kawaseShaderKey;
      }
   }

   private boolean method5608() {
      synchronized (obj) {
         return longValue == Long.MIN_VALUE ? false : simpleFramebuffer != null && simpleFramebuffer.getColorAttachment() != null;
      }
   }

   private boolean method5609(AbstractClientPlayerEntity var1) {
      if (!var1.isAlive() || var1.isSpectator()) {
         return false;
      } else if (var1 == mc.player) {
         return this.method5611() && mc.options.getPerspective() != Perspective.FIRST_PERSON;
      } else if (!this.method5610()) {
         return false;
      } else {
         return !this.method5612() && Zenith.getInstance().getFriends().method7626(var1)
            ? false
            : !AntiBotModule.INSTANCE.isEnabled() || !AntiBotModule.INSTANCE.method2785(var1);
      }
   }

   private boolean method5610() {
      return this.targets.method5227(0);
   }

   private boolean method5611() {
      return this.targets.method5227(1);
   }

   private boolean method5612() {
      return this.targets.method5227(2);
   }

   private boolean method5613() {
      return this.method5614() && this.colorMode.isSelected(2);
   }

   private boolean method5614() {
      return !this.method5615();
   }

   private boolean method5615() {
      return this.texture.isSelected(0);
   }

   @Nullable
   private static RenderLayer method5616(String var0, KawaseShaderKey var1) {
      RenderState.Data var2 = RenderState.method1906();
      RenderState.method1871();
      RenderState.method1873();
      RenderState.method1877();
      RenderState.method1878();
      RenderState.method1882(true);
      RenderPipeline var3 = var1.method5561(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, DrawMode.QUADS, RenderState.method1904());
      RenderState.method1907(var2);
      if (var1 == kawaseShaderKey) {
         flag2 = var1.text3 == null;
      }

      UniformBinder.method5480(var3, var1, () -> {
         int[] var1x = INSTANCE.method5629();
         method5631(var1, "iTime", INSTANCE.method5627());
         method5633(var1, "PrimaryColor", var1x[0]);
         method5633(var1, "SecondaryColor", var1x[1]);
         method5631(var1, "AlphaMul", 1.0F);
         method5631(var1, "FresnelPower", 1.8F);
         method5631(var1, "Freq", INSTANCE.method5628());
         method5631(var1, "ColorTransfer", var1 == kawaseShaderKey ? 0.0F : 0.75F);
         method5631(var1, "Brightness", INSTANCE.brightness.getValue3());
         method5631(var1, "MirrorEnabled", 1.0F);
         method5632(var1, mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight());
      }, INSTANCE::method5618, INSTANCE::isEnabled);
      return ChamsRenderLayer.method3202(var0, var3, null, true, true, true);
   }

   private void method5617() {
      if (mc.world != null && mc.getWindow() != null && mc.getFramebuffer() != null) {
         synchronized (obj) {
            long var2 = this.method5626();
            if (longValue != var2) {
               ChamsModule.Entry var4 = method5605();

               try {
                  int var5 = Math.max(1, mc.getFramebuffer().textureWidth);
                  int var6 = Math.max(1, mc.getFramebuffer().textureHeight);
                  this.method5619(Math.max(1, var5 / 2), Math.max(1, var6 / 2));
                  if (simpleFramebuffer != null) {
                     BlitUtils.method6453(mc.getFramebuffer(), simpleFramebuffer);
                     if (this.method5615()) {
                        this.method5621();
                     }

                     longValue = var2;
                     return;
                  }
               } finally {
                  var4.restore();
               }
            }
         }
      }
   }

   private GpuTextureView method5618() {
      synchronized (obj) {
         return BlitUtils.method6450(simpleFramebuffer);
      }
   }

   private void method5619(int var1, int var2) {
      int var3 = Math.max(1, var1 / 2);
      int var4 = Math.max(1, var2 / 2);
      int var5 = Math.max(1, var1 / 4);
      int var6 = Math.max(1, var2 / 4);
      if (simpleFramebuffer == null
         || simpleFramebuffer2 == null
         || simpleFramebuffer3 == null
         || simpleFramebuffer.textureWidth != var1
         || simpleFramebuffer.textureHeight != var2
         || simpleFramebuffer2.textureWidth != var3
         || simpleFramebuffer2.textureHeight != var4
         || simpleFramebuffer3.textureWidth != var5
         || simpleFramebuffer3.textureHeight != var6) {
         method5634(simpleFramebuffer);
         method5634(simpleFramebuffer2);
         method5634(simpleFramebuffer3);
         simpleFramebuffer = BlitUtils.method6448("zenith chams copy", var1, var2, false);
         simpleFramebuffer2 = BlitUtils.method6448("zenith chams blur 1", var3, var4, false);
         simpleFramebuffer3 = BlitUtils.method6448("zenith chams blur 2", var5, var6, false);
         method5620(simpleFramebuffer);
         method5620(simpleFramebuffer2);
         method5620(simpleFramebuffer3);
         longValue = Long.MIN_VALUE;
      }
   }

   private static void method5620(SimpleFramebuffer var0) {
   }

   private void method5621() {
      if (simpleFramebuffer != null && simpleFramebuffer2 != null && simpleFramebuffer3 != null && method5624()) {
         RenderState.method1872();
         RenderState.method1877();
         RenderState.method1879();
         RenderState.method1882(false);
         ScissorState var1 = RenderSystem.getScissorStateForRenderTypeDraws();
         boolean var2 = var1.isEnabled();
         int var3 = var1.getX();
         int var4 = var1.getY();
         int var5 = var1.getWidth();
         int var6 = var1.getHeight();
         RenderState.method1901();

         try {
            this.method5622();
         } finally {
            if (var2) {
               RenderState.method1900(var3, var4, var5, var6);
            }
         }
      }
   }

   private void method5622() {
      float var1 = Math.max(0.0F, this.blurStrength.getValue3());
      float var2 = Math.max(0.05F, 3.4F) * (0.35F + var1 * 0.08F) * 0.54F;
      this.method5623(shaderProgram, simpleFramebuffer, simpleFramebuffer2, var2);
      this.method5623(shaderProgram, simpleFramebuffer2, simpleFramebuffer3, var2);
      this.method5623(shaderProgram2, simpleFramebuffer3, simpleFramebuffer2, var2);
      this.method5623(shaderProgram2, simpleFramebuffer2, simpleFramebuffer, var2);
   }

   private void method5623(ShaderProgram var1, SimpleFramebuffer var2, SimpleFramebuffer var3, float var4) {
      if (var1 != null && var2 != null && var3 != null) {
         var1.method5517();
         var1.method5518("image", 0);
         var1.method5519("offset", var4);
         var1.method5521("resolution", 1.0F / Math.max(1.0F, (float)var3.textureWidth), 1.0F / Math.max(1.0F, (float)var3.textureHeight));
         RenderState.method1889(0, BlitUtils.method6450(var2));
         ImmediateRenderer.method8013(var3);
         HandShaders.method6282();
         RenderState.method1893(0);
         var1.unbind();
      }
   }

   private static boolean method5624() {
      if (shaderProgram != null && shaderProgram2 != null) {
         return true;
      } else if (flag) {
         return false;
      } else {
         try {
            shaderProgram = new ShaderProgram("chams_kawase", "kawase_down", "kawase");
            shaderProgram2 = new ShaderProgram("chams_kawase", "kawase_up", "kawase");
            return true;
         } catch (RuntimeException var1) {
            method5625();
            flag = true;
            return false;
         }
      }
   }

   private static void method5625() {
      if (shaderProgram != null) {
         shaderProgram.method5528();
         shaderProgram = null;
      }

      if (shaderProgram2 != null) {
         shaderProgram2.method5528();
         shaderProgram2 = null;
      }

      flag = false;
   }

   private long method5626() {
      if (mc.world == null) {
         return Long.MIN_VALUE;
      } else {
         long var1 = mc.world.getTime();
         int var3 = Float.floatToIntBits(mc.getRenderTickCounter().getTickProgress(false));
         return var1 << 32 ^ var3 & 4294967295L;
      }
   }

   private float method5627() {
      if (mc.world == null) {
         return 0.0F;
      } else {
         float var1 = mc.getRenderTickCounter().getTickProgress(false);
         double var2 = (double)(mc.world.getTime() % 100000L) + var1;
         return (float)(var2 * 0.05 * 1.15F);
      }
   }

   private float method5628() {
      return 0.004675F;
   }

   private int[] method5629() {
      if (this.colorMode.isSelected(0)) {
         float var4 = (float)(System.currentTimeMillis() % 9000L) / 9000.0F;
         return new int[]{method5630(var4), method5630(var4 + 0.1F)};
      } else if (this.colorMode.isSelected(1)) {
         int var3 = Zenith.getInstance().getStyleManager().getClientColor(0).method7287();
         int var5 = Zenith.getInstance().getStyleManager().getClientColor(180).method7287();
         return new int[]{var3, var5};
      } else {
         int var1 = this.customColor.method1445();
         int var2 = this.secondColor.isEnabled() ? this.colorSetting.method1445() : var1;
         return new int[]{var1, var2};
      }
   }

   private static int method5630(float var0) {
      float var1 = var0 - (float)Math.floor(var0);
      return ZenithColor.method7297(var1, 0.72F, 1.0F).method7287();
   }

   private static void method5631(KawaseShaderKey var0, String var1, float var2) {
      var0.method5166(var1).method7412(var2);
   }

   private static void method5632(KawaseShaderKey var0, float var1, float var2) {
      var0.method5166("ScreenSize").method7413(var1, var2);
   }

   private static void method5633(KawaseShaderKey var0, String var1, int var2) {
      var0.method5166(var1).method7414(ColorUtils.method6424(var2), ColorUtils.method6425(var2), ColorUtils.method6426(var2));
   }

   private static void method5634(SimpleFramebuffer var0) {
      if (var0 != null) {
         var0.delete();
      }
   }

   public static final class Entry {
      private final RenderState.Data data = RenderState.method1906();

      private static ChamsModule.Entry method5635() {
         return new ChamsModule.Entry();
      }

      public void restore() {
         RenderState.method1907(this.data);
      }
   }
}

package org.zenith.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.util.Arm;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.mixin.accessors.GameRendererAccessor;
import org.zenith.utility.mixin.accessors.MinecraftClientAccessor;
import org.zenith.utility.render.BlitUtils;
import org.zenith.utility.render.HandShaders;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.OutlineVertexProvider;
import org.zenith.utility.render.ShaderProgram;
import org.zenith.utility.render.WardrobeRenderer;

@ModuleInfo(
   name = "ShaderHand",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.shaderhand.description"
)
public final class ShaderHandModule extends Module {
   public static final ShaderHandModule INSTANCE = new ShaderHandModule();
   private static final float CONST_floatValue = 0.72F;
   private static final float CONST_floatValue2 = 0.72F;
   private static final float CONST_floatValue3 = 0.28F;
   private static final float CONST_floatValue4 = 0.22F;
   private static final float CONST_floatValue5 = 5.0F;
   private static final float CONST_floatValue6 = 1.0F;
   private static final float CONST_floatValue7 = 2.15F;
   private static final float CONST_floatValue8 = 200.0F;
   private static final float CONST_floatValue9 = 1.0F;
   private final ModeSetting shaderMode = new ModeSetting(
      "module.shaderHand.shaderMode", "module.shaderHand.shaderMode.desc", ShaderHandModule.Type.method1265()
   );
   private final SliderSetting fireAlpha = new SliderSetting(
      "module.shaderHand.fireAlpha", 0.86F, 0.1F, 1.0F, 0.01F, "module.shaderHand.fireAlpha.desc", "", this::method1253, null
   );
   private final BooleanSetting fireUnderHand = new BooleanSetting(
      "module.shaderHand.fireUnderHand", "module.shaderHand.fireUnderHand.desc", false, this::method1253
   );
   private final ModeSetting colorMode = new ModeSetting(
      "module.shaderHand.colorMode",
      "module.shaderHand.colorMode.desc",
      this::method1262,
      "module.shaderHand.color.rainbow",
      "module.shaderHand.color.client",
      "module.shaderHand.color.custom"
   );
   private final ColorSetting customColor = new ColorSetting(
      "module.shaderHand.customColor", "module.shaderHand.customColor.desc", ZenithColor.INSTANCE, this::method1263
   );
   private final BooleanSetting secondColor = new BooleanSetting("module.shaderHand.secondColor", "module.shaderHand.secondColor.desc", false, this::method1263);
   private final ColorSetting colorSetting = new ColorSetting(
      "module.shaderHand.customSecondColor",
      "module.shaderHand.customSecondColor.desc",
      new ZenithColor(170, 120, 255),
      () -> this.method1263() && this.secondColor.isEnabled()
   );
   private long lastFrameNanos = -1L;
   private float floatValue;
   private float floatValue2 = 0.72F;
   private boolean flag;
   private float floatValue3 = 0.22F;
   private boolean flag2;
   private boolean flag3;
   private SimpleFramebuffer simpleFramebuffer;

   private ShaderHandModule() {
      this.colorMode.set("module.shaderHand.color.client");
   }

   public void method1238(Runnable var1) {
      this.method1239(var1, 0.0F);
   }

   public boolean isFlag23() {
      return this.flag2;
   }

   public void method1239(Runnable var1, float var2) {
      if (!HandShaders.isInitialized()) {
         var1.run();
      } else {
         WardrobeRenderer.method6693();
         if (this.method1253()) {
            var1.run();
         } else {
            try {
               this.method1240(var1);
            } catch (Exception var7) {
               if (!this.flag) {
                  this.flag = true;
                  var7.printStackTrace();
               }
            } finally {
               WardrobeRenderer.method6693();
               this.method1244();
            }
         }
      }
   }

   private void method1240(Runnable var1) {
      Framebuffer var2 = mc.getFramebuffer();
      SimpleFramebuffer var3 = HandShaders.getSimpleFramebuffer();
      ShaderHandModule.Type var4 = this.method1252();
      ShaderProgram var5 = HandShaders.method6279(var4.text2);
      if (var2 != null && var5 != null && var3 != null) {
         MinecraftClientAccessor var6 = (MinecraftClientAccessor)(Object)mc;
         var6.zenith$setFramebuffer(var3);
         this.flag2 = true;

         try {
            BlitUtils.method6456(var3, 0);
            var1.run();
            ((GameRendererAccessor)(Object)mc.gameRenderer).zenith$entityRenderDispatcher().render();
            mc.getBufferBuilders().getEntityVertexConsumers().draw();
         } finally {
            this.flag2 = false;
            var6.zenith$setFramebuffer(var2);
         }

         SimpleFramebuffer var7 = this.method1242(var3, var2);
         RenderState.method1871();
         RenderState.method1873();
         RenderState.method1879();
         RenderState.method1889(0, var3.getColorAttachmentView());
         RenderState.method1889(1, var3.getDepthAttachmentView());

         try {
            var5.method5517();
            this.method1245(var5, var3, var4);
            HandShaders.method6282();
            var5.unbind();
         } finally {
            var5.unbind();
            RenderState.method1893(0);
            RenderState.method1893(1);
            RenderState.method1873();
            RenderState.method1872();
            RenderState.method1878();
            ImmediateRenderer.method8013(null);
         }

         if (var7 != null) {
            this.method1243(var7);
         }
      } else {
         var1.run();
      }
   }

   public boolean method1241() {
      if (!this.flag2) {
         return false;
      } else {
         this.flag3 = true;
         return true;
      }
   }

   private SimpleFramebuffer method1242(SimpleFramebuffer var1, Framebuffer var2) {
      if (!WardrobeRenderer.method6692()) {
         return null;
      } else {
         if (this.simpleFramebuffer == null
            || this.simpleFramebuffer.textureWidth != var1.textureWidth
            || this.simpleFramebuffer.textureHeight != var1.textureHeight) {
            if (this.simpleFramebuffer != null) {
               this.simpleFramebuffer.delete();
            }

            this.simpleFramebuffer = BlitUtils.method6448("zenith hand accessories", var1.textureWidth, var1.textureHeight, true);
         }

         MinecraftClientAccessor var3 = (MinecraftClientAccessor)(Object)mc;
         var3.zenith$setFramebuffer(this.simpleFramebuffer);

         try {
            BlitUtils.method6456(this.simpleFramebuffer, 0);
            if (this.simpleFramebuffer.getDepthAttachment() != null) {
               RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(this.simpleFramebuffer.getDepthAttachment(), 1.0);
            }

            Immediate var4 = mc.getBufferBuilders().getEntityVertexConsumers();
            WardrobeRenderer.method6694(var4);
            var4.draw();
         } finally {
            var3.zenith$setFramebuffer(var2);
         }

         return this.simpleFramebuffer;
      }
   }

   private void method1243(SimpleFramebuffer var1) {
      ShaderProgram var2 = HandShaders.method6279("zenith_passthrough");
      if (var2 != null) {
         RenderState.method1871();
         RenderState.method1873();
         RenderState.method1879();
         RenderState.method1889(0, var1.getColorAttachmentView());
         RenderState.method1889(1, var1.getDepthAttachmentView());

         try {
            var2.method5517();
            var2.method5518("ColorTexture", 0);
            var2.method5518("DepthTexture", 1);
            HandShaders.method6282();
         } finally {
            var2.unbind();
            RenderState.method1893(0);
            RenderState.method1893(1);
            RenderState.method1873();
            RenderState.method1872();
            RenderState.method1878();
            ImmediateRenderer.method8013(null);
         }
      }
   }

   private void method1244() {
      if (this.flag3) {
         this.flag3 = false;
         if (mc.player != null && mc.world != null) {
            Framebuffer var1 = mc.getFramebuffer();
            if (var1 != null) {
               Immediate var2 = mc.getBufferBuilders().getEntityVertexConsumers();
               float var3 = mc.getRenderTickCounter().getTickProgress(false);
               OutlineVertexProvider.method1542(
                  var2, var1x -> ((GameRendererAccessor)(Object)mc.gameRenderer).zenith$overlayRenderer().renderOverlays(mc.player.isSleeping(), var3, var1x)
               );
            }
         }
      }
   }

   private void method1245(ShaderProgram var1, SimpleFramebuffer var2, ShaderHandModule.Type var3) {
      int var4 = Math.max(1, var2.textureWidth);
      int var5 = Math.max(1, var2.textureHeight);
      var1.method5518("ColorTexture", 0);
      var1.method5518("DepthTexture", 1);
      var1.method5521("resolution", var4, var5);
      var1.method5519("time", this.method1249() * var3.floatValue);
      this.method1250();
      var1.method5521("handMotion", this.floatValue2, this.floatValue3);
      var1.method5521("handSway", 0.0F, 0.0F);
      var1.method5519("effectAlpha", 1.0F);
      int[] var6 = this.method1247();
      method1246(var1, "PrimaryColor", var6[0]);
      method1246(var1, "SecondaryColor", var6[1]);
      var1.method5519("Brightness", 2.15F);
      var1.method5519("ScanDensity", 200.0F);
      var1.method5519("Glitch", 1.0F);
   }

   private static void method1246(ShaderProgram var0, String var1, int var2) {
      var0.method5524(var1, ColorUtils.method6424(var2), ColorUtils.method6425(var2), ColorUtils.method6426(var2));
   }

   private int[] method1247() {
      if (this.colorMode.isSelected(0)) {
         float var2 = (float)(System.currentTimeMillis() % 9000L) / 9000.0F;
         return new int[]{method1248(var2), method1248(var2 + 0.1F)};
      } else if (this.colorMode.isSelected(1)) {
         return new int[]{
            Zenith.getInstance().getStyleManager().getClientColor(0).method7287(), Zenith.getInstance().getStyleManager().getClientColor(180).method7287()
         };
      } else {
         int var1 = this.customColor.method1445();
         return new int[]{var1, this.secondColor.isEnabled() ? this.colorSetting.method1445() : var1};
      }
   }

   private static int method1248(float var0) {
      float var1 = var0 - (float)Math.floor(var0);
      return ZenithColor.method7297(var1, 0.72F, 1.0F).method7287();
   }

   private float method1249() {
      long var1 = System.nanoTime();
      if (this.lastFrameNanos < 0L) {
         this.lastFrameNanos = var1;
         return this.floatValue;
      } else {
         float var3 = Math.min((float)(var1 - this.lastFrameNanos) / 1.0E9F, 0.1F);
         this.lastFrameNanos = var1;
         this.floatValue = (this.floatValue + var3 * 5.0F) % 100000.0F;
         return this.floatValue;
      }
   }

   private void method1250() {
      this.floatValue2 = mc.player != null && mc.player.getMainArm() == Arm.LEFT ? 0.28F : 0.72F;
      this.floatValue3 = 0.22F;
   }

   private int method1251(SimpleFramebuffer var1) {
      int var2 = Math.max(1, var1.textureHeight);
      return Math.min(var2, Math.max(1, (int)(var2 * 0.72F)));
   }

   private ShaderHandModule.Type method1252() {
      return this.shaderMode == null ? ShaderHandModule.Type.ICE : ShaderHandModule.Type.method1264(this.shaderMode.getIndex());
   }

   private boolean method1253() {
      return this.method1252() == ShaderHandModule.Type.FIRE;
   }

   public boolean method1254() {
      return this.isEnabled() && this.method1253();
   }

   public float method1255() {
      return this.fireAlpha.getValue3();
   }

   public boolean method1256() {
      return this.fireUnderHand.isEnabled();
   }

   public boolean method1257() {
      return this.colorMode.isSelected(0);
   }

   public boolean method1258() {
      return this.colorMode.isSelected(1);
   }

   public boolean method1259() {
      return this.colorMode.isSelected(2);
   }

   public int method1260() {
      return this.customColor.method1445();
   }

   public int method1261() {
      return this.secondColor.isEnabled() ? this.colorSetting.method1445() : this.customColor.method1445();
   }

   private boolean method1262() {
      ShaderHandModule.Type var1 = this.method1252();
      return var1 == ShaderHandModule.Type.HOLOGRAM || var1 == ShaderHandModule.Type.MATRIX || var1 == ShaderHandModule.Type.FIRE;
   }

   private boolean method1263() {
      return this.method1262() && this.colorMode.isSelected(2);
   }

   private static enum Type {
      ICE("module.shaderHand.shader.ice", "zenith_ice", 1.0F),
      HOLOGRAM("module.shaderHand.shader.hologram", "zenith_hologram", 1.0F),
      MATRIX("module.shaderHand.shader.matrix", "zenith_matrix", 1.0F),
      CHROME("module.shaderHand.shader.chrome", "zenith_chrome", 1.0F),
      FIRE("module.shaderHand.shader.fire", null, 1.0F);

      private final String text;
      private final String text2;
      private final float floatValue;

      private Type(String var3, String var4, float var5) {
         this.text = var3;
         this.text2 = var4;
         this.floatValue = var5;
      }

      private static ShaderHandModule.Type method1264(int var0) {
         ShaderHandModule.Type[] var1 = values();
         return var0 >= 0 && var0 < var1.length ? var1[var0] : ICE;
      }

      private static String[] method1265() {
         ShaderHandModule.Type[] var0 = values();
         String[] var1 = new String[var0.length];

         for (int var2 = 0; var2 < var0.length; var2++) {
            var1[var2] = var0[var2].text;
         }

         return var1;
      }
   }
}

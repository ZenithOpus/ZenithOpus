package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.Random;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.combat.AimAssistModule;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.TargetEspTextures;
import org.zenith.utility.render.TextureFilterUtils;
import org.zenith.utility.render.display.base.MenuDrawContext;

@ModuleInfo(
   name = "TargetESP",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.targetesp.description"
)
public class TargetESPModule extends Module {
   public static final TargetESPModule INSTANCE = new TargetESPModule();
   private static final Identifier[] identifiers = method2147("vortex");
   private static final Identifier[] identifiers2 = method2147("brackets");
   private static final int CONST_intValue = 3;
   private static final float CONST_floatValue = 22.0F;
   private static final float CONST_floatValue2 = (float) (Math.PI / 180.0);
   private static final long CONST_longValue = 250L;
   private static final float CONST_floatValue3 = 220.0F;
   private static final long CONST_longValue2 = 100L;
   private final ModeSetting mode = new ModeSetting("module.targetESP.mode", "module.targetESP.mode.desc");
   private final ModeSetting.Entry entry = new ModeSetting.Entry(this.mode, "module.targetESP.texture.vortex").method2103();
   private final ModeSetting.Entry entry2 = new ModeSetting.Entry(this.mode, "module.targetESP.texture.brackets");
   private final ModeSetting.Entry entry3 = new ModeSetting.Entry(this.mode, "module.targetESP.hearts");
   private final ModeSetting.Entry entry4 = new ModeSetting.Entry(this.mode, "module.targetESP.atom");
   private final ModeSetting.Entry entry5 = new ModeSetting.Entry(this.mode, "module.targetESP.helix");
   private final ModeSetting.Entry entry6 = new ModeSetting.Entry(this.mode, "module.targetESP.saturn");
   private final ModeSetting.Entry entry7 = new ModeSetting.Entry(this.mode, "module.targetESP.stars");
   private final SliderSetting size = new SliderSetting("module.targetESP.size", 1.2F, 0.3F, 3.0F, 0.05F, "module.targetESP.size.desc", "x");
   private final SliderSetting speed = new SliderSetting("module.targetESP.speed", 1.0F, 0.0F, 4.0F, 0.05F, "module.targetESP.speed.desc", "x");
   private final BooleanSetting red = new BooleanSetting("module.targetESP.red", "module.targetESP.red.description", true);
   private final Animation animation = new Animation(250L, Easing.INSTANCE13);
   private final Random random = new Random();
   private final float[] floatValues = new float[3];
   private final float[] floatValues2 = new float[3];
   private final float[] floatValues3 = new float[3];
   private final float[] floatValues4 = new float[3];
   private final float[] floatValues5 = new float[3];
   private final float[] floatValues6 = new float[3];
   private final int[] intValues = new int[3];
   private final int[] intValues2 = new int[3];
   private final Matrix4f matrix4f = new Matrix4f();
   private final TargetEspTextures targetEspTextures = new TargetEspTextures();
   private final int[] intValues3 = new int[4];
   private float floatValue;
   private long longValue;
   private LivingEntity livingEntity;
   private long longValue2;
   private boolean flag;
   private long longValue3;
   private boolean flag2;
   private double doubleValue;
   private double doubleValue2;
   private double doubleValue3;
   private float floatValue2;
   private float floatValue3;
   private long longValue4;
   private double doubleValue4;
   private double doubleValue5;
   private double doubleValue6;
   private float floatValue4;
   private float floatValue5;

   @Override
   public ModulePreviewStage.Kind previewKind() {
      return ModulePreviewStage.Kind.TARGET;
   }

   private TargetESPModule() {
      this.method2163();
   }

   private static Identifier[] method2147(String var0) {
      Identifier[] var1 = new Identifier[3];
      int[] var2 = new int[]{768, 384, 192};

      for (int var3 = 0; var3 < var2.length; var3++) {
         var1[var3] = Zenith.method1501("visuals/targetesp/" + var0 + "_" + var2[var3] + ".png");
         TextureFilterUtils.smooth(var1[var3]);
      }

      return var1;
   }

   @EventTarget
   public void method2148(WorldRenderEvent var1) {
      LivingEntity var2 = AuraModule.INSTANCE.method6358() == null ? AimAssistModule.INSTANCE.method5156() : AuraModule.INSTANCE.method6358();
      if (var2 != null && (!var2.isAlive() || var2.isRemoved())) {
         var2 = null;
      }

      this.method2162(var2);
      this.method2157();
      if (mc.player != null && mc.world != null && this.livingEntity != null && !(this.animation.getValue() <= 0.01F)) {
         this.method2149(var1);
      }
   }

   private void method2149(WorldRenderEvent var1) {
      Camera var2 = mc.getEntityRenderDispatcher().camera;
      Vec3d var3 = var2.getCameraPos();
      Vec3d var4 = MathUtils.method2656(this.livingEntity);
      Box var5 = this.livingEntity.getBoundingBox();
      float var6 = (float)Math.max(var5.getLengthX(), var5.getLengthZ());
      float var7 = (float)var5.getLengthY();
      double var8 = var4.x;
      double var10 = var4.y;
      double var12 = var4.z;
      float var14 = var6;
      float var15 = var7;
      if (this.longValue4 != 0L) {
         float var16 = Math.min(1.0F, (float)(System.currentTimeMillis() - this.longValue4) / 220.0F);
         float var17 = 1.0F - (1.0F - var16) * (1.0F - var16) * (1.0F - var16);
         var8 = MathHelper.lerp((double)var17, this.doubleValue4, var4.x);
         var10 = MathHelper.lerp((double)var17, this.doubleValue5, var4.y);
         var12 = MathHelper.lerp((double)var17, this.doubleValue6, var4.z);
         var14 = MathHelper.lerp(var17, this.floatValue4, var6);
         var15 = MathHelper.lerp(var17, this.floatValue5, var7);
         if (var16 >= 1.0F) {
            this.longValue4 = 0L;
         }
      }

      this.flag2 = true;
      this.doubleValue = var8;
      this.doubleValue2 = var10;
      this.doubleValue3 = var12;
      this.floatValue2 = var14;
      this.floatValue3 = var15;
      double var38 = var8 - var3.x;
      double var18 = var10 - var3.y;
      double var20 = var12 - var3.z;
      float var22 = this.livingEntity.hurtTime > 0 ? (this.livingEntity.hurtTime - GlowUtil.getTickDelta()) / 10.0F : 0.0F;
      float var23 = method2155(MathHelper.clamp(var22, 0.0F, 1.0F));
      float var24 = this.red.isEnabled() ? var23 : 0.0F;
      double var25 = Math.sqrt(var38 * var38 + (var18 + var15 * 0.5) * (var18 + var15 * 0.5) + var20 * var20);
      double var27 = 2.0 * Math.tan(Math.toRadians(mc.options.getFov().getValue().intValue()) / 2.0);
      float var29 = (float)(mc.getWindow().getFramebufferHeight() / var27 / Math.max(0.5, var25));
      Matrix4f var30 = var1.getMatrixStack().peek().getPositionMatrix();
      float var31 = var2.getYaw() * (float) (Math.PI / 180.0);
      float var32 = var2.getPitch() * (float) (Math.PI / 180.0);
      boolean var33 = this.method2161();
      float var34 = this.animation.getValue();
      int var35 = this.method2156();
      if (var35 >= 0) {
         this.targetEspTextures
            .method5054(
               var35,
               var30,
               var38,
               var18,
               var20,
               var14,
               var15,
               this.floatValue,
               this.method2154(var24),
               var34,
               var23,
               this.size.getValue3() / 1.2F,
               var31,
               var32,
               var33,
               var29
            );
      } else {
         ZenithColor var36 = Zenith.getInstance().getStyleManager().getClientColor(90);
         if (var24 > 0.0F) {
            var36 = var36.method7295(ZenithColor.INSTANCE4, var24);
         }

         float var37 = var14 * 5.0F * this.size.getValue3() * method2150(var34, var23);
         this.method2158(this.floatValue, ColorUtils.method6439(var36.method7287(), var34));
         RenderState.method1871();
         if (var33) {
            RenderState.method1878();
         } else {
            RenderState.method1879();
         }

         RenderState.method1882(false);
         RenderState.method1877();
         this.method2153(var30, var38, var18, var20, var37 * 0.5F, method2151(var15), this.method2152(var37 * var29));
         RenderState.method1882(true);
         RenderState.method1878();
         RenderState.method1876();
         RenderState.method1872();
         RenderState.method1873();
      }
   }

   private static float method2150(float var0, float var1) {
      float var2 = 1.0F - (1.0F - var0) * (1.0F - var0) * (1.0F - var0);
      return (0.85F + 0.15F * var2) * (1.0F + 0.08F * var1);
   }

   private static float method2151(float var0) {
      return var0 * 0.62F - 0.006F;
   }

   private Identifier method2152(float var1) {
      Identifier[] var2 = this.entry2.isSelected() ? identifiers2 : identifiers;
      if (var1 > 420.0F) {
         return var2[0];
      } else {
         return var1 > 200.0F ? var2[1] : var2[2];
      }
   }

   private void method2153(Matrix4f var1, double var2, double var4, double var6, float var8, float var9, Identifier var10) {
      RenderState.method1898("core/position_tex_color");
      RenderState.method1890(0, var10);
      RenderState.method1875(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
      BufferBuilder var11 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      float var12 = var9;

      for (int var13 = 0; var13 < 3; var12 += 0.006F) {
         this.method2159(var1, var11, var2, var4, var6, var8, var12, this.floatValues5[var13], this.floatValues6[var13], this.intValues[var13]);
         var13++;
      }

      ImmediateRenderer.method8015(var11.end());
      int var17 = 0;

      for (int var14 = 0; var14 < 3; var14++) {
         var17 = Math.max(var17, this.intValues2[var14] >>> 24);
      }

      if (var17 > 0) {
         RenderState.method1875(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
         var11 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         var12 = var9;

         for (int var18 = 0; var18 < 3; var12 += 0.006F) {
            if (this.intValues2[var18] >>> 24 != 0) {
               this.method2159(var1, var11, var2, var4, var6, var8, var12, this.floatValues5[var18], this.floatValues6[var18], this.intValues2[var18]);
            }

            var18++;
         }

         ImmediateRenderer.method8015(var11.end());
      }
   }

   private int[] method2154(float var1) {
      for (int var2 = 0; var2 < this.intValues3.length; var2++) {
         ZenithColor var3 = Zenith.getInstance().getStyleManager().getClientColor(var2 * 90);
         if (var1 > 0.0F) {
            var3 = var3.method7295(ZenithColor.INSTANCE4, var1);
         }

         this.intValues3[var2] = var3.method7287();
      }

      return this.intValues3;
   }

   private static float method2155(float var0) {
      return var0 * var0 * (3.0F - 2.0F * var0);
   }

   private int method2156() {
      if (this.entry3.isSelected()) {
         return 0;
      } else if (this.entry4.isSelected()) {
         return 1;
      } else if (this.entry5.isSelected()) {
         return 2;
      } else if (this.entry6.isSelected()) {
         return 3;
      } else {
         return this.entry7.isSelected() ? 4 : -1;
      }
   }

   private void method2157() {
      long var1 = System.nanoTime();
      if (this.longValue != 0L) {
         float var3 = Math.min(0.1F, (float)(var1 - this.longValue) / 1.0E9F);
         this.floatValue = this.floatValue + var3 * this.speed.getValue3();
      }

      this.longValue = var1;
   }

   private void method2158(float var1, int var2) {
      for (int var3 = 0; var3 < 3; var3++) {
         float var4 = (MathHelper.sin(var1 * 2.6F + var3 * (float) (Math.PI * 2.0 / 3.0)) + 1.0F) * 0.5F;
         float var5 = MathHelper.lerp(var4, 0.4F, 1.0F);
         this.intValues[var3] = ColorUtils.method6439(var2, var5);
         this.intValues2[var3] = ColorUtils.method6439(var2, var5 * 0.8F);
         this.floatValues5[var3] = -var1 * 120.0F * this.floatValues4[var3] + this.floatValues[var3];
         this.floatValues6[var3] = MathHelper.sin(var1 * this.floatValues3[var3] * 0.8F + this.floatValues2[var3]) * 22.0F;
      }
   }

   private void method2159(
      Matrix4f var1, BufferBuilder var2, double var3, double var5, double var7, float var9, float var10, float var11, float var12, int var13
   ) {
      this.matrix4f
         .set(var1)
         .translate((float)var3, (float)(var5 + var10), (float)var7)
         .rotateY((90.0F + var11) * (float) (Math.PI / 180.0))
         .rotateX(var12 * (float) (Math.PI / 180.0));
      var2.vertex(this.matrix4f, -var9, 0.0F, -var9).texture(0.0F, 0.0F).color(var13);
      var2.vertex(this.matrix4f, -var9, 0.0F, var9).texture(0.0F, 1.0F).color(var13);
      var2.vertex(this.matrix4f, var9, 0.0F, var9).texture(1.0F, 1.0F).color(var13);
      var2.vertex(this.matrix4f, var9, 0.0F, -var9).texture(1.0F, 0.0F).color(var13);
   }

   public void method2160(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.method2157();
      float var8 = MathHelper.clamp(var5, 0.0F, 1.0F);
      float var9 = method2155(MathHelper.clamp(1.0F - (float)(System.currentTimeMillis() % 3200L) / 500.0F, 0.0F, 1.0F));
      float var10 = 22.0F + var7;
      MatrixStack var11 = MatrixUtils.method666(var1.getMatrices());
      var11.translate(var2, var3, 0.0F);
      var11.scale(var4, -var4, var4);
      var11.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var10));
      var11.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var6));
      Matrix4f var12 = var11.peek().getPositionMatrix();
      float var13 = var6 * (float) (Math.PI / 180.0);
      float var14 = -var10 * (float) (Math.PI / 180.0);
      int var15 = this.method2156();
      if (var15 >= 0) {
         this.targetEspTextures
            .method5054(
               var15,
               var12,
               0.0,
               0.0,
               0.0,
               0.6F,
               1.8F,
               this.floatValue,
               this.method2154(0.0F),
               var8,
               var9,
               this.size.getValue3() / 1.2F,
               var13,
               var14,
               false,
               var4
            );
      } else {
         float var16 = 3.0F * this.size.getValue3() * method2150(var8, var9);
         this.method2158(this.floatValue, ColorUtils.method6439(Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), var8));
         RenderState.method1871();
         RenderState.method1879();
         RenderState.method1882(false);
         RenderState.method1877();
         this.method2153(var12, 0.0, 0.0, 0.0, var16 * 0.5F, method2151(1.8F), this.method2152(var16 * var4));
         RenderState.method1882(true);
         RenderState.method1878();
         RenderState.method1876();
         RenderState.method1872();
         RenderState.method1873();
      }
   }

   private boolean method2161() {
      if (mc.player != null && this.livingEntity != null) {
         long var1 = System.currentTimeMillis();
         if (var1 - this.longValue2 >= 100L) {
            this.flag = mc.player.canSee(this.livingEntity);
            this.longValue2 = var1;
         }

         return this.flag;
      } else {
         return false;
      }
   }

   private void method2162(LivingEntity var1) {
      long var2 = System.currentTimeMillis();
      if (var1 != null) {
         this.longValue3 = var2;
      } else if (this.livingEntity != null && this.livingEntity.isAlive() && !this.livingEntity.isRemoved() && var2 - this.longValue3 < 250L) {
         var1 = this.livingEntity;
      }

      if (var1 == null) {
         this.animation.method2808(0.0F);
         if (this.animation.getValue() <= 0.01F) {
            this.livingEntity = null;
            this.flag2 = false;
            this.longValue4 = 0L;
            this.longValue2 = 0L;
         }
      } else {
         if (var1 != this.livingEntity) {
            if (this.livingEntity != null && this.flag2 && this.animation.getValue() > 0.05F) {
               this.longValue4 = var2;
               this.doubleValue4 = this.doubleValue;
               this.doubleValue5 = this.doubleValue2;
               this.doubleValue6 = this.doubleValue3;
               this.floatValue4 = this.floatValue2;
               this.floatValue5 = this.floatValue3;
            } else {
               this.longValue4 = 0L;
               this.method2163();
            }

            this.livingEntity = var1;
            this.longValue2 = 0L;
         }

         this.animation.method2808(1.0F);
      }
   }

   private void method2163() {
      for (int var1 = 0; var1 < 3; var1++) {
         this.floatValues4[var1] = 0.75F + this.random.nextFloat() * 0.5F;
         this.floatValues3[var1] = 0.8F + this.random.nextFloat() * 0.8F;
         this.floatValues2[var1] = this.random.nextFloat() * (float) (Math.PI * 2);
      }

      for (int var5 = 0; var5 < 64; var5++) {
         for (int var2 = 0; var2 < 3; var2++) {
            this.floatValues[var2] = -30.0F + this.random.nextFloat() * 60.0F;
         }

         boolean var6 = true;

         for (int var3 = 0; var3 < 3 && var6; var3++) {
            for (int var4 = var3 + 1; var4 < 3 && var6; var4++) {
               if (Math.abs(this.floatValues[var3] - this.floatValues[var4]) < 22.0F) {
                  var6 = false;
               }
            }
         }

         if (var6) {
            return;
         }
      }

      this.floatValues[0] = -30.0F + this.random.nextFloat() * 4.0F;
      this.floatValues[1] = -2.0F + this.random.nextFloat() * 4.0F;
      this.floatValues[2] = 30.0F - this.random.nextFloat() * 4.0F;
   }
}

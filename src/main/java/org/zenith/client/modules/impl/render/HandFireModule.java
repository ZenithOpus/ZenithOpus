package org.zenith.client.modules.impl.render;

import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.function.Consumer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.lwjgl.opengl.GL20;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.misc.HandRenderState;
import org.zenith.utility.render.BlitUtils;
import org.zenith.utility.render.FramebufferUtils;
import org.zenith.utility.render.HandShaders;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.OutlineVertexProvider;
import org.zenith.utility.render.ShaderProgram;

@ModuleInfo(
   name = "HandFire",
   category = ModuleCategory.RENDER,
   description = "module.handfire.description"
)
public final class HandFireModule extends Module {
   public static final HandFireModule INSTANCE = new HandFireModule();
   private static final int CONST_intValue = 1048576;
   private static final int CONST_intValue2 = new ZenithColor(255, 255, 255).method7287();
   private static final int CONST_intValue3 = new ZenithColor(24, 12, 36).method7287();
   private static final int[] CONST_intValues = new int[4];
   private static SimpleFramebuffer simpleFramebuffer;
   private static SimpleFramebuffer simpleFramebuffer2;
   private static SimpleFramebuffer simpleFramebuffer3;
   private static SimpleFramebuffer simpleFramebuffer4;
   private static ShaderProgram shaderProgram;
   private static ShaderProgram shaderProgram2;
   private static ShaderProgram shaderProgram3;
   private static boolean initialized;
   private static boolean flag;
   private static boolean flag2;
   private static float floatValue = 1.0F;
   private static boolean flag3;
   private static boolean flag4;
   private static boolean flag5;
   private static boolean flag6;
   private static boolean flag7;
   private static float floatValue2;
   private static long longValue;
   private static BufferAllocator bufferAllocator;
   private final int[] intValues = new int[4];
   private static final float CONST_floatValue = 1.0F;
   private static final float CONST_floatValue2 = 0.0F;
   private static final float CONST_floatValue3 = 0.25F;
   private static final float CONST_floatValue4 = 1.0F;
   private static final float CONST_floatValue5 = 255.0F;
   private static final int CONST_intValue4 = new ZenithColor(0, 0, 0).method7287();

   private HandFireModule() {
   }

   public boolean method6612() {
      if (!ShaderHandModule.INSTANCE.method1254()) {
         if ((method6613() || bufferAllocator != null) && mc.isOnThread()) {
            method6614();
         }

         return false;
      } else {
         return mc.player != null && mc.world != null && mc.getWindow() != null;
      }
   }

   private static boolean method6613() {
      return simpleFramebuffer != null || simpleFramebuffer2 != null || simpleFramebuffer3 != null || simpleFramebuffer4 != null;
   }

   private static void method6614() {
      method6637(simpleFramebuffer);
      method6637(simpleFramebuffer2);
      method6637(simpleFramebuffer3);
      method6637(simpleFramebuffer4);
      simpleFramebuffer = null;
      simpleFramebuffer2 = null;
      simpleFramebuffer3 = null;
      simpleFramebuffer4 = null;
      method6640();
      method6636();
   }

   public static float method6615() {
      float var0 = floatValue;
      floatValue = 1.0F;
      return var0;
   }

   public static boolean method6616() {
      return HandRenderState.isActive();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      method6640();
      method6636();
   }

   public void method6617() {
      if (this.method6612() && !HandRenderState.isActive()) {
         method6620();
         if (initialized) {
            HandFireModule.Data var1 = HandFireModule.Data.method6645();
            if (var1.intValue3 > 0 && var1.intValue4 > 0) {
               try {
                  method6628(var1.intValue3, var1.intValue4);
                  if (method6629()) {
                     method6632(simpleFramebuffer);
                     method6632(simpleFramebuffer2);
                     flag3 = true;
                     flag4 = false;
                     flag5 = false;
                     flag6 = false;
                     floatValue = 1.0F;
                     floatValue2 = 0.0F;
                     return;
                  }
               } catch (Exception var6) {
                  method6638(var6);
                  return;
               } finally {
                  method6633(var1);
               }
            }
         }
      }
   }

   public void method6618() {
      if (flag3) {
         HandFireModule.Data var1 = HandFireModule.Data.method6645();

         try {
            if (ShaderHandModule.INSTANCE.method1256()) {
               if (flag6) {
                  method6621();
                  this.method6622();
               }

               method6639();
            } else if (flag4 || flag7) {
               if (flag4) {
                  method6621();
               }

               GpuTextureView var2 = this.method6622();
               this.method6623(var1, var2);
               method6639();
            }
         } catch (Exception var6) {
            method6638(var6);
         } finally {
            flag3 = false;
            flag4 = false;
            flag5 = false;
            flag6 = false;
            floatValue = 1.0F;
            method6633(var1);
         }
      }
   }

   public void method6619(
      AbstractClientPlayerEntity var1,
      float var2,
      float var3,
      Hand var4,
      float var5,
      ItemStack var6,
      float var7,
      MatrixStack var8,
      OrderedRenderCommandQueue var9,
      int var10,
      Consumer<OrderedRenderCommandQueue> var11
   ) {
      if (this.method6612() && !HandRenderState.isActive()) {
         if (!flag3) {
            this.method6617();
         }

         method6620();
         if (initialized && flag3) {
            HandFireModule.Data var12 = HandFireModule.Data.method6645();
            if (var12.intValue3 > 0 && var12.intValue4 > 0) {
               float var13 = this.method6641();
               floatValue = var13;
               floatValue2 = Math.max(floatValue2, 1.0F);

               try {
                  RenderState.method1903(true, true, true, true);
                  RenderState.method1882(true);
                  RenderState.method1878();
                  HandRenderState.begin();
                  BufferAllocator var14 = method6635();

                  try {
                     Immediate var15 = VertexConsumerProvider.immediate(var14);
                     GpuTextureView var16 = RenderSystem.outputColorTextureOverride;
                     GpuTextureView var17 = RenderSystem.outputDepthTextureOverride;
                     RenderSystem.outputColorTextureOverride = simpleFramebuffer.getColorAttachmentView();
                     RenderSystem.outputDepthTextureOverride = simpleFramebuffer.getDepthAttachmentView();

                     try {
                        OutlineVertexProvider.method1542(var15, var11::accept);
                     } finally {
                        RenderSystem.outputColorTextureOverride = var16;
                        RenderSystem.outputDepthTextureOverride = var17;
                     }

                     flag4 = true;
                     longValue = System.currentTimeMillis();
                  } finally {
                     var14.reset();
                     HandRenderState.end();
                  }

                  if (ShaderHandModule.INSTANCE.method1256()) {
                     if (flag5) {
                        flag6 = true;
                     } else {
                        method6621();
                        GpuTextureView var37 = this.method6622();
                        this.method6623(var12, var37);
                        flag5 = true;
                        method6633(var12);
                     }
                  }

                  if (var13 > 0.0F) {
                     method6631(var12);
                     var11.accept(var9);
                  }
               } catch (Exception var35) {
                  method6631(var12);
                  if (var13 > 0.0F) {
                     floatValue = var13;
                     var11.accept(var9);
                  }

                  method6638(var35);
               } finally {
                  HandRenderState.end();
                  method6633(var12);
               }
            } else {
               var11.accept(var9);
            }
         } else {
            var11.accept(var9);
         }
      } else {
         var11.accept(var9);
      }
   }

   private static void method6620() {
      if (!initialized) {
         try {
            shaderProgram = new ShaderProgram("hand", "hand_fire_mask", "smoke");
            shaderProgram2 = new ShaderProgram("hand", "hand_fire_accumulate", "smoke");
            shaderProgram3 = new ShaderProgram("hand", "hand_fire_composite", "smoke");
            initialized = true;
         } catch (Exception var1) {
            if (!flag) {
               System.err.println("Failed to initialize HandFire shaders");
               var1.printStackTrace();
               flag = true;
            }

            initialized = false;
         }
      }
   }

   private static void method6621() {
      RenderState.method1879();
      RenderState.method1882(false);
      RenderState.method1872();
      shaderProgram.method5517();
      shaderProgram.method5518("SourceTex", 0);
      RenderState.method1889(0, BlitUtils.method6450(simpleFramebuffer));
      ImmediateRenderer.method8013(simpleFramebuffer2);
      HandShaders.method6282();
      shaderProgram.unbind();
      RenderState.method1893(0);
   }

   private GpuTextureView method6622() {
      if (!flag7) {
         method6632(simpleFramebuffer3);
         method6632(simpleFramebuffer4);
         flag7 = true;
      }

      RenderState.method1879();
      RenderState.method1882(false);
      RenderState.method1872();
      RenderState.method1903(true, true, true, true);
      int[] var1 = this.method6625();
      shaderProgram2.method5517();
      shaderProgram2.method5518("PreviousTex", 0);
      shaderProgram2.method5518("HandTex", 1);
      shaderProgram2.method5518("MaskTex", 2);
      shaderProgram2.method5521("TexelSize", 1.0F / simpleFramebuffer4.textureWidth, 1.0F / simpleFramebuffer4.textureHeight);
      shaderProgram2.method5519("Time", method6627());
      shaderProgram2.method5519("Decay", 1.0F);
      shaderProgram2.method5519("Intensity", 1.0F);
      shaderProgram2.method5519("Lift", 0.0F);
      shaderProgram2.method5519("Turbulence", 0.25F);
      shaderProgram2.method5519("Alpha", ShaderHandModule.INSTANCE.method1255());
      shaderProgram2.method5519("EmitStrength", floatValue2);
      method6634(shaderProgram2, "ColorHot", var1[0]);
      method6634(shaderProgram2, "ColorMid", var1[1]);
      method6634(shaderProgram2, "ColorEdge", var1[2]);
      method6634(shaderProgram2, "ColorSmoke", var1[3]);
      RenderState.method1889(0, BlitUtils.method6450(simpleFramebuffer3));
      RenderState.method1889(1, BlitUtils.method6450(simpleFramebuffer));
      RenderState.method1889(2, BlitUtils.method6450(simpleFramebuffer2));
      ImmediateRenderer.method8013(simpleFramebuffer4);
      HandShaders.method6282();
      shaderProgram2.unbind();
      method6624();
      SimpleFramebuffer var2 = simpleFramebuffer3;
      simpleFramebuffer3 = simpleFramebuffer4;
      simpleFramebuffer4 = var2;
      return BlitUtils.method6450(simpleFramebuffer3);
   }

   private void method6623(HandFireModule.Data var1, GpuTextureView var2) {
      method6631(var1);
      RenderState.method1879();
      RenderState.method1882(false);
      RenderState.method1871();
      RenderState.method1874(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_CONSTANT_ALPHA);
      shaderProgram3.method5517();
      shaderProgram3.method5518("FireTex", 0);
      shaderProgram3.method5518("HandTex", 1);
      shaderProgram3.method5518("MaskTex", 2);
      shaderProgram3.method5521("TexelSize", 1.0F / var1.intValue3, 1.0F / var1.intValue4);
      shaderProgram3.method5519("Time", method6627());
      shaderProgram3.method5519("Intensity", 1.0F);
      shaderProgram3.method5519("Alpha", ShaderHandModule.INSTANCE.method1255());
      int[] var3 = this.method6625();
      method6634(shaderProgram3, "ColorHot", var3[0]);
      method6634(shaderProgram3, "ColorMid", var3[1]);
      method6634(shaderProgram3, "ColorEdge", var3[2]);
      RenderState.method1889(0, var2);
      RenderState.method1889(1, BlitUtils.method6450(simpleFramebuffer));
      RenderState.method1889(2, BlitUtils.method6450(simpleFramebuffer2));
      HandShaders.method6282();
      shaderProgram3.unbind();
      method6624();
   }

   private static void method6624() {
      RenderState.method1893(0);
      RenderState.method1893(1);
      RenderState.method1893(2);
   }

   private int[] method6625() {
      if (ShaderHandModule.INSTANCE.method1257()) {
         long var4 = System.currentTimeMillis() / 7L;
         this.intValues[0] = method6642((var4 + 8L) % 360L, 255);
         this.intValues[1] = method6642((var4 + 45L) % 360L, 255);
         this.intValues[2] = method6642((var4 + 120L) % 360L, 255);
         this.intValues[3] = method6642((var4 + 210L) % 360L, 185);
         return this.intValues;
      } else if (ShaderHandModule.INSTANCE.method1258()) {
         int var3 = method6644(Zenith.getInstance().getStyleManager().getClientColor(90).method7287(), 255);
         this.method6626(var3, var3);
         return this.intValues;
      } else if (ShaderHandModule.INSTANCE.method1259()) {
         int var1 = method6644(ShaderHandModule.INSTANCE.method1260(), 255);
         int var2 = method6644(ShaderHandModule.INSTANCE.method1261(), 255);
         this.method6626(var1, var2);
         return this.intValues;
      } else {
         this.intValues[0] = -1;
         this.intValues[1] = -1;
         this.intValues[2] = -1;
         this.intValues[3] = -1;
         return this.intValues;
      }
   }

   private void method6626(int var1, int var2) {
      this.intValues[0] = method6643(var1, CONST_intValue2, 0.25F);
      this.intValues[1] = var1;
      this.intValues[2] = method6643(var2, CONST_intValue4, 0.2F);
      this.intValues[3] = method6644(method6643(var2, CONST_intValue3, 0.58F), 185);
   }

   private static float method6627() {
      return (float)(System.currentTimeMillis() % 100000L) / 1000.0F;
   }

   private static void method6628(int var0, int var1) {
      if (simpleFramebuffer == null
         || simpleFramebuffer2 == null
         || simpleFramebuffer3 == null
         || simpleFramebuffer4 == null
         || simpleFramebuffer.textureWidth != var0
         || simpleFramebuffer.textureHeight != var1
         || simpleFramebuffer2.textureWidth != var0
         || simpleFramebuffer2.textureHeight != var1
         || simpleFramebuffer3.textureWidth != var0
         || simpleFramebuffer3.textureHeight != var1
         || simpleFramebuffer4.textureWidth != var0
         || simpleFramebuffer4.textureHeight != var1) {
         method6637(simpleFramebuffer);
         method6637(simpleFramebuffer2);
         method6637(simpleFramebuffer3);
         method6637(simpleFramebuffer4);
         simpleFramebuffer = BlitUtils.method6448("zenith handfire", var0, var1, true);
         simpleFramebuffer2 = BlitUtils.method6448("zenith handfire", var0, var1, false);
         simpleFramebuffer3 = BlitUtils.method6448("zenith handfire", var0, var1, false);
         simpleFramebuffer4 = BlitUtils.method6448("zenith handfire", var0, var1, false);
         method6630(simpleFramebuffer);
         method6630(simpleFramebuffer2);
         method6630(simpleFramebuffer3);
         method6630(simpleFramebuffer4);
         flag7 = false;
      }
   }

   private static boolean method6629() {
      return simpleFramebuffer != null && simpleFramebuffer2 != null && simpleFramebuffer3 != null && simpleFramebuffer4 != null;
   }

   private static void method6630(SimpleFramebuffer var0) {
      if (var0 != null) {
      }
   }

   private static void method6631(HandFireModule.Data var0) {
      ImmediateRenderer.method8013(null);
   }

   private static void method6632(SimpleFramebuffer var0) {
      if (var0 != null) {
         BlitUtils.method6456(var0, 0);
      }
   }

   private static void method6633(HandFireModule.Data var0) {
      RenderState.method1903(true, true, true, true);
      RenderState.method1882(true);
      RenderState.method1878();
      RenderState.method1873();
      RenderState.method1872();
      RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
      GL20.glUseProgram(0);
      FramebufferUtils.method5653();
   }

   private static void method6634(ShaderProgram var0, String var1, int var2) {
      var0.method5523(var1, ColorUtils.method6424(var2), ColorUtils.method6425(var2), ColorUtils.method6426(var2), ColorUtils.method6427(var2));
   }

   private static BufferAllocator method6635() {
      if (bufferAllocator == null) {
         bufferAllocator = new BufferAllocator(1048576);
      }

      return bufferAllocator;
   }

   private static void method6636() {
      if (bufferAllocator != null) {
         bufferAllocator.close();
         bufferAllocator = null;
      }
   }

   private static void method6637(SimpleFramebuffer var0) {
      if (var0 != null) {
         var0.delete();
      }
   }

   private static void method6638(Exception var0) {
      if (!flag2) {
         System.err.println("HandFire render failed");
         var0.printStackTrace();
         flag2 = true;
      }
   }

   private static void method6639() {
      if (floatValue2 <= 0.001F && longValue > 0L && System.currentTimeMillis() - longValue > 2200L) {
         flag7 = false;
         method6632(simpleFramebuffer3);
         method6632(simpleFramebuffer4);
      }
   }

   private static void method6640() {
      flag3 = false;
      flag4 = false;
      flag5 = false;
      flag6 = false;
      flag7 = false;
      floatValue = 1.0F;
      floatValue2 = 0.0F;
      longValue = 0L;
   }

   private float method6641() {
      return Math.max(0.0F, Math.min(255.0F, 255.0F)) / 255.0F;
   }

   private static int method6642(long var0, int var2) {
      float var3 = (float)((var0 % 360L + 360L) % 360L) / 360.0F;
      return ZenithColor.method7297(var3, 1.0F, 1.0F).method7293(var2).method7287();
   }

   private static int method6643(int var0, int var1, float var2) {
      return ZenithColor.method7291(var0).method7295(ZenithColor.method7291(var1), var2).method7287();
   }

   private static int method6644(int var0, int var1) {
      return ZenithColor.method7291(var0).method7293(var1).method7287();
   }

   private static final class Data {
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      public int intValue3() {
         return this.intValue3;
      }

      public int intValue4() {
         return this.intValue4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2) && java.util.Objects.equals(this.intValue3, other.intValue3) && java.util.Objects.equals(this.intValue4, other.intValue4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, intValue2, intValue3, intValue4);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + ", " + "intValue3=" + this.intValue3 + ", " + "intValue4=" + this.intValue4 + "]";
      }

      private Data(int var1, int var2, int var3, int var4) {
         this.intValue = var1;
         this.intValue2 = var2;
         this.intValue3 = var3;
         this.intValue4 = var4;
      }

      public static HandFireModule.Data method6645() {
         Framebuffer var0 = BlitUtils.method6452();
         return new HandFireModule.Data(0, 0, var0.textureWidth, var0.textureHeight);
      }

      public void restore() {
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }

      public int width() {
         return this.intValue3;
      }

      public int height() {
         return this.intValue4;
      }
   }
}

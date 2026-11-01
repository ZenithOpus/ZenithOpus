package org.zenith.utility.render.display.base;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.zenith.MatrixUtils;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.utility.misc.HudQueuedDraw;
import org.zenith.utility.render.Jebriksheet;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.ScissorFlusher;

public final class HudQueuedContext extends MenuDrawContext {
   private boolean replaying;
   private static float alpha = 1.0F;
   private static final Matrix4f QUEUE_MATRIX = new Matrix4f();

   public static void setAlpha(float var0) {
      alpha = Math.max(0.0F, Math.min(1.0F, var0));
   }

   public static float getAlpha() {
      return alpha;
   }

   private static ZenithColor tint(ZenithColor var0) {
      return !(alpha >= 0.999F) && var0 != null ? var0.method7294(alpha) : var0;
   }

   private static GlowColor tint(GlowColor var0) {
      return !(alpha >= 0.999F) && var0 != null ? var0.method8100(alpha) : var0;
   }

   private static int tint(int var0) {
      if (alpha >= 0.999F) {
         return var0;
      } else {
         int var1 = Math.round((var0 >>> 24 & 0xFF) * alpha);
         return var1 << 24 | var0 & 16777215;
      }
   }

   private static boolean itemsHidden() {
      return alpha < 0.5F;
   }

   private HudQueuedContext(MenuDrawContext var1) {
      super(var1, var1.getMouseX(), var1.getMouseY(), var1.getDelta());
   }

   public static HudQueuedContext of(MenuDrawContext var0) {
      return var0 instanceof HudQueuedContext var1 ? var1 : new HudQueuedContext(var0);
   }

   private HudQueuedDraw queueCommand(HudQueuedDraw.Type var1) {
      HudQueuedDraw var2 = Jebriksheet.method2355(this);
      var2.type = var1;
      MatrixUtils.method665(this.getMatrices(), var2.matrix4f);
      return var2;
   }

   private Matrix4f queueMatrix() {
      return MatrixUtils.method665(this.getMatrices(), QUEUE_MATRIX);
   }

   public void replayCommand(HudQueuedDraw var1) {
      Matrix3x2fStack var2 = this.getMatrices();
      boolean var3 = this.replaying;
      var2.pushMatrix();
      MatrixUtils.method669(var2, var1.matrix4f);
      this.replaying = true;

      try {
         this.dispatch(var1);
      } finally {
         this.replaying = var3;
         var2.popMatrix();
      }
   }

   private void dispatch(HudQueuedDraw var1) {
      switch (var1.type) {
         case TEXT_FONT_STRING_RGBA:
            super.drawText((Font)var1.obj, (String)var1.obj2, var1.floatValue, var1.floatValue2, (ZenithColor)var1.obj3);
            break;
         case TEXT_FONT_STRING_RGBA_FADEOUT:
            super.drawText(
               (Font)var1.obj,
               (String)var1.obj2,
               var1.floatValue,
               var1.floatValue2,
               (ZenithColor)var1.obj3,
               var1.flag,
               var1.floatValue3,
               var1.floatValue4,
               var1.floatValue5
            );
            break;
         case TEXT_FONT_STRING_INT_FADEOUT:
            super.drawText(
               (Font)var1.obj,
               (String)var1.obj2,
               var1.floatValue,
               var1.floatValue2,
               var1.intValue,
               var1.flag,
               var1.floatValue3,
               var1.floatValue4,
               var1.floatValue5
            );
            break;
         case TEXT_FONT_STRING_GRADIENT:
            super.drawText((Font)var1.obj, (String)var1.obj2, var1.floatValue, var1.floatValue2, (GlowColor)var1.obj3);
            break;
         case TEXT_FONT_TEXT_INT:
            super.drawText((Font)var1.obj, (Text)var1.obj2, var1.floatValue, var1.floatValue2, var1.intValue);
            break;
         case TEXT_FONT_TEXT:
            super.drawText((Font)var1.obj, (Text)var1.obj2, var1.floatValue, var1.floatValue2);
            break;
         case TEXT_SHADOW_STRING:
            super.drawTextWithShadow((TextRenderer)var1.obj, (String)var1.obj2, var1.intValue, var1.intValue2, var1.intValue3);
            break;
         case TEXT_STRING:
            super.drawText((TextRenderer)var1.obj, (String)var1.obj2, var1.intValue, var1.intValue2, var1.intValue3, var1.flag);
            break;
         case TEXT_SHADOW_ORDERED:
            super.drawTextWithShadow((TextRenderer)var1.obj, (OrderedText)var1.obj2, var1.intValue, var1.intValue2, var1.intValue3);
            break;
         case TEXT_ORDERED:
            super.drawText((TextRenderer)var1.obj, (OrderedText)var1.obj2, var1.intValue, var1.intValue2, var1.intValue3, var1.flag);
            break;
         case TEXT_SHADOW_TEXT:
            super.drawTextWithShadow((TextRenderer)var1.obj, (Text)var1.obj2, var1.intValue, var1.intValue2, var1.intValue3);
            break;
         case TEXT_TEXT:
            super.drawText((TextRenderer)var1.obj, (Text)var1.obj2, var1.intValue, var1.intValue2, var1.intValue3, var1.flag);
            break;
         case WRAPPED_TEXT:
            super.drawWrappedText((TextRenderer)var1.obj, (StringVisitable)var1.obj2, var1.intValue, var1.intValue2, var1.intValue3, var1.intValue4, var1.flag);
            break;
         case STACK_OVERLAY:
            super.drawStackOverlay((TextRenderer)var1.obj, (ItemStack)var1.obj2, var1.intValue, var1.intValue2);
            break;
         case STACK_OVERLAY_TEXT:
            super.drawStackOverlay((TextRenderer)var1.obj, (ItemStack)var1.obj2, var1.intValue, var1.intValue2, (String)var1.obj3);
            break;
         case SPRITE:
            super.drawSprite((CategoryIcon)var1.obj, var1.floatValue, var1.floatValue2, var1.floatValue3, var1.floatValue4, (ZenithColor)var1.obj2);
            break;
         case ROUNDED_RECT_GRADIENT:
            super.drawRoundedRect(var1.floatValue, var1.floatValue2, var1.floatValue3, var1.floatValue4, (ProxyDotRadiusData)var1.obj, (GlowColor)var1.obj2);
            break;
         case SQUIRCLE:
            super.drawSquircle(
               var1.floatValue, var1.floatValue2, var1.floatValue3, var1.floatValue4, var1.floatValue5, (ProxyDotRadiusData)var1.obj, (ZenithColor)var1.obj2
            );
            break;
         case ROUNDED_BORDER_GRADIENT:
            super.drawRoundedBorder(
               var1.floatValue, var1.floatValue2, var1.floatValue3, var1.floatValue4, var1.floatValue5, (ProxyDotRadiusData)var1.obj, (GlowColor)var1.obj2
            );
            break;
         case ROUNDED_BORDER_RGBA:
            super.drawRoundedBorder(
               var1.floatValue, var1.floatValue2, var1.floatValue3, var1.floatValue4, var1.floatValue5, (ProxyDotRadiusData)var1.obj, (ZenithColor)var1.obj2
            );
            break;
         case ROUNDED_CORNER_ONLY:
            super.drawRoundedCornerOnly(
               var1.floatValue,
               var1.floatValue2,
               var1.floatValue3,
               var1.floatValue4,
               var1.floatValue5,
               (ProxyDotRadiusData)var1.obj,
               (ZenithColor)var1.obj2,
               var1.floatValue6
            );
            break;
         case TEXTURE_RGBA:
            super.drawTexture((Identifier)var1.obj, var1.floatValue, var1.floatValue2, var1.floatValue3, var1.floatValue4, (ZenithColor)var1.obj2);
            break;
         case ARC_BORDER:
            super.drawArcBorder(
               var1.floatValue,
               var1.floatValue2,
               var1.floatValue3,
               var1.floatValue4,
               var1.floatValue5,
               var1.floatValue6,
               var1.floatValue7,
               (ZenithColor)var1.obj
            );
            break;
         case ROUNDED_TEXTURE:
            super.drawRoundedTexture(
               (Identifier)var1.obj,
               var1.floatValue,
               var1.floatValue2,
               var1.floatValue3,
               var1.floatValue4,
               (ProxyDotRadiusData)var1.obj2,
               (ZenithColor)var1.obj3
            );
            break;
         case ROUNDED_TEXTURE_RGBA:
            super.drawRoundedTexture(
               (Identifier)var1.obj,
               var1.floatValue,
               var1.floatValue2,
               var1.floatValue3,
               var1.floatValue4,
               (ProxyDotRadiusData)var1.obj2,
               (ZenithColor)var1.obj3
            );
            break;
         case PLAYER_HEAD:
            super.drawPlayerHeadWithRoundedShader(
               (Identifier)var1.obj, var1.floatValue, var1.floatValue2, var1.floatValue3, (ProxyDotRadiusData)var1.obj2, (ZenithColor)var1.obj3
            );
            break;
         case DRAW:
            ScissorFlusher.flush();
            break;
         case FILL_GRADIENT:
            super.fillGradient(var1.intValue, var1.intValue2, var1.intValue3, var1.intValue4, var1.intValue5, var1.intValue6);
            break;
         case FILL_GRADIENT_Z:
            super.fillGradient(var1.intValue, var1.intValue2, var1.intValue3, var1.intValue4, var1.intValue6, var1.intValue7);
            break;
         case FILL_GRADIENT_LAYER:
            super.fillGradient(var1.intValue, var1.intValue2, var1.intValue3, var1.intValue4, var1.intValue5, var1.intValue6);
            break;
         case SPRITE_STRETCHED:
            super.drawSpriteStretched(
               (RenderPipeline)var1.obj, (Sprite)var1.obj2, var1.intValue, var1.intValue2, var1.intValue3, var1.intValue4, var1.intValue5
            );
            break;
         case SPRITE_STRETCHED_COLOR:
            super.drawSpriteStretched(
               (RenderPipeline)var1.obj, (Sprite)var1.obj2, var1.intValue, var1.intValue2, var1.intValue3, var1.intValue4, var1.intValue5
            );
            break;
         case GUI_TEXTURE:
            super.drawGuiTexture((RenderPipeline)var1.obj, (Identifier)var1.obj2, var1.intValue, var1.intValue2, var1.intValue3, var1.intValue4, var1.intValue5);
            break;
         case GUI_TEXTURE_COLOR:
            super.drawGuiTexture((RenderPipeline)var1.obj, (Identifier)var1.obj2, var1.intValue, var1.intValue2, var1.intValue3, var1.intValue4, var1.intValue5);
            break;
         case GUI_TEXTURE_REGION:
            super.drawGuiTexture(
               (RenderPipeline)var1.obj,
               (Identifier)var1.obj2,
               var1.intValue,
               var1.intValue2,
               var1.intValue3,
               var1.intValue4,
               var1.intValue5,
               var1.intValue6,
               var1.intValue7,
               var1.intValue8,
               var1.intValue9
            );
            break;
         case TEXTURE_UV:
            super.drawTexture(
               (RenderPipeline)var1.obj,
               (Identifier)var1.obj2,
               var1.intValue,
               var1.intValue2,
               var1.floatValue,
               var1.floatValue2,
               var1.intValue3,
               var1.intValue4,
               var1.intValue5,
               var1.intValue6,
               var1.intValue7
            );
            break;
         case TEXTURE_UV_COLOR:
            super.drawTexture(
               (RenderPipeline)var1.obj,
               (Identifier)var1.obj2,
               var1.intValue,
               var1.intValue2,
               var1.floatValue,
               var1.floatValue2,
               var1.intValue3,
               var1.intValue4,
               var1.intValue5,
               var1.intValue6,
               var1.intValue7
            );
            break;
         case TEXTURE_REGION:
            super.drawTexture(
               (RenderPipeline)var1.obj,
               (Identifier)var1.obj2,
               var1.intValue,
               var1.intValue2,
               var1.floatValue,
               var1.floatValue2,
               var1.intValue3,
               var1.intValue4,
               var1.intValue5,
               var1.intValue6,
               var1.intValue7,
               var1.intValue8,
               var1.intValue9
            );
            break;
         case TEXTURE_REGION_COLOR:
            super.drawTexture(
               (RenderPipeline)var1.obj,
               (Identifier)var1.obj2,
               var1.intValue,
               var1.intValue2,
               var1.floatValue,
               var1.floatValue2,
               var1.intValue3,
               var1.intValue4,
               var1.intValue5,
               var1.intValue6,
               var1.intValue7,
               var1.intValue8,
               var1.intValue9
            );
            break;
         case ITEM:
            super.drawItem((ItemStack)var1.obj, var1.intValue, var1.intValue2);
            break;
         case ITEM_BAR:
            super.drawItemBar((ItemStack)var1.obj, var1.intValue, var1.intValue2);
            break;
         case COOLDOWN_PROGRESS:
            super.drawCooldownProgress((ItemStack)var1.obj, var1.intValue, var1.intValue2);
            break;
         case ITEM_SEED:
            super.drawItem((ItemStack)var1.obj, var1.intValue, var1.intValue2, var1.intValue3);
            break;
         case ITEM_SEED_Z:
            super.drawItem((ItemStack)var1.obj, var1.intValue, var1.intValue2, var1.intValue3);
            break;
         case ITEM_WITHOUT_ENTITY:
            super.drawItemWithoutEntity((ItemStack)var1.obj, var1.intValue, var1.intValue2);
            break;
         case ITEM_WITHOUT_ENTITY_SEED:
            super.drawItemWithoutEntity((ItemStack)var1.obj, var1.intValue, var1.intValue2, var1.intValue3);
            break;
         case ITEM_ENTITY:
            super.drawItem((LivingEntity)var1.obj, (ItemStack)var1.obj2, var1.intValue, var1.intValue2, var1.intValue3);
            break;
         case SCISSOR_ENABLE:
            super.enableScissor(var1.intValue, var1.intValue2, var1.intValue3, var1.intValue4);
            break;
         case SCISSOR_DISABLE:
            super.disableScissor();
      }
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, ZenithColor var5) {
      if (this.replaying) {
         super.drawText(var1, var2, var3, var4, var5);
      } else {
         HudQueuedDraw var6 = this.queueCommand(HudQueuedDraw.Type.TEXT_FONT_STRING_RGBA);
         var6.obj = var1;
         var6.obj2 = var2;
         var6.obj3 = tint(var5);
         var6.floatValue = var3;
         var6.floatValue2 = var4;
         Jebriksheet.method2359(var6);
      }
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, ZenithColor var5, boolean var6, float var7, float var8, float var9) {
      if (this.replaying) {
         super.drawText(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      } else {
         HudQueuedDraw var10 = this.queueCommand(HudQueuedDraw.Type.TEXT_FONT_STRING_RGBA_FADEOUT);
         var10.obj = var1;
         var10.obj2 = var2;
         var10.obj3 = tint(var5);
         var10.floatValue = var3;
         var10.floatValue2 = var4;
         var10.flag = var6;
         var10.floatValue3 = var7;
         var10.floatValue4 = var8;
         var10.floatValue5 = var9;
         Jebriksheet.method2359(var10);
      }
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, int var5, boolean var6, float var7, float var8, float var9) {
      if (this.replaying) {
         super.drawText(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      } else {
         HudQueuedDraw var10 = this.queueCommand(HudQueuedDraw.Type.TEXT_FONT_STRING_INT_FADEOUT);
         var10.obj = var1;
         var10.obj2 = var2;
         var10.floatValue = var3;
         var10.floatValue2 = var4;
         var10.intValue = tint(var5);
         var10.flag = var6;
         var10.floatValue3 = var7;
         var10.floatValue4 = var8;
         var10.floatValue5 = var9;
         Jebriksheet.method2359(var10);
      }
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, GlowColor var5) {
      if (this.replaying) {
         super.drawText(var1, var2, var3, var4, var5);
      } else {
         HudQueuedDraw var6 = this.queueCommand(HudQueuedDraw.Type.TEXT_FONT_STRING_GRADIENT);
         var6.obj = var1;
         var6.obj2 = var2;
         var6.obj3 = tint(var5);
         var6.floatValue = var3;
         var6.floatValue2 = var4;
         Jebriksheet.method2359(var6);
      }
   }

   @Override
   public void drawText(Font var1, Text var2, float var3, float var4, int var5) {
      if (this.replaying) {
         super.drawText(var1, var2, var3, var4, var5);
      } else {
         HudQueuedDraw var6 = this.queueCommand(HudQueuedDraw.Type.TEXT_FONT_TEXT_INT);
         var6.obj = var1;
         var6.obj2 = var2;
         var6.floatValue = var3;
         var6.floatValue2 = var4;
         var6.intValue = tint(var5);
         Jebriksheet.method2359(var6);
      }
   }

   @Override
   public void drawText(Font var1, Text var2, float var3, float var4) {
      if (this.replaying) {
         super.drawText(var1, var2, var3, var4);
      } else {
         HudQueuedDraw var5 = this.queueCommand(HudQueuedDraw.Type.TEXT_FONT_TEXT);
         var5.obj = var1;
         var5.obj2 = var2;
         var5.floatValue = var3;
         var5.floatValue2 = var4;
         Jebriksheet.method2359(var5);
      }
   }

   @Override
   public void enableScissor(int x1, int y1, int x2, int y2) {
      if (this.replaying) {
         super.enableScissor(x1, y1, x2, y2);
      } else {
         Jebriksheet.method2361(this, this.queueMatrix(), x1, y1, x2, y2);
      }
   }

   @Override
   public void disableScissor() {
      if (this.replaying) {
         super.disableScissor();
      } else {
         Jebriksheet.method2362();
      }
   }

   @Override
   public void drawRoundedRect(float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, ZenithColor var6) {
      if (this.replaying) {
         super.drawRoundedRect(var1, var2, var3, var4, var5, var6);
      } else {
         Jebriksheet.method2364(this.queueMatrix(), var1, var2, var3, var4, var5, tint(var6));
      }
   }

   @Override
   public void drawRoundedRect(float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, GlowColor var6) {
      if (this.replaying) {
         super.drawRoundedRect(var1, var2, var3, var4, var5, var6);
      } else {
         HudQueuedDraw var7 = this.queueCommand(HudQueuedDraw.Type.ROUNDED_RECT_GRADIENT);
         var7.floatValue = var1;
         var7.floatValue2 = var2;
         var7.floatValue3 = var3;
         var7.floatValue4 = var4;
         var7.obj = var5;
         var7.obj2 = tint(var6);
         Jebriksheet.method2356(var7);
      }
   }

   @Override
   public void drawRect(float var1, float var2, float var3, float var4, ZenithColor var5) {
      if (this.replaying) {
         super.drawRect(var1, var2, var3, var4, var5);
      } else {
         Jebriksheet.method2365(this.queueMatrix(), var1, var2, var3, var4, tint(var5));
      }
   }

   @Override
   public void drawSquircle(float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      if (this.replaying) {
         super.drawSquircle(var1, var2, var3, var4, var5, var6, var7);
      } else {
         HudQueuedDraw var8 = this.queueCommand(HudQueuedDraw.Type.SQUIRCLE);
         var8.floatValue = var1;
         var8.floatValue2 = var2;
         var8.floatValue3 = var3;
         var8.floatValue4 = var4;
         var8.floatValue5 = var5;
         var8.obj = var6;
         var8.obj2 = tint(var7);
         Jebriksheet.method2356(var8);
      }
   }

   @Override
   public void drawSprite(CategoryIcon var1, float var2, float var3, float var4, float var5, ZenithColor var6) {
      if (this.replaying) {
         super.drawSprite(var1, var2, var3, var4, var5, var6);
      } else {
         HudQueuedDraw var7 = this.queueCommand(HudQueuedDraw.Type.SPRITE);
         var7.obj = var1;
         var7.obj2 = tint(var6);
         var7.floatValue = var2;
         var7.floatValue2 = var3;
         var7.floatValue3 = var4;
         var7.floatValue4 = var5;
         Jebriksheet.method2357(var7);
      }
   }

   @Override
   public void drawRoundedBorder(float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, GlowColor var7) {
      if (this.replaying) {
         super.drawRoundedBorder(var1, var2, var3, var4, var5, var6, var7);
      } else {
         HudQueuedDraw var8 = this.queueCommand(HudQueuedDraw.Type.ROUNDED_BORDER_GRADIENT);
         var8.floatValue = var1;
         var8.floatValue2 = var2;
         var8.floatValue3 = var3;
         var8.floatValue4 = var4;
         var8.floatValue5 = var5;
         var8.obj = var6;
         var8.obj2 = tint(var7);
         Jebriksheet.method2357(var8);
      }
   }

   @Override
   public void drawRoundedBorder(float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      if (this.replaying) {
         super.drawRoundedBorder(var1, var2, var3, var4, var5, var6, var7);
      } else {
         HudQueuedDraw var8 = this.queueCommand(HudQueuedDraw.Type.ROUNDED_BORDER_RGBA);
         var8.floatValue = var1;
         var8.floatValue2 = var2;
         var8.floatValue3 = var3;
         var8.floatValue4 = var4;
         var8.floatValue5 = var5;
         var8.obj = var6;
         var8.obj2 = tint(var7);
         Jebriksheet.method2357(var8);
      }
   }

   @Override
   public void drawRoundedCornerOnly(float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7, float var8) {
      if (this.replaying) {
         super.drawRoundedCornerOnly(var1, var2, var3, var4, var5, var6, var7, var8);
      } else {
         HudQueuedDraw var9 = this.queueCommand(HudQueuedDraw.Type.ROUNDED_CORNER_ONLY);
         var9.floatValue = var1;
         var9.floatValue2 = var2;
         var9.floatValue3 = var3;
         var9.floatValue4 = var4;
         var9.floatValue5 = var5;
         var9.floatValue6 = var8;
         var9.obj = var6;
         var9.obj2 = tint(var7);
         Jebriksheet.method2357(var9);
      }
   }

   @Override
   public void drawTexture(Identifier var1, float var2, float var3, float var4, float var5, ZenithColor var6) {
      if (this.replaying) {
         super.drawTexture(var1, var2, var3, var4, var5, var6);
      } else {
         HudQueuedDraw var7 = this.queueCommand(HudQueuedDraw.Type.TEXTURE_RGBA);
         var7.obj = var1;
         var7.obj2 = tint(var6);
         var7.floatValue = var2;
         var7.floatValue2 = var3;
         var7.floatValue3 = var4;
         var7.floatValue4 = var5;
         Jebriksheet.method2357(var7);
      }
   }

   @Override
   public void drawBlurHud(float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      this.drawBlurHudBooleanCheck(
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         (InterfaceModule.INSTANCE.method6817() || InterfaceModule.INSTANCE.method6818()) && RenderShaders.flag,
         InterfaceModule.INSTANCE.method6819()
      );
   }

   @Override
   public void drawBlurHudBooleanCheck(
      float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7, boolean var8, boolean var9
   ) {
      if (this.replaying) {
         super.drawBlurHudBooleanCheck(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      } else {
         float var10 = InterfaceModule.INSTANCE.method6779(var5);
         Jebriksheet.method2363(this.queueMatrix(), var1, var2, var3, var4, var10 > 0.0F ? var10 : var5, var6, tint(var7), var8 && var10 > 0.0F, var9);
         if (InterfaceModule.INSTANCE.method6780()) {
            this.drawRoundedBorder(var1, var2, var3, var4, InterfaceModule.INSTANCE.method6781(), var6, InterfaceModule.INSTANCE.method6782());
         }
      }
   }

   @Override
   public void drawArcBorder(float var1, float var2, float var3, float var4, float var5, float var6, float var7, ZenithColor var8) {
      if (this.replaying) {
         super.drawArcBorder(var1, var2, var3, var4, var5, var6, var7, var8);
      } else {
         HudQueuedDraw var9 = this.queueCommand(HudQueuedDraw.Type.ARC_BORDER);
         var9.floatValue = var1;
         var9.floatValue2 = var2;
         var9.floatValue3 = var3;
         var9.floatValue4 = var4;
         var9.floatValue5 = var5;
         var9.floatValue6 = var6;
         var9.floatValue7 = var7;
         var9.obj = tint(var8);
         Jebriksheet.method2357(var9);
      }
   }

   @Override
   public void drawRoundedTexture(Identifier var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6) {
      if (this.replaying) {
         super.drawRoundedTexture(var1, var2, var3, var4, var5, var6);
      } else {
         HudQueuedDraw var7 = this.queueCommand(HudQueuedDraw.Type.ROUNDED_TEXTURE);
         var7.obj = var1;
         var7.obj2 = var6;
         var7.obj3 = tint(ZenithColor.INSTANCE);
         var7.floatValue = var2;
         var7.floatValue2 = var3;
         var7.floatValue3 = var4;
         var7.floatValue4 = var5;
         Jebriksheet.method2357(var7);
      }
   }

   @Override
   public void drawRoundedTexture(Identifier var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      if (this.replaying) {
         super.drawRoundedTexture(var1, var2, var3, var4, var5, var6, var7);
      } else {
         HudQueuedDraw var8 = this.queueCommand(HudQueuedDraw.Type.ROUNDED_TEXTURE_RGBA);
         var8.obj = var1;
         var8.obj2 = var6;
         var8.obj3 = tint(var7);
         var8.floatValue = var2;
         var8.floatValue2 = var3;
         var8.floatValue3 = var4;
         var8.floatValue4 = var5;
         Jebriksheet.method2357(var8);
      }
   }

   @Override
   public void drawPlayerHeadWithRoundedShader(Identifier var1, float var2, float var3, float var4, ProxyDotRadiusData var5, ZenithColor var6) {
      if (this.replaying) {
         super.drawPlayerHeadWithRoundedShader(var1, var2, var3, var4, var5, var6);
      } else {
         HudQueuedDraw var7 = this.queueCommand(HudQueuedDraw.Type.PLAYER_HEAD);
         var7.obj = var1;
         var7.obj2 = var5;
         var7.obj3 = tint(var6);
         var7.floatValue = var2;
         var7.floatValue2 = var3;
         var7.floatValue3 = var4;
         Jebriksheet.method2357(var7);
      }
   }

   @Override
   public void drawItem(ItemStack item, int x, int y) {
      if (this.replaying) {
         super.drawItem(item, x, y);
      } else if (!itemsHidden()) {
         HudQueuedDraw var4 = this.queueCommand(HudQueuedDraw.Type.ITEM);
         var4.obj = item;
         var4.intValue = x;
         var4.intValue2 = y;
         Jebriksheet.method2358(var4);
      }
   }

   @Override
   public void drawItemBar(ItemStack var1, int var2, int var3) {
      if (this.replaying) {
         super.drawItemBar(var1, var2, var3);
      } else if (!itemsHidden()) {
         HudQueuedDraw var4 = this.queueCommand(HudQueuedDraw.Type.ITEM_BAR);
         var4.obj = var1;
         var4.intValue = var2;
         var4.intValue2 = var3;
         Jebriksheet.method2358(var4);
      }
   }

   @Override
   public void drawCooldownProgress(ItemStack var1, int var2, int var3) {
      if (this.replaying) {
         super.drawCooldownProgress(var1, var2, var3);
      } else if (!itemsHidden()) {
         HudQueuedDraw var4 = this.queueCommand(HudQueuedDraw.Type.COOLDOWN_PROGRESS);
         var4.obj = var1;
         var4.intValue = var2;
         var4.intValue2 = var3;
         Jebriksheet.method2358(var4);
      }
   }

   @Override
   public void drawItem(ItemStack stack, int x, int y, int seed) {
      if (this.replaying) {
         super.drawItem(stack, x, y, seed);
      } else if (!itemsHidden()) {
         HudQueuedDraw var5 = this.queueCommand(HudQueuedDraw.Type.ITEM_SEED);
         var5.obj = stack;
         var5.intValue = x;
         var5.intValue2 = y;
         var5.intValue3 = seed;
         Jebriksheet.method2358(var5);
      }
   }

   public void drawItem(ItemStack var1, int var2, int var3, int var4, int var5) {
      if (this.replaying) {
         super.drawItem(var1, var2, var3, var4);
      } else if (!itemsHidden()) {
         HudQueuedDraw var6 = this.queueCommand(HudQueuedDraw.Type.ITEM_SEED_Z);
         var6.obj = var1;
         var6.intValue = var2;
         var6.intValue2 = var3;
         var6.intValue3 = var4;
         var6.intValue4 = var5;
         Jebriksheet.method2358(var6);
      }
   }

   @Override
   public void drawItemWithoutEntity(ItemStack stack, int x, int y) {
      if (this.replaying) {
         super.drawItemWithoutEntity(stack, x, y);
      } else if (!itemsHidden()) {
         HudQueuedDraw var4 = this.queueCommand(HudQueuedDraw.Type.ITEM_WITHOUT_ENTITY);
         var4.obj = stack;
         var4.intValue = x;
         var4.intValue2 = y;
         Jebriksheet.method2358(var4);
      }
   }

   @Override
   public void drawItemWithoutEntity(ItemStack stack, int x, int y, int seed) {
      if (this.replaying) {
         super.drawItemWithoutEntity(stack, x, y, seed);
      } else if (!itemsHidden()) {
         HudQueuedDraw var5 = this.queueCommand(HudQueuedDraw.Type.ITEM_WITHOUT_ENTITY_SEED);
         var5.obj = stack;
         var5.intValue = x;
         var5.intValue2 = y;
         var5.intValue3 = seed;
         Jebriksheet.method2358(var5);
      }
   }

   @Override
   public void drawItem(LivingEntity entity, ItemStack stack, int x, int y, int seed) {
      if (this.replaying) {
         super.drawItem(entity, stack, x, y, seed);
      } else if (!itemsHidden()) {
         HudQueuedDraw var6 = this.queueCommand(HudQueuedDraw.Type.ITEM_ENTITY);
         var6.obj = entity;
         var6.obj2 = stack;
         var6.intValue = x;
         var6.intValue2 = y;
         var6.intValue3 = seed;
         Jebriksheet.method2358(var6);
      }
   }

   @Override
   public void drawStackOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y) {
      if (this.replaying) {
         super.drawStackOverlay(textRenderer, stack, x, y);
      } else if (!itemsHidden()) {
         HudQueuedDraw var5 = this.queueCommand(HudQueuedDraw.Type.STACK_OVERLAY);
         var5.obj = textRenderer;
         var5.obj2 = stack;
         var5.intValue = x;
         var5.intValue2 = y;
         Jebriksheet.method2359(var5);
      }
   }

   @Override
   public void drawStackOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y, String stackCountText) {
      if (this.replaying) {
         super.drawStackOverlay(textRenderer, stack, x, y, stackCountText);
      } else if (!itemsHidden()) {
         HudQueuedDraw var6 = this.queueCommand(HudQueuedDraw.Type.STACK_OVERLAY_TEXT);
         var6.obj = textRenderer;
         var6.obj2 = stack;
         var6.obj3 = stackCountText;
         var6.intValue = x;
         var6.intValue2 = y;
         Jebriksheet.method2359(var6);
      }
   }

   @Override
   public void draw() {
      if (this.replaying) {
         ScissorFlusher.flush();
      } else {
         Jebriksheet.method2357(this.queueCommand(HudQueuedDraw.Type.DRAW));
      }
   }

   @Override
   public void fill(int x1, int y1, int x2, int y2, int color) {
      if (this.replaying) {
         super.fill(x1, y1, x2, y2, color);
      } else {
         this.queueRect(x1, y1, x2, y2, color);
      }
   }

   public void fill(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (this.replaying) {
         super.fill(var1, var2, var3, var4, var6);
      } else {
         this.queueRect(var1, var2, var3, var4, var6);
      }
   }

   @Override
   public void fill(RenderPipeline pipeline, int x1, int y1, int x2, int y2, int color) {
      if (this.replaying) {
         super.fill(pipeline, x1, y1, x2, y2, color);
      } else {
         this.queueRect(x1, y1, x2, y2, color);
      }
   }

   public void fill(RenderLayer var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (this.replaying) {
         super.fill(var2, var3, var4, var5, var7);
      } else {
         this.queueRect(var2, var3, var4, var5, var7);
      }
   }

   private void queueRect(int var1, int var2, int var3, int var4, int var5) {
      int var6 = Math.min(var1, var3);
      int var7 = Math.min(var2, var4);
      int var8 = Math.max(var1, var3);
      int var9 = Math.max(var2, var4);
      Jebriksheet.method2365(this.queueMatrix(), var6, var7, var8 - var6, var9 - var7, tint(ZenithColor.method7291(var5)));
   }

   @Override
   public void fillGradient(int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
      if (this.replaying) {
         super.fillGradient(startX, startY, endX, endY, colorStart, colorEnd);
      } else {
         HudQueuedDraw var7 = this.queueCommand(HudQueuedDraw.Type.FILL_GRADIENT);
         var7.intValue = startX;
         var7.intValue2 = startY;
         var7.intValue3 = endX;
         var7.intValue4 = endY;
         var7.intValue5 = tint(colorStart);
         var7.intValue6 = tint(colorEnd);
         Jebriksheet.method2357(var7);
      }
   }

   public void fillGradient(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (this.replaying) {
         super.fillGradient(var1, var2, var3, var4, var6, var7);
      } else {
         HudQueuedDraw var8 = this.queueCommand(HudQueuedDraw.Type.FILL_GRADIENT_Z);
         var8.intValue = var1;
         var8.intValue2 = var2;
         var8.intValue3 = var3;
         var8.intValue4 = var4;
         var8.intValue5 = var5;
         var8.intValue6 = tint(var6);
         var8.intValue7 = tint(var7);
         Jebriksheet.method2357(var8);
      }
   }

   public void fillGradient(RenderLayer var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      if (this.replaying) {
         super.fillGradient(var2, var3, var4, var5, var6, var7);
      } else {
         HudQueuedDraw var9 = this.queueCommand(HudQueuedDraw.Type.FILL_GRADIENT_LAYER);
         var9.obj = var1;
         var9.intValue = var2;
         var9.intValue2 = var3;
         var9.intValue3 = var4;
         var9.intValue4 = var5;
         var9.intValue5 = tint(var6);
         var9.intValue6 = tint(var7);
         var9.intValue7 = var8;
         Jebriksheet.method2357(var9);
      }
   }

   @Override
   public void drawTextWithShadow(TextRenderer textRenderer, String text, int x, int y, int color) {
      if (this.replaying) {
         super.drawTextWithShadow(textRenderer, text, x, y, color);
      } else {
         HudQueuedDraw var6 = this.queueCommand(HudQueuedDraw.Type.TEXT_SHADOW_STRING);
         var6.obj = textRenderer;
         var6.obj2 = text;
         var6.intValue = x;
         var6.intValue2 = y;
         var6.intValue3 = tint(color);
         Jebriksheet.method2359(var6);
      }
   }

   @Override
   public void drawText(TextRenderer textRenderer, String text, int x, int y, int color, boolean shadow) {
      if (this.replaying) {
         super.drawText(textRenderer, text, x, y, color, shadow);
      } else {
         HudQueuedDraw var7 = this.queueCommand(HudQueuedDraw.Type.TEXT_STRING);
         var7.obj = textRenderer;
         var7.obj2 = text;
         var7.intValue = x;
         var7.intValue2 = y;
         var7.intValue3 = tint(color);
         var7.flag = shadow;
         Jebriksheet.method2359(var7);
      }
   }

   @Override
   public void drawTextWithShadow(TextRenderer textRenderer, OrderedText text, int x, int y, int color) {
      if (this.replaying) {
         super.drawTextWithShadow(textRenderer, text, x, y, color);
      } else {
         HudQueuedDraw var6 = this.queueCommand(HudQueuedDraw.Type.TEXT_SHADOW_ORDERED);
         var6.obj = textRenderer;
         var6.obj2 = text;
         var6.intValue = x;
         var6.intValue2 = y;
         var6.intValue3 = tint(color);
         Jebriksheet.method2359(var6);
      }
   }

   @Override
   public void drawText(TextRenderer textRenderer, OrderedText text, int x, int y, int color, boolean shadow) {
      if (this.replaying) {
         super.drawText(textRenderer, text, x, y, color, shadow);
      } else {
         HudQueuedDraw var7 = this.queueCommand(HudQueuedDraw.Type.TEXT_ORDERED);
         var7.obj = textRenderer;
         var7.obj2 = text;
         var7.intValue = x;
         var7.intValue2 = y;
         var7.intValue3 = tint(color);
         var7.flag = shadow;
         Jebriksheet.method2359(var7);
      }
   }

   @Override
   public void drawTextWithShadow(TextRenderer textRenderer, Text text, int x, int y, int color) {
      if (this.replaying) {
         super.drawTextWithShadow(textRenderer, text, x, y, color);
      } else {
         HudQueuedDraw var6 = this.queueCommand(HudQueuedDraw.Type.TEXT_SHADOW_TEXT);
         var6.obj = textRenderer;
         var6.obj2 = text;
         var6.intValue = x;
         var6.intValue2 = y;
         var6.intValue3 = tint(color);
         Jebriksheet.method2359(var6);
      }
   }

   @Override
   public void drawText(TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow) {
      if (this.replaying) {
         super.drawText(textRenderer, text, x, y, color, shadow);
      } else {
         HudQueuedDraw var7 = this.queueCommand(HudQueuedDraw.Type.TEXT_TEXT);
         var7.obj = textRenderer;
         var7.obj2 = text;
         var7.intValue = x;
         var7.intValue2 = y;
         var7.intValue3 = tint(color);
         var7.flag = shadow;
         Jebriksheet.method2359(var7);
      }
   }

   @Override
   public void drawWrappedText(TextRenderer textRenderer, StringVisitable text, int x, int y, int width, int color, boolean shadow) {
      if (this.replaying) {
         super.drawWrappedText(textRenderer, text, x, y, width, color, shadow);
      } else {
         HudQueuedDraw var8 = this.queueCommand(HudQueuedDraw.Type.WRAPPED_TEXT);
         var8.obj = textRenderer;
         var8.obj2 = text;
         var8.intValue = x;
         var8.intValue2 = y;
         var8.intValue3 = width;
         var8.intValue4 = tint(color);
         var8.flag = shadow;
         Jebriksheet.method2359(var8);
      }
   }

   @Override
   public void drawSpriteStretched(RenderPipeline pipeline, Sprite sprite, int x, int y, int width, int height) {
      if (this.replaying) {
         super.drawSpriteStretched(pipeline, sprite, x, y, width, height);
      } else {
         HudQueuedDraw var7 = this.queueCommand(HudQueuedDraw.Type.SPRITE_STRETCHED);
         var7.obj = pipeline;
         var7.obj2 = sprite;
         var7.intValue = x;
         var7.intValue2 = y;
         var7.intValue3 = width;
         var7.intValue4 = height;
         var7.intValue5 = tint(-1);
         Jebriksheet.method2357(var7);
      }
   }

   @Override
   public void drawSpriteStretched(RenderPipeline pipeline, Sprite sprite, int x, int y, int width, int height, int color) {
      if (this.replaying) {
         super.drawSpriteStretched(pipeline, sprite, x, y, width, height, color);
      } else {
         HudQueuedDraw var8 = this.queueCommand(HudQueuedDraw.Type.SPRITE_STRETCHED_COLOR);
         var8.obj = pipeline;
         var8.obj2 = sprite;
         var8.intValue = x;
         var8.intValue2 = y;
         var8.intValue3 = width;
         var8.intValue4 = height;
         var8.intValue5 = tint(color);
         Jebriksheet.method2357(var8);
      }
   }

   @Override
   public void drawGuiTexture(RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
      if (this.replaying) {
         super.drawGuiTexture(pipeline, sprite, x, y, width, height);
      } else {
         HudQueuedDraw var7 = this.queueCommand(HudQueuedDraw.Type.GUI_TEXTURE);
         var7.obj = pipeline;
         var7.obj2 = sprite;
         var7.intValue = x;
         var7.intValue2 = y;
         var7.intValue3 = width;
         var7.intValue4 = height;
         var7.intValue5 = tint(-1);
         Jebriksheet.method2357(var7);
      }
   }

   @Override
   public void drawGuiTexture(RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, int color) {
      if (this.replaying) {
         super.drawGuiTexture(pipeline, sprite, x, y, width, height, color);
      } else {
         HudQueuedDraw var8 = this.queueCommand(HudQueuedDraw.Type.GUI_TEXTURE_COLOR);
         var8.obj = pipeline;
         var8.obj2 = sprite;
         var8.intValue = x;
         var8.intValue2 = y;
         var8.intValue3 = width;
         var8.intValue4 = height;
         var8.intValue5 = tint(color);
         Jebriksheet.method2357(var8);
      }
   }

   @Override
   public void drawGuiTexture(
      RenderPipeline pipeline, Identifier sprite, int textureWidth, int textureHeight, int u, int v, int x, int y, int width, int height
   ) {
      if (this.replaying) {
         super.drawGuiTexture(pipeline, sprite, textureWidth, textureHeight, u, v, x, y, width, height);
      } else {
         HudQueuedDraw var11 = this.queueCommand(HudQueuedDraw.Type.GUI_TEXTURE_REGION);
         var11.obj = pipeline;
         var11.obj2 = sprite;
         var11.intValue = textureWidth;
         var11.intValue2 = textureHeight;
         var11.intValue3 = u;
         var11.intValue4 = v;
         var11.intValue5 = x;
         var11.intValue6 = y;
         var11.intValue7 = width;
         var11.intValue8 = height;
         var11.intValue9 = tint(-1);
         Jebriksheet.method2357(var11);
      }
   }

   @Override
   public void drawTexture(
      RenderPipeline pipeline, Identifier sprite, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color
   ) {
      if (this.replaying) {
         super.drawTexture(pipeline, sprite, x, y, u, v, width, height, textureWidth, textureHeight, color);
      } else {
         HudQueuedDraw var12 = this.queueCommand(HudQueuedDraw.Type.TEXTURE_UV_COLOR);
         var12.obj = pipeline;
         var12.obj2 = sprite;
         var12.intValue = x;
         var12.intValue2 = y;
         var12.floatValue = u;
         var12.floatValue2 = v;
         var12.intValue3 = width;
         var12.intValue4 = height;
         var12.intValue5 = textureWidth;
         var12.intValue6 = textureHeight;
         var12.intValue7 = tint(color);
         Jebriksheet.method2357(var12);
      }
   }

   @Override
   public void drawTexture(
      RenderPipeline pipeline, Identifier sprite, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight
   ) {
      if (this.replaying) {
         super.drawTexture(pipeline, sprite, x, y, u, v, width, height, textureWidth, textureHeight);
      } else {
         HudQueuedDraw var11 = this.queueCommand(HudQueuedDraw.Type.TEXTURE_UV);
         var11.obj = pipeline;
         var11.obj2 = sprite;
         var11.intValue = x;
         var11.intValue2 = y;
         var11.floatValue = u;
         var11.floatValue2 = v;
         var11.intValue3 = width;
         var11.intValue4 = height;
         var11.intValue5 = textureWidth;
         var11.intValue6 = textureHeight;
         var11.intValue7 = tint(-1);
         Jebriksheet.method2357(var11);
      }
   }

   @Override
   public void drawTexture(
      RenderPipeline pipeline,
      Identifier sprite,
      int x,
      int y,
      float u,
      float v,
      int width,
      int height,
      int regionWidth,
      int regionHeight,
      int textureWidth,
      int textureHeight
   ) {
      if (this.replaying) {
         super.drawTexture(pipeline, sprite, x, y, u, v, width, height, regionWidth, regionHeight, textureWidth, textureHeight);
      } else {
         HudQueuedDraw var13 = this.queueCommand(HudQueuedDraw.Type.TEXTURE_REGION);
         var13.obj = pipeline;
         var13.obj2 = sprite;
         var13.intValue = x;
         var13.intValue2 = y;
         var13.floatValue = u;
         var13.floatValue2 = v;
         var13.intValue3 = width;
         var13.intValue4 = height;
         var13.intValue5 = regionWidth;
         var13.intValue6 = regionHeight;
         var13.intValue7 = textureWidth;
         var13.intValue8 = textureHeight;
         var13.intValue9 = tint(-1);
         Jebriksheet.method2357(var13);
      }
   }

   @Override
   public void drawTexture(
      RenderPipeline pipeline,
      Identifier sprite,
      int x,
      int y,
      float u,
      float v,
      int width,
      int height,
      int regionWidth,
      int regionHeight,
      int textureWidth,
      int textureHeight,
      int color
   ) {
      if (this.replaying) {
         super.drawTexture(pipeline, sprite, x, y, u, v, width, height, regionWidth, regionHeight, textureWidth, textureHeight, color);
      } else {
         HudQueuedDraw var14 = this.queueCommand(HudQueuedDraw.Type.TEXTURE_REGION_COLOR);
         var14.obj = pipeline;
         var14.obj2 = sprite;
         var14.intValue = x;
         var14.intValue2 = y;
         var14.floatValue = u;
         var14.floatValue2 = v;
         var14.intValue3 = width;
         var14.intValue4 = height;
         var14.intValue5 = regionWidth;
         var14.intValue6 = regionHeight;
         var14.intValue7 = textureWidth;
         var14.intValue8 = textureHeight;
         var14.intValue9 = tint(color);
         Jebriksheet.method2357(var14);
      }
   }
}

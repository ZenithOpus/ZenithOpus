package org.zenith.utility.render.display.base;

import java.util.Objects;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.utility.mixin.accessors.DrawContextAccessor;
import org.zenith.utility.render.RectBatch;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.RoundedRectBatch2;
import org.zenith.utility.render.ScissorFlusher;

public class CustomDrawContext extends DrawContext implements MinecraftAccess {
   private final RoundedRectBatch2 roundedRectBatch = new RoundedRectBatch2();
   private final RectBatch rectBatch = new RectBatch();

   public CustomDrawContext(DrawContext var1) {
      super(mc, ((DrawContextAccessor)(Object)var1).zenith$state(), ((DrawContextAccessor)(Object)var1).zenith$mouseX(), ((DrawContextAccessor)(Object)var1).zenith$mouseY());
      this.matrices = var1.matrices;
      this.scissorStack = var1.scissorStack;
   }

   public static CustomDrawContext of(DrawContext var0) {
      return new CustomDrawContext(var0);
   }

   public void draw() {
      ScissorFlusher.flush();
   }

   public void drawText(Font var1, String var2, float var3, float var4, ZenithColor var5) {
      MsdfRenderer.renderText(var1.getFont(), var2, var1.getSize(), var5.method7287(), MatrixUtils.method664(this.getMatrices()), var3, var4, 0.0F);
   }

   public void drawText(Font var1, String var2, float var3, float var4, ZenithColor var5, boolean var6, float var7, float var8, float var9) {
      this.drawText(var1, var2, var3, var4, var5.method7287(), var6, var7, var8, var9);
   }

   public void drawText(Font var1, String var2, float var3, float var4, int var5, boolean var6, float var7, float var8, float var9) {
      MsdfRenderer.renderText(var1.getFont(), var2, var1.getSize(), var5, MatrixUtils.method664(this.getMatrices()), var3, var4, 0.0F, var6, var7, var8, var9);
   }

   public void enableScissor(float var1, float var2, float var3, float var4) {
      this.enableScissor((int)Math.floor(var1), (int)Math.floor(var2), (int)Math.ceil(var3), (int)Math.ceil(var4));
   }

   @Override
   public void enableScissor(int x1, int y1, int x2, int y2) {
      super.enableScissor(x1, y1, x2, y2);
   }

   @Override
   public void disableScissor() {
      super.disableScissor();
   }

   public void drawText(Font var1, String var2, float var3, float var4, GlowColor var5) {
      MsdfRenderer.renderText(var1.getFont(), var2, var1.getSize(), var5, MatrixUtils.method664(this.getMatrices()), var3, var4, 0.0F);
   }

   public void drawText(Font var1, Text var2, float var3, float var4, int var5) {
      MsdfRenderer.renderText(var1.getFont(), var2, var1.getSize(), MatrixUtils.method664(this.getMatrices()), var3, var4, 0.0F, false, 0.0F, 1.0F, 0.0F, var5);
   }

   public void drawText(Font var1, Text var2, float var3, float var4) {
      MsdfRenderer.renderText(var1.getFont(), var2, var1.getSize(), MatrixUtils.method664(this.getMatrices()), var3, var4, 0.0F);
   }

   public void drawSquircle(float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      RenderShaders.method5266(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var5, var6, var7);
   }

   public void drawRoundedRect(float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, ZenithColor var6) {
      RenderShaders.method5268(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var5, var6);
   }

   public void drawRoundedRect(float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, GlowColor var6) {
      RenderShaders.method5270(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var5, var6);
   }

   public void drawRect(float var1, float var2, float var3, float var4, ZenithColor var5) {
      RenderShaders.method5260(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var5);
   }

   public void beginRectBatch() {
      this.rectBatch.begin();
   }

   public void drawRectBatched(float var1, float var2, float var3, float var4, ZenithColor var5) {
      this.rectBatch.method1756(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var5);
   }

   public void flushRects() {
      MsdfRenderer.flushBatch();
      this.rectBatch.flush();
   }

   public void beginRoundedRectBatch(ProxyDotRadiusData var1) {
      this.roundedRectBatch.method1534(var1);
   }

   public void drawRoundedRectBatched(float var1, float var2, float var3, float var4, ProxyDotRadiusData var5, ZenithColor var6) {
      this.roundedRectBatch.method1534(var5);
      this.roundedRectBatch.method1536(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var6);
   }

   public void flushRoundedRects() {
      MsdfRenderer.flushBatch();
      this.roundedRectBatch.flush();
   }

   public boolean isRoundedRectBatchActive() {
      return this.roundedRectBatch.isFlag();
   }

   public int drawTextWithBackground(TextRenderer var1, Text var2, int var3, int var4, int var5, ProxyDotRadiusData var6, ZenithColor var7, ZenithColor var8) {
      int var9 = var3 - 3;
      int var10 = var4 - 2;
      int var11 = var5 + 6;
      Objects.requireNonNull(var1);
      this.drawRoundedRect((float)var9, (float)var10, (float)var11, 13.0F, var6, var8);
      this.drawText(var1, var2, var3, var4, var7.method7287(), true);
      return var1.getWidth(var2);
   }

   public void drawSprite(CategoryIcon var1, float var2, float var3, float var4, float var5, ZenithColor var6) {
      RenderShaders.method5300(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var5, var6);
   }

   public void drawRoundedCorner(float var1, float var2, float var3, float var4, float var5, float var6, ZenithColor var7, ProxyDotRadiusData var8) {
      var3 = Math.round(var3);
      var4 = Math.round(var4);
      this.enableScissor((int)Math.ceil(var1 - 10.0F), (int)(var2 - 10.0F), (int)(var1 + var6), (int)(var2 + var6));
      this.drawRoundedBorder(var1, var2, var3, var4, var5, var8, var7);
      this.disableScissor();
      this.enableScissor((int)(var1 + var3 - var6), (int)(var2 - 10.0F), (int)(var1 + var3 + 10.0F), (int)(var2 + var6));
      this.drawRoundedBorder(var1, var2, var3, var4, var5, var8, var7);
      this.disableScissor();
      this.enableScissor((int)(var1 - 10.0F), (int)(var2 + var4 - var6), (int)(var1 + var6), (int)(var2 + var4 + 10.0F));
      this.drawRoundedBorder(var1, var2, var3, var4, var5, var8, var7);
      this.disableScissor();
      this.enableScissor((int)(var1 + var3 - var6), (int)(var2 + var4 - var6), (int)(var1 + var3 + 10.0F), (int)(var2 + var4 + 10.0F));
      this.drawRoundedBorder(var1, var2, var3, var4, var5, var8, var7);
      this.disableScissor();
   }

   public void drawRoundedCornerOnly(float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7, float var8) {
      RenderShaders.method5279(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void drawRoundedBorder(float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, GlowColor var7) {
      RenderShaders.method5271(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var5, var6, var7);
   }

   public void drawRoundedBorder(float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      RenderShaders.method5272(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var5, var6, var7);
   }

   public void drawTexture(Identifier var1, float var2, float var3, float var4, float var5, ZenithColor var6) {
      RenderShaders.method5280(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var5, var6);
   }

   public void drawBlurHud(float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      RenderShaders.method5286(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var5, var6, var7);
   }

   public void drawBlurHudBooleanCheck(
      float var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7, boolean var8, boolean var9
   ) {
      RenderShaders.method5287(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void drawArcBorder(float var1, float var2, float var3, float var4, float var5, float var6, float var7, ZenithColor var8) {
      RenderShaders.method5275(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void drawRoundedTexture(Identifier var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6) {
      RenderShaders.method5292(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var5, var6);
   }

   public void drawRoundedTexture(Identifier var1, float var2, float var3, float var4, float var5, ProxyDotRadiusData var6, ZenithColor var7) {
      RenderShaders.method5294(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var5, var6, var7);
   }

   public void drawRoundedTexture(
      Identifier var1,
      float var2,
      float var3,
      float var4,
      float var5,
      ProxyDotRadiusData var6,
      ZenithColor var7,
      float var8,
      float var9,
      float var10,
      float var11
   ) {
      RenderShaders.method5296(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public void drawPlayerHeadWithRoundedShader(Identifier var1, float var2, float var3, float var4, ProxyDotRadiusData var5, ZenithColor var6) {
      RenderShaders.method5291(MatrixUtils.method664(this.getMatrices()), var1, var2, var3, var4, var5, var6);
   }

   public void drawItemBar(ItemStack var1, int var2, int var3) {
      ((DrawContextAccessor)(Object)this).callDrawItemBar(var1, var2, var3);
   }

   public void drawCooldownProgress(ItemStack var1, int var2, int var3) {
      ((DrawContextAccessor)(Object)this).callDrawCooldownProgress(var1, var2, var3);
   }

   public void pushMatrix() {
      this.getMatrices().pushMatrix();
   }

   public void popMatrix() {
      this.getMatrices().popMatrix();
   }
}

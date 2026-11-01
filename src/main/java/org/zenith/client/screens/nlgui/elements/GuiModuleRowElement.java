package org.zenith.client.screens.nlgui.elements;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.BindCapture;
import org.zenith.client.screens.nlgui.HoverInfo;
import org.zenith.client.screens.nlgui.MenuGlyph;
import org.zenith.client.screens.nlgui.MouseBindTarget;
import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.render.TextureFilterUtils;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class GuiModuleRowElement extends Element implements MouseBindTarget {
   private static final float MARKER_WIDTH = 2.0F;
   private static final float TEXT_INSET = 8.0F;
   private static final float ROW_GAP = 5.0F;
   private static final float HEART_SIZE = 6.0F;
   private static final Identifier HEART_OUTLINE = Zenith.method1501("textures/heart_outline.png");
   private static final Identifier HEART_FILLED = Zenith.method1501("textures/heart_filled.png");
   private static final Set<AbstractTexture> SMOOTHED = Collections.newSetFromMap(new WeakHashMap<>());
   private final BindLabel bindLabel = new BindLabel();
   private final Animation labelSlot = new Animation(240L, 0.0F, Easing.INSTANCE12);
   private boolean labelSlotKnown;
   private final Module module;
   private final Animation animationEnable = new Animation(160L, Easing.INSTANCE12);
   private final Animation animationHovered = new Animation(120L, Easing.INSTANCE12);
   private final Animation animationSelected = new Animation(120L, Easing.INSTANCE12);
   private final Animation searchVisibleAnimation = new Animation(180L, 1.0F, Easing.INSTANCE13);
   private static final long FAVOURITE_MS = 700L;
   private static final long UNFAVOURITE_MS = 320L;
   private static final int SPARKS = 10;
   private static final Random SPARK_RNG = new Random();
   private long favouriteAt = -1L;
   private long unfavouriteAt = -1L;
   private static final long TOGGLE_MS = 560L;
   private static final long TOGGLE_OFF_MS = 300L;
   private Boolean seenEnabled;
   private long toggledOnAt = -1L;
   private long toggledOffAt = -1L;
   private final float[] sparkAngle = new float[10];
   private final float[] sparkReach = new float[10];
   private static final float SLIDE_SPEED = 0.35F;
   private static final float SLIDE_SNAP = 0.15F;
   private float slideOffset;
   private boolean slideKnown;
   private BoundsData bounds;
   private BoundsData bindBounds;
   private BoundsData heartBounds;
   private boolean binding;
   private boolean searchVisible = true;
   private float width = 236.0F;
   private static final Map<String, String> LOWER_NAMES = new ConcurrentHashMap<>();
   private boolean showCategoryIcon;

   private static void smooth(Identifier var0) {
      try {
         AbstractTexture var1 = MinecraftClient.getInstance().getTextureManager().getTexture(var0);
         if (var1 != null && SMOOTHED.add(var1)) {
            TextureFilterUtils.smooth(var0);
         }
      } catch (Exception var2) {
      }
   }

   public GuiModuleRowElement(Module var1) {
      this.module = var1;
      if (var1.isEnabled()) {
         this.animationEnable.method2809(1.0F);
      }
   }

   @Override
   public String getName() {
      return this.module.getName();
   }

   @Override
   public float getHeight() {
      return 16.0F;
   }

   @Override
   public float getWidth() {
      return this.width;
   }

   public boolean isEnable() {
      return this.module.isEnabled();
   }

   public boolean isSelected() {
      return Zenith.getInstance().getNLMenuScreen().getInspectorPanel().isShowing(this.module);
   }

   public boolean isBinding() {
      if (this.binding && BindCapture.owner() != this) {
         this.binding = false;
      }

      return this.binding;
   }

   public void setSearchVisible(boolean var1) {
      this.searchVisible = var1;
      if (!var1) {
         this.cancelBinding();
      }
   }

   public void cancelBinding() {
      this.binding = false;
      BindCapture.end(this);
   }

   public void resetSearchVisible(boolean var1) {
      this.searchVisible = var1;
      this.searchVisibleAnimation.method2809(var1 ? 1.0F : 0.0F);
      this.forgetSlide();
   }

   public float slideTo(float var1) {
      if (!this.slideKnown) {
         this.slideKnown = true;
         this.slideOffset = var1;
         return this.slideOffset;
      } else {
         float var2;
         if (Math.abs((var2 = MathHelper.lerp(0.35F, this.slideOffset, var1)) - var1) < 0.15F) {
            var2 = var1;
         }

         this.slideOffset = var2;
         return var2;
      }
   }

   public void forgetSlide() {
      this.slideKnown = false;
   }

   public float updateSearchVisible() {
      return this.searchVisibleAnimation.method2808(this.searchVisible ? 1.0F : 0.0F);
   }

   public float getSearchVisibleProgress() {
      return this.searchVisibleAnimation.getValue();
   }

   public boolean shouldRenderInSearch() {
      return this.searchVisible || this.searchVisibleAnimation.getValue() > 0.01F;
   }

   public boolean matchesOwnSearch(String var1) {
      return lowerName(this.module).contains(var1);
   }

   public boolean matchesSearch(String var1) {
      if (this.matchesOwnSearch(var1)) {
         return true;
      } else {
         for (Module var3 : this.module.getSurfacedModules()) {
            if (lowerName(var3).contains(var1)) {
               return true;
            }
         }

         return false;
      }
   }

   private static String lowerName(Module var0) {
      String var1 = var0.getName();
      return LOWER_NAMES.computeIfAbsent(var1, String::toLowerCase);
   }

   public void render(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, BoundsData var7) {
      this.isBinding();
      ZenithStyle var8 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      if (var8 != null) {
         this.bounds = new BoundsData(var4, var5, this.width, 16.0F);
         boolean var9 = this.bounds.contains(var2, var3) && (var7 == null || var7.contains(var2, var3));
         float var10 = this.animationEnable.method2808(this.module.isEnabled() ? 1.0F : 0.0F);
         float var11 = this.animationHovered.method2808(var9 ? 1.0F : 0.0F);
         float var12 = this.animationSelected.method2808(this.isSelected() ? 1.0F : 0.0F);
         ZenithColor var13 = var8.getPrimaryColor().getZenithColor();
         float var14;
         if ((var14 = Math.max(var12, var11 * 0.5F)) > 0.002F) {
            ZenithColor var15 = var12 >= var11 * 0.5F
               ? var8.getDisableActiveBg().getZenithColor()
               : var8.getFieldSurfaceBackground().getZenithColor().method7294(2.0F);
            var1.drawRect(var4, var5, this.width, 16.0F, var15.method7294(var14 * var6));
         }

         if (var12 > 0.01F) {
            var1.drawRect(var4, var5 + 2.0F, 2.0F, 12.0F, var8.getPrimaryColor().getZenithColor().method7294(var6 * var12));
         }

         float var45 = var4 + 8.0F;
         float var16 = var5 + 5.0F;
         this.heartBounds = new BoundsData(var45 - 2.0F, var5, 10.0F, 16.0F);
         boolean var17 = this.heartBounds.contains(var2, var3) && (var7 == null || var7.contains(var2, var3));
         boolean var18 = this.module.isPriority();
         long var19 = System.currentTimeMillis();
         float var21 = progressOf(this.favouriteAt, 700L, var19);
         float var22 = progressOf(this.unfavouriteAt, 320L, var19);
         if (var21 >= 1.0F) {
            this.favouriteAt = -1L;
         }

         if (var22 >= 1.0F) {
            this.unfavouriteAt = -1L;
         }

         if (var21 < 1.0F) {
            this.renderFavouriteSweep(var1, var4, var5, var21, var13, var6);
         }

         boolean var23 = this.module.isEnabled();
         if (this.seenEnabled != null && this.seenEnabled != var23) {
            if (var23) {
               this.toggledOnAt = var19;
               this.toggledOffAt = -1L;
            } else {
               this.toggledOffAt = var19;
               this.toggledOnAt = -1L;
            }
         }

         this.seenEnabled = var23;
         float var24 = progressOf(this.toggledOnAt, 560L, var19);
         float var25 = progressOf(this.toggledOffAt, 300L, var19);
         if (var24 >= 1.0F) {
            this.toggledOnAt = -1L;
         }

         if (var25 >= 1.0F) {
            this.toggledOffAt = -1L;
         }

         if (var24 < 1.0F && var21 >= 1.0F) {
            this.renderSweep(var1, var4, var5, var24, var13, var6, 0.55F);
         }

         if (var25 < 1.0F) {
            var1.drawRect(var4, var5, this.width, 16.0F, var8.getTextTertiary().getZenithColor().method7294(0.1F * (1.0F - var25) * (1.0F - var25) * var6));
         }

         ZenithColor var26 = var18 ? var13 : var8.getTextTertiary().getZenithColor().method7294(var17 ? 0.85F : 0.45F);
         if (var21 < 1.0F) {
            var26 = var26.method7295(ZenithColor.INSTANCE, 0.7F * (float)Math.exp(-6.0F * var21) * Math.min(1.0F, var21 * 12.0F));
         }

         float var27 = heartScale(var21, var22);
         float var28 = var45 + 3.0F;
         float var29 = var16 + 3.0F;
         if (var21 < 1.0F) {
            this.renderFavouriteBurst(var1, var28, var29, var21, var13, var6);
         }

         if (var22 < 1.0F) {
            renderRing(var1, var28, var29, var22, 5.0F, var8.getTextTertiary().getZenithColor().method7294(0.6F), var6);
         }

         Identifier var30 = var18 ? HEART_FILLED : HEART_OUTLINE;
         smooth(var30);
         float var31 = 6.0F * var27;
         var1.drawTexture(var30, var28 - var31 / 2.0F, var29 - var31 / 2.0F, var31, var31, var26.method7294(var6));
         float var33 = var4 + this.width - 8.0F + 5.0F;
         Font var34 = Fonts.NEW_MEDIUM.getFont(5.01F);
         String var35 = BindLabel.text(this.binding, this.module.getKeyCode(), this.module.getKeyModifiers());
         float var36 = var35 == null ? 0.0F : var34.width(var35) + 5.0F;
         if (!this.labelSlotKnown) {
            this.labelSlotKnown = true;
            this.labelSlot.method2809(var36);
         }

         float var37 = var33 - this.labelSlot.method2808(var36) - 5.0F;
         Font var38 = (var10 > 0.5F ? Fonts.NEW_SEMIBOLD : Fonts.NEW_MEDIUM).getFont(5.5F);
         ZenithColor var39 = var8.getTextTertiary()
            .getZenithColor()
            .method7295(var8.getTextEnable().getZenithColor(), Math.max(var10, var11 * 0.45F))
            .method7294(var6);
         float var40 = var45 + 6.0F + 5.0F;
         float var41 = var5 + (16.0F - var38.height()) / 2.0F;
         if (this.showCategoryIcon) {
            MenuGlyph.forCategory(this.module.getCategory()).draw(var1, var40, var5 + 5.0F, var39);
            var40 += 10.0F;
         }

         String var42 = var38.clip(this.module.getName(), var37 - var40);
         var1.drawText(var38, var42, var40, var41, var39);
         float var43 = var40 + var38.width(var42);
         this.bindBounds = this.bindLabel.render(var1, var2, var3, var5, 16.0F, var33 - 5.0F, var35, this.binding, var34, var43 + 5.0F, var9, var6, var7, var8);
         if (var9) {
            String var44 = this.module.getDescription();
            if (var44 != null && !var44.isBlank()) {
               HoverInfo.request(this.module.getName(), var44);
            }
         }
      }
   }

   private void startFavouriteBurst() {
      this.unfavouriteAt = -1L;
      this.favouriteAt = System.currentTimeMillis();
      float var1 = (float) (Math.PI / 5);

      for (int var2 = 0; var2 < 10; var2++) {
         this.sparkAngle[var2] = var2 * var1 + (SPARK_RNG.nextFloat() - 0.5F) * var1 * 0.8F;
         this.sparkReach[var2] = 7.0F + SPARK_RNG.nextFloat() * 6.0F;
      }
   }

   private static float progressOf(long var0, long var2, long var4) {
      return var0 < 0L ? 1.0F : MathHelper.clamp((float)(var4 - var0) / (float)var2, 0.0F, 1.0F);
   }

   private static float heartScale(float var0, float var1) {
      if (var0 < 1.0F) {
         if (var0 < 0.16F) {
            float var3 = var0 / 0.16F;
            return 1.0F + 0.85F * (1.0F - (1.0F - var3) * (1.0F - var3));
         } else {
            float var2 = var0 - 0.16F;
            return 1.0F + 0.85F * (float)(Math.exp(-6.5F * var2) * Math.cos(17.0F * var2));
         }
      } else {
         return var1 < 1.0F ? 1.0F - 0.35F * MathHelper.sin(var1 * (float) Math.PI) : 1.0F;
      }
   }

   private void renderFavouriteSweep(MenuDrawContext var1, float var2, float var3, float var4, ZenithColor var5, float var6) {
      this.renderSweep(var1, var2, var3, var4, var5, var6, 1.0F);
   }

   private void renderSweep(MenuDrawContext var1, float var2, float var3, float var4, ZenithColor var5, float var6, float var7) {
      float var8 = (1.0F - var4) * var6 * var7;
      var1.drawRect(var2, var3, this.width, 16.0F, var5.method7294(0.1F * var8));
      float var9 = var4 < 0.5F ? 2.0F * var4 * var4 : 1.0F - (float)Math.pow(-2.0F * var4 + 2.0F, 2.0) / 2.0F;
      float var10 = var2 - 30.0F + (this.width + 60.0F) * var9;
      float var11 = 26.0F;
      float var12;
      if (!((var12 = 0.3F * var7 * MathHelper.sin(var4 * (float) Math.PI) * var6) <= 0.002F)) {
         ZenithColor var13 = var5.method7294(0.0F);
         ZenithColor var14 = var5.method7295(ZenithColor.INSTANCE, 0.35F).method7294(var12);
         this.drawBand(var1, var10 - var11, var10, var3, var2, var13, var14);
         this.drawBand(var1, var10, var10 + var11, var3, var2, var14, var13);
      }
   }

   private void drawBand(MenuDrawContext var1, float var2, float var3, float var4, float var5, ZenithColor var6, ZenithColor var7) {
      float var8 = Math.max(var2, var5);
      float var9;
      if (!((var9 = Math.min(var3, var5 + this.width)) - var8 <= 0.1F)) {
         float var10 = var3 - var2;
         ZenithColor var11 = var6.method7295(var7, (var8 - var2) / var10);
         ZenithColor var12 = var6.method7295(var7, (var9 - var2) / var10);
         var1.drawRoundedRect(var8, var4, var9 - var8, 16.0F, ProxyDotRadiusData.INSTANCE, GlowColor.method8096(var11, var11, var12, var12));
      }
   }

   private void renderFavouriteBurst(MenuDrawContext var1, float var2, float var3, float var4, ZenithColor var5, float var6) {
      float var7;
      if ((var7 = Math.min(1.0F, var4 / 0.45F)) < 1.0F) {
         float var8 = 3.0F + var7 * 6.0F;
         var1.drawRoundedRect(
            var2 - var8, var3 - var8, var8 * 2.0F, var8 * 2.0F, ProxyDotRadiusData.method8069(var8), var5.method7294(0.35F * (1.0F - var7) * var6)
         );
      }

      renderRing(var1, var2, var3, Math.min(1.0F, var4 / 0.6F), 10.0F, var5, var6);
      float var16 = 1.0F - (1.0F - var4) * (1.0F - var4) * (1.0F - var4);
      float var9 = (1.0F - var4) * var6;

      for (int var10 = 0; var10 < 10; var10++) {
         float var11 = this.sparkReach[var10] * var16;
         float var12 = var2 + MathHelper.cos(this.sparkAngle[var10]) * var11;
         float var13 = var3 + MathHelper.sin(this.sparkAngle[var10]) * var11 + 3.5F * var4 * var4;
         ZenithColor var14 = (var10 % 2 == 0 ? var5 : var5.method7295(ZenithColor.INSTANCE, 0.6F)).method7294(var9);
         if (var10 % 3 == 0) {
            float var15 = 2.6F * (1.0F - var4 * 0.6F);
            smooth(HEART_FILLED);
            var1.drawTexture(HEART_FILLED, var12 - var15 / 2.0F, var13 - var15 / 2.0F, var15, var15, var14);
         } else {
            float var17;
            if (!((var17 = 1.5F * (1.0F - var4)) <= 0.05F)) {
               var1.drawRoundedRect(var12 - var17 / 2.0F, var13 - var17 / 2.0F, var17, var17, ProxyDotRadiusData.method8069(var17 / 2.0F), var14);
            }
         }
      }
   }

   private static void renderRing(MenuDrawContext var0, float var1, float var2, float var3, float var4, ZenithColor var5, float var6) {
      if (!(var3 >= 1.0F)) {
         float var7 = 3.0F + var3 * var4;
         float var8 = (1.0F - var3) * (1.0F - var3) * 0.8F * var6;
         var0.drawRoundedBorder(
            var1 - var7, var2 - var7, var7 * 2.0F, var7 * 2.0F, 0.3F + 0.6F * (1.0F - var3), ProxyDotRadiusData.method8069(var7), var5.method7294(var8)
         );
      }
   }

   private void beginBinding() {
      this.binding = true;
      this.bindLabel.waiting();
      BindCapture.begin(this);
   }

   private void onBindSet() {
      this.bindLabel.set();
   }

   private void onBindCleared(boolean var1) {
      this.bindLabel.cleared();
   }

   @Override
   public boolean bindMouse(Enum3 var1) {
      if (this.isBinding() && this.module != null && MouseBindTarget.bindable(var1)) {
         this.module.setKeyCode(var1.getIntValue());
         this.module.setKeyModifiers(0);
         this.binding = false;
         BindCapture.end(this);
         this.onBindSet();
         Zenith.getInstance().getClientSounds().method1566();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (this.bounds == null || !this.bounds.contains(var1, var3)) {
         return false;
      } else if (this.bindMouse(var5)) {
         return true;
      } else if (this.heartBounds != null && this.heartBounds.contains(var1, var3) && var5 == Enum3.LEFT) {
         boolean var7 = !this.module.isPriority();
         this.module.setPriority(var7);
         if (var7) {
            this.startFavouriteBurst();
            Zenith.getInstance().getClientSounds().method1562();
         } else {
            this.favouriteAt = -1L;
            this.unfavouriteAt = System.currentTimeMillis();
            Zenith.getInstance().getClientSounds().method1570(true);
         }

         return true;
      } else if (this.bindBounds != null && this.bindBounds.contains(var1, var3)) {
         if (var5 == Enum3.RIGHT) {
            boolean var6 = this.module.getKeyCode() != -1 && this.module.getKeyCode() != 0;
            this.module.setKeyCode(-1);
            this.module.setKeyModifiers(0);
            this.binding = false;
            BindCapture.end(this);
            this.onBindCleared(var6);
            Zenith.getInstance().getClientSounds().method1567();
         } else {
            this.beginBinding();
            Zenith.getInstance().getClientSounds().method1570(false);
         }

         return true;
      } else if (var5 == Enum3.MIDDLE) {
         this.beginBinding();
         Zenith.getInstance().getClientSounds().method1570(false);
         return true;
      } else if (var5 == Enum3.RIGHT) {
         Zenith.getInstance().getNLMenuScreen().getInspectorPanel().toggleFor(this.module);
         Zenith.getInstance().getClientSounds().method1570(true);
         return true;
      } else {
         this.module.toggle();
         return true;
      }
   }

   @Override
   public boolean keyPressed(KeyInput var1) {
      return this.keyPressed(var1.key(), var1.scancode(), var1.modifiers());
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (!this.isBinding()) {
         return false;
      } else {
         if (var1 == 256) {
            boolean var4 = this.module.getKeyCode() != -1 && this.module.getKeyCode() != 0;
            this.module.setKeyCode(-1);
            this.module.setKeyModifiers(0);
            this.onBindCleared(var4);
            Zenith.getInstance().getClientSounds().method1567();
         } else {
            this.module.setKeyCode(var1);
            this.module.setKeyModifiers(KeyName.method6700(var1, var3));
            this.onBindSet();
            Zenith.getInstance().getClientSounds().method1566();
         }

         this.binding = false;
         BindCapture.end(this);
         return true;
      }
   }

   @Generated
   public Module getModule() {
      return this.module;
   }

   @Generated
   public void setWidth(float var1) {
      this.width = var1;
   }

   @Generated
   public void setShowCategoryIcon(boolean var1) {
      this.showCategoryIcon = var1;
   }
}

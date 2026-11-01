package org.zenith.client.screens.override.onboarding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.WorldIcon;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.ModuleCategory;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.override.main.MainMenuScreen;
import org.zenith.client.screens.override.main.MenuIcon;
import org.zenith.client.screens.override.main.MenuIcons;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.client.screens.override.main.widget.MenuButton;
import org.zenith.client.screens.override.main.widget.MenuScroll;
import org.zenith.utility.api.OnboardingUtil;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.GlowColor;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public final class OnboardingScreen extends Screen implements MinecraftAccess {
   private static final float GUTTER = MenuTheme.px(96.0F);
   private static final float NUMBER_HEIGHT = MenuTheme.px(40.0F);
   private static final float TITLE_BLOCK = MenuTheme.px(42.0F);
   private static final float BUTTON_WIDTH = MenuTheme.px(384.0F);
   private static final float STEPS_HEIGHT = MenuTheme.px(40.0F);
   private static final float STEP_BAR_WIDTH = MenuTheme.px(45.0F);
   private static final float STEP_BAR_HEIGHT = MenuTheme.px(2.0F);
   private static final float STEP_BAR_GAP = MenuTheme.px(4.0F);
   private static final float DESCRIPTION_WIDTH = MenuTheme.px(852.0F);
   private final List<OnboardingScreen.Step> steps = new ArrayList<>();
   private final MenuButton primaryButton = MenuButton.accent(MenuIcons.ACCEPT, "Start Setup");
   private final Animation openTransition = new Animation(400L, 0.0F, Easing.INSTANCE7);
   private final Animation stepTransition = new Animation(280L, 1.0F, Easing.INSTANCE7);
   private int index;
   private boolean prepared;

   public OnboardingScreen() {
      super(Text.literal("Zenith Setup"));
      this.steps.add(new OnboardingScreen.WelcomeStep());
      this.steps.add(new OnboardingScreen.ThemeStep());
      this.steps.add(new OnboardingScreen.ServerStep());
      this.steps.add(new OnboardingScreen.HotkeyStep());
      this.primaryButton.onClick(this::advance);
      this.syncButton();
   }

   private OnboardingScreen.Step step() {
      return this.steps.get(this.index);
   }

   private void syncButton() {
      this.primaryButton.setIcon(this.step().buttonIcon());
      this.primaryButton.setLabel(this.step().buttonLabel());
   }

   private void advance() {
      this.step().commit();
      if (this.index + 1 < this.steps.size()) {
         this.index++;
         this.syncButton();
         this.stepTransition.method2809(0.0F);
         this.step().enter();
      } else {
         OnboardingUtil.complete();
         boolean var1 = false;

         for (OnboardingScreen.Step var3 : this.steps) {
            if (var3 instanceof OnboardingScreen.ServerStep var4 && var4.picked() != null) {
               var1 = true;
               break;
            }
         }

         MainMenuScreen var5 = Zenith.getInstance().getMainMenuScreen();
         mc.setScreen(var5);
         if (var1) {
            var5.select(2);
         }
      }
   }

   @Override
   public void init() {
      Zenith.getInstance().getMenuParticleRenderer().init();
      if (!this.prepared) {
         for (OnboardingScreen.Step var2 : this.steps) {
            var2.prepare();
         }

         this.prepared = true;
         this.openTransition.method2809(0.0F);
         this.stepTransition.method2809(0.0F);
         this.step().enter();
      }

      super.init();
   }

   @Override
   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      MenuDrawContext var5 = MenuDrawContext.method8080(context, mouseX, mouseY, deltaTicks);
      Window var6 = mc.getWindow();
      float var7 = var6.getScaledWidth();
      float var8 = var6.getScaledHeight();
      var5.drawTexture(Zenith.method1501("menu/background.png"), 0.0F, 0.0F, var7, var8, ZenithColor.INSTANCE);
      Zenith.getInstance().getMenuParticleRenderer().renderParticles(var5);
      this.openTransition.method2808(1.0F);
      this.stepTransition.method2808(1.0F);
      OnboardingScreen.Step var9 = this.step();
      float var10 = var9.bodyHeight();
      float var11 = NUMBER_HEIGHT + GUTTER + TITLE_BLOCK + GUTTER + var10 + GUTTER + MenuButton.HEIGHT + GUTTER + STEPS_HEIGHT;
      float var12 = MenuTheme.snap(Math.max(MenuTheme.px(12.0F), (var8 - var11) / 2.0F));
      float var13 = MenuTheme.snap(var7 / 2.0F);
      float var14 = Math.min(DESCRIPTION_WIDTH, var7 - MenuTheme.px(64.0F));
      float var15 = (1.0F - this.openTransition.getValue()) * MenuTheme.px(40.0F);
      var5.getMatrices().pushMatrix();
      var5.getMatrices().translate(0.0F, var15);
      this.drawNumber(var5, var13, var12);
      float var16 = (1.0F - this.stepTransition.getValue()) * MenuTheme.px(24.0F);
      var5.getMatrices().pushMatrix();
      var5.getMatrices().translate(0.0F, var16);
      float var17 = var12 + NUMBER_HEIGHT + GUTTER;
      this.drawTitle(var5, var9, var13, var17, var14);
      float var18 = var17 + TITLE_BLOCK + GUTTER;
      BoundsData var19 = new BoundsData(var13 - var14 / 2.0F, var18, var14, var10);
      var9.layout(var19);
      var9.render(var5, mouseX, mouseY, var19);
      this.primaryButton.setBounds(var13 - BUTTON_WIDTH / 2.0F, var18 + var10 + GUTTER + var15 + var16, BUTTON_WIDTH, MenuButton.HEIGHT);
      this.primaryButton.setEnabled(var9.canAdvance());
      var5.getMatrices().pushMatrix();
      var5.getMatrices().translate(0.0F, -(var15 + var16));
      this.primaryButton.render(var5, mouseX, mouseY);
      var5.getMatrices().popMatrix();
      var5.getMatrices().popMatrix();
      this.drawSteps(var5, var13, var18 + var10 + GUTTER + MenuButton.HEIGHT + GUTTER);
      var5.getMatrices().popMatrix();
   }

   private void drawNumber(MenuDrawContext var1, float var2, float var3) {
      Font var4 = MenuTheme.semibold(32.0F);
      String var5 = String.valueOf(this.index + 1);
      var1.drawText(var4, var5, var2 - var4.width(var5) / 2.0F, var3 + (NUMBER_HEIGHT - var4.height()) / 2.0F, MenuTheme.text().method7294(0.16F));
   }

   private void drawTitle(MenuDrawContext var1, OnboardingScreen.Step var2, float var3, float var4, float var5) {
      Font var6 = MenuTheme.semibold(16.0F);
      var2.drawTitle(var1, var6, var3, var4);
      Font var7 = MenuTheme.regular(12.0F);
      float var8 = var4 + MenuTheme.px(28.0F);

      for (String var10 : MenuTheme.wrap(var7, var2.description(), var5)) {
         var1.drawText(var7, var10, var3 - var7.width(var10) / 2.0F, var8, MenuTheme.textSecondary());
         var8 += MenuTheme.px(16.0F);
      }
   }

   private void drawSteps(MenuDrawContext var1, float var2, float var3) {
      float var4 = this.steps.size() * STEP_BAR_WIDTH + (this.steps.size() - 1) * STEP_BAR_GAP;
      float var5 = var2 - var4 / 2.0F;
      float var6 = var3 + (STEPS_HEIGHT - STEP_BAR_HEIGHT) / 2.0F;
      ProxyDotRadiusData var7 = ProxyDotRadiusData.method8069(STEP_BAR_HEIGHT / 2.0F);

      for (int var8 = 0; var8 < this.steps.size(); var8++) {
         var1.drawRoundedRect(var5, var6, STEP_BAR_WIDTH, STEP_BAR_HEIGHT, var7, var8 <= this.index ? MenuTheme.accent() : MenuTheme.track());
         var5 += STEP_BAR_WIDTH + STEP_BAR_GAP;
      }
   }

   @Override
   public boolean mouseClicked(Click click, boolean doubled) {
      return this.mouseClicked(click.x(), click.y(), click.button());
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.primaryButton.mouseClicked(var1, var3, var5)) {
         return true;
      } else {
         return this.step().mouseClicked(var1, var3, var5) ? true : super.mouseClicked(new Click(var1, var3, new MouseInput(var5, 0)), false);
      }
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return this.step().mouseScrolled(verticalAmount) ? true : super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   @Override
   public boolean keyPressed(KeyInput input) {
      return this.keyPressed(input.key(), input.scancode(), input.modifiers());
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.step().keyPressed(var1)) {
         return true;
      } else if (var1 == 257 && this.step().canAdvance()) {
         this.advance();
         return true;
      } else {
         return super.keyPressed(new KeyInput(var1, var2, var3));
      }
   }

   @Override
   public void removed() {
      for (OnboardingScreen.Step var2 : this.steps) {
         var2.close();
      }

      super.removed();
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return false;
   }

   @Override
   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
   }

   @Override
   public void renderInGameBackground(DrawContext context) {
   }

   private static final class Appear {
      private static final float DURATION = 0.3F;
      private static final float STAGGER = 0.05F;
      private long since = Util.getMeasuringTimeMs();

      private void reset() {
         this.since = Util.getMeasuringTimeMs();
      }

      private float of(int var1) {
         float var2 = (float)(Util.getMeasuringTimeMs() - this.since) / 1000.0F - var1 * 0.05F;
         float var3 = Math.min(1.0F, Math.max(0.0F, var2 / 0.3F));
         return var3 * var3 * (3.0F - 2.0F * var3);
      }

      private float lift(int var1) {
         return (1.0F - this.of(var1)) * MenuTheme.px(16.0F);
      }
   }

   private static final class HotkeyStep implements OnboardingScreen.Step {
      private static final float ITEM_WIDTH = MenuTheme.px(92.0F);
      private static final float ITEM_GAP = MenuTheme.px(24.0F);
      private static final float KEY_BOX = MenuTheme.px(72.0F);
      private static final float KEY_RADIUS = MenuTheme.px(16.0F);
      private static final List<String> WANTED = List.of("Aura", "Speed", "ESP", "TriggerBot", "Menu");
      private final List<Module> modules = new ArrayList<>();
      private final OnboardingScreen.Appear appear = new OnboardingScreen.Appear();
      private BoundsData[] bounds = new BoundsData[0];
      private int binding = -1;

      @Override
      public void enter() {
         this.appear.reset();
      }

      @Override
      public void prepare() {
         this.modules.clear();

         for (String var2 : WANTED) {
            Module var3 = Zenith.getInstance().getModuleManager().method2484(var2);
            if (var3 != null) {
               this.modules.add(var3);
            }
         }

         if (this.modules.isEmpty()) {
            var var4 = Zenith.getInstance().getModuleManager().getItems();
            this.modules.addAll(var4.subList(0, Math.min(5, var4.size())));
         }
      }

      @Override
      public void drawTitle(MenuDrawContext var1, Font var2, float var3, float var4) {
         String var5 = "Set your primary hotkeys.";
         var1.drawText(var2, var5, var3 - var2.width(var5) / 2.0F, var4, MenuTheme.text());
      }

      @Override
      public String description() {
         return "Pick your preferred keys for the most used modules. Click a tile, then press a key.";
      }

      @Override
      public float bodyHeight() {
         return MenuTheme.px(118.0F);
      }

      @Override
      public MenuIcon buttonIcon() {
         return MenuIcons.LAUNCH;
      }

      @Override
      public String buttonLabel() {
         return "Launch Zenith";
      }

      @Override
      public void render(MenuDrawContext var1, float var2, float var3, BoundsData var4) {
         this.bounds = new BoundsData[this.modules.size()];
         float var5 = this.modules.size() * ITEM_WIDTH + Math.max(0, this.modules.size() - 1) * ITEM_GAP;
         float var6 = var4.getFloatValue() + (var4.width() - var5) / 2.0F;

         for (int var7 = 0; var7 < this.modules.size(); var7++) {
            this.bounds[var7] = new BoundsData(var6, var4.getFloatValue2(), ITEM_WIDTH, var4.height());
            this.drawModule(var1, this.modules.get(var7), var7, var6, var4.getFloatValue2() + this.appear.lift(var7), this.appear.of(var7));
            var6 += ITEM_WIDTH + ITEM_GAP;
         }
      }

      private void drawModule(MenuDrawContext var1, Module var2, int var3, float var4, float var5, float var6) {
         boolean var7 = var2.getKeyCode() > 0;
         boolean var8 = this.binding == var3;
         float var9 = MenuTheme.snap(var4 + (ITEM_WIDTH - KEY_BOX) / 2.0F);
         ProxyDotRadiusData var10 = ProxyDotRadiusData.method8069(KEY_RADIUS);
         if (var7 && !var8) {
            var1.drawRoundedRect(var9, var5, KEY_BOX, KEY_BOX, var10, MenuTheme.accent().method7294(var6));
         } else {
            var1.drawRoundedRect(var9, var5, KEY_BOX, KEY_BOX, var10, MenuTheme.surface().method7294(var6));
            var1.drawRoundedBorder(var9, var5, KEY_BOX, KEY_BOX, -0.1F, var10, (var8 ? MenuTheme.accent() : MenuTheme.border()).method7294(var6));
         }

         Font var11 = MenuTheme.semibold(16.0F);
         String var12 = var8 ? "..." : keyName(var2.getKeyCode());
         var1.drawText(
            var11,
            var12,
            var9 + (KEY_BOX - var11.width(var12)) / 2.0F,
            var5 + (KEY_BOX - var11.height()) / 2.0F,
            (var7 && !var8 ? MenuTheme.onAccent() : MenuTheme.textSecondary()).method7294(var6)
         );
         ModuleCategory var13 = var2.getCategory();
         MenuIcon var14 = MenuIcons.forCategory(var13);
         Font var15 = MenuTheme.medium(12.0F);
         Font var16 = var14.font(12.0F);
         float var17 = var14.width(12.0F) + MenuTheme.px(4.0F) + var15.width(var2.getName());
         float var18 = var4 + (ITEM_WIDTH - var17) / 2.0F;
         float var19 = var5 + KEY_BOX + MenuTheme.px(16.0F);
         var1.drawText(var16, var14.glyph(), var18, var19, MenuTheme.accent().method7294(var6));
         var1.drawText(var15, var2.getName(), var18 + var14.width(12.0F) + MenuTheme.px(4.0F), var19, MenuTheme.text().method7294(var6));
         Font var20 = MenuTheme.regular(10.0F);
         String var21 = var13.getName();
         var1.drawText(var20, var21, var4 + (ITEM_WIDTH - var20.width(var21)) / 2.0F, var19 + MenuTheme.px(18.0F), MenuTheme.textSecondary().method7294(var6));
      }

      private static String keyName(int var0) {
         if (var0 <= 0) {
            return "-";
         } else {
            String var1 = GLFW.glfwGetKeyName(var0, 0);
            return var1 != null && !var1.isBlank() ? var1.toUpperCase() : Type.KEYSYM.createFromCode(var0).getLocalizedText().getString().toUpperCase();
         }
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         if (var5 != 0) {
            return false;
         } else {
            for (int var6 = 0; var6 < this.bounds.length; var6++) {
               if (this.bounds[var6] != null && this.bounds[var6].contains(var1, var3)) {
                  this.binding = this.binding == var6 ? -1 : var6;
                  return true;
               }
            }

            return false;
         }
      }

      @Override
      public boolean keyPressed(int var1) {
         if (this.binding >= 0 && this.binding < this.modules.size()) {
            this.modules.get(this.binding).setKeyCode(var1 != 256 && var1 != 261 ? var1 : -1);
            this.binding = -1;
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void commit() {
         this.binding = -1;
         if (Zenith.getInstance().getManager() != null) {
            Zenith.getInstance().getManager().save();
         }
      }
   }

   private static final class ServerStep implements OnboardingScreen.Step {
      private static final float ITEM_WIDTH = MenuTheme.px(92.0F);
      private static final float ITEM_GAP = MenuTheme.px(16.0F);
      private static final float CIRCLE = MenuTheme.px(72.0F);
      private static final float ICON = MenuTheme.px(64.0F);
      private static final float BADGE = MenuTheme.px(16.0F);
      private final MenuScroll scroll = new MenuScroll();
      private final OnboardingScreen.Appear appear = new OnboardingScreen.Appear();
      private final List<OnboardingScreen.ServerStep.Entry> entries = new ArrayList<>();
      private BoundsData[] bounds = new BoundsData[0];
      private int selected = -1;

      @Override
      public void enter() {
         this.appear.reset();
      }

      @Override
      public void prepare() {
         this.close();
         ServerList var1 = new ServerList(mcStatic());
         var1.loadFile();

         for (int var2 = 0; var2 < var1.size(); var2++) {
            this.entries.add(new OnboardingScreen.ServerStep.Entry(var1.get(var2)));
         }

         this.entries.add(new OnboardingScreen.ServerStep.Entry(null));
         this.selected = this.entries.size() - 1;
      }

      private static MinecraftClient mcStatic() {
         return MinecraftClient.getInstance();
      }

      private ServerInfo picked() {
         return this.selected >= 0 && this.selected < this.entries.size() ? this.entries.get(this.selected).info : null;
      }

      @Override
      public void drawTitle(MenuDrawContext var1, Font var2, float var3, float var4) {
         String var5 = "Where are you playing today?";
         var1.drawText(var2, var5, var3 - var2.width(var5) / 2.0F, var4, MenuTheme.text());
      }

      @Override
      public String description() {
         return "Selecting a server opens its section in the menu with the modules made for it.";
      }

      @Override
      public float bodyHeight() {
         return MenuTheme.px(110.0F);
      }

      @Override
      public MenuIcon buttonIcon() {
         return MenuIcons.CONFIGURE;
      }

      @Override
      public String buttonLabel() {
         return "Configure";
      }

      @Override
      public void render(MenuDrawContext var1, float var2, float var3, BoundsData var4) {
         this.bounds = new BoundsData[this.entries.size()];
         float var5 = this.entries.size() * ITEM_WIDTH + (this.entries.size() - 1) * ITEM_GAP;
         this.scroll.update(var4.width(), var5);
         var1.enableScissor(var4.getFloatValue(), var4.getFloatValue2(), var4.getFloatValue() + var4.width(), var4.getFloatValue2() + var4.height());
         float var6 = var4.getFloatValue() + Math.max(0.0F, (var4.width() - var5) / 2.0F) - this.scroll.offset();

         for (int var7 = 0; var7 < this.entries.size(); var7++) {
            this.bounds[var7] = new BoundsData(var6, var4.getFloatValue2(), ITEM_WIDTH, var4.height());
            this.entries.get(var7).render(var1, var6, var4.getFloatValue2() + this.appear.lift(var7), var7 == this.selected, this.appear.of(var7));
            var6 += ITEM_WIDTH + ITEM_GAP;
         }

         var1.disableScissor();
      }

      @Override
      public void close() {
         for (OnboardingScreen.ServerStep.Entry var2 : this.entries) {
            var2.close();
         }

         this.entries.clear();
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         if (var5 != 0) {
            return false;
         } else {
            for (int var6 = 0; var6 < this.bounds.length; var6++) {
               if (this.bounds[var6] != null && this.bounds[var6].contains(var1, var3)) {
                  this.selected = var6;
                  return true;
               }
            }

            return false;
         }
      }

      @Override
      public boolean mouseScrolled(double var1) {
         return this.scroll.scroll(var1, ITEM_WIDTH + ITEM_GAP);
      }

      private static final class Entry {
         private final ServerInfo info;
         private final WorldIcon icon;
         private byte[] lastFavicon;

         private Entry(ServerInfo var1) {
            this.info = var1;
            this.icon = var1 == null ? null : WorldIcon.forServer(OnboardingScreen.ServerStep.mcStatic().getTextureManager(), var1.address);
         }

         private void render(MenuDrawContext var1, float var2, float var3, boolean var4, float var5) {
            float var6 = MenuTheme.snap(var2 + (OnboardingScreen.ServerStep.ITEM_WIDTH - OnboardingScreen.ServerStep.CIRCLE) / 2.0F);
            if (var4) {
               var1.drawRoundedBorder(
                  var6,
                  var3,
                  OnboardingScreen.ServerStep.CIRCLE,
                  OnboardingScreen.ServerStep.CIRCLE,
                  -0.1F,
                  ProxyDotRadiusData.method8069(OnboardingScreen.ServerStep.CIRCLE / 2.0F),
                  ZenithColor.INSTANCE.method7294(var5)
               );
            }

            float var7 = var6 + (OnboardingScreen.ServerStep.CIRCLE - OnboardingScreen.ServerStep.ICON) / 2.0F;
            float var8 = var3 + (OnboardingScreen.ServerStep.CIRCLE - OnboardingScreen.ServerStep.ICON) / 2.0F;
            if (this.info == null) {
               var1.drawRoundedRect(
                  var7,
                  var8,
                  OnboardingScreen.ServerStep.ICON,
                  OnboardingScreen.ServerStep.ICON,
                  ProxyDotRadiusData.method8069(OnboardingScreen.ServerStep.ICON / 2.0F),
                  MenuTheme.accent().method7294(var5)
               );
               Font var9 = MenuIcons.SPARKLE.font(28.0F);
               var1.drawText(
                  var9,
                  MenuIcons.SPARKLE.glyph(),
                  var7 + (OnboardingScreen.ServerStep.ICON - MenuIcons.SPARKLE.width(28.0F)) / 2.0F,
                  var8 + (OnboardingScreen.ServerStep.ICON - var9.height()) / 2.0F,
                  MenuTheme.onAccent().method7294(var5)
               );
            } else {
               this.syncFavicon();
               RenderShaders.method5294(
                  MatrixUtils.method664(var1.getMatrices()),
                  this.icon.getTextureId(),
                  var7,
                  var8,
                  OnboardingScreen.ServerStep.ICON,
                  OnboardingScreen.ServerStep.ICON,
                  ProxyDotRadiusData.method8069(OnboardingScreen.ServerStep.ICON / 2.0F),
                  ZenithColor.INSTANCE.method7294(var5)
               );
            }

            if (var4) {
               float var18 = var6 + OnboardingScreen.ServerStep.CIRCLE - OnboardingScreen.ServerStep.BADGE;
               float var10 = var3 + OnboardingScreen.ServerStep.CIRCLE - OnboardingScreen.ServerStep.BADGE;
               var1.drawRoundedRect(
                  var18,
                  var10,
                  OnboardingScreen.ServerStep.BADGE,
                  OnboardingScreen.ServerStep.BADGE,
                  ProxyDotRadiusData.method8069(OnboardingScreen.ServerStep.BADGE / 2.0F),
                  MenuTheme.accent().method7294(var5)
               );
               var1.drawRoundedBorder(
                  var18,
                  var10,
                  OnboardingScreen.ServerStep.BADGE,
                  OnboardingScreen.ServerStep.BADGE,
                  -0.1F,
                  ProxyDotRadiusData.method8069(OnboardingScreen.ServerStep.BADGE / 2.0F),
                  ZenithColor.INSTANCE.method7294(var5)
               );
               Font var11 = MenuIcons.ACCEPT.font(10.0F);
               var1.drawText(
                  var11,
                  MenuIcons.ACCEPT.glyph(),
                  var18 + (OnboardingScreen.ServerStep.BADGE - MenuIcons.ACCEPT.width(10.0F)) / 2.0F,
                  var10 + (OnboardingScreen.ServerStep.BADGE - var11.height()) / 2.0F,
                  ZenithColor.INSTANCE.method7294(var5)
               );
            }

            Font var19 = MenuTheme.medium(12.0F);
            String var20 = this.info == null ? "Universal" : this.info.name;
            float var21 = var3 + OnboardingScreen.ServerStep.CIRCLE + MenuTheme.px(8.0F);
            var1.enableScissor(var2, var21, var2 + OnboardingScreen.ServerStep.ITEM_WIDTH, var21 + var19.height());
            var1.drawText(var19, var20, var2 + (OnboardingScreen.ServerStep.ITEM_WIDTH - var19.width(var20)) / 2.0F, var21, MenuTheme.text().method7294(var5));
            var1.disableScissor();
            Font var12 = MenuTheme.regular(10.0F);
            String var13 = this.info == null ? "?" : (this.info.playerCountLabel == null ? "-" : this.info.playerCountLabel.getString());
            Font var14 = MenuIcons.ONLINE.font(10.0F);
            float var15 = MenuIcons.ONLINE.width(10.0F) + MenuTheme.px(4.0F) + var12.width(var13);
            float var16 = var2 + (OnboardingScreen.ServerStep.ITEM_WIDTH - var15) / 2.0F;
            float var17 = var3 + OnboardingScreen.ServerStep.CIRCLE + MenuTheme.px(26.0F);
            var1.drawText(var14, MenuIcons.ONLINE.glyph(), var16, var17, MenuTheme.accent().method7294(var5));
            var1.drawText(var12, var13, var16 + MenuIcons.ONLINE.width(10.0F) + MenuTheme.px(4.0F), var17, MenuTheme.text().method7294(var5));
         }

         private void syncFavicon() {
            byte[] var1 = this.info.getFavicon();
            if (var1 != null && !Arrays.equals(var1, this.lastFavicon)) {
               try {
                  this.icon.load(NativeImage.read(var1));
                  this.lastFavicon = var1;
               } catch (Exception var3) {
               }
            }
         }

         private void close() {
            if (this.icon != null) {
               this.icon.close();
            }
         }
      }
   }

   private interface Step {
      String description();

      float bodyHeight();

      MenuIcon buttonIcon();

      String buttonLabel();

      void render(MenuDrawContext var1, float var2, float var3, BoundsData var4);

      default void drawTitle(MenuDrawContext var1, Font var2, float var3, float var4) {
      }

      default void layout(BoundsData var1) {
      }

      default void prepare() {
      }

      default void enter() {
      }

      default void commit() {
      }

      default void close() {
      }

      default boolean canAdvance() {
         return true;
      }

      default boolean mouseClicked(double var1, double var3, int var5) {
         return false;
      }

      default boolean mouseScrolled(double var1) {
         return false;
      }

      default boolean keyPressed(int var1) {
         return false;
      }
   }

   private static final class ThemeStep implements OnboardingScreen.Step {
      private static final float ITEM_WIDTH = MenuTheme.px(92.0F);
      private static final float ITEM_GAP = MenuTheme.px(16.0F);
      private static final float CIRCLE = MenuTheme.px(72.0F);
      private static final float FILL = MenuTheme.px(64.0F);
      private static final float BADGE = MenuTheme.px(16.0F);
      private final MenuScroll scroll = new MenuScroll();
      private final OnboardingScreen.Appear appear = new OnboardingScreen.Appear();
      private BoundsData[] bounds = new BoundsData[0];
      private int selected;

      @Override
      public void prepare() {
         var var1 = Zenith.getInstance().getStyleManager().getStyles();
         ZenithStyle var2 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         this.selected = Math.max(0, var1.indexOf(var2));
      }

      @Override
      public void enter() {
         this.appear.reset();
      }

      @Override
      public void drawTitle(MenuDrawContext var1, Font var2, float var3, float var4) {
         String var5 = "Select your visual theme.";
         var1.drawText(var2, var5, var3 - var2.width(var5) / 2.0F, var4, MenuTheme.text());
      }

      @Override
      public String description() {
         return "Choose a primary aesthetic for your client. This defines the palette, accents and highlights across all modules and menus.";
      }

      @Override
      public float bodyHeight() {
         return MenuTheme.px(94.0F);
      }

      @Override
      public MenuIcon buttonIcon() {
         return MenuIcons.NEXT;
      }

      @Override
      public String buttonLabel() {
         return "Next Step";
      }

      @Override
      public void render(MenuDrawContext var1, float var2, float var3, BoundsData var4) {
         var var5 = Zenith.getInstance().getStyleManager().getStyles();
         this.bounds = new BoundsData[var5.size()];
         float var6 = var5.size() * ITEM_WIDTH + (var5.size() - 1) * ITEM_GAP;
         this.scroll.update(var4.width(), var6);
         var1.enableScissor(var4.getFloatValue(), var4.getFloatValue2(), var4.getFloatValue() + var4.width(), var4.getFloatValue2() + var4.height());
         float var7 = var4.getFloatValue() + Math.max(0.0F, (var4.width() - var6) / 2.0F) - this.scroll.offset();

         for (int var8 = 0; var8 < var5.size(); var8++) {
            this.bounds[var8] = new BoundsData(var7, var4.getFloatValue2(), ITEM_WIDTH, var4.height());
            this.drawTheme(var1, (ZenithStyle)var5.get(var8), var8 == this.selected, var7, var4.getFloatValue2() + this.appear.lift(var8), this.appear.of(var8));
            var7 += ITEM_WIDTH + ITEM_GAP;
         }

         var1.disableScissor();
      }

      private void drawTheme(MenuDrawContext var1, ZenithStyle var2, boolean var3, float var4, float var5, float var6) {
         float var7 = MenuTheme.snap(var4 + (ITEM_WIDTH - CIRCLE) / 2.0F);
         if (var3) {
            var1.drawRoundedBorder(var7, var5, CIRCLE, CIRCLE, -0.1F, ProxyDotRadiusData.method8069(CIRCLE / 2.0F), ZenithColor.INSTANCE.method7294(var6));
         }

         float var8 = var7 + (CIRCLE - FILL) / 2.0F;
         float var9 = var5 + (CIRCLE - FILL) / 2.0F;
         ZenithColor var10 = var2.getPrimaryColor().getZenithColor().method7294(var6);
         ZenithColor var11 = var2.getSecondaryPrimaryColor().getZenithColor().method7294(var6);
         var1.drawRoundedRect(
            var8,
            var9,
            FILL,
            FILL,
            ProxyDotRadiusData.method8069(FILL / 2.0F),
            GlowColor.method8096(var10, var10.method7295(var11, 0.5F), var11, var11.method7295(var10, 0.5F))
         );
         if (var3) {
            float var12 = var7 + CIRCLE - BADGE;
            float var13 = var5 + CIRCLE - BADGE;
            var1.drawRoundedRect(var12, var13, BADGE, BADGE, ProxyDotRadiusData.method8069(BADGE / 2.0F), var10);
            var1.drawRoundedBorder(var12, var13, BADGE, BADGE, -0.1F, ProxyDotRadiusData.method8069(BADGE / 2.0F), ZenithColor.INSTANCE.method7294(var6));
            Font var14 = MenuIcons.ACCEPT.font(10.0F);
            var1.drawText(
               var14,
               MenuIcons.ACCEPT.glyph(),
               var12 + (BADGE - MenuIcons.ACCEPT.width(10.0F)) / 2.0F,
               var13 + (BADGE - var14.height()) / 2.0F,
               ZenithColor.INSTANCE.method7294(var6)
            );
         }

         Font var15 = MenuTheme.medium(12.0F);
         String var16 = var2.getName();
         var1.drawText(var15, var16, var4 + (ITEM_WIDTH - var15.width(var16)) / 2.0F, var5 + CIRCLE + MenuTheme.px(8.0F), MenuTheme.text().method7294(var6));
      }

      @Override
      public void commit() {
         var var1 = Zenith.getInstance().getStyleManager().getStyles();
         if (this.selected >= 0 && this.selected < var1.size()) {
            Zenith.getInstance().getStyleManager().setCurrentStyle((ZenithStyle)var1.get(this.selected));
         }
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         if (var5 != 0) {
            return false;
         } else {
            for (int var6 = 0; var6 < this.bounds.length; var6++) {
               if (this.bounds[var6] != null && this.bounds[var6].contains(var1, var3)) {
                  this.selected = var6;
                  this.commit();
                  return true;
               }
            }

            return false;
         }
      }

      @Override
      public boolean mouseScrolled(double var1) {
         return this.scroll.scroll(var1, ITEM_WIDTH + ITEM_GAP);
      }
   }

   private static final class WelcomeStep implements OnboardingScreen.Step {
      private static final float ITEM_WIDTH = MenuTheme.px(48.0F);
      private static final float ITEM_GAP = MenuTheme.px(96.0F);
      private final OnboardingScreen.Appear appear = new OnboardingScreen.Appear();

      @Override
      public void enter() {
         this.appear.reset();
      }

      @Override
      public void drawTitle(MenuDrawContext var1, Font var2, float var3, float var4) {
         String var5 = "Welcome back, ";
         String var6 = username();
         float var7 = var2.width(var5) + var2.width(var6);
         float var8 = var3 - var7 / 2.0F;
         var1.drawText(var2, var5, var8, var4, MenuTheme.text());
         var1.drawText(var2, var6, var8 + var2.width(var5), var4, MenuTheme.accent());
      }

      private static String username() {
         String var0 = Zenith.getInstance().getEntry().getUsername();
         return var0 != null && !var0.isEmpty() ? var0 + "." : "Guest";
      }

      @Override
      public String description() {
         return "Everything is ready to go. Let's take a second to adjust the client to your liking.";
      }

      @Override
      public float bodyHeight() {
         return MenuTheme.px(98.0F);
      }

      @Override
      public MenuIcon buttonIcon() {
         return MenuIcons.ACCEPT;
      }

      @Override
      public String buttonLabel() {
         return "Start Setup";
      }

      @Override
      public void render(MenuDrawContext var1, float var2, float var3, BoundsData var4) {
         Zenith.Entry var5 = Zenith.getInstance().getEntry();
         var var6 = List.of(
            new String[]{var5.getText2() == null ? "-" : var5.getText2(), "UID"},
            new String[]{var5.method1504().getName(), "Role"},
            new String[]{"3.5", "Version"}
         );
         var var7 = List.of(MenuIcons.UID, MenuIcons.ROLE, MenuIcons.VERSION);
         float var8 = var6.size() * ITEM_WIDTH + (var6.size() - 1) * ITEM_GAP;
         float var9 = var4.getFloatValue() + (var4.width() - var8) / 2.0F;

         for (int var10 = 0; var10 < var6.size(); var10++) {
            this.drawItem(
               var1,
               (MenuIcon)var7.get(var10),
               ((String[])var6.get(var10))[0],
               ((String[])var6.get(var10))[1],
               var9,
               var4.getFloatValue2() + this.appear.lift(var10),
               this.appear.of(var10)
            );
            var9 += ITEM_WIDTH + ITEM_GAP;
         }
      }

      private void drawItem(MenuDrawContext var1, MenuIcon var2, String var3, String var4, float var5, float var6, float var7) {
         Font var8 = var2.font(48.0F);
         Font var9 = MenuTheme.medium(14.0F);
         Font var10 = MenuTheme.regular(11.0F);
         var1.drawText(
            var8,
            var2.glyph(),
            var5 + (ITEM_WIDTH - var2.width(48.0F)) / 2.0F,
            var6 + (MenuTheme.px(48.0F) - var8.height()) / 2.0F,
            MenuTheme.accent().method7294(var7)
         );
         float var11 = var6 + MenuTheme.px(64.0F);
         var1.drawText(var9, var3, var5 + (ITEM_WIDTH - var9.width(var3)) / 2.0F, var11, MenuTheme.text().method7294(var7));
         var1.drawText(var10, var4, var5 + (ITEM_WIDTH - var10.width(var4)) / 2.0F, var11 + MenuTheme.px(20.0F), MenuTheme.textTertiary().method7294(var7));
      }
   }
}

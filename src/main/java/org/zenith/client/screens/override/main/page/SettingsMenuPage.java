package org.zenith.client.screens.override.main.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.ChatOptionsScreen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.FontOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.MouseOptionsScreen;
import net.minecraft.client.gui.screen.option.OnlineOptionsScreen;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.gui.screen.option.SoundOptionsScreen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.input.MouseInput;
import org.zenith.MinecraftAccess;
import org.zenith.base.font.Font;
import org.zenith.client.screens.override.main.MenuIcon;
import org.zenith.client.screens.override.main.MenuIcons;
import org.zenith.client.screens.override.main.MenuTheme;
import org.zenith.client.screens.override.main.component.MenuNavigationBar;
import org.zenith.client.screens.override.main.component.VanillaOptionStyle;
import org.zenith.client.screens.override.main.widget.MenuScroll;
import org.zenith.client.screens.override.main.widget.MenuTextField;
import org.zenith.utility.render.SodiumOptionsReader;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class SettingsMenuPage implements MainMenuPage, MinecraftAccess {
   private static final float COLUMN_WIDTH = MenuTheme.px(320.0F);
   private static final float TABS_GAP = MenuTheme.px(16.0F);
   private static final float SEARCH_GAP = MenuTheme.px(12.0F);
   private static final float ROW_HEIGHT = MenuTheme.px(42.0F);
   private static final float ROW_GAP = MenuTheme.px(8.0F);
   private static final float RAIL_GAP = MenuTheme.px(16.0F);
   private static final float RAIL_WIDTH = MenuTheme.px(2.0F);
   private static final float SHELL_CHROME = MenuTheme.px(312.0F);
   private static final int MIN_COLUMNS = 2;
   private static final int MAX_COLUMNS = 4;
   private final List<SettingsMenuPage.Category> categories = new ArrayList<>();
   private final Map<String, List<ClickableWidget>> harvested = new LinkedHashMap<>();
   private final Map<String, GameOptionsScreen> sources = new LinkedHashMap<>();
   private final MenuScroll scroll = new MenuScroll();
   private final MenuTextField search = new MenuTextField(MenuIcons.SEARCH, MenuTheme.t("menu.settings.search"));
   private MenuNavigationBar tabs;
   private boolean sodiumAdded;
   private int active;
   private ClickableWidget dragged;
   private BoundsData viewport = new BoundsData(0.0F, 0.0F, 0.0F, 0.0F);
   private int columns = 2;
   private float gridHeight = MenuTheme.px(334.0F);

   public SettingsMenuPage() {
      this.vanilla("menu.settings.video", MenuIcons.VIDEO, () -> new VideoOptionsScreen(mc.currentScreen, mc, mc.options));
      this.vanilla("menu.settings.controls", MenuIcons.CONTROLS, () -> new ControlsOptionsScreen(mc.currentScreen, mc.options));
      this.vanilla("menu.settings.mouse", MenuIcons.MOUSE, () -> new MouseOptionsScreen(mc.currentScreen, mc.options));
      this.vanilla("menu.settings.sound", MenuIcons.SOUND, () -> new SoundOptionsScreen(mc.currentScreen, mc.options));
      this.vanilla("menu.settings.chat", MenuIcons.CHAT, () -> new ChatOptionsScreen(mc.currentScreen, mc.options));
      this.vanilla("menu.settings.skin", MenuIcons.COSMETICS, () -> new SkinOptionsScreen(mc.currentScreen, mc.options));
      this.vanilla("menu.settings.accessibility", MenuIcons.ACCESSIBILITY, () -> new AccessibilityOptionsScreen(mc.currentScreen, mc.options));
      this.vanilla("menu.settings.online", MenuIcons.WORLD, () -> new OnlineOptionsScreen(mc.currentScreen, mc.options));
      this.vanilla("menu.settings.font", MenuIcons.TEXT, () -> new FontOptionsScreen(mc.currentScreen, mc.options));
      this.rebuildTabs();
      this.search.setOnChange(this.scroll::reset);
   }

   private void addSodiumTabs() {
      if (!this.sodiumAdded) {
         List<SodiumOptionsReader.Data> var1 = SodiumOptionsReader.method1598();
         if (!var1.isEmpty()) {
            for (Object var3_o : (Iterable<?>)(Object)(var1)) {
               SodiumOptionsReader.Data var3 = (SodiumOptionsReader.Data)var3_o;
               this.categories.add(new SettingsMenuPage.Category(var3.name(), MenuIcons.SPARKLE, () -> {
                  ArrayList var1x = new ArrayList();

                  for (Object var3x : var3.getItems()) {
                     ClickableWidget var4 = SodiumOptionsReader.method1599(var3x);
                     if (var4 != null) {
                        var1x.add(var4);
                     }
                  }

                  return var1x;
               }));
            }

            this.sodiumAdded = true;
            this.rebuildTabs();
         }
      }
   }

   private void rebuildTabs() {
      ArrayList var1 = new ArrayList();

      for (int var2 = 0; var2 < this.categories.size(); var2++) {
         SettingsMenuPage.Category var3 = this.categories.get(var2);
         int var4 = var2;
         var1.add(new MenuNavigationBar.Item(var3.icon(), var3.label(), () -> this.select(var4)));
      }

      this.tabs = new MenuNavigationBar(var1);
   }

   private void vanilla(String var1, MenuIcon var2, Supplier<GameOptionsScreen> var3) {
      this.categories.add(new SettingsMenuPage.Category(var1, var2, () -> this.harvestVanilla(var1, var3)));
   }

   private List<ClickableWidget> harvestVanilla(String var1, Supplier<GameOptionsScreen> var2) {
      ArrayList<ClickableWidget> var3 = new ArrayList<>();
      if (mc.options == null) {
         return var3;
      } else {
         GameOptionsScreen var4 = (GameOptionsScreen)var2.get();
         var4.init(mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
         this.sources.put(var1, var4);
         OptionListWidget var5 = var4.body;
         if (var5 != null) {
            for (Object var7 : var5.children()) {
               if (var7 instanceof ParentElement var8) {
                  for (Element var10 : var8.children()) {
                     if (var10 instanceof ClickableWidget var11) {
                        var3.add(var11);
                     }
                  }
               }
            }
         }

         return var3;
      }
   }

   private void select(int var1) {
      if (var1 != this.active && var1 >= 0 && var1 < this.categories.size()) {
         this.active = var1;
         this.scroll.reset();
         this.dragged = null;
      }
   }

   @Override
   public void update() {
      int var1 = this.visible().size();
      float var2 = mc.getWindow() == null ? MenuTheme.px(1920.0F) : mc.getWindow().getScaledWidth();
      float var3 = mc.getWindow() == null ? MenuTheme.px(1080.0F) : mc.getWindow().getScaledHeight();
      float var4 = var2 - MenuTheme.px(96.0F) - RAIL_GAP - RAIL_WIDTH;
      int var5 = (int)Math.floor((var4 + ROW_GAP) / (COLUMN_WIDTH + ROW_GAP));
      this.columns = Math.max(2, Math.min(4, Math.min(var5, Math.max(1, var1))));
      float var6 = ROW_HEIGHT + ROW_GAP;
      int var7 = Math.max(1, (var1 + this.columns - 1) / this.columns);
      float var8 = var7 * var6 - ROW_GAP;
      float var9 = var3 - SHELL_CHROME - MenuNavigationBar.HEIGHT - TABS_GAP - MenuTextField.HEIGHT - SEARCH_GAP;
      this.gridHeight = Math.max(var6, Math.min(var8, var9));
   }

   private float listWidth() {
      return this.columns * COLUMN_WIDTH + (this.columns - 1) * ROW_GAP;
   }

   @Override
   public float contentWidth() {
      return this.listWidth() + RAIL_GAP + RAIL_WIDTH;
   }

   @Override
   public float contentHeight() {
      return MenuNavigationBar.HEIGHT + TABS_GAP + MenuTextField.HEIGHT + SEARCH_GAP + this.gridHeight;
   }

   private boolean searching() {
      return !this.search.getValue().trim().isEmpty();
   }

   @Override
   public void onShow() {
      this.addSodiumTabs();
      this.widgets();
   }

   @Override
   public void onHide() {
      VanillaOptionStyle.end();

      for (GameOptionsScreen var2 : this.sources.values()) {
         var2.removed();
      }

      this.sources.clear();
      this.harvested.clear();
      this.dragged = null;
      if (mc.options != null) {
         mc.options.write();
      }
   }

   private List<ClickableWidget> widgets() {
      return this.widgetsOf(this.categories.get(this.active));
   }

   private List<ClickableWidget> widgetsOf(SettingsMenuPage.Category var1) {
      var var2 = this.harvested.get(var1.label());
      if (var2 != null) {
         return var2;
      } else {
         Object var3;
         try {
            var3 = var1.harvest().get();
         } catch (Exception var5) {
            var3 = new ArrayList();
         }

         this.harvested.put(var1.label(), (List<ClickableWidget>)var3);
         return (List<ClickableWidget>)var3;
      }
   }

   private List<ClickableWidget> visible() {
      String var1 = this.search.getValue().trim().toLowerCase(Locale.ROOT);
      if (var1.isEmpty()) {
         return this.widgets();
      } else {
         ArrayList<ClickableWidget> var2 = new ArrayList<>();

         for (SettingsMenuPage.Category var4 : this.categories) {
            for (ClickableWidget var6 : this.widgetsOf(var4)) {
               if (var6.getMessage().getString().toLowerCase(Locale.ROOT).contains(var1)) {
                  var2.add(var6);
               }
            }
         }

         return var2;
      }
   }

   @Override
   public void layout(BoundsData var1) {
      var var2 = this.visible();
      float var3 = var1.getFloatValue2() + MenuNavigationBar.HEIGHT + TABS_GAP;
      this.search.setBounds(var1.getFloatValue(), var3, this.listWidth(), MenuTextField.HEIGHT);
      float var4 = var3 + MenuTextField.HEIGHT + SEARCH_GAP;
      this.viewport = new BoundsData(var1.getFloatValue(), var4, this.listWidth(), this.gridHeight);
      float var5 = ROW_HEIGHT + ROW_GAP;
      int var6 = (var2.size() + this.columns - 1) / this.columns;
      this.scroll.update(this.gridHeight, var6 == 0 ? 0.0F : var6 * var5 - ROW_GAP);
      float var7 = var4 - this.scroll.offset();

      for (int var8 = 0; var8 < var2.size(); var8++) {
         ClickableWidget var9 = (ClickableWidget)var2.get(var8);
         float var10 = var1.getFloatValue() + var8 % this.columns * (COLUMN_WIDTH + ROW_GAP);
         var9.setX(Math.round(MenuTheme.snap(var10)));
         var9.setY(Math.round(MenuTheme.snap(var7 + var8 / this.columns * var5)));
         var9.setWidth(Math.round(COLUMN_WIDTH));
      }
   }

   @Override
   public void render(MenuDrawContext var1, float var2, float var3, BoundsData var4) {
      List<ClickableWidget> var5 = this.visible();
      this.search.render(var1, var2, var3);
      if (var5.isEmpty()) {
         Font var6 = MenuTheme.medium(12.0F);
         String var7 = this.searching()
            ? MenuTheme.t("menu.settings.noMatch") + " \"" + this.search.getValue().trim() + "\""
            : MenuTheme.t("menu.settings.noOptions");
         var1.drawText(
            var6,
            var7,
            var4.getFloatValue() + (this.listWidth() - var6.width(var7)) / 2.0F,
            this.viewport.getFloatValue2() + (this.gridHeight - var6.height()) / 2.0F,
            MenuTheme.textTertiary()
         );
      } else {
         var1.enableScissor(
            this.viewport.getFloatValue(),
            this.viewport.getFloatValue2(),
            this.viewport.getFloatValue() + this.listWidth(),
            this.viewport.getFloatValue2() + this.gridHeight
         );
         VanillaOptionStyle.begin();

         try {
            for (Object var12_o : (Iterable<?>)(Object)(var5)) {
               ClickableWidget var12 = (ClickableWidget)var12_o;
               if (!(var12.getY() + var12.getHeight() < this.viewport.getFloatValue2()) && !(var12.getY() > this.viewport.getFloatValue2() + this.gridHeight)) {
                  var12.render(var1, (int)var2, (int)var3, 0.0F);
               }
            }
         } finally {
            VanillaOptionStyle.end();
         }

         var1.disableScissor();
         this.scroll.renderRail(var1, var4.getFloatValue() + this.listWidth() + RAIL_GAP, this.viewport.getFloatValue2(), this.gridHeight);
      }

      this.tabs.render(var1, var2, var3, var4.getFloatValue() + var4.width() / 2.0F, var4.getFloatValue2(), this.active);
   }

   private static Click zenith$click(double var0, double var2, int var4) {
      return new Click(var0, var2, new MouseInput(var4, 0));
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.search.mouseClicked(var1, var3, var5)) {
         return true;
      } else if (this.tabs.mouseClicked(var1, var3)) {
         return true;
      } else if (!this.viewport.contains(var1, var3)) {
         return false;
      } else {
         for (ClickableWidget var7 : this.visible()) {
            if (var7.mouseClicked(zenith$click(var1, var3, var5), false)) {
               this.dragged = var7;
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean mouseReleased(double var1, double var3, int var5) {
      ClickableWidget var6 = this.dragged;
      this.dragged = null;
      if (var6 == null) {
         return false;
      } else {
         var6.mouseReleased(zenith$click(var1, var3, var5));
         if (mc.options != null) {
            mc.options.write();
         }

         return true;
      }
   }

   @Override
   public boolean mouseDragged(double var1, double var3, int var5) {
      return this.dragged == null ? false : this.dragged.mouseDragged(zenith$click(var1, var3, var5), 0.0, 0.0);
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5) {
      return this.scroll.scroll(var5, ROW_HEIGHT + ROW_GAP);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      return this.search.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      return this.search.charTyped(var1, var2);
   }

   private static final class Category {
      private final String label;
      private final MenuIcon icon;
      private final Supplier<List<ClickableWidget>> harvest;

      public String label() {
         return this.label;
      }

      public MenuIcon icon() {
         return this.icon;
      }

      public Supplier<List<ClickableWidget>> harvest() {
         return this.harvest;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Category other)) return false;
         return java.util.Objects.equals(this.label, other.label) && java.util.Objects.equals(this.icon, other.icon) && java.util.Objects.equals(this.harvest, other.harvest);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(label, icon, harvest);
      }

      @Override
      public String toString() {
         return "Category[" + "label=" + this.label + ", " + "icon=" + this.icon + ", " + "harvest=" + this.harvest + "]";
      }

      private Category(String var1, MenuIcon var2, Supplier<List<ClickableWidget>> var3) {
         this.label = var1;
         this.icon = var2;
         this.harvest = var3;
      }
   }
}

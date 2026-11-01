package org.zenith.client.screens.nlgui;

import java.util.Locale;
import org.zenith.ModuleCategory;
import org.zenith.ZenithColor;
import org.zenith.base.font.SvgIcon;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.client.settings.TextSetting;
import org.zenith.utility.api.BlockListSetting;
import org.zenith.utility.render.display.base.MenuDrawContext;

public enum MenuGlyph {
   SLIDER(SvgIcon.SLIDER),
   TOGGLE(SvgIcon.TOGGLE),
   MODE(SvgIcon.MODE),
   MULTI(SvgIcon.MULTI),
   COLOR(SvgIcon.COLOR),
   KEY(SvgIcon.KEY),
   TEXT(SvgIcon.TEXT),
   ITEMS(SvgIcon.ITEMS),
   WINDOW(SvgIcon.WINDOW),
   ACTION(SvgIcon.ACTION),
   ROTATION(SvgIcon.ROTATION),
   DISTANCE(SvgIcon.DISTANCE),
   TIMER(SvgIcon.TIMER),
   SPEED(SvgIcon.SPEED),
   CRIT(SvgIcon.CRIT),
   TARGET(SvgIcon.TARGET),
   SIZE_GLYPH(SvgIcon.SIZE),
   OPACITY(SvgIcon.OPACITY),
   BOX(SvgIcon.BOX),
   WALL(SvgIcon.WALL),
   HAND(SvgIcon.HAND),
   HEART(SvgIcon.HEART),
   POTION(SvgIcon.POTION),
   PARTICLES(SvgIcon.PARTICLES),
   SOUND(SvgIcon.SOUND),
   NETWORK(SvgIcon.NETWORK),
   RANDOM(SvgIcon.RANDOM),
   SLOT(SvgIcon.SLOT),
   WORLD(SvgIcon.WORLD),
   SHIELD(SvgIcon.SHIELD),
   LIMIT_MIN(SvgIcon.LIMIT_MIN),
   LIMIT_MAX(SvgIcon.LIMIT_MAX),
   PERCENT(SvgIcon.PERCENT),
   COMBAT(SvgIcon.COMBAT),
   MOVEMENT(SvgIcon.MOVEMENT),
   MISC(SvgIcon.MISC),
   VISUALS(SvgIcon.VISUALS),
   PVE(SvgIcon.PLAYER),
   FRIENDS(SvgIcon.FRIENDS),
   CONFIGS(SvgIcon.CONFIGS),
   INTERFACE(SvgIcon.INTERFACE),
   COSMETICS(SvgIcon.COSMETICS),
   PROFILE(SvgIcon.PROFILE),
   CLIENT(SvgIcon.CLIENT),
   CLOUD(SvgIcon.CLOUD),
   CODE(SvgIcon.CODE),
   CHAT(SvgIcon.CHAT),
   TOOLS(SvgIcon.TOOLS),
   BOTS(SvgIcon.BOTS),
   STORAGE(SvgIcon.STORAGE),
   CRAFT(SvgIcon.CRAFT),
   STICKER(SvgIcon.STICKER),
   MIC(SvgIcon.MIC),
   PIN(SvgIcon.PIN),
   GPS(SvgIcon.GPS),
   CHESTPLATE(SvgIcon.CHESTPLATE),
   CLOSE(SvgIcon.CLOSE),
   CHECK(SvgIcon.CHECK),
   SEARCH(SvgIcon.SEARCH),
   COPY(SvgIcon.COPY),
   PASTE(SvgIcon.PASTE),
   RESET(SvgIcon.RESET);

   public static final float SIZE = 6.0F;
   private final SvgIcon icon;
   private static final MenuGlyph.Rule[] RULES = new MenuGlyph.Rule[]{
      new MenuGlyph.Rule(CRIT, "crit"),
      new MenuGlyph.Rule(DISTANCE, "distance", "dist", "range", "reach", "radius"),
      new MenuGlyph.Rule(ROTATION, "rotation", "angle", "correction", "disorient", "recoil", "aim"),
      new MenuGlyph.Rule(TIMER, "cooldown", "delay", "lifetime", "duration", "tick", "time", "pulse"),
      new MenuGlyph.Rule(SPEED, "speed", "accel", "deceleration"),
      new MenuGlyph.Rule(TARGET, "target", "enemies", "enemy", "predict"),
      new MenuGlyph.Rule(FRIENDS, "friend"),
      new MenuGlyph.Rule(WALL, "wall"),
      new MenuGlyph.Rule(HAND, "hand", "arm", "swing"),
      new MenuGlyph.Rule(HEART, "health", "heart", "hp"),
      new MenuGlyph.Rule(POTION, "potion", "consumable", "food", "eat", "drink"),
      new MenuGlyph.Rule(SHIELD, "shield", "block", "armor"),
      new MenuGlyph.Rule(PARTICLES, "particle", "spark", "strand", "storm", "part", "trail"),
      new MenuGlyph.Rule(SOUND, "volume", "sound", "audio"),
      new MenuGlyph.Rule(NETWORK, "packet", "server", "ping", "connect"),
      new MenuGlyph.Rule(RANDOM, "random", "shuffle"),
      new MenuGlyph.Rule(SLOT, "slot", "hotbar", "inventory"),
      new MenuGlyph.Rule(WORLD, "fog", "daylight", "world", "weather", "sky", "light"),
      new MenuGlyph.Rule(OPACITY, "opacity", "alpha", "fill"),
      new MenuGlyph.Rule(SIZE_GLYPH, "scale", "size", "width", "height", "round", "thick"),
      new MenuGlyph.Rule(BOX, "box", "tracer", "glow", "outline", "esp", "render", "display", "preview", "texture", "animation", "trace", "line", "chams"),
      new MenuGlyph.Rule(MOVEMENT, "sprint", "jump", "fall", "elytra", "move", "walk", "step", "fly"),
      new MenuGlyph.Rule(KEY, "bind", "hotkey", "mousebutton"),
      new MenuGlyph.Rule(SEARCH, "search", "find", "price"),
      new MenuGlyph.Rule(CHAT, "chat", "message", "command", "nick", "login", "text"),
      new MenuGlyph.Rule(COLOR, "color", "gradient", "saturation", "hue"),
      new MenuGlyph.Rule(PERCENT, "percent", "chance", "ratio"),
      new MenuGlyph.Rule(LIMIT_MIN, "min"),
      new MenuGlyph.Rule(LIMIT_MAX, "max")
   };

   private MenuGlyph(SvgIcon var3) {
      this.icon = var3;
   }

   public static MenuGlyph forCategory(ModuleCategory var0) {
      return switch (var0) {
         case COMBAT -> COMBAT;
         case MOVEMENT -> MOVEMENT;
         case MISC -> MISC;
         case RENDER -> VISUALS;
         case PLAYER -> PVE;
         default -> INTERFACE;
      };
   }

   public static MenuGlyph forSection(NLMenuScreen.ElementsType var0) {
      return switch (var0) {
         case TOOLS -> TOOLS;
         case CLOUD -> CLOUD;
         case COSMETICS -> COSMETICS;
         case INTERFACE -> INTERFACE;
         case SCRIPTS -> CODE;
         default -> MISC;
      };
   }

   public static MenuGlyph forSetting(Setting var0) {
      if (var0 instanceof SliderSetting) {
         return SLIDER;
      } else if (var0 instanceof ModeSetting) {
         return MODE;
      } else if (var0 instanceof MultiSelectSetting) {
         return MULTI;
      } else if (var0 instanceof ColorSetting) {
         return COLOR;
      } else if (var0 instanceof BindSetting) {
         return KEY;
      } else if (var0 instanceof TextSetting) {
         return TEXT;
      } else if (var0 instanceof BlockListSetting) {
         return ITEMS;
      } else if (var0 instanceof GroupSetting) {
         return WINDOW;
      } else {
         return var0 instanceof ButtonSetting ? ACTION : TOGGLE;
      }
   }

   public static MenuGlyph forSettingPurpose(Setting var0) {
      if (var0 == null) {
         return MODE;
      } else {
         String var1 = var0.getKey();
         if (var1 != null && !var1.isBlank()) {
            String var2 = var1.substring(var1.lastIndexOf(46) + 1).replaceAll("([a-z0-9])([A-Z])", "$1 $2").toLowerCase(Locale.ROOT);
            String[] var3 = var2.split("[^a-zа-яё0-9]+");

            for (MenuGlyph.Rule var7 : RULES) {
               for (String var11 : var7.keywords) {
                  for (String var15 : var3) {
                     if (var15.equals(var11) || var15.equals(var11 + "s")) {
                        return var7.glyph;
                     }
                  }
               }
            }

            return forSetting(var0);
         } else {
            return forSetting(var0);
         }
      }
   }

   public SvgIcon svg() {
      return this.icon;
   }

   public void draw(MenuDrawContext var1, float var2, float var3, ZenithColor var4) {
      this.icon.draw(var1, var2, var3, 6.0F, var4);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static void drawChevron(MenuDrawContext var0, float var1, float var2, float var3, ZenithColor var4) {
      float var5 = 5.0F;
      var0.pushMatrix();

      try {
         var0.getMatrices().translate(var1 + var5 / 2.0F, var2);
         var0.getMatrices().rotate((float) (Math.PI / 2) * Math.clamp(var3, 0.0F, 1.0F));
         SvgIcon.CHEVRON_RIGHT.draw(var0, -var5 / 2.0F, -var5 / 2.0F, var5, var4);
      } catch (Throwable var8) {
         var0.popMatrix();
         throw var8;
      }

      var0.popMatrix();
   }

   private static final class Rule {
      private final MenuGlyph glyph;
      private final String[] keywords;

      public MenuGlyph glyph() {
         return this.glyph;
      }

      public String[] keywords() {
         return this.keywords;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Rule other)) return false;
         return java.util.Objects.equals(this.glyph, other.glyph) && java.util.Objects.equals(this.keywords, other.keywords);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(glyph, keywords);
      }

      @Override
      public String toString() {
         return "Rule[" + "glyph=" + this.glyph + ", " + "keywords=" + this.keywords + "]";
      }

      private Rule(MenuGlyph var1, String... var2) {
         this.glyph = var1;
         this.keywords = var2;
      }
   }
}

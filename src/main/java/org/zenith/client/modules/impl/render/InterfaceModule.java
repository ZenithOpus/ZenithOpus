package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.hud.CooldownsHud;
import org.zenith.client.hud.EventsHud;
import org.zenith.client.hud.Hud1;
import org.zenith.client.hud.Hud2;
import org.zenith.client.hud.Hud3;
import org.zenith.client.hud.Hud4;
import org.zenith.client.hud.InfoBoxesHud;
import org.zenith.client.hud.InfoBoxesHud2;
import org.zenith.client.hud.InterfaceHud;
import org.zenith.client.hud.KeybindsHud;
import org.zenith.client.hud.MusicHud;
import org.zenith.client.hud.PartyHud;
import org.zenith.client.hud.PotionsHud;
import org.zenith.client.hud.ScoreboardHud;
import org.zenith.client.hud.SidebarHud;
import org.zenith.client.hud.StaffListHud;
import org.zenith.client.hud.StatusBarsHud;
import org.zenith.client.hud.TargetHud;
import org.zenith.client.hud.TargetPotionsHud;
import org.zenith.client.hud.TimerHud;
import org.zenith.client.hud.WatermarkIslandHud;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.Class31;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.Jebriksheet;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.ShapeRenderer;
import org.zenith.utility.render.display.base.HudQueuedContext;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.Vec2Utils;

@ModuleInfo(
   name = "Interface",
   category = ModuleCategory.RENDER,
   description = "module.interface.description",
   method896 = true
)
public final class InterfaceModule extends Module {
   private final List<Interface> items = new ArrayList<>();
   private final Map<String, Interface> items2 = new HashMap<>();
   private final Jebriksheet jebriksheet = new Jebriksheet();
   private final Class31 class31 = new Class31();
   public static final InterfaceModule INSTANCE = new InterfaceModule();
   private Interface interfaceValue = null;
   private float floatValue;
   private float floatValue2;
   private float floatValue3;
   private float floatValue4;
   private float floatValue5;
   private long longValue = 0L;
   private boolean flag = false;
   private final MultiSelectSetting cosmetics = new MultiSelectSetting("module.interface.cosmetics");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.cosmetics, "module.interface.blur", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.cosmetics, "module.interface.glass", false);
   private final MultiSelectSetting.Entry entry3 = new MultiSelectSetting.Entry(this.cosmetics, "module.interface.glow", true);
   private final MultiSelectSetting.Entry entry4 = new MultiSelectSetting.Entry(this.cosmetics, "module.interface.whiteNoise", false);
   private final ModeSetting glowMode = new ModeSetting(
      "module.interface.glowMode", "module.interface.glowMode.desc", () -> true, "module.interface.glowMode.outline", "module.interface.fill"
   );
   private final SliderSetting glowRadius = new SliderSetting(
      "module.interface.glowRadius", 8.0F, 5.0F, 15.0F, 1.0F, "module.interface.glowRadius.desc", "px", () -> true, null
   );
   private final GroupSetting glowSettings = new GroupSetting(
      "module.interface.glowSettings", "module.interface.glowSettings.desc", this.entry3::isEnabled, this.glowMode, this.glowRadius
   );
   private final SliderSetting glareSpeed = new SliderSetting(
      "module.interface.glareSpeed", 0.2F, 0.05F, 1.0F, 0.05F, "module.interface.glareSpeed.desc", "x", () -> true, null
   );
   private final ModeSetting glareMode = new ModeSetting(
      "module.interface.glareMode",
      "module.interface.glareMode.desc",
      () -> true,
      "module.interface.glare.diagonal",
      "module.interface.glare.sweep",
      "module.interface.glare.double",
      "module.interface.glare.edge",
      "module.interface.glare.spotlight",
      "module.interface.glare.aurora",
      "module.interface.glare.off"
   );
   private static final int[] CONST_intValues = new int[]{0, 1, 2, 3, 5, 6, 11};
   private final ModeSetting glareTexture = new ModeSetting(
      "module.interface.glareTexture",
      "module.interface.glareTexture.desc",
      () -> true,
      "module.interface.glareTexture.plain",
      "module.interface.glareTexture.prism",
      "module.interface.glareTexture.bloom",
      "module.interface.glareTexture.neon",
      "module.interface.glareTexture.chrome",
      "module.interface.glareTexture.rays"
   );
   private final GroupSetting glassSettings = new GroupSetting(
      "module.interface.glassSettings", "module.interface.glassSettings.desc", this.entry2::isEnabled, this.glareMode, this.glareTexture, this.glareSpeed
   );
   public final SliderSetting volume = new SliderSetting(
      "module.interface.volume", 0.5F, 0.1F, 1.0F, 0.1F, "module.interface.volume.desc", "%", this::method6814, null
   );
   private final SliderSetting round = new SliderSetting("module.interface.round", 6.0F, 0.0F, 6.0F, 0.1F, "module.interface.round.desc", "px");
   private final BooleanSetting corners = new BooleanSetting("module.interface.corners", "module.interface.corners.desc", true);
   private final BooleanSetting saturation = new BooleanSetting("module.interface.saturation", "module.interface.saturation.desc", true);
   private final BooleanSetting hideScoreBoard = new BooleanSetting("module.interface.hideScoreBoard", "module.interface.hideScoreBoard.desc", false);
   private final SliderSetting density = new SliderSetting("module.interface.hud.density", 100.0F, 0.0F, 150.0F, 5.0F, "module.interface.hud.density.desc", "%");
   private final SliderSetting blur = new SliderSetting("module.interface.hud.blur", 21.0F, 0.0F, 40.0F, 1.0F, "module.interface.hud.blur.desc", "px");
   private final ModeSetting outline = new ModeSetting(
      "module.interface.hud.outline",
      "module.interface.hud.outline.desc",
      "module.interface.hud.outline.none",
      "module.interface.hud.outline.soft",
      "module.interface.hud.outline.accent",
      "module.interface.hud.outline.custom"
   );
   private final SliderSetting outlineWidth = new SliderSetting(
      "module.interface.hud.outlineWidth", 0.6F, 0.2F, 2.0F, 0.1F, "module.interface.hud.outlineWidth.desc", "px", () -> !this.outline.isSelected(0)
   );
   private final ColorSetting colorSetting = new ColorSetting(
      "module.interface.hud.outlineColor", "module.interface.hud.outlineColor.desc", new ZenithColor(255, 255, 255, 90), () -> this.outline.isSelected(3)
   );
   private final BooleanSetting fade = new BooleanSetting("module.interface.hud.fade", "module.interface.hud.fade.desc", true);
   private final SliderSetting fadeTime = new SliderSetting(
      "module.interface.hud.fadeTime", 220.0F, 60.0F, 800.0F, 20.0F, "module.interface.hud.fadeTime.desc", "ms", this.fade::isEnabled
   );
   private final BooleanSetting hideInScreens = new BooleanSetting("module.interface.hud.hideInScreens", "module.interface.hud.hideInScreens.desc", false);
   private final SliderSetting combatTime = new SliderSetting(
      "module.interface.hud.combatTime", 6.0F, 2.0F, 20.0F, 1.0F, "module.interface.hud.combatTime.desc", "s"
   );
   private final BooleanSetting guides = new BooleanSetting("module.interface.hud.guides", "module.interface.hud.guides.desc", true);
   private final SliderSetting grid = new SliderSetting("module.interface.hud.grid", 0.0F, 0.0F, 16.0F, 1.0F, "module.interface.hud.grid.desc", "px");
   private final SliderSetting margin = new SliderSetting("module.interface.hud.margin", 0.0F, 0.0F, 24.0F, 1.0F, "module.interface.hud.margin.desc", "px");
   private final ButtonSetting editor = new ButtonSetting("module.interface.hud.editor", this::method6792);
   private final ButtonSetting showAll = new ButtonSetting("module.interface.hud.showAll", () -> this.method6790(true));
   private final ButtonSetting hideAll = new ButtonSetting("module.interface.hud.hideAll", () -> this.method6790(false));
   private final ButtonSetting resetLayout = new ButtonSetting("module.interface.hud.resetLayout", this::method6791);
   private long longValue2 = -4611686018427387904L;
   private final SliderSetting scale = new SliderSetting(
      "module.interface.hudElement.scale", 100.0F, 50.0F, 250.0F, 1.0F, "module.interface.hudElement.scale.desc", "%", () -> true, (var1x, var2) -> {
         for (Interface var4 : this.items) {
            var4.getScale().method2400(var2);
         }
      }
   );
   private static final int CONST_intValue = 64;

   public boolean method6770() {
      return this.hideScoreBoard.isEnabled();
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(this.cosmetics, this.glowSettings, this.glassSettings, this.round, this.corners, this.saturation, this.hideScoreBoard, this.volume);
   }

   public List<Setting> method6771() {
      return List.of(this.cosmetics, this.glowSettings, this.glassSettings, this.round, this.corners);
   }

   public List<Setting> method6772() {
      return List.of(this.saturation, this.hideScoreBoard, this.volume);
   }

   public List<Setting> method6773() {
      return List.of(this.editor, this.showAll, this.hideAll, this.resetLayout);
   }

   public List<Setting> method6774() {
      return List.of(this.density, this.blur, this.outline, this.outlineWidth, this.colorSetting, this.scale);
   }

   public List<Setting> method6775() {
      return List.of(this.guides, this.grid, this.margin);
   }

   public List<Setting> method6776() {
      return List.of(this.fade, this.fadeTime, this.hideInScreens, this.combatTime);
   }

   private List<Setting> method6777() {
      var var1 = new ArrayList<>(this.method6774());
      var1.remove(this.scale);
      var1.addAll(this.method6775());
      var1.addAll(this.method6776());
      return var1;
   }

   public float method6778() {
      return this.density.getValue3() / 100.0F;
   }

   public float method6779(float var1) {
      float var2 = this.blur.getValue3();
      return var2 <= 0.0F ? 0.0F : var1 * var2 / 21.0F;
   }

   public boolean method6780() {
      return !this.outline.isSelected(0);
   }

   public float method6781() {
      return this.outlineWidth.getValue3();
   }

   public ZenithColor method6782() {
      ZenithStyle var1 = Zenith.getInstance().getStyleManager().getCurrentStyle();

      return switch (this.outline.getIndex()) {
         case 1 -> var1.getTextTertiary().getZenithColor().method7293(70);
         case 2 -> var1.getPrimaryColor().getZenithColor().method7293(150);
         default -> this.colorSetting.getZenithColor();
      };
   }

   public boolean method6783() {
      return this.fade.isEnabled();
   }

   public float method6784() {
      return this.fadeTime.getValue3();
   }

   public boolean method6785() {
      return this.hideInScreens.isEnabled();
   }

   public float method6786() {
      return this.grid.getValue3();
   }

   public float method6787() {
      return this.margin.getValue3();
   }

   public boolean method6788() {
      return (float)(System.currentTimeMillis() - this.longValue2) < this.combatTime.getValue3() * 1000.0F;
   }

   private void method6789() {
      if (mc.player != null) {
         boolean var1 = mc.player.hurtTime > 0;
         boolean var2 = mc.player.handSwinging && mc.targetedEntity instanceof LivingEntity;
         if (var1 || var2) {
            this.longValue2 = System.currentTimeMillis();
         }
      }
   }

   public void method6790(boolean var1) {
      for (Interface var3 : this.items) {
         var3.setEnabled(var1);
      }
   }

   public void method6791() {
      for (Interface var2 : this.items) {
         var2.method2063();
      }
   }

   private void method6792() {
      mc.setScreen(new ChatScreen("", false));
   }

   private InterfaceModule() {
      this.method6793(new Hud3("ItemBinds", 349.0F, 0.0F, 960.0F, 495.5F, -11.5F, 146.0F, Interface.Type.TOP_RIGHT));
      this.method6793(new WatermarkIslandHud("Watermark", 0.0F, 0.0F, 960.0F, 495.5F, 10.0F, 10.0F, Interface.Type.TOP_LEFT));
      this.method6793(new PotionsHud("Potions", 0.0F, 0.0F, 960.0F, 495.5F, 119.15234F, 73.0F, Interface.Type.TOP_LEFT));
      this.method6793(new StaffListHud("Staffs", 0.0F, 0.0F, 960.0F, 495.5F, 10.0F, 73.0F, Interface.Type.TOP_LEFT));
      InterfaceHud var1 = new InterfaceHud("Notifications", 181.80615F, 135.5F, 960.0F, 495.5F, 157.03516F, -72.5F, Interface.Type.CENTER);
      this.method6793(var1);
      Zenith.getInstance().getHudHolder().setInterfaceHud(var1);
      this.method6793(new Hud4("Inventory", 269.0F, 229.0F, 960.0F, 495.5F, -11.5F, -74.0F, Interface.Type.BOTTOM_RIGHT));
      this.method6793(new CooldownsHud("Cooldowns", 349.0F, 0.0F, 960.0F, 495.5F, -11.5F, 73.0F, Interface.Type.TOP_RIGHT));
      this.method6793(new InfoBoxesHud("Information", 0.0F, 0.0F, 960.0F, 495.5F, 10.0F, 41.5F, Interface.Type.TOP_LEFT));
      this.method6793(new TimerHud("TimerBar", 0.0F, 0.0F, 960.0F, 495.5F, 10.0F, 57.5F, Interface.Type.TOP_LEFT));
      this.method6793(new InfoBoxesHud2("Coordinates", 0.0F, 0.0F, 960.0F, 495.5F, 10.0F, 41.5F, Interface.Type.TOP_LEFT));
      this.method6793(new KeybindsHud("Keybinds", 349.0F, 0.0F, 960.0F, 495.5F, -122.0F, 73.0F, Interface.Type.TOP_RIGHT));
      this.method6793(new TargetHud("TargetHud", 166.5F, 128.5F, 960.0F, 495.5F, 0.0F, 31.75F, Interface.Type.CENTER));
      this.method6793(new MusicHud("MusicInfo", 342.0F, 257.0F, 960.0F, 495.5F, -11.5F, -16.5F, Interface.Type.BOTTOM_RIGHT));
      this.method6793(new StatusBarsHud("HootBar", 116.5F, 265.0F, 960.0F, 495.5F, 0.0F, -16.5F, Interface.Type.BOTTOM_CENTER));
      this.method6793(new ScoreboardHud("ScoreBoard", 0.0F, 0.0F, 960.0F, 495.5F, -10.0F, 10.0F, Interface.Type.CENTER_RIGHT));
      this.method6793(new SidebarHud("AnimatedTab"));
      this.method6793(new Hud2("ArmorHud", 0.0F, 0.0F, 960.0F, 495.5F, -10.0F, 10.0F, Interface.Type.CENTER_RIGHT));
      this.method6793(new Hud1("Totems", 0.0F, 0.0F, 960.0F, 495.5F, 0.0F, -40.0F, Interface.Type.BOTTOM_CENTER));
      this.method6793(new TargetPotionsHud("TargetPotions", 0.0F, 0.0F, 960.0F, 495.5F, 119.15234F, 90.0F, Interface.Type.TOP_LEFT));
      this.method6793(new EventsHud("Events", 116.5F, 265.0F, 960.0F, 495.5F, 0.0F, -16.5F, Interface.Type.BOTTOM_CENTER));
      this.method6793(new PartyHud("Party", 0.0F, 0.0F, 960.0F, 495.5F, 10.0F, 150.0F, Interface.Type.TOP_LEFT));
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void toggle() {
      if (!this.isEnabledRaw()) {
         super.toggle();
      }
   }

   @Override
   public void setKeyCode(int var1) {
      super.setKeyCode(-1);
   }

   @Override
   public JsonObject save() {
      JsonObject var1 = super.save();
      JsonObject var2 = new JsonObject();

      for (Setting var4 : this.method6777()) {
         var4.safe(var2);
      }

      var1.add("HudLook", var2);
      JsonObject var6 = new JsonObject();

      for (Interface var5 : this.items) {
         var6.add(var5.getName(), var5.save());
      }

      var1.add("HudElements", var6);
      return var1;
   }

   @Override
   public void load(JsonObject var1) {
      super.load(var1);
      if (var1 != null) {
         if (var1.has("HudLook") && var1.get("HudLook").isJsonObject()) {
            JsonObject var2 = var1.getAsJsonObject("HudLook");

            for (Setting var4 : this.method6777()) {
               if (var2.has(var4.getKey())) {
                  var4.load(var2);
               }
            }
         }

         if (var1.has("HudElements") && var1.get("HudElements").isJsonObject()) {
            JsonObject var6 = var1.getAsJsonObject("HudElements");

            for (Interface var8 : this.items) {
               String var5 = var8.getName();
               if (var6.has(var5) && var6.get(var5).isJsonObject()) {
                  var8.load(var6.getAsJsonObject(var5));
               }
            }
         }
      }
   }

   private void method6793(Interface var1) {
      this.items.add(var1);
      this.items2.put(var1.getName(), var1);
   }

   public List<Interface> method6794() {
      return Collections.unmodifiableList(this.items);
   }

   public boolean method6795(Interface var1) {
      return this.class31.method4878(var1) || var1 != null && var1 == this.interfaceValue;
   }

   public boolean method6796() {
      return this.saturation.isEnabled();
   }

   @Override
   public boolean isRenderSetting() {
      return false;
   }

   @EventTarget
   public void method6797(ScreenRenderEvent var1) {
      if (mc.world != null && mc.interactionManager != null && mc.player != null && !mc.options.hudHidden) {
         if (!(mc.currentScreen instanceof ChatScreen)) {
            this.class31.cancel();
            if (this.interfaceValue != null) {
               this.interfaceValue = null;
            }

            this.method6804();
         }

         MenuDrawContext var2 = var1.getMenuDrawContext();
         HudQueuedContext var3 = HudQueuedContext.of(var2);
         float var4 = mc.getWindow().getScaledWidth();
         float var5 = mc.getWindow().getScaledHeight();
         this.method6789();
         var2.pushMatrix();
         Jebriksheet.method2346(this.jebriksheet);

         try {
            for (Interface var7 : this.items) {
               if (!this.class31.method4878(var7)) {
                  var7.method288(var4, var5);
               }

               if (this.method6800(var7)) {
                  try {
                     var7.method291(var3);
                  } catch (Exception var18) {
                     var18.printStackTrace();
                  }
               }
            }

            ArrayList var20 = new ArrayList();

            for (Interface var8 : this.items) {
               if (this.method6800(var8) && var8.method296() > 0.0F && var8.method297() > 0.0F) {
                  var20.add(var8);
               }
            }

            Vector2f var23 = this.method6808();
            this.class31.method4880(var3, var20, var23.x(), var23.y(), var4, var5, mc.currentScreen instanceof ChatScreen);
            this.jebriksheet.flush();
         } finally {
            Jebriksheet.method2347(this.jebriksheet);
         }

         boolean var21 = mc.currentScreen instanceof ChatScreen;
         Vector2f var24 = this.method6808();
         if (var21) {
            float var9 = var24.x();
            float var10 = var24.y();
            if (this.interfaceValue != null) {
               float var11 = this.floatValue - this.floatValue3;
               float var12 = this.floatValue2 - this.floatValue4;
               float var13 = var11 * var11 + var12 * var12;
               if (var13 > 1.0F) {
                  float var14 = ((var9 - this.floatValue3) * var11 + (var10 - this.floatValue4) * var12) / var13;
                  float var15 = this.floatValue5 * var14;
                  var15 = Math.clamp(var15, this.interfaceValue.getScale().getMin(), this.interfaceValue.getScale().getMax());
                  this.interfaceValue.getScale().method2400(var15);
               }
            }

            Interface var25 = this.method6805(var9, var10);
            if (this.interfaceValue == null && (var25 == null || var25.method2059() || !var25.method2075(var9, var10))) {
               this.method6804();
            } else {
               this.method6803();
            }
         } else {
            this.method6804();
         }

         var2.popMatrix();
      }
   }

   private float method6798(Interface var1) {
      return var1.method294();
   }

   private float method6799(Interface var1) {
      return var1.method295();
   }

   private boolean method6800(Interface var1) {
      return var1.isEnabled();
   }

   public static float method6801() {
      return INSTANCE.round.getValue3();
   }

   @EventTarget
   public void method6802(MouseButtonEvent var1) {
      if (!(mc.currentScreen instanceof ChatScreen)) {
         this.class31.cancel();
         if (this.interfaceValue != null) {
            this.interfaceValue = null;
            this.method6804();
         }
      } else {
         Vector2f var2 = this.method6808();
         double var3 = var2.x();
         double var5 = var2.y();
         if (var1.getIntValue2() == 0 && var1.getIntValue() == 1) {
            Interface var8 = this.method6805(var3, var5);
            if (var8 != null) {
               this.class31.cancel();
               this.method6806(var8);
            }
         } else {
            if (var1.getIntValue2() == 1 && var1.getIntValue() == 0) {
               Interface var7 = this.method6805(var3, var5);
               if (var7 != null) {
                  if (var7.method2059()) {
                     var7.method2056(var1);
                  } else if (var7.method2075(var3, var5)) {
                     this.interfaceValue = var7;
                     this.floatValue = (float)var3;
                     this.floatValue2 = (float)var5;
                     this.floatValue3 = var7.method294() + var7.method296() / 2.0F;
                     this.floatValue4 = var7.method295() + var7.method297() / 2.0F;
                     this.floatValue5 = var7.getScale().getValue3();
                  } else if (!var7.method2056(var1)) {
                     this.class31.method4877(var7, (float)var3, (float)var5);
                  }
               }
            } else if (var1.getIntValue2() == 0) {
               if (this.interfaceValue != null) {
                  this.interfaceValue = null;
                  this.method6804();
               }

               this.class31.end();
            }
         }
      }
   }

   private void method6803() {
      if (!this.flag) {
         if (this.longValue == 0L) {
            this.longValue = GLFW.glfwCreateStandardCursor(221191);
         }

         if (this.longValue != 0L) {
            GLFW.glfwSetCursor(mc.getWindow().getHandle(), this.longValue);
            this.flag = true;
         }
      }
   }

   private void method6804() {
      if (this.flag) {
         GLFW.glfwSetCursor(mc.getWindow().getHandle(), 0L);
         this.flag = false;
      }
   }

   private Interface method6805(double var1, double var3) {
      for (int var5 = this.items.size() - 1; var5 >= 0; var5--) {
         Interface var6 = this.items.get(var5);
         if (this.method6800(var6) && var6.method293(var1, var3)) {
            return var6;
         }
      }

      return null;
   }

   private void method6806(Interface var1) {
      NLMenuScreen var2 = Zenith.getInstance().getNLMenuScreen();
      if (var2 != null && var1 != null) {
         if (mc.currentScreen != var2) {
            if (!MenuModule.INSTANCE.isEnabled()) {
               MenuModule.INSTANCE.toggle();
            } else {
               mc.setScreen(var2);
            }
         }

         var2.openHudElementSettings(var1);
      }
   }

   public float method6807() {
      return mc.getWindow().getScaleFactor();
   }

   private Vector2f method6808() {
      return Vec2Utils.method8068(this.method6807());
   }

   public org.joml.Vector2f method6809(float var1, float var2) {
      float var3 = Float.MAX_VALUE;
      float var4 = Float.MAX_VALUE;
      float var5 = 2.0F;
      org.joml.Vector2f var6 = new org.joml.Vector2f(-1.0F, -1.0F);
      float var7 = mc.getWindow().getScaledWidth() / 2.0F;
      float var8 = mc.getWindow().getScaledHeight() / 2.0F;
      float var9 = this.method6810(var7, var7, var7, var1);
      float var10 = this.method6810(var8, var8, var8, var2);
      float var11 = MathUtils.method2650(var9, var1);
      float var12 = MathUtils.method2650(var10, var2);
      boolean var13 = false;
      if (var11 < var3 && var11 < var5) {
         var6.x = var9;
         var13 = true;
      }

      if (var12 < var4 && var12 < var5) {
         var6.y = var10;
         var13 = true;
      }

      if (var13) {
         return var6;
      } else if (!this.guides.isEnabled()) {
         return var6;
      } else {
         for (Interface var20 : this.items) {
            if (!var20.equals(this.class31.getInterfaceValue()) && this.method6800(var20)) {
               var9 = this.method6798(var20);
               var10 = this.method6799(var20);
               var11 = var9 + var20.method296();
               var12 = var10 + var20.method297();
               float var25 = var9 + var20.method296() / 2.0F;
               float var14 = var10 + var20.method297() / 2.0F;
               float var15 = this.method6810(var9, var11, var25, var1);
               float var16 = this.method6810(var10, var12, var14, var2);
               float var17 = MathUtils.method2650(var15, var1);
               float var18 = MathUtils.method2650(var16, var2);
               if (var17 < var3) {
                  var3 = var17;
                  if (var17 < var5) {
                     var6.x = var15;
                  }
               }

               if (var18 < var4) {
                  var4 = var18;
                  if (var18 < var5) {
                     var6.y = var16;
                  }
               }
            }
         }

         return var6;
      }
   }

   public float method6810(float var1, float var2, float var3, float var4) {
      float var5 = var1;
      if (MathUtils.method2650(var2, var4) < MathUtils.method2650(var1, var4)) {
         var5 = var2;
      }

      if (MathUtils.method2650(var3, var4) < MathUtils.method2650(var5, var4)) {
         var5 = var3;
      }

      return var5;
   }

   public boolean method6811() {
      return this.method6815("ScoreBoard");
   }

   public boolean method6812() {
      return this.method6815("HootBar");
   }

   public boolean method6813() {
      return this.method6815("AnimatedTab");
   }

   public boolean method6814() {
      return this.method6815("Notifications");
   }

   private boolean method6815(String var1) {
      Interface var2 = this.items2.get(var1);
      return var2 != null && var2.isEnabled();
   }

   @EventTarget
   public void method6816(UpdateEvent var1) {
      if (ShapeRenderer.hashMap.size() > 64) {
         ShapeRenderer.hashMap.values().removeIf(var0 -> {
            if (var0.method7214()) {
               var0.method7215();
               return true;
            } else {
               return false;
            }
         });
      }

      for (Interface var3 : this.items) {
         if (this.method6800(var3)) {
            try {
               var3.tick();
            } catch (Throwable var5) {
               System.out.println(var3.getName());
               var5.printStackTrace();
            }
         }
      }
   }

   public boolean method6817() {
      return this.entry.isEnabled();
   }

   public boolean method6818() {
      return this.entry2.isEnabled();
   }

   public boolean method6819() {
      return this.entry3.isEnabled();
   }

   public boolean method6820() {
      return this.entry4.isEnabled();
   }

   public boolean method6821() {
      return this.entry3.isEnabled() && this.glowMode.isSelected(0);
   }

   public boolean method6822() {
      return this.corners.isEnabled();
   }

   public int method6823() {
      return (int)this.glowRadius.getValue3();
   }

   public ZenithColor method6824() {
      return Zenith.getInstance().getStyleManager().getCurrentStyle().getGlareColor().getZenithColor();
   }

   public float method6825() {
      return this.glareSpeed.getValue3();
   }

   public int method6826() {
      int var1 = this.glareMode.getIndex();
      return var1 >= 0 && var1 < CONST_intValues.length ? CONST_intValues[var1] : 0;
   }

   public int method6827() {
      return Math.max(0, this.glareTexture.getIndex());
   }

   public boolean method6828() {
      return this.method6815("Potions");
   }
}

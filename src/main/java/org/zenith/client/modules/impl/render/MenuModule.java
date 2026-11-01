package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.client.gui.screen.Screen;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.screens.bot.BotControlScreen;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.MenuDrawContext;

@ModuleInfo(
   name = "Menu",
   category = ModuleCategory.RENDER,
   description = "module.menu.description",
   method896 = true
)
public final class MenuModule extends Module {
   public static final MenuModule INSTANCE = new MenuModule();
   private final ModeSetting animation = new ModeSetting(
      "module.menu.animation", "module.menu.animation.desc", "module.menu.animation.normal", "module.menu.animation.blur"
   );
   private final SliderSetting closeAnimationSpeed = new SliderSetting(
      "module.menu.closeAnimationSpeed", 300.0F, 100.0F, 500.0F, 10.0F, "module.menu.closeAnimationSpeed.desc", "ms"
   );
   private final SliderSetting elementSwitchSpeed = new SliderSetting(
      "module.menu.elementSwitchSpeed", 250.0F, 100.0F, 500.0F, 10.0F, "module.menu.elementSwitchSpeed.desc", "ms"
   );
   private final BooleanSetting moveGui = new BooleanSetting("module.menu.moveGui", "module.menu.moveGui.desc", false);
   private boolean flag;
   private float floatValue;
   private float floatValue2;
   private Screen screen;

   private MenuModule() {
      this.setKeyCode(344);
   }

   @Override
   public void onEnable() {
      if (mc.mouse == null) {
         this.setEnabled(false);
      } else {
         this.screen = mc.currentScreen;
         if (mc.currentScreen != Zenith.getInstance().getNLMenuScreen()) {
            NLMenuScreen var1 = Zenith.getInstance().getNLMenuScreen();
            var1.initialize();
            boolean var2 = this.screen instanceof BotControlScreen;
            var1.getGuiModulePanel().setBotOnly(var2);
            if (var2) {
               var1.resetSearch();
               var1.setType(NLMenuScreen.ElementsType.CATEGORY);
               var1.getGuiModulePanel().setBotContext(((BotControlScreen)this.screen).getBotName());
               var1.getGuiModulePanel().forceCategory(ModuleCategory.PLAYER);
            }

            mc.setScreen(var1);
            Zenith.getInstance().getClientSounds().method1561(Zenith.getInstance().getClientSounds().soundEvent);
            super.onEnable();
         }
      }
   }

   @Override
   public void onDisable() {
      if (mc.currentScreen == Zenith.getInstance().getNLMenuScreen()) {
         this.setEnabled(true);
      } else {
         super.onDisable();
      }
   }

   @Override
   public void setKeyCode(int var1) {
      if (var1 != -1) {
         super.setKeyCode(var1);
      }
   }

   @EventTarget(3)
   public void method5343(ScreenRenderEvent var1) {
      MenuDrawContext var2 = var1.getMenuDrawContext();
      if (this.screen != null) {
         this.screen.render(var2, 0, 0, mc.getRenderTickCounter().getTickProgress(false));
         ScissorFlusher.flush();
         Zenith.getInstance().getKawaseBlur().method7349(var2);
      }

      Zenith.getInstance().getNLMenuScreen().renderTop(var2, var2.getMouseX(), var2.getMouseY());
      if (Zenith.getInstance().getNLMenuScreen().isFinish()) {
         if (this.screen instanceof BotControlScreen) {
            Screen var3 = this.screen;
            this.screen = null;
            if (mc.currentScreen == Zenith.getInstance().getNLMenuScreen() || mc.currentScreen == null) {
               mc.setScreen(var3);
            }
         }

         this.toggle();
      }
   }

   public boolean method5344() {
      return this.animation.isSelected(1);
   }

   public long method5345() {
      return (long)this.closeAnimationSpeed.getValue3();
   }

   public long method5346() {
      return (long)this.elementSwitchSpeed.getValue3();
   }

   public long method5347() {
      return (long)this.elementSwitchSpeed.getValue3();
   }

   public boolean method5348() {
      return this.moveGui.isEnabled();
   }

   public List<Setting> method5349() {
      return List.of(this.moveGui);
   }

   public List<Setting> method5350() {
      return List.of(this.animation, this.closeAnimationSpeed, this.elementSwitchSpeed);
   }

   public float method5351(float var1) {
      return this.flag ? this.floatValue : var1;
   }

   public float method5352(float var1) {
      return this.flag ? this.floatValue2 : var1;
   }

   public void method5353(float var1, float var2) {
      this.floatValue = var1;
      this.floatValue2 = var2;
      this.flag = true;
   }

   @Override
   public JsonObject save() {
      JsonObject var1 = super.save();
      JsonObject var2 = new JsonObject();
      var2.addProperty("saved", this.flag);
      var2.addProperty("x", this.floatValue);
      var2.addProperty("y", this.floatValue2);
      var1.add("GuiPosition", var2);
      return var1;
   }

   @Override
   public void load(JsonObject var1) {
      super.load(var1);
      if (var1 != null && var1.has("GuiPosition") && var1.get("GuiPosition").isJsonObject()) {
         JsonObject var2 = var1.getAsJsonObject("GuiPosition");
         if (var2.has("saved")) {
            this.flag = var2.get("saved").getAsBoolean();
         }

         if (var2.has("x")) {
            this.floatValue = var2.get("x").getAsFloat();
         }

         if (var2.has("y")) {
            this.floatValue2 = var2.get("y").getAsFloat();
         }
      }
   }
}

package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.MathHelper;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ColorSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.CanvasSetting;
import org.zenith.utility.misc.Canvas;
import org.zenith.utility.misc.CrosshairPresets;
import org.zenith.utility.render.display.base.MenuDrawContext;

@ModuleInfo(
   name = "Crosshair",
   category = ModuleCategory.RENDER,
   description = "module.crosshair.description"
)
public final class CrosshairModule extends Module {
   public static final CrosshairModule INSTANCE = new CrosshairModule();
   private static final int CONST_intValue = 0;
   private static final int CONST_intValue2 = 1;
   private static final int CONST_intValue3 = 2;
   private static final int CONST_intValue4 = 3;
   private static final int CONST_intValue5 = 4;
   private static final float CONST_floatValue = 220.0F;
   private static final long CONST_longValue = System.currentTimeMillis();
   private final CanvasSetting canvas = new CanvasSetting("module.crosshair.canvas");
   private final ButtonSetting editor = new ButtonSetting("module.crosshair.editor", "w", this::method516);
   private final SliderSetting scale = new SliderSetting("module.crosshair.scale", 2.0F, 1.0F, 6.0F, 1.0F, "module.crosshair.scale.desc", "x");
   private final ModeSetting color = new ModeSetting(
      "module.crosshair.color", "module.crosshair.colorMode.desc", "module.crosshair.asDrawn", "module.particles.sync", "module.particles.custom"
   );
   private final ColorSetting customColor = new ColorSetting(
      "module.crosshair.customColor", "module.crosshair.customColor.desc", ZenithColor.INSTANCE, () -> this.color.isSelected(2)
   );
   private final ModeSetting animation = new ModeSetting(
      "module.crosshair.animation",
      "module.crosshair.animation.desc",
      "module.crosshair.animNone",
      "module.crosshair.animSpin",
      "module.crosshair.animPulse",
      "module.crosshair.animRecoil",
      "module.crosshair.animHit"
   );
   private final SliderSetting animationSpeed = new SliderSetting(
      "module.crosshair.animationSpeed", 1.0F, 0.2F, 3.0F, 0.1F, "module.crosshair.animationSpeed.desc", "x", () -> !this.animation.isSelected(0), null
   );
   private long longValue;

   private List<Setting> method515() {
      return List.of(this.scale, this.color, this.customColor, this.animation, this.animationSpeed);
   }

   @Override
   public List<Setting> getSettings() {
      return List.of(this.editor, this.canvas);
   }

   @Override
   public JsonObject save() {
      JsonObject var1 = super.save();
      JsonObject var2 = var1.getAsJsonObject("Settings");

      for (Setting var4 : this.method515()) {
         var4.safe(var2);
      }

      return var1;
   }

   @Override
   public void load(JsonObject var1) {
      super.load(var1);
      if (var1 != null) {
         JsonObject var2 = var1.getAsJsonObject("Settings");
         if (var2 != null) {
            for (Setting var4 : this.method515()) {
               if (var2.has(var4.getKey())) {
                  var4.load(var2);
               }
            }
         }
      }
   }

   private void method516() {
      Zenith.getInstance().getNLMenuScreen().getCrosshairEditor().open(this.canvas.getCanvas(), this.method515());
   }

   private CrosshairModule() {
      this.canvas.getCanvas().method139(CrosshairPresets.method5712(0));
   }

   @EventTarget
   public void method517(AttackEvent var1) {
      if (var1.getType4() == AttackEvent.Type.POST) {
         this.longValue = System.currentTimeMillis();
      }
   }

   @EventTarget(0)
   public void method518(ScreenRenderEvent var1) {
      if (mc.player != null && mc.world != null) {
         if (mc.options.getPerspective() == Perspective.FIRST_PERSON) {
            Canvas var2 = this.canvas.getCanvas();
            if (!var2.isEmpty()) {
               MenuDrawContext var3 = var1.getMenuDrawContext();
               float var4 = mc.getWindow().getScaledWidth() / 2.0F;
               float var5 = mc.getWindow().getScaledHeight() / 2.0F;
               float var6 = this.scale.getValue3();
               float var7 = this.animationSpeed.getValue3();
               float var8 = (float)(System.currentTimeMillis() - CONST_longValue) / 1000.0F;
               float var9 = 0.0F;
               float var10 = 1.0F;
               float var11 = 1.0F;
               switch (this.method520()) {
                  case 1:
                     var9 = var8 * 60.0F * var7;
                     break;
                  case 2:
                     var10 = 1.0F + 0.14F * MathHelper.sin(var8 * 3.0F * var7);
                     break;
                  case 3:
                     float var16 = MathHelper.clamp(mc.player.getAttackCooldownProgress(0.0F), 0.0F, 1.0F);
                     var10 = 1.0F + 0.45F * (1.0F - var16) * var7;
                     break;
                  case 4:
                     float var12 = (float)(System.currentTimeMillis() - this.longValue) / (220.0F / var7);
                     float var13 = var12 >= 1.0F ? 0.0F : (1.0F - var12) * (1.0F - var12);
                     var10 = 1.0F + 0.5F * var13;
                     var11 = 1.0F + 0.8F * var13;
               }

               var3.pushMatrix();
               var3.getMatrices().translate(var4, var5);
               if (var9 != 0.0F) {
                  var3.getMatrices().rotate((float)Math.toRadians(var9));
               }

               if (var10 != 1.0F) {
                  var3.getMatrices().scale(var10, var10);
               }

               var3.beginRectBatch();
               float var17 = -15.0F * var6;

               for (int var18 = 0; var18 < 31; var18++) {
                  for (int var14 = 0; var14 < 31; var14++) {
                     int var15 = var2.method136(var14, var18);
                     if (var15 >>> 24 != 0) {
                        var3.drawRectBatched(var17 + var14 * var6, var17 + var18 * var6, var6, var6, this.method519(var15, var11));
                     }
                  }
               }

               var3.flushRects();
               var3.popMatrix();
            }
         }
      }
   }

   private ZenithColor method519(int var1, float var2) {
      int var3 = var1 >>> 24;
      int var4 = var1 >> 16 & 0xFF;
      int var5 = var1 >> 8 & 0xFF;
      int var6 = var1 & 0xFF;
      if (this.color.isSelected(1)) {
         ZenithColor var7 = Zenith.getInstance().getStyleManager().getClientColor(0);
         var4 = var7.getIntValue();
         var5 = var7.getIntValue2();
         var6 = var7.getIntValue3();
      } else if (this.color.isSelected(2)) {
         ZenithColor var8 = this.customColor.getZenithColor();
         var4 = var8.getIntValue();
         var5 = var8.getIntValue2();
         var6 = var8.getIntValue3();
      }

      if (var2 != 1.0F) {
         var4 = Math.min(255, Math.round(var4 * var2));
         var5 = Math.min(255, Math.round(var5 * var2));
         var6 = Math.min(255, Math.round(var6 * var2));
      }

      return new ZenithColor(var4, var5, var6, var3);
   }

   private int method520() {
      for (int var1 = 0; var1 < 5; var1++) {
         if (this.animation.isSelected(var1)) {
            return var1;
         }
      }

      return 0;
   }
}

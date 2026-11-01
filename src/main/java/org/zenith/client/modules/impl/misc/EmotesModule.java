package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.client.option.Perspective;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.KeyEvent;
import org.zenith.MatrixUtils;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.events.MouseScrollEvent;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.screens.emote.EmoteFavoriteScreen;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.EmoteAccess;
import org.zenith.utility.misc.EmoteInfo;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "Emotes",
   category = ModuleCategory.MISC,
   description = "module.emotes.description"
)
public final class EmotesModule extends Module {
   public static final EmotesModule INSTANCE = new EmotesModule();
   private static final int CONST_intValue = 8;
   private static final int CONST_intValue2 = 15;
   private static final float CONST_floatValue = 54.0F;
   private static final float CONST_floatValue2 = 104.0F;
   private static final float CONST_floatValue3 = 2.4F;
   private final BindSetting wheelBind = new BindSetting("module.emotes.wheelBind", "module.emotes.wheelBind.desc", -1);
   private final BooleanSetting autoF5 = new BooleanSetting("module.emotes.autoF5", "module.emotes.autoF5.desc", false);
   private final SliderSetting autoF5Duration = new SliderSetting(
      "module.emotes.autoF5Duration", 5.0F, 1.0F, 30.0F, 0.5F, "module.emotes.autoF5Duration.desc", "s"
   );
   private final String[] texts = new String[120];
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private long longValue;
   private int intValue;
   private int hovered = -1;
   private final Animation animation = new Animation(280L, 0.0F, Easing.entry22);
   private final Animation[] animations = new Animation[8];

   private EmotesModule() {
      this.autoF5Duration.setVisible(this.autoF5::isEnabled);

      for (int var1 = 0; var1 < this.animations.length; var1++) {
         this.animations[var1] = new Animation(160L, 0.0F, Easing.INSTANCE18);
      }
   }

   @Override
   public void onDisable() {
      this.method6888(false);
      this.method6890();
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (this.flag3 && (!this.autoF5.isEnabled() || System.nanoTime() >= this.longValue)) {
         this.method6890();
      }
   }

   @EventTarget
   public void method6882(KeyEvent var1) {
      if (var1.isKey(this.wheelBind.getKeyCode()) && this.wheelBind.getKeyCode() != -1) {
         if (var1.getIntValue() == 1) {
            this.method6886();
         } else if (var1.getIntValue() == 0) {
            this.method6888(true);
         }
      }
   }

   @EventTarget
   public void method6883(MouseButtonEvent var1) {
      if (this.flag && var1.getIntValue2() == 1) {
         if (var1.getIntValue() == 0 || var1.getIntValue() == 1) {
            var1.setCancelled(true);
            int var2;
            if ((var2 = this.method6897(this.method6907(), this.method6908(), this.method6906())) != -1) {
               this.hovered = var2;
               int var7 = this.intValue * 8 + var2;
               EmoteInfo var4 = this.method6900(var2);
               if (var4 == null) {
                  this.method6902(var7);
               } else if (var1.getIntValue() == 1) {
                  this.method6904(var7);
               } else {
                  this.method6889(var4);
                  this.method6888(false);
               }
            } else {
               int var3;
               if ((var3 = this.method6898(this.method6907(), this.method6908(), this.method6906())) != 0 && var1.getIntValue() == 0) {
                  this.method6899(var3, true);
               } else if (var1.getIntValue() == 1 && mc.player != null) {
                  EmoteAccess.method7922(mc.player.getUuid());
               }
            }
         }
      }
   }

   @EventTarget
   public void method6884(MouseScrollEvent var1) {
      if (this.flag && var1.getDoubleValue2() != 0.0) {
         this.method6899(var1.getDoubleValue2() < 0.0 ? 1 : -1, false);
         var1.setCancelled(true);
      }
   }

   @EventTarget(4)
   public void method6885(ScreenRenderEvent var1) {
      if (mc.player != null && mc.world != null && mc.currentScreen == null) {
         if (this.flag) {
            this.method6909(true);
            EmotesModule.Data var2 = this.method6906();
            this.hovered = this.method6897(var1.getMenuDrawContext().getMouseX(), var1.getMenuDrawContext().getMouseY(), var2);
            this.method6891(var1.getMenuDrawContext(), var2);
         }
      } else {
         if (this.flag) {
            this.method6888(false);
         }
      }
   }

   private void method6886() {
      if (mc.player != null && mc.world != null && mc.currentScreen == null) {
         var var10000 = EmoteAccess.method7917();
         if (var10000 != null && !var10000.isEmpty()) {
            this.flag = true;
            this.hovered = -1;

            for (Animation var4 : this.animations) {
               var4.method2809(0.0F);
            }

            this.animation.method2809(0.0F);
            this.animation.method2808(1.0F);
            this.method6909(true);
            return;
         }
      }
   }

   private void method6887() {
      this.method6888(false);
   }

   private void method6888(boolean var1) {
      if (var1 && this.flag) {
         EmoteInfo var2 = this.method6900(this.hovered);
         if (var2 != null) {
            this.method6889(var2);
         }
      }

      this.flag = false;
      this.hovered = -1;
      this.method6909(false);
   }

   private void method6889(EmoteInfo var1) {
      EmoteAccess.method7920(var1.getText());
      if (this.autoF5.isEnabled() && mc.options.getPerspective().isFirstPerson()) {
         this.flag3 = true;
         mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
         this.longValue = System.nanoTime() + (long)(this.autoF5Duration.getValue3() * 1.0E9F);
      }
   }

   private void method6890() {
      if (this.flag3) {
         mc.options.setPerspective(Perspective.FIRST_PERSON);
         this.flag3 = false;
         this.longValue = 0L;
      }
   }

   private void method6891(MenuDrawContext var1, EmotesModule.Data var2) {
      ZenithStyle var3 = zenith.getStyleManager().getCurrentStyle();
      if (var3 != null) {
         float var4 = Math.clamp(this.animation.method2808(1.0F), 0.0F, 1.0F);
         float var5 = 0.82F + 0.18F * var4;
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(var2.floatValue, var2.floatValue2);
         var1.getMatrices().scale(var5, var5);
         var1.getMatrices().translate(-var2.floatValue, -var2.floatValue2);
         this.method6892(var1, var2, var4);
         this.method6893(var1, var2, var3, var4);
         this.method6894(var1, var2, var3, var4);
         this.method6895(var1, var2, var3, var4);
         var1.getMatrices().popMatrix();
         this.method6896(var1, var2, var3, var4);
      }
   }

   private void method6892(MenuDrawContext var1, EmotesModule.Data var2, float var3) {
      float var4;
      if (!((var4 = zenith.getNLMenuScreen().getBlurPower()) <= 0.0F)) {
         float var5 = var2.floatValue4 + 7.0F;
         RenderShaders.method5287(
            MatrixUtils.method664(var1.getMatrices()),
            var2.floatValue - var5,
            var2.floatValue2 - var5,
            var5 * 2.0F,
            var5 * 2.0F,
            var4,
            ProxyDotRadiusData.method8069(var5),
            ZenithColor.INSTANCE.method7294(var3 * 0.92F),
            true,
            false
         );
      }
   }

   private void method6893(MenuDrawContext var1, EmotesModule.Data var2, ZenithStyle var3, float var4) {
      float var5 = 45.0F;
      float var6 = 45.0F - 2.4F;
      ZenithColor var7 = var3.getSurfaceDisableBackground().getZenithColor().method7294(var4);
      ZenithColor var8 = var3.getRightBackground().getZenithColor().method7294(var4 * 0.92F);
      ZenithColor var9 = var3.getPrimaryColor().getZenithColor().method7294(var4);

      for (int var10 = 0; var10 < 8; var10++) {
         float var11 = this.animations[var10].method2808(this.hovered == var10 ? 1.0F : 0.0F);
         float var12 = var2.floatValue4 + var11 * 6.0F;
         ZenithColor var13 = var7.method7295(var3.getSurfaceEnableBackground().getZenithColor().method7294(var4), var11);
         ZenithColor var14 = var8.method7295(var9.method7296(0.48F), var11 * 0.86F);
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(var2.floatValue, var2.floatValue2);
         var1.getMatrices().rotate((float)Math.toRadians(var10 * var5 + 1.2F));
         var1.getMatrices().translate(-var2.floatValue, -var2.floatValue2);
         RenderShaders.method5276(
            MatrixUtils.method664(var1.getMatrices()),
            var2.floatValue - var12,
            var2.floatValue2 - var12,
            var12 * 2.0F,
            var12 * 2.0F,
            var12 - var2.floatValue3,
            var6,
            0.0F,
            var13,
            var14,
            var14,
            var13
         );
         if (var11 > 0.01F) {
            RenderShaders.method5275(
               MatrixUtils.method664(var1.getMatrices()),
               var2.floatValue - var12,
               var2.floatValue2 - var12,
               var12 * 2.0F,
               var12 * 2.0F,
               1.35F + var11,
               var6,
               0.0F,
               var9.method7294(var11 * 0.8F)
            );
         }

         var1.getMatrices().popMatrix();
      }
   }

   private void method6894(MenuDrawContext var1, EmotesModule.Data var2, ZenithStyle var3, float var4) {
      float var5 = (var2.floatValue3 + var2.floatValue4) / 2.0F;

      for (int var6 = 0; var6 < 8; var6++) {
         EmoteInfo var7 = this.method6900(var6);
         float var8 = this.animations[var6].getValue();
         float var9 = method6910(var6);
         float var10 = var5 + var8 * 4.0F;
         float var11 = var2.floatValue + (float)Math.cos(var9) * var10;
         float var12 = var2.floatValue2 + (float)Math.sin(var9) * var10;
         float var13 = 36.0F + var8 * 4.0F;
         ZenithColor var14 = var3.getPanelLeftBackground().getZenithColor().method7294(var4 * (0.72F + var8 * 0.22F));
         var1.drawRoundedRect(var11 - var13 / 2.0F, var12 - var13 / 2.0F, var13, var13, ProxyDotRadiusData.method8069(9.0F + var8 * 2.0F), var14);
         if (var7 != null) {
            float var15 = 31.0F + var8 * 4.0F;
            var1.drawTexture(var7.getIdentifier(), var11 - var15 / 2.0F, var12 - var15 / 2.0F, var15, var15, ZenithColor.INSTANCE.method7294(var4));
         } else {
            float var17 = 5.0F + var8 * 1.5F;
            ZenithColor var16 = var3.getTextTertiary().getZenithColor().method7294(var4 * (0.62F + var8 * 0.38F));
            var1.drawRoundedRect(var11 - var17, var12 - 0.75F, var17 * 2.0F, 1.5F, ProxyDotRadiusData.method8069(0.75F), var16);
            var1.drawRoundedRect(var11 - 0.75F, var12 - var17, 1.5F, var17 * 2.0F, ProxyDotRadiusData.method8069(0.75F), var16);
         }
      }
   }

   private void method6895(MenuDrawContext var1, EmotesModule.Data var2, ZenithStyle var3, float var4) {
      float var5 = 36.0F;
      ZenithColor var6 = var3.getLeftBackground().getZenithColor().method7294(var4 * 0.97F);
      ZenithColor var7 = var3.getTextEnable().getZenithColor().method7294(var4);
      ZenithColor var8 = var3.getTextSecondary().getZenithColor().method7294(var4);
      var1.drawRoundedRect(var2.floatValue - var5, var2.floatValue2 - var5, var5 * 2.0F, var5 * 2.0F, ProxyDotRadiusData.method8069(var5), var6);
      Font var9 = Fonts.NEW_SEMIBOLD.getFont(9.0F);
      String var10 = Integer.toString(this.intValue + 1);
      var1.drawText(var9, var10, var2.floatValue - var9.width(var10) / 2.0F, var2.floatValue2 - var9.height() / 2.0F - 3.0F, var7);
      Font var11 = Fonts.NEW_MEDIUM.getFont(4.8F);
      String var12 = "СТРАНИЦА";
      var1.drawText(var11, var12, var2.floatValue - var11.width(var12) / 2.0F, var2.floatValue2 + 9.0F, var8);
      Font var13 = Fonts.NEW_SEMIBOLD.getFont(7.0F);
      ZenithColor var14 = this.intValue > 0 ? var7 : var8.method7294(0.45F);
      ZenithColor var15 = this.intValue < 14 ? var7 : var8.method7294(0.45F);
      var1.drawText(var13, "‹", var2.floatValue - 24.0F - var13.width("‹") / 2.0F, var2.floatValue2 - var13.height() / 2.0F, var14);
      var1.drawText(var13, "›", var2.floatValue + 24.0F - var13.width("›") / 2.0F, var2.floatValue2 - var13.height() / 2.0F, var15);
   }

   private void method6896(MenuDrawContext var1, EmotesModule.Data var2, ZenithStyle var3, float var4) {
      EmoteInfo var5 = this.method6900(this.hovered);
      String var6 = this.hovered == -1 ? "Колесо эмоций" : (var5 == null ? "Пустой слот" : var5.getText2());
      String var7 = this.hovered == -1
         ? "Колесо мыши — сменить страницу"
         : (var5 == null ? "ЛКМ — выбрать эмоцию" : "Отпустите бинт — воспроизвести  •  ПКМ — удалить");
      Font var8 = Fonts.NEW_SEMIBOLD.getFont(7.5F);
      Font var9 = Fonts.NEW_MEDIUM.getFont(5.5F);
      float var10 = Math.max(var8.width(var6), var9.width(var7)) + 20.0F;
      float var11 = var2.floatValue - var10 / 2.0F;
      float var12 = var2.floatValue2 - var2.floatValue4 - 38.0F;
      ZenithColor var13 = var3.getLeftBackground().getZenithColor().method7294(var4 * 0.92F);
      float var14;
      if ((var14 = zenith.getNLMenuScreen().getBlurPower()) > 0.0F) {
         RenderShaders.method5287(
            MatrixUtils.method664(var1.getMatrices()),
            var11,
            var12,
            var10,
            27.0F,
            var14,
            ProxyDotRadiusData.method8069(7.0F),
            ZenithColor.INSTANCE.method7294(var4),
            true,
            false
         );
      }

      var1.drawRoundedRect(var11, var12, var10, 27.0F, ProxyDotRadiusData.method8069(7.0F), var13);
      var1.drawText(var8, var6, var2.floatValue - var8.width(var6) / 2.0F, var12 + 5.0F, var3.getTextEnable().getZenithColor().method7294(var4));
      var1.drawText(var9, var7, var2.floatValue - var9.width(var7) / 2.0F, var12 + 16.0F, var3.getTextSecondary().getZenithColor().method7294(var4));
   }

   private int method6897(float var1, float var2, EmotesModule.Data var3) {
      float var4 = var1 - var3.floatValue;
      float var5 = var2 - var3.floatValue2;
      double var6;
      if (!((var6 = Math.sqrt(var4 * var4 + var5 * var5)) < var3.floatValue3) && !(var6 > var3.floatValue4 + 7.0F)) {
         double var8;
         if ((var8 = Math.atan2(var5, var4) + (Math.PI / 2)) < 0.0) {
            var8 += Math.PI * 2;
         }

         return (int)Math.floor(var8 / (Math.PI * 2) * 8.0);
      } else {
         return -1;
      }
   }

   private int method6898(float var1, float var2, EmotesModule.Data var3) {
      if (var2 < var3.floatValue2 - 11.0F || var2 > var3.floatValue2 + 11.0F) {
         return 0;
      } else if (var1 >= var3.floatValue - 33.0F && var1 <= var3.floatValue - 15.0F) {
         return -1;
      } else {
         return var1 >= var3.floatValue + 15.0F && var1 <= var3.floatValue + 33.0F ? 1 : 0;
      }
   }

   private void method6899(int var1, boolean var2) {
      if (var1 != 0) {
         int var3 = this.intValue + Integer.signum(var1);
         this.intValue = var2 ? Math.floorMod(var3, 15) : Math.clamp((long)var3, 0, 14);
         this.hovered = -1;
      }
   }

   private EmoteInfo method6900(int var1) {
      if (var1 >= 0 && var1 < 8) {
         String var2 = this.texts[this.intValue * 8 + var1];
         return var2 == null ? null : EmoteAccess.method7918(var2).orElse(null);
      } else {
         return null;
      }
   }

   private int method6901(int var1) {
      return this.intValue * 8 + var1;
   }

   private void method6902(int var1) {
      this.method6887();
      mc.setScreen(new EmoteFavoriteScreen(this, var1));
   }

   public void method6903(int var1, String var2) {
      if (var1 >= 0 && var1 < this.texts.length && !EmoteAccess.method7916().stream().noneMatch(var2::equals)) {
         for (int var3 = 0; var3 < this.texts.length; var3++) {
            if (var2.equals(this.texts[var3])) {
               this.texts[var3] = null;
            }
         }

         this.texts[var1] = var2;
      }
   }

   public void method6904(int var1) {
      if (var1 >= 0 && var1 < this.texts.length) {
         this.texts[var1] = null;
      }
   }

   public String method6905(int var1) {
      return var1 >= 0 && var1 < this.texts.length ? this.texts[var1] : null;
   }

   @Override
   public JsonObject save() {
      JsonObject var1 = super.save();
      JsonArray var2 = new JsonArray();

      for (String var6 : this.texts) {
         var2.add((JsonElement)(var6 == null ? JsonNull.INSTANCE : new JsonPrimitive(var6)));
      }

      var1.add("FavoriteEmotes", var2);
      return var1;
   }

   @Override
   public void load(JsonObject var1) {
      super.load(var1);
      if (var1 != null && var1.has("FavoriteEmotes") && var1.get("FavoriteEmotes").isJsonArray()) {
         JsonArray var2 = var1.getAsJsonArray("FavoriteEmotes");
         Arrays.fill(this.texts, null);

         for (int var3 = 0; var3 < var2.size() && var3 < this.texts.length; var3++) {
            JsonElement var4 = var2.get(var3);
            if (var4.isJsonPrimitive() && var4.getAsJsonPrimitive().isString()) {
               String var5 = var4.getAsString();
               if (EmoteAccess.method7919(var5)) {
                  this.texts[var3] = var5;
               }
            }
         }
      }
   }

   private EmotesModule.Data method6906() {
      float var1 = Math.min(mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
      float var2 = Math.min(1.0F, var1 / 270.0F);
      float var3 = mc.getWindow().getScaledWidth() / 2.0F;
      float var4 = mc.getWindow().getScaledHeight() / 2.0F;
      return new EmotesModule.Data(var3, var4, 54.0F * var2, 104.0F * var2);
   }

   private float method6907() {
      return (float)(mc.mouse.getX() * mc.getWindow().getScaledWidth() / mc.getWindow().getWidth());
   }

   private float method6908() {
      return (float)(mc.mouse.getY() * mc.getWindow().getScaledHeight() / mc.getWindow().getHeight());
   }

   private void method6909(boolean var1) {
      if (mc.mouse != null) {
         if (var1 && !this.flag2) {
            mc.mouse.unlockCursor();
            this.flag2 = true;
         } else if (!var1 && this.flag2) {
            if (mc.currentScreen == null) {
               mc.mouse.lockCursor();
            }

            this.flag2 = false;
         }
      }
   }

   private static float method6910(int var0) {
      return (float)((-Math.PI / 2) + (Math.PI * 2) * ((var0 + 0.5) / 8.0));
   }

   private static final class Data {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + "]";
      }

      private Data(float var1, float var2, float var3, float var4) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
      }

      public float centerX() {
         return this.floatValue;
      }

      public float centerY() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }
   }
}

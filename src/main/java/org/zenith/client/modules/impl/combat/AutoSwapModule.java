package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.util.Comparator;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryOps;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.RotationAxis;
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
import org.zenith.client.events.ClickSlotEvent;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.render.KeyBindUtils;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "AutoSwap",
   category = ModuleCategory.COMBAT,
   description = "module.autoswap.description"
)
public final class AutoSwapModule extends Module {
   public static final AutoSwapModule INSTANCE = new AutoSwapModule();
   private final ModeSetting mode = new ModeSetting(
      "module.autoSwap.mode", "module.autoSwap.mode.desc", "module.autoSwap.mode.items", "module.autoSwap.mode.wheel"
   );
   private final ModeSetting itemType = new ModeSetting(
      "module.autoSwap.itemType",
      "module.autoSwap.itemType.desc",
      () -> this.mode.isSelected(0),
      "module.autoSwap.itemHead",
      "module.autoSwap.itemTotem",
      "module.autoSwap.itemGapple",
      "module.autoSwap.itemShield"
   );
   private final ModeSetting swapType = new ModeSetting(
      "module.autoSwap.swapType",
      "module.autoSwap.swapType.desc",
      () -> this.mode.isSelected(0),
      "module.autoSwap.swapHead",
      "module.autoSwap.swapTotem",
      "module.autoSwap.swapGapple",
      "module.autoSwap.swapShield"
   );
   private final BindSetting keyToSwap = new BindSetting("module.autoSwap.keyToSwap", "module.autoSwap.keyToSwap.desc", -1, () -> this.mode.isSelected(0));
   private final BindSetting keyToSwapCerb = new BindSetting(
      "module.autoSwap.keyToSwapCerb", "module.autoSwap.keyToSwapCerb.desc", -1, () -> this.mode.isSelected(0) && zenith.getServerDetector().method1642()
   );
   private final BindSetting wheelBind = new BindSetting("module.autoSwap.wheelBind", "module.autoSwap.wheelBind.desc", -1, () -> this.mode.isSelected(1));
   private final int intValue = 3;
   private final String text = "minecraft:air";
   private final String text2 = "item";
   private final String text3 = "stack";
   private final float floatValue = 60.0F;
   private final float floatValue2 = 94.0F;
   private final float floatValue3 = 0.35F;
   private final long longValue = 250L;
   private final AutoSwapModule.Entry2[] entry2s = new AutoSwapModule.Entry2[3];
   private boolean flag;
   private boolean flag2;
   private int intValue2 = -1;
   private int intValue3 = -1;
   private int intValue4 = -1;
   private long longValue2;
   private final Animation animation = new Animation(280L, Easing.entry22);
   private final Animation[] animations = new Animation[]{
      new Animation(160L, Easing.INSTANCE18), new Animation(160L, Easing.INSTANCE18), new Animation(160L, Easing.INSTANCE18)
   };

   private AutoSwapModule() {
      for (int var1 = 0; var1 < this.entry2s.length; var1++) {
         this.entry2s[var1] = new AutoSwapModule.Entry2();
      }
   }

   @Override
   public void onDisable() {
      this.method7732();
      super.onDisable();
   }

   @EventTarget
   public void method7682(KeyEvent var1) {
      if (mc.player != null && mc.player.currentScreenHandler instanceof PlayerScreenHandler) {
         if (this.mode.isSelected(1)) {
            this.method7686(var1);
         } else if (mc.currentScreen == null) {
            if (var1.getIntValue() == 1) {
               Slot var2 = this.method7711();
               if (var1.isPress(this.keyToSwap.getKeyCode())) {
                  Slot var3 = InventoryUtils.method913(
                     this.method7712(this.itemType),
                     Comparator.comparing(var0 -> var0.getStack().hasEnchantments()),
                     var1x -> var1x != var2 && var1x.id != 46 && var1x.id != 45
                  );
                  Slot var4 = InventoryUtils.method913(
                     this.method7712(this.swapType),
                     Comparator.comparing(var0 -> var0.getStack().hasEnchantments()),
                     var1x -> var1x != var2 && var1x.id != 46 && var1x.id != 45
                  );
                  Slot var5 = var3 != null && mc.player.getOffHandStack().getItem() != var3.getStack().getItem() ? var3 : var4;
                  this.method7710(var5);
               }

               if (var1.isPress(this.keyToSwapCerb.getKeyCode()) && var2 != null) {
                  this.method7710(var2);
               }
            }
         }
      }
   }

   @EventTarget(4)
   public void method7683(ScreenRenderEvent var1) {
      if (mc.player != null && mc.world != null) {
         if (this.mode.isSelected(1)) {
            if (mc.currentScreen == null) {
               if (this.intValue2 != -1) {
                  this.method7732();
               } else {
                  this.method7693(var1);
               }
            }
         } else {
            if (this.flag || this.flag2) {
               this.method7732();
            }
         }
      }
   }

   @EventTarget
   public void method7684(MouseButtonEvent var1) {
      if (this.mode.isSelected(1) && this.flag && mc.player != null && mc.world != null && mc.currentScreen == null) {
         if (var1.getIntValue2() == 1) {
            float var2 = this.method7729();
            float var3 = this.method7730();
            float var4 = mc.getWindow().getScaledWidth() / 2.0F;
            float var5 = mc.getWindow().getScaledHeight() / 2.0F;
            if (var1.getIntValue() == 1 && this.method7727(var2, var3, var4, var5)) {
               this.method7690();
            } else {
               int var6;
               if ((var6 = this.method7726(var2, var3, var4, var5, 60.0F)) != -1) {
                  this.method7688(var1.getIntValue(), var6);
               }
            }
         }
      }
   }

   @EventTarget
   public void method7685(ClickSlotEvent var1) {
      if (this.mode.isSelected(1) && this.intValue2 != -1 && mc.player != null) {
         if (mc.currentScreen instanceof InventoryScreen) {
            if (var1.getSlotActionType() == SlotActionType.PICKUP) {
               this.method7692(var1);
            }
         }
      }
   }

   private void method7686(KeyEvent var1) {
      if (var1.isKey(this.wheelBind.getKeyCode())) {
         if (var1.getIntValue() == 1) {
            this.method7687();
         } else if (var1.getIntValue() == 0) {
            if (this.intValue2 != -1 && mc.currentScreen instanceof InventoryScreen) {
               return;
            }

            this.method7732();
         }
      }
   }

   private void method7687() {
      if (mc.currentScreen == null) {
         this.flag = true;
         this.method7731(true);
      }
   }

   private void method7688(int var1, int var2) {
      if (var1 == 1) {
         this.method7689(var2);
      } else {
         if (var1 == 0) {
            this.method7691(var2);
         }
      }
   }

   private void method7689(int var1) {
      AutoSwapModule.Entry2 var2 = this.method7723(var1);
      if (var2 != null) {
         var2.clear();
         this.intValue3 = var1;
         this.longValue2 = System.currentTimeMillis() + 250L;
      }
   }

   private void method7690() {
      for (AutoSwapModule.Entry2 var4 : this.entry2s) {
         var4.clear();
      }

      this.intValue3 = -1;
      this.longValue2 = System.currentTimeMillis() + 250L;
   }

   private void method7691(int var1) {
      this.intValue2 = var1;
      mc.setScreen(new InventoryScreen(mc.player));
   }

   private void method7692(ClickSlotEvent var1) {
      DefaultedList var2 = mc.player.currentScreenHandler.slots;
      if (var1.getIntValue2() >= 0 && var1.getIntValue2() < var2.size()) {
         ItemStack var3 = ((Slot)var2.get(var1.getIntValue2())).getStack();
         if (!var3.isEmpty() && var3.getItem() != Items.AIR) {
            AutoSwapModule.Entry2 var4 = this.method7723(this.intValue2);
            if (var4 != null) {
               var4.method7733(var3);
               var1.setCancelled(true);
               mc.setScreen(null);
               this.method7732();
            }
         }
      }
   }

   private void method7693(ScreenRenderEvent var1) {
      float var2;
      if ((var2 = this.animation.method2808(this.flag ? 1.0F : 0.0F)) <= 0.005F && !this.flag) {
         for (Animation var6 : this.animations) {
            var6.method2809(0.0F);
         }
      } else {
         if (this.flag) {
            this.method7731(true);
         }

         AutoSwapModule.Entry var3 = this.method7694(var1.getMenuDrawContext(), var2);
         if (var3 != null) {
            if (this.flag && var3.floatValue6 > 0.4F) {
               this.method7713(var3.intValue);
            }

            this.method7695(var3);
            this.method7696(var3);
            this.method7702(var3);
            this.method7706(var3);
            this.method7708(var3);
         }
      }
   }

   private AutoSwapModule.Entry method7694(MenuDrawContext var1, float var2) {
      float var3 = mc.getWindow().getScaledWidth() / 2.0F;
      float var4 = mc.getWindow().getScaledHeight() / 2.0F;
      float var5 = 0.85F + 0.15F * var2;
      float var6 = 60.0F * var5;
      float var7 = 94.0F * var5;
      int var8 = this.flag ? this.method7726(var1.getMouseX(), var1.getMouseY(), var3, var4, var6) : -1;
      boolean var9 = this.flag && this.method7727(var1.getMouseX(), var1.getMouseY(), var3, var4);
      ZenithStyle var10 = zenith.getStyleManager().getCurrentStyle();
      return var10 == null
         ? null
         : new AutoSwapModule.Entry(var1, MatrixUtils.method666(var1.getMatrices()), var3, var4, var6, var7, var5, var2, var8, var9, var10);
   }

   private void method7695(AutoSwapModule.Entry var1) {
      float var2;
      if (!((var2 = zenith.getNLMenuScreen().getBlurPower()) <= 0.0F)) {
         float var3 = var1.floatValue4 + 7.0F;
         RenderShaders.method5287(
            var1.matrixStack.peek().getPositionMatrix(),
            var1.floatValue - var3,
            var1.floatValue2 - var3,
            var3 * 2.0F,
            var3 * 2.0F,
            var2,
            ProxyDotRadiusData.method8069(var3),
            ZenithColor.INSTANCE.method7294(var1.floatValue6 * 0.92F),
            true,
            false
         );
      }
   }

   private void method7696(AutoSwapModule.Entry var1) {
      float var2 = this.method7697();
      float var3 = this.method7698();
      float var4 = var2 - var3;
      boolean var5 = this.intValue3 != -1 && System.currentTimeMillis() <= this.longValue2;

      for (int var6 = 0; var6 < 3; var6++) {
         float var7 = this.animations[var6].method2808(var6 == var1.intValue ? 1.0F : 0.0F);
         boolean var8 = this.method7714(var6);
         AutoSwapModule.Entry3 var9 = this.method7699(var1, var7, var5 && var6 == this.intValue3, var8);
         float var10 = var1.floatValue4 + var7 * 6.0F;
         this.method7700(var1, var6 * var2 + var3 * 0.5F);
         this.method7701(var1, var10, var4, var9.zenithColor, var9.zenithColor2);
         if (var7 > 0.01F) {
            RenderShaders.method5275(
               var1.matrixStack.peek().getPositionMatrix(),
               var1.floatValue - var10,
               var1.floatValue2 - var10,
               var10 * 2.0F,
               var10 * 2.0F,
               1.35F + var7,
               var4,
               0.0F,
               var1.zenithStyle.getPrimaryColor().getZenithColor().method7294(var1.floatValue6 * var7 * 0.8F)
            );
         }

         var1.matrixStack.pop();
      }
   }

   private float method7697() {
      return 120.0F;
   }

   private float method7698() {
      return 2.4F;
   }

   private AutoSwapModule.Entry3 method7699(AutoSwapModule.Entry var1, float var2, boolean var3, boolean var4) {
      if (var3) {
         return new AutoSwapModule.Entry3(
            new ZenithColor(255, 90, 90, (int)(220.0F * var1.floatValue6)), new ZenithColor(210, 40, 40, (int)(170.0F * var1.floatValue6))
         );
      } else if (var4) {
         return new AutoSwapModule.Entry3(
            var1.zenithStyle
               .getDisableActiveBg()
               .getZenithColor()
               .method7294(var1.floatValue6)
               .method7295(var1.zenithStyle.getPrimaryColor().getZenithColor().method7294(var1.floatValue6), var2 * 0.45F),
            var1.zenithStyle
               .getRightBackground()
               .getZenithColor()
               .method7294(var1.floatValue6 * 0.9F)
               .method7295(var1.zenithStyle.getPrimaryColor().getZenithColor().method7296(0.55F).method7294(var1.floatValue6), var2 * 0.4F)
         );
      } else {
         ZenithColor var5 = var1.zenithStyle.getSurfaceDisableBackground().getZenithColor().method7294(var1.floatValue6);
         ZenithColor var6 = var1.zenithStyle.getRightBackground().getZenithColor().method7294(var1.floatValue6 * 0.92F);
         if (var2 <= 0.001F) {
            return new AutoSwapModule.Entry3(var5, var6);
         } else {
            ZenithColor var7 = var1.zenithStyle
               .getSurfaceEnableBackground()
               .getZenithColor()
               .method7294(var1.floatValue6)
               .method7295(var1.zenithStyle.getPrimaryColor().getZenithColor().method7294(var1.floatValue6), var2 * 0.65F);
            ZenithColor var8 = var1.zenithStyle.getPrimaryColor().getZenithColor().method7296(0.48F).method7294(var1.floatValue6);
            return new AutoSwapModule.Entry3(var5.method7295(var7, var2), var6.method7295(var8, var2));
         }
      }
   }

   private void method7700(AutoSwapModule.Entry var1, float var2) {
      var1.matrixStack.push();
      var1.matrixStack.translate(var1.floatValue, var1.floatValue2, 0.0F);
      var1.matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var2));
      var1.matrixStack.translate(-var1.floatValue, -var1.floatValue2, 0.0F);
   }

   private void method7701(AutoSwapModule.Entry var1, float var2, float var3, ZenithColor var4, ZenithColor var5) {
      RenderShaders.method5276(
         var1.matrixStack.peek().getPositionMatrix(),
         var1.floatValue - var2,
         var1.floatValue2 - var2,
         var2 * 2.0F,
         var2 * 2.0F,
         var2 - var1.floatValue3,
         var3,
         0.0F,
         var4,
         var5,
         var5,
         var4
      );
   }

   private void method7702(AutoSwapModule.Entry var1) {
      for (int var2 = 0; var2 < 3; var2++) {
         this.method7703(var1, var2, false);
      }

      ScissorFlusher.flush();

      for (int var3 = 0; var3 < 3; var3++) {
         this.method7703(var1, var3, true);
      }
   }

   private void method7703(AutoSwapModule.Entry var1, int var2, boolean var3) {
      ItemStack var4 = this.method7717(var2);
      float var5 = this.animations[var2].getValue();
      float var6 = this.method7728(var2);
      float var7 = (var1.floatValue3 + var1.floatValue4) / 2.0F + var5 * 4.0F;
      float var8 = var1.floatValue + (float)Math.cos(var6) * var7;
      float var9 = var1.floatValue2 + (float)Math.sin(var6) * var7;
      if (!var4.isEmpty() && var4.getItem() != Items.AIR) {
         if (var3) {
            this.method7705(var1, var4, var8, var9);
         } else {
            var1.menuDrawContext.drawItem(var4, (int)(var8 - 8.0F), (int)(var9 - 8.0F));
         }
      } else {
         if (!var3) {
            this.method7704(var1, var8, var9, var5);
         }
      }
   }

   private void method7704(AutoSwapModule.Entry var1, float var2, float var3, float var4) {
      ZenithColor var5 = var1.zenithStyle.getTextTertiary().getZenithColor().method7294(var1.floatValue6 * (0.62F + var4 * 0.38F));
      var1.menuDrawContext.drawRoundedRect(var2 - 5.0F, var3 - 0.7F, 10.0F, 1.4F, ProxyDotRadiusData.method8069(0.7F), var5);
      var1.menuDrawContext.drawRoundedRect(var2 - 0.7F, var3 - 5.0F, 1.4F, 10.0F, ProxyDotRadiusData.method8069(0.7F), var5);
   }

   private void method7705(AutoSwapModule.Entry var1, ItemStack var2, float var3, float var4) {
      int var5;
      if ((var5 = this.method7716(var2)) > 1) {
         Font var6 = Fonts.NEW_SEMIBOLD.getFont(6.0F);
         String var7 = String.valueOf(var5);
         float var8 = var3 + 8.0F - var6.width(var7);
         float var9 = var4 + 5.5F;
         var1.menuDrawContext
            .drawText(var6, var7, var8 + 0.5F, var9 + 0.5F, var1.zenithStyle.getRightBackground().getZenithColor().method7294(var1.floatValue6));
         var1.menuDrawContext.drawText(var6, var7, var8, var9, var1.zenithStyle.getTextEnable().getZenithColor().method7294(var1.floatValue6));
      }
   }

   private void method7706(AutoSwapModule.Entry var1) {
      this.method7707(var1);
      if (mc.player != null) {
         ItemStack var2 = mc.player.getOffHandStack();
         if (!var2.isEmpty() && var2.getItem() != Items.AIR) {
            var1.menuDrawContext.drawItem(var2, (int)(var1.floatValue - 8.0F), (int)(var1.floatValue2 - 8.0F));
         }
      }
   }

   private void method7707(AutoSwapModule.Entry var1) {
      float var2 = 32.0F * var1.floatValue5;
      var1.menuDrawContext
         .drawRoundedRect(
            var1.floatValue - var2,
            var1.floatValue2 - var2,
            var2 * 2.0F,
            var2 * 2.0F,
            ProxyDotRadiusData.method8069(var2),
            var1.zenithStyle.getLeftBackground().getZenithColor().method7294(var1.floatValue6 * 0.97F)
         );
      if (var1.flag) {
         var1.menuDrawContext
            .drawRoundedBorder(
               var1.floatValue - var2 - 1.0F,
               var1.floatValue2 - var2 - 1.0F,
               (var2 + 1.0F) * 2.0F,
               (var2 + 1.0F) * 2.0F,
               1.2F,
               ProxyDotRadiusData.method8069(var2 + 1.0F),
               var1.zenithStyle.getPrimaryColor().getZenithColor().method7294(var1.floatValue6 * 0.75F)
            );
      }
   }

   private void method7708(AutoSwapModule.Entry var1) {
      if (!(var1.floatValue6 <= 0.5F)) {
         String var2;
         String var3;
         if (var1.flag) {
            var2 = "Вторая рука";
            var3 = "ПКМ очистить всё";
         } else if (var1.intValue != -1) {
            ItemStack var4 = this.method7717(var1.intValue);
            boolean var5;
            var2 = (var5 = var4.isEmpty() || var4.getItem() == Items.AIR) ? "Пустой слот" : var4.getName().getString();
            var3 = this.method7709(var1.intValue, var5);
         } else {
            var2 = "AutoSwap";
            var3 = "Наведитесь на нужный предмет";
         }

         Font var10 = Fonts.NEW_SEMIBOLD.getFont(7.5F);
         Font var11 = Fonts.NEW_MEDIUM.getFont(5.5F);
         float var6 = Math.max(var10.width(var2), var11.width(var3)) + 20.0F;
         float var7 = var1.floatValue - var6 / 2.0F;
         float var8 = var1.floatValue2 - var1.floatValue4 - 38.0F;
         float var9;
         if ((var9 = zenith.getNLMenuScreen().getBlurPower()) > 0.0F) {
            RenderShaders.method5287(
               var1.matrixStack.peek().getPositionMatrix(),
               var7,
               var8,
               var6,
               27.0F,
               var9,
               ProxyDotRadiusData.method8069(7.0F),
               ZenithColor.INSTANCE.method7294(var1.floatValue6),
               true,
               false
            );
         }

         var1.menuDrawContext
            .drawRoundedRect(
               var7,
               var8,
               var6,
               27.0F,
               ProxyDotRadiusData.method8069(7.0F),
               var1.zenithStyle.getLeftBackground().getZenithColor().method7294(var1.floatValue6 * 0.92F)
            );
         var1.menuDrawContext
            .drawText(
               var10,
               var2,
               var1.floatValue - var10.width(var2) / 2.0F,
               var8 + 5.0F,
               var1.zenithStyle.getTextEnable().getZenithColor().method7294(var1.floatValue6)
            );
         var1.menuDrawContext
            .drawText(
               var11,
               var3,
               var1.floatValue - var11.width(var3) / 2.0F,
               var8 + 16.0F,
               var1.zenithStyle.getTextSecondary().getZenithColor().method7294(var1.floatValue6)
            );
      }
   }

   private String method7709(int var1, boolean var2) {
      if (var2) {
         return "ЛКМ привязать";
      } else {
         return this.method7714(var1) ? "Нет в инвентаре" : "ПКМ очистить";
      }
   }

   private void method7710(Slot var1) {
      if (var1 != null) {
         if (KeyBindUtils.method2630(AutoTotemModule.class) && KeyBindUtils.method2630(AutoSwapModule.class)) {
            KeyBindUtils.method2627(AutoSwapModule.class, () -> {
               if (KeyBindUtils.method2630(AutoTotemModule.class)) {
                  InventoryUtils.method900(var1, Hand.OFF_HAND, true);
               }
            });
            OffHandManagerModule.INSTANCE.reset();
         }
      }
   }

   private Slot method7711() {
      return this.keyToSwapCerb.getKeyCode() == -1
         ? null
         : InventoryUtils.method907(
            Items.PLAYER_HEAD,
            var0 -> {
               if (var0.getStack().isEmpty()) {
                  return false;
               } else {
                  NbtComponent var1 = var0.getStack().get(DataComponentTypes.CUSTOM_DATA);
                  return var1 != null
                     && var1.copyNbt().contains("SkullOwner")
                     && var1.copyNbt()
                        .get("SkullOwner")
                        .toString()
                        .contains(
                           "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA5NWE3ZmQ5MGRhYTFiYmU3MDY5MDg5NzQwZTA1ZDBiZmM2NjI5NmVlM2M0MGVlNzFhNGUwYTY2MTZiMmJiYyJ9fX0="
                        );
               }
            }
         );
   }

   private Item method7712(ModeSetting var1) {
      return switch (var1.getIndex()) {
         case 0 -> Items.PLAYER_HEAD;
         case 1 -> Items.TOTEM_OF_UNDYING;
         case 2 -> Items.GOLDEN_APPLE;
         case 3 -> Items.SHIELD;
         default -> Items.AIR;
      };
   }

   private void method7713(int var1) {
      if (var1 == -1) {
         this.intValue4 = -1;
      } else if (var1 != this.intValue4) {
         ItemStack var2 = this.method7717(var1);
         if (var2.isEmpty() || var2.getItem() == Items.AIR) {
            this.intValue4 = -1;
         } else if (this.method7721(mc.player.getOffHandStack(), var2)) {
            this.intValue4 = var1;
         } else {
            Slot var3 = this.method7715(var2);
            if (var3 == null) {
               this.intValue4 = var1;
            } else {
               this.method7710(var3);
               this.intValue4 = var1;
            }
         }
      }
   }

   private boolean method7714(int var1) {
      ItemStack var2 = this.method7717(var1);
      return !this.method7718(var2) && this.method7715(var2) == null && !this.method7722(var2);
   }

   private Slot method7715(ItemStack var1) {
      return this.method7718(var1)
         ? null
         : InventoryUtils.method913(
            var1.getItem(),
            Comparator.comparing(var0 -> var0.getStack().hasEnchantments()),
            var2 -> var2.id != 46 && var2.id != 45 && this.method7721(var2.getStack(), var1)
         );
   }

   private int method7716(ItemStack var1) {
      if (!this.method7718(var1) && mc.player != null && mc.player.currentScreenHandler != null) {
         int var2 = 0;

         for (Slot var4 : mc.player.currentScreenHandler.slots) {
            if (var4.id != 46) {
               ItemStack var5 = var4.getStack();
               if (this.method7721(var5, var1)) {
                  var2 += var5.getCount();
               }
            }
         }

         return var2;
      } else {
         return 0;
      }
   }

   private ItemStack method7717(int var1) {
      AutoSwapModule.Entry2 var2 = this.method7723(var1);
      return var2 == null ? ItemStack.EMPTY : var2.method7734();
   }

   private boolean method7718(ItemStack var1) {
      return var1 == null || var1.isEmpty() || var1.getItem() == Items.AIR;
   }

   private ItemStack method7719(ItemStack var1) {
      ItemStack var2 = var1.copy();
      var2.setCount(1);
      return var2;
   }

   private ItemStack method7720(String var1) {
      if (var1 != null && !var1.isBlank()) {
         Identifier var2 = Identifier.tryParse(var1);
         if (var2 != null && Registries.ITEM.containsId(var2)) {
            Item var3 = Registries.ITEM.get(var2);
            return var3 == Items.AIR ? ItemStack.EMPTY : var3.getDefaultStack();
         } else {
            return ItemStack.EMPTY;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   private boolean method7721(ItemStack var1, ItemStack var2) {
      if (!var1.isEmpty() && !var2.isEmpty()) {
         return var2.getItem() != Items.PLAYER_HEAD ? var1.getItem() == var2.getItem() : ItemStack.areItemsAndComponentsEqual(var1, var2);
      } else {
         return false;
      }
   }

   private boolean method7722(ItemStack var1) {
      return this.method7721(mc.player.getOffHandStack(), var1);
   }

   private AutoSwapModule.Entry2 method7723(int var1) {
      return var1 >= 0 && var1 < this.entry2s.length ? this.entry2s[var1] : null;
   }

   private String method7724(ItemStack var1) {
      if (var1 != null && !var1.isEmpty() && mc.world != null) {
         try {
            RegistryOps var2 = mc.world.getRegistryManager().getOps(JsonOps.INSTANCE);
            DataResult var3 = ItemStack.CODEC.encodeStart(var2, var1);
            return (String)var3.result().map(e -> String.valueOf(e)).orElse(null);
         } catch (Exception var4) {
            return null;
         }
      } else {
         return null;
      }
   }

   private ItemStack method7725(String var1) {
      if (var1 != null && !var1.isBlank() && mc.world != null) {
         try {
            JsonElement var2 = JsonParser.parseString(var1);
            RegistryOps var3 = mc.world.getRegistryManager().getOps(JsonOps.INSTANCE);
            DataResult var4 = ItemStack.CODEC.decode(var3, var2);
            if (var4.result().isEmpty()) {
               return ItemStack.EMPTY;
            } else {
               ItemStack var5 = (ItemStack)((Pair)var4.result().get()).getFirst();
               if (var5.isEmpty()) {
                  return ItemStack.EMPTY;
               } else {
                  var5.setCount(1);
                  return var5;
               }
            }
         } catch (Exception var6) {
            return ItemStack.EMPTY;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   @Override
   public JsonObject save() {
      JsonObject var1 = super.save();
      JsonArray var2 = new JsonArray();

      for (AutoSwapModule.Entry2 var6 : this.entry2s) {
         var2.add(var6.save());
      }

      var1.add("WheelItems", var2);
      return var1;
   }

   @Override
   public void load(JsonObject var1) {
      super.load(var1);
      if (var1 != null && var1.has("WheelItems") && var1.get("WheelItems").isJsonArray()) {
         for (AutoSwapModule.Entry2 var5 : this.entry2s) {
            var5.clear();
         }

         JsonArray var6 = var1.getAsJsonArray("WheelItems");

         for (int var7 = 0; var7 < this.entry2s.length && var7 < var6.size(); var7++) {
            this.entry2s[var7].method7735(var6.get(var7));
         }
      }
   }

   private int method7726(float var1, float var2, float var3, float var4, float var5) {
      float var6 = var1 - var3;
      float var7 = var2 - var4;
      if ((float)Math.sqrt(var6 * var6 + var7 * var7) < var5 * 0.35F) {
         return -1;
      } else {
         double var9;
         if ((var9 = Math.atan2(var7, var6) + (Math.PI / 2)) < 0.0) {
            var9 += Math.PI * 2;
         }

         int var11;
         return (var11 = (int)Math.floor(var9 / (Math.PI * 2) * 3.0)) >= 0 && var11 < 3 ? var11 : -1;
      }
   }

   private boolean method7727(float var1, float var2, float var3, float var4) {
      float var5 = var1 - var3;
      float var6 = var2 - var4;
      return Math.sqrt(var5 * var5 + var6 * var6) <= 21.0;
   }

   private float method7728(int var1) {
      return (float)((-Math.PI / 2) + (Math.PI * 2) * ((var1 + 0.5) / 3.0));
   }

   private float method7729() {
      return (float)(mc.mouse.getX() * mc.getWindow().getScaledWidth() / mc.getWindow().getWidth());
   }

   private float method7730() {
      return (float)(mc.mouse.getY() * mc.getWindow().getScaledHeight() / mc.getWindow().getHeight());
   }

   private void method7731(boolean var1) {
      if (mc != null && mc.mouse != null) {
         if (var1) {
            if (!this.flag2) {
               mc.mouse.unlockCursor();
               this.flag2 = true;
            }
         } else {
            if (this.flag2) {
               if (mc.currentScreen == null) {
                  mc.mouse.lockCursor();
               }

               this.flag2 = false;
            }
         }
      }
   }

   private void method7732() {
      this.flag = false;
      this.intValue2 = -1;
      this.intValue3 = -1;
      this.intValue4 = -1;
      this.longValue2 = 0L;
      this.method7731(false);
   }

   private static final class Entry {
      private final MenuDrawContext menuDrawContext;
      private final MatrixStack matrixStack;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final int intValue;
      private final boolean flag;
      private final ZenithStyle zenithStyle;

      private Entry(
         MenuDrawContext var1,
         MatrixStack var2,
         float var3,
         float var4,
         float var5,
         float var6,
         float var7,
         float var8,
         int var9,
         boolean var10,
         ZenithStyle var11
      ) {
         this.menuDrawContext = var1;
         this.matrixStack = var2;
         this.floatValue = var3;
         this.floatValue2 = var4;
         this.floatValue3 = var5;
         this.floatValue4 = var6;
         this.floatValue5 = var7;
         this.floatValue6 = var8;
         this.intValue = var9;
         this.flag = var10;
         this.zenithStyle = var11;
      }
   }

   private final class Entry2 {
      private String text = "minecraft:air";
      private String text2;
      private ItemStack itemStack;

      private void clear() {
         this.text = "minecraft:air";
         this.text2 = null;
         this.itemStack = null;
      }

      private void method7733(ItemStack var1) {
         if (AutoSwapModule.this.method7718(var1)) {
            this.clear();
         } else {
            ItemStack var2 = AutoSwapModule.this.method7719(var1);
            this.text = Registries.ITEM.getId(var2.getItem()).toString();
            this.text2 = AutoSwapModule.this.method7724(var2);
            this.itemStack = var2;
         }
      }

      private ItemStack method7734() {
         if (this.itemStack != null) {
            return this.itemStack;
         } else {
            ItemStack var1 = AutoSwapModule.this.method7725(this.text2);
            if (var1.isEmpty()) {
               var1 = AutoSwapModule.this.method7720(this.text);
            }

            this.itemStack = var1;
            return var1;
         }
      }

      private JsonObject save() {
         JsonObject var1 = new JsonObject();
         var1.addProperty("item", this.text == null ? "minecraft:air" : this.text);
         String var2 = this.text2;
         if ((var2 == null || var2.isBlank()) && this.itemStack != null && !this.itemStack.isEmpty()) {
            var2 = AutoSwapModule.this.method7724(this.itemStack);
         }

         if (var2 != null && !var2.isBlank()) {
            var1.addProperty("stack", var2);
         }

         return var1;
      }

      private void method7735(JsonElement var1) {
         this.clear();
         if (var1 != null) {
            if (var1.isJsonPrimitive()) {
               this.text = var1.getAsString();
            } else if (var1.isJsonObject()) {
               JsonObject var2 = var1.getAsJsonObject();
               this.text = var2.has("item") ? var2.get("item").getAsString() : "minecraft:air";
               this.text2 = var2.has("stack") ? var2.get("stack").getAsString() : null;
               ItemStack var3 = AutoSwapModule.this.method7725(this.text2);
               if (!var3.isEmpty()) {
                  this.itemStack = var3;
                  this.text = Registries.ITEM.getId(var3.getItem()).toString();
               }
            }
         }
      }
   }

   private static final class Entry3 {
      private final ZenithColor zenithColor;
      private final ZenithColor zenithColor2;

      private Entry3(ZenithColor var1, ZenithColor var2) {
         this.zenithColor = var1;
         this.zenithColor2 = var2;
      }
   }
}

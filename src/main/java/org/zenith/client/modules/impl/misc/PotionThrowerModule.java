package org.zenith.client.modules.impl.misc;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.KeyEvent;
import org.zenith.MatrixUtils;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.events.ScreenRenderEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "PotionThrower",
   category = ModuleCategory.MISC,
   description = "module.potionThrower.description"
)
public final class PotionThrowerModule extends Module {
   private static final PotionThrowerModule.Type[] types = PotionThrowerModule.Type.values();
   public static final PotionThrowerModule INSTANCE = new PotionThrowerModule();
   private static final float CONST_floatValue = 62.0F;
   private static final float CONST_floatValue2 = 98.0F;
   private static final float CONST_floatValue3 = 0.35F;
   private static final float CONST_floatValue4 = 2.4F;
   private final ModeSetting mode = new ModeSetting(
      "module.potionThrower.mode", "module.potionThrower.mode.desc", "module.potionThrower.modeWheel", "module.potionThrower.modeKeys"
   );
   private final BindSetting wheelBind = new BindSetting(
      "module.potionThrower.wheelBind", "module.potionThrower.wheelBind.desc", -1, () -> this.mode.isSelected(0)
   );
   private final BooleanSetting drawRadius = new BooleanSetting("module.potionThrower.drawRadius", "module.potionThrower.drawRadius.desc", true);
   private final List<PotionThrowerModule.Entry> items = new ArrayList<>();
   private final Animation animation = new Animation(280L, Easing.entry22);
   private final Animation[] animations = new Animation[types.length];
   private final Slot[] slots = new Slot[types.length];
   private final int[] intValues = new int[types.length];
   private boolean flag;
   private boolean flag2;
   private int intValue = -1;

   private PotionThrowerModule() {
      for (PotionThrowerModule.Type var4 : types) {
         this.items.add(new PotionThrowerModule.Entry(var4, new BindSetting(var4.text, var4.text + ".desc", -1, () -> this.mode.isSelected(1))));
      }

      for (int var5 = 0; var5 < this.animations.length; var5++) {
         this.animations[var5] = new Animation(160L, Easing.INSTANCE18);
      }
   }

   @Override
   public List<Setting> getSettings() {
      var var1 = new ArrayList<>(List.of(this.mode, this.wheelBind, this.drawRadius));
      this.items.forEach(var1x -> var1.add(var1x.bindSetting));
      return var1;
   }

   @Override
   public void onDisable() {
      this.method2695();
      this.items.forEach(var0 -> var0.flag = false);
      super.onDisable();
   }

   @EventTarget
   public void method2677(KeyEvent var1) {
      if (mc.player != null && mc.player.currentScreenHandler instanceof PlayerScreenHandler) {
         if (this.mode.isSelected(0)) {
            this.method2678(var1);
         } else if (mc.currentScreen == null) {
            for (PotionThrowerModule.Entry var3 : this.items) {
               if (var3.bindSetting.getKeyCode() != -1) {
                  if (var1.isPress(var3.bindSetting.getKeyCode())) {
                     var3.flag = true;
                  }

                  if (var1.isRelease(var3.bindSetting.getKeyCode())) {
                     var3.flag = false;
                     this.method2680(var3.type);
                  }
               }
            }
         }
      }
   }

   private void method2678(KeyEvent var1) {
      if (this.wheelBind.getKeyCode() != -1 && var1.isKey(this.wheelBind.getKeyCode())) {
         if (var1.getIntValue() == 1) {
            if (mc.currentScreen == null) {
               this.flag = true;
               this.method2694(true);
            }
         } else {
            if (var1.getIntValue() == 0) {
               int var2 = this.intValue;
               this.method2695();
               if (var2 != -1) {
                  this.method2680(types[var2]);
               }
            }
         }
      }
   }

   @EventTarget
   public void method2679(MouseButtonEvent var1) {
      if (this.mode.isSelected(0) && this.flag && mc.player != null && mc.currentScreen == null) {
         if (var1.getIntValue2() == 1 && var1.getIntValue() == 0) {
            int var2;
            if ((
                  var2 = this.method2691(
                     this.method2692(), this.method2693(), mc.getWindow().getScaledWidth() / 2.0F, mc.getWindow().getScaledHeight() / 2.0F, 62.0F
                  )
               )
               != -1) {
               this.method2695();
               this.method2680(types[var2]);
            }
         }
      }
   }

   private void method2680(PotionThrowerModule.Type var1) {
      if (mc.player != null && this.method2681(var1.floatValue)) {
         InventoryUtils.method928(Items.SPLASH_POTION, var1.method2700());
      }
   }

   private boolean method2681(float var1) {
      return var1 == 0.0F
         ? true
         : mc.world != null
            && mc.world
               .getPlayers()
               .stream()
               .anyMatch(
                  var1x -> var1x != mc.player
                     && !Zenith.getInstance().getFriends().method7627(var1x.getGameProfile().name())
                     && mc.player.distanceTo(var1x) <= var1
               );
   }

   @EventTarget(4)
   public void method2682(ScreenRenderEvent var1) {
      if (mc.player != null && mc.world != null) {
         if (this.mode.isSelected(0)) {
            if (mc.currentScreen == null) {
               this.method2683(var1.getMenuDrawContext());
            }
         } else {
            if (this.flag || this.flag2) {
               this.method2695();
            }
         }
      }
   }

   private void method2683(MenuDrawContext var1) {
      float var2;
      if ((var2 = this.animation.method2808(this.flag ? 1.0F : 0.0F)) <= 0.005F && !this.flag) {
         for (Animation var14 : this.animations) {
            var14.method2809(0.0F);
         }

         this.intValue = -1;
      } else {
         if (this.flag) {
            this.method2694(true);
         }

         ZenithStyle var3 = zenith.getStyleManager().getCurrentStyle();
         if (var3 != null) {
            float var4 = mc.getWindow().getScaledWidth() / 2.0F;
            float var5 = mc.getWindow().getScaledHeight() / 2.0F;
            float var6 = 0.85F + 0.15F * var2;
            float var7 = 62.0F * var6;
            float var8 = 98.0F * var6;
            int var9 = this.flag ? this.method2691(var1.getMouseX(), var1.getMouseY(), var4, var5, var7) : -1;
            this.intValue = this.flag ? var9 : -1;
            this.method2684();
            MatrixStack var10 = MatrixUtils.method666(var1.getMatrices());
            this.method2685(var10, var4, var5, var8, var2);
            this.method2686(var10, var3, var4, var5, var7, var8, var2, var9);
            this.method2687(var1, var3, var4, var5, var7, var8, var2);
            this.method2688(var1, var3, var4, var5, var2, var6);
            this.method2690(var1, var10, var3, var4, var5, var8, var2, var9);
         }
      }
   }

   private void method2684() {
      Arrays.fill(this.slots, null);
      Arrays.fill(this.intValues, 0);

      for (Slot var2 : mc.player.currentScreenHandler.slots) {
         ItemStack var3 = var2.getStack();
         if (!var3.isEmpty()) {
            NbtComponent var4 = var3.get(DataComponentTypes.CUSTOM_DATA);
            if (var4 != null) {
               String var5 = var4.copyNbt().toString();
               boolean var6 = var3.getItem().equals(Items.SPLASH_POTION);

               for (int var7 = 0; var7 < types.length; var7++) {
                  if (var5.contains(types[var7].text2)) {
                     if (var2.id != 46) {
                        this.intValues[var7] = this.intValues[var7] + var3.getCount();
                     }

                     if (var6 && (this.slots[var7] == null || var2.id > this.slots[var7].id)) {
                        this.slots[var7] = var2;
                     }
                  }
               }
            }
         }
      }
   }

   private void method2685(MatrixStack var1, float var2, float var3, float var4, float var5) {
      float var6;
      if (!((var6 = zenith.getNLMenuScreen().getBlurPower()) <= 0.0F)) {
         float var7 = var4 + 7.0F;
         RenderShaders.method5287(
            var1.peek().getPositionMatrix(),
            var2 - var7,
            var3 - var7,
            var7 * 2.0F,
            var7 * 2.0F,
            var6,
            ProxyDotRadiusData.method8069(var7),
            ZenithColor.INSTANCE.method7294(var5 * 0.92F),
            true,
            false
         );
      }
   }

   private void method2686(MatrixStack var1, ZenithStyle var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      float var9;
      float var10 = (var9 = 360.0F / types.length) - 2.4F;

      for (int var11 = 0; var11 < types.length; var11++) {
         float var12 = this.animations[var11].method2808(var11 == var8 ? 1.0F : 0.0F);
         boolean var13 = this.slots[var11] == null;
         float var14 = var6 + var12 * 6.0F;
         ZenithColor var15;
         ZenithColor var16;
         if (var13) {
            var15 = var2.getDisableActiveBg()
               .getZenithColor()
               .method7294(var7)
               .method7295(var2.getPrimaryColor().getZenithColor().method7294(var7), var12 * 0.45F);
            var16 = var2.getRightBackground()
               .getZenithColor()
               .method7294(var7 * 0.9F)
               .method7295(var2.getPrimaryColor().getZenithColor().method7296(0.55F).method7294(var7), var12 * 0.4F);
         } else {
            var15 = var2.getSurfaceDisableBackground().getZenithColor().method7294(var7);
            var16 = var2.getRightBackground().getZenithColor().method7294(var7 * 0.92F);
            if (var12 > 0.001F) {
               ZenithColor var17 = var2.getSurfaceEnableBackground()
                  .getZenithColor()
                  .method7294(var7)
                  .method7295(var2.getPrimaryColor().getZenithColor().method7294(var7), var12 * 0.65F);
               ZenithColor var18 = var2.getPrimaryColor().getZenithColor().method7296(0.48F).method7294(var7);
               var15 = var15.method7295(var17, var12);
               var16 = var16.method7295(var18, var12);
            }
         }

         var1.push();
         var1.translate(var3, var4, 0.0F);
         var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var11 * var9 + 1.2F));
         var1.translate(-var3, -var4, 0.0F);
         RenderShaders.method5276(
            var1.peek().getPositionMatrix(), var3 - var14, var4 - var14, var14 * 2.0F, var14 * 2.0F, var14 - var5, var10, 0.0F, var15, var16, var16, var15
         );
         if (var12 > 0.01F) {
            RenderShaders.method5275(
               var1.peek().getPositionMatrix(),
               var3 - var14,
               var4 - var14,
               var14 * 2.0F,
               var14 * 2.0F,
               1.35F + var12,
               var10,
               0.0F,
               var2.getPrimaryColor().getZenithColor().method7294(var7 * var12 * 0.8F)
            );
         }

         var1.pop();
      }
   }

   private void method2687(MenuDrawContext var1, ZenithStyle var2, float var3, float var4, float var5, float var6, float var7) {
      for (int var8 = 0; var8 < 2; var8++) {
         if (var8 == 1) {
            ScissorFlusher.flush();
         }

         for (int var9 = 0; var9 < types.length; var9++) {
            PotionThrowerModule.Type var10 = types[var9];
            float var11 = this.animations[var9].getValue();
            float var12 = (float)((-Math.PI / 2) + (Math.PI * 2) * ((var9 + 0.5) / types.length));
            float var13 = (var5 + var6) / 2.0F + var11 * 4.0F;
            float var14 = var3 + (float)Math.cos(var12) * var13;
            float var15 = var4 + (float)Math.sin(var12) * var13;
            if (var8 == 0) {
               Slot var16 = this.slots[var9];
               ItemStack var17 = var16 == null ? var10.method2699() : var16.getStack();
               this.method2689(var1, var17, var14 - 8.0F, var15 - 8.0F, var16 == null ? var7 * 0.45F : var7);
            } else {
               int var21;
               if ((var21 = this.intValues[var9]) > 1) {
                  Font var22 = Fonts.NEW_SEMIBOLD.getFont(6.0F);
                  String var18 = String.valueOf(var21);
                  float var19 = var14 + 8.0F - var22.width(var18);
                  float var20 = var15 + 5.5F;
                  var1.drawText(var22, var18, var19 + 0.5F, var20 + 0.5F, var2.getRightBackground().getZenithColor().method7294(var7));
                  var1.drawText(var22, var18, var19, var20, var2.getTextEnable().getZenithColor().method7294(var7));
               }
            }
         }
      }
   }

   private void method2688(MenuDrawContext var1, ZenithStyle var2, float var3, float var4, float var5, float var6) {
      float var7 = 32.0F * var6;
      var1.drawRoundedRect(
         var3 - var7,
         var4 - var7,
         var7 * 2.0F,
         var7 * 2.0F,
         ProxyDotRadiusData.method8069(var7),
         var2.getLeftBackground().getZenithColor().method7294(var5 * 0.97F)
      );
      ItemStack var8 = mc.player.getMainHandStack();
      if (!var8.isEmpty() && var8.getItem() != Items.AIR) {
         this.method2689(var1, var8, var3 - 8.0F, var4 - 8.0F, var5);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void method2689(MenuDrawContext var1, ItemStack var2, float var3, float var4, float var5) {
      RenderState.method1871();
      RenderState.method1873();
      RenderState.method1883(1.0F, 1.0F, 1.0F, Math.clamp(var5, 0.0F, 1.0F));

      try {
         var1.drawItem(var2, (int)var3, (int)var4);
      } catch (Throwable var8) {
         RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
         throw var8;
      }

      RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void method2690(MenuDrawContext var1, MatrixStack var2, ZenithStyle var3, float var4, float var5, float var6, float var7, int var8) {
      if (!(var7 <= 0.5F)) {
         String var9;
         String var10;
         if (var8 != -1) {
            PotionThrowerModule.Type var11 = types[var8];
            var9 = var11.method2702();
            if (this.slots[var8] == null) {
               var10 = translate("module.potionThrower.missing");
            } else if (!this.method2681(var11.floatValue)) {
               var10 = translate("module.potionThrower.noTarget");
            } else {
               var10 = translate("module.potionThrower.release");
            }
         } else {
            var9 = translate("module.potionThrower.title");
            var10 = translate("module.potionThrower.hint");
         }

         Font var17 = Fonts.NEW_SEMIBOLD.getFont(7.5F);
         Font var12 = Fonts.NEW_MEDIUM.getFont(5.5F);
         float var13 = Math.max(var17.width(var9), var12.width(var10)) + 20.0F;
         float var14 = var4 - var13 / 2.0F;
         float var15 = var5 - var6 - 38.0F;
         float var16;
         if ((var16 = zenith.getNLMenuScreen().getBlurPower()) > 0.0F) {
            RenderShaders.method5287(
               var2.peek().getPositionMatrix(),
               var14,
               var15,
               var13,
               27.0F,
               var16,
               ProxyDotRadiusData.method8069(7.0F),
               ZenithColor.INSTANCE.method7294(var7),
               true,
               false
            );
         }

         var1.drawRoundedRect(
            var14, var15, var13, 27.0F, ProxyDotRadiusData.method8069(7.0F), var3.getLeftBackground().getZenithColor().method7294(var7 * 0.92F)
         );
         var1.drawText(var17, var9, var4 - var17.width(var9) / 2.0F, var15 + 5.0F, var3.getTextEnable().getZenithColor().method7294(var7));
         var1.drawText(var12, var10, var4 - var12.width(var10) / 2.0F, var15 + 16.0F, var3.getTextSecondary().getZenithColor().method7294(var7));
      }
   }

   private int method2691(float var1, float var2, float var3, float var4, float var5) {
      float var6 = var1 - var3;
      float var7 = var2 - var4;
      if (Math.sqrt(var6 * var6 + var7 * var7) < var5 * 0.35F) {
         return -1;
      } else {
         double var8;
         if ((var8 = Math.atan2(var7, var6) + (Math.PI / 2)) < 0.0) {
            var8 += Math.PI * 2;
         }

         int var10;
         return (var10 = (int)Math.floor(var8 / (Math.PI * 2) * types.length)) >= 0 && var10 < types.length ? var10 : -1;
      }
   }

   private float method2692() {
      return (float)(mc.mouse.getX() * mc.getWindow().getScaledWidth() / mc.getWindow().getWidth());
   }

   private float method2693() {
      return (float)(mc.mouse.getY() * mc.getWindow().getScaledHeight() / mc.getWindow().getHeight());
   }

   private void method2694(boolean var1) {
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

   private void method2695() {
      this.flag = false;
      this.intValue = -1;
      this.method2694(false);
   }

   private static String translate(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   @EventTarget
   public void method2696(WorldRenderEvent var1) {
      if (this.drawRadius.isEnabled() && mc.player != null && mc.world != null) {
         if (this.mode.isSelected(0)) {
            if (this.flag && this.intValue != -1) {
               this.method2697(var1.getMatrixStack(), types[this.intValue]);
            }
         } else {
            for (PotionThrowerModule.Entry var3 : this.items) {
               if (var3.flag) {
                  this.method2697(var1.getMatrixStack(), var3.type);
               }
            }
         }
      }
   }

   private void method2697(MatrixStack var1, PotionThrowerModule.Type var2) {
      if (!(var2.floatValue <= 0.0F)) {
         this.method2698(var1, var2.floatValue, ColorUtils.CONST_intValue);
      }
   }

   private void method2698(MatrixStack var1, float var2, int var3) {
      float var4 = mc.player.getWidth() / 2.0F;
      int var5 = this.method2681(var2) ? Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287() : var3;
      Vec3d var6 = MathUtils.method2656(mc.player).add(var4, 0.02, var4);
      Vec3d var7 = var6.subtract(mc.getEntityRenderDispatcher().camera.getCameraPos());
      GL11.glEnable(2881);
      RenderState.method1871();
      RenderState.method1878();
      RenderState.method1882(false);
      RenderState.method1877();
      RenderState.method1874(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_CONSTANT_ALPHA);
      RenderState.method1898("core/position_color");
      BufferBuilder var8 = Tessellator.getInstance().begin(DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
      int var9 = 0;

      for (byte var10 = 90; var9 <= var10; var9++) {
         Vec3d var11 = MathUtils.method2653(var9, var10, var2);
         Vec3d var12 = MathUtils.method2653(var9 + 1, var10, var2);
         GlowUtil.method2235(
            var1, var8, var7.add(var11), var7.add(var11.x, var11.y + 2.0, var11.z), ColorUtils.method6439(var5, 0.2F), ColorUtils.method6439(var5, 0.0F)
         );
         GlowUtil.method2245(var6.add(var11), var6.add(var12), var5, 2.0F, true);
      }

      var9 = 0;

      for (byte var14 = 90; var9 <= var14; var9++) {
         Vec3d var15 = MathUtils.method2653(var9, var14, var2);
         GlowUtil.method2235(
            var1, var8, var7.add(var15), var7.add(var15.x, var15.y - 2.0, var15.z), ColorUtils.method6439(var5, 0.2F), ColorUtils.method6439(var5, 0.0F)
         );
      }

      ImmediateRenderer.method8015(var8.end());
      RenderState.method1879();
      RenderState.method1882(true);
      RenderState.method1872();
      GL11.glDisable(2881);
   }

   private static final class Entry {
      private final PotionThrowerModule.Type type;
      private final BindSetting bindSetting;
      private boolean flag;

      private Entry(PotionThrowerModule.Type var1, BindSetting var2) {
         this.type = var1;
         this.bindSetting = var2;
      }
   }

   private static enum Type {
      RADIATION("module.potionThrower.radiation", "potion-radiation", 10.0F, 3329330),
      DROWSINESS("module.potionThrower.drowsiness", "potion-drowsiness", 10.0F, 4737096),
      POPPER("module.potionThrower.popper", "potion-popper", 10.0F, 16711680),
      ASSASSIN("module.potionThrower.assassin", "potion-assassin", 0.0F, 3355443),
      PALADIN("module.potionThrower.paladin", "potion-paladin", 0.0F, 65535),
      RAGE("module.potionThrower.rage", "potion-rage", 0.0F, 10040115),
      HOLY_WATER("module.potionThrower.holyWater", "potion-holy-water", 0.0F, 16777215);

      private final String text;
      private final String text2;
      private final float floatValue;
      private final int intValue;
      private ItemStack itemStack;

      private Type(String var3, String var4, float var5, int var6) {
         this.text = var3;
         this.text2 = var4;
         this.floatValue = var5;
         this.intValue = var6;
      }

      private ItemStack method2699() {
         if (this.itemStack == null) {
            ItemStack var1 = new ItemStack(Items.SPLASH_POTION);
            var1.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.empty(), Optional.of(this.intValue), List.of(), Optional.empty()));
            this.itemStack = var1;
         }

         return this.itemStack;
      }

      private Predicate<Slot> method2700() {
         return var1 -> InventoryUtils.method918(var1.getStack(), this.text2);
      }

      private Slot method2701() {
         return InventoryUtils.method907(Items.SPLASH_POTION, this.method2700());
      }

      private int count() {
         return InventoryUtils.method923(var1 -> var1.id != 46 && InventoryUtils.method918(var1.getStack(), this.text2));
      }

      private String method2702() {
         return PotionThrowerModule.translate(this.text);
      }
   }
}

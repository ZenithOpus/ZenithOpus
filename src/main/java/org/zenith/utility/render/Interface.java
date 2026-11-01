package org.zenith.utility.render;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.gui.screen.ChatScreen;
import org.joml.Vector2f;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.GroupSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.HudQueuedContext;
import org.zenith.utility.render.display.base.MenuDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;
import org.zenith.utility.render.display.base.Vec2Utils;

public abstract class Interface implements MinecraftAccess {
   private final String text;
   private static final float CONST_floatValue = 12.0F;
   private static final float CONST_floatValue2 = 0.5F;
   private static final float CONST_floatValue3 = 1.2F;
   private static final float CONST_floatValue4 = 0.8F;
   private static final float CONST_floatValue5 = 1.0F;
   public float floatValue;
   public float floatValue2;
   public float width;
   public float height;
   public float floatValue3;
   public float floatValue4;
   public float floatValue5 = Float.NaN;
   public float floatValue6 = Float.NaN;
   public float floatValue7 = -1.0F;
   public float floatValue8 = -1.0F;
   public final SliderSetting scale = new SliderSetting(
      "module.interface.hudElement.scale", 100.0F, 50.0F, 250.0F, 1.0F, "module.interface.hudElement.scale.desc", "%"
   );
   public final SliderSetting opacity = new SliderSetting(
      "module.interface.hudElement.opacity", 100.0F, 10.0F, 100.0F, 1.0F, "module.interface.hudElement.opacity.desc", "%"
   );
   public final ModeSetting visibility = new ModeSetting(
      "module.interface.hudElement.visibility",
      "module.interface.hudElement.visibility.desc",
      "module.interface.hudElement.visibility.always",
      "module.interface.hudElement.visibility.combat",
      "module.interface.hudElement.visibility.peace"
   );
   public final BooleanSetting hideInScreens = new BooleanSetting(
      "module.interface.hudElement.hideInScreens", "module.interface.hudElement.hideInScreens.desc", false
   );
   public final BooleanSetting lock = new BooleanSetting("module.interface.hudElement.lock", "module.interface.hudElement.lock.desc", false);
   public final ButtonSetting reset = new ButtonSetting("module.interface.hudElement.reset", this::method2063);
   private final Interface.Type type;
   private final float floatValue9;
   private final float floatValue10;
   private float floatValue11 = 1.0F;
   private long lastFrame;
   private static final float CONST_floatValue6 = 8.0F;
   private boolean flag = true;
   private Interface.Type type2 = Interface.Type.TOP_LEFT;
   private float floatValue12 = 0.0F;
   private float floatValue13 = 0.0F;
   private float floatValue14 = 1.0F;
   private float floatValue15;
   private boolean flag2;
   private static final float[] CONST_floatValues = new float[4];
   private static long longValue = Long.MIN_VALUE;
   private static int intValue;
   private static int intValue2;
   private static float floatValue16;
   private static final int[] CONST_intValues = new int[]{3, 2, 0, 1};
   private static final double[] CONST_doubleValues = new double[]{-Math.PI * 3.0 / 4.0, -Math.PI / 4, Math.PI * 3.0 / 4.0, Math.PI / 4};
   private static final float[] CONST_floatValues2 = new float[]{-1.0F, 1.0F, -1.0F, 1.0F};
   private static final float[] CONST_floatValues3 = new float[]{-1.0F, -1.0F, 1.0F, 1.0F};
   private static final ZenithColor zenithColor = new ZenithColor(255, 70, 90);
   private static final ZenithColor zenithColor2 = new ZenithColor(70, 170, 255);

   public void tick() {
   }

   public boolean method2056(MouseButtonEvent var1) {
      return false;
   }

   public Interface(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      this.text = var1;
      this.floatValue = var2;
      this.floatValue2 = var3;
      this.floatValue3 = var4;
      this.floatValue4 = var5;
      this.floatValue12 = var6;
      this.floatValue13 = var7;
      this.type2 = var8 != null ? var8 : Interface.Type.TOP_LEFT;
      this.type = this.type2;
      this.floatValue9 = var6;
      this.floatValue10 = var7;
   }

   public List<Setting> method2057() {
      return List.of(this.scale, this.opacity, this.visibility, this.hideInScreens, this.lock, this.reset);
   }

   public List<Setting> method2058() {
      var var1 = new ArrayList<>(this.getSettings());
      var1.removeAll(this.method2057());
      return var1;
   }

   public boolean method2059() {
      return this.lock.isEnabled();
   }

   public boolean method2060() {
      return !this.visibility.isSelected(0) || this.hideInScreens.isEnabled();
   }

   public boolean method2061() {
      if (this.visibility.isSelected(1) && !InterfaceModule.INSTANCE.method6788()) {
         return false;
      } else {
         return this.visibility.isSelected(2) && InterfaceModule.INSTANCE.method6788()
            ? false
            : !this.hideInScreens.isEnabled() && !InterfaceModule.INSTANCE.method6785()
               || mc.currentScreen == null
               || mc.currentScreen instanceof ChatScreen
               || Zenith.getInstance().getNLMenuScreen().isRenderHud();
      }
   }

   private float method2062() {
      long var1 = System.currentTimeMillis();
      float var3 = this.lastFrame == 0L ? 0.0F : Math.min(250.0F, (float)(var1 - this.lastFrame));
      this.lastFrame = var1;
      float var4 = this.method2061() ? 1.0F : 0.0F;
      if (!InterfaceModule.INSTANCE.method6783()) {
         this.floatValue11 = var4;
      } else {
         float var5 = var3 / Math.max(1.0F, InterfaceModule.INSTANCE.method6784());
         this.floatValue11 = var4 > this.floatValue11 ? Math.min(var4, this.floatValue11 + var5) : Math.max(var4, this.floatValue11 - var5);
      }

      return this.floatValue11;
   }

   public void method2063() {
      float var1 = this.method2097(this.floatValue3);
      float var2 = this.method2098(this.floatValue4);
      if (!(var1 <= 0.0F) && !(var2 <= 0.0F)) {
         this.type2 = this.type;
         this.floatValue12 = this.floatValue9;
         this.floatValue13 = this.floatValue10;
         float var3 = this.method2079(this.type, var1) + this.floatValue9;
         float var4 = this.method2080(this.type, var2) + this.floatValue10;
         this.floatValue = this.method2088(var3 - this.method2064(), var1) + this.method2064();
         this.floatValue2 = this.method2089(var4 - this.method2065(), var2) + this.method2065();
         this.method2094(var1, var2);
         this.method2091(var1, var2);
      }
   }

   public float method290() {
      return Math.max(0.01F, this.scale.getValue3() / 100.0F);
   }

   public float method296() {
      return this.width * this.method290();
   }

   public float method297() {
      return this.height * this.method290();
   }

   public float method294() {
      return this.floatValue - this.method2064();
   }

   public float method295() {
      return this.floatValue2 - this.method2065();
   }

   private float method2064() {
      return (this.method296() - this.width) / 2.0F;
   }

   public float method2065() {
      return (this.method297() - this.height) / 2.0F;
   }

   public void method291(MenuDrawContext var1) {
      float var2 = this.method2062();
      float var3 = this.opacity.getValue3() / 100.0F * var2;
      if (this.method2074()) {
         var3 = Math.max(var3, 0.35F);
      } else if (var3 <= 0.01F) {
         return;
      }

      float var4 = this.method290();
      float var5 = HudQueuedContext.getAlpha();
      HudQueuedContext.setAlpha(var3);

      try {
         this.method2068(var1, var4);
      } finally {
         HudQueuedContext.setAlpha(var5);
      }
   }

   public float getFloatValue14() {
      return this.floatValue14;
   }

   public void setFloatValue14(float var1) {
      this.floatValue14 = var1;
   }

   public void method2066(float var1, boolean var2) {
      this.floatValue15 = var1;
      this.flag2 = var2;
   }

   public void method2067(float var1, float var2, float var3, float var4) {
      this.floatValue = this.method2088(var1, var3) + this.method2064();
      this.floatValue2 = this.method2089(var2, var4) + this.method2065();
      this.floatValue3 = var3;
      this.floatValue4 = var4;
      this.method2091(var3, var4);
   }

   private void method2068(MenuDrawContext var1, float var2) {
      var2 *= this.floatValue14;
      var1.pushMatrix();

      try {
         var1.getMatrices().translate(this.floatValue + this.width / 2.0F, this.floatValue2 + this.height / 2.0F);
         var1.getMatrices().scale(var2, var2);
         var1.getMatrices().translate(-this.floatValue - this.width / 2.0F, -this.floatValue2 - this.height / 2.0F);
         float var3 = this.width;
         float var4 = this.height;
         this.width = 0.0F;
         this.height = 0.0F;
         this.method8(var1);
         if (!(this.width <= 0.0F) && !(this.height <= 0.0F)) {
            if (this.floatValue15 > 0.01F) {
               ZenithColor var5 = Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor();
               var1.drawRoundedRect(
                  this.floatValue,
                  this.floatValue2,
                  this.width,
                  this.height,
                  ProxyDotRadiusData.method8069(Math.max(0.0F, InterfaceModule.method6801())),
                  var5.method7292((this.flag2 ? 0.12F : 0.06F) * this.floatValue15)
               );
            }

            this.method2069(var1);
         } else {
            this.width = var3;
            this.height = var4;
         }
      } finally {
         var1.popMatrix();
      }
   }

   public abstract void method8(CustomDrawContext var1);

   public void method2069(CustomDrawContext var1) {
      this.method4679(var1, this.floatValue, this.floatValue2, this.width, this.height, this.method254());
   }

   public void method4679(CustomDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = this.floatValue15;
      if (InterfaceModule.INSTANCE.method6822() || !(var7 <= 0.01F)) {
         if (!(var4 <= 0.0F) && !(var5 <= 0.0F)) {
            float var8 = Math.clamp(Math.max(var6, var7), 0.0F, 1.0F);
            if (!(var8 <= 0.01F)) {
               float var9 = var2 - 1.0F;
               float var10 = var3 - 1.0F;
               float var11 = var4 + 2.0F;
               float var12 = var5 + 2.0F;
               float var13 = Math.max(0.0F, InterfaceModule.method6801()) + 1.0F;
               float var14 = Math.max(12.0F, var13 * 2.0F + 4.0F) + var7 * (this.flag2 ? 7.0F : 4.0F);
               var14 = Math.min(var14, Math.min(var11, var12) / 2.0F);
               float var15 = 1.2F + var7 * (this.flag2 ? 0.9F : 0.5F);
               if (!(var14 <= var13)) {
                  ZenithColor var16 = Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor();
                  ZenithColor var17 = InterfaceModule.INSTANCE.method6824();
                  ProxyDotRadiusData var18 = ProxyDotRadiusData.method8069(var13);
                  float[] var19 = this.method2071();
                  float var22 = var9 + var11 - var14;
                  float var23 = var10 + var12 - var14;
                  float[] var24 = new float[]{var9, var22, var9, var22};
                  float[] var25 = new float[]{var10, var10, var23, var23};
                  var1.pushMatrix();
                  var1.getMatrices().translate(var2 + var4 / 2.0F, var3 + var5 / 2.0F);
                  var1.getMatrices().scale(var8, var8);
                  var1.getMatrices().translate(-(var2 + var4 / 2.0F), -(var3 + var5 / 2.0F));
                  ZenithColor var26 = var16.method7295(ZenithColor.INSTANCE, 0.8F * (1.0F - var7 * 0.75F));

                  for (int var27 = 0; var27 < 4; var27++) {
                     float var28 = var19[var27] * (1.0F - var7);
                     float var29 = 0.5F * (0.75F + 0.5F * var28) * var8;
                     var29 += (1.0F - var29) * var7 * 0.9F;
                     ZenithColor var30 = var26.method7295(this.method2072(var17, var27), var28 * 0.7F).method7294(var29);
                     var1.drawRoundedCornerOnly(var24[var27], var25[var27], var14, var14, var15, var18, var30, var27);
                  }

                  var1.popMatrix();
               }
            }
         }
      }
   }

   private float[] method2071() {
      long var1 = System.currentTimeMillis();
      int var3 = InterfaceModule.INSTANCE.method6826();
      int var4 = InterfaceModule.INSTANCE.method6827();
      float var5 = InterfaceModule.INSTANCE.method6825();
      if (var1 == longValue && var3 == intValue && var4 == intValue2 && var5 == floatValue16) {
         return CONST_floatValues;
      } else {
         float[] var6 = method2073(var1, var3, var5);

         for (int var7 = 0; var7 < var6.length; var7++) {
            float var8 = Math.max(0.0F, Math.min(1.0F, var6[var7]));

            var6[var7] = switch (var4) {
               case 2 -> Math.min(1.0F, (float)Math.pow(var8, 0.7) * 0.6F + (float)Math.pow(var8, 5.0) * 0.6F);
               case 3 -> {
                  float var10 = Math.max(0.0F, Math.min(1.0F, var8 * (1.0F - var8) * 4.0F));
                  yield var10 * var10;
               }
               case 4 -> {
                  float var9 = 0.5F + 0.5F * (float)Math.cos(var8 * Math.PI * 6.0);
                  yield var8 * (0.3F + 0.7F * var9 * var9);
               }
               case 5 -> var8 * (0.5F + 0.5F * (float)Math.pow(0.5 + 0.5 * Math.sin(var1 / 400.0 + var7 * 2.1), 2.0));
               default -> var8;
            };
         }

         longValue = var1;
         intValue = var3;
         intValue2 = var4;
         floatValue16 = var5;
         return var6;
      }
   }

   private ZenithColor method2072(ZenithColor var1, int var2) {
      return switch (InterfaceModule.INSTANCE.method6827()) {
         case 1 -> var1.method7295(var2 % 2 == 0 ? zenithColor : zenithColor2, 0.65F);
         case 2, 4 -> var1.method7295(ZenithColor.INSTANCE, 0.55F);
         default -> var1;
      };
   }

   private static float[] method2073(long var0, int var2, float var3) {
      float var4 = (float)(var0 % 1000000L / 1000.0 * var3);
      float[] var5 = CONST_floatValues;
      Arrays.fill(var5, 0.0F);
      switch (var2) {
         case 1:
         case 2:
            float var16 = var4 * 0.5F - (float)Math.floor(var4 * 0.5F);
            float var20 = (float)Math.exp(-Math.pow((var16 - 0.25F) * 7.0F, 2.0));
            float var24 = (float)Math.exp(-Math.pow((var16 - 0.75F) * 7.0F, 2.0));
            var5[0] = var20;
            var5[2] = var20;
            var5[1] = var24;
            var5[3] = var24;
            return var5;
         case 3:
            float var15 = (float)(var4 * Math.PI % (Math.PI * 2));
            double[] var19 = CONST_doubleValues;

            for (int var23 = 0; var23 < 4; var23++) {
               double var27 = Math.abs(((var19[var23] - var15) % (Math.PI * 2) + (Math.PI * 3)) % (Math.PI * 2) - Math.PI);
               var5[var23] = (float)Math.exp(-var27 * var27 * 2.5);
            }

            return var5;
         case 4:
         case 7:
         case 8:
         case 9:
         case 10:
         default:
            float var14 = var4 % 4.0F;
            int var18 = (int)var14;
            float var22 = var14 - var18;
            int[] var26 = CONST_intValues;
            var5[var26[var18]] = (float)Math.sin(Math.PI * var22);
            var5[var26[(var18 + 1) % 4]] = (float)Math.sin(Math.PI * var22) * 0.25F * var22;
            return var5;
         case 5:
            float var13 = (float)Math.sin(var4 * 1.7F);
            float var17 = (float)Math.cos(var4 * 1.13F);
            float[] var21 = CONST_floatValues2;
            float[] var25 = CONST_floatValues3;

            for (int var10 = 0; var10 < 4; var10++) {
               float var11 = var13 - var21[var10];
               float var12 = var17 - var25[var10];
               var5[var10] = (float)Math.exp(-(var11 * var11 + var12 * var12) * 0.9F);
            }

            return var5;
         case 6:
            float var6 = (float)Math.sin(var4 * 0.6F) * 0.35F;
            float var7 = (float)Math.exp(-Math.pow((-1.0F - var6) * 1.6F, 2.0));
            float var8 = (float)Math.exp(-Math.pow((1.0F - var6) * 1.6F, 2.0));
            float var9 = 0.75F + 0.25F * (float)Math.sin(var4 * 3.0F);
            var5[0] = var7 * var9;
            var5[1] = var7 * var9;
            var5[2] = var8 * var9;
            var5[3] = var8 * var9;
            return var5;
         case 11:
            return var5;
      }
   }

   public boolean method2074() {
      return mc.currentScreen instanceof ChatScreen || Zenith.getInstance().getNLMenuScreen().isRenderHud();
   }

   public float method254() {
      return 1.0F;
   }

   public boolean isEnabled() {
      return this.flag;
   }

   public void setEnabled(boolean var1) {
      this.flag = var1;
   }

   public void toggle() {
      this.flag = !this.flag;
   }

   public List<Setting> getSettings() {
      ArrayList<Setting> var1 = new ArrayList<>();

      for (Class var2 = this.getClass(); var2 != null && Interface.class.isAssignableFrom(var2); var2 = var2.getSuperclass()) {
         Arrays.stream(var2.getDeclaredFields()).forEach(var2x -> {
            try {
               var2x.setAccessible(true);
               if (var2x.get(this) instanceof Setting var4) {
                  var1.add(var4);
               }
            } catch (IllegalAccessException var5) {
            }
         });
         if (var2 == Interface.class) {
            break;
         }
      }

      return var1;
   }

   public boolean method293(double var1, double var3) {
      float var5 = this.method294();
      float var6 = this.method295();
      return var1 >= var5 && var1 <= var5 + this.method296() && var3 >= var6 && var3 <= var6 + this.method297();
   }

   public boolean method2075(double var1, double var3) {
      float var5 = this.method294();
      float var6 = this.method295();
      float var7 = var5 + this.method296();
      float var8 = var6 + this.method297();
      return var1 >= var7 - 8.0F && var1 <= var7 + 2.0F && var3 >= var8 - 8.0F && var3 <= var8 + 2.0F;
   }

   public void method2076(CustomDrawContext var1) {
      float var2 = 5.5F;
      float var3 = 6.0F;
      ZenithColor var4 = new ZenithColor(179, 145, 255, 255);
      var1.drawRoundedBorder(this.floatValue, this.floatValue2, this.width, this.height, var2, ProxyDotRadiusData.method8069(var3), var4);
   }

   public void method289(CustomDrawContext var1, float var2, float var3, InterfaceModule var4, float var5, float var6) {
      float var7 = this.method2088(var2, var5);
      float var8 = this.method2089(var3, var6);
      float var9 = this.method296();
      float var10 = this.method297();
      Vector2f var11 = var4.method6809(var7, var8);
      Interface.Entry var12 = new Interface.Entry(var11.x(), 0.0F);
      Interface.Entry var13 = new Interface.Entry(var11.y(), 0.0F);
      Vector2f var14 = var4.method6809(var7 + var9, var8 + var10);
      Interface.Entry var15 = new Interface.Entry(var14.x(), -var9);
      Interface.Entry var16 = new Interface.Entry(var14.y(), -var10);
      Vector2f var17 = var4.method6809(var7 + var9 / 2.0F, var8 + var10 / 2.0F);
      Interface.Entry var18 = new Interface.Entry(var17.x(), -var9 / 2.0F);
      Interface.Entry var19 = new Interface.Entry(var17.y(), -var10 / 2.0F);
      float var20 = InterfaceModule.INSTANCE.method6786();
      if (var20 >= 1.0F) {
         var7 = this.method2088(Math.round(var7 / var20) * var20, var5);
         var8 = this.method2089(Math.round(var8 / var20) * var20, var6);
      }

      this.floatValue = var7 + this.method2064();
      this.floatValue2 = var8 + this.method2065();
      this.floatValue3 = var5;
      this.floatValue4 = var6;
      this.method2091(var5, var6);
      Interface.Entry var21 = this.method2077(var12, var15, var18);
      Interface.Entry var22 = this.method2077(var13, var16, var19);
      this.method285(var1, var21);
      this.method286(var1, var22);
   }

   private Interface.Entry method2077(Interface.Entry var1, Interface.Entry var2, Interface.Entry var3) {
      if (var1.floatValue != -1.0F) {
         return var1;
      } else {
         return var2.floatValue != -1.0F ? var2 : var3;
      }
   }

   public void method286(CustomDrawContext var1, Interface.Entry var2) {
      if (var2.floatValue == -1.0F) {
         this.floatValue8 = var2.floatValue;
      } else {
         float var3 = this.floatValue3 > 0.0F ? this.floatValue3 : var1.getScaledWindowWidth();
         var1.drawRoundedRect(
            this.method2081(var3),
            var2.floatValue,
            this.method2083(var3),
            1.0F,
            ProxyDotRadiusData.INSTANCE,
            Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor()
         );
         this.floatValue8 = var2.floatValue + var2.offset;
      }
   }

   public void method285(CustomDrawContext var1, Interface.Entry var2) {
      if (var2.floatValue == -1.0F) {
         this.floatValue7 = var2.floatValue;
      } else {
         float var3 = this.floatValue4 > 0.0F ? this.floatValue4 : var1.getScaledWindowHeight();
         var1.drawRoundedRect(
            var2.floatValue,
            this.method2084(var3),
            1.0F,
            this.method2086(var3),
            ProxyDotRadiusData.INSTANCE,
            Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor()
         );
         this.floatValue7 = var2.floatValue + var2.offset;
      }
   }

   public void method287(float var1, float var2) {
      this.floatValue = var1;
      this.floatValue2 = var2;
      float var3 = this.method2097(this.floatValue3);
      float var4 = this.method2098(this.floatValue4);
      if (var3 > 0.0F && var4 > 0.0F) {
         this.update(var3, var4);
         this.method2094(var3, var4);
      }

      this.method2091(var3, var4);
   }

   public void method288(float var1, float var2) {
      if (!(var2 <= 0.0F) && !(var1 <= 0.0F)) {
         if (!this.method2090()) {
            float var3 = this.floatValue3 > 0.0F ? this.floatValue3 : var1;
            float var4 = this.floatValue4 > 0.0F ? this.floatValue4 : var2;
            this.method2091(var3, var4);
         }

         this.floatValue3 = var1;
         this.floatValue4 = var2;
         float var7;
         float var8;
         if (this.method2090()) {
            var7 = this.method2081(var1) + this.floatValue5 * this.method2083(var1) - this.method2092() * this.method296();
            var8 = this.method2084(var2) + this.floatValue6 * this.method2086(var2) - this.method2093() * this.method297();
         } else if (this.type2 != null) {
            float var5 = this.method2079(this.type2, var1) + this.floatValue12;
            float var6 = this.method2080(this.type2, var2) + this.floatValue13;
            var7 = var5 - this.method2064();
            var8 = var6 - this.method2065();
         } else {
            var7 = this.method294();
            var8 = this.method295();
         }

         this.floatValue = this.method2088(var7, var1) + this.method2064();
         this.floatValue2 = this.method2089(var8, var2) + this.method2065();
      }
   }

   public void update(float var1, float var2) {
      if (!(var1 <= 0.0F) && !(var2 <= 0.0F)) {
         float var3 = this.method2088(this.method294(), var1);
         float var4 = this.method2089(this.method295(), var2);
         this.floatValue = var3 + this.method2064();
         this.floatValue2 = var4 + this.method2065();
      }
   }

   public void release() {
      if (this.floatValue7 != -1.0F) {
         this.floatValue = this.floatValue7 + this.method2064();
      }

      if (this.floatValue8 != -1.0F) {
         this.floatValue2 = this.floatValue8 + this.method2065();
      }

      float var1 = this.method2097(this.floatValue3);
      float var2 = this.method2098(this.floatValue4);
      if (var1 > 0.0F && var2 > 0.0F) {
         this.update(var1, var2);
         this.method2094(var1, var2);
      }

      this.floatValue7 = -1.0F;
      this.floatValue8 = -1.0F;
      this.method2091(var1, var2);
   }

   private Interface.Type method2078(float var1, float var2, float var3, float var4) {
      float var5 = this.method2081(var3);
      float var6 = this.method2084(var4);
      float var7 = this.method2083(var3);
      float var8 = this.method2086(var4);
      float var9 = var1 - this.method2064();
      float var10 = var2 - this.method2065();
      float var11 = var9 + this.method296() / 2.0F;
      float var12 = var10 + this.method297() / 2.0F;
      boolean var13 = var11 < var5 + var7 / 3.0F;
      boolean var14 = var11 > var5 + var7 * 2.0F / 3.0F;
      boolean var15 = !var13 && !var14;
      boolean var16 = var12 < var6 + var8 / 3.0F;
      boolean var17 = var12 > var6 + var8 * 2.0F / 3.0F;
      boolean var18 = !var16 && !var17;
      if (var16) {
         if (var13) {
            return Interface.Type.TOP_LEFT;
         } else {
            return var15 ? Interface.Type.TOP_CENTER : Interface.Type.TOP_RIGHT;
         }
      } else if (var18) {
         if (var13) {
            return Interface.Type.CENTER_LEFT;
         } else {
            return var15 ? Interface.Type.CENTER : Interface.Type.CENTER_RIGHT;
         }
      } else if (var13) {
         return Interface.Type.BOTTOM_LEFT;
      } else {
         return var15 ? Interface.Type.BOTTOM_CENTER : Interface.Type.BOTTOM_RIGHT;
      }
   }

   private float method2079(Interface.Type var1, float var2) {
      float var3 = this.method2081(var2);
      float var4 = this.method2082(var2);
      float var5 = var3 + this.method2083(var2) / 2.0F;
      float var6 = this.method296();

      float var7 = switch (var1) {
         case TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> var3;
         case TOP_CENTER, CENTER, BOTTOM_CENTER -> var5 - var6 / 2.0F;
         case TOP_RIGHT, CENTER_RIGHT, BOTTOM_RIGHT -> var4 - var6;
      };
      return var7 + this.method2064();
   }

   private float method2080(Interface.Type var1, float var2) {
      float var3 = this.method2084(var2);
      float var4 = this.method2085(var2);
      float var5 = var3 + this.method2086(var2) / 2.0F;
      float var6 = this.method297();

      float var7 = switch (var1) {
         case TOP_LEFT, TOP_CENTER, TOP_RIGHT -> var3;
         case CENTER_LEFT, CENTER, CENTER_RIGHT -> var5 - var6 / 2.0F;
         case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> var4 - var6;
      };
      return var7 + this.method2065();
   }

   public float method2081(float var1) {
      return Math.min(InterfaceModule.INSTANCE.method6787(), var1 / 4.0F);
   }

   public float method2082(float var1) {
      return this.method2081(var1) + this.method2083(var1);
   }

   public float method2083(float var1) {
      return Math.max(1.0F, var1 - this.method2081(var1) * 2.0F);
   }

   public float method2084(float var1) {
      return Math.min(InterfaceModule.INSTANCE.method6787(), var1 / 4.0F);
   }

   public float method2085(float var1) {
      return this.method2084(var1) + this.method2086(var1);
   }

   public float method2086(float var1) {
      return Math.max(1.0F, var1 - this.method2084(var1) * 2.0F);
   }

   public JsonObject save() {
      if (!this.method2090()) {
         this.method2091(this.method2097(this.floatValue3), this.method2098(this.floatValue4));
      }

      JsonObject var1 = new JsonObject();
      var1.addProperty("enable", this.flag);
      var1.addProperty("enabled", this.flag);
      var1.addProperty("x", this.floatValue);
      var1.addProperty("y", this.floatValue2);
      var1.addProperty("width", this.width);
      var1.addProperty("height", this.height);
      var1.addProperty("windowWidth", this.floatValue3);
      var1.addProperty("windowHeight", this.floatValue4);
      var1.addProperty("offsetX", this.floatValue12);
      var1.addProperty("offsetY", this.floatValue13);
      var1.addProperty("align", this.type2.name());
      var1.addProperty("anchored", true);
      if (Float.isFinite(this.floatValue5)) {
         var1.addProperty("relativeX", this.floatValue5);
      }

      if (Float.isFinite(this.floatValue6)) {
         var1.addProperty("relativeY", this.floatValue6);
      }

      JsonObject var2 = new JsonObject();

      for (Setting var4 : this.getSettings()) {
         var4.safe(var2);
      }

      var1.add("Settings", var2);
      return var1;
   }

   public void load(JsonObject var1) {
      boolean var2 = false;
      boolean var3 = false;
      boolean var4 = false;
      if (var1.has("enable")) {
         this.flag = var1.get("enable").getAsBoolean();
      } else if (var1.has("enabled")) {
         this.flag = var1.get("enabled").getAsBoolean();
      }

      if (var1.has("x")) {
         this.floatValue = var1.get("x").getAsFloat();
      }

      if (var1.has("y")) {
         this.floatValue2 = var1.get("y").getAsFloat();
      }

      if (var1.has("width")) {
         this.width = var1.get("width").getAsFloat();
      }

      if (var1.has("height")) {
         this.height = var1.get("height").getAsFloat();
      }

      if (var1.has("windowWidth")) {
         this.floatValue3 = var1.get("windowWidth").getAsFloat();
      }

      if (var1.has("windowHeight")) {
         this.floatValue4 = var1.get("windowHeight").getAsFloat();
      }

      if (var1.has("offsetX")) {
         this.floatValue12 = var1.get("offsetX").getAsFloat();
         var3 = true;
      }

      if (var1.has("offsetY")) {
         this.floatValue13 = var1.get("offsetY").getAsFloat();
         var4 = true;
      }

      if (var1.has("align")) {
         try {
            this.type2 = Interface.Type.valueOf(var1.get("align").getAsString());
            var2 = true;
         } catch (IllegalArgumentException var10) {
            this.type2 = Interface.Type.TOP_LEFT;
         }
      }

      if (var1.has("relativeX")) {
         this.floatValue5 = var1.get("relativeX").getAsFloat();
      }

      if (var1.has("relativeY")) {
         this.floatValue6 = var1.get("relativeY").getAsFloat();
      }

      boolean var5 = this.method2090() && !var1.has("anchored");
      if (var1.has("Settings") && var1.get("Settings").isJsonObject()) {
         JsonObject var6 = var1.getAsJsonObject("Settings");

         for (Setting var8 : this.getSettings()) {
            if (var6.has(var8.getKey()) || var8 instanceof GroupSetting) {
               var8.load(var6);
            }
         }
      }

      if (!var2 || !var3 || !var4) {
         float var11 = this.method2097(this.floatValue3);
         float var13 = this.method2098(this.floatValue4);
         this.method2094(var11, var13);
      }

      if (!this.method2090()) {
         this.method2091(this.method2097(this.floatValue3), this.method2098(this.floatValue4));
      }

      if (var5 && mc != null && mc.getWindow() != null) {
         float var12 = mc.getWindow().getScaledWidth();
         float var14 = mc.getWindow().getScaledHeight();
         float var15 = this.method2081(var12) + this.floatValue5 * this.method2083(var12);
         float var9 = this.method2084(var14) + this.floatValue6 * this.method2086(var14);
         this.floatValue = this.method2088(var15, var12) + this.method2064();
         this.floatValue2 = this.method2089(var9, var14) + this.method2065();
         this.floatValue3 = var12;
         this.floatValue4 = var14;
         this.method2094(var12, var14);
         this.method2091(var12, var14);
      }

      if (mc != null && mc.getWindow() != null) {
         this.method288(mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
      }
   }

   public void method2087(float var1, float var2) {
      this.floatValue = var1;
      this.floatValue2 = var2;
      float var3 = this.method2097(this.floatValue3);
      float var4 = this.method2098(this.floatValue4);
      if (var3 > 0.0F && var4 > 0.0F) {
         this.update(var3, var4);
         this.method2094(var3, var4);
      }

      this.method2091(var3, var4);
   }

   public float method2088(float var1, float var2) {
      float var3 = this.method2081(var2);
      float var4 = this.method2082(var2) - this.method296();
      if (var4 < var3) {
         var4 = var3;
      }

      return Math.max(var3, Math.min(var1, var4));
   }

   public float method2089(float var1, float var2) {
      float var3 = this.method2084(var2);
      float var4 = this.method2085(var2) - this.method297();
      if (var4 < var3) {
         var4 = var3;
      }

      return Math.max(var3, Math.min(var1, var4));
   }

   public boolean method2090() {
      return Float.isFinite(this.floatValue5) && Float.isFinite(this.floatValue6);
   }

   public void method2091(float var1, float var2) {
      if (!(var1 <= 0.0F) && !(var2 <= 0.0F)) {
         float var3 = this.method2083(var1);
         float var4 = this.method2086(var2);
         if (!(var3 <= 0.0F) && !(var4 <= 0.0F)) {
            float var5 = this.method2088(this.method294(), var1) + this.method2092() * this.method296();
            float var6 = this.method2089(this.method295(), var2) + this.method2093() * this.method297();
            float var7 = (var5 - this.method2081(var1)) / var3;
            float var8 = (var6 - this.method2084(var2)) / var4;
            this.floatValue5 = Math.max(0.0F, Math.min(1.0F, var7));
            this.floatValue6 = Math.max(0.0F, Math.min(1.0F, var8));
         }
      }
   }

   private float method2092() {
      return switch (this.type2) {
         case TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> 0.0F;
         case TOP_CENTER, CENTER, BOTTOM_CENTER -> 0.5F;
         case TOP_RIGHT, CENTER_RIGHT, BOTTOM_RIGHT -> 1.0F;
      };
   }

   private float method2093() {
      return switch (this.type2) {
         case TOP_LEFT, TOP_CENTER, TOP_RIGHT -> 0.0F;
         case CENTER_LEFT, CENTER, CENTER_RIGHT -> 0.5F;
         case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> 1.0F;
      };
   }

   public void method2094(float var1, float var2) {
      if (!(var1 <= 0.0F) && !(var2 <= 0.0F)) {
         Interface.Type var3 = this.method2078(this.getX(), this.getY(), var1, var2);
         float var4 = this.method2079(var3, var1);
         float var5 = this.method2080(var3, var2);
         this.type2 = var3;
         this.floatValue12 = this.getX() - var4;
         this.floatValue13 = this.getY() - var5;
      }
   }

   public float method2095(float var1) {
      return Math.max(0.0F, this.method2083(var1) - this.method296());
   }

   public float method2096(float var1) {
      return Math.max(0.0F, this.method2086(var1) - this.method297());
   }

   public float method2097(float var1) {
      if (var1 > 0.0F) {
         return var1;
      } else if (this.floatValue3 > 0.0F) {
         return this.floatValue3;
      } else {
         return mc != null && mc.getWindow() != null ? mc.getWindow().getScaledWidth() : var1;
      }
   }

   public float method2098(float var1) {
      if (var1 > 0.0F) {
         return var1;
      } else if (this.floatValue4 > 0.0F) {
         return this.floatValue4;
      } else {
         return mc != null && mc.getWindow() != null ? mc.getWindow().getScaledHeight() : var1;
      }
   }

   public net.minecraft.client.util.math.Vector2f method2099() {
      net.minecraft.client.util.math.Vector2f var1 = Vec2Utils.method8068(mc.getWindow().getScaleFactor());
      float var2 = this.method290();
      float var3 = this.floatValue + this.width / 2.0F;
      float var4 = this.floatValue2 + this.height / 2.0F;
      float var5 = var3 + (var1.x() - var3) / var2;
      float var6 = var4 + (var1.y() - var4) / var2;
      return new net.minecraft.client.util.math.Vector2f(var5, var6);
   }

   @Generated
   public String getName() {
      return this.text;
   }

   @Generated
   public float getX() {
      return this.floatValue;
   }

   @Generated
   public float getY() {
      return this.floatValue2;
   }

   @Generated
   public float getWidth() {
      return this.width;
   }

   @Generated
   public float getHeight() {
      return this.height;
   }

   @Generated
   public SliderSetting getScale() {
      return this.scale;
   }

   public class Entry {
      private float floatValue;
      private float offset;

      public Entry(float var2, float var3) {
         this.floatValue = var2;
         this.offset = var3;
      }

      @Generated
      public float getFloatValue() {
         return this.floatValue;
      }

      @Generated
      public float getOffset() {
         return this.offset;
      }

      @Generated
      public void setFloatValue(float var1) {
         this.floatValue = var1;
      }

      @Generated
      public void setOffset(float var1) {
         this.offset = var1;
      }
   }

   public static enum Type {
      TOP_LEFT,
      TOP_CENTER,
      TOP_RIGHT,
      CENTER_LEFT,
      CENTER,
      CENTER_RIGHT,
      BOTTOM_LEFT,
      BOTTOM_CENTER,
      BOTTOM_RIGHT;
   }
}

package org.zenith.client.hud;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.boss.BossBar.Color;
import net.minecraft.util.Identifier;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.utility.misc.Data2;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.network.Island;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.RenderShaders;
import org.zenith.utility.render.Voice;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class WatermarkIslandHud extends Interface {
   private static final float CONST_floatValue = 20.0F;
   private static final float CONST_floatValue2 = 3.5F;
   private static final float CONST_floatValue3 = 13.0F;
   private static final float CONST_floatValue4 = 11.0F;
   private static final float CONST_floatValue5 = GuiStyle.PADDING.intValue() * 1.5F;
   private static final String text = "F";
   private static final String text2 = "H";
   private static final float CONST_floatValue6 = 15.0F;
   private static final float CONST_floatValue7 = 0.22F;
   private static final float CONST_floatValue8 = 7.0F;
   private static final float CONST_floatValue9 = 9.0F;
   private static final float CONST_floatValue10 = 5.0F;
   private static final float CONST_floatValue11 = 2.5F;
   private static final long CONST_longValue = 320L;
   private static final long CONST_longValue2 = 240L;
   private final BooleanSetting island = new BooleanSetting("module.interface.watermark.island", "module.interface.watermark.island.desc", true);
   private final Map<UUID, WatermarkIslandHud.Data> items = new HashMap<>();
   private final Animation animation = new Animation(180L, 0.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(320L, 0.0F, Easing.INSTANCE4);
   private final Animation animation3 = new Animation(220L, 1.0F, Easing.INSTANCE2);
   private String text3;
   private Island.Entry entry;
   private static volatile long longValue;
   private static Object fitFont;
   private static float fitSize;
   private static float fitWidth;
   private static String text4;
   private static String text5;

   public static boolean method5668() {
      return method5669() && System.currentTimeMillis() - longValue < 500L;
   }

   public static boolean method5669() {
      if (InterfaceModule.INSTANCE.isEnabled() && mc.player != null && mc.world != null && !mc.options.hudHidden) {
         for (Interface var1 : InterfaceModule.INSTANCE.method6794()) {
            if (var1 instanceof WatermarkIslandHud var2) {
               return var2.isEnabled() && var2.island.isEnabled() && var2.method2061();
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static ZenithColor method5670(Color var0) {
      return switch (var0) {
         case PINK -> new ZenithColor(255, 95, 168, 255);
         case BLUE -> new ZenithColor(90, 169, 255, 255);
         case RED -> new ZenithColor(255, 90, 90, 255);
         case GREEN -> new ZenithColor(93, 214, 107, 255);
         case YELLOW -> new ZenithColor(255, 210, 90, 255);
         case PURPLE -> new ZenithColor(180, 107, 255, 255);
         case WHITE -> ZenithColor.INSTANCE;
      };
   }

   public WatermarkIslandHud(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void method8(CustomDrawContext var1) {
      ZenithStyle var2 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      Font var3 = Fonts.NEW_ICONS.getFont(8.06F);
      Font var4 = Fonts.NEW_ICONS.getFont(5.0F);
      Font var5 = Fonts.NEW_MEDIUM.getFont(6.0F);
      Font var6 = Fonts.NEW_REGULAR.getFont(5.4F);
      String var7 = "Zenith";
      String var8 = Zenith.getInstance().getEntry().getUsername();
      String var9 = String.valueOf(mc.getCurrentFps());
      String var10 = this.method5679();
      long var11 = System.currentTimeMillis();
      longValue = this.island.isEnabled() ? var11 : 0L;
      this.method5671();
      Island.Entry var13 = this.island.isEnabled() ? Island.method2875(var11) : null;
      if (!this.island.isEnabled()) {
         this.animation2.method2809(0.0F);
         this.animation.method2809(0.0F);
         this.animation3.method2809(1.0F);
         this.text3 = null;
         this.entry = null;
      }

      float var14 = var13 == null ? 0.0F : 1.0F;
      if (var14 == 1.0F) {
         this.animation2.setEasing(Easing.INSTANCE4);
         this.animation2.setDuration(320L);
      } else {
         this.animation2.setEasing(Easing.INSTANCE14);
         this.animation2.setDuration(240L);
      }

      float var15 = this.animation2.method2808(var14);
      if (var13 != null) {
         if (!var13.text.equals(this.text3)) {
            if (this.text3 != null) {
               this.animation3.method2809(0.0F);
            }

            float var16 = var13.method2884(var11);
            this.animation.method2809(var16 < 0.0F ? 0.0F : var16);
            this.text3 = var13.text;
         }

         this.entry = var13;
      } else if (var15 < 0.01F) {
         this.text3 = null;
         this.entry = null;
      }

      Island.Entry var27 = var13 != null ? var13 : this.entry;
      float var17 = var13 != null ? this.animation3.method2808(1.0F) : 1.0F;
      this.width = 16.5F
         + this.dividerWidth()
         + var5.width(var7)
         + this.dividerWidth()
         + 11.0F
         + GuiStyle.PADDING.intValue()
         + var5.width(var8)
         + this.dividerWidth()
         + this.method5677(var4, var5, var6, "F", var9, " fps")
         + this.dividerWidth()
         + this.method5677(var4, var5, var6, "H", var10, " ms")
         + CONST_floatValue5;
      this.height = 20.0F;
      float var18 = 15.0F * var15;
      ProxyDotRadiusData var19 = ProxyDotRadiusData.method8069(Math.min(InterfaceModule.method6801(), 10.0F));
      boolean var20 = var18 > 0.5F && var27 != null;
      float var21 = 20.0F + (var20 ? var18 : 0.0F);
      if (var20) {
         float var22 = Math.min(InterfaceModule.method6801(), 7.5F);
         RenderShaders.floatValues = new float[]{
            this.method5672(), this.floatValue2 + 20.0F - 9.0F, this.method5673(), var18 + 9.0F, Math.min(var22, var18), this.floatValue2 + 20.0F, 5.0F
         };
      }

      boolean var28 = (InterfaceModule.INSTANCE.method6817() || InterfaceModule.INSTANCE.method6818()) && RenderShaders.flag;
      boolean var23 = InterfaceModule.INSTANCE.method6819();
      var1.drawBlurHudBooleanCheck(this.floatValue, this.floatValue2, this.width, var21, 12.0F, var19, ZenithColor.INSTANCE, var28, false);
      var1.drawRoundedRect(
         this.floatValue,
         this.floatValue2,
         this.width,
         var21,
         var19,
         var2.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778())
      );
      RenderShaders.floatValues = null;
      if (var23) {
         var1.drawBlurHudBooleanCheck(this.floatValue, this.floatValue2, this.width, 20.0F, 12.0F, var19, ZenithColor.INSTANCE, false, true);
         if (var20 && var18 > 2.0F) {
            var1.drawBlurHudBooleanCheck(
               this.method5672(),
               this.floatValue2 + 20.0F - 2.0F,
               this.method5673(),
               var18 + 2.0F,
               12.0F,
               ProxyDotRadiusData.method8069(Math.min(InterfaceModule.method6801(), 7.5F)),
               ZenithColor.INSTANCE,
               false,
               true
            );
         }
      }

      if (var20) {
         this.method5674(var1, var2, var27, var18, var15, var17, var11);
      }

      float var24 = this.floatValue + 3.5F;
      float var25 = this.floatValue2 + 3.5F;
      var1.drawRoundedRect(var24, var25, 13.0F, 13.0F, ProxyDotRadiusData.method8069(3.9F), var2.getPrimaryColor().getZenithColor());
      HudListLayout.method7637(var1, var3, "1", var24, var25, 13.0F, ZenithColor.INSTANCE);
      var24 += 13.0F;
      var24 = this.method5676(var1, var24, var2);
      var1.drawText(var5, var7, var24, this.floatValue2 + (20.0F - var5.height()) / 2.0F, var2.getTextEnable().getZenithColor());
      var24 += var5.width(var7);
      var24 = this.method5676(var1, var24, var2);
      float var26 = this.floatValue2 + 4.5F;
      var1.drawPlayerHeadWithRoundedShader(this.method5680(), var24, var26, 11.0F, ProxyDotRadiusData.method8069(5.5F), ZenithColor.INSTANCE);
      var1.drawRoundedBorder(
         var24 - 1.0F, var26 - 1.0F, 13.0F, 13.0F, 1.0F, ProxyDotRadiusData.method8069(6.5F), var2.getPrimaryColor().getZenithColor().method7294(0.55F)
      );
      var24 += 11.0F + GuiStyle.PADDING.intValue();
      var1.drawText(var5, var8, var24, this.floatValue2 + (20.0F - var5.height()) / 2.0F, var2.getTextEnable().getZenithColor());
      var24 += var5.width(var8);
      var24 = this.method5678(var1, var24, var4, var5, var6, "F", var9, " fps", var2);
      this.method5678(var1, var24, var4, var5, var6, "H", var10, " ms", var2);
   }

   private void method5671() {
      if (this.island.isEnabled() && mc.inGameHud != null && mc.player != null && mc.world != null) {
         var var1 = mc.inGameHud.getBossBarHud().bossBars;
         this.items.keySet().retainAll(var1.keySet());
         ClientBossBar var2 = null;
         Data2 var3 = null;

         for (Object var5_o : (Iterable<?>)(Object)(var1.entrySet())) {
            Map.Entry var5 = (Map.Entry)var5_o;
            ClientBossBar var6 = (ClientBossBar)var5.getValue();
            String var7 = var6.getName().getString();
            WatermarkIslandHud.Data var8 = this.items.get(var5.getKey());
            if (var8 == null || !var8.text().equals(var7)) {
               var8 = new WatermarkIslandHud.Data(var7, Data2.method1515(var7));
               this.items.put((UUID)var5.getKey(), var8);
            }

            Data2 var9 = var8.getData2();
            if (var2 == null
               || var9.isFlag() && !var3.isFlag()
               || var9.isFlag() && var3.isFlag() && var9.getIntValue() > var3.getIntValue()
               || !var9.isFlag() && !var3.isFlag() && var9.getIntValue() >= 0 && var3.getIntValue() < 0) {
               var2 = var6;
               var3 = var9;
            }
         }

         if (var2 == null) {
            Island.drop("boss");
         } else {
            String var10 = var3.isFlag() ? Zenith.getInstance().getLocalization().translate("hud.island.combat") : var3.title();
            if (var1.size() > 1) {
               var10 = var10 + "  +" + (var1.size() - 1);
            }

            Island.method2872(var10, method5670(var2.getColor()), var2.getPercent(), var3.value());
         }
      } else {
         this.items.clear();
         Island.drop("boss");
      }
   }

   private float method5672() {
      return this.floatValue + this.width * 0.22F / 2.0F;
   }

   private float method5673() {
      return this.width - this.width * 0.22F;
   }

   private void method5674(CustomDrawContext var1, ZenithStyle var2, Island.Entry var3, float var4, float var5, float var6, long var7) {
      float var9 = this.method5672();
      float var10 = this.method5673();
      float var11 = this.floatValue2 + 20.0F;
      float var12 = Math.clamp((var5 - 0.45F) / 0.55F, 0.0F, 1.0F) * Math.clamp(var6, 0.0F, 1.0F);
      if (!(var12 <= 0.01F)) {
         float var13 = (1.0F - Math.clamp(var6, 0.0F, 1.0F)) * 3.0F;
         Font var14 = Fonts.NEW_MEDIUM.getFont(5.4F);
         if (var3.flag2) {
            this.method5675(var1, var2, var3, var14, var9, var10, var11, var12, var13);
         } else {
            Font var15 = Fonts.NEW_SEMIBOLD.getFont(5.0F);
            String var16 = var3.method2885(var7);
            float var17 = var15.width(var16);
            String var18 = fit(var14, var3.title, var10 - 14.0F - var17 - CONST_floatValue5 * 2.0F);
            float var19 = var11 + 2.0F + var13;
            var1.drawText(var14, var18, var9 + 7.0F, var19, var2.getTextEnable().getZenithColor().method7294(var12));
            if (!var16.isEmpty()) {
               var1.drawText(var15, var16, var9 + var10 - 7.0F - var17, var19, var2.getTextTertiary().getZenithColor().method7294(var12));
            }

            float var20 = var3.method2884(var7);
            if (!(var20 < 0.0F)) {
               float var21 = var11 + 15.0F - 2.5F - 3.0F;
               float var22 = var9 + 7.0F;
               float var23 = var10 - 14.0F;
               ProxyDotRadiusData var24 = ProxyDotRadiusData.method8069(1.25F);
               var1.drawRoundedRect(var22, var21, var23, 2.5F, var24, var2.getHeaderHudBackground().getZenithColor().method7294(var12));
               float var25 = Math.clamp(this.animation.method2808(var20), 0.0F, 1.0F);
               if (var25 > 0.001F) {
                  ZenithColor var26 = var2.getPrimaryColor()
                     .getZenithColor()
                     .method7295(var2.getSecondaryPrimaryColor().getZenithColor(), Math.clamp(var25, 0.0F, 1.0F));
                  ZenithColor var27 = var3.flag && var25 < 0.2F ? var26.method7295(new ZenithColor(255, 90, 74, 255), 1.0F - var25 / 0.2F) : var26;
                  var1.drawRoundedRect(var22, var21, Math.max(2.5F, var23 * var25), 2.5F, var24, var27.method7294(var12));
               }

               float var30 = Math.clamp(var6, 0.0F, 1.0F);
               if (var30 < 0.999F) {
                  float var31 = var10 * 0.28F;
                  float var28 = var9 - var31 + (var10 + var31) * var30;
                  float var29 = Math.min(var28 + var31, var9 + var10) - Math.max(var28, var9);
                  if (var29 > 0.5F) {
                     var1.drawRoundedRect(
                        Math.max(var28, var9),
                        var11 + 1.5F,
                        var29,
                        12.0F,
                        ProxyDotRadiusData.method8069(6.0F),
                        ZenithColor.INSTANCE.method7294(var12 * 0.1F * (1.0F - var30))
                     );
                  }
               }
            }
         }
      }
   }

   private void method5675(CustomDrawContext var1, ZenithStyle var2, Island.Entry var3, Font var4, float var5, float var6, float var7, float var8, float var9) {
      float var10 = var7 + 7.5F - 0.5F + var9;
      float var11 = 4.0F;
      ZenithColor var12 = var3.zenithColor == null ? var2.getPrimaryColor().getZenithColor() : var3.zenithColor;
      var1.drawRoundedRect(var5 + 7.0F, var10 - var11 / 2.0F, var11, var11, ProxyDotRadiusData.method8069(var11 / 2.0F), var12.method7294(var8));
      boolean var13 = !Float.isNaN(var3.floatValue2);
      float var14 = var13 ? 11.0F : 0.0F;
      float var15 = var5 + 7.0F + var11 + 4.0F;
      float var16 = var5 + var6 - 7.0F - var14 - (var13 ? CONST_floatValue5 : 0.0F) - var15;
      String var17 = fit(var4, var3.title, var16);
      var1.drawText(var4, var17, var15, var10 - var4.height() / 2.0F, var2.getTextEnable().getZenithColor().method7294(var8));
      if (var13) {
         Voice.method371(var1, var5 + var6 - 7.0F - var14, var10, var3.floatValue2, var12.method7294(var8));
      }
   }

   private static String fit(Font var0, String var1, float var2) {
      if (var2 <= 0.0F || var0.width(var1) <= var2) {
         return var1;
      } else if (text5 != null && var0.getFont() == fitFont && var0.getSize() == fitSize && var2 == fitWidth && var1.equals(text4)) {
         return text5;
      } else {
         String var3 = var1;

         while (var3.length() > 1 && var0.width(var3 + "...") > var2) {
            var3 = var3.substring(0, var3.length() - 1);
         }

         fitFont = var0.getFont();
         fitSize = var0.getSize();
         fitWidth = var2;
         text4 = var1;
         text5 = var3 + "...";
         return text5;
      }
   }

   private float dividerWidth() {
      return CONST_floatValue5 * 2.0F + 1.0F;
   }

   private float method5676(CustomDrawContext var1, float var2, ZenithStyle var3) {
      float var4 = 9.0F;
      var1.drawRect(var2 + CONST_floatValue5, this.floatValue2 + (20.0F - var4) / 2.0F, 1.0F, var4, var3.getTextTertiary().getZenithColor().method7294(0.45F));
      return var2 + this.dividerWidth();
   }

   private float method5677(Font var1, Font var2, Font var3, String var4, String var5, String var6) {
      return var1.width(var4) + 2.0F + var2.width(var5) + var3.width(var6);
   }

   private float method5678(CustomDrawContext var1, float var2, Font var3, Font var4, Font var5, String var6, String var7, String var8, ZenithStyle var9) {
      var2 = this.method5676(var1, var2, var9);
      var1.drawText(var3, var6, var2, this.floatValue2 + (20.0F - var3.height()) / 2.0F, var9.getTextTertiary().getZenithColor());
      var2 += var3.width(var6) + 2.0F;
      var1.drawText(var4, var7, var2, this.floatValue2 + (20.0F - var4.height()) / 2.0F, var9.getTextEnable().getZenithColor());
      var2 += var4.width(var7);
      var1.drawText(var5, var8, var2, this.floatValue2 + (20.0F - var5.height()) / 2.0F, var9.getTextTertiary().getZenithColor());
      return var2 + var5.width(var8);
   }

   private String method5679() {
      if (mc.player != null && mc.getNetworkHandler() != null) {
         PlayerListEntry var1 = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
         return var1 == null ? "-" : String.valueOf(Math.max(0, var1.getLatency()));
      } else {
         return "-";
      }
   }

   private Identifier method5680() {
      ClientPlayerEntity var2 = mc.player;
      return var2 instanceof AbstractClientPlayerEntity && var2.getSkin() != null
         ? var2.getSkin().body().texturePath()
         : DefaultSkinHelper.getSteve().body().texturePath();
   }

   private static final class Data {
      private final String text;
      private final Data2 data2;

      public String text() {
         return this.text;
      }

      public Data2 data2() {
         return this.data2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.data2, other.data2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, data2);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "data2=" + this.data2 + "]";
      }

      private Data(String var1, Data2 var2) {
         this.text = var1;
         this.data2 = var2;
      }

      public Data2 getData2() {
         return this.data2;
      }
   }
}

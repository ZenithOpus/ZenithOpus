package org.zenith.client.hud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.api.PartyMember;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.network.Cloud;
import org.zenith.utility.network.CloudPackets;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.PartyManager;
import org.zenith.utility.render.Voice;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class PartyHud extends Interface {
   private static final String text = "P";
   private static final float CONST_floatValue = 24.0F;
   private static final float CONST_floatValue2 = 14.0F;
   private static final float CONST_floatValue3 = 118.0F;
   private static final ZenithColor zenithColor = new ZenithColor(-495247);
   private static final ZenithColor zenithColor2 = new ZenithColor(-278748);
   private final Animation animation = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(200L, 118.0F, Easing.INSTANCE13);
   private final Map<Long, Animation> items = new HashMap<>();
   private final Map<String, PlayerListEntry> items2 = new HashMap<>();
   private final Map<String, Long> items3 = new HashMap<>();
   private static final long CONST_longValue = 2000L;
   private long longValue;
   private final Map<String, Integer> items4 = new HashMap<>();

   public PartyHud(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   private List<PartyHud.Data> method6106(Cloud var1) {
      ArrayList<PartyHud.Data> var2 = new ArrayList<>();
      PartyMember.Data6 var3 = var1 == null ? null : var1.getPartyMember().getData6();
      if (var3 == null) {
         return var2;
      } else {
         for (PartyMember.Data3 var5 : var3.getItems()) {
            if (var5.uid() != var1.getLongValue()) {
               var2.add(
                  new PartyHud.Data(
                     var5.uid(),
                     var5.username(),
                     var5.getText2(),
                     var5.isFlag(),
                     var5.isFlag() ? var1.state().method8040(var5.uid()) : null,
                     var3.method1215(var5.uid())
                  )
               );
            }
         }

         var2.sort((var0, var1x) -> Boolean.compare(var1x.isFlag(), var0.isFlag()));
         return var2;
      }
   }

   @Override
   public void method8(CustomDrawContext var1) {
      Cloud var2 = Zenith.getInstance().getCloud();
      List<PartyHud.Data> var3 = this.method6106(var2);
      long var4 = System.currentTimeMillis();
      if (var4 - this.longValue > 30000L) {
         this.items2.clear();
         this.items3.clear();
         this.longValue = var4;
         if (this.items.size() > var3.size()) {
            HashSet var6 = new HashSet();

            for (Object var8_o : (Iterable<?>)(Object)(var3)) {
               PartyHud.Data var8 = (PartyHud.Data)var8_o;
               var6.add(var8.uid());
            }

            this.items.keySet().retainAll(var6);
         }
      }

      PartyMember.Data6 var19 = var2 == null ? null : var2.getPartyMember().getData6();
      String var20 = var19 == null ? "Party" : var19.name() + " · " + var19.method1217() + "/" + var19.getItems().size();
      Font var21 = HudListLayout.nameFont();
      Font var9 = Fonts.NEW_REGULAR.getFont(4.6F);
      float var10 = HudListLayout.method7638("P", var20);

      for (Object var12_o : (Iterable<?>)(Object)(var3)) {
         PartyHud.Data var12 = (PartyHud.Data)var12_o;
         var10 = Math.max(var10, 26.0F + var21.width(var12.name()) + 34.0F + 6.0F);
      }

      float var22 = this.animation2.method2808(Math.max(118.0F, var10));
      float var23 = 20.0F + GuiStyle.PADDING.intValue() / 2.0F + var3.size() * 24.0F + (var3.isEmpty() ? 0.0F : 2.0F);
      float var13 = this.animation.method2808(var3.isEmpty() && (!(mc.currentScreen instanceof ChatScreen) || var19 == null) ? 0.0F : 1.0F);
      if (!(var13 <= 0.01F)) {
         this.width = var22;
         this.height = var23;
         float var14 = this.floatValue;
         float var15 = this.floatValue2;
         var1.pushMatrix();
         var1.getMatrices().translate(var14 + var22 / 2.0F, var15 + var23 / 2.0F);
         var1.getMatrices().scale(var13, var13);
         var1.getMatrices().translate(-(var14 + var22 / 2.0F), -(var15 + var23 / 2.0F));
         HudListLayout.method7635(var1, var14, var15, var22, var23);
         HudListLayout.method7636(var1, var14, var15, var22, "P", var20);
         float var16 = var15 + 20.0F + GuiStyle.PADDING.intValue() / 4.0F;

         for (Object var18_o : (Iterable<?>)(Object)(var3)) {
            PartyHud.Data var18 = (PartyHud.Data)var18_o;
            this.method6107(var1, var2, var18, var14, var16, var22, var21, var9);
            var16 += 24.0F;
         }

         var1.popMatrix();
      }
   }

   private void method6107(CustomDrawContext var1, Cloud var2, PartyHud.Data var3, float var4, float var5, float var6, Font var7, Font var8) {
      ZenithStyle var9 = HudListLayout.style();
      float var10 = var3.isFlag() ? 1.0F : 0.45F;
      float var11 = var4 + 6.0F;
      float var12 = var5 + 5.0F;
      var1.drawPlayerHeadWithRoundedShader(
         this.method6114(var3.getText2()), var11, var12, 14.0F, ProxyDotRadiusData.method8069(4.2000003F), ZenithColor.INSTANCE.method7294(var10)
      );
      String var13 = "online";
      if (var2 != null && var3.uid() > 0L && var2.getPartyMember().method1142(var3.uid())) {
         var13 = var2.getPartyMember().method1141(var3.uid()).getText();
      }

      float var14 = 4.0F;
      var1.drawRoundedRect(
         var11 + 14.0F - var14 + 1.0F,
         var12 + 14.0F - var14 + 1.0F,
         var14,
         var14,
         ProxyDotRadiusData.method8069(var14 / 2.0F),
         var3.isFlag() ? PartyManager.method51(var13) : var9.getTextTertiary().getZenithColor()
      );
      float var15 = var11 + 14.0F + 5.0F;
      float var16 = var5 + 3.5F;
      String var17 = var3.isFlag2() ? "★ " + var3.name() : var3.name();
      var1.drawText(var7, var17, var15, var16, var9.getTextEnable().getZenithColor().method7294(var10));
      String var18 = this.method6109(var2, var3);
      float var19 = var4 + var6 - 6.0F;
      if (!var18.isEmpty()) {
         var1.drawText(var8, var18, var19 - var8.width(var18), var16 + 0.5F, var9.getTextTertiary().getZenithColor().method7294(var10));
         float var20 = this.method6111(var2, var3);
         if (!Float.isNaN(var20)) {
            method6112(
               var1, var19 - var8.width(var18) - 6.0F, var16 + var8.height() / 2.0F + 0.5F, var20, var9.getPrimaryColor().getZenithColor().method7294(var10)
            );
         }
      }

      Voice var29 = Zenith.getInstance().getVoice();
      if (var29 != null && var3.uid() > 0L && var29.getVoiceState().method5365(var3.uid())) {
         Voice.method371(
            var1,
            var15 + var7.width(var17) + 4.0F,
            var16 + var7.height() / 2.0F,
            var29.getVoiceState().method5366(var3.uid()),
            var9.getPrimaryColor().getZenithColor()
         );
      }

      float var21 = var5 + 24.0F - 8.5F;
      float var22 = var4 + var6 - 6.0F - var15 - 26.0F;
      PartyPlayerInfo var23 = var3.getPartyPlayerInfo();
      float var24 = var23 == null ? 0.0F : method6110(var23.getFloatValue3(), 0.0F);
      float var25 = var23 == null ? 20.0F : Math.max(1.0F, method6110(var23.getFloatValue4(), 20.0F));
      float var26 = var23 == null ? 0.0F : method6110(var23.getFloatValue5(), 0.0F);
      float var27 = this.items
         .computeIfAbsent(var3.uid(), var0 -> new Animation(260L, 0.0F, Easing.INSTANCE12))
         .method2808(MathHelper.clamp(var24 / var25, 0.0F, 1.0F));
      var1.drawRoundedRect(var15, var21, var22, 3.0F, ProxyDotRadiusData.method8069(1.5F), var9.getHeaderHudBackground().getZenithColor().method7294(var10));
      var1.drawRoundedRect(var15, var21, var22 * var27, 3.0F, ProxyDotRadiusData.method8069(1.5F), zenithColor.method7294(var10));
      if (var26 > 0.0F) {
         float var28 = MathHelper.clamp(var26 / var25, 0.0F, 1.0F);
         var1.drawRoundedRect(var15, var21 - 1.5F, var22 * var28, 1.0F, ProxyDotRadiusData.method8069(0.5F), zenithColor2.method7294(var10));
      }

      String var30 = var23 == null ? (var3.isFlag() ? "?" : "—") : String.valueOf(Math.round(var24 + var26));
      var1.drawText(var8, var30, var15 + var22 + 3.0F, var21 - var8.height() / 2.0F + 1.5F, var9.getTextSecondary().getZenithColor().method7294(var10));
      this.method6108(var1, var23, var4 + var6 - 6.0F - 16.0F, var21 - 2.5F, var10);
   }

   private void method6108(CustomDrawContext var1, PartyPlayerInfo var2, float var3, float var4, float var5) {
      if (var2 != null) {
         CloudPackets.Data7[] var6 = var2.getData7s();
         int var7 = 0;
         int var8 = 0;

         for (int var9 = 36; var9 <= 39 && var9 < var6.length; var9++) {
            CloudPackets.Data7 var10 = var6[var9];
            if (var10 != null && !var10.isEmpty()) {
               var7++;
               if (this.items4.size() > 256 && !this.items4.containsKey(var10.getText())) {
                  this.items4.clear();
               }

               int var11 = this.items4.computeIfAbsent(var10.getText(), var0 -> {
                  Identifier var1x = Identifier.tryParse(var0);
                  if (var1x != null && Registries.ITEM.containsId(var1x)) {
                     ItemStack var2x = new ItemStack(Registries.ITEM.get(var1x));
                     return var2x.isDamageable() && var2x.getMaxDamage() > 0 ? var2x.getMaxDamage() : 0;
                  } else {
                     return 0;
                  }
               });
               if (var11 > 0 && var10.getIntValue2() > var11 * 0.75F) {
                  var8++;
               }
            }
         }

         ZenithStyle var12 = HudListLayout.style();

         for (int var13 = 0; var13 < 4; var13++) {
            ZenithColor var14 = var13 < var7
               ? (var13 < var8 ? zenithColor : var12.getPrimaryColor().getZenithColor())
               : var12.getHeaderHudBackground().getZenithColor();
            var1.drawRoundedRect(var3 + var13 * 4.0F, var4, 3.0F, 3.0F, ProxyDotRadiusData.method8069(0.8F), var14.method7294(var5));
         }
      }
   }

   private String method6109(Cloud var1, PartyHud.Data var2) {
      PartyPlayerInfo var3 = var2.getPartyPlayerInfo();
      if (var1 != null && var3 != null && mc.player != null) {
         if (!var3.method3291(var1.method7911(), var1.method7913())) {
            String var12 = var3.server();
            return var12.isEmpty() ? "" : (var12.startsWith("singleplayer") ? "SP" : method6113(var12));
         } else {
            double var4 = var3.getDoubleValue() - mc.player.getX();
            double var6 = var3.getDoubleValue3() - mc.player.getZ();
            double var8 = var3.getDoubleValue2() - mc.player.getY();
            double var10 = Math.sqrt(var4 * var4 + var8 * var8 + var6 * var6);
            return Double.isFinite(var10) && var10 < 1.0E8 ? Math.round(var10) + "m" : "";
         }
      } else {
         return var2.isFlag() ? "" : method6115("hud.party.offline");
      }
   }

   private static float method6110(float var0, float var1) {
      return Float.isFinite(var0) ? var0 : var1;
   }

   private float method6111(Cloud var1, PartyHud.Data var2) {
      PartyPlayerInfo var3 = var2.getPartyPlayerInfo();
      if (var1 != null && var3 != null && mc.player != null && var3.method3291(var1.method7911(), var1.method7913())) {
         double var4 = var3.getDoubleValue() - mc.player.getX();
         double var6 = var3.getDoubleValue3() - mc.player.getZ();
         if (var4 * var4 + var6 * var6 < 4.0) {
            return Float.NaN;
         } else {
            float var8 = (float)Math.toDegrees(Math.atan2(var6, var4)) - 90.0F;
            return MathHelper.wrapDegrees(var8 - mc.player.getYaw());
         }
      } else {
         return Float.NaN;
      }
   }

   private static void method6112(CustomDrawContext var0, float var1, float var2, float var3, ZenithColor var4) {
      var0.pushMatrix();
      var0.getMatrices().translate(var1, var2);
      var0.getMatrices().rotate((float)Math.toRadians(var3));

      for (byte var5 = -1; var5 <= 1; var5 += 2) {
         var0.pushMatrix();
         var0.getMatrices().rotate((float)Math.toRadians(35 * var5));
         var0.drawRect(-0.5F, -2.8F, 1.0F, 3.2F, var4);
         var0.popMatrix();
      }

      var0.drawRect(-0.5F, -2.6F, 1.0F, 5.0F, var4);
      var0.popMatrix();
   }

   private static String method6113(String var0) {
      int var1 = var0.indexOf(46);
      String var2 = var1 > 0 ? var0.substring(0, var1) : var0;
      return var2.length() > 10 ? var2.substring(0, 10) : var2;
   }

   private Identifier method6114(String var1) {
      String var2 = var1 == null ? "" : var1.toLowerCase(Locale.ROOT);
      PlayerListEntry var3 = this.items2.get(var2);
      if (var3 != null && var3.getSkinTextures() != null) {
         return var3.getSkinTextures().body().texturePath();
      } else {
         long var4 = System.currentTimeMillis();
         Long var6 = this.items3.get(var2);
         if (var6 != null && var4 - var6 < 2000L) {
            return DefaultSkinHelper.getSteve().body().texturePath();
         } else {
            if (mc.getNetworkHandler() != null) {
               for (PlayerListEntry var8 : mc.getNetworkHandler().getPlayerList()) {
                  if (var8.getProfile() != null && var8.getProfile().name().equalsIgnoreCase(var2) && var8.getSkinTextures() != null) {
                     this.items2.put(var2, var8);
                     this.items3.remove(var2);
                     return var8.getSkinTextures().body().texturePath();
                  }
               }
            }

            this.items3.put(var2, var4);
            return DefaultSkinHelper.getSteve().body().texturePath();
         }
      }
   }

   private static String method6115(String var0) {
      return Zenith.getInstance().getLocalization().translate(var0);
   }

   private static final class Data {
      private final long longValue;
      private final String text;
      private final String text2;
      private final boolean flag;
      private final PartyPlayerInfo partyPlayerInfo;
      private final boolean flag2;

      public long longValue() {
         return this.longValue;
      }

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public boolean flag() {
         return this.flag;
      }

      public PartyPlayerInfo partyPlayerInfo() {
         return this.partyPlayerInfo;
      }

      public boolean flag2() {
         return this.flag2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.longValue, other.longValue) && java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.partyPlayerInfo, other.partyPlayerInfo) && java.util.Objects.equals(this.flag2, other.flag2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(longValue, text, text2, flag, partyPlayerInfo, flag2);
      }

      @Override
      public String toString() {
         return "Data[" + "longValue=" + this.longValue + ", " + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "flag=" + this.flag + ", " + "partyPlayerInfo=" + this.partyPlayerInfo + ", " + "flag2=" + this.flag2 + "]";
      }

      private Data(long var1, String var3, String var4, boolean var5, PartyPlayerInfo var6, boolean var7) {
         this.longValue = var1;
         this.text = var3;
         this.text2 = var4;
         this.flag = var5;
         this.partyPlayerInfo = var6;
         this.flag2 = var7;
      }

      public long uid() {
         return this.longValue;
      }

      public String name() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public PartyPlayerInfo getPartyPlayerInfo() {
         return this.partyPlayerInfo;
      }

      public boolean isFlag2() {
         return this.flag2;
      }
   }
}

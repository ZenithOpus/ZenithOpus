package org.zenith.client.hud;

import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.Generated;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.misc.HudListLayout;
import org.zenith.utility.misc.TextUtils;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

public class StaffListHud extends Interface {
   private static final float CONST_floatValue = 20.0F;
   private static final float CONST_floatValue2 = 14.0F;
   private static final String text = "P";
   private static final String text2 = "Staffs";
   private final Animation animation = new Animation(200L, 100.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Animation animation3 = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Map<String, StaffListHud.Entry2> items = new LinkedHashMap<>();
   private final Set<String> items2 = Set.of("helper", "ᴀдмин", "moder", "staff", "admin", "curator", "стажёр", "сотрудник", "помощник", "админ", "модер");
   private final Map<String, PlayerListEntry> items3 = new HashMap<>();
   private long longValue = 0L;
   private long longValue2 = 0L;
   private final Set<String> items4 = new HashSet<>();

   public StaffListHud(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void method8(CustomDrawContext var1) {
      long var2 = System.currentTimeMillis();
      if (var2 - this.longValue > 1000L && mc.getNetworkHandler() != null) {
         this.method4481();
         this.longValue = var2;
      }

      if (var2 - this.longValue2 > 30000L) {
         this.items3.clear();
         this.longValue2 = var2;
      }

      this.items.entrySet().removeIf(var0 -> var0.getValue().method4488());
      if (this.items.isEmpty()) {
         this.animation2.method2808(0.0F);
      } else {
         StaffListHud.Entry2 var4 = this.items.values().iterator().next();
         this.animation2.method2808(this.items.size() == 1 && var4.animation.getFloatValue2() == 0.0F ? 0.0F : 1.0F);
      }

      float var13 = this.floatValue;
      float var5 = this.floatValue2;
      float var6 = (float)(
         20.0F + GuiStyle.PADDING.intValue() / 2.0F
            + this.items.values().stream().mapToDouble(var0 -> (var0.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * var0.animation.getValue()).sum()
      );
      float var7 = (float)this.items.values().stream().mapToDouble(StaffListHud.Entry2::method4485).max().orElse(0.0);
      var7 = this.animation.method2808(Math.max(HudListLayout.method7638("P", "Staffs"), var7));
      this.width = var7;
      this.height = var6;
      float var8 = InterfaceModule.method6801();
      this.animation3.method2807(mc.currentScreen instanceof ChatScreen || Zenith.getInstance().getNLMenuScreen().isRenderHud() || !this.items.isEmpty());
      var1.pushMatrix();
      var1.getMatrices().translate(var13 + var7 / 2.0F, var5 + var6 / 2.0F);
      var1.getMatrices().scale(this.animation3.getValue(), this.animation3.getValue());
      var1.getMatrices().translate(-(var13 + var7 / 2.0F), -(var5 + var6 / 2.0F));
      HudListLayout.method7635(var1, var13, var5, var7, var6);
      HudListLayout.method7636(var1, var13, var5, var7, "P", "Staffs");
      if (this.animation3.getValue() == 1.0F) {
         float var9 = var5 + 20.0F;
         var1.enableScissor((int)var13, (int)var5, (int)(var13 + var7), (int)(var5 + var6));

         for (Map.Entry var11 : this.items.entrySet()) {
            StaffListHud.Entry2 var12 = (StaffListHud.Entry2)var11.getValue();
            var12.method4487(var1, var13, var9, var7, this.items4.contains(var11.getKey()));
            var9 += (var12.getHeight() + GuiStyle.PADDING.intValue() / 2.0F) * var12.animation.getValue();
         }

         var1.disableScissor();
      }

      var1.popMatrix();
   }

   private void method4481() {
      if (mc.getNetworkHandler() != null) {
         this.items4.clear();

         for (PlayerListEntry var2 : mc.getNetworkHandler().getPlayerList()) {
            GameProfile var3 = var2.getProfile();
            Text var4 = var2.getDisplayName();
            if (var3 != null) {
               String var5 = var3.name();
               boolean var6 = Zenith.getInstance().getStaffName().method1668(var5);
               if (var4 != null || var6) {
                  String var7 = var4 != null ? var4.getString() : var5;
                  String var8 = var7.replace(var5, "").trim();
                  if (var6 || this.method4482(var8) && var8.length() >= 2) {
                     StaffListHud.Type var9 = var2.getGameMode() == GameMode.SPECTATOR ? StaffListHud.Type.VANISHED : StaffListHud.Type.NONE;
                     if (var4 != null) {
                        var4 = var4.getString().contains(var3.name()) ? TextUtils.method2118(var4, var3.name(), false) : TextUtils.method2108(var4, false);
                     } else {
                        var4 = Text.of(var5);
                     }

                     Text var10 = var4;
                     this.items.computeIfAbsent(var7, var5x -> new StaffListHud.Entry2(var10, var7, var5, var9));
                     this.items4.add(var7);
                  }
               }
            }
         }
      }
   }

   public boolean method4482(String var1) {
      String var2 = var1.toLowerCase(Locale.US);

      for (String var4 : this.items2) {
         if (var2.contains(var4)) {
            return true;
         }
      }

      return false;
   }

   private String method4483(long var1) {
      long var3 = var1 / 60000L;
      long var5 = var1 % 60000L / 1000L;
      return String.format("%d:%02d", var3, var5);
   }

   @Override
   public float method254() {
      return this.animation3.getValue();
   }

   public static class Entry {
      private Text text;
      private String name;
      private boolean flag;
      private StaffListHud.Type type;

      @Generated
      public Text getText() {
         return this.text;
      }

      @Generated
      public String getName() {
         return this.name;
      }

      @Generated
      public boolean isFlag() {
         return this.flag;
      }

      @Generated
      public StaffListHud.Type getType() {
         return this.type;
      }

      @Generated
      public void setText(Text var1) {
         this.text = var1;
      }

      @Generated
      public void setName(String var1) {
         this.name = var1;
      }

      @Generated
      public void setFlag(boolean var1) {
         this.flag = var1;
      }

      @Generated
      public void setType(StaffListHud.Type var1) {
         this.type = var1;
      }

      @Generated
      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof StaffListHud.Entry var2)) {
            return false;
         } else if (!var2.method4484(this)) {
            return false;
         } else if (this.isFlag() != var2.isFlag()) {
            return false;
         } else {
            Text var3 = this.getText();
            Text var4 = var2.getText();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getName();
               String var6 = var2.getName();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  StaffListHud.Type var7 = this.getType();
                  StaffListHud.Type var8 = var2.getType();
                  return var7 == null ? var8 == null : var7.equals(var8);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      @Generated
      public boolean method4484(Object var1) {
         return var1 instanceof StaffListHud.Entry;
      }

      @Generated
      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         var2 = var2 * 59 + (this.isFlag() ? 79 : 97);
         Text var3 = this.getText();
         var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
         String var4 = this.getName();
         var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
         StaffListHud.Type var5 = this.getType();
         return var2 * 59 + (var5 == null ? 43 : var5.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "StaffComponent.Staff(prefix=" + this.getText() + ", name=" + this.getName() + ", isSpec=" + this.isFlag() + ", status=" + this.getType() + ")";
      }

      @Generated
      public Entry(Text var1, String var2, boolean var3, StaffListHud.Type var4) {
         this.text = var1;
         this.name = var2;
         this.flag = var3;
         this.type = var4;
      }
   }

   private class Entry2 {
      private final Animation animation = new Animation(150L, 0.01F, Easing.INSTANCE13);
      private final Text text;
      private final String text2;
      private final String text3;
      private final StaffListHud.Type type;
      private final long longValue;
      private long longValue2 = Long.MIN_VALUE;
      private String text4;

      public Entry2(Text var2, String var3, String var4, StaffListHud.Type var5) {
         this.text = var2;
         this.text2 = var3;
         this.text3 = var4;
         this.type = var5;
         this.longValue = System.currentTimeMillis();
      }

      public float method4485() {
         return HudListLayout.method7645()
            + HudListLayout.nameFont().width(this.text)
            + HudListLayout.CONST_floatValue7
            + HudListLayout.method7647(this.method4486())
            + 6.0F;
      }

      private String method4486() {
         long var1 = System.currentTimeMillis() - this.longValue;
         long var3 = var1 / 1000L;
         if (this.text4 == null || var3 != this.longValue2) {
            this.longValue2 = var3;
            this.text4 = StaffListHud.this.method4483(var1);
         }

         return this.text4;
      }

      public float getHeight() {
         return 14.0F;
      }

      public void method4487(CustomDrawContext var1, float var2, float var3, float var4, boolean var5) {
         ZenithStyle var6 = Zenith.getInstance().getStyleManager().getCurrentStyle();
         Font var7 = HudListLayout.nameFont();
         this.animation.method2808(var5 ? 1.0F : 0.0F);
         var1.pushMatrix();
         var1.getMatrices().translate(var2 + var4 / 2.0F, var3 + this.getHeight() / 2.0F);
         var1.getMatrices().scale(this.animation.getValue(), this.animation.getValue());
         var1.getMatrices().translate(-(var2 + var4 / 2.0F), -(var3 + this.getHeight() / 2.0F));
         Identifier var8 = null;
         PlayerListEntry var9 = StaffListHud.this.items3.get(this.text3);
         if (var9 == null && MinecraftAccess.mc.getNetworkHandler() != null) {
            var9 = MinecraftAccess.mc
               .getNetworkHandler()
               .getPlayerList()
               .stream()
               .filter(var1x -> var1x.getProfile() != null && this.text3.equals(var1x.getProfile().name()))
               .findFirst()
               .orElse(null);
            if (var9 != null && var9.getSkinTextures() != null) {
               StaffListHud.this.items3.put(this.text3, var9);
            }
         }

         if (var9 != null && var9.getSkinTextures() != null) {
            var8 = var9.getSkinTextures().body().texturePath();
         }

         if (var8 == null) {
            var8 = DefaultSkinHelper.getSteve().body().texturePath();
         }

         float var10 = HudListLayout.method7640(var1, var2, var3);
         var1.drawPlayerHeadWithRoundedShader(
            var8, HudListLayout.method7641(var2), HudListLayout.method7642(var3), 10.0F, ProxyDotRadiusData.method8069(3.0F), ZenithColor.INSTANCE
         );
         var1.drawText(var7, this.text, var10, var3 + (this.getHeight() - var7.height()) / 2.0F, var6.getTextEnable().getZenithColor().method7287());
         HudListLayout.method7646(var1, var2, var3, var4, this.method4486());
         var1.popMatrix();
      }

      public boolean method4488() {
         return this.animation.getValue() == 0.0F;
      }
   }

   public static enum Type {
      NONE,
      VANISHED;
   }
}

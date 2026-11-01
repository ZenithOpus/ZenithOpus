package org.zenith.client.hud;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.modules.impl.misc.ClickActionModule;
import org.zenith.client.modules.impl.misc.ServerHelperModule;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BindSetting;
import org.zenith.utility.misc.KeyName;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.render.Interface;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;
import org.zenith.utility.render.display.base.Vec2Utils;

public class Hud3 extends Interface {
   private static final float CONST_floatValue = 16.0F;
   private static final float CONST_floatValue2 = 7.0F;
   private static final float CONST_floatValue3 = 11.0F;
   private static final float CONST_floatValue4 = 3.0F;
   private static final float CONST_floatValue5 = 8.0F;
   private static final float CONST_floatValue6 = 25.0F;
   private static final Vector2f vector2f = new Vector2f(0.0F, 0.0F);
   private final Animation animation = new Animation(200L, 0.0F, Easing.INSTANCE13);
   private final Animation animation2 = new Animation(200L, 25.0F, Easing.INSTANCE4);
   private final List<Hud3.Data> items = new ArrayList<>();
   private final Map<String, Vector2f> items2 = new LinkedHashMap<>();
   private final Map<String, float[]> items3 = new LinkedHashMap<>();
   private String text;
   private float floatValue;
   private float floatValue2;

   public Hud3(String var1, float var2, float var3, float var4, float var5, float var6, float var7, Interface.Type var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public void method8(CustomDrawContext var1) {
      ZenithStyle var2 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      Font var3 = Fonts.NEW_SEMIBOLD.getFont(5.0F);
      boolean var4 = this.method2074();
      List<Hud3.Data> var5 = this.method2280();
      boolean var6 = var4 || !var5.isEmpty();
      if (var6) {
         this.items.clear();
         this.items.addAll(var5);
      } else {
         var5 = this.items;
      }

      ArrayList<Hud3.Data> var7 = new ArrayList();
      ArrayList<Hud3.Data> var8 = new ArrayList();

      for (Object var10_o : (Iterable<?>)(Object)(var5)) {
         Hud3.Data var10 = (Hud3.Data)var10_o;
         (this.items2.containsKey(var10.getText()) ? var8 : var7).add(var10);
      }

      float var18 = this.method2275(var7, var3);
      this.method2279(var5, var18, var4);
      this.width = this.animation2.method2808(this.method2276(var7, var8, var3, var18));
      this.height = this.method2277(var8);
      this.animation.method2807(var6);
      if (this.animation.getValue() <= 0.01F) {
         this.items.clear();
      } else {
         float var19 = this.getX();
         float var11 = this.getY();
         var1.pushMatrix();
         var1.getMatrices().translate(var19 + this.width / 2.0F, var11 + this.height / 2.0F);
         var1.getMatrices().scale(this.animation.getValue(), this.animation.getValue());
         var1.getMatrices().translate(-(var19 + this.width / 2.0F), -(var11 + this.height / 2.0F));
         this.items3.clear();
         if (!var7.isEmpty()) {
            this.method2272(var1, var19, var11, var18, var2);
            float var12 = var19 + 7.0F;

            for (Object var14_o : (Iterable<?>)(Object)(var7)) {
               Hud3.Data var14 = (Hud3.Data)var14_o;
               float var15 = this.method2274(var14, var3);
               this.method2273(var1, var14, var12, var11, var3, var2);
               this.items3.put(var14.getText(), new float[]{var12, var11, var15});
               var12 += var15 + 8.0F;
            }
         }

         for (Object var21_o : (Iterable<?>)(Object)(var8)) {
            Hud3.Data var21 = (Hud3.Data)var21_o;
            Vector2f var22 = this.method2278(var21);
            float var23 = 14.0F + this.method2274(var21, var3);
            float var16 = var19 + var22.x();
            float var17 = var11 + var22.y();
            this.method2272(var1, var16, var17, var23, var2);
            this.method2273(var1, var21, var16 + 7.0F, var17, var3, var2);
            this.items3.put(var21.getText(), new float[]{var16 + 7.0F, var17, this.method2274(var21, var3)});
         }

         var1.popMatrix();
      }
   }

   private void method2272(CustomDrawContext var1, float var2, float var3, float var4, ZenithStyle var5) {
      ProxyDotRadiusData var6 = ProxyDotRadiusData.method8069(Math.min(InterfaceModule.method6801(), 8.0F));
      var1.drawBlurHud(var2, var3, var4, 16.0F, 21.0F, var6, ZenithColor.INSTANCE);
      var1.drawRoundedRect(var2, var3, var4, 16.0F, var6, var5.getHudBackground().getZenithColor().method7294(InterfaceModule.INSTANCE.method6778()));
      this.method4679(var1, var2, var3, var4, 16.0F, this.animation.getValue());
   }

   private void method2273(CustomDrawContext var1, Hud3.Data var2, float var3, float var4, Font var5, ZenithStyle var6) {
      try {
         float var7 = 0.6875F;
         var1.pushMatrix();
         var1.getMatrices().translate(var3, var4 + 2.5F);
         var1.getMatrices().scale(var7, var7);
         var1.drawItem(var2.getItemStack(), 0, 0);
         var1.drawItemBar(var2.getItemStack(), 0, 0);
         var1.drawCooldownProgress(var2.getItemStack(), 0, 0);
         var1.popMatrix();
         var1.drawText(var5, var2.getText2(), var3 + 11.0F + 3.0F, var4 + (16.0F - var5.height()) / 2.0F, var6.getTextEnable().getZenithColor());
      } catch (Exception var8) {
         var8.printStackTrace();
      }
   }

   private float method2274(Hud3.Data var1, Font var2) {
      return 14.0F + var2.width(var1.getText2());
   }

   private float method2275(List<Hud3.Data> var1, Font var2) {
      if (var1.isEmpty()) {
         return 0.0F;
      } else {
         float var3 = 0.0F;

         for (Hud3.Data var5 : var1) {
            var3 += this.method2274(var5, var2) + 8.0F;
         }

         return 14.0F + var3 - 8.0F;
      }
   }

   private float method2276(List<Hud3.Data> var1, List<Hud3.Data> var2, Font var3, float var4) {
      float var5 = Math.max(25.0F, var4);

      for (Hud3.Data var7 : var2) {
         var5 = Math.max(var5, this.method2278(var7).x + 14.0F + this.method2274(var7, var3));
      }

      return var5;
   }

   private float method2277(List<Hud3.Data> var1) {
      float var2 = 16.0F;

      for (Hud3.Data var4 : var1) {
         var2 = Math.max(var2, this.method2278(var4).y + 16.0F);
      }

      return var2;
   }

   private Vector2f method2278(Hud3.Data var1) {
      return this.items2.getOrDefault(var1.getText(), vector2f);
   }

   @Override
   public boolean method2056(MouseButtonEvent var1) {
      if (this.method2074() && var1.getIntValue() == 0 && var1.getIntValue2() == 1) {
         net.minecraft.client.util.math.Vector2f var2 = Vec2Utils.method8068(InterfaceModule.INSTANCE.method6807());

         for (Map.Entry var4 : this.items3.entrySet()) {
            float[] var5 = (float[])var4.getValue();
            if (Vec2Utils.method8067(var5[0], var5[1], var5[2], 16.0, var2.x(), var2.y())) {
               this.text = (String)var4.getKey();
               this.floatValue = var2.x() - var5[0];
               this.floatValue2 = var2.y() - var5[1];
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void method2279(List<Hud3.Data> var1, float var2, boolean var3) {
      if (this.text != null) {
         boolean var4 = mc.getWindow() != null && GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), 0) == 1;
         boolean var5 = var1.stream().anyMatch(var1x -> var1x.getText().equals(this.text));
         if (var3 && var4 && var5) {
            net.minecraft.client.util.math.Vector2f var6 = Vec2Utils.method8068(InterfaceModule.INSTANCE.method6807());
            float var7 = Math.max(0.0F, var6.x() - this.floatValue - 7.0F - this.floatValue);
            float var8 = Math.max(0.0F, var6.y() - this.floatValue2 - this.floatValue2);
            if (var8 < 12.0F && var7 < Math.max(var2, 25.0F)) {
               this.items2.remove(this.text);
            } else {
               this.items2.put(this.text, new Vector2f(var7, var8));
            }
         } else {
            this.text = null;
         }
      }
   }

   @Override
   public void method2069(CustomDrawContext var1) {
   }

   @Override
   public float method254() {
      return this.animation.getValue();
   }

   @Override
   public JsonObject save() {
      JsonObject var1 = super.save();
      if (!this.items2.isEmpty()) {
         JsonObject var2 = new JsonObject();
         this.items2.forEach((var1x, var2x) -> {
            JsonObject var3 = new JsonObject();
            var3.addProperty("x", var2x.x());
            var3.addProperty("y", var2x.y());
            var2.add(var1x, var3);
         });
         var1.add("detached", var2);
      }

      return var1;
   }

   @Override
   public void load(JsonObject var1) {
      super.load(var1);
      this.items2.clear();
      if (var1 != null && var1.has("detached") && var1.get("detached").isJsonObject()) {
         JsonObject var2 = var1.getAsJsonObject("detached");

         for (String var4 : var2.keySet()) {
            if (var2.get(var4).isJsonObject()) {
               JsonObject var5 = var2.getAsJsonObject(var4);
               this.items2.put(var4, new Vector2f(var5.has("x") ? var5.get("x").getAsFloat() : 0.0F, var5.has("y") ? var5.get("y").getAsFloat() : 0.0F));
            }
         }
      }
   }

   private List<Hud3.Data> method2280() {
      ArrayList<Hud3.Data> var1 = new ArrayList<>();
      if (mc != null && mc.player != null) {
         ServerHelperModule var2 = ServerHelperModule.INSTANCE;
         if (method2281(var2.getShulkerKey())) {
            this.method2282(var1, var2.getShulkerKey(), var2.method5217());
         }

         for (ServerHelperModule.Data3 var4 : var2.method5216()) {
            if (method2281(var4.getBindSetting())) {
               this.method2282(var1, var4.getBindSetting(), var4.method5218());
            }
         }

         ClickActionModule var6 = ClickActionModule.INSTANCE;
         if (method2281(var6.getExpBind())) {
            this.method2282(var1, var6.getExpBind(), InventoryUtils.method906(Items.EXPERIENCE_BOTTLE));
         }

         for (ClickActionModule.Data var5 : var6.method5486()) {
            if (method2281(var5.getBindSetting())) {
               this.method2282(var1, var5.getBindSetting(), InventoryUtils.method906(var5.getItem()));
            }
         }

         return var1;
      } else {
         return var1;
      }
   }

   private static boolean method2281(BindSetting var0) {
      return var0 != null && var0.getKeyCode() != -1 && var0.isVisible();
   }

   private void method2282(List<Hud3.Data> var1, BindSetting var2, Slot var3) {
      if (var2 != null && var3 != null && var2.getKeyCode() != -1 && var2.isVisible()) {
         ItemStack var4 = var3.getStack().copy();
         if (!var4.isEmpty()) {
            var1.add(new Hud3.Data(var2.getName(), KeyName.method6701(var2.getKeyCode()), var2.getKeyCode(), var4));
         }
      }
   }

   private static final class Data {
      private final String text;
      private final String text2;
      private final int intValue;
      private final ItemStack itemStack;

      public String text() {
         return this.text;
      }

      public String text2() {
         return this.text2;
      }

      public int intValue() {
         return this.intValue;
      }

      public ItemStack itemStack() {
         return this.itemStack;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.text2, other.text2) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.itemStack, other.itemStack);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(text, text2, intValue, itemStack);
      }

      @Override
      public String toString() {
         return "Data[" + "text=" + this.text + ", " + "text2=" + this.text2 + ", " + "intValue=" + this.intValue + ", " + "itemStack=" + this.itemStack + "]";
      }

      private Data(String var1, String var2, int var3, ItemStack var4) {
         this.text = var1;
         this.text2 = var2;
         this.intValue = var3;
         this.itemStack = var4;
      }

      public String getText() {
         return this.text;
      }

      public String getText2() {
         return this.text2;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public ItemStack getItemStack() {
         return this.itemStack;
      }
   }
}

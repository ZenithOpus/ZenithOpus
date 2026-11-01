package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.client.screens.esp.ModulePreviewStage;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "FireWorkESP",
   category = ModuleCategory.RENDER,
   method896 = true,
   description = "module.fireworkesp.description"
)
public final class FireWorkESPModule extends Module {
   public static final FireWorkESPModule INSTANCE = new FireWorkESPModule();
   private final Map<Vec3d, FireWorkESPModule.Entry> items = new ConcurrentHashMap<>();
   private ClientWorld clientWorld;
   private final SliderSetting scale = new SliderSetting("module.fireworkESP.scale", 0.7F, 0.4F, 1.6F, 0.1F, "module.fireworkESP.scale.desc", "x");
   private final SliderSetting lifetime = new SliderSetting("module.fireworkESP.lifetime", 10.0F, 1.0F, 30.0F, 1.0F, "module.fireworkESP.lifetime.desc", "s");
   private final BooleanSetting throughWalls = new BooleanSetting("module.fireworkESP.throughWalls", "module.fireworkESP.throughWalls.desc", false);
   private final List<FireWorkESPModule.Data> items2 = new ArrayList<>();
   private long longValue;
   private final Random random = new Random();

   private FireWorkESPModule() {
   }

   @Override
   public ModulePreviewStage.Kind previewKind() {
      return ModulePreviewStage.Kind.FIREWORK;
   }

   private long method237() {
      return (long)(this.lifetime.getValue3() * 1000.0F);
   }

   @EventTarget
   public void method238(EntityAddEvent var1) {
      if (mc.player != null && mc.world != null) {
         if (var1.getEntity() instanceof FireworkRocketEntity var2) {
            this.method239();
            this.items.put(var2.getEntityPos(), new FireWorkESPModule.Entry(System.currentTimeMillis()));
         }
      }
   }

   private void method239() {
      if (mc.world != this.clientWorld) {
         this.items.clear();
         this.clientWorld = mc.world;
      }
   }

   @EventTarget
   public void method240(DrawEvent var1) {
      if (mc.player != null && mc.world != null) {
         this.method239();
         long var2 = System.currentTimeMillis();
         long var4 = this.method237();
         var var6 = this.items.entrySet().iterator();

         while (var6.hasNext()) {
            Map.Entry var7 = (Map.Entry)var6.next();
            long var8 = var2 - ((FireWorkESPModule.Entry)var7.getValue()).longValue;
            if (var8 > var4) {
               var6.remove();
            } else {
               this.method241(var1.getCustomDrawContext(), (Vec3d)var7.getKey(), (FireWorkESPModule.Entry)var7.getValue(), var8);
            }
         }
      }
   }

   private void method241(CustomDrawContext var1, Vec3d var2, FireWorkESPModule.Entry var3, long var4) {
      if (var1 != null) {
         Vec3d var6 = ProjectionUtils.method301(var2);
         if (var6 != null && !(var6.z <= 0.0) && !(var6.z >= 1.0)) {
            if (this.throughWalls.isEnabled() || this.method242(var2, var3)) {
               this.method245(var1, (float)var6.x, (float)var6.y, var4);
            }
         }
      }
   }

   private boolean method242(Vec3d var1, FireWorkESPModule.Entry var2) {
      if (mc.world != null && mc.gameRenderer != null && mc.gameRenderer.getCamera() != null) {
         long var3 = mc.world.getTime();
         if (var2.longValue2 != var3) {
            var2.longValue2 = var3;
            Vec3d var5 = mc.gameRenderer.getCamera().getCameraPos();
            BlockHitResult var6 = mc.world.raycast(new RaycastContext(var5, var1, ShapeType.COLLIDER, FluidHandling.NONE, mc.player));
            var2.visible = var6.getType() == Type.MISS || var6.getPos().squaredDistanceTo(var5) + 1.0E-4 >= var1.squaredDistanceTo(var5);
         }

         return var2.visible;
      } else {
         return true;
      }
   }

   public void method243(CustomDrawContext var1, float var2, float var3, float var4, float var5) {
      if (var1 != null) {
         long var6 = System.currentTimeMillis();
         long var8 = this.method237();
         this.items2.removeIf(var4x -> var6 - var4x.getLongValue() > var8);
         long var10 = Math.max(300L, var8 / 5L);
         if (var6 - this.longValue >= var10) {
            this.longValue = var6;
            this.items2.add(new FireWorkESPModule.Data(0.18F + this.random.nextFloat() * 0.64F, 0.12F + this.random.nextFloat() * 0.76F, var6));
         }

         for (FireWorkESPModule.Data var13 : this.items2) {
            this.method245(var1, var2 + var4 * var13.getFloatValue(), var3 + var5 * var13.getFloatValue2(), var6 - var13.getLongValue());
         }
      }
   }

   public void method244() {
      this.items2.clear();
      this.longValue = 0L;
   }

   private void method245(CustomDrawContext var1, float var2, float var3, long var4) {
      ZenithStyle var6 = Zenith.getInstance().getStyleManager().getCurrentStyle();
      Font var7 = Fonts.NEW_MEDIUM.getFont(6.5F);
      String var8 = String.format("%.1fс", var4 / 1000.0);
      float var9 = GuiStyle.PADDING.intValue();
      float var10 = 10.4F;
      float var11 = var9 + var10 + var9 + var7.width(var8) + var9;
      float var12 = Math.max(var7.height(), var10) + GuiStyle.PADDING.intValue();
      float var13 = var2 - var11 / 2.0F;
      float var14 = var3 - var12 / 2.0F;
      float var15 = this.scale.getValue3();
      this.pushCenteredScale(var1, var2, var3, var15, var15);
      EntityESPModule var16 = EntityESPModule.INSTANCE;
      ProxyDotRadiusData var17 = ProxyDotRadiusData.method8069(InterfaceModule.method6801() / 2.0F);
      var1.drawBlurHudBooleanCheck(
         var13,
         var14,
         var11,
         var12,
         22.0F,
         var17,
         ZenithColor.INSTANCE,
         var16.getBlur().isEnabled() && var16.getBlur().isVisible(),
         var16.getGlow().isEnabled() && var16.getGlow().isVisible()
      );
      var1.drawRoundedRect(var13, var14, var11, var12, var17, var6.getHudBackground().getZenithColor());
      var1.drawText(var7, var8, var13 + var9 + var10 + var9, var14 + (var12 - var7.height()) / 2.0F, var6.getTextEnable().getZenithColor());
      var1.pushMatrix();
      var1.getMatrices().translate(var13 + var9, var14 + (var12 - var10) / 2.0F);
      var1.getMatrices().scale(0.65F, 0.65F);
      var1.drawItem(Items.FIREWORK_ROCKET.getDefaultStack(), 0, 0);
      var1.popMatrix();
      this.pop(var1);
   }

   private void pushCenteredScale(CustomDrawContext var1, float var2, float var3, float var4, float var5) {
      var1.pushMatrix();
      var1.getMatrices().translate(var2, var3);
      var1.getMatrices().scale(var4, var5);
      var1.getMatrices().translate(-var2, -var3);
   }

   private void pop(CustomDrawContext var1) {
      var1.popMatrix();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.items.clear();
      this.clientWorld = mc.world;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.items.clear();
      this.clientWorld = null;
   }

   private static final class Data {
      private final float floatValue;
      private final float floatValue2;
      private final long longValue;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public long longValue() {
         return this.longValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.longValue, other.longValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, longValue);
      }

      @Override
      public String toString() {
         return "Data[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "longValue=" + this.longValue + "]";
      }

      private Data(float var1, float var2, long var3) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.longValue = var3;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public long getLongValue() {
         return this.longValue;
      }
   }

   private static final class Entry {
      private final long longValue;
      private long longValue2 = Long.MIN_VALUE;
      private boolean visible;

      private Entry(long var1) {
         this.longValue = var1;
      }
   }
}

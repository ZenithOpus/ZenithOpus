package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.ModeSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.render.ImmediateRenderer;

@ModuleInfo(
   name = "JumpCircle",
   category = ModuleCategory.RENDER,
   description = "module.jumpcircle.description"
)
public class JumpCircleModule extends Module {
   private static final int CONST_intValue = 8;
   private static final String[][] texts = new String[][]{
      {"module.jumpCircle.texture.storm", "shtorm1", "shtorm2", "glow"},
      {"module.jumpCircle.texture.pool", "glow", "omut2", "omut3"},
      {"module.jumpCircle.texture.rings", "colso1", "omut2", "colso3"},
      {"module.jumpCircle.texture.explosion", "boom1", "boom2", "glow"}
   };
   private static final Identifier[][] identifiers = method4583();
   public static final JumpCircleModule INSTANCE = new JumpCircleModule();
   private final ModeSetting texture = new ModeSetting("module.jumpCircle.texture", "module.jumpCircle.texture.desc", method4582());
   private final SliderSetting size = new SliderSetting("module.jumpCircle.size", 1.4F, 0.5F, 3.0F, 0.1F, "module.jumpCircle.size.desc", "x");
   private final SliderSetting speed = new SliderSetting("module.jumpCircle.speed", 4.8F, 0.0F, 8.0F, 0.1F, "module.jumpCircle.speed.desc", "x");
   private final SliderSetting lifetime = new SliderSetting(
      "module.jumpCircle.lifetime", 650.0F, 250.0F, 1500.0F, 50.0F, "module.jumpCircle.lifetime.desc", "ms"
   );
   private final List<JumpCircleModule.Entry> items = new ArrayList<>();
   private boolean lastOnGround;
   private Vec3d vec3d;

   private JumpCircleModule() {
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.items.clear();
      this.lastOnGround = mc.player != null && mc.player.isOnGround();
      this.vec3d = mc.player == null ? null : mc.player.getEntityPos();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.items.clear();
      this.vec3d = null;
   }

   @EventTarget
   public void method4575(UpdateEvent var1) {
      if (mc.player != null && mc.world != null) {
         boolean var2 = mc.player.isOnGround();
         if (var2) {
            this.vec3d = mc.player.getEntityPos();
         }

         if (this.lastOnGround && !var2 && mc.player.getVelocity().y > 0.05) {
            this.items.add(new JumpCircleModule.Entry(this.vec3d == null ? mc.player.getEntityPos() : this.vec3d, System.currentTimeMillis()));
            int var3 = this.items.size() - 8;
            if (var3 > 0) {
               this.items.subList(0, var3).clear();
            }
         }

         this.lastOnGround = var2;
      }
   }

   @EventTarget
   public void method4576(WorldRenderEvent var1) {
      if (mc.player != null && mc.world != null && !this.items.isEmpty()) {
         long var2 = System.currentTimeMillis();
         float var4 = this.lifetime.getValue3();

         for (int var5 = this.items.size() - 1; var5 >= 0; var5--) {
            if ((float)(var2 - this.items.get(var5).longValue) > var4) {
               this.items.remove(var5);
            }
         }

         if (!this.items.isEmpty()) {
            this.method4577(var1.getMatrixStack(), var2, var4);
         }
      }
   }

   private void method4577(MatrixStack var1, long var2, float var4) {
      RenderState.method1871();
      RenderState.method1877();
      RenderState.method1875(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
      RenderState.method1898("core/position_tex_color");
      RenderState.method1878();
      RenderState.method1882(false);
      int var5 = this.texture.getIndex();
      if (var5 < 0 || var5 >= identifiers.length) {
         var5 = 0;
      }

      Identifier[] var6 = identifiers[var5];
      Camera var7 = mc.getEntityRenderDispatcher().camera;
      Vec3d var8 = var7.getCameraPos();
      Matrix4f var9 = var1.peek().getPositionMatrix();
      float var10 = this.size.getValue3();
      float var11 = this.speed.getValue3();
      int var12 = zenith.getStyleManager().getClientColor(0).method7287();
      int var13 = zenith.getStyleManager().getClientColor(180).method7287();

      for (JumpCircleModule.Entry var15 : this.items) {
         this.method4578(var15, var2, var8, var11, var4, var10, var12, var13);
      }

      for (Identifier var17 : var6) {
         RenderState.method1890(0, var17);
         BufferBuilder var18 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

         for (JumpCircleModule.Entry var20 : this.items) {
            this.method4579(var9, var18, var20);
         }

         ImmediateRenderer.method8015(var18.end());
      }

      RenderState.method1882(true);
      RenderState.method1876();
      RenderState.method1872();
      RenderState.method1873();
   }

   private void method4578(JumpCircleModule.Entry var1, long var2, Vec3d var4, float var5, float var6, float var7, int var8, int var9) {
      long var10 = var2 - var1.longValue;
      var1.floatValue = (float)(var1.vec3d.x - var4.x);
      var1.floatValue2 = (float)(var1.vec3d.y - var4.y) + 0.04F;
      var1.floatValue3 = (float)(var1.vec3d.z - var4.z);
      float var12 = (float)var10 * 0.001F * (float) (Math.PI * 2) * var5;
      var1.floatValue4 = MathHelper.cos(var12);
      var1.floatValue5 = MathHelper.sin(var12);
      float var13 = MathHelper.clamp((float)var10 / var6, 0.0F, 1.0F);
      float var14 = 1.0F - var13;
      float var15 = var14 * var14;
      float var16 = MathHelper.lerp(var13, 0.15F, var7);
      var1.floatValue6 = var16 * 1.22F;
      var1.floatValue7 = var16;
      var1.intValue = ColorUtils.method6439(var8, var15 * 0.45F);
      var1.intValue2 = ColorUtils.method6439(var9, var15);
   }

   private void method4579(Matrix4f var1, BufferBuilder var2, JumpCircleModule.Entry var3) {
      this.method4580(var1, var2, var3.floatValue, var3.floatValue2, var3.floatValue3, var3.floatValue6, var3.floatValue4, var3.floatValue5, var3.intValue);
      this.method4580(
         var1, var2, var3.floatValue, var3.floatValue2 + 0.002F, var3.floatValue3, var3.floatValue7, var3.floatValue4, var3.floatValue5, var3.intValue2
      );
   }

   private void method4580(Matrix4f var1, BufferBuilder var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9) {
      float var10 = var6 * var7;
      float var11 = var6 * var8;
      this.method4581(var1, var2, var3 - var10 + var11, var4, var5 - var11 - var10, 0.0F, 0.0F, var9);
      this.method4581(var1, var2, var3 - var10 - var11, var4, var5 - var11 + var10, 0.0F, 1.0F, var9);
      this.method4581(var1, var2, var3 + var10 - var11, var4, var5 + var11 + var10, 1.0F, 1.0F, var9);
      this.method4581(var1, var2, var3 + var10 + var11, var4, var5 + var11 - var10, 1.0F, 0.0F, var9);
   }

   private void method4581(Matrix4f var1, BufferBuilder var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      var2.vertex(var1, var3, var4, var5).texture(var6, var7).color(var8);
   }

   private static String[] method4582() {
      String[] var0 = new String[texts.length];

      for (int var1 = 0; var1 < texts.length; var1++) {
         var0[var1] = texts[var1][0];
      }

      return var0;
   }

   private static Identifier[][] method4583() {
      Identifier[][] var0 = new Identifier[texts.length][];

      for (int var1 = 0; var1 < texts.length; var1++) {
         String[] var2 = texts[var1];
         int var3 = var2.length == 1 ? 0 : 1;
         var0[var1] = new Identifier[var2.length - var3];

         for (int var4 = var3; var4 < var2.length; var4++) {
            var0[var1][var4 - var3] = Zenith.method1501("visuals/jumpcircle/" + var2[var4] + ".png");
         }
      }

      return var0;
   }

   private static class Entry {
      private final Vec3d vec3d;
      private final long longValue;
      private float floatValue;
      private float floatValue2;
      private float floatValue3;
      private float floatValue4;
      private float floatValue5;
      private float floatValue6;
      private float floatValue7;
      private int intValue;
      private int intValue2;

      private Entry(Vec3d var1, long var2) {
         this.vec3d = var1;
         this.longValue = var2;
      }
   }
}

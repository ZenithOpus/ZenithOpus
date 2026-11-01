package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.zenith.Animation;
import org.zenith.Easing;
import org.zenith.MatrixUtils;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.api.PetManager;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.display.base.GlowColor;

@ModuleInfo(
   name = "Arrows",
   description = "module.arrows.description",
   category = ModuleCategory.RENDER
)
public final class ArrowsModule extends Module {
   private final Identifier identifier = Zenith.method1501("textures/arrows.png");
   private final Animation animation = new Animation(150L, 12.0F, Easing.INSTANCE13);
   private final SliderSetting radiusSetting = new SliderSetting(
      "module.arrows.radiusSetting", 50.0F, 30.0F, 100.0F, 10.0F, "module.arrows.radiusSetting.desc", "b"
   );
   private final SliderSetting sizeSetting = new SliderSetting("module.arrows.sizeSetting", 16.0F, 8.0F, 20.0F, 1.0F, "module.arrows.sizeSetting.desc", "px");
   public static final ArrowsModule INSTANCE = new ArrowsModule();
   private final BooleanSetting ignoreGol = new BooleanSetting("module.arrows.ignoreGol", "module.arrows.ignoreGol.desc", true);

   private ArrowsModule() {
   }

   @EventTarget
   public void method3036(UpdateEvent var1) {
   }

   @EventTarget
   public void onDraw(DrawEvent var1) {
      MatrixStack var2 = MatrixUtils.method666(var1.getCustomDrawContext().getMatrices());
      var var3 = Objects.requireNonNull(mc.world)
         .getPlayers()
         .stream()
         .filter(
            var1x -> var1x != mc.player && !PetManager.method4557(var1x.getId()) && (!this.ignoreGol.isEnabled() || UseItemUtils.method6529(var1x) != 0.0F)
         )
         .toList();
      float var4 = mc.getWindow().getScaledWidth() / 2.0F;
      float var5 = mc.getWindow().getScaledHeight() / 2.0F;
      float var6 = var5
         - this.radiusSetting.getValue3()
         - this.animation
            .method2808(
               mc.currentScreen instanceof InventoryScreen
                  ? 80.0F
                  : (mc.currentScreen instanceof GenericContainerScreen ? 100.0F : (Objects.requireNonNull(mc.player).isSprinting() ? 12.0F : 0.0F))
            );
      float var7 = this.sizeSetting.getValue3();
      if (!mc.options.hudHidden && mc.options.getPerspective().equals(Perspective.FIRST_PERSON) && !var3.isEmpty()) {
         RenderState.method1871();
         RenderState.method1877();
         RenderState.method1879();
         RenderState.method1874(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_CONSTANT_ALPHA);
         RenderState.method1890(0, this.identifier);
         RenderState.method1898("core/position_tex_color");
         BufferBuilder var8 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         var3.forEach(
            var6x -> {
               int var7x = zenith.getFriends().method7626(var6x)
                  ? zenith.getStyleManager().getCurrentStyle().getFriendColor().getZenithColor().method7287()
                  : Zenith.getInstance().getStyleManager().getClientColor(90).method7287();
               float var8x = method3037(var6x) - Objects.requireNonNull(mc.player).getYaw();
               var2.push();
               var2.translate(var4, var5, 0.0F);
               var2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var8x));
               var2.translate(-var4, -var5, 0.0F);
               Matrix4f var9 = var2.peek().getPositionMatrix();
               if (zenith.getFriends().method7626(var6x)) {
                  var8.vertex(var9, var4 - var7 / 2.0F, var6 + var7, 0.0F).texture(0.0F, 1.0F).color(var7x);
                  var8.vertex(var9, var4 + var7 / 2.0F, var6 + var7, 0.0F).texture(1.0F, 1.0F).color(var7x);
                  var8.vertex(var9, var4 + var7 / 2.0F, var6, 0.0F).texture(1.0F, 0.0F).color(var7x);
                  var8.vertex(var9, var4 - var7 / 2.0F, var6, 0.0F).texture(0.0F, 0.0F).color(var7x);
               } else {
                  GlowColor var10 = zenith.getStyleManager().getClientColor();
                  var8.vertex(var9, var4 - var7 / 2.0F, var6 + var7, 0.0F).texture(0.0F, 1.0F).color(var10.getZenithColor2().method7287());
                  var8.vertex(var9, var4 + var7 / 2.0F, var6 + var7, 0.0F).texture(1.0F, 1.0F).color(var10.getZenithColor4().method7287());
                  var8.vertex(var9, var4 + var7 / 2.0F, var6, 0.0F).texture(1.0F, 0.0F).color(var10.getZenithColor3().method7287());
                  var8.vertex(var9, var4 - var7 / 2.0F, var6, 0.0F).texture(0.0F, 0.0F).color(var10.getZenithColor().method7287());
               }

               var2.translate(var4, var5, 0.0F);
               var2.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-var8x));
               var2.translate(-var4, -var5, 0.0F);
               var2.pop();
            }
         );
         ImmediateRenderer.method8015(var8.end());
         RenderState.method1878();
         RenderState.method1876();
         RenderState.method1873();
         RenderState.method1872();
      }
   }

   public static float method3037(Entity var0) {
      double var1 = MathUtils.method2658(var0.lastX, var0.getX()) - MathUtils.method2658(mc.player.lastX, mc.player.getX());
      double var3 = MathUtils.method2658(var0.lastZ, var0.getZ()) - MathUtils.method2658(mc.player.lastZ, mc.player.getZ());
      return (float)(-(Math.atan2(var1, var3) * (180.0 / Math.PI)));
   }
}

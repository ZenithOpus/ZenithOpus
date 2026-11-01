package org.zenith.client.screens.esp;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.zenith.MatrixUtils;
import org.zenith.MinecraftAccess;
import org.zenith.RenderState;
import org.zenith.base.Module;
import org.zenith.client.modules.impl.render.AmbientParticlesModule;
import org.zenith.client.modules.impl.render.BlockESPModule;
import org.zenith.client.modules.impl.render.ChamsModule;
import org.zenith.client.modules.impl.render.EntityESPModule;
import org.zenith.client.modules.impl.render.FireWorkESPModule;
import org.zenith.client.modules.impl.render.HitParticlesModule;
import org.zenith.client.modules.impl.render.TargetESPModule;
import org.zenith.client.modules.impl.render.TotemParticlesModule;
import org.zenith.client.modules.impl.render.WeatherModule;
import org.zenith.client.modules.impl.render.WorldModule;
import org.zenith.utility.misc.Enum3;
import org.zenith.utility.misc.FrameTimer;
import org.zenith.utility.render.EspScreen;
import org.zenith.utility.render.OutlineVertexProvider;
import org.zenith.utility.render.ScissorFlusher;
import org.zenith.utility.render.display.base.BoundsData;
import org.zenith.utility.render.display.base.MenuDrawContext;

public final class ModulePreviewStage implements MinecraftAccess {
   private static final float PX_PER_BLOCK = 96.0F;
   private static final float ARRIVAL_SPIN = 28.0F;
   private ModulePreviewStage.Kind kind = ModulePreviewStage.Kind.MODEL;
   private Module owner;
   private boolean open;
   private OtherClientPlayerEntity dummy;
   private ClientWorld dummyWorld;
   private float modelYaw;
   private float modelPitch;
   private boolean draggingModel;
   private float arrival = 1.0F;
   private BoundsData bounds;

   public boolean isOpen() {
      return this.open;
   }

   public ModulePreviewStage.Kind kind() {
      return this.kind;
   }

   public boolean isLive() {
      return this.owner != null && this.owner.isEnabled();
   }

   public void show(ModulePreviewStage.Kind var1, Module var2) {
      if (!this.open || this.kind != var1) {
         this.arrival = 0.0F;
      }

      this.owner = var2;
      this.kind = var1;
      this.open = true;
      if (var1 != ModulePreviewStage.Kind.SHADER) {
         EspScreen.release();
      }

      if (var1 != ModulePreviewStage.Kind.FIREWORK) {
         FireWorkESPModule.INSTANCE.method244();
      }
   }

   public void hide() {
      this.open = false;
      this.bounds = null;
      this.draggingModel = false;
      EspScreen.release();
      FireWorkESPModule.INSTANCE.method244();
   }

   public void releaseModel() {
      this.dummy = null;
      this.dummyWorld = null;
   }

   public void render(MenuDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      if (var8 <= 0.001F) {
         this.bounds = null;
      } else {
         this.bounds = this.open ? new BoundsData(var4, var5, var6, var7) : null;
         this.arrival = MathHelper.lerp(0.12F, this.arrival, 1.0F);
         if (this.arrival > 0.999F) {
            this.arrival = 1.0F;
         }

         boolean var10 = this.isLive();
         if (this.kind == ModulePreviewStage.Kind.WORLD_WEATHER) {
            if (var10) {
               var1.enableScissor(var4, var5, var4 + var6, var5 + var7);

               try {
                  WeatherModule.INSTANCE.method3119(var1, var4, var5, var6, var7, var8);
               } finally {
                  var1.disableScissor();
               }
            }
         } else if (this.kind != ModulePreviewStage.Kind.FIREWORK && this.kind != ModulePreviewStage.Kind.BLOCK) {
            LivingEntity var11 = this.previewEntity();
            if (var11 != null) {
               float var12 = var4 + var6 / 2.0F;
               float var13;
               float var14 = (var13 = var5 + var7 / 2.0F + 8.0F) + 86.399994F;
               float var15 = var7 / 2.0F;
               float var16 = viewAngle(-this.modelYaw) + 28.0F * (1.0F - this.arrival);
               float var17 = viewAngle(-this.modelPitch);
               boolean var18;
               if (var18 = this.open && var10 && this.kind == ModulePreviewStage.Kind.SHADER && var9 >= 0.995F) {
                  EspScreen.method6551(var1, () -> this.drawModel(var1, var4, var6, var13, var15, var16, var17, var11));
               }

               var1.enableScissor(var4, var5, var4 + var6, var5 + var7);

               try {
                  RenderState.method1883(1.0F, 1.0F, 1.0F, var8);

                  try {
                     this.drawModel(var1, var4, var6, var13, var15, var16, var17, var11);
                  } finally {
                     RenderState.method1883(1.0F, 1.0F, 1.0F, 1.0F);
                  }

                  if (!var10) {
                     return;
                  }

                  if (this.kind == ModulePreviewStage.Kind.ENTITY) {
                     EntityESPModule.INSTANCE.method619(var1, var12, var14, 96.0F, var5 + var7 - 4.0F, "Player", var8, var2, var3);
                  } else if (this.kind == ModulePreviewStage.Kind.PARTICLES_AMBIENT) {
                     AmbientParticlesModule.INSTANCE.method2392(var1, var12, var14, 96.0F, var8);
                  } else if (this.kind == ModulePreviewStage.Kind.PARTICLES_TOTEM) {
                     TotemParticlesModule.INSTANCE.method4699(var1, var12, var14, 96.0F, var8);
                  } else if (this.kind == ModulePreviewStage.Kind.PARTICLES_HIT) {
                     HitParticlesModule.INSTANCE.method7795(var1, var12, var14, 96.0F, var8);
                  } else if (this.kind == ModulePreviewStage.Kind.WORLD_VISUALS) {
                     WorldModule.INSTANCE.method5650(var1, var4, var5, var6, var7, var8);
                  } else if (this.kind == ModulePreviewStage.Kind.TARGET) {
                     TargetESPModule.INSTANCE.method2160(var1, var12, var14, 96.0F, var8, var16, var17);
                  }
               } finally {
                  var1.disableScissor();
               }

               if (var18) {
                  EspScreen.method6552(var1, var4, var5, var6, var7, var8);
               }

               RenderState.method1881();
            }
         } else if (var10) {
            var1.enableScissor(var4, var5, var4 + var6, var5 + var7);

            try {
               if (this.kind == ModulePreviewStage.Kind.FIREWORK) {
                  FireWorkESPModule.INSTANCE.method243(var1, var4, var5, var6, var7);
               } else {
                  BlockESPModule.INSTANCE.method7573(var1, var4 + var6 / 2.0F, var5 + var7 / 2.0F, 96.0F, var5 + var7 - 4.0F);
               }
            } finally {
               var1.disableScissor();
            }
         }
      }
   }

   private static float viewAngle(float var0) {
      return (float)Math.atan(var0 / 40.0F) * 20.0F;
   }

   private void drawModel(MenuDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7, LivingEntity var8) {
      ChamsModule.flag3 = this.kind == ModulePreviewStage.Kind.CHAMS && this.isLive();
      boolean var9;
      if (var9 = this.kind == ModulePreviewStage.Kind.WORLD_VISUALS && this.isLive()) {
         WorldModule var10 = WorldModule.INSTANCE;
         FrameTimer.method753(var10.method5651(), 1.0F, 1.0F, 1.0F, var10.method5652());
      }

      float var28 = var8.bodyYaw;
      float var11 = var8.getYaw();
      float var12 = var8.getPitch();
      float var13 = var8.headYaw;
      float var14 = var8.lastHeadYaw;

      try {
         float var15 = 180.0F + var6;
         var8.bodyYaw = var15;
         var8.setYaw(var15);
         var8.setPitch(0.0F);
         var8.headYaw = var15;
         var8.lastHeadYaw = var15;
         Quaternionf var16 = new Quaternionf().rotateX(var7 * (float) (Math.PI / 180.0));
         Quaternionf var17 = new Quaternionf().rotateZ((float) Math.PI).mul(var16);
         float var18 = var8.getScale();
         Vector3f var19 = new Vector3f(0.0F, var8.getHeight() / 2.0F, 0.0F);
         float var20 = 96.0F / var18;
         MatrixStack var21 = MatrixUtils.method666(var1.getMatrices());
         var21.translate(var2 + var3 / 2.0F, var4, 50.0F);
         var21.scale(var20, var20, -var20);
         var21.translate(var19.x, var19.y, var19.z);
         var21.multiply(var17);
         ScissorFlusher.flush();
         EntityRenderManager var22 = mc.getEntityRenderDispatcher();
         EntityRenderState var23 = var22.getAndUpdateRenderState(var8, 1.0F);
         var23.light = 240 << 16 | 240;
         var23.shadowPieces.clear();
         var23.outlineColor = 0;
         OutlineVertexProvider.method1540(var23);
         CameraRenderState var24 = OutlineVertexProvider.method1541(var8.getEntityPos());
         var24.orientation = var16.conjugate(new Quaternionf()).rotateY((float) Math.PI);
         OutlineVertexProvider.method1542(mc.getBufferBuilders().getEntityVertexConsumers(), var4x -> var22.render(var23, var24, 0.0, 0.0, 0.0, var21, var4x));
         ScissorFlusher.flush();
      } finally {
         var8.bodyYaw = var28;
         var8.setYaw(var11);
         var8.setPitch(var12);
         var8.headYaw = var13;
         var8.lastHeadYaw = var14;
         ChamsModule.flag3 = false;
         if (var9) {
            FrameTimer.method754();
         }
      }
   }

   private LivingEntity previewEntity() {
      if (mc.world == null) {
         return null;
      } else {
         if (this.dummy == null || this.dummyWorld != mc.world) {
            this.dummyWorld = mc.world;
            this.dummy = new OtherClientPlayerEntity(mc.world, new GameProfile(UUID.nameUUIDFromBytes("zenith-esp-preview".getBytes()), "Player"));
         }

         return this.dummy;
      }
   }

   public boolean onMouseClicked(double var1, double var3, Enum3 var5) {
      if (!this.open || this.bounds == null || !this.bounds.contains(var1, var3)) {
         return false;
      } else if (this.kind == ModulePreviewStage.Kind.ENTITY && this.isLive() && EntityESPModule.INSTANCE.method561(var1, var3, var5)) {
         return true;
      } else {
         this.draggingModel = true;
         return true;
      }
   }

   public boolean onMouseDragged(double var1, double var3, double var5, double var7) {
      if (!this.open) {
         return false;
      } else if (this.kind == ModulePreviewStage.Kind.ENTITY && EntityESPModule.INSTANCE.method562(var1, var3)) {
         return true;
      } else if (!this.draggingModel) {
         return false;
      } else {
         this.modelYaw += (float)var5 * 1.4F;
         this.modelPitch = MathHelper.clamp(this.modelPitch + (float)var7 * 1.4F, -55.0F, 55.0F);
         return true;
      }
   }

   public void onMouseReleased() {
      this.draggingModel = false;
      EntityESPModule.INSTANCE.method563();
   }

   public static enum Kind {
      MODEL,
      CHAMS,
      TARGET,
      SHADER,
      ENTITY,
      FIREWORK,
      BLOCK,
      WORLD_WEATHER,
      WORLD_VISUALS,
      PARTICLES_AMBIENT,
      PARTICLES_TOTEM,
      PARTICLES_HIT;
   }
}

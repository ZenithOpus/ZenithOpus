package org.zenith.client.modules.impl.render;

import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.projectile.thrown.SplashPotionEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.EggItem;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ExperienceBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SnowballItem;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.RenderState;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.game.ArmorSlots;
import org.zenith.utility.game.TargetUtils;
import org.zenith.utility.network.UseItemUtils;
import org.zenith.utility.render.GlowUtil;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.MathUtils;
import org.zenith.utility.render.ProjectionUtils;

@ModuleInfo(
   name = "Predictions",
   category = ModuleCategory.RENDER,
   description = "module.predictions.description"
)
public final class PredictionsModule extends Module {
   private final BooleanSetting renderItemEntity = new BooleanSetting("module.predictions.renderItemEntity", "module.predictions.renderItemEntity.desc", true);
   private final ModeSetting mode = new ModeSetting(
      "module.predictions.mode", "module.predictions.mode.desc", "module.predictions.sparks", "module.predictions.fire", "module.predictions.frost"
   );
   private static final int CONST_intValue = 1;
   private static final Identifier[] identifiers = new Identifier[]{Identifier.of("zenith", "projectile_fire"), Identifier.of("zenith", "projectile_frost")};
   private static final Identifier identifier = Zenith.method1501("visuals/jumpcircle/glowboost.png");
   private static final double CONST_doubleValue = 1.0E-4;
   private static final float CONST_floatValue = 0.11F;
   private static final Matrix4f matrix4f = new Matrix4f();
   private static final int CONST_intValue2 = 56;
   private static final double CONST_doubleValue2 = 0.017857142857142856;
   private static final double CONST_doubleValue3 = 2.399963;
   private final List<PredictionsModule.Data2> items = new ArrayList<>();
   private final List<List<Vec3d>> items2 = new ArrayList<>();
   private List<Entity> items3 = List.of();
   private final Map<Item, PredictionsModule.Data> items4 = new HashMap<>();
   private final Map<Item, ProjectileEntity> items5 = new HashMap<>();
   private final Matrix4f matrix4f2 = new Matrix4f();
   private final Matrix4f matrix4f3 = new Matrix4f();
   private final Matrix4f matrix4f4 = new Matrix4f();
   private final List<PredictionsModule.Entry> items6 = new ArrayList<>();
   private final Map<Integer, Long> items7 = new HashMap<>();
   private final Map<Integer, Vec3d> items8 = new HashMap<>();
   private long longValue;
   private Map<Integer, Integer> items9 = new HashMap<>();
   private Map<Integer, Integer> items10 = new HashMap<>();
   private static final int CONST_intValue3 = 4;
   private final Map<Integer, Vec3d> items11 = new HashMap<>();
   private long longValue2;
   private World world;
   private final Random random = new Random();
   public static final PredictionsModule INSTANCE = new PredictionsModule();
   public static final List<ItemStack> items12 = List.of(Items.SNOWBALL.getDefaultStack());
   private final List<LivingEntity> items13 = new ArrayList<>();
   private double[] doubleValues = new double[0];
   private double[] doubleValues2 = new double[0];
   private double[] doubleValues3 = new double[0];
   private double[] doubleValues4 = new double[0];
   private double[] doubleValues5 = new double[0];
   private double[] doubleValues6 = new double[0];
   private double[] doubleValues7 = new double[0];
   private float[] floatValues = new float[0];
   private float[] floatValues2 = new float[0];
   private float[] floatValues3 = new float[0];
   private ZenithColor[] zenithColors = new ZenithColor[0];
   private boolean[] flags = new boolean[0];
   private static final int CONST_intValue4 = 48;
   public static final int CONST_intValue5 = 0;
   private boolean flag;
   private static final EntityType<?>[] entityTypes = new EntityType[]{
      EntityType.ENDER_PEARL,
      EntityType.ARROW,
      EntityType.SPECTRAL_ARROW,
      EntityType.TRIDENT,
      EntityType.SNOWBALL,
      EntityType.EGG,
      EntityType.SPLASH_POTION,
      EntityType.LINGERING_POTION,
      EntityType.EXPERIENCE_BOTTLE
   };
   private static Field field;
   private static Field field2;
   private static boolean flag2;

   private PredictionsModule() {
   }

   @Override
   public void onDisable() {
      this.items.clear();
      this.items2.clear();
      this.items3 = List.of();
      this.items4.clear();
      this.items5.clear();
      this.items6.clear();
      this.items7.clear();
      this.items8.clear();
      this.items13.clear();
      this.items9.clear();
      this.items11.clear();
      this.world = null;
      super.onDisable();
   }

   private void method2886() {
      if (mc.world != this.world) {
         this.items5.clear();
         this.items4.clear();
         this.items8.clear();
         this.world = mc.world;
      }
   }

   private boolean method2887(Entity var1, Entity var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      int var15 = 0;

      for (int var16 = this.items13.size(); var15 < var16; var15++) {
         LivingEntity var17 = this.items13.get(var15);
         if (var17 != var1 && var17 != var2 && var17.getBoundingBox().intersects(var3, var5, var7, var9, var11, var13)) {
            return true;
         }
      }

      return false;
   }

   @EventTarget
   public void onTick(TickEvent var1) {
      long var2 = System.currentTimeMillis();

      for (PredictionsModule.Entry var5 : this.items6) {
         var5.update();
         var5.update();
         var5.update();
      }

      this.items6.removeIf(var2x -> var2 - var2x.longValue > 600L);
      this.items.clear();
      this.items2.clear();
      this.method2886();
      if (mc.player != null && mc.world != null) {
         method2925();
         this.method2926();
         this.items3 = this.method2908();
         if (!this.items11.isEmpty()) {
            HashSet var15 = new HashSet();

            for (Entity var6 : this.items3) {
               var15.add(var6.getId());
            }

            this.items11.keySet().retainAll(var15);
         }

         this.items13.clear();
         if (!this.items3.isEmpty()) {
            for (Entity var19 : mc.world.getEntities()) {
               if (var19 instanceof LivingEntity var21 && var21 != mc.player && var21.isAlive()) {
                  this.items13.add(var21);
               }
            }
         }

         for (Entity var20 : this.items3) {
            Vec3d var22 = var20.getVelocity();
            Vec3d var7 = var20.getEntityPos();
            int var9 = 0;
            ArrayList var10 = new ArrayList();
            var10.add(var7);
            Entity var11 = var20 instanceof ProjectileEntity var12 ? var12.getOwner() : null;

            for (int var23 = 0; var23 < 300; var23++) {
               Vec3d var8 = var7;
               var7 = var7.add(var22);
               var22 = this.method2921(var20, var8, var22);
               BlockHitResult var13 = TargetUtils.method3208(var8, var7, ShapeType.COLLIDER, var20);
               if (!var13.getType().equals(Type.MISS)) {
                  var7 = var13.getPos();
               }

               var10.add(var7);
               boolean var14 = this.method2887(
                  var20,
                  var11,
                  Math.min(var8.x, var7.x) - 0.25,
                  Math.min(var8.y, var7.y) - 0.25,
                  Math.min(var8.z, var7.z) - 0.25,
                  Math.max(var8.x, var7.x) + 0.25,
                  Math.max(var8.y, var7.y) + 0.25,
                  Math.max(var8.z, var7.z) + 0.25
               );
               if (var13.getType().equals(Type.BLOCK) || var7.y < -128.0 || var14 || var13.getType().equals(Type.ENTITY)) {
                  this.method2922(var20, var7, var9);
                  break;
               }

               var9++;
            }

            this.items2.add(var10);
         }
      } else {
         this.items3 = List.of();
         this.items13.clear();
         this.items9.clear();
         this.items11.clear();
      }
   }

   @EventTarget
   public void onDraw(DrawEvent var1) {
      for (PredictionsModule.Data2 var3 : this.items) {
         if (ProjectionUtils.method302(var3.vec3d)) {
            Vec3d var4 = ProjectionUtils.method301(var3.vec3d);
            EntityESPModule var5 = EntityESPModule.INSTANCE;
            float var6 = (float)var4.getX();
            float var7 = (float)var4.getY() + EntityESPModule.method608() / 2.0F;
            var5.method609(var1.getCustomDrawContext(), var6, var7, var3.itemStack, this.method2888(var3.intValue), var5.method553(var3.vec3d));
         }
      }
   }

   private Text method2888(int var1) {
      String var2 = String.format(Locale.ROOT, "%.1f", var1 * 50 / 1000.0);
      String var3 = Zenith.getInstance().getLocalization().translate("module.predictions.seconds");
      return Text.literal(var2)
         .append(
            Text.literal(" " + var3)
               .setStyle(Style.EMPTY.withColor(Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7287()))
         );
   }

   @EventTarget
   public void method2889(WorldRenderEvent var1) {
      if (mc.player != null && mc.world != null) {
         this.method2902(var1.getMatrixStack(), ArmorSlots.method5385(mc.player));
         if (!this.items2.isEmpty()) {
            this.method2890(this.items2, var1.getFloatValue());
         }

         if (this.mode.isSelected(0) && !this.items3.isEmpty()) {
            this.method2897(this.items3, System.currentTimeMillis());
         }
      }
   }

   private void method2890(List<List<Vec3d>> var1, float var2) {
      Camera var3 = mc.getEntityRenderDispatcher().camera;
      Vec3d var4 = var3.getCameraPos();
      matrix4f.identity().rotateX(var3.getPitch() * (float) (Math.PI / 180.0)).rotateY((var3.getYaw() + 180.0F) * (float) (Math.PI / 180.0));
      GL11.glEnable(2881);
      RenderState.method1871();
      RenderState.method1877();
      RenderState.method1879();
      RenderState.method1882(false);
      RenderState.method1875(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
      RenderState.method1890(0, GlowUtil.identifier);
      RenderState.method1898("core/position_tex_color");
      BufferBuilder var5 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      float var6 = (float)(System.nanoTime() / 1.0E9 * 5.0 % (Math.PI * 2));

      for (int var7 = 0; var7 < var1.size(); var7++) {
         List var8 = (List)var1.get(var7);
         if (var8.size() >= 2) {
            List var9 = var8;
            if (var7 < this.items3.size()) {
               var9 = new ArrayList(var8.size() + 1);
               var9.add(this.items3.get(var7).getLerpedPos(var2));
               var9.addAll(var8);
            }

            this.method2892((List<Vec3d>)var9, Vec3d.ZERO, var4, var6);
            this.method2893(var5, var9.size(), 0.28599998F, 0.3F);
            this.method2893(var5, var9.size(), 0.11F, 0.9F);
         }
      }

      BuiltBuffer var10 = var5.endNullable();
      if (var10 != null) {
         ImmediateRenderer.method8015(var10);
      }

      RenderState.method1882(true);
      RenderState.method1878();
      RenderState.method1876();
      RenderState.method1872();
      GL11.glDisable(2881);
   }

   private void method2891(int var1) {
      if (this.doubleValues.length < var1) {
         int var2 = Math.max(var1, this.doubleValues.length * 2);
         this.doubleValues = new double[var2];
         this.doubleValues2 = new double[var2];
         this.doubleValues3 = new double[var2];
         this.doubleValues4 = new double[var2];
         this.doubleValues5 = new double[var2];
         this.doubleValues6 = new double[var2];
         this.doubleValues7 = new double[var2];
         this.floatValues = new float[var2];
         this.floatValues2 = new float[var2];
         this.floatValues3 = new float[var2];
         this.zenithColors = new ZenithColor[var2];
         this.flags = new boolean[var2];
      }
   }

   private void method2892(List<Vec3d> var1, Vec3d var2, Vec3d var3, float var4) {
      int var5 = var1.size();
      this.method2891(var5);

      for (int var6 = 0; var6 < var5; var6++) {
         Vec3d var7 = ((Vec3d)var1.get(var6)).add(var2).subtract(var3);
         Vec3d var8 = this.method2894(var1, var6, var5);
         if (var8 == null) {
            this.flags[var6] = true;
            this.zenithColors[var6] = null;
         } else {
            this.flags[var6] = false;
            double var9 = var7.length();
            this.doubleValues7[var6] = Math.max(1.0, var9 / 26.0);
            Vec3d var11 = this.method2895(var8, var7);
            this.doubleValues[var6] = var7.x;
            this.doubleValues2[var6] = var7.y;
            this.doubleValues3[var6] = var7.z;
            this.doubleValues4[var6] = var11.x;
            this.doubleValues5[var6] = var11.y;
            this.doubleValues6[var6] = var11.z;
            float var12 = (float)var6 / (var5 - 1);
            this.floatValues[var6] = MathHelper.clamp(var6 / 3.0F, 0.0F, 1.0F);
            this.floatValues2[var6] = MathHelper.clamp((var5 - 1 - var6) / 6.0F, 0.0F, 1.0F);
            this.floatValues3[var6] = 0.78F + 0.22F * MathHelper.sin(var12 * 11.0F - var4);
            this.zenithColors[var6] = Zenith.getInstance().getStyleManager().getClientColor((int)(var12 * 180.0F));
         }
      }
   }

   private void method2893(BufferBuilder var1, int var2, float var3, float var4) {
      Matrix4f var5 = matrix4f;
      boolean var6 = false;
      float var7 = 0.0F;
      float var8 = 0.0F;
      float var9 = 0.0F;
      float var10 = 0.0F;
      float var11 = 0.0F;
      float var12 = 0.0F;
      float var13 = 0.0F;
      ZenithColor var14 = null;

      for (int var15 = 0; var15 < var2; var15++) {
         if (!this.flags[var15]) {
            float var16 = (float)(var3 * this.doubleValues7[var15]);
            double var17 = this.doubleValues4[var15] * var16;
            double var19 = this.doubleValues5[var15] * var16;
            double var21 = this.doubleValues6[var15] * var16;
            float var23 = (float)(this.doubleValues[var15] + var17);
            float var24 = (float)(this.doubleValues2[var15] + var19);
            float var25 = (float)(this.doubleValues3[var15] + var21);
            float var26 = (float)(this.doubleValues[var15] - var17);
            float var27 = (float)(this.doubleValues2[var15] - var19);
            float var28 = (float)(this.doubleValues3[var15] - var21);
            float var29 = var4 * this.floatValues[var15] * this.floatValues2[var15] * this.floatValues3[var15];
            ZenithColor var30 = this.zenithColors[var15];
            if (var6) {
               int var31 = this.method2896(var14, var13);
               int var32 = this.method2896(var30, var29);
               var1.vertex(var5, var7, var8, var9).texture(0.0F, 0.5F).color(var31);
               var1.vertex(var5, var10, var11, var12).texture(1.0F, 0.5F).color(var31);
               var1.vertex(var5, var26, var27, var28).texture(1.0F, 0.5F).color(var32);
               var1.vertex(var5, var23, var24, var25).texture(0.0F, 0.5F).color(var32);
            }

            var6 = true;
            var7 = var23;
            var8 = var24;
            var9 = var25;
            var10 = var26;
            var11 = var27;
            var12 = var28;
            var13 = var29;
            var14 = var30;
         }
      }
   }

   private Vec3d method2894(List<Vec3d> var1, int var2, int var3) {
      Vec3d var4 = Vec3d.ZERO;
      if (var2 > 0) {
         var4 = var4.add(((Vec3d)var1.get(var2)).subtract((Vec3d)var1.get(var2 - 1)));
      }

      if (var2 < var3 - 1) {
         var4 = var4.add(((Vec3d)var1.get(var2 + 1)).subtract((Vec3d)var1.get(var2)));
      }

      return var4.lengthSquared() < 1.0E-8 ? null : var4.normalize();
   }

   private Vec3d method2895(Vec3d var1, Vec3d var2) {
      Vec3d var3 = var2.lengthSquared() < 1.0E-8 ? new Vec3d(0.0, 0.0, 1.0) : var2.normalize().negate();
      Vec3d var4 = var1.crossProduct(var3);
      if (var4.lengthSquared() < 1.0E-8) {
         var4 = var1.crossProduct(new Vec3d(0.0, 1.0, 0.0));
         if (var4.lengthSquared() < 1.0E-8) {
            var4 = var1.crossProduct(new Vec3d(1.0, 0.0, 0.0));
         }
      }

      return var4.normalize();
   }

   private int method2896(ZenithColor var1, float var2) {
      int var3 = (int)(MathHelper.clamp(var2, 0.0F, 1.0F) * 255.0F);
      return var3 << 24 | var1.getIntValue() << 16 | var1.getIntValue2() << 8 | var1.getIntValue3();
   }

   private void method2897(List<Entity> var1, long var2) {
      Camera var4 = mc.getEntityRenderDispatcher().camera;
      Vec3d var5 = var4.getCameraPos();
      float var6 = var4.getPitch();
      float var7 = var4.getYaw();
      float var8 = var6 * (float) (Math.PI / 180.0);
      float var9 = var7 * (float) (Math.PI / 180.0);
      this.matrix4f2.identity().rotateX(var8).rotateY((var7 + 180.0F) * (float) (Math.PI / 180.0));
      this.matrix4f3.identity().rotateY(-var9).rotateX(var8);
      RenderState.method1871();
      RenderState.method1877();
      RenderState.method1878();
      RenderState.method1882(false);
      RenderState.method1875(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
      RenderState.method1890(0, identifier);
      RenderState.method1898("core/position_tex_color");
      byte var10 = 8;
      ZenithColor[] var11 = new ZenithColor[8];

      for (int var12 = 0; var12 < 8; var12++) {
         var11[var12] = Zenith.getInstance().getStyleManager().getClientColor(var12 * 45);
      }

      float var100 = mc.getRenderTickCounter().getTickProgress(false);
      double var13 = System.nanoTime() % 1000000000000L / 1.0E9;
      long var15 = System.nanoTime();
      float var17 = this.longValue2 == 0L ? 0.05F : MathHelper.clamp((float)(var15 - this.longValue2) / 1.0E9F, 0.0F, 0.1F);
      this.longValue2 = var15;
      float var18 = 1.0F - (float)Math.exp(-var17 * 14.0);
      BufferBuilder var19 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

      for (Entity var21 : var1) {
         Vec3d var22 = var21.getVelocity();
         Vec3d var23 = this.items11.get(var21.getId());
         if (var23 != null && !(var23.squaredDistanceTo(var22) > 1.0)) {
            var23 = var23.lerp(var22, var18);
         } else {
            var23 = var22;
         }

         this.items11.put(var21.getId(), var23);
         double var24 = var23.x * var23.x + var23.y * var23.y + var23.z * var23.z;
         if (!(var24 < 1.0E-4)) {
            Vec3d var26 = var21.getLerpedPos(var100);
            double var27 = var26.x;
            double var29 = var26.y + var21.getHeight() * 0.5;
            double var31 = var26.z;
            double var33 = Math.sqrt(var26.squaredDistanceTo(var5));
            float var35 = (float)MathHelper.clamp(var33 / 22.0, 1.0, 6.0);
            int var36 = var33 > 96.0 ? 28 : 56;
            double var37 = 1.0 / Math.sqrt(var24);
            double var39 = var23.x * var37;
            double var41 = var23.y * var37;
            double var43 = var23.z * var37;
            double var45 = -var43;
            double var47 = 0.0;
            double var49 = var39;
            double var51 = var45 * var45 + var39 * var39;
            if (var51 < 1.0E-4) {
               var45 = 0.0;
               var47 = var43;
               var49 = -var41;
               var51 = var43 * var43 + var49 * var49;
            }

            double var53 = 1.0 / Math.sqrt(var51);
            var45 *= var53;
            var47 *= var53;
            var49 *= var53;
            double var55 = var47 * var43 - var49 * var41;
            double var57 = var49 * var39 - var45 * var43;
            double var59 = var45 * var41 - var47 * var39;
            float var61 = MathHelper.clamp(var21.getWidth() * 0.5F, 0.09F, 0.22F) * var35;
            double var62 = Math.min(2.4, Math.sqrt(var24) * 4.5);
            double var64 = (0.45 + var62 * 0.3) * var35;
            double var66 = (0.34 + var62 * 0.26) * var35;
            double var68 = Math.PI * 3;
            double var70 = var13 * 0.3 + var21.getId() * 0.071;

            for (int var72 = 0; var72 < var36; var72++) {
               double var73 = var70 + var72 * (1.0 / var36);
               var73 -= Math.floor(var73);
               float var75 = (float)Math.min(1.0, var73 / 0.22);
               float var76 = (float)(1.0 - var73);
               float var77 = var75 * var75 * (3.0F - 2.0F * var75);
               float var78 = var76 * var76 * (3.0F - 2.0F * var76);
               float var79 = var77 * var78;
               if (!(var79 < 0.02F)) {
                  double var80 = var73 * (Math.PI * 3) + var72 * 2.399963 * 0.25 + var13 * 0.3;
                  double var82 = var64 * var73;
                  double var84 = var72 * 0.6180339887 % 1.0;
                  double var86 = var66 * (0.35 + var84 * 0.75) * (0.25 + var73 * 0.75);
                  double var88 = Math.cos(var80) * var86;
                  double var90 = Math.sin(var80) * var86;
                  double var92 = var27 - var39 * var82 + var45 * var88 + var55 * var90 - var5.x;
                  double var94 = var29 - var41 * var82 + var47 * var88 + var57 * var90 - var5.y;
                  double var96 = var31 - var43 * var82 + var49 * var88 + var59 * var90 - var5.z;
                  float var98 = var61 * (1.0F - (float)var73 * 0.55F);
                  ZenithColor var99 = var11[var72 % 8];
                  this.method2898(var19, var92, var94, var96, var98 * 2.2F, var79 * 0.7F, var99, false);
                  if (var79 > 0.3F) {
                     this.method2898(var19, var92, var94, var96, var98, var79 * 1.9F, var99, true);
                  }
               }
            }
         }
      }

      BuiltBuffer var101 = var19.endNullable();
      if (var101 != null) {
         ImmediateRenderer.method8015(var101);
      }

      RenderState.method1882(true);
      RenderState.method1876();
      RenderState.method1872();
      RenderState.method1873();
   }

   private void method2898(BufferBuilder var1, double var2, double var4, double var6, float var8, float var9, ZenithColor var10, boolean var11) {
      this.matrix4f4.set(this.matrix4f2).translate((float)var2, (float)var4, (float)var6).mul(this.matrix4f3);
      int var12 = this.method2899(var10, var11 ? var9 : var9 * 0.7F);
      float var13 = var8 * (var11 ? 0.72F : 0.9F);
      var1.vertex(this.matrix4f4, var13, -var13, 0.0F).texture(0.0F, 1.0F).color(var12);
      var1.vertex(this.matrix4f4, -var13, -var13, 0.0F).texture(1.0F, 1.0F).color(var12);
      var1.vertex(this.matrix4f4, -var13, var13, 0.0F).texture(1.0F, 0.0F).color(var12);
      var1.vertex(this.matrix4f4, var13, var13, 0.0F).texture(0.0F, 0.0F).color(var12);
   }

   private int method2899(ZenithColor var1, float var2) {
      int var3 = MathHelper.clamp((int)(var2 * 255.0F), 0, 255);
      return var3 << 24 | var1.getIntValue() << 16 | var1.getIntValue2() << 8 | var1.getIntValue3();
   }

   private void method2900(Entity var1) {
      int var2 = var1.getId();
      long var3 = System.currentTimeMillis();
      if (!this.items7.containsKey(var2) || var3 - this.items7.get(var2) >= 20L) {
         this.items7.put(var2, var3);
         Vec3d var5 = var1.getEntityPos();
         Vec3d var6 = var1.getVelocity().normalize().multiply(-0.05);

         for (int var7 = 0; var7 < 3; var7++) {
            double var8 = (this.random.nextDouble() - 0.5) * 0.35;
            double var10 = (this.random.nextDouble() - 0.5) * 0.35;
            double var12 = (this.random.nextDouble() - 0.5) * 0.35;
            double var14 = var6.x + (this.random.nextDouble() - 0.5) * 0.06;
            double var16 = var6.y + (this.random.nextDouble() - 0.5) * 0.06 - 0.01;
            double var18 = var6.z + (this.random.nextDouble() - 0.5) * 0.06;
            this.items6.add(new PredictionsModule.Entry(var5.x + var8, var5.y + var10, var5.z + var12, var14, var16, var18, this.random.nextInt(90), var3));
         }

         this.items7.entrySet().removeIf(var2x -> var3 - var2x.getValue() > 5000L);
      }
   }

   private void method2901(MatrixStack var1) {
      if (!this.items6.isEmpty()) {
         long var2 = System.currentTimeMillis();
         RenderState.method1871();
         RenderState.method1877();
         RenderState.method1879();
         RenderState.method1875(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
         RenderState.method1890(0, GlowUtil.identifier);
         RenderState.method1898("core/position_tex_color");
         BufferBuilder var4 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         Camera var5 = mc.getEntityRenderDispatcher().camera;

         for (PredictionsModule.Entry var7 : this.items6) {
            long var8 = var2 - var7.longValue;
            float var10 = (float)var8 / 600.0F;
            float var11;
            if (var10 < 0.2F) {
               var11 = var10 / 0.2F;
            } else {
               var11 = 1.0F - (var10 - 0.2F) / 0.8F;
            }

            var11 = MathHelper.clamp(var11, 0.0F, 1.0F);
            float var12 = 0.27F * (1.0F - var10 * 0.6F);
            double var13 = var7.doubleValue - var5.getCameraPos().x;
            double var15 = var7.doubleValue2 - var5.getCameraPos().y;
            double var17 = var7.doubleValue3 - var5.getCameraPos().z;
            MatrixStack var19 = new MatrixStack();
            var19.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var5.getPitch()));
            var19.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(var5.getYaw() + 180.0F));
            var19.translate(var13, var15, var17);
            var19.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-var5.getYaw()));
            var19.multiply(RotationAxis.POSITIVE_X.rotationDegrees(var5.getPitch()));
            Matrix4f var20 = var19.peek().getPositionMatrix();
            int var21 = Zenith.getInstance().getStyleManager().getClientColor(var7.intValue).method7294(var11).method7287();
            var4.vertex(var20, -var12 / 2.0F, -var12 / 2.0F, 0.0F).texture(0.0F, 0.0F).color(var21);
            var4.vertex(var20, -var12 / 2.0F, var12 / 2.0F, 0.0F).texture(0.0F, 1.0F).color(var21);
            var4.vertex(var20, var12 / 2.0F, var12 / 2.0F, 0.0F).texture(1.0F, 1.0F).color(var21);
            var4.vertex(var20, var12 / 2.0F, -var12 / 2.0F, 0.0F).texture(1.0F, 0.0F).color(var21);
         }

         ImmediateRenderer.method8015(var4.end());
         RenderState.method1878();
         RenderState.method1876();
         RenderState.method1872();
      }
   }

   public void method2902(MatrixStack var1, List<ItemStack> var2) {
      this.method2886();
      if (mc.player != null && mc.world != null && !var2.isEmpty()) {
         ItemStack var3 = (ItemStack)var2.getFirst();
         PredictionsModule.Data var4 = this.items4.get(var3.getItem());
         if (var4 == null || var4.getIntValue() != mc.player.age || var4.getWorld() != mc.world) {
            var var5 = this.method2903(var3, mc.player.getActiveItem().getItem(), RotationUtils.method6911());
            var5 = var5 == null ? List.of() : var5.stream().filter(Objects::nonNull).toList();
            var4 = new PredictionsModule.Data(mc.player.age, mc.world, var5);
            this.items4.put(var3.getItem(), var4);
         }

         if (!var4.getItems().isEmpty()) {
            this.method2905(var1, var4.getItems());
         }
      }
   }

   public List<HitResult> method2903(ItemStack var1, Item var2, Rotation var3) {
      this.method2886();
      Object var4 = var1.getItem();
      Objects.requireNonNull(var4);

      return (List<HitResult>)(switch (var4) {
         case ExperienceBottleItem var6 -> this.method2914(this.method2904(var1, () -> new ExperienceBottleEntity(mc.world, mc.player, var1)), var3, 0.8);
         case SplashPotionItem var7 -> this.method2914(this.method2904(var1, () -> new SplashPotionEntity(mc.world, mc.player, var1)), var3, 0.55);
         case TridentItem var8 when var8.equals(var2) && mc.player.getItemUseTime() >= 10 -> this.method2914(
            this.method2904(var1, () -> new TridentEntity(mc.world, mc.player, var1)), var3, 2.5
         );
         case SnowballItem var9 -> this.method2914(this.method2904(var1, () -> new SnowballEntity(mc.world, mc.player, var1)), var3, 1.5);
         case EggItem var10 -> this.method2914(this.method2904(var1, () -> new EggEntity(mc.world, mc.player, var1)), var3, 1.5);
         case EnderPearlItem var11 -> this.method2914(this.method2904(var1, () -> new EnderPearlEntity(mc.world, mc.player, var1)), var3, 1.5);
         case BowItem var12 when var12.equals(var2) && mc.player.isUsingItem() -> this.method2914(
            this.method2904(var1, () -> new ArrowEntity(mc.world, mc.player, var1, var1)),
            var3,
            3.0F * MathHelper.clamp((mc.player.getItemUseTime() + mc.getRenderTickCounter().getTickProgress(false)) / 20.0F, 0.0F, 1.0F)
         );
         case CrossbowItem var13 when CrossbowItem.isCharged(var1) -> {
            ChargedProjectilesComponent var14 = var1.get(DataComponentTypes.CHARGED_PROJECTILES);
            ArrayList var15 = new ArrayList();
            if (var14 != null) {
               float var16 = var14.getProjectiles().getFirst().isOf(Items.FIREWORK_ROCKET) ? 100.0F : 3.0F;
               var15.add(this.method2917(var3.method3135(), this.method2904(var1, () -> new ArrowEntity(mc.world, mc.player, var1, var1)), var16));
               if (var14.getProjectiles().size() > 2) {
                  float var17 = mc.player.getPitch() / 90.0F;
                  float var18 = var17 * var17 * var17 * var17 * var17;
                  float var19 = MathHelper.lerp(Math.abs(var18), 10, 90);
                  float var20 = MathHelper.lerp(var18, 0, 10);
                  var15.add(
                     this.method2917(
                        new Rotation(mc.player.getYaw() - var19, mc.player.getPitch() - var20).method3135(),
                        this.method2904(var1, () -> new ArrowEntity(mc.world, mc.player, var1, var1)),
                        var16
                     )
                  );
                  var15.add(
                     this.method2917(
                        new Rotation(mc.player.getYaw() + var19, mc.player.getPitch() - var20).method3135(),
                        this.method2904(var1, () -> new ArrowEntity(mc.world, mc.player, var1, var1)),
                        var16
                     )
                  );
               }
            }

            yield var15;
         }
         default -> null;
      });
   }

   private ProjectileEntity method2904(ItemStack var1, Supplier<? extends ProjectileEntity> var2) {
      ProjectileEntity var3 = this.items5.get(var1.getItem());
      if (var3 != null && var3.getEntityWorld() == mc.world && var3.getOwner() == mc.player) {
         var3.setPosition(mc.player.getX(), mc.player.getEyeY() - 0.1F, mc.player.getZ());
         var3.setVelocity(Vec3d.ZERO);
         var3.setOnGround(false);
         if (var3 instanceof ThrownItemEntity var4) {
            var4.setItem(var1);
         }
      } else {
         var3 = (ProjectileEntity)var2.get();
         this.items5.put(var1.getItem(), var3);
      }

      return var3;
   }

   public void method2905(MatrixStack var1, List<HitResult> var2) {
      Camera var3 = mc.getEntityRenderDispatcher().camera;
      long var4 = System.nanoTime();
      float var6 = this.longValue == 0L ? 0.05F : MathHelper.clamp((float)(var4 - this.longValue) / 1.0E9F, 0.0F, 0.1F);
      this.longValue = var4;
      float var7 = 1.0F - (float)Math.exp(-var6 * 20.0);
      double var8 = var4 / 1.0E9;
      float var10 = (float)(var8 * 3.2 % (Math.PI * 2));
      float var11 = (float)(var8 * 0.6 % (Math.PI / 2));
      RenderState.method1871();
      RenderState.method1877();
      RenderState.method1879();
      RenderState.method1882(false);
      RenderState.method1875(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
      RenderState.method1890(0, GlowUtil.identifier);
      RenderState.method1898("core/position_tex_color");
      BufferBuilder var12 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

      for (int var13 = 0; var13 < var2.size(); var13++) {
         HitResult var14 = (HitResult)var2.get(var13);
         Vec3d var15 = var14.getPos();
         Vec3d var16 = this.items8.get(var13);
         if (var16 != null && !(var16.squaredDistanceTo(var15) > 64.0)) {
            var16 = var16.lerp(var15, var7);
         } else {
            var16 = var15;
         }

         this.items8.put(var13, var16);
         Vec3d var17 = var16.subtract(var3.getCameraPos());
         boolean var18 = var14.getType().equals(Type.ENTITY);
         ZenithColor var19 = var18 ? ZenithColor.INSTANCE4 : Zenith.getInstance().getStyleManager().getClientColor(90);
         var1.push();
         var1.translate(var17.x, var17.y, var17.z);
         switch (this.method2923(var14)) {
            case UP:
            default:
               break;
            case DOWN:
               var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
               break;
            case NORTH:
               var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
               break;
            case SOUTH:
               var1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
               break;
            case WEST:
               var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0F));
               break;
            case EAST:
               var1.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
         }

         var1.translate(0.0, 0.01, 0.0);
         Matrix4f var20 = var1.peek().getPositionMatrix();
         float var21 = 0.85F + 0.15F * MathHelper.sin(var10);
         this.method2906(var12, var20, 0.62F * var21, this.method2896(var19, 0.22F));
         this.method2907(var12, var20, 0.3F * var21, 0.045F, this.method2896(var19, 0.95F), 0.0F, 48, 1.0F);
         this.method2907(var12, var20, 0.17F, 0.018F, this.method2896(var19, 0.55F), 0.0F, 48, 1.0F);
         this.method2907(var12, var20, 0.4F, 0.05F, this.method2896(var19, 0.8F), var11, 4, 0.22F);
         var1.pop();
      }

      BuiltBuffer var22 = var12.endNullable();
      if (var22 != null) {
         ImmediateRenderer.method8015(var22);
      }

      RenderState.method1882(true);
      RenderState.method1878();
      RenderState.method1876();
      RenderState.method1872();
   }

   private void method2906(BufferBuilder var1, Matrix4f var2, float var3, int var4) {
      var1.vertex(var2, -var3, 0.0F, -var3).texture(0.0F, 0.0F).color(var4);
      var1.vertex(var2, -var3, 0.0F, var3).texture(0.0F, 1.0F).color(var4);
      var1.vertex(var2, var3, 0.0F, var3).texture(1.0F, 1.0F).color(var4);
      var1.vertex(var2, var3, 0.0F, -var3).texture(1.0F, 0.0F).color(var4);
   }

   private void method2907(BufferBuilder var1, Matrix4f var2, float var3, float var4, int var5, float var6, int var7, float var8) {
      float var9 = var3 - var4;
      float var10 = var3 + var4;
      float var11 = (float) (Math.PI * 2) / var7;

      for (int var12 = 0; var12 < var7; var12++) {
         float var13 = var6 + var12 * var11;
         float var14 = var13 + var11 * var8;
         float var15 = MathHelper.cos(var13);
         float var16 = MathHelper.sin(var13);
         float var17 = MathHelper.cos(var14);
         float var18 = MathHelper.sin(var14);
         var1.vertex(var2, var15 * var9, 0.0F, var16 * var9).texture(0.0F, 0.5F).color(var5);
         var1.vertex(var2, var15 * var10, 0.0F, var16 * var10).texture(1.0F, 0.5F).color(var5);
         var1.vertex(var2, var17 * var10, 0.0F, var18 * var10).texture(1.0F, 0.5F).color(var5);
         var1.vertex(var2, var17 * var9, 0.0F, var18 * var9).texture(0.0F, 0.5F).color(var5);
      }
   }

   public List<Entity> method2908() {
      return UseItemUtils.method6510()
         .filter(
            var1 -> (
                  var1 instanceof PersistentProjectileEntity
                     || var1 instanceof ThrownItemEntity
                     || var1 instanceof ItemEntity && this.renderItemEntity.isEnabled()
               )
               && !this.method2924(var1)
         )
         .toList();
   }

   public boolean method2909() {
      return this.isEnabled() && this.mode.getIndex() >= 1;
   }

   public Identifier method2910() {
      int var1 = this.mode.getIndex() - 1;
      return var1 >= 0 && var1 < identifiers.length ? identifiers[var1] : null;
   }

   public boolean method2911(Entity var1) {
      if (this.method2909() && var1 != null) {
         return var1 instanceof PersistentProjectileEntity
               || var1 instanceof ThrownItemEntity
               || var1 instanceof ItemEntity && this.renderItemEntity.isEnabled()
            ? !this.method2924(var1)
            : false;
      } else {
         return false;
      }
   }

   public int method2912() {
      this.flag = true;
      return 0;
   }

   public boolean method2913() {
      boolean var1 = this.flag;
      this.flag = false;
      return var1;
   }

   public List<HitResult> method2914(ProjectileEntity var1, Rotation var2, double var3) {
      return new ArrayList<>(Collections.singleton(this.method2917(var2.method3135(), var1, var3)));
   }

   public List<HitResult> method2915(ProjectileEntity var1, double var2) {
      return this.method2914(var1, RotationUtils.method6911(), var2);
   }

   public List<HitResult> method2916(ItemStack var1, Rotation var2, Vec3d var3, Vec3d var4, int var5) {
      if (var1.getItem() instanceof BowItem && mc.player.isUsingItem()) {
         float var6 = mc.player.getItemUseTime() + mc.getRenderTickCounter().getTickProgress(false) + var5;
         double var7 = 3.0F * MathHelper.clamp(var6 / 20.0F, 0.0F, 1.0F);
         HitResult var9 = this.method2918(var3, var4, var2.method3135(), this.method2904(var1, () -> new ArrowEntity(mc.world, mc.player, var1, var1)), var7);
         return (List<HitResult>)(var9 == null ? List.of() : new ArrayList<>(Collections.singleton(var9)));
      } else {
         return null;
      }
   }

   public HitResult method2917(Vec3d var1, ProjectileEntity var2, double var3) {
      Vec3d var5 = mc.player.getEntityPos().subtract(mc.player.lastX, mc.player.lastY, mc.player.lastZ);
      if (var2 instanceof ArrowEntity var6 && var6.getItemStack().getItem() instanceof CrossbowItem) {
         var5 = Vec3d.ZERO;
      }

      return this.method2918(mc.player.getEyePos().add(MathUtils.method2656(mc.player).subtract(mc.player.getEntityPos())), var5, var1, var2, var3);
   }

   public HitResult method2918(Vec3d var1, Vec3d var2, Vec3d var3, ProjectileEntity var4, double var5) {
      double var7 = var3.length();
      return var7 <= 1.0E-6 ? null : this.method2920(var1, var3.multiply(var5 / var7).add(var2), var4);
   }

   public HitResult method2919(ProjectileEntity var1) {
      return this.method2920(var1.getEntityPos(), var1.getVelocity(), var1);
   }

   public HitResult method2920(Vec3d var1, Vec3d var2, ProjectileEntity var3) {
      for (int var5 = 0; var5 < 300; var5++) {
         Vec3d var4 = var1;
         var1 = var1.add(var2);
         var2 = this.method2921(var3, var4, var2);
         BlockHitResult var6 = TargetUtils.method3208(var4, var1, ShapeType.COLLIDER, var3);
         if (!var6.getType().equals(Type.MISS)) {
            return var6;
         }

         var var7 = mc.world
            .getOtherEntities(
               var3,
               new Box(var4, var1).expand(0.3),
               var1x -> var1x != var3.getOwner() && var1x instanceof LivingEntity var2x && var2x != mc.player && var2x.isAlive()
            );
         if (!var7.isEmpty()) {
            return new EntityHitResult((Entity)var7.getFirst(), var1);
         }

         if (var1.y < -128.0) {
            break;
         }
      }

      return null;
   }

   public Vec3d method2921(Entity var1, Vec3d var2, Vec3d var3) {
      boolean var4 = Objects.requireNonNull(mc.world).getBlockState(BlockPos.ofFloored(var2)).getFluidState().isIn(FluidTags.WATER);
      Objects.requireNonNull(var1);

      float var5 = switch (var1) {
         case TridentEntity var8 -> 0.99F;
         case PersistentProjectileEntity var9 when var4 -> 0.6F;
         default -> var4 ? 0.8F : 0.99F;
      };
      return var3.multiply(var5).add(0.0, -var1.getFinalGravity(), 0.0);
   }

   private void method2922(Entity var1, Vec3d var2, int var3) {
      switch (var1) {
         case ItemEntity var6:
            this.items.add(new PredictionsModule.Data2(var6.getStack(), var2, var3));
            break;
         case ThrownItemEntity var7:
            this.items.add(new PredictionsModule.Data2(var7.getStack(), var2, var3));
            break;
         case PersistentProjectileEntity var8:
            this.items.add(new PredictionsModule.Data2(var8.getItemStack(), var2, var3));
            break;
         default:
      }
   }

   private Direction method2923(HitResult var1) {
      if (var1 instanceof BlockHitResult var3) {
         return var3.getSide();
      } else {
         Vec3d var2 = var1.getPos().subtract(mc.player.getEyePos()).normalize();
         return Direction.getFacing(var2.x, var2.y, var2.z);
      }
   }

   private boolean method2924(Entity var1) {
      boolean var2 = this.items9.getOrDefault(var1.getId(), 0) >= 4;
      boolean var3 = var1 instanceof ItemEntity && (var1.isOnGround() || UseItemUtils.method6515(var1.getBoundingBox().expand(2.0), Blocks.WATER));
      return var2 || var3;
   }

   private static void method2925() {
      if (!flag2) {
         flag2 = true;

         try {
            Class var0 = Class.forName("dev.tr7zw.entityculling.EntityCullingModBase");
            field = var0.getField("instance");
            field2 = var0.getField("tickCullWhitelists");
         } catch (Throwable var2) {
            field = null;
            field2 = null;
         }
      }

      if (field2 != null) {
         try {
            Object var4 = field.get(null);
            if (var4 == null) {
               return;
            }

            Set var1 = (Set)field2.get(var4);
            if (var1 == null || var1.contains(EntityType.ENDER_PEARL)) {
               return;
            }

            Collections.addAll(var1, entityTypes);
         } catch (Throwable var3) {
            field2 = null;
         }
      }
   }

   private void method2926() {
      var var1 = this.items10;
      var1.clear();
      UseItemUtils.method6510()
         .filter(var0 -> var0 instanceof PersistentProjectileEntity || var0 instanceof ThrownItemEntity || var0 instanceof ItemEntity)
         .forEach(var2 -> {
            boolean var3 = var2.getX() != var2.lastX || var2.getY() != var2.lastY || var2.getZ() != var2.lastZ;
            var1.put(var2.getId(), var3 ? 0 : this.items9.getOrDefault(var2.getId(), 0) + 1);
         });
      this.items10 = this.items9;
      this.items9 = var1;
   }

   private static final class Data {
      private final int intValue;
      private final World world;
      private final List<HitResult> items;

      public int intValue() {
         return this.intValue;
      }

      public World world() {
         return this.world;
      }

      public List<HitResult> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.world, other.world) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, world, items);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "world=" + this.world + ", " + "items=" + this.items + "]";
      }

      private Data(int var1, World var2, List<HitResult> var3) {
         this.intValue = var1;
         this.world = var2;
         this.items = var3;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public World getWorld() {
         return this.world;
      }

      public List<HitResult> getItems() {
         return this.items;
      }
   }

   private static final class Data2 {
      private final ItemStack itemStack;
      private final Vec3d vec3d;
      private final int intValue;

      public ItemStack itemStack() {
         return this.itemStack;
      }

      public Vec3d vec3d() {
         return this.vec3d;
      }

      public int intValue() {
         return this.intValue;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.itemStack, other.itemStack) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.intValue, other.intValue);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(itemStack, vec3d, intValue);
      }

      @Override
      public String toString() {
         return "Data2[" + "itemStack=" + this.itemStack + ", " + "vec3d=" + this.vec3d + ", " + "intValue=" + this.intValue + "]";
      }

      private Data2(ItemStack var1, Vec3d var2, int var3) {
         this.itemStack = var1;
         this.vec3d = var2;
         this.intValue = var3;
      }

      public ItemStack getItemStack() {
         return this.itemStack;
      }

      public Vec3d getVec3d() {
         return this.vec3d;
      }

      public int getIntValue() {
         return this.intValue;
      }
   }

   private static class Entry {
      public double doubleValue;
      public double doubleValue2;
      public double doubleValue3;
      public double doubleValue4;
      public double doubleValue5;
      public double doubleValue6;
      public int intValue;
      public long longValue;

      public Entry(double var1, double var3, double var5, double var7, double var9, double var11, int var13, long var14) {
         this.doubleValue = var1;
         this.doubleValue2 = var3;
         this.doubleValue3 = var5;
         this.doubleValue4 = var7;
         this.doubleValue5 = var9;
         this.doubleValue6 = var11;
         this.intValue = var13;
         this.longValue = var14;
      }

      public void update() {
         this.doubleValue = this.doubleValue + this.doubleValue4;
         this.doubleValue2 = this.doubleValue2 + this.doubleValue5;
         this.doubleValue3 = this.doubleValue3 + this.doubleValue6;
         this.doubleValue5 -= 0.002;
         this.doubleValue4 *= 0.98;
         this.doubleValue5 *= 0.98;
         this.doubleValue6 *= 0.98;
      }
   }
}

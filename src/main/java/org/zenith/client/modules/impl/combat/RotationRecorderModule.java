package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.shape.VoxelShape;
import org.zenith.ChatLogger;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.base.rotation.RotationUtils;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.Event2;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.client.settings.ButtonSetting;
import org.zenith.client.settings.ModeSetting;
import org.zenith.utility.game.PlayerSimulation;
import org.zenith.utility.misc.Vec2f;

@ModuleInfo(
   name = "RotationRecorder",
   category = ModuleCategory.COMBAT,
   description = "module.rotationrecorder.description"
)
public final class RotationRecorderModule extends Module {
   public static final RotationRecorderModule INSTANCE = new RotationRecorderModule();
   private static final String text = "module.rotationRecorder.mode.human";
   private static final String text2 = "module.rotationRecorder.mode.aim";
   private final ModeSetting mode = new ModeSetting(
      "module.rotationRecorder.mode", "module.rotationRecorder.mode.desc", "module.rotationRecorder.mode.human", "module.rotationRecorder.mode.aim"
   );
   private final BooleanSetting disableLogs = new BooleanSetting("module.rotationRecorder.disableLogs", "module.rotationRecorder.disableLogs.desc", false);
   private final ButtonSetting openRecordingsFolder = new ButtonSetting("module.rotationRecorder.openRecordingsFolder", "K", this::method4942);
   private static final String text3 = "rotation_dataset_v30_simpy_v2.csv";
   private static final String text4 = "rotation_aim_dataset_v2.csv";
   private static final String text5 = "rotation_world_snapshots_v7.jsonl";
   private static final int CONST_intValue = 30;
   private static final int CONST_intValue2 = 2;
   private static final int CONST_intValue3 = 1;
   private static final String text6 = "state_on_ground";
   private static final int CONST_intValue4 = 0;
   private static final int CONST_intValue5 = 1;
   private static final long CONST_longValue = -1L;
   private static final int CONST_intValue6 = 6;
   private static final int CONST_intValue7 = 3;
   private static final int CONST_intValue8 = 5;
   private static final long CONST_longValue2 = 128L;
   private static final float CONST_floatValue = 50.0F;
   private static final float CONST_floatValue2 = 50.0F;
   private static final float CONST_floatValue3 = 50.0F;
   private static final float CONST_floatValue4 = 20.0F;
   private static final float CONST_floatValue5 = 50.0F;
   private static final int CONST_intValue9 = 20;
   private static final int CONST_intValue10 = 40;
   private static final int CONST_intValue11 = 10;
   private static final int CONST_intValue12 = 64;
   private static final double CONST_doubleValue = 256.0;
   private static final Path path = Path.of("rotation_recordings", "rotation_dataset_v30_simpy_v2.csv");
   private static final Path path2 = Path.of("rotation_recordings", "rotation_aim_dataset_v2.csv");
   private static final Path path3 = Path.of("rotation_recordings", "rotation_world_snapshots_v7.jsonl");
   private static final Path path4 = Path.of("rotation_recordings", "rotation_world_snapshots_v7.jsonl.next");
   private BufferedWriter bufferedWriter;
   private BufferedWriter bufferedWriter2;
   private boolean flag = false;
   private boolean flag2 = false;
   private long longValue = 0L;
   private long longValue2 = 0L;
   private long longValue3 = 1L;
   private long longValue4 = 0L;
   private int intValue = 0;
   private boolean flag3 = false;
   private boolean flag4 = false;
   private float floatValue = 0.0F;
   private float floatValue2 = 0.0F;
   private float floatValue3 = 0.0F;
   private float floatValue4 = 0.0F;
   private float floatValue5 = 0.0F;
   private float floatValue6 = 0.0F;
   private float floatValue7 = 0.0F;
   private float floatValue8 = 0.0F;
   private float floatValue9 = 0.0F;
   private float floatValue10 = 0.0F;
   private int intValue2 = 0;
   private int intValue3 = 0;
   private float floatValue11 = 50.0F;
   private float floatValue12 = 50.0F;
   private float floatValue13 = 0.0F;
   private float floatValue14 = 0.0F;
   private RotationRecorderModule.Entry entry = null;
   private RotationRecorderModule.Entry4 entry4 = null;
   private boolean flag5 = false;
   private final Map<Integer, RotationRecorderModule.Entry9> items = new HashMap<>();
   private RotationRecorderModule.Entry7 entry7 = null;

   private RotationRecorderModule() {
   }

   @Override
   public void onEnable() {
      this.flag3 = this.mode.method2101("module.rotationRecorder.mode.aim");
      this.method4940();
      this.method4941();
      this.method4945();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.method4935();
      this.method4944();
      this.method4948();
      this.method4940();
      if (!this.disableLogs.isEnabled()) {
         ChatLogger.method2019("RotationRecorder: saved " + this.intValue + " rows");
      }

      super.onDisable();
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100()) {
         if (var1.getPacket3() instanceof PlayerPositionLookS2CPacket) {
            mc.execute(() -> {
               if (this.isEnabled()) {
                  this.method4936();
                  this.method4940();
               }
            });
         }
      }
   }

   @EventTarget
   public void method4915(AttackEvent var1) {
      if (var1.getType4() == AttackEvent.Type.POST && mc.player != null && var1.getEntity() instanceof LivingEntity var2) {
         this.flag5 = true;
         int var5 = mc.player.age;
         RotationRecorderModule.Entry9 var4 = this.items.get(var2.getId());
         if (this.entry7 != null && var5 > this.entry7.intValue4) {
            this.method4935();
         }

         if (this.entry7 != null) {
            if (!this.entry7.method5026(var2)) {
               this.method4937();
            } else {
               this.entry7.intValue3 = var5;
               this.entry7.intValue4 = Math.max(this.entry7.intValue4, var5 + 40);
            }
         } else {
            if (var4 != null && var4.method5031(var2) && var4.deque.size() >= 21) {
               this.method4932(var2, var4, var5);
            }
         }
      }
   }

   @EventTarget
   public void method4916(Event2 var1) {
      if (!mc.isPaused() && mc.player != null && mc.world != null && !mc.player.isSpectator()) {
         if (this.entry7 != null && mc.player.age > this.entry7.intValue4) {
            this.method4935();
         }

         boolean var2 = this.flag5;
         this.flag5 = false;
         float var3 = this.flag3 ? rotationManager.method7844().getFloatValue() : mc.player.getYaw();
         float var4 = this.flag3 ? rotationManager.method7844().getFloatValue2() : mc.player.getPitch();
         Rotation var5 = new Rotation(var3, var4);
         if (!this.flag4) {
            this.floatValue = var3;
            this.floatValue2 = var4;
            this.flag4 = true;
         }

         float var6 = MathHelper.wrapDegrees(mc.player.getYaw() - this.floatValue);
         float var7 = mc.player.getPitch() - this.floatValue2;
         int var8 = 0;
         if (this.flag3) {
            Rotation var9 = new Rotation(mc.player.lastYawClient, mc.player.lastPitchClient);
            Vec2f var10 = var9.method3131(zenith.getRotationManager().method7844());
            Vec2f var11 = new Vec2f(var6, var7);
            var6 = var10.getFloatValue();
            var7 = var10.getFloatValue2();
            var8 = var10.getFloatValue() == var11.getFloatValue()
                  && var10.getFloatValue2() == var11.getFloatValue2()
                  && mc.player.getYaw() == zenith.getRotationManager().method7844().getFloatValue()
                  && mc.player.getPitch() == zenith.getRotationManager().method7844().getFloatValue2()
               ? 0
               : 1;
         }

         PlayerInput var25 = this.method4970();
         Box var26 = mc.player.getBoundingBox();
         Vec3d var27 = mc.player.getEyePos();
         byte var12 = 0;
         RotationRecorderModule.Entry10 var13 = null;
         RotationRecorderModule.Entry3 var14 = null;
         Vec3d var15 = null;
         HashSet var16 = new HashSet();

         for (Entity var18 : mc.world.getEntities()) {
            if (var18 instanceof LivingEntity var19 && var19 != mc.player && var19.isAlive() && !(mc.player.squaredDistanceTo(var19) > 256.0)) {
               Box var20 = this.method4931(var19);
               Vec3d var21 = this.method4920(var19);
               if (var20 != null && var21 != null) {
                  if (var12 == 0) {
                     var12 = 1;
                     var13 = this.method4957(var26);
                     var14 = this.method4975(var3, var4, var25);
                     var15 = this.method4919(var5, var27, var25);
                  }

                  RotationRecorderModule.Entry9 var22 = this.items
                     .compute(
                        var19.getId(),
                        (var1x, var2x) -> (RotationRecorderModule.Entry9)(var2x != null && var2x.method5031(var19)
                           ? var2x
                           : new RotationRecorderModule.Entry9(var19))
                     );
                  var16.add(var19.getId());
                  this.method4938(var22);
                  RotationRecorderModule.Entry var23 = this.entry;
                  RotationRecorderModule.Entry var24 = this.method4955(
                     var5, var27, var19, var20, var21, var6, var7, var25, var2, (var8 != 0), var13, var14, var15
                  );
                  this.method4939(var22);
                  if (var24 != null) {
                     var22.method5032(var24);
                     if (this.entry7 != null && this.entry7.method5026(var19) && var23 != null) {
                        this.method4934(var23, var24);
                     }
                  }
               }
            }
         }

         this.items
            .entrySet()
            .removeIf(var2x -> !(var16.contains(var2x.getKey()) || this.entry7 != null && this.entry7.intValue == var2x.getKey()));
         if (this.entry7 != null && !var16.contains(this.entry7.intValue)) {
            this.method4937();
         }

         this.floatValue = var3;
         this.floatValue2 = var4;
      } else {
         this.method4935();
         this.method4940();
      }
   }

   private RotationRecorderModule.Data2 method4917(
      Rotation var1,
      Vec3d var2,
      Vec3d var3,
      LivingEntity var4,
      Box var5,
      Vec3d var6,
      float var7,
      float var8,
      Vec3d var9,
      Vec3d var10,
      float var11,
      PlayerInput var12,
      boolean var13,
      boolean var14
   ) {
      Vec3d[] var15 = new Vec3d[]{
         new Vec3d(var5.minX, var5.minY, var5.minZ),
         new Vec3d(var5.minX, var5.minY, var5.maxZ),
         new Vec3d(var5.minX, var5.maxY, var5.minZ),
         new Vec3d(var5.minX, var5.maxY, var5.maxZ),
         new Vec3d(var5.maxX, var5.minY, var5.minZ),
         new Vec3d(var5.maxX, var5.minY, var5.maxZ),
         new Vec3d(var5.maxX, var5.maxY, var5.minZ),
         new Vec3d(var5.maxX, var5.maxY, var5.maxZ)
      };
      Vec3d var16 = new Vec3d((var5.minX + var5.maxX) * 0.5, (var5.minY + var5.maxY) * 0.5, (var5.minZ + var5.maxZ) * 0.5);
      Rotation var17 = this.method4918(var2, var16);
      Vec2f var18 = var1.method3131(var17);
      float var19 = var18.getFloatValue();
      float var20 = var18.getFloatValue2();
      if (this.isFinite(var19) && this.isFinite(var20)) {
         Box var21 = var5.offset(var6.subtract(var4.getEntityPos()));
         Vec2f var22 = var1.method3131(this.method4918(var3, this.method4923(var21)));
         float var23 = var22.getFloatValue();
         float var24 = var22.getFloatValue2();
         if (this.isFinite(var23) && this.isFinite(var24)) {
            float var25;
            float var26 = (float)Math.sin(var25 = (float)Math.toRadians(var23));
            float var27 = (float)Math.cos(var25);
            Vec3d var28 = var6.add(var4.getEyePos().subtract(var4.getEntityPos()));
            Rotation var29 = new Rotation(var4.getYaw(), var4.getPitch());
            Vec2f var30 = var29.method3131(this.method4918(var28, var2));
            float var31 = var30.getFloatValue();
            float var32 = var30.getFloatValue2();
            Vec3d var33 = var2.subtract(var28);
            if (this.isFinite(var31) && this.isFinite(var32) && this.method4977(var33) && !(var33.lengthSquared() <= 1.0E-10)) {
               float var34 = (float)Math.toRadians(var31);
               float var35 = (float)Math.toRadians(var32);
               float var36 = (float)MathHelper.clamp(var29.method3135().dotProduct(var33.normalize()), -1.0, 1.0);
               float var37 = Float.MAX_VALUE;
               float var38 = -Float.MAX_VALUE;
               float var39 = Float.MAX_VALUE;
               float var40 = -Float.MAX_VALUE;

               for (Vec3d var44 : var15) {
                  Rotation var45 = this.method4918(var2, var44);
                  Vec2f var46 = var1.method3131(var45);
                  float var47 = var46.getFloatValue();
                  float var48 = var46.getFloatValue2();
                  if (this.isFinite(var47) && this.isFinite(var48)) {
                     var47 = method4953(var47, var19);
                     var37 = Math.min(var37, var47);
                     var38 = Math.max(var38, var47);
                     var39 = Math.min(var39, var48);
                     var40 = Math.max(var40, var48);
                  }
               }

               if (var37 == Float.MAX_VALUE) {
                  var37 = var19;
                  var38 = var19;
                  var39 = var20;
                  var40 = var20;
               }

               var19 = method4953(var19, var19);
               float var98 = var38 - var37;
               float var99 = var40 - var39;
               float var100 = (float)(var16.x - var2.x);
               float var101 = (float)(var16.y - var2.y);
               float var102 = (float)(var16.z - var2.z);
               float var103 = (float)var2.distanceTo(var16);
               float var105 = this.hypot(var100, var102);
               Vec3d var106 = var16.add(var9);
               Vec2f var49 = var17.method3131(this.method4918(var2, var106));
               float var50 = (float)var2.distanceTo(var106) - var103;
               Vec3d var51 = var2.add(var10);
               Vec2f var52 = var17.method3131(this.method4918(var51, var16));
               float var53 = (float)var51.distanceTo(var16) - var103;
               if (this.isFinite(var49.getFloatValue())
                  && this.isFinite(var49.getFloatValue2())
                  && this.isFinite(var50)
                  && this.isFinite(var52.getFloatValue())
                  && this.isFinite(var52.getFloatValue2())
                  && this.isFinite(var53)) {
                  int var54 = var37 <= 0.0F && var38 >= 0.0F ? 1 : 0;
                  int var55 = var39 <= 0.0F && var40 >= 0.0F ? 1 : 0;
                  float var56 = 0.0F;
                  if (var54 != 0 && Math.abs(var98) > 1.0E-6F) {
                     var56 = MathHelper.clamp((0.0F - var37) / var98, 0.0F, 1.0F);
                  }

                  float var57 = 0.0F;
                  if (var55 != 0 && Math.abs(var99) > 1.0E-6F) {
                     var57 = MathHelper.clamp((0.0F - var39) / var99, 0.0F, 1.0F);
                  }

                  int var58 = var54 != 0 && var55 != 0 ? 1 : 0;
                  this.floatValue7 = var58 != 0 ? MathHelper.clamp(this.floatValue7 + 1.0F, 0.0F, 50.0F) : 0.0F;
                  this.floatValue8 = var58 != 0 ? 0.0F : MathHelper.clamp(this.floatValue8 + 1.0F, 0.0F, 50.0F);
                  float var61 = var7 - this.floatValue3;
                  float var62 = var8 - this.floatValue4;
                  float var63 = var61 - this.floatValue5;
                  float var64 = var62 - this.floatValue6;
                  float var65 = this.entry != null ? this.method4926(this.entry.data24.getData3().getFloatValue(), this.entry.floatValue2) : var19;
                  float var66 = this.entry != null ? this.method4926(this.entry.data24.getData3().getFloatValue2(), this.entry.floatValue2) : var20;
                  float var67 = this.entry != null ? this.entry.data2.getData17().getFloatValue() : var103;
                  float var68 = MathHelper.wrapDegrees(var19 - var65);
                  float var69 = var20 - var66;
                  float var70 = var103 - var67;
                  float var71 = MathHelper.clamp(var20 / Math.max(Math.abs(var99), 0.1F), -20.0F, 20.0F);
                  float var72 = (float)Math.sqrt(var19 * var19 + var20 * var20);
                  Vec3d var73 = var16.subtract(var2).normalize();
                  Vec3d var74 = var1.method3135().normalize();
                  Vec3d var75 = new Rotation(var1.getFloatValue() + 90.0F, 0.0F).method3135().normalize();
                  Vec3d var76 = var75.crossProduct(var74).normalize();
                  float var77 = (float)var73.dotProduct(var75);
                  float var78 = (float)var73.dotProduct(var76);
                  float var79 = (float)var73.dotProduct(var74);
                  float var80 = this.method4925(var7, var11);
                  float var81 = this.method4925(var8, var11);
                  float var82 = this.method4924(var20, var11);
                  float var83 = 0.0F;
                  float var84 = 0.0F;
                  float var85 = 0.0F;
                  float var86 = 0.0F;
                  if (this.entry != null) {
                     float var87 = this.entry.data24.getData3().getFloatValue();
                     float var88 = this.entry.data24.getData3().getFloatValue2();
                     float var89 = var87 - var80;
                     float var90 = var88 - var81;
                     float var91 = MathHelper.wrapDegrees(this.method4926(var89, var11));
                     float var92 = this.method4924(var91, var11);
                     var83 = Math.abs(var87) - Math.abs(var92);
                     var84 = Math.abs(var88) - Math.abs(var90);
                     var85 = this.method4924(MathHelper.wrapDegrees(var19 - var91), var11);
                     var86 = var82 - var90;
                  }

                  this.floatValue9 = var80 != 0.0F ? this.floatValue9 + 1.0F : 0.0F;
                  this.floatValue10 = var81 != 0.0F ? this.floatValue10 + 1.0F : 0.0F;
                  int var107 = this.method4927(var80);
                  int var108 = this.method4927(var81);
                  this.floatValue13 = this.method4929(var80, var107, this.intValue2, this.floatValue13);
                  this.floatValue14 = this.method4929(var81, var108, this.intValue3, this.floatValue14);
                  float var109 = this.method4930(var80, this.floatValue13);
                  float var110 = this.method4930(var81, this.floatValue14);
                  this.floatValue11 = this.method4928(var107, this.intValue2, this.floatValue11);
                  this.floatValue12 = this.method4928(var108, this.intValue3, this.floatValue12);
                  this.intValue2 = var107 != 0 ? var107 : this.intValue2;
                  this.intValue3 = var108 != 0 ? var108 : this.intValue3;
                  float var111 = var12.forward() ? 1.0F : 0.0F;
                  float var112 = var12.backward() ? 1.0F : 0.0F;
                  float var93 = var12.left() ? 1.0F : 0.0F;
                  float var94 = var12.right() ? 1.0F : 0.0F;
                  float var95 = var12.jump() ? 1.0F : 0.0F;
                  RotationRecorderModule.Data2 var96 = new RotationRecorderModule.Data2(
                     new RotationRecorderModule.Data6(
                        this.method4924(var37 - var19, var11),
                        this.method4924(var38 - var19, var11),
                        this.method4924(var39 - var20, var11),
                        this.method4924(var40 - var20, var11),
                        var71,
                        this.method4924(var72, var11),
                        var77,
                        var78,
                        var79,
                        var56,
                        var57,
                        (var58 != 0)
                     ),
                     new RotationRecorderModule.Data17(var103, var105, var101, this.floatValue7, this.floatValue8),
                     new RotationRecorderModule.Data7(
                        this.method4924(var68, var11),
                        this.method4924(var69, var11),
                        var70,
                        var85,
                        var86,
                        var26,
                        var27,
                        this.method4924(var24, var11),
                        new RotationRecorderModule.Data5(this.method4924(var49.getFloatValue(), var11), this.method4924(var49.getFloatValue2(), var11), var50)
                     ),
                     new RotationRecorderModule.Data12((float)Math.sin(var34), (float)Math.cos(var34), (float)Math.sin(var35), (float)Math.cos(var35), var36),
                     new RotationRecorderModule.Data13(
                        var80,
                        var81,
                        this.method4925(var61, var11),
                        this.method4925(var62, var11),
                        this.method4925(var63, var11),
                        this.method4925(var64, var11),
                        this.floatValue9,
                        this.floatValue10,
                        this.floatValue11,
                        this.floatValue12,
                        var83,
                        var84,
                        var109,
                        var110,
                        this.floatValue13,
                        this.floatValue14
                     ),
                     new RotationRecorderModule.Data16(var111, var112, var93, var94, var95),
                     new RotationRecorderModule.Data5(this.method4924(var52.getFloatValue(), var11), this.method4924(var52.getFloatValue2(), var11), var53),
                     new RotationRecorderModule.Data22(
                        mc.player.getAttackCooldownProgress(0.0F), MathHelper.clamp((float)mc.player.ticksSinceLastAttack, 0.0F, 50.0F), var13, var14
                     )
                  );
                  if (!var96.method4982()) {
                     return null;
                  } else {
                     this.floatValue3 = var7;
                     this.floatValue4 = var8;
                     this.floatValue5 = var61;
                     this.floatValue6 = var62;
                     return var96;
                  }
               } else {
                  return null;
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private Rotation method4918(Vec3d var1, Vec3d var2) {
      return RotationUtils.method6912(var2.subtract(var1));
   }

   private Vec3d method4919(Rotation var1, Vec3d var2, PlayerInput var3) {
      Vec3d var4 = mc.player.getEntityPos();
      Vec3d var5 = var2.subtract(var4);

      Vec3d var10000;
      try {
         PlayerSimulation var6 = PlayerSimulation.method6033(PlayerSimulation.Entry.method6092(var3));
         var6.floatValue = var1.getFloatValue();
         var6.floatValue2 = var1.getFloatValue2();
         var6.tick();
         Vec3d var7 = var6.vec3d.add(var5);
         var10000 = this.method4977(var7) ? var7 : var2;
      } catch (Throwable var8) {
         return var2;
      }

      return var10000;
   }

   private Vec3d method4920(LivingEntity var1) {
      if (var1 != null && var1.getTrackedPosition() != null) {
         Vec3d var2 = var1.getTrackedPosition().getPos();
         return this.method4977(var2) ? var2 : null;
      } else {
         return null;
      }
   }

   private Vec3d method4921(Vec3d var1) {
      if (this.entry != null && var1 != null) {
         Vec3d var2 = var1.subtract(this.entry.vec3d);
         return this.method4977(var2) ? var2 : Vec3d.ZERO;
      } else {
         return Vec3d.ZERO;
      }
   }

   private Vec3d method4922(Box var1) {
      if (this.entry != null && var1 != null) {
         RotationRecorderModule.Data20 var2 = this.entry.data24.getData3().getData20();
         Vec3d var3 = this.method4923(var1);
         Vec3d var4 = var3.subtract(new Vec3d(var2.getFloatValue(), var2.getFloatValue2(), var2.getFloatValue3()));
         return this.method4977(var4) ? var4 : Vec3d.ZERO;
      } else {
         return Vec3d.ZERO;
      }
   }

   private Vec3d method4923(Box var1) {
      return new Vec3d((var1.minX + var1.maxX) * 0.5, (var1.minY + var1.maxY) * 0.5, (var1.minZ + var1.maxZ) * 0.5);
   }

   private float hypot(float var1, float var2) {
      return (float)Math.sqrt(var1 * var1 + var2 * var2);
   }

   private float method4924(float var1, float var2) {
      return var1 / var2;
   }

   private float method4925(float var1, float var2) {
      return Math.round(var1 / var2);
   }

   private float method4926(float var1, float var2) {
      return this.isFinite(var2) && var2 > 0.0F ? var1 * var2 : var1;
   }

   private int method4927(float var1) {
      if (var1 > 0.0F) {
         return 1;
      } else {
         return var1 < 0.0F ? -1 : 0;
      }
   }

   private float method4928(int var1, int var2, float var3) {
      return var1 != 0 && var2 != 0 && var1 != var2 ? 0.0F : Math.min(var3 + 1.0F, 50.0F);
   }

   private float method4929(float var1, int var2, int var3, float var4) {
      float var5 = Math.abs(var1);
      if (var2 == 0) {
         return 0.0F;
      } else {
         return var3 != 0 && var2 == var3 ? Math.max(var4, var5) : var5;
      }
   }

   private float method4930(float var1, float var2) {
      return var2 > 0.0F ? Math.abs(var1) / var2 : 0.0F;
   }

   private Box method4931(LivingEntity var1) {
      return var1 == null ? null : var1.getBoundingBox();
   }

   private void method4932(LivingEntity var1, RotationRecorderModule.Entry9 var2, int var3) {
      var var4 = new ArrayList<>(var2.deque);
      int var5;
      if ((var5 = var4.size() - 20 - 1) >= 0) {
         for (int var6 = var5; var6 < var4.size() - 1; var6++) {
            if (((RotationRecorderModule.Entry)var4.get(var6 + 1)).intValue2 != ((RotationRecorderModule.Entry)var4.get(var6)).intValue2 + 1) {
               return;
            }
         }

         this.entry4 = new RotationRecorderModule.Entry4();

         for (int var7 = var5; var7 < var4.size() - 1; var7++) {
            if (!this.entry4.method4999(this.method4933((RotationRecorderModule.Entry)var4.get(var7), (RotationRecorderModule.Entry)var4.get(var7 + 1)))) {
               this.method4937();
               return;
            }
         }

         RotationRecorderModule.Entry var8 = (RotationRecorderModule.Entry)var4.get(var4.size() - 1);
         this.entry7 = new RotationRecorderModule.Entry7(var1.getId(), var1.getUuid(), var8.intValue2, var3, var3 + 40);
      }
   }

   private RotationRecorderModule.Entry5 method4933(RotationRecorderModule.Entry var1, RotationRecorderModule.Entry var2) {
      RotationRecorderModule.Entry3 var3 = this.method4974(var1, var1, this.method4972(var1), this.method4973(var1));
      return RotationRecorderModule.Entry5.method5006(var1, var2, var3, false);
   }

   private void method4934(RotationRecorderModule.Entry var1, RotationRecorderModule.Entry var2) {
      if (this.entry7 != null && this.entry4 != null) {
         if (var1.intValue2 >= this.entry7.intValue2) {
            if (var1.intValue2 == this.entry7.intValue2 && var2.intValue2 == this.entry7.intValue2 + 1) {
               if (!this.entry4.method4999(this.method4933(var1, var2))) {
                  this.method4937();
               } else {
                  this.entry7.intValue2 = var2.intValue2;
               }
            } else {
               this.method4937();
            }
         }
      }
   }

   private void method4935() {
      if (this.entry7 != null && this.entry4 != null && this.entry7.intValue2 >= this.entry7.intValue4) {
         int var1 = Math.max(0, this.entry7.intValue2 - this.entry7.intValue3);
         this.entry4.method5000(var1);
         this.entry7 = null;
      } else {
         this.method4937();
      }
   }

   private void method4936() {
      if (this.entry7 != null && this.entry4 != null) {
         int var1 = Math.max(0, this.entry7.intValue2 - this.entry7.intValue3);
         this.entry4.method5000(var1);
         this.entry7 = null;
      } else {
         this.method4937();
      }
   }

   private void method4937() {
      if (this.entry4 != null) {
         this.entry4.method5001();
      }

      this.entry7 = null;
   }

   private void method4938(RotationRecorderModule.Entry9 var1) {
      this.floatValue3 = var1.floatValue;
      this.floatValue4 = var1.floatValue2;
      this.floatValue5 = var1.floatValue3;
      this.floatValue6 = var1.floatValue4;
      this.floatValue7 = var1.floatValue5;
      this.floatValue8 = var1.floatValue6;
      this.floatValue9 = var1.floatValue7;
      this.floatValue10 = var1.floatValue8;
      this.intValue2 = var1.intValue2;
      this.intValue3 = var1.intValue3;
      this.floatValue11 = var1.floatValue9;
      this.floatValue12 = var1.floatValue10;
      this.floatValue13 = var1.floatValue11;
      this.floatValue14 = var1.floatValue12;
      this.entry = var1.deque.peekLast();
   }

   private void method4939(RotationRecorderModule.Entry9 var1) {
      var1.floatValue = this.floatValue3;
      var1.floatValue2 = this.floatValue4;
      var1.floatValue3 = this.floatValue5;
      var1.floatValue4 = this.floatValue6;
      var1.floatValue5 = this.floatValue7;
      var1.floatValue6 = this.floatValue8;
      var1.floatValue7 = this.floatValue9;
      var1.floatValue8 = this.floatValue10;
      var1.intValue2 = this.intValue2;
      var1.intValue3 = this.intValue3;
      var1.floatValue9 = this.floatValue11;
      var1.floatValue10 = this.floatValue12;
      var1.floatValue11 = this.floatValue13;
      var1.floatValue12 = this.floatValue14;
   }

   private void method4940() {
      this.flag4 = false;
      this.floatValue = 0.0F;
      this.floatValue2 = 0.0F;
      this.floatValue3 = 0.0F;
      this.floatValue4 = 0.0F;
      this.floatValue5 = 0.0F;
      this.floatValue6 = 0.0F;
      this.floatValue7 = 0.0F;
      this.floatValue8 = 0.0F;
      this.floatValue9 = 0.0F;
      this.floatValue10 = 0.0F;
      this.intValue2 = 0;
      this.intValue3 = 0;
      this.floatValue11 = 50.0F;
      this.floatValue12 = 50.0F;
      this.floatValue13 = 0.0F;
      this.floatValue14 = 0.0F;
      this.entry = null;
      this.entry4 = null;
      this.entry7 = null;
      this.items.clear();
      this.flag5 = false;
   }

   private void method4941() {
      this.flag = false;
      this.longValue = 0L;
      this.intValue = 0;

      try {
         Path var1 = this.method4943();
         Files.createDirectories(var1.getParent());
         int var2 = (!Files.exists(var1) || Files.size(var1) == 0L) && Zenith.getInstance().getEntry().getUsername().equals("Bogdan") ? 1 : 0;
         this.bufferedWriter = Files.newBufferedWriter(var1, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
         this.method4952((var2 != 0));
      } catch (IOException var3) {
         this.bufferedWriter = null;
         this.flag = true;
         System.err.println("[RotationRecorder] Failed to open dataset writer: " + var3.getMessage());
      }
   }

   private void method4942() {
      Path var1 = path.getParent().toAbsolutePath();

      try {
         Files.createDirectories(var1);
         new ProcessBuilder("explorer.exe", var1.toString()).start();
      } catch (IOException var3) {
         System.err.println("[RotationRecorder] Failed to open recordings folder: " + var3.getMessage());
      }
   }

   private Path method4943() {
      return this.flag3 ? path2 : path;
   }

   private void method4944() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:166)
      //
      // Bytecode:
      // 00: nop
      // 01: aload 0
      // 02: getfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter Ljava/io/BufferedWriter;
      // 05: ifnonnull 09
      // 08: return
      // 09: nop
      // 0a: aload 0
      // 0b: getfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter Ljava/io/BufferedWriter;
      // 0e: invokevirtual java/io/BufferedWriter.flush ()V
      // 11: aload 0
      // 12: getfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter Ljava/io/BufferedWriter;
      // 15: invokevirtual java/io/BufferedWriter.close ()V
      // 18: aload 0
      // 19: aconst_null
      // 1a: putfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter Ljava/io/BufferedWriter;
      // 1d: goto 33
      // 20: nop
      // 21: astore 1
      // 22: aload 0
      // 23: aconst_null
      // 24: putfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter Ljava/io/BufferedWriter;
      // 27: goto 33
      // 2a: nop
      // 2b: astore 2
      // 2c: aload 0
      // 2d: aconst_null
      // 2e: putfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter Ljava/io/BufferedWriter;
      // 31: aload 2
      // 32: athrow
      // 33: nop
      // 34: return
   }

   private void method4945() {
      this.flag2 = false;
      this.longValue2 = 0L;

      try {
         Files.createDirectories(path3.getParent());
         this.method4946();
         this.bufferedWriter2 = Files.newBufferedWriter(path3, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
      } catch (IOException var2) {
         this.bufferedWriter2 = null;
         this.flag2 = true;
         System.err.println("[RotationRecorder] Failed to open snapshot writer: " + var2.getMessage());
      }
   }

   private void method4946() {
      this.longValue3 = 1L;
      if (Files.exists(path3)) {
         long var1;
         if ((var1 = this.method4950()) > 0L) {
            this.longValue3 = var1;
         } else {
            long var3 = 0L;

            try (BufferedReader var5 = Files.newBufferedReader(path3, StandardCharsets.UTF_8)) {
               String var6;
               while ((var6 = var5.readLine()) != null) {
                  long var7;
                  if ((var7 = this.method4947(var6)) > var3) {
                     var3 = var7;
                  }
               }

               this.longValue3 = var3 + 1L;
            } catch (IOException var11) {
               System.err.println("[RotationRecorder] Failed to scan existing snapshots; starting from snapshot_id=1: " + var11.getMessage());
               this.longValue3 = 1L;
            }
         }
      }
   }

   private long method4947(String var1) {
      int var2;
      if ((var2 = var1.indexOf("\"snapshot_id\"")) < 0) {
         return -1L;
      } else {
         int var3;
         if ((var3 = var1.indexOf(58, var2)) < 0) {
            return -1L;
         } else {
            int var4 = var3 + 1;

            while (var4 < var1.length() && Character.isWhitespace(var1.charAt(var4))) {
               var4++;
            }

            int var5 = var4;
            if (var4 < var1.length() && var1.charAt(var4) == '-') {
               var4++;
            }

            while (var4 < var1.length() && Character.isDigit(var1.charAt(var4))) {
               var4++;
            }

            if (var4 <= var5) {
               return -1L;
            } else {
               try {
                  return Long.parseLong(var1.substring(var5, var4));
               } catch (NumberFormatException var7) {
                  return -1L;
               }
            }
         }
      }
   }

   private void method4948() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:166)
      //
      // Bytecode:
      // 00: nop
      // 01: aload 0
      // 02: getfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter2 Ljava/io/BufferedWriter;
      // 05: ifnonnull 09
      // 08: return
      // 09: nop
      // 0a: aload 0
      // 0b: getfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter2 Ljava/io/BufferedWriter;
      // 0e: invokevirtual java/io/BufferedWriter.flush ()V
      // 11: aload 0
      // 12: getfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter2 Ljava/io/BufferedWriter;
      // 15: invokevirtual java/io/BufferedWriter.close ()V
      // 18: aload 0
      // 19: aconst_null
      // 1a: putfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter2 Ljava/io/BufferedWriter;
      // 1d: goto 33
      // 20: nop
      // 21: astore 1
      // 22: aload 0
      // 23: aconst_null
      // 24: putfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter2 Ljava/io/BufferedWriter;
      // 27: goto 33
      // 2a: nop
      // 2b: astore 2
      // 2c: aload 0
      // 2d: aconst_null
      // 2e: putfield org/zenith/client/modules/impl/combat/RotationRecorderModule.bufferedWriter2 Ljava/io/BufferedWriter;
      // 31: aload 2
      // 32: athrow
      // 33: nop
      // 34: aload 0
      // 35: invokevirtual org/zenith/client/modules/impl/combat/RotationRecorderModule.method4949 ()V
      // 38: return
   }

   private void method4949() {
      try {
         if (!Files.exists(path3)) {
            return;
         }

         String var1 = this.longValue3 + " " + Files.size(path3) + " " + Files.getLastModifiedTime(path3).toMillis();
         Files.writeString(path4, var1, StandardCharsets.UTF_8);
      } catch (IOException var4) {
         try {
            Files.deleteIfExists(path4);
         } catch (IOException var3) {
         }
      }
   }

   private long method4950() {
      try {
         if (!Files.exists(path4)) {
            return -1L;
         } else {
            String[] var1 = Files.readString(path4, StandardCharsets.UTF_8).trim().split("\\s+");
            if (var1.length != 3) {
               return -1L;
            } else {
               long var2 = Long.parseLong(var1[0]);
               long var4 = Long.parseLong(var1[1]);
               long var6 = Long.parseLong(var1[2]);
               return var2 >= 1L && var4 == Files.size(path3) && var6 == Files.getLastModifiedTime(path3).toMillis() ? var2 : -1L;
            }
         }
      } catch (RuntimeException | IOException var8) {
         return -1L;
      }
   }

   private void method4951(long var1, RotationRecorderModule.Entry5 var3) {
      if (this.bufferedWriter != null && !this.flag) {
         if (var3 != null && var3.isValid()) {
            try {
               StringBuilder var4 = new StringBuilder(16384);
               var4.append(var1).append(',');
               var4.append(var3.longValue).append(',');
               var4.append(var3.intValue);
               var4.append(',').append(var3.floatValue);
               if (this.flag3) {
                  var3.method5008(var4);
               } else {
                  var3.method5007(var4);
               }

               this.bufferedWriter.write(var4.toString());
               this.bufferedWriter.newLine();
               this.longValue++;
               this.intValue++;
               if (this.longValue % 128L == 0L) {
                  this.bufferedWriter.flush();
               }
            } catch (IllegalStateException | IOException var5) {
               this.flag = true;
               System.err.println("[RotationRecorder] Failed to write dataset row: " + var5.getMessage());
            }
         }
      }
   }

   private void method4952(boolean var1) throws IOException {
      if (var1 && this.bufferedWriter != null) {
         StringBuilder var2 = new StringBuilder(16384);
         var2.append("session_id,timestamp_ms,player_age,mouse_degrees_per_count");
         if (this.flag3) {
            RotationRecorderModule.Entry5.method5003(new RotationRecorderModule.Entry8(var2));
         } else {
            RotationRecorderModule.Entry5.method5002(new RotationRecorderModule.Entry8(var2));
         }

         this.bufferedWriter.write(var2.toString());
         this.bufferedWriter.newLine();
         this.bufferedWriter.flush();
      }
   }

   private static float method4953(float var0, float var1) {
      float var2;
      return !Float.isFinite(var2 = var0 - var1) ? var0 : var1 + MathHelper.wrapDegrees(var2);
   }

   private boolean isFinite(float var1) {
      return (!Float.isNaN(var1) && !Float.isInfinite(var1));
   }

   private long method4954() {
      this.longValue4 = Math.max(this.longValue4 + 1L, Math.max(1L, Instant.now().toEpochMilli()));
      return this.longValue4;
   }

   private RotationRecorderModule.Entry method4955(
      Rotation var1,
      Vec3d var2,
      LivingEntity var3,
      Box var4,
      Vec3d var5,
      float var6,
      float var7,
      PlayerInput var8,
      boolean var9,
      boolean var10,
      RotationRecorderModule.Entry10 var11,
      RotationRecorderModule.Entry3 var12,
      Vec3d var13
   ) {
      Vec3d var14 = mc.player.getEntityPos();
      Vec3d var15 = this.method4922(var4);
      Vec3d var16 = this.method4921(var14);
      float var17 = Rotation.method3138();
      if (this.isFinite(var17) && !(var17 <= 0.0F)) {
         boolean var18 = mc.player.isOnGround();
         RotationRecorderModule.Data2 var19 = this.method4917(var1, var2, var13, var3, var4, var5, var6, var7, var15, var16, var17, var8, var18, var9);
         if (var19 != null && var19.method4982()) {
            float var20 = var1.getFloatValue();
            float var21 = var1.getFloatValue2();
            Vec3d var22 = mc.player.getEyePos();
            Vec3d var23 = mc.player.getVelocity();
            Box var24 = mc.player.getBoundingBox();
            Vec3d var25 = this.entry == null ? var14 : this.entry.vec3d;
            Vec3d var26 = this.method4923(var4);
            float var27 = this.method4924(var1.method3131(this.method4918(var2, var26)).getFloatValue(), var17);
            float var28 = this.method4924(var1.method3131(this.method4918(var2, var26)).getFloatValue2(), var17);
            boolean var29 = this.method4971();
            RotationRecorderModule.Data10 var30 = this.method4956(mc.player.isSprinting(), var24, var11);
            if (!var30.method4982()) {
               return null;
            } else {
               RotationRecorderModule.Data24 var31 = new RotationRecorderModule.Data24(
                  new RotationRecorderModule.Data19(
                     RotationRecorderModule.Data20.method5034(var14),
                     RotationRecorderModule.Data20.method5034(var22),
                     var20,
                     var21,
                     RotationRecorderModule.Data20.method5034(var23),
                     RotationRecorderModule.Data20.method5034(var25),
                     RotationRecorderModule.Data9.method5011(var24)
                  ),
                  RotationRecorderModule.Data21.method5036(var8),
                  new RotationRecorderModule.Data4(
                     mc.player.horizontalCollision,
                     mc.player.verticalCollision,
                     mc.player.isTouchingWater(),
                     mc.player.isSwimming(),
                     mc.player.isSubmergedInWater(),
                     mc.player.isGliding(),
                     mc.player.isInSneakingPose(),
                     mc.player.isCrawling(),
                     mc.player.getPose() == null ? -1.0F : mc.player.getPose().ordinal(),
                     (float)mc.player.fallDistance,
                     (float)mc.player.jumpingCooldown,
                     mc.player.jumping,
                     mc.player.hasVehicle(),
                     mc.player.getAbilities().flying,
                     mc.player.isSpectator()
                  ),
                  new RotationRecorderModule.Data3(
                     RotationRecorderModule.Data9.method5011(var4),
                     RotationRecorderModule.Data20.method5034(var26),
                     RotationRecorderModule.Data20.method5034(var5),
                     RotationRecorderModule.Data20.method5034(var15),
                     var27,
                     var28
                  ),
                  RotationRecorderModule.Data.method4980(var12)
               );
               return !var31.method4982()
                  ? null
                  : new RotationRecorderModule.Entry(
                     var19,
                     var31,
                     var30,
                     var14,
                     var23,
                     var24,
                     var8,
                     var18,
                     var29,
                     mc.player.isSprinting(),
                     (float)mc.player.fallDistance,
                     mc.player.jumpingCooldown,
                     mc.player.jumping,
                     mc.player.isGliding(),
                     mc.player.horizontalCollision,
                     mc.player.verticalCollision,
                     mc.player.isTouchingWater(),
                     mc.player.isSwimming(),
                     mc.player.isSubmergedInWater(),
                     mc.player.getPose(),
                     mc.player.isInSneakingPose(),
                     mc.player.isCrawling(),
                     var10,
                     var17,
                     System.currentTimeMillis(),
                     mc.player.age
                  );
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private RotationRecorderModule.Data10 method4956(boolean var1, Box var2, RotationRecorderModule.Entry10 var3) {
      StatusEffectInstance var4 = this.method4966(StatusEffects.LEVITATION);
      StatusEffectInstance var5 = this.method4966(StatusEffects.JUMP_BOOST);
      boolean var6 = this.method4967(StatusEffects.SLOW_FALLING);
      boolean var7 = this.method4967(StatusEffects.BLINDNESS);
      boolean var8 = this.method4967(StatusEffects.DOLPHINS_GRACE);
      int var9 = var3 == null ? (var2 == null ? 0 : MathHelper.floor(var2.minX)) : var3.intValue;
      int var10 = var3 == null ? (var2 == null ? 0 : MathHelper.floor(var2.minY)) : var3.intValue2;
      int var11 = var3 == null ? (var2 == null ? 0 : MathHelper.floor(var2.minZ)) : var3.intValue3;
      int var12 = var3 == null ? 0 : var3.intValue4;
      int var13 = var3 == null ? 0 : var3.intValue5;
      int var14 = var3 == null ? 0 : var3.intValue6;
      long var15 = var3 == null ? -1L : var3.longValue;
      int var17 = var3 == null ? 0 : var3.intValue7;
      int var18 = var3 != null && var15 >= 0L ? 1 : 0;
      return new RotationRecorderModule.Data10(
         new RotationRecorderModule.Data14(30.0F, false, 2.0F, (float)var18),
         new RotationRecorderModule.Data8(
            var1,
            mc.player.getAbilities().allowFlying,
            mc.player.hasNoGravity(),
            mc.player.hasNoDrag(),
            mc.player.isUsingItem(),
            this.method4969(),
            mc.player.getStepHeight(),
            this.method4968(EntityAttributes.MOVEMENT_SPEED, 0.1F),
            this.method4968(EntityAttributes.GRAVITY, 0.08F),
            this.method4968(EntityAttributes.JUMP_STRENGTH, 0.42F),
            this.method4968(EntityAttributes.SNEAKING_SPEED, 0.3F),
            this.method4968(EntityAttributes.WATER_MOVEMENT_EFFICIENCY, 0.0F)
         ),
         new RotationRecorderModule.Data18(
            var6, var4 != null, var5 != null, var7, var8, var4 == null ? -1.0F : var4.getAmplifier(), var5 == null ? -1.0F : var5.getAmplifier()
         ),
         new RotationRecorderModule.Data15((float)var15, (float)var9, (float)var10, (float)var11, (float)var12, (float)var13, (float)var14, (float)var17)
      );
   }

   private RotationRecorderModule.Entry10 method4957(Box var1) {
      if (var1 != null && this.bufferedWriter2 != null && !this.flag2) {
         int var2 = MathHelper.floor(var1.minX) - 6;
         int var3 = MathHelper.floor(var1.minY) - 3;
         int var4 = MathHelper.floor(var1.minZ) - 6;
         int var5 = MathHelper.floor(var1.maxX) + 6;
         int var6 = MathHelper.floor(var1.maxY) + 5;
         int var7 = MathHelper.floor(var1.maxZ) + 6;
         int var8 = var5 - var2 + 1;
         int var9 = var6 - var3 + 1;
         int var10 = var7 - var4 + 1;
         if (var8 > 0 && var9 > 0 && var10 > 0) {
            long var11 = this.longValue3++;
            IdentityHashMap var13 = new IdentityHashMap();
            HashMap var14 = null;
            ArrayList var15 = new ArrayList();
            int[] var16 = new int[var8 * var9 * var10];
            int var17 = 0;
            var15.add(this.method4959());
            Mutable var18 = new Mutable();

            for (int var19 = var3; var19 <= var6; var19++) {
               for (int var20 = var4; var20 <= var7; var20++) {
                  for (int var21 = var2; var21 <= var5; var21++) {
                     var18.set(var21, var19, var20);
                     BlockState var22 = mc.world.getBlockState(var18);
                     FluidState var23 = mc.world.getFluidState(var18);
                     Integer var24;
                     if (var22 != null && !var22.isAir() || var23 != null && !var23.isEmpty()) {
                        if (var22 != null && var23 == var22.getFluidState()) {
                           var24 = (Integer)var13.get(var22);
                           if (var24 == null) {
                              var24 = var15.size();
                              var13.put(var22, var24);
                              var15.add(this.method4960(var22, var23, var18));
                           }
                        } else {
                           if (var14 == null) {
                              var14 = new HashMap();
                           }

                           String var25 = this.method4958(var22, var23);
                           var24 = (Integer)var14.get(var25);
                           if (var24 == null) {
                              var24 = var15.size();
                              var14.put(var25, var24);
                              var15.add(this.method4960(var22, var23, var18));
                           }
                        }
                     } else {
                        var24 = 0;
                     }

                     var16[var17++] = var24;
                  }
               }
            }

            try {
               StringBuilder var27 = new StringBuilder(32768);
               var27.append('{');
               var27.append("\"snapshot_id\":").append(var11).append(',');
               var27.append("\"snapshot_version\":").append(1).append(',');
               var27.append("\"schema\":\"rotation_world_snapshot_v7\",");
               var27.append("\"session_hint\":").append(this.entry4 == null ? 0L : this.entry4.longValue).append(',');
               var27.append("\"player_age\":").append(mc.player.age).append(',');
               this.method4964(var27, "origin", var2, var3, var4).append(',');
               this.method4964(var27, "size", var8, var9, var10).append(',');
               var27.append("\"palette\":[");

               for (int var28 = 0; var28 < var15.size(); var28++) {
                  if (var28 > 0) {
                     var27.append(',');
                  }

                  var27.append((String)var15.get(var28));
               }

               var27.append("],\"blocks_rle\":");
               this.method4965(var27, var16, var17);
               var27.append('}');
               this.bufferedWriter2.write(var27.toString());
               this.bufferedWriter2.newLine();
               this.longValue2++;
               if (this.longValue2 % 128L == 0L) {
                  this.bufferedWriter2.flush();
               }
            } catch (Throwable var26) {
               this.flag2 = true;
               return null;
            }

            return new RotationRecorderModule.Entry10(var11, var2, var3, var4, var8, var9, var10, 1);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private String method4958(BlockState var1, FluidState var2) {
      return var1 != null && !var1.isAir() || var2 != null && !var2.isEmpty() ? var1 + "|" + var2 : "minecraft:air|empty";
   }

   private String method4959() {
      return "{\"name\":\"minecraft:air\",\"state\":\"minecraft:air\",\"slipperiness\":0.6,\"velocity_multiplier\":1.0,\"jump_velocity_multiplier\":1.0,\"fluid_type\":\"none\",\"fluid_height\":0.0,\"fluid_velocity\":[0.0,0.0,0.0],\"climbable\":false,\"powder_snow\":false,\"collision_boxes\":[]}";
   }

   private String method4960(BlockState var1, FluidState var2, BlockPos var3) {
      String var4 = var1 == null ? "minecraft:air" : String.valueOf(Registries.BLOCK.getId(var1.getBlock()));
      String var5 = var1 == null ? var4 : var1.toString();
      float var6 = var1 == null ? 0.6F : var1.getBlock().getSlipperiness();
      float var7 = var1 == null ? 1.0F : var1.getBlock().getVelocityMultiplier();
      float var8 = var1 == null ? 1.0F : var1.getBlock().getJumpVelocityMultiplier();
      int var9 = var1 != null && var1.isIn(BlockTags.CLIMBABLE) ? 1 : 0;
      boolean var10 = var4.equals("minecraft:powder_snow");
      String var11 = "none";
      float var12 = 0.0F;
      Vec3d var13 = Vec3d.ZERO;
      if (var2 != null && !var2.isEmpty()) {
         if (var2.isIn(FluidTags.WATER)) {
            var11 = "water";
         } else if (var2.isIn(FluidTags.LAVA)) {
            var11 = "lava";
         } else {
            var11 = "other";
         }

         var12 = var2.getHeight(mc.world, var3);
         var13 = var2.getVelocity(mc.world, var3);
      }

      StringBuilder var14 = new StringBuilder(1024);
      var14.append('{');
      this.method4962(var14, "name", var4).append(',');
      this.method4962(var14, "state", var5).append(',');
      var14.append("\"slipperiness\":").append(var6).append(',');
      var14.append("\"velocity_multiplier\":").append(var7).append(',');
      var14.append("\"jump_velocity_multiplier\":").append(var8).append(',');
      this.method4962(var14, "fluid_type", var11).append(',');
      var14.append("\"fluid_height\":").append(var12).append(',');
      var14.append("\"fluid_velocity\":[").append((float)var13.x).append(',').append((float)var13.y).append(',').append((float)var13.z).append("],");
      var14.append("\"climbable\":").append((var9 != 0)).append(',');
      var14.append("\"powder_snow\":").append(var10).append(',');
      var14.append("\"collision_boxes\":");
      this.method4961(var14, var1, var3);
      var14.append('}');
      return var14.toString();
   }

   private void method4961(StringBuilder var1, BlockState var2, BlockPos var3) {
      var1.append('[');
      if (var2 != null) {
         try {
            VoxelShape var4 = var2.getCollisionShape(mc.world, var3);
            var var5 = var4.getBoundingBoxes();

            for (int var6 = 0; var6 < var5.size(); var6++) {
               if (var6 > 0) {
                  var1.append(',');
               }

               Box var7 = (Box)var5.get(var6);
               var1.append('[')
                  .append((float)var7.minX)
                  .append(',')
                  .append((float)var7.minY)
                  .append(',')
                  .append((float)var7.minZ)
                  .append(',')
                  .append((float)var7.maxX)
                  .append(',')
                  .append((float)var7.maxY)
                  .append(',')
                  .append((float)var7.maxZ)
                  .append(']');
            }
         } catch (Throwable var8) {
         }
      }

      var1.append(']');
   }

   private StringBuilder method4962(StringBuilder var1, String var2, String var3) {
      var1.append('"').append(var2).append("\":\"").append(this.method4963(var3)).append('"');
      return var1;
   }

   private String method4963(String var1) {
      if (var1 == null) {
         return "";
      } else {
         StringBuilder var2 = new StringBuilder(var1.length() + 16);

         for (int var3 = 0; var3 < var1.length(); var3++) {
            char var4;
            if ((var4 = var1.charAt(var3)) == '\\' || var4 == '"') {
               var2.append('\\').append(var4);
            } else if (var4 == '\n') {
               var2.append("\\n");
            } else if (var4 == '\r') {
               var2.append("\\r");
            } else if (var4 == '\t') {
               var2.append("\\t");
            } else {
               var2.append(var4);
            }
         }

         return var2.toString();
      }
   }

   private StringBuilder method4964(StringBuilder var1, String var2, int var3, int var4, int var5) {
      var1.append('"').append(var2).append("\":[").append(var3).append(',').append(var4).append(',').append(var5).append(']');
      return var1;
   }

   private void method4965(StringBuilder var1, int[] var2, int var3) {
      var1.append('[');
      if (var3 > 0) {
         int var4 = var2[0];
         int var5 = 1;
         byte var6 = 1;

         for (int var7 = 1; var7 < var3; var7++) {
            int var8;
            if ((var8 = var2[var7]) == var4) {
               var5++;
            } else {
               if (var6 == 0) {
                  var1.append(',');
               }

               var1.append('[').append(var4).append(',').append(var5).append(']');
               var6 = 0;
               var4 = var8;
               var5 = 1;
            }
         }

         if (var6 == 0) {
            var1.append(',');
         }

         var1.append('[').append(var4).append(',').append(var5).append(']');
      }

      var1.append(']');
   }

   private StatusEffectInstance method4966(RegistryEntry<StatusEffect> var1) {
      if (var1 == null) {
         return null;
      } else {
         StatusEffectInstance var10000;
         try {
            StatusEffectInstance var2 = mc.player.getStatusEffect(var1);
            var10000 = var2 != null && var2.getDuration() > 0 ? var2 : null;
         } catch (Throwable var3) {
            return null;
         }

         return var10000;
      }
   }

   private boolean method4967(RegistryEntry<StatusEffect> var1) {
      return (this.method4966(var1) != null);
   }

   private float method4968(RegistryEntry<EntityAttribute> var1, float var2) {
      if (var1 == null) {
         return var2;
      } else {
         float var10000;
         try {
            double var3;
            var10000 = Double.isFinite(var3 = mc.player.getAttributes().getValue(var1)) ? (float)var3 : var2;
         } catch (Throwable var5) {
            return var2;
         }

         return var10000;
      }
   }

   private float method4969() {
      return mc.player.getHungerManager() == null ? 20.0F : mc.player.getHungerManager().getFoodLevel();
   }

   private PlayerInput method4970() {
      return new PlayerInput(
         mc.options.forwardKey.isPressed(),
         mc.options.backKey.isPressed(),
         mc.options.leftKey.isPressed(),
         mc.options.rightKey.isPressed(),
         mc.options.jumpKey.isPressed(),
         mc.options.sneakKey.isPressed(),
         mc.options.sprintKey.isPressed()
      );
   }

   private boolean method4971() {
      return !(!mc.player.hasVehicle()
            && !mc.player.getAbilities().flying
            && !mc.player.isSpectator()
            && !mc.player.isTouchingWater()
            && !mc.player.isSwimming()
            && !mc.player.isSubmergedInWater()
            && !mc.player.isGliding());
   }

   private float method4972(RotationRecorderModule.Entry var1) {
      return var1.data24.getData19().getFloatValue();
   }

   private float method4973(RotationRecorderModule.Entry var1) {
      return var1.data24.getData19().getFloatValue2();
   }

   private RotationRecorderModule.Entry3 method4974(RotationRecorderModule.Entry var1, RotationRecorderModule.Entry var2, float var3, float var4) {
      return this.method4976(var1, var2, var3, var4);
   }

   private RotationRecorderModule.Entry3 method4975(float var1, float var2, PlayerInput var3) {
      Vec3d var4 = mc.player.getEntityPos();
      Vec3d var5 = mc.player.getVelocity();

      try {
         PlayerSimulation var6 = PlayerSimulation.method6033(PlayerSimulation.Entry.method6092(var3));
         var6.floatValue = var1;
         var6.floatValue2 = var2;
         var6.tick();
         Vec3d var7 = var6.vec3d.subtract(var4);
         return this.method4977(var7) && this.method4977(var6.vec3d) && this.method4977(var6.vec3d2)
            ? new RotationRecorderModule.Entry3(true, var7, var6.vec3d, var6.vec3d2, var6.flag4, var6.flag5, var6.flag6)
            : RotationRecorderModule.Entry3.method4998(var4, var5);
      } catch (Throwable var8) {
         return RotationRecorderModule.Entry3.method4998(var4, var5);
      }
   }

   private RotationRecorderModule.Entry3 method4976(RotationRecorderModule.Entry var1, RotationRecorderModule.Entry var2, float var3, float var4) {
      try {
         PlayerSimulation var5 = PlayerSimulation.method6033(new PlayerSimulation.Entry(var2.playerInput));
         var5.vec3d = var1.vec3d;
         var5.vec3d2 = var1.vec3d2;
         var5.box = var1.box;
         var5.floatValue = var3;
         var5.floatValue2 = var4;
         var5.flag = var2.flag3;
         var5.floatValue3 = var1.floatValue;
         var5.intValue = var1.intValue;
         var5.flag2 = var1.flag4;
         var5.flag3 = var1.flag5;
         var5.flag4 = var1.flag;
         var5.flag5 = var1.flag6;
         var5.flag6 = var1.flag7;
         var5.flag7 = var1.flag8;
         var5.flag8 = var1.flag9;
         var5.flag9 = var1.flag10;
         var5.entityPose = var1.entityPose;
         var5.inSneakingPose = var1.flag11;
         var5.flag10 = var1.flag12;
         var5.tick();
         Vec3d var6 = var5.vec3d.subtract(var1.vec3d);
         return this.method4977(var6) && this.method4977(var5.vec3d) && this.method4977(var5.vec3d2)
            ? new RotationRecorderModule.Entry3(true, var6, var5.vec3d, var5.vec3d2, var5.flag4, var5.flag5, var5.flag6)
            : RotationRecorderModule.Entry3.method4998(var1.vec3d, var1.vec3d2);
      } catch (Throwable var7) {
         return RotationRecorderModule.Entry3.method4998(var1.vec3d, var1.vec3d2);
      }
   }

   private boolean method4977(Vec3d var1) {
      return (var1 != null && this.isFinite((float)var1.x) && this.isFinite((float)var1.y) && this.isFinite((float)var1.z));
   }

   private static boolean method4978(float... var0) {
      float[] var1 = var0;
      int var2 = var0.length;

      for (int var3 = 0; var3 < var2; var3++) {
         float var4;
         if (!Float.isFinite(var4 = var1[var3])) {
            return false;
         }
      }

      return true;
   }

   private static final class Data implements RotationRecorderModule.Handler {
      private final boolean flag;
      private final RotationRecorderModule.Data20 data20;
      private final RotationRecorderModule.Data20 data202;
      private final RotationRecorderModule.Data20 data203;
      private final boolean flag2;
      private final boolean flag3;
      private final boolean flag4;

      public boolean flag() {
         return this.flag;
      }

      public RotationRecorderModule.Data20 data20() {
         return this.data20;
      }

      public RotationRecorderModule.Data20 data202() {
         return this.data202;
      }

      public RotationRecorderModule.Data20 data203() {
         return this.data203;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      public boolean flag4() {
         return this.flag4;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.data20, other.data20) && java.util.Objects.equals(this.data202, other.data202) && java.util.Objects.equals(this.data203, other.data203) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.flag3, other.flag3) && java.util.Objects.equals(this.flag4, other.flag4);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, data20, data202, data203, flag2, flag3, flag4);
      }

      @Override
      public String toString() {
         return "Data[" + "flag=" + this.flag + ", " + "data20=" + this.data20 + ", " + "data202=" + this.data202 + ", " + "data203=" + this.data203 + ", " + "flag2=" + this.flag2 + ", " + "flag3=" + this.flag3 + ", " + "flag4=" + this.flag4 + "]";
      }

      private Data(
         boolean var1,
         RotationRecorderModule.Data20 var2,
         RotationRecorderModule.Data20 var3,
         RotationRecorderModule.Data20 var4,
         boolean var5,
         boolean var6,
         boolean var7
      ) {
         this.flag = var1;
         this.data20 = var2;
         this.data202 = var3;
         this.data203 = var4;
         this.flag2 = var5;
         this.flag3 = var6;
         this.flag4 = var7;
      }

      private static void method4979(RotationRecorderModule.Entry8 var0) {
         var0.method5029("base_sim_ok");
         RotationRecorderModule.Data20.method5035(var0, "base_sim_motion");
         RotationRecorderModule.Data20.method5035(var0, "base_sim_pos");
         RotationRecorderModule.Data20.method5035(var0, "base_sim_velocity");
         var0.method5029("base_sim_on_ground", "base_sim_horizontal_collision", "base_sim_vertical_collision");
      }

      private static RotationRecorderModule.Data method4980(RotationRecorderModule.Entry3 var0) {
         return new RotationRecorderModule.Data(
            var0.flag,
            RotationRecorderModule.Data20.method5034(var0.vec3d),
            RotationRecorderModule.Data20.method5034(var0.vec3d2),
            RotationRecorderModule.Data20.method5034(var0.vec3d3),
            var0.flag2,
            var0.flag3,
            var0.flag4
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5022(this.flag);
         this.data20.method4981(var1);
         this.data202.method4981(var1);
         this.data203.method4981(var1);
         var1.method5022(this.flag2, this.flag3, this.flag4);
      }

      @Override
      public boolean method4982() {
         return this.data20.method4982() && this.data202.method4982() && this.data203.method4982();
      }

      public boolean isFlag() {
         return this.flag;
      }

      public RotationRecorderModule.Data20 getData20() {
         return this.data20;
      }

      public RotationRecorderModule.Data20 getData202() {
         return this.data202;
      }

      public RotationRecorderModule.Data20 getData203() {
         return this.data203;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public boolean isFlag4() {
         return this.flag4;
      }
   }

   private static final class Data10 implements RotationRecorderModule.Handler {
      private final RotationRecorderModule.Data14 data14;
      private final RotationRecorderModule.Data8 data8;
      private final RotationRecorderModule.Data18 data18;
      private final RotationRecorderModule.Data15 data15;

      public RotationRecorderModule.Data14 data14() {
         return this.data14;
      }

      public RotationRecorderModule.Data8 data8() {
         return this.data8;
      }

      public RotationRecorderModule.Data18 data18() {
         return this.data18;
      }

      public RotationRecorderModule.Data15 data15() {
         return this.data15;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data10 other)) return false;
         return java.util.Objects.equals(this.data14, other.data14) && java.util.Objects.equals(this.data8, other.data8) && java.util.Objects.equals(this.data18, other.data18) && java.util.Objects.equals(this.data15, other.data15);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(data14, data8, data18, data15);
      }

      @Override
      public String toString() {
         return "Data10[" + "data14=" + this.data14 + ", " + "data8=" + this.data8 + ", " + "data18=" + this.data18 + ", " + "data15=" + this.data15 + "]";
      }

      private Data10(
         RotationRecorderModule.Data14 var1, RotationRecorderModule.Data8 var2, RotationRecorderModule.Data18 var3, RotationRecorderModule.Data15 var4
      ) {
         this.data14 = var1;
         this.data8 = var2;
         this.data18 = var3;
         this.data15 = var4;
      }

      private static void method5013(RotationRecorderModule.Entry8 var0) {
         RotationRecorderModule.Data14.method5019(var0);
         RotationRecorderModule.Data8.method4996(var0);
         RotationRecorderModule.Data18.method5028(var0);
         RotationRecorderModule.Data15.method5023(var0);
      }

      private RotationRecorderModule.Data10 method5014(RotationRecorderModule.Data10 var1, boolean var2, boolean var3) {
         return new RotationRecorderModule.Data10(this.data14.method5020(var3, var1.data14.floatValue3), this.data8.method4997(var2), this.data18, var1.data15);
      }

      private boolean method5015(boolean var1) {
         return !this.data8.flag3
            && !this.data8.flag4
            && !this.data8.flag5
            && !this.data18.flag
            && !this.data18.flag2
            && !this.data18.flag3
            && !this.data18.flag4
            && !this.data18.flag5
            && (!var1 || this.data8.floatValue > 6.0F);
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         this.data14.method4981(var1);
         this.data8.method4981(var1);
         this.data18.method4981(var1);
         this.data15.method4981(var1);
      }

      @Override
      public boolean method4982() {
         return this.data14.method4982() && this.data8.method4982() && this.data18.method4982() && this.data15.method4982();
      }

      public RotationRecorderModule.Data14 getData14() {
         return this.data14;
      }

      public RotationRecorderModule.Data8 getData8() {
         return this.data8;
      }

      public RotationRecorderModule.Data18 getData18() {
         return this.data18;
      }

      public RotationRecorderModule.Data15 getData15() {
         return this.data15;
      }
   }

   private static final class Data11 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data11 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2);
      }

      @Override
      public String toString() {
         return "Data11[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + "]";
      }

      private Data11(float var1, float var2) {
         this.floatValue = var1;
         this.floatValue2 = var2;
      }

      private static void method5016(RotationRecorderModule.Entry8 var0) {
         var0.method5029("target_yaw_mouse_count", "target_pitch_mouse_count");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5021(this.floatValue, this.floatValue2);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }
   }

   private static final class Data12 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data12 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5);
      }

      @Override
      public String toString() {
         return "Data12[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + "]";
      }

      private Data12(float var1, float var2, float var3, float var4, float var5) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
      }

      private static void method5017(RotationRecorderModule.Entry8 var0) {
         var0.method5029(
            "target_look_at_player_yaw_error_sin",
            "target_look_at_player_yaw_error_cos",
            "target_look_at_player_pitch_error_sin",
            "target_look_at_player_pitch_error_cos",
            "target_look_at_player_alignment_cos"
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5021(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }
   }

   private static final class Data13 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final float floatValue9;
      private final float floatValue10;
      private final float floatValue11;
      private final float floatValue12;
      private final float floatValue13;
      private final float floatValue14;
      private final float floatValue15;
      private final float floatValue16;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      public float floatValue8() {
         return this.floatValue8;
      }

      public float floatValue9() {
         return this.floatValue9;
      }

      public float floatValue10() {
         return this.floatValue10;
      }

      public float floatValue11() {
         return this.floatValue11;
      }

      public float floatValue12() {
         return this.floatValue12;
      }

      public float floatValue13() {
         return this.floatValue13;
      }

      public float floatValue14() {
         return this.floatValue14;
      }

      public float floatValue15() {
         return this.floatValue15;
      }

      public float floatValue16() {
         return this.floatValue16;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data13 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.floatValue9, other.floatValue9) && java.util.Objects.equals(this.floatValue10, other.floatValue10) && java.util.Objects.equals(this.floatValue11, other.floatValue11) && java.util.Objects.equals(this.floatValue12, other.floatValue12) && java.util.Objects.equals(this.floatValue13, other.floatValue13) && java.util.Objects.equals(this.floatValue14, other.floatValue14) && java.util.Objects.equals(this.floatValue15, other.floatValue15) && java.util.Objects.equals(this.floatValue16, other.floatValue16);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, floatValue10, floatValue11, floatValue12, floatValue13, floatValue14, floatValue15, floatValue16);
      }

      @Override
      public String toString() {
         return "Data13[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "floatValue9=" + this.floatValue9 + ", " + "floatValue10=" + this.floatValue10 + ", " + "floatValue11=" + this.floatValue11 + ", " + "floatValue12=" + this.floatValue12 + ", " + "floatValue13=" + this.floatValue13 + ", " + "floatValue14=" + this.floatValue14 + ", " + "floatValue15=" + this.floatValue15 + ", " + "floatValue16=" + this.floatValue16 + "]";
      }

      private Data13(
         float var1,
         float var2,
         float var3,
         float var4,
         float var5,
         float var6,
         float var7,
         float var8,
         float var9,
         float var10,
         float var11,
         float var12,
         float var13,
         float var14,
         float var15,
         float var16
      ) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
         this.floatValue6 = var6;
         this.floatValue7 = var7;
         this.floatValue8 = var8;
         this.floatValue9 = var9;
         this.floatValue10 = var10;
         this.floatValue11 = var11;
         this.floatValue12 = var12;
         this.floatValue13 = var13;
         this.floatValue14 = var14;
         this.floatValue15 = var15;
         this.floatValue16 = var16;
      }

      private static void method5018(RotationRecorderModule.Entry8 var0) {
         var0.method5029(
            "yaw_mouse_count",
            "pitch_mouse_count",
            "yaw_acceleration_mouse_counts",
            "pitch_acceleration_mouse_counts",
            "yaw_jerk_mouse_counts",
            "pitch_jerk_mouse_counts",
            "mouse_dx_nonzero_streak_ticks",
            "mouse_dy_nonzero_streak_ticks",
            "yaw_sign_flip_ticks_ago",
            "pitch_sign_flip_ticks_ago",
            "yaw_mouse_static_help",
            "pitch_mouse_static_help",
            "yaw_current_to_peak_abs_ratio",
            "pitch_current_to_peak_abs_ratio",
            "yaw_peak_mouse_count_abs",
            "pitch_peak_mouse_count_abs"
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5021(
            this.floatValue,
            this.floatValue2,
            this.floatValue3,
            this.floatValue4,
            this.floatValue5,
            this.floatValue6,
            this.floatValue7,
            this.floatValue8,
            this.floatValue9,
            this.floatValue10,
            this.floatValue11,
            this.floatValue12,
            this.floatValue13,
            this.floatValue14,
            this.floatValue15,
            this.floatValue16
         );
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(
            this.floatValue,
            this.floatValue2,
            this.floatValue3,
            this.floatValue4,
            this.floatValue5,
            this.floatValue6,
            this.floatValue7,
            this.floatValue8,
            this.floatValue9,
            this.floatValue10,
            this.floatValue11,
            this.floatValue12,
            this.floatValue13,
            this.floatValue14,
            this.floatValue15,
            this.floatValue16
         );
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }

      public float getFloatValue9() {
         return this.floatValue9;
      }

      public float getFloatValue10() {
         return this.floatValue10;
      }

      public float getFloatValue11() {
         return this.floatValue11;
      }

      public float getFloatValue12() {
         return this.floatValue12;
      }

      public float getFloatValue13() {
         return this.floatValue13;
      }

      public float getFloatValue14() {
         return this.floatValue14;
      }

      public float getFloatValue15() {
         return this.floatValue15;
      }

      public float getFloatValue16() {
         return this.floatValue16;
      }
   }

   private static final class Data14 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final boolean flag;
      private final float floatValue2;
      private final float floatValue3;

      public float floatValue() {
         return this.floatValue;
      }

      public boolean flag() {
         return this.flag;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data14 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, flag, floatValue2, floatValue3);
      }

      @Override
      public String toString() {
         return "Data14[" + "floatValue=" + this.floatValue + ", " + "flag=" + this.flag + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + "]";
      }

      private Data14(float var1, boolean var2, float var3, float var4) {
         this.floatValue = var1;
         this.flag = var2;
         this.floatValue2 = var3;
         this.floatValue3 = var4;
      }

      private static void method5019(RotationRecorderModule.Entry8 var0) {
         var0.method5029("recorder_schema_version", "simpy_supported_v0", "simpy_schema_version", "simpy_world_mode");
      }

      private RotationRecorderModule.Data14 method5020(boolean var1, float var2) {
         return new RotationRecorderModule.Data14(this.floatValue, var1, this.floatValue2, var2);
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5021(this.floatValue);
         var1.method5022(this.flag);
         var1.method5021(this.floatValue2, this.floatValue3);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2, this.floatValue3);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }
   }

   private static final class Data15 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      public float floatValue8() {
         return this.floatValue8;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data15 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8);
      }

      @Override
      public String toString() {
         return "Data15[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + "]";
      }

      private Data15(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
         this.floatValue6 = var6;
         this.floatValue7 = var7;
         this.floatValue8 = var8;
      }

      private static void method5023(RotationRecorderModule.Entry8 var0) {
         var0.method5029(
            "snapshot_id",
            "snapshot_origin_x",
            "snapshot_origin_y",
            "snapshot_origin_z",
            "snapshot_size_x",
            "snapshot_size_y",
            "snapshot_size_z",
            "snapshot_version"
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5021(
            this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5, this.floatValue6, this.floatValue7, this.floatValue8
         );
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(
            this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5, this.floatValue6, this.floatValue7, this.floatValue8
         );
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }
   }

   private static final class Data16 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data16 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5);
      }

      @Override
      public String toString() {
         return "Data16[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + "]";
      }

      private Data16(float var1, float var2, float var3, float var4, float var5) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
      }

      private static RotationRecorderModule.Data16 method5024(PlayerInput var0) {
         return new RotationRecorderModule.Data16(
            var0.forward() ? 1.0F : 0.0F, var0.backward() ? 1.0F : 0.0F, var0.left() ? 1.0F : 0.0F, var0.right() ? 1.0F : 0.0F, var0.jump() ? 1.0F : 0.0F
         );
      }

      private static void method5025(RotationRecorderModule.Entry8 var0) {
         var0.method5029("movement_input_forward", "movement_input_backward", "movement_input_left", "movement_input_right", "movement_input_jump");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5021(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float left() {
         return this.floatValue3;
      }

      public float right() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }
   }

   private static final class Data17 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data17 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5);
      }

      @Override
      public String toString() {
         return "Data17[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + "]";
      }

      private Data17(float var1, float var2, float var3, float var4, float var5) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
      }

      private static void method5027(RotationRecorderModule.Entry8 var0) {
         var0.method5029(
            "target_center_distance",
            "target_center_horizontal_distance",
            "target_center_vertical_distance",
            "aim_inside_target_box_ticks_clamped",
            "aim_outside_target_box_ticks_clamped"
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5021(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }
   }

   private static final class Data18 implements RotationRecorderModule.Handler {
      private final boolean flag;
      private final boolean flag2;
      private final boolean flag3;
      private final boolean flag4;
      private final boolean flag5;
      private final float floatValue;
      private final float floatValue2;

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      public boolean flag4() {
         return this.flag4;
      }

      public boolean flag5() {
         return this.flag5;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data18 other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.flag3, other.flag3) && java.util.Objects.equals(this.flag4, other.flag4) && java.util.Objects.equals(this.flag5, other.flag5) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, flag2, flag3, flag4, flag5, floatValue, floatValue2);
      }

      @Override
      public String toString() {
         return "Data18[" + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "flag3=" + this.flag3 + ", " + "flag4=" + this.flag4 + ", " + "flag5=" + this.flag5 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + "]";
      }

      private Data18(boolean var1, boolean var2, boolean var3, boolean var4, boolean var5, float var6, float var7) {
         this.flag = var1;
         this.flag2 = var2;
         this.flag3 = var3;
         this.flag4 = var4;
         this.flag5 = var5;
         this.floatValue = var6;
         this.floatValue2 = var7;
      }

      private static void method5028(RotationRecorderModule.Entry8 var0) {
         var0.method5029(
            "effect_slow_falling",
            "effect_levitation",
            "effect_jump_boost",
            "effect_blindness",
            "effect_dolphins_grace",
            "effect_levitation_amp",
            "effect_jump_boost_amp"
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5022(this.flag, this.flag2, this.flag3, this.flag4, this.flag5);
         var1.method5021(this.floatValue, this.floatValue2);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2);
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public boolean isFlag4() {
         return this.flag4;
      }

      public boolean isFlag5() {
         return this.flag5;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }
   }

   private static final class Data19 implements RotationRecorderModule.Handler {
      private final RotationRecorderModule.Data20 data20;
      private final RotationRecorderModule.Data20 data202;
      private final float floatValue;
      private final float floatValue2;
      private final RotationRecorderModule.Data20 data203;
      private final RotationRecorderModule.Data20 data204;
      private final RotationRecorderModule.Data9 data9;

      public RotationRecorderModule.Data20 data20() {
         return this.data20;
      }

      public RotationRecorderModule.Data20 data202() {
         return this.data202;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public RotationRecorderModule.Data20 data203() {
         return this.data203;
      }

      public RotationRecorderModule.Data20 data204() {
         return this.data204;
      }

      public RotationRecorderModule.Data9 data9() {
         return this.data9;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data19 other)) return false;
         return java.util.Objects.equals(this.data20, other.data20) && java.util.Objects.equals(this.data202, other.data202) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.data203, other.data203) && java.util.Objects.equals(this.data204, other.data204) && java.util.Objects.equals(this.data9, other.data9);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(data20, data202, floatValue, floatValue2, data203, data204, data9);
      }

      @Override
      public String toString() {
         return "Data19[" + "data20=" + this.data20 + ", " + "data202=" + this.data202 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "data203=" + this.data203 + ", " + "data204=" + this.data204 + ", " + "data9=" + this.data9 + "]";
      }

      private Data19(
         RotationRecorderModule.Data20 var1,
         RotationRecorderModule.Data20 var2,
         float var3,
         float var4,
         RotationRecorderModule.Data20 var5,
         RotationRecorderModule.Data20 var6,
         RotationRecorderModule.Data9 var7
      ) {
         this.data20 = var1;
         this.data202 = var2;
         this.floatValue = var3;
         this.floatValue2 = var4;
         this.data203 = var5;
         this.data204 = var6;
         this.data9 = var7;
      }

      private static void method5033(RotationRecorderModule.Entry8 var0) {
         RotationRecorderModule.Data20.method5035(var0, "player_pos");
         RotationRecorderModule.Data20.method5035(var0, "player_eye");
         var0.method5029("player_yaw", "player_pitch");
         RotationRecorderModule.Data20.method5035(var0, "player_velocity");
         RotationRecorderModule.Data20.method5035(var0, "player_prev_pos");
         RotationRecorderModule.Data9.method5012(var0, "player_bbox");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         this.data20.method4981(var1);
         this.data202.method4981(var1);
         var1.method5021(this.floatValue, this.floatValue2);
         this.data203.method4981(var1);
         this.data204.method4981(var1);
         this.data9.method4981(var1);
      }

      @Override
      public boolean method4982() {
         return this.data20.method4982()
            && this.data202.method4982()
            && RotationRecorderModule.method4978(this.floatValue, this.floatValue2)
            && this.data203.method4982()
            && this.data204.method4982()
            && this.data9.method4982();
      }

      public RotationRecorderModule.Data20 getData20() {
         return this.data20;
      }

      public RotationRecorderModule.Data20 getData202() {
         return this.data202;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public RotationRecorderModule.Data20 getData203() {
         return this.data203;
      }

      public RotationRecorderModule.Data20 getData204() {
         return this.data204;
      }

      public RotationRecorderModule.Data9 getData9() {
         return this.data9;
      }
   }

   private static final class Data2 implements RotationRecorderModule.Handler {
      private final RotationRecorderModule.Data6 data6;
      private final RotationRecorderModule.Data17 data17;
      private final RotationRecorderModule.Data7 data7;
      private final RotationRecorderModule.Data12 data12;
      private final RotationRecorderModule.Data13 data13;
      private final RotationRecorderModule.Data16 data16;
      private final RotationRecorderModule.Data5 data5;
      private final RotationRecorderModule.Data22 data22;

      public RotationRecorderModule.Data6 data6() {
         return this.data6;
      }

      public RotationRecorderModule.Data17 data17() {
         return this.data17;
      }

      public RotationRecorderModule.Data7 data7() {
         return this.data7;
      }

      public RotationRecorderModule.Data12 data12() {
         return this.data12;
      }

      public RotationRecorderModule.Data13 data13() {
         return this.data13;
      }

      public RotationRecorderModule.Data16 data16() {
         return this.data16;
      }

      public RotationRecorderModule.Data5 data5() {
         return this.data5;
      }

      public RotationRecorderModule.Data22 data22() {
         return this.data22;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.data6, other.data6) && java.util.Objects.equals(this.data17, other.data17) && java.util.Objects.equals(this.data7, other.data7) && java.util.Objects.equals(this.data12, other.data12) && java.util.Objects.equals(this.data13, other.data13) && java.util.Objects.equals(this.data16, other.data16) && java.util.Objects.equals(this.data5, other.data5) && java.util.Objects.equals(this.data22, other.data22);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(data6, data17, data7, data12, data13, data16, data5, data22);
      }

      @Override
      public String toString() {
         return "Data2[" + "data6=" + this.data6 + ", " + "data17=" + this.data17 + ", " + "data7=" + this.data7 + ", " + "data12=" + this.data12 + ", " + "data13=" + this.data13 + ", " + "data16=" + this.data16 + ", " + "data5=" + this.data5 + ", " + "data22=" + this.data22 + "]";
      }

      private Data2(
         RotationRecorderModule.Data6 var1,
         RotationRecorderModule.Data17 var2,
         RotationRecorderModule.Data7 var3,
         RotationRecorderModule.Data12 var4,
         RotationRecorderModule.Data13 var5,
         RotationRecorderModule.Data16 var6,
         RotationRecorderModule.Data5 var7,
         RotationRecorderModule.Data22 var8
      ) {
         this.data6 = var1;
         this.data17 = var2;
         this.data7 = var3;
         this.data12 = var4;
         this.data13 = var5;
         this.data16 = var6;
         this.data5 = var7;
         this.data22 = var8;
      }

      private static void method4983(RotationRecorderModule.Entry8 var0) {
         RotationRecorderModule.Data6.method4994(var0);
         RotationRecorderModule.Data17.method5027(var0);
         RotationRecorderModule.Data7.method4995(var0);
         RotationRecorderModule.Data12.method5017(var0);
         RotationRecorderModule.Data13.method5018(var0);
         RotationRecorderModule.Data16.method5025(var0);
         RotationRecorderModule.Data5.method4993(var0, "player_motion_predicted_1t");
         RotationRecorderModule.Data22.method5038(var0);
      }

      private RotationRecorderModule.Data2 method4984(RotationRecorderModule.Entry var1) {
         return new RotationRecorderModule.Data2(
            this.data6, this.data17, this.data7, this.data12, this.data13, RotationRecorderModule.Data16.method5024(var1.playerInput), this.data5, this.data22
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         this.data6.method4981(var1);
         this.data17.method4981(var1);
         this.data7.method4981(var1);
         this.data12.method4981(var1);
         this.data13.method4981(var1);
         this.data16.method4981(var1);
         this.data5.method4981(var1);
         this.data22.method4981(var1);
      }

      @Override
      public boolean method4982() {
         return this.data6.method4982()
            && this.data17.method4982()
            && this.data7.method4982()
            && this.data12.method4982()
            && this.data13.method4982()
            && this.data16.method4982()
            && this.data5.method4982()
            && this.data22.method4982();
      }

      private float[] method4985(RotationRecorderModule.Data3 var1) {
         return new float[]{
            this.data6.floatValue,
            this.data6.floatValue2,
            this.data6.floatValue3,
            this.data6.floatValue4,
            this.data6.floatValue5,
            this.data6.floatValue6,
            this.data6.floatValue7,
            this.data6.floatValue8,
            this.data6.floatValue9,
            this.data6.floatValue10,
            this.data6.floatValue11,
            this.data6.flag ? 1.0F : 0.0F,
            this.data17.floatValue,
            this.data17.floatValue2,
            this.data17.floatValue3,
            this.data17.floatValue4,
            this.data17.floatValue5,
            this.data7.floatValue,
            this.data7.floatValue2,
            this.data7.floatValue3,
            this.data7.floatValue4,
            this.data7.floatValue5,
            var1.floatValue,
            var1.floatValue2,
            this.data7.data5.floatValue,
            this.data7.data5.floatValue2,
            this.data7.data5.floatValue3,
            this.data13.floatValue,
            this.data13.floatValue2,
            this.data13.floatValue3,
            this.data13.floatValue4,
            this.data13.floatValue5,
            this.data13.floatValue6,
            this.data13.floatValue7,
            this.data13.floatValue8,
            this.data13.floatValue9,
            this.data13.floatValue10,
            this.data13.floatValue11,
            this.data13.floatValue12,
            this.data13.floatValue13,
            this.data13.floatValue14,
            this.data13.floatValue15,
            this.data13.floatValue16,
            this.data16.floatValue,
            this.data16.floatValue2,
            this.data16.floatValue3,
            this.data16.floatValue4,
            this.data5.floatValue,
            this.data5.floatValue2,
            this.data5.floatValue3,
            this.data22.floatValue,
            this.data22.floatValue2,
            this.data22.flag ? 1.0F : 0.0F,
            this.data22.flag2 ? 1.0F : 0.0F
         };
      }

      private float[] method4986(RotationRecorderModule.Data3 var1) {
         float[] var2 = this.method4985(var1);
         float[] var3 = new float[var2.length + 1];
         System.arraycopy(var2, 0, var3, 0, 47);
         var3[47] = this.data16.floatValue5;
         System.arraycopy(var2, 47, var3, 48, var2.length - 47);
         return var3;
      }

      public RotationRecorderModule.Data6 getData6() {
         return this.data6;
      }

      public RotationRecorderModule.Data17 getData17() {
         return this.data17;
      }

      public RotationRecorderModule.Data7 getData7() {
         return this.data7;
      }

      public RotationRecorderModule.Data12 getData12() {
         return this.data12;
      }

      public RotationRecorderModule.Data13 getData13() {
         return this.data13;
      }

      public RotationRecorderModule.Data16 getData16() {
         return this.data16;
      }

      public RotationRecorderModule.Data5 getData5() {
         return this.data5;
      }

      public RotationRecorderModule.Data22 getData22() {
         return this.data22;
      }
   }

   private static final class Data20 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data20 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3);
      }

      @Override
      public String toString() {
         return "Data20[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + "]";
      }

      private Data20(float var1, float var2, float var3) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
      }

      private static RotationRecorderModule.Data20 method5034(Vec3d var0) {
         return new RotationRecorderModule.Data20((float)var0.x, (float)var0.y, (float)var0.z);
      }

      private static void method5035(RotationRecorderModule.Entry8 var0, String var1) {
         var0.method5029(var1 + "_x", var1 + "_y", var1 + "_z");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5021(this.floatValue, this.floatValue2, this.floatValue3);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2, this.floatValue3);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }
   }

   private static final class Data21 implements RotationRecorderModule.Handler {
      private final boolean flag;
      private final boolean flag2;
      private final boolean flag3;
      private final boolean flag4;
      private final boolean flag5;
      private final boolean flag6;
      private final boolean flag7;

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      public boolean flag4() {
         return this.flag4;
      }

      public boolean flag5() {
         return this.flag5;
      }

      public boolean flag6() {
         return this.flag6;
      }

      public boolean flag7() {
         return this.flag7;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data21 other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.flag3, other.flag3) && java.util.Objects.equals(this.flag4, other.flag4) && java.util.Objects.equals(this.flag5, other.flag5) && java.util.Objects.equals(this.flag6, other.flag6) && java.util.Objects.equals(this.flag7, other.flag7);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, flag2, flag3, flag4, flag5, flag6, flag7);
      }

      @Override
      public String toString() {
         return "Data21[" + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "flag3=" + this.flag3 + ", " + "flag4=" + this.flag4 + ", " + "flag5=" + this.flag5 + ", " + "flag6=" + this.flag6 + ", " + "flag7=" + this.flag7 + "]";
      }

      private Data21(boolean var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6, boolean var7) {
         this.flag = var1;
         this.flag2 = var2;
         this.flag3 = var3;
         this.flag4 = var4;
         this.flag5 = var5;
         this.flag6 = var6;
         this.flag7 = var7;
      }

      private static RotationRecorderModule.Data21 method5036(PlayerInput var0) {
         return new RotationRecorderModule.Data21(var0.forward(), var0.backward(), var0.left(), var0.right(), var0.jump(), var0.sneak(), var0.sprint());
      }

      private static void method5037(RotationRecorderModule.Entry8 var0) {
         var0.method5029(
            "input_forward_raw", "input_backward_raw", "input_left_raw", "input_right_raw", "input_jump_raw", "input_sneak_raw", "input_sprint_raw"
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5022(this.flag, this.flag2, this.flag3, this.flag4, this.flag5, this.flag6, this.flag7);
      }

      @Override
      public boolean method4982() {
         return true;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public boolean isFlag4() {
         return this.flag4;
      }

      public boolean isFlag5() {
         return this.flag5;
      }

      public boolean isFlag6() {
         return this.flag6;
      }

      public boolean isFlag7() {
         return this.flag7;
      }
   }

   private static final class Data22 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final boolean flag;
      private final boolean flag2;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data22 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, flag, flag2);
      }

      @Override
      public String toString() {
         return "Data22[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + "]";
      }

      private Data22(float var1, float var2, boolean var3, boolean var4) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.flag = var3;
         this.flag2 = var4;
      }

      private static void method5038(RotationRecorderModule.Entry8 var0) {
         var0.method5029("attack_cooldown_progress", "ticks_since_last_attack_clamped", "state_on_ground", "did_attack_previous_tick");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5021(this.floatValue, this.floatValue2);
         var1.method5022(this.flag, this.flag2);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }
   }

   private static final class Data23 implements RotationRecorderModule.Handler {
      private final boolean flag;

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data23 other)) return false;
         return java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag);
      }

      @Override
      public String toString() {
         return "Data23[" + "flag=" + this.flag + "]";
      }

      private Data23(boolean var1) {
         this.flag = var1;
      }

      private static void method5039(RotationRecorderModule.Entry8 var0) {
         var0.method5029("dirty_physics_flag");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5022(this.flag);
      }

      @Override
      public boolean method4982() {
         return true;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   private static final class Data24 implements RotationRecorderModule.Handler {
      private final RotationRecorderModule.Data19 data19;
      private final RotationRecorderModule.Data21 data21;
      private final RotationRecorderModule.Data4 data4;
      private final RotationRecorderModule.Data3 data3;
      private final RotationRecorderModule.Data data;

      public RotationRecorderModule.Data19 data19() {
         return this.data19;
      }

      public RotationRecorderModule.Data21 data21() {
         return this.data21;
      }

      public RotationRecorderModule.Data4 data4() {
         return this.data4;
      }

      public RotationRecorderModule.Data3 data3() {
         return this.data3;
      }

      public RotationRecorderModule.Data data() {
         return this.data;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data24 other)) return false;
         return java.util.Objects.equals(this.data19, other.data19) && java.util.Objects.equals(this.data21, other.data21) && java.util.Objects.equals(this.data4, other.data4) && java.util.Objects.equals(this.data3, other.data3) && java.util.Objects.equals(this.data, other.data);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(data19, data21, data4, data3, data);
      }

      @Override
      public String toString() {
         return "Data24[" + "data19=" + this.data19 + ", " + "data21=" + this.data21 + ", " + "data4=" + this.data4 + ", " + "data3=" + this.data3 + ", " + "data=" + this.data + "]";
      }

      private Data24(
         RotationRecorderModule.Data19 var1,
         RotationRecorderModule.Data21 var2,
         RotationRecorderModule.Data4 var3,
         RotationRecorderModule.Data3 var4,
         RotationRecorderModule.Data var5
      ) {
         this.data19 = var1;
         this.data21 = var2;
         this.data4 = var3;
         this.data3 = var4;
         this.data = var5;
      }

      private static void method5040(RotationRecorderModule.Entry8 var0) {
         RotationRecorderModule.Data19.method5033(var0);
         RotationRecorderModule.Data21.method5037(var0);
         RotationRecorderModule.Data4.method4992(var0);
         RotationRecorderModule.Data3.method4987(var0);
         RotationRecorderModule.Data.method4979(var0);
      }

      private RotationRecorderModule.Data24 method5041(PlayerInput var1, RotationRecorderModule.Entry3 var2) {
         return new RotationRecorderModule.Data24(
            this.data19,
            RotationRecorderModule.Data21.method5036(var1),
            this.data4.method4991(var1.jump()),
            this.data3,
            RotationRecorderModule.Data.method4980(var2)
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         this.data19.method4981(var1);
         this.data21.method4981(var1);
         this.data4.method4981(var1);
         this.data3.method4981(var1);
         this.data.method4981(var1);
      }

      @Override
      public boolean method4982() {
         return this.data19.method4982() && this.data4.method4982() && this.data3.method4982() && this.data.method4982();
      }

      public RotationRecorderModule.Data19 getData19() {
         return this.data19;
      }

      public RotationRecorderModule.Data21 getData21() {
         return this.data21;
      }

      public RotationRecorderModule.Data4 getData4() {
         return this.data4;
      }

      public RotationRecorderModule.Data3 getData3() {
         return this.data3;
      }

      public RotationRecorderModule.Data getData() {
         return this.data;
      }
   }

   private static final class Data3 implements RotationRecorderModule.Handler {
      private final RotationRecorderModule.Data9 data9;
      private final RotationRecorderModule.Data20 data20;
      private final RotationRecorderModule.Data20 data202;
      private final RotationRecorderModule.Data20 data203;
      private final float floatValue;
      private final float floatValue2;

      public RotationRecorderModule.Data9 data9() {
         return this.data9;
      }

      public RotationRecorderModule.Data20 data20() {
         return this.data20;
      }

      public RotationRecorderModule.Data20 data202() {
         return this.data202;
      }

      public RotationRecorderModule.Data20 data203() {
         return this.data203;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data3 other)) return false;
         return java.util.Objects.equals(this.data9, other.data9) && java.util.Objects.equals(this.data20, other.data20) && java.util.Objects.equals(this.data202, other.data202) && java.util.Objects.equals(this.data203, other.data203) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(data9, data20, data202, data203, floatValue, floatValue2);
      }

      @Override
      public String toString() {
         return "Data3[" + "data9=" + this.data9 + ", " + "data20=" + this.data20 + ", " + "data202=" + this.data202 + ", " + "data203=" + this.data203 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + "]";
      }

      private Data3(
         RotationRecorderModule.Data9 var1,
         RotationRecorderModule.Data20 var2,
         RotationRecorderModule.Data20 var3,
         RotationRecorderModule.Data20 var4,
         float var5,
         float var6
      ) {
         this.data9 = var1;
         this.data20 = var2;
         this.data202 = var3;
         this.data203 = var4;
         this.floatValue = var5;
         this.floatValue2 = var6;
      }

      private static void method4987(RotationRecorderModule.Entry8 var0) {
         RotationRecorderModule.Data9.method5012(var0, "target_box");
         RotationRecorderModule.Data20.method5035(var0, "target_center");
         RotationRecorderModule.Data20.method5035(var0, "target_tracked_pos");
         RotationRecorderModule.Data20.method5035(var0, "target_visual_motion_raw");
         var0.method5029("target_center_delta_yaw_count_units", "target_center_delta_pitch_count_units");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         this.data9.method4981(var1);
         this.data20.method4981(var1);
         this.data202.method4981(var1);
         this.data203.method4981(var1);
         var1.method5021(this.floatValue, this.floatValue2);
      }

      @Override
      public boolean method4982() {
         return this.data9.method4982()
            && this.data20.method4982()
            && this.data202.method4982()
            && this.data203.method4982()
            && RotationRecorderModule.method4978(this.floatValue, this.floatValue2);
      }

      public RotationRecorderModule.Data9 getData9() {
         return this.data9;
      }

      public RotationRecorderModule.Data20 getData20() {
         return this.data20;
      }

      public RotationRecorderModule.Data20 getData202() {
         return this.data202;
      }

      public RotationRecorderModule.Data20 getData203() {
         return this.data203;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }
   }

   private static final class Data4 implements RotationRecorderModule.Handler {
      private final boolean flag;
      private final boolean flag2;
      private final boolean flag3;
      private final boolean flag4;
      private final boolean flag5;
      private final boolean flag6;
      private final boolean flag7;
      private final boolean flag8;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final boolean flag9;
      private final boolean flag10;
      private final boolean flag11;
      private final boolean flag12;

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      public boolean flag4() {
         return this.flag4;
      }

      public boolean flag5() {
         return this.flag5;
      }

      public boolean flag6() {
         return this.flag6;
      }

      public boolean flag7() {
         return this.flag7;
      }

      public boolean flag8() {
         return this.flag8;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public boolean flag9() {
         return this.flag9;
      }

      public boolean flag10() {
         return this.flag10;
      }

      public boolean flag11() {
         return this.flag11;
      }

      public boolean flag12() {
         return this.flag12;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data4 other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.flag3, other.flag3) && java.util.Objects.equals(this.flag4, other.flag4) && java.util.Objects.equals(this.flag5, other.flag5) && java.util.Objects.equals(this.flag6, other.flag6) && java.util.Objects.equals(this.flag7, other.flag7) && java.util.Objects.equals(this.flag8, other.flag8) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.flag9, other.flag9) && java.util.Objects.equals(this.flag10, other.flag10) && java.util.Objects.equals(this.flag11, other.flag11) && java.util.Objects.equals(this.flag12, other.flag12);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, flag2, flag3, flag4, flag5, flag6, flag7, flag8, floatValue, floatValue2, floatValue3, flag9, flag10, flag11, flag12);
      }

      @Override
      public String toString() {
         return "Data4[" + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "flag3=" + this.flag3 + ", " + "flag4=" + this.flag4 + ", " + "flag5=" + this.flag5 + ", " + "flag6=" + this.flag6 + ", " + "flag7=" + this.flag7 + ", " + "flag8=" + this.flag8 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "flag9=" + this.flag9 + ", " + "flag10=" + this.flag10 + ", " + "flag11=" + this.flag11 + ", " + "flag12=" + this.flag12 + "]";
      }

      private Data4(
         boolean var1,
         boolean var2,
         boolean var3,
         boolean var4,
         boolean var5,
         boolean var6,
         boolean var7,
         boolean var8,
         float var9,
         float var10,
         float var11,
         boolean var12,
         boolean var13,
         boolean var14,
         boolean var15
      ) {
         this.flag = var1;
         this.flag2 = var2;
         this.flag3 = var3;
         this.flag4 = var4;
         this.flag5 = var5;
         this.flag6 = var6;
         this.flag7 = var7;
         this.flag8 = var8;
         this.floatValue = var9;
         this.floatValue2 = var10;
         this.floatValue3 = var11;
         this.flag9 = var12;
         this.flag10 = var13;
         this.flag11 = var14;
         this.flag12 = var15;
      }

      private RotationRecorderModule.Data4 method4991(boolean var1) {
         return new RotationRecorderModule.Data4(
            this.flag,
            this.flag2,
            this.flag3,
            this.flag4,
            this.flag5,
            this.flag6,
            this.flag7,
            this.flag8,
            this.floatValue,
            this.floatValue2,
            this.floatValue3,
            var1,
            this.flag10,
            this.flag11,
            this.flag12
         );
      }

      private static void method4992(RotationRecorderModule.Entry8 var0) {
         var0.method5029(
            "state_horizontal_collision",
            "state_vertical_collision",
            "state_touching_water",
            "state_swimming",
            "state_submerged_water",
            "state_fall_flying",
            "state_in_sneaking_pose",
            "state_crawling",
            "state_pose_id",
            "state_fall_distance",
            "state_jumping_cooldown",
            "state_is_jumping",
            "state_has_vehicle",
            "state_flying",
            "state_spectator"
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5022(this.flag, this.flag2, this.flag3, this.flag4, this.flag5, this.flag6, this.flag7, this.flag8);
         var1.method5021(this.floatValue, this.floatValue2, this.floatValue3);
         var1.method5022(this.flag9, this.flag10, this.flag11, this.flag12);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2, this.floatValue3);
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public boolean isFlag4() {
         return this.flag4;
      }

      public boolean isFlag5() {
         return this.flag5;
      }

      public boolean isFlag6() {
         return this.flag6;
      }

      public boolean isFlag7() {
         return this.flag7;
      }

      public boolean isFlag8() {
         return this.flag8;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public boolean isFlag9() {
         return this.flag9;
      }

      public boolean isFlag10() {
         return this.flag10;
      }

      public boolean isFlag11() {
         return this.flag11;
      }

      public boolean isFlag12() {
         return this.flag12;
      }
   }

   private static final class Data5 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data5 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3);
      }

      @Override
      public String toString() {
         return "Data5[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + "]";
      }

      private Data5(float var1, float var2, float var3) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
      }

      private static void method4993(RotationRecorderModule.Entry8 var0, String var1) {
         var0.method5029(var1 + "_yaw_change_count_units", var1 + "_pitch_change_count_units", var1 + "_distance_change");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5021(this.floatValue, this.floatValue2, this.floatValue3);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2, this.floatValue3);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }
   }

   private static final class Data6 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final float floatValue9;
      private final float floatValue10;
      private final float floatValue11;
      private final boolean flag;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      public float floatValue8() {
         return this.floatValue8;
      }

      public float floatValue9() {
         return this.floatValue9;
      }

      public float floatValue10() {
         return this.floatValue10;
      }

      public float floatValue11() {
         return this.floatValue11;
      }

      public boolean flag() {
         return this.flag;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data6 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.floatValue9, other.floatValue9) && java.util.Objects.equals(this.floatValue10, other.floatValue10) && java.util.Objects.equals(this.floatValue11, other.floatValue11) && java.util.Objects.equals(this.flag, other.flag);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, floatValue9, floatValue10, floatValue11, flag);
      }

      @Override
      public String toString() {
         return "Data6[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "floatValue9=" + this.floatValue9 + ", " + "floatValue10=" + this.floatValue10 + ", " + "floatValue11=" + this.floatValue11 + ", " + "flag=" + this.flag + "]";
      }

      private Data6(
         float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, boolean var12
      ) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
         this.floatValue6 = var6;
         this.floatValue7 = var7;
         this.floatValue8 = var8;
         this.floatValue9 = var9;
         this.floatValue10 = var10;
         this.floatValue11 = var11;
         this.flag = var12;
      }

      private static void method4994(RotationRecorderModule.Entry8 var0) {
         var0.method5029(
            "target_box_min_yaw_from_center_count_units",
            "target_box_max_yaw_from_center_count_units",
            "target_box_min_pitch_from_center_count_units",
            "target_box_max_pitch_from_center_count_units",
            "target_center_pitch_by_box_height",
            "target_center_angular_distance_count_units",
            "target_direction_right",
            "target_direction_up",
            "target_direction_forward",
            "aim_x_norm_in_target_box",
            "aim_y_norm_in_target_box",
            "aim_inside_target_box"
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5021(
            this.floatValue,
            this.floatValue2,
            this.floatValue3,
            this.floatValue4,
            this.floatValue5,
            this.floatValue6,
            this.floatValue7,
            this.floatValue8,
            this.floatValue9,
            this.floatValue10,
            this.floatValue11
         );
         var1.method5022(this.flag);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(
            this.floatValue,
            this.floatValue2,
            this.floatValue3,
            this.floatValue4,
            this.floatValue5,
            this.floatValue6,
            this.floatValue7,
            this.floatValue8,
            this.floatValue9,
            this.floatValue10,
            this.floatValue11
         );
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }

      public float getFloatValue9() {
         return this.floatValue9;
      }

      public float getFloatValue10() {
         return this.floatValue10;
      }

      public float getFloatValue11() {
         return this.floatValue11;
      }

      public boolean isFlag() {
         return this.flag;
      }
   }

   private static final class Data7 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;
      private final float floatValue8;
      private final RotationRecorderModule.Data5 data5;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      public float floatValue8() {
         return this.floatValue8;
      }

      public RotationRecorderModule.Data5 data5() {
         return this.data5;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data7 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7) && java.util.Objects.equals(this.floatValue8, other.floatValue8) && java.util.Objects.equals(this.data5, other.data5);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7, floatValue8, data5);
      }

      @Override
      public String toString() {
         return "Data7[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + ", " + "floatValue8=" + this.floatValue8 + ", " + "data5=" + this.data5 + "]";
      }

      private Data7(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, RotationRecorderModule.Data5 var9) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
         this.floatValue6 = var6;
         this.floatValue7 = var7;
         this.floatValue8 = var8;
         this.data5 = var9;
      }

      private static void method4995(RotationRecorderModule.Entry8 var0) {
         var0.method5029(
            "target_center_delta_yaw_change_count_units",
            "target_center_delta_pitch_change_count_units",
            "target_center_distance_change",
            "yaw_target_drift_count_units",
            "pitch_target_drift_count_units",
            "player_sim_1t_tracked_target_center_delta_yaw_sin",
            "player_sim_1t_tracked_target_center_delta_yaw_cos",
            "player_sim_1t_tracked_target_center_delta_pitch_count_units"
         );
         RotationRecorderModule.Data5.method4993(var0, "target_motion_predicted_1t");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5021(
            this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5, this.floatValue6, this.floatValue7, this.floatValue8
         );
         this.data5.method4981(var1);
      }

      @Override
      public boolean method4982() {
         return this.data5.method4982()
            && RotationRecorderModule.method4978(
               this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5, this.floatValue6, this.floatValue7, this.floatValue8
            );
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }

      public float getFloatValue8() {
         return this.floatValue8;
      }

      public RotationRecorderModule.Data5 getData5() {
         return this.data5;
      }
   }

   private static final class Data8 implements RotationRecorderModule.Handler {
      private final boolean flag;
      private final boolean flag2;
      private final boolean flag3;
      private final boolean flag4;
      private final boolean flag5;
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;
      private final float floatValue7;

      public boolean flag() {
         return this.flag;
      }

      public boolean flag2() {
         return this.flag2;
      }

      public boolean flag3() {
         return this.flag3;
      }

      public boolean flag4() {
         return this.flag4;
      }

      public boolean flag5() {
         return this.flag5;
      }

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      public float floatValue7() {
         return this.floatValue7;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data8 other)) return false;
         return java.util.Objects.equals(this.flag, other.flag) && java.util.Objects.equals(this.flag2, other.flag2) && java.util.Objects.equals(this.flag3, other.flag3) && java.util.Objects.equals(this.flag4, other.flag4) && java.util.Objects.equals(this.flag5, other.flag5) && java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6) && java.util.Objects.equals(this.floatValue7, other.floatValue7);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(flag, flag2, flag3, flag4, flag5, floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6, floatValue7);
      }

      @Override
      public String toString() {
         return "Data8[" + "flag=" + this.flag + ", " + "flag2=" + this.flag2 + ", " + "flag3=" + this.flag3 + ", " + "flag4=" + this.flag4 + ", " + "flag5=" + this.flag5 + ", " + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + ", " + "floatValue7=" + this.floatValue7 + "]";
      }

      private Data8(
         boolean var1,
         boolean var2,
         boolean var3,
         boolean var4,
         boolean var5,
         float var6,
         float var7,
         float var8,
         float var9,
         float var10,
         float var11,
         float var12
      ) {
         this.flag = var1;
         this.flag2 = var2;
         this.flag3 = var3;
         this.flag4 = var4;
         this.flag5 = var5;
         this.floatValue = var6;
         this.floatValue2 = var7;
         this.floatValue3 = var8;
         this.floatValue4 = var9;
         this.floatValue5 = var10;
         this.floatValue6 = var11;
         this.floatValue7 = var12;
      }

      private static void method4996(RotationRecorderModule.Entry8 var0) {
         var0.method5029(
            "state_sprinting",
            "state_allow_flying",
            "state_no_gravity",
            "state_no_drag",
            "state_using_item",
            "state_hunger_food_level",
            "state_step_height",
            "attr_movement_speed",
            "attr_gravity",
            "attr_jump_strength",
            "attr_sneaking_speed",
            "attr_water_movement_efficiency"
         );
      }

      private RotationRecorderModule.Data8 method4997(boolean var1) {
         return new RotationRecorderModule.Data8(
            var1,
            this.flag2,
            this.flag3,
            this.flag4,
            this.flag5,
            this.floatValue,
            this.floatValue2,
            this.floatValue3,
            this.floatValue4,
            this.floatValue5,
            this.floatValue6,
            this.floatValue7
         );
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5022(this.flag, this.flag2, this.flag3, this.flag4, this.flag5);
         var1.method5021(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5, this.floatValue6, this.floatValue7);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(
            this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5, this.floatValue6, this.floatValue7
         );
      }

      public boolean isFlag() {
         return this.flag;
      }

      public boolean isFlag2() {
         return this.flag2;
      }

      public boolean isFlag3() {
         return this.flag3;
      }

      public boolean isFlag4() {
         return this.flag4;
      }

      public boolean isFlag5() {
         return this.flag5;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }

      public float getFloatValue7() {
         return this.floatValue7;
      }
   }

   private static final class Data9 implements RotationRecorderModule.Handler {
      private final float floatValue;
      private final float floatValue2;
      private final float floatValue3;
      private final float floatValue4;
      private final float floatValue5;
      private final float floatValue6;

      public float floatValue() {
         return this.floatValue;
      }

      public float floatValue2() {
         return this.floatValue2;
      }

      public float floatValue3() {
         return this.floatValue3;
      }

      public float floatValue4() {
         return this.floatValue4;
      }

      public float floatValue5() {
         return this.floatValue5;
      }

      public float floatValue6() {
         return this.floatValue6;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data9 other)) return false;
         return java.util.Objects.equals(this.floatValue, other.floatValue) && java.util.Objects.equals(this.floatValue2, other.floatValue2) && java.util.Objects.equals(this.floatValue3, other.floatValue3) && java.util.Objects.equals(this.floatValue4, other.floatValue4) && java.util.Objects.equals(this.floatValue5, other.floatValue5) && java.util.Objects.equals(this.floatValue6, other.floatValue6);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(floatValue, floatValue2, floatValue3, floatValue4, floatValue5, floatValue6);
      }

      @Override
      public String toString() {
         return "Data9[" + "floatValue=" + this.floatValue + ", " + "floatValue2=" + this.floatValue2 + ", " + "floatValue3=" + this.floatValue3 + ", " + "floatValue4=" + this.floatValue4 + ", " + "floatValue5=" + this.floatValue5 + ", " + "floatValue6=" + this.floatValue6 + "]";
      }

      private Data9(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.floatValue = var1;
         this.floatValue2 = var2;
         this.floatValue3 = var3;
         this.floatValue4 = var4;
         this.floatValue5 = var5;
         this.floatValue6 = var6;
      }

      private static RotationRecorderModule.Data9 method5011(Box var0) {
         return new RotationRecorderModule.Data9((float)var0.minX, (float)var0.minY, (float)var0.minZ, (float)var0.maxX, (float)var0.maxY, (float)var0.maxZ);
      }

      private static void method5012(RotationRecorderModule.Entry8 var0, String var1) {
         var0.method5029(var1 + "_min_x", var1 + "_min_y", var1 + "_min_z", var1 + "_max_x", var1 + "_max_y", var1 + "_max_z");
      }

      @Override
      public void method4981(RotationRecorderModule.Entry6 var1) {
         var1.method5021(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5, this.floatValue6);
      }

      @Override
      public boolean method4982() {
         return RotationRecorderModule.method4978(this.floatValue, this.floatValue2, this.floatValue3, this.floatValue4, this.floatValue5, this.floatValue6);
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public float getFloatValue2() {
         return this.floatValue2;
      }

      public float getFloatValue3() {
         return this.floatValue3;
      }

      public float getFloatValue4() {
         return this.floatValue4;
      }

      public float getFloatValue5() {
         return this.floatValue5;
      }

      public float getFloatValue6() {
         return this.floatValue6;
      }
   }

   private static final class Entry {
      private final RotationRecorderModule.Data2 data2;
      private final RotationRecorderModule.Data24 data24;
      private final RotationRecorderModule.Data10 data10;
      private final Vec3d vec3d;
      private final Vec3d vec3d2;
      private final Box box;
      private final PlayerInput playerInput;
      private final boolean flag;
      private final boolean flag2;
      private final boolean flag3;
      private final float floatValue;
      private final int intValue;
      private final boolean flag4;
      private final boolean flag5;
      private final boolean flag6;
      private final boolean flag7;
      private final boolean flag8;
      private final boolean flag9;
      private final boolean flag10;
      private final EntityPose entityPose;
      private final boolean flag11;
      private final boolean flag12;
      private final boolean flag13;
      private final float floatValue2;
      private final long longValue;
      private final int intValue2;

      private Entry(
         RotationRecorderModule.Data2 var1,
         RotationRecorderModule.Data24 var2,
         RotationRecorderModule.Data10 var3,
         Vec3d var4,
         Vec3d var5,
         Box var6,
         PlayerInput var7,
         boolean var8,
         boolean var9,
         boolean var10,
         float var11,
         int var12,
         boolean var13,
         boolean var14,
         boolean var15,
         boolean var16,
         boolean var17,
         boolean var18,
         boolean var19,
         EntityPose var20,
         boolean var21,
         boolean var22,
         boolean var23,
         float var24,
         long var25,
         int var27
      ) {
         this.data2 = var1;
         this.data24 = var2;
         this.data10 = var3;
         this.vec3d = var4;
         this.vec3d2 = var5;
         this.box = var6;
         this.playerInput = var7;
         this.flag = var8;
         this.flag2 = var9;
         this.flag3 = var10;
         this.floatValue = var11;
         this.intValue = var12;
         this.flag4 = var13;
         this.flag5 = var14;
         this.flag6 = var15;
         this.flag7 = var16;
         this.flag8 = var17;
         this.flag9 = var18;
         this.flag10 = var19;
         this.entityPose = var20;
         this.flag11 = var21;
         this.flag12 = var22;
         this.flag13 = var23;
         this.floatValue2 = var24;
         this.longValue = var25;
         this.intValue2 = var27;
      }
   }

   private static final class Entry10 {
      private final long longValue;
      private final int intValue;
      private final int intValue2;
      private final int intValue3;
      private final int intValue4;
      private final int intValue5;
      private final int intValue6;
      private final int intValue7;

      private Entry10(long var1, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
         this.longValue = var1;
         this.intValue = var3;
         this.intValue2 = var4;
         this.intValue3 = var5;
         this.intValue4 = var6;
         this.intValue5 = var7;
         this.intValue6 = var8;
         this.intValue7 = var9;
      }
   }

   public static final class Entry2 {
      private final RotationRecorderModule rotationRecorderModule = new RotationRecorderModule();
      private int intValue = Integer.MIN_VALUE;
      private UUID uUID = null;
      private int intValue2 = Integer.MIN_VALUE;

      public float[] method4988(LivingEntity var1, Rotation var2, boolean var3) {
         return this.method4990(var1, var2, var3, false);
      }

      public float[] method4989(LivingEntity var1, Rotation var2, boolean var3) {
         return this.method4990(var1, var2, var3, true);
      }

      private float[] method4990(LivingEntity var1, Rotation var2, boolean var3, boolean var4) {
         if (RotationRecorderModule.mc.player != null
            && RotationRecorderModule.mc.world != null
            && var1 != null
            && var1.isAlive()
            && var2 != null
            && !var2.method3137()) {
            if (var1.getId() != this.intValue
               || !var1.getUuid().equals(this.uUID)
               || this.intValue2 != Integer.MIN_VALUE && RotationRecorderModule.mc.player.age != this.intValue2 + 1) {
               this.reset();
               this.intValue = var1.getId();
               this.uUID = var1.getUuid();
            }

            float var5 = var2.getFloatValue();
            float var6 = var2.getFloatValue2();
            if (!this.rotationRecorderModule.flag4) {
               this.rotationRecorderModule.floatValue = var5;
               this.rotationRecorderModule.floatValue2 = var6;
               this.rotationRecorderModule.flag4 = true;
            }

            float var7 = MathHelper.wrapDegrees(var5 - this.rotationRecorderModule.floatValue);
            float var8 = var6 - this.rotationRecorderModule.floatValue2;
            PlayerInput var9 = this.rotationRecorderModule.method4970();
            Box var10 = this.rotationRecorderModule.method4931(var1);
            Vec3d var11 = this.rotationRecorderModule.method4920(var1);
            if (var10 != null && var11 != null) {
               RotationRecorderModule.Entry3 var12 = this.rotationRecorderModule.method4975(var5, var6, var9);
               Vec3d var13 = RotationRecorderModule.mc.player.getEyePos();
               RotationRecorderModule.Entry var14 = this.rotationRecorderModule
                  .method4955(
                     var2, var13, var1, var10, var11, var7, var8, var9, var3, false, null, var12, this.rotationRecorderModule.method4919(var2, var13, var9)
                  );
               this.rotationRecorderModule.floatValue = var5;
               this.rotationRecorderModule.floatValue2 = var6;
               this.intValue2 = RotationRecorderModule.mc.player.age;
               if (var14 == null) {
                  return null;
               } else {
                  this.rotationRecorderModule.entry = var14;
                  return var4 ? var14.data2.method4986(var14.data24.getData3()) : var14.data2.method4985(var14.data24.getData3());
               }
            } else {
               this.reset();
               return null;
            }
         } else {
            this.reset();
            return null;
         }
      }

      public void reset() {
         this.rotationRecorderModule.method4940();
         this.intValue = Integer.MIN_VALUE;
         this.uUID = null;
         this.intValue2 = Integer.MIN_VALUE;
      }
   }

   private static final class Entry3 {
      private final boolean flag;
      private final Vec3d vec3d;
      private final Vec3d vec3d2;
      private final Vec3d vec3d3;
      private final boolean flag2;
      private final boolean flag3;
      private final boolean flag4;

      private Entry3(boolean var1, Vec3d var2, Vec3d var3, Vec3d var4, boolean var5, boolean var6, boolean var7) {
         this.flag = var1;
         this.vec3d = var2;
         this.vec3d2 = var3;
         this.vec3d3 = var4;
         this.flag2 = var5;
         this.flag3 = var6;
         this.flag4 = var7;
      }

      private static RotationRecorderModule.Entry3 method4998(Vec3d var0, Vec3d var1) {
         return new RotationRecorderModule.Entry3(false, Vec3d.ZERO, var0, var1, false, false, false);
      }
   }

   private final class Entry4 {
      private final List<RotationRecorderModule.Entry5> items = new ArrayList<>();
      private final long longValue = RotationRecorderModule.this.method4954();

      private boolean method4999(RotationRecorderModule.Entry5 var1) {
         if (var1 != null && var1.isValid()) {
            this.items.add(var1);
            return true;
         } else {
            return false;
         }
      }

      private void method5000(int var1) {
         int var2 = Math.max(0, this.items.size() - var1);
         if (var2 <= 0) {
            this.items.clear();
            RotationRecorderModule.this.entry4 = null;
         } else {
            for (int var3 = 0; var3 < var2; var3++) {
               RotationRecorderModule.this.method4951(this.longValue, this.items.get(var3));
            }

            if (!RotationRecorderModule.this.disableLogs.isEnabled()) {
               ChatLogger.method2019("RotationRecorder: wrote " + var2);
            }

            this.items.clear();
            RotationRecorderModule.this.entry4 = null;
         }
      }

      private void method5001() {
         this.items.clear();
         RotationRecorderModule.this.entry4 = null;
      }
   }

   private static final class Entry5 {
      private static final int CONST_intValue = method5004();
      private static final int CONST_intValue2 = method5005();
      private final RotationRecorderModule.Data2 data2;
      private final RotationRecorderModule.Data24 data24;
      private final RotationRecorderModule.Data10 data10;
      private final RotationRecorderModule.Data23 data23;
      private final RotationRecorderModule.Data11 data11;
      private final boolean flag;
      private final long longValue;
      private final int intValue;
      private final float floatValue;

      private static void method5002(RotationRecorderModule.Entry8 var0) {
         RotationRecorderModule.Data2.method4983(var0);
         RotationRecorderModule.Data24.method5040(var0);
         RotationRecorderModule.Data10.method5013(var0);
         RotationRecorderModule.Data23.method5039(var0);
         RotationRecorderModule.Data11.method5016(var0);
      }

      private static void method5003(RotationRecorderModule.Entry8 var0) {
         RotationRecorderModule.Data2.method4983(var0);
         RotationRecorderModule.Data24.method5040(var0);
         RotationRecorderModule.Data10.method5013(var0);
         RotationRecorderModule.Data23.method5039(var0);
         var0.method5029("isAim");
         RotationRecorderModule.Data11.method5016(var0);
      }

      private static int method5004() {
         RotationRecorderModule.Entry8 var0 = new RotationRecorderModule.Entry8(new StringBuilder());
         method5002(var0);
         return var0.method5030();
      }

      private static int method5005() {
         RotationRecorderModule.Entry8 var0 = new RotationRecorderModule.Entry8(new StringBuilder());
         method5003(var0);
         return var0.method5030();
      }

      private Entry5(
         RotationRecorderModule.Data2 var1,
         RotationRecorderModule.Data24 var2,
         RotationRecorderModule.Data10 var3,
         RotationRecorderModule.Data23 var4,
         RotationRecorderModule.Data11 var5,
         boolean var6,
         long var7,
         int var9,
         float var10
      ) {
         this.data2 = var1;
         this.data24 = var2;
         this.data10 = var3;
         this.data23 = var4;
         this.data11 = var5;
         this.flag = var6;
         this.longValue = var7;
         this.intValue = var9;
         this.floatValue = var10;
      }

      private static RotationRecorderModule.Entry5 method5006(
         RotationRecorderModule.Entry var0, RotationRecorderModule.Entry var1, RotationRecorderModule.Entry3 var2, boolean var3
      ) {
         boolean var4 = var3 || var0.flag2 || var1.flag2 || !var2.flag;
         boolean var5 = !var4 && method5009(var0) && method5009(var1);
         return new RotationRecorderModule.Entry5(
            var0.data2.method4984(var0),
            var0.data24.method5041(var0.playerInput, var2),
            var0.data10.method5014(var0.data10, var0.flag3, var5),
            new RotationRecorderModule.Data23(var4),
            new RotationRecorderModule.Data11(var1.data2.getData13().getFloatValue(), var1.data2.getData13().getFloatValue2()),
            var1.flag13,
            var0.longValue,
            var0.intValue2,
            var0.floatValue2
         );
      }

      private void method5007(StringBuilder var1) {
         RotationRecorderModule.Entry6 var2 = new RotationRecorderModule.Entry6(var1);
         this.data2.method4981(var2);
         this.data24.method4981(var2);
         this.data10.method4981(var2);
         this.data23.method4981(var2);
         this.data11.method4981(var2);
         if (var2.getIntValue() != CONST_intValue) {
            throw new IllegalStateException("CSV schema mismatch: header has " + CONST_intValue + " columns, data tick has " + var2.getIntValue());
         }
      }

      private void method5008(StringBuilder var1) {
         RotationRecorderModule.Entry6 var2 = new RotationRecorderModule.Entry6(var1);
         this.data2.method4981(var2);
         this.data24.method4981(var2);
         this.data10.method4981(var2);
         this.data23.method4981(var2);
         var2.method5022(this.flag);
         this.data11.method4981(var2);
         if (var2.getIntValue() != CONST_intValue2) {
            throw new IllegalStateException("Aim CSV schema mismatch: header has " + CONST_intValue2 + " columns, data tick has " + var2.getIntValue());
         }
      }

      private static boolean method5009(RotationRecorderModule.Entry var0) {
         return var0 != null
            && !var0.flag2
            && !var0.flag5
            && !var0.flag8
            && !var0.flag9
            && !var0.flag10
            && !var0.flag6
            && !var0.flag11
            && !var0.flag12
            && !var0.playerInput.sneak()
            && method5010(var0.entityPose)
            && var0.data10 != null
            && var0.data10.method5015(var0.flag3);
      }

      private static boolean method5010(EntityPose var0) {
         return var0 == null || var0 == EntityPose.STANDING || var0 == EntityPose.CROUCHING;
      }

      private boolean isValid() {
         return this.data2 != null
            && this.data24 != null
            && this.data10 != null
            && this.data23 != null
            && this.data11 != null
            && this.floatValue > 0.0F
            && !Float.isNaN(this.floatValue)
            && !Float.isInfinite(this.floatValue)
            && this.data2.method4982()
            && this.data24.method4982()
            && this.data10.method4982()
            && this.data23.method4982()
            && this.data11.method4982();
      }
   }

   private static final class Entry6 {
      private final StringBuilder stringBuilder;
      private int intValue;

      private Entry6(StringBuilder var1) {
         this.stringBuilder = var1;
      }

      private void method5021(float... var1) {
         for (float var5 : var1) {
            this.stringBuilder.append(',').append(var5);
            this.intValue++;
         }
      }

      private void method5022(boolean... var1) {
         for (boolean var5 : var1) {
            this.stringBuilder.append(',').append(var5 ? 1.0F : 0.0F);
            this.intValue++;
         }
      }

      private int getIntValue() {
         return this.intValue;
      }
   }

   private static final class Entry7 {
      private final int intValue;
      private final UUID uUID;
      private int intValue2;
      private int intValue3;
      private int intValue4;

      private Entry7(int var1, UUID var2, int var3, int var4, int var5) {
         this.intValue = var1;
         this.uUID = var2;
         this.intValue2 = var3;
         this.intValue3 = var4;
         this.intValue4 = var5;
      }

      private boolean method5026(LivingEntity var1) {
         return var1 != null && this.intValue == var1.getId() && this.uUID.equals(var1.getUuid());
      }
   }

   private static final class Entry8 {
      private final StringBuilder stringBuilder;
      private final Map<String, Boolean> items = new LinkedHashMap<>();

      private Entry8(StringBuilder var1) {
         this.stringBuilder = var1;
      }

      private void method5029(String... var1) {
         for (String var5 : var1) {
            if (var5 == null || var5.isBlank()) {
               throw new IllegalArgumentException("CSV column name must not be blank");
            }

            if (this.items.put(var5, Boolean.TRUE) != null) {
               throw new IllegalArgumentException("Duplicate CSV column: " + var5);
            }

            this.stringBuilder.append(',').append(var5);
         }
      }

      private int method5030() {
         return this.items.size();
      }
   }

   private static final class Entry9 {
      private final int intValue;
      private final UUID uUID;
      private final Deque<RotationRecorderModule.Entry> deque = new ArrayDeque<>();
      private float floatValue;
      private float floatValue2;
      private float floatValue3;
      private float floatValue4;
      private float floatValue5;
      private float floatValue6;
      private float floatValue7;
      private float floatValue8;
      private int intValue2;
      private int intValue3;
      private float floatValue9 = 50.0F;
      private float floatValue10 = 50.0F;
      private float floatValue11;
      private float floatValue12;

      private Entry9(LivingEntity var1) {
         this.intValue = var1.getId();
         this.uUID = var1.getUuid();
      }

      private boolean method5031(LivingEntity var1) {
         return var1 != null && this.intValue == var1.getId() && this.uUID.equals(var1.getUuid());
      }

      private void method5032(RotationRecorderModule.Entry var1) {
         this.deque.addLast(var1);

         while (this.deque.size() > 64) {
            this.deque.removeFirst();
         }
      }
   }

   private interface Handler {
      void method4981(RotationRecorderModule.Entry6 var1);

      boolean method4982();
   }
}

package org.zenith.utility.config;

import com.mojang.authlib.GameProfile;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.Zenith;
import org.zenith.client.screens.entity.ImplOtherClientPlayerEntity;
import org.zenith.utility.api.PetManager;
import org.zenith.utility.game.ArmorSlots;

public class PetEntityState {
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private final UUID uUID;
   private ImplOtherClientPlayerEntity implOtherClientPlayerEntity;
   private String text = "";
   private Vec3d vec3d;
   private Vec3d vec3d2 = Vec3d.ZERO;
   private float floatValue = ThreadLocalRandom.current().nextFloat() * 360.0F;
   private float floatValue2;
   private float floatValue3;
   private Vec3d vec3d3;
   private Vec3d vec3d4;
   private float floatValue4;
   private float floatValue5;
   private float floatValue6;
   private boolean flag;
   private boolean flag2;
   private boolean lastOnGround;
   private long longValue = System.currentTimeMillis();
   private double doubleValue;
   private double doubleValue2;
   private int intValue = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
   private long longValue2 = 0L;
   private boolean flag3 = false;
   private int intValue2 = 0;
   private static final double CONST_doubleValue = 0.12;
   private static final double CONST_doubleValue2 = 0.72;

   public PetEntityState(UUID var1) {
      this.uUID = UUID.nameUUIDFromBytes(("zenith-pet:" + var1.toString()).getBytes());
   }

   public void method2302(ClientWorld var1, Vec3d var2) {
      this.method2303();
      this.text = "";
      this.implOtherClientPlayerEntity = new ImplOtherClientPlayerEntity(var1, new GameProfile(this.uUID, ""));
      this.implOtherClientPlayerEntity.setId(Integer.MAX_VALUE - ThreadLocalRandom.current().nextInt(1, 100000));
      this.implOtherClientPlayerEntity.setCustomNameVisible(false);
      this.implOtherClientPlayerEntity.setInvisible(false);
      this.implOtherClientPlayerEntity.setNoGravity(true);
      this.method2305(Zenith.getInstance().getPetManager().method4572());
      EntityAttributeInstance var3 = this.implOtherClientPlayerEntity.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
      if (var3 != null) {
         var3.setBaseValue(0.6);
      }

      this.vec3d = var2;
      this.vec3d3 = var2;
      this.vec3d4 = var2;
      this.vec3d2 = Vec3d.ZERO;
      this.floatValue4 = 0.0F;
      this.floatValue5 = 0.0F;
      this.floatValue6 = 0.0F;
      this.floatValue2 = 0.0F;
      this.floatValue3 = 0.0F;
      this.doubleValue = var2.x;
      this.doubleValue2 = var2.z;
      this.longValue = System.currentTimeMillis();
      this.implOtherClientPlayerEntity.refreshPositionAndAngles(var2.x, var2.y, var2.z, 0.0F, 0.0F);
      this.implOtherClientPlayerEntity.setHeadYaw(0.0F);
      this.implOtherClientPlayerEntity.setBodyYaw(0.0F);
      var1.addEntity(this.implOtherClientPlayerEntity);
   }

   public void method2303() {
      PetManager.method4560(this);
      if (this.implOtherClientPlayerEntity != null) {
         this.implOtherClientPlayerEntity.setRemoved(RemovalReason.KILLED);
         this.implOtherClientPlayerEntity.onRemoved();
         this.implOtherClientPlayerEntity = null;
      }

      AvatarUtil.method7251(this.uUID);
   }

   public boolean method2304() {
      if (this.implOtherClientPlayerEntity == null || this.implOtherClientPlayerEntity.isRemoved()) {
         return false;
      } else {
         return mc.world == null ? false : mc.world.getEntityById(this.implOtherClientPlayerEntity.getId()) == this.implOtherClientPlayerEntity;
      }
   }

   public void method2305(float var1) {
      if (this.implOtherClientPlayerEntity != null) {
         EntityAttributeInstance var2 = this.implOtherClientPlayerEntity.getAttributeInstance(EntityAttributes.SCALE);
         if (var2 != null) {
            var2.setBaseValue(var1);
         }
      }
   }

   public void method2306(String var1) {
      if (var1 != null && !var1.isBlank()) {
         if (!var1.equals(this.text)) {
            this.text = var1;
            AvatarUtil.method7239(this.uUID, var1);
         }
      } else {
         AvatarUtil.method7251(this.uUID);
         this.text = "";
      }
   }

   public void method2307(Path var1) {
      if (var1 == null) {
         AvatarUtil.method7251(this.uUID);
         this.text = "";
      } else {
         String var2 = AvatarUtil.method7257(var1);
         if (!var2.equals(this.text)) {
            this.text = var2;
            AvatarUtil.method7240(this.uUID, var1);
         }
      }
   }

   public void method2308(Vec3d var1, float var2, boolean var3, LivingEntity var4) {
      this.vec3d4 = this.vec3d3;
      this.floatValue6 = this.floatValue4;
      if (this.vec3d == null) {
         this.vec3d = var1;
      }

      if (this.vec3d.distanceTo(var1) > 10.0) {
         this.vec3d = var1;
         this.vec3d2 = Vec3d.ZERO;
         this.vec3d3 = this.vec3d;
         this.vec3d4 = this.vec3d;
         this.method2314();
      } else {
         Vec3d var5;
         if (this.flag) {
            double var6 = var1.y - this.vec3d.y;
            this.vec3d2 = new Vec3d(this.vec3d2.x, var6 * 0.2, this.vec3d2.z);
            var5 = this.vec3d.add(this.vec3d2);
            this.vec3d2 = new Vec3d(this.vec3d2.x, 0.0, this.vec3d2.z);
         } else {
            this.vec3d2 = this.vec3d2.add(0.0, -0.08, 0.0);
            this.vec3d2 = this.vec3d2.multiply(1.0, 0.98, 1.0);
            if (this.vec3d2.y < -0.5) {
               this.vec3d2 = new Vec3d(this.vec3d2.x, -0.5, this.vec3d2.z);
            }

            double var48 = this.vec3d2.x;
            double var8 = this.vec3d2.y;
            double var10 = this.vec3d2.z;
            double var12 = this.vec3d.x;
            double var14 = this.vec3d.y;
            double var16 = this.vec3d.z;
            boolean var18 = false;
            if (!method2317(var12, var14 + var8, var16)) {
               var14 += var8;
            } else {
               if (var8 < 0.0) {
                  double var19 = var14 + var8;
                  double var21 = var14;

                  for (int var23 = 0; var23 < 8; var23++) {
                     double var24 = (var19 + var21) * 0.5;
                     if (method2317(var12, var24, var16)) {
                        var19 = var24;
                     } else {
                        var21 = var24;
                     }
                  }

                  var14 = var21;
                  var18 = true;
               }

               var8 = 0.0;
            }

            if (!var18 && method2317(var12, var14 - 0.02, var16)) {
               var18 = true;
            }

            if (Math.abs(var48) > 1.0E-5) {
               if (!method2317(var12 + var48, var14, var16)) {
                  var12 += var48;
               } else {
                  boolean var58 = false;
                  if (var18) {
                     for (double var20 = 0.1; var20 <= 0.6; var20 += 0.1) {
                        if (!method2317(var12 + var48, var14 + var20, var16) && !method2317(var12, var14 + var20, var16)) {
                           var14 += var20;
                           var12 += var48;
                           var58 = true;
                           break;
                        }
                     }
                  }

                  if (!var58) {
                     if (var18 && var8 <= 0.0 && method2319(var12 + Math.signum(var48) * 0.35, var14, var16)) {
                        var8 = 0.52;
                     }

                     var48 = 0.0;
                  }
               }
            }

            if (Math.abs(var10) > 1.0E-5) {
               if (!method2317(var12, var14, var16 + var10)) {
                  var16 += var10;
               } else {
                  boolean var59 = false;
                  if (var18) {
                     for (double var61 = 0.1; var61 <= 0.6; var61 += 0.1) {
                        if (!method2317(var12, var14 + var61, var16 + var10) && !method2317(var12, var14 + var61, var16)) {
                           var14 += var61;
                           var16 += var10;
                           var59 = true;
                           break;
                        }
                     }
                  }

                  if (!var59) {
                     if (var18 && var8 <= 0.0 && method2319(var12, var14, var16 + Math.signum(var10) * 0.35)) {
                        var8 = 0.52;
                     }

                     var10 = 0.0;
                  }
               }
            }

            this.vec3d2 = new Vec3d(var48, var8, var10);
            var5 = new Vec3d(var12, var14, var16);
            this.lastOnGround = var18;
         }

         double var49 = this.lastOnGround ? 1.0 : 0.2;
         if (var4 != null && var4.isAlive()) {
            Box var51 = var4.getBoundingBox().expand(0.1);
            double var9 = (var51.minX + var51.maxX) * 0.5;
            double var56 = (var51.minZ + var51.maxZ) * 0.5;
            double var57 = (var51.maxX - var51.minX) * 0.5;
            double var15 = (var51.maxZ - var51.minZ) * 0.5;
            double var17 = Math.max(var57, var15) + 0.6;
            double var60 = var4.getY();
            double var62 = Math.abs(var5.y - var60);
            double var63 = Math.hypot(var5.x - var9, var5.z - var56);
            if (var62 > 1.0 && this.lastOnGround || var63 > 4.0) {
               double var64 = Math.atan2(var1.z - var56, var1.x - var9);
               double var66 = var9 + Math.cos(var64) * var17;
               double var67 = var56 + Math.sin(var64) * var17;
               var5 = new Vec3d(var66, var60, var67);
               this.vec3d2 = Vec3d.ZERO;
               this.vec3d = var5;
               this.vec3d3 = var5;
               this.vec3d4 = var5;
               this.intValue2 = 0;
               this.method2314();
               return;
            }

            double var25 = Math.atan2(var5.z - var56, var5.x - var9);
            double var27 = var25 + 0.3 * this.intValue;
            double var29 = Math.atan2(var1.z - var56, var1.x - var9);
            double var31 = var27 - var29;

            while (var31 > Math.PI) {
               var31 -= Math.PI * 2;
            }

            while (var31 < -Math.PI) {
               var31 += Math.PI * 2;
            }

            double var33 = Math.PI / 2;
            if (var31 > Math.PI / 2) {
               var31 = Math.PI / 2;
               this.intValue = -this.intValue;
            } else if (var31 < -Math.PI / 2) {
               var31 = -Math.PI / 2;
               this.intValue = -this.intValue;
            }

            var27 = var29 + var31;
            double var35 = var9 + Math.cos(var27) * var17;
            double var37 = var56 + Math.sin(var27) * var17;
            double var39 = MathHelper.lerp(0.35, var5.x, var35);
            double var41 = MathHelper.lerp(0.35, var5.z, var37);
            double var43 = var5.x;
            double var45 = var5.z;
            if (!method2317(var39, var5.y, var5.z) && method2318(var39, var5.y, var5.z)) {
               var43 = var39;
            }

            if (!method2317(var43, var5.y, var41) && method2318(var43, var5.y, var41)) {
               var45 = var41;
            }

            var5 = new Vec3d(var43, var5.y, var45);
            this.vec3d2 = new Vec3d(0.0, this.vec3d2.y, 0.0);
            if (this.implOtherClientPlayerEntity.age % 40 == 0) {
               this.intValue = -this.intValue;
            }

            if (this.lastOnGround) {
               this.intValue2++;
               if (this.intValue2 >= 3) {
                  this.vec3d2 = new Vec3d(this.vec3d2.x, 0.45, this.vec3d2.z);
                  this.intValue2 = 0;
               }
            } else {
               this.intValue2 = 0;
            }

            this.flag3 = true;
         } else {
            this.flag3 = false;
            double var50 = var5.distanceTo(var1);
            if (var50 > 2.0) {
               Vec3d var54 = var1.subtract(var5);
               double var11 = Math.min(1.0, (var50 - 2.0) / 3.0) * var49;
               Vec3d var13 = var54.normalize();
               this.vec3d2 = this.vec3d2.add(var13.x * var11, 0.0, var13.z * var11);
            }
         }

         this.method2309(var1, var4);
         this.vec3d = var5;
         if (this.vec3d.distanceTo(var1) < 0.1) {
            this.floatValue = ThreadLocalRandom.current().nextFloat() * 360.0F;
            double var52 = -Math.sin(Math.toRadians(this.floatValue)) * 0.1;
            double var55 = Math.cos(Math.toRadians(this.floatValue)) * 0.1;
            this.vec3d2 = this.vec3d2.add(var52, 0.0, var55);
         }

         float var53 = this.lastOnGround ? 0.6F : 0.91F;
         this.vec3d2 = new Vec3d(this.vec3d2.x * var53, this.vec3d2.y, this.vec3d2.z * var53);
         this.vec3d3 = this.vec3d;
         if (Math.abs(this.vec3d.x - this.doubleValue) > 0.1 || Math.abs(this.vec3d.z - this.doubleValue2) > 0.1) {
            this.longValue = System.currentTimeMillis();
         }

         this.doubleValue = this.vec3d.x;
         this.doubleValue2 = this.vec3d.z;
         this.method2314();
      }
   }

   private void method2309(Vec3d var1, LivingEntity var2) {
      Vec3d var3 = var2 != null && var2.isAlive() ? var2.getEntityPos() : var1;
      double var4 = var3.x - this.vec3d.x;
      double var6 = var3.z - this.vec3d.z;
      float var8 = (float)Math.toDegrees(Math.atan2(var6, var4)) - 90.0F;
      this.floatValue2 = var8;
      this.floatValue3 = var8;
      this.floatValue4 = var8;
      this.floatValue5 = var8;
   }

   public void method2310() {
   }

   public void method2311(Vec3d var1, float var2, boolean var3) {
      this.vec3d4 = this.vec3d3;
      this.floatValue6 = this.floatValue4;
      this.vec3d = var1;
      this.vec3d3 = var1;
      this.floatValue4 = var2;
      this.floatValue5 = var2;
      this.floatValue3 = var2;
      this.method2314();
   }

   public void method2312(PlayerEntity var1) {
      if (this.implOtherClientPlayerEntity != null && !this.implOtherClientPlayerEntity.isRemoved() && var1 != null) {
         this.implOtherClientPlayerEntity.getInventory().setSelectedSlot(var1.getInventory().getSelectedSlot());
         int var2 = var1.getInventory().getSelectedSlot();
         this.implOtherClientPlayerEntity.getInventory().getMainStacks().set(var2, var1.getInventory().getMainStacks().get(var2).copy());
         this.implOtherClientPlayerEntity.equipStack(EquipmentSlot.OFFHAND, var1.getEquippedStack(EquipmentSlot.OFFHAND).copy());

         for (int var3 = 0; var3 < 4; var3++) {
            this.implOtherClientPlayerEntity.equipStack(ArmorSlots.equipmentSlots[var3], var1.getEquippedStack(ArmorSlots.equipmentSlots[var3]).copy());
         }
      }
   }

   public Vec3d method2313(float var1) {
      return this.vec3d4 != null && this.vec3d3 != null
         ? new Vec3d(
            MathHelper.lerp((double)var1, this.vec3d4.x, this.vec3d3.x),
            MathHelper.lerp((double)var1, this.vec3d4.y, this.vec3d3.y),
            MathHelper.lerp((double)var1, this.vec3d4.z, this.vec3d3.z)
         )
         : this.vec3d3;
   }

   private void method2314() {
      if (this.implOtherClientPlayerEntity != null && !this.implOtherClientPlayerEntity.isRemoved()) {
         this.implOtherClientPlayerEntity.updateTrackedPositionAndAngles(new Vec3d(this.vec3d3.x, this.vec3d3.y, this.vec3d3.z), this.floatValue4, 0.0F);
         this.implOtherClientPlayerEntity.updateTrackedHeadRotation(this.floatValue5, 3);
         this.implOtherClientPlayerEntity.updateTrackedPosition(this.vec3d3.x, this.vec3d3.y, this.vec3d3.z);
         this.implOtherClientPlayerEntity.setOnGround(this.lastOnGround);
         this.implOtherClientPlayerEntity.setVelocity(this.vec3d2);
      }
   }

   private static boolean method2315(double var0, double var2, double var4) {
      if (mc.world == null) {
         return false;
      } else {
         BlockPos var6 = BlockPos.ofFloored(var0, var2, var4);
         return mc.world.getBlockState(var6).isSolid();
      }
   }

   private static Box method2316(double var0, double var2, double var4) {
      return new Box(var0 - 0.12, var2, var4 - 0.12, var0 + 0.12, var2 + 0.72, var4 + 0.12);
   }

   private static boolean method2317(double var0, double var2, double var4) {
      return mc.world != null && !mc.world.isSpaceEmpty(method2316(var0, var2, var4));
   }

   private static boolean method2318(double var0, double var2, double var4) {
      if (mc.world == null) {
         return true;
      } else {
         for (double var6 = 0.01; var6 <= 1.5; var6 += 0.25) {
            if (!mc.world.isSpaceEmpty(method2316(var0, var2 - var6, var4))) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean method2319(double var0, double var2, double var4) {
      return mc.world == null ? false : !method2317(var0, var2 + 1.0, var4);
   }

   private static float method2320(float var0, float var1, float var2) {
      return var0 + MathHelper.wrapDegrees(var1 - var0) * var2;
   }

   @Generated
   public UUID getUUID() {
      return this.uUID;
   }

   @Generated
   public ImplOtherClientPlayerEntity getImplOtherClientPlayerEntity() {
      return this.implOtherClientPlayerEntity;
   }

   @Generated
   public String getText() {
      return this.text;
   }

   @Generated
   public Vec3d getVec3d3() {
      return this.vec3d3;
   }

   @Generated
   public Vec3d getVec3d4() {
      return this.vec3d4;
   }

   @Generated
   public float getFloatValue4() {
      return this.floatValue4;
   }

   @Generated
   public float getFloatValue5() {
      return this.floatValue5;
   }

   @Generated
   public float getFloatValue6() {
      return this.floatValue6;
   }

   @Generated
   public boolean isFlag() {
      return this.flag;
   }

   @Generated
   public void setFlag(boolean var1) {
      this.flag = var1;
   }
}

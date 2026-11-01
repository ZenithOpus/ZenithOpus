package org.zenith.client.modules.impl.movement;

import com.darkmagician6.eventapi.EventTarget;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.collections.EmptyMap;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.Rotation;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.events.EntityAddEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.InventoryUtils;
import org.zenith.utility.render.MathUtils;

@ModuleInfo(
   name = "AutoDodge",
   category = ModuleCategory.MOVEMENT,
   description = "module.autoDodge.desc"
)
public final class AutoDodgeModule extends Module {
   public static final AutoDodgeModule INSTANCE = new AutoDodgeModule();
   private static final int CONST_intValue = -13447886;
   private static final int CONST_intValue2 = -16776961;
   private static final String text = "На этой анархии этот предмет не работает";
   private static final int CONST_intValue3 = -50;
   private static final int CONST_intValue4 = 70;
   private static final double CONST_doubleValue = 9.99999773128142E-7;
   private static final double CONST_doubleValue2 = 0.8000000016738433;
   private static final double CONST_doubleValue3 = 0.9900000205305426;
   private static final double CONST_doubleValue4 = 0.0500000024232657;
   private static final double CONST_doubleValue5 = 0.11999995180429479;
   private static final double CONST_doubleValue6 = 2.0;
   private static final double CONST_doubleValue7 = 2.300000381469741;
   private static final double CONST_doubleValue8 = 25.0;
   private static final double CONST_doubleValue9 = 2.0;
   private static final double CONST_doubleValue10 = 15.0;
   private static final double CONST_doubleValue11 = 0.10000000396251493;
   private static final double CONST_doubleValue12 = 400.0;
   private static final double CONST_doubleValue13 = 20.0;
   private static final float CONST_floatValue = 3.0F;
   private final Map<Integer, AutoDodgeModule.Data2> items = new HashMap<>();
   private int intValue;

   private AutoDodgeModule() {
   }

   private static boolean method4690(PotionEntity var0, Box var1) {
      Vec3d var2 = var0.getVelocity();
      Vec3d var3 = var0.getEntityPos();

      for (int var4 = 0;
         var4 < 70 && var2.lengthSquared() >= 9.99999773128142E-7 && var3.y >= mc.world.getBottomY() && var3.y <= mc.world.getBottomY() + mc.world.getHeight();
         var4++
      ) {
         double var5 = mc.world.getFluidState(BlockPos.ofFloored(var3)).isIn(FluidTags.WATER) ? 0.8000000016738433 : 0.9900000205305426;
         var2 = new Vec3d(var2.x * var5, (var2.y - 0.0500000024232657) * var5, var2.z * var5);
         Vec3d var7 = var3.add(var2);
         BlockHitResult var8 = mc.world.raycast(new RaycastContext(var3, var7, ShapeType.COLLIDER, FluidHandling.NONE, var0));
         if (var8.getType() == Type.BLOCK) {
            return method4691(var1, var3, var8.getPos());
         }

         if (method4691(var1, var3, var7)) {
            return true;
         }

         var3 = var7;
      }

      return false;
   }

   private static boolean method4691(Box var0, Vec3d var1, Vec3d var2) {
      return new Box(
            Math.min(var1.x, var2.x),
            Math.min(var1.y, var2.y),
            Math.min(var1.z, var2.z),
            Math.max(var1.x, var2.x),
            Math.max(var1.y, var2.y),
            Math.max(var1.z, var2.z)
         )
         .expand(0.11999995180429479)
         .intersects(var0);
   }

   private static int method4692(ItemStack var0) {
      PotionContentsComponent var1 = var0.get(DataComponentTypes.POTION_CONTENTS);
      return var1 != null ? var1.getColor() & 16777215 : 0;
   }

   @Override
   public void onDisable() {
      this.items.clear();
      super.onDisable();
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null) {
         var var2 = this.items.entrySet().iterator();

         while (var2.hasNext()) {
            if (mc.world.getEntityById((Integer)((Entry)var2.next()).getKey()) == null) {
               var2.remove();
            }
         }

         boolean var10000;
         label78: {
            if (this.items != null) {
               if (this.items.getClass() == EmptyMap.class) {
                  var10000 = true;
                  break label78;
               }
            }

            var10000 = this.items.isEmpty();
         }

         if (var10000) {
            this.intValue++;
         } else {
            Box var3 = mc.player.getBoundingBox().expand(2.0);
            double var4 = mc.options.getViewDistance().getValue() * 16;

            for (PotionEntity var7 : mc.world.getEntitiesByClass(PotionEntity.class, mc.player.getBoundingBox().expand(var4), var0 -> true)) {
               AutoDodgeModule.Data2 var8 = this.items.get(var7.getId());
               if (var8 != null) {
                  boolean var9 = method4690(var7, var3);
                  int var10 = 0xFF000000 | var8.color() & 16777215;
                  if (!var9 || Zenith.getInstance().getFriends().method7627(var8.getText())) {
                     return;
                  }

                  if (var10 != -13447886 && var10 != -16776961 || mc.player.distanceTo(var7) <= 2.300000381469741 || this.intValue < 0) {
                     return;
                  }

                  ItemStack var11 = Items.DRIED_KELP.getDefaultStack();
                  if (!mc.player.getItemCooldownManager().isCoolingDown(var11) && InventoryUtils.method906(Items.DRIED_KELP) != null) {
                     if (Zenith.getInstance().getTaskScheduler().method5116()) {
                        if (Zenith.getInstance().getRotationManager().method7844().method3130(new Rotation(mc.player.getYaw(), mc.player.getPitch())) < 20.0) {
                           this.intValue++;
                        }

                        if (this.intValue >= 2) {
                           this.intValue = 5;
                           InventoryUtils.method927(Items.DRIED_KELP);
                        }
                     }

                     Rotation var12 = Rotation.method3128(var7.getEyePos(), mc.player.getEyePos());
                     this.method4694(var12.method3140((float)MathUtils.method2651(-3.0, 3.0), (float)MathUtils.method2651(-3.0, 3.0)));
                     break;
                  }
               }
            }

            this.intValue++;
            return;
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (mc.player != null && mc.world != null && var1.method7100()) {
         if (var1.getPacket3() instanceof GameMessageS2CPacket var2 && var2.content().getString().equals("На этой анархии этот предмет не работает")) {
            this.intValue = -50;
         }
      }
   }

   @EventTarget
   public void method4693(EntityAddEvent var1) {
      if (mc.player != null && mc.world != null) {
         if (var1.getEntity() instanceof PotionEntity var2) {
            Vec3d var16 = var2.getEntityPos();
            Vec3d var4 = var2.getVelocity();
            double var5 = Double.MAX_VALUE;
            String var7 = null;
            int var8 = 0;

            for (Entry var10 : this.method4695().entrySet()) {
               AutoDodgeModule.Data var11 = (AutoDodgeModule.Data)var10.getValue();
               double var12;
               if (!((var12 = var16.distanceTo(var11.getVec3d())) > 25.0) && !(var12 >= var5)) {
                  int var14;
                  if (var11.getVec3d().y - var16.y > 2.0) {
                     Vec3d var15;
                     var14 = new Vec3d(var16.x - (var15 = var11.getVec3d()).x, 0.0, var16.z - var15.z).length() < 15.0 ? 1 : 0;
                  } else {
                     var14 = !(var4.lengthSquared() <= 9.99999773128142E-7)
                           && !(var4.normalize().dotProduct(var11.getVec3d2().normalize()) > 0.10000000396251493)
                        ? 0
                        : 1;
                  }

                  if (var14 != 0) {
                     var5 = var12;
                     var8 = var11.color();
                     var7 = (String)var10.getKey();
                  }
               }
            }

            if (var7 != null) {
               this.intValue = 0;
               this.items.put(var2.getId(), new AutoDodgeModule.Data2(var8, var7));
            }
         }
      }
   }

   private void method4694(Rotation var1) {
      Rotation var2 = bcCameraTestnerv.method1851(bcCameraTestnerv.getRotationConfig2(), var1);
      Zenith.getInstance().getRotationManager().method7849(new RotationRequest(var2, () -> var2, bcCameraTestnerv.getRotationConfig2()), 20, this);
   }

   private Map<String, AutoDodgeModule.Data> method4695() {
      HashMap var1 = new HashMap();

      for (AbstractClientPlayerEntity var3 : mc.world.getPlayers()) {
         ItemStack var4 = var3.getMainHandStack();
         ItemStack var5 = var3.getOffHandStack();
         int var6;
         if ((var6 = var4.getItem() == Items.SPLASH_POTION ? method4692(var4) : (var5.getItem() == Items.SPLASH_POTION ? method4692(var5) : -1)) >= 0
            && var3 != mc.player
            && mc.player.squaredDistanceTo(var3) <= 400.0) {
            var1.put(var3.getName().getString(), new AutoDodgeModule.Data(var6, var3.getEntityPos(), var3.getRotationVec(1.0F)));
         }
      }

      return var1;
   }

   private static final class Data {
      private final int intValue;
      private final Vec3d vec3d;
      private final Vec3d vec3d2;

      public int intValue() {
         return this.intValue;
      }

      public Vec3d vec3d() {
         return this.vec3d;
      }

      public Vec3d vec3d2() {
         return this.vec3d2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.vec3d, other.vec3d) && java.util.Objects.equals(this.vec3d2, other.vec3d2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, vec3d, vec3d2);
      }

      @Override
      public String toString() {
         return "Data[" + "intValue=" + this.intValue + ", " + "vec3d=" + this.vec3d + ", " + "vec3d2=" + this.vec3d2 + "]";
      }

      private Data(int var1, Vec3d var2, Vec3d var3) {
         this.intValue = var1;
         this.vec3d = var2;
         this.vec3d2 = var3;
      }

      public int color() {
         return this.intValue;
      }

      public Vec3d getVec3d() {
         return this.vec3d;
      }

      public Vec3d getVec3d2() {
         return this.vec3d2;
      }
   }

   private static final class Data2 {
      private final int intValue;
      private final String text;

      public int intValue() {
         return this.intValue;
      }

      public String text() {
         return this.text;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data2 other)) return false;
         return java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.text, other.text);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(intValue, text);
      }

      @Override
      public String toString() {
         return "Data2[" + "intValue=" + this.intValue + ", " + "text=" + this.text + "]";
      }

      private Data2(int var1, String var2) {
         this.intValue = var1;
         this.text = var2;
      }

      public int color() {
         return this.intValue;
      }

      public String getText() {
         return this.text;
      }
   }
}

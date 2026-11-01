package org.zenith.utility.api;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Generated;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.zenith.MinecraftAccess;
import org.zenith.Zenith;
import org.zenith.client.events.JumpEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.events.WorldRenderEvent;
import org.zenith.client.modules.impl.combat.AimAssistModule;
import org.zenith.client.modules.impl.combat.AuraModule;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.Setting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.config.PetEntityState;
import org.zenith.utility.misc.PartyPlayerInfo;
import org.zenith.utility.misc.ShareCodec;
import org.zenith.utility.network.Cloud;

public class PetManager implements MinecraftAccess {
   public static final float CONST_floatValue = 0.4F;
   private static final int CONST_intValue = 8;
   private final MultiSelectSetting render = new MultiSelectSetting("cosmetics.pet.render");
   private final SliderSetting scale;
   private Path path;
   private PetEntityState petEntityState;
   private final Map<UUID, PetEntityState> items;
   private static final Set<UUID> items2 = ConcurrentHashMap.newKeySet();
   private static final Set<Integer> items3 = ConcurrentHashMap.newKeySet();

   public List<Setting> getSettings() {
      return Arrays.stream(this.getClass().getDeclaredFields()).map(var1 -> {
         try {
            var1.setAccessible(true);
            return var1.get(this);
         } catch (Exception var3) {
            return null;
         }
      }).filter(var0 -> var0 instanceof Setting).map(var0 -> (Setting)var0).collect(Collectors.toList());
   }

   public boolean method4551() {
      return !this.render.method5222("cosmetics.pet.render.self").isEnabled() && !this.render.method5222("cosmetics.pet.render.friends").isEnabled();
   }

   public boolean method4552() {
      return this.render.method5222("cosmetics.pet.render.self").isEnabled();
   }

   public boolean method4553() {
      return this.render.method5222("cosmetics.pet.render.friends").isEnabled();
   }

   public PetManager() {
      new MultiSelectSetting.Entry(this.render, "cosmetics.pet.render.self", true);
      new MultiSelectSetting.Entry(this.render, "cosmetics.pet.render.friends", true);
      this.scale = new SliderSetting("cosmetics.pet.scale", 0.4F, 0.1F, 2.0F, 0.05F, (var1, var2) -> this.method4573());
      this.items = new ConcurrentHashMap<>();
      EventManager.register(this);
   }

   public void method4554(Path var1) {
      this.path = var1;
      if (var1 != null && this.method4566(var1) && mc.player != null && mc.world != null) {
         this.method4570();
         this.petEntityState.method2307(var1);
      } else if (var1 == null && this.petEntityState != null) {
         this.method4571();
      }
   }

   public String method4555() {
      return this.method4566(this.path) ? org.zenith.utility.config.AvatarUtil.method7257(this.path) : "";
   }

   public static boolean method4556(UUID var0) {
      return var0 != null && items2.contains(var0);
   }

   public static boolean method4557(int var0) {
      return items3.contains(var0);
   }

   public static void method4558(PetEntityState var0) {
      if (var0 != null && var0.getImplOtherClientPlayerEntity() != null) {
         items2.add(var0.getUUID());
         items3.add(var0.getImplOtherClientPlayerEntity().getId());
      }
   }

   public static boolean method4559() {
      return !items3.isEmpty();
   }

   public static void method4560(PetEntityState var0) {
      if (var0 != null) {
         items2.remove(var0.getUUID());
         if (var0.getImplOtherClientPlayerEntity() != null) {
            items3.remove(var0.getImplOtherClientPlayerEntity().getId());
         }
      }
   }

   @EventTarget
   public void method4561(UpdateEvent var1) {
      if (mc.player != null && mc.world != null) {
         this.method4565();
         this.method4567();
      } else {
         this.method4574();
      }
   }

   @EventTarget
   public void method4562(TickEvent var1) {
      if (mc.world == null && (this.petEntityState != null || !this.items.isEmpty())) {
         try {
            this.method4574();
         } catch (Throwable var3) {
            this.petEntityState = null;
            this.items.clear();
            items2.clear();
            items3.clear();
         }
      }
   }

   @EventTarget
   public void method4563(JumpEvent var1) {
      if (this.petEntityState != null && this.petEntityState.method2304()) {
         this.petEntityState.method2310();
      }
   }

   @EventTarget
   public void method4564(WorldRenderEvent var1) {
      if (this.petEntityState != null && this.petEntityState.method2304()) {
         Vec3d var2 = this.petEntityState.method2313(var1.getFloatValue());
         if (var2 != null && this.petEntityState.getImplOtherClientPlayerEntity() != null) {
            this.petEntityState.getImplOtherClientPlayerEntity().updateTrackedPosition(var2.x, var2.y, var2.z);
         }
      }

      for (PetEntityState var3 : this.items.values()) {
         if (var3.method2304()) {
            Vec3d var4 = var3.method2313(var1.getFloatValue());
            if (var4 != null && var3.getImplOtherClientPlayerEntity() != null) {
               var3.getImplOtherClientPlayerEntity().updateTrackedPosition(var4.x, var4.y, var4.z);
            }
         }
      }
   }

   private void method4565() {
      if (this.path != null && this.method4552() && this.method4566(this.path)) {
         this.method4570();
         if (!this.petEntityState.method2304()) {
            Vec3d var1 = mc.player.getEntityPos().add(2.0, 0.0, 0.0);
            this.petEntityState.method2302(mc.world, var1);
            this.petEntityState.method2307(this.path);
            method4558(this.petEntityState);
         }

         this.petEntityState.setFlag(this.method4569());
         LivingEntity var4 = null;

         try {
            AuraModule var2 = AuraModule.INSTANCE;
            var4 = var2.method6358() == null ? AimAssistModule.INSTANCE.method5156() : var2.method6358();
         } catch (Exception var3) {
         }

         this.petEntityState.method2308(mc.player.getEntityPos(), mc.player.bodyYaw, mc.player.isSneaking(), var4);
         this.petEntityState.method2312(mc.player);
         if (this.petEntityState.getImplOtherClientPlayerEntity() != null) {
            this.petEntityState.getImplOtherClientPlayerEntity().handSwingProgress = mc.player.handSwingProgress;
            this.petEntityState.getImplOtherClientPlayerEntity().handSwinging = mc.player.handSwinging;
            this.petEntityState.getImplOtherClientPlayerEntity().handSwingTicks = mc.player.handSwingTicks;
            this.petEntityState.getImplOtherClientPlayerEntity().preferredHand = mc.player.preferredHand;
            this.petEntityState.getImplOtherClientPlayerEntity().setSneaking(mc.player.isSneaking());
            this.petEntityState.getImplOtherClientPlayerEntity().setPose(mc.player.isSneaking() ? EntityPose.CROUCHING : EntityPose.STANDING);
         }
      } else {
         if (this.petEntityState != null) {
            this.method4571();
         }
      }
   }

   private boolean method4566(Path var1) {
      return var1 != null && !org.zenith.utility.config.AvatarUtil.method7257(var1).isBlank();
   }

   private void method4567() {
      if (mc.world != null && mc.player != null) {
         if (!this.method4553()) {
            for (PetEntityState var13 : this.items.values()) {
               method4560(var13);
               var13.method2303();
            }

            this.items.clear();
         } else {
            HashSet var1 = new HashSet();
            int var2 = 0;
            Cloud var3 = Zenith.getInstance().getCloud();
            String var4 = var3 != null && var3.method7862() ? var3.method7911() : "";
            if (!var4.isEmpty()) {
               for (PartyPlayerInfo var6 : var3.state().method8038()) {
                  if (var2 >= 8) {
                     break;
                  }

                  if (!var3.prefs().method3316(var6.username()) && var4.equals(var6.server())) {
                     ShareCodec.Data2 var7 = ShareCodec.method6724(var6.uid(), var6.getItems());
                     String var8 = var7.getText3();
                     if (!var8.isBlank()) {
                        PlayerEntity var9 = this.method4568(var6.getText2());
                        if (var9 != null) {
                           UUID var10 = var9.getUuid();
                           var1.add(var10);
                           PetEntityState var11 = this.items.computeIfAbsent(var10, PetEntityState::new);
                           if (!var11.method2304()) {
                              var11.method2302(mc.world, var9.getEntityPos().add(2.0, 0.0, 0.0));
                              method4558(var11);
                           }

                           var11.method2306(var8);
                           var11.method2305(var7.getFloatValue());
                           var11.setFlag(var9.getAbilities().flying || var9.isGliding());
                           var11.method2308(var9.getEntityPos(), var9.bodyYaw, var9.isSneaking(), null);
                           var11.method2312(var9);
                           var2++;
                        }
                     }
                  }
               }
            }

            var var14 = this.items.entrySet().iterator();

            while (var14.hasNext()) {
               Entry var15 = (Entry)var14.next();
               if (!var1.contains(var15.getKey())) {
                  method4560((PetEntityState)var15.getValue());
                  ((PetEntityState)var15.getValue()).method2303();
                  var14.remove();
               }
            }
         }
      }
   }

   private PlayerEntity method4568(String var1) {
      if (var1 != null && !var1.isBlank()) {
         for (AbstractClientPlayerEntity var3 : mc.world.getPlayers()) {
            if (var3 != mc.player && var1.equalsIgnoreCase(var3.getGameProfile().name())) {
               return var3;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private boolean method4569() {
      return mc.player == null ? false : mc.player.getAbilities().flying || mc.player.isGliding();
   }

   private void method4570() {
      if (this.petEntityState == null) {
         this.petEntityState = new PetEntityState(mc.player.getUuid());
      }
   }

   private void method4571() {
      if (this.petEntityState != null) {
         method4560(this.petEntityState);
         this.petEntityState.method2303();
         this.petEntityState = null;
      }
   }

   public float method4572() {
      return this.scale.getValue3();
   }

   private void method4573() {
      if (this.petEntityState != null) {
         this.petEntityState.method2305(this.method4572());
      }
   }

   public JsonObject save() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("name", org.zenith.utility.config.AvatarUtil.method7257(this.path));
      JsonObject var2 = new JsonObject();

      for (Setting var4 : this.getSettings()) {
         var4.safe(var2);
      }

      var1.add("settings", var2);
      return var1;
   }

   public void load(JsonObject var1) {
      if (var1 != null) {
         if (var1.has("settings") && var1.get("settings").isJsonObject()) {
            JsonObject var2 = var1.getAsJsonObject("settings");

            for (Setting var4 : this.getSettings()) {
               if (var2.has(var4.getKey())) {
                  var4.load(var2);
               }
            }
         }

         if (var1.has("name")) {
            String var5 = var1.get("name").isJsonNull() ? "" : var1.get("name").getAsString();
            this.method4554(var5 != null && !var5.isBlank() ? org.zenith.utility.config.AvatarUtil.method7258(var5) : null);
         }
      }
   }

   public void method4574() {
      this.method4571();

      for (PetEntityState var2 : this.items.values()) {
         method4560(var2);
         var2.method2303();
      }

      this.items.clear();
   }

   @Generated
   public MultiSelectSetting getRender() {
      return this.render;
   }

   @Generated
   public SliderSetting getScale() {
      return this.scale;
   }

   @Generated
   public Path getPath() {
      return this.path;
   }
}

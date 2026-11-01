package org.zenith.client.scripting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.zenith.Rotation;
import org.zenith.RotationManager;
import org.zenith.Zenith;
import org.zenith.base.Module;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.settings.Setting;
import org.zenith.utility.misc.HudHolder;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.misc.ScriptPermissions;

public final class ScriptScreenApi {
   private static final Map<Script, Scriptable> items = new WeakHashMap<>();
   private static final Map<KeyBinding, int[]> items2 = new IdentityHashMap<>();
   private static final Map<KeyBinding, Script> items3 = new IdentityHashMap<>();
   private static final Map<Script, long[]> items4 = new WeakHashMap<>();

   private ScriptScreenApi() {
   }

   public static MinecraftClient method7043() {
      return MinecraftClient.getInstance();
   }

   public static Scriptable method7044(Script var0) {
      return items.get(var0);
   }

   public static void method7045(Script var0, Scriptable var1) {
      items.put(var0, var1);
      var0.method1413(() -> items.remove(var0, var1));
   }

   public static void method7046(Script var0, Script.Type var1, Function var2) {
      ScriptModuleApi.method321(var0, var1, var2);
   }

   public static String method7047(Screen var0) {
      if (var0 == null) {
         return "";
      } else if (var0 instanceof ChatScreen) {
         return "chat";
      } else if (var0 instanceof InventoryScreen) {
         return "inventory";
      } else if (var0 instanceof CreativeInventoryScreen) {
         return "creative";
      } else if (var0 instanceof HandledScreen) {
         return "container";
      } else if (var0 instanceof GameMenuScreen) {
         return "pause";
      } else if (var0 instanceof DeathScreen) {
         return "death";
      } else if (var0 instanceof TitleScreen) {
         return "title";
      } else {
         return var0 instanceof NLMenuScreen ? "clickgui" : "other";
      }
   }

   public static Entity method7048(Object var0) {
      if (method7043().world != null && !ScriptValueUtils.method6253(var0)) {
         double var1;
         if (var0 instanceof Scriptable var3) {
            if (!(ScriptValueUtils.method6263(var3, "id") instanceof Function var5)) {
               return null;
            }

            var1 = Context.toNumber(var5.call(Context.getCurrentContext(), var3, var3, new Object[0]));
         } else {
            var1 = Context.toNumber(var0);
         }

         return method7043().world.getEntityById((int)var1);
      } else {
         return null;
      }
   }

   public static Scriptable method7049(Script var0, Scriptable var1) {
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6247(var1);
      var2.method6274("exists", var0x -> method7043().player != null);
      var2.method6274("name", var0x -> method7043().getSession().getUsername());
      var2.method6274("x", var0x -> method7051() == null ? 0.0 : method7051().getX());
      var2.method6274("y", var0x -> method7051() == null ? 0.0 : method7051().getY());
      var2.method6274("z", var0x -> method7051() == null ? 0.0 : method7051().getZ());
      var2.method6274(
         "pos",
         var1x -> method7051() == null
            ? ScriptValueUtils.method6270(var1, 0.0, 0.0, 0.0)
            : ScriptValueUtils.method6270(var1, method7051().getX(), method7051().getY(), method7051().getZ())
      );
      var2.method6274("eyeY", var0x -> method7051() == null ? 0.0 : method7051().getEyeY());
      var2.method6274("velocity", var1x -> {
         if (method7051() == null) {
            return ScriptValueUtils.method6270(var1, 0.0, 0.0, 0.0);
         } else {
            Vec3d var2x = method7051().getVelocity();
            return ScriptValueUtils.method6270(var1, var2x.x, var2x.y, var2x.z);
         }
      });
      var2.method6274("speed", var0x -> {
         if (method7051() == null) {
            return 0.0;
         } else {
            Vec3d var1x = method7051().getVelocity();
            return Math.sqrt(var1x.x * var1x.x + var1x.z * var1x.z);
         }
      });
      var2.method6274("yaw", var0x -> method7051() == null ? 0.0 : method7051().getYaw());
      var2.method6274("pitch", var0x -> method7051() == null ? 0.0 : method7051().getPitch());
      var2.method6274("health", var0x -> method7051() == null ? 0.0 : method7051().getHealth());
      var2.method6274("maxHealth", var0x -> method7051() == null ? 0.0 : method7051().getMaxHealth());
      var2.method6274("absorption", var0x -> method7051() == null ? 0.0 : method7051().getAbsorptionAmount());
      var2.method6274("armor", var0x -> method7051() == null ? 0.0 : method7051().getArmor());
      var2.method6274("hunger", var0x -> method7051() == null ? 0.0 : method7051().getHungerManager().getFoodLevel());
      var2.method6274("saturation", var0x -> method7051() == null ? 0.0 : method7051().getHungerManager().getSaturationLevel());
      var2.method6274("level", var0x -> method7051() == null ? 0.0 : method7051().experienceLevel);
      var2.method6274("onGround", var0x -> method7051() != null && method7051().isOnGround());
      var2.method6274("sneaking", var0x -> method7051() != null && method7051().isSneaking());
      var2.method6274("sprinting", var0x -> method7051() != null && method7051().isSprinting());
      var2.method6274("gliding", var0x -> method7051() != null && method7051().isGliding());
      var2.method6274("swimming", var0x -> method7051() != null && method7051().isSwimming());
      var2.method6274("inWater", var0x -> method7051() != null && method7051().isTouchingWater());
      var2.method6274("inLava", var0x -> method7051() != null && method7051().isInLava());
      var2.method6274("onFire", var0x -> method7051() != null && method7051().isOnFire());
      var2.method6274("usingItem", var0x -> method7051() != null && method7051().isUsingItem());
      var2.method6274("riding", var0x -> method7051() != null && method7051().hasVehicle());
      var2.method6274("creative", var0x -> method7051() != null && method7051().isCreative());
      var2.method6274("flying", var0x -> method7051() != null && method7051().getAbilities().flying);
      var2.method6274("hurtTime", var0x -> method7051() == null ? 0.0 : method7051().hurtTime);
      var2.method6274("fallDistance", var0x -> method7051() == null ? 0.0 : method7051().fallDistance);
      var2.method6274("attackCooldown", var0x -> method7051() == null ? 0.0 : method7051().getAttackCooldownProgress(0.5F));
      var2.method6274("horizontalCollision", var0x -> method7051() != null && method7051().horizontalCollision);
      var2.method6274(
         "moving", var0x -> method7051() != null && (method7051().input.getMovementInput().x != 0.0F || method7051().input.getMovementInput().y != 0.0F)
      );
      var2.method6274("mainHand", var1x -> ScriptEntityApi.method6579(var1, method7051() == null ? ItemStack.EMPTY : method7051().getMainHandStack()));
      var2.method6274("offHand", var1x -> ScriptEntityApi.method6579(var1, method7051() == null ? ItemStack.EMPTY : method7051().getOffHandStack()));
      var2.method6274("armorItems", var1x -> {
         ArrayList var2x = new ArrayList();
         if (method7051() != null) {
            for (EquipmentSlot var6 : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
               var2x.add(ScriptEntityApi.method6579(var1, method7051().getEquippedStack(var6)));
            }
         }

         return ScriptValueUtils.method6267(var1, var2x);
      });
      var2.method6274("effects", var1x -> {
         ArrayList var2x = new ArrayList();
         if (method7051() != null) {
            for (StatusEffectInstance var4 : method7051().getStatusEffects()) {
               var2x.add(ScriptEntityApi.method6578(var1, var4));
            }
         }

         return ScriptValueUtils.method6267(var1, var2x);
      });
      var2.method6274("effect", var1x -> {
         if (method7051() == null) {
            return null;
         } else {
            String var2x = ScriptValueUtils.method6254(var1x, 0, "");

            for (StatusEffectInstance var4 : method7051().getStatusEffects()) {
               if (ScriptEntityApi.matches(var4.getEffectType().getIdAsString(), var2x)) {
                  return ScriptEntityApi.method6578(var1, var4);
               }
            }

            return null;
         }
      });
      var2.method6274("lookingAt", var1x -> ScriptEntityApi.method6581(var1, method7043().crosshairTarget));
      var2.method6274("wrap", var1x -> ScriptEntityApi.method6576(var1, method7051()));
      var2.method6274("slot", var0x -> {
         if (method7051() == null) {
            return -1.0;
         } else {
            if (ScriptValueUtils.method6252(var0x, 0)) {
               int var1x = MathHelper.clamp(ScriptValueUtils.method6257(var0x, 0, 0), 0, 8);
               method7051().getInventory().setSelectedSlot(var1x);
            }

            return (double)method7051().getInventory().getSelectedSlot();
         }
      });
      var2.method6274("attack", var0x -> {
         Entity var1x = method7048(ScriptValueUtils.arg(var0x, 0));
         if (method7051() != null && var1x != null && method7043().interactionManager != null && var1x != method7051()) {
            if (method7051().distanceTo(var1x) > 6.5) {
               return false;
            } else {
               method7043().interactionManager.attackEntity(method7051(), var1x);
               method7051().swingHand(Hand.MAIN_HAND);
               return true;
            }
         } else {
            return false;
         }
      });
      var2.method6274("interact", var0x -> {
         Entity var1x = method7048(ScriptValueUtils.arg(var0x, 0));
         if (method7051() != null && var1x != null && method7043().interactionManager != null) {
            method7043().interactionManager.interactEntity(method7051(), var1x, method7050(var0x, 1));
            return true;
         } else {
            return false;
         }
      });
      var2.method6274("useItem", var0x -> {
         if (method7051() != null && method7043().interactionManager != null) {
            method7043().interactionManager.interactItem(method7051(), method7050(var0x, 0));
            return true;
         } else {
            return false;
         }
      });
      var2.method6274("stopUsing", var0x -> {
         if (method7051() != null && method7043().interactionManager != null) {
            method7043().interactionManager.stopUsingItem(method7051());
            return true;
         } else {
            return false;
         }
      });
      var2.method6274("swing", var0x -> {
         if (method7051() != null) {
            method7051().swingHand(method7050(var0x, 0));
         }

         return null;
      });
      var2.method6274("jump", var0x -> {
         if (method7051() != null && method7051().isOnGround()) {
            method7051().jump();
         }

         return null;
      });
      var2.method6274("setSprinting", var0x -> {
         if (method7051() != null) {
            method7051().setSprinting(ScriptValueUtils.method6259(var0x, 0, true));
         }

         return null;
      });
      var2.method6274(
         "setVelocity",
         var0x -> {
            if (method7051() != null) {
               method7051()
                  .setVelocity(
                     ScriptValueUtils.method6256(var0x, 0, 0.0), ScriptValueUtils.method6256(var0x, 1, 0.0), ScriptValueUtils.method6256(var0x, 2, 0.0)
                  );
            }

            return null;
         }
      );
      var2.method6274(
         "addVelocity",
         var0x -> {
            if (method7051() != null) {
               method7051()
                  .addVelocity(
                     ScriptValueUtils.method6256(var0x, 0, 0.0), ScriptValueUtils.method6256(var0x, 1, 0.0), ScriptValueUtils.method6256(var0x, 2, 0.0)
                  );
            }

            return null;
         }
      );
      var2.method6274("setLook", var0x -> {
         if (method7051() != null) {
            method7051().setYaw((float)ScriptValueUtils.method6256(var0x, 0, method7051().getYaw()));
            method7051().setPitch(MathHelper.clamp((float)ScriptValueUtils.method6256(var0x, 1, method7051().getPitch()), -90.0F, 90.0F));
         }

         return null;
      });
      var2.method6277("inventory", method7052(var1));
      return var2.getScriptableObject();
   }

   private static Hand method7050(Object[] var0, int var1) {
      return "off".equalsIgnoreCase(ScriptValueUtils.method6254(var0, var1, "main")) ? Hand.OFF_HAND : Hand.MAIN_HAND;
   }

   public static ClientPlayerEntity method7051() {
      return method7043().player;
   }

   private static Scriptable method7052(Scriptable var0) {
      ScriptValueUtils.Entry2 var1 = ScriptValueUtils.method6247(var0);
      var1.method6274(
         "slot",
         var1x -> {
            if (method7051() == null) {
               return ScriptEntityApi.method6579(var0, ItemStack.EMPTY);
            } else {
               int var2 = ScriptValueUtils.method6257(var1x, 0, 0);
               return var2 >= 0 && var2 < method7051().getInventory().size()
                  ? ScriptEntityApi.method6579(var0, method7051().getInventory().getStack(var2))
                  : ScriptEntityApi.method6579(var0, ItemStack.EMPTY);
            }
         }
      );
      var1.method6274("size", var0x -> method7051() == null ? 0.0 : method7051().getInventory().size());
      var1.method6274("hotbar", var1x -> {
         ArrayList var2 = new ArrayList();

         for (int var3 = 0; var3 < 9; var3++) {
            var2.add(ScriptEntityApi.method6579(var0, method7051() == null ? ItemStack.EMPTY : method7051().getInventory().getStack(var3)));
         }

         return ScriptValueUtils.method6267(var0, var2);
      });
      var1.method6274("find", var0x -> {
         if (method7051() == null) {
            return -1.0;
         } else {
            String var1x = ScriptValueUtils.method6254(var0x, 0, "");

            for (int var2 = 0; var2 < 36; var2++) {
               ItemStack var3 = method7051().getInventory().getStack(var2);
               if (!var3.isEmpty() && ScriptEntityApi.matches(Registries.ITEM.getId(var3.getItem()).toString(), var1x)) {
                  return (double)var2;
               }
            }

            return -1.0;
         }
      });
      var1.method6274("count", var0x -> {
         if (method7051() == null) {
            return 0.0;
         } else {
            String var1x = ScriptValueUtils.method6254(var0x, 0, "");
            int var2 = 0;

            for (int var3 = 0; var3 < method7051().getInventory().size(); var3++) {
               ItemStack var4 = method7051().getInventory().getStack(var3);
               if (!var4.isEmpty() && ScriptEntityApi.matches(Registries.ITEM.getId(var4.getItem()).toString(), var1x)) {
                  var2 += var4.getCount();
               }
            }

            return (double)var2;
         }
      });
      var1.method6274("screenSlot", var0x -> {
         int var1x = ScriptValueUtils.method6257(var0x, 0, 0);
         if (var1x < 9) {
            return 36.0 + var1x;
         } else if (var1x < 36) {
            return (double)var1x;
         } else {
            return var1x == 40 ? 45.0 : 44.0 - var1x;
         }
      });
      var1.method6274(
         "click",
         var0x -> {
            if (method7051() != null && method7043().interactionManager != null) {
               SlotActionType var1x = method7054(ScriptValueUtils.method6254(var0x, 2, "pickup"));
               method7043()
                  .interactionManager
                  .clickSlot(
                     method7051().currentScreenHandler.syncId,
                     ScriptValueUtils.method6257(var0x, 0, 0),
                     ScriptValueUtils.method6257(var0x, 1, 0),
                     var1x,
                     method7051()
                  );
               return true;
            } else {
               return false;
            }
         }
      );
      var1.method6274("swap", var0x -> {
         if (method7051() != null && method7043().interactionManager != null) {
            int var1x = MathHelper.clamp(ScriptValueUtils.method6257(var0x, 1, 0), 0, 8);
            int var2 = ScriptValueUtils.method6257(var0x, 0, 0);
            if (method7051().currentScreenHandler == method7051().playerScreenHandler && var2 == var1x) {
               return true;
            } else {
               method7043().interactionManager.clickSlot(method7051().currentScreenHandler.syncId, method7053(var2), var1x, SlotActionType.SWAP, method7051());
               return true;
            }
         } else {
            return false;
         }
      });
      var1.method6274(
         "drop",
         var0x -> {
            if (method7051() != null && method7043().interactionManager != null) {
               method7043()
                  .interactionManager
                  .clickSlot(
                     method7051().currentScreenHandler.syncId,
                     method7053(ScriptValueUtils.method6257(var0x, 0, 0)),
                     ScriptValueUtils.method6259(var0x, 1, false) ? 1 : 0,
                     SlotActionType.THROW,
                     method7051()
                  );
               return true;
            } else {
               return false;
            }
         }
      );
      var1.method6274("findBlock", var0x -> {
         if (method7051() == null) {
            return -1.0;
         } else {
            for (int var1x = 0; var1x < 36; var1x++) {
               ItemStack var2 = method7051().getInventory().getStack(var1x);
               if (!var2.isEmpty() && var2.getItem() instanceof BlockItem) {
                  return (double)var1x;
               }
            }

            return -1.0;
         }
      });
      var1.method6274(
         "toHotbar",
         var0x -> {
            if (method7051() != null && method7043().interactionManager != null) {
               int var1x = ScriptValueUtils.method6257(var0x, 0, -1);
               if (var1x < 0 || var1x >= 36) {
                  return -1.0;
               } else if (var1x < 9) {
                  method7051().getInventory().setSelectedSlot(var1x);
                  return (double)var1x;
               } else {
                  int var2 = ScriptValueUtils.method6252(var0x, 1)
                     ? MathHelper.clamp(ScriptValueUtils.method6257(var0x, 1, 0), 0, 8)
                     : method7051().getInventory().getSelectedSlot();
                  method7043()
                     .interactionManager
                     .clickSlot(method7051().currentScreenHandler.syncId, method7053(var1x), var2, SlotActionType.SWAP, method7051());
                  method7051().getInventory().setSelectedSlot(var2);
                  return (double)var2;
               }
            } else {
               return -1.0;
            }
         }
      );
      var1.method6274("close", var0x -> {
         if (method7051() != null) {
            method7051().closeHandledScreen();
         }

         return null;
      });
      var1.method6274("containerOpen", var0x -> method7043().currentScreen instanceof HandledScreen && !(method7043().currentScreen instanceof InventoryScreen));
      var1.method6274("containerTitle", var0x -> method7043().currentScreen == null ? "" : method7043().currentScreen.getTitle().getString());
      var1.method6274("containerSlots", var1x -> {
         ArrayList var2 = new ArrayList();
         if (method7051() != null) {
            ScreenHandler var3 = method7051().currentScreenHandler;

            for (int var4 = 0; var4 < var3.slots.size() && var4 < 128; var4++) {
               Slot var5 = var3.slots.get(var4);
               Scriptable var6 = (Scriptable)ScriptEntityApi.method6579(var0, var5.getStack());
               var6.put("slot", var6, (double)var4);
               var6.put("playerInventory", var6, var5.inventory == method7051().getInventory());
               var2.add(var6);
            }
         }

         return ScriptValueUtils.method6267(var0, var2);
      });
      return var1.getScriptableObject();
   }

   public static int method7053(int var0) {
      if (method7051() == null || method7051().currentScreenHandler != method7051().playerScreenHandler) {
         return var0;
      } else if (var0 < 9) {
         return 36 + var0;
      } else if (var0 < 36) {
         return var0;
      } else if (var0 == 40) {
         return 45;
      } else {
         return var0 < 40 ? 44 - var0 : var0;
      }
   }

   private static SlotActionType method7054(String var0) {
      String var1 = var0.toLowerCase(Locale.ROOT);

      return switch (var1) {
         case "quick_move", "quickmove", "shift" -> SlotActionType.QUICK_MOVE;
         case "swap" -> SlotActionType.SWAP;
         case "clone" -> SlotActionType.CLONE;
         case "throw" -> SlotActionType.THROW;
         case "quick_craft" -> SlotActionType.QUICK_CRAFT;
         case "pickup_all" -> SlotActionType.PICKUP_ALL;
         default -> SlotActionType.PICKUP;
      };
   }

   public static Scriptable method7055(Scriptable var0) {
      ScriptValueUtils.Entry2 var1 = ScriptValueUtils.method6247(var0);
      var1.method6274("exists", var0x -> method7043().world != null);
      var1.method6274("time", var0x -> method7043().world == null ? 0.0 : method7043().world.getTimeOfDay());
      var1.method6274("day", var0x -> method7043().world == null ? 0.0 : method7043().world.getTimeOfDay() / 24000L);
      var1.method6274("dimension", var0x -> method7043().world == null ? "" : method7043().world.getRegistryKey().getValue().toString());
      var1.method6274("raining", var0x -> method7043().world != null && method7043().world.isRaining());
      var1.method6274("thundering", var0x -> method7043().world != null && method7043().world.isThundering());
      var1.method6274("players", var1x -> {
         ArrayList var2 = new ArrayList();
         if (method7043().world != null) {
            for (PlayerEntity var4 : method7043().world.getPlayers()) {
               if (var4 != method7043().player && var2.size() < 512) {
                  var2.add(ScriptEntityApi.method6576(var0, var4));
               }
            }
         }

         return ScriptValueUtils.method6267(var0, var2);
      });
      var1.method6274("entities", var1x -> {
         ArrayList var2 = new ArrayList();
         boolean var3 = !ScriptValueUtils.method6252(var1x, 0) || ScriptValueUtils.method6259(var1x, 0, true);
         double var4 = ScriptValueUtils.method6256(var1x, 1, 128.0);
         if (method7043().world != null && method7051() != null) {
            for (Entity var7 : method7043().world.getEntities()) {
               if (var7 != method7051() && (!var3 || var7 instanceof LivingEntity) && !(var7.squaredDistanceTo(method7051()) > var4 * var4)) {
                  if (var2.size() >= 1024) {
                     break;
                  }

                  var2.add(ScriptEntityApi.method6576(var0, var7));
               }
            }
         }

         return ScriptValueUtils.method6267(var0, var2);
      });
      var1.method6274(
         "entity",
         var1x -> method7043().world == null
            ? null
            : ScriptEntityApi.method6576(var0, method7043().world.getEntityById(ScriptValueUtils.method6257(var1x, 0, -1)))
      );
      var1.method6274(
         "block",
         var1x -> ScriptEntityApi.method6580(
            var0,
            BlockPos.ofFloored(
               ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)
            )
         )
      );
      var1.method6274(
         "blockAt",
         var1x -> ScriptEntityApi.method6580(
            var0,
            BlockPos.ofFloored(
               ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)
            )
         )
      );
      var1.method6274(
         "blockId",
         var0x -> {
            if (method7043().world == null) {
               return "minecraft:air";
            } else {
               BlockPos var1x = BlockPos.ofFloored(
                  ScriptValueUtils.method6256(var0x, 0, 0.0), ScriptValueUtils.method6256(var0x, 1, 0.0), ScriptValueUtils.method6256(var0x, 2, 0.0)
               );
               return Registries.BLOCK.getId(method7043().world.getBlockState(var1x).getBlock()).toString();
            }
         }
      );
      var1.method6274("findBlocks", var1x -> {
         ArrayList var2 = new ArrayList();
         if (method7043().world != null && method7051() != null) {
            String var3 = ScriptValueUtils.method6254(var1x, 0, "");
            int var4 = MathHelper.clamp(ScriptValueUtils.method6257(var1x, 1, 8), 1, 24);
            int var5 = MathHelper.clamp(ScriptValueUtils.method6257(var1x, 2, 256), 1, 2048);
            BlockPos var6 = method7051().getBlockPos();

            for (BlockPos var8 : BlockPos.iterateOutwards(var6, var4, var4, var4)) {
               if (ScriptEntityApi.matches(Registries.BLOCK.getId(method7043().world.getBlockState(var8).getBlock()).toString(), var3)) {
                  var2.add(ScriptValueUtils.method6270(var0, var8.getX(), var8.getY(), var8.getZ()));
                  if (var2.size() >= var5) {
                     break;
                  }
               }
            }

            return ScriptValueUtils.method6267(var0, var2);
         } else {
            return ScriptValueUtils.method6267(var0, var2);
         }
      });
      var1.method6274("raycast", var1x -> {
         if (method7051() == null) {
            return ScriptEntityApi.method6581(var0, null);
         } else {
            float var2 = (float)ScriptValueUtils.method6256(var1x, 0, method7051().getYaw());
            float var3 = (float)ScriptValueUtils.method6256(var1x, 1, method7051().getPitch());
            double var4 = MathHelper.clamp(ScriptValueUtils.method6256(var1x, 2, 4.5), 0.1, 256.0);
            Vec3d var6 = method7051().getEyePos();
            Vec3d var7 = var6.add(Vec3d.fromPolar(var3, var2).multiply(var4));
            return ScriptEntityApi.method6581(var0, ScriptEntityApi.method6582(var6, var7, ScriptValueUtils.method6259(var1x, 3, false)));
         }
      });
      var1.method6274(
         "raycastBetween",
         var1x -> {
            Vec3d var2 = new Vec3d(
               ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)
            );
            Vec3d var3 = new Vec3d(
               ScriptValueUtils.method6256(var1x, 3, 0.0), ScriptValueUtils.method6256(var1x, 4, 0.0), ScriptValueUtils.method6256(var1x, 5, 0.0)
            );
            if (var2.squaredDistanceTo(var3) > 65536.0) {
               throw ScriptValueUtils.method6251("луч длиннее 256 блоков");
            } else {
               return ScriptEntityApi.method6581(var0, ScriptEntityApi.method6582(var2, var3, ScriptValueUtils.method6259(var1x, 6, false)));
            }
         }
      );
      return var1.getScriptableObject();
   }

   public static Scriptable method7056(Script var0, Scriptable var1) {
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6247(var1);
      var2.method6274("press", var1x -> {
         KeyBinding var2x = method7059(ScriptValueUtils.method6254(var1x, 0, ""));
         int var3 = MathHelper.clamp(ScriptValueUtils.method6257(var1x, 1, 1), 1, 1200);
         var2x.setPressed(true);
         if (var3 == 1 && !var2x.isUnbound()) {
            KeyBinding.onKeyPressed(InputUtil.fromTranslationKey(var2x.getBoundKeyTranslationKey()));
         }

         items2.put(var2x, new int[]{var3});
         items3.put(var2x, var0);
         return null;
      });
      var2.method6274("hold", var1x -> {
         KeyBinding var2x = method7059(ScriptValueUtils.method6254(var1x, 0, ""));
         var2x.setPressed(true);
         items2.put(var2x, new int[]{MathHelper.clamp(ScriptValueUtils.method6257(var1x, 1, 1), 1, 1200)});
         items3.put(var2x, var0);
         return null;
      });
      var2.method6274("release", var0x -> {
         KeyBinding var1x = method7059(ScriptValueUtils.method6254(var0x, 0, ""));
         method7057(var1x);
         return null;
      });
      var2.method6274("isDown", var0x -> method7059(ScriptValueUtils.method6254(var0x, 0, "")).isPressed());
      var2.method6274("keyDown", var0x -> InputUtil.isKeyPressed(method7043().getWindow(), ScriptValueUtils.method6257(var0x, 0, -1)));
      var2.method6274("mouseDown", var0x -> GLFW.glfwGetMouseButton(method7043().getWindow().getHandle(), ScriptValueUtils.method6257(var0x, 0, 0)) == 1);
      var2.method6274("keyName", var0x -> {
         int var1x = ScriptValueUtils.method6257(var0x, 0, -1);
         return var1x < 0 ? "" : Type.KEYSYM.createFromCode(var1x).getLocalizedText().getString();
      });
      var0.method1413(() -> {
         for (Entry var2x : new HashMap<>(items3).entrySet()) {
            if (var2x.getValue() == var0) {
               method7057((KeyBinding)var2x.getKey());
            }
         }
      });
      return var2.getScriptableObject();
   }

   private static void method7057(KeyBinding var0) {
      items2.remove(var0);
      items3.remove(var0);
      long var1 = method7043().getWindow().getHandle();
      Key var3 = InputUtil.fromTranslationKey(var0.getBoundKeyTranslationKey());
      boolean var4 = var3.getCategory() == Type.MOUSE
         ? GLFW.glfwGetMouseButton(var1, var3.getCode()) == 1
         : var3.getCode() >= 0 && InputUtil.isKeyPressed(method7043().getWindow(), var3.getCode());
      var0.setPressed(var4);
   }

   public static void method7058() {
      if (!items2.isEmpty()) {
         for (Entry var1 : new HashMap<>(items2).entrySet()) {
            int[] var2 = (int[])var1.getValue();
            var2[0]--;
            if (var2[0] <= 0) {
               method7057((KeyBinding)var1.getKey());
            } else {
               ((KeyBinding)var1.getKey()).setPressed(true);
            }
         }
      }
   }

   private static KeyBinding method7059(String var0) {
      GameOptions var1 = method7043().options;
      String var2 = var0.toLowerCase(Locale.ROOT);

      return switch (var2) {
         case "forward" -> var1.forwardKey;
         case "back" -> var1.backKey;
         case "left" -> var1.leftKey;
         case "right" -> var1.rightKey;
         case "jump" -> var1.jumpKey;
         case "sneak" -> var1.sneakKey;
         case "sprint" -> var1.sprintKey;
         case "attack" -> var1.attackKey;
         case "use" -> var1.useKey;
         case "drop" -> var1.dropKey;
         case "swap" -> var1.swapHandsKey;
         case "inventory" -> var1.inventoryKey;
         case "pick" -> var1.pickItemKey;
         default -> throw ScriptValueUtils.method6251("неизвестная клавиша действия: " + var0);
      };
   }

   public static Scriptable method7060(Script var0, Scriptable var1) {
      ScriptValueUtils.Entry2 var2 = ScriptValueUtils.method6247(var1);
      Module[] var3 = new Module[]{null};
      Supplier var4 = () -> {
         if (var3[0] == null) {
            var3[0] = ScriptModuleApi.method324(var0, "rotation");
         }

         return var3[0];
      };
      BiFunction<org.zenith.Rotation, Scriptable, Object> var5 = (var2x, var3x) -> {
         if (method7051() == null) {
            return null;
         } else {
            float var4x = (float)MathHelper.clamp(ScriptValueUtils.method6264(var3x, "speed", 180.0), 1.0, 180.0);
            float var5x = (float)MathHelper.clamp(ScriptValueUtils.method6264(var3x, "pitchSpeed", var4x), 1.0, 180.0);
            int var6 = (int)MathHelper.clamp(ScriptValueUtils.method6264(var3x, "priority", 3.0), 0.0, 20.0);
            int var7 = (int)MathHelper.clamp(ScriptValueUtils.method6264(var3x, "ticks", 1.0), 1.0, 100.0);
            Rotation var8 = new Rotation(var2x.getFloatValue(), MathHelper.clamp(var2x.getFloatValue2(), -90.0F, 90.0F));
            if (var8.method3137()) {
               return null;
            } else {
               RotationManager var9 = Zenith.getInstance().getRotationManager();
               var9.method7848(
                  new RotationRequest(var8, () -> var9.method7844().method3136(var8, var4x, var5x), var9.getBcCameraTestnerv().getRotationConfig()),
                  var6,
                  (Module)var4.get(),
                  var7
               );
               return ScriptValueUtils.method6247(var1)
                  .method6277("yaw", (double)var8.getFloatValue())
                  .method6277("pitch", (double)var8.getFloatValue2())
                  .getScriptableObject();
            }
         }
      };
      var2.method6274(
         "set",
         var1x -> var5.apply(
            new Rotation((float)ScriptValueUtils.method6256(var1x, 0, 0.0), (float)ScriptValueUtils.method6256(var1x, 1, 0.0)),
            ScriptValueUtils.method6262(var1x, 2)
         )
      );
      var2.method6274(
         "lookAt",
         var1x -> {
            if (method7051() == null) {
               return null;
            } else {
               Rotation var2x = Rotation.method3128(
                  new Vec3d(ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)),
                  method7051().getEyePos()
               );
               return var5.apply(var2x, ScriptValueUtils.method6262(var1x, 3));
            }
         }
      );
      var2.method6274(
         "angleTo",
         var1x -> {
            if (method7051() == null) {
               return ScriptValueUtils.method6247(var1).method6277("yaw", 0.0).method6277("pitch", 0.0).getScriptableObject();
            } else {
               Rotation var2x = Rotation.method3128(
                  new Vec3d(ScriptValueUtils.method6256(var1x, 0, 0.0), ScriptValueUtils.method6256(var1x, 1, 0.0), ScriptValueUtils.method6256(var1x, 2, 0.0)),
                  method7051().getEyePos()
               );
               return ScriptValueUtils.method6247(var1)
                  .method6277("yaw", (double)var2x.getFloatValue())
                  .method6277("pitch", (double)var2x.getFloatValue2())
                  .getScriptableObject();
            }
         }
      );
      var2.method6274(
         "server",
         var1x -> {
            if (method7051() == null) {
               return ScriptValueUtils.method6247(var1).method6277("yaw", 0.0).method6277("pitch", 0.0).getScriptableObject();
            } else {
               Rotation var2x = Zenith.getInstance().getRotationManager().method7844();
               return ScriptValueUtils.method6247(var1)
                  .method6277("yaw", (double)var2x.getFloatValue())
                  .method6277("pitch", (double)var2x.getFloatValue2())
                  .getScriptableObject();
            }
         }
      );
      var2.method6274(
         "camera",
         var1x -> method7051() == null
            ? ScriptValueUtils.method6247(var1).method6277("yaw", 0.0).method6277("pitch", 0.0).getScriptableObject()
            : ScriptValueUtils.method6247(var1)
               .method6277("yaw", (double)method7051().getYaw())
               .method6277("pitch", (double)method7051().getPitch())
               .getScriptableObject()
      );
      return var2.getScriptableObject();
   }

   public static Scriptable method7061(Script var0, Storage var1, Scriptable var2) {
      ScriptValueUtils.Entry2 var3 = ScriptValueUtils.method6247(var2);
      var3.method6274("msg", var1x -> {
         if (method7051() != null) {
            method7051().sendMessage(Text.literal("§7[§b" + var0.method1403() + "§7]§r " + ScriptValueUtils.method6254(var1x, 0, "")), false);
         }

         return null;
      });
      var3.method6274("info", var1x -> method7063(var0, "i", ScriptValueUtils.method6254(var1x, 0, "")));
      var3.method6274("success", var1x -> method7063(var0, "b", ScriptValueUtils.method6254(var1x, 0, "")));
      var3.method6274("warn", var1x -> method7063(var0, "d", ScriptValueUtils.method6254(var1x, 0, "")));
      var3.method6274("error", var1x -> method7063(var0, "d", ScriptValueUtils.method6254(var1x, 0, "")));
      var3.method6274("say", var1x -> {
         ScriptPermissions.method4(var0, "chat");
         if (method7043().getNetworkHandler() == null) {
            return false;
         } else {
            String var2x = ScriptValueUtils.method6254(var1x, 0, "");
            if (!var2x.isBlank() && var2x.length() <= 256) {
               method7062(var0);
               if (var2x.startsWith("/")) {
                  method7043().getNetworkHandler().sendChatCommand(var2x.substring(1));
               } else {
                  method7043().getNetworkHandler().sendChatMessage(var2x);
               }

               return true;
            } else {
               return false;
            }
         }
      });
      var3.method6274("command", var1x -> {
         ScriptPermissions.method4(var0, "chat");
         if (method7043().getNetworkHandler() == null) {
            return false;
         } else {
            String var2x = ScriptValueUtils.method6254(var1x, 0, "");
            if (var2x.startsWith("/")) {
               var2x = var2x.substring(1);
            }

            if (!var2x.isBlank() && var2x.length() <= 256) {
               method7062(var0);
               method7043().getNetworkHandler().sendChatCommand(var2x);
               return true;
            } else {
               return false;
            }
         }
      });
      var3.method6274("username", var0x -> method7043().getSession().getUsername());
      var3.method6274("server", var0x -> method7043().getCurrentServerEntry() == null ? "" : method7043().getCurrentServerEntry().address);
      var3.method6274("fps", var0x -> (double)method7043().getCurrentFps());
      var3.method6274("ping", var0x -> {
         if (method7051() != null && method7043().getNetworkHandler() != null) {
            PlayerListEntry var1x = method7043().getNetworkHandler().getPlayerListEntry(method7051().getUuid());
            return var1x == null ? 0.0 : var1x.getLatency();
         } else {
            return 0.0;
         }
      });
      var3.method6274("inGame", var0x -> method7043().world != null && method7051() != null);
      var3.method6274("screen", var0x -> method7047(method7043().currentScreen));
      var3.method6274("screenOpen", var0x -> method7043().currentScreen != null);
      var3.method6274("width", var0x -> (double)method7043().getWindow().getScaledWidth());
      var3.method6274("height", var0x -> (double)method7043().getWindow().getScaledHeight());
      var3.method6274("isFriend", var0x -> Zenith.getInstance().getFriends().method7627(ScriptValueUtils.method6254(var0x, 0, "")));
      var3.method6274("setClipboard", var1x -> {
         ScriptPermissions.method4(var0, "clipboard");
         method7043().keyboard.setClipboard(ScriptValueUtils.method6254(var1x, 0, ""));
         return null;
      });
      var3.method6274("modules", var1x -> {
         ArrayList var2x = new ArrayList();

         for (Module var4 : Zenith.getInstance().getModuleManager().getItems()) {
            if (!var4.isHidden()) {
               var2x.add(var4.getName());
            }
         }

         return ScriptValueUtils.method6267(var2, var2x);
      });
      var3.method6274("module", var2x -> {
         Module var3x = Zenith.getInstance().getModuleManager().method2484(ScriptValueUtils.method6254(var2x, 0, ""));
         if (var3x == null) {
            return null;
         } else {
            boolean var4 = var3x instanceof Module1 var5 && var5.getScript() != var0;
            return method7064(var0, var2, var3x, var4);
         }
      });
      return var3.getScriptableObject();
   }

   public static void method7062(Script var0) {
      long var1 = System.currentTimeMillis();
      long[] var3 = items4.computeIfAbsent(var0, var2 -> new long[]{var1, 0L});
      if (var1 - var3[0] > 3000L) {
         var3[0] = var1;
         var3[1] = 0L;
      }

      if (++var3[1] > 6L) {
         throw ScriptValueUtils.method6251("слишком много сообщений на сервер: не больше 6 за 3 секунды");
      }
   }

   private static Object method7063(Script var0, String var1, String var2) {
      HudHolder var3 = Zenith.getInstance().getHudHolder();
      if (var3 != null) {
         var3.method6972(var1, Text.literal(var0.method1403() + ": " + var2));
      }

      return null;
   }

   public static Scriptable method7064(Script var0, Scriptable var1, Module var2, boolean var3) {
      ScriptValueUtils.Entry2 var4 = ScriptValueUtils.method6247(var1);
      var4.method6277("name", var2.getName());
      var4.method6277("category", var2.getCategory().getName());
      Runnable var5 = () -> {
         if (var3) {
            throw ScriptValueUtils.method6251("модулями другого скрипта управлять нельзя");
         } else {
            if (!(var2 instanceof Module1)) {
               ScriptPermissions.method4(var0, "modules");
            }
         }
      };
      var4.method6274("enabled", var1x -> var2.isEnabled());
      var4.method6274("toggle", var2x -> {
         var5.run();
         var2.toggle();
         return var2.isEnabled();
      });
      var4.method6274("setEnabled", var2x -> {
         var5.run();
         var2.setToggled(ScriptValueUtils.method6259(var2x, 0, true));
         return null;
      });
      var4.method6274("key", var1x -> (double)var2.getKeyCode());
      var4.method6274("settings", var2x -> {
         ArrayList var3x = new ArrayList();

         for (Setting var5x : var2.getSettings()) {
            var3x.add(var5x.getKey());
         }

         return ScriptValueUtils.method6267(var1, var3x);
      });
      var4.method6274("setting", var3x -> {
         String var4x = ScriptValueUtils.method6254(var3x, 0, "");

         for (Setting var6 : var2.getSettings()) {
            if (var6.getKey().equalsIgnoreCase(var4x) || var6.getName().equalsIgnoreCase(var4x)) {
               var5.run();
               return ScriptSettingsApi.method1223(var1, var6);
            }
         }

         return null;
      });
      return var4.getScriptableObject();
   }

   public static Scriptable method7065(Scriptable var0) {
      ScriptValueUtils.Entry2 var1 = ScriptValueUtils.method6247(var0);
      var1.method6274("fps", var0x -> (double)method7043().getCurrentFps());
      var1.method6274("paused", var0x -> method7043().isPaused());
      var1.method6274("width", var0x -> (double)method7043().getWindow().getScaledWidth());
      var1.method6274("height", var0x -> (double)method7043().getWindow().getScaledHeight());
      var1.method6274("windowWidth", var0x -> (double)method7043().getWindow().getWidth());
      var1.method6274("windowHeight", var0x -> (double)method7043().getWindow().getHeight());
      var1.method6274("fov", var0x -> (double)method7043().options.getFov().getValue().intValue());
      var1.method6274("sensitivity", var0x -> method7043().options.getMouseSensitivity().getValue());
      var1.method6274("renderDistance", var0x -> (double)method7043().options.getViewDistance().getValue().intValue());
      var1.method6274("time", var0x -> (double)System.currentTimeMillis());
      return var1.getScriptableObject();
   }
}

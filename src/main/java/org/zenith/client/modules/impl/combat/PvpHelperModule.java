package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot.Type;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.PacketEvent;
import org.zenith.StopWatch;
import org.zenith.Zenith;
import org.zenith.ZenithColor;
import org.zenith.base.Module;
import org.zenith.client.events.AttackEvent;
import org.zenith.client.events.DrawEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;
import org.zenith.client.modules.impl.render.InterfaceModule;
import org.zenith.client.settings.BooleanSetting;
import org.zenith.utility.game.PotionUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;
import org.zenith.utility.render.display.base.ProxyDotRadiusData;

@ModuleInfo(
   name = "PvpHelper",
   category = ModuleCategory.COMBAT,
   description = "module.pvphelper.description"
)
public final class PvpHelperModule extends Module {
   public static final PvpHelperModule INSTANCE = new PvpHelperModule();
   private static final int CONST_intValue = 32;
   private final BooleanSetting healHelper = new BooleanSetting("module.pvpHelper.healHelper", "module.pvpHelper.healHelper.desc", true);
   private final BooleanSetting reduceCooldown = new BooleanSetting("module.pvpHelper.reduceCooldown", "module.pvpHelper.reduceCooldown.desc", false);
   private final BooleanSetting autoChorus = new BooleanSetting("module.pvpHelper.autoChorus", "module.pvpHelper.autoChorus.desc", false);
   private final BooleanSetting warnArmor = new BooleanSetting("module.pvpHelper.warnArmor", "module.pvpHelper.warnArmor.desc", true);
   private final Set<Integer> items = new HashSet<>();
   private long longValue;
   private boolean flag;
   private int intValue;
   private volatile long longValue2;
   private final StopWatch stopWatch = new StopWatch();
   private final StopWatch stopWatch2 = new StopWatch();
   private volatile int intValue2 = -1;
   private boolean flag2;
   private boolean flag3;
   private int intValue3 = -1;
   private int intValue4 = -1;
   private boolean flag4 = true;

   private PvpHelperModule() {
   }

   @Override
   public void onEnable() {
      this.items.clear();
      this.longValue = 0L;
      this.flag = false;
      this.longValue2 = 0L;
      this.flag4 = true;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.method4651();
      this.items.clear();
      super.onDisable();
   }

   public int method4641(Identifier var1, int var2) {
      if (this.isEnabled() && this.reduceCooldown.isEnabled() && var2 > 0) {
         Item var3 = Registries.ITEM.get(var1);
         return var3 != Items.ENCHANTED_GOLDEN_APPLE && var3 != Items.GOLDEN_APPLE && var3 != Items.POTION && var3 != Items.CHORUS_FRUIT
            ? var2
            : Math.max(1, var2 - 32);
      } else {
         return var2;
      }
   }

   @EventTarget
   public void onUpdate(UpdateEvent var1) {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         this.method4642();
         this.method4643();
         this.method4649();
         this.method4646();
      }
   }

   private void method4642() {
      ItemStack var1 = mc.player.getActiveItem();
      if (mc.player.isUsingItem() && PotionUtils.method2324(var1) && PotionUtils.method2322(var1, StatusEffects.INSTANT_HEALTH)) {
         this.flag = true;
         this.intValue = mc.player.getItemUseTimeLeft();
      } else if (this.flag) {
         if (this.intValue <= 2) {
            this.longValue = System.currentTimeMillis();
         }

         this.flag = false;
      }
   }

   private void method4643() {
      this.items.clear();
      if (this.healHelper.isEnabled() && Zenith.getInstance().getServerDetector().method1635()) {
         if (!mc.player.hasStatusEffect(StatusEffects.STRENGTH)) {
            this.method4644(var0 -> PotionUtils.method2324(var0) && PotionUtils.method2322(var0, StatusEffects.STRENGTH));
         }

         if (!(mc.player.getHealth() + mc.player.getAbsorptionAmount() > 19.0F)) {
            long var1 = Zenith.getInstance().getServerDetector().method1642() ? 10000L : 20000L;
            if (System.currentTimeMillis() - this.longValue >= var1) {
               this.method4644(var0 -> PotionUtils.method2324(var0) && PotionUtils.method2322(var0, StatusEffects.INSTANT_HEALTH));
            } else if (mc.player.getHungerManager().getFoodLevel() < 20) {
               this.method4644(var0 -> var0.isOf(Items.GOLDEN_CARROT));
            } else if (!mc.player.getItemCooldownManager().isCoolingDown(Items.GOLDEN_APPLE.getDefaultStack())) {
               this.method4644(var0 -> var0.isOf(Items.GOLDEN_APPLE));
            } else {
               this.method4644(var0 -> var0.isOf(Items.ENCHANTED_GOLDEN_APPLE));
            }
         }
      }
   }

   private void method4644(Predicate<ItemStack> var1) {
      int var2 = PotionUtils.method2326(var1);
      if (var2 != -1) {
         this.items.add(var2);
      }
   }

   public boolean method4645(int var1) {
      return this.isEnabled() && this.items.contains(var1);
   }

   private void method4646() {
      if (this.warnArmor.isEnabled()) {
         float var1 = 1.0F;

         for (EquipmentSlot var3 : EquipmentSlot.VALUES) {
            if (var3.getType() == Type.HUMANOID_ARMOR) {
               ItemStack var4 = mc.player.getEquippedStack(var3);
               if (!var4.isEmpty() && var4.isDamageable()) {
                  float var5 = var4.getMaxDamage();
                  var1 = Math.min(var1, (var5 - var4.getDamage()) / var5);
               }
            }
         }

         if (var1 < 0.36F) {
            if (this.flag4) {
               Zenith.getInstance().getHudHolder().method6972(this.getCategory().getIcon(), Text.of("Броня почти сломана!"));
               this.flag4 = false;
            }
         } else {
            this.flag4 = true;
         }
      }
   }

   @EventTarget
   public void method4647(DrawEvent var1) {
      if (!this.items.isEmpty() && mc.player != null && !mc.options.hudHidden) {
         if (!InterfaceModule.INSTANCE.isEnabled() || !InterfaceModule.INSTANCE.method6812()) {
            CustomDrawContext var2 = var1.getCustomDrawContext();
            int var3 = var2.getScaledWindowWidth() / 2 - 91;
            int var4 = var2.getScaledWindowHeight() - 22;
            ZenithColor var5 = Zenith.getInstance().getStyleManager().getCurrentStyle().getPrimaryColor().getZenithColor().method7294(0.45F);

            for (int var7 : this.items) {
               var2.drawRoundedRect((float)(var3 + 1 + var7 * 20), (float)(var4 + 1), 20.0F, 20.0F, ProxyDotRadiusData.method8069(3.0F), var5);
            }
         }
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.method7100()) {
         if (this.flag2 && var1.getPacket3() instanceof EntityS2CPacket var2) {
            mc.execute(() -> {
               if (mc.world != null && var2.getEntity(mc.world) != null && var2.getEntity(mc.world).getId() == this.intValue2) {
                  this.stopWatch2.reset();
               }
            });
         }
      }
   }

   @EventTarget
   public void onMoveInput(MoveInputEvent var1) {
   }

   @EventTarget
   public void method4648(AttackEvent var1) {
      if (this.autoChorus.isEnabled() && this.flag2) {
         var1.cancel();
      }
   }

   private void method4649() {
      if (!this.autoChorus.isEnabled()) {
         this.method4651();
         this.stopWatch.reset();
      } else {
         LivingEntity var1 = AuraModule.INSTANCE.method6358();
         if (var1 == null) {
            this.stopWatch.reset();
            if (this.flag2 && !mc.player.isUsingItem()) {
               this.method4651();
            }
         } else if (!var1.isAlive()) {
            this.stopWatch.reset();
         } else {
            this.intValue2 = var1.getId();
            boolean var2 = var1.isUsingItem() && var1.getActiveItem().isOf(Items.CHORUS_FRUIT);
            if (!var2 || mc.player.squaredDistanceTo(var1) > 25.0) {
               this.stopWatch.reset();
            } else if (!this.flag2 && mc.currentScreen == null && !mc.player.isUsingItem() && this.stopWatch.method7546(200L)) {
               this.method4650();
            }

            if (this.flag2) {
               if (mc.player.isUsingItem()) {
                  this.flag3 = true;
               } else if (this.flag3) {
                  this.method4651();
                  return;
               }

               if (this.stopWatch2.method7546(2500L)) {
                  this.method4651();
               }
            }
         }
      }
   }

   private void method4650() {
      if (mc.player.currentScreenHandler instanceof PlayerScreenHandler) {
         int var1 = PotionUtils.method2325(var0 -> var0.isOf(Items.CHORUS_FRUIT));
         if (var1 != -1) {
            int var2 = mc.player.getInventory().getSelectedSlot();
            this.intValue3 = var2;
            this.intValue4 = -1;
            if (var1 < 9) {
               PotionUtils.method2329(var1);
            } else {
               this.intValue4 = var1;
               PotionUtils.method2328(var1, var2);
            }

            mc.options.useKey.setPressed(true);
            this.flag2 = true;
            this.flag3 = false;
            this.stopWatch2.reset();
         }
      }
   }

   private void method4651() {
      if (this.flag2) {
         mc.options.useKey.setPressed(false);
         if (mc.player != null && mc.interactionManager != null) {
            if (this.intValue4 != -1 && this.intValue3 != -1) {
               PotionUtils.method2328(this.intValue4, this.intValue3);
            }

            if (this.intValue3 != -1) {
               PotionUtils.method2329(this.intValue3);
            }
         }

         this.stopWatch.reset();
         this.stopWatch2.reset();
      }

      this.flag2 = false;
      this.flag3 = false;
      this.intValue3 = -1;
      this.intValue4 = -1;
   }
}

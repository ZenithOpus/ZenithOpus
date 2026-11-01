package org.zenith.client.modules.impl.combat;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Hand;
import org.zenith.ModuleCategory;
import org.zenith.ModuleInfo;
import org.zenith.Rotation;
import org.zenith.base.Module;
import org.zenith.client.events.Event5;
import org.zenith.client.events.HandleInputEvent;
import org.zenith.client.settings.MultiSelectSetting;
import org.zenith.client.settings.SliderSetting;
import org.zenith.utility.game.PotionUtils;
import org.zenith.utility.misc.RotationRequest;
import org.zenith.utility.network.UseItemUtils;

@ModuleInfo(
   name = "AutoPotion",
   category = ModuleCategory.COMBAT,
   description = "module.autopotion.description"
)
public final class AutoPotionModule extends Module {
   public static final AutoPotionModule INSTANCE = new AutoPotionModule();
   private static final int CONST_intValue = 20;
   private final MultiSelectSetting potions = new MultiSelectSetting("module.autoPotion.potions", "module.autoPotion.potions.desc");
   private final MultiSelectSetting.Entry entry = new MultiSelectSetting.Entry(this.potions, "module.autoPotion.potions.strength", true);
   private final MultiSelectSetting.Entry entry2 = new MultiSelectSetting.Entry(this.potions, "module.autoPotion.potions.speed", true);
   private final MultiSelectSetting.Entry entry3 = new MultiSelectSetting.Entry(this.potions, "module.autoPotion.potions.fireResistance", true);
   private final MultiSelectSetting.Entry entry4 = new MultiSelectSetting.Entry(this.potions, "module.autoPotion.potions.heal", false);
   private final SliderSetting healHealth = new SliderSetting(
      "module.autoPotion.healHealth", 10.0F, 1.0F, 20.0F, 0.5F, "module.autoPotion.healHealth.desc", "hp", this.entry4::isEnabled
   );
   private int intValue;
   private int intValue2;

   private AutoPotionModule() {
   }

   @Override
   public void onEnable() {
      this.intValue = 20;
      this.intValue2 = 0;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.intValue2 = 0;
      super.onDisable();
   }

   @EventTarget
   public void method510(Event5 var1) {
      if (this.method512() && this.intValue >= 20 && !this.method513().isEmpty()) {
         Rotation var2 = new Rotation(mc.player.getYaw(), 90.0F);
         rotationManager.method7849(new RotationRequest(var2, () -> var2, bcCameraTestnerv.getRotationConfig()), 8, this);
      }
   }

   @EventTarget
   public void method511(HandleInputEvent var1) {
      if (!this.method512()) {
         this.intValue2 = 0;
      } else {
         this.intValue++;
         if (this.intValue >= 20) {
            List<Integer> var2 = this.method513();
            if (var2.isEmpty()) {
               this.intValue2 = 0;
            } else {
               Rotation var3 = rotationManager.getCurrentRotation();
               Rotation var4 = rotationManager.getRotation2();
               if (var3 != null && !(var3.getFloatValue2() < 89.0F) && var4 != null && !(var4.getFloatValue2() < 89.0F)) {
                  if (this.intValue2++ >= 1) {
                     int var5 = mc.player.getInventory().getSelectedSlot();
                     boolean var6 = false;

                     for (Object var8_o : (Iterable<?>)(Object)(var2)) {
                        int var8 = (Integer)var8_o;
                        if (PotionUtils.method2323(mc.player.getInventory().getStack(var8))) {
                           if (var8 < 9) {
                              PotionUtils.method2329(var8);
                              UseItemUtils.useItem(Hand.MAIN_HAND);
                           } else {
                              PotionUtils.method2329(var5);
                              PotionUtils.method2328(var8, var5);
                              UseItemUtils.useItem(Hand.MAIN_HAND);
                              PotionUtils.method2328(var8, var5);
                           }

                           var6 = true;
                        }
                     }

                     PotionUtils.method2329(var5);
                     this.intValue2 = 0;
                     if (var6) {
                        this.intValue = 0;
                     }
                  }
               } else {
                  this.intValue2 = 0;
               }
            }
         }
      }
   }

   private boolean method512() {
      return mc.player != null
         && mc.world != null
         && mc.interactionManager != null
         && mc.getNetworkHandler() != null
         && !mc.player.isGliding()
         && !mc.player.isUsingItem()
         && mc.player.currentScreenHandler instanceof PlayerScreenHandler;
   }

   private List<Integer> method513() {
      ArrayList<Integer> var1 = new ArrayList<>();
      this.method514(var1, this.entry, StatusEffects.STRENGTH);
      this.method514(var1, this.entry2, StatusEffects.SPEED);
      this.method514(var1, this.entry3, StatusEffects.FIRE_RESISTANCE);
      if (this.entry4.isEnabled() && mc.player.getHealth() + mc.player.getAbsorptionAmount() <= this.healHealth.getValue3()) {
         int var2 = PotionUtils.method2325(var0 -> PotionUtils.method2323(var0) && PotionUtils.method2322(var0, StatusEffects.INSTANT_HEALTH));
         if (var2 != -1 && !var1.contains(var2)) {
            var1.add(var2);
         }
      }

      return var1;
   }

   private void method514(List<Integer> var1, MultiSelectSetting.Entry var2, RegistryEntry<StatusEffect> var3) {
      if (var2.isEnabled() && !mc.player.hasStatusEffect(var3)) {
         int var4 = PotionUtils.method2325(var1x -> PotionUtils.method2323(var1x) && PotionUtils.method2322(var1x, var3));
         if (var4 != -1 && !var1.contains(var4)) {
            var1.add(var4);
         }
      }
   }
}

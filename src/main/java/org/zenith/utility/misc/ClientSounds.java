package org.zenith.utility.misc;

import lombok.Generated;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.zenith.MinecraftAccess;
import org.zenith.client.modules.impl.render.InterfaceModule;

public class ClientSounds implements MinecraftAccess {
   public final SoundEvent soundEvent = SoundEvent.of(Identifier.of("zenith:gui_open"));
   public final SoundEvent soundEvent2 = SoundEvent.of(Identifier.of("zenith:gui_close"));
   public final SoundEvent soundEvent3 = SoundEvent.of(Identifier.of("zenith:module_enable"));
   public final SoundEvent soundEvent4 = SoundEvent.of(Identifier.of("zenith:module_disable"));
   public final SoundEvent soundEvent5 = SoundEvent.of(Identifier.of("zenith:enable4"));
   public final SoundEvent soundEvent6 = SoundEvent.of(Identifier.of("zenith:disable4"));
   public final SoundEvent soundEvent7 = SoundEvent.of(Identifier.of("zenith:click_left"));
   public final SoundEvent soundEvent8 = SoundEvent.of(Identifier.of("zenith:click_right"));
   public final SoundEvent soundEvent9 = SoundEvent.of(Identifier.of("zenith:slider_step"));
   public final SoundEvent soundEvent10 = SoundEvent.of(Identifier.of("zenith:category_switch"));
   public final SoundEvent soundEvent11 = SoundEvent.of(Identifier.of("zenith:bind_set"));
   public final SoundEvent soundEvent12 = SoundEvent.of(Identifier.of("zenith:bind_clear"));
   public final SoundEvent soundEvent13 = SoundEvent.of(Identifier.of("zenith:input_confirm"));
   public final SoundEvent soundEvent14 = SoundEvent.of(Identifier.of("zenith:input_cancel"));

   public ClientSounds() {
      Registry.register(Registries.SOUND_EVENT, this.soundEvent.id(), this.soundEvent);
      Registry.register(Registries.SOUND_EVENT, this.soundEvent2.id(), this.soundEvent2);
      Registry.register(Registries.SOUND_EVENT, this.soundEvent3.id(), this.soundEvent3);
      Registry.register(Registries.SOUND_EVENT, this.soundEvent4.id(), this.soundEvent4);
      Registry.register(Registries.SOUND_EVENT, this.soundEvent5.id(), this.soundEvent5);
      Registry.register(Registries.SOUND_EVENT, this.soundEvent6.id(), this.soundEvent6);
      Registry.register(Registries.SOUND_EVENT, this.soundEvent7.id(), this.soundEvent7);
      Registry.register(Registries.SOUND_EVENT, this.soundEvent8.id(), this.soundEvent8);
      Registry.register(Registries.SOUND_EVENT, this.soundEvent9.id(), this.soundEvent9);
      Registry.register(Registries.SOUND_EVENT, this.soundEvent10.id(), this.soundEvent10);
      Registry.register(Registries.SOUND_EVENT, this.soundEvent11.id(), this.soundEvent11);
      Registry.register(Registries.SOUND_EVENT, this.soundEvent12.id(), this.soundEvent12);
      Registry.register(Registries.SOUND_EVENT, this.soundEvent13.id(), this.soundEvent13);
      Registry.register(Registries.SOUND_EVENT, this.soundEvent14.id(), this.soundEvent14);
   }

   public void method1561(SoundEvent var1) {
      this.method1576(var1, InterfaceModule.INSTANCE.volume.getValue3(), 1.0F);
   }

   public void method1562() {
      float var1 = InterfaceModule.INSTANCE.volume.getValue3();
      this.method1576(this.soundEvent7, var1 * 0.75F, 0.9F);
      this.method1576(this.soundEvent5, var1 * 0.45F, 1.5F);
   }

   public void method1563() {
      float var1 = InterfaceModule.INSTANCE.volume.getValue3();
      this.method1576(this.soundEvent8, var1 * 0.7F, 1.05F);
      this.method1576(this.soundEvent6, var1 * 0.4F, 0.85F);
   }

   public void method1564(int var1, int var2) {
      float var3 = var2 > 1 ? (float)var1 / (var2 - 1) : 0.0F;
      this.method1576(this.soundEvent10, InterfaceModule.INSTANCE.volume.getValue3() * 0.6F, 0.94F + var3 * 0.2F);
   }

   public void method1565(boolean var1) {
      this.method1576(var1 ? this.soundEvent3 : this.soundEvent4, InterfaceModule.INSTANCE.volume.getValue3(), 1.0F);
   }

   public void method1566() {
      this.method1576(this.soundEvent11, InterfaceModule.INSTANCE.volume.getValue3() * 0.7F, 1.0F);
   }

   public void method1567() {
      this.method1576(this.soundEvent12, InterfaceModule.INSTANCE.volume.getValue3() * 0.65F, 1.0F);
   }

   public void method1568() {
      this.method1576(this.soundEvent13, InterfaceModule.INSTANCE.volume.getValue3() * 0.6F, 1.0F);
   }

   public void method1569() {
      this.method1576(this.soundEvent14, InterfaceModule.INSTANCE.volume.getValue3() * 0.55F, 1.0F);
   }

   public void method1570(boolean var1) {
      this.method1576(var1 ? this.soundEvent8 : this.soundEvent7, InterfaceModule.INSTANCE.volume.getValue3() * 0.8F, 1.0F);
   }

   public void method1571(float var1) {
      this.method1576(this.soundEvent9, InterfaceModule.INSTANCE.volume.getValue3() * 0.5F, 0.92F + MathHelper.clamp(var1, 0.0F, 1.0F) * 0.28F);
   }

   public void method1572() {
      float var1 = InterfaceModule.INSTANCE.volume.getValue3();
      this.method1576(this.soundEvent7, var1 * 0.55F, 1.25F);
      this.method1576(this.soundEvent5, var1 * 0.35F, 1.6F);
   }

   public void method1573() {
      float var1 = InterfaceModule.INSTANCE.volume.getValue3();
      this.method1576(this.soundEvent5, var1 * 0.5F, 0.95F);
      this.method1576(this.soundEvent7, var1 * 0.3F, 0.8F);
   }

   public void method1574() {
      float var1 = InterfaceModule.INSTANCE.volume.getValue3();
      this.method1576(this.soundEvent7, var1 * 0.6F, 1.1F);
      this.method1576(this.soundEvent9, var1 * 0.35F, 1.35F);
   }

   public void method1575() {
      this.method1576(this.soundEvent9, InterfaceModule.INSTANCE.volume.getValue3() * 0.3F, 1.45F);
   }

   public void method1576(SoundEvent var1, float var2, float var3) {
      mc.getSoundManager().play(PositionedSoundInstance.ui(var1, var3, var2));
   }

   @Generated
   public SoundEvent getSoundEvent() {
      return this.soundEvent;
   }

   @Generated
   public SoundEvent getSoundEvent2() {
      return this.soundEvent2;
   }

   @Generated
   public SoundEvent getSoundEvent3() {
      return this.soundEvent3;
   }

   @Generated
   public SoundEvent getSoundEvent4() {
      return this.soundEvent4;
   }

   @Generated
   public SoundEvent getSoundEvent5() {
      return this.soundEvent5;
   }

   @Generated
   public SoundEvent getSoundEvent6() {
      return this.soundEvent6;
   }

   @Generated
   public SoundEvent getSoundEvent7() {
      return this.soundEvent7;
   }

   @Generated
   public SoundEvent getSoundEvent8() {
      return this.soundEvent8;
   }

   @Generated
   public SoundEvent getSoundEvent9() {
      return this.soundEvent9;
   }

   @Generated
   public SoundEvent getSoundEvent10() {
      return this.soundEvent10;
   }

   @Generated
   public SoundEvent getSoundEvent11() {
      return this.soundEvent11;
   }

   @Generated
   public SoundEvent getSoundEvent12() {
      return this.soundEvent12;
   }

   @Generated
   public SoundEvent getSoundEvent13() {
      return this.soundEvent13;
   }

   @Generated
   public SoundEvent getSoundEvent14() {
      return this.soundEvent14;
   }
}

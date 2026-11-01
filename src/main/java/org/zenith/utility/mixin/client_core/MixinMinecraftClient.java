package org.zenith.utility.mixin.client_core;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter.Dynamic;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.ActionResult.SwingSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.Zenith;
import org.zenith.base.font.HintedText;
import org.zenith.client.events.Event7;
import org.zenith.client.events.Event9;
import org.zenith.client.events.HandleInputEvent;
import org.zenith.client.events.HandleInputPostEvent;
import org.zenith.client.events.PacketProcessEndEvent;
import org.zenith.client.events.PacketProcessStartEvent;
import org.zenith.client.events.SetScreenEvent;
import org.zenith.client.events.StopUsingItemEvent;
import org.zenith.client.events.TickEvent;
import org.zenith.client.modules.impl.combat.CrystalAuraModule;
import org.zenith.client.modules.impl.misc.NoInteractModule;
import org.zenith.client.modules.impl.player.AutoMineModule;
import org.zenith.utility.misc.Util13;
import org.zenith.utility.render.ImmediateRenderer;
import org.zenith.utility.render.ProjectionUtils;
import org.zenith.utility.render.UniformStorage;

@Mixin({MinecraftClient.class})
public abstract class MixinMinecraftClient {
   @Shadow
   @Final
   private Window window;
   @Shadow
   @Nullable
   public ClientPlayerEntity player;
   @Shadow
   @Nullable
   public ClientWorld world;
   @Shadow
   @Nullable
   public ClientPlayerInteractionManager interactionManager;
   @Shadow
   @Final
   public GameRenderer gameRenderer;
   @Shadow
   public int itemUseCooldown;
   @Shadow
   @Final
   public GameOptions options;
   @Shadow
   @Nullable
   public Screen currentScreen;
   @Shadow
   @Final
   private Dynamic renderTickCounter;
   @Shadow
   private volatile boolean paused;
   @Unique
   private final Dynamic zenith$renderTickCounter = new Dynamic(40.0F, 0L, this::getTargetMillisPerTick);

   @Shadow
   public abstract Window getWindow();

   @Shadow
   public abstract void doItemUse();

   @Shadow
   public abstract boolean shouldTick();

   @Shadow
   public abstract float getTargetMillisPerTick(float var1);

   @ModifyVariable(
      method = {"setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private Screen zenith$replaceScreen(Screen var1) {
      if (var1 instanceof TitleScreen) {
      }

      return var1;
   }

   @Inject(
      method = {"render(Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/MinecraftClient;runTasks()V",
         shift = Shift.BEFORE
      )}
   )
   private void hookPacketProcess(boolean var1, CallbackInfo var2) {
      EventManager.call(new PacketProcessEndEvent());
      int var3 = this.zenith$renderTickCounter.beginRenderTick(Util.getMeasuringTimeMs(), true);

      for (int var4 = 0; var4 < Math.min(10, var3); var4++) {
         EventManager.call(new PacketProcessStartEvent());
      }
   }

   @Inject(
      method = {"render(Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/RenderTickCounter$Dynamic;setTickFrozen(Z)V",
         shift = Shift.AFTER
      )}
   )
   private void hookTickRender(boolean var1, CallbackInfo var2) {
      this.zenith$renderTickCounter.tick(this.paused);
      this.zenith$renderTickCounter.setTickFrozen(!this.shouldTick());
   }

   @Unique
   private static boolean zenith$titleApplied;

   @Inject(
      method = {"tick()V"},
      at = {@At("HEAD")}
   )
   private void hookTickEvent(CallbackInfo var1) {
      if (!zenith$titleApplied) {
         zenith$titleApplied = true;

         try {
            this.window.setTitle("hackkkk @yoezproject | @moluchclient");
         } catch (Throwable var3) {
         }
      }

      EventManager.call(new TickEvent());
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"close()V"}
   )
   private void stop(CallbackInfo var1) {
      Zenith.getInstance().shutdown();
   }

   @Inject(
      method = {"handleInputEvents()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$skipInputWithoutPlayer(CallbackInfo var1) {
      if (this.player == null || this.interactionManager == null) {
         var1.cancel();
      }
   }

   @Redirect(
      method = {"handleInputEvents()V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
         ordinal = 0
      )
   )
   public void injectHandleInputEventss(ClientPlayNetworkHandler var1, Packet var2) {
      Event9 var3 = new Event9();
      EventManager.call(var3);
      if (!var3.isCancelled()) {
         var1.sendPacket(var2);
      }
   }

   @Redirect(
      method = {"handleInputEvents()V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;stopUsingItem(Lnet/minecraft/entity/player/PlayerEntity;)V",
         ordinal = 0
      )
   )
   public void injectHandleInputEventsss(ClientPlayerInteractionManager var1, PlayerEntity var2) {
      StopUsingItemEvent var3 = new StopUsingItemEvent();
      EventManager.call(var3);
      if (!var3.isCancelled()) {
         var1.stopUsingItem(var2);
      }
   }

   @Inject(
      method = {"tick()V"},
      at = {@At(
         value = "FIELD",
         target = "Lnet/minecraft/client/MinecraftClient;overlay:Lnet/minecraft/client/gui/screen/Overlay;",
         opcode = 180,
         ordinal = 2
      )}
   )
   public void injectHandleInputEvents(CallbackInfo var1) {
      EventManager.call(new HandleInputPostEvent());
      Event9 var2 = new Event9();
      EventManager.call(var2);
      if (!var2.isCancelled()) {
         HandleInputEvent var3 = new HandleInputEvent();
         EventManager.call(var3);
      }
   }

   @ModifyArg(
      method = {"handleInputEvents()V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V"
      )
   )
   private boolean zenith$autoMineHoldAttack(boolean var1) {
      return var1 || AutoMineModule.INSTANCE.method4352();
   }

   @WrapWithCondition(
      method = {"handleBlockBreaking(Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;cancelBlockBreaking()V"
      )}
   )
   private boolean zenith$keepAuraDig(ClientPlayerInteractionManager var1) {
      return !CrystalAuraModule.INSTANCE.method5743();
   }

   @ModifyReturnValue(
      method = {"getTargetMillisPerTick(F)F"},
      at = {@At("RETURN")}
   )
   private float zenith$applyTimerSpeed(float var1) {
      float var2 = Util13.getFloatValue();
      return var2 == 1.0F ? var1 : var1 / var2;
   }

   @Inject(
      method = {"doItemUse()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/Hand;values()[Lnet/minecraft/util/Hand;"
      )},
      cancellable = true
   )
   public void doItemUseHook(CallbackInfo var1) {
      if (this.player != null && this.interactionManager != null) {
         Event9 var2 = new Event9();
         EventManager.call(var2);
         if (var2.isCancelled()) {
            var1.cancel();
         } else {
            if (NoInteractModule.INSTANCE.isEnabled()) {
               for (Hand var6 : Hand.values()) {
                  if (!this.player.getStackInHand(var6).isEmpty()) {
                     ActionResult var7 = this.interactionManager.interactItem(this.player, var6);
                     if (var7.isAccepted()) {
                        if (var7 instanceof Success var8 && var8.swingSource().equals(SwingSource.CLIENT)) {
                           this.player.swingHand(var6);
                        }

                        this.gameRenderer.firstPersonRenderer.resetEquipProgress(var6);
                        var1.cancel();
                        return;
                     }
                  }
               }
            }
         }
      } else {
         var1.cancel();
      }
   }

   @Inject(
      method = {"handleInputEvents()V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerEntity;getInventory()Lnet/minecraft/entity/player/PlayerInventory;"
      )},
      cancellable = true
   )
   public void handleInputEventsHook(CallbackInfo var1) {
      Event7 var2 = new Event7();
      EventManager.call(var2);
      if (var2.isCancelled()) {
         var1.cancel();
      }
   }

   @Inject(
      method = {"onResolutionChanged()V"},
      at = {@At("TAIL")}
   )
   private void captureResize(CallbackInfo var1) {
      Zenith.getInstance().getKawaseBlur().method7367();
      ProjectionUtils.method300();
   }

   @Inject(
      method = {"render(Z)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gl/Framebuffer;blitToScreen()V",
         shift = Shift.BEFORE
      )}
   )
   private void captureRessize(boolean var1, CallbackInfo var2) {
      ImmediateRenderer.method8009();
      UniformStorage.endFrame();
      HintedText.endFrame();
      Zenith.getInstance().getKawaseBlur().method7368();
   }

   @ModifyVariable(
      method = {"setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private Screen mixin$modifySetScreenArg(Screen var1) {
      SetScreenEvent var2 = new SetScreenEvent(var1);
      EventManager.call(var2);
      return var2.getScreen();
   }
}

package org.zenith.utility.mixin.screen;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.events.HandledScreenRenderEvent;
import org.zenith.client.events.SlotClickEvent;
import org.zenith.client.modules.impl.misc.AutoInventoryModule;
import org.zenith.client.modules.impl.misc.ContainerHelperModule;
import org.zenith.client.modules.impl.player.AHHelperModule;
import org.zenith.client.modules.impl.render.BetterMinecraftModule;
import org.zenith.utility.game.ItemNbtUtils;
import org.zenith.utility.misc.Handler3;

@Mixin({HandledScreen.class})
public abstract class MixinHandledScreen extends Screen implements Handler3 {
   @Unique
   @Mutable
   private boolean isAuc;
   @Unique
   @Mutable
   private Slot lowSumSlotId = null;
   @Unique
   @Mutable
   private Slot lowAllSumSlotId = null;
   @Unique
   private int zenith$lastScanRevision = -1;
   @Unique
   private long zenith$openedAt = System.currentTimeMillis();
   @Unique
   private long zenith$closedAt;
   @Unique
   private float zenith$closeStartScale = 1.0F;
   @Unique
   private boolean zenith$closingAnimation;
   @Unique
   private boolean zenith$forceClose;
   @Unique
   private boolean zenith$scaleApplied;
   @Shadow
   @Final
   public ScreenHandler handler;
   @Shadow
   public int y;
   @Shadow
   public int x;
   @Shadow
   public int backgroundWidth;
   @Shadow
   public int backgroundHeight;
   @Shadow
   @Nullable
   public Slot focusedSlot;

   public MixinHandledScreen(Text var1) {
      super(var1);
   }

   @Shadow
   public abstract ScreenHandler getScreenHandler();

   @Inject(
      method = {"init()V"},
      at = {@At("HEAD")}
   )
   private void zenith$initScaleAnimation(CallbackInfo var1) {
      this.zenith$openedAt = System.currentTimeMillis();
      this.zenith$closedAt = 0L;
      this.zenith$closeStartScale = 1.0F;
      this.zenith$closingAnimation = false;
      this.zenith$forceClose = false;
      this.zenith$scaleApplied = false;
   }

   @Inject(
      method = {"renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawBackground(Lnet/minecraft/client/gui/DrawContext;FII)V"
      )}
   )
   private void zenith$applyBetterMinecraftScale(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      this.zenith$scaleApplied = false;
      if (this.zenith$shouldAnimateBetterMinecraft()) {
         float var6 = this.zenith$getScale();
         if (!(Math.abs(var6 - 1.0F) < 0.001F)) {
            var1.getMatrices().pushMatrix();
            var1.getMatrices().translate(this.width / 2.0F, this.height / 2.0F);
            var1.getMatrices().scale(var6, var6);
            var1.getMatrices().translate(-this.width / 2.0F, -this.height / 2.0F);
            this.zenith$scaleApplied = true;
         }
      }
   }

   @Inject(
      method = {"close()V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void zenith$closeWithBetterMinecraftAnimation(CallbackInfo var1) {
      if (!this.zenith$forceClose && this.zenith$shouldAnimateBetterMinecraft()) {
         this.zenith$startClosingAnimation();
         var1.cancel();
      }
   }

   @Inject(
      method = {"tick()V"},
      at = {@At("HEAD")}
   )
   private void tickScreen(CallbackInfo var1) {
      try {
         if (!AHHelperModule.INSTANCE.isEnabled()) {
            this.isAuc = false;
            this.lowSumSlotId = null;
            this.lowAllSumSlotId = null;
            this.zenith$lastScanRevision = -1;
            return;
         }

         int var2 = this.handler.getRevision();
         if (var2 == this.zenith$lastScanRevision) {
            return;
         }

         this.zenith$lastScanRevision = var2;
         this.isAuc = ItemNbtUtils.method81(this.handler);
         this.lowSumSlotId = null;
         this.lowAllSumSlotId = null;
         if (this.isAuc) {
            int var3 = Integer.MAX_VALUE;
            int var4 = Integer.MAX_VALUE;

            for (int var5 = 0; var5 < 44; var5++) {
               Slot var6 = this.getScreenHandler().slots.get(var5);
               if (!var6.getStack().isEmpty() && !ItemNbtUtils.method87(var6.getStack())) {
                  int var7 = ItemNbtUtils.method62(var6.getStack());
                  if (var7 != Integer.MAX_VALUE) {
                     if (var7 < var3) {
                        this.lowSumSlotId = var6;
                        var3 = var7;
                     }

                     int var8 = ItemNbtUtils.method64(var6.getStack());
                     if (var8 != Integer.MAX_VALUE && var8 < var4) {
                        var4 = var8;
                        this.lowAllSumSlotId = var6;
                     }
                  }
               }
            }
         }
      } catch (Exception var9) {
         var9.printStackTrace();
      }
   }

   @Inject(
      method = {"onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;onMouseClick(Lnet/minecraft/screen/slot/Slot;Lnet/minecraft/screen/slot/SlotActionType;)V"
      )},
      cancellable = true
   )
   private void onClick(Slot var1, int var2, int var3, SlotActionType var4, CallbackInfo var5) {
      SlotClickEvent var6 = new SlotClickEvent(this.handler.syncId, var2, var3, var4);
      EventManager.call(var6);
      if (var6.isCancelled()) {
         var5.cancel();
      }
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/gui/DrawContext;IIF)V"},
      at = {@At("RETURN")}
   )
   private void zenith$renderAuctionHighlights(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      if (this.isAuc && AHHelperModule.INSTANCE.isEnabled()) {
         if (this.lowSumSlotId != null) {
            AHHelperModule.INSTANCE.method4044(var1, this.x, this.y, this.lowSumSlotId, true);
         }

         if (this.lowAllSumSlotId != null && this.lowAllSumSlotId != this.lowSumSlotId) {
            AHHelperModule.INSTANCE.method4044(var1, this.x, this.y, this.lowAllSumSlotId, false);
         }
      }
   }

   @Inject(
      method = {"init()V"},
      at = {@At("RETURN")}
   )
   public void injectInit(CallbackInfo var1) {
      try {
         MinecraftClient var2 = MinecraftClient.getInstance();
         if (AutoInventoryModule.INSTANCE.isEnabled()) {
            this.addDrawableChild(
               ButtonWidget.builder(Text.of("AutoSbor: " + AutoInventoryModule.INSTANCE.isEnabled()), var0 -> AutoInventoryModule.INSTANCE.toggle())
                  .dimensions(this.width / 2 + 2, this.y, 98, 20)
                  .build()
            );
         }

         if (ContainerHelperModule.INSTANCE.isEnabled()) {
            if ((Object)this instanceof InventoryScreen) {
               this.addDrawableChild(ButtonWidget.builder(Text.of("Выкинуть все"), var2x -> {
                  for (int var3x = 0; var3x < var2.player.currentScreenHandler.slots.size(); var3x++) {
                     ItemStack var4 = var2.player.currentScreenHandler.getSlot(var3x).getStack();
                     if (!var4.isEmpty()) {
                        SlotClickEvent var5 = new SlotClickEvent(this.handler.syncId, var3x, var4.getCount(), SlotActionType.THROW);
                        EventManager.call(var5);
                        if (!var5.isCancelled()) {
                           var2.interactionManager.clickSlot(this.handler.syncId, var3x, var4.getCount(), SlotActionType.THROW, var2.player);
                        }
                     }
                  }
               }).dimensions(this.x + 5, this.y - 25, this.backgroundWidth - 10, 20).build());
            } else if (GenericContainerScreen.class.isInstance(this) || ShulkerBoxScreen.class.isInstance(this)) {
               this.addDrawableChild(ButtonWidget.builder(Text.of("Выкинуть"), var2x -> {
                  for (int var3x = 0; var3x < this.handler.slots.size() - 36; var3x++) {
                     ItemStack var4 = this.handler.getSlot(var3x).getStack();
                     if (!var4.isEmpty()) {
                        var2.interactionManager.clickSlot(this.handler.syncId, var3x, var4.getCount(), SlotActionType.THROW, var2.player);
                     }
                  }
               }).dimensions(this.x, this.y - 25, 56, 20).build());
               this.addDrawableChild(ButtonWidget.builder(Text.of("Сложить"), var2x -> {
                  for (int var3x = this.handler.slots.size() - 36; var3x < this.handler.slots.size(); var3x++) {
                     if (!this.handler.getSlot(var3x).getStack().isEmpty()) {
                        var2.interactionManager.clickSlot(this.handler.syncId, var3x, 0, SlotActionType.QUICK_MOVE, var2.player);
                     }
                  }
               }).dimensions(this.x + 56 + 4, this.y - 25, 56, 20).build());
               this.addDrawableChild(ButtonWidget.builder(Text.of("Забрать"), var2x -> {
                  for (int var3x = 0; var3x < this.handler.slots.size() - 36; var3x++) {
                     if (!this.handler.getSlot(var3x).getStack().isEmpty()) {
                        var2.interactionManager.clickSlot(this.handler.syncId, var3x, 0, SlotActionType.QUICK_MOVE, var2.player);
                     }
                  }
               }).dimensions(this.x + 56 + 56 + 4 + 4, this.y - 25, 56, 20).build());
            }
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/gui/DrawContext;IIF)V"},
      at = {@At("RETURN")}
   )
   public void render(DrawContext var1, int var2, int var3, float var4, CallbackInfo var5) {
      EventManager.call(new HandledScreenRenderEvent(var1, this.focusedSlot, this.backgroundWidth, this.backgroundHeight));
      if (!this.zenith$isInventoryScreen()) {
         this.zenith$betterMinecraft$popScaleIfNeeded(var1);
         this.zenith$finishClosingAnimation();
      }
   }

   @Unique
   @Override
   public boolean zenith$betterMinecraft$isClosingAnimation() {
      return this.zenith$closingAnimation && this.zenith$shouldAnimateBetterMinecraft();
   }

   @Unique
   @Override
   public void zenith$betterMinecraft$popScaleIfNeeded(DrawContext var1) {
      if (this.zenith$scaleApplied) {
         var1.getMatrices().popMatrix();
         this.zenith$scaleApplied = false;
      }
   }

   @Unique
   @Override
   public void zenith$betterMinecraft$finishClosingAnimation() {
      this.zenith$finishClosingAnimation();
   }

   @Unique
   private boolean zenith$shouldAnimateBetterMinecraft() {
      return this.zenith$isInventoryScreen() ? BetterMinecraftModule.INSTANCE.method2011() : BetterMinecraftModule.INSTANCE.method2010();
   }

   @Unique
   private boolean zenith$isInventoryScreen() {
      return InventoryScreen.class.isInstance(this);
   }

   @Unique
   private float zenith$getScale() {
      return this.zenith$closingAnimation
         ? BetterMinecraftModule.INSTANCE.method2013(this.zenith$closedAt, this.zenith$closeStartScale)
         : BetterMinecraftModule.INSTANCE.method2012(this.zenith$openedAt);
   }

   @Unique
   private void zenith$startClosingAnimation() {
      if (!this.zenith$closingAnimation) {
         this.zenith$closeStartScale = this.zenith$getScale();
         this.zenith$closedAt = System.currentTimeMillis();
         this.zenith$closingAnimation = true;
      }
   }

   @Unique
   private void zenith$finishClosingAnimation() {
      if (this.zenith$closingAnimation && BetterMinecraftModule.INSTANCE.method2014(this.zenith$closedAt)) {
         this.zenith$forceClose = true;
         this.close();
         this.zenith$forceClose = false;
      }
   }
}

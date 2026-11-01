package org.zenith.utility.game;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import org.zenith.Zenith;
import org.zenith.client.events.MouseButtonEvent;
import org.zenith.client.modules.impl.misc.AutoCraftModule;
import org.zenith.utility.api.Class1;
import org.zenith.utility.misc.CraftBinding;

public final class CraftBindingController {
   private final AutoCraftModule autoCraftModule;
   private CraftBinding craftBinding = CraftBinding.method1685();
   private boolean flag;

   public CraftBindingController(AutoCraftModule var1) {
      this.autoCraftModule = var1;
   }

   public boolean isBinding() {
      return !this.craftBinding.method1686();
   }

   public String method7223() {
      return this.craftBinding.method1686() ? "" : this.craftBinding.method1687(this.autoCraftModule);
   }

   public void method7224() {
      this.craftBinding = CraftBinding.method1685();
   }

   public void reset() {
      this.craftBinding = CraftBinding.method1685();
      this.method7231(true);
   }

   public void method7225(String var1) {
      CraftRecipe var2 = this.autoCraftModule.method1096();
      if (var2 != null && var1 != null && !var1.isBlank()) {
         this.craftBinding = new CraftBinding(CraftBinding.Type.SOURCE, var2.getText2(), var2.getId(), var1);
         this.method7229();
         AutoCraftModule.mc.setScreen(null);
      }
   }

   public void method7226() {
      CraftRecipe var1 = this.autoCraftModule.method1096();
      if (var1 != null) {
         this.craftBinding = new CraftBinding(CraftBinding.Type.OUTPUT, var1.getText2(), var1.getId(), "");
         this.method7229();
         AutoCraftModule.mc.setScreen(null);
      }
   }

   public void method7227() {
      CraftRecipe var1 = this.autoCraftModule.method1096();
      if (var1 != null) {
         this.craftBinding = new CraftBinding(CraftBinding.Type.WORKBENCH, var1.getText2(), var1.getId(), "");
         this.method7229();
         AutoCraftModule.mc.setScreen(null);
      }
   }

   public boolean method7228(MouseButtonEvent var1) {
      if (this.craftBinding.method1686()) {
         return false;
      } else if (AutoCraftModule.mc.player != null && AutoCraftModule.mc.world != null) {
         if (var1.getIntValue2() != 1 || var1.getIntValue() != 1) {
            return true;
         } else if (AutoCraftModule.mc.currentScreen != null) {
            return true;
         } else if (AutoCraftModule.mc.crosshairTarget instanceof BlockHitResult var2 && AutoCraftModule.mc.crosshairTarget.getType() == Type.BLOCK) {
            CraftRecipe var6 = this.autoCraftModule.method1094(this.craftBinding.getText(), this.craftBinding.getText2());
            if (var6 == null) {
               this.craftBinding = CraftBinding.method1685();
               this.method7230();
               return true;
            } else {
               BlockPos var4 = var2.getBlockPos();
               if (this.craftBinding.getType() == CraftBinding.Type.WORKBENCH
                  && AutoCraftModule.mc.world.getBlockState(var4).getBlock() != Blocks.CRAFTING_TABLE) {
                  this.autoCraftModule.method1128("Этот блок не является верстаком");
                  return true;
               } else if ((this.craftBinding.getType() == CraftBinding.Type.SOURCE || this.craftBinding.getType() == CraftBinding.Type.OUTPUT)
                  && !this.method7232(var4)) {
                  this.autoCraftModule.method1128("Этот тип сундуков пока не поддерживается");
                  return true;
               } else {
                  Class1 var5 = Class1.method55(var4);
                  if (this.craftBinding.getType() == CraftBinding.Type.SOURCE && !this.craftBinding.getText3().isBlank()) {
                     var6.getItems3().put(this.craftBinding.getText3(), var5);
                     this.autoCraftModule.method1106();
                     this.autoCraftModule.method1127("Сундук-источник сохранен для " + var6.method5397(this.craftBinding.getText3(), this.autoCraftModule));
                  } else if (this.craftBinding.getType() == CraftBinding.Type.OUTPUT) {
                     var6.method5407(var5);
                     this.autoCraftModule.method1127("Сундук-склад сохранен для " + var6.getText3());
                  } else if (this.craftBinding.getType() == CraftBinding.Type.WORKBENCH) {
                     var6.method5408(var5);
                     this.autoCraftModule.method1127("Верстак сохранен для " + var6.getText3());
                  }

                  this.craftBinding = CraftBinding.method1685();
                  this.method7230();
                  Zenith.getInstance().getManager().save();
                  return true;
               }
            }
         } else {
            this.autoCraftModule.method1128("Наведитесь на блок для привязки Автокрафта");
            return true;
         }
      } else {
         return true;
      }
   }

   public void method7229() {
      if (!this.flag && !this.autoCraftModule.isEnabledRaw()) {
         EventManager.register(this.autoCraftModule);
         this.flag = true;
      }
   }

   public void method7230() {
      this.method7231(false);
   }

   public void method7231(boolean var1) {
      if (this.flag) {
         if (!var1 && this.autoCraftModule.isEnabledRaw()) {
            this.flag = false;
         } else {
            EventManager.unregister(this.autoCraftModule);
            this.flag = false;
         }
      }
   }

   private boolean method7232(BlockPos var1) {
      BlockEntity var2 = AutoCraftModule.mc.world.getBlockEntity(var1);
      return var2 instanceof ChestBlockEntity || var2 instanceof BarrelBlockEntity || var2 instanceof ShulkerBoxBlockEntity;
   }
}

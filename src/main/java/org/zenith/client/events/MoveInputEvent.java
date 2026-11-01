package org.zenith.client.events;

import lombok.Generated;
import net.minecraft.util.PlayerInput;
import org.zenith.CancellableEvent;

public class MoveInputEvent extends CancellableEvent {
   private PlayerInput playerInput;

   public void method3038(boolean var1) {
      this.playerInput = new PlayerInput(
         var1,
         this.playerInput.backward(),
         this.playerInput.left(),
         this.playerInput.right(),
         this.playerInput.jump(),
         this.playerInput.sneak(),
         this.playerInput.sprint()
      );
   }

   public void method3039(boolean var1) {
      this.playerInput = new PlayerInput(
         this.playerInput.forward(),
         this.playerInput.backward(),
         this.playerInput.left(),
         this.playerInput.right(),
         this.playerInput.jump(),
         this.playerInput.sneak(),
         var1
      );
   }

   public void method3040(boolean var1) {
      this.playerInput = new PlayerInput(
         this.playerInput.forward(),
         this.playerInput.backward(),
         this.playerInput.left(),
         this.playerInput.right(),
         this.playerInput.jump(),
         var1,
         this.playerInput.sprint()
      );
   }

   public void method3041(boolean var1) {
      this.playerInput = new PlayerInput(
         this.playerInput.forward(),
         this.playerInput.backward(),
         this.playerInput.left(),
         this.playerInput.right(),
         var1,
         this.playerInput.sneak(),
         this.playerInput.sprint()
      );
   }

   public void method3042(float var1, float var2) {
      boolean[] var3 = method3043(var1);
      boolean[] var4 = method3043(var2);
      this.playerInput = new PlayerInput(var3[0], var3[1], var4[0], var4[1], this.playerInput.jump(), this.playerInput.sneak(), this.playerInput.sprint());
   }

   private static boolean[] method3043(float var0) {
      if (var0 == 1.0F) {
         return new boolean[]{true, false};
      } else {
         return var0 == -1.0F ? new boolean[]{false, true} : new boolean[]{false, false};
      }
   }

   public void method3044() {
      this.playerInput = new PlayerInput(false, false, false, false, false, false, false);
   }

   public void method3045(boolean var1) {
      this.playerInput = new PlayerInput(
         this.playerInput.forward(),
         this.playerInput.backward(),
         var1,
         this.playerInput.right(),
         this.playerInput.jump(),
         this.playerInput.sneak(),
         this.playerInput.sprint()
      );
   }

   public void method3046(boolean var1) {
      this.playerInput = new PlayerInput(
         this.playerInput.forward(),
         this.playerInput.backward(),
         this.playerInput.left(),
         var1,
         this.playerInput.jump(),
         this.playerInput.sneak(),
         this.playerInput.sprint()
      );
   }

   public void method3047(boolean var1) {
      this.playerInput = new PlayerInput(
         this.playerInput.forward(),
         var1,
         this.playerInput.left(),
         this.playerInput.right(),
         this.playerInput.jump(),
         this.playerInput.sneak(),
         this.playerInput.sprint()
      );
   }

   @Generated
   public PlayerInput getPlayerInput() {
      return this.playerInput;
   }

   @Generated
   public void setPlayerInput(PlayerInput var1) {
      this.playerInput = var1;
   }

   @Generated
   public MoveInputEvent(PlayerInput var1) {
      this.playerInput = var1;
   }
}

package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class HeldItemRenderEvent implements Event {
   private AbstractClientPlayerEntity abstractClientPlayerEntity;
   private ItemStack itemStack;
   private Hand hand;

   @Generated
   public AbstractClientPlayerEntity getAbstractClientPlayerEntity() {
      return this.abstractClientPlayerEntity;
   }

   @Generated
   public ItemStack getItemStack() {
      return this.itemStack;
   }

   @Generated
   public Hand getHand() {
      return this.hand;
   }

   @Generated
   public void setAbstractClientPlayerEntity(AbstractClientPlayerEntity var1) {
      this.abstractClientPlayerEntity = var1;
   }

   @Generated
   public void setItemStack(ItemStack var1) {
      this.itemStack = var1;
   }

   @Generated
   public void setHand(Hand var1) {
      this.hand = var1;
   }

   @Generated
   public HeldItemRenderEvent(AbstractClientPlayerEntity var1, ItemStack var2, Hand var3) {
      this.abstractClientPlayerEntity = var1;
      this.itemStack = var2;
      this.hand = var3;
   }
}

package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.entity.Entity;

public class EntityHitBoxEvent implements Event {
   private Entity entity;
   private float size;

   @Generated
   public Entity getEntity() {
      return this.entity;
   }

   @Generated
   public float getSize() {
      return this.size;
   }

   @Generated
   public void setEntity(Entity var1) {
      this.entity = var1;
   }

   @Generated
   public void setSize(float var1) {
      this.size = var1;
   }

   @Generated
   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof EntityHitBoxEvent var2)) {
         return false;
      } else if (!var2.method265(this)) {
         return false;
      } else if (Float.compare(this.getSize(), var2.getSize()) != 0) {
         return false;
      } else {
         Entity var3 = this.getEntity();
         Entity var4 = var2.getEntity();
         return var3 == null ? var4 == null : var3.equals(var4);
      }
   }

   @Generated
   public boolean method265(Object var1) {
      return var1 instanceof EntityHitBoxEvent;
   }

   @Generated
   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + Float.floatToIntBits(this.getSize());
      Entity var3 = this.getEntity();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }

   @Generated
   @Override
   public String toString() {
      return "EventEntityHitBox(entity=" + this.getEntity() + ", size=" + this.getSize() + ")";
   }

   @Generated
   public EntityHitBoxEvent(Entity var1, float var2) {
      this.entity = var1;
      this.size = var2;
   }
}

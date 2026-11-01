package org.zenith.client.events;

import lombok.Generated;
import org.zenith.CancellableEvent;
import org.zenith.Rotation;

public class CameraUpdateEvent extends CancellableEvent {
   private boolean flag;
   private float floatValue;
   private Rotation rotation;

   @Generated
   public boolean isFlag() {
      return this.flag;
   }

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   @Generated
   public Rotation getRotation() {
      return this.rotation;
   }

   @Generated
   public void setFlag(boolean var1) {
      this.flag = var1;
   }

   @Generated
   public void setFloatValue(float var1) {
      this.floatValue = var1;
   }

   @Generated
   public void setRotation(Rotation var1) {
      this.rotation = var1;
   }

   @Generated
   public CameraUpdateEvent(boolean var1, float var2, Rotation var3) {
      this.flag = var1;
      this.floatValue = var2;
      this.rotation = var3;
   }
}

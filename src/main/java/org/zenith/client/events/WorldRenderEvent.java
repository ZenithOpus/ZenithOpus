package org.zenith.client.events;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;
import net.minecraft.client.util.math.MatrixStack;

public final class WorldRenderEvent implements Event {
   private final MatrixStack matrixStack;
   private final float floatValue;

   @Generated
   public MatrixStack getMatrixStack() {
      return this.matrixStack;
   }

   @Generated
   public float getFloatValue() {
      return this.floatValue;
   }

   @Generated
   public WorldRenderEvent(MatrixStack var1, float var2) {
      this.matrixStack = var1;
      this.floatValue = var2;
   }
}

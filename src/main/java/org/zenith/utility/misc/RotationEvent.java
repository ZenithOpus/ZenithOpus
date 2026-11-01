package org.zenith.utility.misc;

import com.darkmagician6.eventapi.events.Event;
import org.zenith.Rotation;

public final class RotationEvent implements Event {
   private final Rotation rotation;

   public Rotation rotation() {
      return this.rotation;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof RotationEvent other)) return false;
      return java.util.Objects.equals(this.rotation, other.rotation);
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(rotation);
   }

   @Override
   public String toString() {
      return "RotationEvent[" + "rotation=" + this.rotation + "]";
   }

   public RotationEvent(Rotation var1) {
      this.rotation = var1;
   }
}

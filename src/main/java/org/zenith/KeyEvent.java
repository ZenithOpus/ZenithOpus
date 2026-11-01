package org.zenith;

import com.darkmagician6.eventapi.events.Event;
import lombok.Generated;

public final class KeyEvent implements Event {
   private final int intValue;
   private final int keyCode;
   private final int modifiers;

   public boolean isKey(int var1) {
      return var1 == this.keyCode;
   }

   public boolean isPress(int var1) {
      return this.isPress(var1, MinecraftAccess.mc.currentScreen == null);
   }

   public boolean isPress(int var1, boolean var2) {
      return this.keyCode == var1 && this.intValue == 1 && var2;
   }

   public boolean isRelease(int var1) {
      return this.isRelease(var1, MinecraftAccess.mc.currentScreen == null);
   }

   public boolean isRelease(int var1, boolean var2) {
      return this.keyCode == var1 && this.intValue == 0 && var2;
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public int getKeyCode() {
      return this.keyCode;
   }

   @Generated
   public int getModifiers() {
      return this.modifiers;
   }

   @Generated
   public KeyEvent(int var1, int var2, int var3) {
      this.intValue = var1;
      this.keyCode = var2;
      this.modifiers = var3;
   }
}

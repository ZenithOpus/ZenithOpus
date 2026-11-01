package org.zenith;

import lombok.Generated;

public enum ModuleCategory {
   COMBAT("Combat", "3"),
   MOVEMENT("Movement", "4"),
   MISC("Misc", "5"),
   RENDER("Visuals", "6"),
   PLAYER("PvE", "7"),
   THEMES("Themes", "G");

   private final String text;
   private final String text2;

   private ModuleCategory(String var3, String var4) {
      this.text = var3;
      this.text2 = var4;
   }

   @Generated
   public String getIcon() {
      return this.text2;
   }

   @Generated
   public String getName() {
      return this.text;
   }
}

package org.zenith.utility.render.display.base;

import lombok.Generated;
import net.minecraft.util.Identifier;
import org.zenith.Zenith;

public class CategoryIcon {
   private final Identifier identifier;

   public CategoryIcon(String var1) {
      if (var1.contains(":")) {
         this.identifier = Identifier.of(var1);
      } else if (var1.contains("/")) {
         this.identifier = Zenith.method1501(var1);
      } else {
         this.identifier = Zenith.method1501("icons/category/" + var1);
      }
   }

   @Generated
   public Identifier getIdentifier() {
      return this.identifier;
   }
}

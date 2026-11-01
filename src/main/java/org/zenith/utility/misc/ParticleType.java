package org.zenith.utility.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.util.Identifier;
import org.zenith.Zenith;

public class ParticleType {
   private static final Map<String, ParticleType.Type> items = new HashMap<>();

   public static String[] method1704() {
      ParticleType.Type[] var0 = ParticleType.Type.values();
      String[] var1 = new String[var0.length];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = var0[var2].getText();
      }

      return var1;
   }

   public static Identifier method1705(String var0) {
      ParticleType.Type var1 = items.get(var0);
      if (var1 == null) {
         try {
            var1 = ParticleType.Type.valueOf(var0.toUpperCase());
         } catch (IllegalArgumentException var3) {
         }
      }

      return var1 != null ? var1.getSupplier().get() : null;
   }

   static {
      for (ParticleType.Type var3 : ParticleType.Type.values()) {
         items.put(var3.getText(), var3);
      }
   }

   public static enum Type {
      SNOWFLAKE("particle.texture.snowflake", "particles/snowflake.png"),
      STAR("particle.texture.star", "particles/star.png"),
      HEART("particle.texture.heart", "particles/heart.png"),
      FIREFLY("particle.texture.firefly", "particles/firefly.png"),
      SPACE_GLOW("particle.texture.spaceGlow", "particles/space_glow.png"),
      SPACE_STAR("particle.texture.spaceStar", "particles/space_star.png");

      private final String text;
      private final Supplier<Identifier> supplier;

      private Type(String var3, String var4) {
         this.text = var3;
         this.supplier = () -> Zenith.method1501(var4);
      }

      @Generated
      public String getText() {
         return this.text;
      }

      @Generated
      public Supplier<Identifier> getSupplier() {
         return this.supplier;
      }
   }
}

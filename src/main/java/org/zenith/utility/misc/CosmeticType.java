package org.zenith.utility.misc;

import java.util.Locale;

public enum CosmeticType {
   HAT(CosmeticType.Type.HEAD),
   HEAD(CosmeticType.Type.HEAD),
   BODY(CosmeticType.Type.BODY),
   LEFT_ARM(CosmeticType.Type.LEFT_ARM),
   RIGHT_ARM(CosmeticType.Type.RIGHT_ARM),
   LEFT_LEG(CosmeticType.Type.LEFT_LEG),
   RIGHT_LEG(CosmeticType.Type.RIGHT_LEG),
   WINGS(CosmeticType.Type.BODY),
   TAIL(CosmeticType.Type.BODY),
   FULL(CosmeticType.Type.BODY),
   PET(CosmeticType.Type.WORLD),
   FLYING_PET(CosmeticType.Type.WORLD),
   MOUNT(CosmeticType.Type.WORLD),
   INDEPENDENT(CosmeticType.Type.WORLD),
   ANIMATION(CosmeticType.Type.NONE),
   UNKNOWN(CosmeticType.Type.NONE);

   private final CosmeticType.Type type;

   private CosmeticType(CosmeticType.Type var3) {
      this.type = var3;
   }

   public CosmeticType.Type getType() {
      return this.type;
   }

   public boolean method1682() {
      return this.type != CosmeticType.Type.NONE;
   }

   public CosmeticType.Type method1683() {
      return switch (this.type) {
         case LEFT_ARM -> CosmeticType.Type.RIGHT_ARM;
         case RIGHT_ARM -> CosmeticType.Type.LEFT_ARM;
         case LEFT_LEG -> CosmeticType.Type.RIGHT_LEG;
         case RIGHT_LEG -> CosmeticType.Type.LEFT_LEG;
         default -> null;
      };
   }

   public static CosmeticType method1684(String var0) {
      if (var0 != null && !var0.isBlank()) {
         try {
            return valueOf(var0.strip().toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var2) {
            return UNKNOWN;
         }
      } else {
         return UNKNOWN;
      }
   }

   public static enum Type {
      HEAD,
      BODY,
      LEFT_ARM,
      RIGHT_ARM,
      LEFT_LEG,
      RIGHT_LEG,
      WORLD,
      NONE;
   }
}

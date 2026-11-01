package org.zenith.utility.misc;

import net.minecraft.util.Identifier;
import org.zenith.utility.api.CosmeticAnimation;
import org.zenith.utility.api.CosmeticItemInfo;
import org.zenith.utility.api.Overrides;

public final class CosmeticModel {
   private final CosmeticItemInfo cosmeticItemInfo;
   private final CosmeticSkeleton cosmeticSkeleton;
   private final Identifier[] identifiers;
   private final CosmeticAnimation cosmeticAnimation;
   private final CosmeticType cosmeticType;
   private final Util31.Data data;
   private final boolean flag;
   private static final float CONST_floatValue = 0.5F;
   private static final float CONST_floatValue2 = 0.75F;

   public CosmeticModel(CosmeticItemInfo var1, CosmeticSkeleton var2, Identifier[] var3, CosmeticAnimation var4, Overrides var5) {
      this.cosmeticItemInfo = var1;
      this.cosmeticSkeleton = var2;
      this.identifiers = var3;
      this.cosmeticAnimation = var4;
      this.cosmeticType = var5 == null ? var1.getCosmeticType() : var5.method6462(var1);
      Util31.Data var6 = Util31.method7852(var1.getData(), var1.getType(), this.cosmeticType);
      Overrides.Data var7 = var5 == null ? null : var5.method6458(var1.getText());
      this.data = var7 != null ? var5.method6463(var1.getText(), var6) : Util23.method5990(var6, this.cosmeticType, var2.method7145());
      this.flag = var1.method7751() && this.cosmeticType.method1683() != null && method5977(var2, this.data, this.cosmeticType);
   }

   private static boolean method5977(CosmeticSkeleton var0, Util31.Data var1, CosmeticType var2) {
      float[] var3 = var0.method7145();
      float var4 = Math.abs((var3[3] - var3[0]) * var1.getFloatValue7());
      boolean var5 = var2 == CosmeticType.LEFT_ARM || var2 == CosmeticType.RIGHT_ARM;
      return var4 < (var5 ? 0.75F : 0.5F);
   }

   public CosmeticItemInfo getCosmeticItemInfo() {
      return this.cosmeticItemInfo;
   }

   public CosmeticType getCosmeticType() {
      return this.cosmeticType;
   }

   public CosmeticSkeleton getCosmeticSkeleton() {
      return this.cosmeticSkeleton;
   }

   public Identifier[] getIdentifiers() {
      return this.identifiers;
   }

   public Identifier texture() {
      return this.identifiers[0];
   }

   public CosmeticAnimation getCosmeticAnimation() {
      return this.cosmeticAnimation;
   }

   public Util31.Data getData() {
      return this.data;
   }

   public boolean isFlag() {
      return this.flag;
   }
}

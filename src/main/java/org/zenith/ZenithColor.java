package org.zenith;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.util.Objects;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;
import org.zenith.utility.misc.ColorUtils;
import org.zenith.utility.render.MathUtils;

public class ZenithColor {
   public static final ZenithColor INSTANCE = new ZenithColor(255, 255, 255);
   public static final ZenithColor INSTANCE2 = new ZenithColor(0, 0, 0);
   public static final ZenithColor INSTANCE3 = new ZenithColor(0, 255, 0);
   public static final ZenithColor INSTANCE4 = new ZenithColor(255, 0, 0);
   public static final ZenithColor INSTANCE5 = new ZenithColor(0, 0, 255);
   public static final ZenithColor INSTANCE6 = new ZenithColor(255, 255, 0);
   public static final ZenithColor INSTANCE7 = new ZenithColor(88, 87, 93);
   public static final ZenithColor INSTANCE8 = new ZenithColor(0, 0, 0, 0);
   public static final ZenithColor INSTANCE9 = method7289("#0E0E10");
   private transient float[] floatValues;
   private final int intValue;
   private final int intValue2;
   private final int intValue3;
   private final int intValue4;
   private static final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4);

   public ZenithColor(String var1) {
      if (var1.startsWith("#")) {
         var1 = var1.substring(1);
      }

      long var2 = Long.parseLong(var1, 16);
      int var4;
      int var5;
      int var6;
      int var7;
      if (var1.length() == 8) {
         var4 = (int)(var2 >> 24 & 255L);
         var5 = (int)(var2 >> 16 & 255L);
         var6 = (int)(var2 >> 8 & 255L);
         var7 = (int)(var2 & 255L);
      } else {
         if (var1.length() != 6) {
            throw new IllegalArgumentException("Invalid hex color: " + var1);
         }

         var4 = (int)(var2 >> 16 & 255L);
         var5 = (int)(var2 >> 8 & 255L);
         var6 = (int)(var2 & 255L);
         var7 = 255;
      }

      this.intValue = var4;
      this.intValue2 = var5;
      this.intValue3 = var6;
      this.intValue4 = var7;
   }

   public ZenithColor(int var1) {
      this(ColorUtils.method6420(var1), ColorUtils.method6421(var1), ColorUtils.method6422(var1), ColorUtils.method6423(var1));
   }

   public ZenithColor(Color var1) {
      this(var1.getRed(), var1.getGreen(), var1.getBlue(), var1.getAlpha());
   }

   public ZenithColor(int var1, int var2, int var3) {
      this(var1, var2, var3, 255);
   }

   public ZenithColor(int var1, int var2, int var3, int var4) {
      var1 = MathHelper.clamp(var1, 0, 255);
      var2 = MathHelper.clamp(var2, 0, 255);
      var3 = MathHelper.clamp(var3, 0, 255);
      var4 = MathHelper.clamp(var4, 0, 255);
      this.intValue = var1;
      this.intValue2 = var2;
      this.intValue3 = var3;
      this.intValue4 = var4;
   }

   public int method7287() {
      int var1 = Math.round((float)this.method7288(this.intValue4));
      int var2 = Math.round((float)this.method7288(this.intValue));
      int var3 = Math.round((float)this.method7288(this.intValue2));
      int var4 = Math.round((float)this.method7288(this.intValue3));
      return (var1 & 0xFF) << 24 | (var2 & 0xFF) << 16 | (var3 & 0xFF) << 8 | var4 & 0xFF;
   }

   private int method7288(float var1) {
      return (int)Math.max(0.0F, Math.min(255.0F, var1));
   }

   public static ZenithColor method7289(String var0) {
      String var1 = var0.startsWith("#") ? var0.substring(1) : var0;
      if (var1.length() != 6 && var1.length() != 8) {
         throw new IllegalArgumentException("Hex color must be in the format #RRGGBB or #RRGGBBAA");
      } else {
         int var2 = Integer.parseInt(var1.substring(0, 2), 16);
         int var3 = Integer.parseInt(var1.substring(2, 4), 16);
         int var4 = Integer.parseInt(var1.substring(4, 6), 16);
         int var5 = var1.length() == 8 ? Integer.parseInt(var1.substring(6, 8), 16) : 255;
         return new ZenithColor(var2, var3, var4, var5);
      }
   }

   public static ZenithColor method7290(ZenithColor var0, ZenithColor var1, float var2) {
      float var3 = Math.max(0.0F, Math.min(1.0F, var2));
      int var4 = (int)(var0.getIntValue() + (var1.getIntValue() - var0.getIntValue()) * var3);
      int var5 = (int)(var0.getIntValue2() + (var1.getIntValue2() - var0.getIntValue2()) * var3);
      int var6 = (int)(var0.getIntValue3() + (var1.getIntValue3() - var0.getIntValue3()) * var3);
      int var7 = (int)(var0.getIntValue4() + (var1.getIntValue4() - var0.getIntValue4()) * var3);
      return new ZenithColor(var4, var5, var6, var7);
   }

   public static ZenithColor method7291(int var0) {
      int var1 = var0 >> 24 & 0xFF;
      int var2 = var0 >> 16 & 0xFF;
      int var3 = var0 >> 8 & 0xFF;
      int var4 = var0 & 0xFF;
      return new ZenithColor(var2, var3, var4, var1);
   }

   public ZenithColor method7292(float var1) {
      return new ZenithColor(this.intValue, this.intValue2, this.intValue3, this.method7288((int)(255.0F * var1)));
   }

   public ZenithColor method7293(int var1) {
      return new ZenithColor(this.intValue, this.intValue2, this.intValue3, var1);
   }

   public ZenithColor method7294(float var1) {
      return this.method7293(Math.max(0, Math.min(255, (int)(this.intValue4 * var1))));
   }

   public ZenithColor method7295(ZenithColor var1, float var2) {
      var2 = Math.min(1.0F, Math.max(0.0F, var2));
      return new ZenithColor(
         (int)MathUtils.method2649(this.getIntValue(), var1.getIntValue(), var2),
         (int)MathUtils.method2649(this.getIntValue2(), var1.getIntValue2(), var2),
         (int)MathUtils.method2649(this.getIntValue3(), var1.getIntValue3(), var2),
         (int)MathUtils.method2649(this.getIntValue4(), var1.getIntValue4(), var2)
      );
   }

   public ZenithColor method7296(float var1) {
      var1 = MathHelper.clamp(var1, 0.0F, 1.0F);
      return new ZenithColor((int)(this.intValue * (1.0F - var1)), (int)(this.intValue2 * (1.0F - var1)), (int)(this.intValue3 * (1.0F - var1)), this.intValue4);
   }

   public static ZenithColor method7297(float var0, float var1, float var2) {
      if (var1 == 0.0F) {
         int var11 = (int)(var2 * 255.0F + 0.5F);
         return new ZenithColor(var11, var11, var11);
      } else {
         float var3 = (var0 - (float)Math.floor(var0)) * 6.0F;
         float var4 = var3 - (float)Math.floor(var3);
         float var5 = var2 * (1.0F - var1);
         float var6 = var2 * (1.0F - var1 * var4);
         float var7 = var2 * (1.0F - var1 * (1.0F - var4));
         float var8 = 0.0F;
         float var9 = 0.0F;
         float var10 = 0.0F;
         switch ((int)var3) {
            case 0:
               var8 = var2;
               var9 = var7;
               var10 = var5;
               break;
            case 1:
               var8 = var6;
               var9 = var2;
               var10 = var5;
               break;
            case 2:
               var8 = var5;
               var9 = var2;
               var10 = var7;
               break;
            case 3:
               var8 = var5;
               var9 = var6;
               var10 = var2;
               break;
            case 4:
               var8 = var7;
               var9 = var5;
               var10 = var2;
               break;
            case 5:
               var8 = var2;
               var9 = var5;
               var10 = var6;
         }

         return new ZenithColor((int)(var8 * 255.0F), (int)(var9 * 255.0F), (int)(var10 * 255.0F));
      }
   }

   public float method7298() {
      return this.method7301()[0];
   }

   public float method7299() {
      return this.method7301()[1];
   }

   public float method7300() {
      return this.method7301()[2];
   }

   private float[] method7301() {
      if (this.floatValues == null) {
         this.floatValues = this.method7302();
      }

      return this.floatValues;
   }

   private float[] method7302() {
      float var1 = this.intValue / 255.0F;
      float var2 = this.intValue2 / 255.0F;
      float var3 = this.intValue3 / 255.0F;
      float var4 = Math.max(var1, Math.max(var2, var3));
      float var5 = Math.min(var1, Math.min(var2, var3));
      float var6 = var4 - var5;
      float var7 = 0.0F;
      if (var6 != 0.0F) {
         if (var4 == var1) {
            var7 = (var2 - var3) / var6;
         } else if (var4 == var2) {
            var7 = (var3 - var1) / var6 + 2.0F;
         } else {
            var7 = (var1 - var2) / var6 + 4.0F;
         }

         var7 /= 6.0F;
         if (var7 < 0.0F) {
            var7++;
         }
      }

      float var8 = var4 == 0.0F ? 0.0F : var6 / var4;
      return new float[]{var7, var8, var4};
   }

   public ZenithColor method7303(float var1) {
      var1 = MathHelper.clamp(var1, 0.0F, 1.0F);
      return new ZenithColor(
         (int)(this.intValue + (255.0F - this.intValue) * var1),
         (int)(this.intValue2 + (255.0F - this.intValue2) * var1),
         (int)(this.intValue3 + (255.0F - this.intValue3) * var1),
         this.intValue4
      );
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ZenithColor var2 = (ZenithColor)var1;
         return Float.compare(this.intValue, var2.intValue) == 0
            && Float.compare(this.intValue2, var2.intValue2) == 0
            && Float.compare(this.intValue3, var2.intValue3) == 0
            && Float.compare(this.intValue4, var2.intValue4) == 0;
      } else {
         return false;
      }
   }

   public float method7304(ZenithColor var1) {
      return Math.abs(this.method7298() - var1.method7298())
         + Math.abs(this.method7300() - var1.method7300())
         + Math.abs(this.method7299() - var1.method7299());
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.intValue, this.intValue2, this.intValue3, this.intValue4);
   }

   @Generated
   public int getIntValue() {
      return this.intValue;
   }

   @Generated
   public int getIntValue2() {
      return this.intValue2;
   }

   @Generated
   public int getIntValue3() {
      return this.intValue3;
   }

   @Generated
   public int getIntValue4() {
      return this.intValue4;
   }
}

package org.zenith.utility.render;

import com.google.common.collect.MapMaker;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction.Axis;
import org.joml.Vector3fc;

public final class BakedQuadCache {
   private static final float CONST_floatValue = 0.0626F;
   private static final int CONST_intValue = 8;
   private static final Map<BakedQuad, BakedQuadCache.Data> items = new MapMaker().weakKeys().concurrencyLevel(1).makeMap();

   private BakedQuadCache() {
   }

   public static List<BakedQuad> method5533(List<BakedQuad> var0) {
      int var1 = var0.size();
      if (var1 < 8) {
         return var0;
      } else {
         BakedQuad var2 = (BakedQuad)var0.get(0);
         BakedQuad var3 = (BakedQuad)var0.get(var1 - 1);
         BakedQuadCache.Data var4 = items.get(var3);
         if (var4 == null || var4.intValue != var1 || var4.bakedQuad != var2) {
            var4 = new BakedQuadCache.Data(var2, var1, method5534(var0));
            items.put(var3, var4);
         }

         return var4.items == null ? var0 : var4.items;
      }
   }

   private static List<BakedQuad> method5534(List<BakedQuad> var0) {
      int var1 = 0;
      int var2 = 0;

      for (int var3 = var0.size(); var2 < var3; var2++) {
         BakedQuad var4 = (BakedQuad)var0.get(var2);
         if (var4.face().getAxis() == Axis.Z) {
            var1++;
         } else if (!method5535(var4)) {
            return null;
         }
      }

      if (var1 != 0 && var1 != var0.size()) {
         List<BakedQuad> var6 = new ArrayList<>(var1);
         int var7 = 0;

         for (int var8 = var0.size(); var7 < var8; var7++) {
            BakedQuad var5 = (BakedQuad)var0.get(var7);
            if (var5.face().getAxis() == Axis.Z) {
               var6.add(var5);
            }
         }

         return List.copyOf(var6);
      } else {
         return null;
      }
   }

   private static boolean method5535(BakedQuad var0) {
      float var1 = Float.MAX_VALUE;
      float var2 = -Float.MAX_VALUE;

      for (int var3 = 0; var3 < 4; var3++) {
         Vector3fc var4 = var0.getPosition(var3);
         var1 = Math.min(var1, var4.z());
         var2 = Math.max(var2, var4.z());
      }

      return var2 - var1 <= 0.0626F;
   }

   private static final class Data {
      private final BakedQuad bakedQuad;
      private final int intValue;
      private final List<BakedQuad> items;

      public BakedQuad bakedQuad() {
         return this.bakedQuad;
      }

      public int intValue() {
         return this.intValue;
      }

      public List<BakedQuad> items() {
         return this.items;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.bakedQuad, other.bakedQuad) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.items, other.items);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(bakedQuad, intValue, items);
      }

      @Override
      public String toString() {
         return "Data[" + "bakedQuad=" + this.bakedQuad + ", " + "intValue=" + this.intValue + ", " + "items=" + this.items + "]";
      }

      private Data(BakedQuad var1, int var2, List<BakedQuad> var3) {
         this.bakedQuad = var1;
         this.intValue = var2;
         this.items = var3;
      }

      public BakedQuad getBakedQuad() {
         return this.bakedQuad;
      }

      public int size() {
         return this.intValue;
      }

      public List<BakedQuad> getItems() {
         return this.items;
      }
   }
}

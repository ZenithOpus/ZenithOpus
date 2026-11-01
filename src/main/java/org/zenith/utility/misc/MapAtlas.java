package org.zenith.utility.misc;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.zenith.utility.render.MapAtlasPage;

public final class MapAtlas {
   private final List<MapAtlasPage> items = new ArrayList<>();

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   @Nullable
   public MapAtlas.Data method6151(TextureManager var1) {
      MapAtlasPage var2 = this.items.isEmpty() ? null : this.items.get(this.items.size() - 1);
      if (var2 == null || var2.isFull()) {
         try {
            var2 = new MapAtlasPage(var1);
         } catch (Throwable var6) {
            return null;
         }

         this.items.add(var2);
      }

      int var3 = var2.method5416();
      if (var3 < 0) {
         return null;
      } else {
         int var4 = var3 % 16 * 128;
         int var5 = var3 / 16 * 128;
         return new MapAtlas.Data(var2, var4, var5);
      }
   }

   public void method6152(TextureManager var1) {
      for (MapAtlasPage var3 : this.items) {
         var3.method5418(var1);
      }

      this.items.clear();
   }

   public static final class Data {
      private final MapAtlasPage mapAtlasPage;
      private final int intValue;
      private final int intValue2;

      public MapAtlasPage mapAtlasPage() {
         return this.mapAtlasPage;
      }

      public int intValue() {
         return this.intValue;
      }

      public int intValue2() {
         return this.intValue2;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Data other)) return false;
         return java.util.Objects.equals(this.mapAtlasPage, other.mapAtlasPage) && java.util.Objects.equals(this.intValue, other.intValue) && java.util.Objects.equals(this.intValue2, other.intValue2);
      }

      @Override
      public int hashCode() {
         return java.util.Objects.hash(mapAtlasPage, intValue, intValue2);
      }

      @Override
      public String toString() {
         return "Data[" + "mapAtlasPage=" + this.mapAtlasPage + ", " + "intValue=" + this.intValue + ", " + "intValue2=" + this.intValue2 + "]";
      }

      public Data(MapAtlasPage var1, int var2, int var3) {
         this.mapAtlasPage = var1;
         this.intValue = var2;
         this.intValue2 = var3;
      }

      public Identifier method6153() {
         return this.mapAtlasPage.getIdentifier();
      }

      public float method6154() {
         return this.intValue / 2048.0F;
      }

      public float method6155() {
         return this.intValue2 / 2048.0F;
      }

      public MapAtlasPage getMapAtlasPage() {
         return this.mapAtlasPage;
      }

      public int getIntValue() {
         return this.intValue;
      }

      public int getIntValue2() {
         return this.intValue2;
      }
   }
}

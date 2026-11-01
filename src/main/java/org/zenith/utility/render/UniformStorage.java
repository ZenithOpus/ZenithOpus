package org.zenith.utility.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gl.DynamicUniformStorage;
import net.minecraft.client.gl.DynamicUniformStorage.Uploadable;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

public final class UniformStorage {
   public static final String text = "ZenithData";
   private static final int CONST_intValue = 16;
   private static final List<DynamicUniformStorage<UniformStorage.Entry>> items = new ArrayList<>();
   private static final List<DynamicUniformStorage<UniformStorage.Entry>> items2 = new ArrayList<>();

   private UniformStorage() {
   }

   public static int method1272(List<float[]> var0) {
      int[] var1 = new int[var0.size()];

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = ((float[])var0.get(var2)).length;
      }

      return method1273(var1);
   }

   public static int method1273(int[] var0) {
      Std140SizeCalculator var1 = new Std140SizeCalculator();

      for (int var5 : var0) {
         switch (var5) {
            case 1:
               var1.putFloat();
               break;
            case 2:
               var1.putVec2();
               break;
            case 3:
               var1.putVec3();
               break;
            case 4:
               var1.putVec4();
               break;
            case 16:
               var1.putMat4f();
               break;
            default:
               throw new IllegalArgumentException("Неподдерживаемый размер uniform: " + var5);
         }
      }

      return MathHelper.roundUpToMultiple(var1.get(), 16);
   }

   public static DynamicUniformStorage<UniformStorage.Entry> method1274(String var0, int var1) {
      DynamicUniformStorage var2 = new DynamicUniformStorage(var0, var1, 16);
      items.add(var2);
      return var2;
   }

   public static void method1275(DynamicUniformStorage<UniformStorage.Entry> var0) {
      if (items.remove(var0)) {
         items2.add(var0);
      }
   }

   public static GpuBufferSlice method1276(DynamicUniformStorage<UniformStorage.Entry> var0, List<float[]> var1) {
      int[] var2 = new int[var1.size()];
      int var3 = 0;

      for (int var4 = 0; var4 < var2.length; var4++) {
         var2[var4] = ((float[])var1.get(var4)).length;
         var3 += var2[var4];
      }

      float[] var8 = new float[var3];
      int var5 = 0;

      for (float[] var7 : var1) {
         System.arraycopy(var7, 0, var8, var5, var7.length);
         var5 += var7.length;
      }

      return var0.write(new UniformStorage.Entry(var8, var2));
   }

   public static void endFrame() {
      for (DynamicUniformStorage var1 : items) {
         var1.clear();
      }

      for (DynamicUniformStorage var3 : items2) {
         var3.close();
      }

      items2.clear();
   }

   public static final class Entry implements Uploadable {
      private static final Matrix4f matrix4f = new Matrix4f();
      private final float[] floatValues;
      private final int[] intValues;

      public Entry(float[] var1, int[] var2) {
         this.floatValues = var1;
         this.intValues = var2;
      }

      public boolean method1277(int[] var1, float[] var2) {
         return this.intValues == var1 && Arrays.equals(this.floatValues, var2);
      }

      @Override
      public void write(ByteBuffer buffer) {
         Std140Builder var2 = Std140Builder.intoBuffer(buffer);
         float[] var3 = this.floatValues;
         int var4 = 0;

         for (int var8 : this.intValues) {
            switch (var8) {
               case 1:
                  var2.putFloat(var3[var4]);
                  break;
               case 2:
                  var2.putVec2(var3[var4], var3[var4 + 1]);
                  break;
               case 3:
                  var2.putVec3(var3[var4], var3[var4 + 1], var3[var4 + 2]);
                  break;
               case 4:
                  var2.putVec4(var3[var4], var3[var4 + 1], var3[var4 + 2], var3[var4 + 3]);
                  break;
               case 16:
                  var2.putMat4f(matrix4f.set(var3, var4));
                  break;
               default:
                  throw new IllegalStateException();
            }

            var4 += var8;
         }
      }

      @Override
      public boolean equals(Object var1) {
         return var1 instanceof UniformStorage.Entry var2 && Arrays.equals(this.intValues, var2.intValues) && Arrays.equals(this.floatValues, var2.floatValues);
      }

      @Override
      public int hashCode() {
         return 31 * Arrays.hashCode(this.intValues) + Arrays.hashCode(this.floatValues);
      }
   }
}

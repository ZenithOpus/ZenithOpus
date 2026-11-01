package org.zenith.utility.misc;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import net.minecraft.client.MinecraftClient;
import org.zenith.base.Module;

public class TickingList<T> {
   private int intValue = 0;
   private final PriorityBlockingQueue<TickingList.Entry<T>> priorityBlockingQueue = new PriorityBlockingQueue<>(
      11, Comparator.comparingInt(var0 -> -var0.intValue2)
   );

   public void method13(int var1) {
      this.intValue += var1;
   }

   public void tick() {
      this.method13(1);
   }

   public void method14(TickingList.Entry<T> var1) {
      this.priorityBlockingQueue.removeIf(var1x -> var1x.module == var1.module);
      var1.intValue = var1.intValue + this.intValue;
      this.priorityBlockingQueue.add(var1);
   }

   public T method15() {
      TickingList.Entry var1 = this.priorityBlockingQueue.peek();
      if (var1 == null) {
         return null;
      } else {
         if (MinecraftClient.getInstance().isOnThread()) {
            while (var1 != null && (var1.intValue <= this.intValue || !var1.module.isEnabled())) {
               this.priorityBlockingQueue.poll();
               var1 = this.priorityBlockingQueue.peek();
            }
         }

         return var1 != null ? (T)var1.obj : null;
      }
   }

   public TickingList.Entry<T> method16() {
      TickingList.Entry var1 = this.priorityBlockingQueue.peek();
      if (var1 == null) {
         return null;
      } else {
         if (MinecraftClient.getInstance().isOnThread()) {
            while (var1 != null && (var1.intValue <= this.intValue || !var1.module.isEnabled())) {
               this.priorityBlockingQueue.poll();
               var1 = this.priorityBlockingQueue.peek();
            }
         }

         return var1;
      }
   }

   public void clear() {
      this.priorityBlockingQueue.clear();
   }

   public static class Entry<T> {
      public int intValue;
      public final int intValue2;
      public final Module module;
      public final T obj;

      public Entry(int var1, int var2, Module var3, T var4) {
         this.intValue = var1;
         this.intValue2 = var2;
         this.module = var3;
         this.obj = (T)var4;
      }
   }
}

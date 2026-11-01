package org.zenith.utility.misc;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Generated;
import net.minecraft.client.MinecraftClient;
import org.zenith.client.events.Event1;
import org.zenith.client.events.Event5;
import org.zenith.client.events.HandleInputEvent;
import org.zenith.client.events.HandleInputPostEvent;
import org.zenith.client.events.MoveInputEvent;
import org.zenith.client.events.UpdateEvent;

public class TaskScheduler {
   private static final Logger logger = Logger.getLogger(TaskScheduler.class.getName());
   private final LinkedList<TaskScheduler.Entry2> linkedList = new LinkedList<>();

   private LinkedList<TaskScheduler.Entry2> getLinkedList() {
      return this.linkedList;
   }

   public TaskScheduler() {
      this(true);
   }

   public TaskScheduler(boolean var1) {
      if (var1) {
         EventManager.register(this);
         new TaskScheduler.Entry().start();
      }
   }

   public void method5102(Object var1) {
      if (var1 != null) {
         var var2 = this.getLinkedList();
         TaskScheduler.Entry2 var3;
         synchronized (var2) {
            var3 = (TaskScheduler.Entry2)var2.peek();
         }

         if (var3 != null) {
            boolean var4 = true;

            try {
               var4 = var3.method5124(var1);
            } catch (Throwable var8) {
               var4 = false;
            }

            if (var4) {
               synchronized (var2) {
                  if (var2.peek() == var3) {
                     var2.poll();
                  } else {
                     var2.removeIf(var1x -> var1x == var3);
                  }
               }
            }
         }
      }
   }

   @EventTarget
   public void method5103(Event1 var1) {
      this.method5102(var1);
   }

   @EventTarget
   public void method5104(Event5 var1) {
      this.method5102(var1);
   }

   @EventTarget(4)
   public void method5105(MoveInputEvent var1) {
      this.method5102(var1);
   }

   @EventTarget(0)
   public void method5106(UpdateEvent var1) {
      this.method5102(var1);
   }

   @EventTarget(3)
   public void method5107(HandleInputEvent var1) {
      this.method5102(var1);
   }

   @EventTarget(0)
   public void method5108(HandleInputPostEvent var1) {
      this.method5102(var1);
   }

   public TaskScheduler.Entry2 method5109(Class<?> var1) {
      LinkedList<TaskScheduler.Entry2> var2 = this.getLinkedList();
      synchronized (var2) {
         for (Object var5_o : (Iterable<?>)(Object)(var2)) {
            TaskScheduler.Entry2 var5 = (TaskScheduler.Entry2)var5_o;
            if (var5.getClassValue() == var1) {
               return var5;
            }
         }

         return new TaskScheduler.Entry2(var1);
      }
   }

   public void method5110(TaskScheduler.Entry2 var1) {
      this.method5111(var1, 0);
   }

   public void method5111(TaskScheduler.Entry2 var1, int var2) {
      if (var1 != null) {
         var var3 = this.getLinkedList();
         synchronized (var3) {
            boolean var5 = var3.stream().anyMatch(var1x -> var1x == var1);
            if (!var5) {
               var1.setIntValue3(var2);
               if (var2 > this.method5112()) {
                  if (!var3.isEmpty()) {
                     var3.add(1, var1);
                  } else {
                     var3.addFirst(var1);
                  }
               } else {
                  var3.add(var1);
               }
            }
         }
      }
   }

   private int method5112() {
      LinkedList<TaskScheduler.Entry2> var1 = this.getLinkedList();
      int var2 = Integer.MIN_VALUE;

      for (Object var4_o : (Iterable<?>)(Object)(var1)) {
         TaskScheduler.Entry2 var4 = (TaskScheduler.Entry2)var4_o;
         if (var4.getIntValue3() > var2) {
            var2 = var4.getIntValue3();
         }
      }

      return var2 == Integer.MIN_VALUE ? 0 : var2;
   }

   public void method5113() {
      var var1 = this.getLinkedList();
      synchronized (var1) {
         var1.clear();
         logger.warning("All tasks cleared manually. tasks.size=0");
      }
   }

   public void method5114(Class<?> var1) {
      var var2 = this.getLinkedList();
      synchronized (var2) {
         var2.removeIf(var1x -> var1x.getClassValue() == var1);
         logger.info("Tasks cleared for owner: " + var1.getSimpleName());
      }
   }

   public int method5115() {
      var var1 = this.getLinkedList();
      synchronized (var1) {
         return var1.size();
      }
   }

   public boolean method5116() {
      var var1 = this.getLinkedList();
      synchronized (var1) {
         boolean var3 = var1.isEmpty();
         logger.finest(() -> "isFinished() -> " + var3);
         return var3;
      }
   }

   public boolean method5117(Class<?> var1) {
      var var2 = this.getLinkedList();
      synchronized (var2) {
         return var2.stream().filter(var1x -> var1x.getClassValue() == var1).allMatch(TaskScheduler.Entry2::method5125);
      }
   }

   public boolean method5118(Class<?> var1) {
      var var2 = this.getLinkedList();
      synchronized (var2) {
         TaskScheduler.Entry2 var4 = (TaskScheduler.Entry2)var2.peek();
         if (var4 == null) {
            return false;
         } else {
            Class var5 = var4.method5127();
            return var5 == var1;
         }
      }
   }

   static {
      logger.setLevel(Level.INFO);
   }

   public static class Entry {
      private Thread thread;
      private volatile boolean running;

      public void start() {
         if (!this.running) {
            this.running = true;
            this.thread = new Thread(() -> {
               while (this.running) {
                  try {
                     try {
                        if (MinecraftClient.getInstance().player != null) {
                           MinecraftClient.getInstance().execute(() -> EventManager.call(new Event1()));
                        }
                     } catch (Exception var2) {
                        var2.printStackTrace();
                     }

                     int var1 = ThreadLocalRandom.current().nextInt(40, 60);
                     Thread.sleep(var1);
                  } catch (InterruptedException var3) {
                  }
               }
            }, "RandomEventCaller");
            this.thread.setDaemon(true);
            this.thread.start();
         }
      }

      public void stop() {
         this.running = false;
         if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
         }
      }
   }

   public static class Entry2 {
      private static final AtomicLong atomicLong = new AtomicLong(1L);
      private final long longValue = atomicLong.getAndIncrement();
      private final Class<?> classValue;
      private final Queue<TaskScheduler.Entry2.Entry<?>> queue = new LinkedList<>();
      private final LinkedList<TaskScheduler.Entry2.Entry<?>> linkedList = new LinkedList<>();
      private int intValue = 0;
      private int intValue2 = Integer.MAX_VALUE;
      private int intValue3 = 0;

      public Entry2() {
         this.classValue = TaskScheduler.class;
      }

      public Entry2(Class<?> var1) {
         this.classValue = var1;
      }

      public TaskScheduler.Entry2 method5119(int var1) {
         this.intValue2 = Math.max(1, var1);
         TaskScheduler.logger.finest(this.method5126() + " set maxIdleTicks=" + this.intValue2);
         return this;
      }

      public <E> TaskScheduler.Entry2 method5120(Class<E> var1, TaskScheduler.Entry2.Handler<E> var2) {
         return this.method5121(var1, var2, 0);
      }

      public <E> TaskScheduler.Entry2 method5121(Class<E> var1, TaskScheduler.Entry2.Handler<E> var2, int var3) {
         TaskScheduler.Entry2.Entry var4 = new TaskScheduler.Entry2.Entry(var1, var2, this.queue.size() + 1, false, var3);
         if (var3 > this.method5122()) {
            LinkedList var5 = (LinkedList)this.queue;
            var5.addFirst(var4);
         } else {
            this.queue.add(var4);
         }

         TaskScheduler.logger.finest(this.method5126() + " scheduled step#" + var4.intValue + " for event=" + var1.getSimpleName());
         return this;
      }

      private int method5122() {
         int var1 = Integer.MIN_VALUE;

         for (TaskScheduler.Entry2.Entry var3 : this.queue) {
            if (var3.intValue2 > var1) {
               var1 = var3.intValue2;
            }
         }

         return var1 == Integer.MIN_VALUE ? 0 : var1;
      }

      public <E> TaskScheduler.Entry2 method5123(Class<E> var1, TaskScheduler.Entry2.Handler<E> var2) {
         TaskScheduler.Entry2.Entry var3 = new TaskScheduler.Entry2.Entry(var1, var2, this.linkedList.size() + 1, true, 0);
         this.linkedList.add(var3);
         TaskScheduler.logger.finest(this.method5126() + " scheduled PERSISTENT step#" + var3.intValue + " for event=" + var1.getSimpleName());
         return this;
      }

      public void cancel() {
         this.queue.clear();
         this.linkedList.clear();
      }

      public boolean method5124(Object var1) {
         if (var1 == null) {
            return false;
         } else {
            TaskScheduler.Entry2.Entry var2 = this.queue.peek();
            if (var2 == null) {
               TaskScheduler.logger.info(this.method5126() + "task COMPLETE.");
               return true;
            } else {
               for (TaskScheduler.Entry2.Entry var4 : this.linkedList) {
                  if (var4.classValue.isInstance(var1)) {
                     try {
                        var4.method5128(var1);
                     } catch (Throwable var7) {
                        var7.printStackTrace();
                     }
                  }
               }

               boolean var8 = false;
               if (var2.classValue.isInstance(var1)) {
                  boolean var9;
                  try {
                     var9 = var2.method5128(var1);
                  } catch (Throwable var6) {
                     var6.printStackTrace();
                     var9 = true;
                  }

                  if (var9) {
                     this.queue.poll();
                     var8 = true;
                     TaskScheduler.logger.info(this.method5126() + " step#" + var2.intValue + " DONE size=" + this.queue.size());
                  } else {
                     TaskScheduler.logger.finest(() -> this.method5126() + " step#" + var2.intValue + " NOT done");
                  }
               }

               if (var8) {
                  this.intValue = 0;
                  boolean var10 = this.queue.isEmpty();
                  if (var10) {
                     TaskScheduler.logger.info(this.method5126() + "TASK COMPLETE.");
                  }

                  return var10;
               } else {
                  if (this.intValue2 != Integer.MAX_VALUE) {
                     this.intValue++;
                  }

                  if (this.intValue > this.intValue2) {
                     this.queue.clear();
                     this.linkedList.clear();
                     return true;
                  } else {
                     return false;
                  }
               }
            }
         }
      }

      public boolean method5125() {
         boolean var1 = this.queue.isEmpty();
         TaskScheduler.logger.finest(() -> this.method5126() + " isCompleted() -> " + var1);
         return var1;
      }

      private String method5126() {
         return "[Task#" + this.longValue + "@" + (this.classValue != null ? this.classValue.getSimpleName() : "Unknown") + "]";
      }

      @Override
      public String toString() {
         return "ScriptTask{id="
            + this.longValue
            + ", owner="
            + (this.classValue != null ? this.classValue.getSimpleName() : "null")
            + ", stepsLeft="
            + this.queue.size()
            + ", persistent="
            + this.linkedList.size()
            + ", idleTicks="
            + this.intValue
            + "/"
            + this.intValue2
            + ", priority="
            + this.intValue3
            + "}";
      }

      public Class<?> method5127() {
         TaskScheduler.Entry2.Entry var1 = this.queue.peek();
         return var1 == null ? null : var1.classValue;
      }

      @Generated
      public long getLongValue() {
         return this.longValue;
      }

      @Generated
      public Class<?> getClassValue() {
         return this.classValue;
      }

      @Generated
      public int getIntValue3() {
         return this.intValue3;
      }

      @Generated
      public void setIntValue3(int var1) {
         this.intValue3 = var1;
      }

      private static class Entry<E> {
         private final Class<E> classValue;
         private final TaskScheduler.Entry2.Handler<E> handler;
         private final int intValue;
         private final boolean flag;
         private final int intValue2;

         public Entry(Class<E> var1, TaskScheduler.Entry2.Handler<E> var2, int var3, boolean var4, int var5) {
            this.classValue = var1;
            this.handler = var2;
            this.intValue = var3;
            this.flag = var4;
            this.intValue2 = var5;
         }

         public boolean method5128(Object var1) {
            return this.handler.accept(this.classValue.cast(var1));
         }

         @Override
         public String toString() {
            return "Step{index="
               + this.intValue
               + ", event="
               + this.classValue.getSimpleName()
               + ", persistent="
               + this.flag
               + ", priority="
               + this.intValue2
               + "}";
         }
      }

      @FunctionalInterface
      public interface Handler<E> {
         boolean accept(E var1);
      }
   }
}

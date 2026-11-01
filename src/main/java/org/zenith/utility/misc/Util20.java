package org.zenith.utility.misc;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;

public final class Util20 {
   public static long longValue;
   public static int intValue;
   private static Thread thread;

   private Util20() {
   }

   public static void method5134() {
      longValue++;
   }

   public static void method5135() {
      intValue++;
   }

   public static void method5136() {
      thread = Thread.currentThread();
   }

   public static void method5137() {
      thread = null;
   }

   public static boolean method5138() {
      Thread var0 = thread;
      return var0 != null && var0 == Thread.currentThread();
   }

   public static Util20.Entry method5139(BlockEntity var0) {
      return ((Util20.Handler)var0).zenith$optData();
   }

   public static final class Entry {
      public long longValue = Long.MIN_VALUE;
      public BlockState blockState;
      public Object source;
      public long longValue2 = Long.MIN_VALUE;
      public int intValue;
      public int intValue2;
      public long longValue3 = Long.MIN_VALUE;
      public int intValue3;
      public BlockState blockState2;
      public boolean flag;
      public long longValue4 = Long.MIN_VALUE;
      public Entity entity;
      public EntityRenderState entityRenderState;
   }

   public interface Handler {
      Util20.Entry zenith$optData();
   }
}

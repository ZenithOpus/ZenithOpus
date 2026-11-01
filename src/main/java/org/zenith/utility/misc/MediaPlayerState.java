package org.zenith.utility.misc;

import dev.redstones.mediaplayerinfo.IMediaSession;
import org.zenith.utility.config.MediaHelperProcess;

public final class MediaPlayerState {
   private static final MediaPlayerState INSTANCE = new MediaPlayerState();
   private static final long CONST_longValue = 650L;
   private String owner = "";
   private float floatValue = -1.0F;
   private float floatValue2 = -1.0F;
   private long changedAt;
   private boolean flag;
   private float floatValue3 = 1.0F;
   private float floatValue4 = -1.0F;
   private long longValue;
   private volatile float floatValue5;
   private volatile long longValue2;

   private MediaPlayerState() {
   }

   public static MediaPlayerState getInstance() {
      return INSTANCE;
   }

   public void method1669(String var1) {
      String var2 = var1 == null ? "" : var1;
      if (!var2.equals(this.owner)) {
         this.owner = var2;
         this.floatValue = -1.0F;
         this.flag = false;
         this.floatValue4 = -1.0F;
         MediaHelperProcess.getInstance().method6863(this.owner);
         MediaHelperProcess.getInstance().method6868(this.owner);
         MediaHelperProcess.getInstance().method6867(this.owner);
      }
   }

   public void method1670(float var1) {
      if (!(var1 < 0.0F)) {
         if (!this.flag) {
            this.floatValue = var1;
         }
      }
   }

   public void method1671(float var1) {
      if (var1 >= 0.0F) {
         this.floatValue5 = var1;
         this.longValue2 = System.currentTimeMillis();
      }
   }

   public float method1672() {
      if (System.currentTimeMillis() - this.longValue2 > 600L) {
         return -1.0F;
      } else {
         float var1 = this.flag && this.floatValue4 > 0.01F ? this.floatValue5 / Math.max(0.05F, this.floatValue3) : this.floatValue5;
         return Math.min(1.0F, var1);
      }
   }

   public boolean method1673() {
      return this.floatValue >= 0.0F;
   }

   public float method1674() {
      return this.flag && this.floatValue4 >= 0.0F ? this.floatValue4 : this.floatValue;
   }

   public long getChangedAt() {
      return this.changedAt;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void method1675(float var1) {
      IMediaSession var2 = MediaSessionManager.getInstance().getIMediaSession();
      if (var2 != null) {
         float var3 = Math.max(0.0F, Math.min(1.0F, var1));
         this.changedAt = System.currentTimeMillis();
         if (this.flag) {
            this.floatValue4 = var3;
         } else {
            this.floatValue = var3;
            MediaHelperProcess.getInstance().method6861(var2.getOwner(), var3);
         }
      }
   }

   public void method1676(float var1) {
      if (!this.method1673()) {
         IMediaSession var2 = MediaSessionManager.getInstance().getIMediaSession();
         if (var2 != null) {
            MediaHelperProcess.getInstance().method6863(var2.getOwner());
         }

         this.floatValue = 0.5F;
      }

      this.method1675(this.method1674() + var1);
   }

   public void method1677(boolean var1, float var2, boolean var3) {
      IMediaSession var4 = MediaSessionManager.getInstance().getIMediaSession();
      long var5 = System.currentTimeMillis();
      if (var3 && var4 != null) {
         if (var1) {
            this.longValue = var5;
            if (!this.flag && this.method1673()) {
               this.floatValue4 = this.floatValue;
               this.floatValue3 = var2;
               this.flag = true;
               MediaHelperProcess.getInstance().method6862(var4.getOwner(), Math.max(0.0F, this.floatValue4 * var2), 260);
            }
         } else {
            if (this.flag && var5 - this.longValue > 650L) {
               this.method1678(var4);
            }
         }
      } else {
         if (this.flag) {
            this.method1678(var4);
         }
      }
   }

   private void method1678(IMediaSession var1) {
      this.flag = false;
      if (this.floatValue4 >= 0.0F) {
         this.floatValue = this.floatValue4;
         if (var1 != null) {
            MediaHelperProcess.getInstance().method6862(var1.getOwner(), this.floatValue4, 900);
         }
      }

      this.floatValue4 = -1.0F;
   }

   public float method1679() {
      float var1 = this.method1674();
      if (this.floatValue2 < 0.0F) {
         this.floatValue2 = var1;
      }

      this.floatValue2 = this.floatValue2 + (var1 - this.floatValue2) * 0.25F;
      return this.floatValue2;
   }
}

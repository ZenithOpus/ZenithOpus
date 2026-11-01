package org.zenith.utility.misc;

import de.maxhenkel.rnnoise4j.Denoiser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VoiceDenoiser {
   private static final Logger logger = LoggerFactory.getLogger("Zenith/Voice");
   private static volatile boolean broken;
   private Denoiser denoiser;
   private short[] shortValues = new short[0];
   private float floatValue;

   public static boolean method801() {
      return !broken;
   }

   public float getFloatValue() {
      return this.floatValue;
   }

   public boolean method802(short[] var1, int var2) {
      if (broken) {
         return false;
      } else {
         try {
            if (this.denoiser == null) {
               this.denoiser = new Denoiser();
               this.shortValues = new short[this.denoiser.getFrameSize()];
            }

            int var3 = this.shortValues.length;
            float var4 = 0.0F;

            for (int var5 = 0; var5 + var3 <= var2; var5 += var3) {
               System.arraycopy(var1, var5, this.shortValues, 0, var3);
               var4 = Math.max(var4, this.denoiser.denoiseInPlace(this.shortValues));
               System.arraycopy(this.shortValues, 0, var1, var5, var3);
            }

            this.floatValue = var4;
            return true;
         } catch (Throwable var6) {
            broken = true;
            logger.warn("Голос: RNNoise недоступен, остаётся простой шумодав: {}", var6.toString());
            this.close();
            return false;
         }
      }
   }

   public void close() {
      if (this.denoiser != null) {
         try {
            this.denoiser.close();
         } catch (Throwable var2) {
         }

         this.denoiser = null;
      }
   }
}

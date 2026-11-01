package org.zenith.utility.misc;

import org.zenith.utility.api.AimPolicy;

public final class Class36 implements Resettable {
   private final AimPolicyRunner aimPolicyRunner;
   private final int[][] intValues;
   private int intValue;
   private int cursor;

   public Class36(AimPolicy var1) {
      this.aimPolicyRunner = new AimPolicyRunner(var1);
      this.intValues = new int[var1.intValue][2];
   }

   @Override
   public int[] method2865(float[] var1) {
      if (this.cursor >= this.intValue) {
         int[][] var2 = this.aimPolicyRunner.method6749(var1);

         for (int var3 = 0; var3 < this.intValues.length; var3++) {
            this.intValues[var3][0] = var2[var3][0];
            this.intValues[var3][1] = var2[var3][1];
         }

         this.intValue = this.intValues.length;
         this.cursor = 0;
      }

      int[] var4 = this.intValues[this.cursor++];
      return new int[]{var4[0], var4[1]};
   }

   @Override
   public void reset() {
      this.aimPolicyRunner.reset();
      this.intValue = 0;
      this.cursor = 0;
   }

   @Override
   public String method2866() {
      return "policy";
   }
}
